package com.xiaotu.makeplays.authority.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.authority.model.CrewAuthMapModel;
import com.xiaotu.makeplays.utils.AuthorityConstants;
import com.xiaotu.makeplays.utils.BaseDao;

@Repository
public class CrewAuthMapDao extends BaseDao<CrewAuthMapModel>{
	
	/**
	 * 查询剧组拥有的权限
	 * @param crewId
	 */
	public List<CrewAuthMapModel> queryByCrewId(String crewId){
		
		String sql = "select * from " + CrewAuthMapModel.TABLE_NAME + " where crewId = ? ";
		
		return this.query(sql, new Object[]{crewId}, CrewAuthMapModel.class, null);
		
	}
	
	/**
	 * 查询剧组和权限的关联关系
	 * @param crewId
	 * @param authId
	 * @return
	 * @throws Exception 
	 */
	public CrewAuthMapModel queryByCrewAuthId (String crewId, String authId) throws Exception {
		String sql = "select * from tab_crew_auth_map where crewId = ? and authId = ?";
		return this.queryForObject(sql, new Object[] {crewId, authId}, CrewAuthMapModel.class);
	}
	
	/**
	 * 查询剧组的指定权限信息
	 * 该查询会返回该剧组拥有的当前权限，以及其所有的子权限
	 * 目前只支持到三级权限
	 * @param crewId
	 * @param authId
	 * @return
	 */
	public List<CrewAuthMapModel> queryByCrewAuthIdWithSubAuth(String crewId, String authId, Integer status) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tcam.* ");
		sql.append(" FROM ");
		sql.append(" 	tab_crew_auth_map tcam ");
		sql.append(" WHERE ");
		sql.append(" 	tcam.crewId = ? ");
		sql.append(" AND ( ");
		sql.append(" 	tcam.authId IN ( ");
		sql.append(" 		SELECT ");
		sql.append(" 			ctsa.authId cAuthId ");
		sql.append(" 		FROM ");
		sql.append(" 			tab_sys_authority ftsa, ");
		sql.append(" 			tab_sys_authority ctsa ");
		sql.append(" 		WHERE ");
		sql.append(" 			ftsa.authId = ctsa.parentId ");
		if(status == 0) {
			sql.append("          AND ftsa.`status` = 0 ");
			sql.append("          AND ctsa.`status` = 0 ");
		}
		sql.append(" 		AND ( ");
		sql.append(" 			ftsa.authId = ? ");
		sql.append(" 			OR ftsa.parentId = ? ");
		sql.append(" 		) ");
		sql.append(" 	) ");
		sql.append(" 	OR tcam.authId = ? ");
		sql.append(" ) ");
		
		return this.query(sql.toString(), new Object[] {crewId, authId, authId, authId}, CrewAuthMapModel.class, null);
	}
	
	/**
	 * 把所有权限赋予给指定剧组
	 * 排除系统管理员
	 * @param crewId
	 */
	public void addAllAuthToCrew(String crewId) {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO tab_crew_auth_map(mapId,crewId,authId,readonly) ");
		sql.append(" 	SELECT distinct REPLACE (UUID(), '-', ''), ");
		sql.append(" 	?, ");
		sql.append(" 	tsa.authId, ");
		sql.append(" 	if(differInRAndW=1 and defaultRorW=1,1,0) ");
		sql.append(" FROM ");
		sql.append(" 	tab_sys_authority tsa ");
		sql.append(" left join tab_role_auth_map tuam on tsa.authId=tuam.authId ");
		sql.append(" where ((tuam.roleId != '0' and tuam.roleId != '2') or tuam.roleId is null) ");
		
		this.getJdbcTemplate().update(sql.toString(), new Object[] {crewId});
	}
	
	/**
	 * 将某个权限赋给所有剧组
	 * @param authId
	 */
	public void addAuthToCrewByAuthId(String authId, boolean readonly, String parentId) {
		List<Object> params = new ArrayList<Object>();
		params.add(authId);
		params.add(readonly);
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO tab_crew_auth_map(mapId,crewId,authId,readonly) ");
		sql.append(" select REPLACE (UUID(), '-', ''), ");
		sql.append(" tci.crewId, ");
		sql.append(" ?, ");
		sql.append(" if(tci.endDate<CURDATE(),true,?) ");
		sql.append(" from tab_crew_info tci ");
		
		//不是根权限,将没有父权限的剧组去掉
		if(!parentId.equals("0") && !parentId.equals("1")) {
			sql.append(" where exists ( ");
			sql.append(" select 1 from tab_crew_auth_map tcam ");
			sql.append(" where tcam.crewId=tci.crewId ");
			sql.append(" and tcam.authId=? ");
			sql.append(" )");
			params.add(parentId);
		}
		
		this.getJdbcTemplate().update(sql.toString(), params.toArray());
	}
	
	/**
	 * 查询所有的剧组权限关联关系
	 * @return
	 */
	public List<Map<String, Object>> queryAllCrewAuthMap() {
		String sql = "SELECT tci.crewId,tcam.authId " 
				+ " FROM tab_crew_info tci LEFT JOIN tab_crew_auth_map tcam " 
				+ " ON tci.crewId = tcam.crewId WHERE tcam.authId IS NOT NULL";
		return this.query(sql, null, null);
	}
	
	/**
	 * 将过期剧组的权限设为只读
	 * @param crewIds
	 */
	public void updateExpiredCrewAuth(String crewIds) {
		String sql = "update tab_crew_auth_map tcam set tcam.readonly=1 "
				+ " where tcam.crewId in ('" + crewIds + "') "
				+ " and tcam.authId in (select authId from tab_sys_authority tsa where tsa.differInRAndW = 1) ";
		this.getJdbcTemplate().update(sql);
	}
	
	/**
	 * 删除剧组权限
	 * @param authId
	 * @param roleId
	 */
	public void deleteCrewAuthMap(String authId, String crewId){
		String sql = "delete from  " + CrewAuthMapModel.TABLE_NAME + " where authId=? and crewId = ? ";
		this.getJdbcTemplate().update(sql,new Object[]{authId, crewId});
	}
	
	/**
	 * 删除过期剧组的收支管理权限
	 * @param crewIds
	 */
	public void deleteExpiredCrewGetcostAuth(String crewIds) {
		String sql = "delete from tab_crew_auth_map where crewId in ('" + crewIds + "') " 
				+ " and authId=(select authId from tab_sys_authority tsa where tsa.authCode='" + AuthorityConstants.GET_COST + "')";
		this.getJdbcTemplate().update(sql);
	}
	
	/**
	 * 删除剧组导入/导出权限
	 * @param crewId
	 */
	public void deleteCrewImpExpAuth(String crewId, String flag) {
		String[] authArray = null;
		if(flag == "import") {
			authArray = AuthorityConstants.IMPORT_AUTHCODE;
		} else if(flag == "export"){
			authArray = AuthorityConstants.EXPORT_AUTHCODE;
		}
		String authCodes = "";
		for(String str : authArray) {
			authCodes += "," + str;
		}
		authCodes = authCodes.substring(1).replace(",", "','");
		String sql = "delete from tab_crew_auth_map where crewId=? " 
				+ " and authId in (select authId from tab_sys_authority tsa where tsa.authCode in ('" + authCodes + "'))";
		this.getJdbcTemplate().update(sql, new Object[]{crewId});		
	}
}
