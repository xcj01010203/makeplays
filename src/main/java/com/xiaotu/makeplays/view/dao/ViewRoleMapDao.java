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
import com.xiaotu.makeplays.view.model.ViewRoleMapModel;

/**
 * 场景和角色关联关系
 * @author xuchangjian 2016-10-25下午1:50:03
 */
@Repository
public class ViewRoleMapDao extends BaseDao<ViewRoleMapModel> {
	
	
	/**
	 * @Description  根据剧组id查询当前场景 角色对应关系
	 * @param crewId
	 * @return
	 */
	public List<ViewRoleMapModel> queryViewRoleMapInfoByCrewId(String crewId){
		String sql = " select * from "+ViewRoleMapModel.TABLE_NAME +" where crewId = ?";
		List<ViewRoleMapModel> viewRoleMapList = this.query(sql, new Object[]{crewId}, ViewRoleMapModel.class, null);
		return viewRoleMapList;
	}
	
	/**
	 * 批量新增操作
	 * @param atmosphereList
	 * @throws Exception 
	 */
	public void addMany (List<ViewRoleMapModel> viewRoleMapList) throws Exception {
		this.addBatch(viewRoleMapList, ViewRoleMapModel.class);
//		for (ViewRoleMapModel sceneRoleMap : viewRoleMapList) {
//			this.addViewRoleMap(sceneRoleMap);
//		}
	}
	
	/**
	 * 新增场景和角色的关联关系
	 * @param viewRoleMapModel
	 */
	public void addViewRoleMap(ViewRoleMapModel viewRoleMapModel) {
		String sql = "{call insert_viewrole_map(?,?,?,?)}";
		
		List<Object> argsList = new ArrayList<Object>();
		argsList.add(viewRoleMapModel.getMapId());
		argsList.add(viewRoleMapModel.getCrewId());
		argsList.add(viewRoleMapModel.getViewId());
		argsList.add(viewRoleMapModel.getViewRoleId());
        
		this.getJdbcTemplate().update(sql, argsList.toArray());
	}



	/**
	 * 根据场景ID删除场景和场景演员角色的关联关系
	 */
	public void deleteManyByViewId(String viewId) {
		
		String sql = "delete from " + ViewRoleMapModel.TABLE_NAME + " where viewId = ?";
		
		this.getJdbcTemplate().update(sql.toString(), new Object[] {viewId});
	}
	
	/**
	 * @Description 根据viewid 批量删除场景角色对照信息
	 * @param viewIdList
	 */
	public void deleteBatchByViewId(List<String> viewIdList){
		if(viewIdList!=null&&viewIdList.size()>0){
			List<Object[]> args = new ArrayList<Object[]>();
			for(String viewId :viewIdList){
				if(StringUtils.isNotBlank(viewId)){
					Object[] oo = {viewId};
					args.add(oo);
				}
			}
			String sql = " delete from "+ ViewRoleMapModel.TABLE_NAME + " where viewId = ?";
			this.getJdbcTemplate().batchUpdate(sql,args);
			
		}
		
	}
	
	
	/**
	 * 删除场景和场景演员角色的关联关系
	 */
	public void deleteManyBySeriesViewNos(String crewId, List<String> seriesViewNos, int viewRoleType) {
		StringBuilder querySql = new StringBuilder();
		querySql.append("select tvrm.mapId from tab_view_info tvi, tab_view_role_map tvrm, tab_view_role tvr where ");
		querySql.append(" tvrm.viewId = tvi.viewId AND tvrm.viewRoleId = tvr.viewRoleId AND tvr.viewRoleType =? and tvr.crewId=? and tvrm.crewId=? and tvi.crewId=? ");
		
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
		
		List<Map<String, Object>> existMapList = this.query(querySql.toString(), new Object[] {viewRoleType, crewId, crewId, crewId}, null);
		
		
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
			String deleteSql = "delete from tab_view_role_map where mapId in ("+ mapIds +")  and crewId=?";
			this.getJdbcTemplate().update(deleteSql.toString(), new Object[] {crewId});
		}
	}
	
	/**
	 * 根据场景ID删除场景和场景演员角色的关联关系
	 * 多个场景ID用,隔开
	 */
	public void deleteManyByViewIds(String viewIds) {
		viewIds = "'" + viewIds.replace(",", "','") + "'";
		
		String sql = "delete from " + ViewRoleMapModel.TABLE_NAME + " where viewId in (" + viewIds + ")";
		
		this.getJdbcTemplate().update(sql);
	}
	
	/**
	 * 根据多个条件查询演员角色关联关系信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ViewRoleMapModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + ViewRoleMapModel.TABLE_NAME + " where 1 = 1 ");
		
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
		List<ViewRoleMapModel> sceneRoleMapList = this.query(sql.toString(), objArr, ViewRoleMapModel.class, page);
		
		return sceneRoleMapList;
	}
	
	/**
	 * 更新信息
	 * @param viewRoleMapModel
	 * @throws Exception 
	 */
	public void update(ViewRoleMapModel viewRoleMapModel) throws Exception {
		this.update(viewRoleMapModel, "mapId");
	}
	
	/**
	 * 根据角色ID批量删除关联关系
	 * 多个角色ID用逗号隔开
	 * @param viewRoleIds
	 */
	public void deleteByViewRoleIds(String viewRoleIds) {
		viewRoleIds = "'" + viewRoleIds.replace(",", "','") + "'";
		
		String sql = "delete from tab_view_role_map where viewRoleId in("+ viewRoleIds +")";
		this.getJdbcTemplate().update(sql);
	}
	
	/**
	 * 删除剧组下所有未手动保存的场景和角色的关联
	 * @param crewId
	 */
	public void deleteNoSaveViewRoleMap(String crewId) {
		String sql = "DELETE from tab_view_role_map where viewId in(SELECT viewId from tab_view_info where isManualSave = 0 and shootStatus = 0 and crewId = ?)";
		this.getJdbcTemplate().update(sql, crewId);
	}
}
