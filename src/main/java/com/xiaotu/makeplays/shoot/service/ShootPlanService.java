package com.xiaotu.makeplays.shoot.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.shoot.dao.ShootPlanDao;
import com.xiaotu.makeplays.shoot.dao.ViewPlanMapDao;
import com.xiaotu.makeplays.shoot.model.ShootPlanModel;
import com.xiaotu.makeplays.shoot.model.ViewPlanMapModel;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.DateUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 拍摄计划
 * @author xuchangjian
 */
@Service
public class ShootPlanService {

	@Autowired
	private ShootPlanDao shootPlanDao;
	
	@Autowired
	private ViewPlanMapDao viewPlanMapDao;
	
	/**
	 * 新建拍摄计划
	 * @param shootPlanModel
	 * @throws Exception 
	 */
	public void addShootPlan(ShootPlanModel shootPlanModel) throws Exception {
		this.shootPlanDao.add(shootPlanModel);
	}
	
	/**
	 * 修改拍摄计划
	 * @param shootPlanModel
	 * @throws Exception 
	 */
	public void updateShootPlan(ShootPlanModel shootPlanModel) throws Exception {
		this.shootPlanDao.update(shootPlanModel, "planId");
	}
	
	/**
	 * 根据多个条件查询拍摄计划信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ShootPlanModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.shootPlanDao.queryManyByMutiCondition(conditionMap, page);
	}
	
	/**
	 * 查询拍摄计划信息，该方法会查询出上级计划的名称
	 * @param conditionMap
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> queryManyByMutiConditionWithParentName(Map<String, Object> conditionMap, Page page) {
		return this.shootPlanDao.queryManyByMutiConditionWithParentName(conditionMap, page);
	}
	
	/**
	 * 通过拍摄计划ID查找拍摄计划信息
	 * @param planId	计划ID
	 * @return
	 */
	public ShootPlanModel queryOneByPlanId (String planId) {
		return this.shootPlanDao.queryOneByPlanId(planId);
	}
	
	/**
	 * 删除拍摄计划以及该计划和场景的关联关系
	 * @param planId 拍摄计划ID
	 * @throws Exception 
	 */
	public void deleteShootPlan(String planId) throws Exception {
		this.viewPlanMapDao.deleteByPlanIdAndViewIds(planId, null);
		this.shootPlanDao.deleteOne(planId, "planId", ShootPlanModel.TABLE_NAME);
	}
	
	/**
	 * 查询计划的父计划ID
	 * @param planIds	多个计划ID，以逗号隔开
	 * @return
	 */
	public List<Map<String, Object>> queryParentPlanIds(String planIds) {
		return this.shootPlanDao.queryParentPlanIds(planIds);
	}
	
	public String saveShootPlan(String planId, String planName, String viewIds, String planStartTime, String planEndTime, String groupId, String parentPlanId, String crewId) throws Exception {
		String message = "";
		//添加拍摄计划信息
		ShootPlanModel shootPlanModel = null;
		if (!StringUtils.isBlank(planId)) {
			shootPlanModel = this.queryOneByPlanId(planId);
		} else {
			shootPlanModel = new ShootPlanModel();
		}
		
		shootPlanModel.setStartDate(DateUtils.parse2Date(planStartTime));
		shootPlanModel.setEndDate(DateUtils.parse2Date(planEndTime));
		shootPlanModel.setGroupId(groupId);
		shootPlanModel.setUpdateTime(new Date());
		shootPlanModel.setCrewId(crewId);
		shootPlanModel.setPlanType(Constants.PLAN_TYPE_SMALL);
		shootPlanModel.setPlanName(planName);
		if (!StringUtils.isBlank(parentPlanId)) {
			//查询父计划，如果子计划时间不在超出父计划范围，直接报错
			Date subPlanStartDate = DateUtils.parse2Date(planStartTime);
			Date subPlanEndDate = DateUtils.parse2Date(planEndTime);
			
			ShootPlanModel parentPlanInfo = this.shootPlanDao.queryOneByPlanId(parentPlanId);
			Date parentPlanStartDate = parentPlanInfo.getStartDate();
			Date parentPlanEndDate = parentPlanInfo.getEndDate();
			if (subPlanStartDate.before(parentPlanStartDate) || subPlanStartDate.after(parentPlanEndDate) 
					|| subPlanEndDate.before(parentPlanStartDate) || subPlanEndDate.after(parentPlanEndDate)) {
				throw new IllegalArgumentException("子计划时间超出父计划时间范围，请重新填写");
			}
			
			shootPlanModel.setParentPlan(parentPlanId);
		}
		
		if (!StringUtils.isBlank(planId)) {
			this.updateShootPlan(shootPlanModel);
			message = "修改拍摄计划成功";
		} else {
			planId = UUIDUtils.getId();
			shootPlanModel.setPlanId(planId);
			this.addShootPlan(shootPlanModel);
			message = "添加拍摄计划成功";
		}
		
		
		//建立拍摄计划和场景的关联关系
		if (!StringUtils.isBlank(viewIds)) {
			String[] viewIdArr = viewIds.split(",");
			for (String viewId : viewIdArr) {
				ViewPlanMapModel viewPlanMap = new ViewPlanMapModel();
				viewPlanMap.setCrewId(crewId);
				viewPlanMap.setMapId(UUIDUtils.getId());
				viewPlanMap.setViewId(viewId);
				viewPlanMap.setPlanId(planId);
				this.viewPlanMapDao.add(viewPlanMap);
			}
			
			//解除与父计划的关联
			if (!StringUtils.isBlank(parentPlanId)) {
				this.viewPlanMapDao.deleteByPlanIdAndViewIds(parentPlanId, viewIdArr);
			}
		}
		
		return message;
	}
}
