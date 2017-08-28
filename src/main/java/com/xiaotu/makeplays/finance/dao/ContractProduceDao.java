package com.xiaotu.makeplays.finance.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.finance.model.ContractProduceModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 制作合同
 * @author xuchangjian 2016-8-3下午5:09:49
 */
@Repository
public class ContractProduceDao extends BaseDao<ContractProduceModel> {
	
	/**
	 * 根据多个条件查询生产合同信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ContractProduceModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + ContractProduceModel.TABLE_NAME + " where 1 = 1 ");
		
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
		List<ContractProduceModel> contractList = this.query(sql.toString(), objArr, ContractProduceModel.class, page);
		
		return contractList;
	}
	
	/**
	 * 根据ID查询合同
	 * @param crewId
	 * @param contractId
	 * @return
	 * @throws Exception 
	 */
	public ContractProduceModel queryById(String crewId, String contractId) throws Exception {
		String sql = "select * from " + ContractProduceModel.TABLE_NAME + " where crewId = ? and contractId = ? order by contractDate";
		return this.queryForObject(sql, new Object[] {crewId, contractId}, ContractProduceModel.class);
	}

	/**
	 * 根据财务科目ID查询合同
	 * @param crewId
	 * @param financeSubjId
	 * @return
	 */
	public List<ContractProduceModel> queryByFinanceSubjId(String crewId, String financeSubjId) {
		String sql = "select * from " + ContractProduceModel.TABLE_NAME + " where crewId = ? and financeSubjId = ? order by contractDate";
		return this.query(sql, new Object[] {crewId, financeSubjId}, ContractProduceModel.class, null);
	}
	
	/**
	 * 根据剧组ID查询合同
	 * @param crewId
	 * @return
	 */
	public List<ContractProduceModel> queryByCrewId(String crewId) {
		String sql = "select * from " + ContractProduceModel.TABLE_NAME + " where crewId = ? ";
		return this.query(sql, new Object[] {crewId}, ContractProduceModel.class, null);
	}
	
	/**
	 * 根据剧组ID查询合同
	 * @param crewId
	 * @return
	 */
	public List<ContractProduceModel> queryFinanContract(String crewId) {
		String sql = "select * from " + ContractProduceModel.TABLE_NAME + " where crewId = ? and (financeSubjId != '' && financeSubjId is not null) ";
		return this.query(sql, new Object[] {crewId}, ContractProduceModel.class, null);
	}
	
	/**
	 * 查询制作合同的预算
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryContractProduceBudget(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tcp.contractId, ");
		sql.append(" 	tcp.financeSubjId, ");
		sql.append(" 	tci.exchangeRate, ");
		sql.append(" 	tcp.totalMoney, ");
		sql.append(" 	tcp.contractNo, ");
		sql.append(" 	tcp.contractDate, ");
		sql.append(" 	tcp.company, ");
		sql.append(" 	tcp.remark, ");
		sql.append(" 	tcp.payWay, ");
		sql.append(" 	tcp.financeSubjName ");
		sql.append(" FROM ");
		sql.append(" 	tab_contract_produce tcp, ");
		sql.append(" 	tab_currency_info tci, ");
		sql.append(" 	tab_finance_subject tfs ");	//添加财务科目表关联只是为了过滤掉合同中关联的无效的财务科目
		sql.append(" WHERE ");
		sql.append(" 	tcp.crewId = ? ");
		sql.append(" AND tcp.currencyId = tci.id ");
		sql.append(" AND tcp.financeSubjId is not null ");
		sql.append(" AND tcp.financeSubjId != '' ");
		sql.append(" AND tci.crewId=? ");
		sql.append(" AND tfs.id = tcp.financeSubjId  ");
		sql.append(" AND tfs.crewId = ? ");
		
		return this.query(sql.toString(), new Object[] {crewId, crewId, crewId}, null);
	}
	
	/**
	 * 根据高级查询条件查询职员合同
	 * @param companys	单位，多个以逗号隔开
	 * @param contactPersons	联系人，多个以逗号隔开
	 * @param paymentTerm	支付条件
	 * @param remark	备注
	 * @return 合同ID，职员名称，合同编号，部门职务，总金额，已付金额，未付金额，合同关联货币ID，编码，货币汇率
	 */
	public List<Map<String, Object>> queryByAnvanceCondition(String crewId,  String companys, String contactPersons, String financeSubjIds, Integer payWay, String paymentTerm, String remark, String paymentStartDate, String paymentEndDate) {
		List<Object> paramsList = new ArrayList<Object>();
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tcp.contractId, ");
		sql.append(" 	tcp.company, ");
		sql.append(" 	tcp.contractNo, ");
		sql.append(" 	tcp.contactPerson, ");
		sql.append(" 	tcp.totalMoney, ");
		sql.append("    tcp.financeSubjId, ");
		sql.append("    tcp.financeSubjName, ");
		sql.append(" 	if (round(SUM(tpi.totalMoney), 2) is NULL, 0, round(SUM(tpi.totalMoney), 2)) payedMoney, ");
		sql.append(" 	round((tcp.totalMoney - if (round(SUM(tpi.totalMoney), 2) is NULL, 0, round(SUM(tpi.totalMoney), 2))), 2) AS leftMoney, ");
		sql.append("    tci.id currencyId, ");
		sql.append(" 	tci.code currencyCode, ");
		sql.append("    tci.exchangeRate, ");
		sql.append("	tpi.receiptNo,");
		sql.append("	tpi.paymentDate,");
		sql.append("	tpi.payeeName,");
		sql.append("	tpi.agent,");
		sql.append("	tpi.totalMoney paymentTotalMoney");
		sql.append(" FROM ");
		sql.append(" 	tab_contract_produce tcp ");
		sql.append(" LEFT JOIN tab_payment_info tpi ON 1 = 1 ");
		sql.append("  AND tcp.contractId = tpi.contractId ");
		sql.append("  AND tpi.contractType = 3 ");
		if (!StringUtils.isBlank(paymentStartDate)) {
			sql.append("  AND tpi.paymentDate >= ? ");
			paramsList.add(paymentStartDate);
		}
		if (!StringUtils.isBlank(paymentEndDate)) {
			sql.append("  AND tpi.paymentDate <= ? ");
			paramsList.add(paymentEndDate);
		}
		sql.append("  AND tpi.`status` = 1, ");
		sql.append(" tab_currency_info tci ");	//合同中关联的货币
		sql.append(" WHERE ");
		sql.append(" 	tcp.crewId = ? ");
		paramsList.add(crewId);
		sql.append(" 	AND tci.id = tcp.currencyId ");
		if (!StringUtils.isBlank(companys)) {
			companys = "'" + companys.replace(",", "','") + "'";
			sql.append(" and tcp.company in ("+ companys +") ");
		}
		if (!StringUtils.isBlank(contactPersons)) {
			contactPersons = "'" + contactPersons.replace(",", "','") + "'";
			sql.append(" and tcp.contactPerson in ("+ contactPersons +") ");
		}
		if (!StringUtils.isBlank(financeSubjIds)) {
			financeSubjIds = "'" + financeSubjIds.replace(",", "','") + "'";
			sql.append(" and tcp.financeSubjId in("+ financeSubjIds +") ");
		}
		if (payWay != null) {
			sql.append(" and tcp.payWay = ? ");
			paramsList.add(payWay);
		}
		if (!StringUtils.isBlank(paymentTerm)) {
			sql.append(" and tcp.paymentTerm like ? ");
			paramsList.add("%" + paymentTerm + "%");
		}
		if (!StringUtils.isBlank(remark)) {
			sql.append(" and tcp.remark like ? ");
			paramsList.add("%" + remark + "%");
		}
		sql.append(" GROUP BY ");
		sql.append(" 	tcp.contractId, ");
		sql.append(" 	tcp.company, ");
		sql.append(" 	tcp.contractNo, ");
		sql.append(" 	tcp.contactPerson, ");
		sql.append(" 	tcp.totalMoney, ");
		sql.append("    tci.id, ");
		sql.append(" 	tci.code, ");
		sql.append("    tci.exchangeRate ");
		sql.append(" ORDER BY ");
		sql.append(" 	tcp.contractNo DESC; ");
		
		return this.query(sql.toString(), paramsList.toArray(), null);
	}
	
	/**
	 * 查询制作合同中的付款单列表
	 * @param crewId
	 * @param finaceSubjId
	 * @return
	 */
	public List<Map<String, Object>> queryContractProducePaymentList(String crewId, String finaceSubjId){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tcp.contractId, ");
		sql.append(" 	tcp.company, ");
		sql.append(" 	tcp.contractNo, ");
		sql.append(" 	tcp.contactPerson, ");
		sql.append(" 	tci.exchangeRate, ");
		sql.append("    GROUP_CONCAT(tfm.financeSubjId) financeSubjId,");
		sql.append("	tpi.receiptNo,");
		sql.append("	tpi.paymentDate,");
		sql.append("	tpi.payeeName,");
		sql.append("	tpi.totalMoney paymentTotalMoney,");
		sql.append("	tfpi.wayName paymentWay,");
		sql.append("	GROUP_CONCAT(IF(tfm.summary = '',NULL,tfm.summary) SEPARATOR '|') remark");
		sql.append(" FROM ");
		sql.append(" 	tab_payment_info tpi ");
		sql.append(" LEFT JOIN tab_contract_produce tcp ON 1 = 1 ");
		sql.append("  AND tcp.contractId = tpi.contractId ");
		sql.append("  AND tpi.contractType = 3 ");
		sql.append("  AND tpi.`status` = 1 ");
		sql.append(" LEFT JOIN tab_currency_info tci ON tcp.currencyId = tci.id ");
		sql.append(" LEFT JOIN tab_payment_finanSubj_map tfm ON tfm.paymentId = tpi.paymentId ");
		sql.append(" LEFT JOIN tab_finance_paymentWay_info tfpi ON tfpi.wayId = tpi.paymentWay ");
		sql.append(" WHERE ");
		sql.append(" 	tcp.crewId = ? ");
		sql.append("	and tcp.financeSubjId = ?");
		sql.append(" 	group by tpi.paymentId ");
		sql.append(" ORDER BY ");
		sql.append(" 	tcp.contractNo DESC; ");
		
		return this.query(sql.toString(), new Object[] {crewId, finaceSubjId}, null);
	}
	
	/**
	 * 查询剧组中制作合同的最大编号
	 * @param crewId
	 * @return
	 */
	public String queryMaxContractNo(String crewId) {
		String sql = "SELECT max(contractNo) from tab_contract_produce where crewId = ?";
		return this.getJdbcTemplate().queryForObject(sql, new Object[] {crewId}, String.class);
	}

	/**
	 * 根据剧组ID清空财务科目关联信息，用于费用预算导入
	 * @param crewId
	 */
	public void deleteFinanceSubjectByCrewId(String crewId) {
		String sql = "update " + ContractProduceModel.TABLE_NAME + " set financeSubjId=null,financeSubjName=null where crewId = ? ";
		this.getJdbcTemplate().update(sql, new Object[]{crewId});
	}
}
