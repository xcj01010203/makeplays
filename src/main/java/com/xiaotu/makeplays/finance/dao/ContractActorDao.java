package com.xiaotu.makeplays.finance.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.finance.model.ContractActorModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 演员合同
 * @author xuchangjian 2016-8-3下午5:09:09
 */
@Repository
public class ContractActorDao extends BaseDao<ContractActorModel>{

	/**
	 * 根据多个条件查询演员合同信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ContractActorModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + ContractActorModel.TABLE_NAME + " where 1 = 1 ");
		
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
		List<ContractActorModel> contractList = this.query(sql.toString(), objArr, ContractActorModel.class, page);
		
		return contractList;
	}
	
	/**
	 * 根据财务科目ID查询演员合同
	 * @param crewId
	 * @param financeSubjId
	 * @return
	 */
	public List<ContractActorModel> queryByFinanceSubjId(String crewId, String financeSubjId) {
		String sql = "select * from " + ContractActorModel.TABLE_NAME + " where crewId = ? and financeSubjId = ? order by contractDate";
		return this.query(sql, new Object[] {crewId, financeSubjId}, ContractActorModel.class, null);
	}
	
	/**
	 * 根据剧组ID查询演员合同
	 * @param crewId
	 * @return
	 */
	public List<ContractActorModel> queryByCrewId(String crewId) {
		String sql = "select * from " + ContractActorModel.TABLE_NAME + " where crewId = ? order by contractDate";
		return this.query(sql, new Object[] {crewId}, ContractActorModel.class, null);
	}
	
	/**
	 * 查询剧组下带有财务科目的演员合同
	 * @param crewId
	 * @return
	 */
	public List<ContractActorModel> queryFinanContract(String crewId) {
		String sql = "select * from " + ContractActorModel.TABLE_NAME + " where crewId = ? and (financeSubjId != '' && financeSubjId is not null) order by contractDate";
		return this.query(sql, new Object[] {crewId}, ContractActorModel.class, null);
	}
	
	/**
	 * 查询演员合同的预算
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryContractActorBudget(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tca.contractId, ");
		sql.append(" 	tca.financeSubjId, ");
		sql.append(" 	tci.exchangeRate, ");
		sql.append(" 	tca.totalMoney, ");
		sql.append(" 	tca.contractNo, ");
		sql.append(" 	tca.actorName, ");
		sql.append(" 	tca.remark, ");
		sql.append(" 	tca.payWay, ");
		sql.append(" 	tca.contractDate, ");
		sql.append(" 	tca.financeSubjName ");
		sql.append(" FROM ");
		sql.append(" 	tab_contract_actor tca, ");
		sql.append(" 	tab_currency_info tci, ");
		sql.append(" 	tab_finance_subject tfs ");	//添加财务科目表关联只是为了过滤掉合同中关联的无效的财务科目
		sql.append(" WHERE ");
		sql.append(" 	tca.crewId = ? ");
		sql.append(" AND tca.currencyId = tci.id ");
		sql.append(" AND tca.financeSubjId is not null ");
		sql.append(" AND tca.financeSubjId != '' ");
		sql.append(" AND tci.crewId=? ");
		sql.append(" AND tfs.id = tca.financeSubjId  ");
		sql.append(" AND tfs.crewId = ? ");
		
		return this.query(sql.toString(), new Object[] {crewId, crewId, crewId}, null);
	}
	
	/**
	 * 根据高级查询条件查询演员合同
	 * @param actorNames	演员名称，多个以逗号隔开
	 * @param roleNames	角色名称，多个以逗号隔开
	 * @param paymentTerm	支付条件
	 * @param remark	备注
	 * @return 合同ID，演员名称，合同编号，角色名称，总金额，已付金额，未付金额，本位币编码
	 */
	public List<Map<String, Object>> queryByAnvanceCondition(String crewId,  String actorNames, String roleNames, String financeSubjIds, Integer payWay, String paymentTerm, String remark, String paymentStartDate, String paymentEndDate) {
		List<Object> paramsList = new ArrayList<Object>();
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tca.contractId, ");
		sql.append(" 	tca.actorName, ");
		sql.append(" 	tca.contractNo, ");
		sql.append(" 	tca.roleName, ");
		sql.append(" 	tca.totalMoney, ");
		sql.append("    tca.financeSubjId, ");
		sql.append("    tca.financeSubjName, ");
		sql.append(" 	if (round(SUM(tpi.totalMoney), 2) is NULL, 0, round(SUM(tpi.totalMoney), 2)) payedMoney, ");
		sql.append(" 	round((tca.totalMoney - if (round(SUM(tpi.totalMoney), 2) is NULL, 0, round(SUM(tpi.totalMoney), 2))), 2) AS leftMoney, ");
		sql.append("    tci.id currencyId, ");
		sql.append(" 	tci.code currencyCode, ");
		sql.append("    tci.exchangeRate, ");
		sql.append("	tpi.receiptNo,");
		sql.append("	tpi.paymentDate,");
		sql.append("	tpi.payeeName,");
		sql.append("	tpi.agent,");
		sql.append("	tpi.totalMoney paymentTotalMoney");
		sql.append(" FROM ");
		sql.append(" 	tab_contract_actor tca ");
		sql.append(" LEFT JOIN tab_payment_info tpi ON 1 = 1 ");
		sql.append("  AND tca.contractId = tpi.contractId ");
		sql.append("  AND tpi.contractType = 2 ");
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
		sql.append(" 	tca.crewId = ? ");
		paramsList.add(crewId);
		sql.append(" 	AND tci.id = tca.currencyId ");
		if (!StringUtils.isBlank(actorNames)) {
			actorNames = "'" + actorNames.replace(",", "','") + "'";
			sql.append(" and tca.actorName in ("+ actorNames +") ");
		}
		if (!StringUtils.isBlank(roleNames)) {
			roleNames = "'" + roleNames.replace(",", "','") + "'";
			sql.append(" and tca.roleName int ("+ roleNames +") ");
		}
		if (!StringUtils.isBlank(financeSubjIds)) {
			financeSubjIds = "'" + financeSubjIds.replace(",", "','") + "'";
			sql.append(" and tca.financeSubjId in("+ financeSubjIds +") ");
		}
		if (payWay != null) {
			sql.append(" and tca.payWay = ? ");
			paramsList.add(payWay);
		}
		if (!StringUtils.isBlank(paymentTerm)) {
			sql.append(" and tca.paymentTerm like ? ");
			paramsList.add("%" + paymentTerm + "%");
		}
		if (!StringUtils.isBlank(remark)) {
			sql.append(" and tca.remark like ? ");
			paramsList.add("%" + remark + "%");
		}
		sql.append(" GROUP BY ");
		sql.append(" 	tca.contractId, ");
		sql.append(" 	tca.actorName, ");
		sql.append(" 	tca.contractNo, ");
		sql.append(" 	tca.roleName, ");
		sql.append(" 	tca.totalMoney, ");
		sql.append("    tci.id, ");
		sql.append(" 	tci.code, ");
		sql.append("    tci.exchangeRate ");
		sql.append(" ORDER BY ");
		sql.append(" 	tca.contractNo DESC; ");
		
		return this.query(sql.toString(), paramsList.toArray(), null);
	}
	
	/**
	 * 查询演员合同的付款单列表详情
	 * @param crewId
	 * @param financeSubjId
	 * @return
	 */
	public List<Map<String, Object>> queryContractActorPaymentList(String crewId, String financeSubjId){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ");
		sql.append(" 	tca.actorName, ");
		sql.append(" 	tca.contractNo, ");
		sql.append(" 	tca.roleName, ");
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
		sql.append(" LEFT JOIN tab_contract_actor tca ON 1 = 1 ");
		sql.append("  AND tca.contractId = tpi.contractId ");
		sql.append("  AND tpi.contractType = 2 ");
		sql.append("  AND tpi.`status` = 1 ");
		sql.append(" LEFT JOIN tab_currency_info tci ON tca.currencyId = tci.id ");
		sql.append(" LEFT JOIN tab_payment_finanSubj_map tfm ON tfm.paymentId = tpi.paymentId ");
		sql.append(" LEFT JOIN tab_finance_paymentWay_info tfpi ON tfpi.wayId = tpi.paymentWay ");
		sql.append(" WHERE ");
		sql.append(" 	tca.crewId = ? ");
		sql.append(" 	and tca.financeSubjId = ? ");
		sql.append(" 	group by tpi.paymentId ");
		sql.append(" ORDER BY ");
		sql.append(" 	tca.contractNo DESC; ");
		
		return this.query(sql.toString(), new Object[] {crewId, financeSubjId}, null);
	}
	
	/**
	 * 查询剧组中演员合同的最大编号
	 * @param crewId
	 * @return
	 */
	public String queryMaxContractNo(String crewId) {
		String sql = "SELECT max(contractNo) from tab_contract_actor where crewId = ?";
		return this.getJdbcTemplate().queryForObject(sql, new Object[] {crewId}, String.class);
	}
	
	/**
	 * 根据ID查询合同
	 * @param crewId
	 * @param contractId
	 * @return
	 * @throws Exception 
	 */
	public ContractActorModel queryById(String crewId, String contractId) throws Exception {
		String sql = "select * from " + ContractActorModel.TABLE_NAME + " where crewId = ? and contractId = ?";
		return this.queryForObject(sql, new Object[] {crewId, contractId}, ContractActorModel.class);
	}
	
	/**
	 * 根据演员姓名查询演员所扮演的角色
	 * @param crewId
	 * @param actorName
	 * @return
	 */
	public List<Map<String, Object>> queryViewRoleNameByActorName(String crewId, String actorName){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT	tvr.viewRoleName");
		sql.append(" FROM tab_actor_info tai");
		sql.append(" LEFT JOIN tab_actor_role_map tarm ON tarm.actorId = tai.actorId");
		sql.append(" LEFT JOIN tab_view_role tvr ON tvr.viewRoleId = tarm.viewRoleId");
		sql.append(" WHERE tai.crewId = ?");
		sql.append(" AND tai.actorName = ?");
		
		return this.query(sql.toString(), new Object[] {crewId, actorName}, null);
	}
	
}
