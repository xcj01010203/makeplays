package com.xiaotu.makeplays.sysrole.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.authority.controller.dto.RoleAuthDto;
import com.xiaotu.makeplays.authority.model.AuthorityModel;
import com.xiaotu.makeplays.authority.model.RoleAuthMapModel;
import com.xiaotu.makeplays.authority.model.constants.AuthorityPlatform;
import com.xiaotu.makeplays.authority.service.AuthorityService;
import com.xiaotu.makeplays.authority.service.RoleAuthMapService;
import com.xiaotu.makeplays.sysrole.model.SysroleInfoModel;
import com.xiaotu.makeplays.sysrole.service.SysRoleInfoService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 系统角色
 * @author xuchangjian 2016-5-23上午10:14:39
 */
@RequestMapping("/sysrole")
@Controller
public class SysroleController extends BaseController {
	
	Logger logger = LoggerFactory.getLogger(SysroleController.class);

	@Autowired
	private SysRoleInfoService sysRoleInfoService;
	
	@Autowired
	private AuthorityService authorityService;
	
	@Autowired
	private RoleAuthMapService roleAuthMapService;
	
	/**
	 * 获取系统部门信息
	 * @param crewId	剧组ID
	 * @param userId	用户ID
	 * @param needManager	是否需要管理员信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryCrewDepartmentAndDuties")
   	public Map<String, Object> queryCrewDepartmentAndDuties(HttpServletRequest request, Boolean needManager) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			String crewId = this.getCrewId(request);
			
			if (needManager == null) {
				needManager = false;
			}
			
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			List<SysroleInfoModel> roleList = this.sysRoleInfoService.queryByCrewId(crewId);
			
			//小组信息
			for (SysroleInfoModel sysRoleInfo : roleList) {
				String parentId = sysRoleInfo.getParentId();
				String roleId = sysRoleInfo.getRoleId();
				
				Map<String, Object> singleRoleMap = new HashMap<String, Object>();
				singleRoleMap.put("roleId", sysRoleInfo.getRoleId());
				singleRoleMap.put("roleName", sysRoleInfo.getRoleName());
				singleRoleMap.put("roleDesc", sysRoleInfo.getRoleDesc());
				
				if (parentId.equals("00") || (needManager && roleId.equals(Constants.ROLE_ID_ADMIN)) 
						|| (needManager && roleId.equals(Constants.ROLE_ID_PROJECT_DIRECTOR))) {
					List<Map<String, Object>> child = new ArrayList<Map<String, Object>>();
					singleRoleMap.put("child", child);

					resultList.add(singleRoleMap);
				}
			}
			
			//职务信息
			for (Map<String, Object> map : resultList) {
				String pRoleId = (String) map.get("roleId");
				
				for (SysroleInfoModel sysRoleInfo : roleList) {
					String myparentId = sysRoleInfo.getParentId();
					String myRoleId = sysRoleInfo.getRoleId();
					
					Map<String, Object> singleRoleMap = new HashMap<String, Object>();
					singleRoleMap.put("roleId", sysRoleInfo.getRoleId());
					singleRoleMap.put("roleName", sysRoleInfo.getRoleName());
					singleRoleMap.put("roleDesc", sysRoleInfo.getRoleDesc());
					
					if (pRoleId.equals(myparentId) || (needManager && pRoleId.equals(Constants.ROLE_ID_ADMIN) && myRoleId.equals(Constants.ROLE_ID_ADMIN)) 
							|| (needManager && pRoleId.equals(Constants.ROLE_ID_PROJECT_DIRECTOR) && myRoleId.equals(Constants.ROLE_ID_PROJECT_DIRECTOR))) {
						List<Map<String, Object>> child = (List<Map<String, Object>>) map.get("child");
						child.add(singleRoleMap);
					}
				}
			}
			
			resultMap.put("roleList", resultList);
			success = true;
			message = "查询成功";
		} catch(IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
   	}
	
	/**
	 * 根据角色ID查询角色信息
	 * @param roleId
	 * @return
	 */
	@RequestMapping("/queryRoleById")
	@ResponseBody
	public Map<String, Object> queryRoleById(String roleId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(roleId)) {
				throw new IllegalArgumentException("请提供角色ID");
			}
			
			SysroleInfoModel roleInfo = this.sysRoleInfoService.queryById(roleId);
			resultMap.put("roleInfo", roleInfo);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
		}
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	
	/**
	 * 根据角色ID查询角色拥有的权限信息
	 * @param roleId
	 * @return
	 */
	@RequestMapping("/queryRoleAuthList")
	@ResponseBody
	public Map<String, Object> queryRoleAuthList(HttpServletRequest request, String roleId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		String crewId = "0";
		try {
			//用户权限信息
			List<RoleAuthDto> appAuthList = new ArrayList<RoleAuthDto>();	//app端权限
			List<RoleAuthDto> pcAuthList = new ArrayList<RoleAuthDto>();	//pc端权限
			
			//系统中所有的权限信息
			List<AuthorityModel> authList = this.authorityService.queryAuthByPlatformWithoutAdmin(null);
			//用户已经有的权限信息
			List<RoleAuthMapModel> ownAuthList = this.roleAuthMapService.queryByRoleId(roleId, crewId);
			
			List<RoleAuthDto> roleAuthList = this.loopAuthList(authList, new ArrayList<RoleAuthDto>(), ownAuthList);
			
			for (RoleAuthDto roleAuth : roleAuthList) {
				int authPlatform = roleAuth.getAuthPlantform();
				if (authPlatform == AuthorityPlatform.Mobile.getValue()) {
					appAuthList.add(roleAuth);
				}
				if (authPlatform == AuthorityPlatform.PC.getValue()) {
					pcAuthList.add(roleAuth);
				}
				if (authPlatform == AuthorityPlatform.Common.getValue()) {
					appAuthList.add(roleAuth);
					pcAuthList.add(roleAuth);
				}
			}
			
			resultMap.put("appAuthList", appAuthList);
			resultMap.put("pcAuthList", pcAuthList);
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 遍历权限元素
	 * 按照父子管理整理出格式
	 * 
	 * 此处遍历的原则是从最底层权限一层一层向上剥离
	 * @param authList	系统中所有权限信息
	 * @param roleAuthList	封装后的用户权限信息
	 * @param ownAuthList	用户拥有的权限信息
	 * @return
	 */
	private List<RoleAuthDto> loopAuthList (List<AuthorityModel> authList, List<RoleAuthDto> roleAuthList, List<RoleAuthMapModel> ownAuthList) {
		List<AuthorityModel> parentAuthList = new ArrayList<AuthorityModel>();
		List<AuthorityModel> childAuthList = new ArrayList<AuthorityModel>();	//当前层中的子权限
		for (AuthorityModel fauth : authList) {
			String fauthId = fauth.getAuthId();
			String fparentId = fauth.getParentId();
			
			boolean isParent = false;
			boolean ischild = false;
			for (AuthorityModel sauth : authList) {
				String sauthId = sauth.getAuthId();
				String sparentId = sauth.getParentId();
				
				if (fauthId.equals(sparentId)) {
					isParent = true;
				}
				
				if (fparentId.equals(sauthId)) {
					ischild = true;
				}
			}
			
			//此处parentAuthList和childAuthList数据在多层权限结构中必然后交集
			//fauth为父权限中的一个
			if (isParent) {
				parentAuthList.add(fauth);
			}
			//fauth为子权限中的一个，如果fauth既不是父权限，也不是子权限，则说明，fauth为系统权限中最顶层的叶子节点权限
			if (ischild || (!isParent && !ischild)) {
				childAuthList.add(fauth);
			}
		}
		
		//childAuthList中存在parentAuthList不存在的权限就是当前循环中的叶子权限
		List<AuthorityModel> lastAuthList = new ArrayList<AuthorityModel>();
		for (AuthorityModel cauth : childAuthList) {
			boolean exist = false;
			for (AuthorityModel pauth : parentAuthList) {
				if (cauth.getAuthId().equals(pauth.getAuthId())) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				lastAuthList.add(cauth);
			}
		}
		
		List<RoleAuthDto> myRoleAuthDtoList = new ArrayList<RoleAuthDto>();
		
		/*
		 * 为最后的结果字段赋值
		 * lastAuthList表示当前循环中的叶子权限
		 * 但是相对于上一层传过来的userAuthList，lastAuthList中有些数据为userAuthList中数据的父权限
		 * 因此，此处对比出lastAuthList中每个权限的子权限，然后为响应字段赋值
		 * 
		 * 如果数据在userAuthList存在而lastAuthList中不存在，则说明此数据层级为当前循环的叶子权限
		 */
		for (AuthorityModel lauth : lastAuthList) {
			String authId = lauth.getAuthId();
			
			List<RoleAuthDto> subRoleAuthDto = new ArrayList<RoleAuthDto>();
			for (RoleAuthDto roleAuth : roleAuthList) {
				String uparentId = roleAuth.getParentId();
				
				if (uparentId.equals(authId)) {
					subRoleAuthDto.add(roleAuth);
				}
			}
			
			RoleAuthDto roleAuthDto = new RoleAuthDto();
			roleAuthDto.setAuthId(authId);
			roleAuthDto.setParentId(lauth.getParentId());
			roleAuthDto.setAuthName(lauth.getAuthName());
			roleAuthDto.setSequence(lauth.getSequence());
			roleAuthDto.setSubAuthList(subRoleAuthDto);
			roleAuthDto.setDifferInRAndW(lauth.getDifferInRAndW());
			roleAuthDto.setAuthPlantform(lauth.getAuthPlantform());
			
			boolean hasAuth = false;
			for (RoleAuthMapModel roleAuthMap : ownAuthList) {
				if (authId.equals(roleAuthMap.getAuthId())) {
					roleAuthDto.setHasAuth(true);
					roleAuthDto.setReadonly(roleAuthMap.getReadonly());
					
					hasAuth = true;
				}
			}
			
			if (!hasAuth) {
				roleAuthDto.setHasAuth(false);
				roleAuthDto.setReadonly(true);
			}
			
			myRoleAuthDtoList.add(roleAuthDto);
		}
		
		for (RoleAuthDto roleAuth : roleAuthList) {
			boolean exists = false;
			for (AuthorityModel lauth : lastAuthList) {
				if (roleAuth.getParentId().equals(lauth.getAuthId())) {
					exists = true;
				}
			}
			
			if (!exists) {
				myRoleAuthDtoList.add(roleAuth);
			}
		}
		
		Collections.sort(myRoleAuthDtoList, new Comparator<RoleAuthDto>() {
			@Override
			public int compare(RoleAuthDto o1, RoleAuthDto o2) {
				return o1.getSequence() - o2.getSequence();
			}
		});
		
		//如果全是叶子权限了，说明已经遍历到最顶层了
		if (parentAuthList.size() > 0) {
			//把最底层的权限剥掉后，继续遍历，一直到只剩下最顶层的为止
			authList.removeAll(lastAuthList);
			myRoleAuthDtoList = this.loopAuthList(authList, myRoleAuthDtoList, ownAuthList);
		}
		
		return myRoleAuthDtoList;
	}
	
	/**
	 * 保存角色的权限信息
	 * @param request
	 * @param aimRoleId	角色ID
	 * @param operateType	操作类型 1：新增  2：修改  3：删除
	 * @param authId 权限ID
	 * @param readonly	是否只读
	 * @return
	 */
	@RequestMapping("/saveRoleAuthInfo")
	@ResponseBody
	public Map<String, Object> saveRoleAuthInfo(HttpServletRequest request, String aimRoleId, int operateType, String authId, Boolean readonly) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		String crewId = "0";
		try {
			
			if (StringUtils.isBlank(authId)) {
				throw new IllegalArgumentException("请选择需要操作的权限");
			}
			
			if (readonly == null) {
				readonly = false;
			}
			
			RoleAuthMapModel roleAuthMap = this.roleAuthMapService.queryByRoleAuthId(crewId, aimRoleId, authId);
			
			//删除
			if (operateType == 3) {
				//需要查询出所有的子权限，然后把角色和所有子权限的关联关系删掉
				List<RoleAuthMapModel> roleAuthMapList = this.roleAuthMapService.queryByRoleAuthIdWithSubAuth(crewId, aimRoleId, authId);
				for (RoleAuthMapModel map : roleAuthMapList) {
					this.roleAuthMapService.deleteById(crewId, aimRoleId, authId, map.getMapId());
				}
			}
			
			//新增
			if (operateType != 3 && roleAuthMap == null) {
				roleAuthMap = new RoleAuthMapModel();
				roleAuthMap.setAuthId(authId);
				roleAuthMap.setCrewId(crewId);
				roleAuthMap.setMapId(UUIDUtils.getId());
				roleAuthMap.setReadonly(readonly);
				roleAuthMap.setRoleId(aimRoleId);
				this.roleAuthMapService.addOne(crewId, aimRoleId, roleAuthMap);
			}
			
			//修改
			if (operateType != 3 && roleAuthMap != null) {
				roleAuthMap.setAuthId(authId);
				roleAuthMap.setCrewId(crewId);
				roleAuthMap.setReadonly(readonly);
				roleAuthMap.setRoleId(aimRoleId);
				this.roleAuthMapService.updateOne(roleAuthMap);
			}
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 保存角色的信息
	 * @param request
	 * @param roleId	角色ID
	 * @param roleName	角色名称
	 * @param roleDesc	角色描述
	 * @param canBeEvaluate	是否可被评价
	 * @param parentId	父权限ID
	 * @return
	 */
	@RequestMapping("/saveRoleInfo")
	@ResponseBody
	public Map<String, Object> saveRoleInfo(HttpServletRequest request, String roleId, 
			String roleName, String roleDesc, Boolean canBeEvaluate, String parentId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(roleName)) {
				throw new IllegalArgumentException("角色名称不能为空");
			}
			
			
			if (!StringUtils.isBlank(roleId)) {
				SysroleInfoModel roleInfo = this.sysRoleInfoService.queryById(roleId);
				roleInfo.setRoleName(roleName);
				roleInfo.setRoleDesc(roleDesc);
				this.sysRoleInfoService.updateRole(roleInfo);
			}
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
		}
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
}
