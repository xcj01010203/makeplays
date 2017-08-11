package com.xiaotu.makeplays.notice.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.notice.model.NoticeRoleTimeModel;
import com.xiaotu.makeplays.utils.BaseDao;

@Repository
public class NoticeRoleTimeDao extends BaseDao<NoticeRoleTimeModel>{

	/**
	 * 查询通告单角色时间安排
	 * @param noticeId
	 * @param roleId
	 * @return
	 * @throws Exception
	 */
	public NoticeRoleTimeModel queryNoticeRoleTimeByNoticeIdAndRoleId(String noticeId,String roleId) throws Exception{
		
		String sql = "select * from "+NoticeRoleTimeModel.TABLE_NAME+" where noticeId=? and viewRoleId=? ";
		
		return this.queryForObject(sql, new Object[]{noticeId,roleId}, NoticeRoleTimeModel.class);
		
	}
	
	/**
	 * 根据通告单删除演员通告时间表
	 * @param noticeId
	 * @return 
	 */
	public int deleteByNoticeId(String noticeId) {
		String sql = "delete from " + NoticeRoleTimeModel.TABLE_NAME + " where noticeId = ?";
		return this.getJdbcTemplate().update(sql, new Object[] {noticeId});
	}
	
	/**
	 * 查询通告单角色最早的时间安排
	 * @param noticeId
	 * @param roleId
	 * @param roleIdList 角色列表
	 * @return
	 * @throws Exception
	 */
	public NoticeRoleTimeModel queryNoticeRoleTimeByNoticeIdAndRoleId(String noticeId, List<String> roleIdList) throws Exception{
		String roleIds = "";
		for (String roleId : roleIdList) {
			roleIds += roleId + ",";
		}
		roleIds = roleIds.substring(0, roleIds.length() - 1);
		roleIds = "'" + roleIds.replaceAll(",", "','") + "'";
		
		String sql = "select * from "+NoticeRoleTimeModel.TABLE_NAME+" where noticeId=? and viewRoleId in ("+ roleIds +") order by arriveTime asc limit 0, 1";
		NoticeRoleTimeModel noticeRoleTime = null;
		Object[] args = new Object[] {noticeId};
		if (getResultCount(sql, args) == 1) {
			noticeRoleTime = this.queryForObject(sql, args, NoticeRoleTimeModel.class);
		}
		
		return noticeRoleTime;
		
	}
	
}
