package com.xiaotu.makeplays.finance.model.constants;

/**
 * 财务科目级别枚举类
 * @author xuchangjian 2016-7-28上午9:41:17
 */
public enum FinanceSubjectLevel {

	/**
	 * 一级
	 */
	LevelOne(1),
	
	/**
	 * 二级
	 */
	LevelTwo(2),
	
	/**
	 * 三级
	 */
	LevelThree(3),
	
	/**
	 * 四级
	 */
	LevelFour(4);
	
	private int value;
	
	private FinanceSubjectLevel(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static FinanceSubjectLevel valueOf(int value) {
		for (FinanceSubjectLevel item : FinanceSubjectLevel.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型FinanceSubjectLevel不支持整形值：" + value);
	}
	
	public static FinanceSubjectLevel nameOf(String name) {
		for (FinanceSubjectLevel item : FinanceSubjectLevel.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型FinanceSubjectLevel不支持字面值：" + name);
	}
	
}
