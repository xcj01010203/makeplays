package com.xiaotu.makeplays.finance.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.finance.dao.PaymentFinanSubjMapDao;
import com.xiaotu.makeplays.finance.model.PaymentFinanSubjMapModel;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 付款单和财务科目关联关系
 * @author xuchangjian 2016-8-9下午2:49:19
 */
@Service
public class PaymentFinanSubjMapService {

	@Autowired
	private PaymentFinanSubjMapDao paymentFinanSubjMapDao;
	
	/**
	 * 根据财务科目ID查询数据
	 * @param crewId
	 * @param financeSubjId
	 * @return
	 */
	public List<PaymentFinanSubjMapModel> queryByFinanceSubjId(String crewId, String financeSubjId) {
		return this.paymentFinanSubjMapDao.queryByFinanceSubjId(crewId, financeSubjId);
	}
	
	/**
	 * 根据剧组ID查询数据
	 * @param crewId
	 * @return
	 */
	public List<PaymentFinanSubjMapModel> queryByCrewId(String crewId) {
		return this.paymentFinanSubjMapDao.queryByCrewId(crewId);
	}
	
	/**
	 * 保存付款单时保存和财务科目的关联关系
	 * @param paymentId
	 * @param sujectMapStr 和财务科目关联情况，格式：摘要##财务科目ID##财务科目名称##金额，多个以&&隔开
	 * @throws Exception 
	 */
	public void saveByPaymentSujectMapStr(String crewId, String paymentId, String sujectMapStr) throws Exception {
		String[] paymentSubjMapStrArray = sujectMapStr.split("&&");
		for (String singlePaySubjMapStr : paymentSubjMapStrArray) {
			String[] mapDetailArray = singlePaySubjMapStr.split("##");
			String summary = mapDetailArray[0];
			String financeSubjId = mapDetailArray[1];
			String financeSubjName = mapDetailArray[2];
			Double money = Double.valueOf(mapDetailArray[3]);
			
			if (StringUtils.isBlank(financeSubjId) || StringUtils.isBlank(financeSubjName)) {
				throw new IllegalArgumentException("请选择财务科目");
			}
			if (money == null) {
				throw new IllegalArgumentException("请填写金额");
			}
			if (!StringUtils.isBlank(summary) && summary.length() > 200) {
				throw new IllegalArgumentException("摘要需控制在200字以内");
			}
			
			PaymentFinanSubjMapModel paymentFianSubjMap = new PaymentFinanSubjMapModel();
			paymentFianSubjMap.setMapId(UUIDUtils.getId());
			paymentFianSubjMap.setCrewId(crewId);
			paymentFianSubjMap.setPaymentId(paymentId);
			paymentFianSubjMap.setFinanceSubjId(financeSubjId);
			paymentFianSubjMap.setFinanceSubjName(financeSubjName);
			paymentFianSubjMap.setSummary(summary);
			paymentFianSubjMap.setMoney(money);
			
			this.paymentFinanSubjMapDao.add(paymentFianSubjMap);
		}
	}
	
	/**
	 * 根据付款单ID删除和财务科目的关联数据
	 * @param crewId
	 * @param paymentId
	 */
	public void deleteByPaymentId(String crewId, String paymentId) {
		this.paymentFinanSubjMapDao.deleteByPaymentId(crewId, paymentId);
	}
	
	/**
	 * 根据付款单ID查询数据
	 * @param paymentId
	 * @return
	 */
	public List<PaymentFinanSubjMapModel> queryByPaymentId(String paymentId) {
		return this.paymentFinanSubjMapDao.queryByPaymentId(paymentId);
	}
}
