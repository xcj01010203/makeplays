package com.xiaotu.makeplays.utils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.sys.service.SysLogService;
import com.xiaotu.makeplays.user.model.UserInfoModel;

public class BaseController {

	@Autowired
	public SysLogService sysLogService;
	

	/**
	 * 从session中获取剧组ID
	 * @param request
	 * @return
	 */
	public String getCrewId(HttpServletRequest request) {
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		String crewId = "";
		if (crewInfo != null) {
			crewId = crewInfo.getCrewId();
		}
		return crewId;
	}
	
	/**
	 * 从session中获取剧组类型
	 * @param request
	 * @return
	 */
	public Integer getSessionCrewType(HttpServletRequest request) {
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		Integer crewType = null;
		if (crewInfo != null) {
			crewType = crewInfo.getCrewType();
		}
		return crewType;
	}
	
	/**
	 * 获取session中剧组信息
	 * @param request
	 * @return
	 */
	public CrewInfoModel getSessionCrewInfo(HttpServletRequest request) {
		CrewInfoModel crewInfo = (CrewInfoModel) request.getSession().getAttribute(Constants.SESSION_CREW_INFO);
		return crewInfo;
	}
	
	/**
	 * 获取session中的用户信息
	 * @param request
	 * @return
	 */
	public UserInfoModel getSessionUserInfo (HttpServletRequest request) {
		UserInfoModel userInfo = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);
		return userInfo;
	}
	
	/**
	 * 获取当前登录人的ID
	 * @param request
	 * @return
	 */
	public String getLoginUserId(HttpServletRequest request) {
		UserInfoModel userInfo = this.getSessionUserInfo(request);
		return userInfo.getUserId();
	}
	
	/**
	 * 获取session中的用户类型
	 * 1-系统管理员  2-客服  3-普通剧组用户
	 * @param request
	 * @return
	 */
	public int getSessionUserType (HttpServletRequest request) {
		int userType = (Integer) request.getSession().getAttribute(Constants.SESSION_LOGIN_USER_TYPE);
		return userType;
	}
	
}
