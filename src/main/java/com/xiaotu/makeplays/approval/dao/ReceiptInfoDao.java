package com.xiaotu.makeplays.approval.dao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.approval.controller.filter.ReceiptInfoFilter;
import com.xiaotu.makeplays.approval.model.ReceiptInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.DateUtils;
import com.xiaotu.makeplays.utils.Page;

/**
 * 单据信息
 * @author xuchangjian 2017-5-12上午10:41:44
 */
@Repository
public class ReceiptInfoDao extends BaseDao<ReceiptInfoModel> {

	/**
	 * 根据ID查询单据信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ReceiptInfoModel queryById(String id) throws Exception {
		String sql = "select * from " + ReceiptInfoModel.TABLE_NAME + " where id = ?";
		return this.queryForObject(sql, new Object[] {id}, ReceiptInfoModel.class);
	}
	
	/**
	 * 查询剧组中最新单据信息
	 * @param crewId
	 * @param receiptType
	 * @return
	 * @throws Exception
	 */
	public ReceiptInfoModel queryLastReceipt(String crewId, Integer receiptType) throws Exception {
		String sql = "select * from " + ReceiptInfoModel.TABLE_NAME + " where crewId = ? and type = ? order by createTime desc limit 1";
		return this.queryForObject(sql, new Object[] {crewId, receiptType}, ReceiptInfoModel.class);
	}
	
	/**
	 * 查询单据列表
	 * @param crewId
	 * @param filter	单据过滤条件
	 * @param page	分页信息
	 * @return
	 * @throws ParseException 
	 */
	public List<Map<String, Object>> queryReceiptInfoList(String crewId, String userId, ReceiptInfoFilter filter, Page page) throws ParseException {
		List<Object> paramsList = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tri.id, ");
		sql.append(" 	tri.type, ");
		sql.append(" 	tri.receiptNo, ");
		sql.append(" 	tri.submitTime, ");
		sql.append(" 	tri.money, ");
		sql.append(" 	tri.description, ");
		sql.append(" 	tri.`status`, ");
		sql.append(" 	tui.realName applyerName, ");
		sql.append(" 	tci. NAME currencyName, ");
		sql.append(" 	tci.`code` currencyCode, ");
		//审批人信息，格式：姓名##审批结果，多个用&&隔开
		sql.append(" 	GROUP_CONCAT(tmp.realName, '##', tmp.resultType ORDER BY tmp.approvalTime is null, tmp.approvalTime, tmp.sequence SEPARATOR '&&') approverInfo ");
		sql.append(" FROM ");
		sql.append(" 	tab_receipt_info tri ");
		if (filter.getListType() != null) {
			if (filter.getListType() == 2) {	//我已审批
				sql.append(" INNER JOIN tab_approval_info tai ON tai.receiptId = tri.id AND tai.resultType != 1 AND tai.approverId = ? AND tri.status != 1 ");
				paramsList.add(userId);
			}
			if (filter.getListType() == 3) {	//待我审批
				sql.append(" INNER JOIN tab_approval_info tai ON tai.receiptId = tri.id AND tai.resultType = 1 AND tai.approverId = ? AND tri.status = 2 ");
				sql.append(" INNER JOIN (select tri.id,min(tai.sequence) sequence ");
				sql.append(" from tab_receipt_info tri ");
				sql.append(" left join tab_approval_info tai on tri.id=tai.receiptId ");
				sql.append(" and tai.resultType=1 ");
				sql.append(" and tai.crewId=? ");
				sql.append(" where tri.crewId=? ");
				sql.append(" group by tri.id) tai2 on tai2.id=tai.receiptId and tai2.sequence=tai.sequence ");
				paramsList.add(userId);
				paramsList.add(crewId);
				paramsList.add(crewId);
			}
		}
		sql.append(" 	LEFT JOIN ( ");
		sql.append(" 		SELECT ");
		sql.append(" 			tai.*, atui.realName ");
		sql.append(" 		FROM ");
		sql.append(" 			tab_approval_info tai, ");
		sql.append(" 			tab_user_info atui ");
		sql.append(" 		WHERE ");
		sql.append(" 			tai.approverId = atui.userId ");
		sql.append(" 			and tai.crewId=? ");
		paramsList.add(crewId);
		sql.append(" 	) tmp ON tmp.receiptId = tri.id, ");
		sql.append(" 	tab_user_info tui, ");
		sql.append(" 	tab_currency_info tci ");
		sql.append(" WHERE 1 = 1");
		sql.append(" AND tri.crewId = ? ");
		paramsList.add(crewId);
		if (filter.getListType() != null && filter.getListType() > 1) {
			sql.append(" AND tai.crewId = ? ");
			paramsList.add(crewId);
		}
		sql.append(" AND tri.status != 5 ");	//排除掉删除状态的单据
		sql.append(" AND tui.userId = tri.createUserId ");
		sql.append(" AND tci.id = tri.currencyId ");
		if (filter.getListType() != null && filter.getListType() == 1) {//我的申请
			sql.append(" AND tri.createUserId = ? ");
			paramsList.add(userId);
		}
		if (filter.getReceiptType() != null) {
			sql.append(" AND tri.type = ? ");
			paramsList.add(filter.getReceiptType());
		}
		if (!StringUtils.isBlank(filter.getReceiptNo())) {
			sql.append(" AND tri.receiptNo = ? ");
			paramsList.add(filter.getReceiptNo());
		}
		if (!StringUtils.isBlank(filter.getApplyerName())) {
			sql.append(" AND tui.realName like ? ");
			paramsList.add("%" + filter.getApplyerName() + "%");
		}
		if (filter.getMaxMoney() != null) {
			sql.append(" AND tri.money <= ? ");
			paramsList.add(filter.getMaxMoney());
		}
		if (filter.getMinMoney() != null) {
			sql.append(" AND tri.money >= ? ");
			paramsList.add(filter.getMinMoney());
		}
		if (!StringUtils.isBlank(filter.getStartDate())) {
			sql.append(" AND tri.submitTime >= ? ");
			paramsList.add(filter.getStartDate());
		}
		if (!StringUtils.isBlank(filter.getEndDate())) {
			sql.append(" AND tri.submitTime <= ? ");
			paramsList.add(DateUtils.getBeforeOrAfterDayDate(filter.getEndDate(), 1, null));
		}
		if (!StringUtils.isBlank(filter.getDescription())) {
			sql.append(" AND tri.description like ? ");
			paramsList.add("%" + filter.getDescription() + "%");
		}
		sql.append(" GROUP BY ");
		sql.append(" 	tri.id, ");
		sql.append(" 	tri.type, ");
		sql.append(" 	tri.receiptNo, ");
		sql.append(" 	tri.submitTime, ");
		sql.append(" 	tri.money, ");
		sql.append(" 	tri.description, ");
		sql.append(" 	tri.`status`, ");
		sql.append(" 	tui.realName, ");
		sql.append(" 	tci. NAME, ");
		sql.append(" 	tci.`code` ");
		if (filter.getListType() != null && filter.getListType() == 1) {	//我的申请
			sql.append(" ORDER BY tri.submitTime is not null, tri.submitTime desc ");
		}
		if (filter.getListType() != null && filter.getListType() == 2) {	//我已审批
			sql.append(" ORDER BY tai.approvalTime desc ");
		}
		if (filter.getListType() != null && filter.getListType() == 3) {	//待我审批
			sql.append(" ORDER BY tri.submitTime ");
		}
		
		return this.query(sql.toString(), paramsList.toArray(), page);
	}
}
