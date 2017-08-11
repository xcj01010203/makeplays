package com.xiaotu.makeplays.notice.dao.clip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.notice.model.clip.DepartmentEvaluateModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 部门评分
 * @author xuchangjian 2016-3-1上午10:02:08
 */
@Repository
public class DepartmentEvaluateDao extends BaseDao<DepartmentEvaluateModel> {

	
	/**
	 * 查询指定通告单下部门评分
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	public List<Map<String, Object>> queryNoticeDepartScore(String crewId, String noticeId, String userId) {
		List<Object> params = new ArrayList<Object>();
		params.add(noticeId);
		params.add(crewId);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tsi.roleId departmentId, ");
		sql.append(" 	tsi.roleName departmentName, ");
		sql.append(" 	tde.score ");
		sql.append(" FROM tab_sysrole_info tsi ");
		sql.append(" 	left join tab_department_evaluate tde on tde.departmentId = tsi.roleId AND tde.noticeId =? AND tde.crewId =? ");
		if (!StringUtils.isBlank(userId)) {
			sql.append(" and tde.userId = ? ");
			params.add(userId);
		}
		sql.append(" WHERE tsi.canBeEvaluate = 1 order by tsi.roleId ");
		
		List<Map<String, Object>> resultList = this.query(sql.toString(), params.toArray(), null);
		return resultList;
	}
	
	/**
	 * 查询指定人员对指定通告单的部门评分信息
	 * 如果userID为空，则返回通告单下的部门评价信息
	 * @param crewId
	 * @param noticeId
	 * @param userId
	 * @return
	 */
	public List<DepartmentEvaluateModel> queryByNoticeUser(String crewId, String noticeId, String userId) {
		List<Object> params = new ArrayList<Object>();
		params.add(crewId);
		params.add(noticeId);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + DepartmentEvaluateModel.TABLE_NAME + " where crewId=? and noticeId=? ");
		if (!StringUtils.isBlank(userId)) {
			sql.append(" and userId=? ");
			params.add(userId);
		}
		return this.query(sql.toString(), params.toArray(),  DepartmentEvaluateModel.class, null);
	}
}
