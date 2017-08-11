package com.xiaotu.makeplays.car.service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.car.dao.CarWorkDao;
import com.xiaotu.makeplays.car.model.CarWorkModel;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.ExcelUtils;
import com.xiaotu.makeplays.utils.Page;

/**
 * 加油信息
 * @author xuchangjian 2017-2-28下午2:05:42
 */
@Service
public class CarWorkService {

	@Autowired
	private CarWorkDao carWorkDao;
	
	private DecimalFormat df = new DecimalFormat("0.00");
	
	/**
	 * 根据多个条件查询加油信息信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<CarWorkModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.carWorkDao.queryManyByMutiCondition(conditionMap, page);
	}
	
	/**
	 * 根据车辆ID查询加油登记信息
	 * @param carId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryCarWorkByCarId(String carId) {
		return this.carWorkDao.queryCarWorkByCarId(carId);
	}
	
	/**
	 * 根据加油ID删除汽车加油记录
	 * @param workId
	 * @throws Exception 
	 */
	public void deleteById(String workId) throws Exception {
		this.carWorkDao.deleteOne(workId, "workId", CarWorkModel.TABLE_NAME);
	}
	
	/**
	 * 根据ID查询数据
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	public CarWorkModel queryById(String workId) throws Exception {
		return this.carWorkDao.queryById(workId);
	}
	
	/**
	 * 根据ID查询数据
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	public CarWorkModel queryCarWorkInfoById(String workId) throws Exception {
		return this.carWorkDao.queryById(workId);
	}
	
	/**
	 * 新增一条车辆加油信息
	 * @param carWorkInfo
	 * @throws Exception 
	 */
	public void addOne(CarWorkModel carWorkInfo) throws Exception {
		this.carWorkDao.add(carWorkInfo);
	}
	
	/**
	 * 更新一条车辆加油信息
	 * @param carWorkInfo
	 * @throws Exception
	 */
	public void updateOne(CarWorkModel carWorkInfo) throws Exception {
		this.carWorkDao.updateWithNull(carWorkInfo, "workId");
	}
	
	/**
	 * 查询剧组中单月的加油金额统计信息（按照天分组）
	 * @param crewId
	 * @param workMonth	格式：yyyy-MM
	 * @return 加油日期，加油金额
	 */
	public List<Map<String, Object>> queryMonthOilMoneyStatistic(String crewId, String workMonth) {
		return this.carWorkDao.queryMonthOilMoneyStatistic(crewId, workMonth);
	}
	

	
	/**
	 * 查询车辆加油升数和加油金额日累计信息
	 * 包括总计
	 * @param crewId 剧组ID
	 * @param startDate 查询条件-开始日期
	 * @param endDate 查询条件-结束日期
	 * @param searchCarNumber 查询条件-车牌号
	 * @return
	 */
	public Map<String, Object> queryCarOilMoneyInfo(String crewId, String startDate, String endDate, String searchCarNumber) {
		Map<String, Object> backMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultList = this.carWorkDao.queryCarOilMoneyInfo(crewId, startDate, endDate, searchCarNumber);
		List<Map<String, Object>> resultListNew = new ArrayList<Map<String,Object>>();
		Double sumMoney = 0.0;
		if(resultList != null && resultList.size() > 0) {
			String workDateFlag = "";
			Map<String, Object> one = null;
			Double dayTotalMoney = 0.0;
			List<Map<String, Object>> oneList = null;
			for(int i = 0; i < resultList.size(); i++) {
				Map<String, Object> map = resultList.get(i);
				Double totalMoney = Double.valueOf(map.get("totalMoney") + "");
				Double totalLiters = Double.valueOf(map.get("totalLiters") + "");
				map.put("totalMoney", df.format(totalMoney));
				map.put("totalLiters", df.format(totalLiters));
				String workDate = map.get("workDate") + "";
				if(!workDate.equals(workDateFlag)) {
					workDateFlag = workDate;
					if(i != 0) {
						one.put("dayTotalMoney", df.format(dayTotalMoney));
					}
					one = new HashMap<String, Object>();
					one.put("workDate", workDate);
					dayTotalMoney = 0.0;
					oneList = new ArrayList<Map<String,Object>>();
					one.put("carList", oneList);
					resultListNew.add(one);
				}
				dayTotalMoney = BigDecimalUtil.add(dayTotalMoney, totalMoney);
				oneList.add(map);
				
				if(i == resultList.size() - 1) {
					one.put("dayTotalMoney", df.format(dayTotalMoney));
				}
				
				sumMoney = BigDecimalUtil.add(sumMoney, totalMoney);
			}
		}
		backMap.put("totalMoney", df.format(sumMoney));
		backMap.put("resultList", resultListNew);
		return backMap;
	}
	
	/**
	 * 查询下拉框数据
	 * @param crewId
	 * @return
	 */
	public Map<String, Object> queryDropDownData(String crewId){
		Map<String, Object> back = new HashMap<String, Object>();
		List<String> dateList = carWorkDao.queryDropDownDataForDate(crewId);
		List<String> carNumberList = carWorkDao.queryDropDownDataForCarNumber(crewId);
		back.put("workDate", dateList);
		back.put("carNumber", carNumberList);
		return back;
	}
	
	/**
	 * 查询车辆加油统计信息并导出
	 * @param response
	 * @param searchDate
	 * @param searchCarNumber
	 * @param crewInfo
	 * @throws IOException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public void queryCarOilMoneyForExport(HttpServletResponse response,
			String startDate, String endDate, String searchCarNumber, CrewInfoModel crewInfo,
			Map<String, String> columnMap) throws IllegalArgumentException,
			IllegalAccessException, IOException {
		Map<String, Object> carOilMoneyInfo = this.queryCarOilMoneyInfo(crewInfo.getCrewId(), startDate, endDate, searchCarNumber);
		
		ExcelUtils.exportCarOilMoneyInfoForExcel(response, carOilMoneyInfo, columnMap, crewInfo.getCrewName());
	}
}
