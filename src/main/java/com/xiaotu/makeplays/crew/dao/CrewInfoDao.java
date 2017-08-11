package com.xiaotu.makeplays.crew.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.authority.model.UserAuthMapModel;
import com.xiaotu.makeplays.crew.controller.filter.CrewInfoFilter;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.model.CrewRoleUserMapModel;
import com.xiaotu.makeplays.crew.model.CrewUserMapModel;
import com.xiaotu.makeplays.crew.model.constants.ProjectType;
import com.xiaotu.makeplays.sysrole.model.UserRoleMapModel;
import com.xiaotu.makeplays.user.model.UserFocusRoleMapModel;
import com.xiaotu.makeplays.utils.AuthorityConstants;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.StringUtils;

@Repository
public class CrewInfoDao extends BaseDao<CrewInfoModel> {
 
	/**
	 * 根据ID查询剧组信息
	 * @param crewId
	 * @return
	 * @throws Exception 
	 */
	public CrewInfoModel queryById(String crewId) throws Exception {
		String sql = "select * from " + CrewInfoModel.TABLE_NAME + " where crewId = ?";
		
		return this.queryForObject(sql, new Object[] {crewId}, CrewInfoModel.class);
	}
	
	/**
	 * 根据多个条件查询剧组信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<CrewInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + CrewInfoModel.TABLE_NAME + " where 1 = 1 ");

		List<Object> conList = new LinkedList<Object>();
		if (conditionMap != null) {
			Set<String> keySet = conditionMap.keySet();
			Iterator<String> iter = keySet.iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				Object value = conditionMap.get(key);
				sql.append(" and " + key + " = ?");
				conList.add(value);
			}
		}
		Object[] objArr = conList.toArray();
		List<CrewInfoModel> crewInfoList = this.query(sql.toString(), objArr, CrewInfoModel.class, page);
		
		return crewInfoList;
	}
	
	/**
	 * 查询用户默认剧组
	 * 用户在剧组中的状态是有效的,剧组未停用
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	public CrewInfoModel queryUserDefaultCrew(String userId) throws Exception{
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tci.* ");
		sql.append(" FROM ");
		sql.append(" 	tab_crew_user_map tcum, ");
		sql.append(" 	tab_crew_info tci ");
		sql.append(" WHERE ");
		sql.append(" 	tcum.userId = ? ");
		sql.append(" AND tcum.ifDefault = 1 ");
		sql.append(" AND tcum.status = 1 ");
		sql.append(" AND tci.isStop = 0 ");
		sql.append(" AND tcum.crewId = tci.crewId ");
//		sql.append(" AND tci.startDate <= CURDATE() ");
//		sql.append(" AND tci.endDate >= CURDATE() ");
		
		return this.queryForObject(sql.toString(), new Object[] {userId}, CrewInfoModel.class);
	}
	
	/**
	 * 查询用户默认剧组
	 * 用户在剧组中的状态是有效的,剧组未停用,剧组未过期
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	public CrewInfoModel queryUserDefaultCrewForApp(String userId) throws Exception{
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tci.* ");
		sql.append(" FROM ");
		sql.append(" 	tab_crew_user_map tcum, ");
		sql.append(" 	tab_crew_info tci ");
		sql.append(" WHERE ");
		sql.append(" 	tcum.userId = ? ");
		sql.append(" AND tcum.ifDefault = 1 ");
		sql.append(" AND tcum.status = 1 ");
		sql.append(" AND tci.isStop = 0 ");
		sql.append(" AND tcum.crewId = tci.crewId ");
		sql.append(" AND tci.startDate <= CURDATE() ");
		sql.append(" AND tci.endDate >= CURDATE() ");
		
		return this.queryForObject(sql.toString(), new Object[] {userId}, CrewInfoModel.class);
	}
	
	/**
	 * 查询用户所有剧组
	 * 用户在剧组中的状态是有效的
	 */
	public List<CrewInfoModel> queryUserAllCrew(String userId){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tci.* ");
		sql.append(" FROM ");
		sql.append(" 	tab_crew_user_map tcum, ");
		sql.append(" 	tab_crew_info tci ");
		sql.append(" WHERE ");
		sql.append(" 	tcum.userId = ? ");
		sql.append(" AND tcum.status = 1 ");
		sql.append(" AND tci.isStop = 0 ");
		sql.append(" AND tcum.crewId = tci.crewId ");
//		sql.append(" AND tci.startDate <= CURDATE() ");
//		sql.append(" AND tci.endDate >= CURDATE() ");
		sql.append(" ORDER BY tci.startDate desc");
		
		return this.query(sql.toString(), new Object[] {userId}, CrewInfoModel.class, null);
	}
	
	/**
	 * 查询用户所有剧组
	 * 用户在剧组中的状态是有效的,剧组未停用,剧组未过期
	 */
	public List<CrewInfoModel> queryUserAllCrewForApp(String userId){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tci.* ");
		sql.append(" FROM ");
		sql.append(" 	tab_crew_user_map tcum, ");
		sql.append(" 	tab_crew_info tci ");
		sql.append(" WHERE ");
		sql.append(" 	tcum.userId = ? ");
		sql.append(" AND tcum.status = 1 ");
		sql.append(" AND tci.isStop = 0 ");
		sql.append(" AND tcum.crewId = tci.crewId ");
		sql.append(" AND tci.startDate <= CURDATE() ");
		sql.append(" AND tci.endDate >= CURDATE() ");
		sql.append(" ORDER BY tci.startDate desc");
		
		return this.query(sql.toString(), new Object[] {userId}, CrewInfoModel.class, null);
	}
	
	/*public List<Map<String,Object>> queryUserAllActiveCrew(String userId){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("    tci.*,tcum.userid,tcum.roleid,tcum.ifdefault ");
		sql.append(" FROM ");
		sql.append("    tab_crew_user_map tcum, ");
		sql.append("    tab_crew_info tci ");
		sql.append(" WHERE ");
		sql.append("    tcum.userid =? ");
		sql.append(" AND tci.startDate <= CURDATE() ");
		sql.append(" AND tci.endDate >= CURDATE() ");
		sql.append(" AND tcum. STATUS = 1 ");
		sql.append(" AND tci.crewid = tcum.crewId order by tcum.ifdefault desc ");
		List<Map<String,Object>> list = this.getJdbcTemplate().queryForList(sql.toString(), new Object[]{userId});
		return list;
	}*/
	/**
	 * 查询用户申请的，还未审核过的，未过期的剧组
	 * @param userId
	 * @return
	 */
	public List<CrewInfoModel> queryAuditingCrewByUserId(String userId) {
		String sql = "select tci.* from tab_crew_info tci, tab_joinCrew_applyMsg tja where tci.startDate <= CURDATE() and tci.endDate >= CURDATE() and tja.aimCrewId = tci.crewId and tja.applyerId = ? and tja.status = 1 and tci.isStop=0";
		return this.query(sql, new Object[] {userId}, CrewInfoModel.class, null);
	}
	
	/**
	 * 查询用户所在的，未过期的剧组
	 * 除了返回剧组的基本信息外，还会返回用户在剧组中的是否有场记单权限，是否有手机端成员管理权限，担任的职务信息以及用户在剧组中的状态
	 * @return
	 */
	public List<Map<String, Object>> queryUserCrewList(String userId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tcum.crewId, ");
		sql.append(" 	tcum.userId, ");
		sql.append(" 	tci.crewName, ");
		sql.append(" 	tci.crewType, ");
		sql.append(" 	tci.company, ");
		sql.append("    tci.recordNumber, ");
		sql.append(" 	tci.shootStartDate, ");
		sql.append(" 	tci.shootEndDate, ");
		sql.append(" 	tci.`status`, ");//剧组状态
		sql.append(" 	tci.director, ");
		sql.append(" 	tci.scriptWriter, ");
		sql.append(" 	tci.mainactor AS mainActorNames, ");
		sql.append(" 	tci.`subject` AS subjectName, ");
		sql.append("    tci.enterPassword, ");
		sql.append("    tci.picPath, ");
		sql.append("    tci.isStop, ");
		sql.append("    tci.seriesNo, ");//立项集数
		sql.append("    tci.coProduction, ");//合拍协议
		sql.append("    tci.coProMoney, ");//合拍协议金额
		sql.append("    tci.budget, ");//剧组执行预算
		sql.append("    tci.remark, "); //重要事项说明及重要情况预警
		sql.append(" 	tcum.`status` AS crewUserStatus, ");
		sql.append(" 	tcum.ifDefault, ");
		sql.append(" 	( ");
		sql.append(" 		SELECT ");
		sql.append(" 			tsa.authId ");
		sql.append(" 		FROM ");
		sql.append(" 			tab_sys_authority tsa, ");
		sql.append(" 			tab_user_auth_map tua ");
		sql.append(" 		WHERE ");
		sql.append(" 			tua.authId = tsa.authId ");
		sql.append(" 		AND tua.userId = ? ");
		sql.append(" 		AND tua.crewId = tcum.crewId ");
		sql.append(" 		AND tsa.authCode = ? ");
		sql.append(" 		AND tsa. STATUS = 0 ");
		sql.append(" 		AND ( ");
		sql.append(" 			tsa.authPlantform = 1 ");
		sql.append(" 			OR tsa.authPlantform = 3 ");
		sql.append(" 		) ");
		sql.append(" 	) AS clipAuthId, ");
		sql.append(" 	( ");
		sql.append(" 		SELECT ");
		sql.append(" 			tsa.authId ");
		sql.append(" 		FROM ");
		sql.append(" 			tab_sys_authority tsa, ");
		sql.append(" 			tab_user_auth_map tua ");
		sql.append(" 		WHERE ");
		sql.append(" 			tua.authId = tsa.authId ");
		sql.append(" 		AND tua.userId = ? ");
		sql.append(" 		AND tua.crewId = tcum.crewId ");
		sql.append(" 		AND tsa.authCode = ? ");
		sql.append(" 		AND tsa. STATUS = 0 ");
		sql.append(" 		AND ( ");
		sql.append(" 			tsa.authPlantform = 1 ");
		sql.append(" 			OR tsa.authPlantform = 3 ");
		sql.append(" 		) ");
		sql.append(" 	) AS crewUserManagerAuthId, ");
		sql.append(" 	GROUP_CONCAT(ptsi.roleName, '-', ctsi.roleName) roleNames ");
		sql.append(" FROM ");
		sql.append(" 	tab_crew_user_map tcum ");
		sql.append(" 		LEFT JOIN tab_user_role_map turm ON turm.userId = ? AND turm.crewId = tcum.crewId");
		
		//两个角色表，ptsi查询的是部门信息，ctsi查询的是职务信息，这样连接查询之后会自动把“剧组管理员”，“项目总监”之类的没有部门信息的职务过滤掉
		sql.append("    	LEFT JOIN tab_sysrole_info ctsi ON ctsi.roleId = turm.roleId ");
		sql.append("    	LEFT JOIN tab_sysrole_info ptsi ON ptsi.roleId = ctsi.parentId, ");	
		
		sql.append(" 	tab_crew_info tci ");
		sql.append(" WHERE ");
		sql.append(" 	tcum.crewId = tci.crewId ");
		sql.append(" AND tcum.userId = ? ");
		sql.append(" AND tci.startDate <= CURDATE() ");
		sql.append(" AND tci.endDate >= CURDATE() ");
		sql.append(" GROUP BY tcum.crewId, ");
		sql.append(" 	tcum.userId, ");
		sql.append(" 	tci.crewName, ");
		sql.append(" 	tci.company, ");
		sql.append(" 	tci.shootStartDate, ");
		sql.append(" 	tci.shootEndDate, ");
		sql.append(" 	tci.`status`, ");
		sql.append(" 	tci.director, ");
		sql.append(" 	tci.scriptWriter, ");
		sql.append(" 	tci.mainactor, ");
		sql.append(" 	tci.`subject`, ");
		sql.append(" 	clipAuthId ");
		sql.append(" ORDER BY tci.createTime desc ");
		
		return this.query(sql.toString(), new Object[] {userId, AuthorityConstants.CLIP, userId, AuthorityConstants.CREW_USER_MANAGE, userId, userId}, null);
	}
	
	/**
	 * 查询用户过期剧组
	 * 除了返回剧组的基本信息外，还会返回用户在剧组中的是否有场记单权限，是否有手机端成员管理权限，担任的职务信息以及用户在剧组中的状态
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> queryUserExpiredCrew(String userId){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tcum.crewId, ");
		sql.append(" 	tcum.userId, ");
		sql.append(" 	tci.crewName, ");
		sql.append(" 	tci.crewType, ");
		sql.append(" 	tci.company, ");
		sql.append("    tci.recordNumber, ");
		sql.append(" 	tci.shootStartDate, ");
		sql.append(" 	tci.shootEndDate, ");
		sql.append(" 	tci.`status`, ");
		sql.append(" 	tci.director, ");
		sql.append(" 	tci.scriptWriter, ");
		sql.append(" 	tci.mainactor AS mainActorNames, ");
		sql.append(" 	tci.`subject` AS subjectName, ");
		sql.append("    tci.enterPassword, ");
		sql.append("    tci.picPath, ");
		sql.append("    tci.isStop, ");
		sql.append(" 	tcum.`status` AS crewUserStatus, ");
		sql.append(" 	tcum.ifDefault, ");
		sql.append(" 	( ");
		sql.append(" 		SELECT ");
		sql.append(" 			tsa.authId ");
		sql.append(" 		FROM ");
		sql.append(" 			tab_sys_authority tsa, ");
		sql.append(" 			tab_user_auth_map tua ");
		sql.append(" 		WHERE ");
		sql.append(" 			tua.authId = tsa.authId ");
		sql.append(" 		AND tua.userId = ? ");
		sql.append(" 		AND tua.crewId = tcum.crewId ");
		sql.append(" 		AND tsa.authCode = ? ");
		sql.append(" 		AND tsa. STATUS = 0 ");
		sql.append(" 		AND ( ");
		sql.append(" 			tsa.authPlantform = 1 ");
		sql.append(" 			OR tsa.authPlantform = 3 ");
		sql.append(" 		) ");
		sql.append(" 	) AS clipAuthId, ");
		sql.append(" 	( ");
		sql.append(" 		SELECT ");
		sql.append(" 			tsa.authId ");
		sql.append(" 		FROM ");
		sql.append(" 			tab_sys_authority tsa, ");
		sql.append(" 			tab_user_auth_map tua ");
		sql.append(" 		WHERE ");
		sql.append(" 			tua.authId = tsa.authId ");
		sql.append(" 		AND tua.userId = ? ");
		sql.append(" 		AND tua.crewId = tcum.crewId ");
		sql.append(" 		AND tsa.authCode = ? ");
		sql.append(" 		AND tsa. STATUS = 0 ");
		sql.append(" 		AND ( ");
		sql.append(" 			tsa.authPlantform = 1 ");
		sql.append(" 			OR tsa.authPlantform = 3 ");
		sql.append(" 		) ");
		sql.append(" 	) AS crewUserManagerAuthId, ");
		sql.append(" 	GROUP_CONCAT(tsi.roleName) roleNames ");
		sql.append(" FROM ");
		sql.append(" 	tab_crew_user_map tcum ");
		sql.append(" 		LEFT JOIN tab_user_role_map turm ON turm.userId = ? AND turm.crewId = tcum.crewId");
		sql.append("    	LEFT JOIN tab_sysrole_info tsi ON tsi.roleId = turm.roleId, ");
		sql.append(" 	tab_crew_info tci ");
		sql.append(" WHERE ");
		sql.append(" 	tcum.crewId = tci.crewId ");
		sql.append(" AND tcum.userId = ? ");
		sql.append(" AND tci.endDate < CURDATE() ");
//		sql.append(" AND tci.projectType != 1");
		sql.append(" GROUP BY tcum.crewId, ");
		sql.append(" 	tcum.userId, ");
		sql.append(" 	tci.crewName, ");
		sql.append(" 	tci.company, ");
		sql.append(" 	tci.shootStartDate, ");
		sql.append(" 	tci.shootEndDate, ");
		sql.append(" 	tci.`status`, ");
		sql.append(" 	tci.director, ");
		sql.append(" 	tci.scriptWriter, ");
		sql.append(" 	tci.mainactor, ");
		sql.append(" 	tci.`subject`, ");
		sql.append(" 	clipAuthId ");
		sql.append(" ORDER BY tci.createTime desc ");
		
		return this.query(sql.toString(), new Object[] {userId, AuthorityConstants.CLIP, userId, AuthorityConstants.CREW_USER_MANAGE, userId, userId}, null);
	}
	
	/**
	 * 查询剧组信息
	 * @param crewName 剧组名称，模糊查询
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryCrewList(CrewInfoFilter crewInfoFilter) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append(" select budget.*, ");
		sql.append(" 	sum(IF(tvi.shootStatus != 3, 1, 0)) totalViewCount, ");
		sql.append(" 	sum(IF(tvi.shootStatus = 2, 1, 0)) finishedViewCount ");
		sql.append(" FROM ");
		sql.append(" (select tci.crewId,tci.crewName,tci.crewType,tci.company, ");
		sql.append(" tci.startDate,tci.endDate,tci.director,tci.shootStartDate,tci.shootEndDate, ");
		sql.append(" tci.projectType,tci.createTime,tci.isStop,ROUND(sum(tfcm.money * ftci.exchangeRate), 2) totalBudgetMoney ");
		sql.append(" FROM tab_crew_info tci ");
		sql.append(" LEFT JOIN tab_finanSubj_currency_map tfcm on tci.crewId=tfcm.crewId ");
		sql.append(" LEFT JOIN tab_currency_info ftci ON ftci.id = tfcm.currencyId ");
		List<Object> params = new ArrayList<Object>();
		if(crewInfoFilter != null){
			sql.append(" where 1=1");
			//剧组类型
			String crewType = crewInfoFilter.getCrewType();
			if(StringUtils.isNotBlank(crewType)) {
				sql.append(" and tci.crewType in (").append(crewType).append(")");
			}
			//剧组名称
			String crewName = crewInfoFilter.getCrewName();
			if(StringUtils.isNotBlank(crewName)) {
				crewName = crewName.replaceAll("_", "\\\\_");
				crewName = crewName.replaceAll("%", "\\\\%");
				sql.append(" and tci.crewName like ?");
				params.add("%" + crewName + "%");
			}
			//有效期
			String startDate = crewInfoFilter.getStartDate();
			if(StringUtils.isNotBlank(startDate)) {
				sql.append(" and tci.startDate>=?");
				params.add(startDate);
			}
			String endDate = crewInfoFilter.getEndDate();
			if(StringUtils.isNotBlank(endDate)) {
				sql.append(" and tci.endDate<=?");
				params.add(endDate);
			}
			//拍摄期
			String shootStartDate = crewInfoFilter.getShootStartDate();
			if(StringUtils.isNotBlank(shootStartDate)) {
				sql.append(" and tci.shootStartDate>=?");
				params.add(shootStartDate);
			}
			String shootEndDate = crewInfoFilter.getShootEndDate();
			if(StringUtils.isNotBlank(shootEndDate)) {
				sql.append(" and tci.shootEndDate<=?");
				params.add(shootEndDate);
			}
			//制片公司
			String company = crewInfoFilter.getCompany();
			if(StringUtils.isNotBlank(company)) {
				company = company.replaceAll("_", "\\\\_");
				company = company.replaceAll("%", "\\\\%");
				sql.append(" and tci.company like ?");
				params.add("%" + company + "%");
			}
			//导演
			String director = crewInfoFilter.getDirector();
			if(StringUtils.isNotBlank(director)) {
				director = director.replaceAll("_", "\\\\_");
				director = director.replaceAll("%", "\\\\%");
				sql.append(" and tci.director like ?");
				params.add("%" + director + "%");
			}
			//项目类型
			String projectType = crewInfoFilter.getProjectType();
			if(StringUtils.isNotBlank(projectType)) {
				sql.append(" and tci.projectType in (").append(projectType).append(")");
			}
			//是否在有效期内
			int outofdate = crewInfoFilter.getOutofdate();
			if(StringUtils.isNotBlank(outofdate + "")) {
				switch (outofdate) {
					case 0:	//全部			
						break;
					case 1: //有效
						sql.append(" and tci.endDate>=CURDATE()");
						break;
					case 2: //已过期
						sql.append(" and tci.endDate<CURDATE()");
						break;
					case 3: //停用
						sql.append(" and tci.isStop=1");
						break;
				}
			}
		}
		sql.append(" group by tci.crewId ) budget ");
		sql.append(" LEFT JOIN ( ");
		sql.append(" select tpi.crewId, ");
		sql.append(" ROUND(sum(tpi.totalMoney * ptci.exchangeRate),2) totalPayedMoney ");
		sql.append(" FROM tab_payment_info tpi ");
		sql.append(" LEFT JOIN tab_currency_info ptci ON tpi.currencyId = ptci.id ");
		sql.append(" where tpi.status = 1 ");
		sql.append(" group by tpi.crewId ");
		sql.append(" ) payed on budget.crewId=payed.crewId ");
		sql.append(" LEFT JOIN tab_view_info tvi ON tvi.crewId = budget.crewId ");
		sql.append(" group by budget.crewId ");
		String orderBySql = " order by budget.createTime desc ";
		if(crewInfoFilter != null){
			String orderBy = crewInfoFilter.getCrewSortCon();
			if(StringUtils.isNotBlank(orderBy)) {
				orderBySql = " order by " + orderBy;
			}
		}
		sql.append(orderBySql);
		return this.query(sql.toString(), params.toArray(), null);
	}
	
	/**
	 * 查询所有剧组的ID，名称
	 * @return
	 */
	public List<Map<String, Object>> queryAllCrewList() {
		String sql = "select crewId, crewName from tab_crew_info";
		return this.query(sql, null, null);
	}
	
	/**
	 * lma 删除剧组成员所有权限
	 */
	@Deprecated
	public void dleteUserRoleAuthAll(String userId,String crewId)throws Exception{
		String sqlUserRole="DELETE FROM tab_user_role_map WHERE userId=? AND crewId=?";
		this.getJdbcTemplate().update(sqlUserRole, new Object[]{userId,crewId});
		
		String crewRoleSql="DELETE FROM tab_crewRole_user_map WHERE userId=? AND crewId=?";
		this.getJdbcTemplate().update(crewRoleSql, new Object[]{userId,crewId});
	}
	@Deprecated
	public void dleteCrewUserMap(String userId,String crewId)throws Exception {
		String sql="DELETE FROM tab_crew_user_map WHERE userId=? AND crewId=?";
		this.getJdbcTemplate().update(sql, new Object[]{userId,crewId});
	}
	
	/**
	 * 删除用户与权限表
	 */
	@Deprecated
	public int  delUserAuth (String userId,String crewId)throws SQLException {
		//删除用户权限
		String authsql = "delete from tab_user_auth_map where userId = ? and crewId = ?";
	  int auth=	this.getJdbcTemplate().update(authsql, new Object[]{userId,crewId});
		/*String sqluser="delete from tab_user_info where userId = ?";
	  this.getJdbcTemplate().update(sqluser, new Object[]{userId});*/
		return auth;
	}
	
	/**
	 * 根据角色ids查询权限id
	 */
	@Deprecated
	public List<Map<String,Object>> getAuthIdByRoleids(String roleIds){
		String roleid = "'"+roleIds.replaceAll(",", "','")+"'";
		String sql = "select DISTINCT(authId) AS authId FROM tab_role_auth_map WHERE roleId in (" + roleid +")";
		return this.query(sql, null, null);
	}
	/**
	 * 查询场景角色
	 */
	@Deprecated
	public List<Map<String, Object>> getCrewRole(String crewId,String userId) throws SQLException{
		String sql=" SELECT * From  tab_crewRole_user_map WHERE crewId=? and userId=? ";
		return this.query(sql, new Object[]{crewId,userId}, null);
	}
	
	/**
	 * 根据剧组名称查询剧组信息
	 * @param crewName
	 * @return
	 */
	public List<Map<String,Object>> queryCrewIdAndCrewName(String crewName){
		String sql=" SELECT crew.crewId,crew.crewName "
				+ " FROM  tab_crew_info crew  " 
				+ " where 1=1 " ;
		List<Object> params = new ArrayList<Object>();
		if(StringUtils.isNotBlank(crewName)){
			crewName = crewName.replaceAll("_", "\\\\_");
			crewName = crewName.replaceAll("%", "\\\\%");
			sql += "and crew.crewName like ? ";
			params.add("%" + crewName + "%");
		}
		sql += "  group by crew.crewId,crew.crewName ";
		return this.query(sql, params.toArray(),  null);
	}
	
	
	/**
	 * 剧组的模糊查询
	 * 查询的结果中不包括以下类型的剧组：用户正在申请中的、用户已经在里面的（不管过期和无效）、过期的
	 * @param keyword 关键字
	 * @param userId 用户ID
	 * @param page 分页信息
	 */
	public List<Map<String, Object>> queryCrewInfoByKeyword(String keyword, String userId, Page page){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tci.crewName, ");
		sql.append(" 	tci.company, ");
		sql.append(" 	tci.shootStartDate, ");
		sql.append(" 	tci.shootEndDate, ");
		sql.append(" 	tci.`status`, ");
		sql.append(" 	tci.director, ");
		sql.append(" 	tci.scriptWriter, ");
		sql.append(" 	tci.mainactor AS mainActorNames, ");
		sql.append(" 	tci. SUBJECT AS subjectName, ");
		sql.append(" 	tci.crewId, ");
		sql.append(" 	tci.picPath, ");
		sql.append(" 	GROUP_CONCAT(tui.realName, IF (tui.phone IS NULL OR tui.phone = '', '', concat('(', tui.phone, ')'))ORDER BY tui.createTime) crewManagerInfo ");
		sql.append(" FROM ");
		sql.append(" 	tab_crew_info tci ");
		sql.append(" 	LEFT JOIN tab_crew_user_map tcum ON tcum.crewId = tci.crewId ");
		sql.append(" 	LEFT JOIN tab_user_role_map turm ON tcum.userId = turm.userId AND turm.crewId = tci.crewId AND turm.roleId = '1' ");
		sql.append(" 	LEFT JOIN tab_user_info tui ON tui.userId = turm.userId ");
		sql.append(" WHERE ");
		sql.append(" 	tci.crewName LIKE ? ");
		sql.append(" AND tci.crewId NOT IN ( ");
		sql.append(" 	SELECT ");
		sql.append(" 		crewId ");
		sql.append(" 	FROM ");
		sql.append(" 		tab_crew_user_map ");
		sql.append(" 	WHERE ");
		sql.append(" 		userId = ? ");
		sql.append(" ) ");
		sql.append(" AND tci.crewId NOT IN ( ");
		sql.append(" 	SELECT ");
		sql.append(" 		aimCrewId ");
		sql.append(" 	FROM ");
		sql.append(" 		tab_joinCrew_applyMsg tja ");
		sql.append(" 	WHERE ");
		sql.append(" 		tja.applyerId = ? ");
		sql.append(" 	AND tja. STATUS = 1 ");
		sql.append(" ) ");
		sql.append(" AND tci.startDate <= CURDATE() ");
		sql.append(" AND tci.endDate >= CURDATE() ");
		sql.append(" GROUP BY ");
		sql.append(" 	tci.crewName, ");
		sql.append(" 	tci.company, ");
		sql.append(" 	tci.shootStartDate, ");
		sql.append(" 	tci.shootEndDate, ");
		sql.append(" 	tci.`status`, ");
		sql.append(" 	tci.director, ");
		sql.append(" 	tci.scriptWriter, ");
		sql.append(" 	tci.mainactor, ");
		sql.append(" 	tci. SUBJECT, ");
		sql.append(" 	tci.crewId ");
		sql.append(" ORDER BY ");
		sql.append(" 	tci.createTime DESC ");
		
		keyword = "%" + keyword + "%";
		return this.query(sql.toString(), new Object[] {keyword, userId, userId}, page);
	}
	
	/**
	 * 删除用户与权限关联
	 * @param userId
	 * @param crewId
	 */
	public void deleteUserAuth (String crewId, String userId) {
		//删除用户权限
		String authsql = "delete from " + UserAuthMapModel.TABLE_NAME + " where crewId = ? and userId = ?";
	  	this.getJdbcTemplate().update(authsql, new Object[]{crewId, userId});
	}
	
	/**
	 * 删除用户和想要关注的演员的关联关系
	 * @param crewId
	 * @param userId
	 */
	public void deleteUserFocusRoleMap(String crewId, String userId) {
		String sql = "delete from " + UserFocusRoleMapModel.TABLE_NAME + " where crewId = ? and userId = ?";
		this.getJdbcTemplate().update(sql, new Object[]{crewId, userId});
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
	
	/**
	 * 删除场景角色与用户关联表
	 * @param crewId
	 * @param userId
	 */
	public void deleteCrewRoleUserMap(String crewId, String userId) {
		String sql = "DELETE FROM " + CrewRoleUserMapModel.TABLE_NAME + " WHERE crewId = ? AND userId = ?";
		this.getJdbcTemplate().update(sql, new Object[]{crewId, userId});
	}
	
	/**
	 * 删除剧组与用户关联
	 * @param crewId
	 * @param userId
	 */
	public void deleteCrewUserMap(String crewId, String userId) {
		String sql = "DELETE FROM " + CrewUserMapModel.TABLE_NAME + " WHERE crewId = ? AND userId = ?";
		this.getJdbcTemplate().update(sql, new Object[]{crewId, userId});
	}
	
	/**
	 * 根据条件查询剧组,
	 * @param userId 所要排除的用户id
	 */
	public List<Map<String,Object>> searchAllCrew(CrewInfoFilter filter, String userId){
		List<String> paramList = new ArrayList<String>();
		paramList.add(userId);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT crew.crewId,crew.crewName,crew.subject,crew.shootlocation,crew.director,crew.status, ");
		sb.append("crew.company,crew.mainactor,crew.crewType,crew.startDate,crew.endDate,if(crew.endDate>=CURDATE(),1,0) isValid ");
		sb.append(" FROM tab_crew_info crew WHERE crew.crewId  ");
		sb.append("NOT IN (SELECT crewId FROM tab_crew_user_map WHERE userId = ?)  ");
//		sb.append("and (crew.status=1 or crew.status=0) "); //筹备中、拍摄中
//		sb.append(" and crew.endDate>=CURDATE()"); //有效
		if(StringUtils.isNotBlank(filter.getCrewName())){
			String crewName= filter.getCrewName();
			crewName = crewName.replaceAll("_", "\\\\_");
			crewName = crewName.replaceAll("%", "\\\\%");
			sb.append("and crew.crewName like ? ");
			paramList.add("%" + crewName + "%");
		}
		if(StringUtils.isNotBlank(filter.getCrewType())){
			sb.append("and crew.crewType = ? ");
			paramList.add(filter.getCrewType());
		}
		if(StringUtils.isNotBlank(filter.getStatus())){
			sb.append("and crew.status = ? ");
			paramList.add(filter.getStatus());
		}
		sb.append(" group by crew.crewId,crew.crewName,crew.subject,crew.shootlocation,crew.director,crew.status,crew.company,crew.mainactor,crew.crewType,crew.startDate,crew.endDate ");
		sb.append(" order by isValid desc,convert(crew.crewName using gbk) ");
		List<Map<String, Object>> li = this.query(sb.toString(), paramList.toArray(), null);
		return li;
	}
	
	/**
	 * 查询所有的剧组ID
	 * @return
	 */
	public List<Map<String, Object>> queryAllCrewIdAndName() {
		String sql = "select crewId,crewName from tab_crew_info";
		return this.query(sql, null, null);
	}
	
	/**
	 * 查询未刷新过权限的过期剧组
	 * @return
	 */
	public List<CrewInfoModel> queryExpiredCrewNeedRefreshAuth() {
		String sql = "select tci.* from tab_crew_info tci where tci.endDate < CURDATE() and refreshAuth = 0";
		return this.query(sql, null, CrewInfoModel.class, null);
	}
	
	/**
	 * 更新处理过的过期剧组的“是否已刷新权限”
	 * @param crewIds
	 */
	public void updateExpiredCrewStatus(String crewIds) {
		String sql = "update tab_crew_info set refreshAuth = 1 where crewId in ('" + crewIds + "')";
		this.getJdbcTemplate().update(sql);
	}
	
	/**
	 * 查询所有的试用剧组
	 * @return
	 */
	public List<CrewInfoModel> queryAllTrialCrew() {
		String sql = "select * from tab_crew_info where projectType=" 
				+ ProjectType.Trial.getValue(); 
				//+ " and startDate <= CURDATE() and endDate >= CURDATE()";
		return this.query(sql, null, CrewInfoModel.class, null);
	}
}
