package com.xiaotu.makeplays.shoot.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.crewPicture.model.CrewPictureInfoModel;
import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;
import com.xiaotu.makeplays.shoot.model.ScheduleGroupModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.RegexUtils;
import com.xiaotu.makeplays.view.controller.filter.ViewFilter;

@Repository
public class ScheduleDao extends BaseDao<ScheduleGroupModel>{
	
	/**
	 * 查询计划分组列表
	 * @param crewId 剧组ID
	 * @param groupName 分组名称
	 * @return
	 */
	public List<Map<String, Object>> queryScheduleGroupList(String crewId, String groupName) {
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		params.add(crewId);
		params.add(crewId);
		params.add(crewId);
		params.add(crewId);
		params.add(crewId);
		sql.append(" select * from (");
		sql.append(" (select '0' groupId,'未分组' groupName,-1 sequence,now() as createTime, ");
		sql.append(" 	min(tvsm.shootDate) startDate,max(tvsm.shootDate) endDate,  ");
		sql.append(" 	count(tvi.viewId) viewCount,sum(tvi.pageCount) pageCount,  ");
		sql.append(" 	count(distinct tvsm.shootDate) dayCount,sum(tvi.pageCount)/count(distinct tvsm.shootDate) everyDayPage  ");
		sql.append(" from tab_view_info tvi ");
		sql.append(" left join tab_view_schedulegroup_map tvsm on tvsm.viewId=tvi.viewId and tvsm.crewId=? ");
		sql.append(" where tvi.crewId=? ");
		sql.append(" and tvsm.planGroupId is null)  ");
		sql.append(" UNION ALL ");
		sql.append(" (select tsg.id groupId,tsg.groupName,tsg.sequence,tsg.createTime, ");
		sql.append(" 	min(tvsm.shootDate) startDate,max(tvsm.shootDate) endDate,  ");
		sql.append(" 	count(tvi.viewId) viewCount,sum(tvi.pageCount) pageCount,  ");
		sql.append(" 	count(distinct tvsm.shootDate) dayCount,sum(tvi.pageCount)/count(distinct tvsm.shootDate) everyDayPage  ");
		sql.append(" from tab_schedule_group tsg ");
		sql.append(" left join tab_view_schedulegroup_map tvsm on tvsm.planGroupId=tsg.id and tvsm.crewId=? ");
		sql.append(" left join tab_view_info tvi on tvi.viewId=tvsm.viewId and tvi.crewId=? ");
		sql.append(" where tsg.crewId=? ");
		if(StringUtils.isNotBlank(groupName)) {
			groupName = groupName.replaceAll("_", "\\\\_");
			groupName = groupName.replaceAll("%", "\\\\%");
			sql.append(" and groupName like ? ");
			params.add("%" + groupName + "%");
		}
		sql.append(" group by tsg.id) ");
		sql.append(" ) mytable order by sequence,createTime desc");
		return this.query(sql.toString(), params.toArray(), null);
	}
	
	/**
	 * 根据id查询出计划分组的详细信息
	 * @param crewId
	 * @param groupId
	 * @return
	 * @throws Exception 
	 */
	public ScheduleGroupModel queryScheduleGroupById(String crewId, String groupId) throws Exception {
		String sql = " select * from "+ ScheduleGroupModel.TABLE_NAME +" where crewId = ? and id = ?";
		return this.queryForObject(sql, new Object[] {crewId, groupId}, ScheduleGroupModel.class);
	}
	
	/**
	 * 更新计划分组排序
	 * @param groupIds
	 */
	public void updateScheduleGroupSequence(String crewId, String groupIds){
		List<Object[]> paramList = new ArrayList<Object[]>();
		String[] groupIdArray = groupIds.split(",");
		for (int i = 0; i < groupIdArray.length; i++) {
			paramList.add(new Object[]{i + 1, groupIdArray[i], crewId});
		}
		String sql = "update " + ScheduleGroupModel.TABLE_NAME + " set sequence = ? where id = ? and crewId = ?";
		this.getJdbcTemplate().batchUpdate(	sql, paramList);
	}
	
	/**
	 * 查询分组最大序号
	 * @param crewId
	 * @return
	 * @throws Exception
	 */
	public int queryMaxSequence(String crewId) throws Exception {
		String sql = "select max(sequence) sequence from " + ScheduleGroupModel.TABLE_NAME + " where crewId=?";
		List<Map<String, Object>> result = this.query(sql, new Object[]{crewId}, null);
		int sequence = 0;
		if(result != null && result.size() > 0 && (Integer) result.get(0).get("sequence") != null) {
			sequence = (Integer) result.get(0).get("sequence");
		}
		return sequence;
	}
	
	/**
	 * 查询场景列表，用于智能整理
	 * 去掉已锁定场景
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryViewListForAutoSchedule(String crewId) {
		String sql = " select tsi.viewId,ifnull(tsha.vcity,'') shootRegion,ifnull(tsha.vname,'') shootLocation, " 
				+ " ifnull(GROUP_CONCAT(distinct tsa.location),'') majorView,ifnull(GROUP_CONCAT(distinct tsa2.location),'') minorView "
				+ " from tab_view_info tsi "
				+ " left join tab_sceneview_info tsha on tsha.id=tsi.shootLocationId "
				+ " left join tab_view_location_map tsam on tsam.viewId=tsi.viewId "
				+ " left join tab_view_location tsa on tsa.locationId=tsam.locationId and tsa.locationType=1 "
				+ " left join tab_view_location tsa2 on tsa2.locationId=tsam.locationId and tsa2.locationType=2 "
				+ " left join tab_view_schedulegroup_map tvsm on tvsm.viewId=tsi.viewId "
				+ " WHERE tsi.crewId=? "
				+ " and (tvsm.isLock!=1 or tvsm.isLock is null) "
				+ " group by tsi.viewId "
				+ " order by seriesNo asc, abs(viewNo) asc,viewNo asc ";
		List<Map<String, Object>> list = this.query(sql, new Object[]{crewId}, null);
		return list;
	}
	
	/**
	 * 删除未锁定的计划分组
	 * @param crewId
	 */
	public void deleteScheduleGroupNotLock(String crewId) {
		String sql = "delete from tab_schedule_group where crewId=? and id not in (" 
				+ "select distinct planGroupId from tab_view_schedulegroup_map where isLock=1 and crewId=? and planGroupId is not null)";
		this.getJdbcTemplate().update(sql, new Object[]{crewId, crewId});
	}

	/**
	 * 计划场景表查询
	 * @param crewId 剧组ID
	 * @param page 分页
	 * @param filter 过滤条件
	 * @return
	 */
	public List<Map<String, Object>> queryViewList(String crewId, Page page, ViewFilter filter){
		
		List<Object> filterList = new ArrayList<Object>();
		filterList.add(crewId);
		filterList.add(crewId);
		filterList.add(crewId);
//		filterList.add(crewId);
		//shootDateInfo.shootDates,shootDateInfo.shootGroups,
		String sql = " select res.*,tpg.id groupId,tvpm.shootDate planShootDate," 
				+ "if(tsg.groupName is null or tsg.groupName='单组','单组',tsg.groupName) planGroupName," 
				+ "if(tsg.groupId='1' or tsg.groupId is null,null,tsg.createTime) as createTime,tvpm.isLock from (" 
				+ " select tsi.viewId,tsi.seriesNo,tsi.viewNo,tsi.specialRemind,tsi.season, "
				+ " tsi.atmosphereId,tsi.site,tsi.viewType,tsha.vCity shootRegion,tsha.vname shootLocation,tsha.id shootLocationId ,tsi.pageCount, "
//				+ " GROUP_CONCAT(DISTINCT tsa.location order by tsa.locationType asc ) as viewAddress, "
//				+ " GROUP_CONCAT(DISTINCT tsa.locationId order by tsa.locationType asc ) as viewAddressId, "
				+ " GROUP_CONCAT(distinct tsa.location) majorView,GROUP_CONCAT(distinct tsa.locationId) majorViewId," 
				+ " GROUP_CONCAT(distinct tsa2.location) minorView,GROUP_CONCAT(distinct tsa2.locationId) minorViewId," 
				+ " GROUP_CONCAT(distinct tsa3.location) thirdLevelView,GROUP_CONCAT(distinct tsa3.locationId) thirdLevelViewId,"
				+ " tsi.mainContent, tsi.remark, tsi.shotDate as shootDate, tsi.shootStatus, "
				+ " GROUP_CONCAT( distinct tmi.id) makeupId,GROUP_CONCAT(distinct tmi.makeupName) makeupName "
				+ " ,GROUP_CONCAT(distinct tci.id) clothesId,GROUP_CONCAT( distinct tci.clothesName) clothesName "
				+ " ,GROUP_CONCAT(distinct tia.advertName) advertName "
				+ " ,GROUP_CONCAT(distinct tia.advertId) advertId "
				+ " ,tai.atmosphereName "
				+ " from tab_view_info tsi "
				+ " left join tab_view_location_map tsam on tsam.viewId=tsi.viewId "
				+ " left join tab_view_location tsa on tsa.locationId=tsam.locationId and tsa.locationType=1 "
				+ " left join tab_view_location tsa2 on tsa2.locationId=tsam.locationId and tsa2.locationType=2 "
				+ " left join tab_view_location tsa3 on tsa3.locationId=tsam.locationId and tsa3.locationType=3 "
//		if (null != filter) {
//			if (StringUtils.isNotBlank(filter.getMajor())) {
//				sql += " AND tsa.locationType =1 ";
//			}
//			if (StringUtils.isNotBlank(filter.getMinor())) {
//				sql += " AND tsa.locationType =2 ";
//			}
//		}
				+ " left join tab_sceneview_info tsha on tsha.id=tsi.shootLocationId"
				+ "  LEFT JOIN ("
				+ "		SELECT tgi.id,tgi.goodsName makeupName,tvgm.viewId"
				+ "		FROM tab_goods_info tgi,tab_view_goods_map tvgm"
				+ "		WHERE tgi.id = tvgm.goodsId AND tgi.goodsType = 2 AND tgi.crewId = ?"
				+ "	) tmi ON tmi.viewId = tsi.viewId"
				+ "  LEFT JOIN ("
				+ "		SELECT tgi.id,tgi.goodsName clothesName,tvgm.viewId"
				+ "		FROM tab_goods_info tgi,tab_view_goods_map tvgm"
				+ "		WHERE tgi.id = tvgm.goodsId AND tgi.goodsType = 3 AND tgi.crewId = ?"
				+ "	) tci ON tci.viewId = tsi.viewId"
				+ " left join tab_view_advert_map tvam on tvam.viewId=tsi.viewId "
				+ " left join tab_inside_advert tia on tia.advertId=tvam.advertId "
				+ " left JOIN tab_atmosphere_info tai ON tsi.atmosphereId = tai.atmosphereId "
				+ " WHERE tsi.crewId=? ";
		sql += " group by tsi.viewId,tsi.seriesNo,tsi.viewNo,tsi.season,tsi.atmosphereId,tsi.site,tsi.viewType,tsha.vname,"
				+ "tsi.pageCount,tsi.mainContent, tsi.remark, tsi.shotDate, tsi.shootStatus, tsi.shootLocationId) res ";
		
		//查询出每一场的完成时间
//		sql += " LEFT JOIN (";
//		sql += "	SELECT";
//		sql += "		tvi.viewId,";
//		sql += "		GROUP_CONCAT(tni.noticeDate ORDER BY tni.noticeDate) shootDates,";
//		sql += "		GROUP_CONCAT(distinct tsg.groupName ORDER BY tsg.createTime) shootGroups";
//		sql += "	FROM";
//		sql += "		tab_view_notice_map tvnm,";
//		sql += "		tab_notice_info tni,";
//		sql += "		tab_view_info tvi,";
//		sql += "		tab_shoot_group tsg";
//		sql += "	WHERE";
//		sql += "		tvnm.viewId = tvi.viewId";
//		sql += "	AND tvnm.noticeId = tni.noticeId";
//		sql += "	AND tsg.groupId = tni.groupId";
//		sql += "	AND tvnm.shootStatus in (1, 2, 4, 5)";
//		sql += "	AND tvnm.crewId=?";
//		sql += "	GROUP BY tvi.viewId";
//		sql += " ) shootDateInfo ON shootDateInfo.viewId = res.viewId ";
		
		//关联计划表
		sql += " LEFT JOIN tab_view_schedulegroup_map tvpm on tvpm.viewId=res.viewId "
				+ " LEFT JOIN tab_schedule_group tpg on tpg.id=tvpm.planGroupId "
				+ " LEFT JOIN tab_shoot_group tsg on tsg.groupId=tvpm.shootGroupId ";
		
		sql += this.genWhereSql(crewId, filter, filterList);
		if(null != filter && StringUtils.isNotBlank(filter.getSortField())){
			//排序
			if(filter.getSortField().equals("planShootDate")) {
				sql += " order by planShootDate is null,planShootDate,createTime,tpg.sequence is null,tpg.sequence,tvpm.sequence is null,tvpm.sequence,seriesNo asc, abs(viewNo) asc,viewNo asc ";
			} else {
				sql += " order by CONVERT(" + filter.getSortField() + " USING gbk) asc,seriesNo asc, abs(viewNo) asc,viewNo asc";
			}
		} else {
			sql+=" order by tpg.sequence is null,tpg.sequence,tvpm.sequence is null,tvpm.sequence, seriesNo asc, abs(viewNo) asc,viewNo asc";
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
			+ " from (select res.* from (select tsi.viewId,tsi.seriesNo,tsi.viewNo,tsi.specialRemind,tsi.season,"
			+ " tsi.atmosphereId,tsi.site,tsi.viewType,tsha.vCity shootRegion,tsha.vname shootLocation,tsha.id shootLocationId,tsi.pageCount,"
//			+ " GROUP_CONCAT(tsa.location order by tsa.locationType asc ) as viewAddress, "
//			+ " GROUP_CONCAT(tsa.locationId order by tsa.locationType asc ) as viewAddressId,"
			+ " GROUP_CONCAT(distinct tsa.location) majorView,GROUP_CONCAT(distinct tsa.locationId) majorViewId," 
			+ " GROUP_CONCAT(distinct tsa2.location) minorView,GROUP_CONCAT(distinct tsa2.locationId) minorViewId," 
			+ " GROUP_CONCAT(distinct tsa3.location) thirdLevelView,GROUP_CONCAT(distinct tsa3.locationId) thirdLevelViewId,"
			+ " tsi.mainContent, tsi.remark, tsi.shotDate shootDate, tsi.shootStatus,"
			+ " GROUP_CONCAT( distinct tmi.id) makeupId,GROUP_CONCAT(distinct tmi.makeupName) makeupName "
			+ " ,GROUP_CONCAT(distinct tci.id) clothesId,GROUP_CONCAT(distinct tci.clothesName) clothesName"
			+ " from tab_view_info tsi "
			+ " left join tab_view_location_map tsam on tsam.viewId=tsi.viewId"
			+ " left join tab_view_location tsa on tsa.locationId=tsam.locationId and tsa.locationType=1 "
			+ " left join tab_view_location tsa2 on tsa2.locationId=tsam.locationId and tsa2.locationType=2 "
			+ " left join tab_view_location tsa3 on tsa3.locationId=tsam.locationId and tsa3.locationType=3 ";
//			if (null != filter) {
//				if (StringUtils.isNotBlank(filter.getMajor())) {
//					sql+=" 	AND tsa.locationType =1 ";
//				}
//				if (StringUtils.isNotBlank(filter.getMinor())) {
//					sql+=" 	AND tsa.locationType =2 ";
//				}
//			}
		sql += " left join tab_sceneview_info tsha on tsha.id=tsi.shootLocationId"
			+ "  LEFT JOIN ("
			+ "		SELECT tgi.id,tgi.goodsName makeupName,tvgm.viewId"
			+ "		FROM tab_goods_info tgi,tab_view_goods_map tvgm"
			+ "		WHERE tgi.id = tvgm.goodsId AND tgi.goodsType = 2 AND tgi.crewId = ?"
			+ "	) tmi ON tmi.viewId = tsi.viewId"
			+ "  LEFT JOIN ("
			+ "		SELECT tgi.id,tgi.goodsName clothesName,tvgm.viewId"
			+ "		FROM tab_goods_info tgi,tab_view_goods_map tvgm"
			+ "		WHERE tgi.id = tvgm.goodsId AND tgi.goodsType = 3 AND tgi.crewId = ?"
			+ "	) tci ON tci.viewId = tsi.viewId"
			+ " WHERE tsi.crewId=? ";
		sql+=" group by tsi.viewId,tsi.seriesNo,tsi.viewNo,tsi.season,tsi.atmosphereId,tsi.site,tsi.viewType,tsha.vname,"
				+ "tsi.pageCount,tsi.mainContent, tsi.remark, tsi.shotDate, tsi.shootStatus) res ";
		
		//关联计划表
		sql += " LEFT JOIN tab_view_schedulegroup_map tvpm on tvpm.viewId=res.viewId "
				+ " LEFT JOIN tab_schedule_group tpg on tpg.id=tvpm.planGroupId "
				+ " LEFT JOIN tab_shoot_group tsg on tsg.groupId=tvpm.shootGroupId ";
		
		sql += this.genWhereSql(crewId, filter, filterList);
		
		sql += " ) groupRes ";
		if(StringUtils.isNotBlank(groupField)){
			sql += " group by groupRes."+groupField;
		}
			
		List<Map<String, Object>> list = this.query(sql,filterList.toArray(), null);
		
		return list;		
	}
	
	private String genWhereSql(String crewId, ViewFilter filter, List<Object> filterList) {
		String sql = "";
		if(null != filter){
			
			sql += " where 1=1 ";
			
			if(StringUtils.isNotBlank(filter.getIsAll())&&filter.getIsAll().equals("0")){
				sql += " AND res.shootStatus != 2 ";
			}
			
			if(StringUtils.isNotBlank(filter.getNoticeId())){
				sql +=" AND res.shootStatus != 3 AND t.viewCount IS NULL ";
			}
			
			if (!StringUtils.isBlank(filter.getPlanId())) {
				sql += " and not EXISTS( select 1 from tab_view_plan_map tvpm where tvpm.viewId=res.viewId and tvpm.planId=? ) ";
				filterList.add(filter.getPlanId());
			}
			
			if (!StringUtils.isBlank(filter.getViewIds())) {
				String viewIds = "'"+ filter.getViewIds().replace(",", "','") +"'";
				sql += " and res.viewId in("+ viewIds +")";
			}
			
			if (!StringUtils.isBlank(filter.getScheduleIds())) {
				sql += " and concat(ifnull(tvpm.shootDate,''),if(tvpm.shootGroupId is null or tvpm.shootGroupId='1','1',tvpm.shootGroupId)) in ('"+ filter.getScheduleIds().replace(",", "','") +"')";
			}
			
			if(null != filter.getSeriesNo()){
				sql += " and res.seriesNo=?";
				filterList.add(filter.getSeriesNo());
			}
			
			if(StringUtils.isNotBlank(filter.getViewNo())){
				sql += " and res.viewno=?";
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
			
			if(StringUtils.isNotBlank(filter.getMajorLike())) {
				sql += " and res.majorView like ? ";
				filterList.add("%" + filter.getMajorLike() + "%");
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
			
			if(StringUtils.isNotBlank(filter.getMinorLike())){
				sql += " and res.minorView like ? ";
				filterList.add("%" + filter.getMinorLike() + "%");
			}
			
			if(StringUtils.isNotBlank(filter.getThirdLevel())){
				if (filter.getMinor().equals("blank")) {
					sql += "and not EXISTS (select 1 from tab_view_location_map tvlm, tab_view_location tvl where tvlm.viewId = res.viewId and tvlm.crewId=? and tvlm.locationId = tvl.locationId and tvl.locationType = 3)";
					filterList.add(crewId);
				} else {
					String[] thirdArr = filter.getThirdLevel().split(",");
					sql += "and (";
					for (int i = 0; i < thirdArr.length; i++) {
						if (i == 0) {
							sql+=" res.thirdLevelView = ?";
						} else {
							sql+=" or res.thirdLevelView = ?";
						}
						filterList.add(thirdArr[i]);
					}
					sql+= ")";
				}
			}
			
			if(StringUtils.isNotBlank(filter.getThirdLevelLike())){
				sql += " and res.thirdLevelView like ? ";
				filterList.add("%" + filter.getThirdLevelLike() + "%");
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
			
			if(StringUtils.isNotBlank(filter.getShootLocationLike())){
				sql += " and (res.shootLocation like ? or res.shootRegion like ?) ";
				filterList.add("%" + filter.getShootLocationLike() + "%");
				filterList.add("%" + filter.getShootLocationLike() + "%");
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
			//计划分组ID
			if(StringUtils.isNotBlank(filter.getScheduleGroupId())) {
				if(filter.getScheduleGroupId().equals("0")) {//未分组
					sql += " and tpg.id is null ";
				} else {
					sql += " and tpg.id=? ";
					filterList.add(filter.getScheduleGroupId());
				}
			}
		}

		return sql;
	}
	
	/**
	 * 查询计划详情
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryScheduleDetail(String crewId, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select CONCAT(ifnull(a.planShootDate,''),ifnull(a.groupId,'')) as scheduleId, ");
		sql.append(" a.*,b.viewNum,b.finishedViewNum,b.pageCount,b.finishedPageCount,dtn.days as dayNum ");
		sql.append(" from  ");
		sql.append(" (SELECT tvsm.shootDate as planShootDate, ");
		sql.append(" 	if(tsg.groupId='1' or tsg.groupId is null,'1',tsg.groupId) as groupId, ");
		sql.append(" 	if(tsg.groupName='单组' or tsg.groupName is null,'单组',tsg.groupName) as planShootGroup, ");
		sql.append(" 	if(tsg.groupId='1' or tsg.groupId is null,null,tsg.createTime) as createTime, ");
		sql.append(" 	GROUP_CONCAT(distinct tsha.vName) shootLocation, ");
		sql.append(" 	GROUP_CONCAT(distinct tvr1.viewRoleName) majorRole, ");
		sql.append(" 	GROUP_CONCAT(distinct tvr2.viewRoleName) guestRole ");
		sql.append(" FROM tab_view_info tvi ");
		sql.append(" LEFT JOIN tab_sceneview_info tsha ON tsha.id=tvi.shootLocationId ");
		sql.append(" LEFT JOIN tab_view_role_map tvrm ON tvrm.viewId=tvi.viewId and tvrm.crewId=? ");
		sql.append(" LEFT JOIN tab_view_role tvr1 ON tvr1.viewRoleId = tvrm.viewRoleId AND tvr1.viewRoleType = 1 and tvr1.crewId=? ");
		sql.append(" LEFT JOIN tab_view_role tvr2 ON tvr2.viewRoleId = tvrm.viewRoleId AND tvr2.viewRoleType = 2 and tvr2.crewId=? ");
		sql.append(" LEFT JOIN tab_view_schedulegroup_map tvsm on tvsm.viewId=tvi.viewId ");
		sql.append(" LEFT JOIN tab_shoot_group tsg on tsg.groupId=tvsm.shootGroupId ");
		sql.append(" where tvi.crewId=? ");
		sql.append(" group by tvsm.shootDate,if(tsg.groupId='1' or tsg.groupId is null,'1',tsg.groupId),");
		sql.append(" if(tsg.groupName='单组' or tsg.groupName is null,'单组',tsg.groupName), ");
		sql.append(" if(tsg.groupId='1' or tsg.groupId is null,null,tsg.createTime)) a ");
		sql.append(" LEFT JOIN (select tvsm.shootDate,if(tvsm.shootGroupId='1' or tvsm.shootGroupId is null,'1',tvsm.shootGroupId) as shootGroupId, ");
		sql.append(" 	count(tvi.viewId) viewNum,count(if(tvi.shootStatus=2,1,null)) finishedViewNum, ");
		sql.append(" 	sum(tvi.pageCount) pageCount,sum(if(tvi.shootStatus=2,pageCount,null)) finishedPageCount  ");
		sql.append(" from tab_view_info tvi ");
		sql.append(" left join tab_view_schedulegroup_map tvsm on tvsm.viewId=tvi.viewId ");
		sql.append(" where tvi.crewId=? ");
		sql.append(" group by tvsm.shootDate,if(tvsm.shootGroupId='1' or tvsm.shootGroupId is null,'1',tvsm.shootGroupId)) b ");
		sql.append(" on CONCAT(ifnull(a.planShootDate,''),ifnull(a.groupId,'')) = CONCAT(ifnull(b.shootDate,''),ifnull(b.shootGroupId,'')) ");
		sql.append("	LEFT JOIN ( ");
		sql.append(" 		select shootDate,cast((@rowNO := @rowNO + 1) as SIGNED INTEGER) AS days ");
		sql.append(" 		from ");
		sql.append(" 		(select distinct shootDate ");
		sql.append(" 		from tab_view_schedulegroup_map t1,tab_view_info tvi  ");
		sql.append(" 		where t1.crewId = ? ");
		sql.append(" 		and t1.viewId=tvi.viewId ");
		sql.append(" 		) a ");
		sql.append(" 		INNER JOIN (SELECT @rowNO := 0) it ");
		sql.append(" 		ORDER BY shootDate is null, shootDate ");
		sql.append("	) dtn ON a.planshootDate = dtn.shootDate ");
		sql.append(" order by a.planShootDate is null, a.planShootDate, a.groupId is null,a.createTime ");
		List<Map<String, Object>> resultList = this.query(sql.toString(), new Object[]{crewId, crewId, crewId, crewId, crewId, crewId}, page);
		return resultList;
	}
}
