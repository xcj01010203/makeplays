package com.xiaotu.makeplays.crew.model;

import java.io.Serializable;
import java.util.List;

public class CrewAmountModel implements Serializable {

	/**
	 * lma
	 */
	private static final long serialVersionUID = 6695462121237390070L;

	private String name;
	private double crewAmountByview;// 以场为单位统计的戏量
	private double crewAmountByPage;// 以页为单位统计的戏量
	private List<CrewAmountModel> childcrewAmountModelList; //子级戏量列表
	
	private String addressName;//lma 拍摄地点
	private Integer viewType;//lma 文武戏
	private String atmosphereName;//lma气氛名称
	private String site;//lma内外景
	
	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public Integer getViewType() {
		return viewType;
	}

	public void setViewType(Integer viewType) {
		this.viewType = viewType;
	}

	public String getAtmosphereName() {
		return atmosphereName;
	}

	public void setAtmosphereName(String atmosphereName) {
		this.atmosphereName = atmosphereName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getcrewAmountByview() {
		return crewAmountByview;
	}

	public void setcrewAmountByview(double crewAmountByview) {
		this.crewAmountByview = crewAmountByview;
	}

	public double getcrewAmountByPage() {
		return crewAmountByPage;
	}

	public void setcrewAmountByPage(double crewAmountByPage) {
		this.crewAmountByPage = crewAmountByPage;
	}

	public List<CrewAmountModel> getChildcrewAmountModelList() {
		return childcrewAmountModelList;
	}

	public void setChildcrewAmountModelList(
			List<CrewAmountModel> childcrewAmountModelList) {
		this.childcrewAmountModelList = childcrewAmountModelList;
	}

	public String getAddressName() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

}
