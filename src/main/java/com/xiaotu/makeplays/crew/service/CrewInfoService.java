package com.xiaotu.makeplays.crew.service;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.xiaotu.makeplays.authority.dao.CrewAuthMapDao;
import com.xiaotu.makeplays.authority.dao.RoleAuthMapDao;
import com.xiaotu.makeplays.authority.dao.UserAuthMapDao;
import com.xiaotu.makeplays.authority.service.AuthorityService;
import com.xiaotu.makeplays.crew.controller.filter.CrewInfoFilter;
import com.xiaotu.makeplays.crew.dao.CrewClearDao;
import com.xiaotu.makeplays.crew.dao.CrewInfoDao;
import com.xiaotu.makeplays.crew.dao.CrewUserMapDao;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.model.CrewUserMapModel;
import com.xiaotu.makeplays.crew.model.constants.CrewType;
import com.xiaotu.makeplays.crew.model.constants.ProjectType;
import com.xiaotu.makeplays.scenario.model.ScenarioInfoModel;
import com.xiaotu.makeplays.sysrole.dao.UserRoleMapDao;
import com.xiaotu.makeplays.sysrole.model.UserRoleMapModel;
import com.xiaotu.makeplays.user.dao.UserInfoDao;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.service.CrewContactService;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.PictureUtils;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.view.model.ViewContentModel;

/**
 * 剧组
 * @author xuchangjian 2016-10-10下午5:40:42
 */
@Service
public class CrewInfoService {
	
	@Autowired
	private CrewInfoDao  crewInfoDao;
	
	@Autowired
	private UserInfoDao userInfoDao;
	
	@Autowired
	private UserRoleMapDao userRoleMapDao;
	
	@Autowired
	private RoleAuthMapDao roleAuthMapDao;
	
	@Autowired
	private AuthorityService authorityService;
	
	@Autowired
	private CrewUserMapDao crewUserMapDao;
	
	@Autowired
	private UserAuthMapDao userAuthMapDao;
	
	@Autowired
	private CrewContactService crewContactService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CrewAuthMapDao crewAuthMapDao;
	
	@Autowired
	private CrewClearDao crewClearDao;
	
	/**
	 * 根据ID查询剧组信息
	 * @param crewId
	 * @return
	 * @throws Exception 
	 */
	public CrewInfoModel queryById(String crewId) throws Exception {
		return this.crewInfoDao.queryById(crewId);
	}
	
	/**
	 * 根据多个条件查询剧组信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<CrewInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.crewInfoDao.queryManyByMutiCondition(conditionMap, page);
	}

	/**
	 * 修改剧本详情
	 * @throws Exception 
	 */
	public int updateCrew(CrewInfoModel crewInfo) throws Exception{
		return crewInfoDao.updateWithNull(crewInfo, "crewId");
	}
	
	/**
	 * 批量更新
	 * @param crewList
	 * @throws Exception 
	 */
	public void updateMany(List<CrewInfoModel> crewList) throws Exception {
		for (CrewInfoModel crewInfo : crewList) {
			this.updateCrew(crewInfo);
		}
	}
	
	/**
	 * 查询剧组信息（剧组名字模糊查询）
	 * @param crewName
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryCrewList(CrewInfoFilter crewInfoFilter) throws Exception {
		return crewInfoDao.queryCrewList(crewInfoFilter);
	}
	
	/**
	 * 查询所有的剧组ID
	 * @return
	 */
	public List<Map<String, Object>> queryAllCrewIdAndName() {
		return crewInfoDao.queryAllCrewIdAndName();
	}
	
	
	/**
	 * lma 删除剧组成员所有权限
	 */
	@Deprecated
	public void dleteUserRoleAuthAll(String userId,String crewId)throws Exception{
		this.crewInfoDao.dleteUserRoleAuthAll(userId, crewId);
	}
	@Deprecated
	public void dleteCrewUserMap(String userId,String crewId)throws Exception{
		this.crewInfoDao.dleteCrewUserMap(userId, crewId);
	}
	
	/**
	 * 删除用户与权限表
	 */
	@Deprecated
	public int  delUserAuth (String userId,String crewId)throws Exception {
		return this.crewInfoDao.delUserAuth(userId,crewId);
	}
	
	/**
	 * 用户权限更改
	 * authids  权限与状态集合  权限之间用‘,’隔开，权限与状态之间用‘-’隔开，有此权限为0，无此权限为1，默认为1
	 */
	@Deprecated
	public void updateUserAuth(String crewId,String userId,String authids){
		//删除剧组用户权限
		this.userInfoDao.deleteUserAuth(crewId, userId);
		String auth[] = authids.split(",");
		for (int i = 0; i < auth.length; i++) {
			String s[] = auth[i].split("-");
			if(s.length>1){
				this.userInfoDao.addUserAuth(s[0], Integer.valueOf(s[1]), userId, crewId);
			}else{
				this.userInfoDao.addUserAuth(s[0], 1, userId, crewId);
			}
			
		}
	}
	
	/**
	 * 根据用户角色更新权限
	 */
	@Deprecated
	public void updateUserAuthByRoleids(String crewId,String userId,String roleids){
		//获取用户权限合集
		List<Map<String,Object>> authList = this.crewInfoDao.getAuthIdByRoleids(roleids);
		
		StringBuilder sb = new StringBuilder();  
		if(authList != null && authList.size()>0){
			for (int i = 0; i < authList.size(); i++) {
				if (i < authList.size() - 1) {  
	                sb.append((authList.get(i)).get("authId") + ",");  
	            } else {  
	                sb.append((authList.get(i)).get("authId"));  
	            }
			}
		}
		if(sb.length()>0){
			this.updateUserAuth(crewId, userId, sb.toString());
		}
	}
	/**
	 * 查询场景角色
	 */
	@Deprecated
	public List<Map<String, Object>> getCrewRole(String crewId,String userId) throws Exception{
		return this.crewInfoDao.getCrewRole(crewId,userId);
	}
	
	/**
	 * 根据剧组名称查询剧组信息
	 * @param crewName
	 * @return
	 */
	public List<Map<String,Object>> queryCrewIdAndCrewName(String crewName){
		return this.crewInfoDao.queryCrewIdAndCrewName(crewName);
	}
	
	/**
	 * 删除用户与权限表
	 * @param userId
	 * @param crewId
	 */
	public void deleteUserAuth (String crewId, String userId) {
		this.crewInfoDao.deleteUserAuth(crewId, userId);
	}
	
	/**
	 * 删除用户和想要关注的演员的关联关系
	 * @param crewId
	 * @param userId
	 */
	public void deleteUserFocusRoleMap(String crewId, String userId) {
		this.crewInfoDao.deleteUserFocusRoleMap(crewId, userId);
	}
	
	/**
	 * 删除用户和角色关联
	 * @param crewId
	 * @param userId
	 */
	public void deleteUserRoleMap(String crewId, String userId) {
		this.crewInfoDao.deleteUserRoleMap(crewId, userId);
	}
	
	/**
	 * 删除场景角色与用户关联表
	 * @param crewId
	 * @param userId
	 */
	public void deleteCrewRoleUserMap(String crewId, String userId) {
		this.crewInfoDao.deleteCrewRoleUserMap(crewId, userId);
	}
	
	/**
	 * 删除剧组与用户关联
	 * @param crewId
	 * @param userId
	 */
	public void deleteCrewUserMap(String crewId, String userId) {
		this.crewInfoDao.deleteCrewUserMap(crewId, userId);
	}
	
	/**
	 * 普通用户新增剧组
	 * @param crewName	剧组名称
	 * @param crewType	剧组类型
	 * @param subject	题材
	 * @param recordNumber	备案号
	 * @param shootStartDate	开机时间
	 * @param shootEndDate	杀青时间
	 * @param company	制片
	 * @param director	导演
	 * @param scriptWriter	编剧
	 * @param mainactor	主演
	 * @param enterPassword	入组密码
	 * @param seriesNo	立项集数
	 * @param status 剧组状态
	 * @param coProduction 合拍协议，0：无，1：已签订
	 * @param budget 剧组执行预算
	 * @param coProMoney 合拍协议金额
	 * @param investmentRatio 我方投资比例
	 * @param remark 重要事项说明
	 * @param userInfoModel 用户信息
	 * @return
	 * @throws Exception 
	 */
	public CrewInfoModel saveCrewByNormalUser(String crewId, String crewName,
			Integer crewType, String subject, String recordNumber,
			String shootStartDate, String shootEndDate, String company,
			String director, String scriptWriter, String mainactor,
			String enterPassword, Integer seriesNo, Integer status,
			Integer coProduction, Double budget, Double coProMoney,
			Double investmentRatio, String remark, UserInfoModel userInfoModel,
			MultipartFile file) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		//保存剧组信息
		CrewInfoModel crewInfo = new CrewInfoModel();
		if (!StringUtils.isBlank(crewId)) {
			crewInfo = this.queryById(crewId);
			
			Integer oldCrewType = crewInfo.getCrewType();
			//电影/网大 与 电视剧/网剧切换时,判断剧本是否存在提示
			if(((crewType == CrewType.TVPlay.getValue() || crewType == CrewType.InternetTvplay.getValue()) 
					&& (oldCrewType == CrewType.Movie.getValue() || oldCrewType == CrewType.InternetMovie.getValue())) 
					|| ((crewType == CrewType.Movie.getValue() || crewType == CrewType.InternetMovie.getValue()) 
							&& (oldCrewType == CrewType.TVPlay.getValue() || oldCrewType == CrewType.InternetTvplay.getValue()))) {
				int scenarioNum = this.crewClearDao.queryRecordNum(crewId, "crewId", ViewContentModel.TABLE_NAME);
				if(scenarioNum != 0) {
					throw new IllegalArgumentException("更改剧组类型，会造成已有剧本及统计信息的格式混乱，请确认删除现有剧本后再修改类型！");
				}
			}
		} else {
			crewInfo.setCrewId(UUIDUtils.getId());
			crewInfo.setCreateTime(new Date());
			crewInfo.setStartDate(new Date());
			//剧组失效日期默认为3年后
			Calendar nowCal = Calendar.getInstance();
			nowCal.add(Calendar.YEAR, 1);
			nowCal.add(Calendar.MONTH, 6);
			crewInfo.setEndDate(nowCal.getTime());
		}
		
		crewInfo.setCrewName(crewName);
		crewInfo.setCrewType(crewType);
		
		if (!StringUtils.isBlank(shootStartDate)) {
			crewInfo.setShootStartDate(sdf.parse(shootStartDate));
		}else {
			crewInfo.setShootStartDate(null);
		}
		if (!StringUtils.isBlank(shootEndDate)) {
			crewInfo.setShootEndDate(sdf.parse(shootEndDate));
		}else {
			crewInfo.setShootEndDate(null);
		}
		crewInfo.setCompany(company);
		crewInfo.setSubject(subject);
		crewInfo.setDirector(director);
		crewInfo.setScriptWriter(scriptWriter);
		crewInfo.setMainactor(mainactor);
		crewInfo.setCreateUser(userInfoModel.getUserId());
		crewInfo.setRecordNumber(recordNumber);
		if(enterPassword != null) {
			crewInfo.setEnterPassword(enterPassword);
		}
		if(status != null) {
			crewInfo.setStatus(status);
		}
		if(seriesNo != null) {
			crewInfo.setSeriesNo(seriesNo);
		}
		if(coProduction != null) {
			crewInfo.setCoProduction(coProduction);
		}
		if(coProMoney != null) {
			crewInfo.setCoProMoney(coProMoney);
		}
		if(budget != null) {
			crewInfo.setBudget(budget);
		}
		if(investmentRatio != null) {
			crewInfo.setInvestmentRatio(investmentRatio);
		}
		if(StringUtil.isNotBlank(crewInfo.getRemark()) && !remark.equals(crewInfo.getRemark())) {
			crewInfo.setLastRemark(crewInfo.getRemark());
			crewInfo.setRemark(remark);
		} else {
			crewInfo.setRemark(remark);
		}
		
		
		//上传剧照
		String picPath = "";
		if (file != null) {
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String baseStorePath = properties.getProperty("fileupload.path");
			String storePath = baseStorePath + "crew/";
			
			Map<String, String> fileMap = FileUtils.uploadFile(file, false, storePath);
			
			String fileStoreName = fileMap.get("fileStoreName");
			String myStorePath = fileMap.get("storePath");
			
			picPath = myStorePath + fileStoreName;
		} else {
			picPath = this.genDefaultPic(crewName);
		}
		
		crewInfo.setPicPath(picPath);
		
		if (!StringUtils.isBlank(crewId)) {
			this.updateCrew(crewInfo);
		} else {
			this.crewInfoDao.add(crewInfo);
			
			//将所有权限赋给剧组
			this.crewAuthMapDao.addAllAuthToCrew(crewInfo.getCrewId());
			
			//把其他剧组设置为非默认剧组
			this.crewUserMapDao.unDefaultUserCrew(userInfoModel.getUserId());
			
			//把用户加入到该剧组中，并设置为默认剧组
			CrewUserMapModel crewUserMap = new CrewUserMapModel();
			crewUserMap.setMapId(UUIDUtils.getId());
			crewUserMap.setCrewId(crewInfo.getCrewId());
			crewUserMap.setUserId(userInfoModel.getUserId());
			crewUserMap.setRoleId(Constants.ROLE_ID_ADMIN);
			crewUserMap.setType(Constants.CREW_TYPE_ADMIN);
			crewUserMap.setStatus(Constants.STATUS_OK);
			crewUserMap.setIfDefault(true);
			this.crewUserMapDao.add(crewUserMap);
			
			//设置用户为剧组管理员
			UserRoleMapModel userRoleMap = new UserRoleMapModel();
			userRoleMap.setMapId(UUIDUtils.getId());
			userRoleMap.setUserId(userInfoModel.getUserId());
			userRoleMap.setRoleId(Constants.ROLE_ID_ADMIN);
			userRoleMap.setCrewId(crewInfo.getCrewId());
			this.userRoleMapDao.add(userRoleMap);
			
			//把用户同步到剧组联系表
			this.crewContactService.syncFromUserInfo(crewInfo.getCrewId(), userInfoModel);
			
			//把剧组管理员的所有权限赋予用户
			this.userAuthMapDao.addByRoleId(crewInfo.getCrewId(), userInfoModel.getUserId(), Constants.ROLE_ID_ADMIN);
			
			//当前用户可建组次数减一
			int ubCreateCrewNum = userInfoModel.getUbCreateCrewNum();
			userInfoModel.setUbCreateCrewNum(--ubCreateCrewNum);
			this.userInfoDao.update(userInfoModel, "userId");
		}
		
		return crewInfo;
	}
	
	/**
	 * 系统管理员新增剧组
	 * @param crewName	剧组名称
	 * @param crewType	剧组类型
	 * @param projectType 项目类型
	 * @param allowExport 是否允许导出
	 * @param subject	题材
	 * @param recordNumber	备案号
	 * @param startDate	账号开始时间
	 * @param endDate	账号结束时间
	 * @param shootStartDate	开机时间
	 * @param shootEndDate	杀青时间
	 * @param company	制片
	 * @param director	导演
	 * @param scriptWriter	编剧
	 * @param mainactor	主演
	 * @param enterPassword	入组密码
	 * @param userInfoModel 用户信息
	 * @param seriesNo	立项集数
	 * @param status 剧组状态
	 * @param coProduction 合拍协议，0：无，1：已签订
	 * @param budget 剧组执行预算
	 * @param coProMoney 合拍协议金额
	 * @param investmentRatio 我方投资比例
	 * @param remark 重要事项说明
	 * @param userInfoModel 用户信息
	 * @return
	 * @throws Exception 
	 */
	public void saveCrewByAdmin(String crewId, String crewName,
			Integer crewType, Integer projectType, boolean allowExport,
			String subject, String recordNumber, String startDate,
			String endDate, String shootStartDate, String shootEndDate,
			String company, String director, String scriptWriter,
			String mainactor, String enterPassword, Integer seriesNo,
			Integer status, Integer coProduction, Double budget,
			Double coProMoney, Double investmentRatio, String remark,
			UserInfoModel userInfoModel, MultipartFile file) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		//保存剧组信息
		CrewInfoModel crewInfo = new CrewInfoModel();
		
		if (!StringUtils.isBlank(crewId)) {
			crewInfo = this.queryById(crewId);
			
			Integer oldCrewType = crewInfo.getCrewType();
			//电影/网大 与 电视剧/网剧切换时,判断剧本是否存在提示
			if(((crewType == CrewType.TVPlay.getValue() || crewType == CrewType.InternetTvplay.getValue()) 
					&& (oldCrewType == CrewType.Movie.getValue() || oldCrewType == CrewType.InternetMovie.getValue())) 
					|| ((crewType == CrewType.Movie.getValue() || crewType == CrewType.InternetMovie.getValue()) 
							&& (oldCrewType == CrewType.TVPlay.getValue() || oldCrewType == CrewType.InternetTvplay.getValue()))) {
				int scenarioNum = this.crewClearDao.queryRecordNum(crewId, "crewId", ViewContentModel.TABLE_NAME);
				if(scenarioNum != 0) {
					throw new IllegalArgumentException("更改剧组类型，会造成已有剧本及统计信息的格式混乱，请确认删除现有剧本后再修改类型！");
				}
			}
		} else {
			crewInfo.setCrewId(UUIDUtils.getId());
			crewInfo.setCreateTime(new Date());
		}
		
		crewInfo.setCrewName(crewName);
		crewInfo.setCrewType(crewType);
		crewInfo.setProjectType(projectType);
//		crewInfo.setAllowExport(allowExport);
		
		if (!StringUtils.isBlank(startDate)) {
			crewInfo.setStartDate(sdf.parse(startDate));
		}
		if (!StringUtils.isBlank(endDate)) {
			crewInfo.setEndDate(sdf.parse(endDate));
		}
		
		if (!StringUtils.isBlank(shootStartDate)) {
			crewInfo.setShootStartDate(sdf.parse(shootStartDate));
		}else {
			crewInfo.setShootStartDate(null);
		}
		if (!StringUtils.isBlank(shootEndDate)) {
			crewInfo.setShootEndDate(sdf.parse(shootEndDate));
		}else {
			crewInfo.setShootEndDate(null);
		}
		crewInfo.setCompany(company);
		crewInfo.setSubject(subject);
		crewInfo.setDirector(director);
		crewInfo.setScriptWriter(scriptWriter);
		crewInfo.setMainactor(mainactor);
		crewInfo.setCreateUser(userInfoModel.getUserId());
		crewInfo.setRecordNumber(recordNumber);
		crewInfo.setEnterPassword(enterPassword);
		crewInfo.setStatus(status);
		crewInfo.setSeriesNo(seriesNo);
		crewInfo.setCoProduction(coProduction);
		crewInfo.setCoProMoney(coProMoney);
		crewInfo.setBudget(budget);
		crewInfo.setInvestmentRatio(investmentRatio);
		if(StringUtil.isNotBlank(crewInfo.getRemark()) && !remark.equals(crewInfo.getRemark())) {
			crewInfo.setLastRemark(crewInfo.getRemark());
			crewInfo.setRemark(remark);
		} else {
			crewInfo.setRemark(remark);
		}
		
		//上传剧照
		String picPath = "";
		if (file != null) {
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String baseStorePath = properties.getProperty("fileupload.path");
			String storePath = baseStorePath + "crew/";
			
			Map<String, String> fileMap = FileUtils.uploadFile(file, false, storePath);
			
			String fileStoreName = fileMap.get("fileStoreName");
			String myStorePath = fileMap.get("storePath");
			
			picPath = myStorePath + fileStoreName;
		} else {
			picPath = this.genDefaultPic(crewName);
		}
		crewInfo.setPicPath(picPath);
		
		if (!StringUtils.isBlank(crewId)) {
			this.updateCrew(crewInfo);
		} else {
			this.crewInfoDao.add(crewInfo);
			
			//将所有权限赋给剧组
			this.crewAuthMapDao.addAllAuthToCrew(crewInfo.getCrewId());
			
			//试用项目，不允许导入，导出根据配置来
//			if(projectType == ProjectType.Trial.getValue()) {
//				this.crewAuthMapDao.deleteCrewImpExpAuth(crewInfo.getCrewId(), "import");
//				if(!allowExport) {
//					this.crewAuthMapDao.deleteCrewImpExpAuth(crewInfo.getCrewId(), "export");
//				}
//			}
		}
	}
	
	/**
	 * 查询用户申请的，还未审核过的剧组
	 * @param userId
	 * @return
	 */
	public List<CrewInfoModel> queryAuditingCrewByUserId(String userId) {
		return this.crewInfoDao.queryAuditingCrewByUserId(userId);
	}
	
	/**
	 * 查询用户所在的，未过期的剧组
	 * 除了返回剧组的基本信息外，还会返回用户在剧组中的是否有场记单权限，担任的职务信息以及用户在剧组中的状态
	 * @return
	 */
	public List<Map<String, Object>> queryUserCrewList(String userId) {
		return this.crewInfoDao.queryUserCrewList(userId);
	}
	
	/**
	 * 剧组的模糊查询
	 * 查询的结果中不包括以下类型的剧组：用户正在申请中的、用户已经在里面的（不管过期和无效）、过期的、无效的
	 * @param keyword 关键字
	 * @param userId 用户ID
	 * @param page 分页信息
	 */
	public List<Map<String, Object>> queryCrewInfoByKeyword(String keyword, String userId, Page page){
		return this.crewInfoDao.queryCrewInfoByKeyword(keyword, userId, page);
	}
	
	/**
	 * 查询用户所有剧组
	 * 用户在剧组中的状态是有效的
	 * 剧组是未过期的
	 */
	public List<CrewInfoModel> queryUserAllCrew(String userId){
		return this.crewInfoDao.queryUserAllCrew(userId);
	}
	
	/**
	 * 查询用户过期剧组
	 * 用户在剧组中的状态是有效的
	 * 剧组是过期的
	 */
	public List<Map<String, Object>> queryUserExpiredCrew(String userId){
		return this.crewInfoDao.queryUserExpiredCrew(userId);
	}
	
	/**
	 * 查询用户默认剧组
	 * 首先查询用户当前的默认剧组，
	 * 如果默认剧组为空，则查询用户其他的所有未过期的剧组，再把其中一个剧组设置成默认剧组
	 * @param userId
	 * @return	如果返回为空，则表示用户未加入任何剧组
	 * @throws Exception 
	 */
	public CrewInfoModel queryUserDefaultCrew(String userId) throws Exception {
		CrewInfoModel crewInfo = this.crewInfoDao.queryUserDefaultCrew(userId);
		
		//如果默认剧组为空，则查询该用户是否有其他剧组，并设置为默认剧组
		if(crewInfo == null) {
			List<CrewInfoModel> crewInfoList = this.crewInfoDao.queryUserAllCrew(userId);
			if (crewInfoList != null &&crewInfoList.size() > 0) {
				crewInfo = crewInfoList.get(0);
				this.userService.switchCrew(userId, crewInfo.getCrewId());
			}
		}
		
		return crewInfo;
	}
	
	/**
	 * 查询用户默认剧组
	 * 首先查询用户当前的默认剧组，
	 * 如果默认剧组为空，则查询用户其他的所有未过期的剧组，再把其中一个剧组设置成默认剧组
	 * @param userId
	 * @return	如果返回为空，则表示用户未加入任何剧组
	 * @throws Exception 
	 */
	public CrewInfoModel queryUserDefaultCrewForApp(String userId) throws Exception {
		CrewInfoModel crewInfo = this.crewInfoDao.queryUserDefaultCrewForApp(userId);
		
		//如果默认剧组为空，则查询该用户是否有其他剧组，并设置为默认剧组
		if(crewInfo == null) {
			List<CrewInfoModel> crewInfoList = this.crewInfoDao.queryUserAllCrewForApp(userId);
			if (crewInfoList != null &&crewInfoList.size() > 0) {
				crewInfo = crewInfoList.get(0);
				this.userService.switchCrew(userId, crewInfo.getCrewId());
			}
		}
		
		return crewInfo;
	}
	
	/**
	 * 生成剧组默认图片
	 * @param crewName
	 * @return	图片路径
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public String genDefaultPic(String crewName) throws FileNotFoundException, IOException {
		Properties properties = PropertiesUitls.fetchProperties("/config.properties");
		String baseStorePath = properties.getProperty("fileupload.path");
		String storePath = baseStorePath + "crew/";
		
		//剧组默认图片的存储路径
		String fileStoreName = UUIDUtils.getId() + ".png";
		String myStorePath = FileUtils.genStorepath(storePath);
		
		File folder = new File(myStorePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		
		//剧组默认图片的背景
		String backgroundPath = (String) properties.get("crew_default_img_path");
		
		Color c = new Color(52, 51, 56);
		PictureUtils.pressImgText(crewName, backgroundPath, myStorePath + fileStoreName, 4, 20, c, "黑体", true, "0.9");
		
		return myStorePath + fileStoreName;
	}
	
	/**
	 * 根据条件查询剧组
	 * @param userId 所要排除的用户id
	 */
	public List<Map<String,Object>> searchAllCrew(CrewInfoFilter filter, String userId){
		return this.crewInfoDao.searchAllCrew(filter, userId);
	}
	
	/**
	 * 查询未刷新过权限的过期剧组
	 * @return
	 */
	public List<CrewInfoModel> queryExpiredCrewNeedRefreshAuth() {
		return this.crewInfoDao.queryExpiredCrewNeedRefreshAuth();
	}
	
	/**
	 * 更新处理过的过期剧组的“是否已刷新权限”
	 * @param crewIds
	 */
	public void updateExpiredCrewStatus(String crewIds) {
		this.crewInfoDao.updateExpiredCrewStatus(crewIds);
	}
	
	/**
	 * 查询所有的试用剧组
	 * 只查询有效剧组
	 * @return
	 */
	public List<CrewInfoModel> queryAllTrialCrew() {
		return this.crewInfoDao.queryAllTrialCrew();
	}
	
	/**
	 * 普通用户新增剧组
	 * @param crewName	剧组名称
	 * @param crewType	剧组类型
	 * @param subject	题材
	 * @param recordNumber	备案号
	 * @param shootStartDate	开机时间
	 * @param shootEndDate	杀青时间
	 * @param company	制片
	 * @param director	导演
	 * @param scriptWriter	编剧
	 * @param mainactor	主演
	 * @param enterPassword	入组密码
	 * @param seriesNo	立项集数
	 * @param status 剧组状态
	 * @param coProduction 合拍协议，0：无，1：已签订
	 * @param budget 剧组执行预算
	 * @param coProMoney 合拍协议金额
	 * @param investmentRatio 我方投资比例
	 * @param remark 重要事项说明
	 * @param userInfoModel 用户信息
	 * @return
	 * @throws Exception 
	 */
	public CrewInfoModel saveCrewWithoutPic(String crewId, String crewName,
			Integer crewType, String subject, String recordNumber,
			String shootStartDate, String shootEndDate, String company,
			String director, String scriptWriter, String mainactor,
			String enterPassword, Integer seriesNo, Integer status,
			Integer coProduction, Double budget, Double coProMoney,
			Double investmentRatio, String remark, UserInfoModel userInfoModel, Boolean picFlag) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		//保存剧组信息
		CrewInfoModel crewInfo = new CrewInfoModel();
		if (!StringUtils.isBlank(crewId)) {//修改
			crewInfo = this.queryById(crewId);
			Integer oldCrewType = crewInfo.getCrewType();
			//电影/网大 与 电视剧/网剧切换时,判断剧本是否存在提示
			if(((crewType == CrewType.TVPlay.getValue() || crewType == CrewType.InternetTvplay.getValue()) 
					&& (oldCrewType == CrewType.Movie.getValue() || oldCrewType == CrewType.InternetMovie.getValue())) 
					|| ((crewType == CrewType.Movie.getValue() || crewType == CrewType.InternetMovie.getValue()) 
							&& (oldCrewType == CrewType.TVPlay.getValue() || oldCrewType == CrewType.InternetTvplay.getValue()))) {
				int scenarioNum = this.crewClearDao.queryRecordNum(crewId, "crewId", ViewContentModel.TABLE_NAME);
				if(scenarioNum != 0) {
					throw new IllegalArgumentException("更改剧组类型，会造成已有剧本及统计信息的格式混乱，请确认删除现有剧本后再修改类型！");
				}
			}			
		} else {//新增
			crewInfo.setCrewId(UUIDUtils.getId());
			crewInfo.setCreateTime(new Date());
			crewInfo.setStartDate(new Date());
			//剧组失效日期默认为3年后
			Calendar nowCal = Calendar.getInstance();
			nowCal.add(Calendar.YEAR, 1);
			nowCal.add(Calendar.MONTH, 6);
			crewInfo.setEndDate(nowCal.getTime());
		}
		
		crewInfo.setCrewName(crewName);
		crewInfo.setCrewType(crewType);
		
		if (!StringUtils.isBlank(shootStartDate)) {
			crewInfo.setShootStartDate(sdf.parse(shootStartDate));
		}else {
			crewInfo.setShootStartDate(null);
		}
		if (!StringUtils.isBlank(shootEndDate)) {
			crewInfo.setShootEndDate(sdf.parse(shootEndDate));
		}else {
			crewInfo.setShootEndDate(null);
		}
		crewInfo.setCompany(company);
		crewInfo.setSubject(subject);
		crewInfo.setDirector(director);
		crewInfo.setScriptWriter(scriptWriter);
		crewInfo.setMainactor(mainactor);
		crewInfo.setCreateUser(userInfoModel.getUserId());
		crewInfo.setRecordNumber(recordNumber);
		if(enterPassword != null) {
			crewInfo.setEnterPassword(enterPassword);
		}
		if(status != null) {
			crewInfo.setStatus(status);
		}
		if(seriesNo != null) {
			crewInfo.setSeriesNo(seriesNo);
		}
		if(coProduction != null) {
			crewInfo.setCoProduction(coProduction);
		}
		if(coProMoney != null) {
			crewInfo.setCoProMoney(coProMoney);
		}
		if(budget != null) {
			crewInfo.setBudget(budget);
		}
		if(investmentRatio != null) {
			crewInfo.setInvestmentRatio(investmentRatio);
		}
		if(StringUtil.isNotBlank(crewInfo.getRemark()) && !remark.equals(crewInfo.getRemark())) {
			crewInfo.setLastRemark(crewInfo.getRemark());
			crewInfo.setRemark(remark);
		} else {
			crewInfo.setRemark(remark);
		}
		//判断是否更新图片地址
		if (picFlag == null || !picFlag) {
			crewInfo.setPicPath(this.genDefaultPic(crewName));
		}
		
		if (!StringUtils.isBlank(crewId)) {
			this.updateCrew(crewInfo);
		} else {
			this.crewInfoDao.add(crewInfo);
			
			//将所有权限赋给剧组
			this.crewAuthMapDao.addAllAuthToCrew(crewInfo.getCrewId());
			
			//把其他剧组设置为非默认剧组
			this.crewUserMapDao.unDefaultUserCrew(userInfoModel.getUserId());
			
			//把用户加入到该剧组中，并设置为默认剧组
			CrewUserMapModel crewUserMap = new CrewUserMapModel();
			crewUserMap.setMapId(UUIDUtils.getId());
			crewUserMap.setCrewId(crewInfo.getCrewId());
			crewUserMap.setUserId(userInfoModel.getUserId());
			crewUserMap.setRoleId(Constants.ROLE_ID_ADMIN);
			crewUserMap.setType(Constants.CREW_TYPE_ADMIN);
			crewUserMap.setStatus(Constants.STATUS_OK);
			crewUserMap.setIfDefault(true);
			this.crewUserMapDao.add(crewUserMap);
			
			//设置用户为剧组管理员
			UserRoleMapModel userRoleMap = new UserRoleMapModel();
			userRoleMap.setMapId(UUIDUtils.getId());
			userRoleMap.setUserId(userInfoModel.getUserId());
			userRoleMap.setRoleId(Constants.ROLE_ID_ADMIN);
			userRoleMap.setCrewId(crewInfo.getCrewId());
			this.userRoleMapDao.add(userRoleMap);
			
			//把用户同步到剧组联系表
			this.crewContactService.syncFromUserInfo(crewInfo.getCrewId(), userInfoModel);
			
			//把剧组管理员的所有权限赋予用户
			this.userAuthMapDao.addByRoleId(crewInfo.getCrewId(), userInfoModel.getUserId(), Constants.ROLE_ID_ADMIN);
			
			//当前用户可建组次数减一
			int ubCreateCrewNum = userInfoModel.getUbCreateCrewNum();
			userInfoModel.setUbCreateCrewNum(--ubCreateCrewNum);
			this.userInfoDao.update(userInfoModel, "userId");
		}
		
		return crewInfo;
	}
	
	/**
	 * 跟新剧组照片信息
	 * @param model
	 * @throws Exception 
	 */
	public void updateCrewPicPath(CrewInfoModel model) throws Exception {
		this.crewInfoDao.update(model, "crewId");
	}
}
