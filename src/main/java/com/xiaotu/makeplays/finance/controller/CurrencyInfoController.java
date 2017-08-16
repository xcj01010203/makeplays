package com.xiaotu.makeplays.finance.controller;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.xiaotu.makeplays.finance.model.CollectionInfoModel;
import com.xiaotu.makeplays.finance.model.ContractActorModel;
import com.xiaotu.makeplays.finance.model.ContractProduceModel;
import com.xiaotu.makeplays.finance.model.ContractWorkerModel;
import com.xiaotu.makeplays.finance.model.CurrencyInfoModel;
import com.xiaotu.makeplays.finance.model.FinanceSubjectModel;
import com.xiaotu.makeplays.finance.model.LoanInfoModel;
import com.xiaotu.makeplays.finance.model.PaymentInfoModel;
import com.xiaotu.makeplays.finance.service.CollectionInfoService;
import com.xiaotu.makeplays.finance.service.ContractActorService;
import com.xiaotu.makeplays.finance.service.ContractProduceService;
import com.xiaotu.makeplays.finance.service.ContractWorkerService;
import com.xiaotu.makeplays.finance.service.CurrencyInfoService;
import com.xiaotu.makeplays.finance.service.FinanceSubjectService;
import com.xiaotu.makeplays.finance.service.LoanInfoService;
import com.xiaotu.makeplays.finance.service.PaymentInfoService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.StringUtil;

/**
 * 货币信息
 * @author xuchangjian 2016-8-3下午4:50:52
 */
@Controller
@RequestMapping("/currencyManager")
public class CurrencyInfoController extends BaseController {
	
	Logger logger = LoggerFactory.getLogger(CurrencyInfoController.class);
	
	DecimalFormat df = new DecimalFormat("#,##0.00");
	
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private CurrencyInfoService currencyInfoService;
	
	@Autowired
	private ContractActorService contractActorService;
	
	@Autowired
	private ContractWorkerService contractWorkerService;
	
	@Autowired
	private ContractProduceService contractProduceService;
	
	@Autowired
	private PaymentInfoService paymentInfoService;
	
	@Autowired
	private LoanInfoService loanInfoService;
	
	@Autowired
	private CollectionInfoService collectionInfoService;
	
	@Autowired
	private FinanceSubjectService financeSubjectService;
	
	/**
	 * 查询剧组中所有的货币信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryCurrencyList")
	public Map<String, Object> queryCurrencyList(HttpServletRequest request, Boolean ifStandard, Boolean ifEnable) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	
        	Map<String, Object> conditionMap = new HashMap<String, Object>();
        	conditionMap.put("crewId", crewId);
        	if (ifEnable != null) {
        		conditionMap.put("ifEnable", ifEnable);
        	}
        	if (ifStandard != null) {
        		conditionMap.put("ifStandard", ifStandard);
        	}
        	
        	List<CurrencyInfoModel> currencyInfoList = this.currencyInfoService.queryManyByMutiCondition(conditionMap, null);
        	
        	//如果剧组中还没有货币信息，则为剧组初始化一个货币
        	if (currencyInfoList == null || currencyInfoList.size() == 0) {
        		CurrencyInfoModel standardCurrency = this.currencyInfoService.initFirstCurrency(crewId);
        		currencyInfoList = new ArrayList<CurrencyInfoModel>();
        		currencyInfoList.add(standardCurrency);
        	}
        	
        	resultMap.put("currencyInfoList", currencyInfoList);
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
	 * 查询剧组中所有的货币信息
	 * 带有总预算金额信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryCurrencyListWithBudget")
	public Map<String, Object> queryCurrencyListWithBudget(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	
        	List<Map<String, Object>> currencyInfoList = this.currencyInfoService.queryCurrencyListWithBudget(crewId);
        	
        	Double totalMoney = 0.0;
        	for (Map<String, Object> currencyInfo : currencyInfoList) {
        		Double money = (Double) currencyInfo.get("money");
        		Double exchangeRate = (Double) currencyInfo.get("exchangeRate");
        		
        		if (money == null) {
        			currencyInfo.put("money", 0);
        		} else {
        			currencyInfo.put("money", this.df.format(money));
        		}
        		
        		if (money != null) {
        			Double standardMoney = BigDecimalUtil.multiply(money, exchangeRate);
            		totalMoney = BigDecimalUtil.add(totalMoney, standardMoney);
        		}
        	}
        	
        	resultMap.put("totalMoney", this.df.format(totalMoney));
        	resultMap.put("currencyInfoList", currencyInfoList);
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
	 * 保存货币信息
	 * @param request
	 * @param id	ID
	 * @param name	名称
	 * @param code	编码
	 * @param ifStandard	是否本位币
	 * @param ifEnable	是否启用
	 * @param exchangeRate	汇率
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveCurrencyInfo")
	public Map<String, Object> saveCurrencyInfo(HttpServletRequest request, String id, String name, 
			String code, Boolean ifStandard, Boolean ifEnable, Double exchangeRate) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	
        	if (StringUtils.isBlank(name)) {
        		throw new IllegalArgumentException("请填写名称");
        	}
        	
        	if (StringUtils.isBlank(code)) {
        		throw new IllegalArgumentException("请填写编码");
        	}
        	
        	if (ifStandard == null) {
        		throw new IllegalArgumentException("请选择是否本位币");
        	}
        	
        	if (ifEnable == null) {
        		throw new IllegalArgumentException("请选择是否启用");
        	}
        	
        	//校验名称是否重复
        	List<CurrencyInfoModel> nameCurrencyList = this.currencyInfoService.queryByNameExcepOwn(crewId, name, id);
        	if (nameCurrencyList != null && nameCurrencyList.size() > 0) {
        		throw new IllegalArgumentException("名称和其他货币重复");
        	}
        	//校验编码是否重复
        	List<Map<String, Object>> codeCurrencyList = this.currencyInfoService.queryByCodeExcepOwn(crewId, code, id);
        	if (codeCurrencyList != null && codeCurrencyList.size() > 0) {
        		throw new IllegalArgumentException("编码和其他货币重复");
        	}
        	//校验币种是否关联单据、合同、财务预算
			if (!StringUtils.isBlank(id) && !ifEnable) {
				Map<String, Object> conditionMap = new HashMap<String, Object>();
				conditionMap.put("currencyId", id);
				//关联的合同（演员、职员、制作）
				List<ContractActorModel> contractActorList = this.contractActorService.queryManyByMutiCondition(conditionMap, null);
				List<ContractWorkerModel> contractWorkerList = this.contractWorkerService.queryManyByMutiCondition(conditionMap, null);
				List<ContractProduceModel> contractProduceList = this.contractProduceService.queryManyByMutiCondition(conditionMap, null);
				if (contractActorList != null && contractActorList.size() > 0) {
					throw new IllegalArgumentException("该币种已关联" + contractActorList.get(0).getActorName() + "合同，不可禁用");
				}
				if (contractWorkerList != null && contractWorkerList.size() > 0) {
					throw new IllegalArgumentException("该币种已关联" + contractWorkerList.get(0).getWorkerName() + "合同，不可禁用");
				}
				if (contractProduceList != null && contractProduceList.size() > 0) {
					throw new IllegalArgumentException("该币种已关联" + contractProduceList.get(0).getCompany() + "合同，不可禁用");
				}
				
				//关联的单据（付款单、收款单、借款单）
				List<PaymentInfoModel> paymentList = this.paymentInfoService.queryManyByMutiCondition(conditionMap, null);
				List<CollectionInfoModel> collectionList = this.collectionInfoService.queryManyByMutiCondition(conditionMap, null);
				List<LoanInfoModel> loanInfoList = this.loanInfoService.queryManyByMutiCondition(conditionMap, null);
				if (paymentList != null && paymentList.size() > 0) {
					throw new IllegalArgumentException("该币种已关联"+ this.sdf1.format(paymentList.get(0).getPaymentDate()) +"日付款单，不可禁用");
				}
				if (collectionList != null && collectionList.size() > 0) {
					throw new IllegalArgumentException("该币种已关联"+ this.sdf1.format(collectionList.get(0).getCollectionDate()) +"日收款单，不可禁用");
				}
				if (loanInfoList != null && loanInfoList.size() > 0) {
					throw new IllegalArgumentException("该币种已关联"+ this.sdf1.format(loanInfoList.get(0).getLoanDate()) +"日借款单，不可禁用");
				}
				
				//关联的财务科目预算
				List<FinanceSubjectModel> subjectList = this.financeSubjectService.queryByCurrencyId(crewId, id);
				if (subjectList != null && subjectList.size() > 0) {
					throw new IllegalArgumentException("该币种已关联"+ subjectList.get(0).getName() +"财务科目，不可禁用");
				}
			}
        	
        	
        	this.currencyInfoService.saveCurrencyInfo(crewId, id, name, code, ifStandard, ifEnable, exchangeRate);
        	
        	String logDesc = "";
			Integer operType = null;
			if(StringUtil.isBlank(id)) {
				logDesc = "添加币种";
				operType = 1;
			} else {
				logDesc = "修改币种";
				operType = 2;
			}
        	this.sysLogService.saveSysLog(request, logDesc, Constants.TERMINAL_PC, CurrencyInfoModel.TABLE_NAME, id, operType);
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
        } catch(Exception e) {
            success = false;
            message = "未知异常";

            logger.error("未知异常", e);
        	this.sysLogService.saveSysLog(request, "保存币种失败：" + e.getMessage(), Constants.TERMINAL_PC, CurrencyInfoModel.TABLE_NAME, id, SysLogOperType.ERROR.getValue());
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
	
	/**
	 * 设置币种的为本位币
	 * @param id	币种ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/makeCurrencyStandard")
	public Map<String, Object> makeCurrencyStandard(HttpServletRequest request, String id) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(id)) {
				throw new IllegalArgumentException("请提供币种ID");
			}
			String crewId = this.getCrewId(request);
			this.currencyInfoService.makeCurrencyStandard(crewId, id);
		} catch(IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(ie.getMessage(), ie);
		} catch(Exception e) {
			success = false;
			message = "未知异常，设置失败";
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 设置币种是否启用
	 * @param request
	 * @param id	币种ID
	 * @param ifEnable	是否启用
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveCurrencyEnableStatus")
	public Map<String, Object> saveCurrencyEnableStatus(HttpServletRequest request, String id, boolean ifEnable) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(id)) {
				throw new IllegalArgumentException("请提供币种ID");
			}
			String crewId = this.getCrewId(request);
			CurrencyInfoModel myCurrency = this.currencyInfoService.queryById(id);
			
			if (!ifEnable) {
				Map<String, Object> conditionMap = new HashMap<String, Object>();
				conditionMap.put("currencyId", id);
				//关联的合同（演员、职员、制作）
				List<ContractActorModel> contractActorList = this.contractActorService.queryManyByMutiCondition(conditionMap, null);
				List<ContractWorkerModel> contractWorkerList = this.contractWorkerService.queryManyByMutiCondition(conditionMap, null);
				List<ContractProduceModel> contractProduceList = this.contractProduceService.queryManyByMutiCondition(conditionMap, null);
				if (contractActorList != null && contractActorList.size() > 0) {
					throw new IllegalArgumentException("该币种已关联" + contractActorList.get(0).getActorName() + "合同，不可禁用");
				}
				if (contractWorkerList != null && contractWorkerList.size() > 0) {
					throw new IllegalArgumentException("该币种已关联" + contractWorkerList.get(0).getWorkerName() + "合同，不可禁用");
				}
				if (contractProduceList != null && contractProduceList.size() > 0) {
					throw new IllegalArgumentException("该币种已关联" + contractProduceList.get(0).getCompany() + "合同，不可禁用");
				}
				
				//关联的单据（付款单、收款单、借款单）
				List<PaymentInfoModel> paymentList = this.paymentInfoService.queryManyByMutiCondition(conditionMap, null);
				List<CollectionInfoModel> collectionList = this.collectionInfoService.queryManyByMutiCondition(conditionMap, null);
				List<LoanInfoModel> loanInfoList = this.loanInfoService.queryManyByMutiCondition(conditionMap, null);
				if (paymentList != null && paymentList.size() > 0) {
					throw new IllegalArgumentException("该币种已关联"+ this.sdf1.format(paymentList.get(0).getPaymentDate()) +"日付款单，不可禁用");
				}
				if (collectionList != null && collectionList.size() > 0) {
					throw new IllegalArgumentException("该币种已关联"+ this.sdf1.format(collectionList.get(0).getCollectionDate()) +"日收款单，不可禁用");
				}
				if (loanInfoList != null && loanInfoList.size() > 0) {
					throw new IllegalArgumentException("该币种已关联"+ this.sdf1.format(loanInfoList.get(0).getLoanDate()) +"日借款单，不可禁用");
				}
				
				//关联的财务科目预算
				List<FinanceSubjectModel> subjectList = this.financeSubjectService.queryByCurrencyId(crewId, id);
				if (subjectList != null && subjectList.size() > 0) {
					throw new IllegalArgumentException("该币种已关联"+ subjectList.get(0).getName() +"财务科目，不可禁用");
				}
				
				if (myCurrency.getIfStandard()) {
					throw new IllegalArgumentException("该币种为本位币，不可禁用");
				}
			}
			myCurrency.setIfEnable(ifEnable);
			this.currencyInfoService.updateOne(myCurrency);			
			
		} catch(IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(ie.getMessage(), ie);
		} catch(Exception e) {
			success = false;
			message = "未知异常，设置失败";
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
}
