package com.xiaotu.makeplays.finance.model;

import java.sql.Timestamp;
import java.util.Date;


/**
 * 
 * 合同待付表
 * @author Administrator
 *
 */
public class ContractToPaidModel {
	public static final String TABLE_NAME="tab_contract_topaid";
	
	private String id;
	
	private String crewId;//剧组id
	
	private Date paiddate;//待付款日期
	
	private String contractId;//合同id
	
	private String contractNo;//合同编号
	
	private String summary;//摘要
	
	private Double money;//待付款金额
	
	private String currencyId;//币种id
	
	private String subjectId;//财务科目id
	
	private String financeSubjName;//财务科目名称
	
	private int status;//待付状态  0：未付  1：已生成待付单  2 ：已生成付款单  3：已结算
	
	private String paymentId;//付款单id

	private String contactname;//合同人
	
	private String contacttype;//合同类型  1：职员合同   2：演员合同  3：制作合同
	
	private Timestamp createtime;//创建时间
	
	private Timestamp updatetime;//最后修改时间

	private String roleName; //职务、角色、负责人
	
	
	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getFinanceSubjName() {
		return financeSubjName;
	}

	public void setFinanceSubjName(String financeSubjName) {
		this.financeSubjName = financeSubjName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getCrewId() {
		return crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public Date getPaiddate() {
		return paiddate;
	}

	public void setPaiddate(Date paiddate) {
		this.paiddate = paiddate;
	}



	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}



	public int getStatus() {
		return status;
	}


	public void setStatus(int status) {
		this.status = status;
	}



	public String getContactname() {
		return contactname;
	}

	public void setContactname(String contactname) {
		this.contactname = contactname;
	}

	public String getContacttype() {
		return contacttype;
	}

	public void setContacttype(String contacttype) {
		this.contacttype = contacttype;
	}

	public Timestamp getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Timestamp createtime) {
		this.createtime = createtime;
	}

	public Timestamp getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Timestamp updatetime) {
		this.updatetime = updatetime;
	}
	

	
}
