package com.xiaotu.makeplays.message.model.constants;

/**
 * 消息类型
 * @author xuchangjian 2016-5-16下午4:03:38
 */
public enum MessageType {
	
	/**
	 * 通告单发布消息
	 */
	NoticePublish(1),
	
	/**
	 * 申请加入剧组审核通过消息
	 */
	AuditJoinCrewSuccess(2),
	
	/**
	 * 申请加入剧组审核不通过消息
	 */
	AuditJoinCrewFail(3),
	
	/**
	 * 申请加入剧组消息
	 */
	ApplyJoinCrew(4),
	
	/**
	 * 付款单发票消息
	 */
	PaymentReceiptGet(5),
	
	/**
	 * 合同支付消息
	 */
	ContractPay(6),
	
	/**
	 * 被加入到剧组中消息
	 */
	BeAddedToCrew(7),
	
	/**
	 * 意见反馈消息
	 */
	Feedback(8),
	
	/**
	 * 剧本变动消息
	 */
	ScenarioEdit(9),
	
	/**
	 * 审批消息
	 */
	Approval(10);
	
	private int value;
	
	private MessageType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static MessageType valueOf(int value) { 
		for (MessageType item : MessageType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型MessageType不支持整形值：" + value);
	}
	
	public static MessageType nameOf(String name) {
		for (MessageType item : MessageType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型MessageType不支持字面值：" + name);
	}
}
