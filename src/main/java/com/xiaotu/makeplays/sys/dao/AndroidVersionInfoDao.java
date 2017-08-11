package com.xiaotu.makeplays.sys.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.sys.model.AndroidVersionInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 安卓版本信息
 * @author xuchangjian 2017-4-13下午4:55:00
 */
@Repository
public class AndroidVersionInfoDao extends BaseDao<AndroidVersionInfoModel> {

	/**
	 * 查询所有版本信息
	 * @return
	 */
	public List<AndroidVersionInfoModel> queryVersionList(Page page) {
		String sql = "select * from tab_android_version_info order by versionNo desc";
		return this.query(sql, null, AndroidVersionInfoModel.class, page);
	}
	
	/**
	 * 根据id查询版本信息
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public AndroidVersionInfoModel queryById(String id) throws Exception {
		String sql = "select * from "+ AndroidVersionInfoModel.TABLE_NAME +" where id = ?";
		return this.queryForObject(sql, new Object[] {id}, AndroidVersionInfoModel.class);
	}
}
