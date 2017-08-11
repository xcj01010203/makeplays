package com.xiaotu.makeplays.mobile.server.feedback;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.feedback.model.FeedbackModel;
import com.xiaotu.makeplays.feedback.model.constants.FeedBackStatus;
import com.xiaotu.makeplays.feedback.service.FeedbackService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.mobile.server.common.BaseFacade;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.sysrole.service.UserRoleMapService;
import com.xiaotu.makeplays.user.controller.filter.FeedbackFilter;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.model.constants.UserType;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.DateUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 用户反馈相关接口
 * @author xuchangjian 2016-10-10下午4:43:20
 */
@Controller
@RequestMapping("/interface/feedbackFacade")
public class FeedbackFacade extends BaseFacade{
	
	Logger logger = LoggerFactory.getLogger(FeedbackFacade.class);
	
	@Autowired
	private FeedbackService feedbackService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRoleMapService userRoleMapService;

	/**
	 * 保存用户反馈信息
	 * @param userId
	 * @param message
	 * @param contact
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveFeedbackInfo")
	public Object saveFeedbackInfo(HttpServletRequest request, String userId, String message, String contact, Integer clientType) {
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkUserValid(userId);
			
			if (StringUtils.isBlank(userId)) {
				throw new IllegalArgumentException("用户ID不能为空");
			}
			if (StringUtils.isBlank(message)) {
				throw new IllegalArgumentException("请填写反馈意见");
			}
			
			FeedbackModel feedbackInfo = new FeedbackModel();
			feedbackInfo.setId(UUIDUtils.getId());
			feedbackInfo.setMessage(message);
			feedbackInfo.setUserId(userId);
			feedbackInfo.setContact(contact);
			feedbackInfo.setCreateTime(new Date());
			feedbackInfo.setClientType(clientType);
			feedbackInfo.setStatus(FeedBackStatus.UnRead.getValue());
			
			this.feedbackService.addOne(feedbackInfo);
			
			this.sysLogService.saveSysLogForApp(request, "保存用户反馈信息", userInfo.getClientType(), FeedbackModel.TABLE_NAME, feedbackInfo.getId(), 1);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，保存返回信息失败", e);
			this.sysLogService.saveSysLogForApp(request, "保存用户反馈信息失败：" + e.getMessage(), 
					userInfo.getClientType(), FeedbackModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，保存返回信息失败", e);
		}
		return null;
	}
	
	/**
	 * 获取反馈用户列表
	 * @param userId	用户ID
	 * @param content	反馈内容
	 * @param userName	反馈人姓名
	 * @param status	状态：1-已读  0-未读
	 * @param startTime	状态更新开始时间
	 * @param endTime	状态更新结束时间
	 * @param pageSize	每页显示条数
	 * @param pageNo	当前页数
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainFeedbackUserList")
	public Object obtainFeedbackUserList(HttpServletRequest request, String userId, String content, String userName, 
			Integer status, String startTime, String endTime, Integer pageSize, Integer pageNo) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = this.userService.queryById(userId);
			
			if (pageSize == null) {
				pageSize = 20;
			}
			if (pageNo == null) {
				pageNo = 1;
			}
			if (!StringUtils.isBlank(endTime)) {
				endTime = DateUtils.getBeforeOrAfterDayDate(endTime, 1, null);
			}
			
			Page page = new Page();
			page.setPagesize(pageSize);
			page.setPageNo(pageNo);
			
			FeedbackFilter filter = new FeedbackFilter();
			filter.setUserName(userName);
			filter.setContent(content);
			filter.setStartTime(startTime);
			filter.setEndTime(endTime);
			filter.setStatus(status);
			filter.setStatus(status);
			
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
			
			List<Map<String, Object>> feedbackUserList = this.feedbackService.queryFeedBackUserList(userId, loginUserType, page, filter);
			int pageCount = page.getPageCount();
			
			resultMap.put("feedbackUserList", feedbackUserList);
			resultMap.put("pageCount", pageCount);
		} catch(Exception e) {
			this.sysLogService.saveSysLogForApp(request, "获取反馈用户列表失败：" + e.getMessage(), 
					userInfo.getClientType(), FeedbackModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			
			logger.error("未知异常，获取反馈用户列表失败", e);
			throw new IllegalArgumentException("未知异常，获取反馈用户列表失败", e);
		}
		
		return resultMap;
	}
	
	/**
	 * 获取指定用户反馈信息列表
	 * @param request
	 * @param userId
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainUserFeedbackList")
	public Object obtainUserFeedbackList(HttpServletRequest request, String userId, String feedbackUserId, Integer pageSize, Integer pageNo) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			if (pageSize == null) {
				pageSize = 20;
			}
			if (pageNo == null) {
				pageNo = 1;
			}
			Page page = new Page();
			page.setPagesize(pageSize);
			page.setPageNo(pageNo);
			
			userInfo = this.userService.queryById(userId);
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
			
			
			UserInfoModel feedbackUserInfo = this.userService.queryById(feedbackUserId);
			List<Map<String, Object>> feedbackList = this.feedbackService.fetchUserFeedBackList(feedbackUserId, page, loginUserType);
			resultMap.put("feedbackList", feedbackList);
			resultMap.put("userName", feedbackUserInfo.getRealName());
			resultMap.put("phone", feedbackUserInfo.getPhone());
			resultMap.put("pageCount", page.getPageCount());
		} catch(Exception e) {
			this.sysLogService.saveSysLogForApp(request, "获取用户反馈列表失败：" + e.getMessage(), 
					userInfo.getClientType(), FeedbackModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			
			logger.error("未知异常，获取反馈用户列表失败", e);
			throw new IllegalArgumentException("未知异常，获取用户反馈列表失败", e);
		}
		
		return resultMap;
	}
	
	/**
	 * 回复反馈信息
	 * @param request
	 * @param userId
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/replyFeedback")
	public Object replyFeedback(HttpServletRequest request, String userId, String feedbackId, String reply, Integer clientType) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			if (StringUtils.isBlank(reply)) {
				throw new IllegalArgumentException("请填写回复内容");
			}
			
			userInfo = this.userService.queryById(userId);
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
			
			FeedbackModel feedbackInfo = this.feedbackService.queryById(feedbackId);
			
			//保存回复内容，并发送消息
			this.feedbackService.replyFeedBack(userInfo, loginUserType, clientType, feedbackId, reply, feedbackInfo.getUserId());
			
			//返回所有回复列表
			List<Map<String, Object>> replyList = this.feedbackService.queryReplyById(feedbackId);
			resultMap.put("replyList", replyList);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch(Exception e) {
			this.sysLogService.saveSysLogForApp(request, "回复反馈信息失败：" + e.getMessage(), 
					userInfo.getClientType(), FeedbackModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			
			logger.error("未知异常，回复反馈信息失败", e);
			throw new IllegalArgumentException("未知异常，回复反馈信息失败", e);
		}
		
		return resultMap;
	}
}
