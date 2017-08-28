package com.xiaotu.makeplays.shoot.controller;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.car.model.CarWorkModel;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.model.constants.CrewType;
import com.xiaotu.makeplays.roleactor.model.constants.ViewRoleType;
import com.xiaotu.makeplays.roleactor.service.ViewRoleService;
import com.xiaotu.makeplays.shoot.model.ScheduleGroupModel;
import com.xiaotu.makeplays.shoot.model.ScheduleViewMapModel;
import com.xiaotu.makeplays.shoot.model.ShootGroupModel;
import com.xiaotu.makeplays.shoot.service.ScheduleService;
import com.xiaotu.makeplays.shoot.service.ShootGroupService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.ExcelUtils;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.view.controller.filter.ViewFilter;
import com.xiaotu.makeplays.view.model.ViewInfoModel;
import com.xiaotu.makeplays.view.service.AtmosphereService;
import com.xiaotu.makeplays.view.service.ViewInfoService;

/**
 * @类名：ScheduleController.java
 * @作者：李晓平
 * @时间：2017年6月19日 下午6:30:17
 * @描述：计划
 */
@Controller
@RequestMapping("/scheduleManager")
public class ScheduleController extends BaseController{

	Logger logger = LoggerFactory.getLogger(ScheduleController.class);
	
	private final Integer terminal = Constants.TERMINAL_PC;
	
	private DecimalFormat df = new DecimalFormat("0.0");
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
	
	@Autowired
	private ScheduleService scheduleService;
	
	@Autowired
	private AtmosphereService atmosphereService;
	
	@Autowired
	private ViewRoleService viewRoleService;
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	@Autowired
	private ShootGroupService shootGroupService;
	
	
	
	/**
	 * 跳转到计划页面
	 * @return
	 */
	@RequestMapping("/toScheduleListPage")
	public ModelAndView toAddTeamInfoPage() {
		ModelAndView mv = new ModelAndView("/schedule/scheduleList");
		return mv;
	}
	
	/**
	 * 跳转到计划分组页面
	 * @return
	 */
	@RequestMapping("/toScheduleGroupListPage")
	public ModelAndView toScheduleGroupListPage(String flag) {
		ModelAndView mv = new ModelAndView("/schedule/scheduleGroupList");
		mv.addObject("flag", flag);
		return mv;
	}
	
	/**
	 * 跳转到查看计划页面
	 * @return
	 */
	@RequestMapping("/toScheduleCalendarPage")
	public ModelAndView toScheduleCalendarPage() {
		ModelAndView mv = new ModelAndView("/schedule/scheduleCalendar");
		return mv;
	}
	
	/**
	 * 跳转到计划详情页面
	 * @return
	 */
	@RequestMapping("/toScheduleDetailPage")
	public ModelAndView toScheduleDetailPage() {
		ModelAndView mv = new ModelAndView("/schedule/scheduleDetail");
		return mv;
	}
	
	/**
	 * 查询计划场景表数据
	 * @param request
	 * @param page 分页参数对象
	 * @param filter 查询过滤条件对象
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryViewList")
	public Map<String, Object> queryViewList(HttpServletRequest request, Page page, ViewFilter filter){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		String crewId = getCrewId(request);
		try {
			List<Map<String, Object>> resultList = scheduleService.queryViewList(crewId, page, filter);
			// 返回的list中每个元素都为Map，map中都包含一个属性roleList，roleList为当前场的所有演员Id
			if (null == resultList) {
				resultList = new ArrayList<Map<String, Object>>();
			}
			page.setResultList(resultList);
			
			resultMap.put("result", page);
		} catch (Exception e) {
			message = "未知错误,查询计划场景表失败!";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);		

		return resultMap;
	}
	
	/**
	 * 查询场景统计数据
	 * @param request
	 * @param filter 过滤条件
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryViewTotal")
	public Map<String, Object> queryViewTotal(HttpServletRequest request, ViewFilter filter) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);	
			// 统计信息
			Map<String, Object> viewStatistics = scheduleService.queryViewStatistics(crewId, filter);
			resultMap.put("viewStatistics", viewStatistics);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		}catch (Exception e) {
			message = "未知异常,查询统计数据失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 查询计划分组列表
	 * @param request
	 * @param groupName 分组名称
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryScheduleGroupList")
	public Map<String, Object> queryScheduleGroupList(HttpServletRequest request, String groupName) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);	
			List<Map<String, Object>> scheduleGroupList = this.scheduleService.queryScheduleGroupList(crewId, groupName);
			if(scheduleGroupList != null && scheduleGroupList.size() > 0) {
				for(Map<String, Object> one : scheduleGroupList) {
					if(StringUtil.isNotBlank(one.get("pageCount") + "")) {
						one.put("pageCount", df.format(one.get("pageCount")));
					}
					if(StringUtil.isNotBlank(one.get("everyDayPage") + "")) {
						one.put("everyDayPage", df.format(one.get("everyDayPage")));
					}
				}
			}
			resultMap.put("scheduleGroupList", scheduleGroupList);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		}catch (Exception e) {
			message = "未知异常,查询计划分组列表失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 保存计划分组信息
	 * @param request
	 * @param groupId 分组ID
	 * @param groupName 分组名称
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveScheduleGroupInfo")
	public Map<String, Object> saveScheduleGroupInfo(HttpServletRequest request,String groupId, String groupName) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			if(StringUtils.isBlank(groupName)) {
				if(StringUtils.isNotBlank(groupId)) {
					ScheduleGroupModel scheduleGroupModel = this.scheduleService.queryScheduleGroupById(crewId, groupId);
					resultMap.put("groupName", scheduleGroupModel.getGroupName());
				}
				throw new IllegalArgumentException("请填写分组名称");
			}	
			ScheduleGroupModel scheduleGroupModel = this.scheduleService.saveScheduleGroupInfo(crewId, groupId, groupName);
			resultMap.put("groupId", scheduleGroupModel.getId());
			if(StringUtil.isBlank(groupId)) {
				this.sysLogService.saveSysLog(request, "新增计划分组信息", terminal, ScheduleGroupModel.TABLE_NAME, null, SysLogOperType.INSERT.getValue());
			} else {
				this.sysLogService.saveSysLog(request, "修改计划分组信息", terminal, ScheduleGroupModel.TABLE_NAME, null, SysLogOperType.UPDATE.getValue());
			}
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;			
			logger.error(message, ie);
		}catch (Exception e) {
			message = "未知异常,保存计划分组信息失败";
			success = false;			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "保存计划分组信息失败：" + e.getMessage(), terminal, ScheduleGroupModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 删除计划分组信息
	 * @param request
	 * @param groupIds 分组ID，多个以逗号分隔
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteScheduleGroupInfo")
	public Map<String, Object> deleteScheduleGroupInfo(HttpServletRequest request,String groupIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {	
			if(StringUtils.isBlank(groupIds)) {
				throw new IllegalArgumentException("请提供分组信息");
			}
			String crewId = this.getCrewId(request);
			this.scheduleService.deleteScheduleGroupInfo(crewId, groupIds);
			this.sysLogService.saveSysLog(request, "删除计划分组信息", terminal, ScheduleGroupModel.TABLE_NAME, null, SysLogOperType.DELETE.getValue());
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;			
			logger.error(message, ie);
		}catch (Exception e) {
			message = "未知异常,删除计划分组信息失败";
			success = false;			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "删除计划分组信息失败：" + e.getMessage(), terminal, ScheduleGroupModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 更新计划分组排序
	 * @param request
	 * @param groupIds 分组ID，多个以逗号分隔
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateScheduleGroupSequence")
	public Map<String, Object> updateScheduleGroupSequence(HttpServletRequest request, String groupIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {	
			if(StringUtils.isBlank(groupIds)) {
				throw new IllegalArgumentException("请提供分组信息");
			}
			String crewId = this.getCrewId(request);
			this.scheduleService.updateScheduleGroupSequence(crewId, groupIds);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;			
			logger.error(message, ie);
		}catch (Exception e) {
			message = "未知异常,更新计划分组排序失败";
			success = false;			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 更新场景锁定状态
	 * @param request
	 * @param viewIds 场景ID，多个以逗号分隔
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateViewGroupMapIsLock")
	public Map<String, Object> updateViewGroupMapIsLock(HttpServletRequest request, String viewIds, boolean isLock) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {	
			if(StringUtils.isBlank(viewIds)) {
				throw new IllegalArgumentException("请提供场景信息");
			}
			String crewId = this.getCrewId(request);
			this.scheduleService.updateViewGroupMapIsLock(crewId, viewIds, isLock);
			this.sysLogService.saveSysLog(request, "更新场景锁定状态", terminal, ScheduleViewMapModel.TABLE_NAME, null, SysLogOperType.UPDATE.getValue());
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			logger.error(message, ie);
		}catch (Exception e) {
			message = "未知异常,更新场景锁定状态失败";
			success = false;			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "更新场景锁定状态失败：" + e.getMessage(), terminal, ScheduleViewMapModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 设置计划日期和计划组别
	 * @param request
	 * @param viewIds 场景ID，多个以逗号分隔
	 * @param planDate 计划拍摄日期
	 * @param planGroupId 计划拍摄组别ID
	 * @param dayNum 提前/延后天数
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/setScheduleDateAndGroup")
	public Map<String, Object> setScheduleDateAndGroup(HttpServletRequest request, String viewIds, String planDate, String planGroupId, Integer dayNum) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {	
			if(StringUtils.isBlank(viewIds)) {
				throw new IllegalArgumentException("请提供场景信息");
			}	
//			if(StringUtils.isBlank(planDate) && StringUtils.isBlank(planGroupId)) {
//				throw new IllegalArgumentException("请设置计划日期或者计划组别");
//			}
			String crewId = this.getCrewId(request);
			this.scheduleService.setScheduleDateAndGroup(crewId, viewIds, planDate, planGroupId, dayNum);
			this.sysLogService.saveSysLog(request, "设置计划日期和计划组别", terminal, ScheduleViewMapModel.TABLE_NAME, null, SysLogOperType.UPDATE.getValue());
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;			
			logger.error(message, ie);
		}catch (Exception e) {
			message = "未知异常,设置计划日期和计划组别失败";
			success = false;			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "设置计划日期和计划组别失败：" + e.getMessage(), terminal, ScheduleViewMapModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 将场景移动到某个计划分组中
	 * @param request
	 * @param viewIds 场景ID，多个以逗号分隔
	 * @param groupId 移动到的计划分组ID
	 * @param targetViewId 粘贴的场景ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/setViewScheduleGroup")
	public Map<String, Object> setViewScheduleGroup(HttpServletRequest request, String viewIds, String groupId, String targetViewId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {	
			if(StringUtils.isBlank(viewIds)) {
				throw new IllegalArgumentException("请提供场景信息");
			}
			if(StringUtils.isBlank(groupId) && StringUtils.isBlank(targetViewId)) {
				throw new IllegalArgumentException("请提供要移动到的位置");
			}
			String crewId = this.getCrewId(request);
			groupId = this.scheduleService.setViewScheduleGroup(crewId, viewIds, groupId, targetViewId);
			resultMap.put("groupId", groupId);
			this.sysLogService.saveSysLog(request, "将场景移动到某个计划分组中", terminal, ScheduleViewMapModel.TABLE_NAME, null, SysLogOperType.UPDATE.getValue());
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			logger.error(message, ie);
		}catch (Exception e) {
			message = "未知异常,将场景移动到某个计划分组中失败";
			success = false;
			logger.error(message, e);
    		this.sysLogService.saveSysLog(request, "将场景移动到某个计划分组中失败：" + e.getMessage(), terminal, ScheduleViewMapModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 智能排期
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/autoSchedule")
	public Map<String, Object> autoSchedule(HttpServletRequest request, String conditionOne, String conditionTwo, String viewRole) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			this.scheduleService.autoSchedule(request, crewId, conditionOne, conditionTwo, viewRole);
			this.sysLogService.saveSysLog(request, "智能排期", terminal, ScheduleViewMapModel.TABLE_NAME, null, SysLogOperType.UPDATE.getValue());
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			logger.error(message, ie);
		}catch (Exception e) {
			message = "未知异常,智能排期失败";
			success = false;
			logger.error(message, e);
    		this.sysLogService.saveSysLog(request, "智能排期失败：" + e.getMessage(), terminal, ScheduleViewMapModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 查询关注项列表，包括主要演员、特殊道具、主场景列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryAttentionInfo")
	public Map<String, Object> queryAttentionInfo(HttpServletRequest request, String name) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			
			resultMap.putAll(this.scheduleService.queryAttentionInfo(crewId, name));
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			logger.error(message, ie);
		}catch (Exception e) {
			message = "未知异常,查询关注项列表失败";
			success = false;
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 查询关注项汇总信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryAttentionTotalInfo")
	public Map<String, Object> queryAttentionTotalInfo(HttpServletRequest request, String attention) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			List<Map<String, Object>> attentionTotal = this.scheduleService.queryAttentionTotalInfo(crewId, attention);
			if(attentionTotal != null && attentionTotal.size() > 0) {
				for(Map<String, Object> one : attentionTotal) {
					if((Double)one.get("pageCount") != null) {
						one.put("pageCount", df.format(one.get("pageCount")));
					}
				}
			}
			resultMap.put("attentionTotal", attentionTotal);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			logger.error(message, ie);
		}catch (Exception e) {
			message = "未知异常,查询关注项汇总信息失败";
			success = false;
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 查询计划按日汇总信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryScheduleCalendarInfo")
	public Map<String, Object> queryScheduleCalendarInfo(HttpServletRequest request, String attention) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			Map<String, Object> calendarInfo = this.scheduleService.queryScheduleCalendarInfo(crewId, attention);
			resultMap.put("calendarInfo", calendarInfo);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			logger.error(message, ie);
		}catch (Exception e) {
			message = "未知异常,查询计划按日汇总信息失败";
			success = false;
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 查询计划详情信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryScheduleDetailInfo")
	public Map<String, Object> queryScheduleDetailInfo(HttpServletRequest request, Page page) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			List<Map<String, Object>> resultList = this.scheduleService.queryScheduleDetail(crewId, page);
			if(resultList != null && resultList.size() > 0) {
				for(Map<String, Object> one : resultList) {
					if((Double)one.get("pageCount") != null) {
						one.put("pageCount", df.format(one.get("pageCount")));
					}
					if((Double)one.get("finishedPageCount") != null) {
						one.put("finishedPageCount", df.format(one.get("finishedPageCount")));
					}
				}
			}
			page.setResultList(resultList);
			resultMap.put("result", page);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			logger.error(message, ie);
		}catch (Exception e) {
			message = "未知异常,查询计划详情信息失败";
			success = false;
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 导出计划
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exportSchedule")
	public Map<String, Object> exportSchedule(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		try {
			String crewId = this.getCrewId(request);
			
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			//查询计划分组列表
			List<Map<String, Object>> scheduleGroupList = this.scheduleService.queryScheduleGroupList(crewId, null);
			//查询场景列表
			List<Map<String, Object>> viewList = scheduleService.queryViewList(crewId, null, null);
			
			// 返回的list中每个元素都为Map，map中都包含一个属性roleList，roleList为当前场的所有演员Id
			if (null == viewList) {
				viewList = new ArrayList<Map<String,Object>>();
			}
			
			// 拍摄状态
			Map<String, Object> shootStatusMap = new HashMap<String, Object>();
			shootStatusMap.put("0", "未完成");
			shootStatusMap.put("1", "部分完成");
			shootStatusMap.put("2", "完成");
			shootStatusMap.put("3", "删戏");
			
			// 主要演员信息
			List<Map<String, Object>> majorRoleList = this.viewRoleService.queryRoleMapByCrewIdAndRoleType(crewId, ViewRoleType.MajorActor.getValue());
			
			//最多只读取230的角色信息
			if (majorRoleList.size() > 230) {
				List<Map<String, Object>> newMajorRoleList = new ArrayList<Map<String, Object>>();
				newMajorRoleList.addAll(majorRoleList.subList(0, 229));
				majorRoleList = newMajorRoleList;
			}
			
			// 遍历查询的场景列表
			for (Map<String, Object> map : viewList) {
				
				if(null != map.get("planGroupName")) {
					if(((String) map.get("planGroupName")).equals("单组")) {
						map.put("planGroupName", "");
					}
				}
				
				String shootLocationRegion = "";
				if(StringUtil.isNotBlank((String) map.get("shootLocation"))) {
					shootLocationRegion = (String) map.get("shootLocation");
					if(StringUtil.isNotBlank((String) map.get("shootRegion"))) {
						shootLocationRegion += "(" + (String) map.get("shootRegion") + ")";
					}
				}
				map.put("shootLocationRegion", shootLocationRegion);
								
				//取出拍摄状态
				if (null != map.get("shootStatus")) {
					map.put("shootStatus", shootStatusMap.get(map.get("shootStatus") + ""));
				}
				
				//格式化拍摄日期
				if (null != map.get("shootDate")) {
					Date shotDate = (Date) map.get("shootDate");
					String shootDateStr = sdf.format(shotDate);
					map.put("shootDate", shootDateStr);
				}
				
				// 场景下的角色
				List<Map<String, Object>> roleList = (List<Map<String, Object>>) map.get("roleList");
				// 循环所有角色
				for (int i = 0; i < majorRoleList.size(); i++) {
					Map<String, Object> role = majorRoleList.get(i);
					
					boolean hasRoleFlag = false; // 标识当前场景的演员在所有主要演员中是否存在
					for (Map<String, Object> roleMap : roleList) {
						if (roleMap.get("viewRoleId").equals(role.get("viewRoleId"))) {
							if("0".equals(String.valueOf(roleMap.get("roleNum")))) {
								map.put(role.get("viewRoleId") + "", "OS");
							}else {
								if (StringUtils.isBlank((String) role.get("shortName"))) {
								map.put(role.get("viewRoleId") + "", "√");
								}else {
									map.put(role.get("viewRoleId") + "", role.get("shortName"));
								}
							}
							hasRoleFlag = true;
							break;
						}
					}
					
					// 如果不存在就添加一个空的对象，保证在表格中显示列正确
					if (!hasRoleFlag) {
						map.put(role.get("viewRoleId") + "", "");
					}
				}
			}
			Map<String, String> columnKeyMap = new LinkedHashMap<String, String>();
	        columnKeyMap.put("计划拍摄日期", "planShootDate");
	        columnKeyMap.put("计划拍摄组别", "planGroupName");
	        if(crewInfo.getCrewType() == CrewType.Movie.getValue() 
	        		|| crewInfo.getCrewType() == CrewType.InternetMovie.getValue()) {
		        columnKeyMap.put("场", "viewNo");
	        } else {
		        columnKeyMap.put("集", "seriesNo");
		        columnKeyMap.put("场", "viewNo");	        	
	        }
	        columnKeyMap.put("特殊提醒", "specialRemind");
	        columnKeyMap.put("气氛", "atmosphereName");
	        columnKeyMap.put("内外景", "site");
//	        columnKeyMap.put("拍摄地点", "shootLocation");
	        columnKeyMap.put("拍摄地点", "shootLocationRegion");
	        columnKeyMap.put("主场景", "majorView");
	        columnKeyMap.put("次场景", "minorView");
	        columnKeyMap.put("三级场景", "thirdLevelView");
	        columnKeyMap.put("主要内容", "mainContent");
	        columnKeyMap.put("页数", "pageCount");
	        for (Map<String, Object> map : majorRoleList) {
	        	columnKeyMap.put(map.get("viewRoleName") + "", map.get("viewRoleId") + "");
	        }
	        columnKeyMap.put("特约演员", "guestRoleList");
	        columnKeyMap.put("群众演员", "massRoleList");
	        columnKeyMap.put("服装", "clothesName");
	        columnKeyMap.put("化妆", "makeupName");
	        columnKeyMap.put("道具", "propsList");
	        columnKeyMap.put("特殊道具", "specialPropsList");
	        columnKeyMap.put("备注", "remark");
	        columnKeyMap.put("商植", "advertName");
	        columnKeyMap.put("拍摄时间", "shootDate");
	        columnKeyMap.put("拍摄状态", "shootStatus");
	        
	        //调用方法导出表格数据
    		ExcelUtils.exportScheduleForExcel(response, crewInfo, scheduleGroupList, viewList, columnKeyMap);
    		this.sysLogService.saveSysLog(request, "导出计划", terminal, ScheduleViewMapModel.TABLE_NAME, null, SysLogOperType.EXPORT.getValue());
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			logger.error(message, ie);
		}catch (Exception e) {
			message = "未知异常,导出计划失败";
			success = false;
			logger.error(message, e);
    		this.sysLogService.saveSysLog(request, "导入计划失败：" + e.getMessage(), terminal, ScheduleViewMapModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		resultMap.put("message", message);
		resultMap.put("success", success);		
		return resultMap;
	}
	
	/**
	 * 导出计划详情
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exportScheduleDetail")
	public Map<String, Object> exportScheduleDetail(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		try {
			String crewId = this.getCrewId(request);
			
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			
			//查询日期汇总信息
			List<Map<String, Object>> dateList = this.scheduleService.queryScheduleDetail(crewId, null);
			//查询场景信息
			ViewFilter filter = new ViewFilter();
			filter.setSortField("planShootDate"); //排序标识
			List<Map<String, Object>> viewInfoList = this.scheduleService.queryViewList(crewId, null, filter);
			
			// 拍摄状态
			Map<String, Object> shootStatusMap = new HashMap<String, Object>();
			shootStatusMap.put("0", "未完成");
			shootStatusMap.put("1", "部分完成");
			shootStatusMap.put("2", "完成");
			shootStatusMap.put("3", "删戏");
			
			// 主要演员信息
			List<Map<String, Object>> majorRoleList = this.viewRoleService.queryRoleMapByCrewIdAndRoleType(crewId, ViewRoleType.MajorActor.getValue());
			
			//最多只读取230的角色信息
			if (majorRoleList.size() > 230) {
				List<Map<String, Object>> newMajorRoleList = new ArrayList<Map<String, Object>>();
				newMajorRoleList.addAll(majorRoleList.subList(0, 229));
				majorRoleList = newMajorRoleList;
			}
			
			if(viewInfoList != null && viewInfoList.size() > 0) {
				for(Map<String, Object> map : viewInfoList) {			
					//取出拍摄状态
					if (null != map.get("shootStatus")) {
						map.put("shootStatus", shootStatusMap.get(map.get("shootStatus") + ""));
					}
					
					String shootLocationRegion = "";
					if(StringUtil.isNotBlank((String) map.get("shootLocation"))) {
						shootLocationRegion = (String) map.get("shootLocation");
						if(StringUtil.isNotBlank((String) map.get("shootRegion"))) {
							shootLocationRegion += "(" + (String) map.get("shootRegion") + ")";
						}
					}
					map.put("shootLocationRegion", shootLocationRegion);
					
					//格式化拍摄日期
					if (null != map.get("shootDate")) {
						Date shotDate = (Date) map.get("shootDate");
						String shootDateStr = sdf.format(shotDate);
						map.put("shootDate", shootDateStr);
					}
					
					// 场景下的角色
					List<Map<String, Object>> roleList = (List<Map<String, Object>>) map.get("roleList");
					// 循环所有角色
					for (int i = 0; i < majorRoleList.size(); i++) {
						Map<String, Object> role = majorRoleList.get(i);
						boolean hasRoleFlag = false; // 标识当前场景的演员在所有主要演员中是否存在
						for (Map<String, Object> roleMap : roleList) {
							if (roleMap.get("viewRoleId").equals(role.get("viewRoleId"))) {
								if("0".equals(String.valueOf(roleMap.get("roleNum")))) {
									map.put(role.get("viewRoleId") + "", "OS");
								}else {
									if (StringUtils.isBlank((String) role.get("shortName"))) {
									map.put(role.get("viewRoleId") + "", "√");
									}else {
										map.put(role.get("viewRoleId") + "", role.get("shortName"));
									}
								}
								hasRoleFlag = true;
								break;
							}
						}
						
						// 如果不存在就添加一个空的对象，保证在表格中显示列正确
						if (!hasRoleFlag) {
							map.put(role.get("viewRoleId") + "", "");
						}
					}
				}
				for(Map<String, Object> one : dateList) {
					if(one.get("planShootDate") != null) {
						one.put("planShootDate", sdf.format((Date)one.get("planShootDate")));
					}
					if((Double)one.get("pageCount") != null) {
						one.put("pageCount", df.format(one.get("pageCount")));
					}
					if((Double)one.get("finishedPageCount") != null) {
						one.put("finishedPageCount", df.format(one.get("finishedPageCount")));
					}
					String key1 = one.get("planShootDate") + "|" + one.get("planShootGroup");
					List<Map<String, Object>> viewList = new ArrayList<Map<String,Object>>();
					for(Map<String, Object> map : viewInfoList) {
						String key2 = map.get("planShootDate") + "|" + map.get("planGroupName");
						if(key1.equals(key2)) {
							viewList.add(map);
						}
					}
					if(one.get("planShootGroup") != null) {
						if(((String) one.get("planShootGroup")).equals("单组")) {
							one.put("planShootGroup", "");
						}
					}
					one.put("viewInfoList", viewList);
				}
			}			
			
			Map<String, String> columnKeyMap = new LinkedHashMap<String, String>();
//	        columnKeyMap.put("计划日期", "planShootDate");
//	        columnKeyMap.put("计划组别", "planGroupName");
	        if(crewInfo.getCrewType() == CrewType.Movie.getValue() 
	        		|| crewInfo.getCrewType() == CrewType.InternetMovie.getValue()) {
		        columnKeyMap.put("场", "viewNo");
	        } else {
		        columnKeyMap.put("集", "seriesNo");
		        columnKeyMap.put("场", "viewNo");
	        }
	        columnKeyMap.put("特殊提醒", "specialRemind");
	        columnKeyMap.put("气氛", "atmosphereName");
	        columnKeyMap.put("内外景", "site");
//	        columnKeyMap.put("拍摄地点", "shootLocation");
	        columnKeyMap.put("拍摄地点", "shootLocationRegion");
	        columnKeyMap.put("主场景", "majorView");
	        columnKeyMap.put("次场景", "minorView");
	        columnKeyMap.put("三级场景", "thirdLevelView");
	        columnKeyMap.put("主要内容", "mainContent");
	        columnKeyMap.put("页数", "pageCount");
	        for (Map<String, Object> map : majorRoleList) {
	        	columnKeyMap.put(map.get("viewRoleName") + "", map.get("viewRoleId") + "");
	        }
	        columnKeyMap.put("特约演员", "guestRoleList");
	        columnKeyMap.put("群众演员", "massRoleList");
	        columnKeyMap.put("服装", "clothesName");
	        columnKeyMap.put("化妆", "makeupName");
	        columnKeyMap.put("道具", "propsList");
	        columnKeyMap.put("特殊道具", "specialPropsList");
	        columnKeyMap.put("备注", "remark");
	        columnKeyMap.put("商植", "advertName");
	        columnKeyMap.put("拍摄时间", "shootDate");
	        columnKeyMap.put("拍摄状态", "shootStatus");
	        
	        //调用方法导出表格数据
    		ExcelUtils.exportScheduleDetailForExcel(response, crewInfo, dateList, columnKeyMap);
    		this.sysLogService.saveSysLog(request, "导出计划详情", terminal, ScheduleViewMapModel.TABLE_NAME, null, SysLogOperType.EXPORT.getValue());
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			logger.error(message, ie);
		}catch (Exception e) {
			message = "未知异常,导出计划详情失败";
			success = false;
			logger.error(message, e);
    		this.sysLogService.saveSysLog(request, "导出计划详情失败：" + e.getMessage(), terminal, ScheduleViewMapModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		resultMap.put("message", message);
		resultMap.put("success", success);		
		return resultMap;
	}
	
	/**
	 * 导入计划表
	 * @param request
	 * @param file
	 * @param isDelete 是否删除旧计划
	 * @param isCover 是否覆盖重复数据
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/importSchedule")
	public Map<String, Object> importSchedule(HttpServletRequest request, MultipartFile file, boolean isDelete, boolean isCover) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		try {
			if (file == null) {
				throw new IllegalArgumentException("请选择上传的文件！");
			}
			String crewId = this.getCrewId(request);
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			// 上传文件到服务器
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String baseStorePath = properties.getProperty("fileupload.path");
			String modelStorePath = baseStorePath + "import/schedule";
			String newName = "《"+ crewInfo.getCrewName() +"》" + sdf2.format(new Date()) + "计划";
			Map<String, String> fileMap = FileUtils.uploadFileForExcel(request, modelStorePath, newName);
			if (fileMap == null) {
				throw new IllegalArgumentException("请选择文件");
			}
			
			String fileStoreName = fileMap.get("fileStoreName");// 新文件名
			String storePath = fileMap.get("storePath");// 服务器存文件路径
			//读取excel表的数据
			Map<String, Object> scheduleInfoMap = ExcelUtils.readScheduleInfo(storePath + fileStoreName);
			
			List<ScheduleGroupModel> scheduleGroupList = null;
			List<ScheduleViewMapModel> scheduleViewMapList = null; 
			
			String errorMessage = "";
			//取出excel读取的数据
			Set<String> sheetSet = scheduleInfoMap.keySet();
			Iterator<String> sheetKeys = sheetSet.iterator();
			
			//拍摄分组
			Set<String> shootGroupSet = new HashSet<String>();
			shootGroupSet.add("单组");
			shootGroupSet.add("A组");
			shootGroupSet.add("B组");
			shootGroupSet.add("C组");
			shootGroupSet.add("D组");
			shootGroupSet.add("E组");
			shootGroupSet.add("F组");
			shootGroupSet.add("G组");
			shootGroupSet.add("H组");
			shootGroupSet.add("I组");
			shootGroupSet.add("J组");
			shootGroupSet.add("K组");
			shootGroupSet.add("L组");
			shootGroupSet.add("M组");
			shootGroupSet.add("N组");
			shootGroupSet.add("O组");
			shootGroupSet.add("P组");
			shootGroupSet.add("Q组");
			shootGroupSet.add("R组");
			shootGroupSet.add("S组");
			shootGroupSet.add("T组");
			shootGroupSet.add("U组");
			shootGroupSet.add("V组");
			shootGroupSet.add("W组");
			shootGroupSet.add("X组");
			shootGroupSet.add("Y组");
			shootGroupSet.add("Z组");
			
			while(sheetKeys.hasNext()){
				String sheetKey = sheetKeys.next();
				//excel读取的数据
				List<ArrayList<String>> excelDataList = (List<ArrayList<String>>) scheduleInfoMap.get(sheetKey);
				//为空或者只有一行（标题）则不保存
				if(excelDataList==null||excelDataList.size()<3){
					continue;
				}
				
				scheduleGroupList = new ArrayList<ScheduleGroupModel>();
				scheduleViewMapList = new ArrayList<ScheduleViewMapModel>();
				int sequence = 1;
				ScheduleGroupModel scheduleGroup = null;
				ScheduleViewMapModel scheduleViewpMap = null;
				for (int i = 0; i < excelDataList.size(); i++) {
					List<String> line = excelDataList.get(i);
					if (i == 0 || i == 1) {
						if(i == 1) {
							if(crewInfo.getCrewType() == CrewType.Movie.getValue()) {
								if(!line.get(2).equals("场")) {
									throw new IllegalArgumentException("表格列名有误");
								}
							} else {
								if(!line.get(2).equals("集") || !line.get(3).equals("场")) {
									throw new IllegalArgumentException("表格列名有误");
								}
							}
						}
						continue;
					}
					ViewInfoModel viewInfo = null;
					Set<String> flag = new HashSet<String>();
					for(String one : line) {
						flag.add(one);
					}
					if(flag.size() != 1) {
						if(crewInfo.getCrewType() == CrewType.Movie.getValue()) {
							String viewNo = line.get(2);
							if(StringUtils.isNotBlank(viewNo)) {
								viewInfo = this.viewInfoService.queryOneByCrewIdAndSeriaViewNo(crewId, null, viewNo);
								if(viewInfo == null) {
									errorMessage += "场景信息不存在；";
								}
							}
						} else {
							String seriesNo = line.get(2);
							String viewNo = line.get(3);
							if(StringUtils.isBlank(seriesNo)) {
								if(StringUtils.isNotBlank(viewNo)) {
									errorMessage += "集为空；";
								}
							} else {
								if(StringUtils.isBlank(viewNo)) {
									errorMessage += "场为空；";
								} else {
									if(seriesNo.contains(".")) {
										seriesNo = seriesNo.substring(0, seriesNo.indexOf("."));
									}
									viewInfo = this.viewInfoService.queryOneByCrewIdAndSeriaViewNo(crewId, Integer.parseInt(seriesNo), viewNo);
									if(viewInfo == null) {
										errorMessage += "场景信息不存在；";
									}
								}
							}
						}
					}
					if(viewInfo == null) {//计划分组
						String groupName = line.get(0);
						
						if(!groupName.equals("未分组")) {							
							scheduleGroup = new ScheduleGroupModel();
							scheduleGroup.setId(UUIDUtils.getId());
							scheduleGroup.setGroupName(groupName);
							scheduleGroup.setSequence(scheduleGroupList.size() + 1);
							scheduleGroup.setCrewId(crewId);
							scheduleGroup.setCreateTime(new Date());
							scheduleGroupList.add(scheduleGroup);
							
							sequence = 1;
						} else {
							scheduleGroup = null;
						}
					} else {//计划分组与场景关联关系
						scheduleViewpMap = new ScheduleViewMapModel();
						scheduleViewpMap.setId(UUIDUtils.getId());
						scheduleViewpMap.setIsLock(0);
						if(scheduleGroup != null) {
							scheduleViewpMap.setPlanGroupId(scheduleGroup.getId());
						} else {
							scheduleViewpMap.setPlanGroupId(null);
						}
						scheduleViewpMap.setSequence(sequence++);
						scheduleViewpMap.setViewId(viewInfo.getViewId());
						scheduleViewpMap.setCrewId(crewId);
						String planShootDateStr = line.get(0);
						Date planShootDate = null;
						if(StringUtil.isNotBlank(planShootDateStr)) {
							try {
								planShootDate = sdf.parse(planShootDateStr);
							} catch (Exception e) {
								errorMessage += "计划日期格式不正确";
							}
						}
						scheduleViewpMap.setShootDate(planShootDate);
						String planShootGroup = line.get(1);
						if(StringUtils.isNotBlank(planShootGroup)) {
							if(!shootGroupSet.contains(planShootGroup)) {
								if(planShootGroup.equals("单组") || planShootGroup.equals("不分组") || planShootGroup.equals("待定")) {
									planShootGroup = "单组";
								} else {
									planShootGroup = "";
									errorMessage += "计划组别填写不规范，请修改；";
								}
							}
						} else {
							planShootGroup = "单组";
						}
						if(StringUtils.isNotBlank(planShootGroup)) {
							ShootGroupModel shootGroup = shootGroupService.queryOneByGroupName(planShootGroup);
							if(shootGroup == null) {
								shootGroup = new ShootGroupModel();
								shootGroup.setGroupId(UUIDUtils.getId());
								shootGroup.setGroupName(planShootGroup);
								shootGroup.setCrewId(crewId);
								shootGroup.setCreateTime(new Date());
								shootGroupService.saveShootGroup(shootGroup);
							}
							scheduleViewpMap.setShootGroupId(shootGroup.getGroupId());
						} else {
							scheduleViewpMap.setShootGroupId(null);
						}
						scheduleViewMapList.add(scheduleViewpMap);
					}
					if (StringUtils.isNotBlank(errorMessage)) {
						throw new IllegalArgumentException(" 第"+ (i+1) + "行 " + errorMessage);
					}
				}
				this.scheduleService.saveAllSchedule(crewId, scheduleGroupList, scheduleViewMapList, isDelete, isCover);
			}
			
    		this.sysLogService.saveSysLog(request, "导入计划", terminal, ScheduleViewMapModel.TABLE_NAME, null, SysLogOperType.IMPORT.getValue());
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			logger.error(message, ie);
		}catch (Exception e) {
			message = "未知异常,导入计划失败";
			success = false;
			logger.error(message, e);
    		this.sysLogService.saveSysLog(request, "导入计划失败：" + e.getMessage(), terminal, ScheduleViewMapModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		resultMap.put("message", message);
		resultMap.put("success", success);		
		return resultMap;
	}
}
