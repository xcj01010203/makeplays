package com.xiaotu.makeplays.hotelInfo.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.hotelInfo.controller.filter.CheckinHotelInfoFilter;
import com.xiaotu.makeplays.hotelInfo.controller.filter.HotelInfoFilter;
import com.xiaotu.makeplays.hotelInfo.model.CheckinHotelInfoModel;
import com.xiaotu.makeplays.hotelInfo.model.HotelInfoModel;
import com.xiaotu.makeplays.hotelInfo.service.CheckinHotelInfoService;
import com.xiaotu.makeplays.hotelInfo.service.HotelInfoService;
import com.xiaotu.makeplays.inhotelcost.service.InHotelCostService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.service.CrewContactService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.RegexUtils;
import com.xiaotu.makeplays.utils.StringUtil;

/**
 * 宾馆信息管理的controller
 * @author wanrenyi 2017年3月14日上午11:17:43
 */
@Controller
@RequestMapping("/hotelManager")
public class HotelInfoController extends BaseController{
	
	Logger logger = LoggerFactory.getLogger(HotelInfoController.class);
	
	private final int terminal = Constants.TERMINAL_PC;

	@Autowired
	private HotelInfoService hotelInfoService;
	
	@Autowired
	private CheckinHotelInfoService checkinHotelInfoService;
	
	@Autowired
	private CrewContactService crewContactService;
	
	@Autowired
	private InHotelCostService inhotelCostService;
	
	
	/**
	 * 跳转到宾馆列表界面
	 * @param request
	 * @return
	 */
	@RequestMapping("/toHotelListPage")
	public ModelAndView toHotelListPage(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("/hotelInfo/hotelListPage");
		
		return mv;
	}
	
	/**
	 * 跳转到入住详情页面
	 * @param request
	 * @return
	 */
	@RequestMapping("/toCheckInHotelPage")
	public ModelAndView toCheckInHotelPage(HttpServletRequest request, String hotelId) {
		ModelAndView mv = new ModelAndView("/hotelInfo/checkInHotelInfo");
		mv.addObject("hotelId",hotelId);
		
		return mv;
	}
	
	/**
	 * 保存或更新宾馆及宾馆的入住信息
	 * @param request
	 * @param id 宾馆id，为空表示新增，不为空表示修改
	 * @param hotelName 宾馆名称
	 * @param hotelAddress 宾馆地址
	 * @param vLongitude 经度
	 * @param vLatitude 维度
	 * @param hotelPhone 宾馆电话
	 * @param roomNumber 宾馆房间数
	 * @param contactPeople 宾馆联系人
	 * @param contactPhone 联系人电话
	 * @param priceRemark 报价说明
	 * @param checkIninfoStr 入住信息字符串（每行字段之间以“**”分割，行与行之间以“##” 分割）
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveHotelAndCheckInInfo")
	public Map<String, Object> saveHotelAndCheckInInfo(HttpServletRequest request, String id, String hotelName, String hotelAddress, 
			String vLongitude, String vLatitude, String hotelPhone, String roomNumber, String contactPeople,String contactPhone, 
			String priceRemark, String checkIninfoStr){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			
			//对数据进行校验
			if (StringUtils.isBlank(hotelName)) {
				throw new IllegalArgumentException("请输入宾馆名称");
			}
			if (hotelName.length()>100) {
				throw new IllegalArgumentException("宾馆名称不能超过100个字，请修改后再保存");
			}
			if (StringUtils.isNotBlank(hotelAddress)) {
				if (hotelAddress.length()>500) {
					throw new IllegalArgumentException("宾馆地址不能超过500个字，请修改后再保存");
				}
			}
			if (StringUtils.isNotBlank(hotelPhone)) {
				if (hotelPhone.length()>50) {
					throw new IllegalArgumentException("宾馆联系电话不能超过50个字，请修改后再保存");
				}
			}
			if (StringUtils.isNotBlank(contactPeople)) {
				if (contactPeople.length()> 20) {
					throw new IllegalArgumentException("联系人名称不能超过20个字，请修改后再保存");
				}
			}
			if (StringUtils.isNotBlank(contactPhone)) {
				if (contactPhone.length()>50) {
					throw new IllegalArgumentException("联系人电话不能超过50个字，请修改后再保存");
				}
			}
			if (StringUtils.isNotBlank(priceRemark)) {
				if (priceRemark.length()>500) {
					throw new IllegalArgumentException("报价说明不能超过500字，请修改后再保存");
				}
			}
			if (StringUtils.isNotBlank(roomNumber) && roomNumber.length() >8) {
				throw new IllegalArgumentException("房间数不能超过8个数字，请修改后在保存");
			}
			
			//根据宾馆名称判断是否已经存在该宾馆
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("crewId", crewId);
			conditionMap.put("hotelName", hotelName);
			List<HotelInfoModel> list = this.hotelInfoService.queryHotelListByCondition(conditionMap);
			if (null != list && list.size()>0) {
				for (HotelInfoModel model : list) {
					String modelId = model.getId();
					if (!modelId.equals(id)) {
						throw new IllegalArgumentException(hotelName +" 已经保存，请不要重复保存");
					}
				}
			}
			
			HotelInfoModel hotelModel = new HotelInfoModel();
			hotelModel.setContactPeople(contactPeople);
			hotelModel.setContactPhone(contactPhone);
			hotelModel.setCrewId(crewId);
			hotelModel.setHotelAddress(hotelAddress);
			hotelModel.setHotelName(hotelName);
			hotelModel.setHotelPhone(hotelPhone);
			hotelModel.setId(id);
			hotelModel.setPriceRemark(priceRemark);
			
			if (StringUtils.isNotBlank(roomNumber)) {
				hotelModel.setRoomNumber(Integer.parseInt(roomNumber));
			}
			hotelModel.setLatitude(vLatitude);
			hotelModel.setLongitude(vLongitude);
			
			hotelInfoService.saveHotelAndCheckInInfo(hotelModel, checkIninfoStr);
			
			message = "保存成功";
			String logDesc = "";
			Integer operType = null;
			if(StringUtil.isBlank(id)) {
				logDesc = "添加宾馆信息及宾馆入住信息";
				operType = 1;
			} else {
				logDesc = "修改宾馆信息及宾馆入住信息";
				operType = 2;
			}
			this.sysLogService.saveSysLog(request, logDesc, Constants.TERMINAL_PC, HotelInfoModel.TABLE_NAME, id, operType);
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，保存失败";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "保存宾馆信息及宾馆入住信息失败：" + e.getMessage(), Constants.TERMINAL_PC, HotelInfoModel.TABLE_NAME, id, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 查询当前剧组所有的入住人员姓名列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryPeopleNameList")
	public Map<String, Object> queryPeopleNameList(HttpServletRequest request, String hotelId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			List<String> resultNameList = new ArrayList<String>();
			
			//查询宾馆入住信息中的联系人名称
			List<Map<String,Object>> list = this.checkinHotelInfoService.queryCheckInPeopleNameList(crewId);
			for (Map<String,Object>  map : list) {
				String propleName = (String) map.get("peopleName");
				if (StringUtils.isNotBlank(propleName)) {
					resultNameList.add(propleName);
				}
			}
			//查询出剧组联系中的姓名
			List<Map<String,Object>> contactNameList = this.crewContactService.queryCrewContactName(crewId);
			for (Map<String, Object> map : contactNameList) {
				String contactName = (String) map.get("contactName");
				if (StringUtils.isNotBlank(contactName)) {
					if (!resultNameList.contains(contactName)) {
						resultNameList.add(contactName);
					}
				}
			}
			
			resultMap.put("nameList", resultNameList);
			
			//获取入住信息中的房间号的列表
			List<Map<String,Object>> roomNoList = this.checkinHotelInfoService.queryRoomNoList(hotelId);
			resultMap.put("roomNoList", roomNoList);
			
			//分机号
			List<Map<String,Object>> extensionList = this.checkinHotelInfoService.queryExtensionList(hotelId);
			resultMap.put("extensionList", extensionList);
		} catch (Exception e) {
			message = "未知异常，查询失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 删除宾馆及宾馆入住信息
	 * @param request
	 * @param hotelId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteHotelInfo")
	public Map<String, Object> deleteHotelInfo(HttpServletRequest request, String hotelId){
		Map<String, Object> resultMap  = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(hotelId)) {
				throw new IllegalArgumentException("请选择需要删除的宾馆");
			}
			
			String crewId = this.getCrewId(request);
			this.hotelInfoService.deleteHotelInfo(hotelId, crewId);
			message = "删除成功";
			
			this.sysLogService.saveSysLog(request, "删除宾馆及宾馆入住信息", Constants.TERMINAL_PC, HotelInfoModel.TABLE_NAME, hotelId, 3);
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，删除失败";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "删除宾馆及宾馆入住信息失败：" + e.getMessage(), Constants.TERMINAL_PC, HotelInfoModel.TABLE_NAME, hotelId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 删除一条入住信息
	 * @param request
	 * @param checkinId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteCheckIninfo")
	public Map<String, Object> deleteCheckIninfo(HttpServletRequest request, String checkinId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			if (StringUtils.isBlank(checkinId)) {
				throw new IllegalArgumentException("请选择需要删除的入住信息");
			}
			
			String crewId = this.getCrewId(request);
			
			this.checkinHotelInfoService.deleteCheckInfoById(checkinId, crewId);
			message = "删除成功";
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，删除失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 获取宾馆信息列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryHotelInfoList")
	public Map<String, Object> queryHotelInfoList(HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			String crewId = this.getCrewId(request);
			
			HotelInfoFilter filter = new HotelInfoFilter();
			List<Map<String,Object>> list = this.hotelInfoService.queryHotelInfoList(crewId, filter);
			for (Map<String, Object> map : list) {
				//对时间进行格式化
				String checkInDate = "";
				String checkOutDate = "";
				if (null != map.get("minCheckInDate")) {
					checkInDate = sdf.format((Date)map.get("minCheckInDate"));
				}
				if (null != map.get("maxCheckOutDate")) {
					checkOutDate = sdf.format((Date)map.get("maxCheckOutDate"));
				}
				
				map.remove("minCheckInDate");
				map.remove("maxCheckOutDate");
				map.put("checkInDate", checkInDate);
				map.put("checkOutDate", checkOutDate);
				
				//计算总费用
				Map<String, Object> inhotelCost = this.inhotelCostService.queryInHotelCostInfo(crewId, null, null, (String)map.get("hotelName"));
				String totalCost = (String) inhotelCost.get("sumCost");
				map.put("totalMoney", totalCost);
			}
			
			resultMap.put("hotleList", list);
			message = "查询成功";
		} catch (Exception e) {
			message = "未知异常，查询失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 获取宾馆及宾馆入住的详细信息
	 * @param request
	 * @param hotelId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryHotelAndCheckIninfo")
	public Map<String, Object> queryHotelAndCheckIninfo(HttpServletRequest request, String hotelId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			if (StringUtils.isBlank(hotelId)) {
				throw new IllegalArgumentException("请选择要查看的宾馆");
			}
			
			//获取宾馆信息
			HotelInfoModel hotelModel = this.hotelInfoService.queryById(hotelId);
			//对数据进行排序，分成数字个包含字符两种
			String regex = "^([^0-9])+";
			//纯数字集合
			List<Map<String, Object>> intMap = new ArrayList<Map<String,Object>>();
			//包含字符的集合
			List<Map<String, Object>> charMap = new ArrayList<Map<String,Object>>();
			//获取宾馆的入住信息
			List<Map<String, Object>> list = this.checkinHotelInfoService.queryCheckInfoList(hotelId);
			for (Map<String, Object> map : list) {
				String checkInDateStr = "";
				String checkOutDateStr = "";
				
				if (null != map.get("checkInDate")) {
					checkInDateStr = sdf.format((Date)map.get("checkInDate"));
				}
				
				if (null != map.get("checkoutDate")) {
					checkOutDateStr = sdf.format((Date)map.get("checkoutDate"));
				}
				map.put("checkInDate", checkInDateStr);
				map.put("checkoutDate", checkOutDateStr);
				
				//取出房间号
				String roomNo = (String) map.get("roomNo");
				
				if (RegexUtils.regexFind(regex, roomNo)) {
					//含有字符的房间号
					charMap.add(map);
				}else {
					intMap.add(map);
				}
			}
			
			//对数字集合进行排序
			Collections.sort(intMap, new Comparator<Map<String, Object>>() {

				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					int firstNo = Integer.parseInt((String)o1.get("roomNo"));
					int secondNo = Integer.parseInt((String)o2.get("roomNo"));
					return firstNo - secondNo;
				}
			});
			
			//将数据添加到需要返回的集合中
			List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
			//先添加数字集合，在添加字符集合
			for (Map<String, Object> map : intMap) {
				resultList.add(map);
			}
			for (Map<String, Object> map : charMap) {
				resultList.add(map);
			}
			
			resultMap.put("hotelModel", hotelModel);
			resultMap.put("checkInList", resultList);
			message = "查询成功";
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，查询失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 同步宾馆数据
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ansycHotelInfo")
	public Map<String, Object> ansycHotelInfo(HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			
			this.hotelInfoService.updateAnsycHotelData();
			
			message = "同步成功";
		} catch (Exception e) {
			message = "未知错误，同步失败";
			success = false;
			                                                        
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 查询出当前剧组中所有的房间类型
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryRoomTypeList")
	public Map<String, Object> queryRoomTypeList(HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = this.getCrewId(request);
			
			//调动service方法
			List<Map<String,Object>> typeList = this.checkinHotelInfoService.queryRoomTypeList(crewId);
			resultMap.put("roomTypeList", typeList);
			
			message = "查询成功";
		} catch (Exception e) {
			message = "未知错误，查询失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 根据条件查询当前宾馆的入住详情
	 * @param request
	 * @param hotelId
	 * @param filter
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryCheckinInfoList")
	public Map<String, Object> queryCheckinInfoList(HttpServletRequest request, CheckinHotelInfoFilter filter){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			if (StringUtils.isBlank(filter.getHotelId())) {
				throw new IllegalArgumentException("请选择要查询的酒店");
			}
			
			List<Map<String, Object>> searchList = this.checkinHotelInfoService.queryCheckinInfoByAll(filter.getHotelId(), filter);
			//对日期进行啊
			for (Map<String, Object> map : searchList) {
				String checkInDateStr = "";
				String checkOutDateStr = "";
				
				if (null != map.get("checkInDate")) {
					checkInDateStr = sdf.format((Date)map.get("checkInDate"));
				}
				
				if (null != map.get("checkoutDate")) {
					checkOutDateStr = sdf.format((Date)map.get("checkoutDate"));
				}
				map.put("checkInDate", checkInDateStr);
				map.put("checkoutDate", checkOutDateStr);
				
			}
			
			resultMap.put("checkInfoList", searchList);
			message = "查询成功";
			
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，查询失败";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
}
