package com.xiaotu.makeplays.user.model.constants;

/**
 * 身份证件类型
 * @author xuchangjian 2016-5-16下午4:03:38
 */
public enum IdentityCardType {
	
	/**
	 * 身份证
	 */
	IdentityCard(1, "身份证"),
	
	/**
	 * 护照
	 */
	Passport(2, "护照"),
	
	/**
	 * 台胞证
	 */
	TaiIdentityCard(3, "台胞证"),
	
	/**
	 * 军官证
	 */
	OffersIdentityCard(4, "军官证"),
	
	/**
	 * 其他
	 */
	Others(5, "其他");
	
	private int value;
	
	private String name;
	
	private IdentityCardType(int value, String name) {
		this.value = value;
		this.name = name;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static IdentityCardType valueOf(int value) { 
		for (IdentityCardType item : IdentityCardType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型IdentityCardType不支持整形值：" + value);
	}
	
	public static IdentityCardType nameOf(String name) {
		for (IdentityCardType item : IdentityCardType.values()) {
			if (item.name.equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型IdentityCardType不支持字面值：" + name);
	}
	
	public static void main(String[] args) {
		System.out.println(IdentityCardType.valueOf(1).getName());
	}
	
}
