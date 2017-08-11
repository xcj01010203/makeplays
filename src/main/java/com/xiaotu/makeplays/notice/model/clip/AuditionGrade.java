package com.xiaotu.makeplays.notice.model.clip;

/**
 * 场记单镜次成绩
 * @author xuchangjian 2015-11-9下午3:22:38
 */
public enum AuditionGrade {

	/**
	 * OK
	 */
	OK(1),
	
	/**
	 * NG
	 */
	NG(2),
	
	/**
	 * 备用
	 */
	Standby(3);
	
	private int value;
	
	private AuditionGrade(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static AuditionGrade valueOf(int value) {
		for (AuditionGrade item : AuditionGrade.values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型AuditionGrade不支持枚举值" + value);
	}
}
