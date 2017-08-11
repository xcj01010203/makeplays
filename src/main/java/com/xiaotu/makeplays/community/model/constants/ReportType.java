package com.xiaotu.makeplays.community.model.constants;

public enum ReportType {

	/**
	 * 虚假广告
	 */
	FalseAdvert(1),
	
	/**
	 * 当前组训不可用
	 */
	ObsceneTeam(2),
	
	/**
	 *违法违纪 
	 */
	BreakLaw(3),
	
	/**
	 * 诈骗
	 */
	FraudTeam(4);
	
	
	private int value;
	
	private ReportType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static ReportType valueOf(int value) {
		for (ReportType item : ReportType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型ReportType不支持整形值：" + value);
	}
	
	public static ReportType nameOf(String name) {
		for (ReportType item : ReportType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型ReportType不支持字面值：" + name);
	}
}
