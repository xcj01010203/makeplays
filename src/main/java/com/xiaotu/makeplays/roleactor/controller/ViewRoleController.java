package com.xiaotu.makeplays.roleactor.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.model.constants.CrewType;
import com.xiaotu.makeplays.roleactor.controller.dto.ViewStatGrpByShootLocationDto;
import com.xiaotu.makeplays.roleactor.controller.dto.ViewStatGrpByViewLocationDto;
import com.xiaotu.makeplays.roleactor.controller.filter.ViewRoleFilter;
import com.xiaotu.makeplays.roleactor.model.ActorLeaveRecordModel;
import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;
import com.xiaotu.makeplays.roleactor.service.ActorLeaveRecordService;
import com.xiaotu.makeplays.roleactor.service.ViewRoleService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.DateUtils;
import com.xiaotu.makeplays.utils.ExcelUtils;
import com.xiaotu.makeplays.utils.ExportExcelUtil;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.StringUtils;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.view.controller.ViewInfoController;
import com.xiaotu.makeplays.view.controller.filter.ViewFilter;
import com.xiaotu.makeplays.view.model.ViewRoleAndActorModel;
import com.xiaotu.makeplays.view.model.ViewRoleMapModel;
import com.xiaotu.makeplays.view.model.constants.LocationType;
import com.xiaotu.makeplays.view.model.constants.ShootStatus;
import com.xiaotu.makeplays.view.model.constants.ViewType;
import com.xiaotu.makeplays.view.service.ViewInfoService;

/**
 * 场景角色信息
 * @author xuchangjian 2016-7-12下午2:24:13
 */
@Controller
@RequestMapping("/viewRole")
public class ViewRoleController extends BaseController{
	private static Map<String, String> ROLE_MAP = new LinkedHashMap<String, String>();//需要导出的联系人字段
    static{
    	ROLE_MAP.put("角色名称", "viewRoleName");
    	ROLE_MAP.put("简称",  "shortName");
    	ROLE_MAP.put("演员类型",  "viewRoleType");
    	ROLE_MAP.put("演员姓名",  "actorName");
    	ROLE_MAP.put("首次出场",  "firstSeriesViewNo");
    	ROLE_MAP.put("场",  "totalViewCount");
    	ROLE_MAP.put("页",  "totalPageCount");
    	ROLE_MAP.put("完成/总场数", "finishRate");
    	ROLE_MAP.put("入组时间",  "enterDate");
    	ROLE_MAP.put("离组时间",  "leaveDate");
    	ROLE_MAP.put("在组天数",  "shootDays");
    	ROLE_MAP.put("工作时长",  "workHours");
    	ROLE_MAP.put("休息时长",  "restHours");
    	ROLE_MAP.put("请假记录",  "leaveInfo");
    	/*ROLE_MAP.put("请假次数",  "leaveCount");
    	ROLE_MAP.put("请假天数",  "totalLeaveDays");*/
    }
	Logger logger = LoggerFactory.getLogger(ViewInfoController.class);
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	
	private final int terminal = Constants.TERMINAL_PC;
	
	@Autowired
	private ViewRoleService viewRoleService;
	
	@Autowired
	private ActorLeaveRecordService actorLeaveRecordService;
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	/**
	 * 跳转到角色管理页面
	 * @param request
	 * @return
	 */
	@RequestMapping("/toViewRolePage")
	public ModelAndView toViewRolePage(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("/viewrole/viewRole");
		
		return mv;
	}
	
	
	/**
	 * 跳转到角色场景列表界面
	 * @param request
	 * @param roles
	 * @return
	 */
	@RequestMapping("/toRoleViewListPage")
	public ModelAndView toRoleViewListPage(HttpServletRequest request, String roles, String viewRoleName) {
		ModelAndView mv = new ModelAndView("/viewrole/roleViewList");
		if (StringUtils.isNotBlank(roles) && StringUtils.isNotBlank(viewRoleName)) {
			mv.addObject("roleId", roles);
			mv.addObject("viewRoleName", viewRoleName);
		}
		return mv;
	}
	
	/**
	 * 查询剧组中角色列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryViewRoleList")
	public Map<String, Object> queryViewRoleList(HttpServletRequest request, ViewRoleFilter viewRoleFilter) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		//主演数量
		int mainCount = 0;
		//特约数量
		int guestCount = 0;
		//群演数量
		int massCount = 0;
		//待定演员
		int otherCount = 0;
		try {
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			//剧组id
			String crewId = crewInfo.getCrewId();
			
			//剧组类型
			Integer crewType = crewInfo.getCrewType();
			
			List<Map<String, Object>> viewRoleList = this.viewRoleService.queryViewRoleList(crewId, viewRoleFilter);
			
			for (Map<String, Object> map : viewRoleList) {
				if (map.get("enterDate") != null) {
					Date enterDate = (Date) map.get("enterDate");
					map.put("enterDate", this.sdf1.format(enterDate));
				}
				if (map.get("leaveDate") != null) {
					Date leaveDate = (Date) map.get("leaveDate");
					map.put("leaveDate", this.sdf1.format(leaveDate));
				}
				
				//拼接角色出现的第一场
				String firstSeriesViewNo = (String) map.get("seriesViewNo");
				if (StringUtils.isNotBlank(firstSeriesViewNo)) {
					String[] strings = firstSeriesViewNo.split(",");
					firstSeriesViewNo = strings[0];
					//根据不同的剧组类型返回不同集场号字段
					//如果为网剧、网大 只返回场次号
					if (crewType == CrewType.Movie.getValue() || crewType == CrewType.InternetMovie.getValue()) {
						//将首次出场拆分，只返回场次号
						firstSeriesViewNo = firstSeriesViewNo.substring(firstSeriesViewNo.lastIndexOf("-")+1, firstSeriesViewNo.length());
					}
				}else {
					firstSeriesViewNo = "";
				}
				
				map.put("seriesViewNo", firstSeriesViewNo);
				
				//计算每个角色的数量
				//取出当前角色的类型
				int viewRoleType = (Integer)map.get("viewRoleType");
				if (viewRoleType == 1) { //主演
					mainCount ++;
				}else if (viewRoleType == 2) { //特约
					guestCount ++;
				}else if (viewRoleType == 3) { //群演
					massCount ++;
				}else { //待定演员
					otherCount ++;
				}
			}
			
			resultMap.put("mainCount", mainCount);
			resultMap.put("guestCount", guestCount);
			resultMap.put("massCount", massCount);
			resultMap.put("otherCount", otherCount);
			resultMap.put("totalCount", mainCount + guestCount + massCount + otherCount);
			resultMap.put("viewRoleList", viewRoleList);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch(Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询演员请假记录列表
	 * @param request
	 * @param actorId	演员ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryActorOffRecordList")
	public Map<String, Object> queryActorOffRecordList(HttpServletRequest request, String actorId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			
			if (StringUtils.isBlank(actorId)) {
				throw new IllegalArgumentException("请提供演员信息");
			}
			
			String crewId = this.getCrewId(request);
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("crewId", crewId);
			conditionMap.put("actorId", actorId);
			
			List<ActorLeaveRecordModel> leaveRecordList = this.actorLeaveRecordService.queryManyByMutiCondition(conditionMap, null);
			
			List<Map<String, Object>> recordMapList = new ArrayList<Map<String, Object>>();
			for (ActorLeaveRecordModel record : leaveRecordList) {
				Map<String, Object> recordMap = new HashMap<String, Object>();
				recordMap.put("id", record.getId());
				recordMap.put("actorId", record.getActorId());
				if (record.getLeaveStartDate() != null) {
					recordMap.put("leaveStartDate", this.sdf1.format(record.getLeaveStartDate()));
				}
				if (record.getLeaveEndDate() != null) {
					recordMap.put("leaveEndDate", this.sdf1.format(record.getLeaveEndDate()));
				}
				recordMap.put("leaveDays", record.getLeaveDays());
				recordMap.put("leaveReason", record.getLeaveReason());
				
				recordMapList.add(recordMap);
			}
			
			resultMap.put("leaveRecordList", recordMapList);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch(Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 保存演员请假记录
	 * @param request
	 * @param actorId	演员ID
	 * @param leaveStartDate	请假开始时间
	 * @param leaveEndDate	请假结束时间
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveActorLeaveRecord")
	public Map<String, Object> saveActorLeaveRecord (HttpServletRequest request, String actorId, String leaveStartDate, String leaveEndDate) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			//校验
			if (StringUtils.isBlank(actorId)) {
				throw new IllegalArgumentException("请提供演员信息");
			}
			if (StringUtils.isBlank(leaveStartDate)) {
				throw new IllegalArgumentException("请提供请假开始时间");
			}
			if (StringUtils.isBlank(leaveEndDate)) {
				throw new IllegalArgumentException("请提供请假结束时间");
			}
			
			String crewId = getCrewId(request);
			
			Date myLeaveStartDate = this.sdf1.parse(leaveStartDate);
			Date myLeaveEndDate = this.sdf1.parse(leaveEndDate);
			if (myLeaveStartDate.after(myLeaveEndDate)) {
				throw new IllegalArgumentException("结束时间不能早于开始时间");
			}
			
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("crewId", crewId);
			conditionMap.put("actorId", actorId);
			conditionMap.put("leaveStartDate", leaveStartDate);
			conditionMap.put("leaveEndDate", leaveEndDate);
			List<ActorLeaveRecordModel> existRecord = this.actorLeaveRecordService.queryManyByMutiCondition(conditionMap, null);
			if (existRecord != null && existRecord.size() > 0) {
				throw new IllegalArgumentException("已存在相同日期的请假记录");
			}
			
			List<ActorLeaveRecordModel> recordList = this.actorLeaveRecordService.queryExistDateRecord(actorId, myLeaveStartDate, myLeaveEndDate);
			if (recordList != null && recordList.size() > 0) {
				throw new IllegalArgumentException("请假日期跟其他请假记录的有重叠，请检查");
			}
			
			//保存数据
			ActorLeaveRecordModel actorLeaveRecordModel = new ActorLeaveRecordModel();
			actorLeaveRecordModel.setId(UUIDUtils.getId());
			actorLeaveRecordModel.setCrewId(crewId);
			actorLeaveRecordModel.setActorId(actorId);
			actorLeaveRecordModel.setLeaveStartDate(myLeaveStartDate);
			actorLeaveRecordModel.setLeaveEndDate(myLeaveEndDate);
			actorLeaveRecordModel.setLeaveDays(DateUtils.daysBetween(myLeaveStartDate, myLeaveEndDate) + 1);
			actorLeaveRecordModel.setCreateTime(new Date());
			
			this.actorLeaveRecordService.addOne(actorLeaveRecordModel);
			
			this.sysLogService.saveSysLog(request, "保存演员请假记录", terminal, ActorLeaveRecordModel.TABLE_NAME, actorId, 1);
		} catch (ParseException pe) {
			success = false;
			message = "日期格式错误";
			
			logger.error("日期格式错误", pe);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch(Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "保存演员请假记录失败：" + e.getMessage(), terminal, ActorLeaveRecordModel.TABLE_NAME, actorId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 删除请假记录
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteActorLeaveRecord")
	public Map<String, Object> deleteActorLeaveRecord(HttpServletRequest request, String id) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			//校验
			if (StringUtils.isBlank(id)) {
				throw new IllegalArgumentException("请提供记录ID");
			}
			this.actorLeaveRecordService.deleteOne(id);
			
			this.sysLogService.saveSysLog(request, "删除演员请假记录", terminal, ActorLeaveRecordModel.TABLE_NAME, id, 3);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch(Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "删除演员请假记录失败：" + e.getMessage(), terminal, ActorLeaveRecordModel.TABLE_NAME, id, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询角色戏量统计
	 * @param request
	 * @param viewRoleId	角色ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryViewCountStatistic")
	public Map<String, Object> queryViewCountStatistic(HttpServletRequest request, String viewRoleId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			//校验
			/*if (StringUtils.isBlank(viewRoleId)) {
				throw new IllegalArgumentException("请提供角色信息");
			}*/
			
			String crewId = this.getCrewId(request);
			
			//角色的场景信息列表
			List<Map<String, Object>> roleViewList = this.viewInfoService.queryRoleViewList(crewId, viewRoleId);
			
			//总的统计 角色名/演员名/总场数/总页数/场景数/（文戏、武戏、文武戏）（场数、页数）/（内景、外景、内外景）（场数、页数）
			Map<String, Object> generalStatistic = this.genGeneralStatistic(roleViewList);
			
			//分场景统计   拍摄地/场数/页数    场景/场数/页数
			List<ViewStatGrpByShootLocationDto> viewStatGrpByShootLocationList = this.genStatByLocation(roleViewList);
			
			//分集统计 集次/场数/页数
			Map<String, Object> serieStatInfo = this.genStatisBySeriesNo(crewId, roleViewList);
			
			resultMap.put("generalStatistic", generalStatistic);
			resultMap.put("viewStatGrpByShootLocationList", viewStatGrpByShootLocationList);
			resultMap.put("serieStatInfo", serieStatInfo);
			
			this.sysLogService.saveSysLog(request, "角色戏量统计", terminal, ViewRoleModel.TABLE_NAME + "," + ViewRoleMapModel.TABLE_NAME, viewRoleId, 0);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch(Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "角色戏量统计失败：" + e.getMessage(), terminal, ViewRoleModel.TABLE_NAME + "," + ViewRoleMapModel.TABLE_NAME, viewRoleId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 生成综合的统计信息
	 * @param roleViewList
	 * @return	总的统计： 角色名/演员名/总场数/总页数/场景数/（文戏、武戏、文武戏）（场数、页数）
	 */
	private Map<String, Object> genGeneralStatistic(List<Map<String, Object>> roleViewList) {
		Map<String, Object> result = new HashMap<String, Object>();
		String viewRoleName = "";
		String actorName = "";
		
		if (roleViewList.size()>0) {
			viewRoleName = (String) roleViewList.get(0).get("viewRoleName");
			actorName = roleViewList.get(0).get("actorName") == null ? "" : (String) roleViewList.get(0).get("actorName");
		}
		
		
		//角色场景信息
		int totalViewCount = 0;	//总场数
		int finishedTotalViewCount = 0; //已完成总场数
		double totalPageCount = 0;	//总页数
		double finishedTotalPageCount = 0; //已完成总页数
		int viewLocationCount = 0;	//主场景数
		
		int insideViewCount = 0;	//内戏场数
		int finishedInsideViewCount = 0; //已完成内戏场数
		double insidePageCount = 0.0;	//内戏页数
		double finishedInsidePageCount = 0.0; //已完成内戏页数
		int outsideViewCount = 0;	//外戏场数，含有“外”的都是外戏，其他都是内戏
		int finishedOutsideViewCount = 0;	//外戏场数，含有“外”的都是外戏，其他都是内戏
		double outsidePageCount = 0.0;	//外戏页数
		double finishedOutsidePageCount = 0.0;	//外戏页数
		int dayViewCount = 0;	//日戏场数
		int finishedDayViewCount = 0;	//日戏场数
		double dayPageCount = 0.0;	//日戏页数
		double finishedDayPageCount = 0.0;	//日戏页数
		int nightViewCount = 0;	//夜戏场数，含有“夜”的都是夜戏，其他都是日戏
		int finishedNightViewCount = 0;	//夜戏场数，含有“夜”的都是夜戏，其他都是日戏
		double nightPageCount = 0.0;	//夜戏页数
		double finishedNightPageCount = 0.0;	//夜戏页数
		int literateViewCount = 0;	//文戏场数
		int finishedLiterateViewCount = 0;	//文戏场数
		double literatePageCount = 0.0;	//文戏页数
		double finishedLiteratePageCount = 0.0;	//文戏页数
		int kungFuViewCount = 0;	//武戏场数，“文武戏”属于武戏
		int finishedKungFuViewCount = 0;	//武戏场数，“文武戏”属于武戏
		double kungFuPageCount = 0.0;	//武戏页数
		double finishedKungFuPageCount = 0.0;	//武戏页数
		
		
		List<String> locationList = new ArrayList<String>();
		for (Map<String, Object> roleViewInfo : roleViewList) {
			if (roleViewInfo.get("viewId") == null) {
				continue;
			}
			
			int shootStatus = (Integer) roleViewInfo.get("shootStatus");
			double pageCount = (Double) roleViewInfo.get("pageCount");
			String location = (String) roleViewInfo.get("location");
			String site = (String) roleViewInfo.get("site");
			String atmosphereName = (String) roleViewInfo.get("atmosphereName");
			Integer viewType = (Integer) roleViewInfo.get("viewType");
			
			if (shootStatus == ShootStatus.DeleteXi.getValue()) {
				continue;
			}
			
			totalViewCount++;
			totalPageCount = BigDecimalUtil.add(totalPageCount, pageCount);
			
			if(shootStatus == ShootStatus.Finished.getValue()) {
				finishedTotalViewCount++;
				finishedTotalPageCount = BigDecimalUtil.add(finishedTotalPageCount, pageCount);
			}
			
			//主场景
			if (!StringUtils.isBlank(location) && !locationList.contains(location)) {
				viewLocationCount++;
				locationList.add(location);
			}
			
			
			//内外景统计
			if (StringUtils.isBlank(site) || site.indexOf("外") == -1) {
				insideViewCount++;
				insidePageCount = BigDecimalUtil.add(insidePageCount, pageCount);
				
				if(shootStatus == ShootStatus.Finished.getValue()) {
					finishedInsideViewCount++;
					finishedInsidePageCount = BigDecimalUtil.add(finishedInsidePageCount, pageCount);
				}
			} else {
				outsideViewCount++;
				outsidePageCount = BigDecimalUtil.add(outsidePageCount, pageCount);
				
				if(shootStatus == ShootStatus.Finished.getValue()) {
					finishedOutsideViewCount++;
					finishedOutsidePageCount = BigDecimalUtil.add(finishedOutsidePageCount, pageCount);
				}
			}
			
			//气氛统计
			if (StringUtils.isBlank(atmosphereName) || atmosphereName.indexOf("夜") == -1) {
				dayViewCount++;
				dayPageCount = BigDecimalUtil.add(dayPageCount, pageCount);
				
				if(shootStatus == ShootStatus.Finished.getValue()) {
					finishedDayViewCount++;
					finishedDayPageCount = BigDecimalUtil.add(finishedDayPageCount, pageCount);
				}
			} else {
				nightViewCount++;
				nightPageCount = BigDecimalUtil.add(nightPageCount, pageCount);
				
				if(shootStatus == ShootStatus.Finished.getValue()) {
					finishedNightViewCount++;
					finishedNightPageCount = BigDecimalUtil.add(finishedNightPageCount, pageCount);
				}
			}
			
			//文武戏统计
			if (viewType == null || viewType == ViewType.TeXiao.getValue()) {
				literateViewCount++;
				literatePageCount = BigDecimalUtil.add(literatePageCount, pageCount);
				
				if(shootStatus == ShootStatus.Finished.getValue()) {
					finishedLiterateViewCount++;
					finishedLiteratePageCount = BigDecimalUtil.add(finishedLiteratePageCount, pageCount);
				}
			} else {
				kungFuViewCount++;
				kungFuPageCount = BigDecimalUtil.add(kungFuPageCount, pageCount);
				
				if(shootStatus == ShootStatus.Finished.getValue()) {
					finishedKungFuViewCount++;
					finishedKungFuPageCount = BigDecimalUtil.add(finishedKungFuPageCount, pageCount);
				}
			}
		}
		
		result.put("viewRoleName", viewRoleName);
		result.put("actorName", actorName);
		
		result.put("totalViewCount", totalViewCount);
		result.put("totalPageCount", totalPageCount);
		result.put("viewLocationCount", viewLocationCount);

		result.put("insideViewCount", insideViewCount);
		result.put("insidePageCount", insidePageCount);
		result.put("outsideViewCount", outsideViewCount);
		result.put("outsidePageCount", outsidePageCount);
		result.put("dayViewCount", dayViewCount);
		result.put("dayPageCount", dayPageCount);
		result.put("nightViewCount", nightViewCount);
		result.put("nightPageCount", nightPageCount);
		result.put("literateViewCount", literateViewCount);
		result.put("literatePageCount", literatePageCount);
		result.put("kungFuViewCount", kungFuViewCount);
		result.put("kungFuPageCount", kungFuPageCount);
		//已完成
		result.put("finishedTotalViewCount", finishedTotalViewCount);
		result.put("finishedTotalPageCount", finishedTotalPageCount);
		result.put("finishedInsideViewCount", finishedInsideViewCount);
		result.put("finishedInsidePageCount", finishedInsidePageCount);
		result.put("finishedOutsideViewCount", finishedOutsideViewCount);
		result.put("finishedOutsidePageCount", finishedOutsidePageCount);
		result.put("finishedDayViewCount", finishedDayViewCount);
		result.put("finishedDayPageCount", finishedDayPageCount);
		result.put("finishedNightViewCount", finishedNightViewCount);
		result.put("finishedNightPageCount", finishedNightPageCount);
		result.put("finishedLiterateViewCount", finishedLiterateViewCount);
		result.put("finishedLiteratePageCount", finishedLiteratePageCount);
		result.put("finishedKungFuViewCount", finishedKungFuViewCount);
		result.put("finishedKungFuPageCount", finishedKungFuPageCount);
		
		return result;
	}
	
	/**
	 * 按照拍摄场景生成统计信息
	 * @param roleViewList
	 * @return	 拍摄地/场数/页数    场景/场数/页数
	 */
	private List<ViewStatGrpByShootLocationDto> genStatByLocation(List<Map<String, Object>> roleViewList) {
		List<ViewStatGrpByShootLocationDto> result = new ArrayList<ViewStatGrpByShootLocationDto>();
		
		//按照拍摄地点分组
		Map<String, List<Map<String, Object>>> shootLocationGroupMap = new HashMap<String, List<Map<String, Object>>>();
		
		for (Map<String, Object> map : roleViewList) {
			if (map.get("viewId") == null) {
				continue;
			}
			
			String shootLocationId = (String) map.get("shootLocationId");	//拍摄地点ID
			int shootStatus = (Integer) map.get("shootStatus");
			
			//删戏的数据不加入统计
			if (shootStatus != ShootStatus.DeleteXi.getValue()) {
				if (StringUtils.isBlank(shootLocationId)) {
					shootLocationId = "0";
				}
				
				if (!shootLocationGroupMap.containsKey(shootLocationId)) {
					List<Map<String, Object>> singlLocationViewList = new ArrayList<Map<String, Object>>();
					singlLocationViewList.add(map);
					shootLocationGroupMap.put(shootLocationId, singlLocationViewList);
				} else {
					shootLocationGroupMap.get(shootLocationId).add(map);
				}
			}
		}
		
		
		//遍历按照拍摄地点分组后的数据，把数据封装成ViewStatGrpByShootLocationDto的格式
		Set<String> shootLocationSet = shootLocationGroupMap.keySet();
		for (String shootLocationId : shootLocationSet) {
			String shootLocation = "";	//拍摄地点名称
			int viewCount = 0;	//该拍摄地点的总场数
			double totalPageCount = 0;	//该拍摄地点的总页数
			int finishedViewCount = 0; //该拍摄地点已完成总场数
			double finishedPageCount = 0;	//该拍摄地点已完成总页数
			List<String> atmosphereList = new ArrayList<String>(); //气氛
			List<String> siteList = new ArrayList<String>(); //内外景
			
			List<Map<String, Object>> mapList = shootLocationGroupMap.get(shootLocationId);	//获取拍摄地点下的所有场景信息
			Map<String, List<Map<String, Object>>> locationGroupMap = new HashMap<String, List<Map<String,Object>>>();	//按照拍摄场景分组后的数据
			
			//把拍摄地点下的所有场景按照拍摄场景分组
			for (Map<String, Object> map : mapList) {
				String locationId = (String) map.get("locationId");	//拍摄场景ID
				
				if (shootLocationId.equals("0")) {
					shootLocation = "待定";
				} else {
					shootLocation = (String) map.get("shootLocation");
				}
				double pageCount = (Double) map.get("pageCount");
				
				if (StringUtils.isBlank(locationId)) {
					locationId = "0";
				}
				if (!locationGroupMap.containsKey(locationId)) {
					List<Map<String, Object>> locatonGroupList = new ArrayList<Map<String, Object>>();
					locatonGroupList.add(map);
					
					locationGroupMap.put(locationId, locatonGroupList);
				} else {
					locationGroupMap.get(locationId).add(map);
				}
				
				viewCount++;
				totalPageCount = BigDecimalUtil.add(totalPageCount, pageCount);
				//已完成
				int shootStatus = (Integer) map.get("shootStatus"); //拍摄状态。0:未完成；1:部分完成；2:完成；3:删戏；
				if(shootStatus == 2) {
					finishedViewCount++;
					finishedPageCount = BigDecimalUtil.add(finishedPageCount, pageCount);
				}
				//气氛、内外景
				String atmosphereName = (String) map.get("atmosphereName");
				String site = (String) map.get("site");
				if(StringUtils.isNotBlank(atmosphereName) && !atmosphereList.contains(atmosphereName)) {
					atmosphereList.add(atmosphereName);
				}
				if(StringUtils.isNotBlank(site) && !siteList.contains(site)) {
					siteList.add(site);
				}
			}
			
			
			//遍历分组后的拍摄场景，把数据封装成ViewStatGrpByViewLocationDto的格式
			Set<String> locationSet = locationGroupMap.keySet();
			List<ViewStatGrpByViewLocationDto> viewStatGrpByViewLocationList = new ArrayList<ViewStatGrpByViewLocationDto>();
			
			for (String locationId : locationSet) {
				String location = "";
				int locationViewCount = 0;
				double locationTotalPageCount = 0;
				int finishedLocationViewCount = 0;
				double finishedLocationPageCount = 0;
				List<String> locationAtmosphereList = new ArrayList<String>(); //气氛
				List<String> locationSiteList = new ArrayList<String>(); //内外景
				
				List<Map<String, Object>> locationGroupList = locationGroupMap.get(locationId);
				for (Map<String, Object> map : locationGroupList) {
					if (locationId.equals("0")) {
						location = "待定";
					} else {
						location = (String) map.get("location");
					}
					double pageCount = (Double) map.get("pageCount");
					
					locationViewCount ++;
					locationTotalPageCount = BigDecimalUtil.add(locationTotalPageCount, pageCount);
					
					//已完成
					int shootStatus = (Integer) map.get("shootStatus"); //拍摄状态。0:未完成；1:部分完成；2:完成；3:删戏；
					if(shootStatus == 2) {
						finishedLocationViewCount++;
						finishedLocationPageCount = BigDecimalUtil.add(finishedLocationPageCount, pageCount);
					}
					//气氛、内外景
					String atmosphereName = (String) map.get("atmosphereName");
					String site = (String) map.get("site");
					if(StringUtils.isNotBlank(atmosphereName) && !locationAtmosphereList.contains(atmosphereName)) {
						locationAtmosphereList.add(atmosphereName);
					}
					if(StringUtils.isNotBlank(site) && !locationSiteList.contains(site)) {
						locationSiteList.add(site);
					}
				}
				
				ViewStatGrpByViewLocationDto viewStatGrpByViewLocationDto = new ViewStatGrpByViewLocationDto();
				viewStatGrpByViewLocationDto.setLocationId(locationId);
				viewStatGrpByViewLocationDto.setLocation(location);
				viewStatGrpByViewLocationDto.setLocationType(LocationType.lvlOneLocation.getValue());
				viewStatGrpByViewLocationDto.setViewCount(locationViewCount);
				viewStatGrpByViewLocationDto.setTotalPageCount(locationTotalPageCount);
				viewStatGrpByViewLocationDto.setFinishedViewCount(finishedLocationViewCount);
				viewStatGrpByViewLocationDto.setFinishedPageCount(finishedLocationPageCount);
				Collections.sort(locationAtmosphereList, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return o1.compareTo(o2);
					}
				});
				String atmosphereStr = StringUtils.listToString(locationAtmosphereList, " ");
				if(siteList.size() > 0) {
					Collections.sort(locationSiteList, new Comparator<String>() {
						@Override
						public int compare(String o1, String o2) {
							return o1.compareTo(o2);
						}
					});
					atmosphereStr += " " + StringUtils.listToString(locationSiteList, " ");
				}
				viewStatGrpByViewLocationDto.setAtmosphere(atmosphereStr);
				
				viewStatGrpByViewLocationList.add(viewStatGrpByViewLocationDto);
			}
			
			//排序
			Collections.sort(viewStatGrpByViewLocationList, new Comparator<ViewStatGrpByViewLocationDto>() {
				@Override
				public int compare(ViewStatGrpByViewLocationDto o1, ViewStatGrpByViewLocationDto o2) {
					int o1ViewCount = o1.getViewCount();
					int o2ViewCount = o2.getViewCount();
	        		return o2ViewCount - o1ViewCount;
				}
			});
			
			ViewStatGrpByShootLocationDto viewStatGrpByShootLocationDto = new ViewStatGrpByShootLocationDto();
			viewStatGrpByShootLocationDto.setShootLocationId(shootLocationId);
			viewStatGrpByShootLocationDto.setShootLocation(shootLocation);
			viewStatGrpByShootLocationDto.setViewCount(viewCount);
			viewStatGrpByShootLocationDto.setTotalPageCount(totalPageCount);
			viewStatGrpByShootLocationDto.setFinishedViewCount(finishedViewCount);
			viewStatGrpByShootLocationDto.setFinishedPageCount(finishedPageCount);
			viewStatGrpByShootLocationDto.setViewLocationStatList(viewStatGrpByViewLocationList);
			
			Collections.sort(atmosphereList, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}
			});
			String atmosphereStr = StringUtils.listToString(atmosphereList, " ");
			if(siteList.size() > 0) {
				Collections.sort(siteList, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return o1.compareTo(o2);
					}
				});
				atmosphereStr += " " + StringUtils.listToString(siteList, " ");
			}
			viewStatGrpByShootLocationDto.setAtmosphere(atmosphereStr);
			
			result.add(viewStatGrpByShootLocationDto);
		}
		
		//排序
		Collections.sort(result, new Comparator<ViewStatGrpByShootLocationDto>() {
			@Override
			public int compare(ViewStatGrpByShootLocationDto o1, ViewStatGrpByShootLocationDto o2) {
				int o1ViewCount = o1.getViewCount();
				int o2ViewCount = o2.getViewCount();
        		return o2ViewCount - o1ViewCount;
			}
		});
		
		return result;
	}
	
	/**
	 * 按照场景集次生成统计信息
	 * @param crewId
	 * @param roleViewList
	 * @return
	 */
	private Map<String, Object> genStatisBySeriesNo(String crewId, List<Map<String, Object>> roleViewList) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		//查询所有集次
		List<Map<String, Object>> seriesNoMapList = this.viewInfoService.querySeriesNoByCrewId(crewId);
		
		Map<Integer, Integer> seriesViewCountMap = new HashMap<Integer, Integer>();
		Map<Integer, Double> seriesPageCountMap = new HashMap<Integer, Double>();
		for (Map<String, Object> map : seriesNoMapList) {
			int seriesNo = (Integer) map.get("seriesNo");
			if (!seriesViewCountMap.containsKey(seriesNo)) {
				seriesViewCountMap.put(seriesNo, 0);
				seriesPageCountMap.put(seriesNo, 0.0);
			}
		}
		
		//角色在每集中拥有的场次
		List<Integer> roleSeiresNoList = new ArrayList<Integer>();
		for (Map<String, Object> map : roleViewList) {
			if (map.get("viewId") == null) {
				continue;
			}
			
			int seriesNo = (Integer) map.get("seriesNo");
			double pageCount = (Double) map.get("pageCount");
			int shootStatus = (Integer) map.get("shootStatus");
			
			int viewCount = (Integer) seriesViewCountMap.get(seriesNo);
			double totalPageCount = seriesPageCountMap.get(seriesNo);
			
			if (shootStatus != ShootStatus.DeleteXi.getValue()) {
				seriesViewCountMap.put(seriesNo, ++viewCount);
				seriesPageCountMap.put(seriesNo, BigDecimalUtil.add(totalPageCount, pageCount));
			}
			
			if (!roleSeiresNoList.contains(seriesNo)) {
				roleSeiresNoList.add(seriesNo);
			}
			
		}
		
		result.put("seriesViewCountMap", seriesViewCountMap);
		result.put("seriesPageCountMap", seriesPageCountMap);
		result.put("totalSeriesNum", seriesNoMapList.size());
		result.put("roleSeriesNum", roleSeiresNoList.size());
		
		return result;
	}
	
	/**
	 * 保存场景角色信息
	 * 带有饰演该角色的演员信息设置
	 * @param request
	 * @param viewRoleId	角色ID
	 * @param viewRoleName	角色名称
	 * @param shortName	角色简称
	 * @param viewRoleType	角色类型
	 * @param actorId 演员ID
	 * @param actorName	演员名称
	 * @param enterDate	演员入组日期
	 * @param leaveDate	演员离组日期
	 * @param shootDays 演员在组天数
	 * @param isAttentionRole 是否是关注角色
	 * @param workHours 工作天数
	 * @param restHours 休息天数
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveViewRoleInfo")
	public Map<String, Object> saveViewRoleInfo(HttpServletRequest request,
			String viewRoleId, String viewRoleName, String shortName,
			Integer viewRoleType, String actorId, String actorName, String enterDate,
			String leaveDate, Integer shootDays, Boolean isAttentionRole, String workHours, String restHours) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			
			if (StringUtils.isBlank(viewRoleName)) {
				throw new IllegalArgumentException("请填写角色名称");
			}
			if (viewRoleName.length() > 50) {
				throw new IllegalArgumentException("角色名称过长，请检查");
			}
			if (viewRoleType == null) {
				throw new IllegalArgumentException("请填写角色类型");
			}
			if (!StringUtils.isBlank(shortName) && shortName.length() > 20) {
				throw new IllegalArgumentException("角色简称过长，请检查");
			}
			if (!StringUtils.isBlank(actorName) && actorName.length() > 50) {
				throw new IllegalArgumentException("演员姓名过长，请检查");
			}
			List<ViewRoleModel> viewRoleList = this.viewRoleService.queryByViewRoleNameExpOne(crewId, viewRoleId, viewRoleName);
			//判断角色是否重复
			if (viewRoleList != null && viewRoleList.size() > 0) {
				throw new IllegalArgumentException("角色名和其他角色重复，请检查");
			}
			
			//校验入组离组日期
			if (!StringUtils.isBlank(enterDate) && !StringUtils.isBlank(leaveDate)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date enterDateFormat = sdf.parse(enterDate);
				Date leaveDateFormat = sdf.parse(leaveDate);
				if (enterDateFormat.after(leaveDateFormat)) {
					throw new IllegalArgumentException("离组日期不能早于入组日期，请检查");
				}
			}
			
			resultMap = this.viewRoleService.saveRoleWithActorInfo(crewId, viewRoleId, viewRoleName, shortName, viewRoleType, 
					actorId, actorName, enterDate, leaveDate, shootDays, isAttentionRole, workHours, restHours);
			
			
			
			Integer operType = null;
			if(StringUtils.isBlank(viewRoleId)) {
				operType = 1;
			} else {
				operType = 2;
			}
			this.sysLogService.saveSysLog(request, "保存场景角色信息", terminal, ViewRoleModel.TABLE_NAME, null, operType);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";

			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "保存场景角色信息失败：" + e.getMessage(), terminal, ViewRoleModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 删除场景角色信息
	 * @param request
	 * @param viewRoleId	角色ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteViewRoleInfo")
	public Map<String, Object> deleteViewRoleInfo(HttpServletRequest request, String viewRoleId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(viewRoleId)) {
				throw new IllegalArgumentException("请提供角色ID");
			}
			
			this.viewRoleService.deleteViewRoleInfo(viewRoleId);

		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";

			logger.error("未知异常", e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 批量删除角色
	 * @param request
	 * @param viewRoleIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteViewRoleInfoBatch")
	public Map<String, Object> deleteViewRoleInfoBatch(HttpServletRequest request, String viewRoleIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(viewRoleIds)) {
				throw new IllegalArgumentException("请提供角色ID");
			}
			
			this.viewRoleService.deleteViewRoleInfoBatch(viewRoleIds);
			
			this.sysLogService.saveSysLog(request, "删除场景角色", terminal, ViewRoleModel.TABLE_NAME, viewRoleIds, 3);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";

			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "删除场景角色失败：" + e.getMessage(), terminal, ViewRoleModel.TABLE_NAME, viewRoleIds, SysLogOperType.ERROR.getValue());
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 导出角色场景表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exportRoleViewList")
	public Map<String, Object> exportRoleViewList(HttpServletRequest request, String viewRoleIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(viewRoleIds)) {
				throw new IllegalArgumentException("请选择角色");
			}
			
			Properties property = PropertiesUitls.fetchProperties("/config.properties");
			
			//根据不同的剧组类型（电影/电视剧），选择不同的导出模板
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			Integer crewType = crewInfo.getCrewType();
			String crewId = crewInfo.getCrewId();
			
			String srcfilePath = "";
			if (crewType == CrewType.Movie.getValue()) {
				srcfilePath = property.getProperty("movie_viewTemplate");
			} else {
				srcfilePath = property.getProperty("tvplay_viewTemplate");
			}
			
			//下载的文件存放的路径
			File pathFile = new File(property.getProperty("downloadPath"));
			if(!pathFile.isDirectory()){
				pathFile.mkdirs();
			}
			
			//查询各个角色的对应的场景，然后进行数据封装
			List<File> roleViewFileList = new ArrayList<File>();
			String[] viewRoleIdArray = viewRoleIds.split(",");
			
			//剧组中所有主要演员信息
			List<ViewRoleAndActorModel> roleSignList = this.viewInfoService.queryViewRoleSign(crewId);
			
			for (String viewRoleId : viewRoleIdArray) {
				
				//生成该角色对应的场景数据
				Map<String, Object> exportData = this.genExportViewData(roleSignList, crewId, viewRoleId);
				
				//获取角色名称
				ViewRoleModel viewRole = this.viewRoleService.queryViewRoleInfoById(viewRoleId);
				
				//获取模板文件地址配置
				String downloadPath = property.getProperty("downloadPath") 
						+ "/场景表(" + viewRole.getViewRoleName() + ")"
						+ new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
				
				ExportExcelUtil.exportViewToExcelTemplate(srcfilePath, exportData, downloadPath);
				File singleRoleViewFile = new File(downloadPath);
				roleViewFileList.add(singleRoleViewFile);
			}
			
			
			//压缩文件
			long sct = System.currentTimeMillis();
			String zipfilepath = property.getProperty("downloadPath") + "/角色场景表" + sct+".zip";
			File zipfile = new File(zipfilepath);
			
	        zipFiles(roleViewFileList, zipfile);
	        
	        resultMap.put("downloadFilePath", zipfilepath);
	        this.sysLogService.saveSysLog(request, "导出角色场景表", terminal, ViewRoleModel.TABLE_NAME, null, 5);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";

			logger.error("未知异常", e);
	        this.sysLogService.saveSysLog(request, "导出角色场景表失败：" + e.getMessage(), terminal, ViewRoleModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}

	/**
	 * 生成角色对应的场景数据
	 * @param roleSignList	剧组中所有主要演员信息
	 * @param crewId	剧组
	 * @param viewRoleId	角色ID
	 * @return
	 */
	private Map<String, Object> genExportViewData(List<ViewRoleAndActorModel> roleSignList, String crewId, String viewRoleId) {
		//查询该角色拥有的所有场景
		ViewFilter filter = new ViewFilter();
		filter.setRoles(viewRoleId);
		List<Map<String, Object>> viewInfoList = this.viewInfoService.queryViewInfoList(crewId, null, filter);
		
		//格式化场景数据
		if(viewInfoList == null){
			viewInfoList = new ArrayList<Map<String, Object>>();
		}
		
		//内外景
		Map<Integer, String> siteMap = new HashMap<Integer, String>();
		siteMap.put(1, "内景");
		siteMap.put(2, "外景");
		siteMap.put(3, "内外景");

		//拍摄状态
		Map<Integer, String> shootStatusMap = new HashMap<Integer, String>();
		shootStatusMap.put(0, "未完成");
		shootStatusMap.put(1, "部分完成");
		shootStatusMap.put(2, "完成");
		shootStatusMap.put(3, "删戏");
		
		//遍历查询的场景列表
		for(Map<String, Object> viewInfo : viewInfoList){
			
			viewInfo.put("atmosphere", viewInfo.get("atmosphereName"));
			
			if(viewInfo.get("shootStatus") != null){
				viewInfo.put("shootStatus", shootStatusMap.get((Integer)viewInfo.get("shootStatus")));
			}
			
			//场景下的角色
			List<Map<String, Object>> roleList = (List<Map<String, Object>>) viewInfo.get("roleList");
			List<ViewRoleModel> newRoleList = new ArrayList<ViewRoleModel>();
			//循环所有角色
			for(int i = 0; i < roleSignList.size(); i++){
				ViewRoleModel role = roleSignList.get(i);
				
				boolean hasRoleFlag = false;	//标识当前场景的演员在所有主要演员中是否存在
				for(Map<String, Object> roleMap: roleList) {
					if(roleMap.get("viewRoleId").equals(role.getViewRoleId())){
						if(StringUtils.isBlank(role.getShortName())){
							role.setShortName("√");
						}
						newRoleList.add(role);
						hasRoleFlag = true;
						break;
					}
				}
				
				//如果不存在就添加一个空的对象，保证在表格中显示列正确
				if (!hasRoleFlag) {
					newRoleList.add(new ViewRoleModel());
				}
			}
			viewInfo.put("roleList",newRoleList);
		}
		
		Map<String, Object> exportData = new HashMap<String, Object>();
		exportData.put("resultList", viewInfoList);
		exportData.put("roleSignList", roleSignList);
		return exportData;
	}
	
	/** 
     * 压缩文件 
     * @param srcfile File[] 需要压缩的文件列表 
     * @param zipfile File 压缩后的文件 
     */  
    private void zipFiles(List<File> srcfile, File zipfile) {
        byte[] buf = new byte[1024];  
        try {  
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));  
            for (int i = 0; i < srcfile.size(); i++) {  
                File file = srcfile.get(i);  
                FileInputStream in = new FileInputStream(file);  
                out.putNextEntry(new ZipEntry(file.getName()));  
                int len;  
                while ((len = in.read(buf)) > 0) {  
                    out.write(buf, 0, len);  
                }  
                out.closeEntry();  
                in.close();  
            }  
            out.close();  
        } catch (IOException e) {  
        	logger.error("未知异常", e);  
        }  
    }
	
	/**
	 * 批量设置角色类型
	 * @param request
	 * @param viewRoleIds 角色Id，多个用逗号隔开
	 * @param viewRoleType	角色类型
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateViewRoleTypeBatch")
	public Map<String, Object> updateViewRoleTypeBatch(HttpServletRequest request, String viewRoleIds, Integer viewRoleType) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(viewRoleIds)) {
				throw new IllegalArgumentException("请选择场景角色");
			}
			if (viewRoleType == null) {
				throw new IllegalArgumentException("请选择角色类型");
			}
			
			this.viewRoleService.updateViewRoleTypeBatch(viewRoleIds, viewRoleType);

			this.sysLogService.saveSysLog(request, "修改角色类型", terminal, ViewRoleModel.TABLE_NAME, viewRoleIds, 2);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";

			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "修改角色类型失败：" + e.getMessage(), terminal, ViewRoleModel.TABLE_NAME, viewRoleIds, SysLogOperType.ERROR.getValue());
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 合并角色
	 * @param request
	 * @param viewRoleIds	待合并的角色ID，多个值用逗号隔开
	 * @param viewRoleName	新角色名称
	 * @param shortName	新角色的简称
	 * @param viewRoleType	新角色的类型
	 * @param actorName	演员姓名
	 * @param enterDate	入组时间
	 * @param leaveDate	离组时间
	 * @param shootDays 在组天数
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/makeRolesToOne")
	public Map<String, Object> makeRolesToOne(HttpServletRequest request,
			String viewRoleIds, String viewRoleName, String shortName,
			Integer viewRoleType, String actorName, String enterDate,
			String leaveDate, Integer shootDays) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(viewRoleName)) {
				throw new IllegalArgumentException("请填写角色名称");
			}
			if (viewRoleType == null) {
				throw new IllegalArgumentException("请填写角色类型");
			}
			
			String crewId = this.getCrewId(request);
			List<ViewRoleModel> viewRoleList = this.viewRoleService.queryByViewRoleNameExpMany(crewId, viewRoleIds, viewRoleName);
			//判断角色是否重复
			if (viewRoleList != null && viewRoleList.size() > 0) {
				throw new IllegalArgumentException("角色名和其他角色重复，请检查");
			}
			
			
			this.viewRoleService.makeRolesToOne(crewId, viewRoleIds, viewRoleName, shortName, viewRoleType, actorName, enterDate, leaveDate, shootDays);
			
			this.sysLogService.saveSysLog(request, "统一角色名称", terminal, ViewRoleModel.TABLE_NAME, viewRoleIds, 2);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";

			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "统一角色名称失败：" + e.getMessage(), terminal, ViewRoleModel.TABLE_NAME, viewRoleIds, SysLogOperType.ERROR.getValue());
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询剧组中角色列表 导出角色表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryViewRoleListForExport")
	public Object queryViewRoleListForExport(HttpServletRequest request, HttpServletResponse response,ViewRoleFilter viewRoleFilter) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			CrewInfoModel crewInfoModel = this.getSessionCrewInfo(request);
			String crewId = crewInfoModel.getCrewId();
			String crewName = crewInfoModel.getCrewName();
			Integer crewType = crewInfoModel.getCrewType();
			
			List<Map<String, Object>> viewRoleList = this.viewRoleService.queryViewRoleList(crewId, viewRoleFilter);
			
			for (Map<String, Object> map : viewRoleList) {
				if (map.get("enterDate") != null) {
					Date enterDate = (Date) map.get("enterDate");
					map.put("enterDate", this.sdf1.format(enterDate));
				}
				if (map.get("leaveDate") != null) {
					Date leaveDate = (Date) map.get("leaveDate");
					map.put("leaveDate", this.sdf1.format(leaveDate));
				}
				
				//请假记录
				String leaveInfo = "";
				int leaveCount = map.get("leaveCount")!=null?Integer.parseInt(map.get("leaveCount").toString()):0;
				if(leaveCount!=0){
					int totalLeaveDays = map.get("totalLeaveDays")!=null?Integer.parseInt(map.get("totalLeaveDays").toString()):0;
					leaveInfo = "请假："+leaveCount+"次/共："+totalLeaveDays+"天";
					
				}
				map.put("leaveInfo", leaveInfo);
				String viewRoleType = map.get("viewRoleType")!=null?map.get("viewRoleType").toString():"";
				//1：主要演员；2：特约演员；3：群众演员';4:待定
				if("1".equals(viewRoleType)){
					viewRoleType = "主要演员";
				}else if("2".equals(viewRoleType)){
					viewRoleType = "特约演员";
				}else if("3".equals(viewRoleType)){
					viewRoleType = "群众演员";
				}else if("4".equals(viewRoleType)){
					viewRoleType = "待定";
				}else {
					viewRoleType = "";
				}
				map.put("viewRoleType", viewRoleType);
				
				//拼接完成场数/总场数
				int finishedViewCount = map.get("finishedViewCount")!=null?Integer.parseInt(map.get("finishedViewCount").toString()):0;
				int totalViewCount = map.get("totalViewCount")!=null?Integer.parseInt(map.get("totalViewCount").toString()):0;
				
				map.put("finishRate", finishedViewCount +"/" + totalViewCount);
				
				//拼接角色出现的第一场
				String firstSeriesViewNo = (String) map.get("seriesViewNo");
				if (StringUtils.isNotBlank(firstSeriesViewNo)) {
					String[] strings = firstSeriesViewNo.split(",");
					firstSeriesViewNo = strings[0];
					//根据不同的剧组类型返回不同集场号字段
					//如果为网剧、网大 只返回场次号
					if (crewType == CrewType.Movie.getValue() || crewType == CrewType.InternetMovie.getValue()) {
						//将首次出场拆分，只返回场次号
						firstSeriesViewNo = firstSeriesViewNo.substring(firstSeriesViewNo.lastIndexOf("-")+1, firstSeriesViewNo.length());
					}
				}else {
					firstSeriesViewNo = "";
				}
				
				map.put("firstSeriesViewNo", firstSeriesViewNo);
				
			}
			ExcelUtils.exportRoleInfoForExcel(viewRoleList,response,ROLE_MAP,crewName);
			
			this.sysLogService.saveSysLog(request, "角色表导出", terminal, ViewRoleModel.TABLE_NAME, null, 5);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch(Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "角色表导出失败：" + e.getMessage(), terminal, ViewRoleModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 更新角色表排序字段
	 * @param request
	 * @param viewRoleIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateViewRoleSequence")
	public Map<String, Object> updateViewRoleSequence(HttpServletRequest request, String viewRoleIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(viewRoleIds)) {
				throw new IllegalArgumentException("角色ID不能为空");
			}
			
			String[] viewRoleIdArray = viewRoleIds.split(",");
			this.viewRoleService.updateViewRoleSequence(viewRoleIdArray);
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch(Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 设置或取消关注角色
	 * @param request
	 * @param viewRoleIds
	 * @param isAttentionRole
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateViewRoleAttention")
	public Map<String, Object> setOrCancleViewRoleAttention(HttpServletRequest request, String viewRoleIds, Boolean isAttentionRole){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message  ="";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			
			if (StringUtils.isBlank(viewRoleIds)) {
				throw new IllegalArgumentException("请选择需要设置的角色");
			}
			if (isAttentionRole == null) {
				isAttentionRole = false;
			}
			
			//如果是关注角色需要判断关注角色数量是否超过6个
			if (isAttentionRole) { 
			
				//判断关注角色是否超过6个
				List<Map<String,Object>> roleList = this.viewRoleService.queryAttentionRoleList(crewId);
				if (roleList != null && roleList.size() > 5) {
						throw new IllegalArgumentException("关注角色最多只能设置6个，请修改");
				}
			}
			
			this.viewRoleService.updateViewRoleAttentionBatch(viewRoleIds, isAttentionRole);
			
			message = "设置成功！";
			this.sysLogService.saveSysLog(request, "设置关注角色", terminal, ViewRoleModel.TABLE_NAME, null, 2);
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，设置关注角色失败";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "设置关注角色失败：" + e.getMessage(), terminal, ViewRoleModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
}
