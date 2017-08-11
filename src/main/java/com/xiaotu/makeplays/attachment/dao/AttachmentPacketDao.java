package com.xiaotu.makeplays.attachment.dao;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.attachment.model.AttachmentPacketModel;
import com.xiaotu.makeplays.utils.BaseDao;

@Repository
public class AttachmentPacketDao extends BaseDao<AttachmentPacketModel> {
	/**
	 * 根据ID查询附件包信息
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public AttachmentPacketModel queryById(String id) throws Exception {
		String sql = "select * from " + AttachmentPacketModel.TABLE_NAME + " where id=?";
		return this.queryForObject(sql, new Object[] {id}, AttachmentPacketModel.class);
	}
}
