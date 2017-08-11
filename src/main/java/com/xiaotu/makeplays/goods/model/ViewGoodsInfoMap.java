package com.xiaotu.makeplays.goods.model;

/**
 * 场景与物品关联的map对象
 * @author wanrenyi 2017年4月24日上午11:11:06
 */
public class ViewGoodsInfoMap {

	public static final String TABLE_NAME = "tab_view_goods_map";
	
	/**
	 * 主键id
	 */
	private String id;
	
	/**
	 * 场景id
	 */
	private String viewId;
	
	/**
	 * 物品id
	 */
	private String goodsId;
	
	/**
	 * 剧组id
	 */
	private String crewId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getViewId() {
		return viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String getCrewId() {
		return crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
	
}
