package com.xiaotu.makeplays.finance.service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.finance.controller.dto.FinanceSubjectDto;
import com.xiaotu.makeplays.finance.dao.ContractActorDao;
import com.xiaotu.makeplays.finance.dao.ContractProduceDao;
import com.xiaotu.makeplays.finance.dao.ContractWorkerDao;
import com.xiaotu.makeplays.finance.dao.FinanSubjCurrencyMapDao;
import com.xiaotu.makeplays.finance.dao.FinanceSettingDao;
import com.xiaotu.makeplays.finance.dao.FinanceSubjectDao;
import com.xiaotu.makeplays.finance.dao.LoanInfoDao;
import com.xiaotu.makeplays.finance.dao.PaymentFinanSubjMapDao;
import com.xiaotu.makeplays.finance.dao.PaymentInfoDao;
import com.xiaotu.makeplays.finance.model.ContractActorModel;
import com.xiaotu.makeplays.finance.model.ContractProduceModel;
import com.xiaotu.makeplays.finance.model.ContractWorkerModel;
import com.xiaotu.makeplays.finance.model.CurrencyInfoModel;
import com.xiaotu.makeplays.finance.model.FinanSubjCurrencyMapModel;
import com.xiaotu.makeplays.finance.model.FinanceSettingModel;
import com.xiaotu.makeplays.finance.model.FinanceSubjectModel;
import com.xiaotu.makeplays.finance.model.LoanInfoModel;
import com.xiaotu.makeplays.finance.model.PaymentFinanSubjMapModel;
import com.xiaotu.makeplays.finance.model.PaymentInfoModel;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 财务科目
 * @author xuchangjian 2016-7-28上午10:19:43
 */
@Service
public class FinanceSubjectService {
	
	//缓存的财务科目列表，财务科目名称格式：父科目-子科目
	List<FinanceSubjectDto> cachedSubjectDtoList = new ArrayList<FinanceSubjectDto>();

	@Autowired
	private FinanceSubjectDao financeSubjectDao;
	
	@Autowired
	private FinanSubjCurrencyMapDao finanSubjCurrencyMapDao;
	
	@Autowired
	private CurrencyInfoService currencyInfoService;
	
	@Autowired
	private AccoFinacSubjMapService accoFinacSubjMapService;
	
	@Autowired
	private FinanceAccountGroupService financeAccountGroupService;
	
	@Autowired
	private ContractActorDao contractActorDao;
	
	@Autowired
	private ContractWorkerDao contractWorkerDao;
	@Autowired
	private ContractProduceDao contractProduceDao;
	@Autowired
	private PaymentInfoDao paymentInfoDao;
	@Autowired
	private LoanInfoDao loanInfoDao;
	
	@Autowired
	private PaymentFinanSubjMapDao paymentFinanSubjMapDap;
	
	@Autowired
	private FinanceSettingDao financeSettingDao;
	
	/**
	 * @Description 根据剧组id查询是否有财务数据关联到财务科目
	 * @param crewId
	 * @return
	 */
	public Map<String, Object> queryForReadImport(String crewId){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("hasDataNum", 0);
		List<ContractActorModel> list = contractActorDao.queryByCrewId(crewId);
		if(list!=null&&list.size()>0){
			for(ContractActorModel contractActorModel :list){
				String subjectId = contractActorModel.getFinanceSubjId();
				if(StringUtils.isNotBlank(subjectId)){
					map.put("title", "演员合同");
					return map;
				}
			}
			
			
			
			
		}
		List<ContractWorkerModel> workList = contractWorkerDao.queryByCrewId(crewId);
		if(workList!=null&&workList.size()>0){
			
			for(ContractWorkerModel contractWorkerModel :workList){
				String subjectId = contractWorkerModel.getFinanceSubjId();
				if(StringUtils.isNotBlank(subjectId)){
					map.put("title", "职员合同");
					return map;
				}
			}
			
		}
		
		List<ContractProduceModel> produceList = contractProduceDao.queryByCrewId(crewId);
		if(produceList!=null&&produceList.size()>0){
			for(ContractProduceModel contractProduceModel :produceList){
				String subjectId = contractProduceModel.getFinanceSubjId();
				if(StringUtils.isNotBlank(subjectId)){
					map.put("title", "制作合同");
					return map;
				}
			}
			
			
		}
		
		List<PaymentInfoModel> payList = paymentInfoDao.queryByCrewId(crewId);
		if(payList!=null&&payList.size()>0){
			map.put("title", "付款单");
			return map;
		}
		
		List<LoanInfoModel> loanList = loanInfoDao.queryByCrewId(crewId);
		if(loanList!=null&&loanList.size()>0){
			map.put("title", "借款单");
			return map;
		}
		return map;
	}
	
	
	/**
	 * 保存excel导入预算信息
	 * @throws Exception 
	 */
	public void saveImportExcelForBudgetInfo( Map<String, Object> map,final String crewId) throws Exception{
		//根据剧组id获取币种信息
		List<CurrencyInfoModel> currencyList = currencyInfoService.queryCurrencyInfoByCrewId(crewId);
		if(currencyList == null || currencyList.size() == 0){
			currencyList = new ArrayList<CurrencyInfoModel>();
			currencyList.add(this.currencyInfoService.initFirstCurrency(crewId));
		}
		
		Map<Object,Object> currencyInfoMap = new HashMap<Object, Object>();
		for(CurrencyInfoModel currencyInfo : currencyList){
			String id = currencyInfo.getId();
			String code = currencyInfo.getCode();
			currencyInfoMap.put(code, id);
		}
		
		//删除原有的财务科目
		financeSubjectDao.deleteByCrewId(crewId);
		//删除原有的预算信息
		this.finanSubjCurrencyMapDao.deleteByCrewId(crewId);
		//清空财务科目ID
		//清空合同与财务科目关联关系
		this.contractWorkerDao.deleteFinanceSubjectByCrewId(crewId);
		this.contractActorDao.deleteFinanceSubjectByCrewId(crewId);
		this.contractProduceDao.deleteFinanceSubjectByCrewId(crewId);
		//清空付款单与财务科目关联关系
		this.paymentFinanSubjMapDap.deleteFinanceSubjectByCrewId(crewId);
		//清空借款单与财务科目关联关系
		this.loanInfoDao.deleteFinanceSubjectByCrewId(crewId);
		//清空财务设置付款单缴税设置信息
		FinanceSettingModel financeSetting = this.financeSettingDao.queryByCrewId(crewId);
    	if (financeSetting != null) {
    		financeSetting.setTaxFinanSubjId(null);
    		financeSetting.setTaxRate(null);
    		this.financeSettingDao.updateWithNull(financeSetting, "setId");
    	}
		
		List<Map<String, Object>> insertDataList = new ArrayList<Map<String,Object>>();
		//添加财务科目信息--
		Set<String> keys = map.keySet();
		Iterator<String> iterator = keys.iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			final List<Map<String, Object>> list = (List<Map<String, Object>>)map.get(key);
			//添加财务科目信息----start----
			String sql = "insert into tab_finance_subject (id,name,parentid,crewid,createtime,remark,sequence,level) values(?,?,?,?,?,?,?,?)";
			int[] i = financeSubjectDao.getJdbcTemplate().batchUpdate(sql,new BatchPreparedStatementSetter() {
				@Override
				public int getBatchSize() {
					return list.size();
				}
				@Override
				public void setValues(java.sql.PreparedStatement ps, int i) throws SQLException {
					ps.setString(1, String.valueOf(list.get(i).get("id")));   
                    ps.setString(2, String.valueOf(list.get(i).get("title")));
                    ps.setString(3, String.valueOf(list.get(i).get("parentId")));   
                    ps.setString(4, crewId);   
                    ps.setTimestamp(5, new Timestamp(System.currentTimeMillis())); 
                    ps.setString(6, String.valueOf(list.get(i).get("备注")));
                    ps.setString(7, String.valueOf(list.get(i).get("sequence")));
                    ps.setString(8, String.valueOf(list.get(i).get("level")));
				}
			});
			//添加财务科目信息----end----
			
			
			//保存预算数据 ------start--------
			//excel中币种种类
			Set<String> currencyInfoCodeSet = new HashSet<String>();
			//excel中币种名称集合
			List<String> currencyCodeList =null;
			//excel中币种对应的值集合
			List<String> currencyValueList = null;
			Map<String,Object> insertDataMap = null;
			int rowNumber = 2;//excel表格数据行号
			for(Map<String, Object> maps:list){//excel每条数据，不包括标题
				insertDataMap = new HashMap<String, Object>();
				boolean isFather = Boolean.valueOf(maps.get("isFather").toString());//如果有子节点不插入数据
				++rowNumber;
				if(isFather){
					continue;
				}
				currencyCodeList= new ArrayList<String>();
				currencyValueList= new ArrayList<String>();
				Iterator<String> innerIt = maps.keySet().iterator();
				while(innerIt.hasNext()){
					String innerKey = innerIt.next();
					//利用正则获取标题上的币种编码
					Pattern pattern = Pattern.compile(Constants.REGEX_EXCEL_TITLE);
					Matcher matcher = pattern.matcher(innerKey);
					if(matcher.find()){
						String matchStr = matcher.group(1);//excel标题栏中币种code
						currencyInfoCodeSet.add(matchStr);
						//处理一个财务科目有多个币种的情况
						String value = maps.get(innerKey)!=null?maps.get(innerKey).toString():"";
						if(StringUtils.isNotBlank(value)){
							currencyValueList.add(value);
							currencyCodeList.add(matchStr);
						}
					}
					
					//准备数据添加到数据库
					if("id".equals(innerKey)){
						insertDataMap.put("financeSubjId", maps.get(innerKey));
					}else if("单位".equals(innerKey)){
						String unit = maps.get(innerKey)!=null?maps.get(innerKey).toString():"";
						if(StringUtils.isBlank(unit)){
							throw new IllegalArgumentException("第"+rowNumber+"行数据异常。请填写单位（长度不超过10个字），财务科目名称："+maps.get("title"));
						}
						
						insertDataMap.put("unitType", unit);
					}else if("单价".equals(innerKey)){
						double perPrice = 0.00;
						if (maps.get(innerKey) != null) {
							try {
								perPrice = Double.parseDouble(maps.get(innerKey).toString().replaceAll(",", ""));
							} catch(NumberFormatException pe) {
								throw new IllegalArgumentException("第" + rowNumber + "行数据异常，单价必须为数字");
							}
						}
						insertDataMap.put("perPrice", perPrice);
					}else if("数量".equals(innerKey)){
						double amount = 0.0;
						try {
							amount = Double.parseDouble(maps.get(innerKey).toString());
						} catch(NumberFormatException pe) {
							throw new IllegalArgumentException("第" + rowNumber + "行数据异常，数量必须为数字");
						}
						insertDataMap.put("amount", amount);
					}else if(currencyCodeList.size() == 0 && innerKey.contains("预算") && !innerKey.equals("预算比例") && currencyList.size() == 1) {
						String currentcyCode = currencyList.get(0).getCode();
						currencyInfoCodeSet.add(currentcyCode);
						currencyCodeList.add(currentcyCode);
						String value = maps.get(innerKey)!=null?maps.get(innerKey).toString():"";
						currencyValueList.add(value);
					}
				}
				if (currencyCodeList.size() == 0) {
					//单币种不提示添加币种
					throw new IllegalArgumentException("请在预算金额列添加币种信息");
				}
				
				if(currencyValueList.size()==1){
					insertDataMap.put("mapid", UUIDUtils.getId());
					insertDataMap.put("crewId", crewId);
					
					Double money = 0.00;
					try {
						money = Double.parseDouble(currencyValueList.get(0).toString().replaceAll(",", ""));
					} catch (NumberFormatException pe) {
						throw new IllegalArgumentException("第" + rowNumber + "行数据异常，预算金额必须为数字");
					}
					
					insertDataMap.put("money", currencyValueList);
					insertDataMap.put("currencyId", currencyCodeList);
					insertDataList.add(insertDataMap);
				}else if(currencyValueList.size()==0){
					throw new IllegalArgumentException("第"+rowNumber+"行数据异常。预算值为空，财务科目名称："+maps.get("title"));
				}else{
					throw new IllegalArgumentException("第"+rowNumber+"行数据异常。一个财务科目不能对应多个币种，财务科目名称："+maps.get("title"));
				}
			}
		}
		
	
		
		
		
		
		//判断是否有数据需要插入数据库
		List<Map<String, Object>> inserDataMapList = new ArrayList<Map<String,Object>>();//最终要插入数据库的数据
		if(insertDataList!=null&&insertDataList.size()>0){
			for(Map<String, Object> tMap:insertDataList){
				List<String> currencyId = tMap.get("currencyId")!=null?(List)tMap.get("currencyId"):null;
				List<String> money = tMap.get("money")!=null?(List)tMap.get("money"):null;
				if(currencyId!=null&&currencyId.size()>0){
					
					
					for(int i=0;i<currencyId.size();i++){
						Map<String, Object> inmap = new HashMap<String, Object>();
						inmap.putAll(tMap);
						String mapid = UUIDUtils.getId();
						inmap.put("mapid", mapid);
						//判断excel中的币种信息是否存在数据库中
						String currencyCode = currencyId.get(i);
						Object currencyIdFromTab = currencyInfoMap.get(currencyCode);
						if(currencyIdFromTab!=null&&StringUtils.isNotBlank(currencyIdFromTab.toString())){
							inmap.put("currencyId", currencyIdFromTab.toString());
							inmap.put("money", money.get(i)!=null?money.get(i).toString().replaceAll(",", ""):0);
						}else{
							throw new IllegalArgumentException("excel表格中的货币编码：【"+currencyCode+"】在该剧组中不存在，如仍需导入请先在：【费用管理-财务设置-币种设置 】下添加对应的币种信息");
						}
						inserDataMapList.add(inmap);
					}
				}
			}
		}
		
		if(inserDataMapList!=null&&inserDataMapList.size()>0){
			//批量插入数据
			String sql = "insert into tab_finanSubj_currency_map(mapid,financeSubjId,currencyId,money,crewId,amount,perPrice,unitType) values(?,?,?,?,?,?,?,?)";
			final List<Map<String, Object>> list = inserDataMapList;
			int[] i  = financeSubjectDao.getJdbcTemplate().batchUpdate(sql,new BatchPreparedStatementSetter() {
					public int getBatchSize() {
						return list.size();
					}
					@Override
					public void setValues(java.sql.PreparedStatement ps, int i) throws SQLException {
					  try {
						  Object moneyO = list.get(i).get("money");
						  Object amountO = list.get(i).get("amount");
						  Object perPriceO = list.get(i).get("perPrice");
						  String money = (moneyO!=null&&StringUtils.isNotBlank(moneyO.toString()))?moneyO.toString():"0";
						  String amount = (amountO!=null&&StringUtils.isNotBlank(amountO.toString()))?amountO.toString():"0";
						  String perPrice = (perPriceO!=null&&StringUtils.isNotBlank(perPriceO.toString()))?perPriceO.toString():"0";
						  
						  ps.setString(1, String.valueOf(list.get(i).get("mapid")));   
			              ps.setString(2, String.valueOf(list.get(i).get("financeSubjId")));
			              ps.setString(3, String.valueOf(list.get(i).get("currencyId")));   
			              ps.setString(4,money);  
			              ps.setString(5, String.valueOf(list.get(i).get("crewId"))); 
			              ps.setString(6, amount); 
			              ps.setString(7, perPrice); 
			              ps.setString(8, String.valueOf(list.get(i).get("unitType")));
						} catch (Exception e) {
							throw new IllegalArgumentException("保存预算信息异常：第"+(i+1)
									+"条数据异常：金额为："+list.get(i).get("money").toString()+""
									+ "数量为："+list.get(i).get("amount").toString()
									+"单价为："+list.get(i).get("perPrice").toString()
									+"单位为："+list.get(i).get("unitType").toString()+"异常信息："+e.getMessage());
						}
						 
					}
				});
		}
		//保存预算数据 ------end--------
	}
	
	
	
	
	/**
	 * 根据多个条件查询财务科目信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<FinanceSubjectModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.financeSubjectDao.queryManyByMutiCondition(conditionMap, page);
	}
	
	/**
	 * 根据剧组ID查询剧组财务科目
	 * @param crewId
	 * @return
	 */
	public List<FinanceSubjectModel> queryByCrewId(String crewId) {
		return this.financeSubjectDao.queryByCrewId(crewId);
	}
	
	/**
	 * 添加一条记录
	 * @param financeSubject
	 * @throws Exception 
	 */
	public void addOne(FinanceSubjectModel financeSubject) throws Exception {
		this.financeSubjectDao.add(financeSubject);
	}
	
	/**
	 * 修改一条记录
	 * @param financeSubject
	 * @throws Exception
	 */
	public void updateOne(FinanceSubjectModel financeSubject) throws Exception {
		this.financeSubjectDao.update(financeSubject, "id");
	}
	
	/**
	 * 批量添加
	 * @param subjectList
	 * @throws Exception 
	 */
	public void addMany(List<FinanceSubjectModel> subjectList) throws Exception {
		for (FinanceSubjectModel subject : subjectList) {
			this.addOne(subject);
		}
	}
	
	/**
	 * 查询财务科目
	 * @param crewId
	 * @return	不仅返回财务科目信息，还会返回对应币种的预算信息
	 */
	public List<Map<String, Object>> queryWithBudgetInfo(String crewId) {
		return this.financeSubjectDao.queryWithBudgetInfo(crewId);
	}
	
	/**
	 * 查询财务科目（带有结算信息）
	 * @param crewId
	 * @param paymentStartDate	付款单开始日期
	 * @param paymentEndDate	付款单结束日期
	 * @return
	 */
	public List<Map<String, Object>> queryWithSettleInfo(String crewId, String paymentStartDate, String paymentEndDate) {
		return this.financeSubjectDao.queryWithSettleInfo(crewId, paymentStartDate, paymentEndDate);
	}
	
	/**
	 * 保存财务科目信息
	 * @param request
	 * @param financeSubjId	财务科目ID
	 * @param financeSubjName	财务科目名称
	 * @param reamrk	备注
	 * @param amount	数量
	 * @param unitType	单位
	 * @param currencyId	币种ID
	 * @param perPrice	单价
	 * @param money	金额
	 * @throws Exception 
	 */
	public void saveBudgetInfo(String crewId, String financeSubjParentId, String financeSubjId, String financeSubjName, 
			Integer level, String remark, Double amount, String unitType, 
			String currencyId, Double perPrice, Double money) throws Exception {
		
		//财务科目信息
		FinanceSubjectModel financeSubject = new FinanceSubjectModel();
		
		boolean isFinanSubjAdd = false;
		if (!StringUtils.isBlank(financeSubjId)) {
			financeSubject = this.financeSubjectDao.queryById(financeSubjId);
		} else {
			financeSubject.setId(UUIDUtils.getId());
			financeSubject.setCreateTime(new Date());
			
			isFinanSubjAdd = true;
		}
		financeSubject.setCrewId(crewId);
		financeSubject.setName(financeSubjName);
		financeSubject.setLevel(level);
		financeSubject.setRemark(remark);
		
		
		if (StringUtils.isBlank(financeSubjParentId)) {
			financeSubject.setParentId("0");
		} else {
			//把父科目和货币的关联关系删掉
			this.finanSubjCurrencyMapDao.deleteByFinanSubjId(financeSubjParentId);
			
			financeSubject.setParentId(financeSubjParentId);
		}
		
		if (isFinanSubjAdd) {
			//更新同级科目的sequence
			this.financeSubjectDao.addOneSubjectSequence(financeSubject.getParentId());
			financeSubject.setSequence(0);
			
			this.financeSubjectDao.add(financeSubject);
		} else {
			this.financeSubjectDao.updateWithNull(financeSubject, "id");
		}
		
		//财务科目和货币的关联关系
		if (amount != null && amount != 0) {
			if (unitType == "") {
				throw new IllegalArgumentException("请填写单位");
			}
			if (perPrice == null) {
				throw new IllegalArgumentException("请填写单价");
			}
			if (money == null) {
				throw new IllegalArgumentException("请填写总金额");
			}
			if (StringUtils.isBlank(currencyId)) {
				throw new IllegalArgumentException("请选择币种");
			}
			
			FinanSubjCurrencyMapModel finanSubjCurrencyMap = this.finanSubjCurrencyMapDao.queryByFinanSubjId(financeSubject.getId());
			
			boolean isFinanSubjCurrencyMapAdd = false;
			if (finanSubjCurrencyMap == null) {
				finanSubjCurrencyMap = new FinanSubjCurrencyMapModel();
				finanSubjCurrencyMap.setMapId(UUIDUtils.getId());
				
				isFinanSubjCurrencyMapAdd = true;
			}
			finanSubjCurrencyMap.setCrewId(crewId);
			finanSubjCurrencyMap.setAmount(amount);
			finanSubjCurrencyMap.setUnitType(unitType);
			finanSubjCurrencyMap.setCurrencyId(currencyId);
			finanSubjCurrencyMap.setPerPrice(perPrice);
			finanSubjCurrencyMap.setMoney(money);
			finanSubjCurrencyMap.setFinanceSubjId(financeSubject.getId());
			
			if (isFinanSubjCurrencyMapAdd) {
				this.finanSubjCurrencyMapDao.add(finanSubjCurrencyMap);
			} else {
				this.finanSubjCurrencyMapDao.updateWithNull(finanSubjCurrencyMap, "mapId");
			}
			
		}
	}
	
	/**
	 * 查询科目的子科目列表
	 * @param crewId
	 * @param parentId
	 * @return
	 */
	public List<FinanceSubjectModel> queryByParentId(String crewId, String parentId) {
		return this.financeSubjectDao.queryByParentId(crewId, parentId);
	}
	
	/**
	 * 删除一个财务科目
	 * @param crewId
	 * @param financeSubjId
	 * @throws Exception 
	 */
	public void deleteOneFinanceSubj(String crewId, String financeSubjId) throws Exception {
		//删除财务科目
		this.financeSubjectDao.deleteOne(financeSubjId, "id", FinanceSubjectModel.TABLE_NAME);
		
		//删除预算信息
		FinanSubjCurrencyMapModel finanSubjCurrencyMap = this.finanSubjCurrencyMapDao.queryByFinanSubjId(financeSubjId);
		if (finanSubjCurrencyMap != null) {
			this.finanSubjCurrencyMapDao.deleteOne(finanSubjCurrencyMap.getMapId(), "mapId", FinanSubjCurrencyMapModel.TABLE_NAME);
		}
		
		//删除和会计科目的关联
		this.accoFinacSubjMapService.deleteByFinaSubId(financeSubjId);
	}
	
	/**
	 * 删除所有财务科目
	 * @param crewId
	 * @param financeSubjId
	 * @throws Exception 
	 */
	public void deleteAllFinanceSubj(String crewId) throws Exception {
		this.financeSubjectDao.deleteByCrewId(crewId);
		this.finanSubjCurrencyMapDao.deleteByCrewId(crewId);
	}
	
	/**
	 * 更新权限表顺序
	 */
	public void updateSubjectSequence(String crewId, String ids){
		String idArray[] = ids.split(",");
		for (int i = 0; i < idArray.length; i++) {
			this.financeSubjectDao.updateSubjectSequence(idArray[i], i);
		}
	}
	
	/**
	 * 把财务科目移动到上一级
	 * @param financeSubjId
	 * @param financeSubjParentId
	 * @param level
	 * @throws Exception 
	 */
	public void upSubjectLevel(String financeSubjId, String financeSubjParentId, Integer level) throws Exception {
		FinanceSubjectModel myFianceSubject = this.financeSubjectDao.queryById(financeSubjId);
		FinanceSubjectModel parentFinanceSubject = this.financeSubjectDao.queryById(financeSubjParentId);
		
		//更新同级科目的sequence
		this.financeSubjectDao.addOneSubjectSequence(parentFinanceSubject.getParentId());
		
		//设置当前财务科目的parentId和level、sequence
		myFianceSubject.setParentId(parentFinanceSubject.getParentId());
		myFianceSubject.setLevel(--level);
		myFianceSubject.setSequence(0);
		
		this.financeSubjectDao.update(myFianceSubject, "id");
	}
	
	/**
	 * 根据ID查询财务科目
	 * @param subjectId
	 * @return
	 * @throws Exception 
	 */
	public FinanceSubjectModel queryById(String subjectId) throws Exception {
		return this.financeSubjectDao.queryById(subjectId);
	}
	
	/**
	 * 查询剧组下的财务科目信息
	 * 该方法还会查询出财务科目对应的会计科目
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryByCrewIdWithAccSubj(String crewId) {
		return this.financeSubjectDao.queryByCrewIdWithAccSubj(crewId);
	}
	
	/**
	 * 查询对于指定会计科目来说不可用的财务科目
	 * 也就是被剧组中其他财务科目已占用的财务科目
	 * @param crewId
	 * @param accountId
	 * @return
	 */
	public List<FinanceSubjectModel> queryUnusedSubjByAccId(String crewId, String accountId) {
		return this.financeSubjectDao.queryUnusedSubjByAccId(crewId, accountId);
	}
	
	/**
	 * 查询会计科目下的财务科目
	 * @param crewId
	 * @param acccount
	 * @return
	 */
	public List<FinanceSubjectModel> queryByAccountId(String crewId, String acccountId) {
		return this.financeSubjectDao.queryByAccountId(crewId, acccountId);
	}
	
	/**
	 * 查询剧组中尚未加入到会计科目下的财务科目信息
	 * @param crewId
	 * @return
	 */
	public List<FinanceSubjectModel> queryUnSelectedBudgSubj(String crewId) {
		return this.financeSubjectDao.queryUnSelectedBudgSubj(crewId);
	}
	
	/**
	 * 刷新缓存中的财务科目列表
	 * @param crewId
	 */
	public List<FinanceSubjectDto> refreshCachedSubjectList (String crewId) {
		Map<String, Object> conditionMap = new HashMap<String, Object>();
    	conditionMap.put("crewId", crewId);
    	
		List<FinanceSubjectModel> subjectList = this.queryManyByMutiCondition(conditionMap, null);
		this.cachedSubjectDtoList = this.loopFinanceSubject(new ArrayList<FinanceSubjectDto>(), subjectList);
		
		return this.cachedSubjectDtoList;
	}
	
	/**
	 * 根据财务科目ID获取财务科目名称
	 * 名称中带有父科目的名称，格式：父科目-父科目-子科目
	 * @param financeSubjId
	 * @return
	 */
	public String getFinanceSubjName(String financeSubjId) {
        String financeSubjName = "";
        for (FinanceSubjectDto subject : this.cachedSubjectDtoList) {
        	if (subject.getId().equals(financeSubjId)) {
        		financeSubjName = subject.getName();
        		break;
        	}
        }
        
        return financeSubjName;
	}
	
	/**
	 * 根据财务科目名称获取财务科目ID
	 * @param finanSubjName 财务科目名称
	 * @return 如果有多个匹配，则返回多个ID
	 */
	public List<FinanceSubjectDto> getFinanceSubjByName(String finanSubjName) {
		List<FinanceSubjectDto> financeSubjList = new ArrayList<FinanceSubjectDto>();
        for (FinanceSubjectDto subject : this.cachedSubjectDtoList) {
        	if (subject.isHasChildren()) {
        		continue;
        	}
        	String myName = subject.getName();
        	String[] myLeafNameArray = myName.split("-");
        	String myLeafName = myLeafNameArray[myLeafNameArray.length - 1];
        	
        	if (myName.equals(finanSubjName) || myLeafName.equals(finanSubjName)) {
        		financeSubjList.add(subject);
        	}
        }
        
        return financeSubjList;
	}
	
	/**
	 * 遍历财务科目数据
	 * 把所有的财务科目名称封装成：父科目名称-子科目名称  的格式
	 * @param subjectDtoList
	 * @param financeSubjectList
	 * @return
	 */
	private List<FinanceSubjectDto> loopFinanceSubject(List<FinanceSubjectDto> subjectDtoList, List<FinanceSubjectModel> financeSubjectList) {
		List<FinanceSubjectDto> mySubjectDtoList = subjectDtoList;
		
		List<FinanceSubjectModel> topSubjectList = new ArrayList<FinanceSubjectModel>();
		List<FinanceSubjectModel> leafSubjectList = new ArrayList<FinanceSubjectModel>();	//叶子节点
		for (FinanceSubjectModel fsubject : financeSubjectList) {
			String fid = fsubject.getId();
			String fparentId = fsubject.getParentId();

			boolean isParent = false;
			boolean isChild = false;
			for (FinanceSubjectModel csubject : financeSubjectList) {
				String cid = csubject.getId();
				String cparentId = csubject.getParentId();
				
				if (fid.equals(cparentId)) {
					isParent = true;
				}
				if (fparentId.equals(cid)) {
					isChild = true;
				}
				if (isParent && isChild) {
					break;
				}
			}
			
			if (!isChild) {
				topSubjectList.add(fsubject);
			}
			if (!isParent) {
				leafSubjectList.add(fsubject);
			}
		}
		
		for (FinanceSubjectDto fsubject : subjectDtoList) {
			String fname = fsubject.getName();
			
			for (FinanceSubjectModel csubject : topSubjectList) {
				String cname = csubject.getName();
				
				if (fsubject.getId().equals(csubject.getParentId())) {
					cname = fname + "-" + cname;
					csubject.setName(cname);
				}
			}
		}
		
		for (FinanceSubjectModel tsubject : topSubjectList) {
			FinanceSubjectDto subjectDto = new FinanceSubjectDto();
			subjectDto.setId(tsubject.getId());
			subjectDto.setName(tsubject.getName());
			subjectDto.setParentId(tsubject.getParentId());
			subjectDto.setHasChildren(true);
			
			for (FinanceSubjectModel lsubject : leafSubjectList) {
				if (lsubject.getId().equals(tsubject.getId())) {
					subjectDto.setHasChildren(false);
					break;
				}
			}
			
			mySubjectDtoList.add(subjectDto);
		}
		
		if (financeSubjectList.size() > 0) {
			financeSubjectList.removeAll(topSubjectList);
			mySubjectDtoList = this.loopFinanceSubject(mySubjectDtoList, financeSubjectList);
		}
		
		return mySubjectDtoList;
	}
	
	/**
	 * 根据币种ID查询对应的财务科目
	 * @param crewId
	 * @param currencyId
	 * @return
	 */
	public List<FinanceSubjectModel> queryByCurrencyId(String crewId, String currencyId) {
		return this.financeSubjectDao.queryByCurrencyId(crewId, currencyId);
	}


	/**
	 * 查询总费用进度
	 * @param crewId
	 * @return
	 */
	public Map<String, Object> queryTotalFinance(String crewId) {
		return this.financeSubjectDao.queryTotalFinance(crewId);
	}

	/**
	 * 查询财务科目预算支出概况
	 * @param crewId 剧组ID
	 * @param statType 统计类型，1：预算科目  2：自定义科目
	 * @param parentId 预算科目父ID
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryBudgetPayedInfo(String crewId,
			String statType, String parentId) throws Exception{
		List<Map<String, Object>> list = this.financeSubjectDao.queryBudgetPayedInfo(crewId);
		if(list == null || list.size() == 0) {
			return null;
		}
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		if(statType.equals("1")) {//预算科目
			for(Map<String, Object> one : list) {
				if((parentId.equals("0") && (one.get("level") + "").equals("1")) 
						|| (one.get("parentId") + "").equals(parentId)) {//获取要显示的科目
					//获取科目总预算支出
					result.add(this.getSubjectTotalBudgetPayed(list, one));
				}
			}
		} else if(statType.equals("2")) {//自定义科目组
			result = this.financeSubjectDao.querySelfBudgetPayedInfo(crewId);
		}
		return result;
	}
	
	/**
	 * 获取科目总预算支出
	 * @param list
	 * @param one
	 * @return
	 */
	private Map<String, Object> getSubjectTotalBudgetPayed(List<Map<String, Object>> list, Map<String, Object> one) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> childList = new ArrayList<Map<String,Object>>();
		loopBudgetPayedInfo(list, one.get("id") + "", childList);
		if(childList.size() == 0) {
			childList.add(one);
		}
		resultMap.putAll(one);
		if(childList == null || childList.size() == 0) {
			resultMap.put("budgetMoney", one.get("budgetMoney"));
			resultMap.put("payedMoney", one.get("payedMoney"));
		} else {
			double budgetTotal = 0;
			double payedTotal = 0;
			for(Map<String, Object> oneChild : childList) {
				if(com.xiaotu.makeplays.utils.StringUtils.isNotBlank(oneChild.get("budgetMoney") + "")) {
					budgetTotal = BigDecimalUtil.add(budgetTotal, 
							(Double) oneChild.get("budgetMoney"));
				}
				if(com.xiaotu.makeplays.utils.StringUtils.isNotBlank(oneChild.get("payedMoney") + "")) {
					payedTotal = BigDecimalUtil.add(payedTotal, 
							(Double) oneChild.get("payedMoney"));
				}
			}
			resultMap.put("budgetMoney", budgetTotal);
			resultMap.put("payedMoney", payedTotal);
		}
		return resultMap;
	}
	
	/**
	 * 递归遍历获取所有叶子节点
	 * @param list
	 * @param parentId
	 * @return
	 */
	private List<Map<String, Object>> loopBudgetPayedInfo(
			List<Map<String, Object>> list, String parentId,
			List<Map<String, Object>> childList) {
		List<Map<String, Object>> childList1 = new ArrayList<Map<String,Object>>();
		for(Map<String, Object> one : list) {
			if ((StringUtils.isBlank(parentId) && (one.get("level") + "").equals("1"))
					|| (StringUtils.isNotBlank(parentId) && (one.get("parentId") + "").equals(parentId))) {
				List<Map<String, Object>> childList2 = loopBudgetPayedInfo(
						list, one.get("id") + "", childList);
				if (childList2 == null || childList2.size() == 0) {
					childList1.add(one);
					childList.add(one);
				}
			}
		}
		return childList1;
	}
	
	/**
	 * 查询财务科目日支出和累计日支出
	 * @param crewId
	 * @param parentId
	 * @return
	 */
	public List<Map<String, Object>> queryDayPayedInfo(String crewId, String parentId) {
		List<Map<String, Object>> list = this.financeSubjectDao.queryDayPayedInfo(crewId);
		if(list == null || list.size() == 0) {
			return null;
		}
		//所有该科目下的叶子节点
		List<Map<String, Object>> realList = new ArrayList<Map<String,Object>>();
		//自己本身的支出
		List<Map<String, Object>> selfObj = new ArrayList<Map<String,Object>>();
		for(Map<String, Object> one : list) {
			if((parentId.equals("0") && (one.get("level") + "").equals("1")) 
					|| (one.get("parentId") + "").equals(parentId)) {//获取要显示的科目
				List<Map<String, Object>> childList = new ArrayList<Map<String,Object>>();
				loopBudgetPayedInfo(list, one.get("id") + "", childList);
				if(childList.size() == 0) {
					childList.add(one);
				}
				realList.addAll(childList);
			}
			if(one.get("id").equals(parentId)) {
				selfObj.add(one);
			}
		}
		//最后一级科目
		if(realList.size() == 0) {
			realList.addAll(selfObj);
		}
		//日支出结果集
		List<Map<String, Object>> dateList = new ArrayList<Map<String,Object>>();
		//用于判断日期是否存在，累计同日支出
		Map<String, Map<String, Object>> dateMap = new HashMap<String, Map<String,Object>>();
		for(Map<String, Object> one : realList) {
			String paymentDate = one.get("paymentDate") + "";
			if(com.xiaotu.makeplays.utils.StringUtils.isNotBlank(paymentDate)) {
				//存放日期支出数据
				Map<String, Object> map = new HashMap<String, Object>();
				if(!dateMap.containsKey(paymentDate)) {
					map.put("paymentDate", paymentDate);
					map.put("dayPayedMoney", one.get("payedMoney"));
					dateMap.put(paymentDate, map);
					dateList.add(map);
				} else {
					map = dateMap.get(paymentDate);
					if(com.xiaotu.makeplays.utils.StringUtils.isNotBlank(one.get("payedMoney") + "")) {
						if(com.xiaotu.makeplays.utils.StringUtils.isNotBlank(map.get("dayPayedMoney") + "")) {
							map.put("dayPayedMoney", BigDecimalUtil.add(
									(Double) map.get("dayPayedMoney"),
									(Double) one.get("payedMoney")));
						} else {
							map.put("dayPayedMoney", one.get("payedMoney"));
						}
					}
				}
			}
		}
		//按日期排序
		Collections.sort(dateList, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1,
					Map<String, Object> o2) {
				String paymentDate1 = o1.get("paymentDate") + "";
				String paymentDate2 = o2.get("paymentDate") + "";
				return paymentDate1.compareTo(paymentDate2);
			}
		});
		//计算累计日支出
		for(int i = 0; i < dateList.size(); i++) {
			Map<String, Object> map = dateList.get(i);
			if(i == 0) {
				map.put("totalDayPayedMoney", map.get("dayPayedMoney"));
			} else {
				Map<String, Object> lastMap = dateList.get(i-1);
				if(com.xiaotu.makeplays.utils.StringUtils.isNotBlank(lastMap.get("totalDayPayedMoney") + "")) {
					if(com.xiaotu.makeplays.utils.StringUtils.isNotBlank(map.get("dayPayedMoney") + "")) {
						map.put("totalDayPayedMoney", BigDecimalUtil.add(
												(Double) lastMap.get("totalDayPayedMoney"),
												(Double) map.get("dayPayedMoney")));
					} else {
						map.put("totalDayPayedMoney", lastMap.get("totalDayPayedMoney"));
					}
				} else {
					map.put("totalDayPayedMoney", map.get("dayPayedMoney"));
				}
			}
		}
		return dateList;
	}
	
	/**
	 * 查询费用结算中预算资金的总支出明细列表
	 * @param crewId
	 * @param finaceSubjId
	 * @return
	 */
	public List<Map<String, Object>> queryFinanceBudgetPaymentList(String crewId, String finaceSubjId){
		return this.financeSubjectDao.queryFinanceSubPaymentList(crewId, finaceSubjId);
	}
}
