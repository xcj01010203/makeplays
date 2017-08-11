package com.xiaotu.makeplays.goods.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.goods.model.ViewGoodsInfoMap;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 场景与物品关联关系表
 * @author wanrenyi 2017年4月25日上午10:07:01
 */
@Repository
public class ViewGoodsMapDao extends BaseDao<ViewGoodsInfoMap>{

	 
	public List<Map<String, Object>> queryGoodsMapListByCondition(Map<String, Object> condition){
		StringBuffer sql = new StringBuffer();
		List<Object> param = new ArrayList<Object>();
		
		sql.append("	select * from tab_view_goods_map");
		sql.append("	where 1=1");
		if (null != condition) {
			
			//根据主键查询
			String id = (String) condition.get("id");
			if (StringUtils.isNotBlank(id)) {
				sql.append("	AND id = ?");
				param.add(id);
			}
			
			//根据场景id查询
			String viewId = (String) condition.get("viewId");
			if (StringUtils.isNotBlank(viewId)) {
				sql.append("	AND viewId = ?");
				param.add(viewId);
			}
			
			//根据物品id查询
			String goodsId = (String) condition.get("goodsId");
			if (StringUtils.isNotBlank(goodsId)) {
				sql.append("	AND goodsId = ?");
				param.add(goodsId);
			}
			
			//根据剧组id查询
			String crewId = (String) condition.get("crewId");
			if (StringUtils.isNotBlank(crewId)) {
				sql.append("	AND crewId = ?");
				param.add(crewId);
			}
		}
		
		return this.query(sql.toString(), param.toArray(), null);
	}
	
	/**
	 * 查询出物品与场景的对应关系 
	 * @param crewId
	 * @return
	 */
	public List<ViewGoodsInfoMap> queryClothViewInfo(String crewId, Integer goodsType){
		StringBuffer sql = new StringBuffer();
		List<Object> param = new ArrayList<Object>();
		sql.append("	SELECT ");
		sql.append("	tgi.goodsName,tgi.goodsType,tvgm.*");
		sql.append("	FROM ");
		sql.append("		tab_goods_info tgi, tab_view_goods_map tvgm");
		sql.append("	WHERE ");
		sql.append("		tgi.id = tvgm.goodsId");
		sql.append("	AND tgi.crewId = ?");
		param.add(crewId);
		if (goodsType != null) {
			sql.append("	AND tgi.goodsType = ?");
			param.add(goodsType);
		}
		
		return this.query(sql.toString(), param.toArray(), ViewGoodsInfoMap.class, null);
	}
	
	/**
	 * 查询出道具与场景的关联关系
	 * @param crewId
	 * @return
	 */
	public List<ViewGoodsInfoMap> queryPropViewMap(String crewId){
		StringBuffer sql = new StringBuffer();
		sql.append("	SELECT ");
		sql.append("	tvgm.*");
		sql.append("	FROM ");
		sql.append("		tab_goods_info tgi, tab_view_goods_map tvgm");
		sql.append("	WHERE ");
		sql.append("		tgi.id = tvgm.goodsId");
		sql.append("	AND tgi.crewId = ?");
		sql.append("	AND tgi.goodsType in (0,1)");
		
		return this.query(sql.toString(), new Object[] {crewId}, ViewGoodsInfoMap.class, null);
	}
	
	/**
	 * 删除剧组下所有未手动保存的场景和角色的关联
	 * @param crewId
	 */
	public void deleteNoSaveViewPropMap(String crewId) {
		String sql = "DELETE from "+ ViewGoodsInfoMap.TABLE_NAME +" where viewId in(SELECT viewId from tab_view_info where isManualSave = 0 and shootStatus = 0 and crewId = ?)";
		this.getJdbcTemplate().update(sql, crewId);
	}
}
