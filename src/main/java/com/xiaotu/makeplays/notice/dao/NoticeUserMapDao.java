package com.xiaotu.makeplays.notice.dao;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.notice.model.NoticeUserMapModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 通告单时间和剧组用户关联关系表
 * @author xuchangjian
 */
@Repository
public class NoticeUserMapDao extends BaseDao<NoticeUserMapModel> {

	/**
	 * 根据多个条件查询关联关系
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<NoticeUserMapModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + NoticeUserMapModel.TABLE_NAME + " where 1 = 1 ");
		
		Set<String> keySet = conditionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		List<Object> conList = new LinkedList<Object>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = conditionMap.get(key);
			sql.append(" and " + key + " = ?");
			conList.add(value);
		}
		Object[] objArr = conList.toArray();
		List<NoticeUserMapModel> noticeUserMapList = this.query(sql.toString(), objArr, NoticeUserMapModel.class, page);
		
		return noticeUserMapList;
	}
	
	/**
	 * 根据通告单时间ID删除通告单和用户关联关系
	 * @param noticeTimeId
	 * @return
	 */
	public int deleteByNoticeTimeId(String noticeTimeId) {
		String sql = "delete from " + NoticeUserMapModel.TABLE_NAME + " where noticeTimeId = ?";
		return this.getJdbcTemplate().update(sql, new Object[] {noticeTimeId});
	}
}
