package com.xiaotu.makeplays.message.dao;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.message.controller.filter.MessageInfoFilter;
import com.xiaotu.makeplays.message.model.MessageInfoModel;
import com.xiaotu.makeplays.message.model.constants.MessageInfoStatus;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 消息
 * @author xuchangjian 2016-9-24上午11:22:46
 */
@Repository
public class MessageInfoDao extends BaseDao<MessageInfoModel> {
	
	/**
	 * 根据多个条件查询消息列表
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<MessageInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page, MessageInfoFilter filter) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + MessageInfoModel.TABLE_NAME + " where 1 = 1 ");
		
		Set<String> keySet = conditionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		List<Object> conList = new LinkedList<Object>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = conditionMap.get(key);
			if(key.equals("crewId")) {
				sql.append(" and (crewId = ? or crewId='0') ");
			} else {
				sql.append(" and " + key + " = ? ");
			}
			conList.add(value);
		}
		sql.append(" and remindTime <= now() ");
		
		if(filter != null) {
			if(StringUtils.isNotBlank(filter.getContent())) {
				sql.append(" and (title like ? or content like ?) ");
				String content = filter.getContent();
				content = content.replaceAll("%", "\\\\%");
				content = content.replaceAll("_", "\\\\_");
				conList.add("%" + content + "%");
				conList.add("%" + content + "%");
			}
			if(filter.getStatus() != null) {
				sql.append(" and status=? ");
				conList.add(filter.getStatus());
			}
			if(StringUtils.isNotBlank(filter.getStartTime())) {
				sql.append(" and remindTime >= ? ");
				conList.add(filter.getStartTime());
			}
			if(StringUtils.isNotBlank(filter.getEndTime())) {
				sql.append(" and remindTime <= ? ");
				conList.add(filter.getEndTime());
			}
		}
		
		sql.append(" ORDER BY remindTime desc ");
		Object[] objArr = conList.toArray();
		List<MessageInfoModel> messageInfoList = this.query(sql.toString(), objArr, MessageInfoModel.class, page);
		
		return messageInfoList;
	}
	
	/**
	 * 根据ID查询消息
	 * @param messageId
	 * @return
	 * @throws Exception 
	 */
	public MessageInfoModel queryById(String messageId) throws Exception {
		String sql = "select * from " + MessageInfoModel.TABLE_NAME + " where id = ?";
		return this.queryForObject(sql, new Object[] {messageId}, MessageInfoModel.class);
	}
	
	/**
	 * 根据业务ID删除消息
	 * @param buzId	业务ID
	 */
	public void deleteByBuzId (String buzId) {
		String sql = "delete from " + MessageInfoModel.TABLE_NAME + " where buzId = ?";
		this.getJdbcTemplate().update(sql, buzId);
	}
	
	/**
	 * 把消息设置成旧消息
	 * @param crewId
	 * @param userId
	 * @throws Exception
	 */
	public void oldMessage(String crewId, String userId, Integer messageType) throws Exception {
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		sql.append("update " + MessageInfoModel.TABLE_NAME + " set isNew = 0");
		sql.append(" where remindTime<=now() ");
		if(StringUtils.isNotBlank(crewId)) {
			sql.append(" and (crewId = ? or crewId='0') ");
			params.add(crewId);
		}
		if(StringUtils.isNotBlank(userId)) {
			sql.append(" and receiverId=? ");
			params.add(userId);
		}
		if (messageType != null) {
			sql.append(" and type = ? ");
			params.add(messageType);
		}
		this.getJdbcTemplate().update(sql.toString(), params.toArray());
	}
	
	/**
	 * 更新消息阅读状态
	 * @param crewId
	 * @param userId
	 * @param messageIds
	 * @throws Exception
	 */
	public void updateMessageStatus(String crewId, String userId, String messageIds) throws Exception {
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		sql.append("update " + MessageInfoModel.TABLE_NAME + " set status=" + MessageInfoStatus.HasRead.getValue());
		sql.append(" where remindTime<=now() ");
		if(StringUtils.isNotBlank(crewId)) {
			sql.append(" and (crewId = ? or crewId='0') ");
			params.add(crewId);
		}
		if(StringUtils.isNotBlank(userId)) {
			sql.append(" and receiverId=? ");
			params.add(userId);
		}
		if(StringUtils.isNotBlank(messageIds)) {
			messageIds = messageIds.replace(",", "','");
			sql.append(" and id in ('" + messageIds + "')");
		}
		this.getJdbcTemplate().update(sql.toString(), params.toArray());
	}
}
