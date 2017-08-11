package com.xiaotu.makeplays.prepare.utils;

import java.util.ArrayList;

/**
 * @ClassName PrepareWorkUtil
 * @Description 筹备期办公筹备 工具类
 * @author Administrator
 * @Date 2017年2月23日 下午5:05:14
 * @version 1.0.0
 */
public class PrepareWorkUtil {
	private String id;
	private String parentId;
	private String type;
	private String purpose;
	private String schedule;
	private String personLiable;
	private ArrayList<PrepareWorkUtil> children = new ArrayList<PrepareWorkUtil>();

	public void add(PrepareWorkUtil node) {// 递归添加节点
		if ("0".equals(node.parentId)) {
			this.children.add(node);
		} else if (node.parentId.equals(this.id)) {
			this.children.add(node);
		} else {
			for (PrepareWorkUtil tmp_node : children) {
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public String getPersonLiable() {
		return personLiable;
	}

	public void setPersonLiable(String personLiable) {
		this.personLiable = personLiable;
	}

	public ArrayList<PrepareWorkUtil> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<PrepareWorkUtil> children) {
		this.children = children;
	}

}
