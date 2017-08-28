package com.xiaotu.makeplays.finance.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.finance.controller.filter.CollectionInfoFilter;
import com.xiaotu.makeplays.finance.controller.filter.LoanInfoFilter;
import com.xiaotu.makeplays.finance.controller.filter.PaymentInfoFilter;
import com.xiaotu.makeplays.finance.model.PaymentFinanSubjMapModel;
import com.xiaotu.makeplays.finance.model.constants.PaymentStatus;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

@Repository
public class GetCostDao extends BaseDao<T>{
	
	/**
	 * 根据合同号查询   演员合同表、 职员合同表、制作合同表
	 * @param contractId
	 * @return
	 */
	public List<Map<String, Object>> queryContractInfoForContractType(String crewId,String contractno,String tabSuffix){
		StringBuilder sql = new StringBuilder();
		sql.append(" select contractid "+tabSuffix +" from tab_contract_"+tabSuffix +" where contractno = ? and crewid = ? ");
		return this.getJdbcTemplate().queryForList(sql.toString(), new Object[]{contractno,crewId});
	}
	
	/**
	 * 查询财务流水账信息（含有付款、借款、收款信息）
	 * 放在一个sql中便于分页
	 * @param crewId
	 * @param paymentInfoFilter	付款单过滤条件
	 * @param collectionFilter	收款单过滤条件
	 * @param loanInfoFilter	借款单过滤条件
	 * @param includePayment	是否包含付款单
	 * @param includeCollection	是否包含收款单
	 * @param includeLoan	是否包含借款单
	 * @param isASC 是否按照票据日期和创建日期升序排列
	 * @return 
	 * receiptId 单据ID
	 * receiptDate	单据日期
	 * createTime 单据创建日期
	 * receiptNo	单据编号
	 * summary	摘要
	 * financeSubjId	财务科目ID
	 * financeSubjName	财务科目名称
	 * collectMoney	收款金额
	 * payedMoney	付款金额
	 * status	付款单状态， '0'：未结算；'1'：已结算；'/'：无意义
	 * formType	单据类型， 1-付款单  2-收款单  3-借款单
	 * aimPersonName	收/付/借款人
	 * paymentWay	支付方式
	 * hasReceipt	付款单是否有发票，'0'：没有；'1'：有；'/'：无意义
	 * billCount  	付款单票据张数，'/'：无意义
	 * agent	记账人
	 * currencyId	币种ID
	 * currencyCode	币种编码
	 * currencyName	币种名称
	 * exchangeRate	币种汇率
	 * contractNo	关联的合同编码
	 * contractName	关联的合同名称
	 * @param isQueryFinanceSubjPayment 是否是查询财务科目支付明细
	 */
	public List<Map<String, Object>> queryFinanceRunningAccount(String crewId, boolean includePayment, 
			boolean includeCollection, boolean includeLoan, 
			PaymentInfoFilter paymentInfoFilter,  
			CollectionInfoFilter collectionFilter, 
			LoanInfoFilter loanInfoFilter, Page page, boolean isASC, Integer sortType) {
		List<Object> paramsList = new ArrayList<Object>();
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" select  ");
		sql.append(" '' loanIds,");
		sql.append(" 0 forLoanMoney,");
		sql.append(" '' receiptId, ");
		sql.append(" null receiptDate, ");
		sql.append(" null createTime, ");
		sql.append(" '' receiptNo, ");
		sql.append(" '' summary, ");
		sql.append(" '' financeSubjId, ");
		sql.append(" '' financeSubjName, ");
		sql.append(" '' financeSubjMoney, ");
		sql.append(" 0 collectMoney, ");
		sql.append(" 0 payedMoney, ");
		sql.append(" '' department, ");
		sql.append(" '' status, ");
		sql.append(" 0 formType, ");
		sql.append(" '' aimPersonName, ");
		sql.append(" '' paymentWay, ");
		sql.append(" '' hasReceipt, ");
		sql.append(" 0 billCount, ");
		sql.append(" null billType, ");
		sql.append(" '' agent, ");
		sql.append(" '' currencyId, ");
		sql.append(" '' currencyCode, ");
		sql.append(" '' currencyName, ");
		sql.append(" 0 exchangeRate, ");
		sql.append(" '' contractId, ");
		sql.append(" '' contractType, ");
		sql.append(" '' contractNo, ");
		sql.append(" '' contractName from dual where 1=0 ");
		
		if (includePayment) {
			sql.append(" UNION ALL ");
			/*
			 * 付款单
			 */
			sql.append(" SELECT  ");
			sql.append(" 	GROUP_CONCAT(tplm.loanId) loanIds, ");
			sql.append(" SUM(tplm.repaymentMoney - tplm.loanBalance) forLoanMoney, ");
			sql.append(" 	tmp.* ");
			sql.append(" 	from (SELECT ");
			sql.append(" 		tpi.paymentId receiptId, ");
			sql.append(" 		tpi.paymentDate receiptDate, ");
			sql.append(" 		tpi.createTime, ");
			sql.append(" 		tpi.receiptNo, ");
			sql.append(" 		GROUP_CONCAT(DISTINCT if(tpfm.summary = '', null, tpfm.summary) order BY tpfm.mapId  SEPARATOR ' | ') summary, ");
			sql.append(" 		GROUP_CONCAT(tpfm.financeSubjId) financeSubjId, ");
			sql.append(" 		GROUP_CONCAT(tpfm.financeSubjName) financeSubjName, ");
			sql.append(" 		GROUP_CONCAT(tpfm.money) financeSubjMoney, ");
			sql.append(" 		0 collectMoney, ");
			sql.append(" 		sum(tpfm.money) payedMoney, ");
			sql.append(" 		tpi.department department, ");
			sql.append(" 		tpi.`status` + '' status, ");
			sql.append(" 		1 formType, ");
			sql.append(" 		tpi.payeeName aimPersonName, ");
			sql.append(" 		tfpi.wayName paymentWay, ");
			sql.append(" 		tpi.hasReceipt + '' hasReceipt, ");
			sql.append(" 		tpi.billCount, ");
			sql.append(" 		tpi.billType,");
			sql.append(" 		tpi.agent, ");
			sql.append(" 		tci.id currencyId, ");
			sql.append(" 		tci.`code` currencyCode, ");
			sql.append(" 		tci.`name` currencyName, ");
			sql.append(" 		tci.exchangeRate, ");
			sql.append(" 		tpi.contractId, ");
			sql.append(" 		tpi.contractType, ");
			sql.append(" 		if(tca.contractNo is NOT NULL, tca.contractNo, if(tcw.contractNo is not null, tcw.contractNo, if(tcp.contractNo is not null, tcp.contractNo, ''))) contractNo, ");
			sql.append(" 		if(tca.actorName is NOT NULL, tca.actorName, if(tcw.workerName is not null, tcw.workerName, if(tcp.company is not null, tcp.company, ''))) contractName ");
			sql.append(" 	FROM ");
			sql.append(" 		tab_payment_info tpi ");
			sql.append(" 	LEFT JOIN tab_contract_actor tca ON tca.contractId = tpi.contractId ");
			sql.append(" 	LEFT JOIN tab_contract_worker tcw ON tcw.contractId = tpi.contractId ");
			sql.append(" 	LEFT JOIN tab_contract_produce tcp ON tcp.contractId = tpi.contractId, ");
			sql.append(" 	tab_payment_finanSubj_map tpfm, ");
			sql.append(" 	tab_finance_paymentWay_info tfpi, ");
			sql.append(" 	tab_currency_info tci ");
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
			
//			//财务科目
//			if (!StringUtils.isBlank(paymentInfoFilter.getFinanceSubjIds())) {
//				String financeSubjIds = "'" + paymentInfoFilter.getFinanceSubjIds().replace(",", "','") + "'";
//				
//				sql.append(" AND tpfm.financeSubjId in ("+ financeSubjIds +") ");
//			}
			
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
			
			//部门
			if (!StringUtils.isBlank(paymentInfoFilter.getDepartment())) {
				String departments = "'" + paymentInfoFilter.getDepartment().replace(",", "','") + "'";
				
				sql.append(" AND tpi.department in ("+ departments +") ");
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
			//查询某个财务科目支付明细使用
			if(paymentInfoFilter.isQueryFinanceSubjPayment() && !StringUtils.isBlank(paymentInfoFilter.getFinanceSubjIds())) {
				String[] financeSubjIdsArray = paymentInfoFilter.getFinanceSubjIds().split(",");
				sql.append(" AND ( ");
				for (int i = 0; i < financeSubjIdsArray.length; i++) {
					String myFinanceSubjId = financeSubjIdsArray[i];
					if (i == 0) {
						sql.append(" tpfm.financeSubjId = ? ");
					} else {
						sql.append(" or tpfm.financeSubjId = ? ");
					}
					paramsList.add(myFinanceSubjId);
				}
				sql.append(" ) ");
			}
			sql.append(" GROUP BY ");
			sql.append(" 	tpi.paymentDate, ");
			sql.append(" 	tpi.receiptNo, ");
			sql.append(" 	tpi.totalMoney ");
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
			sql.append(" ) tmp ");
			sql.append(" LEFT JOIN tab_payment_loan_map tplm ON tmp.receiptId = tplm.paymentId ");
			sql.append(" LEFT JOIN tab_loan_info tli ON tli.loanId=tplm.loanId ");
			sql.append(" GROUP BY tmp.receiptId ");
		}
		
		/*
		 * 收款单
		 */
		if (includeCollection) {
			sql.append(" UNION ALL ");
			
			sql.append(" SELECT ");
			sql.append("    '' loanIds, ");
			sql.append("    0 forLoanMoney, ");
			sql.append(" 	tci.collectionId receiptId, ");
			sql.append(" 	tci.collectionDate receiptDate, ");
			sql.append(" 	tci.createTime, ");
			sql.append(" 	tci.receiptNo, ");
			sql.append(" 	tci.summary, ");
			sql.append(" 	'' financeSubjId, ");
			sql.append(" 	'' financeSubjName, ");
			sql.append(" 	'' financeSubjMoney, ");
			sql.append(" 	tci.money collectMoney, ");
			sql.append(" 	0 payedMoney, ");
			sql.append("	'' department, ");
			sql.append(" 	'/' status, ");
			sql.append(" 	2 formType, ");
			sql.append(" 	tci.otherUnit aimPersonName, ");
			sql.append(" 	tfpi.wayName paymentWay, ");
			sql.append(" 	'/' hasReceipt, ");
			sql.append(" 	'/' billCount, ");
			sql.append(" 	null billType, ");
			sql.append(" 	tci.agent, ");
			sql.append(" 	tcci.id currencyId, ");
			sql.append(" 	tcci. CODE currencyCode, ");
			sql.append(" 	tcci. NAME currencyName, ");
			sql.append(" 	tcci.exchangeRate, ");
			sql.append(" 	'' contractId, ");
			sql.append(" 	'' contractType, ");
			sql.append(" 	'' contractNo, ");
			sql.append(" 	'' contractName ");
			sql.append(" FROM ");
			sql.append(" 	tab_collection_info tci, ");
			sql.append(" 	tab_finance_paymentWay_info tfpi, ");
			sql.append(" 	tab_currency_info tcci ");
			sql.append(" WHERE ");
			sql.append(" 	tci.crewId = ? ");
			paramsList.add(crewId);
			sql.append(" AND tcci.id = tci.currencyId ");
			sql.append(" AND tci.paymentWay = tfpi.wayId ");
			
			if (!StringUtils.isBlank(collectionFilter.getCollectionIds())) {
				String collectionIds = "'" + collectionFilter.getCollectionIds().replace(",", "','") + "'";
				
				sql.append(" AND tci.collectionId in ("+ collectionIds +") ");
			}
			
			if (!StringUtils.isBlank(collectionFilter.getOtherUnits())) {
				String otherUnits = "'" + collectionFilter.getOtherUnits().replace(",", "','") + "'";
				
				sql.append(" AND tci.otherUnit in ("+ otherUnits +") ");
			}
			
			if (!StringUtils.isBlank(collectionFilter.getCollectionDates())) {
				String collectionDates = "'" + collectionFilter.getCollectionDates().replace(",", "','") + "'";
				
				sql.append(" AND tci.collectionDate in ("+ collectionDates +") ");
			}
			
			if (!StringUtils.isBlank(collectionFilter.getStartCollectionDate())) {
				paramsList.add(collectionFilter.getStartCollectionDate());
				sql.append(" AND tci.collectionDate >= ? ");
			}
			
			if (!StringUtils.isBlank(collectionFilter.getEndCollectionDate())) {
				paramsList.add(collectionFilter.getEndCollectionDate());
				sql.append(" AND tci.collectionDate <= ? ");
			}
			
			//收款月份
			if (!StringUtils.isBlank(collectionFilter.getCollectionMonth())) {
				paramsList.add(collectionFilter.getCollectionMonth());
				sql.append(" AND DATE_FORMAT(tci.collectionDate,'%Y年%m月') = ? ");
			}
			
			if (!StringUtils.isBlank(collectionFilter.getAgents())) {
				String agents = "'" + collectionFilter.getAgents().replace(",", "','") + "'";
				
				sql.append(" AND tci.agent in ("+ agents +") ");
			}
			
			if (!StringUtils.isBlank(collectionFilter.getSummary())) {
				sql.append(" AND tci.summary like ? ");
				paramsList.add("%" + collectionFilter.getSummary() + "%");
			}
			
			if (collectionFilter.getMinMoney() != null) {
				sql.append(" AND tci.money >= ? ");
				paramsList.add(collectionFilter.getMinMoney());
			}
			
			if (collectionFilter.getMaxMoney() != null) {
				sql.append(" AND tci.money <= ? ");
				paramsList.add(collectionFilter.getMaxMoney());
			}
			
			//付款方式
			if (!StringUtils.isBlank(collectionFilter.getPaymentWayId())) {
				sql.append(" AND tci.paymentWay = ? ");
				paramsList.add(collectionFilter.getPaymentWayId());
			}
		}
		
		
		/*
		 * 借款单 
		 */
		if (includeLoan) {
			sql.append(" UNION ALL ");
			
			sql.append(" SELECT ");
			sql.append("    '' loanIds, ");
			sql.append("    0 forLoanMoney, ");
			sql.append(" 	tli.loanId receiptId, ");
			sql.append(" 	tli.loanDate receiptDate, ");
			sql.append(" 	tli.createTime, ");
			sql.append(" 	tli.receiptNo, ");
			sql.append(" 	tli.summary, ");
			sql.append(" 	tli.financeSubjId, ");
			sql.append(" 	tli.financeSubjName, ");
			sql.append(" 	tli.money financeSubjMoney, ");
			sql.append(" 	0 collectMoney, ");
			sql.append(" 	tli.money payedMoney, ");
			sql.append("	'' department, ");
			sql.append(" 	'/' status, ");
			sql.append(" 	3 formType, ");
			sql.append(" 	tli.payeeName aimPersonName, ");
			sql.append(" 	tfpi.wayName paymentWay, ");
			sql.append(" 	'/' hasReceipt, ");
			sql.append(" 	'/' billCount, ");
			sql.append(" 	null billType, ");
			sql.append(" 	tli.agent, ");
			sql.append(" 	tci.id currencyId, ");
			sql.append(" 	tci. CODE currencyCode, ");
			sql.append(" 	tci. NAME currencyName, ");
			sql.append(" 	tci.exchangeRate, ");
			sql.append(" 	'' contractId, ");
			sql.append(" 	'' contractType, ");
			sql.append(" 	'' contractNo, ");
			sql.append(" 	'' contractName ");
			sql.append(" FROM ");
			sql.append(" 	tab_loan_info tli, ");
			sql.append(" 	tab_currency_info tci, ");
			sql.append("    tab_finance_paymentWay_info tfpi ");
			sql.append(" WHERE ");
			sql.append(" 	tli.crewId = ? ");
			paramsList.add(crewId);
			sql.append(" AND tci.id = tli.currencyId ");
			sql.append(" AND tfpi.wayId = CONCAT(tli.paymentWay, '') ");
			

			if (!StringUtils.isBlank(loanInfoFilter.getLoanIds())) {
				String loanIds = "'" + loanInfoFilter.getLoanIds().replace(",", "','") + "'";
				
				sql.append(" AND tli.loanId in ("+ loanIds +") ");
			}
			
			//财务科目
			if (!StringUtils.isBlank(loanInfoFilter.getFinanceSubjIds())) {
				String financeSubjIds = "'" + loanInfoFilter.getFinanceSubjIds().replace(",", "','") + "'";
				
				sql.append(" AND tli.financeSubjId in ("+ financeSubjIds +") ");
			}
			
			//借款人
			if (!StringUtils.isBlank(loanInfoFilter.getPayeeNames())) {
				String payeeNames = "'" + loanInfoFilter.getPayeeNames().replace(",", "','") + "'";
				
				sql.append(" AND tli.payeeName in ("+ payeeNames +") ");
			}
			
			//借款日期
			if (!StringUtils.isBlank(loanInfoFilter.getLoanDates())) {
				String loanDates = "'" + loanInfoFilter.getLoanDates().replace(",", "','") + "'";
				
				sql.append(" AND tli.loanDate in ("+ loanDates +") ");
			}
			
			//最小借款日期
			if (!StringUtils.isBlank(loanInfoFilter.getStartLoanDate())) {
				paramsList.add(loanInfoFilter.getStartLoanDate());
				sql.append(" AND tli.loanDate >= ? ");
			}
			
			//最大借款日期
			if (!StringUtils.isBlank(loanInfoFilter.getEndLoanDate())) {
				paramsList.add(loanInfoFilter.getEndLoanDate());
				sql.append(" AND tli.loanDate <= ? ");
			}
			
			//借款月份
			if (!StringUtils.isBlank(loanInfoFilter.getLoanMonth())) {
				paramsList.add(loanInfoFilter.getLoanMonth());
				sql.append(" AND DATE_FORMAT(tli.loanDate,'%Y年%m月') = ? ");
			}
			
			//记账人
			if (!StringUtils.isBlank(loanInfoFilter.getAgents())) {
				String agents = "'" + loanInfoFilter.getAgents().replace(",", "','") + "'";
				
				sql.append(" AND tli.agent in ("+ agents +") ");
			}
			
			//摘要
			if (!StringUtils.isBlank(loanInfoFilter.getSummary())) {
				sql.append(" AND tli.summary like ? ");
				paramsList.add("%" + loanInfoFilter.getSummary() + "%");
			}
			
			//金额区间
			if (loanInfoFilter.getMinMoney() != null) {
				sql.append(" AND tli.money >= ? ");
				paramsList.add(loanInfoFilter.getMinMoney());
			}
			if (loanInfoFilter.getMaxMoney() != null) {
				sql.append(" AND tli.money <= ? ");
				paramsList.add(loanInfoFilter.getMaxMoney());
			}
			
			//付款方式
			if (!StringUtils.isBlank(loanInfoFilter.getPaymentWayId())) {
				sql.append(" AND tli.paymentWay = ? ");
				paramsList.add(loanInfoFilter.getPaymentWayId());
			}
		}
		
		if (includeCollection || includeLoan || includePayment) {
			if (isASC) {
				if (sortType == 0 ) { //按期排序
					sql.append(" ORDER BY receiptDate DESC, createTime DESC, receiptNo DESC");
				}else { //安创建时间排序
					sql.append(" ORDER BY createTime DESC, receiptDate DESC, receiptNo DESC");
				}
			} else {
				sql.append(" ORDER BY receiptDate DESC, createTime DESC, receiptNo DESC ");
			}
		}
		return this.query(sql.toString(), paramsList.toArray(), page);
	}
	
	
	
	
	
	/**
	 * 查询财务流水账（含有付款、借款、收款信息）金额统计信息
	 * @param crewId
	 * @param paymentInfoFilter	付款单过滤条件
	 * @param collectionFilter	收款单过滤条件
	 * @param loanInfoFilter	借款单过滤条件
	 * @param includePayment	是否包含付款单
	 * @param includeCollection	是否包含收款单
	 * @param includeLoan	是否包含借款单
	 * @return 所有付款、所有收款
	 * 
	 */
	public List<Map<String, Object>> queryFinanceRunningAccountTotalMoney(String crewId, boolean includePayment, 
			boolean includeCollection, boolean includeLoan, 
			PaymentInfoFilter paymentInfoFilter,  
			CollectionInfoFilter collectionFilter, 
			LoanInfoFilter loanInfoFilter, Page page) {
		List<Object> paramsList = new ArrayList<Object>();
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" select ");
		sql.append("    currencyId, currencyCode, currencyName, exchangeRate, ");
		sql.append("	round(sum(collectMoney), 2) totalCollectMoney,  ");
		sql.append("	round(sum(payedMoney), 2) totalPayedMoney, ");
		sql.append("    round(sum(forLoanMoney), 2) totalForLoanMoney ");
		sql.append(" 	from ( ");
		
		sql.append(" select ");
		sql.append(" '' loanIds, ");
		sql.append(" 0 forLoanMoney, ");
		sql.append(" '' receiptId, ");
		sql.append(" null receiptDate, ");
		sql.append(" '' createTime, ");
		sql.append(" '' receiptNo, ");
		sql.append(" '' summary, ");
		sql.append(" '' financeSubjId, ");
		sql.append(" '' financeSubjName, ");
		sql.append(" 0.00 collectMoney, ");
		sql.append(" 0.00 payedMoney, ");
		sql.append(" '' status, ");
		sql.append(" 0 formType, ");
		sql.append(" '' aimPersonName, ");
		sql.append(" '' paymentWay, ");
		sql.append(" '' hasReceipt, ");
		sql.append(" 0 billCount, ");
		sql.append(" '' agent, ");
		sql.append(" '' currencyId, ");
		sql.append(" '' currencyCode, ");
		sql.append(" '' currencyName, ");
		sql.append(" 0.00 exchangeRate, ");
		sql.append(" '' contractNo, ");
		sql.append(" '' contractName from dual where 1=0 ");
		
		if (includePayment) {
			sql.append(" UNION ALL ");
			/*
			 * 付款单
			 */
			sql.append(" SELECT  ");
			sql.append(" 	GROUP_CONCAT(tplm.loanId) loanIds, ");
			sql.append(" SUM(tplm.repaymentMoney - tplm.loanBalance) forLoanMoney, ");
			sql.append(" 	tmp.* ");
			sql.append(" 	from (SELECT ");
			sql.append(" 		tpi.paymentId receiptId, ");
			sql.append(" 		tpi.paymentDate receiptDate, ");
			sql.append(" 		tpi.createTime, ");
			sql.append(" 		tpi.receiptNo, ");
			sql.append(" 		GROUP_CONCAT(DISTINCT tpfm.summary) summary, ");
			sql.append(" 		GROUP_CONCAT(tpfm.financeSubjId) financeSubjId, ");
			sql.append(" 		GROUP_CONCAT(tpfm.financeSubjName) financeSubjName, ");
			sql.append(" 		0.00 collectMoney, ");
			if(paymentInfoFilter.isQueryFinanceSubjPayment()) {
				sql.append(" 		sum(tpfm.money) payedMoney, ");
			} else {
				sql.append(" 		tpi.totalMoney payedMoney, ");
			}
			sql.append(" 		tpi.`status` + '' status, ");
			sql.append(" 		1 formType, ");
			sql.append(" 		tpi.payeeName aimPersonName, ");
			sql.append(" 		tfpi.wayName paymentWay, ");
			sql.append(" 		tpi.hasReceipt + '' hasReceipt, ");
			sql.append(" 		tpi.billCount, ");
			sql.append(" 		tpi.agent, ");
			sql.append(" 		tci.id currencyId, ");
			sql.append(" 		tci.`code` currencyCode, ");
			sql.append(" 		tci.`name` currencyName, ");
			sql.append(" 		tci.exchangeRate, ");
			sql.append(" 		if(tca.contractNo is NOT NULL, tca.contractNo, if(tcw.contractNo is not null, tcw.contractNo, if(tcp.contractNo is not null, tcp.contractNo, ''))) contractNo, ");
			sql.append(" 		if(tca.actorName is NOT NULL, tca.actorName, if(tcw.workerName is not null, tcw.workerName, if(tcp.company is not null, tcp.company, ''))) contractName ");
			sql.append(" 	FROM ");
			sql.append(" 		tab_payment_info tpi ");
			sql.append(" 	LEFT JOIN tab_contract_actor tca ON tca.contractId = tpi.contractId ");
			sql.append(" 	LEFT JOIN tab_contract_worker tcw ON tcw.contractId = tpi.contractId ");
			sql.append(" 	LEFT JOIN tab_contract_produce tcp ON tcp.contractId = tpi.contractId, ");
			sql.append(" 	tab_payment_finanSubj_map tpfm, ");
			sql.append(" 	tab_finance_paymentWay_info tfpi, ");
			sql.append(" 	tab_currency_info tci ");
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
			
			//部门
			if (!StringUtils.isBlank(paymentInfoFilter.getDepartment())) {
				String departments = "'" + paymentInfoFilter.getDepartment().replace(",", "','") + "'";
				
				sql.append(" AND tpi.department in ("+ departments +") ");
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
			sql.append(" 	tpi.totalMoney) tmp ");
			sql.append(" LEFT JOIN tab_payment_loan_map tplm ON tmp.receiptId = tplm.paymentId ");
			sql.append(" LEFT JOIN tab_loan_info tli ON tli.loanId=tplm.loanId ");
			sql.append(" GROUP BY tmp.receiptId ");
		}
		
		/*
		 * 收款单
		 */
		if (includeCollection) {
			sql.append(" UNION ALL ");
			
			sql.append(" SELECT ");
			sql.append("    '' loanIds, ");
			sql.append("    0 forLoanMoney, ");
			sql.append(" 	tci.collectionId receiptId, ");
			sql.append(" 	tci.collectionDate receiptDate, ");
			sql.append(" 	tci.createTime, ");
			sql.append(" 	tci.receiptNo, ");
			sql.append(" 	tci.summary, ");
			sql.append(" 	'' financeSubjId, ");
			sql.append(" 	'' financeSubjName, ");
			sql.append(" 	tci.money collectMoney, ");
			sql.append(" 	0.00 payedMoney, ");
			sql.append(" 	'/' status, ");
			sql.append(" 	2 formType, ");
			sql.append(" 	tci.otherUnit aimPersonName, ");
			sql.append(" 	tfpi.wayName paymentWay, ");
			sql.append(" 	'/' hasReceipt, ");
			sql.append(" 	'/' billCount, ");
			sql.append(" 	tci.agent, ");
			sql.append(" 	tcci.id currencyId, ");
			sql.append(" 	tcci. CODE currencyCode, ");
			sql.append(" 	tcci. NAME currencyName, ");
			sql.append(" 	tcci.exchangeRate, ");
			sql.append(" 	'' contractNo, ");
			sql.append(" 	'' contractName ");
			sql.append(" FROM ");
			sql.append(" 	tab_collection_info tci, ");
			sql.append(" 	tab_finance_paymentWay_info tfpi, ");
			sql.append(" 	tab_currency_info tcci ");
			sql.append(" WHERE ");
			sql.append(" 	tci.crewId = ? ");
			paramsList.add(crewId);
			sql.append(" AND tcci.id = tci.currencyId ");
			sql.append(" AND tci.paymentWay = tfpi.wayId ");
			
			if (!StringUtils.isBlank(collectionFilter.getCollectionIds())) {
				String collectionIds = "'" + collectionFilter.getCollectionIds().replace(",", "','") + "'";
				
				sql.append(" AND tci.collectionId in ("+ collectionIds +") ");
			}
			
			if (!StringUtils.isBlank(collectionFilter.getOtherUnits())) {
				String otherUnits = "'" + collectionFilter.getOtherUnits().replace(",", "','") + "'";
				
				sql.append(" AND tci.otherUnit in ("+ otherUnits +") ");
			}
			
			if (!StringUtils.isBlank(collectionFilter.getCollectionDates())) {
				String collectionDates = "'" + collectionFilter.getCollectionDates().replace(",", "','") + "'";
				
				sql.append(" AND tci.collectionDate in ("+ collectionDates +") ");
			}
			
			if (!StringUtils.isBlank(collectionFilter.getStartCollectionDate())) {
				paramsList.add(collectionFilter.getStartCollectionDate());
				sql.append(" AND tci.collectionDate >= ? ");
			}
			
			if (!StringUtils.isBlank(collectionFilter.getEndCollectionDate())) {
				paramsList.add(collectionFilter.getEndCollectionDate());
				sql.append(" AND tci.collectionDate <= ? ");
			}
			
			//收款月份
			if (!StringUtils.isBlank(collectionFilter.getCollectionMonth())) {
				paramsList.add(collectionFilter.getCollectionMonth());
				sql.append(" AND DATE_FORMAT(tci.collectionDate,'%Y年%m月') = ? ");
			}
			
			if (!StringUtils.isBlank(collectionFilter.getAgents())) {
				String agents = "'" + collectionFilter.getAgents().replace(",", "','") + "'";
				
				sql.append(" AND tci.agent in ("+ agents +") ");
			}
			
			if (!StringUtils.isBlank(collectionFilter.getSummary())) {
				sql.append(" AND tci.summary like ? ");
				paramsList.add("%" + collectionFilter.getSummary() + "%");
			}
			
			if (collectionFilter.getMinMoney() != null) {
				sql.append(" AND tci.money >= ? ");
				paramsList.add(collectionFilter.getMinMoney());
			}
			
			if (collectionFilter.getMaxMoney() != null) {
				sql.append(" AND tci.money <= ? ");
				paramsList.add(collectionFilter.getMaxMoney());
			}
			
			//付款方式
			if (!StringUtils.isBlank(collectionFilter.getPaymentWayId())) {
				sql.append(" AND tci.paymentWay = ? ");
				paramsList.add(collectionFilter.getPaymentWayId());
			}
		}
		
		
		/*
		 * 借款单 
		 */
		if (includeLoan) {
			sql.append(" UNION ALL ");
			
			sql.append(" SELECT ");
			sql.append("    '' loanIds, ");
			sql.append("    0 forLoanMoney, ");
			sql.append(" 	tli.loanId receiptId, ");
			sql.append(" 	tli.loanDate receiptDate, ");
			sql.append(" 	tli.createTime, ");
			sql.append(" 	tli.receiptNo, ");
			sql.append(" 	tli.summary, ");
			sql.append(" 	tli.financeSubjId, ");
			sql.append(" 	tli.financeSubjName, ");
			sql.append(" 	0.00 collectMoney, ");
			sql.append(" 	tli.money payedMoney, ");
			sql.append(" 	'/' status, ");
			sql.append(" 	3 formType, ");
			sql.append(" 	tli.payeeName aimPersonName, ");
			sql.append(" 	tfpi.wayName paymentWay, ");
			sql.append(" 	'/' hasReceipt, ");
			sql.append(" 	'/' billCount, ");
			sql.append(" 	tli.agent, ");
			sql.append(" 	tci.id currencyId, ");
			sql.append(" 	tci. CODE currencyCode, ");
			sql.append(" 	tci. NAME currencyName, ");
			sql.append(" 	tci.exchangeRate, ");
			sql.append(" 	'' contractNo, ");
			sql.append(" 	'' contractName ");
			sql.append(" FROM ");
			sql.append(" 	tab_loan_info tli, ");
			sql.append(" 	tab_currency_info tci, ");
			sql.append("    tab_finance_paymentWay_info tfpi ");
			sql.append(" WHERE ");
			sql.append(" 	tli.crewId = ? ");
			paramsList.add(crewId);
			sql.append(" AND tci.id = tli.currencyId ");
			sql.append(" AND tfpi.wayId = CONCAT(tli.paymentWay, '') ");
			

			if (!StringUtils.isBlank(loanInfoFilter.getLoanIds())) {
				String loanIds = "'" + loanInfoFilter.getLoanIds().replace(",", "','") + "'";
				
				sql.append(" AND tli.loanId in ("+ loanIds +") ");
			}
			
			//财务科目
			if (!StringUtils.isBlank(loanInfoFilter.getFinanceSubjIds())) {
				String financeSubjIds = "'" + loanInfoFilter.getFinanceSubjIds().replace(",", "','") + "'";
				
				sql.append(" AND tli.financeSubjId in ("+ financeSubjIds +") ");
			}
			
			//借款人
			if (!StringUtils.isBlank(loanInfoFilter.getPayeeNames())) {
				String payeeNames = "'" + loanInfoFilter.getPayeeNames().replace(",", "','") + "'";
				
				sql.append(" AND tli.payeeName in ("+ payeeNames +") ");
			}
			
			//借款日期
			if (!StringUtils.isBlank(loanInfoFilter.getLoanDates())) {
				String loanDates = "'" + loanInfoFilter.getLoanDates().replace(",", "','") + "'";
				
				sql.append(" AND tli.loanDate in ("+ loanDates +") ");
			}
			
			//最小借款日期
			if (!StringUtils.isBlank(loanInfoFilter.getStartLoanDate())) {
				paramsList.add(loanInfoFilter.getStartLoanDate());
				sql.append(" AND tli.loanDate >= ? ");
			}
			
			//最大借款日期
			if (!StringUtils.isBlank(loanInfoFilter.getEndLoanDate())) {
				paramsList.add(loanInfoFilter.getEndLoanDate());
				sql.append(" AND tli.loanDate <= ? ");
			}
			
			//借款月份
			if (!StringUtils.isBlank(loanInfoFilter.getLoanMonth())) {
				paramsList.add(loanInfoFilter.getLoanMonth());
				sql.append(" AND DATE_FORMAT(tli.loanDate,'%Y年%m月') = ? ");
			}
			
			//记账人
			if (!StringUtils.isBlank(loanInfoFilter.getAgents())) {
				String agents = "'" + loanInfoFilter.getAgents().replace(",", "','") + "'";
				
				sql.append(" AND tli.agent in ("+ agents +") ");
			}
			
			//摘要
			if (!StringUtils.isBlank(loanInfoFilter.getSummary())) {
				sql.append(" AND tli.summary like ? ");
				paramsList.add("%" + loanInfoFilter.getSummary() + "%");
			}
			
			//金额区间
			if (loanInfoFilter.getMinMoney() != null) {
				sql.append(" AND tli.money >= ? ");
				paramsList.add(loanInfoFilter.getMinMoney());
			}
			if (loanInfoFilter.getMaxMoney() != null) {
				sql.append(" AND tli.money <= ? ");
				paramsList.add(loanInfoFilter.getMaxMoney());
			}
			
			//付款方式
			if (!StringUtils.isBlank(loanInfoFilter.getPaymentWayId())) {
				sql.append(" AND tli.paymentWay = ? ");
				paramsList.add(loanInfoFilter.getPaymentWayId());
			}
		}
		
		if (includeCollection || includeLoan || includePayment) {
			sql.append(" ORDER BY receiptDate ASC, createTime ASC, receiptNo ASC ");
		}
		
		if (page != null) {
			sql.append(" limit " + page.getNextIndex() + ", " + page.getPagesize());
		}
		
		sql.append(" 	) tmp ");
		sql.append(" group by currencyId, currencyCode, currencyName, exchangeRate ");
		return this.query(sql.toString(), paramsList.toArray(), null);
	}
}
