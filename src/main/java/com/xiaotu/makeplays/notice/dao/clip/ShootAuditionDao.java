package com.xiaotu.makeplays.notice.dao.clip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.notice.model.clip.CameraInfoModel;
import com.xiaotu.makeplays.notice.model.clip.ShootAuditionModel;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 场记单中拍摄镜次
 * @author xuchangjian 2015-11-9下午3:36:37
 */
@Repository
public class ShootAuditionDao extends BaseDao<ShootAuditionModel> {

	/**
	 * 根据通告单ID查询通告单下的镜次列表
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	public List<Map<String, Object>> queryByNoticeId(String crewId, String noticeId, String userId) {
		List<Object> paramsList = new ArrayList<Object>();
		
		String sql = "select tsi.*, tci.cameraId, tci.cameraName, tci.createTime cCreateTime, tui.realName from " + ShootAuditionModel.TABLE_NAME + " tsi, "+ CameraInfoModel.TABLE_NAME +" tci, "+ UserInfoModel.TABLE_NAME +" tui where tsi.crewId = ? and tsi.noticeId = ? and tsi.cameraId = tci.cameraId and tsi.userId = tui.userId ";
		paramsList.add(crewId);
		paramsList.add(noticeId);
		
		if (!StringUtils.isBlank(userId)) {
			sql += "and tsi.userId=? ";
			paramsList.add(userId);
		}
		sql += " order by tsi.sequence ";
		return this.query(sql, paramsList.toArray(), null);
	}
	
	/**
	 * 查询剧组下所有的镜次信息
	 * 带有气氛、内外、临时销场信息
	 * @param crewId
	 * @param noticeId
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> queryAudiInfoWithTmpCancel(String crewId, String noticeId, String userId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tsi.*, tci.cameraId, ");
		sql.append(" 	tci.cameraName, ");
		sql.append(" 	tci.createTime cCreateTime, ");
		sql.append(" 	tai.atmosphereName, ");
		sql.append(" 	tvi.site, ");
		sql.append(" 	tti.finishDate, ");
		sql.append(" 	tti.shootStatus, ");
		sql.append(" 	tti.tapNo, ");
		sql.append(" 	tti.remark ");
		sql.append(" FROM ");
		sql.append(" 	tab_camera_info tci, ");
		sql.append(" 	tab_shootAudition_info tsi ");
		sql.append(" 	LEFT JOIN tab_view_info tvi on tsi.seriesNo = tvi.seriesNo and tsi.viewNo = tvi.viewNo and tvi.crewId=? ");
		sql.append(" 	LEFT JOIN tab_atmosphere_info tai ON tai.atmosphereId = tvi.atmosphereId and tvi.crewId=? ");
		sql.append(" 	LEFT JOIN tab_tmpCancelView_info tti ON tti.seriesNo = tsi.seriesNo and tti.viewNo = tsi.viewNo and tti.crewId=? ");
		sql.append(" WHERE ");
		sql.append(" 	tsi.crewId = ? ");
		sql.append(" AND tsi.noticeId = ? ");
		sql.append(" AND tsi.cameraId = tci.cameraId ");
		sql.append(" and (tci.crewId = 0 or tci.crewId = ?) ");
		if(StringUtils.isNotBlank(userId)){
			sql.append(" AND tsi.userId ='"+userId+"'");
		}
		
		sql.append(" ORDER BY ");
		sql.append(" 	tci.cameraName, tsi.sequence ");
		
		return this.query(sql.toString(), new Object[] {crewId, crewId, crewId, crewId, noticeId, crewId}, null);
	}
	
	/**
	 * 根据通告单ID删除镜次信息
	 * @param crewId
	 * @param noticeId	通告单ID
	 * @param cameraNameList	机位名称列表
	 */
	public void deleteByNoticeId(String crewId, String noticeId, List<String> cameraNameList) {
		StringBuilder querySql = new StringBuilder();
		querySql.append(" select * from tab_camera_info where cameraName in ( ");
		
		for (int i = 0; i < cameraNameList.size(); i++) {
			String cameraName = cameraNameList.get(i);
			if (i == 0) {
				querySql.append("'" + cameraName + "'");
			} else {
				querySql.append(",'" + cameraName + "'");
			}
		}
		querySql.append(" ) ");
		
		RowMapper<CameraInfoModel> rm = ParameterizedBeanPropertyRowMapper.newInstance(CameraInfoModel.class);
		List<CameraInfoModel> cameraInfoList = this.getJdbcTemplate().query(querySql.toString(), rm);
		
		
		if (cameraInfoList != null && cameraInfoList.size() > 0) {
			String cameraIds = "";
			for (int i = 0; i < cameraInfoList.size(); i++) {
				CameraInfoModel cameraInfo = cameraInfoList.get(i);
				String cameraId = cameraInfo.getCameraId();
				
				if (i == 0) {
					cameraIds += cameraId;
				} else {
					cameraIds += "," + cameraId;
				}
			}
			
			cameraIds = "'" + cameraIds.replace(",", "','") + "'";
			
			String deleteSql = "delete from " + ShootAuditionModel.TABLE_NAME + " where crewId=? and noticeId=? and cameraId in (" + cameraIds + ")";
			this.getJdbcTemplate().update(deleteSql, new Object[] {crewId, noticeId});
		}
	}
}
