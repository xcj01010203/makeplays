package com.xiaotu.makeplays.locationsearch.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.locationsearch.model.SceneViewInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;

@Repository
public class SceneViewInfoDao extends BaseDao<SceneViewInfoModel>{
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	
	public List<Map<String, Object>> querySceneViewBaseInfo(String crewId){
		String sql = "select id,vname,vaddress,vlongitude,vlatitude,remark from "+SceneViewInfoModel.TABLE_NAME+" where crewid = ? order by   ordernumber,vname";
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql, new Object[]{crewId});
		return list;
	}	
	
	

	/**
	 * 查询通告单下的场景中拍摄地点信息
	 * 按照通告单下场景的先后顺序排列
	 * 该方法没有考虑到拍摄地点为空的情况
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	public List<SceneViewInfoModel> queryShootLocationByNoticeId(String crewId, String noticeId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	distinct tsl.* ");
		sql.append(" FROM ");
		sql.append(" 	tab_view_notice_map tvnm, ");
		sql.append(" 	tab_view_info tvi,  ");
		sql.append(" 	tab_sceneview_info tsl ");
		sql.append(" WHERE ");
		sql.append(" 	tvnm.noticeId = ? ");
		sql.append(" AND tvnm.viewId = tvi.viewId ");
		sql.append(" AND tvnm.crewId = ? ");
		sql.append(" AND tvi.crewId = ? ");
		sql.append(" AND tsl.id = tvi.shootLocationId ");
		sql.append(" ORDER BY ");
		sql.append(" 	tvnm.sequence ");
		return  this.query(sql.toString(), new Object[]{noticeId,crewId,crewId},SceneViewInfoModel.class,null);
	}

	
	/**
	 * 根据剧本ID查询拍摄地点信息
	 * 该方法只查询存在于场景表中的拍摄地点
	 * @param crewId
	 * @return
	 */
	public List<SceneViewInfoModel> queryManyOnlyExistsInCrewView(String crewId) {
		String sql = "select tsl.* from tab_sceneview_info tsl, tab_view_info tvi where tsl.crewid=? and tvi.crewid=? and tsl.id = tvi.shootLocationId ORDER BY CONVERT(tsl.vname USING gbk)";
		return this.query(sql,  new Object[]{crewId,crewId},SceneViewInfoModel.class,null);
	}

	/**
	 * 查询已存在的地址
	 * @param address
	 * @return
	 */
	public SceneViewInfoModel queryShootAddressByAddress(String address,String crewId){
		
		String sql = "select * from tab_sceneview_info where vname=? and crewId=?";
		List<SceneViewInfoModel> list = this.query(sql, new Object[]{address,crewId},SceneViewInfoModel.class,null);
		if(null ==list||list.size()==0){
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * 使用剧本id查询剧组拍摄地址
	 * @param crewId
	 * @return
	 */
	public List<SceneViewInfoModel> queryShootAddressByCrewId(String crewId){
		StringBuilder sql = new StringBuilder();
		sql.append("          SELECT ");
		sql.append("             *");
		sql.append("          FROM ");
		sql.append(SceneViewInfoModel.TABLE_NAME);
		sql.append("          WHERE ");
		sql.append("             crewid = ? ");
		sql.append(" ORDER BY ");
		sql.append("    CONVERT (vname USING gbk)");
		return this.query(sql.toString(), new Object[]{crewId},SceneViewInfoModel.class,null);
	}
	
	/**
	 * 根据剧组id查询所有地域
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryShootRegionByCrewId(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	vCity shootRegion, ");
		sql.append(" 	count(tvlm.viewId) count ");
		sql.append(" FROM ");
		sql.append(" 	tab_sceneview_info tsi, ");
		sql.append(" 	tab_sceneview_viewinfo_map tsvm, ");
		sql.append(" 	tab_view_location_map tvlm, ");
		sql.append(" 	tab_view_location tvl ");
		sql.append(" WHERE ");
		sql.append(" 	tsi.id = tsvm.sceneviewId ");
		sql.append(" and tsvm.locationId=tvlm.locationId ");
		sql.append(" and tvlm.locationId=tvl.locationId ");
		sql.append(" and tvl.locationType=1 ");
		sql.append(" AND tsi.crewId = ? ");
		sql.append(" GROUP BY ");
		sql.append(" 	vCity ");
		sql.append(" ORDER BY ");
		sql.append(" 	count DESC,CONVERT (vCity USING gbk) ");
		return this.query(sql.toString(), new Object[] {crewId}, null);
	}

	/**
	 * 通过场景ID查找场景信息
	 * @param viewId
	 * @return
	 */
	public SceneViewInfoModel queryOneByShootLocationId (String shootLocationId) {
		String sql = "select *  from " + SceneViewInfoModel.TABLE_NAME +" where id = ?";
		SceneViewInfoModel shootLocation = null;
		List<SceneViewInfoModel> list = this.query(sql, new Object[]{shootLocationId},SceneViewInfoModel.class,null);
		if(list!=null&&list.size()>0){
			shootLocation = list.get(0);
		}
		return shootLocation;
	}



	public List<Map<String, Object>> queryData(String crewId){
		StringBuilder sql = new StringBuilder();
		
		sql.append("       SELECT ");
		sql.append("          GROUP_CONCAT( ");
		sql.append("             tvl.location ");
		sql.append("             order by locationtype ");
		sql.append("          ) location, ");
		sql.append("          GROUP_CONCAT( ");
		sql.append("             tvl.locationId ");
		sql.append("             order by locationtype ");
		sql.append("          ) locationid, ");
		sql.append("          tvlm.viewId ");
		sql.append("       FROM ");
		sql.append("         tab_view_location_map tvlm ");
		sql.append("       LEFT JOIN tab_view_location tvl   ON tvl.locationId = tvlm.locationId ");
		sql.append("       WHERE ");
		sql.append("          tvl.crewid =? ");
		sql.append("       GROUP BY ");
		sql.append("          tvlm.viewid ");
		return this.getJdbcTemplate().queryForList(sql.toString(),  new Object[]{crewId});
	}



	
	public SceneViewInfoModel querySceneViewInfoById(String id ) throws NoSuchMethodException, Exception{
		SceneViewInfoModel sceneViewInfoModel  = new SceneViewInfoModel();
		sceneViewInfoModel.setId(id);
		sceneViewInfoModel =  this.getEntityById(sceneViewInfoModel, "id");
		return sceneViewInfoModel;
	}
	
	
	
	/**
	 * @Description 更新 实景信息排序 
	 * @param idArray
	 */
	public void updateOrder(String[] idArray){
		String sql = "update "+SceneViewInfoModel.TABLE_NAME+" set orderNumber = ? where id = ?";
		int order = 1;
		List<Object[]> list = new ArrayList<Object[]>();
		Object[] arg = null;
		for(String id :idArray){
			arg = new Object[2];
			arg[0] = order++;
			arg[1] = id ;
			list.add(arg);
		}
		this.getJdbcTemplate().batchUpdate(	sql, list);
	}
	
	
	
	/**
	 * @Description  根据剧组id查询当前剧组戏量最多的  num个角色
	 * @return
	 */
	public List<Map<String, Object>> queryCrewRoleNumberByCrewId(String crewId,int num){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("    count(1), ");
		sql.append("    tvrm.viewroleid, ");
		sql.append("    tvr.viewrolename ");
		sql.append(" FROM ");
		sql.append("    tab_view_role_map tvrm ");
		sql.append(" LEFT JOIN tab_view_role tvr ON tvrm.viewRoleId = tvr.viewroleid ");
		sql.append(" WHERE ");
		sql.append("    tvr.crewId = ? ");
		sql.append(" AND tvr.isAttentionRole =1 ");
		sql.append(" GROUP BY ");
		sql.append("    tvrm.viewroleid, ");
		sql.append("    tvr.viewrolename ");
		sql.append(" ORDER BY ");
		sql.append("    tvr.sequence ,count(1) DESC limit ? ");
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql.toString(), new Object[]{crewId,num});
		return list;
	}
	
	
	/**
	 * 
	 * 查询当前剧组下的实景信息
	 * @param crewId 剧组id
	 * @return
	 */
	public List<Map<String, Object>> querySceneViewInfo(String crewId){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("    tsi.id, ");
		sql.append("    tsi.vname, ");
		sql.append("    tsi.vcity, ");
		sql.append("    tsi.vAddress, ");
		sql.append("    count( ");
		sql.append("       DISTINCT ");
		sql.append("       IF ( ");
		sql.append("          '夜' != tai.atmosphereName, ");
		sql.append("          tvi.viewId, ");
		sql.append("          NULL ");
		sql.append("       ) ");
		sql.append("    ) DAY, ");
		sql.append("    count( ");
		sql.append("       DISTINCT ");
		sql.append("       IF ( ");
		sql.append("          '夜' = tai.atmosphereName, ");
		sql.append("          tvi.viewId, ");
		sql.append("          NULL ");
		sql.append("       ) ");
		sql.append("    ) night, ");
		sql.append("    sum(tvi.pageCount) pageCount, ");
		sql.append("    count(DISTINCT tvi.viewId) siteNum, ");
		sql.append("    GROUP_CONCAT(c.viewroleids) roleids, ");
		sql.append("    GROUP_CONCAT(DISTINCT a.location) location ");
		sql.append(" FROM ");
		sql.append("    tab_sceneview_info tsi ");
		sql.append(" LEFT JOIN tab_view_info tvi ON tvi.shootLocationId = tsi.id ");
		sql.append(" LEFT JOIN ( ");
		sql.append("    SELECT ");
		sql.append("       viewid, ");
		sql.append("       GROUP_CONCAT( ");
		sql.append("          location ");
		sql.append("          ORDER BY ");
		sql.append("             locationtype SEPARATOR '-' ");
		sql.append("       ) location ");
		sql.append("    FROM ");
		sql.append("       tab_view_location_map tvlm ");
		sql.append("    LEFT JOIN tab_view_location tvl ON tvl.locationid = tvlm.locationid ");
		sql.append("    WHERE ");
		sql.append("       tvlm.crewid = ? ");
		sql.append("    GROUP BY ");
		sql.append("       viewid ");
		sql.append(" ) a ON a.viewid = tvi.viewid ");
		sql.append(" LEFT JOIN tab_atmosphere_info tai ON tvi.atmosphereId = tai.atmosphereId ");
		sql.append(" LEFT JOIN ( ");
		sql.append("    SELECT ");
		sql.append("       tvi.viewid, ");
		sql.append("       tvi.crewid, ");
		sql.append("       GROUP_CONCAT(tvrm.viewRoleId) viewroleids ");
		sql.append("    FROM ");
		sql.append("       tab_view_info tvi ");
		sql.append("    LEFT JOIN tab_view_role_map tvrm ON tvi.viewId = tvrm.viewId ");
		sql.append("    WHERE ");
		sql.append("       tvi.crewid = ? ");
		sql.append("    GROUP BY ");
		sql.append("       tvi.viewid ");
		sql.append(" ) c ON tvi.viewId = c.viewid ");
		sql.append(" WHERE ");
		sql.append("     tsi.crewId = ? ");
		sql.append(" GROUP BY ");
		sql.append("    id, ");
		sql.append("    vname ");
		sql.append(" ORDER BY ");
		sql.append("    tsi.orderNumber ,tsi.vname");
		
		
		
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql.toString(),new Object[]{crewId,crewId,crewId});
		return list;
	}
	
	/**
	 * @Description 判断当前剧组是否已经添加了同样的场景信息（根据剧组id，实景名称）判断
	 * @param request
	 * @param sceneViewName  实景名称
	 * @return
	 */
	public List<SceneViewInfoModel> querySceneViewForHasSameName(String crewId,String sceneViewName){
		String sql = "select * from "+SceneViewInfoModel.TABLE_NAME+" where crewid = ? and vName = ? ";
		List<SceneViewInfoModel> list = this.query(sql,new Object[]{crewId,sceneViewName},SceneViewInfoModel.class,null);
		return list;
	}
	
	/**
	 * 保存实景信息（保存实景信息后返回id 再上传图片）
	 * @Description 
	 * @param request
	 * @param id  主键
	 * @param vName 实景名称
	 * @param vCity  所在城市
	 * @param vAddress  详细地址
	 * @param vLongitude  详细地址经度
	 * @param vLatitude  详细地址纬度
	 * @param distanceToHotel   距离住宿地距离
	 * @param holePeoples   容纳人数
	 * @param deviceSpace   设备空间
	 * @param isModifyView   是否改景   0：是   1： 否
	 * @param modifyViewCost  改景费用
	 * @param modifyViewTime  改景耗时
	 * @param hasProp 是否有道具陈设   0：是   1： 否
	 * @param propCost  道具陈设费用
	 * @param propTime  道具陈设时间
	 * @param enterViewDate  进景时间
	 * @param leaveViewDate  离景时间
	 * @param viewUseTime 使用时间
	 * @param contactNo  联系方式
	 * @param contactName  联系人姓名
	 * @param contactRole  联系人职务
	 * @param viewPrice   场景价格
	 * @param freeStartDate  空档期开始时间
	 * @param freeEndDate  空档期结束时间
	 * @param other  自定义字段
	 * @param remark 备注
	 * @param crewId 剧组id
	 * @return
	 */
	public void saveSceneViewInfo(String id,String vName,String vCity,String vAddress,
			String vLongitude,String vLatitude,String distanceToHotel,Integer holePeoples,String deviceSpace,Integer isModifyView,
			Double modifyViewCost,String modifyViewTime,Integer hasProp,Double propCost,String propTime,String enterViewDate,
			String leaveViewDate,String viewUseTime,String contactNo,String contactName,String contactRole,Double viewPrice,String freeStartDate,
			String freeEndDate,String remark,String crewId) throws Exception{
		
		String sql = "insert into "+SceneViewInfoModel.TABLE_NAME+"(id,vName,vCity,vAddress,vLongitude,vLatitude,distanceToHotel,"
				+ "holePeoples,deviceSpace,isModifyView,modifyViewCost,modifyViewTime,hasProp,propCost,enterViewDate,leaveViewDate,viewUseTime,"
				+ "contactNo,contactName,contactRole,viewPrice,freeStartDate,freeEndDate,remark,orderNumber,crewId,propTime) values("
				+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] args = new Object[27];
		args[0] = id;
		args[1] = vName;
		args[2] = vCity;
		args[3] = vAddress;
		args[4] = vLongitude;
		args[5] = vLatitude;
		args[6] = distanceToHotel;
		args[7] = holePeoples;
		args[8] =deviceSpace ;
		args[9] = isModifyView;
		args[10] = modifyViewCost;
		args[11] = modifyViewTime;
		args[12] = hasProp;
		args[13] = propCost;
		args[14] =StringUtils.isBlank(enterViewDate)?null:sdf.parse(enterViewDate) ;
		args[15] = StringUtils.isBlank(leaveViewDate)?null:sdf.parse(leaveViewDate);
		args[16] = viewUseTime;
		args[17] = contactNo;
		args[18] = contactName;
		args[19] = contactRole;
		args[20] = viewPrice;
		args[21] = StringUtils.isBlank(freeStartDate)?null:sdf.parse(freeStartDate);
		args[22] = StringUtils.isBlank(freeEndDate)?null:sdf.parse(freeEndDate);
		args[23] = remark;
		args[24] = 0;
		args[25] = crewId;
		args[26] = propTime;
		this.getJdbcTemplate().update(sql, args);
	}
	
	
	/**
	 * 修改实景信息（保存实景信息后返回id 再上传图片）
	 * @Description 
	 * @param request
	 * @param id  主键
	 * @param vName 实景名称
	 * @param vCity  所在城市
	 * @param vAddress  详细地址
	 * @param vLongitude  详细地址经度
	 * @param vLatitude  详细地址纬度
	 * @param distanceToHotel   距离住宿地距离
	 * @param holePeoples   容纳人数
	 * @param deviceSpace   设备空间
	 * @param isModifyView   是否改景   0：是   1： 否
	 * @param modifyViewCost  改景费用
	 * @param modifyViewTime  改景耗时
	 * @param hasProp 是否有道具陈设   0：是   1： 否
	 * @param propCost  道具陈设费用
	 * @param propTime  道具陈设时间
	 * @param enterViewDate  进景时间
	 * @param leaveViewDate  离景时间
	 * @param viewUseTime 使用时间
	 * @param contactNo  联系方式
	 * @param contactName  联系人姓名
	 * @param contactRole  联系人职务
	 * @param viewPrice   场景价格
	 * @param freeStartDate  空档期开始时间
	 * @param freeEndDate  空档期结束时间
	 * @param other  自定义字段
	 * @param remark 备注
	 * @return
	 */
	public void updateSceneViewInfo(String id,String vName,String vCity,String vAddress,
			String vLongitude,String vLatitude,String distanceToHotel,Integer holePeoples,String deviceSpace,Integer isModifyView,
			Double modifyViewCost,String modifyViewTime,Integer hasProp,Double propCost,String propTime,String enterViewDate,
			String leaveViewDate,String viewUseTime,String contactNo,String contactName,String contactRole,Double viewPrice,String freeStartDate,
			String freeEndDate,String remark) throws Exception{
		String sql = "update "+SceneViewInfoModel.TABLE_NAME +" set vName = ? ,"
				+ "vCity = ? ,vAddress = ? ,vLongitude = ?,vLatitude = ?,"
				+ "distanceToHotel = ?,holePeoples = ?,deviceSpace = ?,"
				+ "isModifyView = ?,modifyViewCost = ?,modifyViewTime = ?,"
				+ "hasProp = ?,propCost = ?,propTime = ?,enterViewDate = ?,"
				+ "leaveViewDate = ?,viewUseTime = ?,contactNo = ?,contactName = ?"
				+ ",contactRole = ?,viewPrice = ?,freeStartDate = ?,freeEndDate = ?,"
				+ "remark = ?  where id = ?";
		
		Object[] args = new Object[25];
		args[0] = vName;
		args[1] = vCity;
		args[2] = vAddress;
		args[3] = vLongitude;
		args[4] = vLatitude;
		args[5] = distanceToHotel;
		args[6] = holePeoples;
		args[7] = deviceSpace;
		args[8] = isModifyView;
		args[9] = modifyViewCost;
		args[10] = modifyViewTime;
		args[11] = hasProp;
		args[12] = propCost;
		args[13] = propTime;
		args[14] = StringUtils.isBlank(enterViewDate)?null:sdf.parse(enterViewDate);
		args[15] = StringUtils.isBlank(leaveViewDate)?null:sdf.parse(leaveViewDate);
		args[16] = viewUseTime;
		args[17] = contactNo;
		args[18] = contactName;
		args[19] = contactRole;
		args[20] = viewPrice;
		args[21] = StringUtils.isBlank(freeStartDate)?null:sdf.parse(freeStartDate);
		args[22] = StringUtils.isBlank(freeEndDate)?null:sdf.parse(freeEndDate);
		args[23] = remark;
		args[24] = id;
		this.getJdbcTemplate().update(sql, args);
	}
	
	
	/**
	 * 根据实景id删除实景信息
	 * @param id
	 * @throws Exception 
	 */
	public void delSceneViewInfo(String id) throws Exception{
		this.deleteOne(id, "id", SceneViewInfoModel.TABLE_NAME);
		
	}
	/**
	 * @Description  删除已经配置的主场景信息
	 * @param sceneViewInfoId 实景id
	 * @param locationId    主场景id
	 * @return
	 */
	public void modifySceneViewMapInfo(String sceneViewInfoId,String[] locationIdArray,String crewId){
		//String sql = "update "+ViewInfoModel.TABLE_NAME+" set shootlocationid = ? where viewid in (select distinct viewid from "+ViewLocationMapModel.TABLE_NAME+" where locationid = ?)";
		StringBuilder sql = new StringBuilder();
		sql.append(" update ");
		sql.append("    tab_view_info tvi set shootlocationid = ? ");
		sql.append(" WHERE ");
		sql.append("    tvi.viewId IN ( ");
		sql.append("       SELECT DISTINCT ");
		sql.append("          viewid ");
		sql.append("       FROM ");
		sql.append("          ( ");
		sql.append("             SELECT ");
		sql.append("                tvi.viewId, ");
		sql.append("                GROUP_CONCAT( ");
		sql.append("                   tvl.locationid ");
		sql.append("                   ORDER BY ");
		sql.append("                      tvl.locationtype ");
		sql.append("                ) locationid, ");
		sql.append("                GROUP_CONCAT( ");
		sql.append("                   tvl.location ");
		sql.append("                   ORDER BY ");
		sql.append("                      tvl.locationtype ");
		sql.append("                ) location ");
		sql.append("             FROM ");
		sql.append("                tab_view_info tvi ");
		sql.append("             LEFT JOIN tab_view_location_map tvlm ON tvi.viewId = tvlm.viewId ");
		sql.append("             LEFT JOIN tab_view_location tvl ON tvlm.locationId = tvl.locationId ");
		sql.append("             WHERE ");
		sql.append("                tvi.crewid = ? ");
		sql.append("             GROUP BY ");
		sql.append("                tvi.viewId ");
		sql.append("          ) a ");
		sql.append("       WHERE ");
		
		String readySql = sql.toString();
		String updateSql = readySql +"          locationid = ? )";
		String upNullSql = readySql+"          locationid is null )";
		List<Object[]> argsList = new ArrayList<Object[]>();
		Object[] args = null;
		boolean blankFlag = false;
		for(String locationId :locationIdArray){
			args = new Object[3];
			args[0] = sceneViewInfoId;
			args[1] = crewId;
			
			if(!"blank".equals(locationId)){
				args[2] = locationId;
				argsList.add(args);
			}else{
				
				blankFlag = true;
			}
			
		}
		this.getJdbcTemplate().batchUpdate(updateSql, argsList);
		if(blankFlag){
			this.getJdbcTemplate().update(upNullSql, new Object[]{sceneViewInfoId,crewId});
		}
	}
	/**
	 * 查询当前实景已经配置的主场景信息或者查询当前剧组下的没有被配置的场景信息
	 * 
	 * 
	 * @param haschecek   是否已经被配置到主场景     true:已经配置（查询当前实景已经配置的场景信息）
	 * 										false:没有配置（查询备选场景信息）
	 * @param crewId     剧组id
	 * @param idOrLoaction		 实景id或者主场景名称
	 * @return
	 */
	public List<Map<String, Object>> queryHasCheckOrAlternativeViewInfoForSceneView(boolean haschecek,String crewId,String idOrLoaction){
		List<Object> parList = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		/*sql.append(" SELECT ");
		sql.append("    location, ");
		sql.append("    locationid, ");
		sql.append("    count(DISTINCT viewId) siteNum, ");
		sql.append("    sum(pageCount) pageCount, ");
		sql.append("    sum(DAY) DAY, ");
		sql.append("    sum(night) night, ");
		sql.append("    GROUP_CONCAT(roleids) roleids ,GROUP_CONCAT(distinct vname) vname");
		sql.append(" FROM ");
		sql.append("    ( ");
		sql.append("       SELECT ");
		sql.append("          tvi.viewId, ");
		sql.append("          b.location, ");
		sql.append("          b.locationid, ");
		sql.append("          tvi.pageCount, ");
		sql.append("          tvi.shootlocationid, ");
		sql.append("          count( ");
		sql.append("             DISTINCT ");
		sql.append("             IF ( ");
		sql.append("                '日' = tai.atmosphereName, ");
		sql.append("                tvi.viewId, ");
		sql.append("                NULL ");
		sql.append("             ) ");
		sql.append("          ) DAY, ");
		sql.append("          count( ");
		sql.append("             DISTINCT ");
		sql.append("             IF ( ");
		sql.append("                '夜' = tai.atmosphereName, ");
		sql.append("                tvi.viewId, ");
		sql.append("                NULL ");
		sql.append("             ) ");
		sql.append("          ) night, ");
		sql.append("          GROUP_CONCAT(c.viewroleid) roleids , vname");
		sql.append("       FROM ");
		sql.append("          tab_view_info tvi ");
		sql.append("       LEFT JOIN ( ");
		sql.append("          SELECT ");
		sql.append("             tvlm.viewId, ");
		sql.append("             tvl.location, ");
		sql.append("             tvl.locationId ");
		sql.append("          FROM ");
		sql.append("             tab_view_location_map tvlm ");
		sql.append("          LEFT JOIN tab_view_location tvl ON tvlm.locationId = tvl.locationId ");
		sql.append("          WHERE ");
		sql.append("             tvlm.crewid = tvl.crewid ");
		sql.append("          AND tvlm.crewid = ? ");
		parList.add(crewId);
		sql.append("       ) b ON tvi.viewId = b.viewId ");
		sql.append("       LEFT JOIN ( ");
		sql.append("          SELECT ");
		sql.append("             viewid, ");
		sql.append("             tvrm.viewroleid ");
		sql.append("          FROM ");
		sql.append("             tab_view_role_map tvrm ");
		sql.append("          LEFT JOIN tab_view_role tvr ON tvrm.viewroleid = tvr.viewroleid ");
		sql.append("          WHERE ");
		sql.append("             tvrm.crewid = tvr.crewid ");
		sql.append("          AND tvrm.crewid = ? ");
		parList.add(crewId);
		sql.append("       ) c ON tvi.viewId = c.viewid ");
		sql.append("       LEFT JOIN tab_atmosphere_info tai ON tvi.atmosphereid = tai.atmosphereid ");
		sql.append("       LEFT JOIN tab_sceneview_info tsi  on tsi.id = tvi.shootlocationid ");
		sql.append("       WHERE ");
		sql.append("          tvi.crewId = ? ");
		parList.add(crewId);
		sql.append("       GROUP BY ");
		sql.append("          b.location, ");
		sql.append("          b.locationid, ");
		sql.append("          viewId, ");
		sql.append("          shootlocationid ,vname");
		sql.append("    ) d ");
		sql.append(" WHERE 1 = 1 ");
		if(haschecek){
			sql.append("  and   d.shootlocationid = ? ");
			parList.add(idOrLoaction);
		}else{
			if(StringUtils.isNotBlank(idOrLoaction)){
				sql.append("  AND d.location LIKE ? ");
				parList.add("%"+idOrLoaction+"%");
			}
		}
		
		
		
		sql.append(" GROUP BY ");
		sql.append("    location, ");
		sql.append("    locationid ");
		sql.append("    order by  vname ");
		*
		*
		*/
		sql.append(" SELECT ");
		sql.append("    location, ");
		sql.append("    locationid, ");
		sql.append("    count(DISTINCT viewId) siteNum, ");
		sql.append("    sum(pageCount) pageCount, ");
		sql.append("    sum(DAY) DAY, ");
		sql.append("    sum(night) night, ");
		sql.append("    GROUP_CONCAT(roleids) roleids, ");
		sql.append("    GROUP_CONCAT(DISTINCT vname) vname ");
		sql.append(" FROM ");
		sql.append("    ( ");
		sql.append("       SELECT ");
		sql.append("          tvi.viewId, ");
		sql.append("          b.location, ");
		sql.append("          b.locationid, ");
		sql.append("          tvi.pageCount, ");
		sql.append("          tvi.shootlocationid, ");
		sql.append("          count( ");
		sql.append("             DISTINCT ");
		sql.append("             IF ( ");
		sql.append("                '日' = tai.atmosphereName, ");
		sql.append("                tvi.viewId, ");
		sql.append("                NULL ");
		sql.append("             ) ");
		sql.append("          ) DAY, ");
		sql.append("          count( ");
		sql.append("             DISTINCT ");
		sql.append("             IF ( ");
		sql.append("                '夜' = tai.atmosphereName, ");
		sql.append("                tvi.viewId, ");
		sql.append("                NULL ");
		sql.append("             ) ");
		sql.append("          ) night, ");
		sql.append("          GROUP_CONCAT(c.viewroleid) roleids, ");
		sql.append("          vname ");
		sql.append("       FROM ");
		sql.append("          tab_view_info tvi ");
		sql.append("       LEFT JOIN ( ");
		sql.append("          SELECT ");
		sql.append("             tvlm.viewId, ");
		sql.append("             GROUP_CONCAT( ");
		sql.append("                tvl.locationid ");
		sql.append("                ORDER BY ");
		sql.append("                   tvl.locationtype ");
		sql.append("             ) locationid, ");
		sql.append("             GROUP_CONCAT( ");
		sql.append("                tvl.location ");
		sql.append("                ORDER BY ");
		sql.append("                   tvl.locationtype SEPARATOR ' - ' ");
		sql.append("             ) location ");
		sql.append("          FROM ");
		sql.append("             tab_view_location_map tvlm ");
		sql.append("          LEFT JOIN tab_view_location tvl ON tvlm.locationId = tvl.locationId ");
		sql.append("          WHERE ");
		sql.append("             tvlm.crewid = tvl.crewid ");
		sql.append("          AND tvlm.crewid = ? ");
		parList.add(crewId);
		sql.append("          GROUP BY ");
		sql.append("             tvlm.viewid ");
		sql.append("       ) b ON tvi.viewId = b.viewId ");
		sql.append("       LEFT JOIN ( ");
		sql.append("          SELECT ");
		sql.append("             viewid, ");
		sql.append("             tvrm.viewroleid ");
		sql.append("          FROM ");
		sql.append("             tab_view_role_map tvrm ");
		sql.append("          LEFT JOIN tab_view_role tvr ON tvrm.viewroleid = tvr.viewroleid ");
		sql.append("          WHERE ");
		sql.append("             tvrm.crewid = tvr.crewid ");
		sql.append("          AND tvrm.crewid = ? ");
		parList.add(crewId);
		sql.append("       ) c ON tvi.viewId = c.viewid ");
		sql.append("       LEFT JOIN tab_atmosphere_info tai ON tvi.atmosphereid = tai.atmosphereid ");
		sql.append("       LEFT JOIN tab_sceneview_info tsi ON tsi.id = tvi.shootlocationid ");
		sql.append("       WHERE ");
		sql.append("          tvi.crewId = ? ");
		parList.add(crewId);
		sql.append("       GROUP BY ");
		sql.append("          b.location, ");
		sql.append("          b.locationid, ");
		sql.append("          viewId, ");
		sql.append("          shootlocationid, ");
		sql.append("          vname ");
		sql.append("    ) d ");
		sql.append(" WHERE ");
		sql.append("    1 = 1 ");
		if(haschecek){
			sql.append("  and   d.shootlocationid = ? ");
			parList.add(idOrLoaction);
		}else{
			if(StringUtils.isNotBlank(idOrLoaction)){
				sql.append("  AND d.location LIKE ? ");
				parList.add("%"+idOrLoaction+"%");
			}
		}
		sql.append(" GROUP BY ");
		sql.append("    location, ");
		sql.append("    locationid ");
		sql.append(" ORDER BY ");
		sql.append("    vname, ");
		sql.append("    location ");
		
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql.toString(), parList.toArray());
		return list;
	}	
	
	/**
	 * 查询所有的省市信息
	 */
	public List<String> queryAllProCity() {
		StringBuilder sql = new StringBuilder();
		sql.append(" select CONCAT(ptai.areaName,tai.areaName) areaName ");
		sql.append(" from tab_area_info tai  ");
		sql.append(" left join tab_area_info ptai on ptai.areaId=tai.parentId ");
		sql.append(" where tai.parentId != 0 ");
		sql.append(" order by tai.areaId ");
		return this.getJdbcTemplate().queryForList(sql.toString(), String.class, null);
	}
}
