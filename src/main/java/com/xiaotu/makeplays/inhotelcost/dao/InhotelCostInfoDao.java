package com.xiaotu.makeplays.inhotelcost.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.inhotelcost.controller.filter.InHotelCostFilter;
import com.xiaotu.makeplays.inhotelcost.model.InhotelCostModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * @ClassName InhotelCostInfoDao
 * @Description 住房费用
 * @author Administrator
 * @Date 2017年1月4日 下午5:08:58
 * @version 1.0.0
 */
@Repository
public class InhotelCostInfoDao extends BaseDao<InhotelCostModel>{
	
	public List<Map<String, Object>> queryInHotelCostInfo(String crewId,String startDate, String endDate,String hotelName){
		List<Object> args = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("    showdate checkInDate, ");
		sql.append("    hotelname, ");
		sql.append("    sum(r) rnum, ");
		sql.append("    sum(p) sprice, ");
		sql.append("    avg(p) aprice, ");
		sql.append("    sum(n) pnum ");
		sql.append(" FROM ");
		sql.append("    ( ");
		sql.append("       SELECT ");
		sql.append("          showdate, ");
		sql.append("          hotelname, ");
		sql.append("          count(DISTINCT roomnumber) r, ");
		sql.append("          max(price) p, ");
		sql.append("          count(DISTINCT contactname) n ");
		sql.append("       FROM ");
		sql.append("          tab_inhotelcost_temp ");
		sql.append("       WHERE ");
		sql.append("          crewid = ? ");
		args.add(crewId);
//		if(StringUtils.isNotBlank(showDate)){
//			String[] showDateArray = StringUtils.split(showDate,",");
//			if(showDateArray!=null&&showDateArray.length>0){
//				sql.append(" and date_format(showdate,'%Y-%m') in ( ");
//				boolean flag = false;
//				for(String sd :showDateArray){
//					if(StringUtils.isNotBlank(sd)){
//						flag = true;
//						sql.append("?,");
//						args.add(sd);
//					}
//				}
//				if(flag){
//					sql.deleteCharAt(sql.length()-1);
//				}
//				sql.append(" )");
//			}
//			
//		}
		if(StringUtils.isNotBlank(startDate)) {
			sql.append(" and showdate >= ? ");
			args.add(startDate);
		}
		if(StringUtils.isNotBlank(endDate)) {
			sql.append(" and showdate <= ? ");
			args.add(endDate);
		}
		if(StringUtils.isNotBlank(hotelName)){
			String[] hotelNameArray = StringUtils.split(hotelName,",");
			if(hotelNameArray!=null&&hotelNameArray.length>0){
				sql.append("  and hotelname  in ( ");
				boolean flag = false;
				for(String sd :hotelNameArray){
					if(StringUtils.isNotBlank(sd)){
						flag = true;
						sql.append("?,");
						args.add(sd);
					}
				}
				if(flag){
					sql.deleteCharAt(sql.length()-1);
				}
				sql.append(" )");
			}
		}
		sql.append("       GROUP BY ");
		sql.append("          showdate, hotelname, ");
		sql.append("          roomnumber ");
		sql.append("       ORDER BY ");
		sql.append("          showdate ");
		sql.append("    ) a ");
		sql.append(" GROUP BY ");
		sql.append("    showdate, ");
		sql.append("    hotelname ");
		
		return this.getJdbcTemplate().queryForList(sql.toString(), args.toArray());
	}
	
	public List<String> queryDrowDataForDate(String crewId){
		String sql = "select distinct date_format(showdate,'%Y-%m') showdate from tab_inhotelcost_temp where crewid = ? order by showdate ";
		List<String> list = this.getJdbcTemplate().queryForList(sql, String.class,new Object[]{crewId});
		
		return list;
	}
	public List<String> queryDrowDataForHotelName(String crewId){
		String sql = "select distinct hotelname from tab_inhotelcost_temp where crewid = ? order by hotelname ";
		List<String> list = this.getJdbcTemplate().queryForList(sql, String.class,new Object[]{crewId});
		return list;
	}
	
	
	/**
	 * @Description 查询某一天 某个宾馆入住详情
	 * @param hotelName
	 * @param checkInDate
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryInHotelCostDetailInfo(String hotelName,String checkInDate,String crewId){
		StringBuilder sql = new StringBuilder();
		List<Object> args = new ArrayList<Object>();
		sql.append(" SELECT DISTINCT ");
		sql.append("   tchi.roomNo roomNumber, tii.hotelName,");
		sql.append("   max(tchi.roomPrice) price, ");
		sql.append("    GROUP_CONCAT(DISTINCT tchi.peopleName order by convert(peopleName using gbk)) contactName ");
		sql.append(" FROM ");
		sql.append("    tab_hotel_info tii ");
		sql.append(" LEFT JOIN tab_checkIn_hotel_info tchi ON tchi.hotelId = tii.id AND tchi.crewId = ?");
		args.add(crewId);
		sql.append(" WHERE 1=1 ");
		if(StringUtils.isNotBlank(hotelName)){
			sql.append("   and  tii.hotelName = ? ");
			args.add(hotelName);
		}
		sql.append(" AND tii.crewId = ?");
		args.add(crewId);
		sql.append(" AND ? >= tchi.checkInDate ");
		args.add(checkInDate);
		sql.append(" AND IF(tchi.checkoutDate - tchi.checkInDate > 0,? <tchi.checkoutDate,? <= tchi.checkoutDate) ");
		args.add(checkInDate);
		args.add(checkInDate);
		sql.append(" GROUP BY ");
		sql.append("    hotelName,roomNo ");
		sql.append(" ORDER BY ");
		sql.append("    hotelName,roomNo ");
		
		return this.getJdbcTemplate().queryForList(sql.toString(), args.toArray());
	}
	
	
	
	/**
	 * @Description 查询某一天 某个宾馆入住详情 导出
	 * @param hotelName
	 * @param checkInDate
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryInHotelCostDetailInfoForExport(String hotelName,String startDate, String endDate,String crewId){
		StringBuilder sql = new StringBuilder();
		List<Object> args = new ArrayList<Object>();
		
		sql.append(" SELECT ");
		sql.append("    showdate, ");
		sql.append("    hotelname, ");
		sql.append("    roomnumber, ");
		sql.append("    max(price) price, ");
		sql.append("    GROUP_CONCAT(contactname) concactname ");
		sql.append(" FROM ");
		sql.append("    tab_inhotelcost_temp ");
		sql.append(" WHERE 1=1 ");
		
//		if(StringUtils.isNotBlank(checkInDate)){
//			String[] showDateArray = StringUtils.split(checkInDate,",");
//			if(showDateArray!=null&&showDateArray.length>0){
//				sql.append(" and date_format(showdate,'%Y-%m') in ( ");
//				boolean flag = false;
//				for(String sd :showDateArray){
//					if(StringUtils.isNotBlank(sd)){
//						flag = true;
//						sql.append("?,");
//						args.add(sd);
//					}
//				}
//				if(flag){
//					sql.deleteCharAt(sql.length()-1);
//				}
//				sql.append(" )");
//			}
//			
//		}
		if(StringUtils.isNotBlank(startDate)) {
			sql.append(" and showdate >= ? ");
			args.add(startDate);
		}
		if(StringUtils.isNotBlank(endDate)) {
			sql.append(" and showdate <= ? ");
			args.add(endDate);
		}
		if(StringUtils.isNotBlank(hotelName)){
			String[] hotelNameArray = StringUtils.split(hotelName,",");
			if(hotelNameArray!=null&&hotelNameArray.length>0){
				sql.append("  and hotelname  in ( ");
				boolean flag = false;
				for(String sd :hotelNameArray){
					if(StringUtils.isNotBlank(sd)){
						flag = true;
						sql.append("?,");
						args.add(sd);
					}
				}
				if(flag){
					sql.deleteCharAt(sql.length()-1);
				}
				sql.append(" )");
			}
		}
		if(StringUtils.isNotBlank(crewId)){
			sql.append(" and crewid = ? ");
			args.add(crewId);
		}
		sql.append(" GROUP BY ");
		sql.append("    showdate, ");
		sql.append("    hotelname, ");
		sql.append("    roomnumber ");
		sql.append(" ORDER BY ");
		sql.append("    showdate ");
		
		return this.getJdbcTemplate().queryForList(sql.toString(), args.toArray());
	}
	
	/**
	 * 从入住信息表中同步数据
	 * @param crewId
	 */
	public void syncFromHotelInfo(String crewId) {
		String callStr = "call onetomanybydateforinhotelcost('" + crewId + "')";
		this.getJdbcTemplate().execute(callStr);
	}
	
	/**
	 * 查询入住费用中入住时间列表（去重）
	 * @param crewId
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> queryDistinctShowDateList(String crewId, InHotelCostFilter filter, Page page) {
		List<Object> params = new ArrayList<Object>();
		params.add(crewId);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT tit.showdate from tab_inhotelcost_temp tit ");
		sql.append("  where tit.crewId = ? ");
		if (!StringUtils.isBlank(filter.getShowDates())) {
			sql.append(" and tit.showDate in ( ");
			
			String[] showDateArray = filter.getShowDates().split(",");
			for (String showDate : showDateArray) {
				sql.append("?,");
				params.add(showDate);
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(" ) ");
		}
		if (!StringUtils.isBlank(filter.getHotelNames())) {
			sql.append(" and tit.hotelName in ( ");
			String[] hotelNameArray = filter.getHotelNames().split(",");
			for (String hotelName : hotelNameArray) {
				sql.append("?,");
				params.add(hotelName);
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(" ) ");
		}
		if (!StringUtils.isBlank(filter.getStartDate())) {
			sql.append(" and tit.showDate >= ? ");
			params.add(filter.getStartDate());
		}
		if (!StringUtils.isBlank(filter.getEndDate())) {
			sql.append(" and tit.showDate <= ? ");
			params.add(filter.getEndDate());
		}
		sql.append(" GROUP BY showdate ");
		sql.append(" ORDER BY showdate ");
		
		return this.query(sql.toString(), params.toArray(), page);
	}
	
	/**
	 * 根据高级查询条件查询住宿费用
	 * @param crewId
	 * @param filter
	 * @return
	 */
	public List<InhotelCostModel> queryByAdvanceCondition(String crewId, InHotelCostFilter filter) {
		List<Object> params = new ArrayList<Object>();
		params.add(crewId);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT tit.showDate, tit.hotelName, tit.roomNumber, tit.price, tit.crewId, tit.contactName from tab_inhotelcost_temp tit ");
		sql.append("  where tit.crewId = ? ");
		if (!StringUtils.isBlank(filter.getShowDates())) {
			sql.append(" and tit.showDate in ( ");
			
			String[] showDateArray = filter.getShowDates().split(",");
			for (String showDate : showDateArray) {
				sql.append("?,");
				params.add(showDate);
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(" ) ");
		}
		if (!StringUtils.isBlank(filter.getHotelNames())) {
			sql.append(" and tit.hotelName in ( ");
			String[] hotelNameArray = filter.getHotelNames().split(",");
			for (String hotelName : hotelNameArray) {
				sql.append("?,");
				params.add(hotelName);
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(" ) ");
		}
		if (!StringUtils.isBlank(filter.getStartDate())) {
			sql.append(" and tit.showDate >= ? ");
			params.add(filter.getStartDate());
		}
		if (!StringUtils.isBlank(filter.getEndDate())) {
			sql.append(" and tit.showDate <= ? ");
			params.add(filter.getEndDate());
		}
		sql.append(" ORDER BY showdate ");
		
		return this.query(sql.toString(), params.toArray(), InhotelCostModel.class, null);
	}
	
	/**
	 * 根据联系人ID删除入住费用信息
	 * @param crewId
	 * @param contactId
	 */
	public void deleteByContactId(String contactId) {
		String sql = "delete from tab_inhotelcost_temp where contactId = ?";
		this.getJdbcTemplate().update(sql, contactId);
	}
}
