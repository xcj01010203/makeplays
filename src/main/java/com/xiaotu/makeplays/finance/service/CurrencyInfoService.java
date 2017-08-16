package com.xiaotu.makeplays.finance.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.finance.dao.CurrencyInfoDao;
import com.xiaotu.makeplays.finance.model.CurrencyInfoModel;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 货币信息
 * @author xuchangjian 2016-8-3下午5:04:20
 */
@Service
public class CurrencyInfoService {

	@Autowired
	private CurrencyInfoDao currencyInfoDao;
	
	@Autowired
	private ContractActorService contractActorService;
	
	@Autowired
	private ContractWorkerService contractWorkerService;
	
	@Autowired
	private ContractProduceService contractProduceService;
	
	/**
	 * 根据多个条件查询货币信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<CurrencyInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.currencyInfoDao.queryManyByMutiCondition(conditionMap, page);
	}
	
	/**
	 * 查询货币列表
	 * 带有总预算信息
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryCurrencyListWithBudget(String crewId) {
		return this.currencyInfoDao.queryCurrencyListWithBudget(crewId);
	}
	
	/**
	 * 保存货币信息
	 * @param id	ID
	 * @param name	名称
	 * @param code	编码
	 * @param ifStandard	是否本位币
	 * @param ifEnable	是否启用
	 * @param exchangeRate	汇率
	 * @return
	 * @throws Exception 
	 */
	public void saveCurrencyInfo(String crewId, String id, String name, 
			String code, Boolean ifStandard, Boolean ifEnable, Double exchangeRate) throws Exception {
		
		CurrencyInfoModel currencyInfo = new CurrencyInfoModel();
		
		if (!StringUtils.isBlank(id)) {
			currencyInfo = this.currencyInfoDao.queryById(id);
		} else {
			currencyInfo.setId(UUIDUtils.getId());
		}
		currencyInfo.setCrewId(crewId);
		currencyInfo.setName(name);
		currencyInfo.setCode(code);
		
		if (ifStandard) {
			exchangeRate = 1.0;
			ifEnable = true;
			
			//把其他的本位币设置为非本位币
			CurrencyInfoModel standardCurrency = this.currencyInfoDao.queryStandardCurrency(crewId);
			standardCurrency.setIfStandard(false);
			this.currencyInfoDao.update(standardCurrency, "id");
		}
		
		currencyInfo.setExchangeRate(exchangeRate);
		currencyInfo.setIfEnable(ifEnable);
		currencyInfo.setIfStandard(ifStandard);
		
		
		if (!StringUtils.isBlank(id)) {
			this.currencyInfoDao.update(currencyInfo, "id");
		} else {
			this.currencyInfoDao.add(currencyInfo);
		}
	}
	
	/**
	 * 根据名称查询货币信息
	 * 如果 @param id不为空，则排除掉ID为id的货币信息
	 * 主要用于检查是否名称是否重复
	 * @param name
	 * @return
	 */
	public List<CurrencyInfoModel> queryByNameExcepOwn(String crewId, String name, String id) {
		return this.currencyInfoDao.queryByNameExcepOwn(crewId, name, id);
	}
	
	/**
	 * 根据编码查询货币信息
	 * 如果 @param id不为空，则排除掉ID为id的货币信息
	 * 主要用于检查是否名称是否重复
	 * @param code
	 * @return
	 */
	public List<Map<String, Object>> queryByCodeExcepOwn(String crewId, String code, String id) {
		return this.currencyInfoDao.queryByCodeExcepOwn(crewId, code, id);
	}
	
	/**
	 * 初始化剧组的第一个货币
	 * @param crewId
	 * @return
	 * @throws Exception 
	 */
	public CurrencyInfoModel initFirstCurrency(String crewId) throws Exception {
		//把人民币加入到剧组中，并设置为本位币
		CurrencyInfoModel rmbCurrencyInfo = this.currencyInfoDao.queryById("1");
		
		CurrencyInfoModel newCurrency = new CurrencyInfoModel();
		newCurrency.setId(UUIDUtils.getId());
		newCurrency.setCrewId(crewId);
		newCurrency.setName(rmbCurrencyInfo.getName());
		newCurrency.setCode(rmbCurrencyInfo.getCode());
		newCurrency.setIfEnable(true);
		newCurrency.setIfStandard(true);
		newCurrency.setExchangeRate(1.0);
		this.currencyInfoDao.add(newCurrency);
		
		return newCurrency;
	}
	
	/**
	 * 根据ID查询货币信息
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public CurrencyInfoModel queryById (String id) throws Exception {
		return this.currencyInfoDao.queryById(id);
	}
	
	/**
	 * 查询剧组中的本位币
	 * @param crewId
	 * @return
	 * @throws Exception 
	 */
	public CurrencyInfoModel queryStandardCurrency(String crewId) throws Exception {
		return this.currencyInfoDao.queryStandardCurrency(crewId);
	}
	/**
	 * 查询剧组中启用的币种信息
	 * @param crewId 剧组id
	 * @return
	 */
	public List<CurrencyInfoModel> queryCurrencyInfoByCrewId(String crewId){
		return this.currencyInfoDao.queryCurrencyInfoByCrewId(crewId);
	}
	
	/**
	 * 设置币种为本位币
	 * @param id	币种ID
	 * @param ifStandard	是否是本位币
	 * @return
	 * @throws Exception 
	 */
	public void makeCurrencyStandard(String crewId, String id) throws Exception {
		//把其他的本位币设置为非本位币
		CurrencyInfoModel standardCurrency = this.currencyInfoDao.queryStandardCurrency(crewId);
		standardCurrency.setIfStandard(false);
		this.currencyInfoDao.update(standardCurrency, "id");
		
		//把该币种设置为本位币
		CurrencyInfoModel myCurrency = this.currencyInfoDao.queryById(id);
		myCurrency.setIfStandard(true);
		myCurrency.setIfEnable(true);
		myCurrency.setExchangeRate(1);
		this.currencyInfoDao.update(myCurrency, "id");
	}
	
	/**
	 * 更新一条记录
	 * @param currency
	 * @throws Exception
	 */
	public void updateOne(CurrencyInfoModel currency) throws Exception {
		this.currencyInfoDao.update(currency, "id");
	}
}
