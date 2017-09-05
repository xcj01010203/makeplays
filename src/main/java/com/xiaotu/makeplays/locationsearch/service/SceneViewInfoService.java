package com.xiaotu.makeplays.locationsearch.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.attachment.dao.AttachmentDao;
import com.xiaotu.makeplays.attachment.dao.AttachmentPacketDao;
import com.xiaotu.makeplays.attachment.model.AttachmentModel;
import com.xiaotu.makeplays.attachment.model.AttachmentPacketModel;
import com.xiaotu.makeplays.locationsearch.dao.SceneViewInfoDao;
import com.xiaotu.makeplays.locationsearch.model.SceneViewInfoModel;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.view.dao.ViewInfoDao;

/**
 * 剧组堪景功能 service
 * 
 * @author Administrator
 *
 */
@Service
public class SceneViewInfoService {

	private static final int NUM = 6;
	
	@Autowired
	private ViewInfoDao viewInfoDao;
	@Autowired
	private SceneViewInfoDao sceneViewInfoDao;
	
	@Autowired
	private AttachmentDao attachmentDao;
	
	@Autowired
	private AttachmentPacketDao attachmentPacketDao;
	
	/**
	 * 查询通告单下的场景中拍摄地点信息
	 * 按照通告单下场景的先后顺序排列
	 * 该方法没有考虑到拍摄地点为空的情况
	 * @param crewId
	 * @param noticeId
	 * @return
	 */
	public List<SceneViewInfoModel> queryShootLocationByNoticeId(String crewId, String noticeId) {
		return this.sceneViewInfoDao.queryShootLocationByNoticeId(crewId, noticeId);
	}
	
	
	/**
	 * 通过场景ID查找场景信息
	 * @param viewId
	 * @return
	 */
	public SceneViewInfoModel queryOneByShootLocationId (String shootLocationId) {
		return this.sceneViewInfoDao.queryOneByShootLocationId(shootLocationId);
	}
	/**
	 * 使用剧本id查询剧组拍摄地址
	 * @param crewId
	 * @return
	 */
	public List<SceneViewInfoModel> queryShootAddressByCrewId(String crewId){
		return this.sceneViewInfoDao.queryShootAddressByCrewId(crewId);
	}
	public List<Map<String, Object>> queryData(String crewId){
		List<Map<String, Object>> list = sceneViewInfoDao.queryData(crewId);
		Map<String,String> idAndParentId = new HashMap<String, String>();
		
		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
		Map<String, Object> data = null;
		for(Map<String, Object> temp:list){
			
			String location = temp.get("location")!=null?temp.get("location").toString():"";
			String locationid = temp.get("locationid")!=null?temp.get("locationid").toString():"";
			String viewid = temp.get("viewid")!=null?temp.get("viewid").toString():"";
			String[] locationArray = location.split(",");
			String[] locationidArray = locationid.split(",");
			String[] viewidArray = viewid.split(",");
			
			for(int i = 0,le = locationidArray.length;i<le;i++){
				data = new HashMap<String, Object>();
				String lid = locationidArray[i];
				/*String lidStr = idAndParentId.get(lid);
				if(StringUtils.isBlank(lidStr)){
					if(i==0){
						lidStr = "pid";
					}else{
						lidStr = locationidArray[i-1];
					}
					idAndParentId.put(lid, lidStr);
					data.put("id", lid);
					data.put("parentId", lidStr);
					data.put("text", locationArray[i]);
					data.put("viewid", locationArray[i]);
					dataList.add(data);
				}*/
				String parentId = "";
				if(i == 0){
					parentId = "pid";
				}else{
					parentId = locationidArray[i-1];
				}
				String oldSonId = idAndParentId.get(parentId);
				if(oldSonId == null){
					oldSonId = "";
				}
				if(!oldSonId.contains(lid)){
					String newSonId = oldSonId +","+lid;
					idAndParentId.put(parentId, newSonId);
					
					data.put("id", lid);
					data.put("parentId", parentId);
					data.put("text", locationArray[i]);
					data.put("viewid", locationArray[i]);
					dataList.add(data);
				}
				
			}
		}
		return dataList;
	}
	
		
	
	
	
	//==============================================================================================================
	
	/**
	 * @Description  更新实景信息排序
	 * @param idArray
	 */
	public void updateOrder(String[] idArray){
		sceneViewInfoDao.updateOrder(idArray);
	}
	
	
	
	
	/**
	 * @Description  根据剧组id查询当前剧组戏量最多的  num个角色
	 * @return
	 */
	public List<Map<String, Object>> queryCrewRoleNumberByCrewId(String crewId){
		List<Map<String, Object>> list = sceneViewInfoDao.queryCrewRoleNumberByCrewId(crewId,NUM);
		return list;
	}
	
	
	private  Map<Object, Object> getTitleMap(){
		Map<Object, Object> titleMap = new LinkedHashMap<Object, Object>();
		titleMap.put("id", "id");
		titleMap.put("地域", "vcity");
		titleMap.put("名称", "vname");
		titleMap.put("主场景", "location");
		titleMap.put("页数", "pageCount");
		titleMap.put("场数", "siteNum");
		titleMap.put("日夜比例", "d_n");
		return titleMap;
	}
	
	
	private Map<Object, Object> getTitleForCheckMap(){
		Map<Object, Object> titleMap = new LinkedHashMap<Object, Object>();
		titleMap.put("id", "id");
		titleMap.put("主场景", "location");
		titleMap.put("时间", "playTime");
		titleMap.put("场/页数", "sitePage");
		titleMap.put("日夜比例", "d_n");
		return titleMap;
	}
	public  Map<String, Object> querySceneViewInfoById(String id) throws NoSuchMethodException, Exception{
		Map<String, Object> back = new HashMap<String, Object>();
		SceneViewInfoModel sceneViewInfoModel = sceneViewInfoDao.querySceneViewInfoById(id);
		List<AttachmentModel> attachmentList = this.attachmentDao.queryByPackId(id);
		back.put("attachmentList", attachmentList);
		back.put("sceneViewInfoModel", sceneViewInfoModel);
		return back;
		
	}
	
	
	/**
	 * @Description 查询勘景简要信息
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> querySceneViewBaseInfo(String crewId){
		List<Map<String, Object>> list = sceneViewInfoDao.querySceneViewBaseInfo(crewId);
		return list;
	}
	
	
	/**
	 * 查询当前剧组下的实景信息
	 * 
	 * @param crewId 剧组id
	 * @return
	 */
	public Map<String, Object> querySceneViewInfo(String crewId){
		Map<String, Object> result = new HashMap<String, Object>();
		Map<Object, Object> titleMap = this.getTitleMap();
		
		List<Map<String, Object>> roleList = this.queryCrewRoleNumberByCrewId(crewId);
		if(roleList == null || roleList.size() == 0){
			//throw new IllegalArgumentException("查询剧组角色失败");
		}else{
			for(Map<String, Object> tMap :roleList){
				titleMap.put(tMap.get("viewrolename"),tMap.get("viewroleid") );
			}
		}
		
		long start = System.currentTimeMillis();
		List<Map<String, Object>> list = sceneViewInfoDao.querySceneViewInfo(crewId);
		
				
		for(Map<String, Object> tMap :list){
			String day = tMap.get("day")!=null?tMap.get("day").toString():"0";
			String night = tMap.get("night")!=null?tMap.get("night").toString():"0";
			tMap.put("d_n", day+":"+night);
			String roleIds = tMap.get("roleids")!=null?tMap.get("roleids").toString():"";
			if(StringUtils.isNotBlank(roleIds)){
				String[] roleIdArray = roleIds.split(",");
				if(roleIdArray!=null&&roleIdArray.length!=0){
					for(String rid :roleIdArray){
						Integer num = tMap.get(rid)!=null?Integer.valueOf(tMap.get(rid).toString())+1:1;
						tMap.put(rid, num);
					}
				}
			}
		}
		long end  = System.currentTimeMillis();
		System.out.println("query -----"+(end-start));
		result.put("titleMap", titleMap);//表格头
		result.put("result", list);
		
		return result;
	}
	/**
	 * @Description 判断当前剧组是否已经添加了同样的场景信息（根据剧组id，实景名称）判断
	 * @param request
	 * @param sceneViewName  实景名称
	 * @return
	 */
	public List<SceneViewInfoModel> querySceneViewForHasSameName(String crewId,String sceneViewName){
		List<SceneViewInfoModel> list = sceneViewInfoDao.querySceneViewForHasSameName(crewId,sceneViewName);
		return list;
	}	
	
	
	/**
	 * @Description 保存/修改实景信息（保存实景信息后返回id 再上传图片）
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
	 * @param hasProp 是否有道具陈设
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
	 * @param orderNumber 排序标号
	 * @param crewId 剧组id
	 * @return
	 */
	public String saveOrUpdateSceneViewInfo(String id,String vName,String vCity,String vAddress,
			String vLongitude,String vLatitude,String distanceToHotel,String holePeoples,String deviceSpace,Integer isModifyView,
			String modifyViewCost,String modifyViewTime,Integer hasProp,String propCost,String propTime,String enterViewDate,
			String leaveViewDate,String viewUseTime,String contactNo,String contactName,String contactRole,String viewPrice,String freeStartDate,
			String freeEndDate,String remark,String crewId) throws Exception{
		if(StringUtils.isBlank(id)){
			id = UUIDUtils.getId();
			sceneViewInfoDao.saveSceneViewInfo(id,vName,vCity,vAddress,vLongitude,vLatitude,distanceToHotel,holePeoples,deviceSpace,isModifyView,
					  modifyViewCost,modifyViewTime,hasProp,propCost,propTime,enterViewDate,leaveViewDate,viewUseTime,contactNo,contactName,
					  contactRole,viewPrice,freeStartDate,freeEndDate,remark,crewId);
			
			saveAttachmentPacketModel(id,crewId);
		}else{
			sceneViewInfoDao.updateSceneViewInfo(id,vName,vCity,vAddress,vLongitude,vLatitude,distanceToHotel,holePeoples,deviceSpace,isModifyView,
					  modifyViewCost,modifyViewTime,hasProp,propCost,propTime,enterViewDate,leaveViewDate,viewUseTime,contactNo,contactName,
					  contactRole,viewPrice,freeStartDate,freeEndDate,remark);
		}
		return id;
	}
	
	
	private void saveAttachmentPacketModel(String id ,String crewId) throws Exception{
		//保存附件
		AttachmentPacketModel attachmentPacketModel = new AttachmentPacketModel();
		String packedId = id;
		attachmentPacketModel.setId(packedId);
		attachmentPacketModel.setCrewId(crewId);
		attachmentPacketModel.setCreateTime(new Date());
		this.attachmentPacketDao.add(attachmentPacketModel);
	}
	
	/**
	 * 删除实景信息  并同时删除附件（图片）信息
	 * 
	 * @param id
	 * @throws Exception 
	 */
	public void delSceneViewInfo(String id) throws Exception{
		//删除实景信息
		sceneViewInfoDao.delSceneViewInfo(id);
		//删除勘景信息时修改场景信息中的拍摄地id
		viewInfoDao.updateShootLocatinId(id);
		//删除实景-主场景配置信息
		//sceneviewViewinfoMapDao.delSceneViewMapInfoBySceneViewId(id);
		//删除附件包信息
		attachmentPacketDao.deleteOne(id, "id", AttachmentPacketModel.TABLE_NAME);
		//删除图片信息
		attachmentDao.deleteOne(id, "attpackId", AttachmentModel.TABLE_NAME);
	}
	
	/**
	 * 保存实景与主场景的对照信息
	 * 
	 *  ：（保存新添加进来的信息）
	 * 			2,保存对照信息
	 * @param sceneviewViewinfoMapModel
	 * @throws Exception
	 */
	public void saveSceneViewViewInfoMap(String sceneViewInfoId,String locationId,String crewId) throws Exception{
		//保存现在的信息
		String[] locationIds = locationId.split("##");
		if(locationIds!=null&&locationIds.length>0){
			sceneViewInfoDao.modifySceneViewMapInfo(sceneViewInfoId, locationIds,crewId);
		}
		
	}
	
	
	
	/**
	 * 查询当前实景已经配置的主场景信息或者查询当前剧组下的没有被配置的场景信息
	 * 
	 * 
	 * @param haschecek   是否已经被配置到主场景     true:已经配置（查询当前实景已经配置的场景信息）
	 * 										false:没有配置（查询备选场景信息）
	 * @param crewId     剧组id
	 * @param id		 实景id
	 * @return
	 */
	public Map<String, Object> queryHasCheckOrAlternativeViewInfoForSceneView(Boolean haschecek,String crewId,String idOrlocation){
		Map<String, Object> result = new HashMap<String, Object>();
		Map<Object, Object> titleMap = this.getTitleForCheckMap(); 
		
		List<Map<String, Object>> roleList = this.queryCrewRoleNumberByCrewId(crewId);
		if(roleList == null || roleList.size() == 0){
			//throw new IllegalArgumentException("查询剧组角色失败");
		}else{
			for(Map<String, Object> tMap :roleList){
				titleMap.put(tMap.get("viewrolename"),tMap.get("viewroleid") );
			}
		}
		//查询备选或者已选数据
		List<Map<String, Object>> list = sceneViewInfoDao.queryHasCheckOrAlternativeViewInfoForSceneView(haschecek,crewId,idOrlocation);
		//获取属性结构数据
		List<Map<String, Object>> treeData = queryData(crewId);
		
		Set<String> parentId = new HashSet<String>();
		//将数据关联上树结构
		
		for(Map<String, Object> tMap :list){
			String locationId = tMap.get("locationid")!=null?tMap.get("locationid").toString():"";
			if("".equals(locationId)){
				tMap.put("location", "[空场景名称]");
			}
			String day = tMap.get("day")!=null?tMap.get("day").toString():"0";
			String night = tMap.get("night")!=null?tMap.get("night").toString():"0";
			String pageCount = tMap.get("pageCount")!=null?tMap.get("pageCount").toString():"0";
			String siteNum = tMap.get("siteNum")!=null?tMap.get("siteNum").toString():"0";
			tMap.put("sitePage", siteNum+"场/"+new DecimalFormat("#0.0").format(Double.valueOf(pageCount))+"页");
			tMap.put("d_n", day+":"+night);
			String roleIds = tMap.get("roleids")!=null?tMap.get("roleids").toString():"";
			if(StringUtils.isNotBlank(roleIds)){
				String[] roleIdArray = roleIds.split(",");
				if(roleIdArray!=null&&roleIdArray.length!=0){
					for(String rid :roleIdArray){
						Integer num = tMap.get(rid)!=null?Integer.valueOf(tMap.get(rid).toString())+1:1;
						tMap.put(rid, num);
					}
				}
			}
			/*//将数据关联上树结构
			if(treeData!=null && treeData.size()>0){
				for(Map<String, Object> tempTree :treeData){
					String lid = tempTree.get("id")!=null?tempTree.get("id").toString():"";
					String pid = tempTree.get("parentId")!=null?tempTree.get("parentId").toString():"";
					if(locationId.equals(lid)||"".equals(locationId)){
						if(!"pid".equals(pid)){
							parentId.add(pid);
						}
						
						tMap.putAll(tempTree);
						//break;
					}
				}
			}*/
		}
		/*//如果是通过名称查询备选场景 添加父节点
		if(!haschecek&&StringUtils.isNotBlank(idOrlocation)){
			Iterator<String> it = parentId.iterator();
			while(it.hasNext()){
				String id = it.next();
				addParentInfo(id,treeData,list);
			}
		}*/
		
		
		
		result.put("titleMap", titleMap);//表格头
		result.put("result", list);
		return result;
	}
	
	
	private void addParentInfo(String sonId,List<Map<String, Object>> treeData,List<Map<String, Object>> addData){
		for(Map<String, Object> tt:treeData){
			String lid = tt.get("id")!=null?tt.get("id").toString():"";
			String pid = tt.get("parentId")!=null?tt.get("parentId").toString():"";
			String location = tt.get("text")!=null?tt.get("text").toString():"";
			if(sonId.equals(lid)){
				Map<String, Object> tmp = new HashMap<String, Object>();
				tmp.put("location", location);
				tmp.put("id", lid);
				tmp.put("locationId", lid);
				tmp.put("parentId", pid);
				addData.add(tmp);
				if(!"pid".equals(pid)){
					addParentInfo(pid,treeData,addData);
				}
			}
		}
	}
	
	
	
	
	/**
	 * @Description  删除已经配置的主场景信息
	 * @param sceneViewInfoId 实景id
	 * @param locationId    主场景id
	 * @return
	 */
	public void delSceneViewMapInfo(String[] locationIdArray,String crewId){
		sceneViewInfoDao.modifySceneViewMapInfo(null,locationIdArray,crewId);
	}
	
	/**
	 * 根据拍摄地的id查询出实景地的详细信息
	 * @param id
	 * @return
	 * @throws NoSuchMethodException
	 * @throws Exception
	 */
	public SceneViewInfoModel querySceneViewById(String id) throws NoSuchMethodException, Exception {
		return this.sceneViewInfoDao.querySceneViewInfoById(id);
	}
	
	/**
	 * 根据实体类更新场景经纬度信息
	 * @param model
	 * @throws Exception 
	 */
	public void updateSceneByEntity(SceneViewInfoModel model) throws Exception {
		this.sceneViewInfoDao.updateWithNull(model, "id");
	}
	
	/**
	 * 查询已存在的地址
	 * @param address
	 * @param crewId
	 * @return
	 */
	public SceneViewInfoModel queryShootAddressByAddress(String address,String crewId){
		return this.sceneViewInfoDao.queryShootAddressByAddress(address, crewId);
	}
	
	/**
	 * 查询所有的省市信息
	 */
	public List<String> queryAllProCity() {
		return this.sceneViewInfoDao.queryAllProCity();
	}
}
