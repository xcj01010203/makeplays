package com.xiaotu.makeplays.mobile.server.message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.xiaotu.makeplays.feedback.model.FeedBackReplyModel;
import com.xiaotu.makeplays.feedback.service.FeedbackService;
import com.xiaotu.makeplays.message.model.MessageInfoModel;
import com.xiaotu.makeplays.message.model.constants.MessageInfoStatus;
import com.xiaotu.makeplays.message.service.MessageInfoService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.sysrole.service.UserRoleMapService;
import com.xiaotu.makeplays.user.controller.filter.FeedbackFilter;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.model.constants.UserType;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.Page;

/**
 * 消息相关接口
 * @author xuchangjian 2016-9-24上午11:23:39
 */
@Controller
@RequestMapping("/interface/messageInfoFacade")
public class MessageInfoFacade {

	Logger logger = LoggerFactory.getLogger(MessageInfoFacade.class);
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	private MessageInfoService messageInfoService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRoleMapService userRoleMapService;
	
	@Autowired
	private FeedbackService feedbackService;
	
	/**
	 * 获取消息列表
	 * @param crewId
	 * @param userId	用户ID
	 * @param pageSize	每页显示条数
	 * @param pageNo	当前页数
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainMessageList")
	public Object obtainMessageList(String crewId, String userId, Integer pageSize, Integer pageNo) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
//			if (StringUtils.isBlank(crewId)) {
//				throw new IllegalArgumentException("请先加入剧组");
//			}
			MobileUtils.checkUserValid(userId);
			
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("crewId", crewId);
			conditionMap.put("receiverId", userId);
			
			if (pageNo == null) {
				pageNo = 1;
			}
			if (pageSize == null) {
				pageSize = 20;
			}
			Page page = new Page();
			page.setPagesize(pageSize);
			page.setPageNo(pageNo);
			
			List<MessageInfoModel> messageList = this.messageInfoService.queryManyByMutiCondition(conditionMap, page);
			
			List<Map<String, Object>> messageMapList = new ArrayList<Map<String, Object>>();
			for (MessageInfoModel messageInfo : messageList) {
				Map<String, Object> messageMap = new HashMap<String, Object>();
				messageMap.put("id", messageInfo.getId());
				messageMap.put("title", messageInfo.getTitle());
				messageMap.put("content", messageInfo.getContent());
				messageMap.put("type", messageInfo.getType());
				messageMap.put("status", messageInfo.getStatus());
				messageMap.put("createTime", this.sdf1.format(messageInfo.getCreateTime()));
				
				messageMapList.add(messageMap);
			}
			
			resultMap.put("messageList", messageMapList);
			resultMap.put("pageCount", page.getPageCount());
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取消息列表失败", e);
			throw new IllegalArgumentException("未知异常，获取消息列表失败", e);
		}
		return resultMap;
	}
	
	/**
	 * 已阅消息
	 * @param crewId
	 * @param userId	用户ID
	 * @param messageId	消息ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/readMessage")
	public Object readMessage(String userId, String messageId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkUserValid(userId);
			if (StringUtils.isBlank(messageId)) {
				throw new IllegalArgumentException("请提供消息ID");
			}
			
			MessageInfoModel messageInfo = this.messageInfoService.queryById(messageId);
			messageInfo.setStatus(MessageInfoStatus.HasRead.getValue());
			messageInfo.setIsNew(false);
			
			this.messageInfoService.updateOne(messageInfo);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，操作失败", e);
			throw new IllegalArgumentException("未知异常，操作失败", e);
		}
		return resultMap;
	}
	
	/**
	 * 使消息变老
	 * @param crewId
	 * @param userId	用户ID
	 * @param messageId	消息ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/oldMessage")
	public Object oldMessage(String crewId, String userId, Integer messageType) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			this.messageInfoService.oldMessage(crewId, userId, messageType);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，操作失败", e);
			throw new IllegalArgumentException("未知异常，操作失败", e);
		}
		return resultMap;
	}

	/**
	 * 删除消息
	 * @param crewId
	 * @param userId	用户ID
	 * @param messageId	消息ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteMessage")
	public Object deleteMessage(String crewId, String userId, String messageId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			this.messageInfoService.deleteOne(messageId);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取消息列表失败", e);
			throw new IllegalArgumentException("未知异常，获取消息列表失败", e);
		}
		return resultMap;
	}
	

	/**
	 * 查询未读消息个数
	 * 20170424被checkhasNewMessage替代
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@Deprecated
	@ResponseBody
	@RequestMapping("/obtainUnreadMessageNum")
	public Object obtainUnreadMessageNum(String crewId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("crewId", crewId);
			conditionMap.put("receiverId", userId);
			conditionMap.put("status", MessageInfoStatus.UnRead);
			
			List<MessageInfoModel> messageInfoList = this.messageInfoService.queryManyByMutiCondition(conditionMap, null);
			
			resultMap.put("num", messageInfoList.size());
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取消息列表失败", e);
			throw new IllegalArgumentException("未知异常，获取消息列表失败", e);
		}
		return resultMap;
	}
	
	/**
	 * 检查是否有新消息
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/checkHasNewMessage")
	public Object checkHasNewMessage(String crewId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkUserValid(userId);
			
			//查询是否有未读的个人消息
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("crewId", crewId);
			conditionMap.put("receiverId", userId);
			conditionMap.put("status", MessageInfoStatus.UnRead);
			List<MessageInfoModel> messageInfoList = this.messageInfoService.queryManyByMutiCondition(conditionMap, null);
			boolean hasUnreadMessage = false;
			if (messageInfoList.size() > 0) {
				hasUnreadMessage = true;	
			}
			
			//查询是否有未读的反馈
			boolean hasUnreadFeedback = false;
			UserInfoModel userInfo = this.userService.queryById(userId);
			int loginUserType = 1;
			if(userInfo.getType() == UserType.CustomerService.getValue()) {
				loginUserType = 2; //普通客服
				
				String roleId = this.userRoleMapService.queryAllUserRoleIds(userId);
				if(roleId.equals(Constants.ROLE_ID_CUSTOM_SERVICE)) {
					loginUserType = 4;	//总客服
				}
			}
			if(userInfo.getType() == UserType.CrewUser.getValue()) {
				loginUserType = 3; //普通剧组用户
			}
			//分普通剧组用户和客服两种情况
			if (loginUserType == 3) {//如果是普通剧组用户，查询是否有新的客服反馈
				List<FeedBackReplyModel> unReadReplyList = this.feedbackService.queryKefuReplyInfo(userId);
				if (unReadReplyList != null && unReadReplyList.size() > 0) {
					hasUnreadFeedback = true;
				}
			} else {//如果是客服，查询是否有未回复的用户反馈
				List<Map<String, Object>> feedbackUserList = this.feedbackService.queryFeedBackUserList(userId, loginUserType, null, new FeedbackFilter());
				for (Map<String, Object> feedbackUserMap : feedbackUserList) {
					boolean hasNewStatus = (Boolean) feedbackUserMap.get("hasNewStatus");
					if (hasNewStatus) {
						hasUnreadFeedback = true;
						break;
					}
				}
			}
			
			
			resultMap.put("hasUnreadMessage", hasUnreadMessage);
			resultMap.put("hasUnreadFeedback", hasUnreadFeedback);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取消息列表失败", e);
			throw new IllegalArgumentException("未知异常，获取消息列表失败", e);
		}
		return resultMap;
	}
}
