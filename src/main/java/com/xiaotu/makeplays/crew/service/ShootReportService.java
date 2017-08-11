package com.xiaotu.makeplays.crew.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.crew.dao.ShootReportDao;
import com.xiaotu.makeplays.crew.model.ShootScheduleBaseModel;
import com.xiaotu.makeplays.crew.model.ShootScheduleModel;
import com.xiaotu.makeplays.utils.ReportConst;
import com.xiaotu.makeplays.utils.StringUtils;
import com.xiaotu.makeplays.view.model.constants.ShootStatus;

/**
 *  拍摄进度统计
 * @author lma
 *
 */
@Service
@Deprecated
public class ShootReportService {
	@Autowired
	private ShootReportDao  shootReportDao;
	
	public  Map<String, Object> statisticsShootSchedule(String playId,Integer statisticsType,Integer viewRoleType)throws Exception {
		Map<String,Object> shootScheduleMap=new HashMap<String, Object>();
		//按拍摄地点统计
		try{
		//按拍摄地点统计
			List<ShootScheduleBaseModel> ssmListByShootAddress=shootReportDao.statisticsShootScheduleByShootAddress(playId, statisticsType);
			shootScheduleMap.put("byShootAddress", ssmListByShootAddress);
		//按角色统计
		List<ShootScheduleBaseModel> ssmListByRole=shootReportDao.statisticsShootScheduleByRole( playId, statisticsType,viewRoleType);
		shootScheduleMap.put("byRole", ssmListByRole);
		//按天统计
		Map<String,Object> map=shootReportDao.statisticsShootScheduleByDay(playId, statisticsType);
		@SuppressWarnings("unchecked")
		List<String> noticeDates=(List<String>) map.get("noticeDates");
		shootScheduleMap.put("noticeDates", noticeDates);
		@SuppressWarnings("unchecked")
		List<ShootScheduleModel> groupPlayAmountList= (List<ShootScheduleModel>) map.get("groupcrewAmountList");
		shootScheduleMap.put("byDay", groupPlayAmountList);
		@SuppressWarnings("unchecked")
		List<ShootScheduleModel> dayTotalList= (List<ShootScheduleModel>) map.get("dayTotalList");
		shootScheduleMap.put("dayTotalList", dayTotalList);
		//概览数据
		Map<String,Object> map_overView=this.statisticsShootScheduleOverView( playId, statisticsType);
		@SuppressWarnings("unchecked")
		List<ShootScheduleBaseModel> overViewShootSchedule=(List<ShootScheduleBaseModel>) map_overView.get("overViewShootSchedule");
		shootScheduleMap.put("overViewShootSchedule", overViewShootSchedule);
		Double needDays=(Double) map_overView.get("needDays");
		shootScheduleMap.put("needDays", needDays);
		Double earlyOrLateDays=(Double) map_overView.get("earlyOrLateDays");
		shootScheduleMap.put("earlyOrLateDays", earlyOrLateDays);
	}catch (Exception e) {			
		e.printStackTrace();
	}finally{
	
	}
	return shootScheduleMap;

	}
	
	/*//角色主要演员特约演员
public  Map<String, Object> roleViewRoleType(String playId,Integer statisticsType, Integer viewRoleType)throws Exception {
	Map<String,Object> shootScheduleMap=new HashMap<String, Object>();
	//按角色统计
	List<ShootScheduleBaseModel> ssmListByRole=shootReportDao.statisticsShootScheduleByRole( playId, statisticsType,viewRoleType);
	shootScheduleMap.put("byRole", ssmListByRole);
	return shootScheduleMap;
}*/
	
/**
 * 	概览数据
 * @param crewId 剧组Id
 * @param statisticsType 场/页
 * @return
 * @throws SQLException
 */
public Map<String,Object> statisticsShootScheduleOverView(String playId, int statisticsType)throws Exception {
		Map<String,Object> map=new HashMap<String, Object>();
		Map<String, TreeMap<String, ShootScheduleBaseModel>> item_ssbmMap=this.statisticscrewAmountAndDaysByGroupAndSite(playId, statisticsType);
		List<Object> ssbmList=null;
		if(item_ssbmMap!=null && item_ssbmMap.size()>0){
			ssbmList=new ArrayList<Object>();
			Map<String,ShootScheduleBaseModel> ssbmMap=item_ssbmMap.get("total");
			if(ssbmMap!=null && ssbmMap.size()>0){
				ShootScheduleBaseModel ssbm=ssbmMap.get("total");
				Double needDays=null;
				Double earlyOrLateDays=null; 
				if(ssbm!=null){					
					Double needTotalDays=this.shootReportDao.forecastNeedDays(playId, statisticsType, ssbm.getTotalCrewAmount(),ssbm.getShootedDays());
					if(needTotalDays!=null){
						needDays=StringUtils.sub(needTotalDays,ssbm.getShootedDays());
						earlyOrLateDays=StringUtils.sub(ssbm.getShootDays(),needTotalDays);
					}					
				}
				map.put("needDays", needDays);
				map.put("earlyOrLateDays",earlyOrLateDays);
				ssbmList.add(ssbm);
			}
			ssbmMap=item_ssbmMap.get("group");
			if(ssbmMap!=null && ssbmMap.size()>0){
				Iterator<String> iterator=ssbmMap.keySet().iterator();
				while(iterator.hasNext()){
					ssbmList.add(ssbmMap.get(iterator.next()));
				}
			}
			ssbmMap=item_ssbmMap.get("site");
			if(ssbmMap!=null && ssbmMap.size()>0){
				Iterator<String> iterator=ssbmMap.keySet().iterator();
				while(iterator.hasNext()){
					ssbmList.add(ssbmMap.get(iterator.next()));
				}
			}
			map.put("overViewShootSchedule", ssbmList);
		}
		return map;
	}
public Map<String,TreeMap<String,ShootScheduleBaseModel>> statisticscrewAmountAndDaysByGroupAndSite(String crewId, int statisticsType) throws Exception {

	//分别按“总进度”、“内外景”、“拍摄计划分组”统计总戏量、已完成戏量
		Map<String,TreeMap<String,ShootScheduleBaseModel>> item_ssbmMap= new HashMap<String, TreeMap<String,ShootScheduleBaseModel>>();
		double totalcrewAmount=0;//总戏量
		double finishedcrewAmount=0;//已完成戏量
		List<Map<String, Object>> resultList=shootReportDao.statisticscrewAmountAndDaysByGroupAndSite(crewId, statisticsType);
		for (Map<String, Object> map : resultList) {
			//删戏的不加入统计
			if ((Integer) map.get("status") == ShootStatus.DeleteXi.getValue()) {
				continue;
			}
			
			double crewAmount=Double.valueOf(map.get("crewAmount")!=null?map.get("crewAmount").toString():"0");
			double crewAmount_finished=0;
			int status=map.get("status")!=null?(Integer) map.get("status"):0;
			if(status==ReportConst.SCENE_STATUS_TYPE_DELETE
					|| status==ReportConst.SCENE_STATUS_TYPE_FINISH){
				crewAmount_finished=crewAmount;
			}
			//统计“总进度”的总戏量、已完成戏量
			totalcrewAmount+=crewAmount;
			finishedcrewAmount+=crewAmount_finished;
			//统计“内外景”的总戏量、已完成戏量
			String site=map.get("site")!=null?map.get("site").toString():null;
			if(site!=null && !site.equals("")){
				TreeMap<String,ShootScheduleBaseModel> ssbmMap=item_ssbmMap.get("site");
				if(ssbmMap==null){
					ssbmMap=new TreeMap<String, ShootScheduleBaseModel>();
				}
				ShootScheduleBaseModel ssbm=ssbmMap.get(site);
				if(ssbm==null){
					ssbm=new ShootScheduleBaseModel();
					ssbm.setTitle(site);
				}
				double site_totalCrewAmount=ssbm.getTotalCrewAmount()+crewAmount;
				ssbm.setTotalCrewAmount(Double.valueOf(String.format("%.2f",site_totalCrewAmount)));
				double site_finishedCrewAmount=ssbm.getFinishedCrewAmount()+crewAmount_finished;
				ssbm.setFinishedCrewAmount(Double.valueOf(String.format("%.2f",site_finishedCrewAmount)));
				ssbmMap.put(site, ssbm);
				item_ssbmMap.put("site", ssbmMap);
			}
			//统计“拍摄计划分组”的总戏量、已完成戏量
			String groupNames=map.get("groupNames")!=null?map.get("groupNames").toString():null;
			if(groupNames!=null && !groupNames.equals("")){
				String[] groups=groupNames.split(ReportConst.LINK_CHAR_DOUHAO);
				if(groups!=null && groups.length>0){
					TreeMap<String,ShootScheduleBaseModel> ssbmMap=item_ssbmMap.get("group");
					if(ssbmMap==null){
						ssbmMap=new TreeMap<String, ShootScheduleBaseModel>();
					}
					String groupName=null;
					//一个场景可能包含在多个分组中
					for(int g=0;g<groups.length;g++){
						groupName=groups[g];
						if(groupName!=null && !groupName.equals("")){
							ShootScheduleBaseModel ssm=ssbmMap.get(groupName);
							if(ssm==null){
								ssm=new ShootScheduleBaseModel();
								ssm.setTitle(groupName);
							}
							double group_totalPlayAmount=ssm.getTotalCrewAmount()+crewAmount;
							ssm.setTotalCrewAmount(Double.valueOf(String.format("%.2f",group_totalPlayAmount)));
							double group_finishedPlayAmount=ssm.getFinishedCrewAmount()+crewAmount_finished;
							ssm.setFinishedCrewAmount(Double.valueOf(String.format("%.2f",group_finishedPlayAmount)));
							ssbmMap.put(groupName, ssm);
						}
					}
					item_ssbmMap.put("group", ssbmMap);
				}
			}			
		}
		
		
		//分别按“总进度”、“内外景”、“拍摄计划分组”统计未完成戏量、已完成的戏量百分比
		TreeMap<String,ShootScheduleBaseModel> ssbmMap=null;
		Iterator<String> iterator=null;
		ShootScheduleBaseModel ssbm=null;
		//“总进度”
		ssbm=new ShootScheduleBaseModel();
		ssbm.setTitle("总进度");
		ssbm.setTotalCrewAmount(Double.parseDouble(String.format("%.2f", totalcrewAmount)));
		ssbm.setFinishedCrewAmount(Double.valueOf(String.format("%.2f",finishedcrewAmount)));
		ssbm.setUnfinishedCrewAmount(Double.valueOf(String.format("%.2f",StringUtils.sub(totalcrewAmount, finishedcrewAmount))));
		if(totalcrewAmount!=0){
			ssbm.setFinishedPercent(Double.valueOf(String.format("%.2f",StringUtils.mul(StringUtils.div(finishedcrewAmount, totalcrewAmount, 4),100))));
		}
		//获取剧组的总拍摄天数、已拍摄天数、日均拍摄戏量
		int[] shotDays=shootReportDao.getShotDaysBycrewId(crewId); 
		if(shotDays!=null && shotDays.length>0){
			ssbm.setShootDays(shotDays[0]);
			if(shotDays.length>1){
				ssbm.setShootedDays(shotDays[1]);
				if(shotDays[1]!=0){
					ssbm.setDailyFinishedCrewAmount(Double.valueOf(String.format("%.2f",StringUtils.div(finishedcrewAmount, shotDays[1],2))));
				}
			}
		}		
		ssbmMap=new TreeMap<String, ShootScheduleBaseModel>();
		ssbmMap.put("total", ssbm);
		item_ssbmMap.put("total", ssbmMap);
		//“内外景”
		ssbmMap=item_ssbmMap.get("site");
		if(ssbmMap!=null && ssbmMap.size()>0){
			iterator=ssbmMap.keySet().iterator();
			while(iterator.hasNext()){
				String site=iterator.next();
				ssbm=ssbmMap.get(site);
				double site_totalCrewAmount=ssbm.getTotalCrewAmount();
				double site_finishedCrewAmount=ssbm.getFinishedCrewAmount();
				ssbm.setUnfinishedCrewAmount(Double.valueOf(String.format("%.2f",StringUtils.sub(site_totalCrewAmount, site_finishedCrewAmount))));
				if(site_totalCrewAmount!=0){
					ssbm.setFinishedPercent(Double.valueOf(String.format("%.2f",StringUtils.div(site_finishedCrewAmount,site_totalCrewAmount, 4)*100)));
				}
				ssbmMap.put(site, ssbm);
			}
			item_ssbmMap.put("site", ssbmMap);
		}
		//“拍摄计划分组”
		ssbmMap=item_ssbmMap.get("group");
		if(ssbmMap!=null && ssbmMap.size()>0){
			iterator=ssbmMap.keySet().iterator();
			String groupNames[]={};
			groupNames=ssbmMap.keySet().toArray(groupNames);
			Map<String,Integer> groupName_shotedDays=shootReportDao.getShotedDayByGroupNames(crewId, groupNames);//某些分组已拍摄的天数
			while(iterator.hasNext()){
				String groupName=iterator.next();
				ssbm=ssbmMap.get(groupName);
				double group_totalCrewAmount=ssbm.getTotalCrewAmount();
				double group_finishedCrewAmount=ssbm.getFinishedCrewAmount();
				ssbm.setUnfinishedCrewAmount(Double.valueOf(String.format("%.2f",StringUtils.sub(group_totalCrewAmount, group_finishedCrewAmount))));
				if(group_totalCrewAmount!=0){
					ssbm.setFinishedPercent(Double.valueOf(String.format("%.2f",StringUtils.mul(StringUtils.div(group_finishedCrewAmount,group_totalCrewAmount, 4),100))));
				}				
				//获取某个分组的已拍摄天数、日均拍摄戏量
				Integer shotedDays=groupName_shotedDays.get(groupName);
				if(shotedDays!=null && shotedDays!=0){
					ssbm.setShootedDays(shotedDays);
					ssbm.setDailyFinishedCrewAmount(Double.valueOf(String.format("%.2f",StringUtils.div(group_finishedCrewAmount, shotedDays,2))));	
				}
				ssbmMap.put(groupName, ssbm);
			}
			item_ssbmMap.put("group", ssbmMap);
		}
		return item_ssbmMap;
	}

	/*
	 * 获取移动端拍摄进度
	 */
	public Map<String,TreeMap<String,ShootScheduleBaseModel>> getMobileShootSchedule(String crewId, int statisticsType) throws Exception {

	//分别按“总进度”、“拍摄计划分组”统计总戏量、已完成戏量
		Map<String,TreeMap<String,ShootScheduleBaseModel>> item_ssbmMap= new HashMap<String, TreeMap<String,ShootScheduleBaseModel>>();
		double totalcrewAmount=0;//总戏量
		double finishedcrewAmount=0;//已完成戏量，包含（加戏、删戏、完成）
		List<Map<String, Object>> resultList=shootReportDao.statisticscrewAmountAndDaysByGroupAndSite(crewId, statisticsType);
		for (Map<String, Object> map : resultList) {
			
			double crewAmount=Double.valueOf(map.get("crewAmount")!=null?map.get("crewAmount").toString():"0");
			double crewAmount_finished=0;
			int status=map.get("status")!=null?(Integer) map.get("status"):0;
			if(status==ReportConst.SCENE_STATUS_TYPE_DELETE
					|| status==ReportConst.SCENE_STATUS_TYPE_FINISH){
				crewAmount_finished=crewAmount;
			}
			//统计“总进度”的总戏量、已完成戏量
			totalcrewAmount+=crewAmount;
			finishedcrewAmount+=crewAmount_finished;
			
		}
		
		
		//分别按“总进度”、“内外景”、“拍摄计划分组”统计未完成戏量、已完成的戏量百分比
		TreeMap<String,ShootScheduleBaseModel> ssbmMap=null;
		Iterator<String> iterator=null;
		ShootScheduleBaseModel ssbm=null;
		//“总进度”
		ssbm=new ShootScheduleBaseModel();
		ssbm.setTitle("总进度");
		ssbm.setTotalCrewAmount(Double.parseDouble(String.format("%.2f", totalcrewAmount)));
		ssbm.setFinishedCrewAmount(Double.valueOf(String.format("%.2f",finishedcrewAmount)));
		ssbm.setUnfinishedCrewAmount(Double.valueOf(String.format("%.2f",StringUtils.sub(totalcrewAmount, finishedcrewAmount))));
		if(totalcrewAmount!=0){
			ssbm.setFinishedPercent(Double.valueOf(String.format("%.2f",StringUtils.mul(StringUtils.div(finishedcrewAmount, totalcrewAmount, 4),100))));
		}
		//获取剧组的总拍摄天数、已拍摄天数、日均拍摄戏量
		int[] shotDays=shootReportDao.getShotDaysBycrewId(crewId); 
		if(shotDays!=null && shotDays.length>0){
			ssbm.setShootDays(shotDays[0]);
			if(shotDays.length>1){
				ssbm.setShootedDays(shotDays[1]);
				if(shotDays[1]!=0){
					ssbm.setDailyFinishedCrewAmount(Double.valueOf(String.format("%.2f",StringUtils.div(finishedcrewAmount, shotDays[1],2))));
				}
			}
		}		
		ssbmMap=new TreeMap<String, ShootScheduleBaseModel>();
		ssbmMap.put("total", ssbm);
		item_ssbmMap.put("total", ssbmMap);
		
		
		//**********获取昨天通告单拍摄情况
		Calendar   cal   =   Calendar.getInstance();
		cal.add(Calendar.DATE,   -1);
		//String yesterday = new SimpleDateFormat( "yyyy-MM-dd ").format(cal.getTime());
		String date = new SimpleDateFormat( "yyyy-MM-dd ").format(cal.getTime());
		List<Map<String, Object>> resultList1=shootReportDao.statisticscrewAmountAndDaysByGroupAndSiteByday(crewId, statisticsType,date);
		for (Map<String, Object> map : resultList1) {
			
			double crewAmount=Double.valueOf(map.get("crewAmount")!=null?map.get("crewAmount").toString():"0");
			double crewAmount_finished=0;
			int status=map.get("status")!=null?(Integer) map.get("status"):0;
			if(status==ReportConst.SCENE_STATUS_TYPE_DELETE
					|| status==ReportConst.SCENE_STATUS_TYPE_FINISH){
				crewAmount_finished=crewAmount;
			}
			//统计“总进度”的总戏量、已完成戏量
			totalcrewAmount+=crewAmount;
			finishedcrewAmount+=crewAmount_finished;
			
			//统计“拍摄计划分组”的总戏量、已完成戏量
			String groupNames=map.get("groupNames")!=null?map.get("groupNames").toString():null;
			if(groupNames!=null && !groupNames.equals("")){
				String[] groups=groupNames.split(ReportConst.LINK_CHAR_DOUHAO);
				if(groups!=null && groups.length>0){
					TreeMap<String,ShootScheduleBaseModel> ssbmMap1=item_ssbmMap.get("group");
					if(ssbmMap1==null){
						ssbmMap1=new TreeMap<String, ShootScheduleBaseModel>();
					}
					String groupName=null;
					//一个场景可能包含在多个分组中
					for(int g=0;g<groups.length;g++){
						groupName=groups[g];
						if(groupName!=null && !groupName.equals("")){
							ShootScheduleBaseModel ssm=ssbmMap1.get(groupName);
							if(ssm==null){
								ssm=new ShootScheduleBaseModel();
								ssm.setTitle(groupName);
							}
							double group_totalPlayAmount=ssm.getTotalCrewAmount()+crewAmount;
							ssm.setTotalCrewAmount(Double.valueOf(String.format("%.2f",group_totalPlayAmount)));
							double group_finishedPlayAmount=ssm.getFinishedCrewAmount()+crewAmount_finished;
							ssm.setFinishedCrewAmount(Double.valueOf(String.format("%.2f",group_finishedPlayAmount)));
							ssbmMap1.put(groupName, ssm);
						}
					}
					item_ssbmMap.put("group", ssbmMap1);
				}
			}			
		}
		
		//“拍摄计划分组”
		ssbmMap=item_ssbmMap.get("group");
		if(ssbmMap!=null && ssbmMap.size()>0){
			iterator=ssbmMap.keySet().iterator();
			String groupNames[]={};
			groupNames=ssbmMap.keySet().toArray(groupNames);
			//Map<String,Integer> groupName_shotedDays=shootReportDao.getShotedDayByGroupNames(crewId, groupNames);//某些分组已拍摄的天数
			while(iterator.hasNext()){
				String groupName=iterator.next();
				ssbm=ssbmMap.get(groupName);
				double group_totalCrewAmount=ssbm.getTotalCrewAmount();
				double group_finishedCrewAmount=ssbm.getFinishedCrewAmount();
				ssbm.setUnfinishedCrewAmount(Double.valueOf(String.format("%.2f",StringUtils.sub(group_totalCrewAmount, group_finishedCrewAmount))));
				if(group_totalCrewAmount!=0){
					ssbm.setFinishedPercent(Double.valueOf(String.format("%.2f",StringUtils.mul(StringUtils.div(group_finishedCrewAmount,group_totalCrewAmount, 4),100))));
				}				
				//获取某个分组的已拍摄天数、日均拍摄戏量
				Integer shotedDays=1;
				if(shotedDays!=null && shotedDays!=0){
					ssbm.setShootedDays(shotedDays);
					ssbm.setDailyFinishedCrewAmount(Double.valueOf(String.format("%.2f",StringUtils.div(group_finishedCrewAmount, shotedDays,2))));	
				}
				ssbmMap.put(groupName, ssbm);
			}
			item_ssbmMap.put("group", ssbmMap);
		}
		return item_ssbmMap;
	}
	
	/**
	 * 按天统计
	 */
	public Map<String,Object> statisticsShootScheduleByDay(String crewId, int statisticsType) throws SQLException {
		return this.shootReportDao.statisticsShootScheduleByDay(crewId, statisticsType);
	}
	
	/**
	 * 获取剧组的总拍摄天数、已拍摄天数、日均拍摄戏量
	 * @param crewId
	 * @return
	 * @throws SQLException
	 */
	public int[] getShotDaysBycrewId(String crewId)throws SQLException {
		return this.shootReportDao.getShotDaysBycrewId(crewId);
	}

}
