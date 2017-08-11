package com.xiaotu.makeplays.sys.model.constants;

/**
 * @类名：SysLogOperType.java
 * @作者：李晓平
 * @时间：2017年3月23日 上午10:42:08
 * @描述：系统日志操作类型枚举类
 */
public enum SysLogOperType {

	/**
	 * 查询
	 */
	QUERY(0),
	
	/**
	 * 插入
	 */
	INSERT(1),
	
	/**
	 * 修改
	 */
	UPDATE(2),
	
	/**
	 * 删除
	 */
	DELETE(3),
	
	/**
	 * 导入
	 */
	IMPORT(4),
	
	/**
	 * 导出
	 */
	EXPORT(5),
	
	/**
	 * 异常
	 */
	ERROR(6),
	
	/**
	 * 其他
	 */
	OTHER(99);
	
	private int value;
	
	private SysLogOperType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static SysLogOperType valueOf(int value) { 
		for (SysLogOperType item : SysLogOperType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型SysLogOperType不支持整形值：" + value);
	}
	
	public static SysLogOperType nameOf(String name) {
		for (SysLogOperType item : SysLogOperType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型SysLogOperType不支持字面值：" + name);
	}
}
