package com.xiaotu.makeplays.authority.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.authority.dao.AuthorityDao;
import com.xiaotu.makeplays.authority.dao.RoleAuthMapDao;
import com.xiaotu.makeplays.authority.model.AuthorityModel;
import com.xiaotu.makeplays.authority.model.RoleAuthMapModel;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 角色权限关联关系
 * @author xuchangjian 2016-6-2上午10:16:16
 */
@Service
public class RoleAuthMapService {

	@Autowired
	private RoleAuthMapDao roleAuthMapDao;
	
	@Autowired
	private AuthorityDao authorityDao;
	
	/**
	 * 查询角色的拥有的权限
	 * @param roleId
	 * @param crewId
	 */
	public List<RoleAuthMapModel> queryByRoleId(String roleId,String crewId){
		return this.roleAuthMapDao.queryByRoleId(roleId, crewId);
	}
	
	/**
	 * 查询角色和权限的关联关系
	 * @param roleId
	 * @param authId
	 * @return
	 * @throws Exception 
	 */
	public RoleAuthMapModel queryByRoleAuthId (String crewId, String roleId, String authId) throws Exception {
		return this.roleAuthMapDao.queryByRoleAuthId(crewId, roleId, authId);
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
	public List<RoleAuthMapModel> queryByRoleAuthIdWithSubAuth(String crewId, String roleId, String authId) {
		return this.roleAuthMapDao.queryByRoleAuthIdWithSubAuth(crewId, roleId, authId);
	}
	
	/**
	 * 根据ID删除角色权限关联关系
	 * @param mapId
	 * @throws Exception 
	 */
	public void deleteById (String crewId, String roleId, String authId, String mapId) throws Exception {
		this.roleAuthMapDao.deleteOne(mapId, "mapId", RoleAuthMapModel.TABLE_NAME);
		this.checkHasMenuAuth(crewId, roleId, authId);
	}
	
	/**
	 * 添加一条记录
	 * @param roleAuthMap
	 * @throws Exception 
	 */
	public void addOne(String crewId, String roleId, RoleAuthMapModel roleAuthMap) throws Exception {
		this.roleAuthMapDao.add(roleAuthMap);
		this.checkHasMenuAuth(crewId, roleId, roleAuthMap.getAuthId());
	}
	
	/**
	 * 校验用户和顶级菜单权限的关系
	 * @param crewId
	 * @param userId
	 * @param authId
	 * @throws Exception
	 */
	public void checkHasMenuAuth (String crewId, String roleId, String authId) throws Exception {
		
		//找到该权限的父权限
		AuthorityModel authorityInfo = this.authorityDao.queryAuthById(authId);
		AuthorityModel pAuthInfo = this.authorityDao.queryAuthById(authorityInfo.getParentId());
		
		//如果父权限是顶级菜单权限（菜单权限的parentId是0，移动端的顶级权限的parentId是1）
		if (pAuthInfo != null && pAuthInfo.getParentId().equals("0")) {
			//查询用户在剧组下拥有的权限，以及其所有的子权限
			List<RoleAuthMapModel> roleAuthMapList = this.roleAuthMapDao.queryByRoleAuthIdWithSubAuth(crewId, roleId, pAuthInfo.getAuthId());
			
			boolean hasTheParentAuth = false;	//标识角色是否拥有该权限
			String pAuthMapId = "";
			for (RoleAuthMapModel roleAuthMap : roleAuthMapList) {
				if (roleAuthMap.getAuthId().equals(pAuthInfo.getAuthId())) {
					hasTheParentAuth = true;
					pAuthMapId = roleAuthMap.getMapId();
					break;
				}
			}
			
			//如果有一个子权限且没有父权限，则新增，如果一个子权限都没有且有父权限，则删除
			if (!hasTheParentAuth && roleAuthMapList != null && roleAuthMapList.size() > 0) {
				//新增与顶级菜单权限的关联
				RoleAuthMapModel newRoleAuth = new RoleAuthMapModel();
				newRoleAuth.setMapId(UUIDUtils.getId());
				newRoleAuth.setRoleId(roleId);
				newRoleAuth.setAuthId(pAuthInfo.getAuthId());
				newRoleAuth.setCrewId(crewId);
				newRoleAuth.setReadonly(false);
				this.roleAuthMapDao.add(newRoleAuth);
			}
			
			if (hasTheParentAuth && roleAuthMapList.size() == 1) {
				//删除与顶级菜单权限的关联
				this.roleAuthMapDao.deleteOne(pAuthMapId, "mapId", RoleAuthMapModel.TABLE_NAME);
			}
		}
	}
	
	/**
	 * 修改一条记录
	 * @param roleAuthMap
	 * @throws Exception 
	 */
	public void updateOne(RoleAuthMapModel roleAuthMap) throws Exception {
		this.roleAuthMapDao.update(roleAuthMap, "mapId");
	}
}
