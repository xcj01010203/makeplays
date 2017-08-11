package com.xiaotu.makeplays.view.model.constants;

/**
 * 场景创建方式 1：手动添加；2：剧本分析
 * @author xuchangjian
 */
public enum ViewCreateWay {

	/**
	 * 手动
	 */
	ByHand(1),
	
	/**
	 * 剧本分析
	 */
	BySceAnalyse(2);
	
	private int value;
	
	private ViewCreateWay(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static ViewCreateWay valueOf(int value) {
		for (ViewCreateWay item : ViewCreateWay.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型SceneCreateWay不支持整形值：" + value);
	}
	
	public static ViewCreateWay nameOf(String name) {
		for (ViewCreateWay item : ViewCreateWay.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型SceneCreateWay不支持字面值：" + name);
	}
}
