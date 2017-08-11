package com.xiaotu.makeplays.view.model.constants;

/**
 * 季节枚举类
 * @author xuchangjian
 */
public enum SeasonType {

	/**
	 * 春天
	 */
	Spring(1,"春天","春"),
	
	/**
	 * 夏天
	 */
	Summer(2,"夏天","夏"),
	
	/**
	 * 秋天
	 */
	Autumn(3,"秋天","秋"),
	
	/**
	 * 冬天
	 */
	Winter(4,"冬天","冬"),
	
	/**
	 * 未知
	 */
	Unknown(99,"","");
	
	private int value;
	private String name;
	private String shortName;
	private SeasonType(int value,String name,String shortName) {
		this.value = value;
		this.name = name;
		this.shortName = shortName;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public int getValue() {
		return this.value;
	}
	
	public static SeasonType valueOf(int value) {
		for (SeasonType item : SeasonType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型Season不支持整形值：" + value);
	}
	
	public static SeasonType nameOf(String name) {
		for (SeasonType item : SeasonType.values()) {
			if (item.name.equals(name)) {
				return item;
			}
			if (item.shortName.equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型Season不支持字面值：" + name);
	}
}
