package com.xiaotu.makeplays.mobile.server.role;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.index.service.IndexService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.mobile.server.common.BaseFacade;
import com.xiaotu.makeplays.mobile.server.role.dto.ToEvaluatePersonDto;
import com.xiaotu.makeplays.roleactor.model.ActorInfoModel;
import com.xiaotu.makeplays.roleactor.model.ActorRoleMapModel;
import com.xiaotu.makeplays.roleactor.model.EvaluateInfoModel;
import com.xiaotu.makeplays.roleactor.model.EvaluateTagMapModel;
import com.xiaotu.makeplays.roleactor.model.EvtagInfoModel;
import com.xiaotu.makeplays.roleactor.model.constants.ViewRoleType;
import com.xiaotu.makeplays.roleactor.service.ActorInfoService;
import com.xiaotu.makeplays.roleactor.service.EvaluateService;
import com.xiaotu.makeplays.roleactor.service.ViewRoleService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.sysrole.model.UserRoleMapModel;
import com.xiaotu.makeplays.user.model.UserFocusRoleMapModel;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.service.UserFocusRoleMapService;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.EmojiFilter;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.view.model.ViewRoleMapModel;
import com.xiaotu.makeplays.view.model.constants.ShootStatus;
import com.xiaotu.makeplays.view.model.constants.ViewType;
import com.xiaotu.makeplays.view.service.ViewInfoService;

/**
 * 手机端场景角色相关接口
 * @author xuchangjian 2016-9-19下午5:52:34
 */
@Controller
@RequestMapping("/interface/viewRoleFacade")
public class ViewRoleFacade extends BaseFacade{
	
	Logger logger = LoggerFactory.getLogger(ViewRoleFacade.class);
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM");

	@Autowired
	private ViewRoleService viewRoleService;
	
	@Autowired
	private UserFocusRoleMapService userFocusRoleMapService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private EvaluateService evaluateService;
	
	@Autowired
	private CrewInfoService crewInfoService;
	
	@Autowired
	private IndexService indexService;
	
	@Autowired
	private ActorInfoService actorInfoService;
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	/**
	 * 获取剧组下所有主要演员、特约演员接口
	 * @param crewId	剧组ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtailAllRoleInfo")
	public Object obtailAllRoleInfo(String crewId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			if (StringUtils.isBlank(crewId)) {
				throw new IllegalArgumentException("无效的剧组访问");
			}
			
			List<Map<String, Object>> majorRoleList = this.viewRoleService.queryRoleMapByCrewIdAndRoleType(crewId, ViewRoleType.MajorActor.getValue());
			List<Map<String, Object>> guestRoleList = this.viewRoleService.queryRoleMapByCrewIdAndRoleType(crewId, ViewRoleType.GuestActor.getValue());
			
			resultMap.put("majorRoleList", majorRoleList);
			resultMap.put("guestRoleList", guestRoleList);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，查询演员角色信息失败", e);
			throw new IllegalArgumentException("未知异常，查询演员角色信息失败");
		}
		
		return resultMap;
	}
	
	/**
	 * 获取剧组下主要演员、特约演员及拍摄进度基本信息接口
	 * 分页查询，根据演员或角色名称查询
	 * @param crewId	剧组ID
	 * @param userId	用户ID
	 * @param viewRoleType	角色类型
	 * @param roleName	演员或角色名称
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainRoleInfoAndShootStat")
	public Object obtainRoleInfoAndShootStat(String crewId, String userId, Integer viewRoleType, String roleName, Integer pageSize, Integer pageNo) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			Page page = null;
			if (pageSize != null && pageNo != null) {
				page = new Page();
				page.setPagesize(pageSize);
				page.setPageNo(pageNo);
			}
			
			if(viewRoleType != null) {
				List<Map<String, Object>> viewRoleList = this.viewRoleService.queryRoleAndShootStatByRoleType(crewId, viewRoleType, roleName, page);
				
				resultMap.put("viewRoleList", viewRoleList);
			} else {
				List<Map<String, Object>> majorRoleList = this.viewRoleService.queryRoleAndShootStatByRoleType(crewId, ViewRoleType.MajorActor.getValue(), roleName, page);
				List<Map<String, Object>> guestRoleList = this.viewRoleService.queryRoleAndShootStatByRoleType(crewId, ViewRoleType.GuestActor.getValue(), roleName, page);
				
				resultMap.put("majorRoleList", majorRoleList);
				resultMap.put("guestRoleList", guestRoleList);
			}
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，查询演员角色信息失败", e);
			throw new IllegalArgumentException("未知异常，查询演员角色信息失败");
		}
		
		return resultMap;
	}
	
	
	/**
	 * 保存用户关注的角色信息
	 * @param crewId	剧组ID
	 * @param userId	用户ID
	 * @param roldIds	关注的角色IDs,多个值用逗号隔开
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveFocusRoleInfo")
	public Object saveFocusRoleInfo(HttpServletRequest request, String crewId, String userId, String roleIds) {
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(crewId)) {
				throw new IllegalArgumentException("无效的剧组访问");
			}
			
			//如果roleIds为空，则删除所有关注的角色
			if (StringUtils.isBlank(roleIds)) {
				this.userFocusRoleMapService.deleteByUserId(userId);
				
				this.sysLogService.saveSysLogForApp(request, "删除用户关注的角色信息", userInfo.getClientType(), UserFocusRoleMapModel.TABLE_NAME, roleIds, 3);
			} else {
				List<UserFocusRoleMapModel> mapList = new ArrayList<UserFocusRoleMapModel>();
				String[] roleIdArr = roleIds.split(",");
				for (String roleId : roleIdArr) {
					UserFocusRoleMapModel map = new UserFocusRoleMapModel();
					map.setMapId(UUIDUtils.getId());
					map.setUserId(userId);
					map.setViewRoleId(roleId);
					map.setCrewId(crewId);
					mapList.add(map);
				}
				this.userFocusRoleMapService.addMany(userId, mapList);
				
				this.sysLogService.saveSysLogForApp(request, "保存用户关注的角色信息", userInfo.getClientType(), UserFocusRoleMapModel.TABLE_NAME, roleIds, 1);
			}
			
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch(Exception e) {
			logger.error("未知异常，关注角色信息失败", e);
			this.sysLogService.saveSysLogForApp(request, "保存用户关注的角色信息失败：" + e.getMessage(), userInfo.getClientType(), UserFocusRoleMapModel.TABLE_NAME, roleIds, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，关注角色信息失败");
		}
		
		return null;
	}
	
	/**
	 * 获取待评价人信息
	 * 包含艺人和主创人信息
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtailToEvaPersonInfo")
	public Object obtailToEvaPersonInfo(HttpServletRequest request, String crewId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(crewId)) {
				throw new IllegalArgumentException("无效的剧组访问");
			}
			
			//查询用户扮演的角色信息
			List<Map<String, Object>> userRoleList = this.crewInfoService.getCrewRole(crewId, userId);
			List<String> userRoleIdList = new ArrayList<String>();
			if (userRoleList != null && userRoleList.size() > 0) {
				for (Map<String, Object> userRole : userRoleList) {
					userRoleIdList.add((String) userRole.get("viewRoleId")); 
				}
			}
			
			List<ToEvaluatePersonDto> leaderList = new ArrayList<ToEvaluatePersonDto>();	//主创人信息
			List<ToEvaluatePersonDto> actorList = new ArrayList<ToEvaluatePersonDto>(); 	//艺人信息
			List<EvtagInfoModel> evtagList = this.evaluateService.queryEvtagList(crewId);		//评价标签信息
			
			//查询演员信息和用户信息
			List<Map<String, Object>> roleActorList = this.actorInfoService.queryViewRoleActorInfo(crewId, null);
			List<Map<String, Object>> userList = this.userService.queryNotRoleUserListbyCrewIdWithRole(crewId);
			
			//主创人信息
			for (Map<String, Object> user : userList) {
				String name = (String) user.get("realName");
				String toEvalatePersonId = (String) user.get("userId");
				String identity = (String) user.get("roleNames");
				
				//不能评价自己
				if (toEvalatePersonId.equals(userId)) {
					continue;
				}
				
				ToEvaluatePersonDto evaluatePersonDto = new ToEvaluatePersonDto();
				evaluatePersonDto.setName(name);
				evaluatePersonDto.setToEvalatePersonId(toEvalatePersonId);
				evaluatePersonDto.setIdentity(identity);
				leaderList.add(evaluatePersonDto);
			}
			resultMap.put("leaderList", leaderList);
			
			
			//艺人信息
			for (Map<String, Object> map : roleActorList) {
				String name = (String) map.get("actorName");
				String toEvalatePersonId = (String) map.get("actorId");
				String identity = (String) map.get("viewRoleName");
				
				//不能评价自己
				if (userRoleIdList.contains(toEvalatePersonId)) {
					continue;
				}
				
				ToEvaluatePersonDto evaluatePersonDto = new ToEvaluatePersonDto();
				evaluatePersonDto.setName(name);
				evaluatePersonDto.setToEvalatePersonId(toEvalatePersonId);
				evaluatePersonDto.setIdentity(identity);
				
				actorList.add(evaluatePersonDto);
			}
			
			resultMap.put("actorList", actorList);
			
			resultMap.put("evtagList", evtagList);
			
			this.sysLogService.saveSysLogForApp(request, "查询艺人和主创人信息", userInfo.getClientType(), 
					ActorRoleMapModel.TABLE_NAME + "," + UserRoleMapModel.TABLE_NAME, null, 0);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch(Exception e) {
			logger.error("未知异常，查询待评价人信息失败", e);
			
			this.sysLogService.saveSysLogForApp(request, "查询艺人和主创人信息失败：" + e.getMessage(), userInfo.getClientType(), 
					ActorRoleMapModel.TABLE_NAME + "," + UserRoleMapModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，获取待评价人信息失败");
		}
		
		return resultMap;
	}
	
	
	/**
	 * 保存用户评价信息
	 * @param crewId	剧组ID
	 * @param userId	评价人ID
	 * @param toEvalatePersonId	被评价人ID
	 * @param type	被评价人类型（1：主创人   2：艺人）
	 * @param score	得分
	 * @param comment	评语
	 * @param evTags	评价标签ID，多个ID以英文逗号分隔
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveEvaluateInfo")
	public Object saveEvaluateInfo(HttpServletRequest request, String crewId,
			String userId, String toEvalatePersonId, Integer type,
			Integer score, String comment, String evTags, String identity) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel user = new UserInfoModel();
		try {
			//数据校验
			MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(toEvalatePersonId)) {
				throw new IllegalArgumentException("请提供评价人信息");
			}
			
			
			//保存评价信息
			EvaluateInfoModel evaluate = new EvaluateInfoModel();
			//基本信息
			String evaluateId = UUIDUtils.getId();
			evaluate.setEvaluateId(evaluateId);
			evaluate.setCreateTime(new Date());
			evaluate.setEvaluateTime(new Date());
			evaluate.setCrewId(crewId);
			evaluate.setStatus(Constants.EVALUATE_STATUS_FINISH);
			
			//评价信息
			evaluate.setScore(score);
			evaluate.setComment(EmojiFilter.filterEmoji(comment));
			
			//评价人信息
			user = this.userService.queryById(userId);
			evaluate.setFromMpUserId(user.getUserId());
			evaluate.setFromUserName(user.getRealName());
			
			//被评价人信息
			evaluate.setRoleName(identity);
			if (type.equals(Constants.TO_EVALUATE_PERSON_TYPE_ACTOR)) {
				ActorInfoModel actorInfo = this.actorInfoService.queryById(toEvalatePersonId);
				if (actorInfo == null) {
					throw new IllegalArgumentException("被评价人不存在，请刷新列表后重试");
				}
				evaluate.setToMpUserId(actorInfo.getActorId());
				evaluate.setToUserName(actorInfo.getActorName());
			}
			
			if (type.equals(Constants.TO_EVALUATE_PERSON_TYPE_USER)) {
				UserInfoModel userInfo = this.userService.queryById(toEvalatePersonId);
				if (userInfo == null) {
					throw new IllegalArgumentException("被评价人不存在，请刷新列表后重试");
				}
				evaluate.setToMpUserId(userInfo.getUserId());
				evaluate.setToUserName(userInfo.getUserName());
			}
			
			
			//评价标签信息
			List<EvaluateTagMapModel> tagMapList = new ArrayList<EvaluateTagMapModel>();
			if (!StringUtils.isBlank(evTags)) {
				String[] tagArray = evTags.split(",");
				for (String tagId : tagArray) {
					EvaluateTagMapModel evaluateTagMap = new EvaluateTagMapModel();
					evaluateTagMap.setMapId(UUIDUtils.getId());
					evaluateTagMap.setEvaluateId(evaluateId);
					evaluateTagMap.setTagId(tagId);
					tagMapList.add(evaluateTagMap);
				}
			}
			
			//保存评价信息
			this.evaluateService.addEvaluate(evaluate, tagMapList);
			
			this.sysLogService.saveSysLogForApp(request, "保存评价信息", user.getClientType(), EvaluateInfoModel.TABLE_NAME, toEvalatePersonId, 1);
			
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch(Exception e) {
			logger.error("未知异常，评价失败", e);
			
			this.sysLogService.saveSysLogForApp(request, "保存评价信息失败：" + e.getMessage(), 
					user.getClientType(), EvaluateInfoModel.TABLE_NAME, toEvalatePersonId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，评价失败");
		}
		
		return resultMap;
	}
	
	/**
	 * 获取角色拍摄详细信息接口
	 * @param crewId
	 * @param userId
	 * @param viewRoleId	角色ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainViewRoleShootDetail")
	public Object obtainViewRoleShootDetail(HttpServletRequest request, String crewId, String userId, String viewRoleId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			//数据校验
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(viewRoleId)) {
				throw new IllegalArgumentException("请提供角色信息");
			}
			
			//角色基本信息
			Map<String, Object> viewRoleInfo = this.viewRoleService.queryByIdWithActorInfo(viewRoleId);
			if (viewRoleInfo.get("enterDate") != null) {
				viewRoleInfo.put("enterDate", this.sdf1.format((Date) viewRoleInfo.get("enterDate")));
			}
			if (viewRoleInfo.get("leaveDate") != null) {
				viewRoleInfo.put("leaveDate", this.sdf1.format((Date) viewRoleInfo.get("leaveDate")));
			}
			
			
			//角色场景信息
			int totalViewCount = 0;	//总场数
			int shootedViewCount = 0;	//已拍摄场数
			int insideViewCount = 0;	//内戏场数
			int shootedInsideViewCount = 0;	//已拍摄内戏场数
			int outsideViewCount = 0;	//外戏场数，含有“外”的都是外戏，其他都是内戏
			int shootedOutsideViewCount = 0;	//已拍摄外戏场数
			int dayViewCount = 0;	//日戏场数
			int shootedDayViewCount = 0;	//已拍摄日戏场数
			int nightViewCount = 0;	//夜戏场数，含有“夜”的都是夜戏，其他都是日戏
			int shootedNightViewCount = 0;	//已拍摄夜戏场数
			int literateViewCount = 0;	//文戏场数
			int shootedLiterateViewCount = 0;	//已拍摄文戏场数
			int kungFuViewCount = 0;	//武戏场数，“文武”戏属于武戏
			int shootedKungFuViewCount = 0;	//已拍摄武戏场数
			double totalPageCount = 0;	//总页数
			double shootedPageCount = 0;	//已拍摄页数
			
			List<Map<String, Object>> roleViewList = this.viewInfoService.queryRoleViewList(crewId, viewRoleId);
			for (Map<String, Object> roleViewInfo : roleViewList) {
				if (roleViewInfo.get("viewId") == null) {
					continue;
				}
				
				int shootStatus = (Integer) roleViewInfo.get("shootStatus");
				String site = (String) roleViewInfo.get("site");
				String atmosphereName = (String) roleViewInfo.get("atmosphereName");
				Integer viewType = (Integer) roleViewInfo.get("viewType");
				Double pageCount = (Double) roleViewInfo.get("pageCount");
				if (pageCount == null) {
					pageCount = 0.0;
				}
				boolean isFinished = false;
				
				if (shootStatus == ShootStatus.DeleteXi.getValue()) {
					continue;
				}
				
				totalViewCount++;
				totalPageCount = BigDecimalUtil.add(totalPageCount, pageCount);
				if (shootStatus == ShootStatus.Finished.getValue()) {
					isFinished = true;
					shootedViewCount++;
					shootedPageCount = BigDecimalUtil.add(shootedPageCount, pageCount);
				}
				
				//内外景统计
				if (StringUtils.isBlank(site) || site.indexOf("外") == -1) {
					insideViewCount++;
					if (isFinished) {
						shootedInsideViewCount++;
					}
				} else {
					outsideViewCount++;
					if (isFinished) {
						shootedOutsideViewCount++;
					}
				}
				
				//气氛统计
				if (StringUtils.isBlank(atmosphereName) || atmosphereName.indexOf("夜") == -1) {
					dayViewCount++;
					if (isFinished) {
						shootedDayViewCount++;
					}
				} else {
					nightViewCount++;
					if (isFinished) {
						shootedNightViewCount++;
					}
				}
				
				//文武戏统计
				if (viewType == null || viewType == ViewType.TeXiao.getValue()) {
					literateViewCount++;
					if (isFinished) {
						shootedLiterateViewCount++;
					}
				} else {
					kungFuViewCount++;
					if (isFinished) {
						shootedKungFuViewCount++;
					}
				}
			}
			Map<String, Object> viewStatisticInfo = new HashMap<String, Object>();
			viewStatisticInfo.put("totalViewCount", totalViewCount);
			viewStatisticInfo.put("shootedViewCount", shootedViewCount);
			viewStatisticInfo.put("insideViewCount", insideViewCount);
			viewStatisticInfo.put("outsideViewCount", outsideViewCount);
			viewStatisticInfo.put("dayViewCount", dayViewCount);
			viewStatisticInfo.put("nightViewCount", nightViewCount);
			viewStatisticInfo.put("literateViewCount", literateViewCount);
			viewStatisticInfo.put("kungFuViewCount", kungFuViewCount);
			
			viewStatisticInfo.put("shootedInsideViewCount", shootedInsideViewCount);
			viewStatisticInfo.put("shootedOutsideViewCount", shootedOutsideViewCount);
			viewStatisticInfo.put("shootedDayViewCount", shootedDayViewCount);
			viewStatisticInfo.put("shootedNightViewCount", shootedNightViewCount);
			viewStatisticInfo.put("shootedLiterateViewCount", shootedLiterateViewCount);
			viewStatisticInfo.put("shootedKungFuViewCount", shootedKungFuViewCount);
			viewStatisticInfo.put("totalPageCount", totalPageCount);
			viewStatisticInfo.put("shootedPageCount", shootedPageCount);
			
			
			//日拍摄量统计
			Map<String, Integer> dayGroupViewCountMap = new HashMap<String, Integer>();	//每一天的拍摄场数，key为日期（yyyy-MM-dd），value为当天拍摄完成场数
			List<Map<String, Object>> roleViewStatistic = this.viewRoleService.queryRoleViewStatistic(viewRoleId);
			for (Map<String, Object> roleViewMap : roleViewStatistic) {
				Date noticeDate = (Date) roleViewMap.get("noticeDate");
				String noticeDateStr = this.sdf1.format(noticeDate);
				int viewCount = ((Long) roleViewMap.get("viewCount")).intValue();
				//每一天拍摄的场数
				dayGroupViewCountMap.put(noticeDateStr, viewCount);
			}
			
			//按照天、月统计已拍摄拍摄场数(封装成日历的格式)
			Set<String> dayKeySet = dayGroupViewCountMap.keySet();
			List<Map<String, Object>> dayGroupViewCountList = new ArrayList<Map<String, Object>>();
			for (String day : dayKeySet) {
				Map<String, Object> dayViewCountMap = new HashMap<String, Object>();
				dayViewCountMap.put("day", day);
				dayViewCountMap.put("viewCount", dayGroupViewCountMap.get(day));
				
				dayGroupViewCountList.add(dayViewCountMap);
			}
			
			//排序
			Collections.sort(dayGroupViewCountList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					int result = 0;
					
					String day1 = (String) o1.get("day");
					String day2 = (String) o2.get("day");
					
					try {
						Date date1 = sdf1.parse(day1);
						Date date2 = sdf1.parse(day2);
						
						if (date1.before(date2)) {
							result = 1;
						} else {
							result = -1;
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					return result;
				}
			});
			
			Map<String, List<Map<String, Object>>> monthGroupViewCountMap = new HashMap<String, List<Map<String, Object>>>();	//key为月份，value为当月的按天统计拍摄场数列表
			for (Map<String, Object> dayViewCountMap : dayGroupViewCountList) {
				String day = (String) dayViewCountMap.get("day");
				
				String month = this.sdf2.format(this.sdf1.parse(day));
				if (monthGroupViewCountMap.containsKey(month)) {
					List<Map<String, Object>> myDayGroupViewCountList = monthGroupViewCountMap.get(month);
					myDayGroupViewCountList.add(dayViewCountMap);
					monthGroupViewCountMap.put(month, myDayGroupViewCountList);
				} else {
					List<Map<String, Object>> myDayGroupViewCountList = new ArrayList<Map<String, Object>>();
					myDayGroupViewCountList.add(dayViewCountMap);
					monthGroupViewCountMap.put(month, myDayGroupViewCountList);
				}
			}
			
			Set<String> monthKeySet = monthGroupViewCountMap.keySet();
			List<Map<String, Object>> monthGroupViewCountList = new ArrayList<Map<String, Object>>();
			for (String month : monthKeySet) {
				Map<String, Object> monthViewCountMap = new HashMap<String, Object>();
				monthViewCountMap.put("month", month);
				monthViewCountMap.put("dayGroupViewCountList", monthGroupViewCountMap.get(month));
				
				monthGroupViewCountList.add(monthViewCountMap);
			}
			

			//排序
			Collections.sort(monthGroupViewCountList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					int result = 0;
					
					String moon1 = (String) o1.get("month");
					String moon2 = (String) o2.get("month");
					
					try {
						Date date1 = sdf2.parse(moon1);
						Date date2 = sdf2.parse(moon2);
						
						if (date1.before(date2)) {
							result = 1;
						} else {
							result = -1;
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					return result;
				}
			});
			
			
			resultMap.put("viewRoleInfo", viewRoleInfo);
			resultMap.put("viewStatisticInfo", viewStatisticInfo);
			resultMap.put("monthGroupViewCountList", monthGroupViewCountList);
			
			this.sysLogService.saveSysLogForApp(request, "查询角色拍摄详细信息", userInfo.getClientType(), ViewRoleMapModel.TABLE_NAME, viewRoleId, 0);
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch(Exception e) {
			logger.error("未知异常，获取角色拍摄详细信息失败", e);
			
			this.sysLogService.saveSysLogForApp(request, "查询角色拍摄详细信息失败：" + e.getMessage(), 
					userInfo.getClientType(), ViewRoleMapModel.TABLE_NAME, viewRoleId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，获取角色拍摄详细信息失败");
		}
		
		return resultMap;
	}
}
