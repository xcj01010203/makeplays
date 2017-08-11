package com.xiaotu.makeplays.finance.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.finance.model.FinancePaymentWayModel;
import com.xiaotu.makeplays.finance.service.FinancePaymentWayService;
import com.xiaotu.makeplays.utils.BaseController;

/**
 * 财务付款方式
 * @author xuchangjian 2016-8-19下午7:47:38
 */
@Controller
@RequestMapping("/financePaymentWay")
public class FinancePaymentWayController extends BaseController {
	
	Logger logger = LoggerFactory.getLogger(FinancePaymentWayController.class);

	@Autowired
	private FinancePaymentWayService financePaymentWayService;
	
	/**
	 * 查询财务支付方式名称列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryPaywayNameList")
	public Map<String, Object> queryPaywayNameList(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		boolean success = true;
		String message = "";
		try {
			String crewId = getCrewId(request);
			
			List<FinancePaymentWayModel> payWayList = this.financePaymentWayService.queryByCrewId(crewId);
			
			List<String> wayNameList = new ArrayList<String>();
			for (FinancePaymentWayModel paymentWay : payWayList) {
				if (!wayNameList.contains(paymentWay.getWayName())) {
					wayNameList.add(paymentWay.getWayName());
				}
			}

			resultMap.put("wayNameList", wayNameList);
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
