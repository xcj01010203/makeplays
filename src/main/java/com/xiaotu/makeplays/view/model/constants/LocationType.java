package com.xiaotu.makeplays.view.model.constants;

/**
 * 拍摄场景类别枚举类
 * @author xuchangjian
 */
public enum LocationType {

	/**
	 * 主场景
	 */
	lvlOneLocation(1),
	
	/**
	 * 次场景
	 */
	lvlTwoLocation(2),
	
	/**
	 * 三级场景
	 */
	lvlThreeLocation(3);
	
	private int value;
	
	private LocationType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static LocationType valueOf(int value) {
		for (LocationType item : LocationType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型AddressType不支持整形值：" + value);
	}
	
	public static LocationType nameOf(String name) {
		for (LocationType item : LocationType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型AddressType不支持字面值：" + name);
	}
	
}
