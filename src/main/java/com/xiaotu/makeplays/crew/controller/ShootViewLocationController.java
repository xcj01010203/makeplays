package com.xiaotu.makeplays.crew.controller;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.model.ShootLocationModel;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.crew.service.ShootViewLocationService;
import com.xiaotu.makeplays.utils.BaseController;


@Deprecated
@Controller
@RequestMapping("shootViewLocation")
public class ShootViewLocationController extends BaseController {
	@Autowired
	private CrewInfoService crewInfoService;
	
	@Autowired
	private ShootViewLocationService shootViewLocationService;
	/**
	 * lma 拍摄地点统计查询
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("locationForm")
	public @ResponseBody  ModelAndView locationForm(HttpServletRequest request) throws Exception{
		
		ModelAndView view = new ModelAndView("reportForm/locationForm");
		//获取剧组ID
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		String crewId=crewInfo.getCrewId();
		//分别按集、拍摄地点、拍摄地点下的主场景，统计角色的戏量
		List<ShootLocationModel> location=null;
		//拍摄地点集合
		List<String> locationList = new ArrayList<String>();
		try {
			location = shootViewLocationService.getShootLocationByCrewId(crewId);
			if (location != null && location.size() > 0) {
				for (ShootLocationModel shootLocation : location) {
					locationList.add(shootLocation.getShootLocation());
				}
			}
			view.addObject("locationList", locationList);	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		view.addObject("location", location);
		
//		this.sysLogService.saveSysLog(request, "拍摄地点统计", Constants.TERMINAL_PC, null, null,0);

		return view;
	}
	
}
