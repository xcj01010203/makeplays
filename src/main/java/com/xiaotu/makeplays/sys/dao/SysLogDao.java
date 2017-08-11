package com.xiaotu.makeplays.sys.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.sys.filter.SyslogFilter;
import com.xiaotu.makeplays.sys.model.SysLogDataModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.StringUtil;

@Repository
public class SysLogDao extends BaseDao<SysLogDataModel>{
	
	/**
	 * 查询日志信息
	 * @param page
	 * @param filter
	 * @return
	 */
	public List<SysLogDataModel> querySyslogList(Page page, SyslogFilter filter){
		StringBuffer sb = new StringBuffer();
		List<SysLogDataModel> queryList = null;
		
		List<Object> params = new ArrayList<Object>();
		
		sb.append("	SELECT ");
		sb.append("	tsl.*, tui.realName, ");
		sb.append("	tui.userName, ");
		sb.append(" GROUP_CONCAT(distinct concat(ptri.roleName,'-',tri.roleName)) as roles, ");
		sb.append("	tui.phone, ");
		sb.append("	tci.crewName, ");
		sb.append("	tull.address ");
		sb.append(" FROM ");
		sb.append("	( ");			
		sb.append("		SELECT ");
		sb.append("			logId,crewId,userId,userIp,operType,objectId,terminal,logDesc,logTime,storePath,logFileName");
		sb.append("		FROM ");
		sb.append("			tab_sys_log log ");
		
		if (StringUtils.isNotBlank(filter.getCrewId())
				|| StringUtils.isNotBlank(filter.getCrewName())
				|| StringUtils.isNotBlank(filter.getCompany())) {
			if(StringUtils.isBlank(filter.getUserId()) 
					&& StringUtils.isBlank(filter.getRealName()) 
					&& StringUtils.isBlank(filter.getUserIp())
					&& StringUtils.isBlank(filter.getPhone())
					&& StringUtils.isBlank(filter.getAddress())) {
				sb.append(" use index(idx_tslog_crewId) ");
			}
		}
		
		StringBuffer whereSql = new StringBuffer();

		whereSql.append(" WHERE 1=1 ");
		if(StringUtils.isNotBlank(filter.getCrewId())) {
			if(filter.getCrewId().equals("blank")) {
				whereSql.append(" and log.crewId is null");
			} else {
				String crewId = "'"+filter.getCrewId().replaceAll(",", "','")+"'";
				whereSql.append(" and log.crewId in (" + crewId + ") ");
			}
		}
		if(StringUtils.isNotBlank(filter.getCrewName())) {
			String crewName = filter.getCrewName();
			crewName = crewName.replaceAll("%", "\\\\%");
			crewName = crewName.replaceAll("_", "\\\\_");
			String sql = " select GROUP_CONCAT(crewId) crewIds from tab_crew_info where crewName like '%" + crewName + "%' ";
			if(StringUtils.isNotBlank(filter.getCompany())) {
				String company = filter.getCompany();
				company = company.replaceAll("%", "\\\\%");
				company = company.replaceAll("_", "\\\\_");
				sql += " and company like '%" + company + "%' ";
			}
			List<Map<String, Object>> result = this.query(sql, null, null);
			if(result != null && result.size() > 0) {
				String crewIds = (String)result.get(0).get("crewIds");
				if(StringUtil.isNotBlank(crewIds)) {
					whereSql.append(" and log.crewId in ('" + crewIds.replace(",", "','") + "') ");
				}
			}	
		} else if(StringUtils.isNotBlank(filter.getCompany())) {
			String company = filter.getCompany();
			company = company.replaceAll("%", "\\\\%");
			company = company.replaceAll("_", "\\\\_");
			String sql = "select GROUP_CONCAT(crewId) crewIds from tab_crew_info where company like '%" + company + "%'";			
			List<Map<String, Object>> result = this.query(sql, null, null);
			if(result != null && result.size() > 0) {
				String crewIds = (String)result.get(0).get("crewIds");
				if(StringUtil.isNotBlank(crewIds)) {
					whereSql.append(" and log.crewId in ('" + crewIds.replace(",", "','") + "') ");
				}
			}
		}
		if(!filter.getIsIncludeInternalProject()) {
			whereSql.append(" and (log.projectType!=2 or log.projectType is null) ");
		}
//		if(!filter.getIsIncludeInternalProject()) {
//			String sql = " select GROUP_CONCAT(crewId) crewIds from tab_crew_info where projectType=2 ";//内部项目
//			List<Map<String, Object>> result = this.query(sql, null, null);
//			if(result != null && result.size() > 0) {
//				String crewIds = (String)result.get(0).get("crewIds");
//				if(StringUtil.isNotBlank(crewIds)) {
//					whereSql.append(" and crewId not in ('" + crewIds.replace(",", "','") + "')");
//				}
//			}
//		}
		if(StringUtils.isNotBlank(filter.getUserId())) {
			String userId = "'"+filter.getUserId().replaceAll(",", "','")+"'";
			whereSql.append(" and log.userId in (" + userId + ") ");
		}
		if(StringUtils.isNotBlank(filter.getRealName())) {
			String userName = filter.getRealName();
			userName = userName.replaceAll("%", "\\\\%");
			userName = userName.replaceAll("_", "\\\\_");
			String sql = "select GROUP_CONCAT(userId) userIds from tab_user_info where realName like '%" + userName + "%'";
			List<Map<String, Object>> result = this.query(sql, null, null);
			if(result != null && result.size() > 0) {
				String userIds = (String)result.get(0).get("userIds");
				if(StringUtil.isNotBlank(userIds)) {
					whereSql.append(" and log.userId in ('" + userIds.replace(",", "','") + "') ");
				}
			}
		}
		if(StringUtils.isNotBlank(filter.getPhone())) {
			String sql = "select GROUP_CONCAT(userId) userIds from tab_user_info where phone = '" + filter.getPhone() + "'";
			List<Map<String, Object>> result = this.query(sql, null, null);
			if(result != null && result.size() > 0) {
				String userIds = (String)result.get(0).get("userIds");
				if(StringUtil.isNotBlank(userIds)) {
					whereSql.append("and log.userId in ('" + userIds.replace(",", "','") + "') ");
				}
			}
		}
		if(StringUtils.isNotBlank(filter.getAddress())) {
			String addr = filter.getAddress();
			addr = addr.replaceAll("%", "\\\\%");
			addr = addr.replaceAll("_", "\\\\_");
			String sql = "select GROUP_CONCAT(userId) userIds from tab_user_info where address like '%" + addr + "%'";
			List<Map<String, Object>> result = this.query(sql, null, null);
			if(result != null && result.size() > 0) {
				String userIds = (String)result.get(0).get("userIds");
				if(StringUtil.isNotBlank(userIds)) {
					whereSql.append("and log.userId in ('" + userIds.replace(",", "','") + "') ");
				}
			}
		}
		if(StringUtils.isNotBlank(filter.getLogDesc())) {
			String desc = filter.getLogDesc();
			desc = desc.replaceAll("%", "\\\\%");
			desc = desc.replaceAll("_", "\\\\_");
			whereSql.append(" and log.logDesc like ? ");
			params.add("%" + desc + "%");
		}
		if(StringUtils.isNotBlank(filter.getObject())) {
			String object = filter.getObject();
			object = object.replaceAll("%", "\\\\%");
			object = object.replaceAll("_", "\\\\_");
			whereSql.append(" and log.objectId like ? ");
			params.add("%" + object + "%");
		}
		if(StringUtils.isNotBlank(filter.getStartTime())) {
			whereSql.append(" and log.logTime >= ? ");
			params.add(filter.getStartTime());
		}
		if(StringUtils.isNotBlank(filter.getEndTime())) {
			whereSql.append(" and log.logTime <= ? ");
			params.add(filter.getEndTime());
		}
		if(StringUtils.isNotBlank(filter.getTerminal())) {
			whereSql.append(" and log.terminal in ("+filter.getTerminal()+") ");
		}
		if(StringUtils.isNotBlank(filter.getUserIp())) {
			String userIp = filter.getUserIp();
			userIp = userIp.replaceAll("%", "\\\\%");
			userIp = userIp.replaceAll("_", "\\\\_");
			if(filter.getIsIp() == 1){
				whereSql.append(" and log.userIp not like ? ");
			}else{
				whereSql.append(" and log.userIp like ? ");
			}
			params.add(userIp + "%");
		}
		if(StringUtils.isNotBlank(filter.getOperType())) {
			String operType = filter.getOperType();
			whereSql.append(" and log.operType in ("+operType+") ");
			if(operType.indexOf("99")>-1){
				whereSql.append(" or log.operType is null ");
			}
		}
		
		//查询总页数
//		String countSql = "select count(1) from tab_sys_log log " + whereSql.toString();
//		page.setTotal(this.getJdbcTemplate().queryForInt(countSql));
		
		sb.append(whereSql);
		sb.append("		ORDER BY ");
		sb.append("			logTime desc");
		sb.append(" limit " + (page.getPageNo()-1)*page.getPagesize() + "," + page.getPagesize());
		sb.append("	) tsl ");
		sb.append(" LEFT JOIN tab_user_login_log tull ON tull.userId = tsl.userId AND tull.ip = tsl.userIp ");
		sb.append(" left join tab_user_info tui on tui.userId = tsl.userId  ");
		sb.append(" left join tab_crew_info tci on tci.crewId = tsl.crewId ");
		sb.append(" LEFT JOIN tab_user_role_map turm on turm.userId=tui.userId and turm.crewId=tci.crewId ");
		sb.append(" LEFT JOIN tab_sysrole_info tri on tri.roleId=turm.roleId ");
		sb.append(" LEFT JOIN tab_sysrole_info ptri ON tri.parentId = ptri.roleId ");
		sb.append(" group by tsl.logId,tsl.userId ");
		sb.append(" ORDER BY ");
		sb.append("	tsl.logTime desc ");
		
		queryList = this.query(sb.toString(), params.toArray(), SysLogDataModel.class, null);
		
		return queryList;
	}
}
