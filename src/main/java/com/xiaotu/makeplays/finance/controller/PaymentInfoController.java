package com.xiaotu.makeplays.finance.controller;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.attachment.model.AttachmentModel;
import com.xiaotu.makeplays.attachment.service.AttachmentService;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.finance.controller.filter.PaymentInfoFilter;
import com.xiaotu.makeplays.finance.model.ContractActorModel;
import com.xiaotu.makeplays.finance.model.ContractProduceModel;
import com.xiaotu.makeplays.finance.model.ContractToPaidModel;
import com.xiaotu.makeplays.finance.model.ContractWorkerModel;
import com.xiaotu.makeplays.finance.model.FinancePaymentWayModel;
import com.xiaotu.makeplays.finance.model.LoanInfoModel;
import com.xiaotu.makeplays.finance.model.PaymentFinanSubjMapModel;
import com.xiaotu.makeplays.finance.model.PaymentInfoModel;
import com.xiaotu.makeplays.finance.model.PaymentLoanMapModel;
import com.xiaotu.makeplays.finance.model.constants.ContractType;
import com.xiaotu.makeplays.finance.model.constants.PaymentStatus;
import com.xiaotu.makeplays.finance.service.ContractActorService;
import com.xiaotu.makeplays.finance.service.ContractProduceService;
import com.xiaotu.makeplays.finance.service.ContractToPaidService;
import com.xiaotu.makeplays.finance.service.ContractWorkerService;
import com.xiaotu.makeplays.finance.service.FinancePaymentWayService;
import com.xiaotu.makeplays.finance.service.FinanceSettingService;
import com.xiaotu.makeplays.finance.service.FinanceSubjectService;
import com.xiaotu.makeplays.finance.service.LoanInfoService;
import com.xiaotu.makeplays.finance.service.PaymentFinanSubjMapService;
import com.xiaotu.makeplays.finance.service.PaymentInfoService;
import com.xiaotu.makeplays.finance.service.PaymentLoanMapService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.ExportExcelUtil;
import com.xiaotu.makeplays.utils.PropertiesUitls;

/**
 * 付款单
 * @author xuchangjian 2016-8-16下午7:22:41
 */
@Controller
@RequestMapping("/paymentManager")
public class PaymentInfoController extends BaseController {
	
	Logger logger = LoggerFactory.getLogger(PaymentInfoController.class);
	
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private ContractToPaidService contractToPaidService;
	
	@Autowired
	private PaymentInfoService paymentInfoService;
	
	@Autowired
	private FinanceSettingService financeSettingService;
	
	@Autowired
	private LoanInfoService loanInfoService;
	
	@Autowired
	private ContractActorService contractActorService;
	
	@Autowired
	private ContractWorkerService contractWorkerService;
	
	@Autowired
	private ContractProduceService contractProduceService;
	
	@Autowired
	private PaymentFinanSubjMapService paymentFinanSubjMapService;
	
	@Autowired
	private PaymentLoanMapService paymentLoanMapService;
	
	@Autowired
	private FinanceSubjectService financeSubjectService;
	
	@Autowired
	private FinancePaymentWayService financePaymentWayService;
	
	@Autowired
	private AttachmentService attachmentService;
	
	/**
	 * 跳转到付款单详细信息页面
	 * @param paymentId
	 * @return
	 */
	@RequestMapping("/toPaymentDetailPage")
	public ModelAndView toPaymentDetailPage(String paymentId,Boolean isContractToPaid) {
		ModelAndView mv = new ModelAndView("/finance/getcost/paymentDetailInfo");
		mv.addObject("paymentId", paymentId);
		if(isContractToPaid!=null){
			mv.addObject("isContractToPaid", isContractToPaid);
		}
		return mv;
	}
	
	/**
	 * 跳转到打印付款单页面
	 * @param paymentIds	多个用逗号隔开
	 * @return
	 */
	@RequestMapping("/toPrintPaymentInfoPage")
	public ModelAndView toPrintPaymentInfoPage(String paymentIds, Boolean needClosePage, Boolean needBacktoPage) {
		ModelAndView mv = new ModelAndView("/finance/getcost/printPaymentInfo");
		
		if (needClosePage == null) {
			needClosePage = false;
		}
		
		mv.addObject("paymentIds", paymentIds);
		mv.addObject("needClosePage", needClosePage);
		mv.addObject("needBacktoPage", needBacktoPage);
		return mv;
	}
	
	/**
	 * 获取最新的票据编号
	 * @param request
	 * @param hasReceipt	是否有票
	 * @param paymentDate	付款日期
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryNewReceiptNo")
	public Map<String, Object> queryNewReceiptNo(HttpServletRequest request, Boolean hasReceipt, String paymentDate, String originalReceipNo, Boolean dateChangedFlag, Boolean hasReceiptChangeFlag) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	if (dateChangedFlag == null) {
        		dateChangedFlag = true;
        	}
        	if (hasReceiptChangeFlag == null) {
        		hasReceiptChangeFlag = true;
        	}
        	if (!StringUtils.isBlank(originalReceipNo)) {
        		originalReceipNo = originalReceipNo.replace("-", "");
        	}
        	
        	String newReceiptNo = this.paymentInfoService.getNewReceiptNo(crewId, hasReceipt, paymentDate, originalReceipNo, dateChangedFlag, hasReceiptChangeFlag);
        	newReceiptNo = this.formatReceiptNo(newReceiptNo);
        	
        	resultMap.put("newReceiptNo", newReceiptNo);
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
	 * 格式化付款单编号
	 * @param receiptNo
	 * @return
	 */
	private String formatReceiptNo(String receiptNo) {
    	
    	String newReceiptNo = receiptNo.substring(0, 2) + "-" + receiptNo.substring(2, 4) + "-" + receiptNo.substring(4, receiptNo.length());
    	
    	return newReceiptNo;
	}
	
	/**
	 * 保存付款单信息
	 * @param request
	 * @param paymentId	付款单ID
	 * @param receiptNo	票据编号
	 * @param paymentDate	付款日期
	 * @param payeeName	收款人
	 * @param contractId	合同ID
	 * @param contractType	合同类型:1-职员  2-演员  3-制作
	 * @param loanId 借款单ID，多个ID用逗号隔开
	 * @param currencyId	货币ID
	 * @param totalMoney	总金额
	 * @param paymentWay	付款方式
	 * @param hasReceipt	是否有发票
	 * @param billCount	单据张数
	 * @param agent	记账人
	 * @param status	状态：0-未结算  1-已结算
	 * @param ifReceiveBill	是否收到发票
	 * @param billType	票据种类：1-普通发票   2-增值税发票
	 * @param remindTime	没有收到发票时提醒时间
	 * @param paymentSubjMapStr 和财务科目关联情况，格式：摘要##财务科目ID##财务科目名称##金额，多个以&&隔开
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/savePaymentInfo")
	public Map<String, Object> savePaymentInfo(HttpServletRequest request, String paymentId,
			String paymentDate, String payeeName, String contractId, Integer contractType, String loanIds,
			String currencyId, Double totalMoney, String paymentWay, Boolean hasReceipt, 
			Integer billCount, String agent, Integer status, Boolean ifReceiveBill, Integer billType, 
			String remindTime, String paymentSubjMapStr,String contractPartId, String attpacketId, String department) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		String receiptNo = "";
		try {
			//基本非空校验
			if (StringUtils.isBlank(paymentDate)) {
				throw new IllegalArgumentException("请填写付款日期");
			}
			if (StringUtils.isBlank(payeeName)) {
				throw new IllegalArgumentException("请填写收款人");
			}
			if (StringUtils.isBlank(currencyId)) {
				throw new IllegalArgumentException("请选择币种");
			}
			if (totalMoney == null) {
				throw new IllegalArgumentException("请填写金额");
			}
			if (StringUtils.isBlank(paymentWay)) {
				throw new IllegalArgumentException("请填写付款方式");
			}
			if (hasReceipt == null) {
				throw new IllegalArgumentException("请选择有无发票");
			}
			if (billCount == null) {
				throw new IllegalArgumentException("请填写附单据张数");
			}
			if (StringUtils.isBlank(agent)) {
				throw new IllegalArgumentException("请填写记账人");
			}
			if (status == null) {
				throw new IllegalArgumentException("请提供结算状态");
			}
			if (StringUtils.isBlank(paymentSubjMapStr)) {
				throw new IllegalArgumentException("请提供财务科目信息");
			}
			if (!StringUtils.isBlank(contractId) && contractType == null) {
				throw new IllegalArgumentException("请提供合同类型");
			}
			
			String crewId = getCrewId(request);
			UserInfoModel loginUserInfo = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);
			String loginUserId = loginUserInfo.getUserId();
			
			//如果关联借款单，则校验币种是否一致
			if (!StringUtils.isBlank(loanIds)) {
				List<LoanInfoModel> loanInfoList = this.loanInfoService.queryByIds(loanIds);
				for (LoanInfoModel loanInfo : loanInfoList) {
					if (!loanInfo.getCurrencyId().equals(currencyId)) {
						throw new IllegalArgumentException("付款单币种必须和关联的借款单币种一致");
					}
					if (!loanInfo.getPayeeName().equals(payeeName)) {
						throw new IllegalArgumentException("收款方和所选借款单中借款人不对应，请检查");
					}
				}
			}
			
			//如果关联合同，则校验币种是否一致
			if (!StringUtils.isBlank(contractId)) {
				String contractCurryId = "";
				if (contractType == ContractType.Actor.getValue()) {
					ContractActorModel contractActor = this.contractActorService.queryById(crewId, contractId);
					contractCurryId = contractActor.getCurrencyId();
				}
				if (contractType == ContractType.Produce.getValue()) {
					ContractProduceModel contractProduce = this.contractProduceService.queryById(crewId, contractId);
					contractCurryId = contractProduce.getCurrencyId();	
				}
				if (contractType == ContractType.Worker.getValue()) {
					ContractWorkerModel contractWorker = this.contractWorkerService.queryById(crewId, contractId);
					contractCurryId = contractWorker.getCurrencyId();
				}
				
				if (!contractCurryId.equals(currencyId)) {
					throw new IllegalArgumentException("付款单币种必须和关联的合同币种一致");
				}
			}
			
			PaymentInfoModel paymentInfo = this.paymentInfoService.queryById(paymentId);
			//判断是否需要重新保存还借款信息
			String originalLoanIds = "";
			List<PaymentLoanMapModel> paymentLoanMapList = this.paymentLoanMapService.queryByPaymentId(paymentId);
			for (PaymentLoanMapModel paymentLoanMap : paymentLoanMapList) {
				String loanId = paymentLoanMap.getLoanId();
				originalLoanIds += loanId + ",";
			}
			if (!StringUtils.isBlank(originalLoanIds)) {
				originalLoanIds = originalLoanIds.substring(0, originalLoanIds.length() - 1);
			}
			
			boolean needSaveLoanInfo = false;
			if ((paymentInfo != null && !paymentInfo.getTotalMoney().equals(totalMoney)) ||!originalLoanIds.equals(loanIds)) {
				this.paymentLoanMapService.deleteByPaymentId(crewId, paymentId);
				needSaveLoanInfo = true;
			} else {
				needSaveLoanInfo = false;
			}
			
			
			//重新生成票据编号
			boolean dateChangedFlag = false;	//票据日期的月份是否改变
			boolean hasReceiptChangeFlag = false;	//有票无票是否改变
			String originalReceipNo = "";
			if (paymentInfo != null) {
				originalReceipNo = paymentInfo.getReceiptNo();
				if (!this.sdf1.format(paymentInfo.getPaymentDate()).substring(0, 7).equals(paymentDate.substring(0, 7))) {
					dateChangedFlag = true;
				}
				if (!paymentInfo.getHasReceipt().equals(hasReceipt)) {
					hasReceiptChangeFlag = true;
				}
			}
			receiptNo = this.paymentInfoService.getNewReceiptNo(crewId, hasReceipt, paymentDate, originalReceipNo, dateChangedFlag, hasReceiptChangeFlag);

			//保存付款单信息
			PaymentInfoModel savedPaymentInfo = this.paymentInfoService.savePaymentInfo(crewId, loginUserId, paymentId, receiptNo, paymentDate, payeeName, contractId, 
					contractType, loanIds, currencyId, totalMoney, 
					paymentWay, hasReceipt, billCount, agent, 
					status, ifReceiveBill, billType, remindTime, paymentSubjMapStr,contractPartId, needSaveLoanInfo, attpacketId, department);
			boolean flag = false;
			if(StringUtils.isNotBlank(paymentId)) {
				flag = true;
			}
			
			attpacketId = savedPaymentInfo.getAttpackId();
			paymentId = savedPaymentInfo.getPaymentId();
			
			resultMap.put("attpacketId", attpacketId);
			resultMap.put("paymentId", paymentId);
			
			if (!flag) {
				this.sysLogService.saveSysLog(request, "新增付款单信息-" + (status == 1 ? "已结算" : "未结算"), Constants.TERMINAL_PC, PaymentInfoModel.TABLE_NAME, receiptNo, SysLogOperType.INSERT.getValue());
			} else {
				this.sysLogService.saveSysLog(request, "修改付款单信息-" + (status == 1 ? "已结算" : "未结算"), Constants.TERMINAL_PC, PaymentInfoModel.TABLE_NAME, receiptNo, SysLogOperType.UPDATE.getValue());
			}
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (ParseException pe) {
			success = false;
			message = "日期格式错误";
			
			logger.error(message, pe);
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "保存付款单信息-" + (status == 1 ? "已结算" : "未结算") + "失败：" + e.getMessage(), 
					Constants.TERMINAL_PC, PaymentInfoModel.TABLE_NAME, receiptNo, SysLogOperType.ERROR.getValue());
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	
	/**
	 * 查询付款单列表信息
	 * 该查询结合预算科目表、会计科目表，查询付款单对应的预算科目、以及预算科目对应的会计科目
	 * 如果一张付款单中有两个预算科目，则将会返回两条该付款单记录
	 * 如果付款单中的预算科目没有分配到会计科目，仍然会返回该条付款单记录
	 * 
	 * 查询条件
	 * @param request
	 * @param paymentDates	付款单票据日期
	 * @param accSubjectCodes	会计科目ID，多个用逗号隔开
	 * @param finaSubjIds	财务科目ID，多个用逗号隔开
	 * @param payeeNames	收款人名称，多个用逗号隔开
	 * @param summary	摘要
	 * @param minMoney	金额范围中最小金额
	 * @param maxMoney	金额范围中最大金额
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryWithAccSubjAndFinaSubjInfo")
	public Map<String, Object> queryWithAccSubjAndFinaSubjInfo(HttpServletRequest request, String paymentDates,
			String accSubjectCodes, String finaSubjIds, String payeeNames, String summary, Double minMoney, Double maxMoney) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			String crewId = getCrewId(request);

			List<Map<String, Object>> paymentList = this.paymentInfoService.queryWithAccSubjAndFinaSubjInfo(crewId, paymentDates,
							accSubjectCodes, finaSubjIds, payeeNames, 
							summary, minMoney, maxMoney);

			resultMap.put("paymentList", paymentList);
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
	 * 导出付款单列表信息
	 * 该查询结合预算科目表、会计科目表，查询付款单对应的预算科目、以及预算科目对应的会计科目
	 * 如果一张付款单中有两个预算科目，则将会返回两条该付款单记录
	 * 如果付款单中的预算科目没有分配到会计科目，仍然会返回该条付款单记录
	 * 
	 * 查询条件
	 * @param request
	 * @param paymentDates	付款单票据日期
	 * @param accSubjectCodes	会计科目ID，多个用逗号隔开
	 * @param finaSubjIds	财务科目ID，多个用逗号隔开
	 * @param payeeNames	收款人名称，多个用逗号隔开
	 * @param summary	摘要
	 * @param startMoney	金额范围中最小金额
	 * @param endMoney	金额范围中最大金额
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exportWithAccSubj")
	public Map<String, Object> exportWithAccSubj(HttpServletRequest request, HttpServletResponse response, String paymentDates,
			String accSubjectCodes, String finaSubjIds, String payeeNames, String summary, Double startMoney, Double endMoney) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			String crewId = crewInfo.getCrewId();
			String crewName = crewInfo.getCrewName();
			
			DecimalFormat df = new DecimalFormat("#,###.####");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			
			Map<String, Object> data = new HashMap<String, Object>();
			List<Map<String, Object>> accSubjList = this.paymentInfoService.queryWithAccSubjAndFinaSubjInfo(crewId, paymentDates,
							accSubjectCodes, finaSubjIds, payeeNames, summary,
							startMoney, endMoney);
			
			if (accSubjList == null || accSubjList.size() == 0) {
				throw new IllegalArgumentException("暂无数据可导出");
			}
			
			for (Map<String, Object> map : accSubjList) {
				
				String currencyCode = (String) map.get("currencyCode");
				Date paymentDate = null;
				if (map.get("paymentDate") != null) {
					paymentDate = (Date) map.get("paymentDate");
				}
				
				map.remove("paymentDate");
				map.put("paymentDate", sdf.format(paymentDate));
				double money = (Double) map.get("money");
				
				map.put("money", df.format(money) + "(" + currencyCode + ")");
				
				if (map.get("billType") != null) {
					int billType = (Integer) map.get("billType");
					if (billType == 1) {
						map.put("billType", "普通发票");
					}
					if (billType == 2) {
						map.put("billType", "增值税发票");
					}
				}
			}
			
			data.put("accSubjList", accSubjList);
			
			Properties property = PropertiesUitls.fetchProperties("/config.properties");
	  		String srcfilePath = property.getProperty("kjkm_template");
	  		String downloadPath = property.getProperty("downloadPath") + "《" + crewName + "》" + "会计科目支出表_"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".xls";
	  		File pathFile = new File(property.getProperty("downloadPath"));
	  		if(!pathFile.isDirectory()){
	  			pathFile.mkdirs();
	  		}
	  		
	  		ExportExcelUtil.exportViewToExcelTemplate(srcfilePath, data, downloadPath);
	  		
	  		
	  		resultMap.put("downloadPath", downloadPath);
	  		this.sysLogService.saveSysLog(request, "导出会计科目支出表", Constants.TERMINAL_PC, PaymentInfoModel.TABLE_NAME, null, SysLogOperType.EXPORT.getValue());
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error(message, e);
	  		this.sysLogService.saveSysLog(request, "导出会计科目支出表失败：" + e.getMessage(), Constants.TERMINAL_PC, PaymentInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	
	/**
	 * 查询剧组中所有付款单
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryPaymentList")
	public Map<String, Object> queryPaymentList(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			String crewId = getCrewId(request);
			
			List<PaymentInfoModel> paymentList = this.paymentInfoService.queryByCrewId(crewId);
			
			resultMap.put("paymentList", paymentList);
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
	 * 查询付款单详细信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryPaymentDetail")
	public Map<String, Object> queryPaymentDetail(HttpServletRequest request, String paymentId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(paymentId)) {
				throw new IllegalArgumentException("请提供付款单ID");
			}
			String crewId = this.getCrewId(request);
			this.financeSubjectService.refreshCachedSubjectList(crewId);
			
			PaymentInfoModel paymentInfo = this.paymentInfoService.queryById(paymentId);
			
			//付款单基本信息
			Map<String, Object> paymentInfoMap = new HashMap<String, Object>();
			paymentInfoMap.put("paymentId", paymentInfo.getPaymentId());
			paymentInfoMap.put("receiptNo", this.formatReceiptNo(paymentInfo.getReceiptNo()));
			paymentInfoMap.put("paymentDate", this.sdf1.format(paymentInfo.getPaymentDate()));
			paymentInfoMap.put("payeeName", paymentInfo.getPayeeName());
			String contractId = paymentInfo.getContractId();
			Integer contractType = paymentInfo.getContractType();
			
			paymentInfoMap.put("contractId", contractId);
			paymentInfoMap.put("contractType", contractType);
			paymentInfoMap.put("totalMoney", paymentInfo.getTotalMoney());
			paymentInfoMap.put("hasReceipt", paymentInfo.getHasReceipt());
			paymentInfoMap.put("billCount", paymentInfo.getBillCount());
			paymentInfoMap.put("department", paymentInfo.getDepartment());
			String contractNo = "";
			String aimPeopleName = "";
			if(StringUtils.isNotBlank(contractId)){
				if (contractType == ContractType.Actor.getValue()) {
					ContractActorModel contractActor = this.contractActorService.queryById(crewId, contractId);
					contractNo = contractActor.getContractNo();
					aimPeopleName = contractActor.getActorName();
				} else if (contractType == ContractType.Worker.getValue()) {
					ContractWorkerModel contractWorker = this.contractWorkerService.queryById(crewId, contractId);
					contractNo = contractWorker.getContractNo();
					aimPeopleName = contractWorker.getWorkerName();
				} else if (contractType == ContractType.Produce.getValue()) {
					ContractProduceModel contractProduce = this.contractProduceService.queryById(crewId, contractId);
					contractNo = contractProduce.getContractNo();
					aimPeopleName = contractProduce.getCompany();
				}
			}
			
			
			FinancePaymentWayModel paymentWayModel = this.financePaymentWayService.queryById(crewId, paymentInfo.getPaymentWay());
			paymentInfoMap.put("paymentWay", paymentWayModel.getWayName());
			
			paymentInfoMap.put("agent", paymentInfo.getAgent());
			paymentInfoMap.put("billType", paymentInfo.getBillType());
			paymentInfoMap.put("ifReceiveBill", paymentInfo.getIfReceiveBill());
			paymentInfoMap.put("currencyId", paymentInfo.getCurrencyId());
			paymentInfoMap.put("status", paymentInfo.getStatus());
			String remindTime = "";
			if (paymentInfo.getRemindTime() != null) {
				remindTime = this.sdf1.format(paymentInfo.getRemindTime());
			}
			paymentInfoMap.put("remindTime", remindTime);
			
			
			//付款单关联的合同信息
			String contractFinanSubjId = "";
			if (!StringUtils.isBlank(contractId) && contractType != null) {
				if (contractType == ContractType.Worker.getValue()) {
					ContractWorkerModel contract = this.contractWorkerService.queryById(crewId, contractId);
					if (!StringUtils.isBlank(this.financeSubjectService.getFinanceSubjName(contract.getFinanceSubjId()))) {
						contractFinanSubjId = contract.getFinanceSubjId();
					}
				}
				if (contractType == ContractType.Actor.getValue()) {
					ContractActorModel contract = this.contractActorService.queryById(crewId, contractId);
					if (!StringUtils.isBlank(this.financeSubjectService.getFinanceSubjName(contract.getFinanceSubjId()))) {
						contractFinanSubjId = contract.getFinanceSubjId();
					}
				}
				if (contractType == ContractType.Produce.getValue()) {
					ContractProduceModel contract = this.contractProduceService.queryById(crewId, contractId);
					if (!StringUtils.isBlank(this.financeSubjectService.getFinanceSubjName(contract.getFinanceSubjId()))) {
						contractFinanSubjId = contract.getFinanceSubjId();
					}
				}
			}
			paymentInfoMap.put("contractFinanSubjId", contractFinanSubjId);
			
			
			//付款单关联的借款
			String loanIds = "";
			List<PaymentLoanMapModel> paymentLoanMapList = this.paymentLoanMapService.queryByPaymentId(paymentId);
			for (PaymentLoanMapModel paymentLoanMap : paymentLoanMapList) {
				String loanId = paymentLoanMap.getLoanId();
				loanIds += loanId + ",";
			}
			if (!StringUtils.isBlank(loanIds)) {
				loanIds = loanIds.substring(0, loanIds.length() - 1);
			}
			paymentInfoMap.put("loanIds", loanIds);
			
			
			//付款单和财务科目的关联信息
			this.financeSubjectService.refreshCachedSubjectList(crewId);
			List<PaymentFinanSubjMapModel> paymentFinanSubjMapList = this.paymentFinanSubjMapService.queryByPaymentId(paymentId);
			for (PaymentFinanSubjMapModel paymentFinanSubjMap : paymentFinanSubjMapList) {
				String financeSubjId = paymentFinanSubjMap.getFinanceSubjId();
				String financeSubjName = paymentFinanSubjMap.getFinanceSubjName();
				
				//由于financeSubjName是新添字段，因此存在老数据未赋值的情况，此处兼容一下
				if (!StringUtils.isBlank(financeSubjId)) {
					financeSubjName = this.financeSubjectService.getFinanceSubjName(financeSubjId);
					paymentFinanSubjMap.setFinanceSubjName(financeSubjName);
				}
			}
			
			//查询付款单的附件信息列表
			List<AttachmentModel> attachmentList = this.attachmentService.queryAttByPackId(paymentInfo.getAttpackId());
			
			resultMap.put("attachmentList", attachmentList);
			resultMap.put("attachmentPacketId", paymentInfo.getAttpackId());
			resultMap.put("paymentInfo", paymentInfoMap);
			resultMap.put("paymentFinanSubjMapList", paymentFinanSubjMapList);
			resultMap.put("contractNo", contractNo);
			resultMap.put("aimPeopleName", aimPeopleName);
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
	 * 查询多个付款单的详细信息(打印用)
	 * 该查询方法返回的币种是字符串文本值
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryManyPaymentDetail")
	public Map<String, Object> queryManyPaymentDetail(HttpServletRequest request, String paymentIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(paymentIds)) {
				throw new IllegalArgumentException("请提供付款单ID");
			}
			String crewId = this.getCrewId(request);
			
			PaymentInfoFilter filter = new PaymentInfoFilter();
			filter.setPaymentIds(paymentIds);
			List<Map<String, Object>> paymentInfoList = this.paymentInfoService.queryPaymentList(crewId, filter);
			
			
			this.financeSubjectService.refreshCachedSubjectList(crewId);
			for (Map<String, Object> map : paymentInfoList) {
				String paymentId = (String) map.get("paymentId");
				
				//付款单和财务科目的关联信息
				List<PaymentFinanSubjMapModel> paymentFinanSubjMapList = this.paymentFinanSubjMapService.queryByPaymentId(paymentId);
				for (PaymentFinanSubjMapModel paymentInfo : paymentFinanSubjMapList) {
					String financeSubjId = paymentInfo.getFinanceSubjId();
					String financeSubjName = paymentInfo.getFinanceSubjName();
					
					//由于financeSubjName是新添字段，因此存在老数据未赋值的情况，此处兼容一下
					if (!StringUtils.isBlank(financeSubjId)) {
						financeSubjName = this.financeSubjectService.getFinanceSubjName(financeSubjId);
						paymentInfo.setFinanceSubjName(financeSubjName);
					}
				}
				
				map.put("paymentFinanSubjMapList", paymentFinanSubjMapList);
			}
			
			resultMap.put("paymentInfoList", paymentInfoList);
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
	 * 批量结算付款单
	 * @param request
	 * @param paymentIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/settlePaymentBatch")
	public Map<String, Object> settlePaymentBatch (HttpServletRequest request, String paymentIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(paymentIds)) {
				throw new IllegalArgumentException("请提供付款单ID");
			}
			this.paymentInfoService.settleBatchPaymentList(paymentIds);			
			
			this.sysLogService.saveSysLog(request, "批量结算付款单(" + paymentIds.split(",").length + ")", Constants.TERMINAL_PC, 
					PaymentInfoModel.TABLE_NAME + "," + ContractToPaidModel.TABLE_NAME, paymentIds, 2);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "批量结算付款单(" + paymentIds.split(",").length + ")失败：" + e.getMessage(), Constants.TERMINAL_PC, 
					PaymentInfoModel.TABLE_NAME + "," + ContractToPaidModel.TABLE_NAME, paymentIds, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 付款单批量无票变有票
	 * @param request
	 * @param paymentIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/setPaymentHasReceiptBatch")
	public Map<String, Object> setPaymentHasReceiptBatch(HttpServletRequest request, String paymentIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(paymentIds)) {
				throw new IllegalArgumentException("请提供付款单ID");
			}
			
			String crewId = this.getCrewId(request);
			this.paymentInfoService.setPaymentHasReceiptBatch(crewId, paymentIds);
			
			this.sysLogService.saveSysLog(request, "付款单批量无票改有票(" + paymentIds.split(",").length + ")", 
					Constants.TERMINAL_PC, PaymentInfoModel.TABLE_NAME, paymentIds, 2);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "付款单批量无票改有票(" + paymentIds.split(",").length + ")失败：" + e.getMessage(), 
					Constants.TERMINAL_PC, PaymentInfoModel.TABLE_NAME, paymentIds, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 根据借款单ID查询付款单信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryPaymentByLoanId")
	public Map<String, Object> queryPaymentByLoanId(HttpServletRequest request, String loanId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(loanId)) {
				throw new IllegalArgumentException("请提供借款单ID");
			}
			
			List<Map<String, Object>>  paymentList = this.paymentInfoService.queryByLoanId(loanId);
			
			resultMap.put("paymentList", paymentList);
			this.sysLogService.saveSysLog(request, "删除借款单信息", Constants.TERMINAL_PC, LoanInfoModel.TABLE_NAME, loanId, SysLogOperType.DELETE.getValue());
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "删除借款单信息失败：" + e.getMessage(), Constants.TERMINAL_PC, LoanInfoModel.TABLE_NAME, loanId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 删除付款单信息
	 * @param request
	 * @param paymentId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deletePaymentInfo")
	public Map<String, Object> deletePaymentInfo(HttpServletRequest request, String paymentId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(paymentId)) {
				throw new IllegalArgumentException("请提供付款单ID");
			}
			//校验付款单是否已结算
			PaymentInfoModel paymentInfo = this.paymentInfoService.queryById(paymentId);
			if (paymentInfo.getStatus() == PaymentStatus.Settled.getValue()) {
				throw new IllegalArgumentException("该付款单已结算，不可以删除");
			}
			
			String crewId = this.getCrewId(request);
			this.paymentInfoService.deletePaymentInfo(crewId, paymentId);
			this.sysLogService.saveSysLog(request, "删除付款单信息", Constants.TERMINAL_PC, PaymentInfoModel.TABLE_NAME, paymentId, SysLogOperType.DELETE.getValue());
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "删除付款单信息失败：" + e.getMessage(), Constants.TERMINAL_PC, PaymentInfoModel.TABLE_NAME, paymentId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 校验修改付款单时用户是否修改了收款方或金额
	 * @param request
	 * @param paymentId
	 * @param payeeName
	 * @param totalMoney
	 * @return
	 */
//	@ResponseBody
//	@RequestMapping("/checkIsChanged")
//	public Map<String, Object> checkIsChanged(HttpServletRequest request, String paymentId, String payeeName, Double totalMoney) {
//		Map<String, Object> resultMap = new HashMap<String, Object>();
//		
//		boolean success = true;
//		String message = "";
//		try {
//			boolean changed = false;
//			DecimalFormat df = new DecimalFormat("###,##0.00");
//			if (!StringUtils.isBlank(paymentId)) {
//				PaymentInfoModel paymentInfo = this.paymentInfoService.queryById(paymentId);
//				String myPayeeName = paymentInfo.getPayeeName();
//				Double myTotalMoney = paymentInfo.getTotalMoney();
//				
//				if (!myPayeeName.equals(payeeName) || !myTotalMoney.equals(totalMoney)) {
//					List<Map<String, Object>> loanerPayInfoList = this.loanInfoService.queryPaymentLoanInfo(paymentId);
//					String alertMessage = "该付款单已还";
//					for (Map<String, Object> map : loanerPayInfoList) {
//						String loaner = (String) map.get("payeeName");
//						Double forLoanMoney = Double.parseDouble(map.get("forLoanMoney").toString());
//						alertMessage += loaner + df.format(forLoanMoney) + "借款,";
//					}
//					if (loanerPayInfoList != null && loanerPayInfoList.size() > 0) {
//						changed = true;
//					} else {
//						alertMessage = null;
//					}
//					
//					resultMap.put("alertMessage", alertMessage);
//				}
//			}
//			resultMap.put("changed", changed);
//		} catch (IllegalArgumentException ie) {
//			success = false;
//			message = ie.getMessage();
//		} catch (Exception e) {
//			success = false;
//			message = "未知异常";
//			
//			logger.error("未知异常", e);
//		}
//		
//		resultMap.put("success", success);
//		resultMap.put("message", message);
//		return resultMap;
//	}
	

	/**
	 * 删除和借款单的关联
	 * @param request
	 * @param paymentId
	 * @param payeeName
	 * @param totalMoney
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteLoanRelate")
	public Map<String, Object> deleteLoanRelate(HttpServletRequest request, String paymentId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			String crewId = this.getCrewId(request);
			this.paymentLoanMapService.deleteByPaymentId(crewId, paymentId);
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
	
}
