package com.xiaotu.makeplays.feedback.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.feedback.model.FeedbackModel;
import com.xiaotu.makeplays.user.controller.filter.FeedbackFilter;
import com.xiaotu.makeplays.user.model.constants.UserType;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 用户反馈信息
 * @author xuchangjian 2016-10-10下午4:49:08
 */
@Repository
public class FeedbackDao extends BaseDao<FeedbackModel>{
	
	/**
	 * 查询客服能看到的反馈用户列表
	 * @param userId
	 * @param loginUserType 用户类型：1-系统管理员  2-客服  3-普通剧组用户  4--总客服
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryFeedBackUserList(String userId, int loginUserType, Page page, FeedbackFilter filter) {
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		sql.append(" select * from (");
		sql.append(" SELECT ");
		sql.append(" 	tfi.userId,tui.realName userName,tui.phone ");
		sql.append("	,if(max(tfrs.createTime) is null or max(tfrs.createTime)<max(tfi.createTime),max(tfi.createTime),max(tfrs.createTime)) as statusUpdateTime ");
		sql.append("	,count(distinct tfi.id) as feedbackNum ");
		sql.append("	,if(min(tfrs.status) is null or min(tfrs.status)=1,min(tfi.status),min(tfrs.status)) as status ");
		sql.append(" FROM ");
		sql.append(" 	tab_feedback_info tfi ");
		sql.append(" 	inner join tab_user_info tui on tfi.userId=tui.userId ");
		sql.append(" 	left join ( ");
		sql.append(" 		select tfr.* ");
		sql.append(" 		from tab_feedback_reply tfr ");
		sql.append(" 		inner join tab_user_info tuir on tuir.userId=tfr.userId ");
		sql.append(" 		where tuir.type= " + UserType.CrewUser.getValue());
		sql.append(" 	) tfrs on tfrs.feedbackId=tfi.id ");
		sql.append(" where 1=1 ");
		if(loginUserType == 2) {
			sql.append(" and (tfi.userId IN ( ");
			sql.append(" 	SELECT ");
			sql.append(" 		tcum.userId ");
			sql.append(" 	FROM ");
			sql.append(" 		tab_crew_user_map tcum ");
			sql.append(" 	LEFT JOIN tab_crew_user_map tcum1 ON tcum.crewId = tcum1.crewId ");
			sql.append(" 	WHERE ");
			sql.append(" 		tcum1.userId = ? ");
			sql.append(" ) ");
			sql.append(" or tfi.userId not in (select userId from tab_crew_user_map)) ");
			params.add(userId);
		}
		
		//反馈、消息内容
		String content = filter.getContent();
		if(StringUtils.isNotBlank(content)) {
			sql.append(" and tfi.userId in (select tfi2.userId FROM tab_feedback_info tfi2 ");
			sql.append(" LEFT JOIN tab_feedback_reply tfr2 ON tfr2.feedbackId = tfi2.id ");
			sql.append(" where tfi2.message like ? or tfr2.reply like ?) ");
			content = content.replaceAll("%", "\\\\%");
			content = content.replaceAll("_", "\\\\_");
			params.add("%" + content + "%");
			params.add("%" + content + "%");
		}
		//用户名
		String userName = filter.getUserName();
		if(StringUtils.isNotBlank(userName)) {
			userName = userName.replaceAll("%", "\\\\%");
			userName = userName.replaceAll("_", "\\\\_");
			sql.append(" and tui.realName like ? ");
			params.add("%" + userName + "%");
		}
		sql.append(" group by tfi.userId,tui.realName,tui.phone ");
		sql.append(" ) mytable where 1=1 ");
		//状态
		Integer status = filter.getStatus();
		if(status != null) {
			sql.append(" and status=? ");
			params.add(status);
		}
		//时间
		String startTime = filter.getStartTime();
		if(StringUtils.isNotBlank(startTime)) {
			sql.append(" and statusUpdateTime >= ? ");
			params.add(startTime);
		}
		String endTime = filter.getEndTime();
		if(StringUtils.isNotBlank(endTime)) {
			sql.append(" and statusUpdateTime <= ? ");
			params.add(endTime);
		}
		
		sql.append(" order by statusUpdateTime desc,userId ");
		return this.query(sql.toString(), params.toArray(), page);
	}
	
	/**
	 * 查询用户的意见反馈列表，包括反馈信息
	 * @param userId
	 * @return
	 */
//	@SuppressWarnings("unchecked")
//	public List<Map<String, Object>> queryUserFeedBackList(String userId, Page page) {
//		StringBuffer sql = new StringBuffer();
//		sql.append(" 	tmp.id AS replyId, ");
//		sql.append(" 	tmp.userId AS replyUserId, ");
//		sql.append(" 	tmp.realName AS replyUserName, ");
//		sql.append(" 	tmp.reply, ");
//		sql.append(" 	tmp.createTime AS replyTime, ");
//		sql.append(" 	tmp.clientType AS replyClientType ");
//		
//		
//		
//		sql.append(" SELECT ");
//		sql.append(" 	tfi.*, tmp.realName, ");
//		sql.append(" 	tui.phone, ");
//		sql.append(" 	tui.type userType, ");
//		sql.append(" FROM ");
//		sql.append(" 	tab_feedback_info tfi ");
//		sql.append(" INNER JOIN tab_user_info tui ON tui.userId = tfi.userId ");
//		sql.append(" LEFT JOIN ( ");
//		sql.append(" 	SELECT tfi.id, IF (max(tfr.createTime) IS NULL OR max(tfr.createTime) < max(tfi.createTime), max(tfi.createTime), max(tfr.createTime)) AS newTime ");
//		sql.append(" 	FROM ");
//		sql.append(" 		tab_feedback_info tfi ");
//		sql.append(" 	LEFT JOIN tab_feedback_reply tfr ON tfr.feedbackId = tfi.id ");
//		sql.append(" 	GROUP BY ");
//		sql.append(" 		tfi.id ");
//		sql.append(" ) tfir ON tfir.id = tfi.id ");
//		sql.append(" WHERE ");
//		sql.append(" 	tfi.userId = ? ");
//		sql.append(" ORDER BY ");
//		sql.append(" 	newTime DESC, ");
//		sql.append(" 	tfi.id, ");
//		sql.append(" 	tmp.createTime ");
//		if (page) {
//			
//		}
//		
//		
//		
//		sql.append(" LEFT JOIN ( ");
//		sql.append(" 	SELECT ");
//		sql.append(" 		tfr.*, tuir.realName, tuir.type ");
//		sql.append(" 	FROM ");
//		sql.append(" 		tab_user_info tuir, ");
//		sql.append(" 		tab_feedback_reply tfr ");
//		sql.append(" 	WHERE ");
//		sql.append(" 		tfr.userId = tuir.userId ");
//		sql.append(" ) tmp ON tmp.feedbackId = tfi.id ");
//		
//		
//		return this.query(sql.toString(), new Object[]{userId}, page);
//	}
	
	/**
	 * 根据ID查询反馈信息
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public FeedbackModel queryById(String id) throws Exception {
		String sql = "select * from " + FeedbackModel.TABLE_NAME + " where id = ?";
		return this.queryForObject(sql, new Object[] {id}, FeedbackModel.class);
	}
	
	/**
	 * 查询指定人的反馈列表
	 * @param userId
	 * @param page
	 * @return
	 */
	public List<FeedbackModel> queryFeedbackList(String userId, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select tfi.*, ");
		sql.append(" IF (max(tfr.createTime) IS NULL OR max(tfr.createTime) < max(tfi.createTime), max(tfi.createTime), max(tfr.createTime)) AS newTime");
		sql.append(" from ");
		sql.append("   tab_feedback_info tfi ");
		sql.append(" LEFT JOIN tab_feedback_reply tfr ON tfr.feedbackId = tfi.id ");
		sql.append(" where tfi.userId = ? ");
		sql.append(" group by tfi.id ");
		sql.append(" order by newTime desc ");
		return this.query(sql.toString(), new Object[] {userId}, FeedbackModel.class, page);
	}
	
	/**
	 * 查询反馈的回复信息
	 * @param feedBackId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryReplyById(String feedBackId) {
		String sql = "select tfr.id as replyId,tfr.userId as replyUserId," 
				+ " tui.realName as replyUserName,tfr.reply,tfr.createTime as replyTime" 
				+ " from tab_feedback_reply tfr" 
				+ " inner join tab_user_info tui on tui.userId=tfr.userId" 
				+ " where tfr.feedBackId=?"
				+ " order by tfr.createTime";
		return this.query(sql, new Object[]{feedBackId}, null);
	}
	
	/**
	 * 更新用户反馈状态为已读
	 * @param userId
	 */
	public void updateFeedBackStatus(String userId) {
		String sql = "update tab_feedback_info set status=1 where userId=? and status=0";
		this.getJdbcTemplate().update(sql, new Object[]{userId});
	}
}
