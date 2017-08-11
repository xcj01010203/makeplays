package com.xiaotu.makeplays.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.user.dao.UserFocusRoleMapDao;
import com.xiaotu.makeplays.user.model.UserFocusRoleMapModel;

@Service
public class UserFocusRoleMapService {

	@Autowired
	private UserFocusRoleMapDao userFocusRoleMapDao;
	
	/**
	 * 删除用户关联的所有角色
	 * @param userId
	 */
	public void deleteByUserId(String userId) {
		this.userFocusRoleMapDao.deleteByUserId(userId);
	}
	
	/**
	 * 批量新增关联关系
	 * @param mapList
	 * @throws Exception 
	 */
	public void addMany(String userId, List<UserFocusRoleMapModel> mapList) throws Exception {
		this.userFocusRoleMapDao.deleteByUserId(userId);
		for (UserFocusRoleMapModel map : mapList) {
			this.userFocusRoleMapDao.add(map);
		}
	}
}
