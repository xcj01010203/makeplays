package com.xiaotu.makeplays.mobile.server.crewpicture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

import com.xiaotu.makeplays.attachment.dto.AttachmentDto;
import com.xiaotu.makeplays.attachment.model.AttachmentModel;
import com.xiaotu.makeplays.attachment.model.constants.AttachmentType;
import com.xiaotu.makeplays.attachment.service.AttachmentService;
import com.xiaotu.makeplays.crewPicture.model.CrewPictureInfoModel;
import com.xiaotu.makeplays.crewPicture.service.CrewPictureInfoService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.mobile.server.common.BaseFacade;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.StringUtil;

/**
 * @类名：CrewPictureFacade.java
 * @作者：李晓平
 * @时间：2017年6月19日 下午12:11:58
 * @描述：相册管理
 */
@Controller
@RequestMapping("/interface/crewPictureFacade")
public class CrewPictureFacade extends BaseFacade{

	Logger logger = LoggerFactory.getLogger(CrewPictureFacade.class);
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	private CrewPictureInfoService crewPictureInfoService;
	
	@Autowired
	private AttachmentService attachmentService;
	
	/**
	 * 获取相册分组列表
	 * @param request
	 * @param crewId 剧组ID
	 * @param userId 用户ID
	 * @param currentGroupId 当前分组ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainCrewPictureGroupList")
	public Object obtainCrewPictureGroupList(HttpServletRequest request,
			String crewId, String userId, String currentGroupId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			UserInfoModel userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			//获取数据
			List<Map<String,Object>> list = this.crewPictureInfoService.queryCrewPictureInfoList(crewId, null);
			//遍历数据，将地址变成预览地址			
			//由于此接口同时支持 移动到 功能中的剧照列表，所以在此处需要去除当前照片所在的剧照
			if (currentGroupId == null) {
				currentGroupId = "";
			}
			for (Map<String, Object> map : list) {
				String id = (String) map.get("id");
				String indexPicturePath = (String) map.get("sdStorePath");
				map.put("createTime", this.sdf.format(map.get("createTime")));
				if (StringUtils.isNotBlank(indexPicturePath)) {
					//生成图片预览地址
					String previewPath = FileUtils.genPreviewPath(indexPicturePath);
					map.put("sdStorePath", previewPath);
				}
				if (!id.equals(currentGroupId)) {
					resultList.add(map);
				}
			}
			
			resultMap.put("crewPictureGroupList", resultList);
			
			this.sysLogService.saveSysLogForApp(request, "查询相册分组列表", userInfo.getClientType(), CrewPictureInfoModel.TABLE_NAME, null, 0);
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取相册分组列表失败", e);
			throw new IllegalArgumentException("未知异常，获取相册分组列表失败");
		}
		return resultMap;
	}
	
	/**
	 * 获取相册分组明细
	 * @param request
	 * @param crewId 剧组ID
	 * @param userId 用户ID
	 * @param pictureGroupId 相册分组ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainCrewPictureGroupDetail")
	public Object obtainCrewPictureGroupDetail(String crewId, String userId, String pictureGroupId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			if (StringUtils.isBlank(pictureGroupId)) {
				throw new IllegalArgumentException("请选择要查看的分组");
			}
			//分组信息
			CrewPictureInfoModel crewPictureInfo = this.crewPictureInfoService.queryCrewPictureInfoById(pictureGroupId, crewId);
			
			//获取剧照中的所有的图片信息
			List<AttachmentModel> attachmentList = this.attachmentService.queryAttByPackId(crewPictureInfo.getAttpackId());
			List<AttachmentDto> pictureList = new ArrayList<AttachmentDto>();
			
			for (AttachmentModel attachmentInfo : attachmentList) {
				AttachmentDto attachmentDto = new AttachmentDto();
				attachmentDto.setAttachmentId(attachmentInfo.getId());
				attachmentDto.setAttpackId(attachmentInfo.getAttpackId());
				attachmentDto.setName(attachmentInfo.getName());
				attachmentDto.setType(attachmentInfo.getType());
				attachmentDto.setSuffix(attachmentInfo.getSuffix());
				attachmentDto.setLength(attachmentInfo.getLength());
				attachmentDto.setHdPreviewUrl(FileUtils.genPreviewPath(attachmentInfo.getHdStorePath()));
				attachmentDto.setSdPreviewUrl(FileUtils.genPreviewPath(attachmentInfo.getSdStorePath()));
				
				pictureList.add(attachmentDto);
			}
			resultMap.put("id", crewPictureInfo.getId());
			resultMap.put("attpackName", crewPictureInfo.getAttpackName());
			resultMap.put("indexPictureId", crewPictureInfo.getIndexPictureId());
			resultMap.put("createUser", crewPictureInfo.getCreateUser());
			resultMap.put("attpackId", crewPictureInfo.getAttpackId());
			resultMap.put("pictureList", pictureList);			
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取相册分组明细失败", e);
			throw new IllegalArgumentException("未知异常，获取相册分组明细失败");
		}
		return resultMap;
	}
	
	/**
	 * 保存相册分组信息
	 * @param request
	 * @param crewId 剧组ID
	 * @param userId 用户ID
	 * @param pictureGroupId 相册分组ID
	 * @param pictureGroupName 分组名称
	 * @param pictureGroupPassword 分组密码
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveCrewPictureGroupInfo")
	public Object saveCrewPictureGroupInfo(HttpServletRequest request,
			String crewId, String userId, String pictureGroupId,
			String pictureGroupName, String pictureGroupPassword) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = null;
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			
			if(StringUtils.isBlank(pictureGroupName)) {
				throw new IllegalArgumentException("分组名称不能为空！");
			}
			if(pictureGroupName.length() > 50) {
				throw new IllegalArgumentException("分组名称最大长度为50");
			}
			if(StringUtils.isNotBlank(pictureGroupPassword) && pictureGroupPassword.length() > 32) {
				throw new IllegalArgumentException("分组密码最大长度为32");
			}
			
			//判断当前分组名称是否已经存在
			List<Map<String,Object>> list = this.crewPictureInfoService.queryPictureListByGroupName(crewId, pictureGroupName);
			if(list != null && list.size() != 0) {
				if(list.size() == 1) {
					Map<String, Object> map = list.get(0);
					String id = (String) map.get("id");
					if (pictureGroupId == null || !pictureGroupId.equals(id)) {
						throw new IllegalArgumentException("该分组名称已经存在，请不要重复添加");
					}
				} else {
					throw new IllegalArgumentException("该分组名称已经存在，请不要重复添加");
				}
			}
			
			//调用service方法
			Map<String, Object> map = this.crewPictureInfoService.saveCrewPictureInfo(crewId, pictureGroupId, pictureGroupName, pictureGroupPassword, userId);
			
			resultMap.put("pictureGroupId", map.get("pictureId")); //分组ID
			resultMap.put("attpackId", map.get("packetId")); //附件包ID
			
			if(StringUtil.isBlank(pictureGroupId)) {
				this.sysLogService.saveSysLogForApp(request, "新增相册分组信息", userInfo.getClientType(), CrewPictureInfoModel.TABLE_NAME, null, SysLogOperType.INSERT.getValue());
			} else {
				this.sysLogService.saveSysLogForApp(request, "修改相册分组信息", userInfo.getClientType(), CrewPictureInfoModel.TABLE_NAME, null, SysLogOperType.UPDATE.getValue());
			}
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，保存相册分组信息失败", e);
			this.sysLogService.saveSysLogForApp(request, "保存相册分组信息失败：" + e.getMessage(), userInfo.getClientType(), CrewPictureInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，保存相册分组信息失败");
		}
		return resultMap;
	}
	
	/**
	 * 上传剧照
	 * @param request
	 * @param crewId 剧组ID
	 * @param userId 用户ID
	 * @param attpackId 附件包ID
	 * @param file 文件数据
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/uploadCrewPicture")
	public Object uploadCrewPicture(HttpServletRequest request,
			String crewId, String userId, String attpackId,
			MultipartFile file) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = null;
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			
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
			
			this.sysLogService.saveSysLogForApp(request, "上传剧照", userInfo.getClientType(), CrewPictureInfoModel.TABLE_NAME, null, SysLogOperType.INSERT.getValue());
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，上传剧照失败", e);
			this.sysLogService.saveSysLogForApp(request, "上传剧照失败：" + e.getMessage(), userInfo.getClientType(), CrewPictureInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，上传剧照失败");
		}
		return resultMap;
	}
	
	/**
	 * 删除分组
	 * @param request
	 * @param crewId 剧组ID
	 * @param userId 用户ID
	 * @param pictureGroupId 相册分组ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteCrewPictureGroup")
	public Object deleteCrewPictureGroup(HttpServletRequest request,
			String crewId, String userId, String pictureGroupId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = null;
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			
			if(StringUtils.isBlank(pictureGroupId)) {
				throw new IllegalArgumentException("请选择分组！");
			}
			this.crewPictureInfoService.deleteCrewPictureAndAttachment(pictureGroupId, crewId);
			this.sysLogService.saveSysLogForApp(request, "删除分组", userInfo.getClientType(), CrewPictureInfoModel.TABLE_NAME, null, SysLogOperType.DELETE.getValue());
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，删除分组失败", e);
			this.sysLogService.saveSysLogForApp(request, "删除分组失败：" + e.getMessage(), userInfo.getClientType(), CrewPictureInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，删除分组失败");
		}
		return resultMap;
	}
	
	/**
	 * 修改分组密码
	 * @param request
	 * @param crewId 剧组ID
	 * @param userId 用户ID
	 * @param pictureGroupId 相册分组ID
	 * @param pictureGroupPassword 相册分组密码
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updatePictureGroupPassword")
	public Object updatePictureGroupPassword(HttpServletRequest request,
			String crewId, String userId, String pictureGroupId,
			String oldPictureGroupPassword, String pictureGroupPassword) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = null;
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			
			if(StringUtils.isBlank(pictureGroupId)) {
				throw new IllegalArgumentException("请选择分组！");
			}
			
			//根据ID查询出分组的详细信息
			CrewPictureInfoModel model = this.crewPictureInfoService.queryCrewPictureInfoById(pictureGroupId, crewId);

			if (model == null) {
				throw new IllegalArgumentException("当前分组不存在");
			}
			if(StringUtils.isBlank(oldPictureGroupPassword)) {
				oldPictureGroupPassword = "";
			}
			String realOldPass = model.getPicturePassword();
			if(StringUtils.isBlank(realOldPass)) {
				realOldPass = "";
			}
			if(!realOldPass.equals(oldPictureGroupPassword)) {
				throw new IllegalArgumentException("旧密码输入不正确");
			}
			
			if(StringUtils.isNotBlank(pictureGroupPassword) && pictureGroupPassword.length() > 32) {
				throw new IllegalArgumentException("分组密码最大长度为32");
			}
			model.setPicturePassword(pictureGroupPassword);
			
			//更新数据
			this.crewPictureInfoService.updateCrewPictureInfo(model);
			this.sysLogService.saveSysLogForApp(request, "修改分组密码", userInfo.getClientType(), CrewPictureInfoModel.TABLE_NAME, null, SysLogOperType.UPDATE.getValue());
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，修改分组密码失败", e);
			this.sysLogService.saveSysLogForApp(request, "修改分组密码失败：" + e.getMessage(), userInfo.getClientType(), CrewPictureInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，修改分组密码失败");
		}
		return resultMap;
	}
	
	/**
	 * 移动图片
	 * @param request
	 * @param crewId 剧组ID
	 * @param userId 用户ID
	 * @param attachmentIds 附件ID，多个以‘,’分隔
	 * @param attpackId 移动到的分组的附件包ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/movePicture")
	public Object movePicture(HttpServletRequest request,
			String crewId, String userId, String attachmentIds, String attpackId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = null;
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			
			if (StringUtils.isBlank(attachmentIds)) {
				throw new IllegalArgumentException("请选择要移动的图片");
			}
			if (StringUtils.isBlank(attpackId)) {
				throw new IllegalArgumentException("请选择要保存图片的分组");
			}
			
			List<AttachmentModel> modelList = new ArrayList<AttachmentModel>();
			String[] attacmentIdArr = attachmentIds.split(",");
			//根据id查询出附件信息
			for (String attachmentId : attacmentIdArr) {
				AttachmentModel model = this.attachmentService.queryAttachmentById(attachmentId);
				if (null != model) {
					model.setAttpackId(attpackId);
					modelList.add(model);
				}
				//如果当前照片是封面照片需要将相册的封面照片id设置为空
				this.crewPictureInfoService.updateCrewPictureIndexId(attacmentIdArr, crewId);
			}
			
			//批量更新
			if (null != modelList && modelList.size()>0) {
				this.attachmentService.updateAttachmentBatch(modelList);
			}
			this.sysLogService.saveSysLogForApp(request, "移动图片", userInfo.getClientType(), CrewPictureInfoModel.TABLE_NAME, null, SysLogOperType.UPDATE.getValue());
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，移动图片失败", e);
			throw new IllegalArgumentException("未知异常，移动图片失败");
		}
		return resultMap;
	}
	
	/**
	 * 修改图片名称
	 * @param request
	 * @param attachmentId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateAttachmentPictureName")
	public Object updateAttachmentPictureName(HttpServletRequest request, 
			String crewId, String userId, String attachmentId, String attachmentName, String attpackId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = null;
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			
			if (StringUtils.isBlank(attachmentId)) {
				throw new IllegalArgumentException("请选择需要修改的图片");
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
			this.sysLogService.saveSysLog(request, "修改图片名称", userInfo.getClientType(), AttachmentModel.TABLE_NAME, null, 2);
		}catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知错误，修改图片名称失败", e);
			this.sysLogService.saveSysLog(request, "修改图片名称失败：" + e.getMessage(), userInfo.getClientType(), AttachmentModel.TABLE_NAME, null, 6);
			throw new IllegalArgumentException("未知错误，修改图片名称失败", e);
		}
		return resultMap;
	}
}
