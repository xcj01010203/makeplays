package com.xiaotu.makeplays.scenario.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.scenario.dao.PublishScenarioSettingDao;
import com.xiaotu.makeplays.scenario.model.PublishScenarioSettingModel;

/**
 * 发布剧本设置
 * @author xuchangjian 2017-8-8下午4:32:47
 */
@Service
public class PublishScenarioSettingService {

	@Autowired
	private PublishScenarioSettingDao publishScenarioSettingDao;
	
	/**
	 * 根据剧组ID和用户ID查询数据
	 * @param crewId	剧组ID
	 * @param userId	用户ID
	 * @return
	 * @throws Exception
	 */
	public PublishScenarioSettingModel queryByCrewIdAndUserId(String crewId, String userId) throws Exception {
		return this.publishScenarioSettingDao.queryByCrewIdAndUserId(crewId, userId);
	}
	
	/**
	 * 新增一条记录
	 * @param setting
	 * @throws Exception 
	 */
	public void addOne(PublishScenarioSettingModel setting) throws Exception {
		this.publishScenarioSettingDao.add(setting);
	}
	
	/**
	 * 更新一条记录
	 * @param setting
	 * @throws Exception 
	 */
	public void updateOne(PublishScenarioSettingModel setting) throws Exception {
		this.publishScenarioSettingDao.update(setting, "id");
	}
}
