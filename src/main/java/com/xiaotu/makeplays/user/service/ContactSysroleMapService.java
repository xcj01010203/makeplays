package com.xiaotu.makeplays.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.user.dao.ContactSysroleMapDao;
import com.xiaotu.makeplays.user.model.ContactSysroleMapModel;

/**
 * 剧组联系表和系统角色关联关系
 * @author xuchangjian 2016-9-21上午10:38:17
 */
@Service
public class ContactSysroleMapService {

	@Autowired
	private ContactSysroleMapDao contactSysroleMapDao;
	
	/**
	 * 根据联系人ID删除ID
	 * @param crewId
	 * @param contactId
	 */
	public void deleteByContactId(String crewId, String contactId) {
		this.contactSysroleMapDao.deleteByContactId(crewId, contactId);
	}
	
	/**
	 * 新增一条记录
	 * @param map
	 * @throws Exception 
	 */
	public void addOne(ContactSysroleMapModel map) throws Exception {
		this.contactSysroleMapDao.add(map);
	}
}
