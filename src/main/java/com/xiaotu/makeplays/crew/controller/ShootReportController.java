package com.xiaotu.makeplays.crew.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.model.ShootScheduleBaseModel;
import com.xiaotu.makeplays.crew.model.ShootScheduleModel;
import com.xiaotu.makeplays.crew.service.ShootReportService;
import com.xiaotu.makeplays.roleactor.model.constants.ViewRoleType;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.ReportConst;

/**
 * 拍摄进度统计
 * @author lma 
 *2015-06-011
 */
@Deprecated
@Controller
@RequestMapping("shootReportManager")
public class ShootReportController extends BaseController {
	@Autowired
	private ShootReportService shootReportService;
	
	Logger logger=LoggerFactory.getLogger(ShootReportController.class);
	/**
	 * 拍摄进度统计
	 * @param statisticsType
	 * @param modelMap
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("formList")
	public @ResponseBody ModelAndView statisticsShootSchedule(HttpServletRequest request,Integer statisticsType,Integer tabValue,Integer viewRoleType) throws Exception{
	 ModelAndView view =new ModelAndView("reportForm/formList");
	 view.addObject("tabValue", tabValue);//初始化前端显示图表
	 CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		String crewId=crewInfo.getCrewId();
		if(statisticsType==null){
			statisticsType=ReportConst.STATISTICS_TYPE_SCENE;//默认为场
		}
		if(viewRoleType==null){
			viewRoleType= ViewRoleType.MajorActor.getValue();//主要演员
		}
		
		//view.addObject("typeline", typeline);
		view.addObject("statisticsType", statisticsType);
		view.addObject("viewRoleType", viewRoleType);
		//获取统计数据
		Map<String,Object> map=shootReportService.statisticsShootSchedule(crewId, statisticsType,viewRoleType);
		if(map!=null){
			List<ShootScheduleModel> ssmListByShootAddress=(List<ShootScheduleModel>) map.get("byShootAddress");
			view.addObject("byShootAddress", ssmListByShootAddress);
			
			List<ShootScheduleModel> ssmListByRole=(List<ShootScheduleModel>) map.get("byRole");
			view.addObject("byRole", ssmListByRole);
			
			List<ShootScheduleModel> groupCrewAmountList=(List<ShootScheduleModel>) map.get("byDay");
			view.addObject("byDay",groupCrewAmountList);
			List<String> noticeDates=(List<String>) map.get("noticeDates");
			view.addObject("noticeDates",noticeDates);
			List<ShootScheduleModel> dayTotalList=(List<ShootScheduleModel>) map.get("dayTotalList");
			view.addObject("dayTotalList",dayTotalList);
			
			List<Object> overViewShootSchedule=(List<Object>) map.get("overViewShootSchedule");
			if(overViewShootSchedule!=null && overViewShootSchedule.size()>0){
				int item_size=overViewShootSchedule.size();
				String[] overView_titles=new String[item_size];
				Double[] overView_total=new Double[item_size];
				Double[] overView_finishedCrewAmount=new Double[item_size];
				Double[] overView_unFinishCrewAmount=new Double[item_size];
				Double[] overView_finishedPercent=new Double[item_size];
				String[] overView_shootDays=new String[item_size];
				String[] overView_shotedDays=new String[item_size];
				String[] overView_dialyFinishedCrewAmount=new String[item_size];
				String[] overView_needDays=new String[item_size];
				String[] overView_earlyOrLateDays=new String[item_size];
				for(int o=0;o<overViewShootSchedule.size();o++){
					ShootScheduleBaseModel ssbm = (ShootScheduleBaseModel) overViewShootSchedule.get(o);
					overView_titles[o]=ssbm.getTitle();
					overView_total[o]=ssbm.getTotalCrewAmount();
					overView_finishedCrewAmount[o]=ssbm.getFinishedCrewAmount();
					overView_unFinishCrewAmount[o]=ssbm.getUnfinishedCrewAmount();
					overView_finishedPercent[o]=ssbm.getFinishedPercent();
					if(o==0){
						overView_shootDays[o]=ssbm.getShootDays()!=null?ssbm.getShootDays()+"":"0";
					}else {
						overView_shootDays[o]="/";
					}
 					if((ssbm.getTitle().contains("组") || ssbm.getTitle().contains("总"))){//ssbm.getTitle()!=null &&
							overView_shotedDays[o]=ssbm.getShootedDays()!=null?ssbm.getShootedDays()+"":"0";
							overView_dialyFinishedCrewAmount[o]=ssbm.getDailyFinishedCrewAmount()>0?ssbm.getDailyFinishedCrewAmount()+"":"0";
					}else{
						overView_shotedDays[o]="/";overView_dialyFinishedCrewAmount[o]="/";
					}					
				}
				view.addObject("countSize", overView_titles.length);
				//数组转换
				JSONArray jsonArray=new JSONArray();
				
				jsonArray.addAll(Arrays.asList(overView_titles));
				
				view.addObject("overView_titles", jsonArray.toString());
				jsonArray.removeAll(Arrays.asList(overView_titles));//移除当前行
				
				jsonArray.addAll(Arrays.asList(overView_total));
				view.addObject("overView_total", jsonArray.toString());	
				jsonArray.removeAll(Arrays.asList(overView_total));
				
				jsonArray.addAll(Arrays.asList(overView_finishedCrewAmount));
				view.addObject("overView_finishedCrewAmount", jsonArray.toString());
				jsonArray.removeAll(Arrays.asList(overView_finishedCrewAmount));
				
				jsonArray.addAll(Arrays.asList(overView_unFinishCrewAmount));
				view.addObject("overView_unFinishCrewAmount", jsonArray.toString());
				jsonArray.removeAll(Arrays.asList(overView_unFinishCrewAmount));
				
				jsonArray.addAll(Arrays.asList(overView_finishedPercent));
				view.addObject("overView_finishedPercent", jsonArray.toString());
				jsonArray.removeAll(Arrays.asList(overView_finishedPercent));
				
				jsonArray.addAll(Arrays.asList(overView_shootDays));
				view.addObject("overView_shootDays", jsonArray.toString());
				jsonArray.removeAll(Arrays.asList(overView_shootDays));
				
				jsonArray.addAll(Arrays.asList(overView_shotedDays));
				view.addObject("overView_shotedDays", jsonArray.toString());
				jsonArray.removeAll(Arrays.asList(overView_shotedDays));
				
				jsonArray.addAll(Arrays.asList(overView_dialyFinishedCrewAmount));
				view.addObject("overView_dialyFinishedCrewAmount", jsonArray.toString());
				jsonArray.removeAll(Arrays.asList(overView_dialyFinishedCrewAmount));
				
				Object needDays =map.get("needDays");
				overView_needDays[0]=needDays!=null?needDays.toString():"0";					
					
				Object earlyOrLateDays=map.get("earlyOrLateDays");
				overView_earlyOrLateDays[0]=earlyOrLateDays!=null?earlyOrLateDays.toString():"0";
				
				for(int i=1;i<overViewShootSchedule.size();i++){
					overView_needDays[i]="/";
					overView_earlyOrLateDays[i]="/";
				}
				
				jsonArray.addAll(Arrays.asList(overView_needDays));
				view.addObject("needDays", jsonArray.toString());
				jsonArray.removeAll(Arrays.asList(overView_needDays));
				
				jsonArray.addAll(Arrays.asList(overView_earlyOrLateDays));
				view.addObject("earlyOrLateDays",jsonArray.toString());
			}

		}
		this.sysLogService.saveSysLog(request, "查询生产进度 ", Constants.TERMINAL_PC, null, null,0);
		return view;
	}
	
	/*@RequestMapping("roleViewRoleType")
	public @ResponseBody Map roleViewRoleType(HttpServletRequest request,Integer statisticsType, Integer viewRoleType) throws Exception{
		Map<String, Object> CrewInfo = (Map<String, Object>) request.getSession().getAttribute(Constants.SESSION_CREW_INFO);
		String crewId=CrewInfo.get("crewId").toString();
		if(statisticsType==null){
			statisticsType=ReportConst.STATISTICS_TYPE_SCENE;//默认为场
		}
		if(viewRoleType==null){
			viewRoleType=Constants.VIEW_ROLE_TYPE_MAIN;//主要演员
		}
		Map map=new HashMap();
		List<ShootScheduleModel> ssmListByRole=shootReportService.roleViewRoleType(crewId, statisticsType, viewRoleType);
		view.addObject("byRole", ssmListByRole);
		return null;
	}*/
	
	
	
}
