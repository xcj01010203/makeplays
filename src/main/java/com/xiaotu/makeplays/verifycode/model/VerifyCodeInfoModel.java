package com.xiaotu.makeplays.verifycode.model;

import java.util.Date;

/**
 * 验证码
 * @author xuchangjian 2016-10-9下午6:53:47
 */
public class VerifyCodeInfoModel {
	
	public static final String TABLE_NAME = "tab_verifyCode_info";
	
	private String id;
	
	/**
	 * 手机号
	 */
	private String phone;
	
	/**
	 * 验证码
	 */
	private String code;
	
	/**
	 * 是否有效
	 */
	private boolean valid;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 类型，详情见VerifyCodeType枚举类
	 */
	private Integer type;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean getValid() {
		return this.valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}
