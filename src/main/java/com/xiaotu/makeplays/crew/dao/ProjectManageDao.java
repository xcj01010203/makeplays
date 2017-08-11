package com.xiaotu.makeplays.crew.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Constants;

@Repository
public class ProjectManageDao extends BaseDao<CrewInfoModel> {
	
	/**
	 * 查询项目总监管理的项目列表
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> queryAllProjects(String userId) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ");
		sql.append(" 	tci.crewId,tci.company,tci.crewName,tci.recordNumber,tci.seriesNo, ");
		sql.append(" 	tci.coProduction,tci.coProMoney,tci.budget,tci.investmentRatio, ");
		sql.append(" 	tci.shootStartDate,tci.shootEndDate,(TIMESTAMPDIFF(DAY,tci.shootStartDate,tci.shootEndDate)+1) as days, ");
		sql.append(" 	(select count(distinct noticeDate) num from tab_notice_info where canceledStatus=1 and crewId = tci.crewId) as finishedDays, ");
		sql.append(" 	tci.status,tci.remark,tci.lastRemark ");
		sql.append(" FROM ");
		sql.append(" 	tab_crew_info tci ");
		sql.append(" LEFT JOIN tab_user_role_map turm ON turm.crewId = tci.crewId ");
		sql.append(" WHERE ");
		sql.append(" 	turm.userId =? ");
		sql.append(" AND turm.roleId =? ");
		return this.query(sql.toString(), new Object[]{userId, Constants.ROLE_ID_PROJECT_DIRECTOR}, null);
	}
}
