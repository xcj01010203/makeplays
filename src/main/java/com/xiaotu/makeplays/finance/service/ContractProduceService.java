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
import com.xiaotu.makeplays.finance.dao.ContractProduceDao;
import com.xiaotu.makeplays.finance.dao.PaymentInfoDao;
import com.xiaotu.makeplays.finance.model.ContractProduceModel;
import com.xiaotu.makeplays.finance.model.constants.ContractType;
import com.xiaotu.makeplays.message.service.MessageInfoService;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 生产合同
 * @author xuchangjian 2016-8-3下午5:10:21
 */
@Service
public class ContractProduceService {

	@Autowired
	private ContractToPaidService contractToPaidService;
	
	
	@Autowired
	private ContractProduceDao contractProduceDao;
	
	@Autowired
	private AttachmentService attachmentService;
	
	@Autowired
	private ContractPayWayService contractPayWayService;
	
	@Autowired
	private PaymentInfoDao paymentInfoDao;
	
	@Autowired
	private MessageInfoService messageInfoService;
	
	/**
	 * 根据多个条件查询生产合同信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ContractProduceModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.contractProduceDao.queryManyByMutiCondition(conditionMap, page);
	}
	
	/**
	 * 根据财务科目ID查询合同
	 * @param crewId
	 * @param financeSubjId
	 * @return
	 */
	public List<ContractProduceModel> queryByFinanceSubjId(String crewId, String financeSubjId) {
		return this.contractProduceDao.queryByFinanceSubjId(crewId, financeSubjId);
	}
	
	/**
	 * 根据剧组ID查询合同
	 * @param crewId
	 * @return
	 */
	public List<ContractProduceModel> queryByCrewId(String crewId) {
		return this.contractProduceDao.queryByCrewId(crewId);
	}
	
	/**
	 * 根据剧组ID查询合同
	 * @param crewId
	 * @return
	 */
	public List<ContractProduceModel> queryFinanContract(String crewId) {
		return this.contractProduceDao.queryFinanContract(crewId);
	}
	
	/**
	 * 查询制作合同的预算
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryContractProduceBudget(String crewId) {
		return this.contractProduceDao.queryContractProduceBudget(crewId);
	}
	
	/**
	 * 根据高级查询条件查询职员合同
	 * @param companys	单位
	 * @param contactPersons	联系人，多个以逗号隔开
	 * @param paymentTerm	支付条件，多个以逗号隔开
	 * @param remark	备注
	 * @return 合同ID，职员名称，合同编号，部门职务，总金额，已付金额，未付金额，合同关联货币ID，编码，货币汇率
	 */
	public List<Map<String, Object>> queryByAnvanceCondition(String crewId, String companys, String contactPersons, String financeSubjIds, Integer payWay, String paymentTerm, String remark, String paymentStartDate, String paymentEndDate) {
		return this.contractProduceDao.queryByAnvanceCondition(crewId, companys, contactPersons, financeSubjIds, payWay, paymentTerm, remark, paymentStartDate, paymentEndDate);
	}
	
	/**
	 * 保存职员合同信息
	 * @param request
	 * @param contractId	合同ID
	 * @param contractNo	合同编号
	 * @param contractDate	支付日期
	 * @param company	对方公司
	 * @param contactPerson	联系人
	 * @param phone	联系电话
	 * @param idNumber	身份证
	 * @param startDate	合同开始时间
	 * @param endDate	合同结束时间
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
	 * @throws ParseException 
	 */
	public ContractProduceModel saveContractInfo(String crewId, String loginUserId, String contractId, String contractNo,
			String contractDate, String company, String contactPerson, 
			String phone, Integer identityCardType, String identityCardNumber, String startDate, String endDate,
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

		ContractProduceModel contractProduce = new ContractProduceModel();
		if (StringUtils.isBlank(contractId)) {
			isAdd = true;
			contractProduce.setContractId(UUIDUtils.getId());
		} else {
			contractProduce = this.contractProduceDao.queryById(crewId, contractId);
		}
		
		contractProduce.setCrewId(crewId);
		contractProduce.setContractNo(contractNo);
		contractProduce.setContractDate(sdf.parse(contractDate));
		contractProduce.setCompany(company);
		contractProduce.setContactPerson(contactPerson);
		contractProduce.setPhone(phone);
		contractProduce.setIdentityCardType(identityCardType);
		contractProduce.setIdentityCardNumber(identityCardNumber);
		if (!StringUtils.isBlank(startDate)) {
			contractProduce.setStartDate(sdf.parse(startDate));
		}
		if (!StringUtils.isBlank(endDate)) {
			contractProduce.setEndDate(sdf.parse(endDate));
		}
		contractProduce.setCurrencyId(currencyId);
		contractProduce.setTotalMoney(totalMoney);
		contractProduce.setPaymentTerm(paymentTerm);
		contractProduce.setBankName(bankName);
		contractProduce.setBankAccountName(bankAccountName);
		contractProduce.setBankAccountNumber(bankAccountNumber);
		contractProduce.setPayWay(payWay);
		contractProduce.setFinanceSubjId(financeSubjId);
		contractProduce.setFinanceSubjName(financeSubjName);
		
		if (StringUtils.isBlank(attpackId)) {
			attpackId = this.attachmentService.createNewPacket(crewId, AttachmentBuzType.Contract.getValue());
		}
		contractProduce.setAttpackId(attpackId);
		
		contractProduce.setRemark(remark);
		
		//新增\修改职员合同
		if (isAdd) {
			this.contractProduceDao.add(contractProduce);
		} else {
			this.contractProduceDao.updateWithNull(contractProduce, "contractId");
		}
		
		//如果是修改，先删除支付方式
		if (!isAdd) {
			this.contractPayWayService.deleteByContractId(contractId, crewId);
		}
    	
		//保存支付方式
    	List<Map<String, Object>> ids = this.contractPayWayService.saveByPaymentTerm(crewId, loginUserId, contractProduce.getContractId(), contractNo, paymentTerm, monthPayDetail, payWay);
		
		
    	//保存待付信息
    	contractToPaidService.saveContractToPaid(contractProduce.getContractId(),contractNo,currencyId,crewId, contactPerson, company, financeSubjId,"3",ids);
    	
    	
    	return contractProduce;
	}
	
	/**
	 * 保存导入的制作合同信息
	 * @param crewId
	 * @param isCover
	 * @param isRepeat
	 * @param customContractNo
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
	public void saveImportContractProduce(String crewId, Boolean isCover, Boolean isRepeat, String customContractNo, Date contractDate, 
			String company, String contactPerson, String phone, Integer identityCardType, String identityCardNumber, Date startDate, Date endDate,
			String currencyId, Double totalMoney, String bankName, String bankAccountName, String bankAccountNumber, 
			String financeSubjId, String financeSubjName, String remark) throws Exception {
		
		if (!isRepeat) { //新增数据
			//自动生成合同号
			String contractNo = this.genContractNo(crewId);
			String contractId = UUIDUtils.getId();
			
			ContractProduceModel contractProduce = new ContractProduceModel();
			contractProduce.setContractId(contractId);
			contractProduce.setCrewId(crewId);
			contractProduce.setContractNo(contractNo);
			contractProduce.setContractDate(contractDate);
			contractProduce.setCompany(company);
			contractProduce.setContactPerson(contactPerson);
			
			contractProduce.setPhone(phone);
			contractProduce.setIdentityCardType(identityCardType);
			contractProduce.setIdentityCardNumber(identityCardNumber);
			if (startDate != null) {
				contractProduce.setStartDate(startDate);
			}
			if (endDate != null) {
				contractProduce.setEndDate(endDate);
			}
			
			contractProduce.setCurrencyId(currencyId);
			contractProduce.setTotalMoney(totalMoney);
			contractProduce.setBankName(bankName);
			contractProduce.setBankAccountName(bankAccountName);
			contractProduce.setBankAccountNumber(bankAccountNumber);
			
			contractProduce.setFinanceSubjId(financeSubjId);
			contractProduce.setFinanceSubjName(financeSubjName);
			contractProduce.setRemark(remark);
			contractProduce.setCustomContractNo(customContractNo);
			
			this.contractProduceDao.add(contractProduce);
		}else {
			//判断重复数据是跳过还是覆盖
			if (isCover) { //覆盖（更新数据）
				//根据多个条件查询是否是重复数据
				Map<String, Object> conditionMap = new HashMap<String, Object>();
				conditionMap.put("company", company);
				conditionMap.put("phone", phone);
				conditionMap.put("crewId", crewId);
				conditionMap.put("customContractNo", customContractNo);
				List<ContractProduceModel> workerByCustomNo = this.contractProduceDao.queryManyByMutiCondition(conditionMap, null);
				ContractProduceModel model = workerByCustomNo.get(0);
				model.setContractDate(contractDate);
				
				model.setCompany(company);
				model.setContactPerson(contactPerson);
				model.setPhone(phone);
				model.setIdentityCardType(identityCardType);
				model.setIdentityCardNumber(identityCardNumber);
				
				if (startDate != null) {
					model.setStartDate(startDate);
				}
				if (endDate != null) {
					model.setEndDate(endDate);
				}
				model.setCurrencyId(currencyId);
				model.setTotalMoney(totalMoney);
				model.setBankName(bankName);
				
				model.setBankAccountName(bankAccountName);
				model.setBankAccountNumber(bankAccountNumber);
				model.setFinanceSubjId(financeSubjId);
				model.setFinanceSubjName(financeSubjName);
				model.setRemark(remark);
				model.setCustomContractNo(customContractNo);
				//更新数据
				this.contractProduceDao.updateWithNull(model, "contractId");
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
		
		String maxContractNo = this.contractProduceDao.queryMaxContractNo(crewId);
		if (StringUtils.isBlank(maxContractNo)) {
			newContractNo = Constants.PRODUCE_CONTRACT_PREFIX + "0001";
		} else {
			DecimalFormat df = new DecimalFormat("0000");
			
			Integer contractNoNum = Integer.parseInt(maxContractNo.substring(Constants.WORK_CONTRACT_PREFIX.length()));
			
			newContractNo = Constants.PRODUCE_CONTRACT_PREFIX + df.format(contractNoNum + 1);
		}
		
		return newContractNo;
	}
	
	/**
	 * 删除合同
	 * @param contractId
	 * @throws Exception 
	 */
	public void deleteContract(String crewId, String contractId) throws Exception {
		
		ContractProduceModel contractProduce = this.queryById(crewId, contractId);
		
		//删除待付信息
    	contractToPaidService.deleteContractToPaidInfoWhenDeleteContract(contractId, "3");
		
		//删除合同记录
    	this.contractProduceDao.deleteOne(contractId, "contractId", ContractProduceModel.TABLE_NAME);
    	
    	//删除支付方式记录
    	this.contractPayWayService.deleteByContractId(contractId, crewId);
    	
    	//清空删除和借款单的关联
    	this.paymentInfoDao.deleteByContractInfo(ContractType.Produce.getValue(), contractId);
    	
    	//删除合同下的附件
    	if (!StringUtils.isBlank(contractProduce.getAttpackId())) {
    		this.attachmentService.deleteByPackId(contractProduce.getAttpackId());
    	}
    	
    	//删除合同对应的消息提醒
    	this.messageInfoService.deleteByBuzId(contractId);
	}
	
	/**
	 * 根据ID查询合同
	 * @param crewId
	 * @param contractId
	 * @return
	 * @throws Exception 
	 */
	public ContractProduceModel queryById(String crewId, String contractId) throws Exception {
		return this.contractProduceDao.queryById(crewId, contractId);
	}
	
	/**
	 * 查询制作合同中的付款单列表
	 * @param crewId
	 * @param financeSubjId
	 * @return
	 */
	public List<Map<String, Object>> queryContractProducePaymentList(String crewId, String financeSubjId){
		return this.contractProduceDao.queryContractProducePaymentList(crewId, financeSubjId);
	}
	
}
