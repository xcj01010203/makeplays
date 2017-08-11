package com.xiaotu.makeplays.community.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.community.dao.WorkExperienceInfoDao;
import com.xiaotu.makeplays.community.model.WorkExperienceInfoModel;
import com.xiaotu.makeplays.crew.dao.CrewInfoDao;
import com.xiaotu.makeplays.user.dao.UserInfoDao;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 工作经历操作的service
 * @author wanrenyi 2016年9月5日上午10:08:21
 */
@Service
public class WorkExperienceInfoService {

	@Autowired
	private WorkExperienceInfoDao workDao;
	
	@Autowired
	private UserInfoDao userInfoDao;
	
	@Autowired
	private CrewInfoDao  crewInfoDao;
	
	/**
	 * 保存工作经历
	 * @param model
	 * @return
	 * @throws Exception 
	 */
	public String addWorkExperByBean(WorkExperienceInfoModel model) throws Exception {
		String experId = UUIDUtils.getId();
		model.setExperienceId(experId);
		this.workDao.addWorkExperByBean(model);
		
		return experId;
	}
	
	/**
	 * 更新工作经历
	 * @param model
	 * @throws Exception 
	 */
	public void updateWorkExper(WorkExperienceInfoModel model) throws Exception {
		this.workDao.updateWorkExperByBean(model);
	}
	
	/**
	 * 根据id删除工作经历信息
	 * @param exprienceId
	 * @throws Exception 
	 */
	public void deleteWorkExper(String userId, String exprienceId) throws Exception {
		//删除工作经历之前应该先判断当前的工作经历是否存在,只有存在时,才能进行删除
		WorkExperienceInfoModel infoModel = this.workDao.getOneWorkExById(userId, exprienceId);
		if (infoModel == null ) {
			throw new IllegalArgumentException("您要删除的信息不存在!");
		}
		
		this.workDao.deleteWorkExper(exprienceId);
	}
	
	/**
	 * 根据用户id查询出当前用户的工作经历列表
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	public List<Map<String, Object>> getWorkExListByUserId(String userId) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		List<Map<String, Object>> modelList = new ArrayList<Map<String,Object>>();
		
		List<WorkExperienceInfoModel> list = this.workDao.getWorkExperListByUserId(userId);
		for (WorkExperienceInfoModel workInfoModel : list) {
			Map<String, Object> modelMap = new HashMap<String, Object>();
			modelMap.put("experienceId", workInfoModel.getExperienceId());
			modelMap.put("createUser", workInfoModel.getCreateUser());
			modelMap.put("crewName", workInfoModel.getCrewName());
			modelMap.put("positionId", workInfoModel.getPositionId());
			modelMap.put("positionName", workInfoModel.getPositionName());
			modelMap.put("joinCrewDate", sdf.format(workInfoModel.getJoinCrewDate()));
			modelMap.put("leaveCrewDate", sdf.format(workInfoModel.getLeaveCrewDate()));
			modelMap.put("workrequirement", workInfoModel.getWorkrequirement());
			modelMap.put("createTime", workInfoModel.getCreateTime());
			modelMap.put("allowUpdate", "");
			modelList.add(modelMap);
		}
		
		//查询用户在系统中所在的剧组
		List<Map<String, Object>> crewList = this.crewInfoDao.queryUserCrewList(userId);
		for (Map<String, Object> map : crewList) {
			//取出状态
			int status = (Integer) map.get("crewUserStatus");
			if (status == 1) {
				Map<String, Object> crewModelMap = new HashMap<String, Object>();
				crewModelMap.put("allowUpdate", "update");
				crewModelMap.put("createUser", userId);
				crewModelMap.put("crewName", (String)map.get("crewName"));
				crewModelMap.put("positionName", (String)map.get("roleNames"));
				Date shootStartDate = (Date) map.get("shootStartDate");
				if (shootStartDate == null) {
					crewModelMap.put("joinCrewDate", "");
				}else {
					crewModelMap.put("joinCrewDate", sdf.format(shootStartDate));
				}
				
				Date shootEndDate = (Date) map.get("shootEndDate");
				if (shootEndDate == null ) {
					crewModelMap.put("leaveCrewDate", "");
				}else {
					crewModelMap.put("leaveCrewDate", sdf.format(shootEndDate));
				}
				
				modelList.add(crewModelMap);
			}
		
		}
		
		return modelList;
	}
	
	/**
	 * 根据用户id查询出用户的详细信息
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	public UserInfoModel getUserInfoById(String userId) throws Exception {
		//查询个人的详细信息,当没有数据时,提示错误信息
		UserInfoModel userInfoModel = this.userInfoDao.queryById(userId);
		if (userInfoModel == null) {
			throw new IllegalArgumentException("当前用户尚未注册,不能查询!");
		}
		
		return userInfoModel;
	}
}
