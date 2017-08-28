package com.xiaotu.makeplays.mobile.server.finance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.finance.controller.filter.CollectionInfoFilter;
import com.xiaotu.makeplays.finance.controller.filter.LoanInfoFilter;
import com.xiaotu.makeplays.finance.controller.filter.PaymentInfoFilter;
import com.xiaotu.makeplays.finance.model.CollectionInfoModel;
import com.xiaotu.makeplays.finance.model.CurrencyInfoModel;
import com.xiaotu.makeplays.finance.model.FinancePaymentWayModel;
import com.xiaotu.makeplays.finance.model.LoanInfoModel;
import com.xiaotu.makeplays.finance.model.PaymentInfoModel;
import com.xiaotu.makeplays.finance.model.constants.ContractType;
import com.xiaotu.makeplays.finance.model.constants.LoanPaymentWay;
import com.xiaotu.makeplays.finance.service.CollectionInfoService;
import com.xiaotu.makeplays.finance.service.CurrencyInfoService;
import com.xiaotu.makeplays.finance.service.FinancePaymentWayService;
import com.xiaotu.makeplays.finance.service.FinanceSubjectService;
import com.xiaotu.makeplays.finance.service.GetCostService;
import com.xiaotu.makeplays.finance.service.LoanInfoService;
import com.xiaotu.makeplays.finance.service.PaymentInfoService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.mobile.server.common.BaseFacade;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.StringUtil;

/**
 * 收支管理相关接口
 * @author xuchangjian 2016-10-12上午9:50:35
 */
@Controller
@RequestMapping("/interface/getCostFacade")
public class GetCostFacade extends BaseFacade{

	Logger logger = LoggerFactory.getLogger(FinanceFacade.class);
	
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private FinanceSubjectService financeSubjectService;
	
	@Autowired
	private PaymentInfoService paymentInfoService;
	
	@Autowired
	private CollectionInfoService collectionInfoService;
	
	@Autowired
	private LoanInfoService loanInfoService;
	
	@Autowired
	private CurrencyInfoService currencyInfoService;
	
	@Autowired
	private FinancePaymentWayService financePaymentWayService;
	
	@Autowired
	private GetCostService getCostService;
	
	/**
	 * 查询财务流水账
	 * @param crewId	剧组ID
	 * @param userId	用户ID
	 * @param pageNo	当前页数
	 * @param pageSize	每页显示条数
	 * @param financeSubjIds	财务科目ID，多个以逗号隔开
	 * @param aimPeopleNames	收/付款人，多个以逗号隔开
	 * @param startAimDate	最小票据日期
	 * @param endAimDate	最大票据日期
	 * @param formTypes	单据类型： 1-付款单  2-收款单  3-借款单，多个以逗号隔开
	 * @param hasReceipt 是否有发票
	 * @param status	结算状态：0-未结算  1-已结算
	 * @param summary	摘要
	 * @param minMoney	最小金额
	 * @param maxMoney	最大金额
	 * @param isQueryFinanceSubjPayment 是否是查询财务科目支付明细
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainFinanceRunnigAccount")
	public Object obtainFinanceRunnigAccount(HttpServletRequest request,
			String crewId, String userId, Integer pageNo, Integer pageSize,
			String financeSubjIds, String aimPeopleNames, String startAimDate,
			String endAimDate, String formTypes, Boolean hasReceipt,
			Integer status, String summary, Double minMoney, Double maxMoney,
			String paymentWayId, boolean isQueryFinanceSubjPayment) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			
			//默认分页
			if (pageNo == null || pageSize == null || pageNo < 1 || pageSize < 0) {
				pageNo = 1;
				pageSize = 20;
			}
			
			List<Integer> formTypeList = new ArrayList<Integer>();
			if (!StringUtils.isBlank(formTypes)) {
				String[] formTypeArray = formTypes.split(",");
				for (String formType : formTypeArray) {
					formTypeList.add(Integer.parseInt(formType));
				}
			}
			
			this.financeSubjectService.refreshCachedSubjectList(crewId);
			
			boolean includePayment = false;
			boolean includeCollection = false;
			boolean includeLoan = false;
			if (StringUtils.isBlank(formTypes) || formTypeList.contains(1)) {
				includePayment = true;
			}
			if (StringUtils.isBlank(formTypes) || formTypeList.contains(2)) {
				includeCollection = true;
			}
			if (StringUtils.isBlank(formTypes) || formTypeList.contains(3)) {
				includeLoan = true;
			}
			if (hasReceipt != null) {
				includeCollection = false;
				includeLoan = false;
			}
			if (status != null) {
				includeCollection = false;
				includeLoan = false;
			}
			if (!StringUtils.isBlank(financeSubjIds)) {
				includeCollection = false;
			}
			
			//付款单过滤条件
			PaymentInfoFilter paymentInfoFilter = new PaymentInfoFilter();
			paymentInfoFilter.setFinanceSubjIds(financeSubjIds);
			paymentInfoFilter.setPayeeNames(aimPeopleNames);
			paymentInfoFilter.setStartPaymentDate(startAimDate);
			paymentInfoFilter.setEndPaymentDate(endAimDate);
			paymentInfoFilter.setHasReceipt(hasReceipt);
			paymentInfoFilter.setStatus(status);
			paymentInfoFilter.setSummary(summary);
			paymentInfoFilter.setMinMoney(minMoney);
			paymentInfoFilter.setMaxMoney(maxMoney);
			paymentInfoFilter.setPaymentWayId(paymentWayId);
			paymentInfoFilter.setQueryFinanceSubjPayment(isQueryFinanceSubjPayment);
			
			//收款单过滤条件
			CollectionInfoFilter collectionFilter = new CollectionInfoFilter();
			collectionFilter.setOtherUnits(aimPeopleNames);
			collectionFilter.setStartCollectionDate(startAimDate);
			collectionFilter.setEndCollectionDate(endAimDate);
			collectionFilter.setSummary(summary);
			collectionFilter.setMinMoney(minMoney);
			collectionFilter.setMaxMoney(maxMoney);
			collectionFilter.setPaymentWayId(paymentWayId);
			
			//借款单过滤条件
			LoanInfoFilter loanInfoFilter = new LoanInfoFilter();
			loanInfoFilter.setFinanceSubjIds(financeSubjIds);
			loanInfoFilter.setPayeeNames(aimPeopleNames);
			loanInfoFilter.setSummary(summary);
			loanInfoFilter.setMinMoney(minMoney);
			loanInfoFilter.setMaxMoney(maxMoney);
			loanInfoFilter.setStartLoanDate(startAimDate);
			loanInfoFilter.setEndLoanDate(endAimDate);
			loanInfoFilter.setPaymentWayId(paymentWayId);
			
			Page page = new Page();
			page.setPageNo(pageNo);
			page.setPagesize(pageSize);
			
			List<Map<String, Object>> runningAccountList = this.getCostService.queryFinanceRunningAccount(crewId, includePayment, 
					includeCollection, includeLoan, 
					paymentInfoFilter, collectionFilter, 
					loanInfoFilter, page, false, 0);
			
			//处理财务科目
			for (Map<String, Object> runningAccountMap : runningAccountList) {
				String financeSubjId = (String) runningAccountMap.get("financeSubjId");
					
				String financeSubjNames = "";
				//获取财务科目名称
				if (!StringUtils.isBlank(financeSubjId)) {
					financeSubjNames = "";
					String[] financeSubjIdArray = financeSubjId.split(",");
					for (String myFinanceSubjId : financeSubjIdArray) {
						financeSubjNames += this.financeSubjectService.getFinanceSubjName(myFinanceSubjId) + " | ";
					}
					
					runningAccountMap.put("financeSubjName", financeSubjNames.substring(0, financeSubjNames.length() - 3));
				}
				
				//票据日期
				Date receiptDate = (Date) runningAccountMap.get("receiptDate");
				runningAccountMap.put("receiptDate", this.sdf1.format(receiptDate));
				
				Date createTime = (Date) runningAccountMap.get("createTime");
				runningAccountMap.put("createTime", this.sdf1.format(createTime));
			}
			
			
//			for (Map<String, Object> map : runningAccountList) {
//				//票据日期
//				Date receiptDate = (Date) map.get("receiptDate");
//				map.put("receiptDate", this.sdf1.format(receiptDate));
//			}

			CurrencyInfoModel standardCurrency = this.currencyInfoService.queryStandardCurrency(crewId);
			if (standardCurrency == null) {
				standardCurrency = this.currencyInfoService.initFirstCurrency(crewId);
	    	}
			
			
			//金额统计信息
			List<Map<String, Object>> currencyMoneyStatistic = this.getCostService.queryFinanceRunningAccountTotalMoney(crewId, includePayment, 
					includeCollection, includeLoan, 
					paymentInfoFilter, collectionFilter, 
					loanInfoFilter, null);
			
			Double totalCollectMoney = 0.0;	//总收款
			Double totalPayedMoney = 0.0;	//总付款
			for (Map<String, Object> currencyTotalMoneyInfo : currencyMoneyStatistic) {
//				Double myTotalCollectMoney = (Double) currencyTotalMoneyInfo.get("totalCollectMoney");
//				Double myTotalPayedMoney = (Double) currencyTotalMoneyInfo.get("totalPayedMoney");
//				
//				totalCollectMoney = BigDecimalUtil.add(totalCollectMoney, BigDecimalUtil.multiply(myTotalCollectMoney, exchangeRate));
//				totalPayedMoney = BigDecimalUtil.add(totalPayedMoney, BigDecimalUtil.multiply(myTotalPayedMoney, exchangeRate));
				
				Double exchangeRate = (Double) currencyTotalMoneyInfo.get("exchangeRate");
				Double myTotalCollectMoney = Double.parseDouble((currencyTotalMoneyInfo.get("totalCollectMoney").toString()));
				Double myTotalPayedMoney = Double.parseDouble(currencyTotalMoneyInfo.get("totalPayedMoney").toString());
				Double myTotalForLoanMoney = 0.0;
				if (currencyTotalMoneyInfo.get("totalForLoanMoney") != null) {
					myTotalForLoanMoney = Double.parseDouble(currencyTotalMoneyInfo.get("totalForLoanMoney").toString());
				}
				
				myTotalCollectMoney = BigDecimalUtil.multiply(myTotalCollectMoney, exchangeRate);
				myTotalPayedMoney = BigDecimalUtil.multiply(myTotalPayedMoney, exchangeRate);
				myTotalForLoanMoney = BigDecimalUtil.multiply(myTotalForLoanMoney, exchangeRate);
				
				if (includeLoan) {
					myTotalPayedMoney = BigDecimalUtil.subtract(myTotalPayedMoney, myTotalForLoanMoney);
				}
				
				totalCollectMoney = BigDecimalUtil.add(totalCollectMoney, BigDecimalUtil.multiply(myTotalCollectMoney, exchangeRate));
				totalPayedMoney = BigDecimalUtil.add(totalPayedMoney, BigDecimalUtil.multiply(myTotalPayedMoney, exchangeRate));
			}
			
			boolean  singleCurrencyFlag= false;	//标识剧组中是否只有一个有效币种
			Map<String, Object> conditionMap = new HashMap<String, Object>();
	    	conditionMap.put("crewId", crewId);
	    	conditionMap.put("ifEnable", true);
	    	List<CurrencyInfoModel> currencyInfoList = this.currencyInfoService.queryManyByMutiCondition(conditionMap, null);
			if (currencyInfoList.size() == 1) {
				singleCurrencyFlag = true;
			}

			resultMap.put("singleCurrencyFlag", singleCurrencyFlag);
			resultMap.put("totalCollectMoney", totalCollectMoney);
			resultMap.put("totalPayedMoney", totalPayedMoney);
			resultMap.put("currencyCode", standardCurrency.getCode());
			resultMap.put("totalCount", page.getTotal());
			resultMap.put("runningAccountList", runningAccountList);
			
			resultMap.put("pageCount", page.getPageCount());
			
			this.sysLogService.saveSysLogForApp(request, "查询财务流水账", userInfo.getClientType(), 
					PaymentInfoModel.TABLE_NAME + "," + CollectionInfoModel.TABLE_NAME + "," + LoanInfoModel.TABLE_NAME, null, 0);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常", e);
			this.sysLogService.saveSysLogForApp(request, "查询财务流水账失败：" + e.getMessage(), userInfo.getClientType(), 
					PaymentInfoModel.TABLE_NAME + "," + CollectionInfoModel.TABLE_NAME + "," + LoanInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常", e);
		}

		return resultMap;
	}
	
	/**
	 * 根据借款单生成流水账单
	 * @param loanInfoList	借款信息列表
	 * @param subjectList	财务科目列表
	 * @return
	 */
	private List<Map<String, Object>> genRunningAccountByLoanInfoList(List<Map<String, Object>> loanInfoList) {
		
		List<Map<String, Object>> runningAccountList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : loanInfoList) {
			Map<String, Object> singleAccountMap = new HashMap<String, Object>();
			
			String loanId = (String) map.get("loanId");
			Date loanDate = (Date) map.get("loanDate");
			Date createTime = (Date) map.get("createTime");
			String receiptNo = (String) map.get("receiptNo");
			String summary = (String) map.get("summary");
			String financeSubjId = (String) map.get("financeSubjId");
			String financeSubjName = (String) map.get("financeSubjName");
			Double money = (Double) map.get("money");
			String payeeName = (String) map.get("payeeName");
			Integer paymentWay = (Integer) map.get("paymentWay");
			String agent = (String) map.get("agent");
			String currencyId = (String) map.get("currencyId");
			String currencyCode = (String) map.get("currencyCode");
			Double exchangeRate = (Double) map.get("exchangeRate");
			
			singleAccountMap.put("receiptId", loanId);
			singleAccountMap.put("receiptDate", loanDate);
			singleAccountMap.put("createTime", createTime);
			singleAccountMap.put("receiptNo", receiptNo);
			singleAccountMap.put("summary", summary);
			
			if (!StringUtils.isBlank(financeSubjId)) {
				financeSubjName = this.financeSubjectService.getFinanceSubjName(financeSubjId);
			}
			singleAccountMap.put("financeSubjName", financeSubjName);
			
			singleAccountMap.put("contractNo", null);
			singleAccountMap.put("contractName", null);
			singleAccountMap.put("collectMoney", 0.00);
			singleAccountMap.put("payedMoney", money);
			singleAccountMap.put("status", "/");
			singleAccountMap.put("formType", 3);
			singleAccountMap.put("aimPersonName", payeeName);
			singleAccountMap.put("paymentWay", LoanPaymentWay.valueOf(paymentWay).getName());
			singleAccountMap.put("hasReceipt", "/");
			singleAccountMap.put("billCount", "/");
			singleAccountMap.put("agent", agent);
			singleAccountMap.put("currencyId", currencyId);
			singleAccountMap.put("currencyCode", currencyCode);
			singleAccountMap.put("exchangeRate", exchangeRate);
			
			runningAccountList.add(singleAccountMap);
			
		}
		
		return runningAccountList;
	}
	
	/**
	 * 根据收款单生成流水账单
	 * @param collectionList	收款单列表
	 * @param subjectList	财务科目列表
	 * @return
	 */
	private List<Map<String, Object>> genRunningAccountByCollectionList(List<Map<String, Object>> collectionList) {
		
		List<Map<String, Object>> runningAccountList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : collectionList) {
			Map<String, Object> singleAccountMap = new HashMap<String, Object>();
			
			String collectionId = (String) map.get("collectionId");
			Date collectionDate = (Date) map.get("collectionDate");
			Date createTime = (Date) map.get("createTime");
			String receiptNo = (String) map.get("receiptNo");
			String summary = (String) map.get("summary");
			Double money = (Double) map.get("money");
			String otherUnit = (String) map.get("otherUnit");
			String paymentWay = (String) map.get("paymentWay");
			String agent = (String) map.get("agent");
			String currencyId = (String) map.get("currencyId");
			String currencyCode = (String) map.get("currencyCode");
			Double exchangeRate = (Double) map.get("exchangeRate");
			
			singleAccountMap.put("receiptId", collectionId);
			singleAccountMap.put("receiptDate", collectionDate);
			singleAccountMap.put("createTime", createTime);
			singleAccountMap.put("receiptNo", receiptNo);
			singleAccountMap.put("summary", summary);
			singleAccountMap.put("financeSubjName", "");
			singleAccountMap.put("contractNo", null);
			singleAccountMap.put("contractName", null);
			singleAccountMap.put("collectMoney", money);
			singleAccountMap.put("payedMoney", 0.00);
			singleAccountMap.put("status", "/");
			singleAccountMap.put("formType", 2);
			singleAccountMap.put("aimPersonName", otherUnit);
			singleAccountMap.put("paymentWay", paymentWay);
			singleAccountMap.put("hasReceipt", "/");
			singleAccountMap.put("billCount", "/");
			singleAccountMap.put("agent", agent);
			singleAccountMap.put("currencyId", currencyId);
			singleAccountMap.put("currencyCode", currencyCode);
			singleAccountMap.put("exchangeRate", exchangeRate);
			
			runningAccountList.add(singleAccountMap);
		}
		
		return runningAccountList;
	}
	
	/**
	 * 根据付款单列表生成流水账单
	 * @param paymentList	付款单列表
	 * @param subjectList 财务科目列表
	 * @return
	 */
	private List<Map<String, Object>> genRunningAccountByPaymentList(List<Map<String, Object>> paymentList) {
		
		List<Map<String, Object>> runningAccountList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : paymentList) {
			Map<String, Object> singleAccountMap = new HashMap<String, Object>();
			
			String paymentId = (String) map.get("paymentId");
			Date paymentDate = (Date) map.get("paymentDate");
			Date createTime = (Date) map.get("createTime");
			String receiptNo = (String) map.get("receiptNo");
			String summary = (String) map.get("summary");
			String financeSubjIds = (String) map.get("financeSubjIds");
			String financeSubjNames = "";
			Double totalMoney = (Double) map.get("totalMoney");
			Integer status = (Integer) map.get("status");
			String payeeName = (String) map.get("payeeName");
			String paymentWay = (String) map.get("paymentWay");
			Integer hasReceipt = (Integer) map.get("hasReceipt");
			Integer billCount = (Integer) map.get("billCount");
			String agent = (String) map.get("agent");
			String currencyId = (String) map.get("currencyId");
			String currencyCode = (String) map.get("currencyCode");
			Double exchangeRate = (Double) map.get("exchangeRate");
			Integer contractType = (Integer) map.get("contractType");
			String acontractNo = map.get("acontractNo")!=null ? map.get("acontractNo").toString() : "";
			String wcontractNo = map.get("wcontractNo")!=null ? map.get("wcontractNo").toString() : "";
			String pcontractNo = map.get("pcontractNo")!=null ? map.get("pcontractNo").toString() : "";
			
			//获取财务科目名称
			if (!StringUtils.isBlank(financeSubjIds)) {
				financeSubjNames = "";
				String[] financeSubjIdArray = financeSubjIds.split(",");
				for (String financeSubjId : financeSubjIdArray) {
					financeSubjNames += this.financeSubjectService.getFinanceSubjName(financeSubjId) + ",";
				}
			}
			
			singleAccountMap.put("receiptId", paymentId);
			singleAccountMap.put("receiptDate", paymentDate);
			singleAccountMap.put("createTime", createTime);
			singleAccountMap.put("receiptNo", receiptNo);
			singleAccountMap.put("summary", summary);
			singleAccountMap.put("financeSubjName", financeSubjNames);
			singleAccountMap.put("collectMoney", 0.00);
			singleAccountMap.put("payedMoney", totalMoney);
			singleAccountMap.put("status", status + "");
			singleAccountMap.put("formType", 1);
			singleAccountMap.put("aimPersonName", payeeName);
			singleAccountMap.put("paymentWay", paymentWay);
			singleAccountMap.put("hasReceipt", hasReceipt + "");
			singleAccountMap.put("billCount", billCount);
			singleAccountMap.put("agent", agent);
			singleAccountMap.put("currencyId", currencyId);
			singleAccountMap.put("currencyCode", currencyCode);
			singleAccountMap.put("exchangeRate", exchangeRate);
			
			singleAccountMap.put("contractNo", null);
			singleAccountMap.put("contractName", null);
			//合同信息
			if (contractType != null) {
				if (contractType == ContractType.Actor.getValue()) {
					singleAccountMap.put("contractNo", acontractNo);
					singleAccountMap.put("contractName", map.get("actorName"));
				} else if (contractType == ContractType.Worker.getValue()) {
					singleAccountMap.put("contractNo", wcontractNo);
					singleAccountMap.put("contractName", map.get("workerName"));
				} else if (contractType == ContractType.Produce.getValue()) {
					singleAccountMap.put("contractNo", pcontractNo);
					singleAccountMap.put("contractName", map.get("company"));
				}
			}
			
			runningAccountList.add(singleAccountMap);
		}
		
		
		return runningAccountList;
	}
	
	/**
	 * 获取收付款人信息
	 * 该接口中返回的信息合并到obtainRunnigAccountSearchData里了
	 * 暂时保留该接口，为了兼容app没有升级的情况
	 * 等app升级了2个版本了之后，在删掉该接口
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@Deprecated
	@ResponseBody
	@RequestMapping("/obtainAimPeople")
	public Object obtainAimPeople(String crewId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			List<String> aimPeople = new ArrayList<String>();	//目标人物
			
			List<PaymentInfoModel> paymentInfoList = this.paymentInfoService.queryByCrewId(crewId);
			for (PaymentInfoModel paymentInfo : paymentInfoList) {
				String aimPerson = paymentInfo.getPayeeName();
				if (!aimPeople.contains(aimPerson)) {
					aimPeople.add(aimPerson);
				}
			}
			
			List<CollectionInfoModel> collectionList = this.collectionInfoService.queryByCrewId(crewId);
			for (CollectionInfoModel collection : collectionList) {
				String aimPerson = collection.getOtherUnit();
				if (!aimPeople.contains(aimPerson)) {
					aimPeople.add(aimPerson);
				}
			}
			
			List<LoanInfoModel> loanInfoList = this.loanInfoService.queryByCrewId(crewId);
			for (LoanInfoModel loanInfo : loanInfoList) {
				String aimPerson = loanInfo.getPayeeName();
				if (!aimPeople.contains(aimPerson)) {
					aimPeople.add(aimPerson);
				}
			}
			
			resultMap.put("aimPeople", aimPeople);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常", e);
			throw new IllegalArgumentException("未知异常", e);
		}

		return resultMap;
	}
	
	
	
	/**
	 * 获取收付款人信息
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainRunnigAccountSearchData")
	public Object obtainRunnigAccountSearchData(String crewId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			List<String> aimPeople = new ArrayList<String>();	//目标人物
			
			List<PaymentInfoModel> paymentInfoList = this.paymentInfoService.queryByCrewId(crewId);
			for (PaymentInfoModel paymentInfo : paymentInfoList) {
				String aimPerson = paymentInfo.getPayeeName();
				if (!aimPeople.contains(aimPerson)) {
					aimPeople.add(aimPerson);
				}
			}
			
			List<CollectionInfoModel> collectionList = this.collectionInfoService.queryByCrewId(crewId);
			for (CollectionInfoModel collection : collectionList) {
				String aimPerson = collection.getOtherUnit();
				if (!aimPeople.contains(aimPerson)) {
					aimPeople.add(aimPerson);
				}
			}
			
			List<LoanInfoModel> loanInfoList = this.loanInfoService.queryByCrewId(crewId);
			for (LoanInfoModel loanInfo : loanInfoList) {
				String aimPerson = loanInfo.getPayeeName();
				if (!aimPeople.contains(aimPerson)) {
					aimPeople.add(aimPerson);
				}
			}
			
			//付款方式
			List<FinancePaymentWayModel> paymentWayList = this.financePaymentWayService.queryByCrewId(crewId);
			List<Map<String, Object>> paymentWayMapList = new ArrayList<Map<String, Object>>();
			for (FinancePaymentWayModel paymentWay : paymentWayList) {
				Map<String, Object> paymentWayMap = new HashMap<String, Object>();
				paymentWayMap.put("wayId", paymentWay.getWayId());
				paymentWayMap.put("wayName", paymentWay.getWayName());
				paymentWayMapList.add(paymentWayMap);
			}
			
			resultMap.put("aimPeople", aimPeople);
			resultMap.put("paymentWayList", paymentWayMapList);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常", e);
			throw new IllegalArgumentException("未知异常", e);
		}

		return resultMap;
	}
}