package com.xiaotu.makeplays.plan.model;

import java.util.List;

public class Cluster {
	
	private List<Integer> cluster;
	
	private double squaredError;
	
	public List<Integer> getCluster() {
		return cluster;
	}

	public void setCluster(List<Integer> cluster) {
		this.cluster = cluster;
	}

	public double getSquaredError() {
		return squaredError;
	}

	public void setSquaredError(double squaredError) {
		this.squaredError = squaredError;
	}
}
