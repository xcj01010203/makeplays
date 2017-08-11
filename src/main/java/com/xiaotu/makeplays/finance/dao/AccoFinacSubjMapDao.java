package com.xiaotu.makeplays.finance.dao;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.finance.model.AccoFinacSubjMapModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 会计科目和预算科目关联关系
 * @author xuchangjian 2016-6-22上午11:00:03
 */
@Repository
public class AccoFinacSubjMapDao extends BaseDao<AccoFinacSubjMapModel> {

	/**
	 * 根据会计科目ID和财务科目ID查询对应的关联关系
	 * @param crewId
	 * @param accountSubjId
	 * @param financeSubjId
	 * @return
	 * @throws Exception
	 */
	public AccoFinacSubjMapModel queryByAccAndFinaSubId(String crewId, String accountSubjId, String financeSubjId) throws Exception {
		String sql = "select * from tab_account_finance_subject_map where crewId=? and accountSubjId=? and financeSubjId=?";
		return this.queryForObject(sql, new Object[] {crewId, accountSubjId, financeSubjId}, AccoFinacSubjMapModel.class);
	}
	
	/**
	 * 根据会计科目ID和财务科目ID删除对应的关联关系
	 * @param crewId
	 * @param accountSubjId
	 * @param financeSubjId
	 */
	public void deleteByAccAndFinaSubId(String crewId, String accountSubjId, String financeSubjId) {
		String sql = "delete from tab_account_finance_subject_map where crewId = ? and accountSubjId=? and financeSubjId=?";
		this.getJdbcTemplate().update(sql, new Object[] {crewId, accountSubjId, financeSubjId});
	}
	
	/**
	 * 根据会计科目Id删除和财务科目的关联关系
	 * @param accountSubjId
	 */
	public void deleteByAccSubjId(String accountSubjId) {
		String sql = "delete from tab_account_finance_subject_map where accountSubjId=? ";
		this.getJdbcTemplate().update(sql, new Object[] {accountSubjId});
	}
	
	/**
	 * 根据财务科目Id删除和财务科目的关联关系
	 * @param accountSubjId
	 */
	public void deleteByFinaSubId(String financeSubjId) {
		String sql = "delete from tab_account_finance_subject_map where financeSubjId=? ";
		this.getJdbcTemplate().update(sql, new Object[] {financeSubjId});
	}
}
