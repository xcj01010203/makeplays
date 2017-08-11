package com.xiaotu.makeplays.cache.dao;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.cache.model.CacheModel;
import com.xiaotu.makeplays.utils.BaseDao;

@Repository
public class CacheDao extends BaseDao<CacheModel>{

	/**
	 * 查询内容
	 * @param crewId
	 * @param userId
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public CacheModel queryCacheInfo(String crewId, String userId, Integer type) throws Exception {
		String sql = "select * from " + CacheModel.TABLE_NAME + " where crewId=? and userId=? and type=?";
		return this.queryForObject(sql, new Object[]{crewId, userId, type}, CacheModel.class);
	}
}
