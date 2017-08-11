package com.xiaotu.makeplays.roleactor.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.roleactor.dao.ActorInfoDao;
import com.xiaotu.makeplays.roleactor.dao.EvaluateInfoDao;
import com.xiaotu.makeplays.roleactor.dao.EvaluateTagMapDao;
import com.xiaotu.makeplays.roleactor.dao.EvtagInfoDao;
import com.xiaotu.makeplays.roleactor.dao.ViewRoleDao;
import com.xiaotu.makeplays.roleactor.model.ActorInfoModel;
import com.xiaotu.makeplays.roleactor.model.EvaluateInfoModel;
import com.xiaotu.makeplays.roleactor.model.EvaluateTagMapModel;
import com.xiaotu.makeplays.roleactor.model.EvtagInfoModel;
import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;
import com.xiaotu.makeplays.roleactor.model.constants.EvaluateStatus;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 演员评价信息
 * @author xuchangjian 2016-7-19上午11:08:52
 */
@Service
public class EvaluateService {
	
	@Autowired
	private EvaluateInfoDao evaluateInfoDao;
	
	@Autowired
	private ActorInfoDao actorInfoDao;
	
	@Autowired
	private ViewRoleDao viewRoleDao;
	
	@Autowired
	private EvaluateTagMapDao evaluateTagMapDao;
	
	@Autowired
	private EvtagInfoDao evtagInfoDao;

	/**
	 *  查询演职员评价信息详情
	 */
	public List<EvaluateInfoModel> queryEvtagList(String fromUserName,String toUserName,String roleName,String crewId)throws Exception{
		return evaluateInfoDao.queryEvaluateList(fromUserName, toUserName,roleName,crewId);
	}
	/**
	 * 添加演职员评价信息
	 */
	public void addEvaluate(EvaluateInfoModel evaluate)throws Exception{
		evaluateInfoDao.add(evaluate);
	}
	/**
	 * 添加演职员评价与标签关联关系
	 */
	public void addEvaluateTagMap(EvaluateTagMapModel evtagMap)throws Exception{
		evaluateInfoDao.add(evtagMap);
	}
	/**
	 * 删除演职员评价与标签关联关系
	 */
	public void delEvaluateTagMap(String evaluateId)throws Exception{
		evaluateInfoDao.delEvaluateTagMap(evaluateId);
	}
	/**
	 * 修改演职员评价信息
	 */
	public void updateEvaluate(EvaluateInfoModel evaluate)throws Exception{
		evaluateInfoDao.update(evaluate, "evaluateId");
	}
	
	/**
	 * 添加演员评价信息
	 * 该方法还会保存评价中的评价标签信息
	 * @param evaluate
	 * @param tagMapList
	 * @throws Exception 
	 */
	public void addEvaluate(EvaluateInfoModel evaluate, List<EvaluateTagMapModel> tagMapList) throws Exception {
		this.evaluateInfoDao.add(evaluate);
		this.evaluateTagMapDao.addBatch(tagMapList, EvaluateTagMapModel.class);
	}
	
	/**
	 * @param sessionUserInfo 登录用户
	 * @param score	得分
	 * @param blackTagIds	黑标签ID，多个用逗号隔开
	 * @param redTagIds	红标签ID，多个用逗号隔开
	 * @param comment	评语
	 * @return
	 * @throws Exception 
	 */
	public void evaluteActor(UserInfoModel sessionUserInfo, String crewId, String actorId,
			Integer score, String evatagIds, String comment)
			throws Exception {

		ActorInfoModel actorInfo = this.actorInfoDao.queryById(actorId);
		ViewRoleModel viewRole = this.viewRoleDao.queryByActorId(actorId);

		//保存评价信息
		EvaluateInfoModel evaluateInfo = new EvaluateInfoModel();
		evaluateInfo.setEvaluateId(UUIDUtils.getId());
		evaluateInfo.setFromUserName(sessionUserInfo.getRealName());
		evaluateInfo.setFromMpUserId(sessionUserInfo.getUserId());
		evaluateInfo.setToUserName(actorInfo.getActorName());
		evaluateInfo.setToMpUserId(actorInfo.getActorId());
		if (viewRole != null) {
			evaluateInfo.setRoleName(viewRole.getViewRoleName());
		}
		evaluateInfo.setScore(score);
		evaluateInfo.setComment(comment);
		evaluateInfo.setCreateTime(new Date());
		evaluateInfo.setEvaluateTime(new Date());
		evaluateInfo.setStatus(EvaluateStatus.Finished.getValue());
		evaluateInfo.setCrewId(crewId);
		
		this.evaluateInfoDao.add(evaluateInfo);
		
		
		//保存评价和标签的关联关系
		if (!StringUtils.isBlank(evatagIds)) {
			String[] evatagIdArray = evatagIds.split(",");
			for (String evatagId : evatagIdArray) {
				EvaluateTagMapModel evaTagMap = new EvaluateTagMapModel();
				evaTagMap.setMapId(UUIDUtils.getId());
				evaTagMap.setEvaluateId(evaluateInfo.getEvaluateId());
				evaTagMap.setTagId(evatagId);
				
				this.evaluateTagMapDao.add(evaTagMap);
			}
		}
	}
	
	/**
	 * 演职员评价标签信息
	 * 
	 * @param crewId
	 * @return
	 */
	public List<EvtagInfoModel> queryEvtagList(String crewId) {
		return this.evtagInfoDao.queryEvtagList(crewId);
	}
}
