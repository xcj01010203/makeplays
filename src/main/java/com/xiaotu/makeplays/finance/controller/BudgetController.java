package com.xiaotu.makeplays.finance.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

import com.xiaotu.makeplays.finance.controller.dto.BudgetCurrencyDto;
import com.xiaotu.makeplays.finance.controller.dto.BudgetInfoDto;
import com.xiaotu.makeplays.finance.model.ContractActorModel;
import com.xiaotu.makeplays.finance.model.ContractProduceModel;
import com.xiaotu.makeplays.finance.model.ContractWorkerModel;
import com.xiaotu.makeplays.finance.model.CurrencyInfoModel;
import com.xiaotu.makeplays.finance.model.FinanceSubjectModel;
import com.xiaotu.makeplays.finance.model.LoanInfoModel;
import com.xiaotu.makeplays.finance.model.PaymentFinanSubjMapModel;
import com.xiaotu.makeplays.finance.service.ContractActorService;
import com.xiaotu.makeplays.finance.service.ContractProduceService;
import com.xiaotu.makeplays.finance.service.ContractWorkerService;
import com.xiaotu.makeplays.finance.service.CurrencyInfoService;
import com.xiaotu.makeplays.finance.service.FinanSubjCurrencyMapService;
import com.xiaotu.makeplays.finance.service.FinanceSubjectService;
import com.xiaotu.makeplays.finance.service.LoanInfoService;
import com.xiaotu.makeplays.finance.service.PaymentFinanSubjMapService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.StringUtil;

/**
 * 财务预算
 * 预算不是一个完全独立的概念，预算是结合财务科目和货币信息组合而成
 * @author xuchangjian 2016-8-4上午9:41:46
 */
@Controller
@RequestMapping("/budgetManager")
public class BudgetController extends BaseController {
	
	Logger logger = LoggerFactory.getLogger(BudgetController.class);
	
	DecimalFormat df = new DecimalFormat("#,##0.00");
	
	DecimalFormat df2 = new DecimalFormat("#,##0.00%");
	
	@Autowired
	private FinanceSubjectService financeSubjectService;
	
	@Autowired
	private CurrencyInfoService currencyInfoService;
	
	@Autowired
	private FinanSubjCurrencyMapService finanSubjCurrencyMapService;
	
	@Autowired
	private PaymentFinanSubjMapService paymentFinanSubjMapService;
	
	@Autowired
	private LoanInfoService loanInfoService;
	
	@Autowired
	private ContractWorkerService contractWorkerService;
	
	@Autowired
	private ContractProduceService contractProduceService;
	
	@Autowired
	private ContractActorService contractActorService;
	
	/**
	 * 查询预算列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryBudgetList")
	public Map<String, Object> queryBudgetList(HttpServletRequest request) {
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
        	
        	//获取财务科目列表
        	List<Map<String, Object>> finanSubjWithBudgetList = this.financeSubjectService.queryWithBudgetInfo(crewId);
        	
        	//计算总预算（用于下面计算预算比例）
        	double totalMoney = 0;
        	for (Map<String, Object> finanSubj : finanSubjWithBudgetList) {
        		Double exchangeRate = (Double) finanSubj.get("exchangeRate");	//汇率
        		Double money = (Double) finanSubj.get("money");	//总金额
        		
        		if (exchangeRate != null && money != null) {
        			double standardMoney = BigDecimalUtil.multiply(exchangeRate, money);
        			totalMoney = BigDecimalUtil.add(totalMoney, standardMoney);
        		}
        	}
        	
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
        		
        		budgetInfoList.add(budgetInfoDto);
        	}
        	
        	
        	
        	//把子节点的预算值向父节点上合并，并把财务科目按照父子关系做成嵌套的形式
        	List<BudgetInfoDto> budgetDtoList = this.loopBudgetInfoDto(new ArrayList<BudgetInfoDto>(), budgetInfoList);
        	
        	//把嵌套形式的财务科目平铺开来，并且把其中的货币信息做成以货币ID为键，总金额为值的形式
        	List<Map<String, Object>> budgetInfoMapList = this.genBudgetInfo(new ArrayList<Map<String, Object>>(), budgetDtoList, totalMoney);
        	Collections.sort(budgetInfoMapList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					int o1sequence = (Integer) o1.get("sequence");
					int o2sequence = (Integer) o2.get("sequence");
	        		return o1sequence - o2sequence;
				}
			});
        	for(Map<String, Object> map : budgetInfoMapList) {
        		String financeSubjParentId = map.get("financeSubjParentId") + "";
        		if(!financeSubjParentId.equals("0")) {
	        		map.put("_parentId", map.get("financeSubjParentId"));
        		}
        	}
            resultMap.put("total", budgetInfoMapList.size());
            resultMap.put("rows", budgetInfoMapList);
        	resultMap.put("budgetInfoMapList", budgetInfoMapList);
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
	 * 把按照父子结构封装起来的财务科目展开
	 * @param budgetDtoList
	 * @return
	 */
	private List<Map<String, Object>> genBudgetInfo(List<Map<String, Object>> budgetInfoMapList, List<BudgetInfoDto> budgetDtoList, double totalMoney) {
		List<Map<String, Object>> myBudgetInfoMapList = budgetInfoMapList;
		
		for (BudgetInfoDto budgetInfoDto : budgetDtoList) {
			String financeSubjId = budgetInfoDto.getFinanceSubjId();
			String financeSubjName = budgetInfoDto.getFinanceSubjName();
			String financeSubjParentId = budgetInfoDto.getFinanceSubjParentId();
			String remark = budgetInfoDto.getRemark();
			Integer level = budgetInfoDto.getLevel();
			boolean hasChildren = budgetInfoDto.isHasChildren();
			
			Map<String, Object> budgetInfoMap = new HashMap<String, Object>();
			budgetInfoMap.put("financeSubjId", financeSubjId);
			budgetInfoMap.put("financeSubjName", financeSubjName);
			budgetInfoMap.put("financeSubjParentId", financeSubjParentId);
			budgetInfoMap.put("remark", remark);
			budgetInfoMap.put("hasChildren", hasChildren);
			budgetInfoMap.put("myLevel", level);
			budgetInfoMap.put("sequence", budgetInfoDto.getSequence());
			
			List<BudgetCurrencyDto> budgetCurrencyList = budgetInfoDto.getBudgetCurrencyList();
			for (BudgetCurrencyDto budgetCurrencyDto : budgetCurrencyList) {
				String currencyId = budgetCurrencyDto.getCurrencyId();
				Double exchangeRate = budgetCurrencyDto.getExchangeRate();
				String mapId = budgetCurrencyDto.getMapId();
				Double amount = budgetCurrencyDto.getAmount();
				Double money = budgetCurrencyDto.getMoney();
				Double perPrice = budgetCurrencyDto.getPerPrice();
				String unitType = budgetCurrencyDto.getUnitType();
				
				budgetInfoMap.put(currencyId, this.df.format(money));
				
				if (amount != null) {
					budgetInfoMap.put("amount", amount);
				}
				if (unitType != null) {
					budgetInfoMap.put("unitType", unitType);
					budgetInfoMap.put("unitTypeStr", unitType);
				}
				if (perPrice != null) {
					budgetInfoMap.put("perPrice", this.df.format(perPrice));
				}
				
				if (!StringUtils.isBlank(mapId)) {
					budgetInfoMap.put("currencyId", currencyId);
					budgetInfoMap.put("money", money);
				}
				
				//原有的本位币换算值和预算比例
				Double preStandardMoney = (Double) budgetInfoMap.get("standardMoney");
				Double preBudgetRate = (Double) budgetInfoMap.get("budgetRate");
				
				//预算比例
				if (totalMoney != 0 && money != null) {
					double standardMoney = BigDecimalUtil.multiply(money, exchangeRate);
					double budgetRate = BigDecimalUtil.divide(standardMoney, totalMoney);
					
					if (preStandardMoney != null) {
						//计算总预算
						budgetInfoMap.put("standardMoney", BigDecimalUtil.add(preStandardMoney, standardMoney));
						budgetInfoMap.put("budgetRate", BigDecimalUtil.add(preBudgetRate, budgetRate));
					} else {
						budgetInfoMap.put("standardMoney", standardMoney);
						budgetInfoMap.put("budgetRate", budgetRate);
					}
					
				} else if (preStandardMoney == null) {
					budgetInfoMap.put("standardMoney", 0.00);
					budgetInfoMap.put("budgetRate", 0.00);
				}
			}
			
			//预算比例格式化
			if (budgetInfoMap.get("budgetRate") != null) {
				budgetInfoMap.put("budgetRate", this.df2.format((Double)budgetInfoMap.get("budgetRate")));
			} else {
				budgetInfoMap.put("budgetRate", "0.00%");
			}
			//总预算金额格式化
			if (budgetInfoMap.get("standardMoney") != null) {
				budgetInfoMap.put("standardMoney", this.df.format((Double)budgetInfoMap.get("standardMoney")));
			} else {
				budgetInfoMap.put("standardMoney", "0.00");
			}
			
			
			myBudgetInfoMapList.add(budgetInfoMap);
			
			List<BudgetInfoDto> children = budgetInfoDto.getChildren();
			if (children != null && children.size() > 0) {
				myBudgetInfoMapList = this.genBudgetInfo(myBudgetInfoMapList, children, totalMoney);
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
			
			for (BudgetInfoDto corgBudgetDto : childBudgetInfoList) {
				
				if (leafBudgetDto.getFinanceSubjId().equals(corgBudgetDto.getFinanceSubjParentId())) {
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
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveBudgetInfo")
	public Map<String, Object> saveBudgetInfo (HttpServletRequest request, String financeSubjId, String financeSubjParentId, String financeSubjName, 
			Integer level, String remark, Double amount, String unitType, 
			String currencyId, Double perPrice, Double money) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			//从sessoin取当前登录用户
			String crewId = this.getCrewId(request);
			
			if (StringUtils.isBlank(financeSubjName)) {
				throw new IllegalArgumentException("请填写财务科目");
			}
			//校验同一个父科目下，是否存在相同名称的财务科目
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("crewId", crewId);
			conditionMap.put("parentId", financeSubjParentId);
			conditionMap.put("name", financeSubjName);
			List<FinanceSubjectModel> subjectList = this.financeSubjectService.queryManyByMutiCondition(conditionMap, null);
			if (subjectList != null && subjectList.size() > 0) {
				if (!subjectList.get(0).getId().equals(financeSubjId) ) {
					throw new IllegalArgumentException("已存在相同名称的财务科目");
				}
			}
			
			
			this.financeSubjectService.saveBudgetInfo(crewId, financeSubjParentId, financeSubjId, financeSubjName, 
					level, remark, amount, unitType, currencyId, perPrice, money);
			
			String logDesc = "";
			Integer operType = null;
			String id = crewId;
			if(StringUtil.isBlank(financeSubjId)) {
				logDesc = "添加财务科目";
				operType = 1;
			} else {
				logDesc = "修改财务科目";
				operType = 2;
				id = financeSubjId;
			}
			this.sysLogService.saveSysLog(request, logDesc, Constants.TERMINAL_PC, FinanceSubjectModel.TABLE_NAME, id, operType);
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";

			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "保存财务科目信息失败：" + e.getMessage(), Constants.TERMINAL_PC, FinanceSubjectModel.TABLE_NAME, financeSubjId, 6);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 删除一条预算信息
	 * @param request
	 * @param financeSubjId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteOneSubject")
	public Map<String, Object> deleteOneSubject(HttpServletRequest request, String financeSubjId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(financeSubjId)) {
				throw new IllegalArgumentException("请选择一条财务科目");
			}
			
			String crewId = this.getCrewId(request);
			
			//校验是否有子科目
			List<FinanceSubjectModel> financeSubjectList = this.financeSubjectService.queryByParentId(crewId, financeSubjId);
			if (financeSubjectList != null && financeSubjectList.size() > 0) {
				throw new IllegalArgumentException("该科目下有子科目，请先删除子科目");
			}
			
			//校验是否已关联付款单、借款单、合同
			//付款单
			List<PaymentFinanSubjMapModel> payFinanSubjMapList = this.paymentFinanSubjMapService.queryByFinanceSubjId(crewId, financeSubjId);
			if (payFinanSubjMapList != null && payFinanSubjMapList.size() > 0) {
				throw new IllegalArgumentException("该科目已关联付款单，不可删除");
			}
			//借款单
			List<LoanInfoModel> loanInfoList = this.loanInfoService.queryByFinanceSubjId(crewId, financeSubjId);
			if (loanInfoList != null && loanInfoList.size() > 0) {
				throw new IllegalArgumentException("该科目已关联借款单，不可删除");
			}
			//演员合同
			List<ContractActorModel> contractActorList = this.contractActorService.queryByFinanceSubjId(crewId, financeSubjId);
			if (contractActorList != null && contractActorList.size() >  0) {
				throw new IllegalArgumentException("该科目已关联《" + contractActorList.get(0).getActorName() + "》合同，不可删除");
			}
			
			//职员合同
			List<ContractWorkerModel> contractWorkerList = this.contractWorkerService.queryByFinanceSubjId(crewId, financeSubjId);
			if (contractWorkerList != null && contractWorkerList.size() > 0) {
				throw new IllegalArgumentException("该科目已关联《" + contractWorkerList.get(0).getWorkerName() + "》合同，不可删除");
			}
			
			//制作合同
			List<ContractProduceModel> contractProduceList = this.contractProduceService.queryByFinanceSubjId(crewId, financeSubjId);
			if (contractProduceList != null && contractProduceList.size() > 0) {
				throw new IllegalArgumentException("该科目已关联《" + contractProduceList.get(0).getCompany() + "》合同，不可删除");
			}
			
			//删除财务科目
			this.financeSubjectService.deleteOneFinanceSubj(crewId, financeSubjId);
			
			this.sysLogService.saveSysLog(request, "删除财务科目", Constants.TERMINAL_PC, FinanceSubjectModel.TABLE_NAME, financeSubjId, 3);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";

			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "删除财务科目失败：" + e.getMessage(), Constants.TERMINAL_PC, FinanceSubjectModel.TABLE_NAME, financeSubjId, 6);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 校验剧组下的财务科目是否全部都没有关联到其他项目上
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/checkIsClean")
	public Map<String, Object> checkIsClean(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			
			boolean isClean = true;
			
			//校验是否已关联付款单、借款单、合同
			//付款单
			List<PaymentFinanSubjMapModel> payFinanSubjMapList = this.paymentFinanSubjMapService.queryByCrewId(crewId);
			if (isClean && payFinanSubjMapList != null && payFinanSubjMapList.size() > 0) {
				message = "存在已关联付款单，不可重新设置";
				isClean = false;
			}
			//借款单
			List<LoanInfoModel> loanInfoList = this.loanInfoService.queryFinanceLoanList(crewId);
			if (isClean && loanInfoList != null && loanInfoList.size() > 0) {
				message = "存在已关联借款单，不可重新设置";
				isClean = false;
			}
			//演员合同
			List<ContractActorModel> contractActorList = this.contractActorService.queryFinanContract(crewId);
			if (isClean && contractActorList != null && contractActorList.size() >  0) {
				message = "存在已关联合同，不可重新设置";
				isClean = false;
			}
			
			//职员合同
			List<ContractWorkerModel> contractWorkerList = this.contractWorkerService.queryFinanContract(crewId);
			if (isClean && contractWorkerList != null && contractWorkerList.size() > 0) {
				message = "存在已关联合同，不可重新设置";
				isClean = false;
			}
			
			//制作合同
			List<ContractProduceModel> contractProduceList = this.contractProduceService.queryFinanContract(crewId);
			if (isClean && contractProduceList != null && contractProduceList.size() > 0) {
				message = "存在已关联合同，不可重新设置";
				isClean = false;
			}
			
			resultMap.put("isClean", isClean);
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
	 * 删除所有预算信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteAllBudget")
	public Map<String, Object> deleteAllBudget(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			
			//校验是否已关联付款单、借款单、合同
			//付款单
			List<PaymentFinanSubjMapModel> payFinanSubjMapList = this.paymentFinanSubjMapService.queryByCrewId(crewId);
			if (payFinanSubjMapList != null && payFinanSubjMapList.size() > 0) {
				throw new IllegalArgumentException("存在已关联付款单的财务科目，不可删除");
			}
			//借款单
			List<LoanInfoModel> loanInfoList = this.loanInfoService.queryFinanceLoanList(crewId);
			if (loanInfoList != null && loanInfoList.size() > 0) {
				throw new IllegalArgumentException("存在已关联借款单的财务科目，不可删除");
			}
			//演员合同
			List<ContractActorModel> contractActorList = this.contractActorService.queryFinanContract(crewId);
			if (contractActorList != null && contractActorList.size() >  0) {
				throw new IllegalArgumentException("存在已关联《" + contractActorList.get(0).getActorName() + "》合同的财务科目，不可删除");
			}
			//职员合同
			List<ContractWorkerModel> contractWorkerList = this.contractWorkerService.queryFinanContract(crewId);
			if (contractWorkerList != null && contractWorkerList.size() > 0) {
				throw new IllegalArgumentException("存在已关联《" + contractWorkerList.get(0).getWorkerName() + "》合同的财务科目，不可删除");
			}
			//制作合同
			List<ContractProduceModel> contractProduceList = this.contractProduceService.queryFinanContract(crewId);
			if (contractProduceList != null && contractProduceList.size() > 0) {
				throw new IllegalArgumentException("存在已关联《" + contractProduceList.get(0).getCompany() + "》合同的财务科目，不可删除");
			}
			
			
			this.financeSubjectService.deleteAllFinanceSubj(crewId);

			this.sysLogService.saveSysLog(request, "删除所有预算信息", Constants.TERMINAL_PC, FinanceSubjectModel.TABLE_NAME, null, 3);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";

			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "删除所有预算信息失败：" + e.getMessage(), Constants.TERMINAL_PC, FinanceSubjectModel.TABLE_NAME, null, 6);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询财务科目中的计算单位类型列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryUnitTypeList")
	public Map<String, Object> queryUnitTypeList(HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
 			List<Map<String,Object>> list = this.finanSubjCurrencyMapService.queryUnitTypeList(crewId);
			
			resultMap.put("unitTypeList", list);
			message = "查询成功";
		} catch (Exception e) {
			message = "未知异常，查询失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
}
