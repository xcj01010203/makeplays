package com.xiaotu.makeplays.mobile.server.scenario;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.mobile.server.common.BaseFacade;
import com.xiaotu.makeplays.scenario.model.DownloadScenarioRecordModel;
import com.xiaotu.makeplays.scenario.model.ScenarioInfoModel;
import com.xiaotu.makeplays.scenario.service.DownloadScenarioRecordService;
import com.xiaotu.makeplays.scenario.service.ScenarioService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.view.controller.dto.SeriesNoDto;
import com.xiaotu.makeplays.view.controller.dto.ViewNoDto;
import com.xiaotu.makeplays.view.model.ViewContentModel;
import com.xiaotu.makeplays.view.model.ViewInfoModel;
import com.xiaotu.makeplays.view.service.ViewContentService;
import com.xiaotu.makeplays.view.service.ViewInfoService;

/**
 * 手机端剧本相关接口
 * @author xuchangjian
 */
@Controller
@RequestMapping("/interface/scenarioFacade")
public class ScenarioFacade extends BaseFacade{
	
	Logger logger = LoggerFactory.getLogger(ScenarioFacade.class);
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	@Autowired
	private ViewContentService viewContentService;
	
	@Autowired
	private CrewInfoService crewInfoService;
	
	@Autowired
	private ScenarioService scenarioService;
	
	@Autowired
	private DownloadScenarioRecordService downLoadScenarioRecordService;

	/**
	 * 获取上一场、下一场剧本信息接口
	 * @param currViewId	当前剧本ID
	 * @param isPre	是否是获取上一场剧本信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainNextOrPreSceInfo")
	public Object obtainNextOrPreSceInfo(String crewId, String currViewId, boolean ispre) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			if (StringUtils.isBlank(crewId)) {
				throw new IllegalArgumentException("无效的剧组访问");
			}
			
			
			ViewInfoModel viewInfo = this.viewInfoService.queryOneByViewId(currViewId);
			if (viewInfo == null) {
				throw new IllegalArgumentException("当前场不存在");
			}
			int currSeriesNo = viewInfo.getSeriesNo();
			String currViewNo = viewInfo.getViewNo();
			
			//查找所有集次场次信息，用户加载页面上左侧的集次-场次树
			List<Map<String, Object>> seriesViewNos = this.viewInfoService.queryAllViewInfoWithContent(crewId, currSeriesNo);
			
			int aimSeriesNo = -1;
			String aimViewNo = "";
			List<Map<String, Object>> aimResultMap = null;
			
			
			List<String> viewList = new LinkedList<String>();
			for (Map<String, Object> viewInfoMap : seriesViewNos) {
				viewList.add((String) viewInfoMap.get("viewNo"));
			}
			
			Comparator<String> sort = com.xiaotu.makeplays.utils.StringUtils.sort();
			Collections.sort(viewList, sort);
			
			int currIndex = -1;	//当前场在所有场中的下标
			for (int i = 0, len = viewList.size(); i < len; i++) {
				String myViewNo = viewList.get(i);
				if (currViewNo.equals(myViewNo)) {
					currIndex = i;
					break;
				}
			}
			
			//查询所有集次信息
			List<Map<String, Object>> seriesNos = this.viewInfoService.querySeriesNoByCrewId(crewId);

			//取得当前集在所有集中的下标
			int currSeriesNoIndex = -1;
			for (int i = 0, len = seriesNos.size(); i < len; i++) {
				Map<String, Object> map = seriesNos.get(i);
				int mySeriesNo = (Integer) map.get("seriesNo");
				if (mySeriesNo == currSeriesNo) {
					currSeriesNoIndex = i;
					break;
				}
			}
			
			if (ispre) {
				//查询上一场剧本信息
				if (currIndex == 0) {
					if (currSeriesNoIndex == 0) {
						throw new IllegalArgumentException("已经是第一场了");
					}
					
					int preSeriesNo = (Integer) seriesNos.get(currSeriesNoIndex - 1).get("seriesNo");
					List<Map<String, Object>> preSeriesViewNos = this.viewInfoService.queryAllViewInfoWithContent(crewId, preSeriesNo);
					List<String> preSeriesViewList = new LinkedList<String>();
					for (Map<String, Object> viewInfoMap : preSeriesViewNos) {
						preSeriesViewList.add((String) viewInfoMap.get("viewNo"));
					}
					
					Collections.sort(preSeriesViewList, sort);
					
					aimSeriesNo = preSeriesNo;
					aimViewNo = preSeriesViewList.get(preSeriesViewList.size() - 1);
					aimResultMap = preSeriesViewNos;
				} else {
					aimViewNo = viewList.get(currIndex - 1);
					aimSeriesNo = currSeriesNo;
					aimResultMap = seriesViewNos;
				}
			} else {
				//查询下一场剧本信息
				if (currIndex == viewList.size()-1) {
					if (currSeriesNoIndex == seriesNos.size() - 1) {
						throw new IllegalArgumentException("已经是最后一场了");
					}
					
					int nextSeriesNo = (Integer) seriesNos.get(currSeriesNoIndex + 1).get("seriesNo");
					List<Map<String, Object>> nextSeriesViewNos = this.viewInfoService.queryAllViewInfoWithContent(crewId, nextSeriesNo);
					List<String> nextSeriesViewList = new LinkedList<String>();
					for (Map<String, Object> viewInfoMap : nextSeriesViewNos) {
						nextSeriesViewList.add((String) viewInfoMap.get("viewNo"));
					}
					
					Collections.sort(nextSeriesViewList, sort);
					
					aimSeriesNo = nextSeriesNo;
					aimViewNo = nextSeriesViewList.get(0);
					aimResultMap = nextSeriesViewNos;
				} else {
					aimViewNo = viewList.get(currIndex + 1);
					aimSeriesNo = currSeriesNo;
					aimResultMap = seriesViewNos;
				}
			}
			
			
			//取得需要的数据
			for (Map<String, Object> map : aimResultMap) {
				int seriesNo = (Integer) map.get("seriesNo");
				String viewNo = (String) map.get("viewNo");
				String viewId = (String) map.get("viewId");
				int isManualSave = (Integer) map.get("isManualSave");
				String title = map.get("title") == null ? "" : (String) map.get("title");
				String viewContent = map.get("content") == null ? "" : (String) map.get("content");
				
				if (seriesNo == aimSeriesNo && viewNo.equals(aimViewNo)) {
					resultMap.put("seriesNo", seriesNo);
					resultMap.put("viewNo", viewNo);
					resultMap.put("viewId", viewId);
					if (isManualSave == 1) {
						resultMap.put("isManualSave", true);
					} else {
						resultMap.put("isManualSave", false);
					}
					
					resultMap.put("viewContent", viewContent);
					resultMap.put("title", title);
					resultMap.put("seriesNo", seriesNo);
					
					break;
				}
			}
			
			
			
			
		} catch (IllegalArgumentException ie) {
			this.logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage());
		} catch(Exception e) {
			this.logger.error("未知异常，查询剧本信息失败", e);
			throw new IllegalArgumentException("未知异常，查询剧本信息失败");
		}
		
		return resultMap;
	}
	
	/**
	 * 获取某一场的场景内容
	 * @param viewId
	 * @return
	 */
	@RequestMapping("/obtainViewContent")
	@ResponseBody
	public Object obtainViewContent(String crewId, String viewId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			//校验数据
			if (StringUtils.isBlank(viewId)) {
				throw new IllegalArgumentException("未获取到场景信息");
			}
			
			ViewInfoModel viewInfo = this.viewInfoService.queryOneByViewId(viewId);
			if (viewInfo == null) {
				throw new IllegalArgumentException("该场信息不存在");
			}
			
			String title = "";
			String content = "";
			ViewContentModel viewContent = this.viewContentService.queryByViewId(viewId);
			if (viewContent != null) {
				title = viewContent.getTitle();
				content = viewContent.getContent();
			}

			resultMap.put("title", title);
			resultMap.put("viewContent", content);
		} catch (IllegalArgumentException ie) {
			String msg = ie.getMessage();
			logger.error(msg, ie);
			throw new IllegalArgumentException(msg);
			
		} catch (Exception e) {
			String msg = "未知异常，获取剧本内容失败";
			logger.error(msg, e);
			throw new IllegalArgumentException(msg);
		}
		
		return resultMap;
	}
	
	/**
	 * 下载剧组下所有剧本信息
	 * @param response
	 * @param crewId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/downloadScenarioInfo")
	public Object downloadScenarioInfo(HttpServletRequest request,
			HttpServletResponse response, String userId, String crewId,
			String clientUUID) {
		FileOutputStream out = null;
		FileInputStream in = null;
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			
			CrewInfoModel crewInfo = this.crewInfoService.queryById(crewId);

			//查找所有集次场次信息，用户加载页面上左侧的集次-场次树
			List<Map<String, Object>> seriesViewNos = this.viewInfoService.queryAllViewInfoWithContent(crewId, null);
			
			Map<Integer, List<String>> seriesViewNoMap = new TreeMap<Integer, List<String>>();	//存储集-场的对应的关系,key-集次  value-该集下所有场次
			List<Integer> seriesNoList = new ArrayList<Integer>();	//查出来的剧本信息中所有的集次
			for (Map<String, Object> viewInfo : seriesViewNos) {
				Integer seriesNo = (Integer) viewInfo.get("seriesNo");
				if (!seriesNoList.contains(seriesNo)) {
					seriesNoList.add(seriesNo);
				}
			}
			
			for (Integer seriesNo : seriesNoList) {
				List<String> viewList = new LinkedList<String>();
				for (Map<String, Object> viewInfo : seriesViewNos) {
					if (viewInfo.get("seriesNo") == seriesNo) {
						viewList.add((String) viewInfo.get("viewNo"));
					}
				}
				
				Comparator<String> sort = com.xiaotu.makeplays.utils.StringUtils.sort();
				Collections.sort(viewList, sort);
				
				seriesViewNoMap.put(seriesNo, viewList);
			}
			
			Set<Integer> keySet = seriesViewNoMap.keySet();
			Iterator<Integer> iter = keySet.iterator();
			
			List<SeriesNoDto> seriesNoDtoList = new ArrayList<SeriesNoDto>();
			while(iter.hasNext()) {
				Integer key = (Integer) iter.next();
				List<String> value = seriesViewNoMap.get(key);
				
				
				SeriesNoDto seriesNoDto = new SeriesNoDto();
				seriesNoDto.setSeriesNo(key);
				List<ViewNoDto> viewNoDtoList = new ArrayList<ViewNoDto>();
				for (String viewNo : value) {
					for (Map<String, Object> viewInfo : seriesViewNos) {
						Integer mySeriesNo = (Integer) viewInfo.get("seriesNo");
						String myViewNo = (String) viewInfo.get("viewNo");
						String title = (String) viewInfo.get("title");
						String content = (String) viewInfo.get("content");
						if (mySeriesNo == key && myViewNo.equals(viewNo)) {
							ViewNoDto viewNoDto = new ViewNoDto();
							viewNoDto.setViewNo(viewNo);
							viewNoDto.setTitle(title);
							viewNoDto.setViewContent(content);
							
							int isManualSave = (Integer) viewInfo.get("isManualSave");
							if (isManualSave == 1) {
								viewNoDto.setIsManualSave(true);
							} else {
								viewNoDto.setIsManualSave(false);
							}
							viewNoDto.setViewId((String) viewInfo.get("viewId"));
							viewNoDtoList.add(viewNoDto);
						}
					}
				}
				seriesNoDto.setViewNoDtoList(viewNoDtoList);
				seriesNoDtoList.add(seriesNoDto);
			}
			
			//更新剧本字段
			ScenarioInfoModel scenarioInfo = this.scenarioService.queryLastScenario(crewId);
			if (scenarioInfo != null) {
				DownloadScenarioRecordModel record = this.downLoadScenarioRecordService.queryByCrewIdAndClientUUID(crewId, clientUUID);
				if (record == null) {
					record = new DownloadScenarioRecordModel();
					record.setId(UUIDUtils.getId());
					record.setCrewId(crewId);
					record.setClientUUID(clientUUID);
					this.downLoadScenarioRecordService.addOne(record);
				}
			}
			
			
			//把结果存成json字符串存到文件中
			String resultStr = JSONArray.fromObject(seriesNoDtoList).toString();
			
			//获取存储根路径
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String baseDownloadPath = properties.getProperty("downloadPath");
			
			String fileName = "《" + crewInfo.getCrewName() + "》" + this.sdf1.format(new Date());	//生成下载的文件名
			String storePath = baseDownloadPath + "scenario/mobile/" + this.sdf2.format(new Date()) + "/";	//存储路径
			String suffix = ".txt";
			//创建文件
			File scenarioFile = new File(storePath + fileName +suffix);
			if (!scenarioFile.getParentFile().isDirectory()) {
				scenarioFile.getParentFile().mkdirs();
			}
			out = new FileOutputStream(scenarioFile);
			
			//向文件中写数据
			byte[] resultByteArr = resultStr.getBytes();
			out.write(resultByteArr);
			out.flush();
			
			FileUtils.downloadFile(response, storePath + fileName + suffix, fileName + suffix);
			//tab_user_watch_viewrole_map
			
			this.sysLogService.saveSysLogForApp(request, "下载剧组下的所有剧本信息", userInfo.getClientType(), ViewInfoModel.TABLE_NAME, null, 5);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);

			this.sysLogService.saveSysLogForApp(request, "下载剧组下的所有剧本信息失败：" + ie.getMessage(), userInfo.getClientType(), ViewInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		} catch (Exception e) {
			logger.error("", e);

			this.sysLogService.saveSysLogForApp(request, "下载剧组下的所有剧本信息失败：" + e.getMessage(), userInfo.getClientType(), ViewInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	/**
	 * 校验剧本是否有更新
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/checkScenarioHasUpdate")
	public Object checkScenarioHasUpdate(HttpServletRequest request, String crewId, String userId, String clientUUID) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			
			boolean hasUpdate = false;
			
			ScenarioInfoModel scenarioInfo = this.scenarioService.queryLastScenario(crewId);
			DownloadScenarioRecordModel record = this.downLoadScenarioRecordService.queryByCrewIdAndClientUUID(crewId, clientUUID);
			if (scenarioInfo != null && record == null) {
				hasUpdate = true;
			}
			
			resultMap.put("hasUpdate", hasUpdate);
			
			this.sysLogService.saveSysLogForApp(request, "查看剧本", userInfo.getClientType(), ScenarioInfoModel.TABLE_NAME, null, 0);
		} catch (IllegalArgumentException ie) {
			String msg = ie.getMessage();
			logger.error(msg, ie);
			throw new IllegalArgumentException(msg);
			
		} catch (Exception e) {
			String msg = "未知异常，获取剧本内容失败";
			logger.error(msg, e);
			this.sysLogService.saveSysLogForApp(request, "查看剧本失败：" + e.getMessage(), userInfo.getClientType(), ScenarioInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException(msg);
		}
		
		return resultMap;
	}
}
