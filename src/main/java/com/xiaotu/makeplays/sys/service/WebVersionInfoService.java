package com.xiaotu.makeplays.sys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.sys.dao.WebVersionInfoDao;
import com.xiaotu.makeplays.sys.model.WebVersionInfoModel;
import com.xiaotu.makeplays.utils.Page;

/**
 * @类名：WebVersionInfoService.java
 * @作者：李晓平
 * @时间：2017年6月14日 上午11:24:05
 * @描述：web版本升级内容管理service
 */
@Service
public class WebVersionInfoService {

	@Autowired
	private WebVersionInfoDao webVersionInfoDao;
	
	/**
	 * 查询所有版本信息
	 * @return
	 */
	public List<WebVersionInfoModel> queryVersionList(Page page) {
		return this.webVersionInfoDao.queryVersionList(page);
	}
	
	/**
	 * 查询最新版本信息
	 * @return
	 * @throws Exception 
	 */
	public WebVersionInfoModel queryNewVersion() throws Exception {
		return this.webVersionInfoDao.queryNewVersion();
	}
	
	/**
	 * 保存版本信息
	 * @param model
	 * @return 
	 * @throws Exception 
	 */
	public int addOne(WebVersionInfoModel model) throws Exception {
		return this.webVersionInfoDao.add(model);
	}
	
	/**
	 * 更新版本信息
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public int updateOne(WebVersionInfoModel model) throws Exception {
		return this.webVersionInfoDao.updateWithNull(model, "id");
	}
	
	/**
	 * 根据id查询版本信息
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public WebVersionInfoModel queryById(String id) throws Exception {
		return this.webVersionInfoDao.queryById(id);
	}
}
