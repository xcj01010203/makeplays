package com.xiaotu.makeplays.mobile.server.notice;

import java.lang.reflect.Type;
import java.text.CollationKey;
import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.reflect.TypeToken;
import com.xiaotu.makeplays.attachment.dto.AttachmentDto;
import com.xiaotu.makeplays.attachment.model.AttachmentModel;
import com.xiaotu.makeplays.attachment.service.AttachmentService;
import com.xiaotu.makeplays.goods.model.constants.GoodsType;
import com.xiaotu.makeplays.mobile.common.MobileRequest;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.mobile.server.common.BaseFacade;
import com.xiaotu.makeplays.mobile.server.notice.dto.ClipRequestDto;
import com.xiaotu.makeplays.mobile.server.notice.dto.DepartScoreRequestDto;
import com.xiaotu.makeplays.mobile.server.notice.dto.DepartmentEvaluateDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.AttendanceDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.ClipCommentDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.ClipInfoDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.ClipPropInfoDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.ClipViewInfoDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.RoleAttendanceDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.ShootAuditionDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.ShootLiveDto;
import com.xiaotu.makeplays.notice.model.NoticeInfoModel;
import com.xiaotu.makeplays.notice.model.NoticeTimeModel;
import com.xiaotu.makeplays.notice.model.clip.CameraInfoModel;
import com.xiaotu.makeplays.notice.model.clip.ClipCommentModel;
import com.xiaotu.makeplays.notice.model.clip.ClipPropModel;
import com.xiaotu.makeplays.notice.model.clip.DepartmentEvaluateModel;
import com.xiaotu.makeplays.notice.model.clip.LiveConvertAddModel;
import com.xiaotu.makeplays.notice.model.clip.RoleAttendanceModel;
import com.xiaotu.makeplays.notice.model.clip.ShootLiveModel;
import com.xiaotu.makeplays.notice.service.ClipService;
import com.xiaotu.makeplays.notice.service.NoticeService;
import com.xiaotu.makeplays.roleactor.model.constants.ViewRoleType;
import com.xiaotu.makeplays.roleactor.service.ViewRoleService;
import com.xiaotu.makeplays.shoot.model.ShootGroupModel;
import com.xiaotu.makeplays.shoot.service.ShootGroupService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.view.controller.dto.CancelViewDto;
import com.xiaotu.makeplays.view.controller.dto.SeriesNoDto;
import com.xiaotu.makeplays.view.controller.dto.ViewNoDto;
import com.xiaotu.makeplays.view.service.ViewInfoService;

/**
 * 场记单facade
 * @author xuchangjian 2015-11-9下午3:48:11
 */
@Controller
@RequestMapping("/interface/clipFacade")
public class ClipFacade extends BaseFacade{
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

	private SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");
	
	Logger logger = LoggerFactory.getLogger(ClipFacade.class);

	@Autowired
	private ClipService clipService;
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	@Autowired
	private ShootGroupService shootGroupSerivce;
	
	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private ViewRoleService viewRoleService;
	@Autowired
	private AttachmentService attachmentService;
	
	/**
	 * 获取场记单中下拉框基本信息
	 * @param crewId	剧组ID
	 * @param userId	用户ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainClipBaseSelectInfo")
	public Object obtainClipBaseSelectInfo(String crewId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			if (StringUtils.isBlank(crewId)) {
				throw new IllegalArgumentException("无效的剧组访问");
			}
			MobileUtils.checkUserValid(userId);
			
			//集场信息
			List<Map<String, Object>> seriesViewNos = this.viewInfoService.queryViewList(crewId, null);
			
			Map<Integer, List<String>> seriesViewNoMap = new TreeMap<Integer, List<String>>();	//存储集-场的对应的关系,key-集次  value-该集下所有场次
			List<Integer> seriesNoList = new ArrayList<Integer>();	//查出来的剧本信息中所有的集次
			for (Map<String, Object> viewInfo : seriesViewNos) {
				Integer seriesNo = (Integer) viewInfo.get("seriesNo");
				if (!seriesNoList.contains(seriesNo)) {
					seriesNoList.add(seriesNo);
				}
			}
			
			for (Integer seriesNo : seriesNoList) {
				List<String> viewList = new LinkedList<String>();
				for (Map<String, Object> viewInfo : seriesViewNos) {
					if (viewInfo.get("seriesNo") == seriesNo) {
						viewList.add((String) viewInfo.get("viewNo"));
					}
				}
				
				Comparator<String> sort = com.xiaotu.makeplays.utils.StringUtils.sort();
				Collections.sort(viewList, sort);
				seriesViewNoMap.put(seriesNo, viewList);
			}
			
			Set<Integer> keySet = seriesViewNoMap.keySet();
			Iterator<Integer> iter = keySet.iterator();
			
			List<SeriesNoDto> seriesNoDtoList = new ArrayList<SeriesNoDto>();
			while(iter.hasNext()) {
				Integer key = (Integer) iter.next();
				List<String> value = seriesViewNoMap.get(key);
				
				
				SeriesNoDto viewNoDto = new SeriesNoDto();
				viewNoDto.setSeriesNo(key);
				List<ViewNoDto> viewNoDtoList = new ArrayList<ViewNoDto>();
				for (String viewNo : value) {
					for (Map<String, Object> viewInfo : seriesViewNos) {
						Integer mySeriesNo = (Integer) viewInfo.get("seriesNo");
						String myViewNo = (String) viewInfo.get("viewNo");
						if (mySeriesNo == key && myViewNo.equals(viewNo)) {
							ViewNoDto viewnoDto = new ViewNoDto();
							viewnoDto.setViewNo(viewNo);
							
							int isManualSave = (Integer) viewInfo.get("isManualSave");
							if (isManualSave == 1) {
								viewnoDto.setIsManualSave(true);
							} else {
								viewnoDto.setIsManualSave(false);
							}
							viewnoDto.setViewId((String) viewInfo.get("viewId"));
							if (viewInfo.get("atmosphereName") != null) {
								viewnoDto.setAtmosphereName((String) viewInfo.get("atmosphereName"));
							}
							if (viewInfo.get("site") != null) {
								viewnoDto.setSite((String) viewInfo.get("site"));
							}
							
							viewNoDtoList.add(viewnoDto);
						}
					}
				}
				viewNoDto.setViewNoDtoList(viewNoDtoList);
				seriesNoDtoList.add(viewNoDto);
			}
			resultMap.put("seriesNoDtoList", seriesNoDtoList);
			
			//拍摄分组信息
			List<String> groupNameList = new ArrayList<String>();
			List<ShootGroupModel> shootGroupList = this.shootGroupSerivce.queryManyByCrewId(crewId);
			for (ShootGroupModel shootGroup : shootGroupList) {
				groupNameList.add(shootGroup.getGroupName());
			}
			resultMap.put("groupNameList", groupNameList);
			
			//通告单列表信息
			List<Map<String, Object>> noticeList = this.noticeService.queryNoticeInfoWithNoticeTime(crewId, userId, null, true);
			
			int maxSize = noticeList.size();
			if (maxSize > 6) {
				maxSize = 6;
			}
			
			List<Map<String, Object>> resultNoticeList = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < maxSize; i++) {
				Map<String, Object> notice = noticeList.get(i);
				
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
				
				resultNoticeList.add(notice);
			}
			
			resultMap.put("noticeList", resultNoticeList);
			
			//机位列表
			List<CameraInfoModel> cameraInfoList = this.clipService.queryCameraListByCrewId(crewId);
			List<String> cameraNameList = new ArrayList<String>();
			for (CameraInfoModel cameraInfo : cameraInfoList) {
				cameraNameList.add(cameraInfo.getCameraName());
			}
			resultMap.put("cameraNameList", cameraNameList);
			
			//主要演员列表
			List<Map<String, Object>> majorRoleList = this.viewRoleService.queryViewRoleListByCrewId(crewId, ViewRoleType.MajorActor.getValue());
			resultMap.put("majorRoleList", majorRoleList);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，获取下拉框信息失败", e);
			throw new IllegalArgumentException("未知异常，获取下拉框信息失败");
		}
		return resultMap;
	}
	

	/**
	 * 保存场记单信息
	 * @param rquestJson 请求参数
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveClipInfo")
	public Object saveClipInfo(String requestJson) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(requestJson)) {
				throw new IllegalArgumentException("无效的数据访问");
			}
			
			Type type = new TypeToken<MobileRequest<ClipRequestDto>>(){}.getType();
			MobileRequest<ClipRequestDto> request = MobileUtils.processRequest(requestJson, type);
			
			String crewId = request.getCrewId();
			String userId = request.getUserId();
			ClipRequestDto clipRequestDto = request.getBuzData();
			
			if (clipRequestDto == null) {
				throw new IllegalArgumentException("请勾选需要保存的场记单");
			}
			
			String noticeId = this.clipService.saveClipInfo(crewId, userId, clipRequestDto);
			Map<String, Object> noticeInfo = this.noticeService.queryOneFullInfoByNoticeId(crewId, noticeId);
			Date noticeDate = (Date) noticeInfo.get("noticeDate");
			String noticeDateStr = this.sdf2.format(noticeDate);
			String groupName = (String) noticeInfo.get("groupName");
			
			resultMap.put("noticeDate", noticeDateStr);
			resultMap.put("groupName", groupName);
			resultMap.put("noticeId", noticeId);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，保存场记单失败", e);
			throw new IllegalArgumentException("未知异常，保存场记单失败");
		}
		return resultMap;
	}
	
	/**
	 * 获取场记单信息
	 * @param crewId
	 * @param userId
	 * @param noticeId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainClipInfo")
	public Object obtainClipInfo(String crewId, String userId, String noticeId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(crewId)) {
				throw new IllegalArgumentException("无效的剧组访问");
			}
			MobileUtils.checkUserValid(userId);
			
			//场记单信息
			List<ClipInfoDto> clipInfoList = this.genClipInfoList(crewId, noticeId, null);
			resultMap.put("clipInfo", clipInfoList);
			
			//现场信息
			ShootLiveModel liveInfo = this.clipService.queryLiveInfoByNoticeId(crewId, noticeId);
			if (liveInfo != null) {
				//现场信息中的转场信息
				List<LiveConvertAddModel> liveConvertAddList = this.clipService.queryConvertByLiveId(crewId, noticeId);
				
				ShootLiveDto shootLiveDto = new ShootLiveDto(liveInfo, liveConvertAddList);
				resultMap.put("liveInfo", shootLiveDto);
			} else {
				Map<String, Object> noticeInfo = this.noticeService.queryNoticeFullInfoById(crewId, noticeId);
				if (noticeInfo == null) {
					throw new IllegalArgumentException("该通告单不属于当前剧组，请检查");
				}
				String shootLocation = (String) noticeInfo.get("shootLocation");
				String viewLocation = (String) noticeInfo.get("viewLocation");
				
				ShootLiveDto shootLiveDto = new ShootLiveDto();
				shootLiveDto.setShootLocation(shootLocation);
				shootLiveDto.setShootScene(viewLocation);
				shootLiveDto.setNoticeId(noticeId);
				resultMap.put("liveInfo", shootLiveDto);
			}
			
			
			//通告单演员出勤信息
			AttendanceDto roleAttendance = this.genRoleAttendance(crewId, noticeId);
			resultMap.put("attendanceInfo", roleAttendance);
			
			//特殊道具信息
			List<ClipPropModel> clipPropList = this.clipService.queryClipPropListByNoticeId(crewId, noticeId);
			if (clipPropList == null || clipPropList.size() == 0) {
				List<Map<String, Object>> noticePropList = this.noticeService.queryNoticePropList(crewId, noticeId, GoodsType.SpecialProps.getValue());
				for (Map<String, Object> noticeProp : noticePropList) {
					ClipPropModel clipPropModel = new ClipPropModel();
					clipPropModel.setNoticeId(noticeId);
					clipPropModel.setName((String) noticeProp.get("goodsName"));
					
					clipPropList.add(clipPropModel);
				}
			}
			resultMap.put("specialPropInfo", clipPropList);
			
			
			//重要备注信息
			List<ClipCommentModel> clipCommentList = this.clipService.queryClipCommentByNoticeId(crewId, noticeId);
			resultMap.put("importCommentInfo", clipCommentList);
			
			
			//天气信息
			NoticeTimeModel noticeTime = this.noticeService.queryNoticeTimeByNoticeId(noticeId);
			String weatherInfo = noticeTime.getWeatherInfo();
			resultMap.put("weatherInfo", weatherInfo);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，获取已有场记单失败", e);
			throw new IllegalArgumentException("未知异常，获取已有场记单失败");
		}
		
		return resultMap;
	}
	
	
	
	/**
	 * 辅助方法
	 * 查询现有的场记单信息
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	private List<ClipInfoDto> genClipInfoList(String crewId, String noticeId, String userId) {
		
		List<ClipInfoDto> clipInfoDtoList = new ArrayList<ClipInfoDto>();
		
		//查询出所有的镜次信息
		List<Map<String, Object>> noticeAuditionList = this.clipService.queryAudiInfoWithTmpCancel(crewId, noticeId, userId);
		
		//按照“集-场”号分组
		Map<String, List<Map<String, Object>>> groupViewAuditionMap = new LinkedHashMap<String, List<Map<String, Object>>>();
		
		//过滤出所有的机位名称
		List<String> cameraNameList = new LinkedList<String>();
		
		for (Map<String, Object> auditionInfo : noticeAuditionList) {
			Integer seriesNo = (Integer) auditionInfo.get("seriesNo");
			String viewNo = (String) auditionInfo.get("viewNo");
			
			String seriesViewNo = seriesNo + "-" + viewNo.toLowerCase();
			String cameraName = (String) auditionInfo.get("cameraName");
			
			if (groupViewAuditionMap.containsKey(seriesViewNo)) {
				groupViewAuditionMap.get(seriesViewNo).add(auditionInfo);
			} else {
				List<Map<String, Object>> mapValue = new ArrayList<Map<String, Object>>();
				mapValue.add(auditionInfo);
				groupViewAuditionMap.put(seriesViewNo, mapValue);
			}
			
			if (!cameraNameList.contains(cameraName)) {
				cameraNameList.add(cameraName);
			}
		}
		
		
		//查询指定的场景信息，会查询出对应的销场信息
		if (noticeAuditionList != null && noticeAuditionList.size() > 0) {
			
			//封装场记单场景信息
			for (String cameraName : cameraNameList) {
				ClipInfoDto clipInfoDto = new ClipInfoDto();
				clipInfoDto.setCameraName(cameraName);
				
				List<ClipViewInfoDto> clipViewDtoList = new ArrayList<ClipViewInfoDto>();
				
				Set<String> groupViewAuditionKeyset = groupViewAuditionMap.keySet();
				for (String seriesViewNo : groupViewAuditionKeyset) {
					List<Map<String, Object>> viewAuditionMapList = groupViewAuditionMap.get(seriesViewNo);
					
					//镜次信息
					if (viewAuditionMapList != null && viewAuditionMapList.size() > 0) {
						Map<String, Object> viewInfoMap = viewAuditionMapList.get(0);
						
						int seriesNo = (Integer) viewInfoMap.get("seriesNo");
						String viewNo = (String) viewInfoMap.get("viewNo");
						String atmosphereName = (String) viewInfoMap.get("atmosphereName");
						String site = (String) viewInfoMap.get("site");
						String shotDateStr = "";	//完成日期
						if (viewInfoMap.get("finishDate") != null) {
							Date shotDate = (Date) viewInfoMap.get("finishDate");
							shotDateStr = this.sdf2.format(shotDate);
						}
						
						Integer shootStatus = (Integer) viewInfoMap.get("shootStatus");
						String statusRemark = (String) viewInfoMap.get("remark");
						String tapNo = (String) viewInfoMap.get("tapNo");
						
						ClipViewInfoDto clipViewInfo = new ClipViewInfoDto();
						clipViewInfo.setSeriesNo(seriesNo);
						clipViewInfo.setViewNo(viewNo);
						clipViewInfo.setAtmosphereName(atmosphereName);
						clipViewInfo.setSite(site);
						
						//销场信息
						CancelViewDto cancelViewDto = new CancelViewDto();
						cancelViewDto.setShootStatus(shootStatus);
						cancelViewDto.setRemark(statusRemark);
						cancelViewDto.setTapNo(tapNo);
						cancelViewDto.setFinishDate(shotDateStr);
						clipViewInfo.setCancelViewInfo(cancelViewDto);
						
						//按照镜号、镜次排序
						Collections.sort(viewAuditionMapList, new Comparator<Map<String, Object>>() {
							@Override
							public int compare(Map<String, Object> o1, Map<String, Object> o2) {
								int o1AuditionNo = (Integer) o1.get("auditionNo");
								int o1LensNo = (Integer) o1.get("lensNo");
								int o2AuditionNo = (Integer) o2.get("auditionNo");
								int o2LensNo = (Integer) o2.get("lensNo");
								
								int result = o1LensNo - o2LensNo;
								if (result == 0) {
									result = o1AuditionNo - o2AuditionNo;
								}
								
				        		return result;
							}
						});
						
						
						List<ShootAuditionDto> auditionDtoList = new ArrayList<ShootAuditionDto>();
						for (Map<String, Object> auditionMap : viewAuditionMapList) {
							String auditionCameraName = (String) auditionMap.get("cameraName");
							if (cameraName.equals(auditionCameraName)) {
								ShootAuditionDto auditionDto = this.genAudtionDto(auditionMap);
								auditionDtoList.add(auditionDto);
							}
						}
						clipViewInfo.setAuditionList(auditionDtoList);
						
						clipViewDtoList.add(clipViewInfo);
					}
				}
				
				clipInfoDto.setViewInfoList(clipViewDtoList);
				
				clipInfoDtoList.add(clipInfoDto);
			}
		}
		
		
		//如果没有镜次信息，默认返回通告单下所有场景信息
		if (noticeAuditionList == null || noticeAuditionList.size() == 0) {
			List<Map<String, Object>> noticeViewList = this.viewInfoService.queryNoticeViewList(crewId, noticeId);
			ClipInfoDto clipInfoDto = new ClipInfoDto();
			clipInfoDto.setCameraName(null);
			
			List<ClipViewInfoDto> clipViewDtoList = new ArrayList<ClipViewInfoDto>();
			for (Map<String, Object> viewInfoMap : noticeViewList) {
				String viewId = (String) viewInfoMap.get("viewId");
				int seriesNo = (Integer) viewInfoMap.get("seriesNo");
				String viewNo = (String) viewInfoMap.get("viewNo");
				String atmosphereName = (String) viewInfoMap.get("atmosphereName");
				String site = (String) viewInfoMap.get("site");
				String shotDateStr = "";	//完成日期
				if (viewInfoMap.get("shotDate") != null) {
					Date shotDate = (Date) viewInfoMap.get("shotDate");
					shotDateStr = this.sdf2.format(shotDate);
				}

				Integer shootStatus = (Integer) viewInfoMap.get("shootStatus");
				String statusRemark = (String) viewInfoMap.get("statusRemark");
				String tapNo = (String) viewInfoMap.get("tapNo");
				
				
				ClipViewInfoDto clipViewInfo = new ClipViewInfoDto();
				clipViewInfo.setSeriesNo(seriesNo);
				clipViewInfo.setViewNo(viewNo);
				clipViewInfo.setAtmosphereName(atmosphereName);
				clipViewInfo.setSite(site);
				
				//销场信息
				/*if (shootStatus != Constants.SHOOTSTATUS_UNFINISHED) {
					
				}*/
				if (shootStatus != null) {
					CancelViewDto cancelViewDto = new CancelViewDto();
					cancelViewDto.setShootStatus(shootStatus);
					cancelViewDto.setRemark(statusRemark);
					cancelViewDto.setTapNo(tapNo);
					cancelViewDto.setFinishDate(shotDateStr);
					clipViewInfo.setCancelViewInfo(cancelViewDto);
				}
				//镜次信息
				clipViewInfo.setAuditionList(null);
				clipViewDtoList.add(clipViewInfo);
			}
			clipInfoDto.setViewInfoList(clipViewDtoList);
			
			clipInfoDtoList.add(clipInfoDto);
		}
		return clipInfoDtoList;
	}
	
	
	/**
	 * 生成镜次Dto
	 * @param auditionMap
	 * @return
	 */
	private ShootAuditionDto genAudtionDto(Map<String, Object> auditionMap) {
		ShootAuditionDto auditionDto = new ShootAuditionDto();
		
		auditionDto.setAuditionId((String) auditionMap.get("auditionId"));
		auditionDto.setAuditionNo((Integer) auditionMap.get("auditionNo"));
		if (auditionMap.get("comment") != null) {
			auditionDto.setComment((String) auditionMap.get("comment"));
		}
		if (auditionMap.get("content") != null) {
			auditionDto.setContent((String) auditionMap.get("content"));
		}
		if (auditionMap.get("createTime") != null) {
			Date createTime = (Date) auditionMap.get("createTime");
			String createTimeStr = this.sdf1.format(createTime);
			auditionDto.setCreateTime(createTimeStr);
		}
		auditionDto.setCrewId((String) auditionMap.get("crewId"));
		if (auditionMap.get("grade") != null) {
			auditionDto.setGrade((Integer) auditionMap.get("grade"));
		}
		
		auditionDto.setLensNo((Integer) auditionMap.get("lensNo"));
		if (auditionMap.get("mobileTime") != null) {
			Date mobileTime = (Date) auditionMap.get("mobileTime");
			auditionDto.setMobileTime(this.sdf1.format(mobileTime));
		}
		auditionDto.setNoticeId((String) auditionMap.get("noticeId"));
		if (auditionMap.get("sceneType") != null) {
			int sceneType = (Integer) auditionMap.get("sceneType");
			auditionDto.setSceneType(sceneType);
		}
		if (auditionMap.get("serverTime") != null) {
			Date serverTime = (Date) auditionMap.get("serverTime");
			auditionDto.setServerTime(this.sdf1.format(serverTime));
		}
		if (auditionMap.get("tcType") != null) {
			int tcType = (Integer) auditionMap.get("tcType");
			auditionDto.setTcType(tcType);
		}
		if (auditionMap.get("tcValue") != null) {
			auditionDto.setTcValue((String) auditionMap.get("tcValue"));
		}
		auditionDto.setSeriesNo((Integer) auditionMap.get("seriesNo"));
		auditionDto.setViewNo((String) auditionMap.get("viewNo"));
		
		return auditionDto;
	}

	/**
	 * 辅助方法
	 * 生成通告单演员出勤信息
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	private AttendanceDto genRoleAttendance(String crewId, String noticeId) {
		AttendanceDto attendanceDto = new AttendanceDto();
		
		List<RoleAttendanceDto> majorRoleAttList = new ArrayList<RoleAttendanceDto>();	//主要演员出勤信息
		List<RoleAttendanceDto> notMajRoleAttList = new ArrayList<RoleAttendanceDto>();	//特约、群众演员出勤信息
		
		//演员出勤信息
		List<RoleAttendanceModel> roleAttendanceList = this.clipService.queryRoleAttenceInfoByNoticeId(crewId, noticeId);
		
		for (RoleAttendanceModel roleAttendance : roleAttendanceList) {
			RoleAttendanceDto roleAttendanceDto = new RoleAttendanceDto(roleAttendance);;
			
			int roleType = roleAttendance.getViewRoleType();
			if (roleType == ViewRoleType.MajorActor.getValue()) {
				majorRoleAttList.add(roleAttendanceDto);
			} else {
				notMajRoleAttList.add(roleAttendanceDto);
			}
		}
		
		
		//如果没有保存过出勤信息，则返回通告单下所有演员的基本信息
		if (roleAttendanceList == null || roleAttendanceList.size() == 0) {
			List<Map<String, Object>> noticeRoleList = this.viewRoleService.queryManyByNoticeId(crewId, noticeId);
			for (Map<String, Object> noticeRoleMap : noticeRoleList) {
				RoleAttendanceDto roleAttendanceDto = genAttendanceDto(noticeRoleMap);
				int roleType = (Integer) noticeRoleMap.get("viewRoleType");
				if (roleType == ViewRoleType.MajorActor.getValue()) {
					majorRoleAttList.add(roleAttendanceDto);
				} else {
					notMajRoleAttList.add(roleAttendanceDto);
				}
			}
		}
		
		if (majorRoleAttList != null && majorRoleAttList.size() > 0) {
			Collections.sort(majorRoleAttList, new Comparator<RoleAttendanceDto>() {
				@Override
				public int compare(RoleAttendanceDto o1, RoleAttendanceDto o2) {
					CollationKey key1 = Collator.getInstance().getCollationKey(o1.getViewRoleName().toString().toLowerCase());// 要想不区分大小写进行比较用o1.toString().toLowerCase()
	        		CollationKey key2 = Collator.getInstance().getCollationKey(o2.getViewRoleName().toString().toLowerCase());
					return key1.compareTo(key2);
				}
			});
		}
		if (notMajRoleAttList != null && notMajRoleAttList.size() > 0) {
			Collections.sort(notMajRoleAttList, new Comparator<RoleAttendanceDto>() {
				@Override
				public int compare(RoleAttendanceDto o1, RoleAttendanceDto o2) {
					CollationKey key1 = Collator.getInstance().getCollationKey(o1.getViewRoleName().toString().toLowerCase());// 要想不区分大小写进行比较用o1.toString().toLowerCase()
	        		CollationKey key2 = Collator.getInstance().getCollationKey(o2.getViewRoleName().toString().toLowerCase());
					return key1.compareTo(key2);
				}
			});
		}
		
		attendanceDto.setMajorRoleAttenInfo(majorRoleAttList);
		attendanceDto.setNotMajRoleAttenInfo(notMajRoleAttList);
		
		return attendanceDto;
	}


	/**
	 * 生成演员出勤Dto
	 * @param roleInfoMap
	 * @return
	 */
	private RoleAttendanceDto genAttendanceDto(Map<String, Object> roleInfoMap) {
		RoleAttendanceDto roleAttendanceDto = new RoleAttendanceDto();
		if (roleInfoMap.get("attendanceId") != null) {
			roleAttendanceDto.setAttendanceId((String) roleInfoMap.get("attendanceId"));
		}
		if (roleInfoMap.get("crewId") != null) {
			roleAttendanceDto.setCrewId((String) roleInfoMap.get("crewId"));
		}
		if (roleInfoMap.get("noticeId") != null) {
			roleAttendanceDto.setNoticeId((String) roleInfoMap.get("noticeId"));
		}
		if (roleInfoMap.get("isLateArrive") != null) {
			int isLateArrive = (Integer) roleInfoMap.get("isLateArrive");
			if (isLateArrive == 1) {
				roleAttendanceDto.setIsLateArrive(true);
			} else {
				roleAttendanceDto.setIsLateArrive(false);
			}
		}
		if (roleInfoMap.get("isLatePackup") != null) {
			int isLatePackup = (Integer) roleInfoMap.get("isLatePackup");
			if (isLatePackup == 1) {
				roleAttendanceDto.setIsLatePackup(true);
			} else {
				roleAttendanceDto.setIsLatePackup(false);
			}
		}
		if (roleInfoMap.get("rarriveTime") != null) {
			Date rarriveTime = (Date) roleInfoMap.get("rarriveTime");
			roleAttendanceDto.setRarriveTime(this.sdf1.format(rarriveTime));
		}
		if (roleInfoMap.get("roleNum") != null) {
			roleAttendanceDto.setRoleNum((Integer) roleInfoMap.get("roleNum"));
		}
		roleAttendanceDto.setRoleType((Integer) roleInfoMap.get("viewRoleType"));
		if (roleInfoMap.get("rpackupTime") != null) {
			Date rpackupTime = (Date) roleInfoMap.get("rpackupTime");
			roleAttendanceDto.setRpackupTime(this.sdf1.format(rpackupTime));
		}
		roleAttendanceDto.setViewRoleName((String) roleInfoMap.get("viewRoleName"));
		if (roleInfoMap.get("actorName") != null) {
			roleAttendanceDto.setActorName((String) roleInfoMap.get("actorName"));
		}
		/*if (roleInfoMap.get("viewCount") != null) {
			roleAttendanceDto.setViewCount(((Long) roleInfoMap.get("viewCount")).intValue());
		}
		if (roleInfoMap.get("totalRoleNum") != null) {
			roleAttendanceDto.setTotalRoleNum(((BigDecimal) roleInfoMap.get("totalRoleNum")).intValue());
		}*/
		return roleAttendanceDto;
	}
	
	/**
	 * 校验需要提交的场记单是否有需要覆盖的数据
	 * @param crewId
	 * @param groupName
	 * @param noticeDate
	 * @param cameras
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/checkHasReplaceData")
	public Object checkHasReplaceData(String crewId, String groupName, String noticeDate, String cameras) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			//数据校验
			if (StringUtils.isBlank(groupName)) {
				throw new IllegalArgumentException("请填写所属通告单的组名");
			}
			if (StringUtils.isBlank(noticeDate)) {
				throw new IllegalArgumentException("请填写通告单日期");
			}
			if (StringUtils.isBlank(cameras)) {
				throw new IllegalArgumentException("请提供机位信息");
			}
			
			List<NoticeInfoModel> noticeInfoList = this.noticeService.queryNoticeByGroupAndDate(crewId, groupName, noticeDate);
			if (noticeInfoList == null || noticeInfoList.size() == 0) {
				throw new IllegalArgumentException("系统中不存在" + noticeDate + groupName + "通告单，请联系管理员");
			}
			if (noticeInfoList.size() > 1) {
				throw new IllegalArgumentException("系统中存在多张" + noticeDate + groupName + "通告单，无法保存场记单信息，请联系管理员");
			}
			
			NoticeInfoModel ownNotice = noticeInfoList.get(0);
			String noticeId = ownNotice.getNoticeId();
			
			//校验该通告单中是否存在相同机位的数据
			boolean hasTocoverData = false;
			
			List<String> myCameraNameList = Arrays.asList(cameras.split(","));
			List<Map<String, Object>> existAuditionList = this.clipService.queryNoticeAudition(crewId, noticeId, null);
			for (Map<String, Object> existAuditionMap : existAuditionList) {
				String cameraName = (String) existAuditionMap.get("cameraName");
				if (myCameraNameList.contains(cameraName)) {
					hasTocoverData = true;
					break;
				}
			}
			
			resultMap.put("hasTocoverData", hasTocoverData);
			resultMap.put("groupName", groupName);
			resultMap.put("noticeDate", noticeDate);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		}
		
		return resultMap;
	}
	
	/**
	 * 手机端获取场记单基本信息
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainPhoneClipBaseInfo")
	public Object obtainPhoneClipBaseInfo(String crewId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			//通告单列表信息
			List<Map<String, Object>> noticeList = this.noticeService.queryNoticeInfoWithNoticeTime(crewId, userId, null, true);
			
			int maxSize = noticeList.size();
			if (maxSize > 6) {
				maxSize = 6;
			}
			
			List<Map<String, Object>> resultNoticeList = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < maxSize; i++) {
				Map<String, Object> notice = noticeList.get(i);
				
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
				
				resultNoticeList.add(notice);
			}
			
			resultMap.put("noticeList", resultNoticeList);
			
			
			//主要演员列表
			List<Map<String, Object>> majorRoleList = this.viewRoleService.queryViewRoleListByCrewId(crewId, ViewRoleType.MajorActor.getValue());
			resultMap.put("majorRoleList", majorRoleList);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，获取信息失败", e);
			throw new IllegalArgumentException("未知异常，获取信息失败");
		}
		return resultMap;
	}
	
	/**
	 * 现场进度获取部门评分接口
	 * @param crewId
	 * @param userId
	 * @param noticeId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainToEvaluateDepart")
	public Object obtainToEvaluateDepart(String crewId, String userId, String noticeId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			List<Map<String, Object>> departmentList = this.clipService.queryNoticeDepartScore(crewId, noticeId, null);
			resultMap.put("departmentList", departmentList);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，获取部门信息失败。", e);
			throw new IllegalArgumentException("未知异常，获取部门信息失败");
		}
		
		return resultMap;
	}
	
	/**
	 * 现场进度保存部门评分接口
	 * 含新增和修改
	 * @param crewId
	 * @param userId
	 * @param noticeId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveDepartmentScore")
	public Object saveDepartmentScore(HttpServletRequest sRequest, String requestJson) {
		//String crewId, String userId, String noticeId, @RequestParam List<DepartmentEvaluateDto> departScoreList
		System.out.println(requestJson);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String userId = "";
		try {
			if (StringUtils.isBlank(requestJson)) {
				throw new IllegalArgumentException("无效的数据访问");
			}
			
			Type type = new TypeToken<MobileRequest<DepartScoreRequestDto>>(){}.getType();
			MobileRequest<DepartScoreRequestDto> request = MobileUtils.processRequest(requestJson, type);
			
			String crewId = request.getCrewId();
			userId = request.getUserId();
			DepartScoreRequestDto departScoreRequestDto = request.getBuzData();
			
			String noticeId = departScoreRequestDto.getNoticeId();
			List<DepartmentEvaluateDto> departScoreList = departScoreRequestDto.getDepartScoreList();
			
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			//已有的评价信息
			List<DepartmentEvaluateModel> existEvaluateList = this.clipService.queryDepartEvaluateByUserNotice(crewId, noticeId, null);
			
			//已有评价数据分组
			Map<String, DepartmentEvaluateModel> groupDepartEval = new HashMap<String, DepartmentEvaluateModel>();
			for (DepartmentEvaluateModel existEval : existEvaluateList) {
				groupDepartEval.put(existEval.getDepartmentId(), existEval);
			}
			
			List<DepartmentEvaluateModel> toAddDepartEvaluateList = new ArrayList<DepartmentEvaluateModel>();
			List<DepartmentEvaluateModel> toUpdateDepartEvaluateList = new ArrayList<DepartmentEvaluateModel>();
			for (DepartmentEvaluateDto evaluateDto : departScoreList) {
				String departmentId = evaluateDto.getDepartmentId();
				
				if (groupDepartEval.get(departmentId) == null) {
					DepartmentEvaluateModel departmentEvaluateModel = new DepartmentEvaluateModel();
					departmentEvaluateModel.setId(UUIDUtils.getId());
					departmentEvaluateModel.setDepartmentId(evaluateDto.getDepartmentId());
					departmentEvaluateModel.setUserId(userId);
					departmentEvaluateModel.setScore(evaluateDto.getScore());
					departmentEvaluateModel.setCrewId(crewId);
					departmentEvaluateModel.setNoticeId(noticeId);
					departmentEvaluateModel.setCreateTime(new Date());
					departmentEvaluateModel.setLastUpdateTime(new Date());
					
					toAddDepartEvaluateList.add(departmentEvaluateModel);
				} else {
					DepartmentEvaluateModel departmentEvaluate = new DepartmentEvaluateModel();
					departmentEvaluate.setId(groupDepartEval.get(departmentId).getId());
					departmentEvaluate.setScore(evaluateDto.getScore());
					departmentEvaluate.setLastUpdateTime(new Date());
					
					toUpdateDepartEvaluateList.add(departmentEvaluate);
				}
			}
			
			this.clipService.addManyDepartEvaluate(toAddDepartEvaluateList);
			this.clipService.updateManyDepartEvaluate(toUpdateDepartEvaluateList);

			sRequest.setAttribute("crewId", crewId);
			sRequest.setAttribute("userId", userId);
			this.sysLogService.saveSysLogForApp(sRequest, "保存部门评分", this.getClientType(userId), DepartmentEvaluateModel.TABLE_NAME, noticeId, 1);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，保存部门评分信息失败。", e);
			this.sysLogService.saveSysLogForApp(sRequest, "保存部门评分失败：" + e.getMessage(), this.getClientType(userId), DepartmentEvaluateModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，保存部门评分信息失败");
		}
		
		return resultMap;
	}
	
	/**
	 * 保存现场信息基本信息
	 * @param crewId
	 * @param userId
	 * @param noticeId	通告单ID
	 * @param tapNo	带号
	 * @param shootLocation	拍摄地点
	 * @param shootScene	拍摄场景
	 * @param startTime	出发时间
	 * @param arriveTime	到场时间
	 * @param bootTime	开机时间
	 * @param packupTime	收工时间
	 * @param mobileTime	移动端保存时间
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveShootLiveInfo")
	public Object saveShootLiveInfo(HttpServletRequest request, String crewId,
			String userId, String noticeId, String tapNo, String shootLocation,
			String shootScene, String startTime, String arriveTime,
			String bootTime, String packupTime, String mobileTime) {
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			boolean isAdd = false;	//是否是新增操作
			
			ShootLiveModel liveModel = this.clipService.queryLiveInfoByNoticeId(crewId, noticeId);
			if (liveModel == null) {
				liveModel = new ShootLiveModel();
				liveModel.setLiveId(UUIDUtils.getId());
				liveModel.setCreateTime(new Date());
				
				isAdd = true;
			}
			
			liveModel.setCrewId(crewId);
			liveModel.setNoticeId(noticeId);
			liveModel.setTapNo(tapNo);
			liveModel.setShootLocation(shootLocation);
			liveModel.setShootScene(shootScene);
			liveModel.setUserId(userId);
			
			if (!StringUtils.isBlank(startTime)) {
				Date startTimeDate = this.sdf1.parse(startTime);
				liveModel.setStartTime(startTimeDate);
			}
			if (!StringUtils.isBlank(arriveTime)) {
				Date arriveTimeDate = this.sdf1.parse(arriveTime);
				liveModel.setArriveTime(arriveTimeDate);
			}
			if (!StringUtils.isBlank(bootTime)) {
				Date bootTimeDate = this.sdf1.parse(bootTime);
				liveModel.setBootTime(bootTimeDate);
			}
			if (!StringUtils.isBlank(packupTime)) {
				Date packupTimeDate = this.sdf1.parse(packupTime);
				liveModel.setPackupTime(packupTimeDate);
			}
			if (!StringUtils.isBlank(mobileTime)) {
				Date mobileTimeDate = this.sdf1.parse(mobileTime);
				liveModel.setMobileTime(mobileTimeDate);
			}
			liveModel.setServerTime(new Date());
			
			if (isAdd) {
				this.clipService.addOneShootLive(liveModel);
				
				this.sysLogService.saveSysLogForApp(request, "新增现场信息基本信息", userInfo.getClientType(), ShootLiveModel.TABLE_NAME, noticeId, 1);
			} else {
				this.clipService.updateOneShootLive(liveModel);
				
				this.sysLogService.saveSysLogForApp(request, "修改现场信息基本信息", userInfo.getClientType(), ShootLiveModel.TABLE_NAME, noticeId, 2);
			}
			
		} catch (IllegalArgumentException ie) {
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (ParseException pe) {
			throw new IllegalArgumentException("时间格式错误", pe);
		} catch (Exception e) {
			this.sysLogService.saveSysLogForApp(request, "保存现场信息基本信息失败：" + e.getMessage(), userInfo.getClientType(), ShootLiveModel.TABLE_NAME, noticeId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，保存现场信息失败", e);
		}
		
		return null;
	}
	
	/**
	 * 保存现场信息中的转场信息
	 * 含新增/修改
	 * @param crewId
	 * @param noticeId
	 * @param userId
	 * @param liveConvertAddDto
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveLiveConvertInfo")
	public Object saveLiveConvertInfo(HttpServletRequest request,
			String crewId, String userId, String noticeId, String convertId,
			String convertTime, String carriveTime, String cbootTime,
			String cpackupTime, String cshootLocation, String cshootScene) {
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			LiveConvertAddModel convertModel = new LiveConvertAddModel();
			if (StringUtils.isBlank(convertId)) {
				convertModel.setConvertId(UUIDUtils.getId());
			} else {
				convertModel.setConvertId(convertId);
			}
			convertModel.setCrewId(crewId);
			convertModel.setNoticeId(noticeId);
			convertModel.setUserId(userId);
			if (!StringUtils.isBlank(convertTime)) {
				convertModel.setConvertTime(this.sdf1.parse(convertTime));
			}
			if (!StringUtils.isBlank(carriveTime)) {
				convertModel.setCarriveTime(this.sdf1.parse(carriveTime));
			}
			if (!StringUtils.isBlank(cbootTime)) {
				convertModel.setCbootTime(this.sdf1.parse(cbootTime));
			}
			if (!StringUtils.isBlank(cpackupTime)) {
				convertModel.setCpackupTime(this.sdf1.parse(cpackupTime));
			}
			convertModel.setCshootLocation(cshootLocation);
			convertModel.setCshootScene(cshootScene);
			
			if (StringUtils.isBlank(convertId)) {
				this.clipService.addOneLiveConvertAdd(convertModel);
				
				this.sysLogService.saveSysLogForApp(request, "新增现场信息转场信息", userInfo.getClientType(), LiveConvertAddModel.TABLE_NAME, noticeId, 1);
			} else {
				this.clipService.updateOneConvertAdd(convertModel);
				
				this.sysLogService.saveSysLogForApp(request, "修改现场信息转场信息", userInfo.getClientType(), LiveConvertAddModel.TABLE_NAME, convertId, 2);
			}
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，保存转场信息失败。", e);
			this.sysLogService.saveSysLogForApp(request, "保存现场信息转场信息失败：" + e.getMessage(), userInfo.getClientType(), LiveConvertAddModel.TABLE_NAME, convertId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，保存转场信息失败");
		}
		return null;
	}
	
	/**
	 * 获取现场信息接口
	 * @param crewId
	 * @param userId
	 * @param noticeId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainNoticeLiveInfo")
	public Object obtainNoticeLiveInfo (String crewId, String userId, String noticeId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			//现场信息
			ShootLiveModel liveInfo = this.clipService.queryLiveInfoByNoticeId(crewId, noticeId);
			
			if (liveInfo == null) {
				liveInfo = new ShootLiveModel();
				
				Map<String, Object> noticeInfo = this.noticeService.queryNoticeFullInfoById(crewId, noticeId);
				if (noticeInfo == null) {
					throw new IllegalArgumentException("该通告单不属于当前剧组，请检查");
				}
				String shootLocation = (String) noticeInfo.get("shootLocation");
				String viewLocation = (String) noticeInfo.get("viewLocation");
				
				liveInfo.setShootLocation(shootLocation);
				liveInfo.setShootScene(viewLocation);
				liveInfo.setNoticeId(noticeId);
			}
			List<LiveConvertAddModel> liveConvertAddList = this.clipService.queryConvertByLiveId(crewId, noticeId);
			ShootLiveDto shootLiveDto = new ShootLiveDto(liveInfo, liveConvertAddList);
			
			resultMap.put("liveInfo", shootLiveDto);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，获取现场信息失败。", e);
			throw new IllegalArgumentException("未知异常，获取现场信息失败");
		}
		
		return resultMap;
	}
	
	/**
	 * 删除现场信息转场信息
	 * @param convertIds
	 */
	@ResponseBody
	@RequestMapping("/deleteConvertInfo")
	public Object deleteConvertInfo(HttpServletRequest request, String crewId,
			String userId, String noticeId, String convertIds) {
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			if (StringUtils.isBlank(convertIds)) {
				throw new IllegalArgumentException("请选择需要删除的转场信息");
			}
			
			this.clipService.deleteConvertAddByIds(convertIds);
			
			this.sysLogService.saveSysLogForApp(request, "删除现场信息转场信息(" 
					+ convertIds.split(",").length + ")", userInfo.getClientType(), 
					LiveConvertAddModel.TABLE_NAME, convertIds, 3);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，删除转场信息失败。", e);
			
			this.sysLogService.saveSysLogForApp(request, "删除现场信息转场信息(" 
					+ convertIds.split(",").length + ")失败：" + e.getMessage(), userInfo.getClientType(), 
					LiveConvertAddModel.TABLE_NAME, convertIds, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，删除转场信息失败");
		}
		
		return null;
	}
	
	/**
	 * 获取通告单演员出勤信息
	 * 该接口当没有演员出勤信息时，不给出通告单下的演员信息
	 * @param crewId
	 * @param noticeId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainRoleAttendanceInfo")
	public Object obtainRoleAttendanceInfo(String crewId, String noticeId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			//演员出勤信息
			List<RoleAttendanceModel> roleAttendanceList = this.clipService.queryRoleAttenceInfoByNoticeId(crewId, noticeId);
			List<RoleAttendanceDto> roleAttendanceDtoList = new ArrayList<RoleAttendanceDto>();
			
			//自动保存通告单中演员信息
			if (roleAttendanceList == null || roleAttendanceList.size() == 0) {
				List<Map<String, Object>> noticeRoleList = this.viewRoleService.queryManyByNoticeId(crewId, noticeId);
				List<RoleAttendanceDto> myRoleAttendanceDtoList = new ArrayList<RoleAttendanceDto>();
				
				for (Map<String, Object> noticeRoleMap : noticeRoleList) {
					RoleAttendanceDto roleAttendanceDto = genAttendanceDto(noticeRoleMap);
					myRoleAttendanceDtoList.add(roleAttendanceDto);
				}
				roleAttendanceList = this.clipService.saveRoleAttendance(crewId, noticeId, userId, myRoleAttendanceDtoList);
			}
			
			for (RoleAttendanceModel roleAttendance : roleAttendanceList) {
				RoleAttendanceDto roleAttendanceDto = new RoleAttendanceDto(roleAttendance);
				roleAttendanceDtoList.add(roleAttendanceDto);
			}
			
			//按照名称进行排序
			if (roleAttendanceDtoList != null && roleAttendanceDtoList.size() > 0) {
				Collections.sort(roleAttendanceDtoList, new Comparator<RoleAttendanceDto>() {
					@Override
					public int compare(RoleAttendanceDto o1, RoleAttendanceDto o2) {
						CollationKey key1 = Collator.getInstance().getCollationKey(o1.getViewRoleName().toString().toLowerCase());// 要想不区分大小写进行比较用o1.toString().toLowerCase()
		        		CollationKey key2 = Collator.getInstance().getCollationKey(o2.getViewRoleName().toString().toLowerCase());
						return key1.compareTo(key2);
					}
				});
			}
			
			resultMap.put("attendanceList", roleAttendanceDtoList);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，获取演员出勤信息失败。", e);
			throw new IllegalArgumentException("未知异常，获取演员出勤信息失败");
		}
		return resultMap;
	}
	
	/**
	 * 保存演员出勤信息
	 * @param crewId
	 * @param noticeId
	 * @param userId
	 * @param attendanceId	演员出勤ID
	 * @param roleType	演员类型，1：主要演员  2：特约演员  3：群众演员
	 * @param actorName	演员名称
	 * @param viewRoleName	角色名称
	 * @param roleNum	人数
	 * @param rarriveTime	到场时间
	 * @param isLateArrive	是否迟到
	 * @param rpackupTime	收工时间
	 * @param isLatePackup	是否迟放
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveRoleAttendanceInfo")
	public Object saveRoleAttendanceInfo(HttpServletRequest request,
			String crewId, String noticeId, String userId, String attendanceId,
			Integer roleType, String actorName, String viewRoleName,
			Integer roleNum, String rarriveTime, Boolean isLateArrive,
			String rpackupTime, Boolean isLatePackup) {
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			if (StringUtils.isBlank(viewRoleName)) {
				throw new IllegalArgumentException("有角色名称未填写，请检查");
			}
			
			RoleAttendanceModel attendanceModel = new RoleAttendanceModel();
			
			if (StringUtils.isBlank(attendanceId)) {
				attendanceModel.setAttendanceId(UUIDUtils.getId());
			} else {
				attendanceModel.setAttendanceId(attendanceId);
			}
			attendanceModel.setCrewId(crewId);
			attendanceModel.setNoticeId(noticeId);
			attendanceModel.setUserId(userId);
			attendanceModel.setViewRoleType(roleType);
			attendanceModel.setViewRoleName(viewRoleName);
			if (roleNum != null) {
				attendanceModel.setRoleNum(roleNum);
			}
			attendanceModel.setActorName(actorName);
			if (!StringUtils.isBlank(rarriveTime)) {
				attendanceModel.setRarriveTime(this.sdf1.parse(rarriveTime));
			}
			attendanceModel.setIsLateArrive(isLateArrive);
			if (!StringUtils.isBlank(rpackupTime)) {
				attendanceModel.setRpackupTime(this.sdf1.parse(rpackupTime));
			}
			attendanceModel.setIsLatePackup(isLatePackup);
			
			if (StringUtils.isBlank(attendanceId)) {
				this.clipService.addOneAttendance(attendanceModel);
				
				this.sysLogService.saveSysLogForApp(request, "新增演员出勤信息", userInfo.getClientType(), RoleAttendanceModel.TABLE_NAME, noticeId, 1);
			} else {
				this.clipService.updateOneAttendance(attendanceModel);
				
				this.sysLogService.saveSysLogForApp(request, "修改演员出勤信息", userInfo.getClientType(), RoleAttendanceModel.TABLE_NAME, attendanceId, 2);
			}
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，保存演员出勤信息失败。", e);
			this.sysLogService.saveSysLogForApp(request, "保存演员出勤信息失败：" + e.getMessage(), 
					userInfo.getClientType(), RoleAttendanceModel.TABLE_NAME, attendanceId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，保存演员出勤信息失败");
		}
		
		return null;
	}
	
	@ResponseBody
	@RequestMapping("/deleteRoleAttendanceInfo")
	public Object deleteRoleAttendanceInfo(HttpServletRequest request, String crewId, String userId, String noticeId, String attendanceIds) {
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			if (StringUtils.isBlank(attendanceIds)) {
				throw new IllegalArgumentException("请选择需要删除的演员出勤信息");
			}
			
			this.clipService.deleteRoleAttendanceByIds(crewId, noticeId, attendanceIds);

			this.sysLogService.saveSysLogForApp(request, "删除演员出勤信息(" + attendanceIds.split(",").length + ")", 
					userInfo.getClientType(), RoleAttendanceModel.TABLE_NAME, attendanceIds, 3);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，删除演员出勤信息失败。", e);
			this.sysLogService.saveSysLogForApp(request, "删除演员出勤信息(" + attendanceIds.split(",").length + ")失败：" + e.getMessage(), 
					userInfo.getClientType(), RoleAttendanceModel.TABLE_NAME, attendanceIds, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，删除演员出勤信息失败");
		}
		
		return null;
	}
	
	/**
	 * 获取特殊道具信息
	 * @param crewId
	 * @param userId
	 * @param noticeId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainClipPropInfo")
	public Object obtainClipPropInfo(String crewId, String userId, String noticeId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String serverBasepath = properties.getProperty("server.basepath");
			
			//特殊道具信息
			List<ClipPropModel> clipPropList = this.clipService.queryClipPropListByNoticeId(crewId, noticeId);
			
			List<ClipPropInfoDto> clipPropInfoDtoList = new ArrayList<ClipPropInfoDto>();
			
			if (clipPropList == null || clipPropList.size() == 0) {
				List<Map<String, Object>> noticePropList = this.noticeService.queryNoticePropList(crewId, noticeId, GoodsType.SpecialProps.getValue());
				
				List<ClipPropModel> toSavePropList = new ArrayList<ClipPropModel>();
				for (Map<String, Object> noticeProp : noticePropList) {
					//自动保存通告单中的道具信息
					ClipPropModel toSavePropModel = new ClipPropModel();
					toSavePropModel.setName((String) noticeProp.get("goodsName"));
					toSavePropList.add(toSavePropModel);
					
					clipPropList = this.clipService.saveClipPropInfo(crewId, noticeId, userId, toSavePropList);
				}
			}
			if (clipPropList != null && clipPropList.size() > 0) {
				List<String> attpackIdList = new ArrayList<String>();
				
				for(ClipPropModel temp:clipPropList){
					attpackIdList.add(temp.getAttpackId());
				}
				List<AttachmentModel> attachmentModelList = attachmentService.queryAttachByPackIdList(attpackIdList);	
				
				//根据附件包ID分组
				Map<String, List<AttachmentDto>> groupAttachmentMap = new HashMap<String, List<AttachmentDto>>();
				for(AttachmentModel temp:attachmentModelList){
					String  attpackId = temp.getAttpackId();
					AttachmentDto attachmentDto = new AttachmentDto();
					attachmentDto.setAttachmentId(temp.getId());
					attachmentDto.setAttpackId(attpackId);
					attachmentDto.setName(temp.getName());
					attachmentDto.setSuffix(temp.getSuffix());
					attachmentDto.setSize(temp.getSize());
					attachmentDto.setLength(temp.getLength());
					
					if (!StringUtils.isBlank(temp.getHdStorePath())) {
						String hdPreviewUrl = serverBasepath + "fileManager/previewAttachment?address=" + temp.getHdStorePath();
						attachmentDto.setHdPreviewUrl(hdPreviewUrl);
					}
					if (!StringUtils.isBlank(temp.getSdStorePath())) {
						String sdPreviewUrl = serverBasepath + "fileManager/previewAttachment?address=" + temp.getSdStorePath();
						attachmentDto.setSdPreviewUrl(sdPreviewUrl);
					}
					
					attachmentDto.setType(temp.getType());
					if(groupAttachmentMap.containsKey(attpackId)){
						groupAttachmentMap.get(attpackId).add(attachmentDto);
					}else{
						List<AttachmentDto> tempAttachmentDtoList = new ArrayList<AttachmentDto>();
						tempAttachmentDtoList.add(attachmentDto);
						groupAttachmentMap.put(attpackId, tempAttachmentDtoList);
					}
				}
				
				for(ClipPropModel temp:clipPropList){
					ClipPropInfoDto clipPropInfoDto = new ClipPropInfoDto();
					clipPropInfoDto.setAttpackId(temp.getAttpackId());
					clipPropInfoDto.setComment(temp.getComment());
					clipPropInfoDto.setName(temp.getName());
					clipPropInfoDto.setNoticeId(temp.getNoticeId());
					clipPropInfoDto.setNum(temp.getNum());
					clipPropInfoDto.setPropId(temp.getPropId());
					
					String attpackId = temp.getAttpackId();
					if (!StringUtils.isBlank(attpackId)) {
						List<AttachmentDto> list = groupAttachmentMap.get(attpackId);
						clipPropInfoDto.setAttachInfoList(list);
					}
					clipPropInfoDtoList.add(clipPropInfoDto);
				}
			}
			
			resultMap.put("propList", clipPropInfoDtoList);

		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，获取道具信息失败。", e);
			throw new IllegalArgumentException("未知异常，获取道具信息失败");
		}
		
		return resultMap;
	}
	
	/**
	 * 保存特殊道具信息
	 * 含新增、修改
	 * @param crewId
	 * @param userId
	 * @param noticeId
	 * @param propId	道具ID
	 * @param name	名称
	 * @param num	数量
	 * @param attpackId	附件包ID
	 * @param comment	备注
	 */
	@ResponseBody
	@RequestMapping("/saveClipPropInfo")
	public Object saveClipPropInfo(HttpServletRequest request, String crewId,
			String userId, String noticeId, String propId, String name,
			Integer num, String attpackId, String comment) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkUserValid(userId);
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			if (StringUtils.isBlank(name)) {
				throw new IllegalArgumentException("请填写道具名称");
			}
			
			ClipPropModel savedPropInfo = this.clipService.saveClipPropInfo(crewId, userId, noticeId, propId, name, num, attpackId, comment);
			
			resultMap.put("attpackId", savedPropInfo.getAttpackId());
			resultMap.put("propId", savedPropInfo.getPropId());

			if (StringUtils.isBlank(propId)) {
				this.sysLogService.saveSysLogForApp(request, "新增特殊道具信息", userInfo.getClientType(), ClipPropModel.TABLE_NAME, noticeId, 1);
			} else {
				this.sysLogService.saveSysLogForApp(request, "修改特殊道具信息", userInfo.getClientType(), ClipPropModel.TABLE_NAME, propId, 2);
			}
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，保存道具信息失败。", e);
			this.sysLogService.saveSysLogForApp(request, "保存特殊道具信息失败：" + e.getMessage(), userInfo.getClientType(), ClipPropModel.TABLE_NAME, propId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，保存道具信息失败");
		}
		
		return resultMap;
	}
	
	/**
	 * 删除场记单特殊道具信息
	 * @param crewId
	 * @param userId
	 * @param noticeId
	 * @param propIds
	 */
	@ResponseBody
	@RequestMapping("/deleteClipPropInfo")
	public Object deleteClipPropInfo(HttpServletRequest request, String crewId,
			String userId, String noticeId, String propIds) {
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			if (StringUtils.isBlank(propIds)) {
				throw new IllegalArgumentException("请选择需要删除的道具");
			}
			
			this.clipService.deleteClipPropByIds(crewId, noticeId, propIds);

			this.sysLogService.saveSysLogForApp(request, "删除特殊道具信息(" + propIds.split(",").length + ")", 
					userInfo.getClientType(), ClipPropModel.TABLE_NAME, propIds, 3);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，删除道具信息失败。", e);
			this.sysLogService.saveSysLogForApp(request, "删除特殊道具信息(" + propIds.split(",").length + ")失败：" + e.getMessage(), 
					userInfo.getClientType(), ClipPropModel.TABLE_NAME, propIds, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，删除道具信息失败");
		}
		
		return null;
	}
	
	/**
	 * 获取重要备注列表
	 * @param crewId
	 * @param noticeId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainImportCommentList")
	public Object obtainImportCommentList(String crewId, String noticeId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String serverBasepath = properties.getProperty("server.basepath");
			
			//重要备注信息
			List<ClipCommentModel> clipCommentList = this.clipService.queryClipCommentByNoticeId(crewId, noticeId);
			
			List<String> attpackIdList = new ArrayList<String>();
			for (ClipCommentModel clipCommentInfo : clipCommentList) {
				attpackIdList.add(clipCommentInfo.getAttpackId());
			}
			
			List<AttachmentModel> attachmentList = this.attachmentService.queryAttachByPackIdList(attpackIdList);
			
			Map<String, List<AttachmentDto>> groupAttachMap = new HashMap<String, List<AttachmentDto>>();
			for (AttachmentModel attachmentInfo : attachmentList) {
				AttachmentDto attachmentDto = new AttachmentDto();
				attachmentDto.setAttachmentId(attachmentInfo.getId());
				attachmentDto.setAttpackId(attachmentInfo.getAttpackId());
				attachmentDto.setName(attachmentInfo.getName());
				attachmentDto.setType(attachmentInfo.getType());
				attachmentDto.setSuffix(attachmentInfo.getSuffix());
				attachmentDto.setSize(attachmentInfo.getSize());
				attachmentDto.setLength(attachmentInfo.getLength());
				
				if (!StringUtils.isBlank(attachmentInfo.getHdStorePath())) {
					String hdPreviewUrl = serverBasepath + "/fileManager/previewAttachment?address=" + attachmentInfo.getHdStorePath();
					attachmentDto.setHdPreviewUrl(hdPreviewUrl);
				}
				if (!StringUtils.isBlank(attachmentInfo.getSdStorePath())) {
					String sdPreviewUrl = serverBasepath + "/fileManager/previewAttachment?address=" + attachmentInfo.getSdStorePath();
					attachmentDto.setSdPreviewUrl(sdPreviewUrl);
				}
				
				if (!groupAttachMap.containsKey(attachmentInfo.getAttpackId())) {
					List<AttachmentDto> attachmentDtoList = new ArrayList<AttachmentDto>();
					attachmentDtoList.add(attachmentDto);
					groupAttachMap.put(attachmentInfo.getAttpackId(), attachmentDtoList);
					
				} else {
					groupAttachMap.get(attachmentInfo.getAttpackId()).add(attachmentDto);
				}
			}
			
			List<ClipCommentDto> clipCommentDtoList = new ArrayList<ClipCommentDto>();
			for (ClipCommentModel clipCommentInfo : clipCommentList) {
				ClipCommentDto clipCommentDto = new ClipCommentDto();
				clipCommentDto.setCommentId(clipCommentInfo.getCommentId());
				clipCommentDto.setCrewId(clipCommentInfo.getCrewId());
				clipCommentDto.setNoticeId(clipCommentInfo.getNoticeId());
				clipCommentDto.setContent(clipCommentInfo.getContent());
				clipCommentDto.setAttpackId(clipCommentInfo.getAttpackId());
				if (!StringUtils.isBlank(clipCommentInfo.getAttpackId())) {
					clipCommentDto.setAttachInfoList(groupAttachMap.get(clipCommentInfo.getAttpackId()));
				}
				clipCommentDtoList.add(clipCommentDto);
			}
			
			resultMap.put("commentInfoList", clipCommentDtoList);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，获取重要备注信息失败。", e);
			throw new IllegalArgumentException("未知异常，获取重要备注信息失败");
		}
		
		return resultMap;
	}
	
	/**
	 * 保存重要备注信息
	 * @param crewId
	 * @param noticeId
	 * @param userId
	 * @param commentId	重要备注ID
	 * @param content	内容
	 * @param attpackId	附件包ID
	 * @param mobileTime	移动端时间
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveImportCommentInfo")
	public Object saveImportCommentInfo(HttpServletRequest request,
			String crewId, String noticeId, String userId, String commentId,
			String content, String attpackId, String mobileTime) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkUserValid(userId);
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			ClipCommentModel comment = this.clipService.saveCommentInfo(crewId, noticeId, userId, commentId, content, attpackId, mobileTime);
			resultMap.put("attpackId", comment.getAttpackId());
			resultMap.put("commentId", comment.getCommentId());

			if (StringUtils.isBlank(commentId)) {
				this.sysLogService.saveSysLogForApp(request, "新增重要备注信息", userInfo.getClientType(), ClipCommentModel.TABLE_NAME, noticeId, 1);
			} else {
				this.sysLogService.saveSysLogForApp(request, "修改重要备注信息", userInfo.getClientType(), ClipCommentModel.TABLE_NAME, commentId, 2);
			}
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，保存重要备注信息失败。", e);
			this.sysLogService.saveSysLogForApp(request, "保存重要备注信息失败：" + e.getMessage(), userInfo.getClientType(), ClipCommentModel.TABLE_NAME, commentId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，保存重要备注信息失败");
		}
		
		return resultMap;
	}
	
	/**
	 * 删除重要备注信息
	 * @param crewId
	 * @param noticeId
	 * @param userId
	 * @param commentIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteImportCommentInfo")
	public Object deleteImportCommentInfo(HttpServletRequest request, String crewId, String noticeId, String userId, String commentIds) {
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			this.clipService.deleteCommentByIds(crewId, noticeId, userId, commentIds);

			this.sysLogService.saveSysLogForApp(request, "删除重要备注信息(" + commentIds.split(",").length + ")", 
					userInfo.getClientType(), ClipCommentModel.TABLE_NAME, commentIds, 3);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，删除重要备注信息失败。", e);

			this.sysLogService.saveSysLogForApp(request, "删除重要备注信息(" + commentIds.split(",").length + ")失败：" + e.getMessage(), 
					userInfo.getClientType(), ClipCommentModel.TABLE_NAME, commentIds, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，删除重要备注信息失败");
		}
		
		return null;
	}
}
