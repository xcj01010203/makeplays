package com.xiaotu.makeplays.mobile.server.crewcontact;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.authority.model.UserAuthMapModel;
import com.xiaotu.makeplays.authority.service.AuthorityService;
import com.xiaotu.makeplays.authority.service.UserAuthMapService;
import com.xiaotu.makeplays.hotelInfo.service.CheckinHotelInfoService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;
import com.xiaotu.makeplays.mobile.server.common.BaseFacade;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.user.controller.filter.ContactFilter;
import com.xiaotu.makeplays.user.model.CrewContactModel;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.service.CrewContactService;
import com.xiaotu.makeplays.utils.AuthorityConstants;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.StringUtil;

/**
 * 剧组联系表
 * @author xuchangjian 2016-9-20下午3:28:47
 */
@Controller
@RequestMapping("/interface/crewContactFacade")
public class CrewContactFacade extends BaseFacade{

	Logger logger = LoggerFactory.getLogger(CrewContactFacade.class);
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private CrewContactService crewContactService;
	
	@Autowired
	private AuthorityService authorityService;
	
	@Autowired
	private UserAuthMapService userAuthMapService;
	
	@Autowired
	private CheckinHotelInfoService checkinHotelInfoService;
	
	/**
	 * 获取剧组联系表
	 */
	@ResponseBody
	@RequestMapping("/obtainCrewContactList")
	public Object obtainCrewContactList(HttpServletRequest request,
			String crewId, String userId, ContactFilter contactFilter,
			Integer pageSize, Integer pageNo) {
		Map<String,Object> resultMap = new HashMap<String, Object>();
		try {
			UserInfoModel userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			
			Page page = new Page();
			if (pageSize != null && pageNo != null) {
				page.setPageNo(pageNo);
				page.setPagesize(pageSize);
			} else {
				page.setPageNo(1);
				page.setPagesize(20);
			}
			
			//判断用户是否有编辑剧组联系表权限
			UserAuthMapModel userAuthMap = this.userAuthMapService.queryOneByUserIdAndAuthCode(crewId, userId, AuthorityConstants.CREW_CONTACT);
			if (userAuthMap != null && userAuthMap.getReadonly()) {
				contactFilter.setIfOpen(true);
			}
			
			List<Map<String, Object>>  contactList = this.crewContactService.queryContactListByAdvanceCondition(crewId, contactFilter, page);
			for (Map<String, Object> map : contactList) {
				Integer ifOpen = (Integer) map.get("ifOpen");
				if (ifOpen == 0) {
					map.put("ifOpen", false);
				} else {
					map.put("ifOpen", true);
				}
				
				Date enterDate = (Date) map.get("enterDate");
				Date leaveDate = (Date) map.get("leaveDate");
				Date checkInDate = (Date) map.get("checkInDate");
				Date checkoutDate = (Date) map.get("checkoutDate");
				
				if (enterDate != null) {
					map.put("enterDate", this.sdf1.format(enterDate));
				}
				if (leaveDate != null) {
					map.put("leaveDate", this.sdf1.format(leaveDate));
				}
				if (checkInDate != null) {
					map.put("checkInDate", this.sdf1.format(checkInDate));
				}
				if (checkoutDate != null) {
					map.put("checkoutDate", this.sdf1.format(checkoutDate));
				}
			}
			resultMap.put("contactList", contactList);
			resultMap.put("pageCount", page.getPageCount());
			
			this.sysLogService.saveSysLogForApp(request, "查询剧组联系表", userInfo.getClientType(), CrewContactModel.TABLE_NAME, null, 0);
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取剧组联系表信息失败", e);
			throw new IllegalArgumentException("未知异常，获取剧组联系表信息失败");
		}
		return resultMap;
	}
	
	/**
	 * 获取剧组联系表，分组显示
	 * @param request
	 * @param crewId
	 * @param userId
	 * @param contactFilter
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainCrewContactListGroup")
	public Object obtainCrewContactListGroup(HttpServletRequest request,
			String crewId, String userId, ContactFilter contactFilter) {
		Map<String,Object> resultMap = new HashMap<String, Object>();
		try {
			UserInfoModel userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
						
			//判断用户是否有编辑剧组联系表权限
			UserAuthMapModel userAuthMap = this.userAuthMapService.queryOneByUserIdAndAuthCode(crewId, userId, AuthorityConstants.CREW_CONTACT);
			if (userAuthMap != null && userAuthMap.getReadonly()) {
				contactFilter.setIfOpen(true);
			}
			
			//剧组联系人列表，多个职务显示多条，按职务排序
			List<Map<String, Object>> contactList = this.crewContactService.queryContactListByAdvanceCondition(crewId, contactFilter, null);			
			
			//职务列表
			List<Map<String, Object>> roleList = new ArrayList<Map<String, Object>>();
			List<String> roleNameList = new ArrayList<String>();
			
			//按照职务分组
			for (Map<String, Object> map : contactList) {
				Integer ifOpen = (Integer) map.get("ifOpen");
				if (ifOpen == 0) {
					map.put("ifOpen", false);
				} else {
					map.put("ifOpen", true);
				}
				
				Date enterDate = (Date) map.get("enterDate");
				Date leaveDate = (Date) map.get("leaveDate");
				Date checkInDate = (Date) map.get("checkInDate");
				Date checkoutDate = (Date) map.get("checkoutDate");
				
				if (enterDate != null) {
					map.put("enterDate", this.sdf1.format(enterDate));
				}
				if (leaveDate != null) {
					map.put("leaveDate", this.sdf1.format(leaveDate));
				}
				if (checkInDate != null) {
					map.put("checkInDate", this.sdf1.format(checkInDate));
				}
				if (checkoutDate != null) {
					map.put("checkoutDate", this.sdf1.format(checkoutDate));
				}
				String pRoleIds = StringUtil.nullToString((String) map.get("pRoleIds"));
				String roleIds = StringUtil.nullToString((String) map.get("sysRoleIds"));
				String sysRoleNames = StringUtil.nullToString((String) map.get("sysRoleNames"));
				String pRoleOrderNos = StringUtil.nullToString((String) map.get("pRoleOrderNos"));
				String roleOrderNos = StringUtil.nullToString((String) map.get("roleOrderNos"));
				String[] pRoleIdsArr = pRoleIds.split(",");
				String[] roleIdsArr = roleIds.split(",");
				String[] sysRoleNamesArr = sysRoleNames.split(",");
				String[] pRoleOrderNosArr = pRoleOrderNos.split(",");
				String[] roleOrderNosArr = roleOrderNos.split(",");
				for(int i = 0; i < roleIdsArr.length; i++) {
					String pRoleId = pRoleIdsArr[i];
					String roleId = roleIdsArr[i];
					String[] sysRoleName = sysRoleNamesArr[i].split("-");
					String pRoleName = "";
					String roleName = "";
					if(sysRoleName.length == 2) {
						pRoleName = sysRoleName[0];
						roleName = sysRoleName[1];
					}
					String pRoleOrderNo = pRoleOrderNosArr[i];
					String roleOrderNo = roleOrderNosArr[i];
					
					if (!roleNameList.contains(roleName)) {
						roleNameList.add(roleName);
						
						Map<String, Object> roleMapMap = new HashMap<String, Object>();
						roleMapMap.put("roleId", roleId);
						roleMapMap.put("roleName", roleName);
						roleMapMap.put("pRoleId", pRoleId);
						roleMapMap.put("pRoleName", pRoleName);
						roleMapMap.put("pRoleOrderNo", Integer.parseInt(pRoleOrderNo));
						roleMapMap.put("roleOrderNo", Integer.parseInt(roleOrderNo));
						
						List<Map<String, Object>> roleUserList = new ArrayList<Map<String, Object>>();
						roleUserList.add(map);
						
						roleMapMap.put("roleUserList", roleUserList);
						
						roleList.add(roleMapMap);
						
					} else {
						Map<String, Object> roleMap = roleList.get(roleNameList.indexOf(roleName));
						List<Map<String, Object>> roleUserList = (List<Map<String, Object>>) roleMap.get("roleUserList");
						
						roleUserList.add(map);
					}
				}
			}
			
			Collections.sort(roleList, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1,
						Map<String, Object> o2) {
					int re = 0;
					Integer pRoleOrderNo1 = (Integer) o1.get("pRoleOrderNo");
					if(pRoleOrderNo1 == null) {
						pRoleOrderNo1 = 999;
					}
					Integer pRoleOrderNo2 = (Integer) o2.get("pRoleOrderNo");
					if(pRoleOrderNo2 == null) {
						pRoleOrderNo2 = 999;
					}
					re = pRoleOrderNo1 - pRoleOrderNo2;
					if(re == 0) {
						Integer roleOrderNo1 = (Integer) o1.get("roleOrderNo");
						if(roleOrderNo1 == null) {
							roleOrderNo1 = 999;
						}
						Integer roleOrderNo2 = (Integer) o2.get("roleOrderNo");
						if(roleOrderNo2 == null) {
							roleOrderNo2 = 999;
						}
						re = roleOrderNo1 - roleOrderNo2;
					}
					return re;
				}
			});
			
			//小组列表
			List<Map<String, Object>> groupList = new ArrayList<Map<String, Object>>();
			
			List<String> groupNameList = new ArrayList<String>();
			for (Map<String, Object> roleMap : roleList) {
				
				String proleId = (String) roleMap.get("pRoleId");
				String proleName = (String) roleMap.get("pRoleName");
				
				if (!groupNameList.contains(proleName)) {
					groupNameList.add(proleName);
					
					Map<String, Object> groupMap = new HashMap<String, Object>();
					groupMap.put("groupId", proleId);
					groupMap.put("groupName", proleName);
					
					List<Map<String, Object>> groupRoleList = new ArrayList<Map<String, Object>>();
					groupRoleList.add(roleMap);
					groupMap.put("groupRoleList", groupRoleList);
					
					groupList.add(groupMap);
				} else {
					Map<String, Object> groupMap = groupList.get(groupNameList.indexOf(proleName));
					List<Map<String, Object>> groupRoleList = (List<Map<String, Object>>) groupMap.get("groupRoleList");
					groupRoleList.add(roleMap);
				}
			}
			
			/*List<Map<String, Object>> contactList = this.crewContactService.queryContactListGroup(crewId, contactFilter);
			
			List<Map<String, Object>> contactGroupList = new ArrayList<Map<String,Object>>();
			Map<String, Map<String, Object>> contactGroupMap = new HashMap<String, Map<String,Object>>();
			Map<String, Map<String, Object>> groupRoleMap = new HashMap<String, Map<String,Object>>();
			for (Map<String, Object> map : contactList) {
				Integer ifOpen = (Integer) map.get("ifOpen");
				if (ifOpen == 0) {
					map.put("ifOpen", false);
				} else {
					map.put("ifOpen", true);
				}
				
				Date enterDate = (Date) map.get("enterDate");
				Date leaveDate = (Date) map.get("leaveDate");
				Date checkInDate = (Date) map.get("checkInDate");
				Date checkoutDate = (Date) map.get("checkoutDate");
				
				if (enterDate != null) {
					map.put("enterDate", this.sdf1.format(enterDate));
				}
				if (leaveDate != null) {
					map.put("leaveDate", this.sdf1.format(leaveDate));
				}
				if (checkInDate != null) {
					map.put("checkInDate", this.sdf1.format(checkInDate));
				}
				if (checkoutDate != null) {
					map.put("checkoutDate", this.sdf1.format(checkoutDate));
				}
				String pRoleId = (String) map.get("pRoleId");
				if(pRoleId == null) {
					pRoleId = "";
				}
				String roleId = (String) map.get("roleId");
				if(roleId == null) {
					roleId = "";
				}
				Map<String, Object> groupMap = null;
				List<Map<String, Object>> groupRoleList = null;
				if(!contactGroupMap.containsKey(pRoleId)) {
					groupMap = new HashMap<String, Object>();
					groupMap.put("groupId", pRoleId);
					groupMap.put("groupName", (String) map.get("pRoleName"));
					groupRoleList = new ArrayList<Map<String,Object>>();
					groupMap.put("groupRoleList", groupRoleList);
					contactGroupList.add(groupMap);
					contactGroupMap.put(pRoleId, groupMap);
				} else {
					groupMap = contactGroupMap.get(pRoleId);
					groupRoleList = (List<Map<String, Object>>) groupMap.get("groupRoleList");
				}
				Map<String, Object> roleMap = null;
				List<Map<String, Object>> roleUserList = null;
				if(!groupRoleMap.containsKey(roleId)) {
					roleMap = new HashMap<String, Object>();
					roleMap.put("roleId", roleId);
					roleMap.put("roleName", (String) map.get("duty"));
					roleUserList = new ArrayList<Map<String,Object>>();
					roleMap.put("roleUserList", roleUserList);
					groupRoleList.add(roleMap);
					groupRoleMap.put(roleId, roleMap);
				} else {
					roleMap = groupRoleMap.get(roleId);
					roleUserList = (List<Map<String, Object>>) roleMap.get("roleUserList");
				}
				roleUserList.add(map);
			}*/
			resultMap.put("contactGroupList", groupList);
			
			this.sysLogService.saveSysLogForApp(request, "查询剧组联系表", userInfo.getClientType(), CrewContactModel.TABLE_NAME, null, 0);
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，获取剧组联系表信息失败", e);
			throw new IllegalArgumentException("未知异常，获取剧组联系表信息失败");
		}
		return resultMap;
	}
	
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
	 */
	@ResponseBody
	@RequestMapping("/saveCrewContactInfo")
	public Object saveCrewContactInfo(HttpServletRequest request, String crewId, String userId, String contactId, 
			String contactName, String phone, Integer sex, 
			Integer identityCardType, String identityCardNumber, String sysRoleIds,
			String enterDate, String leaveDate, String remark, Integer mealType, Boolean ifOpen) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(contactName)) {
				throw new IllegalArgumentException("请填写联系人姓名");
			}
			if (contactName.length() > 20) {
				throw new IllegalArgumentException("姓名过长，请检查");
			}
			if (StringUtils.isBlank(phone)) {
				throw new IllegalArgumentException("请填写手机号");
			}
			if (phone.length() > 20) {
				throw new IllegalArgumentException("手机号过长，请检查");
			}
			if (sex == null) {
				throw new IllegalArgumentException("请选择性别");
			}
			if (StringUtils.isBlank(sysRoleIds)) {
				throw new IllegalArgumentException("请选择职务");
			}
			if (ifOpen == null) {
				throw new IllegalArgumentException("请填写是否公开到组");
			}
			if (!StringUtils.isBlank(identityCardNumber) && identityCardNumber.length() > 18) {
				throw new IllegalArgumentException("证件号码过长，请检查");
			}
			
			//校验是否已有相同手机号的联系人
			CrewContactModel existCrewContact = this.crewContactService.queryByPhone(crewId, contactId, phone);
			if (existCrewContact != null) {
				throw new IllegalArgumentException("已存在相同手机号的联系人，请检查");
			}
			
			CrewContactModel crewContact = this.crewContactService.saveCrewContactInfo(crewId, userId, contactId, contactName, 
					phone, sex, identityCardType, identityCardNumber, 
					sysRoleIds, enterDate, leaveDate, remark, 
					mealType, ifOpen);
			
			resultMap.put("contactId", crewContact.getContactId());
			
			if (!StringUtils.isBlank(contactId)) {
				this.sysLogService.saveSysLogForApp(request, "新增剧组联系人", userInfo.getClientType(), CrewContactModel.TABLE_NAME, null, 1);
			} else {
				this.sysLogService.saveSysLogForApp(request, "修改剧组联系人", userInfo.getClientType(), CrewContactModel.TABLE_NAME, contactId, 2);
			}
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，保存联系人信息失败", e);
			this.sysLogService.saveSysLogForApp(request, "保存剧组联系人失败：" + e.getMessage(), userInfo.getClientType(), 
					CrewContactModel.TABLE_NAME, contactId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，保存联系人信息失败");
		}
		return resultMap;
	}
	
	/**
	 * 删除剧组联系表
	 * @param crewId
	 * @param userId
	 * @param contactId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteCrewContact")
	public Object deleteCrewContact(HttpServletRequest request, String crewId, String userId, String contactId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		UserInfoModel userInfo = new UserInfoModel();
		try {
			userInfo = MobileUtils.checkCrewUserValid(crewId, userId);
			if (StringUtils.isBlank(contactId)) {
				throw new IllegalArgumentException("请提供联系人信息");
			}
			this.crewContactService.deleteCrewContact(crewId, contactId);
			
			this.sysLogService.saveSysLogForApp(request, "删除剧组联系人", userInfo.getClientType(), CrewContactModel.TABLE_NAME, contactId, 3);
		} catch(IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常，删除联系人信息失败", e);
			this.sysLogService.saveSysLogForApp(request, "删除剧组联系人失败：" + e.getMessage(), 
					userInfo.getClientType(), CrewContactModel.TABLE_NAME, contactId, SysLogOperType.ERROR.getValue());
			throw new IllegalArgumentException("未知异常，删除联系人信息失败");
		}
		return resultMap;
	}
}
