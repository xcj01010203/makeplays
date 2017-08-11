package com.xiaotu.makeplays.finance.model.constants;

/**
 * 合同支付方式
 * @author xuchangjian 2016-8-2上午11:19:43
 */
public enum ContractPayWay {

	/**
	 * 按阶段
	 */
	PerStep(1),
	
	/**
	 * 按月
	 */
	PerMonth(2),
	
	/**
	 * 按日（每月结算）
	 */
	PerDayMonthSettle(3),
	
	/**
	 * 按日（定期结算）
	 */
	PerDayRegularSettle(4);
	
	private int value;
	
	private ContractPayWay(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static ContractPayWay valueOf(int value) {
		for (ContractPayWay item : ContractPayWay.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型FinancePayWay不支持整形值：" + value);
	}
	
	public static ContractPayWay nameOf(String name) {
		for (ContractPayWay item : ContractPayWay.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型FinancePayWay不支持字面值：" + name);
	}
	
}
