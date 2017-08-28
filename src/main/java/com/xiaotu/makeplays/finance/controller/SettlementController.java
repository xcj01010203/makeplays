package com.xiaotu.makeplays.finance.controller;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.DoubleRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.finance.controller.dto.BudgetCurrencyDto;
import com.xiaotu.makeplays.finance.controller.dto.BudgetInfoDto;
import com.xiaotu.makeplays.finance.model.CurrencyInfoModel;
import com.xiaotu.makeplays.finance.model.FinanceSubjectModel;
import com.xiaotu.makeplays.finance.model.PaymentFinanSubjMapModel;
import com.xiaotu.makeplays.finance.model.PaymentInfoModel;
import com.xiaotu.makeplays.finance.service.ContractActorService;
import com.xiaotu.makeplays.finance.service.ContractProduceService;
import com.xiaotu.makeplays.finance.service.ContractWorkerService;
import com.xiaotu.makeplays.finance.service.CurrencyInfoService;
import com.xiaotu.makeplays.finance.service.FinanceSubjectService;
import com.xiaotu.makeplays.finance.service.LoanInfoService;
import com.xiaotu.makeplays.finance.service.PaymentFinanSubjMapService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.view.service.ViewInfoService;

/**
 * 费用结算
 * 费用结算不是一个独立的概念，其实结合财务科目预算和对应付款单的支出组合成的报表
 * @author xuchangjian 2016-8-26下午2:50:33
 */
@Controller
@RequestMapping("/settleManager")
public class SettlementController extends BaseController {
	
	Logger logger = LoggerFactory.getLogger(SettlementController.class);
	
	DecimalFormat df1 = new DecimalFormat("#,##0.00");
	
	DecimalFormat df2 = new DecimalFormat("0.00");
	
	@Autowired
	private FinanceSubjectService financeSubjectService;
	
	@Autowired
	private CurrencyInfoService currencyInfoService;
	
	@Autowired
	private PaymentFinanSubjMapService paymentFinanSubjMapService;
	
	@Autowired
	private ContractActorService contractActorService;
	
	@Autowired
	private ContractWorkerService contractWorkerService;
	
	@Autowired
	private ContractProduceService contractProduceService;
	
	@Autowired
	private LoanInfoService loanInfoService;
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	/**
	 * 跳转到财务结算页面
	 * @return
	 */
	@RequestMapping("/toSettlementPage")
	public ModelAndView toSettlementPage() {
		ModelAndView mv = new ModelAndView("/finance/budget/financeSettlement");
		return mv;
	}
	
	/**
	 * 查询费用结算总的统计信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryTotalStatisticInfo")
	public Map<String, Object> queryTotalStatisticInfo(HttpServletRequest request, String paymentStartDate, String paymentEndDate) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	
        	/*
        	 * 总的预算和已付
        	 */
        	Double totalBudgetMoney = 0.0;	//总预算
        	Double totalPayedMoney = 0.0;	//总支出
        	Double totalHasReceipt = 0.00; //有票总支出
        	Double totalNoReceipt = 0.00; //无票总支出
        	Double totalLeftMoney = 0.0;	//总结余
        	Double totalPayedRate = 0.0;	//总完成比例
        	
        	//财务结算信息
        	List<Map<String, Object>> finanSubjWithSettleList = this.financeSubjectService.queryWithSettleInfo(crewId, paymentStartDate, paymentEndDate);
        	for (Map<String, Object> map : finanSubjWithSettleList) {
        		Double payedMoney = (Double) map.get("payedMoney");
        		Double exchangeRate = (Double) map.get("exchangeRate");
        		Integer hasReceipt = (Integer) map.get("hasReceipt");
        		
        		if (payedMoney != null && exchangeRate != null && hasReceipt != null) {
        			if (hasReceipt == 0) { //无票
        				totalNoReceipt = BigDecimalUtil.add(totalNoReceipt, BigDecimalUtil.multiply(payedMoney, exchangeRate));
					}else if (hasReceipt == 1) { //有票
						totalHasReceipt = BigDecimalUtil.add(totalHasReceipt, BigDecimalUtil.multiply(payedMoney, exchangeRate));
					}
        			
        			totalPayedMoney = BigDecimalUtil.add(totalNoReceipt, totalHasReceipt);
        		}
        	}
        	
        	//获取财务科目列表，预算信息
        	List<Map<String, Object>> finanSubjWithBudgetList = this.financeSubjectService.queryWithBudgetInfo(crewId);
        	for (Map<String, Object> map : finanSubjWithBudgetList) {
        		Double money = (Double) map.get("money");
        		Double exchangeRate = (Double) map.get("exchangeRate");
        		if (money != null && exchangeRate != null) {
        			totalBudgetMoney = BigDecimalUtil.add(totalBudgetMoney, BigDecimalUtil.multiply(money, exchangeRate));
        		}
        	}
        	
        	totalLeftMoney = BigDecimalUtil.subtract(totalBudgetMoney, totalPayedMoney);
        	if (totalBudgetMoney != 0) {
        		totalPayedRate = BigDecimalUtil.divide(totalPayedMoney, totalBudgetMoney);
        	}

        	/*
        	 * 合同的预算和已付
        	 */
        	Double totalContractBudget = 0.0;	//合同总预算
        	Double totalContractPayed = 0.0;	//合同总支出
        	Double totalContractLeft = 0.0;	//合同总未付
        	Double totalContractPayedRate = 0.0;	//合同总的支付比例
        	
        	//合同的预算信息
        	Map<String, Double> contractBudgetMap = this.genContractBudget(crewId);
        	Map<String, Double> contractPayedMap = this.genContractPayed(crewId, paymentStartDate, paymentEndDate);
        	
        	Set<String> contractBudgetKeySet = contractBudgetMap.keySet();
        	for (String financeSubjId : contractBudgetKeySet) {
        		Double budgetMoney = contractBudgetMap.get(financeSubjId);
        		totalContractBudget = BigDecimalUtil.add(totalContractBudget, budgetMoney);
        	}
        	
        	Set<String> contractPayedKeySet = contractPayedMap.keySet();
        	for (String financeSubjId : contractPayedKeySet) {
        		if (StringUtils.isBlank(financeSubjId)) {
        			continue;
        		}
        		Double payedMoney = contractPayedMap.get(financeSubjId);
        		totalContractPayed = BigDecimalUtil.add(totalContractPayed, payedMoney);
        	}
        	
        	totalContractLeft = BigDecimalUtil.subtract(totalContractBudget, totalContractPayed);
        	if (totalContractBudget != 0) {
        		totalContractPayedRate = BigDecimalUtil.divide(totalContractPayed, totalContractBudget);
        	}
        	
        	/*
        	 * 借款的预算和已付
        	 */
        	Double totalLoanMoney = 0.0;	//总借款
        	Double totalLoanPayed = 0.0;	//总还款
        	Double totalLoanLeft = 0.0;	//总未还款
        	Double totalLoanPayedRate = 0.0;	//还款比例
        	
        	//关联借款的科目
        	Map<String, Double> loanBugetMap = this.genLoanBudget(crewId);
        	Map<String, Double> loanPayedMap = this.genLoanPayed(crewId, paymentStartDate, paymentEndDate);
        	
        	Set<String> loanBudgetKeySet = loanBugetMap.keySet();
        	for (String financeSubjId : loanBudgetKeySet) {
        		Double budgetMoney = loanBugetMap.get(financeSubjId);
        		totalLoanMoney = BigDecimalUtil.add(totalLoanMoney, budgetMoney);
        	}
        	
        	Set<String> loanPayedKeySet = loanPayedMap.keySet();
        	for (String financeSubjId : loanPayedKeySet) {
        		if (StringUtils.isBlank(financeSubjId)) {
        			continue;
        		}
        		Double payedMoney = loanPayedMap.get(financeSubjId);
        		totalLoanPayed = BigDecimalUtil.add(totalLoanPayed, payedMoney);
        	}
        	
        	totalLoanLeft = BigDecimalUtil.subtract(totalLoanMoney, totalLoanPayed);
        	if (totalLoanMoney != 0) {
        		totalLoanPayedRate = BigDecimalUtil.divide(totalLoanPayed, totalLoanMoney);
        	}
        	

        	/*
        	 * 可机动费用
        	 */
        	Double totalFlexibleMoney = BigDecimalUtil.subtract(totalBudgetMoney, BigDecimalUtil.add(totalContractBudget, totalLoanLeft));	//总可机动费用
        	Double totalFlexibleRate = 0.0;	//总可机动比例
        	if (totalBudgetMoney != 0) {
        		totalFlexibleRate = BigDecimalUtil.divide(totalFlexibleMoney, totalBudgetMoney);
        	}
        	
        	resultMap.put("totalBudgetMoney", this.df1.format(totalBudgetMoney));
        	resultMap.put("totalPayedMoney", this.df1.format(totalPayedMoney));
        	resultMap.put("totalHasReceipt", this.df1.format(totalHasReceipt));
        	resultMap.put("totalNoReceipt", this.df1.format(totalNoReceipt));
        	resultMap.put("totalLeftMoney", this.df1.format(totalLeftMoney));
        	resultMap.put("totalPayedRate", this.df2.format(totalPayedRate * 100) + "%");
        	resultMap.put("totalContractBudget", this.df1.format(totalContractBudget));
        	resultMap.put("totalContractPayed", this.df1.format(totalContractPayed));
        	resultMap.put("totalContractLeft", this.df1.format(totalContractLeft));
        	resultMap.put("totalContractPayedRate", this.df2.format(totalContractPayedRate * 100) + "%");
        	resultMap.put("totalLoanMoney", this.df1.format(totalLoanMoney));
        	resultMap.put("totalLoanPayed", this.df1.format(totalLoanPayed));
        	resultMap.put("totalLoanLeft", this.df1.format(totalLoanLeft));
        	resultMap.put("totalLoanPayedRate", this.df2.format(totalLoanPayedRate * 100) + "%");
        	resultMap.put("totalFlexibleMoney", this.df1.format(totalFlexibleMoney));
        	resultMap.put("totalFlexibleRate", this.df2.format(totalFlexibleRate * 100) + "%");
        	
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
        } catch(Exception e) {
            success = false;
            message = "未知异常";

            logger.error("未知异常", e);
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
	
	/**
	 * 查询财务结算表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/querySettlementList")
	public Map<String, Object> querySettlementList(HttpServletRequest request, String paymentStartDate, String paymentEndDate) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	
        	//货币列表
        	Map<String, Object> conditionMap = new HashMap<String, Object>();
        	conditionMap.put("crewId", crewId);
        	conditionMap.put("ifEnable", true);
        	List<CurrencyInfoModel> currencyInfoList = this.currencyInfoService.queryManyByMutiCondition(conditionMap, null);
        	
        	//财务结算信息
        	List<Map<String, Object>> finanSubjWithSettleList = this.financeSubjectService.queryWithSettleInfo(crewId, paymentStartDate, paymentEndDate);
        	
        	//财务科目结算数据-----第一层map：key为财务科目ID，value为一个map；第二层map：key为币种ID，value为一个map;第三层map：key为有票无票，value为结算金额
        	Map<String, Map<String, Map<String, Double>>> settleMap = new HashMap<String, Map<String, Map<String, Double>>>();	
        	for (Map<String, Object> finanSubj : finanSubjWithSettleList) {
        		String id = (String) finanSubj.get("id");	//财务科目ID
        		String currencyId = (String) finanSubj.get("currencyId");	//币种ID
        		Double payedMoney = (Double) finanSubj.get("payedMoney");	//支出金额
        		Integer hasReceipt = (Integer) finanSubj.get("hasReceipt"); //有票无票
        		
        		if (settleMap.containsKey(id)) {
        			Map<String, Map<String, Double>> currencyMap = settleMap.get(id);
        			if (currencyMap.containsKey(currencyId)) {
        				Map<String, Double> receiptMap = currencyMap.get(currencyId);
        				if (hasReceipt == 0) { //无票
        					receiptMap.put("noReceipt", BigDecimalUtil.add(receiptMap.get("noReceipt")==null?0.00:receiptMap.get("noReceipt"), payedMoney));
						}else if (hasReceipt == 1) { //有票
							receiptMap.put("hasReceipt", BigDecimalUtil.add(receiptMap.get("hasReceipt")==null?0.00:receiptMap.get("hasReceipt"), payedMoney));
						}
        			} else {
        				Map<String, Double> receiptMap = new HashMap<String, Double>();
        				if (hasReceipt == 0) { //无票
        					receiptMap.put("noReceipt",  payedMoney);
						}else if (hasReceipt == 1) { //有票
							receiptMap.put("hasReceipt", payedMoney);
						}
        				currencyMap.put(currencyId, receiptMap);
        			}
        		} else {
        			Map<String, Map<String, Double>> currencyMap = new HashMap<String, Map<String, Double>>();
        			Map<String, Double> receiptMap = new HashMap<String, Double>();
        			if (hasReceipt == 0) { //无票
    					receiptMap.put("noReceipt",  payedMoney);
					}else if (hasReceipt == 1) { //有票
						receiptMap.put("hasReceipt", payedMoney);
					}
        			currencyMap.put(currencyId, receiptMap);
        			
        			settleMap.put(id, currencyMap);
        		}
        	}
        	
        	//合同的预算信息
        	Map<String, Double> contractBudgetMap = this.genContractBudget(crewId);
        	Map<String, Double> contractPayedMap = this.genContractPayed(crewId, paymentStartDate, paymentEndDate);
        	
        	//关联借款的科目
        	Map<String, Double> loanBugetMap = this.genLoanBudget(crewId);
        	Map<String, Double> loanPayedMap = this.genLoanPayed(crewId, paymentStartDate, paymentEndDate);
        	
        	//获取财务科目预算信息列表
        	List<Map<String, Object>> finanSubjWithBudgetList = this.financeSubjectService.queryWithBudgetInfo(crewId);
        	
        	//把财务科目数据结合货币列表封装成BudgetInfoDto数据格式
        	List<BudgetInfoDto> budgetInfoList = new ArrayList<BudgetInfoDto>();
        	
        	for (Map<String, Object> finanSubj : finanSubjWithBudgetList) {
        		String id = (String) finanSubj.get("id");	//财务科目ID
        		String name = (String) finanSubj.get("name");	//财务科目名称
        		String parentId = (String) finanSubj.get("parentId");	//财务科目父科目ID
        		String remark = (String) finanSubj.get("remark");
        		Integer level = (Integer) finanSubj.get("level");
        		Integer sequence = (Integer) finanSubj.get("sequence");
        		
        		String currencyId = (String) finanSubj.get("currencyId");	//货币ID
        		
        		String mapId = (String) finanSubj.get("mapId");	//关联关系ID
        		Double amount = (Double) finanSubj.get("amount");	//数量
        		Double money = (Double) finanSubj.get("money");	//总金额
        		Double perPrice = (Double) finanSubj.get("perPrice");	//单价
        		String unitType = (String) finanSubj.get("unitType");	//单位
        		
        		//该财务科目的所有币种结算金额信息
        		Map<String, Map<String, Double>> currencySettleMap = settleMap.get(id);
        		
        		List<BudgetCurrencyDto> budgetCurrencyList = new ArrayList<BudgetCurrencyDto>();
        		
        		//把所有的货币信息封装到预算货币Dto中
        		for (CurrencyInfoModel currencyInfo : currencyInfoList) {
        			String myCurrencyId = currencyInfo.getId();
        			
        			BudgetCurrencyDto budgetCurrencyDto = new BudgetCurrencyDto();
        			budgetCurrencyDto.setCurrencyId(myCurrencyId);
        			budgetCurrencyDto.setCurrencyCode(currencyInfo.getCode());
        			budgetCurrencyDto.setCurrencyName(currencyInfo.getName());
        			budgetCurrencyDto.setExchangeRate(currencyInfo.getExchangeRate());
        			budgetCurrencyDto.setIfStandard(currencyInfo.getIfStandard());
        			
        			//如果当前科目有预算信息，则设置，否则预算为0
        			if (myCurrencyId.equals(currencyId)) {
        				budgetCurrencyDto.setMapId(mapId);
            			budgetCurrencyDto.setAmount(amount);
            			budgetCurrencyDto.setMoney(money);
            			budgetCurrencyDto.setPerPrice(perPrice);
            			budgetCurrencyDto.setUnitType(unitType);
        			} else {
        				budgetCurrencyDto.setMoney(0d);
        			}
        			
        			//该科目，该币种的结算金额信息
        			if (currencySettleMap == null || currencySettleMap.get(myCurrencyId) == null) {
        				budgetCurrencyDto.setSettleMoney(0.00);
        				budgetCurrencyDto.setHasReceiptMoney(0.00);
        				budgetCurrencyDto.setNoReceiptMoney(0.00);
        			} else {
        				Map<String, Double> receiptMap = currencySettleMap.get(myCurrencyId);
        				budgetCurrencyDto.setHasReceiptMoney(receiptMap.get("hasReceipt"));
        				budgetCurrencyDto.setNoReceiptMoney(receiptMap.get("noReceipt"));
        				Double hasReceiptMoney = 0.00;
        				Double noReceiptMoney = 0.00;
        				if (receiptMap.get("hasReceipt") != null) {
        					hasReceiptMoney = receiptMap.get("hasReceipt");
						}
        				if (receiptMap.get("noReceipt") != null) {
        					noReceiptMoney = receiptMap.get("noReceipt");
						}
        				budgetCurrencyDto.setSettleMoney(BigDecimalUtil.add(hasReceiptMoney, noReceiptMoney));
        			}
        			
        			budgetCurrencyList.add(budgetCurrencyDto);
        		}
        		
        		BudgetInfoDto budgetInfoDto = new BudgetInfoDto();
        		budgetInfoDto.setFinanceSubjId(id);
        		budgetInfoDto.setFinanceSubjName(name);
        		budgetInfoDto.setFinanceSubjParentId(parentId);
        		budgetInfoDto.setRemark(remark);
        		budgetInfoDto.setLevel(level);
        		budgetInfoDto.setSequence(sequence);
        		budgetInfoDto.setBudgetCurrencyList(budgetCurrencyList);
        		
        		//合同预算
        		if (contractBudgetMap.get(id) != null) {
        			budgetInfoDto.setContractBudget(contractBudgetMap.get(id));
        		} else {
        			budgetInfoDto.setContractBudget(0.00);
        		}
        		
        		//合同已付金额
        		if (contractPayedMap.get(id) != null) {
        			budgetInfoDto.setContractPayed(contractPayedMap.get(id));
        		} else {
        			budgetInfoDto.setContractPayed(0.00);
        		}
        		
        		//借款预算
        		if (loanBugetMap.get(id) != null) {
        			budgetInfoDto.setLoanBudget(loanBugetMap.get(id));
        		} else {
        			budgetInfoDto.setLoanBudget(0.00);
        		}
        		
        		//借款已付金额
        		if (loanPayedMap.get(id) != null) {
        			budgetInfoDto.setLoanPayed(loanPayedMap.get(id));
        		} else {
        			budgetInfoDto.setLoanPayed(0.00);
        		}
        		
        		budgetInfoList.add(budgetInfoDto);
        	}
        	
        	
        	
        	//把子节点的预算值向父节点上合并，并把财务科目按照父子关系做成嵌套的形式
        	List<BudgetInfoDto> budgetDtoList = this.loopBudgetInfoDto(new ArrayList<BudgetInfoDto>(), budgetInfoList);
        	
        	//把嵌套形式的财务科目平铺开来，并且把其中的货币信息做成以货币ID为键，总金额为值的形式
        	List<Map<String, Object>> settlementInfoMapList = this.genBudgetInfo(new ArrayList<Map<String, Object>>(), budgetDtoList);
        	Collections.sort(settlementInfoMapList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					int o1sequence = (Integer) o1.get("sequence");
					int o2sequence = (Integer) o2.get("sequence");
	        		return o1sequence - o2sequence;
				}
			});
        	for(Map<String, Object> map : settlementInfoMapList) {
        		String financeSubjParentId = map.get("financeSubjParentId") + "";
        		if(!financeSubjParentId.equals("0")) {
	        		map.put("_parentId", map.get("financeSubjParentId"));
        		}
        	}

            resultMap.put("total", settlementInfoMapList.size());
            resultMap.put("rows", settlementInfoMapList);
        	resultMap.put("settleInfoList", settlementInfoMapList);
        	
        	this.sysLogService.saveSysLog(request, "查询费用结算信息", Constants.TERMINAL_PC, 
        			FinanceSubjectModel.TABLE_NAME + "," + PaymentFinanSubjMapModel.TABLE_NAME + "," + PaymentInfoModel.TABLE_NAME, crewId, 0);
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
        } catch(Exception e) {
            success = false;
            message = "未知异常";

            logger.error("未知异常", e);
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
	
	/**
	 * 生成合同的预算信息
	 * @param crewId
	 */
	private Map<String, Double> genContractBudget(String crewId) {
		//关联合同的科目
		Map<String, Double> contractBudgetMap = new HashMap<String, Double>();	//存储所有合同的预算，key为财务科目ID，value为预算值
		//演员合同
		List<Map<String, Object>> contractActorBudgetList = this.contractActorService.queryContractActorBudget(crewId);
		for (Map<String, Object> contractBudget : contractActorBudgetList) {
			String financeSubjId = (String) contractBudget.get("financeSubjId");
			Double totalMoney = (Double) contractBudget.get("totalMoney");
			Double exchangeRate = (Double) contractBudget.get("exchangeRate");
			
			Double standardMoney = BigDecimalUtil.multiply(totalMoney, exchangeRate);
			
			if (contractBudgetMap.containsKey(financeSubjId)) {
				contractBudgetMap.put(financeSubjId, BigDecimalUtil.add(standardMoney, contractBudgetMap.get(financeSubjId)));
			} else {
				contractBudgetMap.put(financeSubjId, standardMoney);
			}
			
		}
		//职员合同
		List<Map<String, Object>> contractWorkerBudget = this.contractWorkerService.queryContractWorkerBudget(crewId);
		for (Map<String, Object> contractBudget : contractWorkerBudget) {
			String financeSubjId = (String) contractBudget.get("financeSubjId");
			Double totalMoney = (Double) contractBudget.get("totalMoney");
			Double exchangeRate = (Double) contractBudget.get("exchangeRate");
			
			Double standardMoney = BigDecimalUtil.multiply(totalMoney, exchangeRate);
			
			if (contractBudgetMap.containsKey(financeSubjId)) {
				contractBudgetMap.put(financeSubjId, BigDecimalUtil.add(standardMoney, contractBudgetMap.get(financeSubjId)));
			} else {
				contractBudgetMap.put(financeSubjId, standardMoney);
			}
			
		}
		
		//制作合同
		List<Map<String, Object>> contractProduceBudget = this.contractProduceService.queryContractProduceBudget(crewId);
		for (Map<String, Object> contractBudget : contractProduceBudget) {
			String financeSubjId = (String) contractBudget.get("financeSubjId");
			Double totalMoney = (Double) contractBudget.get("totalMoney");
			Double exchangeRate = (Double) contractBudget.get("exchangeRate");
			
			Double standardMoney = BigDecimalUtil.multiply(totalMoney, exchangeRate);
			
			if (contractBudgetMap.containsKey(financeSubjId)) {
				contractBudgetMap.put(financeSubjId, BigDecimalUtil.add(standardMoney, contractBudgetMap.get(financeSubjId)));
			} else {
				contractBudgetMap.put(financeSubjId, standardMoney);
			}
		}
		
		return contractBudgetMap;
	}
	
	/**
	 * 生成合同的支付信息
	 * @param crewId
	 */
	private Map<String, Double> genContractPayed(String crewId, String paymentStartDate, String paymentEndDate) {
		//关联合同的科目
		Map<String, Double> contractPayedMap = new HashMap<String, Double>();	//存储所有合同的结算，key为财务科目ID，value为结算值
		//演员合同
		List<Map<String, Object>> contractActorList = this.contractActorService.queryByAnvanceCondition(crewId, null, null, null, null, null, null, paymentStartDate, paymentEndDate);
		for (Map<String, Object> contractBudget : contractActorList) {
			String financeSubjId = (String) contractBudget.get("financeSubjId");
			Double payedMoney = (Double) contractBudget.get("payedMoney");
			Double exchangeRate = (Double) contractBudget.get("exchangeRate");
			
			Double standardMoney = BigDecimalUtil.multiply(payedMoney, exchangeRate);
			
			if (contractPayedMap.containsKey(financeSubjId)) {
				contractPayedMap.put(financeSubjId, BigDecimalUtil.add(standardMoney, contractPayedMap.get(financeSubjId)));
			} else {
				contractPayedMap.put(financeSubjId, standardMoney);
			}
			
		}
		//职员合同
		List<Map<String, Object>> contractWorkerList = this.contractWorkerService.queryByAnvanceCondition(crewId, null, null, null, null, null, null, paymentStartDate, paymentEndDate);
		for (Map<String, Object> contractBudget : contractWorkerList) {
			String financeSubjId = (String) contractBudget.get("financeSubjId");
			Double payedMoney = (Double) contractBudget.get("payedMoney");
			Double exchangeRate = (Double) contractBudget.get("exchangeRate");
			
			Double standardMoney = BigDecimalUtil.multiply(payedMoney, exchangeRate);
			
			if (contractPayedMap.containsKey(financeSubjId)) {
				contractPayedMap.put(financeSubjId, BigDecimalUtil.add(standardMoney, contractPayedMap.get(financeSubjId)));
			} else {
				contractPayedMap.put(financeSubjId, standardMoney);
			}
			
		}
		
		//制作合同
		List<Map<String, Object>> contractProduceList = this.contractProduceService.queryByAnvanceCondition(crewId, null, null, null, null, null, null, paymentStartDate, paymentEndDate);
		for (Map<String, Object> contractBudget : contractProduceList) {
			String financeSubjId = (String) contractBudget.get("financeSubjId");
			Double payedMoney = (Double) contractBudget.get("payedMoney");
			Double exchangeRate = (Double) contractBudget.get("exchangeRate");
			
			Double standardMoney = BigDecimalUtil.multiply(payedMoney, exchangeRate);
			
			if (contractPayedMap.containsKey(financeSubjId)) {
				contractPayedMap.put(financeSubjId, BigDecimalUtil.add(standardMoney, contractPayedMap.get(financeSubjId)));
			} else {
				contractPayedMap.put(financeSubjId, standardMoney);
			}
		}
		
		return contractPayedMap;
	}
	
	/**
	 * 生成借款的预算信息
	 * @param crewId
	 */
	private Map<String, Double> genLoanBudget(String crewId) {
		Map<String, Double> loanBugetMap = new HashMap<String, Double>();
		List<Map<String, Object>> loanBudget = this.loanInfoService.queryLoanBudget(crewId);
		
		for (Map<String, Object> contractBudget : loanBudget) {
			String financeSubjId = (String) contractBudget.get("financeSubjId");
			Double money = (Double) contractBudget.get("money");
			Double exchangeRate = (Double) contractBudget.get("exchangeRate");
			
			Double standardMoney = BigDecimalUtil.multiply(money, exchangeRate);
			
			if (loanBugetMap.containsKey(financeSubjId)) {
				loanBugetMap.put(financeSubjId, BigDecimalUtil.add(standardMoney, loanBugetMap.get(financeSubjId)));
			} else {
				loanBugetMap.put(financeSubjId, standardMoney);
			}
			
		}
		
		return loanBugetMap;
	}
	
	/**
	 * 生成借款的支付信息
	 * @param crewId
	 */
	private Map<String, Double> genLoanPayed(String crewId, String paymentStartDate, String paymentEndDate) {
		Map<String, Double> loanPayedMap = new HashMap<String, Double>();
		List<Map<String, Object>> loanPayInfoList = this.loanInfoService.queryLoanWithPaymentInfo(crewId, null, null, null, true, paymentStartDate, paymentEndDate, null);
		
		for (Map<String, Object> contractBudget : loanPayInfoList) {
			String financeSubjId = (String) contractBudget.get("financeSubjId");
			Double money = (Double) contractBudget.get("money");
			Double exchangeRate = (Double) contractBudget.get("exchangeRate");
			Double leftMoney = (Double) contractBudget.get("leftMoney");
			
			Double payedMoney = BigDecimalUtil.subtract(money, leftMoney);
			
			Double standardMoney = BigDecimalUtil.multiply(payedMoney, exchangeRate);
			
			if (loanPayedMap.containsKey(financeSubjId)) {
				loanPayedMap.put(financeSubjId, BigDecimalUtil.add(standardMoney, loanPayedMap.get(financeSubjId)));
			} else {
				loanPayedMap.put(financeSubjId, standardMoney);
			}
			
		}
		
		return loanPayedMap;
	}
	
	/**
	 * 封装借款单的付款单列表数据
	 * @param crewId
	 * @return
	 */
	private List<Map<String, Object>> genLoanPaymentList(String crewId, String financeSubjId){
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> loanPayInfoList = this.loanInfoService.queryLoanPaymentList(crewId,financeSubjId);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		//刷新缓存
		financeSubjectService.refreshCachedSubjectList(crewId);
		for (Map<String, Object> contractBudget : loanPayInfoList) {
			//判断付款单id是否为空，若付款单id为空，表示没有关联借款单
			String paymentId = (String) contractBudget.get("paymentId");
			if (StringUtils.isNotBlank(paymentId)) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				
				Double exchangeRate = (Double) contractBudget.get("exchangeRate");
				
				Double payedMoney =(Double) contractBudget.get("forLoanMoney");
				
				Double standardMoney = BigDecimalUtil.multiply(payedMoney, exchangeRate);
				
				//取出财务科目的id
				String financeSubjIdStr = (String) contractBudget.get("paymentSubjId");
				String[] financeSubjIdArr = financeSubjIdStr.split(",");
				String financeSubjName = "";
				for (String financeId : financeSubjIdArr) {
					if (financeSubjName == "") {
						financeSubjName = financeSubjectService.getFinanceSubjName(financeId);
					}else {
						financeSubjName = financeSubjName + "|" + financeSubjectService.getFinanceSubjName(financeId);
					}
				}
					
				//分装数据
				dataMap.put("paymentReceiptNo", contractBudget.get("paymentReceiptNo"));
				dataMap.put("paymentDate",format.format(contractBudget.get("paymentDate")));
				dataMap.put("payeeName", contractBudget.get("payeeName"));
				dataMap.put("paymentSummary", contractBudget.get("paymentSummary"));
				dataMap.put("paymentWay", contractBudget.get("wayName"));
				if (dataMap.containsKey("totalMoney")) {
					dataMap.put("totalMoney", BigDecimalUtil.add(standardMoney, Double.parseDouble(dataMap.get("totalMoney")+"")));
				} else {
					dataMap.put("totalMoney", standardMoney);
				}
				dataMap.put("financeSubjName", financeSubjName);
				
				resultList.add(dataMap);
			}
			
		}
		
		return resultList;
	}
	
	/**
	 * 把按照父子结构封装起来的财务科目展开
	 * @param budgetDtoList
	 * @return
	 */
	private List<Map<String, Object>> genBudgetInfo(List<Map<String, Object>> budgetInfoMapList, List<BudgetInfoDto> budgetDtoList) {
		List<Map<String, Object>> myBudgetInfoMapList = new ArrayList<Map<String, Object>>();
		
		for (BudgetInfoDto budgetInfoDto : budgetDtoList) {
			String financeSubjId = budgetInfoDto.getFinanceSubjId();
			String financeSubjName = budgetInfoDto.getFinanceSubjName();
			String financeSubjParentId = budgetInfoDto.getFinanceSubjParentId();
			String remark = budgetInfoDto.getRemark();
			Integer level = budgetInfoDto.getLevel();
			boolean hasChildren = budgetInfoDto.isHasChildren();
			Double contractBudget = budgetInfoDto.getContractBudget();
			Double contractPayed = budgetInfoDto.getContractPayed();
			Double loanBudget = budgetInfoDto.getLoanBudget();
			Double loanPayed = budgetInfoDto.getLoanPayed();

			Map<String, Object> budgetInfoMap = new HashMap<String, Object>();
			budgetInfoMap.put("financeSubjId", financeSubjId);
			budgetInfoMap.put("financeSubjName", financeSubjName);
			budgetInfoMap.put("financeSubjParentId", financeSubjParentId);
			budgetInfoMap.put("remark", remark);
			budgetInfoMap.put("hasChildren", hasChildren);
			budgetInfoMap.put("level", level);
			budgetInfoMap.put("sequence", budgetInfoDto.getSequence());
			
			Double totalBadgetMoney = 0.0;	//总预算
			Double totalPayedMoney = 0.0;	//总支出
			Double totalHasReceiptMoney = 0.00; //有票总支出
			Double totalNoreceiptMoney = 0.00; //无票总支出
			
			List<BudgetCurrencyDto> budgetCurrencyList = budgetInfoDto.getBudgetCurrencyList();
			for (BudgetCurrencyDto budgetCurrencyDto : budgetCurrencyList) {
				Double money = budgetCurrencyDto.getMoney();
				Double settleMoney = budgetCurrencyDto.getSettleMoney();
				Double exchangeRate = budgetCurrencyDto.getExchangeRate();
				Double hasReceiptMoney = budgetCurrencyDto.getHasReceiptMoney();
				Double noReceiptMoney = budgetCurrencyDto.getNoReceiptMoney();
				if (hasReceiptMoney == null) {
					hasReceiptMoney = 0.00;
				}
				if (noReceiptMoney == null) {
					noReceiptMoney = 0.00;
				}
				totalBadgetMoney = BigDecimalUtil.add(totalBadgetMoney, BigDecimalUtil.multiply(money, exchangeRate));
				totalPayedMoney = BigDecimalUtil.add(totalPayedMoney, BigDecimalUtil.multiply(settleMoney, exchangeRate));
				totalHasReceiptMoney = BigDecimalUtil.add(totalHasReceiptMoney, BigDecimalUtil.multiply(hasReceiptMoney, exchangeRate));
				totalNoreceiptMoney = BigDecimalUtil.add(totalNoreceiptMoney, BigDecimalUtil.multiply(noReceiptMoney, exchangeRate));
			}
			Double totalLeftMoney = BigDecimalUtil.subtract(totalBadgetMoney, totalPayedMoney);	//总结余
			
			Double totalPayedRate = 0.0;	//总支出比例
			if (totalBadgetMoney != null && totalBadgetMoney != 0) {
				totalPayedRate = BigDecimalUtil.divide(totalPayedMoney, totalBadgetMoney);
			}
			
			Double contractLeft = BigDecimalUtil.subtract(contractBudget, contractPayed);	//合同待付
			Double loanLeft = BigDecimalUtil.subtract(loanBudget, loanPayed);	//借款待还
			Double contractPayedRate = 0.0;	//合同支付比例
			if (contractBudget != null && contractBudget != 0) {
				contractPayedRate = BigDecimalUtil.divide(contractPayed, contractBudget);
			}
			Double loanPayedRate = 0.0;	//借款支付比例
			if (loanBudget != null && loanBudget != 0) {
				loanPayedRate = BigDecimalUtil.divide(loanPayed, loanBudget);
			}
			
			Double flexibleMoney = BigDecimalUtil.subtract(totalBadgetMoney, BigDecimalUtil.add(contractBudget, loanLeft));	//可机动费用
			Double flexibleRate = 0.0;
			if (totalBadgetMoney != null && totalBadgetMoney != 0) {
				flexibleRate = BigDecimalUtil.divide(flexibleMoney, totalBadgetMoney);
			}
			
			budgetInfoMap.put("totalBadgetMoney", this.df1.format(totalBadgetMoney));			
			budgetInfoMap.put("totalPayedMoney", this.df1.format(totalPayedMoney));
			budgetInfoMap.put("totalHasReceiptMoney", this.df1.format(totalHasReceiptMoney));
			budgetInfoMap.put("totalNoreceiptMoney", this.df1.format(totalNoreceiptMoney));
			budgetInfoMap.put("totalLeftMoney", this.df1.format(totalLeftMoney));			
			budgetInfoMap.put("totalPayedRate", this.df2.format(totalPayedRate * 100) + "%");
			budgetInfoMap.put("contractBudgetMoney", this.df1.format(contractBudget));
			budgetInfoMap.put("contractPayedMoney", this.df1.format(contractPayed));
			budgetInfoMap.put("contractLeftMoney", this.df1.format(contractLeft));
			budgetInfoMap.put("contractPayedRate", this.df2.format(contractPayedRate * 100) + "%");
			budgetInfoMap.put("loanBudgetMoney", this.df1.format(loanBudget));
			budgetInfoMap.put("loanPayedMoney", this.df1.format(loanPayed));
			budgetInfoMap.put("loanLeftMoney", this.df1.format(loanLeft));
			budgetInfoMap.put("loanPayedRate", this.df2.format(loanPayedRate * 100) + "%");
			budgetInfoMap.put("flexibleMoney", this.df1.format(flexibleMoney));
			budgetInfoMap.put("flexibleRate", this.df2.format(flexibleRate * 100) + "%");
			
			myBudgetInfoMapList.add(budgetInfoMap);
			
			List<BudgetInfoDto> children = budgetInfoDto.getChildren();
			if (children != null && children.size() > 0) {
				myBudgetInfoMapList.addAll(this.genBudgetInfo(myBudgetInfoMapList, children));
			}
		}
		
		return myBudgetInfoMapList;
	}
	
	/**
	 * 递归财务预算DTO
	 * @param originalBudgetInfoList
	 * @return
	 */
	private List<BudgetInfoDto> loopBudgetInfoDto(List<BudgetInfoDto> childBudgetInfoList, List<BudgetInfoDto> originalBudgetInfoList) {
		List<BudgetInfoDto> myBudgetInfoList = new ArrayList<BudgetInfoDto>();
		
		/*
		 * 首先过滤出纯粹子节点科目
		 */
		List<BudgetInfoDto> parentBudgetInfoDtoList = new ArrayList<BudgetInfoDto>();
		List<BudgetInfoDto> childBudgetInfoDtoList = new ArrayList<BudgetInfoDto>();
		
		
		for (BudgetInfoDto forgBudgetDto : originalBudgetInfoList) {
			String fid = forgBudgetDto.getFinanceSubjId();
			String fparentId = forgBudgetDto.getFinanceSubjParentId();
			
			boolean isChild = false;
			boolean isParent = false;
			for (BudgetInfoDto corgBudgetDto : originalBudgetInfoList) {
				String cid = corgBudgetDto.getFinanceSubjId();
				String cparentId = corgBudgetDto.getFinanceSubjParentId();
				
				if (fid.equals(cparentId)) {
					isParent = true;
				}
				if (fparentId.equals(cid)) {
					isChild = true;
				}
			}
			
			//双层循环遍历科目列表，区分中哪些科目是别人的子科目，哪些科目是别人的父科目
			//因为数据嵌套多层，过滤出的这两类数据必然会有重合的地方，但是childBudgetInfoDtoList中有而parentBudgetInfoDtoList没有的数据必然是叶子节点
			if (isChild || (!isParent && !isChild)) {
				childBudgetInfoDtoList.add(forgBudgetDto);
			}
			if (isParent) {
				parentBudgetInfoDtoList.add(forgBudgetDto);
			}
		}
		
		//childBudgetInfoDtoList中有而parentBudgetInfoDtoList没有的数据必然是叶子节点
		List<BudgetInfoDto> leafBudgetInfoDtoList = new ArrayList<BudgetInfoDto>();
		for (BudgetInfoDto corgBudgetDto : childBudgetInfoDtoList) {
			boolean eixst = false;
			for (BudgetInfoDto forgBudgetDto : parentBudgetInfoDtoList) {
				if (corgBudgetDto.getFinanceSubjId().equals(forgBudgetDto.getFinanceSubjId())) {
					eixst = true;
					break;
				}
			}
			
			if (!eixst) {
				leafBudgetInfoDtoList.add(corgBudgetDto);
			}
		}
		
		
		/*
		 * 为最后的结果字段赋值
		 * leafBudgetInfoDtoList表示当前循环中的叶子科目
		 * 但是相对于上一层传过来的childBudgetInfoList，leafBudgetInfoDtoList中有些数据为childBudgetInfoList中数据的父科目
		 * 因此，此处对比出leafBudgetInfoDtoList中每个科目的子科目，然后为相应字段赋值
		 * 
		 * 如果数据在childBudgetInfoList存在且在leafBudgetInfoDtoList中找不到任何父科目，则说明此数据层级也为当前循环的叶子科目
		 */
		for (BudgetInfoDto leafBudgetDto : leafBudgetInfoDtoList) {
			
			List<BudgetInfoDto> children = new ArrayList<BudgetInfoDto>();	//子科目
			List<BudgetCurrencyDto> fbudgetCurrencyList = leafBudgetDto.getBudgetCurrencyList();	//科目对应的货币信息列表
			
			Double contractBudget = leafBudgetDto.getContractBudget();
			Double loanBudget = leafBudgetDto.getLoanBudget();
			Double contractPayed = leafBudgetDto.getContractPayed();
			Double loanPayed = leafBudgetDto.getLoanPayed();
			for (BudgetInfoDto corgBudgetDto : childBudgetInfoList) {
				
				if (leafBudgetDto.getFinanceSubjId().equals(corgBudgetDto.getFinanceSubjParentId())) {
					children.add(corgBudgetDto);
					
					contractBudget = BigDecimalUtil.add(contractBudget, corgBudgetDto.getContractBudget());
					loanBudget = BigDecimalUtil.add(loanBudget, corgBudgetDto.getLoanBudget());
					contractPayed = BigDecimalUtil.add(contractPayed, corgBudgetDto.getContractPayed());
					loanPayed = BigDecimalUtil.add(loanPayed, corgBudgetDto.getLoanPayed());
					
					//把子科目中的每个货币总金额加到父科目中每个货币总金额上
					for (BudgetCurrencyDto fcurrencyDto : fbudgetCurrencyList) {
						for (BudgetCurrencyDto ccurrencyDto : corgBudgetDto.getBudgetCurrencyList()) {
							if (ccurrencyDto.getCurrencyId().equals(fcurrencyDto.getCurrencyId())) {
								fcurrencyDto.setMoney(BigDecimalUtil.add(fcurrencyDto.getMoney(), ccurrencyDto.getMoney()));
								fcurrencyDto.setHasReceiptMoney(BigDecimalUtil.add(fcurrencyDto.getHasReceiptMoney()==null?0.00:fcurrencyDto.getHasReceiptMoney(), ccurrencyDto.getHasReceiptMoney()==null?0.00:ccurrencyDto.getHasReceiptMoney()));
								fcurrencyDto.setNoReceiptMoney(BigDecimalUtil.add(fcurrencyDto.getNoReceiptMoney()==null?0.00:fcurrencyDto.getNoReceiptMoney(), ccurrencyDto.getNoReceiptMoney()==null?0.00:ccurrencyDto.getNoReceiptMoney()));
								fcurrencyDto.setSettleMoney(BigDecimalUtil.add(fcurrencyDto.getSettleMoney(), ccurrencyDto.getSettleMoney()));
								break;
							}
						}
					}
					
				}
			}
			
			leafBudgetDto.setContractBudget(contractBudget);
			leafBudgetDto.setContractPayed(contractPayed);
			leafBudgetDto.setLoanBudget(loanBudget);
			leafBudgetDto.setLoanPayed(loanPayed);
			
			leafBudgetDto.setBudgetCurrencyList(fbudgetCurrencyList);
			leafBudgetDto.setChildren(children);
			if (children != null && children.size() > 0) {
				leafBudgetDto.setHasChildren(true);
			}
			
			myBudgetInfoList.add(leafBudgetDto);
		}
		
		for (BudgetInfoDto corgBudgetDto : childBudgetInfoList) {
			boolean exist = false;
			for (BudgetInfoDto leafBudgetDto : leafBudgetInfoDtoList) {
				if (corgBudgetDto.getFinanceSubjParentId().equals(leafBudgetDto.getFinanceSubjId())) {
					exist = true;
					break;
				}
			}
			
			if (!exist) {
				myBudgetInfoList.add(corgBudgetDto);
			}
		}
		
		
		//如果当前遍历中没有任何父科目了，则说明已经遍历完了，不需要递归了
		if (parentBudgetInfoDtoList.size() > 0) {
			originalBudgetInfoList.removeAll(myBudgetInfoList);
			myBudgetInfoList = this.loopBudgetInfoDto(myBudgetInfoList, originalBudgetInfoList);
		}
		
		return myBudgetInfoList;
	}
	
	/**
	 * 查询出当前财务科目所包含的合同列表
	 * @param request
	 * @param financeSubjId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryFinanceSubjContract")
	public Map<String, Object> queryFinanceSubjContract(HttpServletRequest request, String financeSubjId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(financeSubjId)) {
				throw new IllegalArgumentException("请选择要查看的财务科目");
			}
			
			String crewId = this.getCrewId(request);
			
			//定义总数据列表
			List<Map<String, Object>> totalList = new ArrayList<Map<String,Object>>();
			
			//合计金额
			Double allMoney = 0.0;
			
			//遍历集合，取出当前财务科目的合同
			//演员合同
			List<Map<String, Object>> contractActorBudgetList = this.contractActorService.queryContractActorBudget(crewId);
			for (Map<String, Object> map : contractActorBudgetList) { 
				if (map != null) {
					
					//取出财务id
					String actorSubjId = (String) map.get("financeSubjId");
					//自定义演员姓名字段
					String actorName = (String) map.get("actorName");
					map.put("contractName", actorName);
					int payWay = (Integer) map.get("payWay");
					String payWayStr = "";
					if (payWay == 1) {
						payWayStr = "按阶段支付";
					}else if (payWay == 2) {
						payWayStr = "按月支付";
					}else if (payWay == 3) {
						payWayStr = "按日支付（每月结算）";
					}else if (payWay == 4) {
						payWayStr = "按日支付（定期结算）";
					}
					map.remove("payWay");
					map.put("payWay", payWayStr);
					
					//将金额统一为本位币结算
					Double totalMoney = (Double) map.get("totalMoney");
					Double exchangeRate = (Double) map.get("exchangeRate");
					
					Double standardMoney = BigDecimalUtil.multiply(totalMoney, exchangeRate);
					
					if (map.containsKey("currencyMoney")) {
						map.put("currencyMoney", BigDecimalUtil.add(standardMoney, Double.parseDouble(map.containsKey("currencyMoney")+"")));
					} else {
						map.put("currencyMoney", standardMoney);
					}
					
					if (StringUtils.isNotBlank(actorSubjId) && financeSubjId.equals(actorSubjId)) {
						totalList.add(map);
						allMoney = BigDecimalUtil.add(allMoney , standardMoney);
					}
				}
			}
			
			//职员合同
			List<Map<String, Object>> contractWorkerBudget = this.contractWorkerService.queryContractWorkerBudget(crewId);
			for (Map<String, Object> map : contractWorkerBudget) {
				if (map != null) {
					//取出财务id
					String actorSubjId = (String) map.get("financeSubjId");
					//自定义职员姓名字段
					String workerName = (String) map.get("workerName");
					map.put("contractName", workerName);
					int payWay = (Integer) map.get("payWay");
					String payWayStr = "";
					if (payWay == 1) {
						payWayStr = "按阶段支付";
					}else if (payWay == 2) {
						payWayStr = "按月支付";
					}else if (payWay == 3) {
						payWayStr = "按日支付（每月结算）";
					}else if (payWay == 4) {
						payWayStr = "按日支付（定期结算）";
					}
					map.remove("payWay");
					map.put("payWay", payWayStr);
					
					//将金额统一为本位币结算
					Double totalMoney = (Double) map.get("totalMoney");
					Double exchangeRate = (Double) map.get("exchangeRate");
					
					Double standardMoney = BigDecimalUtil.multiply(totalMoney, exchangeRate);
					
					if (map.containsKey("currencyMoney")) {
						map.put("currencyMoney", BigDecimalUtil.add(standardMoney, Double.parseDouble(map.containsKey("currencyMoney")+"")));
					} else {
						map.put("currencyMoney", standardMoney);
					}
					
					
					if (StringUtils.isNotBlank(actorSubjId) && financeSubjId.equals(actorSubjId)) {
						allMoney = BigDecimalUtil.add(allMoney , standardMoney);
						
						totalList.add(map);
					}
				}
			}
			//制作合同
			List<Map<String, Object>> contractProduceBudget = this.contractProduceService.queryContractProduceBudget(crewId);
			for (Map<String, Object> map : contractProduceBudget) {
				if (map != null) {
					//取出财务id
					String actorSubjId = (String) map.get("financeSubjId");
					//自定义制作合同公司名称字段
					String company = (String) map.get("company");
					map.put("contractName", company);
					int payWay = (Integer) map.get("payWay");
					String payWayStr = "";
					if (payWay == 1) {
						payWayStr = "按阶段支付";
					}else if (payWay == 2) {
						payWayStr = "按月支付";
					}else if (payWay == 3) {
						payWayStr = "按日支付（每月结算）";
					}else if (payWay == 4) {
						payWayStr = "按日支付（定期结算）";
					}
					map.remove("payWay");
					map.put("payWay", payWayStr);
					
					//将金额统一为本位币结算
					Double totalMoney = (Double) map.get("totalMoney");
					Double exchangeRate = (Double) map.get("exchangeRate");
					
					Double standardMoney = BigDecimalUtil.multiply(totalMoney, exchangeRate);
					
					if (map.containsKey("currencyMoney")) {
						map.put("currencyMoney", BigDecimalUtil.add(standardMoney, Double.parseDouble(map.containsKey("currencyMoney")+"")));
					} else {
						map.put("currencyMoney", standardMoney);
					}
					
					if (StringUtils.isNotBlank(actorSubjId) && financeSubjId.equals(actorSubjId)) {
						allMoney = BigDecimalUtil.add(allMoney , standardMoney);
						
						totalList.add(map);
					}
				}
			}
			
			resultMap.put("allMoney", allMoney);
			resultMap.put("totalList", totalList);
			message = "查询成功";
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，查询失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	
	/**
	 * 导出费用结算的合同列表
	 * @param reques
	 * @param financeSubjId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exportFinanceSubjContract")
	public Map<String, Object> exportFinanceSubjContract(HttpServletRequest request, String financeSubjId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		Map<String, Object> data = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			if (StringUtils.isBlank(financeSubjId)) {
				throw new IllegalArgumentException("请选择要查看的财务科目");
			}
			
			String crewId = this.getCrewId(request);
			String crewName = this.getSessionCrewInfo(request).getCrewName();
			//定义总数据列表
			List<Map<String, Object>> totalList = new ArrayList<Map<String,Object>>();
			
			//合计金额
			Double allMoney = 0.0;
			
			//遍历集合，取出当前财务科目的合同
			//演员合同
			List<Map<String, Object>> contractActorBudgetList = this.contractActorService.queryContractActorBudget(crewId);
			for (Map<String, Object> map : contractActorBudgetList) { 
				if (map != null) {
					
					//取出财务id
					String actorSubjId = (String) map.get("financeSubjId");
					//自定义演员姓名字段
					String actorName = (String) map.get("actorName");
					map.put("contractName", actorName);
					int payWay = (Integer) map.get("payWay");
					String payWayStr = "";
					if (payWay == 1) {
						payWayStr = "按阶段支付";
					}else if (payWay == 2) {
						payWayStr = "按月支付";
					}else if (payWay == 3) {
						payWayStr = "按日支付（每月结算）";
					}else if (payWay == 4) {
						payWayStr = "按日支付（定期结算）";
					}
					map.remove("payWay");
					map.put("payWay", payWayStr);
					
					//将金额统一为本位币结算
					Double totalMoney = (Double) map.get("totalMoney");
					Double exchangeRate = (Double) map.get("exchangeRate");
					
					Double standardMoney = BigDecimalUtil.multiply(totalMoney, exchangeRate);
					
					if (map.containsKey("currencyMoney")) {
						map.put("currencyMoney", BigDecimalUtil.add(standardMoney, Double.parseDouble(map.containsKey("currencyMoney")+"")));
					} else {
						map.put("currencyMoney", standardMoney);
					}
					
					if (StringUtils.isNotBlank(actorSubjId) && financeSubjId.equals(actorSubjId)) {
						totalList.add(map);
						allMoney = BigDecimalUtil.add(allMoney , standardMoney);
					}
				}
			}
			
			//职员合同
			List<Map<String, Object>> contractWorkerBudget = this.contractWorkerService.queryContractWorkerBudget(crewId);
			for (Map<String, Object> map : contractWorkerBudget) {
				if (map != null) {
					//取出财务id
					String actorSubjId = (String) map.get("financeSubjId");
					//自定义职员姓名字段
					String workerName = (String) map.get("workerName");
					map.put("contractName", workerName);
					int payWay = (Integer) map.get("payWay");
					String payWayStr = "";
					if (payWay == 1) {
						payWayStr = "按阶段支付";
					}else if (payWay == 2) {
						payWayStr = "按月支付";
					}else if (payWay == 3) {
						payWayStr = "按日支付（每月结算）";
					}else if (payWay == 4) {
						payWayStr = "按日支付（定期结算）";
					}
					map.remove("payWay");
					map.put("payWay", payWayStr);
					
					//将金额统一为本位币结算
					Double totalMoney = (Double) map.get("totalMoney");
					Double exchangeRate = (Double) map.get("exchangeRate");
					
					Double standardMoney = BigDecimalUtil.multiply(totalMoney, exchangeRate);
					
					if (map.containsKey("currencyMoney")) {
						map.put("currencyMoney", BigDecimalUtil.add(standardMoney, Double.parseDouble(map.containsKey("currencyMoney")+"")));
					} else {
						map.put("currencyMoney", standardMoney);
					}
					
					
					if (StringUtils.isNotBlank(actorSubjId) && financeSubjId.equals(actorSubjId)) {
						allMoney = BigDecimalUtil.add(allMoney , standardMoney);
						
						totalList.add(map);
					}
				}
			}
			//制作合同
			List<Map<String, Object>> contractProduceBudget = this.contractProduceService.queryContractProduceBudget(crewId);
			for (Map<String, Object> map : contractProduceBudget) {
				if (map != null) {
					//取出财务id
					String actorSubjId = (String) map.get("financeSubjId");
					//自定义制作合同公司名称字段
					String company = (String) map.get("company");
					map.put("contractName", company);
					int payWay = (Integer) map.get("payWay");
					String payWayStr = "";
					if (payWay == 1) {
						payWayStr = "按阶段支付";
					}else if (payWay == 2) {
						payWayStr = "按月支付";
					}else if (payWay == 3) {
						payWayStr = "按日支付（每月结算）";
					}else if (payWay == 4) {
						payWayStr = "按日支付（定期结算）";
					}
					map.remove("payWay");
					map.put("payWay", payWayStr);
					
					//将金额统一为本位币结算
					Double totalMoney = (Double) map.get("totalMoney");
					Double exchangeRate = (Double) map.get("exchangeRate");
					
					Double standardMoney = BigDecimalUtil.multiply(totalMoney, exchangeRate);
					
					if (map.containsKey("currencyMoney")) {
						map.put("currencyMoney", BigDecimalUtil.add(standardMoney, Double.parseDouble(map.containsKey("currencyMoney")+"")));
					} else {
						map.put("currencyMoney", standardMoney);
					}
					
					if (StringUtils.isNotBlank(actorSubjId) && financeSubjId.equals(actorSubjId)) {
						allMoney = BigDecimalUtil.add(allMoney , standardMoney);
						
						totalList.add(map);
					}
				}
			}
			
			//格式化日期，和金额
			for (Map<String, Object> map : totalList) {
				//格式化日期
				String contractDateStr = "";
				if (map.get("contractDate") != null) {
					contractDateStr = sdf.format((Date) map.get("contractDate"));
				}
				map.put("contractDate", contractDateStr);
				
				//格式化金额
				String currencyMoneyStr = map.get("currencyMoney")+"";
				if (StringUtils.isNotBlank(currencyMoneyStr)) {
					map.put("currencyMoney", this.df1.format(Double.parseDouble(currencyMoneyStr)));
				}
			}
			
			//查询出财务科目
			//刷新缓存
			financeSubjectService.refreshCachedSubjectList(crewId);
			String financeSubjName = financeSubjectService.getFinanceSubjName(financeSubjId);
			
			data.put("allMoney", this.df1.format(allMoney));
			data.put("totalList", totalList);
			data.put("financeSubjName", financeSubjName);
			
			
			//生成导出文件
			Date nowDate = new Date();
        	//导出文件名
			String fileName = "《"+ crewName + "》 "+ financeSubjName+" 合同列表";
			//获取模板文件地址配置
			Properties property = PropertiesUitls.fetchProperties("/config.properties");
			String srcfilePath =  property.getProperty("fianceSubj-contractlist");
			//生成下载路径
			String downloadPath = property.getProperty("downloadPath")+fileName+".xls";
			File pathFile = new File(property.getProperty("downloadPath"));
			if(!pathFile.isDirectory()){
				pathFile.mkdirs();
			}
			
			//生成可下载的excel文件
			viewInfoService.exportViewToExcelTemplate(srcfilePath, data, downloadPath);
			resultMap.put("downloadPath", downloadPath);
			message = "导出成功!";
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，导出合同列表失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 获取已支付合同中 付款单列表
	 * @param request
	 * @param financeSubjId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryContractPaymentList")
	public Map<String, Object> queryContractPaymentList(HttpServletRequest request, String financeSubjId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(financeSubjId)) {
				throw new IllegalArgumentException("请选择要查看的财务科目");
			}
			
			String crewId = this.getCrewId(request);
			
			//定义返回结果列表
			List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
			//刷新缓存
			financeSubjectService.refreshCachedSubjectList(crewId);
			
			//合计金额
			Double allMoney = 0.0;
			
			//演员合同
			List<Map<String, Object>> contractActorList = this.contractActorService.queryContractActorPaymentList(crewId, financeSubjId);
			for (Map<String, Object> map : contractActorList) {
				//取出财务科目的id
				String financeSubjIdStr = (String) map.get("financeSubjId");
				String[] financeSubjIdArr = financeSubjIdStr.split(",");
				String financeSubjName = "";
				for (String financeId : financeSubjIdArr) {
					if (financeSubjName == "") {
						financeSubjName = financeSubjectService.getFinanceSubjName(financeId);
					}else {
						financeSubjName = financeSubjName + "|" + financeSubjectService.getFinanceSubjName(financeId);
					}
				}
					map.put("financeSubjName", financeSubjName);
					
					//将金额统一为本位币结算
					Double totalMoney = (Double) map.get("paymentTotalMoney");
					Double exchangeRate = (Double) map.get("exchangeRate");
					
					Double standardMoney = BigDecimalUtil.multiply(totalMoney, exchangeRate);
					
					if (map.containsKey("currencyMoney")) {
						map.put("currencyMoney", BigDecimalUtil.add(standardMoney, Double.parseDouble(map.containsKey("currencyMoney")+"")));
					} else {
						map.put("currencyMoney", standardMoney);
					}
					
					allMoney = BigDecimalUtil.add(allMoney , standardMoney);
					
					resultList.add(map);
			}
			
			//职员合同
			List<Map<String, Object>> contractWorkerList = this.contractWorkerService.queryContractWorkerPaymentList(crewId, financeSubjId);
			for (Map<String, Object> map : contractWorkerList) {
				String financeSubjIdStr = (String) map.get("financeSubjId");
				String[] financeSubjIdArr = financeSubjIdStr.split(",");
				String financeSubjName = "";
				for (String financeId : financeSubjIdArr) {
					if (financeSubjName == "") {
						financeSubjName = financeSubjectService.getFinanceSubjName(financeId);
					}else {
						financeSubjName = financeSubjName + "|" + financeSubjectService.getFinanceSubjName(financeId);
					}
				}
					map.put("financeSubjName", financeSubjName);
					
					//将金额统一为本位币结算
					Double totalMoney = (Double) map.get("paymentTotalMoney");
					Double exchangeRate = (Double) map.get("exchangeRate");
					
					Double standardMoney = BigDecimalUtil.multiply(totalMoney, exchangeRate);
					
					if (map.containsKey("currencyMoney")) {
						map.put("currencyMoney", BigDecimalUtil.add(standardMoney, Double.parseDouble(map.containsKey("currencyMoney")+"")));
					} else {
						map.put("currencyMoney", standardMoney);
					}
					
					allMoney = BigDecimalUtil.add(allMoney , standardMoney);
					
					resultList.add(map);
			}
			
			//制作合同
			List<Map<String, Object>> contractProduceList = this.contractProduceService.queryContractProducePaymentList(crewId, financeSubjId);
			for (Map<String, Object> map : contractProduceList) {
				String financeSubjIdStr = (String) map.get("financeSubjId");
				String[] financeSubjIdArr = financeSubjIdStr.split(",");
				String financeSubjName = "";
				for (String financeId : financeSubjIdArr) {
					if (financeSubjName == "") {
						financeSubjName = financeSubjectService.getFinanceSubjName(financeId);
					}else {
						financeSubjName = financeSubjName + "|" + financeSubjectService.getFinanceSubjName(financeId);
					}
				}
				map.put("financeSubjName", financeSubjName);
				
				//将金额统一为本位币结算
				Double totalMoney = (Double) map.get("paymentTotalMoney");
				Double exchangeRate = (Double) map.get("exchangeRate");
				
				Double standardMoney = BigDecimalUtil.multiply(totalMoney, exchangeRate);
				
				if (map.containsKey("currencyMoney")) {
					map.put("currencyMoney", BigDecimalUtil.add(standardMoney, Double.parseDouble(map.containsKey("currencyMoney")+"")));
				} else {
					map.put("currencyMoney", standardMoney);
				}
				
				allMoney = BigDecimalUtil.add(allMoney , standardMoney);
				
				resultList.add(map);
			}
			
			resultMap.put("allMoney", allMoney);
			resultMap.put("totalList", resultList);
			message = "查询成功";
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，获取已支付合同的付款单列表失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 导出合同关联的付款单
	 * @param request
	 * @param financeSubjId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exportContractPaymentList")
	public Map<String, Object> exportContractPaymentList(HttpServletRequest request, String financeSubjId){

		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		Map<String, Object> data = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			if (StringUtils.isBlank(financeSubjId)) {
				throw new IllegalArgumentException("请选择要查看的财务科目");
			}
			
			String crewId = this.getCrewId(request);
			
			String crewName = this.getSessionCrewInfo(request).getCrewName();
			
			//定义返回结果列表
			List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
			//刷新缓存
			financeSubjectService.refreshCachedSubjectList(crewId);
			
			//合计金额
			Double allMoney = 0.0;
			
			//演员合同
			List<Map<String, Object>> contractActorList = this.contractActorService.queryContractActorPaymentList(crewId, financeSubjId);
			for (Map<String, Object> map : contractActorList) {
				//取出财务科目的id
				String financeSubjIdStr = (String) map.get("financeSubjId");
				String[] financeSubjIdArr = financeSubjIdStr.split(",");
				String financeSubjName = "";
				for (String financeId : financeSubjIdArr) {
					if (financeSubjName == "") {
						financeSubjName = financeSubjectService.getFinanceSubjName(financeId);
					}else {
						financeSubjName = financeSubjName + "|" + financeSubjectService.getFinanceSubjName(financeId);
					}
				}
					map.put("financeSubjName", financeSubjName);
					
					//将金额统一为本位币结算
					Double totalMoney = (Double) map.get("paymentTotalMoney");
					Double exchangeRate = (Double) map.get("exchangeRate");
					
					Double standardMoney = BigDecimalUtil.multiply(totalMoney, exchangeRate);
					
					if (map.containsKey("currencyMoney")) {
						map.put("currencyMoney", BigDecimalUtil.add(standardMoney, Double.parseDouble(map.containsKey("currencyMoney")+"")));
					} else {
						map.put("currencyMoney", standardMoney);
					}
					
					allMoney = BigDecimalUtil.add(allMoney , standardMoney);
					
					resultList.add(map);
			}
			
			//职员合同
			List<Map<String, Object>> contractWorkerList = this.contractWorkerService.queryContractWorkerPaymentList(crewId, financeSubjId);
			for (Map<String, Object> map : contractWorkerList) {
				String financeSubjIdStr = (String) map.get("financeSubjId");
				String[] financeSubjIdArr = financeSubjIdStr.split(",");
				String financeSubjName = "";
				for (String financeId : financeSubjIdArr) {
					if (financeSubjName == "") {
						financeSubjName = financeSubjectService.getFinanceSubjName(financeId);
					}else {
						financeSubjName = financeSubjName + "|" + financeSubjectService.getFinanceSubjName(financeId);
					}
				}
					map.put("financeSubjName", financeSubjName);
					
					//将金额统一为本位币结算
					Double totalMoney = (Double) map.get("paymentTotalMoney");
					Double exchangeRate = (Double) map.get("exchangeRate");
					
					Double standardMoney = BigDecimalUtil.multiply(totalMoney, exchangeRate);
					
					if (map.containsKey("currencyMoney")) {
						map.put("currencyMoney", BigDecimalUtil.add(standardMoney, Double.parseDouble(map.containsKey("currencyMoney")+"")));
					} else {
						map.put("currencyMoney", standardMoney);
					}
					
					allMoney = BigDecimalUtil.add(allMoney , standardMoney);
					
					resultList.add(map);
			}
			
			//制作合同
			List<Map<String, Object>> contractProduceList = this.contractProduceService.queryContractProducePaymentList(crewId, financeSubjId);
			for (Map<String, Object> map : contractProduceList) {
				String financeSubjIdStr = (String) map.get("financeSubjId");
				String[] financeSubjIdArr = financeSubjIdStr.split(",");
				String financeSubjName = "";
				for (String financeId : financeSubjIdArr) {
					if (financeSubjName == "") {
						financeSubjName = financeSubjectService.getFinanceSubjName(financeId);
					}else {
						financeSubjName = financeSubjName + "|" + financeSubjectService.getFinanceSubjName(financeId);
					}
				}
				map.put("financeSubjName", financeSubjName);
				
				//将金额统一为本位币结算
				Double totalMoney = (Double) map.get("paymentTotalMoney");
				Double exchangeRate = (Double) map.get("exchangeRate");
				
				Double standardMoney = BigDecimalUtil.multiply(totalMoney, exchangeRate);
				
				if (map.containsKey("currencyMoney")) {
					map.put("currencyMoney", BigDecimalUtil.add(standardMoney, Double.parseDouble(map.containsKey("currencyMoney")+"")));
				} else {
					map.put("currencyMoney", standardMoney);
				}
				
				allMoney = BigDecimalUtil.add(allMoney , standardMoney);
				
				resultList.add(map);
			}
			
			//格式化日期，和金额
			for (Map<String, Object> map : resultList) {
				//格式化日期
				String contractDateStr = "";
				if (map.get("paymentDate") != null) {
					contractDateStr = sdf.format((Date) map.get("paymentDate"));
				}
				map.put("paymentDate", contractDateStr);
				
				//格式化金额
				String currencyMoneyStr = map.get("currencyMoney")+"";
				if (StringUtils.isNotBlank(currencyMoneyStr)) {
					map.put("currencyMoney", this.df1.format(Double.parseDouble(currencyMoneyStr)));
				}
			}
			
			//获取财务科目
			String financeSubjName = financeSubjectService.getFinanceSubjName(financeSubjId);
			
			data.put("allMoney", this.df1.format(allMoney));
			data.put("totalList", resultList);
			data.put("financeSubjName", financeSubjName);
			
        	//导出文件名
			String fileName = "《"+ crewName + "》 "+ financeSubjName +" 合同付款单列表";
			//获取模板文件地址配置
			Properties property = PropertiesUitls.fetchProperties("/config.properties");
			String srcfilePath =  property.getProperty("fianceSubj-contract-pay-list");
			//生成下载路径
			String downloadPath = property.getProperty("downloadPath")+fileName+".xls";
			File pathFile = new File(property.getProperty("downloadPath"));
			if(!pathFile.isDirectory()){
				pathFile.mkdirs();
			}
			
			//生成可下载的excel文件
			viewInfoService.exportViewToExcelTemplate(srcfilePath, data, downloadPath);
			resultMap.put("downloadPath", downloadPath);
			message = "导出成功!";
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，导出已支付合同的付款单列表失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	
	}
	
	/**
	 * 查询关联借款中的借款详情
	 * @param request
	 * @param financeSubjId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryFinanceLoanList")
	public Map<String, Object> queryFinanceLoanList(HttpServletRequest request, String financeSubjId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(financeSubjId)) {
				throw new IllegalArgumentException("请选择需要查看的财务科目");
			}
			
			String crewId = this.getCrewId(request);
			
			//定义合计金额
			Double allMoney = 0.0;
			
			//定义返回数据集合
			List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
			//查询数据
			List<Map<String, Object>> loanBudget = this.loanInfoService.queryLoanBudget(crewId);
			for (Map<String, Object> map : loanBudget) {
				//取出财务科目id
				String tempId = (String) map.get("financeSubjId");
				int paymentWay = (Integer) map.get("paymentWay");
				String paymentWayStr = "";
				if (paymentWay ==1) {
					paymentWayStr = "现金";
				}else if (paymentWay == 2) {
					paymentWayStr = "现金（网转）";
				}else if (paymentWay == 3) {
					paymentWayStr = "银行";
				}
				map.remove("paymentWay");
				map.put("paymentWay", paymentWayStr);
				
				//将金额统一为本位币结算
				Double totalMoney = (Double) map.get("money");
				Double exchangeRate = (Double) map.get("exchangeRate");
				
				Double standardMoney = BigDecimalUtil.multiply(totalMoney, exchangeRate);
				
				if (map.containsKey("currencyMoney")) {
					map.put("currencyMoney", BigDecimalUtil.add(standardMoney, Double.parseDouble(map.containsKey("currencyMoney")+"")));
				} else {
					map.put("currencyMoney", standardMoney);
				}
				
				if (StringUtils.isNotBlank(tempId) && financeSubjId.equals(tempId)) {
					allMoney = BigDecimalUtil.add(allMoney , standardMoney);
					
					dataList.add(map);
				}
			}
			
			resultMap.put("allMoney", allMoney);	
			resultMap.put("loanList", dataList);
			message = "查询成功";
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，查询关联借款单列表失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap; 
	}
	
	/**
	 * 导出借款单列表
	 * @param request
	 * @param financeSubjId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exportFinanceLoanList")
	public Map<String, Object> exportFinanceLoanList(HttpServletRequest request, String financeSubjId){

		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		Map<String, Object> data = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			if (StringUtils.isBlank(financeSubjId)) {
				throw new IllegalArgumentException("请选择需要查看的财务科目");
			}
			
			String crewId = this.getCrewId(request);
			
			String crewName = this.getSessionCrewInfo(request).getCrewName();
			
			//定义合计金额
			Double allMoney = 0.0;
			
			//定义返回数据集合
			List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
			//查询数据
			List<Map<String, Object>> loanBudget = this.loanInfoService.queryLoanBudget(crewId);
			for (Map<String, Object> map : loanBudget) {
				//取出财务科目id
				String tempId = (String) map.get("financeSubjId");
				int paymentWay = (Integer) map.get("paymentWay");
				String paymentWayStr = "";
				if (paymentWay ==1) {
					paymentWayStr = "现金";
				}else if (paymentWay == 2) {
					paymentWayStr = "现金（网转）";
				}else if (paymentWay == 3) {
					paymentWayStr = "银行";
				}
				map.remove("paymentWay");
				map.put("paymentWay", paymentWayStr);
				
				//将金额统一为本位币结算
				Double totalMoney = (Double) map.get("money");
				Double exchangeRate = (Double) map.get("exchangeRate");
				
				Double standardMoney = BigDecimalUtil.multiply(totalMoney, exchangeRate);
				
				if (map.containsKey("currencyMoney")) {
					map.put("currencyMoney", BigDecimalUtil.add(standardMoney, Double.parseDouble(map.containsKey("currencyMoney")+"")));
				} else {
					map.put("currencyMoney", standardMoney);
				}
				
				if (StringUtils.isNotBlank(tempId) && financeSubjId.equals(tempId)) {
					allMoney = BigDecimalUtil.add(allMoney , standardMoney);
					
					dataList.add(map);
				}
			}
			
			//格式化日期，和金额
			for (Map<String, Object> map : dataList) {
				//格式化日期
				String contractDateStr = "";
				if (map.get("loanDate") != null) {
					contractDateStr = sdf.format((Date) map.get("loanDate"));
				}
				map.put("loanDate", contractDateStr);
				
				//格式化金额
				String currencyMoneyStr = map.get("currencyMoney")+"";
				if (StringUtils.isNotBlank(currencyMoneyStr)) {
					map.put("currencyMoney", this.df1.format(Double.parseDouble(currencyMoneyStr)));
				}
			}
			
			//查询财务科目
			//刷新缓存
			financeSubjectService.refreshCachedSubjectList(crewId);

			String financeSubjName = financeSubjectService.getFinanceSubjName(financeSubjId);
			data.put("allMoney", this.df1.format(allMoney));	
			data.put("totalList", dataList);
			data.put("financeSubjName", financeSubjName);
			
			//生成导出文件
			Date nowDate = new Date();
        	//导出文件名
			String fileName = "《"+ crewName + "》 "+ financeSubjName+" 借款单列表";
			//获取模板文件地址配置
			Properties property = PropertiesUitls.fetchProperties("/config.properties");
			String srcfilePath =  property.getProperty("fianceSubj-loan-list");
			//生成下载路径
			String downloadPath = property.getProperty("downloadPath")+fileName+".xls";
			File pathFile = new File(property.getProperty("downloadPath"));
			if(!pathFile.isDirectory()){
				pathFile.mkdirs();
			}
			
			//生成可下载的excel文件
			viewInfoService.exportViewToExcelTemplate(srcfilePath, data, downloadPath);
			resultMap.put("downloadPath", downloadPath);
			message = "导出成功!";
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，导出关联借款单列表失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap; 
	
	}
	
	/**
	 * 查询借款中的付款单
	 * @param request
	 * @param financeSubjId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryLoanpaymentList")
	public Map<String, Object> queryLoanpaymentList(HttpServletRequest request, String financeSubjId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(financeSubjId)) {
				throw new IllegalArgumentException("请选择要查看的财务科目");
			}
			
			String crewId = this.getCrewId(request);
			List<Map<String,Object>> list = this.genLoanPaymentList(crewId, financeSubjId);
			
			Double allMoney = 0.0;
			//计算合计金额
			for (Map<String, Object> map : list) {
				//取出金额信息
				double totalMoney = map.get("totalMoney") == null ? 0.0 : (Double)map.get("totalMoney");
				allMoney = BigDecimalUtil.add(allMoney, totalMoney);
			}
			
			resultMap.put("allMOney", allMoney);
			resultMap.put("list", list);
			message = "查询成功";
			
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，查询借款关联的付款单列表失败";
			success =  false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	
	/**
	 * 导出还款列表
	 * @param request
	 * @param financeSubjId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exportLoanpaymentList")
	public Map<String, Object> exportLoanpaymentList(HttpServletRequest request, String financeSubjId){

		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		Map<String, Object> data = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			if (StringUtils.isBlank(financeSubjId)) {
				throw new IllegalArgumentException("请选择要查看的财务科目");
			}
			
			String crewId = this.getCrewId(request);
			
			String crewName = this.getSessionCrewInfo(request).getCrewName();
			
			List<Map<String,Object>> list = this.genLoanPaymentList(crewId, financeSubjId);
			
			Double allMoney = 0.0;
			//计算合计金额
			for (Map<String, Object> map : list) {
				//取出金额信息
				double totalMoney = map.get("totalMoney") == null ? 0.0 : (Double)map.get("totalMoney");
				allMoney = BigDecimalUtil.add(allMoney, totalMoney);
			}
			
			//格式化日期，和金额
			for (Map<String, Object> map : list) {
				//格式化日期
				String contractDateStr = "";
				if (map.get("paymentDate") != null) {
					contractDateStr = sdf.format((Date) map.get("paymentDate"));
				}
				map.put("paymentDate", contractDateStr);
				
				//格式化金额
				String currencyMoneyStr = map.get("totalMoney")+"";
				if (StringUtils.isNotBlank(currencyMoneyStr)) {
					map.put("totalMoney", this.df1.format(Double.parseDouble(currencyMoneyStr)));
				}
			}
			
			//刷新缓存
			financeSubjectService.refreshCachedSubjectList(crewId);
			String financeSubjName = financeSubjectService.getFinanceSubjName(financeSubjId);
			
			data.put("financeSubjName", financeSubjName );
			data.put("allMOney", this.df1.format(allMoney));
			data.put("totalList", list);
			
        	//导出文件名
			String fileName = "《"+ crewName + "》 "+ financeSubjName+" 还款单列表";
			//获取模板文件地址配置
			Properties property = PropertiesUitls.fetchProperties("/config.properties");
			String srcfilePath =  property.getProperty("loan-peyment-list");
			//生成下载路径
			String downloadPath = property.getProperty("downloadPath")+fileName+".xls";
			File pathFile = new File(property.getProperty("downloadPath"));
			if(!pathFile.isDirectory()){
				pathFile.mkdirs();
			}
			
			//生成可下载的excel文件
			viewInfoService.exportViewToExcelTemplate(srcfilePath, data, downloadPath);
			resultMap.put("downloadPath", downloadPath);
			message = "导出成功!";
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，查询借款关联的付款单列表失败";
			success =  false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 查询出费用结算中预算资金的总支出明细列表
	 * @param request
	 * @param financeSubjId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryFinanceBudgetPaymentList")
	public Map<String, Object> queryFinanceBudgetPaymentList(HttpServletRequest request, String financeSubjId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(financeSubjId)) {
				throw new IllegalArgumentException("请选择要查看的财务科目");
			}
			
			String crewId = this.getCrewId(request);
			
			List<Map<String,Object>> list = this.financeSubjectService.queryFinanceBudgetPaymentList(crewId, financeSubjId);
			
			//合计金额
			Double allMoney = 0.0;
			//刷新缓存
			financeSubjectService.refreshCachedSubjectList(crewId);
			for (Map<String, Object> map : list) {
				//将金额统一为本位币结算
				Double totalMoney = (Double) map.get("money");
				Double exchangeRate = (Double) map.get("exchangeRate");
				
				Double standardMoney = BigDecimalUtil.multiply(totalMoney, exchangeRate);
				
				if (map.containsKey("currencyMoney")) {
					map.put("currencyMoney", BigDecimalUtil.add(standardMoney, Double.parseDouble(map.containsKey("currencyMoney")+"")));
				} else {
					map.put("currencyMoney", standardMoney);
				}
				
				allMoney = BigDecimalUtil.add(allMoney, standardMoney);
				//取出财务科目的id
				String financeSubjIdStr = (String) map.get("id");
				String[] financeSubjIdArr = financeSubjIdStr.split(",");
				String financeSubjName = "";
				for (String financeId : financeSubjIdArr) {
					if (financeSubjName == "") {
						financeSubjName = financeSubjectService.getFinanceSubjName(financeId);
					}else {
						financeSubjName = financeSubjName + "|" + financeSubjectService.getFinanceSubjName(financeId);
					}
				}
				map.put("financeSubjName", financeSubjName);
			}
			
			resultMap.put("allMoney", allMoney);
			resultMap.put("list", list);
			message = "查询成功";
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，查询费用结算中预算资金的总支出明细列表失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 根据财务科目id获取财务科目的名称
	 * @param request
	 * @param financeSubjId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryFinanceSubjNameById")
	public Map<String, Object> queryFinanceSubjNameById(HttpServletRequest request, String financeSubjId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			
			//刷新缓存
			financeSubjectService.refreshCachedSubjectList(crewId);

			String financeSubjName = financeSubjectService.getFinanceSubjName(financeSubjId);
			
			resultMap.put("financeSubjName", financeSubjName);
			message = "查询成功";
		} catch (Exception e) {
			message = "未知错误，查询财务科目失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 导出预算总支出
	 * @param request
	 * @param financeSubjId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exportFinanceBudgetPaymentList")
	public Map<String, Object> exportFinanceBudgetPaymentList(HttpServletRequest request, String financeSubjId){

		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		Map<String, Object> data = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			if (StringUtils.isBlank(financeSubjId)) {
				throw new IllegalArgumentException("请选择要查看的财务科目");
			}
			
			String crewId = this.getCrewId(request);
			
			String crewName = this.getSessionCrewInfo(request).getCrewName();
			
			List<Map<String,Object>> list = this.financeSubjectService.queryFinanceBudgetPaymentList(crewId, financeSubjId);
			
			//合计金额
			Double allMoney = 0.0;
			//刷新缓存
			financeSubjectService.refreshCachedSubjectList(crewId);
			for (Map<String, Object> map : list) {
				//将金额统一为本位币结算
				Double totalMoney = (Double) map.get("money");
				Double exchangeRate = (Double) map.get("exchangeRate");
				
				Double standardMoney = BigDecimalUtil.multiply(totalMoney, exchangeRate);
				
				if (map.containsKey("currencyMoney")) {
					map.put("currencyMoney", BigDecimalUtil.add(standardMoney, Double.parseDouble(map.containsKey("currencyMoney")+"")));
				} else {
					map.put("currencyMoney", standardMoney);
				}
				
				allMoney = BigDecimalUtil.add(allMoney, standardMoney);
				//取出财务科目的id
				String financeSubjIdStr = (String) map.get("id");
				String[] financeSubjIdArr = financeSubjIdStr.split(",");
				String financeSubjName = "";
				for (String financeId : financeSubjIdArr) {
					if (financeSubjName == "") {
						financeSubjName = financeSubjectService.getFinanceSubjName(financeId);
					}else {
						financeSubjName = financeSubjName + "|" + financeSubjectService.getFinanceSubjName(financeId);
					}
				}
				map.put("financeSubjName", financeSubjName);
			}
			
			//格式化日期，和金额
			for (Map<String, Object> map : list) {
				//格式化日期
				String contractDateStr = "";
				if (map.get("paymentDate") != null) {
					contractDateStr = sdf.format((Date) map.get("paymentDate"));
				}
				map.put("paymentDate", contractDateStr);
				
				//格式化金额
				String currencyMoneyStr = map.get("currencyMoney")+"";
				if (StringUtils.isNotBlank(currencyMoneyStr)) {
					map.put("currencyMoney", this.df1.format(Double.parseDouble(currencyMoneyStr)));
				}
			}
			
			String financeSubjName = financeSubjectService.getFinanceSubjName(financeSubjId);
			data.put("financeSubjName", financeSubjName);
			data.put("allMoney", this.df1.format(allMoney));
			data.put("totalList", list);
			
        	//导出文件名
			String fileName = "《"+ crewName + "》 "+ financeSubjName+" 总支出列表";
			//获取模板文件地址配置
			Properties property = PropertiesUitls.fetchProperties("/config.properties");
			String srcfilePath =  property.getProperty("budget-peyment-list");
			//生成下载路径
			String downloadPath = property.getProperty("downloadPath")+fileName+".xls";
			File pathFile = new File(property.getProperty("downloadPath"));
			if(!pathFile.isDirectory()){
				pathFile.mkdirs();
			}
			
			//生成可下载的excel文件
			viewInfoService.exportViewToExcelTemplate(srcfilePath, data, downloadPath);
			resultMap.put("downloadPath", downloadPath);
			message = "导出成功!";
			
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，导出费用结算中预算资金的总支出明细列表失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	
	}
}
