package com.xiaotu.makeplays.authority.model.constants;


/**
 * 权限状态
 * @author xuchangjian 2016-5-19下午2:34:05
 */
public enum AuthorityStatus {

	/**
	 * 有效
	 */
	Valid(0),
	
	/**
	 * 无效
	 */
	Invalid(1);
	
	private int value;
	
	private AuthorityStatus (int value) {
		this.value = value;
	}
	
	public int getValue () {
		return this.value;
	}
	
	public static AuthorityStatus valueOf(int value) { 
		for (AuthorityStatus item : AuthorityStatus.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型AuthorityStatus不支持整形值：" + value);
	}
	
	public static AuthorityStatus nameOf(String name) {
		for (AuthorityStatus item : AuthorityStatus.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型AuthorityStatus不支持字面值：" + name);
	}
}
