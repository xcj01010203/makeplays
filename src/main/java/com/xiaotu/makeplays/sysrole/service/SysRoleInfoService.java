package com.xiaotu.makeplays.sysrole.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.authority.dao.AuthorityDao;
import com.xiaotu.makeplays.authority.dao.RoleAuthMapDao;
import com.xiaotu.makeplays.authority.model.RoleAuthMapModel;
import com.xiaotu.makeplays.sysrole.model.SysroleInfoModel;
import com.xiaotu.makeplays.user.dao.SysRoleInfoDao;
import com.xiaotu.makeplays.user.dao.UserInfoDao;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.Page;

@Service
public class SysRoleInfoService {

	@Autowired
	private SysRoleInfoDao sysRoleInfoDao;
	
	@Autowired
	private RoleAuthMapDao roleAuthMapDao;
	
	@Autowired
	private AuthorityDao authorityDao;
	
	@Autowired
	private UserInfoDao userInfoDao;
	
	/**
	 * 查询角色翻页
	 * @param page
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public List<SysroleInfoModel> getRoleByPage(Page page) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		
		List<SysroleInfoModel> list = sysRoleInfoDao.queryRoleByPage(page);
		return list;
	}
	
	
	public void addRole(SysroleInfoModel roleInfo) throws Exception{
		sysRoleInfoDao.add(roleInfo);
	}
	
	
	public void updateRole(SysroleInfoModel roleInfo) throws Exception{
		sysRoleInfoDao.updateWithNull(roleInfo, "roleId");
	}
	
	
	/**
	 * 通过ID查询系统角色信息
	 * @param roleId
	 * @return
	 * @throws Exception
	 */
	public SysroleInfoModel queryById(String roleId) throws Exception{
		
		return sysRoleInfoDao.queryById(roleId);
	}	
	
	public List<RoleAuthMapModel> queryRoleAuth(String roleId){
		
		List<RoleAuthMapModel> list =  roleAuthMapDao.queryByRoleId(roleId, Constants.SYS_DEFULT_CREW_ID);
		
		return list;
	}
	
	/**
	 * 查询角色信息
	 * 该方法返回的是map集合，会顺带查询出演员角色的其他信息，比如：所属剧组名称
	 * 以后如果角色添加新的关联字段，可以对该方法进行扩展
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> queryRoleWithCrewNameByPage(Page page){
		return this.sysRoleInfoDao.queryRoleWithCrewNameByPage(page);
	}
	
	/**
	 * 根据演员角色id获取演员姓名
	 */
	public String getRoleNameById(String roleId){
		return this.sysRoleInfoDao.getRoleNameById(roleId);
	}
	 /**
     * 删除角色
     * @param roleId
     * @throws Exception
     */
	public  String delRole(String roleId,String crewId) throws Exception{
		try {
			this.roleAuthMapDao.deleteRoleAuth(roleId, crewId);//删除角色与权限关联
			this.sysRoleInfoDao.delRole(roleId);//删除角色
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return "false";
    }

	/**
	 * 查询角色名是否已存在
	 * @param roleName
	 * @return
	 * @throws Exception
	 */
	public String getRoleName(String roleName, String roleId) throws Exception {
		return this.sysRoleInfoDao.getRoleName(roleName, roleId);
	}
	
	/**
	 * 查询角色最大的Id
	 * @return
	 * @throws Exception
	 */
   	public Integer getRoleMax() throws Exception{
   		return sysRoleInfoDao.getRoleMax();
    }
   	
   	/**
	 * 查询角色最大的顺序
	 * @return
	 * @throws Exception
	 */
	public Integer getRoleOrderNoMax(String parentId) throws Exception {
		return sysRoleInfoDao.getRoleOrderNoMax(parentId);
	}
   	
   	/**
	 * 获取系统部门信息
	 * @param crewId	剧组ID
	 * @param needManager	是否需要管理员信息
	 * @return
	 */
   	public List<SysroleInfoModel> queryByCrewId(String crewId) {
   		return this.sysRoleInfoDao.queryByCrewId(crewId);
   	}
   	
   	/**
   	 * 根据父ID查询职务信息
   	 * @param crewId
   	 * @param parentIds
   	 * @return
   	 */
   	public List<SysroleInfoModel> queryByParentIds(String crewId, String parentIds) {
   		return this.sysRoleInfoDao.queryByParentIds(crewId, parentIds);
   	}
   	
   	/**
   	 * 获取剧组下所有人员的所属小组信息
   	 * @param crewId
   	 * @return 小组ID  小组名称  小组下人员数量
   	 */
   	public List<Map<String, Object>> queryCrewGroupInfo (String crewId) {
   		return this.sysRoleInfoDao.queryCrewGroupInfo(crewId);
   	}
   	
   	/**
   	 * 获取剧组总人数
   	 * @param crewId
   	 * @return 人员数量
   	 */
   	public List<Map<String, Object>> queryCrewUserNum(String crewId) {
   		return this.sysRoleInfoDao.queryCrewUserNum(crewId);
   	}
   	
   	/**
	 * 查询用户在指定剧组中担任的职务
	 * 该查询返回的数据中包含对应职务的小组的名称
	 * @param crewId
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> queryByCrewUserId(String crewId, String userId) {
		return this.sysRoleInfoDao.queryByCrewUserId(crewId, userId);
	}
	
	/**
	 * 根据职务ID查询职务列表
	 * 该查询还会返回职务对应的部门信息
	 * @param roleIds
	 * @return
	 */
	public List<Map<String, Object>> queryByIdsWithParentInfo(String roleIds) {
		return this.sysRoleInfoDao.queryByIdsWithParentInfo(roleIds);
	}
	
	/**
	 * 更新角色表顺序
	 */
	public void updateRoleSequence(String crewId, String ids){
		String idArray[] = ids.split(",");
		for (int i = 0; i < idArray.length; i++) {
			this.sysRoleInfoDao.updateRoleSequence(idArray[i], i);
		}
	}
	
	/**
	 * 查询角色是否已被引用
	 * @param roleId
	 * @return
	 */
	public Integer getCountOfUserRoleMap(String roleId){
		return this.sysRoleInfoDao.getCountOfUserRoleMap(roleId);
	}
}
