package com.xiaotu.makeplays.finance.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.finance.dao.ContractMonthPayDetailDao;
import com.xiaotu.makeplays.finance.dao.ContractMonthPaywayDao;
import com.xiaotu.makeplays.finance.dao.ContractStagePayWayDao;
import com.xiaotu.makeplays.finance.model.ContractMonthPayDetailModel;
import com.xiaotu.makeplays.finance.model.ContractMonthPaywayModel;
import com.xiaotu.makeplays.finance.model.ContractStagePayWayModel;
import com.xiaotu.makeplays.finance.model.FinanceSettingModel;
import com.xiaotu.makeplays.finance.model.constants.ContractPayWay;
import com.xiaotu.makeplays.finance.model.constants.MonthDayType;
import com.xiaotu.makeplays.message.model.MessageInfoModel;
import com.xiaotu.makeplays.message.model.constants.MessageInfoStatus;
import com.xiaotu.makeplays.message.model.constants.MessageType;
import com.xiaotu.makeplays.message.service.MessageInfoService;
import com.xiaotu.makeplays.user.dao.UserInfoDao;
import com.xiaotu.makeplays.utils.AuthorityConstants;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.DateUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 合同支付方式
 * @author xuchangjian 2016-8-13下午5:18:04
 */
@Service
public class ContractPayWayService {

	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");

	private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/MM");

	@Autowired
	private ContractStagePayWayDao contractStagePayWayDao;
	
	@Autowired
	private ContractMonthPaywayDao contractMonthPaywayDao;
	
	@Autowired
	private ContractMonthPayDetailDao contractMonthPayDetailDao;
	
	@Autowired
	private MessageInfoService messageInfoService;
	
	@Autowired
	private UserInfoDao userInfoDao;
	
	@Autowired
	private FinanceSettingService financeSettingService;
	
	/**
	 * 删除合同与付款方式关联
	 */
	public void deleteByContractId(String contractId, String crewId) {
		this.contractStagePayWayDao.deleteByContractId(contractId, crewId);
		this.contractMonthPaywayDao.deleteByContractId(crewId, contractId);
		this.contractMonthPayDetailDao.deleteByContractId(crewId, contractId);
		
		this.messageInfoService.deleteByBuzId(contractId);
	}
	
	/**
	 * 根据合同的支付方式字符串保存支付方式
	 * @param crewId
	 * @param loginUserId 登录用户ID
	 * @param contractId	合同ID
	 * @param contractNo	合同编号
	 * @param paymentTerm	合同的支付方式字符串
	 * @param monthPayDetail 按月支付薪酬明细
	 * @param payWay	支付方式
	 * @throws ParseException, Exception 
	 */
	public List<Map<String, Object>> saveByPaymentTerm(String crewId, String loginUserId, String contractId, String contractNo, String paymentTerm, String monthPayDetail, Integer payWay) throws ParseException, Exception {
		//支付明细
		List<Map<String, Object>> payDetailInfoList = new ArrayList<Map<String, Object>>();
		
		
		//删除以前的消息记录
		this.messageInfoService.deleteByBuzId(contractId);
		
		//查询有编辑合同权限的人
		List<String> authList = new ArrayList<String>();
		authList.add(AuthorityConstants.PC_FINANCE_CONTRACT);
		List<Map<String, Object>> userList = this.userInfoDao.queryUserByCrewIdAndAuth(crewId, authList, false);
		
		//财务设置信息
		FinanceSettingModel financeSetting = this.financeSettingService.queryByCrewId(crewId);
    	if (financeSetting == null) {
    		financeSetting = this.financeSettingService.initFinanceSetting(crewId);
    	}
		int contractAdvanceRemindDays = -financeSetting.getContractAdvanceRemindDays();
		
		List<MessageInfoModel> messageInfoList = new ArrayList<MessageInfoModel>();
		
		List<ContractStagePayWayModel> toAddContractStagePayWayList = new ArrayList<ContractStagePayWayModel>();
		List<ContractMonthPaywayModel> toAddContractMonthPayWayList = new ArrayList<ContractMonthPaywayModel>();
		
		//添加支付方式
		Date lastDate = null;
		Map<String, Object> payDetailInfo = null;//支付明细信息
    	String[] paymentTermArray = paymentTerm.split("##");
    	for (String singlePaymentTerm : paymentTermArray) {
    		String[] paymentTermDetail = singlePaymentTerm.split("&&");
    		payDetailInfo = new HashMap<String, Object>();
    		
    		
    		//按阶段
    		if (payWay == ContractPayWay.PerStep.getValue()) {
    			
    			Integer stage = Integer.parseInt(paymentTermDetail[0]);	//支付阶段
        		String remindTimeStr = paymentTermDetail[1];	//提醒时间
        		String remark = paymentTermDetail[2];	//备注
        		Double rate = Double.parseDouble(paymentTermDetail[3]);	//支付比例
        		Double money = Double.parseDouble(paymentTermDetail[4]);	//支付金额
        		String oldId = paymentTermDetail[5];	//原ID
    			if (stage== null) {
    				throw new IllegalArgumentException("请填写支付阶段");
    			}
    			if (rate == null || money == null) {
    				throw new IllegalArgumentException("请填写支付金额");
    			}
    			ContractStagePayWayModel contractPayWay = new ContractStagePayWayModel();
    			String stageId = UUIDUtils.getId();
        		contractPayWay.setId(stageId);
        		contractPayWay.setCrewId(crewId);
        		contractPayWay.setContractId(contractId);
        		contractPayWay.setMoney(money);
        		contractPayWay.setRate(rate);
        		contractPayWay.setRemindTime(this.sdf1.parse(remindTimeStr));
        		contractPayWay.setStage(stage);
        		contractPayWay.setRemark(remark);
        		contractPayWay.setCreateTime(new Date());
        		
        		toAddContractStagePayWayList.add(contractPayWay);
        		
        		payDetailInfo.put("oldId", oldId);
        		payDetailInfo.put("id", stageId);
        		payDetailInfo.put("money", money);
        		payDetailInfo.put("paydate", remindTimeStr);
        		payDetailInfoList.add(payDetailInfo);
        		
        		if (StringUtils.isBlank(remindTimeStr)) {
        			continue;
        		}
        		remindTimeStr = DateUtils.getBeforeOrAfterDayDate(remindTimeStr, contractAdvanceRemindDays, null);
        		
        		
        		for (Map<String, Object> userInfo : userList) {
        			String userId = (String) userInfo.get("userId");
        			
        			//保存个人消息
            		MessageInfoModel messageInfo = new MessageInfoModel();
        			messageInfo.setId(UUIDUtils.getId());
        			messageInfo.setCrewId(crewId);
        			messageInfo.setSenderId(loginUserId);
        			messageInfo.setReceiverId(userId);
        			messageInfo.setType(MessageType.PaymentReceiptGet.getValue());
        			messageInfo.setBuzId(contractId);
        			messageInfo.setStatus(MessageInfoStatus.UnRead.getValue());
        			messageInfo.setTitle("合同付款提醒");
        			messageInfo.setContent("编号为"+ contractNo +"的合同付款提示。应付款"+ money +"元");
        			messageInfo.setRemindTime(this.sdf1.parse(remindTimeStr));
        			messageInfo.setCreateTime(new Date());
        			
        			messageInfoList.add(messageInfo);
        		}
    			
    		}
    		
    		//按月份
    		if (payWay == ContractPayWay.PerMonth.getValue() 
    				|| payWay == ContractPayWay.PerDayMonthSettle.getValue() 
    				|| payWay == ContractPayWay.PerDayRegularSettle.getValue()) {
    			if (paymentTermDetail.length < 5) {
        			throw new IllegalArgumentException("支付方式信息不全，请检查");
        		}
    			String remark = paymentTermDetail[0];
    			Double monthMoney = Double.parseDouble(paymentTermDetail[1]);
    			String startDateStr = paymentTermDetail[2];
    			String endDateStr = paymentTermDetail[3];
    			Integer monthPayDay = Integer.parseInt(paymentTermDetail[4]);
    			
    			if (monthMoney == null) {
    				throw new IllegalArgumentException("请填写月薪");
    			}
    			if (StringUtils.isBlank(startDateStr) || StringUtils.isBlank(endDateStr)) {
    				throw new IllegalArgumentException("请完善付款周期");
    			}
    			if (monthPayDay == null) {
    				throw new IllegalArgumentException("请填写每月发薪日");
    			}
        		
        		Date startDate = this.sdf1.parse(startDateStr);
        		Date endDate = this.sdf1.parse(endDateStr);
        		if (startDate.after(endDate)) {
        			throw new IllegalArgumentException("付款周期开始日期不能小于结束日期");
        		}
        		if (lastDate != null && !startDate.after(lastDate)) {
        			throw new IllegalArgumentException("多条支付方式付款周期有重叠或未按顺序设置，请检查");
        		} else {
        			lastDate = endDate;
        		}
        		
    			ContractMonthPaywayModel contractPayWay = new ContractMonthPaywayModel();
    			contractPayWay.setId(UUIDUtils.getId());
    			contractPayWay.setCrewId(crewId);
    			contractPayWay.setContractId(contractId);
    			contractPayWay.setMonthMoney(monthMoney);
    			contractPayWay.setStartDate(this.sdf1.parse(startDateStr));
    			contractPayWay.setEndDate(this.sdf1.parse(endDateStr));
    			contractPayWay.setMonthPayDay(monthPayDay);
    			contractPayWay.setRemark(remark);
    			contractPayWay.setCreateTime(new Date());
    			contractPayWay.setPayWayType(payWay);
    			
    			toAddContractMonthPayWayList.add(contractPayWay);
    		}
    	}
    	
    	//按月支付薪酬明细
    	List<ContractMonthPayDetailModel> monthPayDetailList = new ArrayList<ContractMonthPayDetailModel>();
    	if ((payWay == ContractPayWay.PerMonth.getValue() || payWay == ContractPayWay.PerDayMonthSettle.getValue() 
    			|| payWay == ContractPayWay.PerDayRegularSettle.getValue()) 
    			&& !StringUtils.isBlank(monthPayDetail)) {
    		String[] monthPayDetailArray = monthPayDetail.split("##");
    		for (String singleMonthPayDetail : monthPayDetailArray) {
    			payDetailInfo = new HashMap<String, Object>();
    			
    			String[] singleMonthPayDetailArray = singleMonthPayDetail.split("&&");
    			String monthStr = singleMonthPayDetailArray[0];
    			String startDateStr = singleMonthPayDetailArray[1];
    			String endDateStr = singleMonthPayDetailArray[2];
    			Double money = Double.parseDouble(singleMonthPayDetailArray[3]);
    			String payDateStr = singleMonthPayDetailArray[4];
    			String oldId = singleMonthPayDetailArray[5];
    			if (StringUtils.isBlank(monthStr) || StringUtils.isBlank(startDateStr) 
    					|| StringUtils.isBlank(endDateStr) || money == null 
    					|| StringUtils.isBlank(payDateStr)) {
    				throw new IllegalArgumentException("信息不全");
    			}
    			
    			ContractMonthPayDetailModel monthPayDetailModel = new ContractMonthPayDetailModel();
    			String monthId = UUIDUtils.getId();
    			monthPayDetailModel.setId(monthId);
    			monthPayDetailModel.setCrewId(crewId);
    			monthPayDetailModel.setContractId(contractId);
    			monthPayDetailModel.setMonth(this.sdf3.parse(monthStr));
    			monthPayDetailModel.setStartDate(this.sdf2.parse(startDateStr));
    			monthPayDetailModel.setEndDate(this.sdf2.parse(endDateStr));
    			monthPayDetailModel.setMoney(money);
    			monthPayDetailModel.setPayDate(this.sdf2.parse(payDateStr));
    			monthPayDetailModel.setCreateTime(new Date());
    			monthPayDetailModel.setPayWayType(payWay);
    			
    			monthPayDetailList.add(monthPayDetailModel);
    			
    			payDetailInfo.put("id", monthId);
        		payDetailInfo.put("money", money);
        		payDetailInfo.put("paydate", payDateStr);
        		payDetailInfo.put("oldId", oldId);
        		
        		payDetailInfoList.add(payDetailInfo);
    			
        		payDateStr = DateUtils.getBeforeOrAfterDayDate(payDateStr, contractAdvanceRemindDays, "yyyy/MM/dd");
    			for (Map<String, Object> userInfo : userList) {
        			String userId = (String) userInfo.get("userId");
        			
        			//保存个人消息
            		MessageInfoModel messageInfo = new MessageInfoModel();
        			messageInfo.setId(UUIDUtils.getId());
        			messageInfo.setCrewId(crewId);
        			messageInfo.setSenderId(loginUserId);
        			messageInfo.setReceiverId(userId);
        			messageInfo.setType(MessageType.PaymentReceiptGet.getValue());
        			messageInfo.setBuzId(contractId);
        			messageInfo.setStatus(MessageInfoStatus.UnRead.getValue());
        			messageInfo.setTitle("合同付款提醒");
        			messageInfo.setContent("编号为"+ contractNo +"的合同付款提示。应付款"+ money +"元");
        			messageInfo.setRemindTime(this.sdf2.parse(payDateStr));
        			messageInfo.setCreateTime(new Date());
        			
        			messageInfoList.add(messageInfo);
        		}
    		}
    	}
    	
    	//没有生成薪资明细时，系统自动生成明细数据
    	if ((payWay == ContractPayWay.PerMonth.getValue() || payWay == ContractPayWay.PerDayMonthSettle.getValue() 
    			|| payWay == ContractPayWay.PerDayRegularSettle.getValue()) 
    			&& StringUtils.isBlank(monthPayDetail)) {
    		List<Map<String, Object>> monthPayDetailMapList = new ArrayList<Map<String, Object>>();
    		
    		if (payWay == ContractPayWay.PerDayRegularSettle.getValue()) {
    			monthPayDetailMapList = this.genDayPayDetail(paymentTerm, crewId, payWay);
    		} else {
    			monthPayDetailMapList = this.genMonthPayDetail(paymentTerm, crewId, payWay);
    		}
    		
    		for (Map<String, Object> monthPayDetailMap : monthPayDetailMapList) {
    			payDetailInfo = new HashMap<String, Object>();
    			
    			String month = (String) monthPayDetailMap.get("month");
    			String startDate = (String) monthPayDetailMap.get("startDate");
    			String endDate = (String) monthPayDetailMap.get("endDate");
    			Double money = (Double) monthPayDetailMap.get("money");
    			String payDate = (String) monthPayDetailMap.get("payDate");
    			String oldId  = (String) monthPayDetailMap.get("id");
    			ContractMonthPayDetailModel monthPayDetailModel = new ContractMonthPayDetailModel();
    			String monthId = UUIDUtils.getId();
    			monthPayDetailModel.setId(monthId);
    			monthPayDetailModel.setCrewId(crewId);
    			monthPayDetailModel.setContractId(contractId);
    			monthPayDetailModel.setMonth(this.sdf3.parse(month));
    			monthPayDetailModel.setStartDate(this.sdf2.parse(startDate));
    			monthPayDetailModel.setEndDate(this.sdf2.parse(endDate));
    			monthPayDetailModel.setMoney(money);
    			monthPayDetailModel.setPayDate(this.sdf2.parse(payDate));
    			monthPayDetailModel.setCreateTime(new Date());
    			monthPayDetailModel.setPayWayType(payWay);
    			
    			monthPayDetailList.add(monthPayDetailModel);
    			
    			payDetailInfo.put("oldId", oldId);
    			payDetailInfo.put("id", monthId);
        		payDetailInfo.put("money", money);
        		payDetailInfo.put("paydate", payDate);
        		payDetailInfoList.add(payDetailInfo);
    			
        		payDate = DateUtils.getBeforeOrAfterDayDate(payDate, contractAdvanceRemindDays, "yyyy/MM/dd");
    			for (Map<String, Object> userInfo : userList) {
        			String userId = (String) userInfo.get("userId");
        			
        			//保存个人消息
            		MessageInfoModel messageInfo = new MessageInfoModel();
        			messageInfo.setId(UUIDUtils.getId());
        			messageInfo.setCrewId(crewId);
        			messageInfo.setSenderId(loginUserId);
        			messageInfo.setReceiverId(userId);
        			messageInfo.setType(MessageType.PaymentReceiptGet.getValue());
        			messageInfo.setBuzId(contractId);
        			messageInfo.setStatus(MessageInfoStatus.UnRead.getValue());
        			messageInfo.setTitle("合同付款提醒");
        			messageInfo.setContent("编号为"+ contractNo +"的合同付款提示。应付款"+ money +"元");
        			messageInfo.setRemindTime(this.sdf2.parse(payDate));
        			messageInfo.setCreateTime(new Date());
        			
        			messageInfoList.add(messageInfo);
        		}
    		}
    	}
    	
    	if (toAddContractStagePayWayList.size() > 0) {
    		this.contractStagePayWayDao.addBatch(toAddContractStagePayWayList, ContractStagePayWayModel.class);
    	}
    	if (toAddContractMonthPayWayList.size() > 0) {
    		this.contractMonthPaywayDao.addBatch(toAddContractMonthPayWayList, ContractMonthPaywayModel.class);
    	}
    	if (monthPayDetailList.size() > 0) {
    		this.contractMonthPayDetailDao.addBatch(monthPayDetailList, ContractMonthPayDetailModel.class);
    	}
    	if (messageInfoList.size() > 0) {
    		this.messageInfoService.addMany(messageInfoList);
    	}
    	
    	return payDetailInfoList;
	}
	
	/**
	 * 获取合同与付款方式关联
	 * @param contractId
	 * @param crewId
	 * @return
	 */
	public List<ContractStagePayWayModel> queryByContractId(String contractId, String crewId) {
		return this.contractStagePayWayDao.queryByContractId(contractId, crewId);
	}
	
	/**
	 * 生成“按日支付（定期结算）”支付明细
	 * @param paymentTerm
	 * @param crewId
	 * @param payWay
	 * @return
	 * @throws ParseException 
	 */
	public List<Map<String, Object>> genDayPayDetail(String paymentTerm, String crewId, int payWay) throws ParseException {
		List<Map<String, Object>> dayPayDetailList = new ArrayList<Map<String, Object>>();
		String[] paymentTermArray = paymentTerm.split("##");
		
		Date lastDate = null;
		for (String singlePaymentTerm : paymentTermArray) {
			String[] monthPaywayArray = singlePaymentTerm.split("&&");
			
			/*
			 * 基本信息校验
			 */
    		if (monthPaywayArray.length < 5) {
    			throw new IllegalArgumentException("信息不全");
    		}
    		if (StringUtils.isBlank(monthPaywayArray[1])) {
    			throw new IllegalArgumentException("请填写日薪");
    		}
    		if (StringUtils.isBlank(monthPaywayArray[2]) || StringUtils.isBlank(monthPaywayArray[3])) {
    			throw new IllegalArgumentException("请填写付款周期");
    		}
    		if (StringUtils.isBlank(monthPaywayArray[4])) {
    			throw new IllegalArgumentException("请填写结算周期");
    		}
    		
    		Double dayMoney = Double.parseDouble(monthPaywayArray[1]);	//日薪
    		Date startDate = this.sdf1.parse(monthPaywayArray[2]);	//开始计算日期
    		Date endDate = this.sdf1.parse(monthPaywayArray[3]);	//结束计算日期
    		int payRange = Integer.parseInt(monthPaywayArray[4]);	//支付周期
    		
    		if (startDate.after(endDate)) {
    			throw new IllegalArgumentException("付款周期开始日期不能小于结束日期");
    		}
    		if (lastDate != null && !startDate.after(lastDate)) {
    			throw new IllegalArgumentException("多条支付方式付款周期有重叠或未按顺序设置，请检查");
    		} else {
    			lastDate = endDate;
    		}
    		
    		/*
    		 * 计算付款详情
    		 */
    		//计算开始日期和结束日期之间需要付款多少次
    		int betweenDays = DateUtils.daysBetween(startDate, endDate) + 1;
    		int payCount = betweenDays / payRange;
    		if (betweenDays % payRange != 0) {
    			payCount += 1;
    		}
    		
    		//计算出每次付款的详细信息（月份、开始日期、结束日期、金额）
    		for (int i = 1; i <= payCount; i++) {
    			
    			//计算这次薪酬开始日期
    			Calendar startCal = Calendar.getInstance();
				startCal.setTime(startDate);
				startCal.add(Calendar.DAY_OF_MONTH, (i - 1) * payRange);
				
				//计算这次薪酬结束日期
				Calendar endCal = Calendar.getInstance();
				endCal.setTime(startDate);
				endCal.add(Calendar.DAY_OF_MONTH, i * payRange - 1);
				
				//如果这次结束日期超过薪酬结束计算日期，则把结束日期设置成薪酬结束计算日期
				if (endCal.getTime().after(endDate)) {
					endCal.setTime(endDate);
				}
    			
				String month = this.sdf3.format(endCal.getTime());
    			String myStartDate = this.sdf2.format(startCal.getTime());
    			String myEndDate = this.sdf2.format(endCal.getTime());
    			Double money = BigDecimalUtil.multiply(dayMoney, DateUtils.daysBetween(startCal.getTime(), endCal.getTime()) + 1);
    			String payDate = myEndDate;
    			
    			
    			Map<String, Object> dayPayDetailMap = new HashMap<String, Object>();
    			dayPayDetailMap.put("month", month);
    			dayPayDetailMap.put("startDate", myStartDate);
    			dayPayDetailMap.put("endDate", myEndDate);
    			dayPayDetailMap.put("money", money);
    			dayPayDetailMap.put("payDate", payDate);
    			dayPayDetailMap.put("id", "");
    			
    			dayPayDetailList.add(dayPayDetailMap);
    		}
		}
		
		return dayPayDetailList;
	}
	
	/**
	 * 生成按月支付薪酬详情
	 * @param paymentTerm
	 * @return
	 * @throws Exception 
	 */
	public List<Map<String, Object>> genMonthPayDetail(String paymentTerm, String crewId, int payWay) throws Exception {
		FinanceSettingModel financeSetting = this.financeSettingService.queryByCrewId(crewId);
    	if (financeSetting == null) {
    		financeSetting = this.financeSettingService.initFinanceSetting(crewId);
    	}
		int monthDayType = financeSetting.getMonthDayType();
		
		String[] paymentTermArray = paymentTerm.split("##");
    	
    	List<Map<String, Object>> monthPayDetailList = new ArrayList<Map<String, Object>>();
    	
    	Date lastDate = null;
    	
    	for (String singlePaymentTerm : paymentTermArray) {
    		String[] monthPaywayArray = singlePaymentTerm.split("&&");
    		if (monthPaywayArray.length < 5) {
    			throw new IllegalArgumentException("信息不全");
    		}
    		if (StringUtils.isBlank(monthPaywayArray[1])) {
    			throw new IllegalArgumentException("请填写月薪");
    		}
    		if (StringUtils.isBlank(monthPaywayArray[2]) || StringUtils.isBlank(monthPaywayArray[3])) {
    			throw new IllegalArgumentException("请填写付款周期");
    		}
    		if (StringUtils.isBlank(monthPaywayArray[4])) {
    			throw new IllegalArgumentException("请填写发薪日");
    		}
    		
    		Double monthMoney = Double.parseDouble(monthPaywayArray[1]);
    		Date startDate = this.sdf1.parse(monthPaywayArray[2]);
    		Date endDate = this.sdf1.parse(monthPaywayArray[3]);
    		int monthPayDay = Integer.parseInt(monthPaywayArray[4]);
    		
    		if (startDate.after(endDate)) {
    			throw new IllegalArgumentException("付款周期开始日期不能小于结束日期");
    		}
    		if (lastDate != null && !startDate.after(lastDate)) {
    			throw new IllegalArgumentException("多条支付方式付款周期有重叠或未按顺序设置，请检查");
    		} else {
    			lastDate = endDate;
    		}
    		
    		Calendar startCal = Calendar.getInstance();
    		startCal.setTime(startDate);
    		
    		Calendar endCal = Calendar.getInstance();
    		endCal.setTime(endDate);
    		
    		int startYear = startCal.get(Calendar.YEAR);
    		int endYear = endCal.get(Calendar.YEAR);
    		
    		//跨年的情况
			int yearLength = endYear - startYear;
			
			for (int i = 0; i <= yearLength; i++) {
				Date myStartDate = null;
				Date myEndDate = null;

				startCal.set(Calendar.YEAR, startYear + i);
				endCal.set(Calendar.YEAR, startYear + i);
				if (i != 0) {
					startCal.set(Calendar.MONTH, 0);
					startCal.set(Calendar.DAY_OF_MONTH, 1);
    				myStartDate = startCal.getTime();
				} else {
					myStartDate = startDate;
				}
    			
    			if (i != yearLength) {
        			endCal.set(Calendar.MONTH, 11);
        			endCal.set(Calendar.DAY_OF_MONTH, 31);
        			myEndDate = endCal.getTime();
    			} else {
    				myEndDate = endDate;
    			}
    			
    			monthPayDetailList.addAll(this.genSingleYearPayDetail(myStartDate, myEndDate, monthMoney, monthPayDay, endDate, monthDayType, payWay));
			}
    	}
    	
    	//排序
    	Collections.sort(monthPayDetailList, new Comparator<Map<String, Object>>() {

			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				String o1StartDateStr = (String) o1.get("startDate");
				String o2StartDateStr = (String) o2.get("startDate");
				
				Date o1StartDate = null;
				Date o2StartDate = null;
				try {
					o1StartDate = sdf2.parse(o1StartDateStr);
					o2StartDate = sdf2.parse(o2StartDateStr);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				
				return o1StartDate.compareTo(o2StartDate);
			}
		});
    	
    	return monthPayDetailList;
	}
	
	/**
	 * 获取单年的薪酬详情
	 * @param startDate	开始日期
	 * @param endDate	结束日期
	 * @param monthMoney	月薪
	 * @param monthPayDay	付款日
	 * @param lastDate	付款周期中最后一天
	 * @param monthDayType	每月天数类型，见：MonthDayType枚举类
	 * @param payWay 支付方式
	 * @return
	 * @throws ParseException
	 */
	private List<Map<String, Object>> genSingleYearPayDetail(Date startDate, Date endDate, Double monthMoney, int monthPayDay, Date lastDate, int monthDayType, int payWay) throws ParseException {
		List<Map<String, Object>> monthPayDetailList = new ArrayList<Map<String, Object>>();
		
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startDate);
		
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);
		
		int startMonth = startCal.get(Calendar.MONTH);
		int endMonth = endCal.get(Calendar.MONTH);
		
		int monthLength = endMonth - startMonth;
		for (int i = 0; i <= monthLength; i++) {
			Date myStartDate = null;
			Date myEndDate = null;
			
			startCal.set(Calendar.DAY_OF_MONTH, 1);
			endCal.set(Calendar.DAY_OF_MONTH, 1);
			startCal.set(Calendar.MONTH, startMonth + i);
			endCal.set(Calendar.MONTH, startMonth + i);
			
			int myMonthMaxDay = startCal.getActualMaximum(Calendar.DAY_OF_MONTH);
			
			if (i != 0) {
				startCal.set(Calendar.DAY_OF_MONTH, 1);
				myStartDate = startCal.getTime();
			} else {
				myStartDate = startDate;
			}
			
			if (i != monthLength) {
				endCal.set(Calendar.DAY_OF_MONTH, myMonthMaxDay);
				myEndDate = endCal.getTime();
			} else {
				myEndDate = endDate;
			}
			
			//计算单月的工资
			monthPayDetailList.add(this.genSingleMonthPayDetail(myStartDate, myEndDate, monthMoney, monthPayDay, lastDate, monthDayType, payWay));
		}
		return monthPayDetailList;
	}
	
	/**
	 * 获取单月的薪酬详情
	 * @param startDate 开始日期
	 * @param endDate	结束日期
	 * @param monthMoney	月薪
	 * @param monthPayDay	付款日
	 * @param lastDate	付款周期中最后一天
	 * @param monthDayType	每月天数类型，见：MonthDayType枚举类
	 * @param payWay 支付方式
	 * @return
	 * @throws ParseException
	 */
	private Map<String, Object> genSingleMonthPayDetail(Date startDate, Date endDate, Double monthMoney, int monthPayDay, Date lastDate, int monthDayType, int payWay) throws ParseException {
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startDate);
		
		int year = startCal.get(Calendar.YEAR);
		int monthMaxDay = startCal.getActualMaximum(Calendar.DAY_OF_MONTH);	//当月最大天数
		int month = startCal.get(Calendar.MONTH);
		
		Double dayMoney = 0.0;
		if (monthDayType == MonthDayType.NatureDay.getValue()) {
			dayMoney = BigDecimalUtil.divide(monthMoney, monthMaxDay);	//每天的薪酬
		} else {
			dayMoney = BigDecimalUtil.divide(monthMoney, 30);	//每天的薪酬
		}
		
		//所付的薪酬月份
		Calendar moneyMonth = Calendar.getInstance();
		moneyMonth.set(Calendar.YEAR, year);
		moneyMonth.set(Calendar.MONTH, month);
		moneyMonth.set(Calendar.DAY_OF_MONTH, 1);
		String moneyMonthStr = this.sdf3.format(moneyMonth.getTime());
		
		String startDateStr = this.sdf2.format(startDate);	//薪酬开始计算日期
		String endDateStr = this.sdf2.format(endDate);	//薪酬结束计算日期
		Double money = monthMoney;	//薪酬
		
		int workDays = DateUtils.daysBetween(startDate, endDate) + 1;	//工作的天数
		if (workDays != monthMaxDay) {
			money = BigDecimalUtil.multiply(workDays, dayMoney);
		}
		if (payWay == ContractPayWay.PerDayMonthSettle.getValue()) {
			money = BigDecimalUtil.multiply(workDays, monthMoney);
		}
		
		//付款日
		Calendar payDateCal = Calendar.getInstance();
		payDateCal.set(Calendar.DAY_OF_MONTH, 1);
		payDateCal.set(Calendar.YEAR, year);
		payDateCal.set(Calendar.MONTH, month + 1);
		int payMonthMaxDay = payDateCal.getActualMaximum(Calendar.DAY_OF_MONTH);	//当月最大天数
		if (payMonthMaxDay < monthPayDay) {
			monthPayDay = payMonthMaxDay;
		}
		payDateCal.set(Calendar.DAY_OF_MONTH, monthPayDay);
		
		Date payDateDate = payDateCal.getTime();
		//如果付款日在超出付款周期，则在付款周期中最后日期付款
		if (payDateDate.after(lastDate)) {
			payDateDate = lastDate;
		}
		String payDateStr = this.sdf2.format(payDateDate);
		
		Map<String, Object> startMonthPayDetailMap = new HashMap<String, Object>();
		startMonthPayDetailMap.put("month", moneyMonthStr);
		startMonthPayDetailMap.put("startDate", startDateStr);
		startMonthPayDetailMap.put("endDate", endDateStr);
		startMonthPayDetailMap.put("money", money);
		startMonthPayDetailMap.put("payDate", payDateStr);
		startMonthPayDetailMap.put("id", "");
		return startMonthPayDetailMap;
	}
	
	/**
	 * 根据多个条件查询按月支付详情信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ContractMonthPayDetailModel> queryMonthPayDetailManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.contractMonthPayDetailDao.queryManyByMutiCondition(conditionMap, page);
	}
	
	/**
	 * 根据多个条件查询按月支付方式信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ContractMonthPaywayModel> queryMonthPaywayManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.contractMonthPaywayDao.queryManyByMutiCondition(conditionMap, page);
	}
}
