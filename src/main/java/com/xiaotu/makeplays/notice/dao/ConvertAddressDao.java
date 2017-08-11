package com.xiaotu.makeplays.notice.dao;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.notice.model.ConvertAddressModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 通告单转场信息
 * @author xuchangjian
 */
@Repository
public class ConvertAddressDao extends BaseDao<ConvertAddressModel> {

	/**
	 * 根据通告单ID删除通告单转场信息
	 * @param noticeId
	 * @return
	 */
	public int deleteByNoticeId(String noticeId) {
		String sql = "delete from " + ConvertAddressModel.TABLE_NAME + " where noticeId = ?";
		return this.getJdbcTemplate().update(sql, new Object[] {noticeId});
	}
	
	
	/**
	 * 根据转场后地点和场景查询转场信息
	 * @param crewId
	 * @param noticeId
	 * @param afterLocationId
	 * @param afterViewIds
	 * @return
	 * @throws Exception 
	 */
	public ConvertAddressModel queryByLocationViewIds(String crewId, String noticeId, String afterLocationId, String afterViewIds) throws Exception {
		String[] viewIdArr = afterViewIds.split(",");
		StringBuilder sql = new StringBuilder("select * from " + ConvertAddressModel.TABLE_NAME + " where crewId = ? and noticeId = ? and afterLocationId = ? ");
		
		for (String afterViewId : viewIdArr) {
			sql.append(" and afterViewIds like '%" + afterViewId + "%'");
		}
		sql.append(" limit 0,1");
		
		ConvertAddressModel addressInfo = null;
		Object[] args = new Object[] {crewId, noticeId, afterLocationId};
		if (getResultCount(sql.toString(), args) == 1) {
			addressInfo = this.queryForObject(sql.toString(), new Object[] {crewId, noticeId, afterLocationId},  ConvertAddressModel.class);
		}
		return addressInfo;
		
	}
}
