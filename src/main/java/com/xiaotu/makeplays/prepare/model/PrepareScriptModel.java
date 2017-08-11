package com.xiaotu.makeplays.prepare.model;

import java.sql.Date;

/**
 * @ClassName PrepareScriptModel
 * @Description 剧本筹备进度
 * @author Administrator
 * @Date 2017年2月10日 上午10:01:11
 * @version 1.0.0
 */
public class PrepareScriptModel {
	public static final String TABLE_NAME = "tab_prepare_script";
	
	private String id;//主键
	
	private String scriptTypeId;//剧本类型id
	
	private String edition;//版本
	
	private Date finishDate;//交稿日期
	
	private String personLiable;//负责人
	
	private String content;//内容
	
	private String status;//状态
	
	private String mark;//备注

	private String crewid;//剧组id
	
	/**
	 * 父ID
	 */
	private String parentId;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	public String getParentId() {
		return this.parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCrewid() {
		return crewid;
	}

	public void setCrewid(String crewid) {
		this.crewid = crewid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getScriptTypeId() {
		return scriptTypeId;
	}

	public void setScriptTypeId(String scriptTypeId) {
		this.scriptTypeId = scriptTypeId;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public String getPersonLiable() {
		return personLiable;
	}

	public void setPersonLiable(String personLiable) {
		this.personLiable = personLiable;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}
	
	
	

	
	
}
