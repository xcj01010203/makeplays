package com.xiaotu.makeplays.community.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.community.dao.SearchTeamInfoDao;
import com.xiaotu.makeplays.community.dao.WorkExperienceInfoDao;
import com.xiaotu.makeplays.community.model.SearchTeamInfoModel;
import com.xiaotu.makeplays.community.model.WorkExperienceInfoModel;
import com.xiaotu.makeplays.crew.dao.CrewInfoDao;
import com.xiaotu.makeplays.mobile.server.community.dto.SearchTeamInfoDto;
import com.xiaotu.makeplays.mobile.server.community.dto.SearchUserInfoDto;
import com.xiaotu.makeplays.mobile.server.community.filter.SearchTeamFilter;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 寻组信息操作的service
 * @author wanrenyi 2016年9月5日下午3:47:55
 */
@Service
public class SearchTeamInfoService {

	@Autowired
	private SearchTeamInfoDao searchTeamDao;
	
	@Autowired
	private WorkExperienceInfoDao workDao;
	
	@Autowired
	private CrewInfoDao crewInfoDao;
	
	/**
	 * 保存寻组信息
	 * @param model
	 * @return
	 * @throws Exception 
	 */
	public String addSearchTeamInfo(SearchTeamInfoModel model) throws Exception {
		String searchId = UUIDUtils.getId();
		model.setSearchTeamId(searchId);
		this.searchTeamDao.addSearchTeamInfo(model);
		
		return searchId;
	}
	
	/**
	 * 更新寻组信息
	 * @param model
	 * @throws Exception
	 */
	public void updateSearchTeamInfo(SearchTeamInfoModel model) throws Exception {
		this.searchTeamDao.updateSearchTeamInfo(model);
	}
	
	/**
	 * 批量删除寻组信息
	 * @param searchTeamIds
	 * @param createUser
	 * @throws Exception
	 */
	public void deleteMulSearchTeamInfo(String searchTeamIds, String createUser) throws Exception {
		String[] searchTeamIdArr = searchTeamIds.split(",");
		for(String searchTeamId : searchTeamIdArr) {
			this.deleteSearchTeamInfo(searchTeamId, createUser);
		}
	}
	
	/**
	 * 根据寻组id删除寻组信息
	 * @param searchTeamId
	 * @throws Exception 
	 */
	public void deleteSearchTeamInfo(String searchTeamId, String creatUser) throws Exception {
		//删除之前先进行查询,如果要删除的信息不存在,提示错误信息
		SearchTeamInfoModel searchTeamInfoModel = this.searchTeamDao.getSearchTeamInfoById(searchTeamId);
		if (searchTeamInfoModel == null) {
			throw new IllegalArgumentException("当前寻组信息不存在,不能进行删除!");
		}
		
		if(StringUtils.isNotBlank(creatUser)) {
			//判断查询出来的用户id是否是传递过来的用户id,只有自己才能删除自己的寻组信息
			if (!searchTeamInfoModel.getCreateUser().equals(creatUser)) {
				throw new IllegalArgumentException("您不能删除别人的寻组信息!");
			}
		}
		
		this.searchTeamDao.deleteSearchTeamInfo(searchTeamId);
	}
	
	/**
	 * 根据条件获取寻组信息列表
	 * @param filter
	 * @return
	 * @throws IOException 
	 */
	public List<SearchTeamInfoDto> getSearchTeamList(SearchTeamFilter filter, Page page) throws IOException{
		List<SearchTeamInfoDto> resultList = new ArrayList<SearchTeamInfoDto>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		//根据传递的招聘职位的id是进行投递当前职位人员信息查询
		if (filter !=null) {
			String positionId = filter.getPositionId();
			if (StringUtils.isNotBlank(positionId)) {
				List<Map<String, Object>> userList = searchTeamDao.getUserInfoByPositionId(filter,positionId, page);
				for (Map<String, Object> userMap : userList) {
					SearchTeamInfoDto infoDto = new SearchTeamInfoDto();
					if (userMap != null) {
						//拼接数据
						infoDto.setAge((Integer)userMap.get("age"));
						infoDto.setCreateTime(sdf.format((Date)userMap.get("createTime")));
						infoDto.setRealName((String)userMap.get("realName"));
						infoDto.setSex((Integer)userMap.get("sex"));
						infoDto.setPhone((String)userMap.get("phone"));
						
						String picPath = (String)userMap.get("bigImgUrl");
						if (StringUtils.isNotBlank(picPath)) {
							picPath = FileUtils.genPreviewPath(picPath);
						} else {
							Resource resource = new ClassPathResource("/config.properties");
							Properties props = PropertiesLoaderUtils.loadProperties(resource);
							String serverPath = (String) props.get("server.basepath");
							picPath = serverPath + Constants.DEFAULT_USER_PIC;
						}
						infoDto.setPicPath(picPath);
						
						infoDto.setUserId((String)userMap.get("userId"));
						//拼接工作经历
						if (StringUtils.isNotBlank((String)userMap.get("userId"))) {
							List<WorkExperienceInfoModel> experList = this.workDao.getWorkExperListByUserId((String)userMap.get("userId"));
							//只取第一条工作经历
							if (experList != null && experList.size() > 0) {
								WorkExperienceInfoModel workModel = experList.get(0);
								if (!StringUtils.isBlank(workModel.getPositionName())) {
									infoDto.setWorkExperience("<<" + workModel.getCrewName() + ">>--" + workModel.getPositionName());
								} else {
									infoDto.setWorkExperience("<<" + workModel.getCrewName() + ">>");
								}
							}
							
						}
						resultList.add(infoDto);
					}
				}
				
				return resultList;
			}
		}
		
		//下边是查询其它寻组信息
		//根据条件查询出数据列表
		List<Map<String,Object>> searchTeamList = this.searchTeamDao.getSearchTeamList(filter, page);
		for(Map<String, Object> map : searchTeamList) {
			SearchTeamInfoDto infoDto = new SearchTeamInfoDto();
			if (map != null) {
				
				//拼接数据
				infoDto.setAge((Integer)map.get("age"));
				infoDto.setCreateTime(sdf.format((Date)map.get("createTime")));
				infoDto.setLikePositionId((String) map.get("likePositionId"));
				infoDto.setLikePositionName((String)map.get("likePositionName"));
				infoDto.setRealName((String)map.get("realName"));
				infoDto.setSex((Integer)map.get("sex"));
				infoDto.setPhone((String)map.get("phone"));
				if (map.get("currentStartDate") != null) {
					infoDto.setCurrentStartDate(sdf.format((Date)map.get("currentStartDate")));
				}
				if (map.get("currentEndDate") != null) {
					infoDto.setCurrentEndDate(sdf.format((Date)map.get("currentEndDate")));
				}
				
				String picPath = (String)map.get("bigImgUrl");
				if (StringUtils.isNotBlank(picPath)) {
					picPath = FileUtils.genPreviewPath(picPath);
				} else {
					Resource resource = new ClassPathResource("/config.properties");
					Properties props = PropertiesLoaderUtils.loadProperties(resource);
					String serverPath = (String) props.get("server.basepath");
					picPath = serverPath + Constants.DEFAULT_USER_PIC;
				}
				infoDto.setPicPath(picPath);
				infoDto.setUserId((String)map.get("createUser"));
				infoDto.setSearchTeamId((String)map.get("searchTeamId"));
				
				//拼接工作经历
				if (StringUtils.isNotBlank((String)map.get("createUser"))) {
					List<WorkExperienceInfoModel> experList = this.workDao.getWorkExperListByUserId((String)map.get("createUser"));
					if (null != experList && experList.size()>0) {
						//只取第一条工作经历
						if (experList != null && experList.size() > 0) {
							WorkExperienceInfoModel workModel = experList.get(0);
							if (!StringUtils.isBlank(workModel.getPositionName())) {
								infoDto.setWorkExperience("<<" + workModel.getCrewName() + ">>--" + workModel.getPositionName());
							} else {
								infoDto.setWorkExperience("<<" + workModel.getCrewName() + ">>");
							}
						}
					}else {
						//查询该人员在当前剧组中的职务信息
						//查询用户在系统中所在的剧组
						List<Map<String, Object>> crewList = this.crewInfoDao.queryUserCrewList((String)map.get("createUser"));
						if (null != crewList && crewList.size()>0) {
							Map<String, Object> crewMap = crewList.get(0);
							int status = (Integer) crewMap.get("crewUserStatus");
							if (status == 1) {
								String positionName = (String)crewMap.get("roleNames");
								if (!StringUtils.isBlank(positionName)) {
									infoDto.setWorkExperience("<<" + crewMap.get("crewName") + ">>--" + positionName);
								} else {
									infoDto.setWorkExperience("<<" + crewMap.get("crewName") + ">>");
								}
							}
						}
					}
					
				}
				resultList.add(infoDto);
			}
		}
		
		return resultList;
	}
	
	/**
	 * 根据寻组信息的id查询寻组详细信息中的个人信息
	 * @param searchTeamId 寻组信息的id 必填
	 * @return
	 * @throws IOException 
	 */
	public SearchUserInfoDto getSearchUserInfoByTeamId(String searchTeamId) throws IOException {
		if (StringUtils.isBlank(searchTeamId)) {
			throw new IllegalArgumentException("请选择要查看的寻组!");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		SearchUserInfoDto userInfoDto = new SearchUserInfoDto();
		//查询数据
		List<Map<String, Object>> list = this.searchTeamDao.getSearchUserInfoById(searchTeamId);
		if (list == null || list.size() == 0) {
			throw new IllegalArgumentException("该寻组信息已经失效,请查看其它寻组!");
		}
		
		//封装数据
		Map<String, Object> map = list.get(0);
		if (map != null) {
			userInfoDto.setAge((Integer) map.get("age"));
			
			String picPath = (String)map.get("bigImgUrl");
			if (StringUtils.isNotBlank(picPath)) {
				picPath = FileUtils.genPreviewPath(picPath);
			} else {
				Resource resource = new ClassPathResource("/config.properties");
				Properties props = PropertiesLoaderUtils.loadProperties(resource);
				String serverPath = (String) props.get("server.basepath");
				picPath = serverPath + Constants.DEFAULT_USER_PIC;
			}
			userInfoDto.setBigImgUrl(picPath);
			
			Date currentStartDate = (Date)map.get("currentStartDate");
			if (currentStartDate != null) {
				userInfoDto.setCurrentStartDate(sdf.format((Date)map.get("currentStartDate")));
			}else {
				userInfoDto.setCurrentStartDate("");
			}
			
			Date currentEndDate = (Date)map.get("currentEndDate");
			if (currentEndDate != null) {
				userInfoDto.setCurrentEndDate(sdf.format((Date)map.get("currentEndDate")));
			}else {
				userInfoDto.setCurrentEndDate("");
			}
			userInfoDto.setLikePositionName((String)map.get("likePositionName"));
			userInfoDto.setPhone((String)map.get("phone"));
			userInfoDto.setProfile((String)map.get("profile"));
			userInfoDto.setRealName((String)map.get("realName"));
			userInfoDto.setSex((Integer)map.get("sex"));
		}
		
		return userInfoDto;
	}
}
