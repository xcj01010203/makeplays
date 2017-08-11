package com.xiaotu.makeplays.mobile.server.car;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.xiaotu.makeplays.car.model.CarInfoModel;
import com.xiaotu.makeplays.car.model.CarWorkModel;
import com.xiaotu.makeplays.car.service.CarInfoService;
import com.xiaotu.makeplays.car.service.CarWorkService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.mobile.server.common.BaseFacade;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 车辆
 * 
 * @author xuchangjian 2017-1-19上午9:53:50
 */
@Controller
@RequestMapping("/interface/carInfoFacade")
public class CarInfoFacade extends BaseFacade {

	Logger logger = LoggerFactory.getLogger(CarInfoFacade.class);
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private CarInfoService carInfoService;
	
	@Autowired
	private CarWorkService carWorkService;

	/**
	 * 获取车辆列表
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainCarInfoList")
	public Object obtainCarInfoList(String crewId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			// 判断用户是否有效
			MobileUtils.checkUserValid(userId);

			List<Map<String, Object>> carInfoList = this.carInfoService.queryAllCarInfo(crewId, "");
			for (Map<String, Object> carInfo : carInfoList) {
				Date enterDate = (Date) carInfo.get("enterDate");
				if (enterDate != null) {
					carInfo.put("enterDate", this.sdf1.format(enterDate));
				}
			}

			resultMap.put("carInfoList", carInfoList);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取车辆列表失败", e);
			throw new IllegalArgumentException("未知异常，获取车辆列表失败", e);
		}

		return resultMap;
	}
	
	/**
	 * 获取最新车辆编号
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainMaxCarNo")
	public Object obtainMaxCarNo(String crewId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			// 判断用户是否有效
			MobileUtils.checkUserValid(userId);

			int maxCarNo = this.carInfoService.queryMaxCarNo(crewId);

			resultMap.put("carNo", maxCarNo);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取最新车辆编号失败", e);
			throw new IllegalArgumentException("未知异常，获取最新车辆编号失败", e);
		}

		return resultMap;
	}
	
	/**
	 * 保存车辆信息
	 * @param crewId
	 * @param userId
	 * @param carId	车辆ID
	 * @param carNo	车辆编号
	 * @param carModel	车辆型号
	 * @param carNumber	车牌号
	 * @param driver	司机
	 * @param phone	电话
	 * @param userFor	用途
	 * @param status	状态：0-离组  1-在组
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveCarInfo")
	public Object saveCarInfo(HttpServletRequest request, String crewId, String userId, String carId, 
			Integer carNo, String carModel, String carNumber, 
			String driver, String phone, String useFor, Integer status, String identityNum, String enterDate, String departments) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			// 判断用户是否有效
			MobileUtils.checkUserValid(userId);

			//校验基本信息
			//车辆编号不能为空
			if(carNo == null) {
				throw new IllegalArgumentException("车辆编号不能为空");
			}
			
			if (StringUtils.isBlank(carNumber)) {
				throw new IllegalArgumentException("车牌号码，不能为空");
			}
			if (carNumber.length() > 100) {
				throw new IllegalArgumentException("车牌号码过长，请修改");
			}
			//司机
			if (!StringUtils.isBlank(driver) && driver.length() > 100) {
				throw new IllegalArgumentException("司机名称过长，请修改！");
			}
			
			//电话
			if (!StringUtils.isBlank(phone) && phone.length() > 100) {
				throw new IllegalArgumentException("电话号码过长，请修改！");
			}
			
			//车辆类型
			if (!StringUtils.isBlank(carModel) && carModel.length() > 100) {
				throw new IllegalArgumentException("车辆类型过长，请修改！");
			}
			
			//车辆用途
			if (!StringUtils.isBlank(useFor) && useFor.length() > 500) {
				throw new IllegalArgumentException("车辆用途填写过长，请修改！");
			}
			
			//部门名称
			if (StringUtils.isNotBlank(departments) && departments.length() > 100) {
				throw new IllegalArgumentException("车辆部门填写过长，请修改！");
			}
			
			//验证车辆编号是否已存在
			List<Map<String, Object>> sameCarNoCarlist = this.carInfoService.queryByCarNo(carNo, carId, crewId);
			if(sameCarNoCarlist != null && sameCarNoCarlist.size() > 0) {
				throw new IllegalArgumentException("已存在相同车辆编号的车，请检查");
			}
			//验证车牌号是否重复
			List<Map<String,Object>> carList = this.carInfoService.queryByCarNumber(carNumber, crewId, carId);
			if (carList != null && carList.size() > 0) {
				throw new IllegalArgumentException("车牌号码已存在");
			}
			
			if (status == null) {
				throw new IllegalArgumentException("请填写在组离组状态");
			}
			CarInfoModel carInfo = new CarInfoModel();
			if (!StringUtils.isBlank(carId)) {
				carInfo = this.carInfoService.queryCarInfoById(carId);
			} else {
				carInfo.setCarId(UUIDUtils.getId());
			}
			carInfo.setCarNo(carNo);
			carInfo.setDriver(driver);
			carInfo.setPhone(phone);
			carInfo.setCarModel(carModel);
			carInfo.setCarNumber(carNumber);
			carInfo.setStatus(status);
			carInfo.setCrewId(crewId);
			carInfo.setUseFor(useFor);
			carInfo.setDepartments(departments);
			carInfo.setIdentityNum(identityNum);
			if (!StringUtils.isBlank(enterDate)) {
				carInfo.setEnterDate(this.sdf1.parse(enterDate));
			}
			
			
			if (!StringUtils.isBlank(carId)) {
				this.carInfoService.updateCarInfo(carInfo);
			} else {
				this.carInfoService.addCarInfo(carInfo);
			}
			resultMap.put("carId", carInfo.getCarId());
			
			this.sysLogService.saveSysLogForApp(request, "保存车辆信息", this.getClientType(userId), CarInfoModel.TABLE_NAME, "", 1);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，保存车辆信息失败", e);
			this.sysLogService.saveSysLogForApp(request, "保存车辆信息失败：" + e.getMessage(), this.getClientType(userId), CarInfoModel.TABLE_NAME, "", SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，保存车辆信息失败", e);
		}

		return resultMap;
	}
	
	/**
	 * 获取车辆加油列表
	 * @param crewId
	 * @param userId
	 * @param carId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainCarWorkList")
	public Object obtainCarWorkList(String crewId, String userId, String carId, String workDate) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			// 判断用户是否有效
			MobileUtils.checkUserValid(userId);

			if (StringUtils.isBlank(carId)) {
				throw new IllegalArgumentException("请提供车辆ID");
			}
			
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("carId", carId);
			if (!StringUtils.isBlank(workDate)) {
				conditionMap.put("workDate", workDate);
			}
			List<CarWorkModel> carWorkList = this.carWorkService.queryManyByMutiCondition(conditionMap, null);
			
			List<Map<String, Object>> carWorkMapList = new ArrayList<Map<String, Object>>();
			
			Double totalMiles = 0.0;	//总公里数
			Double totalMoney = 0.0;	//总加油金额
			for (CarWorkModel carWork : carWorkList) {
				Double kilometers = carWork.getKilometers();
				Double oilMoney = carWork.getOilMoney();
				
				if (kilometers == null) {
					kilometers = 0.0;
				}
				if (oilMoney == null) {
					oilMoney = 0.0;
				}
				
				totalMiles = BigDecimalUtil.add(totalMiles, kilometers);
				totalMoney = BigDecimalUtil.add(totalMoney, oilMoney);
				
				Map<String, Object> carWorkMap = new HashMap<String, Object>();
				carWorkMap.put("workId", carWork.getWorkId());
				carWorkMap.put("carId", carWork.getCarId());
//				carWorkMap.put("workNo", 1);
				carWorkMap.put("workDate", this.sdf1.format(carWork.getWorkDate()));
				carWorkMap.put("mileage", carWork.getMileage());
				carWorkMap.put("kilometers", carWork.getKilometers());
				carWorkMap.put("oilLitres", carWork.getOilLitres());
				carWorkMap.put("oilMoney", carWork.getOilMoney());
				carWorkMap.put("remark", carWork.getRemark());
				carWorkMap.put("startMileage", carWork.getStartMileage());
				
				carWorkMapList.add(carWorkMap);
			}

			resultMap.put("totalMiles", totalMiles);
			resultMap.put("totalMoney", totalMoney);
			resultMap.put("carWorkList", carWorkMapList);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取加油信息失败", e);
			throw new IllegalArgumentException("未知异常，获取加油信息失败", e);
		}

		return resultMap;
	}
	
	/**
	 * 删除车辆信息
	 * @param crewId
	 * @param userId
	 * @param carId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteCarInfo")
	public Object deleteCarInfo(HttpServletRequest request, String crewId, String userId, String carId) {
		try {
			// 判断用户是否有效
			MobileUtils.checkUserValid(userId);

			this.carInfoService.deleteCarInfo(carId);
			this.sysLogService.saveSysLogForApp(request, "删除车辆信息", this.getClientType(userId), CarInfoModel.TABLE_NAME, carId, SysLogOperType.DELETE.getValue());
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，删除车辆信息失败", e);
			this.sysLogService.saveSysLogForApp(request, "删除车辆信息失败：" + e.getMessage(), this.getClientType(userId), CarInfoModel.TABLE_NAME, carId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，删除车辆信息失败", e);
		}

		return null;
	}
	
	/**
	 * 保存车辆加油信息
	 * @param crewId	剧组ID
	 * @param userId
	 * @param carId
	 * @param workId
	 * @param workDate
	 * @param mileage
	 * @param kilometers
	 * @param oilLitres
	 * @param oilMoney
	 * @param remark
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveCarWorkInfo")
	public Object saveCarWorkInfo(HttpServletRequest request, String crewId, String userId, String carId, 
			String workId, String workDate, Double mileage, 
			Double kilometers, Double oilLitres, 
			Double oilMoney, String remark, Double startMileage) {
		try {
			// 判断用户是否有效
			MobileUtils.checkUserValid(userId);

			//校验基本信息
			if (StringUtils.isBlank(carId)) {
				throw new IllegalArgumentException("请提供车辆ID");
			}
			if (!StringUtils.isBlank(remark) && remark.length() > 500) {
				throw new IllegalArgumentException("备注过长，请更正");
			}
			if (StringUtils.isBlank(workDate)) {
				throw new IllegalArgumentException("请填写加油日期");
			}
			if ((oilLitres == null && oilMoney != null) || (oilMoney == null && oilLitres != null)) {
				throw new IllegalArgumentException("加油升数和加油金额必须同时为空或同时不为空,请完善信息后再添加");
			}
			
			//判断日期是否重复
			Date myWorkDate = this.sdf1.parse(workDate);
			//根据加油日期、工作结束里程、加油金额 查询出加油记录
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("carId", carId);
			conditionMap.put("workDate", myWorkDate);
			conditionMap.put("mileage", mileage);
			conditionMap.put("oilMoney", oilMoney);
			List<CarWorkModel> existCarWorkList = this.carWorkService.queryManyByMutiCondition(conditionMap, null);
			if (StringUtils.isBlank(workId) && existCarWorkList.size() > 0) {
				throw new IllegalArgumentException("加油信息跟已有加油信息重复，请更正");
			}
			if (!StringUtils.isBlank(workId)) {
				for (CarWorkModel carWork : existCarWorkList) {
					String existCarWorkId = carWork.getWorkId();
					if (!existCarWorkId.equals(workId)) {
						throw new IllegalArgumentException("加油信息跟已有加油信息重复，请更正");
					}
				}
			}
			
			//如果里程表数、公里数、加油升数、加油金额全部为空，服务端执行删除操作
			if (!StringUtils.isBlank(workId) && mileage == null && kilometers == null && oilLitres == null && oilMoney == null && startMileage == null) {
				this.carWorkService.deleteById(workId);
			}
			if (mileage != null || kilometers != null || oilLitres != null || oilMoney != null || startMileage != null) {
				CarWorkModel carWorkInfo = new CarWorkModel();
				if (!StringUtils.isBlank(workId)) {
					carWorkInfo = this.carWorkService.queryById(workId);
				} else {
					carWorkInfo.setWorkId(UUIDUtils.getId());
				}
				
				carWorkInfo.setCarId(carId);
				carWorkInfo.setWorkDate(this.sdf1.parse(workDate));
				carWorkInfo.setStartMileage(startMileage);
				carWorkInfo.setMileage(mileage);
				carWorkInfo.setKilometers(kilometers);
				carWorkInfo.setOilLitres(oilLitres);
				carWorkInfo.setOilMoney(oilMoney);
				carWorkInfo.setCrewId(crewId);
				carWorkInfo.setRemark(remark);
				
				if (!StringUtils.isBlank(workId)) {
					this.carWorkService.updateOne(carWorkInfo);
				} else {
					this.carWorkService.addOne(carWorkInfo);
				}
			}
			
			this.sysLogService.saveSysLogForApp(request, "保存车辆加油信息", this.getClientType(userId), CarWorkModel.TABLE_NAME, "", 1);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，保存车辆加油信息失败", e);
			this.sysLogService.saveSysLogForApp(request, "保存车辆加油信息失败：" + e.getMessage(), this.getClientType(userId), CarWorkModel.TABLE_NAME, carId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，保存车辆加油信息失败", e);
		}

		return null;
	}
	
	/**
	 * 删除车辆加油信息
	 * @param crewId
	 * @param userId
	 * @param workId	加油信息ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteCarWorkInfo")
	public Object deleteCarWorkInfo(String crewId, String userId, String workId) {
		try {
			// 判断用户是否有效
			MobileUtils.checkUserValid(userId);

			if (StringUtils.isBlank(workId)) {
				throw new IllegalArgumentException("请提供加油信息ID");
			}
			this.carWorkService.deleteById(workId);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，删除车辆信息失败", e);
			throw new IllegalArgumentException("未知异常，删除车辆信息失败", e);
		}

		return null;
	}
	
	
	/**
	 * 获取加油费用列表
	 * @param crewId
	 * @param userId
	 * @param workMonth
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainOilMoneyList")
	public Object obtainOilMoneyList(String crewId, String userId, String workMonth) {

		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			// 判断用户是否有效
			MobileUtils.checkUserValid(userId);

			List<Map<String, Object>> oilMoneyList = this.carWorkService.queryMonthOilMoneyStatistic(crewId, workMonth);
			for (Map<String, Object> oilMoneyInfo : oilMoneyList) {
				Date workDate = (Date) oilMoneyInfo.get("workDate");
				oilMoneyInfo.put("workDate", this.sdf1.format(workDate));
			}
			
			resultMap.put("oilMoneyList", oilMoneyList);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取加油费用列表失败", e);
			throw new IllegalArgumentException("未知异常，获取加油费用列表失败", e);
		}

		return resultMap;
	}
	
	/**
	 * 获取所有车辆在某一天的加油信息
	 * @param crewId
	 * @param userId
	 * @param workMonth
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainDayCarWorkList")
	public Object obtainDayCarWorkList(String crewId, String userId, String workDate) {

		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			// 判断用户是否有效
			MobileUtils.checkUserValid(userId);
			
			Double totalOilMoney = 0.0;
			List<Map<String, Object>> carWorkList = this.carInfoService.queryWithDayWorkInfo(crewId, workDate);
			for (Map<String, Object> carWorkInfo : carWorkList) {
				Date myWorkDate = (Date) carWorkInfo.get("workDate");
				Double oilMoney = (Double) carWorkInfo.get("oilMoney");
				
				if (myWorkDate != null) {
					carWorkInfo.put("workDate", this.sdf1.format(myWorkDate));
				}
				if (oilMoney != null) {
					totalOilMoney = BigDecimalUtil.add(totalOilMoney, oilMoney);
				} else {
					carWorkInfo.put("oilMoney", 0.00);
				}
			}
			
			resultMap.put("totalOilMoney", totalOilMoney);
			resultMap.put("carWorkList", carWorkList);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取加油费用列表失败", e);
			throw new IllegalArgumentException("未知异常，获取加油费用列表失败", e);
		}

		return resultMap;
	}
}
