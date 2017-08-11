package com.xiaotu.makeplays.feedback.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.feedback.dao.FeedbackDao;
import com.xiaotu.makeplays.feedback.dao.FeedbackReplyDao;
import com.xiaotu.makeplays.feedback.model.FeedBackReplyModel;
import com.xiaotu.makeplays.feedback.model.FeedbackModel;
import com.xiaotu.makeplays.message.model.MessageInfoModel;
import com.xiaotu.makeplays.message.model.constants.MessageInfoStatus;
import com.xiaotu.makeplays.message.model.constants.MessageType;
import com.xiaotu.makeplays.message.service.MessageInfoService;
import com.xiaotu.makeplays.mobile.push.umeng.model.android.AndroidPushMsg;
import com.xiaotu.makeplays.mobile.push.umeng.model.ios.IOSPushMsg;
import com.xiaotu.makeplays.mobile.push.umeng.service.android.UmengAndroidPushService;
import com.xiaotu.makeplays.mobile.push.umeng.service.ios.UmengIOSPushService;
import com.xiaotu.makeplays.user.controller.filter.FeedbackFilter;
import com.xiaotu.makeplays.user.dao.UserInfoDao;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.model.constants.UserType;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 用户反馈信息
 * @author xuchangjian 2016-10-10下午4:49:19
 */
@Service
public class FeedbackService {
	
	@Autowired
	private FeedbackDao feedbackDao;
	
	@Autowired
	private FeedbackReplyDao feedbackReplyDao;
	
	@Autowired
	private UserInfoDao userInfoDao;
	
	@Autowired
	private MessageInfoService messageInfoService;
	
	@Autowired
	private UmengIOSPushService umengIOSPushService;
	
	@Autowired
	private UmengAndroidPushService umengAndroidPushService;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 新增意见反馈，并发送站内信和推送消息
	 * @param feedbackModel
	 * @return
	 * @throws Exception
	 */
	public void addOne(FeedbackModel feedbackModel) throws Exception{
		//保存意见反馈
		feedbackDao.add(feedbackModel);
		//用户信息
//		UserInfoModel fbUserInfo = this.userInfoDao.queryById(feedBackModel.getUserId());
		//查询需要发送站内信和推送消息的用户
		List<UserInfoModel> userList = this.userInfoDao.queryUserListForFeedBack(feedbackModel.getUserId());
		
		List<String> iosUserTokenList = new ArrayList<String>();
		List<String> androidTokenList = new ArrayList<String>();
		List<MessageInfoModel> messageInfoList = new ArrayList<MessageInfoModel>();
		
		for (UserInfoModel userInfo : userList) {
			if (userInfo.getClientType() == Constants.MOBILE_CLIENTTYPE_IPHONE && !StringUtils.isBlank(userInfo.getToken())) {
				iosUserTokenList.add(userInfo.getToken());
			}
			if (userInfo.getClientType() == Constants.MOBILE_CLIENTTYPE_ANDROID && !StringUtils.isBlank(userInfo.getToken())) {
				androidTokenList.add(userInfo.getToken());
			}
						
			MessageInfoModel messageInfo = new MessageInfoModel();
			messageInfo.setId(UUIDUtils.getId());
			messageInfo.setCrewId("0");
			messageInfo.setSenderId(feedbackModel.getUserId());
			messageInfo.setReceiverId(userInfo.getUserId());
			messageInfo.setType(MessageType.Feedback.getValue());
			messageInfo.setBuzId(null);
			messageInfo.setStatus(MessageInfoStatus.UnRead.getValue());
			messageInfo.setTitle("用户反馈");
			messageInfo.setContent("您收到了一条用户反馈，请查看。");//fbUserInfo.getRealName() + "反馈：" + feedBackModel.getMessage()
			messageInfo.setRemindTime(new Date());
			messageInfo.setCreateTime(new Date());
			messageInfoList.add(messageInfo);
		}
		//添加站内信
		this.messageInfoService.addMany(messageInfoList);
		
		/*
		 * push消息
		 */
		String title = "用户反馈";
		String pushMessage = "您收到了一条用户反馈，请查看。";//fbUserInfo.getRealName() + "反馈：" + feedBackModel.getMessage();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String myTime = sdf.format(new Date());
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", MessageType.Feedback.getValue());
		map.put("time", myTime);
		map.put("crewId", "0");
		
		//IOS推送
		IOSPushMsg msg = new IOSPushMsg();
		msg.setTokenList(iosUserTokenList);
		msg.setAlert(pushMessage);
		msg.setCustomDictionaryMap(map);
		
		this.umengIOSPushService.iOSPushMsg(msg);
		
//		if (responseMap != null) {
//			boolean pushRespSuccess = (Boolean) responseMap.get("success");
//			String pushRespMessage = (String) responseMap.get("message");
//			
//			if (!pushRespSuccess) {
//				throw new IllegalArgumentException(pushRespMessage);
//			}
//		}
		
		
		//安卓推送
		AndroidPushMsg androidMsg = new AndroidPushMsg();
		androidMsg.setTokenList(androidTokenList);
		androidMsg.setTicker(pushMessage);
		androidMsg.setTitle(title);
		androidMsg.setText(pushMessage);
		androidMsg.setCustomDictionaryMap(map);
		this.umengAndroidPushService.androidPushMsg(androidMsg);
//		if (androidPushResponse != null) {
//			boolean pushRespSuccess = (Boolean) androidPushResponse.get("success");
//			String pushRespMessage = (String) androidPushResponse.get("message");
//			
//			if (!pushRespSuccess) {
//				throw new IllegalArgumentException(pushRespMessage);
//			}
//		}
	}
	
	/**
	 * 查询客服能看到的反馈用户列表
	 * @param userId
	 * @param loginUserType
	 * @return
	 */
	public List<Map<String, Object>> queryFeedBackUserList(String userId, int loginUserType, Page page, FeedbackFilter filter) {
		List<Map<String, Object>> result = this.feedbackDao.queryFeedBackUserList(userId, loginUserType, page, filter);
		if(result != null && result.size() > 0) {
			for(Map<String, Object> one : result) {
				Integer myStatus = (Integer) one.get("status");
				boolean hasNewStatus = false;
				if (myStatus == 0) {
					hasNewStatus = true;
				}
				
				one.put("hasNewStatus", hasNewStatus);
				one.put("statusUpdateTime", sdf.format(one.get("statusUpdateTime")));
				
				one.remove("status");
			}
		}
		return result;
	}
	
	/**
	 * 获取用户的意见反馈列表，包括反馈信息
	 * 该方法如果是客服访问，则需要更新用户反馈信息的状态为已读
	 * @param userId
	 * @param loginUserType 用户类型：1-系统管理员  2-客服  3-普通剧组用户  4--总客服
	 * @return
	 * @throws Exception 
	 */
	public List<Map<String, Object>> fetchUserFeedBackList(String userId, Page page, Integer loginUserType) throws Exception {
		if(loginUserType == 2 || loginUserType == 4) {//如果是客服访问，则把访问用户的所有反馈更新为已读
			//更新用户反馈状态
			updateUserFeedBackStatus(userId);
		} else if (loginUserType == 3) {	//如果是剧组用户访问，则把该用户所有反馈中客服的回复更新为已读
			this.feedbackReplyDao.readKefuReplyInfo(userId);
		}
		
		UserInfoModel userInfo = this.userInfoDao.queryById(userId);
		
		List<FeedbackModel> feedbackList = this.feedbackDao.queryFeedbackList(userId, page);
		String feedbackIds = "";
		for (FeedbackModel feedbackInfo : feedbackList) {
			String feedbackId = feedbackInfo.getId();
			feedbackIds += feedbackId + ",";
		}
		if (!StringUtils.isBlank(feedbackIds)) {
			feedbackIds = feedbackIds.substring(0, feedbackIds.length() - 1);
		}
		
		List<Map<String, Object>> replyList = this.feedbackReplyDao.queryReplyListWithUserInfo(feedbackIds);
		
		List<Map<String, Object>> feedbackInfoMapList = new ArrayList<Map<String,Object>>();
		for (FeedbackModel feedbackInfo : feedbackList) {
			Map<String, Object> feedbackInfoMap = new HashMap<String, Object>();
			feedbackInfoMap.put("feedbackId", feedbackInfo.getId());
			feedbackInfoMap.put("userId", userInfo.getUserId());
			feedbackInfoMap.put("userName", userInfo.getRealName());
			feedbackInfoMap.put("phone", userInfo.getPhone());
			feedbackInfoMap.put("contact", feedbackInfo.getContact());
			feedbackInfoMap.put("message", feedbackInfo.getMessage());
			feedbackInfoMap.put("clientType", feedbackInfo.getClientType());
			feedbackInfoMap.put("createTime", this.sdf.format(feedbackInfo.getCreateTime()));
			
			List<Map<String, Object>> myReplyList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> replyInfo : replyList) {
				String feedbackId = (String) replyInfo.get("feedbackId");
				
				Map<String, Object> myReplyInfoMap = new HashMap<String, Object>();
				if (feedbackId.equals(feedbackInfo.getId())) {
					myReplyInfoMap.put("replyId", replyInfo.get("id"));
					myReplyInfoMap.put("replyUserId", replyInfo.get("userId"));
					myReplyInfoMap.put("replyUserName", replyInfo.get("realName"));
					myReplyInfoMap.put("reply", replyInfo.get("reply"));
					myReplyInfoMap.put("replyTime", this.sdf.format(replyInfo.get("createTime")));
					myReplyInfoMap.put("replyClientType", replyInfo.get("clientType"));
					
					Integer replyerType = (Integer) replyInfo.get("replyerType");
					boolean iskefu = false;
					if (replyerType == UserType.CustomerService.getValue()) {
						iskefu = true;
					}
					myReplyInfoMap.put("iskefu", iskefu);
					
					myReplyList.add(myReplyInfoMap);
				}
			}
			
			feedbackInfoMap.put("replyList", myReplyList);
			
			feedbackInfoMapList.add(feedbackInfoMap);
		}
		
		return feedbackInfoMapList;
	}
	
	
	/**
	 * 回复用户意见反馈
	 * @param userInfo 回复用户
	 * @param loginUserType 用户类型
	 * @param feedBackId 意见反馈ID
	 * @param reply 回复内容
	 * @param aimUserId 被回复用户
	 * @throws Exception
	 */
	public void replyFeedBack(UserInfoModel userInfo,int loginUserType, Integer clientType, String feedBackId, String reply, String aimUserId) throws Exception {
		//保存回复信息
		FeedBackReplyModel feedBackReply = new FeedBackReplyModel();
		feedBackReply.setId(UUIDUtils.getId());
		feedBackReply.setFeedBackId(feedBackId);
		feedBackReply.setUserId(userInfo.getUserId());
		feedBackReply.setReply(reply);
		feedBackReply.setCreateTime(new Date());
		feedBackReply.setClientType(clientType);
		feedbackReplyDao.add(feedBackReply);
		
		String messageTitle = "反馈答复";
		String messageContent = "您收到了一条反馈答复，请查看。";
		
		List<UserInfoModel> userList = new ArrayList<UserInfoModel>();
		if(loginUserType == 3) {//普通用户
			messageTitle = "用户反馈";
			messageContent = "您收到了一条用户回复，请查看。";
			
			//查询需要发送站内信和推送消息的用户
			userList = this.userInfoDao.queryUserListForFeedBack(userInfo.getUserId());
		} else {//客服
			userList.add(this.userInfoDao.queryById(aimUserId));
		}

		List<String> iosUserTokenList = new ArrayList<String>();
		List<String> androidTokenList = new ArrayList<String>();
		List<MessageInfoModel> messageInfoList = new ArrayList<MessageInfoModel>();
		for (UserInfoModel one : userList) {
			if (one.getClientType() == Constants.MOBILE_CLIENTTYPE_IPHONE && !StringUtils.isBlank(one.getToken())) {
				iosUserTokenList.add(one.getToken());
			}
			if (one.getClientType() == Constants.MOBILE_CLIENTTYPE_ANDROID && !StringUtils.isBlank(one.getToken())) {
				androidTokenList.add(one.getToken());
			}
						
			MessageInfoModel messageInfo = new MessageInfoModel();
			messageInfo.setId(UUIDUtils.getId());
			messageInfo.setCrewId("0");
			messageInfo.setSenderId(userInfo.getUserId());
			messageInfo.setReceiverId(one.getUserId());
			messageInfo.setType(MessageType.Feedback.getValue());
			messageInfo.setBuzId(null);
			messageInfo.setStatus(MessageInfoStatus.UnRead.getValue());
			messageInfo.setTitle(messageTitle);
			messageInfo.setContent(messageContent);
			messageInfo.setRemindTime(new Date());
			messageInfo.setCreateTime(new Date());
			messageInfoList.add(messageInfo);
		}
		//添加站内信
		this.messageInfoService.addMany(messageInfoList);
		
		/*
		 * push消息
		 */
		String title = messageTitle;
		String pushMessage = messageContent;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String myTime = sdf.format(new Date());
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", MessageType.Feedback.getValue());
		map.put("time", myTime);
		map.put("crewId", "0");
		
		//IOS推送
		IOSPushMsg msg = new IOSPushMsg();
		msg.setTokenList(iosUserTokenList);
		msg.setAlert(pushMessage);
		msg.setCustomDictionaryMap(map);
		
		this.umengIOSPushService.iOSPushMsg(msg);
		
//		if (responseMap != null) {
//			boolean pushRespSuccess = (Boolean) responseMap.get("success");
//			String pushRespMessage = (String) responseMap.get("message");
//			
//			if (!pushRespSuccess) {
//				throw new IllegalArgumentException(pushRespMessage);
//			}
//		}
		
		
		//安卓推送
		AndroidPushMsg androidMsg = new AndroidPushMsg();
		androidMsg.setTokenList(androidTokenList);
		androidMsg.setTicker(pushMessage);
		androidMsg.setTitle(title);
		androidMsg.setText(pushMessage);
		androidMsg.setCustomDictionaryMap(map);
		this.umengAndroidPushService.androidPushMsg(androidMsg);
//		if (androidPushResponse != null) {
//			boolean pushRespSuccess = (Boolean) androidPushResponse.get("success");
//			String pushRespMessage = (String) androidPushResponse.get("message");
//			
//			if (!pushRespSuccess) {
//				throw new IllegalArgumentException(pushRespMessage);
//			}
//		}
	}
	
	/**
	 * 查询反馈的回复信息
	 * @param feedBackId
	 * @return
	 */
	public List<Map<String, Object>> queryReplyById(String feedBackId) {
		List<Map<String, Object>> replyList = this.feedbackDao.queryReplyById(feedBackId);
		if(replyList != null && replyList.size() > 0) {
			for(Map<String, Object> map : replyList) {
				map.put("replyTime", sdf.format(map.get("replyTime")));
			}
		}
		return replyList;
	}
	
	/**
	 * 更新用户反馈状态
	 * @param userId
	 */
	public void updateUserFeedBackStatus(String userId) {
		//更新用户反馈状态为已读
		this.feedbackDao.updateFeedBackStatus(userId);
		//更新用户回复状态为已读
		this.feedbackReplyDao.readUserReplyInfo(userId);
	}
	
	/**
	 * 根据ID查询反馈信息
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public FeedbackModel queryById(String id) throws Exception {
		return this.feedbackDao.queryById(id);
	}
	
	/**
	 * 查询指定用户的反馈中所有客服的回复列表
	 * 返回状态为未读的回复
	 * @param userId
	 * @return
	 */
	public List<FeedBackReplyModel> queryKefuReplyInfo(String userId) {
		return this.feedbackReplyDao.queryKefuReplyInfo(userId);
	}
}
