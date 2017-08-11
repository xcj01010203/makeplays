package com.xiaotu.makeplays.plan.model;

/**
 * 计划的影响因素
 * 
 * @author subin
 */
public class Factor {
	
	public static final String TABLE_NAME = "tab_plan_factor";
	
	private String id;
	
	//计划ID
	private String planid;
	
	//因素类型 地址1, 演员2, 道具3
	private String type;
	
	private String factorid;
	
	private String factorname;
	
	//因素优先级
	private long priority;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPlanid() {
		return planid;
	}

	public void setPlanid(String planid) {
		this.planid = planid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFactorid() {
		return factorid;
	}

	public void setFactorid(String factorid) {
		this.factorid = factorid;
	}

	public String getFactorname() {
		return factorname;
	}

	public void setFactorname(String factorname) {
		this.factorname = factorname;
	}

	public long getPriority() {
		return priority;
	}

	public void setPriority(long priority) {
		this.priority = priority;
	}
}