package com.xiaotu.makeplays.mobile.server.index;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.approval.controller.filter.ReceiptInfoFilter;
import com.xiaotu.makeplays.approval.service.ReceiptInfoService;
import com.xiaotu.makeplays.authority.service.AuthorityService;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.model.CrewSubjectModel;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.crew.service.CrewSubjectService;
import com.xiaotu.makeplays.finance.model.FinanceSettingModel;
import com.xiaotu.makeplays.finance.service.FinanceSettingService;
import com.xiaotu.makeplays.index.service.IndexService;
import com.xiaotu.makeplays.message.model.MessageInfoModel;
import com.xiaotu.makeplays.message.model.constants.MessageType;
import com.xiaotu.makeplays.message.service.MessageInfoService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.mobile.server.index.dto.MobileAuthorityDto;
import com.xiaotu.makeplays.mobile.server.role.ViewRoleFacade;
import com.xiaotu.makeplays.notice.model.NoticeInfoModel;
import com.xiaotu.makeplays.notice.model.constants.NoticeCanceledStatus;
import com.xiaotu.makeplays.notice.service.NoticeService;
import com.xiaotu.makeplays.sysrole.model.SysroleInfoModel;
import com.xiaotu.makeplays.sysrole.service.SysRoleInfoService;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.model.constants.UserType;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.DateUtils;
import com.xiaotu.makeplays.view.service.ViewInfoService;

/**
 * 首页
 * 存放一些公用的且无法归类的接口
 * @author xuchangjian 2016-9-18上午10:51:43
 */
@Controller
@RequestMapping("/interface/indexFacade")
public class IndexFacade {
	
	Logger logger = LoggerFactory.getLogger(ViewRoleFacade.class);

	@Autowired
	private CrewInfoService crewInfoService;

	@Autowired
	private IndexService indexService;
	
	@Autowired
	private AuthorityService authorityService;
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	@Autowired
	private FinanceSettingService financeSettingService;
	
	@Autowired
	private SysRoleInfoService sysRoleInfoService;
	
	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private CrewSubjectService crewSubjectService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private MessageInfoService messageInfoService;
	
	@Autowired
	private ReceiptInfoService receiptInfoService;
	
	/**
	 * 获取首页信息
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainIndexInfo")
	public Object obtainIndexInfo(String crewId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			UserInfoModel userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			
			int shootedDays = 0;	//已拍摄天数
			Integer allShootDays = null;	//总天数
			int shootedViews = 0;	//已拍摄场数
			int allViews = 0;	//总场数
			double totalPageCount = 0.0;
			double shootedPageCount = 0.0;
			double dailyShootPages = 0;	//日平均拍摄页数
			boolean ifFinanceEncry = false;	//财务是否加密
			
			
			//拍摄天数的计算
			CrewInfoModel crewInfo = this.crewInfoService.queryById(crewId);
			Date shootStartDate = crewInfo.getShootStartDate();
			Date shootEndDate = crewInfo.getShootEndDate();
			String crewName = crewInfo.getCrewName();
			
			if (shootStartDate != null && shootEndDate != null) {
				allShootDays = DateUtils.daysBetween(shootStartDate, shootEndDate) + 1;
				if (allShootDays < 0) {
					allShootDays = null;
				}
			}
			
			//已拍摄天数的计算，已销场通告单的天数
			Map<String, Object> noticeConditionMap = new HashMap<String, Object>();
			noticeConditionMap.put("crewId", crewId);
			noticeConditionMap.put("canceledStatus", NoticeCanceledStatus.Canceled.getValue());
			List<NoticeInfoModel> canceledNoticeList = this.noticeService.queryManyByMutiCondition(noticeConditionMap, null);
			
			List<Date> noticeDateList = new ArrayList<Date>();
			for (NoticeInfoModel noticeInfo : canceledNoticeList) {
				Date noticeDate = noticeInfo.getNoticeDate();
				if (!noticeDateList.contains(noticeDate)) {
					noticeDateList.add(noticeDate);
				}
			}
			shootedDays = noticeDateList.size();
			
			
			//拍摄场数的计算
			Map<String, Object> viewCountStatistic = this.viewInfoService.queryViewCountStatistic(crewId);
			allViews = ((Long) viewCountStatistic.get("totalViewCount")).intValue();
			if (viewCountStatistic != null && allViews != 0) {
				shootedViews = ((BigDecimal) viewCountStatistic.get("finishedViewCount")).intValue();
				
				//已拍摄页数
				shootedPageCount = (Double) viewCountStatistic.get("finishedPageCount");
				totalPageCount = (Double) viewCountStatistic.get("totalPageCount");
				if (shootedDays != 0) {
					dailyShootPages = BigDecimalUtil.divide(shootedPageCount, shootedDays);
				}
			}
			
			
			
			//财务是否加密
			FinanceSettingModel financeSetting = this.financeSettingService.queryByCrewId(crewId);
        	if (financeSetting == null) {
        		financeSetting = this.financeSettingService.initFinanceSetting(crewId);
        	}
			ifFinanceEncry = financeSetting.getPwdStatus();
			if (userInfo.getType() == UserType.CustomerService.getValue()) {
				ifFinanceEncry = false;
			}
			
			//权限信息
			MobileAuthorityDto mobileAuthorityDto = this.authorityService.genMobileAuthInfo(crewId, userId);
			
			//判断是否是财务
			boolean isFinance = false;
			if (!StringUtils.isBlank(crewId)) {
				List<Map<String, Object>> myRoleList = this.sysRoleInfoService.queryByCrewUserId(crewId, userId);
				for (Map<String, Object> myRoleInfo : myRoleList) {
					String roleName = (String) myRoleInfo.get("roleName");
					if (roleName.equals("财务")) {
						isFinance = true;
						break;
					}
				}
			}
			
			
			Map<String, Object> messageCondition = new HashMap<String, Object>();
			messageCondition.put("receiverId", userId);
			messageCondition.put("type", MessageType.NoticePublish.getValue());
			/*
			 * 是否有新的通告
			 */
			boolean hasNewNotice = false;
//			messageCondition.put("readStatus", MessageInfoStatus.UnRead.getValue());
//			List<MessageInfoModel> unReadNoticeMessageInfo = this.messageInfoService.queryManyByMutiCondition(messageCondition, null);
//			if (unReadNoticeMessageInfo != null && unReadNoticeMessageInfo.size() > 0) {
//				hasNewNotice = true;
//			}
			List<NoticeInfoModel> notReadNoticeList = this.noticeService.queryNotReadNoticeList(crewId, userId);
			if (notReadNoticeList != null && notReadNoticeList.size() > 0) {
				hasNewNotice = true;
			}
			
			/*
			 * 是否有新的剧本 TODO
			 */
			boolean hasNewScenario = false;
//			messageCondition.put("type", MessageType.ScenarioEdit.getValue());
//			List<MessageInfoModel> unReadScenarioMessageInfo = this.messageInfoService.queryManyByMutiCondition(messageCondition, null);
//			if (unReadScenarioMessageInfo != null && unReadScenarioMessageInfo.size() > 0) {
//				hasNewScenario = true;
//			}
			
			/*
			 * 是否有新的审批 TODO
			 */
			boolean hasNewApproval = false;
			
			//先判断是否有“待我审批”的单据
			ReceiptInfoFilter filter = new ReceiptInfoFilter();
			filter.setListType(3);
			List<Map<String, Object>> receiptList = this.receiptInfoService.queryReceiptInfoList(crewId, userId, filter, null);
			
			//是否有未读新消息
//			messageCondition.put("type", MessageType.Approval.getValue());
//			List<MessageInfoModel> unReadApprovalMessageInfo = this.messageInfoService.queryManyByMutiCondition(messageCondition, null);
			
			if ((receiptList != null && receiptList.size() > 0)) {
				hasNewApproval = true;
			}	
			
			resultMap.put("mobileAuthority", mobileAuthorityDto);
			resultMap.put("shootedDays", shootedDays);
			resultMap.put("allShootDays", allShootDays);
			resultMap.put("shootedViews", shootedViews);
			resultMap.put("allViews", allViews);
			resultMap.put("dailyShootPages", dailyShootPages);
			resultMap.put("shootedPageCount", shootedPageCount);
			resultMap.put("totalPageCount", totalPageCount);
			resultMap.put("ifFinanceEncry", ifFinanceEncry);
			resultMap.put("crewName", crewName);
			resultMap.put("isFinance", isFinance);
			resultMap.put("hasNewNotice", hasNewNotice);
			resultMap.put("hasNewScenario", hasNewScenario);
			resultMap.put("hasNewApproval", hasNewApproval);
			
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch(Exception e) {
			logger.error("未知异常，获取首页信息失败", e);
			throw new IllegalArgumentException("未知异常，获取首页信息失败");
		}
		
		return resultMap;
	}
	
	/**
	 * 获取系统题材码表
	 * @return
	 */
	@RequestMapping("/obtainSubjects")
	@ResponseBody
	public Object obtainSubjects() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			List<CrewSubjectModel> subjectList = this.crewSubjectService.querySubjectList();
			
			resultMap.put("subjectList", subjectList);
		} catch (Exception e) {
			throw new IllegalArgumentException("未知异常");
		}
		
		return resultMap;
	}
	
	/**
	 * 获取系统部门信息
	 * @param crewId	剧组ID
	 * @param userId	用户ID
	 * @param needManager	是否需要管理员信息
	 * @param needDirector  是否需要项目总监信息  手机端不显示项目总监，默认设为false
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainDuties")
   	public Object obtainDuties(String crewId, Boolean needManager, Boolean needDirector) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		
		try {
			if (needManager == null) {
				needManager = false;
			}
			if (needDirector == null) {
				needDirector = false;
			}
			needDirector = false;
			
			List<SysroleInfoModel> roleList = this.sysRoleInfoService.queryByCrewId(crewId);
			
			//小组信息
			for (SysroleInfoModel sysRoleInfo : roleList) {
				String parentId = sysRoleInfo.getParentId();
				String roleId = sysRoleInfo.getRoleId();
				
				Map<String, Object> singleRoleMap = new HashMap<String, Object>();
				singleRoleMap.put("roleId", sysRoleInfo.getRoleId());
				singleRoleMap.put("roleName", sysRoleInfo.getRoleName());
				singleRoleMap.put("roleDesc", sysRoleInfo.getRoleDesc());
				
				if (parentId.equals("00") || (needManager && roleId.equals(Constants.ROLE_ID_ADMIN)) || 
						(needDirector && roleId.equals(Constants.ROLE_ID_PROJECT_DIRECTOR))) {
					List<Map<String, Object>> child = new ArrayList<Map<String, Object>>();
					singleRoleMap.put("child", child);

					resultList.add(singleRoleMap);
				}
				
			}
			
			//职务信息
			for (Map<String, Object> map : resultList) {
				String pRoleId = (String) map.get("roleId");
				
				for (SysroleInfoModel sysRoleInfo : roleList) {
					String myparentId = sysRoleInfo.getParentId();
					String myRoleId = sysRoleInfo.getRoleId();
					
					Map<String, Object> singleRoleMap = new HashMap<String, Object>();
					singleRoleMap.put("roleId", sysRoleInfo.getRoleId());
					singleRoleMap.put("roleName", sysRoleInfo.getRoleName());
					singleRoleMap.put("roleDesc", sysRoleInfo.getRoleDesc());
					
					if (pRoleId.equals(myparentId) || (pRoleId.equals(Constants.ROLE_ID_ADMIN) && myRoleId.equals(Constants.ROLE_ID_ADMIN)) ||
							(pRoleId.equals(Constants.ROLE_ID_PROJECT_DIRECTOR) && myRoleId.equals(Constants.ROLE_ID_PROJECT_DIRECTOR))) {
						List<Map<String, Object>> child = (List<Map<String, Object>>) map.get("child");
						child.add(singleRoleMap);
					}
				}
			}
			
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取系统部门失败", e);
			throw new IllegalArgumentException("未知异常，获取系统部门失败", e);
		}
		
		return resultList;
   	}
	
	/**
	 * 获取部门下的职务信息
	 * @param crewId	剧组ID
	 * @param userId	用户ID
	 * @param needManager	是否需要管理员信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainDutiesByParentIds")
   	public Object obtainDutiesByParentIds(String crewId, String parentIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			if (StringUtils.isBlank(parentIds)) {
				throw new IllegalArgumentException("请提供部门信息");
			}
			
			List<SysroleInfoModel> roleList = this.sysRoleInfoService.queryByParentIds(crewId, parentIds);
			
			resultMap.put("roleList", roleList);
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取职务失败", e);
			throw new IllegalArgumentException("未知异常，获取职务失败", e);
		}
		
		return resultMap;
   	}
}
