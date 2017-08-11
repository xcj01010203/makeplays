package com.xiaotu.makeplays.goods.model.constants;

/**
 * 物品类型枚举类
 * @author wanrenyi 2017年4月24日上午11:04:48
 */
public enum GoodsType {

	
	/**
	 * 普通道具
	 */
	CommonProps(0),
	
	/**
	 * 特殊道具
	 */
	SpecialProps(1),
	
	/**
	 * 化妆
	 */
	Makeup(2),
	
	/**
	 * 服装
	 */
	Clothes(3);
	
	
	private int value;
	
	private GoodsType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static GoodsType valueOf(int value) { 
		for (GoodsType item : GoodsType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型GoodsType不支持整形值：" + value);
	}
	
	public static GoodsType nameOf(String name) {
		for (GoodsType item : GoodsType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型GoodsType不支持字面值：" + name);
	}
}
