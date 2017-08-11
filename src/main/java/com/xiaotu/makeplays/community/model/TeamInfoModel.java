package com.xiaotu.makeplays.community.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 组训基本信息表
 * @author wanrenyi 2016年9月1日下午3:54:25
 */
public class TeamInfoModel {

	public static final String TABLE_NAME = "tab_team_info";
	
	/**
	 * 组训id
	 */
	private String teamId;
	
	/**
	 * 用户id(创建人的id)
	 */
	private String createUser;
	
	/**
	 * 剧组名称
	 */
	private String crewName;
	
	/**
	 * 剧组类型 ; 0：电影；1：电视剧；2：网络剧；3: 网大 ; 99：其他';详情参见CrewType枚举类
	 */
	private Integer crewType;
	
	/**
	 * 制片公司
	 */
	private String company;
	
	/**
	 * 剧组题材
	 */
	private String subject;
	
	/**
	 * 拍摄地点
	 */
	private String shootlocation;
	
	/**
	 * 导演
	 */
	private String director;
	
	/**
	 * 编剧
	 */
	private String scriptWriter;
	
	/**
	 * 开机时间
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date shootStartDate;
	
	/**
	 * 杀青时间
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date shootEndDate;
	
	/**
	 * 联系人姓名
	 */
	private String contactname;
	
	/**
	 * 联系电话
	 */
	private String phoneNum;
	
	/**
	 * 邮箱
	 */
	private String email;
	
	/**
	 * 筹备地址(联系地址)
	 */
	private String contactAddress;
	
	/**
	 * 剧组简介
	 */
	private String crewComment;
	
	/**
	 * 剧组宣传图片地址
	 */
	private String picPath;
	
	/**
	 * 当前组训状态; 1：可用；2：不可用; 默认可用 详情参见teamStatus枚举类
	 */
	private Integer status = 1;
	
	/**
	 * 创建时间
	 */
	private Date createTime;

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getCrewName() {
		return crewName;
	}

	public void setCrewName(String crewName) {
		this.crewName = crewName;
	}

	public Integer getCrewType() {
		return crewType;
	}

	public void setCrewType(Integer crewType) {
		this.crewType = crewType;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getShootlocation() {
		return shootlocation;
	}

	public void setShootlocation(String shootlocation) {
		this.shootlocation = shootlocation;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getScriptWriter() {
		return scriptWriter;
	}

	public void setScriptWriter(String scriptWriter) {
		this.scriptWriter = scriptWriter;
	}

	public Date getShootStartDate() {
		return shootStartDate;
	}

	public void setShootStartDate(Date shootStartDate) {
		this.shootStartDate = shootStartDate;
	}

	public Date getShootEndDate() {
		return shootEndDate;
	}

	public void setShootEndDate(Date shootEndDate) {
		this.shootEndDate = shootEndDate;
	}

	public String getContactname() {
		return contactname;
	}

	public void setContactname(String contactname) {
		this.contactname = contactname;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContactAddress() {
		return contactAddress;
	}

	public void setContactAddress(String contactAddress) {
		this.contactAddress = contactAddress;
	}

	public String getCrewComment() {
		return crewComment;
	}

	public void setCrewComment(String crewComment) {
		this.crewComment = crewComment;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
