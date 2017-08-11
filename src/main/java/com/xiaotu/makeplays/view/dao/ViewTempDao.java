package com.xiaotu.makeplays.view.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.view.model.ViewTempModel;

/**
 * 操作场景零时变量的dao
 * @author xuchangjian
 */
@Repository
public class ViewTempDao extends BaseDao<ViewTempModel>{

	/**
	 * 批量新增操作
	 * @param atmosphereList
	 * @throws Exception 
	 */
	public void addMany (List<ViewTempModel> sceneTempList) throws Exception {
		for (ViewTempModel sceneTemp : sceneTempList) {
			this.add(sceneTemp);
		}
	}
	
	/**
	 * 根据剧本ID查找临时表中指定类型的数据
	 * @param crewId 剧组ID
	 * @param sceneTempDataType	数据类型
	 * @return
	 */
	public List<ViewTempModel> queryManyByCrewId (String crewId, int sceneTempDataType) {
		String sql = "select * from " + ViewTempModel.TABLE_NAME + " where crewId = ? and dataType = ?";
		return this.query(sql, new Object[] {crewId, sceneTempDataType}, ViewTempModel.class, null);
	}
	
	/**
	 * 根据集次、场次、剧组ID查找临时表中的的数据
	 * @param seriesNo	集次
	 * @param viewNo	场次
	 * @param crewId	剧组ID
	 * @param sceneTempDataType	数据类型
	 * @return
	 */
	public ViewTempModel queryOneBySeriesViewCrewId (int seriesNo, String viewNo, String crewId) {
		String sql = "select * from " + ViewTempModel.TABLE_NAME + " where seriesNo = ? and viewNo = ? and crewId = ?";
		
		ViewTempModel sceneTemp = null;
		Object[] args = new Object[] {seriesNo, viewNo, crewId};
		if (getResultCount(sql, args) == 1) {
			sceneTemp = this.getJdbcTemplate().queryForObject(sql, args, ParameterizedBeanPropertyRowMapper
					.newInstance(ViewTempModel.class));
		}
		
		return sceneTemp;
	}
	
	/**
	 * 计算根据集次、场次、剧组ID查找临时表中的的数据数目
	 * @param seriesNo
	 * @param viewNo
	 * @param crewId
	 * @return
	 */
	public int countBySeriesViewCrewId (int seriesNo, String viewNo, String crewId) {
		String sql = "select * from " + ViewTempModel.TABLE_NAME + " where seriesNo = ? and viewNo = ? and crewId = ?";
		Object[] args = new Object[] {seriesNo, viewNo, crewId};
		
		return getResultCount(sql, args);
	}
	
	/**
	 * 根据剧本ID删除临时表中指定类型的数据
	 * @param crewId 剧组ID
	 * @param sceneTempDataType	数据类型
	 * @return
	 */
	public void deleteManyByCrewId (String crewId, int sceneTempDataType) {
		String sql = "delete from " + ViewTempModel.TABLE_NAME + " where crewId = ? and dataType = ?";
		
		this.getJdbcTemplate().update(sql, new Object[] {crewId, sceneTempDataType});
	}
	
	/**
	 * 根据剧组ID删除临时表中的数据
	 * @param crewId 剧组ID
	 * @return
	 */
	public void deleteManyByCrewId (String crewId, int seriesNo, String viewNo) {
		StringBuffer sql = new StringBuffer();
		List<Object> param = new ArrayList<Object>();
		sql.append(" delete from "+ ViewTempModel.TABLE_NAME + " where crewId = ?");
		param.add(crewId);
		if (seriesNo != 0) {
			sql.append(" and seriesNo = ?");
			param.add(seriesNo);
		}
		
		if (StringUtils.isNotBlank(viewNo)) {
			sql.append(" and viewNo = ?");
			param.add(viewNo);
		}
		
		this.getJdbcTemplate().update(sql.toString(), param.toArray());
	}
	
	/**
	 * 根据集次、场次、剧组ID删除临时表中的指定类型的数据
	 * @param seriesNo	集次
	 * @param viewNo	场次
	 * @param crewId	剧组ID
	 * @param sceneTempDataType	数据类型
	 * @return
	 */
	public void deleteOneByseriesViewCrewId (int seriesNo, String viewNo, String crewId, int sceneTempDataType) {
		String sql = "delete from " + ViewTempModel.TABLE_NAME + " where seriesNo = ? and viewNo = ? and crewId = ?";
		
		this.getJdbcTemplate().update(sql, new Object[] {seriesNo, viewNo, crewId, sceneTempDataType});
	}
}
