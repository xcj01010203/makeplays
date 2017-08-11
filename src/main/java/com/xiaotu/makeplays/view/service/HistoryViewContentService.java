package com.xiaotu.makeplays.view.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.view.dao.HistoryViewContentDao;
import com.xiaotu.makeplays.view.model.HistoryViewContentModel;

/**
 * 历史版本剧本
 * @author xuchangjian 2015-12-1下午4:21:29
 */
@Service
public class HistoryViewContentService {
	
	@Autowired
	private HistoryViewContentDao historyViewContentDao;

	/**
	 * 查询场景下的历史版本信息
	 * @param crewId
	 * @param viewId
	 * @return
	 */
	public List<HistoryViewContentModel> queryByViewId(String crewId, String viewId) {
		return this.historyViewContentDao.queryByViewId(crewId, viewId);
	}
	
	/**
	 * 获取上一版剧本内容信息
	 * @param crewId
	 * @param viewId
	 * @return
	 */
	public HistoryViewContentModel queryPreVersionContent(String crewId, String viewId) {
		List<HistoryViewContentModel> allHistoryContentList = this.queryByViewId(crewId, viewId);
		if (allHistoryContentList != null && allHistoryContentList.size() > 0) {
			return allHistoryContentList.get(0);
		}
		return null;
	}
	
	/**
	 * 获取版本号信息
	 * @param crewId
	 * @param viewId
	 * @return
	 */
	public List<Map<String, Object>> queryVersionList(String crewId, String viewId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd.HHmmss");
		
		List<HistoryViewContentModel> allHistoryContentList = this.queryByViewId(crewId, viewId);
		List<Map<String, Object>> versionList = new ArrayList<Map<String, Object>>();
		if (allHistoryContentList != null && allHistoryContentList.size() > 0) {
			for (int i = 0; i < allHistoryContentList.size(); i++) {
				if (i > 2) {
					break;
				}
				Map<String, Object> versionMap = new HashMap<String, Object>();
				String myVersion = allHistoryContentList.get(i).getVersion();
				Date createTime = allHistoryContentList.get(i).getCreateTime();
				versionMap.put("version", myVersion);
				versionMap.put("createTime", sdf.format(createTime));
				versionList.add(versionMap);
			}
		}
		
		return versionList;
	}
	
	/**
	 * 查询场景指定版本的内容
	 * @param crewId
	 * @param viewId
	 * @param version
	 * @return
	 * @throws Exception 
	 */
	public HistoryViewContentModel queryByViewIdAndVersion(String crewId, String viewId, String version) throws Exception {
		return this.historyViewContentDao.queryByViewIdAndVersion(crewId, viewId, version);
	}
}
