package com.xiaotu.makeplays.cache.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.cache.model.CacheModel;
import com.xiaotu.makeplays.cache.service.CacheService;
import com.xiaotu.makeplays.shoot.controller.ScheduleController;
import com.xiaotu.makeplays.utils.BaseController;

/**
 * @类名：CacheController.java
 * @作者：李晓平
 * @时间：2017年6月27日 下午2:11:45
 * @描述：信息记录controller
 */
@Controller
@RequestMapping("/cacheManager")
public class CacheController extends BaseController{
	Logger logger = LoggerFactory.getLogger(ScheduleController.class);
	
	@Autowired
	private CacheService cacheService;
	
	/**
	 * 查询记录内容
	 * @param request
	 * @param type 记录类型，参考CacheType
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryCacheInfo")
	public Map<String, Object> queryCacheInfo(HttpServletRequest request, Integer type){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if(type == null) {
				throw new IllegalArgumentException("请输入查询类型");
			}
			
			String crewId = this.getCrewId(request);
			String userId = this.getLoginUserId(request);
			
			CacheModel cacheInfo = this.cacheService.queryCacheInfo(crewId, userId, type);
			resultMap.put("result", cacheInfo);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误, 查询记录内容失败!";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);		

		return resultMap;
	}
	
	/**
	 * 保存记录内容
	 * @param request
	 * @param content 记录内容
	 * @param type 记录类型，参考CacheType
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveCacheInfo")
	public Map<String, Object> saveCacheInfo(HttpServletRequest request, String content, Integer type){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if(type == null) {
				throw new IllegalArgumentException("请输入查询类型");
			}
			String crewId = this.getCrewId(request);
			String userId = this.getLoginUserId(request);
			
			this.cacheService.saveCacheInfo(crewId, userId, content, type);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误, 保存记录内容失败!";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);		

		return resultMap;
	}
}
