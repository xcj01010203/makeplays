package com.xiaotu.makeplays.cater.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.cater.model.CaterInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 餐饮操作dao
 * @author wanrenyi 2017年2月20日下午2:30:51
 */
@Repository
public class CaterInfoDao extends BaseDao<CaterInfoModel>{
	
	/**
	 * 根据多个条件查询餐饮信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<CaterInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + CaterInfoModel.TABLE_NAME + " where 1 = 1 ");
		
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
		List<CaterInfoModel> caterInfoList = this.query(sql.toString(), objArr, CaterInfoModel.class, page);
		
		return caterInfoList;
	}
	
	/**
	 * 根据日期查询出当天的餐饮信息
	 * @param caterDate
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryCaterInfoByCaterDate(Date caterDate, String crewId){
		String sql = " select * from "+ CaterInfoModel.TABLE_NAME + " where DATE_FORMAT(?,'%Y-%m-%d') = DATE_FORMAT(caterDate,'%Y-%m-%d') and crewId = ?";
		return this.query(sql, new Object[] {caterDate, crewId},  null);
	}
	
	/**
	 * 根据餐饮id查询出餐饮的信息
	 * @param caterId
	 * @return
	 */
	public List<Map<String, Object>> queryCaterInfoByCaterId(String caterId){
		String sql = " select * from "+ CaterInfoModel.TABLE_NAME + " where caterId = ?";
		return this.query(sql, new Object[] {caterId}, null);
	}
	
	/**
	 * 根据餐饮id查询出餐饮的信息
	 * @param caterId
	 * @return
	 * @throws Exception 
	 */
	public CaterInfoModel queryById(String caterId) throws Exception{
		String sql = " select * from "+ CaterInfoModel.TABLE_NAME + " where caterId = ?";
		return this.queryForObject(sql, new Object[] {caterId}, CaterInfoModel.class);
	}
	
	/**
	 * 查询餐饮列表数据
	 * @return
	 */
	public List<Map<String, Object>> queryCaterInfoList(Page page, String crewId){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ");
		sql.append(" tci.caterId,tci.budget, tci.caterDate,	SUM(tcm.caterMoney) caterMoney,	SUM(tcm.peopleCount) totalPeople");
		sql.append(" FROM tab_cater_info tci");
		sql.append(" LEFT JOIN tab_cater_money_info tcm ON tcm.caterId = tci.caterId");
		sql.append(" WHERE tci.crewId = ? GROUP BY	tci.caterId");
		sql.append(" ORDER BY tci.caterDate");
		
		return this.query(sql.toString(), new Object[] {crewId}, null);
	}
	
}
