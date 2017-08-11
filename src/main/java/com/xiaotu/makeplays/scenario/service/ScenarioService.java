package com.xiaotu.makeplays.scenario.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.goods.dao.GoodsInfoDao;
import com.xiaotu.makeplays.goods.dao.ViewGoodsMapDao;
import com.xiaotu.makeplays.goods.model.GoodsInfoModel;
import com.xiaotu.makeplays.goods.model.ViewGoodsInfoMap;
import com.xiaotu.makeplays.message.model.MessageInfoModel;
import com.xiaotu.makeplays.message.model.constants.MessageInfoStatus;
import com.xiaotu.makeplays.message.model.constants.MessageType;
import com.xiaotu.makeplays.message.service.MessageInfoService;
import com.xiaotu.makeplays.mobile.push.umeng.model.android.AndroidPushMsg;
import com.xiaotu.makeplays.mobile.push.umeng.model.ios.IOSPushMsg;
import com.xiaotu.makeplays.mobile.push.umeng.service.android.UmengAndroidPushService;
import com.xiaotu.makeplays.mobile.push.umeng.service.ios.UmengIOSPushService;
import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;
import com.xiaotu.makeplays.roleactor.model.constants.ViewRoleType;
import com.xiaotu.makeplays.roleactor.service.ViewRoleService;
import com.xiaotu.makeplays.scenario.controller.dto.ScenarioViewDto;
import com.xiaotu.makeplays.scenario.dao.BookMarkDao;
import com.xiaotu.makeplays.scenario.dao.ScenarioDao;
import com.xiaotu.makeplays.scenario.dao.ScripteleInfoDao;
import com.xiaotu.makeplays.scenario.dao.SeparatorInfoDao;
import com.xiaotu.makeplays.scenario.model.BookMarkModel;
import com.xiaotu.makeplays.scenario.model.PublishScenarioSettingModel;
import com.xiaotu.makeplays.scenario.model.ScenarioInfoModel;
import com.xiaotu.makeplays.scenario.model.ScripteleInfoModel;
import com.xiaotu.makeplays.scenario.model.SeparatorInfoModel;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.model.constants.UserClientType;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.OfficeUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.RegexUtils;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.view.dao.HistoryViewContentDao;
import com.xiaotu.makeplays.view.dao.ViewContentDao;
import com.xiaotu.makeplays.view.dao.ViewInfoDao;
import com.xiaotu.makeplays.view.dao.ViewRoleMapDao;
import com.xiaotu.makeplays.view.model.HistoryViewContentModel;
import com.xiaotu.makeplays.view.model.ViewContentModel;
import com.xiaotu.makeplays.view.model.ViewInfoModel;
import com.xiaotu.makeplays.view.model.ViewRoleMapModel;
import com.xiaotu.makeplays.view.model.constants.ShootStatus;
import com.xiaotu.makeplays.view.model.constants.ViewContentStatus;
import com.xiaotu.makeplays.view.service.ViewContentService;
import com.xiaotu.makeplays.view.service.ViewInfoService;

@Service
public class ScenarioService {
	
	/**
	 * 行分隔符
	 */
	private final String lineSeprator = "\r\n";
	
	//带有“回忆”、“闪回”字段的标题，需要把标题中对应的内容去掉，再把其放到内容中
	private static List<String> lineSeqWordList = new ArrayList<String>();	//标题中需要做替换且变成换行内容的元素
	
	/**
	 * 批处理最大数量
	 */
	private final int batchSize = 300;
	
	@Autowired
	private ScenarioDao scenarioDao;
	
	@Autowired
	private ViewInfoService viewService;
	
	@Autowired
	private ViewInfoDao viewInfoDao;
	
	@Autowired
	private ScripteleInfoDao scripteleInfoDao;
	
	@Autowired
	private SeparatorInfoDao separatorInfoDao;
	
	@Autowired
	private BookMarkDao bookMarkDao;
	
	@Autowired
	private ViewRoleService viewRoleService;
	
	@Autowired
	private ViewContentService viewContentService;
	
	@Autowired
	private ViewRoleMapDao viewRoleMapDao;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UmengAndroidPushService umengAndroidPushService;
	
	@Autowired
	private UmengIOSPushService umengIOSPushService;
	
	@Autowired
	private HistoryViewContentDao historyViewContentDao;
	
	@Autowired
	private ViewContentDao viewContentDao;
	
	@Autowired
	private DownloadScenarioRecordService downloadScenarioRecordService;
	
	@Autowired
	private ViewGoodsMapDao viewGoodsInfoMapDao;
	
	@Autowired
	private GoodsInfoDao goodsInfoDao;
	
	@Autowired
	private MessageInfoService messageInfoService;
	
	@Autowired
	private CrewInfoService crewInfoService;
	
	@Autowired
	private PublishScenarioSettingService publishScenarioSettingService;
	
	/*
	 * 不完全符合自定义格式信息但是符合集场规则的场景信息
	 * map中key为"seriesNo"表示集次
	 * key为"viewNo"表示场次
	 */
	private List<Map<String, String>> notFullMatchingInfo = new ArrayList<Map<String, String>>();
	
	/**
	 * 不完全符合自定义格式信息但是符合集场规则的场景标题
	 */
	private List<String> notFullMatchingTitleList = new ArrayList<String>();
	
	/*
	 * 标题错误信息
	 */
	private List<String> titleErrorMsg = new ArrayList<String>();
	
	/*
	 * 有问题的集-场号列表 
	 */
	private List<String> errorTitleList = new ArrayList<String>();
	
	/**
	 * 无效的场次标题，此类数据实际上为内容
	 * 无效的条件有：
	 * 1、场次中汉字超过3个，比如：10排的战士们
	 * 2、带有汉字的场次没有对应的不带汉字的场次，比如：有场次“10排中”却没有场次“10”，则判定“10排中”场次所在的标题不是标题
	 * 3、数字值不能大于400
	 */
	private List<String> inValidViewNoList = new ArrayList<String>();

	static {
		lineSeqWordList.add("闪回");
		lineSeqWordList.add("回忆");
	}

	/**
	 * 保存剧本基本信息
	 * @param scenarioInfo
	 * @throws Exception
	 */
	public void addScenario(ScenarioInfoModel scenarioInfo) throws Exception {
		this.scenarioDao.add(scenarioInfo);
	}
	
	/**
	 * 更新剧本基本信息
	 * @param scenarioInfo
	 * @throws Exception
	 */
	public void updateScenario(ScenarioInfoModel scenarioInfo) throws Exception {
		this.scenarioDao.update(scenarioInfo, "scenarioId");
	}
	
	/**
	 * 分析上传的剧本内容
	 * @param storePath
	 * @throws OpenXML4JException 
	 * @throws XmlException 
	 * @throws IOException 
	 * @return 分析后的数据
	 */
	public Map<String, Object> analysisScenario(String crewId, String storePath, 
			int lineCount, int wordCount, 
			String scenarioFormat, boolean hasSeriesNoTag, 
			String extralSeriesNo, boolean groupSeriesNoFlag, 
			boolean supportCNViewNo, boolean pageIncludeTitle) throws IOException, XmlException, OpenXML4JException {
		
		//剧本元素信息
		List<ScripteleInfoModel> scripteleInfoList = this.scripteleInfoDao.queryManyByMutiCondition(null, null);
		
		//剧本符号信息
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("crewId", crewId);
		List<SeparatorInfoModel> separatorInfoList = this.separatorInfoDao.queryManyByMutiCondition(conditionMap, null);
		
		//由于docx文档读取自动编号时，会把"-"转换为"."，因此，此处针对自定义格式和剧本内容进行把"-"替换成"."的操作
		List<String> originalScenarioFormatList = this.genScenarioFormatList(scenarioFormat);
		
		this.checkFormatValid(originalScenarioFormatList);
		
		Map<String, Object> formatInfo = this.genFormatInfo(scripteleInfoList, separatorInfoList, originalScenarioFormatList);
		
		//读取文件中的内容
		String fileContent = OfficeUtils.readWordFile(storePath);
		fileContent = com.xiaotu.makeplays.utils.StringUtils.ToDBC(fileContent);
		/*
		 * 解析文件中的内容
		 * 1、如果用户上传的文件（.doc / .docx）所有场次均非系统可识别“集-场”的格式，提示 “系统无法识别你所上传的剧本的场次信息，请重新上传文件”
		 * 2、如果剧本本身有重复场景，提示用户“剧本中有重复场景，请重新上传文件”。
		 * 3、当前上传剧本所包含的场次已存在于场景表中，需要进行“跳过/替换”处理
		 * 4、已存在于场景表中但当前所上传的剧本文本中不存在的场次，需要进行“保留/不保留”处理
		 */
		
		List<String> contentList = this.getSceContent(fileContent);
		
		/*
		 * 校验文件中的内容格式是否符合模板（1,2两点）
		 */

		//校验带有中文的场次是否符合规范
		this.checkViewNoValid(contentList, formatInfo, originalScenarioFormatList, scripteleInfoList, separatorInfoList, hasSeriesNoTag, extralSeriesNo, groupSeriesNoFlag, supportCNViewNo);
		
		//校验剧本中每个标题是否合法
		this.checkSceTitleIllegal(contentList, formatInfo, originalScenarioFormatList, scripteleInfoList, separatorInfoList, hasSeriesNoTag, extralSeriesNo, groupSeriesNoFlag, supportCNViewNo);
		
		
		//校验此剧本中是否存在相同的场次 
		this.checkSceHasSameView(contentList, formatInfo, originalScenarioFormatList, scripteleInfoList, separatorInfoList, hasSeriesNoTag, extralSeriesNo, groupSeriesNoFlag, supportCNViewNo);
		
		/*
		 * 解析剧本详细信息
		 */
		//解析剧本中每场的标题和内容详细信息
		List<ScenarioViewDto> sceDtoList = this.genSceDetailInfo(contentList, lineCount, wordCount, formatInfo, originalScenarioFormatList, scripteleInfoList, separatorInfoList, hasSeriesNoTag, extralSeriesNo, groupSeriesNoFlag, supportCNViewNo, pageIncludeTitle);
		
		//把剧本中的场次信息和数据库中的信息对比(3,4两点)
		Map<String, Object> viewDataMap = this.compareViewWithDataInDB(crewId, sceDtoList);
		viewDataMap.put("notFullMatchingInfo", notFullMatchingInfo);
		viewDataMap.put("notFullMatchingTitleList", notFullMatchingTitleList);
		viewDataMap.put("titleErrorMsg", titleErrorMsg);
		
		notFullMatchingInfo = new ArrayList<Map<String, String>>();
		notFullMatchingTitleList = new ArrayList<String>();
		titleErrorMsg = new ArrayList<String>();
		errorTitleList = new ArrayList<String>();
		inValidViewNoList = new ArrayList<String>();
		
		return viewDataMap;
	}
	
	/**
	 * 校验自定义的格式是否符合标准，此处不允许两种情况：
	 * 1、元素之间不存在分隔符
	 * 2、人物元素后面还有其他的元素
	 * 3、集场元素不是在开头
	 * @param scenarioFormatList
	 */
	public void checkFormatValid(List<String> scenarioFormatList) {
		
		int preScripteleIndex = -1;	//上一个元素的下标
		int figureIndex = -1;	//人物元素的下标
		
		int firstScriptelteIndex = -1; 	//第一个元素的下标
		
		boolean hasViewNoScript = false;	//是否含有场次元素
		
		List<ScripteleInfoModel> scripteleInfoList = this.scripteleInfoDao.queryManyByMutiCondition(null, null);
		
		for (int i = 0, len = scenarioFormatList.size(); i < len; i++) {
			String currAtomic = scenarioFormatList.get(i);
			if (currAtomic.indexOf("e") == 0) {
				if (firstScriptelteIndex == -1) {
					firstScriptelteIndex = i;
				}
				
				//元素之间不存在分隔符
				if (preScripteleIndex != -1 && preScripteleIndex == i - 1) {
					String preAtomic = scenarioFormatList.get(preScripteleIndex);
					
					String currScripeName = null;
					String preScripeName = null;
					
					for (ScripteleInfoModel scripteleInfo : scripteleInfoList) {
						if (scripteleInfo.getEleId().equals(currAtomic)) {
							currScripeName = scripteleInfo.getEleName();
							continue;
						}
						if (scripteleInfo.getEleId().equals(preAtomic)) {
							preScripeName = scripteleInfo.getEleName();
							continue;
						}
						if (currScripeName != null && preScripeName != null) {
							break;
						}
					}
					
					throw new IllegalArgumentException("元素'" + preScripeName + "'和'" + currScripeName + "'之间不能没有分隔符，请修改后重新上传");
				}
				
				//人物元素后面还有其他的元素
				if (currAtomic.equals(Constants.SCRIPTELE_FIGURE)) {
					figureIndex = i;
				}
				
				if (!currAtomic.equals(Constants.SCRIPTELE_FIGURE) && figureIndex != -1 && i > figureIndex) {
					throw new IllegalArgumentException("人物元素必须在最后");
				}
				
				//必须含有“场次”元素
				if (currAtomic.equals(Constants.SCRIPTELE_VIEWNO)) {
					hasViewNoScript = true;
				}
				
				preScripteleIndex = i;
			}
		}
		
		//第一个元素既不是集，也不是场元素
		if (!scenarioFormatList.get(firstScriptelteIndex).equals(Constants.SCRIPTELE_SERIESNO) & !scenarioFormatList.get(firstScriptelteIndex).equals(Constants.SCRIPTELE_VIEWNO)) {
			throw new IllegalArgumentException("自定义格式的第一元素必须是集或者场");
		}
		if (!hasViewNoScript) {
			throw new IllegalArgumentException("您定义的格式中没有‘场’元素，请重新定义");
		}
	}

	/**
	 * 把剧本内容格式化为List<String> 格式
	 * @param fileContent 剧本内容
	 * @throws IOException 
	 * @return
	 */
	private List<String> getSceContent(String fileContent) throws IOException {
		//剧本文本读取到缓冲区 
		ByteArrayInputStream bais = new ByteArrayInputStream(fileContent.getBytes());
		BufferedReader reader = new BufferedReader(new InputStreamReader(bais));
		
		//缓冲区中的内容读取到集合list中
		List<String> list = new ArrayList<String>();
		String line = null;
		while((line = reader.readLine()) != null) {
			list.add(line);
		}
		return list;
	}
	
	/**
	 * 根据自定义的格式获取分析剧本时需要的剧本格式信息
	 * @param scripteleInfoList		剧本元素
	 * @param separatorInfoList		元素分隔符
	 * @param scenarioFormat
	 * @return 格式Map
	 * 当key为mainTitleRegex时，value表示主标题的正则表达式
	 * seriesViewNoRegex  --  只带有集场的标题的正则表达式
	 * seriesViewNoFormatList  -- 只带有集场的元素列表
	 * noFigureRegex  --  不带有人物的标题的正则表达式
	 * noFigureFormatList  --  不带有人物的标题的元素列表
	 * figureLineNum --  人物元素占据的行数
	 * minCount  --  标题中最小元素数量
	 * lineSeperatorCount -- 换行符的数量
	 * separateSceripRegex  --  分割标题的正则表达式
	 */
	public Map<String, Object> genFormatInfo(List<ScripteleInfoModel> scripteleInfoList, List<SeparatorInfoModel> separatorInfoList, List<String> scenarioFormatList) {
		Map<String, Object> formmatMap = new HashMap<String, Object>();
		
		//标准标题
		List<String> mainTitleRegexList = new ArrayList<String>(scenarioFormatList.size());
		
		//只带有集场号的标题
		List<String> seriesViewNoRegexList = new ArrayList<String>(scenarioFormatList.size());
		List<String> seriesViewNoFormatList = new ArrayList<String>();
		
		//只不带人物及其之前符号的标题
		List<String> noFigureRegexList = new ArrayList<String>();
		List<String> noFigureFormatList = new ArrayList<String>();
		
		String[] separateRegexArray = new String[scenarioFormatList.size() + 2];
		
		int minCount = 0;			//标题中元素的最少数量
		int lineSeperatorCount = 0;	//换行符的数量

		String mainTitleRegex = "";		//拼接标准标题的正则表达式
		String seriesViewNoRegex = "";	//只识别集场号的正则表达式
		
		String noFigureRegex = "";	//识别除了人物及其前面的符号外的正则表达式
		String separateSceripRegex = "(";		//分割元素的正则表达式
		int lineNum = 0;	//人物元素和其之前的元素之间换行符的数量
		
		String preFormatAtomic = "";	//上一个自定义格式元素
		boolean hasFigure = false;
		boolean hasViewLocation = false;
		
		int separatorNum = 0;	//两个元素之间的分隔符数目
		List<String> separatorList = new ArrayList<String>();	//两个元素之间分隔符列表
		
		
		for (int i = 0, len = scenarioFormatList.size(); i < len; i++) {
			String allFormat = scenarioFormatList.get(i);
			
			if (noFigureFormatList.size()==0 && noFigureRegexList.size()==0 && allFormat.equals(Constants.SCRIPTELE_FIGURE)) {
				for (String beforeFigureSepa : separatorList) {
					if (beforeFigureSepa.equals(Constants.SEPARATOR_LINE)) {
						lineNum++;
					}
				}
				
				for (int j = 0; j < i-separatorNum; j++) {
					noFigureFormatList.add(scenarioFormatList.get(j));
				}
				for (int j = 0; j < i-separatorNum; j++) {
					noFigureRegexList.add(mainTitleRegexList.get(j));
				}
			}
			
			for (ScripteleInfoModel scriptele : scripteleInfoList) {
				String eleId = scriptele.getEleId();
				String eleRegex = scriptele.getRegex();
				if (allFormat.equals(eleId)) {
					mainTitleRegexList.add(i, eleRegex);
					
					//忽略"人物"元素的个数
					if (!allFormat.equals(Constants.SCRIPTELE_FIGURE)) {
						minCount ++;
					}
					
					separatorNum = 0;
					separatorList.clear();
				}
			}
			
			for (SeparatorInfoModel separator : separatorInfoList) {
				String sepaId = separator.getSepaId();
				String sepRegex = separator.getRegex();
				
				if (allFormat.equals(sepaId)) {
					if (StringUtils.isBlank(preFormatAtomic) || (!StringUtils.isBlank(preFormatAtomic) && !preFormatAtomic.equals(Constants.SCRIPTELE_FIGURE))) {
						mainTitleRegexList.add(i, sepRegex);
					} else {
						mainTitleRegexList.add(i, "");
					}
					
					//只带有集场号和后面第一个符号的正则表达式
					if (preFormatAtomic.equals(Constants.SCRIPTELE_SERIESNO) || preFormatAtomic.equals(Constants.SCRIPTELE_VIEWNO)) {
						seriesViewNoRegexList.clear();
						for (String notAllRegex : mainTitleRegexList) {
							seriesViewNoRegexList.add(notAllRegex);
						}
						seriesViewNoFormatList = scenarioFormatList.subList(0, i);
					}
					separateRegexArray[i] = sepRegex;
					
					separatorNum++;
					separatorList.add(sepaId);
				}
			}

			if (allFormat.equals(Constants.SEPARATOR_LINE)) {
				lineSeperatorCount++;
			}
			if (!hasFigure && allFormat.equals(Constants.SCRIPTELE_FIGURE)) {
				hasFigure = true;
			}
			if (!hasViewLocation && allFormat.equals(Constants.SCRIPTELE_VIEWLOCATION)) {
				hasViewLocation = true;
			}
			
			preFormatAtomic = allFormat;
		}
		
		if (hasFigure) {
			separateRegexArray[scenarioFormatList.size()] = Constants.REGEX_TITLE_FIGURE_SPLIT_CHAR;
		}
		if (hasViewLocation) {
			separateRegexArray[scenarioFormatList.size() + 1] = Constants.REGEX_TITLE_VIEWLOCATION_SPLIT_CHAR;
		}
		
		
		//主标题的正则表达式
		mainTitleRegex += "^";
		for (int i = 0, len = mainTitleRegexList.size(); i < len; i++) {
			String singleTitleRegex = mainTitleRegexList.get(i);
			if (!StringUtils.isBlank(singleTitleRegex)) {
				mainTitleRegex += singleTitleRegex;
			}
		}
		mainTitleRegex += "$";
		
		
		//只带有集场号的正则表达式
		seriesViewNoRegex += "^";
		for (int i = 0, len = seriesViewNoRegexList.size(); i < len; i++) {
			String mySeriesViewNoRegex = seriesViewNoRegexList.get(i);
			if (!StringUtils.isBlank(mySeriesViewNoRegex)) {
				seriesViewNoRegex += mySeriesViewNoRegex;
			}
		}
		//.*正则匹配不了\r\n字符
		for (int i = 0; i < lineSeperatorCount; i++) {
			seriesViewNoRegex += ".*(\r\n)*";
		}
		seriesViewNoRegex += ".*$";

		
		//不带有人物的正则表达式
		noFigureRegex += "^";
		for (int i = 0; i < noFigureRegexList.size(); i++) {
			noFigureRegex += noFigureRegexList.get(i);
		}
		//.*正则匹配不了\r\n字符
		for (int i = 0; i < lineSeperatorCount; i++) {
			noFigureRegex += ".*(\r\n)*";
		}
		noFigureRegex += ".*$";
		
		
		//对分隔符表达式按照长度倒序排序
		List<String> separateRegexList = Arrays.asList(separateRegexArray);
		Collections.sort(separateRegexList, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				if (StringUtils.isBlank(o1) || StringUtils.isBlank(o2)) {
					return 0;
				}
				return o2.length() - o1.length();
			}
		});
		
		
		for (String singleSepRegex : separateRegexArray) {
			if (!StringUtils.isBlank(singleSepRegex)) {
				separateSceripRegex += singleSepRegex + "|";
			}
		}
		
		formmatMap.put("mainTitleRegex", mainTitleRegex);
		
		formmatMap.put("seriesViewNoRegex", seriesViewNoRegex);
		formmatMap.put("seriesViewNoFormatList", seriesViewNoFormatList);
		
		formmatMap.put("noFigureRegex", noFigureRegex);
		formmatMap.put("noFigureFormatList", noFigureFormatList);
		formmatMap.put("figureLineNum", lineNum);
		
		formmatMap.put("minCount", minCount);
		formmatMap.put("lineSeperatorCount", lineSeperatorCount);
		
		separateSceripRegex = separateSceripRegex.substring(0, separateSceripRegex.length() - 1);
		separateSceripRegex += ")";
		formmatMap.put("separateSceripRegex", separateSceripRegex);
		
		return formmatMap;
	}
	
	/**
	 * 把剧本的格式拆分成单个原子的形式
	 * 例如e1s3e2s2拆分成[e1, s3, e2, s2]
	 * 该方法要求单个原子开头必须是s或e
	 * @param scripteleInfoList	剧本元素信息
	 * @param separatorInfoList	剧本符号信息
	 * @param scenarioFormatStr	剧本格式字符串
	 * @return
	 */
	public List<String> genScenarioFormatList(String scenarioFormatStr) {
		
		List<String> scenarioFormatList = new ArrayList<String>();
		
		for (int i = 0, formatLength = scenarioFormatStr.length(); i < formatLength; i++) {
			if (scenarioFormatStr.length() == 0) {
				break;
			}
			
			int atomicLength = 0;
			
			String tmpScenarioForStr = scenarioFormatStr.substring(1);
			int sIndex = tmpScenarioForStr.indexOf("s");;
			int eIndex = tmpScenarioForStr.indexOf("e");;
			int minIndex = 0;

			if (sIndex != -1 && sIndex < eIndex) {
				minIndex = sIndex;
			}
			if (eIndex != -1 && eIndex < sIndex) {
				minIndex = eIndex;
			}
			if (sIndex == -1 && eIndex == -1) {
				minIndex = scenarioFormatStr.length() - 1;
			}
			if (sIndex == -1 && eIndex != -1) {
				minIndex = eIndex;
			}
			if (eIndex == -1 && sIndex != -1) {
				minIndex = sIndex;
			}
			
			String currAtomic = "";
			currAtomic = scenarioFormatStr.substring(0, minIndex + 1);
			
			atomicLength = currAtomic.length();
			
			scenarioFormatList.add(currAtomic);
			
			scenarioFormatStr = scenarioFormatStr.substring(atomicLength);
		}
		
		return scenarioFormatList;
	}
	
	/**
	 * 获取标题中的元素
	 * @param title	标题字符串
	 * @param scripteleInfoList	所有元素信息
	 * @param separatorInfoList	所有分隔符信息
	 * @return
	 */
	public Map<String, Object> genTitleElement(String titleStr, String scenarioFormatStr, 
			List<String> scenarioFormatList, List<String> titleElementList, 
			List<ScripteleInfoModel> scripteleInfoList, List<SeparatorInfoModel> separatorInfoList, String extralSeriesNo, boolean supportCNViewNo) {
		Map<String, Object> elemenetMap = new HashMap<String, Object>();
		
		String seriesNo = "", 
				viewNo = "", 
				viewLocation = "", 
				atmosphere = "", 
				site = "", 
				season = "", 
				figure = "";
		boolean validTitle = true;	//标题是否有效
		boolean isStandardTitle = true;
		
		//特殊处理不完全符合自定义格式但是符合集场格式的标题
		Map<String, Object> originalFormInfoMap = this.genFormatInfo(scripteleInfoList, separatorInfoList, scenarioFormatList);
		String seriesViewNoRegex = (String) originalFormInfoMap.get("seriesViewNoRegex");	//只包含集场号的正则表达式
		String mainTitleRegex = (String) originalFormInfoMap.get("mainTitleRegex");	//匹配标题的正则表达式
		List<String> seriesViewNoFormatList = (List<String>) originalFormInfoMap.get("seriesViewNoFormatList");	//只包含集场号的剧本格式信息
		String oriSeparateSceripRegex = (String) originalFormInfoMap.get("separateSceripRegex");		//用户自定义标题格式中分隔符信息
		String noFigureRegex = (String) originalFormInfoMap.get("noFigureRegex");	//不带有人物的正则表达式
		List<String> noFigureFormatList = (List<String>) originalFormInfoMap.get("noFigureFormatList");	//不带有人物的剧本格式信息
		int figureLineNum = (Integer) originalFormInfoMap.get("figureLineNum");	//人物元素和其之前的元素之间换行符的数量
		int lineSeperatorCount = (Integer) originalFormInfoMap.get("lineSeperatorCount");	//标题中换行符的数量
		
		boolean standardTitleFlag = RegexUtils.regexFind(mainTitleRegex, titleStr);	 //本行是否为标配的标题
		boolean isTitle = RegexUtils.regexFind(seriesViewNoRegex, titleStr);	//本行是否匹配只含有集场号的标题
		boolean isNoFigure = RegexUtils.regexFind(noFigureRegex, titleStr);	//本行是否匹配只不含有人物及其前面符号的标题格式
		
		//当是中文场次的情况下这三个值的特殊情况
		if (supportCNViewNo) {
			//电影的场次第一个元素肯定为“场”，此处识别第一个元素是否为“三十一”这种中文数字
			String cNViewNo = com.xiaotu.makeplays.utils.StringUtils.genCNViewNo(titleStr.split(oriSeparateSceripRegex)[0]);
			if (!StringUtils.isBlank(cNViewNo)) {
				String cNTitleRegex = mainTitleRegex.replace("^\\d", Constants.REGEX_CN_NUMBER_START);
				if (RegexUtils.regexFind(cNTitleRegex, titleStr)) {
					standardTitleFlag = true;
				} else {
					isTitle = true;
				}
			}
			
			String cnNoFigureRegex = noFigureRegex.replace("^\\d", Constants.REGEX_CN_NUMBER_START);
			if (RegexUtils.regexFind(cnNoFigureRegex, titleStr)) {
				isNoFigure = true;
			}
		}
		
		if (!standardTitleFlag && isTitle) {
			if (noFigureFormatList.size() != 0 && isNoFigure) {
				scenarioFormatList = noFigureFormatList;
				String[] titleLineArray = titleStr.split(this.lineSeprator);
				titleStr = "";
				int size = titleLineArray.length;
				if (lineSeperatorCount == titleLineArray.length - 1) {
					size = titleLineArray.length - figureLineNum;
				}
				
				for (int i = 0; i < size; i++) {
					titleStr += titleLineArray[i] + "，";
				}
				
				String[] titleArray = titleStr.split(oriSeparateSceripRegex);
				titleElementList.clear();
				for (String str : titleArray) {
					if (StringUtils.isBlank(str)) {
						continue;
					}
					titleElementList.add(str);
				}
			} else {
				scenarioFormatList = seriesViewNoFormatList;
			}
			isStandardTitle = false;
		}
		
		String beforeFigureSeparatorId = "";	//人物元素之前的符号ID
		String beforeFigureSeparatorName = "";	//人物元素之前的符号名称
		String preFormatAtomic = "";	//上一个自定义格式元素

		for (int i = 0, len = scenarioFormatList.size(); i < len; i++) {
			String allFormat = scenarioFormatList.get(i);
			//获取人物元素之前的符号
			if (!StringUtils.isBlank(preFormatAtomic) && allFormat.equals(Constants.SCRIPTELE_FIGURE)) {
				beforeFigureSeparatorId = preFormatAtomic;
				break;
			}
			preFormatAtomic = allFormat;
		}
		
		if (!StringUtils.isBlank(beforeFigureSeparatorId)) {
			for (SeparatorInfoModel separator : separatorInfoList) {
				if (separator.getSepaId().equals(beforeFigureSeparatorId)) {
					beforeFigureSeparatorName = separator.getSepaName();
					break;	
				}
			}
			
			//换行做特殊处理
			if (beforeFigureSeparatorName.equals("/r/n")) {
				beforeFigureSeparatorName = "\r\n";
			}
			int titleFigureIndex = titleStr.lastIndexOf(beforeFigureSeparatorName);
			String exceptFigureTitle = titleStr.substring(0, titleFigureIndex);
			String figureTitle = titleStr.substring(titleFigureIndex, titleStr.length());
			
			int formatFigureIndex = scenarioFormatStr.lastIndexOf(beforeFigureSeparatorId);
			String exceptFigureFormat = scenarioFormatStr.substring(0, formatFigureIndex);
			
			
			scenarioFormatList = this.genScenarioFormatList(exceptFigureFormat);
			Map<String, Object> formInfoMap = this.genFormatInfo(scripteleInfoList, separatorInfoList, scenarioFormatList);
			String separateSceripRegex = (String) formInfoMap.get("separateSceripRegex");
			
			String[] titleArray = exceptFigureTitle.split(separateSceripRegex);
			titleElementList.clear();
			for (String str : titleArray) {
				if (StringUtils.isBlank(str)) {
					continue;
				}
				titleElementList.add(str);
			}
			
			//解析figureTitle，把人物值取到
			figureTitle = figureTitle.replace(beforeFigureSeparatorName, "");
			figureTitle = figureTitle.replace("\r\n", "");
			String[] figureArray = figureTitle.split(Constants.REGEX_TITLE_FIGURE_SPLIT_CHAR + "|" + oriSeparateSceripRegex);
			for (String myFigure : figureArray) {
				figure += myFigure + ",";
			}
		}
		
		//比对exceptFigureTitle和exceptFigureFormatList，前后进行比对，把各个元素的值取到（包括场景）
		int extralElementNum = 0;	//实际的标题比自定义格式的标题多出来的元素数量
		int firstViewLocationIndex = -1;	//第一个拍摄地点下标
		String[] scenarioFormatArray = new String[scenarioFormatList.size()];
		
		//去掉标题中的符号信息格式列表
		for (int i = 0, len = scenarioFormatList.size(); i < len; i++) {
			String allFormat = scenarioFormatList.get(i);
			
			for (ScripteleInfoModel scriptele : scripteleInfoList) {
				String eleId = scriptele.getEleId();
				if (allFormat.equals(eleId)) {
					scenarioFormatArray[i] = eleId;
				}
			}
		}
		
		List<String> excepSepFormatList = new ArrayList<String>();
		for (int i = 0, len = scenarioFormatArray.length; i < len; i++) {
			if (StringUtils.isBlank(scenarioFormatArray[i])) {
				continue;
			}
			excepSepFormatList.add(scenarioFormatArray[i]);
		}
		
		if (excepSepFormatList.size() < titleElementList.size()) {
			extralElementNum = titleElementList.size() - excepSepFormatList.size();
		}
		
		for (int i = 0, len = excepSepFormatList.size(); i < len; i++) {
			if (StringUtils.isBlank(excepSepFormatList.get(i))) {
				continue;
			}
			if (firstViewLocationIndex == -1 && excepSepFormatList.get(i).equals(Constants.SCRIPTELE_VIEWLOCATION)) {
				firstViewLocationIndex = i;
				break;
			}
		}
		
		if (excepSepFormatList.size() > titleElementList.size()) {
			String errorMsg = "标题《" + titleStr + "》元素不足，请检查是否缺少标点符号";
			if (!this.titleErrorMsg.contains(errorMsg)) {
				this.titleErrorMsg.add(errorMsg);
				errorTitleList.add(titleStr);
			}
			validTitle = false;
		}
		
		//只有标题符合规则的时候再分析
		if(validTitle) {
			for (int s = 0; s < scripteleInfoList.size(); s++) {
				ScripteleInfoModel scriptele = scripteleInfoList.get(s);
				String eleId = scriptele.getEleId();
				
				//获取自定义格式中指定剧本元素符串的index列表
				List<Integer> indexList = new ArrayList<Integer>();
				for (int i = 0, len = excepSepFormatList.size(); i < len; i++) {
					if (StringUtils.isBlank(excepSepFormatList.get(i))) {
						continue;
					}
					
					if (excepSepFormatList.get(i).equals(Constants.SCRIPTELE_VIEWLOCATION)) {
						continue;
					}
					
					if (!excepSepFormatList.get(i).equals(Constants.SCRIPTELE_VIEWLOCATION) && excepSepFormatList.get(i).equals(eleId)) {
						if (i > firstViewLocationIndex && firstViewLocationIndex != -1) {
							i += extralElementNum;
						}
						indexList.add(i);
					}
				}
				
				if (eleId.equals(Constants.SCRIPTELE_VIEWLOCATION) && firstViewLocationIndex != -1) {
					for (int i = firstViewLocationIndex; i <= firstViewLocationIndex + extralElementNum; i++) {
						indexList.add(i);
					}
				}
				
				String singleValue = "";
				String multiValue = "";
				for (Integer index : indexList) {
					singleValue = titleElementList.get(index);
					multiValue += singleValue + ",";
				}
				if (multiValue.length() > 0) {
					multiValue = multiValue.substring(0, multiValue.length() - 1);
				}
				
				if (indexList.size() > 0) {
					if (eleId.equals(Constants.SCRIPTELE_SERIESNO)) {
						seriesNo = singleValue;
					}
					if (eleId.equals(Constants.SCRIPTELE_VIEWNO)) {
						viewNo = singleValue;
					}
					if (eleId.equals(Constants.SCRIPTELE_VIEWLOCATION)) {
						viewLocation = multiValue;
					}
					if (eleId.equals(Constants.SCRIPTELE_ATMOSPHERE)) {
						atmosphere = singleValue;
					}
					if (eleId.equals(Constants.SCRIPTELE_SITE)) {
						site = singleValue;
					}
					if (eleId.equals(Constants.SCRIPTELE_SEASON)) {
						season = singleValue;
					}
					if (eleId.equals(Constants.SCRIPTELE_FIGURE)) {
						figure = multiValue;
					}
				}
			}
		}
		
		elemenetMap.put("validTitle", validTitle);
		elemenetMap.put("titleErrorMsg", titleErrorMsg);
		elemenetMap.put("seriesNo", seriesNo);
		if (!StringUtils.isBlank(viewNo)) {
			if (supportCNViewNo) {
				viewNo = com.xiaotu.makeplays.utils.StringUtils.genCNViewNo(viewNo.replace("\t", ""));
			} else {
				viewNo = viewNo.replace("\t", "");
			}
			elemenetMap.put("viewNo", viewNo);
		}
		if (!StringUtils.isBlank(viewLocation)) {
			elemenetMap.put("viewLocation", viewLocation);
		}
		
		elemenetMap.put("atmosphere", atmosphere);
		elemenetMap.put("site", site);
		elemenetMap.put("season", season);
		elemenetMap.put("isStandardTitle", isStandardTitle);
		elemenetMap.put("figureLineNum", figureLineNum);
		elemenetMap.put("isTitle", isTitle);
		elemenetMap.put("isNoFigure", isNoFigure);
		
		if (!StringUtils.isBlank(figure)) {
			elemenetMap.put("figure", figure.substring(0, figure.length() - 1));
		}
		
//		if (!StringUtils.isBlank(viewNo) && !this.checkViewNoValid(viewNo)) {
//			isTitle = false;
//		}
		
//		if (saveNotFullMatchData && !standardTitleFlag && isTitle) {
//			Map<String, String> notFullMatchData = new HashMap<String, String>();
//			String mySeriesNo = "";
//			if (StringUtils.isBlank(seriesNo)) {
//				mySeriesNo = extralSeriesNo;
//			} else {
//				mySeriesNo = seriesNo;
//			}
//			notFullMatchData.put("viewNo", viewNo);
//			notFullMatchData.put("seriesNo", seriesNo);
//			
//			boolean contained = false;
//			for (Map<String, String> map : notFullMatchingInfo) {
//				String mapSeriesNo = map.get("seriesNo");
//				String mapViewNo = map.get("viewNo");
//				
//				if (com.xiaotu.makeplays.utils.StringUtils.equalStr(mapSeriesNo, seriesNo) 
//						&& com.xiaotu.makeplays.utils.StringUtils.equalStr(mapViewNo, viewNo)) {
//					contained = true;
//					break;
//				}
//			}
//			if (!contained) {
//				notFullMatchingInfo.add(notFullMatchData);
//			}
//			
//			if (!notFullMatchingTitleList.contains("标题《" + titleStr + "》与自定义标题不完全符合，请检查。")) {
//				notFullMatchingTitleList.add("标题《" + titleStr + "》与自定义标题不完全符合，请检查。");
//			}
//		}
		
		return elemenetMap;
	}
	
	/**
	 * 检查剧本的格式是否合法,
	 * 主要检查剧本中是否存在符合剧本模板中标题格式的文本，如果存在，就合法
	 * 通过抛出异常来体现检查的成功与否
	 * @param contentList 剧本内容
	 * @return Map<String,Object>
	 */
	private void checkSceTitleIllegal(List<String> contentList, Map<String, Object> formatInfo, 
			List<String> scenarioFormatList, List<ScripteleInfoModel> scripteleInfoList, 
			List<SeparatorInfoModel> separatorInfoList, boolean hasSeriesNoTag, 
			String extralSeriesNo, boolean groupSeriesNoFlag, boolean supportCNViewNo) {
		
		boolean hasTitle = false; // 记录第一个（第n集的行）
		
		String mainTitleRegex = (String) formatInfo.get("mainTitleRegex");	//匹配标题的正则表达式
		String separateSceripRegex = (String) formatInfo.get("separateSceripRegex");	//分割标题中元素的正则表达式
		int lineSeperatorCount = (Integer) formatInfo.get("lineSeperatorCount");	//自定义格式中换行符的个数
		String seriesViewNoRegex = (String) formatInfo.get("seriesViewNoRegex");	//只包含集场号的正则表达式
		
		String scenarioFormatStr = "";
		for (String format : scenarioFormatList) {
			scenarioFormatStr += format;
		}
		
		if (contentList != null && contentList.size() > 0) {
			/*
			 * 考虑到自定义格式中有换行符,利用这两个变量对行进行打包分析
			 * 例如有一个换行符，则对剧本的分析时按照每两行一个单位进行解析
			 */
			int groupLineCount = 0;	//表示当前的数据是第几行
			String groupLineValue = "";	//存储包括当前行，往上lineSeperatorCount+1行的数据

			for (String myLineContent : contentList) {
				myLineContent = myLineContent.trim();
				
				if (StringUtils.isBlank(myLineContent) || myLineContent.equals("")) { // 空行跳过
					continue;
				}
				
				/*
				 * 跳过第XX集的行
				 */
				if (RegexUtils.regexFind(Constants.REGEX_SERIES, myLineContent) && myLineContent.length() < 15) {
					if (groupSeriesNoFlag) {
						extralSeriesNo = this.genSeriesNo(myLineContent) + "";
					}
					continue;
				}
				
				if (myLineContent.substring(0, 1).equals("﻿?")) {
					myLineContent = myLineContent.substring(1, myLineContent.length());
				}

				groupLineCount++;
				groupLineValue += myLineContent + this.lineSeprator;
				if (groupLineCount < lineSeperatorCount + 1) {
					continue;
				}
				
				if (groupLineCount != lineSeperatorCount + 1) {
					String[] groupLineArr = groupLineValue.split(this.lineSeprator);
					groupLineValue = "";
					for(int i = 0, len = groupLineArr.length; i < len; i++) {
						if (i != 0) {
							groupLineValue += groupLineArr[i] + this.lineSeprator;
						}
					}
				}
				
//				String groupLineContent = groupLineValue.trim();
//				
//				//此处""不是空格，而是一个特殊字符，该字符会在自动编号的剧本中出现
//				if (groupLineContent.substring(0, 1).equals("﻿")) {
//					groupLineContent = groupLineContent.substring(1, groupLineContent.length());
//				}
//				
//				//兼容自动编号中的符号自动替换问题
//				String dealedGroupLineContent = groupLineContent.replaceAll("-", ".");
//				dealedGroupLineContent = dealedGroupLineContent.replaceAll("—", ".");
//				
//				//英文替换为中文
//				dealedGroupLineContent = com.xiaotu.makeplays.utils.StringUtils.EnToCHSeparator(dealedGroupLineContent);
				
				String dealedGroupLineContent = this.dealSpecialChar(groupLineValue);
				
				// 本行是否为标题
				boolean standardTitleFlag = RegexUtils.regexFind(mainTitleRegex, dealedGroupLineContent);
				boolean isTitle = RegexUtils.regexFind(seriesViewNoRegex, dealedGroupLineContent);	//本行是否匹配只含有集场号的标题
				
				if (supportCNViewNo) {
					String cNViewNo = "";
					//电影的场次第一个元素肯定为“场”，此处识别第一个元素是否为“三十一”这种中文数字
					String[] titleArray = dealedGroupLineContent.split(separateSceripRegex);
					for (int i = 0; i < titleArray.length; i++) {
						if (!StringUtils.isBlank(titleArray[i])) {
							if (!StringUtils.isBlank(com.xiaotu.makeplays.utils.StringUtils.genCNViewNo(titleArray[i]))) {
								cNViewNo = com.xiaotu.makeplays.utils.StringUtils.genCNViewNo(titleArray[i]);
							}
							break;
						}
					}
					
					if (!StringUtils.isBlank(cNViewNo)) {
						String cNTitleRegex = mainTitleRegex.replace("\\d", Constants.REGEX_CN_NUMBER_START);
						if (RegexUtils.regexFind(cNTitleRegex, dealedGroupLineContent)) {
							standardTitleFlag = true;
						}
						
						String cNSeriesViewNoRegex = seriesViewNoRegex.replace("\\d", Constants.REGEX_CN_NUMBER_START);
						if (RegexUtils.regexFind(cNSeriesViewNoRegex, dealedGroupLineContent)) {
							isTitle = true;
						}
					}
				}
				
				if ((standardTitleFlag || isTitle) && !this.inValidViewNoList.contains(dealedGroupLineContent)) {
					//只有存在标准匹配标题的时候才认定剧本格式配置正确
					if (standardTitleFlag) {
						hasTitle = true;
					}
					
					if (groupSeriesNoFlag && StringUtils.isBlank(extralSeriesNo)) {
						throw new IllegalArgumentException("标题《" + groupLineValue + "》前未找到‘第XX集’标识，请检查");
					}
					
					String[] titleArray = dealedGroupLineContent.split(separateSceripRegex);

					List<String> titleElementList = new ArrayList<String>();
					for (String str : titleArray) {
						if (StringUtils.isBlank(str)) {
							continue;
						}
						titleElementList.add(str);
					}

					Map<String, Object> elementMap = this.genTitleElement(dealedGroupLineContent, scenarioFormatStr, scenarioFormatList, titleElementList, scripteleInfoList, separatorInfoList, null, supportCNViewNo);
					boolean eleValidTitle = (Boolean) elementMap.get("validTitle");
					
					if (eleValidTitle) {
						// 集数-场景编号
						if (elementMap.get("seriesNo") != null && elementMap.get("seriesNo") != "") {
							try {
								Integer.parseInt((String) elementMap.get("seriesNo"));
							} catch (NumberFormatException e) {
								this.titleErrorMsg.add("标题《" + groupLineValue + "》中的集次不是数字，请修改后上传");
								errorTitleList.add(dealedGroupLineContent);
							}
						}

						String seriesNo = "";	// 集次
						if (hasSeriesNoTag) {
							seriesNo = (String) elementMap.get("seriesNo");
						} else {
							seriesNo = extralSeriesNo;
						}
						
						String viewNo = (String) elementMap.get("viewNo"); // 场次
						if (StringUtils.isBlank(viewNo)) {
							this.titleErrorMsg.add("标题《" + groupLineValue + "》没有填写场次信息，请修改后上传");
							errorTitleList.add(dealedGroupLineContent);
						}
						
						//与自定义格式不符合完全符合的标题（只符合集场号的标题）
						if (!standardTitleFlag && isTitle) {
							Map<String, String> notFullMatchData = new HashMap<String, String>();
							notFullMatchData.put("viewNo", viewNo);
							notFullMatchData.put("seriesNo", seriesNo);
							
							boolean contained = false;
							for (Map<String, String> map : notFullMatchingInfo) {
								String mapSeriesNo = map.get("seriesNo");
								String mapViewNo = map.get("viewNo");
								
								if (com.xiaotu.makeplays.utils.StringUtils.equalStr(mapSeriesNo, seriesNo) 
										&& com.xiaotu.makeplays.utils.StringUtils.equalStr(mapViewNo, viewNo)) {
									contained = true;
									break;
								}
							}
							if (!contained) {
								notFullMatchingInfo.add(notFullMatchData);
							}
							
							if (!notFullMatchingTitleList.contains("标题《" + groupLineValue + "》与自定义标题不完全符合，请检查。")) {
								notFullMatchingTitleList.add("标题《" + groupLineValue + "》与自定义标题不完全符合，请检查。");
							}
						}
						
						
						if (elementMap.get("site") != null && elementMap.get("site") != "") {
							String site = (String) elementMap.get("site");
							if (site.length() > 10) {
								this.titleErrorMsg.add("标题《" + groupLineValue + "》中内外景字段过长，请检查是否缺少标点符号");
								errorTitleList.add(dealedGroupLineContent);
							}
						}
					}
				}
			}
		}
		if (!hasTitle) {
			throw new IllegalArgumentException("剧本中没有指定格式的标题，请修改后重新上传");
		}
	}
	
	/**
	 * 校验场景号是否符合规范
	 * 无效的条件有：
	 * 1、场次中汉字超过3个，比如：10排的战士们
	 * 2、带有汉字的场次没有对应的不带汉字的场次，比如：有场次“10排中”却没有场次“10”，则判定“10排中”场次所在的标题不是标题
	 * 3、数字值不能大于400
	 */
	private void checkViewNoValid(List<String> contentList, Map<String, Object> formatInfo, List<String> scenarioFormatList,
			List<ScripteleInfoModel> scripteleInfoList, List<SeparatorInfoModel> separatorInfoList, 
			boolean hasSeriesNoTag, String extralSeriesNo, boolean groupSeriesNoFlag, boolean supportCNViewNo) {
		
		String separateSceripRegex = (String) formatInfo.get("separateSceripRegex");	//分割标题中元素的正则表达式
		int lineSeperatorCount = (Integer) formatInfo.get("lineSeperatorCount");	//自定义格式中换行符的个数
		String seriesViewNoRegex = (String) formatInfo.get("seriesViewNoRegex");	//只包含集场号的正则表达式
		
		String scenarioFormatStr = "";
		for (String format : scenarioFormatList) {
			scenarioFormatStr += format;
		}
		
		Map<String, List<String>> seriesViewNoContentMap = new HashMap<String, List<String>>();	//集场对应的标题Map，key为集“集-场”，value为该场的标题
		List<String> containCHSeriesViewNo = new ArrayList<String>();	//场次中含有中文的集场
		
		if (contentList != null && contentList.size() > 0) {
			/*
			 * 考虑到自定义格式中有换行符,利用这两个变量对行进行打包分析
			 * 例如有一个换行符，则对剧本的分析时按照每两行一个单位进行解析
			 */
			int groupLineCount = 0;	//表示当前的数据是第几行
			String groupLineValue = "";	//存储包括当前行，往上lineSeperatorCount+1行的数据

			for (String myLineContent : contentList) {
				myLineContent = myLineContent.trim();
				
				if (StringUtils.isBlank(myLineContent) || myLineContent.equals("﻿")) { // 空行跳过
					continue;
				}
				
				/*
				 * 跳过第XX集的行
				 */
				if (RegexUtils.regexFind(Constants.REGEX_SERIES, myLineContent) && myLineContent.length() < 15) {
					if (groupSeriesNoFlag) {
						extralSeriesNo = this.genSeriesNo(myLineContent) + "";
					}
					continue;
				}
				
				if (myLineContent.substring(0, 1).equals("﻿?")) {
					myLineContent = myLineContent.substring(1, myLineContent.length());
				}

				groupLineCount++;
				groupLineValue += myLineContent + this.lineSeprator;
				if (groupLineCount < lineSeperatorCount + 1) {
					continue;
				}
				
				if (groupLineCount != lineSeperatorCount + 1) {
					String[] groupLineArr = groupLineValue.split(this.lineSeprator);
					groupLineValue = "";
					for(int i = 0, len = groupLineArr.length; i < len; i++) {
						if (i != 0) {
							groupLineValue += groupLineArr[i] + this.lineSeprator;
						}
					}
				}
				
				//处理特殊字符
				String dealedGroupLineContent = this.dealSpecialChar(groupLineValue);
				
				// 本行是否为标题
				boolean isTitle = RegexUtils.regexFind(seriesViewNoRegex, dealedGroupLineContent);	//本行是否匹配只含有集场号的标题
				if (supportCNViewNo) {
					String cNViewNo = "";
					//电影的场次第一个元素肯定为“场”，此处识别第一个元素是否为“三十一”这种中文数字
					String[] titleArray = dealedGroupLineContent.split(separateSceripRegex);
					for (int i = 0; i < titleArray.length; i++) {
						if (!StringUtils.isBlank(titleArray[i])) {
							if (!StringUtils.isBlank(com.xiaotu.makeplays.utils.StringUtils.genCNViewNo(titleArray[i]))) {
								cNViewNo = com.xiaotu.makeplays.utils.StringUtils.genCNViewNo(titleArray[i]);
							}
							break;
						}
					}
					
					if (!StringUtils.isBlank(cNViewNo)) {
						String cNSeriesViewNoRegex = seriesViewNoRegex.replace("\\d", Constants.REGEX_CN_NUMBER_START);
						if (RegexUtils.regexFind(cNSeriesViewNoRegex, dealedGroupLineContent)) {
							isTitle = true;
						}
					}
				}
				
				if (isTitle) {
					if (groupSeriesNoFlag && StringUtils.isBlank(extralSeriesNo)) {
						throw new IllegalArgumentException("标题《" + groupLineValue + "》前未找到‘第XX集’标识，请检查");
					}
					
					String[] titleArray = dealedGroupLineContent.split(separateSceripRegex);

					List<String> titleElementList = new ArrayList<String>();
					for (String str : titleArray) {
						if (StringUtils.isBlank(str)) {
							continue;
						}
						titleElementList.add(str);
					}

					Map<String, Object> elementMap = this.genTitleElement(dealedGroupLineContent, scenarioFormatStr, 
							scenarioFormatList, titleElementList, scripteleInfoList, separatorInfoList, null, supportCNViewNo);
					boolean eleValidTitle = (Boolean) elementMap.get("validTitle");
					
					if (eleValidTitle) {
						
						int seriesNo = 0;	// 集次
						if (hasSeriesNoTag) {
							seriesNo = Integer.parseInt((String) elementMap.get("seriesNo"));
						} else {
							seriesNo = Integer.parseInt(extralSeriesNo);
						}
						
						String viewNo = (String) elementMap.get("viewNo"); // 场次
						String seriesViewNo = seriesNo + "-" + viewNo;
						
						//校验场次中是否包含三个以上的汉字
						//校验场次中的数字值是否大于400
						boolean isTooLongViewNo = false;
						boolean isTooBigViewNo = false;
						if (!StringUtils.isBlank(viewNo)) {
							String[] viewNoArray = viewNo.trim().split("");
							int count = 0;
							String zhTitle = "";
							for (int i = 0; i < viewNoArray.length; i++) {
								String viewNoEle = viewNoArray[i];
								if (RegexUtils.regexFind(Constants.REGEX_CHINESE_WORD, viewNoEle)) {
									count ++;
									zhTitle += viewNoEle;
								}
							}
							if (count > 3) {
								isTooLongViewNo = true;
							}
							
							int numViewNo = MovieScenarioService.genViewNoNumber(viewNo);
							if (numViewNo > 400) {
								isTooBigViewNo = true;
							}
							
							if (count > 0 && !isTooBigViewNo && !isTooLongViewNo && !ScenarioService.lineSeqWordList.contains(zhTitle)) {
								containCHSeriesViewNo.add(seriesViewNo);
							}
						}
						
						//场次过长
						if ((isTooBigViewNo || isTooLongViewNo) && !this.inValidViewNoList.contains(dealedGroupLineContent)) {
							this.inValidViewNoList.add(dealedGroupLineContent);
						} else {
							if (seriesViewNoContentMap.containsKey(seriesViewNo)) {
								List<String> titleList = seriesViewNoContentMap.get(seriesViewNo);
								titleList.add(dealedGroupLineContent);
								
								seriesViewNoContentMap.put(seriesViewNo, titleList);
							} else {
								List<String> titleList = new ArrayList<String>();
								titleList.add(dealedGroupLineContent);
								seriesViewNoContentMap.put(seriesViewNo, titleList);
							}
						}
					}
				}
			}
			
			//校验所有场次含有中文的场次是否存在对应的不含有中文的场次
			//比如，如果存在“1-317部队”这样的场次，那就检查整个剧本中是否存在“1-317”场，如果没有，则判定该场所在的标题为内容
			for (String seriesViewNo : containCHSeriesViewNo) {
				String removeCHSeriesViewNo = seriesViewNo.replaceAll(Constants.REGEX_CHINESE_WORD, "");
				if (!seriesViewNoContentMap.containsKey(removeCHSeriesViewNo)) {
					List<String> titleList = seriesViewNoContentMap.get(seriesViewNo);
					for (String inValidViewNoTitle : titleList) {
						this.inValidViewNoList.add(inValidViewNoTitle);
					}
				}
			}
		}
	}
	
	/**
	 * 处理字符串中的特殊字符
	 * @param str
	 * @return
	 */
	private String dealSpecialChar(String str) {
		str = str.trim();
		
		//此处""不是空格，而是一个特殊字符，该字符会在自动编号的剧本中出现
		if (str.substring(0, 1).equals("﻿")) {
			str = str.substring(1, str.length());
		}
		
		//兼容自动编号中的符号自动替换问题
		String dealedGroupLineContent = str.replaceAll("-", ".");
		dealedGroupLineContent = dealedGroupLineContent.replaceAll("—", ".");
		
		//全角转半角
		dealedGroupLineContent = com.xiaotu.makeplays.utils.StringUtils.ToDBC(dealedGroupLineContent);
		//英文替换为中文
		dealedGroupLineContent = com.xiaotu.makeplays.utils.StringUtils.EnToCHSeparator(dealedGroupLineContent);
		
		return dealedGroupLineContent;
	}
	
	/**
	 * 获取“第XX集”中的集次
	 * @param lineContent
	 * @return
	 */
	private int genSeriesNo(String lineContent) {
		int resultInt = 0;
		String[] array = lineContent.split("(第|集)");
		for (String str : array) {
			if (!StringUtils.isBlank(str) && str.charAt(0) != (char)65279 && !str.equals("?")) {
				try {
					resultInt = Integer.parseInt(str);
				} catch (NumberFormatException e) {
					resultInt = com.xiaotu.makeplays.utils.StringUtils.toNumberLower(str);
				}
				if (resultInt != 0) {
					break;
				}
			}
		}
		
		if (resultInt == 0) {
			this.titleErrorMsg.add("《" + lineContent + "》中集次信息不符合规则，请检查。");
			errorTitleList.add(lineContent);
		}
		
		return resultInt;
	}
	
	/**
	 * 校验剧本中是否存在相同的场次
	 * @param contentList 剧本内容列表
	 */
	private void checkSceHasSameView(List<String> contentList, Map<String, Object> formatInfo, 
			List<String> scenarioFormatList, List<ScripteleInfoModel> scripteleInfoList, 
			List<SeparatorInfoModel> separatorInfoList, 
			boolean hasSeriesNoTag, String extralSeriesNo,
			boolean groupSeriesNoFlag, boolean supportCNViewNo) {
		
		String mainTitleRegex = (String) formatInfo.get("mainTitleRegex");	//匹配标题的正则表达式
		String separateSceripRegex = (String) formatInfo.get("separateSceripRegex");	//分割标题中元素的正则表达式
		int lineSeperatorCount = (Integer) formatInfo.get("lineSeperatorCount");	//自定义格式中换行符的个数
		String seriesViewNoRegex = (String) formatInfo.get("seriesViewNoRegex");	//只包含集场号的正则表达式
		
		String scenarioFormatStr = "";
		for (String format : scenarioFormatList) {
			scenarioFormatStr += format;
		}
		
		/*
		 * 考虑到自定义格式中有换行符,利用这两个变量对行进行打包分析
		 * 例如有一个换行符，则对剧本的分析时按照每两行一个单位进行解析
		 */
		int groupLineCount = 0;	//表示当前的数据是第几行
		String groupLineValue = "";	//存储包括当前行，往上lineSeperatorCount+1行的数据
		
		/*
		 * 利用List判断是否有重复的“集-场”信息
		 */
		List<String> seriesViewNoList = new ArrayList<String>();
		
		// 逐行解析
		for (String myLineContent : contentList) {
			if (StringUtils.isBlank(myLineContent)) {
				continue;
			}
			// 跳过第n集的行
			if (RegexUtils.regexFind(Constants.REGEX_SERIES, myLineContent.trim()) && myLineContent.trim().length() < 15) {
				if (groupSeriesNoFlag) {
					extralSeriesNo = this.genSeriesNo(myLineContent) + "";
				}
				continue;
			}
			
			myLineContent = myLineContent.trim();
			//此处"?"为不识别的格式文字，该字符会在自动编号的剧本中出现
			if (myLineContent.substring(0, 1).equals("﻿?")) {
				myLineContent = myLineContent.substring(1, myLineContent.length());
			}
			
			groupLineCount++;
			groupLineValue += myLineContent + this.lineSeprator;
			if (groupLineCount < lineSeperatorCount + 1) {
				continue;
			}

			if (groupLineCount != lineSeperatorCount + 1) {
				String[] groupLineArr = groupLineValue.split(this.lineSeprator);
				groupLineValue = "";
				for(int i = 0, len = groupLineArr.length; i < len; i++) {
					if (i != 0) {
						groupLineValue += groupLineArr[i] + this.lineSeprator;
					}
				}
			}
			
//			String groupLineContent = groupLineValue.trim();
//			
//			//此处""不是空格，而是一个特殊字符，该字符会在自动编号的剧本中出现
//			if (groupLineContent.substring(0, 1).equals("﻿")) {
//				groupLineContent = groupLineContent.substring(1, groupLineContent.length());
//			}
//			
//
//			//兼容自动编号中的符号自动替换问题
//			String dealedGroupLineContent = groupLineContent.replaceAll("-", ".");
//			dealedGroupLineContent = dealedGroupLineContent.replaceAll("—", ".");
//
//			//英文替换为中文
//			dealedGroupLineContent = com.xiaotu.makeplays.utils.StringUtils.EnToCHSeparator(dealedGroupLineContent);
			
			String dealedGroupLineContent = this.dealSpecialChar(groupLineValue);
			
			//本行是否为标题
			boolean titleFlag = RegexUtils.regexFind(mainTitleRegex, dealedGroupLineContent);
			boolean isTitle = RegexUtils.regexFind(seriesViewNoRegex, dealedGroupLineContent);	//本行是否匹配只含有集场号的标题
			
			if (supportCNViewNo) {
				String cNViewNo = "";
				//电影的场次第一个元素肯定为“场”，此处识别第一个元素是否为“三十一”这种中文数字
				String[] titleArray = dealedGroupLineContent.split(separateSceripRegex);
				for (int i = 0; i < titleArray.length; i++) {
					if (!StringUtils.isBlank(titleArray[i])) {
						if (!StringUtils.isBlank(com.xiaotu.makeplays.utils.StringUtils.genCNViewNo(titleArray[i]))) {
							cNViewNo = com.xiaotu.makeplays.utils.StringUtils.genCNViewNo(titleArray[i]);
						}
						break;
					}
				}
				
				if (!StringUtils.isBlank(cNViewNo)) {
					String cNTitleRegex = mainTitleRegex.replace("\\d", Constants.REGEX_CN_NUMBER_START);
					if (RegexUtils.regexFind(cNTitleRegex, dealedGroupLineContent)) {
						titleFlag = true;
					}
					
					String cNSeriesViewNoRegex = seriesViewNoRegex.replace("\\d", Constants.REGEX_CN_NUMBER_START);
					if (RegexUtils.regexFind(cNSeriesViewNoRegex, dealedGroupLineContent)) {
						isTitle = true;
					}
				}
			}
			
			//groupLineContent.length() < 50
			if ((titleFlag || isTitle) && !this.inValidViewNoList.contains(dealedGroupLineContent)) {
//				String[] titleArr = lineContent.split(this.lineSeprator);
				String title = dealedGroupLineContent;
				
				String[] titleArray = title.split(separateSceripRegex);

				List<String> titleElementList = new ArrayList<String>();
				for (String str : titleArray) {
					if (StringUtils.isBlank(str)) {
						continue;
					}
					titleElementList.add(str);
				}
				Map<String, Object> elementMap = this.genTitleElement(dealedGroupLineContent, scenarioFormatStr, scenarioFormatList, titleElementList, scripteleInfoList, separatorInfoList, extralSeriesNo, supportCNViewNo);
				boolean validTitle = (Boolean) elementMap.get("validTitle");
				
				if (validTitle) {
					int seriesNo = 0;	// 集数
					if (hasSeriesNoTag) {
						seriesNo = Integer.parseInt((String) elementMap.get("seriesNo"));
					} else {
						seriesNo = Integer.parseInt(extralSeriesNo);
					}
					
					String viewNo = (String) elementMap.get("viewNo");	//场次
//					if (seriesViewNoArr.length > 2) {
//						for (int i = 2; i < seriesViewNoArr.length; i++) {
//							viewNo += "-" + seriesViewNoArr[i];
//						}
//					}
					
					if (seriesViewNoList.contains(seriesNo + "-" + viewNo.trim().toLowerCase())) {
						String errorMsg = seriesNo + "集" + viewNo + "场在剧本中重复，请修改后重新上传。";
						if (!this.titleErrorMsg.contains(errorMsg)) {
							this.titleErrorMsg.add(errorMsg);
						}
						errorTitleList.add(dealedGroupLineContent);
						//throw new IllegalArgumentException(seriesNo + "集" + viewNo + "场在剧本中重复，请修改后重新上传。");
					} else {
//						seresAcenoMap.put(seriesNo, viewNo);
						seriesViewNoList.add(seriesNo + "-" + viewNo.trim().toLowerCase());
					}
				}
			}
		}
	}
	
	/**
	 * 获取剧本的详细内容
	 * @param contentList
	 */
	private List<ScenarioViewDto> genSceDetailInfo(List<String> contentList, int lineCount, int wordCount, 
			Map<String, Object> formatInfo, 
			List<String> scenarioFormatList, 
			List<ScripteleInfoModel> scripteleInfoList, 
			List<SeparatorInfoModel> separatorInfoList, 
			boolean hasSeriesNoTag, String extralSeriesNo,
			boolean groupSeriesNoFlag, boolean supportCNViewNo, boolean pageIncludeTitle) {
		
		String scenarioFormatStr = "";
		for (String format : scenarioFormatList) {
			scenarioFormatStr += format;
		}
		
		String mainTitleRegex = (String) formatInfo.get("mainTitleRegex");	//匹配标题的正则表达式
		int minCount = (Integer) formatInfo.get("minCount");	//标题中元素最小数目
		String separateSceripRegex = (String) formatInfo.get("separateSceripRegex");	//分割标题中元素的正则表达式
		int lineSeperatorCount = (Integer) formatInfo.get("lineSeperatorCount");	//自定义格式中换行符的个数
		String seriesViewNoRegex = (String) formatInfo.get("seriesViewNoRegex");	//只包含集场号的正则表达式
		
		List<ScenarioViewDto> scenarioList = new ArrayList<ScenarioViewDto>();	//剧本基本元素列表
		
		StringBuilder viewTitle = new StringBuilder();	//每场的标题，包括主标题和子标题
		StringBuilder viewContent = new StringBuilder(Constants.CONTENT_BUFFER_SIZE);	//每场内容
		
		int titleNo = 0; 	//标识是第几个标题
		
		/*
		 * 考虑到自定义格式中有换行符,利用这两个变量对行进行打包分析
		 * 例如有一个换行符，则对剧本的分析时按照每两行一个单位进行解析
		 */
		int groupLineCount = 0;	//表示当前的数据是第几行
		String groupLineValue = "";	//存储包括当前行，往上lineSeperatorCount+1行的数据
		
		boolean hasMainTitle = false;	//引入它是为了避免自动编号问题导致的系统能识别的第一场标题之前出现子标题，标题引入过多问题，该变量可以过滤第一场标题的前的所有内容
		
		boolean preLineIsGroupSeries = false;	//上一行是否是“第XXX集”的格式
		String newSeriesNo = "1";	//从剧本内容中解析出来的标题
		
		/*
		 * 提取剧本中每场的详细信息
		 * 分析出详细信息后把信息存入到一个dto的List中
		 */
		//用户存储从剧本中分析出来的元素信息
		for (String myLineContent : contentList) {
			//空行跳过
			if (StringUtils.isBlank(myLineContent)) {
				continue;
			}
			
			myLineContent = myLineContent.trim();
			//此处"?"为不识别的格式编码字符，该字符会在自动编号的剧本中出现
			if (myLineContent.substring(0, 1).equals("﻿?")) {
				myLineContent = myLineContent.substring(1, myLineContent.length());
			}
			
			/*
			 * 跳过第XX集的行
			 */
			if (RegexUtils.regexFind(Constants.REGEX_SERIES, myLineContent) && myLineContent.length() < 15) {
				if (groupSeriesNoFlag) {
					newSeriesNo = this.genSeriesNo(myLineContent) + "";
					preLineIsGroupSeries = true;
				}
				continue;
			}
			
			groupLineCount++;
			groupLineValue += myLineContent + this.lineSeprator;
			if (groupLineCount < lineSeperatorCount + 1) {
				continue;
			}
			
			if (groupLineCount != lineSeperatorCount + 1) {
				String[] groupLineArr = groupLineValue.split(this.lineSeprator);
				groupLineValue = "";
				for(int i = 0, len = groupLineArr.length; i < len; i++) {
					if (i != 0) {
						groupLineValue += groupLineArr[i] + this.lineSeprator;
					}
				}
			}
			
			String dealedGroupLineContent = this.dealSpecialChar(groupLineValue);
			
			boolean standardTitleFlag = RegexUtils.regexFind(mainTitleRegex, dealedGroupLineContent);	 //本行是否为标配的标题
			boolean isTitle = RegexUtils.regexFind(seriesViewNoRegex, dealedGroupLineContent);	//本行是否匹配只含有集场号的标题
			
			if (supportCNViewNo) {
				String cNViewNo = "";
				//电影的场次第一个元素肯定为“场”，此处识别第一个元素是否为“三十一”这种中文数字
				String[] titleArray = dealedGroupLineContent.split(separateSceripRegex);
				for (int i = 0; i < titleArray.length; i++) {
					if (!StringUtils.isBlank(titleArray[i])) {
						if (!StringUtils.isBlank(com.xiaotu.makeplays.utils.StringUtils.genCNViewNo(titleArray[i]))) {
							cNViewNo = com.xiaotu.makeplays.utils.StringUtils.genCNViewNo(titleArray[i]);
						}
						break;
					}
				}
				
				if (!StringUtils.isBlank(cNViewNo)) {
					String cNTitleRegex = mainTitleRegex.replace("\\d", Constants.REGEX_CN_NUMBER_START);
					if (RegexUtils.regexFind(cNTitleRegex, dealedGroupLineContent)) {
						standardTitleFlag = true;
					}
					
					String cNSeriesViewNoRegex = seriesViewNoRegex.replace("\\d", Constants.REGEX_CN_NUMBER_START);
					if (RegexUtils.regexFind(cNSeriesViewNoRegex, dealedGroupLineContent)) {
						isTitle = true;
					}
				}
			}
			
			//如果是标题
			if ((standardTitleFlag || isTitle) && !this.inValidViewNoList.contains(dealedGroupLineContent)) {
				hasMainTitle = true;
				
				titleNo++;
				
				/************************保存上一场信息*********************************/
				if (titleNo > 1 && viewTitle.length() > 0) {
					this.genScenarioDto(viewTitle, viewContent, groupLineValue, myLineContent,
							wordCount, lineCount, 
							scenarioFormatStr, scenarioFormatList, scripteleInfoList, separatorInfoList, 
							extralSeriesNo, separateSceripRegex, 
							hasSeriesNoTag, minCount, scenarioList, supportCNViewNo, pageIncludeTitle);

					
					viewTitle = new StringBuilder();
					viewContent = new StringBuilder(Constants.CONTENT_BUFFER_SIZE);
				}
				if (preLineIsGroupSeries) {
					extralSeriesNo = newSeriesNo;
					preLineIsGroupSeries = false;
				}
				
				viewTitle.append(groupLineValue);
				continue;
			}
			
			
			//如果以上情况都不满足，就说明该行内容为剧本每场的具体内容
			if (!myLineContent.trim().equals("") && hasMainTitle) {
				viewContent.append(myLineContent);
				viewContent.append(this.lineSeprator);
			}
		}
		
		//处理最后一场景信息
		if (!StringUtils.isBlank(viewTitle.toString()) && !this.inValidViewNoList.contains(viewTitle.toString())) {
			this.genScenarioDto(viewTitle, viewContent, "", "", wordCount, 
					lineCount, scenarioFormatStr, scenarioFormatList, 
					scripteleInfoList, separatorInfoList, extralSeriesNo, 
					separateSceripRegex, hasSeriesNoTag, minCount, scenarioList, supportCNViewNo, pageIncludeTitle);
			viewTitle = new StringBuilder();
			viewContent = new StringBuilder(Constants.CONTENT_BUFFER_SIZE);
		}
		
		return scenarioList;
	}
	
	/**
	 * 生成剧本信息
	 */
	private void genScenarioDto(StringBuilder viewTitle, StringBuilder viewContent, 
			String groupLineContent, String myLineContent,
			int wordCount, int lineCount,
			String scenarioFormatStr, List<String> scenarioFormatList, 
			List<ScripteleInfoModel> scripteleInfoList, 
			List<SeparatorInfoModel> separatorInfoList,
			String extralSeriesNo, String separateSceripRegex, boolean hasSeriesNoTag, int minCount,
			List<ScenarioViewDto> scenarioList, boolean supportCNViewNo, boolean pageIncludeTitle) {
		if (viewContent.length() > 5000) {
			throw new IllegalArgumentException("《" + viewTitle + "》场内容过长，请检查剧本格式");
		}

		ScenarioViewDto scenarioViewDto = new ScenarioViewDto();
		
		//拼接用户替换的正则表达式
		StringBuilder linSepWordRegex = new StringBuilder("(");
		for (String lineSepWord : ScenarioService.lineSeqWordList) {
			linSepWordRegex.append(lineSepWord + "|");
			linSepWordRegex.append("\\[" + lineSepWord + "\\]|");
			linSepWordRegex.append("\\(" + lineSepWord + "\\)|");
			linSepWordRegex.append("【" + lineSepWord + "】|");
			linSepWordRegex.append("（" + lineSepWord + "）|");
			linSepWordRegex.append(lineSepWord + "|");
		}
		linSepWordRegex.append(")");
		
		int contentStartIndex = 0;
		String remark = "";
		for (String lineSepWord : ScenarioService.lineSeqWordList) {
			if (viewTitle.indexOf(lineSepWord) != -1) {
				viewContent.insert(0, lineSepWord + this.lineSeprator);
				contentStartIndex += lineSepWord.length() + 2;
				remark += lineSepWord + " ";
			}
		}
		scenarioViewDto.setRemark(remark);
		
		viewTitle = new StringBuilder(viewTitle.toString().replaceAll(linSepWordRegex.toString(), ""));
		
		String dealedTitle = this.dealSpecialChar(viewTitle.toString());
		
		if (!errorTitleList.contains(dealedTitle)) {
			String[] titleArray = dealedTitle.split(separateSceripRegex);

			List<String> titleElementList = new ArrayList<String>();
			for (String str : titleArray) {
				if (StringUtils.isBlank(str)) {
					continue;
				}
				titleElementList.add(str);
			}
			
			
			Map<String, Object> elementMap = this.genTitleElement(dealedTitle, scenarioFormatStr, scenarioFormatList, titleElementList, scripteleInfoList, separatorInfoList, extralSeriesNo, supportCNViewNo);
			this.fetchSceTitleDetail(viewTitle.toString(), scenarioViewDto, minCount, separateSceripRegex, elementMap, hasSeriesNoTag, extralSeriesNo);	//获取标题中的元素并存储到dto对象中
			
			//判断是否是标题，如果不是，保存标题时，只保存取到的第一行
			boolean isStandardTitle = (Boolean) elementMap.get("isStandardTitle");
			boolean isSeiresViewNoTitle = (Boolean) elementMap.get("isTitle");
			boolean isNoFigure = (Boolean) elementMap.get("isNoFigure");
			int figureLineNum = (Integer) elementMap.get("figureLineNum");
			boolean eleValidTitle = (Boolean) elementMap.get("validTitle");
			
			String[] viewTitleArr = viewTitle.toString().split(this.lineSeprator);
			int size = viewTitleArr.length;
			StringBuilder toSaveTitle = new StringBuilder();
			if (!isStandardTitle && !isNoFigure && isSeiresViewNoTitle) {
				for (int i = size - 1; i >= 0; i--) {
					if (i < 1) {
						toSaveTitle.append(viewTitleArr[0]);
					} else {
						viewContent.insert(contentStartIndex, viewTitleArr[i] + this.lineSeprator);
					}
				}
			} else if (!isStandardTitle && isNoFigure && viewTitleArr.length - figureLineNum > 0) {
				for (int i = size - 1; i >= 0; i--) {
					if (i < viewTitleArr.length - figureLineNum) {
						if (i == viewTitleArr.length - figureLineNum - 1) {
							toSaveTitle.insert(0, viewTitleArr[i]);
						} else {
							toSaveTitle.insert(0, viewTitleArr[i] + this.lineSeprator);
						}
					} else {
						viewContent.insert(contentStartIndex, viewTitleArr[i] + this.lineSeprator);
					}
				}
			} else {
				toSaveTitle = new StringBuilder(viewTitle.toString());
			}
			
			String title = toSaveTitle.toString().replaceAll(this.lineSeprator + "+", this.lineSeprator);
			if (title.endsWith(this.lineSeprator)) {
				title = title.substring(0, title.length() - this.lineSeprator.length());
			}
			scenarioViewDto.setTitle(title);
			String groupLineExceptOwn = groupLineContent.replace(myLineContent + this.lineSeprator, "");
			String viewContentStr = viewContent.toString().replace(groupLineExceptOwn, "");
			viewContentStr = viewContentStr.replaceAll(" |\t", "");
			viewContentStr = viewContentStr.replaceAll(this.lineSeprator + "+", this.lineSeprator);
			if (viewContentStr.endsWith(this.lineSeprator)) {
				viewContentStr = viewContentStr.substring(0, viewContentStr.length() - this.lineSeprator.length());
			}
			
			scenarioViewDto.setContent(viewContentStr);	
			
			//计算页数
			String pageContent = viewContentStr;
			if (pageIncludeTitle) {
				pageContent = toSaveTitle.toString() + this.lineSeprator + pageContent;
			}
			
			int viewLineCount = this.calculateLineCount(pageContent, wordCount, true);
			double pageCount=com.xiaotu.makeplays.utils.StringUtils.div(viewLineCount, lineCount, 1);
			if (pageCount == 0.0 && StringUtils.isNotBlank(viewContentStr)) {
				pageCount = 0.1;
			}
			scenarioViewDto.setPageCount(pageCount);
			
			if (eleValidTitle) {
				scenarioList.add(scenarioViewDto);
			}
		}
	}
	
	/**
	 * 计算文本的实际行数
	 * 		（1）如果某行的字数大于wordCount指定的字数，需要根据该行的字数/wordCount计算该行文本的实际行数
	 * 		（2）不足一行的按一行计算，即计算的行数存在小数，取大于该小数的最小整数
	 * @param content
	 * @param wordCount 设置的一行文本的字数
	 * @param isCalculateNullLine 是否计算空行
	 * @return
	 */
	public int calculateLineCount(String content,int wordCount,boolean isCalculateNullLine) {

		int lineCount =0;
		
		if(wordCount<=0){
			return lineCount;
		}
		
		if(!StringUtils.isBlank(content)) {
			String []arr = content.split(this.lineSeprator);
			
			for(String str: arr) {
				if(!isCalculateNullLine && str.trim().equals("")){
					//不计算空行
					continue;
				}
				int len = str.length();
				if(len > wordCount) {					
					double v = com.xiaotu.makeplays.utils.StringUtils.div(len, wordCount, 2);
					double rv = Math.ceil(v);
					int temp = Integer.parseInt(new DecimalFormat("0").format(rv));
					lineCount = lineCount + temp;
				}
				else {
					lineCount++;
				}
			}
			
		}
		
		return lineCount;
	}
	
	/**
	 * 解析每场标题中的详细信息
	 * @param viewTitle
	 */
	private void fetchSceTitleDetail (String viewTitle, ScenarioViewDto scenarioDto, 
			int minCount, String separateSceripRegex, 
			Map<String, Object> elementMap, 
			boolean hasSeriesNoTag, String extralSeriesNo) {
		Integer seriesNo = null;	//集次
		String viewNo = "";	//场次
		String atmosphere = "";	//气氛
		String site = "";	//内外景
		List<String> addrList = new LinkedList<String>();// 场景地点列表
		List<String> roleNameList = new ArrayList<String>();
		boolean eleValidTitle = (Boolean) elementMap.get("validTitle");
		
		if (eleValidTitle && !StringUtils.isBlank(viewTitle)) {
//			String[] titleArr = viewTitle.trim().split(this.lineSeprator);
//			String mainTitle = titleArr[0];	//主标题
//			String subTitle = "";	//子标题
//			if (titleArr.length > 1) {
//				subTitle = titleArr[1];
//			}
			
			if(!StringUtils.isBlank(viewTitle)) {
//				String[] titleArray = viewTitle.split(separateSceripRegex);
//				int titleLength = titleArray.length;
				
				//处理“集-场”信息
//				if (titleLength > 0) {
					// 集数-场景编号
//					String series_actNo = titleArray[0];
//					
//					series_actNo = series_actNo.replaceAll(Constants.REGEX_CHINESE_WORD, "");
//					// 处理集及场次
//					int sa_offset = series_actNo.indexOf(SepratorConstant.SEP_CROSS_EN) == -1 
//							? series_actNo.indexOf(SepratorConstant.SEP_CROSS_CN) : series_actNo.indexOf(SepratorConstant.SEP_CROSS_EN);
//					String[] seriesViewNoArr = RegexUtils.regexSplitStr(Constants.REGEX_VIEW_TITLE_SEPRATOR, series_actNo);
					if (hasSeriesNoTag) {
						seriesNo = Integer.parseInt((String) elementMap.get("seriesNo"));// 集数
					} else {
						seriesNo = Integer.parseInt(extralSeriesNo);// 集数
					}
					
					viewNo = (String) elementMap.get("viewNo");	//场次
//					if (seriesViewNoArr.length > 2) {
//						for (int i = 2; i < seriesViewNoArr.length; i++) {
//							viewNo += "-" + seriesViewNoArr[i];
//						}
//					}
//				}
				
				//处理拍摄场景、气氛、内外景信息
//				if (titleLength >= Constants.TITLE_ATLEAST_MEMBER_COUNT) {
//					for (int i = 1; i < titleLength; i++) {
//						String titleArg = titleArray[i];
//						if (StringUtils.isBlank(titleArg)) {
//							continue;
//						}
//						
//						//如果是标题中最后一个元素，那就肯定是"气氛/内外景"信息
//						if (i == titleLength - 1) {
//							if(titleArg.indexOf(SepratorConstant.SEP_SLASH_EN) != -1 || titleArg.indexOf(SepratorConstant.SEP_SLASH_CN) != -1) {
//								// 处理日夜／内外
//								int at_offset = titleArg.indexOf(SepratorConstant.SEP_SLASH_EN) == -1 
//										? titleArg.indexOf(SepratorConstant.SEP_SLASH_CN) : titleArg.indexOf(SepratorConstant.SEP_SLASH_EN);
//								atmosphere = titleArg.substring(0, at_offset);// 气氛信息(日夜)
//								// 夜/雪/外（跳过雪，只留外）
//								int at_site_offset = titleArg.lastIndexOf(SepratorConstant.SEP_SLASH_EN) == -1 
//										? titleArg.lastIndexOf(SepratorConstant.SEP_SLASH_CN) : titleArg.lastIndexOf(SepratorConstant.SEP_SLASH_EN);
//								site = titleArg.substring(at_site_offset + 1);// 内外景信息(内外)
//								
//								
//							}
//							continue;
//						}
//						
//						addrList.add(titleArg);
//					}
//				}
				
				atmosphere = (String) elementMap.get("atmosphere");
				site = (String) elementMap.get("site");
				
				//如果气氛和内外景有明显的颠倒现象，则把这两个值交换
				if (atmosphere != null && site != null 
						&& (atmosphere.indexOf("内") != -1 || atmosphere.indexOf("外") != -1) 
						&& (site.indexOf("日") != -1 || site.indexOf("夜") != -1)) {
					String temp = atmosphere;
					atmosphere = site;
					site = temp;
				}
				
				String addrStr = (String) elementMap.get("viewLocation");
				if (!StringUtils.isBlank(addrStr)) {
					addrList = Arrays.asList(addrStr.split(","));
				}
				
				scenarioDto.setSeriesNo(seriesNo);
				scenarioDto.setViewNo(viewNo);
				scenarioDto.setAtmosphere(atmosphere);
				scenarioDto.setSite(site);
				if (addrList.size() > 0) {
					scenarioDto.setFirstLocation(addrList.get(0));
				}
				if (addrList.size() > 1) {
					scenarioDto.setSecondLocation(addrList.get(1));
				}
				if (addrList.size() > 2) {
					scenarioDto.setThirdLocation(addrList.get(2));
				}
			}
			
			String figure = (String) elementMap.get("figure");
			if (!StringUtils.isBlank(figure)) {
				roleNameList = Arrays.asList(figure.split(","));
				scenarioDto.setToConfirmRoleNameList(roleNameList);
			}
			
			
			Map<String, Integer> seasonMap = new HashMap<String, Integer>();
	    	seasonMap.put("春", 1);
	    	seasonMap.put("夏", 2);
	    	seasonMap.put("秋", 3);
	    	seasonMap.put("冬", 4);
	    	
			String season = (String) elementMap.get("season");
			if (!StringUtils.isBlank(season)) {
	    		Integer seasonValue = seasonMap.get(season);
	    		scenarioDto.setSeason(seasonValue);
	    	}
		}
	}
	
	/**
	 * 根据标题获取角色
	 * @param title 人物：张三，李四
	 * @return
	 */
	private List<String> getRoleNameByTitle(String title) {
		List<String> roleNameList = new ArrayList<String>();
		String []subArr = title.split(Constants.REGEX_SUBTITLE_SPLIT_CHAR);
		if(subArr != null && subArr.length > 1 && !StringUtils.isBlank(subArr[1])) {
			String[] nameArr = subArr[1].split(Constants.REGEX_TITLE_FIGURE_SPLIT_CHAR);
			for(String roleName: nameArr) {
				roleName=RegexUtils.replaceSpace(roleName);
				if(StringUtils.isEmpty(roleName)) {
					continue;
				}
				
				if(!roleNameList.contains(roleName)) {
					roleNameList.add(roleName);
				}
			}
		}
		return roleNameList;
	}
	
	/**
	 * 把剧本中的内容和数据库中已经存储的内容进行比较
	 * 所有场景如下：
	 * 1、当前上传剧本所包含的场次已存在于场景表中，如果该场次的拍摄状态为“已完成”、“加戏”、“删戏”，自动“跳过”
	 * 2、当前上传剧本所包含的场次已存在于场景表中，如果该场次未手动保存，自动“替换”
	 * 3、当前上传剧本所包含的场次已存在于场景表中，如果该场次已手动保存，但两次上传的剧本中该场次的剧本文本相同，自动“跳过”
	 * 4、当前上传剧本所包含的场次已存在于场景表中，如果该场次已手动保存，且两次上传的剧本中该场次的剧本文本不同，需用户选择“跳过”或“替换”，
	 * 5、已存在于场景表中但当前所上传的剧本文本中不存在的场次，如果该场次的拍摄状态为“已完成”、“加戏”、“删戏”，自动“保留”
	 * 6、已存在于场景表中但当前所上传的剧本文本中不存在的场次，否则，需用户选择“保留”或“不保留”，可批量操作
	 * 需要过滤出的数据如下：
	 * 1、自动“替换”的数据
	 * 2、自动保存的数据
	 * 3、供用户选择“跳过”或“替换”的数据
	 * 4、供用户选择“保留”或“不保留”的数据
	 * @param scenarioDtoList TODO
	 */
	public Map<String, Object> compareViewWithDataInDB(String crewId, List<ScenarioViewDto> scenarioDtoList) {
		Map<String, Object> viewDataMap = new HashMap<String, Object>(); 
		
		if (scenarioDtoList == null || scenarioDtoList.size() == 0) {
			return viewDataMap;
		}
		
		int minSeriesNo = scenarioDtoList.get(0).getSeriesNo();	//剧本中最小集数
		int maxSeriesNo = -1;
		
		List<ViewInfoModel> viewInfoList = this.viewService.queryByCrewId(crewId, null);
		
		List<ScenarioViewDto> autoReplaceData = new ArrayList<ScenarioViewDto>();	//自动“替换”的数据
		List<ScenarioViewDto> autoSaveData = new ArrayList<ScenarioViewDto>();	//自动保存的数据
		List<ScenarioViewDto> skipOrReplaceData = new ArrayList<ScenarioViewDto>();	//供用户选择“跳过”或“替换”的数据
		List<ViewInfoModel> keepOrNotData = new ArrayList<ViewInfoModel>();	//供用户选择“保留”或“不保留”的数据
		
		if (viewInfoList == null || viewInfoList.size() == 0) {
			//保存所有数据
			autoSaveData.addAll(scenarioDtoList);
		} else {
			//过滤数据
			for(ViewInfoModel viewInfo : viewInfoList) {
				boolean flag = true;	//标识数据是否是已存在于场景表中但当前所上传的剧本文本中不存在的场次
				
				for (ScenarioViewDto scenarioViewDto : scenarioDtoList) {
					if (scenarioViewDto.getSeriesNo() < minSeriesNo) {
						minSeriesNo = scenarioViewDto.getSeriesNo();
					}
					if (scenarioViewDto.getSeriesNo() > maxSeriesNo) {
						maxSeriesNo = scenarioViewDto.getSeriesNo();
					}
					
					//当前上传剧本所包含的场次已存在于场景表中
					if (scenarioViewDto.getSeriesNo() == viewInfo.getSeriesNo() 
							&& scenarioViewDto.getViewNo().toUpperCase().trim().equals(viewInfo.getViewNo().toUpperCase().trim())) {
						//场次的拍摄状态不是“已完成”、“加戏”、“删戏”
						if (viewInfo.getShootStatus() != ShootStatus.Finished.getValue()
								&& viewInfo.getShootStatus() != ShootStatus.AddXiFinished.getValue()
								&& viewInfo.getShootStatus() != ShootStatus.AddXiUnfinish.getValue()
								&& viewInfo.getShootStatus() != ShootStatus.DeleteXi.getValue()) {
							if (!viewInfo.getIsManualSave()) {
								autoReplaceData.add(scenarioViewDto);
							} else {
								skipOrReplaceData.add(scenarioViewDto);
							}
						}
						flag = false;
						break;
					}
				}
				if (flag && viewInfo.getShootStatus() != ShootStatus.Finished.getValue()
						&& viewInfo.getShootStatus() != ShootStatus.AddXiFinished.getValue()
						&& viewInfo.getShootStatus() != ShootStatus.AddXiUnfinish.getValue()
						&& viewInfo.getShootStatus() != ShootStatus.DeleteXi.getValue()) {
					keepOrNotData.add(viewInfo);
				}
			}
			
			
			for (ScenarioViewDto scenarioViewDto : scenarioDtoList) {
				boolean flag = true;	//标识数据是否是不存在于场景表中但当前所上传的剧本文本中存在的场次
				for (ViewInfoModel viewInfo : viewInfoList) {
					if (scenarioViewDto.getSeriesNo() == viewInfo.getSeriesNo() 
							&& scenarioViewDto.getViewNo().toUpperCase().trim().equals(viewInfo.getViewNo().toUpperCase().trim())) {
						flag = false;
						break;
					}
				}
				if (flag) {
					autoSaveData.add(scenarioViewDto);
				}
			}
			
			
		}
		
		viewDataMap.put("autoReplaceData", autoReplaceData);
		viewDataMap.put("autoSaveData", autoSaveData);
		viewDataMap.put("skipOrReplaceData", skipOrReplaceData);
		viewDataMap.put("keepOrNotData", keepOrNotData);
		viewDataMap.put("minSeriesNo", minSeriesNo);
		
		return viewDataMap;
	}
	
	
	/**
	 * 获取最新上传的剧本信息
	 * @param crewId
	 * @return
	 * @throws Exception 
	 */
	public ScenarioInfoModel queryLastScenario(String crewId) throws Exception {
		return this.scenarioDao.queryLastScenario(crewId);
	}
	
	/**
	 * 保存剧本书签信息
	 * 该方法带有删除用户志之前剧本书签的功能
	 * @param bookMarkInfo
	 * @throws Exception 
	 */
	public void saveSceBookMark(BookMarkModel bookMarkInfo) throws Exception {
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("crewId", bookMarkInfo.getCrewId());
		conditionMap.put("userId", bookMarkInfo.getUserId());
		conditionMap.put("type", bookMarkInfo.getType());
		List<BookMarkModel> existBokMarkInfoList = this.bookMarkDao.queryManyByMutiCondition(conditionMap, null);
		if (existBokMarkInfoList != null && existBokMarkInfoList.size() > 0) {
			this.bookMarkDao.deleteOne(existBokMarkInfoList.get(0).getId(), "id", BookMarkModel.TABLE_NAME);
		}
		
		this.bookMarkDao.add(bookMarkInfo);
	}
	
	/**
	 * 页数反刷
	 * @param crewId
	 * @param lineCount
	 * @param wordCount
	 */
	public void refreshPage(String crewId, Integer lineCount, Integer wordCount, boolean pageIncludeTitle) {
		
		
		int totalCount = this.viewContentDao.countView(crewId);
		
		int batchCount = totalCount / batchSize;
		
		for (int i = 0; i < batchCount; i++) {
			Page page = new Page();
			page.setPageNo(i + 1);
			page.setPagesize(batchSize);
			
			List<Map<String, Object>> viewInfoList = this.viewContentService.queryViewContent(crewId, page, null);
			for (Map<String, Object> viewInfo : viewInfoList) {
				String viewId = (String) viewInfo.get("viewId");
				String title = (String) viewInfo.get("title");
				String content = (String) viewInfo.get("content");
				
				String pageContent = content;
				if (pageIncludeTitle) {
					pageContent = title + this.lineSeprator + pageContent;
				}
				int viewLineCount = this.calculateLineCount(pageContent, wordCount, true);
				double pageCount=com.xiaotu.makeplays.utils.StringUtils.div(viewLineCount, lineCount, 1);
				
				this.viewInfoDao.updatePageCountById(pageCount, viewId);
			}
		}
		
		Page page = new Page();
		page.setPageNo(batchCount + 1);
		int pageSize = totalCount - batchCount * batchSize;
		page.setPagesize(batchSize);
		if (pageSize != 0) {
			// 查询未保存的场景内容信息
			List<Map<String, Object>> viewInfoList = this.viewContentService.queryViewContent(crewId, page, null);
			
			for (Map<String, Object> viewInfo : viewInfoList) {
				String viewId = (String) viewInfo.get("viewId");
				String title = (String) viewInfo.get("title");
				String content = (String) viewInfo.get("content");
				
				String pageContent = content;
				if (pageIncludeTitle) {
					pageContent = title + this.lineSeprator + pageContent;
				}
				int viewLineCount = this.calculateLineCount(pageContent, wordCount, true);
				double pageCount=com.xiaotu.makeplays.utils.StringUtils.div(viewLineCount, lineCount, 1);
				
				this.viewInfoDao.updatePageCountById(pageCount, viewId);
			}
		}
		
	}
	
	/**
	 * 服化道反刷
	 * @param crewId
	 * @throws Exception 
	 */
	public void refreshProp(String crewId) throws Exception {
		//删除剧组下所有未手动保存的场景和物品的关联
		this.viewGoodsInfoMapDao.deleteNoSaveViewPropMap(crewId);
		
		//查询出当前剧组中的所有的服化道信息
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("crewId", crewId);
		List<GoodsInfoModel> goodsList = this.goodsInfoDao.queryGoodsByCondition(conditionMap);
		
		//查询出未保存的场景的信息的总数
		int viewContentCount = this.viewContentService.countViewContent(crewId, null);

		int batchCount = viewContentCount / batchSize;

		for (int i = 0; i < batchCount; i++) {
			Page page = new Page();
			page.setPageNo(i + 1);
			page.setPagesize(batchSize);

			// 查询未保存的场景内容信息
			List<Map<String, Object>> viewContentList = this.viewContentService.queryViewContent(crewId, page, null);
			
			Map<String, Object> map = this.genViewPropMap(crewId, viewContentList, goodsList);
			
			List<ViewGoodsInfoMap> viewPropMapList = (List<ViewGoodsInfoMap>) map.get("viewPropMapList");
			//未提取的服化道信息
			List<Map<String, String>> viewInfoList = (List<Map<String, String>>) map.get("viewInfoList");
			
			this.viewGoodsInfoMapDao.addBatch(viewPropMapList, ViewGoodsInfoMap.class);
			//更新场景中的未提取的服化道信息
			this.viewInfoDao.updateNotGetPropsBatch(viewInfoList);
		}
		
		//处理最后一页
		Page page = new Page();
		page.setPageNo(batchCount + 1);
		int pageSize = viewContentCount - batchCount * batchSize;
		page.setPagesize(batchSize);
		if (pageSize != 0) {
			// 查询未保存的场景内容信息
			List<Map<String, Object>> viewContentList = this.viewContentService.queryViewContent(crewId, page, null);
			Map<String, Object> map = this.genViewPropMap(crewId, viewContentList, goodsList);
			List<ViewGoodsInfoMap> viewPropMapList = (List<ViewGoodsInfoMap>) map.get("viewPropMapList");
			List<Map<String, String>> viewInfoList = (List<Map<String, String>>) map.get("viewInfoList");
			
			this.viewGoodsInfoMapDao.addBatch(viewPropMapList, ViewGoodsInfoMap.class);
			this.viewInfoDao.updateNotGetPropsBatch(viewInfoList);
		}
	}
	
	/**
	 * 生成场景道具关联关系对象
	 * @param crewId	剧组ID
	 * @param viewContentList 场景内容信息
	 * @param propsList	道具信息
	 * @return
	 */
	private Map<String, Object> genViewPropMap(String crewId, List<Map<String, Object>> viewContentList, List<GoodsInfoModel> goodsList) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		List<ViewGoodsInfoMap> viewPropMapList = new ArrayList<ViewGoodsInfoMap>();
		List<Map<String, String>> viewInfoList = new ArrayList<Map<String, String>>();
		
		for (Map<String, Object> viewContentMap : viewContentList) {
			String viewId = (String) viewContentMap.get("viewId");
			String viewContent = (String) viewContentMap.get("content");
			int isManualSave = (Integer) viewContentMap.get("isManualSave");
			
			if (!StringUtils.isBlank(viewContent)) {
				String notGetProps = "";
				
				String[] viewContentArray = viewContent.split(this.lineSeprator);
				
				for (GoodsInfoModel goodsInfo : goodsList) {
					boolean existInDia = false;	//存在于对话中
					boolean existInUnDia = false;	//存在于非对话中
					
					String goodspName = goodsInfo.getGoodsName();
					String goodspId = goodsInfo.getId();
					
					//遍历剧本中每一行的内容
					for (String lineContent : viewContentArray) {
						//只有不在对话中的角色才提取出来
						if (isManualSave == 0 && this.checkContainRole(goodspName, lineContent)) {
							ViewGoodsInfoMap viewPropsMap = new ViewGoodsInfoMap();
							viewPropsMap.setId(UUIDUtils.getId());
							viewPropsMap.setGoodsId(goodspId);
							viewPropsMap.setCrewId(crewId);
							viewPropsMap.setViewId(viewId);
							viewPropMapList.add(viewPropsMap);
							
							existInUnDia = true;
							break;
						}
					}
					
					if (isManualSave == 0 && viewContent.indexOf(goodspName) != -1) {
						existInDia = true;
					}
					
					if (existInDia && !existInUnDia) {
						//该角色为“剧本中未提取的角色”
						notGetProps +=  goodspName + "/"; 
					}
				}
				
				if (!StringUtils.isBlank(notGetProps)) {
					Map<String, String> viewInfo = new HashMap<String, String>();
					viewInfo.put("viewId", viewId);
					viewInfo.put("notGetProps", notGetProps);
					
					viewInfoList.add(viewInfo);
				}
			}
		}
		resultMap.put("viewPropMapList", viewPropMapList);
		resultMap.put("viewInfoList", viewInfoList);
		return resultMap;
	}
	
	/**
	 * 剧本人物反刷
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	public void refreshFigure(String crewId, String newRoleNames) throws Exception {
		
		List<ViewRoleModel> viewRoleList = new ArrayList<ViewRoleModel>();
		if (!StringUtils.isBlank(newRoleNames)) {
			String[] roleNameArray = newRoleNames.split(",");
			for (String roleNameStr : roleNameArray) {
				String[] roleArr = roleNameStr.split("-");
				//根据当前角色的场数，判断角色的类型
				ViewRoleModel viewRoleInfo = new ViewRoleModel();
				String viewCountStr = roleArr[1];
				Double viewCount = null;
				if (StringUtils.isNotBlank(viewCountStr)) {
					viewCount = Double.parseDouble(viewCountStr);
				}else {
					viewCount = 0.0;
				}
				
				viewRoleInfo.setViewRoleId(UUIDUtils.getId());
				viewRoleInfo.setViewRoleName(roleArr[0]);
				
				/**
				 * 根据当前角色的场数来判断属于那种角色；
				 * 规则：场数大于100或则占总场数的40%则为主演；
				 * 场数大于50或占总场数的10为特约演员；其余的为群众演员
				 */
				//查询出总场数
				Double totalViewCount = null ;
				Map<String, Object> totalInfo = viewInfoDao.queryViewTotalInfo(crewId);
				if (totalInfo != null) {
					totalViewCount = Double.parseDouble(totalInfo.get("totalViewCount").toString());
				}else {
					totalViewCount = 1.0;
				}
				
				//计算当前场数与总场数的比值
				double viewRate = viewCount / totalViewCount * 100;
				if (viewCount >= 100 || viewRate >= 40) { // 主演
					viewRoleInfo.setViewRoleType(ViewRoleType.MajorActor.getValue());
				} else if (viewCount >= 50 || viewRate >= 10) { // 特约演员
					viewRoleInfo.setViewRoleType(ViewRoleType.GuestActor.getValue());
				} else { // 群众演员
					viewRoleInfo.setViewRoleType(ViewRoleType.MassesActor.getValue());
				}
				viewRoleInfo.setCrewId(crewId);
				viewRoleList.add(viewRoleInfo);
			}
			this.viewRoleService.addBatch(viewRoleList);
		}
		
		Map<String, Object> roleConditionMap = new HashMap<String, Object>();
		roleConditionMap.put("crewId", crewId);
		viewRoleList.addAll(this.viewRoleService.queryManyByMutiCondition(roleConditionMap, null));
		
		
		//删除剧组下所有未手动保存的场景和角色的关联
		this.viewRoleMapDao.deleteNoSaveViewRoleMap(crewId);
		
		int viewContentCount = this.viewContentService.countViewContent(crewId, null);

		int batchCount = viewContentCount / batchSize;

		for (int i = 0; i < batchCount; i++) {
			Page page = new Page();
			page.setPageNo(i + 1);
			page.setPagesize(batchSize);

			// 查询未保存的场景内容信息
			List<Map<String, Object>> viewContentList = this.viewContentService.queryViewContent(crewId, page, null);
			
			Map<String, Object> map = this.genViewRoleMap(crewId, viewContentList, viewRoleList);
			
			List<ViewRoleMapModel> subViewRoleMapList = (List<ViewRoleMapModel>) map.get("viewRoleMapList");
			List<Map<String, String>> viewInfoList = (List<Map<String, String>>) map.get("viewInfoList");
			
			this.viewRoleMapDao.addMany(subViewRoleMapList);
			this.viewInfoDao.updateNotGetRoleNamesBatch(viewInfoList);
		}
		Page page = new Page();
		page.setPageNo(batchCount + 1);
		int pageSize = viewContentCount - batchCount * batchSize;
		page.setPagesize(batchSize);
		if (pageSize != 0) {
			// 查询未保存的场景内容信息
			List<Map<String, Object>> viewContentList = this.viewContentService.queryViewContent(crewId, page, null);
			
			Map<String, Object> map = this.genViewRoleMap(crewId, viewContentList, viewRoleList);
			
			List<ViewRoleMapModel> subViewRoleMapList = (List<ViewRoleMapModel>) map.get("viewRoleMapList");
			List<Map<String, String>> viewInfoList = (List<Map<String, String>>) map.get("viewInfoList");
			
			this.viewRoleMapDao.addMany(subViewRoleMapList);
			this.viewInfoDao.updateNotGetRoleNamesBatch(viewInfoList);
		}
	}
	
	/**
	 * 生成场景角色关联关系对象
	 * @param crewId	剧组ID
	 * @param viewContentList 场景内容信息
	 * @param viewRoleList	主要演员信息
	 * @return
	 */
	private Map<String, Object> genViewRoleMap(String crewId, List<Map<String, Object>> viewContentList, List<ViewRoleModel> viewRoleList) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		List<ViewRoleMapModel> viewRoleMapList = new ArrayList<ViewRoleMapModel>();
		List<Map<String, String>> viewInfoList = new ArrayList<Map<String, String>>();
		
		for (Map<String, Object> viewContentMap : viewContentList) {
			String viewId = (String) viewContentMap.get("viewId");
			String viewContent = (String) viewContentMap.get("content");
			int isManualSave = (Integer) viewContentMap.get("isManualSave");
			
			if (!StringUtils.isBlank(viewContent)) {
				String notGetRoleNames = "";
				
				String[] viewContentArray = viewContent.split(this.lineSeprator);
					
				for (ViewRoleModel majorRole : viewRoleList) {
					
					boolean existInDia = false;	//存在于对话中
					boolean existInUnDia = false;	//存在于非对话中

					
					String viewRoleName = majorRole.getViewRoleName();
					String viewRoleId = majorRole.getViewRoleId();
					
					//遍历剧本中每一行的内容
					for (String lineContent : viewContentArray) {
						
						//只有不在对话中的角色才提取出来
						if (isManualSave == 0 && this.checkContainRole(viewRoleName, lineContent)) {
							ViewRoleMapModel viewRoleMap = new ViewRoleMapModel();
							viewRoleMap.setCrewId(crewId);
							viewRoleMap.setMapId(UUIDUtils.getId());
							viewRoleMap.setRoleNum(1);
							viewRoleMap.setViewId(viewId);
							viewRoleMap.setViewRoleId(viewRoleId);
							viewRoleMapList.add(viewRoleMap);
							
							existInUnDia = true;
							break;
						}
					}
					
					if (isManualSave == 0 && viewContent.indexOf(viewRoleName) != -1) {
						existInDia = true;
					}
					
					if (existInDia && !existInUnDia) {
						//该角色为“剧本中未提取的角色”
						notGetRoleNames +=  viewRoleName + "/"; 
					}
				}
				
				if (!StringUtils.isBlank(notGetRoleNames)) {
					Map<String, String> viewInfo = new HashMap<String, String>();
					viewInfo.put("viewId", viewId);
					viewInfo.put("notGetRoleNames", notGetRoleNames);
					
					viewInfoList.add(viewInfo);
				}
			}
		}
		
		resultMap.put("viewRoleMapList", viewRoleMapList);
		resultMap.put("viewInfoList", viewInfoList);
		
		return resultMap;
	}
	
	/**
	 * 校验一行内容是否包含指定的角色
	 * @param roleName
	 * @param lineContent
	 * @return
	 */
	public boolean checkContainRole(String roleName, String lineContent) {
		//viewContent.indexOf(viewRoleName) != -1
		int roleIndex = lineContent.indexOf(roleName);
		int sepColonEn = lineContent.indexOf(":");
		int sepColonCn = lineContent.indexOf("：");
		int sepSecondsEn = lineContent.indexOf("\"");
		int sepSecondsCn = lineContent.indexOf("“");
		
		boolean result = false;
		
		if (roleIndex != -1) {
			result = true;
			
			if (sepColonEn != -1 && sepColonEn < roleIndex) {
				result = false;
			}
			if (sepColonCn != -1 && sepColonCn < roleIndex) {
				result = false;
			}
			if (sepSecondsEn != -1 && sepSecondsEn < roleIndex) {
				result = false;
			}
			if (sepSecondsCn != -1 && sepSecondsCn < roleIndex) {
				result = false;
			}
		}
		
		return result;
	}
	
	/**
	 * 发布剧本
	 * @param request
	 * @param updateScenarioIds
	 * @param addScenarioIds
	 * @return
	 * @throws Exception 
	 */
	public void publishScenario(String userId, String crewId, String title, String content, boolean autoShowPublishWin) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		//更新设置信息
		PublishScenarioSettingModel settingInfo = this.publishScenarioSettingService.queryByCrewIdAndUserId(crewId, userId);
		if (settingInfo == null) {
			settingInfo = new PublishScenarioSettingModel();
			settingInfo.setId(UUIDUtils.getId());
			settingInfo.setCrewId(crewId);
			settingInfo.setUserId(userId);
			settingInfo.setAutoShowPublishWin(autoShowPublishWin);
			this.publishScenarioSettingService.addOne(settingInfo);
		} else {
			settingInfo.setAutoShowPublishWin(autoShowPublishWin);
			this.publishScenarioSettingService.updateOne(settingInfo);
		}
		
		/*
		 * 把修改未发布和新增未发布的剧本同步到历史剧本表中，然后把状态改为已发布
		 */
		List<ViewContentModel> notPublishedViewContentList = this.viewContentService.queryNotPublishedContentList(crewId);
		List<HistoryViewContentModel> toAddHistoryContentList = new ArrayList<HistoryViewContentModel>();
		for (ViewContentModel viewContentInfo : notPublishedViewContentList) {
			viewContentInfo.setStatus(ViewContentStatus.Published.getValue());
			viewContentInfo.setReadedPeopleIds(null);
			
			HistoryViewContentModel historyViewContent = new HistoryViewContentModel();
			historyViewContent.setId(UUIDUtils.getId());
			historyViewContent.setViewId(viewContentInfo.getViewId());
			historyViewContent.setContent(viewContentInfo.getContent());
			historyViewContent.setTitle(viewContentInfo.getTitle());
			historyViewContent.setCrewId(crewId);
			historyViewContent.setVersion(sdf.format(new Date()));
			historyViewContent.setCreateTime(new Date());
			toAddHistoryContentList.add(historyViewContent);
		}
		
		this.viewContentDao.updateBatch(notPublishedViewContentList, "contentId", ViewContentModel.class);
		this.historyViewContentDao.addBatch(toAddHistoryContentList, HistoryViewContentModel.class);
		
		
		/*
		 * 把需要收到消息的用户信息查询出来
		 */
		List<UserInfoModel> userList = this.userService.queryValidUserListByCrewId(crewId);
		
		List<String> iosUserTokenList = new ArrayList<String>();
		List<String> androidTokenList = new ArrayList<String>();
		List<MessageInfoModel> toAddMessageList = new ArrayList<MessageInfoModel>();
		for (UserInfoModel userInfo : userList) {
			if (userInfo.getClientType() != null) {
				int clientType = userInfo.getClientType();
				String token = userInfo.getToken();
				
				if (clientType == UserClientType.IOS.getValue() && !StringUtils.isBlank(token)) {
					iosUserTokenList.add(token);
				}
				if (clientType == UserClientType.Android.getValue() && !StringUtils.isBlank(token)) {
					androidTokenList.add(token);
				}
			}
			
			MessageInfoModel messageInfo = new MessageInfoModel();
			messageInfo.setId(UUIDUtils.getId());
			messageInfo.setCrewId(crewId);
			messageInfo.setSenderId(userId);
			messageInfo.setReceiverId(userInfo.getUserId());
			messageInfo.setType(MessageType.ScenarioEdit.getValue());
			messageInfo.setBuzId(null);
			messageInfo.setStatus(MessageInfoStatus.UnRead.getValue());
			messageInfo.setTitle(title);
			messageInfo.setContent(content);//fbUserInfo.getRealName() + "反馈：" + feedBackModel.getMessage()
			messageInfo.setRemindTime(new Date());
			messageInfo.setCreateTime(new Date());
			toAddMessageList.add(messageInfo);
		}
		this.messageInfoService.addMany(toAddMessageList);
		
		//把剧本下载记录删除
		this.downloadScenarioRecordService.deleteByCrewId(crewId);
		
		/*
		 * 推送消息
		 */
		CrewInfoModel crewInfo = this.crewInfoService.queryById(crewId);
		content = "《" + crewInfo.getCrewName() + "》" + content;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", MessageType.ScenarioEdit.getValue());
		map.put("time", sdf.format(new Date()));
		map.put("crewId", crewId);
		map.put("crewName", crewInfo.getCrewName());
		
		//IOS推送
		IOSPushMsg msg = new IOSPushMsg();
		msg.setTokenList(iosUserTokenList);
		msg.setAlert(content);
		msg.setCustomDictionaryMap(map);
		
		this.umengIOSPushService.iOSPushMsg(msg);
		
		//安卓推送
		AndroidPushMsg androidMsg = new AndroidPushMsg();
		androidMsg.setTokenList(androidTokenList);
		androidMsg.setTicker(content);
		androidMsg.setTitle(title);
		androidMsg.setText(content);
		androidMsg.setCustomDictionaryMap(map);
		this.umengAndroidPushService.androidPushMsg(androidMsg);
	}
	
	/**
	 * 自动分析演员信息
	 * 规则：>100 or >40%  主演
	 * >50 or >10%  特约
	 * 其他，群演
	 * @param sceDtoList
	 * @return
	 */
	public List<ScenarioViewDto> analyseViewRoleInfo (List<ScenarioViewDto> sceDtoList) {
		List<ScenarioViewDto> scenarioDtoList = sceDtoList;
		
		//取出分析出的所有的角色，把值放到Map中，键为角色名称，值为角色戏量
		Map<String, Integer> viewRoleCountMap = new HashMap<String, Integer>();
		for (ScenarioViewDto scenarioViewDto : scenarioDtoList) {
			List<String> majorRoleNameList = scenarioViewDto.getMajorRoleNameList();
			
			if (majorRoleNameList != null && majorRoleNameList.size() > 0) {
				for (String majorRoleName : majorRoleNameList) {
					if (StringUtils.isBlank(majorRoleName)) {
						continue;
					}
					if (!viewRoleCountMap.containsKey(majorRoleName)) {
						viewRoleCountMap.put(majorRoleName, 1);
					} else {
						viewRoleCountMap.put(majorRoleName, viewRoleCountMap.get(majorRoleName) + 1);
					}
				}
			}
		}
		
		
		//根据规则把所有的角色分为主要演员、特约演员和群众演员
		List<String> majorRoleList = new ArrayList<String>();
		List<String> guestRoleList = new ArrayList<String>();
		List<String> massRoleList = new ArrayList<String>();
		
		Set<String> viewRoleKeySet = viewRoleCountMap.keySet();
		for (String roleName : viewRoleKeySet) {
			int viewCount = viewRoleCountMap.get(roleName);
			if (viewCount >= 100 || BigDecimalUtil.divide(viewCount, scenarioDtoList.size()) >= 0.4) {
				majorRoleList.add(roleName);
			} else if (viewCount >= 50 || BigDecimalUtil.divide(viewCount, scenarioDtoList.size()) >= 0.1) {
				guestRoleList.add(roleName);
			} else {
				massRoleList.add(roleName);
			}
		}
		
		
		//把场景中的角色和上面的主要演员和群众演员对比，过滤出对应角色类型的角色
		for (ScenarioViewDto scenarioViewDto : scenarioDtoList) {
			List<String> majorRoleNameList = scenarioViewDto.getMajorRoleNameList();
			
			List<String> myMajorRoleList = new ArrayList<String>();
			List<String> myGuestRoleList = new ArrayList<String>();
			List<String> myMassRoleList = new ArrayList<String>();
			
			if (majorRoleNameList != null && majorRoleNameList.size() > 0) {
				for (String roleName : majorRoleNameList) {
					if (StringUtils.isBlank(roleName)) {
						continue;
					}
					if (majorRoleList.contains(roleName)) {
						myMajorRoleList.add(roleName);
					}
					if (guestRoleList.contains(roleName)) {
						myGuestRoleList.add(roleName);
					}
					if (massRoleList.contains(roleName)) {
						myMassRoleList.add(roleName);
					}
				}
			}
			
			scenarioViewDto.setMajorRoleNameList(myMajorRoleList);
			scenarioViewDto.setGuestRoleNameList(myGuestRoleList);
			scenarioViewDto.setMassRoleNameList(myMassRoleList);
		}
		
		return scenarioDtoList;
	}
}
