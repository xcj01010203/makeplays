package com.xiaotu.makeplays.prepare.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.prepare.model.PrepareArteffectRoleModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * @ClassName PrepareArteffectRoleDao
 * @Description 筹备期  美术视觉-角色
 * @author Administrator
 * @Date 2017年2月10日 上午10:34:57
 * @version 1.0.0
 */
@Repository
public class PrepareArteffectRoleDao extends BaseDao<PrepareArteffectRoleModel>{
	/**
	 * @Description 查询美术视觉 角色信息
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryPrepareArteffectRoleInfo(String crewId){
		String sql = "select * from "+PrepareArteffectRoleModel.TABLE_NAME+" where crewId = ?";
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql, new Object[]{crewId});
		return list;
	}
	
	public void delPrepareArteffectRoleInfo(String id){
		String sql = "delete from "+PrepareArteffectRoleModel.TABLE_NAME+" where id = ?";
		this.getJdbcTemplate().update(sql,new Object[]{id});
	}
	
	
	public void saveRoleInfo(String role,String modelling,String confirmDate,
			String status,String mark,String reviewer,String id,String crewId){
		String sql = "insert into "+PrepareArteffectRoleModel.TABLE_NAME+" (id,role,modelling,confirmDate,status,mark,reviewer,createTime,crewId) values(?,?,?,?,?,?,?,?,?)";
		this.getJdbcTemplate().update(sql, new Object[]{id,role,modelling,confirmDate,status,mark,reviewer,new Timestamp(System.currentTimeMillis()),crewId});
	}
	
	
	public void updateRoleInfo(String role,String modelling,String confirmDate,
			String status,String mark,String reviewer,String id){
		String updateSql = "update "+PrepareArteffectRoleModel.TABLE_NAME+" set role =?,modelling=?,confirmDate=?,status=?,mark=?,reviewer=? where id = ? ";
		this.getJdbcTemplate().update(updateSql,new Object[]{role,modelling,confirmDate,status,mark,reviewer,id});
	
	}
}
