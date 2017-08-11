package com.xiaotu.makeplays.prepare.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.prepare.model.PrepareOperateModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * @ClassName PrepareOperateDao
 * @Description 筹备期  商务运营
 * @author Administrator
 * @Date 2017年2月10日 上午10:36:59
 * @version 1.0.0
 */
@Repository
public class PrepareOperateDao extends BaseDao<PrepareOperateModel>{

	/**
	 * @Description 查询剧组筹备期商务运营信息
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryPrepareOperateInfo(String crewId){
		String sql = "select * from "+PrepareOperateModel.TABLE_NAME +" where crewId = ? ORDER BY parentId ";
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql, new Object[]{crewId});
		return list;
	}
	
	/**
	 * @Description 根据id删除一条数据
	 * @param id
	 */
	public void delPrepareOperateInfo(String[] id){
		String sql ="delete from "+PrepareOperateModel.TABLE_NAME +" where id = ?";
		
		List<Object[]> args = new ArrayList<Object[]>();
		for(String str:id){
			args.add(new Object[]{str});
		}
		this.getJdbcTemplate().batchUpdate(sql, args);
	}
	
	
	/**
	 * @Description 保存筹备运营信息
	 * @param id
	 * @param operateType 合作种类
	 * @param operateBrand 品牌
	 * @param operateMode 合作方式
	 * @param operateCost 合作费用
	 * @param contactName 联系人名称
	 * @param phoneNumber 联系电话
	 * @param mark 备注
	 * @param personLiable 负责人
	 * @param parentId 父id
	 * @param crewId
	 */
	public void savePrepareOperateInfo(String id,String operateType,String operateBrand,String operateMode,String operateCost,String contactName,
			String phoneNumber,String mark,String personLiable,String parentId,String crewId){
		String sql = "insert into "+PrepareOperateModel.TABLE_NAME+" (id,operateType,operateBrand,operateMode,operateCost,contactName,phoneNumber,mark,personLiable,parentId,crewId)"
				+ " values (?,?,?,?,?,?,?,?,?,?,?)";
		this.getJdbcTemplate().update(sql, new Object[]{id,operateType,operateBrand,operateMode,operateCost,contactName,phoneNumber,mark,personLiable,parentId,crewId});
	}
	
	/**
	 * @Description 修改筹备运营信息
	 * @param id
	 * @param operateType 合作种类
	 * @param operateBrand 品牌
	 * @param operateMode 合作方式
	 * @param operateCost 合作费用
	 * @param contactName 联系人名称
	 * @param phoneNumber 联系电话
	 * @param mark 备注
	 * @param personLiable 负责人
	 */
	public void updatePrepareOperateInfo(String id,String operateType,String operateBrand,String operateMode,String operateCost,String contactName,
			String phoneNumber,String mark,String personLiable){
		String sql = "update "+PrepareOperateModel.TABLE_NAME+" set operateType=?,operateBrand=?,operateMode=?,operateCost=?,contactName=?,phoneNumber=?,mark=?,personLiable=? where id = ?";
		this.getJdbcTemplate().update(sql, new Object[]{operateType,operateBrand,operateMode,operateCost,contactName,phoneNumber,mark,personLiable,id});
		
		
	}
	
}
