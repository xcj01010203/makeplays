package com.xiaotu.makeplays.crew.model;

import java.util.Date;

/**
 * 剧组信息管理
 * @author lma
 *
 */
public class CrewInfoModel {
	public static final String TABLE_NAME="tab_crew_info";
	
	private String crewId;
	
	/**
	 * 剧组名称
	 */
	private String crewName;
	
	/**
	 * 剧组类型，详情见CrewType枚举类
	 */
	private Integer crewType;
	
	/**
	 * 制片公司
	 */
	private String company;
	
	/**
	 * 有效开始时间
	 */
	private Date startDate;
	
	/**
	 * 有效结束时间
	 */
	private Date endDate;
	
	/**
	 * 拍摄开始时间
	 */
	private Date shootStartDate;
	
	/**
	 * 拍摄结束时间
	 */
	private Date shootEndDate;
	
	/**
	 * 拍摄天数
	 */
	private Long shootCycle;
	
	/**
	 * 题材
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
	 * 主要演员
	 */
	private String mainactor;
	
	/**
	 * 剧组状态。详情见CrewStatus枚举类
	 */
	private Integer status;
	
	/**
	 * 创建人ID
	 */
	private String createUser;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 备案号
	 */
	private String recordNumber;
	
	/**
	 * 入组密码
	 */
	private String enterPassword;
	
	/**
	 * 剧照存储路径
	 */
	private String picPath;
	
	/**
	 * 项目类型，预留字段，例如试用项目、付费项目等，具体内容待定
	 */
	private Integer projectType;

	/**
	 * 是否已刷新权限，用于过期剧组，0：否，1：是
	 */
	private boolean refreshAuth;
	
	/**
	 * 立项集数
	 */
	private Integer seriesNo;
	
	/**
	 * 合拍协议，0：无，1：已签订
	 */
	private Integer coProduction;
	
	/**
	 * 合拍协议金额
	 */
	private Double coProMoney;
	
	/**
	 * 剧组执行预算
	 */
	private Double budget;
	
	/**
	 * 我方投资比例
	 */
	private Double investmentRatio;
	
	/**
	 * 重要事项说明及重要情况预警
	 */
	private String remark;
	
	/**
	 * 前次重要事项说明及重要情况预警
	 */
	private String lastRemark;
	
	/**
	 * 每集时长
	 */
	private Double lengthPerSet;

	/**
	 * 精剪比
	 */
	private Double cutRate;
	
	/**
	 * 是否停用，0：否，1：是
	 */
	private Boolean isStop;
	
	public Double getLengthPerSet() {
		return lengthPerSet;
	}

	public void setLengthPerSet(Double lengthPerSet) {
		this.lengthPerSet = lengthPerSet;
	}

	public Double getCutRate() {
		return cutRate;
	}

	public void setCutRate(Double cutRate) {
		this.cutRate = cutRate;
	}

	public String getPicPath() {
		return this.picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getCrewName() {
		return this.crewName;
	}

	public void setCrewName(String crewName) {
		this.crewName = crewName;
	}

	public Integer getCrewType() {
		return this.crewType;
	}

	public void setCrewType(Integer crewType) {
		this.crewType = crewType;
	}

	public String getCompany() {
		return this.company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getShootStartDate() {
		return this.shootStartDate;
	}

	public void setShootStartDate(Date shootStartDate) {
		this.shootStartDate = shootStartDate;
	}

	public Date getShootEndDate() {
		return this.shootEndDate;
	}

	public void setShootEndDate(Date shootEndDate) {
		this.shootEndDate = shootEndDate;
	}

	public Long getShootCycle() {
		return this.shootCycle;
	}

	public void setShootCycle(Long shootCycle) {
		this.shootCycle = shootCycle;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getShootlocation() {
		return this.shootlocation;
	}

	public void setShootlocation(String shootlocation) {
		this.shootlocation = shootlocation;
	}

	public String getDirector() {
		return this.director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getScriptWriter() {
		return this.scriptWriter;
	}

	public void setScriptWriter(String scriptWriter) {
		this.scriptWriter = scriptWriter;
	}

	public String getMainactor() {
		return this.mainactor;
	}

	public void setMainactor(String mainactor) {
		this.mainactor = mainactor;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCreateUser() {
		return this.createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getRecordNumber() {
		return this.recordNumber;
	}

	public void setRecordNumber(String recordNumber) {
		this.recordNumber = recordNumber;
	}

	public String getEnterPassword() {
		return this.enterPassword;
	}

	public void setEnterPassword(String enterPassword) {
		this.enterPassword = enterPassword;
	}

	public Integer getProjectType() {
		return projectType;
	}

	public void setProjectType(Integer projectType) {
		this.projectType = projectType;
	}	

	public boolean getRefreshAuth() {
		return refreshAuth;
	}

	public void setRefreshAuth(boolean refreshAuth) {
		this.refreshAuth = refreshAuth;
	}

	public Integer getSeriesNo() {
		return seriesNo;
	}

	public void setSeriesNo(Integer seriesNo) {
		this.seriesNo = seriesNo;
	}

	public Integer getCoProduction() {
		return coProduction;
	}

	public void setCoProduction(Integer coProduction) {
		this.coProduction = coProduction;
	}

	public Double getCoProMoney() {
		return coProMoney;
	}

	public void setCoProMoney(Double coProMoney) {
		this.coProMoney = coProMoney;
	}

	public Double getBudget() {
		return budget;
	}

	public void setBudget(Double budget) {
		this.budget = budget;
	}

	public Double getInvestmentRatio() {
		return investmentRatio;
	}

	public void setInvestmentRatio(Double investmentRatio) {
		this.investmentRatio = investmentRatio;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getLastRemark() {
		return lastRemark;
	}

	public void setLastRemark(String lastRemark) {
		this.lastRemark = lastRemark;
	}

	public Boolean getIsStop() {
		return isStop;
	}

	public void setIsStop(Boolean isStop) {
		this.isStop = isStop;
	}
}
