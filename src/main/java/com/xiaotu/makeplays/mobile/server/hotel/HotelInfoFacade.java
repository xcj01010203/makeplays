package com.xiaotu.makeplays.mobile.server.hotel;

import java.text.CollationKey;
import java.text.Collator;
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

import com.xiaotu.makeplays.hotelInfo.controller.filter.CheckinHotelInfoFilter;
import com.xiaotu.makeplays.hotelInfo.controller.filter.HotelInfoFilter;
import com.xiaotu.makeplays.hotelInfo.model.CheckinHotelInfoModel;
import com.xiaotu.makeplays.hotelInfo.model.HotelInfoModel;
import com.xiaotu.makeplays.hotelInfo.service.CheckinHotelInfoService;
import com.xiaotu.makeplays.hotelInfo.service.HotelInfoService;
import com.xiaotu.makeplays.inhotelcost.service.InHotelCostService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.mobile.server.common.BaseFacade;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.service.CrewContactService;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 住宿信息
 * @author xuchangjian 2017-3-17下午5:07:46
 */
@Controller
@RequestMapping("/interface/hotelInfoFacade")
public class HotelInfoFacade extends BaseFacade {
	
	Logger logger = LoggerFactory.getLogger(HotelInfoFacade.class);
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private HotelInfoService hotelInfoService;
	
	@Autowired
	private CheckinHotelInfoService checkinHotelInfoService;
	
	@Autowired
	private CrewContactService crewContactService;
	
	@Autowired
	private InHotelCostService inhotelCostService;

	/**
	 * 获取住宿列表
	 * @param crewId
	 * @param userId
	 * @param peopleName	入住人名
	 * @param hotelNames	酒店名
	 * @param startDate	开始日期
	 * @param endDate	结束日期
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainHotelList")
	public Object obtainHotelList(String crewId, String userId, String peopleName, String hotelNames, String startDate, String endDate) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			HotelInfoFilter filter = new HotelInfoFilter();
			filter.setPeopleName(peopleName);
			filter.setHotelNames(hotelNames);
			filter.setStartDate(startDate);
			filter.setEndDate(endDate);
			List<Map<String, Object>> hotelList = this.hotelInfoService.queryHotelInfoList(crewId, filter);
			for (Map<String, Object> hotelInfo : hotelList) {
				Date minCheckInDate = (Date) hotelInfo.get("minCheckInDate");
				Date maxCheckOutDate = (Date) hotelInfo.get("maxCheckOutDate");
				
				String checkinDate = "";
				String checkoutDate = "";
				if (minCheckInDate != null) {
					checkinDate = this.sdf1.format(minCheckInDate);
				}
				if (maxCheckOutDate != null) {
					checkoutDate = this.sdf1.format(maxCheckOutDate);
				}
				hotelInfo.remove("minCheckInDate");
				hotelInfo.remove("maxCheckOutDate");
				hotelInfo.put("checkinDate", checkinDate);
				hotelInfo.put("checkoutDate", checkoutDate);
				
				//计算总费用
				Map<String, Object> inhotelCost = this.inhotelCostService.queryInHotelCostInfo(crewId, null, null, (String)hotelInfo.get("hotelName"));
				String totalCost = (String) inhotelCost.get("sumCost");
				hotelInfo.put("totalMoney", totalCost);
			}
			
			resultMap.put("hotelList", hotelList);
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取住宿列表失败", e);
			throw new IllegalArgumentException("未知异常，获取住宿列表失败", e);
		}
		return resultMap;
	}
	
	/**
	 * 获取酒店详细信息
	 * @param crewId
	 * @param userId
	 * @param hotelId	酒店ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainHotelDetailInfo")
	public Object obtainHotelDetailInfo(String crewId, String userId, String hotelId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(hotelId)) {
				throw new IllegalArgumentException("请提供酒店ID");
			}
			
			HotelInfoModel hotelInfo = this.hotelInfoService.queryById(hotelId);
			List<CheckinHotelInfoModel> checkinHotelInfoList = this.checkinHotelInfoService.queryByHotelId(hotelId);
			
			List<String> checkinPeopleNameList = new ArrayList<String>();
			List<Map<String, Object>> checkinMapList = new ArrayList<Map<String, Object>>();
			for (CheckinHotelInfoModel checkinHotelInfo : checkinHotelInfoList) {
				Map<String, Object> checkinMapInfo = new HashMap<String, Object>();
				
				//判断人数
				String peopleName = checkinHotelInfo.getPeopleName();
				if (StringUtils.isNotBlank(peopleName)) {
					if (!checkinPeopleNameList.contains(peopleName)) {
						checkinPeopleNameList.add(peopleName);
					}
				}
				
				checkinMapInfo.put("id", checkinHotelInfo.getId());
				checkinMapInfo.put("peopleName", checkinHotelInfo.getPeopleName());
				checkinMapInfo.put("roomNo", checkinHotelInfo.getRoomNo());
				checkinMapInfo.put("extension", checkinHotelInfo.getExtension());
				checkinMapInfo.put("roomPrice", checkinHotelInfo.getRoomPrice());
				checkinMapInfo.put("checkinDate", this.sdf1.format(checkinHotelInfo.getCheckinDate()));
				checkinMapInfo.put("checkoutDate", this.sdf1.format(checkinHotelInfo.getCheckoutDate()));
				checkinMapInfo.put("inTimes", checkinHotelInfo.getInTimes());
				checkinMapInfo.put("remark", checkinHotelInfo.getRemark());
				checkinMapInfo.put("roomType", checkinHotelInfo.getRoomType());
				
				checkinMapList.add(checkinMapInfo);
			}
			
			resultMap.put("hotelName", hotelInfo.getHotelName());
			resultMap.put("hotelAddress", hotelInfo.getHotelAddress());
			resultMap.put("longitude", hotelInfo.getLongitude());
			resultMap.put("latitude", hotelInfo.getLatitude());
			resultMap.put("hotelPhone", hotelInfo.getHotelPhone());
			resultMap.put("roomNumber", hotelInfo.getRoomNumber());
			resultMap.put("priceRemark", hotelInfo.getPriceRemark());
			resultMap.put("contactPeople", hotelInfo.getContactPeople());
			resultMap.put("contactPhone", hotelInfo.getContactPhone());
			
			resultMap.put("checkinPeopleCount", checkinPeopleNameList.size());
			resultMap.put("checkinList", checkinMapList);
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取酒店详细信息失败", e);
			throw new IllegalArgumentException("未知异常，获取酒店详细信息失败", e);
		}
		return resultMap;
	}
	
	/**
	 * 保存酒店详细信息
	 * @param crewId
	 * @param userId
	 * @param hotelId	酒店ID
	 * @param hotelName	酒店名称
	 * @param hotelAddress	地址
	 * @param longitude	经度
	 * @param latitude	纬度
	 * @param hotelPhone	酒店电话
	 * @param roomNumber	房间数
	 * @param priceRemark	报价说明
	 * @param contactPeople	联系人
	 * @param contactPhone	联系电话
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveHotelInfo")
	public Object saveHotelInfo(HttpServletRequest request, String crewId, String userId, String hotelId, String hotelName, 
			String hotelAddress, String longitude, String latitude, 
			String hotelPhone, Integer roomNumber, String priceRemark, 
			String contactPeople, String contactPhone) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			if (StringUtils.isBlank(hotelName)) {
				throw new IllegalArgumentException("请填写酒店名称");
			}
			if (hotelName.length() > 100) {
				throw new IllegalArgumentException("酒店名称最多支持100个汉字");
			}
			if (StringUtils.isNotBlank(hotelAddress) && hotelAddress.length() > 500) {
				throw new IllegalArgumentException("酒店地址最多支持500个汉字");
			}
			if (StringUtils.isNotBlank(longitude) && longitude.length() > 50) {
				throw new IllegalArgumentException("经度最大长度为50");
			}
			if (StringUtils.isNotBlank(latitude) && latitude.length() > 50) {
				throw new IllegalArgumentException("纬度最大长度为50");
			}
			if (StringUtils.isNotBlank(hotelPhone) && hotelPhone.length()> 50) {
				throw new IllegalArgumentException("酒店电话最多支持填写50个字符");
			}
			if (StringUtils.isNotBlank(priceRemark) && priceRemark.length() > 500) {
				throw new IllegalArgumentException("报价说明最多支持500个汉字");
			}
			if (StringUtils.isNotBlank(contactPeople) && contactPeople.length() > 20) {
				throw new IllegalArgumentException("联系人最多支持20个汉字");
			}
			if (StringUtils.isNotBlank(contactPhone) && contactPhone.length() > 50) {
				throw new IllegalArgumentException("联系电话最多支持50个字符");
			}
			
			//根据宾馆名称判断是否已经存在该宾馆
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("crewId", crewId);
			conditionMap.put("hotelName", hotelName);
			List<HotelInfoModel> existNameHotelList = this.hotelInfoService.queryHotelListByCondition(conditionMap);
			if (existNameHotelList != null && existNameHotelList.size() > 0) {
				for (HotelInfoModel hotelInfo : existNameHotelList) {
					String myId = hotelInfo.getId();
					if (!myId.equals(hotelId)) {
						throw new IllegalArgumentException(hotelName +" 已经保存，请不要重复保存");
					}
				}
			}
			
			HotelInfoModel hotelInfo = new HotelInfoModel();
			if (!StringUtils.isBlank(hotelId)) {
				hotelInfo = this.hotelInfoService.queryById(hotelId);
			} else {
				hotelInfo.setId(UUIDUtils.getId());
				hotelInfo.setCreateTime(new Date());
			}
			hotelInfo.setHotelName(hotelName);
			hotelInfo.setHotelAddress(hotelAddress);
			hotelInfo.setLongitude(longitude);
			hotelInfo.setLatitude(latitude);
			hotelInfo.setHotelPhone(hotelPhone);
			hotelInfo.setRoomNumber(roomNumber);
			hotelInfo.setContactPeople(contactPeople);
			hotelInfo.setContactPhone(contactPhone);
			hotelInfo.setPriceRemark(priceRemark);
			hotelInfo.setCrewId(crewId);
			
			if (!StringUtils.isBlank(hotelId)) {
				this.hotelInfoService.updateOne(hotelInfo);
			} else {
				this.hotelInfoService.addOne(hotelInfo);
			}
			
			resultMap.put("hotelId", hotelInfo.getId());
			
			this.sysLogService.saveSysLogForApp(request, "保存酒店信息", this.getClientType(userId), HotelInfoModel.TABLE_NAME, "", 1);
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，保存酒店信息失败", e);
			this.sysLogService.saveSysLogForApp(request, "保存酒店信息失败：" + e.getMessage(), this.getClientType(userId), HotelInfoModel.TABLE_NAME, "", SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，保存酒店信息失败", e);
		}
		return resultMap;
	}
	
	/**
	 * 删除酒店信息
	 * @param crewId
	 * @param userId
	 * @param hotelId	酒店ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteHotelInfo")
	public Object deleteHotelInfo(HttpServletRequest request, String crewId, String userId, String hotelId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(hotelId)) {
				throw new IllegalArgumentException("请提供酒店ID");
			}
			
			this.hotelInfoService.deleteHotelInfo(hotelId, crewId);
			this.sysLogService.saveSysLogForApp(request, "删除酒店信息", this.getClientType(userId), HotelInfoModel.TABLE_NAME, "", 3);
			
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，删除酒店信息失败", e);
			this.sysLogService.saveSysLogForApp(request, "删除酒店信息失败：" + e.getMessage(), this.getClientType(userId), HotelInfoModel.TABLE_NAME, "", SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，删除酒店信息失败", e);
		}
		return resultMap;
	}
	
	/**
	 * 保存酒店信息
	 * @param request
	 * @param crewId
	 * @param userId
	 * @param hotelId	酒店ID
	 * @param id	入住登记ID
	 * @param peopleName	入住人姓名
	 * @param roomNo	房间号
	 * @param extension	分机号
	 * @param roomPrice	房价
	 * @param checkInDate	入住时间
	 * @param checkoutDate	退房时间
	 * @param inTimes	入住天数
	 * @param remark	备注
	 * @return	
	 */
	@ResponseBody
	@RequestMapping("/saveCheckinHotelInfo")
	public Object saveCheckinHotelInfo(HttpServletRequest request, String crewId, String userId, String hotelId,
			String id, String peopleName, String roomNo, String extension, Double roomPrice, String checkinDate, 
			String checkoutDate, String inTimes, String remark, String roomType) {
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(hotelId)) {
				throw new IllegalArgumentException("请提供酒店ID");
			}
			if (StringUtils.isBlank(peopleName)) {
				throw new IllegalArgumentException("请填写入住人姓名");
			}
			if (StringUtils.isBlank(roomNo)) {
				throw new IllegalArgumentException("请填写房间号");
			}
			if (StringUtils.isBlank(checkinDate)) {
				throw new IllegalArgumentException("请填写入住时间");
			}
			if (StringUtils.isBlank(checkoutDate)) {
				throw new IllegalArgumentException("请填写退房时间");
			}
			if (StringUtils.isBlank(inTimes)) {
				throw new IllegalArgumentException("请提供入住天数信息");
			}
			
			if (peopleName.length() > 15) {
				throw new IllegalArgumentException("入住人姓名最多支持15个汉字");
			}
			if (roomNo.length() > 20) {
				throw new IllegalArgumentException("房间号最多支持20个汉字");
			}
			if (!StringUtils.isBlank(extension) && extension.length() > 20) {
				throw new IllegalArgumentException("分机号最多支持20个字符");
			}
			if (inTimes.length() > 10) {
				throw new IllegalArgumentException("入住天数最多支持10个字符");
			}
			if (!StringUtils.isBlank(remark) && remark.length() > 100) {
				throw new IllegalArgumentException("备注最多支持100个汉字");
			}
			if (this.sdf1.parse(checkinDate).after(this.sdf1.parse(checkoutDate))) {
				throw new IllegalArgumentException("退房时间不能晚于入住时间");
			}
			if (StringUtils.isNotBlank(roomType) && roomType.length() > 50) {
				throw new IllegalArgumentException("房间类型最多支持50个汉字");
			}
			
			//校验该入住人在此酒店的入住时间是否有重复
			CheckinHotelInfoFilter filter = new CheckinHotelInfoFilter();
			filter.setPeopleName(peopleName);
			filter.setCheckinDate(checkinDate);
			filter.setCheckoutDate(checkoutDate);
			filter.setHotelId(hotelId);
			List<CheckinHotelInfoModel> existCheckinHotelList = this.checkinHotelInfoService.queryCheckinHotelList(crewId, filter);
			if (existCheckinHotelList != null && existCheckinHotelList.size() > 0) {
				if (StringUtils.isBlank(id) || !existCheckinHotelList.get(0).getId().equals(id)) {
					throw new IllegalArgumentException(peopleName + "的住房时间和他已有的住房时间重复");
				}
			}
			
			CheckinHotelInfoModel checkinHotelInfo = new CheckinHotelInfoModel();
			if (!StringUtils.isBlank(id)) {
				checkinHotelInfo = this.checkinHotelInfoService.queryById(crewId, id);
			} else {
				checkinHotelInfo.setId(UUIDUtils.getId());
			}
			checkinHotelInfo.setPeopleName(peopleName);
			checkinHotelInfo.setRoomNo(roomNo);
			checkinHotelInfo.setExtension(extension);
			checkinHotelInfo.setRoomPrice(roomPrice);
			checkinHotelInfo.setCheckinDate(this.sdf1.parse(checkinDate));
			checkinHotelInfo.setCheckoutDate(this.sdf1.parse(checkoutDate));
			checkinHotelInfo.setInTimes(inTimes);
			checkinHotelInfo.setRemark(remark);
			checkinHotelInfo.setCrewId(crewId);
			checkinHotelInfo.setHotelId(hotelId);
			checkinHotelInfo.setRoomType(roomType);
			
			if (!StringUtils.isBlank(id)) {
				this.checkinHotelInfoService.updateOne(checkinHotelInfo);
			} else {
				this.checkinHotelInfoService.addOne(checkinHotelInfo);
			}
			
			this.sysLogService.saveSysLogForApp(request, "保存入住登记信息", this.getClientType(userId), CheckinHotelInfoModel.TABLE_NAME, "", 2);
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，保存入住信息失败", e);
			this.sysLogService.saveSysLogForApp(request, "保存入住登记信息失败：" + e.getMessage(), this.getClientType(userId), CheckinHotelInfoModel.TABLE_NAME, "", SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，保存入住信息失败", e);
		}
		return null;
	}
	
	/**
	 * 删除入住登记信息
	 * @param crewId
	 * @param userId
	 * @param hotelId	酒店ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteCheckinHotelInfo")
	public Object deleteCheckinHotelInfo(HttpServletRequest request, String crewId, String userId, String id) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(id)) {
				throw new IllegalArgumentException("请提供入住信息ID");
			}
			
			this.checkinHotelInfoService.deleteCheckInfoById(id, crewId);
			this.sysLogService.saveSysLogForApp(request, "删除入住信息", this.getClientType(userId), CheckinHotelInfoModel.TABLE_NAME, "", 3);
			
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，删除入住信息失败", e);
			this.sysLogService.saveSysLogForApp(request, "删除入住信息失败：" + e.getMessage(), this.getClientType(userId), CheckinHotelInfoModel.TABLE_NAME, "", SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，删除入住信息失败", e);
		}
		return resultMap;
	}
	
	/**
	 * 获取宾馆列表
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainHotelNameList")
	public Object obtainHotelNameList(String crewId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("crewId", crewId);
			List<HotelInfoModel> hotelInfoList = this.hotelInfoService.queryHotelListByCondition(conditionMap);
			List<String> hotelNameList = new ArrayList<String>();
			for (HotelInfoModel hotelInfo : hotelInfoList) {
				String hotelName = hotelInfo.getHotelName();
				if (!hotelNameList.contains(hotelName)) {
					hotelNameList.add(hotelName);
				}
			}
			
			resultMap.put("hotelNameList", hotelNameList);
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取宾馆列表失败", e);
			throw new IllegalArgumentException("未知异常，获取宾馆列表失败", e);
		}
		return resultMap;
	}
	
	/**
	 * 获取下拉数据
	 * 入住人员名称备选数据（联系表中联系人、已添加的住宿人）
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainDropDownData")
	public Object obtainDropDownData(String crewId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			List<Map<String, Object>> nameList = new ArrayList<Map<String, Object>>();
			List<String> contactNameList = new ArrayList<String>();
			
			//获取剧组联系表中的联系人信息
			List<Map<String, Object>> contactList = this.crewContactService.queryContactListByAdvanceCondition(crewId, null, null);
			for (Map<String, Object> contactInfo : contactList) {
				String contactName = (String) contactInfo.get("contactName");
				String sysRoleNames = (String) contactInfo.get("sysRoleNames");
				
				Map<String, Object> nameInfoMap = new HashMap<String, Object>();
				if (!StringUtils.isBlank(contactName) && !contactNameList.contains(contactName)) {
					contactNameList.add(contactName);
					nameInfoMap.put("name", contactName);
					nameInfoMap.put("position", sysRoleNames);
					nameInfoMap.put("fletter", contactInfo.get("fletter"));
					
					nameList.add(nameInfoMap);
				}
			}
			
			//获取宾馆入住信息中的人员信息名称
			List<Map<String,Object>> checkInPeopleNameList = this.checkinHotelInfoService.queryCheckInPeopleNameList(crewId);
			
			for (Map<String, Object> hotelNameInfo : checkInPeopleNameList) {
				String contactName = (String) hotelNameInfo.get("peopleName");
				
				Map<String, Object> nameInfoMap = new HashMap<String, Object>();
				if (!StringUtils.isBlank(contactName) && !contactNameList.contains(contactName)) {
					contactNameList.add(contactName);
					nameInfoMap.put("name", contactName);
					nameInfoMap.put("position", "");
					nameInfoMap.put("fletter", hotelNameInfo.get("fletter"));
					
					nameList.add(nameInfoMap);
				}
			}
			
			Collections.sort(nameList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					String fletter1 = (String) o1.get("fletter");
					String fletter2 = (String) o2.get("fletter");
					
					CollationKey key1 = Collator.getInstance().getCollationKey(fletter1.toLowerCase());
	        		CollationKey key2 = Collator.getInstance().getCollationKey(fletter2.toLowerCase());
					
	        		return key1.compareTo(key2);
				}
			});
			
			//获取房间类型，下拉列表数据
			List<Map<String,Object>> roomTypeList = this.checkinHotelInfoService.queryRoomTypeList(crewId);
			
			resultMap.put("roomTypeList", roomTypeList);
			resultMap.put("toSelectPeopleList", nameList);
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取数据失败", e);
			throw new IllegalArgumentException("未知异常，获取数据失败", e);
		}
		return resultMap;
	}
	
	/**
	 * 获取当前剧组中的 房间类型
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainRoomTypeList")
	public Object obtainRoomTypeList(String crewId, String userId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			//获取房间类型，下拉列表数据
			List<Map<String,Object>> roomTypeList = this.checkinHotelInfoService.queryRoomTypeList(crewId);
			
			resultMap.put("roomTypeList", roomTypeList);
		}catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取数据失败", e);
			throw new IllegalArgumentException("未知异常，获取数据失败", e);
		}
		
		return resultMap;
	}
}
