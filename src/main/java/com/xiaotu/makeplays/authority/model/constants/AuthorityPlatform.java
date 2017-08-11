package com.xiaotu.makeplays.authority.model.constants;

/**
 * 权限平台类型
 * @author xuchangjian 2016-5-19下午2:39:47
 */
public enum AuthorityPlatform {

	/**
	 * 通用
	 */
	Common(1),
	
	/**
	 * PC端
	 */
	PC(2),
	
	/**
	 * 移动端
	 */
	Mobile(3);
	
	private int value;
	
	private AuthorityPlatform (int value) {
		this.value = value;
	}
	
	public int getValue () {
		return this.value;
	}
	
	public static AuthorityPlatform valueOf(int value) { 
		for (AuthorityPlatform item : AuthorityPlatform.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型AuthorityPlantform不支持整形值：" + value);
	}
	
	public static AuthorityPlatform nameOf(String name) {
		for (AuthorityPlatform item : AuthorityPlatform.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型AuthorityPlantform不支持字面值：" + name);
	}
}
