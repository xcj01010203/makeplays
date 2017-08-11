package com.xiaotu.makeplays.scenario.dao;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.scenario.model.ScenarioFormatModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 剧组剧本格式
 * @author xuchangjian 2017-5-9下午1:53:58
 */
@Repository
public class ScenarioFormatDao extends BaseDao<ScenarioFormatModel> {

	/**
	 * 查询指定剧组的剧本格式信息
	 * @param crewId
	 * @return
	 * @throws Exception 
	 */
	public ScenarioFormatModel queryByCrewId(String crewId) throws Exception {
		String sql = "select * from tab_scenario_format where crewId = ?";
		return this.queryForObject(sql, new Object[] {crewId}, ScenarioFormatModel.class);
	}
}
