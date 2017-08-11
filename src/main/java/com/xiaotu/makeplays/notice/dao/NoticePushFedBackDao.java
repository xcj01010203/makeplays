package com.xiaotu.makeplays.notice.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.notice.model.NoticePushFedBackModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 通告单push发聩信息
 * @author xuchangjian 2015-11-17下午2:23:19
 */
@Repository
public class NoticePushFedBackDao extends BaseDao<NoticePushFedBackModel> {

	/**
	 * 查询用户针对指定通告单的待反馈状态的反馈信息
	 * 用户移动端反馈时更新反馈状态时使用，如果查出多条记录，则取第一条进行信息更新
	 * @param crewId
	 * @param noticeId
	 * @param noticeVersion
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	public NoticePushFedBackModel queryToBackInfoByNoticeInfo(String crewId, String noticeId, String noticeVersion, String userId) throws Exception {
		String sql = "select * from tab_notice_pushFedBack where crewId=? and noticeId=? and noticeVersion=? and userId=? and (backStatus=1 or backStatus=2) order by backStatus ";
		Object[] params = new Object[] {crewId, noticeId, noticeVersion, userId}; 
		return this.queryForObject(sql, params, NoticePushFedBackModel.class);
	}
	
	/**
	 * 查询用户针对指定版本的通告单最新的反馈信息
	 * @param crewId
	 * @param noticeId
	 * @param noticeVersion
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> queryFedBackInfoByNoticeInfo(String crewId, String noticeId, String noticeVersion) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" tui.realName, tnpa.*, GROUP_CONCAT(DISTINCT tsi.roleName order by tsi.roleId) roleNames ");
		sql.append(" FROM ");
		sql.append(" 	tab_notice_pushFedBack tnpa, ");
		sql.append(" 	tab_crew_user_map tcum, ");
		sql.append(" 	tab_sysrole_info tsi, ");
		sql.append(" 	tab_user_role_map turm, ");
		sql.append(" 	tab_user_info tui ");
		sql.append(" WHERE ");
		sql.append(" 	tnpa.crewId = ? ");
		sql.append(" AND tnpa.noticeId = ? ");
		sql.append(" AND tnpa.noticeVersion = ? ");
		sql.append(" AND tnpa.userId = tcum.userId ");
		sql.append(" AND tcum.crewId = ? ");
		sql.append(" AND tnpa.userId = turm.userId ");
		sql.append(" AND turm.roleId = tsi.roleId ");
		sql.append(" AND tui.userId = tnpa.userId ");
		sql.append(" AND NOT EXISTS ( ");
		sql.append(" 	SELECT ");
		sql.append(" 		1 ");
		sql.append(" 	FROM ");
		sql.append(" 		tab_notice_pushFedBack tnpb ");
		sql.append(" 	WHERE ");
		sql.append(" 		tnpb.userid = tnpa.userid ");
		sql.append(" 		and tnpb.noticeId = tnpa.noticeId ");
		sql.append(" 		and tnpb.noticeVersion = tnpa.noticeVersion ");
		sql.append(" 		and tnpb.crewId = tnpa.crewId ");
		sql.append(" 	AND tnpb.statusupdatetime > tnpa.statusupdatetime ");
		sql.append(" ) ");
		sql.append(" GROUP BY tnpa.userId ");
		sql.append(" ORDER BY tcum.sequence ");
		
		return this.query(sql.toString(), new Object[] {crewId, noticeId, noticeVersion, crewId}, null);
	}
	
	/**
	 * 删除指定版本通告单的反馈信息
	 * @param crewId
	 * @param noticeId
	 * @param version
	 */
	public void deleteByNoticeVersion(String crewId, String noticeId) {
		String sql = "delete from " + NoticePushFedBackModel.TABLE_NAME + " where crewId = ? and noticeId = ?";
		this.getJdbcTemplate().update(sql, new Object[] {crewId, noticeId});
	}
	
	/**
	 * 根据通告单id查询反馈列表
	 * @param noticeId
	 * @return
	 */
	public List<Map<String, Object>> queryFedbackByNoticeId(String noticeId, String noticeVersion){
		String sql = "SELECT * FROM tab_notice_pushFedBack WHERE noticeId = ? AND noticeVersion = ?";
		//String sql = "SELECT * FROM tab_notice_pushFedBack WHERE noticeId = ?";
		return this.query(sql, new Object[] {noticeId, noticeVersion}, null);
	}
	
	/**
	 * 根据通告单的id和时间更新版本信息
	 * @param noticeId
	 */
	public void updateFacdbackVersion(String noticeId, String noticeUpdateTimeStr, String noticeVersion) {
		String sql = " UPDATE tab_notice_pushFedBack SET noticeVersion = ? WHERE noticeId = ? AND noticeVersion = ?";
		this.getJdbcTemplate().update(sql, noticeUpdateTimeStr, noticeId, noticeVersion);
	}
}
