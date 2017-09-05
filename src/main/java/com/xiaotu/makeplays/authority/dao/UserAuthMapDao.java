package com.xiaotu.makeplays.authority.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.authority.model.UserAuthMapModel;
import com.xiaotu.makeplays.utils.AuthorityConstants;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 用户权限关联关系
 * @author xuchangjian 2016-5-12下午5:18:02
 */
@Repository
public class UserAuthMapDao extends BaseDao<UserAuthMapModel> {

	/**
	 * 查询用户在指定剧组下的权限关联
	 * @param crewId
	 * @param userId
	 * @return
	 */
	public List<UserAuthMapModel> queryByCrewUserId (String crewId, String userId) {
		String sql = "select * from tab_user_auth_map where crewId = ? and userId = ?";
		return this.query(sql, new Object[] {crewId, userId}, UserAuthMapModel.class, null);
	}
	
	/**
	 * 查询用户在指定剧组下的权限关联
	 * 多个用户
	 * @param crewId
	 * @param userIds
	 * @return
	 */
	public List<Map<String, Object>> queryByCrewUserIds (String crewId, String userIds) {
		int num = userIds.split(",").length;
		StringBuilder sql = new StringBuilder();
		sql.append(" select authId,if(count(userId)=?,1,2) hasAuth,case when (count(userId)=? and sum(readonly)=0) then 0 when sum(readonly)=count(userId) then 1 else 2 end readonly ");
		sql.append(" from tab_user_auth_map ");
		sql.append(" where crewId=? ");
		sql.append(" and userId in ('" + userIds.replace(",", "','") + "') ");
		sql.append(" group by authId ");
		return this.query(sql.toString(), new Object[]{num, num, crewId}, null);
	}
	
	/**
	 * 查询用户在指定剧组下的指定权限信息
	 * @param crewId
	 * @param userId
	 * @param authId
	 * @return
	 * @throws Exception 
	 */
	public UserAuthMapModel queryByCrewUserAuthId (String crewId, String userId, String authId) throws Exception {
		String sql = "select * from tab_user_auth_map where crewId = ? and userId = ? and authId = ?";
		return this.queryForObject(sql, new Object[] {crewId, userId, authId}, UserAuthMapModel.class);
	}
	
	/**
	 * 查询指定剧组指定权限的用户权限关联信息
	 * @param crewId
	 * @param authId
	 * @return
	 * @throws Exception
	 */
	public List<UserAuthMapModel> queryByCrewAuthId (String crewId, String authId) throws Exception {
		String sql = "select * from tab_user_auth_map where crewId = ? and authId = ?";
		return this.query(sql, new Object[] {crewId, authId}, UserAuthMapModel.class, null);
	}

	/**
	 * 查询用户指定的权限信息
	 * @param crewId
	 * @param userId
	 * @param authCode
	 * @return
	 * @throws Exception 
	 */
	public UserAuthMapModel queryOneByUserIdAndAuthCode(String crewId, String userId, String authCode) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tua.* ");
		sql.append(" FROM ");
		sql.append(" 	tab_sys_authority tsa, ");
		sql.append(" 	tab_user_auth_map tua ");
		sql.append(" WHERE ");
		sql.append(" 	tua.authId = tsa.authId ");
		sql.append(" AND tua.userId = ? ");
		sql.append(" AND tua.crewId=? ");
		sql.append(" AND tsa.authCode = ? ");
		sql.append(" AND tsa. STATUS = 0 ");
		sql.append(" AND ( ");
		sql.append(" 	tsa.authPlantform = 1 ");
		sql.append(" 	OR tsa.authPlantform = 3 ");
		sql.append(" ) ");
		
		return this.queryForObject(sql.toString(), new Object[] {userId, crewId, authCode}, UserAuthMapModel.class);
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
	public List<UserAuthMapModel> queryByCrewUserAuthIdWithSubAuth(String crewId, String userId, String authId, Integer status) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tuam.* ");
		sql.append(" FROM ");
		sql.append(" 	tab_user_auth_map tuam ");
		sql.append(" WHERE ");
		sql.append(" 	tuam.crewId = ? ");
		sql.append(" AND tuam.userId = ? ");
		sql.append(" AND ( ");
		sql.append(" 	tuam.authId IN ( ");
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
		sql.append(" 	OR tuam.authId = ? ");
		sql.append(" ) ");
		
		return this.query(sql.toString(), new Object[] {crewId, userId, authId, authId, authId}, UserAuthMapModel.class, null);
	}
	
	/**
	 * 把剧组下指定角色的所有权限赋予给指定用户
	 * 排除用户已经拥有的权限
	 * 排除剧组没有的权限
	 * @param crewId
	 * @param userId
	 * @param roleId
	 */
	public void addByRoleId(String crewId, String userId, String roleId) {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO tab_user_auth_map SELECT ");
		sql.append(" 	REPLACE (UUID(), '-', ''), ");
		sql.append(" 	tram.authId, ");
		sql.append(" 	?, ");
		sql.append(" 	?, ");
		sql.append(" 	tcam.readonly ");
		sql.append(" FROM ");
		sql.append(" 	tab_role_auth_map tram ");
		sql.append(" inner join tab_crew_auth_map tcam on tcam.authId=tram.authId and tcam.crewId=? ");
		sql.append(" WHERE ");
		sql.append(" 	tram.roleId = ? ");
		sql.append(" AND ( ");
		sql.append(" 	tram.crewId = ? ");
		sql.append(" 	OR tram.crewId = '0' ");
		sql.append(" ) ");
		sql.append(" AND NOT EXISTS ( ");
		sql.append(" 	SELECT ");
		sql.append(" 		1 ");
		sql.append(" 	FROM ");
		sql.append(" 		tab_user_auth_map tuam ");
		sql.append(" 	WHERE ");
		sql.append(" 		tuam.authId = tram.authId ");
		sql.append(" 	AND tuam.userId = ? ");
		sql.append(" 	AND tuam.crewId = ? ");
		sql.append(" ) ");
		
		this.getJdbcTemplate().update(sql.toString(), new Object[] {userId, crewId, crewId, roleId, crewId, userId, crewId});
	}
	
	/**
	 * 删除用户在指定剧组中拥有的指定角色的所有权限
	 * 只删除在用户拥有的角色中该角色特有的权限信息
	 * @param crewId
	 * @param userId
	 * @param roleId
	 */
	public void deleteByRoleId(String crewId, String userId, String roleId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" delete ");
		sql.append(" FROM ");
		sql.append(" 	tab_user_auth_map ");
		sql.append(" WHERE ");
		sql.append(" 	crewId = ? ");
		sql.append(" AND userId = ? ");
		sql.append(" AND authId IN ( ");
		sql.append(" 	SELECT ");
		sql.append(" 		tram.authId ");
		sql.append(" 	FROM ");
		sql.append(" 		tab_role_auth_map tram ");
		sql.append(" 	WHERE ");
		sql.append(" 		tram.roleId = ? ");
		sql.append(" 	AND ( ");
		sql.append(" 		tram.crewId = ? ");
		sql.append(" 		OR tram.crewId = '0' ");
		sql.append(" 	) ");
		sql.append(" ) ");
		sql.append(" AND authId NOT IN ( ");
		sql.append(" 	SELECT ");
		sql.append(" 		tram.authId ");
		sql.append(" 	FROM ");
		sql.append(" 		tab_user_role_map turm, ");
		sql.append(" 		tab_role_auth_map tram ");
		sql.append(" 	WHERE ");
		sql.append(" 		turm.userId = ? ");
		sql.append(" 	AND turm.crewId = ? ");
		sql.append(" 	AND turm.roleId != ? ");
		sql.append(" 	AND turm.roleId = tram.roleId ");
		sql.append(" ); ");
		
		this.getJdbcTemplate().update(sql.toString(), new Object[] {crewId, userId, roleId, crewId, userId, crewId, roleId});
	}
	
	/**
	 * 查询所有的用户权限关联关系
	 * @return
	 */
	public List<Map<String, Object>> queryAllUserAuthMap() {
		String sql = "SELECT tui.userId,tua.authId,tua.crewId " 
				+ " FROM tab_user_info tui LEFT JOIN tab_user_auth_map tua " 
				+ " ON tui.userId = tua.userId WHERE tua.authId IS NOT NULL";
		return this.query(sql, null, null);
	}
	
	/**
	 * 更新指定剧组的用户的指定权限的只读属性，设为只读
	 * @param crewId
	 * @param authId
	 */
	public void updateUserAuthReadOnlyByCrewAuthId(String crewId, String authId, Integer readonly) {
		String sql = "update tab_user_auth_map set readonly=? where crewId=? and authId=?";
		this.getJdbcTemplate().update(sql, new Object[]{readonly, crewId, authId});
	}
	
	/**
	 * 将过期剧组用户的权限设为只读
	 * @param crewIds
	 */
	public void updateExpiredCrewUserAuth(String crewIds) {
		String sql = "update tab_user_auth_map tuam set tuam.readonly=1 "
				+ " where tuam.crewId in ('" + crewIds + "') "
				+ " and tuam.authId in (select authId from tab_sys_authority tsa where tsa.differInRAndW = 1) ";
		this.getJdbcTemplate().update(sql);
	}
	
	/**
	 * 删除某个剧组用户权限
	 * @param authId
	 * @param crewId
	 */
	public void deleteUserAuthMapByCrew(String authId, String crewId){
		String sql = "delete from  " + UserAuthMapModel.TABLE_NAME + " where authId=? and crewId = ? ";
		this.getJdbcTemplate().update(sql,new Object[]{authId, crewId});
	}
	
	/**
	 * 删除过期剧组用户的收支管理权限
	 * @param crewIds
	 */
	public void deleteExpiredUserGetcostAuth(String crewIds) {
		String sql = "delete from tab_user_auth_map where crewId in ('" + crewIds + "') " 
				+ " and authId=(select authId from tab_sys_authority tsa where tsa.authCode='" + AuthorityConstants.GET_COST + "')";
		this.getJdbcTemplate().update(sql);
	}
	
	/**
	 * 删除剧组用户导入/导出权限
	 * @param crewId
	 */
	public void deleteCrewUserImpExpAuth(String crewId, String flag) {
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
		String sql = "delete from tab_user_auth_map where crewId=? " 
				+ " and authId in (select authId from tab_sys_authority tsa where tsa.authCode in ('" + authCodes + "'))";
		this.getJdbcTemplate().update(sql, new Object[]{crewId});		
	}
}
