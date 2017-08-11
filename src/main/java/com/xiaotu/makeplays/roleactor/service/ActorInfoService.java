package com.xiaotu.makeplays.roleactor.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.roleactor.dao.ActorInfoDao;
import com.xiaotu.makeplays.roleactor.model.ActorInfoModel;

/**
 * 演员信息
 * @author xuchangjian 2016-7-12下午4:34:39
 */
@Service
public class ActorInfoService {

	@Autowired
	private ActorInfoDao actorInfoDao;
	
	/**
	 * 查询剧组中所有的演员信息
	 * @param crewId
	 * @param roleType	演员类型（可为空）
	 * @param userId	评价人ID
	 * @return 演员信息，角色信息，角色戏量
	 */
	public List<Map<String, Object>> queryViewRoleActorInfo(String crewId, Integer roleType){
		return this.actorInfoDao.queryViewRoleActorInfo(crewId, roleType);
	}
	
	/**
	 * 根据ID查询演员信息
	 * @param actorId
	 * @return
	 * @throws Exception
	 */
	public ActorInfoModel queryById(String actorId) throws Exception {
		return this.actorInfoDao.queryById(actorId);
	}
}
