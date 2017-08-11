package com.xiaotu.makeplays.view.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.view.dao.ViewRoleMapDao;
import com.xiaotu.makeplays.view.model.ViewRoleMapModel;

@Service
public class ViewRoleMapService {
	
	@Autowired
	private ViewRoleMapDao viewRoleMapDao;

	/**
	 * 批量新增操作
	 * @param atmosphereList
	 * @throws Exception 
	 */
	public void addMany (List<ViewRoleMapModel> viewRoleMapList) throws Exception {
		this.viewRoleMapDao.addMany(viewRoleMapList);
	}
	
	/**
	 * 删除剧组下所有未手动保存的场景和角色的关联
	 * @param crewId
	 */
	public void deleteNoSaveViewRoleMap(String crewId) {
		this.deleteNoSaveViewRoleMap(crewId);
	}
}
