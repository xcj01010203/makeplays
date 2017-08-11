package com.xiaotu.makeplays.finance.dao;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.finance.model.ContractMonthPayDetailModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 合同按月支付薪酬明细
 * @author xuchangjian 2016-11-17上午11:38:18
 */
@Repository
public class ContractMonthPayDetailDao extends BaseDao<ContractMonthPayDetailModel> {

	/**
	 * 根据合同ID删除按月支付详情
	 * @param crewId
	 * @param contractId
	 */
	public void deleteByContractId(String crewId, String contractId) {
		String sql = "delete from tab_contract_month_pay_detail where contractId=? and crewId=?";
		this.getJdbcTemplate().update(sql, contractId, crewId);
	}
	
	/**
	 * 根据多个条件查询按月支付详情信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ContractMonthPayDetailModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + ContractMonthPayDetailModel.TABLE_NAME + " where 1 = 1 ");
		
		Set<String> keySet = conditionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		List<Object> conList = new LinkedList<Object>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = conditionMap.get(key);
			sql.append(" and " + key + " = ?");
			conList.add(value);
		}
		sql.append(" ORDER BY createTime, payDate ,month");
		Object[] objArr = conList.toArray();
		List<ContractMonthPayDetailModel> monthPayDetailList = this.query(sql.toString(), objArr, ContractMonthPayDetailModel.class, page);
		
		return monthPayDetailList;
	}
}
