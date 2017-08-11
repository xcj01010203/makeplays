package com.xiaotu.makeplays.prepare.controller;

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

import com.xiaotu.makeplays.prepare.service.PrepareScriptService;
import com.xiaotu.makeplays.utils.BaseController;

/**
 * @ClassName PrepareScriptController
 * @Description 筹备期 剧本信息
 * @author Administrator
 * @Date 2017年2月14日 上午9:23:22
 * @version 1.0.0
 */
@RequestMapping("prepareScriptController")
@Controller
public class PrepareScriptController extends BaseController{
	
	Logger logger = LoggerFactory.getLogger(PrepareController.class);
	
	@Autowired
	private PrepareScriptService prepareScriptService;
	
	
	/**
	 * @Description 查询剧本类型
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryScriptType")
	public Object queryScriptType(HttpServletRequest httpServletRequest){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		List<Map<String, Object>> list = prepareScriptService.queryScriptType();
    		resultMap.put("scriptTypeList", list);
		} catch (Exception e) {
			message = "未知错误，查询剧本类型失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	
	
	/**
	 * @Description 查询剧本类型
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryScriptTypeChecked")
	public Object queryScriptTypeChecked(HttpServletRequest httpServletRequest){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		String crewId = this.getCrewId(httpServletRequest);
    		List<Map<String, Object>> list = prepareScriptService.queryScriptTypeChecked(crewId);
    		resultMap.put("scriptTypeCheckedList", list);
		} catch (Exception e) {
			message = "未知错误，查询已选剧本类型失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	/**
	 * @Description 查询权重信息
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryWeightInfo")
	public Object queryWeightInfo(HttpServletRequest httpServletRequest){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		String crewId = this.getCrewId(httpServletRequest);
    		List<Map<String, Object>> list = prepareScriptService.queryWeightInfo(crewId);
    		resultMap.put("weightList", list);
		} catch (Exception e) {
			message = "未知错误，查询审核权重信息失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	
	
	/**
	 * @Description 删除权重信息
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("delWeightInfo")
	public Object delWeightInfo(HttpServletRequest httpServletRequest,String id){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		if(StringUtils.isBlank(id)){
    			throw new IllegalArgumentException("参数异常");
    		}
    		
    		prepareScriptService.delWeightInfo(id);
		} catch (Exception e) {
			message = "未知错误，删除权重信息失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	
	/**
	 * @Description 生成进度表信息
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("generateSchedule")
	public Object generateSchedule(HttpServletRequest httpServletRequest,String scriptTypeId,String weightInfo){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		String crewId =this.getCrewId(httpServletRequest);
    		if(StringUtils.isBlank(scriptTypeId)){
    			throw new IllegalArgumentException("剧本类型参数异常");
    		}
    		if(StringUtils.isBlank(weightInfo)){
    			throw new IllegalArgumentException("评审权重信息异常");
    		}
    		prepareScriptService.generateSchedule(crewId,scriptTypeId,weightInfo);
		}catch(IllegalArgumentException ie){
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，生成进度表信息失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	

	/**
	 * @Description 查询剧本进度信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryScriptScheduleInfo")
	public Object queryScriptScheduleInfo(HttpServletRequest httpServletRequest){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		String crewId = this.getCrewId(httpServletRequest);
    		String root = prepareScriptService.queryScriptScheduleInfo(crewId);
    		resultMap.put("result", root);
		} catch (Exception e) {
			message = "未知错误，查询进度表信息失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	
	/**
	 * @Description 保存一条剧本进度信息
	 * @param httpServletRequest
	 * @param parentId 父节点id
	 * @param id 
	 * @param scriptTypeId   剧本类型id
	 * @param edition  版本
	 * @param finishDate  交稿日期
	 * @param personLiable  负责人
	 * @param content 内容
	 * @param status 状态
	 * @param mark  备注
	 * @return
	 */
	@ResponseBody
	@RequestMapping("saveOrUpdateScriptInfo")
	public Object saveOrUpdateScriptInfo(HttpServletRequest httpServletRequest,String parentId,String id,String scriptTypeId,String edition,
			String finishDate,String personLiable,String content,String status,String mark,String weightInfoId,String score){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		String crewId = this.getCrewId(httpServletRequest);
    		id = prepareScriptService.saveOrUpdateScriptInfo(id,parentId,scriptTypeId,edition,finishDate,personLiable,content,status,mark,crewId,weightInfoId,score);
    		resultMap.put("id", id);
		} catch (Exception e) {
			message = "未知错误，修改剧本进度表信息失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	
	
	/**
	 * @Description 根据id删除一条剧本进度信息
	 * @param httpServletRequest
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("delScriptInfo")
	public Object delScriptInfo(HttpServletRequest httpServletRequest,String id){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		if(StringUtils.isBlank(id)){
    			throw new IllegalArgumentException("参数异常");
    		}
    		String[] idArray = id.split(",");
    		if(idArray == null || idArray.length == 0){
    			throw new IllegalArgumentException("参数异常");
    		}
    		prepareScriptService.delScriptInfo(idArray);
		} catch (Exception e) {
			message = "未知错误，删除剧本进度表信息失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	
	
}
