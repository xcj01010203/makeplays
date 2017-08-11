package com.xiaotu.makeplays.view.service;

import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.roleactor.dao.ViewRoleDao;
import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;
import com.xiaotu.makeplays.scenario.controller.dto.ScenarioViewDto;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.view.dao.ViewTempDao;
import com.xiaotu.makeplays.view.model.ViewTempModel;
import com.xiaotu.makeplays.view.model.constants.ViewTempDataType;

/**
 * 操作场景零时变量的service
 * @author xuchangjian
 */
@Service
public class ViewTempService {
	
	@Autowired
	private ViewTempDao crewTempDao;
	
	@Autowired
	private ViewRoleDao viewRoleDao;

	/**
	 * 根据供用户选择“跳过”或“替换”的数据新增临时数据对象
	 * @param skipOrReplaceData
	 * @return ID拼接字符串
	 * @throws Exception 
	 */
	public String addSkipOrReplaceData (List<ScenarioViewDto> skipOrReplaceData, String crewId) throws Exception {
		
		String ids = "";
		
		List<ViewTempModel> sceneTempList = new ArrayList<ViewTempModel>();
		//供用户选择“跳过”或“替换”的数据（存入临时表中）
		for (ScenarioViewDto scenarioDto : skipOrReplaceData) {
			String id = UUIDUtils.getId();
			ids += id + ",";
			ViewTempModel sceneTemp = new ViewTempModel();
			sceneTemp.setViewTempId(id);
			sceneTemp.setCrewId(crewId);
			sceneTemp.setSeriesNo(scenarioDto.getSeriesNo());
			sceneTemp.setViewNo(scenarioDto.getViewNo().toUpperCase());
			sceneTemp.setAtmosphere(scenarioDto.getAtmosphere());
			sceneTemp.setSite(scenarioDto.getSite());
			sceneTemp.setTitle(scenarioDto.getTitle());
			sceneTemp.setContent(scenarioDto.getContent());
			sceneTemp.setRemark(scenarioDto.getRemark());
			sceneTemp.setSeason(scenarioDto.getSeason());
			sceneTemp.setShootLocation(scenarioDto.getShootLocation());
			sceneTemp.setPropsNames(scenarioDto.getProps());
			sceneTemp.setClothesNames(scenarioDto.getClothes());
			sceneTemp.setMakeupNames(scenarioDto.getMakeups());
			sceneTemp.setViewType(scenarioDto.getViewType());
			sceneTemp.setMainContent(scenarioDto.getMainContent());
			sceneTemp.setPageCount(scenarioDto.getPageCount());
			sceneTemp.setSpecialProps(scenarioDto.getSpecialProps());
			sceneTemp.setShootTime(scenarioDto.getShootTime());
			sceneTemp.setCommercialImplants(scenarioDto.getCommercialImplants());
			
			//根据待定演员名称从角色表中判断当前演员的角色类型，保存到场景零时表中
			List<String> confirmRoleNameList = scenarioDto.getToConfirmRoleNameList();
			//主演信息表
			List<String> majorRoleList = new ArrayList<String>();
			//特约信息表
			List<String> guestRoleList = new ArrayList<String>();
			//群众演员信息表
			List<String> massRoleList = new ArrayList<String>();
			
			if (confirmRoleNameList != null && confirmRoleNameList.size()>0) {
				for (String roleName : confirmRoleNameList) {
					Map<String, Object> conditionMap = new HashMap<String, Object>();
					conditionMap.put("viewRoleName", roleName);
					conditionMap.put("crewId", crewId);
					List<ViewRoleModel> list = this.viewRoleDao.queryManyByMutiCondition(conditionMap, null);
					if (list == null || list.size() == 0) { //角色表中不存在当前角色信息，则当做主演信息保存
						majorRoleList.add(roleName);
					}else {
						//根据在角色表中的角色类型来判断属于什么角色
						ViewRoleModel model = list.get(0);
						if (null != model) {
							//角色类型
							int viewRoleType = model.getViewRoleType();
							if (viewRoleType == 1) {
								//主演
								majorRoleList.add(roleName);
							}else if (viewRoleType == 2) {
								//特约
								guestRoleList.add(roleName);
							}else if (viewRoleType == 3) {
								//群演
								massRoleList.add(roleName);
							}
						}
					}
				}
			}
			
			//保存数据
			scenarioDto.setMajorRoleNameList(majorRoleList);
			scenarioDto.setGuestRoleNameList(guestRoleList);
			scenarioDto.setMassRoleNameList(massRoleList);
			
			//主要演员
			String roleNames = null;
			if (scenarioDto.getMajorRoleNameList() != null && scenarioDto.getMajorRoleNameList().size() > 0) {
				List<String> majorRoleNameList = scenarioDto.getMajorRoleNameList();
				Collections.sort(majorRoleNameList, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						CollationKey key1 = Collator.getInstance().getCollationKey(o1.toLowerCase());// 要想不区分大小写进行比较用o1.toString().toLowerCase()
		        		CollationKey key2 = Collator.getInstance().getCollationKey(o2.toLowerCase());
		        		return key1.compareTo(key2);
					}
				});
				
				for (String roleName : majorRoleNameList) {
					if (StringUtils.isBlank(roleNames)) {
						roleNames = "";
						roleNames += roleName;
					} else {
						roleNames = roleNames + "," + roleName;
					}
					
				}
				sceneTemp.setRoleNames(roleNames.toString());
			}
			
			//特约演员
			String guestRoleNames = null;
			if (scenarioDto.getGuestRoleNameList() != null && scenarioDto.getGuestRoleNameList().size() > 0) {
				List<String> guestRoleNameList = scenarioDto.getGuestRoleNameList();
				Collections.sort(guestRoleNameList, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						CollationKey key1 = Collator.getInstance().getCollationKey(o1.toLowerCase());// 要想不区分大小写进行比较用o1.toString().toLowerCase()
		        		CollationKey key2 = Collator.getInstance().getCollationKey(o2.toLowerCase());
		        		return key1.compareTo(key2);
					}
				});
				
				for (String guestRoleName : guestRoleNameList) {
					if (StringUtils.isBlank(guestRoleNames)) {
						guestRoleNames = "";
						guestRoleNames += guestRoleName;
					} else {
						guestRoleNames = guestRoleNames + "," + guestRoleName;
					}
					
				}
				sceneTemp.setGuestNames(guestRoleNames.toString());
			}
			
			//群众演员
			String massRoleNames = null;
			if (scenarioDto.getMassRoleNameList() != null && scenarioDto.getMassRoleNameList().size() > 0) {
				List<String> messRoleNameList = scenarioDto.getMassRoleNameList();
				Collections.sort(messRoleNameList, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						CollationKey key1 = Collator.getInstance().getCollationKey(o1.toLowerCase());// 要想不区分大小写进行比较用o1.toString().toLowerCase()
		        		CollationKey key2 = Collator.getInstance().getCollationKey(o2.toLowerCase());
		        		return key1.compareTo(key2);
					}
				});
				
				for (String massRoleName : messRoleNameList) {
					if (StringUtils.isBlank(massRoleNames)) {
						massRoleNames = "";
						massRoleNames += massRoleName;
					} else {
						massRoleNames = massRoleNames + "," + massRoleName;
					}
				}
				sceneTemp.setMassNames(massRoleNames.toString());
			}
			
			sceneTemp.setFirstLocation(scenarioDto.getFirstLocation());
			sceneTemp.setSecondLocation(scenarioDto.getSecondLocation());
			sceneTemp.setThirdLocation(scenarioDto.getThirdLocation());
			sceneTemp.setDataType(ViewTempDataType.SkipOrReplaceData.getValue());
			sceneTempList.add(sceneTemp);
			//删除零时表中的数据
			this.deleteManyByCrewId(crewId, sceneTemp.getSeriesNo(), sceneTemp.getViewNo());
		}
		
		if (sceneTempList != null && sceneTempList.size() > 0) {
			crewTempDao.addBatch(sceneTempList, ViewTempModel.class);
//			this.crewTempDao.addMany(sceneTempList);
		}
		
		return ids;
	}
	
	/**
	 * 根据剧组ID和临时数据类型查询临时数据
	 * @param crewId
	 * @param sceneTempDataType
	 * @return
	 */
	public List<ViewTempModel> queryManyByCrewId(String crewId, int sceneTempDataType) {
		return this.crewTempDao.queryManyByCrewId(crewId, sceneTempDataType);
	}
	
	/**
	 * 根据集次、场次、剧组ID查找临时表中的指定类型的数据
	 * @param seriesNo	集次
	 * @param viewNo	场次
	 * @param crewId	剧组ID
	 * @return
	 */
	public ViewTempModel queryOneBySeriesViewCrewId (int seriesNo, String viewNo, String crewId) {
		return this.crewTempDao.queryOneBySeriesViewCrewId(seriesNo, viewNo, crewId);
	}
	
	/**
	 * 根据剧本ID删除临时表中指定类型的数据
	 * @param crewId 剧组ID
	 * @param sceneTempDataType	数据类型
	 * @return
	 */
	public void deleteManyByCrewId (String crewId, int sceneTempDataType) {
		this.crewTempDao.deleteManyByCrewId(crewId, sceneTempDataType);
	}
	
	/**
	 * 根据集次、场次、剧组ID删除临时表中的指定类型的数据
	 * @param seriesNo	集次
	 * @param viewNo	场次
	 * @param crewId	剧组ID
	 * @param sceneTempDataType	数据类型
	 * @return
	 */
	public void deleteOneByseriesViewCrewId (int seriesNo, String viewNo, String crewId, int sceneTempDataType) {
		this.crewTempDao.deleteOneByseriesViewCrewId(seriesNo, viewNo, crewId, sceneTempDataType);
	}
	
	/**
	 * 根据剧组ID删除临时表中的数据
	 * @param crewId 剧组ID
	 * @return
	 */
	public void deleteManyByCrewId (String crewId, int seriesNo, String viewNo) {
		this.crewTempDao.deleteManyByCrewId(crewId, seriesNo, viewNo);
	}
}
