package com.xiaotu.makeplays.scenario.dao;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.scenario.model.DownloadScenarioRecordModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 下载剧本记录 
 * @author xuchangjian 2016-11-10下午5:35:03
 */
@Repository
public class DownloadScenarioRecordDao extends BaseDao<DownloadScenarioRecordModel> {

	/**
	 * 根据剧组ID删除记录
	 * @param crewId
	 */
	public void deleteByCrewId(String crewId) {
		String sql = "delete from tab_download_scenario_record where crewId = ?";
		this.getJdbcTemplate().update(sql, crewId);
	}
	
	/**
	 * 根据剧组ID和设备标识查询下载记录
	 * @param crewId
	 * @param clientUUID
	 * @return
	 * @throws Exception 
	 */
	public DownloadScenarioRecordModel queryByCrewIdAndClientUUID(String crewId, String clientUUID) throws Exception {
		String sql = "select * from tab_download_scenario_record where crewId = ? and clientUUID = ?";
		return this.queryForObject(sql, new Object[] {crewId, clientUUID}, DownloadScenarioRecordModel.class);
	}
	
}
