package com.xiaotu.makeplays.crew.model.constants;

/**
 * 剧组类型枚举类
 * @author xuchangjian 2016-3-5上午10:10:53
 */
public enum CrewType {

	/**
	 * 电影
	 */
	Movie(0),
	
	/**
	 * 电视剧
	 */
	TVPlay(1),
	
	/**
	 * 网剧
	 */
	InternetTvplay(2),
	
	/**
	 * 网大
	 */
	InternetMovie(3);
	
	private int value;
	
	private CrewType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static CrewType valueOf(int value) { 
		for (CrewType item : CrewType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型CrewType不支持整形值：" + value);
	}
	
	public static CrewType nameOf(String name) {
		for (CrewType item : CrewType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型CrewType不支持字面值：" + name);
	}
}
