package com.xiaotu.makeplays.finance.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.finance.model.ContractWorkerModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 职员合同
 * @author xuchangjian 2016-8-3下午5:11:09
 */
@Repository
public class ContractWorkerDao extends BaseDao<ContractWorkerModel> {
	
	/**
	 * 根据多个条件查询职员合同信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ContractWorkerModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + ContractWorkerModel.TABLE_NAME + " where 1 = 1 ");
		
		Set<String> keySet = conditionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		List<Object> conList = new LinkedList<Object>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = conditionMap.get(key);
			sql.append(" and " + key + " = ?");
			conList.add(value);
		}
		sql.append(" order by contractDate ");
		Object[] objArr = conList.toArray();
		List<ContractWorkerModel> contractList = this.query(sql.toString(), objArr, ContractWorkerModel.class, page);
		
		return contractList;
	}

	/**
	 * 根据财务科目查询职员合同
	 * @param crewId
	 * @param financeSubjId
	 * @return
	 */
	public List<ContractWorkerModel> queryByFinanceSubjId(String crewId, String financeSubjId) {
		String sql = "select * from " + ContractWorkerModel.TABLE_NAME + " where crewId = ? and financeSubjId = ? order by contractDate";
		return this.query(sql, new Object[] {crewId, financeSubjId}, ContractWorkerModel.class, null);
	}
	
	/**
	 * 根据剧组ID查询职员合同
	 * @param crewId
	 * @return
	 */
	public List<ContractWorkerModel> queryByCrewId(String crewId) {
		String sql = "select * from " + ContractWorkerModel.TABLE_NAME + " where crewId = ? order by contractDate";
		return this.query(sql, new Object[] {crewId}, ContractWorkerModel.class, null);
	}
	
	/**
	 * 查询剧组下带有财务科目的职员合同
	 * @param crewId
	 * @return
	 */
	public List<ContractWorkerModel> queryFinanContract(String crewId) {
		String sql = "select * from " + ContractWorkerModel.TABLE_NAME + " where crewId = ? and (financeSubjId != '' && financeSubjId is not null) order by contractDate";
		return this.query(sql, new Object[] {crewId}, ContractWorkerModel.class, null);
	}
	
	/**
	 * 查询职员合同的预算
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryContractWorkerBudget(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tcw.contractId, ");
		sql.append(" 	tcw.financeSubjId, ");
		sql.append(" 	tci.exchangeRate, ");
		sql.append(" 	tcw.totalMoney, ");//总金额
		sql.append(" 	tcw.contractNo, ");//合同编号
		sql.append(" 	tcw.workerName, ");//职员姓名
		sql.append(" 	tcw.contractDate, ");//签订日期
		sql.append(" 	tcw.remark, ");
		sql.append(" 	tcw.payWay, ");
		sql.append(" 	tcw.financeSubjName ");
		sql.append(" FROM ");
		sql.append(" 	tab_contract_worker tcw, ");
		sql.append(" 	tab_currency_info tci, ");
		sql.append(" 	tab_finance_subject tfs ");	//添加财务科目表关联只是为了过滤掉合同中关联的无效的财务科目
		sql.append(" WHERE ");
		sql.append(" 	tcw.crewId = ? ");
		sql.append(" AND tcw.currencyId = tci.id ");
		sql.append(" AND tcw.financeSubjId is not null ");
		sql.append(" AND tcw.financeSubjId != '' ");
		sql.append(" AND tci.crewId=? ");
		sql.append(" AND tfs.id = tcw.financeSubjId  ");
		sql.append(" AND tfs.crewId = ? ");
		
		return this.query(sql.toString(), new Object[] {crewId, crewId, crewId}, null);
	}
	
	/**
	 * 根据高级查询条件查询职员合同
	 * @param workerNames	职员姓名，多个以逗号隔开
	 * @param department	部门职务
	 * @param paymentTerm	支付条件
	 * @param remark	备注
	 * @return 合同ID，职员名称，合同编号，部门职务，总金额，已付金额，未付金额，合同关联货币ID，编码，货币汇率
	 */
	public List<Map<String, Object>> queryByAnvanceCondition(String crewId, String workerNames, String department, String financeSubjIds, Integer payWay, String paymentTerm, String remark, String paymentStartDate, String paymentEndDate) {
		List<Object> paramsList = new ArrayList<Object>();
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tcw.contractId, ");
		sql.append(" 	tcw.workerName, ");
		sql.append(" 	tcw.contractNo, ");
		sql.append(" 	tcw.department, ");
		sql.append(" 	tcw.totalMoney, ");
		sql.append(" 	tcw.financeSubjId, ");
		sql.append("    tcw.financeSubjName, ");
		sql.append(" 	if (round(SUM(tpi.totalMoney), 2) is NULL, 0, round(SUM(tpi.totalMoney), 2)) payedMoney, ");
		sql.append(" 	round((tcw.totalMoney - if (round(SUM(tpi.totalMoney), 2) is NULL, 0, round(SUM(tpi.totalMoney), 2))), 2) AS leftMoney, ");
		sql.append("    tci.id currencyId, ");
		sql.append(" 	tci.code currencyCode, ");
		sql.append("    tci.exchangeRate, ");
		sql.append("	tpi.receiptNo,");
		sql.append("	tpi.paymentDate,");
		sql.append("	tpi.payeeName,");
		sql.append("	tpi.agent,");
		sql.append("	tpi.totalMoney paymentTotalMoney");
		sql.append(" FROM ");
		sql.append(" 	tab_contract_worker tcw ");
		sql.append(" LEFT JOIN tab_payment_info tpi ON 1 = 1 ");
		sql.append("  AND tcw.contractId = tpi.contractId ");
		sql.append("  AND tpi.contractType = 1 ");
		if (!StringUtils.isBlank(paymentStartDate)) {
			sql.append("  AND tpi.paymentDate >= ? ");
			paramsList.add(paymentStartDate);
		}
		if (!StringUtils.isBlank(paymentEndDate)) {
			sql.append("  AND tpi.paymentDate <= ? ");
			paramsList.add(paymentEndDate);
		}
		sql.append("  AND tpi.`status` = 1, ");
		sql.append(" tab_currency_info tci ");	//合同关联的货币
		sql.append(" WHERE ");
		sql.append(" 	tcw.crewId = ? ");
		paramsList.add(crewId);
		sql.append(" 	AND tci.id = tcw.currencyId ");
		if (!StringUtils.isBlank(workerNames)) {
			workerNames = "'" + workerNames.replace(",", "','") + "'";
			sql.append(" and tcw.workerName in ("+ workerNames +") ");
		}
		if (!StringUtils.isBlank(department)) {
			sql.append(" and tcw.department like ? ");
			paramsList.add("%" + department + "%");
		}
		if (!StringUtils.isBlank(financeSubjIds)) {
			financeSubjIds = "'" + financeSubjIds.replace(",", "','") + "'";
			sql.append(" and tcw.financeSubjId in("+ financeSubjIds +") ");
		}
		if (payWay != null) {
			sql.append(" and tcw.payWay = ? ");
			paramsList.add(payWay);
		}
		if (!StringUtils.isBlank(paymentTerm)) {
			sql.append(" and tcw.paymentTerm like ? ");
			paramsList.add("%" + paymentTerm + "%");
		}
		if (!StringUtils.isBlank(remark)) {
			sql.append(" and tcw.remark like ? ");
			paramsList.add("%" + remark + "%");
		}
		sql.append(" GROUP BY ");
		sql.append(" 	tcw.contractId, ");
		sql.append(" 	tcw.workerName, ");
		sql.append(" 	tcw.contractNo, ");
		sql.append(" 	tcw.department, ");
		sql.append(" 	tcw.totalMoney, ");
		sql.append("    tci.id, ");
		sql.append(" 	tci.code, ");
		sql.append("    tci.exchangeRate ");
		sql.append(" ORDER BY ");
		sql.append(" 	tcw.contractNo DESC ");
		
		return this.query(sql.toString(), paramsList.toArray(), null);
	}
	
	/**
	 * 查询职员合同中的付款单列表
	 * @param crewId
	 * @param financeSubjId
	 * @return
	 */
	public List<Map<String, Object>> queryContractWorkerPayemtnList(String crewId, String financeSubjId){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tcw.workerName, ");
		sql.append(" 	tcw.contractNo, ");
		sql.append(" 	tcw.department, ");
		sql.append(" 	tci.exchangeRate, ");
		sql.append("    GROUP_CONCAT(tfm.financeSubjId) financeSubjId,");
		sql.append("	tpi.receiptNo,");
		sql.append("	tpi.paymentDate,");
		sql.append("	tpi.payeeName,");
		sql.append("	tpi.agent,");
		sql.append("	tpi.totalMoney paymentTotalMoney,");
		sql.append("	tfpi.wayName paymentWay,");
		sql.append("	GROUP_CONCAT(IF(tfm.summary = '',NULL,tfm.summary) SEPARATOR '|') remark");
		sql.append(" FROM ");
		sql.append(" 	tab_payment_info tpi ");
		sql.append(" LEFT JOIN tab_contract_worker tcw ON 1 = 1 ");
		sql.append("  AND tcw.contractId = tpi.contractId ");
		sql.append("  AND tpi.contractType = 1 ");
		sql.append("  AND tpi.`status` = 1 ");
		sql.append(" LEFT JOIN tab_currency_info tci ON tcw.currencyId = tci.id ");
		sql.append(" LEFT JOIN tab_payment_finanSubj_map tfm ON tfm.paymentId = tpi.paymentId ");
		sql.append(" LEFT JOIN tab_finance_paymentWay_info tfpi ON tfpi.wayId = tpi.paymentWay ");
		sql.append(" WHERE ");
		sql.append(" 	tcw.crewId = ? ");
		sql.append("	and tcw.financeSubjId = ?");
		sql.append(" 	group by tpi.paymentId ");
		sql.append(" ORDER BY ");
		sql.append(" 	tcw.contractNo DESC ");
		
		return this.query(sql.toString(), new Object[] {crewId, financeSubjId},  null);
	}
	
	/**
	 * 查询剧组中职员合同的最大编号
	 * @param crewId
	 * @return
	 */
	public String queryMaxContractNo(String crewId) {
		String sql = "SELECT max(contractNo) from tab_contract_worker where crewId = ?";
		return this.getJdbcTemplate().queryForObject(sql, new Object[] {crewId}, String.class);
	}
	
	/**
	 * 根据ID查询合同信息
	 * @param crewId
	 * @param contractId
	 * @return
	 * @throws Exception
	 */
	public ContractWorkerModel queryById (String crewId, String contractId) throws Exception {
		String sql = "select * from " + ContractWorkerModel.TABLE_NAME + " where crewId = ? and contractId = ?";
		return this.queryForObject(sql, new Object[] {crewId, contractId}, ContractWorkerModel.class);
	}
	
	/**
	 * 根据姓名查询职务信息
	 * @param contactName
	 * @return
	 */
	public List<Map<String, Object>> queryDepartmentByContactName(String contactName, String crewId){
		String sql = " SELECT	tcc.contactId,GROUP_CONCAT(DISTINCT tsi.roleName) department FROM tab_crew_contact tcc LEFT JOIN tab_contact_sysrole_map tcsm ON tcsm.contactId = tcc.contactId"
				+ " LEFT JOIN tab_sysrole_info tsi ON tsi.roleId = tcsm.sysroleId WHERE tcc.crewId = ? and tcc.contactName = ?";
		return this.query(sql, new Object[] {crewId, contactName}, null);
	}
}
