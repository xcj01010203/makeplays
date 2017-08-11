package com.xiaotu.makeplays.finance.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.finance.dao.PaymentLoanMapDao;
import com.xiaotu.makeplays.finance.model.PaymentLoanMapModel;
import com.xiaotu.makeplays.utils.BigDecimalUtil;

/**
 * 付款单和借款单的关联
 * @author xuchangjian 2016-8-19下午6:28:14
 */
@Service
public class PaymentLoanMapService {

	@Autowired
	private PaymentLoanMapDao paymentLoanMapDao;
	
	/**
	 * 根据借款单ID查询和付款单的关联
	 * @param crewId
	 * @param loanId
	 * @return
	 */
	public List<PaymentLoanMapModel> queryByLoanId(String crewId, String loanId) {
		return this.paymentLoanMapDao.queryByLoanId(crewId, loanId);
	}
	
	/**
	 * 根据付款单ID查询和借款单的关联关系
	 * @param paymentId
	 * @return
	 */
	public List<PaymentLoanMapModel> queryByPaymentId(String paymentId) {
		return this.paymentLoanMapDao.queryByPaymentId(paymentId);
	}
	
	/**
	 * 根据付款单ID和借款单ID删除关联关系
	 * @param paymentId
	 * @param loanId
	 * @return
	 * @throws Exception 
	 */
	public void deleteByPaymentLoanId(String crewId, String paymentId, String loanId) throws Exception {
		PaymentLoanMapModel paymentLoanMap = this.paymentLoanMapDao.queryByPaymentLoanId(paymentId, loanId);
		Double repaymentMoney = paymentLoanMap == null?0.00:paymentLoanMap.getRepaymentMoney();
		Double loanBalance = paymentLoanMap == null?0.00:paymentLoanMap.getLoanBalance();
		Date createTime = paymentLoanMap == null?null:paymentLoanMap.getCreateTime();
		
		Double payMoney = BigDecimalUtil.subtract(repaymentMoney, loanBalance);
		
		//先删除关联关系
		this.paymentLoanMapDao.deleteByPaymentLoanId(paymentId, loanId);
		
		//再更新这张借款单的其他还款信息，如果还款日期在该张付款单之后，则需要更新金额信息
		List<PaymentLoanMapModel> otherMapList = this.paymentLoanMapDao.queryByLoanId(crewId, loanId);
		List<PaymentLoanMapModel> toUpdateMapList = new ArrayList<PaymentLoanMapModel>();
		for (PaymentLoanMapModel otherMap : otherMapList) {
			Date myCreateTime = otherMap.getCreateTime();
			if (myCreateTime.after(createTime)) {
				otherMap.setRepaymentMoney(BigDecimalUtil.add(otherMap.getRepaymentMoney(), payMoney));
				otherMap.setLoanBalance(BigDecimalUtil.add(otherMap.getLoanBalance(), payMoney));
				toUpdateMapList.add(otherMap);
			}
		}
		
		this.paymentLoanMapDao.updateBatch(toUpdateMapList, "mapId", PaymentLoanMapModel.class);
	}
	
	/**
	 * 根据付款单的ID删除和借款单的关联
	 * @param paymentId
	 * @throws Exception 
	 */
	public void deleteByPaymentId(String crewId, String paymentId) throws Exception {
		List<PaymentLoanMapModel> myMapList = this.paymentLoanMapDao.queryByPaymentId(paymentId);
		List<String> loanIdList = new ArrayList<String>();
		if (myMapList == null) {
			myMapList = new ArrayList<PaymentLoanMapModel>();
		}
		for (PaymentLoanMapModel myMap : myMapList) {
			if (!loanIdList.contains(myMap.getLoanId())) {
				loanIdList.add(myMap.getLoanId());
			}
		}
		
		for (String loanId : loanIdList) {
			this.deleteByPaymentLoanId(crewId, paymentId, loanId);
		}
	}
	
}
