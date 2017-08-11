package com.xiaotu.makeplays.cutview.model.constants;

/**
 * 剪辑状态枚举类型
 * @author wanrenyi 2017年6月15日下午3:39:40
 */
public enum CutViewStatusType {
	
	/**
	 * 已完成剪辑
	 */
	FinishedCutView(1),
	
	/**
	 * 未完成剪辑
	 */
	UnFinishedCutView(2);
	
	
	
	private int value;
	
	private CutViewStatusType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static CutViewStatusType valueOf(int value) { 
		for (CutViewStatusType item : CutViewStatusType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型GoodsType不支持整形值：" + value);
	}
	
	public static CutViewStatusType nameOf(String name) {
		for (CutViewStatusType item : CutViewStatusType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型GoodsType不支持字面值：" + name);
	}
}
