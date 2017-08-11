package com.xiaotu.makeplays.crew.dao;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.ReportConst;

/**
 * 角色戏量统计 dao
 * @author lma
 *
 */
@Deprecated
@Repository
public class RoleCrewAmountReportDao extends BaseDao<CrewInfoModel> {
	/**
	 * 获取每个角色每集的戏量及戏量合计
	 * @param crewId
	 * @param roleType
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> getStatisticsRolecrewAmoutBySet(String crewId,int roleType,String roleNames)throws SQLException{
		StringBuilder sql = new StringBuilder();
		String roleName="";
		if(!StringUtils.isBlank(roleNames)){
			roleName="AND role.viewRoleName='"+roleNames+"'";
		}else{
			roleName="AND role.viewRoleType='"+roleType+"'";
		}
		sql.append("SELECT role.viewRoleId roleId,role.viewRoleName roleName,actor.actorName actorName,seriesNo");
		sql.append(" ,COUNT(viewNo) crewAmountByview");//按场统计每集戏量
		sql.append(" ,ROUND(SUM(pageCount),2) crewAmountByPage");//按页统计每集戏量		
		sql.append(" FROM tab_view_role role");
		sql.append(" LEFT JOIN (tab_actor_role_map ram, tab_actor_info actor) ON (role.viewRoleId=ram.viewRoleId AND ram.actorId=actor.actorId)");
		sql.append(" LEFT JOIN (tab_view_role_map srm,tab_view_info view) ON (role.viewRoleId=srm.viewRoleId AND srm.viewId=view.viewId)");
		sql.append(" WHERE role.crewId=? "+roleName);// AND role.viewRoleName !=''
		sql.append(" GROUP BY role.viewRoleId,view.seriesNo ORDER BY role.viewRoleName,(view.seriesNo+0)");
		List<Map<String, Object>> mapList=this.query(sql.toString(), new Object[]{crewId}, null);
		return mapList;
	}
	/**
	 * 根据剧组id，获取剧组的所有集次
	 * @param crewId
	 * @return
	 * @throws SQLException 
	 */
	public List<Integer> getSetNosBycrewId(String crewId) throws SQLException{	
		

		List<Integer> setNos=new ArrayList<Integer>();;
		String sql="SELECT seriesNo FROM tab_view_info WHERE crewId=? GROUP BY seriesNo ORDER BY (seriesNo+0)";
		@SuppressWarnings("unchecked")
		List<Map<String, Integer>> setList= this.query(sql, new Object[]{crewId}, null);
		for (Map<String, Integer> map : setList) {
			setNos.add(map.get("seriesNo"));
		}
		return setNos;
	}
	
	/**
	 * 获取每个角色每个拍摄地点的戏量及戏量合计		
	 * @param crewId
	 * @param roleType
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> getStatisticsRolecrewAmoutByShootAddress(String crewId, int roleType)  throws SQLException {
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT role.viewRoleId roleId,role.viewRoleName roleName,actor.actorName actorName,IFNULL(sa.vname,?) shootLocation");
		sql.append(" ,COUNT(view.viewNo) crewAmountByview");//按场统计拍摄地点戏量
		sql.append(" ,ROUND(SUM(view.pageCount),2) crewAmountByPage");//按页统计拍摄地点戏量			
		sql.append(" FROM tab_view_role role");
		sql.append(" LEFT JOIN (tab_actor_role_map ram,tab_actor_info actor) ON (role.viewRoleId=ram.viewRoleId AND ram.actorId=actor.actorId)");
		sql.append(" LEFT JOIN (tab_view_role_map srm,tab_view_info view) ON (role.viewRoleId=srm.viewRoleId AND srm.viewId=view.viewId)");
		sql.append(" LEFT JOIN tab_sceneview_info sa ON view.shootLocationId=sa.id");
		sql.append(" WHERE role.crewId=? AND role.viewRoleType=? GROUP BY role.viewRoleId,shootLocation ORDER BY role.viewRoleName,shootLocation");
		return this.query(sql.toString(), new Object[]{ReportConst.SHOOT_ADDRESS_NULL_TITLE,crewId,roleType}, null);
		 
	}
	
	/**
	 * 获取当前剧的所有拍摄地点
	 * @param crewId
	 * @return
	 * @throws SQLException
	 */
	public List<String> getShootAddressListBycrewId(String  crewId) throws SQLException{	
		
		List<String> addressList=new ArrayList<String>();;
		String sql="SELECT DISTINCT vname shootLocation FROM tab_sceneview_info WHERE crewId=? AND vname !='' ";
		List<Map<String, Object>> mapList=this.query(sql, new Object[]{crewId}, null);
		for (Map<String, Object> map : mapList) {
			addressList.add(map.get("shootLocation").toString());
		}
		return addressList;
	}
	/**
	 * 按拍摄地点及主场景分组，统计角色的戏量	
	 * @param crewId
	 * @param roleType
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> getStatisticsByAddress(String crewId,int roleType,String viewRoleName)throws SQLException {
		String roleName="";
		if(!StringUtils.isBlank(viewRoleName)){
			roleName="AND role.viewRoleId='"+viewRoleName+"'";
		}else{
			roleName="AND role.viewRoleType='"+roleType+"'";
		}
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT role.viewRoleId roleId,role.viewRoleName roleName,IFNULL(sa.vname,?) shootLocation,IFNULL(s_a.location,?) viewAddress");
		sql.append(" ,COUNT(view.viewNo) crewAmountByview");//按场统计拍摄地点戏量
		sql.append(" ,ROUND(SUM(view.pageCount),2) crewAmountByPage");//按页统计拍摄地点戏量
		sql.append("  ,view.viewType,atmos.atmosphereName,view.site FROM tab_view_role role");
		sql.append(" LEFT JOIN (tab_view_role_map srm,tab_view_info view) ON (role.viewRoleId=srm.viewRoleId AND srm.viewId=view.viewId)");
		//lma为新增气氛/文武戏
		sql.append(" LEFT JOIN tab_atmosphere_info atmos ON atmos.atmosphereId=view.atmosphereId");//lma为新增气氛/文武戏
		sql.append(" LEFT JOIN  tab_sceneview_info sa ON view.shootLocationId=sa.id");
		sql.append(" LEFT JOIN (tab_view_location_map sam,tab_view_location s_a) ON (view.viewId=sam.viewId AND sam.locationId=s_a.locationId AND s_a.locationType=1)");
		sql.append(" WHERE role.crewId=? "+roleName+" GROUP BY role.viewRoleId,shootLocation,location ORDER BY role.viewRoleName,shootLocation,crewAmountByview DESC,crewAmountByPage DESC");
		List<Map<String, Object>> mapList =this.query(sql.toString(), new Object[]{ReportConst.SHOOT_ADDRESS_NULL_TITLE
			,ReportConst.SHOOT_ADDRESS_NULL_TITLE,crewId},null);
		return mapList;
	}
	/**
	 * @param crewId
	 * @param roleType
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> getStatisticsByviewAddress(String crewId,int roleType)throws SQLException {
		
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT role.viewRoleId AS roleId,role.viewRoleName AS roleName,actor.actorName AS actorName,IFNULL(sa.location,?) AS location");
		sb.append(",COUNT(view.viewNo) AS viewCount ");
		sb.append(",ROUND(SUM(view.pageCount),2) AS pageCount  FROM tab_view_role role ");
		sb.append("LEFT JOIN (tab_actor_role_map AS ram,tab_actor_info actor) ON (ram.viewRoleId=role.viewRoleId AND ram.actorId=actor.actorId) ");
		sb.append("LEFT JOIN (tab_view_role_map AS srm,tab_view_info view) ON (role.viewRoleId=srm.viewRoleId AND srm.viewId=view.viewId) ");
		sb.append("LEFT JOIN (tab_view_location_map AS sdm,tab_view_location AS sa) ON (sdm.viewId=view.viewId AND sa.locationId=sdm.locationId) ");
		sb.append(" WHERE role.crewId=? AND role.viewRoleType=? AND sa.locationType=1 GROUP BY role.viewRoleId,location ORDER BY role.viewRoleName,location");
		List<Map<String, Object>> mapList=this.query(sb.toString(), new Object[]{ReportConst.SHOOT_ADDRESS_NULL_TITLE,crewId,roleType}, null);
		return mapList;
	}
	/**
	 * 获取当前剧中的所有场景地点，同名不同类型的场景地点作为同一地点处理
	 * @param conn
	 * @param crewId
	 * @return
	 * @throws SQLException
	 */
	//得到场景的集合
		public List<String> getviewAddressListBycrewId(String crewId) throws SQLException{
			
			List<String> viewaddressList=new ArrayList<String>();
			String sql="SELECT DISTINCT location FROM tab_view_location WHERE crewId=? AND location !='' AND locationType=1 ";
			List<Map<String, Object>> mapList=this.query(sql, new Object[]{crewId}, null);
			for(Map<String, Object> map:mapList){	
			viewaddressList.add(map.get("location").toString());
			}
			return viewaddressList;
		}
		
	/**
	 * 获取演员角色按天统计
	 */
	public List<Map<String,Object>> getViewRoleDayStatistic(String viewRoleName,String crewId){
		StringBuffer sb = new StringBuffer();
		sb.append("select * from ( ");
		sb.append("SELECT tni.noticeDate,COUNT(tt.viewId) AS viewCount,IF(SUM(tt.pageCount) IS NULL,0,ROUND(SUM(tt.pageCount),2)) AS pageCount FROM tab_notice_info tni "); 
		sb.append("  LEFT JOIN tab_view_notice_map tvnm ON tni.noticeId = tvnm.noticeId AND (tvnm.shootStatus = 2 OR tvnm.shootStatus = 5) ");
		sb.append("  LEFT JOIN ( ");
		sb.append("     SELECT tvi.viewId,tvi.pageCount FROM tab_view_role_map tvrm,tab_view_info tvi  ");
		sb.append("     WHERE tvrm.viewId=tvi.viewId AND tvrm.viewRoleId in ( ");
		sb.append("        SELECT tvr.viewRoleId FROM tab_view_role tvr WHERE tvr.viewRoleName = ? AND tvr.crewId = ? AND (tvr.viewRoleType=1 OR tvr.viewRoleType=2) ");
		sb.append("        ) ");
		sb.append("     ) tt ON tt.viewId = tvnm.viewId ");
		sb.append("WHERE tni.crewId = ? ");
		sb.append("GROUP BY tni.noticeDate ");
		sb.append(" ) res where res.viewCount>0 ");
		List<Map<String,Object>> li = this.query(sb.toString(), new Object[]{viewRoleName,crewId,crewId}, null);
		return li;
	}
	
}
