package com.xiaotu.makeplays.common.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.xiaotu.makeplays.authority.model.constants.AuthorityPlatform;
import com.xiaotu.makeplays.authority.service.AuthorityService;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.crew.service.CrewUserMapService;
import com.xiaotu.makeplays.sys.service.SysLogService;
import com.xiaotu.makeplays.sysrole.service.SysRoleInfoService;
import com.xiaotu.makeplays.sysrole.service.UserRoleMapService;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.model.constants.UserType;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.MD5Util;

/**
 * 用户登录相关接口 
 * @author xuchangjian 2016-10-10下午7:18:19
 */
@Controller
public class LoginController {
	
	Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthorityService authorityService;
	
	@Autowired
	private SysRoleInfoService sysRoleInfoService;
	
	@Autowired
	private CrewInfoService crewInfoService;
	
	@Autowired
	private SysLogService sysLogService;
	
	@Autowired
	private CrewUserMapService crewUserMapService;
	
	@Autowired
	private UserRoleMapService userRoleMapService;
	
	/**
	 * 跳转到登录页面
	 * @param request
	 * @return
	 */
	@RequestMapping("/toLoginPage")
	public ModelAndView toLoginPage(HttpServletRequest request) {
		ModelAndView view = new ModelAndView("login");
		return view;
	}
	/**
	 * 跳转到注册页面
	 * @return
	 */
	@RequestMapping("/toRegisterPage")
	public ModelAndView toRegisterPage() {
		ModelAndView view = new ModelAndView("register");
		return view;
	}
	
	/**
	 * 跳转到忘记密码页面
	 * @return
	 */
	@RequestMapping("/toForgetPassWordPage")
	public ModelAndView toForgetPassWordPage() {
		ModelAndView view = new ModelAndView("forgetPassword");
		return view;
	}
	
	/**
	 * 用户登录操作
	 * @param request
	 * @param userName
	 * @param password
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/login")
	public Map<String, Object> login(HttpServletRequest request, String userName, String password) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		boolean noJionCrew = false;
		
		try {
			/*
			 * 数据校验
			 */
			if (StringUtils.isBlank(userName)) {
				throw new IllegalArgumentException("请输入用户名");
			}
			if (StringUtils.isBlank(password)) {
				throw new IllegalArgumentException("请输入密码");
			}

			// 校验用户名/手机号
			UserInfoModel nameUserInfo = this.userService.queryUserByLoginName(userName, null);
			if (nameUserInfo == null) {
				throw new IllegalArgumentException("不存在的用户名/手机号");
			}
			// 校验密码
			UserInfoModel userInfo = userService.queryUserByNameAndPsd(userName, MD5Util.MD5(password));
			if (userInfo == null) {
				throw new IllegalArgumentException("密码错误");
			}
			// 校验用户状态
			if (userInfo.getStatus().intValue() == Constants.USER_STATUS_INVALID) {
				throw new IllegalArgumentException("您的账户状态异常，请联系管理员");
			}

			/*
			 * 获取用户信息
			 */
			String userId = userInfo.getUserId();

			List<Map<String, Object>> menuList = new ArrayList<Map<String, Object>>(); // 菜单列表
			CrewInfoModel crewInfo = new CrewInfoModel(); // 用户默认剧组
			List<CrewInfoModel> userCrewList = new ArrayList<CrewInfoModel>(); // 当前用户所有的剧组
			Map<String, Object> authCodeMap = new HashMap<String, Object>();	//当前用户权限信息
			int loginUserType = 1;	//用户类型：1-系统管理员  2-客服  3-普通剧组用户  4--总客服
			String roleId = ""; //客服角色ID

			int userType = userInfo.getType();
			if (userType == UserType.Admin.getValue()) {
				// 系统管理员
				menuList = authorityService.queryUserAuthority(null, null);
				loginUserType = 1;
			} else {
				if(userType == UserType.CustomerService.getValue()) {
					loginUserType = 2; //客服
				}
				if(userType == UserType.CrewUser.getValue()) {
					loginUserType = 3; //普通剧组用户
				}
				
				// 查询用户默认剧组，如果默认剧组为空，则查询该用户是否有其他剧组，并设置为默认剧组
				crewInfo = this.crewInfoService.queryUserDefaultCrew(userId);
				if (crewInfo == null) {
					noJionCrew = true;
				}
				//总客服没有加入的剧组,默认加入所有剧组
				if(userType == UserType.CustomerService.getValue()) {
					roleId = this.userRoleMapService.queryAllUserRoleIds(userId);
					if(roleId.equals(Constants.ROLE_ID_CUSTOM_SERVICE)) {
						noJionCrew = false;
						loginUserType = 4;
					}
				}
				
				//如果用户不是总客服且加入了一个剧组，则获取对应的菜单、剧组列表、权限信息
				if (!noJionCrew && loginUserType != 4) {
					userCrewList = this.crewInfoService.queryUserAllCrew(userId);
					
					List<Map<String, Object>> userAuthList = null;
					if(loginUserType != 2) {//普通剧组成员
						menuList = authorityService.queryUserAuthority(userId, crewInfo.getCrewId());

						// 权限列表
						userAuthList = this.authorityService.queryEffectiveAuthByUserAndPlantform(crewInfo.getCrewId(), 
								userInfo.getUserId(), AuthorityPlatform.PC.getValue());
						
						for (Map<String, Object> auth : userAuthList) {
							String authCode = (String) auth.get("authCode");
							int readonly = (Integer) auth.get("readonly");

							if (!StringUtils.isBlank(authCode)) {
								authCodeMap.put(authCode, readonly);
							}
						}
					} else {//客服
						menuList = authorityService.getServiceMenu(roleId);
						
						authCodeMap = authorityService.getServiceAuth(roleId);
					}
				}
			}

			HttpSession session = request.getSession();
			session.invalidate();
			session = request.getSession();
			session.setAttribute(Constants.SESSION_USER_INFO, userInfo); // 用户信息
			session.setAttribute("menuTree", new Gson().toJson(menuList)); // 菜单
			session.setAttribute(Constants.SESSION_CREW_INFO, crewInfo); // 当前剧组信息
			session.setAttribute(Constants.SESSION_CREWINFO_ALL, userCrewList); // 用户所有的剧组
			session.setAttribute(Constants.SESSION_USER_AUTH_MAP, authCodeMap); // 用户权限信息
			session.setAttribute(Constants.SESSION_LOGIN_USER_TYPE, loginUserType);	//用户类型：1-系统管理员  2-客服  3-普通剧组用户  4--总客服
			session.setAttribute(Constants.SESSION_LOGIN_SERVICE_TYPE, roleId);	//客服角色，即具体客服类型

			// 记录用户登录日志
			userService.insertLoginInfo(request, userInfo.getUserId());

			success = true;
			message = "登录成功";
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			logger.error("未知异常，登录失败", e);

			success = false;
			message = "未知异常，登录失败";
		}

		resultMap.put("noJionCrew", noJionCrew);
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	
	/**
	 * 登出
	 * @param request
	 * @return
	 */
	@RequestMapping("logout")
	public ModelAndView logout(HttpServletRequest request){
		ModelAndView view = new ModelAndView("redirect:/toLoginPage");
		request.getSession().invalidate();
		return view;
	}
}
