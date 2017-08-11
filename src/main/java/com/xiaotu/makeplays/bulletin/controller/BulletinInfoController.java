package com.xiaotu.makeplays.bulletin.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.bulletin.model.BulletinInfoModel;
import com.xiaotu.makeplays.bulletin.service.BulletinInfoService;
import com.xiaotu.makeplays.crew.model.CrewUserMapModel;
import com.xiaotu.makeplays.sys.service.SysLogService;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.service.JoinCrewApplyMsgService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 剧组公告信息
 * @author xuchangjian
 */
@Controller
@RequestMapping("/bulletinInfoManager")
public class BulletinInfoController extends BaseController{
	
	Logger logger = LoggerFactory.getLogger(BulletinInfoController.class);
	
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private BulletinInfoService bulletinInfoService;
	
	@Autowired
	private SysLogService sysLogService;
	
	@Autowired
	private JoinCrewApplyMsgService joinCrewApplyMsgService;
	
	/**
	 * 跳转到消息列表页面(消息列表页面只能进行查看公告的操作)
	 * @return
	 */
	@RequestMapping("/bulletinList")
	public ModelAndView toBulletinInfoListPage(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		String viewName = "";
		UserInfoModel userInfo = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);
		if (userInfo.getType().equals(Constants.USER_TYPE_ADMIN)) {
			viewName = "bulletin/adminBulletinList";
		} else {
			viewName = "/bulletin/bulletinList";
		}
		mv.setViewName(viewName);
		return mv;
	}
	
	/**
	 * 跳转到公告管理页面（公告管理页面可以对公告进行增删改查）
	 * @return
	 */
	@RequestMapping("/bulletinManager")
	public ModelAndView toBulletinManagerPage(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("/bulletin/bulletinManager");
		
		//超级管理员跳转到消息列表页面
		UserInfoModel userInfo = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);
		if (userInfo.getType().equals(Constants.USER_TYPE_ADMIN)) {
			mv.setViewName("redirect:/bulletinInfoManager/bulletinList");
		}
		return mv;
	}
	
	/**
	 * 跳转到公告新增、修改页面
	 * @return
	 */
	@RequestMapping("/bulletinDetail")
	public ModelAndView toBulletinDetailPage(HttpServletRequest request, String bulletinId) {
		ModelAndView mv = new ModelAndView("/bulletin/bulletinDetail");
		try {
			if (!StringUtils.isBlank(bulletinId)) {
				BulletinInfoModel bulletinInfo = this.bulletinInfoService.queryOneByBulletinId(bulletinId);
				bulletinInfo.setStartDate(sdf.parse(sdf.format(bulletinInfo.getStartDate())));
				bulletinInfo.setEndDate(sdf.parse(sdf.format(bulletinInfo.getEndDate())));
				mv.addObject("bulletinInfo", bulletinInfo);
			}
		} catch (Exception e) {
			logger.error("未知原因，查询公告信息失败", e);
			e.printStackTrace();
		}
		
		try {
//			this.sysLogService.saveSysLog(request, "跳转到公告新增、修改页面", Constants.TERMINAL_PC, BulletinInfoModel.TABLE_NAME, bulletinId,0);
		} catch (Exception e) {
			logger.error("未知原因，保存系统日志失败", e);
		}
		return mv;
	}
	
	/**
	 * 跳转到查看公告详细信息页面
	 * @param bulletinId
	 * @return
	 */
	@RequestMapping("/bulletinViewPage")
	public ModelAndView toViewBulletinDetailPage(HttpServletRequest request, String bulletinId) {
		ModelAndView mv = new ModelAndView("/bulletin/bulletinViewPage");
		try {
			if (!StringUtils.isBlank(bulletinId)) {
				BulletinInfoModel bulletinInfo = this.bulletinInfoService.queryOneByBulletinId(bulletinId);
				bulletinInfo.setStartDate(sdf.parse(sdf.format(bulletinInfo.getStartDate())));
				bulletinInfo.setEndDate(sdf.parse(sdf.format(bulletinInfo.getEndDate())));
				mv.addObject("bulletinInfo", bulletinInfo);
			}
		} catch (Exception e) {
			logger.error("未知原因，查询公告信息失败", e);
			e.printStackTrace();
		}
		
		try {
//			this.sysLogService.saveSysLog(request, "查看公告详细信息", Constants.TERMINAL_PC, BulletinInfoModel.TABLE_NAME, bulletinId,0);
		} catch (Exception e) {
			logger.error("未知原因，保存系统日志失败", e);
		}
		return mv;
	}
	
	/**
	 * 查询剧组公告列表，只为剧组人员查询
	 * @param request
	 * @param operateType 操作方式（1：只读  2：读写）
	 * @return
	 */
	@RequestMapping("/queryBulletinList")
	public @ResponseBody Map<String, Object> queryBulletinList(HttpServletRequest request, Page page, int operateType) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		String crewId = this.getCrewId(request);
		CrewUserMapModel crewUser = (CrewUserMapModel) request.getSession().getAttribute(Constants.SESSION_CREW_USER_INFO);
		try {
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("crewId", crewId);
			
			//如果是通过页面“消息中心”的请求，则只能看到所属剧组下已发布的公告消息
			if (operateType == Constants.BULLETIN_PERMISSIONTYPE_READ) {
				conditionMap.put("status", Constants.BULLETIN_STATUS_RELEASE);
			}
			//如果是通过“公告管理”的请求，普通剧组用户只能操作自己发布的所有消息，剧组管理员可以操作所有消息
			if (operateType == Constants.BULLETIN_PERMISSIONTYPE_WRITE && crewUser.getType() == Constants.CREW_TYPE_NOT_ADMIN) {
				conditionMap.put("pubUserId", crewUser.getUserId());
			}
			
			//查询剧组公告
			List<BulletinInfoModel> bulletinList = this.bulletinInfoService.queryManyByMutiCondition(conditionMap, page);
			List<Map<String, Object>> bulletinMapList = new ArrayList<Map<String, Object>>();
			
			for (BulletinInfoModel bulletinInfo : bulletinList) {
				Map<String, Object> bulletinInfoMap = new HashMap<String, Object>();
				bulletinInfoMap.put("bulletinId", bulletinInfo.getBulletinId());
				bulletinInfoMap.put("bulletinName", bulletinInfo.getBulletinName());
				bulletinInfoMap.put("content", bulletinInfo.getContent());
				
				if (!StringUtils.isBlank(bulletinInfo.getAttachUrl())) {
					bulletinInfoMap.put("attachUrl", FileUtils.genDownloadPath(bulletinInfo.getAttachUrl(), bulletinInfo.getAttachName()));
				} else {
					bulletinInfoMap.put("attachUrl", null);
				}
				
				bulletinInfoMap.put("attachName", bulletinInfo.getAttachName());
				bulletinInfoMap.put("pubUserName", bulletinInfo.getPubUserName());
				bulletinInfoMap.put("pubUserId", bulletinInfo.getPubUserId());
				bulletinInfoMap.put("createTime", this.sdf.format(bulletinInfo.getCreateTime()));
				bulletinInfoMap.put("startDate", this.sdf.format(bulletinInfo.getStartDate()));
				bulletinInfoMap.put("endDate", this.sdf.format(bulletinInfo.getEndDate()));
				
				bulletinMapList.add(bulletinInfoMap);
			}
			
			resultMap.put("bulletinList", bulletinMapList);
			resultMap.put("totalCount", page.getTotal());
			
		} catch (Exception e) {
			String msg = "未知异常，查询剧组公告失败";
			logger.error(msg, e);
			
			success = false;
			message = msg;
		}
		
		try {
//			this.sysLogService.saveSysLog(request, "查询剧组公告列表，只为剧组人员查询", Constants.TERMINAL_PC, BulletinInfoModel.TABLE_NAME, "",0);
		} catch (Exception e) {
			logger.error("未知原因，保存系统公告失败", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询剧组公告列表，只为超级管理员查询
	 * @param request
	 * @param operateType 操作方式（1：只读  2：读写）
	 * @return
	 */
	@RequestMapping("/bulletinListJsonForAdmin")
	public @ResponseBody Map<String, Object> listBulletinInfoForAdmin(HttpServletRequest request, Page page) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			//查询剧组公告
			List<Map<String, Object>> bulletinList = this.bulletinInfoService.queryAll(page);
			
			page.setResultList(bulletinList);
			resultMap.put("result", page);
			
		} catch (Exception e) {
			String msg = "未知异常，查询剧组公告失败";
			logger.error(msg, e);
			
			success = false;
			message = msg;
		}
		
		try {
//			this.sysLogService.saveSysLog(request, "查询剧组公告列表，只为超级管理员查询", Constants.TERMINAL_PC, BulletinInfoModel.TABLE_NAME, "",0);
		} catch (Exception e) {
			logger.error("保存系统日志失败", e);
		}
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 新增、修改剧组公告
	 * @param request
	 * @param bulletinName	公告名称
	 * @param bulletinId 公告ID
	 * @param content	公告内容
	 * @param startDate	公告有效开始时间
	 * @param endDate	公告有效结束时间
	 * @param operateFlag 保存还是发布的标识符，1：保存  2：发布
	 * @return
	 */
	@RequestMapping("/addBulletinInfo")
	public @ResponseBody Map<String, Object> addBulletinInfo(HttpServletRequest request, String bulletinId, String bulletinName, String content, String startDate, String endDate, Integer operateFlag) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		String crewId = this.getCrewId(request);
		
		UserInfoModel userInfoModel = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);
		String userName = userInfoModel.getUserName();
		
		boolean success = true;
		String message = "";
		
		String logDesc = "";
		Integer operType = 0;
		try {
			//数据校验
			if (StringUtils.isBlank(bulletinName)) {
				throw new IllegalArgumentException("请输入公告名称");
			}
			if (StringUtils.isBlank(content)) {
				throw new IllegalArgumentException("请输入公告内容");
			}
			if (StringUtils.isBlank(startDate)) {
				throw new IllegalArgumentException("请选择公告开始时间");
			}
			if (StringUtils.isBlank(endDate)) {
				throw new IllegalArgumentException("请选择公告结束时间");
			}
			
			//文件上传
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String baseStorePath = properties.getProperty("fileupload.path");
			String storePath = baseStorePath + "bulletin/";
			
			Map<String, String> fileMap = FileUtils.uploadFile(request, storePath);
			
			BulletinInfoModel bulletinInfo = null;
			if (StringUtils.isBlank(bulletinId)) {
				bulletinInfo = new BulletinInfoModel();
			} else {
				bulletinInfo = this.bulletinInfoService.queryOneByBulletinId(bulletinId);
			}
			bulletinInfo.setBulletinName(bulletinName);
			bulletinInfo.setContent(content);
			bulletinInfo.setPubUserId(userInfoModel.getUserId());
			bulletinInfo.setPubUserName(userName);
			bulletinInfo.setCreateTime(new Date());
			
			bulletinInfo.setStartDate(this.sdf.parse(startDate));
			bulletinInfo.setEndDate(this.sdf.parse(endDate));
			bulletinInfo.setCrewId(crewId);
			
			if (fileMap != null) {
				bulletinInfo.setAttachUrl(fileMap.get("storePath") + fileMap.get("fileStoreName"));
				bulletinInfo.setAttachName(fileMap.get("fileRealName"));
			}
			
			//公告状态
			int status = Constants.BULLETIN_STATUS_DRAFT;
			if (operateFlag == Constants.BULLETIN_OPERATETYPE_PUBLISH) {
				status = Constants.BULLETIN_STATUS_RELEASE;
			}
			bulletinInfo.setStatus(status);
			
			if (StringUtils.isBlank(bulletinId)) {
				bulletinId = UUIDUtils.getId();
				bulletinInfo.setBulletinId(bulletinId);
				this.bulletinInfoService.addOneBullinInfo(bulletinInfo);
				logDesc = "新增系统公告";
				message = "添加成功";
				operType = 1;
			} else {
				this.bulletinInfoService.updateOneBullinInfo(bulletinInfo);
				logDesc = "修改系统公告";
				message = "修改成功";
				operType = 2;
			}
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
			
		} catch (Exception e) {
			String msg = "未知异常，操作系统公告失败";
			logger.error(msg, e);
			success = false;
			message = msg;
		}
		
		//添加系统日志
		try {
//			this.sysLogService.saveSysLog(request, logDesc, Constants.TERMINAL_PC, BulletinInfoModel.TABLE_NAME, bulletinId,operType);
		} catch (Exception e) {
			logger.error("添加系统日志失败", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 删除剧组公告
	 * @param bulletinId
	 * @return
	 */
	@RequestMapping("deleteBulletinInfo")
	public @ResponseBody Map<String, Object> deleteBulletinInfo(HttpServletRequest request, String bulletinId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		String logDesc = "";
		try {
			if (StringUtils.isBlank(bulletinId)) {
				throw new IllegalArgumentException("请提供要废弃的公告信息");
			}
			
			//删除剧组公告
			BulletinInfoModel bulletinInfo = this.bulletinInfoService.queryOneByBulletinId(bulletinId);
			bulletinInfo.setStatus(Constants.BUULETIN_STATUS_DELETE);
			this.bulletinInfoService.updateOneBullinInfo(bulletinInfo);
			
			logDesc = "废弃公告信息成功";
			message = "废弃公告信息成功";
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
			
			logDesc = "废弃公告信息失败," + ie.getMessage();
		} catch (Exception e) {
			String msg = "未知异常，废弃公告失败";
			logger.error("", e);
			success = false;
			message = msg;
			
			logDesc = "废弃公告信息失败," + msg;
		}
		
		try {
			//添加系统日志
//			this.sysLogService.saveSysLog(request, logDesc, Constants.TERMINAL_PC, BulletinInfoModel.TABLE_NAME, bulletinId,3);
		} catch (Exception e) {
			
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 下载附件
	 * @param storePath
	 */
	@RequestMapping("downloadFile")
	public ModelAndView downloadFile(HttpServletRequest request, HttpServletResponse response, String storePath, String fileName) {

       FileUtils.downloadFile(response, storePath, fileName);
		
		try {
//			this.sysLogService.saveSysLog(request, "下载公告附件", Constants.TERMINAL_PC, BulletinInfoModel.TABLE_NAME, fileName,5);
		} catch (Exception e) {
			logger.error("保存系统日志失败", e);
		}
        return null;
	}
	
	/**
	 * 发布系统公告
	 * @param request
	 * @param bulletinId
	 * @return
	 */
	@RequestMapping("/publishBulletin")
	public @ResponseBody Map<String, Object> publishBulletin(HttpServletRequest request, String bulletinId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		String logDesc = "";
		try {
			if (StringUtils.isBlank(bulletinId)) {
				throw new IllegalArgumentException("请提供要发布的公告信息");
			}
			
			//删除剧组公告
			BulletinInfoModel bulletinInfo = this.bulletinInfoService.queryOneByBulletinId(bulletinId);
			bulletinInfo.setStatus(Constants.BULLETIN_STATUS_RELEASE);
			this.bulletinInfoService.updateOneBullinInfo(bulletinInfo);
			
			logDesc = "发布公告信息成功";
			message = "发布公告信息成功";
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
			
			logDesc = "发布公告信息失败," + ie.getMessage();
		} catch (Exception e) {
			String msg = "未知异常，发布公告失败";
			logger.error("", e);
			success = false;
			message = msg;
			
			logDesc = "发布公告信息失败," + msg;
		}
		
		try {
			//添加系统日志
//			this.sysLogService.saveSysLog(request, logDesc, Constants.TERMINAL_PC, BulletinInfoModel.TABLE_NAME, bulletinId,2);
		} catch (Exception e) {
			logger.error("未知原因，记录系统日志失败", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 撤回公告
	 * @param request
	 * @param bulletinId
	 * @return
	 */
	@RequestMapping("/rebackBulletionInfo")
	public @ResponseBody Map<String, Object> rebackBulletionInfo(HttpServletRequest request, String bulletinId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		String logDesc = "";
		try {
			if (StringUtils.isBlank(bulletinId)) {
				throw new IllegalArgumentException("请提供要撤回的公告信息");
			}
			
			//删除剧组公告
			BulletinInfoModel bulletinInfo = this.bulletinInfoService.queryOneByBulletinId(bulletinId);
			bulletinInfo.setStatus(Constants.BULLETIN_STATUS_DRAFT);
			this.bulletinInfoService.updateOneBullinInfo(bulletinInfo);
			
			logDesc = "撤回公告信息成功";
			message = "撤回公告信息成功";
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
			
			logDesc = "撤回公告信息失败," + ie.getMessage();
		} catch (Exception e) {
			String msg = "未知异常，撤回公告失败";
			logger.error("", e);
			success = false;
			message = msg;
			
			logDesc = "撤回公告信息失败," + msg;
		}
		
		try {
			//添加系统日志
//			this.sysLogService.saveSysLog(request, logDesc, Constants.TERMINAL_PC, BulletinInfoModel.TABLE_NAME, bulletinId,2);
		} catch (Exception e) {
			
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
}
