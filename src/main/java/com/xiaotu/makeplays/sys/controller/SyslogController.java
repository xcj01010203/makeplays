package com.xiaotu.makeplays.sys.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.sys.filter.SyslogFilter;
import com.xiaotu.makeplays.sys.model.SysLogDataModel;
import com.xiaotu.makeplays.sys.service.SysLogService;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.IpUtil;
import com.xiaotu.makeplays.utils.Page;

@Controller
@RequestMapping("/syslogManager")
public class SyslogController {
	
	Logger logger = LoggerFactory.getLogger(SyslogController.class);
	
	@Autowired
	private SysLogService sysLogService;
	
	/**
	 * 跳转到日志管理页面
	 * @return
	 */
	@RequestMapping("/toSyslogListPage")
	public ModelAndView goSyslog(){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("sys/syslogList");
		return mv;
	}
	
	/**
	 * 查询日志信息
	 * @param filter
	 * @param page
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/querySyslogList")
	public Map<String,Object> querySyslogList(SyslogFilter filter, Integer pageSize, Integer pageNo){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			Page page = new Page();
			page.setPagesize(pageSize);
			page.setPageNo(pageNo);
			List<SysLogDataModel> list = this.sysLogService.querySyslogList(page, filter);
			resultMap.put("resultList", list);
		} catch (Exception e) {
			success = false;
	        message = "未知异常，查询日志信息失败";
	        logger.error(message, e);
		}
        resultMap.put("success", success);
        resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 根据ip获取所属地区
	 * @param ip
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getAddrByIp")
	public Map<String,Object> getAddrByIp(String ip){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			resultMap.put("data", IpUtil.getIpArea(ip));
		} catch (Exception e) {
			success = false;
	        message = "未知异常，根据ip获取所属地区失败";
	        logger.error(message, e);
		}
        resultMap.put("success", success);
        resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 下载日志文件
	 * @param response
	 * @param storePath 存储路径
	 * @param fileName	文件名
	 * @return
	 */
	@RequestMapping("/downLoadLogFile")
	@ResponseBody
	public void downLoadLogFile(HttpServletResponse response, String storePath, String fileName) {
		FileUtils.downloadFile(response, storePath, fileName);
	}

}
