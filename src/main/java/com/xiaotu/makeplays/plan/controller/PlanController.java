package com.xiaotu.makeplays.plan.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.plan.model.Plan;
import com.xiaotu.makeplays.plan.model.PlanFilter;
import com.xiaotu.makeplays.plan.service.PlanService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Page;

/**
 * 计划
 * 
 * @author subin
 */
@Controller
public class PlanController extends BaseController {
	
	@Autowired
	private PlanService planService;
	
	/**
	 * 查询所有计划列表
	 * 
	 */
	@RequestMapping("/plans")
	public @ResponseBody List<Plan> retrievePlans(HttpServletRequest request) throws Exception{
		
		@SuppressWarnings("unchecked")
		String crewId = this.getCrewId(request);
		
		return planService.getPlan(crewId);
	}
	
	/**
	 * 查询一个计划的详细信息
	 * 
	 */
	@RequestMapping("/plan/detail")
	public @ResponseBody Map<String, Object> retrievePlanDetail(PlanFilter filter, Page page) throws Exception{
		
		return planService.getPlanDetail(filter, page);
	}
	
	/**
	 * 查询一个计划的图形统计信息
	 * 
	 */
	@RequestMapping("/plan/overview")
	public @ResponseBody Plan retrievePlanStat(HttpServletRequest request, String planId) throws Exception{
		
		@SuppressWarnings("unchecked")
		String crewId = this.getCrewId(request);
		
		return planService.getPlanStat(planId, crewId);
	}
	
	/**
	 * 查询一个计划的查询条件
	 * 
	 */
	
	/**
	 * 查询添加&修改计划上下文
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/plan/factors")
	public @ResponseBody Map<String, List> retrieveFactorContext(HttpServletRequest request) throws Exception{
		
		@SuppressWarnings("unchecked")
		String crewId = this.getCrewId(request);
		
		return planService.getFactorContext(crewId);
	}
	
	/**
	 * 新建计划
	 * 
	 * @param session
	 * @param factor
	 * 
	 * @return success/fail
	 */
	@RequestMapping("/plan/create")
	public @ResponseBody Map<String, Object> createPlan(HttpServletRequest request, @RequestBody Plan plan) throws Exception{
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		@SuppressWarnings("unchecked")
		String crewId = this.getCrewId(request);
		
		plan.setCrewid(crewId);
		
		String planId = planService.createPlan(plan);
		
		result.put("planId", planId);
		
		return result;
	}
	
	/**
	 * 修改计划
	 * 
	 * @param session
	 * @param factor
	 * @param planid
	 * 
	 * @return success/fail
	 */
	@RequestMapping("/plan/update")
	public @ResponseBody void updatePlan() throws Exception{
		
		
	}
	
	/**
	 * 删除计划
	 * 
	 * @param planid
	 * 
	 * @return success/fail
	 */
	@RequestMapping("/plan/delete")
	public @ResponseBody void deletePlan() throws Exception{
		
		
	}
	
	/**
	 * 更新阶段和分组
	 * 
	 * @param planViewIds
	 * @param stagenum
	 * @param groupnum
	 */
	@RequestMapping("/plan/stage/update")
	public @ResponseBody void updateStage(@RequestBody Map<String, Object> planViews) throws Exception{
		
		planService.updateStageGroup(planViews);
	}
}
