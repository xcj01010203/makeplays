package com.xiaotu.makeplays.crew.controller;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.crew.model.CrewAmountModel;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.model.RoleCrewAmountModel;
import com.xiaotu.makeplays.crew.model.RolecrewAmountBaseModel;
import com.xiaotu.makeplays.crew.service.RoleCrewAmountReportService;
import com.xiaotu.makeplays.roleactor.model.constants.ViewRoleType;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.ReportConst;


@Deprecated
@Controller
@RequestMapping("lmaroleCrewReportManager")
public class RoleCrewAmountReportController extends BaseController{

	@Autowired
	private RoleCrewAmountReportService roleCrewAmountReportService;
	
	Logger logger=LoggerFactory.getLogger(RoleCrewAmountReportController.class);
	
	/**
	 * 页面初次跳转
	 */
	@RequestMapping("roleCrewAmount")
	public ModelAndView roleCrewAmount(HttpServletRequest request,Integer roleType,Integer tabValue,Integer tabTable) throws Exception{
		ModelAndView view=new ModelAndView("reportForm/roleCrewAmount");
		//取当前剧组ID
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		String crewId= crewInfo.getCrewId();
		//获取当前剧组所有角色
		view.addObject("tabValue", tabValue);
		view.addObject("tabTable", tabTable);
		if(roleType==null){
			roleType= ViewRoleType.MajorActor.getValue();
		}
		view.addObject("roleType", roleType);
		List<RolecrewAmountBaseModel>  roleCrewAmountBaseModelList= null;
		List<String> roleNameList=null; //报表中出现的角色的名称列表
		//按拍摄地点统计
		RoleCrewAmountModel rpamByShootAddress=roleCrewAmountReportService.statisticsRolePlayAmoutByShootAddress(crewId, roleType);
		//获取拍摄地点信息
				if (rpamByShootAddress != null) {
					roleCrewAmountBaseModelList = rpamByShootAddress.getRpabmList();
				}
				//遍历获取每个演员的拍摄地点个数
				if (roleCrewAmountBaseModelList != null && roleCrewAmountBaseModelList.size() > 0) {
					roleNameList=new ArrayList<String>();
					for (RolecrewAmountBaseModel roleCrewAmountBaseModel : roleCrewAmountBaseModelList) {
						roleNameList.add(roleCrewAmountBaseModel.getRoleName());
					}
				}
		view.addObject("roleNameList", roleNameList);
		return view;
	}
	/**
	 * 按拍摄地点主场景统计
	 * @param request
	 * @param roleType
	 * @param tabValue
	 * @throws Exception 
	 */
	@RequestMapping("getByAddress")
	@ResponseBody
	public Map<String, Object> getByAddress(HttpServletRequest request,Integer roleType,Integer tabValue,String roleName) throws Exception{
		Map<String, Object> map=new HashMap<String, Object>();
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		String crewId=crewInfo.getCrewId();
		if(roleType==null){
			roleType= ViewRoleType.MajorActor.getValue();
		}
		List<RolecrewAmountBaseModel>  roleCrewAmountBaseModelList= null;
		List<CrewAmountModel>  crewAmountModelList = null;
		RoleCrewAmountModel rpamByAddress= roleCrewAmountReportService.statisticsByAddress(crewId, roleType,roleName);
		//拍摄地点
		int strs=rpamByAddress.getRpabmList().get(0).getCrewAmountModelList().size();
		CrewAmountModel lmacrew=new CrewAmountModel();
		//获取当前角色所有主场景lma
		NumberFormat nf=new DecimalFormat("##,##0.00");
		StringBuffer lmaBuffer=new StringBuffer();
		Integer nineScene=0;//lma 夜气氛
		double ninePage=0;
		Integer byScene=0;//lma 日气氛
		double byPage=0;
		Integer nineByScene=0;//lma 日夜气氛
		double nineByPage=0;
		Integer neiScene=0;//内景
		double neiPage=0;
		Integer waiScene=0;//外景
		double waiPage=0;
		Integer neiWaiScene=0;//内外景
		double neiWaiPage=0;
		Integer wenScene=0;//文戏
		double wenPage=0;
		Integer wuScene=0;//武戏
		double wuPage=0;
		Integer wenWuScene=0;//文武戏
		double wenWuPage=0;
		for (int i = 0; i <strs ; i++) {
			for (int j = 0; j < rpamByAddress.getRpabmList().get(0).getCrewAmountModelList().get(i).getChildcrewAmountModelList().size(); j++) {
				lmacrew=rpamByAddress.getRpabmList().get(0).getCrewAmountModelList().get(i).getChildcrewAmountModelList().get(j);
				//计算气氛
				if(!StringUtils.isBlank(lmacrew.getAtmosphereName()) && lmacrew.getAtmosphereName().contains("夜") && !lmacrew.getAtmosphereName().contains("日夜") ){
					nineScene+=(int)lmacrew.getcrewAmountByview();
					ninePage+=lmacrew.getcrewAmountByPage();
				}else if(!StringUtils.isBlank(lmacrew.getAtmosphereName()) && lmacrew.getAtmosphereName().contains("日夜")){
					nineByScene+=(int)lmacrew.getcrewAmountByview();
					nineByPage+=lmacrew.getcrewAmountByPage();
				}else{
					byScene+=(int)lmacrew.getcrewAmountByview();
					byPage+=lmacrew.getcrewAmountByPage();
				}
				//计算内外景
				if(!StringUtils.isBlank(lmacrew.getSite()) && lmacrew.getSite().equals("内")){
					neiScene+=(int)lmacrew.getcrewAmountByview();
					neiPage+=lmacrew.getcrewAmountByPage();
				}else if(!StringUtils.isBlank(lmacrew.getSite()) && lmacrew.getSite().equals("外")){
					 waiScene+=(int)lmacrew.getcrewAmountByview();
					 waiPage+=lmacrew.getcrewAmountByPage();
				}else {
					 neiWaiScene+=(int)lmacrew.getcrewAmountByview();
					 neiWaiPage+=lmacrew.getcrewAmountByPage();
				}
				//计算文武戏
				if(lmacrew.getViewType()==2){
					wuPage+=lmacrew.getcrewAmountByPage();
					wuScene+=(int)lmacrew.getcrewAmountByview();
				}else if(lmacrew.getViewType()==3){
					wenWuPage+=lmacrew.getcrewAmountByPage();
					wenWuScene+=(int)lmacrew.getcrewAmountByview();
				}else{
					wenPage+=lmacrew.getcrewAmountByPage();
					wenScene+=(int)lmacrew.getcrewAmountByview();
				}
				if(lmacrew.getAddressName()!=null){
					lmaBuffer.append("<tr  align='left'><td  rowspan='"+(rpamByAddress.getRpabmList().get(0).getCrewAmountModelList().get(i).getChildcrewAmountModelList().size()+1)+"'>");
					double page= rpamByAddress.getRpabmList().get(0).getCrewAmountModelList().get(i).getcrewAmountByPage();
					int view =(int) rpamByAddress.getRpabmList().get(0).getCrewAmountModelList().get(i).getcrewAmountByview();
					lmaBuffer.append(lmacrew.getAddressName()+"&nbsp;&nbsp;&nbsp;&nbsp;<br/>"+view+"场"+nf.format(page)+"页</td></tr>");
				}  
				lmaBuffer.append("<tr ><td  align='left' >"+lmacrew.getName()+"</td>");
				lmaBuffer.append("<td align='center' >"+(int)lmacrew.getcrewAmountByview()+"场/"+ nf.format(lmacrew.getcrewAmountByPage())+"</td></tr>");
			}
		}
		if(wuPage!=0){
			map.put("wuPage", "武："+wuScene+"场/"+nf.format(wuPage)+"页");
			map.put("wenPage", "文："+wenScene+"场/"+nf.format(wenPage)+"页");
			if(wenWuPage!=0)
				map.put("wenWuPage", "文武："+wenWuScene+"场/"+nf.format(wenWuPage)+"页");
		}
		if(neiScene!=0)
			map.put("neiScene", neiScene+"场/"+nf.format(neiPage)+"页");//内景
		if(waiScene!=0)
			map.put("waiScene", waiScene+"场/"+nf.format(waiPage)+"页");//外景
		if(neiWaiScene!=0)
			map.put("neiWaiScene", neiWaiScene+"场/"+nf.format(neiWaiPage)+"页");//内外景
		if(nineScene!=0)
			map.put("nineScene",  nineScene+"场/"+nf.format(ninePage)+"页"); //气氛
		if(byScene!=0)
			map.put("byScene",  byScene+"场/"+nf.format(byPage)+"页");
		if(nineByScene!=0)
			map.put("nineByScene", nineByScene+"场/"+nf.format(nineByPage)+"页");
		map.put("resulDatatList", lmaBuffer.toString());
		//按拍摄地点主场景统计下的拍摄地点个数
		if (rpamByAddress != null) {
			roleCrewAmountBaseModelList = rpamByAddress.getRpabmList();
		}
		//主场景去重集合
		Set<String> childCrewAmountModelLists = new HashSet<String>();
		//遍历获取每个演员的主场景个数
		if (roleCrewAmountBaseModelList != null && roleCrewAmountBaseModelList.size() > 0) {
			for (RolecrewAmountBaseModel roleCrewAmountBaseModel : roleCrewAmountBaseModelList) {
				crewAmountModelList = roleCrewAmountBaseModel.getCrewAmountModelList();
				if (crewAmountModelList != null && crewAmountModelList.size() > 0) {
					for (CrewAmountModel crewAmountModel : crewAmountModelList) {
						if (crewAmountModel != null) {
							List<CrewAmountModel> childPlayAmountModelList = crewAmountModel.getChildcrewAmountModelList();
							if (childPlayAmountModelList != null && childPlayAmountModelList.size() > 0) {
								for (CrewAmountModel childPlayAmountModel : childPlayAmountModelList) {
									if (childPlayAmountModel != null && !"待定".equals(childPlayAmountModel.getName())) {
										childCrewAmountModelLists.add(childPlayAmountModel.getName());
									}
								}
							}
						}
					}
				}
				//主场景个数
				roleCrewAmountBaseModel.setShootAddressCount(childCrewAmountModelLists.size());
				childCrewAmountModelLists.clear();
			}
		}
		StringBuffer sb=new StringBuffer();
		for (RolecrewAmountBaseModel rolecrewAmount : rpamByAddress.getRpabmList()) {
			sb.append("<tr>");
			if(rolecrewAmount.getCrewAmountModelList().size()>0){
				sb.append("<td align='center' rowspan='"+rolecrewAmount.getCrewAmountModelList().size()+"'>");
				sb.append("<div class='td_div'>"+rolecrewAmount.getRoleName()+"</div>");
				int view=(int) rolecrewAmount.getTotalCrewAmountByView();
				sb.append("<div class='td_div shoot_addr_dow mt10 '>"+view+"场/"+rolecrewAmount.getTotalCrewAmountByPage()+"页</div>");
				sb.append("<div class='td_div'>主场景个数："+rolecrewAmount.getShootAddressCount()+"场</div>");
				sb.append("</td>");
				int i=0;
				for (CrewAmountModel crewAmountModel : rolecrewAmount.getCrewAmountModelList()) {
					if(i>0){
						sb.append("<tr>");
					}
					i++;
					sb.append("<td align='center' style=\"border-right: 5px solid #ccc;\"><div class='td_div'>"+crewAmountModel.getName()+"</div>");
					int views=(int) crewAmountModel.getcrewAmountByview();
					sb.append("<div class='w120 shoot_addr_dow mt10 td_div'>"+views+"场/"+crewAmountModel.getcrewAmountByPage()+"页</div>");
				    sb.append("</td><td >");
					if(crewAmountModel.getChildcrewAmountModelList().size()>0){
						sb.append("<div class='shoot_addr_tit'>");
						for (CrewAmountModel childcrewAmountModel : crewAmountModel.getChildcrewAmountModelList()) {
							sb.append("<div class='shoot_addr_wrap'>");
							sb.append("<div align='center' class='w120 shoot_addr_top' title='"+childcrewAmountModel.getName()+"'>"+childcrewAmountModel.getName()+"</div>");
							int viewt=(int) childcrewAmountModel.getcrewAmountByview();
							sb.append("<div align='center' class='w120 shoot_addr_dow mt10 '>"+viewt+"场/"+childcrewAmountModel.getcrewAmountByPage()+"页</div>");
							sb.append("</div>");
						}
						sb.append("</div>");
					}
					sb.append("</td></tr>");
					if(rolecrewAmount.getCrewAmountModelList().size()<0){
						sb.append("<td><div class='td_div'>"+rolecrewAmount.getRoleName()+"</div></td><td></td><td></td></tr>");
					}
				}
			}
		}
		//lma角色戏量
		map.put("actorList", rpamByAddress.getRpabmList());
		map.put("totaView",  (int)rpamByAddress.getRpabmList().get(0).getTotalCrewAmountByView()+"场/"+nf.format(rpamByAddress.getRpabmList().get(0).getTotalCrewAmountByPage())+"页");
		map.put("mainCount", rpamByAddress.getRpabmList().get(0).getShootAddressCount());
		map.put("sbStr", sb.toString());
		return map;
	}
	/**
	 * 按集统计
	 * @param request
	 * @param roleType
	 * @param tabValue
	 * @param statisType 1.按场，2.按页
	 * @throws Exception
	 */
	@RequestMapping("getByContSet")
	@ResponseBody
	public Map<String, Object> getByContSet(HttpServletRequest request,Integer roleType,Integer tabValue,String roleName,Integer statisType) throws Exception{
		Map<String, Object> map= new HashMap<String, Object>();
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		String crewId=crewInfo.getCrewId();
		if(roleType==null){
			roleType= ViewRoleType.MajorActor.getValue();
		}
		RoleCrewAmountModel rpamBySet=roleCrewAmountReportService.statisticsRolePlayAmoutBySet(crewId, roleType,roleName);
		List<CrewAmountModel>  crewAmountList = new ArrayList<CrewAmountModel>();
		CrewAmountModel  crewAmountmodel = null;
		List<Double> series =new ArrayList<Double>();
		int countPart = 0;
		List<Integer> categories = rpamBySet.getSetNos();
		for (int i = 0; i < rpamBySet.getRpabmList().get(0).getCrewAmountModelList().size(); i++) {
//			categories.add((i+1)+"");
			if(rpamBySet.getRpabmList().get(0).getCrewAmountModelList().get(i)!=null){
				//crewAmountList.add(rpamBySet.getRpabmList().get(0).getCrewAmountModelList().get(i));
				crewAmountmodel = rpamBySet.getRpabmList().get(0).getCrewAmountModelList().get(i);
				
				if(statisType == 1)
					series.add(crewAmountmodel.getcrewAmountByview());
				else if(statisType == 2)
					series.add(crewAmountmodel.getcrewAmountByPage());
				countPart++;
			}else{
				crewAmountmodel=new CrewAmountModel();
				crewAmountmodel.setName((i+1)+"");
				crewAmountList.add(crewAmountmodel);
				series.add(0.0);
			}
		}
		//lma 分集
		//map.put("pageSize", crewAmountList);
		Map<String, Object> seriesMap = new HashMap<String, Object>();
		if(statisType == 1)
			seriesMap.put("name", "场");
		else if(statisType == 2)
			seriesMap.put("name", "页");
		
		seriesMap.put("type", "bar");
		seriesMap.put("data", series);
		map.put("categories", categories);
		map.put("series", seriesMap);
		map.put("pageTite", rpamBySet.getRpabmList());
		map.put("totalSetNo",rpamBySet.getSetNos().size());
		map.put("partSetNo", countPart);
		StringBuffer sbPage=new StringBuffer();
		for (Integer setNo : rpamBySet.getSetNos()) {
			sbPage.append("<td class=\"w100\" style=\"background-color:#E7EFE2\" ><div class=\"td_div\">"+setNo+"</div></td>");
		}
		map.put("setNo", sbPage.toString());
		StringBuffer sb=new StringBuffer();
		for (RolecrewAmountBaseModel rolecrewAmount : rpamBySet.getRpabmList()) {
			sb.append("<tr>");
			sb.append("<td align='center' class=\"w100\"><div class='td_div'>"+rolecrewAmount.getRoleName()+"</td>");
			String rolName=" ";
			if(rolecrewAmount.getActorName()!=null){
				rolName=rolecrewAmount.getActorName();
			}
			sb.append("<td align='center' class=\"w100\"><div class=\"td_div\">"+rolName+"</td>");
			int view=(int) rolecrewAmount.getTotalCrewAmountByView();
			sb.append("<td class=\"\" align='center' style=\"border-right: 5px solid #ccc;width: 100px;\"><div class=\"td_div\">"+view+"场/"+rolecrewAmount.getTotalCrewAmountByPage()+"页</td>");
			for (CrewAmountModel crewAmountModel : rolecrewAmount.getCrewAmountModelList()) {
					if(crewAmountModel!=null){
						int views=(int) crewAmountModel.getcrewAmountByview();
						sb.append("<td align='center' class=\"w100\"><div class=\"td_div\">"+views+"场/"+crewAmountModel.getcrewAmountByPage()+"页</td>");
					}else {
						sb.append("<td  class=\"w100\"><div class=\"td_div\"></td>");
					}
				
			}
			sb.append("</tr>");
		}
		map.put("marketForm", sb.toString());
		return map;
	}
	/**
	 * 按拍摄地点统计
	 * @param request
	 * @param roleType
	 * @param tabValue
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("getbyShootLocation")
	@ResponseBody
	public Map<String,Object> getbyShootLocation(HttpServletRequest request,Integer roleType,Integer tabValue) throws Exception{
		Map<String, Object> map=new HashMap<String, Object>();
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		String crewId=crewInfo.getCrewId();
		if(roleType==null){
			roleType= ViewRoleType.MajorActor.getValue();
		}
		List<RolecrewAmountBaseModel>  roleCrewAmountBaseModelList= null;
		List<CrewAmountModel>  crewAmountModelList = null;
		List<String> roleNameList=null; //报表中出现的角色的名称列表
		int shootAddressCount = 0;
		//按拍摄地点统计
		RoleCrewAmountModel rpamByShootAddress=roleCrewAmountReportService.statisticsRolePlayAmoutByShootAddress(crewId, roleType);
		//rpamMap.put("byShootAddress", rpamByShootAddress);
		//获取拍摄地点信息
				if (rpamByShootAddress != null) {
					roleCrewAmountBaseModelList = rpamByShootAddress.getRpabmList();
				}
				//遍历获取每个演员的拍摄地点个数
				if (roleCrewAmountBaseModelList != null && roleCrewAmountBaseModelList.size() > 0) {
					roleNameList=new ArrayList<String>();
					for (RolecrewAmountBaseModel roleCrewAmountBaseModel : roleCrewAmountBaseModelList) {
						roleNameList.add(roleCrewAmountBaseModel.getRoleName());
						crewAmountModelList = roleCrewAmountBaseModel.getCrewAmountModelList();
						if (crewAmountModelList != null && crewAmountModelList.size() > 0) {
							for (CrewAmountModel crewAmountModel : crewAmountModelList) {
								if (crewAmountModel != null && !"待定".equals(crewAmountModel.getName())) {
									shootAddressCount++;
								}
							}
						}
						roleCrewAmountBaseModel.setShootAddressCount(shootAddressCount);
						shootAddressCount = 0;
					}
				}
					StringBuffer sb=new StringBuffer();
					for (String shootAddress : rpamByShootAddress.getShootAddressList()) {
						sb.append("<td class='w100' style=\"background-color:#E7EFE2\"><div class='td_div'>"+shootAddress+"</div></td>");
					}
				map.put("headStr", sb.toString());
				//主要内容
				StringBuffer sbto=new StringBuffer();
				for (RolecrewAmountBaseModel rolecrewAmount : rpamByShootAddress.getRpabmList()) {
					sbto.append("<tr> <td align='center'><div class='td_div'>"+rolecrewAmount.getRoleName()+"</td>");
					String actroName=" ";
					if(rolecrewAmount.getActorName()!=null){
						actroName=rolecrewAmount.getActorName();
					}
					sbto.append(" <td align='center'><div class='td_div'>"+actroName+"</td>");
					int view=(int) rolecrewAmount.getTotalCrewAmountByView();
					sbto.append("<td align='center'><div class='td_div'>"+view+"场/"+rolecrewAmount.getTotalCrewAmountByPage()+"页</td>");
					String countS=" ";
					if(rolecrewAmount.getShootAddressCount()>0){
						countS=rolecrewAmount.getShootAddressCount()+"";
					}
					sbto.append(" <td align='center' style=\"border-right: 5px solid #ccc;\"><div class='td_div'>"+countS+"</td>");
					for (CrewAmountModel crewAmountModel : rolecrewAmount.getCrewAmountModelList()) {
						if(crewAmountModel!=null){
							int views=(int) crewAmountModel.getcrewAmountByview();
							sbto.append("<td align='center'  class='w100'><div class='td_div'>"+views+"场/"+crewAmountModel.getcrewAmountByPage()+"页</td>");
						}else {
							sbto.append("<td class='w100'><div class='td_div'></td>");
						}
					}
					sbto.append("</tr>");
				}
			map.put("strTo", sbto.toString());
			return map;
	}
	
	
	
	//按场景统计
	@RequestMapping("getbyViewAddress")
	@ResponseBody
	public Map<String, Object> getbySceneAddress(HttpServletRequest request,Integer roleType,Integer tabValue) throws Exception{
		Map<String, Object> map=new HashMap<String, Object>();
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		String crewId=crewInfo.getCrewId();
		if(roleType==null){
			roleType= ViewRoleType.MajorActor.getValue();
		}
		RoleCrewAmountModel rpamBySceneAddress =roleCrewAmountReportService.statisticsBySceneAddress(crewId, roleType);
		StringBuffer shootLocation=new StringBuffer();
		for (String shootAddress : rpamBySceneAddress.getShootAddressList()) {
			shootLocation.append("<td class=\"w100\" style=\"background-color:#E7EFE2;width:150px;\"><div class=\"td_div\">"+shootAddress+"</td>");
		}
		map.put("shootLocation", shootLocation.toString());
		StringBuffer sb=new StringBuffer();
		for (RolecrewAmountBaseModel rolecrewAmount : rpamBySceneAddress.getRpabmList()) {
			sb.append("<tr>");
			sb.append("<td align='center'><div class=\"td_div\">"+rolecrewAmount.getRoleName()+"</td>");
			String actroName=" ";
			if(rolecrewAmount.getActorName()!=null)
				actroName=rolecrewAmount.getActorName();
			sb.append("<td align='center'><div class=\"td_div\">"+actroName+"</td>");
			int view=(int) rolecrewAmount.getTotalCrewAmountByView();
			sb.append("<td align='center'><div class=\"td_div\">"+view+"场/"+rolecrewAmount.getTotalCrewAmountByPage()+"页</td>");
			sb.append("<td align='center' style=\"border-right: 5px solid #ccc;\"><div class=\"td_div\">"+rolecrewAmount.getShootAddressCount()+"</td>");
			for (CrewAmountModel crewAmountModel : rolecrewAmount.getCrewAmountModelList()) {
				if(crewAmountModel!=null && crewAmountModel.getName()!=null){
					int views=(int) crewAmountModel.getcrewAmountByview();
					sb.append("<td align='center' class=\"w100\"><div class=\"td_div\">"+views+"场/"+crewAmountModel.getcrewAmountByPage()+"页</td>");
				}else {
					sb.append("<td class=\"w100\"><div class=\"td_div\"></td>");
				}
			}
			sb.append("</tr>");
		}
		map.put("viewForm", sb.toString());                       
			                           
		return map;
	}
	
	/**
	 * 演员角色按天统计
	 */
	@ResponseBody
	@RequestMapping("getViewRoleDayStatistic")
	public Map<String,Object> getViewRoleDayStatistic(HttpServletRequest request,String viewRoleName,Integer type){
		if(type==null){
			type=ReportConst.STATISTICS_TYPE_SCENE;//默认为场
		}
		@SuppressWarnings("unchecked")
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		String crewId=crewInfo.getCrewId();
		return this.roleCrewAmountReportService.getViewRoleDayStatistic(viewRoleName, crewId, type);
	}
	
	
	
	
}
