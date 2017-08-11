package com.xiaotu.makeplays.prepare.utils;

import java.util.ArrayList;

/**
 * @ClassName PrepareCrewPeopleUtil
 * @Description TODO 筹备期  剧组人员  工具类
 * @author Administrator
 * @Date 2017年2月23日 下午2:29:18
 * @version 1.0.0
 */
public class PrepareCrewPeopleUtil {

	private String id;
	private String parentId;
	private String groupName;
	private String duties;
	private String name;
	private String phone;
	private String reviewer;
	private String confirmDate;
	private String arrivalTime;
	private String payment;
	private String crewId;
	

	private ArrayList<PrepareCrewPeopleUtil> children = new ArrayList<PrepareCrewPeopleUtil>();
    public void add(PrepareCrewPeopleUtil node) {//递归添加节点
        if ("0".equals(node.parentId)) {
            this.children.add(node);
        } else if (node.parentId.equals(this.id)) {
            this.children.add(node);
        } else {
            for (PrepareCrewPeopleUtil tmp_node : children) {
                tmp_node.add(node);
            }
        }
    }
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getDuties() {
		return duties;
	}
	public void setDuties(String duties) {
		this.duties = duties;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getReviewer() {
		return reviewer;
	}
	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}
	public String getConfirmDate() {
		return confirmDate;
	}
	public void setConfirmDate(String confirmDate) {
		this.confirmDate = confirmDate;
	}
	public String getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public String getPayment() {
		return payment;
	}
	public void setPayment(String payment) {
		this.payment = payment;
	}
	public String getCrewId() {
		return crewId;
	}
	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
	public ArrayList<PrepareCrewPeopleUtil> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<PrepareCrewPeopleUtil> children) {
		this.children = children;
	}
    
    
    
}
