package com.xiaotu.makeplays.crewPicture.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.crewPicture.model.CrewPictureInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 剧照操作的dao层
 * @author wanrenyi 2017年2月28日下午3:50:39
 */
@Repository
public class CrewPictureInfoDao extends BaseDao<CrewPictureInfoModel>{

	/**
	 * 根据id查询出剧照的详细信息
	 * @param pictureId
	 * @return
	 * @throws Exception 
	 */
	public CrewPictureInfoModel queryPictureInfoById(String pictureId) throws Exception {
		String sql = " select * from "+ CrewPictureInfoModel.TABLE_NAME +" where Id = ?";
		return this.queryForObject(sql, new Object[] {pictureId}, CrewPictureInfoModel.class);
	}
	
	/**
	 * 查询当前剧组的所有剧照分组的名称
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryPictureGroupNameList(String crewId){
		String sql = " select attpackName,id,attpackId from "+ CrewPictureInfoModel.TABLE_NAME+ " where crewId = ?";
		return this.query(sql, new Object[] {crewId}, null);
	}
	
	/**
	 * 查询出当前名称是否已经存在
	 * @param crewId
	 * @param groupName
	 * @return
	 */
	public List<Map<String, Object>> queryIsExistGroupName(String crewId, String groupName){
		String sql= " select * from "+ CrewPictureInfoModel.TABLE_NAME + " where crewId = ? and attpackName = ?";
		
		return this.query(sql, new Object[] {crewId, groupName},  null);
	}
	
	/**
	 * 根据id获取剧照的详细信息
	 * @param id
	 * @param crewId
	 * @return
	 * @throws Exception 
	 */
	public CrewPictureInfoModel queryCrewPictureInfoById(String id, String crewId) throws Exception {
		String sql = "select * from "+ CrewPictureInfoModel.TABLE_NAME + " where id = ? and crewId = ?";
		
		return queryForObject(sql, new Object[] {id, crewId},  CrewPictureInfoModel.class);
	}
	
	/**
	 * 查询当前剧组的相册列表
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryCrewPictureList(String crewId, String crewpictureId){
		StringBuffer sql = new StringBuffer();
		List<String> param = new ArrayList<String>();
		sql.append(" SELECT	tpi.*");
		sql.append(" FROM	tab_crew_picture_info tpi");
		sql.append("	where tpi.crewId = ?");
		param.add(crewId);
		if (StringUtils.isNotBlank(crewpictureId)) {
			sql.append(" and tpi.id = ?");
			param.add(crewpictureId);
		}
		sql.append(" ORDER BY tpi.createTime DESC");
		
		return this.query(sql.toString(), param.toArray(), null);
	}
	
	/**
	 * 获取当前相册下所有的照片
	 * @param crewPictureId
	 * @return
	 */
	public List<Map<String, Object>> queryAttachmentListByCrewPictureId(String crewPictureId){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT	tpi.id crewPictureId, tpi.attpackName,tai.*");
		sql.append(" FROM	tab_crew_picture_info tpi");
		sql.append(" LEFT JOIN tab_attachment_info tai ON tai.attpackId = tpi.attpackId");
		sql.append(" WHERE tpi.id = ?");
		sql.append("	order by tai.createTime desc");
		
		return this.query(sql.toString(), new Object[] {crewPictureId}, null);
	}
	
	/**
	 * 更新封面图片
	 * @param attachmentIds
	 */
	public void updateCrewPictureIndexId(String[] attachmentIds, String crewId) {
		StringBuffer sql = new StringBuffer();
		sql.append(" UPDATE tab_crew_picture_info ");
		sql.append(" SET indexPictureId = '' WHERE crewId =? AND  indexPictureId IN ( ");
		for (int i = 0; i < attachmentIds.length; i++) {
			String attachmentId = attachmentIds[i];
			if (i == 0) {
				sql.append("'"+attachmentId+"'");
			}else {
				sql.append(" , '" + attachmentId+"'");
			}
			
		}
		sql.append(" )");
		
		this.getJdbcTemplate().update(sql.toString(), crewId);
	}
}
