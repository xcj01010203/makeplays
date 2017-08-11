package com.xiaotu.makeplays.user.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.user.model.JoinCrewApplyMsgModel;
import com.xiaotu.makeplays.user.model.constants.JoinCrewAuditStatus;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 入组申请消息
 * @author xuchangjian 2016-5-16下午4:45:17
 */
@Repository
public class JoinCrewApplyMsgDao extends BaseDao<JoinCrewApplyMsgModel> {

	/**
	 * 查询指定剧组内指定用户的正在审核中的入组信息
	 * @param crewId
	 * @param userId
	 * @return
	 */
	public JoinCrewApplyMsgModel queryByCrewIdAndUserId (String crewId, String userId) {
		String sql = "select * from tab_joinCrew_applyMsg where aimCrewId = ? and applyerId = ? and status = " + JoinCrewAuditStatus.Auditing.getValue();
		List<JoinCrewApplyMsgModel> messageList = this.query(sql, new Object[] {crewId, userId}, JoinCrewApplyMsgModel.class, null);
		
		if (messageList != null && messageList.size() > 0) {
			return messageList.get(0);
		}
		return null;
	}
	
	/**
	 * 查询剧组下指定状态的入组信息
	 * 该查询和用户表关联，查询出用户其他信息
	 * @param crewId
	 * @param status	状态：1表示审核中  2表示审核通过  3表示审核不通过，如果为空，则查询出所有信息
	 * @return
	 */
	public List<Map<String, Object>> queryCrewAuditingMsg(String crewId, Integer status) {
		List<Object> params = new ArrayList<Object>();
		params.add(crewId);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tja.*, tui.userId, ");
		sql.append(" 	tui.realName userName, ");
		sql.append(" 	tui.phone, ");
		sql.append("    dtui.realName dealerName ");
		sql.append(" FROM ");
		sql.append(" 	tab_joinCrew_applyMsg tja left join tab_user_info dtui on tja.dealerId = dtui.userId, ");
		sql.append(" 	tab_user_info tui ");
		sql.append("     ");
		sql.append(" WHERE ");
		sql.append(" 	tja.aimCrewId = ? ");
		if (status != null) {
			params.add(status);
			sql.append(" AND tja. STATUS = ? ");
		}
		sql.append(" AND tja.applyerId = tui.userId ");
		sql.append(" order by createTime ");		
		
		return this.query(sql.toString(), params.toArray(), null);
	}
}
