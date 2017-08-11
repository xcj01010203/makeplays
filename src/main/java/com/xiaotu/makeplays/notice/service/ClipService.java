package com.xiaotu.makeplays.notice.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.attachment.dao.AttachmentDao;
import com.xiaotu.makeplays.attachment.dao.AttachmentPacketDao;
import com.xiaotu.makeplays.attachment.model.AttachmentModel;
import com.xiaotu.makeplays.attachment.model.AttachmentPacketModel;
import com.xiaotu.makeplays.attachment.model.constants.AttachmentBuzType;
import com.xiaotu.makeplays.mobile.server.notice.dto.ClipRequestDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.AttendanceDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.ClipCommentDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.ClipInfoDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.ClipViewInfoDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.LiveConvertAddDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.RoleAttendanceDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.ShootAuditionDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.ShootLiveDto;
import com.xiaotu.makeplays.notice.dao.NoticeTimeDao;
import com.xiaotu.makeplays.notice.dao.clip.CameraInfoDao;
import com.xiaotu.makeplays.notice.dao.clip.ClipCommentDao;
import com.xiaotu.makeplays.notice.dao.clip.ClipPropDao;
import com.xiaotu.makeplays.notice.dao.clip.DepartmentEvaluateDao;
import com.xiaotu.makeplays.notice.dao.clip.LiveConvertAddDao;
import com.xiaotu.makeplays.notice.dao.clip.RoleAttendanceDao;
import com.xiaotu.makeplays.notice.dao.clip.ShootAuditionDao;
import com.xiaotu.makeplays.notice.dao.clip.ShootLiveDao;
import com.xiaotu.makeplays.notice.dao.clip.TmpCancelViewInfoDao;
import com.xiaotu.makeplays.notice.model.NoticeInfoModel;
import com.xiaotu.makeplays.notice.model.NoticeTimeModel;
import com.xiaotu.makeplays.notice.model.clip.CameraInfoModel;
import com.xiaotu.makeplays.notice.model.clip.ClipCommentModel;
import com.xiaotu.makeplays.notice.model.clip.ClipPropModel;
import com.xiaotu.makeplays.notice.model.clip.DepartmentEvaluateModel;
import com.xiaotu.makeplays.notice.model.clip.LiveConvertAddModel;
import com.xiaotu.makeplays.notice.model.clip.RoleAttendanceModel;
import com.xiaotu.makeplays.notice.model.clip.ShootAuditionModel;
import com.xiaotu.makeplays.notice.model.clip.ShootLiveModel;
import com.xiaotu.makeplays.notice.model.clip.TmpCancelViewInfoModel;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.view.controller.dto.CancelViewDto;
import com.xiaotu.makeplays.view.service.ViewInfoService;

/**
 * 场记单
 * @author xuchangjian 2015-11-9下午3:42:24
 */
@Service
public class ClipService {
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private ShootAuditionDao shootAuditionDao;
	
	@Autowired
	private ShootLiveDao shootLiveDao;
	
	@Autowired
	private LiveConvertAddDao liveConvertAddDao;
	
	@Autowired
	private ClipCommentDao clipCommentDao;
	
	@Autowired
	private ClipPropDao clipPropDao;
	
	@Autowired
	private RoleAttendanceDao roleAttendanceDao;
	
	@Autowired
	private CameraInfoDao cameraInfoDao;
	
	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	@Autowired
	private NoticeTimeDao noticeTimeDao;
	
	@Autowired
	private TmpCancelViewInfoDao tmpCancelViewInfoDao;
	
	@Autowired
	private DepartmentEvaluateDao departmentEvaluateDao;	
	
	@Autowired
	private AttachmentDao attachmentDao;
	
	@Autowired
	private AttachmentPacketDao attachmentPacketDao;
	/**
	 * 根据通告单ID查询通告单下的镜次列表
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	public List<Map<String, Object>> queryNoticeAudition(String crewId, String noticeId, String userId) {
		return this.shootAuditionDao.queryByNoticeId(crewId, noticeId, userId);
	}
	
	/**
	 * 查询剧组下的机位列表
	 * @param crewId
	 * @return
	 */
	public List<CameraInfoModel> queryCameraListByCrewId(String crewId) {
		return this.cameraInfoDao.queryByCrewId(crewId);
	}
	
	/**
	 * 根据通告单ID查询现场信息
	 * @param crewId
	 * @param noticeId
	 * @return
	 * @throws Exception 
	 */
	public ShootLiveModel queryLiveInfoByNoticeId(String crewId, String noticeId) throws Exception {
		return this.shootLiveDao.queryByNoticeId(crewId, noticeId);
	}
	
	/**
	 * 根据现场信息查询下面的转场信息
	 * @param liveId
	 * @return
	 */
	public List<LiveConvertAddModel> queryConvertByLiveId(String crewId, String noticeId) {
		return this.liveConvertAddDao.queryByLiveId(crewId, noticeId);
	}
	
	/**
	 * 查询通告单下演员出勤信息
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	public List<RoleAttendanceModel> queryRoleAttenceInfoByNoticeId(String crewId, String noticeId) {
		return this.roleAttendanceDao.queryByNoticeId(crewId, noticeId);
	}
	
	/**
	 * 查询场记单中特殊道具信息
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	public List<ClipPropModel> queryClipPropListByNoticeId(String crewId, String noticeId) {
		return this.clipPropDao.queryByNoticeId(crewId, noticeId);
	}
	
	/**
	 * 查询通告单下的重要备注信息
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	public List<ClipCommentModel> queryClipCommentByNoticeId(String crewId, String noticeId) {
		return this.clipCommentDao.queryByNoticeId(crewId, noticeId);
	}
	
	/**
	 * 保存场记单信息
	 * @param clipRequestDto
	 * @throws Exception 
	 */
	public String saveClipInfo(String crewId, String userId, ClipRequestDto clipRequestDto) throws Exception {
		//业务信息
		String groupName = clipRequestDto.getGroupName();
		String noticeDate = clipRequestDto.getNoticeDate();
		List<ClipInfoDto> clipInfo = clipRequestDto.getClipInfo();
		ShootLiveDto liveInfo = clipRequestDto.getLiveInfo();
		AttendanceDto attendanceInfo = clipRequestDto.getAttendanceInfo();
		List<ClipPropModel> specialPropList = clipRequestDto.getSpecialPropList();
		List<ClipCommentDto> commentInfoList = clipRequestDto.getCommentInfo();
		String weatherInfo = clipRequestDto.getWeatherInfo();
		
		//数据校验
		if (StringUtils.isBlank(groupName)) {
			throw new IllegalArgumentException("请填写所属通告单的组名");
		}
		if (StringUtils.isBlank(noticeDate)) {
			throw new IllegalArgumentException("请填写通告单日期");
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
		
		/*
		 * 保存业务信息
		 */
		//保存镜次信息
		if (clipInfo != null) {
			this.saveAuditionInfo(crewId, noticeId, userId, noticeDate, groupName, clipInfo);
		}
		//保存现场信息
		if (liveInfo != null) {
			this.saveLiveInfo(crewId, noticeId, userId, liveInfo);
		}
		//保存演员出勤信息
		if (attendanceInfo != null) {
			List<RoleAttendanceDto> roleAttendanceDtoList = new ArrayList<RoleAttendanceDto>();
			if (attendanceInfo.getMajorRoleAttenInfo() != null) {
				roleAttendanceDtoList.addAll(attendanceInfo.getMajorRoleAttenInfo());
			}
			if (attendanceInfo.getNotMajRoleAttenInfo() != null) {
				roleAttendanceDtoList.addAll(attendanceInfo.getNotMajRoleAttenInfo());
			}
			this.saveRoleAttendance(crewId, noticeId, userId, roleAttendanceDtoList);
		}
		//保存特殊道具信息
		if (specialPropList != null) {
			this.saveClipPropInfo(crewId, noticeId, userId, specialPropList);
		}
		//保存备注信息
		if (commentInfoList != null) {
			this.saveCommentInfo(crewId, noticeId, userId, commentInfoList);
		}
		//保存天气信息
		if (!StringUtils.isBlank(weatherInfo)) {
			NoticeTimeModel noticeTime = this.noticeService.queryNoticeTimeByNoticeId(noticeId);
			noticeTime.setWeatherInfo(weatherInfo);
			this.noticeTimeDao.update(noticeTime, "noticeTimeId");
		}
		
		return noticeId;
	}
	
	/**
	 * 保存场记单中重要备注信息
	 * @param crewId
	 * @param noticeId
	 * @param commentInfo
	 * @throws Exception 
	 */
	public void saveCommentInfo(String crewId, String noticeId, String userId, List<ClipCommentDto> commentInfoList) throws Exception {
		List<ClipCommentModel> toAddCommentList = new ArrayList<ClipCommentModel>();
		List<ClipCommentModel> toUpdateCommentList = new ArrayList<ClipCommentModel>();
		List<ClipCommentModel> toDeleteCommentList = new ArrayList<ClipCommentModel>();
		
		
		List<ClipCommentModel> existCommentList = this.clipCommentDao.queryByNoticeId(crewId, noticeId);
		Map<String, ClipCommentModel> groupCommentMap = new HashMap<String, ClipCommentModel>();
		for (ClipCommentModel clipCommentInfo : existCommentList) {
			if (!groupCommentMap.containsKey(clipCommentInfo.getCommentId())) {
				groupCommentMap.put(clipCommentInfo.getCommentId(), clipCommentInfo);
			}
			
			boolean needDelete = true;
			for (ClipCommentDto clipCommentDto : commentInfoList) {
				if (!StringUtils.isBlank(clipCommentDto.getCommentId()) 
						&& clipCommentDto.getCommentId().equals(clipCommentInfo.getCommentId())) {
					needDelete = false;
				}
			}
			
			if (needDelete) {
				toDeleteCommentList.add(clipCommentInfo);
			}
		}
		
		
		for (ClipCommentDto commentDto : commentInfoList) {
			if (StringUtils.isBlank(commentDto.getContent())) {
				continue;
			}
			
			ClipCommentModel clipCommentModel = new ClipCommentModel();
			String commentId = commentDto.getCommentId();
			if (StringUtils.isBlank(commentId) || !groupCommentMap.containsKey(commentId)) {
				//新增
				clipCommentModel.setCommentId(UUIDUtils.getId());
				
				String attpackId = this.createNewPacket(crewId, AttachmentBuzType.ClipComment.getValue());
				clipCommentModel.setAttpackId(attpackId);
				
			} else {
				//修改
				clipCommentModel.setCommentId(commentId);
				clipCommentModel.setAttpackId(groupCommentMap.get(commentId).getAttpackId());
				
				if (StringUtils.isBlank(clipCommentModel.getAttpackId())) {
					String attpackId = this.createNewPacket(crewId, AttachmentBuzType.ClipComment.getValue());
					clipCommentModel.setAttpackId(attpackId);
				}
			}
			clipCommentModel.setCrewId(crewId);
			clipCommentModel.setNoticeId(noticeId);
			clipCommentModel.setContent(commentDto.getContent());
			clipCommentModel.setUserId(userId);
			String mobileTimeStr = commentDto.getMobileTime();
			if (!StringUtils.isBlank(mobileTimeStr)) {
				clipCommentModel.setMobileTime(this.sdf1.parse(mobileTimeStr));
			}
			clipCommentModel.setServerTime(new Date());
			
			if (StringUtils.isBlank(commentId)) {
				toAddCommentList.add(clipCommentModel);
			} else {
				toUpdateCommentList.add(clipCommentModel);
			}
			
		}
		
		this.addClipCommentBatch(toAddCommentList);
		this.updateClipCommentBatch(toUpdateCommentList);
		
		//处理待删除的数据
		for (ClipCommentModel clipCommentInfo : toDeleteCommentList) {
			if (!StringUtils.isBlank(clipCommentInfo.getAttpackId())) {
				this.deleteAttachmentByPackId(clipCommentInfo.getAttpackId());
			}
			
			this.clipCommentDao.deleteOne(clipCommentInfo.getCommentId(), "commentId", ClipCommentModel.TABLE_NAME);
		}
	}
	
	/**
	 * 批量删除重要备注信息
	 * @param crewId
	 * @param userId
	 * @param noticeId
	 * @param propIds
	 * @throws Exception 
	 */
	public void deleteCommentByIds(String crewId, String noticeId, String userId, String commentIds) throws Exception {
		List<ClipCommentModel> commentList = this.clipCommentDao.queryByIds(crewId, noticeId, commentIds);
		
		for (ClipCommentModel clipCommentInfo : commentList) {
			if (!StringUtils.isBlank(clipCommentInfo.getAttpackId())) {
				this.deleteAttachmentByPackId(clipCommentInfo.getAttpackId());
			}
			
			this.clipCommentDao.deleteOne(clipCommentInfo.getCommentId(), "commentId", ClipCommentModel.TABLE_NAME);
		}
	}
	
	/**
	 * 保存单个备注信息
	 * @param crewId
	 * @param noticeId
	 * @param userId
	 * @param clipCommentDto
	 * @return 返回保存的重要备注信息
	 * @throws Exception 
	 */
	public ClipCommentModel saveCommentInfo(String crewId, String noticeId, String userId, String commentId, String content, String attpackId, String mobileTime) throws Exception {
		ClipCommentModel clipCommentModel = new ClipCommentModel();
		
		if (StringUtils.isBlank(commentId)) {
			//新增
			clipCommentModel.setCommentId(UUIDUtils.getId());
			attpackId = this.createNewPacket(crewId, AttachmentBuzType.ClipComment.getValue());
		} else {
			//修改
			clipCommentModel.setCommentId(commentId);
		}
		clipCommentModel.setCrewId(crewId);
		clipCommentModel.setNoticeId(noticeId);
		clipCommentModel.setContent(content);
		clipCommentModel.setUserId(userId);
		clipCommentModel.setAttpackId(attpackId);
		if (!StringUtils.isBlank(mobileTime)) {
			clipCommentModel.setMobileTime(this.sdf1.parse(mobileTime));
		}
		clipCommentModel.setServerTime(new Date());
		
		if (StringUtils.isBlank(commentId)) {
			this.clipCommentDao.add(clipCommentModel);
		} else {
			this.clipCommentDao.update(clipCommentModel, "commentId");
		}
		
		return clipCommentModel;
	}
	
	/**
	 * 添加一条现场信息
	 * @param shootLive
	 * @throws Exception 
	 */
	public void addOneShootLive(ShootLiveModel shootLive) throws Exception {
		this.shootLiveDao.add(shootLive);
	}
	
	/**
	 * 修改一条现场信息
	 * @param shootLive
	 * @throws Exception
	 */
	public void updateOneShootLive(ShootLiveModel shootLive) throws Exception {
		this.shootLiveDao.update(shootLive, "liveId");
	}
	
	/**
	 * 批量新增场记单重要备注信息
	 * @param clipCommentList
	 * @throws Exception 
	 */
	public void addClipCommentBatch(List<ClipCommentModel> clipCommentList) throws Exception {
		for (ClipCommentModel clipComment : clipCommentList) {
			this.clipCommentDao.add(clipComment);
		}
	}
	
	/**
	 * 批量更新场记单重要备注信息
	 * @param clipCommentList
	 * @throws Exception 
	 */
	public void updateClipCommentBatch(List<ClipCommentModel> clipCommentList) throws Exception {
		for (ClipCommentModel clipComment : clipCommentList) {
			this.clipCommentDao.update(clipComment, "commentId");
		}
	}
	
	/**
	 * 保存场记单道具信息
	 * 根据道具名称作为唯一标识，比对出需要新增、修改、删除的道具信息
	 * @param specialPropList
	 * @throws Exception 
	 */
	public List<ClipPropModel> saveClipPropInfo(String crewId, String noticeId, String userId, List<ClipPropModel> specialPropList) throws Exception {
		List<ClipPropModel> result = new ArrayList<ClipPropModel>();
		
		List<ClipPropModel> toAddPropList = new ArrayList<ClipPropModel>();
		List<ClipPropModel> toUpdatePropList = new ArrayList<ClipPropModel>();
		List<ClipPropModel> toDeletePropIdList = new ArrayList<ClipPropModel>();
		
		
		List<ClipPropModel> existPropList = this.clipPropDao.queryByNoticeId(crewId, noticeId);
		Map<String, ClipPropModel> groupPropMap = new HashMap<String, ClipPropModel>();
		for (ClipPropModel existClipPropModel : existPropList) {
			if (!groupPropMap.containsKey(existClipPropModel.getName())) {
				groupPropMap.put(existClipPropModel.getPropId(), existClipPropModel);
			}
			
			//过滤出需要删除的数据
			boolean needDelete = true;
			for (ClipPropModel clipPropModel : specialPropList) {
				if (!StringUtils.isBlank(clipPropModel.getPropId()) && clipPropModel.getPropId().equals(existClipPropModel.getPropId())) {
					needDelete = false;
					break;
				}
			}
			
			if (needDelete) {
				toDeletePropIdList.add(existClipPropModel);
			}
		}
		
		
		for (ClipPropModel clipPropModel : specialPropList) {
			if (StringUtils.isBlank(clipPropModel.getName())) {
				continue;
			}
			
			String propId = clipPropModel.getPropId();
			
			if (!StringUtils.isBlank(clipPropModel.getPropId()) 
					&& groupPropMap.containsKey(clipPropModel.getPropId())) {
				//修改的数据
				clipPropModel.setPropId(clipPropModel.getPropId());
				clipPropModel.setUserId(userId);
				clipPropModel.setAttpackId(groupPropMap.get(propId).getAttpackId());
				
				//兼容老数据中没有附件包ID的情况
				if (StringUtils.isBlank(clipPropModel.getAttpackId())) {
					String attpackId = this.createNewPacket(crewId, AttachmentBuzType.ClipProp.getValue());
					clipPropModel.setAttpackId(attpackId);
				}
				
				toUpdatePropList.add(clipPropModel);
			} else {
				//新增的数据
				clipPropModel.setPropId(UUIDUtils.getId());
				
				String attpackId = this.createNewPacket(crewId, AttachmentBuzType.ClipProp.getValue());
				clipPropModel.setAttpackId(attpackId);
				
				clipPropModel.setCrewId(crewId);
				clipPropModel.setNoticeId(noticeId);
				clipPropModel.setUserId(userId);
				
				toAddPropList.add(clipPropModel);
			}
			
			result.add(clipPropModel);
			
//			String clipPropId = clipPropModel.getPropId();
//			if (StringUtils.isBlank(clipPropId)) {
//				clipPropModel.setPropId(UUIDUtils.getId());
//				
//				String attpackId = UUIDUtils.getId();
//				clipPropModel.setAttpackId(attpackId);
//			}
//			clipPropModel.setCrewId(crewId);
//			clipPropModel.setNoticeId(noticeId);
//			clipPropModel.setUserId(userId);
//			
//			toAddPropList.add(clipPropModel);
		}
		
		this.addClipPropBatch(toAddPropList);
		this.updateClipPropBatch(toUpdatePropList);
		
		
		//处理需要删除的数据
		for (ClipPropModel clipPropInfo : toDeletePropIdList) {
			if (!StringUtils.isBlank(clipPropInfo.getAttpackId())) {
				this.deleteAttachmentByPackId(clipPropInfo.getAttpackId());
			}
			
			this.clipPropDao.deleteOne(clipPropInfo.getPropId(), "propId", ClipPropModel.TABLE_NAME);
		}
		
		return result;
	}
	
	/**
	 * 保存单个道具信息
	 * @param crewId
	 * @param userId
	 * @param noticeId
	 * @param propInfo
	 * @return
	 * @throws Exception 
	 */
	public ClipPropModel saveClipPropInfo(String crewId, String userId, String noticeId, String propId, String name, Integer num, String attpackId, String comment) throws Exception {
		ClipPropModel propInfo = new ClipPropModel();
		if (StringUtils.isBlank(propId)) {
			propInfo.setPropId(UUIDUtils.getId());
			
			attpackId = this.createNewPacket(crewId, AttachmentBuzType.ClipProp.getValue());
			propInfo.setAttpackId(attpackId);
		} else {
			propInfo.setPropId(propId);
		}
		propInfo.setCrewId(crewId);
		propInfo.setNoticeId(noticeId);
		propInfo.setUserId(userId);
		propInfo.setName(name);
		if(num != null) {
			propInfo.setNum(num);
		}
		propInfo.setAttpackId(attpackId);
		propInfo.setComment(comment);
		
		if (StringUtils.isBlank(propId)) {
			this.addOneClipProp(propInfo);
		} else {
			this.updateOneClipProp(propInfo);
		}
		return propInfo;
	}
	
	/**
	 * 新建一个新的附件包
	 * @param buzType	附件包关联的业务类型
	 * @return
	 * @throws Exception 
	 */
	private String createNewPacket(String crewId, Integer buzType) throws Exception {
		AttachmentPacketModel attachmentPacketModel = new AttachmentPacketModel();
		
		String packedId = UUIDUtils.getId();
		attachmentPacketModel.setId(packedId);
		attachmentPacketModel.setCrewId(crewId);
		attachmentPacketModel.setBuzType(buzType);
		attachmentPacketModel.setCreateTime(new Date());
		if (buzType != null) {
			attachmentPacketModel.setRelatedToBuz(true);
		}
		
		this.attachmentPacketDao.add(attachmentPacketModel);
		
		return packedId;
	}
	
	/**
	 * 根据附件包ID删除附件信息
	 * @param attpackId
	 * @throws Exception 
	 */
	private void deleteAttachmentByPackId(String attpackId) throws Exception {
		//首先删除附件文件
		List<AttachmentModel> attachmentList = this.attachmentDao.queryByPackId(attpackId);
		try {
			for (AttachmentModel attachment : attachmentList) {
				if (!StringUtils.isBlank(attachment.getHdStorePath())) {
					FileUtils.deleteFile(attachment.getHdStorePath());
				}
				if (!StringUtils.isBlank(attachment.getSdStorePath())) {
					FileUtils.deleteFile(attachment.getSdStorePath());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//然后删除附件记录
		this.attachmentDao.deleteByPackId(attpackId);
		this.attachmentPacketDao.deleteOne(attpackId, "id", AttachmentPacketModel.TABLE_NAME);
	}
	
	/**
	 * 根据道具ID批量删除道具信息
	 * @param propIds
	 * @throws Exception 
	 */
	public void deleteClipPropByIds(String crewId, String noticeId, String propIds) throws Exception {
		List<ClipPropModel> clipPropList = this.clipPropDao.queryByIds(crewId, noticeId, propIds);
		for (ClipPropModel clipPropInfo : clipPropList) {
			if (!StringUtils.isBlank(clipPropInfo.getAttpackId())) {
				this.deleteAttachmentByPackId(clipPropInfo.getAttpackId());
			}
			
			this.clipPropDao.deleteOne(clipPropInfo.getPropId(), "propId", ClipPropModel.TABLE_NAME);
		}
	}
	
	/**
	 * 批量新增场记单道具信息
	 * @param clipPropModelList
	 * @throws Exception 
	 */
	public void addClipPropBatch(List<ClipPropModel> clipPropModelList) throws Exception {
		for (ClipPropModel clipPropModel : clipPropModelList) {
			this.clipPropDao.add(clipPropModel);
		}
	}
	
	/**
	 * 新增单条场记单道具信息
	 * @param clipPropInfo
	 * @throws Exception 
	 */
	public void addOneClipProp(ClipPropModel clipPropInfo) throws Exception {
		this.clipPropDao.add(clipPropInfo);
	}
	
	/**
	 * 批量更新场记单道具信息
	 * @param clipPropModelList
	 * @throws Exception 
	 */
	public void updateClipPropBatch(List<ClipPropModel> clipPropModelList) throws Exception {
		for (ClipPropModel clipPropModel : clipPropModelList) {
			this.clipPropDao.update(clipPropModel, "propId");
		}
	}
	
	/**
	 * 更新单条场记单道具信息
	 * @param clipPropInfo
	 * @throws Exception 
	 */
	public void updateOneClipProp(ClipPropModel clipPropInfo) throws Exception {
		this.clipPropDao.update(clipPropInfo, "propId");
	}
	
	/**
	 * 辅助方法
	 * 保存镜次信息
	 * @param clipInfo
	 * @throws Exception 
	 */
	private void saveAuditionInfo(String crewId, String noticeId, String userId, String noticeDate, String groupName, List<ClipInfoDto> clipInfoList) throws Exception {
		List<CameraInfoModel> cameraList = this.queryCameraListByCrewId(crewId);
		Map<String, String> cameraInfoMap = new HashMap<String, String>();	//机位map，键为机位名称，值为机位id
		
		for (CameraInfoModel cameraInfo : cameraList) {
			cameraInfoMap.put(cameraInfo.getCameraName(), cameraInfo.getCameraId());
		}
		List<CameraInfoModel> toSaveCameraList = new ArrayList<CameraInfoModel>();
		
		List<String> myCameraNameList = new ArrayList<String>();
		for (ClipInfoDto clipInfo : clipInfoList) {
			//机位信息
			String cameraName = clipInfo.getCameraName();
			if (!cameraInfoMap.containsKey(cameraName) && !StringUtils.isBlank(cameraName)) {
				CameraInfoModel cameraInfo = new CameraInfoModel();
				String cameraId = UUIDUtils.getId();
				cameraInfo.setCameraId(cameraId);
				cameraInfo.setCameraName(cameraName);
				cameraInfo.setCreateTime(new Date());
				cameraInfo.setCrewId(crewId);
				cameraInfo.setUserId(userId);
				
				toSaveCameraList.add(cameraInfo);
				
				cameraInfoMap.put(cameraName, cameraId);
			}
			
			if (!myCameraNameList.contains(cameraName)) {
				myCameraNameList.add(cameraName);
			}
		}
		
		//校验是否有其他人提交了相同机位的镜次
//		List<Map<String, Object>> existAuditionList = this.queryNoticeAudition(crewId, noticeId, null);
//		for (Map<String, Object> existAuditionMap : existAuditionList) {
//			String cameraId = (String) existAuditionMap.get("cameraId");
//			String existUserId = (String) existAuditionMap.get("userId");
//			String realName = (String) existAuditionMap.get("realName");
//			String cameraName = (String) existAuditionMap.get("cameraName");
//			String existDeviceUID = (String) existAuditionMap.get("deviceUID");
//			
//			if (myCameraNameList.contains(cameraName)
//					&& (!existUserId.equals(userId) || (!StringUtils.isBlank(existDeviceUID) && !existDeviceUID.equals(deviceUID)))) {
//				if (!existUserId.equals(userId)) {
//					throw new IllegalArgumentException(realName + "已记录了"
//							+ cameraName + "镜次信息，不允许重复提交。请联系管理员。");
//				}
//				if (!existDeviceUID.equals(deviceUID)) {
//					throw new IllegalArgumentException(cameraName + "镜次信息已被"
//							+ realName + "账户用其他设备记录过，不允许重复提交。请联系管理员。");
//				}
//			}
//		}
		
		//把场景整理到以集场号为key，场景ID为value的map中
//		List<ViewInfoModel> viewList = new ArrayList<ViewInfoModel>();
//		if (seriesViewNoList != null && seriesViewNoList.size() > 0) {
//			viewList = this.viewInfoService.queryBySeriesViewNoList(crewId, seriesViewNoList);
//		}
//		Map<String, String> viewSeriesViewMap = new HashMap<String, String>();
//		String viewIds = "";
//		for (ViewInfoModel viewInfo : viewList) {
//			String viewId = viewInfo.getViewId();
//			int seriesNo = viewInfo.getSeriesNo();
//			String viewNo = viewInfo.getViewNo();
//			
//			String seriesViewNo = seriesNo + "-" + viewNo;
//			
//			if (!viewSeriesViewMap.containsKey(seriesViewNo)) {
//				viewSeriesViewMap.put(seriesViewNo, viewId);
//				viewIds += viewId + ",";
//			}
//		}
		
		
		//查询该通告单下已有的临时销场信息
		List<TmpCancelViewInfoModel> existTmpCancelList = this.queryTmpCancelInfoByViewIds(crewId, noticeId);
		Map<String, String> existTmpCancelMap = new HashMap<String, String>();	//key为“集-场”, value为临时销场ID
		for (TmpCancelViewInfoModel tempCancelViewModel : existTmpCancelList) {
			int seriesNo = tempCancelViewModel.getSeriesNo();
			String viewNo = tempCancelViewModel.getViewNo();
			//集场号不区分大小写
			String seriesViewNo = seriesNo + "-" + viewNo.toLowerCase();
			if (!existTmpCancelMap.containsKey(seriesViewNo)) {
				existTmpCancelMap.put(seriesViewNo, tempCancelViewModel.getId());
			}
		}
		
		//删除通告单下已有的相同机位的镜次信息
		this.shootAuditionDao.deleteByNoticeId(crewId, noticeId, myCameraNameList);
		
		List<ShootAuditionModel> toAddShootAuditionList = new ArrayList<ShootAuditionModel>();	//待新增的镜次信息
		List<TmpCancelViewInfoModel> toAddTmpCancelViewList = new ArrayList<TmpCancelViewInfoModel>();	//待添加临时销场信息
		List<TmpCancelViewInfoModel> toUpdateTmpCancelViewList = new ArrayList<TmpCancelViewInfoModel>();	//待更新临时销场信息
		
		int sequence = 0;
		for (ClipInfoDto clipInfo : clipInfoList) {
			String cameraName = clipInfo.getCameraName();
			String cameraId = cameraInfoMap.get(cameraName);
			
			//场景信息
			List<ClipViewInfoDto> clipViewDtoList = clipInfo.getViewInfoList();
			if (clipViewDtoList == null) {
				clipViewDtoList = new ArrayList<ClipViewInfoDto>();
			}
			
			List<String> seriesViewNoList = new ArrayList<String>();
			for (ClipViewInfoDto clipViewInfo : clipViewDtoList) {
				Integer seriesNo = clipViewInfo.getSeriesNo();
				String viewNo = clipViewInfo.getViewNo();
				
				String seriesViewNo = seriesNo + "-" + viewNo.toLowerCase();
				
				//校验是否有重复
				if (seriesViewNoList.contains(seriesViewNo)) {
					throw new IllegalArgumentException(seriesViewNo + "场在场记单中重复，请检查修改后重新提交。");
				}
				seriesViewNoList.add(seriesViewNo);
				
				CancelViewDto cancelViewDto = clipViewInfo.getCancelViewInfo();
				List<ShootAuditionDto> auditionList = clipViewInfo.getAuditionList();
				
				//销场信息
				if (cancelViewDto != null) {
					TmpCancelViewInfoModel cancelInfo = new TmpCancelViewInfoModel();
					if (existTmpCancelMap.containsKey(seriesViewNo)) {
						cancelInfo.setId(existTmpCancelMap.get(seriesViewNo));
					} else {
						String cancelInfoId = UUIDUtils.getId();
						cancelInfo.setId(cancelInfoId);
					}
					
					cancelInfo.setCrewId(crewId);
					cancelInfo.setUserId(userId);
					cancelInfo.setNoticeId(noticeId);
					cancelInfo.setSeriesNo(seriesNo);
					cancelInfo.setViewNo(viewNo);
					cancelInfo.setCreateTime(new Date());
					cancelInfo.setShootStatus(cancelViewDto.getShootStatus());
					cancelInfo.setRemark(cancelViewDto.getRemark());
					cancelInfo.setTapNo(cancelViewDto.getTapNo());
					cancelInfo.setHasDealed(false);
					String finishDateStr = cancelViewDto.getFinishDate();
					if (!StringUtils.isBlank(finishDateStr)) {
						Date finishDate = this.sdf2.parse(finishDateStr);
						cancelInfo.setFinishDate(finishDate);
					} else {
						cancelInfo.setFinishDate(null);
					}
					
					if (existTmpCancelMap.containsKey(seriesViewNo)) {
						toUpdateTmpCancelViewList.add(cancelInfo);
					} else {
						toAddTmpCancelViewList.add(cancelInfo);
						existTmpCancelMap.put(seriesViewNo, cancelInfo.getId());
					}
				}
				
				//镜次信息
				for (ShootAuditionDto auditionDto : auditionList) {
					ShootAuditionModel auditionModel = new ShootAuditionModel();
					auditionModel.setAuditionId(UUIDUtils.getId());
					auditionModel.setCrewId(crewId);
					auditionModel.setNoticeId(noticeId);
					auditionModel.setCameraId(cameraId);
					auditionModel.setSeriesNo(seriesNo);
					auditionModel.setViewNo(viewNo);
					auditionModel.setLensNo(auditionDto.getLensNo());
					auditionModel.setAuditionNo(auditionDto.getAuditionNo());
					auditionModel.setSceneType(auditionDto.getSceneType());
					auditionModel.setContent(auditionDto.getContent());
					auditionModel.setTcType(auditionDto.getTcType());
					auditionModel.setTcValue(auditionDto.getTcValue());
					auditionModel.setGrade(auditionDto.getGrade());
					auditionModel.setCreateTime(new Date());
					auditionModel.setUserId(userId);
					auditionModel.setSequence(sequence++);
					
					String mobileTimeStr = auditionDto.getMobileTime();
					if (!StringUtils.isBlank(mobileTimeStr)) {
						Date mobileTime = this.sdf1.parse(mobileTimeStr);
						auditionModel.setMobileTime(mobileTime);
					}
					auditionModel.setServerTime(new Date());
					auditionModel.setComment(auditionDto.getComment());
					
					toAddShootAuditionList.add(auditionModel);
				}
			}
		}
		
		this.addAuditionInfoBatch(toAddShootAuditionList);
		
		this.addTmpCancelInfoBatch(toAddTmpCancelViewList);
		this.updateTmpCancelInfoBatch(toUpdateTmpCancelViewList);
		
		this.addCameraInfoBatch(toSaveCameraList);
	}
	
	/**
	 * 批量添加机位信息
	 * @param cameraInfoList
	 * @throws Exception 
	 */
	public void addCameraInfoBatch(List<CameraInfoModel> cameraInfoList) throws Exception {
		for (CameraInfoModel camera : cameraInfoList) {
			this.cameraInfoDao.add(camera);
		}
	}
	
	/**
	 * 批量添加临时销场信息
	 * @param tmpCancelViewList
	 * @throws Exception 
	 */
	public void addTmpCancelInfoBatch(List<TmpCancelViewInfoModel> tmpCancelViewList) throws Exception {
		for (TmpCancelViewInfoModel tmpCancelModel : tmpCancelViewList) {
			this.tmpCancelViewInfoDao.add(tmpCancelModel);
		}
	}
	
	/**
	 * 批量添加更新销场信息
	 * @param tmpCancelViewList
	 * @throws Exception 
	 */
	public void updateTmpCancelInfoBatch(List<TmpCancelViewInfoModel> tmpCancelViewList) throws Exception {
		for (TmpCancelViewInfoModel tmpCancelModel : tmpCancelViewList) {
			this.tmpCancelViewInfoDao.update(tmpCancelModel, "id");
		}
	}
	
	
	/**
	 * 保存现场信息
	 * @param liveInfo
	 * @throws ParseException, Exception 
	 * @throws Exception 
	 */
	public void saveLiveInfo(String crewId, String noticeId, String userId, ShootLiveDto liveDto) throws ParseException, Exception {
		//删除通告单下所有现场信息
		this.shootLiveDao.deleteByNoticeId(crewId, noticeId);
		
		ShootLiveModel liveModel = new ShootLiveModel();
		String liveId = "";
		if (!StringUtils.isBlank(liveDto.getLiveId())) {
			liveId = liveDto.getLiveId();
		} else {
			liveId = UUIDUtils.getId();
			liveModel.setCreateTime(new Date());
		}
		liveModel.setLiveId(liveId);
		liveModel.setCrewId(crewId);
		liveModel.setNoticeId(noticeId);
		liveModel.setTapNo(liveDto.getTapNo());
		liveModel.setShootLocation(liveDto.getShootLocation());
		liveModel.setShootScene(liveDto.getShootScene());
		liveModel.setUserId(userId);
		
		String startTimeStr = liveDto.getStartTime();
		if (!StringUtils.isBlank(startTimeStr)) {
			Date startTime = this.sdf1.parse(startTimeStr);
			liveModel.setStartTime(startTime);
		}
		String arriveTimeStr = liveDto.getArriveTime();
		if (!StringUtils.isBlank(arriveTimeStr)) {
			Date arriveTime = this.sdf1.parse(arriveTimeStr);
			liveModel.setArriveTime(arriveTime);
		}
		String bootTimeStr = liveDto.getBootTime();
		if (!StringUtils.isBlank(bootTimeStr)) {
			Date bootTime = this.sdf1.parse(bootTimeStr);
			liveModel.setBootTime(bootTime);
		}
		String packupTimeStr = liveDto.getPackupTime();
		if (!StringUtils.isBlank(packupTimeStr)) {
			Date packupTime = this.sdf1.parse(packupTimeStr);
			liveModel.setPackupTime(packupTime);
		}
		String mobileTimeStr = liveDto.getMobileTime();
		if (!StringUtils.isBlank(mobileTimeStr)) {
			Date mobileTime = this.sdf1.parse(mobileTimeStr);
			liveModel.setMobileTime(mobileTime);
		}
		liveModel.setServerTime(new Date());
		
		if (StringUtils.isBlank(liveDto.getLiveId())) {
			this.shootLiveDao.add(liveModel);
		} else {
			this.shootLiveDao.update(liveModel, "liveId");
		}
		
		//现场转场信息
		List<LiveConvertAddDto> convertInfoList = liveDto.getConvertInfoList();
		List<LiveConvertAddModel> toAddConvertInfoList = new ArrayList<LiveConvertAddModel>();
		List<LiveConvertAddModel> toUpdateConvertList = new ArrayList<LiveConvertAddModel>();
		if (convertInfoList == null) {
			convertInfoList = new ArrayList<LiveConvertAddDto>();
		}
		for (LiveConvertAddDto liveConvertDto : convertInfoList) {
			String convertId = liveConvertDto.getConvertId();
			
			LiveConvertAddModel convertModel = new LiveConvertAddModel();
			if (StringUtils.isBlank(convertId)) {
				convertModel.setConvertId(UUIDUtils.getId());
			} else {
				convertModel.setConvertId(convertId);
			}
			convertModel.setCrewId(crewId);
			convertModel.setNoticeId(noticeId);
			convertModel.setUserId(userId);
			convertModel.setCshootLocation(liveConvertDto.getCshootLocation());
			convertModel.setCshootScene(liveConvertDto.getCshootScene());
			String convertTimeStr = liveConvertDto.getConvertTime();
			if (!StringUtils.isBlank(convertTimeStr)) {
				convertModel.setConvertTime(this.sdf1.parse(convertTimeStr));
			}
			String carriveTimeStr = liveConvertDto.getCarriveTime();
			if (!StringUtils.isBlank(carriveTimeStr)) {
				convertModel.setCarriveTime(this.sdf1.parse(carriveTimeStr));
			}
			String cbootTimeStr = liveConvertDto.getCbootTime();
			if (!StringUtils.isBlank(cbootTimeStr)) {
				convertModel.setCbootTime(this.sdf1.parse(cbootTimeStr));
			}
			String cpackupTimeStr = liveConvertDto.getCpackupTime();
			if (!StringUtils.isBlank(cpackupTimeStr)) {
				convertModel.setCpackupTime(this.sdf1.parse(cpackupTimeStr));
			}
			
			if (StringUtils.isBlank(convertId)) {
				toAddConvertInfoList.add(convertModel);
			} else {
				toUpdateConvertList.add(convertModel);
			}
		}
		
		this.addLiveConvertAddBatch(toAddConvertInfoList);
		this.updateLiveConvertAddBatch(toUpdateConvertList);
	}
	
	
	/**
	 * 保存演员出勤信息
	 * @param crewId
	 * @param noticeId
	 * @param userId
	 * @param roleAttendanceDtoList
	 * @return
	 * @throws Exception
	 */
	public List<RoleAttendanceModel> saveRoleAttendance(String crewId, String noticeId, String userId, List<RoleAttendanceDto> roleAttendanceDtoList) throws Exception {
		
		List<RoleAttendanceModel> result = new ArrayList<RoleAttendanceModel>();
		
		this.roleAttendanceDao.deleteByNoticeId(crewId, noticeId);
		
		List<RoleAttendanceModel> toAddAttModelList = new ArrayList<RoleAttendanceModel>();
		List<RoleAttendanceModel> toUpdateAttModelList = new ArrayList<RoleAttendanceModel>();
		
		for (RoleAttendanceDto attenDto : roleAttendanceDtoList) {
			if (StringUtils.isBlank(attenDto.getViewRoleName())) {
				throw new IllegalArgumentException("有角色名称未填写，请检查");
			}
			
			RoleAttendanceModel attendanceModel = new RoleAttendanceModel();
			
			String attendanceId = attenDto.getAttendanceId();
			if (StringUtils.isBlank(attendanceId)) {
				attendanceModel.setAttendanceId(UUIDUtils.getId());
			} else {
				attendanceModel.setAttendanceId(attendanceId);
			}
			attendanceModel.setCrewId(crewId);
			attendanceModel.setNoticeId(noticeId);
			attendanceModel.setUserId(userId);
			attendanceModel.setViewRoleType(attenDto.getRoleType());
			attendanceModel.setViewRoleName(attenDto.getViewRoleName());
			if (attenDto.getRoleNum() != null) {
				attendanceModel.setRoleNum(attenDto.getRoleNum());
			}
			attendanceModel.setActorName(attenDto.getActorName());
			String rarriveTime = attenDto.getRarriveTime();
			if (!StringUtils.isBlank(rarriveTime)) {
				attendanceModel.setRarriveTime(this.sdf1.parse(rarriveTime));
			}
			attendanceModel.setIsLateArrive(attenDto.getIsLateArrive());
			String rpackupTime = attenDto.getRpackupTime();
			if (!StringUtils.isBlank(rpackupTime)) {
				attendanceModel.setRpackupTime(this.sdf1.parse(rpackupTime));
			}
			attendanceModel.setIsLatePackup(attenDto.getIsLatePackup());
			
			if (StringUtils.isBlank(attendanceId)) {
				toAddAttModelList.add(attendanceModel);
			} else {
				toUpdateAttModelList.add(attendanceModel);
			}
			
			result.add(attendanceModel);
		}
		
		this.addAttendanceBatch(toAddAttModelList);
		this.updateAttendanceBatch(toUpdateAttModelList);
		
		return result;
	}
	/**
	 * 批量保存演员出勤信息
	 * @param roleAttenList
	 * @throws Exception 
	 */
	public void addAttendanceBatch(List<RoleAttendanceModel> roleAttenList) throws Exception {
		for (RoleAttendanceModel attenModel : roleAttenList) {
			this.roleAttendanceDao.add(attenModel);
		}
	}
	
	/**
	 * 新增一条演员出勤信息
	 * @param roleAttendanceModel
	 * @throws Exception 
	 */
	public void addOneAttendance(RoleAttendanceModel roleAttendanceModel) throws Exception {
		this.roleAttendanceDao.add(roleAttendanceModel);
	}
	
	/**
	 * 批量更新演员出勤信息
	 * @param roleAttenList
	 * @throws Exception 
	 */
	public void updateAttendanceBatch(List<RoleAttendanceModel> roleAttenList) throws Exception {
		for (RoleAttendanceModel attenModel : roleAttenList) {
			this.roleAttendanceDao.update(attenModel, "attendanceId");
		}
	}
	
	/**
	 * 更新一条演员出勤信息
	 * @param roleAttendanceModel
	 * @throws Exception 
	 */
	public void updateOneAttendance(RoleAttendanceModel roleAttendanceModel) throws Exception {
		this.roleAttendanceDao.updateWithNull(roleAttendanceModel, "attendanceId");
	}
	
	
	/**
	 * 批量保存现场转场信息
	 * @param liveConvertModel
	 * @throws Exception 
	 */
	public void addLiveConvertAddBatch(List<LiveConvertAddModel> liveConvertModelList) throws Exception {
		for (LiveConvertAddModel liveConvertModel : liveConvertModelList) {
			this.liveConvertAddDao.add(liveConvertModel);
		}
	}
	
	/**
	 * 保存单个转场信息
	 * @param liveConvertAddModel
	 * @throws Exception 
	 */
	public void addOneLiveConvertAdd(LiveConvertAddModel liveConvertAddModel) throws Exception {
		this.liveConvertAddDao.add(liveConvertAddModel);
	}
	
	/**
	 * 批量更新现场转场信息
	 * @param liveConvertModel
	 * @throws Exception 
	 */
	public void updateLiveConvertAddBatch(List<LiveConvertAddModel> liveConvertModelList) throws Exception {
		for (LiveConvertAddModel liveConvertModel : liveConvertModelList) {
			this.liveConvertAddDao.update(liveConvertModel, "convertId");
		}
	}
	
	/**
	 * 更新单个专场信息
	 * @param liveConvertAddModel
	 * @throws Exception 
	 */
	public void updateOneConvertAdd(LiveConvertAddModel liveConvertAddModel) throws Exception {
		this.liveConvertAddDao.update(liveConvertAddModel, "convertId");
	}
	
	/**
	 * 批量保存镜次信息
	 * @param auditionModelList
	 * @throws Exception 
	 */
	public void addAuditionInfoBatch(List<ShootAuditionModel> auditionModelList) throws Exception {
		for (ShootAuditionModel auditionModel : auditionModelList) {
			this.shootAuditionDao.add(auditionModel);
		}
	}
	
	/**
	 * 批量更新镜次信息
	 * @param auditionModelList
	 * @throws Exception 
	 */
	public void updateAuditionInfoBatch(List<ShootAuditionModel> auditionModelList) throws Exception {
		for (ShootAuditionModel auditionModel : auditionModelList) {
			this.shootAuditionDao.update(auditionModel, "auditionId");
		}
	}
	
	
	/**
	 * 根据通告单ID查询指定剧组下的临时销场信息
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	public List<TmpCancelViewInfoModel> queryTmpCancelInfoByCrewNoticeId(String crewId, String noticeId) {
		return this.tmpCancelViewInfoDao.queryByCrewNoticeId(crewId, noticeId);
	}
	
	/**
	 * 查询通告单下指定场景的临时销场信息
	 * @param crewId
	 * @param noticeId
	 * @param viewIds
	 * @return
	 */
	public List<TmpCancelViewInfoModel> queryTmpCancelInfoByViewIds(String crewId, String noticeId) {
		return this.tmpCancelViewInfoDao.queryByViewIds(crewId, noticeId);
	}
	
	/**
	 * 根据多个ID查询临时销场信息
	 * @param ids
	 * @return
	 */
	public List<TmpCancelViewInfoModel> queryTmpCancelInfoByIds(String ids) {
		return this.tmpCancelViewInfoDao.queryByIds(ids);
	}
	
	/**
	 * 根据多个集-场号查询临时销场信息
	 * @param ids
	 * @return
	 */
	public List<TmpCancelViewInfoModel> queryTmpCancelInfoBySeriesViewNos(String crewId, String noticeId, String seriesViewNos) {
		return this.tmpCancelViewInfoDao.queryBySeiresViewNos(crewId, noticeId, seriesViewNos);
	}
	
	/**
	 * 批量删除临时销场数据
	 * @param ids
	 */
	public void deleteTmpCancelInfoByIds(String ids) {
		this.tmpCancelViewInfoDao.deleteByIds(ids);
	}
	
	/**
	 * 批量做确认销场处理
	 * @param seriesViewNos 集-场号
	 */
	public void makeSureCancelBySeriesViewNos(String crewId, String noticeId, String seriesViewNos) {
		this.tmpCancelViewInfoDao.makeSureBySeriesViewNos(crewId, noticeId, seriesViewNos);
	}
	
	/**
	 * 批量做确认销场处理
	 * @param ids
	 */
	public void makeSureByIds(String ids) {
		this.tmpCancelViewInfoDao.makeSureByIds(ids);
	}
	
	/**
	 * 查询剧组下所有的镜次信息
	 * 带有气氛、内外、临时销场信息
	 * @param crewId
	 * @param noticeId
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> queryAudiInfoWithTmpCancel(String crewId, String noticeId, String userId) {
		return this.shootAuditionDao.queryAudiInfoWithTmpCancel(crewId, noticeId, userId);
	}
	
	/**
	 * 查询指定通告单下部门评分
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	public List<Map<String, Object>> queryNoticeDepartScore(String crewId, String noticeId, String userId) {
		return this.departmentEvaluateDao.queryNoticeDepartScore(crewId, noticeId, userId);
	}
	
	/**
	 * 查询指定人员对指定通告单的部门评分信息
	 * @param crewId
	 * @param noticeId
	 * @param userId
	 * @return
	 */
	public List<DepartmentEvaluateModel> queryDepartEvaluateByUserNotice(String crewId, String noticeId, String userId) {
		return this.departmentEvaluateDao.queryByNoticeUser(crewId, noticeId, userId);
	}
	
	/**
	 * 批量添加部门评分信息
	 * @param departEvaluateList
	 * @throws Exception 
	 */
	public void addManyDepartEvaluate(List<DepartmentEvaluateModel> departEvaluateList) throws Exception {
		for (DepartmentEvaluateModel departEvaluateModel : departEvaluateList) {
			this.departmentEvaluateDao.add(departEvaluateModel);
		}
	}

	/**
	 * 批量修改部门评分信息
	 * @param departEvaluateList
	 * @throws Exception 
	 */
	public void updateManyDepartEvaluate(List<DepartmentEvaluateModel> departEvaluateList) throws Exception {
		for (DepartmentEvaluateModel departEvaluateModel : departEvaluateList) {
			this.departmentEvaluateDao.update(departEvaluateModel, "id");
		}
	}
	
	/**
	 * 根据ID删除通告单现场信息转场信息
	 * @param convertIds
	 */
	public void deleteConvertAddByIds(String convertIds) {
		this.liveConvertAddDao.deleteByIds(convertIds);
	}
	
	/**
	 * 根据ID批量删除演员出勤信息
	 * @param crewId
	 * @param noticeId
	 * @param attendanceIds
	 */
	public void deleteRoleAttendanceByIds(String crewId, String noticeId, String attendanceIds) {
		this.roleAttendanceDao.deleteByIds(crewId, noticeId, attendanceIds);
	}
}
