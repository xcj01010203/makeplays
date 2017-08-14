package com.xiaotu.makeplays.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.authority.model.AuthorityModel;
import com.xiaotu.makeplays.authority.model.constants.AuthorityPlatform;
import com.xiaotu.makeplays.authority.service.AuthorityService;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.StringUtil;

/**
 * @类名：UserAuthFilterService.java
 * @作者：李晓平
 * @时间：2017年8月8日 下午5:28:31
 * @描述：用户权限过滤service
 */
@Service
public class UserAuthFilterService {
	
	@Autowired
	private AuthorityService authorityService;

	//当前用户的功能权限List，保存当前用户的所有功能权限信息
	private static Map<String,List<Map<String, Object>>> userAuthMap = new HashMap<String, List<Map<String,Object>>>();
	//保存所有已注册的权限
	private static List<AuthorityModel> allAuthList = new ArrayList<AuthorityModel>();
		
	/**
	 * 根据用户名称，将其权限信息放入缓存中，并将所有已注册的权限信息放入缓存
	 * @param user 用户实体
	 * @throws Exception 
	 */
	public void setFuncPermitList(String userId, String crewId, int loginUserType, String roleId) throws Exception {
		//用户有效权限信息
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		if (loginUserType == 1) {//系统管理员、客服、总客服
			list = this.authorityService.getAllAuthByRoleId("0");
		} else if (loginUserType == 2 || loginUserType == 4) {
			list = this.authorityService.getAllAuthByRoleId(roleId);
		} else if (loginUserType == 3) {
			list = this.authorityService.queryEffectiveAuthByUserAndPlantform(crewId, userId, AuthorityPlatform.PC.getValue());			
		}
		userAuthMap.put(crewId + userId, list);
		allAuthList = this.authorityService.queryAuthorityList(AuthorityPlatform.PC.getValue());
	}
	
	/**
	 * 根据请求的url，判断当前用户是否对其有访问权
	 * @param crewId 剧组ID
	 * @param userId 用户ID
	 * @param url 请求的URL
	 * @param path 请求的参数path
	 * @return 如果有访问权限返回真，否则返回假。
	 **/
	public static boolean checkAuthControl (String crewId, String userId, String url, String path) {
		boolean flag = false;
		List<Map<String, Object>> userAuthList = userAuthMap.get(crewId + userId);
		if(userAuthList != null && userAuthList.size() > 0) {
			for(Map<String, Object> one : userAuthList) {
				if(one != null && !one.isEmpty()) {
					String authUrl = (String) one.get("authUrl");
					if(authUrl != null) {
						if(StringUtil.isNotBlank(url) && StringUtil.isNotBlank(path)) {
							if(authUrl.contains(url) && authUrl.contains(path)) {
								flag = true;
								break;
							}
						} else if(StringUtil.isNotBlank(url) && StringUtil.isBlank(path)) {
							if(authUrl.contains(url)) {
								flag = true;
								break;
							}
						}
					}
				}
			}
		}
		return flag;
	}
	
	/**
	 * 根据功能权限URL，判断是否该权限已经注册
	 * @param requestUrl 请求的URL。
	 * @return 如果已注册返回真，否则返回假。
	 */
	public static boolean checkAuthRegistered(String url, String path) {		
		boolean flag = false;
		if(allAuthList != null && allAuthList.size() > 0) {
			for(AuthorityModel one : allAuthList) {
				if(one != null) {
					String authUrl = (String) one.getAuthUrl();
					if(authUrl != null) {
						if(StringUtil.isNotBlank(url) && StringUtil.isNotBlank(path)) {
							if(authUrl.contains(url) && authUrl.contains(path)) {
								flag = true;
								break;
							}
						} else if(StringUtil.isNotBlank(url) && StringUtil.isBlank(path)) {
							if(authUrl.contains(url)) {
								flag = true;
								break;
							}
						}
					}
				}
			}
		}		
		return flag;
	}
}