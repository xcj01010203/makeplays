package com.xiaotu.makeplays.index.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.index.service.IndexService;
import com.xiaotu.makeplays.roleactor.service.ViewRoleService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.DateUtils;

/**
 * 首页
 * @author Administrator
 *
 */
@Controller
/*@RequestMapping("indexController")*/
public class IndexController extends BaseController {
	
	Logger logger = LoggerFactory.getLogger(IndexController.class);
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private CrewInfoService crewInfoService;
	
//	@Autowired
//	private RoleActorService roleActorService;
	
//	@Autowired
//	private ShootReportService shootReportService;
	
	@Autowired
	private IndexService indexService;
	
	@Autowired
	private ViewRoleService viewRoleService;
	
	@RequestMapping("/toIndexPage")
	public ModelAndView index(HttpServletRequest request) throws Exception{
		
		ModelAndView view = new ModelAndView("index");
		
		String crewId = this.getCrewId(request);
		
		int userType = 0;
		
		//计算日期
		CrewInfoModel crewInfo = this.crewInfoService.queryById(crewId);
		if (crewInfo == null) {
			userType = 1;
		} else {
			view.addObject("projectType", crewInfo.getProjectType());
		}
		view.addObject("userType", userType);
		//status 拍摄状态  1、尚未设定拍摄周期，去设置，2、距离开机还有xx天，3、 已开机xx天，预计剩余xx天，4、已杀青
		
		if(crewInfo == null || crewInfo.getShootStartDate()== null || crewInfo.getShootEndDate() == null) {  //拍摄起止日期未定
			view.addObject("status", 1);
		}else{
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd "); 
				//String startDateStr = (String) crewInfo.get("shootStartDate");
				//String endDateStr = (String) crewInfo.get("shootEndDate");
				Date startDate = crewInfo.getShootStartDate();
				Date endDate = crewInfo.getShootEndDate();
				String curDate = sdf.format(new Date());
				Date nowDate = sdf.parse(curDate);
				if(nowDate.getTime() < startDate.getTime()){ //距离开机还有xx天
					view.addObject("status", 2);
					view.addObject("forwordStartDate", daysBetween(nowDate,startDate));
				}else if(nowDate.getTime() >= startDate.getTime() && nowDate.getTime() <= endDate.getTime()){
					view.addObject("status", 3);
					view.addObject("endStartDate", daysBetween(startDate,nowDate) + 1);
					view.addObject("forwordEndDate", daysBetween(nowDate,endDate));
				}else if(nowDate.getTime() > endDate.getTime()){
					view.addObject("status", 4);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("计算拍摄日期出错！", e);
			}
		}
		
		//主要角色戏量及进度
		List<Map<String,Object>> li = this.viewRoleService.getIndexCount(crewId);
		view.addObject("roleCount", li);
		
		view.addObject("shootSchedule", this.indexService.getViewListStatistics(crewId));
		
		
		
		//剧组是否过期
		String crewStatusMessage = "";
		
		if (!StringUtils.isBlank(crewId)) {
			CrewInfoModel crewInfoModel = this.crewInfoService.queryById(crewId);
			Date endDate = crewInfoModel.getEndDate();
			
			int days = DateUtils.daysBetween(new Date(), endDate);
			if (days < 0) {
				crewStatusMessage = "已过期";
			}
			if (days >= 0 && days < 5) {
				crewStatusMessage = "剧组还有" + (days + 1) + "天过期";
			}
		}
		view.addObject("crewStatusMessage", crewStatusMessage);
		
		return view;
	}
	
	/**
	 * 获取总进度数据
	 */
	@RequestMapping("index/getTotalSchedule")
	public @ResponseBody Map getTotalSchedule(HttpServletRequest request){
		Map map = new HashMap();
		String crewId = this.getCrewId(request);
		//获取文武戏统计
		Map military = this.indexService.getViewTypeList(crewId);
		map.put("military", military);
		
		//获取地点统计
		map.put("address", this.indexService.getAddressData(crewId));
		
		//获取演员统计
		map.put("actor", this.indexService.getActorData(crewId));
		return map;
	}
	
	/**
	 * 获取费用进度统计数据
	 */
//	@RequestMapping("index/getFinanceSchedule")
//	public @ResponseBody Map getFinanceSchedule(){
//		String crewId = getPlayId(session);
//		Map map = new HashMap();
//		BalanceDataDto bdm = balanceService.getBalanceList(crewId,new FilterBalance());
//		List<AccountInfoModel> settleList = bdm.getBalanceList();
//		List<String> codeList = new ArrayList<String>();
//		Set<MoneyInfoModel> mm_List = null;
//		if(settleList != null && settleList.size() > 0){
//			for (AccountInfoModel moneyModel : settleList) {
//				List<MoneyInfoModel> mon = moneyModel.getMoneyList();
//				mm_List = new HashSet<MoneyInfoModel>();
//				if(mon != null && mon.size() > 0){
//					for (MoneyInfoModel mone : mon) {
//							codeList.add(mone.getCodeName());
//							mm_List.add(mone);							
//					}
//				}
//				break;
//			}
//		}
//		//合计数据
//		List<MoneyInfoModel> countList = new ArrayList<MoneyInfoModel>();
//		double totalMoney = 0d;  //预算总金额
//		double balanceMoney = 0d;  //结算总金额
//		if(codeList != null && codeList.size() > 0){
//			for (String code : codeList) {
//				double budgetCountMoney = 0d;
//				double settleAcountMoney = 0d;
//				double exchangeRate = 0d;
//				MoneyInfoModel moneyInfoModel = new MoneyInfoModel();
//				String currencyId = "";
//				String currencyCode = "";
//				if(settleList != null && settleList.size() > 0){
//					for (AccountInfoModel sa : settleList) {
//						if(sa.getAccountLevel() == 1){
//							List<MoneyInfoModel> money = sa.getMoneyList();
//							if(money != null && money.size() > 0){
//								for (MoneyInfoModel mm : money) {
//									if(code.equals(mm.getCodeName())){
//										currencyId = mm.getCurrencyId();
//										budgetCountMoney = mm.getBudgetMoney() + budgetCountMoney;
//										settleAcountMoney = mm.getSettleAcountMoney() + settleAcountMoney;
//										exchangeRate = mm.getExchangeRate();
//										currencyCode = mm.getCurrencyCode();
//									}
//								}
//							}
//						}
//					}
//				}
//				if(exchangeRate != 0d){
//					moneyInfoModel.setExchangeRate(exchangeRate);
//				}
//				moneyInfoModel.setCodeName(code);
//				moneyInfoModel.setCurrencyId(currencyId);
//				moneyInfoModel.setBudgetMoney(budgetCountMoney);
//				moneyInfoModel.setSettleAcountMoney(settleAcountMoney);
//				moneyInfoModel.setCurrencyCode(currencyCode);
//				countList.add(moneyInfoModel);
//				totalMoney = StringUtils.add(totalMoney, StringUtils.mul(exchangeRate, budgetCountMoney));
//				balanceMoney = StringUtils.add(balanceMoney, StringUtils.mul(exchangeRate, settleAcountMoney));
//				exchangeRate = 0d;
//			}
//		}
//		map.put("countList", countList);
//		map.put("totalMoney", totalMoney);
//		//获取集数
//		Long episode = this.indexService.getCrewEpisode(crewId);
//		if(episode != 0)
//			map.put("episodebudget", StringUtils.div(totalMoney, episode, 2));
//		else
//			map.put("episodebudget", 0);
//		//获取本位币
//		map.put("stardard", this.indexService.getStardard(crewId).getCode());
//		//已支出
//		map.put("balanceMoney", balanceMoney);
//		//剩余的钱数
//		double remainMoney = StringUtils.sub(this.indexService.getCollectionMoney(crewId), balanceMoney) ;
//		map.put("remainMoney", remainMoney);
//		//已拍摄天数
//		long days = this.indexService.getAlreadyShoot(crewId);
//		map.put("days", days);
//		//单日分摊成本
//		if(days != 0)
//			map.put("dayMoney", StringUtils.div(balanceMoney, days, 2));
//		else
//			map.put("dayMoney", 0);
//		//昨日支出费用
//		map.put("yesterdayPay", this.indexService.getYesterdayPay(crewId));
//		return map;
//	}
	
	//获取通告单统计
	@RequestMapping("index/getNoticeStatistics")
	public @ResponseBody Map getNoticeStatistics(HttpServletRequest request){
		Map map = new HashMap();
		String crewId = this.getCrewId(request);
		//获取上一天所有通告单统计信息
		map.put("preNotice", this.indexService.getPreNoticeTotal(crewId));
		
		//获取当天的通告单数据
		map.put("todayNotice", this.indexService.getTodayNotice(crewId));
		
		return map;
	}
	
	//获取通联表
	@RequestMapping("index/getContactData")
	public @ResponseBody Map getContactData(HttpServletRequest request){
		Map map = new HashMap();
		String crewId = this.getCrewId(request);
		map.put("contact", this.indexService.getContactList(crewId));
		return map;
	}
	
	/** 
     * 计算两个日期之间相差的天数 
     * @param date1 
     * @param date2 
     * @return 
     */  
    private static int daysBetween(Date date1,Date date2)  
    {  
        Calendar cal = Calendar.getInstance();  
        cal.setTime(date1);  
        long time1 = cal.getTimeInMillis();               
        cal.setTime(date2);  
        long time2 = cal.getTimeInMillis();       
        long between_days=(time2-time1)/(1000*3600*24);  
          
       return Integer.parseInt(String.valueOf(between_days));         
    }
	
}
