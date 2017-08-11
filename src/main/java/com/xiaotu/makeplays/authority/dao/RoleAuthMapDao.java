package com.xiaotu.makeplays.authority.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.authority.model.RoleAuthMapModel;
import com.xiaotu.makeplays.utils.BaseDao;

@Repository
public class RoleAuthMapDao extends BaseDao<RoleAuthMapModel>{
	
	/**
	 * 查询角色和权限的关联关系
	 * @param roleId
	 * @param authId
	 * @return
	 * @throws Exception 
	 */
	public RoleAuthMapModel queryByRoleAuthId (String crewId, String roleId, String authId) throws Exception {
		String sql = "select * from tab_role_auth_map where (crewId = ? or crewId = '0') and roleId = ? and authId = ?";
		return this.queryForObject(sql, new Object[] {crewId, roleId, authId}, RoleAuthMapModel.class);
	}
	
	/**
	 * 查询用户在指定剧组下的指定权限信息
	 * 该查询会返回用户在剧组下拥有的当前权限，以及其所有的子权限
	 * 目前只支持到三级权限
	 * @param crewId
	 * @param userId
	 * @param authId
	 * @return
	 */
	public List<RoleAuthMapModel> queryByRoleAuthIdWithSubAuth(String crewId, String roleId, String authId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tram.* ");
		sql.append(" FROM ");
		sql.append(" 	tab_role_auth_map tram ");
		sql.append(" WHERE ");
		sql.append(" 	(tram.crewId = ? or tram.crewId = '0') ");
		sql.append(" AND tram.roleId = ? ");
		sql.append(" AND ( ");
		sql.append(" 	tram.authId IN ( ");
		sql.append(" 		SELECT ");
		sql.append(" 			ctsa.authId cAuthId ");
		sql.append(" 		FROM ");
		sql.append(" 			tab_sys_authority ftsa, ");
		sql.append(" 			tab_sys_authority ctsa ");
		sql.append(" 		WHERE ");
		sql.append(" 			ftsa.authId = ctsa.parentId ");
		sql.append("          AND ftsa.`status` = 0 ");
		sql.append("          AND ctsa.`status` = 0 ");
		sql.append(" 		AND ( ");
		sql.append(" 			ftsa.authId = ? ");
		sql.append(" 			OR ftsa.parentId = ? ");
		sql.append(" 		) ");
		sql.append(" 	) ");
		sql.append(" 	OR tram.authId = ? ");
		sql.append(" ) ");
		
		return this.query(sql.toString(), new Object[] {crewId, roleId, authId, authId, authId}, RoleAuthMapModel.class, null);
	}

	/**
	 * 删除角色的权限
	 * @param roleId
	 * @param crewId
	 */
	public void deleteRoleAuth(String roleId,String crewId){
		
		String sql = "delete from  "+RoleAuthMapModel.TABLE_NAME+" where crewid=? and roleId=?";
		this.getJdbcTemplate().update(sql,crewId, roleId);
		
	}
	
	
	/**
	 * 查询角色的拥有的权限
	 * @param roleId
	 * @param crewId
	 */
	public List<RoleAuthMapModel> queryByRoleId(String roleId, String crewId){
		
		String sql = "select * from  "+RoleAuthMapModel.TABLE_NAME+" where (crewId=? or crewId='0') and roleId=?";
		
		return this.query(sql, new Object[]{crewId,roleId},RoleAuthMapModel.class, null);
		
	}
	
	/**
	 * 根据权限id删除角色权限（批量删除）
	 * @param authId
	 */
	public void deleteAuthByAuthId(String authId){
		String sql = "delete from  "+RoleAuthMapModel.TABLE_NAME+" where authId=?";
		this.getJdbcTemplate().update(sql,authId);
	}
	
	/**
	 * 删除角色权限
	 * @param authId
	 * @param roleId
	 */
	public void deleteRoleAuthMap(String authId, String roleId){
		String sql = "delete from  "+RoleAuthMapModel.TABLE_NAME+" where authId=? and roleId = ? ";
		this.getJdbcTemplate().update(sql,new Object[]{authId,roleId});
	}
	
	/**
	 * 查询所有的角色权限关联关系
	 * @return
	 */
	public List<Map<String, Object>> queryAllRoleAuthMap() {
		String sql = "SELECT tsi.roleId,tua.authId FROM tab_sysrole_info tsi " 
				+ " LEFT JOIN tab_role_auth_map tua ON tsi.roleId = tua.roleId";
		return this.query(sql, null, null);
	}
}
