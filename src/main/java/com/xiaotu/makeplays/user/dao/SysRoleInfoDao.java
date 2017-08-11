package com.xiaotu.makeplays.user.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.sysrole.model.SysroleInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.StringUtil;

@Repository
public class SysRoleInfoDao extends BaseDao<SysroleInfoModel>{

	public List<SysroleInfoModel> queryRoleByPage(Page page){
		
		String sql = "select * from "+SysroleInfoModel.TABLE_NAME;
		
		return this.query(sql, null, SysroleInfoModel.class, page);
		
	}
	
	
	/**
	 * 通过ID查询角色信息
	 * @param roleId
	 * @return
	 * @throws Exception
	 */
	public SysroleInfoModel queryById(String roleId) throws Exception{
		
		String sql = "select * from "+SysroleInfoModel.TABLE_NAME+" where roleid=?";
		
		SysroleInfoModel roleInfo = this.queryForObject(sql, new Object[]{roleId}, SysroleInfoModel.class);
		
		return roleInfo;
	}
	
	/**
	 * 查询角色信息
	 * 该方法返回的是map集合，会顺带查询出演员角色的其他信息，比如：所属剧组名称
	 * 以后如果角色添加新的关联字段，可以对该方法进行扩展
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> queryRoleWithCrewNameByPage(Page page){
		
		String sql = "select c.crewName, s.* from "
				+ SysroleInfoModel.TABLE_NAME
				+ " s left join "
				+ CrewInfoModel.TABLE_NAME
				+ " c on s.crewId = c.crewId ORDER BY level,orderNo";
		
		return this.query(sql, null, page);
		
	}
	
	/**
	 * 根据演员角色id获取演员姓名
	 */
	public String getRoleNameById(String roleId){
		String rolename = "";
		String sql = "select viewRoleName from tab_view_role where viewRoleId=?";
		List<Map<String,Object>> li = this.query(sql, new Object[]{roleId}, null);
		if(li!=null && li.size()>0 && (li.get(0)).get("viewRoleName") != null){
			rolename = (li.get(0)).get("viewRoleName").toString();
		}
		return rolename;
	}
	 /**
     * 删除角色
     * @param roleId
     * @throws Exception
     */
	public  Integer delRole(String roleId) throws SQLException{
      String sql="DELETE FROM tab_sysrole_info WHERE roleId=?";
      return this.getJdbcTemplate().update(sql, roleId);
    }

	/**
	 * 查询角色是否已存在
	 * @param roleName
	 * @param roleId
	 * @return
	 * @throws Exception
	 */
	public String getRoleName(String roleName, String roleId) throws Exception {
		List<Object> params = new ArrayList<Object>();
		params.add(roleName);
		String sql = "SELECT count(*) count FROM tab_sysrole_info WHERE roleName=?";
		if(StringUtil.isNotBlank(roleId)) {
			sql += " and roleId != ?";
			params.add(roleId);
		}
		List<Map<String, Object>> li = this.query(sql, params.toArray(), null);
		String count = li.get(0).get("count").toString();
		return count;
	}

	/**
	 * 查询角色最大的Id
	 * @return
	 * @throws Exception
	 */
	public Integer getRoleMax() throws Exception {
		String sql = " SELECT MAX(roleId) as roleId FROM tab_sysrole_info ";
		List<Map<String, Object>> li = this.query(sql, null, null);
		String count = li.get(0).get("roleId").toString();
		return Integer.parseInt(count);
	}
	
	/**
	 * 查询角色最大的顺序
	 * @return
	 * @throws Exception
	 */
	public Integer getRoleOrderNoMax(String parentId) throws Exception {
		if(parentId.equals("00") || parentId.equals("01")) {
			parentId = "00','01";
		}
		String sql = " SELECT MAX(orderNo) as orderNo FROM tab_sysrole_info where parentId in ('" + parentId + "')";
		List<Map<String, Object>> li = this.query(sql, null, null);
		int orderNo = 0;
		if(li != null && li.size() > 0) {
			if(StringUtil.isNotBlank(li.get(0).get("orderNo") + "")) {
				orderNo = Integer.parseInt(li.get(0).get("orderNo") + "");
			}
		}
		return orderNo;
	}
   	
   	/**
	 * 获取系统部门信息
	 * @param crewId	剧组ID
	 * @param needManager	是否需要管理员信息
	 * @return
	 */
   	public List<SysroleInfoModel> queryByCrewId(String crewId) {
   		String sql = "select * from tab_sysrole_info tsi where tsi.crewId = '0' OR tsi.crewId = ? ORDER BY  tsi.parentId desc, orderNo ";//(tsi.roleId+0)
   		List<SysroleInfoModel> li = this.query(sql, new Object[]{crewId},SysroleInfoModel.class, null);
   		return li;
   	}
   	
   	/**
   	 * 根据父ID查询职务信息
   	 * @param crewId
   	 * @param parentIds
   	 * @return
   	 */
   	public List<SysroleInfoModel> queryByParentIds(String crewId, String parentIds) {
   		parentIds = "'" + parentIds.replace(",", "','") + "'";
   		String sql = "select * from tab_sysrole_info where (crewId = '0' or crewId = ?) and parentId in ("+ parentIds +")";
   		return this.query(sql, new Object[] {crewId}, SysroleInfoModel.class, null);
   	}
   	
   	/**
   	 * 获取剧组下所有人员的所属小组信息
   	 * @param crewId
   	 * @return 小组ID  小组名称  小组下人员数量
   	 */
   	public List<Map<String, Object>> queryCrewGroupInfo (String crewId) {
   		StringBuilder sql = new StringBuilder();
   		sql.append(" SELECT ");
   		sql.append(" 	sub.roleId, ");
   		sql.append(" 	sub.roleName, ");
   		sql.append(" 	count(1) userNum");
   		sql.append(" FROM ");
   		sql.append(" 	( ");
   		sql.append(" 		SELECT DISTINCT ");
   		sql.append(" 			tcum.userId, ");
   		sql.append(" 			ptsi.roleId, ");
   		sql.append(" 			ptsi.roleName, ");
   		sql.append(" 			ptsi.orderNo ");
   		sql.append(" 		FROM ");
   		sql.append(" 			tab_crew_user_map tcum, ");
   		sql.append(" 			tab_user_role_map turm, ");
   		sql.append(" 			tab_user_info tui, ");
   		sql.append(" 			tab_sysrole_info ctsi,  ");
   		sql.append(" 			tab_sysrole_info ptsi ");
   		sql.append(" 		WHERE ");
   		sql.append(" 			tcum.crewId = ? ");
   		sql.append(" 		AND tcum.userId = turm.userId ");
   		sql.append(" 		AND tcum.userId = tui.userId ");
   		sql.append(" 		AND tui.status = 1 ");
   		sql.append(" 		AND turm.roleId = ctsi.roleId ");
   		sql.append(" 		AND turm.crewId = ? ");
   		sql.append(" 	AND (ctsi.parentId = ptsi.roleId OR (ctsi.parentId='01' and ctsi.roleId = ptsi.roleId)) ");
   		sql.append(" 	) sub ");
   		sql.append(" GROUP BY ");
   		sql.append(" 	sub.roleId, ");
   		sql.append(" 	sub.roleName ");
   		sql.append(" ORDER BY orderNo ");
   		
   		return this.query(sql.toString(), new Object[] {crewId, crewId}, null);
   	}
   	
   	/**
   	 * 获取剧组总人数
   	 * @param crewId
   	 * @return 人员数量
   	 */
   	public List<Map<String, Object>> queryCrewUserNum(String crewId) {
   		StringBuilder sql = new StringBuilder();
   		sql.append(" SELECT count(DISTINCT tcum.userId) userNum ");
   		sql.append(" FROM ");
   		sql.append(" 	tab_crew_user_map tcum, ");
   		sql.append(" 	tab_user_role_map turm, ");
   		sql.append(" 	tab_user_info tui ");
   		sql.append(" WHERE ");
   		sql.append(" 	tcum.crewId = ? ");
   		sql.append(" 	AND tcum.userId = turm.userId ");
   		sql.append(" 	AND tcum.userId = tui.userId ");
   		sql.append(" 	AND tui.status = 1 ");
   		sql.append(" 	AND turm.crewId = ? ");
   		
   		return this.query(sql.toString(), new Object[] {crewId, crewId}, null);
   	}
   	
   	/**
	 * 根据职务ID查询职务列表
	 * @param roleIds
	 * @return
	 */
	public List<SysroleInfoModel> queryByIds(String roleIds) {
		roleIds = "'" + roleIds.replace(",", "','") + "'";
		String sql = "select * from tab_sysrole_info where roleId in (?)";
		return this.query(sql, new Object[] {roleIds}, SysroleInfoModel.class, null);
	}
	
	/**
	 * 根据职务ID查询职务列表
	 * 该查询还会返回职务对应的部门信息
	 * @param roleIds
	 * @return
	 */
	public List<Map<String, Object>> queryByIdsWithParentInfo(String roleIds) {
		roleIds = "'" + roleIds.replace(",", "','") + "'";
		String sql = "select ctsi.*, ptsi.roleName parentName from tab_sysrole_info ctsi, tab_sysrole_info ptsi where ctsi.roleId in ("+ roleIds +") and ctsi.parentId = ptsi.roleId";
		return this.query(sql, null, null);
	}
	
	/**
	 * 查询用户在指定剧组中担任的职务
	 * 该查询返回的数据中包含对应职务的小组的名称
	 * @param crewId
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> queryByCrewUserId(String crewId, String userId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	ctsi.*, ");
		sql.append(" 	ptsi.roleName as parentName, ");
		sql.append(" 	ptsi.roleId as parentId ");
		sql.append(" FROM ");
		sql.append(" 	tab_sysrole_info ctsi left join tab_sysrole_info ptsi on ctsi.parentId = ptsi.roleId ,");
		sql.append(" 	tab_user_role_map turm ");
		sql.append(" WHERE ");
		sql.append(" 	turm.crewId = ? ");
		sql.append(" AND turm.userId = ? ");
		sql.append(" AND turm.roleId = ctsi.roleId ");
		sql.append(" ORDER BY ctsi.level,ctsi.orderNo ");
		
		return this.query(sql.toString(), new Object[] {crewId, userId}, null);
	}
	
	/**
	 * 更新角色的顺序
	 * @param id	角色ID
	 * @param sequence	顺序
	 */
	public void updateRoleSequence(String id, Integer sequence) {
		String sql = "update " + SysroleInfoModel.TABLE_NAME + " set orderNo = ? where roleId = ? ";
		this.getJdbcTemplate().update(sql, new Object[] {sequence, id});
	}
	
	/**
	 * 查询角色是否已被引用
	 * @param roleId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Integer getCountOfUserRoleMap(String roleId){
		String sql="SELECT count(*) count FROM tab_user_role_map WHERE roleId=?";
		List<Map<String, Object>> list= this.query(sql, new Object[]{roleId}, null);
		String count= list.get(0).get("count").toString();
		 return Integer.parseInt(count);
	}
}
