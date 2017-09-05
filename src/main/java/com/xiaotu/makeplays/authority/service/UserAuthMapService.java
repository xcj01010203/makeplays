package com.xiaotu.makeplays.authority.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.authority.dao.AuthorityDao;
import com.xiaotu.makeplays.authority.dao.UserAuthMapDao;
import com.xiaotu.makeplays.authority.model.AuthorityModel;
import com.xiaotu.makeplays.authority.model.UserAuthMapModel;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 用户权限关联
 * @author xuchangjian 2016-5-19下午3:02:46
 */
@Service
public class UserAuthMapService {
	
	@Autowired
	private UserAuthMapDao userAuthMapDao;
	
	@Autowired
	private AuthorityDao authorityDao;

	/**
	 * 查询用户在指定剧组下的权限关联
	 * @param crewId
	 * @param userId
	 * @return
	 */
	public List<UserAuthMapModel> queryByCrewUserId (String crewId, String userId) {
		return this.userAuthMapDao.queryByCrewUserId(crewId, userId);
	}
	
	/**
	 * 查询用户在指定剧组下的权限关联
	 * 多个用户
	 * @param crewId
	 * @param userIds
	 * @return
	 */
	public List<Map<String, Object>> queryByCrewUserIds (String crewId, String userIds) {
		return this.userAuthMapDao.queryByCrewUserIds(crewId, userIds);
	}
	
	/**
	 * 查询用户在指定剧组下的指定权限信息
	 * @param crewId
	 * @param userId
	 * @param authId
	 * @return
	 * @throws Exception 
	 */
	public UserAuthMapModel queryByCrewUserAuthId (String crewId, String userId, String authId) throws Exception {
		return this.userAuthMapDao.queryByCrewUserAuthId(crewId, userId, authId);
	}
	
	/**
	 * 查询指定剧组指定权限的用户权限关联信息
	 * @param crewId
	 * @param authId
	 * @return
	 * @throws Exception
	 */
	public List<UserAuthMapModel> queryByCrewAuthId (String crewId, String authId) throws Exception {
		return this.userAuthMapDao.queryByCrewAuthId(crewId, authId);
	}
	
	/**
	 * 查询用户在指定剧组下的指定权限信息
	 * 该查询会返回用户在剧组下拥有的当前权限，以及其所有的子权限
	 * 目前只支持到三级权限
	 * @param crewId
	 * @param userId
	 * @param authId
	 * @return
	 */
	public List<UserAuthMapModel> queryByCrewUserAuthIdWithSubAuth(String crewId, String userId, String authId, Integer status) {
		return this.userAuthMapDao.queryByCrewUserAuthIdWithSubAuth(crewId, userId, authId, status);
	}
	
	/**
	 * 查询用户在指定剧组下的指定权限信息,有效权限
	 * 该查询会返回用户在剧组下拥有的当前权限，以及其所有的子权限
	 * 目前只支持到三级权限
	 * @param crewId
	 * @param userId
	 * @param authId
	 * @return
	 */
	public List<UserAuthMapModel> queryByCrewUserAuthIdWithSubAuth(String crewId, String userId, String authId) {
		return this.userAuthMapDao.queryByCrewUserAuthIdWithSubAuth(crewId, userId, authId, 0);
	}
	
	/**
	 * 查询用户指定的权限信息
	 * @param crewId
	 * @param userId
	 * @param authCode
	 * @return
	 * @throws Exception 
	 */
	public UserAuthMapModel queryOneByUserIdAndAuthCode(String crewId, String userId, String authCode) throws Exception {
		return this.userAuthMapDao.queryOneByUserIdAndAuthCode(crewId, userId, authCode);
	}
	
	/**
	 * 根据ID删除数据
	 * @param mapId
	 * @throws Exception
	 */
	public void deleteById (String crewId, String userId, String authId, String mapId) throws Exception {
		this.userAuthMapDao.deleteOne(mapId, "mapId", UserAuthMapModel.TABLE_NAME);
		
		this.checkHasMenuAuth(crewId, userId, authId, 2);
	}
	
	/**
	 * 新增一条记录
	 * @param userAuthMap
	 * @throws Exception 
	 */
	public void addOne(String crewId, String userId, UserAuthMapModel userAuthMap) throws Exception {
		this.userAuthMapDao.add(userAuthMap);
		
		this.checkHasMenuAuth(crewId, userId, userAuthMap.getAuthId(), 1);
	}
	
	/**
	 * 校验用户和顶级菜单权限的关系
	 * @param crewId
	 * @param userId
	 * @param authId
	 * @param flag 1:新增,2:删除
	 * @throws Exception
	 */
	public void checkHasMenuAuth (String crewId, String userId, String authId, Integer flag) throws Exception {
		
		//找到该权限的父权限
		AuthorityModel authorityInfo = this.authorityDao.queryAuthById(authId);
		//如果当前权限是顶级菜单权限
		if(authorityInfo.getParentId().equals("0")) {
			if(flag == 1) {//新增
				addChildMenuAuth(crewId, userId, authId);
			}
		} else {
			AuthorityModel pAuthInfo = this.authorityDao.queryAuthById(authorityInfo.getParentId());
			
			//如果父权限是顶级菜单权限（菜单权限的parentId是0，移动端的顶级权限的parentId是1）
			if (pAuthInfo != null && pAuthInfo.getParentId().equals("0")) {
				//查询用户在剧组下拥有的权限，以及其所有的子权限
				List<UserAuthMapModel> userAuthMapList = this.userAuthMapDao.queryByCrewUserAuthIdWithSubAuth(crewId, userId, pAuthInfo.getAuthId(), 0);
				
				boolean hasTheParentAuth = false;	//标识用户是否拥有该权限
				String pAuthMapId = "";
				for (UserAuthMapModel userAuthMap : userAuthMapList) {
					if (userAuthMap.getAuthId().equals(pAuthInfo.getAuthId())) {
						hasTheParentAuth = true;
						pAuthMapId = userAuthMap.getMapId();
						break;
					}
				}
				
				//如果有一个子权限且没有父权限，则新增，如果一个子权限都没有且有父权限，则删除
				if (!hasTheParentAuth && userAuthMapList != null && userAuthMapList.size() > 0) {
					//新增与顶级菜单权限的关联
					UserAuthMapModel newUserAuth = new UserAuthMapModel();
					newUserAuth.setMapId(UUIDUtils.getId());
					newUserAuth.setAuthId(pAuthInfo.getAuthId());
					newUserAuth.setUserId(userId);
					newUserAuth.setCrewId(crewId);
					newUserAuth.setReadonly(false);
					
					this.userAuthMapDao.add(newUserAuth);
				}
				
				if (hasTheParentAuth && userAuthMapList.size() == 1) {
					//删除与顶级菜单权限的关联
					this.userAuthMapDao.deleteOne(pAuthMapId, "mapId", UserAuthMapModel.TABLE_NAME);
				}
			}
		}
	}
	
	/**
	 * 循环添加子权限
	 * @param crewId
	 * @param authId
	 * @throws Exception
	 */
	public void addChildMenuAuth(String crewId, String userId, String authId) throws Exception{
		//查询用户拥有的权限，以及其所有的子权限
		List<UserAuthMapModel> userAuthMapList = this.userAuthMapDao.queryByCrewUserAuthIdWithSubAuth(crewId, userId, authId, 1);
		//将该权限下的二级权限赋给该用户，设为可编辑
		//查询该权限下的二级权限
		List<AuthorityModel> authorityList = this.authorityDao.queryAuthByPid(authId);
		for(AuthorityModel authority : authorityList) {
			boolean isExist = false;
			if(userAuthMapList != null && userAuthMapList.size() > 0) {
				for(UserAuthMapModel userAuthMap : userAuthMapList) {
					if(userAuthMap.getAuthId().equals(authority.getAuthId())) {
						isExist = true;
						break;
					}
				}
			}
			if(!isExist) {
				//新增与二级菜单权限的关联
				UserAuthMapModel newUserAuth = new UserAuthMapModel();
				newUserAuth.setMapId(UUIDUtils.getId());
				newUserAuth.setAuthId(authority.getAuthId());
				newUserAuth.setUserId(userId);
				newUserAuth.setCrewId(crewId);
				newUserAuth.setReadonly(false);
				this.userAuthMapDao.add(newUserAuth);
				
				//递归添加下一级子权限
				addChildMenuAuth(crewId, userId, authority.getAuthId());
			}
		}
	}
	
	/**
	 * 更新一条记录
	 * @param userAuthMap
	 * @throws Exception
	 */
	public void updateOne(UserAuthMapModel userAuthMap) throws Exception {
		this.userAuthMapDao.update(userAuthMap, "mapId");
	}
	
	/**
	 * 把剧组下指定角色的所有权限赋予给指定用户
	 * @param crewId
	 * @param userId
	 * @param roleId
	 */
	public void addByRoleId(String crewId, String userId, String roleId) {
		this.userAuthMapDao.addByRoleId(crewId, userId, roleId);
	}
	
	/**
	 * 删除用户在指定剧组中拥有的指定角色的所有权限
	 * @param crewId
	 * @param userId
	 * @param roleId
	 */
	public void deleteByRoleId(String crewId, String userId, String roleId) {
		this.userAuthMapDao.deleteByRoleId(crewId, userId, roleId);
	}
	
	/**
	 * 更新指定剧组的用户的指定权限的只读属性，设为只读
	 * @param crewId
	 * @param authId
	 */
	public void updateUserAuthReadOnlyByCrewAuthId(String crewId, String authId, Integer readonly) {
		this.userAuthMapDao.updateUserAuthReadOnlyByCrewAuthId(crewId, authId, readonly);
	}
	
	/**
	 * 将过期剧组用户的权限设为只读
	 */
	public void updateExpiredCrewUserAuth(String crewIds) {
		this.userAuthMapDao.updateExpiredCrewUserAuth(crewIds);
	}
	
	/**
	 * 删除过期剧组用户的收支管理权限
	 * @param crewIds
	 */
	public void deleteExpiredUserGetcostAuth(String crewIds) {
		this.userAuthMapDao.deleteExpiredUserGetcostAuth(crewIds);
	}
	
	/**
	 * 批量设置用户权限只读属性
	 * 根权限下的子权限
	 * @param crewId
	 * @param userId
	 * @param authId
	 * @param readonly
	 * @throws Exception 
	 */
	public void multiSetReadOnly(String crewId, String userId, String authId, boolean readonly) throws Exception {
		//查询用户在剧组下拥有的权限，以及其所有的子权限
		List<UserAuthMapModel> userAuthMapList = this.userAuthMapDao.queryByCrewUserAuthIdWithSubAuth(crewId, userId, authId, 0);
		for (UserAuthMapModel userAuthMap : userAuthMapList) {
			userAuthMap.setReadonly(readonly);
			this.updateOne(userAuthMap);
		}
	}
	
	/**
	 * 批量保存用户权限关联关系
	 * @param crewId
	 * @param userIds
	 * @param authId
	 * @param readonly
	 * @param operateType
	 * @throws Exception
	 */
	public void multiSaveUserAuthMap(String crewId, String userIds, String authId, boolean readonly, int operateType) throws Exception{
		String[] userIdArr = userIds.split(",");
		AuthorityModel authorityInfo = null;
		AuthorityModel pAuthInfo = null;
		if(operateType == 1) {
			//查询用户是否拥有该权限的父权限，如果有则新增
			authorityInfo = this.authorityDao.queryAuthById(authId);
			pAuthInfo = this.authorityDao.queryAuthById(authorityInfo.getParentId());
		}
		for(String userId : userIdArr) {
			UserAuthMapModel userAuthMap = this.userAuthMapDao.queryByCrewUserAuthId(crewId, userId, authId);
			if(userAuthMap == null) {//用户没有这个权限
				if(operateType == 1) {//新增
					if (pAuthInfo != null && !pAuthInfo.getParentId().equals("0")) {
						UserAuthMapModel puserAuthMap = this.userAuthMapDao.queryByCrewUserAuthId(crewId, userId, authorityInfo.getParentId());
						if(puserAuthMap == null) {
							continue;
						}
					}					
					userAuthMap = new UserAuthMapModel();
					userAuthMap.setAuthId(authId);
					userAuthMap.setCrewId(crewId);
					userAuthMap.setMapId(UUIDUtils.getId());
					userAuthMap.setReadonly(readonly);
					userAuthMap.setUserId(userId);
					this.addOne(crewId, userId, userAuthMap);
				}
			} else {
				if(operateType == 1 || operateType == 2) {
					userAuthMap.setReadonly(readonly);
					this.updateOne(userAuthMap);
				} else if(operateType == 3) {
					//需要查询出所有的子权限，然后把用户和所有子权限的关联关系删掉
					List<UserAuthMapModel> userAuthMapList = this.queryByCrewUserAuthIdWithSubAuth(crewId, userId, authId);
					for (UserAuthMapModel map : userAuthMapList) {
						this.deleteById(crewId, userId, authId, map.getMapId());
					}
				} else if(operateType == 4) {
					this.multiSetReadOnly(crewId, userId, authId, readonly);
				}
			}
		}
	}
}
