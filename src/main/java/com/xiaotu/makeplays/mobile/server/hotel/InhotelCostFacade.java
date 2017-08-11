package com.xiaotu.makeplays.mobile.server.hotel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.inhotelcost.controller.dto.InhotelCostDateDto;
import com.xiaotu.makeplays.inhotelcost.controller.dto.InhotelCostDto;
import com.xiaotu.makeplays.inhotelcost.controller.filter.InHotelCostFilter;
import com.xiaotu.makeplays.inhotelcost.model.InhotelCostModel;
import com.xiaotu.makeplays.inhotelcost.service.InHotelCostService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.Page;

/**
 * 入住费用相关接口
 * @author xuchangjian 2017-2-16下午4:58:33
 */
@Controller
@RequestMapping("/interface/inhotelCostFacade")
public class InhotelCostFacade {
	
	Logger logger = LoggerFactory.getLogger(InhotelCostFacade.class);
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private InHotelCostService inhotelCostService;
	
	/**
	 * 获取住宿费用列表接口
	 * @param request
	 * @param crewId
	 * @param userId
	 * @param hotelNames
	 * @param startDate
	 * @param endDate
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainInhotelCostList")
	public Object obtainInhotelCostList(HttpServletRequest request, String crewId, String userId, 
			String hotelNames, String startDate, 
			String endDate, Integer pageNo, Integer pageSize) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			if (pageNo == null) {
				pageNo = 1;
			}
			if (pageSize == null) {
				pageSize = 20;
			}
			
			Page page = new Page();
			page.setPageNo(pageNo);
			page.setPagesize(pageSize);
			
			InHotelCostFilter filter = new InHotelCostFilter();
			filter.setHotelNames(hotelNames);
			filter.setStartDate(startDate);
			filter.setEndDate(endDate);
			
			//带入分页条件查询入住日期
			List<Map<String, Object>> checkinDateList = this.inhotelCostService.queryDistinctShowDateList(crewId, filter, page);

			//计算出日期中的最大、最小日期
			List<String> myCheckinDateList = new ArrayList<String>();
			Date myStartDate = new Date();
			Date myEndDate = new Date();
			for (Map<String, Object> checkinDateMap : checkinDateList) {
				String checkinDate = this.sdf1.format((Date) checkinDateMap.get("showdate"));
				myCheckinDateList.add(checkinDate);
				
				Date myCheckinDate = this.sdf1.parse(checkinDate);
				if (myCheckinDate.before(myStartDate)) {
					myStartDate = myCheckinDate;
				}
				if (myCheckinDate.after(myStartDate)) {
					myEndDate = myCheckinDate;
				}
			}
			filter.setStartDate(this.sdf1.format(myStartDate));
			filter.setEndDate(this.sdf1.format(myEndDate));
			
			//查询日期范围内的住宿费用列表
			List<InhotelCostModel> inhotelCostList = this.inhotelCostService.queryByAdvanceCondition(crewId, filter);
			
			//封装住宿费用信息，其中key为入住日期，value为宾馆列表
			Map<String, List<String>> inhotelCostInfo = new HashMap<String, List<String>>();
			for (InhotelCostModel inhotelCost : inhotelCostList) {
				Date showDate = inhotelCost.getShowDate();
				String hotelName = inhotelCost.getHotelName();
				String showDateStr = this.sdf1.format(showDate);
				
				if (inhotelCostInfo.containsKey(showDateStr)) {
					List<String> myHotelList = inhotelCostInfo.get(showDateStr);
					if (!myHotelList.contains(hotelName)) {
						myHotelList.add(hotelName);
					}
				} else {
					List<String> myHotelList = new ArrayList<String>();
					myHotelList.add(hotelName);
					inhotelCostInfo.put(showDateStr, myHotelList);
				}
			}
			
			/*
			 * 封装住宿费用数据到InhotelCostDateDto中
			 */
			List<InhotelCostDateDto> inhotelCostDateDtoList = new ArrayList<InhotelCostDateDto>();
			Set<String> inhotelCostDateSet = inhotelCostInfo.keySet();
			for (String inhotelCostDate : inhotelCostDateSet) {
				List<String> myHotelList = inhotelCostInfo.get(inhotelCostDate);
				
				InhotelCostDateDto checkinDateDto = new InhotelCostDateDto();
				checkinDateDto.setCheckinDate(inhotelCostDate);
				
				List<InhotelCostDto> inhotelCostInfoList = new ArrayList<InhotelCostDto>();	//该日期下的宾馆列表
				
				for (String myHotelName : myHotelList) {
					
					InhotelCostDto inhotelCostDto = new InhotelCostDto();
					inhotelCostDto.setHotelName(myHotelName);
					
//					List<String> roomNumberList = new ArrayList<String>();	//校验房间号是否重复用，如果重复，说明两个人住一间房
					Map<String, Double> roomPriceMap = new HashMap<String, Double>();	//校验房间号是否重复用，如果重复，说明两个人住一间房,key为房间号，value为房间价格
					for (int i = 0; i < inhotelCostList.size(); i++) {
						InhotelCostModel inhotelCost = inhotelCostList.get(i);
						
						String hotelName = inhotelCost.getHotelName();
						String checkinDate = "";
						if (null != inhotelCost.getShowDate()) {
							checkinDate = this.sdf1.format(inhotelCost.getShowDate());
						}
						String roomNumber = inhotelCost.getRoomNumber();
						
						double price = 0.0;
						if (inhotelCost.getPrice() != null) {
							price = inhotelCost.getPrice();
						}
						
						if (hotelName.equals(myHotelName) && checkinDate.equals(inhotelCostDate)) {
//							if (!roomNumberList.contains(inhotelCost.getRoomNumber())) {
//								inhotelCostDto.setRoomNum(inhotelCostDto.getRoomNum() + 1);
//								inhotelCostDto.setTotalPrice(BigDecimalUtil.add(price, inhotelCostDto.getTotalPrice()));
//								
//								roomNumberList.add(inhotelCost.getRoomNumber());
//							}
							
							if (!roomPriceMap.containsKey(roomNumber)) {
								inhotelCostDto.setRoomNum(inhotelCostDto.getRoomNum() + 1);
								inhotelCostDto.setTotalPrice(BigDecimalUtil.add(price, inhotelCostDto.getTotalPrice()));
								roomPriceMap.put(roomNumber, price);
							} else if (roomPriceMap.get(roomNumber) < price) {
								//更换为房价更高的价格
								inhotelCostDto.setTotalPrice(BigDecimalUtil.add(price, BigDecimalUtil.subtract(inhotelCostDto.getTotalPrice(), roomPriceMap.get(roomNumber))));
								roomPriceMap.put(roomNumber, price);
							}
							
							inhotelCostDto.setPeopleNum(inhotelCostDto.getPeopleNum() + 1);
						}
						
						//计算平均房价
						if (i == inhotelCostList.size() - 1) {
							inhotelCostDto.setAvgPrice(BigDecimalUtil.divide(inhotelCostDto.getTotalPrice(), inhotelCostDto.getRoomNum()));
						}
					}
					
					inhotelCostInfoList.add(inhotelCostDto);
				}
				
				//计算这一天的总费用
				double totalCost = 0.0;
				for (InhotelCostDto inhotelCostDto : inhotelCostInfoList) {
					totalCost = BigDecimalUtil.add(totalCost, inhotelCostDto.getTotalPrice());
				}
				checkinDateDto.setTotalCost(totalCost);
				checkinDateDto.setInhotelCostInfoList(inhotelCostInfoList);
				
				inhotelCostDateDtoList.add(checkinDateDto);
			}
			
			//按照日期进行升序排序
			Collections.sort(inhotelCostDateDtoList, new Comparator<InhotelCostDateDto>() {

				@Override
				public int compare(InhotelCostDateDto o1, InhotelCostDateDto o2) {
					try {
						Date o1CheckinDate = sdf1.parse(o1.getCheckinDate());
						Date o2CheckinDate = sdf1.parse(o2.getCheckinDate());
						return o1CheckinDate.compareTo(o2CheckinDate);
					} catch (ParseException e) {
						logger.error("日期转换失败", e);
					}
					return 0;
				}
			});
			
			
			resultMap.put("pageCount", page.getPageCount());
			resultMap.put("inhotelCostList", inhotelCostDateDtoList);
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取住宿费用列表失败", e);
			throw new IllegalArgumentException("未知异常，获取住宿费用列表失败", e);
		}
		return resultMap;
	}
	
	/**
	 * 获取宾馆里一天的入住人员列表
	 * @param crewId
	 * @param userId
	 * @param checkinDate
	 * @param hotelName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainInhotelPeopleList")
	public Object obtainInhotelPeopleList(String crewId, String userId, String checkinDate, String hotelName) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			List<Map<String, Object>> roomNumberList = this.inhotelCostService.queryInHotelCostDetailInfo(hotelName, checkinDate, crewId);
			
			InHotelCostFilter filter = new InHotelCostFilter();
			filter.setHotelNames(hotelName);
			filter.setShowDates(checkinDate);
			List<InhotelCostModel> inhotelCostList = this.inhotelCostService.queryByAdvanceCondition(crewId, filter);
			
			resultMap.put("hotelName", hotelName);
			resultMap.put("peopleNum", inhotelCostList.size());
			resultMap.put("roomNum", roomNumberList.size());
			resultMap.put("peopleList", roomNumberList);
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取住宿人员列表失败", e);
			throw new IllegalArgumentException("未知异常，获取住宿人员失败", e);
		}
		return resultMap;
	}
	
	/**
	 * 获取宾馆列表
	 * 被HotelInfoFacade中obtainHotelNameList接口取代
	 * 废弃时间：20170318
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@Deprecated
	@ResponseBody
	@RequestMapping("/obtainHotelNameList")
	public Object obtainHotelNameList(String crewId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			//查询出宾馆名称
//			List<Map<String, Object>> inhotelInfoList = this.inhotelService.queryHotleNameList(crewId);
			
			List<String> hotelNameList = new ArrayList<String>();
//			for (Map<String, Object> inhotelNameMap : inhotelInfoList) {
//				String hotelName = (String) inhotelNameMap.get("hotelName");
//				if (!hotelNameList.contains(hotelName)) {
//					hotelNameList.add(hotelName);
//				}
//			}
			
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
	
}
