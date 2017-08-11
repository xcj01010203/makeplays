package com.xiaotu.makeplays.feedback.model.constants;

/**
 * @类名：FeedBackStatus.java
 * @作者：李晓平
 * @时间：2017年4月20日 下午2:06:08
 * @描述：反馈、回复状态
 */
public enum FeedBackStatus {
	
	/**
	 * 未读
	 */
	UnRead(0),
	
	/**
	 * 已读
	 */
	HasRead(1);
	
	private int value;
	
	private FeedBackStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static FeedBackStatus valueOf(int value) { 
		for (FeedBackStatus item : FeedBackStatus.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型MessageInfoStatus不支持整形值：" + value);
	}
	
	public static FeedBackStatus nameOf(String name) {
		for (FeedBackStatus item : FeedBackStatus.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型MessageInfoStatus不支持字面值：" + name);
	}
}
