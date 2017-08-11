package com.xiaotu.makeplays.crew.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.crew.model.CrewRoleUserMapModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 场景角色与用户关联
 * @author xuchangjian 2016-5-27上午10:40:07
 */
@Repository
public class CrewRoleUserMapDao extends BaseDao<CrewRoleUserMapModel> {

	/**
	 * 根据剧组、用户ID查询关联关系
	 * @param crewId
	 * @param userId
	 * @return
	 */
	public List<CrewRoleUserMapModel> queryByCrewUserId (String crewId, String userId) {
		String sql = "select * from tab_crewRole_user_map where crewId = ? and userId = ?";
		return this.query(sql, new Object[] {crewId, userId}, CrewRoleUserMapModel.class, null);
	}
	
	/**
	 * 查询用户和指定场景角色的关联关系
	 * @param crewId
	 * @param userId
	 * @param viewRoleId
	 * @return
	 * @throws Exception
	 */
	public CrewRoleUserMapModel queryByCrewUserRoleId(String crewId, String userId, String viewRoleId) throws Exception {
		String sql = "select * from tab_crewRole_user_map where crewId = ? and userId = ? and viewRoleId = ?";
		return this.queryForObject(sql, new Object[] {crewId, userId, viewRoleId}, CrewRoleUserMapModel.class);
	}
	
	/**
	 * 取消用户在剧组中与场景角色的所有关联
	 * @param crewId
	 * @param userId
	 */
	public void deleteByCrewUserId(String crewId, String userId) {
		String sql = "delete from tab_crewRole_user_map where crewId = ? and userId = ?";
		this.getJdbcTemplate().update(sql, new Object[] {crewId, userId});
	}
}
