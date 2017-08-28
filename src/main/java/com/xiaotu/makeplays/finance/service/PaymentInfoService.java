package com.xiaotu.makeplays.finance.service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.attachment.model.constants.AttachmentBuzType;
import com.xiaotu.makeplays.attachment.service.AttachmentService;
import com.xiaotu.makeplays.finance.controller.filter.PaymentInfoFilter;
import com.xiaotu.makeplays.finance.dao.FinancePaymentWayDao;
import com.xiaotu.makeplays.finance.dao.PaymentInfoDao;
import com.xiaotu.makeplays.finance.dao.PaymentLoanMapDao;
import com.xiaotu.makeplays.finance.model.FinancePaymentWayModel;
import com.xiaotu.makeplays.finance.model.FinanceSettingModel;
import com.xiaotu.makeplays.finance.model.PaymentInfoModel;
import com.xiaotu.makeplays.finance.model.PaymentLoanMapModel;
import com.xiaotu.makeplays.finance.model.constants.BillType;
import com.xiaotu.makeplays.message.model.MessageInfoModel;
import com.xiaotu.makeplays.message.model.constants.MessageInfoStatus;
import com.xiaotu.makeplays.message.model.constants.MessageType;
import com.xiaotu.makeplays.message.service.MessageInfoService;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 付款单
 * @author xuchangjian 2016-7-4下午4:06:15
 */
@Service
public class PaymentInfoService {
	
	@Autowired
	private PaymentInfoDao paymentInfoDao;
	
	@Autowired
	private FinancePaymentWayDao financePaymentWayDao;
	
	@Autowired
	private LoanInfoService loanInfoService;
	
	@Autowired
	private ContractToPaidService contractToPaidService;
	
	@Autowired
	private PaymentLoanMapDao paymentLoanMapDao;

	@Autowired
	private PaymentLoanMapService paymentLoanMapService;
	
	@Autowired
	private PaymentFinanSubjMapService paymentFinanSubjMapService;
	
	@Autowired
	private MessageInfoService messageInfoService;
	
	@Autowired
	private AttachmentService attachmentService;
	
	@Autowired
	private FinanceSettingService financeSettingService;
	
	/**
	 * 根据多个条件查询付款单信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<PaymentInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.paymentInfoDao.queryManyByMutiCondition(conditionMap, page);
	}

	/**
	 * 查询付款单信息
	 * 该查询结合预算科目表、会计科目表，查询付款单对应的预算科目、以及预算科目对应的会计科目
	 * 如果一张付款单中有两个预算科目，则将会返回两条该付款单记录
	 * 如果付款单中的预算科目没有分配到会计科目，仍然会返回该条付款单记录
	 * 
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
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryWithAccSubjAndFinaSubjInfo(String crewId, String paymentDates,
			String accSubjectCodes, String finaSubjIds, String payeeNames,
			String summary, Double startMoney, Double endMoney) {
		return this.paymentInfoDao.queryWithAccSubjAndFinaSubjInfo(crewId,
				paymentDates, accSubjectCodes, finaSubjIds, payeeNames, summary,
				startMoney, endMoney);
	}
	
	/**
	 * 查询剧组下的付款单
	 * @param crewId
	 * @return
	 */
	public List<PaymentInfoModel> queryByCrewId(String crewId) {
		return this.paymentInfoDao.queryByCrewId(crewId);
	}
	
	public List<Map<String, Object>> queryByCrewIdAndStatus(String crewId) {
		return this.paymentInfoDao.queryByCrewIdAndStatus(crewId);
	}
	
	/**
	 * 根据合同ID查询付款单
	 * @param contractId
	 * @return	付款时间、单据编号、总额、摘要、结算状态、货币编码
	 */
	public List<Map<String, Object>> queryByContractId(String contractId) {
		return this.paymentInfoDao.queryByContractId(contractId);
	}
	
	/**
	 * 查询最大的付款单编号
	 * 
	 * @param hasReceipt 是否有发票
	 * @param payStatus	付款单编号是否按月重新开始
	 * @param hasReceiptStatus	付款单编号是否分为有票无票
	 * @param moonFirstDay 付款当月第一天
	 * @param moonLastDay 付款当月最后一天
	 * @return
	 */
	public String queryMaxReceiptNo(String crewId, boolean hasReceipt, boolean payStatus, boolean hasReceiptStatus, Date moonFirstDay, Date moonLastDay) {
		return this.paymentInfoDao.queryMaxReceiptNo(crewId, hasReceipt, payStatus, hasReceiptStatus, moonFirstDay, moonLastDay);
	}
	
	/**
	 * 根据ID查询付款单
	 * @param paymentId
	 * @return
	 * @throws Exception 
	 */
	public PaymentInfoModel queryById(String paymentId) throws Exception {
		return this.paymentInfoDao.queryById(paymentId);
	}
	
	/**
	 * 根据ID查询付款单
	 * @param paymentId
	 * @return
	 * @throws Exception 
	 */
	public List<PaymentInfoModel> queryByIds(String paymentIds) throws Exception {
		paymentIds = "'" + paymentIds.replace(",", "','") + "'";
		String sql = "select * from " + PaymentInfoModel.TABLE_NAME + " where paymentId in ("+ paymentIds +") ";
		return this.paymentInfoDao.query(sql, new Object[] {}, PaymentInfoModel.class, null);
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
	 * @param loanIds 借款单ID，多个以逗号隔开
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
	 * @return 付款单ID
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/savePaymentInfo")
	public PaymentInfoModel savePaymentInfo(String crewId, String loginUserId, String paymentId, String receiptNo, 
			String paymentDate, String payeeName, String contractId, Integer contractType, String loanIds,
			String currencyId, Double totalMoney, String paymentWay, Boolean hasReceipt, 
			Integer billCount, String agent, Integer status, Boolean ifReceiveBill, 
			Integer billType, String remindTime, String paymentSubjMapStr,String contractPartId, boolean needSaveLoanInfo, String attpacketId, String department) throws ParseException, Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		PaymentInfoModel paymentInfo = new PaymentInfoModel();
		if (!StringUtils.isBlank(paymentId)) {
			paymentInfo = this.paymentInfoDao.queryById(paymentId);
		} else {
			paymentInfo.setPaymentId(UUIDUtils.getId());
		}
		//如果没有发票，则票据种类设置为空，是否收到发票设置为否
		if (!hasReceipt) {
			billType = null;
			ifReceiveBill = false;
			billCount = 0;
		}
		paymentInfo.setCrewId(crewId);
		paymentInfo.setReceiptNo(receiptNo.replace("-", ""));
		paymentInfo.setPaymentDate(sdf.parse(paymentDate));
		paymentInfo.setPayeeName(payeeName);
		paymentInfo.setContractId(contractId);
		paymentInfo.setContractType(contractType);
		paymentInfo.setCurrencyId(currencyId);
		paymentInfo.setTotalMoney(totalMoney);
		paymentInfo.setHasReceipt(hasReceipt);
		paymentInfo.setBillCount(billCount);
		paymentInfo.setAgent(agent);
		paymentInfo.setStatus(status);
		paymentInfo.setIfReceiveBill(ifReceiveBill);
		paymentInfo.setBillType(billType);
		if (!StringUtils.isBlank(remindTime)) {
			paymentInfo.setRemindTime(sdf.parse(remindTime));
		}
		paymentInfo.setDepartment(department);
		
		//设置附件包id
		if (StringUtils.isBlank(attpacketId)) {
			attpacketId = this.attachmentService.createNewPacket(crewId, AttachmentBuzType.Contract.getValue());
		}
		paymentInfo.setAttpackId(attpacketId);
		
		//保存消息记录
		//先删除已有的消息记录
		if (!StringUtils.isBlank(paymentId)) {
			this.messageInfoService.deleteByBuzId(paymentId);
		}
		//再新增消息记录
		if (!StringUtils.isBlank(remindTime)) {
			//保存用户的消息信息
			MessageInfoModel messageInfo = new MessageInfoModel();
			messageInfo.setId(UUIDUtils.getId());
			messageInfo.setCrewId(crewId);
			messageInfo.setSenderId(loginUserId);
			messageInfo.setReceiverId(loginUserId);
			messageInfo.setType(MessageType.PaymentReceiptGet.getValue());
			messageInfo.setBuzId(paymentId);
			messageInfo.setStatus(MessageInfoStatus.UnRead.getValue());
			messageInfo.setTitle("发票提醒");
			messageInfo.setContent("编号为"+ receiptNo +"的付款单的需于今日索要发票");
			messageInfo.setRemindTime(sdf.parse(remindTime));
			messageInfo.setCreateTime(new Date());
			this.messageInfoService.addOne(messageInfo);
		}
		
		//付款方式
		List<FinancePaymentWayModel> paymentWayList = this.financePaymentWayDao.queryByWayName(crewId, paymentWay);
		if (paymentWayList != null && paymentWayList.size() > 0) {
			paymentInfo.setPaymentWay(paymentWayList.get(0).getWayId());
		} else {
			FinancePaymentWayModel paymentWayModel = new FinancePaymentWayModel();
			paymentWayModel.setWayId(UUIDUtils.getId());
			paymentWayModel.setCrewId(crewId);
			paymentWayModel.setWayName(paymentWay);
			paymentWayModel.setCreateTime(new Date());
			
			this.financePaymentWayDao.add(paymentWayModel);
			paymentInfo.setPaymentWay(paymentWayModel.getWayId());
		}
		
		//借款单信息
		if (needSaveLoanInfo && !StringUtils.isBlank(loanIds)) {
			String[] loanIdArray = loanIds.split(",");
			List<Map<String, Object>> loanInfoList = this.loanInfoService.queryLoanWithPaymentInfo(crewId, null, loanIds, null, null, null, null, null);

			int index = 0;
			for (String loanId : loanIdArray) {
				boolean needBreak = false;
				for (Map<String, Object> loanInfo : loanInfoList) {
					String myLoanId = (String) loanInfo.get("loanId");
					if (!myLoanId.equals(loanId)) {
						continue;
					}
					Double leftMoney = (Double) loanInfo.get("leftMoney");	//剩余还款金额
					//如果不再欠款，则不为此借款单付款
					if (leftMoney <= 0) {
						continue;
					}
					
					//借款余额
					Double loanBalance = BigDecimalUtil.subtract(leftMoney, totalMoney);
					if (loanBalance < 0) {
						loanBalance = 0.00;
					}
					
//					boolean isAdd = false;
//					PaymentLoanMapModel paymentLoanMap = this.paymentLoanMapDao.queryByPaymentLoanId(paymentId, loanId);;
					PaymentLoanMapModel paymentLoanMap = new PaymentLoanMapModel();
//					if (paymentLoanMap == null) {
//						paymentLoanMap = new PaymentLoanMapModel();
						paymentLoanMap.setMapId(UUIDUtils.getId());
						paymentLoanMap.setCreateTime(new Date(System.currentTimeMillis() + (1000 * (index++))));
//						isAdd = true;
//					}
					paymentLoanMap.setCrewId(crewId);
					paymentLoanMap.setPaymentId(paymentInfo.getPaymentId());
					paymentLoanMap.setLoanId(loanId);
					paymentLoanMap.setLoanBalance(loanBalance);
					paymentLoanMap.setRepaymentMoney(leftMoney);
					
//					if (isAdd) {
						this.paymentLoanMapDao.add(paymentLoanMap);
//					} else {
//						this.paymentLoanMapDao.updateWithNull(paymentLoanMap, "mapId");
//					}
					
					//付款单还剩余的钱
					totalMoney = BigDecimalUtil.subtract(totalMoney, leftMoney);
					if (totalMoney <= 0) {
						needBreak = true;
						break;
					}
				}
				if (needBreak) {
					break;
				}
				
			}
			
		}
		
		//删除付款和财务科目关联关系
		this.paymentFinanSubjMapService.deleteByPaymentId(crewId, paymentInfo.getPaymentId());
		
		//付款和财务科目关联关系的保存
		this.paymentFinanSubjMapService.saveByPaymentSujectMapStr(crewId, paymentInfo.getPaymentId(), paymentSubjMapStr);
		
		if (StringUtils.isBlank(paymentId)) {
			this.paymentInfoDao.add(paymentInfo);
		} else {
			this.paymentInfoDao.updateWithNull(paymentInfo, "paymentId");
		}
		
		
		if(null!=contractType){
			//保存待付信息
			if(status== 0){
				contractToPaidService.arrangeContractToPaidReady(paymentInfo.getPaymentId(),contractId,contractPartId,contractType,status,paymentSubjMapStr);
			}else if(status == 1){
				//保存待付信息
				paymentId = StringUtils.isBlank(paymentId)?paymentInfo.getPaymentId():paymentId;
				if(StringUtils.isBlank(contractPartId)){
					contractToPaidService.updateContractTopaid2SettleByPaymentId(paymentId);
				}else{
					contractToPaidService.arrangeContractToPaidReady(paymentInfo.getPaymentId(),contractId,contractPartId,contractType,status,paymentSubjMapStr);
				}
			}
		}
		
		return paymentInfo;
	}
	
	/**
	 * 查询剧组中付款单信息
	 * @param crewId	剧组ID
	 * @return	付款日期， 创建时间，票据编码，摘要，财务科目ID，
	 * 财务科目名称，总金额，截至当前收款金额, 结算状态，收款人，
	 * 付款方式，是否有发票，单据张数，记账人，付款单关联货币ID，付款单关联货币编码，货币汇率
	 * 合同相关信息
	 */
	public List<Map<String, Object>> queryPaymentList(String crewId, PaymentInfoFilter paymentInfoFilter) {
		
		return this.paymentInfoDao.queryPaymentList(crewId, paymentInfoFilter);
	}
	
	/**
	 * 查询付款单中币种统计信息
	 * @param crewId
	 * @param paymentInfoFilter
	 * @return	币种ID，币种编码，总支出，总还借款的金额
	 */
	public List<Map<String, Object>> queryPaymentStatistic(String crewId, PaymentInfoFilter paymentInfoFilter) {
		return this.paymentInfoDao.queryPaymentStatistic(crewId, paymentInfoFilter);
	}
	
	/**
	 * 批量结算付款单
	 * @param paymentIds
	 */
	public void settleBatchPaymentList(String paymentIds) {
		this.paymentInfoDao.settleBatchPaymentList(paymentIds);
		//批量结算  合同待付清单
		contractToPaidService.batchSettleMent(paymentIds);
	}
	
	/**
	 * 设置付款单有票
	 * @param paymentIds
	 * @throws Exception 
	 */
	public void setPaymentHasReceiptBatch(String crewId, String paymentIds) throws Exception {
    	List<PaymentInfoModel> paymentList = this.paymentInfoDao.queryByIds(paymentIds);
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		for (PaymentInfoModel paymentInfo : paymentList) {
			if (!paymentInfo.getHasReceipt()) {
				String newReceipNo = this.getNewReceiptNo(crewId, true, sdf1.format(paymentInfo.getPaymentDate()), paymentInfo.getReceiptNo(), false, true);
    			paymentInfo.setReceiptNo(newReceipNo);
    			paymentInfo.setHasReceipt(true);
    			//无票改有票，票据种类默认设为普通发票
    			paymentInfo.setBillType(BillType.CommonReceip.getValue());
    			this.paymentInfoDao.update(paymentInfo, "paymentId");
			}
		}
//    	this.paymentInfoDao.updateBatch(paymentList, "paymentId", PaymentInfoModel.class);
	}
	
	/**
	 * 查询跟借款单关联的付款单信息
	 * @param crewId
	 * @param loanId
	 * @return	付款单ID，付款单编号，摘要，关联的财务科目，已付金额，借款余额
	 */
	public List<Map<String, Object>> queryByLoanId(String loanId) {
		return this.paymentInfoDao.queryByLoanId(loanId);
	}
	
	/**
	 * 删除付款单信息
	 * @param paymentId
	 * @throws Exception 
	 */
	public void deletePaymentInfo (String crewId, String paymentId) throws Exception {
		
		//删除付款单
		this.paymentInfoDao.deleteOne(paymentId, "paymentId", PaymentInfoModel.TABLE_NAME);
		
		//删除付款单和财务科目的关联
		this.paymentFinanSubjMapService.deleteByPaymentId(crewId, paymentId);
		
		//删除付款单和借款单的关联
		this.paymentLoanMapService.deleteByPaymentId(crewId, paymentId);
		
		//删除付款单对应的消息提醒
		this.messageInfoService.deleteByBuzId(paymentId);
		
		//删除合同待付信息
		contractToPaidService.deleteContractToPaidInfoByPaymentId(paymentId);
	}
	
	/**
	 * 获取付款单单据编号
	 * 该方法默认有票无票条件已经变了，付款月份已经变了
	 * @param crewId 剧组id
	 * @param hasReceipt 是否有单据
	 * @param paymentDate 操作日期
	 * @param originalReceipNo 原来的票据编号
	 * @param dateChangedFlag 票据日期是否已改变标识
	 * @param hasReceiptChangeFlag 有无发票是否已改变标识
	 * @return  新的单据号
	 * @throws Exception 
	 */
	public String getNewReceiptNo(String crewId, Boolean hasReceipt, String paymentDate, String originalReceipNo, boolean dateChangedFlag, boolean hasReceiptChangeFlag) throws Exception{
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		
    	if (hasReceipt == null) {
    		hasReceipt = true;
    	}
    	
    	//如果付款日期为空，则默认设置为当天
    	Date myPaymentDate = new Date();
    	if (!StringUtils.isBlank(paymentDate)) {
    		myPaymentDate = sdf1.parse(paymentDate);
    	}
    	
    	//获取付款日期当月的第一天和最后一天
    	Date moonFirstDay = new Date();
		Date moonLastDay = new Date();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(myPaymentDate);
		calendar.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
		moonFirstDay = calendar.getTime();
		
		calendar.add(Calendar.MONTH, 1);//月增加1天
		calendar.add(Calendar.DAY_OF_MONTH, -1);//日期倒数一日,即得到本月最后一天
		moonLastDay = calendar.getTime();
    	
    	
    	//查询财务设置票据设置信息
    	FinanceSettingModel financeSetting = this.financeSettingService.queryByCrewId(crewId);
    	if (financeSetting == null) {
    		financeSetting = this.financeSettingService.initFinanceSetting(crewId);
    	}
    	if (financeSetting == null || financeSetting.getHasReceiptStatus() == null || financeSetting.getPayStatus() == null) {
    		throw new IllegalArgumentException("请先在【费用管理-财务设置-单据设置】中进行相关设置");
    	}
    	Boolean hasReceiptStatus = financeSetting.getHasReceiptStatus();	//付款单编号是否分为有票无票
    	Boolean payStatus = financeSetting.getPayStatus();	//付款单编号是否按月重新开始
    	
    	String newReceiptNo = originalReceipNo;
    	if (!StringUtils.isBlank(originalReceipNo) && !hasReceiptStatus) {
			if (hasReceipt) {	//无票改有票
				newReceiptNo = originalReceipNo.replace("W", "Y");
			} else {	//有票改无票
				newReceiptNo = originalReceipNo.replace("Y", "W");
			}
    	} 
    	
    	if (StringUtils.isBlank(originalReceipNo) || (hasReceiptStatus && hasReceiptChangeFlag) || (payStatus && dateChangedFlag)) {
    		//根据hasReceipt查询最大的付款单编号
        	String maxReceipNo = this.queryMaxReceiptNo(crewId, hasReceipt, payStatus, hasReceiptStatus, moonFirstDay, moonLastDay);
        	
        	//计算最新的付款单票据编号
        	if (StringUtils.isBlank(maxReceipNo)) {
        		maxReceipNo = "0";
        	}
        	
        	String prefix = "";
        	if (hasReceipt) {
        		prefix = "FKYP";
        	} else {
        		prefix = "FKWP";
        	}
        	DecimalFormat df = new DecimalFormat("000000");
        	int number = Integer.parseInt(maxReceipNo) + 1;
        	
        	newReceiptNo = prefix.substring(0, 2) + prefix.substring(2, 4) + df.format(number);
    	}
    	
    	return newReceiptNo;
	}
	
	/**
	 * 查询付款单中的部门列表
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryPaymentDepartment(String crewId){
		return this.paymentInfoDao.queryPaymentDepartmentList(crewId);
	}
}
