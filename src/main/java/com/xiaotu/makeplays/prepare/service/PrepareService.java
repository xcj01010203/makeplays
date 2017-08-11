package com.xiaotu.makeplays.prepare.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.locationsearch.dao.SceneViewInfoDao;
import com.xiaotu.makeplays.prepare.dao.PrepareArteffectLocationDao;
import com.xiaotu.makeplays.prepare.dao.PrepareArteffectRoleDao;
import com.xiaotu.makeplays.prepare.dao.PrepareCrewPeopleDao;
import com.xiaotu.makeplays.prepare.dao.PrepareExtensionDao;
import com.xiaotu.makeplays.prepare.dao.PrepareOperateDao;
import com.xiaotu.makeplays.prepare.dao.PrepareRoleDao;
import com.xiaotu.makeplays.prepare.dao.PrepareWorkDao;
import com.xiaotu.makeplays.prepare.model.PrepareArteffectRoleModel;
import com.xiaotu.makeplays.prepare.model.PrepareOperateModel;
import com.xiaotu.makeplays.prepare.model.PrepareWorkModel;
import com.xiaotu.makeplays.prepare.utils.PrepareCrewPeopleUtil;
import com.xiaotu.makeplays.prepare.utils.PrepareOperateUtil;
import com.xiaotu.makeplays.prepare.utils.PrepareRoleUtil;
import com.xiaotu.makeplays.prepare.utils.PrepareWorkUtil;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * @ClassName PrepareService
 * @Description 筹备期
 * @author Administrator
 * @Date 2017年2月10日 上午10:41:26
 * @version 1.0.0
 */
@Service
public class PrepareService {

	@Autowired
	private PrepareArteffectLocationDao prepareArteffectLocationDao;
	
	@Autowired
	private PrepareArteffectRoleDao prepareArteffectRoleDao;
	
	@Autowired
	private PrepareCrewPeopleDao prepareCrewPeopleDao;
	
	@Autowired
	private PrepareExtensionDao prepareExtensionDao;
	
	@Autowired
	private PrepareOperateDao prepareOperateDao;
	
	@Autowired
	private PrepareRoleDao prepareRoleDao;
	
	
	@Autowired
	private PrepareWorkDao prepareWorkDao; 
	
	@Autowired
	private SceneViewInfoDao sceneViewInfoDao;
	/**
	 * @Description 批量保存、修改 角色信息
	 * @throws Exception
	 */
	public String saveOrUpdatePrepareArteffectRoleInfo(String role,String modelling,String confirmDate,
			String status,String mark,String reviewer,String id,String crewId) throws Exception{
		if(StringUtils.isBlank(confirmDate)){
			confirmDate = null;
		}
		if(StringUtils.isBlank(id) || "blank".equals(id)){
			id = UUIDUtils.getId();
			prepareArteffectRoleDao.saveRoleInfo(role,modelling,confirmDate,status,mark,reviewer,id,crewId);
		}else{
			prepareArteffectRoleDao.updateRoleInfo(role,modelling,confirmDate,status,mark,reviewer,id);
		}
		return id;
		
	}
	
	
	/**
	 * @Description 根据剧组id查询筹备期   角色信息
	 * @param crewId
	 * @return
	 */
	public String queryPrepareRoleInfo(String crewId){
		String back = "";
		List<Map<String, Object>> list =prepareRoleDao.queryPrepareRoleInfo(crewId);
		if(list!=null && list.size()>0){
			PrepareRoleUtil root  = new PrepareRoleUtil();
			root.setId("0");
			root.setParentId("0");
			PrepareRoleUtil node  = null;
			for(Map<String, Object> temp:list){
				node = new PrepareRoleUtil();
				String id = temp.get("id")!=null?temp.get("id").toString():"";
				String role = temp.get("role")!=null?temp.get("role").toString():"";
				String actor = temp.get("actor")!=null?temp.get("actor").toString():"";
				String schedule = temp.get("schedule")!=null?temp.get("schedule").toString():"";
				String content = temp.get("content")!=null?temp.get("content").toString():"";
				String mark = temp.get("mark")!=null?temp.get("mark").toString():"";
				String parentId = temp.get("parentId")!=null?temp.get("parentId").toString():"";
				
				node.setId(id);
				node.setRole(role);
				node.setActor(actor);
				node.setSchedule(schedule);
				node.setContent(content);
				node.setMark(mark);
				node.setParentId(parentId);
				root.add(node);
			}
			JSONArray obj = JSONArray.fromObject(root.getChildren());// 不要根  
			back = obj.toString();
		}
		return back; 
	}
	
	
	
	
	/**
	 * @Description 批量保存、修改 剧组人员信息
	 * @throws Exception
	 */
	public String saveOrUpdatePrepareCrewPeopleInfo(String id,String groupName,String duties,String name,String phone,String reviewer,
			String confirmDate,String arrivalTime,String payment,String parentId,String crewId) throws Exception{
		if(StringUtils.isBlank(confirmDate)){
			confirmDate = null;
		}
		if(StringUtils.isBlank(arrivalTime)){
			arrivalTime = null;
		}
		if(StringUtils.isBlank(payment)){
			payment = null;
		}
		if(StringUtils.isBlank(id)){
			id = UUIDUtils.getId();
			prepareCrewPeopleDao.savePrepareCrewPeopleInfo(id,groupName,duties,name,phone,reviewer,confirmDate,arrivalTime,payment,parentId,crewId);
		}else{
			prepareCrewPeopleDao.updatePrepareCrewPeopleInfo(id,groupName,duties,name,phone,reviewer,confirmDate,arrivalTime,payment);
		}
		return id;
		
	}
	
	
	/**
	 * @Description 根据剧组id查询筹备期   剧组人员信息
	 * @param crewId
	 * @return
	 */
	public String queryPrepareCrewPeopleInfo(String crewId){
		String back = "";
		List<Map<String, Object>> list = prepareCrewPeopleDao.queryPrepareCrewPeopleInfo(crewId);
		if(list!=null && list.size() > 0){
			PrepareCrewPeopleUtil root = new PrepareCrewPeopleUtil();
			root.setId("0");
			root.setParentId("0");
			PrepareCrewPeopleUtil node = null;
			for(Map<String, Object> temp:list){
				node = new  PrepareCrewPeopleUtil();
				
				String id = temp.get("id")!=null?temp.get("id").toString():"";
				String groupName = temp.get("groupName")!=null?temp.get("groupName").toString():"";
				String duties = temp.get("duties")!=null?temp.get("duties").toString():"";
				String name = temp.get("name")!=null?temp.get("name").toString():"";
				String phone = temp.get("phone")!=null?temp.get("phone").toString():"";
				String reviewer = temp.get("reviewer")!=null?temp.get("reviewer").toString():"";
				String confirmDate = temp.get("confirmDate")!=null?temp.get("confirmDate").toString():"";
				String arrivalTime = temp.get("arrivalTime")!=null?temp.get("arrivalTime").toString():"";
				String payment = temp.get("payment")!=null?temp.get("payment").toString():"";
				String parentId = temp.get("parentId")!=null?temp.get("parentId").toString():"";
				
				node.setId(id);
				node.setParentId(parentId);
				node.setGroupName(groupName);
				node.setDuties(duties);
				node.setName(name);
				node.setPhone(phone);
				node.setReviewer(reviewer);
				node.setConfirmDate(confirmDate);
				node.setArrivalTime(arrivalTime);
				node.setPayment(payment);
				root.add(node);
			}
			JSONArray json = JSONArray.fromObject(root.getChildren());
			back = json.toString();
		}
		return back;
	}
	
	
	
	
	
	/**
	 * @Description 批量保存、修改 美术视觉角色信息
	 * @throws Exception
	 */
	public void saveOrUpdatePrepareArteffectRoleInfo() throws Exception{
		List<PrepareArteffectRoleModel> addList = new ArrayList<PrepareArteffectRoleModel>();
		List<PrepareArteffectRoleModel> updateList = new ArrayList<PrepareArteffectRoleModel>();
		
		prepareArteffectRoleDao.addBatch(addList, PrepareArteffectRoleModel.class);
		prepareArteffectRoleDao.updateBatch(updateList,"id",PrepareArteffectRoleModel.class);
		
	}
	
	
	/**
	 * @Description 根据剧组id查询筹备期   美术视觉角色信息
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryPrepareArteffectRoleInfo(String crewId){
		return prepareArteffectRoleDao.queryPrepareArteffectRoleInfo(crewId);
	}
	
	
	public void delPrepareArteffectRoleInfo(String id){
		prepareArteffectRoleDao.delPrepareArteffectRoleInfo(id);
	}
	
	
	/**
	 * @Description 批量保存、修改 美术视觉场景信息
	 * @throws Exception
	 */
	public String  saveOrUpdatePrepareArteffectLocationInfo(String location,String designSketch,String designSketchDate,String workDraw,
			String workDrawDate,String scenery,String sceneryDate,String reviewer,String opinion,String id,String crewId) throws Exception{
		if(StringUtils.isBlank(designSketchDate)){
			designSketchDate = null;
		}
		if(StringUtils.isBlank(workDrawDate)){
			workDrawDate = null;
		}
		if(StringUtils.isBlank(sceneryDate)){
			sceneryDate = null;
		}
		if(StringUtils.isBlank(id) || "blank".equals(id)){
			id = UUIDUtils.getId();
			prepareArteffectLocationDao.savePrepareArteffectLocationInfo(location,designSketch,designSketchDate,workDraw,
    				workDrawDate,scenery,sceneryDate,reviewer,opinion,id,crewId);
		}else{
			prepareArteffectLocationDao.updatePrepareArteffectLocationInfo(location,designSketch,designSketchDate,workDraw,
    				workDrawDate,scenery,sceneryDate,reviewer,opinion,id);
		}
		return id;
	}
	
	
	/**
	 * @Description 根据剧组id查询筹备期   美术视觉场景信息
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryPrepareArteffectLocationInfo(String crewId){
		return prepareArteffectLocationDao.queryPrepareArteffectLocationInfo(crewId);
	}
	
	
	
	
	/**
	 * @Description 批量保存、修改 宣传进度信息
	 * @throws Exception
	 */
	public String  saveOrUpdatePrepareExtensionInfo(String id,String type,String material,String personLiable,String reviewer,String crewId) throws Exception{
		if(StringUtils.isBlank(id) || "blank".equals(id) ){
			id = UUIDUtils.getId();
			prepareExtensionDao.saveExtensionInfo(id,type,material,personLiable,reviewer,crewId);
		}else{
			prepareExtensionDao.updateExtensionInfo(id,type,material,personLiable,reviewer);
		}
		return id;
	}
	
	
	/**
	 * @Description 根据剧组id查询筹备期   商务运营
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryPrepareExtensionInfo(String crewId){
		return prepareExtensionDao.queryPrepareExtensionInfo(crewId);
	}
	
	
	/**
	 * @Description 批量保存、修改 筹备期 商务运营信息
	 * @throws Exception
	 */
	public void saveOrUpdatePrepareOperateInfo() throws Exception{
		List<PrepareOperateModel> addList = new ArrayList<PrepareOperateModel>();
		List<PrepareOperateModel> updateList = new ArrayList<PrepareOperateModel>();
		
		prepareOperateDao.addBatch(addList, PrepareOperateModel.class);
		prepareOperateDao.updateBatch(updateList,"id",PrepareOperateModel.class);
	}
	
	/**
	 * @Description 根据剧组id查询筹备期   商务运营
	 * @param crewId
	 * @return
	 */
	public String queryPrepareOperateInfo(String crewId){
		String back = "";
		List<Map<String, Object>> list = prepareOperateDao.queryPrepareOperateInfo(crewId);
		if(list!=null && list.size()> 0){
			PrepareOperateUtil root = new PrepareOperateUtil();
			root.setId("0");
			root.setParentId("0");
			PrepareOperateUtil node = null;
			for(Map<String,Object> temp:list){
				node = new PrepareOperateUtil();
				  
				String id = temp.get("id")!=null?temp.get("id").toString():"";
				String operateType = temp.get("operateType")!=null?temp.get("operateType").toString():"";
				String operateBrand = temp.get("operateBrand")!=null?temp.get("operateBrand").toString():"";
				String operateMode = temp.get("operateMode")!=null?temp.get("operateMode").toString():"";
				String operateCost = temp.get("operateCost")!=null?temp.get("operateCost").toString():"";
				String contactName = temp.get("contactName")!=null?temp.get("contactName").toString():"";
				String phoneNumber = temp.get("phoneNumber")!=null?temp.get("phoneNumber").toString():"";
				String mark = temp.get("mark")!=null?temp.get("mark").toString():"";
				String personLiable = temp.get("personLiable")!=null?temp.get("personLiable").toString():"";
				String parentId = temp.get("parentId")!=null?temp.get("parentId").toString():"";
				
				node.setId(id);
				node.setOperateType(operateType);
				node.setOperateBrand(operateBrand);
				node.setOperateMode(operateMode);
				node.setOperateCost(operateCost);
				node.setContactName(contactName);
				node.setPhoneNumber(phoneNumber);
				node.setMark(mark);
				node.setParentId(parentId);
				node.setPersonLiable(personLiable);
				
				root.add(node);
				
			}
			
			JSONArray json = JSONArray.fromObject(root.getChildren());
			back = json.toString();
		}
		
		return back;
	}
	
	/**
	 * @Description 批量保存、修改   筹备期  办公筹备
	 * @throws Exception
	 */
	public void saveOrUpdatePrepareWorkInfo() throws Exception{
		List<PrepareWorkModel> addList = new ArrayList<PrepareWorkModel>();
		List<PrepareWorkModel> updateList = new ArrayList<PrepareWorkModel>();
		prepareWorkDao.addBatch(addList, PrepareWorkModel.class);
		prepareWorkDao.updateBatch(updateList,"id",PrepareWorkModel.class);
	}
	
	/**
	 * @Description 根据id删除一条办公筹备信息 
	 * @param id
	 */
	public void delPrepareWorkInfo(String[] id){
		prepareWorkDao.delPrepareWorkInfo(id);
	}
	
	
	/**
	 * @Description 根据剧组id查询筹备期   商务运营
	 * @param crewId
	 * @return
	 */
	public String queryPrepareWorkInfo(String crewId){
		String back = "";
		List<Map<String, Object>> list = prepareWorkDao.queryPrepareOperateInfo(crewId);
		if(list!=null && list.size()> 0 ){
			PrepareWorkUtil root = new PrepareWorkUtil();
			root.setId("0");
			root.setParentId("0");
			PrepareWorkUtil node  = null;
			for(Map<String, Object> temp :list){
				node = new PrepareWorkUtil();
				
				String id = temp.get("id")!=null?temp.get("id").toString():"";
				String type = temp.get("type")!=null?temp.get("type").toString():"";
				String purpose = temp.get("purpose")!=null?temp.get("purpose").toString():"";
				String schedule = temp.get("schedule")!=null?temp.get("schedule").toString():"";
				String personLiable = temp.get("personLiable")!=null?temp.get("personLiable").toString():"";
				String parentId = temp.get("parentId")!=null?temp.get("parentId").toString():"";
				
				node.setId(id);
				node.setType(type);
				node.setPurpose(purpose);
				node.setSchedule(schedule);
				node.setPersonLiable(personLiable);
				node.setParentId(parentId);
				root.add(node);
				
			}
			JSONArray json = JSONArray.fromObject(root.getChildren());
			back = json.toString();
		}
		return back;
	}
	
	/**
	 * @Description 保存、修改办公筹备
	 * @return
	 */
	public String saveOrUpdatePrepareWorkInfo(String id,String type,String purpose,String schedule,String personLiable,
			String parentId,String crewId){
		if(StringUtils.isBlank(id)){
			id = UUIDUtils.getId();
			prepareWorkDao.savePrepareWorkInfo(id,type,purpose,schedule,personLiable,parentId,crewId);
			
		}else{
			prepareWorkDao.updatePrepareWorkInfo(id,type,purpose,schedule,personLiable);
		}
		return id;
	}
	
	/**
	 * @Description 保存 、修改筹备运营信息
	 * @param id
	 * @param operateType 合作种类
	 * @param operateBrand 品牌
	 * @param operateMode 合作方式
	 * @param operateCost 合作费用
	 * @param contactName 联系人名称
	 * @param phoneNumber 联系电话
	 * @param mark 备注
	 * @param personLiable 负责人
	 * @param parentId 父id
	 * @param crewId
	 */
	public String saveOrUpdatePrepareOperateInfo(String id,String operateType,String operateBrand,String operateMode,String operateCost,String contactName,
			String phoneNumber,String mark,String personLiable,String parentId,String crewId){
		
		if(StringUtils.isBlank(operateCost)){
			operateCost = null;
		}
		if(StringUtils.isBlank(id)){
			id = UUIDUtils.getId();
			prepareOperateDao.savePrepareOperateInfo(id,operateType,operateBrand,operateMode,operateCost,contactName,
    				phoneNumber,mark,personLiable,parentId,crewId);
		}else{
			prepareOperateDao.updatePrepareOperateInfo(id,operateType,operateBrand,operateMode,operateCost,contactName,
    				phoneNumber,mark,personLiable);
		}
		
		
		return id;
		
	}
	
	
	
	
	/**
	 * @Description  根据id删除一条筹备期美术视觉   场景信息
	 * @param id
	 */
	public void delPrepareArteffectLocationInfo(String id){
		prepareArteffectLocationDao.delPrepareArteffectLocationInfo(id);
	}
	
	
	/**
	 * @Description 根据id删除一条宣传进度信息 
	 * @param id
	 */
	public void delPrepareExtensionInfo(String id){
		prepareExtensionDao.delPrepareExtensionInfo(id);
	}
	
	
	public String saveOrUpdatePrepareRoleInfo(String id,String role,
			String actor,String schedule,String content,String mark,String parentId,String crewId){
		if(StringUtils.isBlank(id) || "blank".equals(id)){
			id = UUIDUtils.getId();
			prepareRoleDao.savePrepareRoleInfo(id,role,actor,schedule,content,mark,parentId,crewId);
		}else{
			prepareRoleDao.updatePrepareRoleInfo(id,role,actor,schedule,content,mark);
		}
		return id;
	}
	
	public void delPrepareRoleInfo(String[] id){
		prepareRoleDao.delPrepareRoleInfo(id);
	}
	
	public void delPrepareCrewPeopleInfo(String[] id){
		prepareCrewPeopleDao.delPrepareCrewPeopleInfo(id);
	}
	public void delPrepareOperateInfo(String[] id){
		prepareOperateDao.delPrepareOperateInfo(id);
	}
}
