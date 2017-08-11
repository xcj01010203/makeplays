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

import com.xiaotu.makeplays.finance.controller.filter.LoanInfoFilter;
import com.xiaotu.makeplays.finance.model.LoanInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 借款信息
 * @author xuchangjian 2016-8-3下午5:12:32
 */
@Repository
public class LoanInfoDao extends BaseDao<LoanInfoModel> {
	
	/**
	 * 根据多个条件查询借款单信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<LoanInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + LoanInfoModel.TABLE_NAME + " where 1 = 1 ");
		
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
		List<LoanInfoModel> loanList = this.query(sql.toString(), objArr, LoanInfoModel.class, page);
		
		return loanList;
	}

	/**
	 * 根据财务科目查询借款单
	 * @param crewId
	 * @param financeSubjId
	 * @return
	 */
	public List<LoanInfoModel> queryByFinanceSubjId(String crewId, String financeSubjId) {
		String sql = "select * from " + LoanInfoModel.TABLE_NAME + " where crewId = ? and financeSubjId = ?";
		return this.query(sql, new Object[] {crewId, financeSubjId}, LoanInfoModel.class, null);
	}
	
	/**
	 * 根据剧组ID查询借款单
	 * @param crewId
	 * @return
	 */
	public List<LoanInfoModel> queryByCrewId(String crewId) {
		String sql = "select * from " + LoanInfoModel.TABLE_NAME + " where crewId = ?";
		return this.query(sql, new Object[] {crewId}, LoanInfoModel.class, null);
	}
	
	/**
	 * 查询剧组下带有财务科目的借款单
	 * @param crewId
	 * @return
	 */
	public List<LoanInfoModel> queryFinanceLoanList(String crewId) {
		String sql = "select * from " + LoanInfoModel.TABLE_NAME + " where crewId = ? and (financeSubjId != '' && financeSubjId is not null) ";
		return this.query(sql, new Object[] {crewId}, LoanInfoModel.class, null);
	}
	
	/**
	 * 查询借款单的预算
	 * 以财务科目为单位，查询每个财务科目目前在借款单上的花销
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryLoanBudget(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tli.loanerId, tli.currencyId, tli.financeSubjId, tci.`name`, ");
		sql.append(" 	tci.`code`, ");
		sql.append(" 	tci.exchangeRate, ");
		sql.append(" 	tli.money, ");
		sql.append(" 	tli.receiptNo, ");//票据编号
		sql.append(" 	tli.loanDate, ");//借款日期
		sql.append(" 	tli.payeeName, ");//借款人名称
		sql.append(" 	tli.financeSubjName, ");//财务科目名称
		sql.append(" 	tli.summary, ");//摘要
		sql.append(" 	tli.paymentWay ");//付款方式
		sql.append(" FROM ");
		sql.append(" 	tab_loan_info tli, ");
		sql.append(" 	tab_currency_info tci, ");
		sql.append(" 	tab_finance_subject tfs ");	//添加财务科目表关联是为了过滤掉借款关联的无效的财务科目
		sql.append(" WHERE ");
		sql.append(" 	tli.currencyId = tci.id ");
		sql.append(" AND tli.financeSubjId is not null ");
		sql.append(" AND tli.financeSubjId != '' ");
		sql.append(" AND tli.crewId = ? ");
		sql.append(" AND tci.crewId= ? ");
		sql.append(" AND tfs.id = tli.financeSubjId ");
		sql.append(" AND tfs.crewId = ? ");
		
		return this.query(sql.toString(), new Object[] {crewId, crewId, crewId}, null);
	}
	
	/**
	 * 根据借款人查询借款单信息，带有所有还款信息
	 * 该查询有个默认强制的条件：所有用来还该借款单的付款单，其币种必须一致，且和该借款单的币种一致
	 * 
	 * @param crewId
	 * @param payeeName	借款人
	 * @param currencyId 币种ID
	 * @return loanId借款单ID， loanDate借款日期， receiptNo借款单票据编号， 
	 * currencyId借款单关联货币ID， currencyCode借款单关联货币编码，exchangeRate汇率， money借款金额
	 * summary借款单摘要， leftMoney欠款金额
	 */
	public List<Map<String, Object>> queryLoanWithPaymentInfo(String crewId, String payeeName, String loanIds, 
			String currencyId, Boolean onlySettled, String paymentStartDate, String paymentEndDate, String financeSubjId) {
		List<Object> paramList = new ArrayList<Object>();
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tli.loanId, ");
		sql.append(" 	tli.loanDate, ");
		sql.append(" 	tli.receiptNo, ");
		sql.append(" 	tli.currencyId, ");
		sql.append(" 	tli.financeSubjId, ");
		sql.append(" 	tli.financeSubjName, ");
		sql.append(" 	tci.`code` currencyCode, ");
		sql.append(" 	tci.exchangeRate, ");
		sql.append(" 	tli.summary, ");
		sql.append(" 	sub.receiptNo paymentReceiptNo, ");
		sql.append(" 	sub.payeeName, ");
		sql.append(" 	sub.agent, ");
		sql.append(" 	sub.paymentDate, ");
		sql.append(" 	sub.totalMoney, ");
		sql.append(" 	round(tli.money, 2) money, ");
		sql.append(" 	if(min(sub.loanBalance) is NULL, tli.money, min(sub.loanBalance)) leftMoney ");
		sql.append(" FROM ");
		sql.append(" 	tab_loan_info tli ");
//		sql.append(" 	LEFT JOIN tab_payment_loan_map tplm ON tli.loanId = tplm.loanId, ");
		//付款单信息
		sql.append(" LEFT JOIN ( ");
		sql.append(" 	SELECT ");
		sql.append(" 	tplm.loanId, tplm.loanBalance, tpi.paymentDate, tpi.paymentId ,tpi.receiptNo,tpi.payeeName,tpi.agent,tpi.totalMoney");
		sql.append(" 	FROM ");
		sql.append(" 		tab_payment_loan_map tplm, ");
		sql.append(" 		tab_payment_info tpi ");
		sql.append(" 	WHERE ");
		sql.append(" 		tplm.paymentId = tpi.paymentId ");
		if (onlySettled != null && onlySettled) {
			sql.append("  AND tpi.`status`= 1 ");
		}
		if (!StringUtils.isBlank(paymentStartDate)) {
			sql.append("  AND tpi.paymentDate >= ? ");
			paramList.add(paymentStartDate);
		}
		if (!StringUtils.isBlank(paymentEndDate)) {
			sql.append("  AND tpi.paymentDate <= ? ");
			paramList.add(paymentEndDate);
		}
		sql.append(" ) sub ON tli.loanId = sub.loanId, ");
		
		sql.append(" 	tab_currency_info tci ");
		sql.append(" WHERE 1 = 1 ");
		sql.append(" AND tci.id = tli.currencyId ");
		
		if (!StringUtils.isBlank(currencyId)) {
			sql.append(" AND tli.currencyId = ? ");
			paramList.add(currencyId);
		}
		
		if (!StringUtils.isBlank(payeeName)) {
			sql.append(" AND tli.payeeName = ? ");
			paramList.add(payeeName);
		}
		if (!StringUtils.isBlank(loanIds)) {
			loanIds = "'" + loanIds.replace(",", "','") + "'";
			sql.append(" AND tli.loanId in ("+ loanIds +") ");
		}
		sql.append(" AND   tli.crewId = ?  ");
		sql.append(" GROUP BY ");
		sql.append(" 	tli.loanId, ");
		sql.append(" 	tli.loanDate, ");
		sql.append(" 	tli.receiptNo, ");
		sql.append(" 	tli.currencyId, ");
		sql.append(" 	tli.financeSubjId, ");
		sql.append(" 	tli.financeSubjName, ");
		sql.append(" 	tci.`code`, ");
		sql.append(" 	tci.exchangeRate, ");
		sql.append(" 	tli.summary, ");
		sql.append(" 	tli.money ");
		sql.append(" ORDER BY tli.receiptNo ");
		
		paramList.add(crewId);
		return this.query(sql.toString(), paramList.toArray(), null);
	}
	
	/**
	 * 查询借款单的付款列表（主要查询付款单数据）
	 * @param crewId
	 * @param financeSubjId
	 * @return
	 */
	public List<Map<String, Object>> queryLoanPaymentList(String crewId, String financeSubjId){
		StringBuffer sql = new StringBuffer();
		sql.append("	SELECT");
		sql.append("		tci.exchangeRate,sub.receiptNo paymentReceiptNo,sub.payeeName,sub.paymentDate,");
		sql.append("		sub.summary paymentSummary,sub.totalMoney,sub.paymentId, sub.financeSubjId paymentSubjId,sub.wayName, sub.forLoanMoney");
		sql.append("	FROM");
		sql.append("		tab_loan_info tli");
		sql.append("	LEFT JOIN (");
		sql.append("		SELECT");
		sql.append("			tplm.loanId,tpi.paymentDate,tpi.paymentId,tpi.receiptNo,tpi.payeeName,(tplm.repaymentMoney - tplm.loanBalance) forLoanMoney,");
		sql.append("			tpi.agent,tpi.totalMoney,GROUP_CONCAT(IF(tpfm.summary = '',NULL,tpfm.summary) SEPARATOR '|') summary,tfpi.wayName,GROUP_CONCAT(tpfm.financeSubjId) financeSubjId");
		sql.append("		FROM");
		sql.append("			tab_payment_loan_map tplm,");
		sql.append("			tab_payment_info tpi,");
		sql.append("			tab_payment_finanSubj_map tpfm,");
		sql.append("			tab_finance_paymentWay_info tfpi");
		sql.append("		WHERE");
		sql.append("			tplm.paymentId = tpi.paymentId");
		sql.append("			AND tpfm.paymentId = tpi.paymentId");
		sql.append("			AND tfpi.wayId = tpi.paymentWay");
		sql.append("			AND tpi.`status` = 1");
		sql.append("		GROUP BY tpi.paymentId");
		sql.append("	) sub ON tli.loanId = sub.loanId,");
		sql.append("	tab_currency_info tci");
		sql.append("	WHERE");
		sql.append("	tci.id = tli.currencyId");
		sql.append("	AND tli.financeSubjId = ?");
		sql.append("	AND tli.crewId = ?");
		sql.append("	ORDER BY tli.receiptNo");
		
		return this.query(sql.toString(), new Object[] {financeSubjId,crewId}, null);
	}
	
	/**
	 * 根据ID查询借款单
	 * @param loanId
	 * @return
	 * @throws Exception 
	 */
	public LoanInfoModel queryById(String loanId) throws Exception {
		String sql = "select * from " + LoanInfoModel.TABLE_NAME + " where loanId = ? ";
		return this.queryForObject(sql, new Object[] {loanId}, LoanInfoModel.class);
	}
	
	/**
	 * 根据多个ID查询借款单
	 * @param loanId
	 * @return
	 * @throws Exception 
	 */
	public List<LoanInfoModel> queryByIds(String loanIds) throws Exception {
		loanIds = "'" + loanIds.replace(",", "','") + "'";
		
		String sql = "select * from " + LoanInfoModel.TABLE_NAME + " where loanId in ("+ loanIds +") ";
		return this.query(sql, null, LoanInfoModel.class, null);
	}
	
	/**
	 * 查询最大的票据编号
	 * 
	 * @param payStatus	票据编号是否按月重新开始
	 * @param moonFirstDay 付款当月第一天
	 * @param moonLastDay 付款当月最后一天
	 * @return
	 */
	public String queryMaxReceiptNo(String crewId, boolean payStatus, Date moonFirstDay, Date moonLastDay) {
		List<Object> paramsList = new ArrayList<Object>();
		paramsList.add(crewId);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select max(receiptNo) from tab_loan_info where 1 = 1 ");
		sql.append(" and crewId = ? ");
		if (payStatus) {
			sql.append(" and loanDate >= ? and loanDate <= ? ");
			paramsList.add(moonFirstDay);
			paramsList.add(moonLastDay);
		}
		
		return this.getJdbcTemplate().queryForObject(sql.toString(), paramsList.toArray(), String.class);
	}
	
	/**
	 * 查询借款单信息
	 * @param crewId
	 * @return	借款日期， 创建时间， 票据编号，摘要，财务科目ID，财务科目名称，金额，借款人，支付方式，记账人，关联货币ID，关联货币编码，货币名称
	 */
	public List<Map<String, Object>> queryLoanInfoList (String crewId, LoanInfoFilter loanInfoFilter) {
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(crewId);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("    tli.loanId, ");
		sql.append("    tli.loanDate, ");
		sql.append("    tli.createTime, ");
		sql.append("    tli.receiptNo, ");
		sql.append("    tli.summary, ");
		sql.append("    tli.financeSubjId, ");
		sql.append("    tli.financeSubjName, ");
		sql.append("    tli.money, ");
		sql.append("    tli.payeeName, ");
		sql.append("    tli.paymentWay, ");
//		sql.append("    tpi.wayName paymentWay, ");
		sql.append("    tli.agent, ");
		sql.append("    tci.id currencyId, ");
		sql.append("    tci. CODE currencyCode, ");
		sql.append("    tci. NAME currencyName, ");
		sql.append("    tci.exchangeRate ");
		sql.append(" FROM ");
		sql.append("    tab_loan_info tli, ");
//		sql.append("  tab_finance_paymentWay_info tpi, ");
		sql.append("  tab_currency_info tci ");
		sql.append(" WHERE ");
		sql.append("    tli.crewId = ? ");
//		sql.append(" AND tli.paymentWay = tpi.wayId ");
		sql.append(" AND tci.id = tli.currencyId ");
		
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
			paramList.add(loanInfoFilter.getStartLoanDate());
			sql.append(" AND tli.loanDate >= ? ");
		}
		
		//最大借款日期
		if (!StringUtils.isBlank(loanInfoFilter.getEndLoanDate())) {
			paramList.add(loanInfoFilter.getEndLoanDate());
			sql.append(" AND tli.loanDate <= ? ");
		}
		
		//借款月份
		if (!StringUtils.isBlank(loanInfoFilter.getLoanMonth())) {
			paramList.add(loanInfoFilter.getLoanMonth());
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
			paramList.add("%" + loanInfoFilter.getSummary() + "%");
		}
		
		//金额区间
		if (loanInfoFilter.getMinMoney() != null) {
			sql.append(" AND tli.money >= ? ");
			paramList.add(loanInfoFilter.getMinMoney());
		}
		if (loanInfoFilter.getMaxMoney() != null) {
			sql.append(" AND tli.money <= ? ");
			paramList.add(loanInfoFilter.getMaxMoney());
		}
		
		//付款方式
		if (!StringUtils.isBlank(loanInfoFilter.getPaymentWayId())) {
			sql.append(" AND tli.paymentWay = ? ");
			paramList.add(loanInfoFilter.getPaymentWayId());
		}
		return this.query(sql.toString(), paramList.toArray(), null);
	}
	
	/**
	 * 查询借款单币种统计信息
	 * @param crewId
	 * @return	
	 */
	public List<Map<String, Object>> queryLoanStatistic (String crewId, LoanInfoFilter loanInfoFilter) {
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(crewId);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("    sum(tli.money) totalLoanMoney, ");
		sql.append("    tci.id currencyId, ");
		sql.append("    tci. CODE currencyCode, ");
		sql.append("    tci. NAME currencyName, ");
		sql.append("    tci.exchangeRate ");
		sql.append(" FROM ");
		sql.append("    tab_loan_info tli, ");
		sql.append("  tab_finance_paymentWay_info tpi, ");
		sql.append("  tab_currency_info tci ");
		sql.append(" WHERE 1 = 1 ");
		sql.append(" AND tli.crewId = ? ");
		sql.append(" AND tli.paymentWay = tpi.wayId ");
		sql.append(" AND tci.id = tli.currencyId ");
		
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
			paramList.add(loanInfoFilter.getStartLoanDate());
			sql.append(" AND tli.loanDate >= ? ");
		}
		
		//最大借款日期
		if (!StringUtils.isBlank(loanInfoFilter.getEndLoanDate())) {
			paramList.add(loanInfoFilter.getEndLoanDate());
			sql.append(" AND tli.loanDate <= ? ");
		}
		
		//借款月份
		if (!StringUtils.isBlank(loanInfoFilter.getLoanMonth())) {
			paramList.add(loanInfoFilter.getLoanMonth());
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
			paramList.add("%" + loanInfoFilter.getSummary() + "%");
		}
		
		//金额区间
		if (loanInfoFilter.getMinMoney() != null) {
			sql.append(" AND tli.money >= ? ");
			paramList.add(loanInfoFilter.getMinMoney());
		}
		if (loanInfoFilter.getMaxMoney() != null) {
			sql.append(" AND tli.money <= ? ");
			paramList.add(loanInfoFilter.getMaxMoney());
		}
		
		//付款方式
		if (!StringUtils.isBlank(loanInfoFilter.getPaymentWayId())) {
			sql.append(" AND tli.paymentWay = ? ");
			paramList.add(loanInfoFilter.getPaymentWayId());
		}
		sql.append("    group by tci.id, ");
		sql.append("    tci. CODE, ");
		sql.append("    tci. NAME, ");
		sql.append("    tci.exchangeRate ");
		return this.query(sql.toString(), paramList.toArray(), null);
	}
	
	/**
	 * 查询借款人列表（带有借款、还款信息）
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryPayeeListWithMoneyInfo(String crewId, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tmp.payeeName, ");
		sql.append(" 	tmp.currencyId, ");
		sql.append(" 	tmp.currencyCode, ");
		sql.append(" 	tmp.exchangeRate, ");
		sql.append(" 	sum(tmp.money) loanMoney, ");
		sql.append(" 	sum(tmp.leftMoney) leftMoney ");
		sql.append(" FROM ");
		sql.append(" 	( ");
		sql.append(" 		SELECT ");
		sql.append(" 			tli.loanId, ");
		sql.append(" 			tli.payeeName, ");
		sql.append(" 			tli.loanDate, ");
		sql.append(" 			tli.receiptNo, ");
		sql.append(" 			tli.currencyId, ");
		sql.append(" 			tli.financeSubjId, ");
		sql.append(" 			tli.financeSubjName, ");
		sql.append(" 			tci.`code` currencyCode, ");
		sql.append(" 			tci.exchangeRate, ");
		sql.append(" 			tli.summary, ");
		sql.append(" 			tli.money, ");
		sql.append(" 			if(min(tplm.loanBalance) is NULL, tli.money, min(tplm.loanBalance)) leftMoney ");
		sql.append(" 		FROM ");
		sql.append(" 			tab_loan_info tli ");
		sql.append(" 		LEFT JOIN tab_payment_loan_map tplm ON tli.loanId = tplm.loanId, ");
		sql.append(" 		tab_currency_info tci ");
		sql.append(" 	WHERE ");
		sql.append(" 		1 = 1 ");
		sql.append(" 	AND tci.id = tli.currencyId ");
		sql.append(" 	AND tli.crewId = ? ");
		sql.append(" 	GROUP BY ");
		sql.append(" 		tli.loanId, ");
		sql.append(" 		tli.payeeName, ");
		sql.append(" 		tli.loanDate, ");
		sql.append(" 		tli.receiptNo, ");
		sql.append(" 		tli.currencyId, ");
		sql.append(" 		tli.financeSubjId, ");
		sql.append(" 		tli.financeSubjName, ");
		sql.append(" 		tci.`code`, ");
		sql.append(" 		tci.exchangeRate, ");
		sql.append(" 		tli.summary, ");
		sql.append(" 		tli.money ");
		sql.append(" 	) tmp ");
		sql.append(" GROUP BY ");
		sql.append(" 	tmp.payeeName, ");
		sql.append(" 	tmp.currencyId, ");
		sql.append(" 	tmp.currencyCode, ");
		sql.append(" 	tmp.exchangeRate ");
		
		return this.query(sql.toString(), new Object[] {crewId}, page);
	}
	
	/**
	 * 查询付款单的还借款情况
	 * @param paymentId
	 * @return	借款人名称，偿还金额
	 */
	public List<Map<String, Object>> queryPaymentLoanInfo(String paymentId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tli.payeeName, sum(tplm.repaymentMoney - tplm.loanBalance) forLoanMoney ");
		sql.append(" FROM ");
		sql.append(" 	tab_payment_loan_map tplm, ");
		sql.append(" 	tab_loan_info tli ");
		sql.append(" WHERE ");
		sql.append(" 	tplm.paymentId = ? ");
		sql.append(" AND tplm.loanId = tli.loanId ");
		sql.append(" GROUP BY tli.payeeName; ");
		
		return this.query(sql.toString(), new Object[]{paymentId}, null);
	}
}
