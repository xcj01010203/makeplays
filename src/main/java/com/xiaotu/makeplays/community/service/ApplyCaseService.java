package com.xiaotu.makeplays.community.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.community.dao.ApplyCaseDao;
import com.xiaotu.makeplays.community.model.ApplyCaseModel;
import com.xiaotu.makeplays.utils.Page;

/**
 * 百晓生系统中的应用案例
 * @author xuchangjian 2017-5-3下午3:14:05
 */
@Service
public class ApplyCaseService {

	@Autowired
	private ApplyCaseDao applyCaseDao;
	
	/**
	 * 根据多个条件查询案例信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ApplyCaseModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.applyCaseDao.queryManyByMutiCondition(conditionMap, page);
	}
}
