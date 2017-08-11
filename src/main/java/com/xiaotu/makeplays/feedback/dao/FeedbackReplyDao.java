package com.xiaotu.makeplays.feedback.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.feedback.model.FeedBackReplyModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * @类名：FeedbackReplyDao.java
 * @作者：李晓平
 * @时间：2017年3月9日 下午4:26:49
 * @描述：客服回复信息
 */
@Repository
public class FeedbackReplyDao extends BaseDao<FeedBackReplyModel>{
	
	/**
	 * 查询回复列表
	 * 该方法还会查询出回复人的信息
	 * @param feedbackIds
	 * @return
	 */
	public List<Map<String, Object>> queryReplyListWithUserInfo(String feedbackIds) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tfr.*, tui.realName, ");
		sql.append(" 	tui.type replyerType ");
		sql.append(" FROM ");
		sql.append(" 	tab_feedback_reply tfr, ");
		sql.append(" 	tab_user_info tui ");
		sql.append(" WHERE ");
		sql.append(" 	tfr.userId = tui.userId ");
		sql.append(" AND tfr.feedbackId IN ('"+ feedbackIds.replace(",", "','") +"') ");
		sql.append(" order by tfr.createTime");
		
		return this.query(sql.toString(), null, null);
	}
	
	/**
	 * 把指定用户的反馈中所有客服回复的消息设置成已读
	 * @param userId
	 */
	public void readKefuReplyInfo(String userId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE tab_feedback_reply tfr ");
		sql.append(" SET `status` = 1 ");
		sql.append(" WHERE ");
		sql.append(" 	EXISTS ( ");
		sql.append(" 		SELECT ");
		sql.append(" 			1 ");
		sql.append(" 		FROM ");
		sql.append(" 			tab_user_info tui, ");
		sql.append(" 			tab_feedback_info tfi ");
		sql.append(" 		WHERE ");
		sql.append(" 			tfr.userId = tui.userId ");
		sql.append(" 		AND tui.type = 2 ");
		sql.append(" 		AND tfi.id = tfr.feedbackId ");
		sql.append(" 		AND tfi.userId = ? ");
		sql.append(" 		AND tfr. STATUS = 0 ");
		sql.append(" 	) ");
		this.getJdbcTemplate().update(sql.toString(), userId);
	}
	
	/**
	 * 把指定用户的所有回复消息状态设置成已读
	 * @param userId
	 */
	public void readUserReplyInfo(String userId) {
		String sql = "update tab_feedback_reply set status=1 where userId=? and status=0";
		this.getJdbcTemplate().update(sql, userId);
	}
	
	/**
	 * 查询指定用户的反馈中所有客服的回复列表
	 * 返回状态为未读的回复
	 * @param userId
	 * @return
	 */
	public List<FeedBackReplyModel> queryKefuReplyInfo(String userId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tfr.* ");
		sql.append(" FROM ");
		sql.append(" 	tab_feedback_reply tfr, ");
		sql.append(" 	tab_user_info tui, ");
		sql.append(" 	tab_feedback_info tfi ");
		sql.append(" WHERE ");
		sql.append(" 	tfr.userId = tui.userId ");
		sql.append(" AND tui.type = 2 ");
		sql.append(" AND tfi.id = tfr.feedbackId ");
		sql.append(" AND tfi.userId = ? ");
		sql.append(" AND tfr. STATUS = 0 ");
		return this.query(sql.toString(), new Object[] {userId}, FeedBackReplyModel.class, null);
	}
}
