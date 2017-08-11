package com.xiaotu.makeplays.roleactor.model.constants;

/**
 * 演员评价状态
 * @author xuchangjian 2016-7-12下午5:57:43
 */
public enum EvaluateStatus {

	/**
	 * 创建完
	 */
	Created(0),
	
	/**
	 * 评价完
	 */
	Finished(1);
	
	private int value;
	
	private EvaluateStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static EvaluateStatus valueOf(int value) {
		for (EvaluateStatus item : EvaluateStatus.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型EvaluateStatus不支持整形值：" + value);
	}
	
	public static EvaluateStatus nameOf(String name) {
		for (EvaluateStatus item : EvaluateStatus.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型EvaluateStatus不支持字面值：" + name);
	}
	
}
