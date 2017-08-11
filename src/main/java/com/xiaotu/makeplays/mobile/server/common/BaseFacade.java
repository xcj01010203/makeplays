package com.xiaotu.makeplays.mobile.server.common;

import org.springframework.beans.factory.annotation.Autowired;

import com.xiaotu.makeplays.sys.service.SysLogService;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.StringUtil;

public class BaseFacade {

	@Autowired
	public SysLogService sysLogService;
	
	@Autowired
	private UserService userService;
	
	/**
	 * 获取app类型
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public Integer getClientType(String userId)  {
		UserInfoModel user;
		try {
			user = userService.queryById(userId);

			if(user != null) {
				if(StringUtil.isNotBlank(user.getClientType() + "")) {
					return user.getClientType() == 1 ? Constants.TERMINAL_IOS : Constants.TERMINAL_ANDROID;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
