package com.xiaotu.makeplays.hotelInfo.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.hotelInfo.controller.filter.CheckinHotelInfoFilter;
import com.xiaotu.makeplays.hotelInfo.model.CheckinHotelInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 入住信息操作的dao
 * @author wanrenyi 2017年3月14日下午5:25:50
 */
@Repository
public class CheckinHotelInfoDao extends BaseDao<CheckinHotelInfoModel>{

	/**
	 * 根据宾馆id删除
	 * @param hotelId
	 */
	public void deleteById(String hotelId) {
		String sql = " DELETE FROM tab_checkIn_hotel_info WHERE hotelId = ?";
		this.getJdbcTemplate().update(sql, hotelId);
	}
	
	/**
	 * 根据ID查找数据
	 * @param crewId
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public CheckinHotelInfoModel queryById(String crewId, String id) throws Exception {
		String sql = "select * from " + CheckinHotelInfoModel.TABLE_NAME + " where crewId = ? and id = ?";
		return this.queryForObject(sql, new Object[] {crewId, id}, CheckinHotelInfoModel.class);
	}
	
	/**
	 * 查询当前剧组内的所有入住人员姓名列表
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryCheckInPeopleNameList(String crewId){
		String sql = " SELECT DISTINCT peopleName,func_get_first_letter(peopleName) fletter FROM tab_checkIn_hotel_info WHERE crewId = ?";
		return this.query(sql, new Object[] {crewId}, null);
	}
	
	/**
	 * 根据宾馆id查询出宾馆的入住信息列表
	 * @param hotelId
	 * @return
	 */
	public List<Map<String, Object>> queryCheckIninfoByHotelId(String hotelId){
		String sql = "select * from " + CheckinHotelInfoModel.TABLE_NAME + " where hotelId = ? order by roomNo, checkInDate, checkoutDate, convert(peopleName using gbk)";
		return this.query(sql, new Object[] {hotelId}, null);
	}
	
	/**
	 * 根据酒店ID查询入住登记列表
	 * @param hotelId
	 * @return
	 */
	public List<CheckinHotelInfoModel> queryByHotelId(String hotelId) {
		String sql = "select * from " + CheckinHotelInfoModel.TABLE_NAME + " where hotelId = ? order by roomNo, checkInDate, convert(peopleName using gbk)";
		return this.query(sql, new Object[] {hotelId}, CheckinHotelInfoModel.class, null);
	}
	
	/**
	 * 查询入住登记信息
	 * @param crewId
	 * @param filter
	 * @return
	 */
	public List<CheckinHotelInfoModel> queryCheckinHotelList (String crewId, CheckinHotelInfoFilter filter) {
		List<Object> paramList = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		sql.append(" select * ");
		sql.append("   from tab_checkIn_hotel_info ");
		sql.append("  where 1 = 1");
		sql.append("  and crewId = ?");
		paramList.add(crewId);
		if (!StringUtils.isBlank(filter.getPeopleName())) {
			sql.append(" and peopleName = ? ");
			paramList.add(filter.getPeopleName());
		}
		if (!StringUtils.isBlank(filter.getCheckinDate())) {
			sql.append(" and checkoutDate >= ? ");
			paramList.add(filter.getCheckinDate());
		}
		if (!StringUtils.isBlank(filter.getCheckoutDate())) {
			sql.append(" and checkinDate <= ? ");
			paramList.add(filter.getCheckoutDate());
		}
		if (!StringUtils.isBlank(filter.getHotelId())) {
			sql.append(" and hotelId = ? ");
			paramList.add(filter.getHotelId());
		}
		return this.query(sql.toString(), paramList.toArray(), CheckinHotelInfoModel.class, null);
	}
	
	/**
	 * 获取宾馆入住信息的同步数据
	 * @return
	 */
	public List<Map<String, Object>> queryAnsyCheckInHotelInfo(){
		String sql= "SELECT	tii.*,tcc.contactName,tcc.crewId FROM	tab_inhotel_info tii "
				+ " LEFT JOIN tab_crew_contact tcc ON tcc.contactId = tii.contactId"
				+ " ORDER BY hotelName";
		
		return this.query(sql, null, null);
	}
	
	/**
	 * 获取当前剧组的所有的房间类型
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryRoomTypeListByCrewId(String crewId){
		String sql = " select DISTINCT roomType from tab_checkIn_hotel_info where roomType IS NOT NULL AND crewId = ? or crewId = '0'  ORDER BY crewId, id";
		
		return this.query(sql, new Object[] {crewId}, null);
	}
	
	/**
	 * 查询出当前剧组中的所有的房间号
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryRoomNumList(String hotelId){
		String sql = "select DISTINCT roomNo FROM tab_checkIn_hotel_info WHERE hotelId = ? ORDER BY ABS(roomNo)";
		
		return this.query(sql, new Object[] {hotelId}, null);
	}
	
	/**
	 * 查询出当前剧组中的分机号列表
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryExtensionList(String hotelId){
		String sql = "select DISTINCT extension FROM tab_checkIn_hotel_info WHERE hotelId = ? ORDER BY ABS(roomNo)";
		
		return this.query(sql, new Object[] {hotelId}, null);
	}
	
	/**
	 * 查询入住登记信息
	 * @param crewId
	 * @param filter
	 * @return
	 */
	public List<Map<String, Object>> queryCheckinInfoListByAllFilter (String hotelId, CheckinHotelInfoFilter filter) {
		List<Object> paramList = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		sql.append(" select * ");
		sql.append("   from tab_checkIn_hotel_info ");
		sql.append("  where hotelId = ?");
		paramList.add(hotelId);
		
		if (StringUtils.isNotBlank(filter.getRoomNo())) {
			sql.append(" and roomNo in ("+ "'" + filter.getRoomNo().replace(",", "','") +"'" +")");
		}
		if (!StringUtils.isBlank(filter.getPeopleName())) {
			sql.append(" and peopleName in ("+ "'" + filter.getPeopleName().replace(",", "','") +"'" +") ");
		}
		if (StringUtils.isNotBlank(filter.getCheckInStartDate())) {
			sql.append(" and checkinDate >= ? ");
			paramList.add(filter.getCheckInStartDate());
		}
		if (StringUtils.isNotBlank(filter.getCheckInEndDate())) {
			sql.append(" and checkinDate <= ? ");
			paramList.add(filter.getCheckInEndDate());
		}
		if (StringUtils.isNotBlank(filter.getCheckoutStartDate())) {
			sql.append(" and checkoutDate >= ? ");
			paramList.add(filter.getCheckoutStartDate());
		}
		if (StringUtils.isNotBlank(filter.getCheckOutEndDate())) {
			sql.append(" and checkoutDate <= ? ");
			paramList.add(filter.getCheckOutEndDate());
		}
		if (StringUtils.isNotBlank(filter.getStartInTimes())) {
			sql.append(" and inTimes >= ? ");
			paramList.add(filter.getStartInTimes());
		}
		if (StringUtils.isNotBlank(filter.getEndInTimes())) {
			sql.append(" and inTimes <= ? ");
			paramList.add(filter.getEndInTimes());
		}
		if (StringUtils.isNotBlank(filter.getRoomType())) {
			sql.append(" and roomType in ("+ "'" + filter.getRoomType().replace(",", "','") +"'" +") ");
		}
		if (filter.getStartRoomPrice() != null) {
			sql.append(" and roomPrice >= ? ");
			paramList.add(filter.getStartRoomPrice());
		}
		if (filter.getEndRoomPrice() != null) {
			sql.append(" and roomPrice <= ? ");
			paramList.add(filter.getEndRoomPrice());
		}
		if (StringUtils.isNotBlank(filter.getExtension())) {
			sql.append(" and extension in ("+ "'" + filter.getExtension().replace(",", "','") +"'" +") ");
		}
		if (StringUtils.isNotBlank(filter.getRemark())) {
			sql.append(" and remark like ? ");
			paramList.add("%" + filter.getRemark() + "%");
		}
		sql.append(" order by ABS(roomNo), checkInDate, checkoutDate, convert(peopleName using gbk)");
		return this.query(sql.toString(), paramList.toArray(), null);
	}
}
