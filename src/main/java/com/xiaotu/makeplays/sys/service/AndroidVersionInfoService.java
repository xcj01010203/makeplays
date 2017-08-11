package com.xiaotu.makeplays.sys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.sys.dao.AndroidVersionInfoDao;
import com.xiaotu.makeplays.sys.model.AndroidVersionInfoModel;
import com.xiaotu.makeplays.utils.Page;

/**
 * 安卓版本信息
 * @author xuchangjian 2017-4-13下午4:55:27
 */
@Service
public class AndroidVersionInfoService {

	@Autowired
	private AndroidVersionInfoDao androidVersionInfoDao;
	
	/**
	 * 查询所有版本信息
	 * @return
	 */
	public List<AndroidVersionInfoModel> queryVersionList(Page page) {
		return this.androidVersionInfoDao.queryVersionList(page);
	}
	
	/**
	 * 保存版本信息
	 * @param model
	 * @return 
	 * @throws Exception 
	 */
	public int addOne(AndroidVersionInfoModel model) throws Exception {
		return this.androidVersionInfoDao.add(model);
	}
	
	/**
	 * 更新版本信息
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public int updateOne(AndroidVersionInfoModel model) throws Exception {
		return this.androidVersionInfoDao.updateWithNull(model, "id");
	}
	
	/**
	 * 根据id查询版本的详细信息
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public AndroidVersionInfoModel queryById(String id) throws Exception {
		return this.androidVersionInfoDao.queryById(id);
	}
}
