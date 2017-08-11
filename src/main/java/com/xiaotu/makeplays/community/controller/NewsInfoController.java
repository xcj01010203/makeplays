package com.xiaotu.makeplays.community.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.community.model.NewsInfoModel;
import com.xiaotu.makeplays.community.service.NewsInfoService;
import com.xiaotu.makeplays.utils.BaseController;

@Controller
@RequestMapping("/appIndex")
public class NewsInfoController extends BaseController{

	@Autowired
	private NewsInfoService newsInfoServicde;
	
	/**
	 * 跳转到资讯页面
	 * @param newsInfoId
	 * @return
	 */
	@RequestMapping("/toNewsInfoPage")
	public ModelAndView toNewsInfoPage(String newsInfoId) {
		ModelAndView mv = new ModelAndView("/newsInfo/newsInfo");
		//方便测试，写死一个id
		if (StringUtils.isNotBlank(newsInfoId)) {
			mv.addObject("newsInfoId", newsInfoId);
		}
		return mv;
	}
	
	/**
	 * 根据id获取资讯详情
	 * @param newsInfoId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getNewsContent")
	public Map<String, Object> getNewsContent(HttpServletRequest request, String newsInfoId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;

		try {
			if (StringUtils.isBlank(newsInfoId)) {
				throw new IllegalArgumentException("请选择要查看的资讯！");
			}
			
			NewsInfoModel model = newsInfoServicde.queryNewsInfoById(newsInfoId);
			resultMap.put("data", model);
			
			this.sysLogService.saveSysLogForApp(request, "查询资讯详情", 1, NewsInfoModel.TABLE_NAME, newsInfoId, 0);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
		} catch (Exception e) {
			message = "未知错误！";
			success = false;
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
}
