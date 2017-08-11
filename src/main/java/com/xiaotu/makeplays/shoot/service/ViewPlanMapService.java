package com.xiaotu.makeplays.shoot.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.goods.dao.GoodsInfoDao;
import com.xiaotu.makeplays.goods.model.constants.GoodsType;
import com.xiaotu.makeplays.roleactor.dao.ViewRoleDao;
import com.xiaotu.makeplays.roleactor.model.constants.ViewRoleType;
import com.xiaotu.makeplays.shoot.dao.ShootPlanDao;
import com.xiaotu.makeplays.shoot.dao.ViewPlanMapDao;
import com.xiaotu.makeplays.shoot.model.ShootPlanModel;
import com.xiaotu.makeplays.shoot.model.ViewPlanMapModel;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.view.controller.filter.ViewFilter;
import com.xiaotu.makeplays.view.dao.InsideAdvertDao;
import com.xiaotu.makeplays.view.dao.ViewInfoDao;
import com.xiaotu.makeplays.view.dao.ViewLocationDao;
import com.xiaotu.makeplays.view.model.ViewInfoModel;
import com.xiaotu.makeplays.view.model.constants.LocationType;

/**
 * 场景和拍摄计划的关联关系
 * @author xuchangjian
 */
@Service
public class ViewPlanMapService {

	@Autowired
	private ViewPlanMapDao viewPlanMapDao;
	
	@Autowired
	private ViewInfoDao viewInfoDao;
	
	@Autowired
	private ViewLocationDao viewLocationDao;
	
	@Autowired
	private ViewRoleDao viewRoleDao;
	
	@Autowired
	private InsideAdvertDao insideAdvertDao;
	
	@Autowired
	private ShootPlanDao shootPlanDao;
	
	@Autowired
	private GoodsInfoDao goodsInfoDao;
	
	/**
	 * 新增关联关系
	 * @param viewPlanMapModel
	 * @throws Exception
	 */
	public void addViewPlanMap(ViewPlanMapModel viewPlanMapModel) throws Exception {
		this.viewPlanMapDao.add(viewPlanMapModel);
	}
	
	/**
	 * 更新关联关系
	 * @param scenePlanMapModel
	 * @throws Exception
	 */
	public void updateScenePlanMap(ViewPlanMapModel scenePlanMapModel) throws Exception {
		this.viewPlanMapDao.update(scenePlanMapModel, "mapId");
	}
	
	/**
	 * 批量更新拍摄计划和场景的关联关系
	 * @param viewPlanMapList
	 * @throws Exception 
	 */
	public void updateMany(List<ViewPlanMapModel> viewPlanMapList) throws Exception {
		for (ViewPlanMapModel viewPlanMap : viewPlanMapList) {
			this.updateScenePlanMap(viewPlanMap);
		}
	}
	
	/**
	 * 根据拍摄计划ID查询和拍摄计划相关的场景完整信息
	 * 该方法不仅仅查询出场景表中的所有信息，
	 * 还查询出每个场景的计划拍摄地点、主场景、次场景、三级场景、主要演员、特约演员、群众演员信息
	 * @param planId
	 * @param crewId
	 * @param page
	 * @param inPlan	是否在计划中
	 * @return
	 */
	public List<Map<String, Object>> queryFullViewInfoByPlanId(String planId, String crewId, Page page, Boolean inPlan, ViewFilter filter) {
		List<Map<String, Object>> planViewList = new ArrayList<Map<String, Object>>();;
		if(inPlan != null && inPlan) {
			String viewIds = "";
			if (filter != null) {
				viewIds = filter.getViewIds();
			}
			planViewList = this.viewInfoDao.queryFullViewInfoInPlan(viewIds, planId, crewId, null);
		}
		if (inPlan != null && !inPlan) {
			/*if (!filter.isFromAdvance()) {
				return planViewList;
			}*/
			filter.setPlanId(planId);
			planViewList = this.viewInfoDao.queryViewList(crewId, page, filter);
			//planViewList = this.viewInfoDao.queryFullViewInfoNotInPlan(planId, crewId, page, filter);
		}
		
		String viewIds = "";
		for (Map<String, Object> viewInfo : planViewList) {
			String viewId = viewInfo.get("viewId") + "";
			viewIds += "'" + viewId + "'" + ",";
		}
		if(StringUtils.isBlank(viewIds)){
			return planViewList;
		}
		viewIds = viewIds.substring(0, viewIds.length() -1);
		
		
		//主场景、次场景、三级场景
		List<Map<String, Object>> viewLocationList = this.viewLocationDao.queryViewLocationByViewIds(viewIds);
		
		//主要演员、特约演员、群众演员
		List<Map<String, Object>> majorRoleList = this.viewRoleDao.queryViewRoleListByViewId(viewIds, ViewRoleType.MajorActor.getValue());
		List<Map<String, Object>> guestRoleList = this.viewRoleDao.queryViewRoleListByViewId(viewIds, ViewRoleType.GuestActor.getValue());
		List<Map<String, Object>> massRoleList = this.viewRoleDao.queryViewRoleListByViewId(viewIds, ViewRoleType.MassesActor.getValue());
		
		
		//服装、化妆、道具
		List<Map<String, Object>> clothesList = this.goodsInfoDao.queryMakeupAndClothesByViews(viewIds, GoodsType.Clothes.getValue());
		List<Map<String, Object>> makeupList = this.goodsInfoDao.queryMakeupAndClothesByViews(viewIds, GoodsType.Makeup.getValue());
		List<Map<String, Object>> propList = this.goodsInfoDao.queryManyByViews(viewIds);
		
		List<Map<String, Object>> advertsList = this.insideAdvertDao.queryManyByViews(viewIds);
		
		
		for (Map<String, Object> viewInfo : planViewList) {
			String firstLocation = "";
			String secondLocation = "";
			String thirdLocation = "";
			
			String majorRole = "";
			String guestRole = "";
			String massRole = "";
			
			String clothesName = "";
			String makeupName = "";
			String propsName = "";
			String specialPropsName = "";
			
			String advertsInfo = "";

			String planViewId = viewInfo.get("viewId") + "";
			
			//场景地点信息
			for (Map<String, Object> viewLocationMap : viewLocationList) {
				String locationViewId = viewLocationMap.get("viewId") + "";
				int locationType = (Integer) viewLocationMap.get("locationType");
				String location = viewLocationMap.get("location") + "";
				if (planViewId.equals(locationViewId) && locationType == LocationType.lvlOneLocation.getValue()) {
					firstLocation += location + ",";
				}
				if (planViewId.equals(locationViewId) && locationType == LocationType.lvlTwoLocation.getValue()) {
					secondLocation += location + ",";
				}
				if (planViewId.equals(locationViewId) && locationType == LocationType.lvlThreeLocation.getValue()) {
					thirdLocation += location + ",";
				}
			}
			
			//主要演员信息
			for (Map<String, Object> majorRoleMap : majorRoleList) {
				String majorRoleViewId = majorRoleMap.get("viewId") + "";
				if (planViewId.equals(majorRoleViewId)) {
					majorRole += majorRoleMap.get("viewRoleName") + ",";
				}
			}
			
			//特约演员信息
			for (Map<String, Object> guestRoleMap : guestRoleList) {
				String guestRoleViewId = guestRoleMap.get("viewId") + "";
				if (planViewId.equals(guestRoleViewId)) {
					guestRole += guestRoleMap.get("viewRoleName") + ",";
				}
			}
			
			//群众演员信息
			for (Map<String, Object> massRoleMap : massRoleList) {
				String massRoleViewId = massRoleMap.get("viewId") + "";
				if (planViewId.equals(massRoleViewId)) {
					massRole += massRoleMap.get("viewRoleName") + ",";
				}
			}
			
			//服装
			for (Map<String, Object> clothesMap : clothesList) {
				String clothesViewId = clothesMap.get("viewId") + "";
				if (clothesViewId.equals(planViewId)) {
					clothesName += clothesMap.get("goodsName") + ",";
				}
			}
			
			//化妆
			for (Map<String, Object> makeupMap : makeupList) {
				String makeupViewId = makeupMap.get("viewId") + "";
				if (makeupViewId.equals(planViewId)) {
					makeupName += makeupMap.get("goodsName") + ",";
				}
			}
			
			//道具
			for (Map<String, Object> propMap : propList) {
				String propViewId = propMap.get("viewId") + "";
				int propsType = (Integer) propMap.get("goodsType");
				if (propViewId.equals(planViewId) && propsType == GoodsType.CommonProps.getValue()) {
					propsName += propMap.get("goodsName") + ",";
				}
				if (propViewId.equals(planViewId) && propsType == GoodsType.SpecialProps.getValue()) {
					specialPropsName += propMap.get("goodsName") + ",";
				}
			}
			
			//植入广告
			for (Map<String, Object> advertMap : advertsList) {
				String advertViewId = advertMap.get("viewId") + "";
				if (advertViewId.equals(planViewId)) {
					advertsInfo += advertMap.get("advertName") + ",";
				}
			}
			
			if (!StringUtils.isBlank(firstLocation)) {
				firstLocation = firstLocation.substring(0,  firstLocation.length() -1);
			}
			if (!StringUtils.isBlank(secondLocation)) {
				secondLocation = secondLocation.substring(0,  secondLocation.length() -1);
			}
			if (!StringUtils.isBlank(thirdLocation)) {
				thirdLocation = thirdLocation.substring(0,  thirdLocation.length() -1);
			}
			if (!StringUtils.isBlank(majorRole)) {
				majorRole = majorRole.substring(0,  majorRole.length() -1);
			}
			if (!StringUtils.isBlank(guestRole)) {
				guestRole = guestRole.substring(0,  guestRole.length() -1);
			}
			if (!StringUtils.isBlank(massRole)) {
				massRole = massRole.substring(0,  massRole.length() -1);
			}
			if (!StringUtils.isBlank(clothesName)) {
				clothesName = clothesName.substring(0,  clothesName.length() -1);
			}
			if (!StringUtils.isBlank(makeupName)) {
				makeupName = makeupName.substring(0,  makeupName.length() -1);
			}
			if (!StringUtils.isBlank(propsName)) {
				propsName = propsName.substring(0,  propsName.length() -1);
			}
			if (!StringUtils.isBlank(specialPropsName)) {
				specialPropsName = specialPropsName.substring(0, specialPropsName.length() - 1);
			}
			if (!StringUtils.isBlank(advertsInfo)) {
				advertsInfo = advertsInfo.substring(0, advertsInfo.length() - 1);
			}
			
			viewInfo.put("firstLocation", firstLocation);
			viewInfo.put("secondLocation", secondLocation);
			viewInfo.put("thirdLocation", thirdLocation);

			viewInfo.put("majorRole", majorRole);
			viewInfo.put("guestRole", guestRole);
			viewInfo.put("massRole", massRole);

			viewInfo.put("clothesName", clothesName);
			viewInfo.put("makeupName", makeupName);
			viewInfo.put("propsName", propsName);
			viewInfo.put("specialPropsName", specialPropsName);
			
			viewInfo.put("advertsInfo", advertsInfo);
		}
		
		return planViewList;
	}
	
	/**
	 * 查询拍摄计划下的所有场景信息
	 * 该查询主要用户计算拍摄计划完成率、拍摄地点、场数、页数信息
	 * 查询简单，高效
	 * @param planId
	 * @return
	 */
	public List<Map<String, Object>> querySimpleViewInfoByPlanId(String planId) {
		return this.viewInfoDao.querySimpleViewInfoByPlanId(planId);
	}
	
	/**
	 * 根据多个条件查询场景和拍摄计划的关联关系信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ViewPlanMapModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.viewPlanMapDao.queryManyByMutiCondition(conditionMap, page);
	}
	
	/**
	 * 批量新增数据
	 * @param viewPlanMapList
	 * @throws Exception
	 */
	public void addMany(List<ViewPlanMapModel> viewPlanMapList) throws Exception {
		this.viewPlanMapDao.addBatch(viewPlanMapList, ViewPlanMapModel.class);
	}
	
	/**
	 * 根据计划ID和多个场景ID删除计划和场景的关联关系
	 * @param planId
	 * @param viewIds
	 */
	public void deleteByPlanIdAndViewIds(String planId, String[] viewIdsArr) {
		this.viewPlanMapDao.deleteByPlanIdAndViewIds(planId, viewIdsArr);
	}
	
	/**
	 * 根据计划ID和多个场景ID删除计划和场景的关联关系
	 * @param planId
	 * @param viewIds
	 */
	public void deleteByPlanIdsAndViewIds(String[] planIdArr, String[] viewIdsArr) {
		this.viewPlanMapDao.deleteByPlanIdsAndViewIds(planIdArr, viewIdsArr);
	}
	
	/**
	 * 添加场景到计划中
	 * 该方法带有以下业务逻辑
	 * 如果所要添加的场景在指定拍摄计划的父计划中，
	 * 需要在新建和子计划的关联关系的同时删除和父计划的关联关系
	 * @param planIds
	 * @param viewIds
	 * @return
	 * @throws Exception 
	 */
	public String addViewToPlan (String planIds, String viewIds, String crewId) throws Exception {
		String idArrayStr = "";
		
		String[] viewIdArr = viewIds.split(",");
		List<ViewPlanMapModel> viewPlanMapList = new ArrayList<ViewPlanMapModel>();
		
		String[] planIdArr = planIds.split(",");
		for (String planId : planIdArr) {
			for (String viewId : viewIdArr) {
				Map<String, Object> conditionMap = new HashMap<String, Object>();
				conditionMap.put("viewId", viewId);
				conditionMap.put("planId", planId);
				
				List<ViewPlanMapModel> existMapList = this.queryManyByMutiCondition(conditionMap, null);
				if (existMapList != null && existMapList.size() > 0) {
					ViewInfoModel viewInfo = this.viewInfoDao.queryById(viewId);
					ShootPlanModel shootPlan = this.shootPlanDao.queryOneByPlanId(planId);
					throw new IllegalArgumentException(viewInfo.getSeriesNo() + "-" + viewInfo.getViewNo() + "场已经在《" + shootPlan.getPlanName() + "》计划中，不能重复添加。");
				}
				
				
				ViewPlanMapModel viewPlanMap = new ViewPlanMapModel();
				String mapId = UUIDUtils.getId();
				
				viewPlanMap.setCrewId(crewId);
				viewPlanMap.setMapId(mapId);
				viewPlanMap.setViewId(viewId);
				viewPlanMap.setPlanId(planId);
				
				viewPlanMapList.add(viewPlanMap);
				idArrayStr += mapId + ",";
			}
		}
		this.addMany(viewPlanMapList);
		
		//解除与父计划的关联
		List<Map<String, Object>> parentPlanIdList = this.shootPlanDao.queryParentPlanIds(planIds);
		String parentPlanIds = "";
		for (Map<String, Object> parentPlanIdMap : parentPlanIdList) {
			String parentPlanId = (String) parentPlanIdMap.get("parentPlan");
			parentPlanIds += parentPlanId + ",";
		}
		if (!StringUtils.isBlank(parentPlanIds)) {
			parentPlanIds = parentPlanIds.substring(0, parentPlanIds.length() - 1);
			this.deleteByPlanIdsAndViewIds(parentPlanIds.split(","), viewIdArr);
		}
		
		return idArrayStr;
	}
	
}
