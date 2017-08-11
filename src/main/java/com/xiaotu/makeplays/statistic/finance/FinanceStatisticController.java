package com.xiaotu.makeplays.statistic.finance;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.finance.model.FinanSubjCurrencyMapModel;
import com.xiaotu.makeplays.finance.model.PaymentFinanSubjMapModel;
import com.xiaotu.makeplays.finance.model.PaymentInfoModel;
import com.xiaotu.makeplays.finance.service.FinanceAccountGroupService;
import com.xiaotu.makeplays.finance.service.FinanceSubjectService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.view.model.ViewLocationModel;

/**
 * @类名：FinanceStatisticController.java
 * @作者：李晓平
 * @时间：2016年11月2日 下午2:22:14 
 * @描述：费用进度
 */
@Controller
@RequestMapping("/financeStatisticManager")
public class FinanceStatisticController extends BaseController{
	Logger logger = LoggerFactory.getLogger(FinanceStatisticController.class);
	
	@Autowired
	private FinanceSubjectService financeSubjectService;
	
	@Autowired
	private FinanceAccountGroupService financeAccountGroupService;
	
	private DecimalFormat df2 = new DecimalFormat("0.00");
	
	/**
	 * 跳转到费用进度统计页面
	 * @param crewId
	 * @return
	 */
	@RequestMapping("/toFinanceStatisticPage")
	public ModelAndView toDayShootStatisticPage() {
		ModelAndView mv = new ModelAndView("/statistic/financeStatistic");
		return mv;
	}

	/**
	 * 总费用进度
	 * @return
	 */
	@RequestMapping("/queryTotalFinance")
	@ResponseBody
	public Map<String, Object> queryTotalFinance(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			resultMap.put("totalFinance", financeSubjectService.queryTotalFinance(crewId));
			
			this.sysLogService.saveSysLog(request, "查询总费用进度", Constants.TERMINAL_PC, 
					FinanSubjCurrencyMapModel.TABLE_NAME + "," + PaymentInfoModel.TABLE_NAME, null, 0);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询总费用进度失败！";
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "查询总费用进度失败：" + e.getMessage(), Constants.TERMINAL_PC, 
					FinanSubjCurrencyMapModel.TABLE_NAME + "," + PaymentInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 预算支出概况
	 * @return
	 */
	@RequestMapping("/queryBudgetPayedInfo")
	@ResponseBody
	public Map<String, Object> queryBudgetPayedInfo(HttpServletRequest request,
			String statType, String parentId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			resultMap.put("budgetPayedInfo", financeSubjectService
					.queryBudgetPayedInfo(crewId, statType, parentId));
			
			this.sysLogService.saveSysLog(request, "查询预算支出概况", Constants.TERMINAL_PC, 
					FinanSubjCurrencyMapModel.TABLE_NAME + "," + PaymentFinanSubjMapModel.TABLE_NAME, null, 0);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询预算支出概况失败！";
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "查询预算支出概况失败：" + e.getMessage(), Constants.TERMINAL_PC, 
					FinanSubjCurrencyMapModel.TABLE_NAME + "," + PaymentFinanSubjMapModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 日支出、累计日支出
	 * @return
	 */
	@RequestMapping("/queryDayPayedInfo")
	@ResponseBody
	public Map<String, Object> queryDayPayedInfo(HttpServletRequest request, String parentId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			resultMap.put("dayPayedInfo", financeSubjectService.queryDayPayedInfo(crewId, parentId));
			
			this.sysLogService.saveSysLog(request, "查询日支出、累计日支出", Constants.TERMINAL_PC, PaymentFinanSubjMapModel.TABLE_NAME, parentId, 0);
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询日支出、累计日支出失败！";
			
			logger.error(message, e);
			
			this.sysLogService.saveSysLog(request, "查询日支出、累计日支出失败：" + e.getMessage(), Constants.TERMINAL_PC, PaymentFinanSubjMapModel.TABLE_NAME, parentId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 添加、修改财务科目预算分组信息
	 * @param request
	 * @param groupId 分组ID，用于修改
	 * @param groupName 分组名称
	 * @param subjectId 科目ID，用“,”分隔
	 * @return
	 */
	@RequestMapping("/saveFinanceAccountGroup")
	@ResponseBody
	public Map<String, Object> saveFinanceAccountGroup(
			HttpServletRequest request, String groupId, String groupName,
			String subjectId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			//判断名称是否已存在
			if(this.financeAccountGroupService.isExistGroupName(groupId, groupName)) {
				throw new IllegalArgumentException("自定义科目组名称已存在");
			}
			//保存信息
			this.financeAccountGroupService.saveFinanceAccountGroup(crewId, groupId, groupName, subjectId);
			
			String logDesc = "";
			Integer operType = null;
			String id = crewId;
			if(StringUtil.isBlank(groupId)) {
				logDesc = "添加自定义科目组";
				operType = 1;
			} else {
				logDesc = "修改自定义科目组";
				operType = 2;
				id = groupId;
			}
			this.sysLogService.saveSysLog(request, logDesc, Constants.TERMINAL_PC, ViewLocationModel.TABLE_NAME, id, operType);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常，保存财务科目预算分组信息失败！";
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "保存自定义科目组失败：" + e.getMessage(), Constants.TERMINAL_PC, ViewLocationModel.TABLE_NAME, groupId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 删除自定义分组
	 * @param groupId
	 * @return
	 */
	@RequestMapping("/deleteOneFinanceAccountGroup")
	@ResponseBody
	public Map<String, Object> deleteOneFinanceAccountGroup(HttpServletRequest request, String groupId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			this.financeAccountGroupService.deleteOneFinanceAccountGroup(groupId);
			
			this.sysLogService.saveSysLog(request, "删除自定义科目组", Constants.TERMINAL_PC, ViewLocationModel.TABLE_NAME, groupId, 3);
		} catch (Exception e) {
			success = false;
			message = "未知异常，删除财务科目预算分组信息失败！";
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "删除自定义科目组失败：" + e.getMessage(), Constants.TERMINAL_PC, ViewLocationModel.TABLE_NAME, groupId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 查询单个财务科目预算分组信息
	 * @param groupId
	 * @return
	 */
	@RequestMapping("/queryOneFinanceAccountGroup")
	@ResponseBody
	public Map<String, Object> queryOneFinanceAccountGroup(String groupId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			resultMap.put("financeAccountGroup", 
					this.financeAccountGroupService.queryOneFinanceAccountGroup(groupId));
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询财务科目预算分组信息失败！";
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 查询所有的自定义分组
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryAllFinanceAccountGroup")
	@ResponseBody
	public Map<String, Object> queryAllFinanceAccountGroup(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			resultMap.put("financeAccountGroupList", 
					this.financeAccountGroupService.queryAllFinanceAccountGroup(crewId));
		} catch (Exception e) {
			success = false;
			message = "未知异常，查询财务科目预算分组信息失败！";
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
}
