package com.xiaotu.makeplays.car.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.car.model.CarWorkModel;
import com.xiaotu.makeplays.car.service.CarWorkService;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.model.constants.UserClientType;
import com.xiaotu.makeplays.utils.BaseController;

/**
 * @类名：CarOilMoneyController.java
 * @作者：李晓平
 * @时间：2017年4月24日 上午11:20:57
 * @描述：车辆加油费用统计
 */
@Controller
@RequestMapping("/carOilMoneyManager")
public class CarOilMoneyController extends BaseController{
	
	Logger logger = LoggerFactory.getLogger(CarOilMoneyController.class);
	
	@Autowired
	private CarWorkService carWorkSerivce;
	
	/**
     * 查询车辆加油升数和加油金额日累计信息
	 * 包括总计
     * @param request
     * @param searchDate 查询条件-日期
     * @param searchCarNumber 查询条件-车牌号
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryCarOilMoneyInfo")
    public Map<String, Object> queryCarOilMoneyInfo(HttpServletRequest request, String startDate,String endDate, String searchCarNumber){
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message = "";
    	boolean success = true;
    	
    	try {
    		String crewId = this.getCrewId(request);
			
    		resultMap = this.carWorkSerivce.queryCarOilMoneyInfo(crewId, startDate, endDate, searchCarNumber);
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，查询车辆加油升数和加油金额日累计信息失败";
			success = false;
			
			logger.error(message, e);
		}
    	
    	resultMap.put("message", message);
    	resultMap.put("success", success);
    	return resultMap;
    }
    
    /**
     * 获取下拉框数据
     * @param request
     * @return
     */
	@ResponseBody
	@RequestMapping("/queryDropDownData")
	public Object queryDrowData(HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	resultMap = carWorkSerivce.queryDropDownData(crewId);
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
            logger.error(message, ie);
        } catch(Exception e) {
            success = false;
            message = "未知异常，获取下拉框数据异常";
            logger.error(message, e);
        }
        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
	
	/**
	 * 导出车辆加油升数和费用日统计信息
	 * @param request
	 * @param response
	 * @param searchDate
	 * @param searchCarNumber
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exportCarOilMoneyInfo")
	public Map<String, Object> exportCarOilMoneyInfo(HttpServletRequest request, HttpServletResponse response, String startDate, String endDate, String searchCarNumber){
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
        	
        	//项目基本信息
            Map<String, String> columnMap = new LinkedHashMap<String, String>();
            columnMap.put("日期", "workDate");
            columnMap.put("车牌号", "carNumber");
            columnMap.put("加油升数", "totalLiters");
            columnMap.put("加油金额", "totalMoney");
            
            this.carWorkSerivce.queryCarOilMoneyForExport(response, startDate, endDate, searchCarNumber, crewInfo, columnMap);
            
        	this.sysLogService.saveSysLog(request, "导出车辆加油升数和费用日统计信息", UserClientType.PC.getValue(), CarWorkModel.TABLE_NAME, "", SysLogOperType.EXPORT.getValue());
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
            logger.error(message, ie);
        } catch(Exception e) {
            success = false;
            message = "未知异常,导出车辆加油升数和费用日统计信息异常";
            logger.error(message, e);
        	this.sysLogService.saveSysLog(request, "导出车辆加油升数和费用日统计信息失败：" + e.getMessage(), UserClientType.PC.getValue(), CarWorkModel.TABLE_NAME, "", SysLogOperType.ERROR.getValue());
        }
        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;		
	}
}
