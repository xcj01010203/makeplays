package com.xiaotu.makeplays.shoot.dao;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.shoot.model.ShootPlanModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 拍摄计划
 * @author xuchangjian
 */
@Repository
public class ShootPlanDao extends BaseDao<ShootPlanModel> {

	/**
	 * 通过拍摄计划ID查找拍摄计划信息
	 * @param planId	计划ID
	 * @return
	 */
	public ShootPlanModel queryOneByPlanId (String planId) {
		String sql = "select * from " + ShootPlanModel.TABLE_NAME +" where planId = ?";
		
		ShootPlanModel shootPlan = null;
		Object[] args = new Object[] {planId};
		if (getResultCount(sql, args) == 1) {
			shootPlan = this.getJdbcTemplate().queryForObject(sql, args, ParameterizedBeanPropertyRowMapper
					.newInstance(ShootPlanModel.class));
		}
		
		return shootPlan;
	}
	
	/**
	 * 根据多个条件查询拍摄计划信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ShootPlanModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + ShootPlanModel.TABLE_NAME + " where 1 = 1 ");
		
		Set<String> keySet = conditionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		List<Object> conList = new LinkedList<Object>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = conditionMap.get(key);
			if (key.equals("planId")) {
				sql.append(" and " + key + " != ?");
				conList.add(value);
				continue;
			}
			if (key.equals("parentPlan") && value == null) {
				sql.append(" and " + key + " is null");
				continue;
			}
			
			sql.append(" and " + key + " = ?");
			conList.add(value);
		}
		sql.append(" order by updateTime desc");
		Object[] objArr = conList.toArray();
		List<ShootPlanModel> shootPlanList = this.query(sql.toString(), objArr, ShootPlanModel.class, page);
		
		return shootPlanList;
	}
	
	/**
	 * 查询计划的父计划ID
	 * @param planIds	多个计划ID，以逗号隔开
	 * @return
	 */
	public List<Map<String, Object>> queryParentPlanIds(String planIds) {
		planIds = "'" + planIds.replace(",", "','") + "'";
		String sql = "select DISTINCT parentPlan from " + ShootPlanModel.TABLE_NAME + " where planId in (" + planIds + ") and parentPlan is not null";
		
		return this.query(sql, null, null);
	}
	
	/**
	 * 查询拍摄计划信息，该方法会查询出上级计划的名称
	 * @param conditionMap
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> queryManyByMutiConditionWithParentName(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select sub.*, par.planName from " + ShootPlanModel.TABLE_NAME + " sub LEFT JOIN " + ShootPlanModel.TABLE_NAME + " par ON par.planId = sub.parentPlan where 1 = 1 ");
		
		Set<String> keySet = conditionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		List<Object> conList = new LinkedList<Object>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = conditionMap.get(key);
			sql.append(" and sub." + key + " = ?");
			conList.add(value);
		}
		sql.append(" order by updateTime desc");
		Object[] objArr = conList.toArray();
		
		return this.query(sql.toString(), objArr, page);
	}
}
