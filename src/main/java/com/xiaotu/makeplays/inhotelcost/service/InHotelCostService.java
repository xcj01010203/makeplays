package com.xiaotu.makeplays.inhotelcost.service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.inhotelcost.controller.filter.InHotelCostFilter;
import com.xiaotu.makeplays.inhotelcost.dao.InhotelCostInfoDao;
import com.xiaotu.makeplays.inhotelcost.model.InhotelCostModel;
import com.xiaotu.makeplays.utils.ExcelUtils;
import com.xiaotu.makeplays.utils.Page;

/**
 * @ClassName InHotelCostService
 * @Description 住宿费用信息
 * @author Administrator
 * @Date 2017年1月4日 下午5:05:00
 * @version 1.0.0
 */
@Service
public class InHotelCostService {
	
	@Autowired
	private InhotelCostInfoDao inhotelCostInfoDao;
	
	
	
	
	/**
	 * @Description 查询住宿费用信息
	 * @param request
	 * @return
	 */
	public Map<String, Object> queryInHotelCostInfo(String crewId,String startDate,String endDate,String hotelName){
		Map<String, Object> back = new HashMap<String, Object>();
		List<Map<String, Object>> list = inhotelCostInfoDao.queryInHotelCostInfo(crewId,startDate,endDate,hotelName);
		if(list!=null &&list.size()>0){
			DecimalFormat    df   = new DecimalFormat("######0.00");  
			Double sumCost = new Double("0.00");
			for(Map<String, Object> temp:list){
				String sumprice = temp.get("sprice")!=null?temp.get("sprice").toString():"0";
				String avgprice = temp.get("aprice")!=null?temp.get("aprice").toString():"0";
				sumCost = sumCost+Double.valueOf(sumprice);
				temp.put("sprice", sumprice);
				temp.put("aprice", avgprice);
			}
			back.put("sumCost", df.format(sumCost));
		}
		back.put("inHotelCostInfoList", list);
		return back;
	}
	
	public Map<String, Object> queryDrowData(String crewId){
		Map<String, Object> back = new HashMap<String, Object>();
		List<String> dateList = inhotelCostInfoDao.queryDrowDataForDate(crewId);
		List<String> hotelNameList = inhotelCostInfoDao.queryDrowDataForHotelName(crewId);
		back.put("showDate", dateList);
		back.put("hotelName", hotelNameList);
		return back;
	}
	
	
	/**
	 * @Description 查询某一天 某个宾馆入住详情
	 * @param request
	 * @return
	 */
	public List<Map<String, Object>> queryInHotelCostDetailInfo(String hotelName,String checkInDate,String crewId){
		return inhotelCostInfoDao.queryInHotelCostDetailInfo(hotelName,checkInDate,crewId);
	}
	
	/**
	 * @Description 导出入住费用信息
	 * @param request
	 * @return
	 * @throws IOException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public void queryInHotelCostDetailInfoForExport(HttpServletResponse httpServletResponse,String hotelName,String startDate,String endDate,String crewId,String crewName) throws IllegalArgumentException, IllegalAccessException, IOException{
		Map<String, Object> mainInfoList = this.queryInHotelCostInfo(crewId,startDate,endDate,hotelName);
		List<Map<String, Object>> detailInfoList = inhotelCostInfoDao.queryInHotelCostDetailInfoForExport(hotelName,startDate,endDate,crewId);
		ExcelUtils.exportInHotelCostInfoForExcel(mainInfoList,detailInfoList,httpServletResponse,crewName);
	}
	
	/**
	 * 从入住信息中同步数据
	 * @param crewId
	 */
	public void syncFromHotelInfo(String crewId) {
		this.inhotelCostInfoDao.syncFromHotelInfo(crewId);
	}
	
	/**
	 * 查询入住费用中入住时间列表（去重）
	 * @param crewId
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> queryDistinctShowDateList(String crewId, InHotelCostFilter filter, Page page) {
		return this.inhotelCostInfoDao.queryDistinctShowDateList(crewId, filter, page);
	}
	
	/**
	 * 根据高级查询条件查询住宿费用
	 * @param crewId
	 * @param filter
	 * @return
	 */
	public List<InhotelCostModel> queryByAdvanceCondition(String crewId, InHotelCostFilter filter) {
		return this.inhotelCostInfoDao.queryByAdvanceCondition(crewId, filter);
	}
}
