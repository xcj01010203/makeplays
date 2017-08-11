package com.xiaotu.makeplays.crewPicture.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.attachment.dao.AttachmentDao;
import com.xiaotu.makeplays.attachment.dao.AttachmentPacketDao;
import com.xiaotu.makeplays.attachment.model.AttachmentModel;
import com.xiaotu.makeplays.attachment.model.AttachmentPacketModel;
import com.xiaotu.makeplays.attachment.model.constants.AttachmentBuzType;
import com.xiaotu.makeplays.attachment.service.AttachmentService;
import com.xiaotu.makeplays.crewPicture.dao.CrewPictureInfoDao;
import com.xiaotu.makeplays.crewPicture.model.CrewPictureInfoModel;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 剧照操作的service
 * @author wanrenyi 2017年2月28日下午3:50:10
 */
@Service
public class CrewPictureInfoService extends AttachmentService{

	@Autowired
	private CrewPictureInfoDao crewPictureInfoDao;
	
	@Autowired
	private AttachmentPacketDao attachmentPacketDao;
	
	@Autowired
	private AttachmentDao attachmentDao;
	
	/**
	 * 保存剧照信息
	 * @param crewId
	 * @param pictureId
	 * @param pictureGroupName
	 * @return
	 * @throws Exception 
	 */
	public Map<String, Object> saveCrewPictureInfo(String crewId, String pictureId, String pictureGroupName,  
				String picturePassword, String userId) throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		Date nowDate = new Date();
		String attachmentPacketId = "";
		//根据剧照id进行判断，如果剧照id为空时表示是新建剧照信息；如果不为空时，是修改剧照信息
		if (StringUtils.isBlank(pictureId)) {
			String packetId = UUIDUtils.getId();
			//先创建附件包信息
			AttachmentPacketModel packModel = new AttachmentPacketModel();
			packModel.setId(packetId);
			packModel.setBuzType(AttachmentBuzType.CrewPicture.getValue());
			packModel.setContainAttment(false);
			packModel.setCreateTime(nowDate);
			packModel.setCrewId(crewId);
			packModel.setRelatedToBuz(true);
			attachmentPacketDao.add(packModel);
			
			attachmentPacketId = packetId;
			//新建剧照信息
			CrewPictureInfoModel pictureModel = new CrewPictureInfoModel();
			pictureId = UUIDUtils.getId();
			pictureModel.setAttpackId(packetId);
			pictureModel.setAttpackName(pictureGroupName);
			pictureModel.setCreateTime(nowDate);
			pictureModel.setCrewId(crewId);
			pictureModel.setId(pictureId);
			pictureModel.setPicturePassword(picturePassword);
			pictureModel.setCreateUser(userId);
			crewPictureInfoDao.add(pictureModel);
		}else {
			//修改当前剧照分组的名称
			//根据剧照id查询出剧照信息
			CrewPictureInfoModel model = crewPictureInfoDao.queryPictureInfoById(pictureId);
			if (StringUtils.isNotBlank(pictureGroupName)) {
				model.setAttpackName(pictureGroupName);
			}
			if (StringUtils.isNotBlank(picturePassword)) {
				model.setPicturePassword(picturePassword);
			}
			crewPictureInfoDao.updateWithNull(model, "id");
			
			attachmentPacketId = model.getAttpackId();
		}
		data.put("pictureId", pictureId);
		data.put("packetId", attachmentPacketId);
		return data;
	} 
	
	/**
	 * 查询剧组中剧照分组的所有名称列表（同时查询出剧照id和附件包Id）
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryPictreGroupNameList(String crewId){
		return this.crewPictureInfoDao.queryPictureGroupNameList(crewId);
	}
	
	/**
	 * 根据分组名称，查询分组列表
	 * @param crewId
	 * @param groupName
	 * @return
	 */
	public List<Map<String, Object>> queryPictureListByGroupName(String crewId, String groupName){
		return this.crewPictureInfoDao.queryIsExistGroupName(crewId, groupName);
	}
	
	/**
	 * 根据id获取剧照的详细信息
	 * @param crewPictureId
	 * @param crewId
	 * @return
	 * @throws Exception 
	 */
	public CrewPictureInfoModel queryCrewPictureInfoById(String crewPictureId, String crewId) throws Exception {
		return this.crewPictureInfoDao.queryCrewPictureInfoById(crewPictureId, crewId);
	}
	
	/**
	 * 更新剧照信息
	 * @param model
	 * @throws Exception 
	 */
	public void updateCrewPictureInfo(CrewPictureInfoModel model) throws Exception {
		this.crewPictureInfoDao.updateWithNull(model, "id");
	}
	
	/**
	 * 删除剧照分组
	 * @param attachmentId
	 * @throws Exception 
	 */
	public void deleteCrewPictureAndAttachment(String crewPictureId, String crewId) throws Exception {
		//根据id查询出对象信息
		CrewPictureInfoModel model = this.crewPictureInfoDao.queryCrewPictureInfoById(crewPictureId, crewId);
		if (null == model) {
			throw new IllegalArgumentException("不存在当前分组信息，请刷新后重试");
		}
		
		//删除分组中的附件和对应的附件包信息
		this.deleteByPackId(model.getAttpackId());
		
		//删除剧照分组信息
		this.crewPictureInfoDao.deleteOne(crewPictureId, "id", CrewPictureInfoModel.TABLE_NAME);
	}
	
	/**
	 * 获取当前剧组中的相册的列表信息
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryCrewPictureInfoList(String crewId, String crewPictureId){
		//先桉顺序取出多有的相册信息
		List<Map<String, Object>> crewPictureList = this.crewPictureInfoDao.queryCrewPictureList(crewId, crewPictureId);
		if (crewPictureList != null && crewPictureList.size()>0) {
			for (Map<String, Object> map : crewPictureList) {
				//取出附加包id
				String attachPacketId = (String) map.get("attpackId");
				//根据附件包id查询出最先上传的那张照片的信息
				List<AttachmentModel> attachmentList = this.attachmentDao.queryByPackId(attachPacketId);
				//取出最后一条记录即为第一条上传的
				if (attachmentList != null && attachmentList.size()>0) {
					AttachmentModel model = attachmentList.get(attachmentList.size()-1);
					map.put("pictureCount", attachmentList.size());
					map.put("sdStorePath", model.getSdStorePath());
					map.put("hdStorePath", model.getHdStorePath());
				}else {
					map.put("pictureCount",0);
				}
			}
		}
		return crewPictureList;
	}
	
	/**
	 * 获取当前分组中的所有的照片
	 * @param crewPictureId
	 * @return
	 * @throws IOException 
	 */
	public List<Map<String, Object>> queryAttachmentListByPictureId(String crewPictureId) throws IOException{
		List<Map<String,Object>> list = this.crewPictureInfoDao.queryAttachmentListByCrewPictureId(crewPictureId);
		for (Map<String, Object> map : list) {
			String sdStorePath = (String) map.get("sdStorePath");
			String hdStorePath = (String) map.get("hdStorePath");
			if (StringUtils.isNotBlank(sdStorePath)) {
				String suffix = "";
				if(sdStorePath.lastIndexOf(".") != -1) {
					suffix = sdStorePath.substring(sdStorePath.lastIndexOf("."), sdStorePath.length());
				}
				String genPreviewPath = FileUtils.genPreviewPath(sdStorePath);
				map.put("sdStorePath", genPreviewPath);
				map.put("suffix", suffix);
			}
			
			if (StringUtils.isNotBlank(hdStorePath)) {
				String genHdPreviewPath = FileUtils.genPreviewPath(hdStorePath);
				map.put("hdStorePath", genHdPreviewPath);
			}
		}
		
		return list;
	}
	
	/**
	 * 移动当前相册的封面图片后，将当前相册的封面设置为空
	 * @param attachmentIds
	 * @param crewId
	 */
	public void updateCrewPictureIndexId(String[] attachmentIds, String crewId) {
		this.crewPictureInfoDao.updateCrewPictureIndexId(attachmentIds, crewId);
	}
}
