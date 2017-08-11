package com.xiaotu.makeplays.view.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.utils.StringUtils;
import com.xiaotu.makeplays.view.controller.filter.ViewFilter;
import com.xiaotu.makeplays.view.model.ViewLocationMapModel;
import com.xiaotu.makeplays.view.model.ViewLocationModel;

/**
 * 场景地点信息表
 * @author xuchangjian
 */
@Repository
public class ViewLocationDao extends BaseDao<ViewLocationModel> {

	/**
	 * 批量新增操作
	 * @param atmosphereList
	 * @throws Exception 
	 */
	public void addMany (List<ViewLocationModel> viewLocationList) throws Exception {
		for (ViewLocationModel viewLocation : viewLocationList) {
			this.add(viewLocation);
		}
	}
	
	/**
	 * 更新数据
	 * @param viewLocation	场景地址信息
	 * @throws Exception 
	 */
	public void update(ViewLocationModel viewLocation) throws Exception {
		this.update(viewLocation, "locationId");
	}
	
	/**
	 * 批量更新数据
	 * @param viewLocationList
	 * @throws Exception 
	 */
	public void updateManyLocationInfo(List<ViewLocationModel> viewLocationList) throws Exception {
		for (ViewLocationModel viewLocation : viewLocationList) {
			this.update(viewLocation);
		}
	}
	
	/**
	 * 根据场景ID查询场景地点信息
	 * @param viewId
	 * @return
	 */
	public List<ViewLocationModel> queryManyByViewId(String viewId) {
		String sql = "select a.* from tab_view_location a, tab_view_location_map am where a.locationId = am.locationId and am.viewId = ? ";
		List<ViewLocationModel> addressList = this.query(sql, new Object[]{viewId}, ViewLocationModel.class, null);
		
		return addressList;
	}
	
	/**
	 * 通过剧本ID查找
	 * @param crewId 剧本ID
	 * @return
	 */
	public List<ViewLocationModel> queryManyByCrewId(String crewId) {
		String sql = "select tvl.* from " + ViewLocationModel.TABLE_NAME + " tvl where tvl.crewId = ? and "
				+" exists(select tvlm.viewId from tab_view_location_map tvlm where tvlm.crewId=? and tvlm.locationId=tvl.locationId) "
				+" ORDER BY CONVERT(tvl.location USING gbk) ";
		
		List<ViewLocationModel> addressList = this.query(sql, new Object[]{crewId,crewId}, ViewLocationModel.class, null);
		
		return addressList;
	}
	
	/**
	 * 通过剧本ID查找
	 * @param crewId 剧本ID
	 * @return
	 */
	public List<ViewLocationModel> queryManyByCrewIdAndTypeAndName(String crewId, Integer locationType, String name) {
		List<Object> params = new ArrayList<Object>();
		params.add(crewId);
		params.add(crewId);
		params.add(locationType);
		String sql = "select tvl.*,count(distinct tvlm.viewId) num from " + ViewLocationModel.TABLE_NAME + " tvl " 
				+ " ,tab_view_location_map tvlm " 
				+ " where tvlm.locationId=tvl.locationId and tvlm.crewId=? and tvl.crewId = ? and tvl.locationType = ? ";
		if(StringUtils.isNotBlank(name)) {
			sql += " and tvl.location like ? ";
			params.add("%" + name + "%");
		}
		sql += " group by tvl.locationId ORDER BY num desc, CONVERT(tvl.location USING gbk) ";
		
		List<ViewLocationModel> addressList = this.query(sql, params.toArray(), ViewLocationModel.class, null);		
		return addressList;
	}
	
	/**
	 * 查询出当前剧组中的所有的场景信息
	 * @param crewId
	 * @return
	 */
	public List<ViewLocationModel> queryLocationInfoByCrewId(String crewId){
		String sql = "select tvl.* from " + ViewLocationModel.TABLE_NAME + " tvl where tvl.crewId = ? "
				+" ORDER BY CONVERT(tvl.location USING gbk) ";
		
		List<ViewLocationModel> addressList = this.query(sql, new Object[]{crewId}, ViewLocationModel.class, null);
		
		return addressList;
	}
	
	/**
	 * 根据多个条件查询场景地点信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ViewLocationModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + ViewLocationModel.TABLE_NAME + " where 1 = 1 ");
		
		Set<String> keySet = conditionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		List<Object> conList = new LinkedList<Object>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = conditionMap.get(key);
			sql.append(" and " + key + " = ?");
			conList.add(value);
		}
		Object[] objArr = conList.toArray();
		List<ViewLocationModel> viewLocationList = this.query(sql.toString(), objArr, ViewLocationModel.class, page);
		
		return viewLocationList;
	}
	
	/**
	 * 根据多个场景ID查询场景下的场景地点信息
	 * @param viewIds
	 * @return
	 */
	public List<Map<String, Object>> queryViewLocationByViewIds(String viewIds) {
		String sql = "select l.*, m.viewId from " 
				+ ViewLocationMapModel.TABLE_NAME 
				+ " m, " + ViewLocationModel.TABLE_NAME 
				+ " l where m.viewId in (" + viewIds + ") and l.locationId = m.locationId";
		
		return this.query(sql, null, null);
	}
	
	/**
	 * 根据剧组ID查询主场景汇总信息
	 * @param crewId
	 * @param locationType 场景类型
	 * @param shootLocation 查询条件：拍摄地点
	 * @param location 查询条件：主场景
	 * @param flag 1:显示，2：不显示
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryViewLocationStatistic(String crewId,
			int locationType, ViewFilter filter, String sortField) {
		StringBuilder sql = new StringBuilder();
		List<Object> paramList = new ArrayList<Object>();
		paramList.add(crewId);
		paramList.add(locationType);
		sql.append(" select if(tsi.id is null or tsi.id='','',tsi.id) as shootLocationId,tsi.orderNumber,tsi.vName as shootLocation,tvl.locationId,tvl.location, ");
		sql.append(" 	count(distinct tvi.viewId) viewNum, count(distinct if(tvi.shootStatus in (2,3),tvi.viewId,null)) finishedViewNum, ");
		sql.append(" 	ifnull(sum(tvi.pageCount),0) pageNum, ifnull(sum(if(tvi.shootStatus in (2,3),tvi.pageCount,0)),0) finishedPageNum, ");
		sql.append(" 	trim(GROUP_CONCAT(distinct tai.atmosphereName order by tai.atmosphereName Separator  ' ')) atmosphere,");
		sql.append("	trim(GROUP_CONCAT(distinct tvi.site order by tvi.site Separator ' ')) site ");
		sql.append(" from tab_view_info tvi ");
		sql.append(" LEFT JOIN tab_view_location_map tvlm on tvi.viewId=tvlm.viewId ");
		sql.append(" LEFT JOIN tab_view_location tvl on tvlm.locationId=tvl.locationId ");
		sql.append(" LEFT JOIN tab_atmosphere_info tai on tvi.atmosphereId=tai.atmosphereId ");
//		sql.append(" LEFT JOIN tab_shoot_location tsl on tvi.shootLocationId=tsl.shootLocationId ");
		sql.append(" LEFT JOIN tab_sceneview_info tsi on tvi.shootLocationId=tsi.id ");
		sql.append(" where tvi.crewId = ? ");
//		sql.append(" and tvi.shootStatus != 3 ");//去掉删戏
		sql.append(" and (tvl.locationType = ? or tvl.locationId is null)");
		String shootLocation = filter.getShootLocation();
		if(StringUtils.isNotBlank(shootLocation)) {
			if(shootLocation.equals("blank")) {
//				sql.append(" and (tsl.shootLocationId is null or tsl.shootLocationId='') ");
				sql.append(" and (tsi.id is null or tsi.id='') ");
			} else {
//				sql.append(" and tsl.shootLocationId in ('" + shootLocation.replace(",", "','") + "')");
				sql.append(" and tsi.id in ('" + shootLocation.replace(",", "','") + "')");
			}
		}
		String location = filter.getMajor();
		if(StringUtils.isNotBlank(location)) {
			if(location.equals("blank")) {
				sql.append(" and (tvl.locationId is null or tvl.locationId='') ");
			} else {
				sql.append(" and tvl.locationId in ('" + location.replace(",", "','") + "')");
			}
		}
		if(StringUtils.isNotBlank(filter.getRoles())){
			boolean blankFlag = false;	//标识所选演员中是否有选择[空]的情况
			String[] roleIdArr = filter.getRoles().split(",");	
			String notBlankRoleIds = "";	//不包含[空]的情况下的所有选中的演员信息
			
			for(int i = 0; i < roleIdArr.length; i++){
				String roleId = roleIdArr[i];
				if (roleId.equals("blank")) {
					blankFlag = true;
				} else {
					notBlankRoleIds += roleId;
					if(i < roleIdArr.length - 1) {
						notBlankRoleIds += ",";
					}
				}
			}
			
			//选择的角色同时存在的场次
			if(StringUtils.isBlank(filter.getSearchMode())||"2".equals(filter.getSearchMode())){
				//如果选择的演员中有非空的情况，执行以下逻辑
				if (!StringUtils.isBlank(notBlankRoleIds)) {
					notBlankRoleIds = notBlankRoleIds.substring(0, notBlankRoleIds.length());
					String roles = "'" + notBlankRoleIds.replaceAll(",", "','") + "'";
					sql.append(" and EXISTS ( select tsrmRes.viewId from ( ");
					sql.append(" select tsrm.viewId,group_concat(tsrm.viewRoleId) roleIdStr from tab_view_role_map tsrm ");
					sql.append(" where tsrm.viewRoleId in (" + roles + ") group by tsrm.viewId ) tsrmRes ");
					sql.append(" where tsrmRes.viewId=tvi.viewId ");

					String[] roleArray = notBlankRoleIds.split(",");
					for(String roleId:roleArray){
						sql.append(" and tsrmRes.roleIdStr like '%" + roleId + "%' ");
					}
					sql.append(" ) ");
				}
				
				//如果选择的演员中有[空]的情况，执行以下逻辑
				if (blankFlag) {
					sql.append(" and not EXISTS (select 1 from tab_view_role_map tvrm where tvrm.viewId = tvi.viewId and tvrm.crewId = ?) ");
					paramList.add(crewId);
				}
				
			}else if("0".equals(filter.getSearchMode())){
				//选择存在即可的场次
				if (!StringUtils.isBlank(notBlankRoleIds)) {
					String roles = "'"+notBlankRoleIds.replaceAll(",", "','")+"'";
					sql.append(" and EXISTS ( select * from tab_view_role_map tsrm where tsrm.viewId=tvi.viewId and tsrm.viewRoleId in (" + roles + ")) ");
				}
				
				if (blankFlag) {
					sql.append(" and not EXISTS (select 1 from tab_view_role_map tvrm, tab_view_role tvr where tvrm.viewId = tvi.viewId and tvrm.crewId = ? and tvr.viewRoleId = tvrm.viewRoleId and tvr.viewRoleType = 1)");
					paramList.add(crewId);
				}
				
			}else if("1".equals(filter.getSearchMode())){//不出现
				if (!StringUtils.isBlank(notBlankRoleIds)) {
					//选择同时不出现的场次
					sql.append(" and not EXISTS ( select tsrmRes.viewId from (select tsrm.viewId,group_concat(tsrm.viewRoleId) roleIdStr from tab_view_role_map tsrm "
							+ " group by tsrm.viewId ) tsrmRes where tsrmRes.viewId=tvi.viewId and (" );

					String[] roleArray = notBlankRoleIds.split(",");
					for(int i = 0; i < roleArray.length; i++){
						String roleId = roleArray[i];
						sql.append(" tsrmRes.roleIdStr like '%" + roleId + "%' ");
						if(i < roleArray.length - 1) {
							sql.append(" or ");
						}
					}
					sql.append(" ) ) ");
				}
				
				if (blankFlag) {
					sql.append("and EXISTS (select 1 from tab_view_role_map tvrm, tab_view_role tvr where tvrm.viewId = tvi.viewId and tvrm.crewId= ? and tvr.viewRoleId = tvrm.viewRoleId and tvr.viewRoleType = 1)");
					paramList.add(crewId);
				}
			}
		}
		sql.append(" group by tsi.id,tsi.vName,tvl.locationId,tvl.location ");
		
		StringBuilder finalSql = new StringBuilder(" select * from (" + sql.toString() + ") mytable where 1=1 ");
		
		//场数
		String minViewNum = filter.getMinViewNum() + "";
		if(StringUtil.isNotBlank(minViewNum)) {
			finalSql.append(" and viewNum >= " + minViewNum);
		}
		String maxViewNum = filter.getMaxViewNum() + "";
		if(StringUtil.isNotBlank(maxViewNum)) {
			finalSql.append(" and viewNum <= " + maxViewNum);
		}
		//完成度
		String completion = filter.getCompletion() + "";
		if(StringUtil.isNotBlank(completion)) {
			String[] completionStrs = completion.split(",");
			finalSql.append(" and ( ");
			for(int i = 0; i < completionStrs.length; i++) {
				String completionStr = completionStrs[i];
				if(completionStr.equals("1")) {//全部完成
					finalSql.append(" viewNum = finishedViewNum ");
				} else if(completionStr.equals("2")) {//部分完成
					finalSql.append(" (viewNum != finishedViewNum and finishedViewNum!=0) ");
				} else if(completionStr.equals("3")) {//未开始
					finalSql.append(" finishedViewNum=0 ");
				}
				if(i < completionStrs.length - 1) {
					finalSql.append(" or ");
				}
			}
			finalSql.append(" ) ");
		}		
		
		if(StringUtil.isBlank(sortField)) {//排序
			sortField = "shootLocation";
		}
		if(sortField.equals("shootLocation")) {//拍摄地点
			finalSql.append(" order by shootLocation is null, orderNumber,CONVERT(shootLocation USING gbk),viewNum desc,location is null,CONVERT(location USING gbk) ");
		} else if (sortField.equals("viewNum")) {//场数
			finalSql.append(" order by viewNum desc,shootLocation is null,orderNumber,CONVERT(shootLocation USING gbk),location is null,CONVERT(location USING gbk) ");
		}
		return this.query(finalSql.toString(), paramList.toArray(), null);
	}
	
	/**
	 * 根据场景id分级别查询出当前场景下的主场景、次场景、三级场景
	 * @param crewId
	 * @param viewId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryLocationByType(String viewId, Integer locationType){
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT tvl.location,tvl.locationId FROM tab_view_location tvl,tab_view_location_map tvlm WHERE tvl.locationId = tvlm.locationId");
		
		if (locationType == 1) { //查询主场景
			sql.append(" 	AND tvl.locationType = 1");
		}else if (locationType == 2) { //查询次场景
			sql.append(" 	AND tvl.locationType = 2");
		}else if (locationType == 3) { //三级场景
			sql.append(" 	AND tvl.locationType = 3");
		}
		
		sql.append(" 	AND tvlm.viewId = ?");
		
		return this.query(sql.toString(), new Object[] {viewId}, null);
	}
	
	/**
	 * 根据场景id字符串查询出所有的主场景
	 * @param viewIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> querySameLocationString(String viewIds){
		StringBuffer sql = new StringBuffer();
		sql.append("  SELECT tvl.location,tvl.locationId FROM tab_view_location tvl,tab_view_location_map tvlm");
		sql.append("  WHERE tvl.locationId = tvlm.locationId	AND tvl.locationType = 1");
		//对字符串进行处理
		String newViewIds = "'"+ viewIds.replace(",", "','") +"'";
		sql.append("  AND tvlm.viewId in ("+ newViewIds +")");
		sql.append("  ORDER BY location");
		
		return this.query(sql.toString(), null, null);
	}
	
	/**
	 * 查询出当前场景的所有的主场景次场景、三级场景
	 * @param viewId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryLocationByViewid(String viewId){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT tvl.location,tvl.locationId,tvl.locationType FROM tab_view_location tvl,tab_view_location_map tvlm");
		sql.append(" WHERE tvl.locationId = tvlm.locationId	");
		sql.append(" AND tvlm.viewId = ? 	ORDER BY tvl.locationType");
		
		return this.query(sql.toString(), new Object[] {viewId}, null);
	}
	
	/**
	 * 根据输入的场景地点信息，模糊查询出，场景的id
	 * @param viewIds
	 * @param locationStr
	 * @return
	 */
	public List<Map<String, Object>> queryViewIdByLocation(String viewIds, String locationStr){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT tvl.location,tvl.locationId,tvlm.viewId FROM tab_view_location tvl,tab_view_location_map tvlm");
		sql.append(" WHERE tvl.locationId = tvlm.locationId	AND tvl.locationType = 1");
		//对字符串进行处理
		String newViewIds = "'"+ viewIds.replace(",", "','") +"'";
		sql.append("  AND tvlm.viewId in ("+ newViewIds +")");
		sql.append(" AND tvl.location LIKE ?");
		
		return this.query(sql.toString(), new Object[] {"%" + locationStr + "%"}, null);
	}
}
