package com.xiaotu.makeplays.notice.controller.dto.clip;

/**
 * 场记单中镜次信息Dto
 * @author xuchangjian 2015-11-9下午4:00:25
 */
public class ShootAuditionDto {

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
	 * 集次
	 */
	private Integer seriesNo;
	
	/**
	 * 场次
	 */
	private String viewNo;
	
	/**
	 * 气氛
	 */
	private String atmosphere;
	
	/**
	 * 内外景
	 */
	private String site;
	
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
	private String createTime;
	
	/**
	 * 移动端最后保存数据时间
	 */
	private String mobileTime;
	
	/**
	 * 服务端最后保存数据时间
	 */
	private String serverTime;
	
	/**
	 * 备注
	 */
	private String comment;

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

	public String getAtmosphere() {
		return this.atmosphere;
	}

	public void setAtmosphere(String atmosphere) {
		this.atmosphere = atmosphere;
	}

	public String getSite() {
		return this.site;
	}

	public void setSite(String site) {
		this.site = site;
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

	public String getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getMobileTime() {
		return this.mobileTime;
	}

	public void setMobileTime(String mobileTime) {
		this.mobileTime = mobileTime;
	}

	public String getServerTime() {
		return this.serverTime;
	}

	public void setServerTime(String serverTime) {
		this.serverTime = serverTime;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
