package com.xiaotu.makeplays.mobile.server.attachment;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.apache.commons.lang.StringUtils;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.xiaotu.makeplays.attachment.model.AttachmentModel;
import com.xiaotu.makeplays.attachment.model.constants.AttachmentType;
import com.xiaotu.makeplays.attachment.service.AttachmentService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.PropertiesUitls;

@Controller
@RequestMapping("/interface/attachFacade")
public class AttachmentFacade {
	
	Logger logger = LoggerFactory.getLogger(AttachmentFacade.class);

	@Autowired
	private AttachmentService attachmentService;
	
	/**
	 * 删除附件接口
	 * @param crewId
	 * @param userId
	 * @param attachmentId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteAttachment")
	public Object deleteAttachment(String crewId, String userId, String attachmentId) {
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			if (StringUtils.isBlank(attachmentId)) {
				throw new IllegalArgumentException("请选择需要删除的附件");
			}
			
			String[] attachmentIdArray = attachmentId.split(",");
			for(String one : attachmentIdArray) {
				this.attachmentService.deleteById(one);
			}
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，删除附件失败。", e);
			throw new IllegalArgumentException("未知异常，删除附件失败");
		}
		
		return null;
	}
	
	/**
	 * 下载附件
	 * @param crewId
	 * @param userId
	 * @param attachmentId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/downloadAttachment")
	public Object downloadAttachment(HttpServletResponse response, String crewId, String userId, String attachmentId) {
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			if (StringUtils.isBlank(attachmentId)) {
				throw new IllegalArgumentException("附件ID不能为空");
			}
			
			AttachmentModel attachmentInfo = this.attachmentService.queryAttachmentById(attachmentId);
			String address = attachmentInfo.getHdStorePath();
			String fileName = attachmentInfo.getName();
			
			File file = new File(address);
			if(!file.exists()){
				throw new IllegalArgumentException("找不到文件");
			}
			String name = java.net.URLEncoder.encode(fileName,"UTF-8");
			
			response.setHeader("Content-Disposition", "attachment;fileName="+ name);
			response.setContentType("application/octet-stream");
	        response.setCharacterEncoding("UTF-8");
	        
			InputStream inputStream = new FileInputStream(address);
			OutputStream os = response.getOutputStream();
			byte[] b = new byte[1024];
			int length;
			while ((length = inputStream.read(b)) > 0) {
				os.write(b, 0, length);
			}
			inputStream.close();
			os.close();
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，删除附件失败。", e);
			throw new IllegalArgumentException("未知异常，删除附件失败");
		}
		
		return null;
	}
	
	/**
	 * 上传附件
	 * 测试的时候注意看存储路径是否符合规范
	 * @param crewId
	 * @param userId
	 * @param file
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/uploadAttachment")
	public Object uploadAttachment(String crewId, String userId, String attpackId, MultipartFile file) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			if (file == null) {
				throw new java.lang.IllegalArgumentException("请提供需要上传的文件");
			}
			
			
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String baseStorePath = properties.getProperty("fileupload.path");
			String storePath = baseStorePath + "common/";
			
			
			//把附件上传到服务器(高清原版)
            Map<String, String> fileMap = FileUtils.uploadFile(file, false, storePath);
			String fileStoreName = fileMap.get("fileStoreName");
			String fileRealName = fileMap.get("fileRealName");
			String hdStorePath = fileMap.get("storePath");
			
			
			/*
			 * 处理高清附件为标清附件（分文件类型）
			 */
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
			
			
			Integer type = AttachmentType.Others.getValue();		
			//判断是否是图片文件
			if (FileUtils.isPicture(hdStorePath + fileStoreName)) {
				type = AttachmentType.Picture.getValue();
			}
			if (suffix.equals(".doc") || suffix.equals(".docx")) {
				type = AttachmentType.Word.getValue();
			}
			if (suffix.equals(".mp3") || suffix.equals(".amr") || suffix.equals(".wav")) {
				type = AttachmentType.Audio.getValue();
			}
			//图片文件将源文件处理为小的缩略图
			if (type == AttachmentType.Picture.getValue()) {
				sdStorePath = hdStorePath + "sd/" + exceptSuffixName + "_sd" + suffix;
				BufferedImage newImage = FileUtils.getNewImage(file, null, 200, 200);
				File destFile = new File(sdStorePath);
				FileUtils.makeDir(destFile);
				
	            ImageIO.write(newImage, "png", destFile);
			}
			
			//音频文件获取文件的声音长度
			long length = 0;
			if (type == AttachmentType.Audio.getValue()) {
				if (suffix.equals(".wav")) {
					Clip clip = AudioSystem.getClip();
					AudioInputStream ais = AudioSystem.getAudioInputStream(new File(hdStorePath+fileStoreName));
					clip.open(ais);
					length = clip.getMicrosecondLength() / 1000000;
				}
				if (suffix.equals(".mp3")) {
					MP3File f = (MP3File)AudioFileIO.read(new File(hdStorePath+fileStoreName));
					MP3AudioHeader audioHeader = (MP3AudioHeader)f.getAudioHeader();
					length = audioHeader.getTrackLength();
				}
			}
			
			long size = FileUtils.getFileSize(hdStorePath+fileStoreName);
			
			AttachmentModel attachment = this.attachmentService.saveAttachmentInfo(crewId, attpackId, type, fileRealName, hdStorePath+fileStoreName, sdStorePath, suffix, size, length);
			
			resultMap.put("attpackId", attachment.getAttpackId());
			resultMap.put("attachmentId", attachment.getId());
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，上传附件失败。", e);
			throw new IllegalArgumentException("未知异常，上传附件失败");
		}
		
		return resultMap;
	}

}
