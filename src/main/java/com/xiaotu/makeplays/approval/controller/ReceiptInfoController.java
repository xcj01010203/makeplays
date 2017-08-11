package com.xiaotu.makeplays.approval.controller;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.approval.controller.filter.ReceiptInfoFilter;
import com.xiaotu.makeplays.approval.model.ApprovalInfoModel;
import com.xiaotu.makeplays.approval.model.ReceiptInfoModel;
import com.xiaotu.makeplays.approval.model.constants.ApprovalResultType;
import com.xiaotu.makeplays.approval.model.constants.ReceiptStatus;
import com.xiaotu.makeplays.approval.service.ApprovalInfoService;
import com.xiaotu.makeplays.approval.service.ReceiptInfoService;
import com.xiaotu.makeplays.attachment.dto.AttachmentDto;
import com.xiaotu.makeplays.attachment.model.AttachmentModel;
import com.xiaotu.makeplays.attachment.model.constants.AttachmentType;
import com.xiaotu.makeplays.attachment.service.AttachmentService;
import com.xiaotu.makeplays.finance.model.CurrencyInfoModel;
import com.xiaotu.makeplays.finance.service.CurrencyInfoService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.sysrole.service.SysRoleInfoService;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.model.constants.UserClientType;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.StringUtil;

/**
 * 单据信息
 * @author xuchangjian 2017-5-12上午10:43:24
 */
@Controller
@RequestMapping("/receiptInfoManager")
public class ReceiptInfoController extends BaseController{

	private Logger logger = LoggerFactory.getLogger(ReceiptInfoController.class);
	
	@Autowired
	private ReceiptInfoService receiptInfoService;
	
	@Autowired
	private ApprovalInfoService approvalInfoService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CurrencyInfoService currencyInfoService;
	
	@Autowired
	private AttachmentService attachmentService;
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private DecimalFormat df = new DecimalFormat("#,##0.00");
	
	private static Integer terminal = UserClientType.PC.getValue();
	
	/**
	 * 跳转到审批列表页面
	 * @return
	 */
	@RequestMapping("/toApprovalListPage")
	public ModelAndView toApprovalListPage() {
		ModelAndView mv = new ModelAndView("/finance/approval/approvalList");
		return mv;
	}
	
	/**
	 * 跳转到审批明细页面
	 * @param receiptId 单据ID
	 * @param receiptStatus 单据状态
	 * @param receiptType 单据类型,1-借款  2-报销  3-预算
	 * @param listType 单据列表类型,1-我的申请  2-我已审批3-待我审批
	 * @return
	 */
	@RequestMapping("/toApprovalDetailPage")
	public ModelAndView toApprovalDetailPage(String receiptId, String receiptStatus, String receiptType, String listType) {
		ModelAndView mv = new ModelAndView("/finance/approval/approvalDetail");
		mv.addObject("receiptId", receiptId);
		mv.addObject("receiptStatus", receiptStatus);
		mv.addObject("receiptType", receiptType);
		mv.addObject("listType", listType);
		return mv;
	}
	
	/**
	 * 查询单据列表
	 * @param request
	 * @param pageNo	当前页数
	 * @param pageSize	每页显示条数
	 * @param listType	单据列表类型，1-我的申请  2-我已审批3-待我审批
	 * @param receiptType	单据类型，1-借款  2-报销  3-预算
	 * @param receiptNo	单据编号（精确搜索）
	 * @param applyerName	申请人（模糊搜索）
	 * @param maxMoney	最大金额
	 * @param minMoney	最小金额
	 * @param startDate	开始日期
	 * @param endDate	结束日期
	 * @param description	单据说明（模糊搜索）
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryReceiptList")
	public Map<String, Object> queryReceiptList(HttpServletRequest request, 
			Page page, Integer listType, Integer receiptType, 
			String receiptNo, String applyerName, Double maxMoney,
			Double minMoney, String startDate, String endDate, String description) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			if (listType == null) {
				throw new IllegalArgumentException("单据类型不可为空");
			}
			
			ReceiptInfoFilter filter = new ReceiptInfoFilter();
			filter.setListType(listType);
			filter.setReceiptType(receiptType);
			filter.setReceiptNo(receiptNo);
			filter.setApplyerName(applyerName);
			filter.setMaxMoney(maxMoney);
			filter.setMinMoney(minMoney);
			filter.setStartDate(startDate);
			filter.setEndDate(endDate);
			filter.setDescription(description);

			String crewId = this.getCrewId(request);
			String userId = this.getLoginUserId(request);
			List<Map<String, Object>> receiptList = this.receiptInfoService.queryReceiptInfoList(crewId, userId, filter, page);
			
			boolean  singleCurrencyFlag= false;	//标识剧组中是否只有一个有效币种
			Map<String, Object> conditionMap = new HashMap<String, Object>();
        	conditionMap.put("crewId", crewId);
        	conditionMap.put("ifEnable", true);
        	List<CurrencyInfoModel> currencyInfoList = this.currencyInfoService.queryManyByMutiCondition(conditionMap, null);
			if (currencyInfoList.size() == 1) {
				singleCurrencyFlag = true;
			}
			for (Map<String, Object> receiptInfoMap : receiptList) {
				Date submitTime = (Date) receiptInfoMap.get("submitTime");
				String approverInfo = (String) receiptInfoMap.get("approverInfo");
				
				//处理币种
				if (!singleCurrencyFlag) {
					receiptInfoMap.put("money", this.df.format(receiptInfoMap.get("money")) + "(" + receiptInfoMap.get("currencyCode") + ")");
				} else {
					receiptInfoMap.put("money", this.df.format(receiptInfoMap.get("money")));
				}
				
				List<Map<String, Object>> approverList = new ArrayList<Map<String, Object>>();
				if(StringUtils.isNotBlank(approverInfo)) {
					String[] approverInfoArray = approverInfo.split("&&");
					for (String singleApproverInfo : approverInfoArray) {
						String[] singleApproverArray = singleApproverInfo.split("##");
						String userName = singleApproverArray[0];
						Integer resultType = Integer.parseInt(singleApproverArray[1]);
						
						Map<String, Object> myApproverInfo = new HashMap<String, Object>();
						myApproverInfo.put("userName", userName);
						myApproverInfo.put("resultType", resultType);
						approverList.add(myApproverInfo);
					}
				}
				
				String submitTimeStr = "";
				if (submitTime != null) {
					submitTimeStr = this.sdf2.format(submitTime);
				}

				receiptInfoMap.put("receiptDate", submitTimeStr);
				receiptInfoMap.put("approverList", approverList);
				receiptInfoMap.remove("createTime");
				receiptInfoMap.remove("approverInfo");
			}
			
			resultMap.put("receiptList", receiptList);
			resultMap.put("total", page.getTotal());
			resultMap.put("pageCount", page.getPageCount());
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(ie.getMessage(), ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询单据列表失败";
			logger.error(message, e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);		
		return resultMap;
	}
	
	/**
	 * 保存单据信息
	 * @param request
	 * @param receiptId	单据ID
	 * @param receiptType	单据类型，1-借款  2-报销  3-预算
	 * @param money	金额
	 * @param currencyId	币种ID
	 * @param description	说明
	 * @param approverIds	审批人，多个以英文逗号隔开
	 * @param operateType	操作类型，1-保存  2-提交
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveReceiptInfo")
	public Map<String, Object> saveReceiptInfo(HttpServletRequest request,
			String receiptId, Integer receiptType, Double money,
			String currencyId, String description, String approverIds,
			Integer operateType) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			if (receiptType == null) {
				throw new IllegalArgumentException("单据类型不能为空");
			}
			if (money == null) {
				throw new IllegalArgumentException("金额不能为空");
			}
			if (StringUtils.isBlank(currencyId)) {
				throw new IllegalArgumentException("币种不能为空");
			}
			if (operateType == null) {
				throw new IllegalArgumentException("操作类型不能为空");
			}
			if (operateType == 2 && StringUtils.isBlank(approverIds)) {
				throw new IllegalArgumentException("审批人不能为空");
			}
			String crewId = this.getCrewId(request);
			String userId = this.getLoginUserId(request);
			ReceiptInfoModel receiptInfo = this.receiptInfoService.saveReceiptInfo(crewId, userId, receiptId, 
					receiptType, money, currencyId, description, approverIds, operateType);
			
			resultMap.put("receiptNo", receiptInfo.getReceiptNo());
			resultMap.put("receiptId", receiptInfo.getId());
			resultMap.put("attpackId", receiptInfo.getAttpackId());

			if(StringUtils.isBlank(receiptId)) {
				this.sysLogService.saveSysLog(request, "保存单据", terminal, ReceiptInfoModel.TABLE_NAME, null, SysLogOperType.INSERT.getValue());
			} else {
				this.sysLogService.saveSysLog(request, "保存单据", terminal, ReceiptInfoModel.TABLE_NAME, null, SysLogOperType.UPDATE.getValue());
			}
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(ie.getMessage(), ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，保存单据失败";
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "保存单据失败：" + e.getMessage(), terminal, ReceiptInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询单据详细信息
	 * @param receiptId	单据ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryReceiptDetailInfo")
	public Map<String, Object> queryReceiptDetailInfo(HttpServletRequest request, String receiptId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(receiptId)) {
				throw new IllegalArgumentException("单据ID不能为空");
			}
			
			//单据详细信息
			ReceiptInfoModel receiptInfo = this.receiptInfoService.queryById(receiptId);
			if (receiptInfo == null) {
				throw new IllegalArgumentException("不存在的单据信息");
			}
			
			//申请人信息、币种信息
			UserInfoModel applyerInfo = this.userService.queryById(receiptInfo.getCreateUserId());
			CurrencyInfoModel currencyInfo = this.currencyInfoService.queryById(receiptInfo.getCurrencyId());
			
			//审批人信息
//			boolean isAllAuditing = true;	//标识单据是否全部是审核中
			List<Map<String, Object>> approvalInfoList = this.approvalInfoService.queryByReceiptIdWithApproverInfo(receiptId);
			List<Map<String, Object>> approverList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> approvalInfo : approvalInfoList) {
				String myApproverId = (String) approvalInfo.get("approverId");
				String userName = (String) approvalInfo.get("realName");
				String phone = (String) approvalInfo.get("phone");
				Integer resultType = (Integer) approvalInfo.get("resultType");
				String comment = (String) approvalInfo.get("comment");
				
//				if (resultType != ApprovalResultType.Auditing.getValue()) {
//					isAllAuditing = false;
//				}
				
				Map<String, Object> approverInfoMap = new HashMap<String, Object>();
				approverInfoMap.put("userId", myApproverId);
				approverInfoMap.put("userName", userName);
				approverInfoMap.put("phone", phone);
				approverInfoMap.put("resultType", resultType);
				if(approvalInfo.get("approvalTime") != null) {
					Date approvalTime = (Date) approvalInfo.get("approvalTime");
					approverInfoMap.put("approvalTime", this.sdf2.format(approvalTime));
				} else {
					approverInfoMap.put("approvalTime", "");
				}
				approverInfoMap.put("comment", comment);
				approverList.add(approverInfoMap);
			}
			//需要在审批列表第一个位置放上申请人信息
			if (approverList.size() > 0) {
				Map<String, Object> approverInfoMap = new HashMap<String, Object>();
				approverInfoMap.put("userId", applyerInfo.getUserId());
				approverInfoMap.put("userName", applyerInfo.getRealName());
				approverInfoMap.put("phone", applyerInfo.getPhone());
				approverInfoMap.put("resultType", ApprovalResultType.Launch.getValue());
				if(receiptInfo.getSubmitTime() != null) {
					approverInfoMap.put("approvalTime", this.sdf2.format(receiptInfo.getSubmitTime()));
				} else {
					approverInfoMap.put("approvalTime", "");
				}
				approverInfoMap.put("comment", "提交申请");
				
				approverList.add(0, approverInfoMap);
			}
			
			//如果单据是草稿状态，且所有审批都是“审批中”状态，则前端不需要展示审批进度（如果展示，会给用户错以为单据已经在审批中了）
			boolean showApprovalProgress = true;
			if (receiptInfo.getStatus() == ReceiptStatus.Draft.getValue()) {
				showApprovalProgress = false;
			}
			
			//附件信息
			List<AttachmentModel> attachmentList = this.attachmentService.queryAttByPackId(receiptInfo.getAttpackId());
			List<AttachmentDto> pictureList = new ArrayList<AttachmentDto>();
			List<AttachmentDto> otherAttachmentList = new ArrayList<AttachmentDto>();
			for (AttachmentModel attachmentInfo : attachmentList) {
				AttachmentDto attachmentDto = new AttachmentDto();
				attachmentDto.setAttachmentId(attachmentInfo.getId());
				attachmentDto.setAttpackId(attachmentInfo.getAttpackId());
				attachmentDto.setName(attachmentInfo.getName());
				attachmentDto.setType(attachmentInfo.getType());
				attachmentDto.setSuffix(attachmentInfo.getSuffix());
				attachmentDto.setLength(attachmentInfo.getLength());
				attachmentDto.setHdPreviewUrl(attachmentInfo.getHdStorePath());
				attachmentDto.setSdPreviewUrl(attachmentInfo.getSdStorePath());
				
				if (attachmentInfo.getType() == AttachmentType.Picture.getValue()) {
					pictureList.add(attachmentDto);
				} else {
					otherAttachmentList.add(attachmentDto);
				}
			}
			
			
			resultMap.put("type", receiptInfo.getType());
			resultMap.put("receiptNo", receiptInfo.getReceiptNo());
			resultMap.put("applyerId", receiptInfo.getCreateUserId());
			resultMap.put("applyerName", applyerInfo.getRealName());
			if(receiptInfo.getSubmitTime() != null) {
				resultMap.put("receiptTime", this.sdf2.format(receiptInfo.getSubmitTime()));
			} else {
				resultMap.put("receiptTime", "");
			}
			resultMap.put("money", receiptInfo.getMoney());
			resultMap.put("currencyId", currencyInfo.getId());
			resultMap.put("currencyName", currencyInfo.getName());
			resultMap.put("currencyCode", currencyInfo.getCode());
			resultMap.put("description", receiptInfo.getDescription());
			resultMap.put("status", receiptInfo.getStatus());
			resultMap.put("approverList", approverList);
			resultMap.put("pictureList", pictureList);
			resultMap.put("attachmentList", otherAttachmentList);
			resultMap.put("doneUserId", receiptInfo.getDoneUserId());
			resultMap.put("showApprovalProgress", showApprovalProgress);
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(ie.getMessage(), ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询单据详细信息失败";
			logger.error(message, e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 审批单据
	 * @param receiptId	单据ID
	 * @param resultType	结果类型：1-同意、2-不同意、3-退回、4-完结
	 * @param comment
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/approveReceipt")
	public Map<String, Object> approveReceipt(HttpServletRequest request, 
			String receiptId, Integer resultType, String comment) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(receiptId)) {
				throw new IllegalArgumentException("单据ID不能为空");
			}
			if (resultType == null) {
				throw new IllegalArgumentException("审批结果不能为空");
			}
			if (resultType == 2 && StringUtils.isBlank(comment)) {
				throw new IllegalArgumentException("请填写审批意见");
			}
			if(StringUtil.isNotBlank(comment) && comment.length() > 200) {
				throw new IllegalArgumentException("审批意见不能超过200字");
			}

			String crewId = this.getCrewId(request);
			String userId = this.getLoginUserId(request);
			this.receiptInfoService.approveReceipt(crewId, userId, receiptId, resultType, comment);

			this.sysLogService.saveSysLog(request, "审批单据", terminal, ReceiptInfoModel.TABLE_NAME, receiptId, SysLogOperType.UPDATE.getValue());
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，审批单据失败";
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "审批单据失败：" + e.getMessage(), terminal, ReceiptInfoModel.TABLE_NAME, receiptId, SysLogOperType.ERROR.getValue());
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 撤销单据
	 * @param receiptId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/revokeReceipt")
	public Map<String, Object> revokeReceipt(HttpServletRequest request, String receiptId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(receiptId)) {
				throw new IllegalArgumentException("单据ID不能为空");
			}
			this.receiptInfoService.revokeReceipt(receiptId);

			this.sysLogService.saveSysLog(request, "撤销单据", terminal, ReceiptInfoModel.TABLE_NAME, receiptId, SysLogOperType.UPDATE.getValue());
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，撤销单据失败";
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "撤销单据失败：" + e.getMessage(), terminal, ReceiptInfoModel.TABLE_NAME, receiptId, SysLogOperType.ERROR.getValue());
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 激活单据
	 * @param receiptId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/activeReceipt")
	public Map<String, Object> activeReceipt(HttpServletRequest request, String receiptId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(receiptId)) {
				throw new IllegalArgumentException("单据ID不能为空");
			}
			String userId = this.getLoginUserId(request);
			this.receiptInfoService.activeReceipt(userId, receiptId);
			this.sysLogService.saveSysLog(request, "激活单据", terminal, ReceiptInfoModel.TABLE_NAME, receiptId, SysLogOperType.UPDATE.getValue());
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，激活单据失败";
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "激活单据失败：" + e.getMessage(), terminal, ReceiptInfoModel.TABLE_NAME, receiptId, SysLogOperType.ERROR.getValue());
		}

		resultMap.put("success", success);
		resultMap.put("message", message);		
		return resultMap;
	}
	
	/**
	 * 删除单据(逻辑删除)
	 * @param receiptId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteReceipt")
	public Map<String, Object> deleteReceipt(HttpServletRequest request, String receiptId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(receiptId)) {
				throw new IllegalArgumentException("单据ID不能为空");
			}
			ReceiptInfoModel receiptInfo = this.receiptInfoService.queryById(receiptId);
			if ((receiptInfo.getStatus() != ReceiptStatus.Draft.getValue()) && (receiptInfo.getStatus() != ReceiptStatus.Rejected.getValue())) {
				throw new IllegalArgumentException("该单据不可删除");
			}
			receiptInfo.setStatus(ReceiptStatus.Deleted.getValue());
			this.receiptInfoService.updateOne(receiptInfo);
			this.sysLogService.saveSysLog(request, "删除单据(逻辑删除)", terminal, ReceiptInfoModel.TABLE_NAME, receiptId, SysLogOperType.UPDATE.getValue());
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，删除单据失败";
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "删除单据(逻辑删除)失败：" + e.getMessage(), terminal, ReceiptInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 添加审批人
	 * @param request
	 * @param receiptId	单据ID
	 * @param approverIds	审批人ID，多个以逗号隔开
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/addApprover")
	public Map<String, Object> addApprover(HttpServletRequest request, String receiptId, String approverIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(approverIds)) {
				throw new IllegalArgumentException("请选择要添加的审批人");
			}
			String crewId = this.getCrewId(request);
			String userId = this.getLoginUserId(request);
			this.receiptInfoService.addApprover(crewId, userId, receiptId, approverIds);			
			this.sysLogService.saveSysLog(request, "添加审批人", terminal, ApprovalInfoModel.TABLE_NAME, null, SysLogOperType.INSERT.getValue());
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(message, ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，添加审批人失败";
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "添加审批人失败：" + e.getMessage(), terminal, ApprovalInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
}
