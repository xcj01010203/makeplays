package com.xiaotu.makeplays.crew.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.crew.dao.ProjectManageDao;
import com.xiaotu.makeplays.utils.StringUtil;

/**
 * @类名：ProjectManageService.java
 * @作者：李晓平
 * @时间：2017年2月13日 下午2:56:33
 * @描述：项目管理Service
 */
@Service
public class ProjectManageService {
	@Autowired
	private ProjectManageDao projectManageDao;
	
	/**
	 * 查询项目总监管理的项目列表
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> queryAllProjects(String userId) {
		List<Map<String, Object>> resultList = this.projectManageDao.queryAllProjects(userId);
		if(resultList != null && resultList.size() > 0) {
			for(Map<String, Object> map : resultList) {
				Integer remainingDays = null;
				if(StringUtil.isNotBlank(map.get("days") + "")) {
					if(StringUtil.isNotBlank(map.get("finishedDays") + "")) {
						remainingDays = Integer.parseInt(map.get("days") + "") 
								- Integer.parseInt(map.get("finishedDays") + "");
					} else {
						remainingDays = Integer.parseInt(map.get("days") + "");
					}
				}
				map.put("remainingDays", remainingDays);
			}
		}
		return resultList;
	}
}
