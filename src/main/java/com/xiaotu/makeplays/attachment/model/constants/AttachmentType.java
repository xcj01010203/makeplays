package com.xiaotu.makeplays.attachment.model.constants;


/**
 * 附件类型，枚举类
 * @author xuchangjian 2016-3-2下午2:15:27
 */
public enum AttachmentType {

	/**
	 * Word文档
	 */
	Word(1),
	
	/**
	 * 图片
	 */
	Picture(2),
	
	/**
	 * 音频
	 */
	Audio(3),
	
	/**
	 * 视频
	 */
	Video(4),
	
	/**
	 * 其他
	 */
	Others(99);
	
	private int value;
	
	private AttachmentType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static AttachmentType valueOf(int value) {
		for (AttachmentType item : AttachmentType.values()) {
			if (item.value == value) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型AttachmentType不支持整形值：" + value);
	}
	
	public static AttachmentType nameOf(String name) {
		for (AttachmentType item : AttachmentType.values()) {
			if (item.toString().equals(name)) {
				return item;
			}
		}
		throw new IllegalArgumentException("枚举类型AttachmentType不支持字面值：" + name);
	}
}
