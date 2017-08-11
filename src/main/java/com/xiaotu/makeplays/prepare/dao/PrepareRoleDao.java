package com.xiaotu.makeplays.prepare.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.prepare.model.PrepareRoleModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * @ClassName PrepareRoleDao
 * @Description 筹备期选角进度
 * @author Administrator
 * @Date 2017年2月10日 上午10:37:37
 * @version 1.0.0
 */
@Repository
public class PrepareRoleDao extends BaseDao<PrepareRoleModel>{
	public List<Map<String, Object>> queryPrepareRoleInfo(String crewId){
		String sql = "select * from "+PrepareRoleModel.TABLE_NAME+" where crewId = ? ORDER BY parentId";
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql, new Object[]{crewId});
		return list;
	}
	
	public void savePrepareRoleInfo(String id,String role,
			String actor,String schedule,String content,String mark,String parentId,String crewId){
		
		String sql = "insert into "+PrepareRoleModel.TABLE_NAME+" (id,role,actor,schedule,content,mark,parentId,crewId,createTime) values(?,?,?,?,?,?,?,?,?)";
		this.getJdbcTemplate().update(sql,new Object[]{id,role,actor,schedule,content,mark,parentId,crewId,new Timestamp(System.currentTimeMillis())});
		
	}
	public void updatePrepareRoleInfo(String id,String role,String actor,String schedule,String content,String mark){
		String sql = "update "+PrepareRoleModel.TABLE_NAME+" set role=?,actor=?,schedule=?,content=?,mark=? where id = ?";
		this.getJdbcTemplate().update(sql,new Object[]{role,actor,schedule,content,mark,id});
	}
	
	public void delPrepareRoleInfo(String[] id){
		String  sql = "delete from "+PrepareRoleModel.TABLE_NAME+" where id = ?";
		
		List<Object[]> args = new ArrayList<Object[]>();
		for(String str:id){
			args.add(new Object[]{str});
		}
		this.getJdbcTemplate().batchUpdate(sql, args);
	}
	
}
