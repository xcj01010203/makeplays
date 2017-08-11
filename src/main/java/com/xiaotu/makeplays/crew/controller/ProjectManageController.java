package com.xiaotu.makeplays.crew.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.crew.service.ProjectManageService;
import com.xiaotu.makeplays.finance.controller.dto.BudgetCurrencyDto;
import com.xiaotu.makeplays.finance.controller.dto.BudgetInfoDto;
import com.xiaotu.makeplays.finance.model.CurrencyInfoModel;
import com.xiaotu.makeplays.finance.model.FinanceSubjectModel;
import com.xiaotu.makeplays.finance.model.PaymentFinanSubjMapModel;
import com.xiaotu.makeplays.finance.model.PaymentInfoModel;
import com.xiaotu.makeplays.finance.service.CollectionInfoService;
import com.xiaotu.makeplays.finance.service.CurrencyInfoService;
import com.xiaotu.makeplays.finance.service.FinanceSubjectService;
import com.xiaotu.makeplays.notice.service.NoticeService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.DateUtils;
import com.xiaotu.makeplays.utils.ExcelUtils;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.utils.StringUtils;
import com.xiaotu.makeplays.view.service.ViewInfoService;

/**
 * @类名：ProjectManageController.java
 * @作者：李晓平
 * @时间：2017年2月13日 下午2:55:24
 * @描述：项目管理Controller，项目总监使用
 */
@Controller
@RequestMapping("/projectManager")
public class ProjectManageController extends BaseController{
	
	Logger logger = LoggerFactory.getLogger(ProjectManageController.class);
	
	private DecimalFormat df1 = new DecimalFormat("#,##0.00");
	
	private DecimalFormat df2 = new DecimalFormat("0.00");

	@Autowired
	private ProjectManageService projectManageService;
	
	@Autowired
	private CurrencyInfoService currencyInfoService;
	
	@Autowired
	private FinanceSubjectService financeSubjectService;
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	@Autowired
	private CrewInfoService crewInfoService;
	
	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private CollectionInfoService collectionInfoService;
	
	/**
	 * 跳转到项目管理页面
	 * @return
	 */
	@RequestMapping("/toProjectListPage")
	public ModelAndView toProjectListPage() {
		ModelAndView view = new ModelAndView("project/projectList");
		return view;
	}
	
	/**
	 * 查询项目信息
	 * @param request
	 * @param crewInfoFilter
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryProjectList")
	public Map<String, Object> queryProjectList(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			String userId = this.getLoginUserId(request);
			resultMap.put("result", this.projectManageService.queryAllProjects(userId));
			this.sysLogService.saveSysLog(request, "查询项目对比信息", Constants.TERMINAL_PC, CrewInfoModel.TABLE_NAME, null, 0);
		} catch (Exception e) {
			success = false;
	        message = "未知异常，获取项目信息失败";
	        logger.error(message, e);
		}
        resultMap.put("success", success);
        resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询财务预算支出
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryFinanceBudgetPayed")
	public Map<String, Object> queryFinanceBudgetPayed(HttpServletRequest request, String crewId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	if(StringUtil.isBlank(crewId)) {
        		throw new IllegalArgumentException("请提供剧组信息");
        	}       	
        	
        	resultMap.put("settleInfoList", this.getSettlementInfoMapList(crewId));
        	
        	this.sysLogService.saveSysLog(request, "查询财务预算支出信息", Constants.TERMINAL_PC, 
        			FinanceSubjectModel.TABLE_NAME + "," + PaymentFinanSubjMapModel.TABLE_NAME + "," + PaymentInfoModel.TABLE_NAME, crewId, 0);
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
        } catch(Exception e) {
            success = false;
            message = "未知异常,查询财务预算支出信息失败";

            logger.error(message, e);
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
	
	/**
	 * 查询财务预算支出的统计信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryTotalBudgetPayed")
	public Map<String, Object> queryTotalBudgetPayed(HttpServletRequest request, String crewId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	       	
        	resultMap.putAll(this.getTotalInfo(crewId));
        	
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
        } catch(Exception e) {
            success = false;
            message = "未知异常,查询财务预算支出的统计信息失败";

            logger.error(message, e);
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
	
	/**
	 * 查询制作进度
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryProductionSchedule")
	public Map<String, Object> queryProductionSchedule(HttpServletRequest request, String crewId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	resultMap.putAll(this.getProductionSchedule(crewId));
			
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
        } catch(Exception e) {
            success = false;
            message = "未知异常,查询制作进度失败";

            logger.error(message, e);
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
	

	
	/**
	 * 导出项目对比情况
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exprotProjectList")
	public Map<String, Object> exprotProjectList(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";        

        //项目基本信息
        Map<String, String> projectInfoColumn = new LinkedHashMap<String, String>();
        projectInfoColumn.put("公司", "company");
        projectInfoColumn.put("项目名称", "crewName");
        projectInfoColumn.put("广电备案", "recordNumber");
        projectInfoColumn.put("立项集数", "seriesNo");
        projectInfoColumn.put("合拍协议", "coProduction");
        projectInfoColumn.put("合拍协议金额", "coProMoney");
        projectInfoColumn.put("剧组执行预算", "budget");
        projectInfoColumn.put("我方投资比例", "investmentRatio");
        projectInfoColumn.put("开拍时间", "shootStartDate");
        projectInfoColumn.put("预计杀青时间", "shootEndDate");
        projectInfoColumn.put("预计拍摄天数", "days");
        projectInfoColumn.put("实际已拍摄天数", "finishedDays");
        projectInfoColumn.put("剩余拍摄天数/(超期天数)", "remainingDays");
        projectInfoColumn.put("目前状态", "status");
        projectInfoColumn.put("更新重要事项说明及重要情况预警", "remark");
        projectInfoColumn.put("前次重要事项说明及重要情况预警", "lastRemark");
        
        //项目明细-预算支出
        Map<String, String> budgetPayedColumn = new LinkedHashMap<String, String>();
        budgetPayedColumn.put("财务科目", "financeSubjName");
        budgetPayedColumn.put("总预算", "totalBadgetMoney");
        budgetPayedColumn.put("总支出", "totalPayedMoney");
        budgetPayedColumn.put("总结余", "totalLeftMoney");
        budgetPayedColumn.put("支出比例", "totalPayedRate");
        
        //项目明细-制作进度
        String[] proScheTitle = new String[]{"项目", "预计总量", "实际完成量", "剩余量", "完成进度"};
		Map<String, String[]> proScheKeyMap = new LinkedHashMap<String, String[]>();
		proScheKeyMap.put("场次", new String[]{"totalViewCount", "finishedViewCount", "unfinishedViewCount", "viewFinishedRate"});
		proScheKeyMap.put("页数", new String[]{"totalPageCount", "finishedPageCount", "unfinishedPageCount", "pageFinishedRate"});
		proScheKeyMap.put("工作时间(天数)", new String[]{"totalShootDate", "shootDate", "unfinishedShootDate", "dateFinishedRate"});
		proScheKeyMap.put("资金投入", new String[]{"totalBudget", "totalInput", "remainInput", "inputRate"});
		proScheKeyMap.put("资金支出", new String[]{"totalBudget", "totalPayed", "remainPayed", "payedRate"});
        
        try {
        	//查询项目对比数据
        	String userId = this.getLoginUserId(request);
			List<Map<String, Object>> projectList = this.projectManageService.queryAllProjects(userId);
			//项目明细
			List<Map<String, Object>> projectDetailList = new ArrayList<Map<String,Object>>();
			if(projectList != null && projectList.size() > 0) {
				for(Map<String, Object> map : projectList) {
					String coProduction = map.get("coProduction") + "";
					if(StringUtil.isNotBlank(coProduction)) {
						if(coProduction.equals("0")) {
							map.put("coProduction", "无");
						} else if(coProduction.equals("1")) {
							map.put("coProduction", "已签订");
						}
					}
					if(StringUtil.isNotBlank(map.get("coProMoney") + "")) {
						map.put("coProMoney", this.df1.format(map.get("coProMoney")));
					} else if(StringUtil.isNotBlank(coProduction) && coProduction.equals("0")) {
						map.put("coProMoney", "-");
					}
					if(StringUtil.isNotBlank(map.get("budget") + "")) {
						map.put("budget", this.df1.format(map.get("budget")));
					}
					if(StringUtil.isNotBlank(map.get("investmentRatio") + "")) {
						map.put("investmentRatio", this.df2.format(map.get("investmentRatio")) + "%");
					}
					if(StringUtil.isNotBlank(map.get("status") + "")) {
						String str = "";
						switch((Integer) map.get("status")){
						  	case 1:
						  		str="筹备中";
						  		break;
							case 2:
								str="拍摄中";
								break;
							case 3:
								str="后期制作中";
								break;
							case 4:
								str="已完成";
								break;
							case 5:
								str="播出中";
								break;
							case 6:
								str="暂停";
						  		break;
						}
						map.put("status", str);
					}
					String crewId = map.get("crewId") + "";
					Map<String, Object> projectDetailInfo = new HashMap<String, Object>();
					List<Map<String, Object>> budgetPayedList = this.getSettlementInfoMapList(crewId);
					Map<String, Object> totalInfo = this.getTotalInfo(crewId);
					Map<String, Object> productionSchedule = this.getProductionSchedule(crewId);
					projectDetailInfo.put("crewName", map.get("crewName"));
					projectDetailInfo.put("budgetPayed", budgetPayedList);
					projectDetailInfo.put("totalInfo", totalInfo);
					projectDetailInfo.put("productionSchedule", productionSchedule);
					projectDetailList.add(projectDetailInfo);
				}
			}
			
        	//调用方法导出表格数据
    		ExcelUtils.exportProjectInfoForExcel(projectList, projectDetailList, response, projectInfoColumn, budgetPayedColumn, proScheTitle, proScheKeyMap);
    		this.sysLogService.saveSysLog(request, "导出项目对比情况", Constants.TERMINAL_PC, CrewInfoModel.TABLE_NAME, null, 5);
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
        } catch(Exception e) {
            success = false;
            message = "未知异常,导出项目对比情况失败";

            logger.error(message, e);
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
	
	/**
	 * 获取费用结算信息
	 * @param crewId
	 * @return
	 */
	private List<Map<String, Object>> getSettlementInfoMapList(String crewId) {
		//货币列表
    	Map<String, Object> conditionMap = new HashMap<String, Object>();
    	conditionMap.put("crewId", crewId);
    	conditionMap.put("ifEnable", true);
    	List<CurrencyInfoModel> currencyInfoList = this.currencyInfoService.queryManyByMutiCondition(conditionMap, null);
    	
    	//财务结算信息
    	List<Map<String, Object>> finanSubjWithSettleList = this.financeSubjectService.queryWithSettleInfo(crewId, null, null);
    	
    	//财务科目结算数据-----第一层map：key为财务科目ID，value为一个map；第二层map：key为币种ID，value为结算金额
    	Map<String, Map<String, Double>> settleMap = new HashMap<String, Map<String, Double>>();	
    	for (Map<String, Object> finanSubj : finanSubjWithSettleList) {
    		String id = (String) finanSubj.get("id");	//财务科目ID
    		String currencyId = (String) finanSubj.get("currencyId");	//币种ID
    		Double payedMoney = (Double) finanSubj.get("payedMoney");	//支出金额
    		
    		if (settleMap.containsKey(id)) {
    			Map<String, Double> currencyMap = settleMap.get(id);
    			if (currencyMap.containsKey(currencyId)) {
    				currencyMap.put(currencyId, BigDecimalUtil.add(currencyMap.get(currencyId), payedMoney));
    			} else {
    				currencyMap.put(currencyId, payedMoney);
    			}
    		} else {
    			Map<String, Double> currencyMap = new HashMap<String, Double>();
    			currencyMap.put(currencyId, payedMoney);
    			
    			settleMap.put(id, currencyMap);
    		}
    	}
    	
    	//获取财务科目预算信息列表
    	List<Map<String, Object>> finanSubjWithBudgetList = this.financeSubjectService.queryWithBudgetInfo(crewId);
    	
    	//把财务科目数据结合货币列表封装成BudgetInfoDto数据格式
    	List<BudgetInfoDto> budgetInfoList = new ArrayList<BudgetInfoDto>();
    	
    	for (Map<String, Object> finanSubj : finanSubjWithBudgetList) {
    		String id = (String) finanSubj.get("id");	//财务科目ID
    		String name = (String) finanSubj.get("name");	//财务科目名称
    		String parentId = (String) finanSubj.get("parentId");	//财务科目父科目ID
    		String remark = (String) finanSubj.get("remark");
    		Integer level = (Integer) finanSubj.get("level");
    		Integer sequence = (Integer) finanSubj.get("sequence");
    		
    		String currencyId = (String) finanSubj.get("currencyId");	//货币ID
    		
    		String mapId = (String) finanSubj.get("mapId");	//关联关系ID
    		Double amount = (Double) finanSubj.get("amount");	//数量
    		Double money = (Double) finanSubj.get("money");	//总金额
    		Double perPrice = (Double) finanSubj.get("perPrice");	//单价
    		String unitType = (String) finanSubj.get("unitType");	//单位
    		
    		//该财务科目的所有币种结算金额信息
    		Map<String, Double> currencySettleMap = settleMap.get(id);
    		
    		List<BudgetCurrencyDto> budgetCurrencyList = new ArrayList<BudgetCurrencyDto>();
    		
    		//把所有的货币信息封装到预算货币Dto中
    		for (CurrencyInfoModel currencyInfo : currencyInfoList) {
    			String myCurrencyId = currencyInfo.getId();
    			
    			BudgetCurrencyDto budgetCurrencyDto = new BudgetCurrencyDto();
    			budgetCurrencyDto.setCurrencyId(myCurrencyId);
    			budgetCurrencyDto.setCurrencyCode(currencyInfo.getCode());
    			budgetCurrencyDto.setCurrencyName(currencyInfo.getName());
    			budgetCurrencyDto.setExchangeRate(currencyInfo.getExchangeRate());
    			budgetCurrencyDto.setIfStandard(currencyInfo.getIfStandard());
    			
    			//如果当前科目有预算信息，则设置，否则预算为0
    			if (myCurrencyId.equals(currencyId)) {
    				budgetCurrencyDto.setMapId(mapId);
        			budgetCurrencyDto.setAmount(amount);
        			budgetCurrencyDto.setMoney(money);
        			budgetCurrencyDto.setPerPrice(perPrice);
        			budgetCurrencyDto.setUnitType(unitType);
    			} else {
    				budgetCurrencyDto.setMoney(0d);
    			}
    			
    			//该科目，该币种的结算金额信息
    			if (currencySettleMap == null || currencySettleMap.get(myCurrencyId) == null) {
    				budgetCurrencyDto.setSettleMoney(0.00);
    			} else {
    				budgetCurrencyDto.setSettleMoney(currencySettleMap.get(myCurrencyId));
    			}
    			
    			budgetCurrencyList.add(budgetCurrencyDto);
    		}
    		
    		BudgetInfoDto budgetInfoDto = new BudgetInfoDto();
    		budgetInfoDto.setFinanceSubjId(id);
    		budgetInfoDto.setFinanceSubjName(name);
    		budgetInfoDto.setFinanceSubjParentId(parentId);
    		budgetInfoDto.setRemark(remark);
    		budgetInfoDto.setLevel(level);
    		budgetInfoDto.setSequence(sequence);
    		budgetInfoDto.setBudgetCurrencyList(budgetCurrencyList);        		
    		
    		budgetInfoList.add(budgetInfoDto);
    	}        	
    	
    	//把子节点的预算值向父节点上合并，并把财务科目按照父子关系做成嵌套的形式
    	List<BudgetInfoDto> budgetDtoList = this.loopBudgetInfoDto(new ArrayList<BudgetInfoDto>(), budgetInfoList);
    	
    	//把嵌套形式的财务科目平铺开来，并且把其中的货币信息做成以货币ID为键，总金额为值的形式
    	List<Map<String, Object>> settlementInfoMapList = this.genBudgetInfo(new ArrayList<Map<String, Object>>(), budgetDtoList);
    	Collections.sort(settlementInfoMapList, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				int o1sequence = (Integer) o1.get("sequence");
				int o2sequence = (Integer) o2.get("sequence");
        		return o1sequence - o2sequence;
			}
		});
    	return settlementInfoMapList;
	}
	
	/**
	 * 获取合计信息
	 * @param crewId
	 * @return
	 */
	private Map<String, Object> getTotalInfo(String crewId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		/*
    	 * 总的预算和已付
    	 */
    	Double totalBudgetMoney = 0.0;	//总预算
    	Double totalPayedMoney = 0.0;	//总支出
    	Double totalLeftMoney = 0.0;	//总结余
    	Double totalPayedRate = 0.0;	//总完成比例
    	
    	//财务结算信息
    	List<Map<String, Object>> finanSubjWithSettleList = this.financeSubjectService.queryWithSettleInfo(crewId, null, null);
    	for (Map<String, Object> map : finanSubjWithSettleList) {
    		Double payedMoney = (Double) map.get("payedMoney");
    		Double exchangeRate = (Double) map.get("exchangeRate");
    		
    		if (payedMoney != null && exchangeRate != null) {
    			totalPayedMoney = BigDecimalUtil.add(totalPayedMoney, BigDecimalUtil.multiply(payedMoney, exchangeRate));
    		}
    	}
    	
    	//获取财务科目列表，预算信息
    	List<Map<String, Object>> finanSubjWithBudgetList = this.financeSubjectService.queryWithBudgetInfo(crewId);
    	for (Map<String, Object> map : finanSubjWithBudgetList) {
    		Double money = (Double) map.get("money");
    		Double exchangeRate = (Double) map.get("exchangeRate");
    		if (money != null && exchangeRate != null) {
    			totalBudgetMoney = BigDecimalUtil.add(totalBudgetMoney, BigDecimalUtil.multiply(money, exchangeRate));
    		}
    	}
    	
    	totalLeftMoney = BigDecimalUtil.subtract(totalBudgetMoney, totalPayedMoney);
    	if (totalBudgetMoney != 0) {
    		totalPayedRate = BigDecimalUtil.divide(totalPayedMoney, totalBudgetMoney);
    	}
    	
    	resultMap.put("totalBudgetMoney", this.df1.format(totalBudgetMoney));
    	resultMap.put("totalPayedMoney", this.df1.format(totalPayedMoney));
    	resultMap.put("totalLeftMoney", this.df1.format(totalLeftMoney));
    	resultMap.put("totalPayedRate", this.df2.format(totalPayedRate * 100) + "%");
    	return resultMap;
	}
	
	/**
	 * 获取项目制作进度
	 * @param crewId
	 * @return
	 * @throws Exception 
	 */
	private Map<String, Object> getProductionSchedule(String crewId) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		int totalViewCount = 0; //总场数
    	int finishedViewCount = 0; //已完成场数
    	int unfinishedViewCount = 0; //剩余场数
    	double viewFinishedRate = 0; //场数完成进度
    	double totalPageCount = 0; //总页数
    	double finishedPageCount = 0; //已完成页数
    	double unfinishedPageCount = 0; //剩余页数
    	double pageFinishedRate = 0; //页数完成进度
    	
    	//查询场次、页数信息
    	Map<String, Object> viewTotalInfo = viewInfoService.queryViewCountStatistic(crewId);
    	if(viewTotalInfo != null && !viewTotalInfo.isEmpty()) {
    		if(viewTotalInfo.get("totalViewCount") != null) {
        		totalViewCount = ((Long) viewTotalInfo.get("totalViewCount")).intValue();
    		}
    		if(viewTotalInfo.get("finishedViewCount") != null) {
        		finishedViewCount = ((BigDecimal) viewTotalInfo.get("finishedViewCount")).intValue();
    		}
    		if(viewTotalInfo.get("unfinishedViewCount") != null) {
        		unfinishedViewCount = ((BigDecimal) viewTotalInfo.get("unfinishedViewCount")).intValue();
    		}
    		if(totalViewCount != 0) {
        		viewFinishedRate = BigDecimalUtil.divide(finishedViewCount, totalViewCount);
    		}
    		if(viewTotalInfo.get("totalPageCount") != null) {
        		totalPageCount = (Double) viewTotalInfo.get("totalPageCount");
    		}
    		if(viewTotalInfo.get("finishedPageCount") != null) {
        		finishedPageCount = (Double) viewTotalInfo.get("finishedPageCount");
    		}
    		if(viewTotalInfo.get("unfinishedPageCount") != null) {
        		unfinishedPageCount = (Double) viewTotalInfo.get("unfinishedPageCount");
    		}
    		if(totalPageCount != 0) {
        		pageFinishedRate = BigDecimalUtil.divide(finishedPageCount, totalPageCount);
    		}
    	}
    	
    	//剧组信息
    	CrewInfoModel crewInfo = this.crewInfoService.queryById(crewId);
    	Date shootStartDate = crewInfo.getShootStartDate();
		Date shootEndDate = crewInfo.getShootEndDate();
		int totalShootDate = 0;//拍摄天数
		if(StringUtils.isNotBlank(shootStartDate + "") && StringUtils.isNotBlank(shootEndDate + "")) {
			totalShootDate = DateUtils.daysBetween(shootStartDate, shootEndDate) + 1;
		}
		//已拍摄天数
		int shootDate = this.noticeService.queryShootDates(crewId);
		//剩余天数
		int unfinishedShootDate = totalShootDate - shootDate;
		//天数完成进度
		double dateFinishedRate = 0;
		if(totalShootDate != 0) {
			dateFinishedRate = BigDecimalUtil.divide(shootDate, totalShootDate);
		}
		double totalBudget = 0; //总预算
		double totalInput = 0; //总投入
		double remainInput = 0; //投入预算剩余量
		double inputRate = 0; //投入完成进度
		double totalPayed = 0; //总支出
		double remainPayed = 0; //支出预算剩余量
		double payedRate = 0; //支出完成进度
		Map<String, Object> financeMap = financeSubjectService.queryTotalFinance(crewId);
		if(financeMap != null && !financeMap.isEmpty()) {
			if(financeMap.get("totalBudgetMoney") != null) {
				totalBudget = (Double) financeMap.get("totalBudgetMoney");
			}
			if(financeMap.get("totalPayedMoney") != null) {
				totalPayed = (Double) financeMap.get("totalPayedMoney");
			}
			remainPayed = totalBudget - totalPayed;
			if(totalBudget != 0) {
				payedRate = BigDecimalUtil.divide(totalPayed, totalBudget);
			}
		}
		Map<String, Object> collectionMap = collectionInfoService.queryTotalCollection(crewId);
		if(collectionMap != null && ! collectionMap.isEmpty()) {
			if(collectionMap.get("totalCollectionMoney") != null) {
				totalInput = (Double) collectionMap.get("totalCollectionMoney");
			}
			remainInput = totalBudget - totalInput;
			if(totalBudget != 0) {
				inputRate = BigDecimalUtil.divide(totalInput, totalBudget);
			}
		}
		
    	resultMap.put("totalViewCount", totalViewCount);
    	resultMap.put("finishedViewCount", finishedViewCount);
    	resultMap.put("unfinishedViewCount", unfinishedViewCount);
    	resultMap.put("viewFinishedRate", this.df2.format(viewFinishedRate * 100) + "%");
    	resultMap.put("totalPageCount", this.df2.format(totalPageCount));
    	resultMap.put("finishedPageCount", this.df2.format(finishedPageCount));
    	resultMap.put("unfinishedPageCount", this.df2.format(unfinishedPageCount));
    	resultMap.put("pageFinishedRate", this.df2.format(pageFinishedRate * 100) + "%");
		resultMap.put("totalShootDate", totalShootDate);
		resultMap.put("shootDate", shootDate);
		resultMap.put("unfinishedShootDate", unfinishedShootDate);
    	resultMap.put("dateFinishedRate", this.df2.format(dateFinishedRate * 100) + "%");
		resultMap.put("totalBudget", this.df1.format(totalBudget));
		resultMap.put("totalInput", this.df1.format(totalInput));
		resultMap.put("remainInput", this.df1.format(remainInput));
    	resultMap.put("inputRate", this.df2.format(inputRate * 100) + "%");
		resultMap.put("totalPayed", this.df1.format(totalPayed));
		resultMap.put("remainPayed", this.df1.format(remainPayed));
    	resultMap.put("payedRate", this.df2.format(payedRate * 100) + "%");
    	return resultMap;
	}
	
	/**
	 * 递归财务预算DTO
	 * @param originalBudgetInfoList
	 * @return
	 */
	private List<BudgetInfoDto> loopBudgetInfoDto(List<BudgetInfoDto> childBudgetInfoList, List<BudgetInfoDto> originalBudgetInfoList) {
		List<BudgetInfoDto> myBudgetInfoList = new ArrayList<BudgetInfoDto>();
		
		/*
		 * 首先过滤出纯粹子节点科目
		 */
		List<BudgetInfoDto> parentBudgetInfoDtoList = new ArrayList<BudgetInfoDto>();
		List<BudgetInfoDto> childBudgetInfoDtoList = new ArrayList<BudgetInfoDto>();
		
		
		for (BudgetInfoDto forgBudgetDto : originalBudgetInfoList) {
			String fid = forgBudgetDto.getFinanceSubjId();
			String fparentId = forgBudgetDto.getFinanceSubjParentId();
			
			boolean isChild = false;
			boolean isParent = false;
			for (BudgetInfoDto corgBudgetDto : originalBudgetInfoList) {
				String cid = corgBudgetDto.getFinanceSubjId();
				String cparentId = corgBudgetDto.getFinanceSubjParentId();
				
				if (fid.equals(cparentId)) {
					isParent = true;
				}
				if (fparentId.equals(cid)) {
					isChild = true;
				}
			}
			
			//双层循环遍历科目列表，区分中哪些科目是别人的子科目，哪些科目是别人的父科目
			//因为数据嵌套多层，过滤出的这两类数据必然会有重合的地方，但是childBudgetInfoDtoList中有而parentBudgetInfoDtoList没有的数据必然是叶子节点
			if (isChild || (!isParent && !isChild)) {
				childBudgetInfoDtoList.add(forgBudgetDto);
			}
			if (isParent) {
				parentBudgetInfoDtoList.add(forgBudgetDto);
			}
		}
		
		//childBudgetInfoDtoList中有而parentBudgetInfoDtoList没有的数据必然是叶子节点
		List<BudgetInfoDto> leafBudgetInfoDtoList = new ArrayList<BudgetInfoDto>();
		for (BudgetInfoDto corgBudgetDto : childBudgetInfoDtoList) {
			boolean eixst = false;
			for (BudgetInfoDto forgBudgetDto : parentBudgetInfoDtoList) {
				if (corgBudgetDto.getFinanceSubjId().equals(forgBudgetDto.getFinanceSubjId())) {
					eixst = true;
					break;
				}
			}
			
			if (!eixst) {
				leafBudgetInfoDtoList.add(corgBudgetDto);
			}
		}
		
		
		/*
		 * 为最后的结果字段赋值
		 * leafBudgetInfoDtoList表示当前循环中的叶子科目
		 * 但是相对于上一层传过来的childBudgetInfoList，leafBudgetInfoDtoList中有些数据为childBudgetInfoList中数据的父科目
		 * 因此，此处对比出leafBudgetInfoDtoList中每个科目的子科目，然后为相应字段赋值
		 * 
		 * 如果数据在childBudgetInfoList存在且在leafBudgetInfoDtoList中找不到任何父科目，则说明此数据层级也为当前循环的叶子科目
		 */
		for (BudgetInfoDto leafBudgetDto : leafBudgetInfoDtoList) {
			
			List<BudgetInfoDto> children = new ArrayList<BudgetInfoDto>();	//子科目
			List<BudgetCurrencyDto> fbudgetCurrencyList = leafBudgetDto.getBudgetCurrencyList();	//科目对应的货币信息列表
			
			for (BudgetInfoDto corgBudgetDto : childBudgetInfoList) {
				
				if (leafBudgetDto.getFinanceSubjId().equals(corgBudgetDto.getFinanceSubjParentId())) {
					children.add(corgBudgetDto);
					
					//把子科目中的每个货币总金额加到父科目中每个货币总金额上
					for (BudgetCurrencyDto fcurrencyDto : fbudgetCurrencyList) {
						for (BudgetCurrencyDto ccurrencyDto : corgBudgetDto.getBudgetCurrencyList()) {
							if (ccurrencyDto.getCurrencyId().equals(fcurrencyDto.getCurrencyId())) {
								fcurrencyDto.setMoney(BigDecimalUtil.add(fcurrencyDto.getMoney(), ccurrencyDto.getMoney()));
								fcurrencyDto.setSettleMoney(BigDecimalUtil.add(fcurrencyDto.getSettleMoney(), ccurrencyDto.getSettleMoney()));
								break;
							}
						}
					}
					
				}
			}
						
			leafBudgetDto.setBudgetCurrencyList(fbudgetCurrencyList);
			leafBudgetDto.setChildren(children);
			if (children != null && children.size() > 0) {
				leafBudgetDto.setHasChildren(true);
			}
			
			myBudgetInfoList.add(leafBudgetDto);
		}
		
		for (BudgetInfoDto corgBudgetDto : childBudgetInfoList) {
			boolean exist = false;
			for (BudgetInfoDto leafBudgetDto : leafBudgetInfoDtoList) {
				if (corgBudgetDto.getFinanceSubjParentId().equals(leafBudgetDto.getFinanceSubjId())) {
					exist = true;
					break;
				}
			}
			
			if (!exist) {
				myBudgetInfoList.add(corgBudgetDto);
			}
		}
		
		
		//如果当前遍历中没有任何父科目了，则说明已经遍历完了，不需要递归了
		if (parentBudgetInfoDtoList.size() > 0) {
			originalBudgetInfoList.removeAll(myBudgetInfoList);
			myBudgetInfoList = this.loopBudgetInfoDto(myBudgetInfoList, originalBudgetInfoList);
		}
		
		return myBudgetInfoList;
	}
	
	/**
	 * 把按照父子结构封装起来的财务科目展开
	 * @param budgetDtoList
	 * @return
	 */
	private List<Map<String, Object>> genBudgetInfo(List<Map<String, Object>> budgetInfoMapList, List<BudgetInfoDto> budgetDtoList) {
		List<Map<String, Object>> myBudgetInfoMapList = new ArrayList<Map<String, Object>>();
		
		for (BudgetInfoDto budgetInfoDto : budgetDtoList) {
			String financeSubjId = budgetInfoDto.getFinanceSubjId();
			String financeSubjName = budgetInfoDto.getFinanceSubjName();
			String financeSubjParentId = budgetInfoDto.getFinanceSubjParentId();
			String remark = budgetInfoDto.getRemark();
			Integer level = budgetInfoDto.getLevel();
			boolean hasChildren = budgetInfoDto.isHasChildren();

			Map<String, Object> budgetInfoMap = new HashMap<String, Object>();
			budgetInfoMap.put("financeSubjId", financeSubjId);
			budgetInfoMap.put("financeSubjName", financeSubjName);
			budgetInfoMap.put("financeSubjParentId", financeSubjParentId);
			budgetInfoMap.put("remark", remark);
			budgetInfoMap.put("hasChildren", hasChildren);
			budgetInfoMap.put("level", level);
			budgetInfoMap.put("sequence", budgetInfoDto.getSequence());
			
			Double totalBadgetMoney = 0.0;	//总预算
			Double totalPayedMoney = 0.0;	//总支出
			
			List<BudgetCurrencyDto> budgetCurrencyList = budgetInfoDto.getBudgetCurrencyList();
			for (BudgetCurrencyDto budgetCurrencyDto : budgetCurrencyList) {
				Double money = budgetCurrencyDto.getMoney();
				Double settleMoney = budgetCurrencyDto.getSettleMoney();
				Double exchangeRate = budgetCurrencyDto.getExchangeRate();
				
				totalBadgetMoney = BigDecimalUtil.add(totalBadgetMoney, BigDecimalUtil.multiply(money, exchangeRate));
				totalPayedMoney = BigDecimalUtil.add(totalPayedMoney, BigDecimalUtil.multiply(settleMoney, exchangeRate));
			}
			Double totalLeftMoney = BigDecimalUtil.subtract(totalBadgetMoney, totalPayedMoney);	//总支出
			
			Double totalPayedRate = 0.0;	//总支出比例
			if (totalBadgetMoney != null && totalBadgetMoney != 0) {
				totalPayedRate = BigDecimalUtil.divide(totalPayedMoney, totalBadgetMoney);
			}
			
			budgetInfoMap.put("totalBadgetMoney", this.df1.format(totalBadgetMoney));
			budgetInfoMap.put("totalPayedMoney", this.df1.format(totalPayedMoney));
			budgetInfoMap.put("totalLeftMoney", this.df1.format(totalLeftMoney));
			budgetInfoMap.put("totalPayedRate", this.df2.format(totalPayedRate * 100) + "%");
			
			myBudgetInfoMapList.add(budgetInfoMap);
			
			List<BudgetInfoDto> children = budgetInfoDto.getChildren();
			if (children != null && children.size() > 0) {
				myBudgetInfoMapList.addAll(this.genBudgetInfo(myBudgetInfoMapList, children));
			}
		}
		
		return myBudgetInfoMapList;
	}
}
