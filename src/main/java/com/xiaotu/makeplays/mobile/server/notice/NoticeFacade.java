package com.xiaotu.makeplays.mobile.server.notice;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.locationsearch.model.SceneViewInfoModel;
import com.xiaotu.makeplays.locationsearch.service.SceneViewInfoService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.mobile.server.common.BaseFacade;
import com.xiaotu.makeplays.mobile.server.notice.dto.DayGroupNoticeDto;
import com.xiaotu.makeplays.mobile.server.notice.dto.LocationViewDto;
import com.xiaotu.makeplays.mobile.server.notice.dto.MonthGroupNoticeDto;
import com.xiaotu.makeplays.mobile.server.notice.dto.NoticeInfoDto;
import com.xiaotu.makeplays.mobile.server.notice.dto.NoticeRoleTimeDto;
import com.xiaotu.makeplays.mobile.server.notice.dto.NoticeTimeDto;
import com.xiaotu.makeplays.mobile.server.notice.dto.PictureDto;
import com.xiaotu.makeplays.mobile.server.notice.dto.ViewInfoDto;
import com.xiaotu.makeplays.notice.model.ConvertAddressModel;
import com.xiaotu.makeplays.notice.model.NoticeInfoModel;
import com.xiaotu.makeplays.notice.model.NoticePictureModel;
import com.xiaotu.makeplays.notice.model.NoticePushFedBackModel;
import com.xiaotu.makeplays.notice.model.NoticeRoleTimeModel;
import com.xiaotu.makeplays.notice.model.NoticeTimeModel;
import com.xiaotu.makeplays.notice.model.constants.NoticeIsSatisfied;
import com.xiaotu.makeplays.notice.model.constants.NoticePushFedBackStatus;
import com.xiaotu.makeplays.notice.service.ConvertAddressService;
import com.xiaotu.makeplays.notice.service.NoticeService;
import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;
import com.xiaotu.makeplays.roleactor.model.constants.ViewRoleType;
import com.xiaotu.makeplays.roleactor.service.ViewRoleService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.service.CrewContactService;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.DateUtils;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.view.service.ViewContentService;
import com.xiaotu.makeplays.view.service.ViewInfoService;

/**
 * 通告单手机端接口
 * @author xuchangjian
 */
@Controller
@RequestMapping("/interface/noticeFacade")
public class NoticeFacade extends BaseFacade{
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

	private SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");

	private SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM");

	private SimpleDateFormat sdf5 = new SimpleDateFormat("yyyyMMddHHmmss");
	
	Logger logger = LoggerFactory.getLogger(NoticeFacade.class);

	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	@Autowired
	private ConvertAddressService convertAddressService;
	
	@Autowired
	private ViewContentService viewContentService;
	
	@Autowired
	private CrewInfoService crewInfoService;
	
	@Autowired
	private ViewRoleService viewRoleService;
	
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CrewContactService crewContactService;
	
	
	@Autowired
	private SceneViewInfoService sceneViewInfoService;
	
	/**
	 * 获取通告单列表
	 * @param userId 用户ID
	 * @param crewId	剧组ID
	 * @return
	 */
	@RequestMapping("/obtainNoticeList")
	@ResponseBody
	public Object obtainNoticeList(String userId, String crewId, Integer pageSize, Integer pageNo) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(crewId)) {
				throw new IllegalArgumentException("未获取到剧组信息");
			}
			Page page = null;
			if (pageSize != null && pageNo != null) {
				page = new Page();
				page.setPagesize(pageSize);
				page.setPageNo(pageNo);
			}
			
			CrewInfoModel crewInfo = this.crewInfoService.queryById(crewId);
			
			List<Map<String, Object>> noticeList = this.noticeService.queryNoticeInfoWithSomeColumns(crewId, userId, page, false);
			
			for (Map<String, Object> notice : noticeList) {
				Date noticeDate = (Date) notice.get("noticeDate");
				String noticeDateStr = sdf2.format(noticeDate);
				notice.remove("noticeDate");
				notice.put("noticeDate", noticeDateStr);
				
				Date updateTime = (Date) notice.get("updateTime");
				String updateTimeStr = sdf3.format(updateTime);
				notice.remove("updateTime");
				notice.put("updateTime", updateTimeStr);

				Date createTime = (Date) notice.get("createTime");
				String createTimeStr = sdf3.format(createTime);
				notice.remove("createTime");
				notice.put("createTime", createTimeStr);
				
				Date noticeTimeUpdateTime = (Date) notice.get("noticeTimeUpdateTime");
				String noticeTimeUpdateTimeStr = sdf1.format(noticeTimeUpdateTime);
				notice.remove("noticeTimeUpdateTime");
				notice.put("noticeTimeUpdateTime", noticeTimeUpdateTimeStr);
				
				if (notice.get("publishTime") != null) {
					Date publishTime = (Date) notice.get("publishTime");
					String publishTimeStr = sdf1.format(publishTime);
					notice.remove("publishTime");
					notice.put("publishTime", publishTimeStr);
				}
			}
			
			resultMap.put("noticeList", noticeList);
			resultMap.put("crewName", crewInfo.getCrewName());
			resultMap.put("pageCount", page.getPageCount());
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，获取通告单列表失败", e);
			throw new IllegalArgumentException("未知异常，获取通告单列表失败");
		}
		return resultMap;
	}
	
	/**
	 * 获取通告单列表(日历格式版)
	 * @param userId 用户ID
	 * @param crewId	剧组ID
	 * @return
	 */
	@RequestMapping("/obtainCalendarNoticeList")
	@ResponseBody
	public Object obtainCalendarNoticeList(HttpServletRequest request, String userId, String crewId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(crewId)) {
				throw new IllegalArgumentException("未获取到剧组信息");
			}
			
			List<Map<String, Object>> noticeList = this.noticeService.queryNoticeInfoWithSomeColumns(crewId, userId, null, false);
			
			/*
			 * 把noticeList按照拍摄时间（天）分组
			 */
			//把noticeList数据封装到dayGroupNoticeMap中
			Map<String, List<NoticeInfoDto>> dayGroupNoticeMap = new HashMap<String, List<NoticeInfoDto>>();	//key为通告单日期（yyyy-MM-dd），value为这一天的所有通告单列表
			for (Map<String, Object> notice : noticeList) {
				NoticeInfoDto noticeInfoDto = new NoticeInfoDto();
				noticeInfoDto.setNoticeId((String) notice.get("noticeId"));
				noticeInfoDto.setNoticeName((String) notice.get("noticeName"));
				noticeInfoDto.setVersion((String) notice.get("version"));
				noticeInfoDto.setGroupName((String) notice.get("groupName"));
				noticeInfoDto.setCanceledStatus((Integer) notice.get("canceledStatus"));
				
				Date noticeDate = (Date) notice.get("noticeDate");
				String noticeDateStr = sdf2.format(noticeDate);
				noticeInfoDto.setNoticeDate(noticeDateStr);
				
				if (notice.get("publishTime") != null) {
					Date publishTime = (Date) notice.get("publishTime");
					String publishTimeStr = sdf1.format(publishTime);
					noticeInfoDto.setPublishTime(publishTimeStr);
				}
				
				//反馈状态 1：未收取  2：已收取   3：已查看
				Integer backStatus = (Integer) notice.get("backStatus");
				
				// TODO hasReceived字段弃用，可以删掉				
				if (backStatus == null || backStatus != NoticePushFedBackStatus.NotReceived.getValue()) {
					noticeInfoDto.setHasReceived(true);
				}
				if(backStatus == null) {//兼容老数据
					noticeInfoDto.setBackStatus(NoticePushFedBackStatus.Readed.getValue());
				} else {
					noticeInfoDto.setBackStatus(backStatus);
				}
				
				if (dayGroupNoticeMap.containsKey(noticeDateStr)) {
					List<NoticeInfoDto> dayNoticeList = dayGroupNoticeMap.get(noticeDateStr);
					dayNoticeList.add(noticeInfoDto);
				} else {
					List<NoticeInfoDto> dayNoticeList = new ArrayList<NoticeInfoDto>();
					dayNoticeList.add(noticeInfoDto);
					dayGroupNoticeMap.put(noticeDateStr, dayNoticeList);
				}
			}
			
			//把dayGroupNoticeMap封装到dayGroupNoticeList中
			List<DayGroupNoticeDto> dayGroupNoticeList = new ArrayList<DayGroupNoticeDto>();
			Set<String> dayKeySet = dayGroupNoticeMap.keySet();
			for (String noticeDate : dayKeySet) {
				DayGroupNoticeDto dayGroupNoticeDto = new DayGroupNoticeDto();
				dayGroupNoticeDto.setDay(noticeDate);
				dayGroupNoticeDto.setNoticeList(dayGroupNoticeMap.get(noticeDate));
				
				dayGroupNoticeList.add(dayGroupNoticeDto);
			}
			
			//排序
			Collections.sort(dayGroupNoticeList, new Comparator<DayGroupNoticeDto>() {
				@Override
				public int compare(DayGroupNoticeDto o1, DayGroupNoticeDto o2) {
					int result = 0;
					
					String day1 = o1.getDay();
					String day2 = o2.getDay();
					
					try {
						Date date1 = sdf2.parse(day1);
						Date date2 = sdf2.parse(day2);
						
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
			
			/*
			 * 把dayGroupNoticeList按照拍摄日期（月）分组
			 */
			Map<String, List<DayGroupNoticeDto>> moonGroupNoticeMap = new HashMap<String, List<DayGroupNoticeDto>>();
			for (DayGroupNoticeDto dayGroupNoticeDto : dayGroupNoticeList) {
				String day = dayGroupNoticeDto.getDay();
				String moon = this.sdf4.format(this.sdf2.parse(day));
				
				if (moonGroupNoticeMap.containsKey(moon)) {
					List<DayGroupNoticeDto> myDayGroupNoticeList = moonGroupNoticeMap.get(moon);
					myDayGroupNoticeList.add(dayGroupNoticeDto);
				} else {
					List<DayGroupNoticeDto> myDayGroupNoticeList = new ArrayList<DayGroupNoticeDto>();
					myDayGroupNoticeList.add(dayGroupNoticeDto);
					
					moonGroupNoticeMap.put(moon, myDayGroupNoticeList);
				}
			}
			
			List<MonthGroupNoticeDto> monthGroupNoticeList = new ArrayList<MonthGroupNoticeDto>();
			Set<String> moonKeySet = moonGroupNoticeMap.keySet();
			for (String moon : moonKeySet) {
				MonthGroupNoticeDto monthGroupNoticeDto = new MonthGroupNoticeDto();
				monthGroupNoticeDto.setMonth(moon);
				monthGroupNoticeDto.setDayGroupNoticeList(moonGroupNoticeMap.get(moon));

				monthGroupNoticeList.add(monthGroupNoticeDto);
			}
			
			//排序
			Collections.sort(monthGroupNoticeList, new Comparator<MonthGroupNoticeDto>() {
				@Override
				public int compare(MonthGroupNoticeDto o1, MonthGroupNoticeDto o2) {
					int result = 0;
					
					String moon1 = o1.getMonth();
					String moon2 = o2.getMonth();
					
					try {
						Date date1 = sdf4.parse(moon1);
						Date date2 = sdf4.parse(moon2);
						
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
			
			resultMap.put("monthGroupNoticeList", monthGroupNoticeList);
			
			this.sysLogService.saveSysLogForApp(request, "查询通告单列表(日历版)", userInfo.getClientType(), NoticeInfoModel.TABLE_NAME, null, 0);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，获取通告单列表失败", e);
			this.sysLogService.saveSysLogForApp(request, "查询通告单列表(日历版)失败：" + e.getMessage(), 
					userInfo.getClientType(), NoticeInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，获取通告单列表失败");
		}
		return resultMap;
	}
	
	/**
	 * 获取通告单列表(日历格式版)
	 * @param userId 用户ID
	 * @param crewId	剧组ID
	 * @return
	 */
	@RequestMapping("/obtainCalendarNoticeListForClip")
	@ResponseBody
	public Object obtainCalendarNoticeListForClip(HttpServletRequest request, String userId, String crewId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(crewId)) {
				throw new IllegalArgumentException("未获取到剧组信息");
			}
			
			List<Map<String, Object>> noticeList = this.noticeService.queryNoticeInfoWithSomeColumns(crewId, userId, null, true);
			
			/*
			 * 把noticeList按照拍摄时间（天）分组
			 */
			//把noticeList数据封装到dayGroupNoticeMap中
			Map<String, List<NoticeInfoDto>> dayGroupNoticeMap = new HashMap<String, List<NoticeInfoDto>>();	//key为通告单日期（yyyy-MM-dd），value为这一天的所有通告单列表
			for (Map<String, Object> notice : noticeList) {
				NoticeInfoDto noticeInfoDto = new NoticeInfoDto();
				noticeInfoDto.setNoticeId((String) notice.get("noticeId"));
				noticeInfoDto.setNoticeName((String) notice.get("noticeName"));
				noticeInfoDto.setVersion((String) notice.get("version"));
				noticeInfoDto.setGroupName((String) notice.get("groupName"));
				noticeInfoDto.setCanceledStatus((Integer) notice.get("canceledStatus"));
				
				Date noticeDate = (Date) notice.get("noticeDate");
				String noticeDateStr = sdf2.format(noticeDate);
				noticeInfoDto.setNoticeDate(noticeDateStr);
				
				if (notice.get("publishTime") != null) {
					Date publishTime = (Date) notice.get("publishTime");
					String publishTimeStr = sdf1.format(publishTime);
					noticeInfoDto.setPublishTime(publishTimeStr);
				}
				
				Integer backStatus = (Integer) notice.get("backStatus");
				if (backStatus == null || backStatus != NoticePushFedBackStatus.NotReceived.getValue()) {
					noticeInfoDto.setHasReceived(true);
				}
				
				if (dayGroupNoticeMap.containsKey(noticeDateStr)) {
					List<NoticeInfoDto> dayNoticeList = dayGroupNoticeMap.get(noticeDateStr);
					dayNoticeList.add(noticeInfoDto);
				} else {
					List<NoticeInfoDto> dayNoticeList = new ArrayList<NoticeInfoDto>();
					dayNoticeList.add(noticeInfoDto);
					dayGroupNoticeMap.put(noticeDateStr, dayNoticeList);
				}
			}
			
			//把dayGroupNoticeMap封装到dayGroupNoticeList中
			List<DayGroupNoticeDto> dayGroupNoticeList = new ArrayList<DayGroupNoticeDto>();
			Set<String> dayKeySet = dayGroupNoticeMap.keySet();
			for (String noticeDate : dayKeySet) {
				DayGroupNoticeDto dayGroupNoticeDto = new DayGroupNoticeDto();
				dayGroupNoticeDto.setDay(noticeDate);
				dayGroupNoticeDto.setNoticeList(dayGroupNoticeMap.get(noticeDate));
				
				dayGroupNoticeList.add(dayGroupNoticeDto);
			}
			
			//排序
			Collections.sort(dayGroupNoticeList, new Comparator<DayGroupNoticeDto>() {
				@Override
				public int compare(DayGroupNoticeDto o1, DayGroupNoticeDto o2) {
					int result = 0;
					
					String day1 = o1.getDay();
					String day2 = o2.getDay();
					
					try {
						Date date1 = sdf2.parse(day1);
						Date date2 = sdf2.parse(day2);
						
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
			
			/*
			 * 把dayGroupNoticeList按照拍摄日期（月）分组
			 */
			Map<String, List<DayGroupNoticeDto>> moonGroupNoticeMap = new HashMap<String, List<DayGroupNoticeDto>>();
			for (DayGroupNoticeDto dayGroupNoticeDto : dayGroupNoticeList) {
				String day = dayGroupNoticeDto.getDay();
				String moon = this.sdf4.format(this.sdf2.parse(day));
				
				if (moonGroupNoticeMap.containsKey(moon)) {
					List<DayGroupNoticeDto> myDayGroupNoticeList = moonGroupNoticeMap.get(moon);
					myDayGroupNoticeList.add(dayGroupNoticeDto);
				} else {
					List<DayGroupNoticeDto> myDayGroupNoticeList = new ArrayList<DayGroupNoticeDto>();
					myDayGroupNoticeList.add(dayGroupNoticeDto);
					
					moonGroupNoticeMap.put(moon, myDayGroupNoticeList);
				}
			}
			
			List<MonthGroupNoticeDto> monthGroupNoticeList = new ArrayList<MonthGroupNoticeDto>();
			Set<String> moonKeySet = moonGroupNoticeMap.keySet();
			for (String moon : moonKeySet) {
				MonthGroupNoticeDto monthGroupNoticeDto = new MonthGroupNoticeDto();
				monthGroupNoticeDto.setMonth(moon);
				monthGroupNoticeDto.setDayGroupNoticeList(moonGroupNoticeMap.get(moon));

				monthGroupNoticeList.add(monthGroupNoticeDto);
			}
			
			//排序
			Collections.sort(monthGroupNoticeList, new Comparator<MonthGroupNoticeDto>() {
				@Override
				public int compare(MonthGroupNoticeDto o1, MonthGroupNoticeDto o2) {
					int result = 0;
					
					String moon1 = o1.getMonth();
					String moon2 = o2.getMonth();
					
					try {
						Date date1 = sdf4.parse(moon1);
						Date date2 = sdf4.parse(moon2);
						
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
			
			resultMap.put("monthGroupNoticeList", monthGroupNoticeList);
			
			this.sysLogService.saveSysLogForApp(request, "查询现场日志列表(日历版)", userInfo.getClientType(), NoticeInfoModel.TABLE_NAME, null, 0);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，获取现场日志列表失败", e);
			this.sysLogService.saveSysLogForApp(request, "查询现场日志列表(日历版)失败：" + e.getMessage(), 
					userInfo.getClientType(), NoticeInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，获取现场日志列表失败");
		}
		return resultMap;
	}
	
	
	
	/**
	 * 手机端获取通告单的主要详细信息接口
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	@RequestMapping("/obtainNoticeDetail")
	@ResponseBody
	public Object obtainNoticeDetail(HttpServletRequest request, String crewId, String noticeId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("未获取到通告单信息");
			}
			
			//剧组通告时间信息
			NoticeTimeDto noticeTimeDto = new NoticeTimeDto();
			//通告单演员信息
			List<NoticeRoleTimeDto> noticeRoleTimeDtoList = new ArrayList<NoticeRoleTimeDto>();
			//场次信息
			List<LocationViewDto> locationViewDtoList = new ArrayList<LocationViewDto>();
			//场景拍摄地点信息
			// TODO: 无用，可以删掉
			List<Map<String, Object>> viewLocationInfoList = new ArrayList<Map<String,Object>>();
			
			
			NoticeInfoModel noticeInfo = this.noticeService.getNotice(noticeId);
			if (noticeInfo == null) {
				throw new IllegalArgumentException("不存在的通告单");
			}
			//场次列表信息
			List<Map<String, Object>> viewList = this.viewInfoService.queryNoticeViewList(crewId, null, noticeId, null);
			
			//将场景地点的经纬度添加到集合中
			for (Map<String, Object> viewMap : viewList) {
				if (StringUtils.isNotBlank((String)viewMap.get("shootLocation"))) {
					Map<String, Object> locationInfoMap = new HashMap<String, Object>();
					locationInfoMap.put("shootLocation", viewMap.get("shootLocation"));
					locationInfoMap.put("vLongitude", viewMap.get("vLongitude"));
					locationInfoMap.put("vLatitude", viewMap.get("vLatitude"));
					
					viewLocationInfoList.add(locationInfoMap);
				}
			}
			
			//对场景地点的集合去重
			if (viewLocationInfoList != null && viewLocationInfoList.size() > 0) {
				//对结果进行去重
				for (int j = 0; j < viewLocationInfoList.size(); j++) {
					for(int k =  viewLocationInfoList.size()-1; k > j; k--) {
						Map<String, Object> firstMap = viewLocationInfoList.get(j);
						Map<String, Object> secondMap = viewLocationInfoList.get(k);
						String firstLocation = (String) firstMap.get("shootLocation");
						if (firstLocation == null) {
							firstLocation = "";
						}
						String secondLocation = (String) secondMap.get("shootLocation");
						if (secondLocation == null) {
							secondLocation = "";
						}
						
						if (firstLocation.equals(secondLocation)) {
							viewLocationInfoList.remove(secondMap);
						}
					}
				}
			}
			
			String viewIds = "";
			for (Map<String, Object> viewInfo : viewList) {
				viewIds+=viewInfo.get("viewId")+",";
			}
			if (!StringUtils.isBlank(viewIds)) {
				viewIds.substring(0, viewIds.length()-1);
			}
			
			//通告单时间信息
			noticeTimeDto = this.genNoticeTimeDto(noticeInfo, crewId, viewList);
			//设置经纬度信息
			noticeTimeDto.setViewLocationInfoList(viewLocationInfoList);
			//统计信息
			String statistics = this.genStatisticsInfo(viewList);
			noticeTimeDto.setStatistics(statistics);
			
			//演员信息
			noticeRoleTimeDtoList = this.genMainRoleInfo(crewId, userId, viewIds, noticeId);
			
			/*
			 * 按照拍摄地分组的场景信息
			 */
			if (viewList != null && viewList.size() > 0) {
				locationViewDtoList = this.genLocationViewInfo(crewId, noticeId, viewList);
			}
			
			//演员通告单信息
			Map<String, Object> actorNoticeInfo = this.genActorNoticeInfo(crewId, userId, noticeId);
			
			//是否需要反馈
			boolean needFedback = false;
			boolean isFedback = false;
			String version = this.sdf5.format(this.sdf1.parse(noticeTimeDto.getNoticeTimeUpdateTime()));
			NoticePushFedBackModel toBackInfo = this.noticeService.queryToBackInfoByNoticeInfo(crewId, noticeId, version, userId);
			if (toBackInfo != null && toBackInfo.getNeedFedBack()) {
				needFedback = true;
			}
			if (toBackInfo == null) {
				isFedback = true;
			}
			
			resultMap.put("needFedback", needFedback);
			resultMap.put("isFedback", isFedback);
			
			resultMap.put("noticeTime", noticeTimeDto);
			resultMap.put("noticeRoleTimeList", noticeRoleTimeDtoList);
			resultMap.put("locationViewList", locationViewDtoList);
			resultMap.put("actorNoticeInfo", actorNoticeInfo);
			
			this.sysLogService.saveSysLogForApp(request, "查询通告单的主要详细信息", userInfo.getClientType(), NoticeInfoModel.TABLE_NAME, noticeId, 0);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
			
		} catch (Exception e) {
			logger.error("未知异常，查询通告单详细信息失败", e);
			this.sysLogService.saveSysLogForApp(request, "查询通告单的主要详细信息失败：" + e.getMessage(), 
					userInfo.getClientType(), NoticeInfoModel.TABLE_NAME, noticeId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，查询通告单详细信息失败");
		}
		return resultMap;
	}
	
	/**
	 * 生成气氛内外景的统计信息
	 * @return
	 */
	private String genStatisticsInfo(List<Map<String, Object>> list) {
		DecimalFormat df = new DecimalFormat("0.0");
		String statistics = "";
		
		List<Map<String, Object>> innerSiteViewList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> outerSiteViewList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> inoutSiteViewList = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> viewMap:list){
			//计算气氛/内外景统计信息
			String site = (String) viewMap.get("site");
			if (Arrays.asList(Constants.INNERSITEARRAY).contains(site)) {
				innerSiteViewList.add(viewMap);
			}
			if (Arrays.asList(Constants.OUTERSITEARRAY).contains(site)) {
				outerSiteViewList.add(viewMap);
			}
			if (Arrays.asList(Constants.INOUTSITEARRAY).contains(site)) {
				inoutSiteViewList.add(viewMap);
			}
		}
		
		//内景统计
		Map<String, List<Map<String, Object>>> innerSiteAtmosph = new HashMap<String, List<Map<String, Object>>>();
		for (Map<String, Object> viewMap : innerSiteViewList) {
			String atmosphereName = (String) viewMap.get("atmosphereName");
			if (innerSiteAtmosph.containsKey(atmosphereName)) {
				innerSiteAtmosph.get(atmosphereName).add(viewMap);
			} else {
				List<Map<String, Object>> viewList = new ArrayList<Map<String, Object>>();
				viewList.add(viewMap);
				innerSiteAtmosph.put(atmosphereName, viewList);
			}
		}
		Set<String> innerSiteKeySet = innerSiteAtmosph.keySet();
		for (String innerAtmosph : innerSiteKeySet) {
			List<Map<String, Object>> myViewList = innerSiteAtmosph.get(innerAtmosph);
			double pageCount = 0;
			for (Map<String, Object> view : myViewList) {
				pageCount += (Double) view.get("pageCount");
			}
			
			if (StringUtils.isBlank(innerAtmosph)) {
				innerAtmosph = "";
			}
			
			statistics += myViewList.size() + "场" + innerAtmosph + "内" + df.format(pageCount) + "页\n";
		}
		
		
		//外景统计
		Map<String, List<Map<String, Object>>> outSiteAtmosph = new HashMap<String, List<Map<String, Object>>>();
		for (Map<String, Object> viewMap : outerSiteViewList) {
			String atmosphereName = (String) viewMap.get("atmosphereName");
			if (outSiteAtmosph.containsKey(atmosphereName)) {
				outSiteAtmosph.get(atmosphereName).add(viewMap);
			} else {
				List<Map<String, Object>> viewList = new ArrayList<Map<String, Object>>();
				viewList.add(viewMap);
				outSiteAtmosph.put(atmosphereName, viewList);
			}
		}
		Set<String> outSiteKeySet = outSiteAtmosph.keySet();
		for (String outAtmosph : outSiteKeySet) {
			List<Map<String, Object>> myViewList = outSiteAtmosph.get(outAtmosph);
			double pageCount = 0;
			for (Map<String, Object> view : myViewList) {
				pageCount += (Double) view.get("pageCount");
			}
			
			if (StringUtils.isBlank(outAtmosph)) {
				outAtmosph = "";
			}
			
			statistics += myViewList.size() + "场" + outAtmosph + "外" + df.format(pageCount) + "页\n";
		}
		
		//内外景统计
		Map<String, List<Map<String, Object>>> inoutSiteAtmosph = new HashMap<String, List<Map<String, Object>>>();
		for (Map<String, Object> viewMap : inoutSiteViewList) {
			String atmosphereName = (String) viewMap.get("atmosphereName");
			if (inoutSiteAtmosph.containsKey(atmosphereName)) {
				inoutSiteAtmosph.get(atmosphereName).add(viewMap);
			} else {
				List<Map<String, Object>> viewList = new ArrayList<Map<String, Object>>();
				viewList.add(viewMap);
				inoutSiteAtmosph.put(atmosphereName, viewList);
			}
		}
		Set<String> inoutSiteKeySet = inoutSiteAtmosph.keySet();
		for (String inoutAtmosph : inoutSiteKeySet) {
			List<Map<String, Object>> myViewList = inoutSiteAtmosph.get(inoutAtmosph);
			double pageCount = 0;
			for (Map<String, Object> view : myViewList) {
				pageCount += (Double) view.get("pageCount");
			}
			
			if (StringUtils.isBlank(inoutAtmosph)) {
				inoutAtmosph = "";
			}
			
			statistics += myViewList.size() + "场" + inoutAtmosph + "内外" + df.format(pageCount) + "页\n";
		}
		
		
		return statistics;
	}

	/**
	 * 生成通告单场景信息
	 * 返回的场景信息按照拍摄地点分组
	 * @param crewId
	 * @param noticeId
	 * @param locationViewDtoList
	 * @param viewList
	 * @param pageCount
	 * @return
	 * @throws Exception
	 */
	private List<LocationViewDto> genLocationViewInfo(String crewId, String noticeId, List<Map<String, Object>> viewList)
			throws Exception {
		List<LocationViewDto> locationViewDtoList = new ArrayList<LocationViewDto>();
		
		String preShootLocation = "";
		String preShootLocationId = "";
		String locationViewIds = "";
		String preVLatitude = "";
		String preVLongitude = "";
		
		LocationViewDto locationViewDto = new LocationViewDto();
		List<ViewInfoDto> viewInfoDtoList = new ArrayList<ViewInfoDto>();
		
		for(Map<String, Object> viewMap : viewList){
			ViewInfoDto viewInfoDto = new ViewInfoDto();
			if (viewMap.get("seriesNo") != null && !viewMap.get("seriesNo").equals("")) {
				viewInfoDto.setSeriesNo((Integer) viewMap.get("seriesNo"));
			}
			if (viewMap.get("viewNo") != null) {
				viewInfoDto.setViewNo((String) viewMap.get("viewNo"));
			}
			String viewAddress = "";
			if (viewMap.get("majorView") != null ) {
				viewAddress = (String) viewMap.get("majorView");
			}
			if (viewMap.get("minorView") != null ) {
				if (StringUtils.isBlank(viewAddress)) {
					viewAddress = (String) viewMap.get("minorView");
				}else {
					viewAddress = viewAddress + "," + (String) viewMap.get("minorView");
				}
			}
			if (viewMap.get("thirdLevelView") != null ) {
				if (StringUtils.isBlank(viewAddress)) {
					viewAddress = (String) viewMap.get("thirdLevelView");
				}else {
					viewAddress = viewAddress + "," + (String) viewMap.get("thirdLevelView");
				}
			}
			viewInfoDto.setViewLocation(viewAddress);
			
			if (viewMap.get("pageCount") != null && !viewMap.get("pageCount").equals("")) {
				viewInfoDto.setPageCount((Double)viewMap.get("pageCount"));
			}
			if (viewMap.get("atmosphereName") != null) {
				viewInfoDto.setAtmosphereName((String) viewMap.get("atmosphereName"));
			}
			if (viewMap.get("site") != null) {
				viewInfoDto.setSite((String) viewMap.get("site"));
			}
			if (viewMap.get("viewType") != null) {
				viewInfoDto.setViewType((Integer) viewMap.get("viewType"));
			}
			if (viewMap.get("mainContent") != null) {
				viewInfoDto.setMainContent((String) viewMap.get("mainContent"));
			}
			if (viewMap.get("roleList") != null) {
				//主要演员
				String roleList = (String) viewMap.get("roleList");
				viewInfoDto.setMainRoleNames(roleList);
			}
			if (viewMap.get("roleShortNames") != null) {
				String roleShortNames = (String) viewMap.get("roleShortNames");
				viewInfoDto.setMainRoleShortNames(roleShortNames);
			}
			if (viewMap.get("guestRoleList") != null) {
				viewInfoDto.setGuestRoleNames((String) viewMap.get("guestRoleList"));
			}
			if (viewMap.get("massRoleList") != null) {
				viewInfoDto.setMassRoleNames((String) viewMap.get("massRoleList"));
			}
			if (viewMap.get("clothesName") != null) {
				viewInfoDto.setClothesNames((String) viewMap.get("clothesName"));
			}
			if (viewMap.get("makeupName") != null) {
				viewInfoDto.setMakeupNames((String) viewMap.get("makeupName"));
			}
			if (viewMap.get("propsList") != null) {
				viewInfoDto.setPropNames((String) viewMap.get("propsList"));
			}
			if (viewMap.get("specialPropsList") != null) {
				viewInfoDto.setSpecialPropName((String) viewMap.get("specialPropsList"));
			}
			if (viewMap.get("viewRemark") != null) {
				viewInfoDto.setRemark((String) viewMap.get("viewRemark"));
			}
			if (viewMap.get("advertName") != null) {
				viewInfoDto.setInsertAdverts((String) viewMap.get("advertName"));
			}
			if (viewMap.get("prepareStatus") != null && (Integer)viewMap.get("prepareStatus") == 1) {
				viewInfoDto.setPrepareStatus(true);
			} else {
				viewInfoDto.setPrepareStatus(false);
			}
			
			if (StringUtils.isBlank(preShootLocation)) {	//第一场信息
				viewInfoDtoList.add(viewInfoDto);
				locationViewIds += viewMap.get("viewId") + ",";
				
			} else if (!preShootLocation.equals((String) viewMap.get("shootLocation"))) {
				//此处开始转场
				locationViewIds = locationViewIds.substring(0, locationViewIds.length() - 1);
				ConvertAddressModel convertAddress = this.convertAddressService.queryByLocationViewIds(crewId, noticeId, preShootLocationId, locationViewIds);
				
				locationViewDto.setShootLocation(preShootLocation);
				locationViewDto.setViewInfoList(viewInfoDtoList);
				if (convertAddress != null) {
					String convertRemark = convertAddress.getRemark();
					convertRemark = convertRemark == null ? "" : convertRemark;
					
					locationViewDto.setConvertRemark(convertRemark);
				}
				locationViewDto.setvLatitude(preVLatitude);
				locationViewDto.setvLongitude(preVLongitude);
				locationViewDtoList.add(locationViewDto);
				
				locationViewDto = new LocationViewDto();
				viewInfoDtoList = new ArrayList<ViewInfoDto>();
				locationViewIds = "";
				viewInfoDtoList.add(viewInfoDto);
				locationViewIds += viewMap.get("viewId") + ",";
			} else {
				viewInfoDtoList.add(viewInfoDto);
				locationViewIds += viewMap.get("viewId") + ",";
			}
			
			//上一场实拍摄地
			preShootLocation = (String) viewMap.get("shootLocation");
			preShootLocationId = (String) viewMap.get("shootLocationId");
			preVLatitude = (String) viewMap.get("vLatitude");
			preVLongitude = (String) viewMap.get("vLongitude");
		}
		
		//处理最后一组实拍景地
		locationViewIds = locationViewIds.substring(0, locationViewIds.length() - 1);
		ConvertAddressModel convertAddress = this.convertAddressService.queryByLocationViewIds(crewId, noticeId, preShootLocationId, locationViewIds);
		locationViewDto.setShootLocation(preShootLocation);
		locationViewDto.setViewInfoList(viewInfoDtoList);
		if (convertAddress != null) {
			String convertRemark = convertAddress.getRemark();
			convertRemark = convertRemark == null ? "" : convertRemark;
			
			locationViewDto.setConvertRemark(convertRemark);
		}
		locationViewDto.setvLatitude(preVLatitude);
		locationViewDto.setvLongitude(preVLongitude);
		locationViewDtoList.add(locationViewDto);
		
		return locationViewDtoList;
	}

	/**
	 * 生成通告单下演员时间表信息
	 * @param viewIds
	 * @return
	 */
	private List<NoticeRoleTimeDto> genMainRoleInfo(String crewId, String userId, String viewIds, String noticeId) {
		List<NoticeRoleTimeDto> noticeRoleTimeDtoList = new ArrayList<NoticeRoleTimeDto>();
		//所有主演信息
		List<Map<String, Object>> roleSignList = this.viewRoleService.queryViewRoleListByNoticeId(noticeId);
		//查询当前用户关联的角色
		List<ViewRoleModel> viewRoleList = this.viewRoleService.queryUserRoleInfo(crewId, userId);
		Set<String> viewRoleSet = new HashSet<String>();
		if(viewRoleList != null && viewRoleList.size() > 0) {
			for(ViewRoleModel viewRole : viewRoleList) {
				viewRoleSet.add(viewRole.getViewRoleId());
			}
		}
		
		for (Map<String, Object> roleInfo : roleSignList) {
			NoticeRoleTimeDto noticeRoleTimeDto = new NoticeRoleTimeDto();
			noticeRoleTimeDto.setViewRoleName((String) roleInfo.get("viewRoleName"));
			noticeRoleTimeDto.setActorName((String) roleInfo.get("actorName"));
			if (roleInfo.get("shortName") == null) {
				noticeRoleTimeDto.setShortName("√");
			} else {
				noticeRoleTimeDto.setShortName((String) roleInfo.get("shortName"));
			}
			
			noticeRoleTimeDto.setMakeup((String) roleInfo.get("makeup"));
			noticeRoleTimeDto.setArriveTime((String) roleInfo.get("arriveTime"));
			noticeRoleTimeDto.setGiveMakeupTime((String) roleInfo.get("giveMakeupTime"));
			
			if(viewRoleSet != null && !viewRoleSet.isEmpty() && viewRoleSet.contains((String) roleInfo.get("viewRoleId"))) {
				noticeRoleTimeDto.setCurrentUserRole(true);
			}
			
			noticeRoleTimeDtoList.add(noticeRoleTimeDto);
		}
		
		return noticeRoleTimeDtoList;
	}

	/**
	 * 生成通告单时间信息
	 * @param noticeTimeDto
	 * @param noticeInfo
	 * @param lastNoticeTime
	 * @param list
	 * @throws Exception 
	 */
	private NoticeTimeDto genNoticeTimeDto(NoticeInfoModel noticeInfo, String crewId, List<Map<String, Object>> list) throws Exception {
		NoticeTimeDto noticeTimeDto = new NoticeTimeDto();
		
		NoticeTimeModel myNoticeTime = this.noticeService.queryNoticeTimeByNoticeId(noticeInfo.getNoticeId());
		//通告单日期是周几
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(noticeInfo.getNoticeDate());
//		int month = calendar.get(Calendar.MONTH);
//		if(month == 12){
//			month = 1;
//		} else {
//			month++;
//		}
		//通告单在一周中的某天
		Map<Integer, String> dayOfWeekMap = new HashMap<Integer, String>();
		dayOfWeekMap.put(1, "一");
		dayOfWeekMap.put(2, "二");
		dayOfWeekMap.put(3, "三");
		dayOfWeekMap.put(4, "四");
		dayOfWeekMap.put(5, "五");
		dayOfWeekMap.put(6, "六");
		dayOfWeekMap.put(7, "七");
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		dayOfWeek = dayOfWeek - 1;
		if(dayOfWeek == 0){
			dayOfWeek = 7;
		}
		noticeTimeDto.setWeekday("星期" + dayOfWeekMap.get(dayOfWeek));
		
		//通告单开机天数
		NoticeInfoModel firstNoticeInfo = noticeService.getFirstNotice(noticeInfo.getCrewId());	//第一封通告单
		noticeTimeDto.setShootDays(DateUtils.daysBetween(firstNoticeInfo.getNoticeDate(), noticeInfo.getNoticeDate()));
		
		//总场数
		noticeTimeDto.setTotalViewnum(list.size());
		//总页数
		BigDecimal pageCount=new BigDecimal(0);
		for(Map<String, Object> viewMap:list){
			pageCount=pageCount.add(new BigDecimal((Double)viewMap.get("pageCount")));
		}
		noticeTimeDto.setTotalPagenum(pageCount.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		
		noticeTimeDto.setNoticeName(noticeInfo.getNoticeName());
		noticeTimeDto.setVersion(myNoticeTime.getVersion());
		noticeTimeDto.setGroupDirector(myNoticeTime.getGroupDirector());
		if (myNoticeTime != null) {
			//早餐时间
			noticeTimeDto.setBreakfastTime(myNoticeTime.getBreakfastTime());
			//出发时间
			noticeTimeDto.setDepartureTime(myNoticeTime.getDepartureTime());
			//拍摄地点
			noticeTimeDto.setShootLocationInfos(myNoticeTime.getShootLocationInfos());
			//天气情况
			noticeTimeDto.setWeatherInfo(myNoticeTime.getWeatherInfo());
			//人员调度
			noticeTimeDto.setRoleInfo(myNoticeTime.getRoleInfo());
			//提示信息
			noticeTimeDto.setNote(myNoticeTime.getNote());
			//其他提示
			noticeTimeDto.setOtherTips(myNoticeTime.getRoleConvertRemark());
			//备注
			noticeTimeDto.setRemark(myNoticeTime.getRemark());
			//商植
			noticeTimeDto.setInsideAdvert(myNoticeTime.getInsideAdvert());
			//联系人
			noticeTimeDto.setNoticeContact(myNoticeTime.getNoticeContact());
			
			//通告单图片
			List<PictureDto> pictureDtoList = new ArrayList<PictureDto>();
			List<NoticePictureModel> noticePictureList = this.noticeService.queryNoticeImgByNotice(noticeInfo.getNoticeId(), this.sdf5.format(myNoticeTime.getUpdateTime()));
			for (NoticePictureModel noticePic : noticePictureList) {
				PictureDto pictureDto = new PictureDto();
				pictureDto.setName(noticePic.getName());
				pictureDto.setUploadTime(this.sdf1.format(noticePic.getUploadTime()));

				//大图片地址
				pictureDto.setBigPicurl(FileUtils.genPreviewPath(noticePic.getBigPicurl()));
				//小图片地址
				pictureDto.setSmallPicurl(FileUtils.genPreviewPath(noticePic.getSmallPicurl()));
				pictureDtoList.add(pictureDto);
			}
			noticeTimeDto.setPictureInfo(pictureDtoList);
			
			//发布的通告单最后修改时间
			Date noticeTimeUpdateTime = myNoticeTime.getUpdateTime();
			String noticeTimeUpdateTimeStr = this.sdf1.format(noticeTimeUpdateTime);
			noticeTimeDto.setNoticeTimeUpdateTime(noticeTimeUpdateTimeStr);
		}
		
		return noticeTimeDto;
	}
	
	/**
	 * 生成演员通告单信息
	 * @param userId
	 * @param noticeId
	 * @return
	 * @throws Exception 
	 */
	private Map<String, Object> genActorNoticeInfo (String crewId, String userId, String noticeId) throws Exception {
		/*
		 * 简单校验
		 */
		NoticeInfoModel noticeInfo = this.noticeService.getNotice(noticeId);
		if (noticeInfo == null) {
			throw new IllegalArgumentException("不存在的通告单");
		}
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		//查询用户扮演的角色信息(如果用户没有关联主要角色，则返回空)
		List<ViewRoleModel> userRoleList = this.viewRoleService.queryUserRoleInfo(crewId, userId);
		List<String> userRoleIds = new ArrayList<String>();
		List<String> userRoleNameList = new ArrayList<String>();
		for (ViewRoleModel viewRole : userRoleList) {
			if (viewRole.getViewRoleType() == ViewRoleType.MajorActor.getValue()) {
				userRoleIds.add(viewRole.getViewRoleId());
				userRoleNameList.add(viewRole.getViewRoleName());
			}
		}
		if (userRoleIds.size() == 0 || userRoleList == null || userRoleList.size() == 0) {
			return null;
		}
		
		//查询用户拥有的角色在该通告单中的有戏的场次信息，如果为空，表示演员在该通告单中没戏
		List<Map<String, Object>> viewList = this.viewInfoService.queryViewByNoticeRole(crewId, noticeId, userRoleIds);
		if (viewList == null || viewList.size() == 0) {
			return null;
		}
		String viewNos = "";	//获取场次列表信息
		List<String> cooperatorList = new ArrayList<String>();	//获取搭戏人列表
		List<String> hasViewRoleNameList = new ArrayList<String>();	//用户在通告单中所有场景中扮演的角色
		for (Map<String, Object> viewMap : viewList) {
			Integer seriesNo = (Integer) viewMap.get("seriesNo");
			String viewNo = (String) viewMap.get("viewNo");
			String viewRoleNames = (String) viewMap.get("viewRoleNames");
			
			String seriesViewNo = seriesNo + "-" + viewNo;
			viewNos += seriesViewNo + ",";
			if (!StringUtils.isBlank(viewRoleNames)) {
				String[] viewRoleNameArr = viewRoleNames.split(",");
				for (String viewRoleName : viewRoleNameArr) {
					if (!userRoleNameList.contains(viewRoleName) && !cooperatorList.contains(viewRoleName)) {
						cooperatorList.add(viewRoleName);
					}
					if (!hasViewRoleNameList.contains(viewRoleName) && userRoleNameList.contains(viewRoleName)) {
						hasViewRoleNameList.add(viewRoleName);
					}
				}
			}
		}
		String cooperators = "";
		for (String cooperator : cooperatorList) {
			cooperators += cooperator + ",";
		}
		String userRoleNames = "";
		for (String userRoleName : hasViewRoleNameList) {
			userRoleNames += userRoleName + ",";
		}
		
		//查询用户拥有的角色在通告单中的化妆信息，如果有多个角色，以到场时间最早的为主
		NoticeRoleTimeModel noticeRoleTime = this.noticeService.queryNoticeRoleTimeByNoticeIdAndRoleId(noticeId, userRoleIds);
		//化妆信息
		String makeup = "";
		String arriveTime = "";
		String giveMakeupTime = "";
		if (noticeRoleTime != null) {
			makeup = noticeRoleTime.getMakeup();
			if (noticeRoleTime.getArriveTime() != null) {
				arriveTime = noticeRoleTime.getArriveTime();
			}
			if (noticeRoleTime.getGiveMakeupTime() != null) {
				giveMakeupTime = noticeRoleTime.getGiveMakeupTime();
			}
		}
		
		
		//查询通告单下的所有拍摄地点信息，按照场景中拍摄地点先后排序
		List<SceneViewInfoModel> shootLocationList = this.sceneViewInfoService.queryShootLocationByNoticeId(crewId, noticeId);
		//转场信息
		String shootLocations = "";
		if (shootLocationList != null && shootLocationList.size() != 0) {
			for (SceneViewInfoModel shootLocation : shootLocationList) {
				String location = shootLocation.getVName();
				if(StringUtils.isNotBlank(location)){
					shootLocations += location + ",";
				}
			}
		}
		
		
		
		//处理返回数据
		if (!StringUtils.isBlank(userRoleNames)) {
			userRoleNames = userRoleNames.substring(0, userRoleNames.length() - 1);
		}
		if (!StringUtils.isBlank(viewNos)) {
			viewNos = viewNos.substring(0, viewNos.length() - 1);
		}
		if (!StringUtils.isBlank(cooperators)) {
			cooperators = cooperators.substring(0, cooperators.length() - 1);
		}
		if (!StringUtils.isBlank(shootLocations)) {
			shootLocations = shootLocations.substring(0, shootLocations.length() - 1);
//			shootLocations = "今天的拍摄地依次为" + shootLocations;
		}
		resultMap.put("roleNames", userRoleNames);
		resultMap.put("viewNos", viewNos);
		resultMap.put("converLocationInfo", shootLocations);
		resultMap.put("cooperators", cooperators);
		resultMap.put("makeup", makeup);
		resultMap.put("arriveTime", arriveTime);
		resultMap.put("giveMakeupTime", giveMakeupTime);
		
		return resultMap;
	}
	
	/**
	 * 获取演员通告单信息
	 * 如果是不符合条件的演员请求，则接口返回null
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainActorNoticeInfo")
	public Object obtainActorNoticeInfo(String crewId, String noticeId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			/*
			 * 简单校验
			 */
			NoticeInfoModel noticeInfo = this.noticeService.getNotice(noticeId);
			if (noticeInfo == null) {
				throw new IllegalArgumentException("不存在的通告单");
			}
			
			//查询用户扮演的角色信息(如果用户没有关联主要角色，则返回空)
			List<ViewRoleModel> userRoleList = this.viewRoleService.queryUserRoleInfo(crewId, userId);
			List<String> userRoleIds = new ArrayList<String>();
			List<String> userRoleNameList = new ArrayList<String>();
			for (ViewRoleModel viewRole : userRoleList) {
				if (viewRole.getViewRoleType() == ViewRoleType.MajorActor.getValue()) {
					userRoleIds.add(viewRole.getViewRoleId());
					userRoleNameList.add(viewRole.getViewRoleName());
				}
			}
			if (userRoleIds.size() == 0 || userRoleList == null || userRoleList.size() == 0) {
				return null;
			}
			
			//查询用户拥有的角色在该通告单中的有戏的场次信息，如果为空，表示演员在该通告单中没戏
			List<Map<String, Object>> viewList = this.viewInfoService.queryViewByNoticeRole(crewId, noticeId, userRoleIds);
			if (viewList == null || viewList.size() == 0) {
				return null;
			}
			String viewNos = "";	//获取场次列表信息
			List<String> cooperatorList = new ArrayList<String>();	//获取搭戏人列表
			List<String> hasViewRoleNameList = new ArrayList<String>();	//用户在通告单中所有场景中扮演的角色
			for (Map<String, Object> viewMap : viewList) {
				Integer seriesNo = (Integer) viewMap.get("seriesNo");
				String viewNo = (String) viewMap.get("viewNo");
				String viewRoleNames = (String) viewMap.get("viewRoleNames");
				
				String seriesViewNo = seriesNo + "-" + viewNo;
				viewNos += seriesViewNo + ",";
				if (!StringUtils.isBlank(viewRoleNames)) {
					String[] viewRoleNameArr = viewRoleNames.split(",");
					for (String viewRoleName : viewRoleNameArr) {
						if (!userRoleNameList.contains(viewRoleName) && !cooperatorList.contains(viewRoleName)) {
							cooperatorList.add(viewRoleName);
						}
						if (!hasViewRoleNameList.contains(viewRoleName) && userRoleNameList.contains(viewRoleName)) {
							hasViewRoleNameList.add(viewRoleName);
						}
					}
				}
			}
			String cooperators = "";
			for (String cooperator : cooperatorList) {
				cooperators += cooperator + ",";
			}
			String userRoleNames = "";
			for (String userRoleName : hasViewRoleNameList) {
				userRoleNames += userRoleName + ",";
			}
			
			//查询用户拥有的角色在通告单中的化妆信息，如果有多个角色，以到场时间最早的为主
			NoticeRoleTimeModel noticeRoleTime = this.noticeService.queryNoticeRoleTimeByNoticeIdAndRoleId(noticeId, userRoleIds);
			//化妆信息
			String makeup = "";
			String arriveTime = "";
			String giveMakeupTime = "";
			if (noticeRoleTime != null) {
				makeup = noticeRoleTime.getMakeup();
				arriveTime = noticeRoleTime.getArriveTime();
				giveMakeupTime = noticeRoleTime.getGiveMakeupTime();
			}
			
			
			//查询通告单下的所有拍摄地点信息，按照场景中拍摄地点先后排序
			List<SceneViewInfoModel> shootLocationList = this.sceneViewInfoService.queryShootLocationByNoticeId(crewId, noticeId);
			//转场信息
			String shootLocations = "";
			if (shootLocationList != null && shootLocationList.size() != 0) {
				for (SceneViewInfoModel shootLocation : shootLocationList) {
					String location = shootLocation.getVName();
					if(StringUtils.isNotBlank(location)){
						shootLocations += location + ",";
					}
				}
			}
			
			
			
			//处理返回数据
			if (!StringUtils.isBlank(userRoleNames)) {
				userRoleNames = userRoleNames.substring(0, userRoleNames.length() - 1);
			}
			if (!StringUtils.isBlank(viewNos)) {
				viewNos = viewNos.substring(0, viewNos.length() - 1);
			}
			if (!StringUtils.isBlank(cooperators)) {
				cooperators = cooperators.substring(0, cooperators.length() - 1);
			}
			if (!StringUtils.isBlank(shootLocations)) {
				shootLocations = shootLocations.substring(0, shootLocations.length() - 1);
//				shootLocations = "今天的拍摄地依次为" + shootLocations;
			}
			resultMap.put("roleNames", userRoleNames);
			resultMap.put("viewNos", viewNos);
			resultMap.put("cooperators", cooperators);
			resultMap.put("makeup", makeup);
			resultMap.put("arriveTime", arriveTime);
			resultMap.put("giveMakeupTime", giveMakeupTime);
			resultMap.put("converLocationInfo", shootLocations);
			
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取演员版通告单失败", e);
		}
		return resultMap;
	}
	
	/**
	 * 反馈通告单push接口
	 * @param crewId	剧组ID
	 * @param userId	用户ID
	 * @param backStatus	反馈状态
	 * @param isStaisfied 是否满意
	 * @param remark  备注
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/feedbackNoticePush")
	public Object feedbackNoticePush(HttpServletRequest request, String crewId, String userId, String noticeId, String noticeVersion, Integer backStatus, Boolean isSatisfied, String remark) {
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			
			if (StringUtils.isBlank(noticeVersion)) {
				throw new IllegalArgumentException("请提供通告单版本信息");
			}
			
			NoticePushFedBackModel toBackInfo = this.noticeService.queryToBackInfoByNoticeInfo(crewId, noticeId, noticeVersion, userId);
			if (toBackInfo != null) {
				toBackInfo.setBackStatus(backStatus);
				toBackInfo.setStatusUpdateTime(new Date());
				if (isSatisfied != null) {
					if (isSatisfied) {
						toBackInfo.setIsSatisfied(NoticeIsSatisfied.Satisfied.getValue());
					} else {
						toBackInfo.setIsSatisfied(NoticeIsSatisfied.NotSatisfied.getValue());
					}
				}
				toBackInfo.setRemark(remark);
				this.noticeService.updateNoticeFedBackInfo(toBackInfo);
			}
			
			this.sysLogService.saveSysLogForApp(request, "通告单已阅反馈", userInfo.getClientType(), NoticePushFedBackModel.TABLE_NAME, noticeId, 2);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch(Exception e) {
			logger.error("未知异常，反馈失败", e);
			this.sysLogService.saveSysLogForApp(request, "通告单已阅反馈失败：" + e.getMessage(), userInfo.getClientType(), NoticePushFedBackModel.TABLE_NAME, noticeId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，反馈失败");
		}
		return null;
	}
}
