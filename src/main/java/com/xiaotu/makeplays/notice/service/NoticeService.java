package com.xiaotu.makeplays.notice.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.cutview.dao.CutViewInfoDao;
import com.xiaotu.makeplays.goods.dao.GoodsInfoDao;
import com.xiaotu.makeplays.message.model.MessageInfoModel;
import com.xiaotu.makeplays.message.model.constants.MessageInfoStatus;
import com.xiaotu.makeplays.message.model.constants.MessageType;
import com.xiaotu.makeplays.message.service.MessageInfoService;
import com.xiaotu.makeplays.mobile.push.umeng.model.android.AndroidPushMsg;
import com.xiaotu.makeplays.mobile.push.umeng.model.ios.IOSPushMsg;
import com.xiaotu.makeplays.mobile.push.umeng.service.android.UmengAndroidPushService;
import com.xiaotu.makeplays.mobile.push.umeng.service.ios.UmengIOSPushService;
import com.xiaotu.makeplays.notice.dao.ConvertAddressDao;
import com.xiaotu.makeplays.notice.dao.NoticeInfoDao;
import com.xiaotu.makeplays.notice.dao.NoticePictureDao;
import com.xiaotu.makeplays.notice.dao.NoticePushFedBackDao;
import com.xiaotu.makeplays.notice.dao.NoticeRoleTimeDao;
import com.xiaotu.makeplays.notice.dao.NoticeTimeDao;
import com.xiaotu.makeplays.notice.dao.NoticeUserMapDao;
import com.xiaotu.makeplays.notice.dao.ViewNoticeMapDao;
import com.xiaotu.makeplays.notice.dao.clip.TmpCancelViewInfoDao;
import com.xiaotu.makeplays.notice.model.ConvertAddressModel;
import com.xiaotu.makeplays.notice.model.NoticeInfoModel;
import com.xiaotu.makeplays.notice.model.NoticePictureModel;
import com.xiaotu.makeplays.notice.model.NoticePushFedBackModel;
import com.xiaotu.makeplays.notice.model.NoticeRoleTimeModel;
import com.xiaotu.makeplays.notice.model.NoticeTimeModel;
import com.xiaotu.makeplays.notice.model.NoticeUserMapModel;
import com.xiaotu.makeplays.notice.model.ViewNoticeMapModel;
import com.xiaotu.makeplays.notice.model.clip.TmpCancelViewInfoModel;
import com.xiaotu.makeplays.notice.model.constants.NoticeCanceledStatus;
import com.xiaotu.makeplays.user.dao.UserInfoDao;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.DateUtils;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.view.dao.ViewInfoDao;
import com.xiaotu.makeplays.view.model.ViewInfoModel;
import com.xiaotu.makeplays.view.model.constants.ShootStatus;

@Service
public class NoticeService {

	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
	
	@Autowired
	private NoticeInfoDao noticeInfoDao;
	
	@Autowired
	private ViewNoticeMapDao viewNoticeMapDao;
	
	@Autowired
	private NoticeTimeDao noticeTimeDao;
	
	@Autowired
	private NoticeRoleTimeDao noticeRoleTimeDao;
	
	@Autowired
	private ViewInfoDao viewInfoDao;
	
	@Autowired
	private NoticeUserMapDao noticeUserMapDao;
	
	@Autowired
	private ConvertAddressDao convertAddressDao;
	
	@Autowired
	private NoticePictureDao noticePictureDao;
	
	@Autowired
	private NoticePushFedBackDao noticePushFedBackDao;
	
	@Autowired
	private UserInfoDao userInfoDao;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UmengIOSPushService umengIOSPushService;
	
	@Autowired
	private ClipService clipSerivce;
	
	@Autowired
	private TmpCancelViewInfoDao tmpCancelViewInfoDao;
	
	@Autowired
	private UmengAndroidPushService umengAndroidPushService;
	
	@Autowired
	private MessageInfoService messageInfoService;
	
	@Autowired
	private GoodsInfoDao goodsInfoDao;
	
	@Autowired
	private CrewInfoService crewInfoService;
	
	@Autowired
	private CutViewInfoDao cutViewDao;
	
	/**
	 * 保存通告单此接口会保存通告单与场景的关联关系
	 * @param notice
	 * @param viewIds
	 * @throws Exception
	 */
	public void saveNotice(NoticeInfoModel notice,String viewIds) throws Exception{
		
		if(StringUtils.isBlank(notice.getNoticeId())){
			notice.setNoticeId(UUIDUtils.getId());
			noticeInfoDao.add(notice);
		}else{
			noticeInfoDao.update(notice,"noticeId");
		}
		
		if(StringUtils.isBlank(viewIds)){
			return ;
		}
		
		String [] viewIdArray = viewIds.split(",");
		int i=0;
		for(String viewId:viewIdArray){
			saveViewNoticeMap(notice.getNoticeId(),notice.getCrewId(), viewId, i);
			i++;
		}
	}
	
	/**
	 * 保存通告单此接口只会保存通告单信息
	 * @param notice
	 * @param viewIds
	 * @throws Exception
	 */
	public String saveNotice(NoticeInfoModel notice) throws Exception{
		String noticeId = "";
		if(StringUtils.isBlank(notice.getNoticeId())){
			noticeId = UUIDUtils.getId();
			notice.setNoticeId(noticeId);
			noticeInfoDao.add(notice);
		}else{
			noticeId = notice.getNoticeId();
			noticeInfoDao.update(notice, "noticeId");
		}
		return noticeId;
		
	}
	
	/**
	 * 保存场景与通告单关联关系 (若是不确定sequence，则传入null，系统自动分配一个sequence)
	 * @param noticeId 通告单的id
	 * @param crewId 剧组的id
	 * @param viewId 场景id
	 * @param sequence 序列号
	 * @throws Exception
	 */
	public void saveViewNoticeMap(String noticeId,String crewId,String viewId,Integer sequence) throws Exception{
		
		ViewNoticeMapModel viewNotice = new ViewNoticeMapModel();
		viewNotice.setMapId(UUIDUtils.getId());
		viewNotice.setNoticeId(noticeId);
		viewNotice.setCrewId(crewId);
		
		if(null == sequence){
			//根据通告单id和场景id取出最大序列号,确保序列号不会重复
			sequence = viewNoticeMapDao.getNoticeViewLastSequence(noticeId,crewId)+1;
			viewNotice.setSequence(sequence);
		}else{
			viewNotice.setSequence(sequence);
		}
		
		viewNotice.setViewId(viewId);
		viewNoticeMapDao.add(viewNotice);
		
		//根据id查询出通告单信息
		NoticeInfoModel model = noticeInfoDao.queryNoticeInfoModelById(noticeId);
		model.setCanceledStatus(NoticeCanceledStatus.Uncancel.getValue());
		this.noticeInfoDao.update(model, "noticeId");
	}
	
	
	/**
	 * 删除场次通告单关联
	 * @param notice
	 * @param viewId
	 * @param sequence
	 * @throws Exception
	 */
	public void deleteNoticeView(String noticeId,String crewId,String viewIds) throws Exception{
		if(StringUtils.isNotBlank(viewIds)&&StringUtils.isNotBlank(noticeId)&&StringUtils.isNotBlank(crewId)){
			String []viewIdArray=viewIds.split(",");
			for(String viewId:viewIdArray){
				viewNoticeMapDao.deleteViewMoticeMap(noticeId,crewId,viewId);
			}
		}
		
		//查询出当前通告单下所有的场景
		List<Map<String, Object>> viewList =  viewInfoDao.queryNoticeViewList(crewId, noticeId);
		//检查通告单是否销场
		if(checkNoticeViewStatus(noticeId)==0 && viewList.size()>0){
			//销场
			NoticeInfoModel noticeModel = new NoticeInfoModel();
			noticeModel.setNoticeId(noticeId);
			noticeModel=noticeInfoDao.getEntityById(noticeModel, "noticeId");
			noticeModel.setCanceledStatus(NoticeCanceledStatus.Canceled.getValue());
			noticeModel.setUpdateTime(null);
			noticeInfoDao.update(noticeModel, "noticeId");
		}
	}
	
	/**
	 * 获取场次已存在的通告单
	 * @param notice
	 * @param viewIds
	 * @return
	 */
	public List<Map<String, Object>> validatorNoticeSave(String viewIds,String noticeId){
		
		List<Map<String, Object>> validatorList = noticeInfoDao.queryNoticeView(viewIds,noticeId);
		return validatorList;
	}
	
	
	/**
	 * 查询通告单列表
	 * @param crewId
	 * @return
	 * @throws ParseException 
	 */
	public List<Map<String, Object>> queryNoticeList(String crewId,String groupId, Page page, Boolean forSimple, Map<String, Object> conditionMap) throws ParseException{
		return noticeInfoDao.queryNoticeByCrewId(crewId,groupId, page, forSimple, conditionMap);
	}
	
	/**
	 * 查询通告单列表视图数据
	 * @param crewId
	 * @param page
	 * @param conditionMap
	 * @return
	 */
	public List<Map<String, Object>> queryNoticeListTableData(String crewId,Page page, Map<String, Object> conditionMap){
		return this.noticeInfoDao.queryNoticeListTableData(crewId, page, conditionMap);
	}
	
	/**
	 * 正序查询通告单列表
	 * @param crewId
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> queryAscNoticeList(String crewId, Page page){
		return this.noticeInfoDao.queryAscNoticeList(crewId, page);
	}
	/**
	 * 根据通告单ID查询单条通告单信息
	 * 该方法在查询通告单基本信息的同时，还会查询出场数、页数、拍摄地点等额外信息
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	public Map<String, Object> queryOneFullInfoByNoticeId(String crewId, String noticeId) {
		return this.noticeInfoDao.queryOneFullInfoByNoticeId(crewId, noticeId);
	}
	
	/**
	 * 查询通告单列表(单条通告单)
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryNoticeList(String crewId,String groupId,String noticeId){
		
		return noticeInfoDao.queryNoticeByCrewId(crewId,groupId,noticeId);
	}
	
	/**
	 * 查询通告单
	 * @param noticeId
	 * @return
	 */
	public NoticeInfoModel getNotice(String noticeId){
		return noticeInfoDao.queryNoticeInfoModelById(noticeId);
	}
	
	
	/**
	 * 查询第一个通告单
	 * @param crewId
	 * @return
	 */
	public NoticeInfoModel getFirstNotice(String crewId){
		return noticeInfoDao.queryFirstNoticeInfoModelByCrewId(crewId);
	}
	
	/**
	 * 生成通告单信息
	 * @param request
	 * @param noticeId	通告单ID
	 * @param breakfastTime	早餐时间
	 * @param departureTime	出发时间
	 * @param note	特别提示
	 * @param contact	联系人
	 * @param remark	备注
	 * @param version	版本信息
	 * @param groupDirector	组导演
	 * @param shootGuide	摄影
	 * @param insideAdvert	商植
	 * @param roleMakeup	化妆
	 * @param roleArriveTime	演员出发时间
	 * @param roleGiveMakeupTime	演员交妆时间
	 * @param convertRemark
	 * @param roleConvertRemark
	 * @throws Exception
	 */
	public void saveNoticeInfo(String crewId, String noticeId,
			String breakfastTime,Date noticeDate,String groupId, String departureTime, String note, String roleInfo,
			String contact, String remark, String version,
			String groupDirector, String shootGuide, String insideAdvert,
			String roleMakeup, String roleArriveTime,
			String roleGiveMakeupTime, String convertRemark,
			String roleConvertRemark, String shootLocationInfos, String weatherInfo, String imgStorePath,
			String smallImgStorePath, String concatInfoList, String noticeName, String changePublished) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		//保存通告单信息
		Date nowDate = new Date();
		NoticeInfoModel model = new NoticeInfoModel();
		model.setNoticeId(noticeId);
		model.setNoticeDate(noticeDate);
		model.setGroupId(groupId);
		model.setUpdateTime(nowDate);
		saveNotice(model);
		

		//根据id，查询通告单时间信息
		NoticeTimeModel oldNoticeTime = this.noticeTimeDao.queryNoticeTimeByNoticeId(noticeId);
		String oldVersion = "";
		if (oldNoticeTime != null) {
			oldVersion = sdf.format(oldNoticeTime.getUpdateTime());
		}
		
		//保存通告单生成信息
		NoticeTimeModel noticeTime = this.saveOrUpdateNoticeTime(crewId,
				noticeId, breakfastTime, departureTime, note, roleInfo, remark,
				version, groupDirector, shootGuide, insideAdvert,
				roleConvertRemark, shootLocationInfos, weatherInfo, imgStorePath, smallImgStorePath, concatInfoList);
		NoticeTimeModel newNoticeTime = this.noticeTimeDao.queryNoticeTimeByNoticeId(noticeId);
		//根据是否撤销改变通告单图片的版本及反馈列表的版本
		if (StringUtils.isNotBlank(changePublished)) {
			//更新反馈列表的版本号
			String newVersion = sdf.format(newNoticeTime.getUpdateTime());
			this.noticePushFedBackDao.updateFacdbackVersion(noticeId, newVersion, oldVersion);
			
			//更新通告单图片的版本号
			this.noticePictureDao.updatePictureVersion(noticeId, newVersion, oldVersion);
			
		}
		
		//设置通告单状态为未发布
		NoticeInfoModel noticeInfo = this.queryNoticeInfoModelById(noticeId);
		if (StringUtils.isBlank(changePublished)) {
			noticeInfo.setPublished(false);
			noticeInfo.setPublishTime(null);
			this.updateNoticeInfo(noticeInfo);
		}
		
		//保存通告单中演员时间信息
		this.saveOrUpdateNoticeRoleTime(noticeId, roleMakeup, roleArriveTime, roleGiveMakeupTime);
		
		
		//保存通告单时间和用户关联关系
		this.saveNoticeUserMap(crewId, noticeTime.getNoticeTimeId(), contact);
		
		
		//保存转场信息
		this.saveOrUpdateConvertAddress(crewId, noticeId, convertRemark);
	}
	
	
	/**
	 * 生成通告单信息
	 * @param noticeId	通告单ID
	 * @param breakfastTime	早餐时间
	 * @param departureTime	出发时间
	 * @param note	特别提示
	 * @param remark	备注
	 * @param version	版本信息
	 * @param groupDirector	组导演
	 * @param shootGuide	摄影
	 * @param insideAdvert	商植
	 * @param roleConvertRemark
	 * @throws Exception
	 */
	public NoticeTimeModel saveOrUpdateNoticeTime(String crewId, String noticeId,
			String breakfastTime, String departureTime, String note, String roleInfo,
			String remark, String version,
			String groupDirector, String shootGuide, String insideAdvert,
			String roleConvertRemark, String shootLocationInfos, String weatherInfo, String imgStorePath,
			String smallImgStorePath, String concatInfoList) throws Exception {
	
		//保存通告单生成信息
		NoticeTimeModel noticeTime= noticeTimeDao.queryNoticeTimeByNoticeId(noticeId);
		if (noticeTime == null) {
			noticeTime = new NoticeTimeModel();
		}
		Date currentTime = new Date();
		noticeTime.setBreakfastTime(breakfastTime);
		noticeTime.setDepartureTime(departureTime);
		noticeTime.setNote(note);
		noticeTime.setCrewId(crewId);
		noticeTime.setNoticeId(noticeId);
		noticeTime.setRemark(remark);
		noticeTime.setVersion(version);
		noticeTime.setGroupDirector(groupDirector);
		noticeTime.setShootGuide(shootGuide);
		noticeTime.setInsideAdvert(insideAdvert);
		noticeTime.setRoleConvertRemark(roleConvertRemark);
		noticeTime.setRoleInfo(roleInfo);
		noticeTime.setShootLocationInfos(shootLocationInfos);
		noticeTime.setWeatherInfo(weatherInfo);
		noticeTime.setNoticeContact(concatInfoList);
//		noticeTime.setPictureUrl(imgStorePath);
//		noticeTime.setSmallPicurl(smallImgStorePath);
		noticeTime.setUpdateTime(currentTime);
		if (StringUtils.isBlank(noticeTime.getNoticeTimeId())) {
			noticeTime.setNoticeTimeId(UUIDUtils.getId());
			noticeTime.setCreateTime(new Date());
			this.noticeTimeDao.add(noticeTime);
		} else {
			this.noticeTimeDao.updateWithNull(noticeTime, "noticeTimeId");
		}
		
		return noticeTime;
	}
	
	
	/**
	 * 保存或更新演员通告单时间
	 * 传递的参数格式都为“演员ID|值，演员ID|值，演员ID|值，演员ID|值”
	 * @param roleMakeup	演员化妆
	 * @param roleArriveTime	演员到场时间
	 * @param roleGiveMakeupTime	演员交妆时间
	 * @throws Exception
	 */
	public List<NoticeRoleTimeModel> saveOrUpdateNoticeRoleTime(String noticeId, String roleMakeup, String roleArriveTime, String roleGiveMakeupTime) throws Exception{
		List<NoticeRoleTimeModel> noticeRoleTimeList = new ArrayList<NoticeRoleTimeModel>();
		
		//首先删除该通告单下的所有演员时间记录
		this.noticeRoleTimeDao.deleteByNoticeId(noticeId);
		
		String[] roleMakeupArray = roleMakeup.split(",");
		String[] roleArriveTimeArray = roleArriveTime.split(",");
		String[] roleGiveMakeupTimeArray = roleGiveMakeupTime.split(",");
		
		for(int i =0;i<roleMakeupArray.length;i++){
			
			String roleMakeupIdAndTime = roleMakeupArray[i];
			String roleArriveTimeIdAndTime = roleArriveTimeArray[i];
			String roleGiveMakeupTimeIdAndTime = roleGiveMakeupTimeArray[i];
			
			
			String[] roleMakeupIdAndTimeArray = roleMakeupIdAndTime.split("\\|");
			String[] roleArriveTimeIdAndTimeArray = roleArriveTimeIdAndTime.split("\\|");
			String[] roleGiveMakeupTimeIdAndTimeArray = roleGiveMakeupTimeIdAndTime.split("\\|");
			
			if (roleMakeupIdAndTimeArray.length > 1 || roleArriveTimeIdAndTimeArray.length > 1 || roleGiveMakeupTimeIdAndTimeArray.length > 1) {
				NoticeRoleTimeModel noticeRoleTimeModel = new NoticeRoleTimeModel();
				
				if(roleMakeupIdAndTimeArray.length > 1){
					noticeRoleTimeModel.setMakeup(roleMakeupIdAndTimeArray[1]);
				}
				if(roleArriveTimeIdAndTimeArray.length > 1){
					noticeRoleTimeModel.setArriveTime(roleArriveTimeIdAndTimeArray[1]);
				}
				if(roleGiveMakeupTimeIdAndTimeArray.length > 1){
					noticeRoleTimeModel.setGiveMakeupTime(roleGiveMakeupTimeIdAndTimeArray[1]);
				}
				
				noticeRoleTimeModel.setNoticeId(noticeId);
				
				if(roleMakeupIdAndTimeArray.length > 1){
					noticeRoleTimeModel.setViewRoleId(roleMakeupIdAndTimeArray[0].split("_")[1]);
				}
				
				if (StringUtils.isBlank(noticeRoleTimeModel.getNoticeRoleTimeId())) {
					noticeRoleTimeModel.setNoticeRoleTimeId(UUIDUtils.getId());
					this.noticeRoleTimeDao.add(noticeRoleTimeModel);
				} else {
					this.noticeRoleTimeDao.update(noticeRoleTimeModel, "noticeRoleTimeId");
				}
				
				noticeRoleTimeList.add(noticeRoleTimeModel);
			}
		}
		
		return noticeRoleTimeList;
	}
	
	/**
	 * 保存通告单时间和用户的关联关系
	 * @param noticeTimeId	通告单时间ID
	 * @param contact	用户ID，多个值以都好隔开
	 * @return
	 * @throws Exception 
	 */
	public List<NoticeUserMapModel> saveNoticeUserMap(String crewId, String noticeTimeId, String contact) throws Exception {
		List<NoticeUserMapModel> addNoticeUserMapList = new ArrayList<NoticeUserMapModel>();
		
		//首先删除通告单下所有用户的关联关系
		this.noticeUserMapDao.deleteByNoticeTimeId(noticeTimeId);
		
		String[] contactArr = contact.split(",");
		for (String userId : contactArr) {
				NoticeUserMapModel noticeUserMap = new NoticeUserMapModel();
				noticeUserMap.setMapId(UUIDUtils.getId());
				noticeUserMap.setCrewId(crewId);
				noticeUserMap.setNoticeTimeId(noticeTimeId);
				noticeUserMap.setUserId(userId);
				
				this.noticeUserMapDao.add(noticeUserMap);
				
				addNoticeUserMapList.add(noticeUserMap);
		}
		
		return addNoticeUserMapList;
	}
	
	/**
	 * 保存通告单转场信息
	 * @return
	 * @throws Exception 
	 */
	public List<ConvertAddressModel> saveOrUpdateConvertAddress(String crewId, String noticeId, String convertRemark) throws Exception {
		List<ConvertAddressModel> convertAddList = new ArrayList<ConvertAddressModel>();
		
		//首先删除通告单下所有的转场信息
		this.convertAddressDao.deleteByNoticeId(noticeId);
		
		String[] convertRemarkArray = convertRemark.split("\\|");
		for (String convertInfo : convertRemarkArray) {
			String[] convertInfoArray = convertInfo.split("_");
			if (convertInfoArray.length >= 3) {
				ConvertAddressModel convertAddress = new ConvertAddressModel();
				convertAddress.setConvertId(UUIDUtils.getId());
				convertAddress.setCrewId(crewId);
				convertAddress.setNoticeId(noticeId);
				convertAddress.setAfterLocationId(convertInfoArray[0]);
				convertAddress.setAfterViewIds(convertInfoArray[1]);
				convertAddress.setRemark(convertInfoArray[2]);
				
				this.convertAddressDao.add(convertAddress);
				
				convertAddList.add(convertAddress);
			}
		}
		
		return convertAddList;
	}
	
	/**
	 * 查询通告单时间，查询最后一次通告单
	 * @param viewRoleTime
	 * @throws Exception 
	 */
	public NoticeTimeModel queryLastNoticeTime(String crewId) throws Exception{
		return noticeTimeDao.queryLastNoticeTime(crewId);
	}
	
	/**
	 * 根据通告单的id查询通告单的详情
	 * @param noticeId
	 * @return
	 * @throws Exception
	 */
	public NoticeTimeModel queryNoticeTimeByNoticeId(String noticeId) throws Exception{
		return this.noticeTimeDao.queryNoticeTimeByNoticeId(noticeId);
	}
	
	/**
	 * 查询上一个同组通告单时间
	 * @param crewId
	 * @param groupId
	 * @return
	 * @throws Exception 
	 */
	public NoticeTimeModel queryLastGroupNoticeTime(String crewId, String groupId) throws Exception {
		return this.noticeTimeDao.queryLastGroupNoticeTime(crewId, groupId);
	}


	/**
	 * 保存场景拍摄状态
	 * @param noticeId
	 * @param viewIds
	 * @param shootStatus
	 * @param remark
	 * @param tapNo
	 * @throws Exception 
	 * @throws NoSuchMethodException 
	 */
	public void saveNoticeShootStatus(String noticeId,String viewIds,Integer shootStatus,String remark,String tapNo, String shootDate) throws NoSuchMethodException, Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ViewNoticeMapModel viewNoticeMap = new ViewNoticeMapModel();
		
		viewNoticeMap.setNoticeId(noticeId);
		viewNoticeMap.setShootStatus(shootStatus);
		viewNoticeMap.setStatusRemark(remark);
		viewNoticeMap.setTapNo(tapNo);
		viewNoticeMapDao.updateViewMoticeMapStatus(viewNoticeMap, viewIds);
		
		int status = shootStatus.intValue();
		
		if(status==1||status==4){	//部分完成
			viewInfoDao.updateViewShootStatus(viewIds, new Integer(1), remark, tapNo, null, shootDate);
		}else if(status==2||status==5){	//完成
			viewInfoDao.updateViewShootStatus(viewIds, new Integer(2), remark, tapNo, noticeId, shootDate);
		}else if(status == 3){		//删戏
			viewInfoDao.updateViewShootStatus(viewIds, new Integer(3), remark, tapNo, noticeId, null);
			String[] viewIdArr = viewIds.split(",");
			if (viewIdArr != null ) {
				for (String viewId : viewIdArr) {
					//需要删除该场的剪辑信息
					this.cutViewDao.deleteCutViewInfoByViewId(viewId, noticeId);
				}
			}
		}else if(status == 0) {	//甩戏
			//需要删除该场的剪辑信息
			String[] viewIdArr = viewIds.split(",");
			if (viewIdArr != null ) {
				for (String viewId : viewIdArr) {
					//需要删除该场的剪辑信息
					this.cutViewDao.deleteCutViewInfoByViewId(viewId, noticeId);
				}
			}
			//将场景状态设置为未拍摄之前，需要判断当前场景在其它通告单中的状态如果有状态则需要重置为其它通告单中的状态，没有在设置为未拍摄状态
			//根据场景id查询出通告单与场景的关联关系
			List<Map<String, Object>> viewNoticeList = this.viewNoticeMapDao.queryViewNoticeMapInfoByViewIds(viewIds);
			if (null == viewNoticeList || viewNoticeList.size() == 0) {
				viewInfoDao.updateViewShootStatus(viewIds, new Integer(0), remark, tapNo, null, null);
			}else {
				boolean flag = true;
				//只有完成状态，才重置场景状态为完成状态
				for (Map<String, Object> map : viewNoticeList) {
					//取出场景在通告单中的状态
					int mapStatus = map.get("shootStatus") == null ? 0 : Integer.parseInt(map.get("shootStatus")+"");
					if (mapStatus == 2 || mapStatus == 5) { //完成和加戏部分完成
						//取出拍摄时间
						String mapShootDate = map.get("shotDate")== null? null : sdf.format((Date) map.get("shotDate"));
						//备注
						String mapRemark = (String) map.get("remark");
						//拍摄带号
						String mapTapNo = (String) map.get("tapNo");
						
						flag = false;
						
						//将场景状态重置为该场景在其它通告单中的状态
						viewInfoDao.updateViewShootStatus(viewIds, new Integer(2), mapRemark, mapTapNo, noticeId, mapShootDate);
						break;
					}
				}
				
				//如果没有已完成状态的，则重置为未完成
				if (flag) {
					viewInfoDao.updateViewShootStatus(viewIds, new Integer(0), remark, tapNo, null, null);
				}
			}
			
		}
		
		//检查通告单是否销场
		if(checkNoticeViewStatus(noticeId)==0){
			//销场
			NoticeInfoModel noticeModel = new NoticeInfoModel();
			noticeModel.setNoticeId(noticeId);
			noticeModel=noticeInfoDao.getEntityById(noticeModel, "noticeId");
			noticeModel.setCanceledStatus(NoticeCanceledStatus.Canceled.getValue());
			noticeModel.setUpdateTime(null);
			noticeInfoDao.update(noticeModel, "noticeId");
		}
	}
	
	/**
	 * 检查是否销场
	 * @return
	 */
	public int checkNoticeViewStatus(String noticeId){
		
		return viewNoticeMapDao.queryNoticeViewStatusCount(noticeId);
	}
	
	/**
	 * 更新通告单排列顺序
	 */
	public void updateNoticeViewSort(String noticeId,String viewIds){
		
		String[] viewIdArray =  viewIds.split(",");
		
		for(int i=0;i<viewIdArray.length;i++){
			viewNoticeMapDao.updateNoticeViewSequence(noticeId, viewIdArray[i], i);
		}
		
	}
	
	
	/**
	 * 删除一条通告单
	 * 该方法会顺带删除和该通告单相关的信息
	 * @param crewId	剧组ID
	 * @param noticeId	通告单ID
	 * @throws Exception 
	 */
	public void deleteOneNotice(String crewId, String noticeId) throws Exception {
		//删除通告单信息
		this.noticeInfoDao.deleteOne(noticeId, "noticeId", NoticeInfoModel.TABLE_NAME);
		
		//删除通告单时，同时删除时间信息
		this.noticeTimeDao.deleteNoticeTimeByNoticeId(noticeId);
		
		//删除场景和通告单的关联关系
		this.viewNoticeMapDao.deleteViewMoticeMap(noticeId, crewId);
		
		//清空场景信息中记录此通告单的信息
		this.viewInfoDao.deleteViewNoticeInfo(noticeId);
		
		//删除该通告单的剪辑信息
		this.cutViewDao.deleteCutViewInfoByNoticeId(noticeId);
	}
	
	/**
	 * 根据多个条件查询通告单信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<NoticeInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.noticeInfoDao.queryManyByMutiCondition(conditionMap, page);
	}
	
	/**
	 * 查询通告单信息
	 * 该方法还会查询出通告单时间信息
	 * @param crewId
	 * @return
	 * @throws Exception 
	 */
	public List<Map<String, Object>> queryNoticeInfoWithNoticeTime (String crewId, String userId, Page page, boolean isForClip) throws Exception {
		boolean isKefu = this.userService.checkIsKefu(userId);
		return this.noticeInfoDao.queryNoticeInfoWithNoticeTime(crewId, userId, page, isKefu, isForClip);
	}
	
	/**
	 * 查询通告单信息
	 * 该方法还会查询出通告单时间信息
	 * @param crewId
	 * @return
	 * @throws Exception 
	 */
	public List<Map<String, Object>> queryNoticeInfoWithSomeColumns (String crewId, String userId, Page page, boolean isForClip) throws Exception {
		boolean isKefu = this.userService.checkIsKefu(userId);
		return this.noticeInfoDao.queryNoticeInfoWithSomeColumns(crewId, userId, page, isKefu, isForClip);
	}
	
	/**
	 * 查询通告单下所有场景的集场列表信息
	 * @param noticeId
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryNoticeSeriesViewNo(String noticeId, String crewId) {
		return this.noticeInfoDao.queryNoticeSeriesViewNo(noticeId, crewId);
	}
	
	/**
	 * 更新通告单时间信息
	 * @param noticeTime
	 * @throws Exception
	 */
	public void updateNoticeTime (NoticeTimeModel noticeTime) throws Exception {
		this.noticeInfoDao.update(noticeTime, "noticeTimeId");
	}
	
	/**
	 * 查询通告单角色最早的时间安排
	 * @param noticeId
	 * @param roleId
	 * @param roleIdList 角色列表
	 * @return
	 * @throws Exception
	 */
	public NoticeRoleTimeModel queryNoticeRoleTimeByNoticeIdAndRoleId(String noticeId, List<String> roleIdList) throws Exception{
		return this.noticeRoleTimeDao.queryNoticeRoleTimeByNoticeIdAndRoleId(noticeId, roleIdList);
	}
	
	/**
	 * 根据ID查找通告单信息
	 * @param noticeId
	 * @return
	 */
	public NoticeInfoModel queryNoticeInfoModelById(String noticeId){
		return this.noticeInfoDao.queryNoticeInfoModelById(noticeId);
	}
	
	/**
	 * 更新通告单信息
	 * @param noticeInfoModel
	 * @throws Exception
	 */
	public void updateNoticeInfo(NoticeInfoModel noticeInfoModel) throws Exception {
		this.noticeInfoDao.update(noticeInfoModel, "noticeId");
	}
	
	/**
	 * 查询通告单下指定类型的道具信息
	 * @param crewId
	 * @param noticeId
	 * @param propType
	 * @return
	 */
	public List<Map<String, Object>> queryNoticePropList(String crewId, String noticeId, Integer propType) {
		return this.goodsInfoDao.queryNoticePropList(crewId, noticeId, propType);
	}
	
	/**
	 * 根据组名和通告单日期查询通告单
	 * @param groupName
	 * @param noticeDate
	 * @return
	 */
	public List<NoticeInfoModel> queryNoticeByGroupAndDate(String crewId, String groupName, String noticeDate) {
		return this.noticeInfoDao.queryNoticeByGroupAndDate(crewId, groupName, noticeDate);
	}
	
	/**
	 * 根据通告单ID和版本查询通告单下的图片附件信息
	 * @param noticeId 通告单ID
	 * @param noticeVersion	通告单版本
	 * @return
	 */
	public List<NoticePictureModel> queryNoticeImgByNotice(String noticeId, String noticeVersion) {
		return this.noticePictureDao.queryByNoticeIdAndVersion(noticeId, noticeVersion);
	}
	
	/**
	 * 查询用户针对指定通告单的待反馈状态或已收到状态的反馈信息
	 * 用户移动端反馈时更新反馈状态时使用，如果查出多条记录，则取第一条进行信息更新
	 * @param crewId
	 * @param noticeId
	 * @param noticeVersion
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	public NoticePushFedBackModel queryToBackInfoByNoticeInfo(String crewId, String noticeId, String noticeVersion, String userId) throws Exception {
		return this.noticePushFedBackDao.queryToBackInfoByNoticeInfo(crewId, noticeId, noticeVersion, userId);
	}
	
	/**
	 * 删除指定版本通告单的反馈信息
	 * @param crewId
	 * @param noticeId
	 * @param version
	 */
	public void deleteFedbackByNoticeVersion(String crewId, String noticeId, String version) {
		this.noticePushFedBackDao.deleteByNoticeVersion(crewId, noticeId);
	}
	
	/**
	 * 更新通告单反馈信息
	 * @param noticeFedBackModel
	 * @throws Exception 
	 */
	public void updateNoticeFedBackInfo(NoticePushFedBackModel noticeFedBackModel) throws Exception {
		this.noticePushFedBackDao.update(noticeFedBackModel, "id");
	}
	
	/**
	 * 保存通告单图片信息
	 * @param noticePictureModel
	 * @throws Exception 
	 */
	public void addNoticePicture(NoticePictureModel noticePictureModel) throws Exception {
		this.noticePictureDao.add(noticePictureModel);
	}
	
	/**
	 * 删除通告单图片
	 * @param crewId
	 * @param imgId
	 * @throws Exception 
	 */
	public void deleteNoticeImg(String crewId, String imgId) throws Exception {
		NoticePictureModel pictureModel = this.noticePictureDao.queryById(crewId, imgId);
		if (pictureModel != null) {
			final String bigPicurl = pictureModel.getBigPicurl();
			final String smallPicurl = pictureModel.getSmallPicurl();
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						FileUtils.deleteFile(bigPicurl);
						FileUtils.deleteFile(smallPicurl);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		
		this.noticePictureDao.deleteById(crewId, imgId);
	}
	
	/**
	 * 发布通告单
	 * @param noticeId	通告单ID
	 * @param noticeTitle	push消息标题
	 * @param noticeContent	push消息内容
	 * @param noticeVersion 通告单版本
	 * @param userIds	用户ID，多个ID用,隔开
	 * @param isAll	是否是发给所有人
	 * @return
	 * @throws Exception 
	 */
	public void publishNotice(String crewId, String loginUserId, String crewName, String noticeId, String noticeTitle, 
			String noticeContent, String userIds, Boolean isAll, Boolean needFeedback, Boolean publishNotice) throws Exception {
		NoticeTimeModel noticeTime = this.noticeTimeDao.queryNoticeTimeByNoticeId(noticeId);
		
		if (isAll != null && isAll) { //发布给所有人
			//更新通告单发布状态
			NoticeInfoModel noticeInfo = this.queryNoticeInfoModelById(noticeId);
			noticeInfo.setPublished(true);
			noticeInfo.setPublishTime(new Date());
			this.updateNoticeInfo(noticeInfo);
		}
		//根据id查询出当前用户信息
		List<UserInfoModel> userInfoList = this.userInfoDao.queryByIds(userIds);
		
		List<String> iosUserTokenList = new ArrayList<String>();
		List<String> androidTokenList = new ArrayList<String>();
		List<NoticePushFedBackModel> toAddFedBackModelList = new ArrayList<NoticePushFedBackModel>();
		List<MessageInfoModel> messageInfoList = new ArrayList<MessageInfoModel>();
		
		for (UserInfoModel userInfo : userInfoList) {
			if (userInfo.getClientType() == Constants.MOBILE_CLIENTTYPE_IPHONE && !StringUtils.isBlank(userInfo.getToken())) {
				iosUserTokenList.add(userInfo.getToken());
			}
			if (userInfo.getClientType() == Constants.MOBILE_CLIENTTYPE_ANDROID && !StringUtils.isBlank(userInfo.getToken())) {
				androidTokenList.add(userInfo.getToken());
			}
			
			//将用户加入待反馈列表
			NoticePushFedBackModel fedBackModel = new NoticePushFedBackModel();
			fedBackModel.setId(UUIDUtils.getId());
			fedBackModel.setCrewId(crewId);
			fedBackModel.setNoticeId(noticeId);
			fedBackModel.setNoticeVersion(this.sdf2.format(noticeTime.getUpdateTime()));
			fedBackModel.setUserId(userInfo.getUserId());
			fedBackModel.setNeedFedBack(needFeedback);
			fedBackModel.setBackStatus(Constants.NOTICE_FEDBACK_STATUS_NOTYET);
			toAddFedBackModelList.add(fedBackModel);
			
			//保存用户的消息信息
			if (publishNotice) { //判断是否需要发送推送信息
				MessageInfoModel messageInfo = new MessageInfoModel();
				messageInfo.setId(UUIDUtils.getId());
				messageInfo.setCrewId(crewId);
				messageInfo.setSenderId(loginUserId);
				messageInfo.setReceiverId(userInfo.getUserId());
				messageInfo.setType(MessageType.NoticePublish.getValue());
				messageInfo.setBuzId(noticeId);
				messageInfo.setStatus(MessageInfoStatus.UnRead.getValue());
				messageInfo.setTitle(noticeTitle);
				messageInfo.setContent(noticeContent);
				messageInfo.setRemindTime(new Date());
				messageInfo.setCreateTime(new Date());
				messageInfoList.add(messageInfo);
			}
		}
		
		this.messageInfoService.addMany(messageInfoList);
		
		//先删除以前的反馈
		this.noticePushFedBackDao.deleteByNoticeVersion(crewId, noticeId);
		//再添加新的反馈
		this.addManyNoticePushFedBack(toAddFedBackModelList);
		
		/*
		 * push消息
		 */
		CrewInfoModel crewInfo = this.crewInfoService.queryById(crewId);
		
		String title = noticeTitle;
		String pushMessage = noticeContent;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String myTime = sdf.format(new Date());
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", MessageType.NoticePublish.getValue());
		map.put("time", myTime);
		map.put("crewId", crewId);
		map.put("crewName", crewInfo.getCrewName());
		
		if (publishNotice) { //判断是否需要发送推送信息
			//IOS推送
			IOSPushMsg msg = new IOSPushMsg();
			msg.setTokenList(iosUserTokenList);
			msg.setAlert(pushMessage);
			msg.setCustomDictionaryMap(map);
			
			this.umengIOSPushService.iOSPushMsg(msg);
			
//			if (responseMap != null) {
//				boolean pushRespSuccess = (Boolean) responseMap.get("success");
//				String pushRespMessage = (String) responseMap.get("message");
//				
//				if (!pushRespSuccess) {
//					throw new IllegalArgumentException(pushRespMessage);
//				}
//			}
			
			
			//安卓推送
			AndroidPushMsg androidMsg = new AndroidPushMsg();
			androidMsg.setTokenList(androidTokenList);
			androidMsg.setTicker(pushMessage);
			androidMsg.setTitle(title);
			androidMsg.setText(pushMessage);
			androidMsg.setCustomDictionaryMap(map);
			this.umengAndroidPushService.androidPushMsg(androidMsg);
//			if (androidPushResponse != null) {
//				boolean pushRespSuccess = (Boolean) androidPushResponse.get("success");
//				String pushRespMessage = (String) androidPushResponse.get("message");
//				
//				if (!pushRespSuccess) {
//					throw new IllegalArgumentException(pushRespMessage);
//				}
//			}
		}
	}
	
	/**
	 * 批量新增反馈列表
	 * @param noticePushFedBackList
	 * @throws Exception 
	 */
	public void addManyNoticePushFedBack(List<NoticePushFedBackModel> noticePushFedBackList) throws Exception {
		this.noticePushFedBackDao.addBatch(noticePushFedBackList, NoticePushFedBackModel.class);
	}
	
	/**
	 * 查询用户针对指定版本的通告单最新的反馈信息
	 * @param crewId
	 * @param noticeId
	 * @param noticeVersion
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> queryFedBackInfoByNoticeInfo(String crewId, String noticeId, String noticeVersion) {
		return this.noticePushFedBackDao.queryFedBackInfoByNoticeInfo(crewId, noticeId, noticeVersion);
	}
	
	/**
	 * 查询通告单以往版本的图片信息
	 * @param currVersion	当前版本
	 * @return
	 */
	public List<NoticePictureModel> queryOldVersionPicture(String noticeId, String currVersion) {
		return this.noticePictureDao.queryOldVersionPicture(noticeId, currVersion);
	}
	
	/**
	 * 确认销场操作
	 * @param noticeViewIds
	 * @throws Exception 
	 */
	public void makeSureCancelView(String crewId, String noticeId, String seriesViewNos) throws Exception {
		String[] seriesViewNoArr = seriesViewNos.split(",");
		List<String> seriesViewNoList = Arrays.asList(seriesViewNoArr);
		List<ViewInfoModel> viewInfoList = this.viewInfoDao.queryBySeriesViewNoList(crewId, seriesViewNoList);
		
		//场景表中场景按照集-场号分组
		Map<String, String> viewlistGroupView = new HashMap<String, String>();
		for (ViewInfoModel viewInfo : viewInfoList) {
			int seriesNo = viewInfo.getSeriesNo();
			String viewNo = viewInfo.getViewNo();
			String viewId = viewInfo.getViewId();
			
			String seriesViewNo = seriesNo + "-" + viewNo.toLowerCase();
			
			if (!viewlistGroupView.containsKey(seriesViewNo)) {
				viewlistGroupView.put(seriesViewNo, viewId);
			}
		}
		
		//临时销场信息
		List<TmpCancelViewInfoModel> tmpCancelViewList = this.clipSerivce.queryTmpCancelInfoBySeriesViewNos(crewId, noticeId, seriesViewNos);
		//按照集-场号分组
		Map<String, TmpCancelViewInfoModel> toCancelNoticeGroupView = new HashMap<String, TmpCancelViewInfoModel>();	//按照通告单分组的场景信息
		for (TmpCancelViewInfoModel tmpCancelViewInfo : tmpCancelViewList) {
			int seriesNo = tmpCancelViewInfo.getSeriesNo();
			String viewNo = tmpCancelViewInfo.getViewNo();
			
			String seriesViewNo = seriesNo + "-" + viewNo.toLowerCase();
			if (!toCancelNoticeGroupView.containsKey(seriesViewNo)) {
				toCancelNoticeGroupView.put(seriesViewNo, tmpCancelViewInfo);
			}
		}
		
		//查询通告单中现有的场景信息
		List<Map<String, Object>> existNoticeViewList = this.viewNoticeMapDao.queryViewListByNoticeId(crewId, noticeId);
		//按照集-场号分组
		Map<String, String> noticeGroupView = new HashMap<String, String>();
		for (Map<String, Object> noticeViewMap : existNoticeViewList) {
			int seriesNo = (Integer) noticeViewMap.get("seriesNo");
			String viewNo = (String) noticeViewMap.get("viewNo");
			String viewId = (String) noticeViewMap.get("viewId");
			
			String seriesViewNo = seriesNo + "-" + viewNo.toLowerCase();
			if (!noticeGroupView.containsKey(seriesViewNo)) {
				noticeGroupView.put(seriesViewNo, viewId);
			}
		}
		
		/*
		 * 遍历用户选择的场景信息
		 */
		//校验该场景在该通告单中是否存在，如果存在直接销场，如果不存在，先为通告单添加场景，然后销场
		List<ViewNoticeMapModel> toAddViewNoticeList = new ArrayList<ViewNoticeMapModel>();
		
		int sequence = this.viewNoticeMapDao.getNoticeViewLastSequence(noticeId, crewId) + 1;
		for (String seriesViewNo : seriesViewNoList) {
			seriesViewNo = seriesViewNo.toLowerCase();
			
			String[] mySeriesViewNoArr = seriesViewNo.split("-");
			int seriesNo = Integer.parseInt(mySeriesViewNoArr[0]);
			String viewNo = mySeriesViewNoArr[1];
			
			
			//校验场景信息在顺场表中是否存在，如果不存在则不允许操作
			if (!viewlistGroupView.containsKey(seriesViewNo)) {
				throw new IllegalArgumentException(seriesViewNo + "场在顺场表中不存在，请先添加场景再销场。");
			}
			
			String viewId = viewlistGroupView.get(seriesViewNo);
			
			//校验场景信息在临时销场表中是否存在，如果不存在，则默认销场为甩戏
			TmpCancelViewInfoModel tmpCancelViewInfo = null;
			if (!toCancelNoticeGroupView.containsKey(seriesViewNo)) {
				tmpCancelViewInfo = new TmpCancelViewInfoModel();
				tmpCancelViewInfo.setSeriesNo(seriesNo);
				tmpCancelViewInfo.setViewNo(viewNo);
				tmpCancelViewInfo.setShootStatus(ShootStatus.Unfinished.getValue());
			} else {
				tmpCancelViewInfo = toCancelNoticeGroupView.get(seriesViewNo);
			}
			
			int shootStatus = tmpCancelViewInfo.getShootStatus();
			String statusRemark = tmpCancelViewInfo.getRemark();
			String tapNo = tmpCancelViewInfo.getTapNo();
			Date finishDate = tmpCancelViewInfo.getFinishDate();
			String finishDateStr = "";
			if (finishDate != null) {
				finishDateStr = this.sdf1.format(finishDate);
			}
			
			//校验场景信息在通告单中是否存在，如果不存在，则执行添加场景到通告单操作，如果存在，直接销场
			if (!noticeGroupView.containsKey(seriesViewNo)) {
				//为通告单添加场景
				ViewNoticeMapModel viewNoticeMap = new ViewNoticeMapModel();
				viewNoticeMap.setMapId(UUIDUtils.getId());
				viewNoticeMap.setViewId(viewId);
				viewNoticeMap.setNoticeId(noticeId);
				viewNoticeMap.setSequence(sequence);
				viewNoticeMap.setCrewId(crewId);
				viewNoticeMap.setShootStatus(shootStatus);
				viewNoticeMap.setStatusRemark(statusRemark);
				viewNoticeMap.setTapNo(tapNo);
				
				toAddViewNoticeList.add(viewNoticeMap);
			} else {
				//销场
				//找到map，更新map
				this.viewNoticeMapDao.cancelView(crewId, noticeId, viewId, shootStatus, statusRemark, tapNo);
			}
			
			//更新view，更新view的tapNo等信息，更新view的noticeId信息
			if (shootStatus == 1 || shootStatus == 4) { // 部分完成
				this.viewInfoDao.updateViewShootStatus(viewId, new Integer(1), statusRemark, tapNo, null, null);
			} else if (shootStatus == 2 || shootStatus == 5) { // 完成
				this.viewInfoDao.updateViewShootStatus(viewId, new Integer(2), statusRemark, tapNo, noticeId, finishDateStr);
			} else if (shootStatus == 3) { // 删戏
				this.viewInfoDao.updateViewShootStatus(viewId, new Integer(3), statusRemark, tapNo, noticeId, null);
			} else if (shootStatus == 0) { // 未完成
				this.viewInfoDao.updateViewShootStatus(viewId, new Integer(0), statusRemark, tapNo, null, null);
			}
		}
		
		this.addManyViewNoticeMap(sequence, toAddViewNoticeList);
		
		//检查通告单是否销场
		if(checkNoticeViewStatus(noticeId)==0){
			//销场
			NoticeInfoModel noticeModel = new NoticeInfoModel();
			noticeModel.setNoticeId(noticeId);
			noticeModel=noticeInfoDao.getEntityById(noticeModel, "noticeId");
			noticeModel.setCanceledStatus(NoticeCanceledStatus.Canceled.getValue());
			noticeModel.setUpdateTime(new Date());
			noticeInfoDao.update(noticeModel, "noticeId");
		}

		//将带待销场信息设置为已处理
		this.tmpCancelViewInfoDao.makeSureBySeriesViewNos(crewId, noticeId, seriesViewNos);
	}
	
	/**
	 * 批量添加场景和通告单的关联关系
	 * @param viewNoticeMap
	 * @throws Exception 
	 */
	public void addManyViewNoticeMap(int initSequence, List<ViewNoticeMapModel> viewNoticeMapList) throws Exception {
		for (ViewNoticeMapModel viewNoticeMap : viewNoticeMapList) {
			viewNoticeMap.setSequence(initSequence);
			this.viewNoticeMapDao.add(viewNoticeMap);
			initSequence ++;
		}
	}
	
	/**
	 * 查询单条通告单的完整信息
	 * 包含拍摄地、场景信息
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	public Map<String, Object> queryNoticeFullInfoById(String crewId, String noticeId) {
		return this.noticeInfoDao.queryNoticeFullInfoById(crewId, noticeId);
	}
	
	/**
	 * 根据时间段计算出共有多少个通告单 （A组B组或单组算成一个通告单）
	 * @param firstNoticeDate 第一个新建通告单的时间
	 * @param currNoticeDate 当前通告单的时间
	 * @return
	 */
	public List<Map<String, Object>> queryNoticeCountByDate(Date firstNoticeDate, Date currNoticeDate, String crewId, String cancledNotice){
		return this.noticeInfoDao.queryNoticeCountByDate(firstNoticeDate, currNoticeDate, crewId, cancledNotice);
	}
	
	/**
	 * 查询拍摄生产报表
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryShootingProductionReport(String crewId) {
		return this.noticeInfoDao.queryShootingProductionReport(crewId);
	}
	
	
	/**
	 * 根据场景id查询出通告单类列表
	 * @param viewId
	 * @return
	 */
	public List<Map<String, Object>> qureyNoticeListByViewId(String viewId, String crewId){
		return this.noticeInfoDao.queryNoticeIdByViewId(viewId,crewId);
	}
	
	 /**
	  * 查询当前剧组中已销场通告单的总数
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryCancledNoticeCount(String crewId){
		 return this.noticeInfoDao.queryCancledNoticeCount(crewId);
	 }
	
	 /**
	  * 查询当前剧组中未销场通告单的总数
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryNotCancledNoticeCount(String crewId){
		 return this.noticeInfoDao.queryNotCancledNoticeCount(crewId);
	 }
	
	/**
	 * 分页查询月份列表
	 * @param crewId
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> queryCancleMonthList(String crewId, Page page){
		return this.noticeInfoDao.queryCancledMonthList(crewId, page);
	}
	
	
	/**
	 * 根据通告单的id查询出通告单下场景的状态
	 * @param noticeId
	 * @return
	 */
	public List<Map<String, Object>> queryViewStatusByNoticeId(String noticeId){
		return this.noticeInfoDao.queryNoticeViewListByNoticeId(noticeId);
	}
	
	/**
	 * 根据通告单id，查询通告单的反馈列表
	 * @param noticeId
	 * @return
	 */
	public List<Map<String, Object>> queryNoticeFacdbackList(String noticeId, String noticeVersion){
		return this.noticePushFedBackDao.queryFedbackByNoticeId(noticeId, noticeVersion);
	}
	
	/**
	 * 查询已拍摄多少天，根据已销场的通告单
	 * @param crewId
	 * @return
	 */
	public int queryShootDates(String crewId) {
		return this.noticeInfoDao.queryShootDates(crewId);
	}
	
	/**
	 * 查询日拍摄进度，分组
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryDayProduction(String crewId) {
		return this.noticeInfoDao.queryDayProduction(crewId);
	}
	
	/**
	 * 查询通告单和场景关系表中的场景信息
	 * @param noticeId
	 * @param viewId
	 * @return
	 * @throws Exception 
	 */
	public ViewNoticeMapModel queryViewMapInfo(String noticeId, String viewId) throws Exception{
		return this.viewNoticeMapDao.queryViewMapInfo(noticeId, viewId);
	}
	
	/**
	 * 批量更新关系表数据
	 * @param list
	 * @throws Exception 
	 */
	public void updateNoticeViewMapInfo(List<ViewNoticeMapModel> list) throws Exception {
		this.viewNoticeMapDao.updateBatch(list, "mapId", ViewNoticeMapModel.class);
	}
	
	/**
	 * 查询用户在剧组中未读的通告单列表
	 * @param crewId
	 * @param userId
	 * @return
	 */
	public List<NoticeInfoModel> queryNotReadNoticeList(String crewId, String userId) {
		return this.noticeInfoDao.queryNotReadNoticeList(crewId, userId);
	}
	
	/**
	 * 分页查询通告单日期列表
	 * @param crewId
	 * @param canceledStatus
	 * @param noticeMonth
	 * @param page
	 * @return
	 * @throws ParseException 
	 */
	public List<Map<String, Object>> queryNoticeDateList(String crewId, Integer canceledStatus, String noticeMonth, Page page) throws ParseException {
		//查询通告单日期统计列表
		List<Map<String, Object>> noticeDateList = this.noticeInfoDao.queryNoticeDateList(crewId, canceledStatus, noticeMonth, page);

		if(noticeDateList != null && noticeDateList.size() > 0) {

			String noticeStartDate = null;
			String noticeEndDate = null;
			if(canceledStatus == 0) {
				noticeStartDate = sdf1.format(noticeDateList.get(0).get("noticeDate"));
				noticeEndDate = sdf1.format(noticeDateList.get(noticeDateList.size() - 1).get("noticeDate"));
			} else {
				noticeStartDate = sdf1.format(noticeDateList.get(noticeDateList.size() - 1).get("noticeDate"));
				noticeEndDate = sdf1.format(noticeDateList.get(0).get("noticeDate"));
			}
			//查询指定日期下的通告单
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("noticeStartDate", noticeStartDate);
			conditionMap.put("noticeEndDate", noticeEndDate);
			conditionMap.put("canceledStatus", canceledStatus);
			List<Map<String, Object>> noticeList = this.noticeInfoDao.queryNoticeByCrewId(crewId, null, null, true, conditionMap);
			for(Map<String, Object> noticeDateMap : noticeDateList) {
				String noticeDate = noticeDateMap.get("noticeDate") + "";
				String rownum = noticeDateMap.get("rownum") + "";
				List<Map<String, Object>> noticeMapList = new ArrayList<Map<String,Object>>();
				noticeDateMap.put("noticeList", noticeMapList);
				for(Map<String, Object> noticeInfo : noticeList) {
					if((noticeInfo.get("noticeDate") + "").equals(noticeDate)) {
						noticeInfo.put("rownum", rownum);
						//取出通告单中主要演员的信息
						String mainRole = (String) noticeInfo.get("mainrole");
						//取出通告单中特约演员的信息
						String guestRole = (String) noticeInfo.get("guestrole");
						
						if (!StringUtils.isBlank(mainRole)) {
							String[] mainRoleArr = mainRole.split(",");
							List<String> mainRoleList = new ArrayList<String>();
							mainRole = "";
							
							//使用list集合去除主要演员的重复数据
							for (String mainRoleStr : mainRoleArr) {
								if (!mainRoleList.contains(mainRoleStr)) {
									mainRoleList.add(mainRoleStr);
									mainRole += mainRoleStr + "，";
								}
							}
							
							if (!StringUtils.isBlank(mainRole)) {
								mainRole = mainRole.substring(0, mainRole.length() - 1);
							}
							//将去重之后的主要演员的信息重新放进map中
							noticeInfo.remove("mainrole");
							noticeInfo.put("mainrole", mainRole);
						}
						
						//对特约演员的数据进行去重
						if (!StringUtils.isBlank(guestRole)) {
							String[] guestRoleArr = guestRole.split(",");
							
							List<String> guestRoleList = new ArrayList<String>();
							guestRole = "";
							for (String guestRoleStr : guestRoleArr) {
								if (!guestRoleList.contains(guestRoleStr)) {
									guestRoleList.add(guestRoleStr);
									guestRole += guestRoleStr + ",";
								}
							}
							if (!StringUtils.isBlank(guestRole)) {
								guestRole = guestRole.substring(0, guestRole.length() - 1);
							}
							noticeInfo.remove("guestrole");
							noticeInfo.put("guestrole", guestRole);
						}
						
						//将更新时间进行格式化
						Date updateTime = (Date) noticeInfo.get("updateTime");
						String updateTimeStr = DateUtils.parse2String(updateTime, "yyyy-MM-dd HH:mm:ss");
						noticeInfo.remove("updateTime");
						noticeInfo.put("updateTime", updateTimeStr);
						
						//格式化发布通告单的时间
						if (noticeInfo.get("publishTime") != null) {
							Date publishTime = (Date) noticeInfo.get("publishTime");
							String publishTimeStr = DateUtils.parse2String(publishTime, "yyyy-MM-dd HH:mm:ss");
							noticeInfo.remove("publishTime");
							noticeInfo.put("publishTime", publishTimeStr);
						}
						noticeMapList.add(noticeInfo);
					}
				}
				noticeDateList.removeAll(noticeMapList);
			}
		}
		return noticeDateList;
	}
 }
