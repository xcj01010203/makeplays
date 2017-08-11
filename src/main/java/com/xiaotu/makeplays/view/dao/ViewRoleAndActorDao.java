package com.xiaotu.makeplays.view.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.view.model.ViewRoleAndActorModel;

/**
 * 场景角色信息
 */
@Deprecated
@Repository
public class ViewRoleAndActorDao extends BaseDao<ViewRoleAndActorModel> {

	/**
	 * 查询所有演员，并根据类型,去除重复数据
	 * @return
	 */
	public List<ViewRoleAndActorModel> queryViewRoleListViewIdSign(String viewIds,Integer roleType){
		
		if(StringUtils.isBlank(viewIds)){
			return null;
		}
		
		String sql = "select res.* from (select tsr.viewRoleName,tsr.crewId,tsr.shortName,tsr.viewRoleId,tsr.viewRoleType,tai.actorId,tai.actorName,tsrm.roleNum,count(tsrm.mapId) viewRoleCount from "
				+ " tab_view_role_map tsrm "
				+ " left join tab_view_role tsr on tsr.viewRoleId=tsrm.viewRoleId "
				+ " left join tab_actor_role_map tarm on tarm.viewRoleId=tsrm.viewRoleId "
				+ " left join tab_actor_info tai on tai.actorId=tarm.actorId "
				+ " where tsr.viewRoleType="+roleType.intValue()
				+ " and tsrm.viewId in ("+viewIds+")"
						+ " group by tsr.viewRoleName,tsr.crewId,tsr.shortName,tsr.viewRoleId,tsr.viewRoleType,tai.actorId,tai.actorName) res order by res.viewRoleCount desc ";
		
		return this.query(sql, null,ViewRoleAndActorModel.class, null);
		
	}
	
	/**
	 * 查询所有演员，并根据类型,去除重复数据
	 * 该方法会查询出演员、角色的所有信息
	 * @return
	 */
	public List<Map<String, Object>> queryViewRoleListByCrewId(String crewId,Integer roleType){
		
		if(StringUtils.isBlank(crewId)){
			return null;
		}
		
		String sql = "select res.* from (select tsr.viewRoleName,tsr.crewId,if(tsr.shortName is null,'',tsr.shortName) as shortName,tsr.viewRoleId,tsr.viewRoleType,if(tai.actorId is null,'',tai.actorId) as actorId,if(tai.actorName is null,'',tai.actorName) as actorName,sum(tsrm.roleNum) roleNum,count(tsrm.mapId) viewRoleCount from "
				+ " tab_view_role_map tsrm "
				+ " left join tab_view_role tsr on tsr.viewRoleId=tsrm.viewRoleId "
				+ " left join tab_actor_role_map tarm on tarm.viewRoleId=tsrm.viewRoleId "
				+ " left join tab_actor_info tai on tai.actorId=tarm.actorId "
				+ " where tsr.viewRoleType="+roleType.intValue()
				+ " and tsrm.crewId = ?"
						+ " group by tsr.viewRoleName,tsr.crewId,tsr.shortName,tsr.viewRoleId,tsr.viewRoleType,tai.actorId,tai.actorName) res order by res.viewRoleCount desc ";
		
		return this.query(sql, new Object[] {crewId}, null);
		
	}
	
	/**
	 * 查询所有场的指定类型的演员去重
	 * @param crewId
	 * @return
	 */
	public List<ViewRoleAndActorModel> queryViewRoleSign(String crewId, Integer roleType){
		List<Object> conditionList = new ArrayList<Object>();
		conditionList.add(crewId);
		
		StringBuilder sql = new StringBuilder();
		sql.append("		SELECT ");
		sql.append("			tsr.viewRoleName, ");
		sql.append("			tsr.crewId, ");
		sql.append("			tsr.shortName, ");
		sql.append("			tsr.viewRoleId, ");
		sql.append("			tsr.viewRoleType, ");
		sql.append("			tai.actorId, ");
		sql.append("			tai.actorName, ");
		sql.append("			sum(tsrm.roleNum) roleNum, ");
		sql.append("			count(tsrm.mapId) viewRoleCount ");
		sql.append("		FROM ");
		sql.append("			tab_view_role_map tsrm ");
		sql.append("		LEFT JOIN tab_view_role tsr ON tsr.viewRoleId = tsrm.viewRoleId ");
		sql.append("		LEFT JOIN tab_actor_role_map tarm ON tarm.viewRoleId = tsrm.viewRoleId ");
		sql.append("		LEFT JOIN tab_actor_info tai ON tai.actorId = tarm.actorId ");
		sql.append("		left JOIN tab_view_info tvi on tsrm.viewId = tvi.viewId ");
		sql.append("		WHERE tsr.crewId = ?");
		if (roleType != null) {
			sql.append("	  and tsr.viewRoleType = ? ");
			conditionList.add(roleType);
		}
		sql.append("		GROUP BY ");
		sql.append("			tsr.viewRoleName, ");
		sql.append("			tsr.crewId, ");
		sql.append("			tsr.shortName, ");
		sql.append("			tsr.viewRoleId, ");
		sql.append("			tsr.viewRoleType, ");
		sql.append("			tai.actorId, ");
		sql.append("			tai.actorName ");
		sql.append("ORDER BY ");
		sql.append("	viewRoleCount DESC ");
		
		return this.query(sql.toString(), conditionList.toArray(), ViewRoleAndActorModel.class, null); 
	}
}
