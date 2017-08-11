package com.xiaotu.makeplays.goods.model;

import java.util.Date;

/**
 * 物品信息对象
 * @author wanrenyi 2017年4月24日上午10:42:12
 */
public class GoodsInfoModel {
	
	public static final String TABLE_NAME = "tab_goods_info";

	/**
	 * 主键id
	 */
	private String id;
	
	/**
	 * 物品名称
	 */
	private String goodsName;
	
	/**
	 * 物品类型 详情参见 GoodsType
	 */
	private Integer goodsType;
	
	/**
	 * 草图、效果图存放地址
	 */
	private String draftUrl;
	
	/**
	 * 草图、效果图描述
	 */
	private String draftDesc;
	
	/**
	 * 创建人员用户ID
	 */
	private String userId;
	
	/**
	 * 用户名
	 */
	private String userName;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 库存量
	 */
	private Integer stock;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public Integer getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(Integer goodsType) {
		this.goodsType = goodsType;
	}

	public String getDraftUrl() {
		return draftUrl;
	}

	public void setDraftUrl(String draftUrl) {
		this.draftUrl = draftUrl;
	}

	public String getDraftDesc() {
		return draftDesc;
	}

	public void setDraftDesc(String draftDesc) {
		this.draftDesc = draftDesc;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCrewId() {
		return crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}
	
}
