package com.xiaotu.makeplays.roleactor.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.roleactor.controller.filter.ViewRoleFilter;
import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;
import com.xiaotu.makeplays.roleactor.model.constants.ViewRoleType;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.view.controller.filter.ViewFilter;
import com.xiaotu.makeplays.view.model.ViewRoleMapModel;
import com.xiaotu.makeplays.view.model.constants.ShootStatus;

/**
 * 场景角色信息
 * @author xuchangjian
 */
@Repository
public class ViewRoleDao extends BaseDao<ViewRoleModel> {

	/**
	 * 更新数据
	 * @param atmosphere	气氛对象
	 * @throws Exception 
	 */
	public void update(ViewRoleModel viewRole) throws Exception {
		this.updateWithNull(viewRole, "viewRoleId");
	}
	
	/**
	 * 批量更新数据
	 * @throws Exception 
	 */
	public void updateManyAddressInfo(List<ViewRoleModel> viewRoleList) throws Exception {
		for (ViewRoleModel viewRole : viewRoleList) {
			this.update(viewRole);
		}
	}
	
	/**
	 * 根据场景ID查询场景角色信息
	 * @param viewId
	 * @return
	 */
	public List<ViewRoleModel> queryManyByViewId(String viewId) {
		String sql = "select r.* from " + ViewRoleModel.TABLE_NAME + " r, " + ViewRoleMapModel.TABLE_NAME + " rm where r.viewRoleId = rm.viewRoleId and rm.viewId = ? ";
		List<ViewRoleModel> viewRoleList = this.query(sql, new Object[]{viewId}, ViewRoleModel.class, null);
		
		return viewRoleList;
	}
	
	/**
	 * 根据场景ID查询场景角色信息
	 * 该查询方法会一起查询出演员的数量信息
	 * @param viewId
	 * @return
	 */
	public List<Map<String, Object>> queryViewRoleByViewId(String viewId) {
		String sql = "select r.*, rm.roleNum from " + ViewRoleModel.TABLE_NAME + " r, " + ViewRoleMapModel.TABLE_NAME + " rm where r.viewRoleId = rm.viewRoleId and rm.viewId = ? ";
		List<Map<String, Object>> viewRoleList = this.query(sql, new Object[]{viewId}, null);
		
		return viewRoleList;
	}
	

	/**
	 * 查询所有演员，并根据类型
	 * 该方法会查询出场景表ID信息
	 * @param viewIds	场景表ID，格式：'1','2',''
	 * @param roleType 角色类型，可为空
	 * @return
	 */
	public List<Map<String, Object>> queryViewRoleListByViewId(String viewIds,Integer roleType){
		
		String sql = " select tsr.*,tsrm.viewId,tsrm.roleNum from tab_view_role_map tsrm, ";
		sql += " tab_view_role tsr where tsr.viewRoleId=tsrm.viewRoleId ";
		if (roleType != null) {
			sql += " and tsr.viewRoleType="+roleType.intValue();
		}
		sql += " and tsrm.viewId in ("+viewIds+") order by tsrm.viewId";
		
		return this.query(sql, null, null);
		
	}
	
	/**
	 * 查询场景地、角色关系
	 * @param crewId
	 * @param roleType
	 * @param locationType
	 * @param crewRole 查询条件--角色
	 * @param flag 1:显示，2：不显示
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryLocationRoleListByCrewId(
			String crewId, int locationType, ViewFilter filter) {
		StringBuilder sql = new StringBuilder();
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(crewId);
		paramList.add(locationType);
		sql.append(" select distinct ifnull(tsi.id,'') as shootLocationId,tvlm.locationId,tvl.location,tvr.* ");
		sql.append(" from tab_view_role tvr ");
		sql.append(" LEFT JOIN tab_view_role_map tvrm ON tvrm.viewRoleId = tvr.viewRoleId ");
		sql.append(" LEFT JOIN tab_view_location_map tvlm ON tvlm.viewId = tvrm.viewId ");
		sql.append(" LEFT JOIN tab_view_location tvl ON tvl.locationId = tvlm.locationId ");
		sql.append(" INNER JOIN tab_view_info tvi ON tvi.viewId = tvrm.viewId ");
		sql.append(" left join tab_sceneview_info tsi on tvi.shootLocationId=tsi.id ");
		sql.append(" where tvr.crewId = ? ");
//		sql.append(" 	and tvi.shootStatus != 3 "); //去掉删戏
		sql.append(" 	and tvr.isAttentionRole = 1 "); //特别关注角色
		sql.append("    AND (tvl.locationType = ? or tvl.locationType is null)");
		/*if(StringUtils.isNotBlank(crewRole)) {
			if(flag.equals("1")) {
				sql.append(" and tvr.viewRoleId in ('" + crewRole.replace(",", "','") + "')");
			} else {
				sql.append(" and tvr.viewRoleId not in ('" + crewRole.replace(",", "','") + "')");
			}
		}*/
		if(StringUtils.isNotBlank(filter.getRoles())){
			boolean blankFlag = false;	//标识所选演员中是否有选择[空]的情况
			String[] roleIdArr = filter.getRoles().split(",");	
			String notBlankRoleIds = "";	//不包含[空]的情况下的所有选中的演员信息
			
			for(int i = 0; i < roleIdArr.length; i++){
				String roleId = roleIdArr[i];
				if (roleId.equals("blank")) {
					blankFlag = true;
				} else {
					notBlankRoleIds += roleId;
					if(i < roleIdArr.length - 1) {
						notBlankRoleIds += ",";
					}
				}
			}
			
			//选择的角色同时存在的场次
			if(StringUtils.isBlank(filter.getSearchMode())||"2".equals(filter.getSearchMode())){
				//如果选择的演员中有非空的情况，执行以下逻辑
				if (!StringUtils.isBlank(notBlankRoleIds)) {
					notBlankRoleIds = notBlankRoleIds.substring(0, notBlankRoleIds.length());
					String roles = "'" + notBlankRoleIds.replaceAll(",", "','") + "'";
					sql.append(" and EXISTS ( select tsrmRes.viewId from ( ");
					sql.append(" select tsrm.viewId,group_concat(tsrm.viewRoleId) roleIdStr from tab_view_role_map tsrm ");
					sql.append(" where tsrm.viewRoleId in (" + roles + ") group by tsrm.viewId ) tsrmRes ");
					sql.append(" where tsrmRes.viewId=tvi.viewId ");

					String[] roleArray = notBlankRoleIds.split(",");
					for(String roleId:roleArray){
						sql.append(" and tsrmRes.roleIdStr like '%" + roleId + "%' ");
					}
					sql.append(" ) ");
				}
				
				//如果选择的演员中有[空]的情况，执行以下逻辑
				if (blankFlag) {
					sql.append(" and not EXISTS (select 1 from tab_view_role_map tvrm where tvrm.viewId = tvi.viewId and tvrm.crewId = ?) ");
					paramList.add(crewId);
				}
				
			}else if("0".equals(filter.getSearchMode())){
				//选择存在即可的场次
				if (!StringUtils.isBlank(notBlankRoleIds)) {
					String roles = "'"+notBlankRoleIds.replaceAll(",", "','")+"'";
					sql.append(" and EXISTS ( select * from tab_view_role_map tsrm where tsrm.viewId=tvi.viewId and tsrm.viewRoleId in (" + roles + ")) ");
				}
				
				if (blankFlag) {
					sql.append(" and not EXISTS (select 1 from tab_view_role_map tvrm, tab_view_role tvr where tvrm.viewId = tvi.viewId and tvrm.crewId = ? and tvr.viewRoleId = tvrm.viewRoleId and tvr.viewRoleType = 1)");
					paramList.add(crewId);
				}
				
			}else if("1".equals(filter.getSearchMode())){//不出现
				if (!StringUtils.isBlank(notBlankRoleIds)) {
					//选择同时不出现的场次
					sql.append(" and not EXISTS ( select tsrmRes.viewId from (select tsrm.viewId,group_concat(tsrm.viewRoleId) roleIdStr from tab_view_role_map tsrm "
							+ " group by tsrm.viewId ) tsrmRes where tsrmRes.viewId=tvi.viewId and (" );

					String[] roleArray = notBlankRoleIds.split(",");
					for(int i = 0; i < roleArray.length; i++){
						String roleId = roleArray[i];
						sql.append(" tsrmRes.roleIdStr like '%" + roleId + "%' ");
						if(i < roleArray.length - 1) {
							sql.append(" or ");
						}
					}
					sql.append(" ) ) ");
				}
				
				if (blankFlag) {
					sql.append("and EXISTS (select 1 from tab_view_role_map tvrm, tab_view_role tvr where tvrm.viewId = tvi.viewId and tvrm.crewId= ? and tvr.viewRoleId = tvrm.viewRoleId and tvr.viewRoleType = 1)");
					paramList.add(crewId);
				}
			}
		}
		return this.query(sql.toString(), paramList.toArray(), null);
	}
	
	/**
	 * 查询所有演员，并根据类型,
	 * 该方法查询的结果会去除重复的角色数据
	 * 该方法返回角色信息对象，不会带所属场景ID的信息
	 * @param viewIds	场景表ID，格式：'1','2',''
	 * @param roleType 角色类型，可为空
	 * @return
	 */
	public List<ViewRoleModel> queryViewRoleListViewIdSign(String viewIds,Integer roleType){
		
		if(StringUtils.isBlank(viewIds)){
			return null;
		}
		
		String sql = " select distinct tsr.* from tab_view_role_map tsrm, ";
		sql += " tab_view_role tsr where tsr.viewRoleId=tsrm.viewRoleId ";
		if (roleType != null) {
			sql += " and tsr.viewRoleType="+roleType.intValue();
		}
		sql	+= " and tsrm.viewId in ("+viewIds+") order by tsrm.viewId ";
		
		return this.query(sql, null,ViewRoleModel.class, null);
		
	}
	
	/**
	 * 根据多个条件查询演员角色信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ViewRoleModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + ViewRoleModel.TABLE_NAME + " where 1 = 1 ");
		
		Set<String> keySet = conditionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		List<Object> conList = new LinkedList<Object>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = conditionMap.get(key);
			sql.append(" and " + key + " = ?");
			conList.add(value);
		}
		Object[] objArr = conList.toArray();
		List<ViewRoleModel> viewRoleList = this.query(sql.toString(), objArr, ViewRoleModel.class, page);
		
		return viewRoleList;
	}
	
	/**
	 * 根据多个条件查询演员角色信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ViewRoleModel> queryManyByCrewIdAndTypeAndName(String crewId, Integer viewRoleType, String name) {
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		params.add(crewId);
		params.add(viewRoleType);
		sql.append(" select * from " + ViewRoleModel.TABLE_NAME + " where crewId=? and viewRoleType=? ");
		if(StringUtils.isNotBlank(name)) {
			sql.append(" and viewRoleName like ? ");
			params.add("%" + name + "%");
		}
		sql.append(" order by sequence ");
		
		List<ViewRoleModel> viewRoleList = this.query(sql.toString(), params.toArray(), ViewRoleModel.class, null);		
		return viewRoleList;
	}
	
	
	/**
	 * 根据剧本ID查找对应的场景角色信息
	 * @param crewId 剧本ID
	 * @return
	 */
	public List<ViewRoleModel> queryByCrewId(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from ");
		sql.append(" (select tvr.*, count(tvrm.mapId)  viewRoleCount ");
		sql.append(" from tab_view_role tvr ");
		sql.append(" LEFT JOIN tab_view_role_map tvrm on tvr.viewRoleId = tvrm.viewRoleId");
		sql.append(" where tvr.crewId = ? ");
		sql.append(" GROUP BY tvr.viewRoleId) obj ORDER BY obj.viewRoleCount desc, convert(obj.viewRoleName using gbk)");
		
		return this.query(sql.toString(), new Object[] {crewId}, ViewRoleModel.class, null);
	}
	
	/**
	 * 根据剧本ID查找对应的场景角色信息
	 * 该方法只查询出在所有场景中出现的场景角色
	 * @param crewId 剧本ID
	 * @return
	 */
	public List<ViewRoleModel> queryManyOnlyExistsInCrewView(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from ");
		sql.append(" (select tvr.*, count(tvrm.mapId)  viewRoleCount ");
		sql.append(" from tab_view_role tvr, tab_view_role_map tvrm  ");
		sql.append(" where tvr.crewId = ? ");
		sql.append("   and tvr.viewRoleId = tvrm.viewRoleId");
		sql.append(" GROUP BY tvr.viewRoleId) obj ORDER BY obj.viewRoleCount desc, convert(obj.viewRoleName using gbk)");
		
		return this.query(sql.toString(), new Object[] {crewId}, ViewRoleModel.class, null);
	}
	
	/**
	 * 根据剧本ID和角色类型查找对应的场景角色信息
	 * @param crewId 剧本ID
	 * @param viewRoleType 场景角色类型
	 * @return 场景角色列表，先按照角色数量排序，然后按照角色名称排序
	 */
	public List<ViewRoleModel> queryManyByCrewIdAndRoleType(String crewId, int viewRoleType) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from ");
		sql.append(" (select tvr.*, count(tvrm.mapId)  viewRoleCount ");
		sql.append(" from tab_view_role tvr ");
		sql.append(" LEFT JOIN tab_view_role_map tvrm on tvr.viewRoleId = tvrm.viewRoleId");
		sql.append(" where tvr.crewId = ? ");
		sql.append("   and tvr.viewRoleType = ?");
		sql.append(" GROUP BY tvr.viewRoleId) obj ORDER BY obj.viewRoleCount desc, convert(obj.viewRoleName using gbk)");
		
		return this.query(sql.toString(), new Object[] {crewId, viewRoleType}, ViewRoleModel.class, null);
	}
	
	/**
	 * 根据剧本ID和角色类型查找对应的场景角色信息
	 * @param crewId 剧本ID
	 * @param viewRoleType 场景角色类型
	 * @param excludeRoles 需要特殊处理的角色
	 * @param flag 显示，不显示
	 * @return 场景角色列表，先按照角色数量排序，然后按照角色名称排序
	 */
	public List<ViewRoleModel> queryManyByIdAndTypeExcludeSome(String crewId,
			int viewRoleType, ViewFilter filter) {
		StringBuilder sql = new StringBuilder();
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(crewId);
		paramList.add(viewRoleType);
		
		sql.append(" SELECT ");
		sql.append(" 	* ");
		sql.append(" FROM ");
		sql.append(" 	tab_view_role tvr ");
		sql.append(" WHERE ");
		sql.append(" 	tvr.crewId = ? ");
		sql.append(" AND tvr.viewRoleType = ? ");
		sql.append(" ORDER BY ");
		sql.append(" 	sequence ");
		
		return this.query(sql.toString(), paramList.toArray(), ViewRoleModel.class, null);
	}
	
	/**
	 * 根据剧本ID和是否是关注角色查找对应的场景角色信息
	 * @param crewId 剧本ID
	 * @param viewRoleType 场景角色类型
	 * @param excludeRoles 需要特殊处理的角色
	 * @param flag 显示，不显示
	 * @return 场景角色列表，先按照角色数量排序，然后按照角色名称排序
	 */
	public List<ViewRoleModel> queryManyByIdAndIsAttentionRole(String crewId) {
		StringBuilder sql = new StringBuilder();
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(crewId);
		
	
		sql.append(" SELECT ");
		sql.append(" 	* ");
		sql.append(" FROM ");
		sql.append(" 	tab_view_role tvr ");
		sql.append(" WHERE ");
		sql.append(" 	tvr.crewId = ? ");
		sql.append(" AND tvr.isAttentionRole =1 ");
		sql.append(" ORDER BY ");
		sql.append(" 	sequence ");
		
		return this.query(sql.toString(), paramList.toArray(), ViewRoleModel.class, null);
	}
	
	/**
	 * 根据剧本ID和角色类型查找对应的场景角色信息
	 * 该方法会查询出角色拥有的戏量
	 * @param crewId 剧本ID
	 * @param viewRoleType 场景角色类型
	 * @return 场景角色所有信息，角色拥有的戏量，对应的演员ID，对应的演员名称
	 */
	public List<Map<String, Object>> queryRoleMapByCrewIdAndRoleType(String crewId, int viewRoleType) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from ");
		sql.append(" (select tvr.*, count(tvrm.mapId)  viewRoleCount, tai.actorId, tai.actorName ");
		sql.append(" from tab_view_role tvr ");
		sql.append(" LEFT JOIN tab_view_role_map tvrm on tvr.viewRoleId = tvrm.viewRoleId");
		sql.append(" LEFT JOIN tab_actor_role_map tarm on tarm.viewRoleId = tvr.viewRoleId ");
		sql.append(" LEFT JOIN tab_actor_info tai on tai.actorId = tarm.actorId ");
		sql.append(" where tvr.crewId = ? ");
		sql.append("   and tvr.viewRoleType = ?");
		sql.append(" GROUP BY tvr.viewRoleId) obj");
		sql.append(" ORDER BY obj.sequence, obj.viewRoleCount desc, obj.viewRoleName ");
		
		return this.query(sql.toString(), new Object[] {crewId, viewRoleType}, null);
	}
	
	/**
	 * 获取用户关注的角色信息
	 * @param userId
	 * @return
	 */
	public List<ViewRoleModel> queryFocusRoleByUserId(String crewId, String userId) {
		String sql = "select tvr.* from tab_user_focusrole_map tufm, tab_view_role tvr where tufm.crewId = ? and tufm.userId = ? and tvr.viewRoleId = tufm.viewRoleId";
		return this.query(sql, new Object[] {crewId, userId}, ViewRoleModel.class, null);
	}
	
	/**
	 * 获取当前用户扮演的角色
	 */
	public List<ViewRoleModel> queryUserRoleInfo(String crewId,String userId){
		String sql = "SELECT tvr.* FROM tab_crewRole_user_map tcum,tab_view_role tvr WHERE tcum.viewRoleId = tvr.viewRoleId AND tcum.crewId = ? AND tcum.userId = ? ";
		
		return this.query(sql, new Object[] {crewId, userId}, ViewRoleModel.class, null);
	}
	
	/**
	 * 查询通告单下的演员信息
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	public List<Map<String, Object>> queryManyByNoticeId(String crewId, String noticeId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	* ");
		sql.append(" FROM ");
		sql.append(" 	( ");
		sql.append(" 		SELECT ");
		sql.append(" 			tvr.viewRoleId, ");
		sql.append(" 			tvr.viewRoleName, ");
		sql.append(" 			tvr.viewRoleType, ");
		sql.append("            tvr.crewId, ");
		sql.append("            tvnm.noticeId, ");
		sql.append(" 			count(tvrm.mapId) viewCount, ");
		sql.append(" 			sum(tvrm.roleNum) totalRoleNum, ");
		sql.append(" 			tai.actorName ");
		sql.append(" 		FROM ");
		sql.append(" 			tab_view_role tvr ");
		sql.append(" 			LEFT JOIN tab_actor_role_map tarm ON tvr.viewRoleId = tarm.viewRoleId ");
		sql.append(" 			LEFT JOIN tab_actor_info tai ON tarm.actorId = tai.actorId, ");
		sql.append(" 			tab_view_role_map tvrm, ");
		sql.append(" 			tab_view_notice_map tvnm ");
		sql.append(" 		WHERE ");
		sql.append(" 			tvr.viewRoleId = tvrm.viewRoleId ");
		sql.append(" 		AND tvr.crewId = ? ");
		sql.append(" 		AND tvrm.viewId = tvnm.viewId ");
		sql.append(" 		AND tvrm.crewId = ? ");
		sql.append(" 		AND tvnm.noticeId = ? ");
		sql.append(" 		AND tvnm.crewId = ? ");
		sql.append(" 		GROUP BY ");
		sql.append(" 			tvr.viewRoleId ");
		sql.append(" 	) t ");
		sql.append(" ORDER BY ");
		sql.append(" 	t.viewCount desc ");
		
		return this.query(sql.toString(), new Object[] {crewId, crewId, noticeId, crewId}, null);
	}
	
	/**
	 * 获取剧组角色列表
	 * @param crewId	剧组ID
	 * @param viewRoleFilter 高级查询条件
	 * @return	角色ID，角色名称，角色类型，简称，演员ID，演员姓名，入组时间，离组时间，总场数，总页数，已完成场数，未完成场数，请假次数，请假天数
	 */
	public List<Map<String, Object>> queryViewRoleList(String crewId, ViewRoleFilter viewRoleFilter) {
		List<Object> params = new ArrayList<Object>();
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT sub.*,  ");
		sql.append(" 	sum(if(talr.id is NULL, 0, 1)) leaveCount,  ");
		sql.append(" 	sum(talr.leaveDays) totalLeaveDays  ");
		sql.append(" 	from ( ");
		sql.append(" 		SELECT ");
		sql.append(" 			tvr.viewRoleId, ");
		sql.append(" 			tvr.viewRoleName, ");
		sql.append(" 			tvr.viewRoleType, ");
		sql.append(" 			tvr.shortName, ");
		sql.append(" 			tvr.sequence, ");
		sql.append(" 			tai.actorId, ");
		sql.append(" 			tai.actorName, ");
		sql.append(" 			tai.enterDate, ");
		sql.append(" 			tai.leaveDate, ");
		sql.append(" 			tai.shootDays, ");
		sql.append(" 			sum(IF(tvi.shootStatus = 0 || tvi.shootStatus = 1 || tvi.shootStatus = 2, 1, 0)) totalViewCount, ");
		sql.append(" 			ROUND(sum(if(tvi.shootStatus = 3 || tvi.pageCount is NULL, 0, tvi.pageCount)), 3) totalPageCount, ");
		sql.append(" 			sum(if(tvi.shootStatus = 2, 1, 0)) finishedViewCount, ");
		sql.append(" 			sum(if(tvi.shootStatus = 0 || tvi.shootStatus = 1, 1, 0)) unfinishedViewCount,ttvrm.seriesViewNo, ");
		sql.append("			tvr.isAttentionRole,");
		sql.append("			tai.workHours,");
		sql.append("			tai.restHours");
		sql.append(" 		FROM ");
		sql.append(" 			tab_view_role tvr ");
		sql.append(" 		LEFT JOIN tab_actor_role_map tarm ON tarm.viewRoleId = tvr.viewRoleId ");
		sql.append(" 		AND tarm.crewId = ? ");
		params.add(crewId);
		sql.append(" 		LEFT JOIN tab_actor_info tai ON tai.actorId = tarm.actorId ");
		sql.append(" 		AND tai.crewId = ? ");
		params.add(crewId);
		sql.append(" 		LEFT JOIN tab_view_role_map tvrm ON tvrm.viewRoleId = tvr.viewRoleId ");
		sql.append(" 		AND tvrm.crewId = ? ");
		params.add(crewId);
		sql.append(" 		LEFT JOIN tab_view_info tvi ON tvi.viewId = tvrm.viewId ");
		sql.append(" 		AND tvi.crewId = ? ");
		params.add(crewId);
		sql.append("		LEFT JOIN ( SELECT tvrm.viewRoleId, tvr.viewRoleName, GROUP_CONCAT(tvi.seriesNo, '-', tvi.viewNo ORDER BY tvi.seriesNo ASC,abs(tvi.viewNo) ASC,tvi.viewNo) seriesViewNo");
		sql.append("		FROM tab_view_role_map tvrm LEFT JOIN tab_view_info tvi ON tvrm.viewId = tvi.viewId");
		sql.append("		LEFT JOIN tab_view_role tvr ON tvr.viewRoleId = tvrm.viewRoleId");
		sql.append("		WHERE tvrm.crewId = ? GROUP BY tvrm.viewRoleId ORDER BY tvi.seriesNo ASC,abs(tvi.viewNo) ASC,tvi.viewNo) ttvrm ON ttvrm.viewRoleId =tvr.viewRoleId");
		params.add(crewId);
		sql.append(" 		WHERE ");
		sql.append(" 			tvr.crewId = ? ");
		params.add(crewId);
		sql.append(" 		GROUP BY ");
		sql.append(" 			tvr.viewRoleId, ");
		sql.append(" 			tvr.viewRoleName, ");
		sql.append(" 			tvr.viewRoleType, ");
		sql.append(" 			tvr.shortName, ");
		sql.append(" 			tai.actorId, ");
		sql.append(" 			tai.actorName, ");
		sql.append(" 			tai.enterDate, ");
		sql.append(" 			tai.leaveDate) sub ");
		sql.append(" LEFT JOIN tab_actor_leave_record talr ON talr.actorId = sub.actorId AND talr.crewId = ? ");
		params.add(crewId);

		sql.append(" WHERE 1 = 1 ");
		if (!StringUtils.isBlank(viewRoleFilter.getViewRoleName())) {
			sql.append(" AND sub.viewRoleName like ? ");
			params.add("%" + viewRoleFilter.getViewRoleName() + "%");
		}
		if (viewRoleFilter.getViewRoleType() != null) {
			sql.append(" AND sub.viewRoleType = ? ");
			params.add(viewRoleFilter.getViewRoleType());
		}
		if (viewRoleFilter.getMinViewCount() != null) {
			sql.append(" AND sub.totalViewCount >= ? ");
			params.add(viewRoleFilter.getMinViewCount());
		}
		if (viewRoleFilter.getMaxViewCount() != null) {
			sql.append(" AND sub.totalViewCount <= ? ");
			params.add(viewRoleFilter.getMaxViewCount());
		}
		if (viewRoleFilter.getMinFinished() != null) {
			sql.append(" AND (sub.finishedViewCount/sub.totalViewCount*100) >= ?");
			params.add(viewRoleFilter.getMinFinished());
		}
		if (viewRoleFilter.getMaxFinished() != null) {
			sql.append(" AND (sub.finishedViewCount/sub.totalViewCount*100) <= ?");
			params.add(viewRoleFilter.getMaxFinished());
		}
		
		sql.append(" GROUP BY  ");
		sql.append(" 		sub.viewRoleId, ");
		sql.append(" 		sub.viewRoleName, ");
		sql.append(" 		sub.viewRoleType, ");
		sql.append(" 		sub.shortName, ");
		sql.append(" 		sub.actorId, ");
		sql.append(" 		sub.actorName, ");
		sql.append(" 		sub.enterDate, ");
		sql.append(" 		sub.leaveDate ");
		sql.append(" ORDER BY ");
		sql.append(" 	sub.viewRoleType, sub.sequence, sub.totalViewCount DESC, sub.viewRoleName; ");
		
		return this.query(sql.toString(), params.toArray(), null);
	}
	
	/**
	 * 根据ID查找场景角色
	 * @param viewRoleId
	 * @return
	 * @throws Exception 
	 */
	public ViewRoleModel queryById(String viewRoleId) throws Exception {
		String sql = "select * from tab_view_role where viewRoleId = ?";
		return this.queryForObject(sql, new Object[] {viewRoleId}, ViewRoleModel.class);
	}
	
	/**
	 * 根据ID查找场景角色
	 * @param viewRoleId
	 * @return	场景角色信息，对应的演员信息
	 * @throws Exception 
	 */
	public Map<String, Object> queryByIdWithActorInfo(String viewRoleId) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tvr.viewRoleId, ");
		sql.append(" 	tvr.viewRoleName, ");
		sql.append(" 	tvr.shortName, ");
		sql.append(" 	tai.actorId, ");
		sql.append(" 	tai.actorName, ");
		sql.append(" 	tai.sex, ");
		sql.append(" 	tai.enterDate, ");
		sql.append(" 	tai.leaveDate ");
		sql.append(" FROM ");
		sql.append(" 	tab_view_role tvr ");
		sql.append(" LEFT JOIN tab_actor_role_map tarm ON tarm.viewRoleId = tvr.viewRoleId ");
		sql.append(" LEFT JOIN tab_actor_info tai ON tai.actorId = tarm.actorId ");
		sql.append(" WHERE ");
		sql.append(" 	tvr.viewRoleId = ? ");
		return this.getJdbcTemplate().queryForMap(sql.toString(), viewRoleId);
	}
	
	/**
	 * 批量设置角色类型
	 * @param viewRoleIds	场景角色ID，多个用逗号隔开
	 * @param viewRoleType	角色类型
	 */
	public void updateViewRoleTypeBatch(String viewRoleIds, Integer viewRoleType) {
		viewRoleIds = "'" + viewRoleIds.replace(",", "','") +"'";
		String sql = "update tab_view_role set viewRoleType = ? where viewRoleId in("+ viewRoleIds +")";
		
		this.getJdbcTemplate().update(sql, new Object[] {viewRoleType});
	}
	
	/**
	 * 批量设置或取消关注角色
	 * @param viewRoleIds
	 * @param isAttentionRole
	 */
	public void updateViewRoleAttentionBatch(String viewRoleId, Boolean isAttentionRole) {
		String sql = "update tab_view_role set isAttentionRole = ? where viewRoleId = ? ";
		
		this.getJdbcTemplate().update(sql, new Object[] {isAttentionRole, viewRoleId});
	}
	
	/**
	 * 查询当前剧组的关注角色
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryAttentionRoleList(String crewId){
		String sql = " select * from tab_view_role where crewId = ? and isAttentionRole = 1";
		return this.query(sql, new Object[] {crewId}, null);
	}
	
	/**
	 * 根据场景角色ID删除角色
	 * 多个角色用逗号隔开
	 * @param viewRoleIds
	 */
	public void deleteByViewRoleIds (String viewRoleIds) {
		viewRoleIds = "'" + viewRoleIds.replace(",", "','") +"'";
		String sql = "delete from tab_view_role where viewRoleId in ("+ viewRoleIds +")";
		this.getJdbcTemplate().update(sql);
	}
	
	/**
	 * 根据演员ID查询对应的角色
	 * @param actorId
	 * @return
	 * @throws Exception 
	 */
	public ViewRoleModel queryByActorId(String actorId) throws Exception {
		String sql = "select tvr.* from tab_view_role tvr, tab_actor_role_map tarm where tarm.actorId = ? and tarm.viewRoleId = tvr.viewRoleId";
		return this.queryForObject(sql, new Object[] {actorId}, ViewRoleModel.class);
	}
	
	/**
	 * 根据角色名称查询剧组中的角色，
	 * 如果角色ID不为空，则查询结果排除角色自己信息
	 * @param crewId
	 * @param viewRoleId	角色ID
	 * @param viewRoleName	角色名称
	 * @return
	 */
	public List<ViewRoleModel> queryByViewRoleNameExpOne(String crewId, String viewRoleId, String viewRoleName) {
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(crewId);
		paramList.add(viewRoleName);
		
		String sql = "select * from tab_view_role where crewId = ? and viewRoleName = ? ";
		if (!StringUtils.isBlank(viewRoleId)) {
			sql += " and viewRoleId != ? ";
			paramList.add(viewRoleId);
			
		}
		return this.query(sql, paramList.toArray(), ViewRoleModel.class, null);
	}
	
	/**
	 * 根据角色名称查询剧组中的角色，
	 * 如果角色ID不为空，则查询结果排除角色自己信息
	 * 和queryByViewRoleNameExpOne方法的区别是该方法排除多个角色ID
	 * @param crewId
	 * @param viewRoleIds	角色ID，多个用逗号隔开
	 * @param viewRoleName	角色名称
	 * @return
	 */
	public List<ViewRoleModel> queryByViewRoleNameExpMany(String crewId, String viewRoleIds, String viewRoleName) {
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(crewId);
		paramList.add(viewRoleName);
		
		String sql = "select * from tab_view_role where crewId = ? and viewRoleName = ? ";
		if (!StringUtils.isBlank(viewRoleIds)) {
			viewRoleIds = "'" + viewRoleIds.replace(",", "','") + "'";
			
			sql += " and viewRoleId not in ("+ viewRoleIds +") ";
		}
		return this.query(sql, paramList.toArray(), ViewRoleModel.class, null);
	}
	
	/**
	 * 首页统计数据
	 */
	public List<Map<String,Object>> getIndexCount(String crewId){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT role.viewRoleName, COUNT(view.viewId) crewAmountByview ,IF(coun.count is null,0,coun.count) as endcount ");
		sql.append("FROM tab_view_role role  ");
		sql.append("LEFT JOIN (tab_view_role_map srm,tab_view_info view) ON role.viewRoleId=srm.viewRoleId AND srm.viewId=view.viewId  ");
		sql.append("LEFT JOIN ( ");
		sql.append("SELECT role.viewRoleId,count(*) as count FROM tab_view_role role  ");
		sql.append("LEFT JOIN (tab_view_role_map srm,tab_view_info view) ON (role.viewRoleId=srm.viewRoleId AND srm.viewId=view.viewId)  ");
		sql.append("WHERE role.crewId=? and `view`.shootStatus in(2,3) GROUP BY role.viewRoleId ) as coun ON role.viewRoleId=coun.viewRoleId  ");
		sql.append("WHERE role.crewId=? AND role.isAttentionRole =1  GROUP BY role.viewRoleId ORDER BY role.viewRoleType, role.sequence, crewAmountByview DESC ");
		List<Map<String,Object>> li = this.query(sql.toString(), new Object[]{crewId,crewId}, null);
		return li;
	}
	
	/**
	 * 查询演员已拍摄天数
	 * @param viewRoleId
	 * @return
	 */
	public Map<String, Object> queryViewRoleFinishedDays(String viewRoleId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	count(DISTINCT tni.noticeDate) finishedDays ");
		sql.append(" FROM ");
		sql.append(" 	tab_notice_info tni, ");
		sql.append(" 	tab_view_notice_map tvnm, ");
		sql.append(" 	tab_view_role_map tvrm ");
		sql.append(" WHERE ");
		sql.append(" 	tvnm.noticeId = tni.noticeId ");
		sql.append(" AND tvrm.viewId = tvnm.viewId ");
		sql.append(" AND tvrm.viewRoleId = ? ");
		sql.append(" AND ( ");
		sql.append(" 	tvnm.shootStatus = 2 ");
		sql.append(" 	OR tvnm.shootStatus = 5 ");
		sql.append(" ) ");
		return this.getJdbcTemplate().queryForMap(sql.toString(), new Object[]{viewRoleId});
	}
	
	/**
	 * 查询演员计划拍摄天数
	 * @param viewRoleId
	 * @return
	 */
	public List<Map<String, Object>> queryViewRoleShootDays(String viewRoleId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT tai.shootDays ");
		sql.append(" FROM tab_actor_info tai,tab_actor_role_map tarm ");
		sql.append(" WHERE tarm.actorId=tai.actorId ");
		sql.append(" and tarm.viewRoleId=? ");
		return this.query(sql.toString(), new Object[]{viewRoleId}, null);
	}
	
	/**
	 * 查询角色日拍摄量信息
	 * @param viewRoleId
	 * @return
	 */
	public List<Map<String, Object>> queryRoleViewStatistic(String viewRoleId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tni.noticeDate, ");
		sql.append(" 	count(1) viewCount, ");
		sql.append(" 	round(ifnull(sum(tvi.pageCount),0),2) pageCount ");
		sql.append(" FROM ");
		sql.append(" 	tab_view_role_map tvrm, ");
		sql.append(" 	tab_view_info tvi, ");
		sql.append(" 	tab_view_notice_map tvnm, ");
		sql.append(" 	tab_notice_info tni ");
		sql.append(" WHERE ");
		sql.append(" 	tvrm.viewRoleId = ? ");
		sql.append(" AND tvi.viewId = tvrm.viewId ");
		sql.append(" AND tvnm.viewId = tvi.viewId ");
		sql.append(" AND ( ");
		sql.append(" 	tvnm.shootStatus = 2 ");
		sql.append(" 	OR tvnm.shootStatus = 5 ");
		sql.append(" ) ");
		sql.append(" AND tni.noticeId = tvnm.noticeId ");
		sql.append(" group by tni.noticeDate; ");
		return this.query(sql.toString(), new Object[] {viewRoleId}, null);
	}
	
	/**
	 * 更新角色表排序字段
	 * @param viewRoleIdArray
	 */
	public void updateViewRoleSequence(String[] viewRoleIdArray){
		String sql = "update "+ViewRoleModel.TABLE_NAME+" set sequence = ? where viewRoleId = ?";
		int sequence = 1;
		List<Object[]> paramList = new ArrayList<Object[]>();
		Object[] arg = null;
		for (String id : viewRoleIdArray) {
			arg = new Object[2];
			arg[0] = sequence++;
			arg[1] = id;
			paramList.add(arg);
		}
		this.getJdbcTemplate().batchUpdate(	sql, paramList);
	}
	
	/**
	 * 更新角色类型
	 * @param viewRoleIdArray
	 */
	public void updateViewRoleType(List<Object[]> paramList){
		String sql = "update "+ViewRoleModel.TABLE_NAME+" set viewRoleType = ? where viewRoleId = ?";
		this.getJdbcTemplate().batchUpdate(sql, paramList);
	}
	
	/**
	 * 查询通告单下场景中所有角色信息
	 * 包含角色在该通告单中的化妆信息
	 * @param viewIds
	 * @param roleType
	 * @param noticeId
	 * @return
	 */
	public List<Map<String, Object>> queryViewRoleListByNoticeId(String noticeId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	res.* ");
		sql.append(" FROM ");
		sql.append(" 	( ");
		sql.append(" 		SELECT ");
		sql.append(" 			tsr.viewRoleName, ");
		sql.append(" 			tsr.crewId, ");
		sql.append(" 			tsr.shortName, ");
		sql.append(" 			tsr.viewRoleId, ");
		sql.append(" 			tsr.viewRoleType, ");
		sql.append(" 			tsr.sequence, ");
		sql.append(" 			tai.actorId, ");
		sql.append(" 			tai.actorName, ");
		sql.append(" 			sum(tsrm.roleNum) roleNum, ");
		sql.append(" 			count(tsrm.mapId) viewRoleCount, ");
		sql.append(" 			max(tnrt.createTime) createTime, ");
		sql.append(" 			tnrt.makeup, ");
		sql.append(" 			tnrt.arriveTime, ");
		sql.append(" 			tnrt.giveMakeupTime ");
		sql.append(" 		FROM ");
		sql.append(" 			tab_view_role_map tsrm ");
		sql.append(" 		LEFT JOIN tab_view_role tsr ON tsr.viewRoleId = tsrm.viewRoleId ");
		sql.append(" 		LEFT JOIN tab_actor_role_map tarm ON tarm.viewRoleId = tsrm.viewRoleId ");
		sql.append(" 		LEFT JOIN tab_actor_info tai ON tai.actorId = tarm.actorId ");
		sql.append(" 		LEFT JOIN tab_notice_role_time tnrt ON tnrt.viewRoleId = tsrm.viewRoleId AND tnrt.noticeId = ?, ");
		sql.append(" 		tab_view_notice_map tvnm ");
		sql.append(" 		WHERE ");
		sql.append(" 			tsr.viewRoleType = 1 ");
		sql.append(" 		AND tvnm.noticeId = ? ");
		sql.append(" 		AND tvnm.viewId = tsrm.viewId ");
		sql.append(" 		GROUP BY ");
		sql.append(" 			tsr.viewRoleName, ");
		sql.append(" 			tsr.crewId, ");
		sql.append(" 			tsr.shortName, ");
		sql.append(" 			tsr.viewRoleId, ");
		sql.append(" 			tsr.viewRoleType, ");
		sql.append(" 			tsr.sequence, ");
		sql.append(" 			tai.actorId, ");
		sql.append(" 			tai.actorName, ");
		sql.append(" 			tnrt.makeup, ");
		sql.append(" 			tnrt.arriveTime, ");
		sql.append(" 			tnrt.giveMakeupTime ");
		sql.append(" 	) res ");
		sql.append(" ORDER BY ");
		sql.append(" 	res.sequence, res.viewRoleCount DESC ");
		
		return this.query(sql.toString(), new Object[] {noticeId, noticeId}, null);
	}
	
	/**
	 * 把剧组中所有角色序号加一
	 * @param crewId
	 */
	public void downViewRoleSequence(String crewId) {
		String sql = "update tab_view_role set sequence = sequence + 1 where crewId = ?";
		this.getJdbcTemplate().update(sql, crewId);
	}
	
	/**
	 * 查询角色过滤的关键字
	 * @return
	 */
	public List<Map<String, Object>> queryFilterKeyword(){
		String sql = "select * from tab_figure_keyword";
		return this.query(sql, null, null);
	}

	/**
	 * 查询角色戏量按集分布
	 * 去掉删戏
	 * @return
	 */
	public List<Map<String, Object>> queryRoleViewBySeries(String crewId, String viewRoleId) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT tvi.seriesNo, ");
		sql.append(" 	count(if(tvrm.viewRoleId is null,null,1)) viewCount,");
		sql.append(" 	round(ifnull(sum(if(tvrm.viewRoleId is null,null,pageCount)),0),2) pageCount ");
		sql.append(" FROM tab_view_info tvi ");
		sql.append(" LEFT JOIN tab_view_role_map tvrm ");
		sql.append(" ON tvi.viewId=tvrm.viewId and tvrm.viewRoleId=? ");
		sql.append(" WHERE tvi.crewId=?");
		sql.append(" and tvi.shootStatus!=" + ShootStatus.DeleteXi.getValue());//去掉删戏
		sql.append(" GROUP BY tvi.seriesNo ");
		sql.append(" ORDER BY (seriesNo+0) ");
		return this.query(sql.toString(), new Object[] {viewRoleId, crewId}, null);
	}
	
	/**
	 * 查询角色名
	 * @param crewId
	 * @param viewRoleType
	 */
	public List<Map<String, Object>> queryRoleNamesByCrewId(String crewId, String viewRoleType) {
		String sql = "select GROUP_CONCAT(viewRoleName) names from tab_view_role tvr where tvr.crewId=? and viewRoleType in (" + viewRoleType + ")";
		return this.query(sql, new Object[]{crewId}, null);
	}
	
	/**
	 * 获取剧组角色列表
	 * @param crewId	剧组ID
	 * @param viewRoleType	角色类型
	 * @param roleName 角色或演员名称
	 * @param page
	 * @return	角色ID，角色名称，演员ID，演员姓名，总场数，总页数，已完成场数，已完成页数
	 */
	public List<Map<String, Object>> queryRoleAndShootStatByRoleType(String crewId, Integer viewRoleType, String roleName, Page page) {
		List<Object> params = new ArrayList<Object>();
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tvr.viewRoleId, ");
		sql.append(" 	tvr.viewRoleName, ");
		sql.append(" 	tai.actorId, ");
		sql.append(" 	tai.actorName, ");
		sql.append(" 	sum(IF(tvi.shootStatus = 0 || tvi.shootStatus = 1 || tvi.shootStatus = 2, 1, 0)) totalViewCount, ");
		sql.append(" 	sum(if(tvi.shootStatus = 3 || tvi.pageCount is NULL, 0, tvi.pageCount)) totalPageCount, ");
		sql.append(" 	sum(if(tvi.shootStatus = 2, 1, 0)) finishedViewCount, ");
		sql.append(" 	sum(if(tvi.shootStatus = 2, tvi.pageCount, 0)) finishedPageCount ");
		sql.append(" FROM ");
		sql.append(" 	tab_view_role tvr ");
		sql.append(" LEFT JOIN tab_actor_role_map tarm ON tarm.viewRoleId = tvr.viewRoleId AND tarm.crewId = ? ");
		params.add(crewId);
		sql.append(" LEFT JOIN tab_actor_info tai ON tai.actorId = tarm.actorId AND tai.crewId = ? ");
		params.add(crewId);
		sql.append(" LEFT JOIN tab_view_role_map tvrm ON tvrm.viewRoleId = tvr.viewRoleId AND tvrm.crewId = ? ");
		params.add(crewId);
		sql.append(" LEFT JOIN tab_view_info tvi ON tvi.viewId = tvrm.viewId AND tvi.crewId = ? ");
		params.add(crewId);
		sql.append(" WHERE ");
		sql.append(" 	tvr.crewId = ? ");
		params.add(crewId);
		if(viewRoleType != null) {
			sql.append(" 	and tvr.viewRoleType = ? ");
			params.add(viewRoleType);
		}
		if(StringUtils.isNotBlank(roleName)) {
			sql.append(" 	and (tvr.viewRoleName like ? or tai.actorName like ?) ");
			params.add("%" + roleName + "%");
			params.add("%" + roleName + "%");
		}
		sql.append(" GROUP BY ");
		sql.append(" 	tvr.viewRoleId, ");
		sql.append(" 	tvr.viewRoleName, ");
		sql.append(" 	tai.actorId, ");
		sql.append(" 	tai.actorName ");
		
		return this.query(sql.toString(), params.toArray(), page);
	}
}
