package com.xiaotu.makeplays.notice.model.clip;

/**
 * 场记单镜次中的景别
 * @author xuchangjian 2015-11-9下午3:13:11
 */
public enum AuditionSceneType {

	/**
	 * 近景
	 */
	CloseView(1),
	
	/**
	 * 远景
	 */
	DistanceView(2),
	
	/**
	 * 特写
	 */
	SpecialView(3),
	
	/**
	 * 中景
	 */
	MiddleView(4),
	
	/**
	 * 全景
	 */
	AroundView(5);
	
	private int value;
	
	private AuditionSceneType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static AuditionSceneType valueOf(int value) {
		for (AuditionSceneType item : AuditionSceneType.values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型AuditionSceneType不支持枚举值" + value);
	}
}
