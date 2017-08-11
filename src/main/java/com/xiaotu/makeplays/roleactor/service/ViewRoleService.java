package com.xiaotu.makeplays.roleactor.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.roleactor.controller.filter.ViewRoleFilter;
import com.xiaotu.makeplays.roleactor.dao.ActorInfoDao;
import com.xiaotu.makeplays.roleactor.dao.ActorRoleMapDao;
import com.xiaotu.makeplays.roleactor.dao.ViewRoleDao;
import com.xiaotu.makeplays.roleactor.model.ActorInfoModel;
import com.xiaotu.makeplays.roleactor.model.ActorRoleMapModel;
import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;
import com.xiaotu.makeplays.sysrole.dao.UserRoleMapDao;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.view.controller.filter.ViewFilter;
import com.xiaotu.makeplays.view.dao.ViewRoleAndActorDao;
import com.xiaotu.makeplays.view.dao.ViewRoleMapDao;
import com.xiaotu.makeplays.view.model.ViewInfoModel;
import com.xiaotu.makeplays.view.model.ViewRoleMapModel;
import com.xiaotu.makeplays.view.service.ViewInfoService;

/**
 * 场景角色信息
 * @author xuchangjian
 */
@Service
public class ViewRoleService {

	@Autowired
	private ViewRoleDao viewRoleDao;
	
	@Autowired
	private ViewRoleMapDao viewRoleMapDao;
	
	@Autowired
	private ViewRoleAndActorDao viewRoleAndActorDao;
	
	@Autowired
	private ActorInfoDao actorInfoDao;
	
	@Autowired
	private ActorRoleMapDao actorRoleMapDao;
	
	@Autowired
	private UserRoleMapDao userRoleMapDao;
	
	@Autowired
	private ActorLeaveRecordService actorLeaveRecordService;
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	/**
	 * 根据场景ID查询场景地点信息
	 * @param viewId
	 * @return
	 */
	public List<ViewRoleModel> queryManyByViewId(String viewId) {
		return this.viewRoleDao.queryManyByViewId(viewId);
	}
	
	/**
	 * 根据场景ID查询场景角色信息
	 * 该查询方法会一起查询出演员的数量信息
	 * @param viewId
	 * @return
	 */
	public List<Map<String, Object>> queryViewRoleByViewId(String viewId) {
		return this.viewRoleDao.queryViewRoleByViewId(viewId);
	}
	
	/**
	 * 根据场景ID删除场景对应的角色演员信息信息
	 * 包括场景地点信息和场景和场景地点的关联关系
	 * @param viewId
	 * @throws Exception 
	 */
	public void deleteManyByViewId(String viewId) throws Exception {
		List<String> viewRoleIdList = new ArrayList<String>();	//场景地点的ID列表
		
		List<ViewRoleModel> mapList = this.viewRoleDao.queryManyByViewId(viewId);
		for (ViewRoleModel map : mapList) {
			viewRoleIdList.add(map.getViewRoleId());
		}
		
		//删除场景地点信息
		if (viewRoleIdList.size() > 0) {
			String[] strArray = new String[viewRoleIdList.size()];
			this.viewRoleDao.deleteMany(viewRoleIdList.toArray(strArray), "viewRoleId", ViewRoleModel.TABLE_NAME);
		}
		
		//删除场景和场景角色的关联关系
		this.viewRoleMapDao.deleteManyByViewId(viewId);
	}
	
	/**
	 * 根据剧本ID查找对应的场景角色信息
	 * @param crewId 剧本ID
	 * @return
	 */
	public List<ViewRoleModel> queryByCrewId(String crewId) {
		return this.viewRoleDao.queryByCrewId(crewId);
	}
	
	/**
	 * 根据剧本ID查找对应的场景角色信息
	 * 该方法只查询出在所有场景中出现的场景角色
	 * @param crewId 剧本ID
	 * @return
	 */
	public List<ViewRoleModel> queryManyOnlyExistsInCrewView(String crewId) {
		return this.viewRoleDao.queryManyOnlyExistsInCrewView(crewId);
	}
	
	/**
	 * 根据剧本ID和角色类型查找对应的场景角色信息
	 * @param crewId 剧本ID
	 * @param viewRoleType 场景角色类型
	 * @return 场景角色列表，先按照角色数量排序，然后按照角色名称排序
	 */
	public List<ViewRoleModel> queryManyByCrewIdAndRoleType(String crewId, int viewRoleType) {
		return this.viewRoleDao.queryManyByCrewIdAndRoleType(crewId, viewRoleType);
	}
	
	/**
	 * 根据剧本ID和角色类型查找对应的场景角色信息（去掉）
	 * @param crewId
	 * @param viewRoleType
	 * @param excludeRoles
	 * @return
	 */
	public List<ViewRoleModel> queryManyByIdAndTypeExcludeSome(String crewId,
			int viewRoleType, ViewFilter filter) {
		return this.viewRoleDao.queryManyByIdAndTypeExcludeSome(crewId,
				viewRoleType, filter);
	}
	
	/**
	 * 根据剧本ID和是否是关注角色查找对应的场景角色信息
	 * @param crewId
	 * @param viewRoleType
	 * @param excludeRoles
	 * @return
	 */
	public List<ViewRoleModel> queryManyByIdAndIsAttentionRole(String crewId) {
		return this.viewRoleDao.queryManyByIdAndIsAttentionRole(crewId);
	}
	
	/**
	 * 根据剧本ID和角色类型查找对应的场景角色信息
	 * 该方法会查询出角色拥有的戏量
	 * @param crewId 剧本ID
	 * @param viewRoleType 场景角色类型
	 * @return
	 */
	public List<Map<String, Object>> queryRoleMapByCrewIdAndRoleType(String crewId, int viewRoleType) {
		List<Map<String, Object>> majorRoleList = this.viewRoleDao.queryRoleMapByCrewIdAndRoleType(crewId, viewRoleType);
		return majorRoleList;
	}
	
	/**
	 * 获取当前用户扮演的角色
	 */
	public List<ViewRoleModel> queryUserRoleInfo(String crewId,String userId){
		return this.viewRoleDao.queryUserRoleInfo(crewId, userId);
	}
	
	/**
	 * 根据多个条件查询演员角色信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ViewRoleModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.viewRoleDao.queryManyByMutiCondition(conditionMap, page);
	}
	
	/**
	 * 查询通告单下的演员信息
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	public List<Map<String, Object>> queryManyByNoticeId(String crewId, String noticeId) {
		return this.viewRoleDao.queryManyByNoticeId(crewId, noticeId);
	}
	
	/**
	 * 查询所有演员，并根据类型,去除重复数据
	 * 该方法会查询出演员、角色的所有信息
	 * @return
	 */
	public List<Map<String, Object>> queryViewRoleListByCrewId(String crewId,Integer roleType){
		return this.viewRoleAndActorDao.queryViewRoleListByCrewId(crewId, roleType);
	}
	
	/**
	 * 获取剧组角色列表
	 * @param crewId	剧组ID
	 * @param viewRoleFilter 高级查询条件
	 * @return	角色ID，角色名称，角色类型，简称，演员ID，演员姓名，入组时间，离组时间，总场数，总页数，已完成场数，未完成场数，请假次数，请假天数
	 */
	public List<Map<String, Object>> queryViewRoleList(String crewId, ViewRoleFilter viewRoleFilter) {
		return this.viewRoleDao.queryViewRoleList(crewId, viewRoleFilter);
	}
	
	/**
	 * 保存场景角色信息
	 * 该方法还会保存演员信息
	 * @param viewRoleId	角色ID
	 * @param viewRoleName	角色名称
	 * @param shortName	角色简称
	 * @param viewRoleType	角色类型
	 * @param actorId 演员ID
	 * @param actorName	演员名称
	 * @param enterDate	演员入组日期
	 * @param leaveDate	演员离组日期
	 * @param shootDays 演员在组天数
	 * @return 演员ID
	 * @throws Exception 
	 */
	public Map<String, Object> saveRoleWithActorInfo(String crewId, String viewRoleId, String viewRoleName, String shortName,
			Integer viewRoleType, String actorId, String actorName, String enterDate,
			String leaveDate, Integer shootDays, Boolean isAttentionRole, String workHours, String restHours) throws Exception {
		Map<String, Object> back = new HashMap<String, Object>();
		
		boolean hasId = true;
		//保存场景角色信息
		ViewRoleModel viewRole = new ViewRoleModel();
		if (!StringUtils.isBlank(viewRoleId)) {
			viewRole = this.viewRoleDao.queryById(viewRoleId);
		} else {
			hasId = false;
			viewRoleId = UUIDUtils.getId();
			viewRole.setViewRoleId(viewRoleId);
			viewRole.setSequence(1);
		}
		
		viewRole.setViewRoleName(viewRoleName);
		viewRole.setShortName(shortName);
		viewRole.setViewRoleType(viewRoleType);
		viewRole.setCrewId(crewId);
		if (isAttentionRole != null) {
			viewRole.setIsAttentionRole(isAttentionRole);
		}
		
		if (hasId) {
			this.viewRoleDao.update(viewRole);
		} else {
			this.viewRoleDao.downViewRoleSequence(crewId);
			this.viewRoleDao.add(viewRole);
		}
		
		
		//保存演员信息
		ActorInfoModel actorInfo = new ActorInfoModel();
		if (!StringUtils.isBlank(actorId)) {
			actorInfo = this.actorInfoDao.queryById(actorId);
		} else {
			actorInfo.setActorId(UUIDUtils.getId());
		}
		
		//在演员名称不为空的情况下才保存演员信息，否则当成未设置演员来处理
		if (!StringUtils.isBlank(actorName)) {
			actorInfo.setCrewId(crewId);
			actorInfo.setActorName(actorName);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (!StringUtils.isBlank(enterDate)) {
				actorInfo.setEnterDate(sdf.parse(enterDate));
			}else {
				actorInfo.setEnterDate(null);
			}
			
			if (!StringUtils.isBlank(leaveDate)) {
				actorInfo.setLeaveDate(sdf.parse(leaveDate));
			}else {
				actorInfo.setLeaveDate(null);
			}
			
			actorInfo.setShootDays(shootDays);
			//工作时长、休息时长
			actorInfo.setWorkHours(workHours);
			actorInfo.setRestHours(restHours);
			
			if (!StringUtils.isBlank(actorId)) {
				this.actorInfoDao.updateWithNull(actorInfo, "actorId");
			} else {
				this.actorInfoDao.add(actorInfo);
				
				ActorRoleMapModel actorRoleMap = new ActorRoleMapModel();
				actorRoleMap.setMapId(UUIDUtils.getId());
				actorRoleMap.setCrewId(crewId);
				actorRoleMap.setActorId(actorInfo.getActorId());
				actorRoleMap.setViewRoleId(viewRole.getViewRoleId());
				
				this.actorRoleMapDao.add(actorRoleMap);
			}
		} else {
			if (!StringUtils.isBlank(viewRoleId)) {
				//删除角色对应的演员以及与其的关联关系
				ActorRoleMapModel actorRoleMap = this.actorRoleMapDao.queryByViewRoleId(viewRoleId);
				
				if (actorRoleMap != null) {
					this.actorInfoDao.deleteOne(actorRoleMap.getActorId(), "actorId", ActorInfoModel.TABLE_NAME);
					this.actorRoleMapDao.deleteOne(actorRoleMap.getMapId(), "mapId", ActorRoleMapModel.TABLE_NAME);
				}
				this.actorLeaveRecordService.deleteByViewRoleIds(viewRoleId);
			}
		}
		//只有当 演员姓名不为空时，才返回演员的id
		if (StringUtils.isNotBlank(actorName)) {
			back.put("actorId", actorInfo.getActorId());
		}
		back.put("viewRoleId", viewRoleId);
		return back;
	}
	
	/**
	 * 删除场景角色信息
	 * 还会删除角色对应的演员信息
	 * 以及角色和演员的关联关系
	 * 场景和角色的关联关系
	 * @param viewRoleId
	 * @throws Exception 
	 */
	public void deleteViewRoleInfo(String viewRoleId) throws Exception {

		this.viewRoleDao.deleteOne(viewRoleId, "viewRoleId", ViewRoleModel.TABLE_NAME);
		
		//删除角色对应的演员以及与其的关联关系
		ActorRoleMapModel actorRoleMap = this.actorRoleMapDao.queryByViewRoleId(viewRoleId);
		
		if (actorRoleMap != null) {
			this.actorInfoDao.deleteOne(actorRoleMap.getActorId(), "actorId", ActorInfoModel.TABLE_NAME);
			this.actorRoleMapDao.deleteOne(actorRoleMap.getMapId(), "mapId", ActorRoleMapModel.TABLE_NAME);
		}
		
		//删除角色和场景的关联关系
		this.viewRoleDao.deleteByViewRoleIds(viewRoleId);
	}
	
	/**
	 * 删除场景角色信息
	 * 还会删除角色对应的演员信息
	 * 以及角色和演员的关联关系
	 * 场景和角色的关联关系
	 * @param viewRoleId
	 * @throws Exception 
	 */
	public void deleteViewRoleInfoBatch(String viewRoleIds) throws Exception {

		this.viewRoleDao.deleteByViewRoleIds(viewRoleIds);
		
		//删除角色对应的演员以及与其的关联关系
		this.actorInfoDao.deleteByViewRoleIds(viewRoleIds);
		this.actorRoleMapDao.deleteByViewRoleIds(viewRoleIds);
		
		//删除角色和场景的关联关系
		this.viewRoleDao.deleteByViewRoleIds(viewRoleIds);
	}
	
	/**
	 * 批量设置角色类型
	 * @param viewRoleIds	场景角色ID，多个用逗号隔开
	 * @param viewRoleType	角色类型
	 */
	public void updateViewRoleTypeBatch(String viewRoleIds, Integer viewRoleType) {
		this.viewRoleDao.updateViewRoleTypeBatch(viewRoleIds, viewRoleType);
	}
	
	/**
	 * 查询出当前剧组中的关注的角色
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryAttentionRoleList(String crewId){
		return this.viewRoleDao.queryAttentionRoleList(crewId);
	}
	
	/**
	 * 设置或取消关注角色
	 * @param viewRoleIds
	 * @param isAttentionRole
	 */
	public void updateViewRoleAttentionBatch(String viewRoleId, Boolean isAttentionRole) {
		this.viewRoleDao.updateViewRoleAttentionBatch(viewRoleId, isAttentionRole);
	}
	
	/**
	 * 合并角色
	 * @param request
	 * @param viewRoleIds	待合并的角色ID，多个值用逗号隔开
	 * @param viewRoleName	新角色名称
	 * @param shortName	新角色的简称
	 * @param viewRoleType	新角色的类型
	 * @param actorName	演员姓名
	 * @param enterDate	入组时间
	 * @param leaveDate	离组时间
	 * @param shootDays 在组天数
	 * @return
	 * @throws Exception 
	 */
	public void makeRolesToOne(String crewId, String viewRoleIds, String viewRoleName, String shortName,
			Integer viewRoleType, String actorName, String enterDate,
			String leaveDate, Integer shootDays) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		//新增新角色
		ViewRoleModel viewRole = new ViewRoleModel();
		viewRole.setViewRoleId(UUIDUtils.getId());
		viewRole.setCrewId(crewId);
		viewRole.setViewRoleName(viewRoleName);
		viewRole.setViewRoleType(viewRoleType);
		viewRole.setShortName(shortName);
		
		this.viewRoleDao.add(viewRole);
		
		
		//新增新演员
		if (!StringUtils.isBlank(actorName)) {
			ActorInfoModel actorInfo = new ActorInfoModel();
			actorInfo.setActorId(UUIDUtils.getId());
			actorInfo.setCrewId(crewId);
			actorInfo.setActorName(actorName);
			if (!StringUtils.isBlank(enterDate)) {
				actorInfo.setEnterDate(sdf.parse(enterDate));
			}
			if (!StringUtils.isBlank(leaveDate)) {
				actorInfo.setLeaveDate(sdf.parse(leaveDate));
			}
			actorInfo.setShootDays(shootDays);
			
			this.actorInfoDao.add(actorInfo);
			
			
			//新增演员和角色关联
			ActorRoleMapModel actorRoleMap = new ActorRoleMapModel();
			actorRoleMap.setMapId(UUIDUtils.getId());
			actorRoleMap.setCrewId(crewId);
			actorRoleMap.setActorId(actorInfo.getActorId());
			actorRoleMap.setViewRoleId(viewRole.getViewRoleId());
			
			this.actorRoleMapDao.add(actorRoleMap);
		}
		
		//保存角色和场景的关联
		List<ViewInfoModel> viewList = this.viewInfoService.queryByViewRoleIds(viewRoleIds);
		List<ViewRoleMapModel> viewRoleMapList = new ArrayList<ViewRoleMapModel>();
		for (ViewInfoModel viewInfo : viewList) {
			ViewRoleMapModel viewRoleMap = new ViewRoleMapModel();
			viewRoleMap.setMapId(UUIDUtils.getId());
			viewRoleMap.setViewId(viewInfo.getViewId());
			viewRoleMap.setViewRoleId(viewRole.getViewRoleId());
			viewRoleMap.setRoleNum(1);
			viewRoleMap.setCrewId(crewId);
			
			viewRoleMapList.add(viewRoleMap);
		}
		this.viewRoleMapDao.addBatch(viewRoleMapList, ViewRoleMapModel.class);
		
		//更新角色和用户的关联
		this.userRoleMapDao.updateUserRoleMap(viewRoleIds, crewId, viewRole.getViewRoleId());
		
		//删除老角色、演员、角色和演员的关联关系、请假记录
		this.viewRoleDao.deleteByViewRoleIds(viewRoleIds);
		this.actorRoleMapDao.deleteByViewRoleIds(viewRoleIds);
		this.viewRoleMapDao.deleteByViewRoleIds(viewRoleIds);
		this.actorInfoDao.deleteByViewRoleIds(viewRoleIds);
		this.actorLeaveRecordService.deleteByViewRoleIds(viewRoleIds);
	}
	
	/**
	 * 根据ID查找场景角色
	 * @param viewRoleId
	 * @return
	 * @throws Exception 
	 */
	public ViewRoleModel queryViewRoleInfoById(String viewRoleId) throws Exception {
		return this.viewRoleDao.queryById(viewRoleId);
	}
	
	/**
	 * 根据ID查找场景角色
	 * @param viewRoleId
	 * @return	场景角色信息，对应的演员信息
	 * @throws Exception 
	 */
	public Map<String, Object> queryByIdWithActorInfo(String viewRoleId) throws Exception {
		return this.viewRoleDao.queryByIdWithActorInfo(viewRoleId);
	}
	
	/**
	 * 根据角色名称查询剧组中的角色
	 * @param crewId
	 * @param viewRoleName
	 * @return
	 */
	public List<ViewRoleModel> queryByViewRoleNameExpOne(String crewId, String viewRoleId, String viewRoleName) {
		return this.viewRoleDao.queryByViewRoleNameExpOne(crewId, viewRoleId, viewRoleName);
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
		return this.viewRoleDao.queryByViewRoleNameExpMany(crewId, viewRoleIds, viewRoleName);
	}
	
	/**
	 * 获取用户关注的角色信息
	 * @param crewId
	 * @param userId
	 * @return
	 */
	public List<ViewRoleModel> queryUserFocusRoleInfo(String crewId, String userId) {
		//获取用户关注的角色信息
		List<ViewRoleModel> fouceRoleList = this.viewRoleDao.queryFocusRoleByUserId(crewId, userId);
		
		//获取用户扮演的角色
		if (fouceRoleList == null || fouceRoleList.size() == 0) {
			List<ViewRoleModel> userRoleList = this.viewRoleDao.queryUserRoleInfo(crewId, userId);
			if (userRoleList != null && userRoleList.size() > 0) {
				fouceRoleList = new ArrayList<ViewRoleModel>();
				fouceRoleList.addAll(userRoleList);
			}
		}
		
		return fouceRoleList;
	}
	
	/**
	 * 首页统计数据
	 */
	public List<Map<String,Object>> getIndexCount(String crewId){
		return this.viewRoleDao.getIndexCount(crewId);
	}
	
	/**
	 * 查询演员已拍摄天数
	 * @param viewRoleId
	 * @return
	 */
	public Map<String, Object> queryViewRoleFinishedDays(String viewRoleId) {
		return this.viewRoleDao.queryViewRoleFinishedDays(viewRoleId);
	}
	
	/**
	 * 查询演员计划拍摄天数
	 * @param viewRoleId
	 * @return
	 */
	public List<Map<String, Object>> queryViewRoleShootDays(String viewRoleId) {
		return this.viewRoleDao.queryViewRoleShootDays(viewRoleId);
	}
	
	/**
	 * 查询角色日拍摄量信息
	 * @param viewRoleId
	 * @return
	 */
	public List<Map<String, Object>> queryRoleViewStatistic(String viewRoleId) {
		return this.viewRoleDao.queryRoleViewStatistic(viewRoleId);
	}
	
	/**
	 * 批量新增
	 * @param viewRoleList
	 * @throws Exception 
	 */
	public void addBatch(List<ViewRoleModel> viewRoleList) throws Exception {
		this.viewRoleDao.addBatch(viewRoleList, ViewRoleModel.class);
	}
	
	/**
	 * 更新角色表排序字段
	 * @param viewRoleIdArray
	 */
	public void updateViewRoleSequence(String[] viewRoleIdArray){
		this.viewRoleDao.updateViewRoleSequence(viewRoleIdArray);
	}
	
	/**
	 * 更新角色类型
	 * @param viewRoleIdArray
	 */
	public void updateViewRoleType(List<Object[]> paramList){
		this.viewRoleDao.updateViewRoleType( paramList);
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
		return this.viewRoleDao.queryViewRoleListByNoticeId(noticeId);
	}
	
	/**
	 * 查询角色过滤关键字
	 * @return
	 */
	public List<Map<String, Object>> queryFilterKeyword(){
		return this.viewRoleDao.queryFilterKeyword();
	}
	
	/**
	 * 查询角色戏量按集分布
	 * @return
	 */
	public List<Map<String, Object>> queryRoleViewBySeries(String crewId, String viewRoleId) {
		return this.viewRoleDao.queryRoleViewBySeries(crewId, viewRoleId);
	}
	
	/**
	 * 获取剧组角色列表
	 * @param crewId	剧组ID
	 * @param viewRoleType	角色类型
	 * @param roleName 角色或演员名称
	 * @param page
	 * @return	角色ID，角色名称，演员ID，演员姓名，总场数，总页数，已完成场数，已完成页数
	 */
	public List<Map<String, Object>> queryRoleAndShootStatByRoleType(
			String crewId, Integer viewRoleType, String roleName, Page page) {
		return this.viewRoleDao.queryRoleAndShootStatByRoleType(crewId,
				viewRoleType, roleName, page);
	}
}
