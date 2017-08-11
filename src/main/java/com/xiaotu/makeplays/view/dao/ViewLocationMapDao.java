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
import com.xiaotu.makeplays.view.model.ViewLocationMapModel;

/**
 * 场景地址关联关系信息
 * @author xuchangjian
 */
@Repository
public class ViewLocationMapDao extends BaseDao<ViewLocationMapModel> {

	/**
	 * 批量新增操作
	 * @param viewLocationMapList
	 * @throws Exception 
	 */
	public void addMany (List<ViewLocationMapModel> viewLocationMapList) throws Exception {
		for (ViewLocationMapModel viewLoationMap : viewLocationMapList) {
			this.add(viewLoationMap);
		}
	}
	
	/**
	 * 更新数据
	 * @param viewLoationMap	场景和场景地址关联关系
	 * @throws Exception 
	 */
	public void update(ViewLocationMapModel viewLoationMap) throws Exception {
		this.update(viewLoationMap, "mapId");
	}
	
	/**
	 * 根据场景ID查找场景和场景地点的关联关系
	 */
	public List<ViewLocationMapModel> queryManyByViewId(String viewId) {
		
		String sql = "select * from tab_view_location_map where viewId = ?";
		return this.query(sql, new Object[] {viewId}, ViewLocationMapModel.class, null);
	}
	
	/**
	 * 根据剧组id查询场景  场景地 对照关系
	 */
	public List<ViewLocationMapModel> queryViewLocationMapByCrewId(String crewId) {
		String sql = "select * from "+ViewLocationMapModel.TABLE_NAME+" where crewId = ?";
		return this.query(sql, new Object[] {crewId}, ViewLocationMapModel.class, null);
	}
	
	
	/**
	 * 根据场景ID删除场景和场景地点的关联关系
	 */
	public void deleteManyByViewId(String viewId) {
		
		String sql = "delete from tab_view_location_map where viewId = ?";
		
		this.getJdbcTemplate().update(sql.toString(), new Object[] {viewId});
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
			String sql = "delete from "+ViewLocationMapModel.TABLE_NAME +" where viewId = ?";
			this.getJdbcTemplate().batchUpdate(sql, args);
		}
	}
	/**
	 * 根据集场号列表删除场景和场景地点的关联关系
	 */
	public void deleteManyBySeriesViewNos(String crewId, List<String> seriesViewNos, int locationType) {
		StringBuilder querySql = new StringBuilder();
		querySql.append("select tvlm.mapId from tab_view_location_map tvlm, tab_view_info tvi, tab_view_location tvl ");
		querySql.append(" where tvi.crewId=? and tvlm.crewId=? and tvl.crewId=? and tvlm.viewId=tvi.viewId and tvlm.locationId = tvl.locationId and tvl.locationType=? ");
		
		querySql.append(" and (");
		for (int i = 0; i < seriesViewNos.size(); i++) {
			String seriesViewNo = seriesViewNos.get(i);
			
			String[] seriesViewNoArr = seriesViewNo.split("-");
			String seriesNo = seriesViewNoArr[0];
			String viewNo = seriesViewNoArr[1];
			
			if (i == 0) {
				querySql.append("(tvi.seriesNo = " + seriesNo + " and tvi.viewNo = '" + viewNo + "')");
			} else {
				querySql.append(" or (tvi.seriesNo = " + seriesNo + " and tvi.viewNo = '" + viewNo + "')");
			}
			
		}
		
		querySql.append(")");
		
		List<Map<String, Object>> existMapList = this.query(querySql.toString(), new Object[] {crewId, crewId, crewId, locationType}, null);
		
		if (existMapList != null && existMapList.size() > 0) {
			String mapIds = "";
			for (int i = 0; i < existMapList.size(); i++) {
				Map<String, Object> map = existMapList.get(i);
				String mapId = (String) map.get("mapId");
				
				if (i == 0) {
					mapIds = mapId;
				} else {
					mapIds += "," + mapId;
				}
			}
			
			
			mapIds = "'" + mapIds.replace(",", "','") + "'";
			String deleteSql = "delete from tab_view_location_map where mapId in ("+ mapIds +")  and crewId=?";
			this.getJdbcTemplate().update(deleteSql.toString(), new Object[] {crewId});
		}
	}
	
	/**
	 * 根据场景ID删除场景和场景地点的关联关系
	 * 多个场景ID用,隔开
	 */
	public void deleteManyByViewIds(String viewIds) {
		viewIds = "'" + viewIds.replace(",", "','") + "'";
		
		String sql = "delete from tab_view_location_map where viewId in (" + viewIds + ")";
		
		this.getJdbcTemplate().update(sql);
	}
	
	/**
	 * 根据多个条件查询场景地点信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ViewLocationMapModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + ViewLocationMapModel.TABLE_NAME + " where 1 = 1 ");
		
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
		List<ViewLocationMapModel> viewLocationMapList = this.query(sql.toString(), objArr, ViewLocationMapModel.class, page);
		
		return viewLocationMapList;
	}
	
	/**
	 * 根据场景地点的id删除场景地点和地点与场景之间的关联关系
	 * @param locationId
	 */
	public void deleteLocationInfoByLocationId(String locationId) {
		String sql = "DELETE FROM tab_view_location_map WHERE locationId = ?";
		this.getJdbcTemplate().update(sql, locationId);
	}
}
