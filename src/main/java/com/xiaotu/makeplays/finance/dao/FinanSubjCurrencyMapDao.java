package com.xiaotu.makeplays.finance.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.finance.model.FinanSubjCurrencyMapModel;
import com.xiaotu.makeplays.finance.model.FinanceSubjectModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 财务预算币种关联信息
 * @author xuchangjian 2016-8-3下午5:07:03
 */
@Repository
public class FinanSubjCurrencyMapDao extends BaseDao<FinanSubjCurrencyMapModel> {

	/**
	 * 根据剧组ID查询财务预算和币种的关联关系
	 * @param crewId
	 * @return
	 */
	public List<FinanSubjCurrencyMapModel> queryByCrewId(String crewId) {
		String sql = "select * from " + FinanceSubjectModel.TABLE_NAME + " where crewId = ?";
		return this.query(sql, new Object[] {crewId}, FinanSubjCurrencyMapModel.class, null);
	}
	
	/**
	 * 根据财务科目ID查询和货币的关联关系
	 * @param financeSubjId
	 * @return
	 * @throws Exception 
	 */
	public FinanSubjCurrencyMapModel queryByFinanSubjId(String financeSubjId) throws Exception {
		String sql = "select * from " + FinanSubjCurrencyMapModel.TABLE_NAME + " where financeSubjId = ? ";
		return this.queryForObject(sql, new Object[] {financeSubjId}, FinanSubjCurrencyMapModel.class);
	}
	
	/**
	 * 删除剧组下的所有关联关系
	 * @param crewId
	 */
	public void deleteByCrewId(String crewId) {
		String sql = "delete from " + FinanSubjCurrencyMapModel.TABLE_NAME + " where crewId = ?";
		this.getJdbcTemplate().update(sql, new Object[] {crewId});
	}
	
	/**
	 * 根据财务科目ID删除关联关系
	 * @param financeSubjId
	 */
	public void deleteByFinanSubjId(String financeSubjId) {
		String sql = "delete from " + FinanSubjCurrencyMapModel.TABLE_NAME + " where financeSubjId = ?";
		this.getJdbcTemplate().update(sql, financeSubjId);
	}
	
	/**
	 * 查询费用预算单位列表
	 * @return
	 */
	public List<Map<String, Object>> queryFinanSubjUnitTypeList(String crewId){
		String sql = "SELECT DISTINCT unitType FROM tab_finanSubj_currency_map WHERE crewId = ? or crewId = '0' ORDER BY crewId,unitType";
		
		return this.query(sql, new Object[] {crewId}, null);
	}
}
