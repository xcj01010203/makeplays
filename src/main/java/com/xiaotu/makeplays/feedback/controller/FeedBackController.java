package com.xiaotu.makeplays.feedback.controller;

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
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.feedback.model.FeedBackReplyModel;
import com.xiaotu.makeplays.feedback.model.FeedbackModel;
import com.xiaotu.makeplays.feedback.service.FeedbackService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.controller.filter.FeedbackFilter;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.model.constants.UserClientType;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.DateUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 用户反馈相关接口
 * @author xuchangjian 2016-10-10下午4:43:20
 */
@Controller
@RequestMapping("/feedbackManager")
public class FeedBackController extends BaseController {
	
	Logger logger = LoggerFactory.getLogger(FeedBackController.class);
	
	@Autowired
	private FeedbackService feedbackService;
	
	private static int terminal = UserClientType.PC.getValue();
	
	/**
	 * 跳转到用户反馈页面
	 */
	@RequestMapping("/toFeedbackPage")
	public ModelAndView toFeedbackPage(HttpServletRequest request, String userId){
		ModelAndView mv = new ModelAndView("/usercenter/feedback");
		int loginUserType = this.getSessionUserType(request);
		if(loginUserType == 2 || loginUserType == 4) {//客服
			mv.setViewName("/usercenter/feedbackManage");
		}
		return mv;
	}

	/**
	 * 保存用户反馈信息
	 * @param userId
	 * @param message
	 * @param contact
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveFeedBackInfo")
	public Map<String, Object> saveFeedBackInfo(HttpServletRequest request, String message, String contact) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String operateMessage = "";
		
		try {
			String userId = this.getLoginUserId(request);
			if (StringUtils.isBlank(message)) {
				throw new IllegalArgumentException("请填写反馈意见");
			}
			
			FeedbackModel feedBackInfo = new FeedbackModel();
			feedBackInfo.setId(UUIDUtils.getId());
			feedBackInfo.setMessage(message);
			feedBackInfo.setUserId(userId);
			feedBackInfo.setContact(contact);
			feedBackInfo.setClientType(terminal);
			feedBackInfo.setCreateTime(new Date());
			
			this.feedbackService.addOne(feedBackInfo);
			
			this.sysLogService.saveSysLog(request, "保存用户反馈", terminal, FeedbackModel.TABLE_NAME, feedBackInfo.getId(), SysLogOperType.INSERT.getValue());
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			operateMessage = ie.getMessage();
		} catch (Exception e) {
			success = false;
			operateMessage = "未知异常，反馈失败";
			logger.error(operateMessage, e);
			this.sysLogService.saveSysLog(request, "保存用户反馈失败：" + e.getMessage(), terminal, FeedbackModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", operateMessage);
		return resultMap;
	}
	
	/**
	 * 查询客服能看到的反馈用户列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryFeedBackUserList")
	public Map<String, Object> queryFeedBackUserList(HttpServletRequest request, Integer pageNo, Integer pageSize, FeedbackFilter filter) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		try {
			if (!StringUtils.isBlank(filter.getEndTime())) {
				filter.setEndTime(DateUtils.getBeforeOrAfterDayDate(filter.getEndTime(), 1, null));
			}
			
			String userId = this.getLoginUserId(request);
			int loginUserType = this.getSessionUserType(request);
			
			Page page = null;
			if(pageNo != null && pageSize != null) {
				page = new Page();
				page.setPageNo(pageNo);
				page.setPagesize(pageSize);
			}
			
			resultMap.put("result", this.feedbackService.queryFeedBackUserList(userId, loginUserType, page, filter));
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询反馈用户列表失败";
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询用户的意见反馈列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryUserFeedBackList")
	public Map<String, Object> queryUserFeedBackList(HttpServletRequest request, String userId, Integer pageNo, Integer pageSize) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		try {
			if(StringUtils.isBlank(userId)) {
				userId = this.getLoginUserId(request);
			}
			
			Page page = null;
			if(pageNo != null && pageSize != null) {
				page = new Page();
				page.setPageNo(pageNo);
				page.setPagesize(pageSize);
			}
			int loginUserType = this.getSessionUserType(request);
			resultMap.put("result", this.feedbackService.fetchUserFeedBackList(userId, page, loginUserType));
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询用户的意见反馈列表失败";
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 保存回复信息
	 * @param request
	 * @param feedBackId 意见反馈ID
	 * @param reply 回复内容
	 * @param aimUserId 被回复用户ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveReplyInfo")
	public Map<String, Object> saveReplyInfo(HttpServletRequest request, String feedBackId, String reply, String aimUserId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		try {
			UserInfoModel userInfo = this.getSessionUserInfo(request);
			int loginUserType = this.getSessionUserType(request);
			
			//保存回复内容，并发送消息
			this.feedbackService.replyFeedBack(userInfo, loginUserType, terminal, feedBackId, reply, aimUserId);
			
			//返回所有回复列表
			List<Map<String, Object>> replyList = this.feedbackService.queryReplyById(feedBackId);
			resultMap.put("replyList", replyList);

			this.sysLogService.saveSysLog(request, "保存回复信息", terminal, FeedBackReplyModel.TABLE_NAME, feedBackId, SysLogOperType.INSERT.getValue());
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常，保存回复信息失败";
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "保存回复信息失败：" + e.getMessage(), terminal, FeedBackReplyModel.TABLE_NAME, feedBackId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
}
