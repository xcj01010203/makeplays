package com.xiaotu.makeplays.finance.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.finance.model.FinanceSubjectModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 财务科目
 * @author xuchangjian 2016-7-28上午10:17:10
 */
@Repository
public class FinanceSubjectDao extends BaseDao<FinanceSubjectModel> {
	
	
	/**
	 * 根据多个条件查询财务科目信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<FinanceSubjectModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + FinanceSubjectModel.TABLE_NAME + " where 1 = 1 ");
		
		Set<String> keySet = conditionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		List<Object> conList = new LinkedList<Object>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = conditionMap.get(key);
			sql.append(" and " + key + " = ?");
			conList.add(value);
		}
		sql.append(" order by sequence");
		Object[] objArr = conList.toArray();
		List<FinanceSubjectModel> propsInfoList = this.query(sql.toString(), objArr, FinanceSubjectModel.class, page);
		
		return propsInfoList;
	}
	

	/**
	 * 根据剧组ID查询剧组财务科目
	 * @param crewId
	 * @return
	 */
	public List<FinanceSubjectModel> queryByCrewId(String crewId) {
		String sql = "select * from " + FinanceSubjectModel.TABLE_NAME + " where crewId = ? order by level, sequence";
		return this.query(sql, new Object[] {crewId}, FinanceSubjectModel.class, null);
	}
	
	/**
	 * 查询所有财务科目（带有预算信息）
	 * @param crewId
	 * @return	不仅返回财务科目信息，还会返回对应币种的预算信息
	 */
	public List<Map<String, Object>> queryWithBudgetInfo(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tfs.id, tfs.name, tfs.parentId, tfs.remark, tci.id currencyId, tci.exchangeRate, tfs.level, tfs.sequence, ");
		sql.append(" 	tfcm.mapId, ");
		sql.append(" 	tfcm.amount, ");
		sql.append(" 	tfcm.money, ");
		sql.append(" 	tfcm.perPrice, ");
		sql.append(" 	tfcm.unitType ");
		sql.append(" FROM ");
		sql.append(" 	tab_finance_subject tfs ");
		sql.append(" LEFT JOIN tab_finanSubj_currency_map tfcm ON tfcm.financeSubjId = tfs.id ");
		sql.append(" LEFT JOIN tab_currency_info tci ON tci.id = tfcm.currencyId AND tci.ifEnable = 1 ");
		sql.append(" WHERE ");
		sql.append(" 	tfs.crewId = ? ");
		sql.append(" ORDER BY sequence ");
		
		return this.query(sql.toString(), new Object[] {crewId}, null);
	}
	
	/**
	 * 查询带有结算信息的财务科目（带有结算信息）
	 * @param crewId
	 * @param paymentStartDate	付款单开始日期
	 * @param paymentEndDate	付款单结束日期
	 * @return
	 */
	public List<Map<String, Object>> queryWithSettleInfo(String crewId, String paymentStartDate, String paymentEndDate) {
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(crewId);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tfs.id, ");
		sql.append(" 	tfs. NAME, ");
		sql.append(" 	tfs.parentId, ");
		sql.append(" 	sum(tpfm.money) payedMoney, ");
		sql.append(" 	tpi.currencyId, ");
		sql.append("    tci.exchangeRate, ");
		sql.append("	tpi.hasReceipt ");
		sql.append(" FROM ");
		sql.append(" 	tab_finance_subject tfs, ");
		sql.append(" 	tab_payment_finanSubj_map tpfm, ");
		sql.append(" 	tab_payment_info tpi, ");
		sql.append("    tab_currency_info tci ");
		sql.append(" WHERE ");
		sql.append(" 	tfs.crewId = ? ");
		sql.append(" AND tci.id = tpi.currencyId  ");
		sql.append(" AND tpfm.financeSubjId = tfs.id ");
		sql.append(" AND tpfm.paymentId = tpi.paymentId ");
		sql.append(" AND tpi.status = 1 ");
		if (!StringUtils.isBlank(paymentStartDate)) {
			sql.append(" AND tpi.paymentDate >= ? ");
			paramList.add(paymentStartDate);
		}
		if (!StringUtils.isBlank(paymentEndDate)) {
			sql.append(" AND tpi.paymentDate <= ? ");
			paramList.add(paymentEndDate);
		}
		sql.append(" GROUP BY ");
		sql.append(" 	tfs.id, ");
		sql.append(" 	tfs. NAME, ");
		sql.append("	tpi.hasReceipt,");
		sql.append(" 	tfs.parentId, ");
		sql.append(" 	tpi.currencyId ");
		sql.append(" ORDER BY ");
		sql.append(" 	sequence ");
		
		return this.query(sql.toString(), paramList.toArray(), null);
	}
	
	/**
	 * 根据ID查询财务科目
	 * @param subjectId
	 * @return
	 * @throws Exception 
	 */
	public FinanceSubjectModel queryById(String subjectId) throws Exception {
		String sql = "select * from " + FinanceSubjectModel.TABLE_NAME + " where id = ? ";
		return this.queryForObject(sql, new Object[] {subjectId}, FinanceSubjectModel.class);
	}
	
	
	/**
	 * 查看当前层级的最大的序列表
	 * @param level
	 * @return
	 */
	public Map<String, Object> queryMaxSequenceByParentId(String parentId) {
		String sql = "SELECT max(sequence) maxSequence from tab_finance_subject where parentId = ?";
		return this.getJdbcTemplate().queryForMap(sql, new Object[] {parentId});
	}
	
	/**
	 * 查询科目的子科目列表
	 * @param crewId
	 * @param parentId
	 * @return
	 */
	public List<FinanceSubjectModel> queryByParentId(String crewId, String parentId) {
		String sql = "select * from " + FinanceSubjectModel.TABLE_NAME + " where crewId = ? and parentId = ?";
		return this.query(sql, new Object[] {crewId, parentId}, FinanceSubjectModel.class, null);
	}
	
	/**
	 * 删除剧组下所有财务科目
	 * @param crewId
	 */
	public void deleteByCrewId(String crewId) {
		String sql = "delete from " + FinanceSubjectModel.TABLE_NAME + " where crewId = ?";
		this.getJdbcTemplate().update(sql, new Object[] {crewId});
	}
	
	/**
	 * 更新财务科目的顺序
	 * @param id	财务科目ID
	 * @param sequence	财务科目顺序
	 */
	public void updateSubjectSequence(String id, Integer sequence) {
		String sql = "update " + FinanceSubjectModel.TABLE_NAME + " set sequence = ? where id = ? ";
		this.getJdbcTemplate().update(sql, new Object[] {sequence, id});
	}
	
	/**
	 * 把指定父科目下所有财务科目序列加一
	 * @param parentId
	 */
	public void addOneSubjectSequence(String parentId) {
		String sql = " update " + FinanceSubjectModel.TABLE_NAME + " set sequence = sequence + 1 where parentId = ?";
		this.getJdbcTemplate().update(sql, new Object[] {parentId});
	}
	
	/**
	 * 查询剧组下的财务科目信息
	 * 该方法还会查询出财务科目对应的会计科目
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryByCrewIdWithAccSubj(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tfs.*, tas.name accSubjName, tas.code accSubjCode ");
		sql.append(" FROM ");
		sql.append(" 	tab_finance_subject tfs ");
		sql.append(" LEFT JOIN tab_account_finance_subject_map tafsm ON tafsm.crewId =? and tfs.id = tafsm.financeSubjId ");
		sql.append(" LEFT JOIN tab_account_subject tas ON tafsm.accountSubjId = tas.id and tas.crewId=? ");
		sql.append(" WHERE ");
		sql.append(" 	tfs.crewId = ? ");
		sql.append(" ORDER BY ");
		sql.append(" 	sequence ");
		return this.query(sql.toString(), new Object[] {crewId, crewId, crewId}, null);
	}
	
	/**
	 * 查询对于指定会计科目来说不可用的财务科目
	 * 也就是被剧组中其他财务科目已占用的财务科目
	 * @param crewId
	 * @param accountId
	 * @return
	 */
	public List<FinanceSubjectModel> queryUnusedSubjByAccId(String crewId, String accountId) {
		String sql = "select distinct tfs.* from tab_account_finance_subject_map tafsm, tab_finance_subject tfs where tafsm.financeSubjId = tfs.id and tafsm.crewId = ? and tafsm.accountSubjId != ? ";
		return this.query(sql, new Object[] {crewId, accountId}, FinanceSubjectModel.class, null);
	}
	
	/**
	 * 查询会计科目下的财务科目
	 * @param crewId
	 * @param acccount
	 * @return
	 */
	public List<FinanceSubjectModel> queryByAccountId(String crewId, String acccountId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select tfs.* ");
		sql.append("   from tab_finance_subject tfs, ");
		sql.append("        tab_account_finance_subject_map tafsm ");
		sql.append("  where tafsm.crewId = ? ");
		sql.append("    and tafsm.accountSubjId = ? ");
		sql.append("    and tafsm.financeSubjId = tfs.id ");
		sql.append("  order by sequence ");
		
		return this.query(sql.toString(), new Object[] {crewId, acccountId}, FinanceSubjectModel.class, null);
	}
	
	/**
	 * 查询剧组中尚未加入到会计科目下的财务科目信息
	 * @param crewId
	 * @return
	 */
	public List<FinanceSubjectModel> queryUnSelectedBudgSubj(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	* ");
		sql.append(" FROM ");
		sql.append(" 	tab_finance_subject tfs ");
		sql.append(" WHERE ");
		sql.append(" 	crewId = ? ");
		sql.append(" AND NOT EXISTS ( ");
		sql.append(" 	SELECT ");
		sql.append(" 		1 ");
		sql.append(" 	FROM ");
		sql.append(" 		tab_account_finance_subject_map tafsm ");
		sql.append(" 	WHERE ");
		sql.append(" 		tfs.id = tafsm.financeSubjId ");
		sql.append(" 	AND tafsm.crewId = ? ");
		sql.append(" ) ");
		
		return this.query(sql.toString(), new Object[] {crewId, crewId}, FinanceSubjectModel.class, null);
		
	}
	

	/**
	 * 根据币种ID查询对应的财务科目
	 * @param crewId
	 * @param currencyId
	 * @return
	 */
	public List<FinanceSubjectModel> queryByCurrencyId(String crewId, String currencyId) {
		String sql = "SELECT tfs.* from tab_finance_subject tfs, tab_finanSubj_currency_map tfcm where tfs.id = tfcm.financeSubjId and tfcm.currencyId = ? and tfcm.crewId = ?;";
		return this.query(sql, new Object[] {currencyId, crewId}, FinanceSubjectModel.class, null);
	}
	
	/**
	 * 查询总费用进度
	 * @param crewId
	 * @return
	 */
	public Map<String, Object> queryTotalFinance(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT totalBudgetMoney,totalPayedMoney from ( ");
		sql.append(" select sum(tfcm.money * ftci.exchangeRate) totalBudgetMoney ");
		sql.append(" FROM tab_finanSubj_currency_map tfcm ");
		sql.append(" LEFT JOIN tab_currency_info ftci ON ftci.id = tfcm.currencyId ");
		sql.append(" WHERE tfcm.crewId = ?) a, ");
		sql.append(" (SELECT sum(tpi.totalMoney * ptci.exchangeRate) totalPayedMoney ");
		sql.append(" FROM tab_payment_info tpi  ");
		sql.append(" LEFT JOIN tab_currency_info ptci ON tpi.currencyId = ptci.id ");
		sql.append(" WHERE tpi.crewId = ? AND tpi.status = 1) b ");
		return this.getJdbcTemplate().queryForMap(sql.toString(), crewId, crewId);
	}
	
	/**
	 * 查询财务科目预算支出概况
	 * @param crewId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryBudgetPayedInfo(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT tfs.id,tfs.name,tfs.level,tfs.parentId,tfs.sequence,budgetMoney,payedMoney ");
		sql.append(" FROM tab_finance_subject tfs ");
		sql.append(" LEFT JOIN (SELECT tfcm.financeSubjId, ");
		sql.append(" ROUND(sum(tfcm.money * ftci.exchangeRate), 2) budgetMoney ");
		sql.append(" FROM tab_finanSubj_currency_map tfcm ");
		sql.append(" LEFT JOIN tab_currency_info ftci ON ftci.id = tfcm.currencyId ");
		sql.append(" WHERE tfcm.crewId = ? ");
		sql.append(" GROUP BY tfcm.financeSubjId ");
		sql.append(" ) budget ON tfs.id=budget.financeSubjId ");
		sql.append(" LEFT JOIN (SELECT tpfm.financeSubjId, ");
		sql.append(" ROUND(sum(tpfm.money * ptci.exchangeRate), 2) payedMoney ");
		sql.append(" FROM tab_payment_finanSubj_map tpfm ");
		sql.append(" LEFT JOIN tab_payment_info tpi ON tpi.paymentId = tpfm.paymentId AND tpi.status = 1 ");
		sql.append(" LEFT JOIN tab_currency_info ptci ON tpi.currencyId = ptci.id ");
		sql.append(" WHERE tpfm.crewId = ? ");
		sql.append(" GROUP BY tpfm.financeSubjId ");
		sql.append(" ) payed ON tfs.id=payed.financeSubjId ");
		sql.append(" WHERE tfs.crewId = ? ");
		sql.append(" order by tfs.level,tfs.sequence ");
		return this.query(sql.toString(), new Object[]{crewId, crewId, crewId}, null);
	}
	
	/**
	 * 查询自定义科目预算支出概况
	 * @param crewId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> querySelfBudgetPayedInfo(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT budget.*,payedMoney ");
		sql.append(" FROM  ");
		sql.append(" (SELECT tfag.groupId as id,tfag.groupName as name, ");
		sql.append(" 	ROUND(sum(tfcm.money * ftci.exchangeRate), 2) budgetMoney ");
		sql.append(" FROM tab_finance_account_group tfag ");
		sql.append(" LEFT JOIN tab_finance_account_group_map tfagm ON tfagm.groupId = tfag.groupId ");
		sql.append(" LEFT JOIN tab_finance_subject tfs ON tfs.id = tfagm.accountId ");
		sql.append(" LEFT JOIN tab_finanSubj_currency_map tfcm ON tfcm.financeSubjId = tfs.id ");
		sql.append(" LEFT JOIN tab_currency_info ftci ON ftci.id = tfcm.currencyId ");
		sql.append(" WHERE ");
		sql.append(" 	tfag.crewId = ? ");
		sql.append(" GROUP BY tfag.groupId,tfag.groupName) budget ");
		sql.append(" LEFT JOIN  ");
		sql.append(" (SELECT tfag.groupId as id,tfag.groupName as name, ");
		sql.append(" 	ROUND(sum(tpfm.money * ptci.exchangeRate), 2) payedMoney ");
		sql.append(" FROM tab_finance_account_group tfag ");
		sql.append(" LEFT JOIN tab_finance_account_group_map tfagm ON tfagm.groupId = tfag.groupId ");
		sql.append(" LEFT JOIN tab_finance_subject tfs ON tfs.id = tfagm.accountId ");
		sql.append(" LEFT JOIN tab_payment_finanSubj_map tpfm ON tpfm.financeSubjId=tfs.id ");
		sql.append(" LEFT JOIN tab_payment_info tpi ON tpi.paymentId = tpfm.paymentId AND tpi.STATUS = 1 ");
		sql.append(" LEFT JOIN tab_currency_info ptci ON tpi.currencyId = ptci.id ");
		sql.append(" WHERE ");
		sql.append(" 	tfag.crewId = ? ");
		sql.append(" GROUP BY tfag.groupId,tfag.groupName) payed on budget.id=payed.id ");
		return this.query(sql.toString(), new Object[]{crewId, crewId}, null);
	}
	
	/**
	 * 查询财务科目日支出
	 * @param crewId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryDayPayedInfo(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT paymentDate,tfs.id,tfs.level,tfs.parentId, ");
		sql.append(" 	ROUND(sum(tpfm.money * ptci.exchangeRate), 2) payedMoney ");
		sql.append(" FROM tab_finance_subject tfs ");
		sql.append(" LEFT JOIN tab_payment_finanSubj_map tpfm ON tpfm.financeSubjId = tfs.id ");
		sql.append(" LEFT JOIN tab_payment_info tpi ON tpi.paymentId = tpfm.paymentId and tpi.status = 1 ");
		sql.append(" LEFT JOIN tab_currency_info ptci ON tpi.currencyId = ptci.id ");
		sql.append(" WHERE tfs.crewId = ? ");
		sql.append(" group by paymentDate,tfs.id,tfs.level,tfs.parentId ");
		sql.append(" order by paymentDate,tfs.level,tfs.sequence ");
		return this.query(sql.toString(), new Object[]{crewId}, null);
	}
	
	/**
	 * 查询出费用结算中预算资金的总支出明细
	 * @param crewId
	 * @param financeSubjId
	 * @return
	 */
	public List<Map<String, Object>> queryFinanceSubPaymentList(String crewId, String financeSubjId){
		StringBuffer sql = new StringBuffer();
		sql.append("	SELECT");
		sql.append("		GROUP_CONCAT(tfs.id) id, SUM(tpfm.money) money, tpi.currencyId,");
		sql.append("		tpi.payeeName, tpi.totalMoney, tpi.receiptNo,");
		sql.append("		tpi.agent, tpi.paymentDate, tfpi.wayName paymentWay,");
		sql.append("		GROUP_CONCAT(IF(tpfm.summary = '',NULL,tpfm.summary) SEPARATOR '|') summary, tci.exchangeRate");
		sql.append("	FROM");
		sql.append("		tab_finance_subject tfs,");
		sql.append("		tab_payment_finanSubj_map tpfm,");
		sql.append("		tab_payment_info tpi,");
		sql.append("		tab_currency_info tci,");
		sql.append("		tab_finance_paymentWay_info tfpi ");
		sql.append("	WHERE");
		sql.append("		tfs.crewId = ?");
		sql.append("		AND tfs.id = ?");
		sql.append("		AND tci.id = tpi.currencyId");
		sql.append("		AND tpfm.financeSubjId = tfs.id");
		sql.append("		AND tpfm.paymentId = tpi.paymentId");
		sql.append("		AND tfpi.wayId = tpi.paymentWay");
		sql.append("		AND tpi. STATUS = 1");
		sql.append("		GROUP BY	tpi.paymentId");
		sql.append("		ORDER BY	sequence ");
		
		return this.query(sql.toString(), new Object[] {crewId,financeSubjId}, null);
	}
}
