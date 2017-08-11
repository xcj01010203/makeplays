package com.xiaotu.makeplays.scenario.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.scenario.dao.DownloadScenarioRecordDao;
import com.xiaotu.makeplays.scenario.model.DownloadScenarioRecordModel;

/**
 * 下载剧本记录
 * @author xuchangjian 2016-11-10下午5:35:09
 */
@Service
public class DownloadScenarioRecordService {

	@Autowired
	private DownloadScenarioRecordDao downLoadScenarioRecordDao;
	
	/**
	 * 根据剧组ID删除记录
	 * @param crewId
	 */
	public void deleteByCrewId(String crewId) {
		this.downLoadScenarioRecordDao.deleteByCrewId(crewId);
	}
	
	/**
	 * 根据剧组ID和设备标识查询下载记录
	 * @param crewId
	 * @param clientUUID
	 * @return
	 * @throws Exception 
	 */
	public DownloadScenarioRecordModel queryByCrewIdAndClientUUID(String crewId, String clientUUID) throws Exception {
		return this.downLoadScenarioRecordDao.queryByCrewIdAndClientUUID(crewId, clientUUID);
	}
	
	/**
	 * 新增一条记录
	 * @param record
	 * @throws Exception 
	 */
	public void addOne(DownloadScenarioRecordModel record) throws Exception {
		this.downLoadScenarioRecordDao.add(record);
	}
}
