package com.xiaotu.makeplays.view.model.constants;

/**
 * 拍摄状态枚举类
 * @author xuchangjian
 */
public enum ShootStatus {

	/**
	 * 未完成
	 */
	Unfinished(0,"未完成"),
	
	/**
	 * 部分完成
	 */
	PartlyFinished(1,"部分完成"),
	
	/**
	 * 完成
	 */
	Finished(2,"完成"),
	
	/**
	 * 删戏
	 */
	DeleteXi(3,"删戏"),
	
	/**
	 * 加戏
	 */
	AddXiUnfinish(4,"加戏"),
	
	/**
	 * 加戏已完成
	 */
	AddXiFinished(5,"加戏已完成");
	
	private int value;
	
	private String name;
	
	
	
	private ShootStatus(int value,String name) {
		this.value = value;
		this.name = name;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public String getName() {
		return name;
	}

	public static ShootStatus valueOf(int value) {
		for (ShootStatus item : ShootStatus.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型ShootStatus不支持整形值：" + value);
	}
	
	public static ShootStatus nameOf(String name) {
		for (ShootStatus item : ShootStatus.values()) {
			if (item.name.equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型ShootStatus不支持字面值：" + name);
	}
}
