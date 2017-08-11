package com.xiaotu.makeplays.shoot.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.goods.model.constants.GoodsType;
import com.xiaotu.makeplays.roleactor.model.constants.ViewRoleType;
import com.xiaotu.makeplays.shoot.model.ScheduleViewMapModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.view.model.constants.LocationType;

@Repository
public class ScheduleViewMapDao extends BaseDao<ScheduleViewMapModel>{
	
	/**
	 * 更新分组场景排列排序
	 * @param groupIds
	 */
	public void updateViewGroupMapSequence(String crewId){
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE tab_view_schedulegroup_map a ");
		sql.append(" INNER JOIN ( ");
		sql.append(" 	SELECT ");
		sql.append(" 		(@rowNO := @rowNO + 1) AS rowno, ");
		sql.append(" 		tvsm.id ");
		sql.append(" 	FROM ");
		sql.append(" 		tab_view_schedulegroup_map tvsm ");
		sql.append(" 	LEFT JOIN tab_schedule_group tsg ON tsg.id = tvsm.planGroupId ");
		sql.append(" 	INNER JOIN (SELECT @rowNO := 0) it ");
		sql.append(" 	where tvsm.sequence is not null ");
		sql.append(" 	and tvsm.crewId=? ");
		sql.append(" 	ORDER BY ");
		sql.append(" 		tsg.sequence, ");
		sql.append(" 		tvsm.sequence ");
		sql.append(" ) b ON a.id = b.id ");
		sql.append(" SET a.sequence = b.rowno ");
		this.getJdbcTemplate().update(sql.toString(), new Object[]{crewId});
	}
		
	/**
	 * 修改场景锁定状态
	 * @param viewIds 场景ID
	 * @param isLock 是否锁定
	 */
	public void updateViewGroupMapIsLock(String crewId, String viewIds, boolean isLock){
		String sql = "update tab_view_schedulegroup_map tvsm set isLock=? where crewId=? and tvsm.viewId in ('" + viewIds.replace(",", "','") + "')";
		this.getJdbcTemplate().update(sql, new Object[]{isLock, crewId});
	}
	
	/**
	 * 添加锁定场景
	 * @param viewIds 场景ID
	 * @param isLock 是否锁定
	 */
	public void addViewGroupMapLock(String crewId, String viewIds, boolean isLock){
		StringBuilder sql = new StringBuilder();
		sql.append(" insert into tab_view_schedulegroup_map(id,viewId,planGroupId,shootDate,shootGroupId,sequence,isLock,crewId) ");
		sql.append(" select REPLACE(UUID(), '-', ''),viewId,null,null,null,null,?,? ");
		sql.append(" from tab_view_info tvi ");
		sql.append(" where tvi.crewId=? ");
		sql.append(" and tvi.viewId in ('" + viewIds.replace(",", "','") + "') ");
		sql.append(" and tvi.viewId not in ( ");
		sql.append(" 	select tvsm.viewId from tab_view_schedulegroup_map tvsm where crewId=? ");
		sql.append(" ) ");
		this.getJdbcTemplate().update(sql.toString(), new Object[]{isLock, crewId, crewId, crewId});
	}
	
	/**
	 * 设置场景计划日期和计划组别
	 * @param crewId
	 * @param viewIds
	 * @param date
	 * @param planGroupId
	 * @param dayNum 提前/延后天数
	 */
	public void updateViewGroupMapDateAndGroupId(String crewId, String viewIds, String planDate, String planGroupId, Integer dayNum) {
		List<Object> params = new ArrayList<Object>();
		String sql = "update tab_view_schedulegroup_map tvsm set ";
		if(planDate != null) {
			sql += " shootDate = ? ";
			String planDateNew = planDate;
			if(planDateNew.equals("")) {
				planDateNew = null;
			}
			params.add(planDateNew);
		}
		if(dayNum != null) {
			sql += " shootDate = DATE_ADD(shootDate,INTERVAL ? DAY) ";
			params.add(dayNum);
		}
		if(planGroupId != null) {
			if(planDate != null || dayNum != null) {
				sql += " , shootGroupId = ? ";
			} else {
				sql += " shootGroupId = ? ";
			}
			params.add(planGroupId);
		}
		sql += " where crewId=? and tvsm.viewId in ('" + viewIds.replace(",", "','") + "')";
		params.add(crewId);
		this.getJdbcTemplate().update(sql, params.toArray());
	}
	
	/**
	 * 新增场景计划日期和计划组别
	 * @param crewId
	 * @param viewIds
	 * @param date
	 * @param planGroupId
	 */
	public void addViewGroupMapDateAndGroupId(String crewId, String viewIds, String planDate, String planGroupId){
		if(planDate != null && planDate.equals("")) {
			planDate = null;
		}
		StringBuilder sql = new StringBuilder();
		sql.append(" insert into tab_view_schedulegroup_map(id,viewId,planGroupId,shootDate,shootGroupId,sequence,isLock,crewId) ");
		sql.append(" select REPLACE(UUID(), '-', ''),viewId,null,?,?,null,null,? ");
		sql.append(" from tab_view_info tvi ");
		sql.append(" where tvi.crewId=? ");
		sql.append(" and tvi.viewId in ('" + viewIds.replace(",", "','") + "') ");
		sql.append(" and tvi.viewId not in ( ");
		sql.append(" 	select tvsm.viewId from tab_view_schedulegroup_map tvsm where crewId=? ");
		sql.append(" ) ");
		this.getJdbcTemplate().update(sql.toString(), new Object[]{planDate, planGroupId, crewId, crewId, crewId});
	}
	
	/**
	 * 查询分组最大排列顺序
	 * @param crewId
	 * @param groupId
	 * @return
	 */
	public List<Map<String, Object>> queryMaxSequenceByPlanGroupId(String crewId, String groupId) {
		String sql = "select max(sequence) sequence from tab_view_schedulegroup_map where planGroupId=? and crewId=?";
		List<Map<String, Object>> list = this.query(sql, new Object[]{groupId, crewId}, null);
		return list;
	}
	
	/**
	 * 更新场景计划分组
	 * @param crewId
	 * @param viewIds
	 * @param groupId
	 */
	public void updateViewScheduleGroupId(String crewId, String viewIds, String groupId) {
		String sql = "update tab_view_schedulegroup_map tvsm set planGroupId=?"
				+ " where tvsm.crewId=? and tvsm.viewId in ('" + viewIds.replace(",", "','") + "')";
		this.getJdbcTemplate().update(sql, new Object[]{groupId, crewId});
	}
	
	/**
	 * 根据计划分组ID将分组场景关联关系中分组ID清空
	 * @param crewId
	 * @param groupId
	 */
	public void updateViewScheduleGroupIdBySchGroupId(String crewId, String groupId) {
		String sql = "update tab_view_schedulegroup_map tvsm set planGroupId=null, sequence=null "
				+ " where tvsm.crewId=? and tvsm.planGroupId=?";
		this.getJdbcTemplate().update(sql, new Object[]{crewId, groupId});
	}
	
	/**
	 * 新增场景计划分组
	 * @param crewId
	 * @param viewIds
	 * @param groupId
	 */
	public void addViewScheduleGroupId(String crewId, String viewIds, String groupId){
		StringBuilder sql = new StringBuilder();
		sql.append(" insert into tab_view_schedulegroup_map(id,viewId,planGroupId,shootDate,shootGroupId,sequence,isLock,crewId) ");
		sql.append(" select REPLACE(UUID(), '-', ''),viewId,?,null,null,null,null,? ");
		sql.append(" from tab_view_info tvi ");
		sql.append(" where tvi.crewId=? ");
		sql.append(" and tvi.viewId in ('" + viewIds.replace(",", "','") + "') ");
		sql.append(" and tvi.viewId not in ( ");
		sql.append(" 	select tvsm.viewId from tab_view_schedulegroup_map tvsm where crewId=? ");
		sql.append(" ) ");
		this.getJdbcTemplate().update(sql.toString(), new Object[]{groupId, crewId, crewId, crewId});
	}
	
	/**
	 * 批量更新分组内场景排序
	 * @param crewId
	 * @param viewIds
	 * @param sequence
	 */
	public void updateViewSequenceBatch(String crewId, String viewIds, int sequence){
		List<Object[]> paramList = new ArrayList<Object[]>();
		String[] viewIdArray = viewIds.split(",");
		for (int i = 0; i < viewIdArray.length; i++) {
			paramList.add(new Object[]{sequence + i, viewIdArray[i], crewId});
		}
		String sql = "update " + ScheduleViewMapModel.TABLE_NAME + " set sequence = ? where viewId = ? and crewId = ?";
		this.getJdbcTemplate().batchUpdate(	sql, paramList);
	}
	
	/**
	 * 更新场景顺序为空
	 * @param crewId
	 * @param viewIds
	 */
	public void updateViewSequenceNull(String crewId, String viewIds) {
		String sql = "update " + ScheduleViewMapModel.TABLE_NAME + " set sequence = null where viewId in ('" + viewIds.replace(",", "','") + "') and crewId=? ";
		this.getJdbcTemplate().update(sql, new Object[]{crewId});
	}
	
	/**
	 * 查询场景所在分组及排序等明细
	 * @param crewId
	 * @param viewId
	 * @return
	 * @throws Exception 
	 */
	public ScheduleViewMapModel queryDetailByViewId(String crewId, String viewId) throws Exception {
		String sql = "select * from " + ScheduleViewMapModel.TABLE_NAME + " where viewId=? and crewId=?";
		return this.queryForObject(sql, new Object[]{viewId, crewId}, ScheduleViewMapModel.class);
	}
	
	/**
	 * 更新分组内原有场景排序
	 * @param crewId
	 * @param groupId
	 * @param sequence
	 */
	public void updateViewSequence(String crewId, String groupId, int sequence, int num) {
		String sql = "update " + ScheduleViewMapModel.TABLE_NAME + " set sequence=sequence+? where planGroupId = ? and crewId = ? and sequence>=?";
		this.getJdbcTemplate().update(sql, new Object[]{num, groupId, crewId,sequence});
	}
	
	/**
	 * 删除未锁定的分组场景关联关系
	 * @param crewId
	 */
	public void deleteScheduleViewMapIsNotLock(String crewId) {
		String sql = "delete from tab_view_schedulegroup_map where crewId=? and isLock!=1";
		this.getJdbcTemplate().update(sql, new Object[]{crewId});
	}
	
	/**
	 * 删除分组场景关联关系
	 * @param crewId
	 * @param viewId
	 */
	public void deleteScheduleViewMap(String crewId, String viewIds) {
		String sql = "delete from tab_view_schedulegroup_map where crewId=? and viewId in ('" + viewIds.replace(",", "','") + "')";
		this.getJdbcTemplate().update(sql, new Object[]{crewId});
	}
	
	/**
	 * 将未锁定的分组场景关联关系分组ID置为空
	 * @param crewId
	 */
	public void updateNotLockPlanGroupIdNullByCrewId(String crewId) {
		String sql = "update tab_view_schedulegroup_map set planGroupId=null where crewId=? and isLock!=1";
		this.getJdbcTemplate().update(sql, new Object[]{crewId});
	}
	
	/**
	 * 查询计划按日汇总信息，包括关注项
	 * @param crewId
	 * @param viewRoleIds
	 * @param propIds
	 * @param locationIds
	 * @return
	 */
	public List<Map<String, Object>> queryScheduleCalendar(String crewId, String viewRoleIds, String propIds, String locationIds) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select DATE_FORMAT(tvsm.shootDate,'%Y') year, ");
		sql.append(" DATE_FORMAT(tvsm.shootDate,'%m') month, ");
		sql.append(" DATE_FORMAT(tvsm.shootDate,'%d') day,shootDate, ");
		sql.append(" GROUP_CONCAT(distinct tsg.groupName) shootGroup, ");
		sql.append(" count(distinct tvi.viewId) viewNum, ");
		sql.append(" GROUP_CONCAT(distinct tsi.vname) shootLocation ");
		if(StringUtils.isNotBlank(viewRoleIds)) {
			sql.append(" ,GROUP_CONCAT(distinct tvrm.viewRoleId) roles ");
		}
		if(StringUtils.isNotBlank(propIds)) {
			sql.append(" ,GROUP_CONCAT(distinct tvgm.goodsId) specialProps ");
		}
		if(StringUtils.isNotBlank(locationIds)) {
			sql.append(" ,GROUP_CONCAT(distinct tvlm.locationId) majorViews ");
		}
		sql.append(" from tab_view_info tvi ");
		sql.append(" left join tab_sceneview_info tsi on tsi.id=tvi.shootLocationId ");
		if(StringUtils.isNotBlank(viewRoleIds)) {
			sql.append(" left join tab_view_role_map tvrm on tvrm.viewId=tvi.viewId and tvrm.viewRoleId in ('" + viewRoleIds.replace(",", "','") + "') ");
		};
		if(StringUtils.isNotBlank(propIds)) {
			sql.append(" left join tab_view_goods_map tvgm on tvgm.viewId=tvi.viewId and tvgm.goodsId in ('" + propIds.replace(",", "','") + "') ");
		}
		if(StringUtils.isNotBlank(locationIds)) {
			sql.append(" left join tab_view_location_map tvlm on tvlm.viewId=tvi.viewId and tvlm.locationId in ('" + locationIds.replace(",", "','") + "') ");
		}
		sql.append(" left join tab_view_schedulegroup_map tvsm on tvsm.viewId=tvi.viewId ");
		sql.append(" LEFT JOIN tab_shoot_group tsg ON tsg.groupId = tvsm.shootGroupId ");
		sql.append(" where tvi.crewId=? ");
		sql.append(" group by DATE_FORMAT(tvsm.shootDate,'%m'),DATE_FORMAT(tvsm.shootDate,'%d') ");
		sql.append(" order by year is null, year,month is null,month,day is null,day ");
		
		return this.getJdbcTemplate().queryForList(sql.toString(), new Object[] {crewId});
	}
	
	/**
	 * 查询关注项汇总信息
	 * @param crewId
	 * @param attentionId
	 * @param type
	 * @return
	 */
	public Map<String, Object> queryAttentionTotalInfo(String crewId, String attentionId, String type) {
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		params.add(attentionId);
		params.add(type);
		sql.append(" select ? as id,");
		if(type.equals("1")) {
			sql.append(" tvr.viewRoleName name, ");
		} else if(type.equals("2")) {
			sql.append(" tgi.goodsName name, ");
		} else if(type.equals("3")) {
			sql.append(" tvl.location name, ");
		}
		sql.append(" ? as type,min(tvsm.shootDate) startDate,max(tvsm.shootDate) endDate, ");
		sql.append(" 	count(distinct tvsm.shootDate) dayNum,count(tvi.viewId) viewNum,sum(tvi.pageCount) pageCount, ");
		sql.append(" 	group_concat(distinct tsg.groupName order by tsg.createTime) groupName ");
		sql.append(" from ");
		if(type.equals("1")) {
			sql.append(" tab_view_role tvr ");
			sql.append(" left join tab_view_role_map map on map.viewRoleId=tvr.viewRoleId ");
		} else if(type.equals("2")) {
			sql.append(" tab_goods_info tgi ");
			sql.append(" left join tab_view_goods_map map on map.goodsId=tgi.id ");
		} else if(type.equals("3")) {
			sql.append(" tab_view_location tvl ");
			sql.append(" left join tab_view_location_map map on map.locationId=tvl.locationId ");
		}
		sql.append(" left join tab_view_info tvi on tvi.viewId = map.viewId ");
		sql.append(" left join tab_view_schedulegroup_map tvsm on tvsm.viewId=tvi.viewId ");
		sql.append(" left join tab_shoot_group tsg on tsg.groupId=tvsm.shootGroupId ");
		sql.append(" where ");
		if(type.equals("1")) {
			sql.append(" tvr.crewId=? and tvr.viewRoleId=? and tvr.viewRoleType=" + ViewRoleType.MajorActor.getValue() + " group by tvr.viewRoleId ");
		} else if(type.equals("2")) {
			sql.append(" tgi.crewId=? and tgi.id=? and tgi.goodsType=" + GoodsType.SpecialProps.getValue() + " group by tgi.id ");
		} else if(type.equals("3")) {
			sql.append(" tvl.crewId=? and map.locationId=? and tvl.locationType=" + LocationType.lvlOneLocation.getValue() + " group by tvl.locationId ");
			
		}
		params.add(crewId);
		params.add(attentionId);
		List<Map<String, Object>> list = this.query(sql.toString(), params.toArray(), null);
		if(list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
}
