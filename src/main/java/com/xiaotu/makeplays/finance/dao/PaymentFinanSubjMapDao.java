package com.xiaotu.makeplays.finance.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.finance.model.PaymentFinanSubjMapModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 付款与财务科目关联信息
 * @author xuchangjian 2016-8-9下午2:46:44
 */
@Repository
public class PaymentFinanSubjMapDao extends BaseDao<PaymentFinanSubjMapModel> {

	/**
	 * 根据财务科目ID查询数据
	 * @param crewId
	 * @param financeSubjId
	 * @return
	 */
	public List<PaymentFinanSubjMapModel> queryByFinanceSubjId(String crewId, String financeSubjId) {
		String sql = "select * from " + PaymentFinanSubjMapModel.TABLE_NAME + " where crewId = ? and financeSubjId = ?";
		return this.query(sql, new Object[] {crewId, financeSubjId}, PaymentFinanSubjMapModel.class, null);
	}
	
	/**
	 * 根据剧组ID查询数据
	 * @param crewId
	 * @return
	 */
	public List<PaymentFinanSubjMapModel> queryByCrewId(String crewId) {
		String sql = "select * from " + PaymentFinanSubjMapModel.TABLE_NAME + " where crewId = ?";
		return this.query(sql, new Object[] {crewId}, PaymentFinanSubjMapModel.class, null);
	}
	
	/**
	 * 根据付款单ID删除和财务科目的关联数据
	 * @param crewId
	 * @param paymentId
	 */
	public void deleteByPaymentId(String crewId, String paymentId) {
		String sql = "delete from " + PaymentFinanSubjMapModel.TABLE_NAME + " where crewId = ? and paymentId = ?";
		this.getJdbcTemplate().update(sql, new Object[] {crewId, paymentId});
	}
	
	/**
	 * 根据付款单ID查询数据
	 * @param paymentId
	 * @return
	 */
	public List<PaymentFinanSubjMapModel> queryByPaymentId(String paymentId) {
		String sql = "select * from " + PaymentFinanSubjMapModel.TABLE_NAME + " where paymentId = ? order by mapId";
		return this.query(sql, new Object[] {paymentId}, PaymentFinanSubjMapModel.class, null);
	}
	
	/**
	 * 根据剧组ID清空财务科目关联信息，用于费用预算导入
	 * @param crewId
	 */
	public void deleteFinanceSubjectByCrewId(String crewId) {
		String sql = "update " + PaymentFinanSubjMapModel.TABLE_NAME + " set financeSubjId=null,financeSubjName=null where crewId = ? ";
		this.getJdbcTemplate().update(sql, new Object[]{crewId});
	}
}
