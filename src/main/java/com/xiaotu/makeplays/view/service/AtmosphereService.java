package com.xiaotu.makeplays.view.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.view.dao.AtmosphereDao;
import com.xiaotu.makeplays.view.model.AtmosphereInfoModel;

@Service
public class AtmosphereService {

	@Autowired
	private AtmosphereDao atmosphereDao;
	
	/**
	 * 根据气氛ID查找对应的气氛信息
	 * @return
	 */
	public AtmosphereInfoModel queryOneById(String atmosphereId) {
		return this.atmosphereDao.queryOneById(atmosphereId);
	}
	
	
	/**
	 * 获取所有气氛
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public List<AtmosphereInfoModel> queryAllByCrewId(String crewId) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		
		return atmosphereDao.queryByCrewId(crewId);
		
	}
	
	/**
	 * 查询在场景中存在的气氛信息
	 * @param crewId
	 * @return
	 */
	public List<AtmosphereInfoModel> queryExistByCrewId(String crewId) {
		return this.atmosphereDao.queryExistByCrewId(crewId);
	}
}
