package com.xiaotu.makeplays.shoot.controller;

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

import com.xiaotu.makeplays.shoot.model.ShootGroupModel;
import com.xiaotu.makeplays.shoot.service.ShootGroupService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 剧组分组信息
 * @author xuchangjian 2016年8月4日下午3:41:35
 */
@Controller
@RequestMapping("/shootGroupManager")
public class ShootGroupController extends BaseController{
	
	Logger logger = LoggerFactory.getLogger(ShootGroupController.class);

	@Autowired
	private ShootGroupService shootGroupService;
	
	/**
	 * 根据剧组ID查询分组信息
	 * @return
	 */
	@RequestMapping("/groupListJson")
	public @ResponseBody Map<String, Object> listGroupByPlayId(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String crewId = getCrewId(request);
		
		try {
			List<ShootGroupModel> shootGroupList = this.shootGroupService.queryManyByCrewId(crewId);
			resultMap.put("shootGroupList", shootGroupList);
		} catch (Exception e) {
			logger.error("未知异常，查询分组信息失败", e);
		}
		
		return resultMap;
	}
	
	
	/**
	 * 保存分组信息
	 * @param request
	 * @param groupName 分组名称
	 * @param crewId 剧组id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveGroup")
	public Map<String, Object> saveGroup(HttpServletRequest request, String groupName,String crewId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success  = true;
		
		//获取剧组id
		if(StringUtils.isBlank(crewId)) {
			crewId = this.getCrewId(request);
		}
		
		try {
			if (StringUtils.isBlank(groupName)) {
				throw new IllegalArgumentException("当前分组名称不能为空!");
			}
			
			//定义分组对象并保存
			ShootGroupModel group = new ShootGroupModel();
			group.setGroupName(groupName);
			group.setCrewId(crewId);
			group.setGroupId(UUIDUtils.getId());
			shootGroupService.saveShootGroup(group);
			
			message = "保存成功!";
			resultMap.put("group", group);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message  ="未知异常,保存失败!";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
}
