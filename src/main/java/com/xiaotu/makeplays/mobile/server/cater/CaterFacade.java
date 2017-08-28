package com.xiaotu.makeplays.mobile.server.cater;

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

import com.xiaotu.makeplays.cater.model.CaterInfoModel;
import com.xiaotu.makeplays.cater.model.CaterMoneyInfoModel;
import com.xiaotu.makeplays.cater.service.CaterInfoService;
import com.xiaotu.makeplays.cater.service.CaterMoneyInfoService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.mobile.server.car.CarInfoFacade;
import com.xiaotu.makeplays.mobile.server.common.BaseFacade;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.utils.BigDecimalUtil;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 餐饮相关接口
 * @author xuchangjian 2017-3-8下午4:26:10
 */
@Controller
@RequestMapping("/interface/caterFacade")
public class CaterFacade extends BaseFacade {
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	
	Logger logger = LoggerFactory.getLogger(CarInfoFacade.class);

	@Autowired
	private CaterInfoService caterInfoService;
	
	@Autowired
	private CaterMoneyInfoService caterMoneyInfoService;
	
	/**
	 * 获取餐饮列表接口
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainCaterList")
	public Object obtainCaterList(HttpServletRequest request, String crewId, String userId, Integer pageNo, Integer pageSize) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			// 判断用户是否有效
			MobileUtils.checkUserValid(userId);
			
			if (pageNo == null) {
				pageNo = 1;
			}
			if (pageSize == null) {
				pageSize = 20;
			}

			Page page = new Page();
			page.setPageNo(pageNo);
			page.setPagesize(pageSize);
			
			double totalMoney = 0.0;
			List<Map<String, Object>> caterList = this.caterInfoService.queryCaterInfoListNoFormat(page, crewId);
			for (Map<String, Object> caterInfo : caterList) {
				//计算总费用
				Double caterMoney = (Double) caterInfo.get("caterMoney");
				if (caterMoney != null) {
					totalMoney = BigDecimalUtil.add(totalMoney, caterMoney);
				}
				
				//格式化日期
				Date caterDate = (Date) caterInfo.get("caterDate");
				caterInfo.put("caterDate", this.sdf1.format(caterDate));
				
				Integer totalPeople = 0;
				if (caterInfo.get("totalPeople") != null) {
					totalPeople = Integer.parseInt(caterInfo.get("totalPeople").toString());
				}
				
				//计算人均
				Double perCapita = 0.0;
				if (totalPeople != 0) {
					perCapita = BigDecimalUtil.divide(caterMoney, totalPeople);
				}
				caterInfo.put("perCapita", perCapita);
			}
			
			int totalDays = caterList.size();
			int pageCount = page.getPageCount();
			
			resultMap.put("totalDays", totalDays);
			resultMap.put("totalMoney", totalMoney);
			resultMap.put("caterList", caterList);
			resultMap.put("pageCount", pageCount);
			
			this.sysLogService.saveSysLogForApp(request, "查询餐饮列表", this.getClientType(userId), CaterInfoModel.TABLE_NAME, "", 1);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取餐饮列表失败", e);
			throw new IllegalArgumentException("未知异常，获取餐饮列表失败", e);
		}

		return resultMap;
	}
	
	/**
	 * 获取一天的餐饮信息接口
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainDayCaterInfo")
	public Object obtainDayCaterInfo(String crewId, String userId, String caterId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			// 判断用户是否有效
			MobileUtils.checkUserValid(userId);
			if (StringUtils.isBlank(caterId)) {
				throw new IllegalArgumentException("请提供餐饮ID");
			}
			
			CaterInfoModel caterInfo = this.caterInfoService.queryById(caterId);
			
			List<Map<String, Object>> caterMoneyList = this.caterMoneyInfoService.queryCaterMoneyByCaterId(caterId);
			
			int peopleCount = 0;	//总人数
			double totalMoney = 0.0;	//总金额
			double budget = caterInfo.getBudget();	//本日预算
			
			for (Map<String, Object> caterMoneyInfo : caterMoneyList) {
				Integer myPeopleCount = (Integer) caterMoneyInfo.get("peopleCount");
				Double caterMoney = (Double) caterMoneyInfo.get("caterMoney");
				String typeName = (String) caterMoneyInfo.get("caterType");
				
				if (myPeopleCount != null) {
					peopleCount += myPeopleCount;
				}
				totalMoney = BigDecimalUtil.add(totalMoney, caterMoney);
				
				caterMoneyInfo.put("caterType", typeName);
			}

			double saveMoney = BigDecimalUtil.subtract(budget, totalMoney);	//节约金额
			double peopleAvg = 0;
			if(peopleCount != 0) {
				peopleAvg = BigDecimalUtil.divide(totalMoney, peopleCount); 
			}			
			
			resultMap.put("peopleCount", peopleCount);
			resultMap.put("totalMoney", totalMoney);
			resultMap.put("saveMoney", saveMoney);
			resultMap.put("peopleAvg", peopleAvg);
			resultMap.put("caterDate", this.sdf1.format(caterInfo.getCaterDate()));
			resultMap.put("budget", budget);
			resultMap.put("caterMoneyList", caterMoneyList);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取餐饮信息失败", e);
			throw new IllegalArgumentException("未知异常，获取餐饮信息失败", e);
		}

		return resultMap;
	}
	
	/**
	 * 保存餐饮信息接口
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveCaterInfo")
	public Object saveCaterInfo(HttpServletRequest request, String crewId, String userId, String caterId, String caterDate, Double budget) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			// 判断用户是否有效
			MobileUtils.checkUserValid(userId);
			if (StringUtils.isBlank(caterDate)) {
				throw new IllegalArgumentException("请填写餐饮日期");
			}
			if (budget == null) {
				throw new IllegalArgumentException("请填写预算");
			}
			//校验是否已经存在该天的餐饮记录
			Map<String, Object> caterConditionMap = new HashMap<String, Object>();
			caterConditionMap.put("crewId", crewId);
			caterConditionMap.put("caterDate", caterDate);
			List<CaterInfoModel> existCaterList = this.caterInfoService.queryManyByMutiCondition(caterConditionMap, null);
			if (existCaterList != null && existCaterList.size() > 0) {
				if (!caterId.equals(existCaterList.get(0).getCaterId())) {
					throw new IllegalArgumentException("已存在相同日期的餐饮记录，请修改日期");
				}
			}
			
			CaterInfoModel caterInfo = new CaterInfoModel();
			if (StringUtils.isBlank(caterId)) {
				caterInfo.setCaterId(UUIDUtils.getId());
			} else {
				caterInfo = this.caterInfoService.queryById(caterId); 
			}
			caterInfo.setCaterDate(this.sdf1.parse(caterDate));
			caterInfo.setBudget(budget);
			caterInfo.setCrewId(crewId);
			
			if (StringUtils.isBlank(caterId)) {
				this.caterInfoService.addOne(caterInfo);
			} else {
				this.caterInfoService.updateOne(caterInfo);
			}
			
			resultMap.put("caterId", caterInfo.getCaterId());
			
			this.sysLogService.saveSysLogForApp(request, "保存餐饮信息", this.getClientType(userId), CaterInfoModel.TABLE_NAME, "", 1);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，保存餐饮信息失败", e);
			this.sysLogService.saveSysLogForApp(request, "保存餐饮信息失败：" + e.getMessage(), this.getClientType(userId), CaterInfoModel.TABLE_NAME, "", SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，保存餐饮信息失败", e);
		}

		return resultMap;
	}
	
	/**
	 * 删除餐饮信息
	 * @param userId
	 * @param crewId
	 * @param caterId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteCaterInfo")
	public Object deleteCaterInfo(HttpServletRequest request, String userId, String crewId, String caterId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			// 判断用户是否有效
			MobileUtils.checkUserValid(userId);
			if (StringUtils.isBlank(caterId)) {
				throw new IllegalArgumentException("请提供餐饮ID");
			}
			
			this.caterInfoService.deleteCaterInfoById(caterId);
			

			this.sysLogService.saveSysLogForApp(request, "删除餐饮信息", this.getClientType(userId), CaterInfoModel.TABLE_NAME, "", 1);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，删除餐饮信息失败", e);
			this.sysLogService.saveSysLogForApp(request, "删除餐饮信息失败：" + e.getMessage(), this.getClientType(userId), CaterInfoModel.TABLE_NAME, "", SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，删除餐饮信息失败", e);
		}

		return resultMap;
	}
	
	/**
	 * 保存餐饮明细
	 * @param userId
	 * @param crewId
	 * @param caterId	餐饮ID
	 * @param caterMoneyId	餐饮明细ID
	 * @param caterType	餐别
	 * @param caterTime 用餐时间
	 * @param caterAddr 用餐地点
	 * @param peopleCount	人数
	 * @param caterCount	份数
	 * @param caterMoney	金额
	 * @param perCapita	人均
	 * @param remark	备注
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveCaterDetailInfo")
	public Object saveCaterDetailInfo(HttpServletRequest request, String userId, String crewId, String caterId, 
			String caterMoneyId, String caterType, String caterTime, String caterAddr, Integer peopleCount, 
			Integer caterCount, Double caterMoney, Double perCapita, 
			String remark) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			// 判断用户是否有效
			MobileUtils.checkUserValid(userId);
			
			if (StringUtils.isBlank(caterId)) {
				throw new IllegalArgumentException("请提供餐饮ID");
			}
			if (StringUtils.isBlank(caterType)) {
				throw new IllegalArgumentException("请填写餐别");
			}
			if (caterMoney == null) {
				throw new IllegalArgumentException("请填写金额");
			}
			
			this.caterMoneyInfoService.saveCaterDetailInfo(crewId, caterId, caterMoneyId,
					caterType, caterTime, caterAddr, peopleCount, caterCount, caterMoney,
					perCapita, remark);

			this.sysLogService.saveSysLogForApp(request, "保存餐饮信息", this.getClientType(userId), CaterMoneyInfoModel.TABLE_NAME, "", 1);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，保存餐饮信息失败", e);
			this.sysLogService.saveSysLogForApp(request, "保存餐饮信息失败：" + e.getMessage(), this.getClientType(userId), CaterMoneyInfoModel.TABLE_NAME, "", SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，保存餐饮信息失败", e);
		}

		return resultMap;
	}
	
	/**
	 * 删除餐饮明细ID
	 * @param crewId
	 * @param userId
	 * @param caterMoneyId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteCaterDetailInfo")
	public Object deleteCaterDetailInfo (HttpServletRequest request, String crewId, String userId, String caterMoneyId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			// 判断用户是否有效
			MobileUtils.checkUserValid(userId);
			if (StringUtils.isBlank(caterMoneyId)) {
				throw new IllegalArgumentException("请提供餐饮明细信息ID");
			}
			
			this.caterMoneyInfoService.deleteById(caterMoneyId);

			this.sysLogService.saveSysLogForApp(request, "删除餐饮信息", this.getClientType(userId), CaterMoneyInfoModel.TABLE_NAME, "", 1);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，删除餐饮信息失败", e);
			this.sysLogService.saveSysLogForApp(request, "删除餐饮信息失败：" + e.getMessage(), this.getClientType(userId), CaterMoneyInfoModel.TABLE_NAME, "", SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，删除餐饮信息失败", e);
		}

		return resultMap;
	}
	
	/**
	 * 获取下拉数据
	 * @param crewId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainDropDownData")
	public Object obtainDropDownData(String crewId, String userId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			// 判断用户是否有效
			MobileUtils.checkUserValid(userId);
			
			//获取用餐类型
			List<Map<String,Object>> list = this.caterMoneyInfoService.queryCaterTypeByCrewId(crewId);			
			List<String> caterTypeNameList = new ArrayList<String>();
			for (Map<String, Object> map : list) {
				caterTypeNameList.add((String)map.get("caterType"));
			}
			//获取用餐时间，用餐地点
			List<List<Map<String,Object>>> timeAddrList = this.caterMoneyInfoService.queryCaterTimeAddrByCrewId(crewId);
			List<String> caterTimeNameList = new ArrayList<String>();
			for(Map<String, Object> map : timeAddrList.get(0)) {
				caterTimeNameList.add((String) map.get("caterTimeType"));
			}
			List<String> caterAddrNameList = new ArrayList<String>();
			for(Map<String, Object> map : timeAddrList.get(1)) {
				caterAddrNameList.add((String) map.get("caterAddr"));
			}
			
			resultMap.put("caterTypeList", caterTypeNameList);
			resultMap.put("caterTimeList", caterTimeNameList);
			resultMap.put("caterAddrList", caterAddrNameList);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取信息失败", e);
			throw new IllegalArgumentException("未知异常，获取信息失败", e);
		}

		return resultMap;
	}
}
