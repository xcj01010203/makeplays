package com.xiaotu.makeplays.mobile.push.umeng.model.android;

/**
 * Android点击"通知"的后续行为，默认为打开app
 * @author xuchangjian
 */
public enum AndroidAfterOpen {

	/**
	 * 打开应用
	 */
	GoApp("go_app", 1),
	
	/**
	 * 跳转到URL
	 */
	GoUrl("go_url", 2),
	
	/**
	 * 打开特定的activity
	 */
	GoActivity("go_activity", 3),
	
	/**
	 * 用户自定义内容
	 */
	GoCustom("go_custom", 4);
	
	private int value;
	
	private String name;
	
	private AndroidAfterOpen(String name, int value) {
		this.value = value;
		this.name = name;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public String getName() {
		return this.name;
	}
}
