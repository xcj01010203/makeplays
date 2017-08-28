package com.xiaotu.makeplays.finance.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.finance.dao.ContractToPaidDao;
import com.xiaotu.makeplays.finance.model.ContractToPaidModel;
import com.xiaotu.makeplays.finance.model.FinanceSubjectModel;
import com.xiaotu.makeplays.utils.DateUtils;

/**
 * 
 * 合同待付
 * @author Administrator
 *
 */
@Service
public class ContractToPaidService {

	@Autowired
	private ContractToPaidDao contractToPaidDao;
	
	@Autowired
	private FinanceSubjectService financeSubjectService;
	
	@Autowired
	private PaymentInfoService paymentInfoService;
	
	/**
	 * 保存合同信息时保存待付信息
	 * @param currencyId  货币信息
	 * @param crewid   剧组id
	 * @param contractNo   合同号
	 * @param roleName   演员合同：角色名称  ，   职员合同：职务，  制作合同：对方公司名称
	 * @param name	人员名称
	 * @param financeSubjId   财务科目id
	 * @param contractType   合同类型   0：演员合同   1：职员合同   2：制作合同
	 * @param paydetailInfoList  保存合同是个期或各阶段信息
	 * @throws Exception 
	 */								
	public void saveContractToPaid(String contractId,String contractNo,String currencyId,String crewid,String roleName,String name,String financeSubjId,String contractType,List<Map<String, Object>> paydetailInfoList) throws Exception{
		if(paydetailInfoList!=null&&paydetailInfoList.size()>0){
//			contractToPaidDao.deleteContractToPaidInfoWhenDeleteContract(contractId,contractType);
			
			//待付清单中状态为  已付的数据
			List<ContractToPaidModel> contractToPaidList = this.contractToPaidDao.queryByContractId(crewid, contractId);
			//需要删除待付清单表中的数据
			Set<String> delset = new HashSet<String>();
			//需要添加信息
			List<ContractToPaidModel> addList = new ArrayList<ContractToPaidModel>();
			
			for(Map<String, Object> teMap : paydetailInfoList){
				String oldId = teMap.get("oldId") != null ? teMap.get("oldId").toString() : "";
				String id = teMap.get("id") != null ? teMap.get("id").toString() : "";
				String paiddate = teMap.get("paydate") != null ? teMap.get("paydate").toString() : "";
				String summary = roleName + name + "合同款";
				double money = teMap.get("money") != null ? Double.valueOf(teMap.get("money").toString()) : 0.0;
				
				ContractToPaidModel contractToPaidModel = new ContractToPaidModel();
				contractToPaidModel.setContactname(name);
				contractToPaidModel.setContractId(contractId);
				contractToPaidModel.setContacttype(contractType);
				contractToPaidModel.setCreatetime(new Timestamp(System.currentTimeMillis()));
				contractToPaidModel.setCrewId(crewid);
				contractToPaidModel.setSummary(summary);
				contractToPaidModel.setId(id);
				contractToPaidModel.setStatus(0);
				contractToPaidModel.setContractNo(contractNo);
				contractToPaidModel.setMoney(money);
				contractToPaidModel.setCurrencyId(currencyId);
				contractToPaidModel.setRoleName(roleName);
				contractToPaidModel.setSubjectId(financeSubjId);
				contractToPaidModel.setPaiddate(new SimpleDateFormat("yyyy-MM-dd").parse(DateUtils.formatToString(paiddate, 0)));
				
//				boolean flag = false;// 不需要删除
				if (contractToPaidList != null && contractToPaidList.size() > 0) {
					for (ContractToPaidModel toPaidInfo : contractToPaidList) {
//						String did = toPaidInfo.get("id") != null ? toPaidInfo.get("id").toString() : "";
//						Integer statusFData = toPaidInfo.get("status") != null ? Integer.valueOf(toPaidInfo.get("status").toString()) : 0;
//						if (oldId.equals(did)) {
//							flag = true;
//							contractToPaidModel.setStatus(statusFData);
//							break;
//						}
						String myId = toPaidInfo.getId();
						int myStatus = toPaidInfo.getStatus();
						if (myId.equals(oldId)) {
							contractToPaidModel.setStatus(myStatus);
							break;
						}
					}
				}
//				if (flag) {
//					delset.add(oldId);
//				}
				addList.add(contractToPaidModel);
			}
			
			this.contractToPaidDao.deleteByContractId(crewid, contractId);
			if(addList!=null&&addList.size()>0){
				if(delset!=null&&delset.size()>0){
					List<String> deList = new ArrayList<String>(delset);
					contractToPaidDao.batchDeleteContractToPaidInfo(deList);
				}
				//保存合同未付信息
				contractToPaidDao.addBatch(addList, ContractToPaidModel.class);
			}
	}
	}
	
	/**
	 * 整理数据
	 * @param contractPartId  合同阶段id
	 * @param contractType    合同类型
	 * @param receiptNo       付款单号
	 * @param paymentStatus          付款单状态    状态：0-未结算  1-已结算
	 * @param paymentSubjMapStr   付款信息
	 * @throws Exception
	 */
	public void arrangeContractToPaidReady(String paymentId,String contractId,String contractPartId,Integer contractType,Integer paymentStatus,String paymentSubjMapStr) throws Exception{
		
		
		if(contractType==1||contractType==2||contractType==3){
			//PaymentInfoModel paymentInfoModel = paymentInfoService.queryById(paymentId);
			List<ContractToPaidModel> listPayment = contractToPaidDao.queryContractToPaidInfo(contractPartId);
			if(listPayment!=null&&listPayment.size()>0){
				List<ContractToPaidModel> batchUpdate = new ArrayList<ContractToPaidModel>();
				for(ContractToPaidModel contractToPaidModel:listPayment){
					if(paymentStatus!=null&&paymentStatus==0){
						contractToPaidModel.setStatus(2);
					}else{
						contractToPaidModel.setStatus(3);
					}
					//保存修改的财务科目
					String fSubIdFromPage = "";//前台-科目id
					String summaryFromPage = "";//前台-摘要
					String moneyFromPage = "";//前台-金额
					String[] payinfo = paymentSubjMapStr.split("&&");
					if(payinfo!=null&&payinfo.length>0){
						String payinfoFirst = payinfo[0];
						String[] payParts = payinfoFirst.split("##");
						if(payParts!=null&&payParts.length>0){
							fSubIdFromPage = payParts[1];
							summaryFromPage = payParts[0];
							moneyFromPage = payParts[3];
						}
					}
					
					
					
					contractToPaidModel.setMoney(Double.parseDouble(moneyFromPage));
					contractToPaidModel.setSubjectId(fSubIdFromPage);
					
					contractToPaidModel.setSummary(summaryFromPage);
					contractToPaidModel.setPaymentId(paymentId);
					contractToPaidModel.setCreatetime(new Timestamp(System.currentTimeMillis()));
					contractToPaidModel.setUpdatetime(new Timestamp(System.currentTimeMillis()));
					batchUpdate.add(contractToPaidModel);
				}
				contractToPaidDao.updateBatch(batchUpdate, "id", ContractToPaidModel.class);
				
			}
			
		}
	}

	
	
	
	
 	/**
 	 * 筛选合同待付清单数据
 	 * @param crewid 剧组id
 	 * @param starDate 开始时间
 	 * @param endDate   结束时间
 	 * @param contractType  合同类型
 	 * @param contractName  合同方
 	 * @param financeSubjectId  财务科目
 	 * @param status   状态
 	 * @return
 	 */
 	public List<Map<String, Object>> queryContractToPaidList(String crewid,String starDate,String endDate,String contractType,String contractName,String financeSubjectId,String status){
 		List<Map<String, Object>> result = contractToPaidDao.queryContractToPaidList(null,crewid, starDate, endDate, contractType, contractName, financeSubjectId, status);
 		List<Map<String, Object>> back =arrangeDateForToPaidList(result,crewid);
 		return back;
	}
 	/**
 	 * 
 	 * 根据id查询合同待付信息
 	 * @param id
 	 * @return
 	 */
 	public List<Map<String, Object>> queryContractToPaidListById(String id,String crewId){
 		List<Map<String, Object>> result = contractToPaidDao.queryContractToPaidList(id,crewId,null,null,null,null,null,null);
 		List<Map<String, Object>> back = arrangeDateForToPaidList(result,crewId);
 		return back;
 	}
 	
 	
 	
	/**
	 * 整理查询数据
	 * @param result
	 * @return
	 */
	private List<Map<String, Object>> arrangeDateForToPaidList(List<Map<String, Object>> result,String crewId) {
		List<Map<String, Object>> back = new ArrayList<Map<String,Object>>();
		if(result!=null&&result.size()>0){
			financeSubjectService.refreshCachedSubjectList(crewId);
			
			
			
 			Map<String, Object> map = null;
 			for(Map<String, Object> tmMap :result){
 				map = new HashMap<String, Object>();
 				map.put("id", tmMap.get("id"));
 				map.put("crewid", tmMap.get("crewid"));
 				Object pdateObje = tmMap.get("paiddate");
 				String paiddate = pdateObje!=null?pdateObje.toString():"暂无";
 				
 				
 				map.put("paiddate",paiddate );
 				map.put("contractId", tmMap.get("contractId"));
 				map.put("contactNO", tmMap.get("contractNo"));
 				String summary = tmMap.get("summary")!=null?tmMap.get("summary").toString():"";
 				String name = tmMap.get("contactname")!=null?tmMap.get("contactname").toString():"";
 				String roleName = tmMap.get("roleName")!=null?tmMap.get("roleName").toString():"";
 				map.put("summary", summary);
 				map.put("money", tmMap.get("money"));
 				map.put(tmMap.get("currencyId").toString(), tmMap.get("money"));
 				map.put("currencyId", tmMap.get("currencyId"));
 				String subjectId = tmMap.get("subjectId")!=null?tmMap.get("subjectId").toString():"";
 				String financeSubjName = tmMap.get("financeSubjName")!=null?tmMap.get("financeSubjName").toString():"";
 				map.put("subjectId", subjectId);
 				financeSubjName = financeSubjectService.getFinanceSubjName(subjectId);
 				map.put("subjectName", financeSubjName);
 				String subjectNameDetail = "";
 				String subjectNameMain = "";
 				if(StringUtils.isNotBlank(financeSubjName)){
 					int num = financeSubjName.indexOf("-");
 					if(num!=-1){
 						subjectNameMain = financeSubjName.substring(0,num);
 						subjectNameDetail = financeSubjName.substring(num+1);
 					}else{
 						subjectNameMain = financeSubjName;
 					}
 				}
 				map.put("subjectNameDetail", subjectNameDetail);
 				map.put("subjectNameMain", subjectNameMain);
 				map.put("status", tmMap.get("status"));
 				map.put("paymentno", tmMap.get("receiptno"));
 				map.put("contactname", name);
 				map.put("contacttype", tmMap.get("contacttype"));
 				map.put("param", roleName);
 				map.put("crewName", tmMap.get("crewname"));
 				map.put("paymentId", tmMap.get("paymentId"));
 				back.add(map);
 			}
 		}
		return back;
	}
	/**
	 * 修改合同待付信息
	 * @param id  合同待付信息id
	 * @param summary  摘要
	 * @param money    金额
	 * @param status   状态   0：未付 1:待付    2：已付
	 */
 	public void updateContractToPaidInfo(String id,String summary,Double money,Integer status,String paymentId){
 		
 		contractToPaidDao.updateContractToPaidInfo(id,summary,money,status,paymentId);
 		
 	}
 	
 	/**
	 * 查询合同待付表中已有合同方
	 * @param crewid
	 * @return
	 */
 	public List<String> queryDropListForContractName(String crewId){
 		List<String> list = contractToPaidDao.queryDropListForContractName(crewId);
 		return list;
 	}
 	
 	
 	/**
 	 * 获取财务科目，只查询合同批量支付中有的财务科目
 	 * @param crewId
 	 * @return
 	 */
 	public List<FinanceSubjectModel> queryDropListForContractSubjectName(String crewId){
 		List<FinanceSubjectModel> back = new ArrayList<FinanceSubjectModel>();
 		List<FinanceSubjectModel> list = contractToPaidDao.querySubjectId(crewId);
 		if(list!=null&&list.size()>0){
 			back.addAll(list);
 			for(FinanceSubjectModel financeSubjectModel:list){
 				List<FinanceSubjectModel> flist = contractToPaidDao.queryDropListForContractSubjectName(financeSubjectModel.getId());
 				back.addAll(flist);
 			}
 		}
 		return back;
 	}
 	
 	public void deleteContractToPaidInfoByPaymentId(String paymentId){
 		contractToPaidDao.deleteContractToPaidInfoByPaymentId(paymentId);
 	}
 	
 	/**
 	 * 删除合同的时候删除合同待付信息中不是已付的信息
 	 * 
 	 * @param contractId	合同id
 	 * @param contractType  合同类型
 	 */
 	public void deleteContractToPaidInfoWhenDeleteContract(String contractId,String contractType){
 		contractToPaidDao.deleteContractToPaidInfoWhenDeleteContract(contractId,contractType);
 	}
 	
 	/**
 	 * 根据付款单id批量将合同待付信息修改为已结算
 	 * 
 	 * @param paymentIds
 	 */
 	public void batchSettleMent(String paymentIds){
 		List<Map<String, Object>> toPaidInfoList = contractToPaidDao.queryContractToPaidInfoByPaymentIds(paymentIds);
 		if(toPaidInfoList!=null&&toPaidInfoList.size()>0){
 			StringBuilder ids = new StringBuilder();
 			for(int i = 0;i<toPaidInfoList.size();i++){
 				Map<String, Object> tMap = toPaidInfoList.get(i);
 				String id = tMap.get("id")!=null?tMap.get("id").toString():"";
 				if(StringUtils.isBlank(id)){
 					continue;
 				}
 				ids.append(id);
 				if(i<toPaidInfoList.size()-1){
 					ids.append(",");
 				}
 			}
 			contractToPaidDao.updateContractToPaidInfo(ids.toString(),null,null,3,"");
 		}
 		
 	}
 	
 	/**
 	 * 将待付信息状态修改为结算  通过 paymentId
 	 * 
 	 * @param paymentId
 	 */
 	public void updateContractTopaid2SettleByPaymentId(String paymentId){
 		contractToPaidDao.updateContractTopaid2SettleByPaymentId(paymentId);
 	}
 	
 	
 	
 	
 	
 	
 	
 	
 	
}
