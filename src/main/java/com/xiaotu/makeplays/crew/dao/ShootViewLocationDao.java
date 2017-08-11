package com.xiaotu.makeplays.crew.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.sysrole.model.SysroleInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;


@Deprecated
@Repository
public class ShootViewLocationDao extends BaseDao<SysroleInfoModel> {
 
   private	final String  MAINSQL="SELECT shootLocation,shootLocationId,firstViewLocation,secondViewLocation,viewId ,COUNT(viewNo) viewNo,"+
	" ROUND(SUM(pageCount),2)  pageCount"+
	" FROM (SELECT	vlm.viewId,views.shootLocationId,viewNo,pageCount, IF(sl.shootLocation IS NULL,'待定',sl.shootLocation) shootLocation,"+
	" GROUP_CONCAT(DISTINCT IF (vl.locationType = 1, vl.location, NULL) ORDER BY vl.location SEPARATOR ',') firstViewLocation,"+
	" GROUP_CONCAT(DISTINCT IF (vl.locationType = 2, vl.location, NULL) ORDER BY vl.location SEPARATOR ',') secondViewLocation"+
	" FROM tab_view_info views LEFT JOIN (tab_view_location_map vlm,tab_view_location vl) ON ( views.viewId = vlm.viewId AND vlm.locationId = vl.locationId)"+
	" LEFT JOIN tab_shoot_location sl ON views.shootLocationId = sl.shootLocationId WHERE views.crewId=? GROUP BY views.viewId) AS shoot_address_temp";
	/**
	 * 获取主场景列表
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> getMainLocation(String crewId)throws SQLException{
		String sql=MAINSQL+" GROUP BY  shootLocation,firstViewLocation ORDER BY viewNo DESC,pageCount DESC";
		return this.query(sql, new Object[]{crewId}, null);
	}
	/**
	 * 拍摄地址list
	 * @param crewId 剧组ID
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> getLocationList(String crewId)throws SQLException{
		String sql=MAINSQL+" GROUP BY  shootLocation ORDER BY viewNo DESC,pageCount DESC";
		return this.query(sql, new Object[]{crewId}, null);
	}
	/**
	 * 获取次场景列表
	 * @param crewId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getLastLocation(String crewId)throws Exception{
		String sql=MAINSQL+" GROUP BY  shootLocation,firstViewLocation,secondViewLocation ORDER BY viewNo DESC,pageCount DESC";
		return this.query(sql, new Object[]{crewId}, null);
		
	}
}
