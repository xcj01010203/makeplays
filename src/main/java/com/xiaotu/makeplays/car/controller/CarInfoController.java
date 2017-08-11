package com.xiaotu.makeplays.car.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.car.model.CarInfoModel;
import com.xiaotu.makeplays.car.model.CarWorkModel;
import com.xiaotu.makeplays.car.service.CarInfoService;
import com.xiaotu.makeplays.car.service.CarWorkService;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.ExcelUtils;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.view.service.ViewInfoService;

/**
 * @类名：CarInfoController.java
 * @作者：李晓平
 * @时间：2016年12月19日 下午7:07:14
 * @描述：车辆管理
 */
@Controller
@RequestMapping("/carManager")
public class CarInfoController extends BaseController {
	
	Logger logger = LoggerFactory.getLogger(CarInfoController.class);
	
	private final int terminal = Constants.TERMINAL_PC;
	
	@Autowired
	private CarInfoService carInfoService;
	
	@Autowired
	private CarWorkService carWorkSerivce;
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	/**
     * 跳转到车辆管理页面
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/toCarListPage")
    public ModelAndView toCarListPage(HttpServletRequest request){
    	ModelAndView view = new ModelAndView("car/carList");		
		return view;
    }
    
    /**
	 * 跳转到车辆明细页面
	 * @return
	 */
	@RequestMapping("/toCarDetailListPage")
	public ModelAndView toCarDetailListPage(String carId) {
		ModelAndView mv = new ModelAndView("car/carDetailList");
		mv.addObject("aimCarId", carId);
		return mv;
	}
    
    /**
     * 查询所有的车辆信息
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryAllCarInfo")
    public Map<String, Object> queryAllCarInfo(HttpServletRequest request, String searchCarNo){
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	
    	try {
    		String crewId = this.getCrewId(request);
			resultMap.put("result", this.carInfoService.queryAllCarInfo(crewId, searchCarNo));
		} catch (Exception e) {
			message = "未知错误，查询所有的车辆信息失败";
			success = false;
			
			logger.error(message, e);
		}
    	
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
    }
    
    /**
     * 查询单个车辆信息
     * @param request
     * @param carId
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryOneCarInfo")
    public Map<String, Object> queryOneCarInfo(HttpServletRequest request, String carId){
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	
    	try {
			resultMap.put("carInfo", this.carInfoService.queryById(carId));
			resultMap.put("carWorks", this.carWorkSerivce.queryCarWorkByCarId(carId));
		} catch (Exception e) {
			message = "未知错误，查询单个车辆信息失败";
			success = false;
			
			logger.error(message, e);
		}
    	
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
    }
    
    /**
     * 导出车辆信息列表数据
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/exportCarList")
    public Object exportCarList(HttpServletRequest request, HttpServletResponse response, String searchCarNo){
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message = "";
    	boolean success = true;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	
    	//车辆信息对应的字段
    	Map<String, String> carInfoColoum = new LinkedHashMap<String, String>();
    	carInfoColoum.put("车辆编号", "carNo");
    	carInfoColoum.put("部门", "departments");
    	carInfoColoum.put("用途", "useFor");
    	carInfoColoum.put("车牌号码", "carNumber");
    	carInfoColoum.put("车辆类型", "carModel");
    	carInfoColoum.put("电话", "phone");
    	carInfoColoum.put("司机", "driver");
    	carInfoColoum.put("是否在组", "status");
    	carInfoColoum.put("累计油费(元)", "totalMoney");
    	carInfoColoum.put("累计里程(KM)", "totalMiles");
    	carInfoColoum.put("累计油量(L)", "totalOil");
    	carInfoColoum.put("实际油耗(L)", "oilConsume");
    	
    	//车辆加油信息对应的字段
    	Map<String, String> carOilColoum = new LinkedHashMap<String, String>();
    	carOilColoum.put("日期", "workDate");
    	carOilColoum.put("开工里程数(KM)", "startMileage");
    	carOilColoum.put("工作结束里程(KM)", "mileage");
    	carOilColoum.put("公里数(KM)", "kilometers");
    	carOilColoum.put("加油升数(L)", "oilLitres");
    	carOilColoum.put("加油金额(元)", "oilMoney");
    	carOilColoum.put("备注", "remark");
    	
    	try {
    		String crewId = this.getCrewId(request);
    		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
    		List<List<Map<String, Object>>> carOilList = new ArrayList<List<Map<String,Object>>>();
    		
    		//获取车辆信息列表
    		List<Map<String,Object>> carInfoList = this.carInfoService.queryAllCarInfo(crewId, searchCarNo);
    		for (Map<String, Object> map : carInfoList) {
				//格式化在组状态
    			int statusInt = (Integer) map.get("status");
    			if (statusInt == 0) {
					map.remove("status");
					map.put("status", "离组");
				}else if (statusInt == 1) {
					map.remove("status");
					map.put("status", "在组");
				}
    			
    			//根据车辆id，查询出加油记录
    			String carId = (String) map.get("carId");
    			String carNumber = (String) map.get("carNumber");
    			//获取车辆加油记录列表
    			List<Map<String, Object>> carOillist = this.carWorkSerivce.queryCarWorkByCarId(carId);
    			for (Map<String, Object> oilMap : carOillist) {
    				oilMap.put("carNumber", carNumber);
    				Date workDate = (Date) oilMap.get("workDate");
    				oilMap.remove("workDate");
    				if (workDate != null) {
    					oilMap.put("workDate", sdf.format(workDate));
					}else {
						oilMap.put("workDate", "");
					}
    			}
    			if (carOillist == null || carOillist.size() == 0) {
					Map<String, Object> oilMap = new HashMap<String, Object>();
					oilMap.put("carNumber", carNumber);
					carOillist.add(oilMap);
				}
    			carOilList.add(carOillist);
			}
    		
    		//调用方法导出表格数据
    		ExcelUtils.exportCarInfoForExcel(carInfoList, carOilList, response, carInfoColoum, carOilColoum, crewInfo.getCrewName());
    		this.sysLogService.saveSysLog(request, "车辆信息导出", terminal, CarInfoModel.TABLE_NAME, null, 5);
			message = "导出成功!";
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
		} catch (Exception e) {
			message = "未知异常，导出失败";
			success = false;
			logger.error(message, e);
    		this.sysLogService.saveSysLog(request, "车辆信息导出失败：" + e.getMessage(), terminal, CarInfoModel.TABLE_NAME, null, 6);
		}
    	
    	resultMap.put("message", message);
    	resultMap.put("success", success);
    	return resultMap;
    }
    
    /**
     * 获取最大车辆编号
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryMaxCarNo")
    public Map<String, Object> queryMaxCarNo(HttpServletRequest request){
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	
    	try {
    		String crewId = getCrewId(request);
			resultMap.put("maxCarNo", this.carInfoService.queryMaxCarNo(crewId));
		} catch (Exception e) {
			message = "未知错误，获取最大车辆编号失败";
			success = false;
			
			logger.error(message, e);
		}
    	
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
    }
    
    /**
     * 判断是否已存在车辆编号
     * @param request
     * @param carNo
     * @param carId
     * @return
     */
    @ResponseBody
    @RequestMapping("/isExistCarNo")
	public Map<String, Object> isExistCarNo(HttpServletRequest request, String carNo, String carId) {
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	
    	try {
    		String crewId = this.getCrewId(request);
			List<Map<String, Object>> list = this.carInfoService.queryByCarNo(Integer.parseInt(carNo), carId, crewId);
			boolean isExist = false;
			if(list != null && list.size() > 0) {
				isExist = true;
			}
			resultMap.put("isExist", isExist);
		} catch (Exception e) {
			message = "未知错误，判断是否已存在车辆编号失败";
			success = false;
			
			logger.error(message, e);
		}
    	
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
    }
    
    /**
     * 保存车辆信息
     * @param request
     * @param carId	车辆ID
     * @param carNo	车辆编号
     * @param driver	司机
     * @param useFor	用途
     * @param phone	电话
     * @param carModel	车辆型号
     * @param carNumber	车牌号
     * @param status	状态：0：离组，1：在组
     * @param identityNum	身份证号码
     * @param enterDate	入组日期
     * @param carWorkStr	加油信息
     * @return
     */
    @ResponseBody
    @RequestMapping("/saveOrUpdateCarInfo")
	public Map<String, Object> saveOrUpdateCarInfo(HttpServletRequest request,
			String carId, Integer carNo, String driver, String useFor,
			String phone, String carModel, String carNumber, 
			Integer status, String identityNum, String enterDate, String carWorkStr, String departments) {
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	
    	try {
			String crewId = getCrewId(request);
			//车辆编号不能为空
			if(StringUtil.isBlank(carNo + "")) {
				throw new IllegalArgumentException("车辆编号不能为空");
			}
			
			if (StringUtils.isBlank(carNumber)) {
				throw new IllegalArgumentException("车牌号码，不能为空");
			}else if (carNumber.length()>255) {
				throw new IllegalArgumentException("车牌号码过长，请修改");
			}
			//验证车辆编号是否已存在
			List<Map<String, Object>> list = this.carInfoService.queryByCarNo(carNo, carId, crewId);
			if(list != null && list.size() > 0) {
				throw new IllegalArgumentException("车辆编号已存在");
			}
			
			//司机
			if (driver.length()>20) {
				throw new IllegalArgumentException("司机名称过长，请修改！");
			}
			
			//电话
			if (phone.length()>20) {
				throw new IllegalArgumentException("电话号码过长，请修改！");
			}
			
			//车辆类型
			if (carModel.length()>255) {
				throw new IllegalArgumentException("车辆类型过长，请修改！");
			}
			
			//车辆用途
			if (useFor.length()>500) {
				throw new IllegalArgumentException("车辆用途填写过长，请修改！");
			}
			
			//验证车牌号是否重复
			List<Map<String,Object>> carList = this.carInfoService.queryByCarNumber(carNumber, crewId, carId);
			if (carList != null && carList.size() > 0) {
				throw new IllegalArgumentException("车牌号码已存在");
			}
			
			//保存车辆信息
			this.carInfoService.saveCarInfo(crewId, carId, carNo, driver, useFor,
					phone, carModel, carNumber, status, identityNum, enterDate, carWorkStr, departments);
			
			String logDesc = "";
			Integer operType = null;
			if(StringUtil.isBlank(carId)) {
				logDesc = "添加车辆信息及加油登记信息";
				operType = 1;
			} else {
				logDesc = "修改车辆信息及加油登记信息";
				operType = 2;
			}
			this.sysLogService.saveSysLog(request, logDesc, Constants.TERMINAL_PC, CarInfoModel.TABLE_NAME, carId, operType);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，保存车辆信息失败";
			success = false;			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "保存车辆信息失败：" + e.getMessage(), Constants.TERMINAL_PC, CarInfoModel.TABLE_NAME, carId, 6);
		}
    	
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
    }
    
    /**
     * 删除车辆信息
     * @param request
     * @param carId
     * @return
     */
    @ResponseBody
    @RequestMapping("/deleteCarInfo")
	public Map<String, Object> deleteCarInfo(HttpServletRequest request, String carId) {
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message ="";
    	boolean success = true;
    	
    	try {
    		if(StringUtil.isBlank(carId)) {
    			throw new IllegalArgumentException("请选择车辆信息");
    		}
    		
			this.carInfoService.deleteCarInfo(carId);

			this.sysLogService.saveSysLog(request, "删除车辆信息", Constants.TERMINAL_PC, CarInfoModel.TABLE_NAME, carId, 3);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，删除车辆信息失败";
			success = false;			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "删除车辆信息失败：" + e.getMessage(), Constants.TERMINAL_PC, CarInfoModel.TABLE_NAME, carId, 6);
		}
    	
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
    }
    
    /**
     * 导入车辆的详细信息
     * @param request
     * @param file
     * @param isCover
     * @return
     */
    @ResponseBody
    @RequestMapping("/importCarDetailInfo")
    public Map<String, Object> importCarDetailInfo(HttpServletRequest request, MultipartFile file, boolean isCover){
    	Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		String crewId = this.getCrewId(request);
		String crewName = crewInfo.getCrewName();
		SimpleDateFormat secondFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			if (file == null) {
				throw new IllegalArgumentException("请选择上传的文件！");
			}
			
			// 上传文件到服务器
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String baseStorePath = properties.getProperty("fileupload.path");
			String modelStorePath = baseStorePath + "import/car";
			String newName = "《"+ crewName +"》" + secondFormat.format(new Date())+"车辆信息";
			Map<String, String> fileMap = FileUtils.uploadFileForExcel(request, modelStorePath, newName);
			if (fileMap == null) {
				throw new IllegalArgumentException("请选择文件");
			}
			
			String fileStoreName = fileMap.get("fileStoreName");// 新文件名
			String storePath = fileMap.get("storePath");// 服务器存文文件路径
			//读取excel表的数据
			Map<String, Object> getCostInfoMap = ExcelUtils.readCarWorkInfo(storePath + fileStoreName);
			List<Map<String, Object>> dataList = null;
			
			String errorMessage = "";
			//取出excel读取的数据
			Set<String> sheetSet = getCostInfoMap.keySet();
			Iterator<String> sheetKeys = sheetSet.iterator();
			int a = 1;
			
			while(sheetKeys.hasNext()){
				String sheetKey = sheetKeys.next();
				//excel读取的数据
				List<ArrayList<String>> excelDataList = (List<ArrayList<String>>)getCostInfoMap.get(sheetKey);
				//为空或者只有一行（标题）则不保存
				if(excelDataList==null||excelDataList.size()<3){
					continue;
				}
				
				//如果当前是第一个sheet页时，表示是车辆信息
				if (a==1) {
					dataList = new ArrayList<Map<String,Object>>();
					//获取最大车牌号
					int maxCarNo = this.carInfoService.queryMaxCarNo(crewId);
					//读取第一行的数据
					for (int i=0; i<excelDataList.size(); i++) {
						if (i == 0 || i == 1) {
							continue;
						}
						Map<String, Object> dataMap = new HashMap<String, Object>();
						List<String> arrayList = excelDataList.get(i);
						//部门
						String department = arrayList.get(1);
						if (department.length() > 100) {
							errorMessage += "车辆所属部门字段过长，请修改；";
						}
						//判断当前填写的部门在系统中是否存在
						/*if (StringUtils.isNotBlank(department)) {
							String[] departmentArr = department.split(",");
							for (String departmentName : departmentArr) {
								//根据名称查询部门信息
								List<Map<String, Object>> list = this.carInfoService.queryDeparmentsByName(departmentName, crewId);
								if (list == null || list.size() == 0) {
									errorMessage += "车辆所属部门  '"+ departmentName +"' 在剧组中不存在，请修改；";
								}
							}
						}*/
						dataMap.put("departments", department);
						//车辆类型
						String carModel = arrayList.get(4);
						if (carModel.length()>255) {
							errorMessage += "车辆类型字段过长，请修改；";
						}
						dataMap.put("carModel", carModel);
						//车牌
						String carNumber = arrayList.get(3);
						if (StringUtils.isBlank(carNumber)) {
							errorMessage += "请填写车牌号；";
						}else {
							//根据车牌号判断是否是重复数据
							List<Map<String,Object>> list = this.carInfoService.queryByCarNumber(carNumber, crewId, "");
							
							if (list == null || list.size() == 0) {
								dataMap.put("isRepeat", false);
								
								//不是重复数据时，需要获取新的车辆编号
								int newCarNo = maxCarNo +1;
								maxCarNo++;
								dataMap.put("carNo", newCarNo);
							}else {
								dataMap.put("isRepeat", true);
								dataMap.put("carNo", 0);
							}
							dataMap.put("carNumber", carNumber);
						}
						
						//用途
						String useFor = arrayList.get(2);
						if (useFor.length()>500) {
							errorMessage += "车辆用途字段过长，请修改；";
						}
						dataMap.put("useFor", useFor);
						//电话号码
						String phone = arrayList.get(5);
						if (phone.length()>20) {
							errorMessage += "电话号码，过长，请修改；";
						}
						dataMap.put("phone", phone);
						//司机
						String driver = arrayList.get(6);
						if (driver.length()>20) {
							errorMessage += "司机字段过长，请修改；";
						}
						dataMap.put("driver", driver);
						
						//在组状态
						String status = arrayList.get(7);
						if (StringUtils.isNotBlank(status)) {
							if (status.equals("在组")) {
								dataMap.put("status", 1);
							}else if (status.equals("离组")) {
								dataMap.put("status", 0);
							}else {
								errorMessage += "在组状态填写不规范，请修改；";
							}
						}else {
							dataMap.put("status", 1);
						}
						
						if (StringUtils.isNotBlank(errorMessage)) {
							throw new IllegalArgumentException("车辆目录" + " 第"+ i + "行 " + errorMessage);
						}else {
							dataList.add(dataMap);
						}
						
					}
					
					if (StringUtils.isBlank(errorMessage)) {
						for (Map<String, Object> dataMap: dataList) {
							boolean isRepeat = (Boolean) dataMap.get("isRepeat");
							String departments = (String) dataMap.get("departments");
							String carModel = (String) dataMap.get("carModel");
							String carNumber = (String) dataMap.get("carNumber");
							String useFor = (String) dataMap.get("useFor");
							String phone = (String) dataMap.get("phone");
							
							int carNo = (Integer) dataMap.get("carNo");
							String driver = (String) dataMap.get("driver");
							
							Integer status = (Integer) dataMap.get("status");
							
							//将信息保存起来
							this.carInfoService.saveImportCarDetail(isCover, isRepeat, departments, carNo, driver, phone, carModel, carNumber, crewId, status, useFor);
						}
					}
				}else { //a不等1时表示导入的是车辆的加油信息记录
					//获取当前sheet页的名称即车牌号码 sheetKey就是每个sheet页的名称
					List<Map<String, Object>> carWorkList = carInfoService.queryByCarNumber(sheetKey, crewId, "");
					
					String carOilErrorMessage = "";
					if (carWorkList != null && carWorkList.size()>0) {
						dataList = new ArrayList<Map<String,Object>>();
						Map<String, Object> carInfoMap = carWorkList.get(0);
						String carId = (String) carInfoMap.get("carId");
						List<String> dateList = new ArrayList<String>();
						
						//读取第一行的数据
						for (int i=0; i<excelDataList.size(); i++) {
							if (i == 0 || i == 1) {
								continue;
							}
							Map<String, Object> dataMap = new HashMap<String, Object>();
							List<String> arrayList = excelDataList.get(i);
							
							//加油日期
							String workDateStr = arrayList.get(0);
							Date workDate = null;
							if (StringUtils.isBlank(workDateStr)) {
								carOilErrorMessage += "请填写加油日期；";
							}else {
								workDate = dateFormat.parse(workDateStr);
							}
							dataMap.put("workDate", workDate);
							
							//开始里程数
							String startMileage = arrayList.get(1);
							dataMap.put("startMileage", startMileage);
							
							//工作结束里程
							String mileage = arrayList.get(2);
							dataMap.put("mileage", mileage);
							
							//公里数
							String kilometers = arrayList.get(3);
							dataMap.put("kilometers", kilometers);
							
							//加油升数
							String oilLitres = arrayList.get(4);
							dataMap.put("oilLitres", oilLitres);
							
							//加油金额
							String oilMoney = arrayList.get(5);
							dataMap.put("oilMoney", oilMoney);
							
							//判断是否是重复数据
							dateList.add(workDateStr +""+startMileage+""+mileage+""+oilMoney);
							workDate = dateFormat.parse(workDateStr);
							
							//根据加油日期、工作结束里程、加油金额 查询出加油记录
							Map<String, Object> conditionMap = new HashMap<String, Object>();
							conditionMap.put("workDate", workDate);
							conditionMap.put("mileage", mileage);
							conditionMap.put("oilMoney", oilMoney);
							conditionMap.put("carId", carId);
							List<CarWorkModel> list = this.carWorkSerivce.queryManyByMutiCondition(conditionMap, null);
							if (list != null && list.size() > 0) {
								dataMap.put("isRepeat", true);
							}else {
								dataMap.put("isRepeat", false);
							}
							//备注信息
							String remark = arrayList.get(6);
							if (StringUtils.isNotBlank(remark) && remark.length()>500) {
								carOilErrorMessage += "备注填写过长，请修改；";
							}
							dataMap.put("remark", remark);
							
							if (StringUtils.isNotBlank(carOilErrorMessage)) {
								throw new IllegalArgumentException(sheetKey + " 的加油记录 " + "第"+ i + "行 " + carOilErrorMessage);
							}else {
								dataList.add(dataMap);
							}
							
						}
						
						//判断导入的加油日期是否有重复数据
						for(int i=0; i<dateList.size(); i++) {
							for(int j=dateList.size()-1; j>i; j--) {
								if (dateList.get(i).equals(dateList.get(j))) {
									carOilErrorMessage += sheetKey + " 的加油记录 " + "第 "+ (i+3) + " 行 和第 "+ (j+3) + " 行，加油记录有重复，请将重复数据合并！";
								}
							}
							
						}
						
						if (StringUtils.isBlank(carOilErrorMessage)) {
							for (Map<String, Object> dataMap: dataList) {
								boolean isRepeat = (Boolean) dataMap.get("isRepeat");
								//加油日期
								Date workDate = (Date) dataMap.get("workDate");
								
								//开始里程数
								Double startMileage = null;
								String startMileageStr = (String)dataMap.get("startMileage");
								if (StringUtils.isNotBlank(startMileageStr)) {
									startMileage = Double.parseDouble(startMileageStr);
								}
								
								//工作结束里程数
								Double mileage = null;
								String mileageStr = (String)dataMap.get("mileage");
								if (StringUtils.isNotBlank(mileageStr)) {
									mileage = Double.parseDouble(mileageStr);
								}
								
								//公里数
								String kilometersStr = (String) dataMap.get("kilometers");
								Double kilometers = null;
								if (StringUtils.isNotBlank(kilometersStr)) {
									kilometers = Double.parseDouble(kilometersStr);
								}
								
								//加油升数
								Double oilLitres = null;
								String oilLitresStr = (String) dataMap.get("oilLitres");
								if (StringUtils.isNotBlank(oilLitresStr)) {
									oilLitres = Double.parseDouble(oilLitresStr);
								}
								
								//加油金额
								Double oilMoney = null;
								String oilMoneyStr = (String) dataMap.get("oilMoney");
								if (StringUtils.isNotBlank(oilMoneyStr)) {
									oilMoney = Double.parseDouble(oilMoneyStr);
								}
								String remark = (String) dataMap.get("remark");
								
								//将信息保存起来
								this.carInfoService.saveImportCarWorkInfo(isCover, isRepeat,startMileage, mileage, workDate, kilometers, oilLitres, oilMoney, crewId, carId, remark);
							}
						}else {
							throw new IllegalArgumentException(carOilErrorMessage);
						}
					}
					
				}
				
				a++;
			}
			
			this.sysLogService.saveSysLog(request, "车辆信息导入", terminal, CarInfoModel.TABLE_NAME, null, 4);
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，导入失败";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "车辆信息导入失败：" + e.getMessage(), terminal, CarInfoModel.TABLE_NAME, null, 6);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
    }
    
    /**
     * 根据车辆加油信息id删除车辆的加油记录
     * @param request
     * @param workId
     * @return
     */
    @ResponseBody
    @RequestMapping("/deleteCarWorkInfo")
    public Map<String, Object> deleteCarWorkInfo(HttpServletRequest request, String workId){
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message = "";
    	boolean success = true;
    	
    	try {
			if (StringUtils.isBlank(workId)) {
				throw new IllegalArgumentException("请选择要删除的加油记录");
			}
			
			this.carWorkSerivce.deleteById(workId);
			
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
     * 查询出当前剧组中所有的部门
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryCrewDepartmentList")
    public Map<String, Object> queryCrewDepartmentList(HttpServletRequest request){
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message = "";
    	boolean success = true;
    	
    	try {
    		
    		String crewId = this.getCrewId(request);
    		
			resultMap.put("departmentList", this.carInfoService.queryCrewDepartmentInfo(crewId));
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
     * 更新车辆序号
     * @param request
     * @param carIds
     * @return
     */
    @ResponseBody
    @RequestMapping("/updateCarSequence")
    public Map<String, Object> updateCarSequence(HttpServletRequest request, String carIds){
    	Map<String, Object> resultMap = new HashMap<String, Object>();
    	String message = "";
    	boolean success = true;
    	
    	try {
			if (StringUtils.isBlank(carIds)) {
				throw new IllegalArgumentException("请选择要更新的车辆");
			}
			
			String crewId = this.getCrewId(request);
			
			this.carInfoService.updateCarSequence(carIds);
			message = "更新成功";
		}catch(IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知错误，更新失败";
			success = false;
			
			logger.error(message, e);
		}
    	resultMap.put("success", success);
    	resultMap.put("message", message);
    	return resultMap;
    }
    
}
