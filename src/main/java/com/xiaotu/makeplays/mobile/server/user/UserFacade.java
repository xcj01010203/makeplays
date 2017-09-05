package com.xiaotu.makeplays.mobile.server.user;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

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

import com.xiaotu.makeplays.authority.controller.dto.UserAuthDto;
import com.xiaotu.makeplays.authority.model.AuthorityModel;
import com.xiaotu.makeplays.authority.model.UserAuthMapModel;
import com.xiaotu.makeplays.authority.model.constants.AuthorityPlatform;
import com.xiaotu.makeplays.authority.service.AuthorityService;
import com.xiaotu.makeplays.authority.service.UserAuthMapService;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.model.CrewUserMapModel;
import com.xiaotu.makeplays.crew.model.constants.CrewUserStatus;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.crew.service.CrewRoleUserMapService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.mobile.server.common.BaseFacade;
import com.xiaotu.makeplays.mobile.server.role.ViewRoleFacade;
import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;
import com.xiaotu.makeplays.roleactor.service.ViewRoleService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.sysrole.model.SysroleInfoModel;
import com.xiaotu.makeplays.sysrole.service.SysRoleInfoService;
import com.xiaotu.makeplays.sysrole.service.UserRoleMapService;
import com.xiaotu.makeplays.user.model.JoinCrewApplyMsgModel;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.model.constants.JoinCrewAuditStatus;
import com.xiaotu.makeplays.user.model.constants.UserStatus;
import com.xiaotu.makeplays.user.service.JoinCrewApplyMsgService;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.RegexUtils;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.verifycode.model.VerifyCodeInfoModel;
import com.xiaotu.makeplays.verifycode.model.constants.VerifyCodeType;
import com.xiaotu.makeplays.verifycode.service.VerifyCodeInfoService;

/**
 * 用户信息相关的接口 
 * @author xuchangjian 2016-9-22下午4:21:21
 */
@Controller
@RequestMapping("/interface/userFacade")
public class UserFacade extends BaseFacade{
	
	Logger logger = LoggerFactory.getLogger(ViewRoleFacade.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthorityService authorityService;
	
	@Autowired
	private SysRoleInfoService sysRoleInfoService;
	
	@Autowired
	private JoinCrewApplyMsgService joinCrewApplyMsgService;
	
	@Autowired
	private UserAuthMapService userAuthMapService;
	
	@Autowired
	private UserRoleMapService userRoleMapService;
	
	@Autowired
	private CrewRoleUserMapService crewRoleUserMapService;
	
	@Autowired
	private ViewRoleService viewRoleService;
	
	@Autowired
	private VerifyCodeInfoService verifyCodeInfoService;
	
	@Autowired
	private CrewInfoService crewInfoService;
	
	/**
	 * 登录
	 * @param phone	手机号
	 * @param password	密码（MD5加密密文）
	 * @param token
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/login")
	public Object login(HttpServletRequest request, String phone,
			String password, String token, Integer clientType, String appVersion) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			//数据校验
			if (StringUtils.isBlank(phone)) {
				throw new IllegalArgumentException("手机号不能为空");
			}
			if (StringUtils.isBlank(password)) {
				throw new IllegalArgumentException("密码不能为空");
			}
			if (clientType == null) {
				throw new IllegalArgumentException("客户端类型不能为空");
			}
//			if (appVersion == null) {
//				throw new IllegalArgumentException("版本号不能为空");
//			}
			
			//校验用户信息
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("phone", phone);
			List<UserInfoModel> phoneUserList = this.userService.queryManyByMutiCondition(conditionMap, null);
			if (phoneUserList == null || phoneUserList.size() == 0) {
				throw new IllegalArgumentException("该手机号未注册");
			}
			
			userInfo = this.userService.queryByPhoneAndPwd(phone, password);
			if (userInfo == null) {
				throw new IllegalArgumentException("密码错误");
			}
			
			//校验用户状态
			if(userInfo.getStatus().intValue() == UserStatus.Invalid.getValue()){
				throw new IllegalArgumentException("您的账户状态异常，请联系管理员");
			}
			
			//执行登录操作，返回移动端需要的数据
			resultMap = this.userService.mobileUserLogin(userInfo.getUserId(), clientType, token, appVersion);
			
			
			//校验移动端是否需要弹窗
			boolean needAlert = false;
			String content = "系统将于几小时内升级，请耐心等待";
//			if (!phone.equals("18810967140")) {
//				needAlert = true;
//			}
			
			resultMap.put("needAlert", needAlert);
			resultMap.put("alertContent", content);
			
			request.setAttribute("userId", userInfo.getUserId());
			
			this.sysLogService.saveSysLogForApp(request, "app用户登录", userInfo.getClientType(), UserInfoModel.TABLE_NAME, null, 99);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，登录失败", e);
			this.sysLogService.saveSysLogForApp(request, "app用户登录失败：" + e.getMessage(), userInfo.getClientType(), UserInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，登录失败", e);
		}
		
		return resultMap;
	}
	
	/**
	 * 登出
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/logout")
	public Object logout(HttpServletRequest request, String userId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if(StringUtils.isBlank(userId)){
				throw new IllegalArgumentException("用户ID不能为空");
			}
			
			this.userService.clearUserToken(userId);
			
			this.sysLogService.saveSysLogForApp(request, "app用户登出", this.getClientType(userId), null, null, 99);
		} catch(IllegalArgumentException ie){
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		}catch (Exception e) {
			logger.error("未知异常，登录失败", e);
			this.sysLogService.saveSysLogForApp(request, "app用户登出失败：" + e.getMessage(), this.getClientType(userId), null, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常", e);
		}
		return resultMap;
	}

	/**
	 * 注册
	 * @param phone	手机号
	 * @param verifyCode	验证码
	 * @param password	密码（MD5密文）
	 * @param realName	姓名
	 * @param sex	性别
	 * @param token	
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/register")
	public Object register(String phone, String verifyCode, String password, String realName, Integer sex, String token, Integer clientType, String appVersion) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			this.checkRegistUserInfo(phone, verifyCode, realName, password, sex, token, clientType);
			
			UserInfoModel userInfo = this.userService.registerOneUser(phone, verifyCode, password, realName, sex, token, clientType, appVersion);
			
			resultMap.put("userId", userInfo.getUserId());
			resultMap.put("realName", userInfo.getRealName());
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，注册失败", e);
			throw new IllegalArgumentException("未知异常，注册失败", e);
		}
		
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
	private void checkRegistUserInfo(String phone, String verifyCode, String realName, 
			String password, Integer sex, String token, Integer clientType) throws Exception {
		//基本信息
		if (StringUtils.isBlank(realName)) {
			throw new IllegalArgumentException("请填写姓名");
		}
		if (StringUtils.isBlank(password)) {
			throw new IllegalArgumentException("密码不能为空");
		}
		if (sex == null) {
			throw new IllegalArgumentException("性别不能为空");
		}
		if (clientType == null) {
			throw new IllegalArgumentException("客户端类型不能为空");
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
	public Object findbackPassword(HttpServletRequest request, String phone,
			String verifyCode, String newPassword, String token,
			Integer clientType, String appVersion) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			if (StringUtils.isBlank(newPassword)) {
				throw new IllegalArgumentException("密码不能为空");
			}
			if (clientType == null) {
				throw new IllegalArgumentException("客户端类型不能为空");
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
			userInfo = this.userService.queryByPhone(phone);
			if (userInfo == null) {
				throw new IllegalArgumentException("该手机号未注册");
			}
			//校验用户状态
			if(userInfo.getStatus().intValue() == Constants.USER_STATUS_INVALID){
				throw new IllegalArgumentException("您的账户状态异常，请联系管理员");
			}
			
			resultMap = this.userService.mobileFindbackPassword(userInfo.getUserId(), phone, newPassword, token, clientType, appVersion);
			
			this.sysLogService.saveSysLogForApp(request, "找回密码", userInfo.getClientType(), VerifyCodeInfoModel.TABLE_NAME + "," + UserInfoModel.TABLE_NAME, phone, 0);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，找回密码失败", e);
			this.sysLogService.saveSysLogForApp(request, "找回密码失败：" + e.getMessage(), userInfo.getClientType(), 
					VerifyCodeInfoModel.TABLE_NAME + "," + UserInfoModel.TABLE_NAME, phone, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，找回密码失败", e);
		}
		
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
	public Object updatePhone(HttpServletRequest request, String userId, String phone, String password, String verifyCode) {
		UserInfoModel userInfo = new UserInfoModel();
		try {
			MobileUtils.checkUserValid(userId);
			
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
			userInfo = this.userService.queryById(userId);
			if (!userInfo.getPassword().equals(password)) {
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
					throw new IllegalArgumentException("手机号已被其他用户使用!");
				}
			}
			
			this.userService.updateUserPhone(userId, phone);
			
			this.sysLogService.saveSysLogForApp(request, "修改手机号", userInfo.getClientType(), 
					VerifyCodeInfoModel.TABLE_NAME + "," + UserInfoModel.TABLE_NAME, null, 1);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，修改手机号失败", e);
			this.sysLogService.saveSysLogForApp(request, "修改手机号失败：" + e.getMessage(), userInfo.getClientType(), 
					VerifyCodeInfoModel.TABLE_NAME + "," + UserInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，修改手机号失败", e);
		}
		
		return null;
	}
	
	/**
	 * 修改密码
	 * @param userId
	 * @param password	MD5密文
	 * @param newPassword	MD5密文
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updatePassword")
	public Object updatePassword(HttpServletRequest request, String userId, String password, String newPassword) {
		UserInfoModel userInfo = new UserInfoModel();
		try {
			MobileUtils.checkUserValid(userId);
			
			if (StringUtils.isBlank(password)) {
				throw new IllegalArgumentException("密码不能为空");
			}
			if (StringUtils.isBlank(newPassword)) {
				throw new IllegalArgumentException("新密码不能为空");
			}
			
			//校验用户信息
			userInfo = this.userService.queryById(userId);
			if (!userInfo.getPassword().equals(password)) {
				throw new IllegalArgumentException("密码错误");
			}
			
			userInfo.setPassword(newPassword);
			this.userService.updateOne(userInfo);
			
			this.sysLogService.saveSysLogForApp(request, "修改密码", userInfo.getClientType(), UserInfoModel.TABLE_NAME, null, 1);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，修改密码失败", e);
			this.sysLogService.saveSysLogForApp(request, "修改密码失败：" + e.getMessage(), userInfo.getClientType(), UserInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，修改密码失败", e);
		}
		
		return null;
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
	public Object updateUserBaseInfo(HttpServletRequest request, String userId,
			String crewId, String realName, Integer sex, Integer age,
			String email, String profile) {
		UserInfoModel userInfo = new UserInfoModel();
		try {
			MobileUtils.checkUserValid(userId);
			
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
			
			userInfo = this.userService.queryById(userId);
			userInfo.setRealName(realName);
			userInfo.setSex(sex);
			userInfo.setAge(age);
			userInfo.setEmail(email);
			userInfo.setProfile(profile);
			
			this.userService.updateOne(userInfo);
			
			this.sysLogService.saveSysLogForApp(request, "修改用户基本信息", userInfo.getClientType(), UserInfoModel.TABLE_NAME, null, 0);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，修改用户信息失败", e);
			this.sysLogService.saveSysLogForApp(request, "修改用户基本信息失败：" + e.getMessage(), userInfo.getClientType(), UserInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，修改用户信息失败", e);
		}
		
		return null;
	}
	
	/**
	 * 获取用户信息
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainUserInfo")
	public Object obtainUserInfo(String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkUserValid(userId);
			
			UserInfoModel userInfo = this.userService.queryById(userId);
			
			String imgUrl = userInfo.getBigImgUrl();
			if (StringUtils.isBlank(imgUrl)) {
				Resource resource = new ClassPathResource("/config.properties");
				Properties props = PropertiesLoaderUtils.loadProperties(resource);
				String serverPath = (String) props.get("server.basepath");
				imgUrl = serverPath + Constants.DEFAULT_USER_PIC;
			} else {
				imgUrl = FileUtils.genPreviewPath(imgUrl);
			}
			
			resultMap.put("imgUrl", imgUrl);
			resultMap.put("realName", userInfo.getRealName());
			resultMap.put("sex", userInfo.getSex());
			resultMap.put("age", userInfo.getAge());
			resultMap.put("email", userInfo.getEmail());
			resultMap.put("phone", userInfo.getPhone());
			resultMap.put("token", userInfo.getToken());
			resultMap.put("profile", userInfo.getProfile());
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取用户信息失败", e);
			throw new IllegalArgumentException("未知异常，获取用户信息失败", e);
		}
		
		return resultMap;
	}
	
	/**
	 * 切换剧组
	 * @param userId 
	 * @param crewId  切换的剧组id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/switchCrew") 
	public Object switchCrew(String userId, String crewId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			//判断用户是否有效
			MobileUtils.checkCrewUserValid(crewId, userId);
			this.userService.switchCrew(userId, crewId);
			
			CrewInfoModel crewInfo = this.crewInfoService.queryById(crewId);
			
			List<ViewRoleModel> focusRoleList = this.viewRoleService.queryUserFocusRoleInfo(crewId, userId);
			
			resultMap.put("focusRoleList", focusRoleList);
			resultMap.put("crewType", crewInfo.getCrewType());
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，切换剧组失败", e);
			throw new IllegalArgumentException("未知异常，切换剧组失败", e);
		}
		
		return resultMap;
	}
	
	/**
	 * 上传用户头像
	 * @param userModel
	 * @param iconData
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/uploadUserHeader")
	public Object uploadUserHeader(String userId, MultipartFile iconData) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkUserValid(userId);
			
			if (iconData == null) {
				throw new IllegalArgumentException("请选择头像图片");
			}
			
			UserInfoModel userInfo = this.userService.updateUserImg(userId, iconData);
            
            resultMap.put("imgUrl", FileUtils.genPreviewPath(userInfo.getBigImgUrl()));
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，切换剧组失败", e);
			throw new IllegalArgumentException("未知异常，切换剧组失败", e);
		}
		
		return resultMap;
	}
	
	/**
	 * 校验用户是否有指定的权限
	 * @param crewId
	 * @param userId
	 * @param authCode
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/checkHasAuthority")
	public Object checkHasAuthority(String crewId, String userId, String authCode) {
		boolean hasAuthority = false;
		try {
			hasAuthority = this.authorityService.checkHashAuthority(crewId, userId, authCode);
		} catch (Exception e) {
			throw new IllegalArgumentException("未知异常，校验用户权限失败", e);
		}
		return hasAuthority;
	}
	
	/**
	 * 校验用户是否可以创建剧组
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/checkCanCreateCrew")
	public Object checkCanCreateCrew(String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkUserValid(userId);
			
			UserInfoModel userInfo = this.userService.queryById(userId);
			
			if (userInfo.getUbCreateCrewNum() <= 0) {
				throw new IllegalArgumentException("您的建组机会已用完，请联系系统客服人员");
			}
			
		} catch (IllegalArgumentException ie) {
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			throw new IllegalArgumentException("未知异常", e);
		}
		
		return resultMap;
	}
	
	/**
	 * 申请加入指定剧组
	 * @param userId 
	 * @param crewId 加入剧组id
	 * @param remark 备注
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/joinCrew")
	public Object joinCrew(HttpServletRequest request, String userId, String crewId, String enterPassword, String roleIds, String roleNames, String remark) {
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkUserValid(userId);
			if (StringUtils.isBlank(crewId)) {
				throw new IllegalArgumentException("请选择想要加入的剧组");
			}
			if (StringUtils.isBlank(enterPassword)) {
				throw new IllegalArgumentException("请输入入组密码");
			}
			if (StringUtils.isBlank(roleIds)) {
				throw new IllegalArgumentException("请选择职务信息");
			}
			
			this.userService.joinCrew(userId, crewId, enterPassword, roleIds, roleNames, remark);
			
			this.sysLogService.saveSysLogForApp(request, "申请加入指定剧组", userInfo.getClientType(), JoinCrewApplyMsgModel.TABLE_NAME, null, 1);
		} catch (IllegalArgumentException ie) {
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			this.sysLogService.saveSysLogForApp(request, "申请加入指定剧组失败：" + e.getMessage(), userInfo.getClientType(), JoinCrewApplyMsgModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常", e);
		}
		
		return null;
	}
	
	/**
	 * 获取剧组下所有成员信息
	 * 包括被冻结的用户数
	 * @param userId
	 * @param crewId
	 * @return
	 */
	@RequestMapping("/obtainCrewUserList")
	@ResponseBody
	public Object obtainCrewUserList(String userId, String crewId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
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
				
				auditingUserList.add(auditingUserInfo);
			}
			
			
			//剧组已有人员部门信息
			List<Map<String, Object>> crewGroupList = this.sysRoleInfoService.queryCrewGroupInfo(crewId);
			
			//剧组总人数
			List<Map<String, Object>> crewUserNumList = this.sysRoleInfoService.queryCrewUserNum(crewId);
			int crewUserNum = 0;
			if(crewUserNumList != null && crewUserNumList.size() > 0) {
				crewUserNum = Integer.parseInt(crewUserNumList.get(0).get("userNum") + "");
			}
			
			resultMap.put("toAuditUserList", auditingUserList);
			resultMap.put("crewUserList", crewGroupList);
			resultMap.put("crewUserNum", crewUserNum);
			
		} catch (IllegalArgumentException ie) {
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			throw new IllegalArgumentException("未知异常", e);
		}
		return resultMap;
	}
	
	/**
	 * 获取指定剧组的指定小组下人员列表
	 * 只返回状态为有效的用户
	 * 在剧组中被冻结的用户也需要返回
	 * @return
	 */
	@RequestMapping("/obtainGroupUserList")
	@ResponseBody
	public Object obtainGroupUserList(String userId, String crewId, String groupId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(crewId)) {
				throw new IllegalArgumentException("无效的剧组访问");
			}
			if (StringUtils.isBlank(groupId)) {
				throw new IllegalArgumentException("请选择分组");
			}
			
			//小组名、小组内总人数
			SysroleInfoModel sysRoleInfo = this.sysRoleInfoService.queryById(groupId);
			String groupName = sysRoleInfo.getRoleName();
			
			List<UserInfoModel> userList = this.userService.queryCrewUserByGroupId(crewId, groupId);
			int userNum = userList.size();
			
			List<Map<String, Object>> roleInfo = new ArrayList<Map<String, Object>>();	//小组内职务信息
			List<Map<String, Object>> groupUserList = this.userService.queryByCrewGroupId(crewId, groupId);	//小组内用户信息
			
			List<String> roleNames = new ArrayList<String>();
			for (Map<String, Object> map : groupUserList) {
				String roleName = (String) map.get("roleName");
				
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
			resultMap.put("userNum", userNum);
			resultMap.put("roleInfo", roleInfo);
			
		} catch (IllegalArgumentException ie) {
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			throw new IllegalArgumentException("未知异常", e);
		}
		
		return resultMap;
	}
	
	/**
	 * 获取用户的职务和权限接口
	 * @param crewId	剧组ID
	 * @param userId	用户ID
	 * @return
	 */
	@RequestMapping("/obtainCrewUserInfo")
	@ResponseBody
	public Object obtainCrewUserInfo(String crewId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
//			MobileUtils.checkCrewUserValid(crewId, userId);	//该人可能是被冻结状态，所以不能用此校验
			if (StringUtils.isBlank(crewId)) {
				throw new IllegalArgumentException("无效的剧组访问");
			}
			
			//用户名、电话、在剧组中的状态
			UserInfoModel userInfo = this.userService.queryById(userId);
			String userName = userInfo.getRealName();
			String phone = userInfo.getPhone();
			
			CrewUserMapModel crewUserMap = this.userService.queryCrewUserBycrewId(userId, crewId);
			int status = crewUserMap.getStatus();
			
			resultMap.put("userName", userName);
			resultMap.put("phone", phone);
			resultMap.put("status", status);
			
			//用户职务信息
			List<Map<String, Object>> userRoleList = this.sysRoleInfoService.queryByCrewUserId(crewId, userId);
			List<Map<String, Object>> roleList = new ArrayList<Map<String, Object>>();
			boolean isActorUser = false;
			for (Map<String, Object> map : userRoleList) {
				String roleId = (String) map.get("roleId");
				String roleName = (String) map.get("roleName");
				String parentName = (String) map.get("parentName");
				
				Map<String, Object> singleRoleInfo = new HashMap<String, Object>();
				singleRoleInfo.put("roleId", roleId);
				if (parentName != null) {
					singleRoleInfo.put("roleName", parentName + "-" + roleName);
					
					if (parentName.equals("演员组")) {
						isActorUser = true;
					}
				} else {
					singleRoleInfo.put("roleName", roleName);
				}
				roleList.add(singleRoleInfo);
			}
			resultMap.put("roleList", roleList);
			
			//用户权限信息
			List<UserAuthDto> appAuthList = new ArrayList<UserAuthDto>();	//app端权限
			List<UserAuthDto> pcAuthList = new ArrayList<UserAuthDto>();	//pc端权限
			
			//系统中所有的权限信息
//			List<AuthorityModel> authList = this.authorityService.queryAuthByPlatformWithoutAdmin(null);
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
			
			
			//演员用户扮演的角色
			List<ViewRoleModel> viewRoleList = this.viewRoleService.queryUserRoleInfo(crewId, userId);
			resultMap.put("relatedRoles", viewRoleList);
			resultMap.put("isActorUser", isActorUser);
			
		} catch (IllegalArgumentException ie) {
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			throw new IllegalArgumentException("未知异常", e);
		}
		
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
			
			//此处parentAuthList和childAuthList数据在多层权限结构中必然有交集
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
		 * 因此，此处对比出lastAuthList中每个权限的子权限，然后为相应字段赋值
		 * 
		 * 如果数据在userAuthList存在且在lastAuthList中找不到父权限，则说明此数据层级为当前循环的叶子权限
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
	 * 保存用户职务信息
	 * @param userId	用户ID
	 * @param crewId	剧组ID
	 * @param roleId	职务ID
	 * @return
	 */
	@RequestMapping("/saveUserRoleInfo")
	@ResponseBody
	public Object saveUserRoleInfo (String userId, String crewId, String aimUserId, String roleIds) {
		
		try {
			//校验
			MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(roleIds)) {
				throw new IllegalArgumentException("用户至少应该拥有一个职务");
			}
			
			List<String> roleIdList = Arrays.asList(roleIds.split(","));
			if (roleIdList.contains(Constants.ROLE_ID_ADMIN)) {
				//查询剧组下剧组管理员的数量
				List<UserInfoModel> managerList = this.userService.queryCrewUserByGroupId(crewId, Constants.ROLE_ID_ADMIN);
				boolean isManager = false;
				for (UserInfoModel userInfo : managerList) {
					if (userInfo.getUserId().equals(aimUserId)) {
						isManager = true;
						break;
					}
				}
				if (!isManager && managerList.size() >= 3) {
					throw new IllegalArgumentException("剧组管理员不能超过三个");
				}
			}
			
			this.userRoleMapService.saveUserRoleInfo(crewId, aimUserId, roleIds);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，保存用户职务信息失败", e);
			throw new IllegalArgumentException("未知异常，保存用户职务信息失败", e);
		}
		
		return null;
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
	public Object saveUserAuthInfo(String userId, String crewId, String aimUserId, int operateType, String authId, Boolean readonly) {
		
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
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
			}
			
			//新增
			if (operateType != 3 && userAuthMap == null) {
				userAuthMap = new UserAuthMapModel();
				userAuthMap.setAuthId(authId);
				userAuthMap.setCrewId(crewId);
				userAuthMap.setMapId(UUIDUtils.getId());
				userAuthMap.setReadonly(readonly);
				userAuthMap.setUserId(aimUserId);
				this.userAuthMapService.addOne(crewId, aimUserId, userAuthMap);
			}
			
			//修改
			if (operateType != 3 && userAuthMap != null) {
				userAuthMap.setAuthId(authId);
				userAuthMap.setCrewId(crewId);
				userAuthMap.setReadonly(readonly);
				userAuthMap.setUserId(aimUserId);
				this.userAuthMapService.updateOne(userAuthMap);
			}
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，保存权限失败", e);
			throw new IllegalArgumentException("未知异常", e);
		}
		
		return null;
	}
	
	/**
	 * 修改用户在剧组中的状态接口
	 * @param crewId	剧组ID
	 * @param userId	当前操作人ID
	 * @param operateType	操作类型：1--冻结   2--解冻
	 * @param aimUserId	要修改的用户ID
	 * @return
	 */
	@RequestMapping("/updateUserStatus")
	@ResponseBody
	public Object updateUserStatus (String crewId, String userId, int operateType, String aimUserId) {
		
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			if (StringUtils.isBlank(crewId)) {
				throw new IllegalArgumentException("无效的剧组访问");
			}
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
			
		} catch (IllegalArgumentException ie) {
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			throw new IllegalArgumentException("未知异常", e);
		}
		
		return null;
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
	public Object auditEnterApply(String userId, String crewId, Boolean agree, String aimUserId) {
		
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			if (StringUtils.isBlank(crewId)) {
				throw new IllegalArgumentException("无效的剧组访问");
			}
			if (StringUtils.isBlank(aimUserId)) {
				throw new IllegalArgumentException("请选择需要操作的用户");
			}
			
			if (agree == null) {
				agree = false;
			}
			
			this.joinCrewApplyMsgService.auditEnterApply(userId, crewId, agree, aimUserId);
			
		} catch (IllegalArgumentException ie) {
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			throw new IllegalArgumentException("未知异常", e);
		}
		
		return null;
	}
	
	/**
	 * 保存演员用户和剧组中场景角色关联信息接口
	 * @param userId
	 * @param crewId
	 * @param aimUserId
	 * @param viewRoleId
	 * @return
	 */
	@RequestMapping("/saveActorUserCrewRoleRelation")
	@ResponseBody
	public Object saveActorUserCrewRoleRelation(String userId, String crewId, String aimUserId, String viewRoleIds) {
		
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			if (StringUtils.isBlank(aimUserId)) {
				throw new IllegalArgumentException("请选择需要操作的用户");
			}
			if (StringUtils.isBlank(viewRoleIds)) {
				throw new IllegalArgumentException("请选择关联的角色");
			}
			
			this.crewRoleUserMapService.saveActorUserCrewRoleRelation(crewId, aimUserId, viewRoleIds);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，关联角色失败", e);
			throw new IllegalArgumentException("未知异常，关联角色失败", e);
		}
		
		return null;
	}
	
	/**
	 * 更新个人简介
	 * @param userId
	 * @param profile
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateProfile")
	public Object updateProfile(HttpServletRequest request, String userId, String profile) {
		UserInfoModel userInfo = new UserInfoModel();
		try {
			MobileUtils.checkUserValid(userId);
			
			userInfo = this.userService.queryById(userId);
			userInfo.setProfile(profile);
			this.userService.updateOne(userInfo);
			
			this.sysLogService.saveSysLogForApp(request, "修改个人简介", userInfo.getClientType(), UserInfoModel.TABLE_NAME, null, 1);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，修改个人简介失败", e);
			this.sysLogService.saveSysLogForApp(request, "修改个人简介失败：" + e.getMessage(), userInfo.getClientType(), UserInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，修改个人简介失败", e);
		}
		
		return null;
	}
	
	/**
	 * 获取剧组下所有用户信息
	 * 带有名称首字母信息的返回
	 * @param crewId
	 * @param userId
	 * @param isHasPRole 是否包含分组
	 * @param isHasManager 是否包含系统管理员
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainCrewAllUserListWithFletter")
	public Object obtainCrewAllUserListWithFletter(HttpServletRequest request, String crewId, String userId, boolean isHasPRole, boolean isHasManager) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = null;
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
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
					userMap.put("name", myUserName);
					userMap.put("roleNames", userIdRoleMap.get(myUserId));
					userMap.put("phone", phone);
					userMap.put("fletter", fletter);
					
					crewUserMapList.add(userMap);
					
					userIdList.add(myUserId);
				}
			}
			
			resultMap.put("userList", crewUserMapList);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取用户列表失败", e);
			this.sysLogService.saveSysLogForApp(request, "获取用户列表失败：" + e.getMessage(), userInfo.getClientType(), UserInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，获取用户列表失败", e);
		}
		
		return resultMap;
	}

	/**
	 * 获取剧组成员信息,按职务分组
	 * @param request
	 * @return
	 */
	@RequestMapping("/obtainCrewUserGroupList")
	@ResponseBody
	public Object obtainCrewUserGroupList (String crewId, String userId, boolean isHasManager) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			//剧组已有人员部门信息
			List<Map<String, Object>> userList = this.userService.queryCrewUserListWithRole(crewId);
			
			//职务列表
			List<Map<String, Object>> roleList = new ArrayList<Map<String, Object>>();
			List<String> roleNameList = new ArrayList<String>();
			
			//按照职务分组
			for (Map<String, Object> userMap : userList) {
				String roleId = (String) userMap.get("roleId");
				String roleName = (String) userMap.get("roleName");
				
				//系统管理员
				if((roleId.equals(Constants.ROLE_ID_ADMIN) || roleId.equals(Constants.ROLE_ID_PROJECT_DIRECTOR)) && !isHasManager) {
					continue;
				}
				
				Map<String, Object> myUserInfo = new HashMap<String, Object>();
				myUserInfo.put("userId", (String) userMap.get("userId"));
				myUserInfo.put("name", (String) userMap.get("realName"));
				myUserInfo.put("proleName", (String) userMap.get("proleName"));
//				myUserInfo.put("phone", (String) userMap.get("phone"));
//				myUserInfo.put("fletter", (String) userMap.get("fletter"));
				
				if (!roleNameList.contains(roleName)) {
					roleNameList.add(roleName);
					
					Map<String, Object> roleMapMap = new HashMap<String, Object>();
					roleMapMap.put("roleName", roleName);
					
					List<Map<String, Object>> roleUserList = new ArrayList<Map<String, Object>>();
					roleUserList.add(myUserInfo);
					
					roleMapMap.put("roleUserList", roleUserList);
					
					roleList.add(roleMapMap);
					
				} else {
					Map<String, Object> roleMap = roleList.get(roleNameList.indexOf(roleName));
					List<Map<String, Object>> roleUserList = (List<Map<String, Object>>) roleMap.get("roleUserList");
					
					roleUserList.add(myUserInfo);
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
			
			resultMap.put("crewUserGroupList", groupList);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取用户列表失败", e);
			throw new IllegalArgumentException("未知异常，获取用户列表失败", e);
		}
		return resultMap;
	}
	
	/**
	 * 获取剧组拥有某权限的成员信息
	 * @param request
	 * @param authId
	 * @return
	 */
	@RequestMapping("/obtainAuthUserList")
	@ResponseBody
	public Object obtainAuthUserList (String crewId, String userId, String authId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {	
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			if(authId == null) {
				throw new IllegalArgumentException("请选择需要操作的权限");
			}
			
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
			//计算小组人数
			for(Map<String, Object> groupMap : groupList) {
				Set<String> userIdSet = new HashSet<String>();
				List<Map<String, Object>> groupRoleList = (List<Map<String, Object>>) groupMap.get("groupRoleList");
				for(Map<String, Object> roleMap : groupRoleList) {
					List<Map<String, Object>> roleUserList = (List<Map<String, Object>>) roleMap.get("roleUserList");
					for(Map<String, Object> userMap : roleUserList) {
						userIdSet.add((String) userMap.get("userId"));
					}
				}
				groupMap.put("userNum", userIdSet.size());
			}
			
			resultMap.put("authUserList", groupList);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取剧组拥有某权限的成员信息失败", e);
			throw new IllegalArgumentException("未知异常，获取剧组拥有某权限的成员信息失败", e);
		}
		
		return resultMap;		
	}
}