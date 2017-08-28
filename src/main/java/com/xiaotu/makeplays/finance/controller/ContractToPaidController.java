package com.xiaotu.makeplays.finance.controller;

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

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.finance.model.ContractToPaidModel;
import com.xiaotu.makeplays.finance.model.CurrencyInfoModel;
import com.xiaotu.makeplays.finance.model.FinanceSubjectModel;
import com.xiaotu.makeplays.finance.service.ContractToPaidService;
import com.xiaotu.makeplays.finance.service.CurrencyInfoService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;

/*
 * *
 * 合同待付清单
 * @author Administrator
 *
 */
@Controller
@RequestMapping("contractToPaidController")
public class ContractToPaidController extends BaseController{
	Logger logger = LoggerFactory.getLogger(ContractToPaidController.class);
	@Autowired
	private ContractToPaidService contractToPaidService;
	
	@Autowired
	private CurrencyInfoService currencyInfoService;
	
	/**
	 * 查询待付清单数据
	 * @param request
	 * @param starDate 开始时间
	 * @param endDate  结束时间
	 * @param contractType  合同类型
	 * @param contractName  合同方
	 * @param financeSubjectId  财务科目id
	 * @param status    状态
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryContractToPaidList")
	public Map<String, Object> queryContractToPaidList(HttpServletRequest request,String startDate,String endDate,String contractType,String contractName,String financeSubjectId,String status){
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	CrewInfoModel crewInfoModel = this.getSessionCrewInfo(request);
    		List<Map<String, Object>> list = contractToPaidService.queryContractToPaidList(crewInfoModel.getCrewId(),startDate,endDate,contractType,contractName,financeSubjectId,status);
        	resultMap.put("contractToPaidList", list);
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
	 * 根据id查询合同待付信息
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryContractToPaidListById")
	public Map<String, Object> queryContractToPaidListById(HttpServletRequest request,String id){
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	if(StringUtils.isBlank(id)){
        		throw new IllegalArgumentException("参数异常");
        	}
        	String crewId = this.getCrewId(request);
    		List<Map<String, Object>> list = contractToPaidService.queryContractToPaidListById(id,crewId);
        	resultMap.put("contractToPaidMap", list);
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
	 * 查询剧组所有的可用货币信息
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryManyByMutiCondition")
	public Map<String, Object> queryManyByMutiCondition(HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewid = this.getCrewId(request);
        	//货币列表
        	Map<String, Object> conditionMap = new HashMap<String, Object>();
        	conditionMap.put("crewId", crewid);
        	conditionMap.put("ifEnable", true);
        	List<CurrencyInfoModel> currencyInfoList = this.currencyInfoService.queryManyByMutiCondition(conditionMap, null);
        	resultMap.put("currencyInfoList", currencyInfoList);
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
	 * 跳转到合同待付清单
	 * @param request
	 * @return
	 */
	@RequestMapping("toContractToPaidPrintPage")
	public Object toContractToPaidPrintPage(HttpServletRequest request,String paidId){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/finance/contract/printPaidInfo");
		mv.addObject("id", paidId);
		return mv;
	}
	/**
	 * 修改合同待付信息
	 * @param id  合同待付信息id
	 * @param summary  摘要
	 * @param money    金额
	 * @param status   状态   0：未付 1:待付    2：已付
	 */
	@ResponseBody
	@RequestMapping("updateContractToPaidInfo")
	public Map<String, Object> updateContractToPaidInfo(HttpServletRequest request, String id,String summary,Double money,Integer status){
		Map<String, Object> resultMap = new HashMap<String, Object>();
        boolean success = true;
        String message = "";
        try {
        	if(StringUtils.isBlank(id)){
        		throw new IllegalArgumentException("参数异常");
        	}
        	contractToPaidService.updateContractToPaidInfo(id,summary,money,status,"");
        	
        	this.sysLogService.saveSysLog(request, "修改合同待付信息(" + id.split(",").length + ")", Constants.TERMINAL_PC, ContractToPaidModel.TABLE_NAME, id, 2);
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
        } catch(Exception e) {
            success = false;
            message = "未知异常";

            logger.error("未知异常", e);
        	this.sysLogService.saveSysLog(request, "修改合同待付信息(" + id.split(",").length + ")失败：" + e.getMessage(), Constants.TERMINAL_PC, ContractToPaidModel.TABLE_NAME, id, SysLogOperType.ERROR.getValue());
        }
        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
		
	}
	/**
	 * 获取下拉框数据   合同用户名
	 * 			   财务科目名称
	 * 只查询合同批量支付中有的财务科目
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryDropList")
	public Map<String, Object> queryDropList(HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	//获取合同方
        	List<String> contractNameList = contractToPaidService.queryDropListForContractName(crewId);
        	//获取财务科目
        	List<FinanceSubjectModel> subjectNameList = contractToPaidService.queryDropListForContractSubjectName(crewId);
        	
        	resultMap.put("contractNameList", contractNameList);
        	resultMap.put("subjectNameList", subjectNameList);
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
	
}
