package com.xiaotu.makeplays.finance.service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.attachment.model.constants.AttachmentBuzType;
import com.xiaotu.makeplays.attachment.service.AttachmentService;
import com.xiaotu.makeplays.finance.controller.filter.LoanInfoFilter;
import com.xiaotu.makeplays.finance.dao.FinancePaymentWayDao;
import com.xiaotu.makeplays.finance.dao.LoanInfoDao;
import com.xiaotu.makeplays.finance.dao.PaymentLoanMapDao;
import com.xiaotu.makeplays.finance.model.FinanceSettingModel;
import com.xiaotu.makeplays.finance.model.LoanInfoModel;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 借款信息
 * @author xuchangjian 2016-8-3下午5:13:25
 */
@Service
public class LoanInfoService {
	
	@Autowired
	private LoanInfoDao loanInfoDao;
	
	@Autowired
	private FinancePaymentWayDao financePaymentWayDao;
	
	@Autowired
	private PaymentLoanMapDao paymentLoanMapDao;
	
	@Autowired
	private AttachmentService attachmentService;
	
	@Autowired
	private FinanceSettingService financeSettingService;
	
	/**
	 * 根据多个条件查询借款单信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<LoanInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.loanInfoDao.queryManyByMutiCondition(conditionMap, page);
	}

	/**
	 * 根据财务科目查询借款单
	 * @param crewId
	 * @param financeSubjId
	 * @return
	 */
	public List<LoanInfoModel> queryByFinanceSubjId(String crewId, String financeSubjId) {
		return this.loanInfoDao.queryByFinanceSubjId(crewId, financeSubjId);
	}
	
	/**
	 * 根据剧组ID查询借款单
	 * @param crewId
	 * @return
	 */
	public List<LoanInfoModel> queryByCrewId(String crewId) {
		return this.loanInfoDao.queryByCrewId(crewId);
	}
	
	/**
	 * 查询剧组下带有财务科目的借款单
	 * @param crewId
	 * @return
	 */
	public List<LoanInfoModel> queryFinanceLoanList(String crewId) {
		return this.loanInfoDao.queryFinanceLoanList(crewId);
	}
	
	/**
	 * 查询借款单的预算
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryLoanBudget(String crewId) {
		return this.loanInfoDao.queryLoanBudget(crewId);
	}
	
	/**
	 * 根据借款人查询借款单信息，带有已结算还款信息
	 * 该查询有个默认强制的条件：所有用来还该借款单的付款单，其币种必须一致，且和该借款单的币种一致
	 * 
	 * @param crewId
	 * @param payeeName	借款人
	 * @param currencyId 币种ID
	 * @return loanerId借款单ID， loanDate借款日期， receiptNo借款单票据编号， 
	 * currencyId借款单关联货币ID， currencyCode借款单关联货币编码，exchangeRate汇率， money借款金额
	 * summary借款单摘要， paymoney已还金额，paymentCount关联的付款单数量
	 */
	public List<Map<String, Object>> queryLoanWithPaymentInfo(String crewId, String payeeName, String loanIds, 
			String currencyId, Boolean onlySettled, String paymentStartDate, String paymentEndDate, String financeSubjId) {
		return this.loanInfoDao.queryLoanWithPaymentInfo(crewId, payeeName, loanIds, currencyId, 
				onlySettled, paymentStartDate, paymentEndDate, financeSubjId);
	}
	
	/**
	 * 根据ID查询借款单
	 * @param loanId
	 * @return
	 * @throws Exception 
	 */
	public LoanInfoModel queryById(String loanId) throws Exception {
		return this.loanInfoDao.queryById(loanId);
	}
	
	/**
	 * 根据多个ID查询借款单
	 * @param loanId
	 * @return
	 * @throws Exception 
	 */
	public List<LoanInfoModel> queryByIds(String loanIds) throws Exception {
		return this.loanInfoDao.queryByIds(loanIds);
	}
	
	/**
	 * 查询最大的票据编号
	 * 
	 * @param payStatus	票据编号是否按月重新开始
	 * @param moonFirstDay 付款当月第一天
	 * @param moonLastDay 付款当月最后一天
	 * @return
	 */
	public String queryMaxReceiptNo(String crewId, boolean payStatus, Date moonFirstDay, Date moonLastDay) {
		return this.loanInfoDao.queryMaxReceiptNo(crewId, payStatus, moonFirstDay, moonLastDay);
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
	 * @return 借款单ID
	 * @throws Exception 
	 */
	public LoanInfoModel saveLoanInfo(String crewId, String loanId, String receiptNo, 
			String loanDate, String payeeName, String summary, Double money, String currencyId, 
			Integer paymentWay, String agent, String financeSubjId, String financeSubjName, String attpacketId) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		LoanInfoModel loanInfo = new LoanInfoModel();
		if (!StringUtils.isBlank(loanId)) {
			loanInfo = this.loanInfoDao.queryById(loanId);
		} 
		
		loanInfo.setCrewId(crewId);
		loanInfo.setReceiptNo(receiptNo.replace("-", ""));
		loanInfo.setLoanDate(sdf.parse(loanDate));
		loanInfo.setPayeeName(payeeName);
		loanInfo.setSummary(summary);
		loanInfo.setMoney(money);
		loanInfo.setCurrencyId(currencyId);
		loanInfo.setAgent(agent);
		loanInfo.setFinanceSubjId(financeSubjId);
		loanInfo.setFinanceSubjName(financeSubjName);
		loanInfo.setPaymentWay(paymentWay);
		
		//设置附件包id
		if (StringUtils.isBlank(attpacketId)) {
			attpacketId = this.attachmentService.createNewPacket(crewId, AttachmentBuzType.Contract.getValue());
		}
		loanInfo.setAttpackId(attpacketId);
		
		if (!StringUtils.isBlank(loanId)) {
			this.loanInfoDao.updateWithNull(loanInfo, "loanId");
		} else {
			loanId = UUIDUtils.getId();
			loanInfo.setLoanId(loanId);
			loanInfo.setCreateTime(new Date());
			this.loanInfoDao.add(loanInfo);
		}
		
		return loanInfo;
	}
	
	/**
	 * 查询借款单信息
	 * @param crewId
	 * @return	借款日期， 创建时间， 票据编号，摘要，财务科目ID，财务科目名称，金额，借款人，支付方式，记账人，关联货币ID，关联货币编码，货币名称
	 */
	public List<Map<String, Object>> queryLoanInfoList (String crewId, LoanInfoFilter loanInfoFilter) {
		
		return this.loanInfoDao.queryLoanInfoList(crewId, loanInfoFilter);
	}
	
	/**
	 * 查询借款单币种统计信息
	 * @param crewId
	 * @return	
	 */
	public List<Map<String, Object>> queryLoanStatistic (String crewId, LoanInfoFilter loanInfoFilter) {
		return this.loanInfoDao.queryLoanStatistic(crewId, loanInfoFilter);
	}
	
	/**
	 * 查询借款人列表（带有借款、还款信息）
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryPayeeListWithMoneyInfo(String crewId, Page page) {
		return this.loanInfoDao.queryPayeeListWithMoneyInfo(crewId, page);
	}
	
	/**
	 * 删除借款单信息
	 * @param loanId
	 * @throws Exception 
	 */
	public void deleteLoanInfo (String loanId) throws Exception {
		//删除借款单
		this.loanInfoDao.deleteOne(loanId, "loanId", LoanInfoModel.TABLE_NAME);
		
		//删除和付款单的关联
		this.paymentLoanMapDao.deleteByLoanId(loanId);
	}
	
	/**
	 * 批量更新借款单
	 * @param loanList
	 * @throws Exception
	 */
	public void updateBatch(List<LoanInfoModel> loanList) throws Exception {
		this.loanInfoDao.updateBatch(loanList, "loanId", LoanInfoModel.class);
	}
	
	/**
	 * 查询付款单的还借款情况
	 * @param paymentId
	 * @return	借款人名称，偿还金额
	 */
	public List<Map<String, Object>> queryPaymentLoanInfo(String paymentId) {
		return this.loanInfoDao.queryPaymentLoanInfo(paymentId);
	}
	
	/**
	 * 获取借款单新的单据号
	 * @param crewId  剧组id
	 * @param loanDate操作时间
	 * @param originalReceipNo 已有的编号
	 * @return   新的单据号
	 * @throws Exception
	 */
	public String getNewReceiptNo(String crewId, String loanDate, String originalReceipNo) throws Exception{
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		
    	//如果付款日期为空，则默认设置为当天
    	Date myCollectionDate = new Date();
    	if (!StringUtils.isBlank(loanDate)) {
    		myCollectionDate = sdf1.parse(loanDate);
    	}
    	
    	//获取付款日期当月的第一天和最后一天
    	Date moonFirstDay = new Date();
		Date moonLastDay = new Date();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(myCollectionDate);
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
    	if (financeSetting == null || financeSetting.getPayStatus() == null) {
    		throw new IllegalArgumentException("请先在【费用管理-财务设置-单据设置】中进行相关设置");
    	}
    	Boolean payStatus = financeSetting.getPayStatus();	//付款单编号是否按月重新开始
    	
    	String newReceiptNo = originalReceipNo;
    	if (StringUtils.isBlank(originalReceipNo) || payStatus) {
    		//根据hasReceipt查询最大的付款单编号
        	String maxReceipNo = this.queryMaxReceiptNo(crewId, payStatus, moonFirstDay, moonLastDay);
        	
        	//计算最新的付款单票据编号
        	if (StringUtils.isBlank(maxReceipNo)) {
        		maxReceipNo = "JK00000000";
        	}
        	
        	String prefix = maxReceipNo.substring(0, 2);
        	String numberStr = maxReceipNo.substring(2, maxReceipNo.length());
        	
        	DecimalFormat df = new DecimalFormat("00000000");
        	int number = Integer.parseInt(numberStr) + 1;
        	String newNumberStr = df.format(number);
        	
        	newReceiptNo = prefix.substring(0, 2) + newNumberStr.substring(0, 4) + newNumberStr.substring(4, 8);
    	}
    	return newReceiptNo;
	}
	
	/**
	 * 查询借款单的付款单列表
	 * @param crewId
	 * @param financeSubjId
	 * @return
	 */
	public List<Map<String, Object>> queryLoanPaymentList(String crewId, String financeSubjId){
		return this.loanInfoDao.queryLoanPaymentList(crewId, financeSubjId);
	}
}
