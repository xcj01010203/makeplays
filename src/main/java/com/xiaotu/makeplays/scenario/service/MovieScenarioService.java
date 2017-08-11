package com.xiaotu.makeplays.scenario.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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

import com.xiaotu.makeplays.scenario.controller.dto.ScenarioViewDto;
import com.xiaotu.makeplays.scenario.dao.ScripteleInfoDao;
import com.xiaotu.makeplays.scenario.dao.SeparatorInfoDao;
import com.xiaotu.makeplays.scenario.model.ScripteleInfoModel;
import com.xiaotu.makeplays.scenario.model.SeparatorInfoModel;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.OfficeUtils;
import com.xiaotu.makeplays.utils.RegexUtils;
import com.xiaotu.makeplays.view.model.ViewInfoModel;
import com.xiaotu.makeplays.view.model.constants.ShootStatus;
import com.xiaotu.makeplays.view.service.ViewInfoService;

/**
 * 电影剧本专用类
 * @author xuchangjian 2016-3-8下午4:32:31
 */
@Service
public class MovieScenarioService {
	/**
	 * 行分隔符
	 */
	private final String lineSeprator = "\r\n";
	
	//带有“回忆”、“闪回”字段的标题，需要把标题中对应的内容去掉，再把其放到内容中
	private static List<String> lineSeqWordList = new ArrayList<String>();	//标题中需要做替换且变成换行内容的元素
	
	@Autowired
	private ViewInfoService viewService;
	
	@Autowired
	private ScripteleInfoDao scripteleInfoDao;
	
	@Autowired
	private SeparatorInfoDao separatorInfoDao;
	
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
	 * 过长的场次标题，此类数据实际上为内容
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
	 * 解析电影剧本
	 * @param crewId
	 * @param storePath
	 * @param lineCount
	 * @param wordCount
	 * @param scenarioFormat
	 * @param hasSeriesNoTag
	 * @param extralSeriesNo
	 * @param groupSeriesNoFlag
	 * @return
	 * @throws IOException 
	 * @throws OpenXML4JException 
	 * @throws XmlException 
	 */
	public Map<String, Object> analyseScnario(String crewId, String storePath, 
			int lineCount, int wordCount, 
			String scenarioFormat, boolean hasSeriesNoTag, 
			String extralSeriesNo, boolean groupSeriesNoFlag, 
			boolean supportCNViewNo, boolean pageIncludeTitle) 
					throws IOException, XmlException, OpenXML4JException {
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
		 * 
		 * 3、当前上传剧本所包含的场次已存在于场景表中，需要进行“跳过/替换”处理
		 * 4、已存在于场景表中但当前所上传的剧本文本中不存在的场次，需要进行“保留/不保留”处理
		 */
		
		List<String> contentList = this.getSceContent(fileContent);
		
		//校验场次是否符合规范
		this.checkViewNoValid(contentList, formatInfo, originalScenarioFormatList, scripteleInfoList, separatorInfoList, supportCNViewNo);
		
		//校验标题格式
		this.checkSceTitleIllegal(contentList, formatInfo, originalScenarioFormatList, scripteleInfoList, separatorInfoList, supportCNViewNo);
		
		//检查是否有重复的场
		this.checkSceHashSameView(contentList, formatInfo, 
				originalScenarioFormatList, scripteleInfoList, 
				separatorInfoList, supportCNViewNo);
		
		//解析剧本中每场的标题和内容详细信息
		List<ScenarioViewDto> SceDtoList = this.genSceDetailInfo(contentList, lineCount, 
				wordCount, formatInfo, 
				originalScenarioFormatList, scripteleInfoList, 
				separatorInfoList, supportCNViewNo, pageIncludeTitle);
		
		//把剧本中的场次信息和数据库中的信息对比(3,4两点)
		Map<String, Object> viewDataMap = this.compareViewWithDataInDB(crewId, SceDtoList);
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
	 * 校验场景号是否符合规范
	 * 无效的条件有：
	 * 1、场次中汉字超过3个，比如：10排的战士们
	 * 2、带有汉字的场次没有对应的不带汉字的场次，比如：有场次“10排中”却没有场次“10”，则判定“10排中”场次所在的标题不是标题
	 * 3、数字值不能大于400
	 */
	private void checkViewNoValid(List<String> contentList, Map<String, Object> formatInfo, List<String> scenarioFormatList,
			List<ScripteleInfoModel> scripteleInfoList, List<SeparatorInfoModel> separatorInfoList, boolean supportCNViewNo) {
		
		String separateSceripRegex = (String) formatInfo.get("separateSceripRegex");	//分割标题中元素的正则表达式
		int lineSeperatorCount = (Integer) formatInfo.get("lineSeperatorCount");	//自定义格式中换行符的个数
		String seriesViewNoRegex = (String) formatInfo.get("seriesViewNoRegex");	//只包含集场号的正则表达式
		
		String scenarioFormatStr = "";
		for (String format : scenarioFormatList) {
			scenarioFormatStr += format;
		}
		
		Map<String, List<String>> viewNoContentMap = new HashMap<String, List<String>>();	//集场对应的标题Map，key为集“场次”，value为该场的标题
		List<String> containCHViewNo = new ArrayList<String>();	//场次中含有中文的场次
		
		if (contentList != null && contentList.size() > 0) {
			/*
			 * 考虑到自定义格式中有换行符,利用这两个变量对行进行打包分析
			 * 例如有一个换行符，则对剧本的分析时按照每两行一个单位进行解析
			 */
			int groupLineCount = 0;	//表示当前的数据是第几行
			String groupLineValue = "";	//存储包括当前行，往上lineSeperatorCount+1行的数据

			for (String myLineContent : contentList) {
				
				if (StringUtils.isBlank(myLineContent) || myLineContent.trim().equals("")) { // 空行跳过
					continue;
				}
				
				myLineContent = myLineContent.trim();
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
					String[] titleArray = dealedGroupLineContent.split(separateSceripRegex);

					List<String> titleElementList = new ArrayList<String>();
					for (String str : titleArray) {
						if (StringUtils.isBlank(str)) {
							continue;
						}
						titleElementList.add(str);
					}

					Map<String, Object> elementMap = this.genTitleElement(dealedGroupLineContent, scenarioFormatStr, 
							scenarioFormatList, titleElementList, 
							scripteleInfoList, separatorInfoList, supportCNViewNo);
					boolean eleValidTitle = (Boolean) elementMap.get("validTitle");
					
					if (eleValidTitle) {
						String viewNo = (String) elementMap.get("viewNo"); // 场次
						
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
							
							int numViewNo = genViewNoNumber(viewNo);
							if (numViewNo > 400) {
								isTooBigViewNo = true;
							}
							
							if (count > 0 && !isTooBigViewNo && !isTooLongViewNo && !MovieScenarioService.lineSeqWordList.contains(zhTitle)) {
								containCHViewNo.add(viewNo);
							}
						}
						
						//场次过长
						if ((isTooBigViewNo || isTooLongViewNo) && !this.inValidViewNoList.contains(dealedGroupLineContent)) {
							this.inValidViewNoList.add(dealedGroupLineContent);
						} else {
							if (viewNoContentMap.containsKey(viewNo)) {
								List<String> titleList = viewNoContentMap.get(viewNo);
								titleList.add(dealedGroupLineContent);
								
								viewNoContentMap.put(viewNo, titleList);
							} else {
								List<String> titleList = new ArrayList<String>();
								titleList.add(dealedGroupLineContent);
								viewNoContentMap.put(viewNo, titleList);
							}
						}
					}
				}
			}
			
			//校验所有场次含有中文的场次是否存在对应的不含有中文的场次
			//比如，如果存在“1-317部队”这样的场次，那就检查整个剧本中是否存在“1-317”场，如果没有，则判定该场所在的标题为内容
			for (String seriesViewNo : containCHViewNo) {
				String removeCHSeriesViewNo = seriesViewNo.replaceAll(Constants.REGEX_CHINESE_WORD, "");
				if (!viewNoContentMap.containsKey(removeCHSeriesViewNo)) {
					List<String> titleList = viewNoContentMap.get(seriesViewNo);
					for (String inValidViewNoTitle : titleList) {
						this.inValidViewNoList.add(inValidViewNoTitle);
					}
				}
			}
		}
	
	}
	
	/**
	 * 获取场次中的数字
	 * “23扉页” -> “23”
	 * “23num” -> "23"
	 * "23扉页1册" -> "23"
	 * @param viewNo
	 * @return
	 */
	public static int genViewNoNumber(String viewNo) {
		String[] viewNoArray = viewNo.trim().split("");
		int firstNotNumIndex = viewNoArray.length;	//第一个非数字的字符下标
		for (int i = 1; i < viewNoArray.length; i++) {
			String viewNoEle = viewNoArray[i];
			if (!RegexUtils.regexFind("\\d", viewNoEle) && firstNotNumIndex == viewNoArray.length) {
				firstNotNumIndex = i;
			}
		}
		
		int numViewNo = Integer.parseInt(viewNo.substring(0, firstNotNumIndex - 1));
		return numViewNo;
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
		
		//全角转半角
		String dealedGroupLineContent = com.xiaotu.makeplays.utils.StringUtils.ToDBC(str);
		//英文替换为中文
		dealedGroupLineContent = com.xiaotu.makeplays.utils.StringUtils.EnToCHSeparator(dealedGroupLineContent);
		
		return dealedGroupLineContent;
	}
	
	/**
	 * 检查电影剧本的格式是否合法,
	 * 主要检查剧本中是否存在符合剧本模板中标题格式的文本，如果存在，就合法
	 * @param contentList
	 * @param formatInfo
	 * @param scenarioFormatList
	 * @param scripteleInfoList
	 * @param separatorInfoList
	 * @param groupSeriesNoFlag
	 */
	private void checkSceTitleIllegal(List<String> contentList, 
			Map<String, Object> formatInfo, 
			List<String> scenarioFormatList,
			List<ScripteleInfoModel> scripteleInfoList, 
			List<SeparatorInfoModel> separatorInfoList, boolean supportCNViewNo) {
		
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
				
				if (StringUtils.isBlank(myLineContent) || myLineContent.trim().equals("")) { // 空行跳过
					continue;
				}
				
				/*
				 * 跳过第n集的行
				 */
				if (RegexUtils.regexFind(Constants.REGEX_SERIES, myLineContent.trim()) && myLineContent.trim().length() < 15) {
					continue;
				}
				
				myLineContent = myLineContent.trim();
				//此处""不是空格，而是一个特殊字符，该字符会在自动编号的剧本中出现
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
				
//				if (supportCNViewNo && dealedGroupLineContent.split(separateSceripRegex).length > 0) {
//					//电影的场次第一个元素肯定为“场”，此处识别第一个元素是否为“三十一”这种中文数字
//					String[] titleArray = dealedGroupLineContent.split(separateSceripRegex);
//					for (int i = 0; i < titleArray.length; i++) {
//						if (!StringUtils.isBlank(titleArray[i])) {
//							String cNViewNo = this.genCNViewNo(titleArray[i]);
//							if (!StringUtils.isBlank(cNViewNo)) {
//								titleFlag = true;
//							}
//							break;
//						}
//					}
//				}
				
				
				if ((isTitle || titleFlag) && !this.inValidViewNoList.contains(dealedGroupLineContent)) {
					//只有存在标准匹配标题的时候才认定剧本格式配置正确
					if (titleFlag) {
						hasTitle = true;
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
							scenarioFormatList, titleElementList, 
							scripteleInfoList, separatorInfoList, supportCNViewNo);
					boolean eleValidTitle = (Boolean) elementMap.get("validTitle");
					
					if (eleValidTitle) {
						String viewNo = (String) elementMap.get("viewNo"); // 场次
						// 集数-场景编号
						if (StringUtils.isBlank(viewNo)) {
							this.titleErrorMsg.add("标题《" + groupLineValue + "》没有填写场次信息，请修改后上传");
							errorTitleList.add(dealedGroupLineContent);
						}
						
						if (elementMap.get("site") != null && elementMap.get("site") != "") {
							String site = (String) elementMap.get("site");
							if (site.length() > 10) {
								this.titleErrorMsg.add("标题《" + groupLineValue + "》中内外景字段过长，请检查是否缺少标点符号");
								errorTitleList.add(dealedGroupLineContent);
							}
						}
						
						if (!titleFlag && isTitle) {
							Map<String, String> notFullMatchData = new HashMap<String, String>();
							notFullMatchData.put("viewNo", viewNo);
							
							boolean contained = false;
							for (Map<String, String> map : notFullMatchingInfo) {
								String mapViewNo = map.get("viewNo");
								
								if (com.xiaotu.makeplays.utils.StringUtils.equalStr(mapViewNo, viewNo)) {
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
					}
				}
			}
		}
		if (!hasTitle) {
			throw new IllegalArgumentException("剧本中没有指定格式的标题，请修改后重新上传");
		}
	}
	
	/**
	 * 校验电影剧本中是否有重复的场次
	 * @param contentList	剧本中每行内容列表
	 * @param formatInfo	剧本格式信息
	 * @param scenarioFormatList	自定义格式中包含的单个原子列表
	 * @param scripteleInfoList	系统中所有剧本元素信息
	 * @param separatorInfoList	系统中所有剧本分隔符信息
	 * @param supportCNViewNo
	 */
	public void checkSceHashSameView(List<String> contentList, Map<String, Object> formatInfo, 
			List<String> scenarioFormatList, List<ScripteleInfoModel> scripteleInfoList, 
			List<SeparatorInfoModel> separatorInfoList,  boolean supportCNViewNo) {
		
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
		List<String> viewNoList = new ArrayList<String>();
		
		// 逐行解析
		for (String myLineContent : contentList) {
			// 跳过第n集的行
			if (StringUtils.isBlank(myLineContent)) {
				continue;
			}
			if (RegexUtils.regexFind(Constants.REGEX_SERIES, myLineContent.trim()) && myLineContent.trim().length() < 15) {
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
			
//			if (supportCNViewNo && dealedGroupLineContent.split(separateSceripRegex).length > 0) {
//				//电影的场次第一个元素肯定为“场”，此处识别第一个元素是否为“三十一”这种中文数字
//				String[] titleArray = dealedGroupLineContent.split(separateSceripRegex);
//				for (int i = 0; i < titleArray.length; i++) {
//					if (!StringUtils.isBlank(titleArray[i])) {
//						String cNViewNo = this.genCNViewNo(titleArray[i]);
//						if (!StringUtils.isBlank(cNViewNo)) {
//							titleFlag = true;
//						}
//						break;
//					}
//				}
//			}
			
			// && groupLineContent.length() < 50
			if ((titleFlag || isTitle) && !this.inValidViewNoList.contains(dealedGroupLineContent)) {
				String title = dealedGroupLineContent;
				
				String[] titleArray = title.split(separateSceripRegex);

				List<String> titleElementList = new ArrayList<String>();
				for (String str : titleArray) { 
					if (StringUtils.isBlank(str)) {
						continue;
					}
					titleElementList.add(str);
				}
				Map<String, Object> elementMap = this.genTitleElement(dealedGroupLineContent, scenarioFormatStr, 
						scenarioFormatList, titleElementList, 
						scripteleInfoList, separatorInfoList, supportCNViewNo);
				boolean validTitle = (Boolean) elementMap.get("validTitle");
				
				if (validTitle) {
					String viewNo = (String) elementMap.get("viewNo");	//场次\
					
					if (viewNoList.contains(viewNo.trim().toLowerCase())) {
						String errorMsg = viewNo + "场在剧本中重复，请修改后重新上传。";
						if (!this.titleErrorMsg.contains(errorMsg)) {
							this.titleErrorMsg.add(viewNo + "场在剧本中重复，请修改后重新上传。");
						}
						
						errorTitleList.add(dealedGroupLineContent);
					} else {
						viewNoList.add(viewNo.trim().toLowerCase());
					}
				}
			}
		}
	}
	
	/**
	 * 提取电影剧本详细信息
	 * @param contentList	剧本中每行内容列表
	 * @param lineCount	没页行数
	 * @param wordCount	没行字数
	 * @param formatInfo	剧本格式信息
	 * @param scenarioFormatList	剧本格式中单个原子列表
	 * @param scripteleInfoList	系统中所有剧本元素列表
	 * @param separatorInfoList	系统中所有剧本分隔符列表
	 * @param supportCNViewNo	是否支持中文数字的场次提取
	 * @return
	 */
	private List<ScenarioViewDto> genSceDetailInfo(List<String> contentList, int lineCount, int wordCount, 
			Map<String, Object> formatInfo, 
			List<String> scenarioFormatList, 
			List<ScripteleInfoModel> scripteleInfoList, 
			List<SeparatorInfoModel> separatorInfoList, 
			boolean supportCNViewNo, boolean pageIncludeTitle) {
		
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
//		int viewLineCount = 0; 	//标识一场一共有多少行
		
		/*
		 * 考虑到自定义格式中有换行符,利用这两个变量对行进行打包分析
		 * 例如有一个换行符，则对剧本的分析时按照每两行一个单位进行解析
		 */
		int groupLineCount = 0;	//表示当前的数据是第几行
		String groupLineValue = "";	//存储包括当前行，往上lineSeperatorCount+1行的数据
		boolean hasMainTitle = false;	//引入它是为了避免自动编号问题导致的系统能识别的第一场标题之前出现子标题，标题引入过多问题，该变量可以过滤第一场标题的前的所有内容
		
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
			
//			String groupLineContent = groupLineValue.toString().trim();
//			
//			//此处""不是空格，而是一个特殊字符，该字符会在自动编号的剧本中出现
//			if (groupLineContent.substring(0, 1).equals("﻿")) {
//				groupLineContent = groupLineContent.substring(1, groupLineContent.length());
//			}
//			
//			//兼容自动编号中的符号自动替换问题
//			String dealedGroupLineContent = groupLineContent.replaceAll("-", ".");
//			dealedGroupLineContent = dealedGroupLineContent.replaceAll("—", ".");
//
//			//英文替换为中文
//			dealedGroupLineContent = com.xiaotu.makeplays.utils.StringUtils.EnToCHSeparator(dealedGroupLineContent);
			
			String dealedGroupLineContent = this.dealSpecialChar(groupLineValue);
			
			// 跳过第n集的行
			if (RegexUtils.regexFind(Constants.REGEX_SERIES, myLineContent.trim())) {
				if(myLineContent.trim().length() < 15) {
					continue;
				}
			}
			
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
			
//			if (supportCNViewNo && dealedGroupLineContent.split(separateSceripRegex).length > 0) {
//				//电影的场次第一个元素肯定为“场”，此处识别第一个元素是否为“三十一”这种中文数字
//				String[] titleArray = dealedGroupLineContent.split(separateSceripRegex);
//				for (int i = 0; i < titleArray.length; i++) {
//					if (!StringUtils.isBlank(titleArray[i])) {
//						String cNViewNo = this.genCNViewNo(titleArray[i]);
//						if (!StringUtils.isBlank(cNViewNo)) {
//							String cNTitleRegex = mainTitleRegex.replace("^\\d", Constants.REGEX_CN_NUMBER_START);
//							if (RegexUtils.regexFind(cNTitleRegex, mainTitleRegex)) {
//								standardTitleFlag = true;
//							} else {
//								isTitle = true;
//							}
//						}
//						break;
//					}
//				}
//			}
			
			//如果是标题  && dealedGroupLineContent.length() < 50
			if ((standardTitleFlag || isTitle) && !this.inValidViewNoList.contains(dealedGroupLineContent)) {
				hasMainTitle = true;
				
				titleNo++;
				
				/************************保存上一场信息*********************************/
				if (titleNo > 1 && viewTitle.length() > 0) {
					this.genScenarioDto(viewTitle, viewContent, groupLineValue, 
							myLineContent, wordCount, lineCount, 
							scenarioFormatStr, scenarioFormatList, scripteleInfoList, 
							separatorInfoList, separateSceripRegex, supportCNViewNo, 
							minCount, scenarioList, pageIncludeTitle);
					
					viewTitle = new StringBuilder();
					viewContent = new StringBuilder(Constants.CONTENT_BUFFER_SIZE);
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
		if (!StringUtils.isBlank(viewTitle.toString())) {	
			this.genScenarioDto(viewTitle, viewContent, "", "", wordCount, 
					lineCount, scenarioFormatStr, scenarioFormatList, 
					scripteleInfoList, separatorInfoList, separateSceripRegex, 
					supportCNViewNo, minCount, scenarioList, pageIncludeTitle);
			
//			String dealedTitle = this.dealSpecialChar(viewTitle.toString());
//			
//			if (!errorTitleList.contains(dealedTitle)) {
//				String[] titleArray = dealedTitle.split(separateSceripRegex);
//
//				List<String> titleElementList = new ArrayList<String>();
//				for (String str : titleArray) {
//					if (StringUtils.isBlank(str)) {
//						continue;
//					}
//					titleElementList.add(str);
//				}
//				Map<String, Object> elementMap = this.genTitleElement(dealedTitle, scenarioFormatStr, scenarioFormatList, titleElementList, scripteleInfoList, separatorInfoList, supportCNViewNo);
//				
//				
//				ScenarioViewDto scenarioDto = new ScenarioViewDto();
//				this.fetchSceTitleDetail(viewTitle.toString(), scenarioDto, minCount, separateSceripRegex, elementMap);	//获取标题中的元素并存储到dto对象中
//				
//				//判断是否是标题，如果不是，保存标题时，只保存取到的第一行
//				boolean isStandardTitle = (Boolean) elementMap.get("isStandardTitle");
//				boolean isSeiresViewNoTitle = (Boolean) elementMap.get("isTitle");
//				boolean isNoFigure = (Boolean) elementMap.get("isNoFigure");
//				int figureLineNum = (Integer) elementMap.get("figureLineNum");
//				boolean eleValidTitle = (Boolean) elementMap.get("validTitle");
//				
//				String[] viewTitleArr = viewTitle.toString().split(this.lineSeprator);
//				int size = viewTitleArr.length;
//				StringBuilder toSaveTitle = new StringBuilder();
//				if (!isStandardTitle && !isNoFigure && isSeiresViewNoTitle) {
//					for (int i = size - 1; i >= 0; i--) {
//						if (i < 1) {
//							toSaveTitle.append(viewTitleArr[0]);
//						} else {
//							viewContent.insert(0, viewTitleArr[i] + this.lineSeprator);
//						}
//					}
//				}
//				if (!isStandardTitle && isNoFigure && viewTitleArr.length - figureLineNum > 0) {
//					for (int i = size - 1; i >= 0; i--) {
//						if (i < viewTitleArr.length - figureLineNum) {
//							if (i == viewTitleArr.length - figureLineNum - 1) {
//								toSaveTitle.insert(0, viewTitleArr[i]);
//							} else {
//								toSaveTitle.insert(0, viewTitleArr[i] + this.lineSeprator);
//							}
//						} else {
//							viewContent.insert(0, viewTitleArr[i] + this.lineSeprator);
//						}
//					}
//				} else {
//					toSaveTitle = new StringBuilder(viewTitle.toString());
//				}
//				scenarioDto.setTitle(toSaveTitle.toString());
//				
//				String viewContentStr = viewContent.toString();
//				scenarioDto.setContent(viewContentStr.replaceAll(" |\t", ""));
//				
//				int viewLineCount = this.calculateLineCount(toSaveTitle.toString() + this.lineSeprator + viewContentStr, wordCount, true);
//				double pageCount = com.xiaotu.makeplays.utils.StringUtils.div(viewLineCount, lineCount, 2);
//				scenarioDto.setPageCount(pageCount);
//				
//				if (eleValidTitle) {
//					scenarioList.add(scenarioDto);
//				}
//			}
			
			viewTitle = new StringBuilder();
			viewContent = new StringBuilder(Constants.CONTENT_BUFFER_SIZE);
		}
		
		return scenarioList;
	}
	
	/**
	 * 生成剧本信息
	 * @param viewTitle
	 * @param viewContent
	 * @param groupLineContent
	 * @param myLineContent
	 * @param wordCount
	 * @param lineCount
	 * @param scenarioFormatStr
	 * @param scenarioFormatList
	 * @param scripteleInfoList
	 * @param separatorInfoList
	 * @param separateSceripRegex
	 * @param supportCNViewNo
	 * @param minCount
	 * @param scenarioList
	 */
	private void genScenarioDto(StringBuilder viewTitle, StringBuilder viewContent, 
			String groupLineContent, String myLineContent,
			int wordCount, int lineCount,
			String scenarioFormatStr, List<String> scenarioFormatList, 
			List<ScripteleInfoModel> scripteleInfoList, List<SeparatorInfoModel> separatorInfoList, 
			String separateSceripRegex, boolean supportCNViewNo, 
			int minCount, List<ScenarioViewDto> scenarioList, boolean pageIncludeTitle) {
		if (viewContent.length() > 5000) {
			throw new IllegalArgumentException("《" + viewTitle + "》场内容过长，请检查剧本格式");
		}

		ScenarioViewDto scenarioViewDto = new ScenarioViewDto();
		
		//拼接用户替换的正则表达式
		StringBuilder linSepWordRegex = new StringBuilder("(");
		for (String lineSepWord : MovieScenarioService.lineSeqWordList) {
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
		for (String lineSepWord : MovieScenarioService.lineSeqWordList) {
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
			
			
			Map<String, Object> elementMap = this.genTitleElement(dealedTitle, scenarioFormatStr, scenarioFormatList, titleElementList, scripteleInfoList, separatorInfoList, supportCNViewNo);
			this.fetchSceTitleDetail(viewTitle.toString(), scenarioViewDto, minCount, separateSceripRegex, elementMap);	//获取标题中的元素并存储到dto对象中
			
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
	 * 和数据库中的电影数据进行对比
	 * @param crewId
	 * @param scenarioDtoList
	 * @return
	 */
	private Map<String, Object> compareViewWithDataInDB(String crewId, List<ScenarioViewDto> scenarioDtoList) {
		Map<String, Object> viewDataMap = new HashMap<String, Object>(); 
		
		if (scenarioDtoList == null || scenarioDtoList.size() == 0) {
			return viewDataMap;
		}
		
		int minSeriesNo = 1;
		List<ViewInfoModel> viewInfoList = this.viewService.queryByCrewId(crewId,null);
		
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
					
					//当前上传剧本所包含的场次已存在于场景表中
					if (scenarioViewDto.getViewNo().toLowerCase().trim().equals(viewInfo.getViewNo().toLowerCase().trim())) {
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
					if (scenarioViewDto.getViewNo().toLowerCase().trim().equals(viewInfo.getViewNo().toLowerCase().trim())) {
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
	 * 获取电影剧本标题中的元素
	 * @param titleStr
	 * @param scenarioFormatStr
	 * @param scenarioFormatList
	 * @param titleElementList
	 * @param scripteleInfoList
	 * @param separatorInfoList
	 * @param supportCNViewNo 是否支持中文场次
	 * @return
	 */
	public Map<String, Object> genTitleElement(String titleStr, String scenarioFormatStr, 
			List<String> scenarioFormatList, List<String> titleElementList, 
			List<ScripteleInfoModel> scripteleInfoList, List<SeparatorInfoModel> separatorInfoList, boolean supportCNViewNo) {
		Map<String, Object> elemenetMap = new HashMap<String, Object>();
		
		String seriesNo = "", 
				viewNo = "", 
				viewLocation = "", 
				atmosphere = null, 
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
//				int fgSepatorIndex = titleStr.indexOf(beforeFigureSepetorName);
				scenarioFormatList = noFigureFormatList;
//				titleStr = titleStr.substring(0, fgSepatorIndex);
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
			//throw new IllegalArgumentException("标题《" + titleStr + "》元素不足，请检查是否缺少分隔符");
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
		
//		if (!standardTitleFlag && isTitle) {
//			Map<String, String> notFullMatchData = new HashMap<String, String>();
//			notFullMatchData.put("viewNo", viewNo);
//			
//			boolean contained = false;
//			for (Map<String, String> map : notFullMatchingInfo) {
//				String mapViewNo = map.get("viewNo");
//				
//				if (com.xiaotu.makeplays.utils.StringUtils.equalStr(mapViewNo, viewNo)) {
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
	 * 校验场次是否符合标准
	 * 里面的汉子个数不能多余3个
	 * @param viewNo
	 * @return
	 */
//	private boolean checkViewNoValid(String viewNo) {
//		boolean result = true;
//		
//		if (!StringUtils.isBlank(viewNo)) {
//			String[] viewNoArray = viewNo.split("");
//			
//			int count = 0;
//			for (String viewNoEle : viewNoArray) {
//				if (RegexUtils.regexFind(Constants.REGEX_CHINESE_WORD, viewNoEle)) {
//					count ++;
//				}
//			}
//			
//			if (count > 3) {
//				result = false;
//			}
//		}
//		return result;
//	}
	
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
	 * 解析每场标题中的详细信息
	 * @param viewTitle
	 */
	private void fetchSceTitleDetail (String viewTitle, ScenarioViewDto scenarioDto, 
			int minCount, String separateSceripRegex, 
			Map<String, Object> elementMap) {
		int seriesNo = 1;	//集次
		String viewNo = "";	//场次
		String atmosphere = "";	//气氛
		String site = "";	//内外景
		List<String> addrList = new LinkedList<String>();// 场景地点列表
		List<String> roleNameList = new ArrayList<String>();
		boolean eleValidTitle = (Boolean) elementMap.get("validTitle");
		
		if (eleValidTitle && !StringUtils.isBlank(viewTitle)) {
			if(!StringUtils.isBlank(viewTitle)) {
				
				viewNo = (String) elementMap.get("viewNo");	//场次
				
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
				scenarioDto.setMajorRoleNameList(roleNameList);
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
	 * 计算文本的实际行数
	 * 		（1）如果某行的字数大于wordCount指定的字数，需要根据该行的字数/wordCount计算该行文本的实际行数
	 * 		（2）不足一行的按一行计算，即计算的行数存在小数，取大于该小数的最小整数
	 * @param content
	 * @param wordCount 设置的一行文本的字数
	 * @param isCalculateNullLine 是否计算空行
	 * @return
	 */
	private int calculateLineCount(String content,int wordCount,boolean isCalculateNullLine) {

		int lineCount =0;
		
		if(wordCount<=0){
			return lineCount;
		}
		
		if(content!=null) {
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
	 * 自动分析演员信息
	 * 如果演员戏量小于或等于5%且小于10场，则判定该演员为群众演员
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
		
		
		//根据规则把所有的角色分为主要演员和群众演员
		List<String> majorRoleList = new ArrayList<String>();
		List<String> massRoleList = new ArrayList<String>();
		
		Set<String> viewRoleKeySet = viewRoleCountMap.keySet();
		for (String roleName : viewRoleKeySet) {
			int viewCount = viewRoleCountMap.get(roleName);
			if (BigDecimalUtil.divide(viewCount, scenarioDtoList.size()) <= 0.05 && viewCount < 10) {
				massRoleList.add(roleName);
			} else {
				majorRoleList.add(roleName);
			}
		}
		
		
		//把场景中的角色和上面的主要演员和群众演员对比，过滤出对应角色类型的角色
		for (ScenarioViewDto scenarioViewDto : scenarioDtoList) {
			List<String> majorRoleNameList = scenarioViewDto.getMajorRoleNameList();
			
			List<String> myMajorRoleList = new ArrayList<String>();
			List<String> myMassRoleList = new ArrayList<String>();
			
			if (majorRoleNameList != null && majorRoleNameList.size() > 0) {
				for (String roleName : majorRoleNameList) {
					if (StringUtils.isBlank(roleName)) {
						continue;
					}
					if (majorRoleList.contains(roleName)) {
						myMajorRoleList.add(roleName);
					}
					if (massRoleList.contains(roleName)) {
						myMassRoleList.add(roleName);
					}
				}
			}
			
			scenarioViewDto.setMajorRoleNameList(myMajorRoleList);
			scenarioViewDto.setMassRoleNameList(myMassRoleList);
		}
		
		return scenarioDtoList;
	}
}
