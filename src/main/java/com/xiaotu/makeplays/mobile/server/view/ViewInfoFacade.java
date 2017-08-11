package com.xiaotu.makeplays.mobile.server.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.locationsearch.model.SceneViewInfoModel;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.mobile.server.common.BaseFacade;
import com.xiaotu.makeplays.notice.model.NoticeInfoModel;
import com.xiaotu.makeplays.notice.model.constants.NoticeCanceledStatus;
import com.xiaotu.makeplays.notice.service.NoticeService;
import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;
import com.xiaotu.makeplays.roleactor.service.ViewRoleService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.DateUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.view.controller.dto.SeriesNoDto;
import com.xiaotu.makeplays.view.controller.dto.ViewNoDto;
import com.xiaotu.makeplays.view.controller.filter.ViewFilter;
import com.xiaotu.makeplays.view.model.ViewInfoModel;
import com.xiaotu.makeplays.view.model.constants.ShootStatus;
import com.xiaotu.makeplays.view.model.constants.ViewType;
import com.xiaotu.makeplays.view.service.ViewContentService;
import com.xiaotu.makeplays.view.service.ViewInfoService;

/**
 * 手机端场景相关接口
 * @author xuchangjian 2016-9-19下午2:16:14
 */
@Controller
@RequestMapping("/interface/viewInfoFacade")
public class ViewInfoFacade extends BaseFacade{
	
	Logger logger = LoggerFactory.getLogger(ViewInfoFacade.class);
	
	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	@Autowired
	private ViewContentService viewContentService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ViewRoleService viewRoleService;
	
	@Autowired
	private CrewInfoService crewInfoService;

	/**
	 * 获取集场号
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	@RequestMapping("/obtainSeriesViewNos")
	@ResponseBody
	public Object obtainSeriesViewNos(String crewId, String userId, String noticeId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			//校验数据
			if (StringUtils.isBlank(crewId) && StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("未获取到剧本信息");
			}
			
			//用户关注的角色信息
			List<String> focusRoleIdList = this.genFocusRoleInfo(crewId, userId);
			
			List<String> focusViewIdList = new ArrayList<String>();
			if (focusRoleIdList.size() > 0) {
				String foucsRoleIds = "";
				for (int i = 0; i < focusRoleIdList.size(); i++) {
					String roleId = focusRoleIdList.get(i);
					if (i == 0) {
						foucsRoleIds = roleId;
					} else {
						foucsRoleIds += "," + roleId;
					}
				}
				
				List<Map<String, Object>> focusSeriesNoMapList = this.viewInfoService.queryViewNoBySeriesNoAndRoleInfo(crewId, null, foucsRoleIds);
				
				for (Map<String, Object> focusSeriesMap : focusSeriesNoMapList) {
					String viewId = (String) focusSeriesMap.get("viewId");
					focusViewIdList.add(viewId);
				}
			}
			
			//查找所有集次场次信息
			List<Map<String, Object>> seriesViewNos = new ArrayList<Map<String, Object>>();
			
			if (!StringUtils.isBlank(noticeId)) {
				seriesViewNos = this.noticeService.queryNoticeSeriesViewNo(noticeId, crewId);
			} else {
				seriesViewNos = this.viewInfoService.querySeriesViewNoByCrewId(crewId);
			}
			
			Map<Integer, List<String>> seriesViewNoMap = new TreeMap<Integer, List<String>>();	//存储集-场的对应的关系,key-集次  value-该集下所有场次
			List<Integer> seriesNoList = new ArrayList<Integer>();	//查出来的剧本信息中所有的集次
			for (Map<String, Object> viewInfo : seriesViewNos) {
				Integer seriesNo = (Integer) viewInfo.get("seriesNo");
				if (!seriesNoList.contains(seriesNo)) {
					seriesNoList.add(seriesNo);
				}
			}
			
			for (Integer seriesNo : seriesNoList) {
				List<String> viewList = new LinkedList<String>();
				for (Map<String, Object> viewInfo : seriesViewNos) {
					if (viewInfo.get("seriesNo") == seriesNo) {
						viewList.add((String) viewInfo.get("viewNo"));
					}
				}
				
				if (StringUtils.isBlank(noticeId)) {
					Comparator<String> sort = com.xiaotu.makeplays.utils.StringUtils.sort();
					Collections.sort(viewList, sort);
				}
				seriesViewNoMap.put(seriesNo, viewList);
			}
			
			Set<Integer> keySet = seriesViewNoMap.keySet();
			Iterator<Integer> iter = keySet.iterator();
			
			List<SeriesNoDto> seriesNoDtoList = new ArrayList<SeriesNoDto>();
			while(iter.hasNext()) {
				Integer key = (Integer) iter.next();
				List<String> value = seriesViewNoMap.get(key);
				
				
				SeriesNoDto viewNoDto = new SeriesNoDto();
				viewNoDto.setSeriesNo(key);
				List<ViewNoDto> viewNoDtoList = new ArrayList<ViewNoDto>();
				for (String viewNo : value) {
					for (Map<String, Object> viewInfo : seriesViewNos) {
						Integer mySeriesNo = (Integer) viewInfo.get("seriesNo");
						String myViewNo = (String) viewInfo.get("viewNo");
						String myViewId = (String) viewInfo.get("viewId");
						if (mySeriesNo == key && myViewNo.equals(viewNo)) {
							ViewNoDto viewnoDto = new ViewNoDto();
							viewnoDto.setViewNo(viewNo);
							
							int isManualSave = (Integer) viewInfo.get("isManualSave");
							if (isManualSave == 1) {
								viewnoDto.setIsManualSave(true);
							} else {
								viewnoDto.setIsManualSave(false);
							}
							viewnoDto.setViewId(myViewId);
							if (focusViewIdList.contains(myViewId)) {
								viewnoDto.setHasFocusRole(true);
							} else {
								viewnoDto.setHasFocusRole(false);
							}
							viewNoDtoList.add(viewnoDto);
						}
					}
				}
				viewNoDto.setViewNoDtoList(viewNoDtoList);
				seriesNoDtoList.add(viewNoDto);
			}
			
			resultMap.put("seriesNoDtoList", seriesNoDtoList);
		} catch (IllegalArgumentException ie) {
			String msg = ie.getMessage();
			logger.error(msg, ie);
			throw new IllegalArgumentException(msg);
		} catch (Exception e) {
			String msg = "未知异常，获取集场号失败";
			logger.error(msg, e);
			throw new IllegalArgumentException(msg);
		}
		
		return resultMap;
	}
	
	/**
	 * 获取剧组下的集次列表
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	@RequestMapping("/obtainSeriesNoList")
	@ResponseBody
	public Object obtainSeriesNoList(String crewId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);

			List<Map<String, String>> seriesInfoList = new ArrayList<Map<String, String>>();
			
			List<String> focusRoleIdList = this.genFocusRoleInfo(crewId, userId);
			
			Map<Integer, Integer> focusSeriesViewCountMap = new HashMap<Integer, Integer>();
			if (focusRoleIdList.size() > 0) {
				String foucsRoleIds = "";
				for (int i = 0; i < focusRoleIdList.size(); i++) {
					String roleId = focusRoleIdList.get(i);
					if (i == 0) {
						foucsRoleIds = roleId;
					} else {
						foucsRoleIds += "," + roleId;
					}
				}
				
				List<Map<String, Object>> focusSeriesNoMapList = this.viewInfoService.querySeriesNoWithRoleInfo(crewId, foucsRoleIds);
				
				for (Map<String, Object> focusSeriesMap : focusSeriesNoMapList) {
					int seriesNo = (Integer) focusSeriesMap.get("seriesNo");
					Long viewCount = (Long) focusSeriesMap.get("viewCount");
					
					if (viewCount == null) {
						viewCount = 0L;
					}
					focusSeriesViewCountMap.put(seriesNo, viewCount.intValue());
				}
			}
			
			List<Map<String, Object>> seriesNosMap = this.viewInfoService.querySeriesNoByCrewId(crewId);
			for (Map<String, Object> map : seriesNosMap) {
				Integer seriesNo = (Integer) map.get("seriesNo");
				Long totalViewCount = (Long) map.get("totalViewCount");
				
				Integer focusViewCount = focusSeriesViewCountMap.get(seriesNo);
				
				Map<String, String> singleResult = new HashMap<String, String>();
				singleResult.put("seriesNo", seriesNo.toString());
				singleResult.put("totalViewCount", totalViewCount.toString());
				singleResult.put("focusViewCount", focusViewCount == null ? "0" : focusViewCount.toString());
				
				seriesInfoList.add(singleResult);
			}
			
			resultMap.put("seriesInfoList", seriesInfoList);
		} catch (IllegalArgumentException ie) {
			String msg = ie.getMessage();
			logger.error(msg, ie);
			throw new IllegalArgumentException(msg);
		} catch (Exception e) {
			String msg = "未知异常，获取集次信息失败";
			logger.error(msg, e);
			throw new IllegalArgumentException(msg);
		}
		
		return resultMap;
	}
	
	/**
	 * 获取剧组下指定一集的场次列表
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	@RequestMapping("/obtainViewNoList")
	@ResponseBody
	public Object obtainViewNoList(String crewId, String seriesNo, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			List<ViewNoDto> viewNoDtoList = new ArrayList<ViewNoDto>();
			
			if (StringUtils.isBlank(crewId) || StringUtils.isBlank(seriesNo)) {
				throw new IllegalArgumentException("无效的数据访问");
			}
			
			//关注的角色在剧本中的场次
			List<String> focusRoleIdList = this.genFocusRoleInfo(crewId, userId);
			
			List<String> focusViewIdList = new ArrayList<String>();
			if (focusRoleIdList.size() > 0) {
				String foucsRoleIds = "";
				for (int i = 0; i < focusRoleIdList.size(); i++) {
					String roleId = focusRoleIdList.get(i);
					if (i == 0) {
						foucsRoleIds = roleId;
					} else {
						foucsRoleIds += "," + roleId;
					}
				}
				
				List<Map<String, Object>> focusSeriesNoMapList = this.viewInfoService.queryViewNoBySeriesNoAndRoleInfo(crewId, seriesNo, foucsRoleIds);
				
				for (Map<String, Object> focusSeriesMap : focusSeriesNoMapList) {
					String viewId = (String) focusSeriesMap.get("viewId");
					focusViewIdList.add(viewId);
				}
			}
			
			List<Map<String, Object>> seriesNosMap = this.viewInfoService.queryViewNoByCrewIdAndSeriesNo(crewId, seriesNo);
			
			//排序
			List<String> viewNoList = new ArrayList<String>();
			for (Map<String, Object> map : seriesNosMap) {
				viewNoList.add((String) map.get("viewNo"));
			}
			Comparator<String> sort = com.xiaotu.makeplays.utils.StringUtils.sort();
			Collections.sort(viewNoList, sort);
			
			
			for (String viewNo : viewNoList) {
				for (Map<String, Object> map : seriesNosMap) {
					String viewId = (String) map.get("viewId");
					String myViewNo = (String) map.get("viewNo");
					int isManualSave = (Integer) map.get("isManualSave");
					
					boolean hasFocusRole = false;
					if (focusViewIdList.contains(viewId)) {
						hasFocusRole = true;
					}
					
					if (viewNo.equals(myViewNo)) {
						ViewNoDto viewNoDto = new ViewNoDto();
						if (isManualSave == 1) {
							viewNoDto.setIsManualSave(true);
						} else {
							viewNoDto.setIsManualSave(false);
						}
						viewNoDto.setViewId(viewId);
						viewNoDto.setViewNo(viewNo);
						viewNoDto.setHasFocusRole(hasFocusRole);
						
						viewNoDtoList.add(viewNoDto);
					}
				}
			}
			
			resultMap.put("viewList", viewNoDtoList);
		} catch (IllegalArgumentException ie) {
			String msg = ie.getMessage();
			logger.error(msg, ie);
			throw new IllegalArgumentException(msg);
		} catch (Exception e) {
			String msg = "未知异常，获取场次信息失败";
			logger.error(msg, e);
			throw new IllegalArgumentException(msg);
		}
		return resultMap;
	}
	
	/**
	 * 获取用户关注的角色信息
	 * @param crewId
	 * @param userId
	 * @return
	 */
	private List<String> genFocusRoleInfo(String crewId, String userId) {
		List<String> focusRoleIdList = new ArrayList<String>();
		//获取用户关注的角色信息
		List<ViewRoleModel> userFouceRoleList = this.viewRoleService.queryUserFocusRoleInfo(crewId, userId);
		for (ViewRoleModel viewRole : userFouceRoleList) {
			focusRoleIdList.add(viewRole.getViewRoleId());
		}
		return focusRoleIdList;
	}
	
	/**
	 * 获取剧组场景统计信息
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainViewStatisticInfo")
	public Object obtainViewStatisticInfo(HttpServletRequest request, String crewId, String userId) { 
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			
			/*
			 * 拍摄天数/场数统计数据
			 */
			int shootedDays = 0;	//已拍摄天数
			Integer allShootDays = 0;	//总天数
			int shootedViews = 0;	//已拍摄场数
			int allViews = 0;	//总场数
			double finishedPageCount = 0;	//已拍摄页数
			double totalPageCount = 0; 	//总页数
			
			//拍摄天数的计算
			CrewInfoModel crewInfo = this.crewInfoService.queryById(crewId);
			Date shootStartDate = crewInfo.getShootStartDate();
			Date shootEndDate = crewInfo.getShootEndDate();
			
			if (shootStartDate != null && shootEndDate != null) {
				allShootDays = DateUtils.daysBetween(shootStartDate, shootEndDate) + 1;
				if (allShootDays < 0) {
					allShootDays = null;
				}
			}
			
			//已拍摄天数的计算，已销场通告单的天数
			Map<String, Object> noticeConditionMap = new HashMap<String, Object>();
			noticeConditionMap.put("crewId", crewId);
			noticeConditionMap.put("canceledStatus", NoticeCanceledStatus.Canceled.getValue());
			List<NoticeInfoModel> canceledNoticeList = this.noticeService.queryManyByMutiCondition(noticeConditionMap, null);
			
			List<Date> noticeDateList = new ArrayList<Date>();
			for (NoticeInfoModel noticeInfo : canceledNoticeList) {
				Date noticeDate = noticeInfo.getNoticeDate();
				if (!noticeDateList.contains(noticeDate)) {
					noticeDateList.add(noticeDate);
				}
			}
			shootedDays = noticeDateList.size();
			
			//拍摄场数的计算
//			Map<String, Object> viewCountStatistic = this.viewInfoService.queryViewCountStatistic(crewId);
//			allViews = ((Long) viewCountStatistic.get("totalViewCount")).intValue();
//			if (viewCountStatistic != null && allViews != 0) {
//				shootedViews = ((BigDecimal) viewCountStatistic.get("finishedViewCount")).intValue();
//			}
			
			/*
			 * 气氛、内外景、文武戏的统计
			 */
//			int totalViewCount = 0;	//总场数
//			int shootedViewCount = 0;	//已拍摄场数
			int insideViewCount = 0;	//内戏场数
			int shootedInsideViewCount = 0;	//已拍摄内戏场数
			int outsideViewCount = 0;	//外戏场数，含有“外”的都是外戏，其他都是内戏
			int shootedOutsideViewCount = 0;	//已拍摄外戏场数
			int dayViewCount = 0;	//日戏场数
			int shootedDayViewCount = 0;	//已拍摄日戏场数
			int nightViewCount = 0;	//夜戏场数，含有“夜”的都是夜戏，其他都是日戏
			int shootedNightViewCount = 0;	//已拍摄夜戏场数
			int literateViewCount = 0;	//文戏场数
			int shootedLiterateViewCount = 0;	//已拍摄文戏场数
			int kungFuViewCount = 0;	//武戏场数，“文武”戏属于武戏
			int shootedKungFuViewCount = 0;	//已拍摄武戏场数
			
			List<Map<String, Object>> viewList = this.viewInfoService.queryViewList(crewId, null);
			for (Map<String, Object> viewInfo : viewList) {
				if (viewInfo.get("viewId") == null) {
					continue;
				}
				
				int shootStatus = (Integer) viewInfo.get("shootStatus");
				String site = (String) viewInfo.get("site");
				String atmosphereName = (String) viewInfo.get("atmosphereName");
				Integer viewType = (Integer) viewInfo.get("viewType");
				Double pageCount = viewInfo.get("pageCount") == null ? 0 : (Double) viewInfo.get("pageCount");
				
				boolean isFinished = false;
				
				if (shootStatus == ShootStatus.DeleteXi.getValue()) {
					continue;
				}
				
				allViews++;
				totalPageCount = BigDecimalUtil.add(totalPageCount, pageCount);
				if (shootStatus == ShootStatus.Finished.getValue()) {
					isFinished = true;
					shootedViews++;
					finishedPageCount = BigDecimalUtil.add(finishedPageCount, pageCount);
				}
				
				//内外景统计
				if (StringUtils.isBlank(site) || site.indexOf("外") == -1) {
					insideViewCount++;
					if (isFinished) {
						shootedInsideViewCount++;
					}
				} else {
					outsideViewCount++;
					if (isFinished) {
						shootedOutsideViewCount++;
					}
				}
				
				//气氛统计
				if (StringUtils.isBlank(atmosphereName) || atmosphereName.indexOf("夜") == -1) {
					dayViewCount++;
					if (isFinished) {
						shootedDayViewCount++;
					}
				} else {
					nightViewCount++;
					if (isFinished) {
						shootedNightViewCount++;
					}
				}
				
				//文武戏统计
				if (viewType == null || viewType == ViewType.TeXiao.getValue()) {
					literateViewCount++;
					if (isFinished) {
						shootedLiterateViewCount++;
					}
				} else {
					kungFuViewCount++;
					if (isFinished) {
						shootedKungFuViewCount++;
					}
				}
			}
			

			//按集次统计信息
			List<Map<String, Object>> seriesViewStatisticInfo = this.viewInfoService.querySeriesNoByCrewId(crewId);
			
			/*
			 * 拍摄情况计算
			 */
			double planDayPageCount = 0;	//计划日均完成页数
			double actualDayPageCount = 0;	//实际日均完成页数
			double needDayPageCount = 0;	//按原计划完成需日均拍摄页数
			double beforeDays = 0;	//当前已提前拍摄天数
			
			if(allShootDays != null && allShootDays != 0) {
				planDayPageCount = BigDecimalUtil.divide(totalPageCount, allShootDays,1);
			}
			if(shootedDays != 0) {
				actualDayPageCount = BigDecimalUtil.divide(finishedPageCount, shootedDays,1);
			}
			if (planDayPageCount != 0) {
				beforeDays = BigDecimalUtil.divide(finishedPageCount - planDayPageCount * shootedDays, planDayPageCount,1);
			}
			if((allShootDays - shootedDays) != 0) {
				needDayPageCount = BigDecimalUtil.divide(totalPageCount - finishedPageCount, allShootDays - shootedDays, 1);
			}
			
			resultMap.put("shootedDays", shootedDays);
			resultMap.put("allShootDays", allShootDays);
			resultMap.put("shootedViews", shootedViews);
			resultMap.put("allViews", allViews);
			
//			resultMap.put("totalViewCount", totalViewCount);
//			resultMap.put("shootedViewCount", shootedViewCount);
			resultMap.put("insideViewCount", insideViewCount);
			resultMap.put("outsideViewCount", outsideViewCount);
			resultMap.put("dayViewCount", dayViewCount);
			resultMap.put("nightViewCount", nightViewCount);
			resultMap.put("literateViewCount", literateViewCount);
			resultMap.put("kungFuViewCount", kungFuViewCount);
			
			resultMap.put("shootedInsideViewCount", shootedInsideViewCount);
			resultMap.put("shootedOutsideViewCount", shootedOutsideViewCount);
			resultMap.put("shootedDayViewCount", shootedDayViewCount);
			resultMap.put("shootedNightViewCount", shootedNightViewCount);
			resultMap.put("shootedLiterateViewCount", shootedLiterateViewCount);
			resultMap.put("shootedKungFuViewCount", shootedKungFuViewCount);
			
			resultMap.put("seriesViewStatisticInfo", seriesViewStatisticInfo);

			resultMap.put("planDayPageCount", planDayPageCount);
			resultMap.put("actualDayPageCount", actualDayPageCount);
			resultMap.put("needDayPageCount", needDayPageCount);
			resultMap.put("beforeDays", beforeDays);
			
			this.sysLogService.saveSysLogForApp(request, "查询剧组拍摄进度统计信息", userInfo.getClientType(), ViewInfoModel.TABLE_NAME, null, 0);
			
		} catch (IllegalArgumentException ie) {
			String msg = ie.getMessage();
			logger.error(msg, ie);
			throw new IllegalArgumentException(msg);
		} catch (Exception e) {
			String msg = "未知异常，获取拍摄进度失败";
			logger.error(msg, e);
			this.sysLogService.saveSysLogForApp(request, "查询剧组拍摄进度统计信息失败：" + e.getMessage(), userInfo.getClientType(), ViewInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException(msg);
		}
		return resultMap;
	}
	
	/**
	 * 获取拍摄地统计信息
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainShootStatisticInfo")
	public Object obtainShootStatisticInfo(HttpServletRequest request, String crewId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			
			List<Map<String, Object>> shootLocationStatisticList = this.viewInfoService.queryViewStatisticGroupByShootLocation(crewId);
			
			resultMap.put("shootLocationStatisticList", shootLocationStatisticList);
			
			this.sysLogService.saveSysLogForApp(request, "查询剧组拍摄地统计信息", userInfo.getClientType(), SceneViewInfoModel.TABLE_NAME, null, 0);
		} catch (IllegalArgumentException ie) {
			String msg = ie.getMessage();
			logger.error(msg, ie);
			throw new IllegalArgumentException(msg);
		} catch (Exception e) {
			String msg = "未知异常，获取拍摄进度失败";
			logger.error(msg, e);
			this.sysLogService.saveSysLogForApp(request, "查询剧组拍摄地统计信息失败：" + e.getMessage(), userInfo.getClientType(), SceneViewInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException(msg);
		}
		return resultMap;
	}
	
	/**
	 * 获取场景列表
	 * @param request
	 * @param crewId
	 * @param userId
	 * @param seriesNo	集次
	 * @param shootLocationId	拍摄地点ID
	 * @param viewRoleId	场景角色ID
	 * @param shootStatus	拍摄状态
	 * @param pageNo	当前页数
	 * @param pageSize	每页显示条数
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainViewList")
	public Object obtainViewList(HttpServletRequest request, String crewId, String userId, Integer seriesNo, 
			String shootLocationId, String viewRoleId, 
			Integer shootStatus, Integer pageNo, Integer pageSize) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			
			if (pageNo == null) {
				pageNo = 1;
			}
			if (pageSize == null) {
				pageSize = 20;
			}
			
			Page page = new Page();
			page.setPageNo(pageNo);
			page.setPagesize(pageSize);
			
			ViewFilter filter = new ViewFilter();
			filter.setSeriesNo(seriesNo);
			filter.setShootLocation(shootLocationId);
			filter.setRoles(viewRoleId);
			if (shootStatus != null && shootStatus == 0) {
				filter.setShootStatus("0,1");
			}
			if (shootStatus != null && shootStatus == 1) {
				filter.setShootStatus("2");
			}
			
			List<Map<String, Object>> viewInfoList = this.viewInfoService.queryViewInfoList(crewId, page, filter);
			if (viewInfoList != null) {
				for (Map<String, Object> viewInfoMap : viewInfoList) {
					String viewLocation = (String) viewInfoMap.get("majorView");
					String mainRoleNames = (String) viewInfoMap.get("mainRoleList");
					String guestRoleNames = (String) viewInfoMap.get("guestRoleList");
					String massRoleNames = (String) viewInfoMap.get("massRoleList");
					String clothesNames = (String) viewInfoMap.get("clothesName");
					String makeupNames = (String) viewInfoMap.get("makeupName");
					String propNames = (String) viewInfoMap.get("propsList");
					String specialPropNames = (String) viewInfoMap.get("specialPropsList");
					String insertAdverts = (String) viewInfoMap.get("advertName");
					
					
					viewInfoMap.put("viewLocation", viewLocation);
					viewInfoMap.put("mainRoleNames", mainRoleNames);
					viewInfoMap.put("guestRoleNames", guestRoleNames);
					viewInfoMap.put("massRoleNames", massRoleNames);
					viewInfoMap.put("clothesNames", clothesNames);
					viewInfoMap.put("makeupNames", makeupNames);
					viewInfoMap.put("propNames", propNames);
					viewInfoMap.put("specialPropNames", specialPropNames);
					viewInfoMap.put("insertAdverts", insertAdverts);
					
					viewInfoMap.remove("shootDate");
					viewInfoMap.remove("viewAddress");
					viewInfoMap.remove("viewAddressId");
					viewInfoMap.remove("minorView");
					viewInfoMap.remove("thirdLevelView");
					viewInfoMap.remove("roleList");
					viewInfoMap.remove("viewId");
					viewInfoMap.remove("season");
					viewInfoMap.remove("atmosphereId");
					viewInfoMap.remove("shootLocation");
					viewInfoMap.remove("shootLocationId");
					viewInfoMap.remove("shootStatus");
					viewInfoMap.remove("makeupId");
					viewInfoMap.remove("clothesId");
					viewInfoMap.remove("advertId");
					
					viewInfoMap.remove("majorView");
					viewInfoMap.remove("mainRoleList");
					viewInfoMap.remove("guestRoleList");
					viewInfoMap.remove("massRoleList");
					viewInfoMap.remove("clothesName");
					viewInfoMap.remove("makeupName");
					viewInfoMap.remove("propsList");
					viewInfoMap.remove("specialPropsList");
					viewInfoMap.remove("advertName");
					
				}
			}
			
			resultMap.put("viewList", viewInfoList);
			resultMap.put("pageCount", page.getPageCount());
			this.sysLogService.saveSysLogForApp(request, "获取场景列表", userInfo.getClientType(), ViewInfoModel.TABLE_NAME, null, 0);
		} catch (IllegalArgumentException ie) {
			String msg = ie.getMessage();
			logger.error(msg, ie);
			throw new IllegalArgumentException(msg);
		} catch (Exception e) {
			String msg = "未知异常，获取场景列表失败";
			logger.error(msg, e);
			this.sysLogService.saveSysLogForApp(request, "获取场景列表失败：" + e.getMessage(), userInfo.getClientType(), ViewInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException(msg);
		}
		return resultMap;
	}
}
