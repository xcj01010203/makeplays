package com.xiaotu.makeplays.shoot.dao;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.shoot.model.ShootGroupModel;
import com.xiaotu.makeplays.utils.BaseDao;

@Repository
public class ShootGroupDao extends BaseDao<ShootGroupModel> {

	/**
	 * 根据剧组ID查找对应的拍摄分组信息
	 * @param crewId
	 * @return
	 */
	public List<ShootGroupModel> queryManyByCrewId(String crewId) {
		String sql = "select * from " + ShootGroupModel.TABLE_NAME + " where crewId = ? or crewId='0' order by createTime";
		
		return this.query(sql, new Object[] {crewId}, ShootGroupModel.class, null);
	}
	
	/**
	 * 通过拍摄组ID查找拍摄分组信息
	 * @param groupId
	 * @return
	 */
	public ShootGroupModel queryOneByGroupId (String groupId) {
		String sql = "select * from " + ShootGroupModel.TABLE_NAME +" where groupId = ?";
		
		ShootGroupModel shootGroupModel = null;
		Object[] args = new Object[] {groupId};
		if (getResultCount(sql, args) == 1) {
			shootGroupModel = this.getJdbcTemplate().queryForObject(sql, args, ParameterizedBeanPropertyRowMapper
					.newInstance(ShootGroupModel.class));
		}
		
		return shootGroupModel;
	}
	
	/**
	 * 根据拍摄组名称查找拍摄分组信息
	 * @param groupName
	 * @return
	 * @throws Exception 
	 */
	public ShootGroupModel queryOneByGroupName(String groupName) throws Exception {
		String sql = "select * from " + ShootGroupModel.TABLE_NAME + " where groupName = ?";
		return this.queryForObject(sql, new Object[]{groupName}, ShootGroupModel.class);
	}
}
