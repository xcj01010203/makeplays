package com.xiaotu.makeplays.view.model;

/**
 * 场景剧本内容表
 * @author xuchangjian
 */
public class ViewContentModel {

	public static final String TABLE_NAME = "tab_view_content";
	
	/**
	 * 场景内容ID
	 */
	private String contentId;
	
	/**
	 * 场景ID
	 */
	private String viewId;
	
	/**
	 * 剧本标题
	 */
	private String title;
	
	/**
	 * 剧本内容
	 */
	private String content;
	
	/**
	 * 剧本ID
	 */
	private String crewId;
	
	/**
	 * 剧本状态，详情见ViewContentStatus枚举类
	 */
	private Integer status;
	
	/**
	 * 信息指纹，MD5加密后的字符串
	 */
	private String figureprint;
	
	/**
	 * 已读用户ID，格式[a, b, c, d]
	 */
	private String readedPeopleIds;

	public String getReadedPeopleIds() {
		return this.readedPeopleIds;
	}

	public void setReadedPeopleIds(String readedPeopleIds) {
		this.readedPeopleIds = readedPeopleIds;
	}

	public String getFigureprint() {
		return this.figureprint;
	}

	public void setFigureprint(String figureprint) {
		this.figureprint = figureprint;
	}

	public String getContentId() {
		return this.contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public String getViewId() {
		return this.viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
