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
import com.xiaotu.makeplays.finance.dao.ContractActorDao;
import com.xiaotu.makeplays.finance.dao.PaymentInfoDao;
import com.xiaotu.makeplays.finance.model.ContractActorModel;
import com.xiaotu.makeplays.finance.model.constants.ContractType;
import com.xiaotu.makeplays.message.service.MessageInfoService;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 演员合同
 * @author xuchangjian 2016-8-3下午5:08:36
 */
@Service
public class ContractActorService {

	@Autowired
	private ContractActorDao contractActorDao;
	
	@Autowired
	private AttachmentService attachmentService;
	
	@Autowired
	private ContractPayWayService contractPayWayService;
	
	@Autowired
	private ContractToPaidService contractToPaidService;
	
	@Autowired
	private PaymentInfoDao paymentInfoDao;
	
	@Autowired
	private MessageInfoService messageInfoService;
	
	
	/**
	 * 根据多个条件查询演员合同信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ContractActorModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.contractActorDao.queryManyByMutiCondition(conditionMap, page);
	}
	
	/**
	 * 根据ID查询合同
	 * @param crewId
	 * @param contractId
	 * @return
	 * @throws Exception 
	 */
	public ContractActorModel queryById(String crewId, String contractId) throws Exception {
		return this.contractActorDao.queryById(crewId, contractId);
	}
	
	/**
	 * 根据财务科目ID查询演员合同
	 * @param crewId
	 * @param financeSubjId
	 * @return
	 */
	public List<ContractActorModel> queryByFinanceSubjId(String crewId, String financeSubjId) {
		return this.contractActorDao.queryByFinanceSubjId(crewId, financeSubjId);
	}
	
	/**
	 * 根据剧组ID查询演员合同
	 * @param crewId
	 * @return
	 */
	public List<ContractActorModel> queryByCrewId(String crewId) {
		return this.contractActorDao.queryByCrewId(crewId);
	}
	
	/**
	 * 查询剧组下带有财务科目的演员合同
	 * @param crewId
	 * @return
	 */
	public List<ContractActorModel> queryFinanContract(String crewId) {
		return this.contractActorDao.queryFinanContract(crewId);
	}
	
	/**
	 * 查询演员合同的预算
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryContractActorBudget(String crewId) {
		return this.contractActorDao.queryContractActorBudget(crewId);
	}
	
	/**
	 * 根据高级查询条件查询职员合同
	 * @param actorNames	演员名称，多个以逗号隔开
	 * @param roleNames	角色名称，多个以逗号隔开
	 * @param paymentTerm	支付条件
	 * @param remark	备注
	 * @return 合同ID，职员名称，合同编号，部门职务，总金额，已付金额，未付金额，合同关联货币ID，编码，货币汇率
	 */
	public List<Map<String, Object>> queryByAnvanceCondition(String crewId,  String actorNames, String roleNames, String financeSubjIds, Integer payWay, String paymentTerm, String remark, String paymentStartDate, String paymentEndDate) {
		return this.contractActorDao.queryByAnvanceCondition(crewId, actorNames, roleNames, financeSubjIds, payWay, paymentTerm, remark, paymentStartDate, paymentEndDate);
	}
	
	/**
	 * 保存职员合同信息
	 * @param request
	 * @param loginUserId 登录用户ID
	 * @param contractId	合同ID
	 * @param contractNo	合同编号
	 * @param contractDate	支付日期
	 * @param actorName	演员名称
	 * @param roleName	角色名称
	 * @param phone	联系电话
	 * @param idNumber	身份证
	 * @param startDate	入组时间
	 * @param endDate	离组时间
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
	public ContractActorModel saveContractInfo(String crewId, String loginUserId, String contractId, String contractNo,
			String contractDate, String actorName, String roleName, 
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
    	
		ContractActorModel contractActor = new ContractActorModel();
		if (StringUtils.isBlank(contractId)) {
			isAdd = true;
			contractActor.setContractId(UUIDUtils.getId());
		} else {
			contractActor = this.contractActorDao.queryById(crewId, contractId);
		}
		
		contractActor.setCrewId(crewId);
		contractActor.setContractNo(contractNo);
		contractActor.setContractDate(sdf.parse(contractDate));
		contractActor.setActorName(actorName);
		contractActor.setRoleName(roleName);
		contractActor.setPhone(phone);
		contractActor.setIdentityCardType(identityCardType);
		contractActor.setIdentityCardNumber(identityCardNumber);
		if (!StringUtils.isBlank(startDate)) {
			contractActor.setStartDate(sdf.parse(startDate));
		}
		if (!StringUtils.isBlank(endDate)) {
			contractActor.setEndDate(sdf.parse(endDate));
		}
		contractActor.setCurrencyId(currencyId);
		contractActor.setTotalMoney(totalMoney);
		contractActor.setPaymentTerm(paymentTerm);
		contractActor.setBankName(bankName);
		contractActor.setBankAccountName(bankAccountName);
		contractActor.setBankAccountNumber(bankAccountNumber);
		contractActor.setPayWay(payWay);
		contractActor.setFinanceSubjId(financeSubjId);
		contractActor.setFinanceSubjName(financeSubjName);
		
		if (StringUtils.isBlank(attpackId)) {
			attpackId = this.attachmentService.createNewPacket(crewId, AttachmentBuzType.Contract.getValue());
		}
		contractActor.setAttpackId(attpackId);
		
		contractActor.setRemark(remark);
		
		//新增\修改职员合同
		if (isAdd) {
			this.contractActorDao.add(contractActor);
		} else {
			this.contractActorDao.updateWithNull(contractActor, "contractId");
		}
		
		//如果是修改，先删除支付方式
		if (!isAdd) {
			this.contractPayWayService.deleteByContractId(contractId, crewId);
		}
    	
		//保存支付方式
    	List<Map<String, Object>> list = this.contractPayWayService.saveByPaymentTerm(crewId, loginUserId, contractActor.getContractId(), contractNo, paymentTerm, monthPayDetail, payWay);
		
    	//保存待付信息
    	contractToPaidService.saveContractToPaid(contractActor.getContractId(),contractNo,currencyId,crewId, roleName, actorName, financeSubjId, "2",list);
    	
		return contractActor;
	}
	
	/**
	 * 保存批量导入的演员合同
	 * @param crewId
	 * @param loginUserId
	 * @param contractId
	 * @param contractNo
	 * @param contractDate
	 * @param actorName
	 * @param roleName
	 * @param phone
	 * @param identityCardType
	 * @param identityCardNumber
	 * @param startDate
	 * @param endDate
	 * @param currencyId
	 * @param totalMoney
	 * @param paymentTerm
	 * @param monthPayDetail
	 * @param bankName
	 * @param bankAccountName
	 * @param bankAccountNumber
	 * @param payWay
	 * @param financeSubjId
	 * @param financeSubjName
	 * @param remark
	 * @param attpackId
	 * @return
	 * @throws Exception 
	 */
	public void saveImportContractActor(String crewId, Boolean isCover, Boolean isRepeat, String customContractNo,
			Date contractDate, String actorName, String roleName,String phone, Integer identityCardType, 
			String identityCardNumber, Date startDate, Date endDate, String currencyId, Double totalMoney, 
			String bankName, String bankAccountName, String bankAccountNumber, 
			String financeSubjId, String financeSubjName, String remark) throws Exception {
		
		if (!isRepeat) {
			//自动生成合同号
			String contractNo = this.genContractNo(crewId);
			String contractId = UUIDUtils.getId();
			
			ContractActorModel contractActor = new ContractActorModel();
			contractActor.setContractId(contractId);
			contractActor.setCrewId(crewId);
			contractActor.setContractNo(contractNo);
			contractActor.setContractDate(contractDate);
			
			contractActor.setActorName(actorName);
			contractActor.setRoleName(roleName);
			contractActor.setPhone(phone);
			contractActor.setIdentityCardType(identityCardType);
			
			contractActor.setIdentityCardNumber(identityCardNumber);
			contractActor.setStartDate(startDate);
			contractActor.setEndDate(endDate);
			contractActor.setCurrencyId(currencyId);
			
			contractActor.setTotalMoney(totalMoney);
			contractActor.setBankName(bankName);
			contractActor.setBankAccountName(bankAccountName);
			contractActor.setBankAccountNumber(bankAccountNumber);
			
			contractActor.setFinanceSubjId(financeSubjId);
			contractActor.setFinanceSubjName(financeSubjName);
			contractActor.setRemark(remark);
			contractActor.setCustomContractNo(customContractNo);
			
			//保存数据
			this.contractActorDao.add(contractActor);
		}else {
			//判断重复数据是跳过还是覆盖
			if (isCover) { //覆盖（更新数据）
				//根据多个条件查询是否是重复数据
				Map<String, Object> conditionMap = new HashMap<String, Object>();
				conditionMap.put("actorName", actorName);
				conditionMap.put("phone", phone);
				conditionMap.put("crewId", crewId);
				conditionMap.put("customContractNo", customContractNo);
				List<ContractActorModel> workerByCustomNo = this.contractActorDao.queryManyByMutiCondition(conditionMap, null);
				if(workerByCustomNo == null || workerByCustomNo.size() == 0) {
					conditionMap.remove("customContractNo");
					conditionMap.put("contractNo", customContractNo);
					workerByCustomNo = this.contractActorDao.queryManyByMutiCondition(conditionMap, null);
				}
				ContractActorModel model = workerByCustomNo.get(0);
				model.setContractDate(contractDate);
				
				model.setActorName(actorName);
				model.setRoleName(roleName);
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
				this.contractActorDao.updateWithNull(model, "contractId");
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
		
		String maxContractNo = this.contractActorDao.queryMaxContractNo(crewId);
		if (StringUtils.isBlank(maxContractNo)) {
			newContractNo = Constants.ACTOR_CONTRACT_PREFIX + "0001";
		} else {
			DecimalFormat df = new DecimalFormat("0000");
			
			Integer contractNoNum = Integer.parseInt(maxContractNo.substring(Constants.WORK_CONTRACT_PREFIX.length()));
			
			newContractNo = Constants.ACTOR_CONTRACT_PREFIX + df.format(contractNoNum + 1);
		}
		
		return newContractNo;
	}
	
	/**
	 * 删除合同
	 * @param contractId
	 * @throws Exception 
	 */
	public void deleteContract(String crewId, String contractId) throws Exception {
		ContractActorModel contractActor = this.queryById(crewId, contractId);
		
		//删除待付信息
    	contractToPaidService.deleteContractToPaidInfoWhenDeleteContract(contractId, "2");
		
		//删除合同记录
    	this.contractActorDao.deleteOne(contractId, "contractId", ContractActorModel.TABLE_NAME);
    	
    	//删除支付方式记录
    	this.contractPayWayService.deleteByContractId(contractId, crewId);
    	
    	//删除对应的付款单
    	this.paymentInfoDao.deleteByContractInfo(ContractType.Actor.getValue(), contractId);
    	
    	//删除合同下的附件
    	if (!StringUtils.isBlank(contractActor.getAttpackId())) {
    		this.attachmentService.deleteByPackId(contractActor.getAttpackId());
    	}
    	
    	//删除合同对应的消息提醒
    	this.messageInfoService.deleteByBuzId(contractId);
    	
	}
	
	/**
	 * 根据演员姓名查询出演员所扮演的角色信息
	 * @param crewId
	 * @param actorName
	 * @return
	 */
	public List<Map<String, Object>> queryViewRoleNameByActorName(String crewId, String actorName){
		return this.contractActorDao.queryViewRoleNameByActorName(crewId, actorName);
	}
	
	/**
	 * 查询演员合同的付款单列表
	 * @param crewId
	 * @param financeSubjId
	 * @return
	 */
	public List<Map<String, Object>> queryContractActorPaymentList(String crewId, String financeSubjId){
		return this.contractActorDao.queryContractActorPaymentList(crewId, financeSubjId);
	}
}
