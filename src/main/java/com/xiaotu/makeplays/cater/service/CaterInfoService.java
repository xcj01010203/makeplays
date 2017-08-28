package com.xiaotu.makeplays.cater.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.cater.dao.CaterInfoDao;
import com.xiaotu.makeplays.cater.dao.CaterMoneyInfoDao;
import com.xiaotu.makeplays.cater.model.CaterInfoModel;
import com.xiaotu.makeplays.cater.model.CaterMoneyInfoModel;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 餐饮详细信息操作的service
 * @author wanrenyi 2017年2月20日下午3:07:55
 */
@Service
public class CaterInfoService {

	@Autowired
	private CaterInfoDao caterInfoDao;
	
	@Autowired
	private CaterMoneyInfoDao caterMoneyInfoDao;
	
	/**
	 * 根据多个条件查询餐饮信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<CaterInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.caterInfoDao.queryManyByMutiCondition(conditionMap, page);
	}
	
	
	/**
	 * 保存或修改餐饮详细信息
	 * @param caterId
	 * @param caterDate
	 * @param budget
	 * @param peopleCount
	 * @param caterCount
	 * @param caterTypeId
	 * @param caterMoney
	 * @param perCapita
	 * @param remark
	 * @param crewId
	 * @param typeId
	 * @throws Exception 
	 */
	public void saveCaterInfo(CaterInfoModel caterInfo, String caterMoneyStr) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String caterId = "";
		String crewId = caterInfo.getCrewId();
		
		//根据日期判断是否已经保存过当天的餐饮信息
		List<Map<String,Object>> list = this.caterInfoDao.queryCaterInfoByCaterDate(caterInfo.getCaterDate(), caterInfo.getCrewId());
		if (list != null && list.size() > 0) {
			Map<String, Object> map = list.get(0);
			if (null != map) {
				String oldCaterId = (String) map.get("caterId");
				if (StringUtils.isNotBlank(caterInfo.getCaterId())) {
					if (!oldCaterId.equals(caterInfo.getCaterId())) {
						throw new IllegalArgumentException(sdf.format(caterInfo.getCaterDate())+" 的餐饮信息已经保存，请不要重复添加");
					}
				}else {
					throw new IllegalArgumentException(sdf.format(caterInfo.getCaterDate())+" 的餐饮信息已经保存，请不要重复添加");
				}
			}
		}
		
		//保存餐饮信息
		if (StringUtils.isBlank(caterInfo.getCaterId())) {
			//新增
			caterId = UUIDUtils.getId();
			caterInfo.setCaterId(caterId);
			
			this.caterInfoDao.add(caterInfo);
		}else {
			//修改
			caterId = caterInfo.getCaterId();
			this.caterInfoDao.updateWithNull(caterInfo, "caterId");
			
			//根据餐饮的id删除餐饮金额的详细信息
			//this.caterMoneyInfoDao.deleteByCaterId(caterId);
		}
		
		//保存餐饮的金额信息数据
		List<CaterMoneyInfoModel> modelList = new ArrayList<CaterMoneyInfoModel>(); //所有数据
		List<CaterMoneyInfoModel> addMoneyList = new ArrayList<CaterMoneyInfoModel>(); //新增shuju
		List<CaterMoneyInfoModel> updateMoneyList = new ArrayList<CaterMoneyInfoModel>(); //更新数据
		if (StringUtils.isNotBlank(caterMoneyStr)) {
			
			String[] caterTrArr=null;
			caterTrArr = caterMoneyStr.split("##");
			//前端页面将##移至最后一个参数后面 或者
			//前端页面在拼写参数时如果有新增的用餐地点和用餐时间，需要在最后的参数后拼接 @@ 字符串，用来分割参数
			/*if(caterMoneyStr.indexOf("@@")!=-1) {
				caterMoneyStr=caterMoneyStr.replaceAll("##", "");
				caterTrArr = caterMoneyStr.split("@@");
			}else {
				//参数增加@@打开
				//if(caterMoneyStr.lastIndexOf("##")!=caterMoneyStr.length()-2) {
				//	caterMoneyStr=caterMoneyStr.replaceAll("##", "");
				//}
				//对餐饮的详细信息就行分割
				caterTrArr = caterMoneyStr.split("##");
			}*/
			
			int i =1;
			for (String caterTrData : caterTrArr) {
				CaterMoneyInfoModel caterMoneyInfo = new CaterMoneyInfoModel();
				//设置餐饮id
				caterMoneyInfo.setCaterId(caterId);
				//取出每一行的数据
				String[] caterInfoArr = caterTrData.split(",");
				
				if (caterInfoArr.length<4) {
					throw new IllegalArgumentException("第"+ i +" 行就餐金额为空，请填写就餐金额");
				}
				//餐别名称
				String caterType = caterInfoArr[0];
				if (StringUtils.isBlank(caterType)) {
					throw new IllegalArgumentException("第 " + i + " 行餐饮类别为空，请完善信息");
				}
				//人数
				String peopleCountStr = caterInfoArr[1];
				//份数
				String caterCountStr = caterInfoArr[2];
				//金额
				String caterMoney = caterInfoArr[3];
				if (StringUtils.isBlank(caterMoney)) {
					throw new IllegalArgumentException("第"+ i +" 行就餐金额为空，请填写就餐金额");
				}
				caterMoneyInfo.setCaterMoney(Double.parseDouble(caterMoney));
				
				//人均
				String perCapita = "";
				if (caterInfoArr.length>4) {
					perCapita = caterInfoArr[4];
				}
				
				//备注
				String remark = "";
				if (caterInfoArr.length>5) {
					remark = caterInfoArr[5];
				}
				
				String caterMoneyId = "";
				if (caterInfoArr.length>6) {
					caterMoneyId = caterInfoArr[6];
				}
				
				//供餐时间
				String caterTypeTime = "";
				if(caterInfoArr.length>7) {
					caterTypeTime = caterInfoArr[7];
					/*if (StringUtils.isBlank(caterTypeTime)) {
						throw new IllegalArgumentException("第 " + i + " 行用餐时间为空，请完善信息");
					}*/
				}
				//用餐地点
				String caterAddr = "";
				if(caterInfoArr.length>8) {
					caterAddr = caterInfoArr[8];
					/*if (StringUtils.isBlank(caterAddr)) {
						throw new IllegalArgumentException("第 " + i + " 行用餐地点为空，请完善信息");
					}*/
				}
				caterMoneyInfo.setCaterType(caterType);
				caterMoneyInfo.setCaterTimeType(caterTypeTime);
				caterMoneyInfo.setCaterAddr(caterAddr);
				
				if (StringUtils.isNotBlank(peopleCountStr)) {
					caterMoneyInfo.setPeopleCount(Integer.parseInt(peopleCountStr));
				}
				
				if (StringUtils.isNotBlank(caterCountStr)) {
					caterMoneyInfo.setCaterCount(Integer.parseInt(caterCountStr));
				}
				
				if (StringUtils.isNotBlank(perCapita)) {
					caterMoneyInfo.setPerCapita(Double.parseDouble(perCapita));
				}
				
				caterMoneyInfo.setRemark(remark);
				caterMoneyInfo.setCrewId(crewId);
				
				if (StringUtils.isNotBlank(caterMoneyId)) {
					caterMoneyInfo.setCaterMoneyId(caterMoneyId);
					updateMoneyList.add(caterMoneyInfo);
				}else {
					//设置餐饮金额Id
					caterMoneyInfo.setCaterMoneyId(UUIDUtils.getId());
					addMoneyList.add(caterMoneyInfo);
				}
				modelList.add(caterMoneyInfo);
				i++;
			}
		}
		
		//对餐饮的金额list进行判断是否有重复数据
		if (modelList != null && modelList.size()>0) {
			for (int i = 0; i < modelList.size(); i++) {
				for (int j = modelList.size()-1; j > i; j--) {
					CaterMoneyInfoModel firstModel = modelList.get(i);
					CaterMoneyInfoModel secondModel = modelList.get(j);
					if (firstModel.getCaterType().equals(secondModel.getCaterType())) {
						throw new IllegalArgumentException("餐饮类别不能重复，请将重复数据进行合并");
					}
				}
			}
		}
		
		//将当前数据同数据库中的数据进行比对，不能出现重复数据
		checkCaterMoneyInfo(modelList);
		
		//将新的餐饮金额的详细信息保存
		this.caterMoneyInfoDao.addBatch(addMoneyList, CaterMoneyInfoModel.class);
		
		//更新已有的餐饮信息
		this.caterMoneyInfoDao.updateBatch(updateMoneyList, "caterMoneyId", CaterMoneyInfoModel.class);
	}
	
	/**
	 * 根据餐饮id删除餐饮
	 * @param caterId
	 * @throws Exception 
	 */
	public void deleteCaterInfoById(String caterId) throws Exception {
		//删除餐饮信息
		this.caterInfoDao.deleteOne(caterId, "caterId", CaterInfoModel.TABLE_NAME);
		
		//删除餐饮金额
		this.caterMoneyInfoDao.deleteByCaterId(caterId);
	}
	
	
	/**
	 * 根据餐饮日期和剧组的id查询出当天餐饮的详细信息包括餐饮的金额信息
	 * @param caterDateStr
	 * @param crewId
	 * @return
	 */
	public Map<String, Object> queryCaterInfoByCaterId(String caterId){
		Map<String, Object> data = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Double budget = 0.0;
		//根据餐饮id先查询出餐饮的信息
		List<Map<String, Object>> caterInfoList = this.caterInfoDao.queryCaterInfoByCaterId(caterId);
		for (Map<String, Object> map : caterInfoList) {
			//格式化时间
			Date caterDate = (Date) map.get("caterDate");
			map.remove("caterDate");
			map.put("caterDate", sdf.format(caterDate));
			data.put("caterInfo", map);
			//取出本日预算
			budget = Double.parseDouble(map.get("budget")+"");
		}
		
		//根据餐饮id查询餐饮金额的详细信息
		List<Map<String, Object>> caterMoneyList = this.caterMoneyInfoDao.queryCaterMoneyByCaterId(caterId);
		data.put("caterMoneyList", caterMoneyList);
		
		//查询出餐饮的统计信息
		List<Map<String,Object>> summerylist = this.caterMoneyInfoDao.querySummeryData(caterId);
		for (Map<String, Object> summeryMap : summerylist) {
			String totalPeopleCount = summeryMap.get("totalPeopleCount")+"";
			Double totalMoney = 0.0;
			String totalMoneyStr = summeryMap.get("totalMoney")+"";
			if (summeryMap.get("totalMoney") != null) {
				totalMoney = Double.parseDouble(totalMoneyStr);
			}
			
			double leftMoney = BigDecimalUtil.subtract(budget, totalMoney);
			
			data.put("totalPeopleMoney", totalPeopleCount);
			data.put("totalMoney", totalMoney);
			data.put("leftMoney", leftMoney);
		}
		return data;
	}
	
	/**
	 * 查询餐饮列表数据
	 * @param page
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryCaterInfoList(Page page, String crewId){
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//确保double类型数据只保留两位小数
		DecimalFormat df = new DecimalFormat("0.00");
		
		/*//当前页数
		int pageNo = page.getPageNo();
		//每页显示条数
		int pageSize = page.getPagesize();
		//初始天数
		int countDay = (pageNo*pageSize)+1;*/
		int countDay = 1;
		//初始累计费用
		Double firstAccumulateMoney = 0.0;
		//初始节约、超支费用
		Double firstLeftMoney = 0.0;
		//查询数据
		List<Map<String,Object>> list = this.caterInfoDao.queryCaterInfoList(page, crewId);
		for (Map<String, Object> caterMap : list) {
			Map<String, Object> dataMap = new HashMap<String, Object>();
			//餐饮id
			dataMap.put("caterId", caterMap.get("caterId"));
			//第几天
			dataMap.put("days", countDay);
			
			//日期
			Date caterDateStr = (Date)caterMap.get("caterDate");
			if (caterDateStr == null) {
				dataMap.put("caterDate", caterDateStr);
			}else {
				dataMap.put("caterDate", sdf.format(caterDateStr));
			}
			//当日费用
			Double dayTotalMoney = 0.0;
			if (caterMap.get("caterMoney") != null) {
				dayTotalMoney = Double.parseDouble(caterMap.get("caterMoney")+"");
			}
			dataMap.put("dayTotalMoney", df.format(dayTotalMoney));
			
			//当日预算
			Double budget = 0.0;
			if (caterMap.get("budget") != null) {
				budget = Double.parseDouble(caterMap.get("budget")+"");
			}
			dataMap.put("budget", df.format(budget));
			
			//人数
			int peopleCount = 0;
			if (caterMap.get("totalPeople") != null) {
				peopleCount = ((BigDecimal)caterMap.get("totalPeople")).intValue();
			}
			dataMap.put("peopleCount", peopleCount);
			
			//当日节约超支
			Double dayLeftMoney = budget - dayTotalMoney;
			dataMap.put("dayLeftMoney", df.format(dayLeftMoney));
			
			//人均
			double dayPerCapita = 0.0;
			if (peopleCount != 0) {
				dayPerCapita = dayTotalMoney/peopleCount;
			}
			dataMap.put("dayPerCapita", df.format(dayPerCapita));
			
			//累计费用
			Double accumulateMoney = 0.0;
			//累计超支、节约费用
			Double accumulateLeftMoney = 0.0;
			if (countDay == 1) { //第一天
				accumulateMoney = dayTotalMoney;
				firstAccumulateMoney = accumulateMoney;
				
				accumulateLeftMoney = dayLeftMoney;
				firstLeftMoney = accumulateLeftMoney;
			}else {
				accumulateMoney = dayTotalMoney + firstAccumulateMoney;
				firstAccumulateMoney = accumulateMoney;
				
				accumulateLeftMoney = dayLeftMoney + firstLeftMoney;
				firstLeftMoney = accumulateLeftMoney;
			}
			
			dataMap.put("accumulateMoney", df.format(accumulateMoney));
			dataMap.put("accumulateLeftMoney", df.format(accumulateLeftMoney));
			data.add(dataMap);
			
			countDay ++;
		}
		
		return data;
	}
	
	/**
	 * 查询餐饮列表数据
	 * @return
	 */
	public List<Map<String, Object>> queryCaterInfoListNoFormat(Page page, String crewId){
		return this.caterInfoDao.queryCaterInfoList(page, crewId);
	}
	
	/**
	 * 根据餐饮id查询出餐饮的信息
	 * @param caterId
	 * @return
	 * @throws Exception 
	 */
	public CaterInfoModel queryById(String caterId) throws Exception{
		return this.caterInfoDao.queryById(caterId);
	}
	
	/**
	 * 新增一条记录
	 * @param caterInfo
	 * @throws Exception 
	 */
	public void addOne(CaterInfoModel caterInfo) throws Exception {
		this.caterInfoDao.add(caterInfo);
	}
	
	/**
	 * 更新一条记录
	 * @param caterInfo
	 * @throws Exception 
	 */
	public void updateOne(CaterInfoModel caterInfo) throws Exception {
		this.caterInfoDao.update(caterInfo, "caterId");
	}
	
	/**
	 * 根据餐饮金额信息id删除餐饮金额
	 * @param caterMoneyId
	 * @return 
	 * @throws Exception 
	 */
	public int deleteById(String caterMoneyId) throws Exception {
		return this.caterMoneyInfoDao.deleteOne(caterMoneyId, "caterMoneyId", CaterMoneyInfoModel.TABLE_NAME);
	}
	
	/**
	 * 根据餐别类型、餐饮id判断是否是重复数据
	 * @param caterId
	 * @param caterType
	 */
	private void checkCaterMoneyInfo(List<CaterMoneyInfoModel> saveList) {
		if (saveList !=null && saveList.size()>0) {
			for (CaterMoneyInfoModel saveModel : saveList) {
				String saveCaterMoneyId = saveModel.getCaterMoneyId()==null?"":saveModel.getCaterMoneyId();
				Map<String, Object> conditionMap = new HashMap<String, Object>();
				conditionMap.put("caterId", saveModel.getCaterId());
				conditionMap.put("caterType", saveModel.getCaterType());
				List<CaterMoneyInfoModel> modelList = this.caterMoneyInfoDao.queryManyByMutiCondition(conditionMap, null);
				if (modelList != null && modelList.size() >0) {
					for (CaterMoneyInfoModel dataModel : modelList) {
						String dataCaterMoneyId = dataModel.getCaterMoneyId()==null?"":dataModel.getCaterMoneyId();
						if (!saveCaterMoneyId.equals(dataCaterMoneyId)) {
							throw new IllegalArgumentException("餐饮类别不能重复，请将重复数据进行合并");
						}
					}
				}
			}
		}
	}
}
