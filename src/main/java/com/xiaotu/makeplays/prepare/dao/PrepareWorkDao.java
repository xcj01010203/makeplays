package com.xiaotu.makeplays.prepare.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.prepare.model.PrepareWorkModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * @ClassName PrepareWorkDao
 * @Description 筹备期  办公筹备
 * @author Administrator
 * @Date 2017年2月10日 上午10:38:57
 * @version 1.0.0
 */
@Repository
public class PrepareWorkDao extends BaseDao<PrepareWorkModel>{

	/**
	 * @Description  根据剧组id查询 筹备期  办公筹备
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryPrepareOperateInfo(String crewId){
		String sql = "select * from "+PrepareWorkModel.TABLE_NAME +" where crewId = ? ORDER BY parentId";
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql, new Object[]{crewId});
		return list;
	}
	
	/**
	 * @Description 根据id删除一条办公筹备数据
	 * @param id
	 */
	public void delPrepareWorkInfo(String[] id){
		
		String sql = "delete from "+PrepareWorkModel.TABLE_NAME+" where id = ?";
		List<Object[]> args = new ArrayList<Object[]>();
		for(String str:id){
			args.add(new Object[]{str});
		}
		this.getJdbcTemplate().batchUpdate(sql, args);
	}
	
	/**
	 * @Description  保存一条办公筹备信息
	 * @param id  
	 * @param type 类型
	 * @param purpose 用途
	 * @param schedule 进度
	 * @param personLiable 负责人
	 * @param parentId 父id
	 * @param crewId 剧组id
	 */
	public void savePrepareWorkInfo(String id,String type,String purpose,String schedule,String personLiable,
			String parentId,String crewId){
		String sql = "insert into "+ PrepareWorkModel.TABLE_NAME+" (id,type,purpose,schedule,personLiable,parentId,crewId,createTime) values (?,?,?,?,?,?,?,?)";
		this.getJdbcTemplate().update(sql, new Object[]{id,type,purpose,schedule,personLiable,parentId,crewId,new Timestamp(System.currentTimeMillis())});
	}
	
	/**
	 * @Description  修改一条办公筹备数据
	 * @param id
	 * @param type类型
	 * @param purpose用途
	 * @param schedule 进度
	 * @param personLiable负责人
	 */
	public void updatePrepareWorkInfo(String id,String type,String purpose,String schedule,String personLiable){
		String sql = "update "+PrepareWorkModel.TABLE_NAME +" set type=?,purpose=?,schedule=?,personLiable=? where id =?";
		this.getJdbcTemplate().update(sql, new Object[]{type,purpose,schedule,personLiable,id});
	}
	
}
