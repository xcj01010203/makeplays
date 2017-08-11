package com.xiaotu.makeplays.crew.model;

import java.io.Serializable;
import java.util.List;

public class ShootScheduleModel implements Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * lma
	 */
	private String title;
	private List<Double> crewAmountList;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Double> getcrewAmountList() {
		return crewAmountList;
	}

	public void setcrewAmountList(List<Double> crewAmountList) {
		this.crewAmountList = crewAmountList;
	}

}
