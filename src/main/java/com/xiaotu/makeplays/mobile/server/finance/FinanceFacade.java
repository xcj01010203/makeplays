package com.xiaotu.makeplays.mobile.server.finance;

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

import com.xiaotu.makeplays.finance.controller.dto.BudgetCurrencyDto;
import com.xiaotu.makeplays.finance.controller.dto.BudgetInfoDto;
import com.xiaotu.makeplays.finance.controller.filter.PaymentInfoFilter;
import com.xiaotu.makeplays.finance.model.CurrencyInfoModel;
import com.xiaotu.makeplays.finance.model.FinanSubjCurrencyMapModel;
import com.xiaotu.makeplays.finance.model.FinanceSettingModel;
import com.xiaotu.makeplays.finance.model.FinanceSubjectModel;
import com.xiaotu.makeplays.finance.model.LoanInfoModel;
import com.xiaotu.makeplays.finance.model.PaymentFinanSubjMapModel;
import com.xiaotu.makeplays.finance.model.PaymentInfoModel;
import com.xiaotu.makeplays.finance.service.CurrencyInfoService;
import com.xiaotu.makeplays.finance.service.FinanceSettingService;
import com.xiaotu.makeplays.finance.service.FinanceSubjectService;
import com.xiaotu.makeplays.finance.service.LoanInfoService;
import com.xiaotu.makeplays.finance.service.PaymentInfoService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.mobile.server.common.BaseFacade;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.sysrole.service.SysRoleInfoService;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.MD5Util;

/**
 * 财务相关接口
 * @author xuchangjian 2016-9-21下午3:38:54
 */
@Controller
@RequestMapping("/interface/financeFacade")
public class FinanceFacade extends BaseFacade{
	
	Logger logger = LoggerFactory.getLogger(FinanceFacade.class);
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private FinanceSettingService financeSettingService;
	
	@Autowired
	private PaymentInfoService paymentInfoService;
	
	@Autowired
	private LoanInfoService loanInfoService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FinanceSubjectService financeSubjectService;
	
	@Autowired
	private CurrencyInfoService currencyInfoService;
	
	@Autowired
	private SysRoleInfoService sysRoleInfoService;
	
	/**
	 * 校验财务密码是否正确
	 * @param crewId
	 * @param userId
	 * @param password
	 * @return
	 */
	@RequestMapping("/checkPassword")
	@ResponseBody
	public Object checkPassword(String crewId, String userId, String password) {
		
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			if (StringUtils.isBlank(password)) {
				throw new IllegalArgumentException("密码不可为空");
			}
			
			FinanceSettingModel financeSetting = this.financeSettingService.queryByCrewId(crewId);
        	if (financeSetting == null) {
        		financeSetting = this.financeSettingService.initFinanceSetting(crewId);
        	}
			if (!financeSetting.getFinancePassword().equals(MD5Util.MD5(password))) {
				throw new IllegalArgumentException("密码错误");
			}
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，校验财务密码失败", e);
			throw new IllegalArgumentException("未知异常，校验财务密码失败", e);
		}
		return null;
	}
	
	/**
	 * 获取个人财务数据
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainPersonalFinanceData")
	public Object obtainPersonalFinanceData(HttpServletRequest request, String crewId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			userInfo = this.userService.queryById(userId);
			
			//用户基本信息
			List<Map<String, Object>> userRoleList = this.sysRoleInfoService.queryByCrewUserId(crewId, userId);
			String roleNames = "";
			for (Map<String, Object> userRoleMap : userRoleList) {
				String roleName = (String) userRoleMap.get("roleName");
				
				roleNames += roleName + ",";
			}
			if (!StringUtils.isBlank(roleNames)) {
				roleNames = roleNames.substring(0, roleNames.length() - 1);
			}
			
			//收款信息（从财务的角度来说是付款信息，从个人角度来说是收款信息）
			PaymentInfoFilter filter = new PaymentInfoFilter();
			filter.setPayeeNames(userInfo.getRealName());
			List<Map<String, Object>> paymentList = this.paymentInfoService.queryPaymentList(crewId, filter);
			
			Double totalCollectionMoney = 0.0;
			for (Map<String, Object> paymentInfo : paymentList) {
				Date paymentDate = (Date) paymentInfo.get("paymentDate");
				Date createTime = (Date) paymentInfo.get("createTime");
				Date remindTime = (Date) paymentInfo.get("remindTime");
				
				if (paymentDate != null) {
					paymentInfo.put("paymentDate", this.sdf1.format(paymentDate));
				}
				if (createTime != null) {
					paymentInfo.put("createTime", this.sdf1.format(createTime));
				}
				if (remindTime != null) {
					paymentInfo.put("remindTime", this.sdf1.format(remindTime));
				}
				
				Double exchangeRate = (Double) paymentInfo.get("exchangeRate");
				Double totalMoney = BigDecimalUtil.multiply((Double) paymentInfo.get("totalMoney"), exchangeRate);//金额需要折合成本位币的金额
				
				paymentInfo.put("totalMoney", totalMoney);
				
				totalCollectionMoney = BigDecimalUtil.add(totalCollectionMoney, totalMoney);
				
				String loanIds = (String) paymentInfo.get("loanIds");
				if (!StringUtils.isBlank(loanIds)) {
					paymentInfo.put("isforLoan", true);
				} else {
					paymentInfo.put("isforLoan", false);
				}
			}
			
			//借款信息
			List<Map<String, Object>> loanList = this.loanInfoService.queryLoanWithPaymentInfo(crewId, userInfo.getRealName(), null, null, null, null, null, null);
			
			Double totalLoanMoney = 0.0;
			Double totalLeftMoney = 0.0;
			for (Map<String, Object> loanInfo : loanList) {
				Date loanDate = (Date) loanInfo.get("loanDate");
				Date paymentDate = (Date) loanInfo.get("paymentDate");
				
				if (loanDate != null) {
					loanInfo.put("loanDate", this.sdf1.format(loanDate));
				}
				if (paymentDate != null) {
					loanInfo.put("paymentDate", this.sdf1.format(paymentDate));
				}
				
				//借款金额
				Double exchangeRate = (Double) loanInfo.get("exchangeRate");
				Double money = BigDecimalUtil.multiply((Double) loanInfo.get("money"), exchangeRate);	//金额需要折合成本位币的金额
				Double leftMoney = BigDecimalUtil.multiply((Double) loanInfo.get("leftMoney"), exchangeRate);	//剩余还款金额，金额需要折合成本位币的金额
				Double payedMoney = BigDecimalUtil.subtract(money, leftMoney);
				
				loanInfo.put("money", money);
				loanInfo.put("payedMoney", payedMoney);
				loanInfo.put("leftMoney", leftMoney);
				
				totalLoanMoney = BigDecimalUtil.add(totalLoanMoney, money);
				totalLeftMoney = BigDecimalUtil.add(totalLeftMoney, leftMoney);
			}
			
			resultMap.put("imgUrl", FileUtils.genPreviewPath(userInfo.getSmallImgUrl()));
			resultMap.put("userName", userInfo.getRealName());
			resultMap.put("roleNames", roleNames);
			
			resultMap.put("totalCollectionMoney", totalCollectionMoney);
			resultMap.put("totalLoanMoney", totalLoanMoney);
			resultMap.put("totalLeftMoney", totalLeftMoney);
			resultMap.put("collectionList", paymentList);
			resultMap.put("loanList", loanList);
			
			this.sysLogService.saveSysLogForApp(request, "查询个人财务信息", userInfo.getClientType(), 
					PaymentInfoModel.TABLE_NAME + "," + LoanInfoModel.TABLE_NAME, null, 0);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取个人财务数据失败", e);
			this.sysLogService.saveSysLogForApp(request, "查询个人财务信息失败：" + e.getMessage(), userInfo.getClientType(), 
					PaymentInfoModel.TABLE_NAME + "," + LoanInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("获取个人财务数据失败", e);
		}
		return resultMap;
	}
	
	/**
	 * 查询财务科目的结算信息（财务进度）
	 * @param crewId
	 * @param userId
	 * @param parentId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainFinanceSubjSettleInfo")
	public Object obtainFinanceSubjSettleInfo(HttpServletRequest request, String crewId, String userId, String parentId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			
			//如果parentId为空，则表示查询的是一级科目
			if (StringUtils.isBlank(parentId)) {
				parentId = "0";
			}
			
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
        	
        	
        	//获取财务科目列表
        	List<Map<String, Object>> finanSubjWithBudgetList = this.financeSubjectService.queryWithBudgetInfo(crewId);
        	
        	//把财务科目数据结合货币列表封装成BudgetInfoDto数据格式
        	List<BudgetInfoDto> budgetInfoList = new ArrayList<BudgetInfoDto>();
        	
        	for (Map<String, Object> finanSubj : finanSubjWithBudgetList) {
        		String id = (String) finanSubj.get("id");	//财务科目ID
        		String name = (String) finanSubj.get("name");	//财务科目名称
        		String myParentId = (String) finanSubj.get("parentId");	//财务科目父科目ID
        		String remark = (String) finanSubj.get("remark");
        		Integer level = (Integer) finanSubj.get("level");
        		
        		String currencyId = (String) finanSubj.get("currencyId");	//货币ID
        		
        		String mapId = (String) finanSubj.get("mapId");	//关联关系ID
        		Double amount = (Double) finanSubj.get("amount");	//数量
        		Double money = (Double) finanSubj.get("money");	//总金额
        		Double perPrice = (Double) finanSubj.get("perPrice");	//单价
        		String unitType = (String) finanSubj.get("unitType");	//单位
        		
        		//该财务科目的币种结算金额信息
        		Map<String, Double> currencySettleMap = settleMap.get(id);
        		
        		List<BudgetCurrencyDto> budgetCurrencyList = new ArrayList<BudgetCurrencyDto>();
        		
        		//把所有的货币信息封装到预算货币Dto中
        		for (CurrencyInfoModel currencyInfo : currencyInfoList) {
        			String myCurrencyId = currencyInfo.getId();
        			
        			BudgetCurrencyDto budgetCurrencyDto = new BudgetCurrencyDto();
        			budgetCurrencyDto.setCurrencyId(currencyInfo.getId());
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
        		budgetInfoDto.setFinanceSubjParentId(myParentId);
        		budgetInfoDto.setRemark(remark);
        		budgetInfoDto.setLevel(level);
        		budgetInfoDto.setBudgetCurrencyList(budgetCurrencyList);
        		
        		budgetInfoList.add(budgetInfoDto);
        	}
        	
        	//把子节点的预算值向父节点上合并，并把财务科目按照父子关系做成嵌套的形式
        	List<BudgetInfoDto> budgetDtoList = this.loopBudgetInfoDto(new ArrayList<BudgetInfoDto>(), budgetInfoList);
        	
        	//把嵌套形式的财务科目平铺开来，并且把其中的货币信息做成以货币ID为键，总金额为值的形式
        	List<Map<String, Object>> settlementInfoMapList = this.genBudgetInfo(new ArrayList<Map<String, Object>>(), budgetDtoList);
			
        	//查询当前需要的子科目
        	List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
        	Double myBudgetMoney = 0.0;
        	Double myPayedMoney = 0.0;
        	String myFinanceSubjName = "费用进度";
			for (Map<String, Object> map : settlementInfoMapList) {
				String financeSubjParentId = (String) map.get("financeSubjParentId");
				String financeSubjId = (String) map.get("financeSubjId");
				String financeSubjName = (String) map.get("financeSubjName");
				Double budgetMoney = (Double) map.get("budgetMoney");
				Double payedMoney = (Double) map.get("payedMoney");
				
				if (financeSubjParentId.equals(parentId)) {
					children.add(map);
					
					//如果当前查询的是所有一级科目，则应该返回总的总计信息
					if (parentId.equals("0")) {
						myBudgetMoney = BigDecimalUtil.add(myBudgetMoney, budgetMoney);
						myPayedMoney = BigDecimalUtil.add(myPayedMoney, payedMoney);
					}
				}
				
				if (financeSubjId.equals(parentId)) {
					myBudgetMoney = budgetMoney;
					myPayedMoney = payedMoney;
					myFinanceSubjName = financeSubjName;
				}
			}
			
			resultMap.put("financeSubjName", myFinanceSubjName);
			resultMap.put("budgetMoney", myBudgetMoney);
			resultMap.put("payedMoney", myPayedMoney);
			resultMap.put("children", children);
			
			this.sysLogService.saveSysLogForApp(request, "查询财务科目的结算信息（财务进度）", 
					userInfo.getClientType(), FinanceSubjectModel.TABLE_NAME + "," 
					+ FinanSubjCurrencyMapModel.TABLE_NAME + "," + PaymentFinanSubjMapModel.TABLE_NAME, parentId, 0);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取财务进度失败", e);
			this.sysLogService.saveSysLogForApp(request, "查询财务科目的结算信息（财务进度）失败：" + e.getMessage(), 
					userInfo.getClientType(), FinanceSubjectModel.TABLE_NAME + "," 
					+ FinanSubjCurrencyMapModel.TABLE_NAME + "," + PaymentFinanSubjMapModel.TABLE_NAME, parentId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("获取财务进度失败", e);
		}
		return resultMap;
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
			if (budgetInfoMapList.size() == 0) {
				financeSubjParentId = "0";	//第一级财务科目的父科目ID统一为0，该段代码是为了兼容以前2016.9月前改版的系统中剧组的数据
			}
			budgetInfoMap.put("financeSubjParentId", financeSubjParentId);
			budgetInfoMap.put("remark", remark);
			budgetInfoMap.put("hasChildren", hasChildren);
			budgetInfoMap.put("level", level);
			
			List<BudgetCurrencyDto> budgetCurrencyList = budgetInfoDto.getBudgetCurrencyList();
			Double totalBudgetMoney = 0.0;
			Double totalSettleMoney = 0.0;
			Double totalLeftMoney = 0.0;
			for (BudgetCurrencyDto budgetCurrencyDto : budgetCurrencyList) {
				double exchangeRate = budgetCurrencyDto.getExchangeRate();
				Double money = BigDecimalUtil.multiply(budgetCurrencyDto.getMoney(), exchangeRate);
				Double settleMoney = BigDecimalUtil.multiply(budgetCurrencyDto.getSettleMoney(), exchangeRate);
				
				Double leftMoney = BigDecimalUtil.subtract(money, settleMoney);
				
				totalBudgetMoney = BigDecimalUtil.add(totalBudgetMoney, money);
				totalSettleMoney = BigDecimalUtil.add(totalSettleMoney, settleMoney);
				totalLeftMoney = BigDecimalUtil.add(totalLeftMoney, leftMoney);
			}
			budgetInfoMap.put("budgetMoney", totalBudgetMoney);
			budgetInfoMap.put("payedMoney", totalSettleMoney);
			budgetInfoMap.put("leftMoney", totalLeftMoney);
			
			myBudgetInfoMapList.add(budgetInfoMap);
			
			List<BudgetInfoDto> children = budgetInfoDto.getChildren();
			if (children != null && children.size() > 0) {
				myBudgetInfoMapList.addAll(this.genBudgetInfo(myBudgetInfoMapList, children));
			}
		}
		
		return myBudgetInfoMapList;
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
	 * 获取币种列表
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainCurrencyList")
	public Object obtainCurrencyList(HttpServletRequest request, String crewId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			
			Map<String, Object> conditionMap = new HashMap<String, Object>();
        	conditionMap.put("crewId", crewId);
        	conditionMap.put("ifEnable", true);
        	
        	List<CurrencyInfoModel> currencyInfoList = this.currencyInfoService.queryManyByMutiCondition(conditionMap, null);
        	
        	//如果剧组中还没有货币信息，则为剧组初始化一个货币
        	if (currencyInfoList == null || currencyInfoList.size() == 0) {
        		CurrencyInfoModel standardCurrency = this.currencyInfoService.initFirstCurrency(crewId);
        		currencyInfoList = new ArrayList<CurrencyInfoModel>();
        		currencyInfoList.add(standardCurrency);
        	}
        	
        	resultMap.put("currencyList", currencyInfoList);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取币种列表失败", e);
			this.sysLogService.saveSysLogForApp(request, "获取币种列表失败：" + e.getMessage(), userInfo.getClientType(), 
					CurrencyInfoModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("获取币种列表失败", e);
		}
		return resultMap;
	}
}
