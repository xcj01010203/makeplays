package com.xiaotu.makeplays.goods.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.goods.dao.GoodsInfoDao;
import com.xiaotu.makeplays.goods.dao.ViewGoodsMapDao;
import com.xiaotu.makeplays.goods.model.GoodsInfoModel;
import com.xiaotu.makeplays.goods.model.ViewGoodsInfoMap;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 物品管理的service
 * @author wanrenyi 2017年4月24日上午11:34:11
 */
@Service
public class GoodsInfoService {

	@Autowired
	private GoodsInfoDao goodsInfoDao;
	
	@Autowired
	private ViewGoodsMapDao viewGoodsMapDao;
	
	/**
	 * 多条件查询物品列表
	 * @param condition
	 * @return
	 */
	public List<GoodsInfoModel> queryGoodsListByCondition(Map<String, Object> condition){
		return this.goodsInfoDao.queryGoodsByCondition(condition);
	}
	
	/**
	 * 根据条件查询道具信息列表
	 * @param crewId
	 * @param goodsName
	 * @param type
	 * @param start
	 * @param end
	 * @return
	 */
	public List<Map<String, Object>> queryGoodsListByView(String crewId,String goodsName,Integer type, String start,String end, Integer sortType){
		return this.goodsInfoDao.queryGoodsInfoByView(crewId, goodsName, type, start, end, sortType);
	}
	
	/**
	 * 更新合并后的道具信息
	 * @param idArray
	 * @param propName
	 * @param type
	 * @param remark
	 * @param crewId
	 * @param userId
	 * @param userName
	 * @throws Exception
	 */
	public void updateCombinePropsInfo(String[] idArray,String propName,Integer type, String remark,String crewId,String userId,String userName) throws Exception{
		//根据道具id获取总库存量
		Integer stock = goodsInfoDao.queryStockCount(idArray);
		//删除旧道具信息
		goodsInfoDao.delGoodsInfoByIds(idArray);
		String pid = UUIDUtils.getId();
		//保存新物品信息
		GoodsInfoModel model = new GoodsInfoModel();
		model.setCrewId(crewId);
		model.setCreateTime(new java.util.Date());
		model.setId(pid);
		model.setGoodsName(propName);
		model.setGoodsType(type);
		model.setRemark(remark);
		model.setStock(stock);
		model.setUserId(userId);
		model.setUserName(userName);
		goodsInfoDao.add(model);
		//修改场景道具对照关系信息
		goodsInfoDao.updateViewGoodsMapInfo(idArray, pid);
	}
	
	/**
	 * @Description  批量修改物品类型
	 * @param ids 道具id
	 * @param type 道具类型
	 */
	public void updateGoodsType(String[] ids,Integer type){
		goodsInfoDao.updateGoodsType(ids, type);
	}
	
	/**
	 * @Description 批量删除物品信息
	 * @param ids
	 * @throws Exception 
	 */
	public void delGoodsInfo(String[] ids) throws Exception{
		//删除物品信息
		goodsInfoDao.delGoodsInfo(ids);
		//删除物品场景对照信息
		goodsInfoDao.deleteMany(ids, "goodsId", "tab_view_goods_map");
	}
	
	/**
	 * @Description 保存物品信息
	 * @param propName  道具名称
	 * @param type  类型
	 * @param remark  备注
	 * @param stock  库存信息
	 * @param crewId 剧组id
	 * @param userId  用户id
	 * @param userName  用户名称
	 * @throws Exception 
	 */
	public void saveGoodsInfo(String id,String goodsName,Integer type, String remark,Integer stock,String crewId,String userId,String userName) throws Exception{
		
		GoodsInfoModel model = new GoodsInfoModel();
		model.setCrewId(crewId);
		
		model.setGoodsName(goodsName);
		model.setGoodsType(type);
		model.setRemark(remark);
		
		model.setStock(stock);
		model.setUserId(userId);
		model.setUserName(userName);
		if(StringUtils.isBlank(id)){
			model.setId(UUIDUtils.getId());
			model.setCreateTime(new Date());
			goodsInfoDao.add(model);
		}else{
			model.setId(id);
			//根据id查询出原始数据
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("id", id);
			List<GoodsInfoModel> list = this.goodsInfoDao.queryGoodsByCondition(condition);
			if (null != list && list.size()>0) {
				GoodsInfoModel tempMap = list.get(0);
				model.setCreateTime(tempMap.getCreateTime());
				goodsInfoDao.updateWithNull(model, "id");
			}
		}
	}
	
	/**
	 * 查询物品的使用情况
	 * @param crewId
	 * @param id
	 * @return
	 */
	public List<Map<String, Object>> queryGoodsUseInfo(String crewId, String id){
		return this.goodsInfoDao.queryGoodsUseInfo(id, crewId);
	}
	
	/**
	 * 同步服化道信息到新的表中
	 * @throws Exception
	 */
	public void ansycData() throws Exception {
		//查询数据
		List<Map<String,Object>> list = this.goodsInfoDao.queryAnsycData();
		
		int count = 1;
		for (Map<String, Object> map : list) {
			//定义需要保存的对象
			GoodsInfoModel model = new GoodsInfoModel();
			String id = UUIDUtils.getId();
			model.setId(id);
			Date createTime = (Date)map.get("createTime");
			model.setCreateTime(new Date(createTime.getTime()+count*1000));
			model.setCrewId((String)map.get("crewId"));
			model.setDraftDesc((String)map.get("draftDesc"));
			model.setDraftUrl((String)map.get("draftUrl"));
			model.setGoodsName((String)map.get("goodsName"));
			Long goodType = (Long)map.get("goodType");
			model.setGoodsType(goodType.intValue());
			model.setRemark((String)map.get("remark"));
			int stock = 0;
			String stockStr = (String)map.get("stock");
			if (StringUtils.isNotBlank(stockStr)) {
				stock = Integer.parseInt(stockStr);
			}
			model.setStock(stock);
			model.setUserId((String)map.get("userId"));
			model.setUserName((String)map.get("userName"));
			
			//保存数据
			goodsInfoDao.add(model);
			
			//保存关联关系
			ViewGoodsInfoMap mapModel = new ViewGoodsInfoMap();
			mapModel.setId(UUIDUtils.getId());
			mapModel.setCrewId((String)map.get("crewId"));
			mapModel.setGoodsId(id);
			mapModel.setViewId((String)map.get("viewId"));
			viewGoodsMapDao.add(mapModel);
			
			count ++;
		}
	}
	
	/**
	 * 根据场景id查询出物品信息id
	 * @param viewId
	 * @return
	 */
	public List<GoodsInfoModel> queryGoodsInfoByViewid(String viewId){
		return this.goodsInfoDao.queryGoodsInfoByviewid(viewId);
	}
	
	/**
	 * 根据场景id的字符串查询出，场景中的道具信息
	 * @return
	 */
	public List<Map<String, Object>> queryPropInfoByViewIds(String viewIds){
		return this.goodsInfoDao.queryManyByViews(viewIds);
	}
}
