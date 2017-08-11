package com.xiaotu.makeplays.user.dao;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.user.model.ContactSysroleMapModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 剧组联系表和系统角色关联关系
 * @author xuchangjian 2016-9-21上午10:37:29
 */
@Repository
public class ContactSysroleMapDao extends BaseDao<ContactSysroleMapModel> {

	/**
	 * 根据联系人ID删除ID
	 * @param crewId
	 * @param contactId
	 */
	public void deleteByContactId(String crewId, String contactId) {
		String sql = "delete from tab_contact_sysrole_map where crewId = ? and contactId = ?";
		this.getJdbcTemplate().update(sql, crewId, contactId);
	}
}
