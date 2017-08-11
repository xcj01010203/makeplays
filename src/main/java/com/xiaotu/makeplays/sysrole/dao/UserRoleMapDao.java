package com.xiaotu.makeplays.sysrole.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.sysrole.model.UserRoleMapModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 用户和系统角色关联关系
 * @author xuchangjian 2016-5-12下午4:37:26
 */
@Repository
public class UserRoleMapDao extends BaseDao<UserRoleMapModel> {

	/**
	 * 查询用户和指定权限的关联关系
	 * @return
	 * @throws Exception 
	 */
	public UserRoleMapModel queryByUserRoleId(String crewId, String userId, String roleId) throws Exception {
		String sql = "select * from tab_user_role_map where crewId = ? and userId = ? and roleId = ?";
		return this.queryForObject(sql, new Object[] {crewId, userId, roleId}, UserRoleMapModel.class);
	}
	
	/**
	 * 查询用户在剧组中的职务
	 * @param crewId
	 * @param userId
	 * @return
	 */
	public List<UserRoleMapModel> queryByUserId(String crewId, String userId) {
		String sql = "select * from tab_user_role_map where crewId = ? and userId = ?";
		return this.query(sql, new Object[] {crewId, userId}, UserRoleMapModel.class, null);
	}
	
	/**
	 * 批量修改用户角色信息
	 * @param roleIds
	 * @param actorIds
	 * @return
	 * @throws Exception
	 */
	
	public Integer updateUserRoleMap(String viewRoleIds,String crewId,String newViewRoleId)throws SQLException{
		viewRoleIds = "'" + viewRoleIds.replace(",", "','") + "'";
		
		String sql="update tab_crewRole_user_map SET viewRoleId = ? WHERE crewId = ? AND viewRoleId in ("+ viewRoleIds +")";
		return this.getJdbcTemplate().update(sql, new Object[]{newViewRoleId, crewId});
	}
	
	/**
	 * 查询所有的角色用户关联关系
	 * @return
	 */
	public List<Map<String, Object>> queryAllRoleUserMap() {
		String sql = "select tsi.roleId,turm.userId,turm.crewId " 
				+ " from tab_sysrole_info tsi left join tab_user_role_map turm " 
				+ " ON tsi.roleId = turm.roleId where turm.userId is not null";
		return this.query(sql, null, null);
	}
	
	/**
	 * 查询所有的用户角色关联关系
	 * @return
	 */
	public List<Map<String, Object>> queryAllUserRoleMap() {
		String sql = "select tui.userId,turm.roleId,turm.crewId " 
				+ " from tab_user_info tui left join tab_user_role_map turm " 
				+ " ON tui.userId = turm.userId where turm.roleId is not null";
		return this.query(sql, null, null);
	}
	
	/**
	 * 查询用户所有的用户角色关联关系
	 * @param userId
	 */
	public List<Map<String, Object>> queryAllRoleIdsByUserId(String userId) {
		String sql = "select tsi.roleId,turm.userId,turm.crewId " 
				+ " from tab_sysrole_info tsi left join tab_user_role_map turm " 
				+ " ON tsi.roleId = turm.roleId where turm.userId is not null and turm.userId=?";
		return this.query(sql, new Object[]{userId}, null);
	}
		
	/**
	 * 更新客服在剧组中的角色
	 * @param customerServiceId
	 * @param roleId
	 */
	public void updateRoleIdByCustomerService(String customerServiceId, String roleId) {
		String sql = "update tab_user_role_map set roleId=? where userId = ? ";
		this.getJdbcTemplate().update(sql, new Object[]{roleId, customerServiceId});
	}
	
	/**
	 * 删除用户和角色关联
	 * @param crewId
	 * @param userId
	 */
	public void deleteUserRoleMap(String crewId, String userId) {
		String sql = "DELETE FROM " + UserRoleMapModel.TABLE_NAME + " WHERE crewId = ? AND userId = ?";
		this.getJdbcTemplate().update(sql, new Object[]{crewId, userId});
	}
}
