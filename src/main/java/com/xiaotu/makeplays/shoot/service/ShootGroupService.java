package com.xiaotu.makeplays.shoot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.shoot.dao.ShootGroupDao;
import com.xiaotu.makeplays.shoot.model.ShootGroupModel;

@Service
public class ShootGroupService {

	@Autowired
	private ShootGroupDao shootGroupDao;
	
	/**
	 * 根据剧组ID查找对应的拍摄分组信息
	 * @param crewId
	 * @return
	 */
	public List<ShootGroupModel> queryManyByCrewId(String crewId) {
		return this.shootGroupDao.queryManyByCrewId(crewId);
	}
	
	/**
	 * 通过拍摄组ID查找拍摄分组信息
	 * @param groupId
	 * @return
	 */
	public ShootGroupModel queryOneByGroupId (String groupId) {
		return this.shootGroupDao.queryOneByGroupId(groupId);
	}
	
	/**
	 * 保存分组
	 * @param group
	 * @throws Exception
	 */
	public void saveShootGroup(ShootGroupModel group) throws Exception{
		
		shootGroupDao.add(group);
	}
	
	/**
	 * 根据拍摄组名称查找拍摄分组信息
	 * @param groupName
	 * @return
	 * @throws Exception 
	 */
	public ShootGroupModel queryOneByGroupName(String groupName) throws Exception {
		return this.shootGroupDao.queryOneByGroupName(groupName);
	}
}
