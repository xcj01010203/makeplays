package com.xiaotu.makeplays.finance.model.constants;

/**
 * 财务科目模板类型
 * @author xuchangjian 2016-7-28上午10:14:23
 */
public enum FinanceSubjTemplType {

	/**
	 * 按照制作周期
	 */
	ByProduceCycle(0),
	
	/**
	 * 按照部门
	 */
	ByDepartment(1);
	
	private int value;
	
	private FinanceSubjTemplType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static FinanceSubjTemplType valueOf(int value) {
		for (FinanceSubjTemplType item : FinanceSubjTemplType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型FinanceSubjTemplType不支持整形值：" + value);
	}
	
	public static FinanceSubjTemplType nameOf(String name) {
		for (FinanceSubjTemplType item : FinanceSubjTemplType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型FinanceSubjTemplType不支持字面值：" + name);
	}
	
}
