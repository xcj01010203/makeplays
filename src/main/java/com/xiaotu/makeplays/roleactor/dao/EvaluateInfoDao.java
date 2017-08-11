package com.xiaotu.makeplays.roleactor.dao;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.roleactor.model.EvaluateInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 演员评价信息
 * @author xuchangjian 2016-7-19上午11:33:10
 */
@Repository
public class EvaluateInfoDao extends BaseDao<EvaluateInfoModel> {
    /**
     * 查询演职员评价信息详情
     * @param fromUserName 评价人员名称
     * @param toUserName 被评价人员名称
     * @param crewId
     * @return
     * @throws SQLException
     */
	public List<EvaluateInfoModel> queryEvaluateList(String fromUserName,String toUserName,String roleName,String crewId)throws SQLException{
		String sql="select map.tagId,eval.evaluateId,fromUserName,score,comment from tab_evaluate_info  eval LEFT JOIN  tab_evaluate_tag_map map  ON eval.evaluateId = map.evaluateId " +
				" where fromUserName=? and toUserName=? AND roleName=? and crewId=? ";//roleName
		return this.query(sql, new Object[]{fromUserName,toUserName,roleName,crewId},EvaluateInfoModel.class,null);
	}
	/**
	 * 删除演职员评价与标签关联关系
	 */
	public void delEvaluateTagMap(String evaluateId)throws SQLException{
		String sql="DELETE FROM tab_evaluate_tag_map WHERE evaluateId=? ";
		this.getJdbcTemplate().update(sql, new Object[]{evaluateId});
	}
	
}
