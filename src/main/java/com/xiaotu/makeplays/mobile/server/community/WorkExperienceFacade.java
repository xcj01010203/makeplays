package com.xiaotu.makeplays.mobile.server.community;

import java.text.SimpleDateFormat;
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

import com.xiaotu.makeplays.community.model.WorkExperienceInfoModel;
import com.xiaotu.makeplays.community.service.WorkExperienceInfoService;
import com.xiaotu.makeplays.mobile.server.common.BaseFacade;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.model.UserInfoModel;

/**
 * 工作经历的controller
 * @author wanrenyi 2016年9月5日上午10:18:11
 */
@Controller
@RequestMapping("/interface/work")
public class WorkExperienceFacade extends BaseFacade{

	Logger logger = LoggerFactory.getLogger(WorkExperienceInfoModel.class);
	
	@Autowired
	private WorkExperienceInfoService workService;
	
	/**
	 * 保存工作经历
	 * @param createUser 用户id
	 * @param experienceId 工作经历id
	 * @param crewName 剧组名称
	 * @param positionId 职位id,多个职位之间以","分隔
	 * @param positionName 职位名称,多个职位之间以"," 分隔
	 * @param joinCrewDate 入组时间
	 * @param leaveCrewDate 离组时间
	 * @param workrequirement 工作职责(100字以内)
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveWorkExperience")
	public Object saveWorkExperience(HttpServletRequest request,
			String createUser, String experienceId, String crewName,
			String positionId, String positionName, String joinCrewDate,
			String leaveCrewDate, String workrequirement) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			if (StringUtils.isBlank(createUser)) {
				throw new IllegalArgumentException("请您登陆后再添加工作经历!");
			}
			if (StringUtils.isBlank(crewName)) {
				throw new IllegalArgumentException("工作经历中剧组名称不能为空!");
			}
			if (StringUtils.isBlank(positionId)) {
				throw new IllegalArgumentException("请选择您的工作职责!");
			}
			if (StringUtils.isBlank(joinCrewDate)) {
				throw new IllegalArgumentException("请填写入组时间！");
			}
			if (StringUtils.isBlank(leaveCrewDate)) {
				throw new IllegalArgumentException("请填写离组时间！");
			}
			
			//封装参数
			WorkExperienceInfoModel model = new WorkExperienceInfoModel();
			model.setCreateUser(createUser);
			model.setCrewName(crewName);
			model.setPositionId(positionId);
			model.setPositionName(positionName);
			model.setJoinCrewDate(sdf.parse(joinCrewDate));
			model.setLeaveCrewDate(sdf.parse(leaveCrewDate));
			model.setWorkrequirement(workrequirement);
			
			//判断当前经历的id若为空时表示新增,不为空时表示修改操作
			if (StringUtils.isBlank(experienceId)) {
				experienceId = this.workService.addWorkExperByBean(model);
				
				this.sysLogService.saveSysLogForApp(request, "新增工作经历", this.getClientType(createUser), WorkExperienceInfoModel.TABLE_NAME, experienceId, 1);
			}else {
				model.setExperienceId(experienceId);
				this.workService.updateWorkExper(model);
				
				this.sysLogService.saveSysLogForApp(request, "修改工作经历", this.getClientType(createUser), WorkExperienceInfoModel.TABLE_NAME, experienceId, 2);
			}
			
			resultMap.put("experienceId", experienceId);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage());
			
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());

			this.sysLogService.saveSysLogForApp(request, "保存工作经历失败：" + e.getMessage(), this.getClientType(createUser), WorkExperienceInfoModel.TABLE_NAME, experienceId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知错误！");
		}
		
		return resultMap;
	}
	
	/**
	 * 删除工作经历
	 * @param experienceId
	 * @param userId
	 */
	@ResponseBody
	@RequestMapping("/deleteWorkExper")
	public Object deleteWorkExper(HttpServletRequest request, String experienceId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(userId)) {
				throw new IllegalArgumentException("请先登录在进行操作!");
			}
			if (StringUtils.isBlank(experienceId)) {
				throw new IllegalArgumentException("请选择要删除的工作经历!");
			}
			
			this.workService.deleteWorkExper(userId, experienceId);
			
			this.sysLogService.saveSysLogForApp(request, "删除工作经历", this.getClientType(userId), WorkExperienceInfoModel.TABLE_NAME, experienceId, 3);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage());
			
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());

			this.sysLogService.saveSysLogForApp(request, "删除工作经历失败：" + e.getMessage(), this.getClientType(userId), WorkExperienceInfoModel.TABLE_NAME, experienceId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知错误！");
		}
		return resultMap;
	}
	
	/**
	 * 根据用户id查询出用户的详细信息和用户的工作经历列表
	 * @param userId 用户id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainWorkExperAndUserInfo")
	public Object obtainWorkExperAndUserInfo(HttpServletRequest request, String userId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			if (StringUtils.isBlank(userId)) {
				throw new IllegalArgumentException("请登录后再进行查询!");
			}
			
			//获取用户的个人信息
			UserInfoModel userInfoModel = this.workService.getUserInfoById(userId);
			resultMap.put("userInfo", userInfoModel);
			
			//获取用户的工作经历列表
			List<Map<String, Object>> list = this.workService.getWorkExListByUserId(userId);
			resultMap.put("workList", list);
			
			this.sysLogService.saveSysLogForApp(request, "查询个人简历列表", userInfoModel.getClientType(), 
					UserInfoModel.TABLE_NAME + "," + WorkExperienceInfoModel.TABLE_NAME, null, 0);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage());
			
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			
			throw new IllegalArgumentException("未知错误！");
		}
		
		return resultMap;
	}
	
}
