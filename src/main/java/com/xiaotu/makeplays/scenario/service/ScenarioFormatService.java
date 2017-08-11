package com.xiaotu.makeplays.scenario.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.scenario.dao.ScenarioFormatDao;
import com.xiaotu.makeplays.scenario.model.ScenarioFormatModel;

/**
 * 剧组剧本格式
 * @author xuchangjian 2017-5-9下午1:54:24
 */
@Service
public class ScenarioFormatService {

	@Autowired
	private ScenarioFormatDao scenarioFormatDao;
	
	/**
	 * 查询指定剧组的剧本格式信息
	 * @param crewId
	 * @return
	 * @throws Exception 
	 */
	public ScenarioFormatModel queryByCrewId(String crewId) throws Exception {
		return this.scenarioFormatDao.queryByCrewId(crewId);
	}
	
	/**
	 * 添加一条记录
	 * @param scenarioFormat
	 * @throws Exception 
	 */
	public void addOne(ScenarioFormatModel scenarioFormat) throws Exception {
		this.scenarioFormatDao.add(scenarioFormat);
	}
	
	/**
	 * 更新一条记录
	 * @param scenarioFormat
	 * @throws Exception
	 */
	public void updateOne(ScenarioFormatModel scenarioFormat) throws Exception {
		this.scenarioFormatDao.updateWithNull(scenarioFormat, "id");
	}
}
