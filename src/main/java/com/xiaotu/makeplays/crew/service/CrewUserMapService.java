package com.xiaotu.makeplays.crew.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.crew.dao.CrewUserMapDao;
import com.xiaotu.makeplays.crew.model.CrewUserMapModel;

/**
 * 剧组用户关联关系
 * @author xuchangjian 2016-10-10下午6:49:34
 */
@Service
public class CrewUserMapService {

	@Autowired
	private CrewUserMapDao crewUserMapDao;
	
	/**
	 * 根据用户ID查询记录
	 */
	public List<CrewUserMapModel> queryByUserId(String userId){
		return this.crewUserMapDao.queryByUserId(userId);
	}
	
	public List<Map<String, Object>> queryCrewInfoByUserId(String userId){
		return this.crewUserMapDao.queryCrewInfoByUserId(userId);
	}
	
	public List<Map<String, Object>> queryCrewInfoByUserIdNotContainsCurrCrew(String userId,String crewId){
		return this.crewUserMapDao.queryCrewInfoByUserIdNotContainsCurrCrew(userId,crewId);
	}	
	
}
