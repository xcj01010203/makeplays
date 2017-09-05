package com.xiaotu.makeplays.authority.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.authority.dao.AuthorityDao;
import com.xiaotu.makeplays.authority.dao.CrewAuthMapDao;
import com.xiaotu.makeplays.authority.model.AuthorityModel;
import com.xiaotu.makeplays.authority.model.CrewAuthMapModel;
import com.xiaotu.makeplays.authority.model.UserAuthMapModel;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * @类名：CrewAuthMapService.java
 * @作者：李晓平
 * @时间：2017年2月9日 下午5:01:20
 * @描述：剧组权限关联关系
 */
@Service
public class CrewAuthMapService {

	@Autowired
	private CrewAuthMapDao crewAuthMapDao;
	
	@Autowired
	private AuthorityDao authorityDao;
	
	@Autowired
	private UserAuthMapService userAuthMapService;
	
	@Autowired
	private CrewInfoService crewInfoService;
	
	/**
	 * 查询剧组拥有的权限
	 * @param crewId
	 */
	public List<CrewAuthMapModel> queryByCrewId(String crewId){
		return this.crewAuthMapDao.queryByCrewId(crewId);
	}
	
	/**
	 * 查询剧组和权限的关联关系
	 * @param crewId
	 * @param authId
	 * @return
	 * @throws Exception 
	 */
	public CrewAuthMapModel queryByCrewAuthId (String crewId, String authId) throws Exception {
		return this.crewAuthMapDao.queryByCrewAuthId(crewId, authId);
	}
	
	/**
	 * 查询剧组的指定权限信息
	 * 该查询会返回该剧组拥有的当前权限，以及其所有的子权限
	 * 目前只支持到三级权限
	 * @param crewId
	 * @param authId
	 * @param status 标识是否有效  0：有效 1：无效
	 * @return
	 */
	public List<CrewAuthMapModel> queryByCrewAuthIdWithSubAuth(String crewId, String authId, Integer status) {
		return this.crewAuthMapDao.queryByCrewAuthIdWithSubAuth(crewId, authId, status);
	}
	
	/**
	 * 根据ID删除剧组权限关联关系
	 * @param mapId
	 * @throws Exception 
	 */
	public void deleteById (String crewId, String authId, String mapId) throws Exception {
		//删除剧组权限关联关系
		this.crewAuthMapDao.deleteOne(mapId, "mapId", CrewAuthMapModel.TABLE_NAME);
		this.checkHasMenuAuth(crewId, authId, 0);
		//删除剧组用户与权限关联关系
		//查询该剧组有该权限的用户
		List<UserAuthMapModel> userList = userAuthMapService.queryByCrewAuthId(crewId, authId);
		if(userList != null &&  userList.size() > 0) {
			for(UserAuthMapModel userAuthMap : userList) {
				//需要查询出所有的子权限，然后把用户和所有子权限的关联关系删掉
				List<UserAuthMapModel> userAuthMapList = this.userAuthMapService.queryByCrewUserAuthIdWithSubAuth(crewId, userAuthMap.getUserId(), authId, 1);
				for (UserAuthMapModel map : userAuthMapList) {
					userAuthMapService.deleteById(crewId, map.getUserId(), authId, map.getMapId());
				}
			}
		}
	}
	
	/**
	 * 添加一条记录
	 * @param crewAuthMap
	 * @throws Exception 
	 */
	public void addOne(String crewId, CrewAuthMapModel crewAuthMap) throws Exception {
		this.crewAuthMapDao.add(crewAuthMap);
		this.checkHasMenuAuth(crewId, crewAuthMap.getAuthId(), 1);
	}
	
	/**
	 * 修改一条记录
	 * @param crewAuthMap
	 * @throws Exception 
	 */
	public void updateOne(CrewAuthMapModel crewAuthMap) throws Exception {
		this.crewAuthMapDao.update(crewAuthMap, "mapId");
		if(crewAuthMap.getReadonly()) {//只读
			//更新该剧组下用户该权限的只读属性
			this.userAuthMapService.updateUserAuthReadOnlyByCrewAuthId(crewAuthMap.getCrewId(), crewAuthMap.getAuthId(), 1);
		}
	}
	
	/**
	 * 校验剧组和顶级菜单权限的关系
	 * @param crewId
	 * @param authId
	 * @param flag 0：删除,1：新增
	 * @throws Exception
	 */
	public void checkHasMenuAuth (String crewId, String authId, Integer flag) throws Exception {		
		//找到该权限的父权限
		AuthorityModel authorityInfo = this.authorityDao.queryAuthById(authId);
		//如果当前权限是顶级菜单权限
		if(authorityInfo.getParentId().equals("0")) {
			if(flag == 1) {//新增
				addChildMenuAuth(crewId, authId);
			}
		} else {
			AuthorityModel pAuthInfo = this.authorityDao.queryAuthById(authorityInfo.getParentId());
			
			//如果父权限是顶级菜单权限（菜单权限的parentId是0，移动端的顶级权限的parentId是1）
			if (pAuthInfo != null && pAuthInfo.getParentId().equals("0")) {
				//查询剧组拥有的权限，以及其所有的子权限
				List<CrewAuthMapModel> crewAuthMapList = this.crewAuthMapDao.queryByCrewAuthIdWithSubAuth(crewId, pAuthInfo.getAuthId(), 0);
				
				boolean hasTheParentAuth = false;	//标识剧组是否拥有该权限
				String pAuthMapId = "";
				for (CrewAuthMapModel crewAuthMap : crewAuthMapList) {
					if (crewAuthMap.getAuthId().equals(pAuthInfo.getAuthId())) {
						hasTheParentAuth = true;
						pAuthMapId = crewAuthMap.getMapId();
						break;
					}
				}
				
				//如果有一个子权限且没有父权限，则新增，如果一个子权限都没有且有父权限，则删除
				if (!hasTheParentAuth && crewAuthMapList != null && crewAuthMapList.size() > 0) {
					//新增与顶级菜单权限的关联
					CrewAuthMapModel newCrewAuth = new CrewAuthMapModel();
					newCrewAuth.setMapId(UUIDUtils.getId());
					newCrewAuth.setAuthId(pAuthInfo.getAuthId());
					newCrewAuth.setCrewId(crewId);
					newCrewAuth.setReadonly(false);
					this.crewAuthMapDao.add(newCrewAuth);
				}
				
				if (hasTheParentAuth && crewAuthMapList.size() == 1) {
					//删除与顶级菜单权限的关联
					this.crewAuthMapDao.deleteOne(pAuthMapId, "mapId", CrewAuthMapModel.TABLE_NAME);
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
	public void addChildMenuAuth(String crewId, String authId) throws Exception{
		//查询剧组拥有的权限，以及其所有的子权限
		List<CrewAuthMapModel> crewAuthMapList = this.crewAuthMapDao.queryByCrewAuthIdWithSubAuth(crewId, authId, 1);
		//将该权限下的二级权限赋给该剧组，设为可编辑
		//查询该权限下的二级权限
		List<AuthorityModel> authorityList = this.authorityDao.queryAuthByPid(authId);
		for(AuthorityModel authority : authorityList) {
			boolean isExist = false;
			if(crewAuthMapList != null && crewAuthMapList.size() > 0) {
				for(CrewAuthMapModel crewAuthMap : crewAuthMapList) {
					if(crewAuthMap.getAuthId().equals(authority.getAuthId())) {
						isExist = true;
						break;
					}
				}
			}
			if(!isExist) {
				//新增与二级菜单权限的关联
				CrewAuthMapModel newCrewAuth = new CrewAuthMapModel();
				newCrewAuth.setMapId(UUIDUtils.getId());
				newCrewAuth.setAuthId(authority.getAuthId());
				newCrewAuth.setCrewId(crewId);
				newCrewAuth.setReadonly(false);
				this.crewAuthMapDao.add(newCrewAuth);
				
				//递归添加下一级子权限
				addChildMenuAuth(crewId, authority.getAuthId());
			}
		}
	}
	
	/**
	 * 将过期剧组的权限设为只读
	 */
	public void updateExpiredCrewAuth() {
		//查询未刷新过权限的过期剧组
		List<CrewInfoModel> expiredCrewList = crewInfoService.queryExpiredCrewNeedRefreshAuth();
		if(expiredCrewList != null && expiredCrewList.size() > 0) {
			String crewIds = "";
			for(CrewInfoModel crewInfo : expiredCrewList) {
				crewIds += "," + crewInfo.getCrewId();
			}
			crewIds = crewIds.substring(1).replace(",", "','");
			//将过期剧组的用户权限设为只读
			userAuthMapService.updateExpiredCrewUserAuth(crewIds);			
			//将过期剧组的权限设为只读
			crewAuthMapDao.updateExpiredCrewAuth(crewIds);
			//将过期剧组用户的收支管理权限去掉
			userAuthMapService.deleteExpiredUserGetcostAuth(crewIds);
			//将过期剧组的收支管理权限去掉
			crewAuthMapDao.deleteExpiredCrewGetcostAuth(crewIds);
			//更改处理过的剧组状态--“是否已刷新权限”
			crewInfoService.updateExpiredCrewStatus(crewIds);
		}
	}
}
