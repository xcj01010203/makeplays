package com.xiaotu.makeplays.cache.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.cache.dao.CacheDao;
import com.xiaotu.makeplays.cache.model.CacheModel;
import com.xiaotu.makeplays.utils.UUIDUtils;

@Service
public class CacheService {

	@Autowired
	private CacheDao cacheDao;
	
	/**
	 * 保存记录内容
	 * @param crewId
	 * @param userId
	 * @param content
	 * @param type
	 */
	public void saveCacheInfo(String crewId, String userId, String content, Integer type) throws Exception{
		CacheModel cacheInfo = this.cacheDao.queryCacheInfo(crewId, userId, type);
		if(cacheInfo == null) {//新增
			cacheInfo = new CacheModel();
			cacheInfo.setId(UUIDUtils.getId());
			cacheInfo.setType(type);
			cacheInfo.setCrewId(crewId);
			cacheInfo.setUserId(userId);
			cacheInfo.setContent(content);
			this.cacheDao.add(cacheInfo);
		} else {//修改
			cacheInfo.setContent(content);
			this.cacheDao.updateWithNull(cacheInfo, "id");
		}
	}
	
	/**
	 * 查询记录内容
	 * @param crewId
	 * @param userId
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public CacheModel queryCacheInfo(String crewId, String userId, Integer type) throws Exception {
		return this.cacheDao.queryCacheInfo(crewId, userId, type);
	}
}
