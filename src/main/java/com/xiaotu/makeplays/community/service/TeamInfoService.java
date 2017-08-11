package com.xiaotu.makeplays.community.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.community.dao.TeamInfoDao;
import com.xiaotu.makeplays.community.model.TeamInfoModel;
import com.xiaotu.makeplays.community.model.TeamPositionInfoModel;
import com.xiaotu.makeplays.community.model.TeamResumeMapModel;
import com.xiaotu.makeplays.mobile.server.community.dto.TeamInfoDto;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 组讯基本信息操作的service
 * @author wanrenyi 2016年9月1日下午5:24:35
 */
@Service
public class TeamInfoService {
	
	@Autowired
	private TeamInfoDao teamInfoDao;
	
	@Autowired
	private StoreInfoSrevice storeService;
	
	@Autowired
	private NewsInfoService newsInfoService;
	
	/**
	 * 根据单个组讯对象保存组讯信息
	 * @param teamInfo
	 * @return
	 * @throws Exception
	 */
	public String addTeamInfoByBean(TeamInfoModel teamInfo) throws Exception {
		String teamId = UUIDUtils.getId();
		teamInfo.setTeamId(teamId);
		
		//设置默认图片
		/*String storePicPath = teamInfo.getPicPath();
		if (StringUtils.isBlank(storePicPath)) {
			storePicPath = Constants.DEFAULT_CREW_PIC;
			teamInfo.setPicPath(storePicPath);
		}*/
		
		teamInfoDao.addTeamInfoByBean(teamInfo);
		
		return teamId;
	}
	
	/**
	 * 根据组讯对象更新组讯信息
	 * @param teamInfo
	 * @throws Exception
	 */
	public void updateTeamInfoByBean(TeamInfoModel teamInfo) throws Exception {
		teamInfoDao.updateTeamInfoByBean(teamInfo);
	}
	
	/**
	 * 批量删除组讯
	 * @param teamIds
	 * @param createUser
	 * @throws Exception
	 */
	public void deleteMulTeamInfoById(String teamIds, String createUser) throws Exception {
		String[] teamIdArray = teamIds.split(",");
		for(String teamId : teamIdArray) {
			deleteTeamInfoById(teamId, createUser);
		}
	}
	
	/**
	 * 根据组讯id删除一条组讯(此删除方法只是逻辑删除即将当前组讯的状态改为 2:不可用 状态,数据库中仍然存在这条记录);
	 * 删除组讯信息时,同时要删除组讯关联的职位信息
	 * 在删除之前,先根据条件确定要删除的信息是否存在
	 * @param teamId
	 * @throws Exception
	 */
	public void deleteTeamInfoById(String teamId, String creatUser) throws Exception {
		
		//根据组讯id判断当前组讯信息是否存在
		TeamInfoModel infoModel = teamInfoDao.getTeamInfoById(teamId);
		if (infoModel == null ) {
			throw new IllegalArgumentException("您要删除的组讯信息不存在,请刷新后在重试!");
		}
		
		if(StringUtils.isNotBlank(creatUser)) {
			//对当前删除的身份进行校验,只有本人才能删除组讯信息
			if (!infoModel.getCreateUser().equals(creatUser)) {
				throw new IllegalArgumentException("您不是发布者,无权删除!");
			}
		}
		//删除组讯信息表中的数据
		teamInfoDao.deleteTeamInfoById(teamId);
		
		//删除招聘职位信息表中的数据
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("teamId", teamId);
		//根据条件先查询出当前组讯信息是否存在
		List<TeamPositionInfoModel> list = teamInfoDao.queryPositionListByCondition(conditionMap);
		if (list != null && list.size() > 0) {
			teamInfoDao.deleteTeamPositionBycondition(conditionMap);
		}
		
		//删除当前组训投递人关系
		this.teamInfoDao.deleteTeamResumeMapByCondition(conditionMap);
	}
	
	/**
	 * 根据组讯的id查询组讯的详情信息
	 * @param teamId
	 * @return
	 * @throws IOException 
	 */
	public TeamInfoModel getTeamInfoById(String teamId) throws IOException {
		TeamInfoModel teamInfoModel = teamInfoDao.getTeamInfoById(teamId);
		//设置默认的开机和杀青时间不能返回null
		/*Date shootStartDate = teamInfoModel.getShootStartDate();
		Date currDate = new Date();
		if (shootStartDate == null) {
			teamInfoModel.setShootStartDate(currDate);
		}
		
		Date shootEndDate = teamInfoModel.getShootEndDate();
		if (shootEndDate == null) {
			teamInfoModel.setShootEndDate(currDate);
		}*/
		
		//设置下载宣传图片地址
		String picPath = teamInfoModel.getPicPath();
		if (StringUtils.isNotBlank(picPath)) {
			picPath = FileUtils.genPreviewPath(picPath);
		}
		teamInfoModel.setPicPath(picPath);
		return teamInfoModel;
	}
	

	
	/**
	 * 根据组讯的id查询组讯的详情信息
	 * @param teamId
	 * @return
	 * @throws IOException 
	 */
	public TeamInfoModel getTeamInfoByIdWithoutFormatPicPath(String teamId) throws IOException {
		TeamInfoModel teamInfoModel = teamInfoDao.getTeamInfoById(teamId);
		return teamInfoModel;
	}
	
	/**
	 * 判断当前剧组是否有人投递简历
	 * @param teamId
	 * @return
	 */
	public boolean isApplyTeam(String teamId, String userId) {
		//根据组讯id查询出所有投递简历的人员的id
		List<Map<String, Object>> list = teamInfoDao.getResumeUser(teamId);
		boolean flag = false;
		//判断是否投递当前剧组
		for (Map<String, Object> map : list) {
			String data = (String) map.get("userId");
			if (StringUtils.isNotBlank(data)) {
				if (data.equals(userId)) {
					flag = true;
					break;
				}
			}
		}
		
		return flag;
	}
	
	/**
	 * 根据组讯id判断是否收藏组讯
	 * @param teamId
	 * @param userId
	 * @return
	 */
	public boolean isStoreTeam(String teamId, String userId) {
		List<Map<String, Object>> list = storeService.getStoreUser(teamId);
		boolean flag = false;
		for (Map<String, Object> map : list) {
			String data = (String) map.get("userId");
			if (StringUtils.isNotBlank(data)) {
				if (data.equals(userId)) {
					flag = true;
					break;
				}
			}
		}
		
		return flag;
	}
	
	/**
	 * 根据条件分页查询组讯信息列表
	 * @param conditionMap
	 * @return
	 */
	public List<TeamInfoDto> getTeamInfoList(Map<String, Object> conditionMap, Page page) throws Exception{
		//查询出数据
		List<Map<String,Object>> listMap = teamInfoDao.queryTeamInfoList(conditionMap, page);
		//查询出每个组讯的收藏人数
		List<Map<String,Object>> storeList = storeService.getStoreUserCount();
		//查询出投递简历人数
		List<Map<String, Object>> resumeList = teamInfoDao.getTeamResumeCount();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<TeamInfoDto> resultList = new ArrayList<TeamInfoDto>();
		
		//拼接dto数据
		for(Map<String, Object> map : listMap) {
			TeamInfoDto teamInfoDto = new TeamInfoDto();
			//取出查询出的数据
			
			//组讯id
			String teamId = (String) map.get("teamId");
			teamInfoDto.setTeamId(teamId);
			//发布人id
			teamInfoDto.setCreateUser(map.get("createUser").toString());
			//发布人姓名
			if(StringUtils.isNotBlank((String) map.get("realName"))) {
				teamInfoDto.setRealName(map.get("realName").toString());
			} else {
				teamInfoDto.setRealName("");
			}
			//发布时间
			String createTime = sdf.format((Date)map.get("createTime"));
			teamInfoDto.setCreateTime(createTime);
			//剧组名称
			teamInfoDto.setCrewName((String)map.get("crewName"));
			
			//招聘职位名称
			teamInfoDto.setPositionName((String)map.get("positionName"));
			
			//剧组状态
			teamInfoDto.setStatus(Integer.parseInt(map.get("status") + ""));
			
			Date startDate = (Date) map.get("shootStartDate");
			//开机时间和距今开机天数
			Date currDate = new Date();
			if (startDate != null) {
				teamInfoDto.setShootStartDate(sdf.format(startDate));
				
				//距离开机天数
				//如果当前剧组已经开机，则将当前组讯的距今天数设置为 0
				if (startDate.getTime() <= currDate.getTime()) {
					teamInfoDto.setAgoDays(0);
				}else {
					//计算距今开机时间
					Integer days = (int) ((startDate.getTime() - currDate.getTime())/(1000*60*60*24));
					teamInfoDto.setAgoDays(days);
				}
			}
			//联系电话
			teamInfoDto.setPhoneNum((String)map.get("phoneNum")) ;
			//联系地址(筹备地址)
			teamInfoDto.setContactAddress((String)map.get("contactAddress"));
			
			//设置下载宣传图片地址
			String picPath = (String)map.get("picPath");
			if (StringUtils.isNotBlank(picPath)) {
				picPath = FileUtils.genPreviewPath(picPath);
			}
			//宣传图片地址
			teamInfoDto.setPicPath(picPath);
			//收藏人数默认设置为0
			teamInfoDto.setStoreCount(0);
			//投递简历人数默认设置为0
			teamInfoDto.setResumeCount(0);
			
			//拼接收藏人数
			for(Map<String, Object> storeMap : storeList) {
				String storeTeamId = (String) storeMap.get("teamId");
				if (StringUtils.isNotBlank(storeTeamId)) {
					if (teamId.equals(storeTeamId)) {
						//取出收藏的人数
						Integer count = ((Long) storeMap.get("count")).intValue();
						teamInfoDto.setStoreCount(count);
						break;
					}
				}
			}
			
			//拼接投递简历人数
			for(Map<String, Object> resumeMap : resumeList) {
				String resumeTeamId = (String) resumeMap.get("teamId");
				if (StringUtils.isNotBlank(resumeTeamId)) {
					if (teamId.equals(resumeTeamId)) {
						//取出收藏的人数
						Integer count = ((Long) resumeMap.get("count")).intValue();
						teamInfoDto.setResumeCount(count);
						break;
					}
				}
			}
			
			resultList.add(teamInfoDto);
		}
		
		return resultList;
	}
	
	/**
	 * 根据剧组的名称和剧组的类型判断要添加的组讯是否重复
	 * @param conditionMap
	 * @return
	 * @throws IllegalArgumentException 
	 */
	public boolean isRepeatTeam(Map<String, Object> conditionMap) throws IllegalArgumentException {
		
		if (conditionMap == null || conditionMap.size() == 0) {
			throw new IllegalArgumentException("请添加查询条件!");
		}
		
		List<Map<String, Object>> list = teamInfoDao.queryTeamInfoList(conditionMap, null);
		
		if (list != null && list.size() >0 ) {
			String teamId = (String) conditionMap.get("teamId");
			if (StringUtils.isNotBlank(teamId)) {
				
				//遍历根据剧组名称和类型取出的组讯列表，当前组讯是修改时，返回false
				for (Map<String, Object> map : list) {
					String  getTeamId = (String) map.get("teamId");
					if (getTeamId.equals(teamId)) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 *	根据对象信息保存组讯中招聘职位的信息
	 * @param teamPosition
	 * @return
	 * @throws Exception
	 */
	public String addTeamPositionByBean(TeamPositionInfoModel teamPosition) throws Exception {
		String positionId = UUIDUtils.getId();
		teamPosition.setPositionId(positionId);
		
		teamInfoDao.addTeamPositionByBean(teamPosition);
		return positionId;
	}
	
	/**
	 * 根据招聘职位信息对象更新招聘的职位信息
	 * @param teamPosition
	 * @throws Exception
	 */
	public void updateTeamPositionByBean(TeamPositionInfoModel teamPosition) throws Exception {
		teamInfoDao.updateTeamPositionByBean(teamPosition);
	}
	
	/**
	 * 保存招募职位信息
	 * @param toAddTeamPositionList
	 * @param toUpdateTeamPositionList
	 * @throws Exception 
	 */
	public void saveTeamPosition(List<TeamPositionInfoModel> toAddTeamPositionList, 
			List<TeamPositionInfoModel> toUpdateTeamPositionList) throws Exception {
		if(toAddTeamPositionList != null && toAddTeamPositionList.size() > 0) {
			for(TeamPositionInfoModel teamPosition : toAddTeamPositionList) {
				teamInfoDao.addTeamPositionByBean(teamPosition);
			}
		}
		if(toUpdateTeamPositionList != null && toUpdateTeamPositionList.size() > 0) {
			for(TeamPositionInfoModel teamPosition : toUpdateTeamPositionList) {
				teamInfoDao.updateTeamPositionByBean(teamPosition);
			}
		}
	}
	
	/**
	 * 根据条件删除组讯信息中的招聘职位信息(可以根据组讯id删除;也可以根据具体的职位id删除)
	 * 在进行删除之前要根据id,先判断是否存在这样的信息
	 * @param conditionMap key为positionId或则teamId value为其具体值
	 * @throws Exception 
	 */
	public void deleteTeamPositionByCondition(Map<String, Object> conditionMap) throws Exception {
		//根据条件先查询出当前组讯信息是否存在
		List<TeamPositionInfoModel> list = teamInfoDao.queryPositionListByCondition(conditionMap);
		if (list == null || list.size() == 0) {
			throw new java.lang.IllegalArgumentException("您要删除的职位信息不存在,请刷新后重试!");
		}
		
		if(StringUtils.isNotBlank((String) conditionMap.get("createUser"))) {
			TeamPositionInfoModel positionModel = list.get(0);
			if (!positionModel.getCreateUser().equals(conditionMap.get("createUser"))) {
				throw new IllegalArgumentException("您不是该条信息的发布者,无权删除该条信息!");
			}
		}
		
		//删除招聘职位信息
		teamInfoDao.deleteTeamPositionBycondition(conditionMap);
		
		//删除当前职位的投递简历信息
		this.teamInfoDao.deleteTeamResumeMapByCondition(conditionMap);
	}
	
	/**
	 * 根据条件查询组讯中职位列表
	 * @param conditionMap
	 * @return
	 */
	public List<TeamPositionInfoModel> queryTeamPositionListByConfition(Map<String, Object> conditionMap){
		List<TeamPositionInfoModel> list = teamInfoDao.queryPositionListByCondition(conditionMap);
		
		return list;
	}
	
	/**
	 * 根据组讯id查询出当前组讯中每条招聘职位的投递简历的人数
	 * @param teamId
	 * @return
	 */
	public List<Map<String, Object>> queryTeamPositionCount(String teamId){
		return teamInfoDao.getPositionResumeCount(teamId);
	}
	
	
	/**
	 * 添加投递简历信息
	 * @param model
	 * @throws Exception 
	 */
	public void addAppleTeamMap(TeamResumeMapModel model) throws Exception {
		String mapId = UUIDUtils.getId();
		model.setMapId(mapId);
		
		teamInfoDao.addApplyTeam(model);
	}
	
	/**
	 * 根据分页信息查询剧组的宣传图片(最新发布的图片)
	 * @param pagesize 每页显示条数
	 * @return
	 * @throws IOException 
	 */
	public List<Map<String, Object>> getTeamPic(Integer pagesize, Integer picType) throws IOException{
		if (pagesize == null || pagesize == 0) {
			throw new IllegalArgumentException("请输入要显示的条数!");
		}
		
		List<Map<String, Object>> picList = new ArrayList<Map<String, Object>>();
		Page page = new Page();
		page.setPagesize(pagesize);
		//结果list
		List<Map<String, Object>> list = null;
		
		//获取组讯图片
		if (picType == null || picType == 0) {
			list = this.teamInfoDao.getTeamPic(page);
			
			//查询出每个组讯的收藏人数
			List<Map<String,Object>> storeList = storeService.getStoreUserCount();
			//查询出投递简历人数
			List<Map<String, Object>> resumeList = teamInfoDao.getTeamResumeCount();
			
			for (Map<String, Object> teamMap : list) {
				//取出剧组id
				String teamPicId = (String) teamMap.get("teamId");
				teamMap.put("storeCount", 0);
				teamMap.put("resumeCount", 0);
				
				//拼接收藏人数
				for(Map<String, Object> storeMap : storeList) {
					String storeTeamId = (String) storeMap.get("teamId");
					if (StringUtils.isNotBlank(storeTeamId)) {
						if (teamPicId.equals(storeTeamId)) {
							//取出收藏的人数
							Integer count = ((Long) storeMap.get("count")).intValue();
							teamMap.put("storeCount", count);
							break;
						}
					}
				}
				
				//拼接投递简历人数
				for(Map<String, Object> resumeMap : resumeList) {
					String resumeTeamId = (String) resumeMap.get("teamId");
					if (StringUtils.isNotBlank(resumeTeamId)) {
						if (teamPicId.equals(resumeTeamId)) {
							//取出收藏的人数
							Integer count = ((Long) resumeMap.get("count")).intValue();
							teamMap.put("resumeCount", count);
							break;
						}
					}
				}
			}
		}else if (picType == 1) { //获取资讯图片
			//TODO
			
		}
		
		
		for (Map<String, Object> map : list) {
			String oldPath = (String) map.get("picPath");
			if (StringUtils.isNotBlank(oldPath)) {
				//读取配置文件，获得服务器地址
				Resource resource = new ClassPathResource("/config.properties");
				Properties props = PropertiesLoaderUtils.loadProperties(resource);
				
				String serverPath = (String) props.get("server.basepath");
				String downLoadPath = serverPath + "/fileManager/downloadFileByAddr?address=" + oldPath;
				map.remove("picPath");
				map.put("picPath", downLoadPath);
				picList.add(map);
			}
			
		}
		return picList;
	}
	
	/**
	 * 根据条件获取投递简历的记录
	 * @param conditionMap
	 * @return
	 */
	public List<TeamResumeMapModel> queryResumeRecoder(Map<String, Object> conditionMap){
		return teamInfoDao.queryResumeInfoByCondition(conditionMap);
	}
	
	/**
	 * 根据组讯ID查询投递简历详情
	 * @param teamId
	 * @return
	 */
	public List<Map<String, Object>> queryTeamInfoApply(String teamId, String positionId) {
		return this.teamInfoDao.queryTeamInfoApply(teamId, positionId);
	}
	
	/**
	 * 根据组讯ID查询收藏详情
	 * @param teamId
	 * @return
	 */
	public List<Map<String, Object>> queryTeamInfoStore(String teamId) {
		return this.teamInfoDao.queryTeamInfoStore(teamId);
	}
}
