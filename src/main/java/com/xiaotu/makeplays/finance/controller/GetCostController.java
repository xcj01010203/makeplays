package com.xiaotu.makeplays.finance.controller;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.attachment.model.AttachmentModel;
import com.xiaotu.makeplays.attachment.model.constants.AttachmentType;
import com.xiaotu.makeplays.attachment.service.AttachmentService;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
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
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.ExcelUtils;
import com.xiaotu.makeplays.utils.ExportExcelUtil;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.OfficeUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.PropertiesUitls;

/**
 * 收支管理公用、综合接口
 * 该类是包含付款单、收款单、借款单三类收支单在内的公用接口
 * @author xuchangjian 2016-8-23上午9:27:44
 */
@Controller
@RequestMapping("/getCostManager")
public class GetCostController extends BaseController {
	private static Map<String, String> FINANCE_MAP = new LinkedHashMap<String, String>();//需要导出的联系人字段
    static{
    	FINANCE_MAP.put("日期", "receiptDate");
    	FINANCE_MAP.put("票据编号",  "receiptNo");
    	FINANCE_MAP.put("摘要",  "summary");
    	FINANCE_MAP.put("关联合同号",  "contractNo");
    	FINANCE_MAP.put("财务科目",  "financeSubjName");
    	FINANCE_MAP.put("收款金额",  "collectMoney");
    	FINANCE_MAP.put("付款金额",  "payedMoney");
    	FINANCE_MAP.put("状态",  "status");
    	FINANCE_MAP.put("财务类型",  "formType");
    	FINANCE_MAP.put("部门",  "department");
    	FINANCE_MAP.put("收/付款方",  "aimPersonName");
    	FINANCE_MAP.put("付款方式",  "paymentWay");
    	FINANCE_MAP.put("有无发票",  "hasReceipt");
    	FINANCE_MAP.put("票据张数",  "billCount");
    	FINANCE_MAP.put("记账人",  "agent");
    }
	
	Logger logger = LoggerFactory.getLogger(GetCostController.class);
	
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月");
	
	SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private DecimalFormat df = new DecimalFormat("#,##0.00");

	@Autowired
	private PaymentInfoService paymentInfoService;
	
	@Autowired
	private LoanInfoService loanInfoService;
	
	@Autowired
	private CollectionInfoService collectionInfoService;
	
	@Autowired
	private FinanceSubjectService financeSubjectService;
	@Autowired
	private CrewInfoService crewInfoService;
	
	@Autowired
	private GetCostService getCostService;
	
	@Autowired
	private FinancePaymentWayService financePaymentWayService;
	
	@Autowired
	private CurrencyInfoService currencyInfoService;
	
	@Autowired
	private AttachmentService attachmentService;
	
	/**
	 * 跳转到财务流水账页面
	 * @return
	 */
	@RequestMapping("/toFinanceRunningAccountPage")
	public ModelAndView toFinanceRunningAccountPage() {
		ModelAndView mv = new ModelAndView("/finance/getcost/financeRunningAccount");
		return mv;
	}
	
	/**
	 * 跳转到收支管理页面
	 * @param receiptType 单据类型，1-付款单  2-收款单   3-借款单
	 * @return
	 */
	@RequestMapping("/toGetCostPage")
	public ModelAndView toGetCostPage(Integer receiptType) {
		ModelAndView mv = new ModelAndView("/finance/getcost/getCostInfo");
		if (receiptType == null) {
			receiptType = 1;
		}
		mv.addObject("receiptType", receiptType);
		return mv;
	}
	
	/**
	 * 查询财务流水账
	 * @param request
	 * @param financeSubjIds	财务科目ID，多个以逗号隔开
	 * @param aimPeopleNames	收/付款人，多个以逗号隔开
	 * @param aimDates	票据日期，多个以逗号隔开，单个日期格式：yyyy-MM-dd
	 * @param aimMonth	票据日期月份，格式：yyyy年MM月
	 * @param agents	记账人，多个以逗号隔开
	 * @param formType	单据类型： 1-付款单  2-收款单  3-借款单
	 * @param hasReceipt 是否有发票
	 * @param status	结算状态：0-未结算  1-已结算
	 * @param summary	摘要
	 * @param billType 票据种类
	 * @param minMoney	最小金额
	 * @param maxMoney	最大金额
	 * @param includeLoan 是否含借款
	 * @param paymentWayId 付款方式
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryFinanceRunningAccount")
	public Map<String, Object> queryFinanceRunningAccount(HttpServletRequest request, String financeSubjIds, 
			String aimPeopleNames, String aimDates, String aimMonth, String agents, Integer formType, Boolean hasReceipt, Integer status, 
			String summary, Double minMoney, Double maxMoney, Boolean includeLoan, String paymentWayId, Integer billType, Integer pageNo, Integer pageSize, Boolean isAsc,
			Integer sortType,String department) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			if (includeLoan == null) {
				includeLoan = false;
			}
			
			String crewId = getCrewId(request);
			this.financeSubjectService.refreshCachedSubjectList(crewId);
			
			boolean  singleCurrencyFlag= false;	//标识剧组中是否只有一个有效币种
			Map<String, Object> conditionMap = new HashMap<String, Object>();
        	conditionMap.put("crewId", crewId);
        	conditionMap.put("ifEnable", true);
        	List<CurrencyInfoModel> currencyInfoList = this.currencyInfoService.queryManyByMutiCondition(conditionMap, null);
			if (currencyInfoList.size() == 1) {
				singleCurrencyFlag = true;
			}
			
			PaymentInfoFilter paymentInfoFilter = new PaymentInfoFilter();
			paymentInfoFilter.setFinanceSubjIds(financeSubjIds);
			paymentInfoFilter.setPayeeNames(aimPeopleNames);
			paymentInfoFilter.setPaymentDates(aimDates);
			paymentInfoFilter.setPaymentMonth(aimMonth);
			paymentInfoFilter.setAgents(agents);
			paymentInfoFilter.setHasReceipt(hasReceipt);
			paymentInfoFilter.setStatus(status);
			paymentInfoFilter.setSummary(summary);
			paymentInfoFilter.setMinMoney(minMoney);
			paymentInfoFilter.setMaxMoney(maxMoney);
			paymentInfoFilter.setPaymentWayId(paymentWayId);
			paymentInfoFilter.setBillType(billType);
			paymentInfoFilter.setDepartment(department);
			
			CollectionInfoFilter collectionFilter = new CollectionInfoFilter();
			collectionFilter.setOtherUnits(aimPeopleNames);
			collectionFilter.setCollectionDates(aimDates);
			collectionFilter.setCollectionMonth(aimMonth);
			collectionFilter.setAgents(agents);
			collectionFilter.setSummary(summary);
			collectionFilter.setMinMoney(minMoney);
			collectionFilter.setMaxMoney(maxMoney);
			collectionFilter.setPaymentWayId(paymentWayId);
			
			LoanInfoFilter loanInfoFilter = new LoanInfoFilter();
			loanInfoFilter.setFinanceSubjIds(financeSubjIds);
			loanInfoFilter.setPayeeNames(aimPeopleNames);
			loanInfoFilter.setLoanDates(aimDates);
			loanInfoFilter.setLoanMonth(aimMonth);
			loanInfoFilter.setAgents(agents);
			loanInfoFilter.setSummary(summary);
			loanInfoFilter.setMinMoney(minMoney);
			loanInfoFilter.setMaxMoney(maxMoney);
			loanInfoFilter.setPaymentWayId(paymentWayId);
			
			boolean includePayment = false;
			boolean includeCollection = false;
			boolean myIncludeLoan = false;
			
			if (formType == null || formType == 1) {
				includePayment = true;
			}
			if (formType == null || formType == 2) {
				includeCollection = true;
			}
			if ((formType != null && formType == 3) || includeLoan) {
				myIncludeLoan = true;
			}
			if (hasReceipt != null) {
				includeCollection = false;
				myIncludeLoan = false;
			}
			if (StringUtils.isNotBlank(department)) {
				includeCollection = false;
				myIncludeLoan = false;
			}
			if (status != null) {
				includeCollection = false;
				myIncludeLoan = false;
			}
			if (billType != null) {
				includeCollection = false;
				myIncludeLoan = false;
			}
			if (!StringUtils.isBlank(financeSubjIds)) {
				includeCollection = false;
			}
			if(isAsc == null) {
				isAsc = false;
			}
			
			if (sortType == null) {
				sortType = 0;
			}
			Page page = null;
			if(pageNo != null && pageSize != null) {
				page = new Page();
				page.setPageNo(pageNo);
				page.setPagesize(pageSize);
			}
			
			List<Map<String, Object>> runningAccountList = this.getCostService.queryFinanceRunningAccount(crewId, includePayment, 
					includeCollection, myIncludeLoan, 
					paymentInfoFilter, collectionFilter, 
					loanInfoFilter, page, isAsc, sortType);
			
			
			
			Map<String, Double> totalCollectMoneyCurrencyMap = new HashMap<String, Double>();	//key为币种ID，value为总收入
			Map<String, Double> totalPayedMoneyCurrencyMap = new HashMap<String, Double>();	//key为币种ID，value为总支出
			//查询该页记录之前的所有账务详情统计信息
			if (pageNo != null && pageNo != 1) {
				Page prePage = new Page();
				prePage.setPageNo(1);
				prePage.setPagesize(pageSize * (pageNo - 1));
				
				List<Map<String, Object>> currencyMoneyStatistic = this.getCostService.queryFinanceRunningAccountTotalMoney(crewId, includePayment, 
						includeCollection, includeLoan, 
						paymentInfoFilter, collectionFilter, 
						loanInfoFilter, prePage);
				for (Map<String, Object> currencyTotalMoneyInfo : currencyMoneyStatistic) {
					String currencyId = (String) currencyTotalMoneyInfo.get("currencyId");
					Double myTotalCollectMoney = Double.parseDouble(currencyTotalMoneyInfo.get("totalCollectMoney").toString());
					Double myTotalPayedMoney = Double.parseDouble(currencyTotalMoneyInfo.get("totalPayedMoney").toString());
					Double myTotalForLoanMoney = 0.0;
					if (currencyTotalMoneyInfo.get("totalForLoanMoney") != null) {
						myTotalForLoanMoney = Double.parseDouble(currencyTotalMoneyInfo.get("totalForLoanMoney").toString());
					}
					
					
					totalCollectMoneyCurrencyMap.put(currencyId, myTotalCollectMoney);
					if (!myIncludeLoan) {
						totalPayedMoneyCurrencyMap.put(currencyId, myTotalPayedMoney);
					} else {
						totalPayedMoneyCurrencyMap.put(currencyId, BigDecimalUtil.subtract(myTotalPayedMoney, myTotalForLoanMoney));
					}
				}
			}
			
			for (Map<String, Object> map : runningAccountList) {
				Double collectMoney = Double.parseDouble(map.get("collectMoney").toString());
				Double payedMoney = Double.parseDouble((map.get("payedMoney").toString()));
				Double forLoanMoney = 0.0;
				if (map.get("forLoanMoney") != null) {
					forLoanMoney = Double.parseDouble(map.get("forLoanMoney").toString());
				}
				String currencyId = (String) map.get("currencyId");
				String currencyCode = (String) map.get("currencyCode");
				int myFormType = ((Long) map.get("formType")).intValue();
				String financeSubjId = (String) map.get("financeSubjId");
				
				String loanIds = (String) map.get("loanIds");
				
				Double dealedPayedMoney = payedMoney;
				if (includeLoan && !StringUtils.isBlank(loanIds)) {
					dealedPayedMoney = BigDecimalUtil.subtract(dealedPayedMoney, forLoanMoney);
				}
				
				
				//处理总收入
				if (totalCollectMoneyCurrencyMap.containsKey(currencyId)) {
					Double totalCollectMoney = totalCollectMoneyCurrencyMap.get(currencyId);
					totalCollectMoneyCurrencyMap.put(currencyId, BigDecimalUtil.add(totalCollectMoney, collectMoney));
				} else {
					totalCollectMoneyCurrencyMap.put(currencyId, collectMoney);
				}
				
				//处理总支出
				if (totalPayedMoneyCurrencyMap.containsKey(currencyId)) {
					Double totalPayedMoney = totalPayedMoneyCurrencyMap.get(currencyId);
					totalPayedMoneyCurrencyMap.put(currencyId, BigDecimalUtil.add(totalPayedMoney, dealedPayedMoney));
				} else {
					totalPayedMoneyCurrencyMap.put(currencyId, dealedPayedMoney);
				}
				
				Double totalCollectionMoney = totalCollectMoneyCurrencyMap.get(currencyId);
				Double totalPayedMoney = totalPayedMoneyCurrencyMap.get(currencyId);
				
				Double leftMoney = BigDecimalUtil.subtract(totalCollectionMoney, totalPayedMoney);
				
				map.put("leftMoney", leftMoney);
				if (!singleCurrencyFlag) {
					map.put("leftMoneyStr", this.df.format(leftMoney) + "(" + currencyCode + ")");
					map.put("collectMoneyStr", this.df.format(collectMoney) + "(" + currencyCode + ")");
					map.put("payedMoneyStr", this.df.format(dealedPayedMoney) + "(" + currencyCode + ")");
				} else {
					map.put("leftMoneyStr", this.df.format(leftMoney));
					map.put("collectMoneyStr", this.df.format(collectMoney));
					map.put("payedMoneyStr", this.df.format(dealedPayedMoney));
				}
				
				if (myFormType == 1) {
					map.put("formTypeStr", "付款单");
				} else if (myFormType == 2) {
					map.put("formTypeStr", "收款单");
				} else {
					map.put("formTypeStr", "借款单");
				}
				
				//获取财务科目名称
				String financeSubjNames = "";
				if (!StringUtils.isBlank(financeSubjId)) {
					financeSubjNames = "";
					String[] financeSubjIdArray = financeSubjId.split(",");
					for (String myFinanceSubjId : financeSubjIdArray) {
						financeSubjNames += this.financeSubjectService.getFinanceSubjName(myFinanceSubjId) + " | ";
					}
					
					map.put("financeSubjName", financeSubjNames.substring(0, financeSubjNames.length() - 3));
				}
				
				//票据日期
				Date receiptDate = (Date) map.get("receiptDate");
				map.put("receiptDate", this.sdf1.format(receiptDate));
				
				//如果查询结果包含借款单信息，则用来付借款的付款单票据类型显示（付款单（抵借）），金额显示“付款金额-抵借金额”
				if (myIncludeLoan && myFormType == 1 && !StringUtils.isBlank(loanIds)) {
					map.put("formTypeStr", "付款单（抵借款）");
					if (!singleCurrencyFlag) {
						map.put("payedMoneyStr", this.df.format(payedMoney) + "(" + currencyCode + ")" + "-" + this.df.format(forLoanMoney) + "(" + currencyCode + ")");
					} else {
						map.put("payedMoneyStr", this.df.format(payedMoney) + "-" + this.df.format(forLoanMoney));
					}
				}
			}
			
			resultMap.put("runningAccountList", runningAccountList);
			if(page != null) {
				resultMap.put("pageNo", page.getPageNo());
				resultMap.put("pageCount", page.getPageCount());
				resultMap.put("total", page.getTotal());
			}
			
			this.sysLogService.saveSysLog(request, "查询账务详情", Constants.TERMINAL_PC, 
					PaymentInfoModel.TABLE_NAME + "," + CollectionInfoModel.TABLE_NAME + "," + LoanInfoModel.TABLE_NAME, null, 0);
		}catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询账务详情统计信息
	 * @param request
	 * @param financeSubjIds	财务科目ID，多个以逗号隔开
	 * @param aimPeopleNames	收/付款人，多个以逗号隔开
	 * @param aimDates	票据日期，多个以逗号隔开
	 * @param aimMonth	票据日期月份，格式：yyyy年MM月，单个日期格式：yyyy-MM-dd
	 * @param agents	记账人，多个以逗号隔开
	 * @param formType	单据类型： 1-付款单  2-收款单  3-借款单
	 * @param hasReceipt 是否有发票
	 * @param status	结算状态：0-未结算  1-已结算
	 * @param summary	摘要
	 * @param minMoney	最小金额
	 * @param maxMoney	最大金额
	 * @param includeLoan 是否含借款
	 * @param paymentWayId 付款方式
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryRunnigAccountStatistic")
	public Map<String, Object> queryRunnigAccountStatistic(HttpServletRequest request, String financeSubjIds, 
			String aimPeopleNames, String aimDates, String aimMonth, String agents, Integer formType, Boolean hasReceipt, Integer status, 
			String summary, Double minMoney, Double maxMoney,Integer billType, Boolean includeLoan, String paymentWayId,String department) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			/*if (hasReceipt != null) {
				formType = 1;
			}*/
			/*if (status != null) {
				formType = 1;
			}*/
			if (includeLoan == null) {
				includeLoan = false;
			}
			
			String crewId = getCrewId(request);
			
			PaymentInfoFilter paymentInfoFilter = new PaymentInfoFilter();
			paymentInfoFilter.setFinanceSubjIds(financeSubjIds);
			paymentInfoFilter.setPayeeNames(aimPeopleNames);
			paymentInfoFilter.setPaymentDates(aimDates);
			paymentInfoFilter.setPaymentMonth(aimMonth);
			paymentInfoFilter.setAgents(agents);
			paymentInfoFilter.setHasReceipt(hasReceipt);
			paymentInfoFilter.setStatus(status);
			paymentInfoFilter.setSummary(summary);
			paymentInfoFilter.setMinMoney(minMoney);
			paymentInfoFilter.setMaxMoney(maxMoney);
			paymentInfoFilter.setPaymentWayId(paymentWayId);
			paymentInfoFilter.setBillType(billType);
			paymentInfoFilter.setDepartment(department);
			
			CollectionInfoFilter collectionFilter = new CollectionInfoFilter();
			collectionFilter.setOtherUnits(aimPeopleNames);
			collectionFilter.setCollectionDates(aimDates);
			collectionFilter.setCollectionMonth(aimMonth);
			collectionFilter.setAgents(agents);
			collectionFilter.setSummary(summary);
			collectionFilter.setMinMoney(minMoney);
			collectionFilter.setMaxMoney(maxMoney);
			collectionFilter.setPaymentWayId(paymentWayId);
			
			LoanInfoFilter loanInfoFilter = new LoanInfoFilter();
			loanInfoFilter.setFinanceSubjIds(financeSubjIds);
			loanInfoFilter.setPayeeNames(aimPeopleNames);
			loanInfoFilter.setLoanDates(aimDates);
			loanInfoFilter.setLoanMonth(aimMonth);
			loanInfoFilter.setAgents(agents);
			loanInfoFilter.setSummary(summary);
			loanInfoFilter.setMinMoney(minMoney);
			loanInfoFilter.setMaxMoney(maxMoney);
			loanInfoFilter.setPaymentWayId(paymentWayId);
			
			boolean includePayment = false;
			boolean includeCollection = false;
			boolean myIncludeLoan = false;
			
			if (formType == null || formType == 1) {
				includePayment = true;
			}
			if (formType == null || formType == 2) {
				includeCollection = true;
			}
			if ((formType != null && formType == 3) || includeLoan) {
				myIncludeLoan = true;
			}
			if (hasReceipt != null) {
				includeCollection = false;
				myIncludeLoan = false;
			}
			if (status != null) {
				includeCollection = false;
				myIncludeLoan = false;
			}
			if (billType != null) {
				includeCollection = false;
				myIncludeLoan = false;
			}
			if (!StringUtils.isBlank(financeSubjIds)) {
				includeCollection = false;
			}
			
			List<Map<String, Object>> currencyMoneyStatistic = this.getCostService.queryFinanceRunningAccountTotalMoney(crewId, includePayment, 
					includeCollection, myIncludeLoan, 
					paymentInfoFilter, collectionFilter, 
					loanInfoFilter, null);
			
			for (Map<String, Object> currencyTotalMoneyInfo : currencyMoneyStatistic) {
				Double myTotalCollectMoney = Double.parseDouble((currencyTotalMoneyInfo.get("totalCollectMoney").toString()));
				Double myTotalPayedMoney = Double.parseDouble(currencyTotalMoneyInfo.get("totalPayedMoney").toString());
				Double myTotalForLoanMoney = 0.0;
				if (currencyTotalMoneyInfo.get("totalForLoanMoney") != null) {
					myTotalForLoanMoney = Double.parseDouble(currencyTotalMoneyInfo.get("totalForLoanMoney").toString());
				}
				
				currencyTotalMoneyInfo.put("collectMoney", myTotalCollectMoney);
				currencyTotalMoneyInfo.put("payedMoney", myTotalPayedMoney);
				if (!myIncludeLoan) {
					currencyTotalMoneyInfo.put("leftMoney", BigDecimalUtil.subtract(myTotalCollectMoney, myTotalPayedMoney));
				} else {
					currencyTotalMoneyInfo.put("leftMoney", BigDecimalUtil.add(BigDecimalUtil.subtract(myTotalCollectMoney, myTotalPayedMoney), myTotalForLoanMoney));
					currencyTotalMoneyInfo.put("payedMoney", BigDecimalUtil.subtract(myTotalPayedMoney, myTotalForLoanMoney));
				}
			}
			
			resultMap.put("currencyList", currencyMoneyStatistic);
		}catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 导出财务流水账
	 * @param request
	 * @param financeSubjIds	财务科目ID，多个以逗号隔开
	 * @param aimPeopleNames	收/付款人，多个以逗号隔开
	 * @param aimDates	票据日期，多个以逗号隔开
	 * @param agents	记账人，多个以逗号隔开
	 * @param formType	单据类型： 1-付款单  2-收款单  3-借款单
	 * @param hasReceipt 是否有发票
	 * @param status	结算状态：0-未结算  1-已结算
	 * @param summary	摘要
	 * @param minMoney	最小金额
	 * @param maxMoney	最大金额
	 * @param includeLoan 是否含借款
	 * @param templateName 模板名称
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exportFinanceRunningAccount")
	public Map<String, Object> exportFinanceRunningAccount(HttpServletRequest request, String financeSubjIds, 
			String aimPeopleNames, String aimDates, String aimMonth, String agents, Integer formType, Boolean hasReceipt, Integer status, 
			String summary, Double minMoney, Double maxMoney, Boolean includeLoan, String paymentWayId, Integer billType, String templateName) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			/*if (hasReceipt != null) {
				formType = 1;
			}
			if (status != null) {
				formType = 1;
			}*/
			if (includeLoan == null) {
				includeLoan = false;
			}
			
			boolean includePayment = false;
			boolean includeCollection = false;
			boolean myIncludeLoan = false;
			
			if (formType == null || formType == 1) {
				includePayment = true;
			}
			if (formType == null || formType == 2) {
				includeCollection = true;
			}
			if ((formType != null && formType == 3) || includeLoan) {
				myIncludeLoan = true;
			}
			if (hasReceipt != null) {
				includeCollection = false;
				myIncludeLoan = false;
			}
			if (status != null) {
				includeCollection = false;
				myIncludeLoan = false;
			}
			if (billType != null) {
				includeCollection = false;
				myIncludeLoan = false;
			}
			if (!StringUtils.isBlank(financeSubjIds)) {
				includeCollection = false;
			}
			
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			String crewId = crewInfo.getCrewId();
			String crewName = crewInfo.getCrewName();
			
			List<Map<String, Object>> runningAccountList = new ArrayList<Map<String, Object>>();
			this.financeSubjectService.refreshCachedSubjectList(crewId);
			
			boolean  singleCurrencyFlag= false;	//标识剧组中是否只有一个有效币种
			Map<String, Object> conditionMap = new HashMap<String, Object>();
        	conditionMap.put("crewId", crewId);
        	conditionMap.put("ifEnable", true);
        	List<CurrencyInfoModel> currencyInfoList = this.currencyInfoService.queryManyByMutiCondition(conditionMap, null);
			if (currencyInfoList.size() == 1) {
				singleCurrencyFlag = true;
			}
			
			//付款单
			if (includePayment) {
				PaymentInfoFilter filter = new PaymentInfoFilter();
				filter.setFinanceSubjIds(financeSubjIds);
				filter.setPayeeNames(aimPeopleNames);
				filter.setPaymentDates(aimDates);
				filter.setPaymentMonth(aimMonth);
				filter.setAgents(agents);
				filter.setHasReceipt(hasReceipt);
				filter.setStatus(status);
				filter.setSummary(summary);
				filter.setMinMoney(minMoney);
				filter.setMaxMoney(maxMoney);
				filter.setBillType(billType);
				filter.setPaymentWayId(paymentWayId);
				
				List<Map<String, Object>> paymentList = this.paymentInfoService.queryPaymentList(crewId, filter);
				runningAccountList.addAll(this.genRunningAccountByPaymentList(paymentList, includeLoan, true, singleCurrencyFlag));
			}
			
			//收款单
			if (includeCollection) {
				CollectionInfoFilter filter = new CollectionInfoFilter();
				filter.setOtherUnits(aimPeopleNames);
				filter.setCollectionDates(aimDates);
				filter.setCollectionMonth(aimMonth);
				filter.setAgents(agents);
				filter.setSummary(summary);
				filter.setMinMoney(minMoney);
				filter.setMaxMoney(maxMoney);
				filter.setPaymentWayId(paymentWayId);
				
				List<Map<String, Object>> collectionList = this.collectionInfoService.queryCollectionInfoList(crewId, filter);
				runningAccountList.addAll(this.genRunningAccountByCollectionList(collectionList, singleCurrencyFlag));
			}
			
			//借款单
			if (myIncludeLoan) {
				LoanInfoFilter filter = new LoanInfoFilter();
				filter.setFinanceSubjIds(financeSubjIds);
				filter.setPayeeNames(aimPeopleNames);
				filter.setLoanDates(aimDates);
				filter.setLoanMonth(aimMonth);
				filter.setAgents(agents);
				filter.setSummary(summary);
				filter.setMinMoney(minMoney);
				filter.setMaxMoney(maxMoney);
				filter.setPaymentWayId(paymentWayId);
				
				List<Map<String, Object>> loanInfoList = this.loanInfoService.queryLoanInfoList(crewId, filter);
				runningAccountList.addAll(this.genRunningAccountByLoanInfoList(loanInfoList, singleCurrencyFlag));
			}
			
			//把所有的收支单据先按照单据日期排序，再按照创建日期排序
			Collections.sort(runningAccountList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					Date o1ReceiptDate = (Date) o1.get("receiptDate");
					Date o1CreateTime = (Date) o1.get("createTime");
					
					Date o2ReceiptDate = (Date) o2.get("receiptDate");
					Date o2CreateTime = (Date) o2.get("createTime");
					
					int result = 0;
					if (o1ReceiptDate.before(o2ReceiptDate)) {
						result = -1;
					} else if (o1ReceiptDate.after(o2ReceiptDate)) {
						result = 1;
					}  else if (o1CreateTime.before(o2CreateTime)) {
						result = -1;
					} else if (o1CreateTime.after(o2CreateTime)) {
						result = 1;
					}
	        		return result;
				}
			});
			
			//处理有票无票/结算状态
			for (Map<String, Object> map : runningAccountList) {
				
				//状态
				String myStatus = (String) map.get("status");
				if (myStatus.equals("0")) {
					map.put("status", "未结算");
				}
				if (myStatus.equals("1")) {
					map.put("status", "已结算");
				}
				
				//有无发票
				String myHasReceipt = (String) map.get("hasReceipt");
				if (myHasReceipt.equals("1")) {
					map.put("hasReceipt", "有发票");
				}
				if (myHasReceipt.equals("0")) {
					map.put("hasReceipt", "无发票");
				}
				
				//票据日期
				Date receiptDate = (Date) map.get("receiptDate");
				map.put("receiptDate", this.sdf1.format(receiptDate));
			}
			
			/*
			 * 统计信息
			 */
			PaymentInfoFilter paymentInfoFilter = new PaymentInfoFilter();
			paymentInfoFilter.setFinanceSubjIds(financeSubjIds);
			paymentInfoFilter.setPayeeNames(aimPeopleNames);
			paymentInfoFilter.setPaymentDates(aimDates);
			paymentInfoFilter.setPaymentMonth(aimMonth);
			paymentInfoFilter.setAgents(agents);
			paymentInfoFilter.setHasReceipt(hasReceipt);
			paymentInfoFilter.setStatus(status);
			paymentInfoFilter.setSummary(summary);
			paymentInfoFilter.setMinMoney(minMoney);
			paymentInfoFilter.setMaxMoney(maxMoney);
			paymentInfoFilter.setPaymentWayId(paymentWayId);
			paymentInfoFilter.setBillType(billType);
			
			CollectionInfoFilter collectionFilter = new CollectionInfoFilter();
			collectionFilter.setOtherUnits(aimPeopleNames);
			collectionFilter.setCollectionDates(aimDates);
			collectionFilter.setCollectionMonth(aimMonth);
			collectionFilter.setAgents(agents);
			collectionFilter.setSummary(summary);
			collectionFilter.setMinMoney(minMoney);
			collectionFilter.setMaxMoney(maxMoney);
			collectionFilter.setPaymentWayId(paymentWayId);
			
			LoanInfoFilter loanInfoFilter = new LoanInfoFilter();
			loanInfoFilter.setFinanceSubjIds(financeSubjIds);
			loanInfoFilter.setPayeeNames(aimPeopleNames);
			loanInfoFilter.setLoanDates(aimDates);
			loanInfoFilter.setLoanMonth(aimMonth);
			loanInfoFilter.setAgents(agents);
			loanInfoFilter.setSummary(summary);
			loanInfoFilter.setMinMoney(minMoney);
			loanInfoFilter.setMaxMoney(maxMoney);
			loanInfoFilter.setPaymentWayId(paymentWayId);
			
			List<Map<String, Object>> currencyMoneyStatistic = this.getCostService.queryFinanceRunningAccountTotalMoney(crewId, includePayment, 
					includeCollection, myIncludeLoan, 
					paymentInfoFilter, collectionFilter, 
					loanInfoFilter, null);
			
			for (Map<String, Object> currencyTotalMoneyInfo : currencyMoneyStatistic) {
				Double myTotalCollectMoney = Double.parseDouble((currencyTotalMoneyInfo.get("totalCollectMoney").toString()));
				Double myTotalPayedMoney = Double.parseDouble(currencyTotalMoneyInfo.get("totalPayedMoney").toString());
				Double myTotalForLoanMoney = 0.0;
				if (currencyTotalMoneyInfo.get("totalForLoanMoney") != null) {
					myTotalForLoanMoney = Double.parseDouble(currencyTotalMoneyInfo.get("totalForLoanMoney").toString());
				}
				
				currencyTotalMoneyInfo.put("collectMoney", myTotalCollectMoney);
				currencyTotalMoneyInfo.put("payedMoney", myTotalPayedMoney);
				if (!myIncludeLoan) {
					currencyTotalMoneyInfo.put("leftMoney", BigDecimalUtil.subtract(myTotalCollectMoney, myTotalPayedMoney));
				} else {
					currencyTotalMoneyInfo.put("leftMoney", BigDecimalUtil.add(BigDecimalUtil.subtract(myTotalCollectMoney, myTotalPayedMoney), myTotalForLoanMoney));
					currencyTotalMoneyInfo.put("payedMoney", BigDecimalUtil.subtract(myTotalPayedMoney, myTotalForLoanMoney));
				}
			}
			
			//生成下载的文件
			Map<String, Object> exportDataMap = new HashMap<String, Object>();
			exportDataMap.put("runningAccountList", runningAccountList);
			exportDataMap.put("currencyList", currencyMoneyStatistic);
			
			Properties property = PropertiesUitls.fetchProperties("/config.properties");
	  		String srcfilePath = property.getProperty("zwxq_template");
	  		if(StringUtils.isNotBlank(templateName) && templateName.equals("subject")) {
	  			srcfilePath = property.getProperty("zwxq_template_subject");
	  		}
	  		String downloadPath = property.getProperty("downloadPath") + "《" + crewName + "》" + "账务详情_"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".xls";
	  		File pathFile = new File(property.getProperty("downloadPath"));
	  		if(!pathFile.isDirectory()){
	  			pathFile.mkdirs();
	  		}
	  		
	  		ExportExcelUtil.exportViewToExcelTemplate(srcfilePath, exportDataMap, downloadPath);
			
	  		
			resultMap.put("downloadPath", downloadPath);
			
			this.sysLogService.saveSysLog(request, "导出账务详情", Constants.TERMINAL_PC, 
					PaymentInfoModel.TABLE_NAME + "," + CollectionInfoModel.TABLE_NAME + "," + LoanInfoModel.TABLE_NAME, null, 5);
		}catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "导出账务详情失败：" + e.getMessage(), Constants.TERMINAL_PC, 
					PaymentInfoModel.TABLE_NAME + "," + CollectionInfoModel.TABLE_NAME + "," + LoanInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 根据借款单生成流水账单
	 * @param loanInfoList	借款信息列表
	 * @param subjectList	财务科目列表
	 * @return
	 */
	private List<Map<String, Object>> genRunningAccountByLoanInfoList(List<Map<String, Object>> loanInfoList, boolean singleCurrencyFlag) {
		
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
			if (!singleCurrencyFlag) {
				singleAccountMap.put("collectMoneyStr", "0.00(" + currencyCode + ")");
			} else {
				singleAccountMap.put("collectMoneyStr", "0.00");
			}
			singleAccountMap.put("payedMoney", money);
			if (!singleCurrencyFlag) {
				singleAccountMap.put("payedMoneyStr", this.df.format(money) + "(" + currencyCode + ")");
			} else {
				singleAccountMap.put("payedMoneyStr", this.df.format(money));
			}
			singleAccountMap.put("status", "/");
			singleAccountMap.put("formType", 3);
			singleAccountMap.put("formTypeStr", "借款单");
			singleAccountMap.put("aimPersonName", payeeName);
			singleAccountMap.put("paymentWay", LoanPaymentWay.valueOf(paymentWay).getName());
			singleAccountMap.put("hasReceipt", "/");
			singleAccountMap.put("billCount", "/");
			singleAccountMap.put("agent", agent);
			singleAccountMap.put("currencyId", currencyId);
			singleAccountMap.put("currencyCode", currencyCode);
			
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
	private List<Map<String, Object>> genRunningAccountByCollectionList(List<Map<String, Object>> collectionList, boolean singleCurrencyFlag) {
		
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
//			String acontractNo = map.get("acontractNo")!=null?map.get("acontractNo").toString():"";
//			String wcontractNo = map.get("wcontractNo")!=null?map.get("wcontractNo").toString():"";
//			String pcontractNo = map.get("pcontractNo")!=null?map.get("pcontractNo").toString():"";
			
			
			singleAccountMap.put("receiptId", collectionId);
			singleAccountMap.put("receiptDate", collectionDate);
			singleAccountMap.put("createTime", createTime);
			singleAccountMap.put("receiptNo", receiptNo);
			singleAccountMap.put("summary", summary);
			singleAccountMap.put("financeSubjName", "");
			singleAccountMap.put("contractNo", null);
			singleAccountMap.put("contractName", null);
			singleAccountMap.put("collectMoney", money);
			if (!singleCurrencyFlag) {
				singleAccountMap.put("collectMoneyStr", this.df.format(money) + "(" + currencyCode + ")");
			} else {
				singleAccountMap.put("collectMoneyStr", this.df.format(money));
			}
			singleAccountMap.put("payedMoney", 0.00);
			if (!singleCurrencyFlag) {
				singleAccountMap.put("payedMoneyStr", "0.00(" + currencyCode + ")");
			} else {
				singleAccountMap.put("payedMoneyStr", "0.00");
			}
			singleAccountMap.put("status", "/");
			singleAccountMap.put("formType", 2);
			singleAccountMap.put("formTypeStr", "收款单");
			singleAccountMap.put("aimPersonName", otherUnit);
			singleAccountMap.put("paymentWay", paymentWay);
			singleAccountMap.put("hasReceipt", "/");
			singleAccountMap.put("billCount", "/");
			singleAccountMap.put("agent", agent);
			singleAccountMap.put("currencyId", currencyId);
			singleAccountMap.put("currencyCode", currencyCode);
			
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
	private List<Map<String, Object>> genRunningAccountByPaymentList(List<Map<String, Object>> paymentList, boolean includeLoan, boolean forExport, boolean singleCurrencyFlag) {
		List<Map<String, Object>> runningAccountList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : paymentList) {
			Map<String, Object> singleAccountMap = new HashMap<String, Object>();
			
			String paymentId = (String) map.get("paymentId");
			Date paymentDate = (Date) map.get("paymentDate");
			Date createTime = (Date) map.get("createTime");
			String receiptNo = (String) map.get("receiptNo");
			String summary = (String) map.get("summary");
			String financeSubjIds = (String) map.get("financeSubjIds");
			String financeSubjInfo = (String) map.get("financeSubjInfo");	//财务科目信息，格式：财务科目ID&&金额&&备注，多个以##隔开
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
			String loanIds = (String) map.get("loanIds");
			Double forLoanMoney = (Double) map.get("forLoanMoney");
			String department = (String) map.get("department");
			
			Integer contractType = (Integer) map.get("contractType");
			String acontractNo = map.get("acontractNo")!=null ? map.get("acontractNo").toString() : "";
			String wcontractNo = map.get("wcontractNo")!=null ? map.get("wcontractNo").toString() : "";
			String pcontractNo = map.get("pcontractNo")!=null ? map.get("pcontractNo").toString() : "";
			
			singleAccountMap.put("receiptId", paymentId);
			singleAccountMap.put("receiptDate", paymentDate);
			singleAccountMap.put("createTime", createTime);
			singleAccountMap.put("receiptNo", receiptNo);
			singleAccountMap.put("summary", summary);
			singleAccountMap.put("collectMoney", 0.00);
			if (!singleCurrencyFlag) {
				singleAccountMap.put("collectMoneyStr", "0.00(" + currencyCode + ")");
			} else {
				singleAccountMap.put("collectMoneyStr", "0.00");
			}
			singleAccountMap.put("payedMoney", totalMoney);
			if (!singleCurrencyFlag) {
				singleAccountMap.put("payedMoneyStr", this.df.format(totalMoney) + "(" + currencyCode + ")");
			} else {
				singleAccountMap.put("payedMoneyStr", this.df.format(totalMoney));
			}
			singleAccountMap.put("status", status + "");
			singleAccountMap.put("formType", 1);
			singleAccountMap.put("formTypeStr", "付款单");
			singleAccountMap.put("aimPersonName", payeeName);
			singleAccountMap.put("paymentWay", paymentWay);
			singleAccountMap.put("hasReceipt", hasReceipt + "");
			singleAccountMap.put("billCount", billCount);
			singleAccountMap.put("agent", agent);
			singleAccountMap.put("currencyId", currencyId);
			singleAccountMap.put("currencyCode", currencyCode);
			singleAccountMap.put("department", department);
			
			//账务详情如果查询借款单，则对付款金额和单据类型做特殊处理
			if (includeLoan && !StringUtils.isBlank(loanIds)) {
				singleAccountMap.put("formTypeStr", "付款单(抵借)");
				singleAccountMap.put("payedMoney", BigDecimalUtil.subtract(totalMoney, forLoanMoney));
				if (!singleCurrencyFlag) {
					singleAccountMap.put("payedMoneyStr", this.df.format(totalMoney) + "(" + currencyCode + ")-" + this.df.format(forLoanMoney) + "(" + currencyCode + ")");
				} else {
					singleAccountMap.put("payedMoneyStr", this.df.format(totalMoney) + "-" + this.df.format(forLoanMoney));
				}
			}
			
			//合同信息
			singleAccountMap.put("contractNo", null);
			singleAccountMap.put("contractName", null);
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
			
			//获取财务科目名称
			if (!StringUtils.isBlank(financeSubjIds)) {
				financeSubjNames = "";
				String[] financeSubjIdArray = financeSubjIds.split(",");
				for (String financeSubjId : financeSubjIdArray) {
					financeSubjNames += this.financeSubjectService.getFinanceSubjName(financeSubjId) + ",";
				}
				if (!StringUtils.isBlank(financeSubjNames)) {
					financeSubjNames = financeSubjNames.substring(0, financeSubjNames.length() - 1);
				}
			}

			singleAccountMap.put("financeSubjName", financeSubjNames);
			
			if (forExport) {
				if (!StringUtils.isBlank(financeSubjIds)) {
					String[] financeSubjInfoArray = financeSubjInfo.split("##");
					for (String myFinanceSubjInfo : financeSubjInfoArray) {
						String[] myFinanceSubjInfoArray = myFinanceSubjInfo.split("&&");
						String myFinanceSubjId = myFinanceSubjInfoArray[0];
						Double myFinanceSubjMoney = Double.parseDouble(myFinanceSubjInfoArray[1]);
						String myFinanceSubjSummary = myFinanceSubjInfoArray[2];
						
						Map<String, Object> mySingleAccountMap = new HashMap<String, Object>();
						mySingleAccountMap.putAll(singleAccountMap);
						mySingleAccountMap.put("payedMoney", myFinanceSubjMoney);
						mySingleAccountMap.put("summary", myFinanceSubjSummary);
						
						if (!singleCurrencyFlag) {
							mySingleAccountMap.put("payedMoneyStr", this.df.format(myFinanceSubjMoney) + "(" + currencyCode + ")");
						} else {
							mySingleAccountMap.put("payedMoneyStr", this.df.format(myFinanceSubjMoney));
						}
						mySingleAccountMap.put("financeSubjName", this.financeSubjectService.getFinanceSubjName(myFinanceSubjId));
						runningAccountList.add(mySingleAccountMap);
					}
				}
			} else {
				runningAccountList.add(singleAccountMap);
			}
		}
		return runningAccountList;
	}
	
	/**
	 * 查询付款单、收款单、借款单中所有下拉项列表
	 * @param request
	 * @param includePayment	是否包含付款单中数据
	 * @param includeCollection	是否包含收款单中数据
	 * @param includeLoan	是否包含借款单中数据
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryDropDownData")
	public Map<String, Object> queryDropDownData(HttpServletRequest request, Boolean includePayment, Boolean includeCollection, Boolean includeLoan) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			String crewId = getCrewId(request);
			
			List<String> aimPeople = new ArrayList<String>();	//目标人物
			List<String> receiptDateList = new ArrayList<String>();	//票据日期，格式：yyyy-MM-dd
			List<String> agentList = new ArrayList<String>();	//记账人
			List<String> receiptMoonList = new ArrayList<String>();	//票据日期，格式：yyyy年MM月
			
			if (includePayment == null || includePayment) {
				List<PaymentInfoModel> paymentInfoList = this.paymentInfoService.queryByCrewId(crewId);
				for (PaymentInfoModel paymentInfo : paymentInfoList) {
					String receiptDate = this.sdf1.format(paymentInfo.getPaymentDate());	//票据日期，格式：yyyy-MM-dd
					String receiptMoon = this.sdf2.format(paymentInfo.getPaymentDate());	//票据日期，格式：yyyy年MM月
					String aimPerson = paymentInfo.getPayeeName();	//目标人物
					String agent = paymentInfo.getAgent();	//记账人
					
					if (!agentList.contains(agent)) {
						agentList.add(agent);
					}
					
					if (!aimPeople.contains(aimPerson)) {
						aimPeople.add(aimPerson);
					}
					
					if (!receiptDateList.contains(receiptDate)) {
						receiptDateList.add(receiptDate);
					}
					
					if (!receiptMoonList.contains(receiptMoon)) {
						receiptMoonList.add(receiptMoon);
					}
				}
			}
			
			if (includeCollection == null || includeCollection) {
				List<CollectionInfoModel> collectionList = this.collectionInfoService.queryByCrewId(crewId);
				for (CollectionInfoModel collection : collectionList) {
					String receiptDate = this.sdf1.format(collection.getCollectionDate());	//票据日期，格式：yyyy-MM-dd
					String receiptMoon = this.sdf2.format(collection.getCollectionDate());	//票据日期，格式：yyyy年MM月
					String aimPerson = collection.getOtherUnit();	//目标人物
					String agent = collection.getAgent();	//记账人
					
					if (!agentList.contains(agent)) {
						agentList.add(agent);
					}
					
					if (!aimPeople.contains(aimPerson)) {
						aimPeople.add(aimPerson);
					}
					if (!receiptDateList.contains(receiptDate)) {
						receiptDateList.add(receiptDate);
					}
					
					if (!receiptMoonList.contains(receiptMoon)) {
						receiptMoonList.add(receiptMoon);
					}
				}
			}
			
			if (includeLoan == null || includeLoan) {
				List<LoanInfoModel> loanInfoList = this.loanInfoService.queryByCrewId(crewId);
				for (LoanInfoModel loanInfo : loanInfoList) {
					String receiptDate = this.sdf1.format(loanInfo.getLoanDate());	//票据日期，格式：yyyy-MM-dd
					String receiptMoon = this.sdf2.format(loanInfo.getLoanDate());	//票据日期，格式：yyyy年MM月
					String aimPerson = loanInfo.getPayeeName();	//目标人物
					String agent = loanInfo.getAgent();	//记账人
					
					if (!agentList.contains(agent)) {
						agentList.add(agent);
					}
					
					if (!aimPeople.contains(aimPerson)) {
						aimPeople.add(aimPerson);
					}
					if (!receiptDateList.contains(receiptDate)) {
						receiptDateList.add(receiptDate);
					}
					
					if (!receiptMoonList.contains(receiptMoon)) {
						receiptMoonList.add(receiptMoon);
					}
				}
			}
			
			
			//付款方式
			List<FinancePaymentWayModel> paymentWayList = this.financePaymentWayService.queryByCrewId(crewId);
			
			
			Collections.sort(receiptDateList, new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					int result = 0;
					try {
						Date date1 = sdf1.parse(o1);
						Date date2 = sdf1.parse(o2);
						
						if (date2.before(date1)) {
							result = -1;
						} else {
							result = 1;
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					return result;
				}
			});
			
			Collections.sort(receiptMoonList, new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					int result = 0;
					try {
						Date date1 = sdf2.parse(o1);
						Date date2 = sdf2.parse(o2);
						
						if (date2.before(date1)) {
							result = -1;
						} else {
							result = 1;
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					return result;
				}
			});
			
			//查询付款单中的所有的部门列表
			List<Map<String, Object>> departmentList = this.paymentInfoService.queryPaymentDepartment(crewId);
			
			resultMap.put("departmentList", departmentList);
			resultMap.put("aimPeople", aimPeople);
			resultMap.put("receiptDateList", receiptDateList);
			resultMap.put("receiptMoonList", receiptMoonList);
			resultMap.put("agentList", agentList);
			resultMap.put("paymentWayList", paymentWayList);
		}catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	
	/**
	 * 导入财务流水账
	 * 
	 * @param request
	 * @param file
	 * @param submitNums
	 * @param isCover
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/importRunningAccount")
	public Map<String, Object> importRunningAccount(HttpServletRequest request, MultipartFile file, String submitNums, boolean isCover) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			String crewName = crewInfo.getCrewName();
			String crewId = crewInfo.getCrewId();
			
			// 上传文件到服务器
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String baseStorePath = properties.getProperty("fileupload.path");
			String modelStorePath = baseStorePath + "import/getCost";
			String newName = crewName + sdf3.format(new Date());
			Map<String, String> fileMap = FileUtils.uploadFileForExcel(request, modelStorePath, newName);
			if (fileMap == null) {
				throw new IllegalArgumentException("请选择文件");
			}
			String fileStoreName = fileMap.get("fileStoreName");// 新文件名
			String storePath = fileMap.get("storePath");// 服务器存文文件路径

			// 整理预算excel文件内容
			Map<String, Object> getCostInfoMap = ExcelUtils.readGetCostInfo(storePath + fileStoreName);

			this.getCostService.saveFinanceInfoFromExcel(getCostInfoMap, FINANCE_MAP, crewId, isCover);
//			for (Map<String, Object> data : needDealDataList) {
//				String finanSubjName = (String) data.get("financeSubjName");
//				List<FinanceSubjectDto> financeSubjectDtoList = this.financeSubjectService.getFinanceSubjByName(finanSubjName);
//				
//				List<Map<String, Object>> toChooseFinanSubjList = new ArrayList<Map<String, Object>>();
//				for (FinanceSubjectDto subject : financeSubjectDtoList) {
//					String finanSubjId = subject.getId();
//					String myFinanSubjName = subject.getName();
//					
//					Map<String, Object> subjectMap = new HashMap<String, Object>();
//					subjectMap.put("finanSubjId", finanSubjId);
//					subjectMap.put("finanSubjName", myFinanSubjName);
//					toChooseFinanSubjList.add(subjectMap);
//				}
//				
//				data.put("toChooseFinanSubjList", toChooseFinanSubjList);
//			}
			
//			resultMap.put("needDealDataList", needDealDataList);
			this.sysLogService.saveSysLog(request, "导入账务详情", Constants.TERMINAL_PC, 
					PaymentInfoModel.TABLE_NAME + "," + CollectionInfoModel.TABLE_NAME + "," + LoanInfoModel.TABLE_NAME, null, 4);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			logger.error("未知异常", e);
			
			success = false;
			message = "未知异常";
			this.sysLogService.saveSysLog(request, "导入账务详情失败：" + e.getMessage(), Constants.TERMINAL_PC, 
					PaymentInfoModel.TABLE_NAME + "," + CollectionInfoModel.TABLE_NAME + "," + LoanInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 导出财务流水账
	 * 该导出如果一个付款单和多个财务科目关联，则返回多条付款单记录
	 * @param request
	 * @param financeSubjIds	财务科目ID，多个以逗号隔开
	 * @param aimPeopleNames	收/付款人，多个以逗号隔开
	 * @param aimDates	票据日期，多个以逗号隔开
	 * @param agents	记账人，多个以逗号隔开
	 * @param formType	单据类型： 1-付款单  2-收款单  3-借款单
	 * @param hasReceipt 是否有发票
	 * @param status	结算状态：0-未结算  1-已结算
	 * @param summary	摘要
	 * @param minMoney	最小金额
	 * @param maxMoney	最大金额
	 * @param includeLoan 是否含借款
	 * @return
	 */
//	@ResponseBody
//	@RequestMapping("/exportRunningAccountInfo")
//	public Map<String, Object> exportRunningAccountInfo(HttpServletRequest request,HttpServletResponse response,String financeSubjIds, 
//			String aimPeopleNames, String aimDates, String aimMonth, String agents, Integer formType, Boolean hasReceipt, Integer status, 
//			String summary, Double minMoney, Double maxMoney, Boolean includeLoan){
//		
//		Map<String, Object> resultMap = new HashMap<String, Object>();
//		boolean success = true;
//		String message = "";
//		try {
//
//			if (hasReceipt != null) {
//				formType = 1;
//			}
//			if (status != null) {
//				formType = 1;
//			}
//			if (includeLoan == null) {
//				includeLoan = false;
//			}
//			
//			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
//			String crewId = crewInfo.getCrewId();
//			String crewName = crewInfo.getCrewName();
//			
//			List<Map<String, Object>> runningAccountList = new ArrayList<Map<String, Object>>();
//			this.financeSubjectService.refreshCachedSubjectList(crewId);
//			
//			//付款单
//			if (formType == null || formType == 1) {
//				List<Map<String, Object>> paymentList = this.paymentInfoService.queryPaymentListForExport(crewId, null, financeSubjIds, 
//						aimPeopleNames, aimDates, aimMonth, agents, 
//						hasReceipt, status, summary, minMoney, maxMoney);
//				runningAccountList.addAll(this.genRunningAccountByPaymentListForExport(paymentList));
//			}
//			
//			//收款单
//			if (formType == null || formType == 2) {
//				CollectionInfoFilter filter = new CollectionInfoFilter();
//				filter.setOtherUnits(aimPeopleNames);
//				filter.setCollectionDates(aimDates);
//				filter.setCollectionMonth(aimMonth);
//				filter.setAgents(agents);
//				filter.setSummary(summary);
//				filter.setMinMoney(minMoney);
//				filter.setMaxMoney(maxMoney);
//				
//				List<Map<String, Object>> collectionList = this.collectionInfoService.queryCollectionInfoList(crewId, filter);
//				runningAccountList.addAll(this.genRunningAccountByCollectionList(collectionList));
//			}
//			
//			//借款单
//			if ((formType == null || formType == 3) && includeLoan) {
//				LoanInfoFilter filter = new LoanInfoFilter();
//				filter.setFinanceSubjIds(financeSubjIds);
//				filter.setPayeeNames(aimPeopleNames);
//				filter.setLoanDates(aimDates);
//				filter.setLoanMonth(aimMonth);
//				filter.setAgents(agents);
//				filter.setSummary(summary);
//				filter.setMinMoney(minMoney);
//				filter.setMaxMoney(maxMoney);
//				
//				List<Map<String, Object>> loanInfoList = this.loanInfoService.queryLoanInfoList(crewId, filter);
//				runningAccountList.addAll(this.genRunningAccountByLoanInfoList(loanInfoList));
//			}
//			
//			//把所有的收支单据先按照单据日期排序，再按照创建日期排序
//			Collections.sort(runningAccountList, new Comparator<Map<String, Object>>() {
//				@Override
//				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
//					Date o1ReceiptDate = (Date) o1.get("receiptDate");
//					Date o1CreateTime = (Date) o1.get("createTime");
//					
//					Date o2ReceiptDate = (Date) o2.get("receiptDate");
//					Date o2CreateTime = (Date) o2.get("createTime");
//					
//					int result = 0;
//					if (o1ReceiptDate.before(o2ReceiptDate)) {
//						result = -1;
//					} else if (o1ReceiptDate.after(o2ReceiptDate)) {
//						result = 1;
//					}  else if (o1CreateTime.before(o2CreateTime)) {
//						result = -1;
//					} else if (o1CreateTime.after(o2CreateTime)) {
//						result = 1;
//					}
//	        		return result;
//				}
//			});
//			
//			Map<String, Double> totalCollectMoneyCurrencyMap = new HashMap<String, Double>();	//key为币种ID，value为总收入
//			Map<String, Double> totalPayedMoneyCurrencyMap = new HashMap<String, Double>();	//key为币种ID，value为总支出
//			
//			Map<String, String> currencyIdCodeMap = new HashMap<String, String>();	//key为币种ID，value为币种编码
//			for (Map<String, Object> map : runningAccountList) {
//				Double collectMoney = (Double) map.get("collectMoney");
//				Double payedMoney = (Double) map.get("payedMoney");
//				String currencyId = (String) map.get("currencyId");
//				String currencyCode = (String) map.get("currencyCode");
//				
//				if (!currencyIdCodeMap.containsKey(currencyId)) {
//					currencyIdCodeMap.put(currencyId, currencyCode);
//				}
//				
//				//处理总收入
//				if (totalCollectMoneyCurrencyMap.containsKey(currencyId)) {
//					Double totalCollectMoney = totalCollectMoneyCurrencyMap.get(currencyId);
//					totalCollectMoneyCurrencyMap.put(currencyId, BigDecimalUtil.add(totalCollectMoney, collectMoney));
//				} else {
//					totalCollectMoneyCurrencyMap.put(currencyId, collectMoney);
//				}
//				
//				//处理总支出
//				if (totalPayedMoneyCurrencyMap.containsKey(currencyId)) {
//					Double totalPayedMoney = totalPayedMoneyCurrencyMap.get(currencyId);
//					totalPayedMoneyCurrencyMap.put(currencyId, BigDecimalUtil.add(totalPayedMoney, payedMoney));
//				} else {
//					totalPayedMoneyCurrencyMap.put(currencyId, payedMoney);
//				}
//				
//				Double totalCollectionMoney = totalCollectMoneyCurrencyMap.get(currencyId);
//				Double totalPayedMoney = totalPayedMoneyCurrencyMap.get(currencyId);
//				
//				map.put("leftMoney", this.df.format(BigDecimalUtil.subtract(totalCollectionMoney, totalPayedMoney)) + "(" + currencyCode + ")");
//				
//				//金钱格式化
//				if (collectMoney == 0) {
//					map.put("collectMoney", "");
//				} else {
//					map.put("collectMoney", this.df.format(collectMoney) + "(" + currencyCode + ")");
//				}
//				if (payedMoney == 0) {
//					map.put("payedMoney", "");
//				} else {
//					map.put("payedMoney", this.df.format(payedMoney) + "(" + currencyCode + ")");
//				}
//				
//				//状态
//				String myStatus = (String) map.get("status");
//				if (myStatus.equals("0")) {
//					map.put("status", "未结算");
//				}
//				if (myStatus.equals("1")) {
//					map.put("status", "已结算");
//				}
//				
//				//财务类型
//				Integer myFormType = (Integer) map.get("formType");
//				if (myFormType == 1) {
//					map.put("formType", "付款");
//				}
//				if (myFormType == 2) {
//					map.put("formType", "收款");
//				}
//				if (myFormType == 3) {
//					map.put("formType", "借款");
//				}
//				
//				//有无发票
//				String myHasReceipt = (String) map.get("hasReceipt");
//				if (myHasReceipt.equals("1")) {
//					map.put("hasReceipt", "有发票");
//				}
//				if (myHasReceipt.equals("0")) {
//					map.put("hasReceipt", "无发票");
//				}
//				
//				//票据日期
//				Date receiptDate = (Date) map.get("receiptDate");
//				map.put("receiptDate", this.sdf1.format(receiptDate));
//			}
//			
//			
//			//单个货币的收支数据
//			List<Map<String, Object>> currencyList = new ArrayList<Map<String, Object>>();
//			Set<String> currencyIdSet = currencyIdCodeMap.keySet();
//			for (String currencyId : currencyIdSet) {
//				Double collectMoney = totalCollectMoneyCurrencyMap.get(currencyId);
//				Double payedMoney = totalPayedMoneyCurrencyMap.get(currencyId);
//				
//				Map<String, Object> currencyMap = new HashMap<String, Object>();
//				currencyMap.put("currencyId", currencyId);
//				currencyMap.put("currencyCode", currencyIdCodeMap.get(currencyId));
//				currencyMap.put("collectMoney", this.df.format(collectMoney));
//				currencyMap.put("payedMoney", this.df.format(payedMoney));
//				currencyMap.put("leftMoney", this.df.format(BigDecimalUtil.subtract(collectMoney, payedMoney)));
//				
//				currencyList.add(currencyMap);
//			}
//			
//			ExcelUtils.exportFinanceInfoForExcel(runningAccountList,response,FINANCE_MAP,crewName);
//		} catch(java.lang.IllegalArgumentException iException){
//			message = iException.getMessage();
//			success = false;
//			logger.error(message);
//		}catch (Exception e) {
//			message = "未知异常";
//			success = false;
//			logger.error("未知异常",e);
//		}
//		resultMap.put("message", message);
//		resultMap.put("success", success);
//		return resultMap;
//	}
	
	/**
	 * 根据收款单id删除收款单信息
	 * @param request
	 * @param collectionId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteCollectionInfo")
	public Map<String, Object> deleteCollectionInfo(HttpServletRequest request, String collectionId){
		Map<String, Object> resuleMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(collectionId)) {
				throw new IllegalArgumentException("请选择要删除的收款单！");
			}
			
			this.collectionInfoService.deleteById(collectionId);
			message = "删除成功！";
			this.sysLogService.saveSysLog(request, "删除收款单", Constants.TERMINAL_PC, CollectionInfoModel.TABLE_NAME, collectionId, SysLogOperType.DELETE.getValue());
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
		} catch (Exception e) {
			message = "未知异常删除失败！";
			success = false;
			this.sysLogService.saveSysLog(request, "删除收款单失败：" + e.getMessage(), Constants.TERMINAL_PC, CollectionInfoModel.TABLE_NAME, collectionId, SysLogOperType.ERROR.getValue());
		}
		resuleMap.put("message", message);
		resuleMap.put("success", success);
		
		return resuleMap;
	}
	
	/**
	 * 上传收支管理的附件
	 * @param request
	 * @param attpackId
	 * @param file
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/upoloadCostAttachment")
	public Map<String, Object> upoloadCostAttachment(HttpServletRequest request, String attpackId, MultipartFile file){

		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			
			if (file == null) {
				throw new java.lang.IllegalArgumentException("请提供需要上传的文件");
			}
			
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String baseStorePath = properties.getProperty("fileupload.path");
			String storePath = baseStorePath + "common/getCost/";
			
			//把附件上传到服务器(高清原版)
            Map<String, String> fileMap = FileUtils.uploadFile(file, false, storePath);
			String fileStoreName = fileMap.get("fileStoreName");
			String fileRealName = fileMap.get("fileRealName");
			String hdStorePath = fileMap.get("storePath");
			
			String sdStorePath = "";
			
			String exceptSuffixName = fileStoreName.substring(0, fileStoreName.lastIndexOf("."));	//不带后缀的文件名
			String suffix = fileStoreName.substring(fileStoreName.lastIndexOf("."));//文件后缀
			
			int type = AttachmentType.Others.getValue();	//附件类型
			
			hdStorePath = hdStorePath + fileStoreName;
			
			if (suffix.equals(".doc") || suffix.equals(".docx") || suffix.equals(".xls") || suffix.equals(".xlsx")) {
				String dateStr = this.sdf1.format(new Date());
				
				sdStorePath = storePath + dateStr + "/sd/" + exceptSuffixName + ".pdf";
				OfficeUtils.word2Format(hdStorePath, sdStorePath);
				
				String tempPath = hdStorePath;
				hdStorePath = sdStorePath;
				sdStorePath = tempPath;
				
				if (suffix.equals(".doc") || suffix.equals(".docx")) {
					type = AttachmentType.Word.getValue();
				}else if (suffix.equals(".xls") || suffix.equals(".xlsx")) {
					type = AttachmentType.Others.getValue();
				}
			}
			
			//判断是否是图片文件
			if (FileUtils.isPicture(hdStorePath)) {
				type = AttachmentType.Picture.getValue();
			}
			
			long size = FileUtils.getFileSize(hdStorePath);
			
			AttachmentModel attachment = this.attachmentService.saveAttachmentInfo(crewId, attpackId, type, fileRealName, hdStorePath, sdStorePath, suffix, size, 0);
			
			resultMap.put("attpackId", attachment.getAttpackId());
			resultMap.put("attachmentId", attachment.getId());
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			logger.error("未知异常，上传附件失败", e);
			
			success = false;
			message = "未知异常，上传附件失败";
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
}
