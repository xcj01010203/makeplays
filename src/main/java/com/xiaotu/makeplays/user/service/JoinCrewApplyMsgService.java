package com.xiaotu.makeplays.user.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.authority.dao.UserAuthMapDao;
import com.xiaotu.makeplays.crew.dao.CrewInfoDao;
import com.xiaotu.makeplays.crew.dao.CrewUserMapDao;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.message.model.MessageInfoModel;
import com.xiaotu.makeplays.message.model.constants.MessageInfoStatus;
import com.xiaotu.makeplays.message.model.constants.MessageType;
import com.xiaotu.makeplays.message.service.MessageInfoService;
import com.xiaotu.makeplays.mobile.push.umeng.model.android.AndroidPushMsg;
import com.xiaotu.makeplays.mobile.push.umeng.model.ios.IOSPushMsg;
import com.xiaotu.makeplays.mobile.push.umeng.service.android.UmengAndroidPushService;
import com.xiaotu.makeplays.mobile.push.umeng.service.ios.UmengIOSPushService;
import com.xiaotu.makeplays.sysrole.dao.UserRoleMapDao;
import com.xiaotu.makeplays.user.dao.JoinCrewApplyMsgDao;
import com.xiaotu.makeplays.user.dao.UserInfoDao;
import com.xiaotu.makeplays.user.model.JoinCrewApplyMsgModel;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.model.constants.JoinCrewAuditStatus;
import com.xiaotu.makeplays.user.model.constants.UserClientType;
import com.xiaotu.makeplays.utils.UUIDUtils;

@Service
public class JoinCrewApplyMsgService {
	
	@Autowired
	private CrewUserMapDao crewUserMapDao;

	@Autowired
	private JoinCrewApplyMsgDao joinCrewApplyMsgDao;
	
	@Autowired
	private UserRoleMapDao userRoleMapDao;
	
	@Autowired
	private UserInfoDao userInfoDao;
	
	@Autowired
	private UmengAndroidPushService umengAndroidPushService;
	
	@Autowired
	private UmengIOSPushService umengIOSPushService;
	
	@Autowired
	private CrewInfoDao crewInfoDao;
	
	@Autowired
	private CrewContactService crewContactService;
	
	@Autowired
	private UserAuthMapDao userAuthMapDao;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private MessageInfoService messageInfoService;
	
	/**
	 * 查询剧组下指定状态的入组信息
	 * 该查询和用户表关联，查询出用户其他信息
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryCrewAuditingMsg(String crewId, Integer status) {
		return this.joinCrewApplyMsgDao.queryCrewAuditingMsg(crewId, status);
	}
	
	/**
	 * 审核用户入组申请
	 * @param userId	审核人ID
	 * @param crewId	剧组ID
	 * @param agree	是否同意入组
	 * @param aimUserId	申请入组人ID
	 * @throws Exception 
	 */
	public void auditEnterApply (String userId, String crewId, boolean agree, String aimUserId) throws Exception {

		UserInfoModel aimUserInfo = this.userInfoDao.queryById(aimUserId);
		
		JoinCrewApplyMsgModel joinMsg = this.joinCrewApplyMsgDao.queryByCrewIdAndUserId(crewId, aimUserId);
		if (joinMsg == null) {
			throw new IllegalArgumentException("该条申请已被审核");
		}
		if (agree) {
			joinMsg.setStatus(JoinCrewAuditStatus.Agree.getValue());
		} else {
			joinMsg.setStatus(JoinCrewAuditStatus.Reject.getValue());
		}
		//更新申请消息记录
		joinMsg.setDealerId(userId);
		joinMsg.setLastModifyTime(new Date());
		this.joinCrewApplyMsgDao.update(joinMsg, "id");
		
		
		//把用户加入到剧组中
		if (agree) {
			this.userService.addUserToCrew(crewId, aimUserId, joinMsg.getAimRoleIds());
		}
		
		//推送消息
		try {
			List<String> iosUserTokenList = new ArrayList<String>();
			List<String> androidTokenList = new ArrayList<String>();
			
			//加入的剧组信息
			CrewInfoModel crewInfoModel = this.crewInfoDao.queryById(crewId);
			String crewName = crewInfoModel.getCrewName();
			
			//审核人信息
			UserInfoModel auditUserInfo = this.userInfoDao.queryById(userId);
			String auditUserName = auditUserInfo.getRealName();
			
			Integer clientType = aimUserInfo.getClientType();
			String token = aimUserInfo.getToken();
			if (clientType != null && !StringUtils.isBlank(token)) {
				if (clientType.intValue() == UserClientType.Android.getValue()) {
					androidTokenList.add(token);
				}
				if (clientType.intValue() == UserClientType.IOS.getValue()) {
					iosUserTokenList.add(token);
				}
			}
			
			/*
			 * push消息
			 */
			String title = "申请入组审核结果";
			String pushMessage = "";
			if (agree) {
				pushMessage = auditUserName + "批准您加入《" + crewName + "》剧组";
			} else {
				pushMessage = auditUserName + "拒绝您加入《" + crewName + "》剧组";
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String myTime = sdf.format(new Date());
			
			Map<String, Object> map = new HashMap<String, Object>();
			if (agree) {
				map.put("type", MessageType.AuditJoinCrewSuccess.getValue());
			} else {
				map.put("type", MessageType.AuditJoinCrewFail.getValue());
			}
			
			map.put("title", title);
			map.put("time", myTime);
			map.put("buzId", crewId);
			map.put("crewId", crewId);
			map.put("crewName", crewInfoModel.getCrewName());
			
			
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
			
			
			//保存用户的消息信息
			MessageInfoModel messageInfo = new MessageInfoModel();
			messageInfo.setId(UUIDUtils.getId());
			messageInfo.setSenderId(userId);
			messageInfo.setReceiverId(aimUserId);
			if (agree) {
				messageInfo.setType(MessageType.AuditJoinCrewSuccess.getValue());
				messageInfo.setCrewId(crewId);
			} else {
				messageInfo.setType(MessageType.AuditJoinCrewFail.getValue());
				messageInfo.setCrewId("0");
			}
			messageInfo.setStatus(MessageInfoStatus.UnRead.getValue());
			messageInfo.setTitle(title);
			messageInfo.setContent(pushMessage);
			messageInfo.setRemindTime(new Date());
			messageInfo.setCreateTime(new Date());
			this.messageInfoService.addOne(messageInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
