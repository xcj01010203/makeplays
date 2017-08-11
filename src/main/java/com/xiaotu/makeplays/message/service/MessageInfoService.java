package com.xiaotu.makeplays.message.service;


import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.message.controller.filter.MessageInfoFilter;
import com.xiaotu.makeplays.message.dao.MessageInfoDao;
import com.xiaotu.makeplays.message.model.MessageInfoModel;
import com.xiaotu.makeplays.utils.Page;

/**
 * 系统公用消息
 * @author xuchangjian 2016-8-13下午5:19:28
 */
@Service
public class MessageInfoService {

	@Autowired
	private MessageInfoDao messageInfoDao;
	
	/**
	 * 新增一条记录
	 * @param messageInfo
	 * @throws Exception
	 */
	public void addOne(MessageInfoModel messageInfo) throws Exception {
		this.messageInfoDao.add(messageInfo);
	}
	
	/**
	 * 新增一条记录
	 * @param messageInfo
	 * @throws Exception
	 */
	public void addMany(List<MessageInfoModel> messageInfoList) throws Exception {
		this.messageInfoDao.addBatch(messageInfoList, MessageInfoModel.class);
	}
	
	/**
	 * 更新一条记录
	 * @param messageInfo
	 * @throws Exception
	 */
	public void updateOne(MessageInfoModel messageInfo) throws Exception {
		this.messageInfoDao.update(messageInfo, "id");
	}
	
	/**
	 * 删除一条记录
	 * @param messageId
	 * @throws Exception
	 */
	public void deleteOne(String messageId) throws Exception {
		this.messageInfoDao.deleteOne(messageId, "id", MessageInfoModel.TABLE_NAME);
	}
	
	/**
	 * 根据多个条件查询消息列表
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<MessageInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page, MessageInfoFilter filter) {
		return this.messageInfoDao.queryManyByMutiCondition(conditionMap, page, filter);
	}
	
	/**
	 * 根据多个条件查询消息列表
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<MessageInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.queryManyByMutiCondition(conditionMap, page, null);
	}
	
	/**
	 * 根据ID查询消息
	 * @param messageId
	 * @return
	 * @throws Exception 
	 */
	public MessageInfoModel queryById(String messageId) throws Exception {
		return this.messageInfoDao.queryById(messageId);
	}
	
	/**
	 * 根据业务ID删除消息
	 */
	public void deleteByBuzId (String buzId) {
		this.messageInfoDao.deleteByBuzId(buzId);
	}
	
	/**
	 * 保存消息信息
	 * @param crewId	剧组ID
	 * @param senderId	发送人ID
	 * @param receiverId	接收人ID
	 * @param type	消息类型
	 * @param buzId	业务ID
	 * @param title	标题
	 * @param content	内容
	 * @param remindTime	提醒时间
	 * @return
	 */
	public MessageInfoModel saveMessageInfo(String crewId, String senderId, String receiverId, String type, 
			String buzId, String title, String content, Date remindTime) {
		
		return null;
	}
	
	/**
	 * 更新消息查看状态
	 * @param crewId
	 * @param userId
	 * @throws Exception
	 */
	public void oldMessage(String crewId, String userId, Integer messageType) throws Exception {
		this.messageInfoDao.oldMessage(crewId, userId, messageType);
	}
	
	/**
	 * 已阅消息
	 * @param messageIds
	 * @throws Exception
	 */
	public void readMultiMessage(String crewId, String userId, String messageIds) throws Exception {
		this.messageInfoDao.updateMessageStatus(crewId, userId, messageIds);
	}
}
