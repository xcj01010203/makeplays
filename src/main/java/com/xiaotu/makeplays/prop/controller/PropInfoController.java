package com.xiaotu.makeplays.prop.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.model.constants.CrewType;
import com.xiaotu.makeplays.goods.model.GoodsInfoModel;
import com.xiaotu.makeplays.goods.model.constants.GoodsType;
import com.xiaotu.makeplays.goods.service.GoodsInfoService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.ExcelUtils;

/**
 * 服化道管理操作的controlle
 * @author wanrenyi 2017年3月22日下午4:20:02
 */
@Controller
@RequestMapping("/propManager")
public class PropInfoController extends BaseController{

	Logger logger = LoggerFactory.getLogger(GoodsInfoModel.class);
	private static int terminal = Constants.TERMINAL_PC;
	private static Map<String, String> CREW_PROPS_MAP = new LinkedHashMap<String, String>();//需要导出的联系人字段
    static{
    	CREW_PROPS_MAP.put("服化道", "propsName");
    	CREW_PROPS_MAP.put("服化道类型",  "propsType");
    	CREW_PROPS_MAP.put("首次出现",  "firstUse");
    	CREW_PROPS_MAP.put("场数",  "allViewNo");
    	CREW_PROPS_MAP.put("库存数量",  "stock");
    	CREW_PROPS_MAP.put("备注",  "remark");
    }
	
	@Autowired
	private GoodsInfoService goodsInfoService;
	/**
	 * 跳转到服化道列表页面
	 * @param request
	 * @return
	 */
	@RequestMapping("/toPropInfoList")
	public ModelAndView toPropInfoList(HttpServletRequest request) {
		ModelAndView mv  = new ModelAndView("/prop/propInfoList");
		return mv;
	}
	/**
	 * @Description  根据id 名称查询服化道信息/判断服化道名称是否重名
	 * @param propsId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryPropsInfoById")
	public Object queryPropsInfoById(HttpServletRequest request,String propsName,String propsId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		String crewId = this.getCrewId(request);
    		if(StringUtils.isBlank(propsName) && StringUtils.isBlank(propsId)){
    			throw new IllegalArgumentException("请填写服化道名称或选择要查看的服化道");
    		}
    		
    		//定义查询条件
    		Map<String, Object> condition = new HashMap<String, Object>();
    		if (StringUtils.isNotBlank(propsName)) {
    			condition.put("goodsName", propsName);
			}
    		if (StringUtils.isNotBlank(propsId)) {
				condition.put("id", propsId);
			}
    		condition.put("crewId", crewId);
    		
    		//查询数据
    		List<GoodsInfoModel> propInfoList = goodsInfoService.queryGoodsListByCondition(condition);
    		resultMap.put("propInfoList", propInfoList);
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			
			logger.error(message, ie);
			success = false;
		} catch (Exception e) {
			message = "未知异常";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
		return resultMap;
	}
	
	
	/**
	 * @Description  查询服化道信息
	 * @param request
	 * @param propName  服化道名称
	 * @param type   服化道类型
	 * @param start  开始场数
	 * @param end    结束场数
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryPropsInfoList")
	public Object queryPropsInfoList(HttpServletRequest request,String propName,Integer type, String start,String end, Integer sortType){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
    		
    		//剧组id
    		String crewId = crewInfo.getCrewId();
    		//剧组类型
    		Integer crewType = crewInfo.getCrewType();
    		
    		List<Map<String, Object>> propInfoList = goodsInfoService.queryGoodsListByView(crewId, propName, type, start, end, sortType);
    		if (null != propInfoList && propInfoList.size()>0) {
				for (Map<String, Object> infoMap : propInfoList) {
					//取出集场号
					String firstUse = (String) infoMap.get("firstUse");
					//根据不同的剧组类型返回不同集场号字段
					//如果为网剧、网大 只返回场次号
					if (crewType == CrewType.Movie.getValue() || crewType == CrewType.InternetMovie.getValue()) {
						//将首次出场拆分，只返回场次号
						String sub_firstUse = firstUse.substring(firstUse.lastIndexOf("-")+1, firstUse.length());
						infoMap.put("firstUse", sub_firstUse);
					}
				}
			}
    		
    		resultMap.put("propInfoList", propInfoList);
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常,查询服化道列表失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
		return resultMap;
	}
	/**
	 * @Description 2.合并服化道信息 
	 * @param request
	 * @param idArray 服化道id
	 * @param propName 服化道名称
	 * @param type  服化道类型
	 * @param remark  备注
	 * @param stock 库存量
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateCombinePropsInfo")
	public Object updateCombinePropsInfo(HttpServletRequest request,String idArray,String propName,Integer type, String remark){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		if(StringUtils.isBlank(propName)){
    			throw new IllegalArgumentException("请输入服化道名称");
    		}
    		if(StringUtils.isBlank(idArray)){
    			throw new IllegalArgumentException("请选择服化道信息");
    		}
    		String ids[] = idArray.split(",");
    		if(ids == null || ids.length < 2){
    			throw new IllegalArgumentException("请选择至少两条服化道信息");
    		}
    		if( type == null ){
    			throw new IllegalArgumentException("请选择服化道类型");
    		}
    		String crewId = this.getCrewId(request);
    		UserInfoModel userInfoModel = this.getSessionUserInfo(request);
    		
    		//校验保存的服化道名称是否重复
    		Map<String, Object> condition = new HashMap<String, Object>();
    		condition.put("crewId",crewId);
    		condition.put("goodsName", propName);
    		List<GoodsInfoModel> propInfoList = goodsInfoService.queryGoodsListByCondition(condition);
    		for (GoodsInfoModel map : propInfoList) {
				String propsId = map.getId();
				if (!idArray.contains(propsId)) {
					throw new IllegalArgumentException("已存在该服化道，请修改后再保存");
				}
			}
    		
    		goodsInfoService.updateCombinePropsInfo(ids, propName, type, remark, crewId, userInfoModel.getUserId(),userInfoModel.getUserName());
			
    		this.sysLogService.saveSysLog(request, "合并服化道信息", terminal, GoodsInfoModel.TABLE_NAME, idArray, SysLogOperType.INSERT.getValue());
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，合并服化道信息失败";
			success = false;
			logger.error(message, e);
    		this.sysLogService.saveSysLog(request, "合并服化道信息失败：" + e.getMessage(), terminal, GoodsInfoModel.TABLE_NAME, idArray, SysLogOperType.ERROR.getValue());
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
		return resultMap;
	}
	/**
	 * @Description 3.批量设置服化道类型 
	 * @param request
	 * @param propsIdArray  服化道id集合
	 * @param type   服化道类型
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updatePropsType")
	public Object updatePropsType(HttpServletRequest request,String propsIdArray,Integer type){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	long start = System.currentTimeMillis();
    	try {
    		if(type == null){
    			throw new IllegalArgumentException();
    		}
    		if(StringUtils.isBlank(propsIdArray)){
    			throw new IllegalArgumentException("请选择服化道信息");
    		}
    		String ids[] = propsIdArray.split(",");
    		if(ids == null || ids.length == 0){
    			throw new IllegalArgumentException("请选择服化道信息");
    		}
    		
    		goodsInfoService.updateGoodsType(ids, type);
    		long end = System.currentTimeMillis();
    		System.out.println(end-start);
    		
    		this.sysLogService.saveSysLog(request, "批量设置服化道类型", terminal, GoodsInfoModel.TABLE_NAME, propsIdArray, SysLogOperType.UPDATE.getValue());
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，批量设置服化道类型失败";
			success = false;
			logger.error(message, e);
    		this.sysLogService.saveSysLog(request, "批量设置服化道类型失败：" + e.getMessage(), terminal, GoodsInfoModel.TABLE_NAME, propsIdArray, SysLogOperType.ERROR.getValue());
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * @Description  4.删除服化道信息
	 * @param request
	 * @param propsIdArray  服化道id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/delPropsInfo")
	public Object delPropsInfo(HttpServletRequest request,String propsIdArray){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
			if(StringUtils.isBlank(propsIdArray)){
				throw new IllegalArgumentException("请选择需要删除的服化道信息");
			}
			String[] ids = propsIdArray.split(",");
			if(ids == null || ids.length == 0){
				throw new IllegalArgumentException("请选出需要删除的服化道信息");
			}
			//删除服化道信息
			goodsInfoService.delGoodsInfo(ids);
    		this.sysLogService.saveSysLog(request, "删除服化道信息", terminal, GoodsInfoModel.TABLE_NAME, propsIdArray, SysLogOperType.DELETE.getValue());
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，删除服化道信息失败";
			success = false;
    		this.sysLogService.saveSysLog(request, "删除服化道信息失败：" + e.getMessage(), terminal, GoodsInfoModel.TABLE_NAME, propsIdArray, SysLogOperType.ERROR.getValue());
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * @Description  导出服化道信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exportPropsInfo")
	public Object exportPropsInfo(HttpServletRequest request,HttpServletResponse response, Integer sortType){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
    		CrewInfoModel crewInfoModel = this.getSessionCrewInfo(request);
    		Map<String, Object> condition = new HashMap<String, Object>();
    		condition.put("crewId", crewInfoModel.getCrewId());
    		//获取剧组类型
    		Integer crewType = crewInfoModel.getCrewType();
    		
    		List<Map<String, Object>> propInfoList = goodsInfoService.queryGoodsListByView(crewInfoModel.getCrewId(), null, null, null, null, sortType);
    		if(propInfoList!=null && propInfoList.size() >0){
    			for(Map<String, Object> temp:propInfoList){
    				Integer propType = temp.get("propsType")!=null?Integer.valueOf(temp.get("propsType").toString()):9;
    				if(GoodsType.CommonProps.getValue() == propType){
    					temp.put("propsType", "普通道具");
    				}else if(GoodsType.SpecialProps.getValue() == propType){
    					temp.put("propsType", "特殊道具");
    				}else if(GoodsType.Clothes.getValue() == propType){
    					temp.put("propsType", "服装");
    				}else if(GoodsType.Makeup.getValue() == propType){
    					temp.put("propsType", "化妆");
    				}
    				
    				//处理首次出现集场号，若为电影/网大类型时，不显示集次号
					//取出集场号
					String firstUse = (String) temp.get("firstUse");
					//根据不同的剧组类型返回不同集场号字段
					//如果为网剧、网大 只返回场次号
					if (crewType == CrewType.Movie.getValue() || crewType == CrewType.InternetMovie.getValue()) {
						//将首次出场拆分，只返回场次号
						String sub_firstUse = firstUse.substring(firstUse.lastIndexOf("-")+1, firstUse.length());
						temp.put("firstUse", sub_firstUse);
					}
    			}
    			ExcelUtils.exportPropsInfoForExcel(propInfoList,response,CREW_PROPS_MAP,crewInfoModel.getCrewName());

        		this.sysLogService.saveSysLog(request, "导出服化道信息", terminal, GoodsInfoModel.TABLE_NAME, null, SysLogOperType.EXPORT.getValue());
    		}else{
    			throw new IllegalArgumentException("没有可导出的数据");
    		}
			
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常,导出服化道信息失败";
			success = false;
			logger.error(message, e);
    		this.sysLogService.saveSysLog(request, "导出服化道信息失败：" + e.getMessage(), terminal, GoodsInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * @Description 6.保存服化道信息
	 * @param request
	 * @param propName 服化道名称
	 * @param type  服化道类型
	 * @param remark  备注
	 * @param stock  库存数量
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/savePropsInfo")
	public Object savePropsInfo(HttpServletRequest request,String propsId,String propName,Integer type, String remark,Integer stock){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
			if(StringUtils.isBlank(propName)){
				throw new IllegalArgumentException("请填写服化道名称");
			}
			if(type == null){
				throw new IllegalArgumentException("请选择服化道类型");
			}
//			if(stock == null){
//				throw new IllegalArgumentException("请填写库存信息");
//			}
			UserInfoModel userInfoModel = this.getSessionUserInfo(request);
			String crewId = this.getCrewId(request);
			
			//根据服化道名称进行判重
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("crewId", crewId);
			conditionMap.put("goodsName", propName);
			List<GoodsInfoModel> list = goodsInfoService.queryGoodsListByCondition(conditionMap);
			if (list != null && list.size()>0) {
				for (GoodsInfoModel map : list) {
					if (StringUtils.isBlank(propsId) || !propsId.equals(map.getId())) {
						throw new IllegalArgumentException("该服化道已存在，请修改后在保存");
					}
				}
			}
			
			goodsInfoService.saveGoodsInfo(propsId, propName, type, remark, stock, crewId, userInfoModel.getUserId(),userInfoModel.getUserName());
    		
    		this.sysLogService.saveSysLog(request, "保存服化道信息", terminal, GoodsInfoModel.TABLE_NAME, null, SysLogOperType.INSERT.getValue());
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常,保存服化道信息失败";
			success = false;
			logger.error(message, e);
    		this.sysLogService.saveSysLog(request, "保存服化道信息失败：" + e.getMessage(), terminal, GoodsInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * @Description 7.查询服化道使用情况
	 * @param request 
	 * @param propId  服化道id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryPropsUseInfo")
	public Object queryPropsUseInfo(HttpServletRequest request,String propId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	try {
			if(StringUtils.isBlank(propId)){
				throw new IllegalArgumentException("请选择要查看的服化道");
			}
			
			String crewId = this.getCrewId(request);
			List<Map<String, Object>> propsUseInfoList = goodsInfoService.queryGoodsUseInfo(crewId, propId);
			
			resultMap.put("propsUseInfoList", propsUseInfoList);
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
		} catch (Exception e) {
			message = "未知异常";
			success = false;
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
		return resultMap;
	}
}
