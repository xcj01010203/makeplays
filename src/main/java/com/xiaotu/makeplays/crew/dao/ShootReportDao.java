package com.xiaotu.makeplays.crew.dao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.model.ShootScheduleBaseModel;
import com.xiaotu.makeplays.crew.model.ShootScheduleModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.ComparatorShootSchedule;
import com.xiaotu.makeplays.utils.ReportConst;
import com.xiaotu.makeplays.utils.StringUtils;

/**
 * 拍摄进度统计 dao
 * @author lma
 *
 */
@Deprecated
@Repository
public class ShootReportDao extends BaseDao<CrewInfoModel> {
 
	
	/**
	 * 获取场景以及所属的拍摄计划分组
	 * @param crewId
	 * @param statisticsType
	 * @throws SQLException
	 */
public List<Map<String,Object>> statisticscrewAmountAndDaysByGroupAndSite(String crewId, int statisticsType) throws SQLException {
		
		//
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT site,view.shootStatus status");
		if(statisticsType==ReportConst.STATISTICS_TYPE_SCENE){
			sql.append(",1 crewAmount");
		}else if(statisticsType==ReportConst.STATISTICS_TYPE_PAGE){
			sql.append(",pageCount crewAmount");
		}
		sql.append(",GROUP_CONCAT(DISTINCT groups.groupName SEPARATOR ? ) groupNames");
		sql.append(" FROM tab_view_info view");
		//sql.append(" LEFT JOIN (tab_view_plan_map sspm,tab_shootplan_info sp) ON (view.viewId=sspm.viewId AND sspm.planId=sp.planId)");
		sql.append(" LEFT JOIN (tab_view_notice_map sspm,tab_notice_info sp) ON (view.viewId=sspm.viewId AND sspm.noticeId=sp.noticeId) ");
		sql.append(" LEFT JOIN tab_shoot_group groups ON sp.groupId=groups.groupId");
		sql.append(" WHERE view.crewId=?");
		sql.append(" GROUP BY view.viewId");

		return  this.query(sql.toString(), new Object[]{ReportConst.LINK_CHAR_DOUHAO,crewId}, null);
		
	}
	/**
	 * 获取场景以及所属的拍摄计划分组(按日期查询)
	 * @param crewId
	 * @param statisticsType
	 * @throws SQLException
	 */
	public List<Map<String,Object>> statisticscrewAmountAndDaysByGroupAndSiteByday(String crewId, int statisticsType,String date) throws SQLException {
		
		//
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT site,view.shootStatus status");
		if(statisticsType==ReportConst.STATISTICS_TYPE_SCENE){
			sql.append(",1 crewAmount");
		}else if(statisticsType==ReportConst.STATISTICS_TYPE_PAGE){
			sql.append(",pageCount crewAmount");
		}
		sql.append(",GROUP_CONCAT(DISTINCT groups.groupName SEPARATOR ? ) groupNames");
		sql.append(" FROM tab_view_info view");
		//sql.append(" LEFT JOIN (tab_view_plan_map sspm,tab_shootplan_info sp) ON (view.viewId=sspm.viewId AND sspm.planId=sp.planId)");
		sql.append(" LEFT JOIN (tab_view_notice_map sspm,tab_notice_info sp) ON (view.viewId=sspm.viewId AND sspm.noticeId=sp.noticeId) ");
		sql.append(" LEFT JOIN tab_shoot_group groups ON sp.groupId=groups.groupId");
		sql.append(" WHERE view.crewId=? and sp.noticeDate=? ");
		sql.append(" GROUP BY view.viewId");
	
		return  this.query(sql.toString(), new Object[]{ReportConst.LINK_CHAR_DOUHAO,crewId,date}, null);
		
	}
	/**
	 * 获取剧组的总拍摄天数、已拍摄天数、日均拍摄戏量
	 * @param crewId
	 * @return
	 * @throws SQLException
	 */
	public int[] getShotDaysBycrewId(String crewId)throws SQLException {
	
		int[] shotDays=new int[2];;		
		
		//获取剧组的拍摄天数（剧组拍摄结束日期-剧组拍摄开始日期）、已拍摄天数：当前日期-最小通告单日期
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT DATEDIFF(shootEndDate,DATE_SUB(shootStartDate,INTERVAL 1 DAY)) shotDays");
		/*sql.append(" ,DATEDIFF(CURDATE(),DATE_SUB(MIN(noticeDate),INTERVAL 1 DAY)) shotedDays ");*/
		sql.append(" ,count(*) as shotedDays ");
		sql.append(" FROM tab_crew_info crew LEFT JOIN tab_notice_info notice ON crew.crewId=notice.crewId ");
		/*sql.append(" WHERE crew.crewId=? GROUP BY crew.crewId");*/
		sql.append(" WHERE crew.crewId=? AND canceledStatus=1 GROUP BY noticeDate");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> mapList= (List<Map<String, Object>>) this.query(sql.toString(), new Object[]{crewId}, null);
		Integer shootdates=0;
		if(mapList!=null && mapList.size()!=0){
			for (Map<String, Object> map : mapList) {
				if(map.get("shotedDays")!=null && !map.get("shotedDays").equals("")){
					shootdates++;
				}
			}
			if(mapList.get(0).get("shotDays")!=null && !mapList.get(0).get("shotDays").equals("")){
				shotDays[0]=Integer.parseInt(mapList.get(0).get("shotDays").toString());
			}
			shotDays[1]=shootdates;
		}

		return shotDays;
	}
	/**
	 * 获取某些分组已拍摄的天数
	 * @param crewId
	 * @param groupNames
	 * @return
	 * @throws SQLException
	 */
	public Map<String,Integer> getShotedDayByGroupNames(String crewId,String[] groupNames) throws SQLException {
		
		Map<String,Integer> groupName_shotedDay=new HashMap<String, Integer>();	
		
		//获取分组的已拍摄天数：当前日期-该分组的通告单的最小日期
		StringBuilder sql = new StringBuilder();
		/*sql.append(" SELECT  groupName,DATEDIFF(CURDATE(),DATE_SUB(MIN(noticeDate),INTERVAL 1 DAY)) shotedDays");*/
		sql.append(" SELECT  groupName,count(*) shotedDays");
		sql.append(" FROM tab_notice_info notice,tab_shoot_group groups");
		sql.append(" WHERE notice.crewId=?  AND notice.groupId=groups.groupId");
		if(groupNames!=null && groupNames.length>0){
			sql.append(" AND groups.groupName IN (");
			for(int g=0;g<groupNames.length;g++){
				if(g>0){
					sql.append(",");
				}
				sql.append("'"+groupNames[g]+"'");				
			}
			sql.append(")");
		}	
		sql.append(" AND canceledStatus=1 ");
		sql.append(" GROUP BY groups.groupName");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> mapList= (List<Map<String, Object>>) this.query(sql.toString(), new Object[]{crewId}, null);
		for (Map<String, Object> map : mapList) {
			String groupName=(String) map.get("groupName");
			int shotedDays=map.get("shotedDays")!=null?Integer.parseInt(map.get("shotedDays").toString()):0;
			groupName_shotedDay.put(groupName, shotedDays);
		}
		
		return groupName_shotedDay;
	}

	public Double forecastNeedDays(String crewId,int statisticsType,double totalcrewAmount,int shotedDays) throws SQLException {
		
		//拍摄天数不大于10，无法进行剩余戏量所需天数的计算
		if(shotedDays<=10){
			return null;
		}
		
		//以通告单日期为标准，获取截止到当天，每个通告单拍摄完成的戏量，按通告单日期升序排列
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT noticeDate");
		if(statisticsType==ReportConst.STATISTICS_TYPE_SCENE){
			sql.append(" ,COUNT(viewNo) crewAmount");//按场统计每天每组的戏量
		}else if(statisticsType==ReportConst.STATISTICS_TYPE_PAGE){
			sql.append(" ,ROUND(SUM(pageCount),2) crewAmount");//按页统计每天每组的戏量
		}
		sql.append(" FROM tab_notice_info notice LEFT JOIN tab_view_info view ON (notice.noticeId=view.noticeId AND view.shootStatus IN(?,?))");//?,
		sql.append(" WHERE notice.crewId=? AND noticeDate<=CURDATE() GROUP BY noticeDate ORDER BY noticeDate");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> mapList =this.query(sql.toString(), new Object[]{ReportConst.SCENE_STATUS_TYPE_DELETE
			,ReportConst.SCENE_STATUS_TYPE_FINISH,crewId}, null);
		
		if(mapList==null || mapList.size() == 0){
			return null;
		}
		
		//从第一个通告单开始到当天为止的期间，统计到每天为止已经完成的戏量，补全不存在通告单的日期，对应总共完成的戏量延用前一天完成的总戏量
		Map<Double,Double> shotedDay_finishedTotalcrewAmount=new HashMap<Double, Double>();//key表示第几天，value表示到第几天总共完成的戏量
		double y_i=0;//key，Yi
		double sum_x_i=0;//value，∑Xi
		double sum_y_i=0;//∑Yi 
		double sum_sum_x_i=0;//∑∑Xi
		Calendar beforeCalendar=Calendar.getInstance();
		Calendar nextCalendar=Calendar.getInstance();
		
		int i=0;
		for(Map<String, Object> map:mapList){
			Date tempDate=(Date) map.get("noticeDate");
			if(tempDate==null){
				//error("存在不合法的通告单，某些场景对应的通告单没有日期，无法进行计算");
				return null;
			}
			nextCalendar.setTime(tempDate);
			if(i!=0){
				//补全空缺日期
				while (beforeCalendar.before(nextCalendar)) {
					y_i=StringUtils.add(y_i,1);		
					shotedDay_finishedTotalcrewAmount.put(y_i,sum_x_i);
					beforeCalendar.add(Calendar.DATE, 1);
					
					sum_y_i=StringUtils.add(sum_y_i, y_i);
					sum_sum_x_i=StringUtils.add(sum_sum_x_i, sum_x_i);
				}
			}
			y_i=StringUtils.add(y_i,1);
			sum_x_i=StringUtils.add(sum_x_i,map.get("crewAmount")!=null?Double.parseDouble(map.get("crewAmount").toString()):0);			
			shotedDay_finishedTotalcrewAmount.put(y_i,sum_x_i);
			
			sum_y_i=StringUtils.add(sum_y_i, y_i);
			sum_sum_x_i=StringUtils.add(sum_sum_x_i, sum_x_i);
			
			beforeCalendar.setTime(tempDate);
			beforeCalendar.add(Calendar.DATE, 1);
			i++;
		}
		/*//求X平、Y平
		double y_avg=StringUtils.div(sum_y_i, (double)shotedDays,2);
		double x_avg=StringUtils.div(sum_sum_x_i,  (double)shotedDays,2);*/
		
		double dd = StringUtils.div(totalcrewAmount,  StringUtils.div(sum_x_i,  (double)y_i,2),2);

		/*if(shotedDay_finishedTotalcrewAmount.size()==0){
			return null;
		}
		//求b，b=∑(∑Xi-X平)(Yi-Y平) / ∑(∑Xi-X平)2 
		double b_v1=0;
		double b_v2=0;
		for(int d=1;d<=shotedDay_finishedTotalcrewAmount.size();d++){
			y_i=d;
			sum_x_i=shotedDay_finishedTotalcrewAmount.get(y_i);
			double x_sub=StringUtils.sub(sum_x_i, x_avg);
			double y_sub=StringUtils.sub(y_i, y_avg);
			b_v1+=StringUtils.mul(x_sub,y_sub);
			b_v2+=StringUtils.mul(x_sub,x_sub);
		}
		if(b_v2==0){
			//logger.error("求b时，分母为0");
			return null;
		}
		double b=StringUtils.div(b_v1, b_v2, 2);
		//求a，a = Y平 - b×X平
		double a=StringUtils.sub(y_avg, StringUtils.mul(b, x_avg));
		//求Y
		double y=StringUtils.add(a, StringUtils.mul(b,totalcrewAmount));	
		
		y=StringUtils.div(StringUtils.mul(y,100),100,2);//保留2位小数
*/		return dd;
	}
	/**
	 * 拍摄地点统计
	 */
	@SuppressWarnings("unchecked")
	public List<ShootScheduleBaseModel> statisticsShootScheduleByShootAddress(String crewId, int statisticsType) throws SQLException {
		//获取每个拍摄地点以及对应的场景
		StringBuilder sql = new StringBuilder();
		sql.append("select * from ( SELECT sa.id shootLocationId,sa.vname shootLocation");
		if(statisticsType==ReportConst.STATISTICS_TYPE_SCENE){
			sql.append(",if(view.viewId is null, null,1) crewAmount");
		}else if(statisticsType==ReportConst.STATISTICS_TYPE_PAGE){
			sql.append(",pageCount crewAmount");
		}	
		sql.append(",view.shootStatus ");
		sql.append(" FROM tab_sceneview_info sa LEFT JOIN tab_view_info view ON  sa.id=view.shootLocationId");
		sql.append(" WHERE sa.crewId=?");
		sql.append(" ORDER BY sa.id ) rr  WHERE rr.crewAmount is not null");
		List<Map<String, Object>> mapList=this.query(sql.toString(), new Object[]{crewId},null);	
		//统计每个拍摄地点的总戏量及以完成戏量
		List<ShootScheduleBaseModel> ssmList=new ArrayList<ShootScheduleBaseModel>();
		 int j=0;
		 for (int i=0;i < mapList.size(); i++) {
			 if(j==mapList.size()){
				 break;//当 J 为最后一个时，跳出循环 
			 }
			 double totalcrewAmount=0;
			Map<String, Object> map=mapList.get(i);
			ShootScheduleBaseModel ssm=new ShootScheduleBaseModel();
			String shootAddressId= map.get("shootLocationId").toString();
			String shootAddress=map.get("shootLocation").toString();
			ssm.setTitle(shootAddress);
			double crewAmount=Double.valueOf(map.get("crewAmount")!=null?map.get("crewAmount").toString():"0");;//某个拍摄地点当前场景戏量
			totalcrewAmount=crewAmount;//某个拍摄地点的总戏量
			double finishedcrewAmount=0;//某个拍摄地点的已完成戏量
			Integer status=map.get("shootStatus")!=null?(Integer) map.get("shootStatus"):0;
			if(status==ReportConst.SCENE_STATUS_TYPE_DELETE
					|| status==ReportConst.SCENE_STATUS_TYPE_FINISH){
				finishedcrewAmount=crewAmount;
			}
			for (j = i+1; j < mapList.size(); j++) {
				Map<String, Object> mapto=mapList.get(j);
				String next_shootAddressId=mapto.get("shootLocationId").toString();
 				if(next_shootAddressId.equals(shootAddressId)){
					//统计某拍摄地点的戏量合计、已完成戏量
					crewAmount=Double.valueOf(mapto.get("crewAmount").toString());
					totalcrewAmount+=crewAmount;
					status=(Integer) mapto.get("shootStatus");
					if(status==ReportConst.SCENE_STATUS_TYPE_DELETE
							|| status==ReportConst.SCENE_STATUS_TYPE_FINISH){
						finishedcrewAmount+=crewAmount;
					}
				}else{
					//移动到上一行
					i=j-1;
					break;
				}
			}
			ssm.setTotalCrewAmount(Double.valueOf(String.format("%.2f",totalcrewAmount)));
			ssm.setFinishedCrewAmount(Double.valueOf(String.format("%.2f",finishedcrewAmount)));
			ssmList.add(ssm);
			
		}
		//获取没有设置拍摄地点的场景
		 ShootScheduleBaseModel ssm=statisticsNullShootAddresscrewAmount( crewId, statisticsType);
			if(ssm!=null){
				ssmList.add(ssm);
			}
		//按拍摄总戏量进行降序排序
		ComparatorShootSchedule css= new ComparatorShootSchedule();
		Collections.sort(ssmList, css);
		
		
		return ssmList;
	}
	/**
	 * 获取没有设置拍摄地点的场景
	 * @param crewId
	 * @param statisticsType
	 * @return
	 * @throws SQLException
	 */
	public ShootScheduleBaseModel statisticsNullShootAddresscrewAmount(String crewId, int statisticsType) throws SQLException {

		
		ShootScheduleBaseModel ssm= new ShootScheduleBaseModel();
		//获取没有设置拍摄地点的场景
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT shootStatus");
		if(statisticsType==ReportConst.STATISTICS_TYPE_SCENE){
			sql.append(",IF(viewId IS NULL,0,1) crewAmount");
		}else if(statisticsType==ReportConst.STATISTICS_TYPE_PAGE){
			sql.append(",pageCount crewAmount");
		}	
		sql.append(" FROM tab_view_info");
		sql.append(" WHERE crewId=? AND (shootLocationId IS NULL OR shootLocationId='') ");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> mapList=this.query(sql.toString(), new Object[]{crewId}, null);
		//统计未设置拍摄地点的总戏量及以完成戏量
		ssm.setTitle(ReportConst.SHOOT_ADDRESS_NULL_TITLE);
		double totalcrewAmount=0;//某个拍摄地点的总戏量
		double finishedcrewAmount=0;//某个拍摄地点的已完成戏量
		for (Map<String, Object> map : mapList) {
			
			double crewAmount=Double.valueOf(map.get("crewAmount")!=null?map.get("crewAmount").toString():"0");//某个拍摄地点当前场景戏量
			totalcrewAmount+=crewAmount;
			int status=(Integer) map.get("shootStatus");
			if(status==ReportConst.SCENE_STATUS_TYPE_DELETE
					|| status==ReportConst.SCENE_STATUS_TYPE_FINISH){
				finishedcrewAmount+=crewAmount;
			}
		}
		ssm.setTotalCrewAmount(Double.valueOf(String.format("%.2f",totalcrewAmount)));
		ssm.setFinishedCrewAmount(Double.valueOf(String.format("%.2f",finishedcrewAmount)));
		
		return ssm;
	}
	/**
	 * 按角色统计
	 * @param crewId 剧组Id
	 * @param statisticsType 场/页
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<ShootScheduleBaseModel> statisticsShootScheduleByRole(String crewId, Integer statisticsType,Integer viewRoleType) throws SQLException {

		//获取每个角色以及对应的场景
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT role.viewRoleId,role.viewRoleName,shootStatus");
		if(statisticsType==ReportConst.STATISTICS_TYPE_SCENE){
			sql.append(",if(view.viewId IS NULL,0,1) crewAmount");
		}else if(statisticsType==ReportConst.STATISTICS_TYPE_PAGE){
			sql.append(",if(pageCount IS NULL,0,pageCount) crewAmount");
		}	
		sql.append(" FROM tab_view_role role LEFT JOIN (tab_view_role_map srm,tab_view_info view) ON (role.viewRoleId=srm.viewRoleId AND srm.viewId=view.viewId)");
		sql.append(" WHERE role.crewId=? AND role.viewRoleType in ("+viewRoleType+") ORDER BY role.viewRoleId ");
		List<Map<String, Object>> mapList=this.query(sql.toString(), new Object[]{crewId}, null);
		//统计每个角色的总戏量及以完成戏量
		List<ShootScheduleBaseModel> ssmList=new ArrayList<ShootScheduleBaseModel>();
		 int j=0;
		 for (int i=0;i < mapList.size(); i++) {
			 if(j==mapList.size()){
				 break;//当 J 为最后一个时，跳出循环 
			 }
			Map<String, Object> map=mapList.get(i);
			ShootScheduleBaseModel ssm=new ShootScheduleBaseModel();
			String roleId=map.get("viewRoleId").toString();
			String roleName=map.get("viewRoleName").toString();
			ssm.setTitle(roleName);
			double crewAmount=Double.valueOf(map.get("crewAmount").toString());//某个角色当前场景戏量
			double totalcrewAmount=crewAmount;//某个角色的总戏量
			double finishedcrewAmount=0;//某个角色的已完成戏量
			int status=map.get("shootStatus")!=null?(Integer) map.get("shootStatus"):0;
			if(status==ReportConst.SCENE_STATUS_TYPE_DELETE
					|| status==ReportConst.SCENE_STATUS_TYPE_FINISH){
				finishedcrewAmount=crewAmount;
			}
			for (j = i+1; j < mapList.size(); j++) {
				Map<String, Object> mapto=mapList.get(j);
				String next_roleId=mapto.get("viewRoleId").toString();
				if(next_roleId.equals(roleId)){
					//统计某角色的戏量合计、已完成戏量
					crewAmount=Double.valueOf(mapto.get("crewAmount").toString());
					totalcrewAmount+=crewAmount;
					status=mapto.get("shootStatus")!=null?(Integer) mapto.get("shootStatus"):0;
					if(status==ReportConst.SCENE_STATUS_TYPE_DELETE
							|| status==ReportConst.SCENE_STATUS_TYPE_FINISH){
						finishedcrewAmount+=crewAmount;
					}				
				}else{
					//移动到上一行
					i=j-1;
					break;
				}
			}
			
			ssm.setTotalCrewAmount(Double.valueOf(String.format("%.2f",totalcrewAmount)));
			ssm.setFinishedCrewAmount(Double.valueOf(String.format("%.2f",finishedcrewAmount)));
			ssmList.add(ssm);
			
		}
		//按总戏量进行降序排序
		//ComparatorShootSchedule css= new ComparatorShootSchedule();
		Collections.sort(ssmList, new Comparator<ShootScheduleBaseModel>(){

			@Override
			public int compare(ShootScheduleBaseModel o1,
					ShootScheduleBaseModel o2) {
				int compareFlag=0;
				
				if(o1 instanceof ShootScheduleBaseModel && o2 instanceof ShootScheduleBaseModel){
					//比较总戏量
					ShootScheduleBaseModel ssm=(ShootScheduleBaseModel)o1;
					double totalPlayAmount=ssm.getTotalCrewAmount();
					ssm=(ShootScheduleBaseModel)o2;
					double nextTotalPlayAmount=ssm.getTotalCrewAmount();
					if(totalPlayAmount<nextTotalPlayAmount){
						compareFlag=1;
					}else if(totalPlayAmount>nextTotalPlayAmount){
						compareFlag=-1;
					}
				}
				
				return compareFlag;
			}
			
		});
		
		return ssmList;
	}
/**
 * 按天统计
 */
public Map<String,Object> statisticsShootScheduleByDay(String crewId, int statisticsType) throws SQLException {
		
		
		//获取当前剧的所有通告单日期
		List<String> noticeDates=getNoticeDatesBycrewId(crewId);
			//new date
			//List<String> noticeDates=new ArrayList<String>();
		//获取每天每组通告单完成的戏量，按分组排序
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT notice.noticeDate,groups.groupName groupName ");
		if(statisticsType==ReportConst.STATISTICS_TYPE_SCENE){
			sql.append(" ,COUNT(groups.crewId) crewAmount");//按场统计每天每组的戏量
		}else if(statisticsType==ReportConst.STATISTICS_TYPE_PAGE){
			sql.append(" ,ROUND(SUM(pageCount),2) crewAmount");//按页统计每天每组的戏量
		}
		sql.append(" FROM tab_notice_info notice ");
		sql.append(" LEFT JOIN tab_shoot_group groups ON notice.groupId=groups.groupId  ");//AND notice.canceledStatus IN(1)
		sql.append(" LEFT JOIN tab_view_notice_map map ON (notice.noticeId=map.noticeId ) left JOIN tab_view_info views ON views.viewId=map.viewId");
		sql.append(" WHERE notice.crewId=? AND noticeDate<=CURDATE() AND (map.shootStatus=2 or map.shootStatus=5) AND groupName is not null GROUP BY notice.noticeDate,groups.groupName ORDER BY groupName");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> mapList=this.query(sql.toString(), new Object[]{ crewId},null);
		List<ShootScheduleModel>groupcrewAmountList=new ArrayList<ShootScheduleModel>();//每个分组对应的所有通告单的戏量
		List<Double> totalcrewAmountList=new ArrayList<Double>();//每天通告单的总戏量
		
		int j=0;
        for (int i = 0; i < mapList.size(); i++) {
        	 if(j==mapList.size()){
				 break;//当 J 为最后一个时，跳出循环 
			 }
			Map<String, Object> map=mapList.get(i);
			String groupName=(String) map.get("groupName").toString();	
			Map<Object,Double> noticeDate_crewAmount=new HashMap<Object,Double>();//通告单日期_对应的戏量
			String noticeDate=org.apache.commons.lang.StringUtils.isNotBlank(map.get("noticeDate").toString())?map.get("noticeDate").toString():"";
			double crewAmount=Double.valueOf(map.get("crewAmount")!=null?map.get("crewAmount").toString():"0");	
			noticeDate_crewAmount.put(noticeDate, crewAmount);
			for (j = i+1; j < mapList.size(); j++) {
			  Map<String, Object> mapto=mapList.get(j);
				String next_groupName=(String) mapto.get("groupName").toString();
				if(next_groupName.equals(groupName)){
					//统计某分组在某天（某一通告单日期）的戏量
					noticeDate=org.apache.commons.lang.StringUtils.isNotBlank(mapto.get("noticeDate").toString())?mapto.get("noticeDate").toString():"";
					crewAmount=mapto.get("crewAmount")!=null?Double.valueOf(mapto.get("crewAmount").toString()):0;
					noticeDate_crewAmount.put(noticeDate, crewAmount);
				}else{					
					//移动到上一行
					i=j-1;
					break;
				}
			}
			//将不出现当前分组的通告单日期的戏量补0，并统计每天的总戏量
			if(noticeDates!=null && noticeDates.size()>0){
				List<Double> crewAmountList=new ArrayList<Double>();
				double count_crewAmount=0;
				for(int s=0;s<noticeDates.size();s++){
					Object obj=noticeDates.get(s);
					Double temp_crewAmount=noticeDate_crewAmount.get(obj.toString());
					Double crewAmount_str=Double.valueOf(String.format("%.2f",temp_crewAmount!=null?temp_crewAmount+count_crewAmount:(0+count_crewAmount)));
					crewAmountList.add(crewAmount_str);
					/*if(typeline == 2){//为日累计
						count_crewAmount+=temp_crewAmount!=null?temp_crewAmount:0;//日累计
					}*/
					if(totalcrewAmountList.size()-1<s){
						if(temp_crewAmount==null){
							temp_crewAmount=0d;
						}
						totalcrewAmountList.add(temp_crewAmount);
					}else{
						if(temp_crewAmount==null){
							temp_crewAmount=0d;
						}
						if(totalcrewAmountList.get(s)!=null){
							temp_crewAmount+=totalcrewAmountList.get(s);
						}
						totalcrewAmountList.set(s, temp_crewAmount);
					}
				
				}
				//if(typeline != 2){
					ShootScheduleModel ssm=new ShootScheduleModel();
					ssm.setTitle(groupName);
					ssm.setcrewAmountList(crewAmountList);
					groupcrewAmountList.add(ssm);
				//}
			}
		}
        //if(typeline == 2){//为日累计
        List<ShootScheduleModel> dayTotalList=new ArrayList<ShootScheduleModel>();//每个分组对应的所有通告单的戏量
			ShootScheduleModel ssm=new ShootScheduleModel();
			ssm.setTitle("合计");
		
			for (int k = 0; k < totalcrewAmountList.size()-1; k++) {//日累计合
				//if(totalcrewAmountList.get(k)!=null&&totalcrewAmountList.get(k+1)!=null)
					totalcrewAmountList.set((k+1),Double.valueOf(String.format("%.2f",totalcrewAmountList.get(k)+totalcrewAmountList.get(k+1))));
			}
		
			ssm.setcrewAmountList(totalcrewAmountList);
			dayTotalList.add(ssm);
        //}
		
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("noticeDates", noticeDates);
		map.put("groupcrewAmountList", groupcrewAmountList);
		map.put("dayTotalList", dayTotalList);
		return map;		
	}
	/**
	 * 获取当前剧的所有通告单日期
	 * 只包含已销场场景的通告单
	 */
	public List<String> getNoticeDatesBycrewId(String crewId)throws SQLException {
		List<String> noticeDates=new ArrayList<String>();
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT DISTINCT ");
		sql.append(" 	tni.noticeDate ");
		sql.append(" FROM ");
		sql.append(" 	tab_notice_info tni, ");
		sql.append(" 	tab_view_notice_map tvnm ");
		sql.append(" WHERE ");
		sql.append(" 	tni.crewId = ? ");
		sql.append(" AND tni.noticeId = tvnm.noticeId ");
		sql.append(" AND (tvnm.shootStatus=2 or tvnm.shootStatus=5) ");
		sql.append(" ORDER BY ");
		sql.append(" 	tni.noticeDate ");
		
		List<Map<String, Object>> mapList=this.query(sql.toString(),new Object[]{crewId},null);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (Map<String, Object> map : mapList) {
			Date noticeDate = (Date) map.get("noticeDate");
			noticeDates.add(sdf.format(noticeDate));
		}
		return noticeDates;
	}
}
