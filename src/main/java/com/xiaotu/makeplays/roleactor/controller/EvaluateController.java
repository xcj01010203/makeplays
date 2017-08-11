package com.xiaotu.makeplays.roleactor.controller;

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

import com.xiaotu.makeplays.roleactor.model.EvaluateInfoModel;
import com.xiaotu.makeplays.roleactor.model.EvtagInfoModel;
import com.xiaotu.makeplays.roleactor.model.constants.EvtagType;
import com.xiaotu.makeplays.roleactor.service.EvaluateService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;

/**
 * 演员评价
 * @author xuchangjian 2016-8-8上午10:46:19
 */
@Controller
@RequestMapping("/evaluateManager")
public class EvaluateController extends BaseController {
	
	Logger logger = LoggerFactory.getLogger(EvaluateController.class);
	
	private final int terminal = Constants.TERMINAL_PC;

	@Autowired
	private EvaluateService evaluateService;
	
	/**
	 * 评价演员
	 * @param request
	 * @param score	得分
	 * @param blackTagIds	黑标签ID，多个用逗号隔开
	 * @param redTagIds	红标签ID，多个用逗号隔开
	 * @param comment	评语
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/evaluateActor")
	public Map<String, Object> evaluateActor(HttpServletRequest request, String actorId, Integer score, String evatagIds, String comment) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(actorId)) {
				throw new IllegalArgumentException("请选择待评价的人员");
			}
			
			//从sessoin取当前登录用户
			UserInfoModel userInfo = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);
			String crewId = this.getCrewId(request);
			
			this.evaluateService.evaluteActor(userInfo, crewId, actorId, score, evatagIds, comment);
			
			this.sysLogService.saveSysLog(request, "评价演员", terminal, EvaluateInfoModel.TABLE_NAME, actorId, 1);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";

			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "评价演员失败：" + e.getMessage(), terminal, EvaluateInfoModel.TABLE_NAME, actorId, SysLogOperType.ERROR.getValue());
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询评价标签列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryEvaluateTagList")
	public Map<String, Object> queryEvaluateTagList(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			List<EvtagInfoModel> evTagList = this.evaluateService.queryEvtagList(crewId);
			
			List<EvtagInfoModel> redTagList = new ArrayList<EvtagInfoModel>();
			List<EvtagInfoModel> blackTagList = new ArrayList<EvtagInfoModel>();
			
			for (EvtagInfoModel evtagInfo : evTagList) {
				int tagType = evtagInfo.getTagType();
				if (tagType == EvtagType.BlackTag.getValue()) {
					blackTagList.add(evtagInfo);
				}
				if (tagType == EvtagType.RedTag.getValue()) {
					redTagList.add(evtagInfo);
				}
			}
			
			resultMap.put("blackTagList", blackTagList);
			resultMap.put("redTagList", redTagList);
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常，获取评价标签信息失败";

			logger.error("未知异常，获取评价标签信息失败", e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
}
