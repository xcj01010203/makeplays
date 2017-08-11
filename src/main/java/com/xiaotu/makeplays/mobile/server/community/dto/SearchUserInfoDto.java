package com.xiaotu.makeplays.mobile.server.community.dto;


/**
 * 寻组中个人的详细信息
 * @author wanrenyi 2016年9月6日下午2:07:07
 */
public class SearchUserInfoDto {

	/**
	 * 个人头像地址
	 */
	private String bigImgUrl;
	
	/**
	 * 姓名
	 */
	private String realName;
	
	/**
	 * 性别
	 */
	private Integer sex;
	
	/**
	 * 年龄
	 */
	private Integer age;
	
	/**
	 * 联系电话
	 */
	private String phone;
	
	/**
	 * 个人简介
	 */
	private String profile;
	
	/**
	 * 意向职位
	 */
	private String likePositionName;
	
	/**
	 * 个人档期开始时间
	 */
	private String currentStartDate;
	
	/**
	 * 个人档期结束时间
	 */
	private String currentEndDate;
	
	public String getBigImgUrl() {
		return bigImgUrl;
	}
	public void setBigImgUrl(String bigImgUrl) {
		this.bigImgUrl = bigImgUrl;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public Integer getSex() {
		return sex;
	}
	public void setSex(Integer sex) {
		this.sex = sex;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	public String getLikePositionName() {
		return likePositionName;
	}
	public void setLikePositionName(String likePositionName) {
		this.likePositionName = likePositionName;
	}
	public String getCurrentStartDate() {
		return currentStartDate;
	}
	public void setCurrentStartDate(String currentStartDate) {
		this.currentStartDate = currentStartDate;
	}
	public String getCurrentEndDate() {
		return currentEndDate;
	}
	public void setCurrentEndDate(String currentEndDate) {
		this.currentEndDate = currentEndDate;
	}
	
}
