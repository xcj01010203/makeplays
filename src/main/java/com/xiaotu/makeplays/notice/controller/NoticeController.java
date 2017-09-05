
package com.xiaotu.makeplays.notice.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.model.constants.CrewType;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.cutview.service.CutViewInfoService;
import com.xiaotu.makeplays.goods.model.constants.GoodsType;
import com.xiaotu.makeplays.goods.service.GoodsInfoService;
import com.xiaotu.makeplays.locationsearch.model.SceneViewInfoModel;
import com.xiaotu.makeplays.locationsearch.service.SceneViewInfoService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.mobile.push.umeng.service.ios.IOSOriginalPushService;
import com.xiaotu.makeplays.mobile.push.umeng.service.ios.UmengIOSPushService;
import com.xiaotu.makeplays.mobile.server.notice.dto.LocationViewDto;
import com.xiaotu.makeplays.mobile.server.notice.dto.NoticeRoleTimeDto;
import com.xiaotu.makeplays.mobile.server.notice.dto.NoticeTimeDto;
import com.xiaotu.makeplays.mobile.server.notice.dto.PictureDto;
import com.xiaotu.makeplays.mobile.server.notice.dto.ViewInfoDto;
import com.xiaotu.makeplays.notice.model.ConvertAddressModel;
import com.xiaotu.makeplays.notice.model.NoticeInfoModel;
import com.xiaotu.makeplays.notice.model.NoticePictureModel;
import com.xiaotu.makeplays.notice.model.NoticePushFedBackModel;
import com.xiaotu.makeplays.notice.model.NoticeRoleTimeModel;
import com.xiaotu.makeplays.notice.model.NoticeTimeModel;
import com.xiaotu.makeplays.notice.model.ViewNoticeMapModel;
import com.xiaotu.makeplays.notice.model.clip.TmpCancelViewInfoModel;
import com.xiaotu.makeplays.notice.model.constants.NoticeCanceledStatus;
import com.xiaotu.makeplays.notice.service.ClipService;
import com.xiaotu.makeplays.notice.service.ConvertAddressService;
import com.xiaotu.makeplays.notice.service.NoticeService;
import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;
import com.xiaotu.makeplays.roleactor.model.constants.ViewRoleType;
import com.xiaotu.makeplays.roleactor.service.ViewRoleService;
import com.xiaotu.makeplays.shoot.model.ScheduleViewMapModel;
import com.xiaotu.makeplays.shoot.model.ShootGroupModel;
import com.xiaotu.makeplays.shoot.service.ShootGroupService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.service.CrewContactService;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.AuthorityConstants;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.DateUtils;
import com.xiaotu.makeplays.utils.ExcelUtils;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.HttpUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.view.controller.filter.ViewFilter;
import com.xiaotu.makeplays.view.model.AtmosphereInfoModel;
import com.xiaotu.makeplays.view.model.ViewContentModel;
import com.xiaotu.makeplays.view.model.ViewInfoModel;
import com.xiaotu.makeplays.view.model.ViewRoleAndActorModel;
import com.xiaotu.makeplays.view.service.AtmosphereService;
import com.xiaotu.makeplays.view.service.InsideAdvertService;
import com.xiaotu.makeplays.view.service.ViewInfoService;
import com.xiaotu.makeplays.weather.controller.dto.WeatherInfoDto;
import com.xiaotu.makeplays.weather.service.WeatherInfoService;

/**
 * 通告单操作
 * @author xuchangjian 2016年8月5日上午9:55:12
 */
@Controller
@RequestMapping("/notice")
public class NoticeController extends BaseController{

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private SimpleDateFormat sdf4 = new SimpleDateFormat("yyyyMMdd");
	
	Logger logger = LoggerFactory.getLogger(NoticeController.class);
	
	private final int terminal = Constants.TERMINAL_PC;
	
	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private WeatherInfoService weatherInfoService;
	
	@Autowired
	private ViewInfoService viewService;
	
	@Autowired
	private AtmosphereService atmosphereService;
	
	@Autowired
	private ShootGroupService shootGroupService;
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	@Autowired
	private InsideAdvertService insideAdvertService;
	
	@Autowired
	private CrewInfoService crewInfoService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ConvertAddressService convertAddressService;
	
	@Autowired
	private IOSOriginalPushService iOSOriginalPushService;
	
	@Autowired
	private UmengIOSPushService umengIOSPushService;
	
	@Autowired
	private ClipService clipSerivce;
	
	@Autowired
	private CrewContactService crewContactService;
	
	@Autowired
	private ViewRoleService viewRoleService;
	
	@Autowired
	private SceneViewInfoService sceneViewInfoService;
	
	@Autowired
	private GoodsInfoService goodsInfoService;
	
	@Autowired
	private CutViewInfoService cutViewService;
	
	/**
	 * 跳转到新增通告单页面
	 * @return
	 */
	@Deprecated
	@RequestMapping("/toNoticeAddPage")
	public ModelAndView toNoticeAddPage(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("/notice/noticeAdd");
		return mv;
	}
	
	/**
	 * 跳转到通告单分享页面
	 * @return
	 */
	@RequestMapping("/appIndex/toNoticeSharePage")
	public ModelAndView toNoticeSharePage(String userId, String crewId, String noticeId) {
		ModelAndView mv = new ModelAndView("/notice/noticeShare");
		mv.addObject("crewId", crewId);
		mv.addObject("userId", userId);
		mv.addObject("noticeId", noticeId);
		return mv;
	}
	
	/**
	 * 跳转到更新通告单页面
	 * @return
	 */
	@Deprecated
	@RequestMapping("/noticeUpdate")
	public ModelAndView toNoticeUpdatePage(String noticeId) {
		ModelAndView mv = new ModelAndView("/notice/noticeUpdate");
		
		NoticeInfoModel noticeInfo = noticeService.getNotice(noticeId);
		
		List<ShootGroupModel> groupList = this.shootGroupService.queryManyByCrewId(noticeInfo.getCrewId());
		
		mv.addObject("noticeInfo", noticeInfo);
		mv.addObject("groupList", groupList);
		return mv;
	}
	
	
	/**
	 * 跳转到通告单场景列表界面
	 * @param noticeId 通告单id
	 * @return
	 */
	@RequestMapping("/toNoticeViewListPage")
	public ModelAndView toNoticeViewListPage(String noticeId) {
		ModelAndView mv = new ModelAndView("/notice/noticeViewList");
		
		if (StringUtils.isNotBlank(noticeId)) {
			mv.addObject("noticeId", noticeId);
		}
		return mv;
	}
	
	/**
	 * 根据通告单的id查询单个通告单的信息
	 * @param request
	 * @param noticeId 单场通告单的id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryOneNotice")
	public Map<String, Object> queryOneNotice(HttpServletRequest request, String noticeId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String crewId = getCrewId(request);
		
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请选择要查看的通告单!");
			}
			
			Map<String, Object> noticeInfo = this.noticeService.queryOneFullInfoByNoticeId(crewId, noticeId);
			//获取主演信息
			String mainRole = (String) noticeInfo.get("mainrole");
			//获取特约演员信息
			String guestRole = (String) noticeInfo.get("guestrole");
			if (!StringUtils.isBlank(mainRole)) {
				String[] mainRoleArr = mainRole.split(",");
				
				List<String> mainRoleList = new ArrayList<String>();
				mainRole = "";
				//对主演信息进行去重
				for (String mainRoleStr : mainRoleArr) {
					if (!mainRoleList.contains(mainRoleStr)) {
						mainRoleList.add(mainRoleStr);
						mainRole += mainRoleStr + ",";
					}
				}
				if (!StringUtils.isBlank(mainRole)) {
					mainRole = mainRole.substring(0, mainRole.length() - 1);
				}
				noticeInfo.remove("mainrole");
				noticeInfo.put("mainrole", mainRole);
			}
			
			if (!StringUtils.isBlank(guestRole)) {
				String[] guestRoleArr = guestRole.split(",");
				
				List<String> guestRoleList = new ArrayList<String>();
				guestRole = "";
				//对特约演员信息进行去重
				for (String guestRoleStr : guestRoleArr) {
					if (!guestRoleList.contains(guestRoleStr)) {
						guestRoleList.add(guestRoleStr);
						guestRole += guestRoleStr + ",";
					}
				}
				if (!StringUtils.isBlank(guestRole)) {
					guestRole = guestRole.substring(0, guestRole.length() - 1);
				}
				noticeInfo.remove("guestrole");
				noticeInfo.put("guestrole", guestRole);
			}
			
			//对更新时间进行格式化后返回
			Date updateTime = (Date) noticeInfo.get("updateTime");
			String updateTimeStr = DateUtils.parse2String(updateTime, "yyyy-MM-dd HH:mm:ss");
			noticeInfo.remove("updateTime");
			noticeInfo.put("updateTime", updateTimeStr);
			
			resultMap.put("noticeInfo", noticeInfo);
			message = "查询成功";
		}catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询失败";
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message",  message);
		return resultMap;
	}
	
	/**
	 * 保存通告单,此接口会保存通告单与场景之间的关联关系
	 * @param request
	 * @param noticeInfo 通告单信息表对象
	 * @param viewIds 场景id字符串,多个场景id之间以","进行分割
	 * @param noticeDateStr 通告单的添加时间
	 * @return
	 * @throws Exception
	 */
	@ResponseBody 
	@RequestMapping("/noticeSave")
	public Map<String,Object> saveNotice(HttpServletRequest request, NoticeInfoModel noticeInfo, String viewIds, String noticeDateStr){
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		
		Map<String,Object> resultMap = new HashMap<String, Object>();
		String message ="";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(viewIds)) {
				throw new IllegalArgumentException("请选择要添加到通告单的场景!");
			}
			
			if (StringUtils.isBlank(noticeDateStr)) {
				throw new IllegalArgumentException("通告单的添加时间不能为空!");
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			noticeInfo.setNoticeDate(sdf.parse(noticeDateStr));
			if (StringUtils.isBlank(noticeInfo.getNoticeId())) {
				noticeInfo.setCanceledStatus(NoticeCanceledStatus.Uncancel.getValue());
				noticeInfo.setCrewId(crewInfo.getCrewId());
			}

			//校验通告单
			this.checkNoticeInfo(crewInfo.getCrewId(), noticeInfo.getNoticeId(), noticeInfo.getNoticeName(), noticeInfo.getGroupId(), noticeInfo.getNoticeDate());
			//验证所选场次是否已有被其他通告单使用
			List<Map<String, Object>> list = noticeService.validatorNoticeSave(viewIds, noticeInfo.getNoticeId());
			
			if (null != list && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {

					Map<String, Object> viewNoticeMap = (Map<String, Object>) list.get(i);
					String noticeName = (String) viewNoticeMap.get("noticeName");
					if (crewInfo.getCrewType() == 0 || crewInfo.getCrewType() == 3) {
						message += viewNoticeMap.get("viewNo") + "";
					}else {
						message += viewNoticeMap.get("seriesNo") + "-" + viewNoticeMap.get("viewNo") + "";
					}
					message += "场已在通告单《" + noticeName + "》中，";
				}
				message += "请先进行销场，再重新添加通告单！";

				resultMap.put("message", message);
				success = false;
				resultMap.put("success", success);
				return resultMap;
			}
			
			noticeService.saveNotice(noticeInfo, viewIds);
//			sysLogService.saveSysLog(request, "保存通告单及通告单下的场景", Constants.TERMINAL_PC, noticeInfo.TABLE_NAME + "," + ViewInfoModel.TABLE_NAME, noticeInfo.getNoticeId(),1);
			message = "保存通告单成功!";
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，保存通告单失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 校验通告单保存时的信息
	 * @param request
	 * @param noticeInfo
	 * @param noticeDateStr
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/checkNoticeSaveInfo")
	public Map<String, Object> checkNoticeSaveInfo(HttpServletRequest request, NoticeInfoModel noticeInfo, 
			String noticeDateStr){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(noticeInfo.getNoticeName())) {
				throw new IllegalArgumentException("请输入通告单名称");
			}
			
			noticeInfo.setNoticeDate(sdf.parse(noticeDateStr));
			noticeInfo.setCrewId(crewInfo.getCrewId());
			
			// 校验通告单是否符合规则
			this.checkNoticeInfo(crewInfo.getCrewId(), noticeInfo.getNoticeId(), noticeInfo.getNoticeName(), noticeInfo.getGroupId(), noticeInfo.getNoticeDate());
		}catch(IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知错误，校验失败";
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 保存通告单
	 * 该保存方法不带有添加场景到通告的中的功能
	 * @param request
	 * @param noticeInfo 用于接收传递参数的对象
	 * @param noticeDateStr 通告单新建时间
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/noticeSaveWinoutView")
	public @ResponseBody Map<String, Object> saveNoticeWithoutView(HttpServletRequest request, NoticeInfoModel noticeInfo, 
			String noticeDateStr, String cancleChanged){
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(noticeInfo.getNoticeName())) {
				throw new IllegalArgumentException("请输入通告单名称");
			}
			
			noticeInfo.setNoticeDate(sdf.parse(noticeDateStr));
			noticeInfo.setCrewId(crewInfo.getCrewId());
			//如果当前通告单的id为空时表示是新建通告单,则此时通告单状态应该设置为未销场状态
			if(StringUtils.isBlank(noticeInfo.getNoticeId())){
				noticeInfo.setCanceledStatus(NoticeCanceledStatus.Uncancel.getValue());
			}
			
			
			if (StringUtils.isBlank(noticeInfo.getNoticeId())) {
				noticeInfo.setPublished(false);
				noticeInfo.setPublishTime(null);
			}else {
				//设置为未发布
				if (StringUtils.isBlank(cancleChanged)) {
					noticeInfo.setPublished(false);
					noticeInfo.setPublishTime(null);
					//更新时间戳,只有当通告单的附加信息不为空时，才需要更新时间戳
					NoticeTimeModel timeModel = this.noticeService.queryNoticeTimeByNoticeId(noticeInfo.getNoticeId());
					if (timeModel != null ) {
						timeModel.setUpdateTime(new Date());
						this.noticeService.updateNoticeTime(timeModel);
					}
				}
			}
			
			// 校验通告单是否符合规则
			this.checkNoticeInfo(crewInfo.getCrewId(), noticeInfo.getNoticeId(), noticeInfo.getNoticeName(), noticeInfo.getGroupId(), noticeInfo.getNoticeDate());
			//校验当前的分组信息
			//checkNoticeGroup(crewInfo.getCrewId(), noticeInfo.getNoticeId(), noticeInfo.getGroupId(), sdf.parse(noticeDateStr));
			
			String noticeId = noticeService.saveNotice(noticeInfo);
			message = "保存成功!";
			
			resultMap.put("noticeId", noticeId);
			
			sysLogService.saveSysLog(request, "保存通告单信息", Constants.TERMINAL_PC, NoticeInfoModel.TABLE_NAME, noticeInfo.getNoticeName(),2);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，新增通告单失败";
			
			logger.error(message, e);
			sysLogService.saveSysLog(request, "保存通告单信息失败：" + e.getMessage(), Constants.TERMINAL_PC, NoticeInfoModel.TABLE_NAME, noticeInfo.getNoticeName(), SysLogOperType.ERROR.getValue());
		}
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 保存修改的通告单时，判断分组信息是否正确
	 * @param crewId 剧组id
	 * @param noticeId 通告单id
	 * @param groupId 分组id
	 * @param noticeDate 通告单时间
	 * @throws IllegalArgumentException
	 */
	public void checkNoticeGroup(String crewId, String noticeId, String groupId, Date noticeDate) throws IllegalArgumentException {
		
		//校验名称是否有重复
		Map<String, Object> existConditionMap = new HashMap<String, Object>();
		existConditionMap.put("crewId", crewId);
		existConditionMap.put("noticeDate", noticeDate);
		existConditionMap.put("groupId", groupId);
		List<NoticeInfoModel> existNoticeList = this.noticeService.queryManyByMutiCondition(existConditionMap, null);
		for (NoticeInfoModel noticeInfoModel : existNoticeList) {
			//取出通告单id
			 String getNoticeId = noticeInfoModel.getNoticeId();
			 if (!getNoticeId.equals(noticeId)) {
				 throw new IllegalArgumentException("已存在相同名称的通告单，请重新填写");
			}
		}
		/*
		 * 如果当天已经有对应分组的通告单，不允许重复添加
		 * 如果当天已经有分组的通告单，不能再建未分组的通告，反之亦然
		 */
	    /*Map<String, Object> otherConditionMap = new HashMap<String, Object>();
		otherConditionMap.put("crewId", crewId);
		otherConditionMap.put("noticeDate", noticeDate);
		//otherConditionMap.put("noticeId", noticeId);
		List<NoticeInfoModel> otherNoticeList = this.noticeService.queryManyByMutiCondition(otherConditionMap, null);
		
		SimpleDateFormat sdfCN = new SimpleDateFormat("yyyy年MM月dd日");
		String noticeDateStr = sdfCN.format(noticeDate);
		ShootGroupModel shootGroup = this.shootGroupService.queryOneByGroupId(groupId);
		String groupName = shootGroup.getGroupName();
		
		if (otherNoticeList != null && otherNoticeList.size() > 0) {
			for (NoticeInfoModel noticeInfo : otherNoticeList) {
				String otherGroupId = noticeInfo.getGroupId();
				//如果当天已经有对应分组的通告单，不允许重复添加
				if (otherGroupId.equals(groupId)) {
					throw new IllegalArgumentException(noticeDateStr + "已存在" + groupName + "通告单，不允许重复添加");
				}
				//如果当天已经有分组的通告单，不能再建单组的通告
				if (!otherGroupId.equals("1") && groupId.equals("1")) {
					throw new IllegalArgumentException(noticeDateStr + "已存在分组通告单，不允许添加单组通告单");
				}
				//如果当天已经有单组的通告单，不能再建分组的通告
				if (otherGroupId.equals("1") && !groupId.equals("1")) {
					throw new IllegalArgumentException(noticeDateStr + "已存在单组通告单，不允许添加分组通告单");
				}
			}
		}*/
	}
	
	/**
	 * 新增通告单时校验通告单信息
	 * @param noticeInfo	通告单信息
	 * @throws IllegalArgumentException 
	 */
	private void checkNoticeInfo(String crewId, String noticeId, String noticeName, String groupId, Date noticeDate) throws IllegalArgumentException {
		SimpleDateFormat sdfCN = new SimpleDateFormat("yyyy年MM月dd日");
		ShootGroupModel shootGroup = this.shootGroupService.queryOneByGroupId(groupId);
		String groupName = shootGroup.getGroupName();
		//校验名称是否有重复
		Map<String, Object> existConditionMap = new HashMap<String, Object>();
		existConditionMap.put("crewId", crewId);
		existConditionMap.put("noticeName", noticeName);
		existConditionMap.put("noticeId", noticeId);
		List<NoticeInfoModel> existNoticeList = this.noticeService.queryManyByMutiCondition(existConditionMap, null);
		
		String noticeDateStr = sdfCN.format(noticeDate);
		if (existNoticeList != null && existNoticeList.size() > 0) {
			throw new IllegalArgumentException(noticeDateStr + "已存在" + groupName + "通告单，不允许重复添加");
		}
		
		/*
		 * 如果当天已经有对应分组的通告单，不允许重复添加
		 * 如果当天已经有分组的通告单，不能再建未分组的通告，反之亦然
		 */
		Map<String, Object> otherConditionMap = new HashMap<String, Object>();
		otherConditionMap.put("crewId", crewId);
		otherConditionMap.put("noticeDate", noticeDate);
		otherConditionMap.put("noticeId", noticeId);
		List<NoticeInfoModel> otherNoticeList = this.noticeService.queryManyByMutiCondition(otherConditionMap, null);
		
		if (otherNoticeList != null && otherNoticeList.size() > 0) {
			for (NoticeInfoModel noticeInfo : otherNoticeList) {
				String otherGroupId = noticeInfo.getGroupId();
				//如果当天已经有对应分组的通告单，不允许重复添加
				if (otherGroupId.equals(groupId)) {
					throw new IllegalArgumentException(noticeDateStr + "已存在" + groupName + "通告单，不允许重复添加");
				}
				//如果当天已经有分组的通告单，不能再建单组的通告
				if (!otherGroupId.equals("1") && groupId.equals("1")) {
					throw new IllegalArgumentException(noticeDateStr + "已存在分组通告单，不允许添加单组通告单");
				}
				//如果当天已经有单组的通告单，不能再建分组的通告
				if (otherGroupId.equals("1") && !groupId.equals("1")) {
					throw new IllegalArgumentException(noticeDateStr + "已存在单组通告单，不允许添加分组通告单");
				}
			}
		}
		
	}
	
	
	/**
	 * 跳转到通告单列表页面
	 * @return
	 */
	@RequestMapping("/toNoticeList")
	public ModelAndView tonoticeListPage(HttpServletRequest request, String noticeId, String source, String stepPage, String report, String window) {
		ModelAndView view = new ModelAndView();
		if (StringUtils.isNotBlank(noticeId)) {
			view.addObject("noticeId", noticeId);
		}
		
		if (StringUtils.isNotBlank(window)) {
			view.addObject("window", window);
		}
		
		if (StringUtils.isNotBlank(source)) {
			if (source.equals("generateNotice")) {
				view.setViewName("/notice/generateNotice");
				return view;
			}else if (source.equals("createNoticePage")) {
				if (StringUtils.isNotBlank(stepPage)) {
					view.addObject("stepPage", stepPage);
				}
				view.setViewName("/notice/createNotice");
				return view;
			}
			
			view.setViewName("/notice/noticeViewList");
			view.addObject("source", source);
		}else {
			if(StringUtils.isNotBlank(report)) {
				view.setViewName("/statistic/productionReport");
			} else {
				view.setViewName("notice/noticeList");
				this.sysLogService.saveSysLog(request, "查询通告单列表", terminal, NoticeInfoModel.TABLE_NAME, null, 0);
			}
		}
		return view;
	}
	
	/**
	 * 跳转到通告单页面时需要加载的数据(及获取当前剧组的分组信息)
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getCrewGroupList")
	public Map<String, Object> getCrewGroupList(HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		
		try {
			String crewId = crewInfo.getCrewId();
			//分组信息
			Map<String, String> groupList = new LinkedHashMap<String, String>();	
			//根据剧组id取出当前剧组的所有分组信息
			List<ShootGroupModel> groupModelList = this.shootGroupService.queryManyByCrewId(crewId);
			//对当前取出的剧组的分组信息进行去重然后返回前端
			for (ShootGroupModel shootGroupModel : groupModelList) {
				String shootGroupName = shootGroupModel.getGroupName();
				if (!groupList.containsValue(shootGroupName)) {
					groupList.put(shootGroupModel.getGroupId(), shootGroupName);
				}
			}
			resultMap.put("groupList", groupList);
		} catch (Exception e) {
			message = "未知错误,查询失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	
	/**
	 * 跳转到通告单列表界面需要加载的数据
	 * @param request
	 * @param pageSize 每页显示的条数
	 * @param pageNo 当前页数
	 * @param groupId 分组id
	 * @param forSimple 是否用于通告单的简单查询（当在场景表中或拍摄计划中向通告单中添加场景时，此时列出的通告单不带分页功能）
	 * @return
	 */
	@ResponseBody 
	@RequestMapping("/loadNoticeList")
	public Map<String, Object> loadNoticeData(HttpServletRequest request,Page page,String groupId, Boolean forSimple, 
			String shootLocationStr, String sceriesViewNo, String viewTape, String reamrkStr, String usedNotice, 
			String cancledNotice, String noticeDateMonth, boolean isFromListTable){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		
		try {
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			
			if (StringUtils.isNotBlank(shootLocationStr)) {
				conditionMap.put("shootLocationStr", shootLocationStr);
			}
			
			if (StringUtils.isNotBlank(sceriesViewNo)) {
				conditionMap.put("sceriesViewNo", sceriesViewNo);
			}
			
			if (StringUtils.isNotBlank(viewTape)) {
				conditionMap.put("viewTape", viewTape);
			}
			
			if (StringUtils.isNotBlank(reamrkStr)) {
				conditionMap.put("remarkInfo", reamrkStr);
			}
			
			if (StringUtils.isNotBlank(cancledNotice)) {
				conditionMap.put("cancledNotice", cancledNotice);
			}
			
			if (StringUtils.isNotBlank(noticeDateMonth)) {
				conditionMap.put("noticeDateMonth", noticeDateMonth);
			}
			
			List<Map<String, Object>> noticeList = null;
			if (StringUtils.isNotBlank(usedNotice)) {
				noticeList = noticeService.queryAscNoticeList(crewInfo.getCrewId(), page);
			}else {
				if (isFromListTable) {
					noticeList = noticeService.queryNoticeListTableData(crewInfo.getCrewId(), page, conditionMap);
				}else {
					noticeList = noticeService.queryNoticeList(crewInfo.getCrewId(),groupId, page, forSimple, conditionMap);
				}
			}
			
			for (Map<String, Object> noticeInfo : noticeList) {
				//取出通告单中主要演员的信息
				String mainRole = (String) noticeInfo.get("mainrole");
				//取出通告单中特约演员的信息
				String guestRole = (String) noticeInfo.get("guestrole");
				
				if (!StringUtils.isBlank(mainRole)) {
					String[] mainRoleArr = mainRole.split(",");
					List<String> mainRoleList = new ArrayList<String>();
					mainRole = "";
					
					//使用list集合去除主要演员的重复数据
					for (String mainRoleStr : mainRoleArr) {
						if (!mainRoleList.contains(mainRoleStr)) {
							mainRoleList.add(mainRoleStr);
							mainRole += mainRoleStr + "，";
						}
					}
					
					if (!StringUtils.isBlank(mainRole)) {
						mainRole = mainRole.substring(0, mainRole.length() - 1);
					}
					//将去重之后的主要演员的信息重新放进map中
					noticeInfo.remove("mainrole");
					noticeInfo.put("mainrole", mainRole);
				}
				
				//对特约演员的数据进行去重
				if (!StringUtils.isBlank(guestRole)) {
					String[] guestRoleArr = guestRole.split(",");
					
					List<String> guestRoleList = new ArrayList<String>();
					guestRole = "";
					for (String guestRoleStr : guestRoleArr) {
						if (!guestRoleList.contains(guestRoleStr)) {
							guestRoleList.add(guestRoleStr);
							guestRole += guestRoleStr + ",";
						}
					}
					if (!StringUtils.isBlank(guestRole)) {
						guestRole = guestRole.substring(0, guestRole.length() - 1);
					}
					noticeInfo.remove("guestrole");
					noticeInfo.put("guestrole", guestRole);
				}
				
				//将更新时间进行格式化
				Date updateTime = (Date) noticeInfo.get("updateTime");
				String updateTimeStr = DateUtils.parse2String(updateTime, "yyyy-MM-dd HH:mm:ss");
				noticeInfo.remove("updateTime");
				noticeInfo.put("updateTime", updateTimeStr);
				
				//格式化发布通告单的时间
				if (noticeInfo.get("publishTime") != null) {
					Date publishTime = (Date) noticeInfo.get("publishTime");
					String publishTimeStr = DateUtils.parse2String(publishTime, "yyyy-MM-dd HH:mm:ss");
					noticeInfo.remove("publishTime");
					noticeInfo.put("publishTime", publishTimeStr);
				}
				
				//判断当前通告单时第几天
				//第一封通告单
				/*NoticeInfoModel firstNoticeInfo = null;
				if (firstNoticeInfo == null) {
					firstNoticeInfo = noticeService.getFirstNotice(crewInfo.getCrewId());
				}
				//取出通告时间
				Date noticeDate = (Date) noticeInfo.get("noticeDate");
				if (StringUtils.isBlank(cancledNotice)) {
					//根据第一个通告单的时间和当前通告单的时间计算出共有多少个通告单
					List<Map<String, Object>> listDate = this.noticeService.queryNoticeCountByDate(firstNoticeInfo.getNoticeDate(), noticeDate, crewInfo.getCrewId(), cancledNotice);
					
					//通告单日期的天数
					noticeInfo.put("shootDays",listDate.size());
				}*/
				
			}
			
			page.setResultList(noticeList);
			resultMap.put("result", page);
			message="获取成功!";
		}catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		}catch (Exception e) {
			message = "未知异常,查询失败!";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 根据通告单id获取通告单的详细xixni
	 * @param request
	 * @param noticeId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getNoticeDate")
	public Map<String, Object> getNoticeDate(HttpServletRequest request,String noticeId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请选择通告单！");
			}
			
			NoticeInfoModel model = noticeService.queryNoticeInfoModelById(noticeId);
			if (model == null) {
				model = new NoticeInfoModel();
				model.setNoticeDate(new Date());
			}
			resultMap.put("noticeDate", model.getNoticeDate());
			resultMap.put("noticeInfo", model);
			message = "查询成功！";
		}catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询失败！";
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 获取通告单下所有的场景
	 * @param request
	 * @param noticeId 通告单id
	 * @param page 分页参数
	 * @param filter 接收传递参数的对象
	 * @param pageFlag 是否分页查询
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/loadNoticeView")
	public Map<String, Object> loadNoticeView(HttpServletRequest request,String noticeId,Page page, ViewFilter filter, boolean pageFlag, boolean isNoticeListView) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;	
		
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		
		try {
			if(!pageFlag){
				page=null;
			}else{
				//TODO 由于ui框架bug导致嵌套表格翻页无法实现，暂时一次查出所有场次
				//page.setPagesize(1000000);
			}
			
			List<Map<String, Object>> list = viewService.queryNoticeViewList(crewInfo.getCrewId(), page, noticeId, filter);
			/*if (isNoticeListView) {
				list = viewService.queryNoticeListTableView(noticeId, crewInfo.getCrewId());
			}else {
				list = viewService.queryNoticeViewList(crewInfo.getCrewId(), page, noticeId, filter);
			}*/
			
			if (list == null || list.size() == 0) {
				list = new ArrayList<Map<String,Object>>();
				Map<String, Object> viewListMap = new HashMap<String, Object>();
				list.add(viewListMap);
			}
			
			Double totalPage = 0.0;
			//遍历取出总页数
			for (Map<String, Object> map : list) {
				Object pageCount = map.get("pageCount");
				if (pageCount != null) {
					double currPage = (Double) map.get("pageCount");
					Object shootPage = map.get("shootPage");
					if (null != shootPage) {
						totalPage = totalPage + Double.parseDouble(shootPage+"");
					}else {
						totalPage = totalPage + currPage;
					}
				}
			}
			
			resultMap.put("totalPage", totalPage);
			
			if(null != page){
				resultMap.put("total", page.getTotal());
				resultMap.put("pageCount", page.getPageCount());
			}
			
			//拼接请假信息
			String leaveInfo = viewService.queryActorLeaveInfo(noticeId, null, null);
			resultMap.put("actorLeaveInfo", leaveInfo);
			
			resultMap.put("viewList", list);
		} catch (IllegalArgumentException ie){
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误,加载失败!";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 判断当前添加的场景中是否有演员请假
	 * @param request
	 * @param noticeId 通告单id
	 * @param viewIds 场景id的字符串，多个以“，”分割
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/checkIsLeave")
	public Map<String, Object> checkIsLeave(HttpServletRequest request,String noticeId,String viewIds, String noticeDateStr){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message ="";
		boolean success = true;
		
		try {
			//对前台传递的参数进行校验
			if (StringUtils.isBlank(viewIds)) {
				throw new IllegalArgumentException("请选择要保存的场景!");
			}
			
			String[] viewIdArray = viewIds.split(",");
			
			//取出场景中的角色id
			List<String> viewRoleIds = new ArrayList<String>();
			for(String viewId:viewIdArray) {
				List<Map<String, Object>> roleIdList = viewInfoService.queryViewRoleIds(viewId);
				for (Map<String, Object> map : roleIdList) {
					String roleId = (String) map.get("viewRoleId");
					if (!viewRoleIds.contains(roleId)) {
						viewRoleIds.add(roleId);
					}
				}
			}
			
			Date currDate = null;
			if (StringUtils.isNotBlank(noticeDateStr)) {
				currDate = sdf.parse(noticeDateStr);
			}
			
			//取出请假信息
			String leaveInfo = viewInfoService.queryActorLeaveInfo(noticeId, viewRoleIds, currDate);
			if (StringUtils.isNotBlank(leaveInfo)) {
				resultMap.put("leaveInfo", leaveInfo);
			}
			message = "查询成功";
		}catch(IllegalArgumentException ie){
			success = false;
			message = ie.getMessage();
			
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询失败！";
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
		
	}
	/**
	 * 向已有的通告单中添加场景信息
	 * @param request
	 * @param noticeId 通告单id
	 * @param viewIds 场景id的字符串,多个场景id以","进行分割
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/addNoticeView")
	public Map<String, Object> addNoticeView(HttpServletRequest request,String noticeId,String viewIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message ="";
		boolean success = true;
		
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);

		String currentNoticeName = "";
		try {
			//对前台传递的参数进行校验
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请选择要保存的通告单!");
			}
			
			if (StringUtils.isBlank(viewIds)) {
				throw new IllegalArgumentException("请选择要保存的场景!");
			}
			
			NoticeInfoModel noticeInfoModel = this.noticeService.queryNoticeInfoModelById(noticeId);
			if(noticeInfoModel != null) {
				currentNoticeName = noticeInfoModel.getNoticeName();
			}
			
			//验证所选场次是否已有被其他通告单使用
			List<Map<String, Object>> list = noticeService.validatorNoticeSave(viewIds,noticeId);
			if(null != list && list.size()>0 ){
				for (int i = 0; i < list.size(); i++) {
					
					Map<String, Object> viewNoticeMap = (Map<String, Object>)list.get(i);
					String getNoticeId = (String) viewNoticeMap.get("noticeid");
					
					String noticeName = viewNoticeMap.get("noticeName") == null ? "" : (String) viewNoticeMap.get("noticeName");
					if (StringUtils.isNotBlank(getNoticeId)) {
						if (noticeId.equals(getNoticeId)) {
							if (crewInfo.getCrewType() == 0 || crewInfo.getCrewType() == 3) {
								message += viewNoticeMap.get("viewNo")+"  已添加到"+ noticeName +"中，请不要重复添加！ ";
							}else {
								message += viewNoticeMap.get("seriesNo")+"-"+viewNoticeMap.get("viewNo")+"  已添加到"+ noticeName +"中，请不要重复添加！ ";
							}
							continue;
						}
					}
					int shootStatus = viewNoticeMap.get("shootStatus")== null ? 0:(Integer)viewNoticeMap.get("shootStatus");
					if (shootStatus == 3) {
						if (crewInfo.getCrewType() == 0 || crewInfo.getCrewType() == 3) {
							message += viewNoticeMap.get("viewNo")+"已删戏，不能添加！";
						}else {
							message += viewNoticeMap.get("seriesNo")+"-"+viewNoticeMap.get("viewNo")+"已删戏，不能添加！";
						}
					}else {
						if (crewInfo.getCrewType() == 0 || crewInfo.getCrewType() == 3) {
							message += viewNoticeMap.get("viewNo")+"";
						}else {
							message += viewNoticeMap.get("seriesNo")+"-"+viewNoticeMap.get("viewNo")+"";
						}
						message+="场所在通告单《"+ noticeName +"》还未销场，";
						message += "请先进行销场，再重新添加！";
					}
				}
				
				success = false;
				resultMap.put("message", message);
				resultMap.put("success", success);
				return resultMap;
			}
			String[] viewIdArray = viewIds.split(",");
			
			//取出场景中的角色id
			List<String> viewRoleIds = new ArrayList<String>();
			for(String viewId:viewIdArray) {
				List<Map<String, Object>> roleIdList = viewInfoService.queryViewRoleIds(viewId);
				for (Map<String, Object> map : roleIdList) {
					String roleId = (String) map.get("viewRoleId");
					if (!viewRoleIds.contains(roleId)) {
						viewRoleIds.add(roleId);
					}
				}
			}
			
			//取出请假信息
			String leaveInfo = viewInfoService.queryActorLeaveInfo(noticeId, viewRoleIds, null);
			if (StringUtils.isNotBlank(leaveInfo)) {
				resultMap.put("leaveInfo", leaveInfo);
			}
			
			//由于传过来的场景ID有可能有重复的情况，所以此处做一个去重处理
			List<String> viewIdList = new ArrayList<String>();
			for(String viewId:viewIdArray){
				if (!viewIdList.contains(viewId)) {
					noticeService.saveViewNoticeMap(noticeId,crewInfo.getCrewId(), viewId,null);
					viewIdList.add(viewId);
				}
			}
			
			message = "添加成功!";
			
			this.sysLogService.saveSysLog(request, "保存通告单场景信息", terminal, ViewNoticeMapModel.TABLE_NAME, currentNoticeName, 1);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常,保存失败!";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "保存通告单场景信息失败：" + e.getMessage(), terminal, ViewNoticeMapModel.TABLE_NAME, currentNoticeName, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 删除通告单中的场景信息,此接口支持批量删除(此接口删除的是场景表与通告单的关联关系)
	 * @param request
	 * @param noticeId 通告单的id
	 * @param viewIds 场景id的字符串
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteNoticeView")
	public Map<String, Object> deleteNoticeView(HttpServletRequest request,String noticeId,String viewIds){
		Map<String, Object> resultMap=new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请选择通告单!");
			}
			if (StringUtils.isBlank(viewIds)) {
				throw new IllegalArgumentException("请选择要删除的场景!");
			}
			
			noticeService.deleteNoticeView(noticeId, crewInfo.getCrewId(), viewIds);
			message = "移除场景成功!";
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误,移除场景失败!";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 根据场景id的字符串查询出剪辑信息
	 * @param request
	 * @param viewIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryCutViewInfoByViewId")
	public Map<String, Object> queryCutViewInfoByViewId(HttpServletRequest request,String noticeId, String viewIds){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			List<String> hasCutViewList = new ArrayList<String>();
			
			if (StringUtils.isBlank(viewIds)) {
				throw new IllegalArgumentException("请选择要查询的场景");
			}
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请选择要查看的通告单");
			}
			
			String[] viewIdArr = viewIds.split(",");
			for (String viewId : viewIdArr) {
				List<Map<String, Object>> cutViewList = this.cutViewService.queryCutInfoByNoticeIdOrViewId(noticeId, viewId, crewId);
				if (cutViewList != null && cutViewList.size() >0) {
					hasCutViewList.add(viewId);
				}
			}
			
			resultMap.put("hasCutViewList", hasCutViewList);
			
		}catch(IllegalArgumentException ie){
			message = ie.getMessage();
			success = false;
			
		} catch (Exception e) {
			success = false;
			message = "未知错误，查询失败";
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 导出通告单
	 * @param noticeId 通告单的id
	 */
	@ResponseBody
	@RequestMapping("/exportNotice")
	public Map<String, Object> exportNotice(HttpServletRequest request, String noticeId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		Integer crewType = crewInfo.getCrewType();
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请选择需要导出的通告单!");
			}
			
			//生成导出数据
			Map<String, Object> data = this.genExcelData(request, noticeId);
			
			//取出分组数据
			List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("locationGroupList");
			for (Map<String, Object> tempMap : list) {
				List<Map<String, Object>> viewList = (List<Map<String, Object>>) tempMap.get("viewList");
				for (Map<String, Object> viewMap : viewList) {
					//对备戏进行处理
					Integer prepareStatus = (Integer)viewMap.get("prepareStatus");
					if (null != prepareStatus && prepareStatus == 1) {
						viewMap.put("prepareStatus", "备");
					}
				}
			}
			
			//导出文件名
			String fileName = data.get("noticeName") + "";
			
			//获取模板文件地址配置
			Properties property = PropertiesUitls.fetchProperties("/config.properties");
			String srcfilePath = "";
			//根据剧组类型选择不同类型的导出模板
			if (crewType == CrewType.Movie.getValue()) {
				srcfilePath = property.getProperty("movie_noticeTemplate");
			} else {
				srcfilePath = property.getProperty("tvplay_noticeTemplate");
			}
			
			//生成下载路径
			String downloadPath = property.getProperty("downloadPath")+fileName+".xls";
			File pathFile = new File(property.getProperty("downloadPath"));
			if(!pathFile.isDirectory()){
				pathFile.mkdirs();
			}
			
			//生成可下载的excel文件
			viewInfoService.exportViewToExcelTemplate(srcfilePath, data, downloadPath);
			//ExportExcelUtil.exportViewToExcelTemplate(srcfilePath, data , downloadPath);
			resultMap.put("downloadPath", downloadPath);
			message = "导出成功!";
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(ie.getMessage());
		} catch (Exception e) {
			message = "未知异常，导出通告单场景信息失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
		
	}
	
	/**
	 * 生成通告单导出和打印信息
	 * @param request
	 * @param noticeId
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> genExcelData(HttpServletRequest request, String noticeId) throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		
		if(StringUtils.isBlank(noticeId)){
			return data;
		}
		
		data.put("noticeId", noticeId);
		
		//剧本信息
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		
		//通告单信息
		NoticeInfoModel noticeInfo = noticeService.getNotice(noticeId);
		data.put("noticeName", noticeInfo.getNoticeName());
		data.put("noticePublished", noticeInfo.getPublished());
		data.put("updateTime", noticeInfo.getUpdateTime());
		
		//通告单对应的通告单时间
		NoticeTimeModel myNoticeTime = noticeService.queryNoticeTimeByNoticeId(noticeId);
		//上一份通告单时间，用于第一次生成通告单时缺省值的填写
		NoticeTimeModel lastNoticeTime = this.noticeService.queryLastNoticeTime(crewInfo.getCrewId());
		//查询上一个同组通告单时间(用于缺省信息的填写)
		NoticeTimeModel lastGroupNoticeTime = this.noticeService.queryLastGroupNoticeTime(crewInfo.getCrewId(), noticeInfo.getGroupId());
		
		if (myNoticeTime != null) {
			String contactUserStr = "";
			//根据通告单的时间查询出,当前时间下的通告单中联系人的信息
			List<Map<String, Object>> crewContactList = this.crewContactService.queryByNotictTimeId(crewInfo.getCrewId(), myNoticeTime.getNoticeTimeId());
			if (crewContactList != null && crewContactList.size() > 0) {
				String userIds = "";
				for(Map<String, Object> contactInfo : crewContactList) {
					userIds += contactInfo.get("contactId") + ",";
					contactUserStr += (String) contactInfo.get("duty") + (String) contactInfo.get("contactName") + "：" + (String) contactInfo.get("phone") + "\r\n";
				}
				//通告单中联系人的职务/姓名/电话信息
				data.put("contactUserStr", contactUserStr);
				//通告单中联系人的列表信息
				data.put("contactUserList", crewContactList);
				//通告单中的联系人的id
				data.put("contactUserIds", userIds.substring(0, userIds.length() - 1));
			}
		}
		
		//查询上一封通告单的联系人信息
		if (lastGroupNoticeTime != null) {
			List<Map<String, Object>> crewContactList = this.crewContactService.queryByNotictTimeId(crewInfo.getCrewId(), lastGroupNoticeTime.getNoticeTimeId());
			if (crewContactList != null && crewContactList.size() > 0) {
				String contactIds = "";
				for(Map<String, Object> contactInfo : crewContactList) {
					contactIds += contactInfo.get("contactId") + ",";
				}
				data.put("lastContactUserList", crewContactList);
				data.put("lastContactUserIds", contactIds.substring(0, contactIds.length() - 1));
			}
			
			//判断同组通告单中的组导演是否填写，如果没填写，则获取创建剧组时填写的导演
			String groupDirector = lastGroupNoticeTime.getGroupDirector();
			if (StringUtils.isBlank(groupDirector)) {
				//没有导演信息，获取新建剧组时填写的导演信息
				lastGroupNoticeTime.setGroupDirector(crewInfo.getDirector());
			}
		}else {
			lastGroupNoticeTime = new NoticeTimeModel();
			lastGroupNoticeTime.setGroupDirector(crewInfo.getDirector());
		}
		
		//根据通告单的id查询出分组信息
		ShootGroupModel shootGroupModel  = shootGroupService.queryOneByGroupId(noticeInfo.getGroupId());
		//第一封通告单
		NoticeInfoModel firstNoticeInfo = noticeService.getFirstNotice(noticeInfo.getCrewId());
		
		//剧名
		data.put("crewName", crewInfo.getCrewName());
		
		//分组名
		if (shootGroupModel != null) {
			data.put("groupName", shootGroupModel.getGroupName());
		}
		
		//通告单时间
		data.put("myNoticeTime", myNoticeTime);
		data.put("lastNoticeTime", lastNoticeTime);
		data.put("lastGroupNoticeTime", lastGroupNoticeTime);
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(noticeInfo.getNoticeDate());
		int month=calendar.get(Calendar.MONTH);
		
		if(month==12){
			month=1;
		}else{
			month++;
		}
		
		data.put("noticeDataDay", month +"月" + calendar.get(Calendar.DAY_OF_MONTH) + "日");
		//通告单日期
		data.put("noticeDate", calendar.get(Calendar.YEAR)+"-" + month + "-" + calendar.get(Calendar.DAY_OF_MONTH));
		//通告单在一周中的某天
		Map<Integer, String> dayOfWeekMap = new HashMap<Integer, String>();
		dayOfWeekMap.put(1, "一");
		dayOfWeekMap.put(2, "二");
		dayOfWeekMap.put(3, "三");
		dayOfWeekMap.put(4, "四");
		dayOfWeekMap.put(5, "五");
		dayOfWeekMap.put(6, "六");
		dayOfWeekMap.put(7, "日");
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		dayOfWeek = dayOfWeek-1;
		if(dayOfWeek==0){
			dayOfWeek=7;
		}
		data.put("day", dayOfWeekMap.get(dayOfWeek));
		
		/******* 计算当前通告单属于第几天 **********/
		//根据第一个通告单的时间和当前通告单的时间计算出共有多少个通告单
		List<Map<String, Object>> listDate = this.noticeService.queryNoticeCountByDate(firstNoticeInfo.getNoticeDate(), noticeInfo.getNoticeDate(),crewInfo.getCrewId(), null);
		
		//通告单日期的天数
		data.put("shootDays",listDate.size());
		
		//通告单下的场景列表信息
		List<Map<String, Object>> list = viewService.queryNoticeViewList(noticeInfo.getCrewId(), null, noticeId,null);
		if (list == null || list.size() == 0) {
			return data;
		}
		
		String viewIds = "";
		//遍历场景信息的列表,取出每场场景的id
		for (Map<String, Object> viewInfo : list) {
			viewIds += viewInfo.get("viewId")+",";
		}
		
		if (!StringUtils.isBlank(viewIds)) {
			viewIds.substring(0, viewIds.length()-1);
		}
		//根据取出的场景的id查询出场景的相关信息
		//所有主演信息
		List<Map<String, Object>> roleSignList = this.viewRoleService.queryViewRoleListByNoticeId(noticeId);
		//所有特约演员
		List<ViewRoleAndActorModel> roleGuestSignList = viewService.queryViewGuestRoleSignByViewIds(viewIds);
		//所有群众演员
		List<ViewRoleAndActorModel> roleMassSignList = viewService.queryViewMassRoleSignByViewIds(viewIds);
		//所有道具
		List<Map<String, Object>> propsList = this.goodsInfoService.queryPropInfoByViewIds(viewIds);
		
		String props="";
		String roleProps="";
		for(Map<String, Object> propsModel : propsList){
			//普通道具
			if(Integer.parseInt(propsModel.get("goodsType").toString()) == GoodsType.CommonProps.getValue()){
				props += propsModel.get("goodsName")+",";
			//特殊道具
			}else if(Integer.parseInt(propsModel.get("goodsType").toString()) == GoodsType.SpecialProps.getValue()){
				roleProps += propsModel.get("goodsName")+",";
			}
		}
		
		if(StringUtils.isNotBlank(props)){
			props.substring(0, props.length()-1);
		}
		if(StringUtils.isNotBlank(roleProps)){
			roleProps.substring(0, roleProps.length()-1);
		}
		
		//总场数
		data.put("viewCount", list.size());
		//主演数
		data.put("mainRoleCount", roleSignList.size());
		//主演List
		data.put("mainRoleList", roleSignList);
		//特约演员数
		data.put("guestRoleCount", "\n特约" + roleGuestSignList.size());
		
		int roleNum = 0;
		//群众演员
		String massRoles = "";
		for(ViewRoleAndActorModel massRole : roleMassSignList){
			roleNum = roleNum + massRole.getRoleNum();
			if (massRole.getRoleNum() > 1) {
				massRoles += massRole.getViewRoleName()+ "(" + massRole.getRoleNum() +")" + ",";
			}else {
				massRoles += massRole.getViewRoleName()+ ",";
			}
		}
		//特约演员
		String guestRoles = "";
		for (ViewRoleAndActorModel guestRole : roleGuestSignList) {
			guestRoles += guestRole.getViewRoleName() + ",";
		}
		//群众演员人数
		data.put("massCount", "群众" + roleNum);
		
		data.put("massrole", massRoles != "" ? massRoles.substring(0, massRoles.length() - 1) : "");
		data.put("guestrole", guestRoles != "" ? guestRoles.substring(0, guestRoles.length() - 1) : "");
		
		//道具
		data.put("props", props);
		//特殊道具
		data.put("roleProps", roleProps);
		
		//取出气氛信息
		List<AtmosphereInfoModel> atmo = atmosphereService.queryAllByCrewId(noticeInfo.getCrewId());
		Map<String, Object> atmoMap = new HashMap<String, Object>();
		for(AtmosphereInfoModel atmosphere : atmo){
			atmoMap.put(atmosphere.getAtmosphereId(), atmosphere.getAtmosphereName());
		}
		
		//内外景
		Map<String, String> siteMap = new HashMap<String, String>();
		siteMap.put("1","内景");
		siteMap.put("2","外景");
		siteMap.put("3","内外景");
		
		//总页数
		BigDecimal pageCount=new BigDecimal(0);
		
		//按场景分组
		List<Map<String, Object>> locationGroup = new ArrayList<Map<String, Object>>();
		//Map<String, Object> locationMap = new HashMap<String, Object>();
		
		List<String> viewLocations = new ArrayList<String>();
		List<String> viewAdverts = new ArrayList<String>();
		String preShootLocation = "firstLocationFlag";
		String preShootLocationId = "";
		List<Map<String, Object>> locationViewList = new ArrayList<Map<String, Object>>();
		String locationViewIds = "";
		
		for(Map<String, Object> viewMap : list){
			
			if(null != viewMap.get("atmosphereId")){
				viewMap.put("atmosphere", atmoMap.get(viewMap.get("atmosphereId")));
			}
			
			if (viewMap.get("pageCount") != null) {
				Object shootPage = viewMap.get("shootPage");
				if (null == shootPage) {
					pageCount=pageCount.add(new BigDecimal((Double)viewMap.get("pageCount")));
				}else {
					pageCount=pageCount.add(new BigDecimal((Double)viewMap.get("shootPage")));
				}
			}
			
			//场景主演字符串
			String roleList = (String)viewMap.get("roleList");
			String []roleArray=roleList.split(",");
			//场景主演list
			List<String> viewRole = Arrays.asList(roleArray);
			//替换场景主演的list
			List<Object> newViewRoleList = new ArrayList<Object>();
			//循环所有的角色查看当前场是否有
			for(Map<String, Object> roleModel : roleSignList){
				
				if(viewRole.contains((String)roleModel.get("viewRoleName"))){
					
					if(null == roleModel.get("shortName") || StringUtils.isBlank((String)roleModel.get("shortName"))){
						roleModel.put("shortName","√");
					}
					newViewRoleList.add(roleModel);
				}else{
					newViewRoleList.add(new ViewRoleModel());
				}
			}
			
			viewMap.put("roleList", newViewRoleList);
			
			//转场
			/*if (null == locationMap.get(viewMap.get("shootLocation"))) {
				List location = new ArrayList();
				Map groupMap = new HashMap();
				groupMap.put("location", viewMap.get("shootLocation"));
				locationGroup.add(groupMap);
				location.add(viewMap);
				locationMap.put(viewMap.get("shootLocation"), location);
			} else {
				List location = (List)locationMap.get(viewMap.get("shootLocation"));
				location.add(viewMap);
			}*/
			
			//获取所有拍摄地点
			if (!viewLocations.contains(viewMap.get("shootLocation")) && !StringUtils.isBlank((String) viewMap.get("shootLocation"))) {
				viewLocations.add((String) viewMap.get("shootLocation"));
			}
			//获取所有商植
			if (!StringUtils.isBlank((String) viewMap.get("advertName"))) {
				String advertNames = (String) viewMap.get("advertName");
				String[] advertArr = advertNames.split(",");
				for (String advert : advertArr) {
					if (!viewAdverts.contains(advert)) {
						viewAdverts.add(advert);
					}
				}
			}
			
			//考虑拍摄地没有设置或设置为空的情况
			if (!StringUtils.isBlank(preShootLocation) && preShootLocation.equals("firstLocationFlag")) {	//第一场信息
				locationViewList.add(viewMap);
				locationViewIds += viewMap.get("viewId") + ",";
				
			} else if ((StringUtils.isBlank(preShootLocation) && viewMap.get("shootLocation") != null && !viewMap.get("shootLocation").equals("")) 
					|| (!StringUtils.isBlank(preShootLocation) && viewMap.get("shootLocation") != null && !viewMap.get("shootLocation").equals("") && !preShootLocation.equals((String) viewMap.get("shootLocation")))
					|| (!StringUtils.isBlank(preShootLocation) && (viewMap.get("shootLocation") == null || viewMap.get("shootLocation").equals("")))) {
				locationViewIds = locationViewIds.substring(0, locationViewIds.length() - 1);
				//查询出专场信息
				ConvertAddressModel convertAddress = this.convertAddressService.queryByLocationViewIds(crewInfo.getCrewId(), noticeId, preShootLocationId, locationViewIds);
				
				//此处有个转场提示信息
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("location", preShootLocation);
				map.put("locationId", preShootLocationId);
				map.put("viewList", locationViewList);
				map.put("locationViewIds", locationViewIds);
				
				//根据拍摄地点的id查询出拍摄地的经纬度
				String longitude = ""; //经度
				String latitude = ""; //维度
				SceneViewInfoModel model = this.sceneViewInfoService.querySceneViewById(preShootLocationId);
				if (model != null ) {
					longitude = model.getVLongitude();
					latitude = model.getVLatitude();
				}
				map.put("longitude", longitude);
				map.put("latitude", latitude);
				
				if (convertAddress != null) {
					map.put("convertAddressInfo", convertAddress.getRemark());
				}
				locationGroup.add(map);
				
				locationViewList = new ArrayList<Map<String, Object>>();
				locationViewIds = "";
				locationViewList.add(viewMap);
				locationViewIds += viewMap.get("viewId") + ",";
			} else {
				locationViewList.add(viewMap);
				locationViewIds += viewMap.get("viewId") + ",";
			}
			
			//上一场实拍摄地
			preShootLocation = (String) viewMap.get("shootLocation");
			preShootLocationId = (String) viewMap.get("shootLocationId");
		}	//循环结束
		
		//处理最后一组实拍景地
		if (!StringUtils.isBlank(locationViewIds)) {
			locationViewIds = locationViewIds.substring(0, locationViewIds.length() - 1);
		}
		ConvertAddressModel convertAddress = this.convertAddressService.queryByLocationViewIds(crewInfo.getCrewId(), noticeId, preShootLocationId, locationViewIds);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("location", preShootLocation);
		map.put("locationId", preShootLocationId);
		
		//根据拍摄地点的id查询出拍摄地的经纬度
		String longitude = ""; //经度
		String latitude = ""; //维度
		SceneViewInfoModel model = this.sceneViewInfoService.querySceneViewById(preShootLocationId);
		if (model != null ) {
			longitude = model.getVLongitude();
			latitude = model.getVLatitude();
		}
		map.put("longitude", longitude);
		map.put("latitude", latitude);
		
		map.put("viewList", locationViewList);
		map.put("locationViewIds", locationViewIds);
		if (convertAddress != null) {
			map.put("convertAddressInfo", convertAddress.getRemark());
		}
		locationGroup.add(map);
		
		
		//转场
		/*for (Map groupMap: locationGroup) {
			groupMap.put("viewList", locationMap.get(groupMap.get("location")));
		}*/
		String viewLocationStr = "";
		for (String location : viewLocations) {
			viewLocationStr += location + ",";
		}
		if (!StringUtils.isBlank(viewLocationStr)) {
			viewLocationStr = viewLocationStr.substring(0, viewLocationStr.length() - 1);
		}
		data.put("viewLocations", viewLocationStr);
		String allAdvertNames = "";
		for (String advert : viewAdverts) {
			allAdvertNames += advert + ",";
		}
		if (!StringUtils.isBlank(allAdvertNames)) {
			allAdvertNames = allAdvertNames.substring(0, allAdvertNames.length() - 1);
		}
		data.put("allAdvertNames", allAdvertNames);
		//场次数据
		data.put("locationGroupList", locationGroup);
		
		//页数
		data.put("pageCount", pageCount.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		
		data.put("today",sdf.format(new Date()) );
		
		return data;
	}
	
	/**
	 * 获取天气信息
	 * @param cityName 城市名称
	 * @param noticeId 通告单id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainWeatherInfo")
	public Map<String, Object> obtainWeatherInfo(String cityName, String noticeId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			if (StringUtils.isBlank(cityName)) {
				throw new IllegalArgumentException("未定位到城市，请稍等片刻！");
			}
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请选择要查询的通告单!");
			}
			//根据通告单的id查询出通告单的额信息
			NoticeInfoModel noticeInfo = noticeService.getNotice(noticeId);
			List<WeatherInfoDto> weatherList = weatherInfoService.saveWeatherInfoByCityName(cityName);
			
			String weatherInfoStr = " ";
			
			String noticeDateStr = this.sdf4.format(noticeInfo.getNoticeDate());
			for (WeatherInfoDto weatherInfo : weatherList) {
				if (weatherInfo.getDay().equals(noticeDateStr)) {
					String dayTemperature = weatherInfo.getDayTemperature();
					String dayWindDirection = weatherInfo.getDayWindDirection();
					String dayWeather = weatherInfo.getDayWeather();
					String nightTemperature = weatherInfo.getNightTemperature();
					
					weatherInfoStr = nightTemperature + "~"+  dayTemperature + "°  " + dayWeather + "  " + dayWindDirection ;
					
					break;
				}
			}
			
			if (StringUtils.isBlank(weatherInfoStr)) {
				weatherInfoStr = "暂无天气";
			}
			
			resultMap.put("weatherInfo", weatherInfoStr);
			
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，获取天气信息失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 根据Ip地址查询所在城市
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainCityInfoByIp")
	public Map<String, Object> obtainCityInfoByIp() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String appId = properties.getProperty("YIYUAN_APPID");
			String secret = properties.getProperty("YIYUAN_SECRET");

			// 从易源获取外网IP地址
			JSONObject ipResultJson = HttpUtils.httpGet("http://route.showapi.com/632-1?showapi_appid="
							+ appId + "&showapi_sign=" + secret
							+ "&showapi_timestamp="
							+ this.sdf3.format(new Date()));
			
			if("0".equals(ipResultJson.get("showapi_res_code").toString())){
				//String ip = ipResultJson.getJSONObject("showapi_res_body").getString("ip");
				String city = ipResultJson.getJSONObject("showapi_res_body").getString("city");
				
				resultMap.put("city", city);
			}
			
			message = "获取所在城市成功";
		} catch (Exception e) {
			success = false;
			message = "未知异常，获取城市信息失败";
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 跳转到通告单打印预览发布页面
	 * @param request
	 * @param noticeId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/printView")
	public ModelAndView toPrintView(HttpServletRequest request,String noticeId){
		ModelAndView view = new ModelAndView();
		view.addObject("noticeId", noticeId);
		view.setViewName("/notice/printView");
		return view;
	}
	
	/**
	 * 根据通告单id查询通告单的详细信息
	 * @param request
	 * @param noticeId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getNoticePrintData")
	public Map<String, Object> getNoticePrintData(HttpServletRequest request,String noticeId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		SimpleDateFormat sdfPoint = new SimpleDateFormat("yyyy.MM.dd");
		SimpleDateFormat sdfHour = new SimpleDateFormat("HH：mm：ss");
		
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		Integer crewType = crewInfo.getCrewType();
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请选择要查看的通告单！");
			}
			
			Map<String, Object> map = this.genExcelData(request, noticeId);
			
			//特别
			String maxSpecial = "";
			//场景
			String maxLocation = "";
			//内容提要
			String maxContent = "";
			//特约
			String maxGuest = "";
			//群演
			String maxMass = "";
			//服化道
			String maxClothPro = "";
			//备注
			String maxRemark = "";
			//取出每个字段的最大字符串
			if (map != null) {
				List<Map<String, Object>> locationGroupList = (List<Map<String, Object>>) map.get("locationGroupList");
				for (int i =0; i<locationGroupList.size(); i++) {
					Map<String, Object> locationViewMap = locationGroupList.get(i);
					List<Map<String, Object>> viewList = (List<Map<String, Object>>) locationViewMap.get("viewList");
					for(int a=0; a<viewList.size(); a++) {
						Map<String, Object> viewMap = viewList.get(a);
						//初始化数据
						String specialProps = (String) viewMap.get("specialPropsList");
						String viewType = viewMap.get("viewType")== null ? "": String.valueOf(viewMap.get("viewType"));
						String season = viewMap.get("season") == null ? "": String.valueOf(viewMap.get("season"));
						if (StringUtils.isBlank(specialProps)) {
							specialProps = "";
						}
						if (StringUtils.isBlank(viewType)) {
							viewType = "";
						}
						if (StringUtils.isBlank(season)) {
							season = "";
						}
						//场景
						String majorView = (String) viewMap.get("majorView");
						String minorView = (String) viewMap.get("minorView");
						String thirdLevelView = (String) viewMap.get("thirdLevelView");
						if (StringUtils.isBlank(majorView)) {
							majorView = "";
						}
						if (StringUtils.isBlank(minorView)) {
							minorView = "";
						}
						if (StringUtils.isBlank(thirdLevelView)) {
							thirdLevelView = "";
						}
						
						//服化道
						String clothesName = (String) viewMap.get("clothesName");
						String makeupName = (String) viewMap.get("makeupName");
						String propsList = (String) viewMap.get("propsList");
						if (StringUtils.isBlank(clothesName)) {
							clothesName = "";
						}
						if (StringUtils.isBlank(makeupName)) {
							makeupName = "";
						}
						if (StringUtils.isBlank(propsList)) {
							propsList = "";
						}
						if (i == 0 && a == 0) {
							//特别
							maxSpecial = specialProps + viewType + season;
							//场景
							maxLocation = majorView + minorView + thirdLevelView;
							//内容提要
							maxContent = (String) viewMap.get("mainContent");
							if (StringUtils.isBlank(maxContent)) {
								maxContent ="";
							}
							//特约
							maxGuest = (String) viewMap.get("guestRoleList");
							if (StringUtils.isBlank(maxGuest)) {
								maxGuest = "";
							}
							//群演
							maxMass = (String) viewMap.get("massRoleList");
							if (StringUtils.isBlank(maxMass)) {
								maxMass = "";
							}
							//服化道
							maxClothPro = clothesName + makeupName + propsList;
							//备注
							maxRemark = (String) viewMap.get("viewRemark");
							if (StringUtils.isBlank(maxRemark)) {
								maxRemark = "";
							}
							continue;
						}
						
						String special = specialProps + viewType + season;
						String location = majorView + minorView + thirdLevelView;
						String content = (String) viewMap.get("mainContent");
						if (StringUtils.isBlank(content)) {
							content = "";
						}
						String guest = (String) viewMap.get("guestRoleList");
						if (StringUtils.isBlank(guest)) {
							guest = "";
						}
						String mass  =(String) viewMap.get("massRoleList");
						if (StringUtils.isBlank(mass)) {
							mass = "";
						}
						String clothPro = clothesName + makeupName + propsList;
						String remark = (String) viewMap.get("viewRemark");
						if (StringUtils.isBlank(remark)) {
							remark = "";
						}
						
						//取出最大值
						//特别
						if (special.length() > maxSpecial.length()) {
							maxSpecial = special;
						}
						//场景
						if (location.length() > maxLocation.length()) {
							maxLocation = location;
						}
						//内容提要
						if (content.length() > maxContent.length()) {
							maxContent = content;
						}
						//特约
						if (guest.length() > maxGuest.length()) {
							maxGuest = guest;
						}
						//群演
						if (mass.length() > maxMass.length()) {
							maxMass = mass;
						}
						//服化道
						if (clothPro.length() > maxClothPro.length()) {
							maxClothPro = clothPro;
						}
						//备注
						if (remark.length() > maxRemark.length()) {
							maxRemark = remark;
						}
					}
				}
			}
			
			map.put("maxSpecial", maxSpecial);
			map.put("maxLocation", maxLocation);
			map.put("maxContent", maxContent);
			map.put("maxGuest", maxGuest);
			map.put("maxMass", maxMass);
			map.put("maxClothPro", maxClothPro);
			map.put("maxRemark", maxRemark);
			resultMap.put("data", map);
			//拼接制表时间
			Date updateTime = (Date) map.get("updateTime");
			if (updateTime != null) {
				String makeDate = sdfPoint.format(updateTime);
				String makeTime = sdfHour.format(updateTime);
				
				resultMap.put("makeDate", makeDate);
				resultMap.put("makeTime", makeTime);
			}
			//通告单图片信息
			NoticeTimeModel myNoticeTime = (NoticeTimeModel) map.get("myNoticeTime");
			String version = this.sdf3.format(myNoticeTime.getUpdateTime());
			List<NoticePictureModel> noticePictureList = this.noticeService.queryNoticeImgByNotice(noticeId, version);
			for (NoticePictureModel noticePicture : noticePictureList) {
				if (!StringUtils.isBlank(noticePicture.getBigPicurl())) {
					noticePicture.setBigPicurl(FileUtils.genPreviewPath(noticePicture.getBigPicurl()));
				}
				if (!StringUtils.isBlank(noticePicture.getSmallPicurl())) {
					noticePicture.setSmallPicurl(FileUtils.genPreviewPath(noticePicture.getSmallPicurl()));
				}
			}
			
			resultMap.put("noticePictureList", noticePictureList);
			resultMap.put("pictureCount", noticePictureList.size());
			
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
		} catch (Exception e) {
			message = "未知异常，查询失败";
			success = false;
			
			logger.error(message, e);
		}
		resultMap.put("crewType", crewType);
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	/**
	 * 点击预览通告单跳转到通告单信息修改界面
	 * @param request
	 * @param noticeId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/toGenerateNotice")
	public ModelAndView toGenerateNotice(HttpServletRequest request,String noticeId) {
		ModelAndView view = new ModelAndView("/notice/generateNotice");
		if (StringUtils.isNotBlank(noticeId)) {
			view.addObject("noticeId", noticeId);
		}
		return view;
	}
	
	/**
	 * 跳转到通告单信息修改界面时需要加载的数据
	 * @param request
	 * @param noticeId 通告单的id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryGenerateNoticeData")
	public Map<String, Object> queryGenerateNoticeData(HttpServletRequest request,String noticeId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请选择要生成信息的通告单!");
			}
			
			Map<String, Object> map = this.genExcelData(request, noticeId);
			
			//根据通告单id查询通告单信息
			NoticeInfoModel model = this.noticeService.getNotice(noticeId);
			//查询通告单的反馈列表
			List<Map<String,Object>> facdbackList = this.noticeService.queryNoticeFacdbackList(noticeId, sdf.format(model.getUpdateTime()));
			
			resultMap.put("data", map);
			resultMap.put("facdbackList", facdbackList);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
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
	 * 保存通告单信息
	 * @param request
	 * @param noticeId	通告单ID
	 * @param breakfastTime	早餐时间
	 * @param departureTime	出发时间
	 * @param note	特别提示
	 * @param contact	联系人
	 * @param remark	备注
	 * @param version	版本信息
	 * @param groupDirector	组导演
	 * @param shootGuide	摄影
	 * @param insideAdvert	商植
	 * @param roleMakeup	化妆
	 * @param roleArriveTime	演员出发时间
	 * @param roleGiveMakeupTime	演员交妆时间
	 * @param convertRemark	转场信息
	 * @param roleConvertRemark	演员转场信息
	 * @param shootLocationInfos 拍摄地点信息
	 * @param weatherInfo 天气信息
	 * @param imgStorePath 通告单全文图片存储路径
	 * @param makeTableTimeStr 制表时间字符串
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/saveGeneratedNotice")
	public Map<String, Object> saveGeneratedNotice(HttpServletRequest request, String noticeId,
			String breakfastTime, String departureTime, String note, String roleInfo,
			String contact, String remark, String version, String groupDirector, String shootGuide, 
			String insideAdvert, String roleMakeup, String roleArriveTime, String roleGiveMakeupTime, 
			String convertRemark, String roleConvertRemark, String shootLocationInfos, String weatherInfo, String imgStorePath, String smallImgStorePath,
			String groupId, String noticeDateStr,String concatInfoList, String noticeName, String chengePublished) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String crewId = getCrewId(request);
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请选择要保存的通告单!");
			}
			//校验当前的分组信息
			checkNoticeGroup(crewInfo.getCrewId(), noticeId, groupId, sdf.parse(noticeDateStr));
			
			noticeService.saveNoticeInfo(crewId, noticeId, breakfastTime,sdf.parse(noticeDateStr),groupId,
					departureTime, note, roleInfo, contact, remark, version, groupDirector,
					shootGuide, insideAdvert, roleMakeup, roleArriveTime,
					roleGiveMakeupTime, convertRemark, roleConvertRemark, shootLocationInfos, weatherInfo, imgStorePath, 
					smallImgStorePath,concatInfoList,noticeName,chengePublished);
			
			message = "保存通告单成功!";
			
			this.sysLogService.saveSysLog(request, "保存通告单信息-明细", terminal, NoticeTimeModel.TABLE_NAME, noticeName, 1);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知错误，保存失败";
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "保存通告单信息-明细失败：" + e.getMessage(), terminal, NoticeTimeModel.TABLE_NAME, noticeName, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 保存拍摄状态
	 * @param noticeId  通告单id
	 * @param viewIds 场景id的字符串
	 * @param shootStatus 拍摄状态
	 * @param statusRemark 状态备注
	 * @param tapNo 带号(拍摄的磁带号)
	 * @param shootDate 拍摄日期
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveNoticeViewShootStatus")
	public Map<String, Object> saveNoticeViewStatus(HttpServletRequest request, 
			String noticeId, String viewIds,Integer shootStatus, 
			String statusRemark, String tapNo, String shootDate) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		String noticeName = "";
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请选择通告单!");
			}
			if (StringUtils.isBlank(viewIds)) {
				throw new IllegalArgumentException("请选择要销场的场景!");
			}
			
			NoticeInfoModel noticeInfoModel = this.noticeService.queryNoticeInfoModelById(noticeId);
			if(noticeInfoModel != null) {
				noticeName = noticeInfoModel.getNoticeName();
			}
			
			noticeService.saveNoticeShootStatus(noticeId, viewIds, shootStatus, statusRemark, tapNo, shootDate);
			message = "保存拍摄状态成功!";
			
			this.sysLogService.saveSysLog(request, "销场", terminal, ViewNoticeMapModel.TABLE_NAME, noticeName + "," + viewIds, 2);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误,保存失败!";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "销场失败：" + e.getMessage(), terminal, ViewNoticeMapModel.TABLE_NAME, noticeName + "," + viewIds, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 获取场景剧本内容文本
	 * @param viewId 场景的id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getViewContent")
	public Map<String, Object> getViewContent(String viewId){
		Map<String, Object> resuleMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(viewId)) {
				throw new IllegalArgumentException("请选择要查询的场景!");
			}
			//根据场景号,查询出场景的详细信息
			ViewContentModel  viewContentModel = viewService.queryViewContentModel(viewId);
			
			if(null == viewContentModel){
				viewContentModel = new ViewContentModel();
				viewContentModel.setContent("");
				viewContentModel.setTitle("");
				resuleMap.put("content", viewContentModel);
			}else{
				if(null == viewContentModel.getTitle()){
					viewContentModel.setTitle("");
				}else{
					viewContentModel.setTitle(viewContentModel.getTitle().replaceAll("\r\n", "<br>"));
				}
				if(null == viewContentModel.getContent()){
					viewContentModel.setContent("");
				}else{
					viewContentModel.setContent(viewContentModel.getContent().replaceAll("\r\n", "<br>"));
				}
				resuleMap.put("content", viewContentModel);
			}
			message = "查询文本内容成功!";
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误,查询失败!";
			success = false;
			
			logger.error(message, e);
		}
		
		resuleMap.put("message", message);
		resuleMap.put("success", success);
		return resuleMap;
	}
	
	/**
	 * 更新通告单内的场景的排列顺序
	 * @param noticeId 通告单的id
	 * @param viewIds 场景id的字符串
	 * @return
	 */
	@ResponseBody 
	@RequestMapping("/sortNoticeView")
	public Map<String, Object> sortNoticeView(String noticeId,String viewIds){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请选择要排序的通告单!");
			}
			if (StringUtils.isBlank(viewIds)) {
				throw new IllegalArgumentException("请选择要排序的场景!");
			}
			
			noticeService.updateNoticeViewSort(noticeId, viewIds);
			message = "排序成功!";
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误,排序失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 删除一条通告单
	 * @param request
	 * @param noticeId	通告单ID
	 * @return
	 */
	@RequestMapping("/deleteOneNotice")
	public @ResponseBody Map<String, Object> deleteOneNotice(HttpServletRequest request, String noticeId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		String crewId = getCrewId(request);
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请选择要删除的通告单!");
			}
			
			this.noticeService.deleteOneNotice(crewId, noticeId);
			
			message = "删除成功!";
		} catch (IllegalArgumentException ie){
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch(Exception e) {
			message = "未知错误，删除通告单失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 上传通告单图片
	 * 通过MultipartFile file传入文件流
	 * @param file	图片文件
	 * @param request	
	 * @param noticeId	通告单ID
	 */
	@ResponseBody
	@RequestMapping("/uploadNoticeImg")
	public Map<String, Object> uploadNoticeImg(MultipartFile file, HttpServletRequest request, String noticeId) {
		String crewId = getCrewId(request);
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请选择通告单!");
			}
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String baseStorePath = properties.getProperty("fileupload.path");
			String storePath = baseStorePath + "notice/";
			
			//大图
			Map<String, String> fileMap = FileUtils.uploadFile(file, false, storePath);
			String fileStoreName = fileMap.get("fileStoreName");
			String fileRealName = fileMap.get("fileRealName");
			String realStorePath = fileMap.get("storePath");
			
			
			//将上传的图片缩小后进行存储,并保存到数据库中
			String exceptSuffixName = fileStoreName.substring(0, fileRealName.lastIndexOf("."));
			String suffix = fileStoreName.substring(fileRealName.lastIndexOf("."));
			
			String smallImgPath = realStorePath + "/small/" + exceptSuffixName + "_sd" + suffix;
			BufferedImage newImage = FileUtils.getNewImage(file, null, 768, 320);
			File destFile = new File(smallImgPath);
			FileUtils.makeDir(destFile);
			//将图片上传到服务器中
            ImageIO.write(newImage, "png", destFile);
			
			NoticeTimeModel noticeTime = this.noticeService.queryNoticeTimeByNoticeId(noticeId);
			if (noticeTime != null) {
				String imgId = UUIDUtils.getId();
				
				NoticePictureModel noticePicture = new NoticePictureModel();
	            noticePicture.setId(imgId);
	            noticePicture.setCrewId(crewId);
	            noticePicture.setNoticeId(noticeId);
	            noticePicture.setNoticeVersion(this.sdf3.format(noticeTime.getUpdateTime()));
	            noticePicture.setName(fileRealName.substring(0, fileRealName.lastIndexOf(".")));
	            noticePicture.setBigPicurl(realStorePath + fileStoreName);
	            noticePicture.setSmallPicurl(smallImgPath);
	            noticePicture.setUploadTime(new Date());
	            //将通告单的图片信息保存到数据库中
				this.noticeService.addNoticePicture(noticePicture);
				
				resultMap.put("imgId", imgId);
			}
			
			resultMap.put("storePath", FileUtils.genPreviewPath(realStorePath + fileStoreName));
			resultMap.put("smallImgPath", FileUtils.genPreviewPath(smallImgPath));
			resultMap.put("imgName", fileRealName.substring(0, fileRealName.lastIndexOf(".")));
			
			List<NoticePictureModel> noticePictureList = this.noticeService.queryNoticeImgByNotice(noticeId, noticeTime.getVersion());
			resultMap.put("pictureCount", noticePictureList.size());
			
			message = "上传成功";
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，上传通告单图片失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 发布通告单
	 * @param request
	 * @param noticeId	通告单ID
	 * @param noticeTitle	push消息标题
	 * @param noticeContent	push消息内容
	 * @param noticeVersion 通告单版本
	 * @param userIds	用户ID，多个ID用,隔开
	 * @param isAll	是否是发给所有人
	 * @param needFeedback	是否需要回复意见
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/publishNotice")
	public Map<String, Object> publishNotice(HttpServletRequest request, String noticeId, String noticeTitle, 
			String noticeContent, String userIds, Boolean isAll, Boolean needFeedback, Boolean publishNotice) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		String crewId = getCrewId(request);
		
		UserInfoModel loginUserInfo = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);
		String loginUserId = loginUserInfo.getUserId();
		
		String noticeName = "";
		try {
			if (StringUtils.isBlank(userIds)) {
				throw new IllegalArgumentException("请勾选接受消息的剧组人员");
			}
			
			if (publishNotice == null) {
				publishNotice = true;
			}
			
			NoticeInfoModel noticeInfoModel = this.noticeService.queryNoticeInfoModelById(noticeId);
			if(noticeInfoModel != null) {
				noticeName = noticeInfoModel.getNoticeName();
			}
			
			//发布通告单,并向选择的用户推送通告单消息
			this.noticeService.publishNotice(crewId, loginUserId, crewInfo.getCrewName(), noticeId, noticeTitle, 
					noticeContent, userIds, isAll, needFeedback, publishNotice);
			message = "发布成功!";
			
			this.sysLogService.saveSysLog(request, "发布通告单", terminal, NoticeInfoModel.TABLE_NAME, noticeName, 2);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message);
		} catch (Exception e) {
			message = "未知异常，消息推送失败";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "发布通告单失败：" + e.getMessage(), terminal, NoticeInfoModel.TABLE_NAME, noticeName, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 上传通告单图片
	 * 通过base64Str传入文件的base64编码
	 * @param file	图片文件
	 * @param request	
	 * @param noticeId	通告单ID
	 * @param base64Str	base64文件编码
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/uploatNoticeImgBase64")
	public @ResponseBody Map<String, Object> uploatNoticeImgBase64(HttpServletRequest request, String noticeId, String base64Str) throws IOException{
		String crewId = getCrewId(request);
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String baseStorePath = properties.getProperty("fileupload.path");
			String storePath = baseStorePath + "notice";
			
			//大图
			Map<String, String> fileMap = FileUtils.saveBase64Img(storePath, base64Str);
			String fileStoreName = fileMap.get("fileStoreName");
			String fileRealName = "自动生成图片";
			String realStorePath = fileMap.get("storePath");
			
			
			//小图
			String smallImgPath = realStorePath + "/small/" + fileStoreName + "_small.png";
			BufferedImage newImage = FileUtils.getNewImage(null, new File(realStorePath + fileStoreName), 768, 320);
			File destFile = new File(smallImgPath);
			FileUtils.makeDir(destFile);
            ImageIO.write(newImage, "png", destFile);
			
            
			NoticeTimeModel noticeTime = this.noticeService.queryNoticeTimeByNoticeId(noticeId);
			if (noticeTime != null) {
//				noticeTime.setPictureUrl(realStorePath + fileStoreName);
//				noticeTime.setSmallPicurl(smallImgPath);
				
				String imgId = UUIDUtils.getId();
				
				NoticePictureModel noticePicture = new NoticePictureModel();
	            noticePicture.setId(imgId);
	            noticePicture.setCrewId(crewId);
	            noticePicture.setNoticeId(noticeId);
	            noticePicture.setNoticeVersion(this.sdf3.format(noticeTime.getUpdateTime()));
	            noticePicture.setName(fileRealName);
	            noticePicture.setBigPicurl(realStorePath + fileStoreName);
	            noticePicture.setSmallPicurl(smallImgPath);
	            noticePicture.setUploadTime(new Date());
	            
				this.noticeService.addNoticePicture(noticePicture);
				
				resultMap.put("imgId", imgId);
			}
			
			resultMap.put("storePath", FileUtils.genPreviewPath(realStorePath + fileStoreName));
			resultMap.put("smallImgPath", FileUtils.genPreviewPath(smallImgPath));
			resultMap.put("imgName", fileRealName);
			
			success = true;
			message = "上传成功";
		} catch (Exception e) {
			message = "未知异常，上传通告单图片失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 获取待销场的场景信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryToCancelViewList")
	public Map<String, Object> queryToCancelViewList(HttpServletRequest request, String noticeId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请选择通告单!");
			}
			String crewId = getCrewId(request);
			//根据通告单id获取零时销场信息
			List<TmpCancelViewInfoModel> tmpCancelViewList = this.clipSerivce.queryTmpCancelInfoByCrewNoticeId(crewId, noticeId);
			//查询通告单中的场景表信息
			List<Map<String, Object>> noticeViewList = viewService.queryNoticeViewList(crewId, null, noticeId, null);
			
			List<String> noticeViewSeriesViewNoList = new ArrayList<String>();
			for (Map<String, Object> noticeViewMap : noticeViewList) {
				int seriesNo = (Integer) noticeViewMap.get("seriesNo");
				String viewNo = (String) noticeViewMap.get("viewNo");
				
				String seriewViewNo = seriesNo + "-" + viewNo.toLowerCase();
				if (!noticeViewSeriesViewNoList.contains(seriewViewNo)) {
					noticeViewSeriesViewNoList.add(seriewViewNo);
				}
			}
			List<TmpCancelViewInfoModel> newAddViewInfoList = new ArrayList<TmpCancelViewInfoModel>();
			for (TmpCancelViewInfoModel tmp : tmpCancelViewList) {
				int seriesNo = tmp.getSeriesNo();
				String viewNo = tmp.getViewNo();
				
				String seriewViewNo = seriesNo + "-" + viewNo.toLowerCase();
				if (!noticeViewSeriesViewNoList.contains(seriewViewNo)) {
					newAddViewInfoList.add(tmp);
				}
			}
			
			resultMap.put("tmpCancelViewList", tmpCancelViewList);
			resultMap.put("noticeViewList", noticeViewList);
			resultMap.put("newAddViewInfoList", newAddViewInfoList);
			message = "查询成功!";
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，获取信息失败";
			logger.error("未知异常，获取信息失败", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 校验是否需要提示用户同步场景单销场信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/checkNeedAlarm")
	public Map<String, Object> checkNeedAlarm(HttpServletRequest request, String noticeId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			String crewId = getCrewId(request);
			
			boolean needAlarm = true;
			List<TmpCancelViewInfoModel> tmpCancelViewList = this.clipSerivce.queryTmpCancelInfoByCrewNoticeId(crewId, noticeId);
			if (tmpCancelViewList == null || tmpCancelViewList.size() == 0) {
				needAlarm = false;
			}
			
			List<Map<String, Object>> noticeViewList = viewService.queryNoticeViewList(crewId, null, noticeId, null);
			for (Map<String, Object> noticeViewMap : noticeViewList) {
				if (noticeViewMap.get("shootStatus") != null) {
					needAlarm = false;
					break;
				}
			}
			
			resultMap.put("needAlarm", needAlarm);
			message= "查询成功!";
		} catch (Exception e) {
			success = false;
			message = "未知异常，获取信息失败";
			logger.error("未知异常，获取信息失败", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 统筹确认销场操作
	 * @param tmpCancelViewIds 临时销场信息ID，多个以英文逗号隔开
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/makeSureCancelView")
	public Map<String, Object> makeSureCancelView(HttpServletRequest request, String noticeId, String seriesViewNos) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			String crewId = getCrewId(request);
			if (StringUtils.isBlank(seriesViewNos)) {
				throw new IllegalArgumentException("请勾选需要操作的场");
			}
			this.noticeService.makeSureCancelView(crewId, noticeId, seriesViewNos);
			
			List<Map<String, Object>> viewList = viewService.queryNoticeViewList(crewId, null, noticeId, null);
			resultMap.put("viewList", viewList);
			
			
			success = true;
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(ie.getMessage(), ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，销场失败";
			logger.error("未知异常，销场失败", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
		
	/**
	 * 查询通告单push后的反馈信息
	 * @param request
	 * @param noticeId
	 * @param noticeVersion
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryNoticeFedBackInfo")
	public Map<String, Object> queryNoticeFedBackInfo(HttpServletRequest request, String noticeId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			String crewId = getCrewId(request);
			
			NoticeTimeModel noticeTime = this.noticeService.queryNoticeTimeByNoticeId(noticeId);
			if (noticeTime == null) {
				throw new IllegalArgumentException("暂无反馈信息");
			}
			
			if (noticeTime != null) {
				String version = this.sdf3.format(noticeTime.getUpdateTime());
				
				int totalCount = 0;
				int hasFinishLookCount = 0;
				
				List<Map<String, Object>> fedBackList = this.noticeService.queryFedBackInfoByNoticeInfo(crewId, noticeId, version);
				for (Map<String, Object> fedbackInfoMap : fedBackList) {
					Date statusUpdateTime = (Date) fedbackInfoMap.get("statusUpdateTime");
					String statusUpdateTimeStr = this.sdf2.format(statusUpdateTime);
					fedbackInfoMap.put("statusUpdateTime", statusUpdateTimeStr);
					
					int backStatus = (Integer) fedbackInfoMap.get("backStatus");
					totalCount++;
					if (backStatus == Constants.NOTICE_FEDBACK_STATUS_BACKED) {
						hasFinishLookCount++;
					}
				}
				resultMap.put("fedBackList", fedBackList);
				resultMap.put("totalCount", totalCount);
				resultMap.put("hasFinishLookCount", hasFinishLookCount);
			}
			
			message = "查询成功!";
		} catch (Exception e) {
			success = false;
			message = "未知异常，获取反馈信息失败";
			logger.error("未知异常，获取反馈信息失败");
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询通告单的历史版本信息
	 * @param request
	 * @param noticeId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryOldVersionNoticeInfo")
	public Map<String, Object> queryOldVersionNoticeInfo(HttpServletRequest request, String noticeId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			//根据id查询出当前的通告单信息
			NoticeTimeModel noticeTime = this.noticeService.queryNoticeTimeByNoticeId(noticeId);
			if (noticeTime != null) {
				String version = this.sdf3.format(noticeTime.getUpdateTime());
				//查询出以往的通告单信息列表
				List<NoticePictureModel> oldVersionPicList = this.noticeService.queryOldVersionPicture(noticeId, version);
				Map<String, List<NoticePictureModel>> versionGroup = new LinkedHashMap<String, List<NoticePictureModel>>();
				
				for (NoticePictureModel noticePicture : oldVersionPicList) {
					String noticeVersion = noticePicture.getNoticeVersion();
					
					if (versionGroup.containsKey(noticeVersion)) {
						List<NoticePictureModel> versionPicList = versionGroup.get(noticeVersion);
						versionPicList.add(noticePicture);
					} else {
						List<NoticePictureModel> versionPicList = new ArrayList<NoticePictureModel>();
						versionPicList.add(noticePicture);
						versionGroup.put(noticeVersion, versionPicList);
					}
				}
				
				resultMap.put("versionGroup", versionGroup);
			}
			
			success = true;
		} catch (Exception e) {
			success = false;
			message = "未知异常，获取历史版本失败";
			logger.error("未知异常，获取历史版本失败");
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	
	/**
	 * 获取剧组下有手机端通告单权限的用户信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryCrewMobileUserWithNoticeAuth")
	public Map<String, Object> queryCrewMobileUserWithNoticeAuth(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = false;
		String message = "";
		try {
			String crewId = getCrewId(request);
			List<String> authList = new ArrayList<String>();
			authList.add(AuthorityConstants.NOTICE);
			
			List<Map<String, Object>> resultUserList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> userInfoList = this.userService.queryUserByCrewIdAndAuth(crewId, authList, null);
			//去重list
			List<String> noReapeatList = new ArrayList<String>();
			for (Map<String, Object> userInfo : userInfoList) {
				String userId = (String) userInfo.get("userId");
				if (!noReapeatList.contains(userId)) {
					noReapeatList.add(userId);
					resultUserList.add(userInfo);
				}
			}
			resultMap.put("userList", resultUserList);
			
			success = true;
			message = "查询成功";
		} catch (Exception e) {
			success = false;
			message = "未知异常，获取用户信息失败";
			logger.error("未知异常，获取用户信息失败", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 删除通告单图片
	 * @param request
	 * @param imgId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteNoticeImg")
	public Map<String, Object> deleteNoticeImg(HttpServletRequest request, String imgId) {
		String crewId = getCrewId(request);
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			this.noticeService.deleteNoticeImg(crewId, imgId);
			
			success = true;
			message = "删除成功";
		} catch (Exception e) {
			success = false;
			message = "未知异常，删除图片失败";
			logger.error("未知异常，删除图片失败", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	
	/**
	 * 根据场景id查询出通告单的id
	 * @param request
	 * @param viewIds 场景id的字符串 多个场景以","分割
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryNoticeListByViewId")
	public Map<String, Object> queryNoticeListByViewId(HttpServletRequest request, String viewIds){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(viewIds)) {
				throw new IllegalArgumentException("请输入需要查询的场景号");
			}
			
			String crewId = getCrewId(request);
			
			String[] split = viewIds.split(",");
			for (String viewId : split) {
				List<Map<String,Object>> list = noticeService.qureyNoticeListByViewId(viewId,crewId);
				resultMap.put("noticeList", list);
			}
			message = "查询成功！";
		}catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，查询失败！";
			success = false;
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 获取分享通告单的主要详细信息接口
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	@RequestMapping("/appIndex/queryNoticeShareData")
	@ResponseBody
	public Map<String, Object> queryNoticeShareData(HttpServletRequest request, String crewId, String noticeId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("未获取到通告单信息");
			}
			
			//剧组通告时间信息
			NoticeTimeDto noticeTimeDto = new NoticeTimeDto();
			//通告单演员信息
			List<NoticeRoleTimeDto> noticeRoleTimeDtoList = new ArrayList<NoticeRoleTimeDto>();
			//场次信息
			List<LocationViewDto> locationViewDtoList = new ArrayList<LocationViewDto>();
			
			
			NoticeInfoModel noticeInfo = this.noticeService.getNotice(noticeId);
			if (noticeInfo == null) {
				throw new IllegalArgumentException("不存在的通告单");
			}
			//场次列表信息
			List<Map<String, Object>> viewList = this.viewInfoService.queryNoticeViewList(crewId, null, noticeId, null);
			
			String viewIds = "";
			for (Map<String, Object> viewInfo : viewList) {
				viewIds+=viewInfo.get("viewId")+",";
			}
			if (!StringUtils.isBlank(viewIds)) {
				viewIds.substring(0, viewIds.length()-1);
			}
			
			//通告单时间信息
			noticeTimeDto = this.genNoticeTimeDto(noticeInfo, crewId, viewList);
			//统计信息
			String statistics = this.genStatisticsInfo(viewList);
			noticeTimeDto.setStatistics(statistics);
			
			//演员信息
			noticeRoleTimeDtoList = this.genMainRoleInfo(viewIds, noticeId);
			
			/*
			 * 按照拍摄地分组的场景信息
			 */
			if (viewList != null && viewList.size() > 0) {
				locationViewDtoList = this.genLocationViewInfo(crewId, noticeId, viewList);
			}
			
			//演员通告单信息
			Map<String, Object> actorNoticeInfo = this.genActorNoticeInfo(crewId, userId, noticeId);
			
			//是否需要反馈
			boolean needFedback = false;
			boolean isFedback = false;
			String version = this.sdf3.format(this.sdf2.parse(noticeTimeDto.getNoticeTimeUpdateTime()));
			NoticePushFedBackModel toBackInfo = this.noticeService.queryToBackInfoByNoticeInfo(crewId, noticeId, version, userId);
			if (toBackInfo != null && toBackInfo.getNeedFedBack()) {
				needFedback = true;
			}
			if (toBackInfo == null) {
				isFedback = true;
			}
			
			resultMap.put("needFedback", needFedback);
			resultMap.put("isFedback", isFedback);
			
			resultMap.put("noticeTime", noticeTimeDto);
			resultMap.put("noticeRoleTimeList", noticeRoleTimeDtoList);
			resultMap.put("locationViewList", locationViewDtoList);
			resultMap.put("actorNoticeInfo", actorNoticeInfo);
			
			this.sysLogService.saveSysLogForApp(request, "查询分享通告单的主要详细信息", userInfo.getClientType(), NoticeInfoModel.TABLE_NAME, noticeId, 0);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
			
		} catch (Exception e) {
			logger.error("未知异常，查询通告单详细信息失败", e);
			success = false;
			message = "未知异常，查询通告单详细信息失败";
			this.sysLogService.saveSysLogForApp(request, "查询分享通告单的主要详细信息失败：" + e.getMessage(), userInfo.getClientType(), NoticeInfoModel.TABLE_NAME, noticeId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	/**
	 * 生成通告单时间信息
	 * @param noticeTimeDto
	 * @param noticeInfo
	 * @param lastNoticeTime
	 * @param list
	 * @throws Exception 
	 */
	private NoticeTimeDto genNoticeTimeDto(NoticeInfoModel noticeInfo, String crewId, List<Map<String, Object>> list) throws Exception {
		NoticeTimeDto noticeTimeDto = new NoticeTimeDto();
		
		NoticeTimeModel myNoticeTime = this.noticeService.queryNoticeTimeByNoticeId(noticeInfo.getNoticeId());
		//通告单日期是周几
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(noticeInfo.getNoticeDate());
//		int month = calendar.get(Calendar.MONTH);
//		if(month == 12){
//			month = 1;
//		} else {
//			month++;
//		}
		//通告单在一周中的某天
		Map<Integer, String> dayOfWeekMap = new HashMap<Integer, String>();
		dayOfWeekMap.put(1, "一");
		dayOfWeekMap.put(2, "二");
		dayOfWeekMap.put(3, "三");
		dayOfWeekMap.put(4, "四");
		dayOfWeekMap.put(5, "五");
		dayOfWeekMap.put(6, "六");
		dayOfWeekMap.put(7, "七");
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		dayOfWeek = dayOfWeek - 1;
		if(dayOfWeek == 0){
			dayOfWeek = 7;
		}
		noticeTimeDto.setWeekday("星期" + dayOfWeekMap.get(dayOfWeek));
		
		//通告单开机天数
		NoticeInfoModel firstNoticeInfo = noticeService.getFirstNotice(noticeInfo.getCrewId());	//第一封通告单
		noticeTimeDto.setShootDays(DateUtils.daysBetween(firstNoticeInfo.getNoticeDate(), noticeInfo.getNoticeDate()));
		
		//总场数
		noticeTimeDto.setTotalViewnum(list.size());
		//总页数
		BigDecimal pageCount=new BigDecimal(0);
		for(Map<String, Object> viewMap:list){
			pageCount=pageCount.add(new BigDecimal((Double)viewMap.get("pageCount")));
		}
		noticeTimeDto.setTotalPagenum(pageCount.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		
		noticeTimeDto.setNoticeName(noticeInfo.getNoticeName());
		noticeTimeDto.setVersion(myNoticeTime.getVersion());
		noticeTimeDto.setGroupDirector(myNoticeTime.getGroupDirector());
		if (myNoticeTime != null) {
			//早餐时间
			noticeTimeDto.setBreakfastTime(myNoticeTime.getBreakfastTime());
			//出发时间
			noticeTimeDto.setDepartureTime(myNoticeTime.getDepartureTime());
			//拍摄地点
			noticeTimeDto.setShootLocationInfos(myNoticeTime.getShootLocationInfos());
			//天气情况
			noticeTimeDto.setWeatherInfo(myNoticeTime.getWeatherInfo());
			//人员调度
			noticeTimeDto.setRoleInfo(myNoticeTime.getRoleInfo());
			//提示信息
			noticeTimeDto.setNote(myNoticeTime.getNote());
			//其他提示
			noticeTimeDto.setOtherTips(myNoticeTime.getRoleConvertRemark());
			//备注
			noticeTimeDto.setRemark(myNoticeTime.getRemark());
			//商植
			noticeTimeDto.setInsideAdvert(myNoticeTime.getInsideAdvert());
			//联系人
			noticeTimeDto.setNoticeContact(myNoticeTime.getNoticeContact());
			
			//通告单图片
			List<PictureDto> pictureDtoList = new ArrayList<PictureDto>();
			List<NoticePictureModel> noticePictureList = this.noticeService.queryNoticeImgByNotice(noticeInfo.getNoticeId(), this.sdf3.format(myNoticeTime.getUpdateTime()));
			for (NoticePictureModel noticePic : noticePictureList) {
				PictureDto pictureDto = new PictureDto();
				pictureDto.setName(noticePic.getName());
				pictureDto.setUploadTime(this.sdf2.format(noticePic.getUploadTime()));

				//大图片地址
				pictureDto.setBigPicurl(FileUtils.genPreviewPath(noticePic.getBigPicurl()));
				//小图片地址
				pictureDto.setSmallPicurl(FileUtils.genPreviewPath(noticePic.getSmallPicurl()));
				pictureDtoList.add(pictureDto);
			}
			noticeTimeDto.setPictureInfo(pictureDtoList);
			
			//发布的通告单最后修改时间
			Date noticeTimeUpdateTime = myNoticeTime.getUpdateTime();
			String noticeTimeUpdateTimeStr = this.sdf2.format(noticeTimeUpdateTime);
			noticeTimeDto.setNoticeTimeUpdateTime(noticeTimeUpdateTimeStr);
		}
		
		return noticeTimeDto;
	}
	
	/**
	 * 生成演员通告单信息
	 * @param userId
	 * @param noticeId
	 * @return
	 * @throws Exception 
	 */
	private Map<String, Object> genActorNoticeInfo (String crewId, String userId, String noticeId) throws Exception {
		/*
		 * 简单校验
		 */
		NoticeInfoModel noticeInfo = this.noticeService.getNotice(noticeId);
		if (noticeInfo == null) {
			throw new IllegalArgumentException("不存在的通告单");
		}
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		//查询用户扮演的角色信息(如果用户没有关联主要角色，则返回空)
		List<ViewRoleModel> userRoleList = this.viewRoleService.queryUserRoleInfo(crewId, userId);
		List<String> userRoleIds = new ArrayList<String>();
		List<String> userRoleNameList = new ArrayList<String>();
		for (ViewRoleModel viewRole : userRoleList) {
			if (viewRole.getViewRoleType() == ViewRoleType.MajorActor.getValue()) {
				userRoleIds.add(viewRole.getViewRoleId());
				userRoleNameList.add(viewRole.getViewRoleName());
			}
		}
		if (userRoleIds.size() == 0 || userRoleList == null || userRoleList.size() == 0) {
			return null;
		}
		
		//查询用户拥有的角色在该通告单中的有戏的场次信息，如果为空，表示演员在该通告单中没戏
		List<Map<String, Object>> viewList = this.viewInfoService.queryViewByNoticeRole(crewId, noticeId, userRoleIds);
		if (viewList == null || viewList.size() == 0) {
			return null;
		}
		String viewNos = "";	//获取场次列表信息
		List<String> cooperatorList = new ArrayList<String>();	//获取搭戏人列表
		List<String> hasViewRoleNameList = new ArrayList<String>();	//用户在通告单中所有场景中扮演的角色
		for (Map<String, Object> viewMap : viewList) {
			Integer seriesNo = (Integer) viewMap.get("seriesNo");
			String viewNo = (String) viewMap.get("viewNo");
			String viewRoleNames = (String) viewMap.get("viewRoleNames");
			
			String seriesViewNo = seriesNo + "-" + viewNo;
			viewNos += seriesViewNo + ",";
			if (!StringUtils.isBlank(viewRoleNames)) {
				String[] viewRoleNameArr = viewRoleNames.split(",");
				for (String viewRoleName : viewRoleNameArr) {
					if (!userRoleNameList.contains(viewRoleName) && !cooperatorList.contains(viewRoleName)) {
						cooperatorList.add(viewRoleName);
					}
					if (!hasViewRoleNameList.contains(viewRoleName) && userRoleNameList.contains(viewRoleName)) {
						hasViewRoleNameList.add(viewRoleName);
					}
				}
			}
		}
		String cooperators = "";
		for (String cooperator : cooperatorList) {
			cooperators += cooperator + ",";
		}
		String userRoleNames = "";
		for (String userRoleName : hasViewRoleNameList) {
			userRoleNames += userRoleName + ",";
		}
		
		//查询用户拥有的角色在通告单中的化妆信息，如果有多个角色，以到场时间最早的为主
		NoticeRoleTimeModel noticeRoleTime = this.noticeService.queryNoticeRoleTimeByNoticeIdAndRoleId(noticeId, userRoleIds);
		//化妆信息
		String makeup = "";
		String arriveTime = "";
		String giveMakeupTime = "";
		if (noticeRoleTime != null) {
			makeup = noticeRoleTime.getMakeup();
			arriveTime = noticeRoleTime.getArriveTime();
			giveMakeupTime = noticeRoleTime.getGiveMakeupTime();
		}
		
		
		//查询通告单下的所有拍摄地点信息，按照场景中拍摄地点先后排序
		List<SceneViewInfoModel> shootLocationList = this.sceneViewInfoService.queryShootLocationByNoticeId(crewId, noticeId);
		//转场信息
		String shootLocations = "";
		if (shootLocationList != null && shootLocationList.size() != 0) {
			for (SceneViewInfoModel shootLocation : shootLocationList) {
				String location = shootLocation.getVName();
				if(StringUtils.isNotBlank(location)){
					shootLocations += location + ",";
				}
			}
		}
		
		
		
		//处理返回数据
		if (!StringUtils.isBlank(userRoleNames)) {
			userRoleNames = userRoleNames.substring(0, userRoleNames.length() - 1);
		}
		if (!StringUtils.isBlank(viewNos)) {
			viewNos = viewNos.substring(0, viewNos.length() - 1);
		}
		if (!StringUtils.isBlank(cooperators)) {
			cooperators = cooperators.substring(0, cooperators.length() - 1);
		}
		if (!StringUtils.isBlank(shootLocations)) {
			shootLocations = shootLocations.substring(0, shootLocations.length() - 1);
//			shootLocations = "今天的拍摄地依次为" + shootLocations;
		}
		resultMap.put("roleNames", userRoleNames);
		resultMap.put("viewNos", viewNos);
		resultMap.put("converLocationInfo", shootLocations);
		resultMap.put("cooperators", cooperators);
		resultMap.put("makeup", makeup);
		resultMap.put("arriveTime", arriveTime);
		resultMap.put("giveMakeupTime", giveMakeupTime);
		
		return resultMap;
	}
	
	/**
	 * 生成气氛内外景的统计信息
	 * @return
	 */
	private String genStatisticsInfo(List<Map<String, Object>> list) {
		DecimalFormat df = new DecimalFormat("0.00");
		String statistics = "";
		
		List<Map<String, Object>> innerSiteViewList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> outerSiteViewList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> inoutSiteViewList = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> viewMap:list){
			//计算气氛/内外景统计信息
			String site = (String) viewMap.get("site");
			if (Arrays.asList(Constants.INNERSITEARRAY).contains(site)) {
				innerSiteViewList.add(viewMap);
			}
			if (Arrays.asList(Constants.OUTERSITEARRAY).contains(site)) {
				outerSiteViewList.add(viewMap);
			}
			if (Arrays.asList(Constants.INOUTSITEARRAY).contains(site)) {
				inoutSiteViewList.add(viewMap);
			}
		}
		
		//内景统计
		Map<String, List<Map<String, Object>>> innerSiteAtmosph = new HashMap<String, List<Map<String, Object>>>();
		for (Map<String, Object> viewMap : innerSiteViewList) {
			String atmosphereName = (String) viewMap.get("atmosphereName");
			if (innerSiteAtmosph.containsKey(atmosphereName)) {
				innerSiteAtmosph.get(atmosphereName).add(viewMap);
			} else {
				List<Map<String, Object>> viewList = new ArrayList<Map<String, Object>>();
				viewList.add(viewMap);
				innerSiteAtmosph.put(atmosphereName, viewList);
			}
		}
		Set<String> innerSiteKeySet = innerSiteAtmosph.keySet();
		for (String innerAtmosph : innerSiteKeySet) {
			List<Map<String, Object>> myViewList = innerSiteAtmosph.get(innerAtmosph);
			double pageCount = 0;
			for (Map<String, Object> view : myViewList) {
				pageCount += (Double) view.get("pageCount");
			}
			
			if (StringUtils.isBlank(innerAtmosph)) {
				innerAtmosph = "";
			}
			
			statistics += myViewList.size() + "场" + innerAtmosph + "内" + df.format(pageCount) + "页\n";
		}
		
		
		//外景统计
		Map<String, List<Map<String, Object>>> outSiteAtmosph = new HashMap<String, List<Map<String, Object>>>();
		for (Map<String, Object> viewMap : outerSiteViewList) {
			String atmosphereName = (String) viewMap.get("atmosphereName");
			if (outSiteAtmosph.containsKey(atmosphereName)) {
				outSiteAtmosph.get(atmosphereName).add(viewMap);
			} else {
				List<Map<String, Object>> viewList = new ArrayList<Map<String, Object>>();
				viewList.add(viewMap);
				outSiteAtmosph.put(atmosphereName, viewList);
			}
		}
		Set<String> outSiteKeySet = outSiteAtmosph.keySet();
		for (String outAtmosph : outSiteKeySet) {
			List<Map<String, Object>> myViewList = outSiteAtmosph.get(outAtmosph);
			double pageCount = 0;
			for (Map<String, Object> view : myViewList) {
				pageCount += (Double) view.get("pageCount");
			}
			
			if (StringUtils.isBlank(outAtmosph)) {
				outAtmosph = "";
			}
			
			statistics += myViewList.size() + "场" + outAtmosph + "外" + df.format(pageCount) + "页\n";
		}
		
		//内外景统计
		Map<String, List<Map<String, Object>>> inoutSiteAtmosph = new HashMap<String, List<Map<String, Object>>>();
		for (Map<String, Object> viewMap : inoutSiteViewList) {
			String atmosphereName = (String) viewMap.get("atmosphereName");
			if (inoutSiteAtmosph.containsKey(atmosphereName)) {
				inoutSiteAtmosph.get(atmosphereName).add(viewMap);
			} else {
				List<Map<String, Object>> viewList = new ArrayList<Map<String, Object>>();
				viewList.add(viewMap);
				inoutSiteAtmosph.put(atmosphereName, viewList);
			}
		}
		Set<String> inoutSiteKeySet = inoutSiteAtmosph.keySet();
		for (String inoutAtmosph : inoutSiteKeySet) {
			List<Map<String, Object>> myViewList = inoutSiteAtmosph.get(inoutAtmosph);
			double pageCount = 0;
			for (Map<String, Object> view : myViewList) {
				pageCount += (Double) view.get("pageCount");
			}
			
			if (StringUtils.isBlank(inoutAtmosph)) {
				inoutAtmosph = "";
			}
			
			statistics += myViewList.size() + "场" + inoutAtmosph + "内外" + df.format(pageCount) + "页\n";
		}
		
		
		return statistics;
	}
	
	/**
	 * 生成通告单场景信息
	 * 返回的场景信息按照拍摄地点分组
	 * @param crewId
	 * @param noticeId
	 * @param locationViewDtoList
	 * @param viewList
	 * @param pageCount
	 * @return
	 * @throws Exception
	 */
	private List<LocationViewDto> genLocationViewInfo(String crewId, String noticeId, List<Map<String, Object>> viewList)
			throws Exception {
		List<LocationViewDto> locationViewDtoList = new ArrayList<LocationViewDto>();
		
		String preShootLocation = "";
		String preShootLocationId = "";
		String locationViewIds = "";
		
		LocationViewDto locationViewDto = new LocationViewDto();
		List<ViewInfoDto> viewInfoDtoList = new ArrayList<ViewInfoDto>();
		
		for(Map<String, Object> viewMap : viewList){
			ViewInfoDto viewInfoDto = new ViewInfoDto();
			if (viewMap.get("seriesNo") != null && !viewMap.get("seriesNo").equals("")) {
				viewInfoDto.setSeriesNo((Integer) viewMap.get("seriesNo"));
			}
			if (viewMap.get("viewNo") != null) {
				viewInfoDto.setViewNo((String) viewMap.get("viewNo"));
			}
			if (viewMap.get("viewAddress") != null) {
				viewInfoDto.setViewLocation((String) viewMap.get("viewAddress"));
			}
			if (viewMap.get("pageCount") != null && !viewMap.get("pageCount").equals("")) {
				viewInfoDto.setPageCount((Double)viewMap.get("pageCount"));
			}
			if (viewMap.get("atmosphereName") != null) {
				viewInfoDto.setAtmosphereName((String) viewMap.get("atmosphereName"));
			}
			if (viewMap.get("site") != null) {
				viewInfoDto.setSite((String) viewMap.get("site"));
			}
			if (viewMap.get("viewType") != null) {
				viewInfoDto.setViewType((Integer) viewMap.get("viewType"));
			}
			if (viewMap.get("mainContent") != null) {
				viewInfoDto.setMainContent((String) viewMap.get("mainContent"));
			}
			if (viewMap.get("roleList") != null) {
				//主要演员
				String roleList = (String) viewMap.get("roleList");
				viewInfoDto.setMainRoleNames(roleList);
			}
			if (viewMap.get("roleShortNames") != null) {
				String roleShortNames = (String) viewMap.get("roleShortNames");
				viewInfoDto.setMainRoleShortNames(roleShortNames);
			}
			if (viewMap.get("guestRoleList") != null) {
				viewInfoDto.setGuestRoleNames((String) viewMap.get("guestRoleList"));
			}
			if (viewMap.get("massRoleList") != null) {
				viewInfoDto.setMassRoleNames((String) viewMap.get("massRoleList"));
			}
			if (viewMap.get("clothesName") != null) {
				viewInfoDto.setClothesNames((String) viewMap.get("clothesName"));
			}
			if (viewMap.get("makeupName") != null) {
				viewInfoDto.setMakeupNames((String) viewMap.get("makeupName"));
			}
			if (viewMap.get("propsList") != null) {
				viewInfoDto.setPropNames((String) viewMap.get("propsList"));
			}
			if (viewMap.get("specialPropsList") != null) {
				viewInfoDto.setSpecialPropName((String) viewMap.get("specialPropsList"));
			}
			if (viewMap.get("viewRemark") != null) {
				viewInfoDto.setRemark((String) viewMap.get("viewRemark"));
			}
			if (viewMap.get("advertName") != null) {
				viewInfoDto.setInsertAdverts((String) viewMap.get("advertName"));
			}
			
			if (StringUtils.isBlank(preShootLocation)) {	//第一场信息
				viewInfoDtoList.add(viewInfoDto);
				locationViewIds += viewMap.get("viewId") + ",";
				
			} else if (!preShootLocation.equals((String) viewMap.get("shootLocation"))) {
				//此处开始转场
				locationViewIds = locationViewIds.substring(0, locationViewIds.length() - 1);
				ConvertAddressModel convertAddress = this.convertAddressService.queryByLocationViewIds(crewId, noticeId, preShootLocationId, locationViewIds);
				
				locationViewDto.setShootLocation(preShootLocation);
				locationViewDto.setViewInfoList(viewInfoDtoList);
				if (convertAddress != null) {
					String convertRemark = convertAddress.getRemark();
					convertRemark = convertRemark == null ? "" : convertRemark;
					
					locationViewDto.setConvertRemark(convertRemark);
				}
				locationViewDtoList.add(locationViewDto);
				
				locationViewDto = new LocationViewDto();
				viewInfoDtoList = new ArrayList<ViewInfoDto>();
				locationViewIds = "";
				viewInfoDtoList.add(viewInfoDto);
				locationViewIds += viewMap.get("viewId") + ",";
			} else {
				viewInfoDtoList.add(viewInfoDto);
				locationViewIds += viewMap.get("viewId") + ",";
			}
			
			//上一场实拍摄地
			preShootLocation = (String) viewMap.get("shootLocation");
			preShootLocationId = (String) viewMap.get("shootLocationId");
		}
		
		//处理最后一组实拍景地
		locationViewIds = locationViewIds.substring(0, locationViewIds.length() - 1);
		ConvertAddressModel convertAddress = this.convertAddressService.queryByLocationViewIds(crewId, noticeId, preShootLocationId, locationViewIds);
		locationViewDto.setShootLocation(preShootLocation);
		locationViewDto.setViewInfoList(viewInfoDtoList);
		if (convertAddress != null) {
			String convertRemark = convertAddress.getRemark();
			convertRemark = convertRemark == null ? "" : convertRemark;
			
			locationViewDto.setConvertRemark(convertRemark);
		}
		locationViewDtoList.add(locationViewDto);
		
		return locationViewDtoList;
	}

	/**
	 * 生成通告单下演员时间表信息
	 * @param viewIds
	 * @return
	 */
	private List<NoticeRoleTimeDto> genMainRoleInfo(String viewIds, String noticeId) {
		List<NoticeRoleTimeDto> noticeRoleTimeDtoList = new ArrayList<NoticeRoleTimeDto>();
		//所有主演信息
		List<Map<String, Object>> roleSignList = this.viewRoleService.queryViewRoleListByNoticeId(noticeId);
		for (Map<String, Object> roleInfo : roleSignList) {
			NoticeRoleTimeDto noticeRoleTimeDto = new NoticeRoleTimeDto();
			noticeRoleTimeDto.setViewRoleName((String) roleInfo.get("viewRoleName"));
			noticeRoleTimeDto.setActorName((String) roleInfo.get("actorName"));
			if (roleInfo.get("shortName") == null) {
				noticeRoleTimeDto.setShortName("√");
			} else {
				noticeRoleTimeDto.setShortName((String) roleInfo.get("shortName"));
			}
			
			noticeRoleTimeDto.setMakeup((String) roleInfo.get("makeup"));
			noticeRoleTimeDto.setArriveTime((String) roleInfo.get("arriveTime"));
			noticeRoleTimeDto.setGiveMakeupTime((String) roleInfo.get("giveMakeupTime"));
			
			noticeRoleTimeDtoList.add(noticeRoleTimeDto);
		}
		
		return noticeRoleTimeDtoList;
	}
	
	/**
	 * 查询最新发布的通告单所在的月
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryLastNoticeMonth")
	public Map<String, Object> queryLastNoticeMonth(HttpServletRequest request, String cancledNotice, Page page){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = getCrewId(request);
			
			//已销场的通告单共有多少天
			List<Map<String, Object>> listDate = this.noticeService.queryNoticeCountByDate(null, null, crewId, cancledNotice);
			resultMap.put("totalCancleCount", listDate.size());
			//计算共有多少张已销场的通告单
			List<Map<String, Object>> cancledNoticeCount = this.noticeService.queryCancledNoticeCount(crewId);
			
			//已销场通告单总数
			resultMap.put("cancledCount", cancledNoticeCount.size());
			
			Map<String, Object> lastNoticeMap = cancledNoticeCount.get(0);
			String noticeMonth = (String) lastNoticeMap.get("noticeMonth");
			resultMap.put("lastNoticeMonth", noticeMonth);
			
			message = "查询成功！";
		} catch (Exception e) {
			message = "未知异常，查询失败";
			success = false;
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 分页查询月份列表
	 * @param request
	 * @param page
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryMonthList")
	public Map<String, Object> queryMonthList(HttpServletRequest request, Integer pagesize, Integer pageNo){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = getCrewId(request);
			
			//计算共有多少张未销场的通告单
			List<Map<String, Object>> notCancledNoticeCount = this.noticeService.queryNotCancledNoticeCount(crewId);
			
			//未销场通告单总数
			resultMap.put("notCancledCount", notCancledNoticeCount.size());
			
			Page page = null;
			if(pagesize != null && pageNo != null) {
				page = new Page();
				page.setPagesize(pagesize);
				page.setPageNo(pageNo);
			}
			
			List<Map<String,Object>> monthList = this.noticeService.queryCancleMonthList(crewId, page);
			int totalNum = 0;
			if(monthList != null && monthList.size() > 0) {
				for(Map<String, Object> monthMap : monthList) {
					totalNum += Integer.parseInt(monthMap.get("noticeNum") + "");
				}
			}			
			if(page != null) {
				resultMap.put("totalCount", page.getPageCount());
			}
			resultMap.put("cancleNoticeMonthList", monthList);
			resultMap.put("totalNum", totalNum);
		} catch (Exception e) {
			success = false;
			message = "未知异常,查询失败";
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 判断当前剧组是否有场景
	 * @param request
	 * @param page
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryViewListByCrewId")
	public Map<String, Object> queryViewListByCrewId(HttpServletRequest request, Page page){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		boolean isHaveView = true;
		
		try {
			String crewId = getCrewId(request);
			List<ViewInfoModel> list = this.viewInfoService.queryByCrewId(crewId, page);
			if (list == null || list.size() == 0) {
				isHaveView = false;
			}
			
		} catch (Exception e) {
			message = "未知错误，查询失败";
			success = false;
			
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		resultMap.put("isHaveView", isHaveView);
		return resultMap;
	}
	
	/**
	 * 根据通告单的id查询出通告单下所有场景的状态；如果为删戏和部分完成则不能删除
	 * @param request
	 * @param noticeId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryViewStatusByNoticeId")
	public Map<String, Object> queryViewStatusByNoticeId(HttpServletRequest request, String noticeId){
		Map<String, Object> resuleMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请选择要删除的通告单");
			}
			
			List<Map<String, Object>> list = this.noticeService.queryViewStatusByNoticeId(noticeId);
			for (Map<String, Object> ViewMap : list) {
				//取出拍摄状态
				Object status = ViewMap.get("shootStatus");
				if (status != null) {
					
					int shootStatus = (Integer) ViewMap.get("shootStatus");
					if (shootStatus == 1 || shootStatus == 4) {
						throw new IllegalArgumentException("该同通告单中有已拍摄的场景，不能删除");
					}else if (shootStatus == 3) {
						throw new IllegalArgumentException("该通告单中有场景已被删戏，不能删除");
					}
				}
			}
			
			//查询当前通告单是否有剪辑信息
			List<Map<String, Object>> cutInfoList = this.cutViewService.queryCutInfoByNoticeIdOrViewId(noticeId, "", crewId);
			if (cutInfoList != null && cutInfoList.size() >0) {
				resuleMap.put("hasCutInfo", true);
			}else {
				resuleMap.put("hasCutInfo", false);
			}
			
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
		} catch (Exception e) {
			message = "未知错误，查询失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resuleMap.put("success", success);
		resuleMap.put("message", message);
		
		return resuleMap;
	}
	
	/**
	 * 设置通告单中的备戏及实际拍摄页数信息
	 * @param request
	 * @param noticeId
	 * @param viewIds 场景id字符串，多个id之间以“,”分割
	 * @param prepareView
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveNoticeViewMapInfo")
	public Map<String, Object> saveNoticeViewMapInfo(HttpServletRequest request, String noticeId, String viewIds, Integer prepareView, String shootPage){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		String noticeName = "";
		try {
			String crewId = this.getCrewId(request);
			
			if (StringUtils.isBlank(viewIds)) {
				throw new IllegalArgumentException("请选择需要设置的场景");
			}
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请选择要查看的通告单");
			}
			
			NoticeInfoModel noticeInfoModel = this.noticeService.queryNoticeInfoModelById(noticeId);
			if(noticeInfoModel != null) {
				noticeName = noticeInfoModel.getNoticeName();
			}
			
			List<ViewNoticeMapModel> updateList = new ArrayList<ViewNoticeMapModel>();
			String[] viewIdArr = viewIds.split(",");
			for (String viewId : viewIdArr) {
				//根据id查询出关系表中的数据
				ViewNoticeMapModel model = this.noticeService.queryViewMapInfo(noticeId, viewId);
				if (model != null) {
					if (prepareView != null) {
						model.setPrepareView(prepareView);
					}
					
					if (StringUtils.isNotBlank(shootPage)) {
						BigDecimal deciaml = new BigDecimal(Double.parseDouble(shootPage)); 
						
						model.setShootPage(deciaml.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
					}
					
					updateList.add(model);
				}
			}
			
			this.noticeService.updateNoticeViewMapInfo(updateList);
			message = "设置成功";
			if(StringUtils.isNotBlank(shootPage)) {
				this.sysLogService.saveSysLog(request, "设置通告单中实际拍摄页数", terminal, NoticeInfoModel.TABLE_NAME, noticeName, SysLogOperType.UPDATE.getValue());
			} else {
				this.sysLogService.saveSysLog(request, "设置通告单中备戏", terminal, NoticeInfoModel.TABLE_NAME, noticeName, SysLogOperType.UPDATE.getValue());
			}
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，设置失败";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "设置通告单中备戏或实际拍摄页数失败：" + e.getMessage(), terminal, NoticeInfoModel.TABLE_NAME, noticeName, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 导出通告单列表页面（支持高级查询）
	 * @param request
	 * @param page
	 * @param forSimple
	 * @param shootLocationStr
	 * @param sceriesViewNo
	 * @param viewTape
	 * @param reamrkStr
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exportNoticeListData")
	public Map<String, Object> exportNoticeListData(HttpServletRequest request,HttpServletResponse response, Page page, Boolean forSimple, 
			String shootLocationStr, String sceriesViewNo, String viewTape, String reamrkStr){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		
		try {
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			
			if (StringUtils.isNotBlank(shootLocationStr)) {
				conditionMap.put("shootLocationStr", shootLocationStr);
			}
			
			if (StringUtils.isNotBlank(sceriesViewNo)) {
				conditionMap.put("sceriesViewNo", sceriesViewNo);
			}
			
			if (StringUtils.isNotBlank(viewTape)) {
				conditionMap.put("viewTape", viewTape);
			}
			
			if (StringUtils.isNotBlank(reamrkStr)) {
				conditionMap.put("remarkInfo", reamrkStr);
			}
			
			List<Map<String, Object>> noticeList = noticeService.queryNoticeListTableData(crewInfo.getCrewId(), page, conditionMap);
			
			//取出每张通告单中的场景
			if (noticeList != null && noticeList.size()>0) {
				for (Map<String, Object> noticeListMap : noticeList) {
					//取出通告单中主要演员的信息
					String mainRole = (String) noticeListMap.get("mainrole");
					//取出通告单中特约演员的信息
					String guestRole = (String) noticeListMap.get("guestrole");
					//取出通告单中的时间
					Date noticeDate = (Date) noticeListMap.get("noticeDate");
					
					if (!StringUtils.isBlank(mainRole)) {
						String[] mainRoleArr = mainRole.split(",");
						List<String> mainRoleList = new ArrayList<String>();
						mainRole = "";
						
						//使用list集合去除主要演员的重复数据
						for (String mainRoleStr : mainRoleArr) {
							if (!mainRoleList.contains(mainRoleStr)) {
								mainRoleList.add(mainRoleStr);
								mainRole += mainRoleStr + "，";
							}
						}
						
						if (!StringUtils.isBlank(mainRole)) {
							mainRole = mainRole.substring(0, mainRole.length() - 1);
						}
						//将去重之后的主要演员的信息重新放进map中
						noticeListMap.remove("mainrole");
						noticeListMap.put("mainrole", mainRole);
					}
					
					//对特约演员的数据进行去重
					if (!StringUtils.isBlank(guestRole)) {
						String[] guestRoleArr = guestRole.split(",");
						
						List<String> guestRoleList = new ArrayList<String>();
						guestRole = "";
						for (String guestRoleStr : guestRoleArr) {
							if (!guestRoleList.contains(guestRoleStr)) {
								guestRoleList.add(guestRoleStr);
								guestRole += guestRoleStr + ",";
							}
						}
						if (!StringUtils.isBlank(guestRole)) {
							guestRole = guestRole.substring(0, guestRole.length() - 1);
						}
						noticeListMap.remove("guestrole");
						noticeListMap.put("guestrole", guestRole);
					}
					
					//拼接主演特约信息
					String noticeMainGuestRoleStr = "";
					if (noticeListMap.get("mainrole") != null) {
						noticeMainGuestRoleStr = noticeListMap.get("mainrole").toString();
					}
					if (noticeListMap.get("guestrole") != null) {
						if (StringUtils.isBlank(noticeMainGuestRoleStr)) {
							noticeMainGuestRoleStr = noticeListMap.get("guestrole").toString();
						}else {
							noticeMainGuestRoleStr += " | " + noticeListMap.get("guestrole").toString();
						}
					}
					noticeListMap.put("noticeMainGuestRoleStr", noticeMainGuestRoleStr);
					
					//将更新时间进行格式化
					Date updateTime = (Date) noticeListMap.get("updateTime");
					String updateTimeStr = DateUtils.parse2String(updateTime, "yyyy-MM-dd HH:mm:ss");
					noticeListMap.remove("updateTime");
					noticeListMap.put("updateTime", updateTimeStr);
					
					//格式化发布通告单的时间
					if (noticeListMap.get("publishTime") != null) {
						Date publishTime = (Date) noticeListMap.get("publishTime");
						String publishTimeStr = DateUtils.parse2String(publishTime, "yyyy-MM-dd HH:mm:ss");
						noticeListMap.remove("publishTime");
						noticeListMap.put("publishTime", publishTimeStr);
					}
					//拼接天数及通告提示信息
					String noticeMessage = "第  "+ noticeListMap.get("shootDays") + " 天 \r\n" + sdf.format(noticeDate) + noticeListMap.get("groupName");
					noticeListMap.put("noticeMessage", noticeMessage);
					//取出通告单id
					String noticeId = (String) noticeListMap.get("noticeId");
					//根据通告单id查询出所有的场景
					List<Map<String, Object>> viewList = viewService.queryNoticeViewList(crewInfo.getCrewId(), null, noticeId, null);
					
					for (Map<String, Object> map : viewList) {
						if (crewInfo.getCrewType() == CrewType.TVPlay.getValue() || crewInfo.getCrewType() == CrewType.InternetTvplay.getValue()) {
							map.put("seriesViewNo", map.get("seriesNo") + "-" + map.get("viewNo"));
						}
						
						String atmosphereSiteStr = "";
						if (map.get("atmosphereName") != null) {
							atmosphereSiteStr = (String) map.get("atmosphereName");
						}
						if (map.get("site") != null) {
							if (StringUtils.isBlank(atmosphereSiteStr)) {
								atmosphereSiteStr = (String) map.get("site");
							}else { 
								atmosphereSiteStr = (String) map.get("atmosphereName") + " / " + (String) map.get("site");
							}
						}
						map.put("atmosphereSite", atmosphereSiteStr);
						
						//将主次三级场景按照分隔符“|” 拼接起来
						String viewLocation = "";
						if (StringUtils.isNotBlank((String) map.get("majorView"))) {
							viewLocation = map.get("majorView").toString();
						}
						if (StringUtils.isNotBlank((String) map.get("minorView"))) {
							if (StringUtils.isBlank(viewLocation)) {
								viewLocation = map.get("minorView").toString();
							}else {
								viewLocation += " | " + map.get("minorView").toString();
							}
						}
						if (StringUtils.isNotBlank((String) map.get("thirdLevelView"))) {
							if (StringUtils.isBlank(viewLocation)) {
								viewLocation = map.get("thirdLevelView").toString();
							}else {
								viewLocation += " | " + map.get("thirdLevelView").toString();
							}
						}
						map.put("viewLocation", viewLocation);
						
						//拼接特约群众演员
						String guestMassRole = "";
						if (StringUtils.isNotBlank((String) map.get("guestRoleList"))) {
							guestMassRole = map.get("guestRoleList").toString();
						}
						if (StringUtils.isNotBlank((String) map.get("massRoleList"))) {
							if (StringUtils.isBlank(guestMassRole)) {
								guestMassRole = map.get("massRoleList").toString();
							}else {
								guestMassRole += " | " + map.get("massRoleList").toString();
							}
						}
						map.put("guestMassRole", guestMassRole);
						
						//拼接服化道信息
						String goodsInfoStr = "";
						if (StringUtils.isNotBlank((String) map.get("makeupName"))) {
							goodsInfoStr = (String) map.get("makeupName");
						}
						if (StringUtils.isNotBlank((String) map.get("clothesName"))) {
							if (StringUtils.isBlank(goodsInfoStr)) {
								goodsInfoStr = (String) map.get("clothesName");
							}else {
								goodsInfoStr += " | " + map.get("clothesName");
							}
						}
						if (StringUtils.isNotBlank((String) map.get("propsList"))) {
							if (StringUtils.isBlank(goodsInfoStr)) {
								goodsInfoStr = map.get("propsList").toString();
							}else {
								goodsInfoStr += " | " + map.get("propsList").toString();
							}
						}
						map.put("goodsInfoStr", goodsInfoStr);
						
					}
					
					noticeListMap.put("viewList", viewList);
				}
			}
			
			//定义导出的列
			Map<String, String> columnKeyMap = new LinkedHashMap<String, String>();
			columnKeyMap.put("拍摄状态", "shootStatus");
			if (crewInfo.getCrewType() == CrewType.Movie.getValue() || crewInfo.getCrewType() == CrewType.InternetMovie.getValue()) { //电影或网大
				columnKeyMap.put("场次", "viewNo");
			}else {
				columnKeyMap.put("集场号",  "seriesViewNo");
			}
			
			columnKeyMap.put("气氛/内外", "atmosphereSite");
			columnKeyMap.put("页数", "pageCount");
			columnKeyMap.put("拍摄地点", "shootLocation");
			columnKeyMap.put("场景", "viewLocation");
			columnKeyMap.put("主要内容", "mainContent");
			columnKeyMap.put("主要演员", "roleList");
			columnKeyMap.put("特约群众演员", "guestMassRole");
			columnKeyMap.put("服化道", "goodsInfoStr");
			columnKeyMap.put("带号", "tapNo");
			columnKeyMap.put("备注", "remark");
			
			 //调用方法导出表格数据
    		ExcelUtils.exportNoticeListForExcel(response, crewInfo, noticeList, columnKeyMap);
    		this.sysLogService.saveSysLog(request, "导出通告单列表数据", terminal, NoticeInfoModel.TABLE_NAME, null, SysLogOperType.EXPORT.getValue());
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，保存失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 查询通告单列表
	 * @param request
	 * @param page 分页
	 * @param canceledStatus 销场状态，0：表示未销场;1：表示已销场
	 * @param noticeDateMonth 通告单月份
	 * @return
	 */
	@ResponseBody 
	@RequestMapping("/queryNoticeList")
	public Map<String, Object> queryNoticeList(HttpServletRequest request, Page page, Integer canceledStatus, String noticeMonth){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		try {
			String crewId = this.getCrewId(request);
			
			if(canceledStatus == null) {
				canceledStatus = 0;
			}
			
			List<Map<String, Object>> noticeList = this.noticeService.queryNoticeDateList(crewId, canceledStatus, noticeMonth, page);
			
			page.setResultList(noticeList);
			resultMap.put("result", page);
			message="获取成功!";
		}catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		}catch (Exception e) {
			message = "未知异常,查询失败!";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
}
