package com.xiaotu.makeplays.notice.model.constants;

/**
 * 通告单销场状态
 * 
 * @author wanrenyi 2016年7月28日上午9:56:49
 */
public enum NoticeCanceledStatus {

	/**
	 * 通告单未销场
	 */
	Uncancel(0),
	
	/**
	 * 通告单以销场
	 */
	Canceled(1);
	
	private int value;
	
	private NoticeCanceledStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static NoticeCanceledStatus valueOf(int value) {
		for (NoticeCanceledStatus item : NoticeCanceledStatus.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型NoticeCanceledStatus不支持整形值：" + value);
	}
	
	public static NoticeCanceledStatus nameOf(String name) {
		for (NoticeCanceledStatus item : NoticeCanceledStatus.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型NoticeCanceledStatus不支持字面值：" + name);
	}
	
}
