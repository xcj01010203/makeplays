package com.xiaotu.makeplays.roleactor.model;

/**
 * 演职员评价与标签关联关系
 * 
 * @author xuchangjian 2016-7-12下午5:47:36
 */
public class EvaluateTagMapModel {

	public static final String TABLE_NAME = "tab_evaluate_tag_map";

	private String mapId;

	/**
	 * 评价信息ID
	 */
	private String evaluateId;

	/**
	 * 标签信息Id
	 */
	private String tagId;

	public String getMapId() {
		return mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public String getEvaluateId() {
		return evaluateId;
	}

	public void setEvaluateId(String evaluateId) {
		this.evaluateId = evaluateId;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

}
