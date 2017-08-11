package com.xiaotu.makeplays.view.dao;

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
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.locationsearch.model.SceneViewInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.RegexUtils;
import com.xiaotu.makeplays.view.controller.filter.ViewFilter;
import com.xiaotu.makeplays.view.model.AtmosphereInfoModel;
import com.xiaotu.makeplays.view.model.ViewContentModel;
import com.xiaotu.makeplays.view.model.ViewInfoModel;

/**
 * 场景信息
 * @author xuchangjian
 */
@Repository
public class ViewInfoDao extends BaseDao<ViewInfoModel> {
	
	/**
	 * @Description  删除勘景信息时修改场景表中拍摄地id为null
	 * @param locationId
	 */
	public void updateShootLocatinId(String locationId){
		String sql = "update "+ViewInfoModel.TABLE_NAME +" set shootlocationid = null where shootlocationid = ? ";
		this.getJdbcTemplate().update(sql, new Object[]{locationId});
	}
	
	

	/**
	 * 通过场景ID查找场景信息
	 * @param viewId
	 * @return
	 */
	public ViewInfoModel queryById (String viewId) {
		String sql = "select * from " + ViewInfoModel.TABLE_NAME +" where viewId = ?";
		
		ViewInfoModel viewInfoModel = null;
		Object[] args = new Object[] {viewId};
		if (getResultCount(sql, args) == 1) {
			viewInfoModel = this.getJdbcTemplate().queryForObject(sql, args, ParameterizedBeanPropertyRowMapper
					.newInstance(ViewInfoModel.class));
		}
		
		return viewInfoModel;
	}
	
	/**
	 * 根据剧组ID查找对应的所有场景信息
	 * @param crewId
	 * @return
	 */
	public List<ViewInfoModel> queryByCrewId(String crewId, Page page) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select * from tab_view_info where crewId = ? order by seriesNo");
		
		List<ViewInfoModel> viewList = this.query(sql.toString(), new Object[]{crewId}, ViewInfoModel.class, page);
		
		return viewList;
	}
	
	/**
	 * 根据多个条件查询场景信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page	分页信息
	 * @return
	 */
	public List<ViewInfoModel> queryManyByMuitiContition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from tab_view_info where 1 = 1 ");
		
		Set<String> keySet = conditionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		List<Object> conList = new LinkedList<Object>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = conditionMap.get(key);
			sql.append(" and " + key + " = ?");
			conList.add(value);
		}
		
		Object[] objArr = conList.toArray();
		List<ViewInfoModel> viewList = this.query(sql.toString(), objArr, ViewInfoModel.class, page);
		
		return viewList;
	}
	
	/**
	 * 根据剧本ID,集次，场次查询对应的场景信息
	 * @param crewId	剧本ID
	 * @param seriesNo	集次
	 * @param viewNo	场次
	 * @return
	 */
	public ViewInfoModel queryOneByCrewIdAndSeViNo (String crewId, Integer seriesNo, String viewNo) {
		List<Object> consitionList = new ArrayList<Object>();
		
		StringBuilder sql = new StringBuilder("select * from tab_view_info where crewId = ? and viewNo = ?");
		consitionList.add(crewId);
		consitionList.add(viewNo);
		if (seriesNo != null) {
			sql.append(" and seriesNo = ?");
			consitionList.add(seriesNo);
		}
		
		ViewInfoModel view = null;
		Object[] args = consitionList.toArray();
		if (getResultCount(sql.toString(), args) == 1) {
			view = this.getJdbcTemplate().queryForObject(sql.toString(), args, ParameterizedBeanPropertyRowMapper
					.newInstance(ViewInfoModel.class));
		}
		
		return view;
	}
	
	/**
	 * 根据剧本ID查找剧本下不重复的场景信息
	 * @param crewId
	 * @return
	 */
	public List<String> querySiteListByCrewId (String crewId) {
		String sql = "select DISTINCT(site) from " + ViewInfoModel.TABLE_NAME + " where crewId = ? and site != ?";
		Object[] args = new Object[] {crewId, ""};
		
		List<String> siteString = this.getJdbcTemplate().queryForList(sql, String.class ,args);
		return siteString;
	}
	
	/**
	 * 根据剧本id查询出所有的特殊提醒
	 * @return
	 */
	public List<String> querySpecialRemindByCrewId(String crewId){
		String sql = "select DISTINCT(specialRemind) from " + ViewInfoModel.TABLE_NAME + " where crewId = ? and specialRemind != ?";
		Object[] args = new Object[] {crewId, ""};
		
		List<String> siteString = this.getJdbcTemplate().queryForList(sql, String.class ,args);
		return siteString;
	}
	
	/**
	 * 批量新增操作
	 * @param atmosphereList
	 * @throws Exception 
	 */
	public void addMany (List<ViewInfoModel> viewInfoList) throws Exception {
		this.addBatch(viewInfoList, ViewInfoModel.class);
	}
	
	/**
	 * 更新数据
	 * @param viewInfo	场景信息
	 * @throws Exception 
	 */
	public void update(ViewInfoModel viewInfo) throws Exception {
		this.updateWithNull(viewInfo, "viewId");
	}
	
	/**
	 * 批量更新数据
	 * @param viewInfoList
	 * @throws Exception 
	 */
	public void updateManyViewInfo(List<ViewInfoModel> viewInfoList) throws Exception {
		this.updateBatch(viewInfoList, "viewId", ViewInfoModel.class);
	}
	
	/**
	 * 批量更新数据
	 * 空值不更新入库
	 * @param viewInfoList
	 * @throws Exception
	 */
	public void updateManyWithoutNull(List<ViewInfoModel> viewInfoList) throws Exception {
		for (ViewInfoModel viewInfo : viewInfoList) {
			this.update(viewInfo, "viewId");
		}
	}
	
	/**
	 * 批量更新场景中可能存在的角色
	 * @param viewInfoList
	 */
	public void updateNotGetRoleNamesBatch(List<Map<String, String>> viewInfoList) {
		for (Map<String, String> viewInfo : viewInfoList) {
			String viewId = viewInfo.get("viewId");
			String notGetRoleNames = viewInfo.get("notGetRoleNames");
			this.updateNotGetRoleNames(viewId, notGetRoleNames);
		}
	}
	
	/**
	 * 更新场景中可能存在的角色
	 * @param viewId
	 * @param notGetRoleNames
	 */
	public void updateNotGetRoleNames(String viewId, String notGetRoleNames) {
		String sql = "update tab_view_info set notGetRoleNames = ? where viewId = ?";
		this.getJdbcTemplate().update(sql, notGetRoleNames, viewId);
	}
	
	/**
	 * 批量更新场景中可能存在的道具
	 * @param viewInfoList
	 */
	public void updateNotGetPropsBatch(List<Map<String, String>> viewInfoList) {
		for (Map<String, String> viewInfo : viewInfoList) {
			String viewId = viewInfo.get("viewId");
			String notGetProps = viewInfo.get("notGetProps");
			this.updateNotGetProps(viewId, notGetProps);
		}
	}
	
	/**
	 * 更新场景中可能存在的道具
	 * @param viewId
	 * @param notGetRoleNames
	 */
	public void updateNotGetProps(String viewId, String notGetProps) {
		String sql = "update tab_view_info set notGetProps = ? where viewId = ?";
		this.getJdbcTemplate().update(sql, notGetProps, viewId);
	}
	
	
	/**
	 * 根据场景角色ID查询场景信息
	 * @param viewRoleIds
	 * @return
	 */
	public List<ViewInfoModel> queryByViewRoleIds(String viewRoleIds) {
		viewRoleIds = "'" + viewRoleIds.replace(",", "','") + "'";
		String sql = "SELECT distinct tvi.* from tab_view_info tvi, tab_view_role_map tvrm where tvrm.viewRoleId in("+ viewRoleIds +") and tvrm.viewId = tvi.viewId";
		return this.query(sql, null, ViewInfoModel.class, null);
	}
	
	
	
	/**
	 * 场景表查询
	 * @param crewId
	 * @param page
	 * @param sortField
	 * @param sort
	 * @return
	 */
	public List<Map<String, Object>> queryViewList(String crewId,Page page,ViewFilter filter){
		
		List<Object> filterList = new ArrayList<Object>();
		filterList.add(crewId);
		filterList.add(crewId);
		filterList.add(crewId);
		filterList.add(crewId);
		filterList.add(crewId);
		String sql = "select res.*, shootDateInfo.shootDates from (select tsi.viewId,tsi.seriesNo,tsi.viewNo,tsi.specialRemind,tsi.season,"
				+ "tsi.atmosphereId,tsi.site,tsi.viewType,tsha.vCity shootRegion,tsha.vname shootLocation,tsha.id shootLocationId ,tsi.pageCount,"
				+" GROUP_CONCAT(distinct tsa.location) majorView, GROUP_CONCAT(distinct tsa.locationId) majorViewId, "
				+" GROUP_CONCAT(distinct tsa2.location) minorView, GROUP_CONCAT(distinct tsa2.locationId) minorViewId, "
				+" GROUP_CONCAT(distinct tsa3.location) thirdLevelView, GROUP_CONCAT(distinct tsa3.locationId) thirdLevelViewId,"
				+ " tml.locationViewCount, tml.location mainLocation, tsi.mainContent, tsi.remark, tsi.shotDate shootDate, tsi.shootStatus,"
				+ " GROUP_CONCAT( distinct tmi.id) makeupId,GROUP_CONCAT(distinct tmi.makeupName) makeupName "
				+ " ,GROUP_CONCAT(distinct tci.id) clothesId,GROUP_CONCAT( distinct tci.clothesName) clothesName"
				+",GROUP_CONCAT(distinct tia.advertName) advertName "
				+",GROUP_CONCAT(distinct tia.advertId) advertId "
				+ " ,tai.atmosphereName "
				+" from tab_view_info tsi "
				+" left join tab_view_location_map tsam on tsam.viewId=tsi.viewId"
				+" left join tab_view_location tsa on tsa.locationId=tsam.locationId AND tsa.locationType = 1 "
				+" left join tab_view_location tsa2 on tsa2.locationId=tsam.locationId AND tsa2.locationType = 2 "
				+" left join tab_view_location tsa3 on tsa3.locationId=tsam.locationId AND tsa3.locationType = 3 ";
				sql+=" left join tab_sceneview_info tsha on tsha.id=tsi.shootLocationId"
						
				//求出每个主场景的场数，用于分场排序
				+" LEFT JOIN ( "
				+" 	select tmp.*, tvlm3.viewId from (SELECT "
				+" 		tvl2.locationId, tvl2.location, "
				+" 		count(1) locationViewCount "
				+" 	FROM "
				+" 		tab_view_location tvl2, "
				+" 		tab_view_location_map tvlm2 "
				+" 	WHERE "
				+" 		tvl2.locationId = tvlm2.locationId "
				+" 	AND tvlm2.crewId =? "
				+" 	AND tvl2.crewId =? "
				+" 	AND tvl2.locationType = 1 "
				+" 	GROUP BY tvl2.locationId, tvl2.location) tmp  "
				+"  LEFT JOIN tab_view_location_map tvlm3 on tvlm3.locationId = tmp.locationId "
				+" ) tml ON tml.viewId = tsi.viewId "
				
				
				+"  LEFT JOIN ("
				+"		SELECT tgi.id,tgi.goodsName makeupName,tvgm.viewId"
				+"		FROM tab_goods_info tgi,tab_view_goods_map tvgm"
				+"		WHERE tgi.id = tvgm.goodsId AND tgi.goodsType = 2 AND tgi.crewId = ?"
				+"	) tmi ON tmi.viewId = tsi.viewId"
				+"  LEFT JOIN ("
				+"		SELECT tgi.id,tgi.goodsName clothesName,tvgm.viewId"
				+"		FROM tab_goods_info tgi,tab_view_goods_map tvgm"
				+"		WHERE tgi.id = tvgm.goodsId AND tgi.goodsType = 3 AND tgi.crewId = ?"
				+"	) tci ON tci.viewId = tsi.viewId"
				+" left join tab_view_advert_map tvam on tvam.viewId=tsi.viewId "
				+" left join tab_inside_advert tia on tia.advertId=tvam.advertId "
				+" left JOIN tab_atmosphere_info tai ON tsi.atmosphereId = tai.atmosphereId "
				+" WHERE tsi.crewId=? ";
		sql+=" group by tsi.viewId,tsi.seriesNo,tsi.viewNo,tsi.season,tsi.atmosphereId,tsi.site,tsi.viewType,tsha.vname,"
				+ "tsi.pageCount,tsi.mainContent, tsi.remark, tsi.shotDate, tsi.shootStatus, tml.locationViewCount, tml.location, tsi.shootLocationId) res ";
		
		
//		sql += " left join tab_view_notice_map tvnm on tvnm.viewId = res.viewId";
		//查询出每一场的完成时间
		sql += "LEFT JOIN (";
		sql += "	SELECT";
		sql += "		tvi.viewId,";
		sql += "		GROUP_CONCAT(";
		sql += "			tni.noticeDate";
		sql += "			ORDER BY";
		sql += "				tni.noticeDate";
		sql += "		) shootDates";
		sql += "	FROM";
		sql += "		tab_view_notice_map tvnm,";
		sql += "		tab_notice_info tni,";
		sql += "		tab_view_info tvi";
		sql += "	WHERE";
		sql += "		tvnm.viewId = tvi.viewId";
		sql += "	AND tvnm.noticeId = tni.noticeId";
		sql += "	AND tvnm.shootStatus in (1, 2, 4, 5)";
		sql += "	AND tvnm.crewId=?";
		filterList.add(crewId);
		sql += "	GROUP BY tvi.viewId";
		sql += ") shootDateInfo ON shootDateInfo.viewId = res.viewId";
		
		if(null != filter){
			
			if(StringUtils.isNotBlank(filter.getNoticeId())){
				sql+=" LEFT JOIN ( SELECT	tvnm.viewId as viewId, count(*) as viewCount";
				sql+=" FROM	tab_view_notice_map tvnm	WHERE (	tvnm.shootStatus IS NULL	OR tvnm.noticeId = ?)";
				sql+=" AND tvnm.crewId = ? GROUP BY tvnm.viewId) t ON t.viewId = res.viewId";
				filterList.add(filter.getNoticeId());
				filterList.add(crewId);
			}
			
			sql+=" where 1=1 ";
			
			if(StringUtils.isNotBlank(filter.getIsAll())&&filter.getIsAll().equals("0")){
				sql+=" AND res.shootStatus != 2 ";
			}
			
			if(StringUtils.isNotBlank(filter.getNoticeId())){
				sql +=" AND res.shootStatus != 3 AND t.viewCount IS NULL ";
				
				/*sql+=" AND res.shootStatus != 3 AND NOT EXISTS ( SELECT DISTINCT tvnm.viewId FROM tab_view_notice_map tvnm WHERE ( tvnm.shootStatus IS NULL OR tvnm.noticeId = ? )";
				sql+=" AND tvnm.crewId = ? AND tvnm.viewId = res.viewId )";
				filterList.add(filter.getNoticeId());
				filterList.add(crewId);*/
			}
			
			/*if(StringUtils.isNotBlank(filter.getIsAll())&&filter.getIsAll().equals("0")){
				sql+=" and not EXISTS( select tsnm.viewId from tab_view_notice_map tsnm where tsnm.viewId=res.viewId and tsnm.shootStatus in (2,4,5) ) ";
			}
			
			if(StringUtils.isNotBlank(filter.getNoticeId())){
				sql+=" and not EXISTS( select tsnm.viewId from tab_view_notice_map tsnm where tsnm.viewId=res.viewId ) ";
				//filterList.add(filter.getNoticeId());
			}*/
			
			if (!StringUtils.isBlank(filter.getPlanId())) {
				sql+=" and not EXISTS( select 1 from tab_view_plan_map tvpm where tvpm.viewId=res.viewId and tvpm.planId=? ) ";
				filterList.add(filter.getPlanId());
			}
			
			if (!StringUtils.isBlank(filter.getViewIds())) {
				String viewIds = "'"+ filter.getViewIds().replace(",", "','") +"'";
				sql += " and res.viewId in("+ viewIds +")";
			}
			
			if(null != filter.getSeriesNo()){
				sql+=" and res.seriesNo=?";
				filterList.add(filter.getSeriesNo());
			}
			
			if(StringUtils.isNotBlank(filter.getViewNo())){
				sql+=" and res.viewno=?";
				filterList.add(filter.getViewNo());
			}
			
			if (!StringUtils.isBlank(filter.getSeriesViewNos())) {
				sql += " and ( ";
				String[] seriesViewNos = RegexUtils.regexSplitStr("(，|,|；|;)", filter.getSeriesViewNos());
				for (int i = 0; i < seriesViewNos.length; i++) {
					if (StringUtils.isBlank(seriesViewNos[i])) {
						continue;
					}
					String[] seriesViewArr = RegexUtils.regexSplitStr("(-|－|——)", seriesViewNos[i]);
					String seriesNo = seriesViewArr[0].trim();
					String viewNo = seriesViewArr[1].trim();
					if (seriesViewArr.length > 2) {
						for (int v = 2; v < seriesViewArr.length; v++) {
							viewNo += "-" + seriesViewArr[v];
						}
					}
					
					if (i == 0) {
						sql += "(seriesNo = ? and viewNo = ?)";
					} else {
						sql += "or (seriesNo = ? and viewNo = ?)";
					}
					filterList.add(seriesNo);
					filterList.add(viewNo);
				}
				sql += " ) ";
			}
			
			//开始和结束集场，全部填写时执行
			if(null != filter.getStartSeriesNo()&& !StringUtils.isBlank(filter.getStartViewNo())
					&& null != filter.getEndSeriesNo()&& !StringUtils.isBlank(filter.getEndViewNo())){
				
				if(filter.getStartSeriesNo().equals(filter.getEndSeriesNo())){
					//当开始结束集相同时
					sql+=" and (res.seriesNo = ? and abs(res.viewNo) >=abs(?) and abs(res.viewNo) <=abs(?)) ";
					filterList.add(filter.getStartSeriesNo());
					filterList.add(filter.getStartViewNo());
					filterList.add(filter.getEndViewNo());
				}else{
					sql+=" and ((res.seriesNo > ? and res.seriesNo<?) or (res.seriesNo=? and abs(res.viewNo) >=abs(?)) or (res.seriesNo=? and abs(res.viewNo) <=abs(?))) ";
					filterList.add(filter.getStartSeriesNo());
					filterList.add(filter.getEndSeriesNo());
					filterList.add(filter.getStartSeriesNo());
					filterList.add(filter.getStartViewNo());
					filterList.add(filter.getEndSeriesNo());
					filterList.add(filter.getEndViewNo());
				}
				
			}else if(null != filter.getStartSeriesNo()&& !StringUtils.isBlank(filter.getStartViewNo())
					&& null != filter.getEndSeriesNo()&& StringUtils.isBlank(filter.getEndViewNo())){
				//只有结束场次为空时
				sql+=" and ((res.seriesNo > ? and res.seriesNo<=?) or (res.seriesNo=? and abs(res.viewNo) >=abs(?)) ) ";
				filterList.add(filter.getStartSeriesNo());
				filterList.add(filter.getEndSeriesNo());
				filterList.add(filter.getStartSeriesNo());
				filterList.add(filter.getStartViewNo());
			}else if(null != filter.getStartSeriesNo()&& StringUtils.isBlank(filter.getStartViewNo())
					&& null != filter.getEndSeriesNo()&& !StringUtils.isBlank(filter.getEndViewNo())){
				//只有开始场次为空时
				sql+=" and ((res.seriesNo >= ? and res.seriesNo<?) or (res.seriesNo=? and abs(res.viewNo) <=abs(?)) ) ";
				filterList.add(filter.getStartSeriesNo());
				filterList.add(filter.getEndSeriesNo());
				filterList.add(filter.getEndSeriesNo());
				filterList.add(filter.getEndViewNo());
			}else if(null != filter.getStartSeriesNo()&& StringUtils.isBlank(filter.getStartViewNo())
					&& null != filter.getEndSeriesNo()&& StringUtils.isBlank(filter.getEndViewNo())){
				//只输入开始结束集
				sql+=" and ((res.seriesNo between ? and ?) ) ";
				filterList.add(filter.getStartSeriesNo());
				filterList.add(filter.getEndSeriesNo());
			}else if(null != filter.getStartSeriesNo()&& !StringUtils.isBlank(filter.getStartViewNo())
					&& null == filter.getEndSeriesNo()&& StringUtils.isBlank(filter.getEndViewNo())){
				//只填写开始集和场
				sql+=" and ( (res.seriesNo = ? and abs(res.viewNo )>= abs(?)) or res.seriesNo > ? ) ";
				filterList.add(filter.getStartSeriesNo());
				filterList.add(filter.getStartViewNo());
				filterList.add(filter.getStartSeriesNo());
			}else if(null == filter.getStartSeriesNo()&& StringUtils.isBlank(filter.getStartViewNo())
					&& null != filter.getEndSeriesNo()&& !StringUtils.isBlank(filter.getEndViewNo())){
				//只填写结束集和场
				sql+=" and ((res.seriesNo = ? and abs(res.viewNo )<= abs(?)) or res.seriesNo < ? ) ";
				filterList.add(filter.getEndSeriesNo());
				filterList.add(filter.getEndViewNo());
				filterList.add(filter.getEndSeriesNo());
			}else if(null != filter.getStartSeriesNo()&& StringUtils.isBlank(filter.getStartViewNo())
					&& null == filter.getEndSeriesNo()&& StringUtils.isBlank(filter.getEndViewNo())){
				//只填写开始集
				sql+=" and (res.seriesNo >= ? ) ";
				filterList.add(filter.getStartSeriesNo());
			}else if(null == filter.getStartSeriesNo()&& StringUtils.isBlank(filter.getStartViewNo())
					&& null != filter.getEndSeriesNo()&& StringUtils.isBlank(filter.getEndViewNo())){
				//只填写结束集
				sql+=" and (res.seriesNo <= ? ) ";
				filterList.add(filter.getEndSeriesNo());
			}
			
			//内外景
			if(StringUtils.isNotBlank(filter.getSite())){
				String[] siteArr = filter.getSite().split(",");
				sql += " and ( ";
				for (int i = 0; i < siteArr.length; i++) {
					String site = siteArr[i];
					if (i == 0) {
						if("blank".equals(site) || "[空]".equals(site)){
							sql+=" res.site is null or res.site='' ";
						}else{
							sql+=" res.site = ? ";
							filterList.add(site);
						}
					} else {
						if("blank".equals(site) || "[空]".equals(site)){
							sql+=" or res.site is null or res.site='' ";
						}else{
							sql+=" or res.site = ?";
							filterList.add(site);
						}
					}
				}
				sql += " ) ";
				
			}
			
			//特殊提醒
			if(StringUtils.isNotBlank(filter.getSpecialRemind())){
				String[] specialArr = filter.getSpecialRemind().split(",");
				sql += " and ( ";
				for (int i = 0; i < specialArr.length; i++) {
					String specialRemind = specialArr[i];
					if (i == 0) {
						if("blank".equals(specialRemind) || "[空]".equals(specialRemind)){
							sql+=" res.specialRemind is null or res.specialRemind='' ";
						}else{
							sql+=" res.specialRemind = ? ";
							filterList.add(specialRemind);
						}
					} else {
						if("blank".equals(specialRemind) || "[空]".equals(specialRemind)){
							sql+=" or res.specialRemind is null or res.specialRemind='' ";
						}else{
							sql+=" or res.specialRemind = ?";
							filterList.add(specialRemind);
						}
					}
				}
				sql += " ) ";
				
			}
			
			//气氛
			if(StringUtils.isNotBlank(filter.getAtmosphere())){
				String[] atmosphereArr = filter.getAtmosphere().split(",");
				sql += " and ( ";
				for (int i = 0; i < atmosphereArr.length; i++) {
					String atmosphere = atmosphereArr[i];
					if (i == 0) {
						if("blank".equals(atmosphere)){
							sql+=" res.atmosphereId is null or res.atmosphereId='' ";
						}else{
							sql+=" res.atmosphereId = ? ";
							filterList.add(atmosphere);
						}
					} else {
						if("blank".equals(atmosphere)){
							sql+=" or res.atmosphereId is null or res.atmosphereId='' ";
						}else{
							sql+=" or res.atmosphereId = ?";
							filterList.add(atmosphere);
						}
					}
				}
				sql += " ) ";
				
			}
			
			if(StringUtils.isNotBlank(filter.getShootStatus())){
				sql+=" and res.shootStatus in ("+filter.getShootStatus()+")";
			}
			
			if(StringUtils.isNotBlank(filter.getMajor())){
				
				if (filter.getMajor().equals("blank")) {
					sql += " and not EXISTS (select 1 from tab_view_location_map tvlm, tab_view_location tvl where tvlm.viewId = res.viewId and tvlm.crewId=? and tvlm.locationId = tvl.locationId and tvl.locationType = 1)";
					filterList.add(crewId);
				} else {
					String[] majorArr = filter.getMajor().split(",");
					sql += " and (";
					for (int i = 0; i < majorArr.length; i++) {
						if (i == 0) {
							sql += " res.majorView = ?";
						} else {
							sql += " or res.majorView = ?";
						}
						filterList.add(majorArr[i]);
					}
					sql+= ")";
				}
			}
			
			if(StringUtils.isNotBlank(filter.getMinor())){
				if (filter.getMinor().equals("blank")) {
					sql += "and not EXISTS (select 1 from tab_view_location_map tvlm, tab_view_location tvl where tvlm.viewId = res.viewId and tvlm.crewId=? and tvlm.locationId = tvl.locationId and tvl.locationType = 2)";
					filterList.add(crewId);
				} else {
					String[] minorArr = filter.getMinor().split(",");
					sql += "and (";
					for (int i = 0; i < minorArr.length; i++) {
						if (i == 0) {
							sql+=" res.minorView = ?";
						} else {
							sql+=" or res.minorView = ?";
						}
						filterList.add(minorArr[i]);
					}
					sql+= ")";
				}
			}
			

			if(StringUtils.isNotBlank(filter.getThirdLevel())){
				if (filter.getThirdLevel().equals("blank")) {
					sql += "and not EXISTS (select 1 from tab_view_location_map tvlm, tab_view_location tvl where tvlm.viewId = res.viewId and tvlm.crewId=? and tvlm.locationId = tvl.locationId and tvl.locationType = 3)";
					filterList.add(crewId);
				} else {
					String[] thirdLevelArr = filter.getThirdLevel().split(",");
					sql += "and (";
					for (int i = 0; i < thirdLevelArr.length; i++) {
						if (i == 0) {
							sql+=" res.thirdLevelView = ?";
						} else {
							sql+=" or res.thirdLevelView = ?";
						}
						filterList.add(thirdLevelArr[i]);
					}
					sql+= ")";
				}
			}
			
			if(StringUtils.isNotBlank(filter.getMakeup())){
				if("blank".equals(filter.getMakeup())){
					sql+=" and (res.makeupId is null or res.makeupId ='' )";
				}else{
					
					String makeup = "'"+filter.getMakeup().replaceAll(",", "','")+"'";
					sql +=" and EXISTS ( select * from tab_view_goods_map tvmm where tvmm.viewId=res.viewId and tvmm.goodsId in ("+makeup+")) ";
				}
				
			}
			if(StringUtils.isNotBlank(filter.getClothes())){
				
				if("blank".equals(filter.getClothes())){
					sql+=" and (res.clothesId is null or res.clothesId ='' )";
				}else{
					
					String clothes = "'"+filter.getClothes().replaceAll(",", "','")+"'";
					sql +=" and EXISTS ( select * from tab_view_goods_map tvcm where tvcm.viewId=res.viewId and tvcm.goodsId in ("+clothes+")) ";
				}
				
			}
			if(StringUtils.isNotBlank(filter.getRoles())){
				boolean blankFlag = false;	//标识所选演员中是否有选择[空]的情况
				String[] roleIdArr = filter.getRoles().split(",");	
				String notBlankRoleIds = "";	//不包含[空]的情况下的所有选中的演员信息
				
				for (String roleId : roleIdArr) {
					if (roleId.equals("blank")) {
						blankFlag = true;
					} else {
						notBlankRoleIds += roleId + ",";
					}
				}
				
				//选择的角色同时存在的场次
				if(StringUtils.isBlank(filter.getSearchMode())||"0".equals(filter.getSearchMode())){
					//如果选择的演员中有非空的情况，执行以下逻辑
					if (!StringUtils.isBlank(notBlankRoleIds)) {
						notBlankRoleIds = notBlankRoleIds.substring(0, notBlankRoleIds.length());
						String roles = "'"+notBlankRoleIds.replaceAll(",", "','")+"'";
						sql +=" and EXISTS ( select tsrmRes.viewId from (select tsrm.viewId,group_concat(tsrm.viewRoleId) roleIdStr from tab_view_role_map tsrm where "
								+ " tsrm.viewRoleId in ("+roles+") group by tsrm.viewId ) tsrmRes where tsrmRes.viewId=res.viewId ";

						String[] roleArray = notBlankRoleIds.split(",");
						for(String roleId:roleArray){
							sql+=" and tsrmRes.roleIdStr like '%"+roleId+"%'";
						}
						sql+=" ) ";
					}
					
					//如果选择的演员中有[空]的情况，执行以下逻辑
					if (blankFlag) {
						sql += "and not EXISTS (select 1 from tab_view_role_map tvrm where tvrm.viewId = res.viewId and tvrm.crewId = ?)";
						filterList.add(crewId);
					}
					
				}else if("1".equals(filter.getSearchMode())){
					//选择存在即可的场次
					if (!StringUtils.isBlank(notBlankRoleIds)) {
						String roles = "'"+notBlankRoleIds.replaceAll(",", "','")+"'";
						sql +=" and EXISTS ( select * from tab_view_role_map tsrm where tsrm.viewId=res.viewId and tsrm.viewRoleId in ("+roles+")) ";
					}
					
					if (blankFlag) {
						sql += "and not EXISTS (select 1 from tab_view_role_map tvrm, tab_view_role tvr where tvrm.viewId = res.viewId and tvrm.crewId = ? and tvr.viewRoleId = tvrm.viewRoleId and tvr.viewRoleType = 1)";
						filterList.add(crewId);
					}
					
				}else if("2".equals(filter.getSearchMode())){
					//选择不同时出现的场次(正确逻辑为在存在即可的基础上过滤掉同时出现的场景)
					if (!StringUtils.isBlank(notBlankRoleIds)) {
						String roles = "'"+notBlankRoleIds.replaceAll(",", "','")+"'";
						sql +=" and EXISTS ( select * from tab_view_role_map tsrm where tsrm.viewId=res.viewId and tsrm.viewRoleId in ("+roles+")) ";
						
						sql +=" and not EXISTS ( select tsrmRes.viewId from (select tsrm.viewId,group_concat(tsrm.viewRoleId) roleIdStr from tab_view_role_map tsrm "
								+ " group by tsrm.viewId ) tsrmRes where tsrmRes.viewId=res.viewId ";

						String[] roleArray = notBlankRoleIds.split(",");
						for(String roleId:roleArray){
							sql+=" and tsrmRes.roleIdStr like '%"+roleId+"%'";
						}
						sql+=" ) ";
					}
					
					//空的情况不符合业务逻辑，不进行考虑
					
				}else if("3".equals(filter.getSearchMode())){
					if (!StringUtils.isBlank(notBlankRoleIds)) {
						//选择同时不出现的场次
						sql +=" and not EXISTS ( select tsrmRes.viewId from (select tsrm.viewId,group_concat(tsrm.viewRoleId) roleIdStr from tab_view_role_map tsrm "
								+ " group by tsrm.viewId ) tsrmRes where tsrmRes.viewId=res.viewId and (";

						String[] roleArray = notBlankRoleIds.split(",");
						for(String roleId:roleArray){
							sql+=" tsrmRes.roleIdStr like '%"+roleId+"%' or";
						}
						sql=sql.substring(0,sql.length()-2 );
						sql+=" ) ) ";
					}
					
					if (blankFlag) {
						sql += "and EXISTS (select 1 from tab_view_role_map tvrm, tab_view_role tvr where tvrm.viewId = res.viewId and tvrm.crewId= ? and tvr.viewRoleId = tvrm.viewRoleId and tvr.viewRoleType = 1)";
						filterList.add(crewId);
					}
				}
			}
			
			if(StringUtils.isNotBlank(filter.getProps())){
				
				if("blank".equals(filter.getProps())){
					sql +=" and not EXISTS ( select * from tab_view_goods_map tspm, tab_goods_info tpi where tspm.viewId=res.viewId and tspm.goodsId = tpi.id and tpi.goodsType = 0) ";
				}else{
					String props = "'"+filter.getProps().replaceAll(",", "','")+"'";
					sql +=" and EXISTS ( select * from tab_view_goods_map tspm where tspm.viewId=res.viewId and tspm.goodsId in ("+props+")) ";
				}
			}
			
			if(StringUtils.isNotBlank(filter.getSpecialProps())){
				
				if("blank".equals(filter.getSpecialProps())){
					sql +=" and not EXISTS ( select * from tab_view_goods_map tspm, tab_goods_info tpi where tspm.viewId=res.viewId and tspm.goodsId = tpi.id and tpi.goodsType = 1) ";
				}else{
					String getSpeicalProps = "'"+filter.getSpecialProps().replaceAll(",", "','")+"'";
					sql +=" and EXISTS ( select * from tab_view_goods_map tspm where tspm.viewId=res.viewId and tspm.goodsId in ("+getSpeicalProps+")) ";
				}
			}
			
			if(StringUtils.isNotBlank(filter.getGuest())){
				
				if("blank".equals(filter.getGuest())){
					sql +=" and not EXISTS (select 1 from tab_view_role_map tvrm, tab_view_role tvr where tvrm.viewId = res.viewId and tvrm.crewId=? and tvr.viewRoleId = tvrm.viewRoleId and tvr.viewRoleType = 2)";
					filterList.add(crewId);
				}else{
					String guest = "'"+filter.getGuest().replaceAll(",", "','")+"'";
					sql +=" and EXISTS ( select * from tab_view_role_map tsrm1 where tsrm1.viewId=res.viewId and tsrm1.viewRoleId in ("+guest+")) ";
				}
			}
			
			if(StringUtils.isNotBlank(filter.getMass())){
				
				if("blank".equals(filter.getMass())){
					sql +=" and not EXISTS (select 1 from tab_view_role_map tvrm, tab_view_role tvr where tvrm.viewId = res.viewId and tvrm.crewId=? and tvr.viewRoleId = tvrm.viewRoleId and tvr.viewRoleType = 3)";
					filterList.add(crewId);
				}else{
					String mass = "'"+filter.getMass().replaceAll(",", "','")+"'";
					sql +=" and EXISTS ( select * from tab_view_role_map tsrm2 where tsrm2.viewId=res.viewId and tsrm2.viewRoleId in ("+mass+")) ";
				}
			}
			
			if(StringUtils.isNotBlank(filter.getAdvert())){
				
				if("blank".equals(filter.getAdvert())){
					sql+=" and (res.advertId is null or res.advertId ='' )";
				}else{
					
					String adverts = "'"+filter.getAdvert().replaceAll(",", "','")+"'";
					sql +=" and EXISTS ( select 1 from tab_view_advert_map tvam where tvam.viewId=res.viewId and tvam.advertId in ("+adverts+")) ";
					
				}
			}
			
			if(StringUtils.isNotBlank(filter.getShootLocation())){
				String[] shootLocationArr = filter.getShootLocation().split(",");
				sql += " and ( ";
				for (int i = 0; i < shootLocationArr.length; i++) {
					String shootLocation = shootLocationArr[i];
					if (i == 0) {
						if ("blank".equals(shootLocation) || "[空]".equals(shootLocation)) {
							sql += "  res.shootLocation is null or res.shootLocation='' ";
						} else {
							sql += " res.shootLocationId = ? ";
							filterList.add(shootLocation);
						}
					} else {
						if ("blank".equals(shootLocation) || "[空]".equals(shootLocation)) {
							sql += " or res.shootLocation is null or res.shootLocation='' ";
						} else {
							sql += " or res.shootLocationId = ? ";
							filterList.add(shootLocation);
						}
					}
					
				}
				sql += " ) ";
			}
			
			//主要内容
			if (StringUtils.isNotBlank(filter.getMainContent())) {
				sql += " and res.mainContent like ?";
				filterList.add("%"+filter.getMainContent()+"%");
			}
			//备注
			if (StringUtils.isNotBlank(filter.getRemark())) {
				sql += " and res.remark like ?";
				filterList.add("%"+filter.getRemark()+"%");
			}
			
			sql+=" order by ";
			if(null != filter.getSortType()){
				if(1==filter.getSortType().intValue()){ //顺场
					sql+=" res.seriesNo asc, abs(res.viewNo) asc,res.viewNo asc";
				}else if(2==filter.getSortType().intValue()){ //分场
					sql+=" locationViewCount is null, res.locationViewCount desc, convert(res.mainLocation USING 'gbk'), convert(res.minorView USING 'gbk'),convert(res.thirdLevelView USING 'gbk'),res.seriesNo asc, abs(res.viewNo) asc,res.viewNo asc";
				}
			}
		}else{
			sql+=" order by seriesNo asc, abs(viewNo) asc,viewNo asc";
		}
		List<Map<String, Object>> list = this.query(sql,filterList.toArray(), page);
		return list;
		
	}
	
	
	/**
	 * 场景表查询汇总数据
	 * @param crewId
	 * @param filter
	 * @param groupField 分组字段 可以为空
	 * @param mathFieldName 要使用函数计算的字段
	 * @param functionMode 要使用的函数名称
	 * @return
	 */
	public List<Map<String, Object>> queryViewListStatistics(String crewId,ViewFilter filter,String groupField,String mathFieldName,String functionMode){
		
		List<Object> filterList = new ArrayList<Object>();
		filterList.add(crewId);
		filterList.add(crewId);
		filterList.add(crewId);
		String sql = "select ";
		
		if(StringUtils.isNotBlank(groupField)){
			sql+= "groupRes."+groupField+",";
		}
			sql+= functionMode+"(groupRes."+mathFieldName+") funResult "
			+ "from (select res.* from (select tsi.viewId,tsi.seriesNo,tsi.viewNo,tsi.specialRemind,tsi.season,"
			+ "tsi.atmosphereId,tsi.site,tsi.viewType,tsha.vname shootLocation,tsha.id shootLocationId,tsi.pageCount,"
			+" GROUP_CONCAT(distinct tsa.location) majorView, GROUP_CONCAT(distinct tsa.locationId) majorViewId, "
			+" GROUP_CONCAT(distinct tsa2.location) minorView, GROUP_CONCAT(distinct tsa2.locationId) minorViewId, "
			+" GROUP_CONCAT(distinct tsa3.location) thirdLevelView, GROUP_CONCAT(distinct tsa3.locationId) thirdLevelViewId,"
			+ " tsi.mainContent, tsi.remark, tsi.shotDate shootDate, tsi.shootStatus,"
			+ " GROUP_CONCAT( distinct tmi.id) makeupId,GROUP_CONCAT(distinct tmi.makeupName) makeupName "
			+ " ,GROUP_CONCAT(distinct tci.id) clothesId,GROUP_CONCAT(distinct tci.clothesName) clothesName"
			+" from tab_view_info tsi "
			+" left join tab_view_location_map tsam on tsam.viewId=tsi.viewId"
			+" left join tab_view_location tsa on tsa.locationId=tsam.locationId AND tsa.locationType = 1 "
			+" left join tab_view_location tsa2 on tsa2.locationId=tsam.locationId AND tsa2.locationType = 2 "
			+" left join tab_view_location tsa3 on tsa3.locationId=tsam.locationId AND tsa3.locationType = 3 ";
			/*if (null != filter) {
				if (StringUtils.isNotBlank(filter.getMajor())) {
					sql+=" 	AND tsa.locationType =1 ";
				}
				if (StringUtils.isNotBlank(filter.getMinor())) {
					sql+=" 	AND tsa.locationType =2 ";
				}
			}*/
//			+" left join tab_shoot_location tsha on tsha.shootLocationId=tsi.shootLocationId"
			sql+=" left join tab_sceneview_info tsha on tsha.id=tsi.shootLocationId"
			//+" left join tab_view_plan_map tsplm on tsplm.viewId=tsi.viewId "
			+"  LEFT JOIN ("
			+"		SELECT tgi.id,tgi.goodsName makeupName,tvgm.viewId"
			+"		FROM tab_goods_info tgi,tab_view_goods_map tvgm"
			+"		WHERE tgi.id = tvgm.goodsId AND tgi.goodsType = 2 AND tgi.crewId = ?"
			+"	) tmi ON tmi.viewId = tsi.viewId"
			+"  LEFT JOIN ("
			+"		SELECT tgi.id,tgi.goodsName clothesName,tvgm.viewId"
			+"		FROM tab_goods_info tgi,tab_view_goods_map tvgm"
			+"		WHERE tgi.id = tvgm.goodsId AND tgi.goodsType = 3 AND tgi.crewId = ?"
			+"	) tci ON tci.viewId = tsi.viewId"
			+" WHERE tsi.crewId=? ";
		sql+=" group by tsi.viewId,tsi.seriesNo,tsi.viewNo,tsi.season,tsi.atmosphereId,tsi.site,tsi.viewType,tsha.vname,"
				+ "tsi.pageCount,tsi.mainContent, tsi.remark, tsi.shotDate, tsi.shootStatus) res ";
		if(null != filter){
			
			sql+=" where 1=1 ";
			
			if(null != filter.getSeriesNo()){
				sql+=" and res.seriesNo=?";
				filterList.add(filter.getSeriesNo());
			}
			if(StringUtils.isNotBlank(filter.getViewNo())){
				sql+=" and res.viewno=?";
				filterList.add(filter.getViewNo());
			}
			
			if (!StringUtils.isBlank(filter.getSeriesViewNos())) {
				sql += " and ( ";
				String[] seriesViewNos = RegexUtils.regexSplitStr("(，|,|；|;)", filter.getSeriesViewNos());
				for (int i = 0; i < seriesViewNos.length; i++) {
					if (StringUtils.isBlank(seriesViewNos[i])) {
						continue;
					}
					String[] seriesViewArr = RegexUtils.regexSplitStr("(-|－|——)", seriesViewNos[i]);
					String seriesNo = seriesViewArr[0].trim();
					String viewNo = seriesViewArr[1].trim();
					if (seriesViewArr.length > 2) {
						for (int v = 2; v < seriesViewArr.length; v++) {
							viewNo += "-" + seriesViewArr[v];
						}
					}
					
					if (i == 0) {
						sql += "(seriesNo = ? and viewNo = ?)";
					} else {
						sql += "or (seriesNo = ? and viewNo = ?)";
					}
					filterList.add(seriesNo);
					filterList.add(viewNo);
				}
				sql += " ) ";
			}
			//开始和结束集场，全部填写时执行
			if(null != filter.getStartSeriesNo()&& !StringUtils.isBlank(filter.getStartViewNo())
					&& null != filter.getEndSeriesNo()&& !StringUtils.isBlank(filter.getEndViewNo())){
				
				if(filter.getStartSeriesNo().equals(filter.getEndSeriesNo())){
					//当开始结束集相同时
					sql+=" and (res.seriesNo = ? and abs(res.viewNo) >=abs(?) and abs(res.viewNo) <=abs(?)) ";
					filterList.add(filter.getStartSeriesNo());
					filterList.add(filter.getStartViewNo());
					filterList.add(filter.getEndViewNo());
				}else{
					sql+=" and ((res.seriesNo > ? and res.seriesNo<?) or (res.seriesNo=? and abs(res.viewNo) >=abs(?)) or (res.seriesNo=? and abs(res.viewNo) <=abs(?))) ";
					filterList.add(filter.getStartSeriesNo());
					filterList.add(filter.getEndSeriesNo());
					filterList.add(filter.getStartSeriesNo());
					filterList.add(filter.getStartViewNo());
					filterList.add(filter.getEndSeriesNo());
					filterList.add(filter.getEndViewNo());
				}
				
			}else if(null != filter.getStartSeriesNo()&& !StringUtils.isBlank(filter.getStartViewNo())
					&& null != filter.getEndSeriesNo()&& StringUtils.isBlank(filter.getEndViewNo())){
				//只有结束场次为空时
				sql+=" and ((res.seriesNo > ? and res.seriesNo<=?) or (res.seriesNo=? and abs(res.viewNo) >=abs(?)) ) ";
				filterList.add(filter.getStartSeriesNo());
				filterList.add(filter.getEndSeriesNo());
				filterList.add(filter.getStartSeriesNo());
				filterList.add(filter.getStartViewNo());
			}else if(null != filter.getStartSeriesNo()&& StringUtils.isBlank(filter.getStartViewNo())
					&& null != filter.getEndSeriesNo()&& !StringUtils.isBlank(filter.getEndViewNo())){
				//只有开始场次为空时
				sql+=" and ((res.seriesNo >= ? and res.seriesNo<?) or (res.seriesNo=? and abs(res.viewNo) <=abs(?)) ) ";
				filterList.add(filter.getStartSeriesNo());
				filterList.add(filter.getEndSeriesNo());
				filterList.add(filter.getEndSeriesNo());
				filterList.add(filter.getEndViewNo());
			}else if(null != filter.getStartSeriesNo()&& StringUtils.isBlank(filter.getStartViewNo())
					&& null != filter.getEndSeriesNo()&& StringUtils.isBlank(filter.getEndViewNo())){
				//只输入开始结束集
				sql+=" and ((res.seriesNo between ? and ?) ) ";
				filterList.add(filter.getStartSeriesNo());
				filterList.add(filter.getEndSeriesNo());
			}else if(null != filter.getStartSeriesNo()&& !StringUtils.isBlank(filter.getStartViewNo())
					&& null == filter.getEndSeriesNo()&& StringUtils.isBlank(filter.getEndViewNo())){
				//只填写开始集和场
				sql+=" and ( (res.seriesNo = ? and abs(res.viewNo )>= abs(?)) or res.seriesNo > ? ) ";
				filterList.add(filter.getStartSeriesNo());
				filterList.add(filter.getStartViewNo());
				filterList.add(filter.getStartSeriesNo());
			}else if(null == filter.getStartSeriesNo()&& StringUtils.isBlank(filter.getStartViewNo())
					&& null != filter.getEndSeriesNo()&& !StringUtils.isBlank(filter.getEndViewNo())){
				//只填写结束集和场
				sql+=" and ((res.seriesNo = ? and abs(res.viewNo )<= abs(?)) or res.seriesNo < ? ) ";
				filterList.add(filter.getEndSeriesNo());
				filterList.add(filter.getEndViewNo());
				filterList.add(filter.getEndSeriesNo());
			}else if(null != filter.getStartSeriesNo()&& StringUtils.isBlank(filter.getStartViewNo())
					&& null == filter.getEndSeriesNo()&& StringUtils.isBlank(filter.getEndViewNo())){
				//只填写开始集
				sql+=" and (res.seriesNo >= ? ) ";
				filterList.add(filter.getStartSeriesNo());
			}else if(null == filter.getStartSeriesNo()&& StringUtils.isBlank(filter.getStartViewNo())
					&& null != filter.getEndSeriesNo()&& StringUtils.isBlank(filter.getEndViewNo())){
				//只填写结束集
				sql+=" and (res.seriesNo <= ? ) ";
				filterList.add(filter.getEndSeriesNo());
			}
			
			if(StringUtils.isNotBlank(filter.getSite())){
				String[] siteArr = filter.getSite().split(",");
				sql += " and ( ";
				for (int i = 0; i < siteArr.length; i++) {
					String site = siteArr[i];
					if (i == 0) {
						if("blank".equals(site) || "[空]".equals(site)){
							sql+=" res.site is null or res.site='' ";
						}else{
							sql+=" res.site = ? ";
							filterList.add(site);
						}
					} else {
						if("blank".equals(site) || "[空]".equals(site)){
							sql+=" or res.site is null or res.site='' ";
						}else{
							sql+=" or res.site = ?";
							filterList.add(site);
						}
					}
				}
				sql += " ) ";
			}
			
			//特殊提醒
			if(StringUtils.isNotBlank(filter.getSpecialRemind())){
				String[] specialArr = filter.getSpecialRemind().split(",");
				sql += " and ( ";
				for (int i = 0; i < specialArr.length; i++) {
					String specialRemind = specialArr[i];
					if (i == 0) {
						if("blank".equals(specialRemind) || "[空]".equals(specialRemind)){
							sql+=" res.specialRemind is null or res.specialRemind='' ";
						}else{
							sql+=" res.specialRemind = ? ";
							filterList.add(specialRemind);
						}
					} else {
						if("blank".equals(specialRemind) || "[空]".equals(specialRemind)){
							sql+=" or res.specialRemind is null or res.specialRemind='' ";
						}else{
							sql+=" or res.specialRemind = ?";
							filterList.add(specialRemind);
						}
					}
				}
				sql += " ) ";
				
			}
			
			if(StringUtils.isNotBlank(filter.getAtmosphere())){
				String[] atmosphereArr = filter.getAtmosphere().split(",");
				sql += " and ( ";
				for (int i = 0; i < atmosphereArr.length; i++) {
					String atmosphere = atmosphereArr[i];
					if (i == 0) {
						if("blank".equals(atmosphere)){
							sql+=" res.atmosphereId is null or res.atmosphereId='' ";
						}else{
							sql+=" res.atmosphereId = ? ";
							filterList.add(atmosphere);
						}
					} else {
						if("blank".equals(atmosphere)){
							sql+=" or res.atmosphereId is null or res.atmosphereId='' ";
						}else{
							sql+=" or res.atmosphereId = ?";
							filterList.add(atmosphere);
						}
					}
				}
				sql += " ) ";
			}
			if(StringUtils.isNotBlank(filter.getSeason())){
				
				if("blank".equals(filter.getSeason())){
					sql+=" and ( res.season is null or res.season='' or res.season < 0 ) ";
				}else{
					sql+=" and res.season in("+filter.getSeason()+")";
				}
			}
			if(StringUtils.isNotBlank( filter.getViewType())){
				
				if("blank".equals(filter.getViewType())){
					sql+=" and ( res.viewType is null or res.viewType='' ) ";
				}else{
					sql+=" and res.viewType in ('"+filter.getViewType().replaceAll(",", "','")+"')";
				}
			}
			if(StringUtils.isNotBlank(filter.getShootStatus())){
				sql+=" and res.shootStatus in ("+filter.getShootStatus()+")";
				//filterList.add(filter.getShootStatus());
			}
			
			//查询主场景
			if(StringUtils.isNotBlank(filter.getMajor())){
				
				if (filter.getMajor().equals("blank")) {
					sql += "and not EXISTS (select 1 from tab_view_location_map tvlm, tab_view_location tvl where tvlm.viewId = res.viewId and tvlm.crewId=? and tvlm.locationId = tvl.locationId and tvl.locationType = 1)";
					filterList.add(crewId);
				} else {
					String[] majorArr = filter.getMajor().split(",");
					sql += " and (";
					for (int i = 0; i < majorArr.length; i++) {
						if (i == 0) {
							sql+=" res.majorView = ?";
						} else {
							sql+=" or res.majorView = ?";
						}
						filterList.add( majorArr[i] );
					}
					sql+= ")";
				}
			}
			
			//查询次场景
			if(StringUtils.isNotBlank(filter.getMinor())){
				if (filter.getMinor().equals("blank")) {
					sql += "and not EXISTS (select 1 from tab_view_location_map tvlm, tab_view_location tvl where tvlm.viewId = res.viewId and tvlm.crewId=? and tvlm.locationId = tvl.locationId and tvl.locationType = 2)";
					filterList.add(crewId);
				} else {
					String[] minorArr = filter.getMinor().split(",");
					sql += "and (";
					for (int i = 0; i < minorArr.length; i++) {
						if (i == 0) {
							sql+=" res.minorView = ?";
						} else {
							sql+=" or res.minorView = ?";
						}
						filterList.add( minorArr[i] );
					}
					sql+= ")";
				}
			}
			
			//查询三级场景
			if(StringUtils.isNotBlank(filter.getThirdLevel())){
				if (filter.getThirdLevel().equals("blank")) {
					sql += "and not EXISTS (select 1 from tab_view_location_map tvlm, tab_view_location tvl where tvlm.viewId = res.viewId and tvlm.crewId=? and tvlm.locationId = tvl.locationId and tvl.locationType = 3)";
					filterList.add(crewId);
				} else {
					String[] minorArr = filter.getThirdLevel().split(",");
					sql += "and (";
					for (int i = 0; i < minorArr.length; i++) {
						if (i == 0) {
							sql+=" res.thirdLevelView = ?";
						} else {
							sql+=" or res.thirdLevelView = ?";
						}
						filterList.add( minorArr[i] );
					}
					sql+= ")";
				}
			}
			
			if(StringUtils.isNotBlank(filter.getMakeup())){
				//sql+=" and res.makeupId like '%"+filter.getMakeup()+"%'";
				if("blank".equals(filter.getMakeup())){
					sql+=" and (res.makeupId is null or res.makeupId ='' )";
				}else{
					String[] makeupIdArray = filter.getMakeup().split(",");
					for(String makeupId:makeupIdArray){
						sql+=" and res.makeupId = '"+makeupId+"'";
					}
				}
			}
			
			if(StringUtils.isNotBlank(filter.getClothes())){
				
				if("blank".equals(filter.getClothes())){
					sql+=" and (res.clothesId is null or res.clothesId ='' )";
				}else{
					String[] clothesIdArray = filter.getClothes().split(",");
					for(String clothesId:clothesIdArray){
						sql+=" and res.clothesId = '"+clothesId+"'";
					}
				}
			}
			
			if(StringUtils.isNotBlank(filter.getRoles())){
				boolean blankFlag = false;	//标识所选演员中是否有选择[空]的情况
				String[] roleIdArr = filter.getRoles().split(",");	
				String notBlankRoleIds = "";	//不包含[空]的情况下的所有选中的演员信息
				
				for (String roleId : roleIdArr) {
					if (roleId.equals("blank")) {
						blankFlag = true;
					} else {
						notBlankRoleIds += roleId + ",";
					}
				}
				
				//选择的角色同时存在的场次
				if(StringUtils.isBlank(filter.getSearchMode())||"0".equals(filter.getSearchMode())){
					//如果选择的演员中有非空的情况，执行以下逻辑
					if (!StringUtils.isBlank(notBlankRoleIds)) {
						notBlankRoleIds = notBlankRoleIds.substring(0, notBlankRoleIds.length());
						String roles = "'"+notBlankRoleIds.replaceAll(",", "','")+"'";
						sql +=" and EXISTS ( select tsrmRes.viewId from (select tsrm.viewId,group_concat(tsrm.viewRoleId) roleIdStr from tab_view_role_map tsrm where "
								+ " tsrm.viewRoleId in ("+roles+") group by tsrm.viewId ) tsrmRes where tsrmRes.viewId=res.viewId ";

						String[] roleArray = notBlankRoleIds.split(",");
						for(String roleId:roleArray){
							sql+=" and tsrmRes.roleIdStr like '%"+roleId+"%'";
						}
						sql+=" ) ";
					}
					
					//如果选择的演员中有[空]的情况，执行以下逻辑
					if (blankFlag) {
						sql += "and not EXISTS (select 1 from tab_view_role_map tvrm where tvrm.viewId = res.viewId and tvrm.crewId = ?)";
						filterList.add(crewId);
					}
					
				}else if("1".equals(filter.getSearchMode())){
					//选择存在即可的场次
					if (!StringUtils.isBlank(notBlankRoleIds)) {
						String roles = "'"+notBlankRoleIds.replaceAll(",", "','")+"'";
						sql +=" and EXISTS ( select * from tab_view_role_map tsrm where tsrm.viewId=res.viewId and tsrm.viewRoleId in ("+roles+")) ";
					}
					
					if (blankFlag) {
						sql += "and not EXISTS (select 1 from tab_view_role_map tvrm, tab_view_role tvr where tvrm.viewId = res.viewId and tvrm.crewId = ? and tvr.viewRoleId = tvrm.viewRoleId and tvr.viewRoleType = 1)";
						filterList.add(crewId);
					}
					
				}else if("2".equals(filter.getSearchMode())){
					//选择不同时出现的场次(正确逻辑为在存在即可的基础上过滤掉同时出现的场景)
					if (!StringUtils.isBlank(notBlankRoleIds)) {
						String roles = "'"+notBlankRoleIds.replaceAll(",", "','")+"'";
						sql +=" and EXISTS ( select * from tab_view_role_map tsrm where tsrm.viewId=res.viewId and tsrm.viewRoleId in ("+roles+")) ";
						
						sql +=" and not EXISTS ( select tsrmRes.viewId from (select tsrm.viewId,group_concat(tsrm.viewRoleId) roleIdStr from tab_view_role_map tsrm "
								+ " group by tsrm.viewId ) tsrmRes where tsrmRes.viewId=res.viewId ";

						String[] roleArray = notBlankRoleIds.split(",");
						for(String roleId:roleArray){
							sql+=" and tsrmRes.roleIdStr like '%"+roleId+"%'";
						}
						sql+=" ) ";
					}
					
					//空的情况不符合业务逻辑，不进行考虑
				}else if("3".equals(filter.getSearchMode())){
					if (!StringUtils.isBlank(notBlankRoleIds)) {
						//选择同时不出现的场次
						sql +=" and not EXISTS ( select tsrmRes.viewId from (select tsrm.viewId,group_concat(tsrm.viewRoleId) roleIdStr from tab_view_role_map tsrm "
								+ " group by tsrm.viewId ) tsrmRes where tsrmRes.viewId=res.viewId and (";

						String[] roleArray = notBlankRoleIds.split(",");
						for(String roleId:roleArray){
							sql+=" tsrmRes.roleIdStr like '%"+roleId+"%' or";
						}
						sql=sql.substring(0,sql.length()-2 );
						sql+=" ) ) ";
					}
					
					if (blankFlag) {
						sql += "and EXISTS (select 1 from tab_view_role_map tvrm, tab_view_role tvr where tvrm.viewId = res.viewId and tvrm.crewId= ? and tvr.viewRoleId = tvrm.viewRoleId and tvr.viewRoleType = 1)";
						filterList.add(crewId);
					}
				}
			}
			
			if(StringUtils.isNotBlank(filter.getProps())){
				
				if("blank".equals(filter.getProps())){
					sql +=" and not EXISTS ( select * from tab_view_goods_map tspm, tab_goods_info tpi where tspm.viewId=res.viewId and tspm.goodsId = tpi.id and tpi.goodsType = 0) ";
				}else{
					String props = "'"+filter.getProps().replaceAll(",", "','")+"'";
					sql +=" and EXISTS ( select * from tab_view_goods_map tspm where tspm.viewId=res.viewId and tspm.goodsId in ("+props+")) ";
				}
			}
			
			if(StringUtils.isNotBlank(filter.getSpecialProps())){
				
				if("blank".equals(filter.getSpecialProps())){
					sql +=" and not EXISTS ( select * from tab_view_goods_map tspm, tab_goods_info tpi where tspm.viewId=res.viewId and tspm.goodsId = tpi.id and tpi.goodsType = 1) ";
				}else{
					String getSpeicalProps = "'"+filter.getSpecialProps().replaceAll(",", "','")+"'";
					sql +=" and EXISTS ( select * from tab_view_goods_map tspm where tspm.viewId=res.viewId and tspm.goodsId in ("+getSpeicalProps+")) ";
				}
			}
			
			if(StringUtils.isNotBlank(filter.getGuest())){
				
				if("blank".equals(filter.getGuest())){
					sql +=" and not EXISTS (select 1 from tab_view_role_map tvrm, tab_view_role tvr where tvrm.viewId = res.viewId and tvrm.crewId=? and tvr.viewRoleId = tvrm.viewRoleId and tvr.viewRoleType = 2)";
					filterList.add(crewId);
				}else{
					String guest = "'"+filter.getGuest().replaceAll(",", "','")+"'";
					sql +=" and EXISTS ( select * from tab_view_role_map tsrm1 where tsrm1.viewId=res.viewId and tsrm1.viewRoleId in ("+guest+")) ";
				}
			}
			
			if(StringUtils.isNotBlank(filter.getMass())){
				
				if("blank".equals(filter.getMass())){
					sql +=" and not EXISTS (select 1 from tab_view_role_map tvrm, tab_view_role tvr where tvrm.viewId = res.viewId and tvrm.crewId=? and tvr.viewRoleId = tvrm.viewRoleId and tvr.viewRoleType = 3)";
					filterList.add(crewId);
				}else{
					String mass = "'"+filter.getMass().replaceAll(",", "','")+"'";
					sql +=" and EXISTS ( select * from tab_view_role_map tsrm2 where tsrm2.viewId=res.viewId and tsrm2.viewRoleId in ("+mass+")) ";
				}
			}
			
			if(StringUtils.isNotBlank(filter.getAdvert())){
				
				if("blank".equals(filter.getAdvert())){
					sql+=" and (res.advertId is null or res.advertId ='' )";
				}else{
					String adverts = "'"+filter.getAdvert().replaceAll(",", "','")+"'";
					sql +=" and EXISTS ( select 1 from tab_view_advert_map tvam where tvam.viewId=res.viewId and tvam.advertId in ("+adverts+")) ";
					
				}
			}
			
			//主要内容
			if (StringUtils.isNotBlank(filter.getMainContent())) {
				sql += " and res.mainContent like ?";
				filterList.add("%"+filter.getMainContent()+"%");
			}
			//备注
			if (StringUtils.isNotBlank(filter.getRemark())) {
				sql += " and res.remark like ?";
				filterList.add("%"+filter.getRemark()+"%");
			}
			
			if(StringUtils.isNotBlank(filter.getShootLocation())){

				String[] shootLocationArr = filter.getShootLocation().split(",");
				sql += " and ( ";
				for (int i = 0; i < shootLocationArr.length; i++) {
					String shootLocation = shootLocationArr[i];
					if (i == 0) {
						if ("blank".equals(shootLocation) || "[空]".equals(shootLocation)) {
							sql += "  res.shootLocation is null or res.shootLocation='' ";
						} else {
							sql += " res.shootLocationId = ? ";
							filterList.add(shootLocation);
						}
					} else {
						if ("blank".equals(shootLocation) || "[空]".equals(shootLocation)) {
							sql += " or res.shootLocation is null or res.shootLocation='' ";
						} else {
							sql += " or res.shootLocationId = ? ";
							filterList.add(shootLocation);
						}
					}
					
				}
				sql += " ) ";
			/*
				
				if("blank".equals(filter.getShootLocation()) || "[空]".equals(filter.getShootLocation())){
					sql+=" and (res.shootLocation is null or res.shootLocation='' )"
				}else{
					sql+=" and res.shootLocation in ('"+filter.getShootLocation().replaceAll(",", "','")+"') ";
				}
				//filterList.add(filter.getShootLocation());*/
			}
			
		}
		
		sql+=" ) groupRes ";
		if(StringUtils.isNotBlank(groupField)){
			sql+=" group by groupRes."+groupField;
		}
			
		List<Map<String, Object>> list = this.query(sql,filterList.toArray(), null);
		
		return list;
		
	}
	
	/**
	 * 通告单场景表查询,只有已选择场景
	 * @param crewId
	 * @param page
	 * @param noticeId
	 * @return
	 */
	public List<Map<String, Object>> queryNoticeViewList(String crewId,String noticeId, String viewIds){
		
		List<Object> filterList = new ArrayList<Object>();
		filterList.add(crewId);
		filterList.add(crewId);
		filterList.add(crewId);
		
		String sql = "select res.shootLocationId, res.viewId, res.viewCount, res.seriesNo, res.viewNo,res.season, res.atmosphereId,res.site, res.viewType,res.shootRegion, res.shootLocation,res.vLongitude,"
				+ "res.vLatitude, if(tsnm.shootPage is not null, tsnm.shootPage, res.pageCount) shootPage, res.shootDate, res.majorView, res.minorView,res.thirdLevelView, res.viewRemark, res.mainContent, res.makeupId,res.makeupName,res.clothesId,res.clothesName,"
				+ "res.advertName,res.advertId, res.atmosphereName,  res.specialRemind,"
				+ "tsnm.shootStatus, tsnm.statusRemark remark, tsnm.tapNo, tsnm.prepareView prepareStatus,	res.pageCount "
				+ "from (select tsi.shootLocationId,tsi.viewId,count(tsi.viewId) viewCount,tsi.seriesNo,tsi.viewNo,tsi.season,tsi.specialRemind,"
				+ "tsi.atmosphereId,tsi.site,tsi.viewType,tsha.vCity shootRegion,tsha.vname shootLocation,tsha.vLongitude,tsha.vLatitude,tsi.pageCount,tsi.shotDate shootDate,"
					+"GROUP_CONCAT(DISTINCT tsa.location) majorView, "
					+" GROUP_CONCAT(DISTINCT tsa2.location) minorView, "
					+" GROUP_CONCAT(DISTINCT tsa3.location) thirdLevelView, "
					+ " tsi.remark viewRemark, tsi.mainContent, "
					+ " GROUP_CONCAT(distinct mtgi.id) makeupId,GROUP_CONCAT(distinct mtgi.goodsName) makeupName "
					+ " ,GROUP_CONCAT(distinct ctgi.id) clothesId,GROUP_CONCAT(distinct ctgi.goodsName) clothesName "
					+ " ,GROUP_CONCAT(distinct tia.advertName) advertName"
					+",GROUP_CONCAT(distinct tia.advertId) advertId "
					+ ",tai.atmosphereName "
				+" from tab_view_info tsi "
				+" left join tab_view_location_map tsam on tsam.viewId=tsi.viewId AND tsam.crewId = ?"
				+" left join tab_view_location tsa on tsa.locationId=tsam.locationId AND tsa.locationType = 1"
				+" 	LEFT JOIN tab_view_location tsa2 ON tsa2.locationId = tsam.locationId AND tsa2.locationType = 2 "
				+" LEFT JOIN tab_view_location tsa3 ON tsa3.locationId = tsam.locationId AND tsa3.locationType = 3"
				+" left join tab_sceneview_info tsha on tsha.id=tsi.shootLocationId AND tsha.crewId = ?"
				//+" left join tab_view_plan_map tsplm on tsplm.viewId=tsi.viewId "
				
				+" left join tab_view_goods_map tvgm on tvgm.viewId = tsi.viewId"
				+" left join tab_goods_info mtgi on mtgi.id = tvgm.goodsId and mtgi.goodsType = 2" //化妆
				+" left join tab_goods_info ctgi on ctgi.id = tvgm.goodsId and ctgi.goodsType = 3" //服装
				/*+" left join tab_view_makeup_map tvmm on tvmm.viewId=tsi.viewId "
				+" left join tab_makeup_info tmi on tmi.makeupId=tvmm.makeupId "
				+" left join tab_view_clothes_map tvcm on tvcm.viewId=tsi.viewId "
				+" left join tab_clothes_info tci on tci.clothesId=tvcm.clothesId "*/
				+" left join tab_view_advert_map tvam on tvam.viewId=tsi.viewId "
				+" left join tab_inside_advert tia on tia.advertId=tvam.advertId "
				+" left JOIN tab_atmosphere_info tai ON tsi.atmosphereId = tai.atmosphereId "
				+" WHERE tsi.crewId=? ";
		sql+=" group by tsi.viewId,tsi.seriesNo,tsi.viewNo,tsi.season,tsi.atmosphereId,tsi.site,tsi.viewType,tsha.vname,"
				+ "tsi.pageCount,tsi.shotDate,tsi.mainContent, tsi.tapNo) res,tab_view_notice_map tsnm ";
			
			sql+=" where res.viewId= tsnm.viewId";
			
			if(StringUtils.isNotBlank(noticeId)){
				sql+=" and tsnm.noticeId=? ";
				filterList.add(noticeId);
			}
			if (!StringUtils.isBlank(viewIds)) {
				viewIds = "'" + viewIds.replace(",", "','") + "'";
				sql += "and res.viewId in("+ viewIds +")";
			}
			
			sql+=" order by tsnm.sequence,seriesNo asc,abs(viewNo) asc";
			
		List<Map<String, Object>> list = this.query(sql,filterList.toArray(), null);
		
		return list;
		
	}
	
	/**
	 * 场景汇总场景表查询
	 * @param crewId
	 * @param shootLocationId 拍摄地ID
	 * @param locationId 主场景ID
	 * @param role 主要演员
	 * @param searchMode 查询模式
	 * @return
	 */
	public List<Map<String, Object>> queryViewListByMajorLocation(String crewId,String shootLocationId, String locationId, String role, String searchMode){
		
		List<Object> filterList = new ArrayList<Object>();
		filterList.add(crewId);
		filterList.add(crewId);
		filterList.add(crewId);

		StringBuilder sql = new StringBuilder("select tsi.shootLocationId,tsi.viewId,count(tsi.viewId) viewCount,tsi.seriesNo,tsi.viewNo,tsi.season,tsi.shootStatus,"
				+ "tsi.atmosphereId,tsi.site,tsi.viewType,tsha.vname shootLocation,tsha.vLongitude,tsha.vLatitude,tsi.pageCount,tsi.shotDate shootDate,"
				+ "GROUP_CONCAT(DISTINCT(tsa.location) order by tsa.locationType asc ) as viewAddress, tsi.specialRemind, "
				+ " GROUP_CONCAT(DISTINCT(tsa.locationId) order by tsa.locationType asc ) as viewAddressId,"
				+ " tsi.remark viewRemark, tsi.mainContent, "
				+ " GROUP_CONCAT(distinct mtgi.id) makeupId,GROUP_CONCAT(distinct mtgi.goodsName) makeupName "
				+ " ,GROUP_CONCAT(distinct ctgi.id) clothesId,GROUP_CONCAT(distinct ctgi.goodsName) clothesName "
				+ " ,GROUP_CONCAT(distinct tia.advertName) advertName"
				+ ",GROUP_CONCAT(distinct tia.advertId) advertId "
				+ ",tai.atmosphereName "
				+ " from tab_view_info tsi "
				+ " left join tab_view_location_map tsam on tsam.viewId=tsi.viewId AND tsam.crewId = ?"
				+ " left join tab_view_location tsa on tsa.locationId=tsam.locationId "
				+ " left join tab_sceneview_info tsha on tsha.id=tsi.shootLocationId AND tsha.crewId = ?"
				+ " left join tab_view_goods_map tvgm on tvgm.viewId = tsi.viewId"
				+ " left join tab_goods_info mtgi on mtgi.id = tvgm.goodsId and mtgi.goodsType = 2" // 化妆
				+ " left join tab_goods_info ctgi on ctgi.id = tvgm.goodsId and ctgi.goodsType = 3" // 服装
				+ " left join tab_view_advert_map tvam on tvam.viewId=tsi.viewId "
				+ " left join tab_inside_advert tia on tia.advertId=tvam.advertId "
				+ " left JOIN tab_atmosphere_info tai ON tsi.atmosphereId = tai.atmosphereId "
				+ " WHERE tsi.crewId=? ");
		if(StringUtils.isNotBlank(shootLocationId) && !"null".equals(shootLocationId)) {
			sql.append(" and tsha.id=? ");
			filterList.add(shootLocationId);
		} else {
			sql.append(" and tsha.id is null ");
		}
		if(StringUtils.isNotBlank(locationId) && !"null".equals(locationId)) {
			sql.append(" and tsa.locationId=? ");
			filterList.add(locationId);
		} else {
			sql.append(" and tsa.locationId is null ");
		}
		if(StringUtils.isNotBlank(role)){
			boolean blankFlag = false;	//标识所选演员中是否有选择[空]的情况
			String[] roleIdArr = role.split(",");	
			String notBlankRoleIds = "";	//不包含[空]的情况下的所有选中的演员信息
			
			for(int i = 0; i < roleIdArr.length; i++){
				String roleId = roleIdArr[i];
				if (roleId.equals("blank")) {
					blankFlag = true;
				} else {
					notBlankRoleIds += roleId;
					if(i < roleIdArr.length - 1) {
						notBlankRoleIds += ",";
					}
				}
			}
			
			//选择的角色同时存在的场次
			if(StringUtils.isBlank(searchMode)||"2".equals(searchMode)){
				//如果选择的演员中有非空的情况，执行以下逻辑
				if (!StringUtils.isBlank(notBlankRoleIds)) {
					notBlankRoleIds = notBlankRoleIds.substring(0, notBlankRoleIds.length());
					String roles = "'" + notBlankRoleIds.replaceAll(",", "','") + "'";
					sql.append(" and EXISTS ( select tsrmRes.viewId from ( ");
					sql.append(" select tsrm.viewId,group_concat(tsrm.viewRoleId) roleIdStr from tab_view_role_map tsrm ");
					sql.append(" where tsrm.viewRoleId in (" + roles + ") group by tsrm.viewId ) tsrmRes ");
					sql.append(" where tsrmRes.viewId=tvi.viewId ");

					String[] roleArray = notBlankRoleIds.split(",");
					for(String roleId : roleArray){
						sql.append(" and tsrmRes.roleIdStr like '%" + roleId + "%' ");
					}
					sql.append(" ) ");
				}
				
				//如果选择的演员中有[空]的情况，执行以下逻辑
				if (blankFlag) {
					sql.append(" and not EXISTS (select 1 from tab_view_role_map tvrm where tvrm.viewId = tsi.viewId and tvrm.crewId = ?) ");
					filterList.add(crewId);
				}
				
			}else if("0".equals(searchMode)){
				//选择存在即可的场次
				if (!StringUtils.isBlank(notBlankRoleIds)) {
					String roles = "'"+notBlankRoleIds.replaceAll(",", "','")+"'";
					sql.append(" and EXISTS ( select * from tab_view_role_map tsrm where tsrm.viewId=tsi.viewId and tsrm.viewRoleId in (" + roles + ")) ");
				}
				
				if (blankFlag) {
					sql.append(" and not EXISTS (select 1 from tab_view_role_map tvrm, tab_view_role tvr where tvrm.viewId = tsi.viewId and tvrm.crewId = ? and tvr.viewRoleId = tvrm.viewRoleId and tvr.viewRoleType = 1) ");
					filterList.add(crewId);
				}
				
			}else if("1".equals(searchMode)){//不出现
				if (!StringUtils.isBlank(notBlankRoleIds)) {
					//选择同时不出现的场次
					sql.append(" and not EXISTS ( select tsrmRes.viewId from (select tsrm.viewId,group_concat(tsrm.viewRoleId) roleIdStr from tab_view_role_map tsrm "
							+ " group by tsrm.viewId ) tsrmRes where tsrmRes.viewId=tsi.viewId and (" );

					String[] roleArray = notBlankRoleIds.split(",");
					for(int i = 0; i < roleArray.length; i++){
						String roleId = roleArray[i];
						sql.append(" tsrmRes.roleIdStr like '%" + roleId + "%' ");
						if(i < roleArray.length - 1) {
							sql.append(" or ");
						}
					}
					sql.append(" ) ) ");
				}
				
				if (blankFlag) {
					sql.append(" and EXISTS (select 1 from tab_view_role_map tvrm, tab_view_role tvr where tvrm.viewId = tsi.viewId and tvrm.crewId= ? and tvr.viewRoleId = tvrm.viewRoleId and tvr.viewRoleType = 1) ");
					filterList.add(crewId);
				}
			}
		}
		sql.append(" group by tsi.viewId,tsi.seriesNo,tsi.viewNo,tsi.season,tsi.atmosphereId,tsi.site,tsi.viewType,tsha.vname,"
				+ "tsi.pageCount,tsi.shotDate,tsi.mainContent, tsi.tapNo ");
		sql.append(" order by seriesNo asc,abs(viewNo) asc ");

		List<Map<String, Object>> list = this.query(sql.toString(), filterList.toArray(), null);

		return list;
	}
	
	/**
	 * 查询通告单列表视图中的场景
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	@Deprecated
	public List<Map<String, Object>> queryNoticeListTableView(String crewId,String noticeId){
		StringBuffer sql = new StringBuffer();
		sql.append("	SELECT res.*, tsnm.shootStatus,	tsnm.tapNo");
		sql.append("	FROM ( SELECT count(tsi.viewId) viewCount,tsi.viewId,");
		sql.append("	tsi.seriesNo,	tsi.viewNo,	tsi.season,	tsi.atmosphereId,	tsi.site,");
		sql.append("	tsha.shootLocation,	tsi.pageCount,	GROUP_CONCAT(DISTINCT (tsa.location)	ORDER BY	tsa.locationType ASC	SEPARATOR ' | ') AS majorView,");
		sql.append("	tsi.remark viewRemark,	tsi.mainContent,	GROUP_CONCAT(DISTINCT tmi.makeupName) makeupName,");
		sql.append("	GROUP_CONCAT(DISTINCT tci.clothesName) clothesName,	GROUP_CONCAT(DISTINCT tia.advertName) advertName,");
		sql.append("	tai.atmosphereName, GROUP_CONCAT(DISTINCT tvr1.viewRoleName) roleList,");
		sql.append("	GROUP_CONCAT(DISTINCT tvr2.viewRoleName) guestRoleList,	GROUP_CONCAT(DISTINCT tpi1.propsName) propsList,");
		sql.append("	GROUP_CONCAT(DISTINCT tpi2.propsName) specialPropsList");
		sql.append("	FROM	tab_view_info tsi");
		sql.append("	LEFT JOIN tab_view_location_map tsam ON tsam.viewId = tsi.viewId");
		sql.append("	LEFT JOIN tab_view_location tsa ON tsa.locationId = tsam.locationId");
		sql.append("	LEFT JOIN tab_shoot_location tsha ON tsha.shootLocationId = tsi.shootLocationId");
		sql.append("	LEFT JOIN tab_view_makeup_map tvmm ON tvmm.viewId = tsi.viewId");
		sql.append("	LEFT JOIN tab_makeup_info tmi ON tmi.makeupId = tvmm.makeupId");
		sql.append("	LEFT JOIN tab_view_clothes_map tvcm ON tvcm.viewId = tsi.viewId");
		sql.append("	LEFT JOIN tab_clothes_info tci ON tci.clothesId = tvcm.clothesId");
		sql.append("	LEFT JOIN tab_view_advert_map tvam ON tvam.viewId = tsi.viewId");
		sql.append("	LEFT JOIN tab_inside_advert tia ON tia.advertId = tvam.advertId");
		sql.append("	LEFT JOIN tab_atmosphere_info tai ON tsi.atmosphereId = tai.atmosphereId");
		sql.append("	LEFT JOIN tab_view_role_map tvrm ON tvrm.viewId = tsi.viewId");
		sql.append("	LEFT JOIN tab_view_role tvr1 ON tvr1.viewRoleId = tvrm.viewRoleId AND tvr1.viewRoleType = 1");
		sql.append("	LEFT JOIN tab_view_role tvr2 ON tvr2.viewRoleId = tvrm.viewRoleId AND tvr2.viewRoleType = 2");
		sql.append("	LEFT JOIN tab_view_props_map tvpm ON tvpm.viewId = tsi.viewId");
		sql.append("	LEFT JOIN tab_props_info tpi1 ON tpi1.propsId = tvpm.propsId AND tpi1.propsType =1");
		sql.append("	LEFT JOIN tab_props_info tpi2 ON tpi2.propsId = tvpm.propsId AND tpi2.propsType =2");
		sql.append("	WHERE	tsi.crewId =?");
		sql.append("	GROUP BY tsi.seriesNo,tsi.viewNo,tsi.season,tsi.atmosphereId,tsi.site,tsi.viewType,tsha.shootLocation,tsi.pageCount,tsi.shotDate,tsi.mainContent,tsi.tapNo");
		sql.append("		) res, tab_view_notice_map tsnm");
		sql.append("	WHERE res.viewId = tsnm.viewId");
		sql.append("	AND tsnm.noticeId =?");
		sql.append("	ORDER BY tsnm.sequence,seriesNo ASC,abs(viewNo) ASC ");
		
		return this.query(sql.toString(), new Object[] {crewId, noticeId}, null);
	}
	
	/**
	 * 根据拍摄计划ID查询计划下的场景完整信息
	 * 该方法不仅仅查询出场景表中的所有信息，
	 * 还查询出每个场景的计划拍摄时间、拍摄地点、主场景、次场景、三级场景、主要演员、特约演员、群众演员信息
	 * 查询效率可能有待考究
	 * @param planId 拍摄计划ID
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> queryFullViewInfoInPlan(String viewIds, String planId, String crewId, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT m.shootDate, tvi.*,");
		
		sql.append(" 	tsl.vname shootLocation, tai.atmosphereName");
		sql.append(" FROM " + ViewInfoModel.TABLE_NAME + " tvi ");
		
		sql.append(" LEFT JOIN " + SceneViewInfoModel.TABLE_NAME +" tsl ON tsl.id = tvi.shootLocationId ");
		sql.append(" LEFT JOIN " + AtmosphereInfoModel.TABLE_NAME + " tai ON  tai.atmosphereId = tvi.atmosphereId, ");
		sql.append(" tab_view_plan_map m ");
		sql.append(" WHERE  1 = 1 ");
		sql.append("   and tvi.crewId = ?");
		sql.append("   and tvi.viewId = m.viewId");
		sql.append("   and m.planId = ?");
		if (!StringUtils.isBlank(viewIds)) {
			viewIds = "'"+ viewIds.replace(",", "','") +"'";
			sql.append(" and tvi.viewId in (" + viewIds + ")");
		}
		sql.append(" order by m.shootDate, tvi.seriesNo, abs(tvi.viewNo), tvi.viewNo");
		
		List<Map<String, Object>> viewInfoList = this.query(sql.toString(),new Object[] {crewId, planId}, page);
		
		return viewInfoList;
	}
	
	/**
	 * 查询拍摄计划下的所有场景信息
	 * 该查询主要用户计算拍摄计划完成率、拍摄地点、场数、页数信息
	 * 带有子计划中的场景
	 * 查询简单，高效
	 * @param planId
	 * @return
	 */
	public List<Map<String, Object>> querySimpleViewInfoByPlanId(String planId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	DISTINCT tvi.*, tsl.vname shootLocation ");
		sql.append(" FROM ");
		sql.append(" 	tab_shootplan_info ptsi ");
		sql.append(" 	LEFT JOIN tab_shootplan_info ctsi ON ptsi.planId = ctsi.parentPlan, ");
		sql.append(" 	tab_view_plan_map tvpm, ");
		sql.append(" 	tab_view_info tvi ");
		sql.append(" 	LEFT JOIN tab_sceneview_info tsl ON tsl.id = tvi.shootLocationId ");
		sql.append(" WHERE ");
		sql.append(" 	(tvpm.planId = ptsi.planId or tvpm.planId = ctsi.planId) ");
		sql.append(" AND  ");
		sql.append(" 	ptsi.planId = ? ");
		sql.append(" AND tvi.viewId = tvpm.viewId ");
		
		List<Map<String, Object>> viewInfoList = this.query(sql.toString(), new Object[] {planId}, null);
		
		return viewInfoList;
	}
	
	/**
	 * 统计所有拍摄状态的场次
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryStatisticsShootStatus(String crewId){
		
		String sql = "select shootStatus,count(viewId) viewCount  from tab_view_info where crewId=? group by shootStatus";
		
		return this.query(sql, new Object[]{crewId}, null);
	}
	
	/**
	 * 统计剧本总页数
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryStatisticsPageCount(String crewId){
		
		String sql = "select SUM(pageCount) pageSum from tab_view_info where crewId=? ";
		
		return this.query(sql, new Object[]{crewId}, null);
	}
	
	/**
	 * 统计剧本总场数
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryStatisticsTotalCount(String crewId) {
		String sql = "select count(1) totalCount from " + ViewInfoModel.TABLE_NAME + " where crewId = ?";
		return this.query(sql, new Object[] {crewId}, null);
	}
	
	/**
	 * 按照拍摄组统计剧组下已加入拍摄计划的总场数和总页数
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryStatTCountAndPCountByGroup(String crewId) {
		StringBuilder sql  = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" tsg.groupName groupName, count(tvi.viewId) viewCount, sum(tvi.pageCount) pageCount ");
		sql.append(" FROM ");
		sql.append(" 	tab_view_info tvi, ");
		sql.append(" 	tab_view_plan_map tvpm, ");
		sql.append(" 	tab_shootplan_info tsi, ");
		sql.append(" 	tab_shoot_group tsg");
		sql.append(" WHERE");
		sql.append(" 	tvpm.viewId = tvi.viewId");
		sql.append(" AND tvpm.planId = tsi.planId");
		sql.append(" AND tvi.crewId = ?");
		sql.append(" AND tsi.groupId = tsg.groupId");
		sql.append(" group by tsi.groupId, tsg.groupName order by groupName");
		
		return this.query(sql.toString(), new Object[] {crewId}, null);
	}
	
	/**
	 * 统计场次类型场数
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryStatisticsType(String crewId){
		
		String sql = "select viewType,count(viewId) viewCount from tab_view_info where crewId=? group by viewType";
		
		return this.query(sql, new Object[]{crewId}, null);
	}
	
	/**
	 * 统计内外景场数
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryStatisticsSite(String crewId){
		
		String sql = "select site,count(viewId) viewCount from tab_view_info where crewId=? group by site";
		
		return this.query(sql, new Object[]{crewId}, null);
	}
	
	/**
	 * 更新场景拍摄地址
	 * @param viewId
	 * @param shootLocationId
	 */
	public void updateViewShootAddress(String viewId,String shootLocationId){
		
		String updateSql = "update tab_view_info set shootLocationId=? where viewId=?";
		
		this.getJdbcTemplate().update(updateSql, shootLocationId,viewId);
		
	}
	
	
	/**
	 * 更新场景状态
	 * @param viewId
	 * @param shootLocationId
	 * @throws ParseException 
	 */
	public void updateViewShootStatus(String viewIds,Integer shootStatus,String statusRemark,String tapNo, String noticeId, String shootDate) throws ParseException{
		
		String updateSql = "update tab_view_info set shootStatus=? ";
		
		List<Object> params = new ArrayList<Object>();
		if(null != shootStatus){
			params.add(shootStatus);
		}else{
			return;
		}
		if(StringUtils.isNotBlank(statusRemark)){
			params.add(statusRemark);
			updateSql +=" ,statusRemark=?";
		}
		if(StringUtils.isNotBlank(tapNo)){
			params.add(tapNo);
			updateSql +=" ,tapNo=?";
		}
		if (StringUtils.isNotBlank(noticeId)) {
			params.add(noticeId);
			updateSql += " ,noticeId=?";
		} else {
			updateSql += " ,noticeId=''";
		}
		if (StringUtils.isNotBlank(shootDate)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date shotDate = sdf.parse(shootDate);
			params.add(shotDate);
			updateSql += " ,shotDate=?";
		} else {
			updateSql += " ,shotDate=null";
		}
		
		updateSql+=" where viewId in ('"+viewIds.replaceAll(",", "','")+"')";
		
		this.getJdbcTemplate().update(updateSql,params.toArray());
		
	}
	
	/**
	 * 删除通告单时，设置含有此通告单ID的场景信息中对应字段为空
	 * @param noticeId
	 */
	public void deleteViewNoticeInfo(String noticeId){
		String sql = "update "+ ViewInfoModel.TABLE_NAME + " set noticeId = '' where noticeId = ?";
		
		this.getJdbcTemplate().update(sql, new Object[] {noticeId});
	}
	
	/**
	 * 查询剧组下的所有场景的集场号
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> querySeriesViewNoByCrewId(String crewId) {
		String sql = "select viewId, seriesNo, viewNo, isManualSave from " + ViewInfoModel.TABLE_NAME + " where crewId = ?";
		return this.query(sql, new Object[] {crewId}, null);
	}
	
	/**
	 * 查询剧组下所有的场景信息
	 * 该查询会查询出场景对应的内容
	 * @return
	 */
	public List<Map<String, Object>> queryAllViewInfoWithContent(String crewId, Integer seriesNo) {
		String sql = "select tvi.isManualSave, tvi.viewId, tvi.seriesNo, tvi.viewNo, tvc.title, tvc.content from "+ ViewInfoModel.TABLE_NAME +" tvi left join "+ ViewContentModel.TABLE_NAME +" tvc on tvi.viewId = tvc.viewId where tvi.crewId = ?";
		
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(crewId);
		if (seriesNo != null) {
			sql += " and tvi.seriesNo = ?";
			paramList.add(seriesNo);
		}
		return this.query(sql, paramList.toArray(), null);
	}
	
	/**
	 * 查询剧组下的所有集次
	 * @param crewId
	 * @return	集次，总场数，总页数，已完成场数，未完成场数
	 */
	public List<Map<String, Object>> querySeriesNoByCrewId(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	seriesNo, ");
		sql.append(" 	count(viewId) totalViewCount, ");
		sql.append(" 	round(sum(pageCount), 2) totalPageCount, ");
		sql.append(" 	sum(IF(shootStatus = 2, 1, 0)) finishedViewCount, ");
		sql.append(" 	sum(IF(shootStatus = 2, 0, 1)) unFinishedViewCount ");
		sql.append(" FROM ");
		sql.append(" 	tab_view_info ");
		sql.append(" WHERE ");
		sql.append(" 	crewId = ? ");
		sql.append(" AND shootStatus != 3 ");
		sql.append(" GROUP BY ");
		sql.append(" 	seriesNo ");
		sql.append(" ORDER BY seriesNo ");
		return this.query(sql.toString(), new Object[] {crewId}, null);
	}
	
	/**
	 * 查询含有指定角色的集次信息
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> querySeriesNoWithRoleInfo(String crewId, String roleIds) {
		roleIds = "'" + roleIds.replace(",", "','") + "'";
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tvi.seriesNo, ");
		sql.append(" 	count(tvi.viewNo) viewCount ");
		sql.append(" FROM ");
		sql.append(" 	tab_view_info tvi ");
		sql.append(" WHERE ");
		sql.append(" 	EXISTS ( ");
		sql.append(" 		SELECT ");
		sql.append(" 			1 ");
		sql.append(" 		FROM ");
		sql.append(" 			tab_view_role_map tvrm ");
		sql.append(" 		WHERE ");
		sql.append(" 			tvrm.viewId = tvi.viewId ");
		sql.append(" 		AND tvrm.viewRoleId IN ( ");
		sql.append(roleIds);
		sql.append(" 		) ");
		sql.append(" 		AND tvrm.crewId = ? ");
		sql.append(" 	) ");
		sql.append(" AND tvi.crewId = ? ");
		sql.append(" GROUP BY ");
		sql.append(" 	tvi.seriesNo ");
		
		return this.query(sql.toString(), new Object[] {crewId, crewId}, null);
	}
	
	/**
	 * 查询剧组指定集下的所有场次
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryViewNoByCrewIdAndSeriesNo(String crewId, String seriesNo) {
		String sql = "select viewId, seriesNo, viewNo, isManualSave from " + ViewInfoModel.TABLE_NAME + " where crewId = ? and seriesNo = ?";
		return this.query(sql, new Object[] {crewId, seriesNo}, null);
	}
	
	/**
	 * 查询剧组指定集下的含有指定角色的所有场次
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryViewNoBySeriesNoAndRoleInfo(String crewId, String seriesNo, String roleIds) {
		roleIds = "'" + roleIds.replace(",", "','") + "'";
		
		List<Object> paramsList = new ArrayList<Object>();
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tvi.viewId, tvi.seriesNo, tvi.viewNo, tvi.isManualSave ");
		sql.append(" FROM ");
		sql.append(" 	tab_view_info tvi, ");
		sql.append(" 	tab_view_role_map tvrm ");
		sql.append(" WHERE ");
		sql.append(" 	tvrm.viewId = tvi.viewId ");
		sql.append(" AND tvrm.viewRoleId IN ("+ roleIds +") ");
		sql.append(" AND tvrm.crewId = ? ");
		sql.append(" AND tvi.crewId = ? ");
		
		paramsList.add(crewId);
		paramsList.add(crewId);
		if (!StringUtils.isBlank(seriesNo)) {
			paramsList.add(seriesNo);
			sql.append(" AND tvi.seriesNo = ? ");
		}
		
		return this.query(sql.toString(), paramsList.toArray(), null);
	}
	
	
	/**
	 * 查询指定角色在指定通告单下拥有的戏的场次信息
	 * 该方法还会查出场次中所有的主要演员信息，以逗号隔开
	 * @param crewId
	 * @param noticeId
	 * @param roleIdList
	 * @return
	 */
	public List<Map<String, Object>> queryViewByNoticeRole(String crewId, String noticeId, List<String> roleIdList) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	* ");
		sql.append(" FROM ");
		sql.append(" 	( ");
		sql.append(" 		SELECT ");
		sql.append(" 			tvi.viewId, ");
		sql.append(" 			tvi.seriesNo, ");
		sql.append(" 			tvi.viewNo, ");
		sql.append("            tvnm.sequence, ");
		sql.append(" 			GROUP_CONCAT(tvr.viewRoleId) viewRoleIds, ");
		sql.append(" 			GROUP_CONCAT(tvr.viewRoleName) viewRoleNames ");
		sql.append(" 		FROM ");
		sql.append(" 			tab_view_info tvi, ");
		sql.append(" 			tab_view_role_map tvrm, ");
		sql.append(" 			tab_view_role tvr, ");
		sql.append(" 			tab_view_notice_map tvnm ");
		sql.append(" 		WHERE ");
		sql.append(" 			tvnm.viewId = tvi.viewId ");
		sql.append("        AND tvi.crewId = ?");
		sql.append(" 		AND tvrm.viewId = tvi.viewId ");
		sql.append(" 		AND tvr.viewRoleId = tvrm.viewRoleId ");
		sql.append(" 		AND tvr.viewRoleType = 1 ");
		sql.append(" 		AND tvnm.noticeId = ? ");
		sql.append(" 		GROUP BY ");
		sql.append(" 			tvi.viewId ");
		sql.append(" 	) t ");
		sql.append(" WHERE ");
		sql.append(" 	( ");
		for (int i = 0; i < roleIdList.size(); i++) {
			String roleId = roleIdList.get(i);
			if (i == 0) {
				sql.append(" 		t.viewRoleIds LIKE '%"+ roleId +"%' ");
			} else {
				sql.append(" 		OR t.viewRoleIds LIKE '%"+ roleId +"%' ");
			}
		}
		sql.append(" 	) order by sequence ");
		
		return this.query(sql.toString(), new Object[] {crewId, noticeId}, null);
	}
	
	/**
	 * 查询剧本中的场景信息
	 * 主要为了查询剧本的关键信息，包括：气氛  内外景  人物  场景  剧本内容
	 * @param crewId
	 * @param seriesNo 集次
	 * @return
	 */
	public List<Map<String, Object>> queryScenarioViewInfo(String crewId, Integer seriesNo, Page page, String viewId) {
		StringBuilder sql = new StringBuilder();
		List<Object> param = new ArrayList<Object>();
		sql.append(" SELECT ");
		sql.append(" 	tvi.viewId, ");
		sql.append(" 	tvi.seriesNo, ");
		sql.append(" 	tvi.viewNo, ");
		sql.append(" 	tvi.crewId, ");
		sql.append(" 	tvi.site, ");
		sql.append(" 	tai.atmosphereName, ");
		sql.append(" 	tvc.content, ");
		sql.append(" 	GROUP_CONCAT(DISTINCT tvr.viewRoleName) viewRoleNames, ");
		sql.append(" 	GROUP_CONCAT( ");
		sql.append(" 		DISTINCT tvl.location ");
		sql.append(" 		ORDER BY ");
		sql.append(" 			tvl.locationType ");
		sql.append(" 	) viewLocations ");
		sql.append(" FROM ");
		sql.append(" 	tab_view_info tvi ");
		sql.append(" LEFT JOIN tab_view_content tvc ON tvi.viewId = tvc.viewId ");
		sql.append(" LEFT JOIN tab_view_role_map tvrm ON tvrm.viewId = tvi.viewId ");
		sql.append(" LEFT JOIN tab_view_role tvr ON tvr.viewRoleId = tvrm.viewRoleId ");
		sql.append(" LEFT JOIN tab_view_location_map tvlm ON tvlm.viewId = tvi.viewId ");
		sql.append(" LEFT JOIN tab_view_location tvl ON tvl.locationId = tvlm.locationId ");
		sql.append(" LEFT JOIN tab_atmosphere_info tai ON tvi.atmosphereId = tai.atmosphereId ");
		sql.append(" WHERE ");
		sql.append(" 	tvi.crewId = ? ");
		param.add(crewId);
		if (seriesNo != null) {
			sql.append(" and tvi.seriesNo = " + seriesNo);
		}
		if (StringUtils.isNotBlank(viewId)) {
			sql.append(" and tvi.viewId = ? ");
			param.add(viewId);
		}
		sql.append(" GROUP BY ");
		sql.append(" 	tvi.viewId ");
		sql.append(" ORDER BY ");
		sql.append(" 	tvi.seriesNo, ABS(tvi.viewNo), tvi.viewNo ");
		
		return this.query(sql.toString(), param.toArray(), page);
	}
	
	/**
	 * 查询剧本中的场景信息
	 * @param crewId
	 * @return
	 */
	public int countScenarioViewInfo(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tvi.viewId, ");
		sql.append(" 	tvi.seriesNo, ");
		sql.append(" 	tvi.viewNo, ");
		sql.append(" 	tvi.crewId, ");
		sql.append(" 	tvi.site, ");
		sql.append(" 	tai.atmosphereName, ");
		sql.append(" 	tvc.content, ");
		sql.append(" 	GROUP_CONCAT(DISTINCT tvr.viewRoleName), ");
		sql.append(" 	GROUP_CONCAT( ");
		sql.append(" 		DISTINCT tvl.location ");
		sql.append(" 		ORDER BY ");
		sql.append(" 			tvl.locationType ");
		sql.append(" 	) ");
		sql.append(" FROM ");
		sql.append(" 	tab_view_info tvi ");
		sql.append(" LEFT JOIN tab_view_content tvc ON tvi.viewId = tvc.viewId ");
		sql.append(" LEFT JOIN tab_view_role_map tvrm ON tvrm.viewId = tvi.viewId ");
		sql.append(" LEFT JOIN tab_view_role tvr ON tvr.viewRoleId = tvrm.viewRoleId ");
		sql.append(" LEFT JOIN tab_view_location_map tvlm ON tvlm.viewId = tvi.viewId ");
		sql.append(" LEFT JOIN tab_view_location tvl ON tvl.locationId = tvlm.locationId ");
		sql.append(" LEFT JOIN tab_atmosphere_info tai ON tvi.atmosphereId = tai.atmosphereId ");
		sql.append(" WHERE ");
		sql.append(" 	tvi.crewId = ? ");
		sql.append(" GROUP BY ");
		sql.append(" 	tvi.viewId ");
		sql.append(" ORDER BY ");
		sql.append(" 	tvi.seriesNo, ABS(tvi.viewNo), tvi.viewNo ");
		
		return this.getResultCount(sql.toString(), new Object[] {crewId});
	}
	
	
	/**
	 * 查询剧本中的场景信息
	 * 只查询气氛、拍摄地主要字段，没排序，排序将在代码中进行
	 * @param crewId
	 * @param seriesNo 集次
	 * @return
	 */
	public List<Map<String, Object>> queryViewList(String crewId, Integer seriesNo) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tvi.viewId, ");
		sql.append(" 	tvi.seriesNo, ");
		sql.append(" 	tvi.viewNo, ");
		sql.append(" 	tvi.crewId, ");
		sql.append("    tvi.isManualSave, ");
		sql.append(" 	tvi.site, ");
		sql.append(" 	tvi.shootStatus, ");
		sql.append(" 	tvi.pageCount, ");
		sql.append(" 	tvi.viewType, ");
		sql.append(" 	tai.atmosphereName, ");
		sql.append(" 	GROUP_CONCAT( ");
		sql.append(" 		DISTINCT tvl.location ");
		sql.append(" 		ORDER BY ");
		sql.append(" 			tvl.locationType ");
		sql.append(" 	) viewLocations ");
		sql.append(" FROM ");
		sql.append(" 	tab_view_info tvi ");
		sql.append(" LEFT JOIN tab_view_location_map tvlm ON tvlm.viewId = tvi.viewId ");
		sql.append(" LEFT JOIN tab_view_location tvl ON tvl.locationId = tvlm.locationId ");
		sql.append(" LEFT JOIN tab_atmosphere_info tai ON tvi.atmosphereId = tai.atmosphereId ");
		sql.append(" WHERE ");
		sql.append(" 	tvi.crewId = ? ");
		if (seriesNo != null) {
			sql.append(" and tvi.seriesNo = " + seriesNo);
		}
		sql.append(" GROUP BY ");
		sql.append(" 	tvi.viewId ");
		
		return this.query(sql.toString(), new Object[] {crewId}, null);
	}
	
	/**
	 * 根据场景ID查询场景信息
	 * 多个场景ID用英文逗号隔开
	 * 该查询会查询出对应的临时销场信息
	 * @param crewId
	 * @param viewIds
	 * @return
	 */
	public List<Map<String, Object>> queryViewListByViewIds(String crewId, String viewIds) {
		viewIds = "'" + viewIds.replace(",", "','") + "'";
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tvi.viewId, ");
		sql.append(" 	tvi.seriesNo, ");
		sql.append(" 	tvi.viewNo, ");
		sql.append(" 	tvi.crewId, ");
		sql.append(" 	tvi.site, ");
		sql.append("    tvi.shotDate,");
		sql.append(" 	tai.atmosphereName, ");
		sql.append("    tti.shootStatus, ");
		sql.append("    tti.remark, ");
		sql.append("    tti.tapNo, ");
		sql.append(" 	GROUP_CONCAT( ");
		sql.append(" 		DISTINCT tvl.location ");
		sql.append(" 		ORDER BY ");
		sql.append(" 			tvl.locationType ");
		sql.append(" 	) viewLocations ");
		sql.append(" FROM ");
		sql.append(" 	tab_view_info tvi");
		sql.append(" LEFT JOIN tab_view_location_map tvlm ON tvlm.viewId = tvi.viewId ");
		sql.append(" LEFT JOIN tab_view_location tvl ON tvl.locationId = tvlm.locationId ");
		sql.append(" LEFT JOIN tab_atmosphere_info tai ON tvi.atmosphereId = tai.atmosphereId ");
		sql.append(" LEFT JOIN tab_tmpCancelView_info tti ON tvi.viewId = tti.viewId ");
		sql.append(" WHERE ");
		sql.append(" 	tvi.crewId = ? ");
		sql.append("    and tvi.viewId in("+ viewIds +")");
		sql.append(" GROUP BY tvi.viewId ");
		
		return this.query(sql.toString(), new Object[] {crewId}, null);
	}
	
	
	/**
	 * 根据集场号列表查询剧组下的场景信息
	 * 该查询会查询出对应的临时销场信息
	 * @param crewId
	 * @param seriesViewNoList
	 * @return
	 */
	public List<Map<String, Object>> queryTmpCanBySeriesViewNoList(String crewId, List<String> seriesViewNoList) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tvi.viewId, ");
		sql.append(" 	tvi.seriesNo, ");
		sql.append(" 	tvi.viewNo, ");
		sql.append(" 	tvi.crewId, ");
		sql.append(" 	tvi.site, ");
		sql.append("    tvi.shotDate,");
		sql.append(" 	tai.atmosphereName, ");
		sql.append("    tti.shootStatus, ");
		sql.append("    tti.remark, ");
		sql.append("    tti.tapNo, ");
		sql.append(" 	GROUP_CONCAT( ");
		sql.append(" 		DISTINCT tvl.location ");
		sql.append(" 		ORDER BY ");
		sql.append(" 			tvl.locationType ");
		sql.append(" 	) viewLocations ");
		sql.append(" FROM ");
		sql.append(" 	tab_view_info tvi");
		sql.append(" LEFT JOIN tab_view_location_map tvlm ON tvlm.viewId = tvi.viewId ");
		sql.append(" LEFT JOIN tab_view_location tvl ON tvl.locationId = tvlm.locationId ");
		sql.append(" LEFT JOIN tab_atmosphere_info tai ON tvi.atmosphereId = tai.atmosphereId ");
		sql.append(" LEFT JOIN tab_tmpCancelView_info tti ON tvi.seriesNo = tti.seriesNo and tvi.viewNo = tti.viewNo ");
		sql.append(" WHERE ");
		sql.append(" 	tvi.crewId = ? ");
		sql.append("    AND ( ");
		for (int i = 0, len = seriesViewNoList.size(); i < len; i++) {
			String[] arr = seriesViewNoList.get(i).split("-");
			int seriesNo = Integer.parseInt(arr[0]);
			String viewNo = arr[1];
			
			if (i == 0) {
				sql.append(" 	(tvi.seriesNo = "+ seriesNo +" AND tvi.viewNo = '"+ viewNo +"') ");
			} else {
				sql.append(" 	or (tvi.seriesNo = "+ seriesNo +" AND tvi.viewNo = '"+ viewNo +"') ");
			}
		}
		sql.append(" ) ");
		sql.append(" GROUP BY tvi.viewId ");
		
		return this.query(sql.toString(), new Object[] {crewId}, null);
	}
	
	/**
	 * 根据通告单ID查询通告单下的场景信息
	 * 该查询会查询出通告单下场景的销场信息
	 * @param crewId
	 * @param viewIds
	 * @return
	 */
	public List<Map<String, Object>> queryNoticeViewList(String crewId, String noticeId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tvi.viewId, ");
		sql.append(" 	tvi.seriesNo, ");
		sql.append(" 	tvi.viewNo, ");
		sql.append(" 	tvi.crewId, ");
		sql.append(" 	tvi.site, ");
		sql.append("    tvi.shotDate,");
		sql.append(" 	tai.atmosphereName, ");
		sql.append("    tvnm.shootStatus, ");
		sql.append("    tvnm.statusRemark, ");
		sql.append("    tvnm.tapNo, ");
		sql.append(" 	GROUP_CONCAT( ");
		sql.append(" 		DISTINCT tvl.location ");
		sql.append(" 		ORDER BY ");
		sql.append(" 			tvl.locationType ");
		sql.append(" 	) viewLocations ");
		sql.append(" FROM ");
		sql.append(" 	tab_view_notice_map tvnm, tab_view_info tvi ");
		sql.append(" LEFT JOIN tab_view_location_map tvlm ON tvlm.viewId = tvi.viewId ");
		sql.append(" LEFT JOIN tab_view_location tvl ON tvl.locationId = tvlm.locationId ");
		sql.append(" LEFT JOIN tab_atmosphere_info tai ON tvi.atmosphereId = tai.atmosphereId ");
		sql.append(" WHERE ");
		sql.append(" 	tvi.crewId = ? ");
		sql.append("    and tvi.viewId = tvnm.viewId ");
		sql.append("    and tvnm.noticeId = ? ");
		sql.append("    GROUP BY tvnm.viewId ");
		sql.append("    ORDER BY tvnm.sequence ");
		
		return this.query(sql.toString(), new Object[] {crewId, noticeId}, null);
	}
	
	/**
	 * 根据集场号列表查询剧组下的场景信息
	 * @param crewId
	 * @param seriesViewNoList
	 * @return
	 */
	public List<ViewInfoModel> queryBySeriesViewNoList(String crewId, List<String> seriesViewNoList) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	* ");
		sql.append(" FROM ");
		sql.append(" 	tab_view_info ");
		sql.append(" WHERE ");
		sql.append(" 	crewId = ? ");
		sql.append(" AND ( ");
		for (int i = 0, len = seriesViewNoList.size(); i < len; i++) {
			String[] arr = seriesViewNoList.get(i).split("-");
			int seriesNo = Integer.parseInt(arr[0]);
			String viewNo = arr[1];
			
			if (i == 0) {
				sql.append(" 	(seriesNo = "+ seriesNo +" AND viewNo = '"+ viewNo +"') ");
			} else {
				sql.append(" 	or (seriesNo = "+ seriesNo +" AND viewNo = '"+ viewNo +"') ");
			}
		}
		sql.append(" ) ");
		
		return this.query(sql.toString(), new Object[] {crewId}, ViewInfoModel.class, null);
	}
	
	/**
	 * 查询剧组下待发布的场景信息
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryTopublishViewInfo(String crewId) {
		String sql = "SELECT tvi.*, tvc.status contentStatus from tab_view_content tvc, tab_view_info tvi where tvi.viewId = tvc.viewId and tvc.`status` in(1, 2) and tvc.crewId=? and tvi.crewId=?";
		return this.query(sql, new Object[] {crewId, crewId}, null);
	}
	
	/**
	 * 修改场景的基本信息（多个场景）
	 * @param crewId
	 * @param seriesViewNos	集场号列表，集、场以中划线隔开
	 * @param filedName	字段名
	 * @param fileValue	字段值
	 */
	public void updateManyBaseInfo(String crewId, List<String> viewIds, String filedName, Object filedValue) {
		StringBuilder sql = new StringBuilder();
		sql.append("update " + ViewInfoModel.TABLE_NAME + " set " + filedName + "= ? where crewId = ?");
		
		sql.append(" and (");
		for (int i = 0; i < viewIds.size(); i++) {
			String viewId = viewIds.get(i);
			if (i == 0) {
				sql.append("(viewId = '" + viewId + "')");
			} else {
				sql.append(" or (viewId = '" + viewId + "')");
			}
			
		}
		sql.append(")");
		
		this.getJdbcTemplate().update(sql.toString(), new Object[] {filedValue, crewId});
	}
	
	/**
	 * 更新场景的页数
	 * @param pageCount
	 * @param viewId
	 */
	public void updatePageCountById(double pageCount, String viewId) {
		String sql = "update tab_view_info set pageCount = ? where viewId = ?";
		this.getJdbcTemplate().update(sql, new Object[] {pageCount, viewId});
	}
	
	/**
	 * 查询指定角色的场景信息
	 * 
	 * 可扩展返回参数
	 * @param crewId
	 * @param viewRoleId
	 * @return	角色名称、演员名称、场景ID、集次、拍摄状态、页数、主场景ID、主场景名称、文武戏类别、拍摄地ID、拍摄地类型
	 */
	public List<Map<String, Object>> queryRoleViewList(String crewId, String viewRoleId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tvr.viewRoleName, ");
		sql.append(" 	tai.actorName, ");
		sql.append(" 	tvi.viewId, ");
		sql.append(" 	tvi.seriesNo, ");
		sql.append(" 	tvi.shootStatus, ");
		sql.append(" 	tvi.shotDate, ");
		sql.append(" 	tvi.site, ");
		sql.append(" 	tvi.viewType, ");
		sql.append(" 	round(tvi.pageCount, 2) pageCount, ");
		sql.append(" 	taai.atmosphereName, ");
		sql.append(" 	tsl.id shootLocationId, ");
		sql.append(" 	tsl.vname shootLocation, ");
		sql.append(" 	GROUP_CONCAT(tvl.locationId) locationId, ");
		sql.append(" 	GROUP_CONCAT(tvl.location) location, ");
		sql.append("	tvr.isAttentionRole,");
		sql.append("	tai.workHours,");
		sql.append("	tai.restHours");
		sql.append(" FROM ");
		sql.append(" 	tab_view_role tvr ");
		sql.append(" LEFT JOIN tab_actor_role_map tarm ON tarm.viewRoleId = tvr.viewRoleId AND tarm.crewId = ? ");
		sql.append(" LEFT JOIN tab_actor_info tai ON tai.actorId = tarm.actorId AND tai.crewId = ? ");
		sql.append(" LEFT JOIN tab_view_role_map tvrm ON tvrm.viewRoleId = tvr.viewRoleId AND tvrm.crewId = ? ");
		sql.append(" LEFT JOIN tab_view_info tvi ON tvi.viewId = tvrm.viewId AND tvi.crewId = ? ");
		sql.append(" LEFT JOIN tab_view_location_map tvlm ON tvlm.viewId = tvi.viewId AND tvlm.crewId = ? ");
		sql.append(" LEFT JOIN tab_view_location tvl ON tvl.locationId = tvlm.locationId AND tvl.crewId = ? AND tvl.locationType = 1 ");
		sql.append(" LEFT JOIN tab_sceneview_info tsl ON tsl.id = tvi.shootLocationId ");
		sql.append(" LEFT JOIN tab_atmosphere_info taai ON taai.atmosphereId = tvi.atmosphereId ");
		sql.append(" WHERE ");
		sql.append(" 	tvr.viewRoleId = ? ");
		sql.append(" GROUP BY  ");
		sql.append(" tvr.viewRoleName, ");
		sql.append(" 	tai.actorName, ");
		sql.append(" 	tvi.viewId, ");
		sql.append(" 	tvi.seriesNo, ");
		sql.append(" 	tvi.shootStatus, ");
		sql.append(" 	tvi.statusUpdateTime, ");
		sql.append(" 	tvi.site, ");
		sql.append(" 	tvi.viewType, ");
		sql.append(" 	tvi.pageCount, ");
		sql.append(" 	taai.atmosphereName, ");
		sql.append(" 	tsl.id, ");
		sql.append(" 	tsl.vname  ");
		
		return this.query(sql.toString(), new Object[] {crewId, crewId, crewId, crewId, crewId, crewId, viewRoleId}, null);
	}
	
	/**
	 * 查询剧组中场景总的统计信息
	 * 排除删戏的场次
	 * @param crewId
	 * @return	总的需要拍摄的场景数（去除删戏的数量），已完成场数，未完成场数，已完成页数
	 */
	public Map<String, Object> queryViewTotalStatistic(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	count(tvi.viewId) totalViewCount, ");
		sql.append(" 	sum(IF(tvi.shootStatus = 2, 1, 0)) finishedViewCount, ");
		sql.append(" 	sum(IF(tvi.shootStatus != 2, 1, 0)) unfinishedViewCount, ");
		sql.append(" 	round(ifnull(sum(tvi.pageCount),0), 2) totalPageCount, ");
		sql.append("    round(sum(IF(tvi.shootStatus = 2 AND tvi.pageCount is not NULL, tvi.pageCount, 0)), 2) finishedPageCount, ");
		sql.append("    sum(IF(tvi.shootStatus != 2, tvi.pageCount, 0)) unfinishedPageCount ");
		sql.append(" FROM ");
		sql.append(" 	tab_view_info tvi ");
		sql.append(" WHERE ");
		sql.append(" 	tvi.crewId = ? ");
		sql.append(" AND tvi.shootStatus != 3 ");
		
		return this.getJdbcTemplate().queryForMap(sql.toString(), crewId);
	}
	
	
	/**
	 * 根据通告单的日期查询当前场景中所有请假的演员的信息列表
	 * @param noticeDate 通告单的发布是日期
	 * @param viewRoleId 场景角色id
	 * @return
	 */
	public List<Map<String, Object>> queryActorLeaveRecordByViewRoleId(Date noticeDate, String viewRoleId){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT DISTINCT(tar.actorId),tar.id,tar.leaveDays,tar.leaveReason,tar.leaveStartDate,tar.leaveEndDate,tai.actorName,tvr.viewRoleName ");
		sql.append(" FROM tab_actor_leave_record tar");
		sql.append(" LEFT JOIN tab_actor_role_map tamp ON tar.actorId = tamp.actorId");
		sql.append(" LEFT JOIN tab_actor_info tai ON tar.actorId = tai.actorId");
		sql.append(" LEFT JOIN tab_view_role tvr ON tvr.viewRoleId = tamp.viewRoleId");
		sql.append(" WHERE tamp.viewRoleId = ?");
		sql.append(" AND DATE_FORMAT(?, '%Y-%m-%d') BETWEEN DATE_FORMAT(leaveStartDate, '%Y-%m-%d') AND DATE_FORMAT(leaveEndDate, '%Y-%m-%d')");
		
		return this.query(sql.toString(), new Object[] {viewRoleId, noticeDate}, null);
	}
	
	/**
	 * 根据场景id获取当前场景中的角色id
	 * @param viewId
	 * @return
	 */
	public List<Map<String, Object>> queryRoleIdByViewId(String viewId){
		String sql = "select tvrm.viewRoleId from tab_view_role_map tvrm where tvrm.viewId = ?";
		return this.query(sql, new Object[]{viewId}, null);
	}

	
	
	/**
	 * 查询剧组下的主场景名称列表(去除已经被被配置的主场景名称)
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryMainSceneName(String crewId,String mainSceneName){
		List<String> args = new ArrayList<String>();
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("    tvl.locationid, ");
		sql.append("    tvl.location ");
		sql.append(" FROM ");
		sql.append("    tab_view_info tvi ");
		sql.append(" LEFT JOIN tab_view_location_map tvlm ON tvi.viewid = tvlm.viewid ");
		sql.append(" LEFT JOIN tab_view_location tvl ON tvl.locationid = tvlm.locationid ");
		sql.append(" AND tvl.locationtype = 1 ");
		sql.append(" WHERE ");
		sql.append("    tvi.crewid = ?  ");
		args.add(crewId);
		if(StringUtils.isNotBlank(mainSceneName)){
			mainSceneName = "%"+mainSceneName+"%";
			sql.append("  and   tvl.location like ?  ");
			args.add(mainSceneName);
		}
		sql.append(" and not EXISTS (select locationid from tab_sceneview_viewinfo_map tsvm where tvlm.locationId = tsvm.locationId) ");
		sql.append(" GROUP BY ");
		sql.append("    tvl.locationid ");
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql.toString(),  args.toArray());
		return list;
	}
	
	
	
	
	/**
	 * 分集汇总
	 * @param crewId
	 * @return 集数、场数	、页数、(夜外)场数、比重、页数、比重	、(夜内)场数、比重、页数、比重、(日外)场数、比重、页数、比重、(日内)场数、比重、页数、比重
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> querySeriesnoTotalInfo(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select concat('第',seriesNo,'集') seriesNo,viewNum,finishedViewNum,pageNum,finishedPageNum finishedpageNum, ");
		sql.append(" 	nightoutView,ifnull(nightoutView/viewNum*100,0) noViewPer, ");
		sql.append(" 	nightoutPage,ifnull(nightoutPage/pageNum*100,0) noPagePer, ");
		sql.append(" 	nightView,ifnull(nightView/viewNum*100,0) nViewPer, ");
		sql.append(" 	nightPage,ifnull(nightPage/pageNum*100,0) nPagePer, ");
		sql.append(" 	dayoutView,ifnull(dayoutView/viewNum*100,0) doViewPer, ");
		sql.append(" 	dayoutPage,ifnull(dayoutPage/pageNum*100,0) doPagePer, ");
		sql.append(" 	dayView,ifnull(dayView/viewNum*100,0) dViewPer, ");
		sql.append(" 	dayPage,ifnull(dayPage/pageNum*100,0) dPagePer ");
		sql.append(" FROM( ");
		sql.append(" 	select tvi.seriesNo,count(viewId) viewNum,count(if(tvi.shootStatus in (2,3),viewId,null)) finishedViewNum, ");
		sql.append(" 		ifnull(sum(pageCount),0) pageNum, ifnull(sum(if(tvi.shootStatus in (2,3),pageCount,0)),0) finishedPageNum,");
		sql.append(" 		ifnull(sum(if(tai.atmosphereName like '%夜%' and tvi.site like '%外%',1,0)),0) nightoutView, ");
		sql.append(" 		ifnull(sum(if(tai.atmosphereName like '%夜%' and tvi.site like '%外%',pageCount,0)),0) nightoutPage, ");
		sql.append(" 		ifnull(sum(if(tai.atmosphereName like '%夜%' and (tvi.site not like '%外%' or tvi.site is null),1,0)),0) nightView, ");
		sql.append(" 		ifnull(sum(if(tai.atmosphereName like '%夜%' and (tvi.site not like '%外%' or tvi.site is null),pageCount,0)),0) nightPage, ");
		sql.append(" 		ifnull(sum(if((tai.atmosphereName not like '%夜%' or tai.atmosphereName is null) and tvi.site like '%外%',1,0)),0) dayoutView, ");
		sql.append(" 		ifnull(sum(if((tai.atmosphereName not like '%夜%' or tai.atmosphereName is null) and tvi.site like '%外%',pageCount,0)),0) dayoutPage, ");
		sql.append(" 		ifnull(sum(if((tai.atmosphereName not like '%夜%' or tai.atmosphereName is null) and (tvi.site not like '%外%' or tvi.site is null),1,0)),0) dayView, ");
		sql.append(" 		ifnull(sum(if((tai.atmosphereName not like '%夜%' or tai.atmosphereName is null) and (tvi.site not like '%外%' or tvi.site is null),pageCount,0)),0) dayPage ");
		sql.append(" 	FROM tab_view_info tvi ");
		sql.append(" 	LEFT JOIN tab_atmosphere_info tai on tai.atmosphereId=tvi.atmosphereId ");
		sql.append(" 	where tvi.crewId = ? ");
		sql.append(" 	group by tvi.seriesNo ");
		sql.append(" 	order by seriesNo ");
		sql.append(" ) mytable ");
		return this.query(sql.toString(), new Object[] {crewId}, null);
	}
	
	/**
	 * 分集汇总--合计、平均
	 * @param crewId
	 * @return 集数、场数	、页数、(夜外)场数、比重、页数、比重	(夜景)、场数、比重、页数、比重、(日景)场数、比重、页数、比重、其他
	 */
	public Map<String, Object> queryTotalAverageInfo(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select viewNum,round(ifnull(viewNum/num,0),0) viewAvg, ");
		sql.append(" 	finishedViewNum,round(ifnull(finishedViewNum/num,0),0) finishedViewAvg, ");
		sql.append(" 	pageNum,ifnull(pageNum/num,0) pageAvg, ");
		sql.append(" 	finishedPageNum,ifnull(finishedPageNum/num,0) finishedPageAvg, ");
		sql.append(" 	nightoutView,round(ifnull(nightoutView/num,0),0) nightoutViewAvg, ");
		sql.append(" 	ifnull(nightoutView/viewNum*100,0) noViewPer, ");
		sql.append(" 	nightoutPage, ");
		sql.append(" 	ifnull(nightoutPage/num,0) nightoutPageAvg, ");
		sql.append(" 	ifnull(nightoutPage/pageNum*100,0) noPagePer, ");
		sql.append(" 	nightView,round(ifnull(nightView/num,0),0) nightViewAvg, ");
		sql.append(" 	ifnull(nightView/viewNum*100,0) nViewPer, ");
		sql.append(" 	nightPage, ");
		sql.append(" 	ifnull(nightPage/num,0) nightPageAvg, ");
		sql.append(" 	ifnull(nightPage/pageNum*100,0) nPagePer, ");
		sql.append(" 	dayoutView,round(ifnull(dayoutView/num,0),0) dayoutViewAvg, ");
		sql.append(" 	ifnull(dayoutView/viewNum*100,0) doViewPer, ");
		sql.append(" 	dayoutPage, ");
		sql.append(" 	ifnull(dayoutPage/num,0) dayoutPageAvg, ");
		sql.append(" 	ifnull(dayoutPage/pageNum*100,0) doPagePer, ");
		sql.append(" 	dayView,round(ifnull(dayView/num,0),0) dayViewAvg, ");
		sql.append(" 	ifnull(dayView/viewNum*100,0) dViewPer, ");
		sql.append(" 	dayPage, ");
		sql.append(" 	ifnull(dayPage/num,0) dayPageAvg, ");
		sql.append(" 	ifnull(dayPage/pageNum*100,0) dPagePer ");
		sql.append(" FROM( ");
		sql.append(" select count(distinct seriesNo) num,count(viewId) viewNum,count(if(tvi.shootStatus in (2,3),viewId,null)) finishedViewNum, ");
		sql.append(" 	ifnull(sum(pageCount),0) pageNum,ifnull(sum(if(tvi.shootStatus in (2,3),pageCount,0)),0) finishedPageNum, ");
		sql.append(" 	ifnull(sum(if(tai.atmosphereName like '%夜%' and tvi.site like '%外%',1,0)),0) nightoutView, ");
		sql.append(" 	ifnull(sum(if(tai.atmosphereName like '%夜%' and tvi.site like '%外%',pageCount,0)),0) nightoutPage, ");
		sql.append(" 	ifnull(sum(if(tai.atmosphereName like '%夜%' and (tvi.site not like '%外%' or tvi.site is null),1,0)),0) nightView, ");
		sql.append(" 	ifnull(sum(if(tai.atmosphereName like '%夜%' and (tvi.site not like '%外%' or tvi.site is null),pageCount,0)),0) nightPage, ");
		sql.append(" 	ifnull(sum(if((tai.atmosphereName not like '%夜%' or tai.atmosphereName is null) and tvi.site like '%外%',1,0)),0) dayoutView, ");
		sql.append(" 	ifnull(sum(if((tai.atmosphereName not like '%夜%' or tai.atmosphereName is null) and tvi.site like '%外%',pageCount,0)),0) dayoutPage, ");
		sql.append(" 	ifnull(sum(if((tai.atmosphereName not like '%夜%' or tai.atmosphereName is null) and (tvi.site not like '%外%' or tvi.site is null),1,0)),0) dayView, ");
		sql.append(" 	ifnull(sum(if((tai.atmosphereName not like '%夜%' or tai.atmosphereName is null) and (tvi.site not like '%外%' or tvi.site is null),pageCount,0)),0) dayPage ");
		sql.append(" FROM tab_view_info tvi ");
		sql.append(" LEFT JOIN tab_atmosphere_info tai on tai.atmosphereId=tvi.atmosphereId ");
		sql.append(" where tvi.crewId=? ");
		sql.append(" ) mytable ");
		return this.getJdbcTemplate().queryForMap(sql.toString(), new Object[] {crewId});
	}
	
	/**
	 * 查询剧组中场景的总体信息，总集数、总场次、总页数
	 * @param crewId
	 * @return
	 */
	public Map<String, Object> queryViewTotalInfo(String crewId) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select count(distinct tvi.seriesNo) totalSeriesNo, ");
		sql.append(" 	count(viewId) totalViewCount,ifnull(sum(tvi.pageCount),0) totalPageCount ");
		sql.append(" FROM tab_view_info tvi ");
		sql.append(" where tvi.crewId=? ");
		return this.getJdbcTemplate().queryForMap(sql.toString(), new Object[] {crewId});
	}
	
	/**
	 * 修改场景表状态时，同时修改通告单中的额场景的状态
	 * @param viewId
	 */
	public void updateViewNoticeMapStatus(Integer status, String viewIds ) {
		String viewId = "'"+ viewIds.replace(",", "','") +"'";
		String sql = " UPDATE tab_view_notice_map SET shootStatus = ? WHERE viewId in ("+ viewId +")";
		this.getJdbcTemplate().update(sql, status);
	}
	
	/**
	 * 按照拍摄地分组，查询每个拍摄地下的场景统计信息
	 * @return	拍摄地Id，拍摄地名称，总场数，已拍摄场数，总页数，已拍摄页数
	 */
	public List<Map<String, Object>> queryViewStatisticGroupByShootLocation(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tsi.id shootLocationId, ");
		sql.append(" 	tsi.vName shootLocation, ");
		sql.append(" 	sum(if(tvi.shootStatus != 3, 1, 0)) totalViewCount, ");
		sql.append(" 	sum(if(tvi.shootStatus = 2, 1, 0)) shootedViewCount, ");
		sql.append(" 	ROUND(sum(if(tvi.shootStatus != 3, tvi.pageCount, 0)), 2) totalPageNum, ");
		sql.append(" 	ROUND(sum(if(tvi.shootStatus = 2, tvi.pageCount, 0)), 2) shootedPageNum ");
		sql.append(" FROM ");
		sql.append(" 	tab_sceneview_info tsi, ");
		sql.append(" 	tab_view_info tvi ");
		sql.append(" WHERE ");
		sql.append(" 	tvi.shootLocationId = tsi.id ");
		sql.append(" AND tvi.crewId = ? ");
		sql.append(" GROUP BY ");
		sql.append(" 	tsi.id, ");
		sql.append(" 	tsi.vName ");
		sql.append(" ORDER BY totalViewCount DESC, shootLocation; ");
		
		return this.query(sql.toString(), new Object[] {crewId}, null);
	}
	
	/**
	 * 查询剧组中场景信息列表，用于统计总体进度
	 * 去掉删戏
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryTotalViewInfo(String crewId) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT tai.atmosphereName,tvi.site,tvi.viewType,tvi.shootStatus,tvi.pageCount ");
//		sql.append(" 	,GROUP_CONCAT(DISTINCT tsg.groupName SEPARATOR ',') groupNames ");
		sql.append(" FROM tab_view_info tvi ");
		sql.append(" LEFT JOIN tab_atmosphere_info tai ");
		sql.append(" ON tvi.atmosphereId=tai.atmosphereId ");
//		sql.append(" LEFT JOIN tab_view_notice_map tvnm ");
//		sql.append(" ON tvi.viewId=tvnm.viewId");
//		sql.append(" LEFT JOIN tab_notice_info tni ");
//		sql.append(" ON tvi.noticeId=tni.noticeId ");
//		sql.append(" LEFT JOIN tab_shoot_group tsg ");
//		sql.append(" ON tni.groupId=tsg.groupId ");
		sql.append(" WHERE tvi.crewId=? ");
		sql.append(" and tvi.shootStatus!=3 "); //去掉删戏
		sql.append(" GROUP BY tvi.viewId ");
		
		return this.query(sql.toString(), new Object[]{crewId}, null);
	}
	
	/**
	 * 按照拍摄地分组，查询每个拍摄地下的场景统计信息,包括未设置拍摄地（设为待定）
	 * @return	拍摄地Id，拍摄地名称，总场数，已拍摄场数，总页数，已拍摄页数
	 */
	public List<Map<String, Object>> queryShootLocationProduction(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select tsi.id as shootLocationId,ifnull(tsi.vName,'待定') shootLocation, ");
		sql.append(" 	count(if(tvi.viewId is null,null,1)) viewCount, ");
		sql.append(" 	count(if(tvi.viewId is null or tvi.shootStatus!=2,null,1)) finishedViewCount, ");
		sql.append(" 	round(ifnull(sum(tvi.pageCount),0),2) pageCount, ");
		sql.append(" 	round(ifnull(sum(if(tvi.viewId is null or tvi.shootStatus!=2,null,tvi.pageCount)),0),2) finishedPageCount ");
		sql.append(" from tab_view_info tvi ");
//		sql.append(" LEFT JOIN tab_shoot_location tsl ");
//		sql.append(" ON tvi.shootLocationId=tsl.shootLocationId ");
		sql.append(" left join tab_sceneview_info tsi on tvi.shootLocationId=tsi.id ");
		sql.append(" WHERE tvi.crewId=? ");
		sql.append(" and tvi.shootStatus!=3 ");
//		sql.append(" GROUP BY (tvi.shootLocationId is null or tvi.shootLocationId=''),tsl.shootLocation ");
		sql.append(" GROUP BY tsi.id,tsi.vName ");
		sql.append(" ORDER BY viewCount desc ");
		
		return this.query(sql.toString(), new Object[] {crewId}, null);
	}
	
	/**
	 * 查询场景角色的场景统计信息，包括主要演员、特约演员
	 * @return	场景角色Id，场景角色名称，总场数，已拍摄场数，总页数，已拍摄页数
	 */
	public List<Map<String, Object>> queryViewRoleProduction(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select tvr.viewRoleId,tvr.viewRoleName,tvr.viewRoleType, ");
		sql.append(" 	count(if(tvi.viewId is null,null,1)) viewCount, ");
		sql.append(" 	count(if(tvi.viewId is null or tvi.shootStatus!=2,null,1)) finishedViewCount,  ");
		sql.append(" 	round(ifnull(sum(tvi.pageCount),0),2) pageCount, ");
		sql.append(" 	round(ifnull(sum(if(tvi.viewId is null or shootStatus!=2,null,tvi.pageCount)),0),2) finishedPageCount ");
		sql.append(" from tab_view_role tvr ");
		sql.append(" left join tab_view_role_map tvrm ");
		sql.append(" ON tvrm.viewRoleId=tvr.viewRoleId ");
		sql.append(" left join tab_view_info tvi ");
		sql.append(" ON tvi.viewId=tvrm.viewId and tvi.shootStatus!=3 ");
		sql.append(" WHERE tvr.crewId=? ");
		sql.append(" AND tvr.viewRoleType in (1,2) ");
		sql.append(" group by tvr.viewRoleId,tvr.viewRoleName,tvr.viewRoleType ");
		sql.append(" order by viewRoleType,viewCount desc ");
		return this.query(sql.toString(), new Object[] {crewId}, null);
	}
	
	/**
	 * 查询出所有季节和文武特效不为空的场景场景
	 * @return
	 */
	public List<ViewInfoModel> queryAllViewInfo(){
		String sql = "select * from " + ViewInfoModel.TABLE_NAME + " WHERE season in(1,2,3,4) OR viewType in(1,2,3) GROUP BY viewId";
		return this.query(sql, null, ViewInfoModel.class, null);
	}
}
