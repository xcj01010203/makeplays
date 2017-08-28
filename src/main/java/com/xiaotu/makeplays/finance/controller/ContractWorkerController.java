package com.xiaotu.makeplays.finance.controller;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
import com.xiaotu.makeplays.attachment.service.AttachmentService;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.finance.controller.dto.FinanceSubjectDto;
import com.xiaotu.makeplays.finance.model.ContractMonthPayDetailModel;
import com.xiaotu.makeplays.finance.model.ContractMonthPaywayModel;
import com.xiaotu.makeplays.finance.model.ContractStagePayWayModel;
import com.xiaotu.makeplays.finance.model.ContractWorkerModel;
import com.xiaotu.makeplays.finance.model.CurrencyInfoModel;
import com.xiaotu.makeplays.finance.service.ContractPayWayService;
import com.xiaotu.makeplays.finance.service.ContractToPaidService;
import com.xiaotu.makeplays.finance.service.ContractWorkerService;
import com.xiaotu.makeplays.finance.service.CurrencyInfoService;
import com.xiaotu.makeplays.finance.service.FinanceSubjectService;
import com.xiaotu.makeplays.finance.service.PaymentInfoService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.model.constants.IdentityCardType;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.ExcelUtils;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.view.service.ViewInfoService;

/**
 * 职员合同
 * @author xuchangjian 2016-8-12下午3:33:49
 */
@Controller
@RequestMapping("/contractWorker")
public class ContractWorkerController extends BaseController {
	
	Logger logger = LoggerFactory.getLogger(ContractWorkerController.class);
	
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM");

	SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/MM/dd");

	SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy/MM");

	@Autowired
	private ContractWorkerService contractWorkerService;
	
	@Autowired
	private ContractToPaidService contractToPaidService;
	@Autowired
	private PaymentInfoService paymentInfoService;
	
	@Autowired
	private ContractPayWayService contractPayWayService;
	
	@Autowired
	private AttachmentService attachmentService;
	
	@Autowired
	private CurrencyInfoService currencyInfoService;
	
	@Autowired
	private FinanceSubjectService financeSubjectService;
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	/**
	 * 跳转到职员合同详细信息页面
	 * @return
	 */
	@RequestMapping("/toContractWorkerDetailPage")
	public ModelAndView toContractWorkerDetailPage(String contractId, Boolean readonly) {
		ModelAndView mv = new ModelAndView("/finance/contract/contractWorkerDetail");
		if (!StringUtils.isBlank(contractId)) {
			mv.addObject("contractId", contractId);
		}
		if (readonly != null) {
			mv.addObject("readonly", readonly);
		}
		return mv;
	}
	
	/**
	 * 查询职员合同列表
	 * 带高级查询条件
	 * @param request
	 * @param workerNames	职员姓名，多个以逗号隔开
	 * @param department	部门职务
	 * @param paymentTerm	支付条件
	 * @param remark	备注
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryContractList")
	public Map<String, Object> queryContractList(HttpServletRequest request, String workerNames, String department, String financeSubjIds, Integer payWay, String paymentTerm, String remark) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	
        	List<Map<String, Object>> contractWorkerList = this.contractWorkerService.queryByAnvanceCondition(crewId, workerNames, department, financeSubjIds, payWay, paymentTerm, remark, null, null);
        	
        	resultMap.put("contractWorkerList", contractWorkerList);
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
	 * 查询剧组中职员合同金额的总的统计信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryContractMoneyStatistics")
	public Map<String, Object> queryContractMoneyStatistics(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	
        	List<Map<String, Object>> contractWorkerList = this.contractWorkerService.queryByAnvanceCondition(crewId, null, null, null, null, null, null, null, null);
        	CurrencyInfoModel standardCurrency = this.currencyInfoService.queryStandardCurrency(crewId);	//本位币
        	
        	Double sumStandardTotalMoney = 0.0;	//所有薪酬（本位币）
        	Double sumStandardPayedMoney = 0.0;	//所有已付薪酬（本位币）
        	Double sumStandardLeftMoney = 0.0;	//所有未付薪酬（本位币）
        	
        	List<Map<String, Object>> currencySumMoneyList = new ArrayList<Map<String, Object>>();
        	
        	//遍历合同列表，把合同中相同币种的金额进行合并，不同的币种数据放到currencySumMoneyList中
        	for (Map<String, Object> contractWorker : contractWorkerList) {
        		Double totalMoney = (Double) contractWorker.get("totalMoney");
        		Double payedMoney = (Double) contractWorker.get("payedMoney");
        		Double leftMoney = (Double) contractWorker.get("leftMoney");
        		
        		String currencyId = (String) contractWorker.get("currencyId");
        		String currencyCode = (String) contractWorker.get("currencyCode");
        		Double exchangeRate = (Double) contractWorker.get("exchangeRate");
        		
        		
        		boolean exists = false;
        		for (Map<String, Object> currencySumMoneyMap : currencySumMoneyList) {
        			String myCurrencyId = (String) currencySumMoneyMap.get("currencyId");
            		Double myTotalMoney = (Double) currencySumMoneyMap.get("totalMoney");
            		Double myPayedMoney = (Double) currencySumMoneyMap.get("payedMoney");
            		Double myLeftMoney = (Double) currencySumMoneyMap.get("leftMoney");
            		
            		if (currencyId.equals(myCurrencyId)) {
            			currencySumMoneyMap.put("totalMoney", BigDecimalUtil.add(totalMoney, myTotalMoney));
            			currencySumMoneyMap.put("payedMoney", BigDecimalUtil.add(payedMoney, myPayedMoney));
            			currencySumMoneyMap.put("leftMoney", BigDecimalUtil.add(leftMoney, myLeftMoney));
            			
            			exists = true;
            			break;
            		}
        		}
        		
        		if (!exists) {
        			Map<String, Object> currencySumMoneyMap = new HashMap<String, Object>();
            		currencySumMoneyMap.put("currencyId", currencyId);
            		currencySumMoneyMap.put("currencyCode", currencyCode);
            		currencySumMoneyMap.put("totalMoney", totalMoney);
            		currencySumMoneyMap.put("payedMoney", payedMoney);
            		currencySumMoneyMap.put("leftMoney", leftMoney);
            		currencySumMoneyList.add(currencySumMoneyMap);
        		}
        		
        		//折合成本位币的金额
        		sumStandardTotalMoney = BigDecimalUtil.add(sumStandardTotalMoney, BigDecimalUtil.multiply(totalMoney, exchangeRate));
        		sumStandardPayedMoney = BigDecimalUtil.add(sumStandardPayedMoney, BigDecimalUtil.multiply(payedMoney, exchangeRate));
        		sumStandardLeftMoney = BigDecimalUtil.add(sumStandardLeftMoney, BigDecimalUtil.multiply(leftMoney, exchangeRate));
        	}
        	
        	for (Map<String, Object> currencySumMoneyMap : currencySumMoneyList) {
        		Double payedMoney = (Double) currencySumMoneyMap.get("payedMoney");
        		Double totalMoney = (Double) currencySumMoneyMap.get("totalMoney");
        		Double payedRate = BigDecimalUtil.divide(payedMoney, totalMoney);
        		currencySumMoneyMap.put("payedRate", payedRate * 100);
        	}
        	
        	
        	//本位币总金额统计信息
        	Double payedRate = 0.00;	//已付比例
        	if (sumStandardTotalMoney != 0) {
        		payedRate = BigDecimalUtil.divide(sumStandardPayedMoney, sumStandardTotalMoney);
        	}
        	Map<String, Object> standardSumMoneyMap = new HashMap<String, Object>();
        	standardSumMoneyMap.put("totalMoney", sumStandardTotalMoney);
        	standardSumMoneyMap.put("payedMoney", sumStandardPayedMoney);
        	standardSumMoneyMap.put("leftMoney", sumStandardLeftMoney);
        	standardSumMoneyMap.put("payedRate", payedRate * 100);
        	standardSumMoneyMap.put("currencyCode", standardCurrency.getCode());
        	
        	resultMap.put("standardSumMoneyMap", standardSumMoneyMap);
        	resultMap.put("currencySumMoneyList", currencySumMoneyList);
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
	 * 导出职员合同列表；支持高级查询
	 * @param request
	 * @param workerNames
	 * @param department
	 * @param financeSubjIds
	 * @param payWay
	 * @param paymentTerm
	 * @param remark
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exportContractWorkerList")
	public Map<String, Object> exportContractWorkerList(HttpServletRequest request, String workerNames,
			String department, String financeSubjIds, Integer payWay, String paymentTerm, String remark){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			boolean  singleCurrencyFlag= false;	//标识剧组中是否只有一个有效币种
			Map<String, Object> conditionMap = new HashMap<String, Object>();
        	conditionMap.put("crewId", crewInfo.getCrewId());
        	conditionMap.put("ifEnable", true);
        	List<CurrencyInfoModel> currencyInfoList = this.currencyInfoService.queryManyByMutiCondition(conditionMap, null);
			if (currencyInfoList.size() == 1) {
				singleCurrencyFlag = true;
			}
			
			//获取数据
			DecimalFormat df = new DecimalFormat("#,##0.00");
			List<Map<String, Object>> contractWorkerList = this.contractWorkerService.queryByAnvanceCondition(crewInfo.getCrewId(), workerNames, department, financeSubjIds, payWay, paymentTerm, remark, null, null);
			for (Map<String, Object> map : contractWorkerList) {
				Double totalMoneyDou = (Double) map.get("totalMoney");
				Double payedMoneyDou = (Double) map.get("payedMoney");
				Double leftMoneyDou = (Double) map.get("leftMoney");
				String totalMoney = df.format(totalMoneyDou);
				String payedMoney = df.format(payedMoneyDou);
				String leftMoney = df.format(leftMoneyDou);
				String currencyCode = (String) map.get("currencyCode");
				
				if (!singleCurrencyFlag) {
					totalMoney += "(" + currencyCode + ")";
					payedMoney += "(" + currencyCode + ")";
					leftMoney += "(" + currencyCode + ")";
				}
				
				map.put("totalMoney", totalMoney);
				map.put("payedMoney", payedMoney);
				map.put("leftMoney", leftMoney);
			}
			data.put("contractWorkerList", contractWorkerList);
			
			//获取统计信息
			List<Map<String, Object>> contractSummerWorkerList = this.contractWorkerService.queryByAnvanceCondition(crewInfo.getCrewId(), null, null, null, null, null, null, null, null);
        	CurrencyInfoModel standardCurrency = this.currencyInfoService.queryStandardCurrency(crewInfo.getCrewId());	//本位币
        	
        	Double sumStandardTotalMoney = 0.0;	//所有薪酬（本位币）
        	Double sumStandardPayedMoney = 0.0;	//所有已付薪酬（本位币）
        	Double sumStandardLeftMoney = 0.0;	//所有未付薪酬（本位币）
        	
        	List<Map<String, Object>> currencySumMoneyList = new ArrayList<Map<String, Object>>();
        	
        	//遍历合同列表，把合同中相同币种的金额进行合并，不同的币种数据放到currencySumMoneyList中
        	for (Map<String, Object> contractWorker : contractSummerWorkerList) {
        		Double totalMoney = (Double) contractWorker.get("totalMoney");
        		Double payedMoney = (Double) contractWorker.get("payedMoney");
        		Double leftMoney = (Double) contractWorker.get("leftMoney");
        		
        		String currencyId = (String) contractWorker.get("currencyId");
        		String currencyCode = (String) contractWorker.get("currencyCode");
        		Double exchangeRate = (Double) contractWorker.get("exchangeRate");
        		
        		
        		boolean exists = false;
        		for (Map<String, Object> currencySumMoneyMap : currencySumMoneyList) {
        			String myCurrencyId = (String) currencySumMoneyMap.get("currencyId");
            		Double myTotalMoney = (Double) currencySumMoneyMap.get("totalMoney");
            		Double myPayedMoney = (Double) currencySumMoneyMap.get("payedMoney");
            		Double myLeftMoney = (Double) currencySumMoneyMap.get("leftMoney");
            		
            		if (currencyId.equals(myCurrencyId)) {
            			currencySumMoneyMap.put("totalMoney", BigDecimalUtil.add(totalMoney, myTotalMoney));
            			currencySumMoneyMap.put("payedMoney", BigDecimalUtil.add(payedMoney, myPayedMoney));
            			currencySumMoneyMap.put("leftMoney", BigDecimalUtil.add(leftMoney, myLeftMoney));
            			
            			exists = true;
            			break;
            		}
        		}
        		
        		if (!exists) {
        			Map<String, Object> currencySumMoneyMap = new HashMap<String, Object>();
            		currencySumMoneyMap.put("currencyId", currencyId);
            		currencySumMoneyMap.put("currencyCode", currencyCode);
            		currencySumMoneyMap.put("totalMoney", totalMoney);
            		currencySumMoneyMap.put("payedMoney", payedMoney);
            		currencySumMoneyMap.put("leftMoney", leftMoney);
            		currencySumMoneyList.add(currencySumMoneyMap);
        		}
        		
        		//折合成本位币的金额
        		sumStandardTotalMoney = BigDecimalUtil.add(sumStandardTotalMoney, BigDecimalUtil.multiply(totalMoney, exchangeRate));
        		sumStandardPayedMoney = BigDecimalUtil.add(sumStandardPayedMoney, BigDecimalUtil.multiply(payedMoney, exchangeRate));
        		sumStandardLeftMoney = BigDecimalUtil.add(sumStandardLeftMoney, BigDecimalUtil.multiply(leftMoney, exchangeRate));
        	}
        	
        	for (Map<String, Object> currencySumMoneyMap : currencySumMoneyList) {
        		Double payedMoney = (Double) currencySumMoneyMap.get("payedMoney");
        		Double totalMoney = (Double) currencySumMoneyMap.get("totalMoney");
        		Double leftMoney = (Double) currencySumMoneyMap.get("leftMoney");
        		Double payedRate = BigDecimalUtil.divide(payedMoney, totalMoney);
        		currencySumMoneyMap.put("payedRate", payedRate * 100);
        		
        		String currencyCode = (String) currencySumMoneyMap.get("currencyCode");
        		//格式化金额
        		String totalMoneyStr = df.format(totalMoney);
        		String payedMoneyStr = df.format(payedMoney);
        		String leftMoneyStr = df.format(leftMoney);
        		if (!singleCurrencyFlag) {
        			totalMoneyStr += "(" + currencyCode + ")";
        			payedMoneyStr += "(" + currencyCode + ")";
        			leftMoneyStr += "(" + currencyCode + ")";
        		}
        		
        		currencySumMoneyMap.put("totalMoney", totalMoneyStr);
        		currencySumMoneyMap.put("payedMoney",  payedMoneyStr);
        		currencySumMoneyMap.put("leftMoney", leftMoneyStr);
        	}
        	
        	//本位币总金额统计信息
        	Double payedRate = 0.00;	//已付比例
        	if (sumStandardTotalMoney != 0) {
        		payedRate = BigDecimalUtil.divide(sumStandardPayedMoney, sumStandardTotalMoney);
        	}
        	Map<String, Object> standardSumMoneyMap = new HashMap<String, Object>();
        	String totalMoney = df.format(sumStandardTotalMoney);
        	String payedMoney = df.format(sumStandardPayedMoney);
        	String leftMoney = df.format(sumStandardLeftMoney);
        	String currencyCode = standardCurrency.getCode();
        	
        	if (!singleCurrencyFlag) {
        		totalMoney += "(" + currencyCode + ")";
        		payedMoney += "(" + currencyCode + ")";
        		leftMoney += "(" + currencyCode + ")";
        	}
        	
        	standardSumMoneyMap.put("totalMoney", totalMoney);
        	standardSumMoneyMap.put("payedMoney", payedMoney);
        	standardSumMoneyMap.put("leftMoney", leftMoney);
        	
        	standardSumMoneyMap.put("payedRate", payedRate * 100);
        	standardSumMoneyMap.put("currencyCode", currencyCode);
        	
        	data.put("standardSumMoneyMap", standardSumMoneyMap);
        	data.put("currencySumMoneyList", currencySumMoneyList);
        	
        	Date nowDate = new Date();
        	//导出文件名
			String fileName = nowDate.getTime()+"职员合同";
			//获取模板文件地址配置
			Properties property = PropertiesUitls.fetchProperties("/config.properties");
			String srcfilePath =  property.getProperty("contract_worker_templete");
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
			
			this.sysLogService.saveSysLog(request, "导出职员合同统计列表", Constants.TERMINAL_PC, ContractWorkerModel.TABLE_NAME, null, 5);
		} catch (Exception e) {
			message = "未知错误，导出失败";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "导出职员合同统计列表失败：" + e.getMessage(), Constants.TERMINAL_PC, ContractWorkerModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	
	/**
	 * 导出职员合同的详细信息列表；不支持高级查询
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exportContractWorkerDetail")
	public Map<String, Object> exportContractWorkerDetail(HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			//获取剧组id
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			boolean  singleCurrencyFlag= false;	//标识剧组中是否只有一个有效币种
			Map<String, Object> conditionMap = new HashMap<String, Object>();
        	conditionMap.put("crewId", crewInfo.getCrewId());
        	conditionMap.put("ifEnable", true);
        	List<CurrencyInfoModel> currencyInfoList = this.currencyInfoService.queryManyByMutiCondition(conditionMap, null);
			if (currencyInfoList.size() == 1) {
				singleCurrencyFlag = true;
			}
			//查询数据列表
			List<ContractWorkerModel> contractWorkerList = this.contractWorkerService.queryByCrewId(crewInfo.getCrewId());
			if (contractWorkerList == null || contractWorkerList.size() == 0) {
				throw new IllegalArgumentException("暂无职员合同信息！");
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			DecimalFormat moneydf = new DecimalFormat("#,##0.00");
			Map<String, Object> data = new HashMap<String, Object>();
			List<Map<String, Object>> formatDataList = new ArrayList<Map<String,Object>>();
			//对查询的结果进行格式化
			for (ContractWorkerModel model : contractWorkerList) {
				Map<String, Object> modelMap = new HashMap<String, Object>();
				modelMap.put("workerName", model.getWorkerName());
				modelMap.put("department", model.getDepartment());
				modelMap.put("phone", model.getPhone());
				modelMap.put("contractNo", model.getContractNo());
				
				if (model.getContractDate() != null ) {
					modelMap.put("contractDate", sdf.format(model.getContractDate()));
				}else {
					modelMap.put("contractDate", "");
				}
				if (model.getEnterDate() != null) {
					modelMap.put("enterDate", sdf.format(model.getEnterDate()));
				}else {
					modelMap.put("enterDate", "");
				}
				if (model.getLeaveDate() != null) {
					
					modelMap.put("leaveDate", sdf.format(model.getLeaveDate()));
				}else {
					modelMap.put("leaveDate", "");
				}
				
				String totalMoney = moneydf.format(model.getTotalMoney());
				if (!singleCurrencyFlag) {
					//根据币种id查询币种符号
					CurrencyInfoModel currencyInfoModel = this.currencyInfoService.queryById(model.getCurrencyId());
					totalMoney += "(" +currencyInfoModel.getCode() +")";
				}
				modelMap.put("totalMoney", totalMoney);
				
				
				//获取证件类型
				modelMap.put("identityCardType", IdentityCardType.valueOf(model.getIdentityCardType()).getName());
				modelMap.put("identityCardNumber", model.getIdentityCardNumber());
				
				modelMap.put("bankName", model.getBankName());
				modelMap.put("bankAccountName", model.getBankAccountName());
				modelMap.put("bankAccountNumber", model.getBankAccountNumber());
				modelMap.put("financeSubjName", model.getFinanceSubjName());
				modelMap.put("remark", model.getRemark());
				formatDataList.add(modelMap);
			}
			data.put("contractWorkerDetailList", formatDataList);
			
			Date nowDate = new Date();
			//导出文件名
			String fileName = nowDate.getTime()+"职员合同详细信息表";
			//获取模板文件地址配置
			Properties property = PropertiesUitls.fetchProperties("/config.properties");
			String srcfilePath =  property.getProperty("contract_worker_detail_templete");
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
			
			this.sysLogService.saveSysLog(request, "导出职员合同详细信息", Constants.TERMINAL_PC, ContractWorkerModel.TABLE_NAME, null, 5);
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
		} catch (Exception e) {
			message = "未知错误，导出失败！";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "导出职员合同详细信息失败：" + e.getMessage(), Constants.TERMINAL_PC, ContractWorkerModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 查询所有职员姓名（用于下拉控件）
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryDropDownList")
	public Map<String, Object> queryDropDownList(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	
        	this.financeSubjectService.refreshCachedSubjectList(crewId);
        	
        	List<String> workerNameList = new ArrayList<String>();
        	List<Map<String, String>> financeSubjList = new ArrayList<Map<String, String>>();	//财务科目信息
        	List<String> financeSubjIdList = new ArrayList<String>();	//财务科目Id
        	
        	List<ContractWorkerModel> contractWorkerList = this.contractWorkerService.queryByCrewId(crewId);
        	for (ContractWorkerModel contractWorker : contractWorkerList) {
        		String workerName = contractWorker.getWorkerName();
        		
        		String financeSubjId = contractWorker.getFinanceSubjId();
        		String financeSubjName = this.financeSubjectService.getFinanceSubjName(financeSubjId);
        		
        		if (!StringUtils.isBlank(financeSubjId) && !financeSubjIdList.contains(financeSubjId)) {
        			financeSubjIdList.add(financeSubjId);
        			Map<String, String> financeSubjMap = new HashMap<String, String>();
        			financeSubjMap.put("financeSubjId", financeSubjId);
        			financeSubjMap.put("financeSubjName", financeSubjName);
        			financeSubjList.add(financeSubjMap);
        		}
        		if (!workerNameList.contains(workerName)) {
        			workerNameList.add(workerName);
        		}
        	}
        	
        	resultMap.put("workerNameList", workerNameList);
        	resultMap.put("financeSubjList", financeSubjList);
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
	 * 保存职员合同信息
	 * @param request
	 * @param contractId	合同ID
	 * @param contractNo	合同编号
	 * @param contractDate	支付日期
	 * @param workerName	职员名称
	 * @param department	部门职务
	 * @param phone	联系电话
	 * @param idNumber	身份证
	 * @param enterDate	入组时间
	 * @param leaveDate	离组时间
	 * @param currencyId	货币ID
	 * @param totalMoney	总金额
	 * @param paymentTerm 支付方式：
	 * 按阶段支付格式---阶段&&提醒时间&&支付条件&&支付比例&&支付金额 
	 * 按月支付格式----备注&&月薪&&付款开始日期&&付款结束日期&&每月发薪日
	 * 多个以##隔开
	 * @param monthPayDetail 按月支付薪酬明细
	 * @param bankName	银行名称
	 * @param bankAccountName	账户名称
	 * @param bankAccountNumber	账号
	 * @param payWay	支付方式：1-按阶段  2-按月
	 * @param financeSubjId	财务科目ID
	 * @param financeSubjName	财务科目名称
	 * @param remark	备注
	 * @param attpackId 附件包ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveContractInfo")
	public Map<String, Object> saveContractInfo(HttpServletRequest request, String contractId, String contractNo,
			String contractDate, String workerName, String department, 
			String phone, Integer identityCardType, String identityCardNumber, String enterDate, String leaveDate,
			String currencyId, Double totalMoney, String paymentTerm, String monthPayDetail,
			String bankName, String bankAccountName, String bankAccountNumber, 
			Integer payWay, String financeSubjId, String financeSubjName, String remark, String attpackId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	if (StringUtils.isBlank(workerName)) {
        		throw new IllegalArgumentException("请填写员工姓名");
        	}
        	if (StringUtils.isBlank(currencyId)) {
        		throw new IllegalArgumentException("请选择币种");
        	}
        	if (totalMoney == null) {
        		throw new IllegalArgumentException("请填写总金额");
        	}
        	if (StringUtils.isBlank(contractDate)) {
        		throw new IllegalArgumentException("请填写合同日期");
        	}
        	
        	if (!StringUtils.isBlank(enterDate) && !StringUtils.isBlank(leaveDate) && this.sdf1.parse(enterDate).after(this.sdf1.parse(leaveDate))) {
        		throw new IllegalArgumentException("在组日期开始时间不能晚于结束时间");
        	}
        	
        	if (StringUtils.isBlank(paymentTerm)) {
        		throw new IllegalArgumentException("请填写支付方式");
        	}
        	
        	String crewId = this.getCrewId(request);
        	UserInfoModel loginUserInfo = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);
    		String loginUserId = loginUserInfo.getUserId();
        	
        	//如果修改合同，且合同下已关联付款单，则不允许修改货币
        	if (!StringUtils.isBlank(contractId)) {
        		List<Map<String, Object>> paymentList = this.paymentInfoService.queryByContractId(contractId);
        		ContractWorkerModel myContract = this.contractWorkerService.queryById(crewId, contractId);
        		if (paymentList != null && paymentList.size() > 0 && !myContract.getCurrencyId().equals(currencyId)) {
        			throw new IllegalArgumentException("合同已关联付款单，不能够修改币种");
        		}
        	}
        	
        	ContractWorkerModel contractWorker = this.contractWorkerService.saveContractInfo(crewId, loginUserId, contractId, contractNo, 
        			contractDate, workerName, department, phone, identityCardType, identityCardNumber, 
        			enterDate, leaveDate, currencyId, totalMoney, paymentTerm, monthPayDetail,
        			bankName, bankAccountName, bankAccountNumber, payWay, financeSubjId, financeSubjName, remark, attpackId);
        	
        	resultMap.put("attpackId", contractWorker.getAttpackId());
        	String logDesc = "";
			Integer operType = null;
			if(StringUtil.isBlank(contractId)) {
				logDesc = "添加职员合同";
				operType = 1;
			} else {
				logDesc = "修改职员合同";
				operType = 2;
			}
        	this.sysLogService.saveSysLog(request, logDesc, Constants.TERMINAL_PC, ContractWorkerModel.TABLE_NAME, contractWorker.getContractNo(), operType);
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
            
            logger.error(ie.getMessage(), ie);
        } catch (ParseException pe) {
        	success = false;
        	message = "时间格式错误";
        	
        	logger.error("时间格式错误", pe);
        } catch(Exception e) {
            success = false;
            message = "未知异常";

            logger.error("未知异常", e);
        	this.sysLogService.saveSysLog(request, "保存职员合同失败：" + e.getMessage(), Constants.TERMINAL_PC, ContractWorkerModel.TABLE_NAME, contractNo, SysLogOperType.ERROR.getValue());
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
	
	/**
	 * 删除合同
	 * @param request
	 * @param contractId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteContract")
	public Map<String, Object> deleteContract(HttpServletRequest request, String contractId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	if (StringUtils.isBlank(contractId)) {
        		throw new IllegalArgumentException("请提供合同ID");
        	}
        	
        	
        	String crewId = this.getCrewId(request);
        	
        	//判断是否已经产生付付款单
        	List<Map<String, Object>> paymentInfoList = this.paymentInfoService.queryByContractId(contractId);
        	if (paymentInfoList != null && paymentInfoList.size() > 0) {
        		throw new IllegalArgumentException("该合同已产生付款，不可以删除");
        	}
        	
        	this.contractWorkerService.deleteContract(crewId, contractId);        	
        	
        	this.sysLogService.saveSysLog(request, "删除职员合同", Constants.TERMINAL_PC, ContractWorkerModel.TABLE_NAME, contractId, 3);
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
        } catch(Exception e) {
            success = false;
            message = "未知异常";

            logger.error("未知异常", e);
        	this.sysLogService.saveSysLog(request, "删除职员合同失败：" + e.getMessage(), Constants.TERMINAL_PC, ContractWorkerModel.TABLE_NAME, contractId, SysLogOperType.ERROR.getValue());
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
	
	/**
	 * 根据合同ID查询合同信息
	 * @param contractId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryContractById")
	public Map<String, Object> queryContractById(HttpServletRequest request, String contractId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	if (StringUtils.isBlank(contractId)) {
        		throw new IllegalArgumentException("请提供合同ID");
        	}
        	
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	
        	String crewId = this.getCrewId(request);
        	this.financeSubjectService.refreshCachedSubjectList(crewId);
        	
        	ContractWorkerModel contractWorker = this.contractWorkerService.queryById(crewId, contractId);
        	if (contractWorker == null) {
        		throw new IllegalArgumentException("不存在的合同信息");
        	}
        	
        	Map<String, Object> contractInfo = new HashMap<String, Object>();
        	contractInfo.put("contractId", contractWorker.getContractId());
        	contractInfo.put("contractNo", contractWorker.getContractNo());
        	contractInfo.put("contractDate", sdf.format(contractWorker.getContractDate()));
        	contractInfo.put("workerName", contractWorker.getWorkerName());
        	contractInfo.put("department", contractWorker.getDepartment());
        	contractInfo.put("phone", contractWorker.getPhone());
        	contractInfo.put("identityCardType", contractWorker.getIdentityCardType());
        	contractInfo.put("identityCardNumber", contractWorker.getIdentityCardNumber());
        	if (contractWorker.getEnterDate() != null) {
        		contractInfo.put("enterDate", sdf.format(contractWorker.getEnterDate()));
        	}
        	if (contractWorker.getLeaveDate() != null) {
        		contractInfo.put("leaveDate", sdf.format(contractWorker.getLeaveDate()));
        	}
        	contractInfo.put("currencyId", contractWorker.getCurrencyId());
        	contractInfo.put("totalMoney", contractWorker.getTotalMoney());
        	contractInfo.put("bankName", contractWorker.getBankName());
        	contractInfo.put("bankAccountName", contractWorker.getBankAccountName());
        	contractInfo.put("bankAccountNumber", contractWorker.getBankAccountNumber());
        	contractInfo.put("payWay", contractWorker.getPayWay());
        	contractInfo.put("financeSubjId", contractWorker.getFinanceSubjId());
        	if (!StringUtils.isBlank(contractWorker.getFinanceSubjId())) {
        		contractInfo.put("financeSubjName", this.financeSubjectService.getFinanceSubjName(contractWorker.getFinanceSubjId()));
        	} else {
        		contractInfo.put("financeSubjName", "");
        	}
        	
        	contractInfo.put("attpackId", contractWorker.getAttpackId());
        	contractInfo.put("remark", contractWorker.getRemark());
        	
        	
        	//支付方式信息
        	List<ContractStagePayWayModel> contractStagePayWayList = this.contractPayWayService.queryByContractId(contractId, crewId);
        	
        	Map<String, Object> paywayConditionMap = new HashMap<String, Object>();
        	paywayConditionMap.put("contractId", contractId);
        	paywayConditionMap.put("crewId", crewId);
        	List<ContractMonthPaywayModel> monthPaywayList = this.contractPayWayService.queryMonthPaywayManyByMutiCondition(paywayConditionMap, null);
        	List<ContractMonthPayDetailModel> monthPayDetailList = this.contractPayWayService.queryMonthPayDetailManyByMutiCondition(paywayConditionMap, null);
        	
        	List<Map<String, Object>> contractStagePayWayMapList = new ArrayList<Map<String, Object>>();
        	for (ContractStagePayWayModel contractPayWay : contractStagePayWayList) {
        		Map<String, Object> paywayMap = new HashMap<String, Object>();
        		paywayMap.put("id", contractPayWay.getId());
        		paywayMap.put("remark", contractPayWay.getRemark());
        		paywayMap.put("money", contractPayWay.getMoney());
        		paywayMap.put("rate", contractPayWay.getRate());
        		if (contractPayWay.getRemindTime() != null) {
        			paywayMap.put("remindTime", this.sdf1.format(contractPayWay.getRemindTime()));
        		}
        		paywayMap.put("stage", contractPayWay.getStage());
        		
        		contractStagePayWayMapList.add(paywayMap);
        	}
        	
        	List<Map<String, Object>> contractMonthPayWayMapList = new ArrayList<Map<String, Object>>();
        	for (ContractMonthPaywayModel monthPayway : monthPaywayList) {
        		Map<String, Object> paywayMap = new HashMap<String, Object>();
        		paywayMap.put("id", monthPayway.getId());
        		paywayMap.put("monthMoney", monthPayway.getMonthMoney());
        		paywayMap.put("startDate", this.sdf1.format(monthPayway.getStartDate()));
        		paywayMap.put("endDate", this.sdf1.format(monthPayway.getEndDate()));
        		paywayMap.put("monthPayDay", monthPayway.getMonthPayDay());
        		paywayMap.put("remark", monthPayway.getRemark());
        		contractMonthPayWayMapList.add(paywayMap);
        	}
        	
        	List<Map<String, Object>> contractMonthPayDetailMapList = new ArrayList<Map<String, Object>>();
        	for (ContractMonthPayDetailModel payDetail : monthPayDetailList) {
        		Map<String, Object> payDetailMap = new HashMap<String, Object>();
        		payDetailMap.put("id", payDetail.getId());
        		payDetailMap.put("month", this.sdf4.format(payDetail.getMonth()));
        		payDetailMap.put("startDate", this.sdf3.format(payDetail.getStartDate()));
        		payDetailMap.put("endDate", this.sdf3.format(payDetail.getEndDate()));
        		payDetailMap.put("money", payDetail.getMoney());
        		payDetailMap.put("payDate", this.sdf3.format(payDetail.getPayDate()));
        		contractMonthPayDetailMapList.add(payDetailMap);
        	}
        	
        	//附件信息
        	List<AttachmentModel> attachmentList = this.attachmentService.queryAttByPackId(contractWorker.getAttpackId());
        	
        	//付款单信息
        	List<Map<String, Object>> paymentList = this.paymentInfoService.queryByContractId(contractId);
        	for (Map<String, Object> paymentInfo : paymentList) {
        		Date paymentDate = (Date) paymentInfo.get("paymentDate");
        		paymentInfo.put("paymentDate", sdf.format(paymentDate));
        	}
        	

        	resultMap.put("contractInfo", contractInfo);
        	resultMap.put("contractStagePayWayList", contractStagePayWayMapList);
        	resultMap.put("contractMonthPayWayList", contractMonthPayWayMapList);
        	resultMap.put("attachmentList", attachmentList);
        	resultMap.put("paymentList", paymentList);
        	resultMap.put("contractMonthPayDetailList", contractMonthPayDetailMapList);
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
	 * 导入职员合同详情
	 * @param request
	 * @param file
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/importantContractWorkerDetail")
	public Map<String, Object> importantContractWorkerDetail(HttpServletRequest request, MultipartFile file, boolean isCover){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		String crewId = this.getCrewId(request);
		String crewName = crewInfo.getCrewName();
		SimpleDateFormat secondFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			if (file == null) {
				throw new IllegalArgumentException("请选择上传的文件！");
			}
			boolean  singleCurrencyFlag= false;	//标识剧组中是否只有一个有效币种
			Map<String, Object> currencyConditionMap = new HashMap<String, Object>();
			currencyConditionMap.put("crewId", crewInfo.getCrewId());
			currencyConditionMap.put("ifEnable", true);
        	List<CurrencyInfoModel> currencyInfoList = this.currencyInfoService.queryManyByMutiCondition(currencyConditionMap, null);
			if (currencyInfoList.size() == 1) {
				singleCurrencyFlag = true;
			}
			
			// 上传文件到服务器
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String baseStorePath = properties.getProperty("fileupload.path");
			String modelStorePath = baseStorePath + "import/contract";
			String newName = crewName + secondFormat.format(new Date());
			Map<String, String> fileMap = FileUtils.uploadFileForExcel(request, modelStorePath, newName);
			if (fileMap == null) {
				throw new IllegalArgumentException("请选择文件");
			}
			
			String fileStoreName = fileMap.get("fileStoreName");// 新文件名
			String storePath = fileMap.get("storePath");// 服务器存文文件路径
			//读取excel表的数据
			Map<String, Object> getCostInfoMap = ExcelUtils.readGetCostInfo(storePath + fileStoreName);
			//刷新缓存中的财务科目信息
			this.financeSubjectService.refreshCachedSubjectList(crewId);
			List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
			
			String errorMessage = "";
			//取出excel读取的数据
			Set<String> sheetSet = getCostInfoMap.keySet();
			Iterator<String> sheetKeys = sheetSet.iterator();
			while(sheetKeys.hasNext()){
				String sheetKey = sheetKeys.next();
				//excel读取的数据
				List<ArrayList<String>> excelDataList = (List<ArrayList<String>>)getCostInfoMap.get(sheetKey);
				//为空或者只有一行（标题）则不保存
				if(excelDataList==null||excelDataList.size()<3){
					continue;
				}
				
				//读取第一行的数据
				for (int i=0; i<excelDataList.size(); i++) {
					if (i == 0 || i == 1) {
						continue;
					}
					Map<String, Object> dataMap = new HashMap<String, Object>();
					List<String> arrayList = excelDataList.get(i);
					String customContractNo = arrayList.get(0);
					if (StringUtils.isBlank(customContractNo)) {
						errorMessage += "请填写合同编号；";
					}
					if (customContractNo.length() > 32) {
						errorMessage += "合同编号过长；";
					}
					dataMap.put("customContractNo", customContractNo);
					
					String workerName = arrayList.get(1);
					dataMap.put("workerName", workerName);
					if (StringUtils.isBlank(workerName)) {
						errorMessage += "请填写职员姓名";
					}
					String department = arrayList.get(2);
					dataMap.put("department", department);
					String phone = arrayList.get(3);
					dataMap.put("phone", phone);
					String financeSubjName = arrayList.get(4);
					
					//根据多个条件查询是否是重复数据
					Map<String, Object> conditionMap = new HashMap<String, Object>();
					conditionMap.put("workerName", workerName);
					conditionMap.put("phone", phone);
					conditionMap.put("crewId", crewId);
					conditionMap.put("customContractNo", customContractNo);
					List<ContractWorkerModel> workerByCustomNo = this.contractWorkerService.queryManyByMutiCondition(conditionMap, null);
					if (workerByCustomNo == null || workerByCustomNo.size() == 0) {
						conditionMap.remove("customContractNo");
						conditionMap.put("contractNo", customContractNo);
						workerByCustomNo = this.contractWorkerService.queryManyByMutiCondition(conditionMap, null);
						if (workerByCustomNo == null || workerByCustomNo.size() == 0) {
							dataMap.put("isRepeat", false);
						} else {
							dataMap.put("isRepeat", true);
						}
					}else {
						dataMap.put("isRepeat", true);
					}
					
					//如果保存了财务科目，则需要查询出财务科目对应的id
					String financeSubjId = "";
					if (StringUtils.isNotBlank(financeSubjName)) {
						List<FinanceSubjectDto> financeList = this.financeSubjectService.getFinanceSubjByName(financeSubjName);
						if (financeList == null || financeList.size() == 0) {
				 			errorMessage += "财务科目填写错误；";
						}else if (financeList.size() > 1) {
							errorMessage += "请填写完整的财务科目节点信息；";
						}else if (financeList.size() == 1) {
							financeSubjId = financeList.get(0).getId();
						}
					}
					dataMap.put("financeSubjId", financeSubjId);
					dataMap.put("financeSubjName", financeSubjName);
					
					//获取总金额中的币种
					String totalMoneyStr = arrayList.get(5);
					Double totalMoney = null;
					String code = "";
					String currencyId = "";
					if (StringUtils.isBlank(totalMoneyStr)) {
						errorMessage += "请填写总金额；";
					}else {
						totalMoneyStr = totalMoneyStr.replaceAll("（", "(").replaceAll("）", ")");
						if (!singleCurrencyFlag && totalMoneyStr.indexOf("(") == -1) {
							errorMessage += "请填写货币编号；";
						}
						//获取币种信息
						if (singleCurrencyFlag) {
							currencyId = currencyInfoList.get(0).getId();
						} else if (totalMoneyStr.indexOf("(") != -1) {
							code = totalMoneyStr.substring(totalMoneyStr.indexOf("(") + 1, totalMoneyStr.lastIndexOf(")"));
							//根据获取的货币编码获取货币的id
							//根据币种id查询币种符号
							Map<String, Object> codeConditionMap = new HashMap<String, Object>();
							codeConditionMap.put("crewId", crewId);
							codeConditionMap.put("code", code);
							codeConditionMap.put("ifEnable", 1);
							
							List<CurrencyInfoModel> currencyList = this.currencyInfoService.queryManyByMutiCondition(codeConditionMap, null);
							if (currencyList == null || currencyList.size() == 0) {
								errorMessage += "货币编码输入错误；";
							} else {
								currencyId = currencyList.get(0).getId();
							}
						}
						
						//获取金额信息
						if (totalMoneyStr.indexOf("(") != -1) {
							String moneyStr = totalMoneyStr.substring(0,totalMoneyStr.indexOf("("));
							String newMoneyStr = moneyStr.replaceAll(",", "");
							//将字符串金额变为double
							totalMoney = Double.parseDouble(newMoneyStr);
						} else {
							totalMoney = Double.parseDouble(totalMoneyStr.replaceAll(",", ""));
						}
					}
					
					dataMap.put("totalMoney", totalMoney);
					dataMap.put("currencyId", currencyId);
					
					//获取证件类型所对应的数值
					int identityCardType = 0;
					String identityCardName = arrayList.get(6);
					if (StringUtils.isBlank(identityCardName)) {
						errorMessage += "请填写证件类型；";
					}else {
						identityCardType = IdentityCardType.nameOf(identityCardName).getValue();
					}
					dataMap.put("identityCardType", identityCardType);
					
					//证件号
					String identityCardNumber = arrayList.get(7);
					dataMap.put("identityCardNumber", identityCardNumber);
					
					//入组日期
					String enterDateStr = arrayList.get(8);
					dataMap.put("enterDateStr", enterDateStr);
					
					//离组时间
					String leaveDateStr = arrayList.get(9);
					dataMap.put("leaveDateStr", leaveDateStr);
					
					//合同签署日期
					String contractDateStr = arrayList.get(10);
					Date contractDate = null;
					if (StringUtils.isBlank(contractDateStr)) {
						errorMessage += "签署日期不能为空；";
					}else {
						contractDate = format.parse(contractDateStr);
					}
					dataMap.put("contractDate", contractDate);
					
					//银行名称
					String bankName = arrayList.get(11);
					dataMap.put("bankName", bankName);
					//账号名称
					String bankAccountName = arrayList.get(12);
					dataMap.put("bankAccountName", bankAccountName);
					//账号
					String bankAccountNumber = arrayList.get(13);
					dataMap.put("bankAccountNumber", bankAccountNumber);
					//备注
					String remark = arrayList.get(14);
					dataMap.put("remark", remark);
					
					if (StringUtils.isNotBlank(errorMessage)) {
						throw new IllegalArgumentException("第"+ (i+1) + "行 " + errorMessage);
					}else {
						dataList.add(dataMap);
					}
					
				}
				
				if (StringUtils.isBlank(errorMessage)) {
					for (Map<String, Object> dataMap: dataList) {
						boolean isRepeat = (Boolean) dataMap.get("isRepeat");
						String customContractNo = (String) dataMap.get("customContractNo");
						Date contractDate = (Date) dataMap.get("contractDate");
						String workerName = (String) dataMap.get("workerName");
						String department = (String) dataMap.get("department");
						String phone = (String) dataMap.get("phone");
						
						int identityCardType = (Integer) dataMap.get("identityCardType");
						String identityCardNumber = (String) dataMap.get("identityCardNumber");
						
						String enterDateStr = (String) dataMap.get("enterDateStr");
						Date enterDate = null;
						if (StringUtils.isNotBlank(enterDateStr)) {
							enterDate = format.parse(enterDateStr);
						}
						
						String leaveDateStr = (String) dataMap.get("leaveDateStr");
						Date leaveDate = null;
						if (StringUtils.isNotBlank(leaveDateStr)) {
							leaveDate = format.parse(leaveDateStr);
						}
						
						String currencyId = (String) dataMap.get("currencyId");
						Double totalMoney = (Double) dataMap.get("totalMoney");
						String bankName = (String) dataMap.get("bankName");
						String bankAccountName = (String) dataMap.get("bankAccountName");
						
						String bankAccountNumber = (String) dataMap.get("bankAccountNumber");
						String financeSubjId = (String) dataMap.get("financeSubjId");
						String financeSubjName = (String) dataMap.get("financeSubjName");
						String remark = (String) dataMap.get("remark");
						
						//将信息保存起来
						this.contractWorkerService.saveImportContractInfo(crewId, isCover, isRepeat, customContractNo, contractDate, workerName, department, 
								phone, identityCardType, identityCardNumber, enterDate, leaveDate, currencyId, totalMoney, bankName, bankAccountName, 
								bankAccountNumber, financeSubjId, financeSubjName, remark);
					}
				}
			}
			this.sysLogService.saveSysLog(request, "导入职员合同", Constants.TERMINAL_PC, ContractWorkerModel.TABLE_NAME, null, 4);
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(ie.getMessage(), ie);
		}catch (ParseException pe) {
			message = "时间格式错误，请修改！";
			success = false;
			
			logger.error(message, pe);
		} catch (Exception e) {
			message = "未知异常，导入失败";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "导入职员合同失败：" + e.getMessage(), Constants.TERMINAL_PC, ContractWorkerModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 根据电话号码和姓名，查询是够有重复数据
	 * @param request
	 * @param workerName
	 * @param phone
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryRepeatData")
	public Map<String, Object> queryRepeatData(HttpServletRequest request, String workerName, String phone, String contractId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			boolean isRepeat = false;
			
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("crewId", crewId);
			conditionMap.put("workerName", workerName);
			conditionMap.put("phone", phone);
			List<ContractWorkerModel> list = this.contractWorkerService.queryManyByMutiCondition(conditionMap, null);
			
			for (ContractWorkerModel model : list) {
				if (!model.getContractId().equals(contractId)) {
					isRepeat = true; 
				}
			}
			
			resultMap.put("isRepeat", isRepeat);
 		} catch (Exception e) {
			message = "未知异常，查询失败！";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 根据姓名查询职务列表
	 * @param request
	 * @param contactName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryDepartmentList")
	public Map<String, Object> queryDepartmentList(HttpServletRequest request, String contactName){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message ="";
		boolean success = true;
		
		try {
			String cewId = this.getCrewId(request);
			
			List<Map<String,Object>> list = null;
			String department = "";
			if (StringUtils.isNotBlank(contactName)) {
				list = this.contractWorkerService.queryDepatmentListByContactName(contactName, cewId);
				if (list != null) {
					Map<String, Object> map = list.get(0);
					department = (String) map.get("department");
				}
			}
			
			resultMap.put("department", department);
			
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
	
