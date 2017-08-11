package com.xiaotu.makeplays.prepare.utils;

import java.util.ArrayList;

/**
 * @ClassName PrepareOperateUtil
 * @Description  筹备期 运营 工具类
 * @author Administrator
 * @Date 2017年2月23日 下午5:12:03
 * @version 1.0.0
 */
public class PrepareOperateUtil {
	
	private String id;
	private String parentId;
	private String operateType;
	private String operateBrand;
	private String operateMode;
	private String operateCost;
	private String contactName;
	private String phoneNumber;
	private String mark;
	
	private String personLiable ;
	
	private ArrayList<PrepareOperateUtil> children = new ArrayList<PrepareOperateUtil>();

	public void add(PrepareOperateUtil node) {// 递归添加节点
		if ("0".equals(node.parentId)) {
			this.children.add(node);
		} else if (node.parentId.equals(this.id)) {
			this.children.add(node);
		} else {
			for (PrepareOperateUtil tmp_node : children) {
				tmp_node.add(node);
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getPersonLiable() {
		return personLiable;
	}

	public void setPersonLiable(String personLiable) {
		this.personLiable = personLiable;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
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

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public String getOperateBrand() {
		return operateBrand;
	}

	public void setOperateBrand(String operateBrand) {
		this.operateBrand = operateBrand;
	}

	public String getOperateMode() {
		return operateMode;
	}

	public void setOperateMode(String operateMode) {
		this.operateMode = operateMode;
	}

	public String getOperateCost() {
		return operateCost;
	}

	public void setOperateCost(String operateCost) {
		this.operateCost = operateCost;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public ArrayList<PrepareOperateUtil> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<PrepareOperateUtil> children) {
		this.children = children;
	}
	
	
	
	
}
