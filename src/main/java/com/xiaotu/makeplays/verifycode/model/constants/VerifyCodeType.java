package com.xiaotu.makeplays.verifycode.model.constants;

/**
 * 验证码类型
 * @author xuchangjian 2016-5-16下午4:03:38
 */
public enum VerifyCodeType {
	
	/**
	 * 找回密码
	 */
	FindbackPassword(1),
	
	/**
	 * 注册
	 */
	Register(2),
	
	/**
	 * 修改手机号
	 */
	ModifyPhone(3),
	
	/**
	 * 财务-验证用户手机号
	 */
	ValidUserPhone(4);
	
	private int value;
	
	private VerifyCodeType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static VerifyCodeType valueOf(int value) { 
		for (VerifyCodeType item : VerifyCodeType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型VarifyCodeType不支持整形值：" + value);
	}
	
	public static VerifyCodeType nameOf(String name) {
		for (VerifyCodeType item : VerifyCodeType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型VarifyCodeType不支持字面值：" + name);
	}
}
