package com.xiaotu.makeplays.finance.model.constants;

/**
 * 每月天数类型
 * @author xuchangjian 2017-2-15下午3:25:45
 */
public enum MonthDayType {

	/**
	 * 自然月天数
	 */
	NatureDay(1),
	
	/**
	 * 标准30天
	 */
	ThirtyDay(2);
	
	private int value;
	
	private MonthDayType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static MonthDayType valueOf(int value) {
		for (MonthDayType item : MonthDayType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型MonthDayType不支持整形值：" + value);
	}
	
	public static MonthDayType nameOf(String name) {
		for (MonthDayType item : MonthDayType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型MonthDayType不支持字面值：" + name);
	}
	
}
