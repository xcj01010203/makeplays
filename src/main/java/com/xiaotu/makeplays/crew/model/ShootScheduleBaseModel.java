package com.xiaotu.makeplays.crew.model;

import java.io.Serializable;

@Deprecated
public class ShootScheduleBaseModel implements Serializable {

	/**
	 * lma 概览
	 */
	private String title;
	private double totalCrewAmount;
	private double finishedCrewAmount;
	private double unfinishedCrewAmount;
	private Integer shootDays;
	private Integer shootedDays;
	private double finishedPercent;
	private double dailyFinishedCrewAmount;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getTotalCrewAmount() {
		return totalCrewAmount;
	}

	public void setTotalCrewAmount(double totalCrewAmount) {
		this.totalCrewAmount = totalCrewAmount;
	}

	public double getFinishedCrewAmount() {
		return finishedCrewAmount;
	}

	public void setFinishedCrewAmount(double finishedCrewAmount) {
		this.finishedCrewAmount = finishedCrewAmount;
	}

	public double getUnfinishedCrewAmount() {
		return unfinishedCrewAmount;
	}

	public void setUnfinishedCrewAmount(double unfinishedCrewAmount) {
		this.unfinishedCrewAmount = unfinishedCrewAmount;
	}

	public double getFinishedPercent() {
		return finishedPercent;
	}

	public void setFinishedPercent(double finishedPercent) {
		this.finishedPercent = finishedPercent;
	}

	public Integer getShootDays() {
		return shootDays;
	}

	public void setShootDays(Integer shootDays) {
		this.shootDays = shootDays;
	}

	public Integer getShootedDays() {
		return shootedDays;
	}

	public void setShootedDays(Integer shootedDays) {
		this.shootedDays = shootedDays;
	}

	public Double getDailyFinishedCrewAmount() {
		return dailyFinishedCrewAmount;
	}

	public void setDailyFinishedCrewAmount(Double dailyFinishedCrewAmount) {
		this.dailyFinishedCrewAmount = dailyFinishedCrewAmount;
	}

}
