package com.xiaotu.makeplays.view.dao;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.view.model.InsideAdvertModel;
import com.xiaotu.makeplays.view.model.ViewAdvertMapModel;

/**
 * 植入广告基本信息
 * @author xuchangjian
 */
@Repository
public class InsideAdvertDao extends BaseDao<InsideAdvertModel> {

	/**
	 * 根据多个条件查询植入广告信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<InsideAdvertModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + InsideAdvertModel.TABLE_NAME + " where 1 = 1 ");
		
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
		List<InsideAdvertModel> advertList = this.query(sql.toString(), objArr, InsideAdvertModel.class, page);
		
		return advertList;
	}
	
	/**
	 * 使用剧组id查询剧组广告信息
	 * @param crewId
	 * @return
	 */
	public List<InsideAdvertModel> queryAdvertInfoByCrewId(String crewId){
		
		String sql = "select * from " + InsideAdvertModel.TABLE_NAME + " where crewid=?";
		
		
		return this.query(sql, new Object[]{crewId}, InsideAdvertModel.class, null);
	}
	
	/**
	 * 根据场景ID查询植入广告信息
	 * @param viewId 场景ID
	 * @return
	 */
	public List<Map<String, Object>> queryAdvertByViewId(String viewId) {
		String sql = "select a.*, m.advertType, m.viewId from " + InsideAdvertModel.TABLE_NAME + " a, " + ViewAdvertMapModel.TABLE_NAME + " m where m.viewId = ? and m.advertId = a.advertId";
		
		return this.query(sql, new Object[] {viewId}, null);
	}
	
	/**
	 * 根据多个场景id查询广告
	 * 包括查询场景ID信息
	 * 该查询带有根据场景ID和广告名称去重效果
	 */
	public List<Map<String, Object>> queryManyByViews(String viewIds){
		
		String sql = " select distinct m.viewId, a.advertName from " + InsideAdvertModel.TABLE_NAME + " a," + ViewAdvertMapModel.TABLE_NAME + " m where m.advertId = a.advertId and m.viewId in ("+viewIds+")";
		
		return this.query(sql, null, null);
	}
	
	/**
	 * 根据id删除一条广告信息
	 * @param advertId
	 */
	public void deleteAdvertById(String advertId) {
		String sql = "delete from " + InsideAdvertModel.TABLE_NAME + " where advertId = ?";
		this.getJdbcTemplate().update(sql, advertId);
	}
}
