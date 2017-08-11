package com.xiaotu.makeplays.view.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.view.dao.ViewAdvertMapDao;
import com.xiaotu.makeplays.view.model.ViewAdvertMapModel;

/**
 * 植入广告
 * @author xuchangjian
 */
@Service
public class ViewAdvertMapService {

	@Autowired
	private ViewAdvertMapDao viewAdvertMapDao;
	
	/**
	 * 保存关联关系
	 * 该方法中加入了判断关联关系是否存在的逻辑
	 * @param viewId	场景ID
	 * @param advertId	植入广告ID
	 * @param crewId	剧组ID
	 * @throws Exception 
	 */
	public String addViewAdvertMapInfo(String viewId, String advertId, String advertType, String crewId) throws Exception {
		
		String mapId = "";
		
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("advertId", advertId);
		conditionMap.put("viewId", viewId);
		conditionMap.put("crewId", crewId);
		conditionMap.put("advertType", advertType);
		List<ViewAdvertMapModel> viewAdvertMapList = this.viewAdvertMapDao.queryManyByMutiCondition(conditionMap, null);
		if (viewAdvertMapList == null || viewAdvertMapList.size() == 0) {
			ViewAdvertMapModel map = new ViewAdvertMapModel();
			mapId = UUIDUtils.getId();
			map.setAdvertId(advertId);
			map.setCrewId(crewId);
			map.setMapId(mapId);
			map.setViewId(viewId);
			map.setAdvertType(advertType);
			this.addOneMap(map);
		} else {
			mapId = viewAdvertMapList.get(0).getAdvertId();
		}
		
		return mapId;
	}
	
	/**
	 * 保存关联关系
	 * @param viewAdvertMap
	 * @throws Exception 
	 */
	public String addOneMap(ViewAdvertMapModel viewAdvertMap) throws Exception {
		this.viewAdvertMapDao.add(viewAdvertMap);
		return viewAdvertMap.getAdvertId();
	}
}
