package com.xiaotu.makeplays.goods.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.goods.model.GoodsInfoModel;
import com.xiaotu.makeplays.goods.model.ViewGoodsInfoMap;
import com.xiaotu.makeplays.goods.model.constants.GoodsType;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 物品管理的dao层
 * @author wanrenyi 2017年4月24日上午11:33:27
 */
@Repository
public class GoodsInfoDao extends BaseDao<GoodsInfoModel>{

	/**
	 * 根据多个条件查询物品信息列表
	 * @param conditionMap
	 * @return
	 */
	public List<GoodsInfoModel> queryGoodsByCondition(Map<String, Object> conditionMap){
		StringBuffer sql = new StringBuffer();
		List<Object> param = new ArrayList<Object>();
		
		sql.append("	SELECT  *");
		sql.append("	FROM ");
		sql.append("		tab_goods_info tgi");
		sql.append("	WHERE 1=1");
		
		//添加查询条件
		if (conditionMap != null) {
			//根据剧组id查询
			String crewId = (String) conditionMap.get("crewId");
			if (StringUtils.isNotBlank(crewId)) {
				sql.append("	AND tgi.crewId = ?");
				param.add(crewId);
			}
			
			//根据物品id查询
			String id = (String) conditionMap.get("id");
			if (StringUtils.isNotBlank(id)) {
				sql.append("	AND tgi.id = ?");
				param.add(id);
			}
			
			//根据物品名称查询
			String goodsName = (String) conditionMap.get("goodsName");
			if (StringUtils.isNotBlank(goodsName)) {
				sql.append("	AND tgi.goodsName = ?");
				param.add(goodsName);
			}
			
			//根据物品类型查询
			Integer goodsType = (Integer) conditionMap.get("goodsType");
			if (goodsType != null) {
				sql.append("	AND tgi.goodsType = ?");
				param.add(goodsType);
			}
			
			//根据创建人员查询
			String userId = (String) conditionMap.get("userId");
			if (StringUtils.isNotBlank(userId)) {
				sql.append("	AND tgi.userId = ?");
				param.add(userId);
			}
			
			//根据备注查询
			String remark = (String) conditionMap.get("remark");
			if (StringUtils.isNotBlank(remark)) {
				sql.append("	AND tgi.remark = ?");
				param.add(remark);
			}
		}
		
		return this.query(sql.toString(), param.toArray(), GoodsInfoModel.class, null);
	}
	
	/**
	 * @Description  查询道具信息
	 * @param crewId
	 * @param propName
	 * @param type
	 * @param start
	 * @param end
	 * @return
	 */
	public List<Map<String, Object>> queryGoodsInfoByView(String crewId,String goodsName,Integer type, String start,String end, Integer sortType){
		List<Object> args = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT ");
		sql.append("    propsId,propsName,propsType,crewid,remark,stock,createTime,max(firstUse) firstUse,sum(allViewNo) allViewNo,max(seriesNo) seriesNo,max(viewNo) viewNo ");
		sql.append(" FROM ");
		sql.append("    ( ");
		sql.append("       SELECT ");
		sql.append("          id propsId, ");
		sql.append("          goodsName propsName, ");
		sql.append("          goodsType propsType, ");
		sql.append("          crewid, ");
		sql.append("          remark, ");
		sql.append("          stock, ");
		sql.append("          createTime, ");
		sql.append("          CONCAT(seriesNo, ' - ', viewno) firstUse, ");
		sql.append("          allViewNo, ");
		sql.append("		  seriesNo,");
		sql.append("		  viewNo");
		sql.append("       FROM ");
		sql.append("          ( ");
		sql.append("             SELECT ");
		sql.append("                tpi.*, tvi.viewid, ");
		sql.append("                tvi.seriesNo, ");
		sql.append("                tvi.viewNo, ");
		sql.append("				count(DISTINCT tvi.viewid) allViewNo");
		sql.append("             FROM ");
		sql.append("                tab_goods_info tpi ");
		sql.append("             LEFT JOIN tab_view_goods_map tvpm ON tvpm.goodsId = tpi.id ");
		sql.append("             LEFT JOIN tab_view_info tvi ON tvpm.viewid = tvi.viewid ");
		sql.append("             WHERE ");
		sql.append("                tpi.crewid = ? ");
		args.add(crewId);
		sql.append("				AND tvpm.crewId = ?");
		sql.append("				AND tvi.crewId = ?");
		args.add(crewId);
		args.add(crewId);
		sql.append("	GROUP BY tpi.goodsName ");
		sql.append("          ) a ");
		sql.append("	UNION");
		sql.append("		SELECT");
		sql.append("			id propsId,goodsName propsName,goodsType propsType,crewid,remark,");
		sql.append("						stock,createTime,'' firstUse,0 allViewNo,0 seriesNo,'' viewNo");
		sql.append("		FROM");
		sql.append("			tab_goods_info tpi");
		sql.append("		WHERE");
		sql.append("		tpi.crewid = ?");
		args.add(crewId);
		sql.append("    ) b ");
		sql.append(" WHERE ");
		sql.append("    1 = 1 ");
		if(StringUtils.isNotBlank(goodsName)){
			sql.append(" and propsName like ? ");
			args.add("%"+goodsName+"%");
		}
		if(type != null ){
			sql.append(" and propstype = ? ");
			args.add(type);
		}
		if(StringUtils.isNotBlank(start)){
			sql.append(" and allViewNo >= ? ");
			args.add(start);
		}
		if(StringUtils.isNotBlank(end)){
			sql.append(" and allViewNo <= ? ");
			args.add(end);
		}
		sql.append("	group by propsId,propsName,propsType,crewid,remark,stock,createTime ");
		
		//判断排序条件
		if (sortType != null) {
			//按场数排序
			if (sortType == 0) {
				sql.append("	ORDER BY allViewNo desc,seriesNo, abs(viewno), viewno, propsType,CONVERT(propsName USING gbk)");
			}else if (sortType == 1) { //按照道具名称排序
				sql.append("	ORDER BY CONVERT(propsName USING gbk),allViewNo, seriesNo, abs(viewno), viewno, propsType");
			}else if (sortType == 2) { //按照道具类型排序
				sql.append("	ORDER BY propsType, allViewNo desc,seriesNo, abs(viewno), viewno,CONVERT(propsName USING gbk)");
			}else if (sortType == 3) { //按照出场集次排序
				sql.append("	ORDER BY seriesNo, abs(viewno), viewno, allViewNo desc, propsType,CONVERT(propsName USING gbk)");
			}
		}
		List<Map<String, Object>> resultList = this.getJdbcTemplate().queryForList(sql.toString(), args.toArray());
		return resultList;
	}
	
	/**
	 * 根据id获取库存总量
	 * @param idArray
	 * @return
	 */
	public Integer queryStockCount(String[] idArray){
		Integer count = 0;
		StringBuilder sql = new StringBuilder();
		sql.append("select sum(stock) stockCount from tab_goods_info where id in ( ");
		List<Object> args = new ArrayList<Object>();
		for(int i =0,le = idArray.length;i<le;i++){
			sql.append("?");
			args.add(idArray[i]);
			if(i < le -1){
				sql.append(",");
			}
			
		}
		sql.append(" ) ");
		List<Map<String, Object>> countList = this.getJdbcTemplate().queryForList(sql.toString(),args.toArray());
		if(countList != null && countList.size() > 0){
			count = countList.get(0).get("stockCount")!=null?Integer.valueOf(countList.get(0).get("stockCount").toString()):0;
		}
		return count;
	}
	
	/**
	 * @Description  根据id删除物品表信息
	 * @param idArray
	 */
	public void delGoodsInfoByIds(String[] idArray){
		String sql = "delete from tab_goods_info where id = ?";
		List<Object[]> args = new ArrayList<Object[]>();
		for(String id :idArray){
			args.add(new Object[]{id});
		}
		this.getJdbcTemplate().batchUpdate(sql,args);
	}
	
	/**
	 * @Description 修改场景物品对照表中物品表id
	 * @param idArray
	 * @param newId
	 */
	public void updateViewGoodsMapInfo(String[] idArray ,String newId){
		String sql = "update tab_view_goods_map set goodsId = ? where goodsId = ?  ";
		List<Object[]> args = new ArrayList<Object[]>();
		for(String id :idArray){
			args.add(new Object[]{newId,id});
		}
		this.getJdbcTemplate().batchUpdate(sql,args);
	}
	
	/**
	 * @Description 批量修改物品类型
	 * @param ids 道具id
	 * @param type  道具类型
	 */
	public void updateGoodsType(String[] ids,Integer type){
		String sql = "update tab_goods_info set goodsType = ? where id = ? ";
		List<Object[]> args = new ArrayList<Object[]>();
		for(String id:ids){
			args.add(new Object[]{type,id});
		}
		this.getJdbcTemplate().batchUpdate(sql,args);
	}
	
	/**
	 * @Description  根据id删除物品表信息
	 * @param idArray
	 */
	public void delGoodsInfo(String[] idArray){
		String sql = "delete from tab_goods_info where id = ?";
		List<Object[]> args = new ArrayList<Object[]>();
		for(String id :idArray){
			args.add(new Object[]{id});
		}
		this.getJdbcTemplate().batchUpdate(sql,args);
	}
	
	/**
	 * @Description 根据物品id查询物品使用情况
	 * @param propsId
	 * @return
	 */
	public List<Map<String, Object>> queryGoodsUseInfo(String id, String crewId){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("    tsi.vName shootName, ");
		sql.append("    tsi.id shootId, ");
		sql.append("    tvl.location, ");
		sql.append("    count(DISTINCT tvi.viewId) number ");
		sql.append(" FROM ");
		sql.append("    tab_view_goods_map tvpm ");
		sql.append(" LEFT JOIN tab_view_info tvi ON tvpm.viewId = tvi.viewId ");
		sql.append(" LEFT JOIN tab_view_location_map tvlm ON tvi.viewId = tvlm.viewId ");
		sql.append(" LEFT JOIN tab_view_location tvl ON tvlm.locationId = tvl.locationId ");
		sql.append(" LEFT JOIN tab_sceneview_info tsi ON tsi.id = tvi.shootLocationId ");
		sql.append(" WHERE ");
		sql.append("    tvpm.goodsId = ?  and tvpm.crewId = ?");
		sql.append("	AND (tvl.locationType = 1 or tvl.location is null)");
		sql.append(" GROUP BY ");
		sql.append("    tsi.id, ");
		sql.append("    tsi.vName, ");
		sql.append("    tvl.location ");
		sql.append(" ORDER BY ");
		sql.append("    vname ");
		return this.getJdbcTemplate().queryForList(sql.toString(), new Object[]{id, crewId});
	}
	
	/**
	 * 查询出服化道三张表中的旧数据
	 * @return
	 */
	public List<Map<String, Object>> queryAnsycData(){
		StringBuffer sql = new StringBuffer();
		sql.append("	SELECT");
		sql.append("		tpi.propsName goodsName,");
		sql.append("		tpi.propsType goodType,");
		sql.append("		tpi.draftUrl,");
		sql.append("		tpi.draftDesc,");
		sql.append("		tpi.userId,");
		sql.append("		tpi.userName,");
		sql.append("		tpi.createTime,");
		sql.append("		tpi.crewId,");
		sql.append("		tpi.remark,");
		sql.append("		tpi.stock,");
		sql.append("		tvpm.viewId");
		sql.append("	FROM");
		sql.append("		tab_props_info tpi");
		sql.append("	LEFT JOIN tab_view_props_map tvpm ON tvpm.propsId = tpi.propsId ");
		sql.append("	group by tpi.propsName ");
		sql.append(" UNION ");
		sql.append("	SELECT");
		sql.append("		tmi.makeupName goodsName,");
		sql.append("		2 goodType,");
		sql.append("		tmi.draftUrl,");
		sql.append("		tmi.draftDesc,");
		sql.append("		tmi.userId,");
		sql.append("		tmi.userName,");
		sql.append("		tmi.createTime,");
		sql.append("		tmi.crewId,");
		sql.append("		'' remark,");
		sql.append("		'' stock,");
		sql.append("		tvmm.viewId");
		sql.append("	FROM");
		sql.append("		tab_makeup_info tmi");
		sql.append("	LEFT JOIN tab_view_makeup_map tvmm ON tvmm.makeupId = tmi.makeupId ");
		sql.append("	group by tmi.makeupName ");
		sql.append(" UNION ");
		sql.append("	SELECT ");
		sql.append("		tci.clothesName goodsName,");
		sql.append("		3 goodType,");
		sql.append("		tci.draftUrl,");
		sql.append("		tci.draftDesc,");
		sql.append("		tci.userId,");
		sql.append("		tci.userName,");
		sql.append("		tci.createTime,");
		sql.append("		tci.crewId,");
		sql.append("		'' remark,");
		sql.append("		'' stock,");
		sql.append("		tvcm.viewId");
		sql.append("	FROM");
		sql.append("		tab_clothes_info tci");
		sql.append("	LEFT JOIN tab_view_clothes_map tvcm ON tvcm.clothesId = tci.clothesId");
		sql.append("	group by tci.clothesName ");
		
		return this.query(sql.toString(), null, null);
	}
	
	/**
	 * 查询当前剧组的所有的道具信息
	 * @param crewId
	 * @return
	 */
	public List<GoodsInfoModel> queryCrewPropInfo(String crewId){
		String sql = " select * from " + GoodsInfoModel.TABLE_NAME +" where crewId = ? AND goodsType in (0,1)";
		return this.query(sql, new Object[] {crewId}, GoodsInfoModel.class, null);
	}
	
	/**
	 * 根据场景id查询出道具信息列表
	 * @param viewId
	 * @return
	 */
	public List<GoodsInfoModel> queryGoodsInfoByviewid(String viewId){
		StringBuffer sql = new StringBuffer();
		sql.append("	SELECT tgi.*");
		sql.append("	FROM tab_goods_info tgi");
		sql.append("	LEFT JOIN tab_view_goods_map tvgm ON tvgm.goodsId = tgi.id ");
		sql.append("	WHERE 	tvgm.viewId = ?");
		return this.query(sql.toString(), new Object[] {viewId}, GoodsInfoModel.class, null);
	}
	
	/**
	 * 根据多个id查询道具
	 * 包括查询场景ID信息
	 */
	public List<Map<String, Object>> queryManyByViews(String viewIds){
		if (!viewIds.contains("'")) {
			viewIds = "'" + viewIds.replace(",", "','") + "'";
		}
		String sql = " select tvpm.viewId, tpi.* from tab_goods_info tpi,tab_view_goods_map tvpm where tpi.id=tvpm.goodsId and tvpm.viewId in ("+viewIds+") and"
				+ " tpi.goodsType in (0,1)";
		
		
		return this.query(sql, null, null);
	}
	
	/**
	 * 查询出当前剧组需要的物品信息
	 * @param crewId
	 * @param goodsType
	 * @return
	 */
	public List<Map<String, Object>> queryGoodsAndViewByCrewId(String crewId, Integer goodsType){
		String sql = " select tvpm.viewId, tpi.* from tab_goods_info tpi LEFT JOIN  tab_view_goods_map tvpm ON tpi.id = tvpm.goodsId where tpi.crewId = ? and tpi.goodsType = ? ORDER BY CONVERT(goodsName USING gbk)";
		return this.query(sql, new Object[] {crewId, goodsType}, null);
	}
	
	/**
	 * 根据场景id删除物品与场景的关联关系
	 * @param viewId
	 */
	public void deleteViewGoodsMapByViewId(String viewId) {
		String sql = "DELETE FROM " + ViewGoodsInfoMap.TABLE_NAME + " WHERE viewId = ?";
		this.getJdbcTemplate().update(sql, viewId);
	}
	
	/**
	 * @Description  根据场景id批量删除场景 物品对照关系
	 * @param viewIdList
	 */
	public void deleteBatchByViewId(List<String> viewIdList){
		if(viewIdList!=null&&viewIdList.size()>0){
			List<Object[]> args = new ArrayList<Object[]>();
			for(String viewId :viewIdList){
				if(StringUtils.isNotBlank(viewId)){
					args.add(new Object[]{viewId});
				}
			}
			String sql = "delete from "+ ViewGoodsInfoMap.TABLE_NAME +" where viewId = ?";
			this.getJdbcTemplate().batchUpdate(sql, args);
		}
	}
	
	/**
	 * 查询出当前剧组中的道具信息
	 * @param crewId
	 * @return
	 */
	public List<GoodsInfoModel> queryGoodsInfoByCrewId(String crewId){
		String sql = " SELECT * FROM tab_goods_info WHERE crewId = ? AND goodsType in(0,1) ORDER BY CONVERT(goodsName USING gbk)";
		return this.query(sql, new Object[] {crewId}, GoodsInfoModel.class, null);
	}
	
	/**
	 * 查询出当前剧组中的道具信息
	 * @param crewId
	 * @return
	 */
	public List<GoodsInfoModel> queryManyByCrewIdAndTypeAndName(String crewId, Integer goodsType, String name){
		List<Object> params = new ArrayList<Object>();
		params.add(crewId);
		params.add(crewId);
		params.add(goodsType);
		String sql = " SELECT tgi.*,count(distinct tvgm.viewId) num FROM tab_goods_info tgi " 
				+ " left join tab_view_goods_map tvgm on tvgm.goodsId=tgi.id and tvgm.crewId=?  " 
				+ " WHERE tgi.crewId = ? AND tgi.goodsType =?";
		if(StringUtils.isNotBlank(name)) {
			sql += " and tgi.goodsName like ? ";
			params.add("%" + name + "%");
		}
		sql += " group by tgi.id ORDER BY num desc, CONVERT(goodsName USING gbk)";
		return this.query(sql, params.toArray(), GoodsInfoModel.class, null);
	}
	
	/**
	 * 根据场景ID删除场景和物品的关联关系
	 * 多个场景ID用,隔开
	 */
	public void deleteManyByViewIds(String viewIds) {
		viewIds = "'" + viewIds.replace(",", "','") + "'";
		
		String sql = "delete from " + ViewGoodsInfoMap.TABLE_NAME + " where viewId in (" + viewIds + ")";
		
		this.getJdbcTemplate().update(sql);
	}
	
	/**
	 * 查询通告单下指定类型的道具信息
	 * @param crewId
	 * @param noticeId
	 * @param propType
	 * @return
	 */
	public List<Map<String, Object>> queryNoticePropList(String crewId, String noticeId, Integer propType) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	DISTINCT tgi.* ");
		sql.append(" FROM ");
		sql.append(" 	tab_goods_info tgi, ");
		sql.append(" 	tab_view_goods_map tvpm, ");
		sql.append(" 	tab_view_notice_map tvnm ");
		sql.append(" WHERE ");
		sql.append(" 	tgi.id = tvpm.goodsId ");
		if (propType != null) {
			sql.append(" AND tgi.goodsType = " + propType);
		}
		sql.append(" AND tgi.crewId = ? ");
		sql.append(" AND tvpm.viewId = tvnm.viewId ");
		sql.append(" AND tvpm.crewId = ? ");
		sql.append(" AND tvnm.noticeId = ? ");
		sql.append(" AND tvnm.crewId = ? ");

		return this.query(sql.toString(), new Object[]{crewId, crewId, noticeId, crewId}, null);
	}
	
	/**
	 * 根据多个id查询服装，化妆信息
	 * 包括查询场景ID信息
	 */
	public List<Map<String, Object>> queryMakeupAndClothesByViews(String viewIds, Integer goodsType){
		String sql = "";
		if (goodsType == GoodsType.Clothes.getValue()) { //服装
			sql = " select tvpm.viewId, tpi.* from tab_goods_info tpi,tab_view_goods_map tvpm where tpi.id=tvpm.goodsId and tvpm.viewId in ("+viewIds+") and"
					+ " tpi.goodsType in (3)";
		}else if (goodsType == GoodsType.Makeup.getValue()) {
			sql = " select tvpm.viewId, tpi.* from tab_goods_info tpi,tab_view_goods_map tvpm where tpi.id=tvpm.goodsId and tvpm.viewId in ("+viewIds+") and"
					+ " tpi.goodsType in (2)";
		}
		
		return this.query(sql, null, null);
	}
}
