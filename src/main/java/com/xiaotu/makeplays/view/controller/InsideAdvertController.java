package com.xiaotu.makeplays.view.controller;

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

import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.view.model.InsideAdvertModel;
import com.xiaotu.makeplays.view.service.InsideAdvertService;
import com.xiaotu.makeplays.view.service.ViewAdvertMapService;

/**
 * 植入广告
 * @author xuchangjian
 */
@Controller
@RequestMapping("/insiteAdvertManager")
public class InsideAdvertController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(InsideAdvertController.class);
	
	@Autowired
	private InsideAdvertService insideAdvertService;
	
	@Autowired
	private ViewAdvertMapService viewAdvertMapService;
	
	/**
	 * 保存植入广告信息
	 * @param viewId 场景id
	 * @param advertName 广告的名称
	 * @param advertType 广告的类型
	 * @return
	 */
	@RequestMapping("/saveInsiteAdvert")
	@ResponseBody
	public  Map<String, Object> saveInsiteAdvert(HttpServletRequest request, String viewId, String advertName, String advertType) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String crewId = getCrewId(request);
		
		boolean success = true;
		String message = "";
		
		String idArrayStr = "";
		try {
			if (StringUtils.isBlank(advertName)) {
				throw new IllegalArgumentException("请输入广告名称");
			}
			
			if (StringUtils.isBlank(advertType)) {
				throw new IllegalArgumentException("请选择广告类型");
			}
			
			if (StringUtils.isBlank(viewId)) {
				throw new IllegalArgumentException("场景id不能为空!");
			}
			
			//保存广告信息
			String advertId = this.insideAdvertService.saveAdvertInfo(advertName, crewId);
			
			//保存广告和场景的关联关系
			if (!StringUtils.isBlank(advertId)) {
				this.viewAdvertMapService.addViewAdvertMapInfo(viewId, advertId, advertType, crewId);
			}
			message = "添加成功";
			idArrayStr = advertId;
			
			resultMap.put("advertId", advertId);
			this.sysLogService.saveSysLog(request, "保存植入广告信息", Constants.TERMINAL_PC, InsideAdvertModel.TABLE_NAME, idArrayStr,1);
		} catch(IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			
			logger.error(ie.getMessage(), ie);
		} catch (Exception e) {
			success = false;
			message = "未知错误，保存广告信息失败";
			
			logger.error("未知错误，保存广告信息失败", e);
			this.sysLogService.saveSysLog(request, "保存植入广告信息失败：" + e.getMessage(), Constants.TERMINAL_PC, InsideAdvertModel.TABLE_NAME, idArrayStr, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 根据id删除广告信息
	 * @param advertId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteAdvert")
	public Map<String, Object> deleteAdvert(String advertId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(advertId)) {
				throw new IllegalArgumentException("请选择要删除的广告!");
			}
			
			this.insideAdvertService.deleteAdvertById(advertId);
			message = "删除成功!";
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = e.getMessage();
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 更新广告名称
	 * @param request
	 * @param advertName
	 * @param advertId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateAdvertName")
	public Map<String, Object> updateAdvertName(HttpServletRequest request, String advertName, String advertId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(advertId)) {
				throw new IllegalArgumentException("请选择要修改的广告");
			}
			if (StringUtils.isBlank(advertName)) {
				throw new IllegalArgumentException("广告名称，不能为空");
			}
			
			this.insideAdvertService.updateAdvertNameByAdvertId(advertName, advertId);
			
			message = "更新成功";
			this.sysLogService.saveSysLog(request, "修改广告信息", Constants.TERMINAL_PC, InsideAdvertModel.TABLE_NAME, advertName,2);
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
		} catch (Exception e) {
			message = "未知错误，更新失败";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "修改广告信息失败：" + e.getMessage(), Constants.TERMINAL_PC, InsideAdvertModel.TABLE_NAME, advertName, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
}
