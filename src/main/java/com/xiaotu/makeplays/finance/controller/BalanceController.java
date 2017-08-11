package com.xiaotu.makeplays.finance.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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
import com.xiaotu.makeplays.finance.service.ContractActorService;
import com.xiaotu.makeplays.finance.service.ContractProduceService;
import com.xiaotu.makeplays.finance.service.ContractWorkerService;
import com.xiaotu.makeplays.finance.service.CurrencyInfoService;
import com.xiaotu.makeplays.finance.service.FinanceSubjectService;
import com.xiaotu.makeplays.finance.service.LoanInfoService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.BigDecimalUtil;

/**
 * 费用管控
 * 费用管控不是一个独立的概念，是结合财务科目预算和合同、借款等支出信息组成的报表
 * @author xuchangjian 2016-8-10下午5:26:52
 */
@Controller
@RequestMapping("/balanceManager")
public class BalanceController extends BaseController {
	
	Logger logger = LoggerFactory.getLogger(BalanceController.class);
	
	DecimalFormat df = new DecimalFormat("#,##0.00");

	@Autowired
	private FinanceSubjectService financeSubjectService;
	
	@Autowired
	private CurrencyInfoService currencyInfoService;
	
	@Autowired
	private ContractActorService contractActorService;
	
	@Autowired
	private ContractWorkerService contractWorkerService;
	
	@Autowired
	private ContractProduceService contractProduceService;
	
	@Autowired
	private LoanInfoService loanInfoService;
	
	/**
	 * 跳转到费用管控页面
	 * @return
	 */
	@RequestMapping("/toBalancePage")
	public ModelAndView toBalancePage() {
		ModelAndView mv = new ModelAndView("/finance/budget/financeBalance");
		return mv;
	}
	
	/**
	 * 查询费用管控总的统计信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryTotalStatisticInfo")
	public Map<String, Object> queryTotalStatisticInfo(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
        	String crewId = this.getCrewId(request);
        	
        	//合同的预算信息
        	Double totalContractBudget = 0.00;
        	Map<String, Double> contractBudgetMap = this.genContractBudget(crewId);
        	Set<String> contractKeySet = contractBudgetMap.keySet();
        	for (String key : contractKeySet) {
        		totalContractBudget = BigDecimalUtil.add(totalContractBudget, contractBudgetMap.get(key));
        	}
        	
        	//关联借款的科目
        	Double totalLoanBudget = 0.00;
        	Map<String, Double> loanBudgetMap = this.genLoanBudget(crewId);
        	Set<String> loanKeySet = loanBudgetMap.keySet();
        	for (String key : loanKeySet) {
        		totalLoanBudget = BigDecimalUtil.add(totalLoanBudget, loanBudgetMap.get(key));
        	}
        	
        	//获取财务科目列表
        	List<Map<String, Object>> finanSubjWithBudgetList = this.financeSubjectService.queryWithBudgetInfo(crewId);
        	
        	Double totalBudget = 0.00;	//总预算金额
        	for (Map<String, Object> finanSubj : finanSubjWithBudgetList) {
        		Double money = (Double) finanSubj.get("money");	//总金额
        		Double exchangeRate = (Double) finanSubj.get("exchangeRate");	//汇率
        		
        		if (money != null) {
        			totalBudget = BigDecimalUtil.add(totalBudget, BigDecimalUtil.multiply(money, exchangeRate));
        		}
        	}
        	
        	Double leftBudget = BigDecimalUtil.subtract(totalBudget, BigDecimalUtil.add(totalContractBudget, totalLoanBudget));
        	
        	Double leftRate = 0.00;
        	if (totalBudget != 0) {
        		leftRate = BigDecimalUtil.divide(leftBudget, totalBudget);
        	}
        	
        	
        	
        	resultMap.put("totalContractBudget", totalContractBudget);
        	resultMap.put("totalLoanBudget", totalLoanBudget);
        	resultMap.put("totalBudget", totalBudget);
        	resultMap.put("leftBudget", leftBudget);
        	resultMap.put("leftRate", leftRate * 100);
        	
        } catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";

			logger.error("未知异常", e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询费用管控列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryBalanceList")
	public Map<String, Object> queryBalanceList(HttpServletRequest request) {
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
        	
        	//合同的预算信息
        	Map<String, Double> contractBudgetMap = this.genContractBudget(crewId);
        	
        	//关联借款的科目
        	Map<String, Double> loanBugetMap = this.genLoanBudget(crewId);
        	
        	//获取财务科目列表
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
        		
        		List<BudgetCurrencyDto> budgetCurrencyList = new ArrayList<BudgetCurrencyDto>();
        		
        		//把所有的货币信息封装到预算货币Dto中
        		for (CurrencyInfoModel currencyInfo : currencyInfoList) {
        			String myCurrencyId = currencyInfo.getId();
        			
        			BudgetCurrencyDto budgetCurrencyDto = new BudgetCurrencyDto();
        			budgetCurrencyDto.setCurrencyId(currencyInfo.getId());
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
        		
        		//借款预算
        		if (loanBugetMap.get(id) != null) {
        			budgetInfoDto.setLoanBudget(loanBugetMap.get(id));
        		} else {
        			budgetInfoDto.setLoanBudget(0.00);
        		}
        		
        		budgetInfoList.add(budgetInfoDto);
        	}
        	
        	
        	//把子节点的预算值向父节点上合并，并把财务科目按照父子关系做成嵌套的形式
        	List<BudgetInfoDto> budgetDtoList = this.loopBudgetInfoDto(new ArrayList<BudgetInfoDto>(), budgetInfoList);
        	
        	//把嵌套形式的财务科目平铺开来，并且把其中的货币信息做成以货币ID为键，总金额为值的形式
        	List<Map<String, Object>> balanceInfoMapList = this.genBudgetInfo(new ArrayList<Map<String, Object>>(), budgetDtoList);
        	Collections.sort(balanceInfoMapList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					int o1sequence = (Integer) o1.get("sequence");
					int o2sequence = (Integer) o2.get("sequence");
	        		return o1sequence - o2sequence;
				}
			});
        	
        	resultMap.put("balanceInfoMapList", balanceInfoMapList);
        } catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";

			logger.error("未知异常", e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
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
	 * 把按照父子结构封装起来的财务科目展开
	 * @param budgetDtoList
	 * @return
	 */
	private List<Map<String, Object>> genBudgetInfo(List<Map<String, Object>> budgetInfoMapList, List<BudgetInfoDto> budgetDtoList) {
		List<Map<String, Object>> myBudgetInfoMapList = budgetInfoMapList;
		
		for (BudgetInfoDto budgetInfoDto : budgetDtoList) {
			String financeSubjId = budgetInfoDto.getFinanceSubjId();
			String financeSubjName = budgetInfoDto.getFinanceSubjName();
			String financeSubjParentId = budgetInfoDto.getFinanceSubjParentId();
			
			Double contractBudget = budgetInfoDto.getContractBudget();
			Double loanBudget = budgetInfoDto.getLoanBudget();
			
			Map<String, Object> budgetInfoMap = new HashMap<String, Object>();
			budgetInfoMap.put("financeSubjId", financeSubjId);
			budgetInfoMap.put("financeSubjName", financeSubjName);
			budgetInfoMap.put("financeSubjParentId", financeSubjParentId);
			budgetInfoMap.put("contractBudget", this.df.format(contractBudget));
			budgetInfoMap.put("loanBudget", this.df.format(loanBudget));
			budgetInfoMap.put("sequence", budgetInfoDto.getSequence());
			
			List<BudgetCurrencyDto> budgetCurrencyList = budgetInfoDto.getBudgetCurrencyList();
			for (BudgetCurrencyDto budgetCurrencyDto : budgetCurrencyList) {
				Double exchangeRate = budgetCurrencyDto.getExchangeRate();
				Double money = budgetCurrencyDto.getMoney();
				
				//原有的本位币换算值和预算比例
				Double preStandardMoney = (Double) budgetInfoMap.get("standardMoney");
				//预算比例
				if (money != null) {
					double standardMoney = BigDecimalUtil.multiply(money, exchangeRate);
					
					if (preStandardMoney != null) {
						//计算总预算
						budgetInfoMap.put("standardMoney", BigDecimalUtil.add(preStandardMoney, standardMoney));
					} else {
						budgetInfoMap.put("standardMoney", standardMoney);
					}
					
				} else if (preStandardMoney == null) {
					budgetInfoMap.put("standardMoney", 0.00);
				}
			}
			
			Double standardMoney = (Double) budgetInfoMap.get("standardMoney");
			Double totalCost = BigDecimalUtil.add(contractBudget, loanBudget);
			Double leftMoney = BigDecimalUtil.subtract(standardMoney, totalCost);
			
			budgetInfoMap.put("standardMoney", this.df.format(standardMoney));
			budgetInfoMap.put("leftMoney", this.df.format(leftMoney));
			if (standardMoney != 0) {
				budgetInfoMap.put("leftRate", BigDecimalUtil.divide(leftMoney, standardMoney) * 100 + "%");
			} else {
				budgetInfoMap.put("leftRate", "0.00%");
			}
			
			
			myBudgetInfoMapList.add(budgetInfoMap);
			
			List<BudgetInfoDto> children = budgetInfoDto.getChildren();
			if (children != null && children.size() > 0) {
				myBudgetInfoMapList = this.genBudgetInfo(myBudgetInfoMapList, children);
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
			for (BudgetInfoDto corgBudgetDto : childBudgetInfoList) {
				
				if (leafBudgetDto.getFinanceSubjId().equals(corgBudgetDto.getFinanceSubjParentId())) {
					contractBudget = BigDecimalUtil.add(contractBudget, corgBudgetDto.getContractBudget());
					loanBudget = BigDecimalUtil.add(loanBudget, corgBudgetDto.getLoanBudget());
					
					children.add(corgBudgetDto);
					
					//把子科目中的每个货币总金额加到父科目中每个货币总金额上
					for (BudgetCurrencyDto fcurrencyDto : fbudgetCurrencyList) {
						for (BudgetCurrencyDto ccurrencyDto : corgBudgetDto.getBudgetCurrencyList()) {
							if (ccurrencyDto.getCurrencyId().equals(fcurrencyDto.getCurrencyId())) {
								fcurrencyDto.setMoney(BigDecimalUtil.add(fcurrencyDto.getMoney(), ccurrencyDto.getMoney()));
								break;
							}
						}
					}
					
				}
			}
			
			leafBudgetDto.setBudgetCurrencyList(fbudgetCurrencyList);
			leafBudgetDto.setChildren(children);
			leafBudgetDto.setContractBudget(contractBudget);
			leafBudgetDto.setLoanBudget(loanBudget);
			
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
	
}
