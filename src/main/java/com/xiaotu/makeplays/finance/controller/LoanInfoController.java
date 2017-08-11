package com.xiaotu.makeplays.finance.controller;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.attachment.model.AttachmentModel;
import com.xiaotu.makeplays.attachment.service.AttachmentService;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.finance.controller.filter.LoanInfoFilter;
import com.xiaotu.makeplays.finance.controller.filter.PaymentInfoFilter;
import com.xiaotu.makeplays.finance.model.CurrencyInfoModel;
import com.xiaotu.makeplays.finance.model.LoanInfoModel;
import com.xiaotu.makeplays.finance.model.PaymentInfoModel;
import com.xiaotu.makeplays.finance.model.PaymentLoanMapModel;
import com.xiaotu.makeplays.finance.model.constants.LoanPaymentWay;
import com.xiaotu.makeplays.finance.service.CurrencyInfoService;
import com.xiaotu.makeplays.finance.service.FinanceSettingService;
import com.xiaotu.makeplays.finance.service.FinanceSubjectService;
import com.xiaotu.makeplays.finance.service.LoanInfoService;
import com.xiaotu.makeplays.finance.service.PaymentInfoService;
import com.xiaotu.makeplays.finance.service.PaymentLoanMapService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.ExportExcelUtil;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.PropertiesUitls;

/**
 * 借款信息
 * @author xuchangjian 2016-8-17下午7:13:09
 */
@Controller
@RequestMapping("/loanInfoManager")
public class LoanInfoController extends BaseController {
	
	Logger logger = LoggerFactory.getLogger(LoanInfoController.class);
	
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	
	DecimalFormat df = new DecimalFormat("#,##0.00");

	@Autowired
	private LoanInfoService loanInfoService;
	
	@Autowired
	private CurrencyInfoService currencyInfoService;
	
	@Autowired
	private FinanceSettingService financeSettingService;
	
	@Autowired
	private PaymentLoanMapService paymentLoanMapService;
	
	@Autowired
	private FinanceSubjectService financeSubjectService;
	
	@Autowired
	private PaymentInfoService paymentInfoService;
	
	@Autowired
	private AttachmentService attachmentService;
	
	/**
	 * 跳转到借款单列表页面
	 * @return
	 */
	@RequestMapping("/toLoanListPage")
	public ModelAndView toLoanListPage() {
		ModelAndView mv = new ModelAndView("/finance/getcost/loanList");
		return mv;
	}
	
	/**
	 * 跳转到借款单详细信息页面
	 * @param loanId
	 * @return
	 */
	@RequestMapping("/toLoanDetailInfoPage")
	public ModelAndView toLoanDetailInfoPage(String loanId) {
		ModelAndView mv = new ModelAndView("/finance/getcost/loanDetailInfo");
		mv.addObject("loanId", loanId);
		return mv;
	}
	
	/**
	 * 跳转到打印借款单页面
	 * @param loanIds
	 * @return
	 */
	@RequestMapping("/toPrintLoanInfoPage")
	public ModelAndView toPrintLoanInfoPage(String loanIds, Boolean needClosePage, Boolean needBacktoPage) {
		ModelAndView mv = new ModelAndView("/finance/getcost/printLoanInfo");
		
		if (needClosePage == null) {
			needClosePage = false;
		}
		
		mv.addObject("loanIds", loanIds);
		mv.addObject("needClosePage", needClosePage);
		mv.addObject("needBacktoPage", needBacktoPage);
		return mv;
	}
	
	/**
	 * 根据借款人名称查询对应的借款余额
	 * @param request
	 * @param payeeName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryLoanLeftMoney")
	public Map<String, Object> queryLoanLeftMoney(HttpServletRequest request, String payeeName, String paymentId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	if (StringUtils.isBlank(payeeName)) {
        		throw new IllegalArgumentException("请提供借款人名称");
        	}
        	
        	String crewId = this.getCrewId(request);

        		
        	PaymentInfoModel paymentInfo = this.paymentInfoService.queryById(paymentId);
        	if (paymentInfo != null && !paymentInfo.getPayeeName().equals(payeeName)) {
        		paymentId = null;
        	}
        	
        	//剧组中的本位币
        	CurrencyInfoModel currencyInfo = this.currencyInfoService.queryStandardCurrency(crewId);
        	boolean  singleCurrencyFlag= false;	//标识剧组中是否只有一个有效币种
			Map<String, Object> conditionMap = new HashMap<String, Object>();
        	conditionMap.put("crewId", crewId);
        	conditionMap.put("ifEnable", true);
        	List<CurrencyInfoModel> currencyInfoList = this.currencyInfoService.queryManyByMutiCondition(conditionMap, null);
			if (currencyInfoList.size() == 1) {
				singleCurrencyFlag = true;
			}
			
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
        	
			//查询借款人当前借款余额
        	String loanLeftMoneyStr = this.genLoanLeftMoney(payeeName, crewId, currencyInfo, singleCurrencyFlag);
        	//查询付款单已付该借款人金额
        	String forLoanMoneyStr = this.genForLoanMoney(payeeName, paymentId, crewId, currencyInfo, singleCurrencyFlag);
        	//计算补领的金额
        	String payLeftMoneyStr = this.genPayLeftMoney(payeeName, paymentId, crewId, currencyInfo, singleCurrencyFlag);
        	
        	if (!StringUtils.isBlank(forLoanMoneyStr) && StringUtils.isBlank(loanLeftMoneyStr)) {
        		loanLeftMoneyStr = "借款余额：0.00";
        	}
        	
        	resultMap.put("loanLeftMoney", loanLeftMoneyStr);
        	resultMap.put("forLoanMoney", forLoanMoneyStr);
        	resultMap.put("payLeftMoney", payLeftMoneyStr);
        	resultMap.put("loanIds", loanIds);
        	
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
	 * 获取某个人的借款未还金额
	 * @param payeeName	借款人名称
	 * @param crewId	剧组ID
	 * @param currencyInfo	本位币信息
	 * @param singleCurrencyFlag	是否单币种标识
	 * @return
	 */
	private String genLoanLeftMoney(String payeeName, String crewId,
			CurrencyInfoModel currencyInfo, boolean singleCurrencyFlag) {
		List<Map<String, Object>> loanInfoList = this.loanInfoService.queryLoanWithPaymentInfo(crewId, payeeName, null, null, null, null, null,null);
		
		//过滤掉已经结算完的付款单
		Map<String, Double> currencyLoanMap = new HashMap<String, Double>();	//每个货币对应的借款余额,key为货币ID，value为未还金额
		Double standardLoanMoney = 0.0;	//折合成本位币的借款余额
		Map<String, String> currencyIdCodeMap = new HashMap<String, String>();	//货币ID和货币编码对应的Map
		
		for (Map<String, Object> loanInfo : loanInfoList) {
			Double money = (Double) loanInfo.get("money");	//借款金额
			Double leftMoney = (Double) loanInfo.get("leftMoney");	//剩余还款金额
			loanInfo.put("leftMoney", leftMoney);
			
			String currencyId = (String) loanInfo.get("currencyId");
			String currencyCode = (String) loanInfo.get("currencyCode");
			Double exchangeRate = (Double) loanInfo.get("exchangeRate");
			
			if (!currencyIdCodeMap.containsKey(currencyId)) {
				currencyIdCodeMap.put(currencyId, currencyCode);
			}
			
			//借款单尚未还的金额
			if (leftMoney > 0) {
				if (currencyLoanMap.containsKey(currencyId)) {
					currencyLoanMap.put(currencyId, BigDecimalUtil.add(leftMoney, currencyLoanMap.get(currencyId)));
				} else {
					currencyLoanMap.put(currencyId, leftMoney);
				}
				
				standardLoanMoney = BigDecimalUtil.add(standardLoanMoney, BigDecimalUtil.multiply(leftMoney, exchangeRate));
			}
		}
		
		
		//把几种币种的借款余额用+链接
		String loanLeftMoneyStr = "";
		DecimalFormat df = new DecimalFormat("#,###,###.00");
		
		if (currencyLoanMap.size() > 0) {
			if (!singleCurrencyFlag) {	//多币种的情况
				Set<String> currencyIdSet = currencyLoanMap.keySet();
		    	for (String currencyId : currencyIdSet) {
		    		String currencyCode = currencyIdCodeMap.get(currencyId);
		    		Double leftMoney = currencyLoanMap.get(currencyId);
		    		
		    		loanLeftMoneyStr += df.format(leftMoney) + "(" + currencyCode + ")" + "+";
		    	}
		    	//拼接最后的结果
		    	String standardLoanMoneyStr = df.format(standardLoanMoney) + "(" + currencyInfo.getCode() + ")";
		    	if (!StringUtils.isBlank(loanLeftMoneyStr)) {
		    		loanLeftMoneyStr = loanLeftMoneyStr.substring(0, loanLeftMoneyStr.length() - 1);
		    		if (!loanLeftMoneyStr.equals(standardLoanMoneyStr)) {
		    			loanLeftMoneyStr += "=" + standardLoanMoneyStr;
		    		}
		    		loanLeftMoneyStr = "借款余额：" + loanLeftMoneyStr;
		    	}
			} else {//单币种的情况
				String standardLoanMoneyStr = df.format(standardLoanMoney);
				loanLeftMoneyStr = "借款余额：" + standardLoanMoneyStr;
			}
		}
		return loanLeftMoneyStr;
	}

	/**
	 * 获取一张付款单还了某个人的借款金额
	 * @param payeeName	借款人名称
	 * @param paymentId	付款单ID
	 * @param crewId	剧组ID
	 * @param currencyInfo	本位币信息
	 * @param singleCurrencyFlag	是否单币种标识
	 * @return
	 */
	private String genForLoanMoney(String payeeName, String paymentId,
			String crewId, CurrencyInfoModel currencyInfo,
			boolean singleCurrencyFlag) {
		DecimalFormat df = new DecimalFormat("#,###,###.00");
		
		String forLoanMoneyStr = "";
		if (!StringUtils.isBlank(paymentId)) {
			PaymentInfoFilter paymentInfoFilter = new PaymentInfoFilter();
			paymentInfoFilter.setLoanerName(payeeName);
			paymentInfoFilter.setPaymentIds(paymentId);
			List<Map<String, Object>> paymentStatisticInfo = this.paymentInfoService.queryPaymentStatistic(crewId, paymentInfoFilter);
			
			Double standardPayedMoney = 0.0;
			for (Map<String, Object> paymentStatistic : paymentStatisticInfo) {
				Double forLoanMoney = (Double) paymentStatistic.get("forLoanMoney");
				String currencyCode = (String) paymentStatistic.get("currencyCode");
				Double exchangeRate = (Double) paymentStatistic.get("exchangeRate");
				
				if (forLoanMoney != null) {
					standardPayedMoney = BigDecimalUtil.add(standardPayedMoney, BigDecimalUtil.multiply(forLoanMoney, exchangeRate));
					forLoanMoneyStr += df.format(forLoanMoney) + "(" + currencyCode + ")" + "+";
				}
			}
			
			if (!StringUtils.isBlank(forLoanMoneyStr)) {
				if (!singleCurrencyFlag) {//多币种
					String standartPayedMoneyStr = df.format(standardPayedMoney) + "(" + currencyInfo.getCode() + ")";
		    		
		    		if (!StringUtils.isBlank(forLoanMoneyStr)) {
		    			forLoanMoneyStr = forLoanMoneyStr.substring(0, forLoanMoneyStr.length() - 1);
		    			if (!forLoanMoneyStr.equals(standartPayedMoneyStr)) {
		    				forLoanMoneyStr += "=" + standartPayedMoneyStr;
		    			}
		    			forLoanMoneyStr = "本次还款：" + forLoanMoneyStr;
		    		}
				} else {//单币种
					forLoanMoneyStr = "本次还款：" + df.format(standardPayedMoney);
				}
			}
		}
		return forLoanMoneyStr;
	}
	
	/**
	 * 获取一张付款单还借款后补领的金额
	 * @param payeeName	借款人名称
	 * @param paymentId	付款单ID
	 * @param crewId	剧组ID
	 * @param currencyInfo	本位币信息
	 * @param singleCurrencyFlag	是否单币种标识
	 * @return
	 */
	private String genPayLeftMoney(String payeeName, String paymentId,
			String crewId, CurrencyInfoModel currencyInfo,
			boolean singleCurrencyFlag) {
		DecimalFormat df = new DecimalFormat("#,###,###.00");
		
		String payLeftMoneyStr = "";
		if (!StringUtils.isBlank(paymentId)) {
			PaymentInfoFilter paymentInfoFilter = new PaymentInfoFilter();
			paymentInfoFilter.setLoanerName(payeeName);
			paymentInfoFilter.setPaymentIds(paymentId);
			List<Map<String, Object>> paymentStatisticInfo = this.paymentInfoService.queryPaymentStatistic(crewId, paymentInfoFilter);
			
			Double standardPayLeftMoney = 0.0;
			for (Map<String, Object> paymentStatistic : paymentStatisticInfo) {
				Double forLoanMoney = (Double) paymentStatistic.get("forLoanMoney");
				Double totalPayedMoney = (Double) paymentStatistic.get("totalPayedMoney");
				String currencyCode = (String) paymentStatistic.get("currencyCode");
				Double exchangeRate = (Double) paymentStatistic.get("exchangeRate");
				
				if (forLoanMoney != null) {
					Double payLeftMoney = BigDecimalUtil.subtract(totalPayedMoney, forLoanMoney);
					if (payLeftMoney == 0) {
						continue;
					}
					standardPayLeftMoney = BigDecimalUtil.add(standardPayLeftMoney, BigDecimalUtil.multiply(payLeftMoney, exchangeRate));
					payLeftMoneyStr += df.format(payLeftMoney) + "(" + currencyCode + ")" + "+";
				}
			}
			
			if (!StringUtils.isBlank(payLeftMoneyStr)) {
				if (!singleCurrencyFlag) {//多币种
					String standartPayedMoneyStr = df.format(standardPayLeftMoney) + "(" + currencyInfo.getCode() + ")";
		    		
		    		if (!StringUtils.isBlank(payLeftMoneyStr)) {
		    			payLeftMoneyStr = payLeftMoneyStr.substring(0, payLeftMoneyStr.length() - 1);
		    			if (!payLeftMoneyStr.equals(standartPayedMoneyStr)) {
		    				payLeftMoneyStr += "=" + standartPayedMoneyStr;
		    			}
		    			payLeftMoneyStr = "补领金额：" + payLeftMoneyStr;
		    		}
				} else {//单币种
					payLeftMoneyStr = "补领金额：" + df.format(standardPayLeftMoney);
				}
			}
		}
		return payLeftMoneyStr;
	}
	
	/**
	 * 根据借款人名称查询对应的借款单列表
	 * 排除掉已经结清的借款单
	 * @param request
	 * @param payeeName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryNotPayedLoanList")
	public Map<String, Object> queryNotPayedLoanList(HttpServletRequest request, String payeeName, String paymentId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	if (StringUtils.isBlank(payeeName)) {
        		throw new IllegalArgumentException("请提供借款人名称");
        	}
        	
        	String crewId = this.getCrewId(request);
        	
        	Map<String, Double> loanIdMoneyMap = new HashMap<String, Double>();	//key为借款单ID，value为该借款单还借款金额
        	if (!StringUtils.isBlank(paymentId)) {
            	List<PaymentLoanMapModel> paymentLoanMapList = this.paymentLoanMapService.queryByPaymentId(paymentId);
            	for (PaymentLoanMapModel paymentLoanMap : paymentLoanMapList) {
            		String loanId = paymentLoanMap.getLoanId();
            		Double loanBalance = paymentLoanMap.getLoanBalance();
            		Double repaymentMoney = paymentLoanMap.getRepaymentMoney();
            		
            		Double forLoanMoney = BigDecimalUtil.subtract(repaymentMoney, loanBalance);
            		loanIdMoneyMap.put(loanId, forLoanMoney);
            	}
        	}
        	
        	List<Map<String, Object>> loanInfoList = this.loanInfoService.queryLoanWithPaymentInfo(crewId, payeeName, null, null, null, null, null, null);
        	
        	this.financeSubjectService.refreshCachedSubjectList(crewId);
        	
        	//过滤掉已经结算完的付款单
//        	List<Map<String, Object>> notPayedLoanInfoList = new ArrayList<Map<String, Object>>();
        	for (Map<String, Object> loanInfo : loanInfoList) {
        		Double money = (Double) loanInfo.get("money");	//借款金额
				Double leftMoney = (Double) loanInfo.get("leftMoney");	//剩余还款金额
				String loanId = (String) loanInfo.get("loanId");
        		String financeSubjId = (String) loanInfo.get("financeSubjId");
        		String financeSubjName = (String) loanInfo.get("financeSubjName");
				
        		Double payedMoney = BigDecimalUtil.subtract(money, leftMoney);	//已还金额
        		loanInfo.put("payedMoney", payedMoney);
        		
        		//借款单尚未还的金额
//        		if (leftMoney > 0) {
//        			notPayedLoanInfoList.add(loanInfo);
//        		}
        		
        		if (!StringUtils.isBlank(financeSubjId)) {
        			financeSubjName = this.financeSubjectService.getFinanceSubjName(financeSubjId);
        			loanInfo.put("financeSubjName", financeSubjName);
        		}
        		
        		if (loanIdMoneyMap.containsKey(loanId)) {
        			loanInfo.put("selected", true);
        			loanInfo.put("myPayedMoney", loanIdMoneyMap.get(loanId));
        		} else {
        			loanInfo.put("selected", false);
        		}
        	}
        	
        	resultMap.put("loanInfoList", loanInfoList);
        	
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
	 * 获取最新的票据编号
	 * @param request
	 * @param loanDate
	 * @param originalReceipNo 原来的编号
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryNewReceiptNo")
	public Map<String, Object> queryNewReceiptNo(HttpServletRequest request, String loanDate, String originalReceipNo) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	if (!StringUtils.isBlank(originalReceipNo)) {
        		originalReceipNo = originalReceipNo.replace("-", "");
        	}
        	String newReceiptNo = this.loanInfoService.getNewReceiptNo(crewId, loanDate, originalReceipNo);
        	
        	resultMap.put("newReceiptNo", this.formatReceiptNo(newReceiptNo));
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
	 * 格式化票据编号
	 * @param receiptNo
	 * @return
	 */
	private String formatReceiptNo(String receiptNo) {
    	String newReceiptNo = receiptNo.substring(0, 2) + "-" + receiptNo.substring(2, 6) + "-" + receiptNo.substring(6, receiptNo.length());
    	
    	return newReceiptNo;
	}
	
	/**
	 * 保存借款单信息
	 * @param request
	 * @param loanId	借款单ID
	 * @param receiptNo	票据编号
	 * @param loanDate	借款日期
	 * @param payeeName	借款人
	 * @param summary	摘要
	 * @param money	金额
	 * @param currencyId	货币ID
	 * @param paymentWay	支付方式：1-现金  2-现金（网转） 3-银行
	 * @param agent	记账人
	 * @param financeSubjId	财务科目ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveLoanInfos")
	public Map<String, Object> saveLoanInfo(HttpServletRequest request, String loanId,
			String loanDate, String payeeName, String summary, Double money, String currencyId, 
			Integer paymentWay, String agent, String financeSubjId, String financeSubjName, String attpacketId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        String receiptNo = "";
        try {
        	String crewId = this.getCrewId(request);
        	
        	if (StringUtils.isBlank(loanDate)) {
        		throw new IllegalArgumentException("请填写借款日期");
        	}
        	if (StringUtils.isBlank(payeeName)) {
        		throw new IllegalArgumentException("请填写借款人");
        	}
        	if (money == null || money == 0.0) {
        		throw new IllegalArgumentException("请填写金额");
        	}
        	if (StringUtils.isBlank(currencyId)) {
        		throw new IllegalArgumentException("请选择币种");
        	}
        	if (paymentWay == null) {
        		throw new IllegalArgumentException("请选择付款方式");
        	}
        	if (StringUtils.isBlank(agent)) {
        		throw new IllegalArgumentException("请填写记账人");
        	}
        	if (!StringUtils.isBlank(summary) && summary.length() > 100) {
        		throw new IllegalArgumentException("摘要需控制在100字以内");
        	}
        	//生成票据编号
        	LoanInfoModel originalLoanInfo = this.loanInfoService.queryById(loanId);
        	
        	//借款单一旦关联了付款单，则不允许修改
        	if (!StringUtils.isBlank(loanId)) {
        		List<PaymentLoanMapModel> mapList = this.paymentLoanMapService.queryByLoanId(crewId, loanId);
        		if ((!originalLoanInfo.getPayeeName().equals(payeeName) || originalLoanInfo.getMoney() != money || 
            			!originalLoanInfo.getCurrencyId().equals(currencyId)) && mapList != null && mapList.size() > 0) {
        			throw new IllegalArgumentException("该借款单已关联付款单，不允许修改借款人、币种、借款金额");
        		}
        	}
        	
        	
        	if (originalLoanInfo != null) {
        		receiptNo = originalLoanInfo.getReceiptNo();
        		if (!this.sdf1.format(originalLoanInfo.getLoanDate()).substring(0,  7).equals(loanDate.substring(0, 7))) {
        			receiptNo = this.loanInfoService.getNewReceiptNo(crewId, loanDate, receiptNo);
        		}
        	} else {
        		receiptNo = this.loanInfoService.getNewReceiptNo(crewId, loanDate, receiptNo);
        	}
        	
        	LoanInfoModel loanInfo = this.loanInfoService.saveLoanInfo(crewId, loanId, receiptNo, loanDate, payeeName, 
        			summary, money, currencyId, paymentWay, agent, financeSubjId, financeSubjName, attpacketId);
        	boolean flag = false; //标识新增、修改，false：新增
        	if (StringUtils.isNotBlank(loanId)) {
        		flag = true;
        	}
        	
    		loanId = loanInfo.getLoanId();
    		attpacketId = loanInfo.getAttpackId();
        	
    		resultMap.put("attpacketId", attpacketId);
        	resultMap.put("loanId", loanId);
        	if (!flag) {
            	this.sysLogService.saveSysLog(request, "新增借款单信息", Constants.TERMINAL_PC, LoanInfoModel.TABLE_NAME, receiptNo, SysLogOperType.INSERT.getValue());
        	} else {
            	this.sysLogService.saveSysLog(request, "修改借款单信息", Constants.TERMINAL_PC, LoanInfoModel.TABLE_NAME, receiptNo, SysLogOperType.UPDATE.getValue());
        	}
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
        } catch(Exception e) {
            success = false;
            message = "未知异常";

            logger.error("未知异常", e);
        	this.sysLogService.saveSysLog(request, "保存借款单信息失败：" + e.getMessage(), Constants.TERMINAL_PC, LoanInfoModel.TABLE_NAME, loanId, SysLogOperType.ERROR.getValue());
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
	
	/**
	 * 根据借款单id删除借款单信息；如果借款单关联了付款单则不能删除
	 * @param request
	 * @param loanId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteLoanInfo")
	public Map<String, Object> deleteLoanInfo(HttpServletRequest request, String loanId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			if (StringUtils.isBlank(loanId)) {
				throw new IllegalArgumentException("请选择要删除的借款单！");
			}
			
			//先根据借款单id查询是否有付款单，如果有则提示先删除付款单，在删除借款单
			List<PaymentLoanMapModel> mapList = this.paymentLoanMapService.queryByLoanId(crewId, loanId);
    		if (mapList != null && mapList.size() > 0) {
    			throw new IllegalArgumentException("该借款已有付款单，请先删除付款单，再删除借款单！");
    		}
			
    		this.loanInfoService.deleteLoanInfo(loanId);
    		message = "删除成功！";
    		
    		this.sysLogService.saveSysLog(request, "删除借款单", Constants.TERMINAL_PC, LoanInfoModel.TABLE_NAME, loanId, 3);
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
		} catch (Exception e) {
			message = "未知异常，删除失败！";
			success = false;
    		this.sysLogService.saveSysLog(request, "删除借款单失败：" + e.getMessage(), Constants.TERMINAL_PC, LoanInfoModel.TABLE_NAME, loanId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		
		return resultMap;
	}
	
	/**
	 * 查询单个借款单详细信息
	 * @param request
	 * @param loanId	借款单ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryLoanDetailInfo")
	public Map<String, Object> queryLoanDetailInfo(HttpServletRequest request, String loanId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	if (StringUtils.isBlank(loanId)) {
        		throw new IllegalArgumentException("请提供借款单ID");
        	}
        	String crewId = this.getCrewId(request);
        	
        	LoanInfoModel loanInfo = this.loanInfoService.queryById(loanId);
        	
        	Map<String, Object> loanInfoMap = new HashMap<String, Object>();
        	loanInfoMap.put("loanId", loanInfo.getLoanerId());
        	loanInfoMap.put("receiptNo", this.formatReceiptNo(loanInfo.getReceiptNo()));
        	loanInfoMap.put("loanDate", this.sdf1.format(loanInfo.getLoanDate()));
        	loanInfoMap.put("peyeeName", loanInfo.getPayeeName());
        	loanInfoMap.put("summary", loanInfo.getSummary());
        	loanInfoMap.put("financeSubjId", loanInfo.getFinanceSubjId());
        	
        	loanInfoMap.put("financeSubjName", loanInfo.getFinanceSubjName());
        	this.financeSubjectService.refreshCachedSubjectList(crewId);
        	//由于financeSubjName是新添字段，因此存在老数据未赋值的情况，此处兼容一下
			if (!StringUtils.isBlank(loanInfo.getFinanceSubjId())) {
				loanInfoMap.put("financeSubjName", this.financeSubjectService.getFinanceSubjName(loanInfo.getFinanceSubjId()));
			}
        	
        	loanInfoMap.put("currencyId", loanInfo.getCurrencyId());
        	loanInfoMap.put("money", loanInfo.getMoney());
        	loanInfoMap.put("paymentWay", loanInfo.getPaymentWay());
        	loanInfoMap.put("agent", loanInfo.getAgent());
        	
        	//查询附件信息列表
        	List<AttachmentModel> attachmentList = this.attachmentService.queryAttByPackId(loanInfo.getAttpackId());
        	
        	resultMap.put("attachmentList", attachmentList);
        	resultMap.put("attachmentPacketId", loanInfo.getAttpackId());
        	resultMap.put("loanInfo", loanInfoMap);
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
	 * 查询多个借款单详细信息(打印用)
	 * 该查询方法返回的币种是字符串文本值
	 * @param request
	 * @param loanIds	借款单ID，多个以逗号隔开
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryManyLoanDetailInfo")
	public Map<String, Object> queryManyLoanDetailInfo(HttpServletRequest request, String loanIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	
        	LoanInfoFilter filter = new LoanInfoFilter();
        	filter.setLoanIds(loanIds);
        	List<Map<String, Object>> loanInfoList = this.loanInfoService.queryLoanInfoList(crewId, filter);
        	
        	this.financeSubjectService.refreshCachedSubjectList(crewId);
        	
        	for (Map<String, Object> map : loanInfoList) {
        		String financeSubjId = (String) map.get("financeSubjId");
    			Integer paymentWay = (Integer) map.get("paymentWay");
        		if (!StringUtils.isBlank(financeSubjId)) {
        			map.put("financeSubjName", this.financeSubjectService.getFinanceSubjName(financeSubjId));
        		}
        		map.put("paymentWay", LoanPaymentWay.valueOf(paymentWay).getName());
        	}
        	
        	resultMap.put("loanInfoList", loanInfoList);
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
	 * 查询借款人列表（带有借款人的借款、还款信息）
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryPayeeListWithMoneyInfo")
	public Map<String, Object> queryPayeeListWithMoneyInfo(HttpServletRequest request, Page page) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	
        	List<Map<String, Object>> payeeList = this.loanInfoService.queryPayeeListWithMoneyInfo(crewId, page);
        	
        	Map<String, String> currencyIdCodeMap = new HashMap<String, String>();	//key为币种ID，value为币种编码
        	Map<String, Double> currencyIdRateMap = new HashMap<String, Double>();	//key为币种ID，value为币种汇率
        	Map<String, Double> currencyIdLoanMoneyMap = new HashMap<String, Double>();	//key为币种ID，value为该币种借款金额
        	Map<String, Double> currencyIdPayedMoneyMap = new HashMap<String, Double>();	//key为币种ID，value为该币种已还金额
        	Map<String, Double> currencyIdLeftMoneyMap = new HashMap<String, Double>();	//key为币种ID，value为该币种欠款金额
        	for (Map<String, Object> map : payeeList) {
        		Double loanMoney = (Double) map.get("loanMoney");
        		Double leftMoney = (Double) map.get("leftMoney");
        		String currencyId = (String) map.get("currencyId");
        		String currencyCode = (String) map.get("currencyCode");
        		Double exchangeRate = (Double) map.get("exchangeRate");
        		
        		//已还款金额
        		Double payedMoney = BigDecimalUtil.subtract(loanMoney, leftMoney);
        		map.put("payedMoney", payedMoney);
        		
        		//币种
        		if (!currencyIdCodeMap.containsKey(currencyId)) {
        			currencyIdCodeMap.put(currencyId, currencyCode);
        		}
        		if (!currencyIdRateMap.containsKey(currencyId)) {
        			currencyIdRateMap.put(currencyId, exchangeRate);
        		}
        		//借款金额
        		if (currencyIdLoanMoneyMap.containsKey(currencyId)) {
        			Double myLoanMoney = currencyIdLoanMoneyMap.get(currencyId);
        			currencyIdLoanMoneyMap.put(currencyId, BigDecimalUtil.add(loanMoney, myLoanMoney));
        		} else {
        			currencyIdLoanMoneyMap.put(currencyId, loanMoney);
        		}
        		//已还金额
        		if (currencyIdPayedMoneyMap.containsKey(currencyId)) {
        			Double myPayedMoney= currencyIdPayedMoneyMap.get(currencyId);
        			currencyIdPayedMoneyMap.put(currencyId, BigDecimalUtil.add(payedMoney, myPayedMoney));
        		} else {
        			currencyIdPayedMoneyMap.put(currencyId, payedMoney);
        		}
        		//欠款金额
        		if (currencyIdLeftMoneyMap.containsKey(currencyId)) {
        			Double myLeftMoney= currencyIdLeftMoneyMap.get(currencyId);
        			currencyIdLeftMoneyMap.put(currencyId, BigDecimalUtil.add(leftMoney, myLeftMoney));
        		} else {
        			currencyIdLeftMoneyMap.put(currencyId, leftMoney);
        		}
        	}
        	
        	
        	//货币统计信息
        	Double standardLoanMoney = 0.00;	//折合成本位币的借款金额
        	Double standardPayedMoney = 0.00;	//折合成本位币的付款金额
        	Double standardLeftMoney = 0.00;	//折合成本位币的欠款金额
        	
        	List<Map<String, Object>> currencyList = new ArrayList<Map<String, Object>>();	//其他各个货币统计信息
        	Set<String> currencyIdSet = currencyIdCodeMap.keySet();
        	for (String currencyId : currencyIdSet) {
        		Double exchangeRate = currencyIdRateMap.get(currencyId);
        		Double loanMoney = currencyIdLoanMoneyMap.get(currencyId);
        		Double payedMoney = currencyIdPayedMoneyMap.get(currencyId);
        		Double leftMoney = currencyIdLeftMoneyMap.get(currencyId);
        		
        		Map<String, Object> currencyMoneyInfo = new HashMap<String, Object>();
        		currencyMoneyInfo.put("currencyId", currencyId);
        		currencyMoneyInfo.put("currencyCode", currencyIdCodeMap.get(currencyId));
        		currencyMoneyInfo.put("loanMoney", loanMoney);
        		currencyMoneyInfo.put("payedMoney", payedMoney);
        		currencyMoneyInfo.put("leftMoney", leftMoney);
        		
        		currencyList.add(currencyMoneyInfo);
        		
        		
        		//折合成本位币
        		standardLoanMoney = BigDecimalUtil.add(standardLoanMoney, BigDecimalUtil.multiply(exchangeRate, loanMoney));
        		standardPayedMoney = BigDecimalUtil.add(standardPayedMoney, BigDecimalUtil.multiply(exchangeRate, payedMoney));
        		standardLeftMoney = BigDecimalUtil.add(standardLeftMoney, BigDecimalUtil.multiply(exchangeRate, leftMoney));
        	}
        	
        	//本位币统计信息
        	CurrencyInfoModel standardCurrency = this.currencyInfoService.queryStandardCurrency(crewId);
        	Map<String, Object> standardMoneyMap = new HashMap<String, Object>();
        	standardMoneyMap.put("currencyId", standardCurrency.getId());
        	standardMoneyMap.put("currencyCode", standardCurrency.getCode());
        	standardMoneyMap.put("loanMoney", standardLoanMoney);
        	standardMoneyMap.put("payedMoney", standardPayedMoney);
        	standardMoneyMap.put("leftMoney", standardLeftMoney);
        	
        	
        	resultMap.put("currencyList", currencyList);
        	resultMap.put("payeeList", payeeList);
        	resultMap.put("standardMoneyMap", standardMoneyMap);
        	
        	this.sysLogService.saveSysLog(request, "查询借款人列表（带有借款人的借款、还款信息）", Constants.TERMINAL_PC, LoanInfoModel.TABLE_NAME, null, 0);
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
	 * 查询借款单列表
	 * @param request
	 * @param payeeName	借款人名称
	 * @param currencyId	币种ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryLoanInfoList")
	public Map<String, Object> queryLoanInfoList(HttpServletRequest request, String payeeName, String currencyId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	
        	List<Map<String, Object>> loanInfoList = this.loanInfoService.queryLoanWithPaymentInfo(crewId, payeeName, null, currencyId, null, null, null, null);
        	
        	for (Map<String, Object> map : loanInfoList) {
				Double money = (Double) map.get("money");
				Double leftMoney = (Double) map.get("leftMoney");
				Double payedMoney = BigDecimalUtil.subtract(money, leftMoney);
				map.put("leftMoney", leftMoney);
				map.put("payedMoney", payedMoney);
        	}
        	
        	resultMap.put("loanInfoList", loanInfoList);
        	
        	this.sysLogService.saveSysLog(request, "查询借款单列表", Constants.TERMINAL_PC, LoanInfoModel.TABLE_NAME, payeeName, 0);
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
	 * 导出借款人列表（带有借款人的借款、还款信息）
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exportPayeeListWithMoneyInfo")
	public Map<String, Object> exportPayeeListWithMoneyInfo(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			String crewId = crewInfo.getCrewId();
			String crewName = crewInfo.getCrewName();
        	
			boolean  singleCurrencyFlag= false;	//标识剧组中是否只有一个有效币种
			Map<String, Object> conditionMap = new HashMap<String, Object>();
        	conditionMap.put("crewId", crewId);
        	conditionMap.put("ifEnable", true);
        	List<CurrencyInfoModel> currencyInfoList = this.currencyInfoService.queryManyByMutiCondition(conditionMap, null);
			if (currencyInfoList.size() == 1) {
				singleCurrencyFlag = true;
			}
			
        	List<Map<String, Object>> payeeList = this.loanInfoService.queryPayeeListWithMoneyInfo(crewId, null);
        	
        	
        	Map<String, String> currencyIdCodeMap = new HashMap<String, String>();	//key为币种ID，value为币种编码
        	Map<String, Double> currencyIdRateMap = new HashMap<String, Double>();	//key为币种ID，value为币种汇率
        	Map<String, Double> currencyIdLoanMoneyMap = new HashMap<String, Double>();	//key为币种ID，value为该币种借款金额
        	Map<String, Double> currencyIdPayedMoneyMap = new HashMap<String, Double>();	//key为币种ID，value为该币种已还金额
        	Map<String, Double> currencyIdLeftMoneyMap = new HashMap<String, Double>();	//key为币种ID，value为该币种欠款金额
        	for (Map<String, Object> map : payeeList) {
        		Double loanMoney = (Double) map.get("loanMoney");
        		Double leftMoney = (Double) map.get("leftMoney");
        		String currencyId = (String) map.get("currencyId");
        		String currencyCode = (String) map.get("currencyCode");
        		Double exchangeRate = (Double) map.get("exchangeRate");
        		
        		//已还款金额
        		Double payedMoney = BigDecimalUtil.subtract(loanMoney, leftMoney);
        		map.put("payedMoney", payedMoney);
        		
        		//币种
        		if (!currencyIdCodeMap.containsKey(currencyId)) {
        			currencyIdCodeMap.put(currencyId, currencyCode);
        		}
        		if (!currencyIdRateMap.containsKey(currencyId)) {
        			currencyIdRateMap.put(currencyId, exchangeRate);
        		}
        		//借款金额
        		if (currencyIdLoanMoneyMap.containsKey(currencyId)) {
        			Double myLoanMoney = currencyIdLoanMoneyMap.get(currencyId);
        			currencyIdLoanMoneyMap.put(currencyId, BigDecimalUtil.add(loanMoney, myLoanMoney));
        		} else {
        			currencyIdLoanMoneyMap.put(currencyId, loanMoney);
        		}
        		//已还金额
        		if (currencyIdPayedMoneyMap.containsKey(currencyId)) {
        			Double myPayedMoney= currencyIdPayedMoneyMap.get(currencyId);
        			currencyIdPayedMoneyMap.put(currencyId, BigDecimalUtil.add(payedMoney, myPayedMoney));
        		} else {
        			currencyIdPayedMoneyMap.put(currencyId, payedMoney);
        		}
        		//欠款金额
        		if (currencyIdLeftMoneyMap.containsKey(currencyId)) {
        			Double myLeftMoney= currencyIdLeftMoneyMap.get(currencyId);
        			currencyIdLeftMoneyMap.put(currencyId, BigDecimalUtil.add(leftMoney, myLeftMoney));
        		} else {
        			currencyIdLeftMoneyMap.put(currencyId, leftMoney);
        		}
        		
        		String leftMoneyStr = this.df.format(leftMoney);
        		String loanMoneyStr = this.df.format(loanMoney);
        		String payedMoneyStr = this.df.format(payedMoney);
        		if (!singleCurrencyFlag) {
        			leftMoneyStr += "(" + currencyCode + ")";
        			loanMoneyStr += "(" + currencyCode + ")";
        			payedMoneyStr += "(" + currencyCode + ")";
        		}
        		
        		map.put("leftMoney", leftMoneyStr);
        		map.put("loanMoney", loanMoneyStr);
        		map.put("payedMoney", payedMoneyStr);
        	}
        	
        	
        	//货币统计信息
        	Double standardLoanMoney = 0.00;	//折合成本位币的借款金额
        	Double standardPayedMoney = 0.00;	//折合成本位币的付款金额
        	Double standardLeftMoney = 0.00;	//折合成本位币的欠款金额
        	
        	List<Map<String, Object>> currencyList = new ArrayList<Map<String, Object>>();
        	Set<String> currencyIdSet = currencyIdCodeMap.keySet();
        	for (String currencyId : currencyIdSet) {
        		String currencyCode = currencyIdCodeMap.get(currencyId);
        		Double exchangeRate = currencyIdRateMap.get(currencyId);
        		Double loanMoney = currencyIdLoanMoneyMap.get(currencyId);
        		Double payedMoney = currencyIdPayedMoneyMap.get(currencyId);
        		Double leftMoney = currencyIdLeftMoneyMap.get(currencyId);
        		
        		String leftMoneyStr = this.df.format(leftMoney);
        		String loanMoneyStr = this.df.format(loanMoney);
        		String payedMoneyStr = this.df.format(payedMoney);
        		if (!singleCurrencyFlag) {
        			leftMoneyStr += "(" + currencyCode + ")";
        			loanMoneyStr += "(" + currencyCode + ")";
        			payedMoneyStr += "(" + currencyCode + ")";
        		}
        		
        		Map<String, Object> currencyMoneyInfo = new HashMap<String, Object>();
        		currencyMoneyInfo.put("currencyId", currencyId);
        		currencyMoneyInfo.put("currencyCode", currencyCode);
        		currencyMoneyInfo.put("loanMoney", loanMoneyStr);
        		currencyMoneyInfo.put("payedMoney", payedMoneyStr);
        		currencyMoneyInfo.put("leftMoney", leftMoneyStr);
        		
        		currencyList.add(currencyMoneyInfo);
        		
        		
        		//折合成本位币
        		standardLoanMoney = BigDecimalUtil.add(standardLoanMoney, BigDecimalUtil.multiply(exchangeRate, loanMoney));
        		standardPayedMoney = BigDecimalUtil.add(standardPayedMoney, BigDecimalUtil.multiply(exchangeRate, payedMoney));
        		standardLeftMoney = BigDecimalUtil.add(standardLeftMoney, BigDecimalUtil.multiply(exchangeRate, leftMoney));
        	}
        	
        	//本位币统计信息
        	CurrencyInfoModel standardCurrency = this.currencyInfoService.queryStandardCurrency(crewId);
        	Map<String, Object> standardMoneyMap = new HashMap<String, Object>();
        	String currencyCode = standardCurrency.getCode();
        	standardMoneyMap.put("currencyId", standardCurrency.getId());
        	standardMoneyMap.put("currencyCode", currencyCode);
        	String leftMoneyStr = this.df.format(standardLeftMoney);
    		String loanMoneyStr = this.df.format(standardLoanMoney);
    		String payedMoneyStr = this.df.format(standardPayedMoney);
    		if (!singleCurrencyFlag) {
    			leftMoneyStr += "(" + currencyCode + ")";
    			loanMoneyStr += "(" + currencyCode + ")";
    			payedMoneyStr += "(" + currencyCode + ")";
    		}
        	standardMoneyMap.put("loanMoney", loanMoneyStr);
        	standardMoneyMap.put("payedMoney", payedMoneyStr);
        	standardMoneyMap.put("leftMoney", leftMoneyStr);
        	
        	
        	
        	//生成下载的文件
			Map<String, Object> exportDataMap = new HashMap<String, Object>();
			exportDataMap.put("currencyList", currencyList);
			exportDataMap.put("payeeList", payeeList);
			exportDataMap.put("standardMoneyMap", standardMoneyMap);
			
			Properties property = PropertiesUitls.fetchProperties("/config.properties");
	  		String srcfilePath = property.getProperty("jkxq_template");
	  		String downloadPath = property.getProperty("downloadPath") + "《" + crewName + "》" + "借款详情_"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".xls";
	  		File pathFile = new File(property.getProperty("downloadPath"));
	  		if(!pathFile.isDirectory()){
	  			pathFile.mkdirs();
	  		}
	  		
	  		ExportExcelUtil.exportViewToExcelTemplate(srcfilePath, exportDataMap, downloadPath);
        	
	  		resultMap.put("downloadPath", downloadPath);
	  		
	  		this.sysLogService.saveSysLog(request, "导出借款详情", Constants.TERMINAL_PC, LoanInfoModel.TABLE_NAME, null, 5);
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
        } catch(Exception e) {
            success = false;
            message = "未知异常";

            logger.error("未知异常", e);
	  		this.sysLogService.saveSysLog(request, "导出借款详情失败：" + e.getMessage(), Constants.TERMINAL_PC, LoanInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
	
}
