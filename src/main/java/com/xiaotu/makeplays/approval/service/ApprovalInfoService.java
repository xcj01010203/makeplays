package com.xiaotu.makeplays.approval.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.approval.dao.ApprovalInfoDao;
import com.xiaotu.makeplays.approval.model.ApprovalInfoModel;

/**
 * 审批信息
 * @author xuchangjian 2017-5-12上午10:47:29
 */
@Service
public class ApprovalInfoService {

	@Autowired
	private ApprovalInfoDao approvalInfoDao;
	
	/**
	 * 删除单据的审批信息
	 * @param receiptId
	 */
	public void deleteByReceiptId(String receiptId) {
		this.approvalInfoDao.deleteByReceiptId(receiptId);
	}
	

	/**
	 * 删除单据的审批中的审批信息
	 * @param receiptId
	 */
	public void deleteAuditingByReceiptId(String receiptId) {
		this.approvalInfoDao.deleteAuditingByReceiptId(receiptId);
	}
	
	/**
	 * 批量新增数据
	 * @param approvalInfoList
	 * @throws Exception 
	 */
	public void addBatch(List<ApprovalInfoModel> approvalInfoList) throws Exception {
		this.approvalInfoDao.addBatch(approvalInfoList, ApprovalInfoModel.class);
	}
	
	/**
	 * 查询单据的所有审批信息
	 * @param receiptId	单据ID
	 * @return
	 */
	public List<ApprovalInfoModel> queryByReceiptId(String receiptId) {
		return this.approvalInfoDao.queryByReceiptId(receiptId);
	}
	
	/**
	 * 查询单据的所有审批信息
	 * 该查询会查询出审批人相关信息
	 * @param receiptId	单据ID
	 * @return
	 */
	public List<Map<String, Object>> queryByReceiptIdWithApproverInfo(String receiptId) {
		return this.approvalInfoDao.queryByReceiptIdWithApproverInfo(receiptId);
	}
	
	/**
	 * 根据单据号和审批人查询审批信息
	 * @param receiptId
	 * @param approverId
	 * @return
	 * @throws Exception 
	 */
	public ApprovalInfoModel queryByRecieptIdAndApproverId(String receiptId, String approverId) throws Exception {
		return this.approvalInfoDao.queryByRecieptIdAndApproverId(receiptId, approverId);
	}
	
	/**
	 * 更新一条记录
	 * @param approvalInfo
	 * @throws Exception 
	 */
	public void updateOne(ApprovalInfoModel approvalInfo) throws Exception {
		this.approvalInfoDao.update(approvalInfo, "id");
	}
	
	/**
	 * 批量更新单据下的所有审批结果
	 * @param receiptId	单据ID
	 * @param resultType	审批结果
	 */
	public void updateApprovalResultByReceiptId(String receiptId, Integer resultType) {
		this.approvalInfoDao.updateApprovalResultByReceiptId(receiptId, resultType);
	}
	
	/**
	 * 根据单据号和序号查询下一个审批人
	 * @param receiptId 单据ID
	 * @param sequence 当前审批人序号
	 * @return
	 * @throws Exception
	 */
	public ApprovalInfoModel queryNextApprover(String receiptId, Integer sequence) throws Exception {
		return this.approvalInfoDao.queryNextApprover(receiptId, sequence);
	}
}
