package com.xiaotu.makeplays.scenario.dao;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.scenario.model.PublishScenarioSettingModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 发布剧本设置
 * @author xuchangjian 2017-8-8下午3:56:14
 */
@Repository
public class PublishScenarioSettingDao extends BaseDao<PublishScenarioSettingModel> {

	/**
	 * 根据剧组ID和用户ID查询数据
	 * @param crewId	剧组ID
	 * @param userId	用户ID
	 * @return
	 * @throws Exception
	 */
	public PublishScenarioSettingModel queryByCrewIdAndUserId(String crewId, String userId) throws Exception {
		String sql = "select * from tab_publish_scenario_setting where crewId = ? and userId = ?";
		return this.queryForObject(sql, new Object[] {crewId, userId}, PublishScenarioSettingModel.class);
	}
}
