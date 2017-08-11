package com.xiaotu.makeplays.finance.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.finance.controller.filter.CollectionInfoFilter;
import com.xiaotu.makeplays.finance.model.CollectionInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 收款单
 * @author xuchangjian 2016-8-20下午2:42:46
 */
@Repository
public class CollectionInfoDao extends BaseDao<CollectionInfoModel> {
	
	/**
	 * 根据多个条件查询收款单信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<CollectionInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + CollectionInfoModel.TABLE_NAME + " where 1 = 1 ");
		
		Set<String> keySet = conditionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		List<Object> conList = new LinkedList<Object>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = conditionMap.get(key);
			sql.append(" and " + key + " = ?");
			conList.add(value);
		}
		Object[] objArr = conList.toArray();
		List<CollectionInfoModel> collectionList = this.query(sql.toString(), objArr, CollectionInfoModel.class, page);
		
		return collectionList;
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
		List<Object> paramsList = new ArrayList<Object>();
		paramsList.add(crewId);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select max(receiptNo) from tab_collection_info where 1 = 1 ");
		sql.append(" and crewId = ? ");
		if (payStatus) {
			sql.append(" and collectionDate >= ? and collectionDate <= ? ");
			paramsList.add(moonFirstDay);
			paramsList.add(moonLastDay);
		}
		
		return this.getJdbcTemplate().queryForObject(sql.toString(), paramsList.toArray(), String.class);
	}
	
	/**
	 * 根据ID查询收款单
	 * @param collectionId
	 * @return
	 * @throws Exception
	 */
	public CollectionInfoModel queryById(String collectionId) throws Exception {
		String sql = "select * from " + CollectionInfoModel.TABLE_NAME + " where collectionId = ? ";
		return this.queryForObject(sql, new Object[] {collectionId}, CollectionInfoModel.class);
	}
	
	/**
	 * 根据剧组ID查询收款单信息
	 * @param crewId
	 * @return
	 */
	public List<CollectionInfoModel> queryByCrewId(String crewId) {
		String sql = "select * from " + CollectionInfoModel.TABLE_NAME + " where crewId = ?";
		return this.query(sql, new Object[] {crewId}, CollectionInfoModel.class, null);
	}
	
	/**
	 * 查询收款单列表
	 * @param crewId
	 * @return	收款日期， 创建时间， 收款单编号，摘要，总金额，付款人，支付方式，记账人，关联货币ID，关联货币编码，关联货币名称
	 */
	public List<Map<String, Object>> queryCollectionInfoList (String crewId, CollectionInfoFilter collectionFilter) {
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(crewId);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("    tci.collectionId, ");
		sql.append(" 	tci.collectionDate, ");
		sql.append(" 	tci.createTime, ");
		sql.append(" 	tci.receiptNo, ");
		sql.append(" 	tci.summary, ");
		sql.append(" 	tci.money, ");
		sql.append(" 	tci.otherUnit, ");
		sql.append(" 	tfpi.wayName paymentWay, ");
		sql.append(" 	tci.agent, ");
		sql.append("    tcci.id currencyId, ");
		sql.append("    tcci.code currencyCode, ");
		sql.append("    tcci.name currencyName, ");
		sql.append("    tcci.exchangeRate ");
		sql.append(" FROM ");
		sql.append(" 	tab_collection_info tci, ");
		sql.append(" 	tab_finance_paymentWay_info tfpi, ");
		sql.append("    tab_currency_info tcci ");
		sql.append(" WHERE ");
		sql.append(" 	tci.crewId = ? ");
		sql.append(" AND tcci.id = tci.currencyId ");
		sql.append(" AND tci.paymentWay = tfpi.wayId ");
		
		if (!StringUtils.isBlank(collectionFilter.getCollectionIds())) {
			String collectionIds = "'" + collectionFilter.getCollectionIds().replace(",", "','") + "'";
			
			sql.append(" AND tci.collectionId in ("+ collectionIds +") ");
		}
		
		if (!StringUtils.isBlank(collectionFilter.getOtherUnits())) {
			String otherUnits = "'" + collectionFilter.getOtherUnits().replace(",", "','") + "'";
			
			sql.append(" AND tci.otherUnit in ("+ otherUnits +") ");
		}
		
		if (!StringUtils.isBlank(collectionFilter.getCollectionDates())) {
			String collectionDates = "'" + collectionFilter.getCollectionDates().replace(",", "','") + "'";
			
			sql.append(" AND tci.collectionDate in ("+ collectionDates +") ");
		}
		
		if (!StringUtils.isBlank(collectionFilter.getStartCollectionDate())) {
			paramList.add(collectionFilter.getStartCollectionDate());
			sql.append(" AND tci.collectionDate >= ? ");
		}
		
		if (!StringUtils.isBlank(collectionFilter.getEndCollectionDate())) {
			paramList.add(collectionFilter.getEndCollectionDate());
			sql.append(" AND tci.collectionDate <= ? ");
		}
		
		//收款月份
		if (!StringUtils.isBlank(collectionFilter.getCollectionMonth())) {
			paramList.add(collectionFilter.getCollectionMonth());
			sql.append(" AND DATE_FORMAT(tci.collectionDate,'%Y年%m月') = ? ");
		}
		
		if (!StringUtils.isBlank(collectionFilter.getAgents())) {
			String agents = "'" + collectionFilter.getAgents().replace(",", "','") + "'";
			
			sql.append(" AND tci.agent in ("+ agents +") ");
		}
		
		if (!StringUtils.isBlank(collectionFilter.getSummary())) {
			sql.append(" AND tci.summary like ? ");
			paramList.add("%" + collectionFilter.getSummary() + "%");
		}
		
		if (collectionFilter.getMinMoney() != null) {
			sql.append(" AND tci.money >= ? ");
			paramList.add(collectionFilter.getMinMoney());
		}
		
		if (collectionFilter.getMaxMoney() != null) {
			sql.append(" AND tci.money <= ? ");
			paramList.add(collectionFilter.getMaxMoney());
		}
		
		//付款方式
		if (!StringUtils.isBlank(collectionFilter.getPaymentWayId())) {
			sql.append(" AND tci.paymentWay = ? ");
			paramList.add(collectionFilter.getPaymentWayId());
		}
		
		return this.query(sql.toString(), paramList.toArray(), null);
	}
	
	/**
	 * 查询收款单统计信息
	 * @param crewId
	 * @return	总借款金额，币种ID，币种编码，币种名称，汇率
	 */
	public List<Map<String, Object>> queryCollectionStatistic (String crewId, CollectionInfoFilter collectionFilter) {
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(crewId);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	sum(tci.money) totalCollectionMoney, ");
		sql.append("    tcci.id currencyId, ");
		sql.append("    tcci.code currencyCode, ");
		sql.append("    tcci.name currencyName, ");
		sql.append("    tcci.exchangeRate ");
		sql.append(" FROM ");
		sql.append(" 	tab_collection_info tci, ");
		sql.append(" 	tab_finance_paymentWay_info tfpi, ");
		sql.append("    tab_currency_info tcci ");
		sql.append(" WHERE ");
		sql.append(" 	tci.crewId = ? ");
		sql.append(" AND tcci.id = tci.currencyId ");
		sql.append(" AND tci.paymentWay = tfpi.wayId ");
		
		if (!StringUtils.isBlank(collectionFilter.getCollectionIds())) {
			String collectionIds = "'" + collectionFilter.getCollectionIds().replace(",", "','") + "'";
			
			sql.append(" AND tci.collectionId in ("+ collectionIds +") ");
		}
		
		if (!StringUtils.isBlank(collectionFilter.getOtherUnits())) {
			String otherUnits = "'" + collectionFilter.getOtherUnits().replace(",", "','") + "'";
			
			sql.append(" AND tci.otherUnit in ("+ otherUnits +") ");
		}
		
		if (!StringUtils.isBlank(collectionFilter.getCollectionDates())) {
			String collectionDates = "'" + collectionFilter.getCollectionDates().replace(",", "','") + "'";
			
			sql.append(" AND tci.collectionDate in ("+ collectionDates +") ");
		}
		
		if (!StringUtils.isBlank(collectionFilter.getStartCollectionDate())) {
			paramList.add(collectionFilter.getStartCollectionDate());
			sql.append(" AND tci.collectionDate >= ? ");
		}
		
		if (!StringUtils.isBlank(collectionFilter.getEndCollectionDate())) {
			paramList.add(collectionFilter.getEndCollectionDate());
			sql.append(" AND tci.collectionDate <= ? ");
		}
		
		//收款月份
		if (!StringUtils.isBlank(collectionFilter.getCollectionMonth())) {
			paramList.add(collectionFilter.getCollectionMonth());
			sql.append(" AND DATE_FORMAT(tci.collectionDate,'%Y年%m月') = ? ");
		}
		
		if (!StringUtils.isBlank(collectionFilter.getAgents())) {
			String agents = "'" + collectionFilter.getAgents().replace(",", "','") + "'";
			
			sql.append(" AND tci.agent in ("+ agents +") ");
		}
		
		if (!StringUtils.isBlank(collectionFilter.getSummary())) {
			sql.append(" AND tci.summary like ? ");
			paramList.add("%" + collectionFilter.getSummary() + "%");
		}
		
		if (collectionFilter.getMinMoney() != null) {
			sql.append(" AND tci.money >= ? ");
			paramList.add(collectionFilter.getMinMoney());
		}
		
		if (collectionFilter.getMaxMoney() != null) {
			sql.append(" AND tci.money <= ? ");
			paramList.add(collectionFilter.getMaxMoney());
		}
		
		//付款方式
		if (!StringUtils.isBlank(collectionFilter.getPaymentWayId())) {
			sql.append(" AND tci.paymentWay = ? ");
			paramList.add(collectionFilter.getPaymentWayId());
		}
		
		sql.append("    group by tcci.id, ");
		sql.append("    tcci.code, ");
		sql.append("    tcci.name, ");
		sql.append("    tcci.exchangeRate ");
		
		return this.query(sql.toString(), paramList.toArray(), null);
	}
	
	
	
	/**
	 * 查询收款单列表 导出
	 * @param crewId
	 * @param collectionIds 收款单ID，多个以逗号隔开
	 * @param otherUnits	付款人，多个以逗号隔开
	 * @param collectionDates	收款日期，多个以逗号隔开
	 * @param agents	记账人，多个以逗号隔开
	 * @param summary	摘要
	 * @param minMoney	最小金额
	 * @param maxMoney	最大金额
	 * @return	收款日期， 创建时间， 收款单编号，摘要，总金额，付款人，支付方式，记账人，关联货币ID，关联货币编码，关联货币名称
	 */
	public List<Map<String, Object>> queryCollectionInfoListForExport (String crewId, String collectionIds, String otherUnits, 
			String collectionDates, String collectionMonth, String agents, String summary, Double minMoney, Double maxMoney ) {
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(crewId);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("    tci.collectionId, ");
		sql.append("    tci.collectionDate, ");
		sql.append("    tci.createTime, ");
		sql.append("    tci.receiptNo, ");
		sql.append("    tci.summary, ");
		sql.append("    tci.money, ");
		sql.append("    tci.otherUnit, ");
		sql.append("    tfpi.wayName paymentWay, ");
		sql.append("    tci.agent, ");
		sql.append("    tcci.id currencyId, ");
		sql.append("    tcci. CODE currencyCode, ");
		sql.append("    tcci. NAME currencyName, ");
		sql.append("    tca.contractNo acontractNo, ");
		sql.append("    tcw.contractNo wcontractNo, ");
		sql.append("    tcp.contractNo pcontractNo ");
		sql.append(" FROM ");
		sql.append("    tab_collection_info tci ");
		sql.append(" LEFT JOIN tab_contract_actor tca ON tca.actorName = tci.otherUnit ");
		sql.append(" AND tca.crewId = tci.crewId ");
		sql.append(" LEFT JOIN tab_contract_worker tcw ON tci.otherUnit = tcw.workerName ");
		sql.append(" AND tcw.crewId = tci.crewId ");
		sql.append(" LEFT JOIN tab_contract_produce tcp ON tcp.company = tci.otherUnit ");
		sql.append(" AND tci.crewId = tcp.crewId, ");
		sql.append("  tab_finance_paymentWay_info tfpi, ");
		sql.append("  tab_currency_info tcci ");
		sql.append(" WHERE ");
		sql.append("    tci.crewId = ? ");
		sql.append(" AND tcci.id = tci.currencyId ");
		sql.append(" AND tci.paymentWay = tfpi.wayId ");
		
		if (!StringUtils.isBlank(collectionIds)) {
			collectionIds = "'" + collectionIds.replace(",", "','") + "'";
			
			sql.append(" AND tci.collectionId in ("+ collectionIds +") ");
		}
		
		if (!StringUtils.isBlank(otherUnits)) {
			otherUnits = "'" + otherUnits.replace(",", "','") + "'";
			
			sql.append(" AND tci.otherUnit in ("+ otherUnits +") ");
		}
		
		if (!StringUtils.isBlank(collectionDates)) {
			collectionDates = "'" + collectionDates.replace(",", "','") + "'";
			
			sql.append(" AND tci.collectionDate in ("+ collectionDates +") ");
		}
		
		//收款月份
		if (!StringUtils.isBlank(collectionMonth)) {
			paramList.add(collectionMonth);
			sql.append(" AND DATE_FORMAT(tci.collectionDate,'%Y年%m月') = ? ");
		}
		
		if (!StringUtils.isBlank(agents)) {
			agents = "'" + agents.replace(",", "','") + "'";
			
			sql.append(" AND tci.agent in ("+ agents +") ");
		}
		
		if (!StringUtils.isBlank(summary)) {
			sql.append(" AND tci.summary like '?' ");
			paramList.add("%" + summary + "%");
		}
		
		if (minMoney != null) {
			sql.append(" AND tci.money >= ? ");
			paramList.add(minMoney);
		}
		
		if (maxMoney != null) {
			sql.append(" AND tci.money <= ? ");
			paramList.add(maxMoney);
		}
		
		return this.query(sql.toString(), paramList.toArray(), null);
	}
	
	/**
	 * 查询总收入
	 * @param crewId
	 * @return
	 */
	public Map<String, Object> queryTotalCollection(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT sum(tci.money * tcui.exchangeRate) totalCollectionMoney ");
		sql.append(" FROM tab_collection_info tci  ");
		sql.append(" LEFT JOIN tab_currency_info tcui ON tcui.id = tci.currencyId ");
		sql.append(" WHERE tci.crewId = ? ");
		return this.getJdbcTemplate().queryForMap(sql.toString(), crewId);
	}
}
