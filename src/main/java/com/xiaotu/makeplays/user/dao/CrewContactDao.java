package com.xiaotu.makeplays.user.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.user.controller.filter.ContactFilter;
import com.xiaotu.makeplays.user.model.CrewContactModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 剧组联系表
 * @author xuchangjian 2016-5-20上午9:52:22
 */
@Repository
public class CrewContactDao extends BaseDao<CrewContactModel> {
	
	/**
	 * 根据高级查询条件查询
	 * @param contactFilterModel
	 * @return 联系人基本信息，职务ID（多个以逗号隔开），职务名称（部门-职务，多个以逗号隔开），部门名称（多个以逗号隔开）
	 */
	public List<Map<String, Object>> queryContactListByAdvanceCondition(String crewId, ContactFilter contactFilter, Page page){
		List<Object> paramList = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("    tcc.contactId, ");
		sql.append("    tcc.contactName, ");
		sql.append("    tcc.phone, ");
		sql.append("    tcc.sex, ");
		sql.append("    tcc.enterDate, ");
		sql.append("    tcc.leaveDate, ");
		sql.append("    tcc.remark, ");
		sql.append("    tcc.mealType , ");
		sql.append("    thi.hotelName hotel, ");
		sql.append("    b.roomNo roomNumber, ");
		sql.append("    b.extension, ");
		sql.append("    b.checkInDate, ");
		sql.append("    b.checkoutDate,");
		sql.append("    tcc.ifOpen, ");
		sql.append("    tcc.sequence, ");
		sql.append("    tcc.userId, ");
		sql.append("    tcc.identityCardType, ");
		sql.append("    tcc.identityCardNumber, ");
		sql.append("    tsi.roleName duty, ");
		sql.append("    GROUP_CONCAT(tsi.orderNo) roleOrderNos, ");
		sql.append("    GROUP_CONCAT(tsi.roleId) sysRoleIds, ");
		sql.append("    GROUP_CONCAT(ptsi.orderNo) pRoleOrderNos, ");
		sql.append("    GROUP_CONCAT(DISTINCT ptsi.roleId) departmentIds, ");
		sql.append("    GROUP_CONCAT(ptsi.roleId) pRoleIds, ");
		sql.append("    GROUP_CONCAT( ");
		sql.append("       CONCAT( ");
		sql.append("          ptsi.roleName, ");
		sql.append("          '-', ");
		sql.append("          tsi.roleName ");
		sql.append("       ) ");
		sql.append("    ) sysRoleNames, ");
		sql.append("	func_get_first_letter(tcc.contactName) fletter");
		sql.append(" FROM ");
		sql.append("    tab_crew_contact tcc ");
		sql.append(" LEFT JOIN tab_contact_sysrole_map tcsm ON tcsm.contactId = tcc.contactId ");
		sql.append(" LEFT JOIN tab_sysrole_info tsi ON tsi.roleId = tcsm.sysroleId ");
		sql.append(" LEFT JOIN tab_sysrole_info ptsi ON tsi.parentId = ptsi.roleId ");
		sql.append(" LEFT JOIN ( ");
		sql.append("    SELECT ");
		sql.append("       peopleName,	max(checkInDate) checkInDate,	MAX(checkoutDate) checkoutDate, ");
		sql.append("		roomNo,	extension,	hotelId ");
		sql.append("    FROM ");
		sql.append("       tab_checkIn_hotel_info ");
		sql.append("	WHERE	crewId = ?");
		paramList.add(crewId);
		sql.append("    GROUP BY ");
		sql.append("       peopleName ");
		sql.append(" ) b ON tcc.contactName = b.peopleName ");
		sql.append("  LEFT JOIN tab_hotel_info thi ON b.hotelId = thi.id and thi.crewId = ? ");
		paramList.add(crewId);
		sql.append(" WHERE ");
		sql.append("    tcc.crewId = ? ");
		sql.append(" and ptsi.parentId != '01' ");
		paramList.add(crewId);
		if (contactFilter != null) {
			if (StringUtils.isNotBlank(contactFilter.getSourceFrom())) {
				sql.append(" AND tcsm.sysroleId not in (1) ");
			}
			if (StringUtils.isNotBlank(contactFilter.getContactId())) {
				sql.append(" AND tcc.contactid =? ");
				paramList.add(contactFilter.getContactId());
			}
			if (!StringUtils.isBlank(contactFilter.getContactName())) {
				sql.append(" AND tcc.contactName = ? ");
				paramList.add(contactFilter.getContactName());
			}
			
			if (contactFilter.getSex() != null) {
				sql.append(" AND tcc.sex = ? ");
				paramList.add(contactFilter.getSex());
			}
			if (!StringUtils.isBlank(contactFilter.getDepartmentIds())) {
				String departmentIds = "'" + contactFilter.getDepartmentIds().replace(",", "','") + "'";
				sql.append(" AND ptsi.roleId in ("+ departmentIds +") ");
			}
			if (!StringUtils.isBlank(contactFilter.getSysRoleIds())) {
				String roleIds = "'" + contactFilter.getSysRoleIds().replace(",", "','") + "'";
				sql.append(" AND tsi.roleId in ("+ roleIds +") ");
			}
			if (contactFilter.getIdentityCardType() != null) {
				sql.append(" AND tcc.identityCardType = ? ");
				paramList.add(contactFilter.getIdentityCardType());
			}
			if (!StringUtils.isBlank(contactFilter.getEnterDate())) {
				sql.append(" AND tcc.enterDate = ? ");
				paramList.add(contactFilter.getEnterDate());
			}
			if (!StringUtils.isBlank(contactFilter.getLeaveDate())) {
				sql.append(" AND tcc.leaveDate = ? ");
				paramList.add(contactFilter.getLeaveDate());
			}
			if (contactFilter.getMealType() != null) {
				sql.append(" AND tcc.mealType = ? ");
				paramList.add(contactFilter.getMealType());
			}
			if (!StringUtils.isBlank(contactFilter.getHotel())) {
				sql.append(" AND thi.hotelname like '%"+ contactFilter.getHotel() +"%' ");
			}
			if (contactFilter.getIfOpen() != null) {
				sql.append(" AND tcc.ifOpen = ? ");
				if (contactFilter.getIfOpen()) {
					paramList.add(1);
				} else {
					paramList.add(0);
				}
			}
		}
		
		sql.append(" GROUP BY ");
		sql.append("    tcc.contactId, ");
		sql.append("    tcc.contactName, ");
		sql.append("    tcc.phone, ");
		sql.append("    tcc.sex, ");
		sql.append("    tcc.enterDate, ");
		sql.append("    tcc.leaveDate, ");
		sql.append("    tcc.remark, ");
		sql.append("    tcc.mealType, ");
		/*sql.append("    tii.hotelname, ");
		sql.append("    tii.roomNumber, ");
		sql.append("    tii.extension, ");
		sql.append("    tii.checkInDate, ");
		sql.append("    tii.checkoutDate, ");
		sql.append("    tii.inhotelid,");*/
		sql.append("    tcc.ifOpen, ");
		sql.append("    tcc.sequence, ");
		sql.append("    tcc.userId, ");
		sql.append("    tcc.identityCardType, ");
		sql.append("    tcc.identityCardNumber ");
		sql.append(" ORDER BY ");
		sql.append("    tcc.sequence ");
		sql.append("  ");
		
		return this.query(sql.toString(), paramList.toArray(), page);
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
	public List<Map<String, Object>> queryContactListGroup(String crewId, ContactFilter contactFilter){
		List<Object> paramList = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT ");
		sql.append(" 	tcc.contactId, ");
		sql.append(" 	tcc.contactName, ");
		sql.append(" 	tcc.phone, ");
		sql.append(" 	tcc.sex, ");
		sql.append(" 	tcc.enterDate, ");
		sql.append(" 	tcc.leaveDate, ");
		sql.append(" 	tcc.remark, ");
		sql.append(" 	tcc.mealType, ");
		sql.append(" 	thi.hotelName hotel, ");
		sql.append(" 	b.roomNo roomNumber, ");
		sql.append(" 	b.extension, ");
		sql.append(" 	b.checkInDate, ");
		sql.append(" 	b.checkoutDate, ");
		sql.append(" 	tcc.ifOpen, ");
		sql.append(" 	tcc.sequence, ");
		sql.append(" 	tcc.userId, ");
		sql.append(" 	tcc.identityCardType, ");
		sql.append(" 	tcc.identityCardNumber, ");
		sql.append(" 	tsi.roleName duty, ");
		sql.append(" 	tsi.roleId, ");
		sql.append(" 	ptsi.roleId pRoleId, ");
		sql.append(" 	ptsi.roleName pRoleName, ");
		sql.append(" 	func_get_first_letter (tcc.contactName) fletter ");
		sql.append(" FROM ");
		sql.append(" 	tab_crew_contact tcc ");
		sql.append(" LEFT JOIN tab_contact_sysrole_map tcsm ON tcsm.contactId = tcc.contactId ");
		sql.append(" LEFT JOIN tab_sysrole_info tsi ON tsi.roleId = tcsm.sysroleId ");
		sql.append(" LEFT JOIN tab_sysrole_info ptsi ON tsi.parentId = ptsi.roleId ");
		sql.append(" LEFT JOIN ( ");
		sql.append(" 	SELECT ");
		sql.append(" 		peopleName, ");
		sql.append(" 		max(checkInDate) checkInDate, ");
		sql.append(" 		MAX(checkoutDate) checkoutDate, ");
		sql.append(" 		roomNo, ");
		sql.append(" 		extension, ");
		sql.append(" 		hotelId ");
		sql.append(" 	FROM ");
		sql.append(" 		tab_checkIn_hotel_info ");
		sql.append(" 	WHERE ");
		sql.append(" 		crewId = ? ");
		paramList.add(crewId);
		sql.append(" 	GROUP BY ");
		sql.append(" 		peopleName ");
		sql.append(" ) b ON tcc.contactName = b.peopleName ");
		sql.append(" LEFT JOIN tab_hotel_info thi ON b.hotelId = thi.id ");
		sql.append(" AND thi.crewId = ? ");
		paramList.add(crewId);
		sql.append(" WHERE ");
		sql.append(" 	tcc.crewId = ? ");
		paramList.add(crewId);
		sql.append(" and ptsi.parentId != '01' ");
		if (contactFilter != null) {
			if (StringUtils.isNotBlank(contactFilter.getSourceFrom())) {
				sql.append(" AND tcsm.sysroleId not in (1) ");
			}
			if (StringUtils.isNotBlank(contactFilter.getContactId())) {
				sql.append(" AND tcc.contactid =? ");
				paramList.add(contactFilter.getContactId());
			}
			if (!StringUtils.isBlank(contactFilter.getContactName())) {
				sql.append(" AND tcc.contactName = ? ");
				paramList.add(contactFilter.getContactName());
			}
			
			if (contactFilter.getSex() != null) {
				sql.append(" AND tcc.sex = ? ");
				paramList.add(contactFilter.getSex());
			}
			if (!StringUtils.isBlank(contactFilter.getDepartmentIds())) {
				String departmentIds = "'" + contactFilter.getDepartmentIds().replace(",", "','") + "'";
				sql.append(" AND ptsi.roleId in ("+ departmentIds +") ");
			}
			if (!StringUtils.isBlank(contactFilter.getSysRoleIds())) {
				String roleIds = "'" + contactFilter.getSysRoleIds().replace(",", "','") + "'";
				sql.append(" AND tsi.roleId in ("+ roleIds +") ");
			}
			if (contactFilter.getIdentityCardType() != null) {
				sql.append(" AND tcc.identityCardType = ? ");
				paramList.add(contactFilter.getIdentityCardType());
			}
			if (!StringUtils.isBlank(contactFilter.getEnterDate())) {
				sql.append(" AND tcc.enterDate = ? ");
				paramList.add(contactFilter.getEnterDate());
			}
			if (!StringUtils.isBlank(contactFilter.getLeaveDate())) {
				sql.append(" AND tcc.leaveDate = ? ");
				paramList.add(contactFilter.getLeaveDate());
			}
			if (contactFilter.getMealType() != null) {
				sql.append(" AND tcc.mealType = ? ");
				paramList.add(contactFilter.getMealType());
			}
			if (!StringUtils.isBlank(contactFilter.getHotel())) {
				sql.append(" AND thi.hotelname like '%"+ contactFilter.getHotel() +"%' ");
			}
			if (contactFilter.getIfOpen() != null) {
				sql.append(" AND tcc.ifOpen = ? ");
				if (contactFilter.getIfOpen()) {
					paramList.add(1);
				} else {
					paramList.add(0);
				}
			}
		}
		sql.append(" ORDER BY ");
		sql.append(" 	ptsi.orderNo is null,ptsi.orderNo,tsi.orderNo is null,tsi.orderNo ");
		return this.query(sql.toString(), paramList.toArray(), null);
	}
	
	/**
	 * 查询剧组中联系人的最大序列
	 * @param crewId
	 * @return
	 */
	public int queryMaxSequence(String crewId) {
		String sql = "select max(sequence) maxSequece from tab_crew_contact where crewId = ?";
		
		List<Map<String, Integer>> resultList = this.query(sql, new Object[] {crewId}, null);
		if (resultList != null) {
			return resultList.get(0).get("maxSequece");
		}
		return 0;
	}
	
	/**
	 * 使联系表中所有联系人序列加1
	 * @param crewId
	 */
	public void makeSequenceAddOne(String crewId) {
		String sql = "update tab_crew_contact set sequence = sequence + 1 where crewId = ?";
		this.getJdbcTemplate().update(sql, crewId);
	}
	
	/**
	 * 更新联系表排列顺序
	 */
	public void updateContactSequence(String crewId, int seq, String contactId) {
		String sql = "update " + CrewContactModel.TABLE_NAME + " set sequence = ? where crewId = ? and contactId = ? ";
		this.getJdbcTemplate().update(sql, new Object[]{seq,crewId,contactId});
	}
	
	/**
	 * 根据用户ID查询剧组联系人信息
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	public CrewContactModel queryByCrewUserId(String crewId, String userId) throws Exception {
		String sql = "select * from tab_crew_contact where crewId = ? and userId = ?";
		return this.queryForObject(sql, new Object[] {crewId, userId}, CrewContactModel.class);
	}
	
	/**
	 * 根据用户手机号查找联系人信息
	 * 如果contactId不为空，则排除掉自己
	 * @param crewId
	 * @param phone
	 * @return
	 * @throws Exception 
	 */
	public CrewContactModel queryByPhone(String crewId, String contactId, String phone) throws Exception {
		List<String> paramList = new ArrayList<String>();
		paramList.add(crewId);
		paramList.add(phone);
		
		StringBuilder sql = new StringBuilder("select * from tab_crew_contact where crewId = ? and phone = ?");
		if (!StringUtils.isBlank(contactId)) {
			sql.append(" AND contactId != ? ");
			paramList.add(contactId);
		}
		return this.queryForObject(sql.toString(), paramList.toArray(), CrewContactModel.class);
	}
	
	/**
	 * 根据ID查询数据
	 * @param contactId
	 * @return
	 * @throws Exception
	 */
	public CrewContactModel queryById(String contactId) throws Exception {
		String sql = "select * from tab_crew_contact where contactId = ?";
		return this.queryForObject(sql, new Object[] {contactId}, CrewContactModel.class);
	}
	
	/**
	 * 根据通告单时间查询通告单下的联系人
	 * @param noticeTimeId
	 * @return	联系人ID，联系人名称，联系人职务ID（多个以逗号隔开），联系人职务名称（多个以逗号隔开）
	 */
	public List<Map<String, Object>> queryByNotictTimeId(String crewId, String noticeTimeId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tcc.contactId, ");
		sql.append(" 	tcc.contactName, ");
		sql.append(" 	tcc.phone, tsi.roleName duty,");
		sql.append(" 	GROUP_CONCAT(tsi.roleId) sysRoleIds, ");
		sql.append(" 	GROUP_CONCAT(CONCAT(ptsi.roleName, '-', tsi.roleName)) sysRoleNames ");
		sql.append(" FROM ");
		sql.append(" 	tab_crew_contact tcc, ");
		sql.append(" 	tab_notice_user_map tnum ");
		sql.append(" LEFT JOIN tab_contact_sysrole_map tcsm ON tcsm.contactId = tnum.userId ");
		sql.append(" LEFT JOIN tab_sysrole_info tsi ON tsi.roleId = tcsm.sysroleId ");
		sql.append(" LEFT JOIN tab_sysrole_info ptsi ON tsi.parentId = ptsi.roleId ");
		sql.append(" WHERE ");
		sql.append(" 	tcc.contactId = tnum.userId ");
		sql.append(" AND tnum.noticeTimeId = ? ");
		sql.append(" AND tcc.crewId = ? ");
		sql.append(" GROUP BY ");
		sql.append(" 	tcc.contactId, ");
		sql.append(" 	tcc.contactName, ");
		sql.append(" 	tcc.phone ");
		sql.append(" ORDER BY ");
		sql.append(" 	tcc.sequence ");
		
		return this.query(sql.toString(), new Object[] {noticeTimeId, crewId}, null);
	}
	
	/**
	 * 设置剧组联系人是否公开到组
	 * @param contactId	
	 * @param ifOpen
	 */
	public void setIfOpen(String contactId, Boolean ifOpen) {
		String sql = "update tab_crew_contact set ifOpen = ? where contactId = ?";
		this.getJdbcTemplate().update(sql, ifOpen, contactId);
	}

	/**
	 * 查询出剧组联系表中的人员名称
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryCrewContactName(String crewId){
		String sql = "SELECT DISTINCT contactName FROM tab_crew_contact WHERE crewId = ?";
		return this.query(sql, new Object[] {crewId}, null);
	}
}
