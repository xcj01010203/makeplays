package com.xiaotu.makeplays.scenario.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.scenario.dao.ScripteleInfoDao;
import com.xiaotu.makeplays.scenario.model.ScripteleInfoModel;
import com.xiaotu.makeplays.utils.DateUtils;
import com.xiaotu.makeplays.utils.Page;


/**
 * 查询剧本分析时的元素信息的service
 * @author wanrenyi 2016年7月28日下午2:18:01
 */
@Service
public class ScripteleInfoService {
	
	private List<ScripteleInfoModel> scripteleList;	//存储数据库中的剧本元素信息

	private Date preFreshData;	//上次刷新时间
	
	@Autowired
	private ScripteleInfoDao scripteleInfoDao;
	
	/**
	 * 根据多个条件查询元素信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 * @throws ParseException 
	 */
	public List<ScripteleInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) throws ParseException {
		Date nowDate = new Date();
		int between_minus = 0;
		if (preFreshData != null) {
			between_minus = DateUtils.minusBetween(preFreshData, nowDate);
		}
		
		if (scripteleList == null || preFreshData == null || between_minus >= 30) {
			scripteleList = this.scripteleInfoDao.queryManyByMutiCondition(conditionMap, page);
			preFreshData = new Date();
		}
		return scripteleList;
	}
}
