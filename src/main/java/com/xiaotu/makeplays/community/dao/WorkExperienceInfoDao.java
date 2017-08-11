package com.xiaotu.makeplays.community.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.community.model.WorkExperienceInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 工作经历操作的dao
 * @author wanrenyi 2016年9月5日上午9:48:06
 */
@Repository
public class WorkExperienceInfoDao extends BaseDao<WorkExperienceInfoModel>{

	/**
	 * 保存工作经历信息
	 * @param model
	 * @throws Exception
	 */
	public void addWorkExperByBean(WorkExperienceInfoModel model) throws Exception {
		this.add(model);
	}
	
	/**
	 * 更新工作经历
	 * @param model
	 * @throws Exception
	 */
	public void updateWorkExperByBean(WorkExperienceInfoModel model) throws Exception {
		this.updateWithNull(model, "experienceId");
	}
	
	/**
	 * 根据工作经历id删除工作经历
	 * @param exprienceId
	 */
	public void deleteWorkExper(String exprienceId) {
		String sql = "delete from " + WorkExperienceInfoModel.TABLE_NAME + " where experienceId = ? ";
		this.getJdbcTemplate().update(sql, exprienceId);
	}
	
	/**
	 * 根据用户id和经历id查询出一条工作经历
	 * @param userId
	 * @param exprienceId
	 * @return
	 * @throws Exception
	 */
	public WorkExperienceInfoModel getOneWorkExById(String userId, String exprienceId) throws Exception {
		String sql = "select * from " + WorkExperienceInfoModel.TABLE_NAME + " where createUser = ? and experienceId = ?";
		return this.queryForObject(sql, new Object[] {userId, exprienceId}, WorkExperienceInfoModel.class);
	}
	
	/**
	 * 根据userid获取当前用户的工作经历的列表
	 * @param userId
	 * @return
	 */
	public List<WorkExperienceInfoModel> getWorkExperListByUserId(String userId){
		String sql = "select * from " + WorkExperienceInfoModel.TABLE_NAME + " where createUser = ?";
		List<WorkExperienceInfoModel> list = this.query(sql, new Object[] {userId}, WorkExperienceInfoModel.class, null);
		
		return list;
	}
	
}

