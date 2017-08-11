package com.xiaotu.makeplays.finance.service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.attachment.model.constants.AttachmentBuzType;
import com.xiaotu.makeplays.attachment.service.AttachmentService;
import com.xiaotu.makeplays.finance.dao.ContractWorkerDao;
import com.xiaotu.makeplays.finance.dao.PaymentInfoDao;
import com.xiaotu.makeplays.finance.model.ContractWorkerModel;
import com.xiaotu.makeplays.finance.model.constants.ContractType;
import com.xiaotu.makeplays.message.dao.MessageInfoDao;
import com.xiaotu.makeplays.message.service.MessageInfoService;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 职员合同
 * @author xuchangjian 2016-8-3下午5:11:35
 */
@Service
public class ContractWorkerService {
	
	@Autowired
	private ContractToPaidService contractToPaidService;
	
	@Autowired
	private ContractWorkerDao contractWorkerDao;
	
	@Autowired
	private AttachmentService attachmentService;
	
	@Autowired
	private MessageInfoDao messageInfoDao;
	
	@Autowired
	private ContractPayWayService contractPayWayService;
	
	@Autowired
	private PaymentInfoDao paymentInfoDao;
	
	@Autowired
	private MessageInfoService messageInfoService;
	
	/**
	 * 根据多个条件查询职员合同信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ContractWorkerModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.contractWorkerDao.queryManyByMutiCondition(conditionMap, page);
	}
	
	/**
	 * 根据财务科目查询职员合同
	 * @param crewId
	 * @param financeSubjId
	 * @return
	 */
	public List<ContractWorkerModel> queryByFinanceSubjId(String crewId, String financeSubjId) {
		return this.contractWorkerDao.queryByFinanceSubjId(crewId, financeSubjId);
	}
	
	/**
	 * 根据剧组ID查询职员合同
	 * @param crewId
	 * @return
	 */
	public List<ContractWorkerModel> queryByCrewId(String crewId) {
		return this.contractWorkerDao.queryByCrewId(crewId);
	}
	
	/**
	 * 查询剧组下带有财务科目的职员合同
	 * @param crewId
	 * @return
	 */
	public List<ContractWorkerModel> queryFinanContract(String crewId) {
		return this.contractWorkerDao.queryFinanContract(crewId);
	}
	
	/**
	 * 查询职员合同的预算
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryContractWorkerBudget(String crewId) {
		return this.contractWorkerDao.queryContractWorkerBudget(crewId);
	}
	
	/**
	 * 根据高级查询条件查询职员合同
	 * @param workerNames	职员姓名，多个以逗号隔开
	 * @param department	部门职务
	 * @param paymentTerm	支付条件
	 * @param remark	备注
	 * @return 合同ID，职员名称，合同编号，部门职务，总金额，已付金额，未付金额，合同关联货币ID，编码，货币汇率
	 */
	public List<Map<String, Object>> queryByAnvanceCondition(String crewId, String workerNames, String department, String financeSubjIds, Integer payWay, String paymentTerm, String remark, String paymentStartDate, String paymentEndDate) {
		return this.contractWorkerDao.queryByAnvanceCondition(crewId, workerNames, department, financeSubjIds, payWay, paymentTerm, remark, paymentStartDate, paymentEndDate);
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
	 * @param attpackId	附件包ID
	 * @param remark	备注
	 * @param attpackId 附件包ID
	 * @return
	 * @throws ParseException 
	 */
	public ContractWorkerModel saveContractInfo(String crewId, String loginUserId, String contractId, String contractNo,
			String contractDate, String workerName, String department, 
			String phone, Integer identityCardType, String identityCardNumber, String enterDate, String leaveDate,
			String currencyId, Double totalMoney, String paymentTerm, String monthPayDetail,
			String bankName, String bankAccountName, String bankAccountNumber, 
			Integer payWay, String financeSubjId, String financeSubjName, String remark, String attpackId) throws ParseException, IllegalArgumentException, Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		//是否是添加操作
		boolean isAdd = false;
		
		//自动生成合同号
		if (StringUtils.isBlank(contractNo)) {
			contractNo = this.genContractNo(crewId);
		}
    	
		ContractWorkerModel contractWorker = new ContractWorkerModel();
		if (StringUtils.isBlank(contractId)) {
			isAdd = true;
			contractWorker.setContractId(UUIDUtils.getId());
		} else {
			contractWorker = this.contractWorkerDao.queryById(crewId, contractId);
		}
		
		
		contractWorker.setCrewId(crewId);
		contractWorker.setContractNo(contractNo);
		contractWorker.setContractDate(sdf.parse(contractDate));
		contractWorker.setWorkerName(workerName);
		contractWorker.setDepartment(department);
		contractWorker.setPhone(phone);
		contractWorker.setIdentityCardType(identityCardType);
		contractWorker.setIdentityCardNumber(identityCardNumber);
		if (!StringUtils.isBlank(enterDate)) {
			contractWorker.setEnterDate(sdf.parse(enterDate));
		}
		if (!StringUtils.isBlank(leaveDate)) {
			contractWorker.setLeaveDate(sdf.parse(leaveDate));
		}
		contractWorker.setCurrencyId(currencyId);
		contractWorker.setTotalMoney(totalMoney);
		contractWorker.setPaymentTerm(paymentTerm);
		contractWorker.setBankName(bankName);
		contractWorker.setBankAccountName(bankAccountName);
		contractWorker.setBankAccountNumber(bankAccountNumber);
		contractWorker.setPayWay(payWay);
		contractWorker.setFinanceSubjId(financeSubjId);
		contractWorker.setFinanceSubjName(financeSubjName);
		
		if (StringUtils.isBlank(attpackId)) {
			attpackId = this.attachmentService.createNewPacket(crewId, AttachmentBuzType.Contract.getValue());
		}
		contractWorker.setAttpackId(attpackId);
		
		contractWorker.setRemark(remark);
		
		//新增\修改职员合同
		if (isAdd) {
			this.contractWorkerDao.add(contractWorker);
		} else {
			this.contractWorkerDao.updateWithNull(contractWorker, "contractId");
		}
		//如果是修改，先删除支付方式
		if (!isAdd) {
			this.contractPayWayService.deleteByContractId(contractId, crewId);
		}
    	
		//保存支付方式
    	List<Map<String, Object>> paydetailInfoList = this.contractPayWayService.saveByPaymentTerm(crewId, loginUserId, contractWorker.getContractId(), contractNo, paymentTerm, monthPayDetail, payWay);
    	
    	//保存待付信息
    	contractToPaidService.saveContractToPaid(contractWorker.getContractId(),contractNo,currencyId,crewId, department, workerName, financeSubjId,"1",paydetailInfoList);
    	
		return contractWorker;
	}
	
	/**
	 * 保存批量导入的合同信息
	 * @param crewId
	 * @param contractDate
	 * @param workerName
	 * @param department
	 * @param phone
	 * @param identityCardType
	 * @param identityCardNumber
	 * @param enterDate
	 * @param leaveDate
	 * @param currencyId
	 * @param totalMoney
	 * @param bankName
	 * @param bankAccountName
	 * @param bankAccountNumber
	 * @param financeSubjId
	 * @param financeSubjName
	 * @param remark
	 * @throws Exception 
	 */
	public void saveImportContractInfo(String crewId, Boolean isCover, Boolean isRepeat, String customContractNo, Date contractDate, String workerName, String department, 
			String phone, Integer identityCardType, String identityCardNumber, Date enterDate, Date leaveDate,
			String currencyId, Double totalMoney, String bankName, String bankAccountName, String bankAccountNumber, 
			String financeSubjId, String financeSubjName, String remark) throws Exception {
		
		if (!isRepeat) { //新增数据
			//自动生成合同号
			String contractNo = this.genContractNo(crewId);
			String contractId = UUIDUtils.getId();
			
			ContractWorkerModel contractWorker = new ContractWorkerModel();
			contractWorker.setContractId(contractId);
			contractWorker.setCrewId(crewId);
			contractWorker.setContractNo(contractNo);
			contractWorker.setContractDate(contractDate);
			
			contractWorker.setWorkerName(workerName);
			contractWorker.setDepartment(department);
			contractWorker.setPhone(phone);
			contractWorker.setIdentityCardType(identityCardType);
			contractWorker.setIdentityCardNumber(identityCardNumber);
			
			if (enterDate != null) {
				contractWorker.setEnterDate(enterDate);
			}
			if (leaveDate != null) {
				contractWorker.setLeaveDate(leaveDate);
			}
			contractWorker.setCurrencyId(currencyId);
			contractWorker.setTotalMoney(totalMoney);
			contractWorker.setBankName(bankName);
			
			contractWorker.setBankAccountName(bankAccountName);
			contractWorker.setBankAccountNumber(bankAccountNumber);
			contractWorker.setFinanceSubjId(financeSubjId);
			contractWorker.setFinanceSubjName(financeSubjName);
			contractWorker.setRemark(remark);
			contractWorker.setCustomContractNo(customContractNo);
			
			this.contractWorkerDao.add(contractWorker);
		}else {
			//判断重复数据是跳过还是覆盖
			if (isCover) { //覆盖（更新数据）
				//根据用户定义的合同号，查询出已经存在的合同信息
				//根据多个条件查询是否是重复数据
				Map<String, Object> conditionMap = new HashMap<String, Object>();
				conditionMap.put("workerName", workerName);
				conditionMap.put("phone", phone);
				conditionMap.put("crewId", crewId);
				conditionMap.put("customContractNo", customContractNo);
				List<ContractWorkerModel> workerByCustomNo = this.contractWorkerDao.queryManyByMutiCondition(conditionMap, null);
				ContractWorkerModel workerModel = workerByCustomNo.get(0);
				workerModel.setContractDate(contractDate);
				
				workerModel.setWorkerName(workerName);
				workerModel.setDepartment(department);
				workerModel.setPhone(phone);
				workerModel.setIdentityCardType(identityCardType);
				workerModel.setIdentityCardNumber(identityCardNumber);
				
				if (enterDate != null) {
					workerModel.setEnterDate(enterDate);
				}
				if (leaveDate != null) {
					workerModel.setLeaveDate(leaveDate);
				}
				workerModel.setCurrencyId(currencyId);
				workerModel.setTotalMoney(totalMoney);
				workerModel.setBankName(bankName);
				
				workerModel.setBankAccountName(bankAccountName);
				workerModel.setBankAccountNumber(bankAccountNumber);
				workerModel.setFinanceSubjId(financeSubjId);
				workerModel.setFinanceSubjName(financeSubjName);
				workerModel.setRemark(remark);
				workerModel.setCustomContractNo(customContractNo);
				//更新数据
				this.contractWorkerDao.updateWithNull(workerModel, "contractId");
			}
		}
	}
	
	/**
	 * 生成合同编号
	 * @param crewId
	 * @return
	 */
	public String genContractNo (String crewId) {
		String newContractNo = "";
		
		String maxContractNo = this.contractWorkerDao.queryMaxContractNo(crewId);
		if (StringUtils.isBlank(maxContractNo)) {
			newContractNo = Constants.WORK_CONTRACT_PREFIX + "0001";
		} else {
			DecimalFormat df = new DecimalFormat("0000");
			
			Integer contractNoNum = Integer.parseInt(maxContractNo.substring(Constants.WORK_CONTRACT_PREFIX.length()));
			
			newContractNo = Constants.WORK_CONTRACT_PREFIX + df.format(contractNoNum + 1);
		}
		
		return newContractNo;
	}
	
	/**
	 * 删除合同
	 * @param contractId
	 * @throws Exception 
	 */
	public void deleteContract(String crewId, String contractId) throws Exception {
		ContractWorkerModel contractWorker = this.queryById(crewId, contractId);
		
		//删除待付信息
    	contractToPaidService.deleteContractToPaidInfoWhenDeleteContract(contractId, "1");
		
		//删除合同记录
    	this.contractWorkerDao.deleteOne(contractId, "contractId", ContractWorkerModel.TABLE_NAME);
    	
    	//删除支付方式记录
    	this.contractPayWayService.deleteByContractId(contractId, crewId);
    	
    	//清空删除和借款单的关联
    	this.paymentInfoDao.deleteByContractInfo(ContractType.Worker.getValue(), contractId);
    	
    	//删除合同下的附件
    	if (!StringUtils.isBlank(contractWorker.getAttpackId())) {
    		this.attachmentService.deleteByPackId(contractWorker.getAttpackId());
    	}
    	
    	//删除合同对应的消息提醒
    	this.messageInfoService.deleteByBuzId(contractId);
	}
	
	/**
	 * 根据ID查询合同信息
	 * @param crewId
	 * @param contractId
	 * @return
	 * @throws Exception
	 */
	public ContractWorkerModel queryById (String crewId, String contractId) throws Exception {
		return this.contractWorkerDao.queryById(crewId, contractId);
	}
	
	/**、
	 * 根据姓名查询出职务信息
	 * @param contcatNam
	 * @return
	 */
	public List<Map<String, Object>> queryDepatmentListByContactName(String contactName, String crewId){
		return this.contractWorkerDao.queryDepartmentByContactName(contactName, crewId);
	}
	
	/**
	 * 查询职员合同的付款单列表
	 * @param crewId
	 * @param financeSubjId
	 * @return
	 */
	public List<Map<String, Object>> queryContractWorkerPaymentList(String crewId, String financeSubjId){
		return this.contractWorkerDao.queryContractWorkerPayemtnList(crewId, financeSubjId);
	}
	
}
