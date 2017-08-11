package com.xiaotu.makeplays.crew.model;


/**
 * lma
 */
public class PaymentModel implements java.io.Serializable {


	private String paymentId;
	private String paymentDate;
	private String currencyId;
	private String currencyCode;
	private Double exchangeRate;
	private Double money;
	private String accountId;
	private Integer status;
	private Integer voucherFlag;
	private String  crewId;

	public String getPaymentId() {
		return paymentId;
	}


	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}


	public String getPaymentDate() {
		return paymentDate;
	}


	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}


	public String getCurrencyId() {
		return currencyId;
	}


	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}


	public String getCurrencyCode() {
		return currencyCode;
	}


	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}


	public Double getExchangeRate() {
		return exchangeRate;
	}


	public void setExchangeRate(Double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}


	public Double getMoney() {
		return money;
	}


	public void setMoney(Double money) {
		this.money = money;
	}


	public String getAccountId() {
		return accountId;
	}


	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}


	public Integer getStatus() {
		return status;
	}


	public void setStatus(Integer status) {
		this.status = status;
	}


	public Integer getVoucherFlag() {
		return voucherFlag;
	}


	public void setVoucherFlag(Integer voucherFlag) {
		this.voucherFlag = voucherFlag;
	}


	public String getCrewId() {
		return crewId;
	}


	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}


	@Override
	public String toString() {
		return "PaymentModel [currencyCode=" + currencyCode + ", currencyId="
				+ currencyId + ", exchangeRate=" + exchangeRate
				+ ", accountId=" + accountId + ", paymentId=" + paymentId
				+ ", money=" + money + ", paymentDate=" + paymentDate
				+ ", crewId=" + crewId + ", status=" + status
				+ ", voucherFlag=" + voucherFlag + "]";
	}

}