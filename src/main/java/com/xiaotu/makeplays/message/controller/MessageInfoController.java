package com.xiaotu.makeplays.message.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.xiaotu.makeplays.message.controller.filter.MessageInfoFilter;
import com.xiaotu.makeplays.message.model.MessageInfoModel;
import com.xiaotu.makeplays.message.model.constants.MessageInfoStatus;
import com.xiaotu.makeplays.message.service.MessageInfoService;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.Page;

/**
 * 消息
 * @author xuchangjian 2016-9-24上午11:18:14
 */
@Controller
@RequestMapping("/messageInfoManager")
public class MessageInfoController extends BaseController {

	Logger logger = LoggerFactory.getLogger(MessageInfoController.class);
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	private MessageInfoService messageInfoService;
	
	/**
	 * 跳转到消息页面
	 */
	@RequestMapping("/toMessagePage")
	public ModelAndView toMessagePage(){
		ModelAndView mv = new ModelAndView("/usercenter/messageList");
		return mv;
	}
	
	/**
	 * 获取消息列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryMessageList")
	public Map<String, Object> queryMessageList(HttpServletRequest request, Integer pagesize, Integer pageNo, MessageInfoFilter filter) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			
			String crewId = this.getCrewId(request);
			UserInfoModel loginUserInfo = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);
			
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("crewId", crewId);
			conditionMap.put("receiverId", loginUserInfo.getUserId());
			
			Page page = null;
			if(pagesize != null && pageNo != null) {
				page = new Page();
				page.setPagesize(pagesize);
				page.setPageNo(pageNo);
			}
			
			List<MessageInfoModel> messageList = this.messageInfoService.queryManyByMutiCondition(conditionMap, page, filter);
			
			List<Map<String, Object>> messageMapList = new ArrayList<Map<String, Object>>();
			for (MessageInfoModel messageInfo : messageList) {
				Map<String, Object> messageMap = new HashMap<String, Object>();
				messageMap.put("id", messageInfo.getId());
				messageMap.put("title", messageInfo.getTitle());
				messageMap.put("content", messageInfo.getContent());
				messageMap.put("type", messageInfo.getType());
				messageMap.put("status", messageInfo.getStatus());
				messageMap.put("remindTime", this.sdf1.format(messageInfo.getRemindTime()));
				messageMap.put("createTime", this.sdf1.format(messageInfo.getCreateTime()));
				
				messageMapList.add(messageMap);
			}
			
			resultMap.put("messageList", messageMapList);
			resultMap.put("totalCount", page.getTotal());
			resultMap.put("pageCount", page.getPageCount());
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常，获取消息列表失败";
			logger.error(message, e);
		}
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 已阅消息
	 * @param messageId	消息ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/readMessage")
	public Map<String, Object> readMessage(String messageId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			if (StringUtils.isBlank(messageId)) {
				throw new IllegalArgumentException("请提供消息ID");
			}
			
			MessageInfoModel messageInfo = this.messageInfoService.queryById(messageId);
			messageInfo.setStatus(MessageInfoStatus.HasRead.getValue());
			messageInfo.setIsNew(false);
			
			this.messageInfoService.updateOne(messageInfo);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常，操作失败";
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 已阅消息
	 * @param messageIds 消息ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/readMultiMessage")
	public Map<String, Object> readMultiMessage(HttpServletRequest request, String messageIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			String crewId = this.getCrewId(request);
			String userId = this.getLoginUserId(request);
			this.messageInfoService.readMultiMessage(crewId, userId, messageIds);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(ie.getMessage(), ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，已阅消息失败";
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询未读消息数量,同时查询新消息数量,用于右下角弹出消息提醒数量
	 * 新消息定义：用户未点进去看过的未读消息，即isNew=1 and status=0
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryUnReadMessageNum")
	public Map<String, Object> queryUnReadMessageNum(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			String crewId = this.getCrewId(request);
			UserInfoModel loginUserInfo = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);
			
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("crewId", crewId);
			conditionMap.put("receiverId", loginUserInfo.getUserId());
			conditionMap.put("status", MessageInfoStatus.UnRead.getValue());
			
			List<MessageInfoModel> messageList = this.messageInfoService.queryManyByMutiCondition(conditionMap, null);
			
			Integer num = null;
			if(messageList != null) {
				num = messageList.size();
				
				Integer tipNum = 0;//新消息数量
				for(MessageInfoModel one : messageList) {
					if(one.getIsNew()) {//未查看,指用户没有点进我的消息页面
						tipNum++;
					}
				}
				resultMap.put("tipNum", tipNum);
			}
			resultMap.put("num", num);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询未读消息数量失败";
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询消息明细
	 * @param messageId	消息ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryMessageById")
	public Map<String, Object> queryMessageById(String messageId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			if (StringUtils.isBlank(messageId)) {
				throw new IllegalArgumentException("请提供消息ID");
			}
			
			MessageInfoModel messageInfo = this.messageInfoService.queryById(messageId);
			Map<String, Object> messageMap = new HashMap<String, Object>();
			messageMap.put("id", messageInfo.getId());
			messageMap.put("title", messageInfo.getTitle());
			messageMap.put("content", messageInfo.getContent());
			messageMap.put("type", messageInfo.getType());
			messageMap.put("status", messageInfo.getStatus());
			messageMap.put("createTime", this.sdf1.format(messageInfo.getCreateTime()));
			messageMap.put("remindTime", this.sdf1.format(messageInfo.getRemindTime()));
			resultMap.put("result", messageMap);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询消息明细失败";
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 更新消息查看状态
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateMessageReadStatus")
	public Map<String, Object> updateMessageReadStatus(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			String crewId = this.getCrewId(request);
			UserInfoModel loginUserInfo = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);
			
			messageInfoService.oldMessage(crewId, loginUserInfo.getUserId(), null);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常，更新消息查看状态失败";
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
}
