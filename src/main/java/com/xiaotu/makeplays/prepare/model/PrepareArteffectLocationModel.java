package com.xiaotu.makeplays.prepare.model;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @ClassName PrepareArteffectLocationModel
 * @Description 筹备期  美术视觉 -场景
 * @author Administrator
 * @Date 2017年2月10日 上午10:28:26
 * @version 1.0.0
 */
public class PrepareArteffectLocationModel {
	public static final String TABLE_NAME = "tab_prepare_arteffect_location";
	
	private String id;
	
	private String location;//场景
	
	private String designSketch;//效果图
	
	private Date  designSketchDate;//出效果图日期
	
	private String workDraw;//施工图
	
	private Date workDrawDate;//出施工图时间
	
	private String scenery;//置景
	
	private Date sceneryDate;//置景时间
	
	private String reviewer;//审核人
	
	private String opinion;//意见
	
	private Timestamp createTime;//创建时间
	
	private String crewId;
	
	public String getCrewId() {
		return crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDesignSketch() {
		return designSketch;
	}

	public void setDesignSketch(String designSketch) {
		this.designSketch = designSketch;
	}

	public Date getDesignSketchDate() {
		return designSketchDate;
	}

	public void setDesignSketchDate(Date designSketchDate) {
		this.designSketchDate = designSketchDate;
	}

	public String getWorkDraw() {
		return workDraw;
	}

	public void setWorkDraw(String workDraw) {
		this.workDraw = workDraw;
	}

	public Date getWorkDrawDate() {
		return workDrawDate;
	}

	public void setWorkDrawDate(Date workDrawDate) {
		this.workDrawDate = workDrawDate;
	}

	public String getScenery() {
		return scenery;
	}

	public void setScenery(String scenery) {
		this.scenery = scenery;
	}

	public Date getSceneryDate() {
		return sceneryDate;
	}

	public void setSceneryDate(Date sceneryDate) {
		this.sceneryDate = sceneryDate;
	}

	public String getReviewer() {
		return reviewer;
	}

	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
	

}
