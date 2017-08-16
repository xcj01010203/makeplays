package com.xiaotu.makeplays.community.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.community.model.TeamInfoModel;
import com.xiaotu.makeplays.community.model.TeamPositionInfoModel;
import com.xiaotu.makeplays.community.model.TeamResumeMapModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 组训信息操作的dao
 * @author wanrenyi 2016年9月1日下午4:50:06
 */
@Repository
public class TeamInfoDao extends BaseDao<TeamInfoModel> {

	/**
	 * 根据组训信息对象,保存组训信息
	 * @param teamInfo
	 * @throws Exception 
	 */
	public void addTeamInfoByBean(TeamInfoModel teamInfo) throws Exception {
		this.add(teamInfo);
	}
	
	/**
	 * 根据组训信息对象更新组训信息
	 * @param teamInfo
	 * @throws Exception
	 */
	public void updateTeamInfoByBean(TeamInfoModel teamInfo) throws Exception {
		this.updateWithNull(teamInfo, "teamId");
	}
	
	/**
	 * 根据组训id删除组训信息(逻辑删除)
	 * @param teamId
	 * @throws Exception
	 */
	public void deleteTeamInfoById(String teamId) throws Exception {
		String sql = "update "+TeamInfoModel.TABLE_NAME+" set status = 2 where teamId=?";
		
		this.getJdbcTemplate().update(sql, teamId);
	}
	
	/**
	 * 根据id查询组训的详细信息
	 * @param teamId
	 * @return
	 */
	public TeamInfoModel getTeamInfoById(String teamId) {
		String sql = "select * from " + TeamInfoModel.TABLE_NAME + " where teamId=?";
		
		TeamInfoModel teamModel = null;
		Object[] args = new Object[] {teamId};
		if (getResultCount(sql, args) == 1) {
			teamModel = this.getJdbcTemplate().queryForObject(sql, args, ParameterizedBeanPropertyRowMapper
					.newInstance(TeamInfoModel.class));
		}
		return teamModel;
	}
	
	/**
	 * 根据条件分页查询可用的组训信息列表(根据创建时间进行倒叙查询)
	 * @param page
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryTeamInfoList(Map<String, Object> conditionMap, Page page){
		
		StringBuilder sql = new StringBuilder();
		List<Object> param = new ArrayList<Object>();
		
		sql.append(" select ti.createUser,tui.realName, ti.createTime, ti.crewName, ti.teamId,ti.status, ");
		if(conditionMap != null && conditionMap.get("status") != null 
				&& ((conditionMap.get("status")) + "").equals("1") && conditionMap.get("flag") == null) {
			sql.append(" GROUP_CONCAT(DISTINCT IF (tpi.`status` = 1 ,tpi.positionName,null),'') positionName, ");
		}else{
			sql.append(" GROUP_CONCAT(DISTINCT tpi.positionName) positionName, ");
		}
		sql.append(" ti.shootStartDate, ti.phoneNum, ti.contactAddress, ti.picPath, tpi.status useStatus from tab_team_info ti");
		sql.append(" LEFT JOIN tab_team_position_info tpi on ti.teamId = tpi.teamId ");
		sql.append(" LEFT JOIN tab_user_info tui on tui.userId=ti.createUser ");
		
		if (conditionMap != null && StringUtils.isNotBlank((String)conditionMap.get("storeUserId"))) {
			sql.append(" RIGHT JOIN tab_store_info tsi ON tsi.teamId = ti.teamId");
		}
		
		sql.append(" where 1=1 ");
		
		if (conditionMap != null) {
			//取出map中的当前时间
			Date currentDate = new Date();
			
			//状态
			if(conditionMap.get("status") != null) {
				sql.append(" and ti.status = ? ");
				param.add(conditionMap.get("status"));
			}
			
			
			//判断是否是查询自己发布的组训信息
			String createUser = (String) conditionMap.get("createUser");
			if (StringUtils.isNotBlank(createUser)) {
				sql.append(" and ti.createUser = ?");
				param.add(createUser);
			}
			
			//根据收藏人id进行查询
			String storeUserId = (String) conditionMap.get("storeUserId");
			if (StringUtils.isNotBlank(storeUserId)) {
				sql.append(" AND tsi.userId = ?");
				param.add(storeUserId);
			}
			
			//根据剧组名称查询
			String crewName = (String) conditionMap.get("crewName");
			if (StringUtils.isNotBlank(crewName)) {
				sql.append(" and ti.crewName = ?");
				param.add(crewName);
			}
			
			//根据类型查询
			Integer crewType = (Integer) conditionMap.get("crewType");
			if (crewType != null) {
				sql.append(" and ti.crewType = ?");
				param.add(crewType);
			}
			
			//根据题材进行查询
			String subject = (String) conditionMap.get("subject");
			if (StringUtils.isNotBlank(subject)) {
				sql.append(" and ti.subject = ?");
				param.add(subject);
			}
			
			//根据开机时间进行查询
			Integer startType = (Integer) conditionMap.get("shootStartType");
			if (startType != null && startType != 0) {
				//值为 1 表示查询最近一个月; 2 表示查询最近三个月; 3 表示查询最近半年
				sql.append(" and ti.shootStartDate IS NOT NULL ");
				if (startType == 1) {
					sql.append(" AND TIMESTAMPDIFF(DAY, DATE_FORMAT(now(), '%y-%m-%d'), DATE_FORMAT(ti.shootStartDate, '%y-%m-%d')) <= 30");
				}else if (startType == 2) {
					sql.append(" AND TIMESTAMPDIFF(DAY, DATE_FORMAT(now(), '%y-%m-%d'), DATE_FORMAT(ti.shootStartDate, '%y-%m-%d')) <= 90");
				}else if (startType == 3) {
					sql.append(" AND TIMESTAMPDIFF(DAY, DATE_FORMAT(now(), '%y-%m-%d'), DATE_FORMAT(ti.shootStartDate, '%y-%m-%d')) <= 180");
				}
				sql.append(" AND TIMESTAMPDIFF(DAY, DATE_FORMAT(now(), '%y-%m-%d'), DATE_FORMAT(ti.shootStartDate, '%y-%m-%d')) >= 0 ");
			}
			
			//根据发布时间查询
			Integer createTimeType = (Integer) conditionMap.get("createTimeType");
			if (createTimeType != null && createTimeType != 0) {
				//值为 1表示查询一个星期之内的; 2表示查询最近一个月;3表示近三个月
				if (createTimeType == 1) {
					sql.append(" and TIMESTAMPDIFF(DAY,DATE_FORMAT(ti.createTime,'%y-%m-%d'),DATE_FORMAT(?,'%y-%m-%d')) < 7");
					param.add(currentDate);
				}else if (createTimeType == 2) {
					sql.append(" and TIMESTAMPDIFF(DAY,DATE_FORMAT(ti.createTime,'%y-%m-%d'),DATE_FORMAT(?,'%y-%m-%d')) < 30");
					param.add(currentDate);
				}else if (createTimeType == 3) {
					sql.append(" and TIMESTAMPDIFF(DAY,DATE_FORMAT(ti.createTime,'%y-%m-%d'),DATE_FORMAT(?,'%y-%m-%d')) < 90");
					param.add(currentDate);
				}
			}
		}
		
		//添加排序字段
		sql.append(" GROUP BY ti.teamId order by ti.createTime DESC");
		
		List<Map<String, Object>> resultList = this.query(sql.toString(), param.toArray(), page);
		
		return resultList;
	}
	
	/**
	 * 保存组训中招聘职位信息
	 * @param teamPosition
	 * @throws Exception
	 */
	public void addTeamPositionByBean (TeamPositionInfoModel teamPosition) throws Exception {
		this.add(teamPosition);
	}
	
	/**
	 * 根据对象更新招聘的职位信息
	 * @param teamPosition
	 * @throws Exception
	 */
	public void updateTeamPositionByBean(TeamPositionInfoModel teamPosition) throws Exception {
		StringBuffer sql = new StringBuffer();
		
		sql.append("update " + TeamPositionInfoModel.TABLE_NAME + " set positionName = ?,needPositionId = ?,needPeopleNum = ?,positionRequirement = ?");
		sql.append(" where positionId = ?");
		
		this.getJdbcTemplate().update(sql.toString(), teamPosition.getPositionName(),teamPosition.getNeedPositionId(),teamPosition.getNeedPeopleNum(),teamPosition.getPositionRequirement(),teamPosition.getPositionId());
	}
	
	/**
	 * 根据组训id或则发布的招聘职位的id删除组训中的职位信息(逻辑删除)
	 * @param conditionMap
	 */
	public void deleteTeamPositionBycondition(Map<String, Object> conditionMap) throws Exception{
		StringBuffer sql = new StringBuffer();
		List<String> param = new ArrayList<String>();
		
		sql.append("update " + TeamPositionInfoModel.TABLE_NAME + " set status = 2 where 1=1");
		if (conditionMap != null && conditionMap.size() != 0) {
			String teamId = (String) conditionMap.get("teamId");
			String positionId = (String) conditionMap.get("positionId");
			if (StringUtils.isNotBlank(teamId)) {
				sql.append(" and teamId = ?");
				param.add(teamId);
			}else if (StringUtils.isNotBlank(positionId)) {
				sql.append(" and positionId = ?");
				param.add(positionId);
			}
		}
		
		this.getJdbcTemplate().update(sql.toString(), param.toArray());
	}
	
	/**
	 * 根据条件查询组训信息中的职位列表(不分页查询)
	 * @param conditionMap
	 * @return
	 */
	public List<TeamPositionInfoModel> queryPositionListByCondition(Map<String, Object> conditionMap){
		List<Object> param = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + TeamPositionInfoModel.TABLE_NAME + " where 1=1 ");
		
		if (conditionMap != null) {
			if(conditionMap.get("status") != null) {
				sql.append(" and status=? ");
				param.add(conditionMap.get("status"));
			}
			
			//根据组训id查询
			String teamId = (String) conditionMap.get("teamId");
			if (StringUtils.isNotBlank(teamId)) {
				sql.append(" and teamId = ?");
				param.add(teamId);
			}
			
			//招聘职位id
			String positionId = (String) conditionMap.get("positionId");
			if (StringUtils.isNotBlank(positionId)) {
				sql.append(" and positionId = ?");
				param.add(positionId);
			}
			
			//根据创建者查询
			String createUser = (String) conditionMap.get("createUser");
			if (StringUtils.isNotBlank(createUser)) {
				sql.append(" and createUser = ?");
				param.add(createUser);
			}
			
			//根据职位名称查询
			String positionName = (String) conditionMap.get("needPositionId");
			if (StringUtils.isNotBlank(positionName)) {
				sql.append(" and needPositionId = ?");
				param.add(positionName);
			}
		}
		sql.append(" order by createTime desc");
		
		RowMapper<TeamPositionInfoModel> rm = ParameterizedBeanPropertyRowMapper.newInstance(TeamPositionInfoModel.class);
		List<TeamPositionInfoModel> list = this.getJdbcTemplate().query(sql.toString(), param.toArray(), rm);
		
		return list;
	}
	
	/**
	 * 根据组训id获取当前组训投递简历的人数
	 * @param teamId
	 * @return
	 */
	public List<Map<String, Object>> getTeamResumeCount() {
		String sql = "select count(userId) count, teamId from tab_tean_resume_map group by teamId" ;
		List<Map<String, Object>> list = this.query(sql, null, null);
		
		return list;
	}
	
	/**
	 * 根据职位id获取当前职位投递简历的人数
	 * @param 
	 * @return
	 */
	public List<Map<String, Object>> getPositionResumeCount(String teamId) {
		String sql = "select count(userId) count, positionId from tab_tean_resume_map where teamId = ? group by positionId" ;
		List<Map<String, Object>> list = this.query(sql, new Object[] {teamId}, null);
		
		return list;
	}
	
	/**
	 * 根据组训id查询出组训的投递人信息
	 * @param teamId
	 * @return
	 */
	public List<Map<String, Object>> getResumeUser(String teamId){
		String sql = "select userId from tab_tean_resume_map where teamId = ?";
		List<Map<String, Object>> list = this.query(sql, new Object[] {teamId}, null);
		
		return list;
	}
	
	/**
	 * 保存投递简历信息
	 * @param resumeModel
	 * @throws Exception 
	 */
	public void addApplyTeam(TeamResumeMapModel resumeModel) throws Exception {
		this.add(resumeModel);
	}
	
	/**
	 * 根据条件获取投递简历的记录
	 * @param conditionMap
	 * @return
	 */
	public List<TeamResumeMapModel> queryResumeInfoByCondition(Map<String, Object> conditionMap){
		StringBuffer sql = new StringBuffer();
		List<Object> param = new ArrayList<Object>();
		sql.append(" SELECT * FROM tab_tean_resume_map");
		sql.append(" WHERE 1=1");
		if (conditionMap != null) {
			
			//按组训id收索
			String teamId = (String) conditionMap.get("teamId");
			if (StringUtils.isNotBlank(teamId)) {
				sql.append("	AND teamId = ?");
				param.add(teamId);
			}
			
			//按投递简历人id
			String userId = (String) conditionMap.get("userId");
			if (StringUtils.isNotBlank(userId)) {
				sql.append("	AND userId = ?");
				param.add(userId);
			}
			
			//职位id
			String positionId = (String) conditionMap.get("positionId");
			if (StringUtils.isNotBlank(positionId)) {
				sql.append("	AND positionId = ?");
				param.add(positionId);
			}
		}
		
		return this.query(sql.toString(), param.toArray(), null);
	}
	
	/**
	 * 根据分页信息获取最新发布组讯的宣传照片
	 * @return
	 */
	public List<Map<String, Object>> getTeamPic(Page page){
		String sql = "select picPath,teamId from " + TeamInfoModel.TABLE_NAME + " order by createTime desc";
		List<Map<String, Object>> list = this.query(sql, null, page);
		return list;
	}
	
	/**
	 * 根据组讯ID查询投递简历详情
	 * @param teamId
	 * @return
	 */
	public List<Map<String, Object>> queryTeamInfoApply(String teamId, String positionId) {
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		sql.append(" select ttrm.userId,tui.realName,ttpi.positionName,ttpi.positionId,ttrm.createTime ");
		sql.append(" from tab_tean_resume_map ttrm ");
		sql.append(" inner join tab_user_info tui ");
		sql.append(" on tui.userId = ttrm.userId ");
		sql.append(" inner join tab_team_position_info ttpi ");
		sql.append(" on ttpi.positionId = ttrm.positionId ");
		sql.append(" where ttrm.teamId=? ");
		params.add(teamId);
		if(StringUtils.isNotBlank(positionId)) {
			sql.append(" and ttpi.positionId=? ");
			params.add(positionId);
		}
		
		sql.append(" order by ttpi.createTime desc,ttrm.createTime desc ");
		
		return this.query(sql.toString(), params.toArray(), null);
	}
	
	/**
	 * 根据组讯ID查询收藏详情
	 * @param teamId
	 * @return
	 */
	public List<Map<String, Object>> queryTeamInfoStore(String teamId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT tsi.userId,tui.realName,tui.phone,tsi.createTime ");
		sql.append(" FROM tab_store_info tsi ");
		sql.append(" inner join tab_user_info tui ");
		sql.append(" on tui.userId = tsi.userId ");
		sql.append(" where tsi.teamId=? ");
		sql.append(" order by tsi.createTime desc ");
		return this.query(sql.toString(), new Object[]{teamId}, null);
	}
	
	/**
	 * 根据条件删除组训中职位的投递信息
	 * @param conditionMap
	 */
	public int deleteTeamResumeMapByCondition(Map<String, Object> conditionMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("	DELETE FROM tab_tean_resume_map ");
		if (conditionMap != null) {
			
			//根据组训id删除
			String teamId = (String) conditionMap.get("teamId");
			if (StringUtils.isNotBlank(teamId)) {
				sql.append("	where teamId = ?");
				return this.getJdbcTemplate().update(sql.toString(), teamId);
			}
			
			//根据职位id删除
			String positionId = (String) conditionMap.get("positionId");
			if (StringUtils.isNotBlank(positionId)) {
				sql.append("	where positionId = ?");
				return this.getJdbcTemplate().update(sql.toString(), positionId);
			}
		}
		return 0;
	}
}
