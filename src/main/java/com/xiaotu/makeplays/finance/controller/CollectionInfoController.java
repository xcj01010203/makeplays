package com.xiaotu.makeplays.finance.controller;

import java.text.SimpleDateFormat;
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

import com.xiaotu.makeplays.attachment.model.AttachmentModel;
import com.xiaotu.makeplays.attachment.service.AttachmentService;
import com.xiaotu.makeplays.finance.controller.filter.CollectionInfoFilter;
import com.xiaotu.makeplays.finance.model.CollectionInfoModel;
import com.xiaotu.makeplays.finance.model.FinancePaymentWayModel;
import com.xiaotu.makeplays.finance.service.CollectionInfoService;
import com.xiaotu.makeplays.finance.service.FinancePaymentWayService;
import com.xiaotu.makeplays.finance.service.FinanceSettingService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;

/**
 * 收款单
 * @author xuchangjian 2016-8-17下午7:10:36
 */
@Controller
@RequestMapping("/collectionManager")
public class CollectionInfoController extends BaseController {
	
	Logger logger = LoggerFactory.getLogger(CollectionInfoController.class);
	
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private CollectionInfoService collectionInfoService;
	
	@Autowired
	private FinanceSettingService financeSettingService;
	
	@Autowired
	private FinancePaymentWayService financePaymentWayService;
	
	@Autowired
	private AttachmentService attachmentService;
	
	/**
	 * 跳转到收款单详细信息页面
	 * @param collectionId
	 * @return
	 */
	@RequestMapping("/toCollectionDetailInfo")
	public ModelAndView toCollectionDetailInfo(String collectionId, Boolean needClosePage) {
		ModelAndView mv = new ModelAndView("/finance/getcost/collectionDetailInfo");
		
		if (needClosePage == null) {
			needClosePage = false;
		}
		
		mv.addObject("collectionId", collectionId);
		mv.addObject("needClosePage", needClosePage);
		return mv;
	}
	
	/**
	 * 跳转到打印收款单信息页面
	 * @param collectionId
	 * @return
	 */
	@RequestMapping("/toPrintCollectionInfoPage")
	public ModelAndView toPrintCollectionInfoPage(String collectionIds, Boolean needClosePage, Boolean needBacktoPage) {
		ModelAndView mv = new ModelAndView("/finance/getcost/printCollectionInfo");
		if (needClosePage == null) {
			needClosePage = false;
		}
		mv.addObject("collectionIds", collectionIds);
		mv.addObject("needClosePage", needClosePage);
		mv.addObject("needBacktoPage", needBacktoPage);
		return mv;
	}
	
	/**
	 * 获取最新的票据编号
	 * @param request
	 * @param collectionDate
	 * @param originalReceipNo 已有编号
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryNewReceiptNo")
	public Map<String, Object> queryNewReceiptNo(HttpServletRequest request, String collectionDate, String originalReceipNo) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	if (!StringUtils.isBlank(originalReceipNo)) {
        		originalReceipNo = originalReceipNo.replace("-", "");
        	}
        	String newReceiptNo = this.collectionInfoService.getNewReceiptNo(crewId, collectionDate, originalReceipNo);
        	resultMap.put("newReceiptNo", this.formatReceiptNo(newReceiptNo));
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
        } catch(Exception e) {
            success = false;
            message = "未知异常";

            logger.error("未知异常", e);
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
	
	/**
	 * 格式化票据编号
	 * @param receiptNo
	 * @return
	 */
	private String formatReceiptNo(String receiptNo) {
    	String newReceiptNo = receiptNo.substring(0, 2) + "-" + receiptNo.substring(2, 6) + "-" + receiptNo.substring(6, receiptNo.length());
    	
    	return newReceiptNo;
	}
	
	
	/**
	 * 保存收款单信息
	 * @param request
	 * @param collectionId	收款单ID
	 * @param receiptNo	票据编号
	 * @param collectionDate	收款日期
	 * @param otherUnit	收款单位
	 * @param summary	摘要
	 * @param money	金额
	 * @param currencyId	货币ID
	 * @param paymentWay	支付方式
	 * @param agent	记账人
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveCollectionInfo")
	public Map<String, Object> saveCollectionInfo(HttpServletRequest request, String collectionId, 
			String collectionDate, String otherUnit, 
			String summary, Double money, String currencyId, String paymentWay, String agent, String attpacketId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        String receiptNo = "";
        try {
        	String crewId = this.getCrewId(request);
        	if (StringUtils.isBlank(collectionDate)) {
        		throw new IllegalArgumentException("请填写收款日期");
        	}
        	if (StringUtils.isBlank(otherUnit)) {
        		throw new IllegalArgumentException("请填写付款人");
        	}
        	if (money == null) {
        		throw new IllegalArgumentException("请填写金额");
        	}
        	if (StringUtils.isBlank(currencyId)) {
        		throw new IllegalArgumentException("请选择币种");
        	}
        	if (StringUtils.isBlank(paymentWay)) {
        		throw new IllegalArgumentException("请选择付款方式");
        	}
        	if (StringUtils.isBlank(agent)) {
        		throw new IllegalArgumentException("请填写记账人");
        	}
        	if (!StringUtils.isBlank(summary) && summary.length() > 100) {
        		throw new IllegalArgumentException("摘要需控制再100字以内");
        	}
        	
        	//生成票据编号
        	CollectionInfoModel originalCollectionInfo = this.collectionInfoService.queryById(collectionId);
        	if (originalCollectionInfo != null) {
        		receiptNo = originalCollectionInfo.getReceiptNo();
        		if (!this.sdf1.format(originalCollectionInfo.getCollectionDate()).substring(0,  7).equals(collectionDate.substring(0, 7))) {
        			receiptNo = this.collectionInfoService.getNewReceiptNo(crewId, collectionDate, receiptNo);
        		}
        	} else {
        		receiptNo = this.collectionInfoService.getNewReceiptNo(crewId, collectionDate, receiptNo);
        	}
        	
        	
        	
        	CollectionInfoModel collectionInfo = this.collectionInfoService.saveCollectionInfo(crewId, collectionId, receiptNo, collectionDate, 
        			otherUnit, summary, money, currencyId, paymentWay, agent, attpacketId);
        	boolean flag = false; //标识新增、修改，false：新增
        	if (StringUtils.isNotBlank(collectionId)) {
        		flag = true;
        	}
        	
			attpacketId = collectionInfo.getAttpackId();
			collectionId = collectionInfo.getCollectionId();
			
        	resultMap.put("attpacketId", attpacketId);
        	resultMap.put("collectionId", collectionId);
        	if (!flag) {
            	this.sysLogService.saveSysLog(request, "新增收款单信息", Constants.TERMINAL_PC, CollectionInfoModel.TABLE_NAME, receiptNo, SysLogOperType.INSERT.getValue());
        	} else {
            	this.sysLogService.saveSysLog(request, "修改收款单信息", Constants.TERMINAL_PC, CollectionInfoModel.TABLE_NAME, receiptNo, SysLogOperType.UPDATE.getValue());
        	}
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
        } catch(Exception e) {
            success = false;
            message = "未知异常";

            logger.error("未知异常", e);
        	this.sysLogService.saveSysLog(request, "保存收款单信息失败：" + e.getMessage(), Constants.TERMINAL_PC, CollectionInfoModel.TABLE_NAME, receiptNo, 6);
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
	
	/**
	 * 查询收款单的详细信息
	 * @param collectionId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryCollectionDetailInfo")
	public Map<String, Object> queryCollectionDetailInfo (HttpServletRequest request, String collectionId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(collectionId)) {
				throw new IllegalArgumentException("请提供收款单ID");
			}
			
			String crewId = this.getCrewId(request);
			
			CollectionInfoModel collectionInfo = this.collectionInfoService.queryById(collectionId);
			
			Map<String, Object> collectionInfoMap = new HashMap<String, Object>();
			collectionInfoMap.put("collectionId", collectionInfo.getCollectionId());
			collectionInfoMap.put("receiptNo", this.formatReceiptNo(collectionInfo.getReceiptNo()));
			collectionInfoMap.put("collectionDate", this.sdf1.format(collectionInfo.getCollectionDate()));
			collectionInfoMap.put("otherUnit", collectionInfo.getOtherUnit());
			collectionInfoMap.put("summary", collectionInfo.getSummary());
			collectionInfoMap.put("currencyId", collectionInfo.getCurrencyId());
			collectionInfoMap.put("money", collectionInfo.getMoney());
			
			FinancePaymentWayModel paymentWayModel = this.financePaymentWayService.queryById(crewId, collectionInfo.getPaymentWay());
			collectionInfoMap.put("paymentWay", paymentWayModel.getWayName());
			
			collectionInfoMap.put("agent", collectionInfo.getAgent());
			
			//获取附件信息列表
			List<AttachmentModel> attachmentList = this.attachmentService.queryAttByPackId(collectionInfo.getAttpackId());
			
			resultMap.put("attachmentList", attachmentList);
			resultMap.put("collectionInfo", collectionInfoMap);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	
	/**
	 * 查询多个收款单的详细信息（打印用）
	 * 该查询方法返回的币种是字符串文本值
	 * @param collectionIds 收款单ID，多个以逗号隔开
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryManyCollectionDetailInfo")
	public Map<String, Object> queryManyCollectionDetailInfo (HttpServletRequest request, String collectionIds) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(collectionIds)) {
				throw new IllegalArgumentException("请提供收款单ID");
			}
			
			String crewId = this.getCrewId(request);
			CollectionInfoFilter filter = new CollectionInfoFilter();
			filter.setCollectionIds(collectionIds);
			List<Map<String, Object>> collectionList = this.collectionInfoService.queryCollectionInfoList(crewId, filter);
			
			resultMap.put("collectionList", collectionList);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
		}

		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
}
