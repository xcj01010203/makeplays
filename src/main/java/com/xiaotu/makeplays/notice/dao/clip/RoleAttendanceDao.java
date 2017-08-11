package com.xiaotu.makeplays.notice.dao.clip;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.notice.model.clip.RoleAttendanceModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 演员出勤信息
 * @author xuchangjian 2015-11-9下午3:39:16
 */
@Repository
public class RoleAttendanceDao extends BaseDao<RoleAttendanceModel> {

	/**
	 * 查询通告单下演员出勤信息
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
		public List<RoleAttendanceModel> queryByNoticeId(String crewId, String noticeId) {
			String sql = "select * from " + RoleAttendanceModel.TABLE_NAME + " where crewId=? and noticeId=?";
		
		return this.query(sql.toString(), new Object[] {crewId, noticeId},RoleAttendanceModel.class, null);
	}
	
	/**
	 * 删除通告单下场记单中记录的演员出勤信息
	 * @param crewId
	 * @param noticeId
	 */
	public void deleteByNoticeId(String crewId, String noticeId) {
		String sql = "delete from " + RoleAttendanceModel.TABLE_NAME + " where crewId=? and noticeId=?";
		this.getJdbcTemplate().update(sql, new Object[] {crewId, noticeId});
	}
	
	/**
	 * 根据ID批量删除演员出勤信息
	 * @param crewId
	 * @param noticeId
	 * @param attendanceId
	 */
	public void deleteByIds(String crewId, String noticeId, String attendanceIds) {
		attendanceIds = "'" + attendanceIds.replace(",", "','") + "'";
		String sql = "delete from " + RoleAttendanceModel.TABLE_NAME + " where crewId=? and noticeId=? and attendanceId in("+ attendanceIds +")";
		this.getJdbcTemplate().update(sql, new Object[] {crewId, noticeId});
	}
}
