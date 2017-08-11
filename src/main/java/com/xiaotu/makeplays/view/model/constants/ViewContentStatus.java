package com.xiaotu.makeplays.view.model.constants;

/**
 * 书签类别枚举类型
 * 
 * @author wanrenyi 2016年7月28日上午9:56:49
 */
public enum ViewContentStatus {

	/**
	 * 添加未发布
	 */
	AddNotpublished(1),
	
	/**
	 * 修改未发布
	 */
	UpdateNotPublished(2),
	
	/**
	 * 已发布
	 */
	Published(3);
	
	private int value;
	
	private ViewContentStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static ViewContentStatus valueOf(int value) {
		for (ViewContentStatus item : ViewContentStatus.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型BookmarkType不支持整形值：" + value);
	}
	
	public static ViewContentStatus nameOf(String name) {
		for (ViewContentStatus item : ViewContentStatus.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型BookmarkType不支持字面值：" + name);
	}
	
}
