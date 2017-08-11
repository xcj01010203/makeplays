package com.xiaotu.makeplays.statistic.view;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.notice.service.NoticeService;
import com.xiaotu.makeplays.roleactor.service.ViewRoleService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.DateUtils;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.utils.StringUtils;
import com.xiaotu.makeplays.view.model.ViewInfoModel;
import com.xiaotu.makeplays.view.model.constants.ShootStatus;
import com.xiaotu.makeplays.view.model.constants.ViewType;
import com.xiaotu.makeplays.view.service.ViewInfoService;

/**
 * @类名：ViewProductionStatisticController.java
 * @作者：李晓平
 * @时间：2017年1月13日 上午11:10:25
 * @描述：生产进度
 */
@Controller
@RequestMapping("/shootStatistic")
public class ShootStatisticController extends BaseController{

	Logger logger = LoggerFactory.getLogger(ShootStatisticController.class);
	
	private DecimalFormat df = new DecimalFormat("0.00");
	private DecimalFormat df2 = new DecimalFormat("0.0");
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private ViewRoleService viewRoleService;
	
	/**
	 * 跳转到生产进度统计页面
	 * @return
	 */
	@RequestMapping("/toShootStatisticPage")
	public ModelAndView toShootStatisticPage(HttpServletRequest request, String crewId) {
		ModelAndView mv = new ModelAndView("/statistic/shootStatistic");
		
		this.sysLogService.saveSysLog(request, "查询生产进度", Constants.TERMINAL_PC, ViewInfoModel.TABLE_NAME, "", 0);
		return mv;
	}
	
	/**
	 * 查询总体进度
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryTotalProduction")
	@ResponseBody
	public Map<String, Object> queryTotalProduction(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			//剧组信息
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			//剧组总体信息
			Map<String, Object> viewTotal = this.viewInfoService.queryViewTotalInfo(crewId);
			//场景总体拍摄进度
			List<Map<String, Object>> viewInfoList = viewInfoService.queryTotalViewInfo(crewId);
			//总体进度
			Map<String, Object> totalMap = new HashMap<String, Object>();
			totalMap.put("title", "总进度");
			totalMap.put("totalSeriesNo", viewTotal.get("totalSeriesNo"));//总集数
			//分组进度
			Map<String, Map<String, Object>> groupKeyMap = new LinkedHashMap<String, Map<String,Object>>();
			//内外、日夜、武戏进度
			Map<String, Map<String, Object>> siteKeyMap = new HashMap<String, Map<String,Object>>();
			int totalViewCount = 0;
			int finishedViewCount = 0;
			double totalPageCount = 0;
			double finishedPageCount = 0;
			if(viewInfoList != null && viewInfoList.size() > 0) {
				for(Map<String, Object> one : viewInfoList) {
					double pageCount = (Double) one.get("pageCount");
					int shootStatus = (Integer) one.get("shootStatus");
					//总进度
					totalViewCount++;
					totalPageCount += pageCount;
					if(shootStatus == ShootStatus.Finished.getValue()) {
						finishedViewCount++;
						finishedPageCount = BigDecimalUtil.add(finishedPageCount, pageCount);
					}
					//分组进度
//					String groupNames = one.get("groupNames") + "";
//					if(StringUtil.isNotBlank(groupNames)) {
//						String[] groupNameArr = groupNames.split(",");
//						for(String groupName : groupNameArr) {
//							this.genStatisticMap(groupKeyMap, groupName, shootStatus, pageCount);
//						}
//					}
					//内外、日夜、武戏进度
					String site = one.get("site") + "";
					String siteName = "";
					if(StringUtil.isNotBlank(site) && site.indexOf("外") >= 0) {//内外
						siteName = "外戏";
					} else {
						siteName = "内戏";
					}
					this.genStatisticMap(siteKeyMap, siteName, shootStatus, pageCount);
					String atmosphereName = one.get("atmosphereName") + "";
					if(StringUtil.isNotBlank(atmosphereName) && atmosphereName.indexOf("夜") >= 0) {//日夜
						siteName = "夜戏";
					} else {
						siteName = "日戏";
					}
					this.genStatisticMap(siteKeyMap, siteName, shootStatus, pageCount);
					Integer viewType = (Integer) one.get("viewType");
					if(viewType != null && (viewType == ViewType.Wuxi.getValue() || viewType == ViewType.WuTe.getValue())) {//文武
						siteName = "武戏";
					} else {
						siteName = "文戏";
					}
					this.genStatisticMap(siteKeyMap, siteName, shootStatus, pageCount);
				}
			}
			totalMap.put("totalViewCount", totalViewCount);//总场数
			totalMap.put("finishedViewCount", finishedViewCount);//已完成场数
			totalMap.put("totalPageCount", df2.format(totalPageCount));//总页数
			totalMap.put("finishedPageCount", df2.format(finishedPageCount));//已完成页数
			if(StringUtil.isNotBlank(viewTotal.get("totalSeriesNo") + "") && totalPageCount != 0) {
				totalMap.put("finishedSeriesNo", BigDecimalUtil.divide(finishedPageCount, totalPageCount / Double.parseDouble(viewTotal.get("totalSeriesNo") + ""), 2));
			} else {
				totalMap.put("finishedSeriesNo", 0);
			}
			
			//计划拍摄天数
			Date shootStartDate = crewInfo.getShootStartDate();
			Date shootEndDate = crewInfo.getShootEndDate();
			int planShootDate = 0;
			if(StringUtils.isNotBlank(shootStartDate + "") && StringUtils.isNotBlank(shootEndDate + "")) {
				planShootDate = DateUtils.daysBetween(shootStartDate, shootEndDate) + 1;
			}
			totalMap.put("planShootDate", planShootDate);
			//计划日均完成
			double planEveryDayViewCount = 0;
			double planEveryDayPageCount = 0;
			if(planShootDate != 0) {
				planEveryDayViewCount = BigDecimalUtil.divide(totalViewCount, planShootDate, 2);
				planEveryDayPageCount = BigDecimalUtil.divide(totalPageCount, planShootDate, 2);
			}
			totalMap.put("planEveryDayViewCount", planEveryDayViewCount);
			totalMap.put("planEveryDayPageCount", planEveryDayPageCount);
			//已拍摄天数
			int shootDate = this.noticeService.queryShootDates(crewId);
			totalMap.put("shootDate", shootDate);
			//实际日均完成
			double everyDayViewCount = 0;
			double everyDayPageCount = 0;
			if(shootDate != 0) {
				everyDayViewCount = BigDecimalUtil.divide(finishedViewCount, shootDate, 2);
				everyDayPageCount = BigDecimalUtil.divide(finishedPageCount, shootDate, 2);
			}
			totalMap.put("everyDayViewCount", everyDayViewCount);
			totalMap.put("everyDayPageCount", everyDayPageCount);
			//当前提前/超期
			double pageDateCha = 0;
			if(planEveryDayPageCount != 0) {
				pageDateCha = (finishedPageCount - planEveryDayPageCount * shootDate) / planEveryDayPageCount;
				totalMap.put("pageDateCha", df.format(pageDateCha));
			} else {
				totalMap.put("pageDateCha", 0);
			}
			//预计提前/逾期
//			double realViewDate = 0;
//			double realPageDate = 0;
//			if(everyDayViewCount != 0) {
//				realViewDate = BigDecimalUtil.divide(totalViewCount, everyDayViewCount, 2);
//				totalMap.put("viewDateCha", df.format(realViewDate - planShootDate));
//			} else {
//				totalMap.put("viewDateCha", 0);
//			}
//			if(everyDayPageCount != 0) {
//				realPageDate = BigDecimalUtil.divide(totalPageCount, everyDayPageCount, 2);
//				totalMap.put("pageDateCha", df.format(realPageDate - planShootDate));
//			} else {
//				totalMap.put("pageDateCha", 0);
//			}
			//如按期完成，需日均拍摄
			double needPageCount = 0;
			if((planShootDate - shootDate) != 0) {
				needPageCount = (totalPageCount - finishedPageCount) / (planShootDate - shootDate);
			}
			totalMap.put("needPageCount", df2.format(needPageCount));
			
			resultMap.put("total", totalMap);
			resultMap.put("groupMap", groupKeyMap);
			//检查内外、日夜、文武是否都有
			String[] keys = new String[]{"内戏", "外戏", "日戏", "夜戏", "文戏", "武戏"};
			for(String key : keys) {
				if(!siteKeyMap.containsKey(key)) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("title", key);
					map.put("totalViewCount", 0);
					map.put("finishedViewCount", 0);
					map.put("totalPageCount", 0.0);
					map.put("finishedPageCount", 0.0);
					siteKeyMap.put(key, map);
				} else {
					Map<String, Object> map = siteKeyMap.get(key);
					map.put("totalPageCount", df2.format(map.get("totalPageCount")));
					map.put("finishedPageCount", df2.format(map.get("finishedPageCount")));
				}
			}
			resultMap.put("siteMap", siteKeyMap);
			
//			this.sysLogService.saveSysLog(request, "查询生产总体进度", Constants.TERMINAL_PC, ViewInfoModel.TABLE_NAME, "", 0);
        } catch (Exception e) {
			success = false;
			message = "未知异常,查询总体进度失败";

			logger.error(message, e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}	
	
	/**
	 * 计算合计场次、已完成场次、合计页数、已完成页数
	 * @param keyMap
	 * @param key
	 * @param shootStatus
	 * @param pageCount
	 */
	private void genStatisticMap(Map<String, Map<String, Object>> keyMap, String key,
			int shootStatus, double pageCount) {
		Map<String, Object> map = null;
		if(!keyMap.containsKey(key)) {
			map = new HashMap<String, Object>();
			map.put("title", key);
			map.put("totalViewCount", 0);
			map.put("finishedViewCount", 0);
			map.put("totalPageCount", 0.0);
			map.put("finishedPageCount", 0.0);
			keyMap.put(key, map);
		} else {
			map = keyMap.get(key);
		}
		map.put("totalViewCount", Integer.parseInt(map.get("totalViewCount") + "") + 1);
		map.put("totalPageCount", BigDecimalUtil.add(Double.parseDouble(map.get("totalPageCount") + ""), pageCount));
		if(shootStatus == ShootStatus.Finished.getValue()) {
			map.put("finishedViewCount", Integer.parseInt(map.get("finishedViewCount") + "") + 1);
			map.put("finishedPageCount", BigDecimalUtil.add(Double.parseDouble(map.get("finishedPageCount") + ""), pageCount));
		}
	}
	
	/**
	 * 查询日进度，包括日拍摄量、拍摄进度累计
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryDayProduction")
	@ResponseBody
	public Map<String, Object> queryDayProduction(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			
			List<Map<String, Object>> resultList = this.noticeService.queryDayProduction(crewId);
			if(resultList != null && resultList.size() > 0) {
				Map<String, List<Map<String, Object>>> dateMap = new LinkedHashMap<String, List<Map<String,Object>>>();//存放日期对应的分组场次、页数
				Map<String, Set<String>> dateGroupMap = new HashMap<String, Set<String>>();//存放日期对应的分组
				Set<String> group = new HashSet<String>();//存放所有分组
				for(Map<String, Object> map : resultList) {
					String noticeDate = map.get("noticeDate") + "";
					String groupName = map.get("groupName") + "";
					map.put("pageCount", df.format(Double.parseDouble(map.get("pageCount") + "")));
					List<Map<String, Object>> groupList = null;
					Set<String> groupSet = null;
					if(!dateMap.containsKey(noticeDate)) {
						groupList = new ArrayList<Map<String,Object>>();
						dateMap.put(noticeDate, groupList);
						groupSet = new HashSet<String>();
						dateGroupMap.put(noticeDate, groupSet);
					} else {
						groupList = dateMap.get(noticeDate);
						groupSet = dateGroupMap.get(noticeDate);
					}
					groupList.add(map);
					groupSet.add(groupName);
					if(!group.contains(groupName)) {
						group.add(groupName);
					}
				}
				//对数据进行遍历，将日期没有的分组场次、页数置为0
				Iterator<String> i = group.iterator();		          
		        while(i.hasNext()){//遍历  
		        	String groupName = i.next();
		            Iterator<String> j = dateGroupMap.keySet().iterator();
		            while(j.hasNext()) {
		            	String noticeDate = j.next();
		            	Set<String> groupSet = dateGroupMap.get(noticeDate);
		            	if(!groupSet.contains(groupName)) {
		            		List<Map<String, Object>> groupList = dateMap.get(noticeDate);
		            		Map<String, Object> groupMap = new HashMap<String, Object>();
		            		groupMap.put("noticeDate", noticeDate);
		            		groupMap.put("groupName", groupName);
		            		groupMap.put("viewCount", 0);
		            		groupMap.put("pageCount", 0.0);
		            		groupList.add(groupMap);
		            	}
		            }
		        }
		        
		        //日累计
		        //计算每日完成量
		        List<Map<String, Object>> dateList = new ArrayList<Map<String,Object>>();
		        Iterator<String> j = dateMap.keySet().iterator();
		        while(j.hasNext()) {
		        	String noticeDate = j.next();
		        	int totalViewCount = 0;
		        	double totalPageCount = 0.0;
		        	List<Map<String, Object>> groupList = dateMap.get(noticeDate);
		        	//将拍摄分组名排序
		        	Collections.sort(groupList, new Comparator<Map<String,Object>>() {
		        		@Override
		        		public int compare(Map<String, Object> o1,
		        				Map<String, Object> o2) {
		        			String name1 = o1.get("groupName") + "";
		        			String name2 = o2.get("groupName") + "";
		        			return name1.compareTo(name2);
		        		}
					});
		        	for(Map<String, Object> one : groupList) {
		        		totalViewCount += Integer.parseInt(one.get("viewCount") + "");
		        		totalPageCount = BigDecimalUtil.add(totalPageCount, Double.parseDouble(one.get("pageCount") + ""));
		        	}
		        	Map<String, Object> map = new HashMap<String, Object>();
		        	map.put("noticeDate", noticeDate);
		        	map.put("viewNum", totalViewCount);
		        	map.put("pageNum", df2.format(totalPageCount));
		        	dateList.add(map);
		        }
		        //计算每日累计量
		        for(int m = 0; m < dateList.size(); m++) {
		        	Map<String, Object> one = dateList.get(m);
		        	if(m == 0) {
		        		one.put("viewCount", one.get("viewNum"));
		        		one.put("pageCount", df2.format(Double.parseDouble(one.get("pageNum") + "")));
		        	} else {
		        		Map<String, Object> lastOne = dateList.get(m - 1);
		        		one.put("viewCount", Integer.parseInt(one.get("viewNum") + "") + Integer.parseInt(lastOne.get("viewCount") + ""));
		        		one.put("pageCount", df2.format(BigDecimalUtil.add(Double.parseDouble(one.get("pageNum") + ""), Double.parseDouble(lastOne.get("pageCount") + ""))));
		        	}
		        }

				resultMap.put("dateMap", dateMap);
				resultMap.put("dateList", dateList);
			}
			
//			this.sysLogService.saveSysLog(request, "查询日拍摄量，拍摄进度累计", Constants.TERMINAL_PC, NoticeInfoModel.TABLE_NAME, "", 0);
        } catch (Exception e) {
			success = false;
			message = "未知异常,查询日进度失败";

			logger.error(message, e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询拍摄地
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryLocationProduction")
	@ResponseBody
	public Map<String, Object> queryLocationProduction(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			
			List<Map<String, Object>> resultList = this.viewInfoService.queryShootLocationProduction(crewId);
			resultMap.put("result", resultList);
			
//			this.sysLogService.saveSysLog(request, "查询拍摄地进度", Constants.TERMINAL_PC, ViewInfoModel.TABLE_NAME, "", 0);
        } catch (Exception e) {
			success = false;
			message = "未知异常,查询拍摄地进度失败";

			logger.error(message, e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询场景角色拍摄进度
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryViewRoleProduction")
	@ResponseBody
	public Map<String, Object> queryViewRoleProduction(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			
			List<Map<String, Object>> resultList = this.viewInfoService.queryViewRoleProduction(crewId);
			//将主要演员、特约演员分开
			if(resultList != null && resultList.size() > 0) {
				List<Map<String, Object>> majorList = new ArrayList<Map<String,Object>>();
				List<Map<String, Object>> guestList = new ArrayList<Map<String,Object>>();
				for(Map<String, Object> one : resultList) {
					String viewRoleType = one.get("viewRoleType") + "";
					if(viewRoleType.equals("1")) {
						majorList.add(one);
					} else if(viewRoleType.equals("2")) {
						guestList.add(one);
					}
				}
				resultMap.put("majorList", majorList);
				resultMap.put("guestList", guestList);
			}
//			this.sysLogService.saveSysLog(request, "查询场景角色拍摄进度", Constants.TERMINAL_PC, NoticeInfoModel.TABLE_NAME, "", 0);
        } catch (Exception e) {
			success = false;
			message = "未知异常,查询场景角色拍摄进度失败";

			logger.error(message, e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询角色已拍摄天数、预计拍摄天数
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryViewRoleShootDays")
	@ResponseBody
	public Map<String, Object> queryViewRoleShootDays(HttpServletRequest request, String viewRoleId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {			
			//角色已拍摄天数
			Map<String, Object> viewRoleFinishedDays = this.viewRoleService.queryViewRoleFinishedDays(viewRoleId);
			//角色预计拍摄天数
			List<Map<String, Object>> viewRoleShootDays = this.viewRoleService.queryViewRoleShootDays(viewRoleId);
			if(viewRoleFinishedDays != null && !viewRoleFinishedDays.isEmpty()) {
				resultMap.put("finishedDays", viewRoleFinishedDays.get("finishedDays"));
			}
			if(viewRoleShootDays != null && viewRoleShootDays.size() > 0) {
				resultMap.put("shootDays", viewRoleShootDays.get(0).get("shootDays"));
			}
        } catch (Exception e) {
			success = false;
			message = "未知异常,查询角色已拍摄天数、预计拍摄天数失败";

			logger.error(message, e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询角色日拍摄量
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryViewRoleDayProduction")
	@ResponseBody
	public Map<String, Object> queryViewRoleDayProduction(HttpServletRequest request, String viewRoleId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {			
			//角色-日拍摄量
			List<Map<String, Object>> viewRoleDayList = this.viewRoleService.queryRoleViewStatistic(viewRoleId);
			resultMap.put("result", viewRoleDayList);
        } catch (Exception e) {
			success = false;
			message = "未知异常,查询角色日拍摄量失败";

			logger.error(message, e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询角色分集戏量
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryViewRoleSeries")
	@ResponseBody
	public Map<String, Object> queryViewRoleSeries(HttpServletRequest request, String viewRoleId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			
			List<Map<String, Object>> resultList = this.viewRoleService.queryRoleViewBySeries(crewId, viewRoleId);
			
			resultMap.put("result", resultList);
        } catch (Exception e) {
			success = false;
			message = "未知异常,查询角色分集戏量失败";

			logger.error(message, e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
}
