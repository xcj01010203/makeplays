package com.xiaotu.makeplays.mobile.common.utils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.mobile.common.MobileRequest;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.model.constants.UserStatus;
import com.xiaotu.makeplays.user.model.constants.UserType;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.GsonUtils;
import com.xiaotu.makeplays.utils.SpringContextUtil;

public class MobileUtils {

	/**
	 * 校验用户是否有效工具方法 如果无效，以异常的形式抛出
	 * 校验用户自身有效无效
	 * @param userId
	 * @throws Exception 
	 */
	public static UserInfoModel checkUserValid(String userId) throws Exception {
		UserService userService = SpringContextUtil.getBean(Constants.USERSERVICE);
		
		if (StringUtils.isBlank(userId)) {
			throw new IllegalArgumentException("无效的用户访问");
		}
		
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("userId", userId);
		conditionMap.put("status", UserStatus.Valid.getValue());
		List<UserInfoModel> userList = userService.queryManyByMutiCondition(conditionMap, null);

		if (userList == null || userList.size() == 0) {
			throw new IllegalArgumentException("无效的用户访问");
		}
		
		UserInfoModel loginUserInfo = userList.get(0);
		return loginUserInfo;
	}
	
	/**
	 * 校验用户是否有效工具方法 如果无效，以异常的形式抛出
	 * 校验用户是否自身有效无效，是否在指定的剧组中，在剧组中是否未被冻结，所在剧组是否是未过期
	 * @param crewId
	 * @param userId
	 * @throws Exception 
	 */
	public static UserInfoModel checkCrewUserValid(String crewId, String userId) throws Exception {
		UserService userService = SpringContextUtil.getBean(Constants.USERSERVICE);

		UserInfoModel loginUserInfo = userService.queryById(userId);
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(crewId) || loginUserInfo == null 
				|| loginUserInfo.getStatus() == UserStatus.Invalid.getValue()) {
			throw new IllegalArgumentException("无效的用户访问");
		}
		
		List<UserInfoModel> userList = userService.queryByCrewUserId(crewId, userId);
		
		boolean isBigKefu = loginUserInfo.getType() == UserType.CustomerService.getValue();
		if (!isBigKefu && (userList == null || userList.size() == 0)) {
			throw new IllegalArgumentException("无效的用户访问");
		}
		return loginUserInfo;
	}
	
	/**
	 * 移动端用json形式请求接口时，校验请求参数的充分必要性
	 * 校验失败以异常的形式抛出
	 * 如果校验成功，则返回json待转换成的对象
	 * @param requestJson
	 * @param type
	 * @throws Exception 
	 */
	public static <T> MobileRequest<T> processRequest(String requestJson, Type type) throws Exception {
		UserService userService = SpringContextUtil.getBean(Constants.USERSERVICE);
		CrewInfoService crewInfoService = SpringContextUtil.getBean(Constants.CREWSERVICE);
		
		MobileRequest<T> request = (MobileRequest<T>) GsonUtils.fromJson(requestJson, type);
		String crewId = request.getCrewId();
		String userId = request.getUserId();
		
		if (StringUtils.isBlank(crewId) || StringUtils.isBlank(userId)) {
			throw new IllegalArgumentException("无效的用户访问");
		}
		
		List<UserInfoModel> userList = null;
		if (!StringUtils.isBlank(userId)) {
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("userId", userId);
			conditionMap.put("status", UserStatus.Valid.getValue());
			userList = userService.queryManyByMutiCondition(conditionMap, null);
		}
		
		if (userList == null || userList.size() == 0) {
			throw new IllegalArgumentException("无效的用户访问");
		}
		
		CrewInfoModel crewInfo = crewInfoService.queryById(crewId);
		if (crewInfo == null) {
			throw new IllegalArgumentException("剧组无效");
		}
		return request;
	}
}
