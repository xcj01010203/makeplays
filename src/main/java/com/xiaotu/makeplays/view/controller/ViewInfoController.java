package com.xiaotu.makeplays.view.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.CollationKey;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.reflect.TypeToken;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.model.constants.CrewType;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.goods.model.GoodsInfoModel;
import com.xiaotu.makeplays.goods.model.constants.GoodsType;
import com.xiaotu.makeplays.goods.service.GoodsInfoService;
import com.xiaotu.makeplays.locationsearch.model.SceneViewInfoModel;
import com.xiaotu.makeplays.locationsearch.service.SceneViewInfoService;
import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;
import com.xiaotu.makeplays.roleactor.model.constants.ViewRoleType;
import com.xiaotu.makeplays.roleactor.service.ViewRoleService;
import com.xiaotu.makeplays.scenario.controller.dto.ScenarioViewDto;
import com.xiaotu.makeplays.scenario.model.BookMarkModel;
import com.xiaotu.makeplays.scenario.model.ScenarioFormatModel;
import com.xiaotu.makeplays.scenario.service.BookMarkService;
import com.xiaotu.makeplays.scenario.service.ScenarioFormatService;
import com.xiaotu.makeplays.scenario.service.ScenarioService;
import com.xiaotu.makeplays.shoot.model.ShootGroupModel;
import com.xiaotu.makeplays.shoot.service.ShootGroupService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.ExcelUtils;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.GsonUtils;
import com.xiaotu.makeplays.utils.MD5Util;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.RegexUtils;
import com.xiaotu.makeplays.utils.SepratorConstant;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.view.controller.dto.BatchUpdateViewDto;
import com.xiaotu.makeplays.view.controller.dto.SameViewLocationDto;
import com.xiaotu.makeplays.view.controller.dto.SeriesNoDto;
import com.xiaotu.makeplays.view.controller.dto.StandardViewInfoDto;
import com.xiaotu.makeplays.view.controller.dto.ViewFilterDto;
import com.xiaotu.makeplays.view.controller.dto.ViewInfoDto;
import com.xiaotu.makeplays.view.controller.dto.ViewNoDto;
import com.xiaotu.makeplays.view.controller.filter.ViewFilter;
import com.xiaotu.makeplays.view.model.AtmosphereInfoModel;
import com.xiaotu.makeplays.view.model.ViewContentModel;
import com.xiaotu.makeplays.view.model.ViewInfoModel;
import com.xiaotu.makeplays.view.model.ViewLocationMapModel;
import com.xiaotu.makeplays.view.model.ViewLocationModel;
import com.xiaotu.makeplays.view.model.ViewRoleAndActorModel;
import com.xiaotu.makeplays.view.model.ViewTempModel;
import com.xiaotu.makeplays.view.model.constants.BookmarkType;
import com.xiaotu.makeplays.view.model.constants.LocationType;
import com.xiaotu.makeplays.view.model.constants.SeasonType;
import com.xiaotu.makeplays.view.model.constants.ShootStatus;
import com.xiaotu.makeplays.view.model.constants.ViewContentStatus;
import com.xiaotu.makeplays.view.model.constants.ViewType;
import com.xiaotu.makeplays.view.service.AtmosphereService;
import com.xiaotu.makeplays.view.service.HistoryViewContentService;
import com.xiaotu.makeplays.view.service.InsideAdvertService;
import com.xiaotu.makeplays.view.service.ViewContentService;
import com.xiaotu.makeplays.view.service.ViewInfoService;
import com.xiaotu.makeplays.view.service.ViewLocationService;
import com.xiaotu.makeplays.view.service.ViewTempService;

/**
 * 场景信息
 * 
 * @author xuchangjian
 */
@Controller
@RequestMapping("/viewManager")
public class ViewInfoController extends BaseController {
	Logger logger = LoggerFactory.getLogger(ViewInfoController.class);
	
	private final static SimpleDateFormat yyyyMMddFormate = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private final Integer terminal = Constants.TERMINAL_PC;
	
	//EXCEL表格中的数据，key为行号，value为行值集合
	private Map<Integer, Map<Integer, String>> excelData = new LinkedHashMap<Integer, Map<Integer, String>>();
	
	//主要演员数据,key为列号，value为列值
	private Map<Integer, String> mainRoleCellNameMap = new LinkedHashMap<Integer, String>();

	private String lineSeparator = "\r\n";
	
	@Autowired
	private ViewTempService viewTempService;

	@Autowired
	private ScenarioService scenarioService;
	
	@Autowired
	private ViewInfoService viewInfoService;

	@Autowired
	private AtmosphereService atmosphereService;

	@Autowired
	private ViewLocationService viewLocationService;

	@Autowired
	private ViewContentService viewContentService;

	@Autowired
	private ViewRoleService viewRoleService;

	@Autowired
	private ShootGroupService shootGroupService;

	@Autowired
	private SceneViewInfoService sceneViewInfoService;
	@Autowired
	private InsideAdvertService insideAdvertService;

	@Autowired
	private BookMarkService bookMarkService;

	@Autowired
	private HistoryViewContentService historyViewContentService;

	@Autowired
	private CrewInfoService crewInfoService;
	
	@Autowired
	private GoodsInfoService goodsInfoService;
	
	@Autowired
	private ScenarioFormatService scenarioFormatService;
	
	/**
	 * 获取剧组类型和剧组的id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getCrewType")
	public Map<String, Object> getCrewType(HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			String crewId = crewInfo.getCrewId();
			Integer crewType = crewInfo.getCrewType();
			String crewName = crewInfo.getCrewName();
			
			resultMap.put("crewType", crewType);
			resultMap.put("crewId", crewId);
			resultMap.put("crewName", crewName);
			resultMap.put("seriesLgth", crewInfo.getLengthPerSet());
			resultMap.put("cutRadio", crewInfo.getCutRate());
			message = "获取成功!";
		} catch (Exception e) {
			message = "未知错误,查询失败!";
			success = false;
			
			logger.error(message, e);
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	/**
	 * 根据集-场编号批量执行替换操作
	 * 
	 * @param seriesViewNoStr 多个集场编号的字符串(以逗号进行分割)
	 * @return
	 */
	@RequestMapping("/replaceViewBatch")
	public @ResponseBody Map<String, Object> replaceViewBatch(HttpServletRequest request, String seriesViewNoStr) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);
		String message = "";
		boolean success = true;

		try {
			String crewId = this.getCrewId(request);
			//定义需要操作的剧本中场景信息dto的list集合
			List<ScenarioViewDto> sceViewDtoList = new ArrayList<ScenarioViewDto>();
			
			//对前台传递的参数进行校验
			if (StringUtils.isBlank(seriesViewNoStr)) {
				throw new IllegalArgumentException("请选择需要替换的集-场编号!");
			}
			
			String[] seriesViewsArr = seriesViewNoStr.split(SepratorConstant.SEP_COMMA_EN);
			// 根据集次-场次-剧组ID查找场景临时表中的数据
			for (int i = 0; i < seriesViewsArr.length; i++) {
				String seriesView = seriesViewsArr[i];
				String[] seriesViewArr = seriesView.split(SepratorConstant.SEP_CROSS_EN);
				ViewTempModel viewTemp = this.viewTempService.queryOneBySeriesViewCrewId(Integer.parseInt(seriesViewArr[0]), seriesViewArr[1], crewId);
				ScenarioViewDto scenarioViewDto = this.genScenarioDtoByViewTemp(viewTemp);
				if (scenarioViewDto != null) {
					sceViewDtoList.add(scenarioViewDto);
				}
				
				//从零时场景表中移除当前场
				viewTempService.deleteManyByCrewId(crewId, Integer.parseInt(seriesViewArr[0]), seriesViewArr[1]);
			}

			if (sceViewDtoList != null && sceViewDtoList.size() > 0) {
				this.viewInfoService.updateManyBySceDto(sceViewDtoList, false, crewId, userInfo);
			}
			
			message = "执行替换操作成功";
//			this.sysLogService.saveSysLog(request, "根据集-场编号批量执行替换操作", terminal, ViewInfoModel.TABLE_NAME, seriesViewNoStr, 2);
		}catch (IllegalArgumentException ie){
			success = false;
			message = ie.getMessage();
			
			logger.error(message, ie);
		}catch (Exception e) {
			message = "未知异常,执行替换操作失败!";
			success = false;

			logger.error(message, e);
		}

		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}

	/**
	 * 根据集-场编号批量执行替换剧本内容操作
	 * 
	 * @param seriesViewNoStr 多个集场编号的字符串(以逗号进行分割)
	 * @return
	 */
	@RequestMapping("/replaceSecBatch")
	public @ResponseBody Map<String, Object> replaceSecBatch(HttpServletRequest request, String seriesViewNoStr) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			List<ViewContentModel> viewContentList = new ArrayList<ViewContentModel>();
			
			//对前台传递的参数进行校验
			if (StringUtils.isBlank(seriesViewNoStr)) {
				throw new IllegalArgumentException("请选择需要替换的集-场编号!");
			}
			
			String[] seriesViewsArr = seriesViewNoStr.split(SepratorConstant.SEP_COMMA_EN);
			// 根据集次-场次-剧组ID查找场景临时表中的数据
			for (int i = 0; i < seriesViewsArr.length; i++) {
				String seriesView = seriesViewsArr[i];
				String[] seriesViewArr = seriesView.split(SepratorConstant.SEP_CROSS_EN);
				ViewTempModel viewTemp = this.viewTempService.queryOneBySeriesViewCrewId(Integer.parseInt(seriesViewArr[0]), seriesViewArr[1], crewId);

				int seriesNo = viewTemp.getSeriesNo();
				String viewNo = viewTemp.getViewNo();
				String title = viewTemp.getTitle();
				String content = viewTemp.getContent();

				ViewContentModel viewContent = this.viewContentService.queryBySeriesViewNo(crewId, seriesNo, viewNo);
				boolean isChanged = this.viewInfoService.isViewContentChange(viewContent, content, title);
				if (isChanged && viewContent.getStatus() == ViewContentStatus.Published.getValue()) {
					viewContent.setStatus(ViewContentStatus.UpdateNotPublished.getValue());
				}
				if (!StringUtils.isBlank(content)) {
					viewContent.setFigureprint(MD5Util.MD5(content));
				}
				viewContent.setTitle(title);
				viewContent.setContent(content);
				viewContent.setReadedPeopleIds(null);
				viewContentList.add(viewContent);
				
				//从零时场景表中移除当前场
				viewTempService.deleteManyByCrewId(crewId, Integer.parseInt(seriesViewArr[0]), seriesViewArr[1]);
			}
			
			this.viewContentService.updateMany(viewContentList);
			message = "执行替换操作成功";
//			this.sysLogService.saveSysLog(request, "根据集-场编号批量执行替换操作", terminal, ViewInfoModel.TABLE_NAME, seriesViewNoStr, 2);
			
		}catch (IllegalArgumentException ie){
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		}catch (Exception e) {
			message = "未知异常,执行替换操作失败!";
			success = false;

			logger.error(message, e);
		}

		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}

	/**
	 * 根据集-场编号批量执行不保留操作
	 * 
	 * @param seiresViewNoStr 集场次编号的字符串,集场次编号之间用","进行分割;多个集场次编号用","进行分割
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/notKeepViewBatch")
	public Map<String, Object> notKeepViewBatch(HttpServletRequest request, String seiresViewNoStr) {
		Map<String, Object> resuleMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = getCrewId(request);
			List<ViewInfoModel> viewInfoList = new ArrayList<ViewInfoModel>();
			
			if (StringUtils.isBlank(seiresViewNoStr)) {
				throw new IllegalArgumentException("请选择要删除的集场次编号!");
			}
			
			if (!StringUtils.isBlank(seiresViewNoStr)) {
				String[] seriesViewsArr = seiresViewNoStr.split(SepratorConstant.SEP_COMMA_EN);
				
				//取出页面传递过来的每一个集场号对应的场景信息
				for (int i = 0; i < seriesViewsArr.length; i++) {
					String seriesView = seriesViewsArr[i];
					String[] seriesViewArr = seriesView.split(SepratorConstant.SEP_CROSS_EN);

					int seriesNo = 0;
					String viewNo = "";
					if (seriesViewArr.length > 0) {
						seriesNo = Integer.parseInt(seriesViewArr[0]);
					}
					if (seriesViewArr.length > 1) {
						viewNo = seriesViewArr[1];
					}
					
					//根据剧本ID,集次，场次查询对应的场景信息
					ViewInfoModel viewInfo = this.viewInfoService.queryOneByCrewIdAndSeriaViewNo(crewId, seriesNo,viewNo);
					
					if (viewInfo != null) {
						viewInfo.setShootStatus(ShootStatus.DeleteXi.getValue());
						viewInfoList.add(viewInfo);
					}
				}
			}

			if (viewInfoList != null && viewInfoList.size() > 0) {
				this.viewInfoService.updateManyViewInfo(viewInfoList);
			}

			message = "执行不保留操作成功";
//			this.sysLogService.saveSysLog(request, "根据集-场编号批量执行不保留操作", terminal, ViewInfoModel.TABLE_NAME, seiresViewNoStr, 2);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，执行替换操作失败。";
			success = false;
			
			logger.error(message, e);
		}

		resuleMap.put("message", message);
		resuleMap.put("success", success);
		return resuleMap;
	}

	/**
	 * 用户在操作选择“跳过”或“替换”的数据和操作选择“保留”或“不保留”的数据的页面点击取消按钮时，系统默认执行“跳过”和“保留”操作
	 * 
	 * @return
	 */
	@RequestMapping("/cancelOperate")
	@ResponseBody
	public Map<String, Object> cancelOperate(HttpServletRequest request, String seriesViewNoStr) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		String message = "";
		boolean success = true;
		final String crewId = getCrewId(request);
		
		
		try {
			
			String[] seriesViewNOArr = null;
			if (StringUtils.isNotBlank(seriesViewNoStr)) {
				seriesViewNOArr = seriesViewNoStr.split(",");
			}
			
			for (String seriesViewNo : seriesViewNOArr) {
				String[] seriesViewNoArr = null;
				if (seriesViewNo.contains("-")) {
					seriesViewNoArr = seriesViewNo.split("-");
				}else {
					seriesViewNoArr = new String[] {"0",seriesViewNo};
				}
				viewTempService.deleteManyByCrewId(crewId, Integer.parseInt(seriesViewNoArr[0]), seriesViewNoArr[1]);
			}
			
			message = "操作成功!";
		} catch (Exception e) {
			success = false;
			message = "出现未知异常，操作失败";
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}

	/**
	 * 根据场景表临时信息对象生成剧本场景信息
	 * 
	 * @param viewTemp 场景零时信息对象
	 * @return
	 */
	private ScenarioViewDto genScenarioDtoByViewTemp(ViewTempModel viewTemp) {
		//定义剧本中场景信息的dto
		ScenarioViewDto scenarioViewDto = null;
		if (viewTemp != null) {
			scenarioViewDto = new ScenarioViewDto();
			scenarioViewDto.setSeriesNo(viewTemp.getSeriesNo());
			scenarioViewDto.setViewNo(viewTemp.getViewNo());
			scenarioViewDto.setAtmosphere(viewTemp.getAtmosphere());
			scenarioViewDto.setSite(viewTemp.getSite());
			scenarioViewDto.setTitle(viewTemp.getTitle());
			scenarioViewDto.setContent(viewTemp.getContent());
			scenarioViewDto.setPageCount(viewTemp.getPageCount());
			scenarioViewDto.setViewType(viewTemp.getViewType());
			scenarioViewDto.setShootTime(viewTemp.getShootTime());
			scenarioViewDto.setCommercialImplants(viewTemp.getCommercialImplants());
			//取出角色列表中的所有演员的姓名
			String roleNames = viewTemp.getRoleNames();
			List<String> roleNameList = new ArrayList<String>();
			if (!StringUtils.isBlank(roleNames)) {
				//根据正则规则将姓名字符串分割为单个姓名的数组
				String[] roleNameArr = RegexUtils.regexSplitStr(Constants.REGEX_TITLE_FIGURE_SPLIT_CHAR, roleNames);
				//将姓名数组转换成角色姓名集合
				roleNameList = Arrays.asList(roleNameArr);
			}

			// 特约演员姓名
			String guestNames = viewTemp.getGuestNames();
			List<String> guestNameList = new ArrayList<String>();
			if (!StringUtils.isBlank(guestNames)) {
				String[] guestNameArr = RegexUtils.regexSplitStr(Constants.REGEX_TITLE_FIGURE_SPLIT_CHAR, guestNames);
				guestNameList = Arrays.asList(guestNameArr);
			}

			// 群众演员姓名
			String massNames = viewTemp.getMassNames();
			List<String> massNameList = new ArrayList<String>();
			if (!StringUtils.isBlank(massNames)) {
				String[] massNameArr = RegexUtils.regexSplitStr(Constants.REGEX_TITLE_FIGURE_SPLIT_CHAR, massNames);
				massNameList = Arrays.asList(massNameArr);
			}

			// 道具
			String propsNames = viewTemp.getPropsNames();

			scenarioViewDto.setMajorRoleNameList(roleNameList);
			scenarioViewDto.setGuestRoleNameList(guestNameList);
			scenarioViewDto.setMassRoleNameList(massNameList);
			scenarioViewDto.setProps(propsNames);
			scenarioViewDto.setClothes(viewTemp.getClothesNames());
			scenarioViewDto.setMakeups(viewTemp.getMakeupNames());
			scenarioViewDto.setFirstLocation(viewTemp.getFirstLocation());
			scenarioViewDto.setSecondLocation(viewTemp.getSecondLocation());
			scenarioViewDto.setThirdLocation(viewTemp.getThirdLocation());
			scenarioViewDto.setRemark(viewTemp.getRemark());
			scenarioViewDto.setSeason(viewTemp.getSeason());
			scenarioViewDto.setShootLocation(viewTemp.getShootLocation());
			scenarioViewDto.setMainContent(viewTemp.getMainContent());
			scenarioViewDto.setSpecialProps(viewTemp.getSpecialProps());
		}

		return scenarioViewDto;
	}

	/**
	 * 跳转到新老剧本比对页面
	 * 
	 * @param request
	 * @param seriesViewNo
	 *            集次-场次编号
	 * @return
	 */
	@RequestMapping("/toScenarioComparePage")
	public ModelAndView toScenarioComparePage(HttpServletRequest request, String seriesViewNo,boolean isViewList) {
		ModelAndView mv = new ModelAndView("/scenario/viewCompare");
		if (!StringUtils.isBlank(seriesViewNo)) {
			
		mv.addObject("seriesViewNo", seriesViewNo);
		mv.addObject("isViewList", isViewList);
		}
		
		return mv;
	}

	
	/**
	 * 初始化新老剧本对比页面时需要加载的数据
	 * 
	 * @param seriesViewNo
	 *            集次-场次编号
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryScenarioCompareInfo")
	public Map<String, Object> queryScenarioCompareInfo(HttpServletRequest request, String seriesViewNo){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			String crewId = this.getCrewId(request);
			Integer crewType = crewInfo.getCrewType();
			
			if (StringUtils.isBlank(seriesViewNo)) {
				throw new IllegalArgumentException("请选择要比对的集场次编号!");
			}

			// 查询老剧本数据
			String[] seriesViewArr = seriesViewNo.split(SepratorConstant.SEP_CROSS_EN);
			int seriesNo = Integer.parseInt(seriesViewArr[0]);
			String viewNo = seriesViewArr[1];

			ViewInfoModel viewInfo = this.viewInfoService.queryOneByCrewIdAndSeriaViewNo(crewId, seriesNo, viewNo);
			if (viewInfo != null) {
				ViewInfoDto viewInfoDto = this.genViewInfoDtoByViewInfo(viewInfo);
				resultMap.put("oldViewData", viewInfoDto);
			}

			// 查询新版本数据
			ViewTempModel viewTemp = this.viewTempService.queryOneBySeriesViewCrewId(seriesNo, viewNo, crewId);
			if (viewTemp != null) {
				if (!StringUtils.isBlank(viewTemp.getTitle())) {
					viewTemp.setTitle(viewTemp.getTitle().replaceAll(this.lineSeparator, "<br/>"));
				}
				if (!StringUtils.isBlank(viewTemp.getContent())) {
					viewTemp.setContent(viewTemp.getContent().replaceAll(this.lineSeparator, "<br/>"));
				}
				
				resultMap.put("newViewData", viewTemp);
			}
			
			resultMap.put("crewType", crewType);
//			this.sysLogService.saveSysLog(request, "跳转到新老剧本比对页面", terminal, ViewTempModel.TABLE_NAME, seriesViewNo, 0);
			
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;

			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，查询失败";
			success = false;

			logger.error(message, e);
		}

		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 根据场景信息生成场景DTO
	 * 
	 * @param viewInfo
	 * @return
	 */
	private ViewInfoDto genViewInfoDtoByViewInfo(ViewInfoModel viewInfo) {
		String viewId = viewInfo.getViewId();

		// 气氛
		String atmosphereId = viewInfo.getAtmosphereId();
		AtmosphereInfoModel atmosphere = this.atmosphereService.queryOneById(atmosphereId);
		String atmosphereName = null;
		if (atmosphere != null) {
			atmosphereName = atmosphere.getAtmosphereName();
		}

		// 内外景
		String site = viewInfo.getSite();

		// 场景
		String firstLocation = null; //主场景
		String secondLocation = null; //次级场景
		String thirdLocation = null; //三级场景
		
		List<ViewLocationModel> viewLocationList = viewLocationService.queryManyByViewId(viewId);
		for (ViewLocationModel viewLocation : viewLocationList) {
			if (viewLocation.getLocationType() == LocationType.lvlOneLocation.getValue()) {
				firstLocation = viewLocation.getLocation();
			}
			if (viewLocation.getLocationType() == LocationType.lvlTwoLocation.getValue()) {
				secondLocation = viewLocation.getLocation();
			}
			if (viewLocation.getLocationType() == LocationType.lvlThreeLocation.getValue()) {
				thirdLocation = viewLocation.getLocation();
			}
		}

		// 演员角色
		String majorActor = null; // 主要演员
		String guestActor = null; // 特约演员
		String massesActor = null; // 群众演员
		
		List<Map<String, Object>> viewRoleList = this.viewRoleService.queryViewRoleByViewId(viewId);
		if (viewRoleList != null && viewRoleList.size() > 0) {
			//对演员角色表进行排序
			Collections.sort(viewRoleList, new Comparator<Map<String, Object>>() {
						@Override
						public int compare(Map<String, Object> o1, Map<String, Object> o2) {
							String o1ViewRoleName = (String) o1.get("viewRoleName");
							String o2ViewRoleName = (String) o2.get("viewRoleName");
							
							// 要想不区分大小写进行比较用o1.toString().toLowerCase()
							CollationKey key1 = Collator.getInstance().getCollationKey(o1ViewRoleName.toLowerCase());
							CollationKey key2 = Collator.getInstance().getCollationKey(o2ViewRoleName.toLowerCase());
							return key1.compareTo(key2);
						}
					});

			for (Map<String, Object> viewRole : viewRoleList) {
				//角色类型
				int viewRoleType = (Integer) viewRole.get("viewRoleType");
				//角色姓名
				String viewRoleName = (String) viewRole.get("viewRoleName");
				
				//取出主要演员的姓名,多个演员之间用","进行分割
				if (!StringUtils.isBlank(viewRoleName)) {
					if (viewRoleType == ViewRoleType.MajorActor.getValue()) {
						int roleNum = (Integer) viewRole.get("roleNum");
						
						if (roleNum == 0) {
							if (StringUtils.isBlank(majorActor)) {
								majorActor = "";
								majorActor += viewRoleName+"(OS)";
							} else {
								if (roleNum == 0) {
									majorActor = majorActor + "," + viewRoleName+"(OS)";
								}else {
									majorActor = majorActor + "," + viewRoleName;
								}
							}
						}else {
							if (StringUtils.isBlank(majorActor)) {
								majorActor = "";
								majorActor += viewRoleName;
							} else {
								majorActor = majorActor + "," + viewRoleName;
							}
						}
						continue;
					}
					
					//取出特约演员的姓名,多个演员之间用","进行分割
					if (viewRoleType == ViewRoleType.GuestActor.getValue()) {
						if (StringUtils.isBlank(guestActor)) {
							guestActor = "";
							guestActor += viewRoleName;
						} else {
							guestActor = guestActor + "," + viewRoleName;
						}
						continue;
					}
					
					//取出群众演员的姓名,多个演员之间用","进行分割
					if (viewRoleType == ViewRoleType.MassesActor.getValue()) {
						if (StringUtils.isBlank(massesActor)) {
							massesActor = "";
								int roleNum = (Integer) viewRole.get("roleNum");
								if (roleNum == 1) {
									massesActor += viewRoleName;
								}else {
									
									massesActor += viewRoleName + "_" + viewRole.get("roleNum");
								}
						} else {
							int roleNum = (Integer) viewRole.get("roleNum");
							if (roleNum == 1) {
								massesActor = massesActor + "," + viewRoleName;
							}else {
								massesActor = massesActor + "," + viewRoleName + "_" + viewRole.get("roleNum");
							}
						}
						continue;
					}
				}
			}
		}

		// 道具
		String commonProps = null; // 普通道具
		String personProps = null; // 个人道具
		// 服装
		String clothesName = null;
		// 化妆
		String makeupName = null;
		//查询出物品信息
		List<GoodsInfoModel> goodsList = this.goodsInfoService.queryGoodsInfoByViewid(viewId);
		for (GoodsInfoModel goodsInfo : goodsList) {
			
			if (goodsInfo.getGoodsType() != null) {
				int goodsType = goodsInfo.getGoodsType();
				//获取普通道具
				if (goodsType == GoodsType.CommonProps.getValue()) {
					if (StringUtils.isBlank(commonProps)) {
						commonProps = "";
						commonProps += goodsInfo.getGoodsName();
					} else {
						commonProps = commonProps + "," + goodsInfo.getGoodsName();
					}
				}
				
				//获取特殊道具
				if (goodsType == GoodsType.SpecialProps.getValue()) {
					if (StringUtils.isBlank(personProps)) {
						personProps = "";
						personProps = goodsInfo.getGoodsName();
					} else {
						personProps = personProps + "," + goodsInfo.getGoodsName();
					}
				}
				
				//化妆
				if (goodsType == GoodsType.Makeup.getValue()) {
					if (StringUtils.isBlank(makeupName)) {
						makeupName = "";
						makeupName = goodsInfo.getGoodsName();
					} else {
						makeupName += "," + goodsInfo.getGoodsName();
					}
				}
				
				//服装
				if (goodsType == GoodsType.Clothes.getValue()) {
					if (StringUtils.isBlank(clothesName)) {
						clothesName = "";
						clothesName += goodsInfo.getGoodsName();
					} else {
						clothesName += "," + goodsInfo.getGoodsName();
					}
				}
			}
		}
		
		// 拍摄状态
		Map<Integer, String> shootStatusMap = new HashMap<Integer, String>();
		shootStatusMap.put(0, "未完成");
		shootStatusMap.put(1, "部分完成");
		shootStatusMap.put(2, "已完成");
		shootStatusMap.put(3, "删戏");
		shootStatusMap.put(4, "加戏未完成");
		shootStatusMap.put(5, "加戏已完成");
		String shootStatusStr = shootStatusMap.get(viewInfo.getShootStatus());

		// 标题和剧本内容
		String title = null;
		String content = null;
		ViewContentModel viewContent = this.viewContentService
				.queryByViewId(viewId);
		if (viewContent != null) {
			title = !StringUtils.isBlank(viewContent.getTitle()) ? viewContent
					.getTitle().replaceAll(this.lineSeparator, "<br/>") : null;
			content = !StringUtils.isBlank(viewContent.getContent()) ? viewContent
					.getContent().replaceAll(this.lineSeparator, "<br/>")
					: null;
		}

		// 拍摄地点
		String shootLocation = null;
		String shootRegion = null;
		SceneViewInfoModel shootLocationInfo = this.sceneViewInfoService.queryOneByShootLocationId(viewInfo.getShootLocationId());
		if (shootLocationInfo != null) {
			shootLocation = shootLocationInfo.getVName();
			shootRegion = shootLocationInfo.getVCity();
		}

		// 新建场景Dto对象,并对属性进行封装
		ViewInfoDto viewInfoDto = new ViewInfoDto();
		viewInfoDto.setViewId(viewInfo.getViewId());
		
		viewInfoDto.setSeriesNo(viewInfo.getSeriesNo());
		viewInfoDto.setViewNo(viewInfo.getViewNo());
		viewInfoDto.setAtmosphereName(atmosphereName);
		viewInfoDto.setSite(site);
		
		if (viewInfo.getPageCount() != null) {
			viewInfoDto.setPageCount(viewInfo.getPageCount());
		}
		
		viewInfoDto.setFirstLocation(firstLocation);
		viewInfoDto.setSecondLocation(secondLocation);
		viewInfoDto.setThirdLocation(thirdLocation);
		viewInfoDto.setMainContent(viewInfo.getMainContent());
		viewInfoDto.setMajorActor(majorActor);
		
		viewInfoDto.setGuestActor(guestActor);
		viewInfoDto.setMassesActor(massesActor);
		viewInfoDto.setCommonProps(commonProps);
		viewInfoDto.setSpecialProps(personProps);
		viewInfoDto.setType(viewInfo.getViewType());
		
		viewInfoDto.setTypeValue(viewInfo.getViewType());
		viewInfoDto.setRemark(viewInfo.getRemark());
		viewInfoDto.setShootStatus(viewInfo.getShootStatus());
		viewInfoDto.setTitle(title);
		
		viewInfoDto.setContent(content);
		viewInfoDto.setManualSave(viewInfo.getIsManualSave());
		viewInfoDto.setClothes(clothesName);
		viewInfoDto.setMakeups(makeupName);
		
		viewInfoDto.setShootStatusValue(shootStatusStr);
		viewInfoDto.setShootLocation(shootLocation);
		viewInfoDto.setShootRegion(shootRegion);
		viewInfoDto.setSpecialRemind(viewInfo.getSpecialRemind());

		return viewInfoDto;
	}

	/**
	 * 跳转到剧本分析页面
	 * @param pageType 页面类型：1-剧本分析  2-剧本编辑   3-剧本比对
	 * @return ModelAndView
	 */
	@RequestMapping("/toScenarioManagePage")
	public ModelAndView toScenarioManagePage(HttpServletRequest request, String startSeriesNo, Integer pageType) {
		ModelAndView mv = new ModelAndView();
		if (StringUtils.isNotBlank(startSeriesNo)) {
			mv.addObject("startSeriesNo", startSeriesNo);
		}
		if (pageType == null || pageType == 1) {
			mv.setViewName("/view/scenarioAnalysis");
		} else if (pageType == 2) {
			mv.setViewName("/view/scenarioEdit");
		} else if (pageType == 3) {
			mv.setViewName("/view/scenarioCompare");
		}
		
		return mv;
	}
	
	/**
	 * 获取当前剧组所有剧本的集次号和场次号和书签信息
	 * 
	 * @param request
	 * @param seriesViewNo 集次-场次编号
	 * @param fromUpload 是否是剧本分析页面跳转过来的
	 * @param startSeriesNo 初始集次号
	 * @return map 结果集合
	 */
	@RequestMapping("/querySeriesNoAndViewNo")
	@ResponseBody
	public Map<String, Object> querySeriesNoAndViewNo(HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		String crewId = this.getCrewId(request);
		String userId = this.getLoginUserId(request);
		UserInfoModel userInfo = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);

		boolean success = true; // 方法执行成功与否
		String message = ""; // 方法执行后返回的信息
		try {
			//查询当前用户已读的剧本内容对应的场景ID列表
			List<Map<String, Object>> readedPeopleInfo = this.viewContentService.queryReadedPeopleInfo(crewId);
			List<String> readedViewIdList = new ArrayList<String>();
			if (readedPeopleInfo != null) {
				for (Map<String, Object> contentInfo : readedPeopleInfo) {
					String viewId = (String) contentInfo.get("viewId");
					String readedPeopleIds = (String) contentInfo.get("readedPeopleIds");
					
					Type type = new TypeToken<List<String>>(){}.getType();
					List<String> readedPeopleIdList = (List<String>) GsonUtils.fromJson(readedPeopleIds, type);
					if (readedPeopleIdList != null && readedPeopleIdList.contains(userId)) {
						readedViewIdList.add(viewId);
					}
				}
			}

			// 查找所有集次场次信息，用户加载页面上左侧的集次-场次树
			List<ViewInfoModel> viewInfoList = this.viewInfoService.queryByCrewId(crewId, null);
			if (viewInfoList == null || viewInfoList.size() == 0) {
				resultMap.put("noViewMessage", "当前剧组还未上传剧本，是否现在上传？");
				success = false;
				resultMap.put("success", success);
				return resultMap;
			}

			// 存储集-场的对应的关系,key-集次 value-该集下所有场次
			Map<Integer, List<String>> seriesViewNoMap = new TreeMap<Integer, List<String>>();
			// 取出剧本信息中所有的集次添加进集次信息列表中
			List<Integer> seriesNoList = new ArrayList<Integer>();
			for (ViewInfoModel viewInfo : viewInfoList) {
				if (viewInfo.getSeriesNo() != null && !seriesNoList.contains(viewInfo.getSeriesNo())) {
					seriesNoList.add(viewInfo.getSeriesNo());
				}
			}

			// 取出剧本信息中所有的场次添加进场次信息列表中
			for (Integer seriesNo : seriesNoList) {
				List<String> viewList = new LinkedList<String>();
				for (ViewInfoModel viewInfo : viewInfoList) {
					if (viewInfo.getSeriesNo() == seriesNo) {
						viewList.add(viewInfo.getViewNo());
					}
				}

				// 对当前的场次进行排序
				Comparator<String> sort = com.xiaotu.makeplays.utils.StringUtils.sort();
				Collections.sort(viewList, sort);
				// 将对应的集次与场次信息添加到存储集场关系的map中
				seriesViewNoMap.put(seriesNo, viewList);
			}

			// 从map中取出所有的集次
			Set<Integer> keySet = seriesViewNoMap.keySet();
			Iterator<Integer> iter = keySet.iterator();

			List<SeriesNoDto> seriesNoDtoList = new ArrayList<SeriesNoDto>();
			while (iter.hasNext()) {
				Integer key = (Integer) iter.next();
				// 根据集次信息取出场次信息列表
				List<String> value = seriesViewNoMap.get(key);

				SeriesNoDto seriesNoDto = new SeriesNoDto();
				seriesNoDto.setSeriesNo(key);
				// 定义场次信息列表
				List<ViewNoDto> viewNoDtoList = new ArrayList<ViewNoDto>();

				for (String viewNo : value) {
					for (ViewInfoModel viewInfo : viewInfoList) {
						// 遍历集合,只有当前的集次号与数据库中取出的集次号相等并且场次也相等时,才能往场次信息列表中存储
						if (viewInfo.getSeriesNo() == key && viewInfo.getViewNo().equals(viewNo)) {
							ViewNoDto viewNoDto = new ViewNoDto();
							viewNoDto.setViewNo(viewNo);
							viewNoDto.setIsManualSave(viewInfo.getIsManualSave());
							viewNoDto.setViewId(viewInfo.getViewId());

							// 如果当前的集次场次信息中仍然存在未提取的角色信息,则在场次对象中进行标识
							if (!StringUtils.isBlank(viewInfo.getNotGetRoleNames())) {
								viewNoDto.setHasNoGetRole(true);
							}
							if (!StringUtils.isBlank(viewInfo.getNotGetProps())) {
								viewNoDto.setHasNoGetRole(true);
							}
							if (readedViewIdList.contains(viewInfo.getViewId())) {
								viewNoDto.setIsReaded(true);
							} else {
								viewNoDto.setIsReaded(false);
							}
							
							viewNoDtoList.add(viewNoDto);
						}
					}
				}

				seriesNoDto.setViewNoDtoList(viewNoDtoList);
				seriesNoDtoList.add(seriesNoDto);
			}

			// 查询书签信息
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("crewId", crewId);
			conditionMap.put("userId", userInfo.getUserId());
			conditionMap.put("type", BookmarkType.BookMarkType.getValue());

			List<BookMarkModel> existBokMarkInfoList = this.bookMarkService.queryManyByMutiCondition(conditionMap, null);
			if (existBokMarkInfoList != null && existBokMarkInfoList.size() > 0) {
				String bookMarkViewId = existBokMarkInfoList.get(0).getValue();
				ViewInfoModel bookMarkViewInfo = this.viewInfoService.queryOneByViewId(bookMarkViewId);

				if (bookMarkViewInfo != null) {
					String bmSeriesViewNo = bookMarkViewInfo.getSeriesNo() + "-" + bookMarkViewInfo.getViewNo();
					resultMap.put("bmSeriesViewNo", bmSeriesViewNo);
				}
			}
			
			resultMap.put("seriesNoDtoList", seriesNoDtoList);
		} catch (Exception e) {
			logger.error("未知异常", e);
			success = false;
			message = "未知异常";
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
		
	}
	
	/**
	 * 根据集场次号查询出剧本内容
	 * @param seriesViewNo 集场次编号
	 * @return
	 */
	@RequestMapping("/queryViewContent")
	public @ResponseBody Map<String, Object> queryViewContent(HttpServletRequest request, String viewId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String crewId = this.getCrewId(request);
		String userId = this.getLoginUserId(request);
		boolean success = true;
		String message = "";
		
		try {
			if (StringUtils.isBlank(viewId)) {
				throw new IllegalArgumentException("请提供需要获取内容的集场编号");
			}
			
			ViewInfoModel viewInfo = this.viewInfoService.queryOneByViewId(viewId);
			
			if (viewInfo != null) {
				String title = "";
				String content = "";
				
				//取出当前场景的剧本内容信息
				ViewContentModel viewContent = this.viewContentService.queryByViewId(viewId);
				if (viewContent != null) {
					title = !StringUtils.isBlank(viewContent.getTitle()) ? viewContent.getTitle() : "";
					content = !StringUtils.isBlank(viewContent.getContent()) ? viewContent.getContent() : "";
				}
				resultMap.put("seriesNoAndViewNo", viewInfo.getSeriesNo() + "-" + viewInfo.getViewNo());
				resultMap.put("title", title);
				resultMap.put("shootStatus", viewInfo.getShootStatus());
				//对应场次的剧本的详细信息
				resultMap.put("viewContent", content);
				if (!StringUtils.isBlank(viewInfo.getNotGetRoleNames())) {
					resultMap.put("noGetRoleNames", viewInfo.getNotGetRoleNames().substring(0, viewInfo.getNotGetRoleNames().length() - 1));
				}
				if (!StringUtils.isBlank(viewInfo.getNotGetProps())) {
					resultMap.put("noGetProps", viewInfo.getNotGetProps().substring(0, viewInfo.getNotGetProps().length() - 1));
				}
				
				//更新已阅人员信息
				if (viewContent != null) {
					String readedPeopleIds = viewContent.getReadedPeopleIds();
					List<String> readedPeopleIdList = new ArrayList<String>();
					if (!StringUtils.isBlank(readedPeopleIds)) {
						Type type = new TypeToken<List<String>>(){}.getType();
						readedPeopleIdList = (List<String>) GsonUtils.fromJson(readedPeopleIds, type);
					}
					if (!readedPeopleIdList.contains(userId)) {
						readedPeopleIdList.add(userId);
					}
					String newReadedPeopleIds = JSONArray.fromObject(readedPeopleIdList) + "";
					viewContent.setReadedPeopleIds(newReadedPeopleIds);
					this.viewContentService.updateOne(viewContent);
				}
				
				
				//更新书签信息
				BookMarkModel bookMarkInfo = new BookMarkModel();
				bookMarkInfo.setId(UUIDUtils.getId());
				bookMarkInfo.setType(BookmarkType.BookMarkType.getValue());
				bookMarkInfo.setCrewId(crewId);
				bookMarkInfo.setUserId(userId);
				bookMarkInfo.setValue(viewInfo.getViewId());
				
				this.scenarioService.saveSceBookMark(bookMarkInfo);
			}
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			String msg = "未知异常，查询场景内容失败";
			logger.error(msg, e);
			success = false;
			message = msg;
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}

	/**
	 * 跳转到场景详细信息页面
	 * 
	 * @param seriesViewNo 集场编号
	 * @return
	 */
	@RequestMapping("/toViewDetailInfo")
	public ModelAndView toViewDetailInfo(HttpServletRequest request, String viewId) {
		ModelAndView mv = new ModelAndView("/view/viewDetailInfo");
		if (!StringUtils.isBlank(viewId)) {
			mv.addObject("viewId", viewId);
		}
		return mv;
	}

	/**
	 * 根据集场编号,获取场景详情
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryViewDetailInfo")
	@ResponseBody
	public Map<String, Object> queryViewDetailInfo(HttpServletRequest request, String viewId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		//取出当前剧组的类型
		Integer crewType = crewInfo.getCrewType();

		boolean success = true;
		String message = "";
		try {
			String crewId = getCrewId(request);
			/*
			 * 当集场编号不为空时,表示是根据集场号进行查询场景的详细信息
			 * 当集场号为空时表示是新增场景信息
			}*/
			if (StringUtils.isNotBlank(viewId)) {
				ViewInfoModel viewInfo = this.viewInfoService.queryOneByViewId(viewId);
				if (viewInfo != null) {
					//根据场景信息的model类生成场景DTO类
					ViewInfoDto viewInfoDto = this.genViewInfoDtoByViewInfo(viewInfo);
					//根据场景id查询出当前场景的商植信息
					List<Map<String, Object>> advertInfoList = this.insideAdvertService.queryAdvertByViewId(viewInfo.getViewId());
					
					resultMap.put("viewInfoDto", viewInfoDto);
					resultMap.put("advertInfoList", advertInfoList);
					resultMap.put("saveType", "update");
				}
			} else {
				// 当seriesViewNo即集场编号为空时，执行新增，当seriesViewNo有值时执行更新
				resultMap.put("saveType", "new");
			}
			
			//根据剧组ID查找剧组下的所有下拉框列表信息
			ViewFilterDto filterDto = this.viewInfoService.genFilterDtoByCrewId(crewId, true);
			
			resultMap.put("filterDto", filterDto);
//			this.sysLogService.saveSysLog(request, "查询场景详细信息", terminal,	ViewInfoModel.TABLE_NAME, seriesViewNo, 0);
		} catch(IllegalArgumentException ie){
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常,加载场景详细信息失败！";
			success = false;
			
			logger.error(message, e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		resultMap.put("crewType", crewType);
		
		return resultMap;
	}
	
	/**
	 * 保存或更新场景信息
	 * 
	 * @param request
	 * @param site 内外景
	 * @param culturFierce 文/武戏
	 * @param specialRemind 特殊提醒
	 * @param massesActor 群众演员
	 * @param remark 备注
	 * @param viewId 场景ID
	 * @param guestActor 特约演员
	 * @param seriesNo 集次
	 * @param mainContent 主要内容
	 * @param majorActor 主要演员
	 * @param thirdLocation 三级场景
	 * @param secondLocation 次场景
	 * @param viewNo 场次
	 * @param firstLocation 主场景
	 * @param specialProps 个人道具
	 * @param commonProps 普通道具
	 * @param clothes 服装
	 * @param makeups 化妆
	 * @param shootLocation 拍摄地点
	 * @param title 剧本标题
	 * @param content 剧本内容
	 * @param view 气氛
	 * @param shootRegion 地域
	 * @return
	 */
	@ResponseBody 
	@RequestMapping("/saveViewInfo")
	public Map<String, Object> saveViewInfo(HttpServletRequest request,String site, String specialRemind,
				String massesActor, String remark, String viewId, String guestActor, String seriesNo, String mainContent,
				String majorActor, String thirdLocation, String secondLocation,	String viewNo, String firstLocation, 
				String view, String specialProps, String commonProps, String clothes, String makeups, Double pageCount, 
				String shootLocation, String shootRegion, String title, String content) {
		
		String crewId = getCrewId(request);
		UserInfoModel userInfo = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (pageCount == null) {
			pageCount = 0.00;
		}
		BigDecimal deciaml = new BigDecimal(pageCount); 
		boolean success = true;
		String message = "";
		String logDesc = "";
		String idArrayStr = "";
		Integer operType = null;
		
		try {

			if (StringUtils.isBlank(seriesNo) || StringUtils.isBlank(viewNo)) {
				throw new IllegalArgumentException("请填写集场信息");
			}

			// 校验主要演员是否在特约演员和群众演员中同时存在
			this.checkActor(massesActor, guestActor, majorActor);
			
			//校验普通道具和特殊道具是否有重复
			this.checkPropsRepeat(specialProps, commonProps, crewId);
			
			//去除title和content中的空行
			if (!StringUtils.isBlank(title)) {
				title = title.replaceAll("\n+", this.lineSeparator);
				if (title.endsWith(this.lineSeparator)) {
					title = title.substring(0, title.length() - this.lineSeparator.length());
				}
			}
			if (!StringUtils.isBlank(content)) {
				content = content.replaceAll("\n+", this.lineSeparator);
				if (content.endsWith(this.lineSeparator)) {
					content = content.substring(0, content.length() - this.lineSeparator.length());
				}
			}
			if (!StringUtils.isBlank(view)) {
				view = view.trim();
			}
			
			//如果场景是已完成或删戏状态，则不再保存场景信息
			/*if (!StringUtils.isBlank(viewId)) {
				ViewInfoModel viewInfo = this.viewInfoService.queryOneByViewId(viewId);
				if (viewInfo.getShootStatus() == ShootStatus.DeleteXi.getValue() || viewInfo.getShootStatus() == ShootStatus.Finished.getValue()) {
					resultMap.put("viewId", viewId);
					resultMap.put("success", success);
					return resultMap;
				}
			}*/
			
			ViewInfoModel viewInfo = new ViewInfoModel();
			//校验集场号是否已存在
			viewInfo = this.viewInfoService.queryOneByCrewIdAndSeriaViewNo(crewId, Integer.parseInt(seriesNo), viewNo.toUpperCase());
			if (StringUtils.isBlank(viewId) && viewInfo != null) {
				throw new IllegalArgumentException("集场号已存在，请重新输入");
			}
			if (!StringUtils.isBlank(viewId) && viewInfo != null && !viewInfo.getViewId().equals(viewId)) {
				throw new IllegalArgumentException("集场号已存在，请重新输入");
			}
			

			ViewInfoDto viewInfoDto = new ViewInfoDto();
			viewInfoDto.setViewId(viewId);
			viewInfoDto.setSeriesNo(Integer.parseInt(seriesNo));
			viewInfoDto.setViewNo(viewNo.toUpperCase());
			viewInfoDto.setAtmosphereName(view);
			viewInfoDto.setSite(site.trim());
			viewInfoDto.setFirstLocation(firstLocation);
			viewInfoDto.setSecondLocation(secondLocation);
			viewInfoDto.setThirdLocation(thirdLocation);
			viewInfoDto.setMainContent(mainContent);
			viewInfoDto.setSpecialRemind(specialRemind);
			viewInfoDto.setCrewId(crewId);
			viewInfoDto.setRemark(remark);
			viewInfoDto.setTitle(title);
			viewInfoDto.setContent(content);
			if (pageCount != null) {
				viewInfoDto.setPageCount(deciaml.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
			}
			viewInfoDto.setShootLocation(shootLocation);
			viewInfoDto.setShootRegion(shootRegion);

			// 为了去除演员信息中一些重复的信息，利用list把重复的去掉
			List<String> uniqueStrList = new ArrayList<String>();
			if (!StringUtils.isBlank(majorActor)) {
				uniqueStrList.clear();
				String dealedMajorActor = "";
				String[] majorActorArr = majorActor.split(",");
				for (String majorActorStr : majorActorArr) {
					if (!uniqueStrList.contains(majorActorStr)) {
						uniqueStrList.add(majorActorStr);
						dealedMajorActor = dealedMajorActor + "," + majorActorStr;
					}
				}
				viewInfoDto.setMajorActor(dealedMajorActor);
			}
			if (!StringUtils.isBlank(massesActor)) {
				uniqueStrList.clear();
				String dealedMassesActor = "";
				String[] massesActorArr = massesActor.split(",");
				for (String massesActorStr : massesActorArr) {
					if (!uniqueStrList.contains(massesActorStr.split("_")[0])) {
						uniqueStrList.add(massesActorStr);
						dealedMassesActor = dealedMassesActor + "," + massesActorStr;
					}
				}
				viewInfoDto.setMassesActor(dealedMassesActor);
			}
			if (!StringUtils.isBlank(guestActor)) {
				uniqueStrList.clear();
				String dealedGuestActor = "";
				String[] guestActorArr = guestActor.split(",");
				for (String guestActorStr : guestActorArr) {
					if (!uniqueStrList.contains(guestActorStr)) {
						uniqueStrList.add(guestActorStr);
						dealedGuestActor = dealedGuestActor + "," + guestActorStr;
					}
				}
				viewInfoDto.setGuestActor(dealedGuestActor);
			}

			// 道具的重复处理(普通道具处理)
			if (!StringUtils.isBlank(commonProps)) {
				uniqueStrList.clear();
				String dealedMajorTopStr = "";
				String[] majorTopArr = commonProps.split(",");
				for (String majorTopStr : majorTopArr) {
					if (!uniqueStrList.contains(majorTopStr)) {
						uniqueStrList.add(majorTopStr);
						dealedMajorTopStr = dealedMajorTopStr + ","
								+ majorTopStr;
					}
				}
				viewInfoDto.setCommonProps(dealedMajorTopStr);
			}
			//特殊道具处理
			if (!StringUtils.isBlank(specialProps)) {
				uniqueStrList.clear();
				String dealedSpecialTopStr = "";
				String[] specialTopArr = specialProps.split(",");
				for (String specialTopStr : specialTopArr) {
					if (!uniqueStrList.contains(specialTopStr)) {
						uniqueStrList.add(specialTopStr);
						dealedSpecialTopStr = dealedSpecialTopStr + ","
								+ specialTopStr;
					}
				}
				viewInfoDto.setSpecialProps(dealedSpecialTopStr);
			}

			// 服装的重复处理
			if (!StringUtils.isBlank(clothes)) {
				uniqueStrList.clear();
				String dealedClothesStr = "";
				String[] clothesArr = clothes.split(",");
				for (String clothesStr : clothesArr) {
					if (!uniqueStrList.contains(clothesStr)) {
						uniqueStrList.add(clothesStr);
						dealedClothesStr = dealedClothesStr + "," + clothesStr;
					}
				}
				viewInfoDto.setClothes(dealedClothesStr);
			}
			
			//化妆的处理
			if (!StringUtils.isBlank(makeups)) {
				uniqueStrList.clear();
				String dealedMakeupsStr = "";
				String[] makeupsArr = makeups.split(",");
				for (String makeupsStr : makeupsArr) {
					if (!uniqueStrList.contains(makeupsStr)) {
						uniqueStrList.add(makeupsStr);
						dealedMakeupsStr = dealedMakeupsStr + "," + makeupsStr;
					}
				}
				viewInfoDto.setMakeups(dealedMakeupsStr);
			}

			// 保存数据
			if (StringUtils.isBlank(viewId)) {
				viewInfo = this.viewInfoService.addByViewInfoDto(viewInfoDto, userInfo, crewId);
				idArrayStr = viewInfo.getViewId();
				
				message = "操作成功";
				logDesc = "新增场景";
				operType = 1;
			} else {
				// 更新数据
				viewInfo = this.viewInfoService.updateByViewInfoDto(viewInfoDto, userInfo);
				
				message = "操作成功";
				logDesc = "更新场景";
				idArrayStr = viewId;
				operType = 2;
			}
			
			resultMap.put("viewId", viewInfo.getViewId());
			this.sysLogService.saveSysLog(request, logDesc, terminal, ViewInfoModel.TABLE_NAME, seriesNo + "-" + viewNo, operType);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常,保存失败!";
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "保存或更新场景信息失败：" + e.getMessage(), terminal, seriesNo + "-" + viewNo, idArrayStr, SysLogOperType.ERROR.getValue());
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}

	/**
	 * 暂时没有使用
	 * 保存或更新场景信息 该方法和saveViewInfo方法的区别在于，字段的命名严格按照StandardViewInfoDto中的命名规则来
	 * 
	 * @param request
	 * @return
	 */
	@Deprecated
	@RequestMapping("/saveOrUpdateViewInfo")
	public @ResponseBody Map saveOrUpdateViewInfo(HttpServletRequest request, StandardViewInfoDto standardViewInfoDto) {
		String crewId = this.getCrewId(request);
		UserInfoModel userInfo = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";

		String logDesc = "";
		String idArrayStr = "";
		try {

			if (standardViewInfoDto.getSeriesNo() == null || StringUtils.isBlank(standardViewInfoDto.getViewNo())) {
				throw new IllegalArgumentException("请填写集场信息");
			}

			// 校验主要演员是否在特约演员和群众演员中同时存在
			this.checkActor(standardViewInfoDto.getMassesRoles(), standardViewInfoDto.getGuestRoles(), standardViewInfoDto.getLeadingRoles());

			ViewInfoDto viewInfoDto = new ViewInfoDto();
			viewInfoDto.setViewId(standardViewInfoDto.getViewId());
			viewInfoDto.setSeriesNo(standardViewInfoDto.getSeriesNo());
			viewInfoDto.setViewNo(standardViewInfoDto.getViewNo().trim());
			viewInfoDto.setAtmosphereName(standardViewInfoDto
					.getAtmosphereName().trim());
			viewInfoDto.setSite(standardViewInfoDto.getSite().trim());
			viewInfoDto.setFirstLocation(standardViewInfoDto
					.getLvlOneLocation().trim());
			viewInfoDto.setSecondLocation(standardViewInfoDto
					.getLvlTwoLocation().trim());
			viewInfoDto.setThirdLocation(standardViewInfoDto
					.getLvlThreeLocation().trim());
			viewInfoDto.setMainContent(standardViewInfoDto.getMainContent()
					.trim());

			viewInfoDto.setCrewId(crewId);
			viewInfoDto.setRemark(standardViewInfoDto.getRemark());
			if (standardViewInfoDto.getPageCount() != null) {
				viewInfoDto.setPageCount(standardViewInfoDto.getPageCount());
			}
			viewInfoDto.setShootLocation(standardViewInfoDto.getShootLocation()
					.trim());

			// 为了去除演员信息中一些重复的信息，利用list把重复的去掉
			List<String> uniqueStrList = new ArrayList<String>();
			if (!StringUtils.isBlank(standardViewInfoDto.getLeadingRoles())) {
				uniqueStrList.clear();
				String dealedMajorActor = "";
				String[] majorActorArr = standardViewInfoDto.getLeadingRoles()
						.split(",");
				for (String majorActorStr : majorActorArr) {
					if (!uniqueStrList.contains(majorActorStr)) {
						uniqueStrList.add(majorActorStr);
						dealedMajorActor = dealedMajorActor + ","
								+ majorActorStr;
					}
				}
				viewInfoDto.setMajorActor(dealedMajorActor);
			}
			if (!StringUtils.isBlank(standardViewInfoDto.getMassesRoles())) {
				uniqueStrList.clear();
				String dealedMassesActor = "";
				String[] massesActorArr = standardViewInfoDto.getMassesRoles()
						.split(",");
				for (String massesActorStr : massesActorArr) {
					if (!uniqueStrList.contains(massesActorStr.split("_")[0])) {
						uniqueStrList.add(massesActorStr);
						dealedMassesActor = dealedMassesActor + ","
								+ massesActorStr;
					}
				}
				viewInfoDto.setMassesActor(dealedMassesActor);
			}
			if (!StringUtils.isBlank(standardViewInfoDto.getGuestRoles())) {
				uniqueStrList.clear();
				String dealedGuestActor = "";
				String[] guestActorArr = standardViewInfoDto.getGuestRoles()
						.split(",");
				for (String guestActorStr : guestActorArr) {
					if (!uniqueStrList.contains(guestActorStr)) {
						uniqueStrList.add(guestActorStr);
						dealedGuestActor = dealedGuestActor + ","
								+ guestActorStr;
					}
				}
				viewInfoDto.setGuestActor(dealedGuestActor);
			}

			// 道具的重复处理
			if (!StringUtils.isBlank(standardViewInfoDto.getCommonProps())) {
				uniqueStrList.clear();
				String dealedMajorTopStr = "";
				String[] majorTopArr = standardViewInfoDto.getCommonProps()
						.split(",");
				for (String majorTopStr : majorTopArr) {
					if (!uniqueStrList.contains(majorTopStr)) {
						uniqueStrList.add(majorTopStr);
						dealedMajorTopStr = dealedMajorTopStr + ","
								+ majorTopStr;
					}
				}
				viewInfoDto.setCommonProps(dealedMajorTopStr);
			}
			if (!StringUtils.isBlank(standardViewInfoDto.getSpecialProps())) {
				uniqueStrList.clear();
				String dealedSpecialTopStr = "";
				String[] specialTopArr = standardViewInfoDto.getSpecialProps()
						.split(",");
				for (String specialTopStr : specialTopArr) {
					if (!uniqueStrList.contains(specialTopStr)) {
						uniqueStrList.add(specialTopStr);
						dealedSpecialTopStr = dealedSpecialTopStr + ","
								+ specialTopStr;
					}
				}
				viewInfoDto.setSpecialProps(dealedSpecialTopStr);
			}

			// 服装化妆的重复处理
			if (!StringUtils.isBlank(standardViewInfoDto.getClothes())) {
				uniqueStrList.clear();
				String dealedClothesStr = "";
				String[] clothesArr = standardViewInfoDto.getClothes().split(
						",");
				for (String clothesStr : clothesArr) {
					if (!uniqueStrList.contains(clothesStr)) {
						uniqueStrList.add(clothesStr);
						dealedClothesStr = dealedClothesStr + "," + clothesStr;
					}
				}
				viewInfoDto.setClothes(dealedClothesStr);
			}
			if (!StringUtils.isBlank(standardViewInfoDto.getMakeups())) {
				uniqueStrList.clear();
				String dealedMakeupsStr = "";
				String[] makeupsArr = standardViewInfoDto.getMakeups().split(
						",");
				for (String makeupsStr : makeupsArr) {
					if (!uniqueStrList.contains(makeupsStr)) {
						uniqueStrList.add(makeupsStr);
						dealedMakeupsStr = dealedMakeupsStr + "," + makeupsStr;
					}
				}
				viewInfoDto.setMakeups(dealedMakeupsStr);
			}

			// 保存数据
			if (StringUtils.isBlank(standardViewInfoDto.getViewId())) {
				// 新增数据
				ViewInfoModel viewInfo = this.viewInfoService.queryOneByCrewIdAndSeriaViewNo(crewId,
								standardViewInfoDto.getSeriesNo(),
								standardViewInfoDto.getViewNo());
				if (null != viewInfo) {
					throw new IllegalArgumentException("场景编号已存在，请重新输入");
				} else {
					/*viewInfo = this.viewInfoService.addByViewInfoDto(
							viewInfoDto, userInfo, crewId);*/
					idArrayStr = viewInfo.getViewId();
				}
				success = true;
				message = "操作成功";
				logDesc = "新增场景";
			} else {
				// 更新数据
				//this.viewInfoService.updateByViewInfoDto(viewInfoDto, userInfo);
				success = true;
				message = "操作成功";

				logDesc = "更新场景";
				idArrayStr = standardViewInfoDto.getViewId();
			}

		} catch (IllegalArgumentException ie) {
			ie.printStackTrace();

			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			e.printStackTrace();

			success = false;
			message = "保存失败";
		}

		try {
			this.sysLogService.saveSysLog(request, logDesc, terminal,
					ViewInfoModel.TABLE_NAME, idArrayStr, 2);
		} catch (Exception e) {
			logger.error("未知异常，保存系统日志失败", e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}

	/**
	 * 校验集-场编号是否有重复
	 * 
	 * @param request
	 * @param seriesNo
	 * @param viewNo
	 * @return
	 */
	@Deprecated
	@ResponseBody
	@RequestMapping("/checkSeriesViewNoRepeat")
	public Map<String, Object> checkSeriesViewNoRepeat(HttpServletRequest request, Integer seriesNo, String viewNo) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);

			boolean repeated = false;
			ViewInfoModel viewInfo = this.viewInfoService.queryOneByCrewIdAndSeriaViewNo(crewId, seriesNo, viewNo);
			if (viewInfo != null) {
				repeated = true;
			}

			resultMap.put("repeated", repeated);
		} catch (Exception e) {
			success = false;
			message = "未知异常，校验失败。";
			logger.error("未知异常，校验失败。", e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}

	/**
	 * 删除场景信息(支持批量删除场景信息)
	 * 
	 * @param request
	 * @param viewIds
	 *            场景ID的字符串(多个id用","进行分割)
	 */
	@ResponseBody
	@RequestMapping("/deleteViewInfo")
	public Map<String, Object> deleteView(HttpServletRequest request, String viewIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String crewId = getCrewId(request);
		
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(viewIds)) {
				throw new IllegalArgumentException("请选择要删除的场景!");
			}
			
			// 删除场景信息
			this.viewInfoService.deleteViewByViewId(crewId, viewIds);
			message = "删除成功";
			this.sysLogService.saveSysLog(request, "批量删除场景信息(" + viewIds.split(",").length + ")", terminal, ViewInfoModel.TABLE_NAME, viewIds, 5);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;

			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，删除失败";
			success = false;

			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "批量删除场景信息(" + viewIds.split(",").length + ")失败：" + e.getMessage(), terminal, ViewInfoModel.TABLE_NAME, viewIds, SysLogOperType.ERROR.getValue());
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}

	/**
	 * 校验主要演员是否在特约演员和群众演员中同时存在
	 * 
	 * @param massesActor
	 *            群众演员
	 * @param guestActor
	 *            特约演员
	 * @param majorActor
	 *            主要演员
	 * @return
	 * @throws IllegalArgumentException 
	 */
	private void checkActor(String massesActor, String guestActor, String majorActor) throws IllegalArgumentException {
		String[] massesActorArr = massesActor.split(",");
		String[] guestActorArr = guestActor.split(",");
		String[] majorActorArr = majorActor.split(",");

		// 校验传过来的数据
		for (String majorAcStr : majorActorArr) {
			majorAcStr = majorAcStr.trim();
			String majorAc = "";
			if (majorAcStr.contains("OS")) {
				majorAc = majorAcStr.substring(0, majorAcStr.indexOf("("));
			}else {
				majorAc = majorAcStr;
			}
			if (!StringUtils.isBlank(majorAc)) {
				for (String guestAc : guestActorArr) {
					guestAc = guestAc.trim();
					if (!StringUtils.isBlank(guestAc)
							&& guestAc.equals(majorAc)) {
						throw new IllegalArgumentException("演员‘" + majorAc
								+ "’在主要演员和特约演员中同时存在，保存失败");
					}
				}
				
				for (String massesAc : massesActorArr) {
					massesAc = massesAc.trim().split("_")[0];
					if (!StringUtils.isBlank(massesAc)
							&& massesAc.equals(majorAc)) {
						throw new IllegalArgumentException("演员‘" + majorAc
								+ "’在主要演员和群众演员中同时存在，保存失败");
					}
				}
			}
		}
	}

	/**
	 * 跳转到场景列表页面
	 * 
	 * @return
	 */
	@RequestMapping("/toviewListPage")
	public ModelAndView toviewListPage(HttpServletRequest request) {
		ModelAndView view = new ModelAndView("view/viewList");
		/*view.addObject("message", message);
		if (!StringUtils.isBlank(source)) {
			view.addObject("source", source);
		}*/
		
		return view;
	}

	/**
	 * 跳转到场景列表界面时需要加载的数据
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryViewList")
	public Map<String, Object> queryViewList(HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		String crewId = getCrewId(request);
		try {
			// 主要演员信息
			List<Map<String, Object>> majorRoleList = this.viewRoleService.queryRoleMapByCrewIdAndRoleType(crewId, ViewRoleType.MajorActor.getValue());

			// 分组信息
			Map<String, String> groupList = new LinkedHashMap<String, String>(); // 分组信息
			List<ShootGroupModel> groupModelList = this.shootGroupService.queryManyByCrewId(crewId);
			for (ShootGroupModel shootGroupModel : groupModelList) {
				String shootGroupName = shootGroupModel.getGroupName();
				if (!groupList.containsValue(shootGroupName)) {
					groupList.put(shootGroupModel.getGroupId(), shootGroupName);
				}
			}
			
			resultMap.put("groupList", groupList);
			resultMap.put("majorRoleList", majorRoleList);
		} catch (Exception e) {
			message = "未知异常，场景表数据加载失败!";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 加载场景表数据
	 * 
	 * @param request
	 * @param page 分页参数对象
	 * @param filter 查询过滤条件对象
	 * @param viewRoleIds
	 *            主要演员ID，多个数据已逗号隔开
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/loadViewList")
	public @ResponseBody Map<String, Object> loadViewTable(HttpServletRequest request, Page page, ViewFilter filter, String viewRoleIds){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		String crewId = getCrewId(request);
		try {
			// 取角色Id;
			if (!StringUtils.isBlank(viewRoleIds)) {
				filter.setRoles(viewRoleIds);
			}

			List<Map<String, Object>> resultList = viewInfoService.queryViewInfoList(crewId, page, filter);
			// 返回的list中每个元素都为Map，map中都包含一个属性roleList，roleList为当前场的所有演员Id
			if (null == resultList) {
				resultList = new ArrayList<Map<String, Object>>();
			}
			page.setResultList(resultList);
			
			resultMap.put("result", page);
			message = "查询成功!";
			
			if(page.getPageNo() == 1) {
				this.sysLogService.saveSysLog(request, "场景表查询", terminal, ViewContentModel.TABLE_NAME, null, 0);
			}
		} catch (Exception e) {
			message = "出现未知错误,查询失败!";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "场景表查询失败：" + e.getMessage(), terminal, ViewContentModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);		

		return resultMap;
	}


	/**
	 * 加载统计数据
	 * 
	 * @param request
	 * @param filter 过滤条件
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/loadSummary")
	public Map<String, Object> loadSummary(HttpServletRequest request, ViewFilter filter) {
		String crewId = getCrewId(request);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(crewId)) {
				throw new IllegalArgumentException("前选择剧组!");
			}
			
			// 统计信息
			Map<String, Object> viewStatistics = viewInfoService.queryViewStatistics(crewId, filter);
			resultMap.put("viewStatistics", viewStatistics);
			message = "查询成功!";
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		}catch (Exception e) {
			message = "未知异常,查询统计数据失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}

	/**
	 * 保存拍摄地点信息
	 * 
	 * @param request
	 * @param addressStr 拍摄地点字符串
	 * @param viewIds 场景id的字符串;多个场景id之间用","进行分割
	 * @return
	 * @throws Exception
	 */
	@ResponseBody 
	@RequestMapping("/saveAddress")
	public Map<String, Object> saveAddress(HttpServletRequest request, String addressStr, String viewIds, String shootRegion) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		String crewId = getCrewId(request);

		try {
			if (StringUtils.isBlank(viewIds)) {
				throw new IllegalArgumentException("请选择要设置的场景!");
			}
			
			if (StringUtils.isBlank(addressStr)) {
				viewInfoService.setViewShootLoation("", viewIds);
			} else {
				SceneViewInfoModel addressModel = viewInfoService.addOrGetShootLocationByLocationAndCrewId(addressStr, shootRegion, crewId);
				String shootLocationId = " ";
				if (addressModel != null) {
					shootLocationId = addressModel.getId();
				}
				viewInfoService.setViewShootLoation(shootLocationId, viewIds);
			}
			
			message = "保存成功!";
		} catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常,保存失败!";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);

		return resultMap;
	}

	/**
	 * 场景表导出(支持同时导出剧本内容)
	 * 
	 * @param request
	 * @param filter
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/exportExcel")
	public Map<String, Object> exportExcel(HttpServletRequest request, ViewFilter filter) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		String downloadPath = "";
		String fileName = "";
		try {
			String crewId = getCrewId(request);
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			Integer crewType = crewInfo.getCrewType();
			String crewName = crewInfo.getCrewName();
			List<String> viewIdList = new ArrayList<String>();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

			// 获取模板文件地址配置
			Properties property = PropertiesUitls.fetchProperties("/config.properties");
			String srcfilePath = "";
			
			//判断是否导出场景
			if (crewType == CrewType.Movie.getValue()) {
				srcfilePath = property.getProperty("movie_viewTemplate");
			} else {
				srcfilePath = property.getProperty("tvplay_viewTemplate");
			}
			
			//生成下载文件路径
			downloadPath = property.getProperty("downloadPath") + "view_list_" + System.currentTimeMillis() + ".xls";
			File pathFile = new File(property.getProperty("downloadPath"));
			if (!pathFile.isDirectory()) {
				pathFile.mkdirs();
			}
			
			//生成下载文件名
			fileName = "《" + crewInfo.getCrewName() + "》"	+ sdf.format(new Date()) + "场景表"+".xls";
			
			//根据条件查询出所有的场景
			List<Map<String, Object>> resultViewList = viewInfoService.queryViewInfoList(crewId, null, filter);
			// 返回的list中每个元素都为Map，map中都包含一个属性roleList，roleList为当前场的所有演员Id
			if (null == resultViewList) {
				resultViewList = new ArrayList<Map<String,Object>>();
			}
			
			//查询出所有的气氛数据
			List<AtmosphereInfoModel> atmo = atmosphereService.queryAllByCrewId(crewId);
			Map<String, Object> atmoMap = new HashMap<String, Object>();
			//添加气氛数据
			for (AtmosphereInfoModel atmosphere : atmo) {
				atmoMap.put(atmosphere.getAtmosphereId(), atmosphere.getAtmosphereName());
			}
			
			// 内外景
			Map<String, Object> siteMap = new HashMap<String, Object>();
			siteMap.put("1", "内景");
			siteMap.put("2", "外景");
			siteMap.put("3", "内外景");
			
			// 拍摄状态
			Map<String, Object> shootStatusMap = new HashMap<String, Object>();
			shootStatusMap.put("0", "未完成");
			shootStatusMap.put("1", "部分完成");
			shootStatusMap.put("2", "完成");
			shootStatusMap.put("3", "删戏");
			
			//查询所有场次的主要演员并去掉重复数据
			List<ViewRoleAndActorModel> roleSignList = viewInfoService.queryViewRoleSign(crewId);
			
			//最多只读取230的角色信息
			if (roleSignList.size() > 240) {
				List<ViewRoleAndActorModel> newRoleSignList = new ArrayList<ViewRoleAndActorModel>();
				newRoleSignList.addAll(roleSignList.subList(0, 229));
				roleSignList = newRoleSignList;
			}
			
			// 遍历查询的场景列表
			for (Map<String, Object> resultMapList : resultViewList) {
				
				//取出场景id
				if (null != resultMapList.get("viewId") ) {
					viewIdList.add((String)resultMapList.get("viewId"));
				}
				
				//取出气氛信息
				if (null != resultMapList.get("atmosphereId")) {
					resultMapList.put("atmosphere",	atmoMap.get(resultMapList.get("atmosphereId")));
				}
				
				//取出特殊提醒
				if (null != resultMapList.get("specialRemind")) {
					resultMapList.put("specialRemind", resultMapList.get("specialRemind"));
				}
				
				//取出拍摄状态
				if (null != resultMapList.get("shootStatus")) {
					resultMapList.put("shootStatus", shootStatusMap.get(((Integer) resultMapList.get("shootStatus")).intValue() + ""));
				}
				
				//格式化拍摄日期
				if (null != resultMapList.get("shootDate")) {
					Date shotDate = (Date) resultMapList.get("shootDate");
					String shootDateStr = sdf2.format(shotDate);
					resultMapList.put("shootDate", shootDateStr);
				}
				
				// 场景下的角色
				List<Map<String, Object>> roleList = (List<Map<String, Object>>) resultMapList.get("roleList");
				List<ViewRoleModel> newRoleList = new ArrayList<ViewRoleModel>();
				// 循环所有角色
				for (int i = roleSignList.size() - 1; i >= 0; i--) {
					ViewRoleModel role = roleSignList.get(i);
					boolean hasRoleFlag = false; // 标识当前场景的演员在所有主要演员中是否存在
					for (Map<String, Object> roleMap : roleList) {
						if (roleMap.get("viewRoleId").equals(role.getViewRoleId())) {
							if (StringUtils.isBlank(role.getShortName())) {
								role.setShortName("√");
							}
							newRoleList.add(role);
							hasRoleFlag = true;
							break;
						}
					}
					
					// 如果不存在就添加一个空的对象，保证在表格中显示列正确
					if (!hasRoleFlag) {
						newRoleList.add(new ViewRoleModel());
					}
				}
				//倒叙排列角色信息
				Collections.reverse(newRoleList);
				resultMapList.put("roleList", newRoleList);
			}
			
			Map<String, Object> exportResultMap = new HashMap<String, Object>();
			exportResultMap.put("resultList", resultViewList);
			exportResultMap.put("roleSignList", roleSignList);
			
			viewInfoService.exportViewToExcelTemplate(srcfilePath, exportResultMap,	downloadPath);
		
			resultMap.put("downloadPath", downloadPath);
			resultMap.put("fileName", fileName);
			
			message = "导出成功!";
			sysLogService.saveSysLog(request, "场景表导出", Constants.TERMINAL_PC, ViewInfoModel.TABLE_NAME, null, 5);
		} catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常,导出失败!";
			success = false;
			
			logger.error(message, e);
			sysLogService.saveSysLog(request, "场景表导出失败：" + e.getMessage(), Constants.TERMINAL_PC, ViewInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	
	/**
	 * 导出场景剧本
	 * @param request
	 * @param filter
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exportViewContent")
	public Map<String, Object> exportViewContent(HttpServletRequest request, ViewFilter filter){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss");
		
		FileOutputStream out = null;
		String downloadContentPath = "";
		try {
			String crewId = getCrewId(request);
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			Integer crewType = crewInfo.getCrewType();
			String crewName = crewInfo.getCrewName();
			List<String> viewIdList = new ArrayList<String>();

			// 获取模板文件地址配置
			Properties property = PropertiesUitls.fetchProperties("/config.properties");
			
			//根据条件查询出所有的场景
			List<Map<String, Object>> resultViewList = viewInfoService.queryViewInfoList(crewId, null, filter);
			// 返回的list中每个元素都为Map，map中都包含一个属性roleList，roleList为当前场的所有演员Id
			if (null == resultViewList) {
				resultViewList = new ArrayList<Map<String,Object>>();
			}
			
			// 遍历查询的场景列表
			for (Map<String, Object> resultMapList : resultViewList) {
				//取出场景id
				if (null != resultMapList.get("viewId") ) {
					viewIdList.add((String)resultMapList.get("viewId"));
				}
			}
			
			//根据场景id导出场景的剧本内容（判断是否选择了需要导出剧本内容）
			//获取存储根路径
			String baseDownloadPath = property.getProperty("downloadPath");
			
			//生成下载的文件名
			String contentFileName = "《" + crewName + "》剧本_" + sdf3.format(new Date());	
			//存储路径
			String storePath = baseDownloadPath + "scenario/pc/" + sdf1.format(new Date()) + "/";
			//下载文件的后缀名
			String suffix = ".doc";
			File scenarioFile = new File(storePath + contentFileName + suffix);
			
			//当前存储文件的文件夹若	不存在则创建一个新的文件夹
			if (!scenarioFile.getParentFile().isDirectory()) {
				scenarioFile.getParentFile().mkdirs();
			}
			//生成一个输出流
			out = new FileOutputStream(scenarioFile);
			
			// 新建一个文档
			XWPFDocument doc = new XWPFDocument();
			// 创建一个段落
			XWPFParagraph para = doc.createParagraph();
			XWPFRun run = null;
			
			//遍历场景id取出每一场的内容
			for (String viewId : viewIdList) {
			//根据剧组id和场景id查询出场景信息
			List<Map<String, Object>> scenarioViewList = this.viewInfoService.queryScenarioViewInfo(crewId, null, null, viewId);
			
			for (Map<String, Object> scenarioView : scenarioViewList) {
				StringBuilder singleViewScenario = new StringBuilder();
				
				int seriesNo = (Integer) scenarioView.get("seriesNo");
				String viewNo = (String) scenarioView.get("viewNo");
				String site = (String) scenarioView.get("site");
				String atmosphereName = (String) scenarioView.get("atmosphereName");
				String content = (String) scenarioView.get("content");
				String viewRoleNames = (String) scenarioView.get("viewRoleNames");
				String viewLocations = (String) scenarioView.get("viewLocations");
				
				if (crewType == CrewType.Movie.getValue()) {
					//电影剧本只显示场次
					singleViewScenario.append(viewNo);
				} else {
					singleViewScenario.append(seriesNo + "-" + viewNo);
				}
				//singleViewScenario.append(seriesNo + "-" + viewNo);
				
				if (!StringUtils.isBlank(viewLocations)) {
					singleViewScenario.append(" " + viewLocations);
				}
				if (!StringUtils.isBlank(atmosphereName)) {
					singleViewScenario.append(" " + atmosphereName);
				}
				if (!StringUtils.isBlank(atmosphereName) && !StringUtils.isBlank(site)) {
					singleViewScenario.append("/");
				}
				if (StringUtils.isBlank(atmosphereName) && !StringUtils.isBlank(site)) {
					singleViewScenario.append(" ");
				}
				if (!StringUtils.isBlank(site)) {
					singleViewScenario.append(site);
				}
				
				//设置导出的标题头
				String title = singleViewScenario.toString();
				run = para.createRun();
				run.setBold(true); // 加粗
				run.setFontSize(12);
				run.setText(title);
				run.addCarriageReturn();
				
				if (!StringUtils.isBlank(viewRoleNames)) {
					run.setText("\r\n" + "人物：" + viewRoleNames);
					run.addCarriageReturn();
				}
				
				//设置导出的正文
				run = para.createRun();
				run.setFontSize(12);
				if (!StringUtils.isBlank(content)) {
					String[] contentArr = content.split("\r\n");
					for (String singleLineC : contentArr) {
						run.setText(singleLineC);
						run.addCarriageReturn();
						}
					}
				run.addCarriageReturn();
				}
			}
			
			doc.write(out);
			downloadContentPath = storePath + contentFileName + suffix;
			
			resultMap.put("contentPath", downloadContentPath);
			message = "导出成功!";
			sysLogService.saveSysLog(request, "导出场景剧本", Constants.TERMINAL_PC, ViewInfoModel.TABLE_NAME, null, 5);
		} catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常,导出失败!";
			success = false;
			
			logger.error(message, e);
			sysLogService.saveSysLog(request, "导出场景剧本失败：" + e.getMessage(), Constants.TERMINAL_PC, ViewInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					message = "未知异常,剧本导出失败!";
					success = false;
					
					logger.error(message, e);
					sysLogService.saveSysLog(request, "导出场景剧本失败：" + e.getMessage(), Constants.TERMINAL_PC, ViewInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
				}
			}
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}

	/**
	 * 加载高级查询数据
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/loadAdvanceSerachData")
	public Map<String, Object> loadAdvanceSerachData(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String crewId = getCrewId(request);
		boolean success = true;
		String message = "";
		
		try {
			ViewFilterDto viewFilterDto = this.viewInfoService.genFilterDtoByCrewId(crewId, false);
			resultMap.put("viewFilterDto", viewFilterDto);

			success = true;
			message = "查询成功";
//			this.sysLogService.saveSysLog(request, "查询高级查询条件信息", terminal, "", "", 0);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询高级查询条件失败";
			
			logger.error(message, e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}

	/**暂时没有使用
	 * 查询主要演员
	 * 
	 * @param request
	 * @author subin
	 */
	@Deprecated
	@RequestMapping("/retrieveRole")
	public @ResponseBody List<Map<String, Object>> retrieveRole(HttpServletRequest request) {

		String crewId = super.getCrewId(request);

		return this.viewRoleService.queryRoleMapByCrewIdAndRoleType(crewId,	ViewRoleType.MajorActor.getValue());
	}

	/**暂时没有使用
	 * 查询 场景查询需要的上下文
	 * 
	 * @param request
	 * @author subin
	 */
	@Deprecated
	@RequestMapping("/retrieveQueryContext")
	public @ResponseBody Map<String, Object> retrieveQueryContext(HttpServletRequest request, Boolean includeNotExists) {

		Map<String, Object> result = new HashMap<String, Object>();
		String crewId = this.getCrewId(request);

		try {

			result = viewInfoService.getQueryContext(crewId, includeNotExists);

		} catch (Exception e) {
			logger.error("未知异常，查询高级查询条件失败", e);
		}

		return result;
	}

	/**
	 * 批量修改场景
	 * @param request
	 * @param viewIds 场景id字符串 多个场景id以字符串分割
	 * @param batchUpdateViewDto 存贮数据的DTO
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/batchUpdateView")
	public Map<String, Object> updateManyScenario(HttpServletRequest request, String viewIds, BatchUpdateViewDto batchUpdateViewDto) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);

		boolean success = true;
		String message = "";
		try {
			
			if (batchUpdateViewDto != null) {
				//校验填写的主场景不能为空
				if (batchUpdateViewDto.isCgLvlOneLocation()) {
					if (StringUtils.isBlank(batchUpdateViewDto.getLvlOneLocation())) {
						throw new IllegalArgumentException("您勾选主场景为必选项，请填写主场景内容！");
					}
				}
			}else {
				throw new IllegalArgumentException("请选择需要修改的内容！");
			}
			
			String crewId = this.getCrewId(request);

			//设置手动保存
			batchUpdateViewDto.setIsManualSave(true);
			this.viewInfoService.updateManyScenario(crewId, userInfo, viewIds, batchUpdateViewDto);

			message = "操作成功!";
			logger.info("场景批量修改成功！");
			
			this.sysLogService.saveSysLog(request, "批量修改场景(" + viewIds.split(",").length + ")", terminal, ViewInfoModel.TABLE_NAME, viewIds, 2);
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，更新失败";

			this.logger.error("未知异常，更新失败。", e);
			this.sysLogService.saveSysLog(request, "批量修改场景(" + viewIds.split(",").length + ")失败：" + e.getMessage(), terminal, ViewInfoModel.TABLE_NAME, viewIds, SysLogOperType.ERROR.getValue());
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 导入场景表
	 * @param request
	 * @param file
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/importViewInfo")
	public Map<String, Object> importViewInfo(HttpServletRequest request, MultipartFile file,boolean isCover) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			Map<String, String> VIEWINFO_MAP = new LinkedHashMap<String, String>();//需要导出的联系人字段
	    	VIEWINFO_MAP.put("集", "seriesNo");
	    	VIEWINFO_MAP.put("场",  "viewNo");
	    	VIEWINFO_MAP.put("特殊提醒",  "specialRemind");
	    	VIEWINFO_MAP.put("气氛",  "atmosphere");
	    	VIEWINFO_MAP.put("内外景",  "site");
	    	VIEWINFO_MAP.put("拍摄地点",  "shootLocation");
	    	VIEWINFO_MAP.put("主场景",  "firstLocation");
	    	VIEWINFO_MAP.put("次场景",  "secondLocation");
	    	VIEWINFO_MAP.put("三级场景",  "thirdLocation");
	    	VIEWINFO_MAP.put("主要内容",  "mainContent");
	    	VIEWINFO_MAP.put("页数",  "pageCount");
	    	VIEWINFO_MAP.put("特约演员",  "guestRoleNameList");
	    	VIEWINFO_MAP.put("群众演员",  "massRoleNameList");
	    	VIEWINFO_MAP.put("服装",  "clothes");
	    	VIEWINFO_MAP.put("化妆",  "makeups");
	    	VIEWINFO_MAP.put("道具",  "props");
	    	VIEWINFO_MAP.put("特殊道具",  "specialProps");
	    	VIEWINFO_MAP.put("备注",  "remark");
	    	
	    	VIEWINFO_MAP.put("商植",  "commercialImplants");
	    	VIEWINFO_MAP.put("拍摄时间",  "shootTime");
	    	VIEWINFO_MAP.put("拍摄状态",  "shootStatus");
			
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			String crewName = crewInfo.getCrewName();
			String crewId = crewInfo.getCrewId();
			
			UserInfoModel userInfo = this.getSessionUserInfo(request);

			// 上传文件到服务器
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String baseStorePath = properties.getProperty("fileupload.path");
			String modelStorePath = baseStorePath + "import/viewinfo";
			String newName = crewName + yyyyMMddFormate.format(new Date());
			Map<String, String> fileMap = FileUtils.uploadFileForExcel(request, modelStorePath, newName);
			if (fileMap == null) {
				throw new IllegalArgumentException("请选择文件");
			}
			String fileRealName = fileMap.get("fileRealName");// 原文件名
			String fileStoreName = fileMap.get("fileStoreName");// 新文件名
			String storePath = fileMap.get("storePath");// 服务器存文文件路径

			/*// 对文件进行校验
			String suffix = fileRealName.substring(fileRealName.lastIndexOf("."));
			if (!".xls".equals(suffix) && !".xlsx".equals(suffix)) {
				throw new IllegalArgumentException("请上传.xls或.xlsx格式的文档，其他格式的文档暂不支持。");
			}
			List<ScenarioViewDto> scenarioViewDtoList = getSecnarioViewDtoList(
					fileStoreName, storePath, suffix);

			if (scenarioViewDtoList == null || scenarioViewDtoList.size() == 0) {
				throw new IllegalArgumentException("为未获取到任何场景信息，请检查文件后重试");
			}*/
			
			Map<String, Object> viewInfoMap = ExcelUtils.readViewInfo(storePath + fileStoreName);
			
			boolean isMovie = false;
			if (crewInfo.getCrewType() == CrewType.InternetMovie.getValue() || crewInfo.getCrewType() == CrewType.Movie.getValue()) {
				isMovie = true;
			}
			
			//整理场景表信息
			List<ScenarioViewDto> scenarioViewDtoList = arrangementData(viewInfoMap,VIEWINFO_MAP, isMovie);
			
			if(scenarioViewDtoList!=null&&scenarioViewDtoList.size()>0){
				// 对比文件中的数据和数据库中的数据
				Map<String, Object> viewDataMap = this.scenarioService.compareViewWithDataInDB(crewId, scenarioViewDtoList);

				List<ScenarioViewDto> autoReplaceData = (List<ScenarioViewDto>) viewDataMap.get("autoReplaceData"); // 自动“替换”的数据
				List<ScenarioViewDto> autoSaveData = (List<ScenarioViewDto>) viewDataMap.get("autoSaveData"); // 自动保存的数据
				List<ScenarioViewDto> skipOrReplaceData = (List<ScenarioViewDto>) viewDataMap.get("skipOrReplaceData"); // 供用户选择“跳过”或“替换”的数据
				// List<ViewInfoModel> keepOrNotData = (List<ViewInfoModel>)
				// viewDataMap.get("keepOrNotData"); //供用户选择“保留”或“不保留”的数据
				// 处理自动“替换”的数据
				this.viewInfoService.updateManyBySceDto(autoReplaceData, true, crewId, userInfo);
				
				// 自动保存的数据
				this.viewInfoService.addManyBySceViewDto(autoSaveData, true, crewId, userInfo);
				
				//是否自动替换数据
				if(isCover){
					this.viewInfoService.updateManyBySceDto(skipOrReplaceData, true, crewId, userInfo);
				}
				viewTempService.deleteManyByCrewId(crewId, 0, "");
				/*
				 //暂不跳出对比页
				if ((skipOrReplaceData != null && skipOrReplaceData.size() > 0)) {
					this.viewTempService.addSkipOrReplaceData(skipOrReplaceData, crewId);
				}*/	
			}
			this.sysLogService.saveSysLog(request, "场景表导入", terminal, ViewContentModel.TABLE_NAME, null, 4);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			logger.error("未知异常", e);
			
			success = false;
			message = "未知异常";
			this.sysLogService.saveSysLog(request, "场景表导入失败：" + e.getMessage(), terminal, ViewContentModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	
	/**
	 * 整理场景表数据
	 * 
	 * @param viewInfoMap
	 * @return
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private List<ScenarioViewDto> arrangementData(Map<String, Object> viewInfoMap,Map<String, String> propertyNameMap, boolean isMovie) throws IllegalArgumentException, IllegalAccessException{
		if (isMovie) {
			propertyNameMap.remove("集");
		}
		
		List<ScenarioViewDto> scenarioViewDtoList = new ArrayList<ScenarioViewDto>();
		
		Set<String> mapKey =propertyNameMap.keySet();//列名集合 不包含主演名称
		
		Set<String> keys = viewInfoMap.keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String key = it.next();
			List<ArrayList<String>> list = (List<ArrayList<String>>) viewInfoMap.get(key);
			
			List<String> titles = list.get(0);
			list.remove(0);
			List<String> coloumnNameList = list.get(0);
			list.remove(0);
			
			Set<String> columnSet = propertyNameMap.keySet(); 
			Iterator<String> it1 = columnSet.iterator();
	//校验  标题  和列名
			boolean hasTitle = true;
			boolean columnNameIsTrue = true;
			while(it1.hasNext()){
				String realColumnName = it1.next();
				if(hasTitle){
					for(String str:titles){
						if(StringUtils.isNotBlank(str)){
							if(str.equals(realColumnName)){
								hasTitle = false;
							}
						}
					}
				}
				if(columnNameIsTrue){
					boolean flag = false;
					for(String column:coloumnNameList){
						if(StringUtils.isNotBlank(column)){
							if(column.equals(realColumnName)){
								flag = true;
								break;
							}
						}
					}
					columnNameIsTrue = flag;
				}
			}
			
			if(!hasTitle){
				throw new IllegalArgumentException("请添加标题");
			}
			if(!columnNameIsTrue){
				throw new IllegalArgumentException("表格列名有误");
			}
//校验  标题  和列名	
			
			
			
			
			
			
			
			
			//将标题和主要演员名称分开存放
			List<String>  newTitleList = new ArrayList<String>();
			for(String title :coloumnNameList){
				if(StringUtils.isNotBlank(title)){
					newTitleList.add(title);
				}
			}
			
			Set<String> seriesNoAndViewNo = new HashSet<String>();
			int rowNumber = 2;
			for(List<String> valueList :list){//数据集合
				rowNumber ++;
				if(valueList==null || valueList.size()< 2){
					continue;
				}
				
				String seriesNo = "";//集
				String viewNo = "";//场
				if (isMovie) {
					seriesNo = "1";//集
					viewNo = valueList.get(0).toUpperCase();//场
				} else {
					seriesNo = valueList.get(0);//集
					viewNo = valueList.get(1).toUpperCase();//场
				}
				
				
				
				if(!isMovie && (StringUtils.isBlank(seriesNo)||StringUtils.isBlank(viewNo))){
					throw new IllegalArgumentException("第"+rowNumber+"行，集场信息不能为空");
				}
				if(isMovie && StringUtils.isBlank(viewNo)){
					throw new IllegalArgumentException("第"+rowNumber+"行，场次不能为空");
				}
				int beforeSize = seriesNoAndViewNo.size();
				seriesNoAndViewNo.add(seriesNo+"-"+viewNo);
				int afterSize = seriesNoAndViewNo.size();
				
				if(!isMovie && beforeSize == afterSize){
					throw new IllegalArgumentException("第"+rowNumber+"行，集场号重复(" + seriesNo + "-" + viewNo + ")");
				}
				if(isMovie && beforeSize == afterSize){
					throw new IllegalArgumentException("第"+rowNumber+"行，场次重复(场次：" + viewNo + ")");
				}
				
				
				
				ScenarioViewDto scenarioViewDto = new ScenarioViewDto();
				List<String> mainActorNameList = new ArrayList<String>();
				for(int i = 0;i<newTitleList.size();i++){//标题集合
					String value = valueList.get(i);
					String propertyName = newTitleList.get(i);
					if(mapKey.contains(propertyName)){//如果非主演名称
						Object vObject = new Object();
						if("特约演员".equals(propertyName)||"群众演员".equals(propertyName)){
							String[] values = value.replaceAll("，", ",").replaceAll("、", ",").replaceAll(" ", ",").split(",");
							if(values!=null&&values.length!=0){
								vObject = Arrays.asList(values);
							}else{
								vObject = new ArrayList<String>();
							}
						}else{
							vObject = value;
						}
						reflectToSetValue(scenarioViewDto,propertyNameMap.get(propertyName),vObject);
					}else{
						if(StringUtils.isNotBlank(value)){
							mainActorNameList.add(propertyName);
						}
					}
							
				}
				scenarioViewDto.setMajorRoleNameList(mainActorNameList);

				if (isMovie) {
					scenarioViewDto.setSeriesNo(1);
				}
				scenarioViewDtoList.add(scenarioViewDto);
			}
		}
		
		return scenarioViewDtoList;
	}
	
	
	private void reflectToSetValue(Object obj,String propertyName,Object propertyValue) throws IllegalArgumentException, IllegalAccessException{
		Class<? extends Object> userCla = (Class<? extends Object>) obj.getClass();
		Field[] fields = userCla.getDeclaredFields();
		for(Field field :fields){
			String name = field.getName();
			if(name.equals(propertyName)){
				field.setAccessible(true);//设置为可修改
				String type = field.getType().toString();// 得到此属性的类型
				Object value = new Object();
				if(type.endsWith("String")) {
					value = propertyValue!=null?propertyValue.toString():"";
	            }else if(type.endsWith("int") || type.endsWith("Integer")){
	            	if(propertyValue!=null){
	            		String valueStr = propertyValue.toString();
	            		if(StringUtils.isNotBlank(valueStr)){
	            			Object ob = getValue(name,valueStr);
	            			value = Integer.parseInt(ob.toString());
	            		}else{
	            			value = 0;
	            		}
	            	}else{
	            		value = 0;
	            	}
	            }else if(type.endsWith("double") || type.endsWith("Double")){
	            	if(propertyValue!=null){
	            		String valueStr = propertyValue.toString();
	            		if(StringUtils.isNotBlank(valueStr)){
	            			value = Double.parseDouble(valueStr);
	            		}else{
	            			value = 0.00;
	            		}
	            	}else{
	            		value = 0.00;
	            	}
	            }else{
	            	value = propertyValue;
	            }
				field.set(obj,value); // 给属性设值
			}
			
		}
		
	}
	
	private static Object getValue(String key, Object value) {
		if ("viewType".equals(key)) {
			value = ViewType.nameOf(value.toString()).getValue();
		} else if ("season".equals(key)) {
			value = SeasonType.nameOf(value.toString()).getValue();
		} else if ("shootStatus".equals(key)) {
			value = ShootStatus.nameOf(value.toString()).getValue();
		}
		return value;
	}
	
    /**
     * 跳转到批量修改场景页面
     * @param request
     * @return
     */
    @RequestMapping("/toBatchUpdateViewPage")
    public ModelAndView toBatchUpdateViewPage(HttpServletRequest request) {
    	ModelAndView view = new ModelAndView("/view/batchViewDetailInfo");
    	
    	return view;
    }
    
    /**
     * 根据场景id，获取当前场景的所有的主场景、次场景、三级场景
     * @param request
     * @param viewIds 场景id的字符串
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryLocationListByVieId")
    public Map<String, Object> queryLocationListByVieId(HttpServletRequest request, String viewIds){
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message = "";
    	boolean success = true;
    	
    	/*List<Object> resultList = new ArrayList<Object>();*/
    	try {
			if (StringUtils.isBlank(viewIds)) {
				throw new IllegalArgumentException("请选择要修改的场景！");
			}
			
			/*//根据场景id查询出所有的主场景、次场景、三级场景
			String[] viewArr = viewIds.split(",");
			for (String viewId : viewArr) {
				Map<String, Object> locationResultMap = new HashMap<String, Object>();
				String mainLocationStr = ""; //主场景
				String secondLocationStr = ""; //次场景
				String thirdLocationStr = ""; //三级场景
				List<Map<String,Object>> locationMapList = this.viewInfoService.queryLocationByViewId(viewId);
				for (Map<String, Object> locationMap : locationMapList) {
					//取出场景类型
					int locationType = (Integer) locationMap.get("locationType");
					String locationStr = (String) locationMap.get("location");
					if (locationType == 1) { //主场景
						mainLocationStr = locationStr;
					}else if (locationType == 2) { //次场景
						secondLocationStr = locationStr;
					}else if (locationType == 3) { //三级场景
						thirdLocationStr = locationStr;
					}
				}
				
				locationResultMap.put("mainLocationStr", mainLocationStr);
				locationResultMap.put("secondLocationStr", secondLocationStr);
				locationResultMap.put("thirdLocationStr", thirdLocationStr);
				resultList.add(locationResultMap);
			}
			
			//提取主场景相同的场景字符串
			String sameLocationStr = this.viewInfoService.queryMaxSameLocationStr(viewIds);*/
			
			//查询场景列表数据
			List<List<SameViewLocationDto>> list = this.viewInfoService.querySameLocationList(viewIds);
			
			resultMap.put("sameLocationList", list);
			message = "查询成功";
		}catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
		} catch (Exception e) {
			message = "未知异常，查询失败！";
			success = false;
			
			logger.error(message, e);
		}
    	
    	resultMap.put("message", message);
    	resultMap.put("success", success);
    	return resultMap;
    }
    
    /**
     * 批量更新修改后的场景
     * @param request
     * @param viewStr
     * @return
     */
    @ResponseBody
    @RequestMapping("/batchUpdateViewLocation")
    public Map<String, Object> batchUpdateViewLocation(HttpServletRequest request, String viewStr){
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	
    	try {
			if (StringUtils.isBlank(viewStr)) {
				throw new IllegalArgumentException("请填写需要更新的内容！");
			}
			String crewId = getCrewId(request);
			
			//分割字符串取出每一个场景的内容
			String[] viewTrLocation = viewStr.split("&");
			for (String trLocation : viewTrLocation) {
				//分割每一行的数据，取出对应的场景和场景id
				String[] tdLocations = trLocation.split("-");
				//主场景】
				String mainLocation = tdLocations[0];
				//次场景
				String secondLocation = tdLocations[1];
				//三级场景
				String thirdLocation = tdLocations[2];
				//场景id
				String viewIds = tdLocations[3];
				
				//由于场景的id字符串可能包含多个场景，所以，进行判断，若果包含‘，’表示是多个场景，则需要拆分后进行操作
//				String[] viewIdArr = viewIds.split(",");
				
				//调用service方法更新场景的信息
				this.viewInfoService.savebatchViewLocation(crewId, mainLocation, secondLocation, thirdLocation, viewIds);
			}
			
			message = "更新成功！";
			logger.info("统一场景，更新成功！");
			
			this.sysLogService.saveSysLog(request, "统一场景", terminal, ViewLocationMapModel.TABLE_NAME, null, 2);
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，更新失败";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "统一场景失败：" + e.getMessage(), terminal, ViewLocationMapModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
    	
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
    }
    
    /**
     * 校验道具是否重复
     */
    private void checkPropsRepeat(String specialProps, String commonProps, String crewId) {
    	//利用list进行去重
    	List<String> filterList = new ArrayList<String>();
    	
    	//取出所有的普通道具
    	if (StringUtils.isNotBlank(commonProps)) {
			String[] commonArr = commonProps.split(",");
			for (String commonProp : commonArr) {
				
				if (!filterList.contains(commonProp)) {
					filterList.add(commonProp);
				}
			}
		}
    	
    	//利用list判断保存的普通道具和特殊道具是否有重复
    	if (StringUtils.isNotBlank(specialProps)) {
			String[] specialArr = specialProps.split(",");
			for (String specialProp : specialArr) {
				
				if (filterList.contains(specialProp)) {
					throw new IllegalArgumentException(specialProp + " 道具不能同时是普通道具和特殊道具");
				}
			}
		}
    	
    }
    
    /**
     * 拆分场景
     * 把原场景的所有信息带到新场景中
     * 除了场次、剧本内容、拍摄状态
     * @return
     */
    @ResponseBody
    @RequestMapping("/divideViewInfo")
    public Map<String, Object> divideViewInfo(HttpServletRequest request, Integer seriesNo, String viewNo, String content) {
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message = "";
    	boolean success = true;
    	
    	try {
    		String crewId = this.getCrewId(request);
    		UserInfoModel userInfo = this.getSessionUserInfo(request);
    		
    		
    		//遍历当前集下的所有场次
    		List<String> existViewNoList = new ArrayList<String>();
    		List<Map<String, Object>> viewNoList = this.viewInfoService.queryViewNoByCrewIdAndSeriesNo(crewId, seriesNo + "");
    		for (Map<String, Object> viewNoMap : viewNoList) {
    			existViewNoList.add((String) viewNoMap.get("viewNo"));
    		}
    		
    		String newViewNo = this.genNextViewNo(crewId, seriesNo, viewNo, existViewNoList);
    		
    		ViewInfoModel viewInfo = this.viewInfoService.queryOneByCrewIdAndSeriaViewNo(crewId, seriesNo, viewNo);
    		ViewContentModel originalContent = this.viewContentService.queryByViewId(viewInfo.getViewId());
    		ScenarioFormatModel formatInfo = this.scenarioFormatService.queryByCrewId(crewId);
    		int wordCount = 35;
    		int lineCount = 40;
    		if (formatInfo != null) {
    			wordCount = formatInfo.getWordCount();
    			lineCount = formatInfo.getLineCount();
    		}
    		
			//根据场景信息的model类生成场景DTO类
			ViewInfoDto viewInfoDto = this.genViewInfoDtoByViewInfo(viewInfo);
			viewInfoDto.setContent(content);
			viewInfoDto.setTitle(originalContent.getTitle().replace(viewNo, newViewNo));
			viewInfoDto.setViewNo(newViewNo);
			
			//计算页数
			String pageContent = content;
			if (formatInfo.getPageIncludeTitle()) {
				pageContent = originalContent.getTitle().replace(viewNo, newViewNo) + this.lineSeparator + pageContent;
			}
			
			int viewLineCount = this.scenarioService.calculateLineCount(pageContent, wordCount, true);
			double pageCount=com.xiaotu.makeplays.utils.StringUtils.div(viewLineCount, lineCount, 1);
			viewInfoDto.setPageCount(pageCount);
			
			this.viewInfoService.addByViewInfoDto(viewInfoDto, userInfo, crewId);
    		
			resultMap.put("viewNo", newViewNo);
    	} catch (Exception e) {
			message = "未知异常，获取新的集场号失败";
			success = false;
			
			logger.error(message, e);
		}
    	
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
    }
    
    /**
     * 根据集场号生成新的集场号
     * 例如剧组中所有场次有：1-1 、  1-1a 、 2-1  、 2-1扉页  、 2-1abc 、 2-2 、 3-1 、 3-1a 、 3-1b
     * 如果输入参数为1-1，则返回1-1b，如果输入参数为1-1a，则反馈1-1b
     * 如果输入参数为2-1，则返回2-1a
     * 如果输入参数为3-1，则返回3-1c
     * @param crewId
     * @param seriesNo
     * @param viewNo
     * @return
     */
    private String genNextViewNo(String crewId, Integer seriesNo, String viewNo, List<String> existsViewNoList) {
    	List<String> seriesList = new ArrayList<String>();
		seriesList.add("A");
		seriesList.add("B");
		seriesList.add("C");
		seriesList.add("D");
		seriesList.add("E");
		seriesList.add("F");
		seriesList.add("G");
		seriesList.add("H");
		seriesList.add("I");
		seriesList.add("J");
		seriesList.add("K");
		seriesList.add("L");
		seriesList.add("M");
		seriesList.add("N");
		seriesList.add("O");
		seriesList.add("P");
		seriesList.add("Q");
		seriesList.add("R");
		seriesList.add("S");
		seriesList.add("T");
		seriesList.add("U");
		seriesList.add("V");
		seriesList.add("W");
		seriesList.add("X");
		seriesList.add("Y");
		seriesList.add("Z");

		String prefix = viewNo.substring(0, viewNo.length() - 1);
		String suffix = viewNo.substring(viewNo.length() - 1, viewNo.length());
		
		int index = -1;
		if (!StringUtils.isBlank(suffix)) {
			index = seriesList.indexOf(suffix);
		}
		
		if (index == 25) {
			index = -1;
		}
		
		if (index == -1) {
			prefix = viewNo;
		}
		String newViewNo = prefix + seriesList.get(index + 1);
		if (!existsViewNoList.contains(newViewNo)) {
			return newViewNo;
		}
		return genNextViewNo(crewId, seriesNo, newViewNo, existsViewNoList);
    }
    
    /**
     * 获取字符串前面数字
     * 如果字符串不是以数字开头，则返回-1
     * @param str
     * @return
     */
    public int genPrefixNumber(String str) {
    	int result = -1;
    	
    	String[] array = str.split("");
		String numberviewNo = "";
		for (int i = 1; i < array.length; i++) {
			if (RegexUtils.regexFind("\\d", array[i])) {
				numberviewNo += array[i];
			} else {
				break;
			}
		}
		if (!StringUtils.isBlank(numberviewNo)) {
			result = Integer.parseInt(numberviewNo);
		}
		
		return result;
    }
    
    /**
     * 跳转到场景表打印预览页面
     * @param request
     * @param filter 过滤条件
     * @param hideColumn 隐藏列
     * @return
     */
    @RequestMapping("/toViewPrintPreviewPage")
    public ModelAndView toViewPrintPreviewPage(HttpServletRequest request, ModelMap modelMap) {
    	ModelAndView view = new ModelAndView("/view/viewPrintPreview");
    	Enumeration<String> nameEnumeration = request.getParameterNames();
		if (nameEnumeration != null)
		{
			while (nameEnumeration.hasMoreElements())
			{
				String key = nameEnumeration.nextElement();
				modelMap.addAttribute(key, request.getParameter(key));
			}
		}
    	return view;
    }
    
    /**
     * 同步场景表中的特殊提醒
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/ansycViewSpecialRemind")
    public Map<String, Object> ansycViewSpecialRemind(HttpServletRequest request){
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message = "";
    	boolean success = true;
    	
    	try {
			
    		this.viewInfoService.ansycViewSpecialRemind();
    		message = "同步成功";
		} catch (Exception e) {
			message ="未知异常，同步失败";
			success = false;
			
			logger.error(message, e);
		}
    	
    	resultMap.put("message", message);
    	resultMap.put("success", success);
    	return resultMap;
    }
    
    /**
     * 验证拍摄地地域是否与原来不一致
     * @param request
     * @param shootLocation 拍摄地
     * @param shootRegion 地域
     * @return
     */
    @ResponseBody
    @RequestMapping("/validateShootLocationRegion")
    public Map<String, Object> validateShootLocationRegion(HttpServletRequest request, String shootLocation, String shootRegion){
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message = "";
    	boolean success = true;
    	
    	try {
    		if(StringUtil.isBlank(shootLocation) && StringUtil.isNotBlank(shootRegion)) {
    			throw new IllegalArgumentException("拍摄地为空，不能设置地域");
    		}
    		String crewId = this.getCrewId(request);
    		if(StringUtil.isNotBlank(shootLocation)) {
    			SceneViewInfoModel sceneViewInfo = this.sceneViewInfoService.queryShootAddressByAddress(shootLocation, crewId);
    			if(sceneViewInfo != null) {        			
        			String oldShootRegion = sceneViewInfo.getVCity();        			
        			if(StringUtil.isNotBlank(oldShootRegion)) {
        				if(!shootRegion.equals(oldShootRegion)) {
        					success = false;
        		        	resultMap.put("oldShootRegion", oldShootRegion);
        				}
        			}
    			}
    		}
        	resultMap.put("shootLocation", shootLocation);
        	resultMap.put("shootRegion", shootRegion);
		} catch (IllegalArgumentException ie) {
			success = false;			
			logger.error(ie.getMessage(), ie);
		} catch (Exception e) {
			message ="未知异常，验证拍摄地、地域是否与原来不一致失败";
			success = false;			
			logger.error(message, e);
		}
    	resultMap.put("message", message);
    	resultMap.put("success", success);
    	return resultMap;
    }
}
