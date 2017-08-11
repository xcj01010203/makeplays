package com.xiaotu.makeplays.finance.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.finance.dao.FinanSubjCurrencyMapDao;
import com.xiaotu.makeplays.finance.model.FinanSubjCurrencyMapModel;

/**
 * 财务预算币种关联信息
 * @author xuchangjian 2016-8-3下午5:07:36
 */
@Service
public class FinanSubjCurrencyMapService {
	
	@Autowired
	private FinanSubjCurrencyMapDao finanSubjCurrencyMapDao;

	/**
	 * 根据剧组ID查询财务预算和币种的关联关系
	 * @param crewId
	 * @return
	 */
	public List<FinanSubjCurrencyMapModel> queryByCrewId(String crewId) {
		return this.finanSubjCurrencyMapDao.queryByCrewId(crewId);
	}
	
	/**
	 * 查询财务科目中的单位列表
	 * @return
	 */
	public List<Map<String, Object>> queryUnitTypeList(String crewId){
		return this.finanSubjCurrencyMapDao.queryFinanSubjUnitTypeList(crewId);
	}
}
