package com.xiaotu.makeplays.hotelInfo.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.hotelInfo.controller.filter.HotelInfoFilter;
import com.xiaotu.makeplays.hotelInfo.model.HotelInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 宾馆信息操作的dao
 * @author wanrenyi 2017年3月14日下午4:45:02
 */
@Repository
public class HotelInfoDao extends BaseDao<HotelInfoModel>{

	/**
	 * 查询宾馆信息列表
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryHotelInfoList(String crewId, HotelInfoFilter filter){
		List<Object> paramsList = new ArrayList<Object>();
		
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ");
		sql.append(" 	thi.id, ");
		sql.append(" 	thi.hotelName, ");
		sql.append(" 	thi.hotelAddress, ");
		sql.append(" 	COUNT(DISTINCT tchi.roomNo) roomNumber, ");
		sql.append(" 	MIN(tchi.checkInDate) minCheckInDate, ");
		sql.append(" 	MAX(tchi.checkoutDate) maxCheckOutDate, ");
		sql.append(" 	COUNT(DISTINCT tchi.peopleName) peopleCount, ");
		sql.append("	count(distinct tchi.roomNo) checkinRoomNumber ");	//实际住的房间数
		sql.append(" FROM ");
		sql.append(" 	tab_hotel_info thi ");
		sql.append(" LEFT JOIN tab_checkIn_hotel_info tchi ON tchi.hotelId = thi.id ");
		sql.append(" AND tchi.crewId = ? ");
		paramsList.add(crewId);
		sql.append(" WHERE ");
		sql.append(" 	thi.crewId = ? ");
		paramsList.add(crewId);
		if (!StringUtils.isBlank(filter.getHotelNames())) {
			sql.append(" and thi.hotelName in ( ");
			String[] hotelNameArray = filter.getHotelNames().split(",");
			for (String hotelName : hotelNameArray) {
				sql.append("?,");
				paramsList.add(hotelName);
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(" ) ");
		}
		if (!StringUtils.isBlank(filter.getPeopleName())) {
			sql.append(" and tchi.peopleName like ? ");
			paramsList.add("%" + filter.getPeopleName() + "%");
		}
		sql.append(" GROUP BY ");
		sql.append(" 	thi.id, ");
		sql.append(" 	thi.hotelName, ");
		sql.append(" 	thi.hotelAddress, ");
		sql.append(" 	thi.roomNumber ");
		sql.append(" HAVING 1 = 1 ");
		if (!StringUtils.isBlank(filter.getStartDate())) {
			sql.append(" and MAX(tchi.checkoutDate) >= ? ");
			paramsList.add(filter.getStartDate());
		}
		if (!StringUtils.isBlank(filter.getEndDate())) {
			sql.append(" and MIN(tchi.checkInDate) <= ? ");
			paramsList.add(filter.getEndDate());
		}
		sql.append(" ORDER BY ");
		sql.append(" 	thi.createTime ");
		
		return this.query(sql.toString(), paramsList.toArray(), null);
	}
	
	/**
	 * 根据宾馆id查询出宾馆信息
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public HotelInfoModel queryById(String id) throws Exception {
		String sql = " select * from "+ HotelInfoModel.TABLE_NAME + " where id = ?";
		return this.queryForObject(sql, new Object[] {id}, HotelInfoModel.class);
	}
	
	/**
	 * 根据多个条件查询宾馆信息
	 * @param conditionMap
	 * @return
	 */
	public List<HotelInfoModel> queryByMuitiCondition(Map<String, Object> conditionMap){
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from "+ HotelInfoModel.TABLE_NAME +" where 1 = 1 ");
		
		Set<String> keySet = conditionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		List<Object> conList = new LinkedList<Object>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = conditionMap.get(key);
			sql.append(" and " + key + " = ?");
			conList.add(value);
		}
		sql.append(" order by createTime desc ");
		
		Object[] objArr = conList.toArray();
		List<HotelInfoModel> hotelList = this.query(sql.toString(), objArr, HotelInfoModel.class, null);
		
		return hotelList;
	}
	
	/**
	 * 获取宾馆的同部数据
	 * @return
	 */
	public List<Map<String, Object>> queryAnsycHotelInfo(){
		String sql = "SELECT tii.hotelName, tii.createTime,tcc.crewId FROM   tab_inhotel_info tii"
				+ " LEFT JOIN tab_crew_contact tcc ON tcc.contactId = tii.contactId "
				+ " GROUP BY hotelName";
		return this.query(sql, null, null);
	}
	
	/**
	 * 根据日期查询出当天的住宿信息
	 * @param date
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryHotelDataByDate(String date, String crewId){
		String sql = "SELECT COUNT(DISTINCT contactname) peopleCount, hotelname FROM tab_inhotelcost_temp "
				+" WHERE crewid = ? AND showdate = ? GROUP BY hotelname ORDER BY hotelname";
		return this.query(sql, new Object[] {crewId, date}, null);
	}
}
