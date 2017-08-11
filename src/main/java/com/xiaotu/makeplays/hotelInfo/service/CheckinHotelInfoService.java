package com.xiaotu.makeplays.hotelInfo.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.hotelInfo.controller.filter.CheckinHotelInfoFilter;
import com.xiaotu.makeplays.hotelInfo.dao.CheckinHotelInfoDao;
import com.xiaotu.makeplays.hotelInfo.model.CheckinHotelInfoModel;
import com.xiaotu.makeplays.inhotelcost.dao.InhotelCostInfoDao;

/**
 * 宾馆入住信息操作service
 * @author wanrenyi 2017年3月15日上午10:07:52
 */
@Service
public class CheckinHotelInfoService {

	@Autowired
	private CheckinHotelInfoDao checkinHotelInfoDao;
	
	@Autowired
	private InhotelCostInfoDao inhotelCostInfoDao;
	
	/**
	 * 查询当前剧组中所有的入住人员的姓名列表
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryCheckInPeopleNameList(String crewId){
		return this.checkinHotelInfoDao.queryCheckInPeopleNameList(crewId);
	}
	
	/**
	 * 根据入住id删除一条入住信息
	 * @param id
	 * @return 
	 * @throws Exception 
	 */
	public int deleteCheckInfoById(String id, String crewId) throws Exception {
		int i = checkinHotelInfoDao.deleteOne(id, "id", CheckinHotelInfoModel.TABLE_NAME);
		
		//同步数据
		inhotelCostInfoDao.syncFromHotelInfo(crewId);
		return i;
	}
	
	/**
	 * 查询入住信息列表
	 * @return
	 */
	public List<Map<String, Object>> queryCheckInfoList(String hotelId){
		return this.checkinHotelInfoDao.queryCheckIninfoByHotelId(hotelId);
	}
	
	/**
	 * 根据酒店ID查询入住登记列表
	 * @param hotelId
	 * @return
	 */
	public List<CheckinHotelInfoModel> queryByHotelId(String hotelId) {
		return this.checkinHotelInfoDao.queryByHotelId(hotelId);
	}
	
	/**
	 * 查询入住登记信息
	 * @param crewId
	 * @param filter
	 * @return
	 */
	public List<CheckinHotelInfoModel> queryCheckinHotelList (String crewId, CheckinHotelInfoFilter filter) {
		return this.checkinHotelInfoDao.queryCheckinHotelList(crewId, filter);
	}
	
	/**
	 * 根据ID查找数据
	 * @param crewId
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public CheckinHotelInfoModel queryById(String crewId, String id) throws Exception {
		return this.checkinHotelInfoDao.queryById(crewId, id);
	}
	
	/**
	 * 增加一条记录
	 * @param checkinHotelInfo
	 * @throws Exception 
	 */
	public void addOne(CheckinHotelInfoModel checkinHotelInfo) throws Exception {
		this.checkinHotelInfoDao.add(checkinHotelInfo);
		//同步数据
		this.inhotelCostInfoDao.syncFromHotelInfo(checkinHotelInfo.getCrewId());
	}
	
	/**
	 * 更新一条记录
	 * @param checkinHotelInfo
	 * @throws Exception
	 */
	public void updateOne(CheckinHotelInfoModel checkinHotelInfo) throws Exception {
		this.checkinHotelInfoDao.updateWithNull(checkinHotelInfo, "id");
		//同步数据
		this.inhotelCostInfoDao.syncFromHotelInfo(checkinHotelInfo.getCrewId());
	}
	
	/**
	 * 获取当前剧组中的所有的房间类型列表
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryRoomTypeList(String crewId){
		return this.checkinHotelInfoDao.queryRoomTypeListByCrewId(crewId);
	}
	
	/**
	 * 查询当前剧组中的所有的房间号
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryRoomNoList(String hotelId){
		return this.checkinHotelInfoDao.queryRoomNumList(hotelId);
	}
	
	/**
	 * 查询当前剧组中的分机号列表
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryExtensionList(String hotelId){
		return this.checkinHotelInfoDao.queryExtensionList(hotelId);
	}
	
	/**
	 * 根据所有的高级查询条件查询出入住详情
	 * @return
	 */
	public List<Map<String, Object>> queryCheckinInfoByAll(String hotelId, CheckinHotelInfoFilter filter){
		return this.checkinHotelInfoDao.queryCheckinInfoListByAllFilter(hotelId, filter);
	}
}
