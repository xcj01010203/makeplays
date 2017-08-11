package com.xiaotu.makeplays.notice.dao.clip;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.notice.model.clip.ClipCommentModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 场记单备注信息
 * @author xuchangjian 2015-11-9下午3:40:49
 */
@Repository
public class ClipCommentDao extends BaseDao<ClipCommentModel> {

	/**
	 * 根据通告单ID查询重要备注信息
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	public List<ClipCommentModel> queryByNoticeId(String crewId, String noticeId) {
		String sql = "select * from " + ClipCommentModel.TABLE_NAME + " where crewId=? and noticeId=? order by serverTime";
		return this.query(sql, new Object[] {crewId, noticeId}, ClipCommentModel.class, null);
	}
	
	/**
	 * 根据通告单ID删除场记单中重要备注信息
	 * @param crewId
	 * @param noticeId
	 */
	public void deleteByNoticeId(String crewId, String noticeId) {
		String sql = "delete from " + ClipCommentModel.TABLE_NAME + " where crewId=? and noticeId=?";
		this.getJdbcTemplate().update(sql, new Object[] {crewId, noticeId});
	}
	
	/**
	 * 根据ID查询重要备注信息
	 * @param crewId
	 * @param noticeId
	 * @param commentIds
	 */
	public List<ClipCommentModel> queryByIds(String crewId, String noticeId, String commentIds) {
		commentIds = "'" + commentIds.replace(",", "','") + "'";
		String sql = "select * from " + ClipCommentModel.TABLE_NAME + " where crewId=? and noticeId=? and commentId in(" + commentIds + ")";
		return this.query(sql, new Object[] {crewId, noticeId}, ClipCommentModel.class, null);
	}
}
