package com.xiaotu.makeplays.notice.dao.clip;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.notice.model.clip.LiveConvertAddModel;
import com.xiaotu.makeplays.notice.model.clip.ShootLiveModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 拍摄现场信息
 * @author xuchangjian 2015-11-9下午3:37:41
 */
@Repository
public class ShootLiveDao extends BaseDao<ShootLiveModel> {

	/**
	 * 根据通告单ID查询现场信息
	 * @param crewId
	 * @param noticeId
	 * @return
	 * @throws Exception 
	 */
	public ShootLiveModel queryByNoticeId(String crewId, String noticeId) throws Exception {
		String sql = "select * from " + ShootLiveModel.TABLE_NAME + " where crewId = ? and noticeId = ?";
		if (getResultCount(sql, new Object[] {crewId, noticeId}) == 1) {
			return this.queryForObject(sql, new Object[] {crewId, noticeId}, ShootLiveModel.class);
		} else {
			return null;
		}
	}
	
	/**
	 * 删除通告单下所有的现场信息(包括现场信息中的转场信息)
	 * @param crewId
	 * @param noticeId
	 */
	public void deleteByNoticeId(String crewId, String noticeId) {
		String delConvertSql = "delete from " + LiveConvertAddModel.TABLE_NAME + " where crewId=? and noticeId=?";
		this.getJdbcTemplate().update(delConvertSql, new Object[] {crewId, noticeId});
		
		String delLiveSql = "delete from " + ShootLiveModel.TABLE_NAME + " where crewId=? and noticeId=?";
		this.getJdbcTemplate().update(delLiveSql, new Object[] {crewId, noticeId});
	}
}
