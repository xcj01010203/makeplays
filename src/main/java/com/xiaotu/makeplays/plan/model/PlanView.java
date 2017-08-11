package com.xiaotu.makeplays.plan.model;

/**
 * 计划对应的场景
 * <p> 场景分阶段, 分组
 * 
 * @author subin
 */
public class PlanView {
	
	public static final String TABLE_NAME = "tab_plan_view";
	
	private String id;
	
	//计划ID
	private String planid;
	
	//场景ID
	private String viewid;
	
	//阶段NUM
	private long stagenum;
	
	//分组NUM
	private long groupnum;
	
	//排序NUM
	private long sortnum;

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

	public String getViewid() {
		return viewid;
	}

	public void setViewid(String viewid) {
		this.viewid = viewid;
	}

	public long getStagenum() {
		return stagenum;
	}

	public void setStagenum(long stagenum) {
		this.stagenum = stagenum;
	}

	public long getGroupnum() {
		return groupnum;
	}

	public void setGroupnum(long groupnum) {
		this.groupnum = groupnum;
	}

	public long getSortnum() {
		return sortnum;
	}

	public void setSortnum(long sortnum) {
		this.sortnum = sortnum;
	}
}