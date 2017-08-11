package com.xiaotu.makeplays.statistic.view;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.notice.model.NoticeInfoModel;
import com.xiaotu.makeplays.notice.service.NoticeService;
import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;
import com.xiaotu.makeplays.roleactor.model.constants.ViewRoleType;
import com.xiaotu.makeplays.roleactor.service.ViewRoleService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.DateUtils;
import com.xiaotu.makeplays.utils.ExcelUtils;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.utils.StringUtils;
import com.xiaotu.makeplays.view.controller.dto.ViewFilterDto;
import com.xiaotu.makeplays.view.controller.filter.ViewFilter;
import com.xiaotu.makeplays.view.model.ViewInfoModel;
import com.xiaotu.makeplays.view.model.ViewLocationModel;
import com.xiaotu.makeplays.view.model.ViewRoleMapModel;
import com.xiaotu.makeplays.view.model.constants.LocationType;
import com.xiaotu.makeplays.view.service.ViewInfoService;
import com.xiaotu.makeplays.view.service.ViewLocationService;

/**
 * 场景统计信息代码
 * @author xuchangjian 2016-10-9上午9:45:49
 */
@Controller
@RequestMapping("/viewStatisticManager")
public class ViewStatisticController extends BaseController{

	Logger logger = LoggerFactory.getLogger(ViewStatisticController.class);
	
	private DecimalFormat df = new DecimalFormat("0.00");
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	@Autowired
	private ViewRoleService viewRoleService;
	
	@Autowired
	private ViewLocationService viewLocationService;
	
	@Autowired
	private NoticeService noticeService;
	
	/**
	 * 跳转到日拍摄量统计页面
	 * @param crewId
	 * @return
	 */
	@RequestMapping("/appIndex/toDayShootStatisticPage")
	public ModelAndView toDayShootStatisticPage(String crewId) {
		ModelAndView mv = new ModelAndView("/statistic/dayShootStatistic");
		mv.addObject("crewId", crewId);
		return mv;
	}
	
	/**
	 * 查询日拍摄量情况
	 * @param crewId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/appIndex/queryDayShootInfo")
	public Map<String, Object> queryDayShootInfo(String crewId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			//按日
        	List<Map<String, Object>> shootedViewStatistic = this.noticeService.queryDayProduction(crewId);
        	
        	//按周
        	List<Map<String, Object>> weekShootedViewStatistic = new ArrayList<Map<String,Object>>();
        	Map<String, Map<String, Object>> weekShootedViewMap = new HashMap<String, Map<String,Object>>();
        	for (Map<String, Object> shootedViewMap : shootedViewStatistic) {
        		Date noticeDate = (Date) shootedViewMap.get("noticeDate");
        		String noticeDateStr = this.sdf.format(noticeDate);
        		shootedViewMap.put("noticeDate", noticeDateStr);
        		
        		String year = (String) shootedViewMap.get("year");
        		String week = (String) shootedViewMap.get("week");
        		String groupName = (String) shootedViewMap.get("groupName");
        		String key = year + "-" + week + groupName;
        		Map<String, Object> weekMap = null;
        		if(!weekShootedViewMap.containsKey(key)) {
        			weekMap = new HashMap<String, Object>();
        			
        			//获取一周开始日期、结束日期
        			String[] dates = DateUtils.getDayOfWeek(Integer.parseInt(year), Integer.parseInt(week));

        			weekMap.put("year", year);
        			weekMap.put("week", week);
        			weekMap.put("startDate", dates[0]);
        			weekMap.put("endDate", dates[1]);
        			weekMap.put("groupName", groupName);
        			weekMap.put("viewCount", 0);
        			weekMap.put("pageCount", 0);
        			weekShootedViewStatistic.add(weekMap);
        			weekShootedViewMap.put(key, weekMap);
        		} else {
        			weekMap = weekShootedViewMap.get(key);
        		}
        		weekMap.put("viewCount", Integer.parseInt(weekMap.get("viewCount") + "") 
        				+ Integer.parseInt(shootedViewMap.get("viewCount") + ""));
        		weekMap.put("pageCount", Double.parseDouble(weekMap.get("pageCount") + "") 
        				+ Double.parseDouble(shootedViewMap.get("pageCount") + ""));
        	}
        	
        	resultMap.put("shootedViewStatistic", shootedViewStatistic);
        	resultMap.put("weekShootedViewStatistic", weekShootedViewStatistic);
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
	 * 跳转到场景汇总统计页面
	 * @param crewId
	 * @return
	 */
	@RequestMapping("/toViewLocationStatisticPage")
	public ModelAndView toViewLocationStatisticPage() {
		ModelAndView mv = new ModelAndView("/statistic/viewLocationStatistic");
		return mv;
	}
	
	/**
	 * 查询主要角色
	 * @param request
	 * @param shootLocation 查询条件：拍摄地点
	 * @param location 查询条件：主场景
	 * @param crewRole 查询条件：角色
	 * @param flag 1:显示，2：不显示
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryMajorActor")
	public Map<String, Object> queryMajorActor(
			HttpServletRequest request, ViewFilter filter) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			//查询主要角色列表
			resultMap.put("majorRoleList", this.viewRoleService
					.queryManyByIdAndTypeExcludeSome(crewId,
							ViewRoleType.MajorActor.getValue(), filter));
        } catch (Exception e) {
			success = false;
			message = "未知异常,查询主要角色失败";

			logger.error(message, e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 场景汇总
	 * @param request
	 * @param filter 查询条件
	 * @param sortField 排序字段
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryViewLocationStatistic")
	public Map<String, Object> queryViewLocationStatistic(
			HttpServletRequest request, ViewFilter filter, String sortField) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			//查询主要角色列表
			resultMap.put("majorRoleList", this.viewRoleService.queryManyByIdAndIsAttentionRole(crewId));
			
			List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
			List<Map<String, Object>> locationStatisticList = this.viewLocationService.queryViewLocationStatistic(crewId, 
					LocationType.lvlOneLocation.getValue(),filter, sortField);
			
			Set<String> shootLocationSet = new HashSet<String>();
			Set<String> majorLocationSet = new HashSet<String>();
			Map<String, Map<String, Object>> locationMap = new HashMap<String, Map<String, Object>>();
			int viewNum = 0;
			int finishedViewNum = 0;
			double pageNum = 0;
			double finishedPageNum = 0;
			int finishedMajorLocationNum = 0; //已完成场景数
			int partFinishedMajorLocationNum = 0; //部分完成场景数
			int notStartedMajorLocationNum = 0; //未开始场景数
			if(locationStatisticList != null) {
				//计算拍摄地点对应的行数
				Map<String, Object> firstMap = null;
				int lineNum = 0;
				String shootLocation = "-1";
				for(int i = 0; i < locationStatisticList.size(); i++) {
					Map<String, Object> map = locationStatisticList.get(i);
					String shootLocationId = map.get("shootLocationId") + "";
					if(!shootLocationId.equals(shootLocation)) {
						if(i != 0) {
							firstMap.put("lineNum", lineNum);
							lineNum = 0;
						}
						firstMap = map;
						shootLocation = shootLocationId;
					}
					lineNum++;
					if(i == locationStatisticList.size() - 1) {
						firstMap.put("lineNum", lineNum);
					}
					
					//计算合计信息
					if(StringUtil.isNotBlank(shootLocationId)) {
						shootLocationSet.add(shootLocationId);
					}
					String locationId = map.get("locationId") + "";
					if(StringUtil.isNotBlank(locationId)) {
						majorLocationSet.add(locationId);
						
						Map<String, Object> oneLocation = null;
						if(locationMap.containsKey(locationId)) {
							oneLocation = locationMap.get(locationId);
							oneLocation.put("viewNum", Integer.parseInt(oneLocation.get("viewNum") + "") + Integer.parseInt(map.get("viewNum") + ""));
							oneLocation.put("finishedViewNum", Integer.parseInt(oneLocation.get("finishedViewNum") + "") + Integer.parseInt(map.get("finishedViewNum") + ""));
						} else {
							oneLocation = new HashMap<String, Object>();
							oneLocation.put("viewNum", map.get("viewNum"));
							oneLocation.put("finishedViewNum", map.get("finishedViewNum"));
							locationMap.put(locationId, oneLocation);
						}
					}
					viewNum += Integer.parseInt(map.get("viewNum") + "");
					finishedViewNum += Integer.parseInt(map.get("finishedViewNum") + "");
					pageNum += Double.parseDouble(map.get("pageNum") + "");
					finishedPageNum += Double.parseDouble(map.get("finishedPageNum") + "");					

					map.put("pageNum", df.format(map.get("pageNum")));
					map.put("finishedPageNum", df.format(map.get("finishedPageNum")));
				}
				resultList = locationStatisticList;
				
				if(!locationMap.isEmpty()) {
					Iterator<String> it = locationMap.keySet().iterator();
					while(it.hasNext()) {
						Map<String, Object> oneLocation = locationMap.get(it.next());
						if(Integer.parseInt(oneLocation.get("viewNum") + "") 
								== Integer.parseInt(oneLocation.get("finishedViewNum") + "")) {
							finishedMajorLocationNum++;
						} else if(Integer.parseInt(oneLocation.get("finishedViewNum") + "") == 0) {
							notStartedMajorLocationNum++;
						} else {
							partFinishedMajorLocationNum++;
						}
					}
				}
			}
			Map<String, Object> totalMap = new HashMap<String, Object>();
			totalMap.put("shootLocationNum", shootLocationSet.size());
			totalMap.put("majorLocationNum", majorLocationSet.size());
			totalMap.put("finishedMajorLocationNum", finishedMajorLocationNum);
			totalMap.put("notStartedMajorLocationNum", notStartedMajorLocationNum);
			totalMap.put("partFinishedMajorLocationNum", partFinishedMajorLocationNum);
			totalMap.put("viewNum", viewNum);
			totalMap.put("finishedViewNum", finishedViewNum);
			totalMap.put("pageNum", df.format(pageNum));
			totalMap.put("finishedPageNum", df.format(finishedPageNum));
			//场景汇总信息
			resultMap.put("locationStatisticList", resultList);
			resultMap.put("totalMap", totalMap);
			
			this.sysLogService.saveSysLog(request, "查询场景汇总信息", Constants.TERMINAL_PC, 
					ViewLocationModel.TABLE_NAME + "," + ViewRoleMapModel.TABLE_NAME, null, 0);
        } catch (Exception e) {
			success = false;
			message = "未知异常,查询场景汇总失败";

			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "查询场景汇总信息失败：" + e.getMessage(), Constants.TERMINAL_PC, 
					ViewLocationModel.TABLE_NAME + "," + ViewRoleMapModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 加载高级查询数据
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/loadSearchCondition")
	public Map<String, Object> loadAdvanceSerachData(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";

		String crewId = getCrewId(request);
		try {
			ViewFilterDto viewFilterDto = this.viewInfoService.getFilterDtoForLocStat(crewId, false);
			resultMap.put("viewFilterDto", viewFilterDto);

			success = true;
			message = "查询成功";
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询高级查询条件失败";
			
			logger.error(message, e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 查询主场景对应的场景信息
	 * @param request
	 * @param filter 查询条件
	 * @param shootLocationId
	 * @param locationId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryViewListByMajorLocation")
	public Map<String, Object> queryViewListByMajorLocation(
			HttpServletRequest request, ViewFilter filter, String shootLocationId, String locationId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			
			List<Map<String, Object>> viewList = this.viewInfoService.queryViewListByMajorLocation(crewId, shootLocationId, locationId, filter);
			resultMap.put("result", viewList);
        } catch (Exception e) {
			success = false;
			message = "未知异常,查询主场景对应的场景信息失败";

			logger.error(message, e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 场景汇总信息导出
	 * 
	 * @param request
	 * @param shootLocation 查询条件：拍摄地点
	 * @param location 查询条件：主场景
	 * @param crewRole 查询条件：角色
	 * @param flag 1:显示，2：不显示
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/exportExcel")
	public Map<String, Object> exportExcel(HttpServletRequest request, ViewFilter filter, String sortField) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		DecimalFormat df1 = new DecimalFormat("0.0");
		
		try {
			String crewId = getCrewId(request);
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

			// 获取模板文件地址配置
			Properties property = PropertiesUitls.fetchProperties("/config.properties");
			String srcfilePath = property.getProperty("locatin_statisticTemplate");

			//生成下载文件路径
			String downloadPath = property.getProperty("downloadPath") + "location_statistic_" + System.currentTimeMillis() + ".xls";
			File pathFile = new File(property.getProperty("downloadPath"));
			if (!pathFile.isDirectory()) {
				pathFile.mkdirs();
			}

			//生成下载文件名
			String fileName = "《" + crewInfo.getCrewName() + "》场景汇总信息_"	+ sdf.format(new Date()) + ".xls";
			
			//查询主场景角色列表
			List<ViewRoleModel> majorRoleList = this.viewRoleService.queryManyByIdAndIsAttentionRole(crewId);
			//场景汇总信息
			List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
			List<Map<String, Object>> locationStatisticList = this.viewLocationService
					.queryViewLocationStatistic(crewId,
							LocationType.lvlOneLocation.getValue(),
							filter, sortField);
			
			//最多只读取230的角色信息
			if (majorRoleList != null && majorRoleList.size() > 240) {
				List<ViewRoleModel> newRoleSignList = new ArrayList<ViewRoleModel>();
				newRoleSignList.addAll(majorRoleList.subList(0, 229));
				majorRoleList = newRoleSignList;
			}
			List<String> majorRoleStrList = new ArrayList<String>();
			if(majorRoleList != null && majorRoleList.size() > 0) {
				for(ViewRoleModel viewRole : majorRoleList) {
					majorRoleStrList.add(viewRole.getViewRoleName());
				}
			}
			if (majorRoleList != null && majorRoleList.size() > 0
					&& locationStatisticList != null
					&& locationStatisticList.size() > 0) {
				for(Map<String, Object> one : locationStatisticList) {
					//拍摄地点
					if(StringUtils.isBlank(one.get("shootLocation") + "")) {
						one.put("shootLocation", "");
					}
					//主场景
					if(StringUtils.isBlank(one.get("location") + "")) {
						one.put("location", "");
					}
					//角色列表
					@SuppressWarnings("unchecked")
					List<Map<String, Object>> roleList = (List<Map<String, Object>>) one.get("roleList");
					List<String> newRoleList = new ArrayList<String>();
					for(ViewRoleModel viewRole : majorRoleList) {
						boolean hasRoleFlag = false; // 标识当前场景的演员在所有主要演员中是否存在
						if(roleList != null && roleList.size() > 0) {
							for(Map<String, Object> role : roleList) {
								if ((role.get("viewRoleId") + "").equals(viewRole.getViewRoleId())) {
									if (StringUtils.isBlank(viewRole.getShortName())) {
										viewRole.setShortName("√");
									}
									newRoleList.add(viewRole.getShortName() + "");
									hasRoleFlag = true;
									break;
								}
							}
						}

						// 如果不存在就添加一个空的对象，保证在表格中显示列正确
						if (!hasRoleFlag) {
							newRoleList.add("");
						}
					}
					one.put("roleList", newRoleList);
				}
			}
			Set<String> shootLocationSet = new HashSet<String>();
			Set<String> majorLocationSet = new HashSet<String>();
			int viewNum = 0;
			int finishedViewNum = 0;
			double pageNum = 0;
			double finishedPageNum = 0;
			if(locationStatisticList != null) {
				for(Map<String, Object> map : locationStatisticList) {
					//计算合计信息
					String shootLocationId = map.get("shootLocationId") + "";
					if(StringUtil.isNotBlank(shootLocationId)) {
						shootLocationSet.add(shootLocationId);
					}
					String locationId = map.get("locationId") + "";
					if(StringUtil.isNotBlank(locationId)) {
						majorLocationSet.add(locationId);
					}
					viewNum += Integer.parseInt(map.get("viewNum") + "");
					finishedViewNum += Integer.parseInt(map.get("finishedViewNum") + "");
					pageNum += Double.parseDouble(map.get("pageNum") + "");
					finishedPageNum += Double.parseDouble(map.get("finishedPageNum") + "");	
					map.put("pageNum", df1.format(map.get("pageNum")));
					map.put("finishedPageNum", df1.format(map.get("finishedPageNum")));
				}
				
				resultList = locationStatisticList;
			}
			Map<String, Object> totalMap = new HashMap<String, Object>();
			totalMap.put("shootLocationNum", shootLocationSet.size());
			totalMap.put("majorLocationNum", majorLocationSet.size());
			totalMap.put("viewNum", viewNum);
			totalMap.put("finishedViewNum", finishedViewNum);
			totalMap.put("pageNum", df1.format(pageNum));
			totalMap.put("finishedPageNum", df1.format(finishedPageNum));

			Map<String, Object> exportResultMap = new HashMap<String, Object>();
			exportResultMap.put("crewName", crewInfo.getCrewName());
			exportResultMap.put("resultList", resultList);
			exportResultMap.put("roleSignList", majorRoleStrList);
			//场景汇总信息
			exportResultMap.put("totalMap", totalMap);
			
			viewInfoService.exportViewToExcelTemplate(srcfilePath, exportResultMap,	downloadPath);
			
			resultMap.put("downloadPath", downloadPath);
			resultMap.put("fileName", fileName);
			message = "导出成功!";
			
			this.sysLogService.saveSysLog(request, "导出场景汇总信息", Constants.TERMINAL_PC, 
					ViewLocationModel.TABLE_NAME + "," + ViewRoleMapModel.TABLE_NAME, null, 5);
		} catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常, 导出场景汇总信息失败!";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "导出场景汇总信息失败：" + e.getMessage(), Constants.TERMINAL_PC, 
					ViewLocationModel.TABLE_NAME + "," + ViewRoleMapModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 导出主场景对应的场景信息
	 * @param request
	 * @param response
	 * @param filter 查询条件
	 * @param shootLocationId
	 * @param locationId
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/exportViewListByMajorLocation")
	public Map<String, Object> exportViewListByMajorLocation(HttpServletRequest request, 
			HttpServletResponse response, ViewFilter filter, String shootLocationId, String locationId, String locationName) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = getCrewId(request);
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);			
			List<Map<String, Object>> viewList = this.viewInfoService.queryViewListByMajorLocation(crewId, shootLocationId, locationId, filter);
			if(viewList != null && viewList.size() > 0) {
				
				for(Map<String, Object> one : viewList) {
					one.put("jichang", one.get("seriesNo") +"-" + one.get("viewNo"));
					one.put("specialRemind", one.get("specialRemind"));
					String atmosphereAndSite = "";
					if(StringUtil.isNotBlank(one.get("atmosphereName") + "")) {
						atmosphereAndSite += one.get("atmosphereName") + "";
					}
					if(StringUtil.isNotBlank(one.get("site") + "")) {
						if(StringUtil.isNotBlank(atmosphereAndSite)) {
							atmosphereAndSite += "/";
						}
						atmosphereAndSite += one.get("site");
					}
					one.put("atmosphereAndSite", atmosphereAndSite);
					String viewName = "";
					if(StringUtil.isNotBlank(one.get("majorView") + "")) {
						viewName += one.get("majorView") + "";
					}
					if(StringUtil.isNotBlank(one.get("minorView") + "")) {
						if(StringUtil.isNotBlank(viewName)) {
							viewName += " | ";
						}
						viewName += one.get("minorView") + "";
					}
					if(StringUtil.isNotBlank(one.get("thirdLevelView") + "")) {
						if(StringUtil.isNotBlank(viewName)) {
							viewName += " | ";
						}
						viewName += one.get("thirdLevelView") + "";
					}
					one.put("viewName", viewName);
					String otherRole = one.get("guestRoleList") + "";
					if(StringUtil.isNotBlank(one.get("massRoleList") + "")) {
						if(StringUtil.isNotBlank(otherRole)) {
							otherRole += "/";
						}
						otherRole += one.get("massRoleList");
					}
					one.put("otherRole", otherRole);
					String clothesMakeupProps = "";
					if(StringUtil.isNotBlank(one.get("makeupName") + "")) {
						clothesMakeupProps += one.get("makeupName") + "";
					}
					if(StringUtil.isNotBlank(one.get("clothesName") + "")) {
						if(StringUtil.isNotBlank(clothesMakeupProps)) {
							clothesMakeupProps += " | ";
						}
						clothesMakeupProps += one.get("clothesName") + "";
					}
					if(StringUtil.isNotBlank(one.get("propsList") + "")) {
						if(StringUtil.isNotBlank(clothesMakeupProps)) {
							clothesMakeupProps += " | ";
						}
						clothesMakeupProps += one.get("propsList") + "";
					}
					one.put("clothesMakeupProps", clothesMakeupProps);
				}
			}
			Map<String, String> viewListKeyMap = new LinkedHashMap<String, String>();
			viewListKeyMap.put("拍摄状态", "shootStatus");
			if(crewInfo.getCrewType() == 0 || crewInfo.getCrewType() == 3) {
				viewListKeyMap.put("场次", "viewNo");
			} else {
				viewListKeyMap.put("集-场", "jichang");
			}
	    	viewListKeyMap.put("特殊提醒",  "specialRemind");
	    	viewListKeyMap.put("气氛/内外",  "atmosphereAndSite");
	    	viewListKeyMap.put("页数",  "pageCount");
	    	viewListKeyMap.put("拍摄地点",  "shootLocation");
	    	viewListKeyMap.put("场景",  "viewName");
	    	viewListKeyMap.put("主要内容",  "mainContent");
	    	viewListKeyMap.put("主要演员",  "roleList");
	    	viewListKeyMap.put("特约/群演",  "otherRole");
	    	viewListKeyMap.put("服化道",  "clothesMakeupProps");
	    	viewListKeyMap.put("特殊道具",  "specialPropsList");
	    	viewListKeyMap.put("商植",  "advertName");
	    	viewListKeyMap.put("备注",  "viewRemark");
	    	
	    	ExcelUtils.exportViewListByMajorLocationForExcel(response, viewList, viewListKeyMap, crewInfo.getCrewName(), locationName);
			message = "导出成功!";
		} catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常, 导出主场景对应的场景信息失败!";
			success = false;
			
			logger.error(message, e);
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 跳转到分集汇总统计页面
	 * @param crewId
	 * @return
	 */
	@RequestMapping("/toSeriesnoTotalInfoPage")
	public ModelAndView toSeriesnoTotalInfoPage() {
		ModelAndView mv = new ModelAndView("/statistic/seriesnoTotalInfo");
		return mv;
	}	
	
	/**
	 * 分集汇总
	 * @param request
	 * @return 集数、场数	、页数、(夜外)场数、比重、页数、比重	(夜景)、场数、比重、页数、比重、(日景)场数、比重、页数、比重、其他
	 */
	@ResponseBody
	@RequestMapping("/querySeriesnoTotalInfo")
	public Map<String, Object> querySeriesnoTotalInfo(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			//剧本分集汇总
			List<Map<String, Object>> seriesnoList = this.viewInfoService.querySeriesnoTotalInfo(crewId);
			if(seriesnoList != null && seriesnoList.size() > 0) {
				//合计、平均
				Map<String, Object> totalaverage = this.viewInfoService.queryTotalAverageInfo(crewId);
				resultMap.put("totalaverage", totalaverage);
				/*if(totalaverage != null && !totalaverage.isEmpty()) {
					Map<String, Object> totalMap = new HashMap<String, Object>();
					totalMap.put("seriesNo", "合计");
					totalMap.put("viewNum", totalaverage.get("viewNum"));
					totalMap.put("pageNum", totalaverage.get("pageNum"));
					totalMap.put("nightoutView", totalaverage.get("nightoutView"));
					totalMap.put("noViewPer", totalaverage.get("noViewPer"));
					totalMap.put("nightoutPage", totalaverage.get("nightoutPage"));
					totalMap.put("noPagePer", totalaverage.get("noPagePer"));
					totalMap.put("nightView", totalaverage.get("nightView"));
					totalMap.put("nViewPer", totalaverage.get("nViewPer"));
					totalMap.put("nightPage", totalaverage.get("nightPage"));
					totalMap.put("nPagePer", totalaverage.get("nPagePer"));
					totalMap.put("dayoutView", totalaverage.get("dayoutView"));
					totalMap.put("doViewPer", totalaverage.get("doViewPer"));
					totalMap.put("dayoutPage", totalaverage.get("dayoutPage"));
					totalMap.put("doPagePer", totalaverage.get("doPagePer"));
					totalMap.put("dayView", totalaverage.get("dayView"));
					totalMap.put("dViewPer", totalaverage.get("dViewPer"));
					totalMap.put("dayPage", totalaverage.get("dayPage"));
					totalMap.put("dPagePer", totalaverage.get("dPagePer"));
					seriesnoList.add(totalMap);
					Map<String, Object> averageMap = new HashMap<String, Object>();
					averageMap.put("seriesNo", "平均");
					averageMap.put("viewNum", totalaverage.get("viewAvg"));
					averageMap.put("pageNum", totalaverage.get("pageAvg"));
					averageMap.put("nightoutView", totalaverage.get("nightoutViewAvg"));
					averageMap.put("noViewPer", totalaverage.get("noViewPer"));
					averageMap.put("nightoutPage", totalaverage.get("nightoutPageAvg"));
					averageMap.put("noPagePer", totalaverage.get("noPagePer"));
					averageMap.put("nightView", totalaverage.get("nightViewAvg"));
					averageMap.put("nViewPer", totalaverage.get("nViewPer"));
					averageMap.put("nightPage", totalaverage.get("nightPageAvg"));
					averageMap.put("nPagePer", totalaverage.get("nPagePer"));
					averageMap.put("dayoutView", totalaverage.get("dayoutViewAvg"));
					averageMap.put("doViewPer", totalaverage.get("doViewPer"));
					averageMap.put("dayoutPage", totalaverage.get("dayoutPageAvg"));
					averageMap.put("doPagePer", totalaverage.get("doPagePer"));
					averageMap.put("dayView", totalaverage.get("dayViewAvg"));
					averageMap.put("dViewPer", totalaverage.get("dViewPer"));
					averageMap.put("dayPage", totalaverage.get("dayPageAvg"));
					averageMap.put("dPagePer", totalaverage.get("dPagePer"));
					seriesnoList.add(averageMap);
				}*/
			}
			resultMap.put("seriesnoList", seriesnoList);
			
			this.sysLogService.saveSysLog(request, "查询分集汇总信息", Constants.TERMINAL_PC, ViewInfoModel.TABLE_NAME, null, 0);
        } catch (Exception e) {
			success = false;
			message = "未知异常,查询剧本分集汇总失败";

			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "查询分集汇总信息失败：" + e.getMessage(), Constants.TERMINAL_PC, ViewInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 剧本分集汇总信息导出
	 * 
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/exportSeriesnoTotalInfo")
	public Map<String, Object> exportSeriesnoTotalInfo(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = getCrewId(request);
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

			// 获取模板文件地址配置
			Properties property = PropertiesUitls.fetchProperties("/config.properties");
			String srcfilePath = property.getProperty("seriesno_statisticTemplate");

			//生成下载文件路径
			String downloadPath = property.getProperty("downloadPath") + "seriesno_statistic_" + System.currentTimeMillis() + ".xls";
			File pathFile = new File(property.getProperty("downloadPath"));
			if (!pathFile.isDirectory()) {
				pathFile.mkdirs();
			}

			//生成下载文件名
			String fileName = "《" + crewInfo.getCrewName() + "》剧本分集汇总信息_"	+ sdf.format(new Date()) + ".xls";
			
			//剧本分集汇总信息
			List<Map<String, Object>> seriesnoList = this.viewInfoService.querySeriesnoTotalInfo(crewId);
			Map<String, Object> totalaverage = this.viewInfoService.queryTotalAverageInfo(crewId);
			
			Map<String, Object> exportResultMap = new HashMap<String, Object>();
			exportResultMap.put("resultList", seriesnoList);
			if(seriesnoList != null && seriesnoList.size() > 0) {
				exportResultMap.put("totalaverage", totalaverage);
			}
			exportResultMap.put("crewName", crewInfo.getCrewName());
			
			viewInfoService.exportViewToExcelTemplate(srcfilePath, exportResultMap,	downloadPath);
			
			resultMap.put("downloadPath", downloadPath);
			resultMap.put("fileName", fileName);
			message = "导出成功!";
			
			this.sysLogService.saveSysLog(request, "导出分集汇总信息", Constants.TERMINAL_PC, ViewInfoModel.TABLE_NAME, null, 5);
		} catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常, 导出失败!";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "导出分集汇总信息失败：" + e.getMessage(), Constants.TERMINAL_PC, ViewInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 跳转到摄制生产报表页面
	 * @param crewId
	 * @return
	 */
	@RequestMapping("/toProductionReportPage")
	public ModelAndView toProductionReportPage() {
		ModelAndView mv = new ModelAndView("/statistic/productionReport");
		return mv;
	}
	
	/**
	 * 摄制生产报表--通告单处
	 * 
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/queryShootingProductionReport")
	public Map<String, Object> queryShootingProductionReport(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			//当前剧组信息
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
						
			//拍摄生产进度信息
			resultMap.putAll(this.getShootingProductionReport(crewId, crewInfo));
			
			this.sysLogService.saveSysLog(request, "查询摄制生产报表", Constants.TERMINAL_PC, NoticeInfoModel.TABLE_NAME, null, 0);
        } catch (Exception e) {
			success = false;
			message = "未知异常,查询摄制生产报表失败";

			logger.error(message, e);
			
			this.sysLogService.saveSysLog(request, "查询摄制生产报表失败：" + e.getMessage(), Constants.TERMINAL_PC, NoticeInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 拍摄生产报表
	 * 
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/exportShootingProductionReport")
	public Map<String, Object> exportShootingProductionReport(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = getCrewId(request);
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

			// 获取模板文件地址配置
			Properties property = PropertiesUitls.fetchProperties("/config.properties");
			String srcfilePath = property.getProperty("production_reportTemplate");

			//生成下载文件路径
			String downloadPath = property.getProperty("downloadPath") + "production_report_" + System.currentTimeMillis() + ".xls";
			File pathFile = new File(property.getProperty("downloadPath"));
			if (!pathFile.isDirectory()) {
				pathFile.mkdirs();
			}

			//生成下载文件名
			String fileName = "《" + crewInfo.getCrewName() + "》摄制生产报表_"	+ sdf.format(new Date()) + ".xls";			
			
			Map<String, Object> exportResultMap = new HashMap<String, Object>();
			//拍摄生产进度信息
			exportResultMap.putAll(this.getShootingProductionReport(crewId, crewInfo));
			exportResultMap.put("crewName", crewInfo.getCrewName());
			
			viewInfoService.exportViewToExcelTemplate(srcfilePath, exportResultMap,	downloadPath);
			
			resultMap.put("downloadPath", downloadPath);
			resultMap.put("fileName", fileName);
			message = "导出成功!";
			
			this.sysLogService.saveSysLog(request, "导出摄制生产报表", Constants.TERMINAL_PC, NoticeInfoModel.TABLE_NAME, null, 5);
		} catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常, 导出失败!";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "导出摄制生产报表失败：" + e.getMessage(), Constants.TERMINAL_PC, NoticeInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 对拍摄生产报表进行补充，包括：天数、累计完成、平均完成、差天数、每日需完成、完成集数
	 * @param scheduleList
	 * @throws ParseException 
	 */
	private Map<String, Object> getShootingProductionReport(
			String crewId, CrewInfoModel crewInfo) throws ParseException {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		DecimalFormat df = new DecimalFormat("0.00");
		DecimalFormat df1 = new DecimalFormat("0.0");
		List<Map<String, Object>> scheduleList = this.noticeService.queryShootingProductionReport(crewId);
		//场景总体信息
		Map<String, Object> viewTotal = this.viewInfoService.queryViewTotalInfo(crewId);
		double viewTotalPageCount = Double.parseDouble(viewTotal.get("totalPageCount") + "");
		int viewTotalSeriesNo = Integer.parseInt(viewTotal.get("totalSeriesNo") + "");
		//拍摄日期
		Date shootStartDate = crewInfo.getShootStartDate();
		Date shootEndDate = crewInfo.getShootEndDate();
		if(scheduleList != null && scheduleList.size() > 0) {
			int totalViewCount = 0; //累计完成场数
			int dayNum = 0;
			double totalPageCount = 0; //累计完成页数
			double finishSeriesno = 0; //完成集数
			
			boolean flag = false; //标识是否设置了拍摄日期，false：没有
			int shootDate = 0;
			double avgEveryDayPageCount = 0; //平均每日需完成
			if(StringUtils.isNotBlank(shootStartDate + "") && StringUtils.isNotBlank(shootEndDate + "")) {
				flag = true;
				shootDate = DateUtils.daysBetween(shootStartDate, shootEndDate) + 1;

				resultMap.put("shootDate", shootDate);
				avgEveryDayPageCount = viewTotalPageCount / shootDate;
				resultMap.put("everyDayPageCount", df1.format(avgEveryDayPageCount));
			}
			//每集页数
			double everySeriesPageCount = viewTotalPageCount / viewTotalSeriesNo;
			//累计差额
			double totalDayCha = 0;
			for(int i = 0; i < scheduleList.size(); i++) {
				Map<String, Object> oneSchedule = scheduleList.get(i);
				dayNum++;
				oneSchedule.put("dayNum", dayNum); //天数
				//页数
				oneSchedule.put("planPageCount", df1.format(oneSchedule.get("planPageCount")));
				oneSchedule.put("realPageCount", df1.format(oneSchedule.get("realPageCount")));
				
				double pageCount = Double.parseDouble(oneSchedule.get("realPageCount") + "");
				if(flag) {
					//每日需完成
					double everyDayPageCount = (viewTotalPageCount - totalPageCount) / shootDate;
					if(shootDate == 0) {
						everyDayPageCount = viewTotalPageCount - totalPageCount;
					}
					oneSchedule.put("everyDayPageCount", df1.format(everyDayPageCount)); //每日需完成
					shootDate--;
					//差天数
					double everyDayCha = (pageCount - avgEveryDayPageCount) / avgEveryDayPageCount; //每天差额			
//					double everyDayCha = (pageCount - everyDayPageCount) / everyDayPageCount; //每天差额
					oneSchedule.put("everyDayCha", df.format(everyDayCha));
					totalDayCha += everyDayCha;
					oneSchedule.put("totalDayCha", df.format(totalDayCha)); //累计差额
				}
				
				//累计完成
				totalPageCount = BigDecimalUtil.add(totalPageCount, pageCount);
				oneSchedule.put("totalPageCount", df1.format(totalPageCount)); //累计完成页数
				//平均完成
				oneSchedule.put("avgPageCount", df1.format(totalPageCount / dayNum)); //平均完成页数				

				totalViewCount += Integer.parseInt(oneSchedule.get("realViewCount") + "");
				oneSchedule.put("totalViewCount", totalViewCount); //累计完成场数
				oneSchedule.put("avgViewCount", df.format((totalViewCount * 1.0) / dayNum)); //平均完成场数
				
				//完成集数
				finishSeriesno += (pageCount / everySeriesPageCount);
				oneSchedule.put("finishSeriesno", df.format(finishSeriesno));
			}
			resultMap.put("flag", flag);
		}
		resultMap.put("resultList", scheduleList);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(shootStartDate != null) {
			resultMap.put("shootStartDate", sdf.format(crewInfo.getShootStartDate()));
		}
		if(shootEndDate != null) {
			resultMap.put("shootEndDate", sdf.format(crewInfo.getShootEndDate()));
		}
		resultMap.put("viewTotalViewCount", Integer.parseInt(viewTotal.get("totalViewCount") + ""));
		resultMap.put("viewTotalPageCount", df.format(viewTotalPageCount));
		
		return resultMap;
	}
}
