package com.xiaotu.makeplays.community.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.community.model.StoreInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 收藏信息操作的dao
 * @author wanrenyi 2016年9月2日下午2:43:36
 */
@Repository
public class StoreInfoDao extends BaseDao<StoreInfoModel>{
 
	/**
	 * 收藏组训
	 * @param storeInfo
	 * @throws Exception 
	 */
	public void addStoreByBean(StoreInfoModel storeInfo) throws Exception {
		this.add(storeInfo);
	}
	
	/**
	 * 取消收藏
	 * @param storeInfo
	 */
	public void deleteStoreInfo(StoreInfoModel storeInfo) {
		StringBuffer sql = new StringBuffer();
		List<String> param = new ArrayList<String>();
		sql.append("delete from " + StoreInfoModel.TABLE_NAME + " where teamId = ? and userId = ?");
		param.add(storeInfo.getTeamId());
		param.add(storeInfo.getUserId());
		
		this.getJdbcTemplate().update(sql.toString(), param.toArray());
	}
	
	/**
	 * 获取组训的收藏人数
	 * @param teamId
	 * @return
	 */
	public List<Map<String, Object>> getStoreCount() {
		String sql = "select count(userId) count, teamId  from " + StoreInfoModel.TABLE_NAME + " group by teamId";
		List<Map<String, Object>> list = this.query(sql, null, null);
		
		return list;
	}
	
	/**
	 * 根据组训id查询出收藏组训信息的所有人员的信息
	 * @param teamId
	 * @return
	 */
	public List<Map<String, Object>> getStoreUser(String teamId){
		String sql = "select userId from tab_store_info where teamId = ?";
		List<Map<String, Object>> list = this.query(sql, new Object[] {teamId}, null);
		return list;
	}
	
	/**
	 * 根据组训id和用户id查询出一条收藏信息
	 * @param teamId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public List<StoreInfoModel> getOneStoreInfo(String teamId, String userId) throws Exception {
		String sql = "select * from tab_store_info where teamId = ? and userId = ?";
		List<StoreInfoModel> list = this.query(sql, new Object[] {teamId, userId}, StoreInfoModel.class, null);
		return list;
	}
}
