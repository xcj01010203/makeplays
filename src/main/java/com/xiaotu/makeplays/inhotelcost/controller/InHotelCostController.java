package com.xiaotu.makeplays.inhotelcost.controller;

import java.util.HashMap;
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

import com.xiaotu.makeplays.car.controller.CarInfoController;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.inhotelcost.service.InHotelCostService;
import com.xiaotu.makeplays.utils.BaseController;

/**
 * @ClassName InHotelCostController
 * @Description 住宿费用
 * @author Administrator
 * @Date 2017年1月4日 下午4:07:55
 * @version 1.0.0
 */
@Controller
@RequestMapping("inHotelCostController")
public class InHotelCostController extends BaseController{
	
	Logger logger = LoggerFactory.getLogger(CarInfoController.class);
	
	@Autowired
	private InHotelCostService inHotelCostService;
	
	/**
	 * @Description 跳转到住宿费用页面
	 * @param request
	 * @return
	 */
	/*@RequestMapping("toShowInHotelCost")
	public Object toShowInHotelCost(HttpServletRequest request){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/inHotelCost/inHotelCostInfo");
		return mv;
		
	}*/
	/**
	 * @Description 查询住宿费用信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryInHotelCostInfo")
	public Object queryInHotelCostInfo(HttpServletRequest request,String startDate, String endDate, String hotelName){
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	resultMap = inHotelCostService.queryInHotelCostInfo(crewId,startDate,endDate,hotelName);
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
        } catch(Exception e) {
            success = false;
            message = "未知异常";
        }
        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
		
	}
	/**
	 * @Description  获取下拉框数据
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryDrowData")
	public Object queryDrowData(HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	resultMap = inHotelCostService.queryDrowData(crewId);
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
        } catch(Exception e) {
            success = false;
            message = "未知异常";
        }
        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
	
	
	/**
	 * @Description 查询某一天 某个宾馆入住详情
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryInHotelCostDetailInfo")
	public Object queryInHotelCostDetailInfo(HttpServletRequest request,String hotelName,String checkInDate){
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	List<Map<String, Object>> list = inHotelCostService.queryInHotelCostDetailInfo(hotelName,checkInDate,crewId);
        	resultMap.put("inHotelCostInfoDetailList", list);
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
        } catch(Exception e) {
            success = false;
            message = "未知异常";
        }
        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
		
	}
	
	
	/**
	 * @Description 导出入住信息详情
	 * @param request
	 * @param hotelName
	 * @param startDate 查询条件-开始日期
	 * @param endDate 查询条件-结束日期
	 * @return
	 */
	@ResponseBody
	@RequestMapping("exportInHotelCostDetailInfo")
	public Object exportInHotelCostDetailInfo(HttpServletRequest request,HttpServletResponse httpServletResponse,String hotelName,String startDate,String endDate){
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	CrewInfoModel crewInfoModel = this.getSessionCrewInfo(request);
        	inHotelCostService.queryInHotelCostDetailInfoForExport(httpServletResponse,hotelName,startDate,endDate,crewInfoModel.getCrewId(),crewInfoModel.getCrewName());
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
            
            logger.error(message, ie);
        } catch(Exception e) {
            success = false;
            e.printStackTrace();
            message = "未知异常";
            
            logger.error(message, e);
        }
        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
		
	}
	
}
