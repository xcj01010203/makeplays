package com.xiaotu.makeplays.bulletin.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.bulletin.dao.BulletinInfoDao;
import com.xiaotu.makeplays.bulletin.model.BulletinInfoModel;
import com.xiaotu.makeplays.message.dao.MessageInfoDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 剧组消息公告信息
 * @author xuchangjian
 */
@Service
public class BulletinInfoService {
	
	@Autowired
	private BulletinInfoDao bulletinInfoDao;
	
	@Autowired
	private MessageInfoDao messageInfoDao;
	
	
	/**
	 * 根据多个条件查询剧组公告信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<BulletinInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.bulletinInfoDao.queryManyByMutiCondition(conditionMap, page);
	}
	
	/**
	 * 查询所有公告信息
	 * @param page 分页信息
	 * @return
	 */
	public List<Map<String, Object>> queryAll(Page page) {
		return this.bulletinInfoDao.queryAll(page);
	}
	
	/**
	 * 通过ID查找剧组公告信息
	 * @param shootLogId
	 */
	public BulletinInfoModel queryOneByBulletinId(String bulletinId) {
		return this.bulletinInfoDao.queryOneByBulletinId(bulletinId);
	}
	
	/**
	 * 保存剧组公告信息
	 * @param bulletinInfo
	 * @throws Exception 
	 */
	public void addOneBullinInfo(BulletinInfoModel bulletinInfo) throws Exception {
		this.bulletinInfoDao.add(bulletinInfo);
	}
	
	/**
	 * 更新剧组公告信息
	 * @param bulletinInfo
	 * @throws Exception 
	 */
	public void updateOneBullinInfo(BulletinInfoModel bulletinInfo) throws Exception {
		this.bulletinInfoDao.update(bulletinInfo, "bulletinId");
	}
	
	/**
	 * 删除剧组公告信息
	 * @param bulletinId
	 * @throws Exception
	 */
	public void deleteOneBullinInfo(String bulletinId) throws Exception {
		this.bulletinInfoDao.deleteOne(bulletinId, "bulletinId", BulletinInfoModel.TABLE_NAME);
	}
	
}
