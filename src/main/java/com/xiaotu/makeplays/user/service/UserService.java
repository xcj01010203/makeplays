package com.xiaotu.makeplays.user.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.authority.dao.UserAuthMapDao;
import com.xiaotu.makeplays.crew.dao.CrewInfoDao;
import com.xiaotu.makeplays.crew.dao.CrewUserMapDao;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.model.CrewUserFilter;
import com.xiaotu.makeplays.crew.model.CrewUserMapModel;
import com.xiaotu.makeplays.crew.model.constants.CrewUserStatus;
import com.xiaotu.makeplays.crew.model.constants.CrewUserType;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.crew.service.CrewUserMapService;
import com.xiaotu.makeplays.finance.service.CurrencyInfoService;
import com.xiaotu.makeplays.message.dao.MessageInfoDao;
import com.xiaotu.makeplays.message.model.MessageInfoModel;
import com.xiaotu.makeplays.message.model.constants.MessageInfoStatus;
import com.xiaotu.makeplays.message.model.constants.MessageType;
import com.xiaotu.makeplays.message.service.MessageInfoService;
import com.xiaotu.makeplays.mobile.push.umeng.model.android.AndroidPushMsg;
import com.xiaotu.makeplays.mobile.push.umeng.model.ios.IOSPushMsg;
import com.xiaotu.makeplays.mobile.push.umeng.service.android.UmengAndroidPushService;
import com.xiaotu.makeplays.mobile.push.umeng.service.ios.UmengIOSPushService;
import com.xiaotu.makeplays.roleactor.dao.ViewRoleDao;
import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;
import com.xiaotu.makeplays.roleactor.service.ViewRoleService;
import com.xiaotu.makeplays.sys.model.SysLoginModel;
import com.xiaotu.makeplays.sys.service.SysLogService;
import com.xiaotu.makeplays.sysrole.dao.UserRoleMapDao;
import com.xiaotu.makeplays.sysrole.model.UserRoleMapModel;
import com.xiaotu.makeplays.sysrole.service.SysRoleInfoService;
import com.xiaotu.makeplays.sysrole.service.UserRoleMapService;
import com.xiaotu.makeplays.user.controller.filter.UserFilter;
import com.xiaotu.makeplays.user.dao.JoinCrewApplyMsgDao;
import com.xiaotu.makeplays.user.dao.UserInfoDao;
import com.xiaotu.makeplays.user.model.JoinCrewApplyMsgModel;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.model.constants.JoinCrewAuditStatus;
import com.xiaotu.makeplays.user.model.constants.UserClientType;
import com.xiaotu.makeplays.user.model.constants.UserStatus;
import com.xiaotu.makeplays.user.model.constants.UserType;
import com.xiaotu.makeplays.utils.AuthorityConstants;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.IpUtil;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.RegexUtils;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.verifycode.dao.VerifyCodeInfoDao;
import com.xiaotu.makeplays.verifycode.model.constants.VerifyCodeType;
import com.xiaotu.makeplays.verifycode.service.VerifyCodeInfoService;
@Service
public class UserService {

	private static Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private UserInfoDao userInfoDao;
	
	@Autowired
	private CrewUserMapDao crewUserMapDao;
	
	@Autowired
	private VerifyCodeInfoDao verifyCodeInfoDao;
	
	@Autowired
	private CrewInfoDao crewInfoDao;
	
	@Autowired
	private VerifyCodeInfoService verifyCodeInfoService;
	
	@Autowired
	private MessageInfoDao messageInfoDao;
	
	@Autowired
	private UmengIOSPushService umengIOSPushService;
	
	@Autowired
	private UmengAndroidPushService umengAndroidPushService;
	
	@Autowired
	private ViewRoleDao viewRoleDao;
	
	@Autowired
	private JoinCrewApplyMsgDao joinCrewApplyMsgDao;
	
	@Autowired
	private CurrencyInfoService currencyInfoService;
	
	@Autowired
	private UserRoleMapDao userRoleMapDao;
	
	@Autowired
	private UserRoleMapService userRoleMapService;
	
	@Autowired
	private UserAuthMapDao userAuthMapDao;
	
	@Autowired
	private CrewContactService crewContactService;
	
	@Autowired
	private CrewInfoService crewInfoService;
	
	@Autowired
	private SysLogService sysLogService;
	
	@Autowired
	private MessageInfoService messageInfoService;
	
	@Autowired
	private ViewRoleService viewRoleService;
	
	@Autowired
	private CrewUserMapService crewUserMapService;
	
	@Autowired
	private SysRoleInfoService sysRoleInfoService;
	
	/**
	 * 记录日志
	 * @param request
	 * @param userId
	 * @throws Exception
	 */
	public void insertLoginInfo(HttpServletRequest request,String userId) throws Exception{
		final String ip = IpUtil.getUserIp(request);
		final String id = userId;
		//判断用户是否用此ip登陆过
		if(!this.sysLogService.getIsExistLog(userId, ip)){
			new Thread(){
				public void run(){
					SysLoginModel slm = new SysLoginModel();
					slm.setLogId(UUIDUtils.getId());
					slm.setUserId(id);
					slm.setIp(ip);
					try {
						slm.setAddress(IpUtil.getIpArea(ip));
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
					slm.setClientType(0);
					try {
						sysLogService.add(slm);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
	
	/**
	 * 根据多个条件查询道具信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<UserInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.userInfoDao.queryManyByMutiCondition(conditionMap, page);
	}
	
	/**
	 * 添加用户
	 * @param userInfo
	 * @roleId 用于客服，角色ID
	 * @throws Exception
	 */
	public void addUser(UserInfoModel userInfo, String roleId) throws Exception{
		userInfoDao.add(userInfo);
		//客服，增加用户角色关联关系信息，剧组ID设为0
		if(userInfo.getType() == 2) {
			UserRoleMapModel userRoleMap = new UserRoleMapModel();
			userRoleMap.setMapId(UUIDUtils.getId());
			userRoleMap.setUserId(userInfo.getUserId());
			userRoleMap.setCrewId("0");
			userRoleMap.setRoleId(roleId);
			
			userRoleMapDao.add(userRoleMap);
		}
		
	}
	
	
	/**
	 * 更新用户信息
	 * 首先判断手机号和登录名是否已经被占用
	 * 
	 * @param userInfo
	 * @return
	 */
	public Map<String, Object> updateUser(UserInfoModel userInfo, String roleId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
        boolean success = true;
        try {
        	//获取客服原来的角色，用于判断客服类型是否发生变化
        	String oldRoleId = "";
        	if(userInfo.getType() == 2 ) {//客服
        		oldRoleId = this.userRoleMapService.queryAllUserRoleIds(userInfo.getUserId());
        	}
        	String phone = userInfo.getPhone();
        	String userName = userInfo.getUserName();
        	String userId = userInfo.getUserId();
        	Integer age = userInfo.getAge();
        	String email = userInfo.getEmail();
        	if(age!=null&&StringUtils.isNotBlank(String.valueOf(age))){
        		if (!RegexUtils.regexFind(Constants.REGEX_AGE, String.valueOf(age))) {
    				throw new IllegalArgumentException("年龄不合法（0-120）");
    			}
        	}
    		
        	
    		if(StringUtils.isNotBlank(email)){
    			if (!RegexUtils.regexFind(Constants.REGEX_EMAIL, email)) {
    				throw new IllegalArgumentException("邮箱格式不合法");
    			}
    		}
        	
        	if(StringUtils.isBlank(userId)){
        		throw new IllegalArgumentException("用户信息异常");
        	}
        	if(StringUtils.isNotBlank(userName)){
        		List<UserInfoModel> list = this.queryUserInfoByLoginNameExcepOwn(userId, userName);
        		if(list!=null&&list.size()>0){
        			throw new IllegalArgumentException("用户名已经被占用");
        		}
        	}
        	if(StringUtils.isNotBlank(phone)){
        		List<UserInfoModel> list = this.queryUserInfoByLoginNameExcepOwn(userId, phone);
        		if(list!=null&&list.size()>0){
        			throw new IllegalArgumentException("该手机号已经被注册");
        		}
        	}
        	userInfoDao.update(userInfo,"userId");
        	
        	//更新客服角色
        	if(userInfo.getType() == 2 && !roleId.equals(oldRoleId)) {//客服,且类型发生变化
        		String customerServiceId = userInfo.getUserId();
//        		this.crewUserMapDao.updateRoleIdByCustomer(customerServiceId, roleId);
        		this.userRoleMapDao.updateRoleIdByCustomerService(customerServiceId, roleId);
        	}
        	
        	resultMap.put("success", success);
        } catch (IllegalArgumentException ie) {
        	success = false;
            throw new IllegalArgumentException(ie.getMessage());
        } catch(Exception e) {
        	success = false;
        	throw new IllegalArgumentException("未知异常" + e.getMessage());
        }
        return resultMap;
	}
	
	
	public List<UserInfoModel> getAll() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		
		List<UserInfoModel> list = userInfoDao.getAll(new UserInfoModel());
		return list;
	}
	
	/**
	 * 查询用户翻页
	 * @param page
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public List<Map<String, Object>> queryUserListByPage(Page page,
			UserFilter userFilter, String sortdatafield, String sortorder)
			throws Exception {
		
		List<Map<String, Object>> list = userInfoDao.queryUserListByPage(page,userFilter,sortdatafield,sortorder);
		return list;
	}
	
	/**
	 * 登录验证查询
	 * @param userName
	 * @param password
	 * @return
	 */
	public UserInfoModel queryUserByNameAndPsd(String userName,String password){
		
		return userInfoDao.queryUserByNameAndPsd(userName, password);
	}
	
	/**
	 * 用户名查询用户
	 * 如果userId为不为空，则查询非自己的用户
	 * @param loginName	用户名或手机号
	 * @param password
	 * @return
	 */
	public UserInfoModel queryUserByLoginName(String loginName ,String userId){
		return userInfoDao.queryUserByLoginName(loginName,userId);
	}
	
	public CrewUserMapModel queryEffectiveCrewUserByuserId(String userId){
		return crewUserMapDao.queryEffectiveCrewUserByuserId(userId);
	}
	
	/**
	 * 查询用户剧组和角色
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	public CrewUserMapModel queryCrewUserBycrewId(String userId,String crewId) throws Exception{
		return this.crewUserMapDao.queryCrewUserBycrewId(userId, crewId);
	}
	
	/**
	 * 更新剧组用户关联关系
	 * @param crewUserMap
	 * @throws Exception 
	 */
	public void updateCrewUserMap (CrewUserMapModel crewUserMap) throws Exception {
		this.crewUserMapDao.update(crewUserMap, "mapId");
	}
	
	/**
	 * 更新用户信息
	 * @param userInfo
	 * @throws Exception 
	 */
	public void updateOne(UserInfoModel userInfo) throws Exception {
		this.userInfoDao.updateWithNull(userInfo, "userId");
	}
	
	/**
	 * 添加用户权限
	 */
	public void addUserAuth(String authIds,String userId,String roleId,String crewId){
		userInfoDao.addUserAuth(authIds, userId, roleId,crewId);
	}
	/**
	 * 查询用户所在剧组是否过期
	 */
	public Integer getCrewStatus(String crewId){
		return  userInfoDao.getCrewStatus(crewId);
	}
	
	/**
	 * 注册用户
	 * @param phone	手机号
	 * @param verifyCode	验证码
	 * @param password	密码（MD5密文）
	 * @param realName	姓名
	 * @param sex	性别
	 * @param token	
	 * @param clientType	客户端类型
	 * @param appVersion	版本号
	 * @return
	 * @throws Exception
	 */
	public UserInfoModel registerOneUser(String phone, String verifyCode, String password, String realName, 
			Integer sex, String token, Integer clientType, String appVersion) throws Exception {
		
		UserInfoModel userInfoModel = new UserInfoModel();
		userInfoModel.setUserId(UUIDUtils.getId());
		userInfoModel.setPassword(password);
		userInfoModel.setRealName(realName);
		userInfoModel.setType(UserType.CrewUser.getValue());
		userInfoModel.setPhone(phone);
		userInfoModel.setSex(sex);
		userInfoModel.setStatus(UserStatus.Valid.getValue());
		userInfoModel.setCreateTime(new Date());
		userInfoModel.setToken(token);
		userInfoModel.setClientType(clientType);
		userInfoModel.setAppVersion(appVersion);
		userInfoModel.setUbCreateCrewNum(0);
		userInfoModel.setAge(20);
		userInfoModel.setProfile("");
		
		this.userInfoDao.add(userInfoModel);
		
		this.verifyCodeInfoService.inValidPhoneCode(phone, VerifyCodeType.Register.getValue());
		
		return userInfoModel;
	}
	
	/**
	 * 修改用户手机号
	 * @param userInfo
	 * @param phone
	 * @throws Exception
	 */
	public void updateUserPhone(String userId, String phone) throws Exception {
		UserInfoModel userInfo = this.queryById(userId);
		userInfo.setPhone(phone);
		this.updateOne(userInfo);
		
		//设置用户验证码为无效
		this.verifyCodeInfoService.inValidPhoneCode(phone, VerifyCodeType.ModifyPhone.getValue());
	}
	
	/**
	 * 找回密码
	 * @param phone
	 * @param password	MD5密文
	 * @throws Exception 
	 */
	public void findbackPassword(String userId, String phone, String newPassword) throws Exception {
		//修改用户密码
		UserInfoModel userInfo = this.queryById(userId);
		userInfo.setPassword(newPassword);
		this.updateOne(userInfo);
		
		//设置用户验证码为无效
		this.verifyCodeInfoService.inValidPhoneCode(phone, VerifyCodeType.FindbackPassword.getValue());
	}
	
	/**
	 * 移动端找回密码
	 * 带有自动登录操作
	 * @param phone
	 * @param password
	 * @throws Exception 
	 */
	public Map<String, Object> mobileFindbackPassword(String userId, String phone, String newPassword, String token, Integer clientType, String appVersion) throws Exception {
		this.findbackPassword(userId, phone, newPassword);
		
		//执行自动登录操作
		Map<String, Object> loginInfo = this.mobileUserLogin(userId, clientType, token, appVersion);
		
		return loginInfo;
	}
	
	/**
	 * 手机端用户登录
	 * @throws Exception 
	 */
	public Map<String, Object> mobileUserLogin(String userId, Integer clientType, String token, String appVersion) throws Exception{
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		UserInfoModel userInfo = this.queryById(userId);
		
		//修改用户信息
		if (!StringUtils.isBlank(token) && clientType != null) {
			userInfo.setToken(token);
			userInfo.setClientType(clientType);
			if(!StringUtils.isBlank(appVersion)) {
				userInfo.setAppVersion(appVersion);
			}
			this.updateOne(userInfo);
		}
		
		//获取用户默认剧组
		CrewInfoModel crewInfo = this.crewInfoService.queryUserDefaultCrewForApp(userInfo.getUserId());
		String crewId = null;
		String crewName = null;
		Integer crewType = null;
		if (crewInfo != null) {
			crewId = crewInfo.getCrewId();
			crewName = crewInfo.getCrewName();
			crewType = crewInfo.getCrewType();
		}
		
		//获取用户关注的角色
		List<ViewRoleModel> focusRoleList = new ArrayList<ViewRoleModel>();
		if (!StringUtils.isBlank(crewId)) {
			focusRoleList = this.viewRoleService.queryUserFocusRoleInfo(crewId, userInfo.getUserId());
		}
		
		//用户头像地址
		String imgUrl = userInfo.getBigImgUrl();
		if (StringUtils.isBlank(imgUrl)) {
			Resource resource = new ClassPathResource("/config.properties");
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			String serverPath = (String) props.get("server.basepath");
			imgUrl = serverPath + Constants.DEFAULT_USER_PIC;
		} else {
			imgUrl = FileUtils.genPreviewPath(imgUrl);
		}
		
		
		//判断用户是否是客服
		boolean iskefu = false;
		if (userInfo.getType() == UserType.CustomerService.getValue()) {
			iskefu = true;
		}
		
		resultMap.put("userId", userInfo.getUserId());
		resultMap.put("crewId", crewId);
		resultMap.put("crewName", crewName);
		resultMap.put("focusRoleList", focusRoleList);
		resultMap.put("realName", userInfo.getRealName());
		resultMap.put("imgUrl", imgUrl);
		resultMap.put("crewType", crewType);
		resultMap.put("iskefu", iskefu);
		
		return resultMap;
	}
	
	/**
	 * 根据手机号查询用户
	 * @param phone
	 * @return
	 * @throws Exception 
	 */
	public UserInfoModel queryByPhone(String phone) throws Exception {
		return this.userInfoDao.queryByPhone(phone);
	}
	
	/**
	 * 申请加入指定剧组
	 * 向消息表中插入加入剧组消息
	 * @throws Exception 
	 */
	public void joinCrew(String userId, String crewId, String enterPassword, String roleIds, String roleNames, String remark) throws Exception{
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		
		/*
		 * 校验信息
		 */
		//校验剧组
		CrewInfoModel crewInfo = this.crewInfoDao.queryById(crewId);
		
		if (crewInfo == null) {
			throw new IllegalArgumentException("不存在的剧组");
		}
		Date startDate = crewInfo.getStartDate();
		Date endDate = crewInfo.getEndDate();
		Date nowDate = sdf2.parse(sdf2.format(new Date()));	//取当前日期，清除掉时分秒信息
		
		if (startDate != null && endDate != null && endDate.before(nowDate)) {
			throw new IllegalArgumentException("该剧组已过期，请联系管理员");
		}
		
		//校验入组密码
		String crewEnterPsd = crewInfo.getEnterPassword();
		if (!crewEnterPsd.equals(enterPassword)) {
			throw new IllegalArgumentException("入组密码错误");
		}
		
		//校验是否重复申请
		JoinCrewApplyMsgModel joinMsg = this.joinCrewApplyMsgDao.queryByCrewIdAndUserId(crewId, userId);
		if (joinMsg != null) {
			throw new IllegalArgumentException("您的上次申请正在审核中，请等待管理员审核");
		}
		
		//校验用户是否已经在该剧组中
		CrewUserMapModel crewUserMap = this.crewUserMapDao.queryCrewUserBycrewId(userId, crewId);
		if (crewUserMap != null) {
			throw new IllegalArgumentException("您已经在该剧组中了，不需要重新申请");
		}
		
		
		//向申请表中插入记录，web端需要从该表中读取消息，提示用户
		JoinCrewApplyMsgModel applyMsg = new JoinCrewApplyMsgModel();
		applyMsg.setId(UUIDUtils.getId());
		applyMsg.setApplyerId(userId);
		applyMsg.setAimCrewId(crewId);
		applyMsg.setAimRoleIds(roleIds);
		applyMsg.setAimRoleNames(roleNames);
		applyMsg.setStatus(JoinCrewAuditStatus.Auditing.getValue());
		applyMsg.setCreateTime(new Date());
		applyMsg.setLastModifyTime(new Date());
		applyMsg.setRemark(remark);
		this.joinCrewApplyMsgDao.add(applyMsg);
		
		try {
			UserInfoModel userInfo = this.userInfoDao.queryById(userId);
			
			List<String> authList = new ArrayList<String>();
			authList.add(AuthorityConstants.CREW_USER_MANAGE);
			authList.add(AuthorityConstants.PC_CREW_SETTING);
			
			//向剧组中拥有手机端成员管理权限和web端剧组设置权限的人推送消息
			List<Map<String, Object>> managerList = this.userInfoDao.queryUserByCrewIdAndAuth(crewId, authList, null);
			List<String> iosUserTokenList = new ArrayList<String>();
			List<String> androidTokenList = new ArrayList<String>();
			
			for (Map<String, Object> userInfoModel : managerList) {
				Integer clientType = (Integer) userInfoModel.get("clientType");
				String token = (String) userInfoModel.get("token");
				if (!StringUtils.isBlank(token) && clientType != null && clientType.intValue() == Constants.MOBILE_CLIENTTYPE_IPHONE.intValue()) {
					iosUserTokenList.add(token);
				}
				if (!StringUtils.isBlank(token) && clientType != null && clientType.intValue() == Constants.MOBILE_CLIENTTYPE_ANDROID.intValue()) {
					androidTokenList.add(token);
				}
				
				//保存用户的消息信息
				MessageInfoModel messageInfo = new MessageInfoModel();
				messageInfo.setId(UUIDUtils.getId());
				messageInfo.setCrewId(crewId);
				messageInfo.setSenderId(userId);
				messageInfo.setReceiverId((String) userInfoModel.get("userId"));
				messageInfo.setType(MessageType.ApplyJoinCrew.getValue());
				messageInfo.setStatus(MessageInfoStatus.UnRead.getValue());
				messageInfo.setTitle("人员入组申请");
				messageInfo.setContent(userInfo.getRealName() + "申请担当《" + crewInfo.getCrewName() + "》剧组" + roleNames + "职务");
				messageInfo.setRemindTime(new Date());
				messageInfo.setCreateTime(new Date());
				this.messageInfoService.addOne(messageInfo);
			}
			
			/*
			 * push消息
			 */
			String title = "人员入组申请";
			String pushMessage = userInfo.getRealName() + "申请担当《" + crewInfo.getCrewName() + "》剧组" + roleNames + "职务";
			
			String myTime = sdf1.format(new Date());
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("type", MessageType.ApplyJoinCrew.getValue());
			map.put("title", title);
			map.put("time", myTime);
			map.put("buzId", crewId);
			map.put("crewId", crewId);
			map.put("crewName", crewInfo.getCrewName());
			
			
			//IOS推送
			IOSPushMsg msg = new IOSPushMsg();
			msg.setTokenList(iosUserTokenList);
			msg.setAlert(pushMessage);
			msg.setCustomDictionaryMap(map);
			
			this.umengIOSPushService.iOSPushMsg(msg);
			
//			if (responseMap != null) {
//				boolean pushRespSuccess = (Boolean) responseMap.get("success");
//				String pushRespMessage = (String) responseMap.get("message");
//				
//				if (!pushRespSuccess) {
//					throw new IllegalArgumentException(pushRespMessage);
//				}
//			}
			
			
			//安卓推送
			AndroidPushMsg androidMsg = new AndroidPushMsg();
			androidMsg.setTokenList(androidTokenList);
			androidMsg.setTicker(pushMessage);
			androidMsg.setTitle(title);
			androidMsg.setText(pushMessage);
			androidMsg.setCustomDictionaryMap(map);
			this.umengAndroidPushService.androidPushMsg(androidMsg);
//			if (androidPushResponse != null) {
//				boolean pushRespSuccess = (Boolean) androidPushResponse.get("success");
//				String pushRespMessage = (String) androidPushResponse.get("message");
//				
//				if (!pushRespSuccess) {
//					throw new IllegalArgumentException(pushRespMessage);
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 查询当前剧组下所有生效的剧组成员
	 * @param crewId
	 * @return
	 */
	public List<UserInfoModel> queryValidUserListByCrewId(String crewId) {
		return this.userInfoDao.queryValidUserListByCrewId(crewId);
	}
	
	/**
	 * 查询当前剧组下非演员用户
	 * 该查询不仅查询出用户的基本信息，
	 * 还会查询用户的职位信息
	 * @param crewId
	 * @param userId 评价人ID
	 * @return
	 */
	public List<Map<String, Object>> queryNotRoleUserListbyCrewIdWithRole(String crewId) {
		return this.userInfoDao.queryNotActorUserListbyCrewIdWithRole(crewId);
	}
	
	/**
	 * 根据条件检索用户ee搜索
	 */
	public List<Map<String, Object>> searchAllUser(CrewUserFilter filter,
			Page page,String currentCrewId) {
		//String userid = this.getAllUserIdByCrewId(currentCrewId);
		return this.userInfoDao.searchAllUser(filter, page,currentCrewId);
	}
	
	/**
	 * 获取当前用户下剧组名称、剧组id
	 */
	public List<Map<String, Object>> queryCrewPartMes(String userId) {
		return this.userInfoDao.queryCrewPartMes(userId);
	}
	
	/**
	 * 切换剧组
	 */
	public void switchCrew(String userId,String crewId){
		this.crewUserMapDao.unDefaultUserCrew(userId);
		this.crewUserMapDao.defaultUserCrew(userId, crewId);
	}
	
	public String getPhoneByUserPhone (String phone,String userId) throws Exception{
		if(false != userInfoDao.validatePhone(phone,userId)){
			return "false";
		}
		return "true";
	}
	
	/**
	 * 获取用户所对应的剧组
	 */
	public List<Map<String,Object>> getCrewsByUserId(String userId){
		return this.userInfoDao.getCrewsByUserId(userId);
	}
	
	/**
	 * 根据用户ID查询用户信息
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	public UserInfoModel queryById(String userId) throws Exception{
		return this.userInfoDao.queryById(userId);
	}
	
	/**
	 * 查询剧组下有指定权限的用户
	 * @param crewId	剧组ID
	 * @param authList	权限编码列表
	 * @return
	 */
	public List<Map<String, Object>> queryUserByCrewIdAndAuth(String crewId, List<String> authList, Boolean readonly) {
		return this.userInfoDao.queryUserByCrewIdAndAuth(crewId, authList, readonly);
	}
	
	/**
	 * 根据用户登录名查询非自己的用于
	 * 该方法目前用于修改用户信息时检查是否有相同登录名的用户
	 * @param userId
	 * @param loginName
	 * @return
	 */
	public List<UserInfoModel> queryUserInfoByLoginNameExcepOwn(String userId, String loginName) {
		return this.userInfoDao.queryUserInfoByLoginNameExcepOwn(userId, loginName);
	}
	
	/**
	 * 根据多个用户ID查询用户列表，多个ID用英文逗号隔开
	 * @param userIds
	 * @return
	 */
	public List<UserInfoModel> queryByIds(String userIds) {
		return this.userInfoDao.queryByIds(userIds);
	}
	
	/**
	 * 删除用户剧组，主要用于删除客户服务，清空0剧组
	 * @param userId
	 * @throws Exception
	 */
	public void deleteCrewByUser(String userId) throws Exception {
		this.userInfoDao.deleteOne(userId, "userId", CrewUserMapModel.TABLE_NAME);
	}
	
	/**
	 * 删除用户，清除相关信息
	 * @param userId
	 * @throws Exception
	 */
	public void deleteUser(String userId) throws Exception{
		//删除用户角色关联关系，主要用于客服
		this.userInfoDao.deleteOne(userId, "userId", UserRoleMapModel.TABLE_NAME);
		//删除用户申请入组信息
		this.userInfoDao.deleteOne(userId, "applyerId", JoinCrewApplyMsgModel.TABLE_NAME);
		//删除用户登录日志
		this.userInfoDao.deleteUserLoginLogById(userId);
		//删除用户信息
		this.userInfoDao.deleteById(userId);
	}
	
	/**
	 * 查询用户所有剧组
	 */
	public List<CrewUserMapModel> queryUserAllcrew(String userId){
		return this.crewUserMapDao.queryByUserId(userId);
	}
	
	/**
	 * 查询用户所有未过期的状态有效的剧组
	 */
	public List<CrewUserMapModel> queryUserAllEffectiveCrew(String userId){
		return this.crewUserMapDao.queryUserAllEffectiveCrewMap(userId);
	}
	
	/**
	 * 查询指定分组下的用户信息及其职务信息
	 * 只返回状态为有效的用户
	 * 在剧组中被冻结的用户也需要返回
	 * 如果用户在剧组中有多个职务，则返回多条该用户记录
	 * @param crewId
	 * @param groupId
	 * @return
	 */
	public List<Map<String, Object>> queryByCrewGroupId(String crewId, String groupId) {
		return this.userInfoDao.queryByCrewGroupId(crewId, groupId);
	}
	
	/**
	 * 查询剧组下小组中的所有人员信息
	 * 比如《天龙九部》剧组下导演组的所有用户信息
	 * @param crewId
	 * @param groupId
	 * @return
	 */
	public List<UserInfoModel> queryCrewUserByGroupId(String crewId, String groupId) {
		return this.userInfoDao.queryCrewUserByGroupId(crewId, groupId);
	}
	
	/**
	 * 查询剧组下用户信息
	 * 该查询是按照部门职务的纬度查询用户信息
	 * 返回剧组中每个职务下的用户信息
	 * 如果用户在剧组中担任两个职务，则该查询将会返回两条用户记录
	 * @param crewId
	 * @param userId
	 * @return 用户所有基本信息、职务ID、职务名称、部门ID、部门名称
	 */
	public List<Map<String, Object>> queryCrewUserListWithRole (String crewId) {
		return this.userInfoDao.queryCrewUserListWithRole(crewId);
	}
	
	/**
	 * 查询剧组下拥有某权限的用户信息
	 * 该查询是按照部门职务的纬度查询用户信息
	 * 返回剧组中每个职务下的用户信息
	 * 如果用户在剧组中担任两个职务，则该查询将会返回两条用户记录
	 * 只返回状态为有效的用户
	 * @param crewId
	 * @param authId
	 * @return 用户所有基本信息、入组时间、职务ID、职务名称、部门ID、部门名称
	 */
	public List<Map<String, Object>> queryUserListWithRoleByAuthId (String crewId, String authId) {
		return this.userInfoDao.queryUserListWithRoleByAuthId(crewId, authId);
	}
	
	/**
	 * 根据手机号查询不在当前剧组中的用户
	 * @param crewId
	 * @param phone
	 * @param loginUserType
	 * @return
	 */
	public List<UserInfoModel> queryNotOwnUserByPhone(String crewId, String phone, Integer loginUserType) {
		return this.userInfoDao.queryNotOwnUserByPhone(crewId, phone, loginUserType);
	}
	
	/**
	 * 把用户加入到剧组中
	 * 带有推送操作
	 * @param crewId
	 * @param aimUserId	用户ID
	 * @param roleIds	职务ID，多个以逗号隔开
	 * @throws Exception 
	 */
	public void addUserToCrew(String crewId, String userId, String aimUserId, String roleIds) throws Exception {
		this.addUserToCrew(crewId, aimUserId, roleIds);
		
		UserInfoModel userInfo = this.userInfoDao.queryById(userId);
		UserInfoModel aimUserInfo = this.userInfoDao.queryById(aimUserId);
		CrewInfoModel crewInfo = this.crewInfoService.queryById(crewId);
		//普通用户发送推送消息
		if (aimUserInfo.getType() == 0 && !StringUtils.isBlank(aimUserInfo.getToken())) {
			String title = "入组消息";
			String pushMessage = "您已被"+ userInfo.getRealName() +"加入到《" + crewInfo.getCrewName() + "》剧组";
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String myTime = sdf.format(new Date());
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("type", MessageType.BeAddedToCrew.getValue());
			map.put("title", title);
			map.put("time", myTime);
			map.put("buzId", crewId);
			map.put("crewId", crewId);
			map.put("crewName", crewInfo.getCrewName());
			
			List<String> tokenList = new ArrayList<String>();
			tokenList.add(aimUserInfo.getToken());
			
			//IOS推送
			if (aimUserInfo.getClientType() == UserClientType.IOS.getValue()) {
				IOSPushMsg msg = new IOSPushMsg();
				msg.setTokenList(tokenList);
				msg.setAlert(pushMessage);
				msg.setCustomDictionaryMap(map);
				
				this.umengIOSPushService.iOSPushMsg(msg);
				
//				if (responseMap != null) {
//					boolean pushRespSuccess = (Boolean) responseMap.get("success");
//					String pushRespMessage = (String) responseMap.get("message");
//					
//					if (!pushRespSuccess) {
//						throw new IllegalArgumentException(pushRespMessage);
//					}
//				}
			}
			
			//安卓推送
			if (aimUserInfo.getClientType() == UserClientType.Android.getValue()) {
				AndroidPushMsg androidMsg = new AndroidPushMsg();
				androidMsg.setTokenList(tokenList);
				androidMsg.setTicker(pushMessage);
				androidMsg.setTitle(title);
				androidMsg.setText(pushMessage);
				androidMsg.setCustomDictionaryMap(map);
				this.umengAndroidPushService.androidPushMsg(androidMsg);
//				if (androidPushResponse != null) {
//					boolean pushRespSuccess = (Boolean) androidPushResponse.get("success");
//					String pushRespMessage = (String) androidPushResponse.get("message");
//					
//					if (!pushRespSuccess) {
//						throw new IllegalArgumentException(pushRespMessage);
//					}
//				}
			}
			
			//保存用户的消息信息
			MessageInfoModel messageInfo = new MessageInfoModel();
			messageInfo.setId(UUIDUtils.getId());
			messageInfo.setCrewId(crewId);
			messageInfo.setSenderId(userId);
			messageInfo.setReceiverId(aimUserId);
			messageInfo.setType(MessageType.BeAddedToCrew.getValue());
			messageInfo.setStatus(MessageInfoStatus.UnRead.getValue());
			messageInfo.setTitle(title);
			messageInfo.setContent(pushMessage);
			messageInfo.setRemindTime(new Date());
			messageInfo.setCreateTime(new Date());
			this.messageInfoService.addOne(messageInfo);
		}
	}
	
	/**
	 * 把用户设置为客户服务
	 * 带有推送操作
	 * @param crewId
	 * @param aimUserId	用户ID
	 * @throws Exception 
	 */
	public void setCustomerService(String crewId, String userId, String aimUserId) throws Exception {
		//建立用户和剧组的关联关系
		CrewUserMapModel crewUserMap = new CrewUserMapModel();
		crewUserMap.setCrewId(crewId);
		crewUserMap.setIfDefault(true);
		crewUserMap.setMapId(UUIDUtils.getId());
		crewUserMap.setRoleId(CrewUserType.CustomerService.getValue() + "");
		crewUserMap.setStatus(CrewUserStatus.Normal.getValue());
		crewUserMap.setUserId(aimUserId);
		crewUserMap.setCreateTime(new Date());		
		crewUserMap.setType(CrewUserType.NormalUser.getValue());
		this.crewUserMapDao.add(crewUserMap);
		
		UserInfoModel userInfo = this.userInfoDao.queryById(userId);
		UserInfoModel aimUserInfo = this.userInfoDao.queryById(aimUserId);
		CrewInfoModel crewInfo = this.crewInfoService.queryById(crewId);
		if (!StringUtils.isBlank(aimUserInfo.getToken())) {
			String title = "用户类型改变消息";
			String pushMessage = "您已被"+ userInfo.getRealName() +"设置为客户服务";
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String myTime = sdf.format(new Date());
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("type", MessageType.BeAddedToCrew.getValue());
			map.put("title", title);
			map.put("time", myTime);
			map.put("buzId", crewId);
			map.put("crewId", crewId);
			map.put("crewName", crewInfo.getCrewName());
			
			List<String> tokenList = new ArrayList<String>();
			tokenList.add(aimUserInfo.getToken());
			
			//IOS推送
			if (aimUserInfo.getClientType() == UserClientType.IOS.getValue()) {
				IOSPushMsg msg = new IOSPushMsg();
				msg.setTokenList(tokenList);
				msg.setAlert(pushMessage);
				msg.setCustomDictionaryMap(map);
				
				this.umengIOSPushService.iOSPushMsg(msg);
				
//				if (responseMap != null) {
//					boolean pushRespSuccess = (Boolean) responseMap.get("success");
//					String pushRespMessage = (String) responseMap.get("message");
//					
//					if (!pushRespSuccess) {
//						throw new IllegalArgumentException(pushRespMessage);
//					}
//				}
			}
			
			//安卓推送
			if (aimUserInfo.getClientType() == UserClientType.Android.getValue()) {
				AndroidPushMsg androidMsg = new AndroidPushMsg();
				androidMsg.setTokenList(tokenList);
				androidMsg.setTicker(pushMessage);
				androidMsg.setTitle(title);
				androidMsg.setText(pushMessage);
				androidMsg.setCustomDictionaryMap(map);
				this.umengAndroidPushService.androidPushMsg(androidMsg);
//				if (androidPushResponse != null) {
//					boolean pushRespSuccess = (Boolean) androidPushResponse.get("success");
//					String pushRespMessage = (String) androidPushResponse.get("message");
//					
//					if (!pushRespSuccess) {
//						throw new IllegalArgumentException(pushRespMessage);
//					}
//				}
			}
			
			//保存用户的消息信息
			MessageInfoModel messageInfo = new MessageInfoModel();
			messageInfo.setId(UUIDUtils.getId());
			messageInfo.setCrewId(crewId);
			messageInfo.setSenderId(userId);
			messageInfo.setReceiverId(aimUserId);
			messageInfo.setType(MessageType.BeAddedToCrew.getValue());
			messageInfo.setStatus(MessageInfoStatus.UnRead.getValue());
			messageInfo.setTitle(title);
			messageInfo.setContent(pushMessage);
			messageInfo.setRemindTime(new Date());
			messageInfo.setCreateTime(new Date());
			this.messageInfoService.addOne(messageInfo);
		}
	}
	
	/**
	 * 把用户加入到剧组中
	 * @param crewId
	 * @param aimUserId	用户ID
	 * @param roleIds	职务ID，多个以逗号隔开
	 * @throws Exception 
	 */
	public void addUserToCrew(String crewId, String aimUserId, String roleIds) throws Exception {
		UserInfoModel aimUserInfo = this.userInfoDao.queryById(aimUserId);
		
		//建立用户和剧组的关联
		CrewUserMapModel crewUserMap = new CrewUserMapModel();
		crewUserMap.setCrewId(crewId);
		crewUserMap.setIfDefault(false);
		crewUserMap.setMapId(UUIDUtils.getId());
		crewUserMap.setRoleId(roleIds);
		crewUserMap.setStatus(CrewUserStatus.Normal.getValue());
		crewUserMap.setUserId(aimUserId);
		crewUserMap.setCreateTime(new Date());
		//用户类型。0：普通用户；1：剧组管理员
		crewUserMap.setType(CrewUserType.NormalUser.getValue());
		this.crewUserMapDao.add(crewUserMap);
		//客服，不需要添加职务信息和权限信息
		if(aimUserInfo.getType() != 2) {
			//把职务以及职务对应的权限分配给用户
			String[] roleIdArray = roleIds.split(",");
			for (String roleId : roleIdArray) {
				UserRoleMapModel userRoleMap = new UserRoleMapModel();
				userRoleMap.setMapId(UUIDUtils.getId());
				userRoleMap.setUserId(aimUserId);
				userRoleMap.setRoleId(roleId);
				userRoleMap.setCrewId(crewId);
				
				this.userRoleMapDao.add(userRoleMap);
				
				this.userAuthMapDao.addByRoleId(crewId, aimUserId, roleId);
			}
		}
		
		//普通用户
		if(aimUserInfo.getType() == 0) {
			//同步用户到剧组联系表
			this.crewContactService.syncFromUserInfo(crewId, aimUserInfo);
		}
	}
	
	/**
	 * 查询剧组下的用户信息
	 * 剧组是未过期的、未停用的，用户是有效状态的，用户在剧组中是未冻结的
	 * @param crewId
	 * @param userId
	 * @return
	 */
	public List<UserInfoModel> queryByCrewUserId(String crewId, String userId) {
		return this.userInfoDao.queryByCrewUserId(crewId, userId);
	}
	
	/**
	 * 根据手机号和密码查询用户
	 * @param phone
	 * @param password
	 * @return
	 * @throws Exception 
	 */
	public UserInfoModel queryByPhoneAndPwd(String phone, String password) throws Exception {
		return this.userInfoDao.queryByPhoneAndPwd(phone, password);
	}
	
	/**
	 * 清空用户token
	 * @param userId
	 */
	public void clearUserToken (String userId) {
		this.userInfoDao.clearUserToken(userId);
	}
	
	/**
	 * 根据用户姓名查询剧组中非自己的用户
	 * 该方法目前用于修改用户信息时检查是否有相同登录名的用户
	 * @param userId
	 * @param loginName
	 * @return
	 */
	public List<UserInfoModel> queryUserInfoByRealNameExcepOwn(String crewId, String userId, String realName) {
		return this.userInfoDao.queryUserInfoByRealNameExcepOwn(crewId, userId, realName);
	}
	
	/**
	 * 新增用户IP地址
	 * @param userId
	 * @param userIp
	 */
	public void addUserIp(String userId, String userIp) {
		this.userInfoDao.addUserIp(userId, userIp);
	}
	
	/**
	 * 添加用户IP地址
	 * @param userIp
	 */
	public void updateUserIp(String userId, String userIp) {
		this.userInfoDao.updateUserIp(userId, userIp);
	}
	
	/**
	 * 检查是否是客服（包含总客服，高级、中级、低级客服）
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	public boolean checkIsKefu(String userId) throws Exception {
		boolean isKefu = false;
		
		UserInfoModel userInfo = this.userInfoDao.queryById(userId);
		if (userInfo.getType() == UserType.CustomerService.getValue()) {
			isKefu = true;
		}
		
		return isKefu;
	}
	
	/**
	 * 删除客服用户与剧组关联关系
	 * @param userId
	 * @param crewId
	 */
	public void deleteCrewUserMap(String userId, String crewId) {
		//删除用户角色关联关系
//		this.userRoleMapDao.deleteUserRoleMap(crewId, userId);
		//删除剧组用户关联关系
		this.crewUserMapDao.deleteCrewUserMap(crewId, userId);
	}
}
