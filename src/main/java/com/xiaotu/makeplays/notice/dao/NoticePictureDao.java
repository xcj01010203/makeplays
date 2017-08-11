package com.xiaotu.makeplays.notice.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.notice.model.NoticePictureModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 通告单图片附件
 * @author xuchangjian 2015-11-17下午2:22:22
 */
@Repository
public class NoticePictureDao extends BaseDao<NoticePictureModel> {

	/**
	 * 根据通告单ID和版本查询通告单下的图片附件信息
	 * @param noticeId
	 * @param noticeVersion
	 * @return
	 */
	public List<NoticePictureModel> queryByNoticeIdAndVersion(String noticeId, String noticeVersion) {
		String sql = "select * from tab_notice_picture where noticeId = ? and noticeVersion = ? order by uploadTime";
		return this.query(sql, new Object[] {noticeId, noticeVersion}, NoticePictureModel.class, null);
	}
	
	/**
	 * 根据ID删除图片记录
	 * @param crewId
	 * @param imgId
	 */
	public void deleteById(String crewId, String imgId) {
		String sql = "delete from tab_notice_picture where crewId=? and id=?";
		this.getJdbcTemplate().update(sql, new Object[] {crewId, imgId});
	}
	
	/**
	 * 通过ID查找通告单图片
	 * @param crewId
	 * @param imgId
	 * @throws Exception 
	 */
	public NoticePictureModel queryById(String crewId, String imgId) throws Exception {
		String sql = "select * from tab_notice_picture where crewId=? and id=?";
		
		Object[] params = new Object[] {crewId, imgId};
		if (getResultCount(sql, params) == 1) {
			return this.queryForObject(sql, params, NoticePictureModel.class);
		}
		return null;
	}
	
	/**
	 * 查询通告单以往版本的图片信息
	 * @param currVersion	当前版本
	 * @return
	 */
	public List<NoticePictureModel> queryOldVersionPicture(String noticeId, String currVersion) {
		String sql = "select * from" + NoticePictureModel.TABLE_NAME + " where noticeId = ? and noticeVersion != ? order by uploadTime asc";
		return this.query(sql, new Object[] {noticeId, currVersion}, NoticePictureModel.class, null);
	}
	
	/**
	 * 根据通告单的更新时间，更新通告单的图片版本号
	 * @param noticeId
	 * @param noticeTimeStr
	 */
	public void updatePictureVersion(String noticeId, String noticeTimeStr, String noticeVersion) {
		String sql = " UPDATE tab_notice_picture SET noticeVersion = ? WHERE noticeId = ? AND noticeVersion = ?";
		this.getJdbcTemplate().update(sql, noticeTimeStr, noticeId, noticeVersion);
	}
}
