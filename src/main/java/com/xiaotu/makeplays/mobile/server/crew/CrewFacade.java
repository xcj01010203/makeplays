package com.xiaotu.makeplays.mobile.server.crew;

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
import org.springframework.web.multipart.MultipartFile;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.model.CrewUserMapModel;
import com.xiaotu.makeplays.crew.model.constants.CrewUserStatus;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.mobile.server.common.BaseFacade;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.sysrole.service.UserRoleMapService;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.model.constants.UserType;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.RegexUtils;

/**
 * 剧组信息相关接口
 * @author xuchangjian 2016-5-16上午11:41:32
 */
@Controller
@RequestMapping("/interface/crewFacade")
public class CrewFacade extends BaseFacade{
	
	Logger logger = LoggerFactory.getLogger(CrewFacade.class);
	
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CrewInfoService crewInfoService;
	
	@Autowired
	private UserRoleMapService userRoleMapService;
	
	/**
	 * 剧组的模糊查询
	 * 查询的结果中不包括以下类型的剧组：用户正在申请中的、用户已经在里面的（不管过期和无效）、过期的、无效的
	 * @param userId
	 * @param pageNo  当前的页数，页数从1开始
	 * @param keyword  查询关键字
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainCrewList")
	public Object obtainCrewList(String userId, Integer pageSize, Integer pageNo, String keyword){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			//判断用户是否有效
			MobileUtils.checkUserValid(userId);
			if (pageSize == null) {
				pageSize = 20;
			}
			if (pageNo == null) {
				pageNo = 1;
			}
			
			Page page = new Page();
			page.setPagesize(pageSize);
			page.setPageNo(pageNo);
			
			List<Map<String, Object>> crewList = this.crewInfoService.queryCrewInfoByKeyword(keyword, userId, page);
			for (Map<String, Object> map : crewList) {
				Date shootStartDate = (Date) map.get("shootStartDate");
				Date shootEndDate = (Date) map.get("shootEndDate");
				String picPath = (String) map.get("picPath");
				
				String shootStartDateStr = "";
				if (shootStartDate != null) {
					shootStartDateStr = this.sdf1.format(shootStartDate);
				}
				String shootEndDateStr = "";
				if (shootEndDate != null) {
					shootEndDateStr = this.sdf1.format(shootEndDate);
				}
				
				if (!StringUtils.isBlank(picPath)) {
					picPath = FileUtils.genPreviewPath(picPath);
				}
				
				map.put("shootStartDate", shootStartDateStr);
				map.put("shootEndDate", shootEndDateStr);
				map.put("picPath", picPath);
			}
			
			resultMap.put("crewList", crewList);
			resultMap.put("pageCount", page.getPageCount());
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，查询剧组失败", e);
			throw new IllegalArgumentException("未知异常，查询剧组失败", e);
		}
		
		return resultMap;
	}
	

	/**
	 * 用户新增剧组
	 * @param userId 用户ID
	 * @param crewName	剧组名称
	 * @param crewType	剧组类型
	 * @param subject	题材
	 * @param recordNumber	备案号
	 * @param shootStartDate	开机时间
	 * @param shootEndDate	杀青时间
	 * @param company	制片
	 * @param director	导演
	 * @param scriptWriter	编剧
	 * @param mainactor	主演
	 * @param enterPassword	入组密码
	 * @return
	 */
	@RequestMapping("/saveCrewInfo")
	@ResponseBody
	public Object saveCrewInfo(HttpServletRequest request, String userId,
			String crewId, String crewName, Integer crewType, String subject,
			String recordNumber, String shootStartDate, String shootEndDate,
			String company, String director, String scriptWriter,
			String mainactor, String enterPassword,Integer seriesNo,Integer coProduction,
			Double coProMoney, Double budget, String remark, Integer status, MultipartFile file) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		UserInfoModel userInfo = new UserInfoModel();
		try {
			MobileUtils.checkUserValid(userId);
			
			userInfo = this.userService.queryById(userId);
			
			//校验
			if (StringUtils.isBlank(crewName)) {
				throw new IllegalArgumentException("请填写剧组名称");
			}
			if (crewType == null) {
				throw new IllegalArgumentException("请选择剧组类型");
			}
			if (StringUtils.isBlank(enterPassword)) {
				throw new IllegalArgumentException("请设置入组密码");
			}
			if (!RegexUtils.regexFind("^[0-9]{6}$", enterPassword)) {
				throw new IllegalArgumentException("入组密码必须为6位数字");
			}
			if (!StringUtils.isBlank(shootStartDate) && !StringUtils.isBlank(shootEndDate)) {
				Date shootStart = sdf.parse(shootStartDate);
				Date shootEnd = sdf.parse(shootEndDate);
				
				if (shootStart.after(shootEnd)) {
					throw new IllegalArgumentException("杀青时间不能早于开机时间");
				}
			}
			if (StringUtils.isBlank(crewId) && userInfo.getUbCreateCrewNum() <= 0) {
				throw new IllegalArgumentException("您的建组机会已用完，请联系系统客服人员");
			}
			
			//新增剧组  新增剧组表记录、设置用户为剧组管理员
			CrewInfoModel crewInfo = this.crewInfoService.saveCrewByNormalUser(
					crewId, crewName, crewType, subject, recordNumber,
					shootStartDate, shootEndDate, company, director,
					scriptWriter, mainactor, enterPassword, seriesNo, status, coProduction,
					budget, coProMoney, null, remark, userInfo, file);
			
			crewId = crewInfo.getCrewId();
			
			resultMap.put("crewId", crewId);
			
			if (!StringUtils.isBlank(crewId)) {
				this.sysLogService.saveSysLogForApp(request, "新增剧组信息", userInfo.getClientType(), CrewInfoModel.TABLE_NAME, null, 1);
			} else {
				this.sysLogService.saveSysLogForApp(request, "修改剧组信息", userInfo.getClientType(), CrewInfoModel.TABLE_NAME, null, 2);
			}
			
		} catch (IllegalArgumentException ie) {
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			this.sysLogService.saveSysLogForApp(request, "保存剧组信息失败：" + e.getMessage(), userInfo.getClientType(), CrewInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常", e);
		}
		
		return resultMap;
	}
	
	/**
	 * 获取用户剧组信息
	 */
	@ResponseBody
	@RequestMapping("/obtainUserCrewList")
	public Object obtainUserCrewList(HttpServletRequest request, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			//判断用户是否有效
			UserInfoModel userInfo = MobileUtils.checkUserValid(userId);
			
			List<Map<String, Object>> resultCrewList = new ArrayList<Map<String, Object>>();
			
			//获取用户拥有的剧组
			List<Map<String, Object>> userCrewList = this.crewInfoService.queryUserCrewList(userId);
			//获取用户申请的，正在审核的剧组
			List<CrewInfoModel> auditingCrewList = this.crewInfoService.queryAuditingCrewByUserId(userId);
			
			for (CrewInfoModel crewInfo : auditingCrewList) {
				String crewId = crewInfo.getCrewId();
				String crewName = crewInfo.getCrewName();
				Integer crewType = crewInfo.getCrewType();
				String company = crewInfo.getCompany();
				String recordNumber = crewInfo.getRecordNumber();
				Date shootStartDate = crewInfo.getShootStartDate();
				Date shootEndDate = crewInfo.getShootEndDate();
				Integer status = crewInfo.getStatus();
				String subjectName = crewInfo.getSubject();
				String director = crewInfo.getDirector();
				String scriptWriter = crewInfo.getScriptWriter();
				String mainActorNames = crewInfo.getMainactor();
				String enterPassword = crewInfo.getEnterPassword();
				String picPath = crewInfo.getPicPath();
				Integer coProduction = crewInfo.getCoProduction();//合拍协议：0:无，1：已签订
				Double coProMoney = crewInfo.getCoProMoney();//协议金额
				Integer seriesNo = crewInfo.getSeriesNo();//立项集数
				Double budget = crewInfo.getBudget();//执行预算
				String remark = crewInfo.getRemark();//重要事情说明
				
				
				//用户在剧组中的状态  1：正常  2：审核中  99：冻结
				int crewUserStatus = CrewUserStatus.Auditing.getValue();
				String roleNames = "";
				
				Map<String, Object> singleCrewInfo = new HashMap<String, Object>();
				singleCrewInfo.put("crewId", crewId);
				singleCrewInfo.put("crewName", crewName);
				singleCrewInfo.put("crewType", crewType);
				singleCrewInfo.put("company", company);
				singleCrewInfo.put("recordNumber", recordNumber);
				
				if (shootStartDate != null) {
					singleCrewInfo.put("shootStartDate", this.sdf1.format(shootStartDate));
				} else {
					singleCrewInfo.put("shootStartDate", "");
				}
				if (shootEndDate != null) {
					singleCrewInfo.put("shootEndDate", this.sdf1.format(shootEndDate));
				} else {
					singleCrewInfo.put("shootEndDate", "");
				}
				singleCrewInfo.put("coProduction", coProduction);
				singleCrewInfo.put("coProMoney", coProMoney);
				singleCrewInfo.put("seriesNo", seriesNo);
				singleCrewInfo.put("budget", budget);
				singleCrewInfo.put("remark", remark);
				singleCrewInfo.put("status", status);
				singleCrewInfo.put("subjectName", subjectName);
				singleCrewInfo.put("director", director);
				singleCrewInfo.put("scriptWriter", scriptWriter);
				singleCrewInfo.put("mainActorNames", mainActorNames);
				singleCrewInfo.put("crewUserStatus", crewUserStatus);
				singleCrewInfo.put("hasClipAuth", false);
				singleCrewInfo.put("roleNames", roleNames);
				singleCrewInfo.put("enterPassword", enterPassword);
				singleCrewInfo.put("picPath", FileUtils.genPreviewPath(picPath));
				singleCrewInfo.put("canModify", false);
				
				resultCrewList.add(singleCrewInfo);
			}
			for (Map<String, Object> userCrewInfoMap : userCrewList) {
				Date shootStartDate = (Date) userCrewInfoMap.get("shootStartDate");
				Date shootEndDate = (Date) userCrewInfoMap.get("shootEndDate");
				Integer ifDefault = (Integer) userCrewInfoMap.get("ifDefault");
				String clipAuthId = (String) userCrewInfoMap.get("clipAuthId");	//场记单权限ID
				String crewUserManagerAuthId = (String) userCrewInfoMap.get("crewUserManagerAuthId");	//剧组成员管理权限ID
				String picPath = (String) userCrewInfoMap.get("picPath");
				Integer isStop = (Integer) userCrewInfoMap.get("isStop"); //是否停用，0：否，1：是
				Integer crewUserStatus = (Integer) userCrewInfoMap.get("crewUserStatus"); //用户在剧组中的状态
				
				if (shootStartDate != null) {
					userCrewInfoMap.put("shootStartDate", this.sdf1.format(shootStartDate));
				}
				if (shootEndDate != null) {
					userCrewInfoMap.put("shootEndDate", this.sdf1.format(shootEndDate));
				}
				if (crewUserStatus == CrewUserStatus.Normal.getValue() && ifDefault == 1) {
					userCrewInfoMap.put("crewUserStatus", CrewUserStatus.Currenct.getValue());
				}
				//此处应该是停用，为了兼容旧版本，设为冻结
				//新增参数，代表停用
				if (isStop == 1) {
					userCrewInfoMap.put("crewUserStatus", CrewUserStatus.Frozen.getValue());
					userCrewInfoMap.put("isStop", true);
				} else {
					userCrewInfoMap.put("isStop", false);
				}
				
				if (!StringUtils.isBlank(clipAuthId)) {
					userCrewInfoMap.put("hasClipAuth", true);
				} else {
					userCrewInfoMap.put("hasClipAuth", false);
				}
				if (!StringUtils.isBlank(crewUserManagerAuthId) && isStop != 1 && crewUserStatus != CrewUserStatus.Frozen.getValue()) {
					userCrewInfoMap.put("canModify", true);
				} else {
					userCrewInfoMap.put("canModify", false);
				}
				
				if (!StringUtils.isBlank(picPath)) {
					picPath = FileUtils.genPreviewPath(picPath);
				}
				userCrewInfoMap.put("picPath", picPath);
				
				resultCrewList.add(userCrewInfoMap);
			}
			
			//如果是大客服则返回所有剧组
			if (userInfo.getType() == UserType.CustomerService.getValue()) {
				String roleId = this.userRoleMapService.queryAllUserRoleIds(userId);
				if(roleId.equals(Constants.ROLE_ID_CUSTOM_SERVICE)) {
					List<CrewInfoModel> crewList = this.crewInfoService.queryManyByMutiCondition(null, null);
					for (CrewInfoModel crewInfo : crewList) {
						String crewId = crewInfo.getCrewId();
						String crewName = crewInfo.getCrewName();
						Integer crewType = crewInfo.getCrewType();
						String company = crewInfo.getCompany();
						String recordNumber = crewInfo.getRecordNumber();
						Date shootStartDate = crewInfo.getShootStartDate();
						Date shootEndDate = crewInfo.getShootEndDate();
						Integer status = crewInfo.getStatus();
						String subjectName = crewInfo.getSubject();
						String director = crewInfo.getDirector();
						String scriptWriter = crewInfo.getScriptWriter();
						String mainActorNames = crewInfo.getMainactor();
						String enterPassword = crewInfo.getEnterPassword();
						String picPath = crewInfo.getPicPath();
						
						//用户在剧组中的状态  1：正常  2：审核中  99：冻结
						int crewUserStatus = CrewUserStatus.Normal.getValue();
						String roleNames = "";
						
						Map<String, Object> singleCrewInfo = new HashMap<String, Object>();
						singleCrewInfo.put("crewId", crewId);
						singleCrewInfo.put("crewName", crewName);
						singleCrewInfo.put("crewType", crewType);
						singleCrewInfo.put("company", company);
						singleCrewInfo.put("recordNumber", recordNumber);
						
						if (shootStartDate != null) {
							singleCrewInfo.put("shootStartDate", this.sdf1.format(shootStartDate));
						} else {
							singleCrewInfo.put("shootStartDate", "");
						}
						if (shootEndDate != null) {
							singleCrewInfo.put("shootEndDate", this.sdf1.format(shootEndDate));
						} else {
							singleCrewInfo.put("shootEndDate", "");
						}
						
						singleCrewInfo.put("status", status);
						singleCrewInfo.put("subjectName", subjectName);
						singleCrewInfo.put("director", director);
						singleCrewInfo.put("scriptWriter", scriptWriter);
						singleCrewInfo.put("mainActorNames", mainActorNames);
						singleCrewInfo.put("crewUserStatus", crewUserStatus);
						singleCrewInfo.put("hasClipAuth", false);
						singleCrewInfo.put("roleNames", roleNames);
						singleCrewInfo.put("enterPassword", enterPassword);
						singleCrewInfo.put("picPath", FileUtils.genPreviewPath(picPath));
						singleCrewInfo.put("canModify", false);
						
						resultCrewList.add(singleCrewInfo);
					}
				}
			}
			
			resultMap.put("crewList", resultCrewList);
			
			this.sysLogService.saveSysLogForApp(request, "查询用户剧组信息", userInfo.getClientType(), CrewUserMapModel.TABLE_NAME, null, 0);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取剧组失败", e);
			throw new IllegalArgumentException("未知异常，获取剧组失败", e);
		}
		
		return resultMap;
	}
}
