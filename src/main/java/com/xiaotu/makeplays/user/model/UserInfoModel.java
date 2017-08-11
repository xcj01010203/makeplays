package com.xiaotu.makeplays.user.model;

import java.util.Date;

/**
 * 用户信息
 * @author xuchangjian 2016-9-18上午11:00:39
 */
public class UserInfoModel {

	public static final String TABLE_NAME="tab_user_info";
	
	private String userId;
	
	/**
	 * 登录名
	 */
	private String userName;
	
	/**
	 * 密码
	 */
	private String password;
	
	/**
	 * 真实姓名
	 */
	private String realName;
	
	/**
	 * 用户类型，详情见UserType枚举类
	 */
	private Integer type;
	
	/**
	 * 手机号
	 */
	private String phone;
	
	/**
	 * 性别
	 */
	private Integer sex;
	
	/**
	 * 邮箱
	 */
	private String email;
	
	/**
	 * 用户状态，详情见UserStatus枚举类
	 */
	private Integer status;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 所属组ID
	 */
	private String groupId;
	
	/**
	 * 小头像地址
	 */
	private String smallImgUrl;
	
	/**
	 * 大头像地址
	 */
	private String bigImgUrl;
	
	/**
	 * 手机端唯一标识
	 */
	private String token;
	
	/**
	 * 客户端类型，详情见UserClientType枚举类
	 */
	private Integer clientType;
	
	/**
	 * 版本号
	 */
	private String appVersion;	
	
	/**
	 * 可用的创建剧组次数
	 */
	private Integer ubCreateCrewNum;
	
	/**
	 * 年龄
	 */
	private Integer age;
	
	/**
	 * 个人简介
	 */
	private String profile;
	
	/**
	 * IP地址
	 */
	private String ip;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRealName() {
		return this.realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getSex() {
		return this.sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getSmallImgUrl() {
		return this.smallImgUrl;
	}

	public void setSmallImgUrl(String smallImgUrl) {
		this.smallImgUrl = smallImgUrl;
	}

	public String getBigImgUrl() {
		return this.bigImgUrl;
	}

	public void setBigImgUrl(String bigImgUrl) {
		this.bigImgUrl = bigImgUrl;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Integer getClientType() {
		return this.clientType;
	}

	public void setClientType(Integer clientType) {
		this.clientType = clientType;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public Integer getUbCreateCrewNum() {
		return this.ubCreateCrewNum;
	}

	public void setUbCreateCrewNum(Integer ubCreateCrewNum) {
		this.ubCreateCrewNum = ubCreateCrewNum;
	}

	public Integer getAge() {
		return this.age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getProfile() {
		return this.profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}
	
}
