package com.xiaotu.makeplays.community.controller;

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

import com.xiaotu.makeplays.community.model.SearchTeamInfoModel;
import com.xiaotu.makeplays.community.service.SearchTeamInfoService;
import com.xiaotu.makeplays.mobile.server.community.dto.SearchTeamInfoDto;
import com.xiaotu.makeplays.mobile.server.community.filter.SearchTeamFilter;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.model.constants.UserClientType;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Page;

/**
 * @类名：searchTeamManager.java
 * @作者：李晓平
 * @时间：2017年4月25日 下午4:14:54
 * @描述：寻组
 */
@Controller
@RequestMapping("/searchTeamManager")
public class SearchTeamController extends BaseController {
	
	Logger logger = LoggerFactory.getLogger(SearchTeamController.class);
	
	private int terminal = UserClientType.PC.getValue();
	
	@Autowired
	private SearchTeamInfoService searchTeamService;	
	
	/**
	 * 查询寻组信息列表
	 * @param page 分页参数对象
	 * @param filter 查询条件
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/querySearchTeamList")
	public Map<String, Object> querySearchTeamList(Page page, SearchTeamFilter filter){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {			
			List<SearchTeamInfoDto> searchTeamList = this.searchTeamService.getSearchTeamList(filter, page);
			resultMap.put("searchTeamList", searchTeamList);
			resultMap.put("total", page.getTotal());
			resultMap.put("pageCount", page.getPageCount());
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(ie.getMessage(), ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询寻组信息列表失败";
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 删除寻组信息
	 * @param searchTeamId
	 */
	@ResponseBody
	@RequestMapping("/deleteSearchTeam")
	public Object deleteSearchTeam(HttpServletRequest request, String searchTeamId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			if (StringUtils.isBlank(searchTeamId)) {
				throw new IllegalArgumentException("请选择要删除的寻组信息!");
			}
			
			this.searchTeamService.deleteMulSearchTeamInfo(searchTeamId, null);
			
			this.sysLogService.saveSysLog(request, "删除寻组信息", terminal, SearchTeamInfoModel.TABLE_NAME, searchTeamId, 3);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(ie.getMessage(), ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，删除寻组信息失败";
			logger.error(message, e);

			this.sysLogService.saveSysLog(request, "删除寻组信息失败：" + e.getMessage(), terminal, SearchTeamInfoModel.TABLE_NAME, searchTeamId, SysLogOperType.ERROR.getValue());
			throw new IllegalAccessError("未知错误！");
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
}
