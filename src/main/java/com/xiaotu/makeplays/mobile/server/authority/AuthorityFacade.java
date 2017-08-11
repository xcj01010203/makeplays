package com.xiaotu.makeplays.mobile.server.authority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.authority.model.constants.AuthorityPlatform;
import com.xiaotu.makeplays.authority.service.AuthorityService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.mobile.server.common.BaseFacade;
import com.xiaotu.makeplays.mobile.server.role.ViewRoleFacade;

/**
 * @类名：AuthorityFacade.java
 * @作者：李晓平
 * @时间：2017年8月4日 下午4:53:17
 * @描述：权限相关接口
 */
@Controller
@RequestMapping("/interface/authorityFacade")
public class AuthorityFacade extends BaseFacade{
	
	Logger logger = LoggerFactory.getLogger(ViewRoleFacade.class);
	
	@Autowired
	private AuthorityService authorityService;
	
	/**
	 * 获取剧组权限信息
	 * @param crewId
	 * @param userId
	 * @param platForm 终端类型
	 * @return
	 */
	@RequestMapping("/obtainCrewAuthList")
	@ResponseBody
	public Object obtainCrewAuthList(String crewId, String userId, Integer platForm) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			if(platForm == null) {
				throw new IllegalArgumentException("请选择平台类型");
			}
			
			//剧组权限
			List<Map<String, Object>> list = authorityService.queryAuthAndUserNumWithoutAdmin(platForm, crewId);
			
			//循环组装父子关系
			List<Map<String, Object>> crewAuthList = this.loopAuthList(list, new ArrayList<Map<String,Object>>());			
			
			resultMap.put("crewAuthList", crewAuthList);
			
		} catch (IllegalArgumentException ie) {
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			throw new IllegalArgumentException("未知异常", e);
		}
		return resultMap;
	}
	
	
	/**
	 * 遍历权限元素
	 * 按照父子管理整理出格式
	 * 
	 * 此处遍历的原则是从最底层权限一层一层向上剥离
	 * @param authList	系统中所有权限信息
	 * @param userAuthList	封装后的用户权限信息
	 * @param ownAuthList	用户拥有的权限信息
	 * @return
	 */
	private List<Map<String, Object>> loopAuthList (List<Map<String, Object>> authList, List<Map<String, Object>> crewAuthList) {
		List<Map<String, Object>> parentAuthList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> childAuthList = new ArrayList<Map<String, Object>>();	//当前层中的子权限
		for (Map<String, Object> fauth : authList) {
			String fauthId = (String) fauth.get("authId");
			String fparentId = (String) fauth.get("parentId");
			
			boolean isParent = false;
			boolean ischild = false;
			for (Map<String, Object> sauth : authList) {
				String sauthId = (String) sauth.get("authId");
				String sparentId = (String) sauth.get("parentId");
				
				if (fauthId.equals(sparentId)) {
					isParent = true;
				}
				
				if (fparentId.equals(sauthId)) {
					ischild = true;
				}
			}
			
			//此处parentAuthList和childAuthList数据在多层权限结构中必然有交集
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
		List<Map<String, Object>> lastAuthList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> cauth : childAuthList) {
			boolean exist = false;
			for (Map<String, Object> pauth : parentAuthList) {
				if (((String) cauth.get("authId")).equals((String) pauth.get("authId"))) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				lastAuthList.add(cauth);
			}
		}
		
		List<Map<String, Object>> myCrewAuthList = new ArrayList<Map<String, Object>>();
		
		/*
		 * 为最后的结果字段赋值
		 * lastAuthList表示当前循环中的叶子权限
		 * 但是相对于上一层传过来的userAuthList，lastAuthList中有些数据为userAuthList中数据的父权限
		 * 因此，此处对比出lastAuthList中每个权限的子权限，然后为相应字段赋值
		 * 
		 * 如果数据在userAuthList存在且在lastAuthList中找不到父权限，则说明此数据层级为当前循环的叶子权限
		 */
		for (Map<String, Object> lauth : lastAuthList) {
			String authId = (String) lauth.get("authId");
			
			List<Map<String, Object>> subCrewAuthDto = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> crewAuth : crewAuthList) {
				String uparentId = (String) crewAuth.get("parentId");
				
				if (uparentId.equals(authId)) {
					subCrewAuthDto.add(crewAuth);
				}
			}
			
			lauth.put("subAuthList", subCrewAuthDto);
						
			myCrewAuthList.add(lauth);
		}
		
		for (Map<String, Object> crewAuth : crewAuthList) {
			boolean exists = false;
			for (Map<String, Object> lauth : lastAuthList) {
				if (((String) crewAuth.get("parentId")).equals((String) lauth.get("authId"))) {
					exists = true;
				}
			}
			
			if (!exists) {
				myCrewAuthList.add(crewAuth);
			}
		}
		
		Collections.sort(myCrewAuthList, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				return (Integer) o1.get("sequence") - (Integer) o2.get("sequence");
			}
		});
		
		//如果全是叶子权限了，说明已经遍历到最顶层了
		if (parentAuthList.size() > 0) {
			//把最底层的权限剥掉后，继续遍历，一直到只剩下最顶层的为止
			authList.removeAll(lastAuthList);
			myCrewAuthList = this.loopAuthList(authList, myCrewAuthList);
		}
		
		return myCrewAuthList;
	}
}