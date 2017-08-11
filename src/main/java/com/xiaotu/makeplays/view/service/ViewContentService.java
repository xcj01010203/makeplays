package com.xiaotu.makeplays.view.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.view.dao.ViewContentDao;
import com.xiaotu.makeplays.view.model.ViewContentModel;

/**
 * 场景内日on个
 * @author xuchangjian
 */
@Service
public class ViewContentService {

	@Autowired
	private ViewContentDao viewContentDao;
	
	/**
	 * 根据场景ID查找场景内容信息
	 * @param viewId
	 * @return
	 */
	public ViewContentModel queryByViewId(String viewId) {
		return this.viewContentDao.queryByViewId(viewId);
	}
	
	/**
	 * 查询没有指定类型演员的场景、场景内容信息
	 * @param cewId
	 * @return
	 */
	public List<Map<String, Object>> queryNoMajorRoleView(String crewId, Integer viewRoleType, Page page) {
		return this.viewContentDao.queryNoMajorRoleView(crewId, viewRoleType, page);
	}
	
	/**
	 * 查询没有指定类型演员的场景、场景内容信息
	 * @param cewId
	 * @return
	 */
	public int countNoTypeRoleView(String crewId, Integer viewRoleType) {
		return this.viewContentDao.countNoTypeRoleView(crewId, viewRoleType);
	}

	/**
	 * 查询未保存的场景、场景内容信息
	 * @param cewId
	 * @return
	 */
	public int countViewContent(String crewId, Boolean isManualSave) {
		return this.viewContentDao.countViewContent(crewId, isManualSave);
	}
	
	/**
	 * 查询未保存的场景、场景内容信息
	 * @param cewId
	 * @return
	 */
	public List<Map<String, Object>> queryViewContent(String crewId, Page page, Boolean isManualSave) {
		return this.viewContentDao.queryViewContent(crewId, page, isManualSave);
	}
	
	/**
	 * 修改剧本内容
	 * @param content
	 * @throws Exception 
	 */
	public void updateOne(ViewContentModel content) throws Exception {
		this.viewContentDao.update(content);
	}
	
	/**
	 * 批量更新剧本的内容
	 * @param viewContentList
	 * @throws Exception 
	 */
	public void updateMany(List<ViewContentModel> viewContentList) throws Exception {
		for (ViewContentModel viewContent : viewContentList) {
			this.viewContentDao.update(viewContent);
		}
	}
	
	/**
	 * 根据集-场号查询剧本内容信息
	 * @param seriesNo
	 * @param viewNo
	 * @return
	 * @throws Exception 
	 */
	public ViewContentModel queryBySeriesViewNo (String crewId, int seriesNo, String viewNo) throws Exception {
		return this.viewContentDao.queryBySeriesViewNo(crewId, seriesNo, viewNo);
	}
	
	/**
	 * 查询剧组下未发布的剧本内容
	 * @param crewId
	 * @return
	 */
	public List<ViewContentModel> queryNotPublishedContentList(String crewId) {
		return this.viewContentDao.queryNotPublishedContentList(crewId);
	}
	
	/**
	 * 查询剧组下未发布的剧本内容
	 * @param crewId
	 * @return
	 */
	public int countNotPublishedContentList(String crewId) {
		return this.viewContentDao.countNotPublishedContentList(crewId);
	}
	
	/**
	 * 查询所有含有未发布剧本内容的集次
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryNotPublishedSeriesNo(String crewId) {
		return this.viewContentDao.queryNotPublishedSeriesNo(crewId);
	}
	
	/**
	 * 查询场次的已读人员信息
	 * @param crewId
	 * @return	场景ID，集次，场次，已读用户ID
	 */
	public List<Map<String, Object>> queryReadedPeopleInfo(String crewId) {
		return this.viewContentDao.queryReadedPeopleInfo(crewId);
	}
}
