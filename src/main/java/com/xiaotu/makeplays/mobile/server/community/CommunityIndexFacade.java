package com.xiaotu.makeplays.mobile.server.community;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.community.model.ApplyCaseModel;
import com.xiaotu.makeplays.community.model.constants.TeamStatus;
import com.xiaotu.makeplays.community.service.ApplyCaseService;
import com.xiaotu.makeplays.community.service.NewsInfoService;
import com.xiaotu.makeplays.community.service.SearchTeamInfoService;
import com.xiaotu.makeplays.community.service.TeamInfoService;
import com.xiaotu.makeplays.mobile.server.community.dto.NewsInfoDto;
import com.xiaotu.makeplays.mobile.server.community.dto.SearchTeamInfoDto;
import com.xiaotu.makeplays.mobile.server.community.dto.TeamInfoDto;
import com.xiaotu.makeplays.utils.Page;

/**
 * 获取社区首页数据
 * @author wanrenyi 2016年9月6日下午4:38:12
 */
@Controller
@RequestMapping("/interface/communityIndex")
public class CommunityIndexFacade {

	Logger logger = LoggerFactory.getLogger(TeamInfoFacade.class);
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private TeamInfoService teamInfoService;
	
	@Autowired
	private SearchTeamInfoService searchService;
	
	@Autowired
	private NewsInfoService newsInfoService;
	
	@Autowired
	private ApplyCaseService applyCaseService;
	
	/**
	 * 获取首页数据 
	 * @param picCount 轮播图的条数
	 * @param teamCount 组训条数
	 * @param searchCount 寻组条数
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainIndexData")
	public Object obtainIndexData(Integer picCount, Integer teamCount, Integer searchCount, Integer newsInfoCount){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			if (picCount == null) {
				picCount = 3;
			}
			if (teamCount == null) {
				teamCount = 3;
			}
			if (searchCount == null) {
				searchCount = 3;
			}
			if (newsInfoCount == null) {
				newsInfoCount = 3;
			}
			
			//跟剧不同类型查询不同的首页轮播图
			List<Map<String, Object>> picList = teamInfoService.getTeamPic(picCount, null);
			
			Page teamPage = new Page();
			teamPage.setPagesize(teamCount);
			
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("status", TeamStatus.TeamAvailable.getValue());
			//获取组训列表
			List<TeamInfoDto> teamInfoList = teamInfoService.getTeamInfoList(conditionMap, teamPage);
			
			
			//获取资讯列表
			Page newsPage = new Page();
			newsPage.setPagesize(newsInfoCount);
			List<NewsInfoDto> newsInfoList = newsInfoService.queryNewsInfoList(newsPage, null);
			
			//获取案例列表
			Page casePage = new Page();
			casePage.setPagesize(newsInfoCount);
			List<ApplyCaseModel> applyCaseList = this.applyCaseService.queryManyByMutiCondition(new HashMap<String, Object>(), casePage);
			List<Map<String, Object>> applyCaseMapList = new ArrayList<Map<String, Object>>();
			for (ApplyCaseModel applyCaseInfo : applyCaseList) {
				Map<String, Object> applyCaseMap = new HashMap<String, Object>();
				applyCaseMap.put("id", applyCaseInfo.getId());
				applyCaseMap.put("title", applyCaseInfo.getTitle());
				applyCaseMap.put("introduction", applyCaseInfo.getIntroduction());
				applyCaseMap.put("createTime", this.sdf1.format(applyCaseInfo.getCreatetime()));
				applyCaseMap.put("url", "http://v1.moonpool.com.cn/" + applyCaseInfo.getSrcurl());
				
				applyCaseMapList.add(applyCaseMap);
			}
			
			
			Page searchPage = new Page();
			searchPage.setPagesize(searchCount);
			//获取寻组信息列表
			List<SearchTeamInfoDto> searchTeamList = searchService.getSearchTeamList(null, searchPage);
			
			resultMap.put("picList", picList);
			resultMap.put("teamInfoList", teamInfoList);
			resultMap.put("searchTeamList", searchTeamList);
			resultMap.put("newsInfoList", newsInfoList);
			resultMap.put("applyCaseList", applyCaseMapList);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage());
			
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			
			throw new IllegalArgumentException("未知错误！", e);
		}
		return resultMap;
	}
	
	
	/**
	 * 获取资讯列表
	 * 2017-05-03废弃，资讯由于需求原因换表
	 * @param pageSize 每页显示的资讯条数
	 * @param pageNo 当前页数 默认从第一页开始查询
	 * @return
	 */
	@Deprecated
	@ResponseBody
	@RequestMapping("/obtainNewsList")
	public Object obtainNewsList(Integer pagesize, Integer pageNo, String searchTitle){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			if (pagesize == null || pagesize == 0) {
				throw new IllegalArgumentException("请输入要显示的资讯条数！");
			}
			if (pageNo == null || pageNo == 0) {
				throw new IllegalArgumentException("请输入当前页数！");
			}
			
			Page page = new Page();
			page.setPagesize(pagesize);
			page.setPageNo(pageNo);
			
			Map<String, Object> conditionMap = null;
			if (StringUtils.isNotBlank(searchTitle)) {
				conditionMap = new HashMap<String, Object>();
				conditionMap.put("searchTitle", searchTitle);
			}
			List<NewsInfoDto> newsInfoList = newsInfoService.queryNewsInfoList(page, conditionMap);
			
			resultMap.put("newsInfoList", newsInfoList);
			resultMap.put("pageCount", page.getPageCount());
		} catch (IllegalArgumentException ie) {
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			throw new IllegalArgumentException("未知错误，查询失败！");
		}
		
		return resultMap;
	}
}
