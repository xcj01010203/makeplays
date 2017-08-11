package com.xiaotu.makeplays.notice.model.constants;

/**
 * 通告单反馈状态
 * 
 * @author wanrenyi 2016年7月28日上午9:56:49
 */
public enum NoticePushFedBackStatus {

	/**
	 * 未收取
	 */
	NotReceived(1),
	
	/**
	 * 已收取
	 */
	Received(2),
	
	/**
	 * 已查看
	 */
	Readed(3);
	
	private int value;
	
	private NoticePushFedBackStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static NoticePushFedBackStatus valueOf(int value) {
		for (NoticePushFedBackStatus item : NoticePushFedBackStatus.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型NoticePushFedBackStatus不支持整形值：" + value);
	}
	
	public static NoticePushFedBackStatus nameOf(String name) {
		for (NoticePushFedBackStatus item : NoticePushFedBackStatus.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型NoticePushFedBackStatus不支持字面值：" + name);
	}
	
}
