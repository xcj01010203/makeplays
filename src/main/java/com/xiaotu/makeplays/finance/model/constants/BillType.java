package com.xiaotu.makeplays.finance.model.constants;

/**
 * 票据种类
 * @author xuchangjian 2016-8-19下午4:11:03
 */
public enum BillType {

	/**
	 * 普通发票
	 */
	CommonReceip(1),
	
	/**
	 * 增值税发票
	 */
	TaxReceip(2);
	
	private int value;
	
	private BillType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static BillType valueOf(int value) {
		for (BillType item : BillType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型FinancePayWay不支持整形值：" + value);
	}
	
	public static BillType nameOf(String name) {
		for (BillType item : BillType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型FinancePayWay不支持字面值：" + name);
	}
	
}
