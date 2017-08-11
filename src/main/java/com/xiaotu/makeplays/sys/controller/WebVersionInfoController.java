package com.xiaotu.makeplays.sys.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sun.star.lang.IllegalArgumentException;
import com.xiaotu.makeplays.sys.model.WebVersionInfoModel;
import com.xiaotu.makeplays.sys.service.WebVersionInfoService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.CookieUtil;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * @类名：WebVersionInfoController.java
 * @作者：李晓平
 * @时间：2017年6月14日 上午11:22:55
 * @描述：web版本升级内容管理
 */
@Controller
@RequestMapping("/webVersionInfoManager")
public class WebVersionInfoController extends BaseController{
	
	Logger logger = LoggerFactory.getLogger(WebVersionInfoController.class);
	
	private final int terminal = Constants.TERMINAL_PC;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private WebVersionInfoService webVersionInfoService;
	
	private final int maxAge = 7 * 24 * 60 * 60;
	
	/**
	 * 跳转到web版本管理页面
	 * @param request
	 * @return
	 */
	@RequestMapping("/toWebVersionListPage")
	public ModelAndView toWebVersionListPage(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("/version/webVersionList");
		
		return mv;
	}
	
	/**
	 * 分页查询web版本信息列表
	 * @param request
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryWebVersionList")
	public Map<String, Object> queryWebVersionList(HttpServletRequest request, Integer pageNo, Integer pageSize){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			Page page = null;
			if (pageNo != null) {
				page = new Page();
				page.setPageNo(pageNo);
				page.setPagesize(pageSize);
			}
			
			//调用方法返回版本信息列表
			List<WebVersionInfoModel> list = this.webVersionInfoService.queryVersionList(page);
			resultMap.put("webVersionList", list);
		} catch (Exception e) {
			message = "未知异常，查询web版本信息列表失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 保存或更新web版本信息
	 * @param request
	 * @param id
	 * @param versionName 版本名称
	 * @param insideUpdateLog 内部更新日志
	 * @param userUpdateLog 用户更新日志
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveWebVersionInfo")
	public Map<String, Object> saveWebVersionInfo(HttpServletRequest request,
			String id, String versionName, String insideUpdateLog, String userUpdateLog) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(versionName)) {
				throw new IllegalArgumentException("请填写版本名称");
			}
			if (StringUtils.isBlank(insideUpdateLog)){
				throw new IllegalArgumentException("请填写内部更新日志");
			}
			if (StringUtils.isBlank(insideUpdateLog)){
				throw new IllegalArgumentException("请填写用户更新日志");
			}
			
			if (StringUtils.isBlank(id)) {
				WebVersionInfoModel model = new WebVersionInfoModel();
				model.setVersionName(versionName);
				model.setInsideUpdateLog(insideUpdateLog);
				model.setUserUpdateLog(userUpdateLog);
				id = UUIDUtils.getId();
				//新增 
				model.setId(id);
				model.setCreateTime(new Date());
				
				//调用保存方法
				this.webVersionInfoService.addOne(model);
			}else {
				//更新版本信息
				//根据id查询出版本信息
				WebVersionInfoModel model = this.webVersionInfoService.queryById(id);
				model.setVersionName(versionName);
				model.setInsideUpdateLog(insideUpdateLog);
				model.setUserUpdateLog(userUpdateLog);
				
				//调用更新方法
				this.webVersionInfoService.updateOne(model);
			}
		
			resultMap.put("id", id);
			message = "保存成功";
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，保存web版本信息失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 根据版本信息id查询版本的详细信息
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryWebVersionInfo")
	public Map<String, Object> queryWebVersionInfo(HttpServletRequest request, String id){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(id)) {
				throw new IllegalArgumentException("请选择要查看的版本");
			}
			
			//调用方法
			WebVersionInfoModel webVersionInfo = this.webVersionInfoService.queryById(id);
			
			resultMap.put("result", webVersionInfo);
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，查询失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * web版本升级提醒
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/remindWebVersion")
	public Map<String, Object> remindWebVersion(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		boolean isRemind = false; //标识是否提醒
		try {
			//获取用户登录时间cookie
			Cookie cookie = CookieUtil.getCookieByName(request, Constants.COOKIE_USER_LOGINTIME);
			if(cookie != null) {
				//查询最新版本信息
				WebVersionInfoModel webVersionInfo = this.webVersionInfoService.queryNewVersion();
				if(webVersionInfo != null) {
					//用户最后一次登陆时间早于版本升级时间
					if(cookie.getValue().compareTo(sdf.format(webVersionInfo.getCreateTime())) < 0) {
						isRemind = true;
						resultMap.put("webVersionInfo", webVersionInfo);
					}
				}
			}
			//生成新的登录时间cookie
			CookieUtil.addCookie(response, Constants.COOKIE_USER_LOGINTIME, sdf.format(new Date()), maxAge);
			resultMap.put("isRemind", isRemind);
		} catch (Exception e) {
			message = "未知异常，web版本升级提醒失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
}
