package com.xiaotu.makeplays.authority.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.authority.model.AuthorityModel;
import com.xiaotu.makeplays.authority.model.CrewAuthMapModel;
import com.xiaotu.makeplays.authority.model.RoleAuthMapModel;
import com.xiaotu.makeplays.authority.service.AuthorityService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.utils.UUIDUtils;

@Controller
@RequestMapping("/authorityManager")
public class AuthorityController extends BaseController{
	
	Logger logger = LoggerFactory.getLogger(AuthorityController.class);

	@Autowired
	private AuthorityService authorityService;
	
	
	/**
	 * 跳转到权限管理页面
	 * @return
	 */
	@RequestMapping("/toAuthorityListPage")
	public ModelAndView toAuthorityListPage(){
		ModelAndView mv = new ModelAndView("/user/authorityList");
		return mv;
	}
	
	/**
	 * 查询权限信息
	 * @param request
	 * @param type 权限类型, 2：pc,3:app
	 * @return
	 */
	@RequestMapping("/queryAuthorityList")
	@ResponseBody
	public Map<String, Object> queryAuthorityList(HttpServletRequest request, int type) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try{
			List<AuthorityModel> list = authorityService.queryAuthorityList(type);
			//查找根节点、叶子节点
			List<AuthorityModel> root = new ArrayList<AuthorityModel>();
			List<AuthorityModel> child = new ArrayList<AuthorityModel>();
			for (AuthorityModel am : list) {
				boolean ischild = false;
				boolean isparent = false;
				for (AuthorityModel aml : list) {
					if(am.getParentId().equals( aml.getAuthId())){
						ischild = true;
					}
					if(am.getAuthId().equals( aml.getParentId())){
						isparent = true;
					}
				}
				if(!ischild){
					root.add(am);
				}
				if(!isparent){
					child.add(am);
				}
			}
			
			//查找叶子节点		
			List<Map<String, Object>> li = new ArrayList<Map<String,Object>>();
			for (AuthorityModel am : list) {
				Map<String,Object> rowsMap=new HashMap<String,Object>();
	    		rowsMap.put("id", am.getAuthId());
	    		if(judgetroot(root,am)){
	    			
	    			rowsMap.put("iconCls", "icon-parent");
	    		}else if(judgetroot(child,am)){
	    			rowsMap.put("_parentId", am.getParentId());
	    			rowsMap.put("iconCls", "icon-final");
	    		}else{
	    			rowsMap.put("_parentId", am.getParentId());
	    			rowsMap.put("iconCls", "icon-child");
	    		}
	    		rowsMap.put("fid", am.getParentId());
	    		
	    		rowsMap.put("name", am.getAuthName());
	    		rowsMap.put("ifMenu", am.getIfMenu());
	    		rowsMap.put("authUrl", am.getAuthUrl());
	    		rowsMap.put("status", am.getStatus());
	    		rowsMap.put("sequence", am.getSequence());
	    		rowsMap.put("operDesc", am.getOperDesc());
	    		rowsMap.put("operType", am.getOperType());
	    		rowsMap.put("authPlantform", am.getAuthPlantform());
	    		rowsMap.put("authCode", am.getAuthCode());
	    		rowsMap.put("differInRAndW", am.getDifferInRAndW());
	    		rowsMap.put("defaultRorW", am.getDefaultRorW());
	    		
	    		li.add(rowsMap);
			}
			
			resultMap.put("rows", li);
			resultMap.put("total", li.size());
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询权限信息失败";
			logger.error(message);
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 查询所有权限及拥有该权限的用户数量
	 * @param request
	 * @param type 权限类型, 2：pc,3:app
	 * @return 只返回已分配的权限(通过与tab_role_auth_map关联)，并且会把admin和客服特有的权限排除掉
	 */
	@RequestMapping("/queryAuthAndUserNumWithoutAdmin")
	@ResponseBody
	public Map<String, Object> queryAuthAndUserNum(HttpServletRequest request, int type) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try{
			String crewId = this.getCrewId(request);
			List<Map<String, Object>> list = authorityService.queryAuthAndUserNumWithoutAdmin(type, crewId);
			//查找根节点、叶子节点
			List<Map<String, Object>> root = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> child = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> am : list) {
				boolean ischild = false;
				boolean isparent = false;
				for (Map<String, Object> aml : list) {
					if(am.get("parentId").equals( aml.get("authId"))){
						ischild = true;
					}
					if(am.get("authId").equals( aml.get("parentId"))){
						isparent = true;
					}
				}
				if(!ischild){
					root.add(am);
				}
				if(!isparent){
					child.add(am);
				}
			}
			//组装树节点
			for (Map<String, Object> am : list) {
	    		if(judgetroot(root,am)){	    			
	    			am.put("iconCls", "icon-parent");
	    		}else if(judgetroot(child,am)){
	    			am.put("iconCls", "icon-final");
	    		}else{
	    			am.put("iconCls", "icon-child");
	    		}
			}
			
			resultMap.put("result", list);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询所有权限及拥有该权限的用户数量失败";
			logger.error(message);
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 查询所有权限及拥有该权限的用户数量
	 * 格式化为bootstrap-treeview树形结构
	 * @param request
	 * @param type 权限类型, 2：pc,3:app
	 * @return 只返回已分配的权限(通过与tab_role_auth_map关联)，并且会把admin和客服特有的权限排除掉
	 */
	@RequestMapping("/queryAuthAndUserNumWithoutAdminFormat")
	@ResponseBody
	public Map<String, Object> queryAuthAndUserNumWithoutAdminFormat(HttpServletRequest request, Integer type) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try{
			if(type == null) {
				throw new IllegalArgumentException("请输入平台类型");
			}
			String crewId = this.getCrewId(request);
			List<Map<String, Object>> list = authorityService.queryAuthAndUserNumWithoutAdmin(type, crewId);
			List<Map<String, Object>> authList = loopAuthForNodeTree(list, new ArrayList<Map<String,Object>>());
			resultMap.put("result", authList);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询所有权限及拥有该权限的用户数量失败";
			logger.error(message);
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 递归权限树
	 * 封装成id, text:name-num, nodes的格式
	 * @param subjectList
	 * @param resultList
	 * @return
	 */
	private List<Map<String, Object>> loopAuthForNodeTree(List<Map<String, Object>> authList, List<Map<String, Object>> resultList) {
		List<Map<String, Object>> myAuthMapList = new ArrayList<Map<String, Object>>();
		
		/*
		 * 首先过滤出纯粹子节点科目
		 */
		List<Map<String, Object>> parentAuthList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> childAuthList = new ArrayList<Map<String, Object>>();
		
		for (Map<String, Object> fauth : authList) {
			String fid = (String) fauth.get("authId");
			String fparentId = (String) fauth.get("parentId");

			boolean isParent = false;
			boolean isChild = false;
			for (Map<String, Object> cauth : authList) {
				String cid = (String) cauth.get("authId");
				String cparentId = (String) cauth.get("parentId");
				
				if (fid.equals(cparentId)) {
					isParent = true;
				}
				if (fparentId.equals(cid)) {
					isChild = true;
				}
			}
			
			//双层循环遍历权限列表，区分其中哪些权限是别人的子权限，哪些权限是别人的父权限
			//因为数据嵌套多层，过滤出的这两类数据必然会有重合的地方，但是childAuthList中有而parentAuthList没有的数据必然是叶子节点
			if (isParent) {
				parentAuthList.add(fauth);
			}
			if (isChild || (!isParent && !isChild)) {
				childAuthList.add(fauth);
			}
		}
		
		//childAuthList中有而parentAuthList没有的数据必然是叶子节点
		List<Map<String, Object>> leafAuthList = new ArrayList<Map<String, Object>>();
		
		for (Map<String, Object> cauth : childAuthList) {
			String cid = (String) cauth.get("authId");
			boolean exist = false;
			for (Map<String, Object> fauth : parentAuthList) {
				String fid = (String) fauth.get("authId");
				if (cid.equals(fid)) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				leafAuthList.add(cauth);
			}
		}
		
		
		/*
		 * 为最后的结果字段赋值
		 * leafAuthList表示当前循环中的叶子权限
		 * 但是相对于上一层传过来的resultList，leafAuthList中有些数据为resultList中数据的父权限
		 * 因此，此处对比出leafAuthList中每个权限的子权限，然后为相应字段赋值
		 * 
		 * 如果数据在resultList存在且在leafAuthList中找不到任何父权限，则说明此数据层级也为当前循环的叶子权限
		 */
		for (Map<String, Object> auth : leafAuthList) {
			List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
			
			for (Map<String, Object> authMap : resultList) {
				String parentId = (String) authMap.get("parentId");
				
				if (parentId.equals((String) auth.get("authId"))) {
					children.add(authMap);
				}
			}
			
			//为子权限排序
			Collections.sort(children, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					int o1sequence = (Integer) o1.get("sequence");
					int o2sequence = (Integer) o2.get("sequence");
	        		return o1sequence - o2sequence;
				}
			});
			
			Map<String, Object> myAuthMap = new HashMap<String, Object>();
			myAuthMap.put("authId", auth.get("authId"));
			myAuthMap.put("parentId", auth.get("parentId"));
			myAuthMap.put("userNum", auth.get("userNum") );
			myAuthMap.put("text", auth.get("authName") + "-" + auth.get("userNum"));
			myAuthMap.put("sequence", auth.get("sequence") );
			if (children.size() > 0) {
				myAuthMap.put("nodes", children);
			}
			
			
			myAuthMapList.add(myAuthMap);
		}
		for (Map<String, Object> authMap : resultList) {
			boolean exist = false;
			for (Map<String, Object> auth : leafAuthList) {
				String parentId = (String) authMap.get("parentId");
				if (parentId.equals((String) auth.get("authId"))) {
					exist = true;
				}
			}
			
			if (!exist) {
				myAuthMapList.add(authMap);
			}
		}
		
		if (parentAuthList.size() > 0) {
			authList.removeAll(leafAuthList);
			myAuthMapList = this.loopAuthForNodeTree(authList, myAuthMapList);
		}
		
		//排序
		Collections.sort(myAuthMapList, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				int o1sequence = (Integer) o1.get("sequence");
				int o2sequence = (Integer) o2.get("sequence");
        		return o1sequence - o2sequence;
			}
		});
		return myAuthMapList;
	}
	
	/**
	 * 查询单个权限信息
	 * @param authId
	 * @return
	 */
	@RequestMapping("/queryOneAuthority")
	@ResponseBody
	public Map<String, Object> queryOneAuthority(String authId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;		
		try{
			resultMap.put("result", this.authorityService.queryAuthById(authId));
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询权限信息失败";
			logger.error(message);
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	
	/**
	 * 验证操作编码是否唯一
	 * @param authCode
	 * @param authId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/validateAuthCode")
	public Map<String,Object> validateAuthCode(String authCode, String authId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try{
			if(StringUtil.isNotBlank(authCode)) {
				if(this.authorityService.validateAuthCode(authCode, authId)) {
					throw new IllegalArgumentException("操作编码已存在");
				}
			}
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			resultMap.put("flag", "1");
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，验证操作编码失败";
			logger.error(message, e);
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 添加/修改权限信息
	 * @param request
	 * @param authority
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveAuthority")
	public Map<String,Object> saveAuthority(HttpServletRequest request, AuthorityModel authority, Boolean isForAllCrew){	
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try{
			String logDesc = null;
			Integer operType = null;
			if(StringUtil.isBlank(authority.getAuthId())){
				authority.setAuthId(UUIDUtils.getId());
				authority.setSequence(authorityService.queryAuthorityMaxSeq(authority.getParentId()) + 1);
				authorityService.addAuthority(authority, isForAllCrew);
				logDesc = "添加权限信息";
				operType = 1;
			}else{
				authorityService.updateAuthority(authority);
				logDesc = "修改权限信息";
				operType = 2;
			}
			message = "保存成功！";
			
			this.sysLogService.saveSysLog(request, logDesc, Constants.TERMINAL_PC, AuthorityModel.TABLE_NAME, authority.getAuthId(), operType);
		} catch (Exception e) {
			success = false;
			message = "未知异常，保存权限信息失败";
			logger.error(message, e);
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 删除权限
	 * @param request
	 * @param authId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteAuthority")
	public Map<String,Object> deleteAuthority(HttpServletRequest request, String authId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try{
			//判断权限是否已被使用
			/*String result = "";
			if (authorityService.isRoleAuthUsed(authId)) {
				if (authorityService.isUserAuthUsed(authId)) {
					result = "角色权限表、用户权限表";
				} else {
					result = "角色权限表";
				}					
			} else if (authorityService.isUserAuthUsed(authId)) {
				result = "用户权限表";
			}
			if(StringUtil.isNotBlank(result)) {
				throw new IllegalArgumentException("该权限已被" + result + "使用，不能删除！");
			}*/
			if(authorityService.isHasChidAuth(authId)) {
				throw new IllegalArgumentException("该权限存在子权限，请先删除子权限。");
			}
			if(authorityService.isRoleAuthUsed(authId)) {
				throw new IllegalArgumentException("该权限已被角色使用，不能删除！");
			}
			//删除权限
			authorityService.deleteAuthority(authId);
			message = "删除成功！";
			
			this.sysLogService.saveSysLog(request, "删除权限", Constants.TERMINAL_PC, AuthorityModel.TABLE_NAME, authId, 3);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，删除权限失败";
			logger.error(message, e);
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 更新权限顺序
	 * @param request
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateAuthoritySequence")
	public Map<String,Object> updateAuthoritySequence(HttpServletRequest request, String ids){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;		
		try{
			//更新权限顺序
			this.authorityService.updateAuthoritySequence(ids);
			message = "更新权限顺序成功！";
			
			this.sysLogService.saveSysLog(request, "更新权限顺序", Constants.TERMINAL_PC, AuthorityModel.TABLE_NAME, null,2);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，更新权限顺序失败";
			logger.error(message, e);
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}	
	
	/**
	 * 根据某个权限获取所有角色及角色对应此权限的状态
	 * @param request
	 * @param authId
	 * @param parentId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryAllRoleByAuthId")
	public Map<String,Object> queryAllRoleByAuthId(HttpServletRequest request, String authId, String parentId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;		
		try{
			List<Map<String,Object>> resultList = null;
			if(parentId.equals("0") || parentId.equals("1")) {//根节点
				resultList = this.authorityService.queryAllRoleByAuthId(authId);
			} else {//子节点
				resultList = this.authorityService.queryAllRoleByAuthId(authId, parentId);
			}				
			if(resultList == null){
				return null;
			}
			List<Map<String,Object>> rootList = new ArrayList<Map<String,Object>>();
			
			for (Map<String, Object> map : resultList) {
				boolean flag = false;
				for (Map<String, Object> one : resultList) {
					if(map.get("parentId").equals(one.get("roleId"))){
						flag = true;
					}
				}
				if(!flag){
					rootList.add(map);
				}
			}
			
			List<Map<String,Object>> childList = null;
			for (Map<String, Object> map : rootList) {
				childList = new ArrayList<Map<String,Object>>();
				for (Map<String, Object> one : resultList) {
					if(map.get("roleId").equals(one.get("parentId"))){
						childList.add(one);
					}
				}
				map.put("childList", childList);
			}
			resultMap.put("result", rootList);
//			this.sysLogService.saveSysLog(request, "获取所有角色及角色对应此权限的状态", Constants.TERMINAL_PC, AuthorityModel.TABLE_NAME, authId, 0);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，根据某个权限获取所有角色及角色对应此权限的状态失败";
			logger.error(message, e);
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 更新角色权限关联关系
	 * @param request
	 * @param authId
	 * @param roles
	 * @param isDelete 是否删除用户权限关联信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveAuthRoleMap")
	public Map<String, Object> saveAuthRoleMap(HttpServletRequest request,
			String authId, String roles, boolean isDelete) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;		
		try{
			StringBuffer sb = new StringBuffer();
			String str[] = roles.split(",");
			for (int i = 0; i < str.length; i++) {
				sb.append(str[i].split("-")[0] + ",");
			}
			//判断该权限的子权限是否有角色使用
			if(this.authorityService.judgeAuthorityChildren(authId, sb.substring(0, sb.length()-1))){
				throw new IllegalArgumentException("该权限的修改角色已有子权限使用，请先修改子权限的角色。");
			}
			this.authorityService.updateRoleAuth(authId, roles, isDelete);
			
			this.sysLogService.saveSysLog(request, "批量更新角色权限关联关系(" + roles.split(",").length + ")", Constants.TERMINAL_PC, RoleAuthMapModel.TABLE_NAME, authId,2);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，更新角色权限关联关系失败";
			logger.error(message, e);
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 判断是否为根节点
	 * @param root
	 * @param am
	 * @return
	 */
	private boolean judgetroot(List<AuthorityModel> root,AuthorityModel am){
		for (AuthorityModel authorityModel : root) {
			if(authorityModel.equals(am)){
				return true;
			}
		}
		return false;
	}
	/**
	 * 判断是否为根节点
	 * @param root
	 * @param am
	 * @return
	 */
	private boolean judgetroot(List<Map<String, Object>> root, Map<String, Object> am){
		for (Map<String, Object> authorityModel : root) {
			if(authorityModel.equals(am)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 根据某个权限获取所有剧组及剧组对应此权限的状态
	 * @param request
	 * @param authId
	 * @param parentId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryAllCrewByAuthId")
	public Map<String,Object> queryAllCrewByAuthId(HttpServletRequest request, String authId, String parentId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;		
		try{
			List<Map<String,Object>> resultList = null;
			//查询该权限角色分布，如果只有admin和客服才有，不能赋给剧组
			boolean isAuthUsedByCommonRole = authorityService.isAuthUsedByCommonRole(authId);
			resultMap.put("isAuthUsedByCommonRole", isAuthUsedByCommonRole);
			if(parentId.equals("0") || parentId.equals("1")) {//根节点
				resultList = this.authorityService.queryAllCrewByAuthId(authId);
			} else {//子节点
				resultList = this.authorityService.queryAllCrewByAuthId(authId, parentId);
			}
			resultMap.put("result", resultList);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，根据某个权限获取所有剧组及剧组对应此权限的状态失败";
			logger.error(message, e);
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 更新剧组权限关联关系
	 * @param request
	 * @param authId
	 * @param crews
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveAuthCrewMap")
	public Map<String, Object> saveAuthCrewMap(HttpServletRequest request,
			String authId, String crews) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;		
		try{
			StringBuffer sb = new StringBuffer();
			String str[] = crews.split(",");
			for (int i = 0; i < str.length; i++) {
				sb.append(str[i].split("-")[0] + ",");
			}
			//判断该权限的子权限是否已赋给剧组
			if(this.authorityService.judgeAuthChildIsUsedByCrew(authId, sb.substring(0, sb.length()-1))){
				throw new IllegalArgumentException("该权限的修改剧组已有子权限使用，请先修改子权限的剧组。");
			}
			this.authorityService.updateCrewAuth(authId, crews);
			
			this.sysLogService.saveSysLog(request, "批量更新剧组权限关联关系(" + crews.split(",").length + ")", Constants.TERMINAL_PC, CrewAuthMapModel.TABLE_NAME, authId,2);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，更新剧组权限关联关系失败";
			logger.error(message, e);
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
}
