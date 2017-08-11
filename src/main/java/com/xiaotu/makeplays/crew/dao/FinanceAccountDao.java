package com.xiaotu.makeplays.crew.dao;


import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.sysrole.model.SysroleInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.StringUtils;

@Deprecated
@Repository
public class FinanceAccountDao extends BaseDao<SysroleInfoModel> {
 /**
  * 查询自定义科目组
  * @param crewId 剧组ID
  * @return
  * @throws Exception
  */
   public List<Map<String, Object>> getFinanceAccountGroupList(String  crewId)throws SQLException{
	   String sql="SELECT * FROM tab_finance_account_group WHERE crewId=?";
	   return this.query(sql, new Object[]{crewId}, null);
   }
   public List<Map<String, Object>> getFinanceAccountGroupList(String  crewId,String groupName)throws SQLException{
	   String sql="SELECT groupId FROM tab_finance_account_group WHERE crewId=? AND groupName=?";
	   return this.query(sql, new Object[]{crewId,groupName}, null);
   }
	/**
	 * 查询开始日期
	 * @param crewId 剧组 ID
	 * @return
	 */
	public String getShotStartDate(String crewId)throws SQLException{
			String sql = "SELECT DATE_FORMAT(shootStartDate, '%Y/%m/%d') AS shootStartDate FROM tab_crew_info WHERE crewId = ?";
		List<Map<String, String>> list =this.query(sql, new Object[]{crewId}, null);
		Map<String, String> dateStr=list.get(0);
		return dateStr.get("shootStartDate");
	}
 /**
  * 数据结果集
  * @param crewId
  * @return
  * @throws SQLException
  */
	public List<Map<String, Object>> getBudgetDBList(String crewId) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT f.accountId,f.accountName ,f.accountLevel,f.parentId,f.sequence,f.remark");
		sql.append(",c.currencyId,c.currencyName,c.currencyCode,IF(b.money IS NULL,0,b.money) AS money,c.exchangeRate,c.ifStandard");
		sql.append(" FROM tab_finance_budget_account AS f  ");
		sql.append(" LEFT JOIN tab_currency_info AS c ON c.crewId=? AND c.ifEnable=1");
		sql.append(" LEFT JOIN tab_account_currency_map AS b ON f.accountId=b.accountId AND c.currencyId=b.currencyId");
		sql.append(" WHERE f.crewId=? ORDER BY f.sequence,c.ifStandard DESC,c.currencyName");
		return this.query(sql.toString(), new Object[]{crewId, crewId},null);
	}
	/**
	 * 获取所有的自定义科目组-财务的映射信息
	 * @param crewId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getFinanceAccountGroupMapList(String crewId)throws SQLException{
		String sql="SELECT mapId,accountId,groupId,crewId FROM tab_finance_account_group_map where crewId=?";
		return this.query(sql, new Object[]{crewId}, null);
	}
	public List<Map<String, Object>> getFinanceAccountGroupMapList(String crewId,String groupId)throws SQLException{
		String sql="SELECT accountId FROM tab_finance_account_group_map where crewId=? and groupId=?";
		return this.query(sql, new Object[]{crewId,groupId}, null);
	}
	public List<Map<String, Object>> getDistinctFinanceAccountGroupMapList(String crewId)throws SQLException{
		String sql="SELECT DISTINCT accountId FROM tab_finance_account_group_map where crewId=? ";
		return this.query(sql, new Object[]{crewId}, null);
	}
	/**
	 * 获取薪酬表中的记录
	 * @param crewId
	 * @param salaryDate
	 * 		薪酬日期
	 *是否根据日期查询 ,0:不根据薪酬日期查询, 1:根据薪酬日期查询
	 */
	public List<Map<String, Object>> getSalaryList(String crewId,String startDate, String endDate)throws SQLException{
		StringBuffer sb = new StringBuffer();
		//tab_salary_info 薪酬表（之前查的是薪酬，改成了结算表）
		/*sb.append("SELECT fsb.accountName, currency.exchangeRate, currency.currencyCode, currency.currencyName,DATE_FORMAT(sc.createTime,'%Y/%m/%d') AS journalDate");
		sb.append(",currency.currencyId,contract.workerName,contract.department, sc.* ");
		sb.append(" FROM tab_salary_info AS sc LEFT OUTER JOIN tab_contract_worker contract ON sc.contractId = contract.contractId");
		sb.append(" LEFT OUTER JOIN tab_currency_info currency ON currency.currencyId= sc.currencyId");
		sb.append(" LEFT OUTER JOIN tab_finance_budget_account AS fsb ON fsb.accountId = sc.accountId");*/
		sb.append("SELECT fsb.accountName, tci.exchangeRate, tci.currencyCode, tci.currencyName,DATE_FORMAT(paymentDate,'%Y/%m/%d') AS journalDate");
		sb.append(" ,tci.currencyId,tpi.status,tpsm.accountId,tpsm.money as monthlySalary,tpi.crewId FROM tab_payment_info  tpi");
		sb.append(" LEFT JOIN tab_payment_account_map tpsm ON tpi.paymentId = tpsm.paymentId");   
		sb.append(" LEFT JOIN tab_finance_budget_account fsb ON fsb.accountId=tpsm.accountId");
		sb.append(" LEFT JOIN tab_currency_info tci ON tci.currencyId = tpi.currencyId");
		sb.append(" WHERE tpi.crewId = ? AND tpi.status = 1");
		sb.append(" GROUP BY tpsm.paymentId,tpsm.accountId,tci.currencyName,tci.currencyCode,tci.exchangeRate ORDER BY tpi.paymentDate");
		return this.query(sb.toString(), new Object[]{crewId}, null);
		/*if(StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate)) {
			sb.append(" WHERE tpi.crewId = ? AND tpi.status = 1");
			sb.append(" GROUP BY tpsm.paymentId,tpsm.accountId,tci.currencyName,tci.currencyCode,tci.exchangeRate");
			//sb.append("  WHERE sc.crewId =? AND sc.status = 1");
			return this.query(sb.toString(), new Object[]{crewId}, null);
		}else {
			//sb.append(" WHERE tpi.crewId = ? AND tpi.status = 1 AND tpi.paymentDate BETWEEN ? AND ?");
			sb.append(" GROUP BY tpsm.paymentId,tpsm.accountId,tci.currencyName,tci.currencyCode,tci.exchangeRate ORDER BY tpi.paymentDate");
			//sb.append(" WHERE sc.crewId = ? AND sc.status = 1 AND sc.createTime BETWEEN ? AND ?");
			return this.query(sb.toString(), new Object[]{crewId,startDate,endDate}, null);
		}*/
	}
	/**
	 *  获取此剧组下所有的币种
	 * @param crewId
	 * @throws SQLException
	 */
	public List<Map<String,Object>>  getCurrencyList(String crewId) throws SQLException {
		String sql = "SELECT * FROM tab_currency_info WHERE crewId =? ";
		return  this.query(sql, new Object[]{crewId}, null);
	}
	/**
	 * 查询付款单-财务科目数据
	 * @param crewId
	 * @return
	 * @throws Exception 
	 */
	public List<Map<String, Object>> getPaymentFinanceSubjectList(String crewId) throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT pfsm.paymentId, pfsm.accountId, pfsm.money, cur.currencyId, cur.currencyCode, cur.exchangeRate");
		sb.append(" FROM tab_payment_info payment");
		sb.append(" LEFT JOIN  tab_payment_account_map AS pfsm ON payment.paymentId = pfsm.paymentId");
		sb.append(" LEFT JOIN tab_currency_info AS cur ON payment.currencyId = cur.currencyId");
		sb.append(" WHERE payment.crewId =?  AND payment.`status` = 1");
		return this.query(sb.toString(), new Object[]{crewId}, null);
	}
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getPaymentList(String crewId,String accountIds)throws SQLException{
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT payment.paymentId,pam.money as totalMoney, DATE_FORMAT(payment.paymentDate,'%Y/%m/%d') AS paymentDate, cur.currencyId, cur.currencyCode, cur.exchangeRate");
		sb.append(" FROM tab_payment_info as payment");
		//lma 后改数据 
		sb.append(" LEFT JOIN tab_payment_account_map  pam ON pam.paymentId=payment.paymentId");
		
		sb.append(" LEFT JOIN tab_currency_info AS cur ON payment.currencyId = cur.currencyId");
		sb.append("  WHERE payment.crewId = ? AND payment.`status` = 1 ");
		if(!StringUtils.isEmpty(accountIds)){
			String acc = accountIds.replaceAll(",", "','");
			sb.append("and pam.accountId in ('"+acc+"')");
		}
		return this.query(sb.toString(), new Object[]{crewId}, null);
	}
	/**
	 * 创建科目组
	 */
	public int getsaveSubjectGroup(String groupId, String groupName,String crewId) throws SQLException{
		String sql="INSERT INTO tab_finance_account_group VALUES(?,?,?)";
		return this.getJdbcTemplate().update(sql, new Object[]{groupId,groupName,crewId});
	}
	public int getsaveSubjectGroup(String mapId,String accountId, String groupId,String crewId) throws SQLException{
		String sqlto="INSERT INTO tab_finance_account_group_map VALUES(?,?,?,?)";
		this.getJdbcTemplate().update(sqlto, new Object[]{mapId,accountId,groupId,crewId});
		return 0;
	} 
	/**
	 * 删除科目组
	 */
	public int deleteAccountGroup(String groupId,String crewId)throws SQLException{
		String sql="DELETE FROM tab_finance_account_group WHERE groupId = ? AND crewId=?";
		return this.getJdbcTemplate().update(sql, new Object[]{groupId,crewId});
	}
	/**
	 * 删除科目组映射信息
	 * @param groupId
	 */
	public int deleteAccountGroupMap(String groupId,String crewId)throws SQLException{
		//科目组映射信息
		String sqlto="DELETE FROM tab_finance_account_group_map WHERE groupId = ? AND crewId=?";
		return this.getJdbcTemplate().update(sqlto, new Object[]{groupId,crewId});
	}
	/**
	 * 查询是否有子级科目
	 * @param 
	 */
	public Integer findBudgetAccount(String parentId,String crewId)throws SQLException{
		//科目组映射信息
		String sqlto="SELECT count(accountId) as counts  FROM tab_finance_budget_account where parentId=?  AND  crewId=?";
		List<Map<String, Object>> list= this.query(sqlto.toString(), new Object[]{parentId,crewId}, null);
		return (Integer.valueOf(list.get(0).get("counts").toString())) ;
	}
}
