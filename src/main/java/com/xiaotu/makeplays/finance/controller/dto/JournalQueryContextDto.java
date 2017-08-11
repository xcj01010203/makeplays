package com.xiaotu.makeplays.finance.controller.dto;

import java.util.List;
import java.util.Map;

public class JournalQueryContextDto {
	
	/**
	 * 收付款人
	 */
	private List<Map<String, String>> journalNameList;
	
	/**
	 * 票据日期
	 */
	private List<Map<String, String>> journalDateList;
	
	/**
	 * 记账
	 */
	private List<Map<String, String>> journalAgentList;
	
	/**
	 * 票据类型
	 */
	private List<Map<String, String>> journalType;
	
	/**
	 * 有无发票
	 */
	private List<Map<String, String>> hasReceipt;
	
	/**
	 * 结算状态
	 */
	private List<Map<String, String>> isBalance;

	public List<Map<String, String>> getJournalNameList() {
		return journalNameList;
	}

	public void setJournalNameList(List<Map<String, String>> journalNameList) {
		this.journalNameList = journalNameList;
	}

	public List<Map<String, String>> getJournalDateList() {
		return journalDateList;
	}

	public void setJournalDateList(List<Map<String, String>> journalDateList) {
		this.journalDateList = journalDateList;
	}

	public List<Map<String, String>> getJournalAgentList() {
		return journalAgentList;
	}

	public void setJournalAgentList(List<Map<String, String>> journalAgentList) {
		this.journalAgentList = journalAgentList;
	}

	public List<Map<String, String>> getJournalType() {
		return journalType;
	}

	public void setJournalType(List<Map<String, String>> journalType) {
		this.journalType = journalType;
	}

	public List<Map<String, String>> getHasReceipt() {
		return hasReceipt;
	}

	public void setHasReceipt(List<Map<String, String>> hasReceipt) {
		this.hasReceipt = hasReceipt;
	}

	public List<Map<String, String>> getIsBalance() {
		return isBalance;
	}

	public void setIsBalance(List<Map<String, String>> isBalance) {
		this.isBalance = isBalance;
	}
	

}
