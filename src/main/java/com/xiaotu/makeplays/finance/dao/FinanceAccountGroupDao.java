package com.xiaotu.makeplays.finance.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.crew.model.FinanceAccountGroupMapModel;
import com.xiaotu.makeplays.crew.model.FinanceAccountGroupModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.StringUtils;

/**
 * @类名：FinanceAccountGroupDao.java
 * @作者：李晓平
 * @时间：2016年11月3日 上午11:46:06
 * @描述：财务科目预算分组信息
 */
@Repository
public class FinanceAccountGroupDao extends BaseDao<FinanceAccountGroupModel> {
	
	/**
	 * 根据剧组ID查询所有的自定义分组
	 * @param crewId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryAllFinanAccoGroByCrewId(String crewId){
		String sql = "select * from " + FinanceAccountGroupModel.TABLE_NAME
				+ " where crewId = ? order by groupName";
		return this.query(sql, new Object[] {crewId}, null);
	}

	/**
	 * 根据ID查询财务科目预算分组
	 * 
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	public FinanceAccountGroupModel queryFinanAccoGroByGroupId(String groupId)
			throws Exception {
		String sql = "select * from " + FinanceAccountGroupModel.TABLE_NAME
				+ " where groupId = ? ";
		return this.queryForObject(sql, new Object[] { groupId },
				FinanceAccountGroupModel.class);
	}
	
	/**
	 * 根据名称查询财务科目预算分组
	 * @param groupName
	 * @return
	 */
	public Map<String, Object> queryFinanAccoGroByGroupName(String groupId, String groupName) {
		List<Object> params = new ArrayList<Object>();
		params.add(groupName);
		String sql = "select count(*) as num from " + FinanceAccountGroupModel.TABLE_NAME
				+ " where groupName = ? ";
		if(StringUtils.isNotBlank(groupId)) {
			sql += " and groupId != ? ";
			params.add(groupId);
		}
		return this.getJdbcTemplate().queryForMap(sql, params.toArray());
	}

	/**
	 * 根据ID查询财务科目预算分组与财务科目关联关系
	 * @param groupId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryFinanAccoGroMapByGroupId(
			String groupId) {
		String sql = "select * from " + FinanceAccountGroupMapModel.TABLE_NAME
				+ " where groupId = ?";
		return this.query(sql, new Object[] {groupId}, null);
	}
}
