package com.xiaotu.makeplays.user.dao;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.user.model.UserFocusRoleMapModel;
import com.xiaotu.makeplays.utils.BaseDao;

@Repository
public class UserFocusRoleMapDao extends BaseDao<UserFocusRoleMapModel> {

	/**
	 * 删除用户关联的所有角色
	 * @param userId
	 */
	public void deleteByUserId(String userId) {
		String sql = "delete from " + UserFocusRoleMapModel.TABLE_NAME + " where userId = ?";
		this.getJdbcTemplate().update(sql, new Object[] {userId});
	}
}
