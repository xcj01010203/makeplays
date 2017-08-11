package com.xiaotu.makeplays.sys.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.sun.star.lang.IllegalArgumentException;
import com.xiaotu.makeplays.sys.model.AndroidVersionInfoModel;
import com.xiaotu.makeplays.sys.service.AndroidVersionInfoService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 安卓版本信息
 * @author xuchangjian 2017-4-13下午4:56:29
 */
@Controller
@RequestMapping("/androidVersionInfoManager")
public class AndroidVersionInfoController extends BaseController {

	Logger logger = LoggerFactory.getLogger(AndroidVersionInfoModel.class);
	
	private final int terminal = Constants.TERMINAL_PC;

	@Autowired
	private AndroidVersionInfoService androidVersionInfoService;
	
	
	/**
	 * 跳转到版本控制页面
	 * @param type
	 * @return
	 */
	@RequestMapping("/toAppVersionListPage")
	public ModelAndView toAppVersionListPage(Integer type) {
		ModelAndView mv = new ModelAndView();
		if(type == null || type == 1) {
			mv.setViewName("/version/appVersionList");
		} else if (type == 2) {
			mv.setViewName("/version/webVersionList");
		}
		return mv;
	}
	
	/**
	 * 保存或更新App版本信息
	 * @param request
	 * @param id
	 * @param version
	 * @param versionName
	 * @param updateLog
	 * @param file
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveAppVersionInfo")
	public Map<String, Object> saveAppVersionInfo(HttpServletRequest request, String id, Integer versionNo, String versionName, 
					String updateLog){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (versionNo == null) {
				throw new IllegalArgumentException("请填写版本号");
			}
			if (StringUtils.isBlank(versionName)) {
				throw new IllegalArgumentException("请填写版本名称");
			}
			if (StringUtils.isNotBlank(updateLog) && updateLog.length()>500) {
				throw new IllegalArgumentException("版本更新日志填写长度不能超过500个字");
			}
			
			if (StringUtils.isBlank(id)) {
				AndroidVersionInfoModel model = new AndroidVersionInfoModel();
				model.setVersionNo(versionNo);
				model.setVersionName(versionName);
				model.setUpdateLog(updateLog);
				id = UUIDUtils.getId();
				//新增 
				model.setId(id);
				model.setCreateTime(new Date());
				
				//调用保存方法
				this.androidVersionInfoService.addOne(model);
			}else {
				//更新版本信息
				//根据id查询出版本信息
				AndroidVersionInfoModel model = this.androidVersionInfoService.queryById(id);
				model.setVersionNo(versionNo);
				model.setVersionName(versionName);
				model.setUpdateLog(updateLog);
				
				//调用更新方法
				this.androidVersionInfoService.updateOne(model);
			}
		
			resultMap.put("id", id);
			message = "保存成功";
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，保存版本信息失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 分页查询版本信息列表
	 * @param request
	 * @param page
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryAppVersionList")
	public Map<String, Object> queryAppVersionList(HttpServletRequest request, Integer pageNo, Integer pageSize){
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
			List<AndroidVersionInfoModel> list = this.androidVersionInfoService.queryVersionList(page);
			for (AndroidVersionInfoModel model : list) {
				long size = model.getSize();
				double sizeDouble = (double) size / 1024;
				model.setSize((long)sizeDouble);
			}
			resultMap.put("appVersionList", list);
			message = "查询成功";
		} catch (Exception e) {
			message = "未知异常，查询列表失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 
	 * 根据版本信息id查询版本的详细信息
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryAppVersionInfo")
	public Map<String, Object> queryAppVersionInfo(HttpServletRequest request, String id){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(id)) {
				throw new IllegalArgumentException("请选择要查看的版本");
			}
			
			//调用方法
			AndroidVersionInfoModel model = this.androidVersionInfoService.queryById(id);
			
			resultMap.put("model", model);
			
			//拼接APP名称
			resultMap.put("appName", "makeplaysApp");
			message = "查询成功";
		}catch(IllegalArgumentException ie) {
			message  =ie.getMessage();
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
	 * 上传APP文件
	 * @param request
	 * @param id
	 * @param file
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/uploadAppFile")
	public Map<String, Object> uploadAppFile(HttpServletRequest request, String appVersionId, MultipartFile file){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (file == null) {
				throw new IllegalArgumentException("请选择需要上传的文件");
			}
			if (StringUtils.isBlank(appVersionId)) {
				throw new IllegalArgumentException("请选择需要保存文件的版本");
			}
			
			//根据id查询出版本信息
			AndroidVersionInfoModel model = this.androidVersionInfoService.queryById(appVersionId);
			
			//将APP上传到服务器上
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String baseStorePath = properties.getProperty("fileupload.path");
			String storePath = baseStorePath + "apk/";
			
			
			//判断以前是否上传过APK文件，如果上传过，则删除以前的旧文件
			String path = model.getStorePath();
			if (StringUtils.isNotBlank(path)) {
				//以前上传过文件。删除旧文件
				FileUtils.deleteFile(model.getStorePath());
			}
			//把附件上传到服务器
            Map<String, String> fileMap = FileUtils.uploadFile(file, false, storePath);
            String hdStorePath = fileMap.get("storePath");
            String fileStoreName = fileMap.get("fileStoreName");
			String size = fileMap.get("size");
			
			model.setSize(Long.parseLong(size));
			model.setStorePath( hdStorePath+fileStoreName);
			
			//更新新文件
			this.androidVersionInfoService.updateOne(model);
			
			message  = "上传文件成功";
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，上传失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
}
