package com.xiaotu.makeplays.cater.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.cater.dao.CaterMoneyInfoDao;
import com.xiaotu.makeplays.cater.model.CaterMoneyInfoModel;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 餐饮金额信息
 * @author xuchangjian 2017-3-9上午11:05:12
 */
@Service
public class CaterMoneyInfoService {

	@Autowired
	private CaterMoneyInfoDao caterMoneyInfoDao;
	
	/**
	 * 根据餐饮id查询餐饮的金额信息
	 * @param caterId
	 * @return 除了返回餐饮金额信息，还会返回餐饮类别
	 */
	public List<Map<String, Object>> queryCaterMoneyByCaterId(String caterId){
		return this.caterMoneyInfoDao.queryCaterMoneyByCaterId(caterId);
	}
	
	/**
	 * 增加一条记录
	 * @param caterMoneyInfo
	 * @throws Exception 
	 */
	public void addOne(CaterMoneyInfoModel caterMoneyInfo) throws Exception {
		this.caterMoneyInfoDao.add(caterMoneyInfo);
	}
	
	/**
	 * 更新一条记录
	 * @param caterMoneyInfo
	 * @throws Exception
	 */
	public void updateOne(CaterMoneyInfoModel caterMoneyInfo) throws Exception {
		this.caterMoneyInfoDao.update(caterMoneyInfo, "caterMoneyId");
	}
	
	/**
	 * 根据ID查询数据
	 * @param caterId
	 * @return
	 * @throws Exception 
	 */
	public CaterMoneyInfoModel queryById(String caterMoneyId) throws Exception {
		return this.caterMoneyInfoDao.queryById(caterMoneyId);
	}
	
	/**
	 * 保存餐饮明细
	 * @param userId
	 * @param crewId
	 * @param caterId	餐饮ID
	 * @param caterMoneyId	餐饮明细ID
	 * @param caterType	餐别
	 * @param peopleCount	人数
	 * @param caterCount	份数
	 * @param caterMoney	金额
	 * @param perCapita	人均
	 * @param remark	备注
	 * @return
	 * @throws Exception 
	 */
	public void saveCaterDetailInfo(String crewId, String caterId, 
		String caterMoneyId, String caterType, Integer peopleCount, 
		Integer caterCount, Double caterMoney, Double perCapita, 
		String remark) throws Exception {
		
		//校验是否已经存在该餐别的餐饮记录
		Map<String, Object> caterMoneyConditionMap = new HashMap<String, Object>();
		caterMoneyConditionMap.put("caterId", caterId);
		caterMoneyConditionMap.put("crewId", crewId);
		caterMoneyConditionMap.put("caterType", caterType);
		List<CaterMoneyInfoModel> existCaterTypeMoneyInfoList = this.caterMoneyInfoDao.queryManyByMutiCondition(caterMoneyConditionMap, null);
		if (existCaterTypeMoneyInfoList != null && existCaterTypeMoneyInfoList.size() > 0) {
			if (!existCaterTypeMoneyInfoList.get(0).getCaterMoneyId().equals(caterMoneyId)) {
				throw new IllegalArgumentException("已存在“" + caterType + "”的餐饮记录，请勿重复添加");
			}
		}
		
		
		CaterMoneyInfoModel caterMoneyInfo = new CaterMoneyInfoModel();
		if (!StringUtils.isBlank(caterMoneyId)) {
			caterMoneyInfo = this.queryById(caterMoneyId);
		} else {
			caterMoneyInfo.setCaterMoneyId(UUIDUtils.getId());
		}
		
		caterMoneyInfo.setPeopleCount(peopleCount);
		caterMoneyInfo.setCaterCount(caterCount);
		caterMoneyInfo.setCaterType(caterType);
		caterMoneyInfo.setCaterMoney(caterMoney);
		caterMoneyInfo.setPerCapita(perCapita);
		caterMoneyInfo.setRemark(remark);
		caterMoneyInfo.setCrewId(crewId);
		caterMoneyInfo.setCaterId(caterId);
		
		if (StringUtils.isBlank(caterMoneyId)) {
			this.caterMoneyInfoDao.add(caterMoneyInfo);
		} else {
			this.caterMoneyInfoDao.update(caterMoneyInfo, "caterMoneyId");
		}
	}
	
	/**
	 * 根据ID删除数据
	 * @param caterMoneyId
	 */
	public void deleteById(String caterMoneyId) {
		this.caterMoneyInfoDao.deleteById(caterMoneyId);
	}
	
	/**
	 * 获取当前剧组的所有的餐饮类型
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryCaterTypeByCrewId(String crewId) {
		List<Map<String, Object>> list = this.caterMoneyInfoDao.queryCaterTypeList(crewId);
		Collections.sort(list, new Comparator<Map<String, Object>>() {

			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				String caterType = (String) o2.get("caterType");
				if (caterType.equals("其它")) {
					return -1;
				}else {
					return 0;
				}
			}
		});
		
		return list;
	}
}
