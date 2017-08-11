package com.xiaotu.makeplays.finance.controller;

import java.util.HashMap;
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

import com.xiaotu.makeplays.finance.model.FinanceSettingModel;
import com.xiaotu.makeplays.finance.service.FinanceSettingService;
import com.xiaotu.makeplays.finance.service.FinanceSubjectService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.IpUtil;
import com.xiaotu.makeplays.utils.MD5Util;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.verifycode.model.VerifyCodeInfoModel;
import com.xiaotu.makeplays.verifycode.model.constants.VerifyCodeType;
import com.xiaotu.makeplays.verifycode.service.VerifyCodeInfoService;

/**
 * 财务设置
 * @author xuchangjian 2016-8-12上午9:26:41
 */
@Controller
@RequestMapping("/financeSettingManager")
public class FinanceSettingController extends BaseController {
	
	Logger logger = LoggerFactory.getLogger(FinanceSettingController.class);

	@Autowired
	private FinanceSettingService financeSettingService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private VerifyCodeInfoService verifyCodeInfoService;
	
	@Autowired
	private FinanceSubjectService financeSubjectService;
	
	/**
	 * 跳转到财务设置页面
	 * @param activeTagType 触发的标签类型  1-币种设置   2-单据设置  3-财务密码  4-缴税设置
	 * @return
	 */
	@RequestMapping("/toFinanceSettingPage")
	public ModelAndView toFinanceSettingPage(Integer activeTagType) {
		ModelAndView mv = new ModelAndView("/finance/financeSetting");
		if (activeTagType == null) {
			activeTagType = 1;
		}
		mv.addObject("activeTagType", activeTagType);
		return mv;
	}
	
	/**
	 * 校验是否设置了单据信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/nopassword/checkBillHasSetted")
	public Map<String, Object> checkBillHasSetted(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	
        	boolean hasSetted = false;
        	
        	FinanceSettingModel financeSetting = this.financeSettingService.queryByCrewId(crewId);
        	if (financeSetting == null) {
        		financeSetting = this.financeSettingService.initFinanceSetting(crewId);
        	}
        	if (financeSetting != null && financeSetting.getHasReceiptStatus() != null && financeSetting.getPayStatus() != null) {
        		hasSetted = true;
        	}
        	
        	resultMap.put("hasSetted", hasSetted);
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
	 * 校验是否需要的财务密码,是否需要验证ip地址
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/nopassword/checkPasswordHasSetted")
	public Map<String, Object> checkPasswordHasSetted(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	
        	Boolean needFinancePwd = (Boolean) request.getSession().getAttribute(Constants.NEED_FINANCE_PWD);
        	//session中是否需要验证IP地址
        	Boolean needValidUserIp = (Boolean) request.getSession().getAttribute(Constants.NEED_VALID_USERIP);
        	
        	boolean needPwd = false;
        	boolean needUserIp = false;
        	
        	//客服不需要验证IP地址
        	int loginUserType = this.getSessionUserType(request);
        	if (loginUserType != 2 && loginUserType != 4) {
        		
            	//财务设置中设置的是否需要验证IP地址
            	FinanceSettingModel financeSetting = this.financeSettingService.queryByCrewId(crewId);
            	if (financeSetting == null) {
            		financeSetting = this.financeSettingService.initFinanceSetting(crewId);
            	}
            	if (financeSetting != null && financeSetting.getPwdStatus()) {
            		needPwd = true;
            	}
            	if (financeSetting != null && financeSetting.getIpStatus()) {
            		
                	//用户IP地址是否发生变化
                	UserInfoModel userInfo = this.getSessionUserInfo(request);
        			UserInfoModel myUserInfo = this.userService.queryById(userInfo.getUserId());
        			String localIp = IpUtil.getUserIp(request);//当前IP地址
        			if(StringUtil.isBlank(myUserInfo.getIp())) {//为空，将当前IP地址添加到用户IP地址
        				this.userService.addUserIp(userInfo.getUserId(), localIp);
        			} else {//对比用户IP与当前IP，如不同，则需验证IP
        				String[] userIps = myUserInfo.getIp().split(",");
        				boolean flag = false;
        				for(String userIp : userIps) {
        					if(userIp.equals(localIp)) {
        						flag = true;
        						break;
        					}
        				}
        				if(!flag) {
        					needUserIp = true;
        				}
        			}
            	}
        	}   
        	
        	if (needFinancePwd == null && needPwd) {
        		request.getSession().setAttribute(Constants.NEED_FINANCE_PWD, true);
        	}
        	if (needFinancePwd != null && !needFinancePwd) {
        		needPwd = false;
        	}
        	
        	resultMap.put("needPwd", needPwd);	
        	
        	if (needValidUserIp == null && needUserIp) {
        		request.getSession().setAttribute(Constants.NEED_VALID_USERIP, true);
        	}
        	if (needValidUserIp != null && !needValidUserIp) {
        		needUserIp = false;
        	}
        	
        	resultMap.put("needUserIp", needUserIp);
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
	 * 校验财务密码是否正确,验证码是否正确
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/nopassword/checkPasswordCorrect")
	public Map<String, Object> checkPasswordCorrect(HttpServletRequest request,
			String validType, String password, String phone, String verifyCode) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	if(StringUtils.isBlank(validType) || validType.equals("-1")) {
        		throw new Exception("未知验证类型");
        	}

        	String crewId = this.getCrewId(request);
        	FinanceSettingModel financeSetting = this.financeSettingService.queryByCrewId(crewId);
        	if (financeSetting == null) {
        		financeSetting = this.financeSettingService.initFinanceSetting(crewId);
        	}
        	if(validType.equals("0") || validType.equals("1")) {//验证财务密码
        		if (StringUtils.isBlank(password)) {
            		throw new IllegalArgumentException("请提供财务密码");
            	}
        		if (financeSetting == null) {
            		throw new IllegalArgumentException("尚未进行安全设置");
            	}
        		if (!financeSetting.getFinancePassword().equals(MD5Util.MD5(password))) {
            		throw new IllegalArgumentException("密码不正确");
            	}
        	}
        	if(validType.equals("0") || validType.equals("2")) {//验证验证码
        		if (StringUtils.isBlank(verifyCode)) {
            		throw new IllegalArgumentException("请输入验证码");
            	}
        		if (financeSetting == null) {
            		throw new IllegalArgumentException("尚未进行安全设置");
            	}
				VerifyCodeInfoModel validInfoModel = this.verifyCodeInfoService
						.queryByPhoneAndCode(phone, verifyCode,
								VerifyCodeType.ValidUserPhone.getValue());
        		if (validInfoModel == null) {
        			throw new IllegalArgumentException("验证码错误");
        		} else {
        			//验证通过，将当前ip加入到用户ip中
        			UserInfoModel userInfo = this.getSessionUserInfo(request);
        			String localIp = IpUtil.getUserIp(request);//当前IP地址
        			this.userService.updateUserIp(userInfo.getUserId(), localIp);
        			//将验证码置为无效
        			this.verifyCodeInfoService.inValidPhoneCode(phone, VerifyCodeType.ValidUserPhone.getValue());
        		}
        	}
        	if(validType.equals("0") || validType.equals("1")) {//验证财务密码
            	request.getSession().setAttribute(Constants.NEED_FINANCE_PWD, false);
        	}
        	if(validType.equals("0") || validType.equals("2")) {//验证ip地址
            	request.getSession().setAttribute(Constants.NEED_VALID_USERIP, false);
        	}
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
	 * 校验是否设置了单据信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/checkTaxHasSetted")
	public Map<String, Object> checkTaxHasSetted(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	
        	
        	
        	FinanceSettingModel financeSetting = this.financeSettingService.queryByCrewId(crewId);
        	if (financeSetting == null) {
        		financeSetting = this.financeSettingService.initFinanceSetting(crewId);
        	}
        	
        	String taxFinanSubjName = "";
        	if (!StringUtils.isBlank(financeSetting.getTaxFinanSubjId())) {
        		taxFinanSubjName = this.financeSubjectService.getFinanceSubjName(financeSetting.getTaxFinanSubjId());
        	}
        	
        	boolean hasSetted = false;
        	if (financeSetting.getTaxRate() != null && !StringUtils.isBlank(taxFinanSubjName) && !StringUtils.isBlank(financeSetting.getTaxFinanSubjId())) {
        		hasSetted = true;
        	}
        	
        	resultMap.put("taxRate", financeSetting.getTaxRate());
        	resultMap.put("taxFinanSubjId", financeSetting.getTaxFinanSubjId());
        	resultMap.put("taxFinanSubjName", taxFinanSubjName);
        	resultMap.put("hasSetted", hasSetted);
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
	 * 查询剧组财务设置信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/querySettingInfo")
	public Map<String, Object> querySettingInfo(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	
        	FinanceSettingModel financeSetting = this.financeSettingService.queryByCrewId(crewId);
        	if (financeSetting == null) {
        		financeSetting = this.financeSettingService.initFinanceSetting(crewId);
        	}
        	
        	this.financeSubjectService.refreshCachedSubjectList(crewId);
        	
        	Map<String, Object> financeSettingMap = new HashMap<String, Object>();
        	financeSettingMap.put("payStatus", financeSetting.getPayStatus());
        	financeSettingMap.put("hasReceiptStatus", financeSetting.getHasReceiptStatus());
        	financeSettingMap.put("pwdStatus", financeSetting.getPwdStatus());
        	financeSettingMap.put("financePassword", financeSetting.getFinancePassword());
        	financeSettingMap.put("ipStatus", financeSetting.getIpStatus());
        	financeSettingMap.put("monthDayType", financeSetting.getMonthDayType());
        	financeSettingMap.put("contractAdvanceRemindDays", financeSetting.getContractAdvanceRemindDays());
        	financeSettingMap.put("taxFinanSubjId", financeSetting.getTaxFinanSubjId());
        	financeSettingMap.put("taxFinanSubjName", this.financeSubjectService.getFinanceSubjName(financeSetting.getTaxFinanSubjId()));
        	financeSettingMap.put("taxRate", financeSetting.getTaxRate());
        	
        	resultMap.put("financeSetting", financeSettingMap);
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
	 * 保存财务设置中单据设置信息
	 * @param request
	 * @param hasReceiptStatus	付款单编号是否分有票无票
	 * @param payStatus	付款单编号是否按月重新开始
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveBillSettingInfo")
	public Map<String, Object> saveBillSettingInfo(HttpServletRequest request, Boolean hasReceiptStatus, Boolean payStatus) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	if (hasReceiptStatus == null || payStatus == null ) {
        		throw new IllegalArgumentException("请完善单据设置");
        	}
        	
        	String crewId = this.getCrewId(request);
        	
        	FinanceSettingModel financeSetting = this.financeSettingService.queryByCrewId(crewId);
        	if (financeSetting == null) {
        		financeSetting = this.financeSettingService.initFinanceSetting(crewId);
        	}
    		financeSetting.setHasReceiptStatus(hasReceiptStatus);
    		financeSetting.setPayStatus(payStatus);
    		
    		this.financeSettingService.updateOne(financeSetting);
        	
        	this.sysLogService.saveSysLog(request, "保存单据设置信息", Constants.TERMINAL_PC, FinanceSettingModel.TABLE_NAME, financeSetting.getSetId(), 2);
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
        } catch(Exception e) {
            success = false;
            message = "未知异常";

            logger.error("未知异常", e);
        	this.sysLogService.saveSysLog(request, "保存单据设置信息失败：" + e.getMessage(), Constants.TERMINAL_PC, FinanceSettingModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
	
	/**
	 * 保存财务密码信息
	 * @param request
	 * @param operateFlag	操作类型1-新增  2-修改
	 * @param pwdStatus	是否启用密码
	 * @param oldPassword	旧密码
	 * @param newPassword	新密码
	 * @param repeatPassword	确认密码
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/savePasswordInfo")
	public Map<String, Object> savePasswordInfo(HttpServletRequest request, Integer operateFlag, Boolean pwdStatus, String oldPassword, String newPassword, String repeatPassword) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);

    		FinanceSettingModel financeSetting = this.financeSettingService.queryByCrewId(crewId);
        	if (financeSetting == null) {
        		financeSetting = this.financeSettingService.initFinanceSetting(crewId);
        	}
        	if (pwdStatus == null) {
        		pwdStatus = false;
        	}
        	String logDesc = "";
        	Integer operType = null;
        	//新增财务密码
        	if (operateFlag == 1) {
        		if (pwdStatus && StringUtils.isBlank(newPassword)) {
            		throw new IllegalArgumentException("请填写密码");
            	}
        		if (pwdStatus && StringUtils.isBlank(repeatPassword)) {
        			throw new IllegalArgumentException("请填写确认密码");
        		}
            	if (!StringUtils.isBlank(newPassword) && !newPassword.equals(repeatPassword)) {
            		throw new IllegalArgumentException("密码和确认密码不一致");
            	}
        		
        		financeSetting.setPwdStatus(pwdStatus);
        		if (!StringUtils.isBlank(newPassword)) {
        			newPassword = MD5Util.MD5(newPassword);
        		}
        		financeSetting.setFinancePassword(newPassword);
        		
        		this.financeSettingService.updateOne(financeSetting);
        	}
        	logDesc = "新增财务密码";
        	operType = 1;
        	
        	//修改财务密码
        	if (operateFlag == 2) {
        		if (StringUtils.isBlank(oldPassword)) {
        			throw new IllegalArgumentException("请输入旧密码");
        		}
        		if (pwdStatus && StringUtils.isBlank(newPassword)) {
        			throw new IllegalArgumentException("请输入新密码");
        		}
        		if (pwdStatus && StringUtils.isBlank(repeatPassword)) {
        			throw new IllegalArgumentException("请输入确认密码");
        		}
        		if (pwdStatus && !newPassword.equals(repeatPassword)) {
        			throw new IllegalArgumentException("新密码和确认密码不一致");
        		}
        		
        		if (!financeSetting.getFinancePassword().equals(MD5Util.MD5(oldPassword))) {
        			throw new IllegalArgumentException("旧密码输入错误");
        		}
        		
        		financeSetting.setPwdStatus(pwdStatus);
        		if (!StringUtils.isBlank(newPassword)) {
        			newPassword = MD5Util.MD5(newPassword);
        		}
        		financeSetting.setFinancePassword(newPassword);
        		
        		this.financeSettingService.updateOne(financeSetting);
        		logDesc = "修改财务密码";
        		operType = 2;
        	}
        	
        	request.getSession().removeAttribute(Constants.NEED_FINANCE_PWD);
        	
        	this.sysLogService.saveSysLog(request, logDesc, Constants.TERMINAL_PC, FinanceSettingModel.TABLE_NAME, financeSetting.getSetId(), operType);
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
        } catch(Exception e) {
            success = false;
            message = "未知异常";

            logger.error("未知异常", e);
        	this.sysLogService.saveSysLog(request, "保存财务密码信息失败：" + e.getMessage(), Constants.TERMINAL_PC, FinanceSettingModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
	
	/**
	 * 设置是否根据用户IP地址变化验证用户手机号
	 * @param request
	 * @param ipStatus 0:否, 1:是
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/setIpStatus")
	public Map<String, Object> setIpStatus(HttpServletRequest request, Boolean ipStatus) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);

    		FinanceSettingModel financeSetting = this.financeSettingService.queryByCrewId(crewId);
        	if (financeSetting == null) {
        		financeSetting = this.financeSettingService.initFinanceSetting(crewId);
        	}
    		financeSetting.setIpStatus(ipStatus);
    		this.financeSettingService.updateOne(financeSetting);
        	
        	request.getSession().removeAttribute(Constants.NEED_VALID_USERIP);
        	
        	this.sysLogService.saveSysLog(request, "设置是否根据用户IP地址变化验证用户手机号", Constants.TERMINAL_PC, FinanceSettingModel.TABLE_NAME, financeSetting.getSetId(), 2);
        } catch(Exception e) {
            success = false;
            message = "未知异常";

            logger.error("未知异常", e);
            this.sysLogService.saveSysLog(request, "设置是否根据用户IP地址变化验证用户手机号失败：" + e.getMessage(), Constants.TERMINAL_PC, FinanceSettingModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
	
	/**
	 * 保存缴税信息
	 * @param request
	 * @param taxFinanSubjId 税对应的财务科目ID
	 * @param taxRate 税率
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveTaxInfo")
	public Map<String, Object> saveTaxInfo(HttpServletRequest request, String taxFinanSubjId, Double taxRate) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	if (StringUtils.isBlank(taxFinanSubjId)) {
        		throw new IllegalArgumentException("请选择财务科目");
        	}
        	if (taxRate == null) {
        		throw new IllegalArgumentException("请填写税率");
        	}
        	if (StringUtils.isBlank(this.financeSubjectService.getFinanceSubjName(taxFinanSubjId))) {
        		throw new IllegalArgumentException("请选择财务科目");
        	}
        	
        	String crewId = this.getCrewId(request);

    		FinanceSettingModel financeSetting = this.financeSettingService.queryByCrewId(crewId);
        	if (financeSetting == null) {
        		financeSetting = this.financeSettingService.initFinanceSetting(crewId);
        	}
    		financeSetting.setTaxFinanSubjId(taxFinanSubjId);
    		financeSetting.setTaxRate(taxRate);
    		this.financeSettingService.updateOne(financeSetting);
        	
    		resultMap.put("taxFinanSubjName", this.financeSubjectService.getFinanceSubjName(taxFinanSubjId));
        	this.sysLogService.saveSysLog(request, "保存缴税信息", Constants.TERMINAL_PC, FinanceSettingModel.TABLE_NAME, financeSetting.getSetId(), 2);
        } catch (IllegalArgumentException ie) {
        	success = false;
            message = ie.getMessage();
            
        } catch(Exception e) {
            success = false;
            message = "未知异常";

            logger.error("未知异常", e);
        	this.sysLogService.saveSysLog(request, "保存财务设置信息失败：" + e.getMessage(), Constants.TERMINAL_PC, FinanceSettingModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
	
	/**
	 * 保存其他设置信息
	 * @param request
	 * @param monthDayType	每月天数类型，见MonthDayType枚举类
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveOtherSetting")
	public Map<String, Object> saveOtherSetting(HttpServletRequest request, Integer monthDayType, Integer contractAdvanceRemindDays) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	if (contractAdvanceRemindDays == null) {
        		throw new IllegalArgumentException("请填写合同支付提前提醒天数");
        	}

    		FinanceSettingModel financeSetting = this.financeSettingService.queryByCrewId(crewId);
        	if (financeSetting == null) {
        		financeSetting = this.financeSettingService.initFinanceSetting(crewId);
        	}
    		financeSetting.setMonthDayType(monthDayType);
    		financeSetting.setContractAdvanceRemindDays(contractAdvanceRemindDays);
    		this.financeSettingService.updateOne(financeSetting);
        	
        	this.sysLogService.saveSysLog(request, "保存财务设置其他设置信息", Constants.TERMINAL_PC, FinanceSettingModel.TABLE_NAME, financeSetting.getSetId(), 2);
        } catch (IllegalArgumentException ie) {
        	success = false;
            message = ie.getMessage();
            
        } catch(Exception e) {
            success = false;
            message = "未知异常";

            logger.error("未知异常", e);
        	this.sysLogService.saveSysLog(request, "保存财务设置信息失败：" + e.getMessage(), Constants.TERMINAL_PC, FinanceSettingModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
}
