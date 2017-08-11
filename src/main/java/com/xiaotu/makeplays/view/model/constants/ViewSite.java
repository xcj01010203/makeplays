package com.xiaotu.makeplays.view.model.constants;

/**
 * 拍摄场景内外景枚举类
 * @author xuchangjian
 */
public enum ViewSite {

	/**
	 * 内景
	 */
	InnerSite(1),
	
	/**
	 * 外景
	 */
	OuterSite(2),
	
	/**
	 * 内外景
	 */
	InOutSite(3);
	
	private int value;
	
	private ViewSite(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static ViewSite valueOf(int value) {
		for (ViewSite item : ViewSite.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型SceneSite不支持整形值：" + value);
	}
	
	public static ViewSite nameOf(String name) {
		for (ViewSite item : ViewSite.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型SceneSite不支持字面值：" + name);
	}
}
