package com.xiaotu.makeplays.mobile.server.community.dto;


/**
 * 寻组信息Dto
 * @author wanrenyi 2016年9月5日下午6:25:37
 */
public class SearchTeamInfoDto {

	/**
	 * 寻组信息id
	 */
	private String searchTeamId;
	
	/**
	 * 用户id
	 */
	private String userId;
	
	/**
	 * 用户真实姓名
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
	 * 意向职位
	 */
	private String likePositionName;
	
	/**
	 * 发布时间
	 */
	private String createTime;
	
	private String workExperience;
	/**
	 * 头像地址
	 */
	private String picPath;
	
	/**
	 * 联系电话
	 */
	private String phone;
	
	/**
	 * 个人档期开始时间
	 */
	private String currentStartDate;
	
	/**
	 * 个人档期结束时间
	 */
	private String currentEndDate;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getSearchTeamId() {
		return searchTeamId;
	}
	public void setSearchTeamId(String searchTeamId) {
		this.searchTeamId = searchTeamId;
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
	public String getLikePositionName() {
		return likePositionName;
	}
	public void setLikePositionName(String likePositionName) {
		this.likePositionName = likePositionName;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getPicPath() {
		return picPath;
	}
	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}
	public String getWorkExperience() {
		return workExperience;
	}
	public void setWorkExperience(String workExperience) {
		this.workExperience = workExperience;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
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
