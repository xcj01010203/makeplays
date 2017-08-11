package com.xiaotu.makeplays.mobile.server.sys;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.xiaotu.makeplays.sys.model.SysLogModel;
import com.xiaotu.makeplays.sys.service.SysLogService;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.MsgUtils;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 系统管理接口类
 * @author xuchangjian 2015-12-7上午9:27:00
 */
@RequestMapping("/interface/systemFacade")
@Controller
public class SystemFacade {
	
	Logger logger = LoggerFactory.getLogger(SystemFacade.class);
	
	@Autowired
	private SysLogService sysLogService;
	
	/**
	 * 记录手机端日志
	 * @param crewId	剧组ID
	 * @param userId	用户ID
	 * @param timestamp	时间戳
	 * @param clientType	客户端类型
	 * @param logDesc	日志描述
	 * @param logfile	日志文件
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@RequestMapping("/recordLog")
	@ResponseBody
	public Object recordLog(String crewId, String userId, String timestamp, int clientType, String logDesc, MultipartFile logfile) {
		try {
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String baseStorePath = properties.getProperty("fileupload.path");
			String storePath = baseStorePath + "mobileLog/";
			
			//上传文件
			Map<String, String> logFileInfo = FileUtils.uploadFile(logfile, false, storePath);
			String fileRealName = logFileInfo.get("fileRealName");
			String fileStoreName = logFileInfo.get("fileStoreName");
			String fileStorePath = logFileInfo.get("storePath");
			
			
			SysLogModel sysLog = new SysLogModel();
			sysLog.setLogId(UUIDUtils.getId());
			sysLog.setCrewId(crewId);
			sysLog.setUserId(userId);
			if (!StringUtils.isBlank(timestamp)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date logTime = sdf.parse(timestamp);
				sysLog.setLogTime(logTime);
			}
			sysLog.setLogDesc(logDesc);
			sysLog.setTerminal(clientType);
			sysLog.setStorePath(fileStorePath + fileStoreName);
			sysLog.setLogFileName(fileRealName);
			
			this.sysLogService.addOneLog(sysLog);
			
		} catch (Exception e) {
			logger.error("未知异常，记录系统日志失败", e);
			throw new IllegalArgumentException("未知异常，记录系统日志失败", e);
		}
		return null;
	}
	
	/**
	 * 发送等待升级提示短信
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sendWaitingUpdateMsg")
	public Object sendWaitingUpdateMsg(String phones) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			if (StringUtils.isBlank(phones)) {
				throw new IllegalArgumentException("请提供手机号");
			}
			
			String[] phoneArray = phones.split(",");
			
			
			Calendar nowCal = Calendar.getInstance();
			Calendar halfHourLaterCal = Calendar.getInstance();
			halfHourLaterCal.add(Calendar.HOUR, 2);
			
			String nowDateStr = sdf.format(nowCal.getTime());
			String halfHourLaterDateStr = sdf.format(halfHourLaterCal.getTime());
			for (String phone : phoneArray) {
				String[] args = new String[] {nowDateStr, halfHourLaterDateStr};
				MsgUtils.sendMsg(phone, "129363", args);
			}
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，发送短信失败", e);
			throw new IllegalArgumentException("未知异常，发送短信失败");
		}
		return null;
	}
	
	/**
	 * 发送升级成功短信
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sendUpdatedMsg")
	public Object sendUpdatedMsg(String phones) {
		try {
			if (StringUtils.isBlank(phones)) {
				throw new IllegalArgumentException("请提供手机号");
			}
			
			String[] phoneArray = phones.split(",");
			
			
			String url = "www.baidu.com";
			for (String phone : phoneArray) {
				String[] args = new String[] {url};
				MsgUtils.sendMsg(phone, "129362", args);
			}
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error("未知异常，发送短信失败", e);
			throw new IllegalArgumentException("未知异常，发送短信失败");
		}
		
		return null;
	
	}
	
	
	
	/**
	 * 临时
	 * 天机算发送注册通知短信接口
	 * 
	 * 格式：
	 * 【小土科技】{1}您好，恭喜您成功注册{2}系统，请用谷歌浏览器访问{3}，帐号：{4}，密码：{5}，使用过程中如有疑问，请加QQ群：{6}
	 * 
	 * @param name	用户名
	 * @param phone	用户手机号
	 * @param password	用户密码
	 * @return
	 */
	@RequestMapping("/sendNoticeMsg")
	@ResponseBody
	public Object sendNoticeMsg(String name, String phone, String password) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		
		try {
			String sysName = "天机算-小土数据分析";
			String sysUrl = "http://a.moonpool.com.cn";
			String contact = "532541208";
			
			String[] args = new String[] {name, sysName, sysUrl, phone, phone, contact};
			MsgUtils.sendMsg(phone, "88944", args);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			success = false;
			logger.error("未知异常，获取通告单列表失败", e);
			throw new IllegalArgumentException("未知异常，获取通告单列表失败");
		}
		
		resultMap.put("success", success);
		return resultMap;
	
	}
}
