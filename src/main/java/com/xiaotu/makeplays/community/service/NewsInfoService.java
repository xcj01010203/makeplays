package com.xiaotu.makeplays.community.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.community.dao.NewsInfoDao;
import com.xiaotu.makeplays.community.model.NewsInfoModel;
import com.xiaotu.makeplays.mobile.server.community.dto.NewsInfoDto;
import com.xiaotu.makeplays.utils.Page;

/**
 * 资讯操作的service
 * @author wanrenyi 2016年9月14日上午9:36:58
 */
@Service
public class NewsInfoService {

	//定义图片常量
	private static final String ANALYSIS_IMAGE_ROOT_PATH = "http://bxs.moonpool.com.cn:8899/";
	private static final String NEWSINFO_REPLACE_IMAGE_PATH = "http://a.moonpool.com.cn/";
	private static final String UPLOAD_IMAGE_PATH = "static/upload/image";
	
	@Autowired
	private NewsInfoDao newsInfoDao;
	
	/**
	 * 分页查询最新的资讯列表
	 * 2017-05-03废弃，资讯由于需求原因换表
	 * @param page
	 * @return
	 */
	@Deprecated
	public List<NewsInfoDto> queryNewsInfoList(Page page, Map<String, Object> conditionMap){
		List<NewsInfoDto> newsInfoList = new ArrayList<NewsInfoDto>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		//获取数据
		List<Map<String,Object>> newsList = newsInfoDao.queryIndexNewsList(page, conditionMap);
		
		for (Map<String, Object> map : newsList) {
			NewsInfoDto newsDto = new NewsInfoDto();
			//封装数据
			newsDto.setId((String)map.get("id"));
			newsDto.setTitle((String)map.get("title"));
			newsDto.setIntroduction((String)map.get("introduction"));
			Date newsTime = (Date)map.get("newstime");
			if (newsTime != null) {
				newsDto.setCreateTime(sdf.format(newsTime));
			}
			
			newsInfoList.add(newsDto);
		}
		return newsInfoList;
	}
	
	/**
	 * 根据资讯id获取资讯详情
	 * @param newsId
	 * @return
	 * @throws Exception 
	 */
	public NewsInfoModel queryNewsInfoById(String newsId) throws Exception {
		NewsInfoModel newsInfoModel = newsInfoDao.queryNewsInfoById(newsId);
		//对当前资讯中的图片地址进行重新拼接
		String imagePath = UPLOAD_IMAGE_PATH;
		String basePath = ANALYSIS_IMAGE_ROOT_PATH;
		String replacePath = NEWSINFO_REPLACE_IMAGE_PATH;
		
		//newsInfoModel.setContent(newsInfoModel.getContent().indexOf(replacePath) != -1));
		/*result.put("content",
						((result.get("content") + "").indexOf(replacePath) != -1) ? (map
								.get("content") + "").replaceAll(replacePath
								+ imagePath, basePath + imagePath) : (result
								.get("content") + "").replaceAll("/" + imagePath,
								basePath + imagePath));*/
		//替换图片地址
		String content = newsInfoModel.getContent();
		if (content.indexOf(replacePath)!= (-1)) {
			content = content.replaceAll(replacePath	+ imagePath, basePath + imagePath);
		}else if (content.indexOf(basePath) == -1) {
			content = content.replaceAll("/" + imagePath,basePath + imagePath);
		}
		
		newsInfoModel.setContent(content);
		return newsInfoModel;
	}
}
