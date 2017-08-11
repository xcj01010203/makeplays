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
import com.xiaotu.makeplays.finance.controller.filter.CollectionInfoFilter;
import com.xiaotu.makeplays.finance.dao.CollectionInfoDao;
import com.xiaotu.makeplays.finance.dao.FinancePaymentWayDao;
import com.xiaotu.makeplays.finance.model.CollectionInfoModel;
import com.xiaotu.makeplays.finance.model.FinancePaymentWayModel;
import com.xiaotu.makeplays.finance.model.FinanceSettingModel;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 收款单
 * @author xuchangjian 2016-8-17下午7:11:19
 */
@Service
public class CollectionInfoService {

	@Autowired
	private CollectionInfoDao collectionInfoDao;
	
	@Autowired
	private FinancePaymentWayDao financePaymentWayDao;
	
	@Autowired
	private AttachmentService attachmentService;
	
	@Autowired
	private FinanceSettingService financeSettingService;
	
	/**
	 * 根据多个条件查询收款单信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<CollectionInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.collectionInfoDao.queryManyByMutiCondition(conditionMap, page);
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
		return this.collectionInfoDao.queryMaxReceiptNo(crewId, payStatus, moonFirstDay, moonLastDay);
	}
	
	/**
	 * 保存收款单信息
	 * 
	 * @param crewId
	 * @param collectionId	收款单ID
	 * @param receiptNo	票据编号
	 * @param collectionDate	收款日期
	 * @param otherUnit	收款单位
	 * @param summary	摘要
	 * @param money	金额
	 * @param currencyId	货币ID
	 * @param paymentWay	支付方式
	 * @param agent	记账人
	 * @return 收款单ID
	 * @throws Exception 
	 */
	public CollectionInfoModel saveCollectionInfo (String crewId, String collectionId, 
			String receiptNo, String collectionDate, String otherUnit, 
			String summary, Double money, String currencyId, String paymentWay, String agent, String attpacketId) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		CollectionInfoModel collectionInfo = new CollectionInfoModel();
		if (!StringUtils.isBlank(collectionId)) {
			collectionInfo = this.collectionInfoDao.queryById(collectionId);
		} else {
			collectionInfo.setCollectionId(UUIDUtils.getId());
			collectionInfo.setCreateTime(new Date());
		}
		
		collectionInfo.setCrewId(crewId);
		collectionInfo.setReceiptNo(receiptNo.replace("-", ""));
		collectionInfo.setCollectionDate(sdf.parse(collectionDate));
		collectionInfo.setOtherUnit(otherUnit);
		collectionInfo.setSummary(summary);
		collectionInfo.setMoney(money);
		collectionInfo.setCurrencyId(currencyId);
		collectionInfo.setAgent(agent);
		//设置附件包id
		if (StringUtils.isBlank(attpacketId)) {
			attpacketId = this.attachmentService.createNewPacket(crewId, AttachmentBuzType.Contract.getValue());
		}
		collectionInfo.setAttpackId(attpacketId);
		
		//付款方式
		List<FinancePaymentWayModel> paymentWayList = this.financePaymentWayDao.queryByWayName(crewId, paymentWay);
		if (paymentWayList != null && paymentWayList.size() > 0) {
			collectionInfo.setPaymentWay(paymentWayList.get(0).getWayId());
		} else {
			FinancePaymentWayModel paymentWayModel = new FinancePaymentWayModel();
			paymentWayModel.setWayId(UUIDUtils.getId());
			paymentWayModel.setCrewId(crewId);
			paymentWayModel.setWayName(paymentWay);
			paymentWayModel.setCreateTime(new Date());
			
			this.financePaymentWayDao.add(paymentWayModel);
			collectionInfo.setPaymentWay(paymentWayModel.getWayId());
		}
		
		if (StringUtils.isBlank(collectionId)) {
			this.collectionInfoDao.add(collectionInfo);
		} else {
			this.collectionInfoDao.updateWithNull(collectionInfo, "collectionId");
		}
		
		return collectionInfo;
	}
	
	/**
	 * 查询收款单列表
	 * @param crewId
	 * @return	收款日期， 创建时间， 收款单编号，摘要，总金额，付款人，支付方式，记账人，关联货币ID，关联货币编码
	 */
	public List<Map<String, Object>> queryCollectionInfoList (String crewId, CollectionInfoFilter collectionFilter) {
		
		return this.collectionInfoDao.queryCollectionInfoList(crewId, collectionFilter);
	}
	
	/**
	 * 查询收款单统计信息
	 * @param crewId
	 * @return	总借款金额，币种ID，币种编码，币种名称，汇率
	 */
	public List<Map<String, Object>> queryCollectionStatistic (String crewId, CollectionInfoFilter collectionFilter) {
		return this.collectionInfoDao.queryCollectionStatistic(crewId, collectionFilter);
	}
	
	/**
	 * 查询收款单列表 到处excel
	 * @param crewId
	 * @param collectionIds 收款单ID，多个以逗号隔开
	 * @param otherUnits	付款人，多个以逗号隔开
	 * @param collectionDates	收款日期，多个以逗号隔开
	 * @param agents	记账人，多个以逗号隔开
	 * @param summary	摘要
	 * @param minMoney	最小金额
	 * @param maxMoney	最大金额
	 * @return	收款日期， 创建时间， 收款单编号，摘要，总金额，付款人，支付方式，记账人，关联货币ID，关联货币编码
	 */
	public List<Map<String, Object>> queryCollectionInfoListForExport (String crewId, String collectionIds, String otherUnits, 
			String collectionDates, String collectionMonth, String agents, String summary, Double minMoney, Double maxMoney ) {
		
		return this.collectionInfoDao.queryCollectionInfoListForExport(crewId, collectionIds, otherUnits, collectionDates, collectionMonth, agents, summary, minMoney, maxMoney);
	}
	
	/**
	 * 查询总收入
	 * @param crewId
	 * @return
	 */
	public Map<String, Object> queryTotalCollection(String crewId) {
		return this.collectionInfoDao.queryTotalCollection(crewId);
	}
	
	/**
	 * 根据ID查询收款单
	 * @param collectionId
	 * @return
	 * @throws Exception
	 */
	public CollectionInfoModel queryById(String collectionId) throws Exception {
		return this.collectionInfoDao.queryById(collectionId);
	}
	
	/**
	 * 根据剧组ID查询收款单信息
	 * @param crewId
	 * @return
	 */
	public List<CollectionInfoModel> queryByCrewId(String crewId) {
		return this.collectionInfoDao.queryByCrewId(crewId);
	}
	
	/**
	 * 删除收款单
	 * @param collectionId
	 * @throws Exception 
	 */
	public void deleteById(String collectionId) throws Exception {
		this.collectionInfoDao.deleteOne(collectionId, "collectionId", CollectionInfoModel.TABLE_NAME);
	}
	
	/**
	 * 批量新增
	 * @param collectionList
	 * @throws Exception
	 */
	public void addBatch(List<CollectionInfoModel> collectionList) throws Exception {
		this.collectionInfoDao.addBatch(collectionList, CollectionInfoModel.class);
	}
	
	/**
	 * 批量更新
	 * @param collectionList
	 * @throws Exception 
	 */
	public void updateBatch(List<CollectionInfoModel> collectionList) throws Exception {
		this.collectionInfoDao.updateBatch(collectionList, "collectionId", CollectionInfoModel.class);
	}
	
	/**
	 * 获取新的收款单单据号
	 * 调用该方法默认情况下，票据日期的月份改变了：例如，原来单据日期时2017-03，现在变成了2014-04
	 * @param crewId 剧组id
	 * @param collectionDate  操作日期
	 * @return  返回新的单据号
	 * @throws Exception
	 */
	public String getNewReceiptNo(String crewId, String collectionDate, String originalReceipNo) throws Exception {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		
		//如果付款日期为空，则默认设置为当天
		Date myCollectionDate = new Date();
		if (!StringUtils.isBlank(collectionDate)) {
			myCollectionDate = sdf1.parse(collectionDate);
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
				maxReceipNo = "SK00000000";
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
}
