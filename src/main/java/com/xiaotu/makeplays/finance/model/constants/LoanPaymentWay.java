package com.xiaotu.makeplays.finance.model.constants;

/**
 * 借款单付款方式
 * @author xuchangjian 2016-8-2下午2:53:50
 */
public enum LoanPaymentWay {

	/**
	 * 现金
	 */
	Cash(1,"现金"),
	
	/**
	 * 现金（网转）
	 */
	NetCash(2,"现金（网转）"),
	
	/**
	 * 银行
	 */
	Bank(3,"银行");
	
	private int value;
	private String name;
	
	private LoanPaymentWay(int value,String name) {
		this.value = value;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public int getValue() {
		return this.value;
	}
	
	public static LoanPaymentWay valueOf(int value) {
		for (LoanPaymentWay item : LoanPaymentWay.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型LoanPaymentWay不支持整形值：" + value);
	}
	
	public static LoanPaymentWay nameOf(String name) {
		for (LoanPaymentWay item : LoanPaymentWay.values()) {
			if (item.name.equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型LoanPaymentWay不支持字面值：" + name);
	}
	
}
