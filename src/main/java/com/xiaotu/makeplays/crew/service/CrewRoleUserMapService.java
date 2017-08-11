package com.xiaotu.makeplays.crew.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.crew.dao.CrewRoleUserMapDao;
import com.xiaotu.makeplays.crew.model.CrewRoleUserMapModel;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 场景角色与用户关联
 * @author xuchangjian 2016-5-27上午10:40:39
 */
@Service
public class CrewRoleUserMapService {

	@Autowired
	private CrewRoleUserMapDao crewRoleUserMapDao;
	
	/**
	 * 根据剧组、用户ID查询关联关系
	 * @param crewId
	 * @param userId
	 * @return
	 */
	public List<CrewRoleUserMapModel> queryByCrewUserId (String crewId, String userId) {
		return this.crewRoleUserMapDao.queryByCrewUserId(crewId, userId);
	}
	
	/**
	 * 查询用户和指定场景角色的关联关系
	 * @param crewId
	 * @param userId
	 * @param viewRoleId
	 * @return
	 * @throws Exception
	 */
	public CrewRoleUserMapModel queryByCrewUserRoleId(String crewId, String userId, String viewRoleId) throws Exception {
		return this.crewRoleUserMapDao.queryByCrewUserRoleId(crewId, userId, viewRoleId);
	}
	
	/**
	 * 新增一条记录
	 * @param crewRoleUserMap
	 * @throws Exception 
	 */
	public void addOne(CrewRoleUserMapModel crewRoleUserMap) throws Exception {
		this.crewRoleUserMapDao.add(crewRoleUserMap);
	}
	
	/**
	 * 删除一条记录
	 * @param mapId
	 * @throws Exception 
	 */
	public void deleteOne(String mapId) throws Exception {
		this.crewRoleUserMapDao.deleteOne(mapId, "mapId", CrewRoleUserMapModel.TABLE_NAME);
	}
	
	/**
	 * 取消用户在剧组中与场景角色的所有关联
	 * @param crewId
	 * @param userId
	 */
	public void deleteByCrewUserId(String crewId, String userId) {
		this.crewRoleUserMapDao.deleteByCrewUserId(crewId, userId);
	}
	
	/**
	 * 保存用户和场景角色的关联关系
	 * 批量保存
	 * @param crewId
	 * @param userId
	 * @param viewRoleIds	场景角色ID，多个以逗号隔开
	 * @throws Exception 
	 */
	public void saveActorUserCrewRoleRelation(String crewId, String userId, String viewRoleIds) throws Exception {
		this.crewRoleUserMapDao.deleteByCrewUserId(crewId, userId);
		
		String[] viewRoleIdArray = viewRoleIds.split(",");
		
		for (String viewRoleId : viewRoleIdArray) {
			CrewRoleUserMapModel crewRoleUserMap = new CrewRoleUserMapModel();
			
			crewRoleUserMap.setMapId(UUIDUtils.getId());
			crewRoleUserMap.setUserId(userId);
			crewRoleUserMap.setViewRoleId(viewRoleId);
			crewRoleUserMap.setCrewId(crewId);
			
			this.addOne(crewRoleUserMap);
		}
	}
}
