package com.xiaotu.makeplays.mobile.server.index.dto;

public class MobileSingleAuthorityDto {
	
	/**
	 * 是否有该权限
	 */
	private boolean hasAuth = false;
	
	/**
	 * 是否只读
	 */
	private boolean readonly = false;

	/**
	 * 权限编码
	 */
	private String authCode;
	
	/**
	 * 权限名称
	 */
	private String authName;
	
	public MobileSingleAuthorityDto() {
		
	}
	
	public MobileSingleAuthorityDto(boolean hasAuth, boolean readonly, String authCode, String authName) {
		this.hasAuth = hasAuth;
		this.readonly = readonly;
		this.authCode = authCode;
		this.authName = authName;
	}
	
	public boolean isReadonly() {
		return this.readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public boolean isHasAuth() {
		return this.hasAuth;
	}

	public void setHasAuth(boolean hasAuth) {
		this.hasAuth = hasAuth;
	}

	public String getAuthCode() {
		return this.authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getAuthName() {
		return this.authName;
	}

	public void setAuthName(String authName) {
		this.authName = authName;
	}
	
	
}
