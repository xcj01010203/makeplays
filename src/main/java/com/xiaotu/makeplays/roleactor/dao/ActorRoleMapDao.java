package com.xiaotu.makeplays.roleactor.dao;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.roleactor.model.ActorRoleMapModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 演员与场景角色关联信息
 * @author xuchangjian 2016-7-18下午2:35:58
 */
@Repository
public class ActorRoleMapDao extends BaseDao<ActorRoleMapModel> {

	/**
	 * 根据场景角色ID查询和演员的关联关系
	 * @param viewRoleId	场景角色ID
	 * @return
	 * @throws Exception 
	 */
	public ActorRoleMapModel queryByViewRoleId(String viewRoleId) throws Exception {
		String sql = "select * from tab_actor_role_map where viewRoleId = ?";
		return this.queryForObject(sql, new Object[] {viewRoleId}, ActorRoleMapModel.class);
	}
	
	/**
	 * 根据角色ID删除角色对应的演员信息
	 * 多个ID用逗号隔开
	 * @param viewRoleIds
	 */
	public void deleteByViewRoleIds(String viewRoleIds) {
		viewRoleIds = "'" + viewRoleIds.replace(",", "','") + "'";
		String sql = "delete from tab_actor_role_map where viewRoleId in("+ viewRoleIds +");";
		
		this.getJdbcTemplate().update(sql);
	}
}
