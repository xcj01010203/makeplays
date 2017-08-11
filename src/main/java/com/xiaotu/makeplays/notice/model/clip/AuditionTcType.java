package com.xiaotu.makeplays.notice.model.clip;

/**
 * 场记单镜次中Tc类型
 * @author xuchangjian 2015-11-9下午3:19:14
 */
public enum AuditionTcType {

	/**
	 *	文件 
	 */
	File(1),
	
	/**
	 * 时码
	 */
	TimeCode(2);
	
	private int value;
	
	private AuditionTcType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static AuditionTcType valueOf(int value) {
		for (AuditionTcType item : AuditionTcType.values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型TcType不支持枚举值" + value);
	}
}
