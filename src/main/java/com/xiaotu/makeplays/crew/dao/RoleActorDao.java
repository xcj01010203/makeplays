package com.xiaotu.makeplays.crew.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.roleactor.model.ActorInfoModel;
import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;
import com.xiaotu.makeplays.roleactor.model.constants.ViewRoleType;
import com.xiaotu.makeplays.sysrole.model.SysroleInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 该类废弃，里面方法需要一一废弃，待全部没用的时候再删除
 */
@Deprecated
@Repository
public class RoleActorDao extends BaseDao<SysroleInfoModel> {
 
	/**
	 * lma 查询演员角色管理集合
	 * @param crewId 剧组ID
	 * @param roletype 演员角色
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getRoleActorList(String crewId,Integer roletypes,Integer byPage,Integer byview,Integer leave, String viewRoleName, Page page,Integer viewAccountStart,Integer viewAccountEnd,Integer sortStyle)throws Exception{
		String roletype="";
		if(roletypes!=null && roletypes!=0){
			roletype+=" AND role.viewRoleType='"+roletypes+"'";
		}
		String byviewStr="DESC",byPages="DESC";
		if(byview!=null && byview==1){
			byviewStr="ASC";
		}
		if(byPage!=null && byPage==1){
			byPages="ASC";
		}
		/*String lmaId="";
		if(leave!=null && leave==1){//有请假记录
			lmaId=" WHERE actor_temp.lmaId  is not null ";
		}else if(leave!=null &&leave==2){//无请假记录
			lmaId=" WHERE actor_temp.lmaId  is  null ";
		}*/
		String viewRoleNameCondition = "";
		if (!StringUtils.isBlank(viewRoleName)) {
			viewRoleNameCondition = " and role.viewRoleName like '%" + viewRoleName + "%'";
		}
		
		String sql="SELECT r_temp.*,actor_temp.actorId,actorName,enterDate,leaveDate,leaveDays,daysCount "+
				" ,coun.count,(r_temp.crewAmountByview-if(coun.count is null,0,coun.count)) nocount"+//new sql
				" FROM " +
				"(SELECT * from "+
				"( SELECT role.*, COUNT(view.viewId) crewAmountByview, ROUND(SUM(pageCount),2) crewAmountByPage, `view`.shootStatus " +
				" FROM tab_view_role role LEFT JOIN (tab_view_role_map srm,tab_view_info view) ON role.viewRoleId=srm.viewRoleId AND srm.viewId=view.viewId WHERE role.crewId=? " +roletype+ viewRoleNameCondition +
				" GROUP BY role.viewRoleId " +
				" )AS r_temp1   " ;
				if(viewAccountStart!= null || viewAccountEnd!= null) {
					sql+=" where ";
					if(viewAccountStart== null){
						sql+=" r_temp1.crewAmountByview<= " +viewAccountEnd;
					}else if(viewAccountEnd== null){
						sql+=" r_temp1.crewAmountByview>= " +viewAccountStart;
					}else{
						sql+=" r_temp1.crewAmountByview>="+viewAccountStart+" and r_temp1.crewAmountByview<="+viewAccountEnd+" ";
					}
				}
		//"r_temp1.crewAmountByview>50 and r_temp1.crewAmountByview<150 "+
		sql+=")AS r_temp 	LEFT JOIN (  tab_actor_role_map ram,	" +
				" ( SELECT actor.actorId actorId,aa.actorId lmaId,actor.actorName actorName,enterDate,leaveDate,SUM(DATEDIFF(leaveEndDate,DATE_SUB(leaveStartDate,INTERVAL 1 DAY))) leaveDays,count(leaveDays) daysCount "+
				" FROM tab_actor_info actor LEFT JOIN tab_actor_attendance aa ON actor.actorId=aa.actorId WHERE actor.crewId=? GROUP BY actor.actorId	) AS actor_temp ) ON"+
				" ( r_temp.viewRoleId=ram.viewRoleId AND ram.actorId=actor_temp.actorId )" +//+lmaId+
				//new sql
				" LEFT JOIN (SELECT role.viewRoleId,count(*) as count FROM tab_view_role role LEFT JOIN (tab_view_role_map srm,tab_view_info view) ON"+ 
				" (role.viewRoleId=srm.viewRoleId AND srm.viewId=view.viewId) WHERE role.crewId=? and `view`.shootStatus in(2,3) GROUP BY role.viewRoleId ) as coun"+
				" ON r_temp.viewRoleId=coun.viewRoleId";
			if(sortStyle == 0){
				sql += "  ORDER BY viewRoleType, crewAmountByview "+byviewStr+", crewAmountByPage "+byPages+", r_temp.viewRoleId ASC ";
			}else{
				if(sortStyle == 1){
					sql += "  ORDER BY CONVERT( r_temp.viewRoleName USING gbk ) asc ";
				}else if(sortStyle == 2){
					sql += "  ORDER BY CONVERT( r_temp.viewRoleName USING gbk ) desc ";
				}
			}	
		
		return	this.query(sql, new Object[]{crewId,crewId,crewId}, page);
	}
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getcrewShootDate(String crewId)throws SQLException{
		String sql="SELECT shootStartDate,shootEndDate FROM tab_crew_info WHERE crewId=? ";
		return this.query(sql, new Object[]{crewId}, null);
	}
	/**
	 * 查询角色是否存在
	 */
	public List<Map<String, Object>> getcrewRoleName(String name,String crewId)throws SQLException{
		String sql="SELECT count(viewRoleName) count FROM tab_view_role WHERE viewRoleName=? and crewId=?";
		return this.query(sql,new Object[]{name,crewId}, null);
	}
	/**
	 * lma 添角色演员关联
	 */
	public void addRoleActor(ViewRoleModel role,ActorInfoModel actor)throws Exception{
		String sql="insert into tab_actor_role_map values(?,?,?,?)";
		this.getJdbcTemplate().update(sql, new Object[]{UUIDUtils.getId(),actor.getActorId(),role.getViewRoleId(),role.getCrewId()});
	}
	/**
	 * 删除角色演员关联关系
	 */
	public void deleteRole(String actorId)throws Exception{
		String actorSql="delete from  tab_actor_role_map where actorId=? ";
		this.getJdbcTemplate().update(actorSql, new Object[]{actorId});
	}
	/**
	 * 删除角色
	 */
	public void deleteViewRole(String viewRoleId)throws Exception{
		String sql="delete from tab_view_role where viewRoleId=? ";
		this.getJdbcTemplate().update(sql, new Object[]{viewRoleId});
	}
	/**
	 * 删除场景角色关联
	 */
	public void deleteViewRoleMap(String viewRoleId)throws Exception{
		String sql="delete from tab_view_role_map where viewRoleId=? ";
		this.getJdbcTemplate().update(sql, new Object[]{viewRoleId});
	}
	/**
	 * 删除演员
	 */
	public void deleteActor(String actorId)throws Exception{
		String sql="delete from tab_actor_info where actorId=? ";
		this.getJdbcTemplate().update(sql, new Object[]{actorId});
	}
	/**
	 * 查询演员出勤表
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getleaveList(String crewId,String actorId)throws Exception{
		String sql="SELECT * FROM tab_actor_attendance WHERE actorId=? AND crewId=?";
		return this.query(sql, new Object[]{actorId, crewId},null);
	}
	/**
	 *  删除请假记录 attendanceDelet
	 */
	public  void deletLeave(String attendanceId ,String crewId)throws Exception{
	    String sql="delete from tab_actor_attendance where attendanceId=? and crewId=? ";
		this.getJdbcTemplate().update(sql,new Object[]{attendanceId,crewId});
		
	}
	
	/**
	 * 设定演员角色的角色类型
	 * @param viewRoleIds 演员角色IDs
	 * @param viewRoleType 角色类别
	 */
	public void specifiedRoleType (String viewRoleIds, String viewRoleType) {
		viewRoleIds = viewRoleIds.replace(",", "','");
		viewRoleIds = "'" + viewRoleIds + "'";
		
		String sql = "update " + ViewRoleModel.TABLE_NAME + " set viewRoleType = ? where viewRoleId in(" + viewRoleIds + ")";
		
		this.getJdbcTemplate().update(sql, new Object[] {viewRoleType});
	}
	
	/**
	 * 首页统计数据
	 */
	public List<Map<String,Object>> getIndexCount(String crewId){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT role.viewRoleName, COUNT(view.viewId) crewAmountByview ,IF(coun.count is null,0,coun.count) as endcount ");
		sql.append("FROM tab_view_role role  ");
		sql.append("LEFT JOIN (tab_view_role_map srm,tab_view_info view) ON role.viewRoleId=srm.viewRoleId AND srm.viewId=view.viewId  ");
		sql.append("LEFT JOIN ( ");
		sql.append("SELECT role.viewRoleId,count(*) as count FROM tab_view_role role  ");
		sql.append("LEFT JOIN (tab_view_role_map srm,tab_view_info view) ON (role.viewRoleId=srm.viewRoleId AND srm.viewId=view.viewId)  ");
		sql.append("WHERE role.crewId=? and `view`.shootStatus in(2,3) GROUP BY role.viewRoleId ) as coun ON role.viewRoleId=coun.viewRoleId  ");
		sql.append("WHERE role.crewId=?  GROUP BY role.viewRoleId ORDER BY crewAmountByview DESC LIMIT 0,4 ");
		List<Map<String,Object>> li = this.query(sql.toString(), new Object[]{crewId,crewId}, null);
		return li;
	}
	/**
	 * 查询场景角色信息
	 * @param crewId
	 * @return list
	 */
	public List<Map<String, Object>> getViewRoleList(String crewId)throws SQLException{
		
		//String sql="SELECT viewRoleId,viewRoleName From tab_view_role  WHERE crewId=? and viewRoleType!=? ORDER BY viewRoleType";
		String sql=" SELECT *,count(viewRoleName) AS count From tab_view_role_map map LEFT JOIN tab_view_role role ON map.viewRoleId=role.viewRoleId " +
				" WHERE  map.crewId=? and role.viewRoleType!=?" +
				" GROUP BY role.viewRoleName  ORDER BY count desc,role.viewRoleType";
		List<Map<String, Object>> list=	this.query(sql, new Object[]{crewId, ViewRoleType.MassesActor.getClass()}, null);
		return list;
		
		
	}
	
	/**
	 * 根据演员ID查询演员信息
	 * @param actorId
	 * @return
	 */
	public ActorInfoModel queryActorInfoById(String actorId) {
		String sql = "select * from " + ActorInfoModel.TABLE_NAME + " where actorId = ?";
		
		ActorInfoModel actorInfo = null;
		Object[] args = new Object[] {actorId};
		if (getResultCount(sql, args) == 1) {
			actorInfo =this.getJdbcTemplate().queryForObject(sql, new Object[] {actorId}, ParameterizedBeanPropertyRowMapper
					.newInstance(ActorInfoModel.class));
		}
		
		return actorInfo;
	}
	/**
	 * 查询场景角色与用户关联表是否存在
	 * @param viewRoleId
	 * @return
	 */
	public Integer queryCrewRoleId(String viewRoleId)throws SQLException {
		String sql="SELECT count(viewRoleId) as count FROM tab_crewRole_user_map WHERE viewRoleId=?";
		List<Map<String, Object>> list=	this.query(sql, new Object[]{viewRoleId}, null);
		return Integer.parseInt(list.get(0).get("count").toString());
	}
	
	/**
	 * 根据角色id获取角色戏量（总戏量、已完成戏量）
	 */
	public List<Map<String,Object>> getViewRoleCountById(String crewId,String viewRoleId){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT role.viewRoleName, COUNT(view.viewId) crewAmountByview ,IF(coun.count is null,0,coun.count) as endcount ");
		sql.append("FROM tab_view_role role  ");
		sql.append("LEFT JOIN (tab_view_role_map srm,tab_view_info view) ON role.viewRoleId=srm.viewRoleId AND srm.viewId=view.viewId  ");
		sql.append("LEFT JOIN ( ");
		sql.append("SELECT role.viewRoleId,count(*) as count FROM tab_view_role role  ");
		sql.append("LEFT JOIN (tab_view_role_map srm,tab_view_info view) ON (role.viewRoleId=srm.viewRoleId AND srm.viewId=view.viewId)  ");
		sql.append("WHERE role.crewId=? AND `view`.shootStatus IN (2, 3) GROUP BY role.viewRoleId ) as coun ON role.viewRoleId=coun.viewRoleId  ");
		sql.append("WHERE role.crewId=? AND role.viewRoleId = ?  GROUP BY role.viewRoleId ORDER BY crewAmountByview DESC ");
		List<Map<String,Object>> li = this.query(sql.toString(), new Object[]{crewId,crewId,viewRoleId}, null);
		return li;
	}
}
