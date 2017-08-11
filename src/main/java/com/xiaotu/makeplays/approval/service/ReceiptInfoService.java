package com.xiaotu.makeplays.approval.service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.approval.controller.filter.ReceiptInfoFilter;
import com.xiaotu.makeplays.approval.dao.ReceiptInfoDao;
import com.xiaotu.makeplays.approval.model.ApprovalInfoModel;
import com.xiaotu.makeplays.approval.model.ReceiptInfoModel;
import com.xiaotu.makeplays.approval.model.constants.ApprovalResultType;
import com.xiaotu.makeplays.approval.model.constants.ReceiptStatus;
import com.xiaotu.makeplays.attachment.model.constants.AttachmentBuzType;
import com.xiaotu.makeplays.attachment.service.AttachmentService;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.message.model.MessageInfoModel;
import com.xiaotu.makeplays.message.model.constants.MessageInfoStatus;
import com.xiaotu.makeplays.message.model.constants.MessageType;
import com.xiaotu.makeplays.message.service.MessageInfoService;
import com.xiaotu.makeplays.mobile.push.umeng.model.android.AndroidPushMsg;
import com.xiaotu.makeplays.mobile.push.umeng.model.ios.IOSPushMsg;
import com.xiaotu.makeplays.mobile.push.umeng.service.android.UmengAndroidPushService;
import com.xiaotu.makeplays.mobile.push.umeng.service.ios.UmengIOSPushService;
import com.xiaotu.makeplays.sysrole.service.SysRoleInfoService;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.model.constants.UserClientType;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 单据信息
 * @author xuchangjian 2017-5-12上午10:42:23
 */
@Service
public class ReceiptInfoService {

	@Autowired
	private ReceiptInfoDao receiptInfoDao;
	
	@Autowired
	private AttachmentService attachmentService;
	
	@Autowired
	private ApprovalInfoService approvalInfoService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private MessageInfoService messageInfoService;
	
	@Autowired
	private UmengIOSPushService umengIOSPushService;
	
	@Autowired
	private UmengAndroidPushService umengAndroidPushService;
	
	@Autowired
	private CrewInfoService crewInfoService;
	
	@Autowired
	private SysRoleInfoService sysRoleInfoService;
	
	/**
	 * 根据ID查询单据信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ReceiptInfoModel queryById(String id) throws Exception {
		return this.receiptInfoDao.queryById(id);
	}
	
	/**
	 * 保存单据信息
	 * @param crewId
	 * @param userId
	 * @param receiptId	单据ID
	 * @param receiptType	单据类型，1-借款  2-报销  3-预算
	 * @param money	金额
	 * @param currencyId	币种ID
	 * @param description	说明
	 * @param approverIds	审批人，多个以英文逗号隔开
	 * @param operateType	操作类型，1-保存  2-提交
	 * @return
	 * @throws Exception 
	 */
	public ReceiptInfoModel saveReceiptInfo(String crewId, String userId, String receiptId, 
			Integer receiptType, Double money, String currencyId, 
			String description, String approverIds, Integer operateType) throws Exception {

		if(operateType != 1) {
			//判断最后一个审批人是否是财务
			if(StringUtils.isNotBlank(approverIds)) {
				String[] approverIdArr = approverIds.split(",");
				boolean isFinance = this.isFinance(crewId, approverIdArr[approverIdArr.length - 1]);
				if(!isFinance) {
					throw new IllegalArgumentException("最后一个审批人必须是财务");
				}
			}
		}
		//保存单据的基本信息
		ReceiptInfoModel receiptInfo = new ReceiptInfoModel();
		if (!StringUtils.isBlank(receiptId)) {
			receiptInfo = this.receiptInfoDao.queryById(receiptId);
		} else {
			receiptInfo.setId(UUIDUtils.getId());
			receiptInfo.setReceiptNo(this.getNewReceiptNo(crewId, receiptType));
			receiptInfo.setType(receiptType);
			receiptInfo.setCreateTime(new Date());
			receiptInfo.setAttpackId(this.attachmentService.createNewPacket(crewId, AttachmentBuzType.ApprovalReceipt.getValue()));
		}
		receiptInfo.setCrewId(crewId);
		receiptInfo.setCreateUserId(userId);
		if (operateType == 1) {
			receiptInfo.setStatus(ReceiptStatus.Draft.getValue());
			receiptInfo.setSubmitTime(null);
		} else {
			receiptInfo.setStatus(ReceiptStatus.Auditing.getValue());
			receiptInfo.setSubmitTime(new Date());
		}
		receiptInfo.setMoney(money);
		receiptInfo.setCurrencyId(currencyId);
		receiptInfo.setDescription(description);		
		if (!StringUtils.isBlank(receiptId)) {
			this.updateOne(receiptInfo);
		} else {
			this.receiptInfoDao.add(receiptInfo);
		}
		
		
		//删除单据之前的审批信息
		if (!StringUtils.isBlank(receiptId)) {
			this.approvalInfoService.deleteByReceiptId(receiptId);
		}
		
		//保存单据的审批信息
		String[] approverIdArray = approverIds.split(",");
		List<ApprovalInfoModel> approvalInfoList = new ArrayList<ApprovalInfoModel>();
		for (int i = 0; i < approverIdArray.length; i++) {
			String approverId = approverIdArray[i];
			
			ApprovalInfoModel approvalInfo = new ApprovalInfoModel();
			approvalInfo.setId(UUIDUtils.getId());
			approvalInfo.setCrewId(crewId);
			approvalInfo.setReceiptId(receiptInfo.getId());
			approvalInfo.setApproverId(approverId);
			approvalInfo.setSequence(i + 1);
			approvalInfo.setResultType(ApprovalResultType.Auditing.getValue());
			approvalInfo.setCreateTime(new Date());
			
			approvalInfoList.add(approvalInfo);
		}
		this.approvalInfoService.addBatch(approvalInfoList);
		
		//给审批人发送消息，提交的时候需要发送消息，只给第一个审批人
		if (operateType == 2) {
			String sendApproverId = "";
			if(StringUtils.isNotBlank(approverIds)) {
				sendApproverId = approverIdArray[0];
			}
			this.sendMessage(crewId, userId, receiptId, receiptInfo.getReceiptNo(), sendApproverId, true);
		}
		
		return receiptInfo;
	}

	
	/**
	 * 给审批人发送消息
	 * @param crewId
	 * @param userId	单据申请人ID
	 * @param receiptId	单据ID
	 * @param receiptNo	单据编号
	 * @param approverIds	审批人ID，多个用逗号隔开
	 * @throws Exception
	 */
	private void sendMessage(String crewId, String userId, String receiptId, String receiptNo, String approverIds, boolean isDelete) throws Exception {
		//删除该单据之前的消息记录
		if (!StringUtils.isBlank(receiptId) && isDelete) {
			this.messageInfoService.deleteByBuzId(receiptId);
		}
		
		/*
		 * 向审批人推送消息
		 */
		String title = "单据审批消息";
		String content = "您有一张新的单据需要审批，单据号：" + receiptNo;
		
		List<UserInfoModel> userList = this.userService.queryByIds(approverIds);
		List<String> androidTokenList = new ArrayList<String>();
		List<String> iosTokenList = new ArrayList<String>();
		
		List<MessageInfoModel> messageInfoList = new ArrayList<MessageInfoModel>();
		for (UserInfoModel userInfo : userList) {
			String token = userInfo.getToken();
			Integer clientType = userInfo.getClientType();
			if (!StringUtils.isBlank(token) && clientType != null) {
				if (clientType == UserClientType.Android.getValue()) {
					androidTokenList.add(token);
				}
				if (clientType == UserClientType.IOS.getValue()) {
					iosTokenList.add(token);
				}
			}
			
			MessageInfoModel messageInfo = new MessageInfoModel();
			messageInfo.setId(UUIDUtils.getId());
			messageInfo.setCrewId(crewId);
			messageInfo.setSenderId(userId);
			messageInfo.setReceiverId(userInfo.getUserId());
			messageInfo.setType(MessageType.Approval.getValue());
			messageInfo.setBuzId(receiptId);
			messageInfo.setStatus(MessageInfoStatus.UnRead.getValue());
			messageInfo.setTitle(title);
			messageInfo.setContent(content);
			messageInfo.setRemindTime(new Date());
			messageInfo.setCreateTime(new Date());
			
			messageInfoList.add(messageInfo);
		}
		this.messageInfoService.addMany(messageInfoList);
		
		
		CrewInfoModel crewInfo = this.crewInfoService.queryById(crewId);
		content = "《" + crewInfo.getCrewName() + "》" + content;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String myTime = sdf.format(new Date());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", MessageType.Approval.getValue());
		map.put("time", myTime);
		map.put("crewId", crewId);
		map.put("crewName", crewInfo.getCrewName());
		
		//IOS推送
		IOSPushMsg msg = new IOSPushMsg();
		msg.setTokenList(iosTokenList);
		msg.setAlert(content);
		msg.setCustomDictionaryMap(map);
		this.umengIOSPushService.iOSPushMsg(msg);
		
		//android推送
		AndroidPushMsg androidMsg = new AndroidPushMsg();
		androidMsg.setTokenList(androidTokenList);
		androidMsg.setTicker(content);
		androidMsg.setTitle(title);
		androidMsg.setText(content);
		androidMsg.setCustomDictionaryMap(map);
		this.umengAndroidPushService.androidPushMsg(androidMsg);
	}
	
	/**
	 * 获取单据最新编号
	 * @param crewId
	 * @param receiptType
	 * @return
	 * @throws Exception 
	 */
	private String getNewReceiptNo(String crewId, Integer receiptType) throws Exception {
		DecimalFormat df = new DecimalFormat("000000");
		
		ReceiptInfoModel receiptInfo = this.receiptInfoDao.queryLastReceipt(crewId, receiptType);
		String suffix = "000000";
		if (receiptInfo != null) {
			suffix = receiptInfo.getReceiptNo().substring(1, receiptInfo.getReceiptNo().length());
		}
		
		String prefix = "";
		if (receiptType == 1) {
			prefix = "J";
		}
		if (receiptType == 2) {
			prefix = "B";
		}
		if (receiptType == 3) {
			prefix = "Y";
		}
		
		Integer myNumber = Integer.parseInt(suffix);
		
		String newReceiptNo = prefix + df.format(myNumber + 1);
		return newReceiptNo;
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
		return this.receiptInfoDao.queryReceiptInfoList(crewId, userId, filter, page);
	}
	
	/**
	 * 审批单据
	 * @param userId
	 * @param receiptId	单据ID
	 * @param resultType	结果类型：1-同意、2-不同意、3-退回、4-完结
	 * @param comment
	 * @return
	 * @throws Exception 
	 */
	public void approveReceipt(String crewId, String userId, String receiptId, Integer resultType, String comment) throws Exception {
		ApprovalInfoModel approvalInfo = this.approvalInfoService.queryByRecieptIdAndApproverId(receiptId, userId);
		
		if(approvalInfo == null) {
			throw new IllegalArgumentException("对不起，您已经不能对该单据进行审批，请刷新");
		}
		
		ReceiptInfoModel receiptInfo = this.receiptInfoDao.queryById(receiptId);
		
		approvalInfo.setApprovalTime(new Date());
		approvalInfo.setComment(comment);
		if (resultType == 1) {
			approvalInfo.setResultType(ApprovalResultType.Agree.getValue());
		}
		if (resultType == 2) {
			approvalInfo.setResultType(ApprovalResultType.NotAgree.getValue());
			receiptInfo.setStatus(ReceiptStatus.Rejected.getValue());
			this.updateOne(receiptInfo);
		}
		if (resultType == 3) {
			approvalInfo.setResultType(ApprovalResultType.Return.getValue());
		}
		if (resultType == 4) {
			approvalInfo.setResultType(ApprovalResultType.Agree.getValue());
			receiptInfo.setStatus(ReceiptStatus.Done.getValue());
			receiptInfo.setDoneUserId(userId);
			this.updateOne(receiptInfo);
		}
		this.approvalInfoService.updateOne(approvalInfo);
		
		/*
		 * 向申请人推送消息
		 * 暂定为只有当单据被完结或被拒绝的情况下，才通知申请人审批结果
		 */
		if (resultType == 2 || resultType == 4) {
			String title = "单据审核消息";
			String content = "";
			if (resultType == 4) {
				content = "您的单据" + receiptInfo.getReceiptNo() + "审批通过";
			}
			if (resultType == 2) {
				content = "您的单据" + receiptInfo.getReceiptNo() + "审批被拒，请及时查看";
			}
			
			UserInfoModel applyUserInfo = this.userService.queryById(receiptInfo.getCreateUserId());	//申请人
			List<String> androidTokenList = new ArrayList<String>();
			List<String> iosTokenList = new ArrayList<String>();
			if (!StringUtils.isBlank(applyUserInfo.getToken()) && applyUserInfo.getClientType() != null) {
				if (applyUserInfo.getClientType() == UserClientType.Android.getValue()) {
					androidTokenList.add(applyUserInfo.getToken());
				}
				if (applyUserInfo.getClientType() == UserClientType.IOS.getValue()) {
					iosTokenList.add(applyUserInfo.getToken());
				}
			}
			MessageInfoModel messageInfo = new MessageInfoModel();
			messageInfo.setId(UUIDUtils.getId());
			messageInfo.setCrewId(crewId);
			messageInfo.setSenderId(userId);
			messageInfo.setReceiverId(applyUserInfo.getUserId());
			messageInfo.setType(MessageType.Approval.getValue());
			messageInfo.setBuzId(receiptId);
			messageInfo.setStatus(MessageInfoStatus.UnRead.getValue());
			messageInfo.setTitle(title);
			messageInfo.setContent(content);
			messageInfo.setRemindTime(new Date());
			messageInfo.setCreateTime(new Date());
			this.messageInfoService.addOne(messageInfo);
			
			CrewInfoModel crewInfo = this.crewInfoService.queryById(crewId);
			content = "《" + crewInfo.getCrewName() + "》" + content;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String myTime = sdf.format(new Date());
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("type", MessageType.Approval.getValue());
			map.put("time", myTime);
			map.put("crewId", crewId);
			map.put("crewName", crewInfo.getCrewName());
			
			//IOS推送
			IOSPushMsg msg = new IOSPushMsg();
			msg.setTokenList(iosTokenList);
			msg.setAlert(content);
			msg.setCustomDictionaryMap(map);
			this.umengIOSPushService.iOSPushMsg(msg);
			
			//android推送
			AndroidPushMsg androidMsg = new AndroidPushMsg();
			androidMsg.setTokenList(androidTokenList);
			androidMsg.setTicker(content);
			androidMsg.setTitle(title);
			androidMsg.setText(content);
			androidMsg.setCustomDictionaryMap(map);
			this.umengAndroidPushService.androidPushMsg(androidMsg);
		}
		
		/*
		 * 向下一个审批人推送消息
		 * 当单据审批状态为同意、退回
		 */
		if (resultType == 1 || resultType == 3) {
			ApprovalInfoModel nextApproval = this.approvalInfoService.queryNextApprover(receiptId, approvalInfo.getSequence());
			
			String nextApprovalId = "";
			if(nextApproval != null) {
				nextApprovalId = nextApproval.getApproverId();
			}
			this.sendMessage(crewId, userId, receiptId, receiptInfo.getReceiptNo(), nextApprovalId, false);
		}
	}
	
	/**
	 * 激活单据
	 * @param userId
	 * @param receiptId
	 * @return
	 * @throws Exception 
	 */
	public void activeReceipt(String userId, String receiptId) throws Exception {
		ApprovalInfoModel approvalInfo = this.approvalInfoService.queryByRecieptIdAndApproverId(receiptId, userId);
		ReceiptInfoModel receiptInfo = this.receiptInfoDao.queryById(receiptId);
		
		approvalInfo.setResultType(ApprovalResultType.Auditing.getValue());
		receiptInfo.setStatus(ReceiptStatus.Auditing.getValue());
		
		this.approvalInfoService.updateOne(approvalInfo);
		this.updateOne(receiptInfo);
	}
	
	/**
	 * 更新一条记录
	 * @param receiptInfo
	 * @throws Exception 
	 */
	public void updateOne(ReceiptInfoModel receiptInfo) throws Exception {
		this.receiptInfoDao.updateWithNull(receiptInfo, "id");
	}
	
	/**
	 * 撤销单据
	 * @param receiptId
	 * @throws Exception 
	 */
	public void revokeReceipt(String receiptId) throws Exception {
		ReceiptInfoModel receiptInfo = this.queryById(receiptId);
		receiptInfo.setStatus(ReceiptStatus.Draft.getValue());
		receiptInfo.setSubmitTime(null);
		this.updateOne(receiptInfo);
		
		//把所有的审批状态更新为“审批中”
		this.approvalInfoService.updateApprovalResultByReceiptId(receiptId, ApprovalResultType.Auditing.getValue());
	}
	
	/**
	 * 为单据添加审批人
	 * @param crewId
	 * @param userId
	 * @param receiptId	单据ID
	 * @param approverIds	审批人ID，多个以逗号隔开
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/addApprover")
	public void addApprover(String crewId, String userId, String receiptId, String approverIds) throws Exception {
		ReceiptInfoModel receiptInfo = this.receiptInfoDao.queryById(receiptId);
		List<ApprovalInfoModel> approvalList = this.approvalInfoService.queryByReceiptId(receiptId);

		String[] approverIdArray = approverIds.split(",");
		int startIndex = approvalList.size(); //标识未审批的人的起始位置
		boolean isSend = false; //标识第一个未审批的人是否已发送过通知消息
		for(int i = 0; i < approvalList.size(); i++) {
			ApprovalInfoModel approval = approvalList.get(i);
			if(approval.getResultType() != 1) {
				if(approverIdArray.length > i) {
					if(!approval.getApproverId().equals(approverIdArray[i])) {
						throw new IllegalArgumentException("已审批过的审批人顺序不能更改");
					}
				} else {
					throw new IllegalArgumentException("已审批过的审批人不能删除");
				}
			} else {
				startIndex = i;
				if(approverIdArray.length > i) {
					if(approval.getApproverId().equals(approverIdArray[i])) {
						isSend = true;
					}
				}
				break;
			}
		}
		//判断最后一个审批人是否是财务
		boolean isFinance = this.isFinance(crewId, approverIdArray[approverIdArray.length - 1]);
		if(!isFinance) {
			throw new IllegalArgumentException("最后一个审批人必须是财务");
		}
		
		//删除未审批的审批人信息
		this.approvalInfoService.deleteAuditingByReceiptId(receiptId);
		
		List<ApprovalInfoModel> toAddApprovalList = new ArrayList<ApprovalInfoModel>();
		for (int i = startIndex ; i < approverIdArray.length; i++) {
			ApprovalInfoModel newApprovalInfo = new ApprovalInfoModel();
			newApprovalInfo.setId(UUIDUtils.getId());
			newApprovalInfo.setCrewId(crewId);
			newApprovalInfo.setReceiptId(receiptId);
			newApprovalInfo.setApproverId(approverIdArray[i]);
			newApprovalInfo.setSequence(i + 1);
			newApprovalInfo.setResultType(ApprovalResultType.Auditing.getValue());
			newApprovalInfo.setCreateTime(new Date());
			
			toAddApprovalList.add(newApprovalInfo);
		}
		this.approvalInfoService.addBatch(toAddApprovalList);
		//给下一个审批人发送消息，如果已经发送过，不再发送
		if(!isSend) {
			String sendApproverId = "";
			if(approverIdArray.length > startIndex) {
				sendApproverId = approverIdArray[startIndex];
			}
			this.sendMessage(crewId, userId, receiptId, receiptInfo.getReceiptNo(), sendApproverId, false);
		}
	}
	
	/**
	 * 判断是否是财务
	 * @param crewId
	 * @param userId
	 * @return
	 */
	private boolean isFinance(String crewId, String userId) {
		List<Map<String, Object>> myRoleList = this.sysRoleInfoService.queryByCrewUserId(crewId, userId);
		boolean isFinance = false;
		for (Map<String, Object> myRoleInfo : myRoleList) {
			String roleName = (String) myRoleInfo.get("roleName");
			if (roleName.equals("财务")) {
				isFinance = true;
				break;
			}
		}
		return isFinance;
	}
}
