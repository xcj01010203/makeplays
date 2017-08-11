package com.xiaotu.makeplays.notice.dao.clip;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.notice.model.clip.ClipPropModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 场记单道具信息
 * @author xuchangjian 2015-11-9下午3:40:05
 */
@Repository
public class ClipPropDao extends BaseDao<ClipPropModel> {

	/**
	 * 查询场记单中特殊道具信息
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	public List<ClipPropModel> queryByNoticeId(String crewId, String noticeId) {
		String sql = "select * from " + ClipPropModel.TABLE_NAME + " where crewId = ? and noticeId = ? order by num desc";
		return this.query(sql, new Object[] {crewId, noticeId}, ClipPropModel.class, null);
	}
	
	/**
	 * 根据道具ID查询道具信息
	 * @param crewId
	 * @param noticeId
	 * @param propIds
	 * @return
	 */
	public List<ClipPropModel> queryByIds(String crewId, String noticeId, String propIds) {
		propIds = "'" + propIds.replace(",", "','") + "'";
		
		String sql = "select * from " + ClipPropModel.TABLE_NAME + " where crewId=? and noticeId=? and propId in("+ propIds +")";
		return this.query(sql, new Object[] {crewId, noticeId}, ClipPropModel.class, null);
	}
	
	/**
	 * 通过通告单ID删除特殊道具信息
	 * @param crewId
	 * @param noticeId
	 */
	public void deleteByNoticeId(String crewId, String noticeId) {
		String sql = "delete from " + ClipPropModel.TABLE_NAME + " where crewId=? and noticeId=?";
		this.getJdbcTemplate().update(sql, new Object[] {crewId, noticeId});
	}
}
