package com.xiaotu.makeplays.prepare.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.prepare.model.PrepareCrewPeopleModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * @ClassName PrepareCrewPeopleDao
 * @Description 筹备期 选角进度
 * @author Administrator
 * @Date 2017年2月10日 上午10:35:40
 * @version 1.0.0
 */
@Repository
public class PrepareCrewPeopleDao extends BaseDao<PrepareCrewPeopleModel>{
	/**
	 * @Description 查询筹备期 剧组人员信息
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryPrepareCrewPeopleInfo(String crewId){
		String sql = "select * from "+PrepareCrewPeopleModel.TABLE_NAME+" where crewId = ? ORDER BY parentId";
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql, new Object[]{crewId});
		return list;
	}
	
	public void savePrepareCrewPeopleInfo(String id,String groupName,String duties,String name,String phone,String reviewer,
			String confirmDate,String arrivalTime,String payment,String parentId,String crewId){
		String sql = "insert into "+PrepareCrewPeopleModel.TABLE_NAME+"  (id,groupName,duties,name,phone,reviewer,confirmDate,arrivalTime,payment,parentId,crewId,createTime) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?)";
		
		this.getJdbcTemplate().update(sql,new Object[]{id,groupName,duties,name,phone,reviewer,confirmDate,arrivalTime,payment,parentId,crewId,new Timestamp(System.currentTimeMillis())});
	}
	
	public void updatePrepareCrewPeopleInfo(String id,String groupName,String duties,String name,String phone,String reviewer,
			String confirmDate,String arrivalTime,String payment){
		
		String sql = "update "+PrepareCrewPeopleModel.TABLE_NAME+" set groupName=?,duties=?,name=?,phone=?,reviewer=?,confirmDate=?,arrivalTime=?,payment=? where id = ?";
		this.getJdbcTemplate().update(sql,new Object[]{groupName,duties,name,phone,reviewer,confirmDate,arrivalTime,payment,id});
	}
	
	public void delPrepareCrewPeopleInfo(String[] id){
		String sql = "delete from "+PrepareCrewPeopleModel.TABLE_NAME+" where id = ?";
		List<Object[]> args = new ArrayList<Object[]>();
		for(String str:id){
			args.add(new Object[]{str});
		}
		this.getJdbcTemplate().batchUpdate(sql, args);
	}
	
}
