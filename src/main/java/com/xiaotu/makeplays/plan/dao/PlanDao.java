package com.xiaotu.makeplays.plan.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.plan.model.Plan;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 计划
 * 
 * @author subin
 */
@Repository
public class PlanDao extends BaseDao<Plan>{
	
	/**
	 * 查询所有计划
	 * 
	 */
	public List<Plan> queryPlan(String crewid, Page page) throws Exception{
		
		String sql = " select * from tab_plan where crewid = ? ORDER BY CONVERT(name USING gbk)";
		
		return this.query(sql, new Object[] {crewid}, Plan.class, page);
	}
	
	/**
	 * 查询计划
	 * 
	 */
	public Plan queryPlan(String id) throws Exception{
		
		String sql = " select * from tab_plan where id = ? ";
		
		return this.queryForObject(sql, new Object[] {id}, Plan.class);
	}
	
	/**
	 * 查询默认计划
	 * 
	 */
	public Plan queryDefaultPlan(String crewId) throws Exception{
		
		String sql = " select * from tab_plan where crewid = ? and official = '1' ";
		
		return this.queryForObject(sql, new Object[] {crewId}, Plan.class);
	}
	
	/**
	 * 查询拍摄地
	 * 
	 * @param 剧本ID
	 */
	@SuppressWarnings("rawtypes")
	public List queryShootLocation(String crewId) throws Exception{
		
		String sql = " select DISTINCT tsl.vname shootLocation,tsl.id shootLocationId,tsl.crewId from tab_sceneview_info tsl, tab_view_info tvi where tsl.crewid=? and tvi.crewid=? and tsl.id = tvi.shootLocationId ORDER BY CONVERT(tsl.vname USING gbk) ";
		
		return this.query(sql, new Object[]{crewId, crewId}, null);
	}
	
	/**
	 * 查询演员
	 * 
	 * @param 剧本ID
	 * @param 演员类型, 主演-1
	 */
	@SuppressWarnings("rawtypes")
	public List queryViewRole(String crewId, String type) throws Exception{
		
		String sql = " select tvr.*, count(tvrm.mapId) viewRoleCount from tab_view_role tvr, tab_view_role_map tvrm where tvr.crewId = ? and tvr.viewRoleId = tvrm.viewRoleId and tvr.viewRoleType = ? GROUP BY tvr.viewRoleId ORDER BY viewRoleCount desc, convert(viewRoleName using gbk)";
		
		return this.query(sql, new Object[]{crewId, type}, null);
	}
	
	/**
	 * 查询场景演员
	 * 
	 * @param 场景ID
	 */
	@SuppressWarnings("rawtypes")
	public List queryScenarioActor(String viewid) throws Exception{
		
		String sql = " select viewRoleName as name from tab_view_role_map tvrm LEFT JOIN tab_view_role tvr on tvrm.viewRoleId = tvr.viewRoleId where tvrm.viewId = ? ";
		
		return this.query(sql, new Object[]{viewid}, null);
	}
	
	/**
	 * 查询道具
	 * 
	 * @param 剧本ID
	 */
	@SuppressWarnings("rawtypes")
	public List queryProps(String crewId) throws Exception{
		
		String sql = " select * from tab_props_info where crewId = ? ";
		
		return this.query(sql, new Object[]{crewId}, null);
	}
	
	/**
	 * 插入计划
	 * 
	 * @param 计划
	 */
	public void insert(Plan plan) throws Exception{
		
		String sql = "insert into tab_plan(id, name, crewid, official) values(?,?,?,?)";
		
		this.getJdbcTemplate().update(sql, new Object[]{plan.getId(), plan.getName(), plan.getCrewid(), plan.getOfficial()});
	}
}