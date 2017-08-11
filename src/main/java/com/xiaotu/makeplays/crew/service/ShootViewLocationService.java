package com.xiaotu.makeplays.crew.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.crew.dao.ShootViewLocationDao;
import com.xiaotu.makeplays.crew.model.ShootLocationHomeModel;
import com.xiaotu.makeplays.crew.model.ShootLocationModel;
import com.xiaotu.makeplays.crew.model.ShootLocationtBaseModel;

@Service
public class ShootViewLocationService {
	@Autowired
	private ShootViewLocationDao  shootViewLocationDao;
	
	public List<ShootLocationModel> getShootLocationByCrewId(String crewId) throws Exception {
		
		List<ShootLocationModel> locationList=new ArrayList<ShootLocationModel>();
		try {
			//拍摄地址list
            locationList=this.getLocationList(crewId);
			//获取主场景列表
			List<ShootLocationHomeModel> homeViewList=this.getMainLocation(crewId);
			//获取次场景列表
			List<ShootLocationtBaseModel> baseViewList=this.getLastLocation(crewId);
			//主场景列表不为空的情况下
			if(null!=homeViewList){
				for(int i=0;i<homeViewList.size();i++){
					List<ShootLocationtBaseModel> viewBaseList=new ArrayList<ShootLocationtBaseModel>();
					//主场景实体
					ShootLocationHomeModel homeBase=homeViewList.get(i);
					if(homeBase.getHomeView().getFirstViewLocation()==null){
						homeBase.getHomeView().setFirstViewLocation("待定");
					}
					//次场景列表不为空的情况下
					if(baseViewList!=null && baseViewList.size()>0){
						//遍历次场景
						for(int j=0;j<baseViewList.size();j++){
							ShootLocationtBaseModel viewBase=baseViewList.get(j);
							if(viewBase.getFirstViewLocation()==null){
								viewBase.setFirstViewLocation("待定");
							}
							//次场景名称为空的情况下默认为“待定”
							if(viewBase.getSecondViewLocation()==null){
								viewBase.setSecondViewLocation("待定");
							}
							if(viewBase.getFirstViewLocation().equals(homeBase.getHomeView().getFirstViewLocation())){
								if(viewBase.getShootLocationId().equals(homeBase.getHomeView().getShootLocationId())){
									
									viewBaseList.add(viewBase);
								}
							}
						}
					}
					homeBase.setLocationBaseList(viewBaseList);
				}
			}
			//遍历拍摄场景
			if(locationList!=null){
				//遍历拍摄场景
				for(int i=0;i<locationList.size();i++){
					List<ShootLocationHomeModel> homeList=new ArrayList<ShootLocationHomeModel>();
					ShootLocationModel view=locationList.get(i);
					if(homeViewList!=null && homeViewList.size()>0){
						for(int j=0;j<homeViewList.size();j++){
							ShootLocationHomeModel homeView=homeViewList.get(j);
							if(homeView.getHomeView().getFirstViewLocation()==null){
								homeView.getHomeView().setFirstViewLocation("待定");
							}
							if(homeView.getHomeView().getShootLocationId().equals(view.getShootLocationId())){
								homeList.add(homeView);
							}
						}
					}
					view.setHomeViewList(homeList);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return locationList;
	}
	
	/**
	 * 拍摄地址
	 */
	public List<ShootLocationModel> getLocationList(String crewId)throws Exception{
		List<ShootLocationModel> locationList=new ArrayList<ShootLocationModel>();
		List<Map<String, Object>> mapList= shootViewLocationDao.getLocationList(crewId);
		for (Map<String, Object> map : mapList) {
			ShootLocationModel location=new ShootLocationModel();
			location.setShootLocationId(map.get("shootLocationId")!=null?map.get("shootLocationId").toString():"");
			location.setShootLocation(map.get("shootLocation")!=null?map.get("shootLocation").toString():null);
			location.setLocationCountTotal(Double.parseDouble(map.get("viewNo")!=null?map.get("viewNo").toString():"0"));
			location.setLocationPageTotal(Double.parseDouble(map.get("pageCount")!=null?map.get("pageCount").toString():"0"));
			locationList.add(location);
		}
		return locationList;
	}
	/**
	 * 主场景
	 */
	public List<ShootLocationHomeModel> getMainLocation(String crewId)throws Exception{
		List<ShootLocationHomeModel> list=new ArrayList<ShootLocationHomeModel>();
		List<Map<String, Object>> listMaps=shootViewLocationDao.getMainLocation(crewId);
		for (Map<String, Object> map : listMaps) {
			ShootLocationHomeModel home=new ShootLocationHomeModel();
			ShootLocationtBaseModel base=new ShootLocationtBaseModel();

			base.setFirstViewLocation(map.get("firstViewLocation")!=null?map.get("firstViewLocation").toString():null);
			base.setSecondViewLocation(map.get("secondViewLocation")!=null?map.get("secondViewLocation").toString():null);
			base.setViewId(map.get("viewId")!=null?map.get("viewId").toString():null);
			base.setShootLocation(map.get("shootLocation")!=null?map.get("shootLocation").toString():null);
			base.setShootLocationId(map.get("shootLocationId")!=null?map.get("shootLocationId").toString():"");
			home.setCrewByHomePage(Double.parseDouble(map.get("pageCount")!=null?map.get("pageCount").toString():"0"));
			home.setCrewByHomeView(Double.parseDouble(map.get("viewNo")!=null?map.get("viewNo").toString():"0"));
			home.setHomeView(base);
			list.add(home);
		}
		return list;
	}
	/**
	 * 次场景
	 */
	public List<ShootLocationtBaseModel> getLastLocation(String crewId)throws Exception{
		List<ShootLocationtBaseModel> list=new ArrayList<ShootLocationtBaseModel>();
		List<Map<String, Object>> listmap=shootViewLocationDao.getLastLocation(crewId);
		for (Map<String, Object> map : listmap) {
			ShootLocationtBaseModel location=new ShootLocationtBaseModel();
			location.setPageCount(Double.parseDouble(map.get("pageCount")!=null?map.get("pageCount").toString():"0"));
			location.setViewNo(Double.parseDouble(map.get("viewNo")!=null?map.get("viewNo").toString():"0"));
			location.setFirstViewLocation(map.get("firstViewLocation")!=null?map.get("firstViewLocation").toString():null);
			location.setSecondViewLocation(map.get("secondViewLocation")!=null?map.get("secondViewLocation").toString():null);
			location.setViewId(map.get("viewId")!=null?map.get("viewId").toString():null);
			location.setShootLocation(map.get("shootLocation")!=null?map.get("shootLocation").toString():null);
			location.setShootLocationId(map.get("shootLocationId")!=null?map.get("shootLocationId").toString():"");
			list.add(location);
		}
		return list;
	}
}
