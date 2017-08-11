package com.xiaotu.makeplays.car.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.car.model.CarInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.StringUtil;

/**
 * @类名：CarInfoDao.java
 * @作者：李晓平
 * @时间：2016年12月19日 下午7:17:59
 * @描述：车辆管理Dao
 */
@Repository
public class CarInfoDao extends BaseDao<CarInfoModel>{
	
	/**
	 * 查询所有的车辆信息
	 * @return
	 */
	public List<Map<String, Object>> queryAllCarInfo(String crewId, String searchCarNo) {
		StringBuffer sql = new StringBuffer();
		List<String> param = new ArrayList<String>();
		sql.append(" select tci.*,sum(oilMoney) totalMoney,sum(kilometers) totalMiles, ");
		sql.append(" sum(oilLitres) totalOil,round(sum(oilLitres)/sum(kilometers)*100, 2) oilConsume ");
		sql.append(" from tab_car_info tci ");
		sql.append(" left join tab_car_work tcw on tci.carId=tcw.carId ");
		sql.append(" where tci.crewId=?");
		param.add(crewId);
		if (StringUtils.isNotBlank(searchCarNo)) {
			sql.append(" and tci.carNumber like ?");
			param.add("%" + searchCarNo + "%");
		}
		sql.append(" group by tci.carId ");
		sql.append(" order by tci.sequence,tci.carNo ");
		return this.query(sql.toString(), param.toArray(), null);
	}
	
	/**
	 * 查询单个车辆信息
	 * @param carId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryById(String carId) throws Exception {
		String sql = "select tci.*,sum(oilMoney) totalMoney,sum(kilometers) totalMiles,sum(oilLitres) totalOil" 
				+ " from tab_car_info tci left join tab_car_work tcw" 
				+ " on tci.carId=tcw.carId where tci.carId=? group by tci.carId";
		List<Map<String, Object>> list = this.query(sql, new Object[]{carId}, null);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 根据ID查询车辆信息
	 * @param carId
	 * @return
	 * @throws Exception 
	 */
	public CarInfoModel queryCarInfoById(String carId) throws Exception {
		String sql = "select * from " + CarInfoModel.TABLE_NAME + " where carId = ?";
		return this.queryForObject(sql, new Object[] {carId}, CarInfoModel.class);
	}
	
	/**
	 * 查询最大的车辆编号
	 * @param crewId
	 * @return
	 * @throws Exception
	 */
	public int queryMaxCarNo(String crewId) throws Exception {
		String sql = "select max(carNo) carNo from tab_car_info where crewId=?";
		List<Map<String, Object>> list = this.query(sql, new Object[]{crewId}, null);
		int carNo = 0;
		if(list != null && list.size() > 0) {
			if(StringUtil.isNotBlank(list.get(0).get("carNo") + "")) {
				carNo = Integer.parseInt(list.get(0).get("carNo") + "");
			}
		}
		return carNo;
	}
	
	/**
	 * 根据车辆编号查询车辆
	 * @param carNo
	 * @param carId
	 * @param crewId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryByCarNo(int carNo, String carId, String crewId) {
		List<Object> params = new ArrayList<Object>();
		params.add(carNo);
		params.add(crewId);
		String sql = "select * from tab_car_info where carNo=? and crewId=?";
		if(StringUtil.isNotBlank(carId)) {
			sql += " and carId != ?";
			params.add(carId);
		}
		return this.query(sql, params.toArray(), null);
	}
	
	/**
	 * 根据车牌号查找车辆信息
	 * @param carNumber
	 * @param crewId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryByCarNumber(String carNumber, String crewId, String carId ){
		List<Object> params = new ArrayList<Object>();
		params.add(carNumber);
		params.add(crewId);
		String sql = "select * from tab_car_info where carNumber=? and crewId=?";
		if(StringUtil.isNotBlank(carId)) {
			sql += " and carId != ?";
			params.add(carId);
		}
		return this.query(sql, params.toArray(), null);
	}
	
	/**
	 * 查询剧组中所有车辆列表，带有该车辆在指定日的加油信息
	 * @param crewId
	 * @param workDate
	 * @return 车辆基本信息，车辆加油信息
	 */
	public List<Map<String, Object>> queryWithDayWorkInfo(String crewId, String workDate) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tci.carId, ");
		sql.append(" 	tci.carNo, ");
		sql.append(" 	tci.carModel, ");
		sql.append(" 	tci.carNumber, ");
		sql.append(" 	tci.`status`, ");
		sql.append(" 	round(sum(tcw.oilMoney), 2) oilMoney ");
		sql.append(" FROM ");
		sql.append(" 	tab_car_info tci ");
		sql.append(" LEFT JOIN tab_car_work tcw ON tcw.carId = tci.carId ");
		sql.append(" AND tcw.workDate = ? ");
		sql.append(" WHERE ");
		sql.append(" 	tci.crewId = ? ");
		sql.append(" GROUP BY tci.carId, tci.carNo, tci.carModel, tci.carNumber, tci.status ");
		sql.append(" ORDER BY tci.carNo ");
		
		return this.query(sql.toString(), new Object[] {workDate, crewId}, null);
	}
	
	/**
	 * 查询系统中的部门
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryDepartmentInfo(String crewId){
		String sql = "	SELECT * FROM tab_sysrole_info WHERE (crewId = ? OR crewId = '0' ) and parentId = '00' ORDER BY roleId";
		return this.query(sql, new Object[] {crewId}, null);
	}
	
	/**
	 * 查询系统中的部门
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryDepartmentByName(String departmentName, String crewId){
		String sql = "	SELECT * FROM tab_sysrole_info WHERE roleName = ? ";
		return this.query(sql, new Object[] {departmentName}, null);
	}
	
	/**
	 * 更新车辆信息序号
	 * @param sequence
	 * @param carId
	 */
	public void updateCarSequence (Integer sequence, String carId) {
		String sql = " UPDATE tab_car_info SET sequence  = ? where carId = ?";
		this.getJdbcTemplate().update(sql, sequence, carId);
	}
}
