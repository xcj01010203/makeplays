package com.xiaotu.makeplays.plan.model;

import java.util.List;
import java.util.Map;

/**
 * 计划
 * 
 * @author subin
 */
public class Plan {
	
	public static final String TABLE_NAME = "tab_plan";
	
	private String id;
	
	private String name;
	
	//剧本ID
	private String crewid;
	
	//是否是默认计划
	private String official;
	
	//影响因素
	private List<Factor> factors;
	
	//对应的场景
	private List<Map<String, Object>> planViews;
	
	public List<Factor> getFactors() {
		return factors;
	}

	public void setFactors(List<Factor> factors) {
		this.factors = factors;
	}

	public List<Map<String, Object>> getPlanViews() {
		return planViews;
	}

	public void setPlanViews(List<Map<String, Object>> planViews) {
		this.planViews = planViews;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCrewid() {
		return crewid;
	}

	public void setCrewid(String crewid) {
		this.crewid = crewid;
	}

	public String getOfficial() {
		return official;
	}

	public void setOfficial(String official) {
		this.official = official;
	}
}