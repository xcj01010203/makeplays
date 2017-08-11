package com.xiaotu.makeplays.notice.dao;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.notice.model.NoticeTimeModel;
import com.xiaotu.makeplays.utils.BaseDao;

@Repository
public class NoticeTimeDao extends BaseDao<NoticeTimeModel> {

	
	/**
	 * 查询最后一次时间设置
	 * @param crewId
	 * @return
	 * @throws Exception 
	 */
	public NoticeTimeModel queryLastNoticeTime(String crewId) throws Exception{
		
		String sql = "select * from "+NoticeTimeModel.TABLE_NAME+" where crewId=? order by createTime desc limit 0,1";
		
		return this.queryForObject(sql, new Object[]{crewId}, NoticeTimeModel.class);
		
	}
	
	/**
	 * 根据通告单的id查询通告单的详情
	 * @param noticeId
	 * @return
	 * @throws Exception
	 */
	public NoticeTimeModel queryNoticeTimeByNoticeId(String noticeId) throws Exception{
		String sql = "select * from "+NoticeTimeModel.TABLE_NAME+" where noticeId=? ";
		
		return this.queryForObject(sql, new Object[]{noticeId}, NoticeTimeModel.class);
	}
	
	/**
	 * 查询上一个同组通告单时间
	 * @param crewId
	 * @param groupId
	 * @return
	 * @throws Exception 
	 */
	public NoticeTimeModel queryLastGroupNoticeTime(String crewId, String groupId) throws Exception {
		String sql = "select tnt.* from tab_notice_time tnt, tab_notice_info tni where tni.crewId = ? and tni.groupId = ? and tnt.noticeId = tni.noticeId order by tnt.createTime desc limit 0, 1";
	
		return this.queryForObject(sql, new Object[] {crewId, groupId}, NoticeTimeModel.class);
	}
	
	/**
	 * 根据通告单id删除通告单的时间信息
	 * @param noticeId
	 */
	public void deleteNoticeTimeByNoticeId(String noticeId) {
		String sql = "delete from tab_notice_time where noticeId = ?";
		this.getJdbcTemplate().update(sql, noticeId);
	}
	
}
