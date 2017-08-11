package com.xiaotu.makeplays.mobile.server.community;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.xiaotu.makeplays.community.model.ReportInfoModel;
import com.xiaotu.makeplays.community.model.StoreInfoModel;
import com.xiaotu.makeplays.community.model.TeamInfoModel;
import com.xiaotu.makeplays.community.model.TeamPositionInfoModel;
import com.xiaotu.makeplays.community.model.TeamResumeMapModel;
import com.xiaotu.makeplays.community.model.constants.TeamStatus;
import com.xiaotu.makeplays.community.service.ReportTeamService;
import com.xiaotu.makeplays.community.service.StoreInfoSrevice;
import com.xiaotu.makeplays.community.service.TeamInfoService;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.mobile.server.common.BaseFacade;
import com.xiaotu.makeplays.mobile.server.community.dto.TeamInfoDto;
import com.xiaotu.makeplays.mobile.server.community.dto.TeamResumePositionDto;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.PropertiesUitls;

/**
 * 组讯基本信息操作的controller
 * 
 * @author wanrenyi 2016年9月1日下午6:16:33
 */
@Controller
@RequestMapping("/interface/teaminfo")
public class TeamInfoFacade extends BaseFacade {

	Logger logger = LoggerFactory.getLogger(TeamInfoFacade.class);
	
	@Autowired
	private TeamInfoService teamInfoService;
	
	@Autowired
	private ReportTeamService reportService;
	
	@Autowired
	private StoreInfoSrevice storeService;
	
	@Autowired
	private CrewInfoService crewInfoService;

	/**
	 * 保存组讯信息
	 * @param teamId 组讯id
	 * @param createUser 用户id
	 * @param crewName 剧组名称
	 * @param crewType 剧组类别
	 * @param subject 题材
	 * @param company 公司
	 * @param shootStartDate 开机时间
	 * @param shootEndDate 杀青时间
	 * @param shootLocation 拍摄地点
	 * @param contactName 联系人
	 * @param phoneNum 联系电话
	 * @param email 邮箱
	 * @param contactAddress 筹备地址(联系地址)
	 * @param crewComment 剧组简介
	 * @param file 上传的图片
	 * @param scriptWriter 编剧
	 * @param director 导演
	 * @throws IllegalArgumentException 
	 */
	 @ResponseBody
	 @RequestMapping("/saveTeamInfo")
	public Object saveTeamInfo(HttpServletRequest request, String teamId, String createUser, String crewName,
			Integer crewType, String subject, String company, String shootStartDate, String shootEndDate, 
			String shootLocation, String contactName, String phoneNum, String email, String contactAddress, 
			String crewComment, MultipartFile file, String scriptWriter, String director){
			
		Map<String, Object> resultMap = new HashMap<String, Object>();
		TeamInfoModel teamInfo = new TeamInfoModel();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			
			if (StringUtils.isBlank(createUser)) {
				throw new IllegalArgumentException("请确定要发布组讯的用户!");
			}
			if (StringUtils.isBlank(crewName)) {
				throw new IllegalArgumentException("剧组名称不能为空!");
			}
			if (crewType == null) {
				throw new IllegalArgumentException("请选择剧组类型!");
			}
			
			//在添加或则更新组讯之前需要根据组讯中剧组的名称和类别判断当前的组讯是否重复
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("createUser", createUser);
			conditionMap.put("crewName", crewName);
			conditionMap.put("crewType", crewType);
			conditionMap.put("teamId", teamId);
			conditionMap.put("status", TeamStatus.TeamAvailable.getValue());
			boolean flag = teamInfoService.isRepeatTeam(conditionMap);
			if (flag) {
				throw new IllegalArgumentException("当前剧组已经发布组讯信息,请更换剧组后重新发布!");
			}
			
			//设置默认图片地址
			String storePath = "";
			//封装剧组宣传图片
			if (file != null) {
				storePath = this.uploadCrewPic(file);
				teamInfo.setPicPath(storePath);
			}else {
				teamInfo.setPicPath(this.crewInfoService.genDefaultPic(crewName));
			}
			//用户id
			teamInfo.setCreateUser(createUser);
			//剧组名称
			teamInfo.setCrewName(crewName);
			//剧组类型
			teamInfo.setCrewType(crewType);
			//题材
			teamInfo.setSubject(subject);
			//制片公司
			teamInfo.setCompany(company);
			
			if (StringUtils.isNotBlank(shootStartDate)) {
				//开机时间
				teamInfo.setShootStartDate(sdf.parse(shootStartDate));
			}
			
			if (StringUtils.isNotBlank(shootEndDate)) {
				//杀青时间
				teamInfo.setShootEndDate(sdf.parse(shootEndDate));
			}
			
			//拍摄地点
			teamInfo.setShootlocation(shootLocation);
			//联系人
			teamInfo.setContactname(contactName);
			//联系电话
			teamInfo.setPhoneNum(phoneNum);
			//邮箱
			teamInfo.setEmail(email);
			//联系地址
			teamInfo.setContactAddress(contactAddress);
			//剧组简介
			teamInfo.setCrewComment(crewComment);
			//编剧
			teamInfo.setScriptWriter(scriptWriter);
			//导演
			teamInfo.setDirector(director);
			
			//当id为空时,表示是新增组讯;不为空时表示进行更新操作
			if (StringUtils.isBlank(teamId)) {
				teamId = teamInfoService.addTeamInfoByBean(teamInfo);
				
				this.sysLogService.saveSysLogForApp(request, "新增组讯", this.getClientType(createUser), TeamInfoModel.TABLE_NAME, null, 1);
			}else {
				teamInfo.setTeamId(teamId);
				teamInfoService.updateTeamInfoByBean(teamInfo);
				
				this.sysLogService.saveSysLogForApp(request, "修改组讯", this.getClientType(createUser), TeamInfoModel.TABLE_NAME, teamId, 2);
			}
			resultMap.put("teamId", teamId);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);

			this.sysLogService.saveSysLogForApp(request, "保存组讯失败：" + e.getMessage(), this.getClientType(createUser), TeamInfoModel.TABLE_NAME, teamId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知错误！");
		}
		
		return resultMap;
	}
	
	/**判断组讯中的剧组是否重复
	 * @param createUser 用户id
	 * @param crewName 剧组名称
	 * @param crewType 剧组类型
	 * @return
	 */
	 @ResponseBody
	 @RequestMapping("/isReapeatTeam")
	public Object isReapeatTeamInfo(String createUser, String crewName, Integer crewType){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(createUser)) {
				throw new IllegalArgumentException("请选择用户!");
			}
			if (StringUtils.isBlank(crewName)) {
				throw new IllegalArgumentException("请填写剧组名称!");
			}
			if (crewType == null ||crewType == 0) {
				throw new IllegalArgumentException("请选择剧组类型!");
			}
			//在添加或则更新组讯之前需要根据组讯中剧组的名称和类别判断当前的组讯是否重复
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("createUser", createUser);
			conditionMap.put("crewName", crewName);
			conditionMap.put("crewType", crewType);
			conditionMap.put("status", TeamStatus.TeamAvailable.getValue());
			boolean flag = teamInfoService.isRepeatTeam(conditionMap);
			
			resultMap.put("isRepeat", flag);
		} catch (IllegalArgumentException ie){
			logger.error(ie.getMessage());
			
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			
			throw new IllegalArgumentException("未知错误！");
		}
		
		return resultMap;
	}
	
	/**根据组讯id删除组讯信息,同时会删除组讯关联的招聘职位信息(逻辑删除)
	 * @param teamId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteTeamInfo")
	public Object deleteTeamInfo(HttpServletRequest request, String teamId, String createUser){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(createUser)) {
				throw new IllegalArgumentException("请登录后再进行删除操作!");
			}
			if (StringUtils.isBlank(teamId)) {
				throw new IllegalArgumentException("请选择要删除的组讯!");
			}
			
			teamInfoService.deleteTeamInfoById(teamId, createUser);
			
			this.sysLogService.saveSysLogForApp(request, "删除组讯信息", this.getClientType(createUser), 
					TeamInfoModel.TABLE_NAME + "," + TeamPositionInfoModel.TABLE_NAME, teamId, 3);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage());
			
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());

			this.sysLogService.saveSysLogForApp(request, "删除组讯信息失败：" + e.getMessage(), this.getClientType(createUser), 
					TeamInfoModel.TABLE_NAME + "," + TeamPositionInfoModel.TABLE_NAME, teamId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知错误！");
		}		
	
		return resultMap;
	}
	
	
	/**
	 * 保存组讯招聘职位信息接口
	 * @param positionId 职位id
	 * @param teamId 组讯id
	 * @param createUser 用户id(创建人id)
	 * @param positonName 职位名称
	 * @param needPeopleNum 招聘人数
	 * @param positionRequirement 职位简介
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/savePositionInfo")
	public Object savePositionInfo(HttpServletRequest request, String positionId, String teamId, String createUser,
			String positonName, String needPositionId, Integer needPeopleNum, String positionRequirement){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		TeamPositionInfoModel positionModel = new TeamPositionInfoModel();
		
		try {
			if (StringUtils.isBlank(createUser)) {
				throw new IllegalArgumentException("请选择需要保存的用户!");
			}
			 if (StringUtils.isBlank(teamId)) {
				 throw new IllegalArgumentException("请选择组讯信息!");
			}
			if (StringUtils.isBlank(needPositionId)) {
				throw new IllegalArgumentException("请选择招聘职位名称!");
			}
			if (needPeopleNum == null || needPeopleNum == 0) {
				throw new IllegalArgumentException("请填写招聘人数！");
			}
			if (StringUtils.isBlank(positionRequirement)) {
				throw new IllegalArgumentException("请填写招聘要求！");
			}
			if (positionRequirement.length() > 1000) {
				throw new IllegalArgumentException("招聘要求最多支持1000个字");
			}
			
			//判断当前是修改还是新增
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			if (StringUtils.isNotBlank(positionId)) { //修改
				conditionMap.put("positionId", positionId);
				//先根据id查询出当前招聘职位的信息
				List<TeamPositionInfoModel> list = teamInfoService.queryTeamPositionListByConfition(conditionMap);
				if (list != null && list.size()>0) {
					TeamPositionInfoModel model = list.get(0);
					model.setNeedPeopleNum(needPeopleNum);
					model.setNeedPositionId(needPositionId);
					model.setPositionName(positonName);
					model.setPositionRequirement(positionRequirement);
					//修改数据
					teamInfoService.updateTeamPositionByBean(model);
				}
				this.sysLogService.saveSysLogForApp(request, "修改组讯招聘职位信息", this.getClientType(createUser), 
						TeamPositionInfoModel.TABLE_NAME, teamId, 2);
			}else {
				//新增操作
				//对当前保存的职位名称,进行判断,如果已经保存,则不能再次保存相同的职务
				conditionMap.put("teamId", teamId);
				conditionMap.put("createUser", createUser);
				conditionMap.put("needPositionId", needPositionId);
				conditionMap.put("status", TeamStatus.TeamAvailable.getValue());
				List<TeamPositionInfoModel> positionList = teamInfoService.queryTeamPositionListByConfition(conditionMap);
				
				if (positionList != null && positionList.size()>0) {
					throw new IllegalArgumentException("当前职位信息已经添加,请不要重复添加!");
				}
				
				positionModel.setTeamId(teamId);
				positionModel.setCreateUser(createUser);
				positionModel.setPositionName(positonName);
				positionModel.setNeedPeopleNum(needPeopleNum);
				positionModel.setPositionRequirement(positionRequirement);
				positionModel.setNeedPositionId(needPositionId);
				
				teamInfoService.addTeamPositionByBean(positionModel);
				this.sysLogService.saveSysLogForApp(request, "新增组讯招聘职位信息", this.getClientType(createUser), 
						TeamPositionInfoModel.TABLE_NAME, positionModel.getTeamId(), 1);
			}
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage());
			
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());

			this.sysLogService.saveSysLogForApp(request, "保存组讯招聘职位信息失败：" + e.getMessage(), this.getClientType(createUser), 
					TeamPositionInfoModel.TABLE_NAME, teamId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException(e.getMessage());
		}
		
		return resultMap;
	}
	
	/**
	 * 删除组讯招聘职位信息(根据职位的id进行删除,逻辑删除)
	 * @param createUser 用户id
	 * @param teamId 组讯id
	 * @param positionId 职位id
	 */
	@ResponseBody
	@RequestMapping("/deleteTeamPosition")
	public Object deleteTeamPosition(HttpServletRequest request, String createUser, String teamId, String positionId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(createUser)) {
				throw new IllegalArgumentException("请登录后在进行删除操作!");
			}
			if (StringUtils.isBlank(positionId)) {
				throw new IllegalArgumentException("请选择要删除的职位!");
			}
			if (StringUtils.isBlank(teamId)) {
				throw new IllegalArgumentException("请选择组讯!");
			}
			
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("positionId", positionId);
			conditionMap.put("createUser", createUser);
			teamInfoService.deleteTeamPositionByCondition(conditionMap);
			
			this.sysLogService.saveSysLogForApp(request, "删除组讯招聘职位信息(逻辑删除)", 
					this.getClientType(createUser), TeamPositionInfoModel.TABLE_NAME, teamId + "," + positionId, 3);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage());
			
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());

			this.sysLogService.saveSysLogForApp(request, "删除组讯招聘职位信息(逻辑删除)失败：" + e.getMessage(), 
					this.getClientType(createUser), TeamPositionInfoModel.TABLE_NAME, teamId + "," + positionId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知错误！");
		}
		return resultMap;
	}
	
	
	/**
	 * 获取组讯列表
	 * @param page 分页参数对象
	 * @param createUser 用户id
	 * @param crewType 剧组类型
	 * @param subject 题材
	 * @param shootStartType 开机时间类型
	 * @param createTimeType 发布时间类型
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainTeamList")
	public Object obtainTeamList(Integer pageSize, Integer pageNo, String createUser, String storeUserId, Integer crewType, String subject,
				Integer shootStartType, Integer createTimeType){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("status", TeamStatus.TeamAvailable.getValue());
			if (StringUtils.isNotBlank(createUser)) {
				conditionMap.put("createUser", createUser);
			}
			if (StringUtils.isNotBlank(storeUserId)) {
				conditionMap.put("storeUserId", storeUserId);
			}
			if (crewType != null ) {
				conditionMap.put("crewType", crewType);
			}
			if (StringUtils.isNotBlank(subject)) {
				conditionMap.put("subject", subject);
			}
			if (shootStartType != null && shootStartType != 0) {
				conditionMap.put("shootStartType", shootStartType);
			}
			if (createTimeType != null && createTimeType != 0) {
				conditionMap.put("createTimeType", createTimeType);
			}
			if (pageSize == null) {
				pageSize = 20;
			}
			if (pageNo == null) {
				pageNo = 1;
			}
			
			Page page = new Page();
			page.setPagesize(pageSize);
			page.setPageNo(pageNo);
			List<TeamInfoDto> teamInfoList = teamInfoService.getTeamInfoList(conditionMap, page);
			
			resultMap.put("teamInfoList", teamInfoList);
			resultMap.put("pageCount", page.getPageCount());
		} catch (Exception e) {
			logger.error(e.getMessage());
			
			throw new IllegalArgumentException("位置错误");
		}
		
		return resultMap;
	}
	
	/**
	 * 获取组讯详情信息 
	 * @param teamId 组讯id
	 * @param createUser 用户id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainTeamInfo")
	public Object obtainTeamInfo(String teamId, String createUser){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			if (StringUtils.isBlank(teamId)) {
				throw new IllegalArgumentException("请选择要查看的组讯!");
			}
			
			//当用户id为空时不用返回 是否收藏以及是否投递简历两个参数
			if (StringUtils.isNotBlank(createUser)) {
				//是否申请
				boolean applyTeam = teamInfoService.isApplyTeam(teamId, createUser);
				//是否收藏
				boolean storeTeam = teamInfoService.isStoreTeam(teamId, createUser);
				resultMap.put("isApply", applyTeam);
				resultMap.put("isStore", storeTeam);
			}
			
			//根据id查询组讯对象信息
			TeamInfoModel teamInfoModel = teamInfoService.getTeamInfoById(teamId);
			resultMap.put("teamInfoModel", teamInfoModel);
			
			//查询组讯下的招聘职位信息列表
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("teamId", teamId);
			conditionMap.put("status", TeamStatus.TeamAvailable.getValue());
			List<TeamPositionInfoModel> list = teamInfoService.queryTeamPositionListByConfition(conditionMap);
			//取出每个职位的投递简历人数
			List<Map<String,Object>> positionCount = teamInfoService.queryTeamPositionCount(teamId);
			
			List<TeamResumePositionDto> dtoList = new ArrayList<TeamResumePositionDto>();
			for (TeamPositionInfoModel model : list) {
				TeamResumePositionDto dto = new TeamResumePositionDto();
				BeanUtils.copyProperties(model, dto);
				//取出每一条招聘职位的投递简历人数
				for (Map<String,Object> map : positionCount) {
					String positionId = (String) map.get("positionId");
					if (positionId.equals(model.getPositionId())) {
						dto.setResumeCount((Long) map.get("count"));
						break;
					}else {
						dto.setResumeCount(0);
					}
				}
				dtoList.add(dto);
			}
			
			resultMap.put("positionInfoList", dtoList);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage());
			
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			
			throw new IllegalArgumentException("未知错误！", e);
		}
		
		return resultMap;
	}
	
	/**
	 * 投递简历
	 * @param userId
	 * @param teamId
	 */
	@ResponseBody
	@RequestMapping("/addApplyTeamInfo")
	public Object addApplyTeamInfo(HttpServletRequest request, String userId, String teamId, String positionId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(userId)) {
				throw new IllegalArgumentException("请先登录,再投递简历!");
			}
			if (StringUtils.isBlank(positionId) || StringUtils.isBlank(teamId)) {
				throw new IllegalArgumentException("请选择要投递的职位!");
			}
			
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("userId", userId);
			conditionMap.put("positionId", positionId);
			List<TeamResumeMapModel> list = teamInfoService.queryResumeRecoder(conditionMap);
			if (list != null && list.size()>0) {
				throw new IllegalArgumentException("您已向该岗位投递过简历，不可重复投递！");
			}
			
			TeamResumeMapModel model = new TeamResumeMapModel();
			model.setTeamId(teamId);
			model.setUserId(userId);
			model.setPositionId(positionId);
			teamInfoService.addAppleTeamMap(model);
			
			this.sysLogService.saveSysLogForApp(request, "投递简历", this.getClientType(userId), TeamResumeMapModel.TABLE_NAME, teamId + "," + positionId, 1);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage());
			
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());

			this.sysLogService.saveSysLogForApp(request, "投递简历失败：" + e.getMessage(), this.getClientType(userId), TeamResumeMapModel.TABLE_NAME, teamId + "," + positionId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知错误");
		}
		return resultMap;
	}
	
	/**
	 * 添加举报信息
	 * @param userId
	 * @param teamId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/addReportInfo")
	public Object addReportInfo(HttpServletRequest request, String userId, String teamId, Integer reportType, String reportComment){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			if (StringUtils.isBlank(teamId)) {
				throw new IllegalArgumentException("请选择要举报的组讯!");
			}
			if (reportType == null || reportType == 0) {
				throw new IllegalArgumentException("请选择举报类型!");
			}
			
			ReportInfoModel reportModel = new ReportInfoModel();
			reportModel.setTeamId(teamId);
			if (StringUtils.isBlank(userId)) {
				reportModel.setUserId("");
			}else {
				reportModel.setUserId(userId);
			}
			
			reportModel.setReportType(reportType);
			reportModel.setReportComment(reportComment);
			
			String reportId = reportService.addReportTeam(reportModel);
			
			resultMap.put("reportId", reportId);
			
			this.sysLogService.saveSysLogForApp(request, "举报组讯", this.getClientType(userId), ReportInfoModel.TABLE_NAME, teamId, 1);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage());
			
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());

			this.sysLogService.saveSysLogForApp(request, "举报组讯失败：" + e.getMessage(), this.getClientType(userId), ReportInfoModel.TABLE_NAME, teamId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知错误！");
		}
		
		return resultMap;
	}
	
	/**
	 * 添加或取消收藏
	 * @param userId 用户id
	 * @param teamId 组讯id
	 */
	@ResponseBody
	@RequestMapping("/addOrCancleStore")
	public Object addOrCancleStore(HttpServletRequest request, String userId, String teamId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(userId)) {
				throw new IllegalArgumentException("请登录后再收藏组讯!");
			}
			if (StringUtils.isBlank(teamId)) {
				throw new IllegalArgumentException("请选择要收藏的组讯!");
			}
			
			//封装参数
			StoreInfoModel model = new StoreInfoModel();
			model.setUserId(userId);
			model.setTeamId(teamId);
			
			this.storeService.addOrCancleStore(model);
			
			this.sysLogService.saveSysLogForApp(request, "添加或取消收藏组讯", this.getClientType(userId), StoreInfoModel.TABLE_NAME, teamId, 1);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage());
			
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());

			this.sysLogService.saveSysLogForApp(request, "添加或取消收藏组讯失败：" + e.getMessage(), this.getClientType(userId), StoreInfoModel.TABLE_NAME, teamId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知错误！");
		}
		
		return resultMap;
	}
	
	/**
	 * 辅助方法:上传剧组宣传图片
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws FileUploadException
	 */
	private String uploadCrewPic(MultipartFile file) throws FileNotFoundException, IOException, FileUploadException {
		Properties properties = PropertiesUitls.fetchProperties("/config.properties");
		String baseStorePath = properties.getProperty("fileupload.path");
		String storePath = baseStorePath + "teamInfo";
		
		//把附件上传到服务器(高清原版)
        Map<String, String> fileMap = FileUtils.uploadFile(file, false, storePath);
		String hdStorePath = fileMap.get("storePath");
		String storeName = fileMap.get("fileStoreName");
		
		return hdStorePath + storeName;
	}
}
