package com.xiaotu.makeplays.mobile.push.umeng.model.android;


/**
 * Android推送消息的消息类型
 * @author xuchangjian
 */
public enum AndroidPushDisplayType {

	/**
	 * 通知
	 */
	Notification("notification", 1),
	
	/**
	 * 消息
	 */
	Message("message", 2);
	
	private int value;
	
	private String name;
	
	private AndroidPushDisplayType(String name, int value) {
		this.value = value;
		this.name = name;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static AndroidPushDisplayType valueOf(int value) {
		for (AndroidPushDisplayType item : AndroidPushDisplayType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型AndroidPushDisplayType不支持整形值：" + value);
	}
}
