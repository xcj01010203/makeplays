package com.xiaotu.makeplays.mobile.server.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.mobile.server.common.BaseFacade;
import com.xiaotu.makeplays.sys.model.AndroidVersionInfoModel;
import com.xiaotu.makeplays.sys.service.AndroidVersionInfoService;
import com.xiaotu.makeplays.utils.FileUtils;

/**
 * 安卓版本信息
 * @author xuchangjian 2017-4-13下午4:57:57
 */
@Controller
@RequestMapping("/interface/androidVersionInfoFacade")
public class AndroidVersionInfoFacade extends BaseFacade {
	
	Logger logger = LoggerFactory.getLogger(AndroidVersionInfoFacade.class);

	@Autowired
	private AndroidVersionInfoService androidVersionInfoService;
	
	/**
	 * 校验是否有更新
	 * @param versionNo
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainNewestVersionInfo")
	public Object obtainNewestVersionInfo() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			List<AndroidVersionInfoModel> versionList = this.androidVersionInfoService.queryVersionList(null);
			
			if (versionList != null && versionList.size() > 0) {
				AndroidVersionInfoModel androidVersinInfo = versionList.get(0);
				
				resultMap.put("versionNo", androidVersinInfo.getVersionNo());
				resultMap.put("versionName", androidVersinInfo.getVersionName());
				resultMap.put("updateLog", androidVersinInfo.getUpdateLog());
				resultMap.put("size", FileUtils.getFileSize(androidVersinInfo.getStorePath()) * 1024);
				
				String fileName = "makeplay_" + androidVersinInfo.getVersionName() + ".apk";
				resultMap.put("url", FileUtils.genDownloadPath(androidVersinInfo.getStorePath(), fileName));
			}
			
		} catch(Exception e) {
			logger.error("未知异常，检查新版本失败", e);
			throw new IllegalArgumentException("未知异常，检查新版本失败", e);
		}
		return resultMap;
	}
}
