package com.xiaotu.makeplays.crew.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.crew.model.CrewUserMapModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 剧组和用户关联关系
 * @author xuchangjian 2016-10-10下午6:49:10
 */
@Repository
public class CrewUserMapDao extends BaseDao<CrewUserMapModel>{

	/**
	 * 查询用户默认剧组
	 * @param userId
	 * @return
	 */
	public CrewUserMapModel queryEffectiveCrewUserByuserId(String userId){
		
		String sql = "select tcum.* from tab_crew_user_map tcum where tcum.userid = ? and  tcum.ifDefault = 1 and tcum.status = 1";
		
		List<CrewUserMapModel> list = this.query(sql, new Object[]{userId}, CrewUserMapModel.class, null);
		
		if(null == list | list.size() == 0){
			return null;
		}
		
		return list.get(0);
		
	}
	
	/**
	 * 查询用户剧组和角色
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	public CrewUserMapModel queryCrewUserBycrewId(String userId, String crewId) throws Exception{
		String sql = "select * from "+CrewUserMapModel.TABLE_NAME+" where userid=? and crewId=?";
		return this.queryForObject(sql, new Object[] {userId, crewId}, CrewUserMapModel.class);
	}
	
	/**
	 * 根据用户ID查询记录
	 */
	public List<CrewUserMapModel> queryByUserId(String userId){
		String sql = "select * from "+CrewUserMapModel.TABLE_NAME+" where userid=?";
		return this.query(sql, new Object[]{userId}, CrewUserMapModel.class, null);
	}
	
	/**
	 * 查询用户所有未过期的状态有效的剧组
	 */
	public List<CrewUserMapModel> queryUserAllEffectiveCrewMap(String userId){
		String sql = "select tcum.* from tab_crew_user_map tcum, tab_crew_info tci where tcum.userid=? AND tci.startDate <= CURDATE() AND tci.endDate >= CURDATE() and tcum.status = 1 and tci.crewid=tcum.crewId";
		return this.query(sql, new Object[]{userId}, CrewUserMapModel.class, null);
	}
	
	/**
	 * @Description 根据用户id查询当前用户所拥有的剧组id和名称
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> queryCrewInfoByUserId(String userId){
		String sql = " select tci.crewid,tci.crewname from tab_crew_user_map tcum LEFT JOIN tab_crew_info tci on tcum.crewid = tci.crewid where tcum.userid = ? ";
		return this.getJdbcTemplate().queryForList(sql, new Object[]{userId});
	}
	
	public List<Map<String, Object>> queryCrewInfoByUserIdNotContainsCurrCrew(String userId,String crewId){
		String sql = " select tci.crewid,tci.crewname from tab_crew_user_map tcum LEFT JOIN tab_crew_info tci on tcum.crewid = tci.crewid where tcum.userid = ? and tcum.crewid !=?";
		return this.getJdbcTemplate().queryForList(sql, new Object[]{userId,crewId});
	}
	
	/**
	 * 把用户拥有的所有的剧组设置为非默认
	 * @param crewId
	 * @param userId
	 */
	public void unDefaultUserCrew(String userId) {
		String sql = "update tab_crew_user_map set ifDefault = 0 where userId=?";
		this.getJdbcTemplate().update(sql, new Object[] {userId});
	}
	
	/**
	 * 设置用户指定的剧组为默认
	 * @param userId
	 * @param crewId
	 */
	public void defaultUserCrew(String userId, String crewId) {
		String sql = "update tab_crew_user_map set ifDefault = 1 where userId = ? and crewId = ?";
		this.getJdbcTemplate().update(sql, userId, crewId);
	}
	
	/**
	 * 删除剧组用户关联关系
	 * @param crewId
	 * @param userId
	 */
	public void deleteCrewUserMap(String crewId, String userId) {
		String sql = "DELETE FROM " + CrewUserMapModel.TABLE_NAME + " WHERE crewId = ? AND userId = ?";
		this.getJdbcTemplate().update(sql, new Object[]{crewId, userId});
	}
}
