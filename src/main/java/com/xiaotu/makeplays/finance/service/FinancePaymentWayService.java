package com.xiaotu.makeplays.finance.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.finance.dao.FinancePaymentWayDao;
import com.xiaotu.makeplays.finance.model.FinancePaymentWayModel;

/**
 * 财务付款方式
 * @author xuchangjian 2016-8-19下午5:40:07
 */
@Service
public class FinancePaymentWayService {

	@Autowired
	private FinancePaymentWayDao financePaymentWayDao;
	
	/**
	 * 根据支付方式名称查询支付方式列表
	 * @param crewId
	 * @param wayName	付款方式名称
	 * @return
	 */
	public List<FinancePaymentWayModel> queryByWayName(String crewId, String wayName) {
		return this.financePaymentWayDao.queryByWayName(crewId, wayName);
	}
	
	/**
	 * 根据剧组ID查询财务支付方式
	 * @param crewId
	 * @return
	 */
	public List<FinancePaymentWayModel> queryByCrewId(String crewId) {
		return this.financePaymentWayDao.queryByCrewId(crewId);
	}
	
	/**
	 * 根据ID查询数据
	 * @param crewId
	 * @param wayId
	 * @return
	 * @throws Exception
	 */
	public FinancePaymentWayModel queryById(String crewId, String wayId) throws Exception {
		return this.financePaymentWayDao.queryById(crewId, wayId);
	}
}
