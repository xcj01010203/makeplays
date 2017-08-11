package com.xiaotu.makeplays.finance.service;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.finance.controller.dto.FinanceSubjectDto;
import com.xiaotu.makeplays.finance.controller.filter.CollectionInfoFilter;
import com.xiaotu.makeplays.finance.controller.filter.LoanInfoFilter;
import com.xiaotu.makeplays.finance.controller.filter.PaymentInfoFilter;
import com.xiaotu.makeplays.finance.dao.FinancePaymentWayDao;
import com.xiaotu.makeplays.finance.dao.GetCostDao;
import com.xiaotu.makeplays.finance.dao.PaymentInfoDao;
import com.xiaotu.makeplays.finance.model.CollectionInfoModel;
import com.xiaotu.makeplays.finance.model.CurrencyInfoModel;
import com.xiaotu.makeplays.finance.model.FinancePaymentWayModel;
import com.xiaotu.makeplays.finance.model.FinanceSettingModel;
import com.xiaotu.makeplays.finance.model.LoanInfoModel;
import com.xiaotu.makeplays.finance.model.constants.LoanPaymentWay;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.DateUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

@Service
public class GetCostService {
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	@Autowired
	private FinanceSettingService financeSettingService;
	
	@Autowired
	private PaymentInfoService paymentInfoService;
	
	@Autowired
	private LoanInfoService loanInfoService;
	
	@Autowired
	private CollectionInfoService collectionInfoService;
	
	@Autowired
	private CurrencyInfoService currencyInfoService;
	
	@Autowired
	private GetCostDao getCostDao;
	
	
	@Autowired
	private PaymentInfoDao paymentInfoDao;
	
	
	@Autowired
	private FinancePaymentWayDao financePaymentWayDao;
	
	@Autowired
	private FinanceSubjectService financeSubjectService;
	
	@Autowired
	private PaymentLoanMapService paymentLoanMapService;
	
	/**
	 * 根据合同号判断合同类型    
	 *  查询结果：worker:职员合同，actor:演员合同 ，produce:制作合同
	 *  返回结果：'1：职员合同；2：演员合同；3：制作合同' 0：合同号无效,
	 * @param contractNo 合同id
	 * @param crewId  剧组id
	 * @return
	 */
	public Map<String, Object> queryContractInfoForContractType(String crewId,String contractNo,String rowStr){
		Map<String, Object> back = new HashMap<String, Object>();
		int contractType = 0;
		Object contractId = null;
		String tabSuffix = "";
		if(contractNo.startsWith("ZY")){
			tabSuffix = "worker";
		}else if(contractNo.startsWith("YY")){
			tabSuffix = "actor";
		}else if(contractNo.startsWith("ZZ")){
			tabSuffix = "produce";
		}else{
			throw new IllegalArgumentException("第"+rowStr+"行，合同编号【"+contractNo+"】不存在");
		}
		
		List<Map<String, Object>> list = getCostDao.queryContractInfoForContractType(crewId,contractNo,tabSuffix);
		if(list!=null&&list.size()>0){
			Map<String, Object> map = list.get(0);
			String worker = map.get("worker")!=null?map.get("worker").toString():"";
			String actor = map.get("actor")!=null?map.get("actor").toString():"";
			String produce = map.get("produce")!=null?map.get("produce").toString():"";
			if(StringUtils.isNotBlank(worker)){
				contractType = 1; 
				contractId = worker;
			}
			if(StringUtils.isNotBlank(actor)){
				contractType = 2; 
				contractId = actor;
			}
			if(StringUtils.isNotBlank(produce)){
				contractType = 3; 
				contractId = produce;
			}
		}
		back.put("contractType", contractType);
		back.put("contractId", contractId);
		return back;
	}
	/**
	 * 保存excel导入的财务详情数据
	 * 
	 * @param getCostInfoMap 导入的数据
	 * @param FINANCE_MAP  导入的列名
	 * @param crewId  剧组id
	 * 
	 * @throws Exception 
	 */
	public void saveFinanceInfoFromExcel(Map<String , Object> getCostInfoMap, Map<String, String> FINANCE_MAP, String crewId, Boolean isCover){
		try {
			//获取财务科目数据（财务科目名称格式：父科目-子科目）
			List<FinanceSubjectDto> subjectDtoList = this.financeSubjectService.refreshCachedSubjectList(crewId);
			
			//判断当前剧组的财务设置中的票据编号是否是按月分号
			FinanceSettingModel financeSettingModel = financeSettingService.queryByCrewId(crewId);
			//判断是否是按月编号
			Boolean payStatus = financeSettingModel.getPayStatus();
			
			//先将数据拆分开（付款、收款、借款）
			List<Map<String, Object>> payList = new ArrayList<Map<String,Object>>();//付款
			List<Map<String, Object>> collectList = new ArrayList<Map<String,Object>>();//收款
			List<Map<String, Object>> loanList = new ArrayList<Map<String,Object>>();//借款信息
			List<Map<String, Object>> needDealDataList = new ArrayList<Map<String,Object>>();//借款信息
			
			//获取当前剧组货币信息   key:货币code  value:货币id
			Map<String, String> currencyMap  = queryCrewCurrencyCodeIdByCrewId(crewId);
			
			//根据剧组id获取该剧组下的付款方式，只针对付款和收款，借款的付款方式为固定的（详情见：LoanPaymentWay)
			Map<String, String> payOrCollectPaymentWayMap = queryPayOrCollectPaymentWay(crewId);
			
			//保存不存在的付款方式
			List<FinancePaymentWayModel> listPayWay = new ArrayList<FinancePaymentWayModel>();
			Map<String, String> tempMap = new HashMap<String, String>();//临时存储需要添加的付款方式id，名称避免重复添加
			
			//整理excel中的数据  将数据分类为    收款、付款、借款三类数据
			arrangeSaveInfoFromExcel(crewId, getCostInfoMap, FINANCE_MAP, payList, collectList, loanList, needDealDataList, subjectDtoList);
			
			if (needDealDataList.size() > 0) {
				String errorRowNumber = "";
				for (Map<String, Object> needDealData : needDealDataList) {
					int rowNumber = (Integer) needDealData.get("rowNumber");
					errorRowNumber += rowNumber + "行" + ",";
				}
				errorRowNumber = errorRowNumber.substring(0, errorRowNumber.length() - 1);
				throw new IllegalArgumentException("第" + errorRowNumber + "单据的财务科目在系统中存在多个，请改成“父科目-子科目”的完整格式");
			}
			
			//保存收款信息
			//根据 日期，摘要，收付款方，金额判断是否有重复数据     未结算的数据才能支持覆盖
			if(collectList!=null&&collectList.size()>0){
				List<CollectionInfoModel>  collectInfoList = collectionInfoService.queryByCrewId(crewId);
				if(collectInfoList!=null&&collectInfoList.size()>0){
					List<Map<String, Object>> insertList = new ArrayList<Map<String,Object>>();//保存数据
					List<Map<String, Object>> updateList = new ArrayList<Map<String,Object>>();//需要更新的数据
					int columIndex = 3;
						
					for(Map<String, Object> mapExcel :collectList){
						boolean isRepeat = false;
						String paymentDateExcel = mapExcel.get("receiptDate")!=null?mapExcel.get("receiptDate").toString():"";
						String payeeNameExcel = mapExcel.get("aimPersonName")!=null?mapExcel.get("aimPersonName").toString():"";
						String summaryExcel = mapExcel.get("summary")!=null?mapExcel.get("summary").toString():"";
						String moneyExcel = mapExcel.get("collectMoney")!=null?mapExcel.get("collectMoney").toString():"";
						
						//将excel表中的时间格式化为月份
						String excelMonthDate = "";
						if (StringUtils.isNotBlank(paymentDateExcel)) {
							excelMonthDate = paymentDateExcel.substring(0, paymentDateExcel.lastIndexOf("-"));
						}
						//取出用户输入的票据编号
						String receiptNoExcel = mapExcel.get("receiptNo") != null ?mapExcel.get("receiptNo").toString():"";
						//获取货币编号
						moneyExcel.replaceAll(Constants.REGEX_EXCEL, ")");
						String moneyCode = "";
						if (moneyExcel.contains("(")) {
							moneyCode = moneyExcel.substring(moneyExcel.indexOf("(")+1, moneyExcel.indexOf(")"));
						}
						
						if (moneyExcel.contains("(")) {
							moneyExcel = moneyExcel.substring(0, moneyExcel.indexOf("("));
						}
						
						if(StringUtils.isNotBlank(moneyExcel)){
							moneyExcel = String.valueOf(Double.valueOf(moneyExcel.replaceAll(",", "").replaceAll("，", "")));
						}
						mapExcel.put("collectMoney", moneyExcel +"(" + moneyCode +")");
						
						for (CollectionInfoModel colModel :collectInfoList) {
							//收款
							String collectionId = colModel.getCollectionId();//收款id
							String paymentDateDB = colModel.getCollectionDate()!=null?new SimpleDateFormat("yyyy-MM-dd").format(colModel.getCollectionDate()):"";//收款时间
							String payeeNameDB = colModel.getOtherUnit();//收款方
							String summaryDB = colModel.getSummary();//摘要
							String moneyDB = colModel.getMoney()!=0.0?String.valueOf(colModel.getMoney()):"";
							paymentDateExcel = DateUtils.formatToString(paymentDateExcel, columIndex);
							mapExcel.put("receiptDate", paymentDateExcel);
							String monthDBDate = colModel.getCollectionDate()!=null?new SimpleDateFormat("yyyy-MM").format(colModel.getCollectionDate()):"";//收款时间
							
							/**************在进行其它四个条件比对之前，先根据票据编号比对，如果用户导入的票据编号在库中已存在，就认为是重复数据；否则在比对其它四个条件******************/
							String receiptNoDB = colModel.getReceiptNo(); 
							
							if (payStatus) { //按月编号
								//判断是否在同一个月内，如果在同一个月内，比较票据编号，否则不比较
								if (monthDBDate.equals(excelMonthDate)) { //在同一个月内，判断票据编号
									if (receiptNoDB.equals(receiptNoExcel)) {
										mapExcel.put("collectionId", collectionId);
										mapExcel.put("receiptNo", colModel.getReceiptNo());
										mapExcel.put("createTime", colModel.getCreateTime());
										updateList.add(mapExcel);
										isRepeat = true;
										break;
									}
								}
								
								//判断四个条件是否一样
								if(paymentDateDB.equals(paymentDateExcel)&&payeeNameDB.equals(payeeNameExcel)&&summaryDB.equals(summaryExcel)&&moneyDB.equals(moneyExcel)){
									mapExcel.put("collectionId", collectionId);
									mapExcel.put("receiptNo", colModel.getReceiptNo());
									mapExcel.put("createTime", colModel.getCreateTime());
									updateList.add(mapExcel);
									isRepeat = true;
									break;
								}
							}else {
								
								if (receiptNoDB.equals(receiptNoExcel)) {
									mapExcel.put("collectionId", collectionId);
									mapExcel.put("receiptNo", colModel.getReceiptNo());
									mapExcel.put("createTime", colModel.getCreateTime());
									updateList.add(mapExcel);
									isRepeat = true;
									break;
								}else {
									if(paymentDateDB.equals(paymentDateExcel)&&payeeNameDB.equals(payeeNameExcel)&&summaryDB.equals(summaryExcel)&&moneyDB.equals(moneyExcel)){
										mapExcel.put("collectionId", collectionId);
										mapExcel.put("receiptNo", colModel.getReceiptNo());
										mapExcel.put("createTime", colModel.getCreateTime());
										updateList.add(mapExcel);
										isRepeat = true;
										break;
									}
								}
							}
						}
						if(!isRepeat){
							insertList.add(mapExcel);
						}
						columIndex ++;
					}
					
					if(isCover){
						//更新需要覆盖的数据
						List<CollectionInfoModel> toUpdateCollectionList = new ArrayList<CollectionInfoModel>();
						int count = 1;
						for (Map<String, Object> toUpdateData : updateList) {
							Integer rowNumber = (Integer) toUpdateData.get("rowNumber");
							
							String collectionId = (String) toUpdateData.get("collectionId");
							String receiptNo = (String) toUpdateData.get("receiptNo");
							String collectionDate = (String) toUpdateData.get("receiptDate");
							String otherUnit = (String) toUpdateData.get("aimPersonName");
							String summary = (String) toUpdateData.get("summary");
							
							String collectMoneyStr = (String) toUpdateData.get("collectMoney");
							String[] moneyInfo = getMoneyInfo(collectMoneyStr, currencyMap, rowNumber.toString());
							Double money = Double.parseDouble(moneyInfo[0]);
							String currencyId = moneyInfo[1];
							
							String paymentWay = (String) toUpdateData.get("paymentWay");
							String paymentWayId = payOrCollectPaymentWayMap.get(paymentWay);//付款方式id
							if(StringUtils.isBlank(paymentWayId)){
								//判断临时map中是否有付款方式信息
								paymentWayId = tempMap.get(paymentWay);
								if(StringUtils.isBlank(paymentWayId)){
									paymentWayId = UUIDUtils.getId();
									FinancePaymentWayModel financePaymentWayModel = new FinancePaymentWayModel();
									financePaymentWayModel.setWayId(paymentWayId);
									financePaymentWayModel.setWayName(paymentWay);
									financePaymentWayModel.setCreateTime(new Timestamp(System.currentTimeMillis()+(count*1000)));
									financePaymentWayModel.setCrewId(crewId);
									
									listPayWay.add(financePaymentWayModel);
									tempMap.put(paymentWay, paymentWayId);
								}
							}
							
							String agent = (String) toUpdateData.get("agent");
							Date tempDate = (Date) toUpdateData.get("createTime");
							Date createTime = new Timestamp(tempDate.getTime()+(count*1000));
							
							
							CollectionInfoModel toUpdateCollectionInfo = new CollectionInfoModel();
							toUpdateCollectionInfo.setCollectionId(collectionId);
							toUpdateCollectionInfo.setCrewId(crewId);
							toUpdateCollectionInfo.setReceiptNo(receiptNo);
							toUpdateCollectionInfo.setCollectionDate(this.sdf1.parse(collectionDate));
							toUpdateCollectionInfo.setOtherUnit(otherUnit);
							toUpdateCollectionInfo.setSummary(summary);
							toUpdateCollectionInfo.setMoney(money);
							toUpdateCollectionInfo.setCurrencyId(currencyId);
							toUpdateCollectionInfo.setPaymentWay(paymentWayId);
							toUpdateCollectionInfo.setAgent(agent);
							toUpdateCollectionInfo.setCreateTime(createTime);
							
							toUpdateCollectionList.add(toUpdateCollectionInfo);
							
							count ++;
						}
						
						this.collectionInfoService.updateBatch(toUpdateCollectionList);
					}
					//如果不覆盖   保存不重复的数据
					collectList = insertList;
				}
				saveCollectInfo(crewId, collectList, currencyMap, payOrCollectPaymentWayMap, listPayWay, tempMap);
			}
			
			//保存付款单信息
			//根据 日期，摘要，收付款方，金额判断是否有重复数据     未结算的数据才能支持覆盖
			if(payList!=null&&payList.size()>0){
				List<Map<String, Object>> insertList = new ArrayList<Map<String,Object>>();//需要保存的数据  tab_payment_info
				List<Map<String, Object>> insertMap = new ArrayList<Map<String,Object>>();//需要保存的数据  tab_payment_finanSubj_map，
				List<Map<String, Object>> updateList = new ArrayList<Map<String,Object>>();//需要修改的数据  主要是tab_payment_info 
				List<String> delList = new ArrayList<String>();//删除tab_payment_finanSubj_map中的数据
				Set<String> mapids = new HashSet<String>();
				List<Map<String, Object>>  paymentInfoList = paymentInfoService.queryByCrewIdAndStatus(crewId);
				
				//取出库中所有的票据编号
				List<String> receiptNoDBList = new ArrayList<String>();
				for (Map<String, Object> map : paymentInfoList) {
					String receiptNoDB = (String) map.get("receiptNo");
					if (!receiptNoDBList.contains(receiptNoDB)) {
						receiptNoDBList.add(receiptNoDB);
					}
				}
				
				
				if(paymentInfoList!=null&&paymentInfoList.size()>0){
					int payIndex = 3;
					//日期，摘要，收付款方，金额 四个字段如果值一样则判断为重复
					for(Map<String, Object> mapExcel :payList){//excel数据
						Set<String> keyset = mapExcel.keySet();
						Iterator<String> eIt = keyset.iterator();
						
						List<Map<String, Object>> insertpaymentTabList = new ArrayList<Map<String,Object>>();
						String  receiptNo = "";
						while(eIt.hasNext()){
							receiptNo = eIt.next();
							List<Map<String, Object>> listMap = (List<Map<String, Object>>)mapExcel.get(receiptNo);
							double totalMoney = 0.0;
							for(Map<String, Object> innerMap :listMap){
								boolean isRepeat = false;
								String paymentDateExcel = innerMap.get("receiptDate")!=null?innerMap.get("receiptDate").toString():"";
								String payeeNameExcel = innerMap.get("aimPersonName")!=null?innerMap.get("aimPersonName").toString():"";
								String summaryExcel = innerMap.get("summary")!=null?innerMap.get("summary").toString():"";
								String moneyExcel = innerMap.get("payedMoney")!=null?innerMap.get("payedMoney").toString():"0";
								
								//将excel表中的时间格式化为月份
								String excelMonthDate = "";
								if (StringUtils.isNotBlank(paymentDateExcel)) {
									excelMonthDate = paymentDateExcel.substring(0, paymentDateExcel.lastIndexOf("-"));
								}

								//获取货币编号
								moneyExcel.replaceAll(Constants.REGEX_EXCEL, ")");
								String moneyCode = "";
								if (moneyExcel.contains("(")) {
									moneyCode = moneyExcel.substring(moneyExcel.indexOf("(")+1, moneyExcel.indexOf(")"));
								}
								
								//取出用户输入的票据编号
								String receiptNoExcel = "";
								if (StringUtils.isNotBlank(receiptNo)) {
									receiptNoExcel = receiptNo;
								}
								
								if (moneyExcel.contains("(")) {
									moneyExcel = moneyExcel.substring(0, moneyExcel.indexOf("("));
								}
								
								if(StringUtils.isNotBlank(moneyExcel)){
									moneyExcel = String.valueOf(Double.valueOf(moneyExcel.replaceAll(",", "").replaceAll("，", "")));
								}
								
								if (totalMoney == 0.0) {
									totalMoney = Double.parseDouble(moneyExcel);
								}else {
									totalMoney = totalMoney + Double.parseDouble(moneyExcel);
								}
								innerMap.put("totalMoney", moneyExcel);
								innerMap.put("payedMoney", moneyExcel+"("+ moneyCode +")");
								for(Map<String, Object> mapDB :paymentInfoList){//数据库数据
									String mapId = mapDB.get("mapId")!=null?mapDB.get("mapId").toString():"";
									String paymentId = mapDB.get("paymentId")!=null?mapDB.get("paymentId").toString():"";
									String paymentDateDB = mapDB.get("paymentDate")!=null?mapDB.get("paymentDate").toString():"";
									String payeeNameDB = mapDB.get("payeeName")!=null?mapDB.get("payeeName").toString():"";
									String summaryDB = mapDB.get("summary")!=null?mapDB.get("summary").toString():"";
									String moneyDB = mapDB.get("money")!=null?mapDB.get("money").toString():"0";
									paymentDateExcel = DateUtils.formatToString(paymentDateExcel, payIndex);
									innerMap.put("receiptDate", paymentDateExcel);
									String monthDBDate = mapDB.get("paymentDate")!=null?new SimpleDateFormat("yyyy-MM").format((Date)mapDB.get("paymentDate")):"";//收款时间
									
									/**************在进行其它四个条件比对之前，先根据票据编号比对，如果用户导入的票据编号在库中已存在，就认为是重复数据；否则在比对其它四个条件******************/
									String receiptNoDB = (String) mapDB.get("receiptNo"); 
									
									//判断是否按月重新编号
									if (payStatus) {
										//判断是否在同一个月内
										if (monthDBDate.equals(excelMonthDate)) {
											if (receiptNoDB.equals(receiptNoExcel)) {
												mapids.add(mapId);//需要删除的数据  主要是tab_payment_finanSubj_map
												innerMap.put("paymentId", paymentId);
												insertMap.add(innerMap);//需要保存的数据  主要是tab_payment_finanSubj_map
												updateList.add(innerMap);//需要修改的数据  主要是tab_payment_info
												isRepeat = true;
												break;
											}
										}
										
										//判断当前编号在库中是否存在
										if(!receiptNoDBList.contains(receiptNoExcel) && paymentDateDB.equals(paymentDateExcel)&&payeeNameDB.equals(payeeNameExcel)&&summaryDB.equals(summaryExcel)&&moneyDB.equals(moneyExcel)){
											mapids.add(mapId);//需要删除的数据  主要是tab_payment_finanSubj_map
											innerMap.put("paymentId", paymentId);
											insertMap.add(innerMap);//需要保存的数据  主要是tab_payment_finanSubj_map
											updateList.add(innerMap);//需要修改的数据  主要是tab_payment_info
											isRepeat = true;
											break;
										}
									}else {
										//先判断票据编号是否重复
										if (receiptNoDB.equals(receiptNoExcel)) {
											mapids.add(mapId);//需要删除的数据  主要是tab_payment_finanSubj_map
											innerMap.put("paymentId", paymentId);
											insertMap.add(innerMap);//需要保存的数据  主要是tab_payment_finanSubj_map
											updateList.add(innerMap);//需要修改的数据  主要是tab_payment_info
											isRepeat = true;
											break;
											
										}else {
											if(!receiptNoDBList.contains(receiptNoExcel) && paymentDateDB.equals(paymentDateExcel)&&payeeNameDB.equals(payeeNameExcel)&&summaryDB.equals(summaryExcel)&&moneyDB.equals(moneyExcel)){
												mapids.add(mapId);//需要删除的数据  主要是tab_payment_finanSubj_map
												innerMap.put("paymentId", paymentId);
												insertMap.add(innerMap);//需要保存的数据  主要是tab_payment_finanSubj_map
												updateList.add(innerMap);//需要修改的数据  主要是tab_payment_info
												isRepeat = true;
												break;
											}
										}
									}
									
								}
								if(!isRepeat){
									insertpaymentTabList.add(innerMap);
								}
							}
						}
						
						if(insertpaymentTabList.size()>0){
							Map<String, Object> inserpaymentTabMap = new HashMap<String, Object>();
							inserpaymentTabMap.put(receiptNo, insertpaymentTabList);
							insertList.add(inserpaymentTabMap);
						}
						
						payIndex ++;
					}
					 
					if(isCover){
						//覆盖   删除原有数据添加新数据
						//删除tab_payment_finanSubj_map
						String delSql = "delete from tab_payment_finanSubj_map where paymentId = ?";
						List<Object[]> paramsDel = new ArrayList<Object[]>();
						for(Map<String, Object> innmap :updateList){
							//根据付款单的id删除关联的借款单信息
							String paymentId = (String) innmap.get("paymentId");//付款id
							if (!delList.contains(paymentId)) {
								delList.add(paymentId);
							}
						}
						for(String paymentId :delList){
							Object[] oo = new Object[]{paymentId};
							paramsDel.add(oo);
						}
						//删除原有数据
						getCostDao.getJdbcTemplate().batchUpdate(delSql, paramsDel);					
						
						//删除付款单关联的借款单信息
						for(Map<String, Object> innmap :updateList){
							//根据付款单的id删除关联的借款单信息
							String paymentId = (String) innmap.get("paymentId");//付款id
							paymentLoanMapService.deleteByPaymentId(crewId, paymentId);
						}
						
						//insert map 数据
						String insertMapSql = "insert into tab_payment_finanSubj_map (mapId,paymentId,financeSubjId,summary,money,crewId,financeSubjName) values(?,?,?,?,?,?,?)";
						
						List<Object[]> parmInsertMap = new ArrayList<Object[]>();
						for(Map<String, Object> innmap :insertMap){
							String rowNumber = 	innmap.get("rowNumber")!=null?innmap.get("rowNumber").toString():""; 
							//获取剧组财务科目id
							String financeSubjName = innmap.get("financeSubjName")!=null?innmap.get("financeSubjName").toString():"";
							String financeSubjId = this.getFinanSubjIdByLevelNames(crewId, financeSubjName, rowNumber, subjectDtoList);
							
							if(StringUtils.isBlank(financeSubjId)){
								throw new IllegalArgumentException("当前剧组中不存在名为：["+financeSubjName+"]的财务科目层级");
							}
							//整理付款金额
							String money = innmap.get("payedMoney")!=null?innmap.get("payedMoney").toString():"";
							Pattern pattern = Pattern.compile(Constants.REGEX_EXCEL);
							Matcher matcher = pattern.matcher(money);
							if(matcher.find()){
								money = matcher.group(1);
							}
							if(StringUtils.isNotBlank(money)){
								money = String.valueOf(Double.valueOf(money.replaceAll(",", "").replaceAll("，", "")));
							}
							
							Object[] oo = new Object[7];
							oo[0] = UUIDUtils.getId();//主键
							oo[1] = innmap.get("paymentId");//付款id
							oo[2] = financeSubjId;//财务科目id
							oo[3] = innmap.get("summary");//摘要
							oo[4] = money;//付款金额
							oo[5] = crewId;//剧组id
							oo[6] = financeSubjName;//财务科目名称
							parmInsertMap.add(oo);
						}
						getCostDao.getJdbcTemplate().batchUpdate(insertMapSql, parmInsertMap);	
						
						//修改tab_payment_info
						
						String updateSql = "update tab_payment_info set contractId =? ,contractType = ? ,currencyId = ? , paymentWay = ? , hasReceipt = ? , billCount = ? , agent = ?, totalMoney = ?, payeeName = ?,department = ? where paymentid = ?";
						
						List<Object[]> parmUpdateList = new ArrayList<Object[]>();
						Map<String, Object> maxMoneyMap = new HashMap<String, Object>();
						
						for(int i =0 ; i<updateList.size(); i++) {
							Map<String, Object> firstMap = updateList.get(i);
							double allMoney = 0.0;
							//判断当前map是否遍历
							boolean isForeach = false;
							Object isForeachObject = updateList.get(i).get("isForeach");
							if (isForeachObject != null) {
								isForeach = (Boolean) updateList.get(i).get("isForeach");
							}
							if (!isForeach) {
								for (int j = updateList.size()-1; j > i; j--) {
									Map<String, Object> secondMap = updateList.get(j);
									String firstMoneyExcel = firstMap.get("totalMoney")!=null?firstMap.get("totalMoney").toString():"0";
									String firstPaymentStr = (String) firstMap.get("paymentId");//付款id
									
									String secondMoneyExcel = secondMap.get("totalMoney")!=null?secondMap.get("totalMoney").toString():"0";
									String secondPaymentStr = (String) secondMap.get("paymentId");//付款id
									if (firstPaymentStr.equals(secondPaymentStr)) {
										if (allMoney == 0.0) {
											allMoney = Double.parseDouble(firstMoneyExcel);
										}
										secondMap.put("isForeach", true);
										allMoney = allMoney + Double.parseDouble(secondMoneyExcel);
										if (!maxMoneyMap.containsKey(firstPaymentStr)) {
											maxMoneyMap.put(firstPaymentStr, allMoney);
										}else {
											//取出总金额，存入最大金额
											double maxMoney = 0.0;
											String maxMoneyStr = maxMoneyMap.get(firstPaymentStr)+"";
											if (maxMoneyStr != null) {
												maxMoney = Double.parseDouble(maxMoneyStr);
											}
											
											if (maxMoney < allMoney) {
												maxMoneyMap.put(firstPaymentStr, allMoney);
											}
										}
									}
								}
							}
						}
						
						for (Map<String, Object> innmap : updateList) {
							//取出付款单id
							String paymentIDStr = innmap.get("paymentId")+"";
							if (maxMoneyMap.containsKey(paymentIDStr)) {
								//取出总金额
								Object maxMoney = maxMoneyMap.get(paymentIDStr);
								innmap.put("totalMoney", maxMoney);
							}
						}
						
						for(Map<String, Object> innmap :updateList){
							String rowNumber = 	innmap.get("rowNumber")!=null?innmap.get("rowNumber").toString():""; 
							//合同号
							int contractType = 0;
							String contractId = "";
							String contractNo = innmap.get("contractNo")!=null?innmap.get("contractNo").toString():"";
							if(StringUtils.isNotBlank(contractNo)){
								String rowStr = innmap.get("rowNumber")!=null?innmap.get("rowNumber").toString():"";
								//获取合同类型
								Map<String, Object> contractMap = queryContractInfoForContractType(crewId,contractNo,rowStr);
								contractType = contractMap.get("contractType")!=null?Integer.valueOf(contractMap.get("contractType").toString()):0;
								contractId = contractMap.get("contractId")!=null?contractMap.get("contractId").toString():"";
								if(0==contractType){
									
									throw new IllegalArgumentException("第"+rowStr+"行，合同号：【"+contractNo+"】在数据库中不存在");
								}
							}
							
							//发票张数
							int billCount = 0;
							try {
								billCount = innmap.get("billCount")!=null?StringUtils.isNotBlank(innmap.get("billCount").toString())?Integer.valueOf(innmap.get("billCount").toString()):0:0;
							} catch (Exception e) {
								throw new IllegalArgumentException("第"+rowNumber+"行，票据张数必须为数字");
							}
							
							//有无发票
							int hasReceipt = 0;//无发票
							String ifReceiveBill = innmap.get("hasReceipt")!=null?innmap.get("hasReceipt").toString():"";
							if("有发票".equals(ifReceiveBill)){
								hasReceipt = 1;//有发票
							} else {
								billCount = 0;
							}
							
							
							String payedMoney = innmap.get("payedMoney")!=null?innmap.get("payedMoney").toString():"";
							String[] moneyInfo = getMoneyInfo(payedMoney,currencyMap,rowNumber);
							//货币id
							String currencyId = moneyInfo[1];
							
							String paymentWay=innmap.get("paymentWay").toString();
							String wayid = payOrCollectPaymentWayMap.get(paymentWay);
							if(StringUtils.isBlank(wayid)){
								//判断临时map中是否有付款方式信息
								wayid = tempMap.get(paymentWay);
								if(StringUtils.isBlank(wayid)){
									wayid = UUIDUtils.getId();
									FinancePaymentWayModel financePaymentWayModel = new FinancePaymentWayModel();
									financePaymentWayModel.setWayId(wayid);
									financePaymentWayModel.setWayName(paymentWay);
									financePaymentWayModel.setCreateTime(new Timestamp(System.currentTimeMillis()));
									financePaymentWayModel.setCrewId(crewId);
									
									listPayWay.add(financePaymentWayModel);
									tempMap.put(paymentWay, wayid);
								}
							}
							
							//整理付款金额
							String money = innmap.get("totalMoney")!=null?innmap.get("totalMoney").toString():"";
							/*Pattern pattern = Pattern.compile(Constants.REGEX_EXCEL);
							Matcher matcher = pattern.matcher(money);
							if(matcher.find()){
								money = matcher.group(1);
							}*/
							if(StringUtils.isNotBlank(money)){
								money = String.valueOf(Double.valueOf(money.replaceAll(",", "").replaceAll("，", "")));
							}
							
							//收款人姓名
							String payeeName = innmap.get("aimPersonName")!=null?innmap.get("aimPersonName").toString():"";
							
							//部门
							String department = innmap.get("department")== null?"":innmap.get("department").toString();
							Object[] oo = new Object[11];
							oo[0] = contractId;//合同id
							oo[1] = contractType;//合同类型
							oo[2] = currencyId;//货币id
							oo[3] = wayid;//付款方式
							oo[4] = hasReceipt;//是否有发票
							oo[5] = billCount;//发票张数
							oo[6] = innmap.get("agent");//经办人
							oo[7] = money;  //总金额
							oo[8] = payeeName; //收款人姓名
							oo[9] = department; //部门
							oo[10] = innmap.get("paymentId");//付款id
							parmUpdateList.add(oo);
						}
						getCostDao.getJdbcTemplate().batchUpdate(updateSql, parmUpdateList);	
						
						
					}
					//   添加不重复数据
					payList = insertList;
					
				}
				//保存付款信息
				savePaymentInfo(crewId, payList, currencyMap, payOrCollectPaymentWayMap, listPayWay, tempMap, subjectDtoList);
			}
			
			//保存借款单信息
			if(loanList!=null&&loanList.size()>0){
				List<Map<String, Object>> insertList = new ArrayList<Map<String,Object>>();//保存数据
				List<Map<String, Object>> updateList = new ArrayList<Map<String, Object>>();//删除数据
				
				//根据 日期，摘要，收付款方，金额判断是否有重复数据     未结算的数据才能支持覆盖
				List<LoanInfoModel>  loanInfoList = loanInfoService.queryByCrewId(crewId);
				int loanIndex = 3;
				for(Map<String, Object> mapExcel :loanList){
					boolean isRepeat = false;
					String paymentDateExcel = mapExcel.get("receiptDate")!=null?mapExcel.get("receiptDate").toString():"";
					String payeeNameExcel = mapExcel.get("aimPersonName")!=null?mapExcel.get("aimPersonName").toString():"";
					String summaryExcel = mapExcel.get("summary")!=null?mapExcel.get("summary").toString():"";
					String moneyExcel = mapExcel.get("payedMoney")!=null?mapExcel.get("payedMoney").toString():"";
					
					//将excel表中的时间格式化为月份
					String excelMonthDate = "";
					if (StringUtils.isNotBlank(paymentDateExcel)) {
						excelMonthDate = paymentDateExcel.substring(0, paymentDateExcel.lastIndexOf("-"));
					}
					
					//取出用户输入的票据编号
					String receiptNoExcel = mapExcel.get("receiptNo") != null ?mapExcel.get("receiptNo").toString():"";
					//获取货币编号
					moneyExcel.replaceAll(Constants.REGEX_EXCEL, ")");
					String moneyCode = "";
					if (moneyExcel.contains("(")) {
						moneyCode = moneyExcel.substring(moneyExcel.indexOf("(")+1, moneyExcel.indexOf(")"));
					}
					
					if (moneyExcel.contains("(")) {
						moneyExcel = moneyExcel.substring(0, moneyExcel.indexOf("("));
					}
					
					if(StringUtils.isNotBlank(moneyExcel)){
						moneyExcel = String.valueOf(Double.valueOf(moneyExcel.replaceAll(",", "").replaceAll("，", "")));
					}
					mapExcel.put("payedMoney", moneyExcel+"("+ moneyCode +")");
					
					for(LoanInfoModel loModel :loanInfoList){
						//借款
						String loanerId = loModel.getLoanId();//借款id
						String paymentDateDB = loModel.getLoanDate()!=null?new SimpleDateFormat("yyyy-MM-dd").format(loModel.getLoanDate()):"";//借款时间
						String payeeNameDB = loModel.getPayeeName();//收款方
						String summaryDB = loModel.getSummary();//摘要
						String moneyDB = loModel.getMoney()!=0.0?String.valueOf(loModel.getMoney()):"";
						paymentDateExcel = DateUtils.formatToString(paymentDateExcel, loanIndex);
						mapExcel.put("receiptDate", paymentDateExcel);
						String monthDBDate = loModel.getLoanDate()!=null?new SimpleDateFormat("yyyy-MM").format(loModel.getLoanDate()):"";//借款时间
						
						/**************在进行其它四个条件比对之前，先根据票据编号比对，如果用户导入的票据编号在库中已存在，就认为是重复数据；否则在比对其它四个条件******************/
						String receiptNoDB = loModel.getReceiptNo();
						
						//判断是否是按月重新编号
						if (payStatus) {
							//判断是否在同一个月内
							if (excelMonthDate.equals(monthDBDate)) {
								if (receiptNoDB.equals(receiptNoExcel)) {
									mapExcel.put("loanId", loModel.getLoanId());
									mapExcel.put("receiptNo", loModel.getReceiptNo());
									mapExcel.put("createTime", loModel.getCreateTime());
									updateList.add(mapExcel);
									isRepeat = true;
									break;
								}
							}
							//判断四个条件是否满足
							if(paymentDateDB.equals(paymentDateExcel)&&payeeNameDB.equals(payeeNameExcel)&&summaryDB.equals(summaryExcel)&&moneyDB.equals(moneyExcel)){
								mapExcel.put("loanId", loModel.getLoanId());
								mapExcel.put("receiptNo", loModel.getReceiptNo());
								mapExcel.put("createTime", loModel.getCreateTime());
								
								updateList.add(mapExcel);
								isRepeat = true;
								break;
							}
						}else {
							if (receiptNoDB.equals(receiptNoExcel)) {
								mapExcel.put("loanId", loModel.getLoanId());
								mapExcel.put("receiptNo", loModel.getReceiptNo());
								mapExcel.put("createTime", loModel.getCreateTime());
								
								updateList.add(mapExcel);
								isRepeat = true;
								break;
							}else {
								if(paymentDateDB.equals(paymentDateExcel)&&payeeNameDB.equals(payeeNameExcel)&&summaryDB.equals(summaryExcel)&&moneyDB.equals(moneyExcel)){
									mapExcel.put("loanId", loModel.getLoanId());
									mapExcel.put("receiptNo", loModel.getReceiptNo());
									mapExcel.put("createTime", loModel.getCreateTime());
									
									updateList.add(mapExcel);
									isRepeat = true;
									break;
								}
							}
						}
						
					}
					if(!isRepeat){
						insertList.add(mapExcel);
					}
					
					loanIndex ++;
				}
				if(isCover){
					int count = 1;
					List<LoanInfoModel> toUpdateLoanInfoList = new ArrayList<LoanInfoModel>();
					for (Map<String, Object> toUpdateData : updateList) {
						Integer rowNumber = (Integer) toUpdateData.get("rowNumber");
						
						String loanId = (String) toUpdateData.get("loanId");
						String receiptNo = (String) toUpdateData.get("receiptNo");
						String loanDate = (String) toUpdateData.get("receiptDate");
						String payeeName = (String) toUpdateData.get("aimPersonName");
						String summary = (String) toUpdateData.get("summary");
						
						String payedMoney = (String) toUpdateData.get("payedMoney");
						String[] moneyInfo = getMoneyInfo(payedMoney, currencyMap, rowNumber.toString());
						Double money = Double.parseDouble(moneyInfo[0]);
						String currencyId = moneyInfo[1];
						
						String paymentWayStr = (String) toUpdateData.get("paymentWay");
						if (!paymentWayStr.equals("现金") && !paymentWayStr.equals("现金（网转）") && !paymentWayStr.equals("银行")) {
							throw new IllegalArgumentException("第" + rowNumber + "行付款方式只能为【现金,现金（网转）,银行】之一");
						}
						int paymentWay = LoanPaymentWay.nameOf(paymentWayStr).getValue();
						String agent = (String) toUpdateData.get("agent");
						Date tempDate = (Date) toUpdateData.get("createTime");
						Date createTime =  new Timestamp(tempDate.getTime()+(count*1000));
						
						String finanSubjName = (String) toUpdateData.get("financeSubjName");
						String financeSubjId = null;
						
						if (StringUtils.isNotBlank(finanSubjName)) {
							financeSubjId = this.getFinanSubjIdByLevelNames(crewId, finanSubjName, rowNumber.toString(), subjectDtoList);
						}
						
						LoanInfoModel toUpdateLoanInfo = new LoanInfoModel();
						toUpdateLoanInfo.setLoanId(loanId);
						toUpdateLoanInfo.setCrewId(crewId);
						toUpdateLoanInfo.setReceiptNo(receiptNo);
						toUpdateLoanInfo.setLoanDate(this.sdf1.parse(loanDate));
						toUpdateLoanInfo.setPayeeName(payeeName);
						toUpdateLoanInfo.setSummary(summary);
						toUpdateLoanInfo.setMoney(money);
						toUpdateLoanInfo.setCurrencyId(currencyId);
						toUpdateLoanInfo.setPaymentWay(paymentWay);
						toUpdateLoanInfo.setAgent(agent);
						toUpdateLoanInfo.setCreateTime(createTime);
						toUpdateLoanInfo.setFinanceSubjId(financeSubjId);
						toUpdateLoanInfo.setFinanceSubjName(finanSubjName);
						
						toUpdateLoanInfoList.add(toUpdateLoanInfo);
					}
					
					this.loanInfoService.updateBatch(toUpdateLoanInfoList);
				}
				loanList = insertList;
				saveloanInfo(crewId, loanList, currencyMap, subjectDtoList);
			}
			
			//保存该剧组的付款方式
			savePayOrCollectPayment(listPayWay);
		} catch(IllegalArgumentException ie){
			throw new IllegalArgumentException(ie.getMessage());
		}catch (Exception e) {
			throw new IllegalArgumentException("未知异常",e);
		}
	}

	/**
	 * 整理导入数据
	 * @param  crewId  剧组ID
	 * @param getCostInfoMap  读取excel中的数据
	 * @param FINANCE_MAP     excel中列标题对应字段
	 * @param payList         付款数据
	 * @param collectList	     收款数据
	 * @param loanList		     借款数据
	 * @param subjectDtoList	剧组中所有财务科目
	 * @param needDealDataList 需要用户手工处理的数据
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private void arrangeSaveInfoFromExcel(String crewId, Map<String, Object> getCostInfoMap, Map<String, String> FINANCE_MAP,
			List<Map<String, Object>> payList, List<Map<String, Object>> collectList,
			List<Map<String, Object>> loanList, List<Map<String, Object>> needDealDataList, List<FinanceSubjectDto> subjectDtoList)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		//根据剧组id获取该剧组下的付款方式，只针对付款和收款，借款的付款方式为固定的（详情见：LoanPaymentWay)
		Map<String, String> payOrCollectPaymentWayMap = queryPayOrCollectPaymentWay(crewId);
		//循环来自excel中的数据将数据归类为  收款、付款、借款三类数据
		Set<String> sheetSet = getCostInfoMap.keySet();
		Iterator<String> sheetKeys = sheetSet.iterator();
		while(sheetKeys.hasNext()){
			String sheetKey = sheetKeys.next();
			List<ArrayList<String>> excelDataList = (List<ArrayList<String>>)getCostInfoMap.get(sheetKey);//excel读取的数据
			if(excelDataList==null||excelDataList.size()<2){//为空或者只有一行（标题）则不保存
				continue;
			}
			
			//获取第一行，标题行
			//List<String> titleList =excelDataList.get(0);
			//删除标题行剩下的就全部是数据了
			List<String> title = excelDataList.get(0);
			excelDataList.remove(0);
			List<String> coloumnNameList = excelDataList.get(0);
			excelDataList.remove(0);
			
			Set<String> columnSet = FINANCE_MAP.keySet(); 
			Iterator<String> it = columnSet.iterator();
	//校验  标题  和列名
			boolean hasTitle = true;
			boolean columnNameIsTrue = true;
			while(it.hasNext()){
				String realColumnName = it.next();
				if(hasTitle){
					for(String str:title){
						if(StringUtils.isNotBlank(str)){
							if(str.equals(realColumnName)){
								hasTitle = false;
							}
						}
					}
				}
				if(columnNameIsTrue){
					boolean flag = false;
					for(String column:coloumnNameList){
						if(StringUtils.isNotBlank(column)){
							if(column.equals(realColumnName)){
								flag = true;
								break;
							}
						}
					}
					columnNameIsTrue = flag;
				}
			}
			
			if(!hasTitle){
				throw new IllegalArgumentException("请添加标题");
			}
			if(!columnNameIsTrue){
				throw new IllegalArgumentException("表格列名有误");
			}
//校验  标题  和列名			
			
			//将list的信息放入到map中
			List<Map<String, Object>> datalist = listToMap(excelDataList,FINANCE_MAP);
			
			//票据编号
			List<String> identifier = new ArrayList<String>();//excel中票据编号
			
			int rowNumber = 2;
			for(Map<String, Object> inMap :datalist){
				++rowNumber; 
				inMap.put("rowNumber", rowNumber);
				String identifierNum = inMap.get("receiptNo")!=null?inMap.get("receiptNo").toString():"";//财务票据编号
				if(StringUtils.isBlank(identifierNum)){
					throw new IllegalArgumentException("第"+rowNumber+"行，票据编号不能为空");
				}
				if (identifierNum.indexOf("FK") != -1) { //付款单据特殊处理
					if (!identifier.contains(identifierNum)) {
						identifier.add(identifierNum);
					}
				}else {
					identifier.add(identifierNum);
				}
			}
			
			Iterator<String> identifierIt = identifier.iterator();
			Map<String, Object> groupMap = null;
			while(identifierIt.hasNext()){
				groupMap = new HashMap<String, Object>();//同一个票据编号对应的数据
				List<Map<String, Object>> groupList = new ArrayList<Map<String,Object>>();
				String identifierNum = identifierIt.next();
				Set<String> payType = new HashSet<String>();// 判断同一个财务票据编号是否有不同的财务类型
				
				String tempStr = "";
				for(Map<String, Object> inMap :datalist){
					String rowStr = inMap.get("rowNumber")!=null?inMap.get("rowNumber").toString():"";
					String innerIdentifierNum = inMap.get("receiptNo")!=null?inMap.get("receiptNo").toString():"";//财务票据编号
					String financeSubjName = (String) inMap.get("financeSubjName");
					String paymentWay = (String) inMap.get("paymentWay");
					String hasReceipt= (String) inMap.get("hasReceipt");
					String paymentDate = inMap.get("receiptDate")!=null?inMap.get("receiptDate").toString():"";//付款日期
					String payeeName = inMap.get("aimPersonName")!=null?inMap.get("aimPersonName").toString():"";//收付款人
					String summary = inMap.get("summary")!=null?inMap.get("summary").toString():"";//摘要
					String money = inMap.get("collectMoney")!=null?inMap.get("collectMoney").toString():"";//收款金额
					
					if (tempStr == "") {
						tempStr = paymentDate + payeeName + summary+ money;
					}
					
					if(identifierNum.equals(innerIdentifierNum) /*||  tempStr.equals(paymentDate + payeeName + summary+ money)*/){
						String formType = inMap.get("formType")!=null?inMap.get("formType").toString():"";//财务类型    收款、付款、借款
						tempStr = paymentDate + payeeName + summary+ money;
						
						if (StringUtils.isBlank(paymentWay)) {
							throw new IllegalArgumentException("第"+rowStr+"行，付款方式不能为空");
						}
						//校验付款方式填写是否正确
						String wayid = payOrCollectPaymentWayMap.get(paymentWay);
						/*if (StringUtils.isBlank(wayid)) {
							throw new IllegalArgumentException("第"+rowStr+"行，付款方式不存在，请修改");
						}*/
						
						if(!"收款单".equals(formType)&&!"付款单".equals(formType)&&!"借款单".equals(formType)){
							throw new IllegalArgumentException("第"+rowStr+"行，财务类型信息异常，财务类型为【收款单、付款单、借款单】");
						}
						
						if(StringUtils.isBlank(formType)){
							throw new IllegalArgumentException("第"+rowStr+"行，财务类型信息不能为空");
						}
						if (!StringUtils.isBlank(hasReceipt)) {
							if (!hasReceipt.equals("有发票") && !hasReceipt.equals("无发票")&& !hasReceipt.equals("/")) {
								throw new IllegalArgumentException("有无发票必须为“有发票”或者“无发票”");
							}
						}
						if(payType.size()!=0){
							int beforeSize = payType.size();
							payType.add(formType);
							int afterSize = payType.size();
							if(beforeSize!=afterSize){
								throw new IllegalArgumentException("第"+rowStr+"行，相同的票据编号有不同的财务类型，票据编号为："+identifierNum);
							}
						}else{
							payType.add(formType);
						}
						
						//校验该剧组中是否有多条该名字的财务科目，如果存在，则把该类数据归类到需要用户自己确认的数据里面
						if (!StringUtils.isBlank(financeSubjName)) {
							String finanSubjId = this.getFinanSubjIdByLevelNames(crewId, financeSubjName, rowStr, subjectDtoList);
							if (finanSubjId.equals("needChoose")) {
								needDealDataList.add(inMap);
								continue;
							}
						}
						
						groupList.add(inMap);
					}
				}
				
				if (groupList.size() == 0) {
					continue;
				}
				
				//区分收款、付款、借款
				boolean collect = payType.contains("收款单");
				boolean pay = payType.contains("付款单");
				boolean borrow = payType.contains("借款单");
				
				groupMap.put(identifierNum, groupList);
				if(collect){
					for(int i = 0;i<groupList.size();i++){
						collectList.add(groupList.get(i));
					}
					/*if(groupList.size()==1){
					}else{
						StringBuilder sb = new StringBuilder();
						for(int i = 0;i<groupList.size();i++){
							Map<String, Object> tMap = groupList.get(i);
							String str = tMap.get("rowNumber")!=null?tMap.get("rowNumber").toString():"";
							sb.append(str);
							if(i<groupList.size()-1){
								sb.append(",");	
							}
						}
						
						throw new IllegalArgumentException("第"+sb.toString()+"行，同一个票据编号有多条收款信息，票据编号为："+identifierNum);	
					}*/
					continue;
				}
				if(pay){
					payList.add(groupMap);
					continue;
				}
				if(borrow){
					for(int i = 0;i<groupList.size();i++){
						loanList.add(groupList.get(i));
					}
					/*if(groupList.size()==1){
					}else{
						StringBuilder sb = new StringBuilder();
						for(int i = 0;i<groupList.size();i++){
							Map<String, Object> tMap = groupList.get(i);
							String str = tMap.get("rowNumber")!=null?tMap.get("rowNumber").toString():"";
							sb.append(str);
							if(i<groupList.size()-1){
								sb.append(",");	
							}
						}
						throw new IllegalArgumentException("第"+sb.toString()+"行，同一个票据编号有多条借款信息，票据编号为："+identifierNum);	
					}*/
					continue;
				}
			}
		}
	}

	private void savePaymentInfo(String crewId, List<Map<String, Object>> payList, Map<String, String> currencyMap,
			Map<String, String> payOrCollectPaymentWayMap, List<FinancePaymentWayModel> listPayWay,
			Map<String, String> tempMap, List<FinanceSubjectDto> subjectDtoList) throws Exception {
		//保存付款数据		
		if(payList!=null&&payList.size()>0){
			//付款信息表
			String insertPaymentSqlToPayment = "insert into tab_payment_info("
					+ "paymentId,"
					+ "receiptNo,"
					+ "paymentDate,"
					+ "payeeName,"
					+ "contractId,"
					+ "contractType,"
					+ "currencyId,"
					+ "totalMoney,"
					+ "paymentWay,"
					+ "hasReceipt,"
					+ "billCount,"
					+ "agent,"
					+ "status,"
					+ "createtime,"
					+ "crewId,"
					+ "department"
					+ ") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			//财务科目和付款信息对照表
			 String insertPayentFinanSubjMapSql = "insert into tab_payment_finanSubj_map(mapId,paymentId,financeSubjId,summary,money,crewId,financeSubjName) values(?,?,?,?,?,?,?)";
			List<Object[]> insertPaymentSqlToPaymentParams = new ArrayList<Object[]>();//插入主表的参数集合
			List<Object[]> insertPayentFinanSubjMapSqlParams = new ArrayList<Object[]>();//插入对照表的参数集合
			
			//有票/无票 票据号前缀
			String hasReceiptPrifix="",noReceiptPrifix="";
			//有票/无票 后缀
			int hasReceiptNum = 0,noReceiptNum=0;
			for(Map<String, Object> collectMap :payList){
				Set<Entry<String, Object>> entry = collectMap.entrySet();
				Iterator<Entry<String, Object>> eIt = entry.iterator();
				while(eIt.hasNext()){
					//一张付款单对应的多条付款信息
					//while 循环中往 tab_payment_info插入一条数据
					//for循环中组织往tab_payment_finanSubj_map 插入多条数据的参数
					
					Entry<String, Object> map = eIt.next();
					List<Map<String, Object>> listMap = (List<Map<String, Object>>)map.getValue();
					//tab_payment_info中字段值
					String paymentId = UUIDUtils.getId();//付款id
					String receiptNo = "0000";//票据编号
					String paymentDate = "";//操作时间
					String payeeName = "";//收款方名称
					String contractId = "";//合同id
					int contractType = 0;//合同类型
					String currencyId = "";//币种信息id
					double totleMoney = 0.00;//总金额
					String paymentWay = "";//支付方式,默认现金
					int hasReceipt = 0;//是否有票据
					int billCount = 0;//票据张数
					String agent = "";//经办人
					String department = ""; //部门
					//tab_payment_info中字段值
					boolean hasReceiveBill = false;//有无发票 for 获取票据编号
					int count = 1;
					for(Map<String, Object> innerMap :listMap){
						String rowNumber = 	innerMap.get("rowNumber")!=null?innerMap.get("rowNumber").toString():""; 
						//合同号
						String contractNo = innerMap.get("contractNo")!=null?innerMap.get("contractNo").toString():"";
						if(StringUtils.isNotBlank(contractNo)){
							//获取合同类型
							Map<String, Object> contractMap = queryContractInfoForContractType(crewId,contractNo,rowNumber);
							contractType = contractMap.get("contractType")!=null?Integer.valueOf(contractMap.get("contractType").toString()):0;
							contractId = contractMap.get("contractId")!=null?contractMap.get("contractId").toString():"";
							if(0==contractType){
								throw new IllegalArgumentException("合同号：【"+contractNo+"】在数据库中不存在");
							}
						}
						
						
						payeeName = innerMap.get("aimPersonName")!=null?innerMap.get("aimPersonName").toString():"";
						
						department = innerMap.get("department") != null?innerMap.get("department").toString():"";
						//操作日期
						paymentDate = innerMap.get("receiptDate")!=null?innerMap.get("receiptDate").toString():"";
						//有无发票
						String ifReceiveBill = innerMap.get("hasReceipt")!=null?innerMap.get("hasReceipt").toString():"";
						//发票张数
						try {
							billCount = innerMap.get("billCount")!=null?StringUtils.isNotBlank(innerMap.get("billCount").toString())?Integer.valueOf(innerMap.get("billCount").toString()):0:0;
						} catch (Exception e) {
							throw new IllegalArgumentException("第"+rowNumber+"行，票据张数必须为数字");
						}
						
						if(StringUtils.isNotBlank(ifReceiveBill)&&"有发票".equals(ifReceiveBill)){
							hasReceipt = 1;
							hasReceiveBill = true;
						} else {
							billCount = 0;
							hasReceiveBill = false;
						}
						//摘要
						String summary = innerMap.get("summary")!=null?innerMap.get("summary").toString():"";
						
						
						//付款金额
						String payMoney = innerMap.get("payedMoney")!=null?innerMap.get("payedMoney").toString().replace(",", "").replace("，", ""):"";
						String[] moneyInfo = getMoneyInfo(payMoney,currencyMap,rowNumber);
						
						//付款金额
						Double money = Double.parseDouble(moneyInfo[0]);
						totleMoney  += money;
						//货币id
						currencyId = moneyInfo[1];
						
						//获取剧组财务科目id
						String financeSubjName = innerMap.get("financeSubjName")!=null?innerMap.get("financeSubjName").toString():"";
						String financeSubjId = this.getFinanSubjIdByLevelNames(crewId, financeSubjName, rowNumber, subjectDtoList);
						
						if(StringUtils.isBlank(financeSubjId)){
							throw new IllegalArgumentException("当前剧组中不存在名为：["+financeSubjName+"]的财务科目层级");
						}
						//付款方式
						paymentWay=innerMap.get("paymentWay").toString();
						//经办人
						agent = innerMap.get("agent")!=null?innerMap.get("agent").toString():"";
						
						//中间表（tab_payment_finanSubj_map）参数信息
						Object[] paymentFinanSubjParams =new Object[7]; 
						paymentFinanSubjParams[0] = UUIDUtils.getId();//id
						paymentFinanSubjParams[1] = paymentId;//主表id
						paymentFinanSubjParams[2] = financeSubjId;//财务科目id
						paymentFinanSubjParams[3] = summary;//摘要
						paymentFinanSubjParams[4] = money;//金额
						paymentFinanSubjParams[5] = crewId;//剧组id
						paymentFinanSubjParams[6] = "";//财务科目名称
						insertPayentFinanSubjMapSqlParams.add(paymentFinanSubjParams);
					}
					
					//获取付款单   单据号
					DecimalFormat df = new DecimalFormat("000000");
					if(hasReceiveBill){
						if(StringUtils.isBlank(hasReceiptPrifix)){
							receiptNo = this.paymentInfoService.getNewReceiptNo(crewId, hasReceiveBill, paymentDate, null, true, true);
							String numberStr = receiptNo.substring(receiptNo.length()-6);
							hasReceiptPrifix =receiptNo.substring(0, receiptNo.length()-6);
							hasReceiptNum = Integer.parseInt(numberStr);
						}
						receiptNo = hasReceiptPrifix + df.format(hasReceiptNum);
						hasReceiptNum++;
					}else{
						if(StringUtils.isBlank(noReceiptPrifix)){
							receiptNo = this.paymentInfoService.getNewReceiptNo(crewId, hasReceiveBill, paymentDate, null, true, true);
							String numberStr = receiptNo.substring(receiptNo.length()-6);
							noReceiptPrifix =receiptNo.substring(0, receiptNo.length()-6);
							noReceiptNum = Integer.parseInt(numberStr);
						}
						receiptNo = noReceiptPrifix + df.format(noReceiptNum);
						noReceiptNum++;
					}
					if("0000".equals(receiptNo)){
						throw new IllegalArgumentException("获取单据编号失败");
					}
					
					//付款方式
					String wayid = payOrCollectPaymentWayMap.get(paymentWay);
					if(StringUtils.isBlank(wayid)){
						wayid = tempMap.get(paymentWay);
						if(StringUtils.isBlank(wayid)){
							wayid = UUIDUtils.getId();
							FinancePaymentWayModel financePaymentWayModel = new FinancePaymentWayModel();
							financePaymentWayModel.setWayId(wayid);
							financePaymentWayModel.setWayName(paymentWay);
							financePaymentWayModel.setCreateTime(new Timestamp(System.currentTimeMillis()+(count*1000)));
							financePaymentWayModel.setCrewId(crewId);
							listPayWay.add(financePaymentWayModel);
							tempMap.put(paymentWay,wayid);
						}
					}
					
					//主表（tab_payment_info）参数信息
					Object[] arg = new Object[16];
					arg[0] = paymentId;//'付款信息ID',
					arg[1] = receiptNo;//'票据编号',
					arg[2] = paymentDate;//'付款日期',
					arg[3] = payeeName;//'收款人单位名称'
					arg[4] = contractId;//'合同ID',
					arg[5] = contractType;//合同类型
					arg[6] = currencyId;//'币种ID',
					arg[7] = totleMoney;//'币种ID',
					arg[8] = wayid;//'财务付款方式',
					arg[9] = hasReceipt;//'有无发票。0：无；1：有',
					arg[10] = billCount;//'单据张数',
					arg[11] = agent;//'经办人',
					arg[12] = 0;//'状态。0：未结算；1：已结算',
					arg[13] = new Timestamp(System.currentTimeMillis()+(count*1000));//'创建时间',
					arg[14] = crewId;//'剧组ID',
					arg[15] = department;//部门
					insertPaymentSqlToPaymentParams.add(arg);
				}
			}
			//保存主表数据 
			paymentInfoDao.getJdbcTemplate().batchUpdate(insertPaymentSqlToPayment, insertPaymentSqlToPaymentParams);
			//保存中间表数据
			paymentInfoDao.getJdbcTemplate().batchUpdate(insertPayentFinanSubjMapSql, insertPayentFinanSubjMapSqlParams);
		}
	}

	/**
	 * 保存借款信息
	 * 
	 * @param crewId 借款信息
	 * @param loanList 借款信息数据
	 * @param currencyMap  当前剧组货币信息
	 * @throws Exception
	 */
	private void saveloanInfo(String crewId, List<Map<String, Object>> loanList, Map<String, String> currencyMap, List<FinanceSubjectDto> subjectDtoList)
			throws Exception {
		//保存借款数据
		if(loanList!=null&&loanList.size()>0){
			String sql = "insert into tab_loan_info("
					+ "loanId,"
					+ "receiptNo,"
					+ "loanDate,"
					+ "payeeName,"
					+ "summary,"
					+ "money,"
					+ "currencyId,"
					+ "paymentWay,"
					+ "agent,"
					+ "createTime,"
					+ "crewId,"
					+ "financeSubjId,"
					+ "financeSubjName) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			// 票据号前缀
			String receiptPrifix="";
			// 后缀
			int receiptNum = 0;
			List<Object[]> params = new ArrayList<Object[]>();
			int count = 1;
			for(Map<String, Object> innerMap :loanList){
				String rowNumber = 	innerMap.get("rowNumber")!=null?innerMap.get("rowNumber").toString():""; 
				String receiptNo = "00000000";
				String loanDate = innerMap.get("receiptDate")!=null?innerMap.get("receiptDate").toString():"";
				if(StringUtils.isBlank(receiptPrifix)){
					receiptNo = this.loanInfoService.getNewReceiptNo(crewId, loanDate, null);
					receiptNo = receiptNo.replaceAll("-", "");
					receiptPrifix = receiptNo.substring(0,receiptNo.length()-8);
					receiptNum = Integer.valueOf(receiptNo.substring(receiptNo.length()-8));
				}
				DecimalFormat df = new DecimalFormat("00000000");
				receiptNo = receiptPrifix + df.format(receiptNum);
				receiptNum++;
				String collectionMoneyStr = innerMap.get("payedMoney").toString().replace(",", "").replace("，", "");
				//获取 金额  以及货币id
				String[] moneyInfo = getMoneyInfo(collectionMoneyStr,currencyMap,rowNumber);
				
				//获取剧组财务科目id
				String financeSubjName = innerMap.get("financeSubjName")!=null?innerMap.get("financeSubjName").toString():"";
				String financeSubjId = this.getFinanSubjIdByLevelNames(crewId, financeSubjName, rowNumber, subjectDtoList);
				
				if(StringUtils.isBlank(financeSubjId)){
					throw new IllegalArgumentException("当前剧组中不存在名为：["+financeSubjName+"]的财务科目层级");
				}
				Object[] arg = new Object[13];
				arg[0] = UUIDUtils.getId();//id
				arg[1] = receiptNo;//借款单票据编号
				arg[2] = loanDate;//借款日期
				arg[3] = innerMap.get("aimPersonName");//借款人
				arg[4] = innerMap.get("summary");//摘要
				arg[5] = moneyInfo[0];//金额
				arg[6] = moneyInfo[1];//货币id
				String paymentWayStr = innerMap.get("paymentWay").toString();
				if (!paymentWayStr.equals("现金") && !paymentWayStr.equals("现金（网转）") && !paymentWayStr.equals("银行")) {
					throw new IllegalArgumentException("第" + rowNumber + "行付款方式只能为【现金,现金（网转）,银行】之一");
				}
				arg[7] = LoanPaymentWay.nameOf(paymentWayStr).getValue();//付款方式
				arg[8] = innerMap.get("agent");//记账人
				arg[9] = new Timestamp(System.currentTimeMillis()+(count*1000));
				arg[10] = crewId;//剧组id
				arg[11] = financeSubjId;//财务科目id
				arg[12] = "";//财务科目名称
				params.add(arg);
			}
			paymentInfoDao.getJdbcTemplate().batchUpdate(sql, params);
		}
	}

	private void savePayOrCollectPayment(List<FinancePaymentWayModel> listPayWay) throws Exception {
		//保存不存在的付款方式
		if(listPayWay!=null&&listPayWay.size()>0){
			financePaymentWayDao.addBatch(listPayWay, FinancePaymentWayModel.class);
		}
	}

	/**
	 * 保存收款信息
	 * 
	 * @param crewId 剧组id
	 * @param collectList  收款信息
	 * @param currencyMap  当前剧组货币信息
	 * @param payOrCollectPaymentWayMap   当前剧组存在的付款、收款方式
	 * @param listPayWay  需要存储到数据库中的当前剧组付款、收款方式
	 * @param tempMap   临时存放需要存储的付款方式信息  （主要是为了避免重复存储付款信息）
	 * @throws Exception
	 */
	private void saveCollectInfo(String crewId, List<Map<String, Object>> collectList, Map<String, String> currencyMap,
			Map<String, String> payOrCollectPaymentWayMap, List<FinancePaymentWayModel> listPayWay,
			Map<String, String> tempMap) throws Exception {
		//保存收款数据
		if(collectList!=null&&collectList.size()>0){
			String sql = "insert into tab_collection_info("
					+ "collectionId,"
					+ "receiptNo,"
					+ "collectionDate,"
					+ "otherUnit,"
					+ "summary,"
					+ "money,"
					+ "currencyId,"
					+ "paymentWay,"
					+ "agent,"
					+ "createtime,"
					+ "crewId) values(?,?,?,?,?,?,?,?,?,?,?)";
			List<Object[]> params = new ArrayList<Object[]>();
			//票据号前缀
			String receiptPrifix="";
			//后缀
			int receiptNum=0;
			int count = 1;
			for(Map<String, Object> innerMap :collectList){
				String rowNumber = 	innerMap.get("rowNumber")!=null?innerMap.get("rowNumber").toString():""; 
				
				String receiptNo = "00000000";//票据编号
				String collectDate = innerMap.get("receiptDate")!=null?innerMap.get("receiptDate").toString():"";
				//获取付款单   单据号
				DecimalFormat df = new DecimalFormat("00000000");
				if(StringUtils.isBlank(receiptPrifix)){
					receiptNo = this.collectionInfoService.getNewReceiptNo(crewId, collectDate, null);
					String numberStr = receiptNo.substring(receiptNo.length()-8);
					receiptPrifix =receiptNo.substring(0, receiptNo.length()-8);
					receiptNum = Integer.parseInt(numberStr);
				}
				receiptNo = receiptPrifix + df.format(receiptNum);
				receiptNum++;
				
				if("00000000".equals(receiptNo)){
					throw new IllegalArgumentException("获取单据编号失败");
				}
				
				//获取票据编号
				Object[] arg = new Object[11];
				arg[0] = UUIDUtils.getId();
				arg[1] = receiptNo;//票据编号
				arg[2] = collectDate;//收款日期
				arg[3] = innerMap.get("aimPersonName");//操作人
				arg[4] = innerMap.get("summary");//摘要
				String collectMoneyStr = innerMap.get("collectMoney").toString().replace(",", "").replace("，", "");
				//获取 金额  以及货币id
				String[] moneyInfo = getMoneyInfo(collectMoneyStr,currencyMap,rowNumber);
				arg[5] = moneyInfo[0];//金额
				arg[6] = moneyInfo[1];//货币id
				//付款方式
				String wayName = innerMap.get("paymentWay").toString();//付款方式名称
				String paymentWay = payOrCollectPaymentWayMap.get(wayName);//付款方式id
				if(StringUtils.isBlank(paymentWay)){
					//判断临时map中是否有付款方式信息
					paymentWay = tempMap.get(wayName);
					if(StringUtils.isBlank(paymentWay)){
						paymentWay = UUIDUtils.getId();
						FinancePaymentWayModel financePaymentWayModel = new FinancePaymentWayModel();
						financePaymentWayModel.setWayId(paymentWay);
						financePaymentWayModel.setWayName(wayName);
						financePaymentWayModel.setCreateTime(new Timestamp(System.currentTimeMillis()+(count*1000)));
						financePaymentWayModel.setCrewId(crewId);
						listPayWay.add(financePaymentWayModel);
						tempMap.put(wayName, paymentWay);
					}
				}
				arg[7] = paymentWay;//付款方式
				arg[8] = innerMap.get("agent");//记账人
				arg[9] = new Timestamp(System.currentTimeMillis()+(count*1000));//系统时间
				arg[10] = crewId;//剧组id
				params.add(arg);
			}
			paymentInfoDao.getJdbcTemplate().batchUpdate(sql, params);
		}
	}

	/**
	 * 根据剧组id获取有效的货币信息   key:货币code  value:货币id
	 * @param crewId 剧组id
	 * @return
	 */
	private Map<String, String> queryCrewCurrencyCodeIdByCrewId(String crewId) {
		Map<String, String> currencyMap = new HashMap<String, String>();
		List<CurrencyInfoModel> currencyList = currencyInfoService.queryCurrencyInfoByCrewId(crewId);
		if(currencyList==null || currencyList.size() == 0){
			throw new IllegalArgumentException("该剧组没有有效的货币，请现在[费用管理-财务设置-币种设置]中设置币种信息");
		}
		for(CurrencyInfoModel currencyInfo : currencyList){
			String code = currencyInfo.getCode();
			String id = currencyInfo.getId();
			currencyMap.put(code, id);
		}
		return currencyMap;
	}
	
	/**
	 * 将list中的数据封装到map中
	 * @param list   数据
	 * @param importColoum  对应的列名和字段名
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private List<Map<String, Object>> listToMap(List<ArrayList<String>> list,Map<String, String> importColoum) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		List<Map<String, Object>> back= new ArrayList<Map<String, Object>>();
		
		Map<String, Object> dataMap =null;
		
		Set<String> keys = importColoum.keySet();
		for(int m=0;m<list.size();m++){
			List<String> inlist = list.get(m);
			int n = 0;
			dataMap = new LinkedHashMap<String, Object>();
			Iterator<String> it =keys.iterator();
			while(it.hasNext()){
				String key = it.next();
				String value = importColoum.get(key);
				String listValue = inlist.get(n);
				dataMap.put(value, listValue);
				n++;
			}
			back.add(dataMap);
		}
		return back;
	}
	
	
	/**
	 * 根据财务科目级别名称获取财务科目id
	 * @param levelNames  财务科目名称（按层级）例：筹备期-工作人员差旅费-编剧差旅费
	 * @param rowNumber  行号
	 * @return  返回子节点财务科目id
	 */
	public String queryFinanceSubjectIdByLevelNames(String crewId,String levelNames,String rowNumber){
		//财务科目子节点id
		String id = "";
		
		if(StringUtils.isBlank(levelNames)){
			throw new IllegalArgumentException("第"+rowNumber+"行，财务科目层级名称为空");
		}
		String[] levelNameArray = levelNames.split("-");
		if(levelNameArray==null||levelNameArray.length==0){
			throw new IllegalArgumentException("第"+rowNumber+"行，财务科目层级名称为空");
		}
		
		StringBuilder sql = new StringBuilder("select o0.id from ");
		
		for(int i =0;i< levelNameArray.length;i++){
			sql.append(" tab_finance_subject o"+i);
			if(i<levelNameArray.length-1){
				sql.append(" ,");
			}
		}
		sql.append(" where o0.crewId = '"+crewId+"' and ");
		for(int i =0;i< levelNameArray.length;i++){
			if(i<levelNameArray.length-1){
				sql.append(" o"+i+".parentid=o"+(i+1)+".id and");
			}
		}
		int n = 0;
		for(int i =levelNameArray.length-1;i>=0;i--){
			sql.append(" o"+i+".name='"+levelNameArray[n++]+"'");
			if(i>0){
				sql.append(" and ");
			}
		}
		
		if(levelNameArray.length==1){
			sql.append(" and o0.parentid = '0' ");
		}
		
		List<Map<String, Object>> list = getCostDao.getJdbcTemplate().queryForList(sql.toString());
		if(list==null||list.size()==0){
			throw new IllegalArgumentException("第"+rowNumber+"行，财务科目名称不存在:"+levelNames);
		}
		if(list.size()>1){
			throw new IllegalArgumentException("第"+rowNumber+"行，财务科目名称不能确定唯一:"+levelNames);
		}
		Map<String, Object> map = list.get(0);
		if(map!=null){
			id = map.get("id")!=null?map.get("id").toString():"";
		}
		return id;
	}
	
	/**
	 * @param crewId
	 * @param levelNames
	 * @param rowNumber
	 * @return
	 */
	public String getFinanSubjIdByLevelNames(String crewId, String finanSubjName, String rowNumber, List<FinanceSubjectDto> subjectDtoList){
		// 财务科目子节点id
		String id = "";

		if (StringUtils.isBlank(finanSubjName)) {
			throw new IllegalArgumentException("第" + rowNumber + "行，财务科目名称为空");
		}
		String[] levelNameArray = finanSubjName.split("-");
		if (levelNameArray == null || levelNameArray.length == 0) {
			throw new IllegalArgumentException("第" + rowNumber + "行，财务科目名称为空");
		}

		List<FinanceSubjectDto> finanSubjList = this.financeSubjectService.getFinanceSubjByName(finanSubjName);
		if (finanSubjList.size() == 0) {
			throw new IllegalArgumentException("第" + rowNumber + "行，财务科目在系统中找不到对应的明细科目");
		}
		if (finanSubjList.size() == 1) {
			id = finanSubjList.get(0).getId();
		} else {
			//需要用户自主选择确定财务科目
			id = "needChoose";
		}

		return id;
	}
	
	
	/**
	 * 获取金额、货币id
	 * 
	 * @param payMoney  金额字段（付款、收款、借款）
	 * @param currencyMap  该剧组的货币信息   key:货币code   value: 货币id
	 * @param rowNumber 数据行号
	 * @return
	 */
	private String[] getMoneyInfo(String payMoney,Map<String, String> currencyMap,String rowNumber){
		boolean  singleCurrencyFlag = false;	//标识剧组中是否只有一个有效币种
		if (currencyMap.size() == 1) {
			singleCurrencyFlag = true;
		}
		
		String[] moneyInfo = new String[2];
		if(StringUtils.isBlank(payMoney)){
			throw new IllegalArgumentException("第"+rowNumber+"行，对应的财务类型金额数据异常");
		}
		
		payMoney = payMoney.replace(",", "").replace("，", "");
		
		//获取币种ID
		String currencyId = "";
		if (singleCurrencyFlag) {
			Set<String> currencySet = currencyMap.keySet();
			for (String currencyCode : currencySet) {
				currencyId = currencyMap.get(currencyCode);
			}
		} else {
			String currencyCode = "";
			Pattern pattern1 = Pattern.compile(Constants.REGEX_EXCEL_TITLE);
			Matcher matcher1 = pattern1.matcher(payMoney);
			if(matcher1.find()){
				currencyCode = matcher1.group(1);
			}
			if(StringUtils.isBlank(currencyCode)){
				throw new IllegalArgumentException("第"+rowNumber+"行，付款中货币编码为空");
			}
			currencyId = currencyMap.get(currencyCode);
			if(StringUtils.isBlank(currencyId)){
				throw new IllegalArgumentException("该剧组中没有有效的货币信息["+currencyCode+"]");
			}
		}
		
		//获取金额
		String payMoneyStr = "";
		Pattern pattern = Pattern.compile(Constants.REGEX_EXCEL);
		Matcher matcher = pattern.matcher(payMoney);
		
		if (matcher.find()) {
			payMoneyStr = matcher.group(1);
		} else if (singleCurrencyFlag) {
			payMoneyStr = payMoney;
		}
		if (StringUtils.isBlank(payMoneyStr)) {
			throw new IllegalArgumentException("第"+rowNumber+"行，对应的财务类型金额数据异常");
		}
		
		moneyInfo[0] = payMoneyStr;
		moneyInfo[1] = currencyId;
		return moneyInfo;
	}
	
	/**
	 * 
	 * 根据剧组id获取当前剧组可用的付款方式
	 * @param crewId
	 * @return
	 */
	private Map<String, String> queryPayOrCollectPaymentWay(String crewId){
		Map<String, String> map = new HashMap<String, String>();
		List<FinancePaymentWayModel> list = this.financePaymentWayDao.queryByCrewId(crewId);
		for(FinancePaymentWayModel paymentWay :list){
			String wayId = paymentWay.getWayId();
			String wayName = paymentWay.getWayName();
			map.put(wayName, wayId);
		}
		return map;
	}

	/**
	 * 查询财务流水账信息（含有付款、借款、收款信息）
	 * 放在一个sql中便于分页
	 * @param crewId
	 * @param paymentInfoFilter	付款单过滤条件
	 * @param collectionFilter	收款单过滤条件
	 * @param loanInfoFilter	借款单过滤条件
	 * @param includePayment	是否包含付款单
	 * @param includeCollection	是否包含收款单
	 * @param includeLoan	是否包含借款单
	 * @return 
	 * receiptId 单据ID
	 * receiptDate	单据日期
	 * createTime 单据创建日期
	 * receiptNo	单据编号
	 * summary	摘要
	 * financeSubjId	财务科目ID
	 * financeSubjName	财务科目名称
	 * collectMoney	收款金额
	 * payedMoney	付款金额
	 * status	付款单状态， '0'：未结算；'1'：已结算；'/'：无意义
	 * formType	单据类型， 1-付款单  2-收款单  3-借款单
	 * aimPersonName	收/付/借款人
	 * paymentWay	支付方式
	 * hasReceipt	付款单是否有发票，'0'：没有；'1'：有；'/'：无意义
	 * billCount  	付款单票据张数，'/'：无意义
	 * agent	记账人
	 * currencyId	币种ID
	 * currencyCode	币种编码
	 * currencyName	币种名称
	 * exchangeRate	币种汇率
	 * contractNo	关联的合同编码
	 * contractName	关联的合同名称
	 */
	public List<Map<String, Object>> queryFinanceRunningAccount(String crewId, boolean includePayment, 
			boolean includeCollection, boolean includeLoan, 
			PaymentInfoFilter paymentInfoFilter,  
			CollectionInfoFilter collectionFilter, 
			LoanInfoFilter loanInfoFilter, Page page, boolean isASC, Integer sortType) {
		return this.getCostDao.queryFinanceRunningAccount(crewId, includePayment, 
				includeCollection, includeLoan, 
				paymentInfoFilter, collectionFilter, 
				loanInfoFilter, page, isASC, sortType);
	}
	
	/**
	 * 查询财务流水账（含有付款、借款、收款信息）金额统计信息
	 * @param crewId
	 * @param paymentInfoFilter	付款单过滤条件
	 * @param collectionFilter	收款单过滤条件
	 * @param loanInfoFilter	借款单过滤条件
	 * @param includePayment	是否包含付款单
	 * @param includeCollection	是否包含收款单
	 * @param includeLoan	是否包含借款单
	 * @return 所有付款、所有收款
	 * 
	 */
	public List<Map<String, Object>> queryFinanceRunningAccountTotalMoney(String crewId, boolean includePayment, 
			boolean includeCollection, boolean includeLoan, 
			PaymentInfoFilter paymentInfoFilter,  
			CollectionInfoFilter collectionFilter, 
			LoanInfoFilter loanInfoFilter, Page page) {
		return this.getCostDao.queryFinanceRunningAccountTotalMoney(crewId, includePayment, includeCollection, 
				includeLoan, paymentInfoFilter, 
				collectionFilter, loanInfoFilter, page);
	}
}
