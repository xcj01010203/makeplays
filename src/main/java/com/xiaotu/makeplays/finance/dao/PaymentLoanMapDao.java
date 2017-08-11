package com.xiaotu.makeplays.finance.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.finance.model.PaymentLoanMapModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 付款单和借款单关联关系
 * @author xuchangjian 2016-8-19下午6:27:28
 */
@Repository
public class PaymentLoanMapDao extends BaseDao<PaymentLoanMapModel> {

	/**
	 * 根据付款单ID和借款单ID查询
	 * @param paymentId
	 * @param loanId
	 * @return
	 * @throws Exception 
	 */
	public PaymentLoanMapModel queryByPaymentLoanId(String paymentId, String loanId) throws Exception {
		String sql = "select * from " + PaymentLoanMapModel.TABLE_NAME + " where paymentId = ? and loanId = ? ";
		return this.queryForObject(sql, new Object[] {paymentId, loanId}, PaymentLoanMapModel.class);
	}
	
	/**
	 * 根据借款单ID查询和付款单的关联
	 * @param crewId
	 * @param loanId
	 * @return
	 */
	public List<PaymentLoanMapModel> queryByLoanId(String crewId, String loanId) {
		String sql = "select * from " + PaymentLoanMapModel.TABLE_NAME + " where crewId = ? and loanId = ? order by createTime ";
		return this.query(sql, new Object[] {crewId, loanId}, PaymentLoanMapModel.class, null);
	}
	
	/**
	 * 根据付款单的ID删除和借款单的关联
	 * @param paymentId
	 */
	public void deleteByPaymentId(String paymentId) {
		String sql = "delete from " + PaymentLoanMapModel.TABLE_NAME + " where paymentId = ? ";
		this.getJdbcTemplate().update(sql, paymentId);
	}
	
	/**
	 * 根据借款单ID删除关联关系
	 * @param loanId
	 */
	public void deleteByLoanId(String loanId) {
		String sql = "delete from " + PaymentLoanMapModel.TABLE_NAME + " where loanId = ? ";
		this.getJdbcTemplate().update(sql, loanId);
	}
	
	/**
	 * 根据付款单ID查询和借款单的关联关系
	 * @param paymentId
	 * @return
	 */
	public List<PaymentLoanMapModel> queryByPaymentId(String paymentId) {
		String sql = "select * from " + PaymentLoanMapModel.TABLE_NAME + " where paymentId = ? order by createTime";
		return this.query(sql, new Object[] {paymentId}, PaymentLoanMapModel.class, null);
	}
	
	/**
	 * 根据付款单ID和借款单ID删除关联关系
	 * @param paymentId
	 * @param loanId
	 * @return
	 * @throws Exception 
	 */
	public void deleteByPaymentLoanId(String paymentId, String loanId) throws Exception {
		String sql = "delete from " + PaymentLoanMapModel.TABLE_NAME + " where paymentId = ? and loanId = ? ";
		this.getJdbcTemplate().update(sql, paymentId, loanId);
	}
	
	/**
	 * 根据付款单ID和借款单ID删除关联关系
	 * @param paymentId
	 * @param loanId
	 * @return
	 * @throws Exception 
	 */
	public void deleteByPaymentLoanIds(String paymentId, String loanIds) throws Exception {
		loanIds = "'" + loanIds.replace(",", "','") + "'";
		String sql = "delete from " + PaymentLoanMapModel.TABLE_NAME + " where paymentId = ? and loanId in ("+ loanIds +") ";
		this.getJdbcTemplate().update(sql, paymentId);
	}
}
