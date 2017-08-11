package com.xiaotu.makeplays.locationsearch.controller;

import java.text.CollationKey;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.locationsearch.model.SceneViewInfoModel;
import com.xiaotu.makeplays.locationsearch.service.SceneViewInfoService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.view.service.ViewInfoService;

/**
 * 勘景功能
 * 
 * @author Administrator
 *
 */
@Controller
@RequestMapping("sceneViewInfoController")
public class SceneViewInfoController extends BaseController{

	
	@Autowired
	private SceneViewInfoService sceneViewInfoService;
	@Autowired
	private ViewInfoService viewInfoService;
	
	/**
	 * 获取拍摄地点数据列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryShootLocationList")
	public Map<String, Object> queryShootLocationList(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		String crewId = getCrewId(request);
		
		try {
			List<SceneViewInfoModel> shootLocationList = this.sceneViewInfoService.queryShootAddressByCrewId(crewId);
			//对查询出的拍摄地点列表进行排序
			Collections.sort(shootLocationList, new Comparator<SceneViewInfoModel>() {
				@Override
				public int compare(SceneViewInfoModel o1, SceneViewInfoModel o2) {
					CollationKey key1 = Collator.getInstance().getCollationKey(o1.getVName().toLowerCase());// 要想不区分大小写进行比较用o1.toString().toLowerCase()
	        		CollationKey key2 = Collator.getInstance().getCollationKey(o2.getVName().toLowerCase());
	        		return key1.compareTo(key2);
				}
			});
			
			resultMap.put("shootLocationList", shootLocationList);
			message = "查询成功";
		} catch (Exception e) {
			message = "未知异常，查询拍摄地点失败";
			success = false;
			
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	@ResponseBody
	@RequestMapping("readData")
	public Object queryData(HttpServletRequest request){
		Map<String,Object> map = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			List<Map<String, Object>> list = sceneViewInfoService.queryData(crewId);
			map.put("list", list);
		}catch(IllegalArgumentException iException){
			success  = false;
			message = iException.getMessage();
		} catch (Exception e) {
			success  = false;
			message = "未知异常";
		}
		map.put("success", success);
		map.put("message", message);
		return map;
	}
	
	
	
//============================================================================================================================================	
	
	/**
	 * @Description  更新实景信息顺序
	 * @param request
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping("updateOrder")
	public Object updateOrder(HttpServletRequest request,String ids){
		Map<String, Object> back = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			if(StringUtils.isBlank(crewId)){
				throw new IllegalArgumentException("获取剧组信息失败");
			}
			if(StringUtils.isBlank(ids)){
				throw new IllegalArgumentException("实景id异常");
			}
			String[] idArray = ids.split(",");
			if(idArray==null || idArray.length == 0){
				throw new IllegalArgumentException("实景id异常");
			}
			
			sceneViewInfoService.updateOrder(idArray);
		}catch(IllegalArgumentException iException){
			success  = false;
			message = iException.getMessage();
		} catch (Exception e) {
			success  = false;
			message = "未知异常";
		}
		back.put("success", success);
		back.put("message", message);
		return back;
	}
	/**
	 * 查询当前剧组下的实景简要信息
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("querySceneViewBaseInfo")
	public Object querySceneViewBaseInfo(HttpServletRequest request){
		Map<String, Object> back = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			if(StringUtils.isBlank(crewId)){
				throw new IllegalArgumentException("获取剧组信息失败");
			}
			List<Map<String, Object>> list = sceneViewInfoService.querySceneViewBaseInfo(crewId);
			back.put("sceneViewBaseInfoList", list);
		}catch(IllegalArgumentException iException){
			success  = false;
			message = iException.getMessage();
		} catch (Exception e) {
			success  = false;
			message = "未知异常";
		}
		back.put("success", success);
		back.put("message", message);
		return back;
	}
	
	/**
	 * 查询当前剧组下的实景信息
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("querySceneViewInfo")
	public Object querySceneViewInfo(HttpServletRequest request){
		Map<String, Object> back = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			if(StringUtils.isBlank(crewId)){
				throw new IllegalArgumentException("获取剧组信息失败");
			}
			back = sceneViewInfoService.querySceneViewInfo(crewId);
		}catch(IllegalArgumentException iException){
			success  = false;
			message = iException.getMessage();
		} catch (Exception e) {
			success  = false;
			message = "未知异常";
		}
		back.put("success", success);
		back.put("message", message);
		return back;
	}
	
	/**
	 * 查询当前剧组下的实景信息
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("querySceneViewInfoById")
	public Object querySceneViewInfoById(String sceneViewId){
		Map<String, Object> back = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			if(StringUtils.isBlank(sceneViewId)){
				throw new IllegalArgumentException("获取实景id异常");
			}
			back = sceneViewInfoService.querySceneViewInfoById(sceneViewId);
		}catch(IllegalArgumentException iException){
			success  = false;
			message = iException.getMessage();
		} catch (Exception e) {
			success  = false;
			message = "未知异常";
		}
		back.put("success", success);
		back.put("message", message);
		return back;
	}
	
	
	
	/**
	 * @Description 判断当前剧组是否已经添加了同样的场景信息（根据剧组id，实景名称）判断
	 * @param request
	 * @param sceneViewName  实景名称
	 * @return
	 */
	@ResponseBody
	@RequestMapping("querySceneViewForHasSameName")
	public Object querySceneViewForHasSameName(HttpServletRequest request,String sceneViewName){
		Map<String, Object> back = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			if(StringUtils.isBlank(crewId)){
				throw new IllegalArgumentException("获取剧组信息失败");
			}
			
			List<SceneViewInfoModel> list = sceneViewInfoService.querySceneViewForHasSameName(crewId,sceneViewName);
			
			back.put("result", list);
		}catch(IllegalArgumentException iException){
			success  = false;
			message = iException.getMessage();
		} catch (Exception e) {
			success  = false;
			message = "未知异常";
		}
		back.put("success", success);
		back.put("message", message);
		return back;
	}
	
	/**
	 * 保存/修改实景信息（保存实景信息后返回id 再上传图片）
	 * @Description 
	 * @param request
	 * @param id  主键
	 * @param vName 实景名称
	 * @param vCity  所在城市
	 * @param vAddress  详细地址
	 * @param vLongitude  详细地址经度
	 * @param vLatitude  详细地址纬度
	 * @param distanceToHotel   距离住宿地距离
	 * @param holePeoples   容纳人数
	 * @param deviceSpace   设备空间
	 * @param isModifyView   是否改景   0：是   1： 否
	 * @param modifyViewCost  改景费用
	 * @param modifyViewTime  改景耗时
	 * @param hasProp 是否有道具陈设   0：是   1： 否
	 * @param propCost  道具陈设费用
	 * @param propTime  道具陈设时间
	 * @param enterViewDate  进景时间
	 * @param leaveViewDate  离景时间
	 * @param viewUseTime 使用时间
	 * @param contactNo  联系方式
	 * @param contactName  联系人姓名
	 * @param contactRole  联系人职务
	 * @param viewPrice   场景价格
	 * @param freeStartDate  空档期开始时间
	 * @param freeEndDate  空档期结束时间
	 * @param other  自定义字段
	 * @param remark 备注
	 * @return
	 * 
	 */
	@ResponseBody
	@RequestMapping("saveOrUpdateSceneViewInfo")
	public Object saveOrUpdateSceneViewInfo(HttpServletRequest request,String id,String vName,String vCity,String vAddress,
			String vLongitude,String vLatitude,String distanceToHotel,Integer holePeoples,String deviceSpace,Integer isModifyView,
			Double modifyViewCost,String modifyViewTime,int hasProp,Double propCost,String propTime,String enterViewDate,
			String leaveViewDate,String viewUseTime,String contactNo,String contactName,String contactRole,Double viewPrice,String freeStartDate,
			String freeEndDate, String remark){
		Map<String, Object> back = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			if(StringUtils.isBlank(crewId)){
				throw new IllegalArgumentException("获取剧组信息失败");
			}
			
			if(StringUtils.isBlank(vName)){
				throw new IllegalArgumentException("获取实景名称信息失败");
			}
			List<SceneViewInfoModel> list = sceneViewInfoService.querySceneViewForHasSameName(crewId,vName);
			if(StringUtils.isBlank(id)){
				if(list!=null&&list.size()>0){
					throw new IllegalArgumentException("该剧组下有相同的实景名称");
				}
			}else{
				if(list!=null&&list.size()>0){
					for(SceneViewInfoModel sceneViewInfoModelTemp :list){
						String idStr = sceneViewInfoModelTemp.getId();
						if(!id.equals(idStr)){
							throw new IllegalArgumentException("该剧组下有相同的实景名称");
						}
					}
				}
			}
			if(isModifyView == 0){
				if(StringUtils.isBlank(modifyViewTime)){
					throw new IllegalArgumentException("已经选择改景，但是改景相关信息异常");
				}
			}
			if("0".equals(hasProp)){
				if(StringUtils.isBlank(propTime)){
					throw new IllegalArgumentException("已经选择有道具，但是道具相关信息异常");
				}
			}
			Date enterViewDate_date = null;
			Date leaveViewDate_date = null;
			Date freeEndDate_date = null;
			Date freeStartDate_date = null;
			if(StringUtils.isNotBlank(enterViewDate)){
				enterViewDate_date = new SimpleDateFormat("yyyy-MM-dd").parse(enterViewDate);
			}
			if(StringUtils.isNotBlank(leaveViewDate)){
				leaveViewDate_date = new SimpleDateFormat("yyyy-MM-dd").parse(leaveViewDate);
			}
			if(StringUtils.isNotBlank(freeEndDate)){
				freeEndDate_date = new SimpleDateFormat("yyyy-MM-dd").parse(freeEndDate);
			}
			if(StringUtils.isNotBlank(freeStartDate)){
				freeStartDate_date = new SimpleDateFormat("yyyy-MM-dd").parse(freeStartDate);
			}
			
			String sceneViewInfoId = sceneViewInfoService.saveOrUpdateSceneViewInfo(id,vName,vCity,vAddress,vLongitude,vLatitude,distanceToHotel,holePeoples,deviceSpace,
					isModifyView,modifyViewCost,modifyViewTime,hasProp,propCost,propTime,enterViewDate,leaveViewDate,viewUseTime,contactNo,contactName,contactRole,viewPrice,
					freeStartDate,freeEndDate, remark,crewId);
			back.put("sceneViewInfoId", sceneViewInfoId);
			String logDesc = "";
			Integer operType = null;
			if(StringUtil.isBlank(id)) {
				logDesc = "添加勘景信息";
				operType = 1;
			} else {
				logDesc = "修改勘景信息";
				operType = 2;
			}
			this.sysLogService.saveSysLog(request, logDesc, Constants.TERMINAL_PC, SceneViewInfoModel.TABLE_NAME, sceneViewInfoId, operType);
		}catch(IllegalArgumentException iException){
			success  = false;
			message = iException.getMessage();
		} catch (Exception e) {
			success  = false;
			message = "未知异常";
			this.sysLogService.saveSysLog(request, "保存堪景信息失败：" + e.getMessage(), Constants.TERMINAL_PC, SceneViewInfoModel.TABLE_NAME, id, SysLogOperType.ERROR.getValue());
		}
		back.put("success", success);
		back.put("message", message);
		return back;
	}
	
	/**
	 * 根据实景信息id删除实景信息
	 *     删除实景信息的同时必须清除  附件包的信息和附件信息
	 * 
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("delSceneViewInfo")
	public Object delSceneViewInfo(HttpServletRequest request,String id){
		Map<String, Object> back = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			if(StringUtils.isBlank(crewId)){
				throw new IllegalArgumentException("获取剧组信息失败");
			}
			
			sceneViewInfoService.delSceneViewInfo(id);

			this.sysLogService.saveSysLog(request, "删除勘景信息", Constants.TERMINAL_PC, SceneViewInfoModel.TABLE_NAME, id, 3);
		}catch(IllegalArgumentException iException){
			success  = false;
			message = iException.getMessage();
		} catch (Exception e) {
			success  = false;
			message = "未知异常";
			this.sysLogService.saveSysLog(request, "删除勘景信息失败：" + e.getMessage(), Constants.TERMINAL_PC, SceneViewInfoModel.TABLE_NAME, id, SysLogOperType.ERROR.getValue());
		}
		back.put("success", success);
		back.put("message", message);
		return back;
	}
	
	
	
	
	/**
	 * 保存实景-主场景对照信息
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("saveSceneViewViewInfoMap")
	public Object saveSceneViewViewInfoMap(HttpServletRequest request,String sceneviewId,String locationId){
		Map<String, Object> back = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			if(StringUtils.isBlank(crewId)){
				throw new IllegalArgumentException("获取剧组信息失败");
			}
			if(StringUtils.isBlank(sceneviewId)){
				throw new IllegalArgumentException("实景信息id获取失败");
			}
			if(StringUtils.isBlank(locationId)){
				throw new IllegalArgumentException("请选择主场景信息");
			}
			
			sceneViewInfoService.saveSceneViewViewInfoMap(sceneviewId,locationId,crewId);
			
			this.sysLogService.saveSysLog(request, "保存关联场景信息", Constants.TERMINAL_PC, SceneViewInfoModel.TABLE_NAME, sceneviewId + "," + locationId, 1);
		}catch(IllegalArgumentException iException){
			success  = false;
			message = iException.getMessage();
		} catch (Exception e) {
			success  = false;
			message = "未知异常";
			this.sysLogService.saveSysLog(request, "保存关联场景信息失败：" + e.getMessage(), Constants.TERMINAL_PC, SceneViewInfoModel.TABLE_NAME, sceneviewId + "," + locationId, SysLogOperType.ERROR.getValue());
		}
		back.put("success", success);
		back.put("message", message);
		return back;
	}
	
	
	/**
	 * 根据实景信息id查询   当前实景已经配置的集场信息
	 * 
	 * @param id SceneViewInfoModel.id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryHasCheckViewInfoForSceneView")
	public Map<String, Object> queryHasCheckViewInfoForSceneView(HttpServletRequest request,String id){
		Map<String, Object> back = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			if(StringUtils.isBlank(crewId)){
				throw new IllegalArgumentException("获取剧组信息失败");
			}
			if(StringUtils.isBlank(id)){
				throw new IllegalArgumentException("实景信息id获取失败");
			}
			
			back = sceneViewInfoService.queryHasCheckOrAlternativeViewInfoForSceneView(true,crewId,id);
		}catch(IllegalArgumentException iException){
			success  = false;
			message = iException.getMessage();
		} catch (Exception e) {
			success  = false;
			message = "未知异常";
		}
		back.put("success", success);
		back.put("message", message);
		return back;
	}
	
	/**
	 * 查询当前剧组  可选为备选 （实景-场景）的场景信息
	 * @param crewid 剧组id
	 * @return
	 */
	
	@ResponseBody
	@RequestMapping("queryAlternativeViewInfo")
	public Map<String, Object> queryAlternativeViewInfo(HttpServletRequest request,String location){
		Map<String, Object> back = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			if(StringUtils.isBlank(crewId)){
				throw new IllegalArgumentException("获取剧组信息失败");
			}
			back = sceneViewInfoService.queryHasCheckOrAlternativeViewInfoForSceneView(false,crewId,location);
		}catch(IllegalArgumentException iException){
			success  = false;
			message = iException.getMessage();
		} catch (Exception e) {
			success  = false;
			message = "未知异常";
		}
		back.put("success", success);
		back.put("message", message);
		return back;
	}
	
	/**
	 * 
	 * 查询根据剧组id查询主场景列表
	 * ViewInfoService .queryMainSceneName
	 * 
	 * 
	 */
	
	/**
	 * 查询当前剧组  可选为备选 （实景-场景）的场景信息
	 * @param crewid 剧组id
	 * @return
	 */
	
	@ResponseBody
	@RequestMapping("queryMainSceneName")
	public Map<String, Object> queryMainSceneName(HttpServletRequest request,String mainSceneName){
		Map<String, Object> back = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			if(StringUtils.isBlank(crewId)){
				throw new IllegalArgumentException("获取剧组信息失败");
			}
			List<Map<String, Object>> mainSceneNameList = viewInfoService.queryMainSceneName(crewId,mainSceneName);
			back.put("mainSceneNameList", mainSceneNameList);
		}catch(IllegalArgumentException iException){
			success  = false;
			message = iException.getMessage();
		} catch (Exception e) {
			success  = false;
			message = "未知异常";
		}
		back.put("success", success);
		back.put("message", message);
		return back;
	}
	
	/**
	 * @Description  删除已经配置的主场景信息
	 * @param sceneViewInfoId 实景id
	 * @param locationId    主场景id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("delSceneViewMapInfo")
	public Object delSceneViewMapInfo(HttpServletRequest request, String sceneViewInfoId,String locationId){
		Map<String, Object> back = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			if(StringUtils.isBlank(locationId)){
				throw new IllegalArgumentException("主场景id异常");
			}
			String[]  locationIdArray = locationId.split("##");
			if(locationIdArray!=null&&locationIdArray.length!=0){
				String crewId = this.getCrewId(request);
				sceneViewInfoService.delSceneViewMapInfo(locationIdArray,crewId);
			}
			
			this.sysLogService.saveSysLog(request, "删除关联场景信息", Constants.TERMINAL_PC, SceneViewInfoModel.TABLE_NAME, sceneViewInfoId + "," + locationId, 3);
		}catch(IllegalArgumentException iException){
			success  = false;
			message = iException.getMessage();
		} catch (Exception e) {
			success  = false;
			message = "未知异常";
			this.sysLogService.saveSysLog(request, "删除关联场景信息失败：" + e.getMessage(), Constants.TERMINAL_PC, SceneViewInfoModel.TABLE_NAME, sceneViewInfoId + "," + locationId, SysLogOperType.ERROR.getValue());
		}
		back.put("success", success);
		back.put("message", message);
		return back;
	}
	
	
	
	/**
	 * 
	 * 跳转到实景配置页面
	 * 
	 * @return
	 */
	@RequestMapping("toSceneViewPage")
	public ModelAndView toSceneViewPage(){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/locationsearch/locationsearch");
		return mv;
	}
	/**
	 * 
	 * 跳转到实景配置详细
	 * 
	 * @return
	 */
	@RequestMapping("toSceneViewDetailPage")
	public ModelAndView toSceneViewDetailPage(String sceneViewId,String where){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/locationsearch/sceneViewDetailInfo");
		mv.addObject("sceneViewId", sceneViewId);
		mv.addObject("where", where);
		return mv;
	}
	
	/**
	 * 更新拍摄地点的经纬度
	 * @param request
	 * @param id 拍摄地点的id
	 * @param vLongitude 经度
	 * @param vLatitude 维度
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateSceneViewInfo")
	public Map<String, Object> updateSceneViewInfo(HttpServletRequest request,String id, String vName, String vCity, String vAddress, 
										String vLongitude,String vLatitude){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(id)) {
				throw new IllegalArgumentException("请选择需要更新的地点");
			}
			
			//根据id查询出拍摄地点的详细信息
			SceneViewInfoModel model = this.sceneViewInfoService.querySceneViewById(id);
			if (StringUtils.isNotBlank(vName)) {
				model.setvName(vName);
			}
			
			if (StringUtils.isNotBlank(vCity)) {
				model.setvCity(vCity);
			}
			
			if (StringUtils.isNotBlank(vAddress)) {
				model.setvAddress(vAddress);
			}
			
			if (StringUtils.isNotBlank(vLongitude)) {
				model.setvLongitude(vLongitude);
			}
			
			if (StringUtils.isNotBlank(vLatitude)) {
				model.setvLatitude(vLatitude);
			}
			
			this.sceneViewInfoService.updateSceneByEntity(model);
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
		} catch (Exception e) {
			message = "未知异常，更新失败！";
			success = false;
			
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 获取地域列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryProCityList")
	public Map<String, Object> queryProCityList(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			List<String> proCityList = this.sceneViewInfoService.queryAllProCity();
			
			resultMap.put("shootRegionList", proCityList);
		} catch (Exception e) {
			message = "未知异常，获取地域列表失败";
			success = false;
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);		
		return resultMap;
	}
}
