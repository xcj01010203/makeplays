package com.xiaotu.makeplays.attachment.model.constants;

/**
 * 附件关联的业务类型枚举类
 * @author xuchangjian 2016-3-2下午2:20:07
 */
public enum AttachmentBuzType {

	/**
	 * 场记单道具
	 */
	ClipProp(1),
	
	/**
	 * 场记单重要备注
	 */
	ClipComment(2),
	
	/**
	 * 合同
	 */
	Contract(3),
	
	/**
	 * 剧照
	 */
	CrewPicture(4),
	
	/**
	 * 审批单据
	 */
	ApprovalReceipt(5);
	
	private int value;

	private AttachmentBuzType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static AttachmentBuzType valueOf(int value) {
		for (AttachmentBuzType item : AttachmentBuzType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型AttachmentBuzType不支持整形值：" + value);
	}
	
	public static AttachmentBuzType nameOf(String name) {
		for (AttachmentBuzType item : AttachmentBuzType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型AttachmentBuzType不支持字面值：" + name);
	}
}
