package com.xiaotu.makeplays.crew.model;

import java.io.Serializable;
import java.util.List;

public class RolecrewAmountBaseModel implements Serializable {
	/**
	 * lma
	 */
	private static final long serialVersionUID = -7372323278494958971L;
	private String roleName;
	private String actorName;
	double totalCrewAmountByView = 0;// 按场统计当前角色的总戏量
	double totalCrewAmountByPage = 0;// 按页统计当前角色的总戏量
	private List<CrewAmountModel> crewAmountModelList;// 当前角色的戏量列表
	private int shootAddressCount;//拍摄地点个数
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getActorName() {
		return actorName;
	}
	public void setActorName(String actorName) {
		this.actorName = actorName;
	}
	public double getTotalCrewAmountByView() {
		return totalCrewAmountByView;
	}
	public void setTotalCrewAmountByView(double totalCrewAmountByView) {
		this.totalCrewAmountByView = totalCrewAmountByView;
	}
	public double getTotalCrewAmountByPage() {
		return totalCrewAmountByPage;
	}
	public void setTotalCrewAmountByPage(double totalCrewAmountByPage) {
		this.totalCrewAmountByPage = totalCrewAmountByPage;
	}
	public List<CrewAmountModel> getCrewAmountModelList() {
		return crewAmountModelList;
	}
	public void setCrewAmountModelList(List<CrewAmountModel> crewAmountModelList) {
		this.crewAmountModelList = crewAmountModelList;
	}
	public int getShootAddressCount() {
		return shootAddressCount;
	}
	public void setShootAddressCount(int shootAddressCount) {
		this.shootAddressCount = shootAddressCount;
	}

	
}
