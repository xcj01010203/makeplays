package com.xiaotu.makeplays.user.service;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.inhotelcost.dao.InhotelCostInfoDao;
import com.xiaotu.makeplays.user.controller.filter.ContactFilter;
import com.xiaotu.makeplays.user.dao.ContactSysroleMapDao;
import com.xiaotu.makeplays.user.dao.CrewContactDao;
import com.xiaotu.makeplays.user.dao.SysRoleInfoDao;
import com.xiaotu.makeplays.user.model.ContactSysroleMapModel;
import com.xiaotu.makeplays.user.model.CrewContactModel;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.model.constants.IdentityCardType;
import com.xiaotu.makeplays.user.model.constants.MealType;
import com.xiaotu.makeplays.user.model.constants.Sex;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 剧组联系表的service
 * @author xuchangjian 2016-5-20上午9:52:05
 */
@Service
public class CrewContactService {
	
	@Autowired
	private CrewContactDao crewContactDao;
	
	@Autowired
	private SysRoleInfoDao sysRoleInfoDao;
	
	@Autowired
	private ContactSysroleMapService contactSysroleMapService;
	
	@Autowired
	private ContactSysroleMapDao contactSysroleMapDao;
	
	@Autowired
	private InhotelCostInfoDao inhotelCostInfoDao;
	
	
	/**
	 * 保存剧组联系人信息
	 * @param crewId
	 * @param userId
	 * @param contactId	联系人ID
	 * @param contactName	联系人姓名
	 * @param phone	电话
	 * @param sex	性别
	 * @param identityCardType	证件类型
	 * @param identityCardNumber	证件号码
	 * @param sysRoleIds	职务ID，多个以逗号隔开
	 * @param enterDate	入组日期（yyyy-MM-dd）
	 * @param leaveDate	离组日期（yyyy-MM-dd）
	 * @param remark	备注
	 * @param mealType	餐别
	 * @param hotel	宾馆
	 * @param roomNumber	房间号
	 * @param extension	分机号
	 * @param checkInDate	入住日期（yyyy-MM-dd）
	 * @param checkoutDate 退房日期（yyyy-MM-dd）
	 * @param ifOpen	是否公开到组
	 * @return
	 * @throws Exception 
	 */
	public CrewContactModel saveCrewContactInfo(String crewId, String userId, String contactId, 
			String contactName, String phone, Integer sex, 
			Integer identityCardType, String identityCardNumber, String sysRoleIds,
			String enterDate, String leaveDate, String remark, Integer mealType, Boolean ifOpen) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		CrewContactModel crewContact = new CrewContactModel();
		if (!StringUtils.isBlank(contactId)) {
			crewContact = this.crewContactDao.queryById(contactId);
		} else {
			crewContact.setContactId(UUIDUtils.getId());
			this.crewContactDao.makeSequenceAddOne(crewId);
			crewContact.setSequence(1);
		}
		
		//保存联系表基本信息
		crewContact.setCrewId(crewId);
		crewContact.setContactName(contactName);
		crewContact.setPhone(phone);
		crewContact.setSex(sex);
		if (identityCardType != null) {
			crewContact.setIdentityCardType(identityCardType);
		}
		crewContact.setIdentityCardNumber(identityCardNumber);
		if (!StringUtils.isBlank(enterDate)) {
			crewContact.setEnterDate(sdf.parse(enterDate));
		} else {
			crewContact.setEnterDate(null);
		}
		if (!StringUtils.isBlank(leaveDate)) {
			crewContact.setLeaveDate(sdf.parse(leaveDate));
		} else {
			crewContact.setLeaveDate(null);
		}
		crewContact.setRemark(remark);
		crewContact.setMealType(mealType);
		crewContact.setIfOpen(ifOpen);
		
		if (!StringUtils.isBlank(contactId)) {
			this.crewContactDao.updateWithNull(crewContact, "contactId");
		} else {
			this.crewContactDao.add(crewContact);
		}
		
		//删除联系表和系统角色的关联关系
		this.contactSysroleMapService.deleteByContactId(crewId, crewContact.getContactId());
		
		//保存联系表和系统角色的关联关系
		if (!StringUtils.isBlank(sysRoleIds)) {
			List<ContactSysroleMapModel> contactSysroleMapList = new ArrayList<ContactSysroleMapModel>();
			
			String[] sysRoleIdArray = sysRoleIds.split(",");
			for (String roleId : sysRoleIdArray) {
				ContactSysroleMapModel map = new ContactSysroleMapModel();
				map.setId(UUIDUtils.getId());
				map.setCrewId(crewId);
				map.setContactId(crewContact.getContactId());
				map.setSysroleId(roleId);
				
				contactSysroleMapList.add(map);
			}
			
			this.contactSysroleMapDao.addBatch(contactSysroleMapList, ContactSysroleMapModel.class);
		}
		
		return crewContact;
	}
	
	/**
	 * 根据高级查询条件查询
	 * @param contactFilterModel
	 * @return 联系人基本信息，职务ID（多个以逗号隔开），职务名称（部门-职务，多个以逗号隔开）
	 */
	public List<Map<String, Object>> queryContactListByAdvanceCondition(String crewId, ContactFilter contactFilterModel, Page page){
		return this.crewContactDao.queryContactListByAdvanceCondition(crewId, contactFilterModel, page);
	}
	
	/**
	 * 查询剧组联系表
	 * 该查询是按照部门职务的纬度查询用户信息
	 * 返回剧组中每个职务下的用户信息
	 * 如果用户在剧组中担任两个职务，则该查询将会返回两条用户记录
	 * @param crewId
	 * @param contactFilter
	 * @return 
	 */
	@Deprecated
	public List<Map<String, Object>> queryContactListGroup(String crewId, ContactFilter contactFilter){
		return this.crewContactDao.queryContactListGroup(crewId, contactFilter);
	}
	
	/**
	 * 删除剧组联系信息
	 * @param contactId
	 * @param crewId
	 * @return
	 * @throws Exception 
	 * @throws IllegalArgumentException 
	 */
	public void deleteCrewContact(String crewId, String contactId) throws Exception {
		//删除联系人和角色的关联
		this.contactSysroleMapService.deleteByContactId(crewId, contactId);
		//删除联系人基本信息
		this.crewContactDao.deleteOne(contactId, "contactId", CrewContactModel.TABLE_NAME);
	}
	
	/**
	 * 删除剧组联系信息
	 * @param crewId
	 * @param userId
	 * @throws Exception
	 */
	public void deleteCrewContactByCrewUserId(String crewId, String userId) throws Exception {
		CrewContactModel crewContact = this.crewContactDao.queryByCrewUserId(crewId, userId);
		if(crewContact != null) {
			String contactId = crewContact.getContactId();
			this.deleteCrewContact(crewId, contactId);
		}
	}
	
	/**
	 *  更新剧组联系表顺序
	 * @param crewId
	 * @param contactIds
	 * @throws IllegalArgumentException 
	 */
	public void updateContactSequence(String crewId,String contactIds) throws IllegalArgumentException{
		String id[] = contactIds.split(",");
		for(int i=0; i<id.length; i++) {
			this.crewContactDao.updateContactSequence(crewId, i+1, id[i]);
		}
	}
	
	/**
	 * 根据用户手机号查找联系人信息
	 * @param crewId
	 * @param phone
	 * @return
	 * @throws Exception 
	 */
	public CrewContactModel queryByPhone(String crewId, String contactId, String phone) throws Exception {
		return this.crewContactDao.queryByPhone(crewId, contactId, phone);
	}
	
	/**
	 * 同步用户信息到联系表
	 * 触发的功能点有：
	 * 新增用户剧组关联关系（web、手机同意申请入组，web主动加人到剧组）、用户基本信息修改、用户职务信息修改
	 * @param userInfoModel
	 * @throws Exception 
	 */
	public void syncFromUserInfo(String crewId, UserInfoModel userInfoModel) throws Exception {
		
		/*
		 * 首先根据userId查找联系人，如果找到，直接更新
		 * 如果没找到，然后根据phone查找联系人，如果找到，直接更新，需要更新userId
		 * 如果没找到，则直接新增联系人
		 * 
		 * 主要同步的字段：
		 * 姓名、手机号、部门、职务、userId
		 * 
		 * 新增时，默认公开到组，修改时，公开到组字段值不变
		 */
		
		String userId = userInfoModel.getUserId();
		String realName = userInfoModel.getRealName();
		String phone = userInfoModel.getPhone();
		Integer sex = userInfoModel.getSex();
		
		CrewContactModel crewContact = this.crewContactDao.queryByCrewUserId(crewId, userId);
		if (crewContact == null) {
			crewContact = this.crewContactDao.queryByPhone(crewId, null, phone);
		}
		
		if (crewContact != null) {
			crewContact.setContactName(realName);
			crewContact.setPhone(phone);
			crewContact.setUserId(userId);
			crewContact.setSex(sex);
			this.crewContactDao.update(crewContact, "contactId");
		} else {
			crewContact = new CrewContactModel();
			crewContact.setContactId(UUIDUtils.getId());
			crewContact.setPhone(phone);
			crewContact.setUserId(userId);
			crewContact.setSex(sex);
			crewContact.setIfOpen(true);
			
			crewContact.setCrewId(crewId);
			crewContact.setContactName(realName);
			this.crewContactDao.add(crewContact);
		}
		
		//保存联系人和剧组角色的关联关系
		List<Map<String, Object>> roleList = this.sysRoleInfoDao.queryByCrewUserId(crewId, userId);
		
		List<ContactSysroleMapModel> contactSysroleMapList = new ArrayList<ContactSysroleMapModel>();
		for (Map<String, Object> roleInfo : roleList) {
			String roleId = (String) roleInfo.get("roleId");
			
			ContactSysroleMapModel map = new ContactSysroleMapModel();
			map.setId(UUIDUtils.getId());
			map.setCrewId(crewId);
			map.setContactId(crewContact.getContactId());
			map.setSysroleId(roleId);
			
			contactSysroleMapList.add(map);
		}
		
		this.contactSysroleMapService.deleteByContactId(crewId, crewContact.getContactId());
		this.contactSysroleMapDao.addBatch(contactSysroleMapList, ContactSysroleMapModel.class);
	}
	
	/**
	 * 设置剧组联系人是否公开到组
	 * @param contactId	
	 * @param ifOpen
	 */
	public void setIfOpen(String contactId, Boolean ifOpen) {
		this.crewContactDao.setIfOpen(contactId, ifOpen);
	}
	
	/**
	 * 根据通告单时间查询通告单下的联系人
	 * @param noticeTimeId
	 * @return
	 */
	public List<Map<String, Object>> queryByNotictTimeId(String crewId, String noticeTimeId) {
		return this.crewContactDao.queryByNotictTimeId(crewId, noticeTimeId);
	}

	/**
	 * xlh
	 * 保存excel导入剧组联系人信息
	 * @param crewContactInfoMap  excel中剧组联系人信息
	 * @param crewId 剧组id
	 * @param importColoum 导入的列名
	 * @param submitNums  1,2  提交次数  第一次提交的时候需要判断是否覆盖原有的数据，如果第二次提交就说明同意覆盖原有数据
	 * @return
	 * @throws Exception 
	 */
	public List<Map<String,Object>> saveCrewContactInfoFromExcel(Map<String, Object> crewContactInfoMap,final String crewId,final Map<String, String> importColoum,Boolean isCover) throws Exception{
		//excel表中的手机号是否在数据库中存在
		List<Map<String,Object>> isRepeatPhoneNumber = new ArrayList<Map<String,Object>>();
		
		
		if(crewContactInfoMap.isEmpty()){
			throw new java.lang.IllegalArgumentException("要导入数据为空");
		}
		
		Set<String> keys = crewContactInfoMap.keySet();
		Iterator<String> it = keys.iterator();
		//遍历多个sheet页整理数据插入数据库
		while(it.hasNext()){
			String key = it.next();
			List<ArrayList<String>> list = (List<ArrayList<String>>)crewContactInfoMap.get(key);
			if(list==null||list.size()<2){//为空或者只有一行（标题）则不保存
				continue;
			}
			//获取第一行，标题行
			List<String> titleList =list.get(0);
			//删除标题行剩下的就全部是数据了
			List<String> coloumnNameList = list.get(1);
			
			list.remove(0);
			list.remove(0);
			
			
			Set<String> columnSet = importColoum.keySet(); 
			Iterator<String> it1 = columnSet.iterator();
	//校验  标题  和列名
			boolean hasTitle = true;
			boolean columnNameIsTrue = true;
			while(it1.hasNext()){
				String realColumnName = it1.next();
				if(hasTitle){
					for(String str:titleList){
						if(StringUtils.isNotBlank(str)){
							if(str.equals(realColumnName)){
								hasTitle = false;
							}
						}
					}
				}
				if(columnNameIsTrue){
					boolean flag = false;
					for(String column:coloumnNameList){
						if(StringUtils.isNotBlank(column)){
							if(column.equals(realColumnName)){
								flag = true;
								break;
							}
						}
					}
					columnNameIsTrue = flag;
				}
			}
			
			if(!hasTitle){
				throw new IllegalArgumentException("请添加标题");
			}
			if(!columnNameIsTrue){
				throw new IllegalArgumentException("表格列名有误");
			}
			
			//将list转为map(需要导入的数据)
			List<Map<String, Object>> datalist = listToMap(list, importColoum);
			
			
			//表格自身数据重复
			Map<String,Map<String, Object>> contactDataMap = new HashMap<String,Map<String,Object>>();
			
			for(Map<String, Object> tMap:datalist){
				String cname = tMap.get("contactName")!=null?tMap.get("contactName").toString():"";
				if(StringUtils.isBlank(cname)){
					Integer rownumber = tMap.get("rownumber")!=null?Integer.valueOf(tMap.get("rownumber").toString()):0;
					throw new IllegalArgumentException("第"+rownumber+"行联系人名称为空");
				}
				Map<String, Object> cnameInMap = contactDataMap.get(cname);
				
				if(cnameInMap==null){
					cnameInMap = tMap;
				}
				
				contactDataMap.put(cname, cnameInMap);
			}
			
			//查库对比，如果需要导入的手机号存在返回通知：让用户选择是否更新
			List<Map<String, Object>> resu = crewContactDao.queryContactListByAdvanceCondition(crewId,null,null);
			
			
			List<Map<String, Object>> insertList = new ArrayList<Map<String,Object>>();//插入数据
			List<Map<String, Object>> delList = new ArrayList<Map<String,Object>>();//删除数据
			List<Map<String, Object>> allDataList = new ArrayList<Map<String,Object>>();//表格所有数据
			
			
			Iterator<String> cNameIt = contactDataMap.keySet().iterator();
			while(cNameIt.hasNext()){
				String cNameKey = cNameIt.next();
				Map<String, Object> excelDataMap = contactDataMap.get(cNameKey);
				String excelPhone = excelDataMap.get("phone")+"";
				boolean isRepeat = false;
				for(Map<String, Object> backResultMap:resu){
					Object resultContactName = backResultMap.get("contactName");
					String resultPhone = (String) backResultMap.get("phone");
					//比较联系人姓名
					if(resultContactName!=null&&cNameKey!=null&&resultContactName.equals(cNameKey)){
						delList.add(backResultMap);
						isRepeat = true;
						continue;
					}
					//比较电话号码
					if(resultPhone!=null&&excelPhone!=null&&resultPhone.equals(excelPhone)){
						delList.add(backResultMap);
						isRepeat = true;
						continue;
					}
				}
				if(!isRepeat){
					insertList.add(contactDataMap.get(cNameKey));
				}
				allDataList.add(contactDataMap.get(cNameKey));
			}
			
			
			
			/**
			 * 是否覆盖重复数据
			 * 			是：删除原有数据（delList），保存所有excel数据（datalist）
			 * 			否：保存非重复数据（insertList）
			 */
			List<Map<String, Object>> saveData = null;
			if(isCover){
				//删除原有数据  删除剧组信息已经 人员部门（tab_crew_contact）、职务信息对照表（tab_contact_sysrole_map）
				List<Object[]> batchArgs = new ArrayList<Object[]>();
				List<Object[]> delInhotelArgs = new ArrayList<Object[]>();
				for(Map<String, Object> tMap:delList){
					Object[] args = new Object[2];
					args[0]=tMap.get("contactId").toString();
					args[1] = crewId;
					batchArgs.add(args);
					
					delInhotelArgs.add(new Object[]{tMap.get("contactId").toString()});
				}
				//删除 tab_crew_contact（contactid，crewid）   tab_contact_sysrole_map（contactid，crewid）数据
				String delContactSql = "delete from tab_crew_contact where contactid=? and crewid = ?";
				String delContactMpSql = "delete from tab_contact_sysrole_map where contactid=? and crewid = ?";
				crewContactDao.getJdbcTemplate().batchUpdate(delContactSql, batchArgs);
				crewContactDao.getJdbcTemplate().batchUpdate(delContactMpSql, batchArgs);
				saveData = allDataList;
			}else{
				saveData = insertList;
			}
			
			
			
//保存tab_crew_contact   tab_contact_sysrole_map数据   保存联系人入住信息
			//保存tab_crew_contact 
			StringBuilder insertContactSql = new StringBuilder("insert into tab_crew_contact (contactId,crewId,ifOpen,");
			StringBuilder insertContactParamSql = new StringBuilder(" values ( ?,?,?, ");
			//拼sql
			Set<String> importColoumKeys = importColoum.keySet();
			Iterator<String> importColoumIt = importColoumKeys.iterator();
			while(importColoumIt.hasNext()){
				String importColoumKey = importColoumIt.next();
				String importColoumValue = importColoum.get(importColoumKey);
				if(!"sysRoleNames".equals(importColoumValue)){
					insertContactSql.append(importColoumValue);
					insertContactSql.append(",");
					insertContactParamSql.append("?,");
				}
				
			}
			insertContactSql.deleteCharAt(insertContactSql.length()-1);
			insertContactSql.append(" ) ");
			insertContactParamSql.deleteCharAt(insertContactParamSql.length()-1);
			insertContactParamSql.append(" ) ");
			insertContactSql.append(insertContactParamSql);
			//拼sql
			Field fields[]=CrewContactModel.class.getDeclaredFields();//获得对象所有属性
			//拼参数集合 顺便将中间表的contactId 和sysrolename存储起来为后期往中间表插入数据做准备
			Map<String, String> contactid_sysrolename = new LinkedHashMap<String, String>();
			List<Object[]> batchArgsList = new ArrayList<Object[]>();
			Object[] aObjects = null;
			for(Map<String, Object> dataMap :saveData){
				int index = 0;
				aObjects = new Object[importColoum.size()+2];//除去部门职务  加上 id,crewid,ifopen
				String newcontactId = UUIDUtils.getId();
				
				aObjects[index++] = newcontactId;
				aObjects[index++] = crewId;
				aObjects[index++] = 1;
				importColoumIt = importColoumKeys.iterator();
				while(importColoumIt.hasNext()){
					String importColoumKey = importColoumIt.next();
					String importColoumValue = importColoum.get(importColoumKey);
					if("sysRoleNames".equals(importColoumValue)){
						String sysrolename =dataMap.get(importColoumValue)!=null?dataMap.get(importColoumValue).toString():"";
						String rownumberStr =dataMap.get("rownumber")!=null?dataMap.get("rownumber").toString():"";
						if(StringUtils.isNotBlank(sysrolename)){
							contactid_sysrolename.put(newcontactId, dataMap.get(importColoumValue).toString());
							contactid_sysrolename.put(newcontactId+"-rownumber", rownumberStr);
						}
						continue ;
					}
					Object insertValue = dataMap.get(importColoumValue);
					for(Field field:fields){
						String fieldName = field.getName();
						if(fieldName.equals(importColoumValue)){
							Class<?> type = field.getType();
							if(insertValue!=null){
								String insertValueStr = insertValue.toString();
								if(type==String.class){
									aObjects[index++] = StringUtils.isNotBlank(insertValueStr)?insertValueStr:"";
									break;
								}else if(type==Integer.class){
									aObjects[index++] = StringUtils.isNotBlank(insertValueStr)?Integer.valueOf(insertValueStr):null;
									break;
								}else if(type==Boolean.class){
									aObjects[index++] = StringUtils.isNotBlank(insertValueStr)?Boolean.valueOf(insertValueStr):null;
									break;
								}else if(type==Date.class){
									aObjects[index++] = StringUtils.isNotBlank(insertValueStr)?new SimpleDateFormat("yyyy-MM-dd").parse(insertValueStr):null;
									break;
								}else if(type==int.class){
									aObjects[index++] = StringUtils.isNotBlank(insertValueStr)?Integer.valueOf(insertValueStr):null;
								}
							}
							
						}
					}
					
				}
				batchArgsList.add(aObjects);
			}
			//拼参数集合
			//批量保存联系人信息
			crewContactDao.getJdbcTemplate().batchUpdate(insertContactSql.toString(), batchArgsList);
			
			//保存数据 tab_contact_sysrole_map数据
			if(contactid_sysrolename!=null&&contactid_sysrolename.size()>0){
				Set<String> sysKeySet = contactid_sysrolename.keySet();
				Iterator<String> sysIt = sysKeySet.iterator();
				//查询部门、职务idsql
				List<Object[]> insertSysroleParamsList = new ArrayList<Object[]>();//往tab_contact_sysrole_map中插入数据的参数集合
				
				while(sysIt.hasNext()){
					String conId = sysIt.next();
					if(conId.contains("-rownumber")){
						continue;
					}
					String rowStr = contactid_sysrolename.get(conId+"-rownumber");
					
					StringBuilder roleIdSql = new StringBuilder("SELECT s.roleid,s.rolename FROM tab_sysrole_info p LEFT JOIN tab_sysrole_info s ON p.roleid = s.parentId WHERE ");
					String roleNames = contactid_sysrolename.get(conId);
					if(StringUtils.isBlank(roleNames)){
						throw new java.lang.IllegalArgumentException("第"+rowStr+"行：职务信息异常");
					}
					String[] roleNameArray = roleNames.replace("，", ",").split(",");
					if(roleNameArray!=null&&roleNameArray.length>0){
						StringBuilder parmsql = new StringBuilder();
						for(int m = 0;m<roleNameArray.length;m++){
							String p_s_name = roleNameArray[m];
							String[] psNames = p_s_name.split("/");
							if(psNames==null || psNames.length == 0) {
								throw new java.lang.IllegalArgumentException("第"+rowStr+"行 职务信息异常，只有职务信息 或 部门、职务信息用  / 隔开");
							}
							if(psNames.length == 1) {
								if(StringUtils.isBlank(psNames[0])) {
									throw new java.lang.IllegalArgumentException("第"+rowStr+"行 职务信息异常，没有名称为空串的职务名称");
								}
								parmsql.append(" s.rolename in ('"+psNames[0]+"') ");
							}
							if(psNames.length == 2) {								
								if(StringUtils.isBlank(psNames[0])||StringUtils.isBlank(psNames[1])){
									throw new java.lang.IllegalArgumentException("第"+rowStr+"行 职务信息异常，没有名称为空串的部门、职务名称");
								}
								parmsql.append("  (p.rolename IN ('"+psNames[0]+"') and s.rolename in ('"+psNames[1]+"') ) ");
							}
							if(m<roleNameArray.length-1){
								parmsql.append(" OR ");
							}
						}
						roleIdSql.append(parmsql);
						//根据部门、职务名称查询获取职务id
						List<Map<String, Object>> listRoleInfo = crewContactDao.getJdbcTemplate().queryForList(roleIdSql.toString());
						if(listRoleInfo==null||listRoleInfo.size()==0||listRoleInfo.size()!=roleNameArray.length){
							throw new java.lang.IllegalArgumentException("第"+rowStr+"行 excel中职务名称与数据库中数量不一致，请检查excel中部门、职务名称");
						}
						for(Map<String, Object> tMap:listRoleInfo){
							Object[] params = new Object[4];
							params[0] = UUIDUtils.getId();
							params[1] = crewId;
							params[2] = conId;
							params[3] = tMap.get("roleid");
							insertSysroleParamsList.add(params);
						}
						
					}
				}
				//批量往tab_contact_sysrole_map表中插入数据
				String inertContactSysroleMapSql = "insert into tab_contact_sysrole_map(id,crewid,contactid,sysroleid) values(?,?,?,?)";
				crewContactDao.getJdbcTemplate().batchUpdate(inertContactSysroleMapSql, insertSysroleParamsList);
			}
		}	
		return isRepeatPhoneNumber;
}

	/**
	 * xlh
	 * 
	 * 将ArrayList<ArrayList> 装换为Arraylist<Map<>>
	 * @param list 数据集合
	 * @param importColoum  导入的excel列名
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private List<Map<String, Object>> listToMap(List<ArrayList<String>> list,Map<String, String> importColoum) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		List<Map<String, Object>> back= new ArrayList<Map<String, Object>>();
		
		Map<String, Object> dataMap =null;
		
		Set<String> keys = importColoum.keySet();
		for(int m=0;m<list.size();m++){
			List<String> inlist = list.get(m);
			int n = 0;
			dataMap = new LinkedHashMap<String, Object>();
			Iterator<String> it =keys.iterator();
			while(it.hasNext()){
				String key = it.next();
				String value = importColoum.get(key);
				String listValue = inlist.get(n);
				
				if("sex".equals(value)){
					if(StringUtils.isNotBlank(listValue)){
						listValue =String.valueOf(Sex.nameOf(listValue).getValue());
					}
				}
				
				if("identityCardType".equals(value)){
					if(StringUtils.isNotBlank(listValue)){
						listValue =String.valueOf(IdentityCardType.nameOf(listValue).getValue());
					}
				}
				
				if("mealType".equals(value)){
					if(StringUtils.isNotBlank(listValue)){
						listValue =String.valueOf(MealType.nameOf(listValue).getValue());
					}
				}
				dataMap.put(value, listValue);
					
				n++;
			}
			dataMap.put("rownumber", m+3);
			back.add(dataMap);
		}
		return back;
	}
	
	/**
	 * 查询出当前剧组中的所有联系人的姓名
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryCrewContactName(String crewId){
		return this.crewContactDao.queryCrewContactName(crewId);
	}
}
