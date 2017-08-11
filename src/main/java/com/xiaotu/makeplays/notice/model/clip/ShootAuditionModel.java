package com.xiaotu.makeplays.notice.model.clip;

import java.util.Date;

/**
 * 场记单中拍摄镜次信息
 * @author xuchangjian 2015-11-9上午11:59:07
 */
public class ShootAuditionModel {

	public static final String TABLE_NAME = "tab_shootAudition_info";
	
	/**
	 * id
	 */
	private String auditionId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 通告单ID
	 */
	private String noticeId;
	
	/**
	 * 操作人ID
	 */
	private String userId;
	
	/**
	 * 设备号
	 */
	private String deviceUID;
	
	/**
	 * 机位ID
	 */
	private String cameraId;
	
	/**
	 * 集
	 */
	private Integer seriesNo;
	
	/**
	 * 场
	 */
	private String viewNo;
	
	/**
	 * 镜号
	 */
	private int lensNo;
	
	/**
	 * 镜次
	 */
	private int auditionNo;
	
	/**
	 * 景别   1：近景  2：远景 3：特写  4：中景 5：全景  见AuditionSceneType枚举类
	 */
	private Integer sceneType;
	
	/**
	 * 内容
	 */
	private String content;
	
	/**
	 * Tc类型  1：文件  2：时码 见TcType枚举类
	 */
	private Integer tcType;
	
	/**
	 * Tc值
	 */
	private String tcValue;
	
	/**
	 * 成绩 1：OK   2：NG   3：备用 见AuditionGrade枚举类
	 */
	private Integer grade;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 移动端最后保存数据时间
	 */
	private Date mobileTime;
	
	/**
	 * 服务端最后保存数据时间
	 */
	private Date serverTime;
	
	/**
	 * 备注
	 */
	private String comment;
	
	/**
	 * 序号，引入此字段的目的是，保证场记单中场景排列有序
	 */
	private int sequence;

	public String getDeviceUID() {
		return this.deviceUID;
	}

	public void setDeviceUID(String deviceUID) {
		this.deviceUID = deviceUID;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getAuditionId() {
		return this.auditionId;
	}

	public void setAuditionId(String auditionId) {
		this.auditionId = auditionId;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getNoticeId() {
		return this.noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCameraId() {
		return this.cameraId;
	}

	public void setCameraId(String cameraId) {
		this.cameraId = cameraId;
	}

	public Integer getSeriesNo() {
		return this.seriesNo;
	}

	public void setSeriesNo(Integer seriesNo) {
		this.seriesNo = seriesNo;
	}

	public String getViewNo() {
		return this.viewNo;
	}

	public void setViewNo(String viewNo) {
		this.viewNo = viewNo;
	}

	public int getLensNo() {
		return this.lensNo;
	}

	public void setLensNo(int lensNo) {
		this.lensNo = lensNo;
	}

	public int getAuditionNo() {
		return this.auditionNo;
	}

	public void setAuditionNo(int auditionNo) {
		this.auditionNo = auditionNo;
	}

	public Integer getSceneType() {
		return this.sceneType;
	}

	public void setSceneType(Integer sceneType) {
		this.sceneType = sceneType;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getTcType() {
		return this.tcType;
	}

	public void setTcType(Integer tcType) {
		this.tcType = tcType;
	}

	public String getTcValue() {
		return this.tcValue;
	}

	public void setTcValue(String tcValue) {
		this.tcValue = tcValue;
	}

	public Integer getGrade() {
		return this.grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getMobileTime() {
		return this.mobileTime;
	}

	public void setMobileTime(Date mobileTime) {
		this.mobileTime = mobileTime;
	}

	public Date getServerTime() {
		return this.serverTime;
	}

	public void setServerTime(Date serverTime) {
		this.serverTime = serverTime;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
