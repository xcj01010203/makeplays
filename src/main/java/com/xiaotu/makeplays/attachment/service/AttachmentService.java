package com.xiaotu.makeplays.attachment.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.attachment.dao.AttachmentDao;
import com.xiaotu.makeplays.attachment.dao.AttachmentPacketDao;
import com.xiaotu.makeplays.attachment.model.AttachmentModel;
import com.xiaotu.makeplays.attachment.model.AttachmentPacketModel;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

@Service
public class AttachmentService {
	
	@Autowired
	private AttachmentDao attachmentDao;
	
	@Autowired
	private AttachmentPacketDao attachmentPacketDao;
	
	/**
	 * 根据多个条件查询附件信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<AttachmentModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.attachmentDao.queryManyByMutiCondition(conditionMap, page);
	}

	/**
	 * 根据附件包ID删除附件信息
	 * @param attpackId
	 */
	public void deleteByPackId(String attpackId) {
		//首先删除附件文件
		List<AttachmentModel> attachmentList = this.attachmentDao.queryByPackId(attpackId);
		try {
			for (AttachmentModel attachment : attachmentList) {
				if (!StringUtils.isBlank(attachment.getHdStorePath())) {
					FileUtils.deleteFile(attachment.getHdStorePath());
				}
				if (!StringUtils.isBlank(attachment.getSdStorePath())) {
					FileUtils.deleteFile(attachment.getSdStorePath());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//然后删除附件记录
		this.attachmentDao.deleteByPackId(attpackId);
		
	}
	
	/**
	 * 删除单个附件
	 * @param attachmentId
	 * @throws Exception 
	 */
	public void deleteById(String attachmentId) throws Exception {
		AttachmentModel attachment = this.attachmentDao.queryById(attachmentId);
		
		try {
			if (!StringUtils.isBlank(attachment.getHdStorePath())) {
				FileUtils.deleteFile(attachment.getHdStorePath());
			}
			if (!StringUtils.isBlank(attachment.getSdStorePath())) {
				FileUtils.deleteFile(attachment.getSdStorePath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.attachmentDao.deleteOne(attachmentId, "id", AttachmentModel.TABLE_NAME);
		
		//校验附件包中是否还有其他附件，如果没有，则设置附件包的状态
		List<AttachmentModel> attachList = this.attachmentDao.queryByPackId(attachment.getAttpackId());
		if (attachList == null || attachList.size() == 0) {
			AttachmentPacketModel attachPacket = this.queryAttpacketById(attachment.getAttpackId());
			attachPacket.setContainAttment(false);
			this.updateAttpacket(attachPacket);
		}
	}
	
	/**
	 * 新增一条附件记录
	 * @param attachment
	 * @throws Exception 
	 */
	public void addOneAttachment(AttachmentModel attachment) throws Exception {
		this.attachmentDao.add(attachment);
	}
	
	/**
	 * 根据ID查询附件包信息
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public AttachmentPacketModel queryAttpacketById(String id) throws Exception {
		return this.attachmentPacketDao.queryById(id);
	}
	
	/**
	 * 批量查询附件包信息
	 * @param idList
	 * @return
	 */
	public List<AttachmentModel> queryAttachByPackIdList(List<String> attpackIdList){
		List<AttachmentModel> attachList = new ArrayList<AttachmentModel>();
		
		if (attpackIdList != null && attpackIdList.size() > 0) {
			attachList = this.attachmentDao.queryByPackIdList(attpackIdList);
		}
		
		return attachList;
	}
	/**
	 * 更新附件包信息
	 * @param attachmentPacket
	 * @throws Exception 
	 */
	public void updateAttpacket(AttachmentPacketModel attachmentPacket) throws Exception {
		this.attachmentPacketDao.update(attachmentPacket, "id");
	}
	
	/**
	 * 新建一个新的附件包
	 * @param buzType	附件包关联的业务类型
	 * @return
	 * @throws Exception 
	 */
	public String createNewPacket(String crewId, Integer buzType) throws Exception {
		AttachmentPacketModel attachmentPacketModel = new AttachmentPacketModel();
		
		String packedId = UUIDUtils.getId();
		attachmentPacketModel.setId(packedId);
		attachmentPacketModel.setCrewId(crewId);
		attachmentPacketModel.setCreateTime(new Date());
		if (buzType != null) {
			attachmentPacketModel.setBuzType(buzType);
			attachmentPacketModel.setRelatedToBuz(true);
		}
		
		this.attachmentPacketDao.add(attachmentPacketModel);
		
		return packedId;
	}
	
	/**
	 * 保存附件信息
	 * 如果传过来的附件包为空，则新建一个空的附件包
	 * @param crewId	剧组ID
	 * @param attpackId	附件包ID
	 * @param type	附件类型
	 * @param fileRealName	文件原来名称
	 * @param hdStorePath	高清附件预览地址
	 * @param sdStorePath	标清附件预览地址
	 * @param suffix	附件后缀
	 * @param size	附件大小
	 * @param length	附件长度
	 * @throws Exception
	 */
	public AttachmentModel saveAttachmentInfo(String crewId, String attpackId, int type,
			String fileRealName, String hdStorePath, String sdStorePath, 
			String suffix, long size, long length)
			throws Exception {
		//保存附件记录
		AttachmentModel attachment = new AttachmentModel();
		attachment.setId(UUIDUtils.getId());
		attachment.setName(fileRealName);
		attachment.setCrewId(crewId);
		attachment.setType(type);
		attachment.setHdStorePath(hdStorePath);
		attachment.setSdStorePath(sdStorePath);
		attachment.setCreateTime(new Date());
		attachment.setSuffix(suffix);
		attachment.setSize(size);
		attachment.setLength(length);
		
		if (StringUtils.isBlank(attpackId)) {
			attpackId = this.createNewPacket(crewId, null);
		} else {
			//更新附件包信息
			AttachmentPacketModel attachPacket = this.queryAttpacketById(attpackId);
			attachPacket.setContainAttment(true);
			this.updateAttpacket(attachPacket);
		}
		
		attachment.setAttpackId(attpackId);
		this.addOneAttachment(attachment);
		
		return attachment;
	}
	
	/**
	 * 根据附件包ID查询附件记录
	 * @param attpackId
	 * @return
	 */
	public List<AttachmentModel> queryAttByPackId (String attpackId) {
		return this.attachmentDao.queryByPackId(attpackId);
	}
	
	/**
	 * 更新附件信息
	 * @param attachmentId
	 * @throws Exception 
	 */
	public void updateAttachmentInfo(AttachmentModel model) throws Exception {
		this.attachmentDao.updateWithNull(model, "id");
	}
	
	/**
	 * 批量更新附件信息
	 * @param list
	 * @throws Exception 
	 */
	public void updateAttachmentBatch(List<AttachmentModel> list) throws Exception {
		this.attachmentDao.updateBatch(list, "id", AttachmentModel.class);
	}
	
	/**
	 * 根据附件id查询附件信息
	 * @param attachmentId
	 * @return
	 * @throws Exception 
	 */
	public AttachmentModel queryAttachmentById(String attachmentId) throws Exception {
		return this.attachmentDao.queryById(attachmentId);
	}
}
