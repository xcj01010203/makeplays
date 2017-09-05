package com.xiaotu.makeplays.notice.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.notice.model.NoticeInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.StringUtil;

/**
 * 通告单相关的dao操作
 * @author xuchangjian 2016年8月5日上午10:16:09
 */
@Repository
public class NoticeInfoDao extends BaseDao<NoticeInfoModel> {

	/**
	 * 根据场景id查询出通告单的信息
	 * @param viewIds
	 * @return
	 */
	public List<Map<String, Object>> queryNoticeView(String viewIds, String noticeId){
		
		String sql = "select tni.noticeDate,tni.noticeName,tsi.viewNo,tsi.seriesNo,tni.noticeid,tsnm.shootStatus from tab_notice_info tni,tab_view_notice_map tsnm,tab_view_info tsi where "
				+ " tni.noticeid=tsnm.noticeid and tsi.viewId=tsnm.viewId AND (tsnm.shootStatus IS NULL OR tsnm.noticeId = ? OR tsnm.shootStatus =3) and tsnm.viewId in ('"+viewIds.replaceAll(",", "','")+"')";
		return this.query(sql, new Object[] {noticeId}, null);
	}
	
	
	/**
	 * 根据剧组id及分组id查询通告单列表
	 * @param crewId
	 * @param groupId
	 * @param page
	 * @param forSimple
	 * @return
	 * @throws ParseException 
	 */
	public List<Map<String, Object>> queryNoticeByCrewId(String crewId,String groupId, Page page, Boolean forSimple, Map<String, Object> conditionMap) throws ParseException{
		
		List<Object> params = new ArrayList<Object>();
		
		params.add(crewId);
		params.add(crewId);
		params.add(crewId);
		
		StringBuffer sql =new StringBuffer( "SELECT res.noticeId,	res.noticeName,	res.noticeDate,	res.published,	res.publishTime,");
				sql.append(" GROUP_CONCAT(DISTINCT res.shootLocation) shootLocation,res.groupName,sum(IF(res.shootPage is NOT NULL,res.shootPage,res.pageCount)) sumPage,count(res.viewId) viewCount,");
				sql.append(" GROUP_CONCAT(DISTINCT res.mainrole) mainrole,res.canceledStatus,res.groupDirector,res.finishCount,res.finishPage");
				sql.append(" FROM ( SELECT res1.noticeId,res1.noticeName,res1.noticeDate,res1.published,res1.publishTime,");
				sql.append(" res1.shootLocation,res1.groupName,res1.createTime,res1.viewId,res1.pageCount,GROUP_CONCAT(DISTINCT res1.mainrole) AS mainrole,");
				sql.append(" res1.canceledStatus,res1.groupDirector,res1.finishCount,res1.finishPage, res1.shootPage FROM ( SELECT");
				sql.append(" tni.noticeId,tni.noticeName,tni.noticeDate,tni.published,tni.publishTime,tsa.vname shootLocation,");
				sql.append(" tsg.groupName,tsg.createTime,tsi.viewId,tsi.pageCount,maintsr.viewRoleId AS mainroleid,maintsr.viewRoleName AS mainrole,");
				sql.append(" tni.canceledStatus,tnt.groupDirector,tsnm2.finishCount,tsnm2.finishPage, tsnm.shootPage");
				
				sql.append(" FROM tab_notice_info tni");
				sql.append(" left join tab_view_notice_map tsnm on tsnm.noticeId=tni.noticeId");
				sql.append(" left join tab_shoot_group tsg on tsg.groupId=tni.groupId");
				sql.append(" left join tab_view_info tsi on tsi.viewId=tsnm.viewId");
				sql.append(" left join tab_view_role_map tsrm on tsrm.viewId=tsnm.viewId");
				sql.append(" left join (");
				sql.append(" select tsr.viewRoleId,tsr.viewRoleName from tab_view_role tsr where tsr.crewId=? and tsr.viewRoleType=1");
				sql.append(" ) maintsr on maintsr.viewRoleId=tsrm.viewRoleId ");
				
				sql.append(" left join (");
				sql.append(" SELECT tvnm.noticeId,count(tvnm.viewId) finishCount,SUM(tvi2.pageCount) finishPage FROM tab_view_notice_map tvnm LEFT JOIN tab_view_info tvi2 ON tvi2.viewId = tvnm.viewId WHERE tvnm.shootStatus in(2,5) AND tvnm.crewId = ? GROUP BY tvnm.noticeId");
				sql.append(" ) tsnm2 ON tsnm2.noticeId = tni.noticeId");
				sql.append(" left join tab_sceneview_info tsa on tsa.id=tsi.shootLocationId");
				sql.append(" LEFT JOIN tab_notice_time tnt ON tnt.noticeId = tni.noticeId");
				
				sql.append(" where tni.crewId=?");
				if(StringUtils.isNotBlank(groupId)){
					sql.append(" and tsg.groupId=? ");
					params.add(groupId);
				}
				
				if (conditionMap != null && conditionMap.size() != 0) {
					
					//查询已销场的通告单
					String cancledNotice = (String) conditionMap.get("cancledNotice");
					if (StringUtils.isNotBlank(cancledNotice)) {
						sql.append(" AND tni.canceledStatus = 1 ");
					}
					//根据销场状态查询通告单
					Integer canceledStatus = (Integer) conditionMap.get("canceledStatus");
					if(canceledStatus != null) {
						sql.append(" AND tni.canceledStatus = ? ");
						params.add(canceledStatus);
					}
					
					//根据通告月份查询通告单
					String noticeDateMonth = (String) conditionMap.get("noticeDateMonth");
					if (StringUtils.isNotBlank(noticeDateMonth)) {
						sql.append(" AND DATE_FORMAT(tni.noticeDate,'%Y-%m') = DATE_FORMAT(?,'%Y-%m')");
						params.add(noticeDateMonth);
					}
					
					//集场号查询
					String sceriesViewNo = (String) conditionMap.get("sceriesViewNo");
					if (StringUtils.isNotBlank(sceriesViewNo)) {
						if (sceriesViewNo.contains("-")) { //电视剧剧本
							String[] sceriesViewNoArr = sceriesViewNo.split("-");
							sql.append(" AND tsi.seriesNo = ? AND tsi.viewNo = ?");
							params.add(sceriesViewNoArr[0]);
							params.add(sceriesViewNoArr[1]);
						}else { //电影剧本
							sql.append(" AND tsi.viewNo = ?");
							params.add(sceriesViewNo);
						}
					}
					
					//拍摄地点
					String shootLocationStr = (String) conditionMap.get("shootLocationStr");
					if (StringUtils.isNotBlank(shootLocationStr)) {
						String[] shootLocationArr = shootLocationStr.split(",");
						
						sql.append(" and ( ");
						for (int i = 0; i < shootLocationArr.length; i++) {
							String shootLocation = shootLocationArr[i];
							if (i == 0) {
								sql.append(" tsi.shootLocationId = ? ");
								params.add(shootLocation);
							} else {
								sql.append(" or tsi.shootLocationId = ? ");
								params.add(shootLocation);
							}
							
						}
						sql.append(" ) ");
					}
					
					//带号
					String viewTape = (String) conditionMap.get("viewTape");
					if (StringUtils.isNotBlank(viewTape)) {
						sql.append(" AND tsnm.tapNo LIKE ?");
						params.add("%" + viewTape + "%");
					}
					
					//备注
					String remark = (String) conditionMap.get("remarkInfo");
					if (StringUtils.isNotBlank(remark)) {
						sql.append(" AND tsnm.statusRemark LIKE ?");
						params.add("%" + remark + "%");
					}
					
					//通告单日期查询
					String noticeStartDate = (String) conditionMap.get("noticeStartDate");
					String noticeEndDate = (String) conditionMap.get("noticeEndDate");
					if (StringUtils.isNotBlank(noticeStartDate) && StringUtils.isNotBlank(noticeEndDate)) {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						sql.append(" AND DATE_FORMAT(noticeDate,'%Y-%m-%d') BETWEEN DATE_FORMAT(?,'%Y-%m-%d') AND DATE_FORMAT(?,'%Y-%m-%d')");
						params.add(sdf.parse(noticeStartDate));
						params.add(sdf.parse(noticeEndDate));
					}
				}
				sql.append(" GROUP BY tni.noticeId,tni.noticeName,tni.noticeDate, tsa.vname,tsg.groupName,tsg.createTime, tsi.viewId");
				sql.append(" ,tsi.pageCount,maintsr.viewRoleId,maintsr.viewRoleName, tni.canceledStatus");
				sql.append(" ) res1 group by res1.noticeId,res1.noticeName,res1.noticeDate,res1.shootLocation,"
						+ "res1.groupName,res1.createTime,res1.viewId,res1.pageCount,res1.canceledStatus");
				sql.append(" ) res group by res.noticeId,res.noticeName,res.noticeDate,res.groupName,res.canceledStatus order by res.noticeDate DESC,res.createTime");
		
		if (forSimple != null && forSimple) {
			return this.query(sql.toString(),params.toArray(), null);
		} else {
			return this.query(sql.toString(),params.toArray(), page);
		}
	}
	
	
	/**
	 * 查询通告单列表视图的数据
	 * @param crewId
	 * @param groupId
	 * @param page
	 * @param conditionMap
	 * @return
	 */
	public List<Map<String, Object>> queryNoticeListTableData(String crewId,Page page, Map<String, Object> conditionMap){
		List<Object> params = new ArrayList<Object>();
		params.add(crewId);
		params.add(crewId);
		params.add(crewId);
		params.add(crewId);
		params.add(crewId);
		params.add(crewId);
		params.add(crewId);
		
		StringBuffer sql = new StringBuffer();
		sql.append("	SELECT	tni.noticeId,	tni.noticeDate,	tsg.groupName, pc.viewCount, ");
		sql.append(" 	pc.sumPage, ");
		sql.append("	tsnm2.finishCount,	tsnm2.finishPage,");
		sql.append("	GROUP_CONCAT(DISTINCT tsl.vname) shootLocation,");
		sql.append("	GROUP_CONCAT(DISTINCT tvr1.viewRoleName) mainrole,");
		sql.append("	GROUP_CONCAT(DISTINCT tvr2.viewRoleName) guestrole, dtn.days shootDays");
		sql.append("	FROM	tab_notice_info tni");
		sql.append("	LEFT JOIN tab_view_notice_map tvnm on tvnm.noticeId = tni.noticeId AND tvnm.crewId=?");
		sql.append("	LEFT JOIN tab_view_info tvi on tvi.viewId=tvnm.viewId");
		sql.append("	LEFT JOIN tab_sceneview_info tsl on tsl.id = tvi.shootLocationId");
		sql.append("	LEFT JOIN tab_view_role_map tvrm ON tvrm.viewId = tvi.viewId");
		sql.append("	LEFT JOIN tab_view_role tvr1 ON tvr1.viewRoleId = tvrm.viewRoleId AND tvr1.viewRoleType = 1");
		sql.append("	LEFT JOIN (	SELECT	tvnma.noticeId,	count(tvnma.viewId) finishCount,	SUM(IF(tvnma.shootPage IS NULL,tvi2.pageCount,tvnma.shootPage)) finishPage");
		sql.append("	FROM	tab_view_notice_map tvnma");
		sql.append("	LEFT JOIN tab_view_info tvi2 ON tvi2.viewId = tvnma.viewId WHERE tvnma.crewId = ? and tvnma.shootStatus IN (2, 5)");
		sql.append("	GROUP BY 	tvnma.noticeId");
		sql.append("	) tsnm2 ON tsnm2.noticeId = tni.noticeId");
		sql.append("	LEFT JOIN ( ");
		sql.append("		SELECT	noticeDate,	(");
		sql.append("			SELECT	count(DISTINCT noticeDate)");
		sql.append("			FROM	tab_notice_info t2");
		sql.append("				WHERE t2.crewId = ?	AND t2.noticeDate <= t1.noticeDate ) days,t1.noticeId");
		sql.append("		FROM	tab_notice_info t1");
		sql.append("		WHERE crewId = ?	ORDER BY	noticeDate");
		sql.append("	) dtn ON dtn.noticeId = tni.noticeId");
		
		sql.append("	LEFT JOIN (");
		sql.append("		SELECT ");
		sql.append("			sum(IF(tavnm.shootPage is NOT NULL,tavnm.shootPage,tvi1.pageCount)) sumPage, tavnm.noticeId, count(DISTINCT tavnm.viewId) viewCount");
		sql.append("		FROM ");
		sql.append("			tab_view_info tvi1");
		sql.append("	LEFT JOIN tab_view_notice_map tavnm ON tavnm.viewId = tvi1.viewId AND tavnm.crewId = ?");
		sql.append("		WHERE tvi1.crewId = ?");
		sql.append("	GROUP BY ");
		sql.append("		tavnm.noticeId,	tvi1.crewId");
		sql.append("	) pc ON pc.noticeId = tni.noticeId");
		
		sql.append("	LEFT JOIN tab_view_role tvr2 ON tvr2.viewRoleId = tvrm.viewRoleId AND tvr2.viewRoleType = 2");
		sql.append("	,tab_shoot_group tsg");
		sql.append("	WHERE tni.crewId = ?");
		
		if (conditionMap != null && conditionMap.size() != 0) {
			//查询已销场的通告单
			String cancledNotice = (String) conditionMap.get("cancledNotice");
			if (StringUtils.isNotBlank(cancledNotice)) {
				sql.append(" AND tni.canceledStatus = 1");
			}
			
			//集场号查询
			String sceriesViewNo = (String) conditionMap.get("sceriesViewNo");
			if (StringUtils.isNotBlank(sceriesViewNo)) {
				if (sceriesViewNo.contains("-")) { //电视剧剧本
					String[] sceriesViewNoArr = sceriesViewNo.split("-");
					sql.append(" AND tvi.seriesNo = ? AND tvi.viewNo = ?");
					params.add(sceriesViewNoArr[0]);
					params.add(sceriesViewNoArr[1]);
				}else { //电影剧本
					sql.append(" AND tvi.viewNo = ?");
					params.add(sceriesViewNo);
				}
			}
			
			//拍摄地点
			String shootLocationStr = (String) conditionMap.get("shootLocationStr");
			if (StringUtils.isNotBlank(shootLocationStr)) {
				String[] shootLocationArr = shootLocationStr.split(",");
				
				sql.append(" and ( ");
				for (int i = 0; i < shootLocationArr.length; i++) {
					String shootLocation = shootLocationArr[i];
					if (i == 0) {
						sql.append(" tvi.shootLocationId = ? ");
						params.add(shootLocation);
					} else {
						sql.append(" or tvi.shootLocationId = ? ");
						params.add(shootLocation);
					}
					
				}
				sql.append(" ) ");
			}
			
			//带号
			String viewTape = (String) conditionMap.get("viewTape");
			if (StringUtils.isNotBlank(viewTape)) {
				sql.append(" AND tvnm.tapNo LIKE ?");
				params.add("%" + viewTape + "%");
			}
			
			//备注
			String remark = (String) conditionMap.get("remarkInfo");
			if (StringUtils.isNotBlank(remark)) {
				sql.append(" AND tvnm.statusRemark LIKE ?");
				params.add("%" + remark + "%");
			}
			
		}
		sql.append("	AND tni.groupId = tsg.groupId");
		sql.append("	GROUP BY tni.noticeId,tni.noticeDate,tsg.groupName");
		sql.append("	ORDER BY tni.noticeDate DESC");
		
		return this.query(sql.toString(), params.toArray(), page);
	}
	/**
	 * 正序查询通告单未销场列表
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryAscNoticeList(String crewId, Page page){
		List<Object> params = new ArrayList<Object>();
		params.add(crewId);
		params.add(crewId);
		params.add(crewId);
		
		StringBuffer sql =new StringBuffer( "select res.noticeId,res.noticeName,res.noticeDate,res.published,res.publishTime,GROUP_CONCAT(distinct res.shootLocation) shootLocation,res.groupName,");
		sql.append(" sum(IF(res.shootPage is NOT NULL,res.shootPage,res.pageCount)) sumPage,count(res.viewId) viewCount,GROUP_CONCAT(distinct res.mainrole) mainrole,GROUP_CONCAT(distinct res.guestrole) guestrole,");
		sql.append(" res.updateTime,res.canceledStatus,res.version,res.groupDirector,res.finishCount,res.finishPage from (");
		sql.append(" select res1.noticeId,res1.noticeName,res1.noticeDate,res1.published,res1.publishTime,res1.shootLocation,res1.groupName,res1.viewId,res1.pageCount,");
		sql.append(" GROUP_CONCAT(distinct res1.mainrole) as mainrole,GROUP_CONCAT(distinct res1.guestrole) as guestrole");
		sql.append(" ,res1.updateTime,res1.canceledStatus,res1.version,res1.groupDirector,res1.finishCount,res1.finishPage, res1.shootPage from (");
		sql.append(" select tni.noticeId,tni.noticeName,tni.noticeDate,tni.published,tni.publishTime, tsa.vname shootLocation,tsg.groupName, tsi.viewId");
		sql.append(" ,tsi.pageCount,maintsr.viewRoleId as mainroleid,maintsr.viewRoleName as mainrole,");
		sql.append(" guesttsr.viewRoleId as guestroleid,guesttsr.viewRoleName as guestrole,tni.updateTime,tni.canceledStatus,tnt.version,tnt.groupDirector,tsnm2.finishCount,tsnm2.finishPage,tsnm.shootPage");
		sql.append(" from tab_notice_info tni");
		sql.append(" left join tab_view_notice_map tsnm on tsnm.noticeId=tni.noticeId");
		sql.append(" left join tab_shoot_group tsg on tsg.groupId=tni.groupId");
		sql.append(" left join tab_view_info tsi on tsi.viewId=tsnm.viewId");
		sql.append(" left join tab_view_role_map tsrm on tsrm.viewId=tsnm.viewId");
		sql.append(" left join (");
		sql.append(" select tsr.viewRoleId,tsr.viewRoleName from tab_view_role tsr where tsr.crewId=? and tsr.viewRoleType=1");
		sql.append(" ) maintsr on maintsr.viewRoleId=tsrm.viewRoleId ");
		sql.append(" left join (");
		sql.append(" select tsr1.viewRoleId,tsr1.viewRoleName from tab_view_role tsr1 where tsr1.crewId=? and tsr1.viewRoleType=2");
		sql.append(" ) guesttsr on guesttsr.viewRoleId=tsrm.viewRoleId");
		sql.append(" left join (");
		sql.append(" SELECT tvnm.noticeId,count(tvnm.viewId) finishCount,SUM(tvi2.pageCount) finishPage FROM tab_view_notice_map tvnm LEFT JOIN tab_view_info tvi2 ON tvi2.viewId = tvnm.viewId WHERE tvnm.shootStatus in(2,5) GROUP BY tvnm.noticeId");
		sql.append(" ) tsnm2 ON tsnm2.noticeId = tni.noticeId");
		sql.append(" left join tab_sceneview_info tsa on tsa.id =tsi.shootLocationId");
		sql.append(" LEFT JOIN tab_notice_time tnt ON tnt.noticeId = tni.noticeId");
		
		sql.append(" where tni.crewId=?");
		sql.append(" AND tni.canceledStatus = 0");
		
		sql.append(" GROUP BY tni.noticeId,tni.noticeName,tni.noticeDate, tsa.vname,tsg.groupName, tsi.viewId");
		sql.append(" ,tsi.pageCount,maintsr.viewRoleId,maintsr.viewRoleName,");
		sql.append(" guesttsr.viewRoleId,guesttsr.viewRoleName,tni.updateTime,tni.canceledStatus");
		sql.append(" ) res1 group by res1.noticeId,res1.noticeName,res1.noticeDate,res1.shootLocation,"
				+ "res1.groupName,res1.viewId,res1.pageCount,res1.updateTime,res1.canceledStatus");
		sql.append(" ) res group by res.noticeId,res.noticeName,res.noticeDate,res.groupName,res.updateTime,res.canceledStatus order by res.noticeDate");

		return this.query(sql.toString(), params.toArray(), page);
	}
	
	/**
	 * 根据通告单ID查询单条通告单信息
	 * 该方法在查询通告单基本信息的同时，还会查询出场数、页数、拍摄地点等额外信息
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	public Map<String, Object> queryOneFullInfoByNoticeId(String crewId, String noticeId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" tni.*, tsg.groupName, ");
		sql.append(" count(viewobj.viewId) viewCount, ");
		sql.append(" sum(viewobj.pageCount) sumPage, ");
		sql.append(" GROUP_CONCAT(distinct viewobj.shootLocation) shootLocation, ");
		sql.append(" GROUP_CONCAT(viewobj.mainrole) mainrole, ");
		sql.append(" GROUP_CONCAT(viewobj.guestrole) guestrole ");
		sql.append(" FROM tab_notice_info tni ");
		sql.append(" LEFT JOIN tab_shoot_group tsg ON tsg.groupId = tni.groupId  ");
		sql.append(" LEFT JOIN ( ");
		sql.append(" 	SELECT ");
		sql.append(" 		tvi.*, ");
		sql.append(" 		GROUP_CONCAT(DISTINCT ftvr.viewRoleName) mainrole,");
		sql.append(" 		GROUP_CONCAT(DISTINCT stvr.viewRoleName) guestrole,");
		sql.append(" 		tsl.vname shootLocation");
		sql.append(" 	FROM");
		sql.append(" 		tab_view_info tvi");
		sql.append(" 	LEFT JOIN tab_sceneview_info tsl ON tvi.shootLocationId = tsl.id");
		sql.append(" 	LEFT JOIN tab_view_role_map tvrm ON tvrm.viewid = tvi.viewId");
		sql.append(" 	LEFT JOIN tab_view_role ftvr ON ftvr.viewRoleId = tvrm.viewRoleId AND ftvr.viewRoleType = 1");
		sql.append(" 	LEFT JOIN tab_view_role stvr ON stvr.viewRoleId = tvrm.viewRoleId AND stvr.viewRoleType = 2,");
		sql.append(" 	tab_view_notice_map tvnm ");
		sql.append(" 	where tvnm.viewId = tvi.viewId ");
		sql.append(" 	and tvnm.noticeId = ?");
		sql.append(" 	GROUP BY viewId");
		sql.append(" ) viewobj on 1=1");
		sql.append(" WHERE");
		sql.append(" 	tni.crewId = ?");
		sql.append(" AND tni.noticeId = ?");
		sql.append(" group by tni.noticeId");
		sql.append(" ORDER BY");
		sql.append(" 	tni.createTime DESC");
		sql.append("");
		
		return this.getJdbcTemplate().queryForMap(sql.toString(), new Object[] {noticeId, crewId, noticeId});
	}
	
	
	/**
	 * 根据分组及通告单的id查询出通告单的详情
	 * @param crewId
	 * @param groupId
	 * @param noticeId
	 * @return
	 */
	public List<Map<String, Object>> queryNoticeByCrewId(String crewId,String groupId,String noticeId){
		
		List<String> params = new ArrayList<String>();
		params.add(crewId);
		params.add(crewId);
		params.add(crewId);
		params.add(crewId);
		
		StringBuffer sql =new StringBuffer( "select res.noticeId,res.noticeName,res.noticeDate,GROUP_CONCAT(distinct res.shootLocation) shootLocation,res.groupName,");
				sql.append(" sum(res.pageCount) sumPage,count(res.viewId) viewCount,GROUP_CONCAT(distinct res.mainrole) mainrole,GROUP_CONCAT(distinct res.guestrole) guestrole,");
				sql.append(" GROUP_CONCAT(distinct res.massrole) massrole,res.updateTime,res.canceledStatus from (");
				sql.append(" select res1.noticeId,res1.noticeName,res1.noticeDate,res1.shootLocation,res1.groupName,res1.viewId,res1.pageCount,");
				sql.append(" GROUP_CONCAT(distinct res1.mainrole) as mainrole,GROUP_CONCAT(distinct res1.guestrole) as guestrole,GROUP_CONCAT(distinct res1.massrole) as massrole");
				sql.append(" ,res1.updateTime,res1.canceledStatus from (");
				sql.append(" select tni.noticeId,tni.noticeName,tni.noticeDate, tsa.shootLocation,tsg.groupName, tsi.viewId");
				sql.append(" ,tsi.pageCount,maintsr.viewRoleId as mainroleid,maintsr.viewRoleName as mainrole,");
				sql.append(" guesttsr.viewRoleId as guestroleid,guesttsr.viewRoleName as guestrole,masstsr.viewRoleName as massrole,tni.updateTime,tni.canceledStatus");
				sql.append(" from tab_notice_info tni");
				sql.append(" left join tab_view_notice_map tsnm on tsnm.noticeId=tni.noticeId");
				sql.append(" left join tab_shoot_group tsg on tsg.groupId=tni.groupId");
				sql.append(" left join tab_view_info tsi on tsi.viewId=tsnm.viewId");
				sql.append(" left join tab_view_role_map tsrm on tsrm.viewId=tsnm.viewId");
				sql.append(" left join (");
				sql.append(" select tsr.viewRoleId,tsr.viewRoleName from tab_view_role tsr where tsr.crewId=? and tsr.viewRoleType=1");
				sql.append(" ) maintsr on maintsr.viewRoleId=tsrm.viewRoleId ");
				sql.append(" left join (");
				sql.append(" select tsr1.viewRoleId,tsr1.viewRoleName from tab_view_role tsr1 where tsr1.crewId=? and tsr1.viewRoleType=2");
				sql.append(" ) guesttsr on guesttsr.viewRoleId=tsrm.viewRoleId");
				sql.append(" left join (");
				sql.append(" select tsr2.viewRoleId,tsr2.viewRoleName from tab_view_role tsr2 where tsr2.crewId=? and tsr2.viewRoleType=3");
				sql.append(" ) masstsr on masstsr.viewRoleId=tsrm.viewRoleId");
				sql.append(" left join tab_shoot_location tsa on tsa.shootLocationId=tsi.shootLocationId");
				sql.append(" where tni.crewId=?");
				
				if(StringUtils.isNotBlank(groupId)){
					sql.append(" and tsg.groupId=? ");
					params.add(groupId);
				}
				
				if(StringUtils.isNotBlank(noticeId)){
					sql.append(" and tni.noticeId=? ");
					params.add(noticeId);
				}
				
				sql.append(" GROUP BY tni.noticeId,tni.noticeName,tni.noticeDate, tsa.shootLocation,tsg.groupName, tsi.viewId");
				sql.append(" ,tsi.pageCount,maintsr.viewRoleId,maintsr.viewRoleName,");
				sql.append(" guesttsr.viewRoleId,guesttsr.viewRoleName,masstsr.viewRoleName,tni.updateTime,tni.canceledStatus");
				sql.append(" ) res1 group by res1.noticeId,res1.noticeName,res1.noticeDate,res1.shootLocation,"
						+ "res1.groupName,res1.viewId,res1.pageCount,res1.updateTime,res1.canceledStatus");
				sql.append(" ) res group by res.noticeId,res.noticeName,res.noticeDate,res.groupName,res.updateTime,res.canceledStatus order by res.noticeDate desc");
		
		return this.query(sql.toString(),params.toArray(), null);
	}
	
	
	/**
	 * 根据通告单的id查询出当前通告单的详细信息
	 * @param noticeId
	 * @return
	 */
	public NoticeInfoModel queryNoticeInfoModelById(String noticeId){
		
		String sql = "select * from "+NoticeInfoModel.TABLE_NAME +" where noticeId=?";
		
		List<NoticeInfoModel> list = this.query(sql, new Object[]{noticeId}, NoticeInfoModel.class, null);
		
		if(null == list || list.size()==0){
			return null;
		}
		
		return list.get(0);
	}
	
	/**
	 * 按日期排序获取第一个通告单
	 * @param crewId
	 * @return
	 */
	public NoticeInfoModel queryFirstNoticeInfoModelByCrewId(String crewId){
		
		String sql = "select * from "+NoticeInfoModel.TABLE_NAME +" where crewId=? order by noticeDate limit 0,1";
		
		List<NoticeInfoModel> list = this.query(sql, new Object[]{crewId}, NoticeInfoModel.class, null);
		
		if(null == list || list.size()==0){
			return null;
		}
		
		return list.get(0);
	}
	
	
	/**
	 * 根据多个条件查询通告单信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<NoticeInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + NoticeInfoModel.TABLE_NAME + " where 1 = 1 ");
		
		Set<String> keySet = conditionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		List<Object> conList = new LinkedList<Object>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = conditionMap.get(key);

			if (key.equals("noticeId")) {
				if (value != null && !StringUtils.isBlank(value.toString())) {
					sql.append(" and " + key + " != ?");
					conList.add(value);
				}
				continue;
			}
			sql.append(" and " + key + " = ?");
			conList.add(value);
		}
		Object[] objArr = conList.toArray();
		List<NoticeInfoModel> noticeInfoList = this.query(sql.toString(), objArr, NoticeInfoModel.class, page);
		
		return noticeInfoList;
	}
	
	/**
	 * 查询通告单信息
	 * 该方法还会查询出通告单时间信息
	 * @param crewId	剧组ID
	 * @param userId	用户ID
	 * @param page	分页信息
	 * @param isKefu	用户是否是客服
	 * @param isForClip	是否现场日志
	 * @return 通告单所有基本信息，通告单时间更新时间，通告单组别名称，该用户针对通告单的反馈状态
	 */
	public List<Map<String, Object>> queryNoticeInfoWithNoticeTime (String crewId, String userId, Page page, boolean isKefu, boolean isForClip) {
		List<Object> params = new ArrayList<Object>();
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tmp.*, tnpp.backStatus ");
		sql.append(" FROM ");
		sql.append(" 	( ");
		sql.append(" 		SELECT ");
		sql.append(" 			tni.*, DATE_FORMAT(tnt.updateTime, '%Y%m%d%H%i%s' ) noticeVersion, tnt.version, ");
		sql.append(" 			tnt.updateTime noticeTimeUpdateTime, ");
		sql.append(" 			if(tsli.shootLocation is null,GROUP_CONCAT(distinct tsa.vname),tsli.shootLocation) shootLocation, ");
		sql.append(" 			tsg.groupName, count(distinct tvi.viewId) viewCount, ");
		sql.append(" 			(select sum(ifnull(tvnm2.shootPage,tvi2.pageCount)) from tab_view_notice_map tvnm2,tab_view_info tvi2 where tvi2.viewId=tvnm2.viewId and tvnm2.noticeId=tni.noticeId) as sumPage,");
		sql.append(" 			if(tsli.shootScene is null,GROUP_CONCAT(distinct tvl.location ORDER BY tvl.locationType),tsli.shootScene) shootScene,tsli.bootTime,tsli.packupTime ");
		sql.append(" 		FROM ");
		sql.append(" 			tab_notice_info tni ");
		sql.append(" 		LEFT JOIN tab_notice_time tnt on tnt.noticeId = tni.noticeId ");
		sql.append(" 		LEFT JOIN tab_shoot_group tsg on tsg.groupId = tni.groupId ");
		sql.append("		LEFT JOIN tab_view_notice_map tvnm on tvnm.noticeId=tni.noticeId ");
		sql.append("		LEFT JOIN tab_view_info tvi on tvi.viewId=tvnm.viewId ");
		sql.append("		LEFT JOIN tab_sceneview_info tsa on tsa.id=tvi.shootLocationId ");
		sql.append("		LEFT JOIN tab_view_location_map tvlm on tvlm.viewId=tvi.viewId ");
		sql.append(" 		LEFT JOIN tab_view_location tvl ON tvl.locationId = tvlm.locationId ");
		sql.append("		LEFT JOIN tab_shootLive_info tsli on tsli.noticeId = tni.noticeId ");
		sql.append(" 		WHERE ");
		sql.append(" 			tni.crewId = ? ");
		
		params.add(crewId);
		
		//如果是客服，则会查询剧组中所有通告
		if (!isKefu) {
			sql.append(" 		AND ( ");
			sql.append(" 			tni.published = 1 ");//添加该条件是为了兼容没有tab_notice_pushFedBack表之前的通告单
			sql.append(" 			OR EXISTS ( ");
			sql.append(" 				SELECT ");
			sql.append(" 					1 ");
			sql.append(" 				FROM ");
			sql.append(" 					tab_notice_pushFedBack tnp ");
			sql.append(" 				WHERE ");
			sql.append(" 					tnp.noticeId = tni.noticeId ");
			sql.append(" 				AND DATE_FORMAT(tnt.updateTime, '%Y%m%d%H%i%s') = tnp.noticeVersion ");
			if (!isForClip) {
				sql.append(" 				AND tnp.userId = ? ");
				params.add(userId);
			}
			
			sql.append(" 				AND tnp.crewId = ? ");
			sql.append(" 			) ");
			sql.append(" 		) ");
			
			params.add(crewId);
		}
		sql.append(" GROUP BY tni.noticeId,tnt.updateTime,tnt.version,tnt.updateTime,tsg.groupName,tsli.bootTime,tsli.packupTime,tsli.shootLocation,tsli.shootScene ");
		sql.append(" 	) tmp ");
		sql.append(" LEFT JOIN tab_notice_pushFedBack tnpp ON tnpp.noticeId = tmp.noticeId ");
		sql.append(" AND tnpp.noticeVersion = tmp.noticeVersion ");
		sql.append(" AND tnpp.userId = ? ");
		sql.append(" AND tnpp.crewId = ? ");
		sql.append(" ORDER BY ");
		sql.append(" 	tmp.noticeDate DESC, tmp.groupId ");
		
		params.add(userId);
		params.add(crewId);
		
		return this.query(sql.toString(), params.toArray(), page);
	}
	
	/**
	 * 查询通告单信息
	 * 该方法还会查询出通告单时间信息
	 * @param crewId	剧组ID
	 * @param userId	用户ID
	 * @param page	分页信息
	 * @param isKefu	用户是否是客服
	 * @param isForClip	是否现场日志
	 * @return 通告单所有基本信息，通告单时间更新时间，通告单组别名称，该用户针对通告单的反馈状态
	 */
	public List<Map<String, Object>> queryNoticeInfoWithSomeColumns (String crewId, String userId, Page page, boolean isKefu, boolean isForClip) {
		List<Object> params = new ArrayList<Object>();
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tmp.noticeId,tmp.noticeName,tmp.noticeDate,tmp.version,tmp.groupName,tmp.publishTime,tmp.canceledStatus, tnpp.backStatus ,tmp.updateTime,tmp.createTime,tmp.noticeTimeUpdateTime");
		sql.append(" FROM ");
		sql.append(" 	( ");
		sql.append(" 		SELECT ");
		sql.append(" 			tni.*, DATE_FORMAT(tnt.updateTime, '%Y%m%d%H%i%s' ) noticeVersion, tnt.version, ");
		sql.append(" 			tnt.updateTime noticeTimeUpdateTime, ");
		sql.append(" 			if(tsli.shootLocation is null,GROUP_CONCAT(distinct tsa.vname),tsli.shootLocation) shootLocation, ");
		sql.append(" 			tsg.groupName, count(distinct tvi.viewId) viewCount, ");
		sql.append(" 			(select sum(ifnull(tvnm2.shootPage,tvi2.pageCount)) from tab_view_notice_map tvnm2,tab_view_info tvi2 where tvi2.viewId=tvnm2.viewId and tvnm2.noticeId=tni.noticeId) as sumPage,");
		sql.append(" 			if(tsli.shootScene is null,GROUP_CONCAT(distinct tvl.location ORDER BY tvl.locationType),tsli.shootScene) shootScene,tsli.bootTime,tsli.packupTime ");
		sql.append(" 		FROM ");
		sql.append(" 			tab_notice_info tni ");
		sql.append(" 		LEFT JOIN tab_notice_time tnt on tnt.noticeId = tni.noticeId ");
		sql.append(" 		LEFT JOIN tab_shoot_group tsg on tsg.groupId = tni.groupId ");
		sql.append("		LEFT JOIN tab_view_notice_map tvnm on tvnm.noticeId=tni.noticeId ");
		sql.append("		LEFT JOIN tab_view_info tvi on tvi.viewId=tvnm.viewId ");
		sql.append("		LEFT JOIN tab_sceneview_info tsa on tsa.id=tvi.shootLocationId ");
		sql.append("		LEFT JOIN tab_view_location_map tvlm on tvlm.viewId=tvi.viewId ");
		sql.append(" 		LEFT JOIN tab_view_location tvl ON tvl.locationId = tvlm.locationId ");
		sql.append("		LEFT JOIN tab_shootLive_info tsli on tsli.noticeId = tni.noticeId ");
		sql.append(" 		WHERE ");
		sql.append(" 			tni.crewId = ? ");
		
		params.add(crewId);
		
		//如果是客服，则会查询剧组中所有通告
		if (!isKefu) {
			sql.append(" 		AND ( ");
			sql.append(" 			tni.published = 1 ");//添加该条件是为了兼容没有tab_notice_pushFedBack表之前的通告单
			sql.append(" 			OR EXISTS ( ");
			sql.append(" 				SELECT ");
			sql.append(" 					1 ");
			sql.append(" 				FROM ");
			sql.append(" 					tab_notice_pushFedBack tnp ");
			sql.append(" 				WHERE ");
			sql.append(" 					tnp.noticeId = tni.noticeId ");
			sql.append(" 				AND DATE_FORMAT(tnt.updateTime, '%Y%m%d%H%i%s') = tnp.noticeVersion ");
			if (!isForClip) {
				sql.append(" 				AND tnp.userId = ? ");
				params.add(userId);
			}
			
			sql.append(" 				AND tnp.crewId = ? ");
			sql.append(" 			) ");
			sql.append(" 		) ");
			
			params.add(crewId);
		}
		sql.append(" GROUP BY tni.noticeId,tnt.updateTime,tnt.version,tnt.updateTime,tsg.groupName,tsli.bootTime,tsli.packupTime,tsli.shootLocation,tsli.shootScene ");
		sql.append(" 	) tmp ");
		sql.append(" LEFT JOIN tab_notice_pushFedBack tnpp ON tnpp.noticeId = tmp.noticeId ");
		sql.append(" AND tnpp.noticeVersion = tmp.noticeVersion ");
		sql.append(" AND tnpp.userId = ? ");
		sql.append(" AND tnpp.crewId = ? ");
		sql.append(" ORDER BY ");
		sql.append(" 	tmp.noticeDate DESC, tmp.groupId ");
		
		params.add(userId);
		params.add(crewId);
		
		return this.query(sql.toString(), params.toArray(), page);
	}
	
	/**
	 * 查询通告单下所有场景的集场列表信息
	 * @param noticeId
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryNoticeSeriesViewNo(String noticeId, String crewId) {
		String sql = "select tvi.viewId, tvi.seriesNo, tvi.viewNo, tvi.isManualSave from tab_view_info tvi, tab_view_notice_map tvnm where tvi.crewId = ? and tvnm.noticeId = ? and tvnm.viewId = tvi.viewId order by tvnm.sequence;";
		
		return this.query(sql, new Object[] {crewId, noticeId}, null);
	}
	
	/**
	 * 根据组名和通告单日期查询通告单
	 * @param groupName
	 * @param noticeDate
	 * @return
	 */
	public List<NoticeInfoModel> queryNoticeByGroupAndDate(String crewId, String groupName, String noticeDate) {
		String sql = "select tni.* from tab_notice_info tni, tab_shoot_group tsg where tni.noticedate = STR_TO_DATE(?,'%Y-%m-%d') and tsg.groupId = tni.groupId and tsg.groupName = ? and tni.crewId=?";
		return this.query(sql, new Object[] {noticeDate, groupName, crewId}, NoticeInfoModel.class, null);
	}
	
	
	/**
	 * 查询单条通告单的完整信息
	 * 包含拍摄地、场景信息
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	public Map<String, Object> queryNoticeFullInfoById(String crewId, String noticeId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tni.*, GROUP_CONCAT(DISTINCT tsl.vname) shootLocation, ");
		sql.append(" 	GROUP_CONCAT(DISTINCT tvl.location ORDER BY tvl.locationType) viewLocation");
		sql.append(" FROM ");
		sql.append(" 	tab_notice_info tni ");
		sql.append(" LEFT JOIN tab_view_notice_map tvnm ON tni.noticeId = tvnm.noticeId ");
		sql.append(" LEFT JOIN tab_view_info tvi ON tvi.viewId = tvnm.viewId ");
		sql.append(" LEFT JOIN tab_sceneview_info tsl ON tsl.id = tvi.shootLocationId ");
		sql.append(" LEFT JOIN tab_view_location_map tvlm ON tvlm.viewId = tvi.viewId ");
		sql.append(" LEFT JOIN tab_view_location tvl ON tvl.locationId = tvlm.locationId ");
		sql.append(" WHERE ");
		sql.append(" 	tni.noticeId = ? ");
		sql.append(" AND tni.crewId = ? ");
		sql.append(" GROUP BY ");
		sql.append(" 	tni.noticeId ");
		
		List<Object> params = new ArrayList<Object>();
		params.add(noticeId);
		params.add(crewId);
		if (getResultCount(sql.toString(), params.toArray()) == 1) {
			return this.getJdbcTemplate().queryForMap(sql.toString(), new Object[] {noticeId, crewId});
		}
		return null;
		
	}
	
	
	/**
	 * 根据场景ID查询所属的通告单
	 * @param crewId
	 * @param viewIds
	 * @return
	 */
	public List<Map<String, Object>> queryByViewIds(String crewId, String viewIds) {
		viewIds = "'" + viewIds.replace(",", "','") + "'";
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	DISTINCT tvi.viewId, tvi.seriesNo, tvi.viewNo, tni.noticeName ");
		sql.append(" FROM ");
		sql.append(" 	tab_notice_info tni, ");
		sql.append(" 	tab_view_notice_map tvnm, ");
		sql.append(" 	tab_view_info tvi ");
		sql.append(" WHERE ");
		sql.append(" 	tvnm.crewId = ? ");
		sql.append(" AND tvnm.noticeId = tni.noticeId ");
		sql.append(" AND tvnm.viewId = tvi.viewId ");
		sql.append(" AND tvnm.viewId IN ("+ viewIds +") ");
		sql.append(" and tvi.crewId=? ");
		sql.append(" and tni.crewId=? ");
		
		return this.query(sql.toString(), new Object[] {crewId, crewId, crewId}, null);
	}
	
	/**
	 * 根据时间段查询通告单总数
	 * @param firstNoticeDate 第一个新建通告单的时间
	 * @param currNoticDate 当前通告单的时间
	 * @return
	 */
	public List<Map<String, Object>> queryNoticeCountByDate(Date firstNoticeDate, Date currNoticDate, String crewId, String cancledNotice){
		StringBuffer sql = new StringBuffer();
		List<Object> param = new ArrayList<Object>();
		
		sql.append(" SELECT	DISTINCT(noticeDate) FROM 	tab_notice_info WHERE ");
		if (StringUtils.isNotBlank(cancledNotice)) {
			sql.append(" canceledStatus = 1  AND crewId = ? ");
			param.add(crewId);
		}else {
			sql.append(" DATE_FORMAT(noticeDate,'%Y-%m-%d') BETWEEN DATE_FORMAT(?,'%Y-%m-%d') AND DATE_FORMAT(?,'%Y-%m-%d') AND crewId = ?");
			param.add(firstNoticeDate);
			param.add(currNoticDate);
			param.add(crewId);
		}
		sql.append(" GROUP BY noticeDate");
		
		return this.query(sql.toString(), param.toArray(), null);
	}
	
	/**
	 * 查询拍摄生产报表
	 * 将删戏计入已完成
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryShootingProductionReport(String crewId) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select tni.noticeDate,count(tvi.viewId) planViewCount, ");
		sql.append(" 	ifnull(sum(if(tvnm.shootStatus in (2,3,5),1,0)),0) realViewCount, ");
		sql.append(" 	round(ifnull(sum(if(tvnm.shootPage is null,tvi.pageCount,tvnm.shootPage)),0),2) planPageCount, ");
		sql.append(" 	round(ifnull(sum(if(tvnm.shootStatus in (2,3,5),if(tvnm.shootPage is null,tvi.pageCount,tvnm.shootPage),0)),0),2) realPageCount ");
		sql.append(" FROM tab_notice_info tni ");
		sql.append(" left join tab_view_notice_map tvnm on tvnm.noticeId=tni.noticeId ");
		sql.append(" left join tab_view_info tvi on tvnm.viewId=tvi.viewId ");
		sql.append(" where tni.crewId=? ");
		sql.append(" group by tni.noticeDate ");
		sql.append(" order by tni.noticeDate ");
		return this.query(sql.toString(), new Object[]{crewId}, null);
	}
	
	/**
	 * 根据场景id查询出场景所在的通告单id
	 * @param viewId
	 * @return
	 */
	public List<Map<String, Object>> queryNoticeIdByViewId(String viewId,String crewId){
		if (viewId.contains("-")) {
			String[] split = viewId.split("-");
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT tni.noticeDate,tni.noticeId,tni.groupId,tsg.groupName,tni.noticeName");
			sql.append(" FROM tab_view_notice_map tnm");
			sql.append(" LEFT JOIN tab_notice_info tni ON tnm.noticeId = tni.noticeId");
			sql.append(" LEFT JOIN tab_shoot_group tsg ON tni.groupId = tsg.groupId");
			sql.append(" LEFT JOIN tab_view_info tvi ON tvi.viewId = tnm.viewId");
			sql.append(" WHERE");
			sql.append(" tvi.seriesNo = ? AND tvi.viewNo = ? AND tnm.crewId = ?");
			return this.query(sql.toString(), new Object[]{split[0], split[1], crewId}, null);
		}else {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT tni.noticeDate,tni.noticeId,tni.groupId,tsg.groupName,tni.noticeName");
			sql.append(" FROM tab_view_notice_map tnm");
			sql.append(" LEFT JOIN tab_notice_info tni ON tnm.noticeId = tni.noticeId");
			sql.append(" LEFT JOIN tab_shoot_group tsg ON tni.groupId = tsg.groupId");
			sql.append(" LEFT JOIN tab_view_info tvi ON tvi.viewId = tnm.viewId");
			sql.append(" WHERE");
			sql.append(" tvi.viewNo = ? AND tnm.crewId = ?");
			return this.query(sql.toString(), new Object[]{viewId, crewId}, null);
		}
		
	}
	
	/**
	 * 查询当前剧组中共有多少张已销场 的通告单
	 * @param creId
	 * @return
	 */
	public List<Map<String, Object>> queryCancledNoticeCount (String crewId){
		String sql = " SELECT noticeId,DATE_FORMAT(noticeDate, '%Y-%m') noticeMonth,noticeName FROM tab_notice_info WHERE canceledStatus = 1 AND crewId= ? ORDER BY noticeDate desc";
		return this.query(sql, new Object[] {crewId}, null);
	}
	
	/**
	 * 查询当前剧组中共有多少张未销场 的通告单
	 * @param creId
	 * @return
	 */
	public List<Map<String, Object>> queryNotCancledNoticeCount (String crewId){
		String sql = " SELECT noticeId,DATE_FORMAT(noticeDate, '%Y-%m') noticeMonth,noticeName FROM tab_notice_info WHERE canceledStatus = 0 AND crewId= ? ORDER BY noticeDate desc";
		return this.query(sql, new Object[] {crewId}, null);
	}
	
	/**
	 * 分页月份列表
	 * @param crewId
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> queryCancledMonthList(String crewId, Page page){
		String sql = " SELECT DISTINCT(DATE_FORMAT(noticeDate, '%Y-%m')) noticeMonth,count(noticeId) noticeNum FROM tab_notice_info " 
				+ " WHERE canceledStatus = 1 AND crewId= ? group by DATE_FORMAT(noticeDate, '%Y-%m') ORDER BY noticeMonth DESC";
		return this.query(sql, new Object[] {crewId}, page);
	}
	
	/**
	 * 根据通告单的id查询通告单中所有场景的状态
	 * @param noticeId
	 * @return
	 */
	public List<Map<String, Object>> queryNoticeViewListByNoticeId(String noticeId){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT tsnm.shootStatus, tvi.seriesNo,tvi.viewNo");
		sql.append(" FROM tab_view_notice_map tsnm ,tab_view_info tvi");
		sql.append(" WHERE tsnm.viewId = tvi.viewId AND tsnm.noticeId = ?");
		
		return this.query(sql.toString(), new Object[] {noticeId}, null);
	}
	
	/**
	 * 查询已拍摄多少天，根据已销场的通告单
	 * @param crewId
	 * @return
	 */
	public int queryShootDates(String crewId) {
		String sql = "select count(distinct noticeDate) num from tab_notice_info where canceledStatus=1 and crewId = ? ";
		Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql, new Object[]{crewId});
		if(map != null && !map.isEmpty()) {
			return Integer.parseInt(map.get("num") + "");
		} else {
			return 0;
		}
	}
	
	/**
	 * 查询日拍摄进度，分组
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryDayProduction(String crewId) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select DATE_FORMAT(tni.noticeDate,'%Y') year,DATE_FORMAT(tni.noticeDate,'%u') week, ");
		sql.append(" 	tni.noticeDate,tsg.groupId,tsg.groupName,count(tvi.viewId) viewCount, ");
		sql.append(" 	ifnull(sum(if(tvnm.shootPage is null,tvi.pageCount,tvnm.shootPage)),0) pageCount ");
		sql.append(" from tab_notice_info tni ");
		sql.append(" LEFT JOIN tab_shoot_group tsg ");
		sql.append(" ON tni.groupId=tsg.groupId ");
		sql.append(" LEFT JOIN tab_view_notice_map tvnm ");
		sql.append(" ON tni.noticeId=tvnm.noticeId ");
		sql.append(" LEFT JOIN tab_view_info tvi ");
		sql.append(" on tvnm.viewId=tvi.viewId ");
		sql.append(" where tni.crewId=? ");
		sql.append(" and tni.noticeDate<=now() ");
		sql.append(" AND (tvnm.shootStatus=2 or tvnm.shootStatus=5) ");
		sql.append(" GROUP BY year,week,tni.noticeDate,tsg.groupId ");
		sql.append(" ORDER BY year,week,tni.noticeDate,tsg.createTime ");
		return this.query(sql.toString(), new Object[]{crewId}, null);
	}
	
	/**
	 * 查询用户在剧组中未读的通告单列表
	 * @param crewId
	 * @param userId
	 * @return
	 */
	public List<NoticeInfoModel> queryNotReadNoticeList(String crewId, String userId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tni.*, tnp.backStatus ");
		sql.append(" FROM ");
		sql.append(" 	tab_notice_pushFedBack tnp, ");
		sql.append(" 	tab_notice_info tni ");
		sql.append(" WHERE ");
		sql.append(" 	tnp.crewId = ? ");
		sql.append(" AND tnp.userId = ? ");
		sql.append(" AND tnp.backStatus != 3 ");
		sql.append(" AND tnp.noticeId = tni.noticeId ");
		sql.append(" AND tnp.noticeVersion = DATE_FORMAT(tni.updateTime, '%Y%m%d%H%i%s')  ");
		return this.query(sql.toString(), new Object[] {crewId, userId}, NoticeInfoModel.class, null);
	}
	
	/**
	 * 分页查询通告单日期列表
	 * @param crewId
	 * @param canceledStatus
	 * @param noticeMonth
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> queryNoticeDateList(String crewId, Integer canceledStatus, String noticeMonth, Page page) {
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		params.add(crewId);
		sql.append(" select * from (");
		sql.append(" select a.*,cast((@rowNO := @rowNO + 1) as SIGNED INTEGER) AS rownum from ( ");
		sql.append(" select noticeDate, count(noticeId) noticeNum ");
		sql.append(" from tab_notice_info tni ");
		sql.append(" where tni.crewId=? ");
		if(canceledStatus != null) {
			sql.append(" and tni.canceledStatus=? ");
			params.add(canceledStatus);
		}
		if(StringUtil.isNotBlank(noticeMonth)) {
			sql.append(" and DATE_FORMAT(tni.noticeDate, '%Y-%m')=? ");
			params.add(noticeMonth);
		}
		sql.append(" group by noticeDate ");
		sql.append(" ) a ");
		sql.append(" INNER JOIN (SELECT @rowNO := 0) it ");
		sql.append(" order by noticeDate ");
		sql.append(" ) mytable ");
		if(canceledStatus == 0) {
			sql.append(" order by noticeDate ");
		} else {
			sql.append(" order by noticeDate desc ");
		}
		return this.query(sql.toString(), params.toArray(), page);
	}
}
