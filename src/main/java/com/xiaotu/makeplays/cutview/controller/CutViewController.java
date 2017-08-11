package com.xiaotu.makeplays.cutview.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.cutview.service.CutViewInfoService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.Page;

/**
 * 场景剪辑的controller
 * @author wanrenyi 2017年6月15日下午2:17:33
 */
@Controller
@RequestMapping("/cutViewManager")
public class CutViewController extends BaseController{
	
	Logger logger =  LoggerFactory.getLogger(CutViewController.class);
	
	private final int terminal = Constants.TERMINAL_PC;

	@Autowired
	private CutViewInfoService cutViewService;
	
	/**
	 * 跳转到场景剪辑界面
	 * @param request
	 * @return
	 */
	@RequestMapping("/toCutViewListPage")
	public ModelAndView toCutViewListPage(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("/cutview/cutViewList");
		return mv;
	}
	
	/**
	 * 保存剪辑信息
	 * @param request
	 * @param viewId
	 * @param id
	 * @param cutLength
	 * @param cutDateStr
	 * @param viewId
	 * @param remark
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveCutViewInfo")
	public Map<String, Object> saveCutViewInfo(HttpServletRequest request, String id, String cutDataStr){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(cutDataStr)) {
				throw new IllegalArgumentException("请选择要剪辑的场景！");
			}
			
			String crewId = this.getCrewId(request);
			
			String cutId = this.cutViewService.saveCutViewInfo( id, cutDataStr, crewId);
			
			message = "保存成功";
			resultMap.put("cutId", cutId);
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，保存失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 查询剪辑列表（支持高级查询）
	 * @param request
	 * @param shootStartDate
	 * @param shootEndDate
	 * @param startSeriesNo
	 * @param startViewNo
	 * @param endSeriesNo
	 * @param endViewNo
	 * @param satrtShootPage
	 * @param endShootPage
	 * @param startCutLength
	 * @param endCutLength
	 * @param startCutDate
	 * @param endCutDate
	 * @param isASc
	 * @param isAll
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryCutViewList")
	public Map<String, Object> queryCutViewList(HttpServletRequest request, String shootStartDate, String shootEndDate, Integer startSeriesNo,
				String startViewNo, Integer endSeriesNo, String endViewNo, Double satrtShootPage, Double endShootPage,Long startCutLength,
				Long endCutLength, String startCutDate, String endCutDate, boolean isASc, boolean isAll, Page page){
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("shootStartDate", shootStartDate);
			conditionMap.put("shootEndDate", shootEndDate);
			conditionMap.put("startSeriesNo", startSeriesNo);
			conditionMap.put("startViewNo", startViewNo);
			conditionMap.put("endSeriesNo", endSeriesNo);
			conditionMap.put("endViewNo", endViewNo);
			conditionMap.put("satrtShootPage", satrtShootPage);
			conditionMap.put("endShootPage", endShootPage);
			conditionMap.put("startCutLength", startCutLength);
			conditionMap.put("endCutLength", endCutLength);
			conditionMap.put("startCutDate", startCutDate);
			conditionMap.put("endCutDate", endCutDate);
			
			List<Map<String, Object>> cutViewList = this.cutViewService.queryCutViewList(crewId, conditionMap, isAll, isASc, page);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			//返回数据
			Map<String, Object> totalMap = new LinkedHashMap<String, Object>();
			Map<String, Object> tempMap = new LinkedHashMap<String, Object>();
			
			//对数据进行封装
			for (Map<String, Object> listMap : cutViewList) {
				//先找出拍摄日期相同的记录
				String shootDate = sdf.format(listMap.get("noticeDate"));
				//如果返回结果中不包含当前日期，则新建一个数据列表
				if (!tempMap.containsKey(shootDate)) {
					List<Map<String, Object>> shootDateList = new ArrayList<Map<String,Object>>();
					shootDateList.add(listMap);
					tempMap.put(shootDate, shootDateList);
				}else {
					List<Map<String, Object>> shootDateList = (List<Map<String, Object>>) tempMap.get(shootDate);
					shootDateList.add(listMap);
				}
				
			}
			
			//每个日期对应的数据长度
			Map<String, Object> dateLengthMap = new HashMap<String, Object>();
			//定义返回数据列表
			//遍历返回结果，将同一个分组下的放在一起
			for (Map.Entry<String, Object> entry : tempMap.entrySet()) {
				List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
				
				//取出拍摄日期
				String shootDate = entry.getKey();
				//取出对应的集合
				List<Map<String, Object>> shootDateList = (List<Map<String, Object>>) entry.getValue();
				
				Map<String, Object> nameMap = new LinkedHashMap<String, Object>();
				for (Map<String, Object> dateMap : shootDateList) {
					//定义分组的map
					//取出分组
					String groupName = (String) dateMap.get("groupName");
					if (!nameMap.containsKey(groupName)) {
						List<Map<String, Object>> groupList = new ArrayList<Map<String,Object>>();
						groupList.add(dateMap);
						nameMap.put(groupName, groupList);
					}else {
						List<Map<String, Object>> groupList = (List<Map<String, Object>>) nameMap.get(groupName);
						groupList.add(dateMap);
					}
				}
				dateLengthMap.put(shootDate, shootDateList.size());
				resultList.add(nameMap);
				totalMap.put(shootDate, resultList);
			}
			
			resultMap.put("dateLength", dateLengthMap);
			resultMap.put("data", totalMap);
			resultMap.put("totalPage", page.getPageCount());
			
		} catch (Exception e) {
			message = "未知错误，查询失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	
	/**
	 * 查询剪辑统计数据（支持高级查询）
	 * @param request
	 * @param shootStartDate
	 * @param shootEndDate
	 * @param startSeriesNo
	 * @param startViewNo
	 * @param endSeriesNo
	 * @param endViewNo
	 * @param satrtShootPage
	 * @param endShootPage
	 * @param startCutLength
	 * @param endCutLength
	 * @param startCutDate
	 * @param endCutDate
	 * @param isASc
	 * @param isAll
	 * @param page
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryStatisticsInfo")
	public Map<String, Object> queryStatisticsInfo(HttpServletRequest request, String shootStartDate, String shootEndDate, Integer startSeriesNo,
			String startViewNo, Integer endSeriesNo, String endViewNo, Double satrtShootPage, Double endShootPage,Long startCutLength,
			Long endCutLength, String startCutDate, String endCutDate, boolean isAll){

		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		DecimalFormat df = new DecimalFormat("0.00");
		
		try {
			String crewId = this.getCrewId(request);
			
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("shootStartDate", shootStartDate);
			conditionMap.put("shootEndDate", shootEndDate);
			conditionMap.put("startSeriesNo", startSeriesNo);
			conditionMap.put("startViewNo", startViewNo);
			conditionMap.put("endSeriesNo", endSeriesNo);
			conditionMap.put("endViewNo", endViewNo);
			conditionMap.put("satrtShootPage", satrtShootPage);
			conditionMap.put("endShootPage", endShootPage);
			conditionMap.put("startCutLength", startCutLength);
			conditionMap.put("endCutLength", endCutLength);
			conditionMap.put("startCutDate", startCutDate);
			conditionMap.put("endCutDate", endCutDate);
			
			Map<String, Object> statisticsMap = new HashMap<String, Object>();
			List<Map<String,Object>> list = this.cutViewService.queryCutViewTotalDataInfo(crewId, conditionMap, isAll);
			if (list != null && list.size()>0) {
				statisticsMap = list.get(0);
				//计算剪辑总时长
				double totalCutTimes = 0;
				Object  bd = statisticsMap.get("totalCutTimes");
				if (bd != null) {
					totalCutTimes = Double.parseDouble(bd.toString());
				}
				statisticsMap.put("totalCutTimes", df.format(totalCutTimes/60));
			}

			resultMap.put("statisticsInfo", statisticsMap);
			
		} catch (Exception e) {
			message = "未知错误，查询失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	
	}
	
	/**
	 * 更新剪辑状态
	 * @param request
	 * @param id
	 * @param cutStatus
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateCutViewStatus")
	public Map<String, Object> updateCutViewStatus(HttpServletRequest request, String id, boolean cutStatus){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(id)) {
				throw new IllegalArgumentException("请选择要修改的剪辑场次");
			}
			
			this.cutViewService.updateCutViewStatus(id, cutStatus);
			
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，更新失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
}
