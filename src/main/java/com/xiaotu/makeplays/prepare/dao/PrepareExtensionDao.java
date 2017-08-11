package com.xiaotu.makeplays.prepare.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.prepare.model.PrepareExtensionModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * @ClassName PrepareExtensionDao
 * @Description 筹备期 宣传进度
 * @author Administrator
 * @Date 2017年2月10日 上午10:36:20
 * @version 1.0.0
 */
@Repository
public class PrepareExtensionDao extends BaseDao<PrepareExtensionModel>{
	/**
	 * @Description  查询宣传进度信息
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryPrepareExtensionInfo(String crewId){
		String sql = " select * from "+PrepareExtensionModel.TABLE_NAME+" where crewId = ?";
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql, new Object[]{crewId});
		return list;
	}
	
	public void delPrepareExtensionInfo(String id){
		String sql = "delete from "+PrepareExtensionModel.TABLE_NAME+" where id = ?";
		this.getJdbcTemplate().update(sql, new Object[]{id});
	}
	
	/**
	 * @Description 修改信息
	 * @param id
	 * @param type
	 * @param material
	 * @param personLiable
	 * @param reviewer
	 */
	public void updateExtensionInfo(String id,String type,String material,String personLiable,String reviewer){
		String sql = "update "+PrepareExtensionModel.TABLE_NAME+" set type = ? ,material=?, personLiable=?, reviewer=? where id = ?";
		this.getJdbcTemplate().update(sql, new Object[]{type,material,personLiable,reviewer,id});
	}
	
	/**
	 * @Description 保存信息
	 * @param id
	 * @param type
	 * @param material
	 * @param personLiable
	 * @param reviewer
	 */
	public void saveExtensionInfo(String id,String type,String material,String personLiable,String reviewer,String crewId){
		String sql = "insert into "+PrepareExtensionModel.TABLE_NAME+" (id,type,material,personLiable,reviewer,crewId,createTime) values (?,?,?,?,?,?,?)";
		this.getJdbcTemplate().update(sql, new Object[]{id,type,material,personLiable,reviewer,crewId,new Timestamp(System.currentTimeMillis())});
	}
}
