package com.xiaotu.makeplays.crew.controller.filter;


public class CrewInfoFilter {
	
	private String crewName; //剧组名称	
	private String status;
	private String crewType; //剧组类型	0：电影；1：电视剧；2：网剧；3：网大；99：其他
	
	/****** 李晓平 2016年10月27日 下午3:29:33 *******/
	private String startDate; //有效期-开始日期
	private String endDate; //有效期-结束日期
	private String shootStartDate; //拍摄期-开机日期
	private String shootEndDate; //拍摄期-结束日期
	private String company; //制片公司
	private String director; //导演
	private String projectType; //项目分类  例如：试用项目、付费项目等
	private String crewSortCon; //排序
	private int outofdate; //是否在有效期内
	/****** 李晓平 2016年10月27日 下午3:29:33 *******/
	
	public String getCrewType() {
		return crewType;
	}
	public void setCrewType(String crewType) {
		this.crewType = crewType;
	}
	public String getCrewName() {
		return crewName;
	}
	public void setCrewName(String crewName) {
		this.crewName = crewName;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getShootStartDate() {
		return shootStartDate;
	}
	public void setShootStartDate(String shootStartDate) {
		this.shootStartDate = shootStartDate;
	}
	public String getShootEndDate() {
		return shootEndDate;
	}
	public void setShootEndDate(String shootEndDate) {
		this.shootEndDate = shootEndDate;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getDirector() {
		return director;
	}
	public void setDirector(String director) {
		this.director = director;
	}
	public String getProjectType() {
		return projectType;
	}
	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}
	public String getCrewSortCon() {
		return crewSortCon;
	}
	public void setCrewSortCon(String crewSortCon) {
		this.crewSortCon = crewSortCon;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getOutofdate() {
		return outofdate;
	}
	public void setOutofdate(int outofdate) {
		this.outofdate = outofdate;
	}

}
