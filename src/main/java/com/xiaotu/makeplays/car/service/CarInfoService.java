package com.xiaotu.makeplays.car.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.car.dao.CarInfoDao;
import com.xiaotu.makeplays.car.dao.CarWorkDao;
import com.xiaotu.makeplays.car.model.CarInfoModel;
import com.xiaotu.makeplays.car.model.CarWorkModel;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * @类名：CarInfoService.java
 * @作者：李晓平
 * @时间：2016年12月19日 下午7:17:25
 * @描述：车辆管理service
 */
@Service
public class CarInfoService {
	
	@Autowired
	private CarInfoDao carInfoDao;
	
	@Autowired
	private CarWorkDao carWorkDao;
	
	/**
	 * 查询所有的车辆信息
	 * @return
	 */
	public List<Map<String, Object>> queryAllCarInfo(String crewId, String searchCarNo) {
		return this.carInfoDao.queryAllCarInfo(crewId, searchCarNo);
	}
		
	/**
	 * 查询单个车辆信息
	 * @param carId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> queryById(String carId) throws Exception {
		return this.carInfoDao.queryById(carId);
	}
	
	/**
	 * 根据ID查询车辆信息
	 * @param carId
	 * @return
	 * @throws Exception 
	 */
	public CarInfoModel queryCarInfoById(String carId) throws Exception {
		return this.carInfoDao.queryCarInfoById(carId);
	}
	
	/**
     * 保存车辆信息
     * @param carId	车辆ID
     * @param carNo	车辆编号
     * @param driver	司机
     * @param userFor	用途
     * @param phone	电话
     * @param carModel	车辆型号
     * @param carNumber	车牌号
     * @param status	状态：0：离组，1：在组
     * @param identityNum	身份证号码
     * @param enterDate	入组日期
     * @param carWorkStr	加油信息
     * @return
     */
	public void saveCarInfo(String crewId, String carId, Integer carNo, String driver, String useFor,
			String phone, String carModel, String carNumber, 
			Integer status, String identityNum, String enterDate, String carWorkStr, String departments) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		CarInfoModel carInfo = new CarInfoModel();
		if (StringUtils.isBlank(carId)) {
			carInfo.setCarId(UUIDUtils.getId());
		} else {
			carInfo = this.carInfoDao.queryCarInfoById(carId);
		}
		carInfo.setCarNo(carNo);
		carInfo.setDriver(driver);
		carInfo.setPhone(phone);
		carInfo.setCarModel(carModel);
		carInfo.setCarNumber(carNumber);
		carInfo.setStatus(status);
		carInfo.setCrewId(crewId);
		carInfo.setUseFor(useFor);
		carInfo.setIdentityNum(identityNum);
		carInfo.setDepartments(departments);
		if (!StringUtils.isBlank(enterDate)) {
			carInfo.setEnterDate(sdf.parse(enterDate));
		}
		if (StringUtils.isBlank(carId)) {
			this.carInfoDao.add(carInfo);
		} else {
			this.carInfoDao.updateWithNull(carInfo, "carId");
			//删除加油登记信息
			//this.carWorkDao.deleteOne(carId, "carId", CarWorkModel.TABLE_NAME);
		}
		
		//保存加油登记表
		if(StringUtil.isNotBlank(carWorkStr)) {
			String[] carWorkStrs = carWorkStr.split("##");
			if(carWorkStrs != null && carWorkStrs.length > 0) {
				List<CarWorkModel> toAllCarWorkList = new ArrayList<CarWorkModel>();//所有数据
				List<CarWorkModel> addcarWorkList = new ArrayList<CarWorkModel>(); //新增数据
				List<CarWorkModel> updateCarWorkList = new ArrayList<CarWorkModel>(); //更新数据
				List<String> repeartStringList = new ArrayList<String>();
				
				for(String oneCarWorkStr : carWorkStrs) {
					String[] carWorks = oneCarWorkStr.split(",", -1);
					CarWorkModel carWork = new CarWorkModel();
					carWork.setCarId(carInfo.getCarId());
					carWork.setCrewId(crewId);
					//加油日期
					carWork.setWorkDate(sdf.parse(carWorks[0]));
					//开工里程表数
					if(StringUtil.isNotBlank(carWorks[1])) {
						carWork.setStartMileage(Double.parseDouble(carWorks[1]));
					}
					//工作结束里程
					if(StringUtil.isNotBlank(carWorks[2])) {
						carWork.setMileage(Double.parseDouble(carWorks[2]));
					}
					
					//公里数
					if(StringUtil.isNotBlank(carWorks[3])) {
						carWork.setKilometers(Double.parseDouble(carWorks[3]));
					}
					
					//加油升数
					if(StringUtil.isNotBlank(carWorks[4])) {
						carWork.setOilLitres(Double.parseDouble(carWorks[4]));
					}
					
					//加油金额
					if(StringUtil.isNotBlank(carWorks[5])) {
						carWork.setOilMoney(Double.parseDouble(carWorks[5]));
					}
					
					//备注
					if (StringUtils.isNotBlank(carWorks[6])) {
						if (carWorks[5].length()>500) {
							throw new IllegalArgumentException("备注填写过长，请修改！");
						}
						carWork.setRemark(carWorks[6]);
					}
					//加油信息id
					String carWorkId = "";
					if (carWorks.length > 7) {
						carWorkId = carWorks[7];
					}
					if (StringUtils.isNotBlank(carWorkId)) {
						carWork.setWorkId(carWorkId);
						updateCarWorkList.add(carWork);
					}else {
						String wrokId = UUIDUtils.getId();
						carWork.setWorkId(wrokId);
						addcarWorkList.add(carWork);
					}
					
					toAllCarWorkList.add(carWork);
					
					//将日期添加到list中判断是否有重复数据(加油日期+公里数+加油金额)
					String myRepeatFlagStr = carWorks[0]+""+carWorks[3]+""+carWorks[5];
					if (repeartStringList.contains(myRepeatFlagStr)) {
						throw new IllegalArgumentException("加油信息有重复，请将重复数据合并后在添加！");
					} else {
						repeartStringList.add(myRepeatFlagStr);
					}
				}
				
				//将需要保存的数据跟数据库中的数据进行比对，确保不会重复
				checkCarRepeartWorkInfo(carId, toAllCarWorkList);
				
				//需要保存的数据
				this.carWorkDao.addBatch(addcarWorkList, CarWorkModel.class);
				
				//需要更新的数据
				this.carWorkDao.updateBatch(updateCarWorkList, "workId", CarWorkModel.class);
			}
		}
	}
	
	/**
	 * 新增车辆信息
	 * @param carInfo
	 * @throws Exception
	 */
	public void addCarInfo(CarInfoModel carInfo) throws Exception {
		this.carInfoDao.add(carInfo);
	}
	
	/**
	 * 修改车辆信息
	 * @param carInfo
	 * @throws Exception
	 */
	public void updateCarInfo(CarInfoModel carInfo) throws Exception {
		this.carInfoDao.updateWithNull(carInfo, "carId");
	}
	
	/**
	 * 查询最大的车辆编号
	 * @return
	 * @throws Exception
	 */
	public int queryMaxCarNo(String crewId) throws Exception {
		return this.carInfoDao.queryMaxCarNo(crewId);
	}
	
	/**
	 * 根据车辆编号查询车辆
	 * @param carNo
	 * @param carId
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryByCarNo(int carNo, String carId, String crewId) {
		return this.carInfoDao.queryByCarNo(carNo, carId, crewId);
	}
	
	/**
	 * 删除车辆信息
	 * @param carId
	 * @throws Exception 
	 */
	public void deleteCarInfo(String carId) throws Exception {
		this.carInfoDao.deleteOne(carId, "carId", CarInfoModel.TABLE_NAME);
		this.carWorkDao.deleteOne(carId, "carId", CarWorkModel.TABLE_NAME);
	}
	
	/**
	 * 根据车牌号查找车辆信息
	 * @param carNumber
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryByCarNumber(String carNumber, String crewId, String carId){
		return this.carInfoDao.queryByCarNumber(carNumber, crewId, carId);
	}
	
	/**
	 * 保存导入的车辆信息
	 * @param isCover
	 * @param isRepeat
	 * @param carNo
	 * @param driver
	 * @param phone
	 * @param carModel
	 * @param carNumber
	 * @param crewId
	 * @param status
	 * @throws Exception 
	 */
	public void saveImportCarDetail(boolean isCover, boolean isRepeat, String departments, Integer carNo, String driver, String phone, 
			String carModel, String carNumber, String crewId, Integer status, String useFor) throws Exception {
		
		if (isRepeat) { // 重复数据
			//判断用户选择的是否是覆盖
			if (isCover) {
				//根据车牌号查询出重复数据
				List<Map<String,Object>> list = this.carInfoDao.queryByCarNumber(carNumber, crewId, "");
				
				Map<String, Object> map = list.get(0);
				CarInfoModel model = new CarInfoModel();
				model.setCarId((String)map.get("carId"));
				model.setCarNo((Integer)map.get("carNo"));
				model.setDepartments(departments);
				model.setDriver(driver);
				model.setPhone(phone);
				model.setCarModel(carModel);
				model.setCarNumber(carNumber);
				model.setStatus(status);
				model.setCrewId(crewId);
				model.setUseFor(useFor);
				
				//更新数据
				this.carInfoDao.updateWithNull(model, "carId");
			}
		}else {
			//新增数据
			CarInfoModel model = new CarInfoModel();
			String carId = UUIDUtils.getId();
			model.setCarId(carId);
			model.setCarModel(carModel);
			model.setCarNo(carNo);
			model.setCarNumber(carNumber);
			model.setDepartments(departments);
			model.setCrewId(crewId);
			model.setDriver(driver);
			model.setPhone(phone);
			model.setStatus(status);
			model.setUseFor(useFor);
			
			//添加数据
			this.carInfoDao.add(model);
		}
	}
	
	/**
	 * 保存导入的车辆加油信息
	 * @param isCover
	 * @param isRepeat
	 * @param workNo
	 * @param mileage
	 * @param workDate
	 * @param kilometers
	 * @param oilLitres
	 * @param oilMoney
	 * @param crewId
	 * @throws Exception 
	 */
	public void saveImportCarWorkInfo(boolean isCover, boolean isRepeat,Double startMileage, Double mileage, Date workDate, 
			Double kilometers, Double oilLitres, Double oilMoney, String crewId, String carId, String remark) throws Exception {
		
		if (isRepeat) {//重复数据
			//判断用户选择的是否是替换
			if (isCover) { //替换
				//根据加油日期、工作结束里程、加油金额 查询出加油记录
				Map<String, Object> conditionMap = new HashMap<String, Object>();
				conditionMap.put("workDate", workDate);
				conditionMap.put("mileage", mileage);
				conditionMap.put("oilMoney", oilMoney);
				conditionMap.put("carId", carId);
				List<CarWorkModel> list = this.carWorkDao.queryManyByMutiCondition(conditionMap, null);
				CarWorkModel carWork = list.get(0);
				CarWorkModel model = new CarWorkModel();
				model.setCarId(carId);
				model.setWorkId(carWork.getWorkId());
				model.setCrewId(crewId);
				/*if (kilometers == null) {
					model.setKilometers((Double)map.get("kilometers"));
				}else {
					model.setKilometers(kilometers);
				}*/
				model.setStartMileage(startMileage);
				model.setKilometers(kilometers);
				model.setMileage(mileage);
				model.setOilLitres(oilLitres);
				model.setOilMoney(oilMoney);
				model.setWorkDate(workDate);
				model.setRemark(remark);
				
				this.carWorkDao.updateWithNull(model, "workId");
			}
		}else { //新增数据
			CarWorkModel model = new CarWorkModel();
			String workId = UUIDUtils.getId();
			model.setCarId(carId);
			model.setCrewId(crewId);
			model.setKilometers(kilometers);
			model.setStartMileage(startMileage);
			model.setMileage(mileage);
			model.setOilLitres(oilLitres);
			model.setOilMoney(oilMoney);
			model.setWorkDate(workDate);
			model.setWorkId(workId);
			model.setRemark(remark);
			
			this.carWorkDao.add(model);
		}
	}
	
	/**
	 * 查询剧组中所有车辆列表，带有该车辆在指定日的加油信息
	 * @param crewId
	 * @param workDate
	 * @return 车辆基本信息，车辆加油信息
	 */
	public List<Map<String, Object>> queryWithDayWorkInfo(String crewId, String workDate) {
		return this.carInfoDao.queryWithDayWorkInfo(crewId, workDate);
	}
	
	/**
	 * 查询剧组中的部门列表
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryCrewDepartmentInfo(String crewId){
		return this.carInfoDao.queryDepartmentInfo(crewId);
	}
	
	/**
	 * 根据部门名称查询部门信息
	 * @param departmentName
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryDeparmentsByName(String departmentName, String crewId){
		return this.carInfoDao.queryDepartmentByName(departmentName, crewId);
	}
	
	/**
	 * 更新车辆序号
	 * @param carIds
	 */
	public void updateCarSequence(String carIds) {
		String[] carIdsArr = carIds.split(",");
		for(int i =0; i<carIdsArr.length; i++) {
			this.carInfoDao.updateCarSequence(i+1, carIdsArr[i]);
		}
	}
	
	/**
	 * 将需要保存或更新的车辆数据与数据库中的数据进行比对，防止重复数据
	 * @param carId
	 * @param saveList
	 */
	private void checkCarRepeartWorkInfo(String carId, List<CarWorkModel> saveList) {
		//根据车辆id查询出当前车辆的加油信息
		List<Map<String, Object>> dataList = this.carWorkDao.queryCarWorkByCarId(carId);
		if (dataList != null && dataList.size()>0) {
			if (saveList != null && saveList.size() >0) {
				for (int i =0; i < saveList.size(); i++) {
					//根据 加油日期，加油金额，公里数 判断是否是重复数据
					CarWorkModel saveModel = saveList.get(i);
					for (int j = 0; j < dataList.size(); j++) {
						Map<String, Object> dataModel = dataList.get(j);
						Date dataDate = (Date) dataModel.get("workDate");
						double dataOilMoney = dataModel.get("oilMoney")==null?0.0:(Double)dataModel.get("oilMoney");
						double dataKilometers = dataModel.get("kilometers")==null?0.0:(Double)dataModel.get("kilometers");
						
						if (saveModel.getWorkDate() == dataDate && saveModel.getOilMoney() == dataOilMoney && 
								saveModel.getKilometers() == dataKilometers && !saveModel.getWorkId().equals(dataModel.get("workId"))) {
							throw new IllegalArgumentException("加油信息有重复，请将重复数据合并后在添加！");
						}
					}
				}
			}
			
		}
	}
}
