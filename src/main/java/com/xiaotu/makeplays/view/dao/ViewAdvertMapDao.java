package com.xiaotu.makeplays.view.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.view.model.ViewAdvertMapModel;

/**
 * 场景和植入广告的关联关系
 * @author xuchangjian
 */
@Repository
public class ViewAdvertMapDao extends BaseDao<ViewAdvertMapModel> {

	/**
	 * 根据多个条件查询植入广告信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ViewAdvertMapModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + ViewAdvertMapModel.TABLE_NAME + " where 1 = 1 ");
		
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
		List<ViewAdvertMapModel> viewAdvertMapList = this.query(sql.toString(), objArr, ViewAdvertMapModel.class, page);
		
		return viewAdvertMapList;
	}
	

	/**
	 * 根据场景ID删除场景和场景地点的关联关系
	 */
	public void deleteManyByViewId(String viewId) {
		
		String sql = "delete from " + ViewAdvertMapModel.TABLE_NAME + " where viewId = ?";
		
		this.getJdbcTemplate().update(sql.toString(), new Object[] {viewId});
	}
	
	/**
	 * 根据场景ID删除场景和场景地点的关联关系
	 * 多个场景ID用,隔开
	 */
	public void deleteManyByViewIds(String viewIds) {
		viewIds = "'" + viewIds.replace(",", "','") + "'";
		
		String sql = "delete from " + ViewAdvertMapModel.TABLE_NAME + " where viewId in (" + viewIds + ")";
		
		this.getJdbcTemplate().update(sql);
	}
	
	public List<ViewAdvertMapModel> queryAdvertInfoByCrewId(String crewId){
		String sql = "select * from " +ViewAdvertMapModel.TABLE_NAME+" where crewId = ?";
		List<ViewAdvertMapModel> list =this.query(sql, new Object[]{crewId}, ViewAdvertMapModel.class, null);
		return list;
	}
	
	/**
	 * @Description  根据场景id批量删除场景 三级场景对照关系
	 * @param viewIdList
	 */
	public void deleteBatchByViewId(List<String> viewIdList){
		if(viewIdList!=null&&viewIdList.size()>0){
			List<Object[]> args = new ArrayList<Object[]>();
			for(String viewId :viewIdList){
				if(StringUtils.isNotBlank(viewId)){
					args.add(new Object[]{viewId});
				}
			}
			String sql = "delete from "+ViewAdvertMapModel.TABLE_NAME +" where viewId = ?";
			this.getJdbcTemplate().batchUpdate(sql, args);
		}
	}
}
