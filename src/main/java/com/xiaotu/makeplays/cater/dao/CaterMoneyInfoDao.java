package com.xiaotu.makeplays.cater.dao;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.cater.model.CaterMoneyInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 餐饮金额详细信息的dao
 * @author wanrenyi 2017年2月21日下午2:24:29
 */
@Repository
public class CaterMoneyInfoDao extends BaseDao<CaterMoneyInfoModel>{

	/**
	 * 根据多个条件查询餐饮金额信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<CaterMoneyInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + CaterMoneyInfoModel.TABLE_NAME + " where 1 = 1 ");
		
		Set<String> keySet = conditionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		List<Object> conList = new LinkedList<Object>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = conditionMap.get(key);
			sql.append(" and " + key + " = ?");
			conList.add(value);
		}
		Object[] objArr = conList.toArray();
		List<CaterMoneyInfoModel> caterMoneyInfoList = this.query(sql.toString(), objArr, CaterMoneyInfoModel.class, page);
		
		return caterMoneyInfoList;
	}
	
	/**
	 * 根据餐饮的id删除当天餐饮金额的详细信息
	 * @param caterId
	 */
	public void deleteByCaterId(String caterId) {
		String sql = "delete from "+ CaterMoneyInfoModel.TABLE_NAME + " where caterId = ?";
		this.getJdbcTemplate().update(sql, caterId);
	}
	
	/**
	 * 根据ID删除数据
	 * @param caterMoneyId
	 */
	public void deleteById(String caterMoneyId) {
		String sql = "delete from "+ CaterMoneyInfoModel.TABLE_NAME + " where caterMoneyId = ?";
		this.getJdbcTemplate().update(sql, caterMoneyId);
	}
	
	/**
	 * 根据餐饮id查询餐饮的金额信息
	 * @param caterId
	 * @return 除了返回餐饮金额信息，还会返回餐饮类别
	 */
	public List<Map<String, Object>> queryCaterMoneyByCaterId(String caterId){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT tci.*");
		sql.append(" FROM");
		sql.append(" tab_cater_money_info tci");
		sql.append(" WHERE tci.caterId = ? order by tci.caterType");
		return this.query(sql.toString(), new Object[] {caterId}, null);
	}
	
	/**
	 * 查询餐饮的统计信息
	 * @param caterId
	 * @return
	 */
	public List<Map<String, Object>> querySummeryData(String caterId){
		StringBuffer sql = new StringBuffer();
		sql.append(" select sum(tci.peopleCount) totalPeopleCount, sum(tci.caterMoney) totalMoney");
		sql.append(" from " + CaterMoneyInfoModel.TABLE_NAME +" tci");
		sql.append(" where tci.caterId = ?");
		
		return this.query(sql.toString(), new Object[] {caterId}, null);
	}
	
	/**
	 * 根据ID查询数据
	 * @param caterId
	 * @return
	 * @throws Exception 
	 */
	public CaterMoneyInfoModel queryById(String caterMoneyId) throws Exception {
		String sql = "select * from tab_cater_money_info where caterMoneyId = ?";
		return this.queryForObject(sql, new Object[] {caterMoneyId}, CaterMoneyInfoModel.class);
	}
	
	/**
	 * 获取当前剧组的餐饮列表列表
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryCaterTypeList(String crewId){
		//String sql = " select DISTINCT IFNULL(caterType,'普餐') caterType from "+ CaterMoneyInfoModel.TABLE_NAME +" where crewId = ? or crewId = '0' ORDER BY crewId, caterMoneyId";
		String sql =" select caterType from "+ CaterMoneyInfoModel.TABLE_NAME +" where crewid='0' "
				+" UNION "
				+" select DISTINCT case caterType when '' then '普餐' "
				+" else IFNULL(caterType,'普餐') END as caterType from "+ CaterMoneyInfoModel.TABLE_NAME 
				+" where crewid= ? ";
		
		return this.query(sql, new Object[] {crewId}, null);
	}
	/**
	 * 获取当前剧组的用餐时间列表
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryCaterTimeTypeList(String crewId){
		//String sql = " select DISTINCT IFNULL(caterTimeType,'早餐') caterTimeType from "+ CaterMoneyInfoModel.TABLE_NAME +" where crewId = ? or crewId = '00' ORDER BY crewId, caterMoneyId";
		String sql =" select caterTimeType from "+ CaterMoneyInfoModel.TABLE_NAME +" where crewid='00' "
				+" UNION "
				+" select DISTINCT CASE caterTimeType when '' then '早餐' "
				+" else IFNULL(caterTimeType,'早餐') END as caterTimeType from "+ CaterMoneyInfoModel.TABLE_NAME 
				+" where crewid= ? ";
		
		return this.query(sql, new Object[] {crewId}, null);
	}
	/**
	 * 获取当前剧组的用餐地点列表
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryCaterAddrList(String crewId){
		//String sql = " select DISTINCT IFNULL(caterAddr,'A组') caterAddr from "+ CaterMoneyInfoModel.TABLE_NAME +" where crewId = ? or crewId = '000' ORDER BY crewId, caterMoneyId";
		String sql =" select caterAddr from "+ CaterMoneyInfoModel.TABLE_NAME +" where crewid='000' "
				+" UNION "
				+" select DISTINCT CASE caterAddr when '' then 'A组' "
				+" else IFNULL(caterAddr,'A组') END as caterAddr from "+ CaterMoneyInfoModel.TABLE_NAME 
				+" where crewid= ? ";
		
		return this.query(sql, new Object[] {crewId}, null);
	}
}
