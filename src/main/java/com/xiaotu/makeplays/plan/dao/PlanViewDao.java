package com.xiaotu.makeplays.plan.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.plan.model.Factor;
import com.xiaotu.makeplays.plan.model.PlanFilter;
import com.xiaotu.makeplays.plan.model.PlanView;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.StringUtils;

/**
 * 计划对应的场景
 * 
 * @author subin
 */
@Repository
public class PlanViewDao extends BaseDao<PlanView>{
	
	/**
	 * 查询此计划对应的所有场景
	 * 
	 * @param 计划ID
	 */
	public List<PlanView> queryPlanView(String planid) throws Exception{
		
		String sql = " select * from tab_plan_view where planid = ? ";
		
		return this.query(sql, new Object[] {planid}, PlanView.class, null);
	}
	
	/**
	 * 查询此计划下此阶段下对应的所有场景
	 * 
	 * @param 计划ID
	 * @param 阶段ID
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryPlanView(String planId, long stagenum) throws Exception{
		
		String sql = " select tpv.id, tvi.viewId,tvi.seriesNo, tvi.viewNo ,tsl.vname shootLocation ,tvi.maincontent ";
			sql += " from tab_plan_view tpv left join tab_view_info tvi on tpv.viewid = tvi.viewId left join tab_sceneview_info tsl on tvi.shootLocationId = tsl.id ";
			sql += " where tpv.planid = ? and tpv.stagenum = ? ORDER BY tpv.sortnum ";
			
		return this.query(sql, new Object[] {planId, stagenum}, null);
	}
	
	/**
	 * 查询此计划下此阶段下此分组下对应的所有场景
	 * 
	 * @param 计划ID
	 * @param 阶段ID
	 * @param 分组ID
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryPlanView(String planId,  long stagenum, long groupnum) throws Exception{
		
		String sql = " select tpv.id, tvi.viewId,tvi.seriesNo, tvi.viewNo ,tsl.vname shootLocation ,tvi.maincontent, vil.location viewlocation ";
			sql += " from tab_plan_view tpv inner join tab_view_info tvi on tpv.viewid = tvi.viewId left join tab_sceneview_info tsl on tvi.shootLocationId = tsl.id ";
			sql += " left join (select tvlm.viewId, tvl.location from tab_view_location_map tvlm LEFT JOIN tab_view_location tvl on tvlm.locationId = tvl.locationId where tvl.locationType = '1' and EXISTS ( select * from tab_view_info tvi where tvi.crewId = (select crewid from tab_plan where id = ?) and tvi.viewId = tvlm.viewId )) vil on tvi.viewId = vil.viewId ";
			sql += " where tpv.planid = ? and tpv.stagenum = ? and tpv.groupnum = ? ORDER BY tpv.sortnum ";
			
		return this.query(sql, new Object[] {planId,planId, stagenum, groupnum}, null);
	}
	
	/**
	 * 查询此计划下此阶段下此分组下对应的所有场景
	 * 
	 * @param 计划ID
	 * @param 阶段ID
	 * @param 分组ID
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryPlanView(PlanFilter filter, Page page) throws Exception{
		
		StringBuilder sql = new StringBuilder();
		List<Object> args = new ArrayList<Object>();
		
		sql.append(" select tpv.id, tvi.viewId,tvi.seriesNo, tvi.viewNo ,tsl.vname shootLocation ,tvi.maincontent, vil.location viewlocation ");
		sql.append(" from tab_plan_view tpv ");
		sql.append(" inner join tab_view_info tvi on tpv.viewid = tvi.viewId ");
		sql.append(" left join tab_sceneview_info tsl on tvi.shootLocationId = tsl.id ");
		sql.append(" left join (select tvlm.viewId, tvl.location from tab_view_location_map tvlm LEFT JOIN tab_view_location tvl on tvlm.locationId = tvl.locationId where tvl.locationType = '1' and EXISTS ( select * from tab_view_info tvi where tvi.crewId = (select crewid from tab_plan where id = ?) and tvi.viewId = tvlm.viewId )) vil on tvi.viewId = vil.viewId ");
		sql.append(" where 1=1 ");
		
		args.add(filter.getPlanId());
		
		if(!StringUtils.isEmpty(filter.getPlanId())){
			
			sql.append(" and tpv.planid = ? ");
			args.add(filter.getPlanId());
		}
		
		sql.append(" ORDER BY tpv.sortnum ");
			
		return this.query(sql.toString(), args.toArray(), page);
	}
	
	/**
	 * 查询计划分了多少阶段
	 * 
	 * @param 计划ID
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryViewStage(String planId) throws Exception{
		
		String sql = " select stagenum, count(*) cnt from tab_plan_view where planid = ? group by stagenum";
		
		return this.query(sql, new Object[] {planId}, null);
	}
	
	/**
	 * 查询每阶段分了多少组
	 * 
	 * @param 计划ID
	 * @param 阶段ID
	 */
	@SuppressWarnings({"unchecked" })
	public List<Map<String, Object>> queryViewGroup(String planId, long stagenum) throws Exception{
		
		String sql = " select groupnum, count(*) cnt from tab_plan_view where planid = ? and stagenum = ? group by groupnum";
		
		return this.query(sql, new Object[] {planId, stagenum}, null);
	}
	
	/**
	 * 插入对应关系
	 * 
	 * @param 对应关系
	 */
	public void insert(PlanView planView) throws Exception{
		
		String sql = "insert into tab_plan_view(id, planid, viewid, stagenum, groupnum, sortnum) values(?,?,?,?,?,?)";
		
		this.getJdbcTemplate().update(sql, new Object[]{planView.getId(),planView.getPlanid(), planView.getViewid(),planView.getStagenum(),planView.getGroupnum(),planView.getSortnum()});
	}
	
	/**
	 * 更新分组
	 * 
	 * @param id
	 * @param groupnum
	 */
	public void updateGroup(String id, long groupnum) throws Exception{
		
		String sql = "update tab_plan_view set groupnum = ? where id = ?";
		
		this.getJdbcTemplate().update(sql, new Object[]{groupnum, id});
	}
	
	/**
	 * 更新阶段和分组
	 * 
	 * @param id
	 * @param stagenum
	 * @param groupnum
	 */
	public void updateStageGroup(String id, long stagenum, long groupnum) throws Exception{
		
		String sql = "update tab_plan_view set stagenum = ? , groupnum = ? where id = ?";
		
		this.getJdbcTemplate().update(sql, new Object[]{stagenum, groupnum, id});
	}
	
	/**
	 * 统计阶段
	 * 
	 * @param 计划ID
	 * @param 阶段ID
	 */
	public Map<String, Object> queryStageStat(String planId, long stagenum) throws Exception{
		
		String sql = " select count(*) viewsnum, stagenum from tab_plan_view where planid = ? and stagenum = ?";
		
		return this.getJdbcTemplate().queryForMap(sql, new Object[] {planId, stagenum});
	}
	
	/**
	 * 统计分组
	 * 
	 * @param 计划ID
	 * @param 阶段ID
	 * @param 分组ID
	 */
	public Map<String, Object> queryGroupStat(String planId, long stagenum, long groupnum) throws Exception{
		
		String sql = " select count(*) viewsnum, groupnum from tab_plan_view where planid = ? and stagenum = ? and groupnum = ?";
		
		return this.getJdbcTemplate().queryForMap(sql, new Object[] {planId, stagenum, groupnum});
	}
	
	/**
	 * 查询此计划下此阶段最大的分组
	 * 
	 * @param 计划ID
	 * @param 阶段ID
	 */
	public long queryMaxGroup(String planId,long stagenum) throws Exception{
		
		String sql = " select max(groupnum) num from tab_plan_view where planid = ? and stagenum = ? ";
		
		Map<String, Object> result = this.getJdbcTemplate().queryForMap(sql, new Object[] {planId, stagenum});
		
		return Long.valueOf(result.get("num").toString());
	}
	
	/**
	 * 查询此计划下最大的阶段
	 * 
	 * @param 计划ID
	 */
	public long queryMaxStage(String planId) throws Exception{
		
		String sql = " select max(stagenum) num from tab_plan_view where planid = ? ";
		
		Map<String, Object> result = this.getJdbcTemplate().queryForMap(sql, new Object[] {planId});
		
		return Long.valueOf(result.get("num").toString());
	}
	
	/**
	 * 查询主场景
	 * 
	 * @param 计划ID
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryViewLocation(String viewId) throws Exception{
		
		String sql = " select tvl.location from tab_view_location_map tvlm LEFT JOIN tab_view_location tvl on tvlm.locationId = tvl.locationId where tvlm.viewId = ? and tvl.locationType = '1'";
		
		return this.query(sql, new Object[] {viewId}, null);
	}
	
	/**
	 * 查询主要演员
	 * 
	 * @param 计划ID
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryViewRole(String viewId) throws Exception{
		
		String sql = " select tvr.viewRoleName from tab_view_role_map tvrm LEFT JOIN tab_view_role tvr on tvrm.viewRoleId = tvr.viewRoleId where tvr.viewRoleType = '1' and tvrm.viewId = ?";
		
		return this.query(sql, new Object[] {viewId}, null);
	}
	
	/**
	 * 查询阶段下影响因素的数量
	 * 
	 * @param 影响因素
	 * @param 阶段ID
	 */
	public long queryFactorStageCnt(Factor factor, long stage) throws Exception{
		
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) num from tab_plan_view tpv left join tab_view_info tvi on tpv.viewid = tvi.viewId  where tpv.planid = ? and tpv.stagenum = ? ");
		
		if(factor.getType().equals("1")){
			
			sql.append(" and tvi.shootLocationId = ? ");
			
		}else if(factor.getType().equals("2")){
			
			sql.append(" and EXISTS (select * from tab_view_role_map tvrm where tvrm.viewId = tvi.viewId and tvrm.viewRoleId = ?) ");
			
		}else if(factor.getType().equals("3")){
			
			sql.append(" and EXISTS (select * from tab_view_props_map tvpm where tvpm.viewId = tvi.viewId and tvpm.propsId = ?) ");
		}
		
		Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql.toString(), new Object[]{factor.getPlanid(), stage, factor.getFactorid()});
		
		return Long.valueOf(map.get("num").toString());
	}
	
	/**
	 * 查询阶段下影响因素的数量
	 * 
	 * @param 影响因素
	 * @param 阶段ID
	 * @param 分组ID
	 */
	public long queryFactorStageGroupCnt(Factor factor, long stage, long group) throws Exception{
		
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) num from tab_plan_view tpv left join tab_view_info tvi on tpv.viewid = tvi.viewId  where tpv.planid = ? and tpv.stagenum = ? and tpv.groupnum = ? ");
		
		if(factor.getType().equals("1")){
			
			sql.append(" and tvi.shootLocationId = ? ");
			
		}else if(factor.getType().equals("2")){
			
			sql.append(" and EXISTS (select * from tab_view_role_map tvrm where tvrm.viewId = tvi.viewId and tvrm.viewRoleId = ?) ");
			
		}else if(factor.getType().equals("3")){
			
			sql.append(" and EXISTS (select * from tab_view_props_map tvpm where tvpm.viewId = tvi.viewId and tvpm.propsId = ?) ");
		}
		
		Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql.toString(), new Object[]{factor.getPlanid(), stage, group, factor.getFactorid()});
		
		return Long.valueOf(map.get("num").toString());
	}
}