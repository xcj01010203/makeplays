package com.xiaotu.makeplays.view.model.constants;

/**
 * 书签类别枚举类型
 * 
 * @author wanrenyi 2016年7月28日上午9:56:49
 */
public enum BookmarkType {

	/**
	 * 剧本书签
	 */
	BookMarkType(1);
	
	private int value;
	
	private BookmarkType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static BookmarkType valueOf(int value) {
		for (BookmarkType item : BookmarkType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型BookmarkType不支持整形值：" + value);
	}
	
	public static BookmarkType nameOf(String name) {
		for (BookmarkType item : BookmarkType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型BookmarkType不支持字面值：" + name);
	}
	
}
