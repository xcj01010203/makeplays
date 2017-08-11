package com.xiaotu.makeplays.mobile.server.community;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.community.model.SearchTeamInfoModel;
import com.xiaotu.makeplays.community.model.WorkExperienceInfoModel;
import com.xiaotu.makeplays.community.service.SearchTeamInfoService;
import com.xiaotu.makeplays.community.service.WorkExperienceInfoService;
import com.xiaotu.makeplays.mobile.server.common.BaseFacade;
import com.xiaotu.makeplays.mobile.server.community.dto.SearchTeamInfoDto;
import com.xiaotu.makeplays.mobile.server.community.dto.SearchUserInfoDto;
import com.xiaotu.makeplays.mobile.server.community.filter.SearchTeamFilter;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.service.UserService;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.Page;

/**
 * 寻组信息的cpntroller
 * @author wanrenyi 2016年9月5日下午4:33:19
 */
@Controller
@RequestMapping("/interface/searchTeam")
public class SearchTeamInfoFacade extends BaseFacade{

	Logger logger = LoggerFactory.getLogger(TeamInfoFacade.class);
	
	@Autowired
	private SearchTeamInfoService searchTeamService;
	
	@Autowired
	private WorkExperienceInfoService workService;
	
	@Autowired
	private UserService userService;
	
	/**
	 * 保存寻组信息
	 * @param createUser 用户id
	 * @param searchTeamId 寻组id 可为空
	 * @param likePositionName 意向职位
	 * @param currentStartDate 个人档期开始时间
	 * @param currentEndDate 个人档期借结束时间
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveSearchTeamInfo")
	public Object saveSearchTeamInfo(HttpServletRequest request,
			String createUser, String searchTeamId, String likePositionName,
			String currentStartDate, String currentEndDate,
			String likePositionId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			if (StringUtils.isBlank(createUser)) {
				throw new IllegalArgumentException("请登陆后在发布寻组信息!");
			}
			if (StringUtils.isBlank(likePositionName) || StringUtils.isBlank(likePositionId)) {
				throw new IllegalArgumentException("请选择意向职位信息!");
			}
			
			if (StringUtils.isBlank(currentStartDate)) {
				throw new IllegalArgumentException("请选择个人档期");
			}
			
			if (StringUtils.isBlank(currentEndDate)) {
				throw new IllegalArgumentException("请选择个人档期");
			}
			
			//封装参数
			SearchTeamInfoModel model = new SearchTeamInfoModel();
			model.setCreateUser(createUser);
			model.setLikePositionName(likePositionName);
			model.setLikePositionId(likePositionId);
			if (StringUtils.isNotBlank(currentStartDate)) {
				model.setCurrentStartDate(sdf.parse(currentStartDate));
			}
			
			if (StringUtils.isNotBlank(currentEndDate)) {
				model.setCurrentEndDate(sdf.parse(currentEndDate));
			}
			
			//保存之前根据创建人、职位意向、个人档期判断是否是重复数据
			checkRepeatData(model);
			
			if (StringUtils.isBlank(searchTeamId)) {
				searchTeamId = this.searchTeamService.addSearchTeamInfo(model);
				
				this.sysLogService.saveSysLogForApp(request, "新增寻组信息", this.getClientType(createUser), SearchTeamInfoModel.TABLE_NAME, searchTeamId, 1);
			}else {
				model.setSearchTeamId(searchTeamId);
				this.searchTeamService.updateSearchTeamInfo(model);
				
				this.sysLogService.saveSysLogForApp(request, "更新寻组信息", this.getClientType(createUser), SearchTeamInfoModel.TABLE_NAME, searchTeamId, 2);
			}
			
			resultMap.put("searchTeamId", searchTeamId);
			
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage());
			
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());

			this.sysLogService.saveSysLogForApp(request, "保存寻组信息失败：" + e.getMessage(), this.getClientType(createUser), SearchTeamInfoModel.TABLE_NAME, searchTeamId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知错误！");
		}
		
		return resultMap;
	}
	
	
	/**
	 * 判断当前寻组信息是否重复
	 * @param model
	 * @throws IOException 
	 */
	private void checkRepeatData(SearchTeamInfoModel model) throws IllegalArgumentException, IOException{
		//拼接查询条件
		SearchTeamFilter filter = new SearchTeamFilter();
		filter.setUserId(model.getCreateUser());
		filter.setLikePositionName(model.getLikePositionName());
		filter.setCurrentStartDate(model.getCurrentStartDate());
		filter.setCurrentEndDate(model.getCurrentEndDate());
		List<SearchTeamInfoDto> list = searchTeamService.getSearchTeamList(filter, null);
		
		if (list != null && list.size() > 0) {
			throw new IllegalArgumentException("当前档期的求职意向已经发布，请不要重复发布！");
		}
	}


	/**
	 * 删除寻组信息;只有自己才能删除自己的寻组信息
	 * @param createUser
	 * @param searchTeamId
	 */
	@ResponseBody
	@RequestMapping("/deleteSearchTeam")
	public Object deleteSearchTeam(HttpServletRequest request, String createUser, String searchTeamId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(createUser)) {
				throw new IllegalArgumentException("请登录后再进行删除!");
			}
			if (StringUtils.isBlank(searchTeamId)) {
				throw new IllegalArgumentException("请选择要删除的寻组信息!");
			}
			
			this.searchTeamService.deleteSearchTeamInfo(searchTeamId, createUser);
			
			this.sysLogService.saveSysLogForApp(request, "删除寻组信息", this.getClientType(createUser), SearchTeamInfoModel.TABLE_NAME, searchTeamId, 3);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage());
			
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());

			this.sysLogService.saveSysLogForApp(request, "删除寻组信息失败：" + e.getMessage(), this.getClientType(createUser), SearchTeamInfoModel.TABLE_NAME, searchTeamId, SysLogOperType.ERROR.getValue());
			throw new IllegalAccessError("未知错误！");
		}
		return resultMap;
	}
	
	/**
	 * 获取寻组信息列表
	 * @param page 分页参数对象
	 * @param userId 用户id
	 * @param teamId 组训id
	 * @param likePositionName 意向职位
	 * @param maxAge 最大年龄
	 * @param minAge 最小年龄
	 * @param sex 性别
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainSearchTeamList")
	public Object obtainSearchTeamList(Integer pageSize, Integer pageNo, String userId, String teamId, String likePositionName,
				Integer maxAge, Integer minAge, Integer sex, String positionId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		SearchTeamFilter filter = new SearchTeamFilter();
		
		try {
			if (pageSize == null) {
				pageSize = 20;
			}
			if (pageNo == null) {
				pageNo = 1;
			}
			
			//封装查询条件
			filter.setLikePositionName(likePositionName);
			filter.setPositionId(positionId);
			filter.setMaxAge(maxAge);
			filter.setTeamId(teamId);
			filter.setUserId(userId);
			filter.setMinAge(minAge);
			filter.setSex(sex);
			
			Page page = new Page();
			page.setPagesize(pageSize);
			page.setPageNo(pageNo);
			List<SearchTeamInfoDto> searchTeamList = this.searchTeamService.getSearchTeamList(filter, page);
			resultMap.put("searchTeamList", searchTeamList);
			resultMap.put("pageCount", page.getPageCount());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			
			throw new IllegalArgumentException("未知错误！");
		}
		return resultMap;
	}
	
	/**
	 * 根据寻组id查询寻组的详细信息
	 * @param searchTeamId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainSearchUserInfo")
	public Object obtainSearchUserInfo(String searchTeamId, String userId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			if (StringUtils.isBlank(searchTeamId)) {
				throw new IllegalArgumentException("请选择要查看的寻组!");
			}
			if (StringUtils.isBlank(userId)) {
				throw new IllegalArgumentException("请查看要查看的用户!");
			}
			
			//获取寻组中的个人信息
			SearchUserInfoDto userInfoDto = this.searchTeamService.getSearchUserInfoByTeamId(searchTeamId);
			//获取用户的工作经历列表
			List<Map<String, Object>> workList = this.workService.getWorkExListByUserId(userId);
			resultMap.put("userInfoDto", userInfoDto);
			resultMap.put("workList", workList);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(),ie);
			
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			
			throw new IllegalArgumentException("未知错误");
		}
		
		return resultMap;
	}
	
	/**
	 * 根据用户id查询用户信息以及用户的工作经历
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/obtainUserInfoAndExperience")
	public Object obtainUserInfoAndExperience(HttpServletRequest request, String userId){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(userId)) {
				throw new IllegalArgumentException("请查看要查看的用户!");
			}
			//根据条件查询用户详细信息
			UserInfoModel userInfoModel = this.userService.queryById(userId);
			String picPath = userInfoModel.getBigImgUrl();
			if (StringUtils.isNotBlank(picPath)) {
				picPath = FileUtils.genPreviewPath(picPath);
			} else {
				Resource resource = new ClassPathResource("/config.properties");
				Properties props = PropertiesLoaderUtils.loadProperties(resource);
				String serverPath = (String) props.get("server.basepath");
				picPath = serverPath + Constants.DEFAULT_USER_PIC;
			}
			userInfoModel.setBigImgUrl(picPath);
			//查询用户的工作经历
			List<Map<String, Object>> workList = this.workService.getWorkExListByUserId(userId);
			
			resultMap.put("userInfo", userInfoModel);
			resultMap.put("workList", workList);
			
			this.sysLogService.saveSysLogForApp(request, "查询人员详细信息", userInfoModel.getClientType(), 
					UserInfoModel.TABLE_NAME + "," + WorkExperienceInfoModel.TABLE_NAME, null, 0);
		}catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage());
			
			throw new IllegalArgumentException(ie.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			
			throw new IllegalArgumentException("未知错误！");
		}
		
		return resultMap;
	}
	
}
