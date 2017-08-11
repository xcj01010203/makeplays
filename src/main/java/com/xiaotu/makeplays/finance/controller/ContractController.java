package com.xiaotu.makeplays.finance.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.xiaotu.makeplays.finance.model.ContractActorModel;
import com.xiaotu.makeplays.finance.model.ContractMonthPayDetailModel;
import com.xiaotu.makeplays.finance.model.ContractProduceModel;
import com.xiaotu.makeplays.finance.model.ContractStagePayWayModel;
import com.xiaotu.makeplays.finance.model.ContractWorkerModel;
import com.xiaotu.makeplays.finance.model.CurrencyInfoModel;
import com.xiaotu.makeplays.finance.model.constants.ContractPayWay;
import com.xiaotu.makeplays.finance.model.constants.ContractType;
import com.xiaotu.makeplays.finance.service.ContractActorService;
import com.xiaotu.makeplays.finance.service.ContractPayWayService;
import com.xiaotu.makeplays.finance.service.ContractProduceService;
import com.xiaotu.makeplays.finance.service.ContractWorkerService;
import com.xiaotu.makeplays.finance.service.CurrencyInfoService;
import com.xiaotu.makeplays.finance.service.FinanceSubjectService;
import com.xiaotu.makeplays.finance.service.PaymentInfoService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.OfficeUtils;
import com.xiaotu.makeplays.utils.PropertiesUitls;

/**
 * 合同的公用、综合接口
 * 职员合同、演员合同、制作合同公用接口
 * @author xuchangjian 2016-8-12下午3:36:57
 */
@Controller
@RequestMapping("/contractManager")
public class ContractController extends BaseController {
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
	
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");

	private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/MM");
	
	Logger logger = LoggerFactory.getLogger(ContractController.class);

	@Autowired
	private PaymentInfoService paymentInfoService;
	
	@Autowired
	private AttachmentService attachmentService;
	
	@Autowired
	private ContractActorService contractActorService;
	
	@Autowired
	private ContractProduceService contractProduceService;
	
	@Autowired
	private ContractWorkerService contractWorkerService;
	
	@Autowired
	private ContractPayWayService contractPayWayService;
	
	@Autowired
	private CurrencyInfoService currencyInfoService;
	
	@Autowired
	private FinanceSubjectService financeSubjectService;
	
	/**
	 * 跳转到合同管理页面
	 * @param contractType	1-职员合同   2-演员合同  3-制作合同  4- 合同待付清单
	 * @return
	 */
	@RequestMapping("/toContractPage")
	public ModelAndView toContractPage(Integer contractType) {
		ModelAndView mv = new ModelAndView();
		
		if (contractType == 1) {
			mv.setViewName("/finance/contract/contractWorker");
		}
		if (contractType == 2) {
			mv.setViewName("/finance/contract/contractActor");
		}
		if (contractType == 3) {
			mv.setViewName("/finance/contract/contractProduce");
		}
		if (contractType == 4) {
			mv.setViewName("/finance/contract/contractToPaid");
		}
		return mv;
	}
	
	/**
	 * 上传合同附件
	 * @param request
	 * @param attpackId	附件包ID
	 * @param file
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/uploadAttachment")
	public Map<String, Object> uploadAttachment(HttpServletRequest request, String attpackId, MultipartFile file) {
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
			String storePath = baseStorePath + "common/";
			
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
			
			//把word文件转换为pdf文件
			if (suffix.equals(".doc") || suffix.equals(".docx")) {
				String dateStr = this.sdf1.format(new Date());
				
				sdStorePath = storePath + dateStr + "/sd/" + exceptSuffixName + ".pdf";
				OfficeUtils.word2Format(hdStorePath, sdStorePath);
				
				String tempPath = hdStorePath;
				hdStorePath = sdStorePath;
				sdStorePath = tempPath;
				
				type = AttachmentType.Word.getValue();
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
	
	/**
	 * 获取合同列表
	 * 合同列表中包含所有类型的合同
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryContractList")
	public Map<String, Object> queryContractList(HttpServletRequest request, String name) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			String crewId = getCrewId(request);
			
			this.financeSubjectService.refreshCachedSubjectList(crewId);
			List<Map<String, Object>> contractList = new ArrayList<Map<String, Object>>();
			
			//合同单位名称、合同ID、合同类型(1-职员合同  2-演员合同  3-制作合同)、合同编号
			
			//职员合同
			Map<String, Object> workerConditionMap = new HashMap<String, Object>();
			workerConditionMap.put("crewId", crewId);
			if (!StringUtils.isBlank(name)) {
				workerConditionMap.put("workerName", name);
			}
			
			List<ContractWorkerModel> contractWorkerList = this.contractWorkerService.queryManyByMutiCondition(workerConditionMap, null);
			for (ContractWorkerModel contractWorker : contractWorkerList) {
				String workerName = contractWorker.getWorkerName();
				String contractId = contractWorker.getContractId();
				String contractNo = contractWorker.getContractNo();
				String currencyId = contractWorker.getCurrencyId();
				String financeSubjId = contractWorker.getFinanceSubjId();
				String financeSubjName = contractWorker.getFinanceSubjName();
				
				Map<String, Object> contractMap = new HashMap<String, Object>();
				contractMap.put("name", workerName);
				contractMap.put("contractId", contractId);
				contractMap.put("contractType", ContractType.Worker.getValue());
				contractMap.put("contractNo", contractNo);
				contractMap.put("currencyId", currencyId);
				contractMap.put("financeSubjId", financeSubjId);
				
				if (!StringUtils.isBlank(financeSubjId)) {
					financeSubjName = this.financeSubjectService.getFinanceSubjName(financeSubjId);
				}
				contractMap.put("financeSubjName", financeSubjName);
				contractList.add(contractMap);
			}
			
			
			//演员合同
			Map<String, Object> actorConditionMap = new HashMap<String, Object>();
			actorConditionMap.put("crewId", crewId);
			if (!StringUtils.isBlank(name)) {
				actorConditionMap.put("actorName", name);
			}
			
			List<ContractActorModel> contractActorList = this.contractActorService.queryManyByMutiCondition(actorConditionMap, null);
			for (ContractActorModel contractActor : contractActorList) {
				String actorName = contractActor.getActorName();
				String contractId = contractActor.getContractId();
				String contractNo = contractActor.getContractNo();
				String currencyId = contractActor.getCurrencyId();
				String financeSubjId = contractActor.getFinanceSubjId();
				String financeSubjName = contractActor.getFinanceSubjName();
				
				Map<String, Object> contractMap = new HashMap<String, Object>();
				contractMap.put("name", actorName);
				contractMap.put("contractId", contractId);
				contractMap.put("contractType", ContractType.Actor.getValue());
				contractMap.put("contractNo", contractNo);
				contractMap.put("currencyId", currencyId);
				contractMap.put("financeSubjId", financeSubjId);
				
				if (StringUtils.isBlank(financeSubjName)) {
					financeSubjName = this.financeSubjectService.getFinanceSubjName(financeSubjId);
				}
				contractMap.put("financeSubjName", financeSubjName);
				contractList.add(contractMap);
			}
			
			
			//制作合同
			Map<String, Object> produceConditionMap = new HashMap<String, Object>();
			produceConditionMap.put("crewId", crewId);
			if (!StringUtils.isBlank(name)) {
				produceConditionMap.put("company", name);
			}
			
			List<ContractProduceModel> contractProduceList = this.contractProduceService.queryManyByMutiCondition(produceConditionMap, null);
			for (ContractProduceModel contractProduce : contractProduceList) {
				String company = contractProduce.getCompany();
				String contractId = contractProduce.getContractId();
				String contractNo = contractProduce.getContractNo();
				String currencyId = contractProduce.getCurrencyId();
				String financeSubjId = contractProduce.getFinanceSubjId();
				String financeSubjName = contractProduce.getFinanceSubjName();
				
				Map<String, Object> contractMap = new HashMap<String, Object>();
				contractMap.put("name", company);
				contractMap.put("contractId", contractId);
				contractMap.put("contractType", ContractType.Produce.getValue());
				contractMap.put("contractNo", contractNo);
				contractMap.put("currencyId", currencyId);
				contractMap.put("financeSubjId", financeSubjId);
				
				if (StringUtils.isBlank(financeSubjName)) {
					financeSubjName = this.financeSubjectService.getFinanceSubjName(financeSubjId);
				}
				contractMap.put("financeSubjName", financeSubjName);
				contractList.add(contractMap);
			}
			
			resultMap.put("contractList", contractList);
			
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
	 * 根据合同ID查询合同支付方式
	 * @param request
	 * @param contractType	合同类型：1-职员合同  2-演员合同  3-制作合同
	 * @param contractId 合同ID
	 * @return 合同对应的财务科目、合同对应的货币、银行名称、账号、账户名称、总金额、已结算金额、剩余金额、支付方式列表
	 */
	@ResponseBody
	@RequestMapping("/queryPayWayByContractId")
	public Map<String, Object> queryPayWayByContractId(HttpServletRequest request, Integer contractType, String contractId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(contractId)) {
				throw new IllegalArgumentException("请提供合同ID");
			}
			if (contractType == null) {
				throw new IllegalArgumentException("请提供合同类型");
			}
			
			String crewId = getCrewId(request);
			this.financeSubjectService.refreshCachedSubjectList(crewId);
			
			String aimPeopleName = "";
			String contractNo = "";
			String financeSubjId = "";	//财务科目ID
			String financeSubjName = "";	//财务科目名称
			String currencyId = "";	//货币名称
			String bankName = "";	//银行名称
			String bankAccountName = "";	//账户名称
			String bankAccountNumber = "";	//银行账号
			Double totalMoney = 0.00;	//总金额
			
			Integer payWay = ContractPayWay.PerStep.getValue();	//支付方式
			
			if (contractType == ContractType.Worker.getValue()) {
				ContractWorkerModel contractWorker = this.contractWorkerService.queryById(crewId, contractId);
				
				aimPeopleName = contractWorker.getWorkerName();
				contractNo = contractWorker.getContractNo();
				financeSubjId = contractWorker.getFinanceSubjId();
				if (!StringUtils.isBlank(financeSubjId)) {
					financeSubjName = this.financeSubjectService.getFinanceSubjName(financeSubjId);
				}
				currencyId = contractWorker.getCurrencyId();
				bankName = contractWorker.getBankName();
				bankAccountName = contractWorker.getBankAccountName();
				bankAccountNumber = contractWorker.getBankAccountNumber();
				totalMoney = contractWorker.getTotalMoney();
				
				payWay = contractWorker.getPayWay();
			}
			if (contractType == ContractType.Actor.getValue()) {
				ContractActorModel contractActor = this.contractActorService.queryById(crewId, contractId);
				
				aimPeopleName = contractActor.getActorName();
				contractNo = contractActor.getContractNo();
				financeSubjId = contractActor.getFinanceSubjId();
				if (!StringUtils.isBlank(financeSubjId)) {
					financeSubjName = this.financeSubjectService.getFinanceSubjName(financeSubjId);
				}
				currencyId = contractActor.getCurrencyId();
				bankName = contractActor.getBankName();
				bankAccountName = contractActor.getBankAccountName();
				bankAccountNumber = contractActor.getBankAccountNumber();
				totalMoney = contractActor.getTotalMoney();
				
				payWay = contractActor.getPayWay();
			}
			if (contractType == ContractType.Produce.getValue()) {
				ContractProduceModel contractProduce = this.contractProduceService.queryById(crewId, contractId);
				
				aimPeopleName = contractProduce.getCompany();
				contractNo = contractProduce.getContractNo();
				financeSubjId = contractProduce.getFinanceSubjId();
				if (!StringUtils.isBlank(financeSubjId)) {
					financeSubjName = this.financeSubjectService.getFinanceSubjName(financeSubjId);
				}
				currencyId = contractProduce.getCurrencyId();
				bankName = contractProduce.getBankName();
				bankAccountName = contractProduce.getBankAccountName();
				bankAccountNumber = contractProduce.getBankAccountNumber();
				totalMoney = contractProduce.getTotalMoney();
				
				payWay = contractProduce.getPayWay();
			}
			
			/*
			 * 支付方式信息
			 */
        	List<ContractStagePayWayModel> contractStagePayWayList = this.contractPayWayService.queryByContractId(contractId, crewId);
        	
        	Map<String, Object> paywayConditionMap = new HashMap<String, Object>();
        	paywayConditionMap.put("contractId", contractId);
        	paywayConditionMap.put("crewId", crewId);
        	List<ContractMonthPayDetailModel> monthPayDetailList = this.contractPayWayService.queryMonthPayDetailManyByMutiCondition(paywayConditionMap, null);
        	//按阶段支付
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
        	//按月支付薪酬明细
        	List<Map<String, Object>> contractMonthPayDetailMapList = new ArrayList<Map<String, Object>>();
        	for (ContractMonthPayDetailModel payDetail : monthPayDetailList) {
        		Map<String, Object> payDetailMap = new HashMap<String, Object>();
        		payDetailMap.put("id", payDetail.getId());
        		payDetailMap.put("month", this.sdf3.format(payDetail.getMonth()));
        		payDetailMap.put("startDate", this.sdf2.format(payDetail.getStartDate()));
        		payDetailMap.put("endDate", this.sdf2.format(payDetail.getEndDate()));
        		payDetailMap.put("money", payDetail.getMoney());
        		payDetailMap.put("payDate", this.sdf2.format(payDetail.getPayDate()));
        		contractMonthPayDetailMapList.add(payDetailMap);
        	}
			
			//合同已付款金额
			List<Map<String, Object>> paymentList = this.paymentInfoService.queryByContractId(contractId);
			Double payedMoney = 0.0;
			for (Map<String, Object> paymentInfo : paymentList) {
				Double money = (Double) paymentInfo.get("totalMoney");
				payedMoney = BigDecimalUtil.add(payedMoney, money);
			}
			
			CurrencyInfoModel currencyInfo = this.currencyInfoService.queryById(currencyId);
			totalMoney = BigDecimalUtil.multiply(totalMoney, currencyInfo.getExchangeRate());
			
			resultMap.put("contractId", contractId);
			resultMap.put("aimPeopleName", aimPeopleName);
			resultMap.put("contractNo", contractNo);
			resultMap.put("contractType", contractType);
			resultMap.put("financeSubjId", financeSubjId);	//财务科目ID
			resultMap.put("financeSubjName", financeSubjName);	//财务科目名称
			resultMap.put("currencyId", currencyId);	//货币ID
			resultMap.put("bankName", bankName);	//银行名称
			resultMap.put("bankAccountName", bankAccountName);	//银行账户名称
			resultMap.put("bankAccountNumber", bankAccountNumber);	//银行账号号码
			resultMap.put("contractStagePayWayList", contractStagePayWayMapList);	//按阶段支付方式列表
			resultMap.put("contractMonthPayDetailList", contractMonthPayDetailMapList);	//按月支付明细列表
			resultMap.put("payWay", payWay);	//付款方式
			resultMap.put("totalMoney", totalMoney);	//总金额	
			resultMap.put("payedMoney", payedMoney);	//已付金额
			resultMap.put("leftMoney", BigDecimalUtil.subtract(totalMoney, payedMoney));	//未付金额
			
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
	 * 计算合同按月支付薪酬明细
	 * paymentTerm 支付方式：
	 * 按月支付-备注&&月薪&&付款开始日期&&付款结束日期&&每月发薪日
	 * 多个以##隔开
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/calculateMonthPayDetail")
	public Map<String, Object> calculateMonthPayDetail(HttpServletRequest request, String paymentTerm, Integer payWay) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	
        	List<Map<String, Object>> monthPayDetailList = new ArrayList<Map<String,Object>>();
        	if (!StringUtils.isBlank(paymentTerm)) {
        		if (payWay == ContractPayWay.PerDayRegularSettle.getValue()) {
        			monthPayDetailList = this.contractPayWayService.genDayPayDetail(paymentTerm, crewId, payWay);
        		} else {
        			monthPayDetailList = this.contractPayWayService.genMonthPayDetail(paymentTerm, crewId, payWay);
        		}
        	}
        	
        	resultMap.put("monthPayDetailList", monthPayDetailList);
        } catch (ParseException pe) {
        	success = false;
            message = "日期不合法";
            
            logger.error("日期不合法", pe);
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
	
}
