package com.xiaotu.makeplays.notice.dao.clip;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.notice.model.clip.LiveConvertAddModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 现场转场信息
 * @author xuchangjian 2015-11-9下午3:38:29
 */
@Repository
public class LiveConvertAddDao extends BaseDao<LiveConvertAddModel> {

	/**
	 * 根据现场信息查询下面的转场信息
	 * @param liveId
	 * @return
	 */
	public List<LiveConvertAddModel> queryByLiveId(String crewId, String noticeId) {
		String sql = "select * from " + LiveConvertAddModel.TABLE_NAME + " where crewId = ? and noticeId = ? order by convertTime";
		return this.query(sql, new Object[] {crewId, noticeId}, LiveConvertAddModel.class, null);
	}
	
	/**
	 * 根据ID删除通告单现场信息转场信息
	 * @param convertIds
	 */
	public void deleteByIds(String convertIds) {
		convertIds = "'" + convertIds.replace(",", "','") + "'";
		String sql = "delete from " + LiveConvertAddModel.TABLE_NAME + " where convertId in ("+ convertIds +")";
		this.getJdbcTemplate().update(sql);
	}
}
