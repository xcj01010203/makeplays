package com.xiaotu.makeplays.roleactor.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.roleactor.model.ActorLeaveRecordModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 演员请假记录
 * @author xuchangjian 2016-7-12下午3:51:03
 */
@Repository
public class ActorLeaveRecordDao extends BaseDao<ActorLeaveRecordModel> {

	/**
	 * 根据多个条件查询道具信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ActorLeaveRecordModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + ActorLeaveRecordModel.TABLE_NAME + " where 1 = 1 ");
		
		Set<String> keySet = conditionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		List<Object> conList = new LinkedList<Object>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = conditionMap.get(key);
			sql.append(" and " + key + " = ?");
			conList.add(value);
		}
		sql.append(" order by leaveStartDate desc ");
		
		Object[] objArr = conList.toArray();
		List<ActorLeaveRecordModel> recordList = this.query(sql.toString(), objArr, ActorLeaveRecordModel.class, page);
		
		return recordList;
	}
	
	/**
	 * 根据场景角色ID删除请假记录
	 * @param viewRoleIds
	 */
	public void deleteByViewRoleIds(String viewRoleIds) {
		viewRoleIds = "'" + viewRoleIds.replace(",", "','") + "'";
		String sql = "delete from tab_actor_leave_record where actorId in(select actorId from tab_actor_role_map where viewRoleId in ("+ viewRoleIds +"));";
		
		this.getJdbcTemplate().update(sql);
	}
	
	/**
	 * 查询已经存在指定日期的请假记录
	 * 比如指定时间为2016-12-12，则查询出该演员所有的在包含2016-12-12的请假记录
	 * @param actorId
	 * @param startDate
	 */
	public List<ActorLeaveRecordModel> queryExistDateRecord(String actorId, Date startDate, Date endDate) {
		String sql = "select * from tab_actor_leave_record where actorId = ? and ((leaveEndDate >= ? and leaveStartDate <= ?) or (leaveEndDate >= ? and leaveStartDate <= ?) or (leaveStartDate >= ? and leaveEndDate <= ?))";
		return this.query(sql, new Object[] {actorId, startDate, startDate, endDate, endDate, startDate, endDate}, ActorLeaveRecordModel.class, null);
	}
}
