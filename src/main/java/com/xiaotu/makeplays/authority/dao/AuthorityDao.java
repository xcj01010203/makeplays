package com.xiaotu.makeplays.authority.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.authority.model.AuthorityModel;
import com.xiaotu.makeplays.authority.model.constants.AuthorityPlatform;
import com.xiaotu.makeplays.authority.model.constants.AuthorityStatus;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.StringUtil;

@Repository
public class AuthorityDao extends BaseDao<AuthorityModel> {
	
	/**
	 * 查询权限信息
	 * @param type 权限作用平台，2：pc，3：app
	 * @return
	 */
	public List<AuthorityModel> queryAuthorityList(int type) {
		String sql = "select * from " + AuthorityModel.TABLE_NAME 
				+ " where authPlantform = ? order by ifMenu desc,sequence";
		return this.query(sql, new Object[]{type}, AuthorityModel.class, null);
	}
	
	/**
	 * 验证操作编码唯一
	 */
	public List<Map<String, Object>> validateAuthCode(String authCode, String authId){
		String sql = "select 1 from " + AuthorityModel.TABLE_NAME + " where authCode = ? and authId != ? ";
		return this.query(sql, new Object[]{authCode, authId}, null);
	}
	
	/**
	 * 查询是否有子节点
	 * @param parentId
	 * @return
	 */
	public List<Map<String, Object>> isHasChidAuth(String parentId) {
		String sql = "select 1 from " + AuthorityModel.TABLE_NAME + " where parentId=?";
		return this.query(sql, new Object[]{parentId}, null);
	}
	
	/**
	 * 查询权限是否被角色使用
	 * @param authId
	 * @return
	 */
	public List<Map<String,Object>> isRoleAuthUsed(String authId){
		String sql = "select 1 from tab_role_auth_map where authId = ? ";
		return this.query(sql, new Object[]{authId}, null);
	}
	
	/**
	 * 查询权限是否被admin和客服以外的角色使用
	 * @param authId
	 * @return
	 */
	public List<Map<String,Object>> isRoleAuthUsedByCommonRole(String authId){
		String sql = "select 1 from tab_role_auth_map where authId = ? and roleId != '0' and roleId != '2'";
		return this.query(sql, new Object[]{authId}, null);
	}
	
	/**
	 * 查询权限是否被用户使用
	 * @param authId
	 * @return
	 */
	public List<Map<String,Object>> isUserAuthUsed(String authId) {
		String sql = "select 1 from tab_user_auth_map where authId = ? ";
		return this.query(sql, new Object[]{authId}, null);
	}
	
	/**
	 * 根据ID查询权限信息
	 * @param authId
	 * @return
	 * @throws Exception
	 */
	public AuthorityModel queryAuthById(String authId) throws Exception{
		
		String sql = "select * from " + AuthorityModel.TABLE_NAME + " where authId = ? ";
		
		return this.queryForObject(sql, new Object[]{authId}, AuthorityModel.class);
	}
	
	/**
	 * 修改权限表顺序
	 * @param authId
	 * @param sequence
	 */
	public void updateAuthoritySequence(String authId, int sequence){
		String sql = "update " + AuthorityModel.TABLE_NAME + " set sequence = ? where authId = ? ";
		this.getJdbcTemplate().update(sql, new Object[]{sequence, authId});
	}
	
	/**
	 * 查询当前组最大顺序
	 * @param parentId
	 * @return
	 */
	public int queryAuthorityMaxSeq(String parentId) {
		String sql = "select max(sequence) as sequence from tab_sys_authority where parentId = ?";
		List<Map<String, Object>> list = this.query(sql, new Object[]{parentId}, null);
		int sequence = 0;
		if(list != null && list.size() > 0) {
			if(StringUtil.isNotBlank(list.get(0).get("sequence") + "")) {
				sequence = Integer.parseInt(list.get(0).get("sequence") + "");
			}
		}
		return sequence;
	}	
	
	/**
	 * 根据某个权限获取所有角色及角色对应此权限的状态--根节点
	 * @param authId
	 * @return
	 */
	public List<Map<String,Object>> queryAllRoleByAuthId(String authId){
		String sql = "SELECT tsi.*,IF(tram.roleId IS NULL,0,1) as status" 
				+ " FROM tab_sysrole_info tsi LEFT JOIN tab_role_auth_map tram" 
				+ " ON tsi.roleId = tram.roleId AND tram.authId=? ORDER BY orderNo";
		List<Map<String,Object>> resultList = this.query(sql, new Object[]{authId}, null);
		return resultList;
	}
	
	/**
	 * 根据某个权限获取所有角色及角色对应此权限的状态--子节点
	 * @param authId
	 * @param parentId
	 * @return
	 */
	public List<Map<String,Object>> queryAllRoleByAuthId(String authId, String parentId){
		String sql = "SELECT tsi1.*,res.`status` FROM tab_sysrole_info tsi1,( " +
				"SELECT tsi.*,IF(tram.roleId IS NULL,0,1) as status " +
				"FROM tab_role_auth_map tra " +
				"LEFT JOIN tab_sysrole_info tsi ON tra.roleId = tsi.roleId " +
				"LEFT JOIN tab_role_auth_map tram ON tsi.roleId = tram.roleId AND tram.authId=? " +
				"WHERE  tra.authId = ? " +
				") res " +
				"WHERE tsi1.roleId = res.roleId OR tsi1.roleId = res.parentId " +
				"GROUP BY tsi1.roleId, tsi1.roleName,tsi1.roleDesc,tsi1.crewId,tsi1.parentId " +
				"ORDER BY tsi1.orderNo ";
		List<Map<String,Object>> resultList = this.query(sql, new Object[]{authId,parentId}, null);
		return resultList;
	}
	
	/**
	 * 判断权限的子权限是否有被修改的角色使用
	 * @param authId
	 * @param roleIds
	 * @return
	 */
	public List<Map<String,Object>> judgeAuthorityChildren(String authId, String roleIds){
		String sql = "SELECT 1 FROM tab_role_auth_map tram " 
				+ " WHERE tram.authId in (SELECT tsa.authId " 
				+ " FROM tab_sys_authority tsa WHERE tsa.parentId = ?) " 
				+ " AND tram.roleId in ('" + roleIds.replaceAll(",", "','") + "') ";
		return this.query(sql, new Object[]{authId}, null);
	}
	
	/**
	 * 判断权限的子权限是否有被修改的剧组使用
	 * @param authId
	 * @param crewIds
	 * @return
	 */
	public List<Map<String,Object>> judgeAuthChildIsUsedByCrew(String authId, String crewIds){
		String sql = "SELECT 1 FROM tab_crew_auth_map tcam " 
				+ " WHERE tcam.authId in (SELECT tsa.authId " 
				+ " FROM tab_sys_authority tsa WHERE tsa.parentId = ?) " 
				+ " AND tcam.crewId in ('" + crewIds.replaceAll(",", "','") + "') ";
		return this.query(sql, new Object[]{authId}, null);
	}
	
	/**
	 * 按照生成树的格式查询用户菜单
	 * @return
	 */
	public List<Map<String,Object>> queryUserAuthToTreeMap(String userId,String crewId){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT distinct ");
		sql.append(" 	tsa1.authId AS `id`, ");
		sql.append(" 	tsa1.parentid AS parentid, ");
		sql.append(" 	tsa1.authName AS `text`, ");
		sql.append(" 	tsa1.authUrl AS `value`, ");
		sql.append(" 	tsa1.operType AS operType, ");
		sql.append(" 	tsa1.ifmenu, ");
		sql.append(" 	tsa1.sequence, ");
		sql.append(" 	tsa1.cssName ");
		sql.append(" FROM ");
		sql.append(" 	tab_sys_authority tsa1, ");
		sql.append(" 	tab_user_auth_map tuam, ");
		sql.append(" 	tab_role_auth_map tram ");
		sql.append(" WHERE ");
		sql.append(" 	tsa1.ifmenu = 1 ");
		sql.append(" AND tsa1.authId = tuam.authId ");
		sql.append(" AND tram.authId = tsa1.authId ");
		sql.append(" AND tsa1. STATUS = 0 ");
		sql.append(" AND tuam.userId = ? ");
		sql.append(" AND tuam.crewId = ? ");
		sql.append(" AND tram.roleId!=0 AND tram.roleId!=2 ");
		sql.append(" ORDER BY tsa1.sequence ");
		
		return this.query(sql.toString(), new Object[]{userId,crewId}, null);
	}
	
	/**
	 * 按照生成树的格式查询管理员菜单
	 * @return
	 */
	public List<Map<String,Object>> queryAdminAuthToTreeMap(){
		
		String sql = "select tsa.authId as `id`,tsa.parentid as parentid,tsa.authName as `text`,tsa.authUrl as `value`,tsa.operType as operType,tsa.cssName "
				+ "from "+AuthorityModel.TABLE_NAME+" tsa ,tab_role_auth_map tram,tab_sysrole_info tsri where ifmenu="+Constants.IF_MENU_YES.intValue()
				+ " and tsa.authId = tram.authId and tram.roleId=tsri.roleId and tsri.roleName='admin' order by tsa.ifMenu desc,tsa.sequence";
		
		return this.query(sql, null, null);
	}
	
	
	/**
	 * 根据某个角色获取所有权限及角色对应此权限的状态
	 */
	public List<Map<String,Object>> getAllAuthByRoleId(String roleId){
		String sql = "SELECT aut.*,romap.readonly from tab_sys_authority aut " 
				+ " LEFT JOIN tab_role_auth_map romap ON aut.authId=romap.authId " 
				+ " where aut.status=0 AND romap.roleId=? ORDER BY aut.sequence";//order by ifMenu desc,sequence
		List<Map<String,Object>> resultList = this.query(sql, new Object[]{roleId}, null);
		return resultList;
	}	
	
	/**
	 * 删除剧组用户权限
	 */
	public void deleteByCrewUserAuthId(String crewId,String userId,String authId){
		//删除用户权限
		String sql = "delete from tab_user_auth_map where userId = ? and crewId = ? and authId = ?";
		this.getJdbcTemplate().update(sql, new Object[]{userId,crewId,authId});
	}
	
	/**
	 * 获取客服操作权限
	 */
	public Map<String,Object> getServiceAuth(String roleId){
		Map<String,Object> map = new HashMap<String, Object>();
		String sql = "SELECT tsa.authCode, readonly FROM tab_role_auth_map tua,tab_sys_authority tsa "
				+ " WHERE tua.authId = tsa.authId AND tua.roleId = ? AND tsa.status=" 
				+ AuthorityStatus.Valid.getValue() + " AND tsa.authCode IS NOT NULL;";
		List<Map<String,Object>> li = this.query(sql, new Object[]{roleId}, null);
		if(li!=null && li.size()>0){
			for (Map<String, Object> map2 : li) {
				if(map2.get("authCode")!=null && StringUtils.isNotBlank(map2.get("authCode").toString()))
					map.put(map2.get("authCode").toString(), map2.get("readonly"));
			}
		}
		return map;
	}
	
	/**
	 * 查询客服菜单
	 */
	public List<Map<String,Object>> getServiceMenu(String roleId){
		String sql = "select tsa.authId as `id`,tsa.parentid as parentid,tsa.authName as `text`,tsa.authUrl as `value`,tsa.operType as operType "
				+ "from " + AuthorityModel.TABLE_NAME
				+ " tsa ,tab_role_auth_map tram where ifmenu=" + Constants.IF_MENU_YES.intValue()
				+ " and tsa.authId = tram.authId and tram.roleId=?" 
				+ " and tsa.status=" + AuthorityStatus.Valid.getValue()
				+ " order by tsa.ifMenu desc,tsa.sequence";
		
		return this.query(sql, new Object[]{roleId}, null);
	}
	
	/**
	 * 查询用户指定的权限信息
	 * @param crewId
	 * @param userId
	 * @param authCode
	 * @return
	 * @throws Exception 
	 */
	public AuthorityModel queryOneByUserIdAndAuthCode(String crewId, String userId, String authCode) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tsa.* ");
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
		
		return this.queryForObject(sql.toString(), new Object[] {userId, crewId, authCode}, AuthorityModel.class);
	}
	
	/**
	 * 查询用户指定平台的有效的权限信息
	 * @param crewId	剧组ID
	 * @param userId	用户ID
	 * @param authPlantform	平台类型（1：全部   2：pc端     3：移动端）
	 * @return 权限的所有信息  用户对权限是否只读
	 */
	public List<Map<String, Object>> queryEffectiveAuthByUserAndPlantform(String crewId, String userId, Integer authPlantform) {
		String sql = "select tsa.*, tua.readonly from " + AuthorityModel.TABLE_NAME 
				+ " tsa, tab_user_auth_map tua where tsa.authId = tua.authId" 
				+ " AND tua.userId = ? and tsa.`status` = 0" 
				+ " and (tsa.authPlantform = ? or tsa.authPlantform = 1) and tua.crewId=?";
	
		return this.query(sql, new Object[] {userId, authPlantform, crewId}, null);
	}
	
	/**
	 * 根据平台类型查询所有权限信息（无论有效无效）
	 * 如果authPlantform为null,则返回所有有效的权限
	 * @param authPlantform 平台类型
	 * @return 返回系统中所有的权限
	 */
	public List<AuthorityModel> queryAuthByPlatform (Integer authPlantform) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from tab_sys_authority where 1 = 1");
		List<Object> params = new ArrayList<Object>();
		if (authPlantform != null) {
			sql.append(" and (authPlantform = ? or authPlantform = "+ AuthorityPlatform.Common.getValue() +") ");
			params.add(authPlantform);
		}
		sql.append(" order by authPlantform, ifMenu desc, sequence ");
		
		return this.query(sql.toString(), params.toArray(), AuthorityModel.class, null);
	}
	
	/**
	 * 根据平台类型查询所有有效的权限信息
	 * 如果authPlantform为null,则返回所有有效的权限
	 * @param authPlantform 平台类型
	 * @return 只返回已分配的权限(通过与tab_role_auth_map关联)，并且会把admin和客服特有的权限排除掉
	 */
	public List<AuthorityModel> queryAuthByPlatformWithoutAdmin (Integer authPlantform) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select distinct tsa.* from tab_sys_authority tsa ");
		sql.append(" left join tab_role_auth_map tuam on tsa.authId=tuam.authId ");
		sql.append(" where tsa.status =  " + AuthorityStatus.Valid.getValue());
		List<Object> params = new ArrayList<Object>();
		if (authPlantform != null) {
			sql.append(" and (tsa.authPlantform = ? or tsa.authPlantform = "+ AuthorityPlatform.Common.getValue() +") ");
			params.add(authPlantform);
		}
		sql.append(" and ((tuam.roleId != '0' and tuam.roleId != '2') or tuam.roleId is null) ");
		sql.append(" order by tsa.authPlantform, tsa.ifMenu desc, tsa.sequence ");
		
		return this.query(sql.toString(), params.toArray(), AuthorityModel.class, null);
	}
	
	/**
	 * 查询所有权限及拥有该权限的用户数量
	 * @param type
	 * @param crewId
	 * @return 只返回已分配的权限(通过与tab_role_auth_map关联)，并且会把admin和客服特有的权限排除掉
	 */
	public List<Map<String, Object>> queryAuthAndUserNumWithoutAdmin(int type, String crewId) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select tsa.*,count(distinct tuam.userId) as userNum ");
		sql.append(" from tab_sys_authority tsa ");
		sql.append(" left join tab_crew_auth_map tcam on tcam.authId=tsa.authId ");
		sql.append(" left join tab_role_auth_map tram on tram.authId=tsa.authId AND tram.crewId = tcam.crewId ");
		sql.append(" left join tab_user_auth_map tuam on tuam.authId=tsa.authId and tuam.crewId=tcam.crewId ");
		sql.append(" where tsa.status = " + AuthorityStatus.Valid.getValue());
		sql.append(" and tcam.crewId=? ");
		sql.append(" and tsa.authPlantform = ? ");
		sql.append(" and ((tram.roleId != '0' and tram.roleId != '2') or tram.roleId is null) ");
		sql.append(" group by tsa.authId ");
		sql.append(" order by tsa.ifMenu desc, tsa.sequence ");
		return this.getJdbcTemplate().queryForList(sql.toString(), new Object[]{crewId, type});
	}
	
	/**
	 * 查询剧组拥有的权限列表
	 * @param crewId
	 * @return
	 */
	public List<AuthorityModel> queryCrewAuthByCrewId(String crewId) {
		String sql = "select distinct tsa.authId,tsa.authName,tsa.operType,tsa.operDesc," 
				+ "tsa.authUrl,tsa.ifMenu,tsa.status,tsa.parentId,tsa.sequence," 
				+ "tsa.authPlantform,tsa.authCode,if(tsa.differInRAndW=1 and tcam.readonly=0,1,0) as differInRAndW,tsa.defaultRorW" 
				+ " from tab_crew_auth_map tcam " 
				+ " left join tab_sys_authority tsa on tsa.authId=tcam.authId " 
				+ " left join tab_role_auth_map tram on tram.authId=tsa.authId "
				+ " where tcam.crewId = ? "
				+ " and tsa.status = " + AuthorityStatus.Valid.getValue()
				+ " and ((tram.roleId != '0' and tram.roleId != '2') or tram.roleId is null) ";
		return this.query(sql, new Object[]{crewId}, AuthorityModel.class, null);
	}
	
	/**
	 * 根据父ID查询子权限信息
	 * @param authId
	 * @return
	 * @throws Exception
	 */
	public List<AuthorityModel> queryAuthByPid(String parentId) throws Exception{
		
		String sql = "select * from " + AuthorityModel.TABLE_NAME + " where parentId = ? ";
		
		return this.query(sql, new Object[]{parentId}, AuthorityModel.class, null);
	}
	
	/**
	 * 根据某个权限获取所有剧组及剧组对应此权限的状态--根节点
	 * @param authId
	 * @return
	 */
	public List<Map<String, Object>> queryAllCrewByAuthId(String authId) {
		String sql = "SELECT tci.*,IF(tcam.crewId IS NULL,0,1) as status, if(tci.endDate<CURDATE(),0,1) as outofdate " 
				+ " FROM tab_crew_info tci LEFT JOIN tab_crew_auth_map tcam" 
				+ " ON tci.crewId = tcam.crewId AND tcam.authId=? ORDER BY CONVERT(tci.crewName USING gbk),tci.crewId ";
		List<Map<String,Object>> resultList = this.query(sql, new Object[]{authId}, null);
		return resultList;
	}
	
	/**
	 * 根据某个权限获取所有剧组及剧组对应此权限的状态--子节点
	 * @param authId
	 * @param parentId
	 * @return
	 */
	public List<Map<String,Object>> queryAllCrewByAuthId(String authId, String parentId){
		String sql = "SELECT distinct tci.*, IF (tcam.crewId IS NULL, 0, 1) AS status, if(tci.endDate<CURDATE(),0,1) as outofdate "
				+ " FROM tab_crew_auth_map tca "
				+ " LEFT JOIN tab_crew_info tci on tci.crewId=tca.crewId "
				+ " LEFT JOIN tab_crew_auth_map tcam ON tcam.crewId=tci.crewId AND tcam.authId = ? "
				+ " WHERE tca.authId = ? "
				+ " ORDER BY CONVERT(tci.crewName USING gbk),tci.crewId ";
		List<Map<String,Object>> resultList = this.query(sql, new Object[]{authId, parentId}, null);
		return resultList;
	}
}
