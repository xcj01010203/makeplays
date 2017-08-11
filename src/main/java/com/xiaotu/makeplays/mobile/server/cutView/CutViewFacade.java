package com.xiaotu.makeplays.mobile.server.cutView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.model.constants.CrewType;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.cutview.service.CutViewInfoService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.mobile.server.common.BaseFacade;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.DateUtils;
import com.xiaotu.makeplays.utils.StringUtils;
import com.xiaotu.makeplays.view.service.ViewInfoService;

/**
 * @类名：CutViewFacade.java
 * @作者：李晓平
 * @时间：2017年8月7日 下午2:06:32
 * @描述：app剪辑相关接口
 */
@Controller
@RequestMapping("/interface/cutViewFacade")
public class CutViewFacade extends BaseFacade{
	
	private Logger logger = LoggerFactory.getLogger(CutViewFacade.class);
	
	@Autowired
	private CrewInfoService crewInfoService;
	
	@Autowired
	private CutViewInfoService cutViewService;
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	/**
	 * 获取剧组剪辑基本设置和统计信息
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainCutViewStatistic")
	public Object obtainCutViewStatistic(HttpServletRequest request, String crewId, String userId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			CrewInfoModel crewInfo = crewInfoService.queryById(crewId);
			
			resultMap.put("lengthPerSet", crewInfo.getLengthPerSet());
			resultMap.put("cutRate", crewInfo.getCutRate());
			//剪辑生产进度信息
			resultMap.putAll(this.getCutViewProductionReport(crewInfo));
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取剧组剪辑基本设置和统计信息失败", e);
			throw new IllegalArgumentException("未知异常，获取剧组剪辑基本设置和统计信息失败", e);
		}
		
		return resultMap;
	}
	
	/**
	 * 更新剧组的每集时长和精剪比
	 * @param crewId
	 * @param userId
	 * @param lengthPerSet
	 * @param cutRate
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateCrewCutInfo")
	public Object updateCrewCutInfo(HttpServletRequest request, String crewId, String userId, Double lengthPerSet, Double cutRate){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		UserInfoModel userInfo = null;
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);

			//取出剧组对象
			CrewInfoModel crewInfo = crewInfoService.queryById(crewId);
			int crewType = crewInfo.getCrewType();
			if(crewType == CrewType.TVPlay.getValue() || crewType == CrewType.InternetTvplay.getValue()) {
				if(lengthPerSet == null || cutRate == null) {
					throw new IllegalArgumentException("请输入设置内容");
				}
			} else {
				if(cutRate == null) {
					throw new IllegalArgumentException("请输入设置内容");
				}
			}
			
			if (lengthPerSet != null) {
				crewInfo.setLengthPerSet(lengthPerSet);
			}
			
			if (cutRate != null) {
				crewInfo.setCutRate(cutRate);
			}
			
			//更新信息
			crewInfoService.updateCrew(crewInfo);
			
			this.sysLogService.saveSysLogForApp(request, "更新剧组的每集时长和精剪比", userInfo.getClientType(), UserInfoModel.TABLE_NAME, null, SysLogOperType.UPDATE.getValue());
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，更新剧组的每集时长和精剪比失败", e);
			this.sysLogService.saveSysLogForApp(request, "更新剧组的每集时长和精剪比失败：" + e.getMessage(), userInfo.getClientType(), UserInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，更新剧组的每集时长和精剪比失败", e);
		}
		
		return resultMap;
	}
	
	/**
	 * 对剪辑生产报表进行补充，
	 * @param scheduleList
	 * @throws ParseException 
	 */
	private Map<String, Object> getCutViewProductionReport(CrewInfoModel crewInfo) throws ParseException {
		Map<String, Object> resultMap = new HashMap<String, Object>(); 
		DecimalFormat df = new DecimalFormat("0.00");
		List<Map<String, Object>> scheduleList = this.cutViewService.queryCutViewStatisticInfo(crewInfo.getCrewId());
		//场景总体信息
		Map<String, Object> viewTotal = this.viewInfoService.queryViewTotalInfo(crewInfo.getCrewId());
		double viewTotalPageCount = Double.parseDouble(viewTotal.get("totalPageCount") + "");
		int viewTotalSeriesNo = Integer.parseInt(viewTotal.get("totalSeriesNo") + "");
		
		//查询每集剪辑分钟数（如果为电影剧本，则是查询每场剪辑分钟数）
		List<Map<String, Object>> preSeriesNoCutList = this.cutViewService.queryPreSeriesNoCutInfo(crewInfo.getCrewId());
		
		//查询每日剪辑量
		List<Map<String, Object>> cutlengthList = this.cutViewService.queryPreDayStatisticData(crewInfo.getCrewId());
		//预计粗剪集数
		Double expectCrudeCutSeriesnos = 0.0;
		//预计精剪集数
		Double expectCarefulCutSeriesnos = 0.0;
		//日均剪辑页数
		Double cutPageAvgDays = 0.0;
		//日均剪辑分钟数
		Double cutMinutesAcgDys = 0.0;
		//累计拍摄页数
		Double finalShootPage = 0.0;
		//总的剪辑页数
		Double finalCutPage = 0.0;
		//最大已剪辑集数
		Double maxFinishCutviewCount = 0.0;
		//最大粗剪分钟数
		Double maxExpectCutMinutes = 0.0;
		//最大精剪分钟数
		Double maxCarefulCutMinutes = 0.0;
		
		int totalCutDays = 0;
		//查询剪辑总天数
		List<Map<String, Object>> list = this.cutViewService.queryTotalCutDays(crewInfo.getCrewId());
		if (list != null && list.size()>0) {
			Map<String, Object> map = list.get(0);
			if (map.get("cutDays") != null) {
				totalCutDays = Integer.parseInt(map.get("cutDays")+"");
			}
		}
		
		//拍摄日期
		Date shootStartDate = crewInfo.getShootStartDate();
		Date shootEndDate = crewInfo.getShootEndDate();
		if(scheduleList != null && scheduleList.size() > 0) {
			int totalViewCount = 0; //累计完成场数
			int dayNum = 0;
			double totalPageCount = 0; //累计完成页数
			double finishSeriesno = 0; //完成集数
			double totalCutPage = 0;//累计剪辑页数
			double totalCutMinutes = 0;//累计分钟数
			
			boolean flag = false; //标识是否设置了拍摄日期，false：没有
			int shootDate = 0;
			double avgEveryDayPageCount = 0; //平均每日需完成
			if(StringUtils.isNotBlank(shootStartDate + "") && StringUtils.isNotBlank(shootEndDate + "")) {
				flag = true;
				shootDate = DateUtils.daysBetween(shootStartDate, shootEndDate) + 1;

				resultMap.put("shootDate", shootDate);
				avgEveryDayPageCount = viewTotalPageCount / shootDate;
				resultMap.put("everyDayPageCount", df.format(avgEveryDayPageCount));
			}
			//每集页数
			double everySeriesPageCount = viewTotalPageCount / viewTotalSeriesNo;
			for(int i = 0; i < scheduleList.size(); i++) {
				Map<String, Object> oneSchedule = scheduleList.get(i);
				dayNum++;
				oneSchedule.put("dayNum", dayNum); //天数
				//页数
				oneSchedule.put("planPageCount", df.format(oneSchedule.get("planPageCount")));
				oneSchedule.put("realPageCount", df.format(oneSchedule.get("realPageCount")));
				
				//剪辑页数
				double cutPage = Double.parseDouble(oneSchedule.get("realCutPage") + "");
						
				double pageCount = Double.parseDouble(oneSchedule.get("realPageCount") + "");
				if(flag) {
					//每日需完成
					double everyDayPageCount = (viewTotalPageCount - totalPageCount) / shootDate;
					if(shootDate == 0) {
						everyDayPageCount = viewTotalPageCount - totalPageCount;
					}
					oneSchedule.put("everyDayPageCount", df.format(everyDayPageCount)); //每日需完成
					shootDate--;
					//差天数
					double everyDayCha = (pageCount - avgEveryDayPageCount) / avgEveryDayPageCount; //每天差额			
//					double everyDayCha = (pageCount - everyDayPageCount) / everyDayPageCount; //每天差额
					oneSchedule.put("everyDayCha", df.format(everyDayCha));
				}
				
				//累计完成
				totalPageCount = BigDecimalUtil.add(totalPageCount, pageCount);
				oneSchedule.put("totalPageCount", df.format(totalPageCount)); //累计完成页数
				
				//平均完成
				oneSchedule.put("avgPageCount", df.format(totalPageCount / dayNum)); //平均完成页数	
				
				//累计剪辑页数
				totalCutPage = BigDecimalUtil.add(totalCutPage, cutPage);
				oneSchedule.put("totalCutPage", df.format(totalCutPage));
				
				//未剪辑页数
				oneSchedule.put("unCutPage", df.format(BigDecimalUtil.subtract(totalPageCount, totalCutPage)));
				
				//剪辑分钟数
				double cutMinutes = 0.0;
				if (oneSchedule.get("cutMinutes") != null) {
					Object object = oneSchedule.get("cutMinutes");
					cutMinutes = Double.parseDouble(object.toString()); 
				}
				cutMinutes = cutMinutes / 60;
				oneSchedule.put("cutMinutes", df.format(cutMinutes));
				
				//累计分钟数
				totalCutMinutes = BigDecimalUtil.add(totalCutMinutes, cutMinutes);
				oneSchedule.put("totalCutMinutes", df.format(totalCutMinutes));
				
				//取出用户定义的每集时长，如果没定义默认为 43分钟
				Double lengthPerSet = crewInfo.getLengthPerSet();
				if (lengthPerSet == null || lengthPerSet == 0.0) {
					lengthPerSet = 43.0;
				}
				//已剪辑集数
				double finishCutviewCount = BigDecimalUtil.divide(totalCutMinutes, lengthPerSet);
				oneSchedule.put("finishCutviewCount", df.format(finishCutviewCount));
				if (finishCutviewCount > maxFinishCutviewCount) {
					maxFinishCutviewCount = finishCutviewCount;
				}
				
				//页数分钟比
				double pageMinuteRate = 0;
				if (totalCutPage != 0) {
					pageMinuteRate = BigDecimalUtil.divide(totalCutMinutes, totalCutPage);
				}
				oneSchedule.put("pageMinuteRate", df.format(pageMinuteRate));
				
				//预计粗剪分钟数
				double expectCutMinutes = pageMinuteRate * viewTotalPageCount;
				oneSchedule.put("expectCutMinutes", df.format(expectCutMinutes)); 
				
				//预计粗剪集数
				double crudeCutSeriesnos = BigDecimalUtil.divide(expectCutMinutes, lengthPerSet);
				oneSchedule.put("crudeCutSeriesnos", df.format(crudeCutSeriesnos));
				
				//取出用户自定义的精剪比
				Double cutRate = crewInfo.getCutRate();
				if (cutRate == null) {
					cutRate = 0.9;
				} 
				//预计精剪集数
				double carefulCutSeriesnos = BigDecimalUtil.multiply(crudeCutSeriesnos, cutRate);
				oneSchedule.put("carefulCutSeriesnos", df.format(carefulCutSeriesnos));
				
				//预计精剪分钟数
				double expectCarefulCutMinutes = expectCutMinutes * cutRate;
				oneSchedule.put("expectCarefulCutMinutes", df.format(expectCarefulCutMinutes));
				
				totalViewCount += Integer.parseInt(oneSchedule.get("realViewCount") + "");
				oneSchedule.put("totalViewCount", totalViewCount); //累计完成场数
				//完成集数
				finishSeriesno += (pageCount / everySeriesPageCount);
				oneSchedule.put("finishSeriesno", df.format(finishSeriesno));
				//最大一条数据
				if (crudeCutSeriesnos >= expectCrudeCutSeriesnos) {
					expectCrudeCutSeriesnos = crudeCutSeriesnos;
				}
				if (carefulCutSeriesnos >= expectCarefulCutSeriesnos) {
					expectCarefulCutSeriesnos = carefulCutSeriesnos;
				}
				
				if (expectCutMinutes >= maxExpectCutMinutes) {
					maxExpectCutMinutes = expectCutMinutes;
					maxCarefulCutMinutes = maxExpectCutMinutes * cutRate;
				}
				if (i == scheduleList.size() -1) {
					
					//计算日均剪辑页数
					if (totalCutDays == 0) {
						cutPageAvgDays = 0.0;
					}else {
						cutPageAvgDays = BigDecimalUtil.divide(totalCutPage, totalCutDays);
					}
					
					//计算日均分钟数
					if (totalCutDays == 0) {
						cutMinutesAcgDys = 0.0;
					}else {
						cutMinutesAcgDys = BigDecimalUtil.divide(totalCutMinutes, totalCutDays);
					}
					//累计拍摄页数
					finalShootPage = totalPageCount;
					
					//总剪辑页数
					finalCutPage = totalCutPage;
				}
			}
			resultMap.put("flag", flag);
			resultMap.put("totalCutMinutes", df.format(totalCutMinutes));
		}
//		resultMap.put("resultList", scheduleList);
		resultMap.put("cutPageAvgDays", df.format(cutPageAvgDays));
		resultMap.put("cutMinutesAcgDys", df.format(cutMinutesAcgDys));
		resultMap.put("expectCrudeCutSeriesnos", df.format(expectCrudeCutSeriesnos));
		resultMap.put("expectCarefulCutSeriesnos", df.format(expectCarefulCutSeriesnos));
		resultMap.put("viewTotalViewCount", Integer.parseInt(viewTotal.get("totalViewCount") + ""));
		resultMap.put("viewTotalPageCount", df.format(viewTotalPageCount));
		resultMap.put("finalShootPage", df.format(finalShootPage));
		resultMap.put("finalCutPage", df.format(finalCutPage));
		resultMap.put("maxFinishCutviewCount", df.format(maxFinishCutviewCount));
//		resultMap.put("preSeriesNoCutList", preSeriesNoCutList);
		resultMap.put("maxExpectCutMinutes", df.format(maxExpectCutMinutes));
		resultMap.put("maxCarefulCutMinutes", df.format(maxCarefulCutMinutes));
//		resultMap.put("preDayCutLength", cutlengthList);
		
		return resultMap;
	}
}
