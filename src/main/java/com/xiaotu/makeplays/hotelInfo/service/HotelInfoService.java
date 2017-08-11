package com.xiaotu.makeplays.hotelInfo.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.hotelInfo.controller.filter.CheckinHotelInfoFilter;
import com.xiaotu.makeplays.hotelInfo.controller.filter.HotelInfoFilter;
import com.xiaotu.makeplays.hotelInfo.dao.CheckinHotelInfoDao;
import com.xiaotu.makeplays.hotelInfo.dao.HotelInfoDao;
import com.xiaotu.makeplays.hotelInfo.model.CheckinHotelInfoModel;
import com.xiaotu.makeplays.hotelInfo.model.HotelInfoModel;
import com.xiaotu.makeplays.inhotelcost.dao.InhotelCostInfoDao;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 宾馆操作的service
 * @author wanrenyi 2017年3月14日下午4:46:23
 */
@Service
public class HotelInfoService {

	@Autowired
	private HotelInfoDao hotelInfoDao;
	
	@Autowired
	private CheckinHotelInfoDao checkHotelDao;
	
	@Autowired
	private InhotelCostInfoDao inhotelCostInfoDao;
	
	/**
	 * 保存或更新宾馆信息及入住信息
	 * @param model
	 * @param checkInStr
	 * @throws Exception 
	 */
	public void saveHotelAndCheckInInfo(HotelInfoModel model, String checkInStr) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<CheckinHotelInfoModel> allList = new ArrayList<CheckinHotelInfoModel>(); //前台提交的所有的入住信息
		List<CheckinHotelInfoModel> addList = new ArrayList<CheckinHotelInfoModel>(); //需要新增的入住信息
		List<CheckinHotelInfoModel> updateList = new ArrayList<CheckinHotelInfoModel>(); //需要更新的入住信息
		
		//根据id判断是保存还是新建
		String hotelId = "";
		if (StringUtils.isBlank(model.getId())) {
			//新建
			hotelId = UUIDUtils.getId();
			model.setId(hotelId);
			model.setCreateTime(new Date());
			
			hotelInfoDao.add(model);
		}else {
			hotelId = model.getId();
			//根据id查询出以前的信息
			HotelInfoModel hotelModel = hotelInfoDao.queryById(model.getId());
			model.setCreateTime(hotelModel.getCreateTime());
			//修改
			hotelInfoDao.updateWithNull(model, "id");
			
			//删除入住信息
			//checkHotelDao.deleteById(model.getId());
		}
		
		//新增入住信息
		if (StringUtils.isNotBlank(checkInStr)) {
			String[] checkInfoTrs = checkInStr.split("##");
			int i = 1;
			for (String checkInfoTr : checkInfoTrs) {
				if (StringUtils.isNotBlank(checkInfoTr)) {
					String[] checkInfoArr = checkInfoTr.split(";");
					
					CheckinHotelInfoModel checkInfoModel = new CheckinHotelInfoModel();
					//宾馆id
					checkInfoModel.setHotelId(hotelId);
					
					//入住人姓名
					String peopleName = checkInfoArr[0];
					if (StringUtils.isBlank(peopleName)) {
						throw new IllegalArgumentException("第  " + i+ " 行入住人员姓名为空，请完善");
					}
					checkInfoModel.setPeopleName(peopleName);
					
					//房间号
					String roomNo = checkInfoArr[1];
					if (StringUtils.isBlank(roomNo)) {
						throw new IllegalArgumentException("第  " + i+ " 行入住房间号为空，请完善");
					}
					checkInfoModel.setRoomNo(roomNo);
					
					//分机号
					String extension = checkInfoArr[2];
					checkInfoModel.setExtension(extension);
					//房价
					String roomPriceStr = checkInfoArr[3];
					Double roomPrice = 0.0;
					if (StringUtils.isNotBlank(roomPriceStr)) {
						roomPrice = Double.parseDouble(roomPriceStr);
					}
					checkInfoModel.setRoomPrice(roomPrice);
					
					//入住时间
					String checkInDateStr = checkInfoArr[4];
					if (StringUtils.isBlank(checkInDateStr)) {
						throw new IllegalArgumentException("第  " + i+ " 行入住时间为空，请完善");
					}
					Date checkInDate = sdf.parse(checkInDateStr);
					checkInfoModel.setCheckinDate(checkInDate);
					
					//退房时间
					String checkOutDateStr = checkInfoArr[5];
					if (StringUtils.isBlank(checkOutDateStr)) {
						throw new IllegalArgumentException("第  " + i+ " 行退房时间为空，请完善");
					}
					Date checkOutDate = sdf.parse(checkOutDateStr);
					
					//判断入住时间不能晚于退房时间
					if (checkInDate.getTime() > checkOutDate.getTime()) {
						throw new IllegalArgumentException("第  " + i+ " 行入住时间不能晚于退房时间，请重新选择");
					}
					checkInfoModel.setCheckoutDate(checkOutDate);
					
					//入住天数
					String inTimes = checkInfoArr[6];
					if (StringUtils.isBlank(inTimes)) {
						throw new IllegalArgumentException("第  " + i+ " 行入住天数为空，请完善");
					}
					checkInfoModel.setInTimes(inTimes);
					
					//房间类型
					String roomType = "";
					if (checkInfoArr.length > 7) {
						roomType = checkInfoArr[7];
					}
					if (StringUtils.isBlank(roomType)) {
						checkInfoModel.setRoomType(null);
					}else {
						checkInfoModel.setRoomType(roomType);
					}
					
					//备注
					String remark = "";
					if (checkInfoArr.length > 8) {
						remark = checkInfoArr[8];
					}
					checkInfoModel.setRemark(remark);
					
					//取出入住信息id
					String checkId = "";
					if (checkInfoArr.length > 9) {
						checkId = checkInfoArr[9];
					}
					checkInfoModel.setId(checkId);
					
					//剧组id
					checkInfoModel.setCrewId(model.getCrewId());
					
					allList.add(checkInfoModel);
					//根据id判断当前数据是新增数据还是更新数据
					if (StringUtils.isNotBlank(checkId)) {
						//更新数据
						updateList.add(checkInfoModel);
					}else {
						//入住信息id
						String id = UUIDUtils.getId();
						checkInfoModel.setId(id);
						addList.add(checkInfoModel);
					}
				}
			}
		}
		
		//对入住信息进行排序
		Collections.sort(allList, new Comparator<CheckinHotelInfoModel>() {

			@Override
			public int compare(CheckinHotelInfoModel o1, CheckinHotelInfoModel o2) {
				if (o1.getCheckinDate().getTime() > o2.getCheckinDate().getTime()) {
					return 1;
				}else if (o1.getCheckinDate().getTime()== o2.getCheckinDate().getTime()) {
					return 0;
				}else {
					return -1;
				}
			}
		});
		
		//对自己添加的入住信息进行判断防止重复时间
		if (null != allList && allList.size()>0) {
			for (int j = 0; j < allList.size(); j++) {
				for (int k = allList.size()-1; k > j; k--) {
					CheckinHotelInfoModel firstModel = allList.get(j);
					CheckinHotelInfoModel secondModel = allList.get(k);
					if (firstModel.getPeopleName().equals(secondModel.getPeopleName())) {
						//当两个人的名称相同时，比较时间是否重复
						//只判断第二个对象的入住时间是否在第一个对象的入住和退房时间之间就可
						Date secondCheckInDate = secondModel.getCheckinDate();
						Date secondCheckoutDate = secondModel.getCheckoutDate();
						Date firstCheckInDate = firstModel.getCheckinDate();
						Date firstCheckOutDate = firstModel.getCheckoutDate();
						if ((secondCheckInDate.getTime()> firstCheckInDate.getTime()) && (secondCheckInDate.getTime() < firstCheckOutDate.getTime())) {
							throw new IllegalArgumentException(firstModel.getPeopleName() + " 的入住时间退房时间有重复，请重新选择");
						}else if ((secondCheckoutDate.getTime()> firstCheckInDate.getTime()) && (secondCheckoutDate.getTime() < firstCheckOutDate.getTime())) {
							throw new IllegalArgumentException(firstModel.getPeopleName() + " 的入住时间退房时间有重复，请重新选择");
						}else if ((secondCheckInDate.getTime() == firstCheckInDate.getTime()) && secondCheckoutDate.getTime() == firstCheckOutDate.getTime()) {
							throw new IllegalArgumentException(firstModel.getPeopleName() + " 的入住时间退房时间有重复，请重新选择");
						}
					}
				}
			}
			
			//将自己提交的数据通数据库中的入住信息进行比对，防止重复数据
			checkRepeartHotelInfo(model.getCrewId(), allList);
			
			//批量保存入住信息
			checkHotelDao.addBatch(addList, CheckinHotelInfoModel.class);
			
			//批量更新入住信息
			checkHotelDao.updateBatch(updateList, "id", CheckinHotelInfoModel.class);
			
			//同步数据
			inhotelCostInfoDao.syncFromHotelInfo(model.getCrewId());
		}
	}
	
	/**
	 * 根据宾馆id删除宾馆信息
	 * @param hotelId
	 * @throws Exception 
	 */
	public void deleteHotelInfo(String hotelId, String crewId) throws Exception {
		//删除宾馆信息
		this.hotelInfoDao.deleteOne(hotelId, "id", HotelInfoModel.TABLE_NAME);
		
		//删除宾馆入住信息
		this.checkHotelDao.deleteById(hotelId);
		//同步数据
		inhotelCostInfoDao.syncFromHotelInfo(crewId);
	}
	
	/**
	 * 获取宾馆信息列表
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryHotelInfoList(String crewId, HotelInfoFilter filter){
		return this.hotelInfoDao.queryHotelInfoList(crewId, filter);
	}
	
	/**
	 * 根据宾馆id，查询出宾馆的详细信息
	 * @param hotelId
	 * @return
	 * @throws Exception 
	 */
	public HotelInfoModel queryById(String hotelId) throws Exception{
		return this.hotelInfoDao.queryById(hotelId);
	}
	
	/**
	 * 根据多个条件查询宾馆信息列表
	 * @param conditionMap
	 * @return
	 */
	public List<HotelInfoModel> queryHotelListByCondition(Map<String, Object> conditionMap){
		return this.hotelInfoDao.queryByMuitiCondition(conditionMap);
	}
	
	/**
	 * 新增一条记录
	 * @param hotelInfo
	 * @throws Exception 
	 */
	public void addOne(HotelInfoModel hotelInfo) throws Exception {
		this.hotelInfoDao.add(hotelInfo);
		//同步数据
		inhotelCostInfoDao.syncFromHotelInfo(hotelInfo.getCrewId());
	}
	
	/**
	 * 更新一条记录
	 * @param hotelInfo
	 * @throws Exception 
	 */
	public void updateOne(HotelInfoModel hotelInfo) throws Exception {
		this.hotelInfoDao.updateWithNull(hotelInfo, "id");
		//同步数据
		inhotelCostInfoDao.syncFromHotelInfo(hotelInfo.getCrewId());
	}
	
	/**
	 * 根据日期查询出当天的入住情况
	 * @param date
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryHotelInfoByDate(String date, String crewId){
		return this.hotelInfoDao.queryHotelDataByDate(date, crewId);
	}
	
	/**
	 * 同步旧住宿信息数据到新表中
	 * @throws Exception 
	 */
	public void updateAnsycHotelData() throws Exception {
		//获取宾馆信息
		List<Map<String, Object>> hotelList = this.hotelInfoDao.queryAnsycHotelInfo();
		List<HotelInfoModel> addHotelList = new ArrayList<HotelInfoModel>();
		//先保存宾馆信息
		for (Map<String, Object> map : hotelList) {
			String crewId = (String) map.get("crewId");
			if (StringUtils.isNotBlank(crewId)) {
				HotelInfoModel model = new HotelInfoModel();
				model.setCrewId(crewId);
				model.setHotelName((String)map.get("hotelName"));
				model.setId(UUIDUtils.getId());
				addHotelList.add(model);
			}
		}
		
		//批量保存宾馆信息
		this.hotelInfoDao.addBatch(addHotelList, HotelInfoModel.class);
		
		//获取宾馆入住信息
		List<Map<String,Object>> checkInHotelInfoList = this.checkHotelDao.queryAnsyCheckInHotelInfo();
		for (HotelInfoModel hotelModel : addHotelList) {
			List<CheckinHotelInfoModel> addCheckInHotelList = new ArrayList<CheckinHotelInfoModel>();
			for (Map<String, Object> map : checkInHotelInfoList) {
				String contactName = (String) map.get("contactName");
				String hotelName = (String) map.get("hotelName");
				
				//先判断当前宾馆的入住信息
				if (hotelName.equals(hotelModel.getHotelName())) {
					if (StringUtils.isNotBlank(contactName)) {
						CheckinHotelInfoModel model = new CheckinHotelInfoModel();
						model.setId(UUIDUtils.getId());
						model.setCheckinDate((Date)map.get("checkInDate"));
						model.setCheckoutDate((Date)map.get("checkoutDate"));
						model.setCrewId((String)map.get("crewId"));
						model.setExtension((String)map.get("extension"));
						model.setHotelId(hotelModel.getId());
						model.setInTimes((String)map.get("inTimes"));
						model.setPeopleName(contactName);
						model.setRoomNo((String)map.get("roomNumber"));
						
						String priceStr = map.get("price")+"";
						if (StringUtils.isNotBlank(priceStr)) {
							model.setRoomPrice(Double.parseDouble(priceStr));
						}
						addCheckInHotelList.add(model);
					}
				}
			}
			
			//保存入住信息
			if (addCheckInHotelList.size()>0) {
				this.checkHotelDao.addBatch(addCheckInHotelList, CheckinHotelInfoModel.class);
			}
		}
		
	}
	
	/**
	 * 判断需要修改或则保存的数据跟数据库中的数据是否有重复
	 * @param saveData
	 */
	private void checkRepeartHotelInfo(String crewId, List<CheckinHotelInfoModel> saveData) {
		CheckinHotelInfoFilter filter = new CheckinHotelInfoFilter();
		if (saveData != null && saveData.size()>0) {
			for (CheckinHotelInfoModel saveModel : saveData) {
				String saveId = saveModel.getId()==null?"":saveModel.getId();
				//根据入住人姓名查询出入住人入住信息
				filter.setPeopleName(saveModel.getPeopleName());
				List<CheckinHotelInfoModel> list = this.checkHotelDao.queryCheckinHotelList(crewId, filter);
				if (list!= null && list.size()>0) {
					//对数据库中的入住信息根据时间进行比对，防止重复数据（此方法需过滤掉本身）
					for (CheckinHotelInfoModel dataModel : list) {
						String dataId = dataModel.getId();
						//只有id不相同时才需要比对入住及退房时间
						if (!saveId.equals(dataId)) {
							//只判断第二个对象的入住时间是否在第一个对象的入住和退房时间之间就可
							Date secondCheckInDate = saveModel.getCheckinDate();
							Date secondCheckoutDate = saveModel.getCheckoutDate();
							Date firstCheckInDate = dataModel.getCheckinDate();
							Date firstCheckOutDate = dataModel.getCheckoutDate();
							if ((secondCheckInDate.getTime()> firstCheckInDate.getTime()) && (secondCheckInDate.getTime() < firstCheckOutDate.getTime())) {
								throw new IllegalArgumentException(saveModel.getPeopleName() + " 的入住时间退房时间有重复，请重新选择");
							}else if ((secondCheckoutDate.getTime()> firstCheckInDate.getTime()) && (secondCheckoutDate.getTime() < firstCheckOutDate.getTime())) {
								throw new IllegalArgumentException(saveModel.getPeopleName() + " 的入住时间退房时间有重复，请重新选择");
							}else if ((secondCheckInDate.getTime() == firstCheckInDate.getTime()) && secondCheckoutDate.getTime() == firstCheckOutDate.getTime()) {
								throw new IllegalArgumentException(saveModel.getPeopleName() + " 的入住时间退房时间有重复，请重新选择");
							}
						}
						
					}
					
				}
			}
		}
		
	}
}
