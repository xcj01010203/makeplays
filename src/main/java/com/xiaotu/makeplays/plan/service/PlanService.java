package com.xiaotu.makeplays.plan.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.plan.dao.FactorDao;
import com.xiaotu.makeplays.plan.dao.PlanDao;
import com.xiaotu.makeplays.plan.dao.PlanViewDao;
import com.xiaotu.makeplays.plan.model.Factor;
import com.xiaotu.makeplays.plan.model.Plan;
import com.xiaotu.makeplays.plan.model.PlanFilter;
import com.xiaotu.makeplays.plan.model.PlanView;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.StringUtils;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 计划
 * 
 * @author subin
 */
@Service
public class PlanService {
	
	@Autowired
	private PlanDao planDao;
	
	@Autowired
	private FactorDao factorDao;
	
	@Autowired
	private PlanViewDao planViewDao;
	
	@Autowired
	private ClusterService cluster;
	
	/**
	 * 查询上下文
	 * 
	 * @param 剧本ID
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, List> getFactorContext(String crewId) throws Exception{
		
		Map<String, List> result = new HashMap<String, List>();
		result.put("shootlocation", planDao.queryShootLocation(crewId));
		result.put("actor", planDao.queryViewRole(crewId, "1"));
		result.put("props", planDao.queryProps(crewId));
		
		return result;
	}
	
	/**
	 * 查询计划列表
	 * 
	 * @param 剧本ID
	 */
	public List<Plan> getPlan(String crewId) throws Exception{
		
		return planDao.queryPlan(crewId, null);
	}
	
	/**
	 * 查询一个计划
	 * <p> 如果没有计划ID, 默认返回此剧的默认计划
	 * 
	 * @param 计划ID
	 * @param 剧本ID
	 */
	public Plan getPlanStat(String planId, String crewId) throws Exception{
		
		Plan result = null;
		
		//是否取默认计划
		if(StringUtils.isEmpty(planId)){
			
			result = planDao.queryDefaultPlan(crewId);
			
		}else{
			
			result = planDao.queryPlan(planId);
		}
		
		if(result != null){
			
			planId = result.getId();
			
			//影响因素
			List<Factor> factors = factorDao.queryFactor(result.getId());
			
			//查询阶段
			List<Map<String, Object>> stages = planViewDao.queryViewStage(result.getId());
			
			//查询分组对应场景
			List<Map<String, Object>> planViews = new ArrayList<Map<String, Object>>();
			
			for(Map<String, Object> one : stages){
				
				long stagenum = Long.valueOf(one.get("stagenum").toString());
				//阶段统计
				Map<String, Object> stage = planViewDao.queryStageStat(planId, stagenum);
				
				List<Map<String, Object>> factorStat = new ArrayList<Map<String, Object>>();
				
				//影响因素的统计
				for(Factor factor : factors){
					
					Map<String, Object> mf = new HashMap<String, Object>();
					
					mf.put("name", factor.getFactorname());
					mf.put("cnt", planViewDao.queryFactorStageCnt(factor, stagenum));
					mf.put("priority", factor.getPriority());
					
					factorStat.add(mf);
				}
				
				stage.put("factorStat", factorStat);
				
				//阶段明细
				List<Map<String, Object>> groupsViews = new ArrayList<Map<String, Object>>();
				
				List<Map<String, Object>> groups = planViewDao.queryViewGroup(result.getId(), stagenum);
				
				for(Map<String, Object> two : groups){
					
					long groupnum = Long.valueOf(two.get("groupnum").toString());
					//分组统计
					Map<String, Object> group = planViewDao.queryGroupStat(planId, stagenum, groupnum);
					
					List<Map<String, Object>> groupStat = new ArrayList<Map<String, Object>>();
					
					//影响因素的统计
					for(Factor factor : factors){
						
						Map<String, Object> mf = new HashMap<String, Object>();
						
						mf.put("name", factor.getFactorname());
						mf.put("cnt", planViewDao.queryFactorStageGroupCnt(factor, stagenum, groupnum));
						mf.put("priority", factor.getPriority());
						
						groupStat.add(mf);
					}
					
					group.put("factorStat", groupStat);
					
					groupsViews.add(group);
				}
				
				stage.put("views", groupsViews);
				
				planViews.add(stage);
			}
			
			result.setPlanViews(planViews);
		}
		
		return result;
	}
	
	/**
	 * 查询一个计划场景详情
	 * 
	 * @param 场景过滤条件
	 * @param 分页
	 */
	public Map<String, Object> getPlanDetail(PlanFilter filter, Page page) throws Exception{
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> views = planViewDao.queryPlanView(filter, page);
		
		for(Map<String, Object> view :views){
			
			//查询主场景
			List<Map<String, Object>> viewLocation = planViewDao.queryViewLocation(view.get("viewId").toString());
			
			if(viewLocation.size() > 0){
				
				view.put("viewLocation", viewLocation.get(0));
			}
			
			//查询主要演员
			List<Map<String, Object>> viewRole = planViewDao.queryViewRole(view.get("viewId").toString());
			
			view.put("viewRole", viewRole);
		}
		
		result.put("views", views);
		result.put("page", page);
		
		return result;
	}
	
	/**
	 * 创建一个计划
	 * <p> 聚类场景
	 * 
	 * @param 剧本ID
	 * @param 影响因素
	 */
	public String createPlan(Plan plan) throws Exception{
		
		//根据影响因素量化场景
		List<Map<String, Object>> quanFactor = factorDao.queryQuantizationFactor(plan);
		
		//聚类结果
		List<Integer> clusterResult = cluster.doCluster(quanFactor, plan.getFactors());
		
		//持久化
		String planId = UUIDUtils.getId();
		plan.setId(planId);
		
		//查询此剧本是否有默认计划//如果没有默认计划自动默认新添加的
		Plan defaultPlan = planDao.queryDefaultPlan(plan.getCrewid());
		
		if(defaultPlan == null){
			
			plan.setOfficial("1");
		}
		
		//添加到计划表
		planDao.insert(plan);
		
		//添加到影响因素表
		for(Factor one :plan.getFactors()){
			
			one.setId(UUIDUtils.getId());
			one.setPlanid(planId);
			factorDao.insert(one);
		}
		
		//添加到对应关系表
		for(int i = 0; i< quanFactor.size(); i++ ){
			
			PlanView one = new PlanView();
			
			one.setId(UUIDUtils.getId());
			one.setPlanid(planId);
			one.setViewid(quanFactor.get(i).get("viewId").toString());
			one.setStagenum(clusterResult.get(i));
			one.setSortnum(i+1);
			
			planViewDao.insert(one);
		}
		
		//自动创建分组
		this.autoGroupPlan(planId);
		
		return planId;
	}
	
	/**
	 * 计划自动分组
	 * <p> 以每阶段250场戏一组
	 * 
	 * @param 计划ID
	 */
	public void autoGroupPlan(String planId) throws Exception{
		
		//查询阶段
		List<Map<String, Object>> stage = planViewDao.queryViewStage(planId);
		
		//分组
		for(Map<String, Object> one : stage){
			
			List<Map<String, Object>> group = planViewDao.queryPlanView(planId, Long.valueOf(one.get("stagenum").toString()));
			
			long num = 0;
			
			for(int i=0; i<group.size(); i++){
				
				planViewDao.updateGroup(group.get(i).get("id").toString(), num);
				
				if((i+1)%250 == 0){
					
					num++;
				}
			}
		}
	}
	
	/**
	 * 更新阶段和分组
	 * 
	 * @param planViewIds
	 * @param stagenum
	 * @param groupnum
	 */
	@SuppressWarnings("unchecked")
	public void updateStageGroup(Map<String, Object> planViews) throws Exception{
		
		List<String> planViewIds = (List<String>) planViews.get("planViewIds");
		
		String planId = planViews.get("planId").toString();
		long stagenum = Long.valueOf(planViews.get("stagenum").toString());
		long groupnum = Long.valueOf(planViews.get("groupnum").toString());
		
		if(stagenum >= 0){
			
			if(groupnum < 0){
				
				//查询最大分组
				long num1 = planViewDao.queryMaxGroup(planId, stagenum);
				
				groupnum = num1 +1;
			}
		}else{
			
			//查询最大阶段
			long num2 = planViewDao.queryMaxStage(planId);
			
			stagenum = num2 + 1;
			
			groupnum = 0;
		}
		
		for(String one: planViewIds){
			
			planViewDao.updateStageGroup(one, stagenum, groupnum);
		}
	}
}