package com.xiaotu.makeplays.goods.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.goods.model.GoodsInfoModel;
import com.xiaotu.makeplays.goods.service.GoodsInfoService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;

/**
 * 物品管理的controller
 * @author wanrenyi 2017年4月24日上午11:32:03
 */
@Controller
@RequestMapping("/goodsInfoManager")
public class GoodsInfoController extends BaseController{
	
	Logger logger = LoggerFactory.getLogger(GoodsInfoModel.class);
	private static int terminal = Constants.TERMINAL_PC;

	@Autowired
	private GoodsInfoService goodsInfoService;
	
	/**
	 * 同步服化道旧表数据到新的物品表中
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ansycData")
	public Map<String, Object> ansycData(HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			//调用同步方法
			goodsInfoService.ansycData();
			message = "同步成功";
		} catch (Exception e) {
			message="未知错误，同步失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
}
