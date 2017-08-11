package com.xiaotu.makeplays.mobile.server.community;

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

import com.xiaotu.makeplays.community.model.ApplyCaseModel;
import com.xiaotu.makeplays.community.service.ApplyCaseService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Page;

/**
 * 百晓生系统中的应用案例
 * @author xuchangjian 2017-5-3下午3:14:34
 */
@Controller
@RequestMapping("/interface/applyCaseFacade")
public class ApplyCaseFacade extends BaseController {


	Logger logger = LoggerFactory.getLogger(ApplyCaseFacade.class);
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	
	
	@Autowired
	private ApplyCaseService applyCaseService;
	
	/**
	 * 获取案例列表
	 * @param pageSize
	 * @param pageNo
	 * @param title
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainApplyCaseList")
	public Object obtainApplyCaseList(Integer pageSize, Integer pageNo, String title) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
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
			
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			if (!StringUtils.isBlank(title)) {
				conditionMap.put("title", title);
			}
			List<ApplyCaseModel> applyCaseList = this.applyCaseService.queryManyByMutiCondition(conditionMap, page);
			
			List<Map<String, Object>> applyCaseMapList = new ArrayList<Map<String, Object>>();
			for (ApplyCaseModel applyCaseInfo : applyCaseList) {
				Map<String, Object> applyCaseMap = new HashMap<String, Object>();
				applyCaseMap.put("id", applyCaseInfo.getId());
				applyCaseMap.put("title", applyCaseInfo.getTitle());
				applyCaseMap.put("introduction", applyCaseInfo.getIntroduction());
				applyCaseMap.put("createTime", this.sdf1.format(applyCaseInfo.getCreatetime()));
				applyCaseMap.put("url", "http://v1.moonpool.com.cn/" + applyCaseInfo.getSrcurl());
				
				applyCaseMapList.add(applyCaseMap);
			}
			
			resultMap.put("applyCaseList", applyCaseMapList);
			resultMap.put("pageCount", page.getPageCount());
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage());
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException("未知异常，获取案例列表失败！", e);
		}
		return resultMap;
	}
}
