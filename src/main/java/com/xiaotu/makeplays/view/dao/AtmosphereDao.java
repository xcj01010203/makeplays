package com.xiaotu.makeplays.view.dao;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.view.model.AtmosphereInfoModel;

/**
 * 气氛信息
 * @author xuchangjian
 */
@Repository
public class AtmosphereDao extends BaseDao<AtmosphereInfoModel> {
	/**
	 * 根据剧组ID和气氛名称查找对应的气氛
	 * @param crewId
	 * @param atmosphereName
	 * @return
	 */
	public AtmosphereInfoModel queryByCrewIdAndAtmName(String crewId, String atmosphereName) {
		String sql = "select * from tab_atmosphere_info where atmosphereName = ? and crewId = ?";
		
		List<AtmosphereInfoModel> atmosphereList = this.query(sql, new Object[]{atmosphereName, crewId}, AtmosphereInfoModel.class, null);
		
		if (atmosphereList != null && atmosphereList.size() > 0) {
			return atmosphereList.get(0);
		}
		return null;
	}
	
	/**
	 * 批量新增操作
	 * @param atmosphereList
	 * @throws Exception 
	 */
	public void addMany (List<AtmosphereInfoModel> atmosphereList) throws Exception {
		for (AtmosphereInfoModel atmosphere : atmosphereList) {
			this.add(atmosphere);
		}
	}
	
	/**
	 * 更新数据
	 * @param atmosphere	气氛对象
	 * @throws Exception 
	 */
	public void update(AtmosphereInfoModel atmosphere) throws Exception {
		this.update(atmosphere, "atmosphereId");
	}
	
	/**
	 * 根据气氛ID查找对应的气氛信息
	 * @return
	 */
	public AtmosphereInfoModel queryOneById(String atmosphereId) {
		String sql = "select * from tab_atmosphere_info where atmosphereId = ?";
		
		AtmosphereInfoModel atmosphere = null;
		Object[] args = new Object[] {atmosphereId};
		if (getResultCount(sql, args) == 1) {
			atmosphere = this.getJdbcTemplate().queryForObject(sql, new Object[]{atmosphereId}, ParameterizedBeanPropertyRowMapper
					.newInstance(AtmosphereInfoModel.class));
		}
		return atmosphere;
	}
	
	/**
	 * 根据剧本ID查找对应的气氛信息
	 * @return
	 */
	public List<AtmosphereInfoModel> queryByCrewId(String crewId) {
		String sql = "select distinct a.atmosphereName,a.atmosphereId from "+AtmosphereInfoModel.TABLE_NAME+" a where a.crewId = ? or a.crewId='0' order by a.atmosphereName ";
		
		List<AtmosphereInfoModel> atmosphere = this.query(sql, new Object[]{crewId}, AtmosphereInfoModel.class, null);
		
		return atmosphere;
	}
	
	/**
	 * 查询在场景中存在的气氛信息
	 * @param crewId
	 * @return
	 */
	public List<AtmosphereInfoModel> queryExistByCrewId(String crewId) {
		//String sql = "select distinct a.atmosphereName,a.atmosphereId from "+AtmosphereInfoModel.TABLE_NAME+" a where (a.crewId = ? or a.crewId='0') and exists(select 1 from tab_view_info tvi where tvi.atmosphereId = a.atmosphereId)";
		String sql = "select distinct a.atmosphereName,a.atmosphereId from " + AtmosphereInfoModel.TABLE_NAME + " a ,tab_view_info tvi where (a.crewId = ? or a.crewId='0') and tvi.atmosphereId = a.atmosphereId and tvi.crewId=?";
		List<AtmosphereInfoModel> atmosphere = this.query(sql, new Object[]{crewId,crewId}, AtmosphereInfoModel.class, null);
		
		return atmosphere;
	}
}
