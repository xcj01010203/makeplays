package com.xiaotu.makeplays.authority.model;

public class AuthorityModel {

	public static final String TABLE_NAME="tab_sys_authority";
	
	
	private String authId;
	private String authName;
	private Integer operType;
	private String operDesc;
	private String authUrl;
	private Integer ifMenu;
	private int status;	//状态 0：有效；1：无效，详细见AuthorityStatus枚举类
	private String parentId;
	private Integer sequence;
	private int authPlantform = 1;  //权限作用平台:1、全部，2：pc端，3：移动端，详情见AuthorityPlantform枚举类
	private String authCode;
	private boolean differInRAndW;	//是否区分读写操作
	private int defaultRorW = 1;	//默认读写操作，详情见AuthorityDefaultRorW枚举类
	private String cssName;  //样式，用于显示导航图片
	
	public int getDefaultRorW() {
		return this.defaultRorW;
	}
	public void setDefaultRorW(int defaultRorW) {
		this.defaultRorW = defaultRorW;
	}
	public boolean getDifferInRAndW() {
		return this.differInRAndW;
	}
	public void setDifferInRAndW(boolean differInRAndW) {
		this.differInRAndW = differInRAndW;
	}
	public String getAuthCode() {
		return authCode;
	}
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
	public int getAuthPlantform() {
		return this.authPlantform;
	}
	public void setAuthPlantform(int authPlantform) {
		this.authPlantform = authPlantform;
	}
	public String getAuthId() {
		return authId;
	}
	public void setAuthId(String authId) {
		this.authId = authId;
	}
	public String getAuthName() {
		return authName;
	}
	public void setAuthName(String authName) {
		this.authName = authName;
	}
	public Integer getOperType() {
		return operType;
	}
	public void setOperType(Integer operType) {
		this.operType = operType;
	}
	public String getOperDesc() {
		return operDesc;
	}
	public void setOperDesc(String operDesc) {
		this.operDesc = operDesc;
	}
	public String getAuthUrl() {
		return authUrl;
	}
	public void setAuthUrl(String authUrl) {
		this.authUrl = authUrl;
	}
	public int getStatus() {
		return this.status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	public Integer getIfMenu() {
		return ifMenu;
	}
	public void setIfMenu(Integer ifMenu) {
		this.ifMenu = ifMenu;
	}
	public String getCssName() {
		return cssName;
	}
	public void setCssName(String cssName) {
		this.cssName = cssName;
	}
	
}
