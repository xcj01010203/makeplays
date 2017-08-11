package com.xiaotu.makeplays.crew.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.attachment.model.AttachmentModel;
import com.xiaotu.makeplays.attachment.model.AttachmentPacketModel;
import com.xiaotu.makeplays.attachment.model.constants.AttachmentBuzType;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.model.CrewUserMapModel;
import com.xiaotu.makeplays.finance.model.FinanceSettingModel;
import com.xiaotu.makeplays.hotelInfo.model.CheckinHotelInfoModel;
import com.xiaotu.makeplays.hotelInfo.model.HotelInfoModel;
import com.xiaotu.makeplays.locationsearch.model.SceneViewInfoModel;
import com.xiaotu.makeplays.notice.model.NoticeInfoModel;
import com.xiaotu.makeplays.notice.model.NoticePictureModel;
import com.xiaotu.makeplays.notice.model.NoticeRoleTimeModel;
import com.xiaotu.makeplays.roleactor.model.EvaluateInfoModel;
import com.xiaotu.makeplays.roleactor.model.EvaluateTagMapModel;
import com.xiaotu.makeplays.scenario.model.ScenarioInfoModel;
import com.xiaotu.makeplays.user.model.ContactSysroleMapModel;
import com.xiaotu.makeplays.user.model.CrewContactModel;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * @类名：CrewClearDao.java
 * @作者：李晓平
 * @时间：2016年10月31日 上午11:17:04 
 * @描述：剧组清除
 */
@Repository
public class CrewClearDao extends BaseDao<CrewInfoModel>{
		
	/**
	 * 查询表记录数
	 * @param crewId
	 * @return
	 */
	public Integer queryRecordNum(String crewId, String fieldName, String tableName) {
		String sql = "select count(*) as num from " + tableName
				+ " where " + fieldName + "=?";
		Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql, crewId);
		return Integer.parseInt(map.get("num") + "");
	}
	
	/**
	 * 查询住宿信息记录数
	 * @param crewId
	 * @return
	 */
	public int queryRecordNumForInHotel(String crewId) {
		String sql = "select count(*) as num from " + HotelInfoModel.TABLE_NAME
				+ " where crewId=?";
		Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql, crewId);
		return Integer.parseInt(map.get("num") + "");
	}
	
	/**
	 * 查询剧组用户的数量,去掉客服
	 * @param crewId
	 * @return
	 */
	public Integer queryRecordNumForCrewUser(String crewId) {
		String sql = "select count(*) as num from " + CrewUserMapModel.TABLE_NAME
				+ " where crewId = ? and userId not in (select userId from " + UserInfoModel.TABLE_NAME
				+ " where type>0)";
		Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql, crewId);
		return Integer.parseInt(map.get("num") + "");
	}
	
	/**
	 * 删除演职员评价与标签关联关系表
	 * @param crewId
	 */
	public void deleteEvaluateTagMapByCrewId(String crewId) {		
		String sql = "delete from " + EvaluateTagMapModel.TABLE_NAME 
				+ " where evaluateId in (select evaluateId from " 
				+ EvaluateInfoModel.TABLE_NAME + " where crewId=?)";		
		this.getJdbcTemplate().update(sql, crewId);
	}
	
	/**
	 * 删除通告单角色化妆时间
	 * @param crewId
	 */
	public void deleteNoticeRoleTimeByCrewId(String crewId) {
		String sql = "delete from " + NoticeRoleTimeModel.TABLE_NAME 
				+ " where noticeId in (select noticeId from " 
				+ NoticeInfoModel.TABLE_NAME + " where crewId=?)";		
		this.getJdbcTemplate().update(sql, crewId);
	}

	/**
	 * 删除计划影响因素
	 * @param crewId
	 */
	public void deletePlanFactorByCrewId(String crewId) {		
		String sql = "delete from tab_plan_factor where planid in (select id from tab_plan where crewid=?)";		
		this.getJdbcTemplate().update(sql, crewId);
	}
	
	/**
	 * 删除计划场景
	 * @param crewId
	 */
	public void deletePlanViewByCrewId(String crewId) {		
		String sql = "delete from tab_plan_view where planid in (select id from tab_plan where crewid=?)";		
		this.getJdbcTemplate().update(sql, crewId);
	}
	
	/**
	 * 删除附件包
	 * @param crewId
	 */
	public void deleteAttachmentPacketByCrewId(String crewId, AttachmentBuzType buzTye) {		
		String sql = "delete from " + AttachmentPacketModel.TABLE_NAME
				+ " where crewId=? and buzType="
				+ buzTye.getValue();	
		this.getJdbcTemplate().update(sql, crewId);
	}
	
	/**
	 * 删除附件信息
	 * @param crewId
	 */
	public void deleteAttachmentInfoByCrewId(String crewId, AttachmentBuzType buzTye) {		
		String sql = "delete from " + AttachmentModel.TABLE_NAME
				+ " where crewId=? and attpackId in (select id from" 
				+ " tab_attachment_packet where crewId=? and buzType=" + buzTye.getValue() + ")";
		this.getJdbcTemplate().update(sql, crewId, crewId);
	}
	
	/**
	 * 查询剧本存放路径
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryScenarioInfoByCrewId(String crewId) {
		String sql = "select scenarioUrl from " + ScenarioInfoModel.TABLE_NAME
				+ " where crewId=?";
		return this.getJdbcTemplate().queryForList(sql, crewId);
	}
	
	/**
	 * 查询通告单预览图片地址
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryNoticePictureByCrewId(String crewId) {
		String sql = "select bigPicurl,smallPicurl from "
				+ NoticePictureModel.TABLE_NAME + " where crewId=?";
		return this.getJdbcTemplate().queryForList(sql, crewId);
	}
	
	/**
	 * 查询附件地址
	 * @param crewId
	 * @param buzTye
	 * @return
	 */
	public List<Map<String, Object>> queryAttachmentInfoByCrewId(String crewId, AttachmentBuzType buzType) {	
		String sql = "select hdStorePath,sdStorePath from "
				+ AttachmentModel.TABLE_NAME
				+ " where crewId=? and attpackId in (select id from" 
				+ " tab_attachment_packet where crewId=? and buzType=?)";
		List<Map<String, Object>> result = this.getJdbcTemplate().queryForList(sql, new Object[]{crewId, crewId, buzType.getValue()});
		return result;
	}
	
	/**
	 * 判断是否启用财务密码
	 * @param crewId
	 * @return
	 */
	public Map<String, Object> queryFinancePassword(String crewId) {
		String sql = "select pwdStatus from " + FinanceSettingModel.TABLE_NAME 
				+ " where crewId=?";
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql, crewId);
		if(list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * 财务密码（清空是否启用密码功能及密码字段）
	 * @param crewId
	 */
	public void clearFinancePassword(String crewId) {
		String sql = "update " + FinanceSettingModel.TABLE_NAME 
				+ " set pwdStatus=0,financePassword='' where crewId=?";
		this.getJdbcTemplate().update(sql, crewId);
	}
	
	/**
	 * 删除剧组联系表中的剧组用户
	 * @param crewId
	 */
	public void deleteCrewUserContact(String crewId) {
		String sql = "delete from " + CrewContactModel.TABLE_NAME
				+ " where crewId=? and userId in (select userId from "
				+ CrewUserMapModel.TABLE_NAME + " where crewId=?)";
		this.getJdbcTemplate().update(sql, crewId, crewId);
	}
	
	/**
	 * 删除剧组联系表中的剧组用户和系统角色关联表
	 * @param crewId
	 */
	public void deleteCrewUserContactRole(String crewId) {
		String sql = "delete from " + ContactSysroleMapModel.TABLE_NAME
				+ " where crewId=? and contactId in (select contactId from "
				+ CrewContactModel.TABLE_NAME + " as cc,"
				+ CrewUserMapModel.TABLE_NAME
				+ " as cum where cc.userId=cum.userId and cc.crewId=?)";
		this.getJdbcTemplate().update(sql, crewId, crewId);
	}
	
	/**
	 * 删除宾馆信息
	 * @param crewId
	 */
	public void deleteInhotelInfo(String crewId) {
		String sql = "delete from " + HotelInfoModel.TABLE_NAME 
				+ " where crewId=?";
		this.getJdbcTemplate().update(sql, crewId);
	}
	
	/**
	 * 删除用户的入住信息
	 * @param crewId
	 */
	public void deleteCheckInhotelInfo(String crewId) {
		String sql = "delete from " + CheckinHotelInfoModel.TABLE_NAME 
				+ " where crewId=?";
		this.getJdbcTemplate().update(sql, crewId);
	}
	
	/**
	 * 删除剧组实景信息表与场景对照表
	 * @param crewId
	 */
//	public void deleteSceneViewMap(String crewId) {
//		String sql = "delete from " + SceneviewViewinfoMapModel.TABLE_NAME 
//				+ " where sceneviewId in (select id from "
//				+ SceneViewInfoModel.TABLE_NAME + " where crewId=?)";
//		this.getJdbcTemplate().update(sql, crewId);
//	}
	
	/**
	 * 删除附件包,用于堪景
	 * @param crewId
	 */
	public void deleteAttachmentPacketByCrewId(String crewId) {		
		String sql = "delete from " + AttachmentPacketModel.TABLE_NAME
				+ " where crewId=? and id in (select id from "
				+ SceneViewInfoModel.TABLE_NAME + " where crewId=?)";	
		this.getJdbcTemplate().update(sql, crewId, crewId);
	}
	
	/**
	 * 删除附件信息,用于堪景
	 * @param crewId
	 */
	public void deleteAttachmentInfoByCrewId(String crewId) {		
		String sql = "delete from " + AttachmentModel.TABLE_NAME
				+ " where crewId=? and attpackId in (select id from "
				+ SceneViewInfoModel.TABLE_NAME + " where crewId=?)";
		this.getJdbcTemplate().update(sql, crewId, crewId);
	}
	
	/**
	 * 查询附件地址,用于堪景
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryAttachmentInfoByCrewId(String crewId) {	
		String sql = "select hdStorePath,sdStorePath from "
				+ AttachmentModel.TABLE_NAME
				+ " where crewId=? and attpackId in (select id from "
				+ SceneViewInfoModel.TABLE_NAME + " where crewId=?)";
		List<Map<String, Object>> result = this.getJdbcTemplate().queryForList(sql, crewId, crewId);
		return result;
	}
	
	/**
	 * 删除剧组和用户关联关系
	 * 不删除客服
	 * @param crewId
	 */
	public void deleteCrewUserMapByCrewId(String crewId) {
		String sql = "delete from " + CrewUserMapModel.TABLE_NAME 
				+ " where crewId = ? and userId not in (select userId from " + UserInfoModel.TABLE_NAME
				+ " where type>0)";
		this.getJdbcTemplate().update(sql, new Object[]{crewId});
	}
}
