package com.xiaotu.makeplays.finance.model;

/**
 * 会计科目和预算科目关联关系
 * @author xuchangjian 2016-6-22上午10:52:00
 */
public class AccoFinacSubjMapModel {

	public static final String TABLE_NAME = "tab_account_finance_subject_map";
	
	private String id;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 会计科目ID
	 */
	private String accountSubjId;
	
	/**
	 * 财务科目ID
	 */
	private String financeSubjId;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getAccountSubjId() {
		return this.accountSubjId;
	}

	public void setAccountSubjId(String accountSubjId) {
		this.accountSubjId = accountSubjId;
	}

	public String getFinanceSubjId() {
		return this.financeSubjId;
	}

	public void setFinanceSubjId(String financeSubjId) {
		this.financeSubjId = financeSubjId;
	}
}
