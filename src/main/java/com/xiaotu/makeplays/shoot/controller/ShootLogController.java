package com.xiaotu.makeplays.shoot.controller;

import java.text.CollationKey;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.goods.model.constants.GoodsType;
import com.xiaotu.makeplays.notice.controller.dto.clip.AttendanceDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.ClipModelDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.RoleAttendanceDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.ShootLiveDto;
import com.xiaotu.makeplays.notice.model.NoticeInfoModel;
import com.xiaotu.makeplays.notice.model.clip.CameraInfoModel;
import com.xiaotu.makeplays.notice.model.clip.ClipCommentModel;
import com.xiaotu.makeplays.notice.model.clip.ClipPropModel;
import com.xiaotu.makeplays.notice.model.clip.LiveConvertAddModel;
import com.xiaotu.makeplays.notice.model.clip.RoleAttendanceModel;
import com.xiaotu.makeplays.notice.model.clip.ShootLiveModel;
import com.xiaotu.makeplays.notice.service.ClipService;
import com.xiaotu.makeplays.notice.service.NoticeService;
import com.xiaotu.makeplays.roleactor.model.constants.ViewRoleType;
import com.xiaotu.makeplays.roleactor.service.ViewRoleService;
import com.xiaotu.makeplays.shoot.service.ShootGroupService;
import com.xiaotu.makeplays.shoot.service.ShootLogService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.sys.service.SysLogService;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.view.service.ViewInfoService;

/**
 * 拍摄日志信息
 * @author xuchangjian 2016年8月10日上午10:08:46
 */
@Controller
@RequestMapping("/shootLogManager")
public class ShootLogController extends BaseController{

	private static Integer terminal = Constants.TERMINAL_PC;
	
	private static Logger logger = LoggerFactory.getLogger(ShootLogController.class);
	
	@Autowired
	private ShootLogService shootLogService;
	
	@Autowired
	private SysLogService sysLogService;
	
	@Autowired
	private ClipService clipService;
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	@Autowired
	private ShootGroupService shootGroupSerivce;
	
	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private ViewRoleService viewRoleService;
	
	@Autowired
	private UserService userService;
	
	/**
	 * 跳转到拍摄日志列表页面
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/toShootLogList")
	public ModelAndView toShootLogList(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();		
		mv.setViewName("/shoot/shootLogList");
		
		this.sysLogService.saveSysLog(request, "查询拍摄日志", Constants.TERMINAL_PC, NoticeInfoModel.TABLE_NAME + "," + ShootLiveModel.TABLE_NAME, null, 0);
		
		return mv;
	}
	
	/**
	 * 跳转到通告单列表界面需要加载的通告单列表数据
	 * @param request
	 * @param page 分页参数
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryShootLogList")
	public Map<String, Object> queryShootLogList(HttpServletRequest request, Page page){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		String crewId = getCrewId(request);
		try {
			//查询拍摄日志列表
			Map<String, Object> shootLogList = this.shootLogService.queryManyByMutiCondition(crewId, page);
			resultMap.put("shootList", shootLogList);
			resultMap.put("totalPageCount", page.getTotal());
			
//			this.sysLogService.saveSysLog(request, "查询拍摄日志", Constants.TERMINAL_PC, NoticeInfoModel.TABLE_NAME + "," + ShootLiveModel.TABLE_NAME, null, 0);
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
	 * 查询现场信息
	 * @param request
	 * @param noticeId 通告单ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryLiveInfo")
	public Map<String, Object> queryLiveInfo(HttpServletRequest request, String noticeId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			String crewId = this.getCrewId(request);
			
			//现场信息
			ShootLiveModel liveInfo = this.clipService.queryLiveInfoByNoticeId(crewId, noticeId);
			
			if (liveInfo == null) {
				liveInfo = new ShootLiveModel();
				
				Map<String, Object> noticeInfo = this.noticeService.queryNoticeFullInfoById(crewId, noticeId);
				if (noticeInfo == null) {
					throw new IllegalArgumentException("该通告单不属于当前剧组，请检查");
				}
				String shootLocation = (String) noticeInfo.get("shootLocation");
				String viewLocation = (String) noticeInfo.get("viewLocation");
				
				liveInfo.setShootLocation(shootLocation);
				liveInfo.setShootScene(viewLocation);
				liveInfo.setNoticeId(noticeId);
			}
			//转场信息
			List<LiveConvertAddModel> liveConvertAddList = this.clipService.queryConvertByLiveId(crewId, noticeId);
			ShootLiveDto shootLiveDto = new ShootLiveDto(liveInfo, liveConvertAddList);
			
			resultMap.put("liveInfo", shootLiveDto);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
		} catch (Exception e) {
			message = "未知错误,查询现场信息失败!";
			success = false;
			
			logger.error(message, e);
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 跳转到场记单页面时,需要加载的场记单/现场信息/演员出勤/特殊道具/重要备注的列表数据
	 * @param request
	 * @param noticeId 通告单的id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryClipInfoList")
	public Map<String, Object> queryClipInfoList(HttpServletRequest request, String noticeId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		String crewId = getCrewId(request);
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请选择要查看的通告单!");
			}
			
			//场记单信息
			Map<String, Object> clipInfoList = this.genClipInfoList(crewId, noticeId, "");
			resultMap.put("clipInfo", clipInfoList.get("clipViewDtoList"));
			resultMap.put("nameList", clipInfoList.get("cameraNameList")); //镜头信息列表
			
			//通告单演员出勤信息
			AttendanceDto roleAttendance = this.genRoleAttendance(crewId, noticeId);
			//主要演员出勤信息
			resultMap.put("majorRoleAttenInfo", roleAttendance.getMajorRoleAttenInfo());
			//特约、群众演员出勤信息
			resultMap.put("notMajRoleAttenInfo", roleAttendance.getNotMajRoleAttenInfo());
			
			//通告单中的特殊道具信息
			List<ClipPropModel> clipPropList = this.clipService.queryClipPropListByNoticeId(crewId, noticeId);
			if (clipPropList == null || clipPropList.size() == 0) {
				List<Map<String, Object>> noticePropList = this.noticeService.queryNoticePropList(crewId, noticeId, GoodsType.SpecialProps.getValue());
				for (Map<String, Object> noticeProp : noticePropList) {
					ClipPropModel clipPropModel = new ClipPropModel();
					
					clipPropModel.setNoticeId(noticeId);
					clipPropModel.setName((String) noticeProp.get("goodsName"));
					clipPropList.add(clipPropModel);
				}
			}
			resultMap.put("specialPropInfo", clipPropList);
			
			//通告单下重要的重要备注信息
			List<ClipCommentModel> clipCommentList = this.clipService.queryClipCommentByNoticeId(crewId, noticeId);
			resultMap.put("importCommentInfo", clipCommentList);
			
			//通告单下的现场信息
			ShootLiveModel liveInfo = this.clipService.queryLiveInfoByNoticeId(crewId, noticeId);
			if (liveInfo != null) {
				//现场信息中的转场信息
				List<LiveConvertAddModel> liveConvertAddList = this.clipService.queryConvertByLiveId(crewId, noticeId);
				
				ShootLiveDto shootLiveDto = new ShootLiveDto(liveInfo, liveConvertAddList);
				resultMap.put("liveInfo", shootLiveDto);
			} else {
				Map<String, Object> noticeInfo = this.noticeService.queryNoticeFullInfoById(crewId, noticeId);
				String shootLocation = (String) noticeInfo.get("shootLocation");
				String viewLocation = (String) noticeInfo.get("viewLocation");
				
				ShootLiveDto shootLiveDto = new ShootLiveDto();
				shootLiveDto.setShootLocation(shootLocation);
				shootLiveDto.setShootScene(viewLocation);
				shootLiveDto.setNoticeId(noticeId);
				resultMap.put("liveInfo", shootLiveDto);
			}
			
			this.sysLogService.saveSysLog(request, "查询场记单的现场信息、演员出勤、特殊道具、重要备注信息", 
					Constants.TERMINAL_PC, CameraInfoModel.TABLE_NAME + "," + RoleAttendanceModel.TABLE_NAME 
					+ "," + ClipPropModel.TABLE_NAME + "," + ClipCommentModel.TABLE_NAME, noticeId, 3);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误,查询失败!";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "查询场记单的现场信息、演员出勤、特殊道具、重要备注信息失败：" + e.getMessage(), 
					Constants.TERMINAL_PC, CameraInfoModel.TABLE_NAME + "," + RoleAttendanceModel.TABLE_NAME 
					+ "," + ClipPropModel.TABLE_NAME + "," + ClipCommentModel.TABLE_NAME, noticeId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 辅助方法
	 * 查询现有的场记单信息
	 * @param crewId
	 * @param noticeId
	 * @return
	 * @throws Exception 
	 */
	private Map<String, Object> genClipInfoList(String crewId, String noticeId, String userId) throws Exception {
		
		Map<String, ClipModelDto> clipViewDtoList = new LinkedHashMap<String, ClipModelDto>();
		
		//查询出所有的镜次信息
		List<Map<String, Object>> noticeAuditionList = this.clipService.queryAudiInfoWithTmpCancel(crewId, noticeId, userId);
		
		//按照viewId，lensNo镜号 ，auditionNo镜次 ，cameraName机位名称 依次分组
		String viewIds = "";
		
		//拍摄状态
		Map<Integer, String> shootStatusMap = new HashMap<Integer, String>();
		shootStatusMap.put(0, "未完成");
		shootStatusMap.put(1, "部分完成");
		shootStatusMap.put(2, "已完成");
		shootStatusMap.put(3, "删戏");
		shootStatusMap.put(4, "加戏未完成");
		shootStatusMap.put(5, "加戏已完成");
		
		//景别状态
		Map<Integer, String> sceneTypeMap = new HashMap<Integer, String>();
		sceneTypeMap.put(1, "近景");
		sceneTypeMap.put(2, "远景");
		sceneTypeMap.put(3, "特写");
		sceneTypeMap.put(4, "中景");
		sceneTypeMap.put(5, "全景");
		
		//成绩
		Map<Integer, String> gradeMap = new HashMap<Integer, String>();
		gradeMap.put(1, "OK");
		gradeMap.put(2, "NG");
		gradeMap.put(3, "备用");
		
		//过滤出所有的机位名称
		List<String> cameraNameList = new LinkedList<String>();
		List<String> cameraUserNameList = new LinkedList<String>();
		
		for (Map<String, Object> auditionInfo : noticeAuditionList) {
			
			String cameraName = (String) auditionInfo.get("cameraName");
			
			if (!cameraNameList.contains(cameraName)) {
				String cameraUserId = (String) auditionInfo.get("userId");
			    String realname = userService.queryById(cameraUserId).getRealName();
				cameraNameList.add(cameraName);
				cameraUserNameList.add(cameraName+"("+realname+")");
			}
		}
		
		
		
		ClipModelDto clipModel = new ClipModelDto();
		clipModel.setType("viewId");
		
		for (Map<String, Object> auditionInfo : noticeAuditionList) {
			
			//机位名称封装为map
			Map<String,Map<String,Object>> cameraNameMap = new LinkedHashMap<String, Map<String,Object>>();
			for(String name:cameraNameList){
				Map<String,Object> map = new LinkedHashMap<String, Object>();
				map.put("sceneType", "-");
				map.put("content", "-");
				map.put("tcValue", "-");
				map.put("grade", "-");
				map.put("comment", "-");
				map.put("cameraId", "00");
				cameraNameMap.put(name, map);
			}
			
			Integer seriesNo = (Integer) auditionInfo.get("seriesNo");
			String viewNo = (String) auditionInfo.get("viewNo");
			
			if(auditionInfo.get("shootStatus")!=null){
				auditionInfo.put("statusDetail", shootStatusMap.get((Integer) auditionInfo.get("shootStatus")));
			}else{
				auditionInfo.put("statusDetail", "");
			}
			if(auditionInfo.get("sceneType")!=null){
				auditionInfo.put("sceneDetail", sceneTypeMap.get((Integer) auditionInfo.get("sceneType")));
			}else{
				auditionInfo.put("sceneDetail", "");
			}
			if(auditionInfo.get("grade")!=null){
				auditionInfo.put("gradeDetail", gradeMap.get((Integer) auditionInfo.get("grade")));
			}else{
				auditionInfo.put("gradeDetail", "");
			}
			
			String seriesViewNo = seriesNo + "-" + viewNo.toLowerCase();
			String lensNo = auditionInfo.get("lensNo") + "";
			String auditionNo = auditionInfo.get("auditionNo") + "";
			String cameraName = (String) auditionInfo.get("cameraName");
			
			Map<String,ClipModelDto> viewli = clipModel.getMap();
			if (viewli!=null && viewli.containsKey(seriesViewNo)) {
				ClipModelDto viewModel = viewli.get(seriesViewNo);
				Map<String, ClipModelDto> viewMap = viewModel.getMap();
				if(viewMap.containsKey(lensNo)){
					ClipModelDto lensNoModel = viewMap.get(lensNo);
					Map<String, ClipModelDto> lensMap = lensNoModel.getMap();
					
					if(lensMap.containsKey(auditionNo)){
						ClipModelDto auditionNoModel = lensMap.get(auditionNo);
						
						Map<String,Map<String,Object>> cameraNameMap1 = auditionNoModel.getCameraMap();
						Map<String,Object> map = new LinkedHashMap<String, Object>();
						map.put("sceneType", auditionInfo.get("sceneDetail") + "");
						map.put("content", auditionInfo.get("content") + "");
						map.put("tcValue", auditionInfo.get("tcValue") + "");
						
						map.put("grade", auditionInfo.get("gradeDetail")==null?"":auditionInfo.get("gradeDetail"));
						map.put("comment", auditionInfo.get("comment") + "");
						map.put("cameraId", auditionInfo.get("cameraId") + "");
						cameraNameMap1.put(cameraName, map);
						
						auditionNoModel.setCameraMap(cameraNameMap1);
						auditionNoModel.setList(auditionInfo);
						auditionNoModel.setNum(auditionNoModel.getNum()+1);
						lensMap.put(auditionNo, auditionNoModel);
					}else{
						ClipModelDto auditionNoModel = new ClipModelDto();
						Map<String,Map<String,Object>> cameraNameMap1 = cameraNameMap;
						Map<String,Object> map = new LinkedHashMap<String, Object>();
						map.put("sceneType", auditionInfo.get("sceneDetail") + "");
						map.put("content", auditionInfo.get("content") + "");
						map.put("tcValue", auditionInfo.get("tcValue") + "");
						
						map.put("grade", auditionInfo.get("gradeDetail") == null ? "" : auditionInfo.get("gradeDetail"));
						map.put("comment", auditionInfo.get("comment") + "");
						map.put("cameraId", auditionInfo.get("cameraId") + "");
						cameraNameMap1.put(cameraName, map);
						auditionNoModel.setCameraMap(cameraNameMap1);
						auditionNoModel.setList(auditionInfo);
						auditionNoModel.setNum(auditionNoModel.getNum()+1);
						lensMap.put(auditionNo, auditionNoModel);
						
						viewModel.setNum(viewModel.getNum()+1);
						lensNoModel.setNum(lensNoModel.getNum()+1);
					}
					lensNoModel.setMap(lensMap);
					
					viewMap.put(lensNo, lensNoModel);
				}else{
					
					ClipModelDto auditionNoModel = new ClipModelDto();
					Map<String,Map<String,Object>> cameraNameMap1 = cameraNameMap;
					Map<String,Object> map = new LinkedHashMap<String, Object>();
					map.put("sceneType", auditionInfo.get("sceneDetail") + "");
					map.put("content", auditionInfo.get("content") + "");
					map.put("tcValue", auditionInfo.get("tcValue") + "");
					
					map.put("grade", auditionInfo.get("gradeDetail") == null ? "" : auditionInfo.get("gradeDetail"));
					map.put("comment", auditionInfo.get("comment") + "");
					map.put("cameraId", auditionInfo.get("cameraId") + "");
					cameraNameMap1.put(cameraName, map);
					auditionNoModel.setCameraMap(cameraNameMap1);
					auditionNoModel.setList(auditionInfo);
					auditionNoModel.setNum(auditionNoModel.getNum()+1);
					
					ClipModelDto lensNoModel = new ClipModelDto();
					Map<String, ClipModelDto> lensMap = new LinkedHashMap<String, ClipModelDto>();
					lensMap.put(auditionNo, auditionNoModel);
					lensNoModel.setNum(lensNoModel.getNum()+1);
					lensNoModel.setMap(lensMap);
					viewMap.put(lensNo, lensNoModel);
					
					viewModel.setNum(viewModel.getNum()+1);
				}
				viewModel.setMap(viewMap);
				
				viewli.put(seriesViewNo, viewModel);
				clipModel.setMap(viewli);
			} else {
				
				ClipModelDto auditionNoModel = new ClipModelDto();
				Map<String,Map<String,Object>> cameraNameMap1 = cameraNameMap;
				Map<String,Object> map = new LinkedHashMap<String, Object>();
				map.put("sceneType", auditionInfo.get("sceneDetail") + "");
				map.put("content", auditionInfo.get("content") + "");
				map.put("tcValue", auditionInfo.get("tcValue") + "");
				
				map.put("grade", auditionInfo.get("gradeDetail") == null ? "" : auditionInfo.get("gradeDetail"));
				map.put("comment", auditionInfo.get("comment") + "");
				map.put("cameraId", auditionInfo.get("cameraId") + "");
				cameraNameMap1.put(cameraName, map);
				auditionNoModel.setCameraMap(cameraNameMap1);
				auditionNoModel.setList(auditionInfo);
				auditionNoModel.setNum(auditionNoModel.getNum()+1);
				
				ClipModelDto lensNoModel = new ClipModelDto();
				Map<String, ClipModelDto> lensMap = new LinkedHashMap<String, ClipModelDto>();
				lensMap.put(auditionNo, auditionNoModel);
				lensNoModel.setNum(lensNoModel.getNum()+1);
				lensNoModel.setMap(lensMap);
				
				ClipModelDto viewModel = new ClipModelDto();
				Map<String, ClipModelDto> viewMap = new LinkedHashMap<String, ClipModelDto>();
				viewMap.put(lensNo, lensNoModel);
				viewModel.setNum(viewModel.getNum()+1);
				viewModel.setMap(viewMap);
				
				if(viewli==null){
					viewli = new LinkedHashMap<String, ClipModelDto>();
				}
				viewli.put(seriesViewNo, viewModel);
				clipModel.setMap(viewli);
				
				viewIds += seriesViewNo + ",";
			}
			
		}
		
		clipViewDtoList = clipModel.getMap();
		
		//查询指定的场景信息，会查询出对应的销场信息
		/*List<Map<String, Object>> viewList = null;
		if (!StringUtils.isBlank(viewIds)) {
			viewList = this.viewInfoService.queryViewListByViewIds(crewId, viewIds.substring(0, viewIds.length() - 1));
			Map<String, Map<String, Object>> groupViewInfoMap = new HashMap<String, Map<String, Object>>();
			for (Map<String, Object> viewInfoMap : viewList) {
				String viewId = (String) viewInfoMap.get("viewId");
				if (!groupViewInfoMap.containsKey(viewId)) {
					groupViewInfoMap.put(viewId, viewInfoMap);
				}
			}
			
			
			Map<String, ClipModelDto> groupViewAuditionMap = clipModel.getMap();
			
			
			Set<String> groupViewAuditionKeyset = groupViewInfoMap.keySet();
			for (String viewId : groupViewAuditionKeyset) {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				Map<String, Object> viewInfoMap = groupViewInfoMap.get(viewId);
				map.put("view", viewInfoMap);
				ClipModelDto clip =  groupViewAuditionMap.get(viewId);
				map.put("clip", clip);
				clipViewDtoList.add(map);
				
			}
		}*/
		
		
		//如果没有镜次信息，默认返回通告单下所有场景信息
		/*if (StringUtils.isBlank(viewIds)) {
			List<Map<String, Object>> noticeViewList = this.viewInfoService.queryNoticeViewList(crewId, noticeId);
			ClipInfoDto clipInfoDto = new ClipInfoDto();
			clipInfoDto.setCameraName(null);
			
			for (Map<String, Object> viewInfoMap : noticeViewList) {
			}
		}*/
		
		Map<String,Object> resu = new HashMap<String, Object>();
		resu.put("cameraNameList", cameraUserNameList);
		resu.put("clipViewDtoList", clipViewDtoList);
		return resu;
	}
	
	/**
	 * 生成通告单演员出勤信息
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	private AttendanceDto genRoleAttendance(String crewId, String noticeId) {
		AttendanceDto attendanceDto = new AttendanceDto();
		
		List<RoleAttendanceDto> majorRoleAttList = new ArrayList<RoleAttendanceDto>();	//主要演员出勤信息
		List<RoleAttendanceDto> notMajRoleAttList = new ArrayList<RoleAttendanceDto>();	//特约、群众演员出勤信息
		
		//演员出勤信息
		List<RoleAttendanceModel> roleAttendanceList = this.clipService.queryRoleAttenceInfoByNoticeId(crewId, noticeId);
		
		//将主演与其它演员的出勤信息分开存放
		for (RoleAttendanceModel roleAttendance : roleAttendanceList) {
			RoleAttendanceDto roleAttendanceDto = new RoleAttendanceDto(roleAttendance);
			
			int roleType = roleAttendance.getViewRoleType();
			//主演
			if (roleType == ViewRoleType.MajorActor.getValue()) {
				majorRoleAttList.add(roleAttendanceDto);
			} else {
				notMajRoleAttList.add(roleAttendanceDto);
			}
		}
		
		//如果没有保存过出勤信息，则返回通告单下所有演员的基本信息
		if (roleAttendanceList == null || roleAttendanceList.size() == 0) {
			List<Map<String, Object>> noticeRoleList = this.viewRoleService.queryManyByNoticeId(crewId, noticeId);
			for (Map<String, Object> noticeRoleMap : noticeRoleList) {
				RoleAttendanceDto roleAttendanceDto = genAttendanceDto(noticeRoleMap);
				int roleType = (Integer) noticeRoleMap.get("viewRoleType");
				if (roleType == ViewRoleType.MajorActor.getValue()) {
					majorRoleAttList.add(roleAttendanceDto);
				} else {
					notMajRoleAttList.add(roleAttendanceDto);
				}
			}
		}
		
		if (majorRoleAttList != null && majorRoleAttList.size() > 0) {
			Collections.sort(majorRoleAttList, new Comparator<RoleAttendanceDto>() {
				@Override
				public int compare(RoleAttendanceDto o1, RoleAttendanceDto o2) {
					CollationKey key1 = Collator.getInstance().getCollationKey(o1.getViewRoleName().toString().toLowerCase());// 要想不区分大小写进行比较用o1.toString().toLowerCase()
	        		CollationKey key2 = Collator.getInstance().getCollationKey(o2.getViewRoleName().toString().toLowerCase());
					return key1.compareTo(key2);
				}
			});
		}
		
		if (notMajRoleAttList != null && notMajRoleAttList.size() > 0) {
			Collections.sort(notMajRoleAttList, new Comparator<RoleAttendanceDto>() {
				@Override
				public int compare(RoleAttendanceDto o1, RoleAttendanceDto o2) {
					CollationKey key1 = Collator.getInstance().getCollationKey(o1.getViewRoleName().toString().toLowerCase());// 要想不区分大小写进行比较用o1.toString().toLowerCase()
	        		CollationKey key2 = Collator.getInstance().getCollationKey(o2.getViewRoleName().toString().toLowerCase());
					return key1.compareTo(key2);
				}
			});
		}
		
		attendanceDto.setMajorRoleAttenInfo(majorRoleAttList);
		attendanceDto.setNotMajRoleAttenInfo(notMajRoleAttList);
		
		return attendanceDto;
	}


	/**
	 * 生成演员出勤Dto
	 * @param roleInfoMap
	 * @return
	 */
	private RoleAttendanceDto genAttendanceDto(Map<String, Object> roleInfoMap) {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//定义演员出勤信息的dto
		RoleAttendanceDto roleAttendanceDto = new RoleAttendanceDto();
		//在出勤信息表中是否有数据
		if (roleInfoMap.get("attendanceId") != null) {
			roleAttendanceDto.setAttendanceId((String) roleInfoMap.get("attendanceId"));
		}
		//封装剧组id
		if (roleInfoMap.get("crewId") != null) {
			roleAttendanceDto.setCrewId((String) roleInfoMap.get("crewId"));
		}
		//封装通告单的id
		if (roleInfoMap.get("noticeId") != null) {
			roleAttendanceDto.setNoticeId((String) roleInfoMap.get("noticeId"));
		}
		//是否迟到
		if (roleInfoMap.get("isLateArrive") != null) {
			int isLateArrive = (Integer) roleInfoMap.get("isLateArrive");
			if (isLateArrive == 1) {
				roleAttendanceDto.setIsLateArrive(true);
			} else {
				roleAttendanceDto.setIsLateArrive(false);
			}
		}
		//是否迟放
		if (roleInfoMap.get("isLatePackup") != null) {
			int isLatePackup = (Integer) roleInfoMap.get("isLatePackup");
			if (isLatePackup == 1) {
				roleAttendanceDto.setIsLatePackup(true);
			} else {
				roleAttendanceDto.setIsLatePackup(false);
			}
		}
		//到场时间
		if (roleInfoMap.get("rarriveTime") != null) {
			Date rarriveTime = (Date) roleInfoMap.get("rarriveTime");
			roleAttendanceDto.setRarriveTime(sdf1.format(rarriveTime));
		}
		//演员人数
		if (roleInfoMap.get("roleNum") != null) {
			roleAttendanceDto.setRoleNum((Integer) roleInfoMap.get("roleNum"));
		}
		roleAttendanceDto.setRoleType((Integer) roleInfoMap.get("viewRoleType"));
		if (roleInfoMap.get("rpackupTime") != null) {
			Date rpackupTime = (Date) roleInfoMap.get("rpackupTime");
			roleAttendanceDto.setRpackupTime(sdf1.format(rpackupTime));
		}
		roleAttendanceDto.setViewRoleName((String) roleInfoMap.get("viewRoleName"));
		if (roleInfoMap.get("actorName") != null) {
			roleAttendanceDto.setActorName((String) roleInfoMap.get("actorName"));
		}
		
		return roleAttendanceDto;
	}
}
