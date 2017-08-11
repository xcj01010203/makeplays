package com.xiaotu.makeplays.finance.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.finance.controller.filter.PaymentInfoFilter;
import com.xiaotu.makeplays.finance.model.PaymentInfoModel;
import com.xiaotu.makeplays.finance.model.constants.PaymentStatus;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 付款单
 * @author xuchangjian 2016-8-18上午11:21:16
 */
@Repository
public class PaymentInfoDao extends BaseDao<PaymentInfoModel> {
	
	/**
	 * 根据多个条件查询付款单信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<PaymentInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + PaymentInfoModel.TABLE_NAME + " where 1 = 1 ");
		
		Set<String> keySet = conditionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		List<Object> conList = new LinkedList<Object>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = conditionMap.get(key);
			sql.append(" and " + key + " = ?");
			conList.add(value);
		}
		Object[] objArr = conList.toArray();
		List<PaymentInfoModel> paymentList = this.query(sql.toString(), objArr, PaymentInfoModel.class, page);
		
		return paymentList;
	}
	
	/**
	 * 查询付款单信息
	 * 该查询结合预算科目表、会计科目表，查询付款单对应的预算科目、以及预算科目对应的会计科目
	 * 如果一张付款单中有两个预算科目，则将会返回两条该付款单记录
	 * 如果付款单中的预算科目没有分配到会计科目，仍然会返回该条付款单记录
	 * 
	 * 
	 * 查询条件
	 * @param request
	 * @param paymentDates	付款单票据日期
	 * @param accSubjectCodes	会计科目ID，多个用逗号隔开
	 * @param finaSubjIds	财务科目ID，多个用逗号隔开
	 * @param payeeNames	收款人名称，多个用逗号隔开
	 * @param summary	摘要
	 * @param startMoney	金额范围中最小金额
	 * @param endMoney	金额范围中最大金额
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryWithAccSubjAndFinaSubjInfo(String crewId, String paymentDates,
			String accSubjectCodes, String finaSubjIds, String payeeNames,
			String summary, Double startMoney, Double endMoney) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tpi.paymentDate, ");
		sql.append(" 	tpi.receiptNo, ");
		sql.append(" 	tas.code accountCode, ");
		sql.append(" 	tas.name accountName, ");
		sql.append(" 	tfs.name subjectName, ");
		sql.append(" 	tpfm.summary, ");
		sql.append(" 	tpfm.money, ");
		sql.append(" 	tpi.payeeName, ");
		sql.append(" 	tpi.hasReceipt, ");
		sql.append(" 	tpi.billType, ");
		sql.append(" 	tfpi.wayName, ");
		sql.append("	tci.code currencyCode ");
		sql.append(" FROM ");
		sql.append(" 	tab_payment_info tpi, ");
		sql.append(" 	tab_finance_subject tfs, ");
		sql.append(" 	tab_finance_paymentWay_info tfpi, ");
		sql.append("	tab_currency_info tci, ");
		sql.append(" 	tab_payment_finanSubj_map tpfm ");
		sql.append(" LEFT JOIN tab_account_finance_subject_map tafsm ON tafsm.crewId =? AND tafsm.financeSubjId = tpfm.financeSubjId ");
		sql.append(" LEFT JOIN tab_account_subject tas ON tafsm.accountSubjId = tas.id AND tas.crewId =? ");
		sql.append(" WHERE ");
		sql.append(" 	tpi.crewId =? ");
		if (!StringUtils.isBlank(paymentDates)) {
			paymentDates = "'" + paymentDates.replace(",", "','") + "'";
			sql.append(" AND tpi.paymentDate in ("+ paymentDates +") ");
		}
		if (!StringUtils.isBlank(payeeNames)) {
			payeeNames = "'" + payeeNames.replace(",", "','") + "'";
			sql.append(" AND tpi.payeeName in(" + payeeNames + ") ");
		}
		if (!StringUtils.isBlank(summary)) {
			sql.append(" AND tpfm.summary like '%"+ summary + "%'");
		}
		if (startMoney != null) {
			sql.append(" AND tpfm.money > " + startMoney);
		}
		if (endMoney != null) {
			sql.append(" AND tpfm.money < " + endMoney);
		}
		sql.append(" AND tfs.crewId = ? ");
		if (!StringUtils.isBlank(finaSubjIds)) {
			finaSubjIds = "'" + finaSubjIds.replace(",", "','") + "'";
			sql.append(" AND tfs.id in (" + finaSubjIds + ") ");
		}
		if (!StringUtils.isBlank(accSubjectCodes)) {
			accSubjectCodes = "'" + accSubjectCodes.replace(",", "','") + "'";
			sql.append(" AND tas.code in("+ accSubjectCodes +") ");
		}
		sql.append(" AND tpfm.crewId =? ");
		sql.append(" AND tpfm.financeSubjId = tfs.id ");
		sql.append(" AND tpfm.paymentId = tpi.paymentId ");
		sql.append(" AND tfpi.wayId = tpi.paymentWay ");
		sql.append(" AND tci.id = tpi.currencyId ");
		sql.append(" ORDER BY paymentDate ");
		
		return this.query(sql.toString(), new Object[] {crewId, crewId, crewId, crewId, crewId}, null);
	}
	
	/**
	 * 查询剧组下的付款单
	 * @param crewId
	 * @return
	 */
	public List<PaymentInfoModel> queryByCrewId(String crewId) {
		String sql = "select * from tab_payment_info where crewId = ? order by paymentDate";
		return this.query(sql, new Object[] {crewId}, PaymentInfoModel.class, null);
	}
	
	
	/**
	 * 根据剧组id   付款信息中的付款日期，付款方名称，金额，摘要
	 * 
	 * @param crewId
	 * @param status
	 * @return
	 */
	public List<Map<String, Object>> queryByCrewIdAndStatus(String crewId) {
		StringBuilder sql  = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("	info.receiptNo,");
		sql.append("    map.mapId, ");
		sql.append("    info.paymentId, ");
		sql.append("    paymentDate, ");
		sql.append("    payeeName, ");
		sql.append("    map.summary, ");
		sql.append("    map.money ");
		sql.append(" FROM ");
		sql.append("    tab_payment_info info ");
		sql.append(" LEFT JOIN tab_payment_finanSubj_map map ON info.paymentId = map.paymentId ");
		sql.append(" WHERE ");
		sql.append("    info.crewid = ? ");
		//sql.append(" and info.status = 0 ");
		return getJdbcTemplate().queryForList(sql.toString(), new Object[] {crewId});
	}
	
	
	/**
	 * 根据合同ID查询付款单
	 * @param contractId
	 * @return	付款时间、单据编号、总额、摘要、结算状态、货币编码
	 */
	public List<Map<String, Object>> queryByContractId(String contractId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tpi.paymentDate, tpi.receiptNo, tpi.totalMoney, GROUP_CONCAT(tpfm.summary) summary, tpi.status, tci.code currencyCode ");
		sql.append(" FROM ");
		sql.append(" 	tab_payment_info tpi, ");
		sql.append(" 	tab_currency_info tci, ");
		sql.append(" 	tab_payment_finanSubj_map tpfm ");
		sql.append(" WHERE ");
		sql.append("   tpi.paymentId = tpfm.paymentId ");
		sql.append(" AND tpi.contractId = ? ");
		sql.append(" AND tpi.currencyId = tci.id ");
//		sql.append(" AND tpi.status = 1  ");
		sql.append(" GROUP BY tpi.paymentDate, tpi.receiptNo, tpi.totalMoney ");
		
		return this.query(sql.toString(), new Object[] {contractId}, null);
	}
	
	/**
	 * 查询最大的付款单编号
	 * 
	 * @param hasReceipt 是否有发票
	 * @param payStatus	付款单编号是否按月重新开始
	 * @param hasReceiptStatus	付款单编号是否分为有票无票
	 * @param moonFirstDay 付款当月第一天
	 * @param moonLastDay 付款当月最后一天
	 * @return
	 */
	public String queryMaxReceiptNo(String crewId, boolean hasReceipt, boolean payStatus, boolean hasReceiptStatus, Date moonFirstDay, Date moonLastDay) {
		List<Object> paramsList = new ArrayList<Object>();
		paramsList.add(crewId);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select max(abs(SUBSTR(receiptNo,5))) from tab_payment_info where 1 = 1 ");
		sql.append(" and crewId = ? ");
		if (hasReceiptStatus) {
			sql.append(" and hasReceipt = ? ");
			if (hasReceipt) {
				paramsList.add(1);
			} else {
				paramsList.add(0);
			}
		}
		if (payStatus) {
			sql.append(" and paymentDate >= ? and paymentDate <= ? ");
			paramsList.add(moonFirstDay);
			paramsList.add(moonLastDay);
		}
		
		return this.getJdbcTemplate().queryForObject(sql.toString(), paramsList.toArray(), String.class);
	}
	
	/**
	 * 根据ID查询付款单
	 * @param paymentId
	 * @return
	 * @throws Exception 
	 */
	public PaymentInfoModel queryById(String paymentId) throws Exception {
		String sql = "select * from " + PaymentInfoModel.TABLE_NAME + " where paymentId = ? ";
		return this.queryForObject(sql, new Object[] {paymentId}, PaymentInfoModel.class);
	}
	
	/**
	 * 根据ID查询付款单
	 * @param paymentId
	 * @return
	 * @throws Exception 
	 */
	public List<PaymentInfoModel> queryByIds(String paymentIds) throws Exception {
		paymentIds = "'" + paymentIds.replace(",", "','") + "'";
		String sql = "select * from " + PaymentInfoModel.TABLE_NAME + " where paymentId in ("+ paymentIds +") ";
		return this.query(sql, new Object[] {}, PaymentInfoModel.class, null);
	}
	
	/**
	 * 查询剧组中付款单信息
	 * 该查询主要用于账务详情中付款单信息的查询
	 * @param crewId	剧组ID
	 * @return	付款日期，创建时间，票据编码，摘要，财务科目ID，财务科目名称，总金额，
	 *  结算状态，收款人，付款方式，是否有发票，单据张数，记账人，付款单关联货币ID，付款单关联货币编码，货币汇率
	 *  合同相关信息
	 */
	public List<Map<String, Object>> queryPaymentList(String crewId, PaymentInfoFilter paymentInfoFilter) {
		List<Object> paramsList = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT  ");
		sql.append(" 	GROUP_CONCAT(tplm.loanId) loanIds, ");
		sql.append(" SUM(tplm.repaymentMoney - tplm.loanBalance) forLoanMoney, ");
		sql.append(" 	tmp.* ");
		sql.append("  from (SELECT ");
		sql.append(" 	tpi.paymentId, ");
		sql.append(" 	tpi.paymentDate, ");
		sql.append(" 	tpi.createTime, ");
		sql.append(" 	tpi.receiptNo, ");
		sql.append(" 	tpi.department, ");
		sql.append(" 	GROUP_CONCAT(DISTINCT tpfm.summary) summary, ");
//		sql.append(" 	GROUP_CONCAT(DISTINCT if(tpfm.summary is null or tpfm.summary='', ' ', tpfm.summary) SEPARATOR '&&') formatSummary, ");
		sql.append(" 	GROUP_CONCAT(tpfm.financeSubjId) financeSubjIds, ");
//		sql.append(" 	GROUP_CONCAT(tpfm.money) financeSubjMoneys, ");
		sql.append("    GROUP_CONCAT(tpfm.financeSubjId, '&&', tpfm.money, '&&', if(tpfm.summary is null or tpfm.summary='', ' ', tpfm.summary) SEPARATOR '##') financeSubjInfo, ");
		
		sql.append(" 	GROUP_CONCAT( ");
		sql.append(" 		tpfm.financeSubjName ");
		sql.append(" 	) financeSubjNames, ");
		sql.append(" 	tpi.totalMoney, ");
		sql.append(" 	tpi.`status`, ");
		sql.append(" 	tpi.payeeName, ");
		sql.append(" 	tfpi.wayName paymentWay, ");
		sql.append(" 	tpi.hasReceipt, ");
		sql.append(" 	tpi.billCount, ");
		sql.append(" 	tpi.agent, ");
		sql.append(" 	tpi.remindTime, ");
		sql.append(" 	tpi.contractType, ");
		sql.append(" 	tci.id currencyId, ");
		sql.append(" 	tci.`code` currencyCode, ");
		sql.append(" 	tci.`name` currencyName, ");
		sql.append(" 	tci.exchangeRate, ");
		sql.append(" 	tca.contractNo acontractNo, ");
		sql.append(" 	tca.actorName, ");
		sql.append(" 	tcw.contractNo wcontractNo, ");
		sql.append(" 	tcw.workerName, ");
		sql.append(" 	tcp.contractNo pcontractNo, ");
		sql.append(" 	tcp.company, ");
		sql.append("	tpi.billType ");
		sql.append(" FROM ");
		sql.append(" 	tab_payment_info tpi ");
		sql.append(" LEFT JOIN tab_contract_actor tca ON tca.contractId = tpi.contractId ");
		sql.append(" LEFT JOIN tab_contract_worker tcw ON tcw.contractId = tpi.contractId ");
		sql.append(" LEFT JOIN tab_contract_produce tcp ON tcp.contractId = tpi.contractId, ");
		sql.append("  tab_payment_finanSubj_map tpfm, ");
		sql.append("  tab_finance_paymentWay_info tfpi, ");
		sql.append("  tab_currency_info tci ");
		sql.append(" WHERE ");
		sql.append(" 	tpi.crewId = ? ");
		paramsList.add(crewId);
		sql.append(" AND tpi.paymentId = tpfm.paymentId ");
		sql.append(" AND tfpi.wayId = tpi.paymentWay ");
		sql.append(" AND tci.id = tpi.currencyId ");
		
		//付款单ID
		if (!StringUtils.isBlank(paymentInfoFilter.getPaymentIds())) {
			String paymentIds = "'" + paymentInfoFilter.getPaymentIds().replace(",", "','") + "'";
			
			sql.append(" AND tpi.paymentId in ("+ paymentIds +") ");
		}
		
		//财务科目
//		if (!StringUtils.isBlank(paymentInfoFilter.getFinanceSubjIds())) {
//			String financeSubjIds = "'" + paymentInfoFilter.getFinanceSubjIds().replace(",", "','") + "'";
//			
//			sql.append(" AND tpfm.financeSubjId in ("+ financeSubjIds +") ");
//		}
		
		//收款人
		if (!StringUtils.isBlank(paymentInfoFilter.getPayeeNames())) {
			String payeeNames = "'" + paymentInfoFilter.getPayeeNames().replace(",", "','") + "'";
			
			sql.append(" AND tpi.payeeName in ("+ payeeNames +") ");
		}
		
		//记账人
		if (!StringUtils.isBlank(paymentInfoFilter.getAgents())) {
			String agents = "'" + paymentInfoFilter.getAgents().replace(",", "','") + "'";
			
			sql.append(" AND tpi.agent in ("+ agents +") ");
		}
		
		//付款日期
		if (!StringUtils.isBlank(paymentInfoFilter.getPaymentDates())) {
			String paymentDates = "'" + paymentInfoFilter.getPaymentDates().replace(",", "','") + "'";
			
			sql.append(" AND tpi.paymentDate in ("+ paymentDates +") ");
		}
		
		//最小付款日期
		if (!StringUtils.isBlank(paymentInfoFilter.getStartPaymentDate())) {
			paramsList.add(paymentInfoFilter.getStartPaymentDate());
			sql.append(" AND tpi.paymentDate >= ? ");
		}
		
		//最大付款日期
		if (!StringUtils.isBlank(paymentInfoFilter.getEndPaymentDate())) {
			paramsList.add(paymentInfoFilter.getEndPaymentDate());
			sql.append(" AND tpi.paymentDate <= ? ");
		}
		
		//付款月份
		if (!StringUtils.isBlank(paymentInfoFilter.getPaymentMonth())) {
			paramsList.add(paymentInfoFilter.getPaymentMonth());
			sql.append(" AND DATE_FORMAT(tpi.paymentDate,'%Y年%m月') = ? ");
		}
		
		//是否有发票
		if (paymentInfoFilter.getHasReceipt() != null) {
			if (paymentInfoFilter.getHasReceipt()) {
				sql.append(" AND tpi.hasReceipt = 1 ");
			} else {
				sql.append(" AND tpi.hasReceipt = 0 ");
			}
		}
		
		//结算状态
		if (paymentInfoFilter.getStatus() != null && paymentInfoFilter.getStatus() == PaymentStatus.Settled.getValue()) {
			sql.append(" AND tpi.`status` = 1 ");
		}
		if (paymentInfoFilter.getStatus() != null && paymentInfoFilter.getStatus() == PaymentStatus.NotSettle.getValue()) {
			sql.append(" AND tpi.`status` = 0 ");
		}
		
		//摘要
		if (!StringUtils.isBlank(paymentInfoFilter.getSummary())) {
			sql.append(" AND tpfm.summary like ? ");
			paramsList.add("%" + paymentInfoFilter.getSummary() + "%");
		}
		
		
		//金额区间
		if (paymentInfoFilter.getMinMoney() != null) {
			sql.append(" AND tpi.totalMoney >= ? ");
			paramsList.add(paymentInfoFilter.getMinMoney());
		}
		if (paymentInfoFilter.getMaxMoney() != null) {
			sql.append(" AND tpi.totalMoney <= ? ");
			paramsList.add(paymentInfoFilter.getMaxMoney());
		}
		
		//付款方式
		if (!StringUtils.isBlank(paymentInfoFilter.getPaymentWayId())) {
			sql.append(" AND tpi.paymentWay = ? ");
			paramsList.add(paymentInfoFilter.getPaymentWayId());
		}
		//票据类型
		if (paymentInfoFilter.getBillType() != null) {
			sql.append(" AND tpi.billType = ? ");
			paramsList.add(paymentInfoFilter.getBillType());
		}
		
		sql.append(" GROUP BY ");
		sql.append(" 	tpi.paymentDate, ");
		sql.append(" 	tpi.receiptNo, ");
		sql.append(" 	tpi.totalMoney  ");
		//财务科目
		if (!StringUtils.isBlank(paymentInfoFilter.getFinanceSubjIds())) {
			String[] financeSubjIdsArray = paymentInfoFilter.getFinanceSubjIds().split(",");
			sql.append(" having ");
			for (int i = 0; i < financeSubjIdsArray.length; i++) {
				String myFinanceSubjId = financeSubjIdsArray[i];
				if (i == 0) {
					sql.append(" locate(? ,GROUP_CONCAT(tpfm.financeSubjId)) != 0 ");
				} else {
					sql.append(" or locate(? ,GROUP_CONCAT(tpfm.financeSubjId)) != 0 ");
				}
				paramsList.add(myFinanceSubjId);
			}
		}
		sql.append("     ) tmp ");
		sql.append(" LEFT JOIN tab_payment_loan_map tplm ON tmp.paymentId = tplm.paymentId ");
		sql.append(" LEFT JOIN tab_loan_info tli ON tli.loanId=tplm.loanId ");
		if (!StringUtils.isBlank(paymentInfoFilter.getLoanerName())) {
			sql.append(" and tli.payeeName = ? ");
			paramsList.add(paymentInfoFilter.getLoanerName());
		}
		sql.append(" GROUP BY tmp.paymentId ");
		sql.append(" ORDER BY tmp.createTime ");
		
		return this.query(sql.toString(), paramsList.toArray(), null);
	}
	
	/**
	 * 查询付款单中币种统计信息
	 * @param crewId
	 * @param paymentInfoFilter
	 * @return	币种ID，币种编码，总支出，总还借款的金额
	 */
	public List<Map<String, Object>> queryPaymentStatistic(String crewId, PaymentInfoFilter paymentInfoFilter) {
		List<Object> paramsList = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tmp2.currencyId, ");
		sql.append(" 	tmp2.currencyCode, ");
		sql.append(" 	tmp2.exchangeRate, ");
		sql.append(" 	sum(tmp2.totalMoney) totalPayedMoney, ");
		sql.append(" 	round(sum(tmp2.forLoanMoney), 2) forLoanMoney ");
		sql.append(" FROM ( ");
		sql.append(" SELECT  ");
		sql.append(" 	GROUP_CONCAT(tplm.loanId) loanIds, ");
		sql.append(" SUM(tplm.repaymentMoney - tplm.loanBalance) forLoanMoney, ");
		sql.append(" 	tmp.* ");
		sql.append("  from (SELECT ");
		sql.append(" 	tpi.paymentId, ");
		sql.append(" 	tpi.paymentDate, ");
		sql.append(" 	tpi.createTime, ");
		sql.append(" 	tpi.receiptNo, ");
		sql.append(" 	GROUP_CONCAT(DISTINCT tpfm.summary) summary, ");
		sql.append(" 	GROUP_CONCAT(distinct tpfm.financeSubjId) financeSubjIds, ");
		sql.append(" 	GROUP_CONCAT( ");
		sql.append(" 		tpfm.financeSubjName ");
		sql.append(" 	) financeSubjNames, ");
		sql.append(" 	tpi.totalMoney, ");
		sql.append(" 	tpi.`status`, ");
		sql.append(" 	tpi.payeeName, ");
		sql.append(" 	tfpi.wayName paymentWay, ");
		sql.append(" 	tpi.hasReceipt, ");
		sql.append(" 	tpi.billCount, ");
		sql.append(" 	tpi.agent, ");
		sql.append(" 	tpi.remindTime, ");
		sql.append(" 	tpi.contractType, ");
		sql.append(" 	tci.id currencyId, ");
		sql.append(" 	tci.`code` currencyCode, ");
		sql.append(" 	tci.`name` currencyName, ");
		sql.append(" 	tci.exchangeRate, ");
		sql.append(" 	tca.contractNo acontractNo, ");
		sql.append(" 	tca.actorName, ");
		sql.append(" 	tcw.contractNo wcontractNo, ");
		sql.append(" 	tcw.workerName, ");
		sql.append(" 	tcp.contractNo pcontractNo, ");
		sql.append(" 	tcp.company ");
		sql.append(" FROM ");
		sql.append(" 	tab_payment_info tpi ");
		sql.append(" LEFT JOIN tab_contract_actor tca ON tca.contractId = tpi.contractId ");
		sql.append(" LEFT JOIN tab_contract_worker tcw ON tcw.contractId = tpi.contractId ");
		sql.append(" LEFT JOIN tab_contract_produce tcp ON tcp.contractId = tpi.contractId, ");
		sql.append("  tab_payment_finanSubj_map tpfm, ");
		sql.append("  tab_finance_paymentWay_info tfpi, ");
		sql.append("  tab_currency_info tci ");
		sql.append(" WHERE ");
		sql.append(" 	tpi.crewId = ? ");
		paramsList.add(crewId);
		sql.append(" AND tpi.paymentId = tpfm.paymentId ");
		sql.append(" AND tfpi.wayId = tpi.paymentWay ");
		sql.append(" AND tci.id = tpi.currencyId ");
		
		//付款单ID
		if (!StringUtils.isBlank(paymentInfoFilter.getPaymentIds())) {
			String paymentIds = "'" + paymentInfoFilter.getPaymentIds().replace(",", "','") + "'";
			
			sql.append(" AND tpi.paymentId in ("+ paymentIds +") ");
		}
		
		//财务科目
		if (!StringUtils.isBlank(paymentInfoFilter.getFinanceSubjIds())) {
			String financeSubjIds = "'" + paymentInfoFilter.getFinanceSubjIds().replace(",", "','") + "'";
			
			sql.append(" AND tpfm.financeSubjId in ("+ financeSubjIds +") ");
		}
		
		//收款人
		if (!StringUtils.isBlank(paymentInfoFilter.getPayeeNames())) {
			String payeeNames = "'" + paymentInfoFilter.getPayeeNames().replace(",", "','") + "'";
			
			sql.append(" AND tpi.payeeName in ("+ payeeNames +") ");
		}
		
		//记账人
		if (!StringUtils.isBlank(paymentInfoFilter.getAgents())) {
			String agents = "'" + paymentInfoFilter.getAgents().replace(",", "','") + "'";
			
			sql.append(" AND tpi.agent in ("+ agents +") ");
		}
		
		//付款日期
		if (!StringUtils.isBlank(paymentInfoFilter.getPaymentDates())) {
			String paymentDates = "'" + paymentInfoFilter.getPaymentDates().replace(",", "','") + "'";
			
			sql.append(" AND tpi.paymentDate in ("+ paymentDates +") ");
		}
		
		//最小付款日期
		if (!StringUtils.isBlank(paymentInfoFilter.getStartPaymentDate())) {
			paramsList.add(paymentInfoFilter.getStartPaymentDate());
			sql.append(" AND tpi.paymentDate >= ? ");
		}
		
		//最大付款日期
		if (!StringUtils.isBlank(paymentInfoFilter.getEndPaymentDate())) {
			paramsList.add(paymentInfoFilter.getEndPaymentDate());
			sql.append(" AND tpi.paymentDate <= ? ");
		}
		
		//付款月份
		if (!StringUtils.isBlank(paymentInfoFilter.getPaymentMonth())) {
			paramsList.add(paymentInfoFilter.getPaymentMonth());
			sql.append(" AND DATE_FORMAT(tpi.paymentDate,'%Y年%m月') = ? ");
		}
		
		//是否有发票
		if (paymentInfoFilter.getHasReceipt() != null) {
			if (paymentInfoFilter.getHasReceipt()) {
				sql.append(" AND tpi.hasReceipt = 1 ");
			} else {
				sql.append(" AND tpi.hasReceipt = 0 ");
			}
		}
		
		//结算状态
		if (paymentInfoFilter.getStatus() != null && paymentInfoFilter.getStatus() == PaymentStatus.Settled.getValue()) {
			sql.append(" AND tpi.`status` = 1 ");
		}
		if (paymentInfoFilter.getStatus() != null && paymentInfoFilter.getStatus() == PaymentStatus.NotSettle.getValue()) {
			sql.append(" AND tpi.`status` = 0 ");
		}
		
		//摘要
		if (!StringUtils.isBlank(paymentInfoFilter.getSummary())) {
			sql.append(" AND tpfm.summary like ? ");
			paramsList.add("%" + paymentInfoFilter.getSummary() + "%");
		}
		
		
		//金额区间
		if (paymentInfoFilter.getMinMoney() != null) {
			sql.append(" AND tpi.totalMoney >= ? ");
			paramsList.add(paymentInfoFilter.getMinMoney());
		}
		if (paymentInfoFilter.getMaxMoney() != null) {
			sql.append(" AND tpi.totalMoney <= ? ");
			paramsList.add(paymentInfoFilter.getMaxMoney());
		}
		
		//付款方式
		if (!StringUtils.isBlank(paymentInfoFilter.getPaymentWayId())) {
			sql.append(" AND tpi.paymentWay = ? ");
			paramsList.add(paymentInfoFilter.getPaymentWayId());
		}
		
		sql.append(" GROUP BY ");
		sql.append(" 	tpi.paymentDate, ");
		sql.append(" 	tpi.receiptNo, ");
		sql.append(" 	tpi.totalMoney) tmp  ");
		sql.append(" LEFT JOIN tab_payment_loan_map tplm ON tmp.paymentId = tplm.paymentId ");
		sql.append(" LEFT JOIN tab_loan_info tli ON tli.loanId=tplm.loanId ");
		if (!StringUtils.isBlank(paymentInfoFilter.getLoanerName())) {
			sql.append(" and tli.payeeName = ? ");
			paramsList.add(paymentInfoFilter.getLoanerName());
		}
		sql.append(" GROUP BY tmp.paymentId) tmp2 GROUP BY tmp2.currencyId, tmp2.currencyCode ");
		
		return this.query(sql.toString(), paramsList.toArray(), null);
	}
	
	/**
	 * 批量结算付款单
	 * @param paymentIds
	 */
	public void settleBatchPaymentList(String paymentIds) {
		paymentIds = "'" + paymentIds.replace(",", "','") + "'";
		String sql = "update " + PaymentInfoModel.TABLE_NAME + " set status = "+ PaymentStatus.Settled.getValue() +" where paymentId in("+ paymentIds +") ";
		
		this.getJdbcTemplate().update(sql);
	}
	
	/**
	 * 设置付款单有票
	 * @param paymentIds
	 */
	public void setPaymentHasReceiptBatch(String paymentIds) {
		paymentIds = "'" + paymentIds.replace(",", "','") + "'";
		String sql = "update " + PaymentInfoModel.TABLE_NAME + " set hasReceipt = 1 where paymentId in("+ paymentIds +")";
		
		this.getJdbcTemplate().update(sql);
	}
	
	/**
	 * 查询跟借款单关联的付款单信息
	 * @param crewId
	 * @param loanId
	 * @return	付款单ID，付款单编号，摘要，关联的财务科目，已付金额，借款余额
	 */
	public List<Map<String, Object>> queryByLoanId(String loanId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tpi.paymentId, ");
		sql.append(" 	tpi.receiptNo, ");
		sql.append(" 	GROUP_CONCAT(tpfm.summary) summary, ");
		sql.append(" 	GROUP_CONCAT(tfs.`name`) financeSubjName, ");
		sql.append(" 	tpi.totalMoney payedMoney, ");
		sql.append("    tci.code currencyCode, ");
		sql.append(" 	tplm.loanBalance, ");
		sql.append(" 	round(tplm.repaymentMoney - tplm.loanBalance, 2) forLoanMoney ");
		sql.append(" FROM ");
		sql.append(" 	tab_payment_info tpi, ");
		sql.append(" 	tab_payment_loan_map tplm, ");
		sql.append(" 	tab_payment_finanSubj_map tpfm, ");
		sql.append(" 	tab_finance_subject tfs, ");
		sql.append("    tab_currency_info tci ");
		sql.append(" WHERE ");
		sql.append(" 	tpi.paymentId = tplm.paymentId ");
		sql.append(" AND tpi.paymentId = tpfm.paymentId ");
		sql.append(" AND tpfm.financeSubjId = tfs.id ");
		sql.append(" AND tplm.loanId = ? ");
		sql.append(" AND tci.id = tpi.currencyId ");
		sql.append(" GROUP BY ");
		sql.append(" tpi.receiptNo ");
		sql.append(" ORDER BY tplm.createTime  ");
		
		return this.query(sql.toString(), new Object[] {loanId}, null);
	}
	
	/**
	 * 删除合同对应的付款单信息
	 * @param contractType
	 * @param contractId
	 */
	public void deleteByContractInfo(int contractType, String contractId) {
		String sql = "delete from " + PaymentInfoModel.TABLE_NAME + " where contractType = ? and contractId = ?";
		this.getJdbcTemplate().update(sql, contractType, contractId);
	}
	
	/**
	 * 查询付款单中的部门
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryPaymentDepartmentList(String crewId){
		String sql = " SELECT DISTINCT IF(department IS NULL,'',department) department FROM tab_payment_info WHERE crewId = ?";
		return this.query(sql, new Object[] {crewId}, null);
	}
}
