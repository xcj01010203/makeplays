package com.xiaotu.makeplays.prepare.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.prepare.model.PrepareArteffectLocationModel;
import com.xiaotu.makeplays.utils.BaseDao;
/**
 * @ClassName PrepareArteffectLocationDao
 * @Description 筹备期  美术视觉 -场景
 * @author Administrator
 * @Date 2017年2月10日 上午10:33:54
 * @version 1.0.0
 */
@Repository
public class PrepareArteffectLocationDao extends BaseDao<PrepareArteffectLocationModel>{

	/**
	 * @Description 查询筹备期美术视觉场景信息
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryPrepareArteffectLocationInfo(String crewId){
		String sql = "select * from "+PrepareArteffectLocationModel.TABLE_NAME+" where crewId = ?";
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql, new Object[]{crewId});
		return list;
	}
	/**
	 * @Description 根据id删除一条信息
	 * @param id
	 */
	public void delPrepareArteffectLocationInfo(String id){
		String sql = "delete from "+PrepareArteffectLocationModel.TABLE_NAME+" where id = ?";
		this.getJdbcTemplate().update(sql,new Object[]{id});
	}
	
	public void savePrepareArteffectLocationInfo(String location,String designSketch,String designSketchDate,String workDraw,
			String workDrawDate,String scenery,String sceneryDate,String reviewer,String opinion,String id,String crewId){
		String sql = "insert into "+PrepareArteffectLocationModel.TABLE_NAME+" (id,location,designSketch,designSketchDate,workDraw,workDrawDate,scenery,"
				+ "sceneryDate,reviewer,opinion,createTime,crewId) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		this.getJdbcTemplate().update(sql, new Object[]{id,location,designSketch,designSketchDate,workDraw,workDrawDate,scenery,sceneryDate,reviewer,opinion,new Timestamp(System.currentTimeMillis()),crewId});
		
	}
	public void updatePrepareArteffectLocationInfo(String location,String designSketch,String designSketchDate,String workDraw,
			String workDrawDate,String scenery,String sceneryDate,String reviewer,String opinion,String id){
		String sql = "update "+PrepareArteffectLocationModel.TABLE_NAME+" set location=?,designSketch=?,designSketchDate=?,workDraw=?,workDrawDate=?,scenery=?,"
				+ "sceneryDate=?,reviewer=?,opinion=?  where id = ? ";
		this.getJdbcTemplate().update(sql, new Object[]{location,designSketch,designSketchDate,workDraw,workDrawDate,scenery,sceneryDate,reviewer,opinion,id});
	}
	
}
