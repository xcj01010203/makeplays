package com.xiaotu.makeplays.crew.model.constants;

/**
 * 剧组类型枚举类
 * @author xuchangjian 2016-3-5上午10:10:53
 */
public enum CrewStatus {
	
	/**
	 * 筹备中
	 */
	Preparing(1),
	/**
	 * 拍摄中
	 */
	Shooting(2),
	
	/**
	 * 后期制作中
	 */
	PostProduction(3),
	
	/**
	 * 已完成
	 */
	Finished(4),
	
	/**
	 * 播出中
	 */
	Broadcasting(5),
	
	/**
	 * 暂停
	 */
	Suspended(6);
	
	private int value;
	
	private CrewStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static CrewStatus valueOf(int value) { 
		for (CrewStatus item : CrewStatus.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型CrewStatus不支持整形值：" + value);
	}
	
	public static CrewStatus nameOf(String name) {
		for (CrewStatus item : CrewStatus.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型CrewStatus不支持字面值：" + name);
	}
}
