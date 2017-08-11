package com.xiaotu.makeplays.view.model.constants;

/**
 * 上传的剧本类别枚举类
 * @author xuchangjian
 */
public enum ScenarioType {

	/**
	 * 剧本标头在一行
	 */
	TitleOneLine(1),
	
	/**
	 * 剧本标头分行
	 */
	TitleManyLine(2);
	
	private int value;
	
	private ScenarioType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static ScenarioType valueOf(int value) {
		for (ScenarioType item : ScenarioType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型PlayType不支持整形值：" + value);
	}
	
	public static ScenarioType nameOf(String name) {
		for (ScenarioType item : ScenarioType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型PlayType不支持字面值：" + name);
	}
	
}
