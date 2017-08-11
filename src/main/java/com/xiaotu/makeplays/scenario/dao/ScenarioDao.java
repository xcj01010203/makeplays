package com.xiaotu.makeplays.scenario.dao;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.scenario.model.ScenarioInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;

@Repository
public class ScenarioDao extends BaseDao<ScenarioInfoModel> {

	/**
	 * 获取最新上传的剧本信息
	 * @param crewId
	 * @return
	 * @throws Exception 
	 */
	public ScenarioInfoModel queryLastScenario(String crewId) throws Exception {
		String sql = "select * from tab_scenario_info where crewId = ? order by uploadTime desc limit 0, 1";
		
		return this.queryForObject(sql, new Object[] {crewId}, ScenarioInfoModel.class);
	}
}
