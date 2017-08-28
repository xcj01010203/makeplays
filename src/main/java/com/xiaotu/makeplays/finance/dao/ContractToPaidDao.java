package com.xiaotu.makeplays.finance.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.finance.model.ContractToPaidModel;
import com.xiaotu.makeplays.finance.model.FinanceSubjectModel;
import com.xiaotu.makeplays.utils.BaseDao;
@Repository
public class ContractToPaidDao extends BaseDao<ContractToPaidModel>{
	
	@Autowired
	private FinanceSubjectDao financeSubjectDao;
	
	/**
	 * 查询合同待付信息
	 * 
	 * @param id            主键
	 * @param crewid   		剧组id
	 * @param starDate		开始时间
	 * @param endDate		结束时间
	 * @param contractType	合同类型
	 * @param contractName	合同方
	 * @param subjectid		剧组财务科目id
	 * @param status		状态
	 * @return
	 */
	public List<Map<String, Object>> queryContractToPaidList(String id,String crewid,String starDate,String endDate,String contractType,String contractName,String subjectid,String status){
		List<Object> args = new ArrayList<Object>();
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select tcp.*,tci.crewname ,tpi.receiptno from "+ContractToPaidModel.TABLE_NAME+" tcp  left join tab_crew_info tci on tcp.crewid = tci.crewid left join tab_payment_info tpi on tcp.crewid = tpi.crewId and tcp.paymentId = tpi.paymentid where tci.crewid is not null  ");
		if(StringUtils.isNotBlank(starDate) ){
			sql.append(" and paiddate >= ?  " );
			args.add(starDate);
		}
		if( StringUtils.isNotBlank(endDate)){
			sql.append(" and paiddate < ? " );
			args.add(endDate);
		}
		
		if(StringUtils.isNotBlank(contractType)){
			String[] ctype = contractType.split(",");
			if(ctype!=null&&ctype.length>0){
				sql.append(" and contacttype in (");
				for(int i = 0;i<ctype.length;i++){
					sql.append(" ? ");
					args.add(ctype[i]);
					if(i<ctype.length-1){
						sql.append(",");
					}
				}
				sql.append(" )");
			}
		}
		if(StringUtils.isNotBlank(contractName)){
			String[] contractNames = contractName.split(",");
			if(contractNames!=null&&contractNames.length>0){
				sql.append(" and contactName in (");
				for(int i = 0;i<contractNames.length;i++){
					sql.append(" ? ");
					args.add(contractNames[i]);
					if(i<contractNames.length-1){
						sql.append(",");
					}
				}
				sql.append(" )");
			}
		}
		if(StringUtils.isNotBlank(subjectid)){
			String[] subjectids = subjectid.split(",");
			if(subjectids!=null&&subjectids.length>0){
				sql.append(" and subjectid in (");
				for(int i = 0;i<subjectids.length;i++){
					String str = subjectids[i];
					if("blank".equals(str)){
						str = "";
					}
					sql.append(" ? ");
					args.add(str);
					if(i<subjectids.length-1){
						sql.append(",");
					}
				}
				sql.append(" )");
			}
		}
		if(StringUtils.isNotBlank(status)){
			String[] statusArray = status.split(",");
			if(statusArray!=null&&statusArray.length>0){
				sql.append(" and tcp.status in (");
				for(int i = 0;i<statusArray.length;i++){
					sql.append(" ? ");
					args.add(statusArray[i]);
					if(i<statusArray.length-1){
						sql.append(",");
					}
				}
				sql.append(" )");
			}
		}else{
			sql.append(" and tcp.status in ( 0,1 ) ");
		}
		
		if(StringUtils.isNotBlank(id)){
			String[] ids = id.split(",");
			if(ids!=null&&ids.length>0){
				sql.append(" and tcp.id in (");
				
				for(int i = 0;i<ids.length;i++){
					sql.append(" ? ");
					args.add(ids[i]);
					if(i<ids.length-1){
						sql.append(",");
					}
				}
				sql.append("  ) ");
			}
		}
		sql.append(" and tcp.crewid = ? ");
		args.add(crewid);
		sql.append(" order by tcp.status asc ,tcp.paiddate  asc ");
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql.toString(),args.toArray());
		return list;
	}
	
	/**
	 * 修改合同待付信息
	 * 
	 * @param id   主键
	 * @param summary  职务/角色/负责人
	 * @param money    金额
	 * @param status   状态
	 */
	public void updateContractToPaidInfo(String id,String summary,Double money,Integer status,String paymentId){
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" update "+ContractToPaidModel.TABLE_NAME+" set ");
		List<Object> list = new ArrayList<Object>();
		if(StringUtils.isNotBlank(summary)){
			sbSql.append(" summary = ?  , ");
			list.add(summary);
		}
		if(money!=null){
			sbSql.append(" money = ? , ");
			list.add(money);
		}
		if(status!=null){
			sbSql.append(" status = ?  , ");
			list.add(status);
		}
		if(StringUtils.isNotBlank(paymentId)){
			sbSql.append(" paymentId = ? ,");
			list.add(paymentId);
		}
		sbSql.append(" updatetime  = ? ");
		list.add(new Timestamp(System.currentTimeMillis()));
		
		if(StringUtils.isBlank(id)){
			return ;
		}
		String[] ids = id.split(",");
		if(ids==null||ids.length==0){
			return ;
		}
		sbSql.append(" where id in ( " );
		for(int i = 0;i<ids.length;i++){
			sbSql.append(" ? ");
			list.add(ids[i]);
			if(i<ids.length-1){
				sbSql.append(",");
			}
		}
		sbSql.append(" ) " );
		this.getJdbcTemplate().update(sbSql.toString(),list.toArray());
		
	}
	
	/**
	 * 查询合同付款方式信息  
	 * 
	 * @param contractPartId
	 * @param contractType
	 * @return
	 *//*
	public List<Map<String, Object>> queryContractPayWayInfoForSave(String contractPartId, Integer contractType){
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" SELECT ");
		sbSql.append("    wayid, ");
		sbSql.append("    paymentMoney, ");
		sbSql.append("    remingtime, ");
		sbSql.append("    w.contractid, ");
		sbSql.append("    w.crewid, ");
		sbSql.append("    contractno, ");
		if(contractType==1){
			sbSql.append("    a.workername payName, ");
			sbSql.append("    a.department personName, ");
		}else if(contractType==2){
			sbSql.append("    a.actorname payName, ");
			sbSql.append("    a.rolename personName, ");
		}else{
			sbSql.append("    a.company payName, ");
			sbSql.append("    a.contactPerson personName, ");
		}
		
		sbSql.append("    phone, ");
		sbSql.append("    currencyid, ");
		sbSql.append("    financesubjname ,");
		sbSql.append("    financeSubjId ");
		sbSql.append(" FROM ");
		sbSql.append("    tab_contract_pay_way w ");
		if(contractType==1){
			sbSql.append(" LEFT JOIN tab_contract_worker a ON w.contractid = a.contractid ");
		}else if(contractType==2){
			sbSql.append(" LEFT JOIN tab_contract_actor a ON w.contractid = a.contractid ");
		}else{
			sbSql.append(" LEFT JOIN tab_contract_produce a ON w.contractid = a.contractid ");
		}
		sbSql.append(" WHERE ");
		sbSql.append("    w.wayid = ?  ");
		List<Map<String, Object>> listPayment  = this.getJdbcTemplate().queryForList(sbSql.toString(), new Object[]{contractPartId});
		
		
		return listPayment;
	};*/
	
	/**
	 * 根据id查询合同待付信息
	 * @param id
	 * @return
	 */
	public List<ContractToPaidModel> queryContractToPaidInfo(String id){
		List<ContractToPaidModel> list = null;
		if(StringUtils.isNotBlank(id) ){
			StringBuilder sql = new StringBuilder("select * from "+ContractToPaidModel.TABLE_NAME+" where id in ( ");
			String[] idArray = id.split(",");
			if(idArray!=null && idArray.length != 0){
				StringBuilder par = new StringBuilder();
				List<Object>  args = new ArrayList<Object>();
				for(int i = 0,le = idArray.length;i<le;i++){
					String iStr = idArray[i];
					if(StringUtils.isNotBlank(iStr)){
						args.add(iStr);
						par.append("?,");
					}
					
				}
				if(par.length()>0){
					par.deleteCharAt(par.length()-1);
				}
				if(par.length()>0){
					sql.append(par);
					sql.append(" ) ");
					list = this.query(sql.toString(), args.toArray(), ContractToPaidModel.class, null);
				}
			}
			
		}
		return list;
	}	
	
	/**
	 * 根据id 批量删除合同待付信息
	 * @param deList
	 */
	public void batchDeleteContractToPaidInfo(List<String> idList) {
		//删除原有合同待付  未付
		String sql = "delete from "+ContractToPaidModel.TABLE_NAME+" where   id = ? ";
		
		List<Object[]> args = new ArrayList<Object[]>();
		for(String strId:idList){
			Object[] oo = new Object[1];
			oo[0] = strId;
			args.add(oo);
		}
		this.getJdbcTemplate().batchUpdate(sql, args);
	}
	
	/**
	 * 查询合同待付表中已有合同方
	 * @param crewid
	 * @return
	 */
 	public List<String> queryDropListForContractName(String crewid){
		String sql = "select distinct contactname from "+ContractToPaidModel.TABLE_NAME+" where crewid = ? and contactname is not null ";
		List<String> list = this.getJdbcTemplate().queryForList(sql, String.class,new Object[]{crewid});
		return list;
	}
	
	/**
	 * 查询合同待付中已有的财务科目信息
	 * 
	 * @param crewid
	 * @return
	 */
	public List<FinanceSubjectModel> queryDropListForContractSubjectName(String subjectId ){
		List<FinanceSubjectModel> list = new ArrayList<FinanceSubjectModel>();
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT T2.* ");
		sql.append(" FROM (  ");
		sql.append("     SELECT  ");
		sql.append("         @r AS _id,  ");
		sql.append("         (SELECT @r := parentid FROM tab_finance_subject WHERE id = _id) AS parentid ");
		sql.append("          ");
		sql.append("     FROM  ");
		sql.append("         (SELECT @r:= '"+subjectId+"') vars,  ");
		sql.append("         tab_finance_subject h  ");
		sql.append("     WHERE @r <> '0' ) T1  ");
		sql.append(" JOIN tab_finance_subject T2  ");
		sql.append(" ON T1._id = T2.id ");
				
		list = financeSubjectDao.query(sql.toString(), null, FinanceSubjectModel.class, null);
		
		return list;
	}
	
	/**
 	 * 获取财务科目，只查询合同批量支付中有的财务科目
 	 * @param crewId
 	 * @return
 	 */
	public List<FinanceSubjectModel> querySubjectId(String crewId){
		String sql = "SELECT * FROM " + FinanceSubjectModel.TABLE_NAME
				+ " WHERE id IN ( SELECT subjectid FROM " + ContractToPaidModel.TABLE_NAME
				+ " WHERE crewid = ? and status between 0 and 1)";
		List<FinanceSubjectModel> list = financeSubjectDao.query(sql, new Object[]{crewId}, FinanceSubjectModel.class, null);
		return list;
		
	}
	
	
	
	
	/**
	 * 删除合同关联的待付信息  不包含已付的 
	 * @param contractid  合同id
	 * @param contractType  合同类型（worker :职员合同    actor:演员合同    produce:制作合同）
	 */
	public void deleteContractToPaidInfoWhenDeleteContract(String contractid,String contractType){
			StringBuilder sql = new StringBuilder();
			sql.append(" DELETE ");
			sql.append(" FROM ");
			sql.append(ContractToPaidModel.TABLE_NAME );
			sql.append(" WHERE ");
			sql.append("    contractId = ? and contacttype = ? and status in ( 0,1)");
			
			this.getJdbcTemplate().update(sql.toString(),new Object[]{contractid,contractType});
	}
	
	/**
	 * 根据付款单id查询 合同待付信息
	 * 
	 * @param paymentId
	 * @return
	 */
	public List<Map<String, Object>> queryContractToPaidInfoByPaymentIds(String paymentId){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		if(StringUtils.isNotBlank(paymentId)){
			String[] paymentIdArray = paymentId.split(",");
			if(paymentIdArray!=null&&paymentIdArray.length>0){
				StringBuilder sbSql = new StringBuilder();
				sbSql.append(" select * from "+ContractToPaidModel.TABLE_NAME+" where paymentId in (");
				for(int i = 0;i<paymentIdArray.length;i++){
					sbSql.append("'"+paymentIdArray[i]+"'");
					if(i<paymentIdArray.length-1){
						sbSql.append(",");	
					}
				}
				sbSql.append(" ) ");
				list = this.getJdbcTemplate().queryForList(sbSql.toString());
			}
		}
		return list;
	}
	
	/**
	 * 批量更新合同待付id
	 * 
	 * @param updateList
	 */
	public void batchUpdateContractToPaidIdById(List<Map<String, String>>	updateList){
		if(updateList!=null&&updateList.size()>0){
			String sql = "update "+ContractToPaidModel.TABLE_NAME+" set id = ? where id = ? ";
			List<Object[]> batchArgs = new ArrayList<Object[]>();
			for(Map<String,String> tmap:updateList){
				String newId = tmap.get("newId");
				String oldId = tmap.get("oldId");
				if(StringUtils.isNotBlank(newId)&&StringUtils.isNotBlank(oldId)){
					Object[] oo = new Object[2];
					oo[0] = newId;
					oo[1] = oldId;
					batchArgs.add(oo);
				}
			}
			this.getJdbcTemplate().batchUpdate(sql, batchArgs);
		}
	}
	
	public void deleteContractToPaidInfoByPaymentId(String paymentId){
		String sql = "delete from "+ContractToPaidModel.TABLE_NAME+" where paymentid = ?";
		this.getJdbcTemplate().update(sql,new Object[]{paymentId});
	}
	
	public void  updateContractTopaid2SettleByPaymentId(String paymentId){
		String sql = "update "+ContractToPaidModel.TABLE_NAME+" set status = 3 where paymentId = ?";
		this.getJdbcTemplate().update(sql,new Object[]{paymentId});
	}
	
	/**
	 * 查询剧组下的合同待付信息
	 * @param crewId
	 * @return
	 */
	public List<ContractToPaidModel> queryByContractId (String crewId, String contractId) {
		String sql = "select * from tab_contract_topaid where crewId = ? and contractId = ?";
		return this.query(sql, new Object[] {crewId, contractId}, ContractToPaidModel.class, null);
	}
	
	/**
	 * 删除剧组下的待付信息
	 * @param crewId
	 */
	public void deleteByContractId(String crewId, String contractId) {
		String sql = "delete from tab_contract_topaid where crewId = ? and contractId = ?";
		this.getJdbcTemplate().update(sql, crewId, contractId);
	}
}
