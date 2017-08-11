package com.xiaotu.makeplays.crew.model;

import java.io.Serializable;
import java.util.List;

public class RoleCrewAmountModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private List<Integer> setNos;
	private List<String> shootAddressList;
	private List<RolecrewAmountBaseModel> rpabmList;

	public List<Integer> getSetNos() {
		return setNos;
	}

	public void setSetNos(List<Integer> setNos) {
		this.setNos = setNos;
	}

	public List<String> getShootAddressList() {
		return shootAddressList;
	}

	public void setShootAddressList(List<String> shootAddressList) {
		this.shootAddressList = shootAddressList;
	}

	public List<RolecrewAmountBaseModel> getRpabmList() {
		return rpabmList;
	}

	public void setRpabmList(List<RolecrewAmountBaseModel> rpabmList) {
		this.rpabmList = rpabmList;
	}

}
