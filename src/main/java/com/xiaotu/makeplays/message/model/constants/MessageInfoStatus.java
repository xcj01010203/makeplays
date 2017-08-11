package com.xiaotu.makeplays.message.model.constants;

/**
 * 消息状态
 * @author xuchangjian 2016-8-13上午9:28:50
 */
public enum MessageInfoStatus {
	
	/**
	 * 未读
	 */
	UnRead(0),
	
	/**
	 * 已读
	 */
	HasRead(1);
	
	private int value;
	
	private MessageInfoStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static MessageInfoStatus valueOf(int value) { 
		for (MessageInfoStatus item : MessageInfoStatus.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型MessageInfoStatus不支持整形值：" + value);
	}
	
	public static MessageInfoStatus nameOf(String name) {
		for (MessageInfoStatus item : MessageInfoStatus.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型MessageInfoStatus不支持字面值：" + name);
	}
}
