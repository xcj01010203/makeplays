package com.xiaotu.makeplays.view.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.roleactor.dao.ViewRoleDao;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.view.controller.filter.ViewFilter;
import com.xiaotu.makeplays.view.dao.ViewLocationDao;
import com.xiaotu.makeplays.view.dao.ViewLocationMapDao;
import com.xiaotu.makeplays.view.model.ViewLocationMapModel;
import com.xiaotu.makeplays.view.model.ViewLocationModel;

/**
 * 场景地点信息
 * @author xuchangjian
 */
@Service
public class ViewLocationService {
	
	private DecimalFormat df = new DecimalFormat("0.00");
	
	@Autowired
	private ViewLocationDao viewLocationDao;
	
	@Autowired
	private ViewLocationMapDao viewLocationMapDao;
	
	@Autowired
	private ViewRoleDao viewRoleDao;

	/**
	 * 根据场景ID删除场景地点信息
	 * 包括场景地点信息和场景和场景地点的关联关系
	 * @param viewId
	 * @throws Exception 
	 */
	public void deleteManyByViewId(String viewId) throws Exception {
		List<String> viewLocationIdList = new ArrayList<String>();	//场景地点的ID列表
		
		List<ViewLocationMapModel> mapList = this.viewLocationMapDao.queryManyByViewId(viewId);
		for (ViewLocationMapModel map : mapList) {
			viewLocationIdList.add(map.getLocationId());
		}
		
		//删除场景地点信息
		if (viewLocationIdList.size() > 0) {
			String[] strArray = new String[viewLocationIdList.size()];
			this.viewLocationDao.deleteMany(viewLocationIdList.toArray(strArray), "location", "tab_view_location");
		}
		
		//删除场景和地点的关联关系
		this.viewLocationMapDao.deleteManyByViewId(viewId);
	}
	
	/**
	 * 根据场景ID查找对应的场景地址信息
	 * @param viewId
	 * @return
	 */
	public List<ViewLocationModel> queryManyByViewId (String viewId) {
		return this.viewLocationDao.queryManyByViewId(viewId);
	}
	
	/**
	 * 通过剧本ID查找
	 * @param crewId 剧本ID
	 * @return
	 */
	public List<ViewLocationModel> queryManyByCrewId(String crewId) {
		return this.viewLocationDao.queryManyByCrewId(crewId);
	}
	

	/**
	 * 保存场景和场景地点之间的关联关系
	 * 该方法中加入了判断关联关系是否已经存在的业务逻辑
	 * @param viewId
	 * @param locationId
	 * @param crewId
	 * @return 关联关系的ID
	 * @throws Exception 
	 */
	public String saveViewLoationMap(String viewId, String locationId, String crewId) throws Exception {
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("viewId", viewId);
		conditionMap.put("locationId", locationId);
		conditionMap.put("crewId", crewId);
		
		List<ViewLocationMapModel> viewLocationMapList = this.viewLocationMapDao.queryManyByMutiCondition(conditionMap, null);
		
		String mapId = "";
		if (viewLocationMapList == null || viewLocationMapList.size() == 0) {
			ViewLocationMapModel viewLocationMap = new ViewLocationMapModel();
			
			mapId = UUIDUtils.getId();
			viewLocationMap.setLocationId(locationId);
			viewLocationMap.setMapId(mapId);
			viewLocationMap.setCrewId(crewId);
			viewLocationMap.setViewId(viewId);
			
			this.viewLocationMapDao.add(viewLocationMap);
		} else {
			mapId = viewLocationMapList.get(0).getMapId();
		}
		
		return mapId;
	}
	
	/**
	 * 主场景汇总
	 * @param crewId
	 * @param shootLocation 查询条件：拍摄地点
	 * @param location 查询条件：主场景
	 * @param crewRole 查询条件：场景角色
	 * @param flag 1:出现即可，2：不出现，3：同时出现，4：不同时出现
	 * @return
	 */
	public List<Map<String, Object>> queryViewLocationStatistic(String crewId,
			int locationType, ViewFilter filter, String sortField) {
		//主场景汇总信息，包括拍摄地、主场景ID、主场景名称、场数/页数、气氛
		List<Map<String, Object>> viewLocationList = this.viewLocationDao
				.queryViewLocationStatistic(crewId, locationType, filter, sortField);
		if(viewLocationList == null || viewLocationList.size() == 0) {
			return null;
		}
		//主场景、场景角色关联关系
		List<Map<String, Object>> locationRoleList = this.viewRoleDao.queryLocationRoleListByCrewId(crewId, locationType,filter);
		Map<String, List<Map<String, Object>>> locationRoleListMap = new HashMap<String, List<Map<String, Object>>>();
		if(locationRoleList != null && locationRoleList.size() > 0) {
			//筛选每个场景包含的场景角色
			for(Map<String, Object> map : locationRoleList) {
				String shootLocationId = map.get("shootLocationId") + "";
				String locationId = map.get("locationId") + "";
				
				String id = shootLocationId + "|" + locationId;

				List<Map<String, Object>> roleList = null;
				if(locationRoleListMap.containsKey(id)) {
					roleList = locationRoleListMap.get(id);
				} else {
					roleList = new ArrayList<Map<String,Object>>();
					locationRoleListMap.put(id, roleList);
				}
				roleList.add(map);
			}
			//将场景角色放入场景汇总信息中
			for(Map<String, Object> map : viewLocationList) {
				String shootLocationId = map.get("shootLocationId") + "";
				String locationId = map.get("locationId") + "";
				String id = shootLocationId + "|" + locationId;
				if(locationRoleListMap.containsKey(id)) {
					map.put("roleList", locationRoleListMap.get(id));
				} else {
					map.put("roleList", new ArrayList<Map<String,Object>>());
				}
			}
		} else {
			for(Map<String, Object> map : viewLocationList) {
				map.put("roleList", new ArrayList<Map<String,Object>>());
			}
		}
		return viewLocationList;
	}
	
	
}