package com.xiaotu.makeplays.user.model.constants;


/**
 * 餐别
 * @author xuchangjian 2016-5-16下午4:03:38
 */
public enum MealType {
	
	/**
	 * 常规
	 */
	Common(1,"常规"),
	
	/**
	 * 回民
	 */
	Hui(2,"清真"),
	
	/**
	 *素餐 
	 */
	Su(3,"素餐"),
	
	
	/**
	 *特餐    主演  导演 
	 */
	Esp(4,"特餐");
	
	private int value;
	
	private String name;
	
	private MealType(int value,String name) {
		this.value = value;
		this.name = name;
	}
	
	public int getValue() {
		return this.value;
	}
	
	
	public String getName() {
		return name;
	}

	public static MealType valueOf(int value) { 
		for (MealType item : MealType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型MealType不支持整形值：" + value);
	}
	
	public static MealType nameOf(String name) {
		for (MealType item : MealType.values()) {
			if (item.name.equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型MealType不支持字面值：" + name);
	}
}
