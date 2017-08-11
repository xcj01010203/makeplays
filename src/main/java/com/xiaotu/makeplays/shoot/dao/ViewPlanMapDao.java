package com.xiaotu.makeplays.shoot.dao;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.shoot.model.ViewPlanMapModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 场景和拍摄计划关联关系
 * @author xuchangjian
 */
@Repository
public class ViewPlanMapDao extends BaseDao<ViewPlanMapModel> {

	/**
	 * 根据多个条件查询场景和拍摄计划的关联关系信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ViewPlanMapModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + ViewPlanMapModel.TABLE_NAME + " where 1 = 1 ");
		
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
		List<ViewPlanMapModel> viewPlanModelList = this.query(sql.toString(), objArr, ViewPlanMapModel.class, page);
		
		return viewPlanModelList;
	}
	
	/**
	 * 根据计划ID和多个场景ID删除计划和场景的关联关系
	 * @param planId
	 * @param viewIds
	 */
	public void deleteByPlanIdAndViewIds(String planId, String[] viewIdsArr) {
		
		StringBuilder sql = new StringBuilder("delete from " + ViewPlanMapModel.TABLE_NAME + " where 1 = 1");
		
		if (viewIdsArr != null && viewIdsArr.length > 0) {
			sql.append(" and viewId in(");
			for (int i = 0; i < viewIdsArr.length; i++) {
				if (i == viewIdsArr.length - 1) {
					sql.append("'" + viewIdsArr[i] + "')");
				} else {
					sql.append("'" + viewIdsArr[i] + "',");
				}
			}
		}
		sql.append(" and planId = ?");
		
		this.getJdbcTemplate().update(sql.toString(), new Object[] {planId});
	}
	
	/**
	 * 根据计划ID和多个场景ID删除计划和场景的关联关系
	 * @param planId
	 * @param viewIds
	 */
	public void deleteByPlanIdsAndViewIds(String[] planIdArr, String[] viewIdsArr) {
		StringBuilder sql = new StringBuilder("delete from " + ViewPlanMapModel.TABLE_NAME + " where 1 = 1");
		List<Object> filterList = new LinkedList<Object>();
		if (planIdArr != null && planIdArr.length > 0 && viewIdsArr != null && viewIdsArr.length > 0) {
			sql.append(" and (");
			for (int i = 0; i < planIdArr.length; i++) {
				if (StringUtils.isBlank(planIdArr[i])) {
					continue;
				}
				for (int j = 0; j < viewIdsArr.length; j++) {
					if (StringUtils.isBlank(viewIdsArr[j])) {
						continue;
					}
					if (i == 0 && j == 0) {
						sql.append("(planId = ? and viewId = ?)");
						filterList.add(planIdArr[i]);
						filterList.add(viewIdsArr[j]);
						continue;
					}
					sql.append(" or (planId = ? and viewId = ?) ");
					filterList.add(planIdArr[i]);
					filterList.add(viewIdsArr[j]);
				}
			}
			sql.append(")");
				
		}
		
		this.getJdbcTemplate().update(sql.toString(), filterList.toArray());
		
	}
	
}
