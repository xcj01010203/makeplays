package com.xiaotu.makeplays.user.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.crew.model.CrewUserFilter;
import com.xiaotu.makeplays.user.controller.filter.UserFilter;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.model.constants.UserStatus;
import com.xiaotu.makeplays.user.model.constants.UserType;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.utils.UUIDUtils;

@Repository
public class UserInfoDao extends BaseDao<UserInfoModel> {
	/**
	 * 根据多个条件查询道具信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<UserInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + UserInfoModel.TABLE_NAME + " where 1 = 1 ");
		
		Set<String> keySet = conditionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		List<Object> conList = new LinkedList<Object>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = conditionMap.get(key);
			sql.append(" and " + key + " = ?");
			conList.add(value);
		}
		Object[] objArr = conList.toArray();
		List<UserInfoModel> userInfoList = this.query(sql.toString(), objArr, UserInfoModel.class, page);
		
		return userInfoList;
	}
	
	/**
	 * 分页查询用户列表
	 * @param page
	 * @param userFilter
	 * @param sortdatafield
	 * @param sortorder
	 * @return
	 */
	public List<Map<String, Object>> queryUserListByPage(Page page, UserFilter userFilter, String sortdatafield, String sortorder){
		List<Object> params = new ArrayList<Object>();
		String sql = "select * from (" 
				+ "select tui.userName,tui.userId,tui.realName,tui.type,tui.ubCreateCrewNum,tui.sex,tui.phone,tui.email,"
				+ "tui.status,tui.token,tui.clientType,tui.appVersion,"
				+ "if(GROUP_CONCAT(if(tcum.crewId='0',null,tcum.crewId)) is null,0,1) as isCrew,"
				+ "GROUP_CONCAT(distinct turm.roleId) as roleIds,"
				+ "count(distinct if(tcum.crewId='0',null,tcum.crewId)) crewNum," 
				+ "count(distinct if(tcum.crewId='0' or tci.endDate < CURDATE(),null,tcum.crewId)) enabledCrewNum,"
				+ "count(distinct if(tcum.crewId='0' or tcum.status=99,null,tcum.crewId)) userCrewNum,"
				+ "count(distinct if(tcum.crewId='0' or tcum.status=99 or tci.endDate < CURDATE(),null,tcum.crewId)) userEnabledCrewNum,"
				+ "tui.createTime "
				+ " from tab_user_info tui "
				+ " left join tab_crew_user_map tcum on tui.userId = tcum.userId "
				+ " left join tab_crew_info tci on tci.crewId=tcum.crewId "
				+ " left join tab_user_role_map turm on turm.userId = tui.userId "
				+ " where 1=1 ";
		if (StringUtils.isNotBlank(userFilter.getPhone())
				&& StringUtils.isNotBlank(userFilter.getRealName())) {
			String realName = userFilter.getRealName();
			realName = realName.replaceAll("%", "\\\\%");
			realName = realName.replaceAll("_", "\\\\_");
			String phone = userFilter.getPhone();
			phone = phone.replaceAll("%", "\\\\%");
			phone = phone.replaceAll("_", "\\\\_");
			sql += "and (tui.phone like ? or tui.realName like ?) ";
			params.add(realName + "%");
			params.add(phone + "%");
		}
		if (StringUtils.isNotBlank(userFilter.getPhone())
				&& StringUtils.isBlank(userFilter.getRealName())) {
			String phone = userFilter.getPhone();
			phone = phone.replaceAll("%", "\\\\%");
			phone = phone.replaceAll("_", "\\\\_");
			sql += "and tui.phone like ? ";
			params.add(phone + "%");
		}
		if (StringUtils.isBlank(userFilter.getPhone())
				&& StringUtils.isNotBlank(userFilter.getRealName())) {
			String realName = userFilter.getRealName();
			realName = realName.replaceAll("%", "\\\\%");
			realName = realName.replaceAll("_", "\\\\_");
			sql += "and tui.realName like ? ";
			params.add(realName + "%");
		}
		if(userFilter.getStatus() != null) {//用户状态
			sql += " and tui.status = ? ";
			params.add(userFilter.getStatus());
		}
		sql += " group by tui.userName,tui.userId,tui.realName,tui.sex,tui.phone,tui.email) mytable ";
		

		if(StringUtils.isNotBlank(userFilter.getUserType())) {//用户类型
			String userType = userFilter.getUserType();
			String[] userTypeArr = userType.split(",");
			String userTypeSql = "";
			for(int i = 0; i < userTypeArr.length; i++) {
				if(userTypeArr[i].equals(UserType.CrewUser.getValue() + "") || userTypeArr[i].equals(UserType.Admin.getValue() + "")) {//普通用户、系统管理员
					userTypeSql += " or type = " + userTypeArr[i];
				} else {//客服
					userTypeSql += " or roleIds = " + userTypeArr[i];
				}
			}
			sql += " where " + userTypeSql.substring(" or".length());
		}

		//排序
		if(StringUtil.isNotBlank(sortdatafield) && StringUtil.isNotBlank(sortorder)) {
			sql += " ORDER BY " + sortdatafield + " " + sortorder;
		} else {
			sql += " ORDER BY createTime desc ";
		}

		return this.query(sql, params.toArray(), page);
		
	}
	
	
	public UserInfoModel queryUserByNameAndPsd(String loginName,String password){
		
		String sql = "select * from tab_user_info where (username=? or phone=?) and password=?";
		
		List<UserInfoModel> list = this.query(sql, new Object[]{loginName,loginName, password}, UserInfoModel.class, null);
		
		if(null == list||list.size()==0){
			return null;
		}
		
		return list.get(0);
	}
	
	/**
	 * 根据用户名/手机号查询用户
	 * 如果userId为不为空，则查询非自己的用户
	 * @param loginName
	 * @param userId
	 * @return
	 */
	public UserInfoModel queryUserByLoginName(String loginName, String userId){
		String str="";
		if(StringUtils.isNotBlank(userId)){
			str=" and userId != '"+userId+"' ";
		}
		String sql = "select * from tab_user_info where (username=? or phone=?) "+str;
		
		List<UserInfoModel> list = this.query(sql, new Object[]{loginName, loginName}, UserInfoModel.class, null);
		
		if(list == null || list.size() == 0){
			return null;
		}
		
		return list.get(0);
	}
	
	/**
	 * 根据ID查询用户信息
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public UserInfoModel queryById(String userId) throws Exception{
		String sql = "select * from tab_user_info where userid=?";
		
		return this.queryForObject(sql, new Object[]{userId}, UserInfoModel.class);
	}
	
	/**
	 * 添加用户权限
	 */
	public void addUserAuth(String authIds,String userId,String roleId,String crewId){
		//删除用户权限
		String sql = "delete from tab_user_auth_map where userId = ? and crewId = ?";
		this.getJdbcTemplate().update(sql, new Object[]{userId,crewId});
		//为用户添加权限
		String sqlAdd = "insert into tab_user_auth_map(map,authId,userId,crewId) values(?,?,?,?,?)";
		if(StringUtils.isBlank(authIds)){
			String sqlRole="select tsa1.authId as `id`,tsa1.parentid as parentid,tsa1.authName as `text`,tsa1.authUrl as `value`,tsa1.operType as operType ,tsa1.ifmenu,tsa1.sequence " +
					"from tab_sys_authority tsa1 ,tab_role_auth_map tram,tab_sysrole_info tsri " +
					"where tsa1.ifmenu=1 and tsa1.authId = tram.authId and tram.roleId=tsri.roleId and tsri.roleId=? ";
			List<Map<String,Object>> li = this.query(sqlRole, new Object[]{roleId}, null);
			if(li != null){
				for(int i = 0;i<li.size();i++){
					Map<String,Object> map = li.get(i);
					String id = UUIDUtils.getId();
					this.getJdbcTemplate().update(sqlAdd, new Object[]{id,map.get("id").toString(),userId,crewId});
				}
			}
		}else{
			String[] auths = authIds.split(",");
			for(int j = 0;j < auths.length;j++){
				String[] s = auths[j].split("-");
				String id = UUIDUtils.getId();
				this.getJdbcTemplate().update(sqlAdd, new Object[]{id,s[0],s[1],userId,crewId});
			}
		}
		
	}
	/**
	 * 删除剧组用户权限
	 */
	public void deleteUserAuth(String crewId,String userId){
		//删除用户权限
		String sql = "delete from tab_user_auth_map where userId = ? and crewId = ?";
		this.getJdbcTemplate().update(sql, new Object[]{userId,crewId});
	}
	/**
	 * 添加剧组用户权限 
	 */
	public void addUserAuth(String authId,Integer authType,String userId,String crewId){
		//为用户添加权限
		String sqlAdd = "insert into tab_user_auth_map(mapId,authId,userId,crewId) values(?,?,?,?)";
		String id = UUIDUtils.getId();
		this.getJdbcTemplate().update(sqlAdd, new Object[]{id,authId,userId,crewId});
	}
	
	/**
	 * 查询用户所在剧组是否过期
	 */
	public Integer getCrewStatus(String crewId){
		String sql="SELECT DATEDIFF(endDate,NOW()) shotDays FROM tab_crew_info WHERE crewId=?";
		List<Map<String, Object>> list= this.query(sql, new Object[]{crewId}, null);
		return Integer.parseInt(list.get(0).get("shotDays").toString());
	}
	
	/**
	 * 验证手机号是否重复
	 */
	public boolean validatePhone(String phone,String userId){
		String str="";
		if(StringUtils.isNotBlank(userId)){
			str=" and userId != '"+userId+"' ";
		}
		boolean b = false;
		String sql = "SELECT 1 FROM tab_user_info WHERE phone = ? or userName=? "+str;
		List<Map<String,Object>> li = this.query(sql, new Object[]{phone,phone}, null);
		if(li.size() > 0){
			b = true;
		}
		return b;
	}
	
	/**
	 * 根据手机号修改密码
	 */
	public void updatePasswordByPhone(String phone, String password){
		String sql = "update tab_user_info SET password = ? WHERE phone = ? ";
		this.getJdbcTemplate().update(sql, new Object[]{password, phone});
	}
	
	/**
	 * 查询当前剧组下所有生效的剧组成员
	 * @param crewId
	 * @return
	 */
	public List<UserInfoModel> queryValidUserListByCrewId(String crewId) {
		String sql = "select tui.* from tab_user_info tui, tab_crew_user_map tcum where tui.userId = tcum.userId and tcum.crewId = ? and tui.`status` = 1";
		return this.query(sql, new Object[] {crewId}, UserInfoModel.class, null);
	}
	
	/**
	 * 查询当前剧组下非演员用户
	 * 该查询不仅查询出用户的基本信息，
	 * 还会查询用户的职位信息
	 * @param crewId
	 * @param userId 评价人ID
	 * @return
	 */
	public List<Map<String, Object>> queryNotActorUserListbyCrewIdWithRole(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" 		SELECT ");
		sql.append(" 			tui.userId, ");
		sql.append(" 			tui.userName, ");
		sql.append("            tui.realName, ");
		sql.append(" 			GROUP_CONCAT(tsi.roleName) roleNames ");
		sql.append(" 		FROM ");
		sql.append(" 			tab_user_info tui, ");
		sql.append(" 		tab_crew_user_map tcum, ");
		sql.append(" 		tab_sysrole_info tsi, ");
		sql.append(" 		tab_user_role_map turm ");
		sql.append(" 	WHERE 1=1");
		sql.append("    AND tui.userId = tcum.userId ");
		sql.append(" 	AND turm.userId = tui.userId ");
		sql.append(" 	AND turm.roleId = tsi.roleId ");
		sql.append(" 	AND turm.crewId = ? ");
		sql.append(" 	AND tui.`status` = 1 ");
		sql.append(" 	AND tcum.crewId = ? ");
		sql.append(" 	AND NOT EXISTS ( ");
		sql.append(" 		SELECT ");
		sql.append(" 			1 ");
		sql.append(" 		FROM ");
		sql.append(" 			tab_crewRole_user_map tcrum ");
		sql.append(" 		WHERE ");
		sql.append(" 			tui.userId = tcrum.userId ");
		sql.append(" 		AND tcrum.crewId = ? ");
		sql.append(" 	) ");
		sql.append(" 	GROUP BY ");
		sql.append(" 		tui.userId, ");
		sql.append(" 		tui.userName ");
		
		return this.query(sql.toString(), new Object[] {crewId, crewId, crewId}, null);
	}
	
	/**
	 * 根据条件检索用户ee搜索
	 */
	public List<Map<String, Object>> searchAllUser(CrewUserFilter filter,
			Page page,String crewId) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT res.userId,res.userName,res.realName,GROUP_CONCAT(res.roleName) as roleName,GROUP_CONCAT(res.roleId) as roleId,res.phone,res.status,res.groupName,res.isContactInfo FROM ( ");
		sb.append("SELECT pum.userId,usr.userName,usr.realName,role.roleName,role.roleId,usr.phone,usr.status,if(groups.groupName is null,'全组',groups.groupName) groupName,pum.isContactInfo  ");
		sb.append("FROM tab_crew_user_map pum LEFT JOIN tab_user_info usr ON ");
		sb.append("pum.userId=usr.userId LEFT JOIN tab_user_role_map urm ON urm.userId=pum.userId  ");
		sb.append("LEFT JOIN tab_sysrole_info role ON role.roleId=urm.roleId  ");
		sb.append("LEFT JOIN tab_shoot_group groups ON groups.groupId=usr.groupId   ");
		sb.append("WHERE pum.userId not in (SELECT userId FROM tab_crew_user_map WHERE crewId = ?)  ");   //and pum.crewId='5fd484a88f1842dbbb1da386879899fc'  AND urm.crewId = '5fd484a88f1842dbbb1da386879899fc'
		if(StringUtils.isNotBlank(filter.getCrewId())){
			sb.append("and pum.crewId= '"+filter.getCrewId()+"' ");
			sb.append("and urm.crewId= '"+filter.getCrewId()+"' ");
		}
		if(StringUtils.isNotBlank(filter.getRoleId())){
			String roleid = "'"+filter.getRoleId().replaceAll(",", "','")+"'";
			sb.append("and role.roleId in ("+roleid+") ");
		}
		if(StringUtils.isNotBlank(filter.getRealName())){
			sb.append("and usr.realName like '%"+filter.getRealName()+"%' ");
		}
		if(StringUtils.isNotBlank(filter.getPhone())){
			sb.append("and usr.phone= '"+filter.getPhone()+"' ");
		}
		sb.append("group by pum.userId,role.roleId,usr.userName,usr.realName,usr.phone,usr.status ");
		
		sb.append(") AS res group by res.userId,res.userName,res.realName ");
		sb.append(" order by convert(res.realName using gbk) ");
		List<Map<String, Object>> li = this.query(sb.toString(), new Object[]{crewId}, page);
		return li;

	}
	
	/**
	 * 获取当前用户下剧组名称、剧组id
	 */
	public List<Map<String, Object>> queryCrewPartMes(String userId) {
		String sql = "SELECT tcum.crewId,tci.crewName " +
				"FROM tab_crew_user_map tcum,tab_crew_info tci " +
				"WHERE tcum.crewId = tci.crewId " +
				"AND tcum.userId = ? " +
				"AND tcum.status=? " +
				"AND tci.startDate <= CURDATE() AND tci.endDate >= CURDATE() " +
				"order by tcum.ifDefault desc ";
		List<Map<String, Object>> li = this.query(sql, new Object[]{userId,Constants.STATUS_OK}, null);
		
		return li;
	}
	
	/**
	 * 获取用户所对应的剧组
	 */
	public List<Map<String,Object>> getCrewsByUserId(String userId){
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT tci.*,GROUP_CONCAT(tsi.roleName) AS roleName,tcu.crewId as tcrewId FROM tab_crew_user_map tcu  ");
		sb.append("LEFT JOIN tab_crew_info tci ON tcu.crewId = tci.crewId ");
		sb.append("LEFT JOIN tab_user_role_map tur ON tcu.userId = tur.userId AND tcu.crewId = tur.crewId ");
		sb.append("LEFT JOIN tab_sysrole_info tsi ON tur.roleId = tsi.roleId  ");
		sb.append("WHERE tcu.userId = ? ");
		sb.append("GROUP BY tcu.crewId  ORDER BY tci.createTime desc");
		List<Map<String,Object>> li = this.query(sb.toString(), new Object[]{userId}, null);
		return li;
	}
	
	/**
	 * 查询剧组下有指定权限的用户
	 * @param crewId	剧组ID
	 * @param authList	权限编码列表
	 * @param readonly 是否只读
	 * @return
	 */
	public List<Map<String, Object>> queryUserByCrewIdAndAuth(String crewId, List<String> authList, Boolean readonly) {
		String authStr = "";
		for (String auth : authList) {
			authStr += auth + ",";
		}
		authStr = authStr.substring(0, authStr.length() - 1);
		authStr = "'" + authStr.replaceAll(",", "','") + "'";
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tui.*, group_concat(distinct tsi.roleName) roleNames ");
		sql.append(" FROM ");
		sql.append(" 	tab_user_info tui, ");
		sql.append(" 	tab_sys_authority tsa, ");
		sql.append(" 	tab_user_auth_map tua, ");
		sql.append(" 	tab_crew_user_map tcum, ");
		sql.append("    tab_sysrole_info tsi, ");
		sql.append("    tab_user_role_map turm ");
		sql.append(" WHERE ");
		sql.append(" 	tua.userId = tui.userId ");
		sql.append(" AND tui.userId = turm.userId ");
		sql.append(" AND turm.roleId = tsi.roleId ");
		sql.append(" AND turm.crewId = ? ");
		sql.append(" AND tua.authId = tsa.authId ");
		sql.append(" AND tua.crewId = ? ");
		
		if (readonly != null) {
			if (readonly) {
				sql.append(" AND tua.readonly = 1 ");
			} else {
				sql.append(" AND tua.readonly = 0 ");
			}
		}
		
		sql.append(" AND tcum.userId = tui.userId ");
		sql.append(" AND tcum.crewId = ? ");
		sql.append(" AND tcum.status = 1 ");
		sql.append(" AND tsa.authCode IN ("+ authStr +") ");
		sql.append(" AND tsa.status = 0 ");
		/*sql.append(" AND tui.token != '' ");
		sql.append(" AND tui.token is not null ");*/
		sql.append(" AND tui.status = 1 ");
		sql.append(" GROUP BY tui.userId ");
		sql.append(" ORDER BY tcum.sequence");
		
		return this.query(sql.toString(), new Object[] {crewId, crewId, crewId}, null);
	}
	
	/**
	 * 根据用户登录名查询非自己的用户
	 * 该方法目前用于修改用户信息时检查是否有相同登录名的用户
	 * @param userId
	 * @param loginName
	 * @return
	 */
	public List<UserInfoModel> queryUserInfoByLoginNameExcepOwn(String userId, String loginName) {
		String sql = "select * from " + UserInfoModel.TABLE_NAME + " where (userName = ? or phone = ?) and userId != ?";
		return this.query(sql, new Object[] {loginName, loginName, userId}, UserInfoModel.class , null);
	}
	
	/**
	 * 根据用户姓名查询剧组中非自己的用户
	 * 该方法目前用于修改用户信息时检查是否有相同登录名的用户
	 * @param userId
	 * @param loginName
	 * @return
	 */
	public List<UserInfoModel> queryUserInfoByRealNameExcepOwn(String crewId, String userId, String realName) {
		String sql = "select tui.* from " + UserInfoModel.TABLE_NAME + " tui, tab_crew_user_map tcum  where tui.userId = tcum.userId and tcum.crewId = ? and tui.realName=? and tui.userId != ?";
		return this.query(sql, new Object[] {crewId, realName, userId}, UserInfoModel.class , null);
	}
	
	
	/**
	 * 根据多个用户ID查询用户列表，多个ID用英文逗号隔开
	 * @param userIds
	 * @return
	 */
	public List<UserInfoModel> queryByIds(String userIds) {
		userIds = "'" + userIds.replace(",",  "','") + "'";
		String sql = "select * from tab_user_info where userId in(" + userIds + ")";
		return this.query(sql, null, UserInfoModel.class, null);
	}
	
	/**
	 * 根据ID删除用户登录日志
	 * @param userId
	 */
	public void deleteUserLoginLogById(String userId) {
		String sql = "delete from tab_user_login_log where userId = ? ";
		this.getJdbcTemplate().update(sql, new Object[]{userId});
	}
	
	/**
	 * 根据ID删除用户
	 * @param userId
	 */
	public void deleteById(String userId){
		String sql = "delete from tab_user_info where userId = ? ";
		this.getJdbcTemplate().update(sql, new Object[]{userId});
	}
	
	/**
	 * 查询剧组下指定角色的用户信息
	 * 查询出的用户都是账号有效的且在剧组中也是有效的
	 * @param crewId
	 * @param roleId
	 * @return
	 */
	public List<UserInfoModel> queryByCrewRole(String crewId, String roleId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tui.* ");
		sql.append(" FROM ");
		sql.append(" 	tab_user_info tui, ");
		sql.append(" 	tab_crew_user_map tcum, ");
		sql.append(" 	tab_user_role_map turm ");
		sql.append(" WHERE ");
		sql.append(" 	tcum.crewId = ? ");
		sql.append(" AND tcum.`status` = 1 ");
		sql.append(" AND tcum.userId = tui.userId ");
		sql.append(" AND turm.userId = tui.userId ");
		sql.append(" AND turm.roleId = ? ");
		sql.append(" AND turm.crewId = ? ");
		sql.append(" AND tui.`status` = 1 ");
		
		return this.query(sql.toString(), new Object[] {crewId, roleId, crewId},  UserInfoModel.class, null);
	}
	
	/**
	 * 查询指定分组下的用户信息及其职务信息
	 * 只返回状态为有效的用户
	 * 在剧组中被冻结的用户也需要返回
	 * @param crewId
	 * @param groupId
	 * @return
	 */
	public List<Map<String, Object>> queryByCrewGroupId(String crewId, String groupId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tui.userId, tui.realName as userName, tui.phone, tsi.roleId, tsi.roleName, tcum.status,tcum.createTime ");
		sql.append(" FROM ");
		sql.append(" 	tab_user_info tui, ");
		sql.append(" 	tab_crew_user_map tcum, ");
		sql.append(" 	tab_user_role_map turm, ");
		sql.append(" 	tab_sysrole_info tsi ");
		sql.append(" WHERE ");
		sql.append(" 	tui.userId = tcum.userId ");
		sql.append(" AND tcum.crewId = ? ");
		sql.append(" AND tui.userId = turm.userId ");
		sql.append(" AND tui.status = 1 ");
		sql.append(" AND turm.roleId = tsi.roleId ");
		sql.append(" AND turm.crewId= ? ");
		
		if (groupId.equals(Constants.ROLE_ID_ADMIN) || groupId.equals(Constants.ROLE_ID_PROJECT_DIRECTOR)) {
			sql.append(" AND tsi.roleId = ? ");
		} else {
			sql.append(" AND tsi.parentId = ? "); 
		}
		
		sql.append(" ORDER BY tsi.orderNo, tcum.createTime, userName ");
		
		return this.query(sql.toString(), new Object[] {crewId, crewId, groupId}, null);
		
	}
	
	/**
	 * 查询剧组下小组中的所有人员信息
	 * 比如《天龙九部》剧组下导演组的所有用户信息
	 * @param crewId
	 * @param groupId
	 * @return
	 */
	public List<UserInfoModel> queryCrewUserByGroupId(String crewId, String groupId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	DISTINCT tui.* ");
		sql.append(" FROM ");
		sql.append(" 	tab_user_info tui, ");
		sql.append(" 	tab_sysrole_info tsi, ");
		sql.append(" 	tab_user_role_map turm ");
		sql.append(" WHERE ");
		sql.append(" 	turm.crewId = ? ");
		sql.append(" AND turm.roleId = tsi.roleId ");
		
		//如果是剧组管理员，则特殊处理
		if (groupId.equals("1") || groupId.equals("3")) {
			sql.append(" AND tsi.roleId = ? ");
		} else {
			sql.append(" AND tsi.parentId = ? ");
		}
		sql.append(" AND turm.userId = tui.userId ");
		
		return this.query(sql.toString(), new Object[] {crewId, groupId}, UserInfoModel.class, null);
	}
	
	/**
	 * 查询剧组下用户信息
	 * 该查询是按照部门职务的纬度查询用户信息
	 * 返回剧组中每个职务下的用户信息
	 * 如果用户在剧组中担任两个职务，则该查询将会返回两条用户记录
	 * 只返回状态为有效的用户
	 * @param crewId
	 * @param userId
	 * @return 用户所有基本信息、入组时间、职务ID、职务名称、部门ID、部门名称
	 */
	public List<Map<String, Object>> queryCrewUserListWithRole (String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tui.*, ");
		sql.append(" 	func_get_first_letter(tui.realName) fletter, ");
		sql.append(" 	tcum.createTime joinTime, ");
		sql.append(" 	tsi.roleId, ");
		sql.append(" 	tsi.roleName, ");
		sql.append(" 	ptsi.roleId proleId, ");
		sql.append(" 	ptsi.roleName proleName ");
		sql.append(" FROM ");
		sql.append(" 	tab_user_info tui, ");
		sql.append(" 	tab_crew_user_map tcum, ");
		sql.append(" 	tab_user_role_map turm, ");
		sql.append(" 	tab_sysrole_info tsi, ");
		sql.append(" 	tab_sysrole_info ptsi ");
		sql.append(" WHERE ");
		sql.append(" 	tcum.crewId = ? ");
		sql.append(" AND tcum.userId = tui.userId ");
		sql.append(" AND tui.userId = turm.userId ");
		sql.append(" AND tui.status = 1 ");
		sql.append(" AND turm.crewId = ? ");
		sql.append(" AND turm.roleId = tsi.roleId ");
		sql.append(" AND (tsi.parentId = ptsi.roleId OR (tsi.parentId='01' and tsi.roleId = ptsi.roleId)) ");
		sql.append(" ORDER BY ptsi.orderNo, tsi.orderNo, joinTime, userName ");
		
		return this.query(sql.toString(), new Object[] {crewId, crewId}, null);
	}
	
	/**
	 * 查询剧组下拥有某权限的用户信息
	 * 该查询是按照部门职务的纬度查询用户信息
	 * 返回剧组中每个职务下的用户信息
	 * 如果用户在剧组中担任两个职务，则该查询将会返回两条用户记录
	 * 只返回状态为有效的用户
	 * @param crewId
	 * @param authId
	 * @return 用户所有基本信息、入组时间、职务ID、职务名称、部门ID、部门名称
	 */
	public List<Map<String, Object>> queryUserListWithRoleByAuthId (String crewId, String authId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tui.*, ");
		sql.append(" 	tcum.createTime joinTime, ");
		sql.append(" 	tsi.roleId, ");
		sql.append(" 	tsi.roleName, ");
		sql.append(" 	ptsi.roleId proleId, ");
		sql.append(" 	ptsi.roleName proleName, ");
		sql.append(" 	tuam.readonly, ");
		sql.append(" 	if(tsa.differInRAndW=1 and tcam.readonly=0,1,0) as differInRAndW ");
		sql.append(" FROM ");
		sql.append(" 	tab_user_info tui, ");
		sql.append(" 	tab_crew_user_map tcum, ");
		sql.append(" 	tab_user_role_map turm, ");
		sql.append(" 	tab_sysrole_info tsi, ");
		sql.append(" 	tab_sysrole_info ptsi, ");
		sql.append(" 	tab_user_auth_map tuam, ");
		sql.append(" 	tab_sys_authority tsa, ");
		sql.append(" 	tab_crew_auth_map tcam");
		sql.append(" WHERE ");
		sql.append(" 	tcum.crewId = ? ");
		sql.append(" AND tcum.userId = tui.userId ");
		sql.append(" AND tui.userId = turm.userId ");
		sql.append(" AND tui.status = 1 ");
		sql.append(" AND turm.crewId = ? ");
		sql.append(" AND turm.roleId = tsi.roleId ");
		sql.append(" AND (tsi.parentId = ptsi.roleId OR (tsi.parentId='01' and tsi.roleId = ptsi.roleId)) ");
		sql.append(" AND tuam.userId=tui.userId ");
		sql.append(" AND tuam.authId = ? ");
		sql.append(" AND tuam.crewId = ? ");
		sql.append(" AND tsa.authId = tuam.authId ");
		sql.append(" AND tcam.crewId=? ");
		sql.append(" AND tcam.authId=tsa.authId ");
		sql.append(" ORDER BY ptsi.orderNo, tsi.orderNo, joinTime, userName ");
		
		return this.query(sql.toString(), new Object[] {crewId, crewId, authId, crewId, crewId}, null);
	}
	
	/**
	 * 根据手机号查询不在当前剧组中的用户
	 * 仅查询普通用户
	 * @param crewId
	 * @param phone
	 * @param loginUserType
	 * @return
	 */
	public List<UserInfoModel> queryNotOwnUserByPhone(String crewId, String phone, Integer loginUserType) {
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		sql.append(" select tui.* ");
		sql.append(" from tab_user_info tui ");
		sql.append(" where tui.type=0 ");
		if(loginUserType == 3) {
			sql.append(" and tui.phone=? ");
			params.add(phone);
		} else {
			sql.append(" and (tui.phone like ? or tui.realName like ?) ");
			phone = phone.replaceAll("%", "\\\\%");
			phone = phone.replaceAll("_", "\\\\_");
			params.add("%" + phone + "%");
			params.add("%" + phone + "%");
		}
		sql.append(" and not exists(select 1 from tab_crew_user_map tcum " 
				+ " where tcum.userId = tui.userId and tcum.crewId = ?) ");
		params.add(crewId);
		return this.query(sql.toString(), params.toArray(), null);
	}
	
	/**
	 * 查询剧组下的用户信息
	 * 剧组是未过期的、未停用的，用户是有效状态的，用户在剧组中是未冻结的
	 * @param crewId
	 * @param userId
	 * @return
	 */
	public List<UserInfoModel> queryByCrewUserId(String crewId, String userId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tui.* ");
		sql.append(" FROM ");
		sql.append(" 	tab_user_info tui, ");
		sql.append(" 	tab_crew_user_map tcum, ");
		sql.append(" 	tab_crew_info tci ");
		sql.append(" WHERE ");
		sql.append(" 	tcum.crewId = ? ");
		sql.append(" AND tcum.userId = ? ");
		sql.append(" AND tcum.userId = tui.userId ");
		sql.append(" AND tcum.`status` = 1 ");
		sql.append(" AND tui.`status` = 1 ");
		sql.append(" AND tci.crewId = tcum.crewId ");
		sql.append(" and tci.startDate <= CURDATE() ");
		sql.append(" AND tci.endDate >= CURDATE() ");
		sql.append(" AND tci.isStop = 0 ");
		
		return this.query(sql.toString(), new Object[] {crewId, userId}, UserInfoModel.class, null);
	}
	
	/**
	 * 根据手机号和密码查询用户
	 * @param phone
	 * @param password
	 * @return
	 * @throws Exception 
	 */
	public UserInfoModel queryByPhoneAndPwd(String phone, String password) throws Exception {
		String sql = "select * from " + UserInfoModel.TABLE_NAME + " where phone = ? and password = ?";
		return this.queryForObject(sql, new Object[] {phone, password}, UserInfoModel.class);
	}
	
	/**
	 * 根据手机号查询用户
	 * @param phone
	 * @return
	 * @throws Exception 
	 */
	public UserInfoModel queryByPhone(String phone) throws Exception {
		String sql = "select * from tab_user_info where phone = ?";
		return this.queryForObject(sql, new Object[] {phone}, UserInfoModel.class);
	}
	
	/**
	 * 清空用户token
	 * @param userId
	 */
	public void clearUserToken (String userId) {
		String sql = "update tab_user_info set token = null where userId = ?";
		this.getJdbcTemplate().update(sql, userId);
	}
	
	/**
	 * 新增用户IP地址
	 * @param userId
	 * @param userIp
	 */
	public void addUserIp(String userId, String userIp) {
		String sql = "update tab_user_info set ip = ? where userId = ?";
		this.getJdbcTemplate().update(sql, new Object[]{userIp, userId});
	}
	
	/**
	 * 添加用户IP地址
	 * @param userIp
	 */
	public void updateUserIp(String userId, String userIp) {
		String sql = "update tab_user_info set ip = concat(ip, ',', ?) where userId = ?";
		this.getJdbcTemplate().update(sql, new Object[]{userIp, userId});
	}
	
	/**
	 * 查询意见反馈需要推送的客服列表，包括总客服
	 * @param userId
	 * @return
	 */
	public List<UserInfoModel> queryUserListForFeedBack(String userId) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT *  ");
		sql.append(" FROM tab_user_info tui ");
		sql.append(" where tui.type= " + UserType.CustomerService.getValue());
		sql.append(" and status= " + UserStatus.Valid.getValue());
		sql.append(" and (tui.userId IN ( ");
		sql.append(" 	SELECT ");
		sql.append(" 		tcum.userId ");
		sql.append(" 	FROM ");
		sql.append(" 		tab_crew_user_map tcum ");
		sql.append(" 	LEFT JOIN tab_crew_user_map tcum1 ON tcum.crewId = tcum1.crewId ");
		sql.append(" 	WHERE ");
		sql.append(" 		tcum1.userId = ? ");
		sql.append(" ) or tui.userId IN (select userId from tab_user_role_map turm ");
		sql.append(" where roleId='" + Constants.ROLE_ID_CUSTOM_SERVICE + "')) ");
		return this.query(sql.toString(), new Object[]{userId}, UserInfoModel.class, null);
	}
}
