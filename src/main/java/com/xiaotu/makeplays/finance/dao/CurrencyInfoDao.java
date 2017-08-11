package com.xiaotu.makeplays.finance.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.finance.model.CurrencyInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 货币信息
 * @author xuchangjian 2016-8-3下午5:03:22
 */
@Repository
public class CurrencyInfoDao extends BaseDao<CurrencyInfoModel> {

	/**
	 * 根据多个条件查询货币信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<CurrencyInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + CurrencyInfoModel.TABLE_NAME + " where 1 = 1 ");
		
		Set<String> keySet = conditionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		List<Object> conList = new LinkedList<Object>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = conditionMap.get(key);
			sql.append(" and " + key + " = ?");
			conList.add(value);
		}
		sql.append(" order by ifStandard desc, ifEnable desc, id ");
		Object[] objArr = conList.toArray();
		List<CurrencyInfoModel> currencyList = this.query(sql.toString(), objArr, CurrencyInfoModel.class, page);
		
		return currencyList;
	}
	
	/**
	 * 查询货币列表
	 * 带有总预算信息
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryCurrencyListWithBudget(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tci.id, tci.name, tci.`code`, sum(tfcm.money) money, tci.exchangeRate ");
		sql.append(" FROM ");
		sql.append(" 	tab_currency_info tci ");
		sql.append(" 	left join tab_finanSubj_currency_map tfcm on tfcm.currencyId = tci.id ");
		sql.append(" 	left join tab_finance_subject tfs on tfcm.financeSubjId = tfs.id and tfs.crewId =? ");
		sql.append(" WHERE ");
		sql.append(" 	tci.ifEnable = 1 ");
		sql.append("    and tci.crewId = ? ");
		sql.append(" GROUP BY tci.id, tci.name, tci.`code` ");
		
		return this.query(sql.toString(), new Object[] {crewId, crewId}, null);
	}
	
	/**
	 * 根据名称查询货币信息
	 * 如果 @param id不为空，则排除掉ID为id的货币信息
	 * 主要用于检查是否名称是否重复
	 * @param name
	 * @return
	 */
	public List<CurrencyInfoModel> queryByNameExcepOwn(String crewId, String name, String id) {
		List<Object> paramsList = new ArrayList<Object>();
		
		StringBuilder sql = new StringBuilder();
		sql.append("select * from " + CurrencyInfoModel.TABLE_NAME + " where crewId = ? and name = ? ");
		paramsList.add(crewId);
		paramsList.add(name);
		if (!StringUtils.isBlank(id)) {
			sql.append(" and id != ? ");
			paramsList.add(id);
		}
		
		return this.query(sql.toString(), paramsList.toArray(), null);
	}
	
	/**
	 * 根据编码查询货币信息
	 * 如果 @param id不为空，则排除掉ID为id的货币信息
	 * 主要用于检查是否名称是否重复
	 * @param code
	 * @return
	 */
	public List<Map<String, Object>> queryByCodeExcepOwn(String crewId, String code, String id) {
		List<Object> paramsList = new ArrayList<Object>();
		
		StringBuilder sql = new StringBuilder();
		sql.append("select * from " + CurrencyInfoModel.TABLE_NAME + " where crewId = ? and code = ? ");
		paramsList.add(crewId);
		paramsList.add(code);
		if (!StringUtils.isBlank(id)) {
			sql.append(" and id != ? ");
			paramsList.add(id);
		}
		
		return this.query(sql.toString(), paramsList.toArray(), null);
	}
	
	/**
	 * 根据ID查询货币信息
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public CurrencyInfoModel queryById (String id) throws Exception {
		String sql = "select * from " + CurrencyInfoModel.TABLE_NAME + " where id = ? ";
		return this.queryForObject(sql, new Object[] {id}, CurrencyInfoModel.class);
	}
	
	/**
	 * 查询剧组中的本位币
	 * @param crewId
	 * @return
	 * @throws Exception 
	 */
	public CurrencyInfoModel queryStandardCurrency(String crewId) throws Exception {
		String sql = "select * from " + CurrencyInfoModel.TABLE_NAME + " where crewId = ? and ifStandard = 1";
		return this.queryForObject(sql, new Object[] {crewId}, CurrencyInfoModel.class);
	}
	
	/**
	 * 查询剧组中启用的币种信息
	 * @param crewId 剧组id
	 * @return
	 */
	public List<CurrencyInfoModel> queryCurrencyInfoByCrewId(String crewId){
		String sql = "select * from " + CurrencyInfoModel.TABLE_NAME + " where crewId = ? and ifEnable = 1";
		return this.query(sql, new Object[] {crewId}, CurrencyInfoModel.class, null);
	}
	
	
}
