package com.xiaotu.makeplays.notice.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.notice.model.ViewNoticeMapModel;
import com.xiaotu.makeplays.utils.BaseDao;

@Repository
public class ViewNoticeMapDao extends BaseDao<ViewNoticeMapModel> {

	/**
	 * 删除场景和通告单关联关系
	 * @param noticeId
	 * @param crewId
	 * @param viewId
	 */
	public void deleteViewMoticeMap(String noticeId,String crewId,String viewId){
		String sql = "delete from "+ViewNoticeMapModel.TABLE_NAME+" where noticeId=? and crewId=? and viewId=?";
		this.getJdbcTemplate().update(sql, noticeId,crewId,viewId);
	}
	
	/**
	 * 删除场景和通告单关联关系
	 * 该方法会删除通告单下的所有和场景的关联关系
	 * @param noticeId
	 * @param crewId
	 * @param viewId
	 */
	public void deleteViewMoticeMap(String noticeId,String crewId){
		String sql = "delete from "+ViewNoticeMapModel.TABLE_NAME+" where noticeId=? and crewId=?";
		this.getJdbcTemplate().update(sql, noticeId,crewId);
	}
	
	
	/**
	 * 更新通告单内场景状态
	 * @param noticeId
	 * @param crewId
	 * @param viewId
	 */
	public void updateViewMoticeMapStatus(ViewNoticeMapModel viewNoticeMap, String viewIds){
		
		String updateSql = "update "+ViewNoticeMapModel.TABLE_NAME+" set shootStatus=? ";
		List<Object> params = new ArrayList<Object>();
		params.add(viewNoticeMap.getShootStatus());
		
		if(StringUtils.isNotBlank(viewNoticeMap.getStatusRemark())){
			updateSql+=" ,statusRemark=? ";
			params.add(viewNoticeMap.getStatusRemark());
		}
		if(StringUtils.isNotBlank(viewNoticeMap.getTapNo())){
			updateSql+=" ,tapNo=? ";
			params.add(viewNoticeMap.getTapNo());
		}
		
		updateSql+=" where noticeId=? and viewId in ('"+viewIds.replaceAll(",", "','")+"')";
		params.add(viewNoticeMap.getNoticeId());
		this.getJdbcTemplate().update(updateSql, params.toArray());
		
	}
	
	
	/**
	 * 查询通告单下状态为空的场景个数
	 * @param noticeId
	 * @return
	 */
	public int queryNoticeViewStatusCount(String noticeId){
		
		String sql = "select count(mapId) from "+ViewNoticeMapModel.TABLE_NAME+" where (shootStatus is null ) and noticeId=? ";
		
		return this.getJdbcTemplate().queryForInt(sql,new Object[]{noticeId});
	}
	
	/**
	 * 更新通告单内场景顺序
	 * @param noticeId
	 * @param viewId
	 * @param sequence
	 */
	public void updateNoticeViewSequence(String noticeId,String viewId,int sequence){
		
		String updateSql = "update "+ViewNoticeMapModel.TABLE_NAME+" set sequence=? ";
		updateSql+=" where noticeId=? and viewId=?";
		this.getJdbcTemplate().update(updateSql, new Object[]{sequence,noticeId,viewId});
		
	}
	
	
	/**
	 * 获取通告单下的场景表最大序号
	 * @param noticeId
	 * @param crewId
	 * @return
	 */
	public int getNoticeViewLastSequence(String noticeId,String crewId){
		
		String sql = "select max(sequence) from tab_view_notice_map where noticeId=? and crewId=?";
		
		return this.getJdbcTemplate().queryForInt(sql,noticeId,crewId);
		
	}
	
	/**
	 * 根据通告单ID查询通告单下的场景信息
	 * @param crewId
	 * @param viewIds
	 * @return
	 */
	public List<ViewNoticeMapModel> queryViewListByNoticeIds (String crewId, String noticeIds) {
		noticeIds = "'" + noticeIds.replace(",", "','") + "'";
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	* ");
		sql.append(" FROM ");
		sql.append(" 	tab_view_notice_map tvnm ");
		sql.append(" WHERE ");
		sql.append("    tvnm.crewId=? ");
		sql.append("    and tvnm.noticeId in ("+ noticeIds +") ");
		
		return this.query(sql.toString(), new Object[] {crewId}, ViewNoticeMapModel.class, null);
	}
	
	/**
	 * 根据通告单ID查询通告单下的场景信息
	 * @param crewId
	 * @param viewIds
	 * @return
	 */
	public List<Map<String, Object>> queryViewListByNoticeId (String crewId, String noticeId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tvi.seriesNo, tvi.viewNo, tvnm.* ");
		sql.append(" FROM ");
		sql.append(" 	tab_view_notice_map tvnm, ");
		sql.append(" 	tab_view_info tvi ");
		sql.append(" WHERE ");
		sql.append("    tvnm.crewId=? ");
		sql.append("    and tvnm.noticeId=? ");
		sql.append("    and tvnm.viewId = tvi.viewId ");
		
		return this.query(sql.toString(), new Object[] {crewId, noticeId}, null);
	}
	
	/**
	 * 通告单销场时更新场景和通告单关联关系中的销场信息
	 * @param crewId	剧组ID
	 * @param noticeId	通告单ID
	 * @param viewId	场景ID
	 * @param shootStatus	拍摄状态
	 * @param statusRemark	拍摄状态备注
	 * @param tapNo	带号
	 */
	public void cancelView(String crewId, String noticeId, String viewId, int shootStatus, String statusRemark, String tapNo) {
		String sql = "update " + ViewNoticeMapModel.TABLE_NAME + " set shootStatus=?, statusRemark=?, tapNo=? where crewId=? and noticeId=? and viewId=?";
		this.getJdbcTemplate().update(sql, new Object[] {shootStatus, statusRemark, tapNo, crewId, noticeId, viewId});
	}
	
	/**
	 * 查询关系表中的场景的信息
	 * @param noticeId
	 * @param viewId
	 * @return
	 * @throws Exception 
	 */
	public ViewNoticeMapModel queryViewMapInfo(String noticeId, String viewId) throws Exception{
		String sql = "select * from "+ ViewNoticeMapModel.TABLE_NAME +" where noticeId = ? and viewId = ?";
		return this.queryForObject(sql, new Object[] {noticeId, viewId}, ViewNoticeMapModel.class);
	}
	
	/**
	 * 根据场景id字符串，查询出场景与通告单之间的关系
	 * @param viewIds
	 * @return
	 */
	public List<Map<String, Object>> queryViewNoticeMapInfoByViewIds(String viewIds){
		viewIds = "'" + viewIds.replace(",", "','") + "'";
		StringBuffer sql = new StringBuffer();
		sql.append("	SELECT");
		sql.append("		tvi.shotDate,tvi.remark,tvi.tapNo,tvnm.*");
		sql.append("	FROM");
		sql.append("		tab_view_notice_map tvnm");
		sql.append("	LEFT JOIN	tab_view_info tvi ON tvi.viewId = tvnm.viewId");
		sql.append("	WHERE");
		sql.append("		tvnm.viewId IN ("+ viewIds +")");
		return this.query(sql.toString(), null, null);
	}
}
