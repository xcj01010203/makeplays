package com.xiaotu.makeplays.finance.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.crew.model.FinanceAccountGroupMapModel;
import com.xiaotu.makeplays.crew.model.FinanceAccountGroupModel;
import com.xiaotu.makeplays.finance.dao.FinanceAccountGroupDao;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * @类名：FinanceAccountGroupService.java
 * @作者：李晓平
 * @时间：2016年11月3日 上午11:50:16 
 * @描述：财务科目预算分组信息
 */
@Service
public class FinanceAccountGroupService {

	@Autowired
	private FinanceAccountGroupDao financeAccountGroupDao;
	
	/**
	 * 判断分组名称是否已存在
	 * @param groupName
	 * @return
	 */
	public boolean isExistGroupName(String groupId, String groupName) {
		boolean isExist = false;
		Map<String, Object> map = this.financeAccountGroupDao.queryFinanAccoGroByGroupName(groupId, groupName);
		if(map != null && !map.isEmpty()) {
			if(!(map.get("num") + "").equals("0")) {
				isExist = true;
			}
		}
		return isExist;
	}
	
	/**
	 * 根据剧组ID查询所有的自定义分组
	 * @param crewId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> queryAllFinanceAccountGroup(String crewId) {
		return this.financeAccountGroupDao.queryAllFinanAccoGroByCrewId(crewId);
	}
	
	/**
	 * 查询单个财务科目预算分组信息
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> queryOneFinanceAccountGroup(String groupId) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("group", this.financeAccountGroupDao.queryFinanAccoGroByGroupId(groupId));
		map.put("groupMap", this.financeAccountGroupDao.queryFinanAccoGroMapByGroupId(groupId));
		return map;
	}
	
	/**
	 * 保存财务科目预算分组信息
	 * @param crewId
	 * @param groupId
	 * @param groupName
	 * @param subjectId
	 */
	public void saveFinanceAccountGroup(String crewId, String groupId,
			String groupName, String subjectId) throws Exception {
		//财务科目预算分组信息
		FinanceAccountGroupModel financeAccoutGroup = new FinanceAccountGroupModel();
		
		boolean isAdd = false; //是否新增
		if(StringUtils.isNotBlank(groupId)) {
			financeAccoutGroup = this.financeAccountGroupDao.queryFinanAccoGroByGroupId(groupId);
		} else {
			financeAccoutGroup.setGroupId(UUIDUtils.getId());
			
			isAdd = true;
		}
		financeAccoutGroup.setCrewId(crewId);
		financeAccoutGroup.setGroupName(groupName);
		
		if(isAdd) {
			this.financeAccountGroupDao.add(financeAccoutGroup);
		} else {
			this.financeAccountGroupDao.update(financeAccoutGroup, "groupId");
			
			//删除原有财务科目预算分组与财务科目关联关系
			this.financeAccountGroupDao.deleteOne(groupId, "groupId", FinanceAccountGroupMapModel.TABLE_NAME);
		}
		
		//财务科目预算分组与财务科目关联
		String[] subjectIds = subjectId.split(",");
		for(String oneSubjectId : subjectIds) {
			FinanceAccountGroupMapModel financeAccountGroupMap = new FinanceAccountGroupMapModel();
			financeAccountGroupMap.setMapId(UUIDUtils.getId());
			financeAccountGroupMap.setCrewId(crewId);
			financeAccountGroupMap.setGroupId(financeAccoutGroup.getGroupId());
			financeAccountGroupMap.setAccountId(oneSubjectId);
			this.financeAccountGroupDao.add(financeAccountGroupMap);
		}
	}
	
	/**
	 * 删除财务科目预算分组信息
	 * @param groupId
	 * @throws Exception
	 */
	public void deleteOneFinanceAccountGroup(String groupId) throws Exception{
		//删除财务科目预算分组与财务科目关联关系
		this.financeAccountGroupDao.deleteOne(groupId, "groupId", FinanceAccountGroupMapModel.TABLE_NAME);
		//删除财务科目预算分组信息
		this.financeAccountGroupDao.deleteOne(groupId, "groupId", FinanceAccountGroupModel.TABLE_NAME);
	}
}
