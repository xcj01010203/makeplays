package com.xiaotu.makeplays.community.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.community.model.SearchTeamInfoModel;
import com.xiaotu.makeplays.mobile.server.community.filter.SearchTeamFilter;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 寻组信息操作dao
 * @author wanrenyi 2016年9月5日下午3:39:13
 */
@Repository
public class SearchTeamInfoDao extends BaseDao<SearchTeamInfoModel> {

	/**
	 * 添加寻组信息
	 * @param model
	 * @throws Exception 
	 */
	public void addSearchTeamInfo(SearchTeamInfoModel model) throws Exception {
		this.add(model);
	}
	
	/**
	 * 更新寻组信息
	 * @param model
	 * @throws Exception 
	 */
	public void updateSearchTeamInfo(SearchTeamInfoModel model) throws Exception {
		this.updateWithNull(model, "searchTeamId");
	}
	
	/**
	 * 根据寻组信息id删除寻组信息
	 * @param searchTeamId
	 */
	public void deleteSearchTeamInfo(String searchTeamId) {
		String sql = "delete from " + SearchTeamInfoModel.TABLE_NAME + " where searchTeamId = ?";
		this.getJdbcTemplate().update(sql, searchTeamId);
	}
	
	/**
	 * 根据寻组id查询出寻组信息的详细信息
	 * @param searchTeamId
	 * @return
	 * @throws Exception
	 */
	public SearchTeamInfoModel getSearchTeamInfoById(String searchTeamId) throws Exception {
		String sql = "select * from " + SearchTeamInfoModel.TABLE_NAME + " where searchTeamId = ?";
		return this.queryForObject(sql, new Object[] {searchTeamId}, SearchTeamInfoModel.class);
	}
	
	/**
	 * 根据招聘职位的id查找用户信息
	 * @param positionId
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> getUserInfoByPositionId(SearchTeamFilter filter, String positionId, Page page){
		StringBuffer sql = new StringBuffer();
		List<Object> param = new ArrayList<Object>();
		
		sql.append(" SELECT tui.userId,tui.sex,tui.age,tui.phone,tui.realName,tui.bigImgUrl,trnm.createTime");
		sql.append(" FROM tab_user_info tui");
		sql.append(" LEFT JOIN tab_tean_resume_map trnm ON trnm.userId = tui.userId");
		sql.append(" WHERE 1=1");
		if (StringUtils.isNotBlank(positionId)) {
			//按最大年龄段收索
			Integer maxAge = filter.getMaxAge();
			if (maxAge != null && maxAge != 0) {
				sql.append(" and tui.age <= ?");
				param.add(maxAge);
			}
			
			//按最小年龄搜索
			Integer minAge = filter.getMinAge();
			if (minAge != null && minAge != 0) {
				sql.append(" and tui.age >= ?");
				param.add(minAge);
			}
			
			//按性别搜索
			Integer sex = filter.getSex();
			if (sex != null && sex != 3) {
				sql.append(" and tui.sex = ?");
				param.add(sex);
			}
			sql.append(" AND tui.userId IN (");
			sql.append(" SELECT trm.userId FROM tab_tean_resume_map trm WHERE trm.positionId = ?");
			sql.append(")");
			param.add(positionId);
			sql.append(" AND trnm.positionId = ?");
			param.add(positionId);
		}
		sql.append(" ORDER BY trnm.createTime DESC");
		
		return this.query(sql.toString(), param.toArray(), page);
	}
	
	/**
	 * 获取寻组信息列表
	 * @param filter
	 * @return
	 */
	public List<Map<String, Object>> getSearchTeamList(SearchTeamFilter filter, Page page){
		StringBuffer sql = new StringBuffer();
		List<Object> param = new ArrayList<Object>();
		//拼接sql
		sql.append(" SELECT sti.*,tui.sex,tui.age,tui.phone,tui.realName,tui.bigImgUrl");
		sql.append(" FROM tab_search_team_info sti");
		sql.append(" LEFT JOIN tab_user_info tui ON sti.createUser = tui.userId");
		sql.append(" WHERE 1=1");
		
		if (filter != null) {
			
			//根据用户id搜索我的发布中的寻组信息
			String userId = filter.getUserId();
			if (StringUtils.isNotBlank(userId)) {
				sql.append(" and sti.createUser = ?");
				param.add(userId);
			}
			
			//按意向职位收索
			String likePositionName = filter.getLikePositionName();
			if (StringUtils.isNotBlank(likePositionName)) {
				sql.append(" and sti.likePositionName LIKE CONCAT('%',?,'%' )");
				param.add(likePositionName);
			}
			
			//个人档期开始时间
			Date currentStartDate = filter.getCurrentStartDate();
			if (currentStartDate != null) {
				sql.append(" and sti.currentStartDate = ?");
				param.add(currentStartDate);
			}
			
			//个人档期结束时间
			Date currentEndDate = filter.getCurrentEndDate();
			if (currentEndDate != null) {
				sql.append(" and sti.currentEndDate = ?");
				param.add(currentEndDate);
			}
			
			//按最大年龄段收索
			Integer maxAge = filter.getMaxAge();
			if (maxAge != null && maxAge != 0) {
				sql.append(" and tui.age <= ?");
				param.add(maxAge);
			}
			
			//按最小年龄搜索
			Integer minAge = filter.getMinAge();
			if (minAge != null && minAge != 0) {
				sql.append(" and tui.age >= ?");
				param.add(minAge);
			}
			
			//按性别搜索
			Integer sex = filter.getSex();
			if (sex != null && sex != 3) {
				sql.append(" and sex = ?");
				param.add(sex);
			}
			
			//如果查询当前组训的寻组信息列表
			String positionId = filter.getPositionId();
			String teamId = filter.getTeamId();
			if (StringUtils.isNotBlank(positionId) && StringUtils.isNotBlank(teamId)) {
				sql.append(" and sti.createUser IN ( SELECT trm.userId FROM tab_tean_resume_map trm");
				sql.append("	");
				param.add(positionId);
			}
		}
		
		sql.append(" ORDER BY sti.createTime DESC");
		
		List<Map<String, Object>> list = this.query(sql.toString(), param.toArray(), page);
		return list;
	}
	
	/**
	 * 根据寻组id查询寻组的详细信息
	 * @param searchTeamId
	 * @return
	 */
	public List<Map<String, Object>> getSearchUserInfoById(String searchTeamId){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT tui.sex,tui.age,tui.realName,tui.bigImgUrl,tui.phone,tui.`profile`,sti.likePositionName,sti.currentStartDate,sti.currentEndDate");
		sql.append(" FROM tab_search_team_info sti");
		sql.append(" LEFT JOIN tab_user_info tui ON sti.createUser = tui.userId");
		sql.append(" WHERE sti.searchTeamId = ?");
		
		List<Map<String, Object>> list = this.query(sql.toString(), new Object[] {searchTeamId}, null);
		return list;
	}
}
