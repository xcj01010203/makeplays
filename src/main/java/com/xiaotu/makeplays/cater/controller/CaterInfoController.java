package com.xiaotu.makeplays.cater.controller;

import java.text.SimpleDateFormat;
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

import com.xiaotu.makeplays.car.model.CarInfoModel;
import com.xiaotu.makeplays.cater.model.CaterInfoModel;
import com.xiaotu.makeplays.cater.service.CaterInfoService;
import com.xiaotu.makeplays.cater.service.CaterMoneyInfoService;
import com.xiaotu.makeplays.hotelInfo.service.HotelInfoService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.StringUtil;

/**
 * 餐饮操作的controller
 * @author wanrenyi 2017年2月21日上午9:20:42
 */
@Controller
@RequestMapping("/caterInfo")
public class CaterInfoController extends BaseController{
	
	Logger logger = LoggerFactory.getLogger(CaterInfoModel.class);
	
	@Autowired
	private CaterInfoService caterInfoService;
	
	@Autowired
	private CaterMoneyInfoService caterMoneyInfoService;
	
	@Autowired
	private HotelInfoService hotelInfoService;
	/**
	 * 跳转到餐饮列表页面
	 * @param request
	 * @return
	 */
	@RequestMapping("/toCaterListPage")
	public ModelAndView toCaterListPage(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("/caterInfo/caterListPage");
		return mv;
	}
	
	/**
	 * 保存或更新餐饮信息
	 * @param request
	 * @param caterInfo
	 * @param caterTypeStr 使用分隔符分割开的餐饮的字符串，每行数据之间以“-”分割，行内以“,”分割
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveOrUpdateCaterInfo")
	public Map<String, Object> saveOrUpdateCaterInfo(HttpServletRequest request, String caterId, String caterDate, 
			Double budget, String caterMOneyStr){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			if (StringUtils.isBlank(caterDate)) {
				throw new IllegalArgumentException("请填写就餐日期");
			}
			
			CaterInfoModel caterInfo = new CaterInfoModel();
			//获取剧组id
			String crewId = this.getCrewId(request);
			caterInfo.setCrewId(crewId);
			
			caterInfo.setBudget(budget);
			caterInfo.setCaterDate(sdf.parse(caterDate));
			caterInfo.setCaterId(caterId);
			//调用service方法
			this.caterInfoService.saveCaterInfo(caterInfo, caterMOneyStr);
			
			message = "保存成功";
			
			String logDesc = "";
			Integer operType = null;
			if(StringUtil.isBlank(caterInfo.getCaterId())) {
				logDesc = "添加餐饮及餐饮金额信息";
				operType = 1;
			} else {
				logDesc = "修改餐饮及餐饮金额";
				operType = 2;
			}
			this.sysLogService.saveSysLog(request, logDesc, Constants.TERMINAL_PC, CarInfoModel.TABLE_NAME, caterInfo.getCaterId(), operType);
		}catch(IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			
			logger.error(message);
		} catch (Exception e) {
			success = false;
			message = "未知错误，保存失败";
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "保存餐饮及餐饮金额失败：" + e.getMessage(), Constants.TERMINAL_PC, CarInfoModel.TABLE_NAME, caterId, 6);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 根据餐饮的id删除餐饮及餐饮的金额信息
	 * @param request
	 * @param caterId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteCaterInfoById")
	public Map<String, Object> deleteCaterInfoById(HttpServletRequest request, String caterId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(caterId)) {
				throw new IllegalArgumentException("请选择要删除的餐饮信息");
			}
			
			this.caterInfoService.deleteCaterInfoById(caterId);
			
			message = "删除成功";
			
			this.sysLogService.saveSysLog(request, "删除餐饮及餐饮金额信息", Constants.TERMINAL_PC, CaterInfoModel.TABLE_NAME, caterId, 3);
		}catch(IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			
			logger.error(message);
		} catch (Exception e) {
			success = false;
			message = "未知错误，删除失败";
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "删除餐饮及餐饮金额信息失败：" + e.getMessage(), Constants.TERMINAL_PC, CaterInfoModel.TABLE_NAME, caterId, 6);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 获取剧组中所有的餐饮类别的列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryCaterTypeList")
	public Map<String, Object> queryCaterTypeList(HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			String crewId = this.getCrewId(request);
			
			//调用service的方法
			List<Map<String,Object>> list = this.caterMoneyInfoService.queryCaterTypeByCrewId(crewId);
			
			resultMap.put("caterTypeList", list);
		} catch (Exception e) {
			success = false;
			message = "未知错误，查询失败";
			
			logger.error(message, e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 根据餐饮id查询餐饮及餐饮金额的详细信息
	 * @param request
	 * @param caterId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryCaterAndMoneyInfo")
	public Map<String, Object> queryCaterAndMoneyInfo(HttpServletRequest request, String caterId, String date){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			/*if (StringUtils.isBlank(caterId)) {
				throw new IllegalArgumentException("请选择要查看的就餐日期");
			}*/
			if (StringUtils.isBlank(date)) {
				throw new IllegalArgumentException("请选择就餐日期");
			}
			//调用service方法
			Map<String, Object> data = this.caterInfoService.queryCaterInfoByCaterId(caterId);
			
			String inHotelInfoStr = "";
			//查询当天的入住信息
			List<Map<String, Object>> list = this.hotelInfoService.queryHotelInfoByDate(date, crewId);
			if (list != null && list.size()>0) {
				for (Map<String, Object> tempMap : list) {
					//取出入住人数和宾馆名称
					Object peopleCount = tempMap.get("peopleCount");
					Object hotelName = tempMap.get("hotelname");
					if (peopleCount != null) {
						inHotelInfoStr += hotelName + ",入住: " + peopleCount +" 人; ";
					}
				}
			}
			resultMap.put("data", data);
			resultMap.put("inHotelInfoStr", inHotelInfoStr);
			message = "查询成功";
		}catch(IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			
			logger.error(message);
		} catch (Exception e) {
			success = false;
			message = "未知错误，查询失败";
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 获取餐饮列表数据
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryCaterInfoList")
	public Map<String, Object> queryCaterInfoList(HttpServletRequest request,Page page){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			//获取剧组id
			String crewId = this.getCrewId(request);
			
			//调用service方法
			List<Map<String, Object>> data = this.caterInfoService.queryCaterInfoList(page, crewId);
			resultMap.put("caterInfoList", data);
			message = "查询成功";
			
			this.sysLogService.saveSysLog(request, "查询餐饮列表信息", Constants.TERMINAL_PC, CaterInfoModel.TABLE_NAME, "", 0);
		} catch (Exception e) {
			success = false;
			message = "未知错误，查询失败";
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 根据餐饮金额id删除餐饮金额信息
	 * @param request
	 * @param caterMoneyId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteCaterMoneyInfo")
	public Map<String, Object> deleteCaterMoneyInfo(HttpServletRequest request, String caterMoneyId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(caterMoneyId)) {
				throw new IllegalArgumentException("请选择需要删除的餐饮金额记录信息");
			}
			
			this.caterInfoService.deleteById(caterMoneyId);
			
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，删除失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
}
