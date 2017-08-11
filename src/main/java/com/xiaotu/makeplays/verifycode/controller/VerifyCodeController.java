package com.xiaotu.makeplays.verifycode.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.mobile.server.sys.VerifyCodeFacade;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.RegexUtils;
import com.xiaotu.makeplays.verifycode.model.VerifyCodeInfoModel;
import com.xiaotu.makeplays.verifycode.service.VerifyCodeInfoService;

@Controller
@RequestMapping("/interface/verifyCodeManager")
public class VerifyCodeController {
	
	Logger logger = LoggerFactory.getLogger(VerifyCodeFacade.class);
	
	@Autowired
	private VerifyCodeInfoService verifyCodeInfoService;

	/**
	 * 发送验证码
	 * @param phone
	 * @param type  1-找回密码  2-注册  3-修改手机号 4-财务-验证手机号
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sendVerifyCode")
	public Object sendVerifyCode(String phone, Integer type) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			if (StringUtils.isBlank(phone)) {
				throw new IllegalArgumentException("手机号不能为空");
			}
			if (!RegexUtils.regexFind(Constants.REGEX_PHONE_NUMBER, phone)) {
				throw new IllegalArgumentException("手机号不合法");
			}
			if (type == null) {
				throw new IllegalArgumentException("验证码类型不能为空");
			}
			
			String code = this.verifyCodeInfoService.sendVerifyCode(phone, type);
			
			resultMap.put("verifyCode", code);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			logger.error("未知异常，发送验证码失败", e);
			
			success = false;
			message = "未知异常，发送验证码失败";
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 校验验证码是否可用
	 * @param phone	手机号
	 * @param verifyCode	验证码
	 * @param type	验证码类型,1-找回密码  2-注册  3-修改手机号
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/checkVerifyCode")
	public Object checkVerifyCode(String phone, String verifyCode, Integer type){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			//校验手机号是否合法
			if (StringUtils.isBlank(phone)) {
				throw new IllegalArgumentException("请填写手机号");
			}
			if (!RegexUtils.regexFind(Constants.REGEX_PHONE_NUMBER, phone)) {
				throw new IllegalArgumentException("手机号不合法");
			}
			
			//校验手机号验证码是否匹配
			if (StringUtils.isBlank(verifyCode)) {
				throw new IllegalArgumentException("请填写短信验证码");
			}
			VerifyCodeInfoModel validInfoModel = this.verifyCodeInfoService.queryByPhoneAndCode(phone, verifyCode, type);
			if (validInfoModel == null) {
				throw new IllegalArgumentException("验证码错误");
			}
			
			
		} catch(IllegalArgumentException ie){
			logger.error(ie.getMessage(), ie);
			
			success = false;
			message = ie.getMessage();
		}catch (Exception e) {
			logger.error("未知异常，校验验证码失败", e);
			
			success = false;
			message = "未知异常，校验验证码失败";
		}
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
}
