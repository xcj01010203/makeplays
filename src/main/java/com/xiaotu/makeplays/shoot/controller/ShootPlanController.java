package com.xiaotu.makeplays.shoot.controller;

import java.io.File;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.shoot.dto.ShootPlanDto;
import com.xiaotu.makeplays.shoot.model.ShootGroupModel;
import com.xiaotu.makeplays.shoot.model.ShootPlanModel;
import com.xiaotu.makeplays.shoot.model.ViewPlanMapModel;
import com.xiaotu.makeplays.shoot.service.ShootGroupService;
import com.xiaotu.makeplays.shoot.service.ShootPlanService;
import com.xiaotu.makeplays.shoot.service.ViewPlanMapService;
import com.xiaotu.makeplays.sys.service.SysLogService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.DateUtils;
import com.xiaotu.makeplays.utils.ExportExcelUtil;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.view.controller.filter.ViewFilter;
import com.xiaotu.makeplays.view.model.constants.ShootStatus;
import com.xiaotu.makeplays.view.model.constants.ViewType;
import com.xiaotu.makeplays.view.service.AtmosphereService;
import com.xiaotu.makeplays.view.service.ViewInfoService;

@Deprecated
@Controller	
//@RequestMapping("/shootPlanManager")
public class ShootPlanController extends BaseController{
	
	Logger logger = org.slf4j.LoggerFactory.getLogger(ShootPlanController.class);

	@Autowired
	private ShootPlanService shootPlanService;
	
	@Autowired
	private ViewPlanMapService viewPlanMapService;
	
	@Autowired
	private SysLogService sysLogService;
	
	@Autowired
	private ShootGroupService shootGroupService;
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	@Autowired
	private AtmosphereService atmosphereService;
	
	/**
	 * 跳转到拍摄计划列表页面
	 * @return
	 */
	@RequestMapping("/shootPlanList")
	public ModelAndView toShootPlanListPage(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("/shoot/shootPlanList");
		String crewId = this.getCrewId(request);
		try {
			/*
			 * 统计信息
			 */
			//剧组下场景的总场数和总页数
			int viewSum = 0;
			List<Map<String, Object>> viewSumMapList = this.viewInfoService.queryStatisticsTotalCount(crewId);
			if (viewSumMapList != null && viewSumMapList.size() > 0) {
				viewSum += (Long)viewSumMapList.get(0).get("totalCount");
			}
			double pageSum = 0;
			List<Map<String, Object>> pageCountList = this.viewInfoService.queryStatisticsPageCount(crewId);
			if (pageCountList != null && pageCountList.size() > 0) {
				pageSum = (Double) pageCountList.get(0).get("pageSum");
			}
			
			
			//按照拍摄组统计剧组下已加入拍摄计划的总场数和总页数
			List<Map<String, Object>> inPlanGroupViewMapList = this.viewInfoService.queryStatTCountAndPCountByGroup(crewId);
			int inPlanViewSum = 0;	//已加入计划的场数
			double inPlanPageSum = 0;	//已加入计划的页数
			for (Map<String, Object> map : inPlanGroupViewMapList) {
				Long groupViewSum = (Long) map.get("viewCount");
				Double groupPageSum = (Double) map.get("pageCount");
				if (groupViewSum != null) {
					inPlanViewSum += groupViewSum;
				}
				if (groupPageSum != null) {
					inPlanPageSum += groupPageSum;
				}
			}
			
			mv.addObject("viewSum", viewSum);	//剧组下场景的总场数
			mv.addObject("pageSum", pageSum);	//剧组下场景的总页数
			mv.addObject("inPlanViewSum", inPlanViewSum);	//剧组下已加入计划的总场数
			mv.addObject("inPlanPageSum", inPlanPageSum);	//剧组下已加入计划的总页数
			mv.addObject("groupPlanViewList", inPlanGroupViewMapList);	//每个小组已加入计划的场数和页数
			
			//分组信息
			List<ShootGroupModel> groupList =shootGroupService.queryManyByCrewId(crewId);
			mv.addObject("groupList", groupList);

		} catch (Exception e) {
			logger.error("未知异常，页面跳转失败", e);
		}
		
		try {
//			this.sysLogService.saveSysLog(request, "跳转到拍摄计划列表页面", Constants.TERMINAL_PC, ShootPlanModel.TABLE_NAME	, "",0);
		} catch (Exception e) {
			logger.error("未知异常，保存系统日志失败", e);
		}
		
		return mv;
	}
	
	/**
	 * 跳转到新增拍摄计划页面
	 * @param request
	 * @return
	 */
	@RequestMapping("/shootPlanAdd")
	public ModelAndView toShootPlanAddPage(HttpServletRequest request, String parentPlanId) {
		ModelAndView mv = new ModelAndView("/shoot/shootPlanAdd");
		String crewId = this.getCrewId(request);
		
		if (!StringUtils.isBlank(parentPlanId)) {
			ShootPlanModel shootPlanInfo = this.shootPlanService.queryOneByPlanId(parentPlanId);
			mv.addObject("shootPlanInfo", shootPlanInfo);
		}
		
		
		List<ShootGroupModel> groupList = this.shootGroupService.queryManyByCrewId(crewId);
		
		mv.addObject("groupList", groupList);
		return mv;
	}
	
	/**
	 * 跳转到修改拍摄计划页面
	 * @param request
	 * @return
	 */
	@RequestMapping("/shootPlanDetail")
	public ModelAndView toShootPlanModifyPage(HttpServletRequest request, String planId) {
		ModelAndView mv = new ModelAndView("/shoot/shootPlanDetail");
		String crewId = this.getCrewId(request);
		
		try {
			ShootPlanModel shootPlanInfo = this.shootPlanService.queryOneByPlanId(planId);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			shootPlanInfo.setStartDate(sdf.parse(sdf.format(shootPlanInfo.getStartDate())));
			shootPlanInfo.setEndDate(sdf.parse(sdf.format(shootPlanInfo.getEndDate())));
			
			List<ShootGroupModel> groupList = this.shootGroupService.queryManyByCrewId(crewId);
			
			mv.addObject("groupList", groupList);
			mv.addObject("shootPlanInfo", shootPlanInfo);
			
		} catch (Exception e) {
			logger.error("未知异常，查询拍摄计划详细信息失败", e);
		}
		
		try {
//			this.sysLogService.saveSysLog(request, "跳转到修改拍摄计划页面", Constants.TERMINAL_PC, ShootPlanModel.TABLE_NAME, planId,0);
		} catch (Exception e) {
			logger.error("未知异常，保存系统日志失败", e);
		}
		
		return mv;
	}
	
	/**
	 * 根据计划ID，查询单个计划信息
	 * @param planId
	 * @return
	 */
	@RequestMapping("/queryOnePlanJson")
	public @ResponseBody Map queryOneByPlanId(String planId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			ShootPlanModel shootPlanInfo = this.shootPlanService.queryOneByPlanId(planId);
			ShootPlanDto planDto = this.genShootPlanDto(shootPlanInfo);
			
			success = true;
			message = "查询成功";
			
			resultMap.put("shootPlanInfo", planDto);
		} catch (Exception e) {
			String msg = "未知异常，查询拍摄计划失败";
			logger.error(msg);
			
			success = false;
			message = msg;
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 查询拍摄计划
	 * @param groupId 拍摄小组信息
	 * @param planId 在拍摄计划中，将场景添加到其他计划功能中，需要过滤掉的拍摄计划
	 * @param parentPlanId 父计划ID
	 * @return
	 */
	@RequestMapping("/shootPlanlistJson")
	public @ResponseBody Map<String, Object> listShootPlan(HttpServletRequest request, String groupId, String planId, String parentPlanId, Page page) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String crewId = this.getCrewId(request);
		
		boolean success = true;
		String message = "";
		
		try {
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("crewId", crewId);
			if (!StringUtils.isBlank(groupId)) {
				conditionMap.put("groupId", groupId);
			}
			if (!StringUtils.isBlank(planId)) {
				conditionMap.put("planId", planId);
			}
			if (!StringUtils.isBlank(parentPlanId)) {
				conditionMap.put("parentPlan", parentPlanId);
			} else {
				conditionMap.put("parentPlan", null);
			}
			List<ShootPlanModel> shootPlanList = null;
			if (!StringUtils.isBlank(parentPlanId)) {
				shootPlanList = this.shootPlanService.queryManyByMutiCondition(conditionMap, null);
			} else {
				shootPlanList = this.shootPlanService.queryManyByMutiCondition(conditionMap, page);
			}
			
			List<ShootPlanDto> shootPlanDtoList = this.genPlanDtoList(shootPlanList);
			page.setResultList(shootPlanDtoList);
			resultMap.put("result", page);
			
			success = true;
			message = "查询拍摄计划列表成功";
		} catch (Exception e) {
			String msg = "未知异常，查询拍摄计划信息失败";
			logger.error("", e);
			
			success = false;
			message = msg;
		}
		
		try {
			if (success) {
//				this.sysLogService.saveSysLog(request, "查询拍摄计划信息", Constants.TERMINAL_PC, ShootPlanModel.TABLE_NAME, "",0);
			}
		} catch (Exception e) {
			logger.error("未知异常，保存系统日志失败", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询所有计划
	 * @return
	 */
	@RequestMapping("/allShootPlanlistJson")
	public @ResponseBody Map<String, Object> listAllShootPlan(HttpServletRequest request, Page page) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String crewId = this.getCrewId(request);
		
		boolean success = true;
		String message = "";
		
		try {
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("crewId", crewId);
			List<ShootPlanModel> shootPlanList = this.shootPlanService.queryManyByMutiCondition(conditionMap, null);
			//List<Map<String, Object>> shootPlanList = this.shootPlanService.queryManyByMutiConditionWithParentName(conditionMap, page);
			
			List<ShootPlanDto> shootPlanDtoList = this.genPlanDtoList(shootPlanList);
			page.setResultList(shootPlanDtoList);
			resultMap.put("result", page);
			
			success = true;
			message = "查询拍摄计划列表成功";
		} catch (Exception e) {
			String msg = "未知异常，查询拍摄计划信息失败";
			logger.error("", e);
			
			success = false;
			message = msg;
		}
		
		try {
			if (success) {
//				this.sysLogService.saveSysLog(request, "查询拍摄计划信息", Constants.TERMINAL_PC, ShootPlanModel.TABLE_NAME, "",0);
			}
		} catch (Exception e) {
			logger.error("未知异常，保存系统日志失败", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询父计划下的子计划列表
	 * @param parentPlanId	父计划ID
	 * @return
	 */
	@RequestMapping("/subShootPlanJson")
	public @ResponseBody Map<String, Object> listSubShootPlan(HttpServletRequest request, String parentPlanId, Page page) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String crewId = this.getCrewId(request);
		
		boolean success = true;
		String message = "";
		
		try {
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("crewId", crewId);
			if (StringUtils.isBlank(parentPlanId)) {
				throw new IllegalArgumentException("请提供父拍摄计划ID");
			}
			conditionMap.put("parentPlan", parentPlanId);
			List<ShootPlanModel> shootPlanList = this.shootPlanService.queryManyByMutiCondition(conditionMap, null);
			
			List<ShootPlanDto> shootPlanDtoList = this.genPlanDtoList(shootPlanList);
			page.setResultList(shootPlanDtoList);
			resultMap.put("result", page);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			
			success = false;
			message = ie.getMessage();
			
		} catch (Exception e) {
			logger.error("未知异常，查询子计划失败", e);
			
			success = false;
			message = "未知异常，查询子计划失败";
		}
		
		try {
			if (success) {
//				this.sysLogService.saveSysLog(request, "查询子计划信息", Constants.TERMINAL_PC, ShootPlanModel.TABLE_NAME, parentPlanId,0);
			}
		} catch (Exception e) {
			logger.error("未知异常，保存系统日志失败", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 生成拍摄计划DTO
	 * @param shootPlanList
	 * @return
	 */
	private List<ShootPlanDto> genPlanDtoList (List<ShootPlanModel> shootPlanList) {
		List<ShootPlanDto> shootPlanDtoList = new ArrayList<ShootPlanDto>();
		for (ShootPlanModel shootPlan : shootPlanList) {
			
			ShootPlanDto shootPlanDto = genShootPlanDto(shootPlan);
			shootPlanDtoList.add(shootPlanDto);
		}
		return shootPlanDtoList;
	}

	/**
	 * 根据拍摄计划信息生成拍摄计划DTO
	 * @param shootPlan
	 * @return
	 */
	private ShootPlanDto genShootPlanDto(ShootPlanModel shootPlan) {
		DecimalFormat df = new DecimalFormat("#0.00");
		List<Map<String, Object>> viewInfoList = this.viewPlanMapService.querySimpleViewInfoByPlanId(shootPlan.getPlanId());
		//拍摄地点
		String shootLocationStr = "";
		
		//场数
		int viewNumTotal = viewInfoList.size();
		
		//页数
		double pageCountNumTotal = 0.0;
		
		//完成率
		double finishRate = 0.0;
		double finishViewCount = 0;	//完成的场数
		
		//计算拍摄计划中的额外字段信息，并将其放入dto中
		List<String> shootLicationList = new ArrayList<String>();
		for (Map<String, Object> viewInfo : viewInfoList) {
			String shootLocation = (String) viewInfo.get("shootLocation");
			if (!StringUtils.isBlank(shootLocation) && !shootLicationList.contains(shootLocation)) {
				shootLicationList.add(shootLocation);
				shootLocationStr += shootLocation + " ";
			}
			
			double pageCount = (Double) viewInfo.get("pageCount");
			pageCountNumTotal += pageCount;
			
			int shootStatus = (Integer) viewInfo.get("shootStatus");
			if (shootStatus == ShootStatus.Finished.getValue() 
					|| shootStatus == ShootStatus.DeleteXi.getValue() 
					|| shootStatus == ShootStatus.AddXiFinished.getValue()) {
				finishViewCount ++;
			}
		}
		
		if (viewNumTotal != 0) {
			finishRate = finishViewCount / viewNumTotal * 100;
		}

		ShootPlanDto shootPlanDto = new ShootPlanDto(shootPlan);
		shootPlanDto.setShootLocations(shootLocationStr);
		shootPlanDto.setViewNumTotal(viewNumTotal);
		shootPlanDto.setPageCountNumTotal(pageCountNumTotal);
		shootPlanDto.setFinishRate(Double.parseDouble(df.format(finishRate)));
		ShootGroupModel shootGroup = this.shootGroupService.queryOneByGroupId(shootPlan.getGroupId());
		if (shootGroup != null) {
			shootPlanDto.setGroupName(shootGroup.getGroupName());
		}
		return shootPlanDto;
	}
	
	/**
	 * 查询和拍摄计划相关的场景信息
	 * @param request
	 * @param planId 拍摄计划ID
	 * @param page	分页信息
	 * @param inPlan	查询计划内场景还是计划外场景的标识，true表示查询计划内的场景信息，反之就是计划外的场景信息
	 * @return
	 */
	@RequestMapping("/planViewListJson")
	public @ResponseBody Map<String, Object> listPlanView(HttpServletRequest request, String planId, Page page, Boolean inPlan, ViewFilter filter) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String crewId = this.getCrewId(request);
		boolean success = true;
		String message = "";
		try {
			List<Map<String, Object>> viewInfoList = this.viewPlanMapService.queryFullViewInfoByPlanId(planId, crewId, page, inPlan, filter);
			
			page.setResultList(viewInfoList);
			resultMap.put("result", page);
			
			success = true;
			message = "查询场景信息成功";
		} catch (Exception e) {
			String msg = "未知异常，查询场景信息失败";
			logger.error(msg, e);
			
			success = false;
			message = msg;
		}
		
		try {
			if (success) {
//				this.sysLogService.saveSysLog(request, "查询和拍摄计划相关的场景信息", Constants.TERMINAL_PC, ViewInfoModel.TABLE_NAME, "",0);
			}
		} catch (Exception e) {
			logger.error("未知异常，保存系统日志失败", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 新建、修改拍摄计划
	 * @param request
	 * @param planName	计划名称
	 * @param viewIds	添加到计划中的场景ID（多个ID以逗号隔开）
	 * @param planStartTime	计划开始时间
	 * @param planEndTime	计划结束时间
	 * @param groupId	分组ID
	 * @param parentPlanId 父计划ID
	 * @return
	 */
	@RequestMapping("/saveShootPlan")
	public @ResponseBody Map<String, Object> saveShootPlan(HttpServletRequest request, String planId, String planName, String viewIds, String planStartTime, String planEndTime, String groupId, String parentPlanId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String crewId = this.getCrewId(request);
		
		boolean success = true;
		String message = "";
		try {
			//校验数据
			if (StringUtils.isBlank(planName)) {
				throw new IllegalArgumentException("请填写计划名称");
			}
			if (StringUtils.isBlank(planStartTime)) {
				throw new IllegalArgumentException("请选择计划开始时间");
			}
			if (StringUtils.isBlank(planEndTime)) {
				throw new IllegalArgumentException("请选择计划结束时间");
			}
			Date planStartTimeValue = DateUtils.parse2Date(planStartTime);
			Date planEndTimeValue = DateUtils.parse2Date(planEndTime);
			if (planEndTimeValue.before(planStartTimeValue)) {
				throw new IllegalArgumentException("计划结束时间早于计划开始时间，请重新选择");
			}
			
			if (StringUtils.isBlank(planId)) {
				Map<String, Object> conditionMap = new HashMap<String, Object>();
				conditionMap.put("planName", planName);
				conditionMap.put("crewId", crewId);
				List<ShootPlanModel> shootPlanList = this.shootPlanService.queryManyByMutiCondition(conditionMap, null);
				if (shootPlanList != null && shootPlanList.size() > 0) {
					throw new IllegalArgumentException("已有相同名称的拍摄计划，请输入其他拍摄计划名称");
				}
			}
			
			//添加拍摄计划信息
			message = this.shootPlanService.saveShootPlan(planId, planName, viewIds, planStartTime, planEndTime, groupId, parentPlanId, crewId);
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			
			//logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，保存拍摄计划信息失败";
			
			logger.error(message, e);
		}
		
		try {
			if (success) {
//				this.sysLogService.saveSysLog(request, message, Constants.TERMINAL_PC, ShootPlanModel.TABLE_NAME, planId,2);
			}
		} catch (Exception e) {
			logger.error("未知异常，添加系统日志失败");
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 把场景添加到拍摄计划中
	 * @param planIds	计划ID，多个ID以逗号隔开
	 * @param viewIds	场景ID，多个ID以逗号隔开
	 * @return
	 */
	@RequestMapping("/addViewToPlan")
	public @ResponseBody Map<String, Object> addViewToPlan(HttpServletRequest request, String planIds, String viewIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String crewId = this.getCrewId(request);
		
		boolean success = false;
		String message = "";
		
		String idArrayStr = "";
		try {
			//校验数据
			if (StringUtils.isBlank(planIds)) {
				throw new IllegalArgumentException("请选择拍摄计划");
			}
			if (StringUtils.isBlank(viewIds)) {
				throw new IllegalArgumentException("请选择场景");
			}
			
			//处理数据
			idArrayStr = this.viewPlanMapService.addViewToPlan(planIds, viewIds, crewId);
			
			success = true;
			message = "添加到拍摄计划中成功";
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			
			logger.error(ie.getMessage(), ie);
		} catch (Exception e) {
			String msg = "未知异常，把场景添加到计划中失败";
			
			success = false;
			message = msg;
			
			logger.error(msg, e);
		}
		
		try {
			if (success) {
//				this.sysLogService.saveSysLog(request, "添加场景到拍摄计划", Constants.TERMINAL_PC, "tab_view_plan_map", idArrayStr,1);
			}
		} catch (Exception e) {
			logger.error("添加系统日志失败", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 从拍摄计划中删除场景
	 * @param request
	 * @param planId	拍摄计划ID
	 * @param viewIds	需要删除关系的场景ID
	 * @return
	 */
	@RequestMapping("/deleteViewFromPlan")
	public @ResponseBody Map<String, Object> deleteViewFromPlan(HttpServletRequest request, String planId, String viewIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = false;
		String message = "";
		
		String idArrayStr = "";
		try {
			if (StringUtils.isBlank(planId)) {
				throw new IllegalArgumentException("请选择一条拍摄计划");
			}
			if (StringUtils.isBlank(viewIds)) {
				throw new IllegalArgumentException("请选择需要删除的场景");
			}
			
			String[] viewIdsArr = viewIds.split(",");
			this.viewPlanMapService.deleteByPlanIdAndViewIds(planId, viewIdsArr);
			
			success = true;
			message = "移除成功";
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			
			logger.error(ie.getMessage(), ie);
		} catch (Exception e) {
			String msg = "未知异常，把场景从计划中删除失败";
			
			success = false;
			message = msg;
			
			logger.error(msg, e);
		}
		
		try {
			if (success) {
//				this.sysLogService.saveSysLog(request, "从拍摄计划中删除场景", Constants.TERMINAL_PC, "tab_view_plan_map", idArrayStr,3);
			}
		} catch (Exception e) {
			logger.error("添加系统日志失败", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
		
	}
	
	
	/**
	 * 设置场景的拍摄时间
	 * @param request
	 * @param viewIds
	 * @param shootDate
	 * @return
	 */
	@RequestMapping("saveShootDate")
	public @ResponseBody Map<String, Object> saveShootDate(HttpServletRequest request, String planId, String viewIds, String shootDate) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String crewId = this.getCrewId(request);
		
		boolean success = true;
		String message = "";
		String idArrayStr = "";
		try {
			/*
			 * 校验数据
			 */
			if (StringUtils.isBlank(viewIds)) {
				throw new IllegalArgumentException("请选择场次");
			}
			if (StringUtils.isBlank(shootDate)) {
				throw new IllegalArgumentException("请选择拍摄时间");
			}
			if (StringUtils.isBlank(planId)) {
				throw new IllegalArgumentException("请选择一条拍摄计划");
			}
			
			//只能选择计划周期内的日期，已过期计划只能修改计划的起止日期
			ShootPlanModel shootPlan = this.shootPlanService.queryOneByPlanId(planId);
			Date shootDateValue = DateUtils.parse2Date(shootDate);
			Date planStartDate = shootPlan.getStartDate();
			Date planEndDate = shootPlan.getEndDate();
			Date nowDate = DateUtils.parse2Date(DateUtils.format(new Date(), "yyyy-MM-dd"));
			if (planEndDate.before(nowDate)) {
				throw new IllegalArgumentException("该计划已过期，请修改计划的起止时间后再设置对应的拍摄日期");
			}
			if (shootDateValue.before(planStartDate) || shootDateValue.after(planEndDate)) {
				throw new IllegalArgumentException("拍摄日期超出计划周期范围，请重新选择");
			}
			
			/*
			 * 处理数据
			 */
			String[] viewIdArr = viewIds.split(",");
			List<ViewPlanMapModel> toUpdateMapList = new ArrayList<ViewPlanMapModel>();
			for (String viewId : viewIdArr) {
				Map<String, Object> conditionMap = new HashMap<String, Object>();
				conditionMap.put("viewId", viewId);
				conditionMap.put("planId", planId);
				
				List<ViewPlanMapModel> viewPlanMapList = this.viewPlanMapService.queryManyByMutiCondition(conditionMap, null);
				if (viewPlanMapList != null && viewPlanMapList.size() > 0) {
					ViewPlanMapModel viewPlanMap = viewPlanMapList.get(0);
					viewPlanMap.setShootDate(DateUtils.parse2Date(shootDate));
					toUpdateMapList.add(viewPlanMap);
					
					idArrayStr += viewPlanMap.getMapId() + ",";
				}
			}
			
			this.viewPlanMapService.updateMany(toUpdateMapList);
			
			success = true;
			message = "设置成功";
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			
			logger.error(ie.getMessage(), ie);
		} catch (Exception e) {
			String msg = "未知异常，设置拍摄时间失败";
			success = false;
			message = msg;
			
			logger.error(msg, e);
		}
		
		try {
			if (success) {
//				this.sysLogService.saveSysLog(request, "设置拍摄计划的拍摄时间", Constants.TERMINAL_PC, ViewPlanMapModel.TABLE_NAME, idArrayStr,2);
			}
		} catch (Exception e) {
			logger.error("添加系统日志失败");
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
		
	}
	
	/**
	 * 删除拍摄计划
	 * @param request
	 * @param planId	拍摄计划ID
	 * @return
	 */
	@RequestMapping("/delteShootPlan")
	public @ResponseBody Map<String, Object> deleteShootPlan(HttpServletRequest request, String planId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(planId)) {
				throw new IllegalArgumentException("请选择一条拍摄计划");
			}
			
			this.shootPlanService.deleteShootPlan(planId);
			
			success = true;
			message = "删除拍摄计划成功";
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			logger.error("未知异常，删除拍摄计划失败");
			
			success = false;
			message = "未知异常，删除拍摄计划失败";
		}
		
		try {
			if (success) {
//				this.sysLogService.saveSysLog(request, "删除拍摄计划", Constants.TERMINAL_PC, ShootPlanModel.TABLE_NAME, planId,3);
			}
		} catch (Exception e) {
			logger.error("未知异常，保存系统日志失败", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 导出拍摄计划信息
	 * @param request
	 * @param planId
	 * @return
	 */
	@RequestMapping("/exportPlanInfo")
	public ModelAndView exportPlanInfo(HttpServletRequest request, HttpServletResponse response, String planId) {
		String crewId = this.getCrewId(request);
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");
		DecimalFormat df = new DecimalFormat("#0.00");
		
		try {
			if (StringUtils.isBlank(planId)) {
				throw new IllegalArgumentException("请选择一条拍摄计划");
			}
			ShootPlanModel shootPlanInfo = this.shootPlanService.queryOneByPlanId(planId);
			List<Map<String, Object>> viewInfoList = this.viewPlanMapService.queryFullViewInfoByPlanId(planId, crewId, null, true, null);
			
			String planName = shootPlanInfo.getPlanName();
			String startDateStr = sdf1.format(shootPlanInfo.getStartDate());
			String endDateStr = sdf1.format(shootPlanInfo.getEndDate());
			String groupId = shootPlanInfo.getGroupId();
			String updateTimeStr = sdf2.format(shootPlanInfo.getUpdateTime());
			
			ShootGroupModel shootGroup = this.shootGroupService.queryOneByGroupId(groupId);
			String groupName = shootGroup.getGroupName();
			
			//场数
			int viewNumTotal = viewInfoList.size();
			
			//页数
			double pageCountNumTotal = 0.0;
			
			//完成率
			double finishRate = 0.0;
			int finishViewCount = 0;	//完成的场数
			
			for (Map<String, Object> viewInfo : viewInfoList) {
				double pageCount = (Double) viewInfo.get("pageCount");
				pageCountNumTotal += pageCount;
				
				int shootStatus = (Integer) viewInfo.get("shootStatus");
				if (shootStatus == ShootStatus.Finished.getValue() 
						|| shootStatus == ShootStatus.DeleteXi.getValue() 
						|| shootStatus == ShootStatus.AddXiFinished.getValue()) {
					finishViewCount ++;
				}
			}
			if (viewNumTotal != 0) {
				finishRate = finishViewCount / viewNumTotal * 100;
			}
			

			//文档主标题
			String mainTitle = planName + "(" + startDateStr + "-" + endDateStr + ")(" + groupName + ")";
			StringBuilder subTitle = new StringBuilder("共计: ");
			subTitle.append(DateUtils.daysBetween(shootPlanInfo.getStartDate(), shootPlanInfo.getEndDate()) + 1 + "天, ");
			subTitle.append(viewNumTotal + "场, ");
			subTitle.append(df.format(pageCountNumTotal) + "页, ");
			subTitle.append(df.format(finishRate) + "%已完成 | ");
			subTitle.append("本计划最后修改时间: " + updateTimeStr);
			
			// 创建Excel的工作书册 Workbook,对应到一个excel文档
			HSSFWorkbook wb = new HSSFWorkbook();
			// 创建Excel的工作sheet,对应到一个excel文档的tab
			HSSFSheet sheet = wb.createSheet(mainTitle);
			sheet.setColumnWidth(0, 2000);
			sheet.setColumnWidth(1, 5000);
			sheet.setColumnWidth(2, 5000);
			sheet.setColumnWidth(3, 2000);
			sheet.setColumnWidth(4, 2000);
			sheet.setColumnWidth(5, 5000);
			sheet.setColumnWidth(6, 5000);
			sheet.setColumnWidth(7, 5000);
			sheet.setColumnWidth(8, 5000);
			sheet.setColumnWidth(9, 5000);
			sheet.setColumnWidth(10, 5000);
			sheet.setColumnWidth(11, 10000);
			sheet.setColumnWidth(12, 3000);
			sheet.setColumnWidth(13, 2000);
			sheet.setColumnWidth(14, 5000);
			sheet.setColumnWidth(15, 5000);
			sheet.setColumnWidth(16, 5000);
			sheet.setColumnWidth(17, 5000);
			
			HSSFFont headerFont = wb.createFont();
			headerFont.setFontName("微软雅黑");
			headerFont.setBoldweight((short) 100);
			headerFont.setFontHeight((short) 300);
			//font.setColor(HSSFColor.BLUE.index);
			
			HSSFCellStyle headerStyle = wb.createCellStyle();
			headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			headerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			//headerStyle.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
			//headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

			// 设置边框
			//headerStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			//headerStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			/*headerStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			headerStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			headerStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);*/

			headerStyle.setFont(headerFont);// 设置字体
			
			////////////////////////////////////////////////////////////////////////////第一行//////////////////////////////////////////////////
			HSSFRow row = sheet.createRow(0);
			row.setHeight((short) 1000);// 设定行的高度
			// 创建一个Excel的单元格
			HSSFCell cell = row.createCell(0);
			// 合并单元格(startRow，endRow，startColumn，endColumn)
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 17));
			cell.setCellStyle(headerStyle);
			cell.setCellValue(mainTitle);
			
			//////////////////////////////////////////////////////////////////////////////第二行////////////////////////////////////////////////////
			HSSFFont secondRowFont = wb.createFont();
			secondRowFont.setFontName("微软雅黑");
			secondRowFont.setBoldweight((short) 50);
			secondRowFont.setFontHeight((short) 250);
			
			HSSFCellStyle secondRowStyle = wb.createCellStyle();
			secondRowStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			secondRowStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			secondRowStyle.setFont(secondRowFont);
			
			
			row = sheet.createRow(1);
			row.setHeight((short)700);
			cell = row.createCell(0);
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 17));
			cell.setCellValue(subTitle.toString());
			cell.setCellStyle(secondRowStyle);
			
			
			/////////////////////////////////////////////////////////////////////////////第三行///////////////////////////////////////////////////////////
			HSSFFont thirdRowFont = wb.createFont();
			thirdRowFont.setFontName("微软雅黑");
			thirdRowFont.setBoldweight((short) 30);
			thirdRowFont.setFontHeight((short) 200);
			
			HSSFCellStyle thirdRowStyle = wb.createCellStyle();
			thirdRowStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			thirdRowStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			thirdRowStyle.setFont(thirdRowFont);
			
			//第一列
			row = sheet.createRow(2);
			row.setHeight((short)500);
			
			cell = row.createCell(0);
			cell.setCellValue("集-场");
			cell.setCellStyle(thirdRowStyle);

			cell = row.createCell(1);
			cell.setCellValue("计划拍摄时间");
			cell.setCellStyle(thirdRowStyle);
			
			cell = row.createCell(2);
			cell.setCellValue("拍摄地点");
			cell.setCellStyle(thirdRowStyle);
			
			cell = row.createCell(3);
			cell.setCellValue("气氛");
			cell.setCellStyle(thirdRowStyle);
			
			cell = row.createCell(4);
			cell.setCellValue("内外景");
			cell.setCellStyle(thirdRowStyle);
			
			cell = row.createCell(5);
			cell.setCellValue("主场景");
			cell.setCellStyle(thirdRowStyle);
			
			cell = row.createCell(6);
			cell.setCellValue("次场景");
			cell.setCellStyle(thirdRowStyle);
			
			cell = row.createCell(7);
			cell.setCellValue("三级场景");
			cell.setCellStyle(thirdRowStyle);
			
			cell = row.createCell(8);
			cell.setCellValue("主要演员");
			cell.setCellStyle(thirdRowStyle);
			
			cell = row.createCell(9);
			cell.setCellValue("特约演员");
			cell.setCellStyle(thirdRowStyle);
			
			cell = row.createCell(10);
			cell.setCellValue("群众演员");
			cell.setCellStyle(thirdRowStyle);
			
			cell = row.createCell(11);
			cell.setCellValue("主要内容");
			cell.setCellStyle(thirdRowStyle);
			
			cell = row.createCell(12);
			cell.setCellValue("文武戏");
			cell.setCellStyle(thirdRowStyle);
			
			cell = row.createCell(13);
			cell.setCellValue("页数");
			cell.setCellStyle(thirdRowStyle);
			
			cell = row.createCell(14);
			cell.setCellValue("服装");
			cell.setCellStyle(thirdRowStyle);
			
			cell = row.createCell(15);
			cell.setCellValue("化妆");
			cell.setCellStyle(thirdRowStyle);
			
			cell = row.createCell(16);
			cell.setCellValue("道具");
			cell.setCellStyle(thirdRowStyle);
			
			cell = row.createCell(17);
			cell.setCellValue("备注");
			cell.setCellStyle(thirdRowStyle);
			
			
			/////////////////////////////////////////////////以下都是场景内容///////////////////////////////////////////
			HSSFFont detailRowFont = wb.createFont();
			detailRowFont.setFontName("微软雅黑");
			detailRowFont.setBoldweight((short) 20);
			detailRowFont.setFontHeight((short) 180);
			
			HSSFCellStyle detailRowStyle = wb.createCellStyle();
			detailRowStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			detailRowStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			detailRowStyle.setFont(detailRowFont);
			
			for (int i = 0; i < viewInfoList.size(); i++) {
				Map<String, Object> viewInfo = viewInfoList.get(i);
				String shootDate = viewInfo.get("shootDate") + "";
				if (shootDate.equals("null") || StringUtils.isBlank(shootDate)) {
					shootDate = "";
				} else {
					shootDate = sdf3.format((Date)viewInfo.get("shootDate"));
				}
				String shootLocation = viewInfo.get("shootLocation") + "";
				if (shootLocation.equals("null") || StringUtils.isBlank(shootLocation)) {
					shootLocation = "";
				}

				String atmosphereName = viewInfo.get("atmosphereName") + "";
				if (atmosphereName.equals("null") || StringUtils.isBlank(atmosphereName)) {
					atmosphereName = "";
				}

				String site = viewInfo.get("site") + "";
				if (site.equals("null") || StringUtils.isBlank(site)) {
					site = "";
				}

				String firstLocation = viewInfo.get("firstLocation") + "";
				if (firstLocation.equals("null") || StringUtils.isBlank(firstLocation)) {
					firstLocation = "";
				}

				String secondLocation = viewInfo.get("secondLocation") + "";
				if (secondLocation.equals("null") || StringUtils.isBlank(secondLocation)) {
					secondLocation = "";
				}

				String thirdLocation = viewInfo.get("thirdLocation") + "";
				if (thirdLocation.equals("null") || StringUtils.isBlank(thirdLocation)) {
					thirdLocation = "";
				}

				String majorRole = viewInfo.get("majorRole") + "";
				if (majorRole.equals("null") || StringUtils.isBlank(majorRole)) {
					majorRole = "";
				}

				String guestRole = viewInfo.get("guestRole") + "";
				if (guestRole.equals("null") || StringUtils.isBlank(guestRole)) {
					guestRole = "";
				}

				String massRole = viewInfo.get("massRole") + "";
				if (massRole.equals("null") || StringUtils.isBlank(massRole)) {
					massRole = "";
				}

				String mainContent = viewInfo.get("mainContent") + "";
				if (mainContent.equals("null") || StringUtils.isBlank(mainContent)) {
					mainContent = "";
				}

				String viewTypeStr = "";
				String viewTypeOrig = viewInfo.get("viewType") + "";
				if (!viewTypeOrig.equals("null") && !StringUtils.isBlank(viewTypeOrig)) {
					int viewType = Integer.parseInt(viewTypeOrig);
					if (viewType == ViewType.Wuxi.getValue()) {
						viewTypeStr = Constants.LITERATE;
					}
					if (viewType == ViewType.TeXiao.getValue()) {
						viewTypeStr = Constants.KUNGFU;
					}
					if (viewType == ViewType.WuTe.getValue()) {
						viewTypeStr = Constants.MIXED;
					}
				}

				String pageCount = viewInfo.get("pageCount") + "";
				if (pageCount.equals("null") || StringUtils.isBlank(pageCount)) {
					pageCount = "";
				}

				String clothesName = viewInfo.get("clothesName") + "";
				if (clothesName.equals("null") || StringUtils.isBlank(clothesName)) {
					clothesName = "";
				}

				String makeupName = viewInfo.get("makeupName") + "";
				if (makeupName.equals("null") || StringUtils.isBlank(makeupName)) {
					makeupName = "";
				}

				String propsName = viewInfo.get("propsName") + "";
				if (propsName.equals("null") || StringUtils.isBlank(propsName)) {
					propsName = "";
				}

				String remark = viewInfo.get("remark") + "";
				if (remark.equals("null") || StringUtils.isBlank(remark)) {
					remark = "";
				}
				
				row = sheet.createRow(i + 3);
				row.setHeight((short)500);
				
				cell = row.createCell(0);
				cell.setCellValue(viewInfo.get("seriesNo") + "-" + viewInfo.get("viewNo"));
				cell.setCellStyle(detailRowStyle);
				
				cell = row.createCell(1);
				cell.setCellValue(shootDate);
				cell.setCellStyle(detailRowStyle);

				cell = row.createCell(2);
				cell.setCellValue(shootLocation);
				cell.setCellStyle(detailRowStyle);

				cell = row.createCell(3);
				cell.setCellValue(atmosphereName);
				cell.setCellStyle(detailRowStyle);

				cell = row.createCell(4);
				cell.setCellValue(site);
				cell.setCellStyle(detailRowStyle);

				cell = row.createCell(5);
				cell.setCellValue(firstLocation);
				cell.setCellStyle(detailRowStyle);

				cell = row.createCell(6);
				cell.setCellValue(secondLocation);
				cell.setCellStyle(detailRowStyle);

				cell = row.createCell(7);
				cell.setCellValue(thirdLocation);
				cell.setCellStyle(detailRowStyle);

				cell = row.createCell(8);
				cell.setCellValue(majorRole);
				cell.setCellStyle(detailRowStyle);

				cell = row.createCell(9);
				cell.setCellValue(guestRole);
				cell.setCellStyle(detailRowStyle);

				cell = row.createCell(10);
				cell.setCellValue(massRole);
				cell.setCellStyle(detailRowStyle);

				cell = row.createCell(11);
				cell.setCellValue(mainContent);
				cell.setCellStyle(detailRowStyle);

				cell = row.createCell(12);
				cell.setCellValue(viewTypeStr);
				cell.setCellStyle(detailRowStyle);

				cell = row.createCell(13);
				cell.setCellValue(pageCount);
				cell.setCellStyle(detailRowStyle);

				cell = row.createCell(14);
				cell.setCellValue(clothesName);
				cell.setCellStyle(detailRowStyle);

				cell = row.createCell(15);
				cell.setCellValue(makeupName);
				cell.setCellStyle(detailRowStyle);

				cell = row.createCell(16);
				cell.setCellValue(propsName);
				cell.setCellStyle(detailRowStyle);

				cell = row.createCell(17);
				cell.setCellValue(remark);
				cell.setCellStyle(detailRowStyle);
			}
			
			
			/////////////////////////////////文件下载/////////////////////////////////
			response.reset();
	        response.setContentType("application/octet-stream; charset=GBK");
	        response.addHeader("Content-Disposition", "attachment; filename=" + 
	                new String((mainTitle + ".xls").getBytes("gb2312"),"iso8859-1"));
			
	        OutputStream outputStream = response.getOutputStream();
	        wb.write(outputStream);
	        
	        outputStream.flush();
			outputStream.close();
			
		} catch (Exception e) {
			String msg = "未知异常，导出拍摄计划失败";
			logger.error(msg, e);
		}
		
		try {
//			this.sysLogService.saveSysLog(request, "导出拍摄计划", Constants.TERMINAL_PC, ShootPlanModel.TABLE_NAME, planId,5);
		} catch (Exception e) {
			logger.error("未知异常，保存系统日志失败", e);
		}
		
		return null;
	}
	
	
	/**
	 * 导出拍摄计划信息
	 * @param request
	 * @param planId
	 * @return
	 */
	@RequestMapping("/exportPlanInfoByTemplate")
	public ModelAndView exportPlanInfoByTemplate(HttpServletRequest request, HttpServletResponse response, String planId) {
		String crewId = this.getCrewId(request);
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");
		DecimalFormat df = new DecimalFormat("#0.00");
		
		String fileName = "《" + crewInfo.getCrewName() + "》";
		try {
			if (StringUtils.isBlank(planId)) {
				throw new IllegalArgumentException("请选择一条拍摄计划");
			}
			ShootPlanModel shootPlanInfo = this.shootPlanService.queryOneByPlanId(planId);
			List<Map<String, Object>> viewInfoList = this.viewPlanMapService.queryFullViewInfoByPlanId(planId, crewId, null, true, null);
			
			Map<String, Object> dataMap = new HashMap<String, Object>();
			
			
			String planName = shootPlanInfo.getPlanName();
			String startDateStr = sdf1.format(shootPlanInfo.getStartDate());
			String endDateStr = sdf1.format(shootPlanInfo.getEndDate());
			String groupId = shootPlanInfo.getGroupId();
			String updateTimeStr = sdf2.format(shootPlanInfo.getUpdateTime());
			
			fileName += planName;
			ShootGroupModel shootGroup = this.shootGroupService.queryOneByGroupId(groupId);
			String groupName = "";
			if (shootGroup != null) {
				groupName = shootGroup.getGroupName();
			}
			
			String dayCount = DateUtils.daysBetween(shootPlanInfo.getStartDate(), shootPlanInfo.getEndDate()) + 1 + "";
			
			//场数
			int viewNumTotal = viewInfoList.size();
			
			//页数
			double pageCountNumTotal = 0.0;
			
			//完成率
			double finishRate = 0.0;
			int finishViewCount = 0;	//完成的场数
			
			for (Map<String, Object> viewInfo : viewInfoList) {
				double pageCount = (Double) viewInfo.get("pageCount");
				pageCountNumTotal += pageCount;
				
				int shootStatus = (Integer) viewInfo.get("shootStatus");
				if (shootStatus == ShootStatus.Finished.getValue() 
						|| shootStatus == ShootStatus.DeleteXi.getValue() 
						|| shootStatus == ShootStatus.AddXiFinished.getValue()) {
					finishViewCount ++;
				}
			}
			if (viewNumTotal != 0) {
				finishRate = finishViewCount / viewNumTotal * 100;
			}
			
			dataMap.put("shootPlanName", planName);
			dataMap.put("startDate", startDateStr);
			dataMap.put("endDate", endDateStr);
			dataMap.put("groupName", groupName);
			dataMap.put("dayCount", dayCount);
			dataMap.put("sceneCount", viewNumTotal + "");
			dataMap.put("pageCount", pageCountNumTotal + "");
			dataMap.put("lastUpdateTime", updateTimeStr);
			dataMap.put("finishRate", finishRate);
			
			/////////////////////////////////////////////////以下都是场景内容///////////////////////////////////////////
			List<String> majorRoleList = new ArrayList<String>();
			for (Map<String, Object> viewInfo : viewInfoList) {
				//文武戏处理
				String viewTypeStr = "";
				String viewTypeOrig = viewInfo.get("viewType") + "";
				if (!viewTypeOrig.equals("null") && !StringUtils.isBlank(viewTypeOrig)) {
					int viewType = Integer.parseInt(viewTypeOrig);
					if (viewType == ViewType.Wuxi.getValue()) {
						viewTypeStr = Constants.LITERATE;
					}
					if (viewType == ViewType.TeXiao.getValue()) {
						viewTypeStr = Constants.KUNGFU;
					}
					if (viewType == ViewType.WuTe.getValue()) {
						viewTypeStr = Constants.MIXED;
					}
				}
				viewInfo.remove("viewType");
				viewInfo.put("viewType", viewTypeStr);
				
				//主要演员处理
				//查出所有的主要演员
				String majorRoles = viewInfo.get("majorRole") + "";
				if (majorRoles.equals("null") || StringUtils.isBlank(majorRoles)) {
					majorRoles = "";
				} else {
					String[] majorRoleArr = majorRoles.split(",");
					for(String majorRole : majorRoleArr) {
						if (!majorRoleList.contains(majorRole)) {
							majorRoleList.add(majorRole);
						}
					}
				}
				
				//拍摄时间处理
				String shootDate = viewInfo.get("shootDate") + "";
				if (shootDate.equals("null") || StringUtils.isBlank(shootDate)) {
					shootDate = "";
				} else {
					shootDate = sdf3.format((Date)viewInfo.get("shootDate"));
				}
				viewInfo.remove("shootDate");
				viewInfo.put("shootDate", shootDate);
				
				
			}
			
			for (Map<String, Object> viewInfo : viewInfoList) {
				String viewMajorRoles = viewInfo.get("majorRole") + "";
				List<String> viewNewRoleList = new ArrayList<String>();
				List<String> viewMajorRoleList = new ArrayList<String>();
				if (viewMajorRoles != null && !StringUtils.isBlank(viewMajorRoles)) {
					viewMajorRoleList = Arrays.asList(viewMajorRoles.split(","));
				}
				for (String majorRole : majorRoleList) {
					if (viewMajorRoleList.contains(majorRole)) {
						viewNewRoleList.add("√");
					} else {
						viewNewRoleList.add(new String());
					}
				}
				viewInfo.put("viewRoleList", viewNewRoleList);
			}
			
			dataMap.put("planViewList", viewInfoList);
			dataMap.put("mainRoleList", majorRoleList);
			
			//获取模板文件地址配置
			Properties property = PropertiesUitls.fetchProperties("/config.properties");
			String srcfilePath = property.getProperty("planTemplate");
			String downloadPath = property.getProperty("downloadPath")+"/"+fileName+".xls";
			File pathFile = new File(property.getProperty("downloadPath"));
			if(!pathFile.isDirectory()){
				pathFile.mkdirs();
			}
			
			ExportExcelUtil.downloadExcel(response, srcfilePath, downloadPath, dataMap);
		} catch (Exception e) {
			String msg = "未知异常，导出拍摄计划失败";
			logger.error(msg, e);
		}

		try {
//			this.sysLogService.saveSysLog(request, "导出拍摄计划", Constants.TERMINAL_PC, ShootPlanModel.TABLE_NAME, planId,5);
		} catch (Exception e) {
			logger.error("未知异常，保存系统日志失败", e);
		}
		return null;
	}
}
