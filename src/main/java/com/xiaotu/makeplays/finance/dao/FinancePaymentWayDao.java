package com.xiaotu.makeplays.finance.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.finance.model.FinancePaymentWayModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 财务付款方式
 * @author xuchangjian 2016-8-19下午5:39:06
 */
@Repository
public class FinancePaymentWayDao extends BaseDao<FinancePaymentWayModel> {

	/**
	 * 根据支付方式名称查询支付方式列表
	 * @param crewId
	 * @param wayName	付款方式名称
	 * @return
	 */
	public List<FinancePaymentWayModel> queryByWayName(String crewId, String wayName) {
		String sql = "select * from " + FinancePaymentWayModel.TABLE_NAME + " where (crewId = ? or crewId = '0') and wayName = ? ";
		return this.query(sql, new Object[] {crewId, wayName}, FinancePaymentWayModel.class, null);
	}
	
	/**
	 * 根据剧组ID查询财务支付方式
	 * @param crewId
	 * @return
	 */
	public List<FinancePaymentWayModel> queryByCrewId(String crewId) {
		String sql = "select * from " + FinancePaymentWayModel.TABLE_NAME + " where crewId = ? or crewId = '0'";
		return this.query(sql, new Object[] {crewId}, FinancePaymentWayModel.class, null);
	}
	
	/**
	 * 根据ID查询数据
	 * @param crewId
	 * @param wayId
	 * @return
	 * @throws Exception
	 */
	public FinancePaymentWayModel queryById(String crewId, String wayId) throws Exception {
		String sql = "select * from " + FinancePaymentWayModel.TABLE_NAME + " where (crewId = ? or crewId = 0) and wayId = ?";
		return this.queryForObject(sql, new Object[] {crewId, wayId}, FinancePaymentWayModel.class);
	}
}
