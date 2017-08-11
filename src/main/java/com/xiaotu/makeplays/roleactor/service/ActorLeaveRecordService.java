package com.xiaotu.makeplays.roleactor.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.roleactor.dao.ActorLeaveRecordDao;
import com.xiaotu.makeplays.roleactor.model.ActorLeaveRecordModel;
import com.xiaotu.makeplays.utils.Page;

/**
 * 演员请假记录表
 * @author xuchangjian 2016-7-12下午3:53:01
 */
@Service
public class ActorLeaveRecordService {

	@Autowired
	private ActorLeaveRecordDao actorLeaveRecordDao;
	
	/**
	 * 根据多个条件查询道具信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ActorLeaveRecordModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.actorLeaveRecordDao.queryManyByMutiCondition(conditionMap, page);
	}
	
	/**
	 * 新增一条记录
	 * @param actorLeaveRecordModel
	 * @throws Exception 
	 */
	public void addOne(ActorLeaveRecordModel actorLeaveRecordModel) throws Exception {
		this.actorLeaveRecordDao.add(actorLeaveRecordModel);
	}
	
	/**
	 * 删除一条记录
	 * @param recordId
	 * @throws Exception
	 */
	public void deleteOne(String recordId) throws Exception {
		this.actorLeaveRecordDao.deleteOne(recordId, "id", ActorLeaveRecordModel.TABLE_NAME);
	}
	
	/**
	 * 根据场景角色ID删除请假记录
	 * @param viewRoleIds
	 */
	public void deleteByViewRoleIds(String viewRoleIds) {
		this.actorLeaveRecordDao.deleteByViewRoleIds(viewRoleIds);
	}
	
	/**
	 * 查询已经存在指定日期的请假记录
	 * 比如指定时间为2016-12-12，则查询出该演员所有的在包含2016-12-12的请假记录
	 * @param actorId
	 * @param startDate
	 */
	public List<ActorLeaveRecordModel> queryExistDateRecord(String actorId, Date startDate, Date endDate) {
		return this.actorLeaveRecordDao.queryExistDateRecord(actorId, startDate, endDate);
	}
}
