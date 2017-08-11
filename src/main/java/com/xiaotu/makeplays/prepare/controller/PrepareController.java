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
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.prepare.model.PrepareArteffectLocationModel;
import com.xiaotu.makeplays.prepare.model.PrepareCrewPeopleModel;
import com.xiaotu.makeplays.prepare.model.PrepareExtensionModel;
import com.xiaotu.makeplays.prepare.model.PrepareOperateModel;
import com.xiaotu.makeplays.prepare.model.PrepareRoleModel;
import com.xiaotu.makeplays.prepare.model.PrepareWorkModel;
import com.xiaotu.makeplays.prepare.service.PrepareService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;

/**
 * @ClassName PrepareController
 * @Description 筹备期
 * @author Administrator
 * @Date 2017年2月10日 上午10:42:12
 * @version 1.0.0
 */
@Controller
@RequestMapping("prepareController")
public class PrepareController extends BaseController{
	
	Logger logger = LoggerFactory.getLogger(PrepareController.class);
	
	@Autowired
	private PrepareService prepareService;

	
	@RequestMapping("toPrepareMainPage")
	public ModelAndView toPreparePage(HttpServletRequest httpServletRequest){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/prepare/prepareMain");
		return mv;
	}
	@RequestMapping("toPreparePageScript")
	public ModelAndView toPrepareScriptPage(HttpServletRequest httpServletRequest){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/prepare/prepareScript");
		return mv;
	}
	@RequestMapping("toPreparePageCrewPeople")
	public ModelAndView toPrepareCrewPeoplePage(HttpServletRequest httpServletRequest){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/prepare/prepareCrewPeople");
		return mv;
	}
	
	@RequestMapping("toPreparePageArteffect")
	public ModelAndView toPrepareArteffectPage(HttpServletRequest httpServletRequest){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/prepare/prepareArteffect");
		return mv;
	}
	
	@RequestMapping("toPreparePageExtension")
	public ModelAndView toPrepareExtensionPage(HttpServletRequest httpServletRequest){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/prepare/prepareExtension");
		return mv;
	}
	
	@RequestMapping("toPreparePageWork")
	public ModelAndView toPrepareWorkPage(HttpServletRequest httpServletRequest){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/prepare/prepareWork");
		return mv;
	}
	@RequestMapping("toPreparePageOperate")
	public ModelAndView toPrepareOperatePage(HttpServletRequest httpServletRequest){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/prepare/prepareOperate");
		return mv;
	}
	@RequestMapping("toPreparePageRole")
	public ModelAndView toPreparePageRole(HttpServletRequest httpServletRequest){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/prepare/prepareRole");
		return mv;
	}
	@RequestMapping("toPreparePageSceneView")
	public ModelAndView toPreparePageSceneView(HttpServletRequest httpServletRequest){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/prepare/prepareSceneView");
		return mv;
	}
	/**
	 * @Description 批量保存、修改筹备期 美术视觉角色信息
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("saveOrUpdatePrepareArteffectRoleInfo")
	public Object saveOrUpdatePrepareArteffectRoleInfo(HttpServletRequest httpServletRequest,String role,String modelling,String confirmDate,
			String status,String mark,String reviewer,String id){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		String crewId = this.getCrewId(httpServletRequest);
    		id = prepareService.saveOrUpdatePrepareArteffectRoleInfo(role,modelling,confirmDate,status,mark,reviewer,id,crewId);
    		resultMap.put("id", id);
    		this.sysLogService.saveSysLog(httpServletRequest, "保存筹备期角色信息", Constants.TERMINAL_PC, PrepareRoleModel.TABLE_NAME, null, 2);
		} catch (Exception e) {
			message = "未知错误，批量保存、修改角色信息失败";
			success = false;
			
			logger.error(message, e);
    		this.sysLogService.saveSysLog(httpServletRequest, "保存筹备期角色信息失败：" + e.getMessage(), Constants.TERMINAL_PC, PrepareRoleModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	
	/**
	 * @Description 查询筹备期  角色信息
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryPrepareRoleInfo")
	public Object queryPrepareRoleInfo(HttpServletRequest httpServletRequest){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		String crewId = this.getCrewId(httpServletRequest);
    		String root = prepareService.queryPrepareRoleInfo(crewId);
    		resultMap.put("result", root);
		} catch (Exception e) {
			message = "未知错误，查询筹备期角色信息失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	
	
	
	
	/**
	 * @Description 批量保存、修改筹备期 剧组人员信息
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("saveOrUpdatePrepareCrewPeopleInfo")
	public Object saveOrUpdatePrepareCrewPeopleInfo(HttpServletRequest httpServletRequest,String id,String groupName,
			String duties,String name,String phone,String reviewer,String confirmDate,String arrivalTime,String payment,String parentId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		String crewId= this.getCrewId(httpServletRequest);
    		id = prepareService.saveOrUpdatePrepareCrewPeopleInfo(id,groupName,duties,name,phone,reviewer,confirmDate,arrivalTime,payment,parentId,crewId);
    		
    		this.sysLogService.saveSysLog(httpServletRequest, "保存筹备期剧组人员信息", Constants.TERMINAL_PC, PrepareCrewPeopleModel.TABLE_NAME, null, 2);
    		resultMap.put("id", id);
    		resultMap.put("parentId", parentId);
    	} catch (Exception e) {
			message = "未知错误，保存、修改剧组人员信息失败";
			success = false;
			
			logger.error(message, e);
    		this.sysLogService.saveSysLog(httpServletRequest, "保存筹备期剧组人员信息失败：" + e.getMessage(), Constants.TERMINAL_PC, PrepareCrewPeopleModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	
	/**
	 * @Description 查询筹备期  剧组人员信息
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryPrepareCrewPeopleInfo")
	public Object queryPrepareCrewPeopleInfo(HttpServletRequest httpServletRequest){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		String crewId = this.getCrewId(httpServletRequest);
    		String root = prepareService.queryPrepareCrewPeopleInfo(crewId);
    		resultMap.put("result", root);
		} catch (Exception e) {
			message = "未知错误，查询筹备期剧组人员信息失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	
	
	
	
	
	
	/**
	 * @Description 查询筹备期  美术视觉角色信息
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryPrepareArteffectRoleInfo")
	public Object queryPrepareArteffectRoleInfo(HttpServletRequest httpServletRequest){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		String crewId = this.getCrewId(httpServletRequest);
    		List<Map<String, Object>> list = prepareService.queryPrepareArteffectRoleInfo(crewId);
    		resultMap.put("result", list);
		} catch (Exception e) {
			message = "未知错误，查询美术视觉角色信息失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	/**
	 * @Description 根据id删除一条美术视觉  角色信息
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("delPrepareArteffectRoleInfo")
	public Object delPrepareArteffectRoleInfo(HttpServletRequest httpServletRequest,String id){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		if(StringUtils.isBlank(id)){
    			throw new IllegalArgumentException("参数异常");
    		}
    		prepareService.delPrepareArteffectRoleInfo(id);
		} catch (Exception e) {
			message = "未知错误，删除美术视觉角色信息失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	
	/**
	 * @Description 根据id删除一条美术视觉  场景信息
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("delPrepareArteffectLocationInfo")
	public Object delPrepareArteffectLocationInfo(HttpServletRequest httpServletRequest,String id){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		if(StringUtils.isBlank(id)){
    			throw new IllegalArgumentException("参数异常");
    		}
    		prepareService.delPrepareArteffectLocationInfo(id);
		} catch (Exception e) {
			message = "未知错误，删除美术视觉场景信息失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	
	
	
	/**
	 * @Description 批量保存、修改筹备期 美术视觉场景信息
	 * @param httpServletRequest
	 * @param location  场景
	 * @param designSketch  效果图
	 * @param designSketchDate  效果图出图日期
	 * @param workDraw  施工图
	 * @param workDrawDate   施工图出图日期
	 * @param scenery    置景  
	 * @param sceneryDate    置景日期
	 * @param reviewer   审核人
	 * @param opinion   意见
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("saveOrUpdatePrepareArteffectLocationInfo")
	public Object saveOrUpdatePrepareArteffectLocationInfo(HttpServletRequest httpServletRequest,
			String location,String designSketch,String designSketchDate,String workDraw,
			String workDrawDate,String scenery,String sceneryDate,String reviewer,String opinion,String id){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		String crewId = this.getCrewId(httpServletRequest);
    		id = prepareService.saveOrUpdatePrepareArteffectLocationInfo(location,designSketch,designSketchDate,workDraw,
    				workDrawDate,scenery,sceneryDate,reviewer,opinion,id,crewId);
    		resultMap.put("id", id);
    		this.sysLogService.saveSysLog(httpServletRequest, "保存筹备期美术视觉场景信息", Constants.TERMINAL_PC, PrepareArteffectLocationModel.TABLE_NAME, null, 2);
		} catch (Exception e) {
			message = "未知错误，保存、修改美术视觉场景信息失败";
			success = false;
			
			logger.error(message, e);
    		this.sysLogService.saveSysLog(httpServletRequest, "保存筹备期美术视觉场景信息失败：" + e.getMessage(), Constants.TERMINAL_PC, PrepareArteffectLocationModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	
	/**
	 * @Description 查询筹备期  美术视觉场景信息
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryPrepareArteffectLocationInfo")
	public Object queryPrepareArteffectLocationInfo(HttpServletRequest httpServletRequest){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		String crewId = this.getCrewId(httpServletRequest);
    		List<Map<String, Object>> list = prepareService.queryPrepareArteffectLocationInfo(crewId);
    		resultMap.put("result", list);
		} catch (Exception e) {
			message = "未知错误，查询美术视觉场景信息失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	
	/**
	 * @Description 保存、修改筹备期 宣传进度
	 * @param httpServletRequest
	 * @param id id
	 * @param type  类型
	 * @param material 素材列表
	 * @param personLiable 负责人
	 * @param reviewer  审核人
	 * @return
	 */
	@ResponseBody
	@RequestMapping("saveOrUpdatePrepareExtensionInfo")
	public Object saveOrUpdatePrepareExtensionInfo(HttpServletRequest httpServletRequest,String id,String type,
			String material,String personLiable,String reviewer){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		String crewId = this.getCrewId(httpServletRequest);
    		id = prepareService.saveOrUpdatePrepareExtensionInfo(id, type,material, personLiable, reviewer,crewId);
    		resultMap.put("id", id);
    		this.sysLogService.saveSysLog(httpServletRequest, "保存筹备期宣传进度", Constants.TERMINAL_PC, PrepareExtensionModel.TABLE_NAME, null, 2);
		} catch (Exception e) {
			message = "未知错误，保存、修改宣传进度失败";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(httpServletRequest, "保存筹备期宣传进度失败：" + e.getMessage(), Constants.TERMINAL_PC, PrepareExtensionModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	
	/**
	 * @Description 查询筹备期  宣传进度
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryPrepareExtensionInfo")
	public Object queryPrepareExtensionInfo(HttpServletRequest httpServletRequest){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		String crewId = this.getCrewId(httpServletRequest);
    		List<Map<String, Object>> list = prepareService.queryPrepareExtensionInfo(crewId);
    		resultMap.put("result", list);
		} catch (Exception e) {
			message = "未知错误，查询宣传进度失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	
	/**
	 * @Description 批量保存、修改筹备期 办公筹备
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("saveOrUpdatePrepareWorkInfo")
	public Object saveOrUpdatePrepareWorkInfo(HttpServletRequest httpServletRequest,String id,String type,String purpose,String schedule,String personLiable,
			String parentId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		
    		String crewId = this.getCrewId(httpServletRequest);
    		id = prepareService.saveOrUpdatePrepareWorkInfo(id,type,purpose,schedule,personLiable,parentId,crewId);
    		
    		this.sysLogService.saveSysLog(httpServletRequest, "保存筹备期办公筹备", Constants.TERMINAL_PC, PrepareWorkModel.TABLE_NAME, null, 2);
    		resultMap.put("id", id);
    		resultMap.put("parentId", parentId);
    	} catch (Exception e) {
			message = "未知错误，保存、修改办公筹备失败";
			success = false;
			
			logger.error(message, e);
    		this.sysLogService.saveSysLog(httpServletRequest, "保存筹备期办公筹备失败：" + e.getMessage(), Constants.TERMINAL_PC, PrepareWorkModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	
	/**
	 * @Description 查询筹备期  办公筹备
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryPrepareWorkInfo")
	public Object queryPrepareWorkInfo(HttpServletRequest httpServletRequest){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		String crewId = this.getCrewId(httpServletRequest);
    		String list = prepareService.queryPrepareWorkInfo(crewId);
    		resultMap.put("result", list);
		} catch (Exception e) {
			message = "未知错误，查询办公筹备失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	/**
	 * @Description 删除筹备期  办公筹备
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("delPrepareWorkInfo")
	public Object delPrepareWorkInfo(HttpServletRequest httpServletRequest,String id){
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
    		
    		prepareService.delPrepareWorkInfo(idArray);
		} catch (Exception e) {
			message = "未知错误，删除办公筹备失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	
	
	/**
	 * @Description 查询筹备期  商务运营信息
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryPrepareOperateInfo")
	public Object queryPrepareOperateInfo(HttpServletRequest httpServletRequest){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		String crewId = this.getCrewId(httpServletRequest);
    		String  back = prepareService.queryPrepareOperateInfo(crewId);
    		resultMap.put("result", back);
		} catch (Exception e) {
			message = "未知错误，查询商务运营失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	/**
	 * @Description 保存 、修改筹备运营信息
	 * @param id
	 * @param operateType 合作种类
	 * @param operateBrand 品牌
	 * @param operateMode 合作方式
	 * @param operateCost 合作费用
	 * @param contactName 联系人名称
	 * @param phoneNumber 联系电话
	 * @param mark 备注
	 * @param personLiable 负责人
	 * @param parentId 父id
	 * @param crewId
	 */
	@ResponseBody
	@RequestMapping("saveOrUpdatePrepareOperateInfo")
	public Object saveOrUpdatePrepareOperateInfo(HttpServletRequest httpServletRequest,String id,String operateType,String operateBrand,String operateMode,String operateCost,String contactName,
			String phoneNumber,String mark,String personLiable,String parentId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		String crewId = this.getCrewId(httpServletRequest);
    		id= prepareService.saveOrUpdatePrepareOperateInfo(id,operateType,operateBrand,operateMode,operateCost,contactName,
    				phoneNumber,mark,personLiable,parentId,crewId);
    		resultMap.put("id", id);
    		
    		this.sysLogService.saveSysLog(httpServletRequest, "保存筹备运营信息", Constants.TERMINAL_PC, PrepareOperateModel.TABLE_NAME, null, 2);
		} catch (Exception e) {
			message = "未知错误，保存、修改商务运营失败";
			success = false;
			
			logger.error(message, e);
    		this.sysLogService.saveSysLog(httpServletRequest, "保存筹备运营信息失败：" + e.getMessage(), Constants.TERMINAL_PC, PrepareOperateModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	
	
	/**
	 * @Description 删除筹备期  商务运营信息
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("delPrepareOperateInfo")
	public Object delPrepareOperateInfo(HttpServletRequest httpServletRequest,String id){
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
    		prepareService.delPrepareOperateInfo(idArray);
		} catch (Exception e) {
			message = "未知错误，删除商务运营信息失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	/**
	 * @Description 根据id删除一条宣传进度信息
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("delPrepareExtensionInfo")
	public Object delPrepareExtensionInfo(HttpServletRequest httpServletRequest,String id){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		if(StringUtils.isBlank(id)){
    			throw new IllegalArgumentException("参数异常");
    		}
    		prepareService.delPrepareExtensionInfo(id);
		} catch (Exception e) {
			message = "未知错误，删除宣传进度信息失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	/**
	 * @Description保存或者修改筹备期角色信息
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("saveOrUpdatePrepareRoleInfo")
	public Object saveOrUpdatePrepareRoleInfo(HttpServletRequest httpServletRequest,String id,String role,
			String actor,String schedule,String content,String mark,String parentId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		if(StringUtils.isBlank(id)){
    			throw new IllegalArgumentException("参数异常");
    		}
    		String crewId = this.getCrewId(httpServletRequest);
    		
    		id = prepareService.saveOrUpdatePrepareRoleInfo(id,role,actor,schedule,content,mark,parentId,crewId);
    		resultMap.put("id", id);
    		resultMap.put("parentId", parentId);    		

    		this.sysLogService.saveSysLog(httpServletRequest, "保存筹备期角色信息", Constants.TERMINAL_PC, PrepareRoleModel.TABLE_NAME, null, 2);
		} catch (Exception e) {
			message = "未知错误，保存、修改筹备期选角进度信息失败";
			success = false;
			
			logger.error(message, e);
    		this.sysLogService.saveSysLog(httpServletRequest, "保存筹备期角色信息失败：" + e.getMessage(), Constants.TERMINAL_PC, PrepareRoleModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	/**
	 * @Description 删除筹备选角信息
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("delPrepareRoleInfo")
	public Object delPrepareRoleInfo(HttpServletRequest httpServletRequest,String id){
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
    		prepareService.delPrepareRoleInfo(idArray);
		} catch (Exception e) {
			message = "未知错误，保存、修改筹备期选角进度信息失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
	
	/**
	 * @Description 删除筹备剧组人员信息
	 * @param httpServletRequest
	 * @return
	 */
	@ResponseBody
	@RequestMapping("delPrepareCrewPeopleInfo")
	public Object delPrepareCrewPeopleInfo(HttpServletRequest httpServletRequest,String id){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		if(StringUtils.isBlank(id)){
    			throw new IllegalArgumentException("参数异常");
    		}
    		if(StringUtils.isBlank(id)){
    			throw new IllegalArgumentException("参数异常");
    		}
    		String[] idArray = id.split(",");
    		if(idArray == null || idArray.length == 0){
    			throw new IllegalArgumentException("参数异常");
    		}
    		prepareService.delPrepareCrewPeopleInfo(idArray);
		} catch (Exception e) {
			message = "未知错误，删除剧组人员信息失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
	}
}
