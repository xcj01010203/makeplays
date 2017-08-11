package com.xiaotu.makeplays.crewPicture.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
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

import com.xiaotu.makeplays.attachment.model.AttachmentModel;
import com.xiaotu.makeplays.attachment.model.constants.AttachmentType;
import com.xiaotu.makeplays.attachment.service.AttachmentService;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crewPicture.model.CrewPictureInfoModel;
import com.xiaotu.makeplays.crewPicture.service.CrewPictureInfoService;
import com.xiaotu.makeplays.notice.controller.NoticeController;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.PropertiesUitls;


/**
 * 剧照管理的controller
 * @author wanrenyi 2017年2月28日上午9:54:25
 */
@Controller
@RequestMapping("/crewPicture")
public class CrewPictureController extends BaseController{

	Logger logger = LoggerFactory.getLogger(CrewPictureController.class);
	
	private final int terminal = Constants.TERMINAL_PC;
	
	@Autowired
	private CrewPictureInfoService crewPictureInfoService;
	
	@Autowired
	private AttachmentService attachmentService;
	
	/**
	 * 跳转到剧照列表页面
	 * @param request
	 * @return
	 */
	@RequestMapping("/toCrewPicturePage")
	public ModelAndView toCrewPicturePage(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("/crewPicture/crewPictureList");
		
		return mv;
	}
	
	/**
	 * 创建新的相册分组（此方法会创建一个附件包和剧照信息）
	 * @param request
	 * @param pictureGroupName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/savePictureGroup")
	public Map<String, Object> savePictureGroup(HttpServletRequest request, String pictureGroupName, String pictureId, String picturePassword){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			
			String userId = this.getLoginUserId(request);
			
			if (StringUtils.isBlank(pictureGroupName)) {
				throw new IllegalArgumentException("剧照分组名称不能为空！");
			}
			if (pictureGroupName.length()>50) {
				throw new IllegalArgumentException("剧照分组名称过长，请修改后在添加");
			}
			
			//判断当前分组名称是否已经存在
			List<Map<String,Object>> list = this.crewPictureInfoService.queryPictureListByGroupName(crewId, pictureGroupName);
			if (list != null && list.size() == 1) {
				if (StringUtils.isBlank(pictureId)) {
					throw new IllegalArgumentException("该分组名称已经存在，请不要重复添加");
				}else {
					Map<String, Object> map = list.get(0);
					String id = (String) map.get("id");
					if (!pictureId.equals(id)) {
						throw new IllegalArgumentException("该分组名称已经存在，请不要重复添加");
					}
				}
			}else if (list != null && list.size() > 1) {
				throw new IllegalArgumentException("该分组名称已经存在，请不要重复添加");
			}
			
			//调用service方法
			Map<String, Object> map = this.crewPictureInfoService.saveCrewPictureInfo(crewId, pictureId, pictureGroupName, picturePassword, userId);
			
			resultMap.put("idsMap", map);
			
			message = "保存成功！";
			this.sysLogService.saveSysLog(request, "保存剧组分组信息", terminal, AttachmentModel.TABLE_NAME, null, 1);
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，保存失败";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "保存剧组分组信息失败：" + e.getMessage(), terminal, AttachmentModel.TABLE_NAME, null, 6);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 根据剧照分组名称查询附件包及附件信息
	 * @param request
	 * @param groupName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryCrewPictureInfo")
	public Map<String, Object> queryCrewPictureInfo(HttpServletRequest request, String groupName){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			
			if (StringUtils.isBlank(groupName)) {
				throw new IllegalArgumentException("请输入分组名称");
			}
			
			//判断当前分组名称是否已经存在
			String packetId = "";
			String crewPictureId = "";
			List<Map<String,Object>> list = this.crewPictureInfoService.queryPictureListByGroupName(crewId, groupName);
			if (null != list && list.size()>0) {
				Map<String, Object> map = list.get(0);
				packetId = (String) map.get("attpackId");
				crewPictureId = (String) map.get("id");
			}
			
			resultMap.put("packetId", packetId);
			resultMap.put("crewPictureId", crewPictureId);
			message = "查询成功";
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，查询失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 查询剧组中所有的剧照的名称列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryPictureGroupNameList")
	public Map<String, Object> queryPictureGroupNameList(HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			
			//调用service方法
			List<Map<String,Object>> list = this.crewPictureInfoService.queryPictreGroupNameList(crewId);
			resultMap.put("groupNameList", list);
			message = "查询成功";
		} catch (Exception e) {
			message = "未知错误，查询失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 上传剧照
	 * @param request
	 * @param attpackId
	 * @param file
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/uploadCrewPicture")
	public Map<String, Object> uploadCrewPicture(HttpServletRequest request, String attpackId, MultipartFile file){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			
			if (file == null) {
				throw new java.lang.IllegalArgumentException("请提供需要上传的文件");
			}
			
			if (StringUtils.isBlank(attpackId)) {
				throw new IllegalArgumentException("请选择上传文件需要保存的分组");
			}
			
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String baseStorePath = properties.getProperty("fileupload.path");
			String storePath = baseStorePath + "crewPicture/";
			
			//把附件上传到服务器(高清原版)
            Map<String, String> fileMap = FileUtils.uploadFile(file, false, storePath);
			String fileStoreName = fileMap.get("fileStoreName");
			String fileRealName = fileMap.get("fileRealName");
			String hdStorePath = fileMap.get("storePath");
			
			String sdStorePath = "";
			int lastCommaIndex = fileStoreName.length();
			if (fileStoreName.lastIndexOf(".") != -1) {
				lastCommaIndex = fileStoreName.lastIndexOf(".");
			}
			String exceptSuffixName = fileStoreName.substring(0, lastCommaIndex);	//不带后缀的文件名
			String suffix = "";//文件后缀
			if (lastCommaIndex != fileStoreName.length()) {
				suffix = fileStoreName.substring(lastCommaIndex);
			}
			
			sdStorePath = hdStorePath + "sd/" + exceptSuffixName + "_sd" + suffix;
			if (FileUtils.isPicture(hdStorePath+fileStoreName)) {
				BufferedImage newImage = FileUtils.getNewImage(file, null, 200, 200);
				File destFile = new File(sdStorePath);
				FileUtils.makeDir(destFile);
				
				ImageIO.write(newImage, "png", destFile);
			}
			
			int type = AttachmentType.Picture.getValue();	//图片类型
			
			hdStorePath = hdStorePath + fileStoreName;
			
			long size = FileUtils.getFileSize(hdStorePath);
			
			AttachmentModel attachment = this.attachmentService.saveAttachmentInfo(crewId, attpackId, type, fileRealName, hdStorePath, sdStorePath, suffix, size, 0);
			
			resultMap.put("attpackId", attachment.getAttpackId());
			resultMap.put("attachmentId", attachment.getId());
			
			this.sysLogService.saveSysLog(request, "上传剧照", terminal, AttachmentModel.TABLE_NAME, null, 4);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			logger.error("未知异常，上传附件失败", e);
			
			success = false;
			message = "未知异常，上传附件失败";
			this.sysLogService.saveSysLog(request, "上传剧照失败：" + e.getMessage(), terminal, AttachmentModel.TABLE_NAME, null, 6);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 修改图片名称
	 * @param request
	 * @param attachmentId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveAttachmentPictureName")
	public Map<String, Object> saveAttachmentPictureName(HttpServletRequest request, String attachmentId, String attachmentName, String attpackId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			
			if (StringUtils.isBlank(attachmentId)) {
				throw new IllegalArgumentException("请选择需要修改的照片");
			}
			if (StringUtils.isBlank(attachmentName)) {
				throw new IllegalArgumentException("请填写需要更新的图片名称");
			}
			
			//判断当前填写的名称是否已经存在
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", attachmentName);
			map.put("crewId", crewId);
			map.put("attpackId", attpackId);
			List<AttachmentModel> listByName = this.attachmentService.queryManyByMutiCondition(map, null);
			if (listByName != null && listByName.size() == 1) {
				//排除当前附件包
				AttachmentModel model = listByName.get(0);
				if (!model.getId().equals(attachmentId)) {
					throw new IllegalArgumentException("图片名称已存在，请重新填写");
				}
			}else if (listByName != null && listByName.size() > 1) {
				throw new IllegalArgumentException("图片名称已存在，请重新填写");
			}
			
			//根据id查询出附件信息
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("id", attachmentId);
			conditionMap.put("crewId", crewId);
			List<AttachmentModel> list = this.attachmentService.queryManyByMutiCondition(conditionMap, null);
			if (list == null || list.size() == 0) {
				throw new IllegalArgumentException("不存在当前图片信息，请刷新后重试");
			}
			
			AttachmentModel model = list.get(0);
			model.setName(attachmentName);
			this.attachmentService.updateAttachmentInfo(model);
			
			message = "更新成功";
			this.sysLogService.saveSysLog(request, "修改剧照名称", terminal, AttachmentModel.TABLE_NAME, null, 2);
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，更新失败";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "修改剧照名称失败：" + e.getMessage(), terminal, AttachmentModel.TABLE_NAME, null, 6);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 设置分组的封面照片
	 * @param request
	 * @param attachmentId
	 * @param crewPictureId
	 * @return
	 */
	@Deprecated
	@ResponseBody
	@RequestMapping("/saveIndexPictureInfo")
	public Map<String, Object> saveIndexPictureInfo(HttpServletRequest request, String attachmentId, String crewPictureId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			
			if (StringUtils.isBlank(attachmentId)) {
				throw new IllegalArgumentException("请选择要作为封面的图片");
			}
			if (StringUtils.isBlank(crewPictureId)) {
				throw new IllegalArgumentException("请选择要设置的分组");
			}
			
			//根据id查询出剧照信息
			CrewPictureInfoModel model = this.crewPictureInfoService.queryCrewPictureInfoById(crewPictureId, crewId);
			if (model == null) {
				throw new IllegalArgumentException("不存在的分组，请刷新后再设置");
			}
			
			model.setIndexPictureId(attachmentId);
			this.crewPictureInfoService.updateCrewPictureInfo(model);
			
			this.sysLogService.saveSysLog(request, "设置剧照封面", terminal, CrewPictureInfoModel.TABLE_NAME, null, 2);
			message = "设置成功";
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，设置失败";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "设置剧照封面失败：" + e.getMessage(), terminal, CrewPictureInfoModel.TABLE_NAME, null, 6);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 删除剧照分组信息（会判断是删除分组中的照片还是删除分组）
	 * @param request
	 * @param crewPictureId 分组id
	 * @param attachmentIds 附件id字符串（即分组中照片的id），多个id之间以“,”分隔
	 * @param isDeleteCrewPicture 是否删除分组（为true时表示删除分组信息，同时会删除分组中的附件及附件包；为false只删除照片附件不删除分组）
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteCrewPictureInfo")
	public Map<String, Object> deleteCrewPictureInfo(HttpServletRequest request, String crewPictureId, String attachmentIds, Boolean isDeleteCrewPicture){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			
			//判断是删除分组还是删除分组中的照片
			if (isDeleteCrewPicture) {
				//删除分组信息
				if (StringUtils.isBlank(crewPictureId)) {
					throw new IllegalArgumentException("请选择需要删除的分组");
				}
				
				this.crewPictureInfoService.deleteCrewPictureAndAttachment(crewPictureId, crewId);
			}else {
				//删除分组中的照片，不删除分组
				if (StringUtils.isBlank(attachmentIds)) {
					throw new IllegalArgumentException("请选择需要删除的照片");
				}
				
				String[] attachmentIdArr = attachmentIds.split(",");
				for (String attachmentId : attachmentIdArr) {
					//调用方法删除附件
					this.attachmentService.deleteById(attachmentId);
				}
			}
			
			message = "删除成功";
			this.sysLogService.saveSysLog(request, "删除剧照信息", terminal, CrewPictureInfoModel.TABLE_NAME, null, 3);
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，删除失败";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "删除剧照信息失败：" + e.getMessage(), terminal, CrewPictureInfoModel.TABLE_NAME, null, 6);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 查询剧组中的剧照列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryCrewPictureInfoList")
	public Map<String, Object> queryCrewPictureInfoList(HttpServletRequest request, String crewPictureId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			
			List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
			//获取数据
			List<Map<String,Object>> list = this.crewPictureInfoService.queryCrewPictureInfoList(crewId, null);
			//遍历数据，将地址变成预览地址
			for (Map<String, Object> map : list) {
				String id = (String) map.get("id");
				String indexPicturePath = (String) map.get("sdStorePath");
				if (StringUtils.isNotBlank(indexPicturePath)) {
					//生成图片预览地址
					String previewPath = FileUtils.genPreviewPath(indexPicturePath);
					map.remove("sdStorePath");
					map.put("sdStorePath", previewPath);
				}
				//由于此接口同时支持移动到 功能中的剧照列表，所以在此处需要去除当前照片所在的剧照
				if (crewPictureId == null) {
					crewPictureId = "";
				}
				if (!id.equals(crewPictureId)) {
					resultList.add(map);
				}
			}
			
			resultMap.put("crewPictureList", resultList);
			message = "查询成功";
		} catch (Exception e) {
			message = "未知错误，查询失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 查询当前相册中的所有的照片(返回的图片地址为压缩图片的预览地址)
	 * @param request
	 * @param crewPictureId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryAttachmentList")
	public Map<String, Object> queryAttachmentList(HttpServletRequest request, String crewPictureId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			String userId = this.getLoginUserId(request);
			
			if (StringUtils.isBlank(crewPictureId)) {
				throw new IllegalArgumentException("请选择要查看的剧照");
			}
			
			//获取剧照中的所有的图片信息
			List<Map<String,Object>> listByPictureId = this.crewPictureInfoService.queryAttachmentListByPictureId(crewPictureId);
			//处理图片的大小，返回单位为M 
			for (Map<String, Object> map : listByPictureId) {
				String sizeStr = map.get("size")+"";
				if (StringUtils.isNotBlank(sizeStr)&& !sizeStr.equals("null")) {
					Long size = Long.parseLong(sizeStr);
					double sizeLong = (double)size/1024;
					map.put("size", sizeLong);
				}
			}
			
			if (listByPictureId.size() == 1) {
				Map<String, Object> pictureMap = listByPictureId.get(0);
				String attachmentId = (String) pictureMap.get("id");
				if (StringUtils.isBlank(attachmentId)) {
					resultMap.put("attachmentList", null);
				}else {
					resultMap.put("attachmentList", listByPictureId);
				}
			}else {
				resultMap.put("attachmentList", listByPictureId);
			}
			
			boolean isCreateUser = false;
			//获取剧照的信息
			List<Map<String,Object>> list = this.crewPictureInfoService.queryCrewPictureInfoList(crewId, crewPictureId);
			if (null != list && list.size()>0) {
				Map<String, Object> crewPictureInfoMap = list.get(0);
				String sdStorePath = (String) crewPictureInfoMap.get("sdStorePath");
				if (StringUtils.isNotBlank(sdStorePath)) {
					String suffix = "";
					if(sdStorePath.lastIndexOf(".") != -1) {
						suffix = sdStorePath.substring(sdStorePath.lastIndexOf("."), sdStorePath.length());
					}
					String genPreviewPath = FileUtils.genPreviewPath(sdStorePath);
					crewPictureInfoMap.remove("sdStorePath");
					crewPictureInfoMap.put("sdStorePath", genPreviewPath);
					crewPictureInfoMap.put("suffix", suffix);
				}
				//判断是否是本人点击查看详情
				String createUserId = crewPictureInfoMap.get("createUser")+"";
				if (createUserId.equals(userId)) {
					isCreateUser = true;
				}
				
				resultMap.put("isCreateUser", isCreateUser);
				resultMap.put("crewPictureInfo", crewPictureInfoMap);
			}
			
			message = "查询成功";
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，查询失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 移动图片到另外一个相册中（此方法支持同时移动多张图片到同一个剧照中）
	 * @param request
	 * @param attachmentIds 附件id字符串，多个id之间以“,”分割
	 * @param packetId 附件包的id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/moveAttachmentInfo")
	public Map<String, Object> moveAttachmentInfo(HttpServletRequest request, String attachmentIds, String packetId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			if (StringUtils.isBlank(packetId)) {
				throw new IllegalArgumentException("请选择要保存图片的分组");
			}
			if (StringUtils.isBlank(attachmentIds)) {
				throw new IllegalArgumentException("请选择要移动的图片");
			}
			
			List<AttachmentModel> modelList = new ArrayList<AttachmentModel>();
			String[] attacmentIdArr = attachmentIds.split(",");
			//根据id查询出附件信息
			for (String attachmentId : attacmentIdArr) {
				AttachmentModel model = this.attachmentService.queryAttachmentById(attachmentId);
				if (null != model) {
					model.setAttpackId(packetId);
					modelList.add(model);
				}
				//如果当前照片是封面照片需要将相册的封面照片id设置为空
				this.crewPictureInfoService.updateCrewPictureIndexId(attacmentIdArr, crewId);
			}
			
			//批量更新
			if (null != modelList && modelList.size()>0) {
				this.attachmentService.updateAttachmentBatch(modelList);
			}
			this.sysLogService.saveSysLog(request, "移动照片", terminal, CrewPictureInfoModel.TABLE_NAME, attachmentIds, 2);
			message = "操作成功";
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，移动失败";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "移动照片失败：" + e.getMessage(), terminal, CrewPictureInfoModel.TABLE_NAME, attachmentIds, 6);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 下载剧照中的图片（支持只下载指定图片和下载整个剧照分组中所有的图片;两个id不可同时传）
	 * @param request
	 * @param attachmentIds
	 * @param packetId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/downLoadCrewPicture")
	public Map<String, Object> downLoadCrewPicture(HttpServletRequest request, String attachmentIds, String packetId, String packetName){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		
		//定义输入输出流
		FileInputStream in = null;
		ZipOutputStream zipOut = null;
		
		try {
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			String crewName = crewInfo.getCrewName();
			
			if (StringUtils.isNotBlank(attachmentIds) && StringUtils.isNotBlank(packetId)) {
				throw new IllegalArgumentException("图片和剧照信息不能同时下载");
			}
			
			if (StringUtils.isBlank(packetName)) {
				throw new IllegalArgumentException("下载的剧照分组名称不能为空");
			}
			
			//获取存储根路径
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String baseDownloadPath = properties.getProperty("downloadPath");
			String storePath = baseDownloadPath + "crewPicture/pc/" + sdf1.format(new Date()) + "/";	//存储路径
			
			List<File> fileList = new ArrayList<File>();
			
			if (StringUtils.isNotBlank(attachmentIds)) {
				//下载指定图片信息
				String[] attachmentIdArr = attachmentIds.split(",");
				for (String attachmentId : attachmentIdArr) {
					//根据id查询附件信息
					AttachmentModel model = this.attachmentService.queryAttachmentById(attachmentId);
					if (null != model) {
						String hdStorePath = model.getHdStorePath();
						if (StringUtils.isNotBlank(hdStorePath)) {
							File pathFile = new File(hdStorePath);
							fileList.add(pathFile);
						}
					}
				}
			}
			
			if (StringUtils.isNotBlank(packetId)) {
				//下载整个相册内的所有图片
				List<AttachmentModel> list = this.attachmentService.queryAttByPackId(packetId);
				for (AttachmentModel model : list) {
					if (null != model) {
						String hdStorePath = model.getHdStorePath();
						if (StringUtils.isNotBlank(hdStorePath)) {
							File pathFile = new File(hdStorePath);
							fileList.add(pathFile);
						}
					}
				}
			}
			
			//存储压缩图片
			String fileName = "《" + crewName + "》剧组的【"+ packetName +"】剧照" + sdf2.format(new Date());
			String suffix = ".zip";
			File zipFile = new File(storePath + fileName + suffix);
			if (!zipFile.getParentFile().isDirectory()) {
				zipFile.getParentFile().mkdirs();
			}
			
			//导出压缩图片
			zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
			byte[] buf = new byte[1024];
			for (int i = 0; i < fileList.size(); i++) {
				File file = fileList.get(i);  
				in = new FileInputStream(file);  
				zipOut.putNextEntry(new ZipEntry(file.getName()));  
				int len;  
				while ((len = in.read(buf)) > 0) {
					zipOut.write(buf, 0, len);  
				}
				zipOut.closeEntry();  
				in.close();
			}
			
			String downloadPath = storePath + fileName + suffix;
			resultMap.put("downloadPath", downloadPath);
			this.sysLogService.saveSysLog(request, "下载剧照中的图片", terminal, AttachmentModel.TABLE_NAME, null, 5);
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，下载失败";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "下载剧照中的图片失败：" + e.getMessage(), terminal, AttachmentModel.TABLE_NAME, null, 6);
		}finally {
			//关闭资源
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					message = e.getMessage();
					success = false;
					
					logger.error(message, e);
				}
			}
			if (zipOut != null) {
				try {
					zipOut.close();
				} catch (IOException e) {
					message = e.getMessage();
					success = false;
					
					logger.error(message, e);
				}
			}
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 查看当前照片的大图
	 * @param request
	 * @param attachmentId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryHdsPicture")
	public Map<String, Object> queryHdsPicture(HttpServletRequest request, String attachmentId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(attachmentId)) {
				throw new IllegalArgumentException("请选择需要查看的照片");
			}
			
			String hdPrevidewPath = "";
			AttachmentModel model = this.attachmentService.queryAttachmentById(attachmentId);
			if (model != null) {
				String hdStorePath = model.getHdStorePath();
				if (StringUtils.isNotBlank(hdStorePath)) {
					hdPrevidewPath = FileUtils.genPreviewPath(hdStorePath);
				}
			}
			
			resultMap.put("hdPrevidewPath", hdPrevidewPath);
			message = "查询成功";
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，查询失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 根据分组id，获取分组的详细消息
	 * @param request
	 * @param crewPictureId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryPictureInfoById")
	public Map<String, Object> queryPictureInfoById(HttpServletRequest request, String crewPictureId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(crewPictureId)) {
				throw new IllegalArgumentException("请选择要查看的分组");
			}
			
			String crewId = this.getCrewId(request);
			
			CrewPictureInfoModel model = this.crewPictureInfoService.queryCrewPictureInfoById(crewPictureId, crewId);
			 
			resultMap.put("crewPictureInfo", model);
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，查询失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 更新当前分组的密码
	 * @param request
	 * @param crewPictureId
	 * @p aram picturePassword
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updatePicturePassword")
	public Map<String, Object> updatePicturePassword(HttpServletRequest request, String crewPictureId, String picturePassword){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			
			if (StringUtils.isBlank(crewPictureId)) {
				throw new IllegalArgumentException("请选择需要查看的分组");
			}
			
			//根据ID查询出分组的详细信息
			CrewPictureInfoModel model = this.crewPictureInfoService.queryCrewPictureInfoById(crewPictureId, crewId);
			if (model != null) {
				model.setPicturePassword(picturePassword);
				
				//更新数据
				this.crewPictureInfoService.updateCrewPictureInfo(model);
				message = "更新成功";
			}else {
				throw new IllegalArgumentException("当前分组不存在，请刷新本页面后，重新设置");
			}
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，更新失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
}
