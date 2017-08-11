package com.xiaotu.makeplays.shoot.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.sun.star.lang.IllegalArgumentException;
import com.xiaotu.makeplays.goods.dao.GoodsInfoDao;
import com.xiaotu.makeplays.goods.model.GoodsInfoModel;
import com.xiaotu.makeplays.goods.model.constants.GoodsType;
import com.xiaotu.makeplays.roleactor.dao.ViewRoleDao;
import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;
import com.xiaotu.makeplays.roleactor.model.constants.ViewRoleType;
import com.xiaotu.makeplays.shoot.dao.ScheduleDao;
import com.xiaotu.makeplays.shoot.dao.ScheduleViewMapDao;
import com.xiaotu.makeplays.shoot.model.ScheduleGroupModel;
import com.xiaotu.makeplays.shoot.model.ScheduleViewMapModel;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.PythonUtil;
import com.xiaotu.makeplays.utils.SocketClientUtil;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.view.controller.filter.ViewFilter;
import com.xiaotu.makeplays.view.dao.ViewLocationDao;
import com.xiaotu.makeplays.view.model.ViewLocationModel;
import com.xiaotu.makeplays.view.model.constants.LocationType;

@Service
public class ScheduleService {

	@Autowired
	private ScheduleDao scheduleDao;
	
	@Autowired
	private ScheduleViewMapDao scheduleViewMapDao;
	
	@Autowired
	private ViewLocationDao viewLocationDao;
	
	@Autowired
	private ViewRoleDao viewRoleDao;
	
	@Autowired
	private GoodsInfoDao goodsInfoDao;
		
	/**
	 * 查询计划分组列表
	 * @param crewId 剧组ID
	 * @param groupName 分组名称
	 * @return
	 */
	public List<Map<String, Object>> queryScheduleGroupList(String crewId, String groupName) {
		return this.scheduleDao.queryScheduleGroupList(crewId, groupName);
	}	
	
	/**
	 * 根据id查询出计划分组的详细信息
	 * @param crewId
	 * @param groupId
	 * @return
	 * @throws Exception 
	 */
	public ScheduleGroupModel queryScheduleGroupById(String crewId, String groupId) throws Exception {
		return this.scheduleDao.queryScheduleGroupById(crewId, groupId);
	}
	
	/**
	 * 保存计划分组信息
	 * @param crewId
	 * @param groupId
	 * @param groupName
	 * @throws Exception
	 */
	public ScheduleGroupModel saveScheduleGroupInfo(String crewId, String groupId, String groupName) throws Exception{
		ScheduleGroupModel scheduleGroupModel = null;
		if(StringUtils.isBlank(groupId)) { //新增,顺序在最上面
			scheduleGroupModel = new ScheduleGroupModel();
			scheduleGroupModel.setId(UUIDUtils.getId());
			scheduleGroupModel.setGroupName(groupName);
			scheduleGroupModel.setSequence(0);
			scheduleGroupModel.setCreateTime(new Date());
			scheduleGroupModel.setCrewId(crewId);
			this.scheduleDao.add(scheduleGroupModel);
		} else { //修改
			scheduleGroupModel = this.scheduleDao.queryScheduleGroupById(crewId, groupId);
			if(StringUtils.isNotBlank(groupName)) {
				scheduleGroupModel.setGroupName(groupName);
			}
			this.scheduleDao.updateWithNull(scheduleGroupModel, "id");
		}
		return scheduleGroupModel;
	}
	
	/**
	 * 更新计划分组排序
	 * @param groupIds
	 */
	public void updateScheduleGroupSequence(String crewId, String groupIds){
		//更新计划分组排序
		this.scheduleDao.updateScheduleGroupSequence(crewId, groupIds);
		//更新分组场景排列顺序
//		this.scheduleViewMapDao.updateViewGroupMapSequence(crewId);
	}
	
	/**
	 * 删除计划分组信息
	 * @param groupIds
	 * @throws Exception
	 */
	public void deleteScheduleGroupInfo(String crewId, String groupIds) throws Exception {
		String[] groupIdArray = groupIds.split(",");
		for(String groupId : groupIdArray) {
			//将分组与场景关联关系中分组ID设为空
//			this.scheduleViewMapDao.deleteOne(groupId, "planGroupId", ScheduleViewMapModel.TABLE_NAME);
			this.scheduleViewMapDao.updateViewScheduleGroupIdBySchGroupId(crewId, groupId);
			//删除分组信息
			this.scheduleDao.deleteOne(groupId, "id", ScheduleGroupModel.TABLE_NAME);
		}
	}
	
	/**
	 * 锁定场景
	 * @param crewId
	 * @param viewIds
	 * @param isLock
	 * @throws Exception
	 */
	public void updateViewGroupMapIsLock(String crewId, String viewIds, boolean isLock) throws Exception {
		//更新已有关联关系场景锁定状态
		this.scheduleViewMapDao.updateViewGroupMapIsLock(crewId, viewIds, isLock);
		//新增未添加到关联关系表中的场景，并设置锁定状态
		this.scheduleViewMapDao.addViewGroupMapLock(crewId, viewIds, isLock);
	}
	
	/**
	 * 设置计划日期和计划组别
	 * @param crewId
	 * @param viewIds 场景ID，多个以逗号分隔
	 * @param planDate 计划拍摄日期
	 * @param planGroupId 计划拍摄组别ID
	 * @param dayNum 提前/延后天数
	 * @throws Exception
	 */
	public void setScheduleDateAndGroup(String crewId, String viewIds, String planDate, String planGroupId, Integer dayNum) throws Exception {
		//更新已有关联关系场景计划日期和计划组别
		this.scheduleViewMapDao.updateViewGroupMapDateAndGroupId(crewId, viewIds, planDate, planGroupId, dayNum);
		//新增未添加到关联关系表中的场景，并设置计划日期和计划组别
		this.scheduleViewMapDao.addViewGroupMapDateAndGroupId(crewId, viewIds, planDate, planGroupId);
	}
	
	/**
	 * 将场景移动到某个计划分组中
	 * @param crewId
	 * @param viewIds
	 * @param groupId 移动到的计划分组ID
	 * @param targetViewId 粘贴的场景ID
	 * @throws Exception
	 */
	public String setViewScheduleGroup(String crewId, String viewIds, String groupId, String targetViewId) throws Exception {
		if(StringUtils.isNotBlank(groupId)) {//移动到
			if(groupId.equals("0")) {//未分组
				groupId = null;
				//更新已有关联关系场景计划分组ID
				this.scheduleViewMapDao.updateViewScheduleGroupId(crewId, viewIds, groupId);
				//新增未添加到关联关系表中的场景，并设置计划分组ID
				this.scheduleViewMapDao.addViewScheduleGroupId(crewId, viewIds, groupId);
				//更新场景顺序为空
				this.scheduleViewMapDao.updateViewSequenceNull(crewId, viewIds);
			} else {				
				int sequence = 1;
				List<Map<String, Object>> sequenceList = this.scheduleViewMapDao.queryMaxSequenceByPlanGroupId(crewId, groupId);
				if(sequenceList != null && sequenceList.size() > 0) {
					if((Integer) sequenceList.get(0).get("sequence") != null) {
						sequence = (Integer) sequenceList.get(0).get("sequence") + 1;
					}
				}
				//更新已有关联关系场景计划分组ID
				this.scheduleViewMapDao.updateViewScheduleGroupId(crewId, viewIds, groupId);
				//新增未添加到关联关系表中的场景，并设置计划分组ID
				this.scheduleViewMapDao.addViewScheduleGroupId(crewId, viewIds, groupId);
				//更新场景的排列顺序
				this.scheduleViewMapDao.updateViewSequenceBatch(crewId, viewIds, sequence);
			}
		} else if(StringUtils.isNotBlank(targetViewId)) {//剪切粘贴
			//查询目标场景所在计划分组及排列顺序
			ScheduleViewMapModel scheduleViewMap = this.scheduleViewMapDao.queryDetailByViewId(crewId, targetViewId);
			if(scheduleViewMap != null && StringUtil.isNotBlank(scheduleViewMap.getPlanGroupId())) {
				groupId = scheduleViewMap.getPlanGroupId();
				int sequence = scheduleViewMap.getSequence();
				int num = viewIds.split(",").length;
				//更新原有场景的排列顺序
				this.scheduleViewMapDao.updateViewSequence(crewId, groupId, sequence, num);
				//更新已有关联关系场景计划分组ID
				this.scheduleViewMapDao.updateViewScheduleGroupId(crewId, viewIds, groupId);
				//新增未添加到关联关系表中的场景，并设置计划分组ID
				this.scheduleViewMapDao.addViewScheduleGroupId(crewId, viewIds, groupId);
				//更新新增场景的排列顺序
				this.scheduleViewMapDao.updateViewSequenceBatch(crewId, viewIds, sequence);
			} else {
				throw new IllegalArgumentException("不能移动到未分组里");
			}
		}
		return groupId;
	}
	
	/**
	 * 智能排期
	 * @param crewId
	 * @param conditionOne
	 * @param conditionTwo
	 * @param viewRole
	 * @throws Exception
	 */
	public void autoSchedule(HttpServletRequest request, String crewId, String conditionOne, String conditionTwo, String viewRole) throws Exception{
		String viewRoleName = "";
		//组装智能排期条件
		List<Object> params = new ArrayList<Object>();
		if(StringUtils.isNotBlank(conditionOne)) {
			if(conditionOne.equals("viewRole")) {
				String[] viewRoleArray = viewRole.split(",");
				for(String one : viewRoleArray) {
					String roleName = viewRoleDao.queryById(one).getViewRoleName();
					viewRoleName += roleName + ",";
					params.add(roleName);
				}
			} else {
				params.add(conditionOne);
			}
		}
		if(StringUtils.isNotBlank(conditionTwo)) {
			if(conditionTwo.equals("viewRole")) {
				String[] viewRoleArray = viewRole.split(",");
				for(String one : viewRoleArray) {
					String roleName = viewRoleDao.queryById(one).getViewRoleName();
					viewRoleName += roleName + ",";
					params.add(roleName);
				}
			} else {
				params.add(conditionTwo);
			}
		}
		if(StringUtils.isNotBlank(viewRoleName)) {
			viewRoleName = viewRoleName.substring(0, viewRoleName.length() - 1);
		}
		//获取主演、特约名称
		List<Map<String, Object>> viewRoleList = this.viewRoleDao.queryRoleNamesByCrewId(crewId, "1,2");
		String viewRoleNames = null;
		if(viewRoleList != null && viewRoleList.size() > 0) {
			viewRoleNames = (String) viewRoleList.get(0).get("names");
		}
		//获取待整理的场景列表
		List<Map<String, Object>> viewInfoList = this.queryViewListForAutoSchedule(crewId, viewRole, viewRoleName);
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("condition", params.toArray());
		dataMap.put("viewRoleNames", viewRoleNames);
		dataMap.put("datas", viewInfoList);
		String datas = JSONObject.fromObject(dataMap).toString();
		System.out.println(datas);
		//调用python脚本进行智能排期
		String pythonPath = request.getSession().getServletContext().getRealPath("/python") + File.separatorChar + "ordination_sort.py";
		PythonUtil python = new PythonUtil(pythonPath);
		String backStr = python.send(datas);
		//调用python socket server进行智能排期
//		Properties property = PropertiesUitls.fetchProperties("/config.properties");
//		String serverIp = property.getProperty("PYTHON_SERVER_IP");
//		String serverPort = property.getProperty("PYTHON_SERVER_PORT");
//		SocketClientUtil socket = new SocketClientUtil(serverIp, Integer.parseInt(serverPort));
//		String backStr = socket.send(datas);
		System.out.println(backStr);
		if(StringUtils.isNotBlank(backStr)) {
			//删除未锁定的原来的分组数据
			this.scheduleDao.deleteScheduleGroupNotLock(crewId);
			//将未锁定的分组场景关联关系分组ID置为空
			this.scheduleViewMapDao.updateNotLockPlanGroupIdNullByCrewId(crewId);
			
			//将返回结果处理成可解析json字符串格式
			JSONObject obj = JSONObject.fromObject(backStr);
//			JSONArray classList = obj.getJSONArray("classList");
			JSONArray classTagList = obj.getJSONArray("classTagList");
			JSONArray viewIdList = obj.getJSONArray("idList");
			List<ScheduleGroupModel> scheduleGroupList = new ArrayList<ScheduleGroupModel>();
			Map<String, ScheduleGroupModel> flagMap = new HashMap<String, ScheduleGroupModel>();
			List<ScheduleViewMapModel> scheduleViewMapAddList = new ArrayList<ScheduleViewMapModel>();
			List<ScheduleViewMapModel> scheduleViewMapUpdateList = new ArrayList<ScheduleViewMapModel>();
			int sequence = this.scheduleDao.queryMaxSequence(crewId);
			int j = 1;
			for(int i = 0; i < classTagList.size(); i++) {
				String groupName = classTagList.getString(i);
				if(StringUtils.isBlank(groupName)) {
					groupName = "其他";
				}
				String viewId = viewIdList.getString(i);
				ScheduleGroupModel scheduleGroupModel = null;
				String id = "";
				if(!flagMap.containsKey(groupName)) {
					j = 1;
					scheduleGroupModel = new ScheduleGroupModel();
					id = UUIDUtils.getId();
					scheduleGroupModel.setId(id);
					scheduleGroupModel.setGroupName(groupName);
					scheduleGroupModel.setCrewId(crewId);
					scheduleGroupModel.setSequence(++sequence);
					scheduleGroupModel.setCreateTime(new Date());
					scheduleGroupList.add(scheduleGroupModel);
					flagMap.put(groupName, scheduleGroupModel);
				} else {
					scheduleGroupModel = flagMap.get(groupName);
					id = scheduleGroupModel.getId();
				}
				
				ScheduleViewMapModel scheduleViewMapModel = this.scheduleViewMapDao.queryDetailByViewId(crewId, viewId);
				if(scheduleViewMapModel == null) {
					scheduleViewMapModel = new ScheduleViewMapModel();
					scheduleViewMapModel.setId(UUIDUtils.getId());
					scheduleViewMapModel.setCrewId(crewId);
					scheduleViewMapModel.setPlanGroupId(id);
					scheduleViewMapModel.setViewId(viewId);
					scheduleViewMapModel.setSequence(j++);
					scheduleViewMapModel.setShootDate(null);
					scheduleViewMapModel.setShootGroupId(null);
					scheduleViewMapModel.setIsLock(0);
					scheduleViewMapAddList.add(scheduleViewMapModel);
				} else {
					scheduleViewMapModel.setPlanGroupId(id);
					scheduleViewMapModel.setSequence(j++);
					scheduleViewMapUpdateList.add(scheduleViewMapModel);
				}
			}
			if(scheduleGroupList != null && scheduleGroupList.size() > 0) {
				this.scheduleDao.addBatch(scheduleGroupList, ScheduleGroupModel.class);
			}
			if(scheduleViewMapAddList != null && scheduleViewMapAddList.size() > 0) {
				this.scheduleViewMapDao.addBatch(scheduleViewMapAddList, ScheduleViewMapModel.class);
			}
			if(scheduleViewMapUpdateList != null && scheduleViewMapUpdateList.size() > 0) {
				this.scheduleViewMapDao.updateBatch(scheduleViewMapUpdateList, "id", ScheduleViewMapModel.class);
			}
		}
	}
	
	private List<Map<String, Object>> queryViewListForAutoSchedule(String crewId, String viewRole, String viewRoleName) {
		//查询待整理场景
		List<Map<String, Object>> viewInfoList = scheduleDao.queryViewListForAutoSchedule(crewId);
		if(null == viewInfoList || viewInfoList.size()==0){
			return null;
		}
		if(StringUtils.isNotBlank(viewRole)) {
			String[] viewRoleArray = viewRole.split(",");
			String[] viewRoleNameArray = viewRoleName.split(",");
			
			String viewIds = "";
			for(Map<String, Object> map : viewInfoList){
				String viewId = (String) map.get("viewId");
				viewIds += "'" + viewId + "',";
			}
			if(StringUtils.isNotBlank(viewIds)) {
				viewIds=viewIds.substring(0, viewIds.length() - 1);
			}
			List<Map<String, Object>> viewRoleList = this.viewRoleDao.queryViewRoleListByViewId(viewIds, ViewRoleType.MajorActor.getValue()); //角色主要演员信息
			Map<String, List<Map<String, Object>>> mainRoleGroup = new HashMap<String, List<Map<String, Object>>>();
			for (Map<String, Object> viewRoleMap : viewRoleList) {
				String roleViewId = (String) viewRoleMap.get("viewId");
				
				//主要演员分组
				if (!mainRoleGroup.containsKey(roleViewId)) {
					List<Map<String, Object>> viewMainRoleList = new ArrayList<Map<String, Object>>();
					viewMainRoleList.add(viewRoleMap);
					mainRoleGroup.put(roleViewId, viewMainRoleList);
				} else {
					mainRoleGroup.get(roleViewId).add(viewRoleMap);
				}
			}
			for(Map<String, Object> viewInfo : viewInfoList) {
				String viewId = (String)viewInfo.get("viewId");
				
				//主要演员信息
				if(null != mainRoleGroup.get(viewId)){
					List<Map<String, Object>> roleList = (List<Map<String, Object>>)mainRoleGroup.get(viewId);
					for(int i = 0; i < viewRoleArray.length; i++) {
						String one = viewRoleArray[i];
						boolean hasRoleFlag = false; // 标识当前场景的演员在所有主要演员中是否存在
						for (Map<String, Object> roleMap : roleList) {
							if (roleMap.get("viewRoleId").equals(one)) {
								if (StringUtils.isBlank((String) roleMap.get("shortName"))) {
									viewInfo.put(viewRoleNameArray[i], "√");
								} else {
									viewInfo.put(viewRoleNameArray[i], (String) roleMap.get("shortName"));
								}
								hasRoleFlag = true;
								break;
							}
						}
						
						// 如果不存在就添加一个空的对象，保证在表格中显示列正确
						if (!hasRoleFlag) {
							viewInfo.put(viewRoleNameArray[i], "");
						}
					}
				} else {
					for(int i = 0; i < viewRoleArray.length; i++) {
						viewInfo.put(viewRoleNameArray[i], "");
					}
				}
			}
		}
		return viewInfoList;
	}
	
	/**
	 * 场景表查询
	 * @param crewId
	 * @param page
	 * @param filter
	 * @return
	 */
	public List<Map<String, Object>> queryViewList(String crewId, Page page, ViewFilter filter){
		
		//查询场景表
		List<Map<String, Object>> viewInfoList = scheduleDao.queryViewList(crewId, page, filter);
		
		if(null == viewInfoList||viewInfoList.size()==0){
			return null;
		}
		
		String viewIds = "";
		for(Map<String, Object> map : viewInfoList){
			String viewId = (String)map.get("viewId");
			viewIds+="'"+viewId+"',";
			
			String address = (String) map.get("viewAddress");
			
			if(StringUtils.isBlank(address)){
				continue;
			}
		}
		if(StringUtils.isNotBlank(viewIds)) {
			viewIds=viewIds.substring(0, viewIds.length() - 1);
		}
		
		if(StringUtils.isBlank(viewIds)){
			return null;
		}
		
		/*
		 * 查询场景表中其他信息
		 */
//		List<Map<String, Object>> viewLocationList = this.viewLocationDao.queryViewLocationByViewIds(viewIds); //主场景、次场景、三级场景
		List<Map<String, Object>> viewRoleList = this.viewRoleDao.queryViewRoleListByViewId(viewIds, null);	//角色演员信息
		List<Map<String, Object>> propsList = this.goodsInfoDao.queryManyByViews(viewIds);//道具信息
		
		//演员角色按照场景ID分组
		Map<String, List<Map<String, Object>>> mainRoleGroup = new HashMap<String, List<Map<String, Object>>>();
		Map<String, String> mainRoleGroupStringMap = new HashMap<String, String>();
		Map<String, String> mainRoleShortNameGroupStringMap = new HashMap<String, String>();
		Map<String, String> guestRoleGroup = new HashMap<String, String>();
		Map<String, String> massRoleGroup = new HashMap<String, String>();
		for (Map<String, Object> viewRoleMap : viewRoleList) {
			String roleViewId = (String) viewRoleMap.get("viewId");
			int roleType = (Integer) viewRoleMap.get("viewRoleType");
			String roleName = (String) viewRoleMap.get("viewRoleName");
			int roleNum = (Integer) viewRoleMap.get("roleNum");
			String shortName = (String) viewRoleMap.get("shortName");
			
			//主要演员分组
			if (roleType == ViewRoleType.MajorActor.getValue()) {
				if (!mainRoleGroup.containsKey(roleViewId)) {
					List<Map<String, Object>> viewMainRoleList = new ArrayList<Map<String, Object>>();
					viewMainRoleList.add(viewRoleMap);
					mainRoleGroup.put(roleViewId, viewMainRoleList);
					
					mainRoleGroupStringMap.put(roleViewId, roleName);
					mainRoleShortNameGroupStringMap.put(roleViewId, shortName);
				} else {
					mainRoleGroup.get(roleViewId).add(viewRoleMap);
					mainRoleGroupStringMap.put(roleViewId, mainRoleGroupStringMap.get(roleViewId) + "," + roleName);
					mainRoleShortNameGroupStringMap.put(roleViewId, mainRoleShortNameGroupStringMap.get(roleViewId) + "," + shortName);
				}
			}
			
			//特约演员分组
			if (roleType == ViewRoleType.GuestActor.getValue()) {
				if (!guestRoleGroup.containsKey(roleViewId)) {
					guestRoleGroup.put(roleViewId, roleName);
				} else {
					guestRoleGroup.put(roleViewId, guestRoleGroup.get(roleViewId) + "," + roleName);
				}
			}
			
			//群众演员分组
			if (roleType == ViewRoleType.MassesActor.getValue()) {
				if (!massRoleGroup.containsKey(roleViewId)) {
					if (roleNum == 1) {
						massRoleGroup.put(roleViewId, roleName);
					}else {
						massRoleGroup.put(roleViewId, roleName +"(" + roleNum + ")");
					}
				} else {
					if (roleNum == 1) {
						massRoleGroup.put(roleViewId, massRoleGroup.get(roleViewId) + "," + roleName);
					}else {
						massRoleGroup.put(roleViewId, massRoleGroup.get(roleViewId) + "," + roleName + "(" + roleNum +")");
					}
				}
			}
		}
		
		//高级查询时主要演员查询条件
		List<String> filterList = new ArrayList<String>();
		
		if(null != filter&& StringUtils.isNotBlank(filter.getRoles())){
			
			String[] role = filter.getRoles().split(",");
			filterList.addAll(Arrays.asList(role));
		}
		
		/*
		 * 道具
		 */
		//道具按照场景ID分组
		Map<String, String> commonPropsGroup = new HashMap<String, String>();
		Map<String, String> specialPropsGroup = new HashMap<String, String>();
		for (Map<String, Object> propmap : propsList) {
			String propViewId = (String) propmap.get("viewId");
			String propName = (String) propmap.get("goodsName");
			int propType = (Integer) propmap.get("goodsType");
			
			//普通道具
			if (propType == GoodsType.CommonProps.getValue()) {
				if(!commonPropsGroup.containsKey(propViewId)) {
					commonPropsGroup.put(propViewId, propName);
				} else {
					commonPropsGroup.put(propViewId, commonPropsGroup.get(propViewId) + "," + propName);
				}
			}
			
			//特殊道具
			if (propType == GoodsType.SpecialProps.getValue()) {
				if(!specialPropsGroup.containsKey(propViewId)) {
					specialPropsGroup.put(propViewId, propName);
				} else {
					specialPropsGroup.put(propViewId, specialPropsGroup.get(propViewId) + "," + propName);
				}
			}
		}
		
		/*
		 * 循环场景表信息，设置场景表中的演员角色、主次三级场景、道具等信息
		 */
		for(int i=viewInfoList.size()-1;i>=0 ;i--){
			Map<String, Object> viewMap = viewInfoList.get(i);
			
			String viewId = (String)viewMap.get("viewId");
			
			//演员角色信息
			//主要演员信息
			if(null != mainRoleGroup.get(viewId)){
				List<String> roleList = (ArrayList)mainRoleGroup.get(viewId);
				viewMap.put("roleList", roleList);
				
				viewMap.put("mainRoleList", mainRoleGroupStringMap.get(viewId));
				viewMap.put("mainRoleShortNames", mainRoleShortNameGroupStringMap.get(viewId));
			}else{
				viewMap.put("roleList", new ArrayList<String>());
				viewMap.put("mainRoleList", "");
				viewMap.put("mainRoleShortNames", "");
			}
			
			//特约演员信息
			if(null != guestRoleGroup.get(viewId)){
				viewMap.put("guestRoleList", guestRoleGroup.get(viewId));
			}else{
				viewMap.put("guestRoleList", "");
			}

			//群众演员信息
			if(null != massRoleGroup.get(viewId)){
				viewMap.put("massRoleList", massRoleGroup.get(viewId));
			}else{
				viewMap.put("massRoleList", "");
			}
			
			//道具信息
			//普通道具
			if(null != commonPropsGroup.get(viewId)){
				viewMap.put("propsList", commonPropsGroup.get(viewId));
			}else{
				viewMap.put("propsList", "");
			}
			
			//特殊道具
			if(null != specialPropsGroup.get(viewId)){
				viewMap.put("specialPropsList", specialPropsGroup.get(viewId));
			}else{
				viewMap.put("specialPropsList", "");
			}
		}
		
		return viewInfoList;
	}
	
	/**
	 * 查询场次统计数据
	 * @return
	 */
	public Map<String, Object> queryViewStatistics(String crewId,ViewFilter filter){
		Map<String, Object> map = new HashMap<String, Object>();
		//统计总场数
		List<Map<String, Object>> viewCountList = scheduleDao.queryViewListStatistics(crewId, filter, "", "viewId", "count");
		
		Map<String, Object> viewCountMap = (Map<String, Object>)viewCountList.get(0);
		if(null == viewCountMap.get("funResult")){
			viewCountMap.put("funResult",0);
		}
		map.put("statisticsViewCount", viewCountList);
		
		//统计剧本总页数
		List<Map<String, Object>> pageCountList = scheduleDao.queryViewListStatistics(crewId, filter, "", "pageCount", "sum");
		Map<String, Object> pageMap = (Map<String, Object>)pageCountList.get(0);
		if(null == pageMap.get("funResult")){
			pageMap.put("funResult",0);
		}
		map.put("statisticsPageCount", pageCountList);
		
		//统计拍摄状态
		List<Map<String, Object>> shootStatusList = scheduleDao.queryViewListStatistics(crewId, filter, "shootStatus", "viewId", "count");
		map.put("statisticsShootStatus",shootStatusList);
		
		//统计内外景
		List<Map<String, Object>> siteList = scheduleDao.queryViewListStatistics(crewId, filter, "site", "viewId", "count");
		map.put("statisticsSite", siteList);
		
		//统计文武戏
		List<Map<String, Object>> viewTypeList = scheduleDao.queryViewListStatistics(crewId, filter, "viewType", "viewId", "count");
		map.put("statisticsType",viewTypeList);
		
		return map;
	}
	
	/**
	 * 查询关注项列表
	 * @param crewId
	 * @param name
	 * @return
	 */
	public Map<String, Object> queryAttentionInfo(String crewId, String name) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<ViewRoleModel> viewRoleList = this.viewRoleDao.queryManyByCrewIdAndTypeAndName(crewId, ViewRoleType.MajorActor.getValue(), name);
		resultMap.put("viewRoleList", viewRoleList);
		List<GoodsInfoModel> goodsList = this.goodsInfoDao.queryManyByCrewIdAndTypeAndName(crewId, GoodsType.SpecialProps.getValue(), name);
		resultMap.put("specialPropList", goodsList);
		List<ViewLocationModel> locationList = this.viewLocationDao.queryManyByCrewIdAndTypeAndName(crewId, LocationType.lvlOneLocation.getValue(), name);
		resultMap.put("locationList", locationList);
		return resultMap;
	}
	
	/**
	 * 查询计划按日汇总信息，包括关注项
	 * @param crewId
	 * @param attention
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryScheduleCalendarInfo(String crewId, String attention) {
		String viewRoleIds = "";
		String propIds = "";
		String locationIds = "";
		if(StringUtils.isNotBlank(attention)) {
			//判断关注条件
			String[] attentionArray = attention.split(",");
			for(String one : attentionArray) {
				String[] arr = one.split(":", -1);
				if(arr[0].equals("1")) {
					viewRoleIds += "," + arr[1];
				} else if(arr[0].equals("2")) {
					propIds += "," + arr[1];
				} else if(arr[0].equals("3")) {
					locationIds += "," + arr[1];
				}
			}
			if(StringUtils.isNotBlank(viewRoleIds)) {
				viewRoleIds = viewRoleIds.substring(1);
			}
			if(StringUtils.isNotBlank(propIds)) {
				propIds = propIds.substring(1);
			}
			if(StringUtils.isNotBlank(locationIds)) {
				locationIds = locationIds.substring(1);
			}			
		}
		//查询按日汇总信息
		List<Map<String, Object>> result = this.scheduleViewMapDao.queryScheduleCalendar(crewId, viewRoleIds, propIds, locationIds);
		//最终结果集
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>(); 
		//处理关注项
		String[] keyArray = new String[] {"roles", "specialProps", "majorViews"};
		if(result != null && result.size() > 0) {
			List<Map<String, Object>> newResult = new ArrayList<Map<String,Object>>();
			newResult.add(result.get(0));
			for(int i = 1; i < result.size(); i++) {
				Map<String, Object> one = result.get(i);
				Map<String, Object> lastOne = result.get(i - 1);
				if((Date)one.get("shootDate") != null) {
					Calendar cal1 = Calendar.getInstance();
					cal1.setTime((Date)one.get("shootDate"));
					Calendar cal2 = Calendar.getInstance();
					cal2.setTime((Date)lastOne.get("shootDate"));
					while((int) ((cal1.getTimeInMillis() - cal2.getTimeInMillis()) / (1000*3600*24)) != 1) {
						cal2.add(Calendar.DAY_OF_MONTH, 1);
						Map<String, Object> map = new LinkedCaseInsensitiveMap<Object>();
						map.put("year", getValue(cal2.get(Calendar.YEAR)));
						map.put("month", getValue(cal2.get(Calendar.MONTH) + 1));
						map.put("day", getValue(cal2.get(Calendar.DAY_OF_MONTH)));
						newResult.add(map);
					}
				}
				newResult.add(one);
			}			
			
			List<Map<String, Object>> shootLocationList = null;
			String shootLocationFlag = "-1";
			for(Map<String, Object> one : newResult) {
				if(StringUtils.isNotBlank(attention)) {
					for(String key : keyArray) {
						if(StringUtils.isNotBlank((String) one.get(key))) {
							String keyStr = (String) one.get(key);
							String[] keyStrs = keyStr.split(",");
							for(String oneStr : keyStrs) {
								one.put(oneStr, true);
							}
						}
					}
				}
				String year = "";
				if(StringUtils.isNotBlank((String)one.get("year"))) {
					year = (String) one.get("year");
				}
				String month = "";
				if(StringUtils.isNotBlank((String)one.get("month"))) {
					month = (String) one.get("month");
				}
				String shootLocation = "";
				if(StringUtils.isNotBlank((String)one.get("shootLocation"))) {
					shootLocation = (String) one.get("shootLocation");
				}
				String key = year + "-" + month;
				if(StringUtil.isBlank(year)) {
					key = "";
				}
				if(resultMap.containsKey(key)) {
					shootLocationList = (List<Map<String, Object>>) resultMap.get(key);
				} else {
					shootLocationList = new ArrayList<Map<String,Object>>();
					resultMap.put(key, shootLocationList);
					shootLocationFlag = "-1";
				}
				List<Map<String, Object>> dayList = null;
				if(StringUtil.isNotBlank(shootLocation) && shootLocationFlag.equals(shootLocation)) {
					if(shootLocationList.size() > 0) {
						Map<String, Object> oneShootLocation = shootLocationList.get(shootLocationList.size() - 1);
						dayList = (List<Map<String, Object>>) oneShootLocation.get("dayList");
					}
				} else {
					shootLocationFlag = shootLocation;
					dayList = new ArrayList<Map<String,Object>>();
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("shootLocation", shootLocation);
					map.put("dayList", dayList);
					shootLocationList.add(map);
				}
				dayList.add(one);
			}
		}
		
		return resultMap;
	}
	
	/** 
     * 十以下前补0 
     * @param num 
     * @return 
     */  
    private static String getValue(int num){  
        return String.valueOf(num>9?num:("0"+num));  
    }
	
	/**
	 * 查询关注项汇总信息
	 * @param crewId
	 * @param attention
	 */
	public List<Map<String, Object>> queryAttentionTotalInfo(String crewId, String attention) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		if(StringUtils.isNotBlank(attention)) {
			//判断关注条件
			String[] attentionArray = attention.split(",");
			for(String one : attentionArray) {
				String[] arr = one.split(":", -1);
				Map<String, Object> map = this.scheduleViewMapDao.queryAttentionTotalInfo(crewId, arr[1], arr[0]);
				if(map != null) {
					resultList.add(map);
				}
			}
		}
		return resultList;
	}
	
	/**
	 * 查询计划详情
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryScheduleDetail(String crewId, Page page) {
		List<Map<String, Object>> resultList = this.scheduleDao.queryScheduleDetail(crewId, page);
		/*if(resultList != null && resultList.size() > 0) {
			String scheduleIds = "";
			for(Map<String, Object> one : resultList) {
				String scheduleId = (String) one.get("scheduleId");
				scheduleIds += scheduleId+",";
			}
			if(StringUtils.isNotBlank(scheduleIds)) {
				scheduleIds = scheduleIds.substring(0, scheduleIds.length() - 1);
			}			
			ViewFilter filter = new ViewFilter();
			filter.setSortField("planShootDate"); //排序标识
			filter.setScheduleIds(scheduleIds);
			List<Map<String, Object>> viewInfoList = this.queryViewList(crewId, null, filter);
			if(viewInfoList != null && viewInfoList.size() > 0) {
				for(Map<String, Object> one : resultList) {
					String key1 = one.get("planShootDate") + "|" + one.get("planShootGroup");
					List<Map<String, Object>> viewList = new ArrayList<Map<String,Object>>();
					for(int i = 0; i < viewInfoList.size(); i++) {
						Map<String, Object> map = viewInfoList.get(i);
						String key2 = map.get("planShootDate") + "|" + map.get("planGroupName");
						if(key1.equals(key2)) {
							viewList.add(map);
						}
					}
					one.put("viewInfoList", viewList);
				}
			}
		}*/
		return resultList;
	}
	
	/**
	 * 保存所有计划
	 * @param crewId 剧组ID
	 * @param scheduleGroupList 
	 * @param scheduleViewMapList
	 * @throws Exception
	 */
	public void saveAllSchedule(String crewId,
			List<ScheduleGroupModel> scheduleGroupList,
			List<ScheduleViewMapModel> scheduleViewMapList, boolean isDelete,
			boolean isCover) throws Exception {
		if(isDelete) {
			//清空之前的计划
			this.scheduleDao.deleteOne(crewId, "crewId", ScheduleGroupModel.TABLE_NAME);
			this.scheduleViewMapDao.deleteOne(crewId, "crewId", ScheduleViewMapModel.TABLE_NAME);
			//新增计划
			if(scheduleGroupList != null && scheduleGroupList.size() > 0) {
				this.scheduleDao.addBatch(scheduleGroupList, ScheduleGroupModel.class);
			}
			if(scheduleViewMapList != null && scheduleViewMapList.size() > 0) {
				this.scheduleViewMapDao.addBatch(scheduleViewMapList, ScheduleViewMapModel.class);
			}
		} else {
			if(scheduleViewMapList != null && scheduleViewMapList.size() > 0) {
				for(ScheduleViewMapModel one : scheduleViewMapList) {
					ScheduleViewMapModel scheduleViewMap = this.scheduleViewMapDao.queryDetailByViewId(crewId, one.getViewId());
					if(scheduleViewMap == null) {
						//新增计划场景关联关系
						this.scheduleViewMapDao.add(one);
						//新增计划分组
						ScheduleGroupModel scheduleGroup = this.scheduleDao.queryScheduleGroupById(crewId, one.getPlanGroupId());
						if(scheduleGroup == null) {
							if(scheduleGroupList != null && scheduleGroupList.size() > 0) {
								for(ScheduleGroupModel map : scheduleGroupList) {
									if(map.getId().equals(one.getPlanGroupId())) {
										this.scheduleDao.add(map);
									}
								}
							}
						}
					} else {
						if(isCover) { //覆盖
							//更新计划场景关联关系
							String oldPlanGroupId = scheduleViewMap.getPlanGroupId();
							one.setId(scheduleViewMap.getId());
							this.scheduleViewMapDao.updateWithNull(one, "id");
							//新增计划分组
							ScheduleGroupModel scheduleGroup = this.scheduleDao.queryScheduleGroupById(crewId, one.getPlanGroupId());
							if(scheduleGroup == null) {
								if(scheduleGroupList != null && scheduleGroupList.size() > 0) {
									for(ScheduleGroupModel map : scheduleGroupList) {
										if(map.getId().equals(one.getPlanGroupId())) {
											this.scheduleDao.add(map);
										}
									}
								}
							}
							//判断原来的计划分组是否还有场景，没有则删掉
							List<Map<String, Object>> list = this.scheduleViewMapDao.queryMaxSequenceByPlanGroupId(crewId, oldPlanGroupId);
							if(list != null && list.size() > 0) {
								if((Integer) list.get(0).get("sequence") == null) {
									this.scheduleViewMapDao.deleteOne(oldPlanGroupId, "id", ScheduleGroupModel.TABLE_NAME);
								}
							}
						}
					}
				}
			}
		}
	}
}
