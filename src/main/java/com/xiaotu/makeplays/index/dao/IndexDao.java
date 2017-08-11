package com.xiaotu.makeplays.index.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.StringUtils;

@Repository
public class IndexDao extends BaseDao<T> {
	
	//获取地点统计
	public Map getAddressData(String crewId){
		Map map = new HashMap();
		String sql = "select count(*) as num from tab_view_info where crewId = ?";
		List<Map<String,Object>> all = this.query(sql, new Object[]{crewId}, null);
		map.put("all", (all.get(0)).get("num"));
		String sql2 = "select count(*) as num from tab_view_info where crewId = ? and LENGTH(shootLocationId) > 0";
		List<Map<String,Object>> li = this.query(sql2, new Object[]{crewId}, null);
		map.put("part", (li.get(0)).get("num"));
		String sql3 = "select count(*) as num from (select * from tab_sceneview_info where crewid=? GROUP BY id) as dto";
		List<Map<String,Object>> number = this.query(sql3, new Object[]{crewId}, null);
		map.put("number", (number.get(0)).get("num"));
		return map;
	}
	
	//获取演员统计
	public Map getActorData(String crewId){
		Map map = new HashMap();
		String sql = "SELECT count(*) as num FROM tab_actor_role_map tarm LEFT JOIN tab_view_role tvr ON tarm.viewRoleId = tvr.viewRoleId WHERE tarm.crewid=? AND tvr.viewRoleType = 1";
		List<Map<String,Object>> part = this.query(sql, new Object[]{crewId}, null);
		map.put("part", (part.get(0)).get("num"));
		String sql1 = "SELECT count(*) as num FROM tab_view_role WHERE crewid=? AND viewRoleType = 1";
		List<Map<String,Object>> total = this.query(sql1, new Object[]{crewId}, null);
		map.put("total", (total.get(0)).get("num"));
		return map;
	}
	
	//获取集数
	public Long getCrewEpisode(String crewId){
		String sql = "SELECT count(*) as num FROM (SELECT DISTINCT(seriesNo) FROM tab_view_info WHERE crewId = ?) as dto";
		List<Map<String,Object>> episode = this.query(sql, new Object[]{crewId}, null);
		return (Long) (episode.get(0)).get("num");
	}
	
	//获取收款金额
	public double getCollectionMoney(String crewId){
		String sql = "SELECT SUM(tci.money) as money,tcu.exchangeRate,tcu.currencyCode FROM tab_collection_info tci,tab_currency_info tcu WHERE tci.crewId=? AND tcu.currencyId = tci.currencyId GROUP BY tci.currencyId";
		double d = 0d;
		List<Map<String,Object>> li = this.query(sql, new Object[]{crewId}, null);
		if(li!=null && li.size()>0){
			for(int i=0;i<li.size();i++){
				d = StringUtils.add(d, StringUtils.mul((Double) (li.get(0)).get("money"), (Double) (li.get(0)).get("exchangeRate")));
			}
			//d = (Double) (li.get(0)).get("num");
		}
		return d;
	}
	
	//获取已拍摄天数
	public long getAlreadyShoot(String crewId){
		String sql = "SELECT COUNT(*) AS num FROM (SELECT DISTINCT(noticeDate) FROM tab_notice_info WHERE crewId = ? AND canceledStatus = 1) AS erp";
		long days = 0l;
		List<Map<String,Object>> li = this.query(sql, new Object[]{crewId}, null);
		days = (Long) (li.get(0)).get("num");
		return days;
	}
	
	//获取前一天结算的付款数据
	public List<Map<String,Object>> getYesterdayPay(String crewId){
		//double pays = 0d;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		String yesterdayDate = sdf.format(calendar.getTime());
		String sql = "SELECT SUM(dto.money) num,dto.currencyCode FROM (SELECT SUM(tpam.money) money,tci.currencyCode,tpi.currencyId FROM tab_payment_info tpi  " +
				"LEFT JOIN tab_payment_account_map tpam ON tpi.paymentId = tpam.paymentId " +
				"LEFT JOIN tab_currency_info tci ON tci.currencyId = tpi.currencyId " +
				"WHERE tpi.crewId = ? AND tpi.paymentDate = ? AND tpi.status = 1 GROUP BY tpi.paymentId) as dto GROUP BY dto.currencyId";
		List<Map<String,Object>> li = this.query(sql, new Object[]{crewId,yesterdayDate}, null);
		/*if(li!=null && li.size()>0){
			
		}*/
		return li;
	}
	
	//获取上一天所有通告单统计信息
	public List<Map<String,Object>> getPreNoticeTotal(String crewId){
		Map map = new HashMap();
//		String preDateSql = "SELECT noticeDate FROM tab_notice_info WHERE crewId = ? ORDER BY noticeDate DESC LIMIT 0,2; ";
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		String yesterdayDate = sdf.format(calendar.getTime());
		
		String sql = "SELECT  count(*) as num , tvn.shootStatus " +
				"FROM tab_notice_info tni  " +
				"LEFT JOIN tab_view_notice_map tvn ON tni.noticeId = tvn.noticeId  " +
				"WHERE tni.crewId = ? AND tvn.mapId IS NOT NULL AND tni.noticeDate = ? " +
				"GROUP BY tvn.shootStatus";
//		List<Map<String,Object>> dateList = this.query(preDateSql, new Object[]{crewId}, null);
		List<Map<String,Object>> li = this.query(sql, new Object[]{crewId,yesterdayDate}, null);
		return li; 
	}
	
	//获取当天的通告单数据
	public List<Map<String,Object>> getTodayNotice(String crewId){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
		/*SELECT  tni.noticeDate,tsg.groupname
		FROM tab_notice_info tni ,tab_shoot_group tsg 
		WHERE tni.groupId = tsg.groupId AND tni.crewId = 'c00162fcebdc43a3857b26607aca60c0' AND tni.noticeDate = '2015-01-19' AND canceledStatus = 0*/
		String sql = "SELECT  tni.noticeName,tni.noticeDate,tsg.groupName " +
				"FROM tab_notice_info tni,tab_shoot_group tsg WHERE tni.groupId = tsg.groupId and " +
				"tni.crewId = ? AND tni.noticeDate = ? AND tni.canceledStatus = 0";
		return this.query(sql, new Object[]{crewId,sdf.format(new Date())}, null);
	}
	
	//获取通联表(首页数据)
	public List<Map<String,Object>> getContactList(String crewId){
		String sql = "SELECT tui.userName,tui.realName,tui.phone,tui.sex,tui.email,tcum.crewId,tsh.groupName,tsi.roleId,tsi.roleName FROM tab_crew_user_map tcum " +
				"LEFT JOIN tab_user_info tui ON tcum.userId = tui.userId " +
				"LEFT JOIN tab_shoot_group tsh ON tui.groupId = tsh.groupId  " +
				"LEFT JOIN tab_user_role_map turm ON tui.userId = turm.userId  and turm.crewId = tcum.crewId  " +
				"LEFT JOIN tab_sysrole_info tsi ON tsi.roleId = turm.roleId " +
				"WHERE tcum.crewId = ? and tcum.isContactInfo = 1 and tui.status = " + Constants.USER_STATUS_VALID +
				" and tcum.status = " + Constants.STATUS_OK +
				" GROUP BY tui.userName,tui.realName,tui.phone,tui.sex,tui.email,tcum.crewId,tsh.groupName HAVING MIN(tsi.roleId) " +
				" ORDER BY tcum.sequence ASC ";
		List<Map<String,Object>> contactList = this.query(sql, new Object[]{crewId}, null);
		List<Map<String,Object>> contact = new ArrayList<Map<String,Object>>();
		/*if(contactList!=null && contactList.size()>0){
			for (Map<String, Object> map : contactList) {
				if(map.get("roleId")!=null){
					boolean flag = judgeContact(map.get("roleId").toString());
					if(flag){
						if(map.get("groupName")!=null){
							if(map.get("groupName").toString().trim().equals("")){
								map.put("groupName", Constants.CONTACT_GROUP_ALL);
							}
						}else{
							map.put("groupName", Constants.CONTACT_GROUP_ALL);
						}
						contact.add(map);
					}
				}
			}
		}*/
		return contactList;
	}
	
	//判断首页需要展示的通联表
	private boolean judgeContact(String name){
		String matchStr[] = {"14","17","2","4","6","22","35","41","51","18","33"};
		boolean flag = false;
		for(int i=0;i<matchStr.length;i++){
			if(name.trim().equals(matchStr[i])){
				flag = true;
				break;
			}
		}
		return flag;
	}

}
