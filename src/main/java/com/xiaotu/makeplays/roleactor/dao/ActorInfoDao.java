package com.xiaotu.makeplays.roleactor.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.roleactor.model.ActorInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 演员信息
 * @author xuchangjian 2016-7-12下午4:33:34
 */
@Repository
public class ActorInfoDao extends BaseDao<ActorInfoModel> {

	/**
	 * 根据ID查询演员信息
	 * @param actorId
	 * @return
	 * @throws Exception
	 */
	public ActorInfoModel queryById(String actorId) throws Exception {
		String sql = "select * from tab_actor_info where actorId = ?";
		return this.queryForObject(sql, new Object[] {actorId}, ActorInfoModel.class);
	}
	
	/**
	 * 查询场景角色ID查询对应的演员
	 * @param viewRoleId	场景角色ID
	 * @return
	 * @throws Exception 
	 */
	public ActorInfoModel queryByViewRoleId(String viewRoleId) throws Exception {
		String sql = "select * from tab_actor_info tai, tab_actor_role_map tarm where tai.actorId = tarm.actorId and tarm.viewRoleId = ?";
		return this.queryForObject(sql, new Object[] {viewRoleId}, ActorInfoModel.class);
	}
	
	/**
	 * 根据角色ID删除角色对应的演员信息
	 * 多个ID用逗号隔开
	 * @param viewRoleIds
	 */
	public void deleteByViewRoleIds(String viewRoleIds) {
		viewRoleIds = "'" + viewRoleIds.replace(",", "','") + "'";
		String sql = "delete from tab_actor_info where actorId in(select actorId from tab_actor_role_map where viewRoleId in ("+ viewRoleIds +"));";
		
		this.getJdbcTemplate().update(sql);
	}
	
	/**
	 * 查询所有的演员信息
	 * @param crewId
	 * @param roleType	演员类型（可为空）
	 * @param userId	评价人ID
	 * @return	演员信息，角色信息，角色戏量
	 */
	public List<Map<String, Object>> queryViewRoleActorInfo(String crewId, Integer roleType){
		List<Object> conditionList = new ArrayList<Object>();
		
		StringBuilder sql = new StringBuilder();
		sql.append(" 		SELECT ");
		sql.append(" 			tsr.viewRoleName, ");
		sql.append(" 			tsr.crewId, ");
		sql.append(" 			tsr.shortName, ");
		sql.append(" 			tsr.viewRoleId, ");
		sql.append(" 			tsr.viewRoleType, ");
		sql.append(" 			tai.actorId, ");
		sql.append(" 			tai.actorName, ");
		sql.append("            count(tvrm.mapId) viewRoleCount");
		sql.append(" 		FROM ");
		sql.append(" 			tab_view_role tsr LEFT JOIN tab_view_role_map tvrm ON tsr.viewRoleId = tvrm.viewRoleId, ");
		sql.append(" 		tab_actor_role_map tarm, ");
		sql.append(" 		tab_actor_info tai ");
		sql.append(" 			WHERE 1=1");
		if (roleType != null) {
			sql.append(" 	AND	tsr.viewRoleType = ? ");
			conditionList.add(roleType);
		}
		sql.append(" 	AND tsr.crewId = ? ");
		sql.append(" 	AND tarm.viewRoleId = tsr.viewRoleId ");
		sql.append(" 	AND tai.actorId = tarm.actorId ");
		sql.append(" 	AND tsr.viewRoleId = tsr.viewRoleId ");
		sql.append("    GROUP BY tsr.viewRoleName, tsr.crewId, tsr.shortName, tsr.viewRoleId, tsr.viewRoleType, tai.actorId, tai.actorName");
		sql.append("    ORDER BY viewRoleCount desc");
		conditionList.add(crewId);
		
		return this.query(sql.toString(), conditionList.toArray(), null);
	}
}
