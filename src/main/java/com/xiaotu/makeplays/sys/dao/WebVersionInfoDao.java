package com.xiaotu.makeplays.sys.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.sys.model.WebVersionInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * @类名：WebVersionInfoDao.java
 * @作者：李晓平
 * @时间：2017年6月14日 上午11:28:39
 * @描述：web版本升级内容管理dao
 */
@Repository
public class WebVersionInfoDao extends BaseDao<WebVersionInfoModel> {

	/**
	 * 查询所有版本信息
	 * @return
	 */
	public List<WebVersionInfoModel> queryVersionList(Page page) {
		String sql = "select * from tab_web_version_info order by createTime desc";
		return this.query(sql, null, WebVersionInfoModel.class, page);
	}
	
	/**
	 * 查询最新版本信息
	 * @return
	 * @throws Exception 
	 */
	public WebVersionInfoModel queryNewVersion() throws Exception {
		String sql = "select * from tab_web_version_info order by createTime desc limit 1";
		return this.queryForObject(sql, null, WebVersionInfoModel.class);
	}
	
	/**
	 * 根据id查询版本信息
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public WebVersionInfoModel queryById(String id) throws Exception {
		String sql = "select * from "+ WebVersionInfoModel.TABLE_NAME +" where id = ?";
		return this.queryForObject(sql, new Object[] {id}, WebVersionInfoModel.class);
	}
}
