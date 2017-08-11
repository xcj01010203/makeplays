package com.xiaotu.makeplays.notice.dao.clip;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.notice.model.clip.ShootAuditionModel;
import com.xiaotu.makeplays.notice.model.clip.TmpCancelViewInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 临时销场信息
 * @author xuchangjian 2015-11-12上午10:47:55
 */
@Repository
public class TmpCancelViewInfoDao extends BaseDao<TmpCancelViewInfoModel> {

	/**
	 * 根据通告单ID查询指定剧组下的临时销场信息
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	public List<TmpCancelViewInfoModel> queryByCrewNoticeId(String crewId, String noticeId) {
		String sql = "select distinct ttv.* from " + TmpCancelViewInfoModel.TABLE_NAME + " ttv, "+ ShootAuditionModel.TABLE_NAME +" tsi where ttv.crewId=? and ttv.noticeId=? and ttv.seriesNo = tsi.seriesNo and ttv.viewNo = tsi.viewNo and tsi.crewId=? and tsi.noticeId=? order by tsi.sequence";
		return this.query(sql, new Object[] {crewId, noticeId, crewId, noticeId}, TmpCancelViewInfoModel.class, null);
	}
	
	/**
	 * 查询通告单下指定场景的临时销场信息
	 * @param crewId
	 * @param noticeId
	 * @param viewIds
	 * @return
	 */
	public List<TmpCancelViewInfoModel> queryByViewIds(String crewId, String noticeId) {
		String sql = "select * from " + TmpCancelViewInfoModel.TABLE_NAME + " where crewId=? and noticeId=?";
		return this.query(sql, new Object[] {crewId, noticeId}, TmpCancelViewInfoModel.class, null);
	}
	
	/**
	 * 根据多个ID查询临时销场信息
	 * @param ids
	 * @return
	 */
	public List<TmpCancelViewInfoModel> queryByIds(String ids) {
		ids = "'" + ids.replace(",", "','") + "'";
		String sql = "select * from " + TmpCancelViewInfoModel.TABLE_NAME + " where id in(" + ids + ")";
		return this.query(sql, null, TmpCancelViewInfoModel.class, null);
		
	}
	
	/**
	 * 根据多个集-场号查询临时销场信息
	 * @param ids
	 * @return
	 */
	public List<TmpCancelViewInfoModel> queryBySeiresViewNos(String crewId, String noticeId, String seriesViewNos) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from  " + TmpCancelViewInfoModel.TABLE_NAME + " where crewId=? and noticeId=?");
		
		if (!StringUtils.isBlank(seriesViewNos)) {
			sql.append(" and ( ");
			
			String[] seriesViewNosArr = seriesViewNos.split(",");
			
			for (int i = 0; i < seriesViewNosArr.length; i++) {
				String seriesViewNo = seriesViewNosArr[i];
				
				String[] seriesViewNoArr = seriesViewNo.split("-");
				String seriesNo = seriesViewNoArr[0];
				String viewNo = seriesViewNoArr[1];
				
				if (i == 0) {
					sql.append(" (seriesNo= " + seriesNo + " and viewNo= '"+ viewNo +"')");
				} else {
					sql.append(" or (seriesNo= " + seriesNo + " and viewNo= '"+ viewNo +"')");
				}
			}
			
			sql.append(" ) ");
		}
		return this.query(sql.toString(), new Object[] {crewId, noticeId}, TmpCancelViewInfoModel.class, null);
		
	}
	
	/**
	 * 根据ID，删除多条记录
	 * @param ids
	 */
	public void deleteByIds(String ids) {
		ids = "'" + ids.replace(",", "','") + "'";
		String sql = "delete from " + TmpCancelViewInfoModel.TABLE_NAME + " where id in(" + ids + ")";
		this.getJdbcTemplate().update(sql);
	}
	
	/**
	 * 批量做确认销场处理
	 * @param ids
	 */
	public void makeSureByIds(String ids) {
		ids = "'" + ids.replace(",", "','") + "'";
		String sql = "update " + TmpCancelViewInfoModel.TABLE_NAME + " set hasDealed = 1 where id in(" + ids + ")";
		this.getJdbcTemplate().update(sql);
	}
	
	/**
	 * 批量做确认销场处理
	 * @param seriesViewNos 集-场号
	 */
	public void makeSureBySeriesViewNos(String crewId, String noticeId, String seriesViewNos) {
		StringBuilder sql = new StringBuilder();
		sql.append(" update " + TmpCancelViewInfoModel.TABLE_NAME + " set hasDealed = 1 where crewId=? and noticeId=?");
		
		if (!StringUtils.isBlank(seriesViewNos)) {
			sql.append(" and ( ");
			
			String[] seriesViewNosArr = seriesViewNos.split(",");
			
			for (int i = 0; i < seriesViewNosArr.length; i++) {
				String seriesViewNo = seriesViewNosArr[i];
				
				String[] seriesViewNoArr = seriesViewNo.split("-");
				String seriesNo = seriesViewNoArr[0];
				String viewNo = seriesViewNoArr[1];
				
				if (i == 0) {
					sql.append(" (seriesNo= " + seriesNo + " and viewNo= '"+ viewNo +"')");
				} else {
					sql.append(" or (seriesNo= " + seriesNo + " and viewNo= '"+ viewNo +"')");
				}
			}
			
			sql.append(" ) ");
			
			this.getJdbcTemplate().update(sql.toString(), new Object[] {crewId, noticeId});
		}
	}
}
