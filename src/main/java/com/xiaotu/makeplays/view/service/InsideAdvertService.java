package com.xiaotu.makeplays.view.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.view.dao.InsideAdvertDao;
import com.xiaotu.makeplays.view.model.InsideAdvertModel;

/**
 * 植入广告基本信息
 * @author xuchangjian
 */
@Service
public class InsideAdvertService {

	@Autowired
	private InsideAdvertDao insideAdvertDao;
	
	/**
	 * 新增一条植入广告
	 * @param insideAdvert
	 * @throws Exception
	 */
	public void addOneAdvert(InsideAdvertModel insideAdvert) throws Exception {
		this.insideAdvertDao.add(insideAdvert);
	}
	
	/**
	 * 根据多个条件查询植入广告信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<InsideAdvertModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.insideAdvertDao.queryManyByMutiCondition(conditionMap, page);
	}
	
	/**
	 * 更新植入广告信息
	 * @param insideAdvert
	 * @throws Exception 
	 */
	public String updateOneAdvert(InsideAdvertModel insideAdvert) throws Exception {
		this.insideAdvertDao.update(insideAdvert, "advertId");
		return insideAdvert.getAdvertId();
	}
	
	/**
	 * 保存植入广告信息
	 * 该方法中加入了判断广告名称在剧组中是否存在的逻辑，如果存在则更新，如果不存在则新增
	 * @param advertName
	 * @param advertDesc
	 * @param crewId
	 * @return		返回植入广告ID
	 * @throws Exception 
	 */
	public String saveAdvertInfo(String advertName, String crewId) throws Exception {
		String insideAdvertId = "";
		
		InsideAdvertModel insideAdvert = null;
		
		//判断数据库中该剧组下是否已存在相同名称的广告，如果不存在就新增，如果存在就更新
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("advertName", advertName);
		conditionMap.put("crewId", crewId);
		List<InsideAdvertModel> insideAdvertList = this.queryManyByMutiCondition(conditionMap, null);
		if (insideAdvertList != null && insideAdvertList.size() > 0) {
			insideAdvert = insideAdvertList.get(0);
		} else {
			insideAdvert = new InsideAdvertModel();
		}
		
		insideAdvert.setAdvertName(advertName);
		insideAdvert.setCrewId(crewId);
		
		if (insideAdvert.getAdvertId() != null) {
			insideAdvertId = insideAdvert.getAdvertId();
			this.updateOneAdvert(insideAdvert);
		} else {
			insideAdvertId = UUIDUtils.getId();
			insideAdvert.setAdvertId(insideAdvertId);
			this.addOneAdvert(insideAdvert);
		}
		
		return insideAdvertId;
	}
	
	/**
	 * 使用剧组id查询剧组广告信息
	 * @param crewId
	 * @return
	 */
	public List<InsideAdvertModel> queryAdvertInfoByCrewId(String crewId){
		return this.insideAdvertDao.queryAdvertInfoByCrewId(crewId);
	}
	
	/**
	 * 根据场景ID查询植入广告信息
	 * @param viewId 场景ID
	 * @return
	 */
	public List<Map<String, Object>> queryAdvertByViewId(String viewId) {
		return this.insideAdvertDao.queryAdvertByViewId(viewId);
	}
	
	/**
	 * 根据id删除一条广告信息
	 * @param advertId
	 */
	public void deleteAdvertById(String advertId) {
		if (StringUtils.isBlank(advertId)) {
			throw new IllegalArgumentException("请选择要删除的广告!");
		}
		
		this.insideAdvertDao.deleteAdvertById(advertId);
	}
	
	/**
	 * 根据广告id更新当前广告的名称
	 * @param advertName
	 * @param advertId
	 * @throws Exception 
	 */
	public void updateAdvertNameByAdvertId(String advertName, String advertId) throws Exception {
		//根据id获取广告信息
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("advertId", advertId);
		List<InsideAdvertModel> list = this.insideAdvertDao.queryManyByMutiCondition(conditionMap, null);
		if (list == null || list.size()==0) {
			throw new IllegalArgumentException("不存在当前广告信息，请查证后在修改");
		}
		
		InsideAdvertModel model = list.get(0);
		model.setAdvertName(advertName);
		this.insideAdvertDao.updateWithNull(model, "advertId");
	}
}
