package com.xiaotu.makeplays.community.model;

import java.util.Date;

/**
 * 资讯对象模型
 * @author wanrenyi 2016年9月14日上午9:13:58
 */
public class NewsInfoModel {

	public static final String TABLE_NAME = "tab_news_baseinfo";
	
	/**
	 * 资讯id
	 */
	private String id;
	
	/**
	 * 用户id
	 */
	private String userid;
	
	/**
	 * 资讯标题
	 */
	private String title;
	
	/**
	 * 副标题
	 */
	private String subtitle;
	
	/**
	 * 资讯简介
	 */
	private String introduction;
	
	/**
	 * 资讯来源网站名称
	 */
	private String srcname;
	
	/**
	 * 资讯来源网站地址
	 */
	private String srcurl;
	
	/**
	 * 资讯内容
	 */
	private String content;
	
	/**
	 * 资讯发布时间
	 */
	private Date newstime;
	
	/**
	 * 创建时间
	 */
	private Date createtime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getSrcname() {
		return srcname;
	}

	public void setSrcname(String srcname) {
		this.srcname = srcname;
	}

	public String getSrcurl() {
		return srcurl;
	}

	public void setSrcurl(String srcurl) {
		this.srcurl = srcurl;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getNewstime() {
		return newstime;
	}

	public void setNewstime(Date newstime) {
		this.newstime = newstime;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
}
