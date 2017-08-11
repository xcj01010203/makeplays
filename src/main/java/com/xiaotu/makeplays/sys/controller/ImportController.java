package com.xiaotu.makeplays.sys.controller;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 导入功能
 * @author xuchangjian 2016-10-14上午10:29:53
 */
@Controller
@RequestMapping("/importManager")
public class ImportController {

	/**
	 * 跳转到导入页面
	 * @param uploadUrl	上传链接
	 * @param needIsCover	是否需要显示覆盖控件
	 * @param refreshUrl	刷新url
	 * @param templateUrl	模板下载url
	 * @param isCompareData	是否对比导入数据和数据库中重复数据
	 * @param queryDelete   是否删除原来的数据
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/toImportPage")
	public ModelAndView toImportPage(String uploadUrl, Boolean needIsCover, String refreshUrl, String templateUrl,Boolean isCompareData, Boolean queryDelete) throws IOException {
		ModelAndView mv = new ModelAndView("/import/import");
		
		if (!StringUtils.isBlank(templateUrl)) {
			Resource resource = new ClassPathResource("/config.properties");
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			String serverPath = (String) props.get("server.basepath");
			templateUrl = serverPath + templateUrl;
		}
		
		if(isCompareData == null ){
			isCompareData = false;
		}
		
		mv.addObject("isCompareData", isCompareData);
		mv.addObject("uploadUrl", uploadUrl);
		mv.addObject("needIsCover", needIsCover);
		mv.addObject("refreshUrl", refreshUrl);
		mv.addObject("templateUrl", templateUrl);
		mv.addObject("queryDelete", queryDelete);
		return mv;
	}
}
