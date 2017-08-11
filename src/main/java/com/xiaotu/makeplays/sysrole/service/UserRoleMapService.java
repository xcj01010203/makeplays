package com.xiaotu.makeplays.sysrole.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.authority.dao.UserAuthMapDao;
import com.xiaotu.makeplays.crew.dao.CrewRoleUserMapDao;
import com.xiaotu.makeplays.crew.dao.CrewUserMapDao;
import com.xiaotu.makeplays.crew.model.CrewUserMapModel;
import com.xiaotu.makeplays.crew.model.constants.CrewUserType;
import com.xiaotu.makeplays.sysrole.dao.UserRoleMapDao;
import com.xiaotu.makeplays.sysrole.model.UserRoleMapModel;
import com.xiaotu.makeplays.user.dao.SysRoleInfoDao;
import com.xiaotu.makeplays.user.dao.UserInfoDao;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.service.CrewContactService;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 系统角色和用户关联关系
 * @author xuchangjian 2016-5-19下午4:26:37
 */
@Service
public class UserRoleMapService {

	@Autowired
	private UserRoleMapDao userRoleMapDao;
	
	@Autowired
	private CrewUserMapDao crewUserMapDao;
	
	@Autowired
	private CrewContactService crewContactService;
	
	@Autowired
	private UserInfoDao userInfoDao;
	
	@Autowired
	private UserAuthMapDao userAuthMapDao;
	
	@Autowired
	private SysRoleInfoDao sysRoleInfoDao;
	
	@Autowired
	private CrewRoleUserMapDao crewRoleUserMapDao;
	
	/**
	 * 查询用户和指定角色的关联关系
	 * @return
	 * @throws Exception 
	 */
	public UserRoleMapModel queryByUserRoleId(String crewId, String userId, String roleId) throws Exception {
		return this.userRoleMapDao.queryByUserRoleId(crewId, userId, roleId);
	}
	
	/**
	 * 查询用户在剧组中的职务
	 * @param crewId
	 * @param userId
	 * @return
	 */
	public List<UserRoleMapModel> queryByUserId(String crewId, String userId) {
		return this.userRoleMapDao.queryByUserId(crewId, userId);
	}
	
	/**
	 * 保存用户的职务信息，批量保存
	 * 先查询出用户已有的职务，然后对比传过来的职务，最后用户进行新增职务和删除职务操作
	 * @param crewId
	 * @param aimUserId
	 * @param roleIds
	 * @throws Exception
	 */
	public void saveUserRoleInfo(String crewId, String aimUserId, String roleIds) throws Exception {
		List<String> roleIdList = Arrays.asList(roleIds.split(","));
		List<UserRoleMapModel> userRoleMapList = this.queryByUserId(crewId, aimUserId);
		List<String> existRoleIdList = new ArrayList<String>();
		for (UserRoleMapModel userRoleMap : userRoleMapList) {
			String roleId = userRoleMap.getRoleId();
			existRoleIdList.add(roleId);
		}
		
		for (String roleId : existRoleIdList) {
			if (!roleIdList.contains(roleId)) {
				UserRoleMapModel userRoleMap = this.queryByUserRoleId(crewId, aimUserId, roleId);
				//删除
				this.deleteById(crewId, aimUserId, userRoleMap.getMapId(), roleId);
			}
		}
		
		for (String roleId : roleIdList) {
			if (!existRoleIdList.contains(roleId)) {
				//新增
				this.addOne(aimUserId, crewId, roleId);
			}
		}
	}
	
	
	/**
	 * 新增一条记录
	 * @param userRoleMap
	 * @throws Exception
	 */
	public void addOne(String userId, String crewId, String roleId) throws Exception {
		UserRoleMapModel userRoleMap = new UserRoleMapModel();
		
		//新增
		userRoleMap.setCrewId(crewId);
		userRoleMap.setMapId(UUIDUtils.getId());
		userRoleMap.setRoleId(roleId);
		userRoleMap.setUserId(userId);
		this.userRoleMapDao.add(userRoleMap);
		
		//如果roleId为剧组管理员，还需要更新剧组用户关联关系中的type字段
		if (roleId.equals(Constants.ROLE_ID_ADMIN)) {
			CrewUserMapModel crewUserMap = this.crewUserMapDao.queryCrewUserBycrewId(userId, crewId);
			crewUserMap.setType(CrewUserType.Manager.getValue());
			this.crewUserMapDao.update(crewUserMap, "mapId");
		}
		
		//同步用户信息到剧组联系表
		UserInfoModel userInfoModel = this.userInfoDao.queryById(userId);
		this.crewContactService.syncFromUserInfo(crewId, userInfoModel);
		
		//新增时要把该角色的所有权限赋予给用户
		this.userAuthMapDao.addByRoleId(crewId, userId, roleId);
	}
	
	/**
	 * 根据ID删除记录
	 * @param roleId
	 * @throws Exception
	 */
	public void deleteById (String crewId, String userId, String mapId, String roleId) throws Exception {
		this.userRoleMapDao.deleteOne(mapId, "mapId", UserRoleMapModel.TABLE_NAME);
		
		//如果roleId为剧组管理员，还需要更新剧组用户关联关系中的type字段
		if (roleId.equals(Constants.ROLE_ID_ADMIN)) {
			CrewUserMapModel crewUserMap = this.crewUserMapDao.queryCrewUserBycrewId(userId, crewId);
			crewUserMap.setType(CrewUserType.NormalUser.getValue());
			this.crewUserMapDao.update(crewUserMap, "mapId");
		}
		
		//同步用户信息到剧组联系表
		UserInfoModel userInfoModel = this.userInfoDao.queryById(userId);
		this.crewContactService.syncFromUserInfo(crewId, userInfoModel);
		
		//删除用户拥有的该角色下的所有权限
		this.userAuthMapDao.deleteByRoleId(crewId, userId, roleId);
		
		//如果用户删除的是演员（73）/演员助理（74）职务，则删除用户和场景角色的所有关联关系
		if (roleId.equals("73") || roleId.equals("74")) {
			boolean isActorRole = false;
			List<Map<String, Object>> userRoleList = this.sysRoleInfoDao.queryByCrewUserId(crewId, userId);
			for (Map<String, Object> map : userRoleList) {
				String myRoleId = (String) map.get("roleId");
				if (myRoleId.equals("73") || myRoleId.equals("74")) {
					isActorRole = true;
					break;
				}
			}
			if (!isActorRole) {
				this.crewRoleUserMapDao.deleteByCrewUserId(crewId, userId);
			}
		}
	}
	
	/**
	 * 查询所有的角色用户关联关系
	 * @return
	 */
	public String queryAllUserRoleIds(String userId) {
		List<Map<String, Object>> roleUserMapList = this.userRoleMapDao.queryAllRoleIdsByUserId(userId);
		String roleIds = "";
		if(roleUserMapList != null && roleUserMapList.size() > 0) {
			for(Map<String, Object> roleUserMap : roleUserMapList) {
				roleIds += "," + roleUserMap.get("roleId") + "";
			}
			roleIds = roleIds.substring(1);
		}
		return roleIds;
	}
}
