package com.xiaotu.makeplays.cutview.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.cutview.model.CutViewInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 场景剪辑的dao
 * @author wanrenyi 2017年6月15日下午3:47:57
 */
@Repository
public class CutViewInfoDao extends BaseDao<CutViewInfoModel>{

	/**
	 * 查询出当前场景的剪辑信息
	 * @param crewId
	 * @param viewId
	 * @return
	 * @throws Exception 
	 */
	public CutViewInfoModel queryCutViewInfoById(String id) throws Exception{
		String sql = "SELECT * FROM tab_cut_view_info WHERE id = ?";
		return this.queryForObject(sql, new Object[] {id}, CutViewInfoModel.class);
	}
	
	/**
	 * 根据条件查询剪辑列表数据
	 * @param conditionMap
	 * @return
	 */
	public List<Map<String, Object>> queryCutViewList(String crewId, Map<String, Object> conditionMap, boolean isAll, boolean isASc, Page page){
		StringBuilder sql = new StringBuilder();
		List<Object> param = new ArrayList<Object>();
		sql.append("	SELECT ");
		sql.append("		tvnm.shootStatus,");
		sql.append("		tvnm.viewId,");
		sql.append("		IF(tvnm.shootPage IS null,tvi.pageCount,tvnm.shootPage) shootPage,");
		sql.append("		tni.noticeDate,");
		sql.append("		tni.noticeId,");
		sql.append("		tsg.groupName,");
		sql.append("		tvi.seriesNo,");
		sql.append("		tvi.viewNo,");
		sql.append("		tcvi.id,");
		sql.append("		tcvi.cutDtae,");
		sql.append("		tcvi.cutLength,");
		sql.append("		tcvi.cutstatus,");
		sql.append("		tcvi.remark ");
		sql.append("	FROM ");
		sql.append("		tab_view_notice_map tvnm");
		sql.append("	LEFT JOIN tab_notice_info tni ON tni.noticeId = tvnm.noticeId");
		sql.append("	LEFT JOIN tab_shoot_group tsg ON tsg.groupId = tni.groupId");
		sql.append("	LEFT JOIN tab_view_info tvi ON tvi.viewId = tvnm.viewId");
		sql.append("	AND tvi.crewId = ?");
		param.add(crewId);
		sql.append("	LEFT JOIN tab_cut_view_info tcvi ON tcvi.viewId = tvnm.viewId");
		sql.append("	AND tcvi.noticeId = tvnm.noticeId");
		sql.append("	WHERE");
		sql.append("		tvnm.crewId = ?");
		param.add(crewId);
		sql.append("	AND tvnm.shootStatus IN (1, 2, 4, 5)");
		
		//隐藏以完成剪辑
		if (!isAll) {
			sql.append("	AND (tcvi.cutstatus IS NULL OR tcvi.cutstatus = 2)");
		}
		
		if (conditionMap != null) {
			//拍摄时间查询
			//拍摄开始时间
			String shootStartDate = (String)conditionMap.get("shootStartDate");
			if (StringUtils.isNotBlank(shootStartDate)) {
				sql.append("	AND DATE_FORMAT(tni.noticeDate,'%Y-%m-%d') >= ?");
				param.add(shootStartDate);
			}
			//拍摄结束时间
			String shootEndDate = (String) conditionMap.get("shootEndDate");
			if (StringUtils.isNotBlank(shootEndDate)) {
				sql.append("	AND DATE_FORMAT(tni.noticeDate,'%Y-%m-%d') <= ?");
				param.add(shootEndDate);
			}
			
			//根据集场号查询
			//开始集
			Integer startSeriesNo = (Integer) conditionMap.get("startSeriesNo");
			//开始场
			String startViewNo = (String) conditionMap.get("startViewNo");
			//结束集
			Integer endSeriesNo = (Integer) conditionMap.get("endSeriesNo");
			//结束场
			String endViewNo = (String) conditionMap.get("endViewNo");
			//开始和结束集场，全部填写时执行
			if(null != startSeriesNo&& !StringUtils.isBlank(startViewNo)
					&& null != endSeriesNo&& !StringUtils.isBlank(endViewNo)){
				
				if(startSeriesNo == endSeriesNo){
					//当开始结束集相同时
					sql.append("	and (tvi.seriesNo = ? and abs(tvi.viewNo) >=abs(?) and abs(tvi.viewNo) <=abs(?)) ");
					param.add(startSeriesNo);
					param.add(startViewNo);
					param.add(endViewNo);
				}else{
					sql.append(" and ((tvi.seriesNo > ? and tvi.seriesNo<?) or (tvi.seriesNo=? and abs(tvi.viewNo) >=abs(?)) or (tvi.seriesNo=? and abs(tvi.viewNo) <=abs(?))) ");
					
					param.add(startSeriesNo);
					param.add(endSeriesNo);
					param.add(startSeriesNo);
					param.add(startViewNo);
					param.add(endSeriesNo);
					param.add(endViewNo);
				}
				
			}else if(null != startSeriesNo&& !StringUtils.isBlank(startViewNo)
					&& null != endSeriesNo&& StringUtils.isBlank(endViewNo)){
				//只有结束场次为空时
				sql.append("	and ((tvi.seriesNo > ? and tvi.seriesNo<=?) or (tvi.seriesNo=? and abs(tvi.viewNo) >=abs(?)) ) ");
				param.add(startSeriesNo);
				param.add(endSeriesNo);
				param.add(startSeriesNo);
				param.add(startViewNo);
			}else if(null != startSeriesNo&& StringUtils.isBlank(startViewNo)
					&& null != endSeriesNo&& !StringUtils.isBlank(endViewNo)){
				//只有开始场次为空时
				sql.append("	and ((tvi.seriesNo >= ? and tvi.seriesNo<?) or (tvi.seriesNo=? and abs(tvi.viewNo) <=abs(?)) ) ");
				param.add(startSeriesNo);
				param.add(endSeriesNo);
				param.add(endSeriesNo);
				param.add(endViewNo);
			}else if(null != startSeriesNo&& StringUtils.isBlank(startViewNo)
					&& null != endSeriesNo&& StringUtils.isBlank(endViewNo)){
				//只输入开始结束集
				sql.append(" and ((tvi.seriesNo between ? and ?) ) ");
				param.add(startSeriesNo);
				param.add(endSeriesNo);
			}else if(null != startSeriesNo&& !StringUtils.isBlank(startViewNo)
					&& null == endSeriesNo&& StringUtils.isBlank(endViewNo)){
				//只填写开始集和场
				sql.append(" and ( (tvi.seriesNo = ? and tvi.viewNo = ? )) ");
				param.add(startSeriesNo);
				param.add(startViewNo);
			}else if(null == startSeriesNo&& StringUtils.isBlank(startViewNo)
					&& null != endSeriesNo&& !StringUtils.isBlank(endViewNo)){
				//只填写结束集和场
				sql.append(" and ((tvi.seriesNo = ? and abs(tvi.viewNo )<= abs(?)) or tvi.seriesNo < ? ) ");
				param.add(endSeriesNo);
				param.add(endViewNo);
				param.add(endSeriesNo);
			}else if(null != startSeriesNo&& StringUtils.isBlank(startViewNo)
					&& null == endSeriesNo&& StringUtils.isBlank(endViewNo)){
				//只填写开始集
				sql.append(" and (tvi.seriesNo >= ? ) ");
				param.add(startSeriesNo);
			}else if(null == startSeriesNo&& StringUtils.isBlank(startViewNo)
					&& null != endSeriesNo&& StringUtils.isBlank(endViewNo)){
				//只填写结束集
				sql.append(" and (tvi.seriesNo <= ? ) ");
				param.add(endSeriesNo);
			}
			
			//拍摄页数
			Double satrtShootPage = (Double) conditionMap.get("satrtShootPage"); 
			Double endShootPage = (Double) conditionMap.get("endShootPage"); 
			if (satrtShootPage != null) {
				sql.append("	AND IF(tvnm.shootPage IS null,tvi.pageCount,tvnm.shootPage) >= ?");
				param.add(satrtShootPage);
			}
			if (endShootPage != null) {
				sql.append("	AND IF(tvnm.shootPage IS null,tvi.pageCount,tvnm.shootPage) <= ?");
				param.add(endShootPage);
			}
			
			//剪辑时长
			Long startCutLength = (Long) conditionMap.get("startCutLength");
			Long endCutLength = (Long) conditionMap.get("endCutLength");
			if (startCutLength != null) {
				sql.append("	AND tcvi.cutLength >= ?");
				param.add(startCutLength);
			}
			if (endCutLength != null) {
				sql.append("	AND tcvi.cutLength <= ?");
				param.add(endCutLength);
			}
			
			//剪辑日期
			String startCutDate = (String) conditionMap.get("startCutDate");
			String endCutDate = (String) conditionMap.get("endCutDate");
			if (StringUtils.isNotBlank(startCutDate)) {
				sql.append("	AND DATE_FORMAT(tcvi.cutDtae,'%Y-%m-%d') >= ?");
				param.add(startCutDate);
			}
			if (StringUtils.isNotBlank(endCutDate)) {
				sql.append("	AND DATE_FORMAT(tcvi.cutDtae,'%Y-%m-%d') <= ?");
				param.add(endCutDate);
			}
		}
		
		//排序规则
		if (isASc) { //升序排列
			sql.append("	ORDER BY 	tni.noticeDate,");
		}else {
			//降序排列
			sql.append("	ORDER BY 	tni.noticeDate DESC,");
		}
		
		sql.append("	tsg.groupName,	tvi.seriesNo, 	ABS(tvi.viewNo), 	tvi.viewNo");
		
		return this.query(sql.toString(), param.toArray(), page);
	}
	
	/**
	 * 查询剪辑的统计数据
	 * @param crewId
	 * @param conditionMap
	 * @param isAll
	 * @param isASc
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> queryCutViewStaticInfo(String crewId, Map<String, Object> conditionMap, boolean isAll){
		StringBuilder sql = new StringBuilder();
		List<Object> param = new ArrayList<Object>();
		sql.append("	SELECT ");
		sql.append("		COUNT(tvnm.viewId) totalCount,");
		sql.append("		ROUND(SUM( IF (tvnm.shootPage IS NULL,tvi.pageCount,tvnm.shootPage)),2) totalPage,");
		sql.append("		COUNT(IF (tcvi.cutstatus = 1,tcvi.id,null)) finshCutCount,");
		sql.append("		ROUND(SUM(IF (tcvi.cutstatus = 1 ,IF (tvnm.shootPage IS NULL,tvi.pageCount,tvnm.shootPage),0)),2) finishCutPage,");
		sql.append("		SUM(tcvi.cutLength) totalCutTimes");
		sql.append("	FROM ");
		sql.append("		tab_view_notice_map tvnm");
		sql.append("	LEFT JOIN tab_notice_info tni ON tni.noticeId = tvnm.noticeId");
		sql.append("	LEFT JOIN tab_shoot_group tsg ON tsg.groupId = tni.groupId");
		sql.append("	LEFT JOIN tab_view_info tvi ON tvi.viewId = tvnm.viewId");
		sql.append("	AND tvi.crewId = ?");
		param.add(crewId);
		sql.append("	LEFT JOIN tab_cut_view_info tcvi ON tcvi.viewId = tvnm.viewId");
		sql.append("	AND tcvi.noticeId = tvnm.noticeId ");
		sql.append("	WHERE");
		sql.append("		tvnm.crewId = ?");
		param.add(crewId);
		sql.append("	AND tvnm.shootStatus IN (1, 2, 4, 5)");
		
		
		if (conditionMap != null) {
			//拍摄时间查询
			//拍摄开始时间
			String shootStartDate = (String)conditionMap.get("shootStartDate");
			if (StringUtils.isNotBlank(shootStartDate)) {
				sql.append("	AND DATE_FORMAT(tni.noticeDate,'%Y-%m-%d') >= ?");
				param.add(shootStartDate);
			}
			//拍摄结束时间
			String shootEndDate = (String) conditionMap.get("shootEndDate");
			if (StringUtils.isNotBlank(shootEndDate)) {
				sql.append("	AND DATE_FORMAT(tni.noticeDate,'%Y-%m-%d') <= ?");
				param.add(shootEndDate);
			}
			
			//根据集场号查询
			//开始集
			Integer startSeriesNo = (Integer) conditionMap.get("startSeriesNo");
			//开始场
			String startViewNo = (String) conditionMap.get("startViewNo");
			//结束集
			Integer endSeriesNo = (Integer) conditionMap.get("endSeriesNo");
			//结束场
			String endViewNo = (String) conditionMap.get("endViewNo");
			//开始和结束集场，全部填写时执行
			if(null != startSeriesNo&& !StringUtils.isBlank(startViewNo)
					&& null != endSeriesNo&& !StringUtils.isBlank(endViewNo)){
				
				if(startSeriesNo == endSeriesNo){
					//当开始结束集相同时
					sql.append("	and (tvi.seriesNo = ? and abs(tvi.viewNo) >=abs(?) and abs(tvi.viewNo) <=abs(?)) ");
					param.add(startSeriesNo);
					param.add(startViewNo);
					param.add(endViewNo);
				}else{
					sql.append(" and ((tvi.seriesNo > ? and tvi.seriesNo<?) or (tvi.seriesNo=? and abs(tvi.viewNo) >=abs(?)) or (tvi.seriesNo=? and abs(tvi.viewNo) <=abs(?))) ");
					
					param.add(startSeriesNo);
					param.add(endSeriesNo);
					param.add(startSeriesNo);
					param.add(startViewNo);
					param.add(endSeriesNo);
					param.add(endViewNo);
				}
				
			}else if(null != startSeriesNo&& !StringUtils.isBlank(startViewNo)
					&& null != endSeriesNo&& StringUtils.isBlank(endViewNo)){
				//只有结束场次为空时
				sql.append("	and ((tvi.seriesNo > ? and tvi.seriesNo<=?) or (tvi.seriesNo=? and abs(tvi.viewNo) >=abs(?)) ) ");
				param.add(startSeriesNo);
				param.add(endSeriesNo);
				param.add(startSeriesNo);
				param.add(startViewNo);
			}else if(null != startSeriesNo&& StringUtils.isBlank(startViewNo)
					&& null != endSeriesNo&& !StringUtils.isBlank(endViewNo)){
				//只有开始场次为空时
				sql.append("	and ((tvi.seriesNo >= ? and tvi.seriesNo<?) or (tvi.seriesNo=? and abs(tvi.viewNo) <=abs(?)) ) ");
				param.add(startSeriesNo);
				param.add(endSeriesNo);
				param.add(endSeriesNo);
				param.add(endViewNo);
			}else if(null != startSeriesNo&& StringUtils.isBlank(startViewNo)
					&& null != endSeriesNo&& StringUtils.isBlank(endViewNo)){
				//只输入开始结束集
				sql.append(" and ((tvi.seriesNo between ? and ?) ) ");
				param.add(startSeriesNo);
				param.add(endSeriesNo);
			}else if(null != startSeriesNo&& !StringUtils.isBlank(startViewNo)
					&& null == endSeriesNo&& StringUtils.isBlank(endViewNo)){
				//只填写开始集和场
				sql.append(" and ( (tvi.seriesNo = ? and abs(tvi.viewNo )>= abs(?))) ");
				param.add(startSeriesNo);
				param.add(startViewNo);
			}else if(null == startSeriesNo&& StringUtils.isBlank(startViewNo)
					&& null != endSeriesNo&& !StringUtils.isBlank(endViewNo)){
				//只填写结束集和场
				sql.append(" and ((tvi.seriesNo = ? and abs(tvi.viewNo )<= abs(?)) or tvi.seriesNo < ? ) ");
				param.add(endSeriesNo);
				param.add(endViewNo);
				param.add(endSeriesNo);
			}else if(null != startSeriesNo&& StringUtils.isBlank(startViewNo)
					&& null == endSeriesNo&& StringUtils.isBlank(endViewNo)){
				//只填写开始集
				sql.append(" and (tvi.seriesNo >= ? ) ");
				param.add(startSeriesNo);
			}else if(null == startSeriesNo&& StringUtils.isBlank(startViewNo)
					&& null != endSeriesNo&& StringUtils.isBlank(endViewNo)){
				//只填写结束集
				sql.append(" and (tvi.seriesNo <= ? ) ");
				param.add(endSeriesNo);
			}
			
			//拍摄页数
			Double satrtShootPage = (Double) conditionMap.get("satrtShootPage"); 
			Double endShootPage = (Double) conditionMap.get("endShootPage"); 
			if (satrtShootPage != null) {
				sql.append("	AND IF(tvnm.shootPage IS null,tvi.pageCount,tvnm.shootPage) >= ?");
				param.add(satrtShootPage);
			}
			if (endShootPage != null) {
				sql.append("	AND IF(tvnm.shootPage IS null,tvi.pageCount,tvnm.shootPage) <= ?");
				param.add(endShootPage);
			}
			
			//剪辑时长
			Long startCutLength = (Long) conditionMap.get("startCutLength");
			Long endCutLength = (Long) conditionMap.get("endCutLength");
			if (startCutLength != null) {
				sql.append("	AND tcvi.cutLength >= ?");
				param.add(startCutLength);
			}
			if (endCutLength != null) {
				sql.append("	AND tcvi.cutLength <= ?");
				param.add(endCutLength);
			}
			
			//剪辑日期
			String startCutDate = (String) conditionMap.get("startCutDate");
			String endCutDate = (String) conditionMap.get("endCutDate");
			if (StringUtils.isNotBlank(startCutDate)) {
				sql.append("	AND DATE_FORMAT(tcvi.cutDtae,'%Y-%m-%d') >= ?");
				param.add(startCutDate);
			}
			if (StringUtils.isNotBlank(endCutDate)) {
				sql.append("	AND DATE_FORMAT(tcvi.cutDtae,'%Y-%m-%d') <= ?");
				param.add(endCutDate);
			}
		}
		
		return this.query(sql.toString(), param.toArray(), null);
	}
	
	/**
	 * 查询剪辑进度统计数据 
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryCutViewStatisticList(String crewId){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT");
		sql.append(" cut.shootDate,sum(cut.planViewCount) planViewCount,SUM(cut.realViewCount) realViewCount,SUM(cut.planPageCount) planPageCount,");
		sql.append(" SUM(cut.realPageCount) realPageCount,SUM(cut.realCutPage) realCutPage,SUM(cut.cutMinutes) cutMinutes");
		sql.append(" FROM");
		sql.append("	(SELECT ");
		sql.append("		tni.noticeDate shootDate,");
		sql.append("		count(tvi.viewId) planViewCount,");
		sql.append("		ifnull(sum(IF (tvnm.shootStatus IN (1,2, 4, 5),1,0)),0) realViewCount,");
		sql.append("		round(ifnull(	sum(IF (tvnm.shootPage IS NULL,tvi.pageCount,	tvnm.shootPage)),0),2) planPageCount,");
		sql.append("		round(ifnull(sum(IF (tvnm.shootStatus IN (1,2, 4, 5),IF (tvnm.shootPage IS NULL,tvi.pageCount,tvnm.shootPage),0)),0),2) realPageCount,  0 realCutPage,  0 cutMinutes");
		sql.append("	FROM");
		sql.append("		tab_notice_info tni");
		sql.append("	LEFT JOIN tab_view_notice_map tvnm ON tvnm.noticeId = tni.noticeId");
		sql.append("	LEFT JOIN tab_view_info tvi ON tvnm.viewId = tvi.viewId");
		sql.append("	WHERE");
		sql.append("		tni.crewId =?");
		sql.append("		and tvnm.shootStatus IN (1,2, 4, 5)");
		sql.append("	GROUP BY");
		sql.append("		tni.noticeDate ");
		sql.append("UNION all ");
		sql.append("	SELECT");
		sql.append("		tcvi.cutDtae shootDate,0 planViewCount, 0 realViewCount, 0 planPageCount, 0 realPageCount,");
		sql.append("		round(ifnull(sum(	IF (	tcvi.cutstatus = 1,	IF (	tvnm.shootPage IS NULL,	tvi.pageCount,	tvnm.shootPage	),	0	)	),	0	),2) realCutPage,");
		sql.append("		round(sum(tcvi.cutLength), 2) cutMinutes");
		sql.append("	FROM");
		sql.append("		tab_cut_view_info tcvi");
		sql.append("	LEFT JOIN tab_view_notice_map tvnm ON tvnm.noticeId = tcvi.noticeId AND tcvi.viewId = tvnm.viewId");
		sql.append("	LEFT JOIN tab_view_info tvi ON tvnm.viewId = tvi.viewId");
		sql.append("	LEFT JOIN tab_notice_info tni ON tvnm.noticeId = tni.noticeId");
		sql.append("	WHERE");
		sql.append("		tcvi.crewId =?");
		sql.append("	GROUP BY	tcvi.cutDtae ) cut");
		sql.append(" GROUP BY	cut.shootDate");
		sql.append("	ORDER BY shootDate");
		return this.query(sql.toString(), new Object[]{crewId,crewId}, null);
	}
	
	/**
	 * 查询剪辑进度统计数据 
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryPreDayCutStatistic(String crewId){
		StringBuffer sql = new StringBuffer();
		sql.append(" select 	tcvi.cutDtae noticeDate,");
		sql.append("	round(sum(tcvi.cutLength),2) cutMinutes");
		sql.append(" FROM 	tab_cut_view_info tcvi ");
		sql.append(" where tcvi.crewId=? ");
		sql.append(" group by tcvi.cutDtae ");
		sql.append(" order by tcvi.cutDtae ");
		return this.query(sql.toString(), new Object[]{crewId}, null);
	}
	
	/**
	 * 查询剪辑的总天数
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryTotalCutDays(String crewId){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT COUNT(DISTINCT tcvi.cutDtae) cutDays");
		sql.append(" FROM");
		sql.append("	tab_cut_view_info tcvi");
		sql.append(" WHERE ");
		sql.append("	tcvi.crewId = ?");
		sql.append("	AND	(tcvi.cutLength IS NOT NULL OR tcvi.cutLength != 0 )");
		return this.query(sql.toString(), new Object[]{crewId}, null);
	}
	
	/**
	 *查询分集剪辑时长
	 * @param crewId
	 * @param isMovice
	 * @return
	 */
	public List<Map<String, Object>> queryPreSeriesNoCutInfo(String crewId){
		StringBuffer sql = new StringBuffer();
		sql.append("	SELECT tvi.seriesNo, ROUND(SUM(IF(tcvi.cutLength IS NULL,0,tcvi.cutLength))/60,2) toatlCutLength");
		sql.append("	FROM tab_view_info tvi");
		sql.append("	LEFT JOIN tab_view_notice_map tvnm ON tvi.viewId = tvnm.viewId AND tvnm.crewId = ?");
		sql.append("	LEFT JOIN tab_cut_view_info tcvi ON tcvi.viewId = tvnm.viewId AND tcvi.noticeId = tvnm.noticeId");
		sql.append("	WHERE ");
		sql.append("		tvi.crewId = ?");
		sql.append("	GROUP BY	tvi.seriesNo");
		
		return this.query(sql.toString(), new Object[] {crewId, crewId}, null);
	}
	
	/**
	 * 更新剪辑状态
	 * @param id
	 * @param cutStatus
	 */
	public void updateCutViewStatus(String id, boolean cutStatus) {
		String sql = "UPDATE tab_cut_view_info SET cutstatus = ? WHERE id = ?";
		List<Object> param = new ArrayList<Object>();
		if (cutStatus) { //剪辑已完成
			param.add(1);
		}else {
			param.add(2);
		}
		param.add(id);
		
		this.getJdbcTemplate().update(sql, param.toArray());
	}
	
	/**
	 * 根据剧组id删除剧组的剪辑信息
	 * @param crewId
	 */
	public void deleteCutViewByCrewId(String crewId) {
		String sql = " DELETE FROM tab_cut_view_info WHERE crewId = ?";
		this.getJdbcTemplate().update(sql, crewId);
	}
	
	/**
	 * 根据通告单或则场景id查询出剪辑信息
	 * @param noticeId
	 * @param viewId
	 * @return
	 */
	public List<Map<String, Object>> queryCutInfoByNoticeIdOrViewId(String noticeId, String viewId, String crewId){
		StringBuilder sql = new StringBuilder();
		List<String> param = new ArrayList<String>();
		sql.append(" SELECT * FROM tab_cut_view_info WHERE crewId = ? ");
		param.add(crewId);
		if (StringUtils.isNotBlank(viewId)) {
			sql.append("	AND viewId = ?");
			param.add(viewId);
		}
		if (StringUtils.isNotBlank(noticeId)) {
			sql.append(" AND noticeId = ?");
			param.add(noticeId);
		}
		
		return this.query(sql.toString(), param.toArray(), null);
	}
	
	/**
	 * 根据通告单id删除剪辑信息
	 * @param noticeId
	 */
	public void deleteCutViewInfoByNoticeId(String noticeId) {
		String sql = " DELETE FROM tab_cut_view_info WHERE noticeId = ?";
		this.getJdbcTemplate().update(sql, noticeId);
	}
	
	/**
	 * 根据场景id删除场景的剪辑信息
	 * @param viewId
	 */
	public void deleteCutViewInfoByViewId(String viewId, String noticeId) {
		String sql = " DELETE FROM tab_cut_view_info WHERE viewId = ? And noticeId = ?";
		this.getJdbcTemplate().update(sql, viewId, noticeId);
	}
}
