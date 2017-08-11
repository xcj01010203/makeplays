package com.xiaotu.makeplays.mobile.server.sceneview;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.xiaotu.makeplays.attachment.model.AttachmentModel;
import com.xiaotu.makeplays.locationsearch.model.SceneViewInfoModel;
import com.xiaotu.makeplays.locationsearch.service.SceneViewInfoService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.mobile.server.common.BaseFacade;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.StringUtil;

/**
 * @类名：SceneViewFacade.java
 * @作者：李晓平
 * @时间：2016年12月30日 下午5:09:23
 * @描述：勘景相关接口
 */
@Controller
@RequestMapping("/interface/sceneViewFacade")
public class SceneViewFacade extends BaseFacade{
	
	Logger logger = LoggerFactory.getLogger(SceneViewFacade.class);
	
	@Autowired
	private SceneViewInfoService sceneViewInfoService;
	
	/**
	 * 获取勘景列表
	 * @param crewId 剧组ID
	 * @param userId 用户ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainSceneViewList")
	@SuppressWarnings("unchecked")
	public Object obtainSceneViewList(HttpServletRequest request, String crewId, String userId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			
			Map<String, Object> result = sceneViewInfoService.querySceneViewInfo(crewId);
			List<Map<String, Object>> resultList = (List<Map<String, Object>>)result.get("result");
			for(Map<String, Object> map : resultList) {
				map.put("vName", map.get("vname"));
				map.put("vCity", map.get("vcity"));				
			}
			resultMap.put("sceneViewList", result.get("result"));
			
			this.sysLogService.saveSysLogForApp(request, "查询堪景列表", userInfo.getClientType(), SceneViewInfoModel.TABLE_NAME, null, 0);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取勘景列表失败", e);
			this.sysLogService.saveSysLogForApp(request, "查询堪景列表失败：" + e.getMessage(), userInfo.getClientType(), SceneViewInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，获取勘景列表失败", e);
		}
		
		return resultMap;
	}
	
	/**
	 * 获取勘景详细信息
	 * @param crewId 剧组ID
	 * @param userId 用户ID
	 * @param sceneViewId 勘景ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainSceneViewInfo")
	@SuppressWarnings("unchecked")
	public Object obtainSceneViewInfo(String crewId, String userId, String sceneViewId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			Map<String, Object> result = this.sceneViewInfoService.querySceneViewInfoById(sceneViewId);
			SceneViewInfoModel sceneViewInfoModel = (SceneViewInfoModel)result.get("sceneViewInfoModel");
			if(sceneViewInfoModel != null) {
				resultMap.put("id", sceneViewInfoModel.getId());
				resultMap.put("vName", sceneViewInfoModel.getVName());
				resultMap.put("vCity", sceneViewInfoModel.getVCity());
				resultMap.put("vAddress", sceneViewInfoModel.getVAddress());
				resultMap.put("vLongitude", sceneViewInfoModel.getVLongitude());
				resultMap.put("vLatitude", sceneViewInfoModel.getVLatitude());
				resultMap.put("distanceToHotel", sceneViewInfoModel.getDistanceToHotel());
				resultMap.put("holePeoples", sceneViewInfoModel.getHolePeoples());
				resultMap.put("deviceSpace", sceneViewInfoModel.getDeviceSpace());
				//是否改景
				String isModifyViewStr = sceneViewInfoModel.getIsModifyView() + "";
				boolean isModifyView = false;
				if(StringUtil.isNotBlank(isModifyViewStr)) {
					if(isModifyViewStr.equals("0")) {
						isModifyView = true;
					}
				}
				resultMap.put("isModifyView", isModifyView);
				resultMap.put("modifyViewCost", sceneViewInfoModel.getModifyViewCost());
				resultMap.put("modifyViewTime", sceneViewInfoModel.getModifyViewTime());
				//是否有道具陈设
				String hasPropStr = sceneViewInfoModel.getHasProp() + "";
				boolean hasProp = false;
				if(StringUtil.isNotBlank(hasPropStr)) {
					if(hasPropStr.equals("0")) {
						hasProp = true;
					}
				}
				resultMap.put("hasProp", hasProp);
				resultMap.put("propCost", sceneViewInfoModel.getPropCost());
				resultMap.put("propTime", sceneViewInfoModel.getPropTime());
				resultMap.put("enterViewDate", sceneViewInfoModel.getEnterViewDate());
				resultMap.put("leaveViewDate", sceneViewInfoModel.getLeaveViewDate());
				resultMap.put("viewUseTime", sceneViewInfoModel.getViewUseTime());
				resultMap.put("contactNo", sceneViewInfoModel.getContactNo());
				resultMap.put("contactName", sceneViewInfoModel.getContactName());
				resultMap.put("contactRole", sceneViewInfoModel.getContactRole());
				resultMap.put("viewPrice", sceneViewInfoModel.getViewPrice());
				resultMap.put("freeStartDate", sceneViewInfoModel.getFreeStartDate());
				resultMap.put("freeEndDate", sceneViewInfoModel.getFreeEndDate());
				resultMap.put("remark", sceneViewInfoModel.getRemark());
				List<AttachmentModel> attachmentModelList = (List<AttachmentModel>)result.get("attachmentList");
				List<Map<String, Object>> attachmentList = new ArrayList<Map<String,Object>>();
				if(attachmentModelList != null) {
					for(AttachmentModel attachmentModel : attachmentModelList) {
						Map<String, Object> attachment = new HashMap<String, Object>();
						attachment.put("attachmentId", attachmentModel.getId());
						attachment.put("attpackId", attachmentModel.getAttpackId());
						attachment.put("name", attachmentModel.getName());
						attachment.put("suffix", attachmentModel.getSuffix());
						attachment.put("type", attachmentModel.getType());
						attachment.put("size", attachmentModel.getSize());
						attachment.put("length", attachmentModel.getLength());
						attachment.put("hdPreviewUrl", FileUtils.genPreviewPath(attachmentModel.getHdStorePath()));
						attachment.put("sdPreviewUrl", FileUtils.genPreviewPath(attachmentModel.getSdStorePath()));
						attachmentList.add(attachment);
					}
				}
				resultMap.put("attachmentList", attachmentList);
			}
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取勘景详细信息失败", e);
			throw new IllegalArgumentException("未知异常，获取勘景详细信息失败", e);
		}
		
		return resultMap;
	}
	
	/**
	 * 保存勘景信息
	 * @param crewId 剧组ID
	 * @param userId 用户ID
	 * @param sceneViewId 勘景ID
	 * @param vName 实景名称
	 * @param vCity 所在城市
	 * @param vAddress 详细地址
	 * @param vLongitude 详细地址精度
	 * @param vLatitude 详细地址纬度
	 * @param distanceToHotel 距离住宿地距离
	 * @param holePeoples 容纳人数
	 * @param deviceSpace 设备空间
	 * @param isModifyView 是否改景
	 * @param modifyViewCost 改景费用
	 * @param modifyViewTime  改景耗时
	 * @param hasProp 是否有道具陈设
	 * @param propCost 道具陈设费用
	 * @param propTime 道具陈设时间
	 * @param enterViewDate 进景时间
	 * @param leaveViewDate 离景时间
	 * @param viewUseTime 
	 * @param contactNo 联系方式
	 * @param contactName 联系人姓名
	 * @param contactRole 联系人职务
	 * @param viewPrice 场景价格
	 * @param freeStartDate 空档期开始时间
	 * @param freeEndDate 空档期结束时间
	 * @param remark 备注
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveSceneViewInfo")
	public Object saveSceneViewInfo(HttpServletRequest request, String crewId,
			String userId, String sceneViewId, String vName, String vCity,
			String vAddress, Double vLongitude, Double vLatitude,
			String distanceToHotel, Integer holePeoples, String deviceSpace,
			Boolean isModifyView, Double modifyViewCost, String modifyViewTime,
			Boolean hasProp, Double propCost, String propTime,
			String enterViewDate, String leaveViewDate, String viewUseTime,
			String contactNo, String contactName, String contactRole,
			Double viewPrice, String freeStartDate, String freeEndDate,
			String remark) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			
			if(StringUtils.isBlank(crewId)){
				throw new IllegalArgumentException("获取剧组信息失败");
			}
			
			if(StringUtils.isBlank(vName)){
				throw new IllegalArgumentException("名称不能为空");
			}
			if(StringUtils.isBlank(vAddress)) {
				throw new IllegalArgumentException("详细地址不能为空");
			}
			List<SceneViewInfoModel> list = sceneViewInfoService.querySceneViewForHasSameName(crewId,vName);
			if(StringUtils.isBlank(sceneViewId)){
				if(list!=null&&list.size()>0){
					throw new IllegalArgumentException("该剧组下有相同的实景名称");
				}
			}else{
				if(list!=null&&list.size()>0){
					for(SceneViewInfoModel sceneViewInfoModelTemp :list){
						String idStr = sceneViewInfoModelTemp.getId();
						if(!sceneViewId.equals(idStr)){
							throw new IllegalArgumentException("该剧组下有相同的实景名称");
						}
					}
				}
			}
			if(StringUtil.isBlank(isModifyView + "")) {
				throw new IllegalArgumentException("是否改景不能为空");
			}
			int isModifyViewStr = 1;
			if(isModifyView){
				if(StringUtils.isBlank(modifyViewTime) || StringUtil.isBlank(modifyViewCost + "")){
					throw new IllegalArgumentException("已经选择改景，但是改景相关信息异常");
				}
				isModifyViewStr = 0;
			} else {
				modifyViewCost = null;
				modifyViewTime = null;
			}
			if(StringUtil.isBlank(hasProp + "")) {
				throw new IllegalArgumentException("是否有道具陈设不能为空");
			}
			int hasPropStr = 1;
			if(hasProp){
				if(StringUtils.isBlank(propTime) || StringUtil.isBlank(propCost + "")){
					throw new IllegalArgumentException("已经选择有道具，但是道具相关信息异常");
				}
				hasPropStr = 0;
			} else {
				propCost = null;
				propTime = null;
			}
			
			//验证字段长度
			if(vName.length() > 50){
				throw new IllegalArgumentException("名称最大长度为50");
			}
			if(StringUtil.isNotBlank(vCity) && vCity.length() > 50){
				throw new IllegalArgumentException("所在地最大长度为50");
			}
			if(vAddress.length() > 255){
				throw new IllegalArgumentException("详细地址最大长度为255");
			}
			if(StringUtil.isNotBlank(distanceToHotel) && distanceToHotel.length() > 50){
				throw new IllegalArgumentException("距离住宿地距离最大长度为50");
			}
			if(StringUtil.isNotBlank(holePeoples + "") && holePeoples.toString().length() > 8){
				throw new IllegalArgumentException("容纳人数最大长度为8");
			}
			if(StringUtil.isNotBlank(deviceSpace) && deviceSpace.length() > 100){
				throw new IllegalArgumentException("设备空间最大长度为100");
			}
			if(StringUtil.isNotBlank(modifyViewTime) && modifyViewTime.length() > 50){
				throw new IllegalArgumentException("改景时间最大长度为50");
			}
			if(StringUtil.isNotBlank(propTime) && propTime.length() > 50){
				throw new IllegalArgumentException("道具陈设时间最大长度为50");
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if(StringUtil.isNotBlank(enterViewDate)) {
				try {
					sdf.parse(enterViewDate);
				} catch (Exception e) {
					throw new IllegalArgumentException("进景时间格式为：yyyy-MM-dd");
				}
			}
			if(StringUtil.isNotBlank(leaveViewDate)) {
				try {
					sdf.parse(leaveViewDate);
				} catch (Exception e) {
					throw new IllegalArgumentException("离景时间格式为：yyyy-MM-dd");
				}
			}
			if(StringUtil.isNotBlank(enterViewDate) && StringUtil.isNotBlank(leaveViewDate)) {
				Date enterViewDateFormat = sdf.parse(enterViewDate);
				Date leaveViewDateFormat = sdf.parse(leaveViewDate);
				if (enterViewDateFormat.after(leaveViewDateFormat)) {
					throw new IllegalArgumentException("离景时间不能早于进景日期，请检查");
				}
			}
			if(StringUtil.isNotBlank(viewUseTime) && viewUseTime.length() > 50){
				throw new IllegalArgumentException("使用时间最大长度为50");
			}
			if(StringUtil.isNotBlank(contactNo) && contactNo.length() > 50){
				throw new IllegalArgumentException("联系方式最大长度为50");
			}
			if(StringUtil.isNotBlank(contactName) && contactName.length() > 50){
				throw new IllegalArgumentException("联系人姓名最大长度为50");
			}
			if(StringUtil.isNotBlank(contactRole) && contactRole.length() > 50){
				throw new IllegalArgumentException("联系人职务最大长度为50");
			}
			if(StringUtil.isNotBlank(freeStartDate)) {
				try {
					sdf.parse(freeStartDate);
				} catch (Exception e) {
					throw new IllegalArgumentException("空档期开始时间格式为：yyyy-MM-dd");
				}
			}
			if(StringUtil.isNotBlank(freeEndDate)) {
				try {
					sdf.parse(freeEndDate);
				} catch (Exception e) {
					throw new IllegalArgumentException("空档期结束时间格式为：yyyy-MM-dd");
				}
			}
			if(StringUtil.isNotBlank(freeStartDate) && StringUtil.isNotBlank(freeEndDate)) {
				Date freeStartDateFormat = sdf.parse(freeStartDate);
				Date freeEndDateFormat = sdf.parse(freeEndDate);
				if (freeStartDateFormat.after(freeEndDateFormat)) {
					throw new IllegalArgumentException("空档期结束时间不能早于空档期开始时间，请检查");
				}
			}
			if(StringUtil.isNotBlank(remark) && remark.length() > 500){
				throw new IllegalArgumentException("备注最大长度为500");
			}
			
			String sceneViewInfoId = sceneViewInfoService
					.saveOrUpdateSceneViewInfo(sceneViewId, vName, vCity,
							vAddress, vLongitude+"", vLatitude+"", distanceToHotel,
							holePeoples, deviceSpace, isModifyViewStr,
							modifyViewCost, modifyViewTime, hasPropStr, propCost,
							propTime, enterViewDate, leaveViewDate,
							viewUseTime, contactNo, contactName, contactRole,
							viewPrice, freeStartDate, freeEndDate, remark,
							crewId);
			resultMap.put("attpackId", sceneViewInfoId);
			
			if(StringUtils.isBlank(sceneViewId)){
				this.sysLogService.saveSysLogForApp(request, "新增堪景信息", userInfo.getClientType(), SceneViewInfoModel.TABLE_NAME, null, 1);
			} else {
				this.sysLogService.saveSysLogForApp(request, "修改堪景信息", userInfo.getClientType(), SceneViewInfoModel.TABLE_NAME, null, 2);
			}
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，保存勘景信息失败", e);
			this.sysLogService.saveSysLogForApp(request, "保存堪景信息失败：" + e.getMessage(), userInfo.getClientType(), SceneViewInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，保存勘景信息失败", e);
		}
		
		return resultMap;
	}
	
	/**
	 * 删除勘景信息
	 * @param crewId 剧组ID
	 * @param userId 用户ID
	 * @param sceneViewId 勘景ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteSceneViewInfo")
	public Object deleteSceneViewInfo(HttpServletRequest request, String crewId, String userId, String sceneViewId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(sceneViewId)) {
				throw new IllegalArgumentException("请提供勘景信息");
			}
			this.sceneViewInfoService.delSceneViewInfo(sceneViewId);
			
			this.sysLogService.saveSysLogForApp(request, "删除堪景信息", userInfo.getClientType(), SceneViewInfoModel.TABLE_NAME, null, 3);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，删除勘景信息失败", e);
			this.sysLogService.saveSysLogForApp(request, "删除堪景信息失败：" + e.getMessage(), userInfo.getClientType(), SceneViewInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，删除勘景信息失败", e);
		}
		
		return resultMap;
	}
}
