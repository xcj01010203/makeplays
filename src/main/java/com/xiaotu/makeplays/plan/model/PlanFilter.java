package com.xiaotu.makeplays.plan.model;

import java.util.List;

public class PlanFilter {
	
	private String planId;
	
	private long stagenum;
	
	private long groupnum;
	
	private List<Factor> factors;

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
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

	public List<Factor> getFactors() {
		return factors;
	}

	public void setFactors(List<Factor> factors) {
		this.factors = factors;
	}
}