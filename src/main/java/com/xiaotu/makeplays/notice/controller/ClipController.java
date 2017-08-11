package com.xiaotu.makeplays.notice.controller;

import java.text.CollationKey;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.attachment.dto.AttachmentDto;
import com.xiaotu.makeplays.attachment.model.AttachmentModel;
import com.xiaotu.makeplays.attachment.service.AttachmentService;
import com.xiaotu.makeplays.goods.model.constants.GoodsType;
import com.xiaotu.makeplays.notice.controller.dto.clip.ClipCommentDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.ClipPropInfoDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.LiveConvertAddDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.RoleAttendanceDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.ShootLiveDto;
import com.xiaotu.makeplays.notice.model.NoticeInfoModel;
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
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * @类名：ClipController.java
 * @作者：李晓平
 * @时间：2017年3月27日 下午12:03:18
 * @描述：现场日志
 */
@Controller
@RequestMapping("/clipManager")
public class ClipController extends BaseController{
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

	private SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");

	Logger logger = LoggerFactory.getLogger(ClipController.class);
	
	@Autowired
	private ClipService clipService;
	
	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private ViewRoleService viewRoleService;
	
	@Autowired
	private AttachmentService attachmentService;
	
	private final int terminal = Constants.TERMINAL_PC;
	
	/**
	 * 跳转到现场日志列表页面
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/toShootLogListPage")
	public ModelAndView toShootLogListPage(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();		
		mv.setViewName("/shoot/shootLogList");
		
		this.sysLogService.saveSysLog(request, "查询现场日志", Constants.TERMINAL_PC, NoticeInfoModel.TABLE_NAME + "," + ShootLiveModel.TABLE_NAME, null, 0);
		
		return mv;
	}
	
	/**
	 * 查询现场日志列表
	 * @param request
	 * @param page 分页参数
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryShootLogList")
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryShootLogList(HttpServletRequest request, Page page){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			String userId = this.getLoginUserId(request);
			
			//查询现场日志列表
			List<Map<String, Object>> shootLogList = this.noticeService.queryNoticeInfoWithNoticeTime(crewId, userId, page, true);
			
			Map<String, Object> result = new LinkedHashMap<String, Object>();
			List<Map<String, Object>> resultList = null;
			if(shootLogList != null && shootLogList.size() > 0) {
				for (Map<String, Object> map : shootLogList) {
					String noticeDate = map.get("noticeDate") + "";
					if(result.containsKey(noticeDate)){
						resultList = (List<Map<String, Object>>) result.get(map.get("noticeDate") + "");
					}else{
						resultList = new ArrayList<Map<String,Object>>();
						result.put(map.get("noticeDate") + "", resultList);
					}
					resultList.add(map);
				}
			}
			resultMap.put("shootList", result);
			resultMap.put("totalPageCount", page.getTotal());
		} catch (Exception e) {
			message = "未知异常,查询现场日志失败!";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 查询现场信息
	 * @param request
	 * @param noticeId 通告单ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryLiveInfo")
	public Map<String, Object> queryLiveInfo(HttpServletRequest request, String noticeId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			String crewId = this.getCrewId(request);
			
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
			//转场信息
			List<LiveConvertAddModel> liveConvertAddList = this.clipService.queryConvertByLiveId(crewId, noticeId);
			ShootLiveDto shootLiveDto = new ShootLiveDto(liveInfo, liveConvertAddList);
			
			resultMap.put("liveInfo", shootLiveDto);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
		} catch (Exception e) {
			message = "未知异常,查询现场信息失败!";
			success = false;
			
			logger.error(message, e);
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}

	/**
	 * 保存现场信息和转场信息
	 * @param request
	 * @param noticeId	通告单ID
	 * @param tapNo	带号
	 * @param shootLocation	拍摄地点
	 * @param shootScene	拍摄场景
	 * @param startTime	出发时间
	 * @param arriveTime	到场时间
	 * @param bootTime	开机时间
	 * @param packupTime	收工时间
	 * @param convertInfoStr  转场信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveShootLiveInfo")
	public Map<String, Object> saveShootLiveInfo(HttpServletRequest request, 
			String noticeId, String tapNo, String shootLocation,
			String shootScene, String startTime, String arriveTime,
			String bootTime, String packupTime, String convertInfoStr) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			String crewId = this.getCrewId(request);
			String userId = this.getLoginUserId(request);
			
			//现场信息
			ShootLiveDto shootLiveDto = new ShootLiveDto();			
			shootLiveDto.setTapNo(tapNo);
			shootLiveDto.setShootLocation(shootLocation);
			shootLiveDto.setShootScene(shootScene);
			shootLiveDto.setStartTime(startTime);
			shootLiveDto.setArriveTime(arriveTime);
			shootLiveDto.setBootTime(bootTime);
			shootLiveDto.setPackupTime(packupTime);
			
			//转场信息
			List<LiveConvertAddDto> convertInfoList = null;
			if(StringUtil.isNotBlank(convertInfoStr)) {
				String[] convertInfoStrArr = convertInfoStr.split("##");
				if(convertInfoStrArr != null && convertInfoStrArr.length > 0) {
					convertInfoList = new ArrayList<LiveConvertAddDto>();					
					for(String convertInfo : convertInfoStrArr) {
						String[] convertInfoArr = convertInfo.split(",", -1);
						if(convertInfoArr.length == 7) {
							LiveConvertAddDto liveConvertAddDto = new LiveConvertAddDto();
							liveConvertAddDto.setCshootLocation(convertInfoArr[1]);
							liveConvertAddDto.setCshootScene(convertInfoArr[2]);
							liveConvertAddDto.setConvertTime(convertInfoArr[3]);
							liveConvertAddDto.setCarriveTime(convertInfoArr[4]);
							liveConvertAddDto.setCbootTime(convertInfoArr[5]);
							liveConvertAddDto.setCpackupTime(convertInfoArr[6]);
							convertInfoList.add(liveConvertAddDto);
						}
					}
				}
			}
			shootLiveDto.setConvertInfoList(convertInfoList);
			//先删除现场信息和转场信息，再重新添加
			this.clipService.saveLiveInfo(crewId, noticeId, userId, shootLiveDto);
			
			this.sysLogService.saveSysLog(request, "保存现场信息和转场信息", terminal, ShootLiveModel.TABLE_NAME + "," + LiveConvertAddModel.TABLE_NAME, noticeId, SysLogOperType.INSERT.getValue());
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
		} catch (Exception e) {
			message = "未知异常,保存现场信息和转场信息失败!";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "保存现场信息和转场信息失败:" + e.getMessage(), terminal, ShootLiveModel.TABLE_NAME + "," + LiveConvertAddModel.TABLE_NAME, noticeId, SysLogOperType.ERROR.getValue());
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 删除转场信息
	 * @param request
	 * @param noticeId
	 * @param convertIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteConvertInfo")
	public Map<String, Object> deleteConvertInfo(HttpServletRequest request,String noticeId, String convertIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			if (StringUtils.isBlank(convertIds)) {
				throw new IllegalArgumentException("请选择需要删除的转场信息");
			}
			
			this.clipService.deleteConvertAddByIds(convertIds);
			
			this.sysLogService.saveSysLog(request, "删除转场信息(" + convertIds.split(",").length + ")", terminal, LiveConvertAddModel.TABLE_NAME, convertIds, 3);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
		} catch (Exception e) {
			message = "未知异常,删除转场信息失败!";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "删除转场信息(" + convertIds.split(",").length + ")失败：" + e.getMessage(), 
					terminal, LiveConvertAddModel.TABLE_NAME, convertIds, SysLogOperType.ERROR.getValue());
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 查询演员出勤信息
	 * 该接口当没有演员出勤信息时，自动保存通告单中演员信息
	 * @param request
	 * @param noticeId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryRoleAttendanceInfo")
	public Object queryRoleAttendanceInfo(HttpServletRequest request, String noticeId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			String crewId = this.getCrewId(request);
			String userId = this.getLoginUserId(request);
			
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
			message = ie.getMessage();
			success = false;
		} catch (Exception e) {
			message = "未知异常,查询演员出勤信息失败!";
			success = false;
			
			logger.error(message, e);
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 保存演员出勤信息
	 * 先删除出勤信息，再保存
	 * @param request
	 * @param noticeId
	 * @param majorguestActorStr
	 * @param massesActorStr
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveRoleAttendanceInfo")
	public Map<String, Object> saveRoleAttendanceInfo(HttpServletRequest request, 
			String noticeId, String majorguestActorStr, String massesActorStr) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		try {
			String crewId = this.getCrewId(request);
			String userId = this.getLoginUserId(request);
			
			List<RoleAttendanceDto> roleAttendanceDtoList = new ArrayList<RoleAttendanceDto>();
			//主演特约
			if(StringUtil.isNotBlank(majorguestActorStr)) {
				String[] majorguestActorStrArr = majorguestActorStr.split("##");
				if(majorguestActorStrArr != null && majorguestActorStrArr.length > 0) {
					for(String majorguestActor : majorguestActorStrArr) {
						String[] majorguestActorArr = majorguestActor.split(",", -1);
						if(majorguestActorArr.length == 8) {
							RoleAttendanceDto roleAttendanceDto = new RoleAttendanceDto();
							roleAttendanceDto.setRoleType(Integer.parseInt(majorguestActorArr[1]));
							roleAttendanceDto.setActorName(majorguestActorArr[2]);
							roleAttendanceDto.setViewRoleName(majorguestActorArr[3]);
							roleAttendanceDto.setRarriveTime(majorguestActorArr[4]);
							roleAttendanceDto.setRpackupTime(majorguestActorArr[5]);
							if(StringUtils.isNotBlank(majorguestActorArr[6])) {
								roleAttendanceDto.setIsLateArrive(Boolean.valueOf(majorguestActorArr[6]));
							}							
							if(StringUtils.isNotBlank(majorguestActorArr[7])) {
								roleAttendanceDto.setIsLatePackup(Boolean.valueOf(majorguestActorArr[7]));
							}
							roleAttendanceDtoList.add(roleAttendanceDto);
						}
					}
				}
			}
			//群演
			if(StringUtil.isNotBlank(massesActorStr)) {
				String[] massesActorStrArr = massesActorStr.split("##");
				if(massesActorStrArr != null && massesActorStrArr.length > 0) {
					for(String massesActor : massesActorStrArr) {
						String[] massesActorArr = massesActor.split(",", -1);
						if(massesActorArr.length == 5) {
							RoleAttendanceDto roleAttendanceDto = new RoleAttendanceDto();
							roleAttendanceDto.setRoleType(ViewRoleType.MassesActor.getValue());
							roleAttendanceDto.setViewRoleName(massesActorArr[1]);
							if(StringUtils.isNotBlank(massesActorArr[2])) {
								roleAttendanceDto.setRoleNum(Integer.parseInt(massesActorArr[2]));
							}
							roleAttendanceDto.setRarriveTime(massesActorArr[3]);
							roleAttendanceDto.setRpackupTime(massesActorArr[4]);
							roleAttendanceDtoList.add(roleAttendanceDto);
						}
					}
				}
			}
			this.clipService.saveRoleAttendance(crewId, noticeId, userId, roleAttendanceDtoList);
			this.sysLogService.saveSysLog(request, "保存演员出勤信息", terminal, RoleAttendanceModel.TABLE_NAME, noticeId, 1);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
		} catch (Exception e) {
			message = "未知异常,保存演员出勤信息失败!";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "保存演员出勤信息失败：" + e.getMessage(), terminal, RoleAttendanceModel.TABLE_NAME, noticeId, SysLogOperType.ERROR.getValue());
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 删除演员出勤信息
	 * @param request
	 * @param noticeId
	 * @param attendanceIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteRoleAttendanceInfo")
	public Map<String, Object> deleteRoleAttendanceInfo(HttpServletRequest request, String noticeId, String attendanceIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			if (StringUtils.isBlank(attendanceIds)) {
				throw new IllegalArgumentException("请选择需要删除的演员出勤信息");
			}
			
			String crewId = this.getCrewId(request);
			
			this.clipService.deleteRoleAttendanceByIds(crewId, noticeId, attendanceIds);

			this.sysLogService.saveSysLog(request, "删除演员出勤信息(" + attendanceIds.split(",").length + ")", terminal, RoleAttendanceModel.TABLE_NAME, attendanceIds, 3);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
		} catch (Exception e) {
			message = "未知异常,删除演员出勤信息失败!";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "删除演员出勤信息(" + attendanceIds.split(",").length + ")失败：" + e.getMessage(), terminal, RoleAttendanceModel.TABLE_NAME, attendanceIds, SysLogOperType.ERROR.getValue());
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 查询部门评分信息
	 * @param request
	 * @param noticeId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryDepartmentEvaluateInfo")
	public Object queryDepartmentEvaluateInfo(HttpServletRequest request, String noticeId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			String crewId = this.getCrewId(request);
			
			List<Map<String, Object>> departmentList = this.clipService.queryNoticeDepartScore(crewId, noticeId, null);
			resultMap.put("departmentList", departmentList);
			
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
		} catch (Exception e) {
			message = "未知异常,查询部门评分信息失败!";
			success = false;
			
			logger.error(message, e);
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 保存部门评分信息
	 * 含新增和修改
	 * @param request
	 * @param noticeId
	 * @param departmentScoreStr
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveDepartmentScore")
	public Map<String, Object> saveDepartmentScore(HttpServletRequest request, String noticeId, String departmentScoreStr) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}			
			
			String crewId = this.getCrewId(request);
			String userId = this.getLoginUserId(request);
			
			//已有的评价信息
			List<DepartmentEvaluateModel> existEvaluateList = this.clipService.queryDepartEvaluateByUserNotice(crewId, noticeId, null);
			
			//已有评价数据分组
			Map<String, DepartmentEvaluateModel> groupDepartEval = new HashMap<String, DepartmentEvaluateModel>();
			for (DepartmentEvaluateModel existEval : existEvaluateList) {
				groupDepartEval.put(existEval.getDepartmentId(), existEval);
			}
			
			List<DepartmentEvaluateModel> toAddDepartEvaluateList = new ArrayList<DepartmentEvaluateModel>();
			List<DepartmentEvaluateModel> toUpdateDepartEvaluateList = new ArrayList<DepartmentEvaluateModel>();
			
			if(StringUtil.isNotBlank(departmentScoreStr)) {
				String[] departmentScoreStrArr = departmentScoreStr.split("##");
				if(departmentScoreStrArr != null && departmentScoreStrArr.length > 0) {
					for(String departmentScore : departmentScoreStrArr) {
						String[] departmentScoreArr = departmentScore.split(",", -1);
						if(departmentScoreArr.length == 2) {
							String departmentId = departmentScoreArr[0];
							if (groupDepartEval.get(departmentId) == null) {
								DepartmentEvaluateModel departmentEvaluateModel = new DepartmentEvaluateModel();
								departmentEvaluateModel.setId(UUIDUtils.getId());
								departmentEvaluateModel.setDepartmentId(departmentId);
								departmentEvaluateModel.setUserId(userId);
								if(StringUtils.isNotBlank(departmentScoreArr[1])) {
									departmentEvaluateModel.setScore(Integer.parseInt(departmentScoreArr[1]));
								}
								departmentEvaluateModel.setCrewId(crewId);
								departmentEvaluateModel.setNoticeId(noticeId);
								departmentEvaluateModel.setCreateTime(new Date());
								departmentEvaluateModel.setLastUpdateTime(new Date());
								
								toAddDepartEvaluateList.add(departmentEvaluateModel);
							} else {
								DepartmentEvaluateModel departmentEvaluate = new DepartmentEvaluateModel();
								departmentEvaluate.setId(groupDepartEval.get(departmentId).getId());
								if(StringUtils.isNotBlank(departmentScoreArr[1])) {
									departmentEvaluate.setScore(Integer.parseInt(departmentScoreArr[1]));
								}
								departmentEvaluate.setLastUpdateTime(new Date());
								
								toUpdateDepartEvaluateList.add(departmentEvaluate);
							}
						}
					}
				}
			}
			
			this.clipService.addManyDepartEvaluate(toAddDepartEvaluateList);
			this.clipService.updateManyDepartEvaluate(toUpdateDepartEvaluateList);
			
			this.sysLogService.saveSysLog(request, "保存部门评分", terminal, DepartmentEvaluateModel.TABLE_NAME, noticeId, 1);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
		} catch (Exception e) {
			message = "未知异常,保存部门评分失败!";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "保存部门评分失败：" + e.getMessage(), terminal, DepartmentEvaluateModel.TABLE_NAME, noticeId, SysLogOperType.ERROR.getValue());
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 查询特殊道具信息
	 * @param request
	 * @param noticeId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryClipPropInfo")
	public Map<String, Object> queryClipPropInfo(HttpServletRequest request, String noticeId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			String crewId = this.getCrewId(request);
			String userId = this.getLoginUserId(request);
			
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
			message = ie.getMessage();
			success = false;
		} catch (Exception e) {
			message = "未知异常,查询特殊道具信息失败!";
			success = false;
			
			logger.error(message, e);
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 保存特殊道具信息
	 * 含新增、修改
	 * @param request
	 * @param noticeId
	 * @param clipPropInfoStr
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveClipPropInfo")
	public Object saveClipPropInfo(HttpServletRequest request, String noticeId, String clipPropInfoStr) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			String crewId = this.getCrewId(request);
			String userId = this.getLoginUserId(request);
			
			List<ClipPropModel> clipPropList = new ArrayList<ClipPropModel>();
			if(StringUtil.isNotBlank(clipPropInfoStr)) {
				String[] clipPropInfoStrArr = clipPropInfoStr.split("##");
				if(clipPropInfoStrArr != null && clipPropInfoStrArr.length > 0) {
					for(String clipPropInfo : clipPropInfoStrArr) {
						String[] clipPropInfoArr = clipPropInfo.split(",", -1);
						if(clipPropInfoArr.length == 4) {
							ClipPropModel clipPropModel = new ClipPropModel();
							clipPropModel.setPropId(clipPropInfoArr[0]);
							if(StringUtil.isBlank(clipPropInfoArr[1])) {
								throw new IllegalArgumentException("请填写道具名称");
							}
							clipPropModel.setName(clipPropInfoArr[1]);
							if(StringUtils.isNotBlank(clipPropInfoArr[2])) {
								clipPropModel.setNum(Integer.parseInt(clipPropInfoArr[2]));
							}
							clipPropModel.setComment(clipPropInfoArr[3]);
							clipPropList.add(clipPropModel);
						}
					}
				}
			}
			this.clipService.saveClipPropInfo(crewId, noticeId, userId, clipPropList);
			
			this.sysLogService.saveSysLog(request, "保存特殊道具", terminal, ClipPropModel.TABLE_NAME, noticeId, 1);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
		} catch (Exception e) {
			message = "未知异常,保存特殊道具信息失败!";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "保存特殊道具失败：" + e.getMessage(), terminal, ClipPropModel.TABLE_NAME, noticeId, SysLogOperType.ERROR.getValue());
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 删除特殊道具信息
	 * @param request
	 * @param noticeId
	 * @param propIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteClipPropInfo")
	public Map<String, Object> deleteClipPropInfo(HttpServletRequest request, String noticeId, String propIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			if (StringUtils.isBlank(propIds)) {
				throw new IllegalArgumentException("请选择需要删除的道具");
			}
			String crewId = this.getCrewId(request);
			
			this.clipService.deleteClipPropByIds(crewId, noticeId, propIds);

			this.sysLogService.saveSysLog(request, "删除特殊道具信息(" + propIds.split(",").length + ")", terminal, ClipPropModel.TABLE_NAME, propIds, 3);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
		} catch (Exception e) {
			message = "未知异常，删除特殊道具信息失败!";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "删除特殊道具信息(" + propIds.split(",").length + ")失败：" + e.getMessage(), 
					terminal, ClipPropModel.TABLE_NAME, propIds, SysLogOperType.ERROR.getValue());
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 查询重要备注信息
	 * @param request
	 * @param noticeId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryImportantCommentList")
	public Object queryImportantCommentList(HttpServletRequest request, String noticeId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			String crewId = this.getCrewId(request);
			
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
			message = ie.getMessage();
			success = false;
		} catch (Exception e) {
			message = "未知异常,查询重要备注信息失败!";
			success = false;
			
			logger.error(message, e);
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 保存重要备注信息
	 * 含新增、修改
	 * @param request
	 * @param noticeId
	 * @param commentInfoStr
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveClipCommentInfo")
	public Object saveClipCommentInfo(HttpServletRequest request, String noticeId, String commentInfoStr) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			String crewId = this.getCrewId(request);
			String userId = this.getLoginUserId(request);
			
			List<ClipCommentDto> clipCommentList = new ArrayList<ClipCommentDto>();
			if(StringUtil.isNotBlank(commentInfoStr)) {
				String[] commentInfoStrArr = commentInfoStr.split("##");
				if(commentInfoStrArr != null && commentInfoStrArr.length > 0) {
					for(String commentInfo : commentInfoStrArr) {
						String[] commentInfoArr = commentInfo.split(",", -1);
						if(commentInfoArr.length == 2) {
							ClipCommentDto clipCommentDto = new ClipCommentDto();
							clipCommentDto.setCommentId(commentInfoArr[0]);
							clipCommentDto.setContent(commentInfoArr[1]);
							clipCommentList.add(clipCommentDto);
						}
					}
				}
			}
			this.clipService.saveCommentInfo(crewId, noticeId, userId, clipCommentList);
			
			this.sysLogService.saveSysLog(request, "保存重要备注", terminal, ClipCommentModel.TABLE_NAME, noticeId, 1);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
		} catch (Exception e) {
			message = "未知异常,保存重要备注信息失败!";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "保存重要备注失败：" + e.getMessage(), terminal, ClipCommentModel.TABLE_NAME, noticeId, SysLogOperType.ERROR.getValue());
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 删除重要备注信息
	 * @param request
	 * @param noticeId
	 * @param commentIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteImportCommentInfo")
	public Map<String, Object> deleteImportCommentInfo(HttpServletRequest request, String noticeId, String commentIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		try {
			if (StringUtils.isBlank(noticeId)) {
				throw new IllegalArgumentException("请提供通告单信息");
			}
			
			String crewId = this.getCrewId(request);
			String userId = this.getLoginUserId(request);
			
			this.clipService.deleteCommentByIds(crewId, noticeId, userId, commentIds);

			this.sysLogService.saveSysLog(request, "删除重要备注信息(" + commentIds.split(",").length + ")", 
					terminal, ClipCommentModel.TABLE_NAME, commentIds, 3);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
		} catch (Exception e) {
			message = "未知异常,删除重要备注信息失败!";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "删除重要备注信息(" + commentIds.split(",").length + ")失败：" + e.getMessage(), 
					terminal, ClipCommentModel.TABLE_NAME, commentIds, SysLogOperType.ERROR.getValue());
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 根据附件包ID查询附件
	 * @param request
	 * @param noticeId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryAttachmentById")
	public Object queryAttachmentById(HttpServletRequest request, String attpackId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		try {
			if (StringUtils.isBlank(attpackId)) {
				throw new IllegalArgumentException("请提供附件包ID");
			}
			
			List<AttachmentModel> attachmentList = this.attachmentService.queryAttByPackId(attpackId);
			
			List<AttachmentDto> attachmentDtoList = new ArrayList<AttachmentDto>();
			for (AttachmentModel attachmentInfo : attachmentList) {
				AttachmentDto attachmentDto = new AttachmentDto();
				attachmentDto.setAttachmentId(attachmentInfo.getId());
				attachmentDto.setAttpackId(attachmentInfo.getAttpackId());
				attachmentDto.setName(attachmentInfo.getName());
				attachmentDto.setType(attachmentInfo.getType());
				attachmentDto.setSuffix(attachmentInfo.getSuffix());
				attachmentDto.setSize(attachmentInfo.getSize());
				attachmentDto.setLength(attachmentInfo.getLength());
				attachmentDto.setHdPreviewUrl(attachmentInfo.getHdStorePath());
				attachmentDto.setSdPreviewUrl(attachmentInfo.getSdStorePath());
				attachmentDtoList.add(attachmentDto);
			}
			
			resultMap.put("attachmentList", attachmentDtoList);
			
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
		} catch (Exception e) {
			message = "未知异常,查询重要备注信息失败!";
			success = false;
			
			logger.error(message, e);
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
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
}
