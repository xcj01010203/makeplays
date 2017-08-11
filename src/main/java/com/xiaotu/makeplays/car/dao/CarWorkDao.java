package com.xiaotu.makeplays.car.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.car.model.CarWorkModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * @类名：CarWorkDao.java
 * @作者：李晓平
 * @时间：2016年12月19日 下午7:17:59
 * @描述：车辆加油登记表Dao
 */
@Repository
public class CarWorkDao extends BaseDao<CarWorkModel>{
	
	/**
	 * 根据多个条件查询加油信息信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<CarWorkModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + CarWorkModel.TABLE_NAME + " where 1 = 1 ");
		
		Set<String> keySet = conditionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		List<Object> conList = new LinkedList<Object>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = conditionMap.get(key);
			sql.append(" and " + key + " = ?");
			conList.add(value);
		}
		sql.append(" order by mileage, workDate ");
		Object[] objArr = conList.toArray();
		List<CarWorkModel> carWorkList = this.query(sql.toString(), objArr, CarWorkModel.class, page);
		
		return carWorkList;
	}
	
	/**
	 * 根据车辆ID查询加油登记信息
	 * @param carId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryCarWorkByCarId(String carId) {
		String sql = "select * from tab_car_work where carId=? order by mileage, workDate";
		return this.getJdbcTemplate().queryForList(sql, new Object[]{carId});
	}
	
	/**
	 * 根据ID查询数据
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	public CarWorkModel queryById(String workId) throws Exception {
		String sql = "select * from " + CarWorkModel.TABLE_NAME + " where workId = ?";
		return this.queryForObject(sql, new Object[] {workId}, CarWorkModel.class);
	}
	
	/**
	 * 查询剧组中单月的加油金额统计信息（按照天分组）
	 * @param crewId
	 * @param workMonth	格式：yyyy-MM
	 * @return 加油日期，加油金额
	 */
	public List<Map<String, Object>> queryMonthOilMoneyStatistic(String crewId, String workMonth) {
		String sql = "SELECT workDate, sum(oilMoney) oilMoney from tab_car_work where crewId = ? and DATE_FORMAT(workDate, '%Y-%m') = ? GROUP BY workDate";
		return this.query(sql, new Object[] {crewId, workMonth}, null);
	}
	
	/**
	 * 查询车辆加油升数和加油金额日累计信息
	 * @param crewId 剧组ID
	 * @param searchDate 查询条件-日期
	 * @param searchCarNumber 查询条件-车牌号
	 * @return
	 */
	public List<Map<String, Object>> queryCarOilMoneyInfo(String crewId, String startDate, String endDate                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    , String searchCarNumber) {
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		sql.append(" SELECT ");
		sql.append(" 	tcw.workDate, ");
		sql.append(" 	tcw.carId, ");
		sql.append(" 	tci.carNumber, ");
//		sql.append(" 	sum(tcw.kilometers) totalKilometers, ");
		sql.append(" 	ifnull(sum(tcw.oilLitres),0) totalLiters, ");
		sql.append(" 	ifnull(sum(tcw.oilMoney),0) totalMoney ");
		sql.append(" FROM ");
		sql.append(" 	tab_car_work tcw, ");
		sql.append(" 	tab_car_info tci ");
		sql.append(" WHERE ");
		sql.append(" 	tcw.carId = tci.carId ");
		sql.append(" AND tcw.crewId = ? ");
		params.add(crewId);
		
//		if(StringUtils.isNotBlank(searchDate)) {
//			String[] searchDateArray = StringUtils.split(searchDate, ",");
//			if(searchDateArray != null && searchDateArray.length > 0){
//				sql.append(" and date_format(tcw.workDate,'%Y-%m') in ( ");
//				boolean flag = false;
//				for(String sd : searchDateArray){
//					if(StringUtils.isNotBlank(sd)){
//						flag = true;
//						sql.append("?,");
//						params.add(sd);
//					}
//				}
//				if(flag){
//					sql.deleteCharAt(sql.length()-1);
//				}
//				sql.append(" ) ");
//			}
//		}
		if(StringUtils.isNotBlank(startDate)) {
			sql.append(" and tcw.workDate >= ? ");
			params.add(startDate);
		}
		if(StringUtils.isNotBlank(endDate)) {
			sql.append(" and tcw.workDate <= ? ");
			params.add(endDate);
		}
		if(StringUtils.isNotBlank(searchCarNumber)) {
			String[] searchCarNumberArray = StringUtils.split(searchCarNumber,",");
			if (searchCarNumberArray != null && searchCarNumberArray.length > 0) {
				sql.append(" and tci.carNumber in ( ");
				boolean flag = false;
				for(String sd :searchCarNumberArray){
					if(StringUtils.isNotBlank(sd)){
						flag = true;
						sql.append("?,");
						params.add(sd);
					}
				}
				if(flag){
					sql.deleteCharAt(sql.length()-1);
				}
				sql.append(" ) ");
			}
		}
		
		sql.append(" group by tcw.workDate,tcw.carId,tci.carNumber ");
		sql.append(" order by tcw.workDate,tci.carNumber ");
		
		return this.getJdbcTemplate().queryForList(sql.toString(), params.toArray());
	}
	
	/**
	 * 获取日期下拉框数据
	 * @param crewId
	 * @return
	 */
	public List<String> queryDropDownDataForDate(String crewId){
		String sql = "select distinct date_format(workDate,'%Y-%m') workDate from tab_car_work where crewId = ? order by workDate ";
		List<String> list = this.getJdbcTemplate().queryForList(sql, String.class, new Object[]{crewId});
		
		return list;
	}
	
	/**
	 * 获取车牌号下拉框数据
	 * @param crewId
	 * @return
	 */
	public List<String> queryDropDownDataForCarNumber(String crewId){
		String sql = "select distinct tci.carNumber from tab_car_work tcw, tab_car_info tci "
				+ " where tci.carId=tcw.carId and tcw.crewId = ? order by carNumber ";
		List<String> list = this.getJdbcTemplate().queryForList(sql, String.class,new Object[]{crewId});
		return list;
	}
}
