package com.xiaotu.makeplays.notice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.notice.dao.ConvertAddressDao;
import com.xiaotu.makeplays.notice.model.ConvertAddressModel;

@Service
public class ConvertAddressService {

	@Autowired
	private ConvertAddressDao convertAddressDao;
	
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
		return this.convertAddressDao.queryByLocationViewIds(crewId, noticeId, afterLocationId, afterViewIds);
	}
}
