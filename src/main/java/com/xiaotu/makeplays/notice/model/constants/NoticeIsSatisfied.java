package com.xiaotu.makeplays.notice.model.constants;

/**
 * 通告单是否满意
 * 
 * @author wanrenyi 2016年7月28日上午9:56:49
 */
public enum NoticeIsSatisfied {

	/**
	 * 不满意
	 */
	NotSatisfied(0),
	
	/**
	 * 满意
	 */
	Satisfied(1),
	
	/**
	 * 未填
	 */
	Blank(99);
	
	private int value;
	
	private NoticeIsSatisfied(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static NoticeIsSatisfied valueOf(int value) {
		for (NoticeIsSatisfied item : NoticeIsSatisfied.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型NoticeIsSatisfied不支持整形值：" + value);
	}
	
	public static NoticeIsSatisfied nameOf(String name) {
		for (NoticeIsSatisfied item : NoticeIsSatisfied.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型NoticeIsSatisfied不支持字面值：" + name);
	}
	
}
