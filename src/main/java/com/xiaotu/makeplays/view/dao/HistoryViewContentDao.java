package com.xiaotu.makeplays.view.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.view.model.HistoryViewContentModel;

/**
 * 历史版本剧本
 * @author xuchangjian 2015-12-1下午4:19:53
 */
@Repository
public class HistoryViewContentDao extends BaseDao<HistoryViewContentModel> {

	/**
	 * 查询场景下的历史版本信息
	 * @param crewId
	 * @param viewId
	 * @return
	 */
	public List<HistoryViewContentModel> queryByViewId(String crewId, String viewId) {
		String sql = "select * from " + HistoryViewContentModel.TABLE_NAME + " where viewId=? and crewId=? order by createTime desc";
		return this.query(sql, new Object[] {viewId, crewId}, HistoryViewContentModel.class, null);
	}
	
	/**
	 * 查询场景指定版本的内容
	 * @param crewId
	 * @param viewId
	 * @param version
	 * @return
	 * @throws Exception 
	 */
	public HistoryViewContentModel queryByViewIdAndVersion(String crewId, String viewId, String version) throws Exception {
		String sql = "select * from " + HistoryViewContentModel.TABLE_NAME + " where viewId=? and crewId=? and version = ?";
		return this.queryForObject(sql, new Object[] {viewId, crewId, version}, HistoryViewContentModel.class);
	}
	
}
