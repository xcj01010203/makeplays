package com.xiaotu.makeplays.crew.service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.crew.dao.RoleCrewAmountReportDao;
import com.xiaotu.makeplays.crew.model.CrewAmountModel;
import com.xiaotu.makeplays.crew.model.RoleCrewAmountModel;
import com.xiaotu.makeplays.crew.model.RolecrewAmountBaseModel;
import com.xiaotu.makeplays.utils.ComparatorRoleTotalPlayAmount;
import com.xiaotu.makeplays.utils.ReportConst;
import com.xiaotu.makeplays.utils.StringUtils;

/**
 *  角色戏量统计
 * @author lma
 *
 */
@Service
public class RoleCrewAmountReportService {
	@Autowired
	private RoleCrewAmountReportDao  rolePlayAmountReportDao;
	/**
	 * 按集统计
	 * @param playId
	 * @param roleType
	 * @throws Exception
	 */
	public RoleCrewAmountModel statisticsRolePlayAmoutBySet(String playId,int roleType,String roleName) throws Exception{
		
		RoleCrewAmountModel rpam=new RoleCrewAmountModel();
		
		//获取当前剧的所有集数
		List<Integer> setNos=rolePlayAmountReportDao.getSetNosBycrewId(playId);
		rpam.setSetNos(setNos);
		//获取每个角色每集的戏量及戏量合计
		List<Map<String, Object>> mapList=rolePlayAmountReportDao.getStatisticsRolecrewAmoutBySet(playId, roleType,roleName);
		List<RolecrewAmountBaseModel> rpabmList=new ArrayList<RolecrewAmountBaseModel>();
		int j= 0;
		for (int i=0; i < mapList.size(); i++) {
			if(j==mapList.size()){
				break;
			}
			Map<String, Object> map=mapList.get(i);
			RolecrewAmountBaseModel rpabm=new RolecrewAmountBaseModel();
			String roleId=(String) map.get("roleId");
			rpabm.setRoleName((String)map.get("roleName"));
			rpabm.setActorName((String)map.get("actorName"));
			double role_playAmountByView=0;//按场统计当前角色的总戏量
			double role_playAmountByPage=0;//按页统计当前角色的总戏量
			
			Map<Integer,CrewAmountModel> setNo_playAmountMap=new HashMap<Integer, CrewAmountModel>();
			CrewAmountModel pam=new CrewAmountModel();
			int viewNo=map.get("seriesNo")!=null?Integer.parseInt(map.get("seriesNo").toString()):0;
			pam.setName(viewNo+"");
			double crewAmountByView=Double.valueOf(map.get("crewAmountByview").toString());	
			pam.setcrewAmountByview(crewAmountByView);
			double playAmountByPage=0;
			if(map.get("crewAmountByPage")!=null&&map.get("crewAmountByPage")!=""){
				 playAmountByPage=Double.valueOf(map.get("crewAmountByPage").toString());	
			}
			pam.setcrewAmountByPage(playAmountByPage);

			setNo_playAmountMap.put(viewNo, pam);
			role_playAmountByView=crewAmountByView;
			role_playAmountByPage=playAmountByPage;
			for (j = i+1; j < mapList.size(); j++) {
				Map<String, Object> map2=mapList.get(j);
				String next_roleId=(String) map2.get("roleId");
				if(next_roleId.equals(roleId)){
					//统计某角色在出现集中的戏量，及戏量合计
					CrewAmountModel	pams=new CrewAmountModel();
					viewNo=map2.get("seriesNo")!=null?Integer.parseInt(map2.get("seriesNo").toString()):0;
					pams.setName(viewNo+"");
					crewAmountByView=Double.valueOf(map2.get("crewAmountByview")+"");	
					pams.setcrewAmountByview(crewAmountByView);
					playAmountByPage=Double.valueOf(map2.get("crewAmountByPage").toString());	
					pams.setcrewAmountByPage(playAmountByPage);
					setNo_playAmountMap.put(viewNo, pams);
					role_playAmountByView=StringUtils.add(role_playAmountByView,crewAmountByView);
					role_playAmountByPage=StringUtils.add(role_playAmountByPage,playAmountByPage);
				}else{					
					//移动到上一行
					i=j-1;
					break;
				}
			}
			//将不出现当前角色的集的戏量补0lma
			if(setNos!=null && setNos.size()>0){
				List<CrewAmountModel> playAmountModelList=new ArrayList<CrewAmountModel>();
				for(int s=0;s<setNos.size();s++){
					CrewAmountModel cam=setNo_playAmountMap.get(setNos.get(s));
						playAmountModelList.add(cam);
				}
				rpabm.setCrewAmountModelList(playAmountModelList);
			}
			rpabm.setTotalCrewAmountByView(role_playAmountByView);
			rpabm.setTotalCrewAmountByPage(role_playAmountByPage);
			rpabmList.add(rpabm);			
		}
		//按角色总戏量进行排序
		ComparatorRoleTotalPlayAmount crtpa=new ComparatorRoleTotalPlayAmount();
		Collections.sort(rpabmList, crtpa);
		rpam.setRpabmList(rpabmList);
		return rpam;
	}
	/**
	 * 按拍摄地点统计
	 * @param playId
	 * @param roleType
	 * @throws SQLException
	 */
	public RoleCrewAmountModel statisticsRolePlayAmoutByShootAddress(String playId, int roleType)  throws Exception {

		
		RoleCrewAmountModel rpam=new RoleCrewAmountModel();
		
		//获取当前剧的所有拍摄地点
		List<String> addressList=rolePlayAmountReportDao.getShootAddressListBycrewId(playId);
		if(addressList.contains(ReportConst.SHOOT_ADDRESS_NULL_TITLE)){
			addressList.remove(ReportConst.SHOOT_ADDRESS_NULL_TITLE);
		}
		addressList.add(0, ReportConst.SHOOT_ADDRESS_NULL_TITLE);
		rpam.setShootAddressList(addressList);
		//获取每个角色每个拍摄地点的戏量及戏量合计	
		List<Map<String, Object>> mapList=rolePlayAmountReportDao.getStatisticsRolecrewAmoutByShootAddress(playId, roleType);
		List<RolecrewAmountBaseModel> rpabmList=new ArrayList<RolecrewAmountBaseModel>();
		int j = 0;
		for (int i = 0; i <mapList.size(); i++) {
			if(j==mapList.size()){
				break;
			}
				Map<String, Object> map=mapList.get(i);
			RolecrewAmountBaseModel rpabm=new RolecrewAmountBaseModel();
			String roleId=(String) map.get("roleId");
			rpabm.setRoleName((String)map.get("roleName"));
			rpabm.setActorName((String)map.get("actorName"));
			double role_playAmountByScene=0;//按场统计当前角色的总戏量
			double role_playAmountByPage=0;//按页统计当前角色的总戏量
			
			Map<String,CrewAmountModel> shootAddress_playAmountMap=new HashMap<String, CrewAmountModel>();//每个拍摄地点对应的戏量
			CrewAmountModel pam=new CrewAmountModel();
			String shootLocation=map.get("shootLocation").toString();
			pam.setName(shootLocation);
			double crewAmountByView=Double.valueOf(map.get("crewAmountByview").toString());	
			pam.setcrewAmountByview(crewAmountByView);
			double crewAmountByPage=0;
			if(map.get("crewAmountByPage")!=null && !map.get("crewAmountByPage").equals("")){
			 crewAmountByPage=Double.valueOf(map.get("crewAmountByPage").toString());
			}
			pam.setcrewAmountByPage(crewAmountByPage);
			shootAddress_playAmountMap.put(shootLocation, pam);
			role_playAmountByScene=crewAmountByView;
			role_playAmountByPage=crewAmountByPage;
			for (j = i+1; j < mapList.size(); j++) {
				Map<String, Object> map2=mapList.get(j);
				String next_roleId=(String) map2.get("roleId");
				if(next_roleId.equals(roleId)){
					//统计某角色每个拍摄地点的戏量，及戏量合计
					pam=new CrewAmountModel();
					shootLocation=(String) map2.get("shootLocation");
					pam.setName(shootLocation);
					crewAmountByView=Double.valueOf(map2.get("crewAmountByview").toString());	
					pam.setcrewAmountByview(crewAmountByView);
					crewAmountByPage=Double.valueOf(map2.get("crewAmountByPage").toString());	
					pam.setcrewAmountByPage(crewAmountByPage);
					shootAddress_playAmountMap.put(shootLocation, pam);
					role_playAmountByScene=StringUtils.add(role_playAmountByScene,crewAmountByView);
					role_playAmountByPage=StringUtils.add(role_playAmountByPage,crewAmountByPage);
				}else{				
					//移动到上一行
					i=j-1;
					break;
				}
			}
			//将不出现当前角色的拍摄地点的戏量补0
			if(addressList!=null && addressList.size()>0){
				List<CrewAmountModel> playAmountModelList=new ArrayList<CrewAmountModel>();
				for(int s=0;s<addressList.size();s++){
					CrewAmountModel temp_playAmount=shootAddress_playAmountMap.get(addressList.get(s));
					playAmountModelList.add(temp_playAmount);
				}
				rpabm.setCrewAmountModelList(playAmountModelList);
			}
			rpabm.setTotalCrewAmountByView(role_playAmountByScene);
			rpabm.setTotalCrewAmountByPage(role_playAmountByPage);
			rpabmList.add(rpabm);
		}
		//按角色总戏量进行排序
		ComparatorRoleTotalPlayAmount crtpa=new ComparatorRoleTotalPlayAmount();
		Collections.sort(rpabmList, crtpa);
		rpam.setRpabmList(rpabmList);
		return rpam;
	}
	/**
	 * 按拍摄地点主场景统计
	 * @param playId
	 * @param roleType
	 * @return
	 * @throws Exception
	 */
	public RoleCrewAmountModel statisticsByAddress(String playId,int roleType,String roleName)throws Exception {
		
		RoleCrewAmountModel rpam=new RoleCrewAmountModel();
		List<RolecrewAmountBaseModel> rpabmList=new ArrayList<RolecrewAmountBaseModel>();
		//按拍摄地点及主场景分组，统计角色的戏量	
		int j=0;
		List<Map<String, Object>> maplist=rolePlayAmountReportDao.getStatisticsByAddress(playId, roleType,roleName);
		for (int i = 0; i < maplist.size(); i++) {
			if(j==maplist.size()){
				break;
			}
			Map<String, Object> map=maplist.get(i);
			RolecrewAmountBaseModel rpabm=new RolecrewAmountBaseModel();
			String roleId=(String) map.get("roleId");
			rpabm.setRoleName(map.get("roleName").toString());
			double role_playAmountByScene=0;//按场统计当前角色的总戏量
			double role_playAmountByPage=0;//按页统计当前角色的总戏量
			//统计当前角色所在拍摄地点的各主场景的戏量
			List<CrewAmountModel> pamList=new ArrayList<CrewAmountModel>();//当前角色所在的拍摄地点列表
			CrewAmountModel pam=new CrewAmountModel();
			String shootAddress=(String) map.get("shootLocation");
			pam.setName(shootAddress);
			List<CrewAmountModel> child_pamList=new ArrayList<CrewAmountModel>();//子级列表，当前拍摄地点下的主场景列表
			pam.setChildcrewAmountModelList(child_pamList);
			pamList.add(pam);
			
			double shootAddress_playAmountByView=0;//按场统计某角色下当前拍摄地点的戏量
			double shootAddress_playAmountByPage=0;//按页统计某角色下当前拍摄地点的戏量
			int paxu=1;
			for (j=i; j < maplist.size(); j++) {
				Map<String,Object> map2=maplist.get(j);
				String next_roleId=(String) map2.get("roleId");
				if(next_roleId.equals(roleId)){
					String next_shootAddress=(String) map2.get("shootLocation");
					if(next_shootAddress.equals(shootAddress)){
						//当前拍摄地点下的主场景
						CrewAmountModel child_pam=new CrewAmountModel();//子级
							String sceneAddress=(String) map2.get("viewAddress");
							child_pam.setName(sceneAddress);	
							child_pam.setViewType(map2.get("viewType")!=null?(Integer)map2.get("viewType"):0);//lma新增文武戏
							child_pam.setAtmosphereName(map2.get("atmosphereName")!=null?map2.get("atmosphereName").toString():"");
							child_pam.setSite(map2.get("site")!=null?map2.get("site").toString():"");//内外景
							if(paxu==1){
								child_pam.setAddressName(shootAddress);//lma新增拍摄地点
								paxu++;
							}
							Integer playAmountByScene=Integer.parseInt(map2.get("crewAmountByview").toString());
							child_pam.setcrewAmountByview(playAmountByScene);
							double playAmountByPage=0;
							if(map2.get("crewAmountByPage")!=null&& !map2.get("crewAmountByPage").equals("")){
								 playAmountByPage=Double.valueOf(map2.get("crewAmountByPage").toString());
							}
								child_pam.setcrewAmountByPage(playAmountByPage);
								child_pamList.add(child_pam);
							
							shootAddress_playAmountByView=StringUtils.add(shootAddress_playAmountByView,playAmountByScene);
							shootAddress_playAmountByPage=StringUtils.add(shootAddress_playAmountByPage,playAmountByPage);						
							pam.setcrewAmountByview(shootAddress_playAmountByView);
							pam.setcrewAmountByPage(shootAddress_playAmountByPage);
							role_playAmountByScene=StringUtils.add(role_playAmountByScene,playAmountByScene);
							role_playAmountByPage=StringUtils.add(role_playAmountByPage,playAmountByPage);		
						}else{
							shootAddress_playAmountByView=0;//按场统计某角色下当前拍摄地点的戏量
							shootAddress_playAmountByPage=0;//按页统计某角色下当前拍摄地点的戏量
							pam=new CrewAmountModel();
							shootAddress=next_shootAddress;
							pam.setName(shootAddress);
							child_pamList=new ArrayList<CrewAmountModel>();//子级列表，当前拍摄地点下的主场景列表
							pam.setChildcrewAmountModelList(child_pamList);
							pamList.add(pam);
							j=j-1;paxu=1;
					}
				}else{				
					//移动到上一行
					paxu=1;
					i=j-1;
					break;
				}
			}
			rpabm.setTotalCrewAmountByPage(role_playAmountByPage);
			rpabm.setTotalCrewAmountByView(role_playAmountByScene);
			ComparatorRoleTotalPlayAmount crtpa=new ComparatorRoleTotalPlayAmount();
			Collections.sort(pamList, crtpa);
			rpabm.setCrewAmountModelList(pamList);
			rpabmList.add(rpabm);
		}
		//按角色总戏量进行排序
		ComparatorRoleTotalPlayAmount crtpa=new ComparatorRoleTotalPlayAmount();
		Collections.sort(rpabmList, crtpa);
		rpam.setRpabmList(rpabmList);
		return rpam;
	}
/**
 * 按场景统计
 */
	public RoleCrewAmountModel statisticsBySceneAddress(String playId,int roleType)throws Exception {
		RoleCrewAmountModel rpam=new RoleCrewAmountModel();
		int sceneCount = 0;
		double pageCount = 0d;
		//得到场景的集合
		List<String> sceneAddressList =rolePlayAmountReportDao.getviewAddressListBycrewId(playId);
		
		List<RolecrewAmountBaseModel> rpabmList=new ArrayList<RolecrewAmountBaseModel>();
		List<Map<String, Object>> maplist=rolePlayAmountReportDao.getStatisticsByviewAddress(playId, roleType);
		int j=0;
		for (int i = 0; i < maplist.size(); i++) {
			if(j==maplist.size()){
				break;
			}
			Map<String, Object> map=maplist.get(i);
			RolecrewAmountBaseModel rpabm=new RolecrewAmountBaseModel();
			List<CrewAmountModel> playAmountModelList = new ArrayList<CrewAmountModel>();
			String roleId = (String) map.get("roleId");
			rpabm.setRoleName((String)map.get("roleName"));
			rpabm.setActorName((String)map.get("actorName"));
				String sceneAddressName1 = (String) map.get("location");
				CrewAmountModel pam = new CrewAmountModel();
				pam.setName(sceneAddressName1);
				pam.setcrewAmountByPage(map.get("pageCount")!=null?Double.valueOf(String.format("%.2f",map.get("pageCount"))):0);
				pam.setcrewAmountByview(Double.valueOf(map.get("viewCount").toString()));
				sceneCount = Integer.parseInt(map.get("viewCount").toString());
				pageCount =map.get("pageCount")!=null?Double.valueOf(map.get("pageCount").toString()):0;
				playAmountModelList.add(pam);
			for ( j=i+1; j <maplist.size(); j++) {
				Map<String, Object> map2=maplist.get(j);
				String role_Id = (String) map2.get("roleId");				
				if (roleId.equals(role_Id)) {
					String sceneAddressName = (String) map2.get("location");
					 pam = new CrewAmountModel();
					pam.setName(sceneAddressName);
					pam.setcrewAmountByPage(map2.get("pageCount")!=null?Double.valueOf(map2.get("pageCount").toString()):0);
					pam.setcrewAmountByview(Double.valueOf(map2.get("viewCount").toString()));
					sceneCount += Integer.parseInt(map2.get("viewCount").toString());
					pageCount +=map2.get("pageCount")!=null?Double.valueOf(map2.get("pageCount").toString()):0;
					playAmountModelList.add(pam);
				} else {
					rpabm.setCrewAmountModelList(playAmountModelList);
					i=j-1;
					break;
				}
			}
			rpabm.setTotalCrewAmountByPage(Double.valueOf(String.format("%.2f",pageCount)));

			rpabm.setTotalCrewAmountByView(sceneCount);
			rpabmList.add(rpabm);
			sceneCount = 0;
			pageCount = 0d;
		}
		
		int shootAddressCount = 0;//每个角色的场景个数
		double page = 0;
		double sceneNum = 0;
		List<CrewAmountModel>  lists = new ArrayList<CrewAmountModel>();
		if (rpabmList != null && rpabmList.size() > 0) {
			//遍历每个角色对象
			for (RolecrewAmountBaseModel rolePlayAmountBaseModel : rpabmList) {
				List<CrewAmountModel>  list = rolePlayAmountBaseModel.getCrewAmountModelList();
				double scenePageCount = rolePlayAmountBaseModel.getTotalCrewAmountByPage();
				double scene_Count = rolePlayAmountBaseModel.getTotalCrewAmountByView();
				//根据剧中的所有场景与角色参与的场景相对应
				if (sceneAddressList !=null && sceneAddressList.size() > 0) {
					for (int u = 0;u < sceneAddressList.size();u++) {
						String str = sceneAddressList.get(u);
						CrewAmountModel play_AmountModel = new CrewAmountModel();
						//根据剧中的场景设置lists长度
						lists.add(play_AmountModel);
						if (list != null && list.size() > 0) {
							for (int k = 0; k < list.size(); k++) {
								CrewAmountModel amount = list.get(k);
								//用于页面数据展示，将角色所参与的场景与表头场景相对应
								if (str.equals(amount.getName())) {
									page += amount.getcrewAmountByPage();
									sceneNum += amount.getcrewAmountByview();
									lists.remove(u);
									lists.add(u,amount);
									shootAddressCount++;
								}
							}
						}
					}
				}
				CrewAmountModel patm = new CrewAmountModel();
				patm.setName(ReportConst.SHOOT_ADDRESS_NULL_TITLE);
				patm.setcrewAmountByview(scene_Count-sceneNum);
				patm.setcrewAmountByPage(Double.valueOf(String.format("%.2f",scenePageCount>page?(scenePageCount-page):0)));
				lists.add(0,patm);
				rolePlayAmountBaseModel.setCrewAmountModelList(lists);
				rolePlayAmountBaseModel.setShootAddressCount(shootAddressCount);
				lists = new ArrayList<CrewAmountModel>();
				shootAddressCount = 0;
				page = 0d;
				sceneNum = 0d;
			}
		}
		sceneAddressList.add(0, ReportConst.SHOOT_ADDRESS_NULL_TITLE);
		ComparatorRoleTotalPlayAmount crtpa=new ComparatorRoleTotalPlayAmount();
		Collections.sort(rpabmList, crtpa);
		rpam.setRpabmList(rpabmList);
		rpam.setShootAddressList(sceneAddressList);
		return rpam;
	}
	
	/**
	 * 获取演员角色按天统计
	 */
	public Map<String,Object> getViewRoleDayStatistic(String viewRoleName,String crewId,Integer type){
		Map<String,Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> ret = this.rolePlayAmountReportDao.getViewRoleDayStatistic(viewRoleName, crewId);
		List<Object> dateList = new ArrayList<Object>();
		List<Object> dataList = new ArrayList<Object>();
		if(ret!=null && ret.size()>0){
			for (Map<String,Object> obj : ret) {
				dateList.add(obj.get("noticeDate"));
				if(type == ReportConst.STATISTICS_TYPE_SCENE){ //按场统计
					dataList.add(obj.get("viewCount"));
				}else if(type == ReportConst.STATISTICS_TYPE_PAGE){  //按月统计
					dataList.add(obj.get("pageCount"));
				}
			}
		}
		map.put("xaxis", dateList);
		map.put("yaxis", dataList);
		return map;
	}
	
}
