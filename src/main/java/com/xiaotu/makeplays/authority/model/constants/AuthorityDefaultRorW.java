package com.xiaotu.makeplays.authority.model.constants;

/**
 * 权限默认读写操作
 * @author xuchangjian 2016-5-31下午4:29:52
 */
public enum AuthorityDefaultRorW {

	/**
	 * 只读
	 */
	Readonly(1),
	
	/**
	 * 可编辑
	 */
	CanWrite(2);
	
	private int value;
	
	private AuthorityDefaultRorW (int value) {
		this.value = value;
	}
	
	public int getValue () {
		return this.value;
	}
	
	public static AuthorityDefaultRorW valueOf(int value) { 
		for (AuthorityDefaultRorW item : AuthorityDefaultRorW.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型AuthorityDefaultRorW不支持整形值：" + value);
	}
	
	public static AuthorityDefaultRorW nameOf(String name) {
		for (AuthorityDefaultRorW item : AuthorityDefaultRorW.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型AuthorityDefaultRorW不支持字面值：" + name);
	}
}
