package com.xiaotu.makeplays.mobile.server.community.dto;


/**
 * 组训信息的Dto对象
 * @author wanrenyi 2016年9月2日下午5:27:30
 */
public class TeamInfoDto {

	/**
	 * 收藏人数
	 */
	private Integer storeCount;
	
	/**
	 * 投递简历人数
	 */
	private Integer resumeCount;
	
	/**
	 * 组训id
	 */
	private String teamId;
	
	/**
	 * 发布人id
	 */
	private String createUser;
	
	/**
	 * 发布人姓名
	 */
	private String realName;
	
	/**
	 * 发布日期
	 */
	private String createTime;
	
	/**
	 * 剧组名称
	 */
	private String crewName;
	
	/**
	 * 剧组类型
	 */
	private String CrewType;
	
	/**
	 * 招聘职位名称;多个职位之间以","分隔
	 */
	private String positionName;
	
	/**
	 * 开机时间
	 */
	private String shootStartDate;
	
	/**
	 * 距今多少天
	 */
	private Integer agoDays = 0;
	
	/**
	 * 联系电话
	 */
	private String phoneNum;
	
	/**
	 * 联系地址(筹备地址)
	 */
	private String contactAddress;
	
	/**
	 * 宣传图片的地址;如果为空时,服务器返回一个默认地址
	 */
	private String picPath;
	
	/**
	 * 状态，参考TeamStatus枚举类
	 */
	private Integer status;

	public Integer getStoreCount() {
		return storeCount;
	}

	public void setStoreCount(Integer storeCount) {
		this.storeCount = storeCount;
	}

	public Integer getResumeCount() {
		return resumeCount;
	}

	public void setResumeCount(Integer resumeCount) {
		this.resumeCount = resumeCount;
	}

	public String getCreateUser() {
		return createUser;
	}

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getCrewName() {
		return crewName;
	}

	public void setCrewName(String crewName) {
		this.crewName = crewName;
	}

	public String getCrewType() {
		return CrewType;
	}

	public void setCrewType(String crewType) {
		CrewType = crewType;
	}

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}

	public Integer getAgoDays() {
		return agoDays;
	}

	public void setAgoDays(Integer agoDays) {
		this.agoDays = agoDays;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getContactAddress() {
		return contactAddress;
	}

	public void setContactAddress(String contactAddress) {
		this.contactAddress = contactAddress;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getShootStartDate() {
		return shootStartDate;
	}

	public void setShootStartDate(String shootStartDate) {
		this.shootStartDate = shootStartDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
