package com.xiaotu.makeplays.scenario.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.model.constants.CrewType;
import com.xiaotu.makeplays.goods.model.ViewGoodsInfoMap;
import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;
import com.xiaotu.makeplays.roleactor.model.constants.ViewRoleType;
import com.xiaotu.makeplays.roleactor.service.ViewRoleService;
import com.xiaotu.makeplays.scenario.controller.dto.ScenarioViewDto;
import com.xiaotu.makeplays.scenario.model.BookMarkModel;
import com.xiaotu.makeplays.scenario.model.PublishScenarioSettingModel;
import com.xiaotu.makeplays.scenario.model.ScenarioFormatModel;
import com.xiaotu.makeplays.scenario.model.ScenarioInfoModel;
import com.xiaotu.makeplays.scenario.model.ScripteleInfoModel;
import com.xiaotu.makeplays.scenario.model.SeparatorInfoModel;
import com.xiaotu.makeplays.scenario.service.BookMarkService;
import com.xiaotu.makeplays.scenario.service.MovieScenarioService;
import com.xiaotu.makeplays.scenario.service.PublishScenarioSettingService;
import com.xiaotu.makeplays.scenario.service.ScenarioFormatService;
import com.xiaotu.makeplays.scenario.service.ScenarioService;
import com.xiaotu.makeplays.scenario.service.ScripteleInfoService;
import com.xiaotu.makeplays.scenario.service.SeparatorInfoService;
import com.xiaotu.makeplays.sys.service.SysLogService;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.MD5Util;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.RegexUtils;
import com.xiaotu.makeplays.utils.SepratorConstant;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.view.model.HistoryViewContentModel;
import com.xiaotu.makeplays.view.model.ViewContentModel;
import com.xiaotu.makeplays.view.model.ViewInfoModel;
import com.xiaotu.makeplays.view.model.ViewRoleMapModel;
import com.xiaotu.makeplays.view.model.ViewTempModel;
import com.xiaotu.makeplays.view.model.constants.BookmarkType;
import com.xiaotu.makeplays.view.model.constants.ViewContentStatus;
import com.xiaotu.makeplays.view.model.constants.ViewTempDataType;
import com.xiaotu.makeplays.view.service.AtmosphereService;
import com.xiaotu.makeplays.view.service.HistoryViewContentService;
import com.xiaotu.makeplays.view.service.ViewContentService;
import com.xiaotu.makeplays.view.service.ViewInfoService;
import com.xiaotu.makeplays.view.service.ViewRoleMapService;
import com.xiaotu.makeplays.view.service.ViewTempService;

/**
 * 剧本信息
 * @author xuchangjian 2016年7月26日下午5:29:04
 */
@Controller
@RequestMapping("/scenarioManager")
public class ScenarioController extends BaseController {
	
	private Logger logger = LoggerFactory.getLogger(ScenarioController.class);
	
	private final int terminal = Constants.TERMINAL_PC;

	private final String lineSeprator = "\r\n";
	
	/**
	 * 批处理的数量
	 */
	private final int batchSize = 300;
	
	@Autowired
	private ScenarioService scenarioService;
	
	@Autowired
	private MovieScenarioService movieScenarioSerivce;
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	@Autowired
	private ViewTempService viewTempService;
	
	@Autowired
	private SysLogService sysLogService;
	
	@Autowired
	private AtmosphereService atmosphereService;
	
	@Autowired
	private SeparatorInfoService separatorInfoService;
	
	@Autowired
	private ScripteleInfoService scripteleInfoService;
	
	@Autowired
	private ViewRoleService viewRoleService;
	
	@Autowired
	private ViewRoleMapService viewRoleMapService;
	
	@Autowired
	private BookMarkService bookMarkService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ScenarioFormatService scenarioFormatService;
	
	@Autowired
	private ViewContentService viewContentService;
	
	@Autowired
	private HistoryViewContentService historyViewContentService;
	
	@Autowired
	private PublishScenarioSettingService publishScenarioSettingService;
	
	/**
	 * 跳转到剧本上传页面
	 * @return
	 */
	@RequestMapping("/toUploadScePage")
	public ModelAndView toUploadScePage() {
		ModelAndView mv = new ModelAndView("scenario/uploadScenario");
		
		return mv;
	}
	
	
	/**
	 * 跳转到上传页面时需要的剧组的类型
	 *  
	 * @param request
	 * @param errorMessage 页面传递的错误信息
	 * @return
	 */
	@RequestMapping("/getCrewType")
	@ResponseBody
	public Map<String, Object> getCrewType(HttpServletRequest request, String errorMessage){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		String message = "";
		boolean success = true;
		try {
			//查询当前剧组类型
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			Integer crewType = crewInfo.getCrewType();
			String crewName = crewInfo.getCrewName();
			resultMap.put("crewType", crewType);
			resultMap.put("crewName", crewName);
			if (errorMessage != null && "".equals(errorMessage)) {
				message = errorMessage;
			}
		} catch (Exception e) {
			logger.error("未知异常",e);
			
			success = false;
			message = "未知异常,查询失败";
		}
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 获取上传结果页面初始化时需要的数据
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryUploadResultData")
	public Map<String, Object> queryUploadResultData(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String crewId = getCrewId(request);
		
		boolean success = true;
		String message = "";
		try {
			List<ViewTempModel> skipOrReplaceData = this.viewTempService.queryManyByCrewId(crewId, ViewTempDataType.SkipOrReplaceData.getValue());
			
			Map<Integer, List<String>> skipOrReplaceSceMap = new TreeMap<Integer, List<String>>();
			
			//存储不重复的集次号
			List<Integer> skipOrRepDataSetNoList = new ArrayList<Integer>();
			//循环剧本场景列表，计算出里面一共有的集数
			for (ViewTempModel viewTempInfo : skipOrReplaceData) {
				if (!skipOrRepDataSetNoList.contains(viewTempInfo.getSeriesNo())) {
					skipOrRepDataSetNoList.add(viewTempInfo.getSeriesNo());
				}
			}
			Collections.sort(skipOrRepDataSetNoList);
			//把剧本场景列表中的每集对应的场信息提取出来
			for (Integer seriesNo : skipOrRepDataSetNoList) {
				List<String> viewList = new ArrayList<String>();
				for (ViewTempModel viewTempInfo : skipOrReplaceData) {
					if (viewTempInfo.getSeriesNo() == seriesNo) {
						viewList.add(viewTempInfo.getViewNo());
					}
				}
				
				//对集次编号进行排序
				Comparator<String> sort = com.xiaotu.makeplays.utils.StringUtils.sort();
				Collections.sort(viewList, sort);
				skipOrReplaceSceMap.put(seriesNo, viewList);
			}
			
			resultMap.put("skipOrReplaceSceMap", skipOrReplaceSceMap);
			success = true;
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，获取数据失败";
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 检验数据库中是否存在跳过和替换的数据
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/hasSkipOrReplaceData")
	public Map<String, Object> hasSkipOrReplaceData(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			String crewId = getCrewId(request);
			List<ViewTempModel> skipOrReplaceData = this.viewTempService.queryManyByCrewId(crewId, ViewTempDataType.SkipOrReplaceData.getValue());
			
			boolean hasSkipOrReplaceData = false;
			if (skipOrReplaceData.size() > 0) {
				hasSkipOrReplaceData = true;
			}
			
			resultMap.put("hasSkipOrReplaceData", hasSkipOrReplaceData);
			success = true;
			message = "查询成功";
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，查询失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 剧本上传、解析
	 * @param request
	 * @param lineCount	每页行数
	 * @param wordCount	每行字数
	 * @param scenarioFormat	自定义的剧本格式
	 * @param hasSeriesNoTag	是否有“集次”元素
	 * @param extralSeriesNo	额外的集次信息
	 * @param groupSeriesNoFlag	是否有“第XX集”标识
	 * @param supportCNViewNo 是否支持中文的场次解析
	 * @param pageIncludeTitle 计算页数时是否包含标题
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/uploadScenario")
	public Map<String, Object> uploadScenario(HttpServletRequest request, String lineCount, String wordCount, String scenarioFormat, 
				boolean hasSeriesNoTag, String extralSeriesNo, boolean groupSeriesNoFlag, boolean supportCNViewNo, boolean pageIncludeTitle) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		UserInfoModel userInfo = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);
		Integer crewType = this.getSessionCrewType(request);
		
		ScenarioInfoModel scenarioInfo = null;
		boolean scenarioStatus = true;
		String scenarioAnalyDesc = "";
		String notFullMatchDataStr = "";	//不完全匹配的场次集场号
		String notFullMatchTitle = "";	//不完全匹配的场次标题
		String titleErrorMsgStr = "";	//有错误的标题
		
		try {
			String crewId = getCrewId(request);
			
			if (StringUtils.isBlank(lineCount) || StringUtils.isBlank(wordCount)) {
				throw new IllegalArgumentException("请填写“行/页，字/行”信息");
			}
			int lineCountInt = 0;
			int wordCountInt = 0;
			
			try {
				lineCountInt = Integer.parseInt(lineCount);
				wordCountInt = Integer.parseInt(wordCount);
			} catch (Exception e) {
				throw new IllegalArgumentException("“行/页，字/行”信息只能填数字");
			}
			
			if (StringUtils.isBlank(scenarioFormat)) {
				throw new IllegalArgumentException("请定义剧本标题格式");
			}
			
			//上传剧本附件到服务器
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String baseStorePath = properties.getProperty("fileupload.path");
			String storePath = baseStorePath + "scenario/";
			
			Map<String, String> uploadResultMap = FileUtils.uploadFile(request, storePath);
			if (uploadResultMap == null) {
				throw new IllegalArgumentException("请选择需要上传的文件");
			}
			
			String fileRealName = uploadResultMap.get("fileRealName");
			String suffix = fileRealName.substring(fileRealName.lastIndexOf("."));
			if (!".doc".equals(suffix) && !".docx".equals(suffix)) {
				throw new IllegalArgumentException("请上传doc或docx格式的文档，其他格式的文档暂不支持。");
			}
			
			//剧本文件信息的保存
			scenarioInfo = new ScenarioInfoModel();
			scenarioInfo.setCrewId(crewId);
			scenarioInfo.setUploadTime(new Date());
			scenarioInfo.setUserId(userInfo.getUserId());
			scenarioInfo.setScenarioId(UUIDUtils.getId());
			scenarioInfo.setScenarioName(fileRealName);
			scenarioInfo.setScenarioUrl(uploadResultMap.get("storePath") + uploadResultMap.get("fileStoreName"));
			scenarioInfo.setLineCount(lineCountInt);
			scenarioInfo.setWordCount(wordCountInt);
			scenarioInfo.setScriptRule(scenarioFormat);
			scenarioInfo.setSupportCNViewNo(supportCNViewNo);
			
			
			//分析附件内容
			Map<String, Object> viewDataMap = new HashMap<String, Object>();
			if (crewType == CrewType.Movie.getValue() || crewType == CrewType.InternetMovie.getValue()) {
				//电影剧本单独对待
				viewDataMap = this.movieScenarioSerivce.analyseScnario(crewId, scenarioInfo.getScenarioUrl(), 
						lineCountInt, wordCountInt, scenarioFormat, hasSeriesNoTag, 
						extralSeriesNo, groupSeriesNoFlag, supportCNViewNo, pageIncludeTitle);
				
			} else {
				viewDataMap = this.scenarioService.analysisScenario(crewId, scenarioInfo.getScenarioUrl(), 
						lineCountInt, wordCountInt, scenarioFormat, hasSeriesNoTag, 
						extralSeriesNo, groupSeriesNoFlag, supportCNViewNo, pageIncludeTitle);
			}

			List<ScenarioViewDto> autoReplaceData = new ArrayList<ScenarioViewDto>();	//自动“替换”的数据
			if (viewDataMap.get("autoReplaceData") != null) {
				autoReplaceData = (List<ScenarioViewDto>) viewDataMap.get("autoReplaceData");
			}
			List<ScenarioViewDto> autoSaveData = new ArrayList<ScenarioViewDto>();	//自动保存的数据
			if (viewDataMap.get("autoSaveData") != null) {
				autoSaveData = (List<ScenarioViewDto>) viewDataMap.get("autoSaveData");
			}
			List<ScenarioViewDto> skipOrReplaceData = new ArrayList<ScenarioViewDto>();	//供用户选择“跳过”或“替换”的数据
			if (viewDataMap.get("skipOrReplaceData") != null) {
				skipOrReplaceData = (List<ScenarioViewDto>) viewDataMap.get("skipOrReplaceData");
			}
			List<ViewInfoModel> keepOrNotData = new ArrayList<ViewInfoModel>();	//供用户选择“保留”或“不保留”的数据
			if (viewDataMap.get("keepOrNotData") != null) {
				keepOrNotData = (List<ViewInfoModel>) viewDataMap.get("keepOrNotData");
			}
			
			List<Map<String, String>> notFullMatchingInfo = (List<Map<String, String>>) viewDataMap.get("notFullMatchingInfo");	//不完全符合自定义格式信息但是符合集场规则的场景信息
			List<String> titleErrorMsg = (List<String>) viewDataMap.get("titleErrorMsg");
			List<String> notFullMatchingTitleList = (List<String>) viewDataMap.get("notFullMatchingTitleList");
			
			Integer minSeriesNo = null;
			if (viewDataMap.get("minSeriesNo") != null) {
				minSeriesNo = (Integer) viewDataMap.get("minSeriesNo");
			}
			
			//处理自动“替换”的数据
			this.viewInfoService.updateManyBySceDto(autoReplaceData, false, crewId, userInfo);
			//自动保存的数据
			this.viewInfoService.addManyBySceViewDto(autoSaveData, false, crewId, userInfo);
			
			for (Map<String, String> notFullMatchData : notFullMatchingInfo) {
				String seriesNo = notFullMatchData.get("seriesNo");
				String viewNo = notFullMatchData.get("viewNo");
				
				if (!StringUtils.isBlank(seriesNo)) {
					notFullMatchDataStr += seriesNo.trim() + "-" + viewNo.trim() + ",";
				} else {
					notFullMatchDataStr += viewNo.trim() + "场,";
				}
				
			}
			if (!StringUtils.isBlank(notFullMatchDataStr)) {
				notFullMatchDataStr += "与自定义标题不符，请检查。";
			}
			
			for (String titleError : titleErrorMsg) {
				String newTitleError = "";
				if (titleError.indexOf(this.lineSeprator)>-1) {
					newTitleError = titleError.replaceAll(this.lineSeprator, "  ");
				}else {
					newTitleError = titleError;
				}
				
				titleErrorMsgStr += "<p class='prompt-message'>&nbsp;&nbsp;&nbsp;&nbsp;" + newTitleError + "</p>";
			}
			
			for (String notFullTitle : notFullMatchingTitleList) {
				String newNotFullTitle = "";
				if (notFullTitle.indexOf(this.lineSeprator)>-1) {
					newNotFullTitle = notFullTitle.replaceAll(this.lineSeprator, "  ");
				}else {
					newNotFullTitle = notFullTitle;
				}
				notFullMatchTitle += "<p class='prompt-message'>&nbsp;&nbsp;&nbsp;&nbsp;" + newNotFullTitle + "</p>";
			}
			
			resultMap.put("notFullMatchDataStr", notFullMatchDataStr);
			resultMap.put("notFullMatchTitle", notFullMatchTitle);
			resultMap.put("titleErrorMsgStr", titleErrorMsgStr);
			resultMap.put("minSeriesNo", minSeriesNo);
			
			if ((skipOrReplaceData != null && skipOrReplaceData.size() > 0)) {
				
				/*
				 * 供用户选择“跳过”或“替换”的数据（存入临时表中和放入dto中返回到前台）
				 */
				this.viewTempService.addSkipOrReplaceData(skipOrReplaceData, crewId);
			}
			
			//将上传的剧本信息保存到数据库中
			if (scenarioInfo != null) {
				scenarioInfo.setStatus(scenarioStatus);
				scenarioInfo.setUploadDesc(scenarioAnalyDesc);
				this.scenarioService.addScenario(scenarioInfo);
			}
			
			
			//保存当前输入的剧本格式信息
			boolean isNewFormatInfo = false;
			ScenarioFormatModel scenarioFormatInfo = this.scenarioFormatService.queryByCrewId(crewId);
			if (scenarioFormatInfo == null) {
				scenarioFormatInfo = new ScenarioFormatModel();
				scenarioFormatInfo.setId(UUIDUtils.getId());
				
				isNewFormatInfo = true;
			}
			scenarioFormatInfo.setCrewId(crewId);
			scenarioFormatInfo.setLineCount(Integer.parseInt(lineCount));
			scenarioFormatInfo.setWordCount(Integer.parseInt(wordCount));
			scenarioFormatInfo.setScriptRule(scenarioFormat);
			scenarioFormatInfo.setSupportCNViewNo(supportCNViewNo);
			scenarioFormatInfo.setPageIncludeTitle(pageIncludeTitle);
			if (isNewFormatInfo) {
				this.scenarioFormatService.addOne(scenarioFormatInfo);
			} else {
				this.scenarioFormatService.updateOne(scenarioFormatInfo);
			}
			
			success = true;
			message = "剧本解析成功";
			scenarioAnalyDesc = "剧本解析成功";
			this.sysLogService.saveSysLog(request, "上传剧本", terminal, ScenarioInfoModel.TABLE_NAME, scenarioInfo.getScenarioId(),4);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			
			success = false;
			message = ie.getMessage();
			
			scenarioAnalyDesc = message;
			scenarioStatus = false;
		} catch (Exception e) {
			success = false;
			message = "未知异常,剧本解析失败";
			
			logger.error("未知异常,剧本解析失败", e);

			this.sysLogService.saveSysLog(request, "上传剧本失败：" + e.getMessage(), terminal, ScenarioInfoModel.TABLE_NAME, scenarioInfo.getScenarioId(), 6);
			
			scenarioAnalyDesc = message;
			scenarioStatus = false;
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 保存上传剧本时的自定义符号信息
	 * @param request
	 * @param operate 自定义符号
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveOperateInfo")
	public Map<String, Object> saveOperateInfo(HttpServletRequest request, String operate, String operateId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String crewId = getCrewId(request);
		
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(operate)) {
				throw new IllegalArgumentException("请填写自定义的符号");
			}
			if (operate.indexOf("s") != -1 || operate.indexOf("e") != -1) {
				throw new IllegalArgumentException("'s'和'e'为系统保留字符，不允许自定义。");
			}
			
			//把英文符号替换为中文符号
			operate = com.xiaotu.makeplays.utils.StringUtils.EnToCHSeparator(operate);
			operate = operate.replaceAll(" ", " ");	//特殊的空格替换成普通的空格
			
			//如果是新增符号，则校验符号是否已存在
			if (StringUtils.isBlank(operateId)) {
				//校验符号在数据库中是否已存在
				Map<String, Object> conditionMap = new HashMap<String, Object>();
				conditionMap.put("crewId", crewId);
				conditionMap.put("sepaName", operate);
				
				List<SeparatorInfoModel> separatorInfoList = this.separatorInfoService.queryManyByMutiCondition(conditionMap, null);
				if (separatorInfoList != null && separatorInfoList.size() != 0) {
					throw new IllegalArgumentException("该符号已存在，请不要重复添加");
				}
			} else {
				List<SeparatorInfoModel> separatorInfoList = this.separatorInfoService.querySameNameSepaExceptOwn(operateId, operate);
				if (separatorInfoList != null && separatorInfoList.size() > 0) {
					throw new IllegalArgumentException("该符号已存在，请不要重复添加");
				}
			}
			
			//获取符号表中最大ID中的数字,确保符号不会重复
			int lastSepaNum = this.separatorInfoService.genLastSepaIdNum();
			
			SeparatorInfoModel separatorInfoModel = null;
			if (StringUtils.isBlank(operateId)) {
				separatorInfoModel = new SeparatorInfoModel();
				separatorInfoModel.setSepaId("s" + (lastSepaNum + 1));
			} else {
				separatorInfoModel = this.separatorInfoService.queryOneById(operateId);
			}
			
			//正则表达式关键字
			List<String> regexKeyWords = new ArrayList<String>();
			regexKeyWords.add("^");
			regexKeyWords.add("$");
			regexKeyWords.add("(");
			regexKeyWords.add(")");
			regexKeyWords.add("[");
			regexKeyWords.add("]");
			regexKeyWords.add("{");
			regexKeyWords.add("}");
			regexKeyWords.add(".");
			regexKeyWords.add("?");
			regexKeyWords.add("+");
			regexKeyWords.add("*");
			regexKeyWords.add("|");
			
			String regex = "";
			if (regexKeyWords.contains(operate)) {
				regex = "\\" + operate;
			} else {
				regex = operate;
			}
			
			separatorInfoModel.setSepaName(operate);
			separatorInfoModel.setSepaDesc("自定义符号");
			separatorInfoModel.setCrewId(crewId);
			separatorInfoModel.setRegex("("+ regex +")");
			
			if (StringUtils.isBlank(operateId)) {
				this.separatorInfoService.addSeparatorInfo(separatorInfoModel);
			} else {
				this.separatorInfoService.updateSeparatorInfo(separatorInfoModel);
			}
			
			resultMap.put("separatorInfoModel", separatorInfoModel);
			message = "保存成功";
		} catch (IllegalArgumentException e) {
			message = e.getMessage();
			success = false;
			
			logger.error(message, e);
		} catch (Exception e) {
			message = "未知异常，保存符号失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 删除上传剧本时的自定义符号信息
	 * @param request
	 * @param operate 自定义符号
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteOperateInfo")
	public Map<String, Object> deleteOperateInfo(HttpServletRequest request, String sepaId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			if (!StringUtils.isBlank(sepaId)) {
				this.separatorInfoService.deleteSeparatorInfo(sepaId);
			}
			
			message = "删除成功";
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常，删除分隔符失败";
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 获取剧组分隔符列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/querySeparatorList")
	public Map<String, Object> querySeparatorList(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String crewId = getCrewId(request);
		
		boolean success = true;
		String message = "";
		try {
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("crewId", crewId);
			List<SeparatorInfoModel> separatorInfoList = this.separatorInfoService.queryManyByMutiCondition(conditionMap, null);
			
			//对获取的分割符信息进行排序
			Collections.sort(separatorInfoList, new Comparator<SeparatorInfoModel>() {
				@Override
				public int compare(SeparatorInfoModel o1, SeparatorInfoModel o2) {
					String sepaId1 = o1.getSepaId();
					int sepaIdNum1 = Integer.parseInt(sepaId1.substring(1, sepaId1.length()));

					String sepaId2 = o2.getSepaId();
					int sepaIdNum2 = Integer.parseInt(sepaId2.substring(1, sepaId2.length()));
	        		return sepaIdNum1 - sepaIdNum2;
				}
			});
			
			resultMap.put("separatorInfoList", separatorInfoList);
			message = "获取符号列表成功";
		}catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		}catch(Exception e) {
			message = "未知异常，获取符号列表信息失败";
			success = false;
			
			logger.error(message, e);
		}
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 获取剧本元素列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryScripteleList")
	public Map<String, Object> queryScripteleList(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			List<ScripteleInfoModel> scripteleList = this.scripteleInfoService.queryManyByMutiCondition(null, null);
			
			//对查询出的剧本元素进行排序
			Collections.sort(scripteleList, new Comparator<ScripteleInfoModel>() {
				@Override
				public int compare(ScripteleInfoModel o1, ScripteleInfoModel o2) {
					String eleId1 = o1.getEleId();
					int eleIdNum1 = Integer.parseInt(eleId1.substring(1, eleId1.length()));

					String eleId2 = o2.getEleId();
					int eleIdNum2 = Integer.parseInt(eleId2.substring(1, eleId2.length()));
	        		return eleIdNum1 - eleIdNum2;
				}
			});
			
			resultMap.put("scripteleList", scripteleList);
			message = "获取剧本元素列表成功";
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch(Exception e) {
			message = "未知异常，获取剧本元素列表信息失败";
			success = false;
			
			logger.error(message, e);
		}
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 获取上传的剧本格式信息
	 * @param crewId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryScenarioFormatInfo")
	public Map<String, Object> queryScenarioFormatInfo(HttpServletRequest request, String formatStr) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		String crewId = getCrewId(request);
		try {
			ScenarioFormatModel scenarioFormatInfo = this.scenarioFormatService.queryByCrewId(crewId);
			String formatInfo = "";
			boolean supportCNViewNo = false;
			boolean isDefaultFormat = false;
			Integer wordCount = 35;
			Integer lineCount = 40;
			boolean pageIncludeTitle = true;
			
			if (scenarioFormatInfo != null) {
				formatInfo = scenarioFormatInfo.getScriptRule();
				supportCNViewNo = scenarioFormatInfo.getSupportCNViewNo();
				wordCount = scenarioFormatInfo.getWordCount();
				lineCount = scenarioFormatInfo.getLineCount();
				pageIncludeTitle = scenarioFormatInfo.getPageIncludeTitle();
			}
			
			if (StringUtils.isBlank(formatInfo)) {
				isDefaultFormat = true;
				
				if (StringUtils.isNotBlank(formatStr)) {
					formatInfo = formatStr;
				}else {
					formatInfo = "";
				}
				/*if (crewType == 0) { //电影剧本格式
					formatInfo = "e2s0e3s0e4s0e5s1s2e7";
				} else {
					formatInfo = "e1s0e2s0e3s0e4s0e5s1s2e7";
				}*/
				
				//标识当前是默认的剧本格式
			}
			
			List<String> scenarioFormatList = this.scenarioService.genScenarioFormatList(formatInfo);
			resultMap.put("scenarioFormatList", scenarioFormatList);
			resultMap.put("supportCNViewNo", supportCNViewNo);
			resultMap.put("isDefaultFormat", isDefaultFormat);
			resultMap.put("wordCount", wordCount);
			resultMap.put("lineCount", lineCount);
			resultMap.put("pageIncludeTitle", pageIncludeTitle);
			
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询失败";
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 解析测试剧本标题
	 * @param scenarioFormat 剧本的格式信息
	 * @param title 剧本测试标题
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/analysisScenarioTitle")
	public Map<String, Object> analysisScenarioTitle(HttpServletRequest request, String scenarioFormat, String title, Boolean supportCNViewNo) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String crewId = this.getCrewId(request);
		Integer crewType = this.getSessionCrewType(request);
		
		String message = "";
		boolean success = true;
		try {
			if (StringUtils.isBlank(title)) {
				throw new IllegalArgumentException("请输入测试标题");
			}
			title = title.replaceAll("﻿", "");	//前面的字符不是空格，是一种特殊字符
			
			//如果剧本格式为空，则取上次上传剧本的格式
			if (StringUtils.isBlank(scenarioFormat) || supportCNViewNo == null) {
				ScenarioFormatModel scenarioFormatInfo = this.scenarioFormatService.queryByCrewId(crewId);
				if (scenarioFormatInfo == null) {
					throw new IllegalArgumentException("请先到剧本上传页面配置剧本格式，再使用此功能");
				}
				scenarioFormat = scenarioFormatInfo.getScriptRule();
				supportCNViewNo = scenarioFormatInfo.getSupportCNViewNo();
			}
			
			//textarea中换行是用/n表示
			String[] origTitleArray = title.split("\n");
			title = "";
			for (int i = 0; i < origTitleArray.length; i++) {
				if (i == origTitleArray.length - 1) {
					title += origTitleArray[i].trim();
				} else {
					title += origTitleArray[i].trim() + this.lineSeprator;
				}
			}
			
			title = com.xiaotu.makeplays.utils.StringUtils.ToDBC(title);
			title = com.xiaotu.makeplays.utils.StringUtils.EnToCHSeparator(title);
			List<String> scenarioFormatList = this.scenarioService.genScenarioFormatList(scenarioFormat);
			this.scenarioService.checkFormatValid(scenarioFormatList);
			
			/*
			 * 获取自定义中格式中的信息
			 */
			//剧本元素信息
			List<ScripteleInfoModel> scripteleInfoList = this.scripteleInfoService.queryManyByMutiCondition(null, null);
			//剧本符号信息
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("crewId", crewId);
			List<SeparatorInfoModel> separatorInfoList = this.separatorInfoService.queryManyByMutiCondition(conditionMap, null);
			Map<String, Object> formatInfo = this.scenarioService.genFormatInfo(scripteleInfoList, separatorInfoList, scenarioFormatList);
			
			
			String mainTitleRegex = (String) formatInfo.get("mainTitleRegex");	//匹配标题的正则表达式
			int minCount = (Integer) formatInfo.get("minCount");	//标题中元素最小数目
			String separateSceripRegex = (String) formatInfo.get("separateSceripRegex");	//分割标题中元素的正则表达式
			String seriesViewNoRegex = (String) formatInfo.get("seriesViewNoRegex");	//只包含集场号的正则表达式
			
			boolean standardTitleFlag = RegexUtils.regexFind(mainTitleRegex, title);	 //本行是否为标配的标题
			boolean isTitle = RegexUtils.regexFind(seriesViewNoRegex, title);	//本行是否匹配只含有集场号的标题
			
			if (supportCNViewNo) {
				//电影的场次第一个元素肯定为“场”，此处识别第一个元素是否为“三十一”这种中文数字
				String[] titleArray = title.split(separateSceripRegex);
				for (int i = 0; i < titleArray.length; i++) {
					if (!StringUtils.isBlank(titleArray[i])) {
						String cNViewNo = com.xiaotu.makeplays.utils.StringUtils.genCNViewNo(titleArray[i]);
						if (!StringUtils.isBlank(cNViewNo)) {
							String cNTitleRegex = mainTitleRegex.replace("\\d", Constants.REGEX_CN_NUMBER_START);
							if (RegexUtils.regexFind(cNTitleRegex, title)) {
								standardTitleFlag = true;
							}
							
							String cNSeriesViewNoRegex = seriesViewNoRegex.replace("\\d", Constants.REGEX_CN_NUMBER_START);
							if (RegexUtils.regexFind(cNSeriesViewNoRegex, title)) {
								isTitle = true;
							}
						}
						break;
					}
				}
			}
			
			//返回剧本的正确格式信息
			List<Map<String, Object>> formatSampleList = new ArrayList<Map<String, Object>>();
			for (String singleFormat : scenarioFormatList) {
				Map<String, Object> singleSampleMap = new HashMap<String, Object>();
				for (ScripteleInfoModel singleScriptele : scripteleInfoList) {
					if (singleFormat.equals(singleScriptele.getEleId())) {
						singleSampleMap.put("name", singleScriptele.getEleName());
						singleSampleMap.put("sample", singleScriptele.getEleSample());
						singleSampleMap.put("type", 1);
						break;
					}
				}
				for (SeparatorInfoModel singleSeparator : separatorInfoList) {
					if (singleFormat.equals(singleSeparator.getSepaId())) {
						singleSampleMap.put("name", singleSeparator.getSepaName());
						singleSampleMap.put("sample", singleSeparator.getSepaName());
						singleSampleMap.put("type", 2);
						break;
					}
				}
				formatSampleList.add(singleSampleMap);
			}
			
			resultMap.put("formatSampleList", formatSampleList);
			//如果匹配不是标题
			if (!standardTitleFlag && !isTitle) {
				throw new IllegalArgumentException("匹配无结果");
			} else {
				//保存当前输入的剧本格式信息
				boolean isNewFormatInfo = false;
				ScenarioFormatModel scenarioFormatInfo = this.scenarioFormatService.queryByCrewId(crewId);
				if (scenarioFormatInfo == null) {
					scenarioFormatInfo = new ScenarioFormatModel();
					scenarioFormatInfo.setId(UUIDUtils.getId());
					
					isNewFormatInfo = true;
				}
				scenarioFormatInfo.setCrewId(crewId);
				scenarioFormatInfo.setScriptRule(scenarioFormat);
				scenarioFormatInfo.setSupportCNViewNo(supportCNViewNo);
				if (isNewFormatInfo) {
					this.scenarioFormatService.addOne(scenarioFormatInfo);
				} else {
					this.scenarioFormatService.updateOne(scenarioFormatInfo);
				}
			}
			
			/*
			 * 处理是标题的情况
			 */
			//把英文符号转换为中文符号
			String dealedTitle = com.xiaotu.makeplays.utils.StringUtils.EnToCHSeparator(title);
			
			String[] titleArray = dealedTitle.split(separateSceripRegex);

			List<String> titleElementList = new ArrayList<String>();
			for (String str : titleArray) {
				if (StringUtils.isBlank(str)) {
					continue;
				}
				titleElementList.add(str);
			}
			int titleLength = titleElementList.size();
			if (titleLength < minCount) {
				throw new IllegalArgumentException("标题中元素不足，最少为" + minCount + "个，实际" + titleLength + "个");
			}
			
			//获取标题中的数据
			Map<String, Object> elementMap = new HashMap<String, Object>();
			
			if (crewType == CrewType.Movie.getValue() || crewType == CrewType.InternetMovie.getValue()) {
				elementMap = this.movieScenarioSerivce.genTitleElement(dealedTitle, scenarioFormat, scenarioFormatList, titleElementList, scripteleInfoList, separatorInfoList, supportCNViewNo);
			} else {
				elementMap = this.scenarioService.genTitleElement(dealedTitle, scenarioFormat, scenarioFormatList, titleElementList, scripteleInfoList, separatorInfoList, null, supportCNViewNo);
			}
			
			if (!(Boolean) elementMap.get("validTitle")) {
				List<String> errorMsgList = (List<String>) elementMap.get("titleErrorMsg");
				throw new IllegalArgumentException(errorMsgList.get(0));
			}
			
			
			for (String format : scenarioFormatList) {
				for (ScripteleInfoModel scriptele : scripteleInfoList) {
					String scrip  = scriptele.getEleId();
					if (format.equals(scrip)) {
						resultMap.put(format, "");
						break;
					}
				}
			}
			
			String seriesNo = "", 
					viewNo = "", 
					viewLocation = "", 
					atmosphere = "", 
					site = "", 
					season = "", 
					figure = "";
			if (elementMap.get("seriesNo") != null) {
				seriesNo = (String) elementMap.get("seriesNo");
			}
			if (elementMap.get("viewNo") != null) {
				viewNo = (String) elementMap.get("viewNo");
			}
			if (elementMap.get("viewLocation") != null) {
				viewLocation = (String) elementMap.get("viewLocation");
			}
			if (elementMap.get("atmosphere") != null) {
				atmosphere = (String) elementMap.get("atmosphere");
			}
			if (elementMap.get("site") != null) {
				site = (String) elementMap.get("site");
			}
			if (elementMap.get("season") != null) {
				season = (String) elementMap.get("season");
			}
			if (elementMap.get("figure") != null) {
				figure = (String) elementMap.get("figure");
			}
			
			
			
			if (resultMap.containsKey(Constants.SCRIPTELE_SERIESNO)) {
				resultMap.put(Constants.SCRIPTELE_SERIESNO, seriesNo);
			}
			if (resultMap.containsKey(Constants.SCRIPTELE_VIEWNO)) {
				resultMap.put(Constants.SCRIPTELE_VIEWNO, viewNo);
			}
			if (resultMap.containsKey(Constants.SCRIPTELE_VIEWLOCATION)) {
				resultMap.put(Constants.SCRIPTELE_VIEWLOCATION, viewLocation.replaceAll(",", "/"));
			}
			if (resultMap.containsKey(Constants.SCRIPTELE_ATMOSPHERE)) {
				resultMap.put(Constants.SCRIPTELE_ATMOSPHERE, atmosphere);
			}
			if (resultMap.containsKey(Constants.SCRIPTELE_SITE)) {
				resultMap.put(Constants.SCRIPTELE_SITE, site);
			}
			if (resultMap.containsKey(Constants.SCRIPTELE_SEASON)) {
				resultMap.put(Constants.SCRIPTELE_SEASON, season);
			}
			if (resultMap.containsKey(Constants.SCRIPTELE_FIGURE)) {
				resultMap.put(Constants.SCRIPTELE_FIGURE, figure.replaceAll(",", "/"));
			}
			
			success = true;
			message = "解析成功";
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			message = ie.getMessage();
			success = false;
		} catch(Exception e) {
			logger.error("未知异常，解析剧本标题失败", e);
			message = "未知异常，解析剧本标题失败";
			success = false;
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 剧本人物反刷
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/refreshFigure")
	public Map<String, Object> refreshFigure(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		String message = "";
		boolean success = true;
		try {
			String crewId = getCrewId(request);
			
			//写在业务类中为了统一事务控制
			this.scenarioService.refreshFigure(crewId, null);
			
			message = "人物重新分析成功!";
			this.sysLogService.saveSysLog(request, "角色提取-根据已有角色分析", terminal, ViewRoleMapModel.TABLE_NAME, null, 1);
		}catch (IllegalArgumentException ie){
			success = false;
			message = ie.getMessage();
			
			logger.error(message, ie);
		}catch (Exception e) {
			logger.error("未知异常，反刷演员信息失败", e);
			message = "未知异常，反刷演员信息失败";
			success = false;
			this.sysLogService.saveSysLog(request, "角色提取-根据已有角色分析失败：" + e.getMessage(), terminal, ViewRoleMapModel.TABLE_NAME, null, 6);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 重新分析页数
	 * @param request
	 * @param lineCount 每行字数
	 * @param wordCount 每页行数
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/refreshPage")
	public Map<String, Object> refreshPage(HttpServletRequest request, Integer lineCount, Integer wordCount, boolean pageIncludeTitle) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		String message = "";
		boolean success = true;
		try {
			String crewId = getCrewId(request);
			
			//对前台传递的数据进行校验
			if (lineCount == null || lineCount <= 0 ) {
				throw new IllegalArgumentException("请输入正确字数");
			}
			
			if (wordCount == null || wordCount <= 0) {
				throw new IllegalArgumentException("请输入正确的行数");
			}
			
			//保存当前输入的剧本格式信息
			boolean isNewFormatInfo = false;
			ScenarioFormatModel scenarioFormatInfo = this.scenarioFormatService.queryByCrewId(crewId);
			if (scenarioFormatInfo == null) {
				scenarioFormatInfo = new ScenarioFormatModel();
				scenarioFormatInfo.setId(UUIDUtils.getId());
				
				isNewFormatInfo = true;
			}
			scenarioFormatInfo.setCrewId(crewId);
			scenarioFormatInfo.setLineCount(lineCount);
			scenarioFormatInfo.setWordCount(wordCount);
			scenarioFormatInfo.setPageIncludeTitle(pageIncludeTitle);
			if (isNewFormatInfo) {
				this.scenarioFormatService.addOne(scenarioFormatInfo);
			} else {
				this.scenarioFormatService.updateOne(scenarioFormatInfo);
			}
			
			//写在业务类中为了统一事务控制
			this.scenarioService.refreshPage(crewId, lineCount, wordCount, pageIncludeTitle);
			
			message = "重新分析页数成功";
			this.sysLogService.saveSysLog(request, "重新分析页数", terminal, ViewRoleMapModel.TABLE_NAME, null, 1);
		} catch(IllegalArgumentException ie){
			success = false;
			message = ie.getMessage();
			
			logger.error(message,ie);
		} catch (Exception e) {
			message = "未知异常，重新页数失败";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "重新分析页数失败：" + e.getMessage(), terminal, ViewRoleMapModel.TABLE_NAME, null, 6);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 剧本服化道信息反刷
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/refreshProp")
	public Map<String, Object> refreshProp(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		String message = "";
		boolean success = true;
		try {
			String crewId = getCrewId(request);
			
			//写在业务类中为了统一事务控制
			this.scenarioService.refreshProp(crewId);
			
			this.sysLogService.saveSysLog(request, "服化道提取-根据已有服化道分析", terminal, ViewGoodsInfoMap.TABLE_NAME, null, 1);
		}catch (IllegalArgumentException ie){
			success = false;
			message = ie.getMessage();
			
			logger.error(message, ie);
		}catch (Exception e) {
			message = "未知异常，反刷服化道信息失败";
			logger.error(message, e);
			success = false;
			this.sysLogService.saveSysLog(request, "服化道提取-根据已有服化道分析：" + e.getMessage(), terminal, ViewGoodsInfoMap.TABLE_NAME, null, 6);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 解析剧本中人物
	 * @param request
	 * @return
	 */
	@RequestMapping("/analyseScenarioFigure")
	@ResponseBody
	public Map<String, Object> analyseScenarioFigure(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		String message = "";
		boolean success = true;
		try {
			String crewId = getCrewId(request);
			
			int viewContentCount = this.viewContentService.countViewContent(crewId, null);

			int batchCount = viewContentCount / batchSize;
			
			/*
			 * 解析剧组中所有可能存在的角色
			 */
			List<String> newRoleNameList = new ArrayList<String>();
			for (int i = 0; i < batchCount; i++) {
				Page page = new Page();
				page.setPageNo(i + 1);
				page.setPagesize(batchSize);

				// 查询未保存的场景内容信息
				List<Map<String, Object>> viewContentInfo = this.viewContentService.queryViewContent(crewId, page, null);
				
				//解析每一场中符合规则的角色
				List<String> myRoleNameList = this.genProbablyRoleList(viewContentInfo);
				
				for (String myRoleName : myRoleNameList) {
					if (!newRoleNameList.contains(myRoleName)) {
						newRoleNameList.add(myRoleName);
					}
				}
			}
			Page page = new Page();
			page.setPageNo(batchCount + 1);
			int pageSize = viewContentCount - batchCount * batchSize;
			page.setPagesize(batchSize);
			if (pageSize != 0) {
				// 查询未保存的场景内容信息
				List<Map<String, Object>> viewContentInfo = this.viewContentService.queryViewContent(crewId, page, null);
				
				//解析每一场中符合规则的角色
				List<String> myRoleNameList = this.genProbablyRoleList(viewContentInfo);
				
				for (String myRoleName : myRoleNameList) {
					if (!newRoleNameList.contains(myRoleName)) {
						newRoleNameList.add(myRoleName);
					}
				}
			}
			
			
			/*
			 * 去除掉特殊情况
			 */
			//去除掉角色表中已有的数据
			Map<String, Object> roleConditionMap = new HashMap<String, Object>();
			roleConditionMap.put("crewId", crewId);
			List<ViewRoleModel> viewRoleList = this.viewRoleService.queryManyByMutiCondition(roleConditionMap, null);
			for (ViewRoleModel roleInfo : viewRoleList) {
				String roleName = roleInfo.getViewRoleName();
				if (newRoleNameList.contains(roleName)) {
					newRoleNameList.remove(roleName);
				}
			}
			
			//去掉过滤词汇
			List<Map<String,Object>> keywordList = this.viewRoleService.queryFilterKeyword();
			List<String> toRemoveRoleList = new ArrayList<String>();
			for (String allRoleName : newRoleNameList) {
				for (Map<String, Object> map : keywordList) {
					String filteWord = (String) map.get("filteWord");
					if (allRoleName.indexOf(filteWord) != -1) {
						toRemoveRoleList.add(allRoleName);
						break;
					}
				}
			}
			newRoleNameList.removeAll(toRemoveRoleList);
			
			/*
			 * 计算角色在剧组中占有的场数
			 */
			Map<String, Integer> roleNameCountMap = new HashMap<String, Integer>();	//key为角色名称   value为角色的场数
			for (int i = 0; i < batchCount; i++) {
				Page page2 = new Page();
				page2.setPageNo(i + 1);
				page2.setPagesize(batchSize);

				// 查询未保存的场景内容信息
				List<Map<String, Object>> viewContentInfo = this.viewContentService.queryViewContent(crewId, page2, null);
				
				Map<String, Integer> myRoleCountMap = this.calculateRoleViewCount(newRoleNameList, viewContentInfo);
				for (String myRoleName : newRoleNameList) {
					Integer myCount = myRoleCountMap.get(myRoleName);
					if (!roleNameCountMap.containsKey(myRoleName)) {
						roleNameCountMap.put(myRoleName, myCount);
					} else {
						roleNameCountMap.put(myRoleName, roleNameCountMap.get(myRoleName) + myCount);
					}
				}
				
			}
			Page page2 = new Page();
			page2.setPageNo(batchCount + 1);
			int pageSize2 = viewContentCount - batchCount * batchSize;
			page2.setPagesize(batchSize);
			if (pageSize2 != 0) {
				// 查询未保存的场景内容信息
				List<Map<String, Object>> viewContentInfo = this.viewContentService.queryViewContent(crewId, page2, null);
				
				Map<String, Integer> myRoleCountMap = this.calculateRoleViewCount(newRoleNameList, viewContentInfo);
				for (String myRoleName : newRoleNameList) {
					Integer myCount = myRoleCountMap.get(myRoleName);
					if (!roleNameCountMap.containsKey(myRoleName)) {
						roleNameCountMap.put(myRoleName, myCount);
					} else {
						roleNameCountMap.put(myRoleName, roleNameCountMap.get(myRoleName) + myCount);
					}
				}
			}
			
			
			//把角色名称和场数封装到list中，便于排序
			List<Map<String, Object>> roleInfoList = new ArrayList<Map<String, Object>>();
			Set<String> roleNameSet = roleNameCountMap.keySet();
			for (String roleName : roleNameSet) {
				Map<String, Object> roleInfo = new HashMap<String, Object>();
				roleInfo.put("roleName", roleName);
				roleInfo.put("roleCount", roleNameCountMap.get(roleName));
				roleInfoList.add(roleInfo);
			}
			
			//按照角色戏量排序
			Collections.sort(roleInfoList, new Comparator<Map<String, Object>>() {

				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					int o1RoleCount = (Integer) o1.get("roleCount");
					int o2RoleCount = (Integer) o2.get("roleCount");
					return o2RoleCount - o1RoleCount;
				}
			});
			
			resultMap.put("roleInfoList", roleInfoList);
			this.sysLogService.saveSysLog(request, "角色提取，解析剧本中人物", terminal, ViewRoleMapModel.TABLE_NAME, "", 1);
		} catch(IllegalArgumentException ie){
			success = false;
			message = ie.getMessage();
			
			logger.error(message,ie);
		} catch (Exception e) {
			message = "未知异常，解析剧本人物失败";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "角色提取失败：" + e.getMessage(), terminal, ViewRoleMapModel.TABLE_NAME, "", 6);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 计算角色在剧组中的场数
	 * @param roleNameList
	 * @param viewContentInfo
	 * @return
	 */
	private Map<String, Integer> calculateRoleViewCount(List<String> roleNameList, List<Map<String, Object>> viewContentInfo) {
		Map<String, Integer> roleNameCountMap = new HashMap<String, Integer>();
		
		for (Map<String, Object> viewContentMap : viewContentInfo) {
			String viewContent = (String) viewContentMap.get("content");
			int isManualSave = (Integer) viewContentMap.get("isManualSave");
			
			if (!StringUtils.isBlank(viewContent)) {
				String[] viewContentArray = viewContent.split(this.lineSeprator);
				for (String viewRoleName : roleNameList) {
					if (!roleNameCountMap.containsKey(viewRoleName)) {
						roleNameCountMap.put(viewRoleName, 0);
					}
					
					for (String lineContent : viewContentArray) {
//						if (isManualSave == 0 && this.scenarioService.checkContainRole(viewRoleName, lineContent) && !roleNameCountMap.containsKey(viewRoleName)) {
//							roleNameCountMap.put(viewRoleName, 1);
//							break;
//						}
						
						if (isManualSave == 0 && this.scenarioService.checkContainRole(viewRoleName, lineContent) && roleNameCountMap.containsKey(viewRoleName)) {
							roleNameCountMap.put(viewRoleName, roleNameCountMap.get(viewRoleName) + 1);
							break;
						}
					}
				}
			}
		}
		
		return roleNameCountMap;
	}
	
	/**
	 * 获取剧本中可能存在的角色
	 * @param viewContentInfo
	 * @return	key表示角色名称  value表示角色总场数
	 */
	private List<String> genProbablyRoleList(List<Map<String, Object>> viewContentInfo) {
		List<String> viewRoleNameList = new ArrayList<String>();	//角色列表
		for (Map<String, Object> viewContentMap : viewContentInfo) {
			String content = (String) viewContentMap.get("content");
			int isManualSave = (Integer) viewContentMap.get("isManualSave");
			
			if (isManualSave == 1) {
				continue;
			}
			
			String[] contentArray = content.split(this.lineSeprator);
			for (String lineContent : contentArray) {
				String[] lineContentArray = lineContent.split(":|：");
				if (lineContentArray.length == 1) {
					continue;
				}
				String firstEle = lineContentArray[0];
				if (firstEle.length() > 4) {
					continue;
				}
				if (RegexUtils.regexFind("\\d", firstEle)) {
					continue;
				}
				if (!viewRoleNameList.contains(firstEle)) {
					viewRoleNameList.add(firstEle);
				}
			}
		}
		
		return viewRoleNameList;
	}
	
	/**
	 * 保存可能存在的角色
	 * @param request
	 * @param roleNames
	 * @return
	 */
	@RequestMapping("/saveProbablyRoles")
	@ResponseBody
	public Map<String, Object> saveProbablyRoles(HttpServletRequest request, String roleNames) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		String message = "";
		boolean success = true;
		try {
			String crewId = getCrewId(request);
			if (StringUtils.isBlank(roleNames)) {
				throw new IllegalArgumentException("请选择角色");
			}
			
			this.scenarioService.refreshFigure(crewId, roleNames);
			this.sysLogService.saveSysLog(request, "角色提取-保存可能存在的角色", terminal, ViewRoleMapModel.TABLE_NAME, null, 1);
		} catch(IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，保存角色失败";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "角色提取-保存可能存在的角色失败:" + e.getMessage(), terminal, ViewRoleMapModel.TABLE_NAME, null, 1);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 把待定角色进行分类
	 * 
	 * 根据当前角色的场数来判断属于那种角色；
	 * 规则：场数大于100或则占总场数的40%则为主演；
	 * 场数大于50或占总场数的10为特约演员；其余的为群众演员
	 */
	@ResponseBody
	@RequestMapping("/classifyViewRole")
	public Map<String, Object> classifyViewRole(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		String message = "";
		boolean success = true;
		try {
			String crewId = getCrewId(request);
			
			Double totalViewCount = null ;
			Map<String, Object> totalInfo = this.viewInfoService.queryViewTotalInfo(crewId);
			if (totalInfo != null) {
				totalViewCount = Double.parseDouble(totalInfo.get("totalViewCount").toString());
			}else {
				totalViewCount = 1.0;
			}
			
			List<Object[]> paramList = new ArrayList<Object[]>();
			List<Map<String, Object>> viewRoleInfoList = this.viewRoleService.queryRoleMapByCrewIdAndRoleType(crewId, ViewRoleType.ToConfirmActor.getValue());
			for (Map<String, Object> viewRoleInfo : viewRoleInfoList) {
				String viewRoleId = (String) viewRoleInfo.get("viewRoleId");
				int viewRoleCount = ((Long) viewRoleInfo.get("viewRoleCount")).intValue();
				
				int viewRoleType = ViewRoleType.MajorActor.getValue();
				
				//计算当前场数与总场数的比值
				double viewRate = viewRoleCount / totalViewCount * 100;
				if (viewRoleCount >= 100 || viewRate >= 40) { // 主演
					viewRoleType = ViewRoleType.MajorActor.getValue();
				} else if (viewRoleCount >= 50 || viewRate >= 10) { // 特约演员
					viewRoleType = ViewRoleType.GuestActor.getValue();
				} else { // 群众演员
					viewRoleType = ViewRoleType.MassesActor.getValue();
				}
				
				Object[] params = new Object[2];
				params[0] = viewRoleType;
				params[1] = viewRoleId;
				paramList.add(params);
			}
			
			this.viewRoleService.updateViewRoleType(paramList);
			
		} catch(IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，自动分类角色失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	
	
	
	/**
	 * 保存剧本书签信息
	 * @param seriesViewNo	集场编号（用-隔开）
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveSceBookMark")
	public Map<String, Object> saveSceBookMark(HttpServletRequest request, String seriesViewNo) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String crewId = getCrewId(request);
		UserInfoModel userInfo = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);
		
		String message = "";
		boolean success = true;
		try {
			if (StringUtils.isBlank(seriesViewNo)) {
				throw new IllegalArgumentException("请提供需要获取内容的集场编号");
			}
			
			String[] seriesViewArr = seriesViewNo.split(SepratorConstant.SEP_CROSS_EN);
			int seriesNo = Integer.parseInt(seriesViewArr[0]);
			String viewNo = seriesViewArr[1];
			
			ViewInfoModel viewInfo = this.viewInfoService.queryOneByCrewIdAndSeriaViewNo(crewId, seriesNo, viewNo);
			
			if (viewInfo != null) {
				BookMarkModel bookMarkInfo = new BookMarkModel();
				bookMarkInfo.setId(UUIDUtils.getId());
				bookMarkInfo.setType(BookmarkType.BookMarkType.getValue());
				bookMarkInfo.setCrewId(crewId);
				bookMarkInfo.setUserId(userInfo.getUserId());
				bookMarkInfo.setValue(viewInfo.getViewId());
				
				this.scenarioService.saveSceBookMark(bookMarkInfo);
			}
			
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch(Exception e) {
			message = "未知异常，保存书签失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 导出剧本
	 * @param request 
	 * @param type 导出方式    1：整剧本导出   2：分集导出
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exportScenario")
	public Map<String, Object> exportScenario(HttpServletRequest request, HttpServletResponse response, Integer type) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String crewId = getCrewId(request);
		
		String message = "";
		boolean success = true;
		//导出文件在服务器中的路径
		String downloadPath = "";
		
		try {
			if (type == null) {
				throw new IllegalArgumentException("导出方式为必填!");
			}
			
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			String crewName = crewInfo.getCrewName(); //剧组名称
			Integer crewType = crewInfo.getCrewType();	//剧组类型
			
			List<ViewInfoModel> viewList = this.viewInfoService.queryByCrewId(crewId, null);
			if (viewList == null || viewList.size() == 0) {
				throw new IllegalArgumentException("请先上传剧本");
			}
			
			//整体导出的形式
			if (type == Constants.SCENARIO_EXPORT_TYPE_ALL) {
				downloadPath = this.exportByAll(response, crewId, crewName, crewType);
			}
			//分集导出的形式，电影剧本只采用分集的导出形式
			if (type == Constants.SCENARIO_EXPORT_TYPE_SINGLE) {
				downloadPath = this.exportBySeries(response, crewId, crewName, crewType);
			}
			
			message = "导出剧本成功";
			resultMap.put("downloadFilePath", downloadPath);
			this.sysLogService.saveSysLog(request, "剧本导出", terminal, ViewInfoModel.TABLE_NAME, null, 3);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			
			logger.error(ie.getMessage(), ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，导出剧本失败";
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "剧本导出失败："+e.getMessage(), terminal, ViewInfoModel.TABLE_NAME, null, 6);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}

	/**
	 * 分集导出剧本
	 * @param response
	 * @param crewId
	 * @param crewName
	 * @param out
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private String exportBySeries(HttpServletResponse response, String crewId,
			String crewName, Integer crewType) throws FileNotFoundException, IOException {
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
		
		//定义输入输出流
		FileOutputStream out = null;
		FileInputStream in = null;
		ZipOutputStream zipOut = null;
		
		//定义文件路径和后缀名
		String fileName = "";
		String suffix = "";
		
		//获取存储根路径
		Properties properties = PropertiesUitls.fetchProperties("/config.properties");
		String baseDownloadPath = properties.getProperty("downloadPath");
		String storePath = baseDownloadPath + "scenario/pc/" + sdf1.format(new Date()) + "/";	//存储路径
		
		List<File> scenarioFileList = new ArrayList<File>();
		
		//循环集次，根据集次查询单集数据
		//1.根据剧组id查询出当前共有多少集次,单个集次共有多少场次
		List<Map<String, Object>> seriesNosMap = this.viewInfoService.querySeriesNoByCrewId(crewId);
		
		for (Map<String, Object> map : seriesNosMap) {
			try {
				Integer seriesNo = (Integer) map.get("seriesNo");
				List<Map<String, Object>> scenarioViewList = this.viewInfoService.queryScenarioViewInfo(crewId, seriesNo, null, null);
				
				String preFileName = "";
				if (crewType == CrewType.Movie.getValue()) {
					preFileName = "《" + crewName + "》剧本_";
				} else {
					preFileName = "《" + crewName + "》第"+ seriesNo +"集剧本_";
				}
				
				fileName = preFileName + sdf2.format(new Date());	//生成下载的文件名
				suffix = ".doc";
				File scenarioFile = new File(storePath + fileName + suffix);
				if (!scenarioFile.getParentFile().isDirectory()) {
					scenarioFile.getParentFile().mkdirs();
				}
				
				out = new FileOutputStream(scenarioFile);
				
				// 新建一个文档
				XWPFDocument doc = new XWPFDocument();
				// 创建一个段落
				XWPFParagraph para = doc.createParagraph();
				XWPFRun run = null;
				
				//循环指定集次下的剧本数据，进行数据装填
				for (Map<String, Object> scenarioView : scenarioViewList) {
					StringBuilder singleViewScenario = new StringBuilder();
					
					String viewNo = (String) scenarioView.get("viewNo");
					String site = (String) scenarioView.get("site");
					String atmosphereName = (String) scenarioView.get("atmosphereName");
					String content = (String) scenarioView.get("content");
					String viewRoleNames = (String) scenarioView.get("viewRoleNames");
					String viewLocations = (String) scenarioView.get("viewLocations");
					
					if (crewType == CrewType.Movie.getValue()) {
						//电影剧本只显示场次
						singleViewScenario.append(viewNo);
					} else {
						singleViewScenario.append(seriesNo + "-" + viewNo);
					}
					if (!StringUtils.isBlank(viewLocations)) {
						singleViewScenario.append(" " + viewLocations);
					}
					if (!StringUtils.isBlank(atmosphereName)) {
						singleViewScenario.append(" " + atmosphereName);
					}
					if (!StringUtils.isBlank(atmosphereName) && !StringUtils.isBlank(site)) {
						singleViewScenario.append("/");
					}
					if (StringUtils.isBlank(atmosphereName) && !StringUtils.isBlank(site)) {
						singleViewScenario.append(" ");
					}
					if (!StringUtils.isBlank(site)) {
						singleViewScenario.append(site);
					}
					
					//设置导出剧本头信息
					String title = singleViewScenario.toString();
					run = para.createRun();
					run.setBold(true); // 加粗
					run.setFontSize(12);
					run.setText(title);
					run.addCarriageReturn();
					
					if (!StringUtils.isBlank(viewRoleNames)) {
						run.setText("人物：" + viewRoleNames);
						run.addCarriageReturn();
					}
					
					//设置剧本的正文信息
					run = para.createRun();
					run.setFontSize(12);
					if (!StringUtils.isBlank(content)) {
						String[] contentArr = content.split("\r\n");
						for (String singleLineC : contentArr) {
							run.setText(singleLineC);
							run.addCarriageReturn();
						}
					}
					run.addCarriageReturn();
				}
				doc.write(out);
				
				scenarioFileList.add(scenarioFile);
				
				//压缩剧本
				fileName = "《" + crewName + "》剧本" + sdf2.format(new Date());
				suffix = ".zip";
				File zipFile = new File(storePath + fileName + suffix);
				if (!zipFile.getParentFile().isDirectory()) {
					zipFile.getParentFile().mkdirs();
				}
				
				//导出压缩文件
				zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
				byte[] buf = new byte[1024];
				for (int i = 0; i < scenarioFileList.size(); i++) {
					File file = scenarioFileList.get(i);  
					in = new FileInputStream(file);  
					zipOut.putNextEntry(new ZipEntry(file.getName()));  
					int len;  
					while ((len = in.read(buf)) > 0) {
						zipOut.write(buf, 0, len);  
					}
					zipOut.closeEntry();  
					in.close();
				}
			} catch (Exception e) {
				throw new IllegalArgumentException("未知异常，分集导出剧本失败", e);
				
			} finally {
				
				//关闭资源
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (zipOut != null) {
					zipOut.close();
				}
			}
		}
		
		//由于源文件已进行压缩,需要删除所有的源文件节省空间
		for (File scenarioFile : scenarioFileList) {
			scenarioFile.delete();
		}
		
		//定义下载地址
		String downloadPath = storePath + fileName + suffix;
		
		return downloadPath;
	}

	/**
	 * 整体导出剧本（不分集）
	 * @param response
	 * @param crewId
	 * @param crewName
	 * @param out
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private String exportByAll(HttpServletResponse response, String crewId, String crewName, int crewType) throws FileNotFoundException,
			IOException, UnsupportedEncodingException {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
		
		FileOutputStream out = null;
		String downloadPath = "";
		try {
			//获取存储根路径
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String baseDownloadPath = properties.getProperty("downloadPath");
			
			//生成下载的文件名
			String fileName = "《" + crewName + "》剧本_" + sdf2.format(new Date());	
			//存储路径
			String storePath = baseDownloadPath + "scenario/pc/" + sdf1.format(new Date()) + "/";
			//下载文件的后缀名
			String suffix = ".doc";
			File scenarioFile = new File(storePath + fileName + suffix);
			
			//当前存储文件的文件夹若	不存在则创建一个新的文件夹
			if (!scenarioFile.getParentFile().isDirectory()) {
				scenarioFile.getParentFile().mkdirs();
			}
			//生成一个输出流
			out = new FileOutputStream(scenarioFile);
			
			// 新建一个文档
			XWPFDocument doc = new XWPFDocument();
			// 创建一个段落
			XWPFParagraph para = doc.createParagraph();
			XWPFRun run = null;
			
			//根据剧本id查询出剧本的场景总数
			int totalCount = this.viewInfoService.countScenarioViewInfo(crewId);
			int batchCount = totalCount / batchSize; //batchSize为批处理的数量,在当前环境中设置为1000
			for (int i = 0; i < batchCount; i++) {
				
				Page page = new Page();
				page.setPageNo(i + 1);
				page.setPagesize(batchSize);
				
				//根据剧组id分页查询出场景的详细信息
				List<Map<String, Object>> scenarioViewList = this.viewInfoService.queryScenarioViewInfo(crewId, null, page,null);
				
				for (Map<String, Object> scenarioView : scenarioViewList) {
					StringBuilder singleViewScenario = new StringBuilder();
					
					int seriesNo = (Integer) scenarioView.get("seriesNo"); //取出集次编号
					String viewNo = (String) scenarioView.get("viewNo"); //取出场次编号
					String site = (String) scenarioView.get("site"); //内外景
					String atmosphereName = (String) scenarioView.get("atmosphereName"); //气氛
					String content = (String) scenarioView.get("content"); //剧本内容
					String viewRoleNames = (String) scenarioView.get("viewRoleNames"); //演员名称
					String viewLocations = (String) scenarioView.get("viewLocations"); //场景拍摄地点
					
					if (crewType == CrewType.Movie.getValue()) {
						//电影剧本只显示场次
						singleViewScenario.append(viewNo);
					} else {
						singleViewScenario.append(seriesNo + "-" + viewNo);
					}
					
					//singleViewScenario.append(seriesNo + "-" + viewNo);
					if (!StringUtils.isBlank(viewLocations)) {
						singleViewScenario.append(" " + viewLocations);
					}
					if (!StringUtils.isBlank(atmosphereName)) {
						singleViewScenario.append(" " + atmosphereName);
					}
					if (!StringUtils.isBlank(atmosphereName) && !StringUtils.isBlank(site)) {
						singleViewScenario.append("/");
					}
					if (StringUtils.isBlank(atmosphereName) && !StringUtils.isBlank(site)) {
						singleViewScenario.append(" ");
					}
					if (!StringUtils.isBlank(site)) {
						singleViewScenario.append(site);
					}
					
					//设置输出标题的格式的格式
					String title = singleViewScenario.toString();
					run = para.createRun();
					run.setBold(true); // 加粗
					run.setFontSize(12);
					run.setText(title);
					run.addCarriageReturn();
					
					if (!StringUtils.isBlank(viewRoleNames)) {
						run.setText("人物：" + viewRoleNames);
						run.addCarriageReturn();
					}
					
					//设置输出正文的格式
					run = para.createRun();
					run.setFontSize(12);
					if (!StringUtils.isBlank(content)) {
						String[] contentArr = content.split("\r\n");
						for (String singleLineC : contentArr) {
							run.setText(singleLineC);
							run.addCarriageReturn();
						}
					}
					run.addCarriageReturn();
				}
			}

			Page page = new Page(); 
			page.setPageNo(batchCount + 1);
			int pageSize = totalCount - batchCount * batchSize;
			page.setPagesize(batchSize);
			if (pageSize != 0) {
				//根据剧组id分页查询出场景信息
				List<Map<String, Object>> scenarioViewList = this.viewInfoService.queryScenarioViewInfo(crewId, null, page, null);
				
				for (Map<String, Object> scenarioView : scenarioViewList) {
					StringBuilder singleViewScenario = new StringBuilder();
					
					int seriesNo = (Integer) scenarioView.get("seriesNo");
					String viewNo = (String) scenarioView.get("viewNo");
					String site = (String) scenarioView.get("site");
					String atmosphereName = (String) scenarioView.get("atmosphereName");
					String content = (String) scenarioView.get("content");
					String viewRoleNames = (String) scenarioView.get("viewRoleNames");
					String viewLocations = (String) scenarioView.get("viewLocations");
					
					if (crewType == CrewType.Movie.getValue()) {
						//电影剧本只显示场次
						singleViewScenario.append(viewNo);
					} else {
						singleViewScenario.append(seriesNo + "-" + viewNo);
					}
					//singleViewScenario.append(seriesNo + "-" + viewNo);
					
					if (!StringUtils.isBlank(viewLocations)) {
						singleViewScenario.append(" " + viewLocations);
					}
					if (!StringUtils.isBlank(atmosphereName)) {
						singleViewScenario.append(" " + atmosphereName);
					}
					if (!StringUtils.isBlank(atmosphereName) && !StringUtils.isBlank(site)) {
						singleViewScenario.append("/");
					}
					if (StringUtils.isBlank(atmosphereName) && !StringUtils.isBlank(site)) {
						singleViewScenario.append(" ");
					}
					if (!StringUtils.isBlank(site)) {
						singleViewScenario.append(site);
					}
					
					//设置导出的标题头
					String title = singleViewScenario.toString();
					run = para.createRun();
					run.setBold(true); // 加粗
					run.setFontSize(12);
					run.setText(title);
					run.addCarriageReturn();
					
					if (!StringUtils.isBlank(viewRoleNames)) {
						run.setText("\r\n" + "人物：" + viewRoleNames);
						run.addCarriageReturn();
					}
					
					//设置导出的正文
					run = para.createRun();
					run.setFontSize(12);
					if (!StringUtils.isBlank(content)) {
						String[] contentArr = content.split("\r\n");
						for (String singleLineC : contentArr) {
							run.setText(singleLineC);
							run.addCarriageReturn();
						}
					}
					run.addCarriageReturn();
				}
			}
			doc.write(out);
			
			downloadPath = storePath + fileName + suffix;
		} catch (Exception e) {
			throw new IllegalArgumentException("未知异常，导出剧本失败", e);
		} finally {
			if (out != null) {
				out.close();
			}
		}
		
		return downloadPath;
	}
	
	/**
	 * 保存剧本信息
	 * @param request
	 * @param viewId 场景id
	 * @param title 场景剧本标题
	 * @param content 场景剧本内容
	 * @return
	 */
	@Deprecated
	@ResponseBody
	@RequestMapping("/saveScenarioInfo")
	public Map<String, Object> saveScenarioInfo(HttpServletRequest request, String viewId, String title, String content) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		String idArrayStr = "";
		try {
			
			if (viewId ==null || "".equals(viewId)) {
				throw new IllegalArgumentException("场景id不能为空!");
			}
			
			if (title == null || "".equals(title)) {
				throw new IllegalArgumentException("剧本场景标题不能为空!");
			}
			
			if (content == null || "".equals(content)) {
				throw new IllegalArgumentException("请填写剧本内容");
			}
			
			ViewContentModel viewContent = this.viewContentService.queryByViewId(viewId);
			viewContent.setTitle(title);
			viewContent.setContent(content);
			viewContent.setStatus(ViewContentStatus.UpdateNotPublished.getValue());
			viewContent.setFigureprint(MD5Util.MD5(content));
			
			this.viewContentService.updateOne(viewContent);
			
			idArrayStr = viewContent.getContentId();
			message = "保存成功";
//			this.sysLogService.saveSysLog(request, "保存剧本内容", terminal, ViewContentModel.TABLE_NAME, idArrayStr,2);
			
		}catch (IllegalArgumentException ie){
			success = false;
			message = ie.getMessage();
			
			logger.error(message, ie);
		}catch (Exception e) {
			success = false;
			message = "未知异常，保存剧本失败";
			
			logger.error("未知异常，保存剧本失败", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 发布剧本
	 * @param request
	 * @param title 剧本场景标题
	 * @param content 剧本内容 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/publishScenario")
	public Map<String, Object> publishScenario(HttpServletRequest request, String title, String content, boolean autoShowPublishWin) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		String crewId = this.getCrewId(request);
		String userId = this.getLoginUserId(request);
		try {
			if (StringUtils.isBlank(title)) {
				throw new IllegalArgumentException("请填写标题");
			}
			
			if (StringUtils.isBlank(content)) {
				throw new IllegalArgumentException("请填写内容");
			}
			
			this.scenarioService.publishScenario(userId, crewId, title, content, autoShowPublishWin);
//			this.sysLogService.saveSysLog(request, "发布剧本", terminal, ViewContentModel.TABLE_NAME, null,2);
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			
			logger.error(ie.getMessage(), ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，发布剧本失败";
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 校验是否是第一次上传剧本
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/checkUploadedScenaris")
	public Map<String, Object> checkUploadedScenaris(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		String message = "";
		boolean success = true;
		try {
			String crewId = getCrewId(request);
			
			//根据剧组的id从数据库中查询是否上传过剧本
			boolean exist = false;
			ScenarioInfoModel scenarioInfo = this.scenarioService.queryLastScenario(crewId);
			if (scenarioInfo != null) {
				exist = true;
			}
			
			resultMap.put("exist", exist);
		} catch (IllegalArgumentException ie){
			success = false;
			message = ie.getMessage();
			
			logger.error(message, ie);
		} catch(Exception e) {
			message = "未知异常，校验失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
     * 校验剧本是否有修改
     * @return
     */
    @ResponseBody
    @RequestMapping("/checkHasNewEdit")
    public Map<String, Object> checkHasNewEdit(HttpServletRequest request) {
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message = "";
    	boolean success = true;
    	
    	try {
    		String crewId = this.getCrewId(request);
    		String userId = this.getLoginUserId(request);
    		int count = this.viewContentService.countNotPublishedContentList(crewId);
    		
    		boolean hasNewEdit = false;
    		if (count > 0) {
    			hasNewEdit = true;
    		}
    		
    		//是否需要自动弹窗提示发布
    		PublishScenarioSettingModel setting = this.publishScenarioSettingService.queryByCrewIdAndUserId(crewId, userId);
			boolean needShow = true;
			if (setting != null) {
				needShow = setting.getAutoShowPublishWin();
			}
			
			resultMap.put("needShow", needShow);
    		resultMap.put("hasNewEdit", hasNewEdit);
    	} catch (Exception e) {
			message = "未知异常，校验失败";
			success = false;
			
			logger.error(message, e);
		}
    	
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
    }
    
    /**
     * 查询发布内容
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryPublishContent")
    public Map<String, Object> queryPublishContent(HttpServletRequest request) {
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message = "";
    	boolean success = true;
    	
    	try {
    		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
    		
    		List<Map<String, Object>> notPublishedSeriesInfo = this.viewContentService.queryNotPublishedSeriesNo(crewInfo.getCrewId());
    		
    		String publishContent = "";
    		for (Map<String, Object> seriesNoMap : notPublishedSeriesInfo) {
    			Integer seriesNo = (Integer) seriesNoMap.get("seriesNo");
    			publishContent += seriesNo + ",";
    		}
    		
    		if (!StringUtils.isBlank(publishContent)) {
    			publishContent = publishContent.substring(0, publishContent.length() - 1);
    			publishContent = "第" + publishContent + "集剧本有改动，请及时更新查看";
    		}
    		
    		//电影剧组特殊处理
    		if (crewInfo.getCrewType() == CrewType.Movie.getValue() || crewInfo.getCrewType() == CrewType.InternetMovie.getValue()) {
    			publishContent = "剧本有改动，请及时更新查看";
    		}
    		
    		resultMap.put("publishContent", publishContent);
    	} catch (Exception e) {
			message = "未知异常，获取发布内容失败";
			success = false;
			
			logger.error(message, e);
		}
    	
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
    }
    
    /**
     * 查询场景内容比对信息
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryViewContentCompareInfo")
    public Map<String, Object> queryViewContentCompareInfo(HttpServletRequest request, String viewId) {
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message = "";
    	boolean success = true;
    	
    	try {
    		if (StringUtils.isBlank(viewId)) {
    			throw new IllegalArgumentException("请提供场景ID");
    		}
    		String crewId = this.getCrewId(request);
    		String userId = this.getLoginUserId(request);
    		
    		String currTitle = "";
    		String currContent = "";
    		String preTitle = "";
    		String preContent = "";
    		
    		ViewContentModel currentViewContent = this.viewContentService.queryByViewId(viewId);
    		if (currentViewContent != null) {
    			currTitle = currentViewContent.getTitle();
    			currContent = currentViewContent.getContent();
    		}
    		HistoryViewContentModel lastHistoryContent = this.historyViewContentService.queryPreVersionContent(crewId, viewId);
    		if (lastHistoryContent != null) {
    			preTitle = lastHistoryContent.getTitle();
    			preContent = lastHistoryContent.getContent();
    		}
    		
    		//更新书签信息
    		BookMarkModel bookMarkInfo = new BookMarkModel();
			bookMarkInfo.setId(UUIDUtils.getId());
			bookMarkInfo.setType(BookmarkType.BookMarkType.getValue());
			bookMarkInfo.setCrewId(crewId);
			bookMarkInfo.setUserId(userId);
			bookMarkInfo.setValue(viewId);
			
			this.scenarioService.saveSceBookMark(bookMarkInfo);
    		
    		resultMap.put("currTitle", currTitle);
    		resultMap.put("currContent", currContent);
    		resultMap.put("preTitle", preTitle);
    		resultMap.put("preContent", preContent);
    	} catch (Exception e) {
			message = "未知异常";
			success = false;
			
			logger.error(message, e);
		}
    	
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
    }
    
    /**
     * 查询指定场次剧本的三个历史版本号
     * @param request
     * @param viewId
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryVersionList")
    public Map<String, Object> queryVersionList(HttpServletRequest request, String viewId) {
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message = "";
    	boolean success = true;
    	
    	try {
    		String crewId = this.getCrewId(request);
    		List<Map<String, Object>> versionList = this.historyViewContentService.queryVersionList(crewId, viewId);
    		
    		resultMap.put("versionList", versionList);    		
    	} catch (Exception e) {
			message = "未知异常";
			success = false;
			
			logger.error(message, e);
		}
    	
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
    }
    
    /**
     * 查询场景指定版本的剧本内容
     * @param request
     * @param viewId
     * @param version
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryVersionViewContent")
    public Map<String, Object> queryVersionViewContent(HttpServletRequest request, String viewId, String version) {
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message = "";
    	boolean success = true;
    	
    	try {
    		if (StringUtils.isBlank(viewId)) {
    			throw new IllegalArgumentException("场景ID不能为空");
    		}
    		if (StringUtils.isBlank(version)) {
    			throw new IllegalArgumentException("版本不能为空");
    		}
    		String crewId = this.getCrewId(request);
    		HistoryViewContentModel content = this.historyViewContentService.queryByViewIdAndVersion(crewId, viewId, version);
    		
    		resultMap.put("content", content.getTitle() + this.lineSeprator + content.getContent()); 		
    	} catch (IllegalArgumentException ie) {
    		message = ie.getMessage();
    		success = false;
    	} catch (Exception e) {
			message = "未知异常";
			success = false;
			
			logger.error(message, e);
		}
    	
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
    }
}
