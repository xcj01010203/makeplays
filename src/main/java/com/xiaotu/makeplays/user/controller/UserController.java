package com.xiaotu.makeplays.user.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.xiaotu.makeplays.authority.controller.dto.UserAuthDto;
import com.xiaotu.makeplays.authority.model.AuthorityModel;
import com.xiaotu.makeplays.authority.model.UserAuthMapModel;
import com.xiaotu.makeplays.authority.model.constants.AuthorityPlatform;
import com.xiaotu.makeplays.authority.service.AuthorityService;
import com.xiaotu.makeplays.authority.service.UserAuthMapService;
import com.xiaotu.makeplays.community.model.WorkExperienceInfoModel;
import com.xiaotu.makeplays.community.service.WorkExperienceInfoService;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.model.CrewRoleUserMapModel;
import com.xiaotu.makeplays.crew.model.CrewUserFilter;
import com.xiaotu.makeplays.crew.model.CrewUserMapModel;
import com.xiaotu.makeplays.crew.model.constants.CrewUserStatus;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.crew.service.CrewRoleUserMapService;
import com.xiaotu.makeplays.feedback.service.FeedbackService;
import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;
import com.xiaotu.makeplays.roleactor.model.constants.ViewRoleType;
import com.xiaotu.makeplays.roleactor.service.ViewRoleService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.sysrole.model.SysroleInfoModel;
import com.xiaotu.makeplays.sysrole.model.UserRoleMapModel;
import com.xiaotu.makeplays.sysrole.service.SysRoleInfoService;
import com.xiaotu.makeplays.sysrole.service.UserRoleMapService;
import com.xiaotu.makeplays.user.controller.filter.UserFilter;
import com.xiaotu.makeplays.user.model.JoinCrewApplyMsgModel;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.model.constants.JoinCrewAuditStatus;
import com.xiaotu.makeplays.user.service.JoinCrewApplyMsgService;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.MD5Util;
import com.xiaotu.makeplays.utils.MsgUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.RegexUtils;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.verifycode.model.VerifyCodeInfoModel;
import com.xiaotu.makeplays.verifycode.model.constants.VerifyCodeType;
import com.xiaotu.makeplays.verifycode.service.VerifyCodeInfoService;

/**
 * 用户管理
 * @author xuchangjian 2016-10-11下午5:27:00
 */
@Controller
@RequestMapping("/userManager")
public class UserController extends BaseController{
	
	Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private FeedbackService feedbackService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthorityService authorityService;
	
	@Autowired
	private SysRoleInfoService sysRoleInfoService;
	
	@Autowired
	private CrewInfoService crewInfoService;
	
	@Autowired
	private VerifyCodeInfoService verifyCodeInfoService;
	
	@Autowired
	private UserAuthMapService userAuthMapService;
	
	@Autowired
	private UserRoleMapService userRoleMapService;
	
	@Autowired
	private ViewRoleService viewRoleService;
	
	@Autowired
	private CrewRoleUserMapService crewRoleUserMapService;
	
	@Autowired
	private JoinCrewApplyMsgService joinCrewApplyMsgService;
	
	@Autowired
	private WorkExperienceInfoService workService;
	
	/**
	 * 跳转到关于我们页面
	 */
	@RequestMapping("/toAboutUsPage")
	public ModelAndView toAboutUsPage(){
		ModelAndView mv = new ModelAndView("/usercenter/aboutUs");
		return mv;
	}
	
	/**
	 * 用户详细信息
	 */
	@ResponseBody
	@RequestMapping("/toUserDetailInfoPage")
	public ModelAndView toUserDetailInfoPage(){
		ModelAndView mv = new ModelAndView("/usercenter/userDetailInfo");
		return mv;
	}
	
	/**
	 * 跳转到批量设置用户权限页面
	 */
	@ResponseBody
	@RequestMapping("/toUserMultiSetAuthPage")
	public ModelAndView toUserMultiSetAuthPage(String userIds){
		ModelAndView mv = new ModelAndView("/user/userMultiSetAuth");

		mv.addObject("userIds", userIds);
		return mv;
	}
	
	/**
	 * 跳转到个人中心页面
	 * @param activeTagType 触发的菜单类型
	 * 1-个人信息  2-我的消息  3-我的剧组  4-用户协议  5-意见反馈  6-关于我们  7-新建/加入剧组
	 */
	@RequestMapping("/toUserCenterPage")
	public ModelAndView toUserCenterPage(Integer activeTagType) {
		ModelAndView mv = new ModelAndView("/usercenter/userCenter");
		if (activeTagType == null) {
			activeTagType = 3;
		}
		mv.addObject("activeTagType", activeTagType);
		return mv;
	}
	
	/**
	 * 跳转到用户协议页面
	 * @return
	 */
	@RequestMapping("/toAgreementPage")
	public ModelAndView toAgreementPage() {
		ModelAndView view = new ModelAndView("/agreement/agreement");
		return view;
	}
	
	/**
	 * 注册用户
	 * @param phone	手机号
	 * @param validCode	验证码
	 * @param realName	真实姓名
	 * @param password	密码
	 * @param confirmPassword	确认密码
	 * @return
	 */
	@RequestMapping("/register")
	@ResponseBody
	public Object register(HttpServletRequest request, String phone, String verifyCode, String realName, String password, String confirmPassword) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try{
			this.checkRegistUserInfo(phone, verifyCode, realName, password, confirmPassword);
			
			this.userService.registerOneUser(phone, verifyCode, MD5Util.MD5(password), realName, null, null, null, null);
			
			this.sysLogService.saveSysLog(request, "注册用户", Constants.TERMINAL_PC, UserInfoModel.TABLE_NAME, null, 1);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			logger.error(message);
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "注册用户失败：" + e.getMessage(), Constants.TERMINAL_PC, UserInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 校验注册用户信息
	 * @param phone	手机号
	 * @param verifyCode	验证码
	 * @param type	验证码类型
	 * @param password	密码（MD5密文）
	 * @param realName	姓名
	 * @param sex	性别
	 * @param token	
	 * @throws Exception
	 */
	private void checkRegistUserInfo(String phone, String verifyCode, String realName, String password, String confirmPassword) throws Exception {
		//基本信息
		if (StringUtils.isBlank(realName)) {
			throw new IllegalArgumentException("请填写姓名");
		}
		if (StringUtils.isBlank(password)) {
			throw new IllegalArgumentException("密码不能为空");
		}
		if (StringUtils.isBlank(confirmPassword)) {
			throw new IllegalArgumentException("确认密码不能为空");
		}
		if (!password.equals(confirmPassword)) {
			throw new IllegalArgumentException("密码和确认密码不一致");
		}
		if (password.length() < 6 || password.length() > 18) {
			throw new IllegalArgumentException("请输入6-18位密码");
		}
		
		//校验手机号是否合法
		if (StringUtils.isBlank(phone)) {
			throw new IllegalArgumentException("请填写手机号");
		}
		if (!RegexUtils.regexFind(Constants.REGEX_PHONE_NUMBER, phone)) {
			throw new IllegalArgumentException("手机号不合法");
		}
		
		//校验手机号验证码是否匹配
		if (StringUtils.isBlank(verifyCode)) {
			throw new IllegalArgumentException("请填写短信验证码");
		}
		VerifyCodeInfoModel validInfoModel = this.verifyCodeInfoService.queryByPhoneAndCode(phone, verifyCode, VerifyCodeType.Register.getValue());
		if (validInfoModel == null) {
			throw new IllegalArgumentException("验证码错误");
		}
		
		//校验手机号是否已存在
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("phone", phone);
		
		List<UserInfoModel> phoneUserList = this.userService.queryManyByMutiCondition(conditionMap, null);
		if (phoneUserList != null && phoneUserList.size() > 0) {
			throw new IllegalArgumentException("手机号已注册");
		}
	}
	
	/**
	 * 查询登录用户信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryLoginUserInfo")
	public Map<String, Object> queryLoginUserInfo(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			UserInfoModel userInfo = this.getSessionUserInfo(request);
			UserInfoModel myUserInfo = this.userService.queryById(userInfo.getUserId());
			
			String imgUrl = myUserInfo.getBigImgUrl();
			if (StringUtils.isBlank(imgUrl)) {
				Resource resource = new ClassPathResource("/config.properties");
				Properties props = PropertiesLoaderUtils.loadProperties(resource);
				String serverPath = (String) props.get("server.basepath");
				imgUrl = serverPath + Constants.DEFAULT_USER_PIC;
			} else {
				imgUrl = FileUtils.genPreviewPath(imgUrl);
			}
			
			myUserInfo.setBigImgUrl(imgUrl);
			
			resultMap.put("userInfo", myUserInfo);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message);
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			logger.error(message);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
		
	}
	
	/**
	 * 跳转到剧组用户详细信息页面
	 * @return
	 */
	@RequestMapping("/toCrewUserDetailPage")
	public ModelAndView toCrewUserDetailPage(String aimUserId) {
		ModelAndView mv = new ModelAndView("/user/crewUserDetail");
		
		mv.addObject("aimUserId", aimUserId);
		return mv;
	}
	
	/**
	 * 跳转到选择用户入组页面
	 * @return
	 */
	@RequestMapping("/toSelectUserPage")
	public ModelAndView toSelectUserPage(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("/user/selectUser");
		
		String crewId = this.getCrewId(request);
		mv.addObject("crewId", crewId);
		return mv;
	}
	
	
	/**
	 * 跳转到用户管理页面
	 * @param type null或0：正常打开，1：打开某个用户
	 * @param userInfo 用户姓名或手机
	 * @return
	 */
	@RequestMapping("/toUserListPage")
	public ModelAndView toUserListPage(Integer type,String userId,String userInfo) {
		ModelAndView mv = new ModelAndView("/user/userList");
		if(type == null){
			type = 0;
		}
		mv.addObject("opentype", type);
		mv.addObject("openUserId", userId);
		mv.addObject("openUserInfo", userInfo);
		return mv;
	}
	
	/**
	 * 查询用户列表,分页
	 * @param request
	 * @param page
	 * @param userFilter
	 * @param sortdatafield
	 * @param sortorder
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/queryUserList")
	@ResponseBody
	public Map<String, Object> queryUserList(HttpServletRequest request,
			Page page, UserFilter userFilter, String sortdatafield,
			String sortorder) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			List<Map<String, Object>> list = userService.queryUserListByPage(page,userFilter,sortdatafield,sortorder);			
			page.setResultList(list);
			resultMap.put("result", page);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询用户信息列表失败！";
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}

	/**
	 * 不分页
	 * @param request
	 * @param userFilter
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryUserInfo")
	public Map<String, Object> queryUserInfo(HttpServletRequest request,UserFilter userFilter) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			List<Map<String, Object>> list = userService.queryUserListByPage(null, userFilter, null, null);
			resultMap.put("result", list);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询用户信息列表失败！";
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 保存新建用户
	 * @param userInfo
	 * @param request
	 * @return
	 */
	@RequestMapping("saveUser")
	public @ResponseBody Map<String, Object> saveUser(UserInfoModel userInfo,HttpServletRequest request,String roleId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if(null == userInfo){
				throw new IllegalArgumentException("请填写用户信息！");
			}
			
			//校验用户名不能为空
			if (StringUtils.isBlank(userInfo.getRealName())) {
				throw new IllegalArgumentException("请填写用户名！");
			}
			
			//校验手机号不能为空
			if (StringUtils.isBlank(userInfo.getPhone())) {
				throw new IllegalArgumentException("请填写手机号码！");
			}
			
			//校验手机号是否已存在
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("phone", userInfo.getPhone());
			
			List<UserInfoModel> phoneUserList = this.userService.queryManyByMutiCondition(conditionMap, null);
			//判断当前用户是否是修改或新建
			if (StringUtils.isNotBlank(userInfo.getUserId())) {
				//校验是否是原用户
				for (UserInfoModel model : phoneUserList) {
					//取出用户id
					String userId = model.getUserId();
					if (!userInfo.getUserId().equals(userId)) {
						throw new IllegalArgumentException("手机号已注册!");
					}
				}
			}else {
				if (phoneUserList != null && phoneUserList.size() > 0) {
					throw new IllegalArgumentException("手机号已注册");
				}
			}
			
			//修改用户信息不用判断手机号码		
			if(StringUtils.isNotBlank(userInfo.getPassword())){
				userInfo.setPassword(MD5Util.MD5(userInfo.getPassword()));
			}
			
			if(StringUtils.isBlank(userInfo.getUserId())){
//				userInfo.setType(Constants.USER_TYPE_NOT_ADMIN);
				userInfo.setUserId(UUIDUtils.getId());
				 
				userService.addUser(userInfo, roleId);
				this.sysLogService.saveSysLog(request, "添加用户", Constants.TERMINAL_PC, UserInfoModel.TABLE_NAME, null,1);
			}else{
				userService.updateUser(userInfo, roleId);
				this.sysLogService.saveSysLog(request, "修改用户", Constants.TERMINAL_PC, UserInfoModel.TABLE_NAME, userInfo.getUserId(),2);
			}
			message = "保存成功！";
			
		} catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，保存或修改用户信息失败！";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 根据用户ID查询用户信息
	 * @param userId  用户ID
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/queryUserById")
	@ResponseBody
	public Map<String, Object> queryUserById(String userId) {		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			UserInfoModel userInfo = userService.queryById(userId);
			//客服
			if(userInfo.getType() == 2) {
				String roleId = this.userRoleMapService.queryAllUserRoleIds(userInfo.getUserId());
				resultMap.put("roleId", roleId);
			}
			resultMap.put("result", userInfo);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询用户信息失败！";
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 普通用户切换剧组
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/switchCrew")
	public Map<String, Object> switchCrew(String crewId, HttpServletRequest request) throws Exception{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			HttpSession session = request.getSession();
			
			//用户信息
			UserInfoModel userInfo = (UserInfoModel) session.getAttribute(Constants.SESSION_USER_INFO);
			String userId = userInfo.getUserId();
			
			//当前剧组信息
			CrewInfoModel crewInfo = this.crewInfoService.queryById(crewId);
			String myCrewId = crewInfo.getCrewId();
			
			//切换用户默认剧组
			this.userService.switchCrew(userId, myCrewId);
			
			//所有剧组列表
			List<CrewInfoModel> userCrewList = this.crewInfoService.queryUserAllCrew(userId);
			
			//用户类型
			Integer loginUserType = (Integer) session.getAttribute("loginUserType");
			
			//菜单
			List<Map<String, Object>> menuList = authorityService.queryUserAuthority(userId, myCrewId);
			
			// 权限列表
			Map<String, Object> authCodeMap = new HashMap<String, Object>();
			List<Map<String, Object>> userAuthList = this.authorityService.queryEffectiveAuthByUserAndPlantform(crewInfo.getCrewId(), 
					userInfo.getUserId(), AuthorityPlatform.PC.getValue());
			
			for (Map<String, Object> auth : userAuthList) {
				String authCode = (String) auth.get("authCode");
				int readonly = (Integer) auth.get("readonly");

				if (!StringUtils.isBlank(authCode)) {
					authCodeMap.put(authCode, readonly);
				}
			}
			
			session.invalidate();
			session = request.getSession();
			session.setAttribute(Constants.SESSION_USER_INFO, userInfo); // 用户信息
			session.setAttribute("menuTree", new Gson().toJson(menuList)); // 菜单
			session.setAttribute(Constants.SESSION_CREW_INFO, crewInfo); // 当前剧组信息
			session.setAttribute(Constants.SESSION_CREWINFO_ALL, userCrewList); // 用户所有的剧组
			session.setAttribute(Constants.SESSION_USER_AUTH_MAP, authCodeMap); // 用户权限信息
			session.setAttribute(Constants.SESSION_LOGIN_USER_TYPE, loginUserType);
			session.setAttribute(Constants.SESSION_IFCHECK, "OK");
			
			this.sysLogService.saveSysLog(request, "普通用户切换剧组", Constants.TERMINAL_PC, "tab_crew_info", null,2);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {			
			success = false;
			message = "未知异常，普通用户切换剧组失败";
			logger.error(message, e);
			
			this.sysLogService.saveSysLog(request, "普通用户切换剧组失败：" + e.getMessage(), Constants.TERMINAL_PC, "tab_crew_info", null, SysLogOperType.ERROR.getValue());
		}
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	

	
	/**
	 * 客户服务切换剧组
	 * @throws Exception 
	 */
	@RequestMapping("/switchCrewForCustomerService")
	@ResponseBody
	public Map<String, Object> switchCrewForCustomerService(String crewId, HttpServletRequest request) throws Exception{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			HttpSession session = request.getSession();
			
			//用户信息
			UserInfoModel userInfo = (UserInfoModel) session.getAttribute(Constants.SESSION_USER_INFO);
			
			//当前剧组信息
			CrewInfoModel crewInfo = this.crewInfoService.queryById(crewId);
			
			//用户类型
			Integer loginUserType = (Integer) session.getAttribute("loginUserType");

			//切换用户默认剧组，总客服不需要
			if(loginUserType != 4 ) {
				this.userService.switchCrew(userInfo.getUserId(), crewInfo.getCrewId());
			}
			
			//客服类型
			String roleId = (String) session.getAttribute(Constants.SESSION_LOGIN_SERVICE_TYPE);
			
			//菜单
			List<Map<String,Object>> menuList = authorityService.getServiceMenu(roleId);
			
			// 权限列表
			Map<String, Object> authCodeMap = this.authorityService.getServiceAuth(roleId);
			
			session.invalidate();
			session = request.getSession();
			session.setAttribute(Constants.SESSION_USER_INFO, userInfo); // 用户信息
			session.setAttribute("menuTree", new Gson().toJson(menuList)); // 菜单
			session.setAttribute(Constants.SESSION_CREW_INFO, crewInfo); // 当前剧组信息
			session.setAttribute(Constants.SESSION_USER_AUTH_MAP, authCodeMap); // 用户权限信息
			session.setAttribute(Constants.SESSION_LOGIN_USER_TYPE, loginUserType);
			session.setAttribute(Constants.SESSION_LOGIN_SERVICE_TYPE, roleId);	//客服角色，即具体客服类型
			session.setAttribute(Constants.SESSION_IFCHECK, "OK");
			
			this.sysLogService.saveSysLog(request, "客服切换剧组", Constants.TERMINAL_PC, "tab_crew_user_map", null,2);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			logger.error("未知异常，客服切换剧组失败", e);
			
			success = false;
			message = "未知异常，客服切换剧组失败";
			
			this.sysLogService.saveSysLog(request, "客服切换剧组失败：" + e.getMessage(), Constants.TERMINAL_PC, "tab_crew_user_map", null, SysLogOperType.ERROR.getValue());
		}
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 客户服务切换剧组
	 * @throws Exception 
	 */
	@Deprecated
	@RequestMapping("/switchCrewForSerivce")
	public ModelAndView switchCrewForSerivce(String crewId, HttpServletRequest request) throws Exception{
		ModelAndView view = new ModelAndView("redirect:/toIndexPage");
		
		try {
			HttpSession session = request.getSession();
			
			//用户信息
			UserInfoModel userInfo = (UserInfoModel) session.getAttribute(Constants.SESSION_USER_INFO);
			
			//当前剧组信息
			CrewInfoModel crewInfo = this.crewInfoService.queryById(crewId);
			
			//用户类型
			Integer loginUserType = (Integer) session.getAttribute("loginUserType");
			
			//菜单
			List<Map<String,Object>> menuList = authorityService.getServiceMenu(Constants.ROLE_ID_CUSTOM_SERVICE);
			
			// 权限列表
			Map<String, Object> authCodeMap = this.authorityService.getServiceAuth(Constants.ROLE_ID_CUSTOM_SERVICE);
			
			session.invalidate();
			session = request.getSession();
			session.setAttribute(Constants.SESSION_USER_INFO, userInfo); // 用户信息
			session.setAttribute("menuTree", new Gson().toJson(menuList)); // 菜单
			session.setAttribute(Constants.SESSION_CREW_INFO, crewInfo); // 当前剧组信息
			session.setAttribute(Constants.SESSION_USER_AUTH_MAP, authCodeMap); // 用户权限信息
			session.setAttribute(Constants.SESSION_LOGIN_USER_TYPE, loginUserType);
			session.setAttribute(Constants.SESSION_IFCHECK, "OK");

			this.sysLogService.saveSysLog(request, "客服切换剧组", Constants.TERMINAL_PC, "tab_crew_user_map", null,2);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，找回密码失败", e);
		}
		
		
		return view;
	}
	
	/**
	 * 获取用户所在的剧组
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/queryUserCrews")
	@ResponseBody
	public Map<String,Object> queryUserCrews(String userId ) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			List<Map<String,Object>> resultList = this.userService.getCrewsByUserId(userId);
			resultMap.put("result", resultList);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询用户所在的剧组失败！";
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 批量为用户添加权限（初始化）
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/batchUpdateUserAuth")
	public List<Map<String,Object>> batchUpdateUserAuth(HttpServletRequest request) throws Exception{
		List<Map<String,Object>> list = userService.searchAllUser(new CrewUserFilter(),null,null);
		for (Map<String, Object> map : list) {
			if(map.get("roleId")!=null && StringUtils.isNotBlank(map.get("roleId").toString())){
				this.crewInfoService.updateUserAuthByRoleids(map.get("crewId").toString(), map.get("userId").toString(), map.get("roleId").toString());
			}
		}
		
//		this.sysLogService.saveSysLog(request, "批量为用户添加权限（初始化）", Constants.TERMINAL_PC, 
//				"tab_user_auth", null,1);
		
		return list;
	}
	
	/**
	 * 设置用户角色为客户服务
	 * @param userId
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/setCustomerService")
	public Map<String,Object> setCustomerService(String userId, HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			userService.setCustomerService(Constants.SYS_DEFULT_CREW_ID, getLoginUserId(request), userId);
			
			this.sysLogService.saveSysLog(request, "设置用户角色为客户服务", Constants.TERMINAL_PC, 
					CrewUserMapModel.TABLE_NAME, userId, 2);
		} catch (Exception e) {
			success = false;
			message = "未知异常，设置用户角色为客户服务失败！";
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;		
	}
	
	/**
	 * 删除系统用户
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("deleteUser")
	public Map<String,Object> deleteUser(HttpServletRequest request, String userId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			List<CrewUserMapModel> list = this.userService.queryUserAllcrew(userId);
			if(list == null || list.size() == 0){
				this.userService.deleteUser(userId);
				resultMap.put("isSuccess", 1);
			} else {
				resultMap.put("isSuccess", 2);
			}
			message = "删除成功";
			this.sysLogService.saveSysLog(request, "删除系统用户", Constants.TERMINAL_PC, UserInfoModel.TABLE_NAME, userId, 3);
		} catch (Exception e) {
			success = false;
			message = "未知异常，删除用户失败";
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 校验手机号是否已存在
	 * @param phone
	 * @return
	 */
	@RequestMapping("/checkPhoneExist")
	@ResponseBody
	public Map<String, Object> checkPhoneExist(String phone) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			if (StringUtils.isBlank(phone)) {
				throw new IllegalArgumentException("请填写手机号");
			}
			
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("phone", phone);
			
			List<UserInfoModel> phoneUserList = this.userService.queryManyByMutiCondition(conditionMap, null);
			
			boolean exists = false;
			if (phoneUserList != null && phoneUserList.size() > 0) {
				exists = true;
			}
			
			resultMap.put("exists", exists);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message,ie);
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
	 * 找回密码
	 * @param phone	手机号
	 * @param verifyCode	验证码
	 * @param newPassword	新密码
	 * @param token	
	 * @param clientType	客户端类型，1-IOS  2-Android  3-ipad
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/findbackPassword")
	public Map<String, Object> findbackPassword(HttpServletRequest request, String phone, String verifyCode, String newPassword, String confirmPassword) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		
		try {
			if (StringUtils.isBlank(newPassword)) {
				throw new IllegalArgumentException("密码不能为空");
			}
			if (!newPassword.equals(confirmPassword)) {
				throw new IllegalArgumentException("密码和确认密码不一致");
			}
			if (newPassword.length() < 6 || newPassword.length() > 18) {
				throw new IllegalArgumentException("请输入6-18位密码");
			}
			
			//校验手机号是否合法
			if (StringUtils.isBlank(phone)) {
				throw new IllegalArgumentException("请填写手机号");
			}
			if (!RegexUtils.regexFind(Constants.REGEX_PHONE_NUMBER, phone)) {
				throw new IllegalArgumentException("手机号不合法");
			}
			
			//校验验证码
			if (StringUtils.isBlank(verifyCode)) {
				throw new IllegalArgumentException("请填写短信验证码");
			}
			VerifyCodeInfoModel validInfoModel = this.verifyCodeInfoService.queryByPhoneAndCode(phone, verifyCode, VerifyCodeType.FindbackPassword.getValue());
			if (validInfoModel == null) {
				throw new IllegalArgumentException("验证码错误");
			}
			
			//校验用户信息
			UserInfoModel userInfo = this.userService.queryByPhone(phone);
			if (userInfo == null) {
				throw new IllegalArgumentException("该手机号未注册");
			}
			//校验用户状态
			if(userInfo.getStatus().intValue() == Constants.USER_STATUS_INVALID){
				throw new IllegalArgumentException("您的账户状态异常，请联系管理员");
			}
			
			this.userService.findbackPassword(userInfo.getUserId(), phone, MD5Util.MD5(newPassword));
			
			this.sysLogService.saveSysLog(request, "找回密码", Constants.TERMINAL_PC, UserInfoModel.TABLE_NAME, userInfo.getUserId(), 2);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			logger.error("未知异常，找回密码失败", e);
			
			success = false;
			message = "未知异常，找回密码失败";
			this.sysLogService.saveSysLog(request, "找回密码失败：" + e.getMessage(), Constants.TERMINAL_PC, UserInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 获取用户基本信息
	 * @param request
	 * @param userId
	 * @return 姓名、手机号、在剧组中的状态
	 */
	@RequestMapping("/queryCrewUserBaseInfo")
	@ResponseBody
	public Map<String, Object> queryCrewUserBaseInfo(HttpServletRequest request, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		String crewId = this.getCrewId(request);
		try {
			Map<String, Object> userInfoMap = new HashMap<String, Object>();
			
			//用户名、电话、在剧组中的状态
			UserInfoModel userInfo = this.userService.queryById(userId);
			String userName = userInfo.getRealName();
			String phone = userInfo.getPhone();
			
			CrewUserMapModel crewUserMap = this.userService.queryCrewUserBycrewId(userId, crewId);
			int status = crewUserMap.getStatus();
			
			userInfoMap.put("userName", userName);
			userInfoMap.put("phone", phone);
			userInfoMap.put("status", status);
			
			resultMap.put("userInfo", userInfoMap);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message,ie);
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
	 * 获取用户职务信息
	 * @param request
	 * @param userId
	 * @return
	 */
	@RequestMapping("/queryCrewUserRoleInfo")
	@ResponseBody
	public Map<String, Object> queryCrewUserRoleInfo(HttpServletRequest request, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		String crewId = this.getCrewId(request);
		
		try {
			//用户职务信息
			List<Map<String, Object>> userRoleList = this.sysRoleInfoService.queryByCrewUserId(crewId, userId);
			resultMap.put("userRoleList", userRoleList);
			
			//系统中所有的职务
			List<SysroleInfoModel> sysRoleList = this.sysRoleInfoService.queryByCrewId(crewId);
			List<Map<String, Object>> userSysRoleList = this.formatSysRoleList(sysRoleList, userRoleList);
			resultMap.put("userSysRoleList", userSysRoleList);
			
			
			//演员用户扮演的角色
			List<ViewRoleModel> viewRoleList = this.viewRoleService.queryUserRoleInfo(crewId, userId);
			//剧组中所有的角色
			List<Map<String, Object>> majorRoleList = this.viewRoleService.queryRoleMapByCrewIdAndRoleType(crewId, ViewRoleType.MajorActor.getValue());
			List<Map<String, Object>> guestRoleList = this.viewRoleService.queryRoleMapByCrewIdAndRoleType(crewId, ViewRoleType.GuestActor.getValue());
			
			for (Map<String, Object> majorRole : majorRoleList) {
				String viewRoleId = (String) majorRole.get("viewRoleId");
				
				boolean hasRelated = false;
				for (ViewRoleModel viewRoleModel : viewRoleList) {
					String myViewRoleId = viewRoleModel.getViewRoleId();
					if (viewRoleId.equals(myViewRoleId)) {
						hasRelated = true;
						break;
					}
				}
				
				majorRole.put("hasRelated", hasRelated);
			}
			for (Map<String, Object> majorRole : guestRoleList) {
				String viewRoleId = (String) majorRole.get("viewRoleId");
				
				boolean hasRelated = false;
				for (ViewRoleModel viewRoleModel : viewRoleList) {
					String myViewRoleId = viewRoleModel.getViewRoleId();
					if (viewRoleId.equals(myViewRoleId)) {
						hasRelated = true;
						break;
					}
				}
				
				majorRole.put("hasRelated", hasRelated);
			}
			
			resultMap.put("majorRoleList", majorRoleList);
			resultMap.put("guestRoleList", guestRoleList);
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message,ie);
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
	 * 获取用户权限信息
	 * @param request
	 * @param userId
	 * @return
	 */
	@RequestMapping("/queryCrewUserAuthInfo")
	@ResponseBody
	public Map<String, Object> queryCrewUserAuthInfo(HttpServletRequest request, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		String crewId = this.getCrewId(request);
		
		try {
			//用户权限信息
			List<UserAuthDto> appAuthList = new ArrayList<UserAuthDto>();	//app端权限
			List<UserAuthDto> pcAuthList = new ArrayList<UserAuthDto>();	//pc端权限
			
			//系统中所有的权限信息
//			List<AuthorityModel> authList = this.authorityService.queryAuthByPlatformWithoutAdmin(null);
			//剧组拥有的权限信息
			List<AuthorityModel> authList = this.authorityService.queryCrewAuthByCrewId(crewId);
			//用户已经有的权限信息
			List<UserAuthMapModel> ownAuthList = this.userAuthMapService.queryByCrewUserId(crewId, userId);
			
			List<UserAuthDto> userAuthList = this.loopAuthList(authList, new ArrayList<UserAuthDto>(), ownAuthList);
			
			for (UserAuthDto userAuth : userAuthList) {
				int authPlatform = userAuth.getAuthPlantform();
				if (authPlatform == AuthorityPlatform.Mobile.getValue()) {
					appAuthList.add(userAuth);
				}
				if (authPlatform == AuthorityPlatform.PC.getValue()) {
					pcAuthList.add(userAuth);
				}
				if (authPlatform == AuthorityPlatform.Common.getValue()) {
					appAuthList.add(userAuth);
					pcAuthList.add(userAuth);
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
			
			logger.error("未知异常", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 格式化系统职务信息
	 * 该方法中还会对比出用户拥有的哪些权限，并存储在hasRole字段中
	 * @param sysRoleList	系统中所有的权限
	 * @param userRoleList	用户拥有的权限
	 * @return
	 */
	private List<Map<String, Object>> formatSysRoleList (List<SysroleInfoModel> sysRoleList, List<Map<String, Object>> userRoleList) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		
		//小组信息
		for (SysroleInfoModel sysRoleInfo : sysRoleList) {
			String parentId = sysRoleInfo.getParentId();
			String roleId = sysRoleInfo.getRoleId();
			
			Map<String, Object> singleRoleMap = new HashMap<String, Object>();
			singleRoleMap.put("roleId", sysRoleInfo.getRoleId());
			singleRoleMap.put("roleName", sysRoleInfo.getRoleName());
			singleRoleMap.put("roleDesc", sysRoleInfo.getRoleDesc());
			
			if (parentId.equals("00") || roleId.equals(Constants.ROLE_ID_ADMIN) || roleId.equals(Constants.ROLE_ID_PROJECT_DIRECTOR)) {
				List<Map<String, Object>> child = new ArrayList<Map<String, Object>>();
				singleRoleMap.put("child", child);

				resultList.add(singleRoleMap);
			}
		}
		
		//职务信息
		for (Map<String, Object> map : resultList) {
			String pRoleId = (String) map.get("roleId");
			
			for (SysroleInfoModel sysRoleInfo : sysRoleList) {
				String myparentId = sysRoleInfo.getParentId();
				String myRoleId = sysRoleInfo.getRoleId();
				
				//遍历用户拥有的权限，判断用户是否有这个权限
				boolean hasRole = false;
				for (Map<String, Object> userRole : userRoleList) {
					String uRoleId = (String) userRole.get("roleId");
					if (myRoleId.equals(uRoleId)) {
						hasRole = true;
						break;
					}
				}
				
				Map<String, Object> singleRoleMap = new HashMap<String, Object>();
				singleRoleMap.put("roleId", sysRoleInfo.getRoleId());
				singleRoleMap.put("roleName", sysRoleInfo.getRoleName());
				singleRoleMap.put("roleDesc", sysRoleInfo.getRoleDesc());
				singleRoleMap.put("hasRole", hasRole);
				
				if (pRoleId.equals(myparentId) || (pRoleId.equals(Constants.ROLE_ID_ADMIN) && myRoleId.equals(Constants.ROLE_ID_ADMIN)) 
						|| (pRoleId.equals(Constants.ROLE_ID_PROJECT_DIRECTOR) && myRoleId.equals(Constants.ROLE_ID_PROJECT_DIRECTOR))) {
					List<Map<String, Object>> child = (List<Map<String, Object>>) map.get("child");
					child.add(singleRoleMap);
				}
			}
		}
		return resultList;
	}
	
	
	/**
	 * 遍历权限元素
	 * 按照父子管理整理出格式
	 * 
	 * 此处遍历的原则是从最底层权限一层一层向上剥离
	 * @param authList	系统中所有权限信息
	 * @param userAuthList	封装后的用户权限信息
	 * @param ownAuthList	用户拥有的权限信息
	 * @return
	 */
	private List<UserAuthDto> loopAuthList (List<AuthorityModel> authList, List<UserAuthDto> userAuthList, List<UserAuthMapModel> ownAuthList) {
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
		
		List<UserAuthDto> myUserAuthDtoList = new ArrayList<UserAuthDto>();
		
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
			
			List<UserAuthDto> subUserAuthDto = new ArrayList<UserAuthDto>();
			for (UserAuthDto userAuth : userAuthList) {
				String uparentId = userAuth.getParentId();
				
				if (uparentId.equals(authId)) {
					subUserAuthDto.add(userAuth);
				}
			}
			
			UserAuthDto userAuthDto = new UserAuthDto();
			userAuthDto.setAuthId(authId);
			userAuthDto.setParentId(lauth.getParentId());
			userAuthDto.setAuthName(lauth.getAuthName());
			userAuthDto.setSequence(lauth.getSequence());
			userAuthDto.setSubAuthList(subUserAuthDto);
			userAuthDto.setDifferInRAndW(lauth.getDifferInRAndW());
			userAuthDto.setAuthPlantform(lauth.getAuthPlantform());
			
			boolean hasAuth = false;
			for (UserAuthMapModel userAuthMap : ownAuthList) {
				if (authId.equals(userAuthMap.getAuthId())) {
					userAuthDto.setHasAuth(true);
					userAuthDto.setReadonly(userAuthMap.getReadonly());
					
					hasAuth = true;
					break;
				}
			}
			
			if (!hasAuth) {
				userAuthDto.setHasAuth(false);
				userAuthDto.setReadonly(true);
			}
			
			myUserAuthDtoList.add(userAuthDto);
		}
		
		for (UserAuthDto userAuth : userAuthList) {
			boolean exists = false;
			for (AuthorityModel lauth : lastAuthList) {
				if (userAuth.getParentId().equals(lauth.getAuthId())) {
					exists = true;
					break;
				}
			}
			
			if (!exists) {
				myUserAuthDtoList.add(userAuth);
			}
		}
		
		Collections.sort(myUserAuthDtoList, new Comparator<UserAuthDto>() {
			@Override
			public int compare(UserAuthDto o1, UserAuthDto o2) {
				return o1.getSequence() - o2.getSequence();
			}
		});
		
		//如果全是叶子权限了，说明已经遍历到最顶层了
		if (parentAuthList.size() > 0) {
			//把最底层的权限剥掉后，继续遍历，一直到只剩下最顶层的为止
			authList.removeAll(lastAuthList);
			myUserAuthDtoList = this.loopAuthList(authList, myUserAuthDtoList, ownAuthList);
		}
		
		return myUserAuthDtoList;
	}
	
	/**
	 * 修改用户在剧组中的状态
	 * @param operateType	操作类型：1--冻结   2--解冻
	 * @param aimUserId	要修改的用户ID
	 * @return
	 */
	@RequestMapping("/modifyUserStatus")
	@ResponseBody
	public Map<String, Object> modifyUserStatus (HttpServletRequest request, int operateType, String aimUserId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		String crewId = this.getCrewId(request);
		try {
			
			if (StringUtils.isBlank(aimUserId)) {
				throw new IllegalArgumentException("请选择需要操作的用户");
			}
			
			int userCrewStatus = 1;
			if (operateType == 2) {
				userCrewStatus = CrewUserStatus.Normal.getValue();
			} else {
				userCrewStatus = CrewUserStatus.Frozen.getValue();
			}
			
			CrewUserMapModel crewUserMap = this.userService.queryCrewUserBycrewId(aimUserId, crewId);
			crewUserMap.setStatus(userCrewStatus);
			this.userService.updateCrewUserMap(crewUserMap);
			
			this.sysLogService.saveSysLog(request, "修改用户在剧组中的状态", Constants.TERMINAL_PC, CrewUserMapModel.TABLE_NAME, aimUserId, 2);
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message,ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "修改用户在剧组中的状态失败：" + e.getMessage(), Constants.TERMINAL_PC, CrewUserMapModel.TABLE_NAME, aimUserId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	
	/**
	 * 保存用户职务信息
	 * @param userId	用户ID
	 * @param crewId	剧组ID
	 * @param operateType	操作类型：1新增    2删除
	 * @param roleId	职务ID
	 * @return
	 */
	@RequestMapping("/saveUserRoleInfo")
	@ResponseBody
	public Map<String, Object> saveUserRoleInfo (HttpServletRequest request, String aimUserId, int operateType, String roleId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		String crewId = this.getCrewId(request);
		try {
			if (StringUtils.isBlank(roleId)) {
				throw new IllegalArgumentException("请选择需要操作的角色信息");
			}
			
			UserRoleMapModel userRoleMap = this.userRoleMapService.queryByUserRoleId(crewId, aimUserId, roleId);
			
			//新增
			if (operateType == 1 && userRoleMap == null) {
				//限定剧组管理员不能超过3个
				if (roleId.equals(Constants.ROLE_ID_ADMIN)) {
					//查询剧组下剧组管理员的数量
					List<UserInfoModel> managerList = this.userService.queryCrewUserByGroupId(crewId, roleId);
					if (managerList.size() >= 3) {
						throw new IllegalArgumentException("剧组管理员不能超过三个");
					}
				}
				this.userRoleMapService.addOne(aimUserId, crewId, roleId);
				
				this.sysLogService.saveSysLog(request, "新增用户职务关联关系", Constants.TERMINAL_PC, UserRoleMapModel.TABLE_NAME, aimUserId + "," + roleId, 1);
			}
			
			//删除
			if (operateType == 2 && userRoleMap != null) {
				//用户至少应该拥有一个职务
				List<Map<String, Object>> ownRoleList = this.sysRoleInfoService.queryByCrewUserId(crewId, aimUserId);
				if (ownRoleList.size() == 1) {
					throw new IllegalArgumentException("用户至少应该拥有一个职务");
				}
				this.userRoleMapService.deleteById(crewId, aimUserId, userRoleMap.getMapId(), roleId);
				
				this.sysLogService.saveSysLog(request, "删除用户职务关联关系", Constants.TERMINAL_PC, UserRoleMapModel.TABLE_NAME, aimUserId + "," + roleId, 3);
			}
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message,ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "保存用户职务关联关系失败：" + e.getMessage(), Constants.TERMINAL_PC, UserRoleMapModel.TABLE_NAME, aimUserId + "," + roleId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	
	/**
	 * 保存用户权限信息
	 * @param userId	用户ID
	 * @param crewId	剧组ID
	 * @param operateType 操作类型 1：新增  2：修改  3：删除
	 * @param authId 权限ID
	 * @param readonly 是否只读
	 * @return
	 */
	@RequestMapping("/saveUserAuthInfo")
	@ResponseBody
	public Map<String, Object> saveUserAuthInfo(HttpServletRequest request, String aimUserId, int operateType, String authId, Boolean readonly) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		String crewId = this.getCrewId(request);
		try {
			
			if (StringUtils.isBlank(authId)) {
				throw new IllegalArgumentException("请选择需要操作的权限");
			}
			
			if (readonly == null) {
				readonly = false;
			}
			
			UserAuthMapModel userAuthMap = this.userAuthMapService.queryByCrewUserAuthId(crewId, aimUserId, authId);
			
			//删除
			if (operateType == 3) {
				//需要查询出所有的子权限，然后把用户和所有子权限的关联关系删掉
				List<UserAuthMapModel> userAuthMapList = this.userAuthMapService.queryByCrewUserAuthIdWithSubAuth(crewId, aimUserId, authId);
				for (UserAuthMapModel map : userAuthMapList) {
					this.userAuthMapService.deleteById(crewId, aimUserId, authId, map.getMapId());
				}
				
				this.sysLogService.saveSysLog(request, "删除用户权限关联关系", Constants.TERMINAL_PC, UserAuthMapModel.TABLE_NAME, aimUserId + "," + authId, 3);
			}
			
			//新增
			if ((operateType != 3 && userAuthMap == null) || operateType == 1) {
				userAuthMap = new UserAuthMapModel();
				userAuthMap.setAuthId(authId);
				userAuthMap.setCrewId(crewId);
				userAuthMap.setMapId(UUIDUtils.getId());
				userAuthMap.setReadonly(readonly);
				userAuthMap.setUserId(aimUserId);
				this.userAuthMapService.addOne(crewId, aimUserId, userAuthMap);
				
				this.sysLogService.saveSysLog(request, "新增用户权限关联关系", Constants.TERMINAL_PC, UserAuthMapModel.TABLE_NAME, aimUserId + "," + authId, 1);
			}
			if(operateType == 4) {
				//批量设置只读属性
				this.userAuthMapService.multiSetReadOnly(crewId, aimUserId, authId, readonly);
				this.sysLogService.saveSysLog(request, "修改用户权限只读属性", Constants.TERMINAL_PC, UserAuthMapModel.TABLE_NAME, aimUserId + "," + authId, 2);			
			} else {
				//修改
				if (operateType != 3 && userAuthMap != null) {
					userAuthMap.setAuthId(authId);
					userAuthMap.setCrewId(crewId);
					userAuthMap.setReadonly(readonly);
					userAuthMap.setUserId(aimUserId);
					this.userAuthMapService.updateOne(userAuthMap);
					
					this.sysLogService.saveSysLog(request, "修改用户权限关联关系", Constants.TERMINAL_PC, UserAuthMapModel.TABLE_NAME, aimUserId + "," + authId, 2);
				}
			}			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message,ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "保存用户权限关联关系失败：" + e.getMessage(), Constants.TERMINAL_PC, UserAuthMapModel.TABLE_NAME, aimUserId + "," + authId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 保存演员用户和剧组中场景角色关联信息接口
	 * @param userId
	 * @param crewId
	 * @param aimUserId
	 * @param operateType	操作类型，1:新增    2：删除
	 * @param viewRoleId
	 * @return
	 */
	@RequestMapping("/saveActorUserCrewRoleRelation")
	@ResponseBody
	public Map<String, Object> saveActorUserCrewRoleRelation(HttpServletRequest request, String aimUserId, int operateType, String viewRoleId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		String crewId = this.getCrewId(request);
		try {
			if (StringUtils.isBlank(aimUserId)) {
				throw new IllegalArgumentException("请选择需要操作的用户");
			}
			
			CrewRoleUserMapModel crewRoleUserMap = this.crewRoleUserMapService.queryByCrewUserRoleId(crewId, aimUserId, viewRoleId);
			
			//新增
			if (operateType == 1 && crewRoleUserMap == null ) {
				crewRoleUserMap = new CrewRoleUserMapModel();
				
				crewRoleUserMap.setMapId(UUIDUtils.getId());
				crewRoleUserMap.setUserId(aimUserId);
				crewRoleUserMap.setViewRoleId(viewRoleId);
				crewRoleUserMap.setCrewId(crewId);
				
				this.crewRoleUserMapService.addOne(crewRoleUserMap);
				
				this.sysLogService.saveSysLog(request, "新增演员用户和剧组中场景角色关联", Constants.TERMINAL_PC, CrewRoleUserMapModel.TABLE_NAME, aimUserId + "," + viewRoleId, 1);
			}
			
			//删除
			if (operateType == 2 && crewRoleUserMap != null) {
				this.crewRoleUserMapService.deleteOne(crewRoleUserMap.getMapId());
				
				this.sysLogService.saveSysLog(request, "删除演员用户和剧组中场景角色关联", Constants.TERMINAL_PC, CrewRoleUserMapModel.TABLE_NAME, aimUserId + "," + viewRoleId, 3);
			}
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message,ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "保存演员用户和剧组中场景角色关联失败：" + e.getMessage(), Constants.TERMINAL_PC, CrewRoleUserMapModel.TABLE_NAME, aimUserId + "," + viewRoleId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 审核入组申请接口
	 * @param userId	用户ID
	 * @param crewId	剧组ID
	 * @param agree	是否同意
	 * @param aimUserId	审核的目标用户ID
	 * @return
	 */
	@RequestMapping("/auditEnterApply")
	@ResponseBody
	public Map<String, Object> auditEnterApply(HttpServletRequest request, Boolean agree, String aimUserId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		String crewId = this.getCrewId(request);
		
		String userId = this.getLoginUserId(request);
		
		try {
			if (StringUtils.isBlank(aimUserId)) {
				throw new IllegalArgumentException("请选择需要操作的用户");
			}
			
			if (agree == null) {
				agree = false;
			}
			
			this.joinCrewApplyMsgService.auditEnterApply(userId, crewId, agree, aimUserId);
			
			this.sysLogService.saveSysLog(request, "审核入组申请", Constants.TERMINAL_PC, JoinCrewApplyMsgModel.TABLE_NAME, aimUserId, 2);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "审核入组申请失败：" + e.getMessage(), Constants.TERMINAL_PC, JoinCrewApplyMsgModel.TABLE_NAME, aimUserId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 根据手机号查询用户,admin和客服支持手机号、姓名模糊查询，其他用户只能用手机号完全查询
	 * @param request
	 * @param phone
	 * @return
	 */
	@RequestMapping("/queryUserByPhone")
	@ResponseBody
	public Map<String, Object> queryUserByPhone(HttpServletRequest request, String phone) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		String crewId = this.getCrewId(request);
		try {
			if (StringUtils.isBlank(phone)) {
				throw new IllegalArgumentException("请提供手机号");
			}
			//登录用户类型
			int loginUserType = this.getSessionUserType(request);
			List<UserInfoModel> userList = this.userService.queryNotOwnUserByPhone(crewId, phone, loginUserType);
			
			resultMap.put("userList", userList);
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message,ie);
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
	 * 把用户添加到剧组
	 * @param request
	 * @param aimUserId
	 * @return
	 */
	@RequestMapping("/addUserToCrew")
	@ResponseBody
	public Map<String, Object> addUserToCrew(HttpServletRequest request, String aimUserId, String roleIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		String crewId = this.getCrewId(request);
		String userId = this.getLoginUserId(request);
		
		try {
			if (StringUtils.isBlank(aimUserId)) {
				throw new IllegalArgumentException("请选择一个用户");
			}
			if (StringUtils.isBlank(roleIds)) {
				throw new IllegalArgumentException("请选择用户将要担任的职务");
			}
			
			CrewUserMapModel crewUserMap = this.userService.queryCrewUserBycrewId(aimUserId, crewId);
			if (crewUserMap != null) {
				throw new IllegalArgumentException("用户已经加入到剧组中");
			}
			
			/*String[] roleIdArr = roleIds.split(",");
			for(String roleId : roleIdArr) {
				//限定剧组管理员不能超过3个
				if (roleId.equals(Constants.ROLE_ID_ADMIN)) {
					//查询剧组下剧组管理员的数量
					List<UserInfoModel> managerList = this.userService.queryCrewUserByGroupId(crewId, roleId);
					if (managerList.size() >= 3) {
						throw new IllegalArgumentException("剧组管理员不能超过三个");
					}
				}
			}*/
			
			this.userService.addUserToCrew(crewId, userId, aimUserId, roleIds);
			
			this.sysLogService.saveSysLog(request, "把用户添加到剧组", Constants.TERMINAL_PC, CrewUserMapModel.TABLE_NAME, aimUserId, 1);
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message,ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "把用户添加到剧组失败：" + e.getMessage(), Constants.TERMINAL_PC, CrewUserMapModel.TABLE_NAME, aimUserId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 临时
	 * 天机算发送注册通知短信接口
	 * 
	 * 格式：
	 * 【小土科技】{1}您好，恭喜您成功注册{2}系统，请用谷歌浏览器访问{3}，帐号：{4}，密码：{5}，使用过程中如有疑问，请加QQ群：{6}
	 * 
	 * @param name	用户名
	 * @param phone	用户手机号
	 * @param password	用户密码
	 * @return
	 */
	@RequestMapping("/sendNoticeMsg")
	@ResponseBody
	public Object sendNoticeMsg(String name, String phone, String password) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		
		try {
			String sysName = "剧易拍制片管理软件";
			String sysUrl = "http://p.moonpool.com.cn:8080";
			String contact = "532541208";
			
			String[] args = new String[] {name, sysName, sysUrl, phone, phone, contact};
			MsgUtils.sendMsg(phone, "88944", args);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			success = false;
			logger.error("未知异常，获取通告单列表失败", e);
			throw new IllegalArgumentException("未知异常，获取通告单列表失败");
		}
		
		resultMap.put("success", success);
		return resultMap;
	
	
	}
	
	/**
	 * 获取当前用户可建剧组的次数
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryCurrUserUbCreateCrewNum")
	@ResponseBody
	public Map<String, Object> queryCurrUserUbCreateCrewNum (HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		UserInfoModel sessionUserInfo = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);
		
		boolean success = true;
		String message = "";
		try {
			String userId = sessionUserInfo.getUserId();
			UserInfoModel userInfo = this.userService.queryById(userId);
			
			int ubCreateCrewNum = userInfo.getUbCreateCrewNum();
			
			resultMap.put("ubCreateCrewNum", ubCreateCrewNum);
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
	 * 修改手机号
	 * @param userId
	 * @param phone
	 * @param password	md5密文
	 * @param verifyCode
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updatePhone")
	public Map<String, Object> updatePhone(HttpServletRequest request, String phone, String password, String verifyCode) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		try {
			String userId = this.getLoginUserId(request);
			
			if (StringUtils.isBlank(password)) {
				throw new IllegalArgumentException("密码不能为空");
			}
			//校验手机号是否合法
			if (StringUtils.isBlank(phone)) {
				throw new IllegalArgumentException("请填写手机号");
			}
			if (!RegexUtils.regexFind(Constants.REGEX_PHONE_NUMBER, phone)) {
				throw new IllegalArgumentException("手机号不合法");
			}
			
			//校验验证码
			if (StringUtils.isBlank(verifyCode)) {
				throw new IllegalArgumentException("请填写短信验证码");
			}
			VerifyCodeInfoModel validInfoModel = this.verifyCodeInfoService.queryByPhoneAndCode(phone, verifyCode, VerifyCodeType.ModifyPhone.getValue());
			if (validInfoModel == null) {
				throw new IllegalArgumentException("验证码错误");
			}
			
			//校验用户信息
			UserInfoModel userInfo = this.userService.queryById(userId);
			if (!userInfo.getPassword().equals(MD5Util.MD5(password))) {
				throw new IllegalArgumentException("密码错误");
			}
			
			//校验手机号是否已存在
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("phone", phone);
			
			List<UserInfoModel> phoneUserList = this.userService.queryManyByMutiCondition(conditionMap, null);
			//判断当前用户是否是修改或新建
			if (StringUtils.isNotBlank(userId)) {
				//校验是否是原用户
				for (UserInfoModel model : phoneUserList) {
					//取出用户id
					if (!model.getUserId().equals(userId)) {
						throw new IllegalArgumentException("手机号已被其他用户使用!");
					}
				}
			}else {
				if (phoneUserList != null && phoneUserList.size() > 0) {
					throw new IllegalArgumentException("手机号已被其他用户使用");
				}
			}
			
			this.userService.updateUserPhone(userId, phone);
			
			this.sysLogService.saveSysLog(request, "修改手机号", Constants.TERMINAL_PC, UserInfoModel.TABLE_NAME, userId, 2);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			logger.error("未知异常，修改手机号失败", e);
			success = false;
			message = "未知异常，修改手机号失败";
			this.sysLogService.saveSysLog(request, "修改手机号失败：" + e.getMessage(), Constants.TERMINAL_PC, UserInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 修改密码
	 * @param userId
	 * @param password
	 * @param newPassword
	 */
	@ResponseBody
	@RequestMapping("/updatePassword")
	public Map<String, Object> updatePassword(HttpServletRequest request, String password, String newPassword) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		try {
			String userId = this.getLoginUserId(request);
			
			if (StringUtils.isBlank(password)) {
				throw new IllegalArgumentException("密码不能为空");
			}
			if (StringUtils.isBlank(newPassword)) {
				throw new IllegalArgumentException("新密码不能为空");
			}
			if (newPassword.length() < 6 || newPassword.length() > 18) {
				throw new IllegalArgumentException("请输入6-18位密码");
			}
			
			//校验用户信息
			UserInfoModel userInfo = this.userService.queryById(userId);
			if (!userInfo.getPassword().equals(MD5Util.MD5(password))) {
				throw new IllegalArgumentException("密码错误");
			}
			
			userInfo.setPassword(MD5Util.MD5(newPassword));
			this.userService.updateOne(userInfo);
			
			this.sysLogService.saveSysLog(request, "修改密码", Constants.TERMINAL_PC, UserInfoModel.TABLE_NAME, userId, 2);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			logger.error("未知异常，修改密码失败", e);
			success = false;
			message = "未知异常，修改密码失败";
			this.sysLogService.saveSysLog(request, "修改密码失败：" + e.getMessage(), Constants.TERMINAL_PC, UserInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 修改用户基本信息
	 * @param userId
	 * @param realName	yongh
	 * @param sex
	 * @param age
	 * @param email
	 * @param profile
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateUserBaseInfo")
	public Map<String, Object> updateUserBaseInfo(HttpServletRequest request, String realName, Integer sex, Integer age, String email, String profile) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		try {
			String userId = this.getLoginUserId(request);
			String crewId = this.getCrewId(request);
			
			if (StringUtils.isBlank(realName)) {
				throw new IllegalArgumentException("姓名不能为空");
			}
			if (sex == null) {
				throw new IllegalArgumentException("性别不能为空");
			}
			if (age != null && age > 200) {
				throw new IllegalArgumentException("请正确填写年龄");
			}
			if (!StringUtils.isBlank(realName) && realName.length() > 20) {
				throw new IllegalArgumentException("请正确填写姓名");
			}
			if (!StringUtils.isBlank(email) && email.length() > 50) {
				throw new IllegalArgumentException("邮箱不合法");
			}
			//校验姓名在组中是否重复
			if (!StringUtils.isBlank(crewId)) {
				List<UserInfoModel> userList = this.userService.queryUserInfoByRealNameExcepOwn(crewId, userId, realName);
				if (userList != null && userList.size() > 0) {
					throw new IllegalArgumentException("剧组中已存在相同姓名的用户");
				}
			}
			
			
			UserInfoModel userInfo = this.userService.queryById(userId);
			userInfo.setRealName(realName);
			userInfo.setSex(sex);
			userInfo.setAge(age);
			userInfo.setEmail(email);
			userInfo.setProfile(profile);
			
			this.userService.updateOne(userInfo);
			
			this.sysLogService.saveSysLog(request, "修改用户信息", Constants.TERMINAL_PC, UserInfoModel.TABLE_NAME, userId, 2);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			logger.error("未知异常，修改用户信息失败", e);
			success = false;
			message = "未知异常，修改用户信息失败";
			this.sysLogService.saveSysLog(request, "修改用户信息失败：" + e.getMessage(), Constants.TERMINAL_PC, UserInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 获取剧组成员信息
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryCrewUserList")
	@ResponseBody
	public Map<String, Object> queryCrewUserList (HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		String crewId = getCrewId(request);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			//审核中的人员申请进组信息
			List<Map<String, Object>> auditingJoinMsgList = this.joinCrewApplyMsgService.queryCrewAuditingMsg(crewId, JoinCrewAuditStatus.Auditing.getValue());
			
			List<Map<String, Object>> auditingUserList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> joinMsg : auditingJoinMsgList) {
				Map<String, Object> auditingUserInfo = new HashMap<String, Object>();
				auditingUserInfo.put("userId", joinMsg.get("userId"));
				auditingUserInfo.put("userName", joinMsg.get("userName"));
				auditingUserInfo.put("phone", joinMsg.get("phone"));
				auditingUserInfo.put("roleNames", joinMsg.get("aimRoleNames"));
				auditingUserInfo.put("remark", joinMsg.get("remark"));
				
				Date createTime = (Date) joinMsg.get("createTime");
				auditingUserInfo.put("createTime", sdf.format(createTime));
				
				auditingUserList.add(auditingUserInfo);
			}
			resultMap.put("toAuditUserList", auditingUserList);
			
			
			//剧组已有人员部门信息
			List<Map<String, Object>> userList = this.userService.queryCrewUserListWithRole(crewId);
			
			//职务列表
			List<Map<String, Object>> roleList = new ArrayList<Map<String, Object>>();
			List<String> roleNameList = new ArrayList<String>();
			
			//按照职务分组
			for (Map<String, Object> userMap : userList) {
				String roleId = (String) userMap.get("roleId");
				String roleName = (String) userMap.get("roleName");
				Date createTime = (Date) userMap.get("joinTime");
				userMap.put("createTime", sdf.format(createTime));
				
				if (!roleNameList.contains(roleName)) {
					roleNameList.add(roleName);
					
					Map<String, Object> roleMapMap = new HashMap<String, Object>();
					roleMapMap.put("roleName", roleName);
					
					List<Map<String, Object>> roleUserList = new ArrayList<Map<String, Object>>();
					roleUserList.add(userMap);
					
					roleMapMap.put("roleUserList", roleUserList);
					
					roleList.add(roleMapMap);
					
				} else {
					Map<String, Object> roleMap = roleList.get(roleNameList.indexOf(roleName));
					List<Map<String, Object>> roleUserList = (List<Map<String, Object>>) roleMap.get("roleUserList");
					
					roleUserList.add(userMap);
				}
			}
			
			//小组列表
			List<Map<String, Object>> groupList = new ArrayList<Map<String, Object>>();
			
			List<String> groupNameList = new ArrayList<String>();
			for (Map<String, Object> roleMap : roleList) {
				List<Map<String, Object>> roleUserList = (List<Map<String, Object>>) roleMap.get("roleUserList");
				Map<String, Object> userMap = roleUserList.get(0);
				
				String proleName = (String) userMap.get("proleName");
				
				if (!groupNameList.contains(proleName)) {
					groupNameList.add(proleName);
					
					Map<String, Object> groupMap = new HashMap<String, Object>();
					groupMap.put("groupName", proleName);
					
					List<Map<String, Object>> groupRoleList = new ArrayList<Map<String, Object>>();
					groupRoleList.add(roleMap);
					groupMap.put("groupRoleList", groupRoleList);
					
					groupList.add(groupMap);
				} else {
					Map<String, Object> groupMap = groupList.get(groupNameList.indexOf(proleName));
					List<Map<String, Object>> groupRoleList = (List<Map<String, Object>>) groupMap.get("groupRoleList");
					groupRoleList.add(roleMap);
				}
			}
			
			resultMap.put("crewUserList", groupList);
			
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
	 * 查询审核中的人员申请入组信息列表
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryJoinCrewApply")
	@ResponseBody
	public Map<String, Object> queryJoinCrewApply (HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();		
		boolean success = true;
		String message = "";
		
		try {
			String crewId = getCrewId(request);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			//审核中的人员申请进组信息
			List<Map<String, Object>> auditingJoinMsgList = this.joinCrewApplyMsgService.queryCrewAuditingMsg(crewId, JoinCrewAuditStatus.Auditing.getValue());
			
			List<Map<String, Object>> auditingUserList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> joinMsg : auditingJoinMsgList) {
				Map<String, Object> auditingUserInfo = new HashMap<String, Object>();
				auditingUserInfo.put("userId", joinMsg.get("userId"));
				auditingUserInfo.put("userName", joinMsg.get("userName"));
				auditingUserInfo.put("phone", joinMsg.get("phone"));
				auditingUserInfo.put("roleNames", joinMsg.get("aimRoleNames"));
				auditingUserInfo.put("remark", joinMsg.get("remark"));
				
				Date createTime = (Date) joinMsg.get("createTime");
				auditingUserInfo.put("createTime", sdf.format(createTime));
				
				auditingUserList.add(auditingUserInfo);
			}
			resultMap.put("resultList", auditingUserList);			
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
	 * 获取剧组拥有某权限的成员信息
	 * @param request
	 * @param authId
	 * @return
	 */
	@RequestMapping("/queryAuthUserList")
	@ResponseBody
	public Map<String, Object> queryAuthUserList (HttpServletRequest request, String authId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		String crewId = this.getCrewId(request);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {	
			
			//剧组已有人员部门信息
			List<Map<String, Object>> userList = this.userService.queryUserListWithRoleByAuthId(crewId, authId);
			
			//职务列表
			List<Map<String, Object>> roleList = new ArrayList<Map<String, Object>>();
			List<String> roleNameList = new ArrayList<String>();
			
			//按照职务分组
			for (Map<String, Object> userMap : userList) {
				String roleName = (String) userMap.get("roleName");
				Date createTime = (Date) userMap.get("joinTime");
				userMap.put("createTime", sdf.format(createTime));
				
				if (!roleNameList.contains(roleName)) {
					roleNameList.add(roleName);
					
					Map<String, Object> roleMapMap = new HashMap<String, Object>();
					roleMapMap.put("roleName", roleName);
					
					List<Map<String, Object>> roleUserList = new ArrayList<Map<String, Object>>();
					roleUserList.add(userMap);
					
					roleMapMap.put("roleUserList", roleUserList);
					
					roleList.add(roleMapMap);
					
				} else {
					Map<String, Object> roleMap = roleList.get(roleNameList.indexOf(roleName));
					List<Map<String, Object>> roleUserList = (List<Map<String, Object>>) roleMap.get("roleUserList");
					
					roleUserList.add(userMap);
				}
			}
			
			//小组列表
			List<Map<String, Object>> groupList = new ArrayList<Map<String, Object>>();
			
			List<String> groupNameList = new ArrayList<String>();
			for (Map<String, Object> roleMap : roleList) {
				List<Map<String, Object>> roleUserList = (List<Map<String, Object>>) roleMap.get("roleUserList");
				Map<String, Object> userMap = roleUserList.get(0);
				
				String proleName = (String) userMap.get("proleName");
				
				if (!groupNameList.contains(proleName)) {
					groupNameList.add(proleName);
					
					Map<String, Object> groupMap = new HashMap<String, Object>();
					groupMap.put("groupName", proleName);
					
					List<Map<String, Object>> groupRoleList = new ArrayList<Map<String, Object>>();
					groupRoleList.add(roleMap);
					groupMap.put("groupRoleList", groupRoleList);
					
					groupList.add(groupMap);
				} else {
					Map<String, Object> groupMap = groupList.get(groupNameList.indexOf(proleName));
					List<Map<String, Object>> groupRoleList = (List<Map<String, Object>>) groupMap.get("groupRoleList");
					groupRoleList.add(roleMap);
				}
			}
			
			resultMap.put("crewUserList", groupList);
			
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
	 * 删除用户剧组关联关系
	 * @param userId
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("deleteCrewUserMap")
	public Map<String,Object> deleteCrewUserMap(HttpServletRequest request, String userId, String crewId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			this.userService.deleteCrewUserMap(userId, crewId);
			this.sysLogService.saveSysLog(request, "删除客服用户与剧组关联关系", Constants.TERMINAL_PC, UserInfoModel.TABLE_NAME, userId, 3);
		} catch (Exception e) {
			success = false;
			message = "未知异常，删除用户剧组关联关系失败";
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 根据用户id查询出用户的详细信息和用户的工作经历列表
	 * @param userId 用户id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryWorkExperAndUserInfo")
	public Map<String, Object> queryWorkExperAndUserInfo(HttpServletRequest request, String userId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			if (StringUtils.isBlank(userId)) {
				throw new IllegalArgumentException("请提供用户信息!");
			}
			
			//获取用户的个人信息
			UserInfoModel userInfoModel = this.workService.getUserInfoById(userId);
			resultMap.put("userInfo", userInfoModel);
			
			//获取用户的工作经历列表
			List<Map<String, Object>> list = this.workService.getWorkExListByUserId(userId);
			resultMap.put("workList", list);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询用户的详细信息和用户的工作经历列表失败";
			logger.error(message, e);
		}		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 删除工作经历
	 * @param experienceId
	 * @param userId
	 */
	@ResponseBody
	@RequestMapping("/deleteWorkExper")
	public Map<String, Object> deleteWorkExper(HttpServletRequest request, String experienceId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(experienceId)) {
				throw new IllegalArgumentException("请选择要删除的工作经历!");
			}
			
			this.workService.deleteWorkExper(userId, experienceId);
			
			this.sysLogService.saveSysLog(request, "删除工作经历", Constants.TERMINAL_PC, WorkExperienceInfoModel.TABLE_NAME, experienceId, SysLogOperType.DELETE.getValue());
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，删除工作经历失败";
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "删除工作经历失败："+e.getMessage(), Constants.TERMINAL_PC, WorkExperienceInfoModel.TABLE_NAME, experienceId, SysLogOperType.ERROR.getValue());
		}
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 判断用户是否是财务
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/judgeUserIsFinance")
	public Map<String, Object> judgeUserIsFinance(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			String userId = this.getLoginUserId(request);
			List<Map<String, Object>> myRoleList = this.sysRoleInfoService.queryByCrewUserId(crewId, userId);
			boolean isFinance = false;
			for (Map<String, Object> myRoleInfo : myRoleList) {
				String roleName = (String) myRoleInfo.get("roleName");
				if (roleName.equals("财务")) {
					isFinance = true;
					break;
				}
			}
			resultMap.put("isFinance", isFinance);
		}catch (Exception e) {
			success = false;
			message = "未知异常，判断用户是否是财务失败";
			logger.error(message, e);
		}
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询剧组下所有用户信息
	 * 带有名称首字母信息的返回
	 * @param request
	 * @param isHasPRole 是否包含分组
	 * @param isHasManager 是否包含系统管理员
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryCrewAllUserListWithFletter")
	public Map<String, Object> queryCrewAllUserListWithFletter(HttpServletRequest request, boolean isHasPRole, boolean isHasManager) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			List<Map<String, Object>> userList = this.userService.queryCrewUserListWithRole(crewId);
			
			//把每个人的职务计算出来
			Map<String, String> userIdRoleMap = new HashMap<String, String>();
			for (Map<String, Object> myUserInfo : userList) {
				String myUserId = (String) myUserInfo.get("userId");
				String roleId = (String) myUserInfo.get("roleId");
				String roleName = (String) myUserInfo.get("roleName");
				String proleName = (String) myUserInfo.get("proleName");
				
				//系统管理员
				if(roleId.equals(Constants.ROLE_ID_ADMIN) && !isHasManager) {
					continue;
				}
				String fullRoleName = roleName;
				if(isHasPRole) {
					fullRoleName = proleName + "-" + roleName;
				}
				
				if (userIdRoleMap.containsKey(myUserId)) {
					userIdRoleMap.put(myUserId, userIdRoleMap.get(myUserId) + "," + fullRoleName);
				} else {
					userIdRoleMap.put(myUserId, fullRoleName);
				}
			}
			
			//封装用户数据
			List<Map<String, Object>> crewUserMapList = new ArrayList<Map<String, Object>>();
			List<String> userIdList = new ArrayList<String>();
			for (Map<String, Object> myUserInfo : userList) {
				String myUserId = (String) myUserInfo.get("userId");
				String myUserName = (String) myUserInfo.get("realName");
				String phone = (String) myUserInfo.get("phone");
				String fletter = (String) myUserInfo.get("fletter");
				
				Map<String, Object> userMap = new HashMap<String, Object>();
				if (!userIdList.contains(myUserId) && StringUtil.isNotBlank((String)userIdRoleMap.get(myUserId))) {
					userMap.put("userId", myUserId);
					userMap.put("userName", myUserName);
					userMap.put("roleNames", userIdRoleMap.get(myUserId));
					userMap.put("phone", phone);
					userMap.put("fletter", fletter);
					
					crewUserMapList.add(userMap);
					
					userIdList.add(myUserId);
				}
			}
			
			resultMap.put("userList", crewUserMapList);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询剧组下所有用户信息失败";
			logger.error(message, e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询剧组下所有用户职务分组列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryCrewUserGroupList")
	public Map<String, Object> queryCrewAllUserListWithFletter(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			//审核中的人员申请进组信息
			List<Map<String, Object>> auditingJoinMsgList = this.joinCrewApplyMsgService.queryCrewAuditingMsg(crewId, JoinCrewAuditStatus.Auditing.getValue());
			List<Map<String, Object>> auditingUserList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> joinMsg : auditingJoinMsgList) {
				Map<String, Object> auditingUserInfo = new HashMap<String, Object>();
				auditingUserInfo.put("userId", joinMsg.get("userId"));
				auditingUserInfo.put("userName", joinMsg.get("userName"));
				auditingUserInfo.put("phone", joinMsg.get("phone"));
				auditingUserInfo.put("roleNames", joinMsg.get("aimRoleNames"));
				auditingUserInfo.put("remark", joinMsg.get("remark"));
				Date createTime = (Date) joinMsg.get("createTime");
				auditingUserInfo.put("createTime", sdf.format(createTime));
				
				auditingUserList.add(auditingUserInfo);
			}
			
			//剧组已有人员部门信息
			List<Map<String, Object>> crewGroupList = this.sysRoleInfoService.queryCrewGroupInfo(crewId);
			
			resultMap.put("toAuditUserList", auditingUserList);
			resultMap.put("crewGroupList", crewGroupList);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询剧组下所有用户职务分组列表失败";
			logger.error(message, e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 获取指定剧组的指定小组下人员列表
	 * 只返回状态为有效的用户
	 * 在剧组中被冻结的用户也需要返回
	 * @param request
	 * @param groupId 职务分组ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryGroupUserList")
	public Map<String, Object> queryGroupUserList(HttpServletRequest request, String groupId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(groupId)) {
				throw new IllegalArgumentException("请选择分组");
			}
			String crewId = this.getCrewId(request);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			//小组名
			SysroleInfoModel sysRoleInfo = this.sysRoleInfoService.queryById(groupId);
			String groupName = sysRoleInfo.getRoleName();
			//小组内总人数
//			List<UserInfoModel> userList = this.userService.queryCrewUserByGroupId(crewId, groupId);
//			int userNum = userList.size();
			
			List<Map<String, Object>> roleInfo = new ArrayList<Map<String, Object>>();	//小组内职务信息
			List<Map<String, Object>> groupUserList = this.userService.queryByCrewGroupId(crewId, groupId);	//小组内用户信息
			
			List<String> roleNames = new ArrayList<String>();
			for (Map<String, Object> map : groupUserList) {
				String roleName = (String) map.get("roleName");
				Date createTime = (Date) map.get("createTime");
				map.put("createTime", sdf.format(createTime));
				
				if (!roleNames.contains(roleName)) {
					roleNames.add(roleName);
					
					Map<String, Object> roleMap = new HashMap<String, Object>();
					roleMap.put("roleName", roleName);
					
					List<Map<String, Object>> roleUserList = new ArrayList<Map<String, Object>>();
					roleUserList.add(map);
					roleMap.put("roleUserList", roleUserList);
					
					roleInfo.add(roleMap);
				} else {
					Map<String, Object> roleInfoMap = roleInfo.get(roleNames.indexOf(roleName));
					List<Map<String, Object>> roleUserList = (List<Map<String, Object>>) roleInfoMap.get("roleUserList");
					roleUserList.add(map);
				}
			}
			
			resultMap.put("groupName", groupName);
//			resultMap.put("userNum", userNum);
			resultMap.put("roleInfo", roleInfo);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，获取指定剧组的指定小组下人员列表失败";
			logger.error(message, e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 获取多个用户权限信息
	 * @param request
	 * @param userId
	 * @return
	 */
	@RequestMapping("/queryMultiUserAuthInfo")
	@ResponseBody
	public Map<String, Object> queryMultiUserAuthInfo(HttpServletRequest request, String userIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		String crewId = this.getCrewId(request);
		
		try {
			//用户权限信息
			List<UserAuthDto> appAuthList = new ArrayList<UserAuthDto>();	//app端权限
			List<UserAuthDto> pcAuthList = new ArrayList<UserAuthDto>();	//pc端权限
			
			//剧组拥有的权限信息
			List<AuthorityModel> authList = this.authorityService.queryCrewAuthByCrewId(crewId);
			//用户已经有的权限信息
			List<Map<String, Object>> ownAuthList = this.userAuthMapService.queryByCrewUserIds(crewId, userIds);
			
			List<UserAuthDto> userAuthList = this.loopMultiAuthList(authList, new ArrayList<UserAuthDto>(), ownAuthList);
			
			for (UserAuthDto userAuth : userAuthList) {
				int authPlatform = userAuth.getAuthPlantform();
				if (authPlatform == AuthorityPlatform.Mobile.getValue()) {
					appAuthList.add(userAuth);
				}
				if (authPlatform == AuthorityPlatform.PC.getValue()) {
					pcAuthList.add(userAuth);
				}
				if (authPlatform == AuthorityPlatform.Common.getValue()) {
					appAuthList.add(userAuth);
					pcAuthList.add(userAuth);
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
			
			logger.error("未知异常", e);
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
	 * @param userAuthList	封装后的用户权限信息
	 * @param ownAuthList	用户拥有的权限信息
	 * @return
	 */
	private List<UserAuthDto> loopMultiAuthList (List<AuthorityModel> authList, List<UserAuthDto> userAuthList, List<Map<String, Object>> ownAuthList) {
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
		
		List<UserAuthDto> myUserAuthDtoList = new ArrayList<UserAuthDto>();
		
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
			
			List<UserAuthDto> subUserAuthDto = new ArrayList<UserAuthDto>();
			for (UserAuthDto userAuth : userAuthList) {
				String uparentId = userAuth.getParentId();
				
				if (uparentId.equals(authId)) {
					subUserAuthDto.add(userAuth);
				}
			}
			
			UserAuthDto userAuthDto = new UserAuthDto();
			userAuthDto.setAuthId(authId);
			userAuthDto.setParentId(lauth.getParentId());
			userAuthDto.setAuthName(lauth.getAuthName());
			userAuthDto.setSequence(lauth.getSequence());
			userAuthDto.setSubAuthList(subUserAuthDto);
			userAuthDto.setDifferInRAndW(lauth.getDifferInRAndW());
			userAuthDto.setAuthPlantform(lauth.getAuthPlantform());
			
			boolean hasAuth = false;
			for (Map<String, Object> userAuthMap : ownAuthList) {
				if (authId.equals((String) userAuthMap.get("authId"))) {
					userAuthDto.setHasAuthStatus(Integer.parseInt(userAuthMap.get("hasAuth") + ""));
					userAuthDto.setReadonlyStatus(Integer.parseInt(userAuthMap.get("readonly") + ""));
					
					hasAuth = true;
					break;
				}
			}
			
			if (!hasAuth) {
				userAuthDto.setHasAuthStatus(0);
				userAuthDto.setReadonly(true);
			}
			
			myUserAuthDtoList.add(userAuthDto);
		}
		
		for (UserAuthDto userAuth : userAuthList) {
			boolean exists = false;
			for (AuthorityModel lauth : lastAuthList) {
				if (userAuth.getParentId().equals(lauth.getAuthId())) {
					exists = true;
					break;
				}
			}
			
			if (!exists) {
				myUserAuthDtoList.add(userAuth);
			}
		}
		
		Collections.sort(myUserAuthDtoList, new Comparator<UserAuthDto>() {
			@Override
			public int compare(UserAuthDto o1, UserAuthDto o2) {
				return o1.getSequence() - o2.getSequence();
			}
		});
		
		//如果全是叶子权限了，说明已经遍历到最顶层了
		if (parentAuthList.size() > 0) {
			//把最底层的权限剥掉后，继续遍历，一直到只剩下最顶层的为止
			authList.removeAll(lastAuthList);
			myUserAuthDtoList = this.loopMultiAuthList(authList, myUserAuthDtoList, ownAuthList);
		}
		
		return myUserAuthDtoList;
	}
	
	/**
	 * 批量保存用户权限信息
	 * @param userIds	用户IDs,多个以逗号分隔
	 * @param operateType 操作类型 1：新增  2：修改  3：删除
	 * @param authId 权限ID
	 * @param readonly 是否只读
	 * @return
	 */
	@RequestMapping("/saveMultiUserAuthInfo")
	@ResponseBody
	public Map<String, Object> saveMultiUserAuthInfo(HttpServletRequest request, String userIds, Integer operateType, String authId, Boolean readonly) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		String crewId = this.getCrewId(request);
		try {
			
			if (StringUtils.isBlank(authId)) {
				throw new IllegalArgumentException("请选择需要操作的权限");
			}
			
			if (StringUtils.isBlank(userIds)) {
				throw new IllegalArgumentException("请选择需要操作的用户");
			}
			
			if (readonly == null) {
				readonly = false;
			}
			this.userAuthMapService.multiSaveUserAuthMap(crewId, userIds, authId, readonly, operateType);
			this.sysLogService.saveSysLog(request, "批量修改用户权限", Constants.TERMINAL_PC, UserAuthMapModel.TABLE_NAME, userIds + "," + authId, SysLogOperType.UPDATE.getValue());		
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message,ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常";			
			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "保存用户权限关联关系失败：" + e.getMessage(), Constants.TERMINAL_PC, UserAuthMapModel.TABLE_NAME, userIds + "," + authId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 上传用户头像
	 * @param request
	 * @param file 头像文件
	 * @return
	 */
	@RequestMapping("/uploadUserHeader")
	@ResponseBody
	public Map<String, Object> uploadUserHeader(HttpServletRequest request, MultipartFile file) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";		
		try {
			String userId = this.getLoginUserId(request);
			
			if (file == null) {
				throw new IllegalArgumentException("请选择头像图片");
			}
			
			UserInfoModel userInfo = this.userService.updateUserImg(userId, file);
            
            resultMap.put("imgUrl", FileUtils.genPreviewPath(userInfo.getBigImgUrl()));
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message,ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常";			
			logger.error("未知异常", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
}