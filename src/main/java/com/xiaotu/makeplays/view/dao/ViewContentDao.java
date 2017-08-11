package com.xiaotu.makeplays.view.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.view.model.ViewContentModel;
import com.xiaotu.makeplays.view.model.ViewInfoModel;
import com.xiaotu.makeplays.view.model.ViewRoleMapModel;

/**
 * 场景内容
 * @author xuchangjian
 */
@Repository
public class ViewContentDao extends BaseDao<ViewContentModel> {
	
	/**
	 * 查询剧组下未发布的剧本内容
	 * @param crewId
	 * @return
	 */
	public List<ViewContentModel> queryNotPublishedContentList(String crewId) {
		String sql = "select * from " + ViewContentModel.TABLE_NAME + " where status in (1, 2) and crewId = ?";
		return this.query(sql, new Object[] {crewId}, ViewContentModel.class, null);
	}
	
	/**
	 * 查询剧组下未发布的剧本内容
	 * @param crewId
	 * @return
	 */
	public int countNotPublishedContentList(String crewId) {
		String sql = "select count(1) from " + ViewContentModel.TABLE_NAME + " where status in (1, 2) and crewId = ?";
		return this.getJdbcTemplate().queryForInt(sql, crewId);
	}

	/**
	 * 批量新增操作
	 * @param atmosphereList
	 * @throws Exception 
	 */
	public void addMany (List<ViewContentModel> viewContentList) throws Exception {
		this.addBatch(viewContentList, ViewContentModel.class);
	}
	
	/**
	 * 更新数据
	 * @param viewContent	场景内容信息
	 * @throws Exception 
	 */
	public void update(ViewContentModel viewContent) throws Exception {
		this.update(viewContent, "contentId");
	}
	
	/**
	 * 批量更新数据
	 * @throws Exception 
	 */
	public void updateManyViewContentInfo(List<ViewContentModel> viewContentList) throws Exception {
		this.updateBatch(viewContentList, "contentId", ViewContentModel.class);
//		for (ViewContentModel viewContent : viewContentList) {
//			this.update(viewContent, "contentId");
//		}
	}
	
	/**
	 * 根据场景ID查找场景内容信息
	 * @param viewId
	 * @return
	 */
	public ViewContentModel queryByViewId(String viewId) {
		String sql ="select * from " + ViewContentModel.TABLE_NAME + " where viewId = ?";
		
		List<ViewContentModel> viewContentList =  this.query(sql, new Object[]{viewId}, ViewContentModel.class,null);
		
		if(null == viewContentList || viewContentList.size()==0){
			return null;
		}
		
		return viewContentList.get(0);
	}
	
	/**
	 * 根据剧组id查询剧本信息
	 * @param viewId
	 * @return
	 */
	public List<ViewContentModel> queryByCrewId(String crewId) {
		String sql ="select * from " + ViewContentModel.TABLE_NAME + " where crewId = ?";
		
		List<ViewContentModel> viewContentList =  this.query(sql, new Object[]{crewId}, ViewContentModel.class,null);
		
		
		return viewContentList;
	}
	
	/**
	 * 查询没有指定类型演员的场景、场景内容信息
	 * @param cewId
	 * @return
	 */
	public List<Map<String, Object>> queryNoMajorRoleView(String crewId, Integer viewRoleType, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tvi.seriesNo, ");
		sql.append(" 	tvi.viewNo, ");
		sql.append(" 	tvi.viewId, ");
		sql.append(" 	tvc.content ");
		sql.append(" FROM ");
		sql.append(" 	"+ ViewInfoModel.TABLE_NAME +" tvi, ");
		sql.append(" 	"+ ViewContentModel.TABLE_NAME +" tvc ");
		sql.append(" WHERE ");
		sql.append(" 	tvi.crewId = ? ");
		sql.append(" AND tvc.crewId = ? ");
		sql.append(" and tvi.viewId = tvc.viewId ");
		sql.append(" AND NOT EXISTS ( ");
		sql.append(" 	SELECT ");
		sql.append(" 		1 ");
		sql.append(" 	FROM ");
		sql.append(" 		"+ ViewRoleModel.TABLE_NAME +" tvr, ");
		sql.append(" 		"+ ViewRoleMapModel.TABLE_NAME +" tvrm ");
		sql.append(" 	WHERE ");
		sql.append(" 		tvrm.viewId = tvi.viewId ");
		sql.append(" 	AND tvrm.viewRoleId = tvr.viewRoleId ");
		sql.append(" 	AND tvr.viewRoleType = ? ");
		sql.append("    AND tvr.crewId = ? ");
		sql.append("    AND tvrm.crewId = ? ");
		sql.append(" ) ");
		sql.append(" AND tvc.content IS NOT NULL");
		
		return this.query(sql.toString(), new Object[] {crewId, crewId, viewRoleType, crewId, crewId}, page);
	}
	
	/**
	 * 查询未保存的场景、场景内容信息
	 * @param cewId
	 * @return
	 */
	public List<Map<String, Object>> queryViewContent(String crewId, Page page, Boolean isManualSave) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tvi.seriesNo, ");
		sql.append(" 	tvi.viewNo, ");
		sql.append(" 	tvi.viewId, ");
		sql.append(" 	tvc.title, ");
		sql.append(" 	tvc.content, ");
		sql.append("    tvi.isManualSave ");
		sql.append(" FROM ");
		sql.append(" 	"+ ViewInfoModel.TABLE_NAME +" tvi, ");
		sql.append(" 	"+ ViewContentModel.TABLE_NAME +" tvc ");
		sql.append(" WHERE ");
		sql.append(" 	tvi.crewId = ? ");
		sql.append(" AND tvc.crewId = ? ");
		sql.append(" and tvi.viewId = tvc.viewId ");
		sql.append(" and tvi.shootStatus = 0 ");
		if (isManualSave != null && isManualSave) {
			sql.append(" AND tvi.isManualSave = 1 ");
		}
		if (isManualSave != null && !isManualSave) {
			sql.append(" AND tvi.isManualSave = 0 ");
		}
		
		sql.append(" AND tvc.content IS NOT NULL ");
		sql.append(" order by tvi.seriesNo ");
		
		return this.query(sql.toString(), new Object[] {crewId, crewId}, page);
	}
	
	/**
	 * 计算剧组下场景的个数
	 * @param crewId
	 * @return
	 */
	public int countView(String crewId) {
		String sql = "select * from tab_view_info where crewId = ?";
		return this.getResultCount(sql, new Object[] {crewId});
	}
	
	/**
	 * 查询没有指定类型演员的场景、场景内容信息
	 * @param cewId
	 * @return
	 */
	public int countNoTypeRoleView(String crewId, Integer viewRoleType) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tvi.seriesNo, ");
		sql.append(" 	tvi.viewNo, ");
		sql.append(" 	tvi.viewId, ");
		sql.append(" 	tvc.content ");
		sql.append(" FROM ");
		sql.append(" 	"+ ViewInfoModel.TABLE_NAME +" tvi, ");
		sql.append(" 	"+ ViewContentModel.TABLE_NAME +" tvc ");
		sql.append(" WHERE ");
		sql.append(" 	tvi.crewId = ? ");
		sql.append(" AND tvc.crewId = ? ");
		sql.append(" and tvi.viewId = tvc.viewId ");
		sql.append(" AND NOT EXISTS ( ");
		sql.append(" 	SELECT ");
		sql.append(" 		1 ");
		sql.append(" 	FROM ");
		sql.append(" 		"+ ViewRoleModel.TABLE_NAME +" tvr, ");
		sql.append(" 		"+ ViewRoleMapModel.TABLE_NAME +" tvrm ");
		sql.append(" 	WHERE ");
		sql.append(" 		tvrm.viewId = tvi.viewId ");
		sql.append(" 	AND tvrm.viewRoleId = tvr.viewRoleId ");
		sql.append(" 	AND tvr.viewRoleType = ? ");
		sql.append("    AND tvr.crewId = ? ");
		sql.append("    AND tvrm.crewId = ? ");
		sql.append(" ) ");
		sql.append(" AND tvc.content IS NOT NULL");
		
		return this.getResultCount(sql.toString(), new Object[] {crewId, crewId, viewRoleType, crewId, crewId});
	}
	
	/**
	 * 查询未保存的场景、场景内容信息
	 * @param cewId
	 * @return
	 */
	public int countViewContent(String crewId, Boolean isManualSave) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tvi.seriesNo, ");
		sql.append(" 	tvi.viewNo, ");
		sql.append(" 	tvi.viewId, ");
		sql.append(" 	tvc.content ");
		sql.append(" FROM ");
		sql.append(" 	"+ ViewInfoModel.TABLE_NAME +" tvi, ");
		sql.append(" 	"+ ViewContentModel.TABLE_NAME +" tvc ");
		sql.append(" WHERE ");
		sql.append(" 	tvi.crewId = ? ");
		sql.append(" AND tvc.crewId = ? ");
		sql.append(" and tvi.viewId = tvc.viewId ");
		sql.append(" AND tvi.shootStatus = 0 ");
		if (isManualSave != null && isManualSave) {
			sql.append(" AND tvi.isManualSave = 1 ");
		}
		if (isManualSave != null && !isManualSave) {
			sql.append(" AND tvi.isManualSave = 0 ");
		}
		sql.append(" AND tvc.content IS NOT NULL ");
		
		return this.getResultCount(sql.toString(), new Object[] {crewId, crewId});
	}
	
	/**
	 * 批量修改剧本状态
	 * @param viewIds
	 * @param status
	 */
	public void updateStatusBatch(String viewIds, int status) {
		viewIds = "'" + viewIds.replace(",", "','") + "'";
		
		String sql = "update tab_view_content set status=? where viewId in(" + viewIds + ")";
		this.getJdbcTemplate().update(sql, new Object[] {status});
	}
	
	/**
	 * 根据集-场号查询剧本内容信息
	 * @param seriesNo
	 * @param viewNo
	 * @return
	 * @throws Exception 
	 */
	public ViewContentModel queryBySeriesViewNo (String crewId, int seriesNo, String viewNo) throws Exception {
		String sql = "select tvc.* from " + ViewContentModel.TABLE_NAME + " tvc, " + ViewInfoModel.TABLE_NAME + " tvi where tvc.viewId = tvi.viewId and tvi.seriesNo=? and tvi.viewNo = ? and tvi.crewId=? and tvc.crewId=?";
		
		return this.queryForObject(sql, new Object[] {seriesNo, viewNo, crewId, crewId}, ViewContentModel.class);
	}
	
	/**
	 * 删除指定场的剧本内容信息
	 * @param crewId
	 * @param viewId
	 */
	public void deleteByViewId(String crewId, String viewId) {
		String sql = "delete from tab_view_content where crewId=? and viewId=?";
		this.getJdbcTemplate().update(sql, new Object[] {crewId, viewId});
	}
	
	/**
	 * 批量删除指定场的剧本内容信息
	 * @param crewId
	 * @param viewIds
	 */
	public void deleteByViewIds(String crewId, String viewIds) {
		viewIds = "'" + viewIds.replace(",", "','") + "'";
		
		String sql = "delete from tab_view_content where crewId=? and viewId in("+ viewIds +")";
		this.getJdbcTemplate().update(sql, new Object[] {crewId});
	}
	
	/**
	 * 查询所有含有未发布剧本内容的集次
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryNotPublishedSeriesNo(String crewId) {
		String sql = "select distinct tvi.seriesNo from tab_view_content tvc, tab_view_info tvi where tvc.viewId = tvi.viewId and tvc.crewId = ? and tvc.status in (1, 2) order by tvi.seriesNo";
		return this.query(sql, new Object[] {crewId}, null);
	}
	
	/**
	 * 查询场次的已读人员信息
	 * 之所以不直接把tab_view_content中的内容全部查询出来，是因为考虑到表中数据太大（content+readedPeopleIds），有可能出现效率问题
	 * @param crewId
	 * @return	场景ID，已读用户ID
	 */
	public List<Map<String, Object>> queryReadedPeopleInfo(String crewId) {
		String sql = "select viewId, readedPeopleIds from tab_view_content where crewId = ?";
		return this.query(sql, new Object[] {crewId}, null);
	}
}
