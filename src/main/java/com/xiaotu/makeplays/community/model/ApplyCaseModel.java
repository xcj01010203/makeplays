package com.xiaotu.makeplays.community.model;

import java.util.Date;

/**
 * 百晓生系统中的应用案例
 * @author xuchangjian 2017-5-3下午2:56:44
 */
public class ApplyCaseModel {
	
	public static final String TABLE_NAME = "tab_apply_case";

	private String id;
	
	/**
	 * 标题
	 */
	private String title;
	
	/**
	 * 副标题
	 */
	private String subTitle;
	
	/**
	 * 描述/简要内容
	 */
	private String introduction;
	
	/**
	 * 预览地址
	 */
	private String preurl;
	
	/**
	 * 完整地址
	 */
	private String srcurl;
	
	/**
	 * 创建时间
	 */
	private Date createtime;
	
	/**
	 * 分享内容标题
	 */
	private String sharetitle;
	
	/**
	 * 分享副标题
	 */
	private String sharesubtitle;
	
	/**
	 * 0:需要付费，1：不需要付费
	 */
	private Integer ispay;
	
	/**
	 * 是否置顶  0：置顶，1：不置顶
	 */
	private Integer istop;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return this.subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getIntroduction() {
		return this.introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getPreurl() {
		return this.preurl;
	}

	public void setPreurl(String preurl) {
		this.preurl = preurl;
	}

	public String getSrcurl() {
		return this.srcurl;
	}

	public void setSrcurl(String srcurl) {
		this.srcurl = srcurl;
	}

	public Date getCreatetime() {
		return this.createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getSharetitle() {
		return this.sharetitle;
	}

	public void setSharetitle(String sharetitle) {
		this.sharetitle = sharetitle;
	}

	public String getSharesubtitle() {
		return this.sharesubtitle;
	}

	public void setSharesubtitle(String sharesubtitle) {
		this.sharesubtitle = sharesubtitle;
	}

	public Integer getIspay() {
		return this.ispay;
	}

	public void setIspay(Integer ispay) {
		this.ispay = ispay;
	}

	public Integer getIstop() {
		return this.istop;
	}

	public void setIstop(Integer istop) {
		this.istop = istop;
	}
}
