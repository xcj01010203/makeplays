package com.xiaotu.makeplays.authority.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.authority.dao.AuthorityDao;
import com.xiaotu.makeplays.authority.dao.CrewAuthMapDao;
import com.xiaotu.makeplays.authority.dao.RoleAuthMapDao;
import com.xiaotu.makeplays.authority.dao.UserAuthMapDao;
import com.xiaotu.makeplays.authority.model.AuthorityModel;
import com.xiaotu.makeplays.authority.model.CrewAuthMapModel;
import com.xiaotu.makeplays.authority.model.RoleAuthMapModel;
import com.xiaotu.makeplays.authority.model.UserAuthMapModel;
import com.xiaotu.makeplays.authority.model.constants.AuthorityPlatform;
import com.xiaotu.makeplays.crew.dao.CrewInfoDao;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.mobile.push.umeng.service.UmengNotification;
import com.xiaotu.makeplays.mobile.server.index.dto.MobileAuthorityDto;
import com.xiaotu.makeplays.mobile.server.index.dto.MobileSingleAuthorityDto;
import com.xiaotu.makeplays.sysrole.dao.UserRoleMapDao;
import com.xiaotu.makeplays.sysrole.service.UserRoleMapService;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.model.constants.UserType;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.AuthorityConstants;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.utils.UUIDUtils;

@Service
public class AuthorityService {
	
	Logger logger = LoggerFactory.getLogger(UmengNotification.class);
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private AuthorityDao authorityDao;
	
	@Autowired
	private RoleAuthMapDao roleAuthMapDao;
	
	@Autowired
	private UserAuthMapDao userAuthMapDao;
	
	@Autowired
	private UserRoleMapDao userRoleMapDao;
	
	@Autowired
	private CrewInfoDao crewInfoDao;
	
	@Autowired
	private CrewAuthMapDao crewAuthMapDao;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRoleMapService userRoleMapService;
	
	/**
	 * 查询权限信息
	 * @param type 权限作用平台，2：pc，3：app
	 * @return
	 */
	public List<AuthorityModel> queryAuthorityList(int type) throws Exception {
		return authorityDao.queryAuthorityList(type);
	}
	
	/**
	 * 验证操作编码唯一
	 * @param authCode 操作编码
	 * @param authId 权限ID
	 * @return
	 */
	public boolean validateAuthCode(String authCode, String authId){
		List<Map<String,Object>> li = authorityDao.validateAuthCode(authCode, authId);
		if(li != null && li.size() > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 添加权限信息
	 * @param auth
	 * @throws Exception
	 */
	public void addAuthority(AuthorityModel auth, Boolean isForAllCrew) throws Exception{
		authorityDao.add(auth);
		
		if(isForAllCrew) {
			//将该权限赋给所有剧组；如果剧组没有该权限的父权限，则不将该权限赋给该剧组
			boolean readonly = false;
			if(auth.getDifferInRAndW() && auth.getDefaultRorW() == 1) {
				readonly = true;
			}
			crewAuthMapDao.addAuthToCrewByAuthId(auth.getAuthId(), readonly, auth.getParentId());
		}
	}
	
	/**
	 * 修改权限信息
	 * @param auth
	 * @throws Exception
	 */
	public void updateAuthority(AuthorityModel auth) throws Exception{
		authorityDao.updateWithNull(auth, "authId");
	}
	
	/**
	 * 查询是否有子节点
	 * @param parentId
	 * @return
	 */
	public boolean isHasChidAuth(String parentId) {
		List<Map<String,Object>> li = authorityDao.isHasChidAuth(parentId);
		if(li != null && li.size() > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 查询权限是否被角色使用
	 * @param authId
	 * @return
	 * @throws Exception
	 */
	public boolean isRoleAuthUsed(String authId) throws Exception {
		List<Map<String,Object>> li = authorityDao.isRoleAuthUsed(authId);
		if(li != null && li.size() > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 查询权限是否被admin和客服以外的角色使用
	 * @param authId
	 * @return
	 * @throws Exception
	 */
	public boolean isAuthUsedByCommonRole(String authId) throws Exception {
		List<Map<String,Object>> li = authorityDao.isRoleAuthUsedByCommonRole(authId);
		if(li != null && li.size() > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 查询权限是否被用户使用
	 * @param authId
	 * @return
	 * @throws Exception
	 */
	public boolean isUserAuthUsed(String authId) throws Exception {
		List<Map<String,Object>> li = authorityDao.isUserAuthUsed(authId);
		if(li != null && li.size() > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 删除权限
	 * @param authId
	 * @return
	 * @throws Exception 
	 */
	public void deleteAuthority(String authId) throws Exception{
		this.authorityDao.deleteOne(authId, "authId", AuthorityModel.TABLE_NAME);
		
		//删除剧组与权限关联关系
		this.authorityDao.deleteOne(authId, "authId", CrewAuthMapModel.TABLE_NAME);
	}
	
	/**
	 * 根据ID查询权限信息
	 * @param authId
	 * @return
	 * @throws Exception
	 */
	public AuthorityModel queryAuthById(String authId) throws Exception {
		return authorityDao.queryAuthById(authId);
	}
	
	/**
	 * 修改权限表顺序
	 * @param ids
	 */
	public void updateAuthoritySequence(String ids){
		String authId[] = ids.split(",");
		for (int i = 0; i < authId.length; i++) {
			this.authorityDao.updateAuthoritySequence(authId[i], (i + 1));
		}
	}
	
	/**
	 * 查询当前组最大顺序
	 * @param parentId
	 * @return
	 */
	public int queryAuthorityMaxSeq(String parentId) {
		return this.authorityDao.queryAuthorityMaxSeq(parentId);
	}
	
	/**
	 * 根据某个权限获取所有角色及角色对应此权限的状态--根节点
	 * @param authId
	 * @return
	 */
	public List<Map<String,Object>> queryAllRoleByAuthId(String authId){
		return this.authorityDao.queryAllRoleByAuthId(authId);
	}
	
	/**
	 * 根据某个权限获取所有角色及角色对应此权限的状态--子节点
	 * @param authId
	 * @param parentId
	 * @return
	 */
	public List<Map<String,Object>> queryAllRoleByAuthId(String authId,String parentId){
		return this.authorityDao.queryAllRoleByAuthId(authId,parentId);
	}
	
	/**
	 * 判断权限的子权限是否有被修改的角色使用
	 * @param authId
	 * @param roleIds
	 * @return
	 */
	public boolean judgeAuthorityChildren(String authId, String roleIds){
		List<Map<String,Object>> li = authorityDao.judgeAuthorityChildren(authId, roleIds);
		if(li != null && li.size() > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断权限的子权限是否有被修改的剧组使用
	 * @param authId
	 * @param crewIds
	 * @return
	 */
	public boolean judgeAuthChildIsUsedByCrew(String authId, String crewIds) {
		List<Map<String,Object>> li = authorityDao.judgeAuthChildIsUsedByCrew(authId, crewIds);
		if(li != null && li.size() > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 修改权限的角色分布
	 * @param authId
	 * @param roles
	 * @throws Exception
	 */
	public void updateRoleAuth(String authId, String roles, boolean isDelete) throws Exception{
		//权限信息
		AuthorityModel authorityInfo = this.authorityDao.queryAuthById(authId);
		boolean readonly = false;
		if (authorityInfo.getDefaultRorW() == 1) {
			 readonly = true;
		}
		
		long startTime  = System.currentTimeMillis();
		//获取角色用户集合
		Map<String, Map<String, List<String>>> roleUserMap = this.getRoleUserMap();
		
		//获取用户权限集合
		Map<String, Map<String, List<String>>> userAuthMap = this.getUserAuthMap();
		
		//获取用户角色集合
		Map<String, Map<String, List<String>>> userRoleMap = this.getUserRoleMap();
		
		//获取剧组权限集合
		Map<String,List<String>> crewAuthMap = this.getCrewAuthMap();
		
		//获取角色权限集合
		Map<String,List<String>> roleAuthMap = this.getRoleAuthMap();
		
		//获取剧组合集
		List<String> crewList = this.getAllCrewId();
		
		String str[] = roles.split(",");
		for (int i = 0; i < str.length; i++) {
			String s[] = str[i].split("-");
			
			RoleAuthMapModel roleAuthMapModel = new RoleAuthMapModel();
			roleAuthMapModel.setRoleId(s[0]);
			roleAuthMapModel.setAuthId(authId);
			
			if(s[1].equals("1")){  //添加角色权限
				
				roleAuthMapModel.setMapId(UUIDUtils.getId());
				roleAuthMapModel.setCrewId("0");
				roleAuthMapModel.setReadonly(readonly);
				
				//添加角色权限
				this.roleAuthMapDao.add(roleAuthMapModel);
				
				if(isDelete) {
					//判断不同剧组，不同用户
					for (String string : crewList) {
						//获取该剧组下的角色用户
						Map<String,List<String>> roleUser = roleUserMap.get(string);
						if(roleUser != null){
							//获取该角色的用户
							List<String> user = roleUser.get(s[0]);
							//获取该剧组用户权限
							Map<String,List<String>> userAuth = userAuthMap.get(string);
							//判断用户是否有此权限
							if(user!=null && userAuth!=null) {
								//获取该剧组权限
								List<String> crewAuth = crewAuthMap.get(string);
								for (String userId : user) {
									if(crewAuth != null && crewAuth.contains(authId)) {
										//获取该用户在剧组中的权限
										List<String> auth = userAuth.get(userId);
										if(auth==null || !auth.contains(authId)){
											//添加用户权限
											UserAuthMapModel singleUserAuthMap = new UserAuthMapModel();
											singleUserAuthMap.setMapId(UUIDUtils.getId());
											singleUserAuthMap.setAuthId(authId);
											singleUserAuthMap.setUserId(userId);
											singleUserAuthMap.setCrewId(string);
											
											CrewInfoModel crewInfo = this.crewInfoDao.queryById(string);
											if(sdf.format(crewInfo.getEndDate()).compareTo(sdf.format(new Date())) < 0) {
												singleUserAuthMap.setReadonly(true);
											} else {
												singleUserAuthMap.setReadonly(readonly);
											}
											
											this.userAuthMapDao.add(singleUserAuthMap);
											
											//更改map值
											if(auth==null){
												auth = new ArrayList<String>();
											}
											auth.add(authId);
											userAuth.put(userId, auth);
										}
									}
								}
							}
							//更改用户权限
							userAuthMap.put(string, userAuth);
						}				
					}
				}
				
				//更改角色权限
				List<String> roleauth = roleAuthMap.get(s[0]);
				if(roleauth==null || !roleauth.contains(authId)){
					if(roleauth==null){
						roleauth = new ArrayList<String>();
					}
					roleauth.add(authId);
					roleAuthMap.put(s[0], roleauth);
				}
				
			}else{  
				//删除角色权限
				this.roleAuthMapDao.deleteRoleAuthMap(authId,s[0]);

				if(isDelete) {
					//判断不同剧组，不同用户
					for (String string : crewList) {
						//获取改剧组下的角色用户
						Map<String,List<String>> roleUser = roleUserMap.get(string);
						if(roleUser!=null){
							//获取改角色的用户
							List<String> user = roleUser.get(s[0]);
							//获取该剧组用户权限
							Map<String,List<String>> userAuth = userAuthMap.get(string);
							//获取该剧组用户 角色
							Map<String,List<String>> userRole = userRoleMap.get(string);
							//判断用户是否有此权限
							if(user!=null && userAuth!=null) {
								for (String userId : user) {
									//获取该用户在剧组中的权限
									List<String> auth = userAuth.get(userId);
									if(auth!=null && auth.contains(authId)){
										//获取用户的所有角色
										List<String> role = userRole.get(userId);
										
										//其他角色是否有此权限标记
										boolean flag = false;
										//判断用户的其他角色是否有此权限
										if(role!=null){
											for (String string2 : role) {
												if(!string2.equals(s[0])){
													//获取当前角色的权限
													List<String> roleau = roleAuthMap.get(string2);
													if(roleau!=null && roleau.contains(authId)){
														flag = true;
														break;
													}
												}
											}
											if(!flag){
												//删除用户权限
												this.authorityDao.deleteByCrewUserAuthId(string, userId, authId);
												//更改map值
												auth.remove(authId);
												userAuth.put(userId, auth);
											}
										}
										
									}
									//更改用户权限
									userAuthMap.put(string, userAuth);
								}
							}							
						}
					}
				}
					
				//更改角色权限
				List<String> roleauth = roleAuthMap.get(s[0]);
				if(roleauth!=null && roleauth.contains(authId)){
					roleauth.remove(authId);
					roleAuthMap.put(s[0], roleauth);
				}
			}
		}
		
		logger.debug("更新权限时长>>>>>>>>>>>>>"+(System.currentTimeMillis() - startTime));
		
	}
	
	/**
	 * 修改权限的剧组分布
	 * @param authId
	 * @param crewIds
	 * @throws Exception
	 */
	public void updateCrewAuth(String authId, String crewIds) throws Exception{
		//权限信息
		AuthorityModel authorityInfo = this.authorityDao.queryAuthById(authId);
		boolean readonly = false;
		if (authorityInfo.getDefaultRorW() == 1) {
			 readonly = true;
		}
		
		String str[] = crewIds.split(",");
		for (int i = 0; i < str.length; i++) {
			String s[] = str[i].split("-");
			
			String crewId = s[0];
			CrewInfoModel crewInfo = this.crewInfoDao.queryById(crewId);
			CrewAuthMapModel crewAuthMapModel = new CrewAuthMapModel();
			crewAuthMapModel.setCrewId(s[0]);
			crewAuthMapModel.setAuthId(authId);
			
			if(s[1].equals("1")){  //添加剧组权限关联关系
				
				crewAuthMapModel.setMapId(UUIDUtils.getId());
				if(sdf.format(crewInfo.getEndDate()).compareTo(sdf.format(new Date())) < 0) {
					crewAuthMapModel.setReadonly(true);
				} else {
					crewAuthMapModel.setReadonly(readonly);
				}
				
				//添加剧组权限
				this.crewAuthMapDao.add(crewAuthMapModel);
			}else{  
				//删除剧组权限
				this.crewAuthMapDao.deleteCrewAuthMap(authId, s[0]);
				//删除剧组用户权限
				this.userAuthMapDao.deleteUserAuthMapByCrew(authId, s[0]);
			}
		}
	}
	
	/**
	 * 获取所有的角色用户集合
	 * @return
	 */
	public Map<String,Map<String,List<String>>> getRoleUserMap(){
		List<Map<String,Object>> li = userRoleMapDao.queryAllRoleUserMap();
		Map<String,Map<String,List<String>>> crewmap = new HashMap<String, Map<String,List<String>>>();  //剧组
		List<String> ls = null;
		Map<String,List<String>> map = null;  //剧组角色用户
		for (Map<String, Object> one : li) {
			String crewId = one.get("crewId") + "";
			String userId = one.get("userId") + "";
			if(StringUtil.isNotBlank(crewId) && StringUtil.isNotBlank(userId)){
				String roleId = one.get("roleId") + "";
				if(crewmap.containsKey(crewId)){
					map = crewmap.get(crewId);
					if(map.containsKey(roleId)){
						ls = map.get(roleId);
						ls.add(userId);
					}else{
						ls = new ArrayList<String>();
						ls.add(userId);
						map.put(roleId, ls);
					}					
				}else{
					map = new HashMap<String, List<String>>();
					if(map.containsKey(roleId)){
						ls = map.get(roleId);
						ls.add(userId);
					}else{
						ls = new ArrayList<String>();
						ls.add(userId);
						map.put(roleId, ls);
					}
					crewmap.put(crewId, map);
				}
			}				
		}
		return crewmap;
	}
	
	/**
	 * 获取所有的用户权限集合
	 * @return
	 */
	public Map<String,Map<String,List<String>>> getUserAuthMap(){
		List<Map<String,Object>> li = userAuthMapDao.queryAllUserAuthMap();
		Map<String,Map<String,List<String>>> crewmap = new HashMap<String, Map<String,List<String>>>();  //剧组
		List<String> ls = null;
		Map<String,List<String>> map = null;  //剧组角色用户
		for (Map<String, Object> one : li) {
			String crewId = one.get("crewId") + "";
			String authId = one.get("authId") + "";
			if(StringUtil.isNotBlank(crewId) && StringUtil.isNotBlank(authId)){
				String userId = one.get("userId") + "";
				if(crewmap.containsKey(crewId)){
					map = crewmap.get(crewId);
					if(map.containsKey(userId)){
						ls = map.get(userId);
						ls.add(authId);
					}else{
						ls = new ArrayList<String>();
						ls.add(authId);
						map.put(userId, ls);
					}					
				}else{
					map = new HashMap<String, List<String>>();
					if(map.containsKey(userId)){
						ls = map.get(userId);
						ls.add(authId);
					}else{
						ls = new ArrayList<String>();
						ls.add(authId);
						map.put(userId, ls);
					}
					crewmap.put(crewId, map);
				}
			}
				
		}
		return crewmap;
	}
	
	/**
	 * 获取所有的剧组权限集合
	 * @return
	 */
	public Map<String,List<String>> getCrewAuthMap(){
		List<Map<String,Object>> li = this.crewAuthMapDao.queryAllCrewAuthMap();
		Map<String,List<String>> map = new HashMap<String, List<String>>();
		List<String> ls = null;
		for (Map<String, Object> one : li) {
			String authId = one.get("authId") + "";
			if(StringUtil.isNotBlank(authId)){
				String crewId = one.get("crewId") + "";
				if(map.containsKey(crewId)){
					ls = map.get(crewId);
				}else{
					ls = new ArrayList<String>();
					map.put(crewId, ls);
				}
				ls.add(authId);
			}				
		}
		return map;
	}
	
	/**
	 * 获取所有的用户角色集合
	 * @return
	 */
	public Map<String,Map<String,List<String>>> getUserRoleMap(){
		List<Map<String,Object>> li = this.userRoleMapDao.queryAllUserRoleMap();
		Map<String,Map<String,List<String>>> crewmap = new HashMap<String, Map<String,List<String>>>();  //剧组
		List<String> ls = null;
		Map<String,List<String>> map = null;  //剧组角色用户
		for (Map<String, Object> one : li) {
			String crewId = one.get("crewId") + "";
			String roleId = one.get("roleId") + "";
			if(StringUtil.isNotBlank(crewId) && StringUtil.isNotBlank(roleId)){
				String userId = one.get("userId") + "";
				if(crewmap.containsKey(crewId)){
					map = crewmap.get(crewId);
					if(map.containsKey(userId)){
						ls = map.get(userId);
						ls.add(roleId);
					}else{
						ls = new ArrayList<String>();
						ls.add(roleId);
						map.put(userId, ls);
					}					
				}else{
					map = new HashMap<String, List<String>>();
					if(map.containsKey(userId)){
						ls = map.get(userId);
						ls.add(roleId);
					}else{
						ls = new ArrayList<String>();
						ls.add(roleId);
						map.put(userId, ls);
					}
					crewmap.put(crewId, map);
				}
			}				
		}
		return crewmap;
	}

	/**
	 * 获取角色权限集合
	 */
	public Map<String,List<String>> getRoleAuthMap(){
		List<Map<String,Object>> li = this.roleAuthMapDao.queryAllRoleAuthMap();
		Map<String,List<String>> map = new HashMap<String, List<String>>();
		List<String> ls = null;
		for (Map<String, Object> one : li) {
			String authId = one.get("authId") + "";
			if(StringUtil.isNotBlank(authId)){
				String roleId = one.get("roleId") + "";
				if(map.containsKey(roleId)){
					ls = map.get(roleId);
					ls.add(authId);
				}else{
					ls = new ArrayList<String>();
					ls.add(authId);
					map.put(roleId, ls);
				}
			}				
		}
		return map;
	}
	
	/**
	 * 获取所有剧组的id
	 * @return
	 */
	public List<String> getAllCrewId() {
		List<Map<String,Object>> li = crewInfoDao.queryAllCrewIdAndName();
		List<String> ls = new ArrayList<String>();
		for (Map<String, Object> map : li) {
			ls.add(map.get("crewId") + "");
		}
//		ls.add("0");
		return ls;
	}
	
	/**
	 * 查询用户菜单
	 * @param userId
	 * @param crewId
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public List<Map<String,Object>> queryUserAuthority(String userId,String crewId) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		
		List<Map<String,Object>> auth = null;
		
		if(null == crewId){
			auth = authorityDao.queryAdminAuthToTreeMap();
		}else{
			auth = authorityDao.queryUserAuthToTreeMap(userId,crewId);
		}
		
		if(auth != null) {
			List<Map<String,Object>> resuleLi = new ArrayList<Map<String,Object>>();
			
			List<Map<String,Object>> childrenLi = null;
			for (Map<String, Object> map : auth) {
				childrenLi = new ArrayList<Map<String,Object>>();
				for (Map<String, Object> map2 : auth) {
					if(map.get("id").equals(map2.get("parentid"))){
						childrenLi.add(map2);
					}
				}
				if(map.get("parentid").equals("0")) {
					map.put("childList", childrenLi);
					resuleLi.add(map);
				}
			}
			return resuleLi;
		}
		return auth;
	}
	
	/**
	 * 根据某个角色获取所有权限及角色对应此权限的状态
	 */
	public List<Map<String,Object>> getAllAuthByRoleId(String roleId)throws Exception{
		return this.authorityDao.getAllAuthByRoleId(roleId);
	}
	
	/**
	 * 查询用户指定平台的有效的权限信息
	 * @param crewId	剧组ID
	 * @param userId	用户ID
	 * @param authPlantform	平台类型（1：全部   2：pc端     3：移动端）
	 * @return	权限的所有信息  用户对权限是否只读
	 */
	public List<Map<String, Object>> queryEffectiveAuthByUserAndPlantform(String crewId, String userId, Integer authPlantform) {
		return this.authorityDao.queryEffectiveAuthByUserAndPlantform(crewId, userId, authPlantform);
	}
	
	/**
	 * 获取手机端权限信息
	 * @param crewId
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	public MobileAuthorityDto genMobileAuthInfo(String crewId, String userId) throws Exception {
		
		UserInfoModel userInfo = this.userService.queryById(userId);
		
		/*
		 * 普通用户权限
		 */
		List<Map<String, Object>> authorityList = this.authorityDao.queryEffectiveAuthByUserAndPlantform(crewId, userId, AuthorityPlatform.Mobile.getValue());
		MobileAuthorityDto mobileAuthorityDto = new MobileAuthorityDto();
		for (Map<String, Object> auth : authorityList) {
			String authCode = (String) auth.get("authCode");
			String authName = (String) auth.get("authName");
			int readonly = (Integer) auth.get("readonly");
			
			MobileSingleAuthorityDto singleAuth = new MobileSingleAuthorityDto();
			singleAuth.setAuthCode(authCode);
			singleAuth.setAuthName(authName);
			singleAuth.setHasAuth(true);
			if (readonly == 0) {
				singleAuth.setReadonly(false);
			} else {
				singleAuth.setReadonly(true);
			}
			
			//通告单权限
			if (authCode.equals(AuthorityConstants.NOTICE)) {
				mobileAuthorityDto.setNoticeAuth(singleAuth);
			}
			//拍摄进度权限
			if (authCode.equals(AuthorityConstants.SHOOTPROGRESS)) {
				mobileAuthorityDto.setShootProgressAuth(singleAuth);
			}
			//财务权限
			if (authCode.equals(AuthorityConstants.FINANCE)) {
				mobileAuthorityDto.setFinanceAuth(singleAuth);
			}
			//现场信息权限
			if (authCode.equals(AuthorityConstants.SCENE)) {
				mobileAuthorityDto.setSceneAuth(singleAuth);
			}
			//剧组联系表添加权限
			if (authCode.equals(AuthorityConstants.CREW_CONTACT)) {
				mobileAuthorityDto.setCrewContactAuth(singleAuth);
			}
			//剧组设置权限
			if (authCode.equals(AuthorityConstants.CREW_USER_MANAGE)) {
				mobileAuthorityDto.setCrewSettingAuth(singleAuth);
			}
			//评价剧组成员权限
			if (authCode.equals(AuthorityConstants.CREW_USER_EVALUATE)) {
//				mobileAuthorityDto.setCrewUserEvaluateAuth(singleAuth);
			}
			//剧本权限
			if (authCode.equals(AuthorityConstants.SCENARIO)) {
				mobileAuthorityDto.setScenarioAuth(singleAuth);
			}
			//堪景权限
			if (authCode.equals(AuthorityConstants.SCENEVIEW)) {
				mobileAuthorityDto.setSceneViewAuth(singleAuth);
			}
			//车辆权限
			if (authCode.equals(AuthorityConstants.Car)) {
				mobileAuthorityDto.setCarAuth(singleAuth);
			}
			//住宿权限
			if (authCode.equals(AuthorityConstants.INHOTELCOST)) {
				mobileAuthorityDto.setInhotelCostAuth(singleAuth);
			}
			//餐饮权限
			if (authCode.equals(AuthorityConstants.YD_CATER)) {
				mobileAuthorityDto.setCaterAuth(singleAuth);
			}
			//审批权限
			if (authCode.equals(AuthorityConstants.YD_APPROVAL)) {
				mobileAuthorityDto.setApprovalAuth(singleAuth);
			}
			//相册权限
			if (authCode.equals(AuthorityConstants.YD_CREWPICTURE)) {
				mobileAuthorityDto.setCrewPictureAuth(singleAuth);
			}
			
		}
		
		/*
		 * 客服权限
		 */
		if (userInfo.getType() == UserType.CustomerService.getValue()) {
			String roleId = this.userRoleMapService.queryAllUserRoleIds(userId);
			
			Map<String,Object> authCodeMap = this.getServiceAuth(roleId);
			
			Set<String> authCodeSet = authCodeMap.keySet();
			for (String authCode : authCodeSet) {
				String myAuthCode = authCode;
				int readonly = (Integer) authCodeMap.get(myAuthCode);
				
				MobileSingleAuthorityDto singleAuth = new MobileSingleAuthorityDto();
				singleAuth.setHasAuth(true);
				if (readonly == 0) {
					singleAuth.setReadonly(false);
				} else {
					singleAuth.setReadonly(true);
				}
				
				//通告单权限
				if (authCode.equals(AuthorityConstants.NOTICE)) {
					mobileAuthorityDto.setNoticeAuth(singleAuth);
				}
				//拍摄进度权限
				if (authCode.equals(AuthorityConstants.SHOOTPROGRESS)) {
					mobileAuthorityDto.setShootProgressAuth(singleAuth);
				}
				//财务权限
				if (authCode.equals(AuthorityConstants.FINANCE)) {
					mobileAuthorityDto.setFinanceAuth(singleAuth);
				}
				//现场信息权限
				if (authCode.equals(AuthorityConstants.SCENE)) {
					mobileAuthorityDto.setSceneAuth(singleAuth);
				}
				//剧组联系表添加权限
				if (authCode.equals(AuthorityConstants.CREW_CONTACT)) {
					mobileAuthorityDto.setCrewContactAuth(singleAuth);
				}
				//剧组设置权限
				if (authCode.equals(AuthorityConstants.CREW_USER_MANAGE)) {
					mobileAuthorityDto.setCrewSettingAuth(singleAuth);
				}
				//评价剧组成员权限
				if (authCode.equals(AuthorityConstants.CREW_USER_EVALUATE)) {
//					mobileAuthorityDto.setCrewUserEvaluateAuth(singleAuth);
				}
				//剧本权限
				if (authCode.equals(AuthorityConstants.SCENARIO)) {
					mobileAuthorityDto.setScenarioAuth(singleAuth);
				}
				//堪景权限
				if (authCode.equals(AuthorityConstants.SCENEVIEW)) {
					mobileAuthorityDto.setSceneViewAuth(singleAuth);
				}
				//车辆权限
				if (authCode.equals(AuthorityConstants.Car)) {
					mobileAuthorityDto.setCarAuth(singleAuth);
				}
				//车辆权限
				if (authCode.equals(AuthorityConstants.INHOTELCOST)) {
					mobileAuthorityDto.setInhotelCostAuth(singleAuth);
				}
				//住宿权限
				if (authCode.equals(AuthorityConstants.INHOTELCOST)) {
					mobileAuthorityDto.setInhotelCostAuth(singleAuth);
				}
				//餐饮权限
				if (authCode.equals(AuthorityConstants.YD_CATER)) {
					mobileAuthorityDto.setCaterAuth(singleAuth);
				}
				//审批权限
				if (authCode.equals(AuthorityConstants.YD_APPROVAL)) {
					mobileAuthorityDto.setApprovalAuth(singleAuth);
				}
				//相册权限
				if(authCode.equals(AuthorityConstants.YD_CREWPICTURE)) {
					mobileAuthorityDto.setCrewPictureAuth(singleAuth);
				}
			}
		}
		
		return mobileAuthorityDto;
	}
	/**
	 * 查询客服菜单
	 */
	public List<Map<String,Object>> getServiceMenu(String roleId){
		List<Map<String,Object>> auth = this.authorityDao.getServiceMenu(roleId);
		if(auth!=null){
			List<Map<String,Object>> resuleLi = new ArrayList<Map<String,Object>>();
			
			for (Map<String, Object> map : auth) {
				boolean b = false;
				List<Map<String,Object>> childrenLi = new ArrayList<Map<String,Object>>();
				for (Map<String, Object> map2 : auth) {
					if(map.get("id").equals(map2.get("parentid"))){
						childrenLi.add(map2);
						b = true;
					}
				}
				if(map.get("parentId").equals("0")){
					map.put("childList", childrenLi);
					resuleLi.add(map);
				}
			}
			return resuleLi;
		}
		return null;
	}
	
	/**
	 * 获取客服操作权限
	 */
	public Map<String, Object> getServiceAuth(String roleId){
		return this.authorityDao.getServiceAuth(roleId);
	}
	
	/**
	 * 校验用户是否有指定的权限
	 * @param userId
	 * @param authCode
	 * @return
	 * @throws Exception 
	 */
	public boolean checkHashAuthority(String crewId, String userId, String authCode) throws Exception {
		AuthorityModel auth = this.authorityDao.queryOneByUserIdAndAuthCode(crewId, userId, authCode);
		if (auth == null) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 根据平台类型查询所有权限信息（无论有效无效）
	 * 如果authPlantform为null,则返回所有有效的权限
	 * @param authPlantform 平台类型
	 * @return 返回系统中所有的权限
	 */
	public List<AuthorityModel> queryAuthByPlatform (Integer authPlantform) {
		return this.authorityDao.queryAuthByPlatform(authPlantform);
	}
	
	/**
	 * 根据平台类型查询所有有效的权限信息
	 * 如果authPlantform为null,则返回所有有效的权限
	 * @param authPlantform 平台类型
	 * @return 该方法会把admin和客服特有的权限排除掉
	 */
	public List<AuthorityModel> queryAuthByPlatformWithoutAdmin (Integer authPlantform) {
		return this.authorityDao.queryAuthByPlatformWithoutAdmin(authPlantform);
	}
	
	/**
	 * 查询所有权限及拥有该权限的用户数量
	 * @param type
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryAuthAndUserNumWithoutAdmin(int type, String crewId) {
		return this.authorityDao.queryAuthAndUserNumWithoutAdmin(type, crewId);
	}
	
	/**
	 * 查询剧组拥有的权限列表
	 * @param crewId
	 * @return
	 */
	public List<AuthorityModel> queryCrewAuthByCrewId(String crewId) {
		return this.authorityDao.queryCrewAuthByCrewId(crewId);
	}
	
	/**
	 * 根据某个权限获取所有剧组及剧组对应此权限的状态--根节点
	 * @param authId
	 * @return
	 */
	public List<Map<String, Object>> queryAllCrewByAuthId(String authId) {
		return this.authorityDao.queryAllCrewByAuthId(authId);
	}
	
	/**
	 * 根据某个权限获取所有剧组及剧组对应此权限的状态--子节点
	 * @param authId
	 * @param parentId
	 * @return
	 */
	public List<Map<String,Object>> queryAllCrewByAuthId(String authId, String parentId){
		return this.authorityDao.queryAllCrewByAuthId(authId, parentId);
	}
}
