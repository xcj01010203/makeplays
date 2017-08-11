package com.xiaotu.makeplays.community.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.community.model.TeamInfoModel;
import com.xiaotu.makeplays.community.model.TeamPositionInfoModel;
import com.xiaotu.makeplays.community.model.constants.TeamStatus;
import com.xiaotu.makeplays.community.service.TeamInfoService;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.mobile.server.community.dto.TeamInfoDto;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.model.constants.UserClientType;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 组讯
 * @author xuchangjian 2016-12-15上午9:48:46
 */
@Controller
@RequestMapping("/teamInfoManager")
public class TeamInfoController extends BaseController {
	
	Logger logger = LoggerFactory.getLogger(TeamInfoController.class);

	@Autowired
	private TeamInfoService teamInfoService;
	
	@Autowired
	private CrewInfoService crewInfoService;
	
	private int terminal = UserClientType.PC.getValue();
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * 跳转到添加组讯页面
	 * @return
	 */
	@RequestMapping("/toAddTeamInfoPage")
	public ModelAndView toAddTeamInfoPage() {
		ModelAndView mv = new ModelAndView("/team/addTeamInfo");
		return mv;
	}
	
	/**
	 * 跳转到社区管理页面
	 * @return
	 */
	@RequestMapping("/toCommunityPage")
	public ModelAndView toCommunityPage(Integer teamType) {
		ModelAndView mv = new ModelAndView();
		if(teamType == null || teamType == 1) {
			mv.setViewName("/team/teamInfoList");
		} else if (teamType == 2) {
			mv.setViewName("/team/searchTeamList");
		}
		return mv;
	}
	
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
	 * @param picFlag 图片标志位，true：有图片，false:没有
	 */
	 @ResponseBody
	 @RequestMapping("/saveTeamInfo")
	public Map<String, Object> saveTeamInfo(HttpServletRequest request, String teamId, String crewName,
			Integer crewType, String subject, String company, String shootStartDate, String shootEndDate, 
			String shootLocation, String contactName, String phoneNum, String email, String contactAddress, 
			String crewComment, String scriptWriter, String director, Boolean picFlag){
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			if (StringUtils.isBlank(crewName)) {
				throw new IllegalArgumentException("剧组名称不能为空!");
			}
			if (crewType == null) {
				throw new IllegalArgumentException("请选择剧组类型!");
			}
			if (StringUtils.isBlank(subject)) {
				throw new IllegalArgumentException("请选择拍摄题材!");
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String loginUserId = this.getLoginUserId(request);
			
			//在添加或者更新组讯之前需要根据组讯中剧组的名称和类别判断当前的组讯是否重复
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("createUser", loginUserId);
			conditionMap.put("crewName", crewName);
			conditionMap.put("crewType", crewType);
			conditionMap.put("teamId", teamId);
			boolean flag = teamInfoService.isRepeatTeam(conditionMap);
			if (flag) {
				throw new IllegalArgumentException("当前剧组已经发布组讯信息,请更换剧组后重新发布!");
			}
			
			TeamInfoModel teamInfo = new TeamInfoModel();
			
			if (StringUtils.isNotBlank(teamId)) {
				teamInfo = teamInfoService.getTeamInfoByIdWithoutFormatPicPath(teamId);
			}
			//设置默认图片地址
			String storePath = "";
			//封装剧组宣传图片
//			if (file != null) {
//				storePath = this.uploadCrewPic(file);
//				teamInfo.setPicPath(storePath);
//			} else {
			if(picFlag == null || !picFlag)
				teamInfo.setPicPath(this.crewInfoService.genDefaultPic(crewName));
//			}
			//用户id
			teamInfo.setCreateUser(loginUserId);
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

				this.sysLogService.saveSysLog(request, "添加组讯", terminal, TeamInfoModel.TABLE_NAME, teamId, SysLogOperType.INSERT.getValue());
			} else {
				teamInfo.setTeamId(teamId);
				teamInfoService.updateTeamInfoByBean(teamInfo);
				
				this.sysLogService.saveSysLog(request, "修改组讯", terminal, TeamInfoModel.TABLE_NAME, teamId, SysLogOperType.UPDATE.getValue());
			}
			
			resultMap.put("teamId", teamId);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			logger.error("未知异常，保存组讯失败", e);
			success = false;
			message = "未知异常，保存组讯失败";
			this.sysLogService.saveSysLog(request, "保存组讯失败：" + e.getMessage(), terminal, TeamInfoModel.TABLE_NAME, teamId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	 
	/**
	 * 保存组讯宣传图片
	 * @param request
	 * @param file
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveTeamInfoPic")
	public Map<String, Object> saveTeamInfoPic(HttpServletRequest request, String teamId, MultipartFile file){
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			if (StringUtils.isBlank(teamId)) {
				throw new IllegalArgumentException("请提供组讯信息!");
			}
			if(file == null) {
				throw new IllegalArgumentException("请选择文件!");
			}
			TeamInfoModel teamInfo = teamInfoService.getTeamInfoById(teamId);
			//删除原来的图片
			if(StringUtils.isNotEmpty(teamInfo.getPicPath())) {
				FileUtils.deleteFile(teamInfo.getPicPath());
			}
			//上传新的图片
			String storePath = this.uploadCrewPic(file);
			teamInfo.setPicPath(storePath);
			teamInfoService.updateTeamInfoByBean(teamInfo);
			
			resultMap.put("teamId", teamId);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			logger.error("未知异常，保存组讯失败", e);
			success = false;
			message = "未知异常，保存组讯失败";
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	 
 	/**
	 * 保存组训招聘职位信息接口
	 * @param positionId 职位id
	 * @param teamId 组训id
	 * @param createUser 用户id(创建人id)
	 * @param positonName 职位名称
	 * @param needPeopleNum 招聘人数
	 * @param positionRequirement 职位简介
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/savePositionInfo")
	public Map<String, Object> savePositionInfo(HttpServletRequest request, String positionId, String teamId,
			String positonName, String needPositionId, Integer needPeopleNum, String positionRequirement){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			if (StringUtils.isBlank(teamId)) {
				 throw new IllegalArgumentException("请选择组训信息!");
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
				throw new IllegalArgumentException("招聘要求做多支持1000个字");
			}
			
			String loginUserId = this.getLoginUserId(request);
			
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
				
			}else {
				//新增操作
			//对当前保存的职位名称,进行判断,如果已经保存,则不能再次保存相同的职务
			conditionMap.put("teamId", teamId);
			conditionMap.put("createUser", loginUserId);
			conditionMap.put("needPositionId", needPositionId);
			conditionMap.put("status", TeamStatus.TeamAvailable.getValue());
			List<TeamPositionInfoModel> positionList = teamInfoService.queryTeamPositionListByConfition(conditionMap);
			
			if (positionList != null && positionList.size()>0) {
				throw new IllegalArgumentException("当前职位信息已经添加,请不要重复添加!");
			}

			TeamPositionInfoModel positionModel = new TeamPositionInfoModel();
			positionModel.setTeamId(teamId);
			positionModel.setCreateUser(loginUserId);
			positionModel.setPositionName(positonName);
			positionModel.setNeedPeopleNum(needPeopleNum);
			positionModel.setPositionRequirement(positionRequirement);
			positionModel.setNeedPositionId(needPositionId);
			
			teamInfoService.addTeamPositionByBean(positionModel);
			
			}
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			logger.error("未知异常，保存职位失败", e);
			success = false;
			message = "未知异常，保存职位失败";
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	 
 	/**
	 * 保存组讯招聘职位信息接口
	 * @param teamId 组讯id
	 * @param positionStr 职位字符串
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveTeamPosition")
	public Map<String, Object> saveTeamPosition(HttpServletRequest request, String teamId, String positionStr){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			if (StringUtils.isBlank(teamId)) {
				 throw new IllegalArgumentException("请选择组讯信息!");
			}
			
			String userId = this.getLoginUserId(request);
			
			if(StringUtil.isNotBlank(positionStr)) {
				//判断当前是修改还是新增
				Map<String, Object> conditionMap = new HashMap<String, Object>();
				String[] positionStrs = positionStr.split("##");
				if(positionStrs != null && positionStrs.length > 0) {
					List<TeamPositionInfoModel> toAddTeamPositionList = new ArrayList<TeamPositionInfoModel>();//新增
					List<TeamPositionInfoModel> toUpdateTeamPositionList = new ArrayList<TeamPositionInfoModel>();//修改
					
					for(String onePositionStr : positionStrs) {
						String[] position = onePositionStr.split("\\$\\$", -1);
						TeamPositionInfoModel teamPositionInfo = new TeamPositionInfoModel();
						if(StringUtil.isBlank(position[0])) {
							
							conditionMap.put("teamId", teamId);
							conditionMap.put("createUser", userId);
							conditionMap.put("needPositionId", position[1]);
							conditionMap.put("status", TeamStatus.TeamAvailable.getValue());
							List<TeamPositionInfoModel> positionList = teamInfoService.queryTeamPositionListByConfition(conditionMap);
							
							if (positionList != null && positionList.size() > 0) {
								throw new IllegalArgumentException(position[2] + "职位信息已经添加,请不要重复添加!");
							}								
							
							teamPositionInfo.setPositionId(UUIDUtils.getId());
							teamPositionInfo.setCreateUser(userId);
							toAddTeamPositionList.add(teamPositionInfo);
						} else {
							teamPositionInfo.setPositionId(position[0]);
							toUpdateTeamPositionList.add(teamPositionInfo);
						}
						teamPositionInfo.setTeamId(teamId);
						teamPositionInfo.setNeedPositionId(position[1]);
						teamPositionInfo.setPositionName(position[2]);
						if(StringUtil.isBlank(position[3]) || Integer.parseInt(position[3]) == 0) {
							throw new IllegalArgumentException("请填写招聘人数！");
						} else {
							teamPositionInfo.setNeedPeopleNum(Integer.parseInt(position[3]));
						}
						if(position[4].length() > 1000) {
							throw new IllegalArgumentException("职务要求不能超过1000字！");
						} else {
							teamPositionInfo.setPositionRequirement(position[4]);
						}
					}
					teamInfoService.saveTeamPosition(toAddTeamPositionList, toUpdateTeamPositionList);
				}				
			}
			
			this.sysLogService.saveSysLog(request, "保存招募职位信息", terminal, "tab_team_position_info", teamId, SysLogOperType.INSERT.getValue());
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			logger.error("未知异常，保存职位失败", e);
			success = false;
			message = "未知异常，保存职位失败";
			this.sysLogService.saveSysLog(request, "保存招募职位信息失败：" + e.getMessage(), terminal, "tab_team_position_info", teamId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
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
	
	/**
	 * 获取组讯列表
	 * @param page 分页参数对象
	 * @param crewType 剧组类型
	 * @param subject 题材
	 * @param shootStartType 开机时间类型
	 * @param createTimeType 发布时间类型
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryTeamInfoList")
	public Map<String, Object> queryTeamInfoList(HttpServletRequest request,
			Page page, Integer crewType, Integer status, String subject,
			Integer shootStartType, Integer createTimeType) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			if (crewType != null ) {
				conditionMap.put("crewType", crewType);
			}
			if(status != null) {
				conditionMap.put("status", status);
				conditionMap.put("flag", true);
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
			
			List<TeamInfoDto> teamInfoList = teamInfoService.getTeamInfoList(conditionMap, page);
			
			resultMap.put("teamInfoList", teamInfoList);
			resultMap.put("total", page.getTotal());
			resultMap.put("pageCount", page.getPageCount());
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询组讯列表异常";
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 获取组讯详情信息 
	 * @param teamId 组讯id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryTeamInfo")
	public Map<String, Object> queryTeamInfo(HttpServletRequest request, String teamId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			if (StringUtils.isBlank(teamId)) {
				throw new IllegalArgumentException("请选择要查看的组讯!");
			}
			
			//根据id查询组讯对象信息
			TeamInfoModel teamInfoModel = teamInfoService.getTeamInfoById(teamId);
			resultMap.put("teamInfoModel", teamInfoModel);
			
			//查询组讯下的招聘职位信息列表
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("teamId", teamId);
			List<TeamPositionInfoModel> list = teamInfoService.queryTeamPositionListByConfition(conditionMap);
			
			//取出每个职位的投递人
			List<Map<String, Object>> positionUserList = teamInfoService.queryTeamInfoApply(teamId, null);
			
			List<Map<String, Object>> positionList = new ArrayList<Map<String,Object>>();
			if(list != null && list.size() > 0) {
				for(TeamPositionInfoModel model : list) {
					Map<String, Object> onePosition = new HashMap<String, Object>();
					onePosition.put("positionId", model.getPositionId());
					onePosition.put("status", model.getStatus());
					onePosition.put("positionName", model.getPositionName());
					onePosition.put("needPeopleNum", model.getNeedPeopleNum());
					onePosition.put("needPositionId", model.getNeedPositionId());
					onePosition.put("positionRequirement", model.getPositionRequirement());
					onePosition.put("createTime", sdf.format(model.getCreateTime()));
					if(positionUserList != null && positionUserList.size() > 0) {
						List<Map<String, Object>> userList = new ArrayList<Map<String,Object>>();
						for(Map<String, Object> map : positionUserList) {
							String positionId = map.get("positionId") + "";
							if(positionId.equals(model.getPositionId())) {
								userList.add(map);
							}
						}
						onePosition.put("userList", userList);
					}
					positionList.add(onePosition);
				}
			}
			
			resultMap.put("positionInfoList", positionList);
			
			//查询收藏情况
			List<Map<String, Object>> teamStoreList = teamInfoService.queryTeamInfoStore(teamId);
			if(teamStoreList != null && teamStoreList.size() > 0) {
				for(Map<String, Object> one : teamStoreList) {
					one.put("createTime", sdf.format((Date) one.get("createTime")));
				}
			}
			resultMap.put("teamStoreList", teamStoreList);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询组讯详情异常";
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
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
	public Map<String, Object> deleteTeamPosition(HttpServletRequest request, String teamId, String positionId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(positionId)) {
				throw new IllegalArgumentException("请选择要删除的职位!");
			}
			if (StringUtils.isBlank(teamId)) {
				throw new IllegalArgumentException("请选择组讯!");
			}
			
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("positionId", positionId);
			teamInfoService.deleteTeamPositionByCondition(conditionMap);
			
			this.sysLogService.saveSysLog(request, "删除组讯招聘职位信息(逻辑删除)", terminal, TeamPositionInfoModel.TABLE_NAME, teamId + "," + positionId, 3);
		} catch (IllegalArgumentException ie) {
			success = false;		
			message = ie.getMessage();
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，删除组讯招聘职位发生异常";
			logger.error(message, e);

			this.sysLogService.saveSysLog(request, "删除组讯招聘职位信息(逻辑删除)失败：" + e.getMessage(), terminal, TeamPositionInfoModel.TABLE_NAME, teamId + "," + positionId, SysLogOperType.ERROR.getValue());
			
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 根据组讯id删除组讯信息,同时会删除组讯关联的招聘职位信息(逻辑删除)
	 * @param teamId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteTeamInfo")
	public Map<String, Object> deleteTeamInfo(HttpServletRequest request, String teamId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(teamId)) {
				throw new IllegalArgumentException("请选择要删除的组讯!");
			}
			
			teamInfoService.deleteMulTeamInfoById(teamId, null);
			
			this.sysLogService.saveSysLog(request, "删除组讯信息", terminal, 
					TeamInfoModel.TABLE_NAME + "," + TeamPositionInfoModel.TABLE_NAME, teamId, SysLogOperType.DELETE.getValue());
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，删除组讯发生异常";
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "删除组讯信息失败：" + e.getMessage(), terminal, 
					TeamInfoModel.TABLE_NAME + "," + TeamPositionInfoModel.TABLE_NAME, teamId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);	
	
		return resultMap;
	}
}
