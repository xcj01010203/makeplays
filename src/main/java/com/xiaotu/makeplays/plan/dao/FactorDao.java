package com.xiaotu.makeplays.plan.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.plan.model.Factor;
import com.xiaotu.makeplays.plan.model.Plan;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 影响因素
 * 
 * @author subin
 */
@Repository
public class FactorDao extends BaseDao<Factor>{
	
	/**
	 * 查询影响因素
	 * 
	 */
	public List<Factor> queryFactor(String planid) throws Exception{
		
		String sql = " select * from tab_plan_factor where planid = ? order by priority desc";
		
		return this.query(sql, new Object[] {planid}, Factor.class, null);
	}
	
	/**
	 * 量化所有场景的影响因素
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryQuantizationFactor(Plan plan) throws Exception{
		
		String id =  plan.getCrewid();
		
		StringBuilder sql = new StringBuilder();
		
		List<Factor> factors = plan.getFactors();
		
		sql.append(" select ");
		
		for(Factor one : factors){
			
			if(one.getType().equals("1")){
				
				sql.append(" IF(tvi.shootLocationId = '"+ one.getFactorid() +"' ,0,1) as '"+ one.getFactorid() +"', ");
				
			}else if(one.getType().equals("2")){
				
				sql.append(" (select count(*) from tab_view_role_map tvrm where tvrm.viewId = tvi.viewId and tvrm.viewRoleId = '"+one.getFactorid()+"') as '"+one.getFactorid()+"', ");
				
			}else if(one.getType().equals("3")){
				
				sql.append(" (select count(*) from tab_view_props_map tvpm where tvpm.viewId = tvi.viewId and tvpm.propsId = '"+one.getFactorid()+"') as '"+one.getFactorid()+"', ");
			}
		}
		
		sql.append(" tvi.viewId from tab_view_info tvi where tvi.crewId = ?");
		
		return this.query(sql.toString(), new Object[] {id}, null);
	}
	
	/**
	 * 插入影响因素
	 * 
	 * @param 影响因素
	 */
	public void insert(Factor factor) throws Exception{
		
		String sql = "insert into tab_plan_factor(id, planid, type, factorid, factorname, priority) values(?,?,?,?,?,?)";
		
		this.getJdbcTemplate().update(sql, new Object[]{factor.getId(), factor.getPlanid(), factor.getType(), factor.getFactorid(),factor.getFactorname(),factor.getPriority()});
	}
}
