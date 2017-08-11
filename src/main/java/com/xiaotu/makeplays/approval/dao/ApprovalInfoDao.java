package com.xiaotu.makeplays.approval.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.approval.model.ApprovalInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 审批信息
 * @author xuchangjian 2017-5-12上午10:45:05
 */
@Repository
public class ApprovalInfoDao extends BaseDao<ApprovalInfoModel> {

	/**
	 * 删除单据的审批信息
	 * @param receiptId
	 */
	public void deleteByReceiptId(String receiptId) {
		String sql = "delete from " + ApprovalInfoModel.TABLE_NAME + " where receiptId = ?";
		this.getJdbcTemplate().update(sql, receiptId);
	}
	
	/**
	 * 删除单据的审批中的审批信息
	 * @param receiptId
	 */
	public void deleteAuditingByReceiptId(String receiptId) {
		String sql = "delete from " + ApprovalInfoModel.TABLE_NAME + " where receiptId = ? and resultType=1";
		this.getJdbcTemplate().update(sql, receiptId);
	}
	
	/**
	 * 查询单据的所有审批信息
	 * @param receiptId	单据ID
	 * @return
	 */
	public List<ApprovalInfoModel> queryByReceiptId(String receiptId) {
		String sql = "select * from " + ApprovalInfoModel.TABLE_NAME + " where receiptId = ? order by approvalTime is null, approvalTime, sequence";
		return this.query(sql, new Object[] {receiptId}, ApprovalInfoModel.class, null);
	}
	
	/**
	 * 查询单据的所有审批信息
	 * 该查询会查询出审批人相关信息
	 * @param receiptId	单据ID
	 * @return
	 */
	public List<Map<String, Object>> queryByReceiptIdWithApproverInfo(String receiptId) {
		String sql = "select tai.*, tui.realName, tui.phone from " + ApprovalInfoModel.TABLE_NAME 
				+ " tai, tab_user_info tui where tai.approverId = tui.userId AND tai.receiptId = ? " 
				+ " order by tai.approvalTime is null, tai.approvalTime, tai.sequence";
		return this.query(sql, new Object[] {receiptId}, null);
	}
	
	/**
	 * 根据单据号和审批人查询审批信息
	 * @param receiptId
	 * @param approverId
	 * @return
	 * @throws Exception 
	 */
	public ApprovalInfoModel queryByRecieptIdAndApproverId(String receiptId, String approverId) throws Exception {
		String sql = "select * from " + ApprovalInfoModel.TABLE_NAME + " where receiptId = ? and approverId = ?";
		return this.queryForObject(sql, new Object[] {receiptId, approverId}, ApprovalInfoModel.class);
	}
	
	/**
	 * 批量更新单据下的所有审批结果
	 * @param receiptId	单据ID
	 * @param resultType	审批结果
	 */
	public void updateApprovalResultByReceiptId(String receiptId, Integer resultType) {
		String sql = "update tab_approval_info set resultType = ? where receiptId = ?";
		this.getJdbcTemplate().update(sql, resultType, receiptId);
	}
	
	/**
	 * 根据单据号和序号查询下一个审批人
	 * @param receiptId 单据ID
	 * @param sequence 当前审批人序号
	 * @return
	 * @throws Exception
	 */
	public ApprovalInfoModel queryNextApprover(String receiptId, Integer sequence) throws Exception {
		String sql = "select * from " + ApprovalInfoModel.TABLE_NAME + " where receiptId = ? and sequence=?";
		return this.queryForObject(sql, new Object[] {receiptId, sequence + 1}, ApprovalInfoModel.class);
	}
}
