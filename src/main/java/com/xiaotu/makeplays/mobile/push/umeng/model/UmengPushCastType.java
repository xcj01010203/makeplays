package com.xiaotu.makeplays.mobile.push.umeng.model;

public enum UmengPushCastType {

	/**
	 * 单播
	 */
	Unicast("unicast", 0),
	
	/**
	 * 列播
	 */
	Listcast("listcast", 1),
	
	/**
	 * 广播
	 */
	Broadcast("broadcast", 2),
	
	/**
	 * 组播
	 */
	Groupcast("groupcast", 3),
	
	/**
	 * 自定义
	 */
	Customizedcast("customizedcast", 4), 
	
	/**
	 * 文件播
	 */
	Filecast("filecast", 5);
	
	private int value;
	
	private String name;
	
	private UmengPushCastType(String name, int value) {
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
