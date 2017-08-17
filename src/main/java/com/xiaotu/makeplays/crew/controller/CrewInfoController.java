package com.xiaotu.makeplays.crew.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.xiaotu.makeplays.authority.controller.dto.CrewAuthDto;
import com.xiaotu.makeplays.authority.model.AuthorityModel;
import com.xiaotu.makeplays.authority.model.CrewAuthMapModel;
import com.xiaotu.makeplays.authority.model.constants.AuthorityPlatform;
import com.xiaotu.makeplays.authority.service.AuthorityService;
import com.xiaotu.makeplays.authority.service.CrewAuthMapService;
import com.xiaotu.makeplays.crew.controller.filter.CrewInfoFilter;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.model.CrewSubjectModel;
import com.xiaotu.makeplays.crew.model.constants.CrewUserStatus;
import com.xiaotu.makeplays.crew.service.CrewClearService;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.crew.service.CrewSubjectService;
import com.xiaotu.makeplays.crew.service.CrewUserMapService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.mobile.push.umeng.service.android.UmengAndroidPushService;
import com.xiaotu.makeplays.mobile.push.umeng.service.ios.IOSOriginalPushService;
import com.xiaotu.makeplays.mobile.push.umeng.service.ios.UmengIOSPushService;
import com.xiaotu.makeplays.shoot.service.ShootGroupService;
import com.xiaotu.makeplays.sysrole.service.SysRoleInfoService;
import com.xiaotu.makeplays.user.model.JoinCrewApplyMsgModel;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.service.CrewContactService;
import com.xiaotu.makeplays.user.service.JoinCrewApplyMsgService;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.RegexUtils;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 剧组管理
 * @author xuchangjian 2016-11-2下午4:07:48
 */
@Controller
@RequestMapping("/crewManager")
public class CrewInfoController extends BaseController{
	
	Logger logger = LoggerFactory.getLogger(CrewInfoController.class);
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private CrewInfoService crewInfoService;
	
	@Autowired
	private CrewUserMapService crewUserMapService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ShootGroupService shootGroupService;
	
	@Autowired
	private IOSOriginalPushService iOSOriginalPushService;
	
	@Autowired
	private UmengIOSPushService umengIOSPushService;
	
	@Autowired
	private UmengAndroidPushService umengAndroidPushService;
	
	@Autowired
	private AuthorityService authorityService;
	
	@Autowired
	private SysRoleInfoService sysRoleInfoService;
	
	@Autowired
	private JoinCrewApplyMsgService joinCrewApplyMsgService;
	
	@Autowired
	private CrewContactService crewContactService;
	
	@Autowired
	private CrewClearService crewClearService;
	
	@Autowired
	private CrewSubjectService crewSubjectService;
	
	@Autowired
	private CrewAuthMapService crewAuthMapService;
	
	/**
	 * 跳转到加入剧组页面
	 * @return
	 */
	@RequestMapping("/toJoinCrewPage")
	public ModelAndView toJoinCrewPage() {
		ModelAndView view = new ModelAndView("crew/joinCrew");
		return view;
	}
	
	/**
	 * 跳转到选择一个剧组然后加入的页面
	 * @return
	 */
	@RequestMapping("/toSelectCrewPage")
	public ModelAndView toSelectCrewPage() {
		ModelAndView view = new ModelAndView("crew/selectCrew");
		return view;
	}
	
	/**
	 * 跳转到创建剧组的页面
	 * @return
	 */
	@RequestMapping("/toCreateCrewPage")
	public ModelAndView toCreateCrewPage() {
		ModelAndView view = new ModelAndView("crew/createCrew");
		return view;
	}
	
	/**
	 * 跳转到剧组设置页面
	 * @return
	 */
	@RequestMapping("/toCrewSettingsPage")
	public ModelAndView toCrewSettingsPage() {
		ModelAndView view = new ModelAndView("/crew/crewSettings");
		return view;
	}
	
	/**
	 * 跳转到剧组管理页面,用于admin剧组管理
	 * @param request
	 * @param crewId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/toCrewSetPage")
	public ModelAndView toCrewSetPage(HttpServletRequest request,String crewId) {
		ModelAndView view = new ModelAndView();
		try {
			//获取剧组信息放到session中
			CrewInfoModel crewInfo = crewInfoService.queryById(crewId);
			HttpSession session = request.getSession();
			session.setAttribute(Constants.SESSION_CREW_INFO, crewInfo); // 用户所选剧组信息
			session.setAttribute(Constants.SESSION_IFCHECK, "OK"); // 用户所选剧组信息
			view.setViewName("/crew/crewSettings");
		} catch (Exception e) {
			logger.error("未知异常，剧组管理", e);
		}
		return view;
	}
	
	/**
	 * 跳转到剧组详细信息页面
	 * @param crewId
	 * @return
	 */
	@RequestMapping("/toCrewDetailPage")
	public ModelAndView toCrewDetailPage(String crewId) {
		ModelAndView view = new ModelAndView("/crew/crewDetail");
		
		view.addObject("crewId", crewId);
		return view;
	}
	
	/**
	 * 跳转到用户剧组列表页面
	 */
	@RequestMapping("/toUserCrewListPage")
	public ModelAndView toUserCrewListPage() {
		ModelAndView mv = new ModelAndView("/usercenter/userCrewList");
		return mv;
	}
	
	/**
	 * 跳转到剧组详细信息页面--admin
	 */
	@RequestMapping("/toCrewDetailPageForAdmin")
	public ModelAndView toCrewDetailPageForAdmin(String crewId) {
		ModelAndView mv = new ModelAndView("/crew/crewDetailForAdmin");
		mv.addObject("crewId", crewId);
		return mv;
	}
	
	/**
	 * 获取用户剧组信息
	 */
	@ResponseBody
	@RequestMapping("/queryUserCrewList")
	public Map<String, Object> queryUserCrewList(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			String userId = this.getLoginUserId(request);
			
			List<Map<String, Object>> resultCrewList = new ArrayList<Map<String, Object>>();
			
			//获取用户拥有的剧组
			List<Map<String, Object>> userCrewList = this.crewInfoService.queryUserCrewList(userId);
			//获取用户申请的，正在审核的剧组
			List<CrewInfoModel> auditingCrewList = this.crewInfoService.queryAuditingCrewByUserId(userId);
			
			for (CrewInfoModel crewInfo : auditingCrewList) {
				String crewId = crewInfo.getCrewId();
				String crewName = crewInfo.getCrewName();
				Integer crewType = crewInfo.getCrewType();
				String company = crewInfo.getCompany();
				String recordNumber = crewInfo.getRecordNumber();
				Date shootStartDate = crewInfo.getShootStartDate();
				Date shootEndDate = crewInfo.getShootEndDate();
				Integer status = crewInfo.getStatus();
				String subjectName = crewInfo.getSubject();
				String director = crewInfo.getDirector();
				String scriptWriter = crewInfo.getScriptWriter();
				String mainActorNames = crewInfo.getMainactor();
				String enterPassword = crewInfo.getEnterPassword();
				String picPath = crewInfo.getPicPath();
				
				//用户在剧组中的状态  1：正常  2：审核中  99：冻结
				int crewUserStatus = CrewUserStatus.Auditing.getValue();
				String roleNames = "";
				
				Map<String, Object> singleCrewInfo = new HashMap<String, Object>();
				singleCrewInfo.put("crewId", crewId);
				singleCrewInfo.put("crewName", crewName);
				singleCrewInfo.put("crewType", crewType);
				singleCrewInfo.put("company", company);
				singleCrewInfo.put("recordNumber", recordNumber);
				
				if (shootStartDate != null) {
					singleCrewInfo.put("shootStartDate", this.sdf1.format(shootStartDate));
				} else {
					singleCrewInfo.put("shootStartDate", "");
				}
				if (shootEndDate != null) {
					singleCrewInfo.put("shootEndDate", this.sdf1.format(shootEndDate));
				} else {
					singleCrewInfo.put("shootEndDate", "");
				}
				
				singleCrewInfo.put("status", status);
				singleCrewInfo.put("subjectName", subjectName);
				singleCrewInfo.put("director", director);
				singleCrewInfo.put("scriptWriter", scriptWriter);
				singleCrewInfo.put("mainActorNames", mainActorNames);
				singleCrewInfo.put("crewUserStatus", crewUserStatus);
				singleCrewInfo.put("hasClipAuth", false);
				singleCrewInfo.put("roleNames", roleNames);
				singleCrewInfo.put("enterPassword", enterPassword);
				singleCrewInfo.put("picPath", FileUtils.genPreviewPath(picPath));
				singleCrewInfo.put("canModify", false);
				
				resultCrewList.add(singleCrewInfo);
			}
			for (Map<String, Object> userCrewInfoMap : userCrewList) {
				Date shootStartDate = (Date) userCrewInfoMap.get("shootStartDate");
				Date shootEndDate = (Date) userCrewInfoMap.get("shootEndDate");
				Integer ifDefault = (Integer) userCrewInfoMap.get("ifDefault");
				String clipAuthId = (String) userCrewInfoMap.get("clipAuthId");	//场记单权限ID
				String crewUserManagerAuthId = (String) userCrewInfoMap.get("crewUserManagerAuthId");	//剧组成员管理权限ID
				String picPath = (String) userCrewInfoMap.get("picPath");
				Integer crewUserStatus = (Integer) userCrewInfoMap.get("crewUserStatus"); //用户在剧组中的状态
				
				if (shootStartDate != null) {
					userCrewInfoMap.put("shootStartDate", this.sdf1.format(shootStartDate));
				}
				if (shootEndDate != null) {
					userCrewInfoMap.put("shootEndDate", this.sdf1.format(shootEndDate));
				}
				if (crewUserStatus == CrewUserStatus.Normal.getValue() && ifDefault == 1) {
					userCrewInfoMap.put("crewUserStatus", CrewUserStatus.Currenct.getValue());
				}
				if (!StringUtils.isBlank(clipAuthId)) {
					userCrewInfoMap.put("hasClipAuth", true);
				} else {
					userCrewInfoMap.put("hasClipAuth", false);
				}
				if (!StringUtils.isBlank(crewUserManagerAuthId)) {
					userCrewInfoMap.put("canModify", true);
				} else {
					userCrewInfoMap.put("canModify", false);
				}
				
				if (!StringUtils.isBlank(picPath)) {
					picPath = FileUtils.genPreviewPath(picPath);
				}
				userCrewInfoMap.put("picPath", picPath);
				
				resultCrewList.add(userCrewInfoMap);
			}
			
			//获取用户所在已过期的剧组
			List<Map<String, Object>> userExpiredCrewList = this.crewInfoService.queryUserExpiredCrew(userId);
			for (Map<String, Object> crewInfo : userExpiredCrewList) {
				Date shootStartDate = (Date) crewInfo.get("shootStartDate");
				Date shootEndDate = (Date) crewInfo.get("shootEndDate");
				Integer ifDefault = (Integer) crewInfo.get("ifDefault");
				String clipAuthId = (String) crewInfo.get("clipAuthId");	//场记单权限ID
				String crewUserManagerAuthId = (String) crewInfo.get("crewUserManagerAuthId");	//剧组成员管理权限ID
				String picPath = (String) crewInfo.get("picPath");
				Integer crewUserStatus = (Integer) crewInfo.get("crewUserStatus"); //用户在剧组中的状态
				
				if (shootStartDate != null) {
					crewInfo.put("shootStartDate", this.sdf1.format(shootStartDate));
				}
				if (shootEndDate != null) {
					crewInfo.put("shootEndDate", this.sdf1.format(shootEndDate));
				}
				if (crewUserStatus == CrewUserStatus.Normal.getValue() && ifDefault == 1) {
					crewInfo.put("crewUserStatus", CrewUserStatus.Currenct.getValue());
				}
				if (!StringUtils.isBlank(clipAuthId)) {
					crewInfo.put("hasClipAuth", true);
				} else {
					crewInfo.put("hasClipAuth", false);
				}
				if (!StringUtils.isBlank(crewUserManagerAuthId)) {
					crewInfo.put("canModify", true);
				} else {
					crewInfo.put("canModify", false);
				}
				
				if (!StringUtils.isBlank(picPath)) {
					picPath = FileUtils.genPreviewPath(picPath);
				}
				crewInfo.put("picPath", picPath);
			}
			
			resultMap.put("crewList", resultCrewList);
			resultMap.put("expiredCrewList", userExpiredCrewList);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			logger.error("未知异常，获取剧组失败", e);
			success = false;
			message = "未知异常，获取剧组失败";
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 跳转到剧组管理页面   lxp  2016-10-27
	 * @param request
	 * @param crewId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/toCrewManagePage")
	public ModelAndView toCrewManagePage(HttpServletRequest request,String crewId) {
		ModelAndView view = new ModelAndView();
		try {
			if(StringUtils.isNotBlank(crewId)){
				//获取剧组信息放到session中
				CrewInfoModel crewInfo = crewInfoService.queryById(crewId);
				HttpSession session = request.getSession();
				session.setAttribute(Constants.SESSION_CREW_INFO, crewInfo); // 用户所选剧组信息
				session.setAttribute(Constants.SESSION_IFCHECK, "OK"); // 用户所选剧组信息
				view.setViewName("/crew/crewSettings");
//				this.sysLogService.saveSysLog(request, "跳转到剧组管理页面", Constants.TERMINAL_PC, "tab_crew_info",null,0);
			}else{
				view.setViewName("/crew/crewManage");
			}
		} catch (Exception e) {
			logger.error("未知异常，剧组管理", e);
		}
		return view;
	}
	
	/**
	 * 查询剧组信息  lxp  2016-10-27
	 * @param request
	 * @param crewName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryAllCrews")
	public Map<String, Object> queryAllCrews(HttpServletRequest request, CrewInfoFilter crewInfoFilter) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			List<Map<String, Object>> crewList = crewInfoService.queryCrewList(crewInfoFilter);
			resultMap.put("crewList", crewList);
			success = true;
//			this.sysLogService.saveSysLog(request, "查询剧组信息 ", Constants.TERMINAL_PC, "tab_crew_info",null,0);
		} catch (Exception e) {
			success = false;
	        message = "未知异常，获取所有剧组失败";
	        logger.error(message, e);
		}
        resultMap.put("success", success);
        resultMap.put("message", message);
		return resultMap;
	}
	
	
	
	/**
	 * @Description 查询当前用户拥有的所有剧组信息(排除当前剧组)
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryAllCrewsByUserId")
	public Map<String, Object> queryAllCrewsByUserId(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			String userId = this.getLoginUserId(request);
			String crewId = this.getCrewId(request);
			List<Map<String, Object>> crewList = crewUserMapService.queryCrewInfoByUserIdNotContainsCurrCrew(userId,crewId);
			resultMap.put("crewList", crewList);
		} catch (Exception e) {
			success = false;
	        message = "未知异常，获取所有剧组失败";
	        logger.error(message, e);
		}
        resultMap.put("success", success);
        resultMap.put("message", message);
		return resultMap;
	}
	
	
	/**
	 * 查询剧组基本信息列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryAllCrewIdAndName")
	public Map<String, Object> queryAllCrewIdAndName(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			List<Map<String, Object>> crewList = crewInfoService.queryAllCrewIdAndName();
			resultMap.put("crewList", crewList);
			success = true;
		} catch (Exception e) {
			success = false;
	        message = "未知异常，获取所有剧组失败";
	        logger.error(message, e);
		}
        resultMap.put("success", success);
        resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 删除剧组成员
	 * @param request
	 * @param userId
	 * @param crewId
	 * @return
	 */
	@RequestMapping("deleteCrewUser")
	@ResponseBody
	public Map<String, Object> deleteCrewUser(HttpServletRequest request, String userId, String crewId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			// 删除用户权限/删除用户信息
			this.crewInfoService.deleteUserAuth(crewId, userId);
			//删除用户角色信息、用户场景角色（场景表中主要演员、特约演员、群众演员）信息
			this.crewInfoService.deleteUserRoleMap(crewId, userId);
			this.crewInfoService.deleteCrewRoleUserMap(crewId, userId);
			//删除用户剧组联系表、联系人和角色的关联、用户入住信息
			this.crewContactService.deleteCrewContactByCrewUserId(crewId, userId);
			//删除用户和想要关注的演员的关联关系
			this.crewInfoService.deleteUserFocusRoleMap(crewId, userId);
			//删除剧组用户关联信息
			this.crewInfoService.deleteCrewUserMap(crewId, userId);//getCrewId(request)
			
			this.sysLogService.saveSysLog(request, "删除剧组用户", Constants.TERMINAL_PC, "tab_crew_user_map", userId, 3);
			message = "删除成功！";
		} catch (Exception e) {
			success = false;
			message = "未知异常，删除剧组用户失败！";
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 根据角色ID获取菜单列表
	 */
	/*@RequestMapping("getMenuListByRole")
	public @ResponseBody Map getMenuListByRole(HttpServletRequest request,String roleId,String userId,String crewId)throws Exception{
		Map map=new HashMap();
		List<Map<String,Object>> resultList=this.crewInfoService.getAuthListByRole(roleId, userId,crewId);
		//pc端
		List<Map<String,Object>> rootList = new ArrayList<Map<String,Object>>();
		for (Map<String, Object> map2 : resultList) {
			if(map2.get("parentId").equals("0")){
				boolean flag = false;
				for (Map<String, Object> map3 : resultList) {
					if(map2.get("parentId").equals(map3.get("authId"))){
						flag = true;
					}
				}
				if(!flag){
					rootList.add(map2);
				}
			}
		}
		List<Map<String,Object>> chileList = null;
		List<Map<String,Object>> chileListTo = null;
		for (Map<String, Object> map2 : rootList) {
			chileList = new ArrayList<Map<String,Object>>();
			for (Map<String, Object> map3 : resultList) {
				if(map2.get("authId").equals(map3.get("parentId"))){
					chileListTo = new ArrayList<Map<String,Object>>();
					for (Map<String, Object> map4 : resultList) {
						if(map3.get("authId").equals(map4.get("parentId"))){
							chileListTo.add(map4);
						}
					}
					map3.put("childListTo", chileListTo);
					chileList.add(map3);
				}
			}
			map2.put("childList", chileList);
		}
		//移动端
		List<Map<String,Object>> rootListmove = new ArrayList<Map<String,Object>>();
		for (Map<String, Object> map2 : resultList) {
			if(map2.get("parentId").equals("1")){
				boolean flag = false;
				for (Map<String, Object> map3 : resultList) {
					if(map2.get("parentId").equals(map3.get("authId"))){
						flag = true;
					}
				}
				if(!flag){
					rootListmove.add(map2);
				}
			}
		}
		List<Map<String,Object>> chileListmove = null;
		List<Map<String,Object>> chileListTomove = null;
		for (Map<String, Object> map2 : rootListmove) {
			chileListmove = new ArrayList<Map<String,Object>>();
			for (Map<String, Object> map3 : resultList) {
				if(map2.get("authId").equals(map3.get("parentId"))){
					chileListTomove = new ArrayList<Map<String,Object>>();
					for (Map<String, Object> map4 : resultList) {
						if(map3.get("authId").equals(map4.get("parentId"))){
							chileListTomove.add(map4);
						}
					}
					map3.put("childListTo", chileListTomove);
					chileListmove.add(map3);
				}
			}
			map2.put("childList", chileListmove);
		}
		map.put("rootList", rootList);
		map.put("rootListmove", rootListmove);
		//this.sysLogService.saveSysLog(request, "根据角色ID获取菜单列表", Constants.TERMINAL_PC, "tab_role_auth_map",roleId);
		return map;
	}*/
	
	/**
	 * 根据剧组名称查询剧组信息
	 * @param request
	 * @param crewName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryCrewIdAndCrewName")
	public Map<String ,Object> queryCrewIdAndCrewName(HttpServletRequest request,String crewName){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if(crewName != null){
				crewName = crewName.trim();
			}
			resultMap.put("result", this.crewInfoService.queryCrewIdAndCrewName(crewName));
		} catch (Exception e) {
			success = false;
			message = "未知异常，根据剧组名称查询剧组信息失败！";
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 查询所有的 剧组题材
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryAllSubject")
	@ResponseBody
	public Map<String, Object> queryAllSubject (HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			List<CrewSubjectModel> subjectList = this.crewSubjectService.querySubjectList();
			
			resultMap.put("subjectList", subjectList);
		} catch (Exception e) {
			logger.error("发生未知异常", e);
			
			success = false;
			message = "发生未知异常";
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 普通用户新增剧组
	 * @param crewId	剧组ID
	 * @param crewName	剧组名称
	 * @param crewType	剧组类型，0:电影   1：电视剧 2-网剧   3-网大
	 * @param subject	题材
	 * @param recordNumber	备案号
	 * @param shootStartDate	开机时间
	 * @param shootEndDate	杀青时间
	 * @param company	制片
	 * @param director	导演
	 * @param scriptWriter	编剧
	 * @param mainactor	主演
	 * @param enterPassword	入组密码
	 * @param seriesNo	立项集数
	 * @param status 剧组状态
	 * @param coProduction 合拍协议，0：无，1：已签订
	 * @param budget 剧组执行预算
	 * @param coProMoney 合拍协议金额
	 * @param investmentRatio 我方投资比例
	 * @param remark 重要事项说明
	 * @return
	 */
	@RequestMapping("/saveCrewByNormalUser")
	@ResponseBody
	public Map<String, Object> saveCrewByNormalUser(HttpServletRequest request,
			String crewId, String crewName, Integer crewType, String subject,
			String recordNumber, String shootStartDate, String shootEndDate,
			String company, String director, String scriptWriter,
			String mainactor, String enterPassword, Integer seriesNo,
			Integer status, Integer coProduction, Double budget,
			Double coProMoney, Double investmentRatio, String remark, Boolean picFlag) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		UserInfoModel userInfo = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			//校验
			if (StringUtils.isBlank(crewName)) {
				throw new IllegalArgumentException("请填写剧组名称");
			}
			if (crewType == null) {
				throw new IllegalArgumentException("请选择剧组类型");
			}
			if (StringUtils.isBlank(enterPassword)) {
				throw new IllegalArgumentException("请设置入组密码");
			}
			if (!RegexUtils.regexFind("^[0-9]{6}$", enterPassword)) {
				throw new IllegalArgumentException("入组密码不符合规范");
			}
			if (!StringUtils.isBlank(shootStartDate) && !StringUtils.isBlank(shootEndDate)) {
				Date shootStart = sdf.parse(shootStartDate);
				Date shootEnd = sdf.parse(shootEndDate);
				
				if (shootStart.after(shootEnd)) {
					throw new IllegalArgumentException("杀青时间不能早于开机时间");
				}
			}
			if (StringUtils.isBlank(crewId) && userInfo.getUbCreateCrewNum() <= 0) {
				throw new IllegalArgumentException("您的建组机会已用完，请联系系统客服人员");
			}
			
			//新增剧组  新增剧组表记录、设置用户为剧组管理员、向session中存储当前剧组信息
			CrewInfoModel crewInfo = this.crewInfoService.saveCrewWithoutPic(
					crewId, crewName, crewType, subject, recordNumber,
					shootStartDate, shootEndDate, company, director,
					scriptWriter, mainactor, enterPassword, seriesNo, status,
					coProduction, budget, coProMoney, investmentRatio, remark,
					userInfo, picFlag);
			
			//向session中存储当前剧组信息
			HttpSession session = request.getSession();
			
			//剧组信息
			session.removeAttribute(Constants.SESSION_CREW_INFO);
			session.setAttribute(Constants.SESSION_CREW_INFO, crewInfo);
			
			if (StringUtils.isBlank(crewId)) {
				//菜单列表
				List<Map<String,Object>> menuList = authorityService.queryUserAuthority(userInfo.getUserId(), crewInfo.getCrewId());
				session.setAttribute("menuTree", new Gson().toJson(menuList));
				
				//权限列表
				List<Map<String, Object>> userAuthList = this.authorityService.queryEffectiveAuthByUserAndPlantform(crewInfo.getCrewId(), userInfo.getUserId(), AuthorityPlatform.PC.getValue());
				Map<String, Object> authCodeMap = new HashMap<String, Object>();
				for (Map<String, Object> auth : userAuthList) {
					String authCode = (String) auth.get("authCode");
					int readonly = (Integer) auth.get("readonly");
					
					if (!StringUtils.isBlank(authCode)) {
						authCodeMap.put(authCode, readonly);
					}
				}
				session.setAttribute(Constants.SESSION_USER_AUTH_MAP, authCodeMap);
				session.setAttribute(Constants.SESSION_IFCHECK, "OK");
			}
			this.sysLogService.saveSysLog(request, "普通用户新增剧组", Constants.TERMINAL_PC, CrewInfoModel.TABLE_NAME, crewInfo.getCrewId(), 1);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			logger.error("发生未知异常", e);
			
			success = false;
			message = "发生未知异常";
			this.sysLogService.saveSysLog(request, "保存剧组信息失败：" + e.getMessage(), Constants.TERMINAL_PC, CrewInfoModel.TABLE_NAME, crewId, 6);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 系统管理员新增剧组
	 * @param crewName	剧组名称
	 * @param crewType	剧组类型，0:电影   1：电视剧 2-网剧   3-网大
	 * @param projectType 项目类型
	 * @param allowExport 是否允许导出
	 * @param subject	题材
	 * @param recordNumber	备案号
	 * @param startDate	账号开始时间
	 * @param endDate	账号结束时间
	 * @param shootStartDate	开机时间
	 * @param shootEndDate	杀青时间
	 * @param company	制片
	 * @param director	导演
	 * @param scriptWriter	编剧
	 * @param mainactor	主演
	 * @param enterPassword	入组密码
	 * @param seriesNo	立项集数
	 * @param status 剧组状态
	 * @param coProduction 合拍协议，0：无，1：已签订
	 * @param budget 剧组执行预算
	 * @param coProMoney 合拍协议金额
	 * @param investmentRatio 我方投资比例
	 * @param remark 重要事项说明
	 * @return
	 */
	@RequestMapping("/saveCrewByAdmin")
	@ResponseBody
	public Map<String, Object> saveCrewByAdmin(HttpServletRequest request,
			String crewId, String crewName, Integer crewType,
			Integer projectType, boolean allowExport, String subject,
			String recordNumber, String shootStartDate, String shootEndDate,
			String startDate, String endDate, String company, String director,
			String scriptWriter, String mainactor, String enterPassword,
			Integer seriesNo, Integer status, Integer coProduction,
			Double budget, Double coProMoney, Double investmentRatio,
			String remark) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		UserInfoModel userInfo = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			//校验
			if (StringUtils.isBlank(crewName)) {
				throw new IllegalArgumentException("请填写剧组名称");
			}
			if (crewType == null) {
				throw new IllegalArgumentException("请选择剧组类型");
			}
			if (projectType == null) {
				throw new IllegalArgumentException("请选择项目类型");
			}
			if (StringUtils.isBlank(enterPassword)) {
				throw new IllegalArgumentException("请设置入组密码");
			}
			if (!RegexUtils.regexFind("^[0-9]{6}$", enterPassword)) {
				throw new IllegalArgumentException("入组密码不符合规范");
			}
			if (StringUtils.isBlank(startDate)) {
				throw new IllegalArgumentException("请填写账号开始时间");
			}
			if (StringUtils.isBlank(endDate)) {
				throw new IllegalArgumentException("请填写账号结束时间");
			}
			if (!StringUtils.isBlank(startDate) && !StringUtils.isBlank(endDate)) {
				Date start = sdf.parse(startDate);
				Date end = sdf.parse(endDate);
				
				if (start.after(end)) {
					throw new IllegalArgumentException("账号结束时间不能早于账号开始时间");
				}
			}
			if (!StringUtils.isBlank(shootStartDate) && !StringUtils.isBlank(shootEndDate)) {
				Date shootStart = sdf.parse(shootStartDate);
				Date shootEnd = sdf.parse(shootEndDate);
				
				if (shootStart.after(shootEnd)) {
					throw new IllegalArgumentException("杀青时间不能早于开机时间");
				}
			}
			
			//新增剧组  新增剧组表记录
			this.crewInfoService.saveCrewByAdmin(crewId, crewName, crewType,
					projectType, allowExport, subject, recordNumber, startDate,
					endDate, shootStartDate, shootEndDate, company, director,
					scriptWriter, mainactor, enterPassword, seriesNo, status,
					coProduction, budget, coProMoney, investmentRatio, remark,
					userInfo, null);
			
			this.sysLogService.saveSysLog(request, "系统管理员新增剧组", Constants.TERMINAL_PC, CrewInfoModel.TABLE_NAME, crewId, 1);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			logger.error("发生未知异常", e);
			
			success = false;
			message = "发生未知异常";
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 根据剧组名称关键字模糊查询剧组
	 * @param keyword
	 * @param page
	 * @return
	 */
	@RequestMapping("/queryByKeyword")
	@ResponseBody
	public Map<String, Object> queryByKeyworkd(HttpServletRequest request, String keyword, Page page) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		UserInfoModel sessionUser = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);
		String userId = sessionUser.getUserId();
		
		try {
			List<Map<String, Object>> crewList = this.crewInfoService.queryCrewInfoByKeyword(keyword, userId, page);
			for (Map<String, Object> map : crewList) {
				Date shootStartDate = (Date) map.get("shootStartDate");
				Date shootEndDate = (Date) map.get("shootEndDate");
				
				String shootStartDateStr = "";
				if (shootStartDate != null) {
					shootStartDateStr = sdf.format(shootStartDate);
				}
				String shootEndDateStr = "";
				if (shootEndDate != null) {
					shootEndDateStr = sdf.format(shootEndDate);
				}
				
				map.put("shootStartDate", shootStartDateStr);
				map.put("shootEndDate", shootEndDateStr);
			}
			
			int totalPagenum = page.getTotal();
			
			resultMap.put("totalPagenum", totalPagenum);
			resultMap.put("crewList", crewList);
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 申请加入指定剧组
	 * @param request
	 * @param crewId	申请加入的剧组ID
	 * @param enterPassword	入组密码
	 * @param roleIds 申请担任的角色IDs,多个角色用逗号隔开
	 * @param remark	备注
	 * @return
	 */
	@RequestMapping("/applyToJoinCrew")
	@ResponseBody
	public Map<String, Object> applyToJoinCrew(HttpServletRequest request, String crewId, String enterPassword, String roleIds, String remark) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		
		UserInfoModel sessionUser = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);
		String userId = sessionUser.getUserId();
		
		try {
			MobileUtils.checkUserValid(userId);
			if (StringUtils.isBlank(crewId)) {
				throw new IllegalArgumentException("请选择想要加入的剧组");
			}
			if (StringUtils.isBlank(enterPassword)) {
				throw new IllegalArgumentException("请输入入组密码");
			}
			if (StringUtils.isBlank(roleIds)) {
				throw new IllegalArgumentException("请选择职务信息");
			}
			
			List<Map<String, Object>> roleInfoList = this.sysRoleInfoService.queryByIdsWithParentInfo(roleIds);
			
			String roleNames = "";
			for (Map<String, Object> map : roleInfoList) {
				String roleName = (String) map.get("roleName");
				String parentName = (String) map.get("parentName");
				
				roleNames +=  parentName + "-" + roleName + ",";
			}
			
			this.userService.joinCrew(userId, crewId, enterPassword, roleIds, roleNames.substring(0, roleNames.length() - 1), remark);
			
			success = true;
			
			this.sysLogService.saveSysLog(request, "申请加入指定剧组", Constants.TERMINAL_PC, JoinCrewApplyMsgModel.TABLE_NAME, crewId, 1);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "申请加入指定剧组失败：" + e.getMessage(), Constants.TERMINAL_PC, JoinCrewApplyMsgModel.TABLE_NAME, crewId, 6);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 获取当前剧组信息
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryCurrentCrewInfo")
	@ResponseBody
	public Map<String, Object> queryCurrentCrewInfo (HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		try {
			String crewId = this.getCrewId(request);
			CrewInfoModel crewInfo = this.crewInfoService.queryById(crewId);
			
			Map<String, Object> crewInfoMap = new HashMap<String, Object>();
			crewInfoMap.put("crewId", crewInfo.getCrewId());
			crewInfoMap.put("crewName", crewInfo.getCrewName());
			crewInfoMap.put("crewType", crewInfo.getCrewType());
			crewInfoMap.put("projectType", crewInfo.getProjectType());
			crewInfoMap.put("company", crewInfo.getCompany());
			crewInfoMap.put("startDate", this.sdf1.format(crewInfo.getStartDate()));
			crewInfoMap.put("endDate", this.sdf1.format(crewInfo.getEndDate()));
			if (crewInfo.getShootStartDate() != null) {
				crewInfoMap.put("shootStartDate", this.sdf1.format(crewInfo.getShootStartDate()));
			}
			if (crewInfo.getShootEndDate() != null) {
				crewInfoMap.put("shootEndDate", this.sdf1.format(crewInfo.getShootEndDate()));
			}
			crewInfoMap.put("shootCycle", crewInfo.getShootCycle());
			crewInfoMap.put("subject", crewInfo.getSubject());
			crewInfoMap.put("shootlocation", crewInfo.getShootlocation());
			crewInfoMap.put("director", crewInfo.getDirector());
			crewInfoMap.put("scriptWriter", crewInfo.getScriptWriter());
			crewInfoMap.put("mainactor", crewInfo.getMainactor());
			crewInfoMap.put("status", crewInfo.getStatus());
			crewInfoMap.put("recordNumber", crewInfo.getRecordNumber());
			crewInfoMap.put("enterPassword", crewInfo.getEnterPassword());
//			crewInfoMap.put("allowExport", crewInfo.getAllowExport());
			crewInfoMap.put("refreshAuth", crewInfo.getRefreshAuth());
			crewInfoMap.put("seriesNo", crewInfo.getSeriesNo());
			crewInfoMap.put("coProduction", crewInfo.getCoProduction());
			crewInfoMap.put("coProMoney", crewInfo.getCoProMoney());
			crewInfoMap.put("budget", crewInfo.getBudget());
			crewInfoMap.put("investmentRatio", crewInfo.getInvestmentRatio());
			crewInfoMap.put("remark", crewInfo.getRemark());
			crewInfoMap.put("isStop", crewInfo.getIsStop());
			
			String picPath = crewInfo.getPicPath();
			if (!StringUtils.isBlank(picPath)) {
				picPath = FileUtils.genPreviewPath(picPath);
			}
			crewInfoMap.put("picPath", picPath);
			
			resultMap.put("crewInfo", crewInfoMap);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
		
	}
	
	/**
	 * 刷新剧照
	 * 把系统中原有的没有剧照的剧组，剧照设置成默认图片
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/refreshCrewImg")
	public Map<String, Object> refreshCrewImg(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		try {
			List<CrewInfoModel> toUpdateCrewList = new ArrayList<CrewInfoModel>();
			
			List<CrewInfoModel> crewInfoList = this.crewInfoService.queryManyByMutiCondition(null, null);
			for (CrewInfoModel crewInfo : crewInfoList) {
				String crewName = crewInfo.getCrewName();
				String picPath = crewInfo.getPicPath();
				
				if (StringUtils.isBlank(picPath)) {
					picPath = this.crewInfoService.genDefaultPic(crewName);
					crewInfo.setPicPath(picPath);
					toUpdateCrewList.add(crewInfo);
				}
			}
			
			this.crewInfoService.updateMany(toUpdateCrewList);
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
		
	}
	
	/**
	 * 查询剧组相关信息记录数
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryCrewInfoNum")
	@ResponseBody
	public Map<String, Object> queryCrewInfoNum(HttpServletRequest request, String crewId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if(StringUtils.isBlank(crewId)) {
				crewId = this.getCrewId(request);
			}
			Map<String, Object> map = crewClearService.queryCrewInfoNum(crewId);
			resultMap.put("crewInfoNum", map);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询剧组相关信息记录数失败！";
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 清除剧组信息
	 * @param request
	 * @param infoIds
	 * @return
	 */
	@RequestMapping("/clearCrewInfo")
	@ResponseBody
	public Map<String, Object> clearCrewInfo(HttpServletRequest request, String infoIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			crewClearService.clearCrewInfo(crewId, infoIds);
			message = "清除成功！";
			
			this.sysLogService.saveSysLog(request, "清除剧组信息", Constants.TERMINAL_PC, CrewInfoModel.TABLE_NAME, crewId, 3);
		} catch (Exception e) {
			success = false;
			message = "未知异常，清除失败！";
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 删除剧组
	 * @param request
	 * @param infoIds
	 * @return
	 */
	@RequestMapping("/deleteCrew")
	@ResponseBody
	public Map<String, Object> deleteCrew(HttpServletRequest request, String crewId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			crewClearService.deleteCrewInfo(crewId);
			message = "删除成功！";
			
			this.sysLogService.saveSysLog(request, "删除剧组", Constants.TERMINAL_PC, CrewInfoModel.TABLE_NAME, crewId, 3);
		} catch (Exception e) {
			success = false;
			message = "未知异常，删除剧组失败！";
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 根据条件查询剧组,用于用户管理-添加剧组
	 * @param currentUserId 所要排除的用户id
	 */
	@ResponseBody
	@RequestMapping("searchAllCrew")
	public Map<String,Object> searchAllCrew(HttpServletRequest request, CrewInfoFilter filter, String currentUserId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			List<Map<String, Object>> resultList = this.crewInfoService
					.searchAllCrew(filter, currentUserId);
			resultMap.put("result", resultList);
		} catch (Exception e) {
			success = false;
			message = "未知异常，根据条件查询剧组失败！";
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 批量进行剧组与用户关联
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/addUserToCrew")
	public Map<String ,Object> addUserToCrew(HttpServletRequest request, String userId,String crewIds, String roleIds) throws Exception{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crew[] = crewIds.split(",");
			for (int i = 0; i < crew.length; i++) {
				String id = UUIDUtils.getId();
				//剧组用户关联关系表tab_crew_user_map
//				this.crewInfoService.addCrewUser(id, crew[i], userId, null, 0, 1, 0, "0");
				//把用户加入到剧组中
				this.userService.addUserToCrew(crew[i], userId, roleIds);
			}
			
			this.sysLogService.saveSysLog(request, "批量进行用户与剧组关联(" + crew.length + ")",
					Constants.TERMINAL_PC, "tab_crew_user_map", crewIds, 1);
		} catch (Exception e) {
			success = false;
			message = "未知异常，批量进行剧组与用户关联失败！";
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 获取剧组权限信息
	 * @param request
	 * @param userId
	 * @return
	 */
	@RequestMapping("/queryCrewAuthList")
	@ResponseBody
	public Map<String, Object> queryCrewAuthList(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		String crewId = this.getCrewId(request);
		
		try {
			//剧组权限信息
			List<CrewAuthDto> appAuthList = new ArrayList<CrewAuthDto>();	//app端权限
			List<CrewAuthDto> pcAuthList = new ArrayList<CrewAuthDto>();	//pc端权限
			
			//系统中所有的权限信息
			List<AuthorityModel> authList = this.authorityService.queryAuthByPlatformWithoutAdmin(null);
			//剧组已经有的权限信息
			List<CrewAuthMapModel> ownAuthList = this.crewAuthMapService.queryByCrewId(crewId);
			//剧组权限列表
			List<CrewAuthDto> crewAuthList = this.loopAuthList(authList, new ArrayList<CrewAuthDto>(), ownAuthList);
			
			for (CrewAuthDto crewAuth : crewAuthList) {
				int authPlatform = crewAuth.getAuthPlantform();
				if (authPlatform == AuthorityPlatform.Mobile.getValue()) {
					appAuthList.add(crewAuth);
				}
				if (authPlatform == AuthorityPlatform.PC.getValue()) {
					pcAuthList.add(crewAuth);
				}
				if (authPlatform == AuthorityPlatform.Common.getValue()) {
					appAuthList.add(crewAuth);
					pcAuthList.add(crewAuth);
				}
			}
			
			resultMap.put("appAuthList", appAuthList);
			resultMap.put("pcAuthList", pcAuthList);
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message,ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 遍历权限元素
	 * 按照父子管理整理出格式
	 * 
	 * 此处遍历的原则是从最底层权限一层一层向上剥离
	 * @param authList	系统中所有权限信息
	 * @param roleAuthList	封装后的用户权限信息
	 * @param ownAuthList	用户拥有的权限信息
	 * @return
	 */
	private List<CrewAuthDto> loopAuthList (List<AuthorityModel> authList, List<CrewAuthDto> crewAuthList, List<CrewAuthMapModel> ownAuthList) {
		List<AuthorityModel> parentAuthList = new ArrayList<AuthorityModel>();
		List<AuthorityModel> childAuthList = new ArrayList<AuthorityModel>();	//当前层中的子权限
		for (AuthorityModel fauth : authList) {
			String fauthId = fauth.getAuthId();
			String fparentId = fauth.getParentId();
			
			boolean isParent = false;
			boolean ischild = false;
			for (AuthorityModel sauth : authList) {
				String sauthId = sauth.getAuthId();
				String sparentId = sauth.getParentId();
				
				if (fauthId.equals(sparentId)) {
					isParent = true;
				}
				
				if (fparentId.equals(sauthId)) {
					ischild = true;
				}
			}
			
			//此处parentAuthList和childAuthList数据在多层权限结构中必然后交集
			//fauth为父权限中的一个
			if (isParent) {
				parentAuthList.add(fauth);
			}
			//fauth为子权限中的一个，如果fauth既不是父权限，也不是子权限，则说明，fauth为系统权限中最顶层的叶子节点权限
			if (ischild || (!isParent && !ischild)) {
				childAuthList.add(fauth);
			}
		}
		
		//childAuthList中存在parentAuthList不存在的权限就是当前循环中的叶子权限
		List<AuthorityModel> lastAuthList = new ArrayList<AuthorityModel>();
		for (AuthorityModel cauth : childAuthList) {
			boolean exist = false;
			for (AuthorityModel pauth : parentAuthList) {
				if (cauth.getAuthId().equals(pauth.getAuthId())) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				lastAuthList.add(cauth);
			}
		}
		
		List<CrewAuthDto> myCrewAuthDtoList = new ArrayList<CrewAuthDto>();
		
		/*
		 * 为最后的结果字段赋值
		 * lastAuthList表示当前循环中的叶子权限
		 * 但是相对于上一层传过来的userAuthList，lastAuthList中有些数据为userAuthList中数据的父权限
		 * 因此，此处对比出lastAuthList中每个权限的子权限，然后为响应字段赋值
		 * 
		 * 如果数据在userAuthList存在而lastAuthList中不存在，则说明此数据层级为当前循环的叶子权限
		 */
		for (AuthorityModel lauth : lastAuthList) {
			String authId = lauth.getAuthId();
			
			List<CrewAuthDto> subCrewAuthDto = new ArrayList<CrewAuthDto>();
			for (CrewAuthDto crewAuth : crewAuthList) {
				String uparentId = crewAuth.getParentId();
				
				if (uparentId.equals(authId)) {
					subCrewAuthDto.add(crewAuth);
				}
			}
			
			CrewAuthDto crewAuthDto = new CrewAuthDto();
			crewAuthDto.setAuthId(authId);
			crewAuthDto.setParentId(lauth.getParentId());
			crewAuthDto.setAuthName(lauth.getAuthName());
			crewAuthDto.setSequence(lauth.getSequence());
			crewAuthDto.setSubAuthList(subCrewAuthDto);
			crewAuthDto.setDifferInRAndW(lauth.getDifferInRAndW());
			crewAuthDto.setAuthPlantform(lauth.getAuthPlantform());
			
			boolean hasAuth = false;
			for (CrewAuthMapModel roleAuthMap : ownAuthList) {
				if (authId.equals(roleAuthMap.getAuthId())) {
					crewAuthDto.setHasAuth(true);
					crewAuthDto.setReadonly(roleAuthMap.getReadonly());
					
					hasAuth = true;
					break;
				}
			}
			
			if (!hasAuth) {
				crewAuthDto.setHasAuth(false);
				crewAuthDto.setReadonly(true);
			}
			
			myCrewAuthDtoList.add(crewAuthDto);
		}
		
		for (CrewAuthDto crewAuth : crewAuthList) {
			boolean exists = false;
			for (AuthorityModel lauth : lastAuthList) {
				if (crewAuth.getParentId().equals(lauth.getAuthId())) {
					exists = true;
					break;
				}
			}
			
			if (!exists) {
				myCrewAuthDtoList.add(crewAuth);
			}
		}
		
		Collections.sort(myCrewAuthDtoList, new Comparator<CrewAuthDto>() {
			@Override
			public int compare(CrewAuthDto o1, CrewAuthDto o2) {
				return o1.getSequence() - o2.getSequence();
			}
		});
		
		//如果全是叶子权限了，说明已经遍历到最顶层了
		if (parentAuthList.size() > 0) {
			//把最底层的权限剥掉后，继续遍历，一直到只剩下最顶层的为止
			authList.removeAll(lastAuthList);
			myCrewAuthDtoList = this.loopAuthList(authList, myCrewAuthDtoList, ownAuthList);
		}
		
		return myCrewAuthDtoList;
	}
	
	/**
	 * 保存剧组的权限信息
	 * @param request
	 * @param operateType	操作类型 1：新增  2：修改  3：删除
	 * @param authId 权限ID
	 * @param readonly	是否只读
	 * @return
	 */
	@RequestMapping("/saveCrewAuthInfo")
	@ResponseBody
	public Map<String, Object> saveCrewAuthInfo(HttpServletRequest request, int operateType, String authId, Boolean readonly) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		try {
			
			if (StringUtils.isBlank(authId)) {
				throw new IllegalArgumentException("请选择需要操作的权限");
			}
			
			String crewId = this.getCrewId(request);
			
			if (readonly == null) {
				readonly = false;
			}
			
			CrewAuthMapModel crewAuthMap = this.crewAuthMapService.queryByCrewAuthId(crewId, authId);
			
			//删除
			if (operateType == 3) {
				//需要查询出所有的子权限，然后把剧组和所有子权限的关联关系删掉,所有权限（包括有效无效）
				List<CrewAuthMapModel> crewAuthMapList = this.crewAuthMapService.queryByCrewAuthIdWithSubAuth(crewId, authId, 1);
				for (CrewAuthMapModel map : crewAuthMapList) {
					this.crewAuthMapService.deleteById(crewId, authId, map.getMapId());
				}
			}
			
			//新增
			if ((operateType != 3 && crewAuthMap == null) || operateType == 1) {
				crewAuthMap = new CrewAuthMapModel();
				crewAuthMap.setAuthId(authId);
				crewAuthMap.setCrewId(crewId);
				crewAuthMap.setMapId(UUIDUtils.getId());
				crewAuthMap.setReadonly(readonly);
				this.crewAuthMapService.addOne(crewId, crewAuthMap);
			}
			
			//修改
			if (operateType != 3 && crewAuthMap != null) {
				crewAuthMap.setAuthId(authId);
				crewAuthMap.setCrewId(crewId);
				crewAuthMap.setReadonly(readonly);
				this.crewAuthMapService.updateOne(crewAuthMap);
			}
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 判断剧组是否已设置权限
	 * @param request
	 * @return
	 */
	@RequestMapping("/isCrewHasAuth")
	@ResponseBody
	public Map<String, Object> isCrewHasAuth(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		try {
			String crewId = this.getCrewId(request);
			//剧组已经有的权限信息
			List<CrewAuthMapModel> ownAuthList = this.crewAuthMapService.queryByCrewId(crewId);
			
			if(ownAuthList == null || ownAuthList.size() == 0) {
				throw new IllegalArgumentException("请先设置剧组权限");
			}
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";			
			logger.error("未知异常", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 设置剧组是否停用
	 * @param request
	 * @return
	 */
	@RequestMapping("/updateCrewIsStop")
	@ResponseBody
	public Map<String, Object> updateCrewIsStop(HttpServletRequest request, boolean isStop) {
		Map<String, Object> resultMap = new HashMap<String, Object>();		
		boolean success = true;
		String message = "";		
		try {
			String crewId = this.getCrewId(request);
			CrewInfoModel crewInfo = this.crewInfoService.queryById(crewId);
			if(crewInfo == null) {
				throw new IllegalArgumentException("请提供剧组信息");
			}
			crewInfo.setIsStop(isStop);
			this.crewInfoService.updateCrew(crewInfo);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常,设置剧组是否停用失败";
			logger.error(message, e);
		}		
		resultMap.put("success", success);
		resultMap.put("message", message);		
		return resultMap;
	}
	
	/**
	 * 上传剧组照片
	 * @param request
	 * @param crewId
	 * @param file
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/uploadCrewPicture")
	public Map<String, Object> uploadCrewPicture(HttpServletRequest request, String crewId, MultipartFile file){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			if (StringUtils.isBlank(crewId)) {
				throw new IllegalArgumentException("请选择要上传图片的剧组！");
			}
			
			//根据id查询出当前剧组的信息
			CrewInfoModel model = this.crewInfoService.queryById(crewId);
			
			//删除旧的图片
			if (model != null && StringUtils.isNotEmpty(model.getPicPath())) {
				FileUtils.deleteFile(model.getPicPath());
			}
			
			//上传图片
			//上传新的图片
			String storePath = this.uploadCrewPic(file);
			model.setPicPath(storePath);
			this.crewInfoService.updateCrewPicPath(model);
			
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，上传失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	
	/**
	 * 辅助方法:上传剧组图片
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws FileUploadException
	 */
	private String uploadCrewPic(MultipartFile file) throws FileNotFoundException, IOException, FileUploadException {
		Properties properties = PropertiesUitls.fetchProperties("/config.properties");
		String baseStorePath = properties.getProperty("fileupload.path");
		String storePath = baseStorePath + "crew";
		
		//把附件上传到服务器(高清原版)
        Map<String, String> fileMap = FileUtils.uploadFile(file, false, storePath);
		String hdStorePath = fileMap.get("storePath");
		String storeName = fileMap.get("fileStoreName");
		
		return hdStorePath + storeName;
	}
}
