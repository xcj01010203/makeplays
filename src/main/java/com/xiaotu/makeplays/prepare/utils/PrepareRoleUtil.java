package com.xiaotu.makeplays.prepare.utils;

import java.util.ArrayList;

/**
 * @ClassName PrepareRoleUtil  筹备期选角进度  转换为树表数据类
 * @Description TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @Date 2017年2月23日 上午10:19:57
 * @version 1.0.0
 */
public class PrepareRoleUtil {
	private String id;
	private String role;//'角色名称'
	private String actor;//'备选演员'
	private String schedule;//'沟通进度'
	private String content;//'沟通内容'
	private String mark;//'备注'
	private String parentId;
	private String crewId;//'剧组id'
	
	private ArrayList<PrepareRoleUtil> children = new ArrayList<PrepareRoleUtil>();
    public void add(PrepareRoleUtil node) {//递归添加节点
        if ("0".equals(node.parentId)) {
            this.children.add(node);
        } else if (node.parentId.equals(this.id)) {
            this.children.add(node);
        } else {
            for (PrepareRoleUtil tmp_node : children) {
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
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getActor() {
		return actor;
	}
	public void setActor(String actor) {
		this.actor = actor;
	}
	public String getSchedule() {
		return schedule;
	}
	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getCrewId() {
		return crewId;
	}
	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
	public ArrayList<PrepareRoleUtil> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<PrepareRoleUtil> children) {
		this.children = children;
	}
    
    
    
}
