package com.xiaotu.makeplays.community.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.community.model.NewsInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 资讯操作的dao
 * @author wanrenyi 2016年9月14日上午9:24:45
 */
@Repository
public class NewsInfoDao extends BaseDao<NewsInfoModel>{

	/**
	 * 分页查询首页资讯列表
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> queryIndexNewsList(Page page, Map<String, Object> conditionMap){
		StringBuffer sql = new StringBuffer();
		List<Object> param = new ArrayList<Object>();
		
		sql.append("select id,title,introduction,newstime from " + NewsInfoModel.TABLE_NAME);
		
		if (conditionMap != null) {
			String searchTitle = (String) conditionMap.get("searchTitle");
			if (StringUtils.isNotBlank(searchTitle)) {
				sql.append(" where  title like concat('%',?,'%')");
				param.add(searchTitle);
			}
		}
		sql.append(" order by newstime desc");
		List<Map<String, Object>> query = this.query(sql.toString(), param.toArray(), page);
		return query;
	}
	
	/**
	 * 根据资讯id查询资讯内容
	 * @param mewsId
	 * @return
	 * @throws Exception 
	 */
	public NewsInfoModel queryNewsInfoById(String newsId) throws Exception {
		String sql = "select * from " + NewsInfoModel.TABLE_NAME + " where id = ?";
		
		NewsInfoModel infoModel = this.queryForObject(sql, new Object[] {newsId}, NewsInfoModel.class);
		return infoModel;
	}
}
