package com.xiaotu.makeplays.notice.controller.dto.clip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.xiaotu.makeplays.notice.model.clip.LiveConvertAddModel;
import com.xiaotu.makeplays.notice.model.clip.ShootLiveModel;


/**
 * 场记单拍摄现场信息
 * @author xuchangjian 2015-11-9下午4:20:31
 */
public class ShootLiveDto {

	/**
	 * id
	 */
	private String liveId;
	
	/**
	 * 剧组ID
	 */
	private String crewId;
	
	/**
	 * 通告单ID
	 */
	private String noticeId;
	
	/**
	 * 带号
	 */
	private String tapNo;
	
	/**
	 * 拍摄地点
	 */
	private String shootLocation;
	
	/**
	 * 拍摄场景
	 */
	private String shootScene;
	
	/**
	 * 出发时间
	 */
	private String startTime;
	
	/**
	 * 到场时间
	 */
	private String arriveTime;
	
	/**
	 * 开机时间
	 */
	private String bootTime;
	
	/**
	 * 收工时间
	 */
	private String packupTime;
	
	/**
	 * 转场信息
	 */
	private List<LiveConvertAddDto> convertInfoList;
	
	/**
	 * 创建时间
	 */
	private String createTime;
	
	/**
	 * 移动端最后保存时间
	 */
	private String mobileTime;
	
	/**
	 * 服务端最后保存时间
	 */
	private String serverTime;
	
	public ShootLiveDto() {
		
	}
	
	public ShootLiveDto(ShootLiveModel shootLiveModel, List<LiveConvertAddModel> liveConvertAddList) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		this.liveId = shootLiveModel.getLiveId();
		this.crewId = shootLiveModel.getCrewId();
		this.noticeId = shootLiveModel.getNoticeId();
		this.tapNo = shootLiveModel.getTapNo();
		this.shootLocation = shootLiveModel.getShootLocation();
		this.shootScene = shootLiveModel.getShootScene();
		if (shootLiveModel.getStartTime() != null) {
			this.startTime = sdf.format(shootLiveModel.getStartTime());
		}
		if (shootLiveModel.getArriveTime() != null) {
			this.arriveTime = sdf.format(shootLiveModel.getArriveTime());
		}
		if (shootLiveModel.getBootTime() != null) {
			this.bootTime = sdf.format(shootLiveModel.getBootTime());
		}
		if (shootLiveModel.getPackupTime() != null) {
			this.packupTime = sdf.format(shootLiveModel.getPackupTime());
		}
		if (shootLiveModel.getCreateTime() != null) {
			this.createTime = sdf.format(shootLiveModel.getCreateTime());
		}
		if (shootLiveModel.getMobileTime() != null) {
			this.mobileTime = sdf.format(shootLiveModel.getMobileTime());
		}
		if (shootLiveModel.getServerTime() != null) {
			this.serverTime = sdf.format(shootLiveModel.getServerTime());
		}
		if (liveConvertAddList != null) {
			List<LiveConvertAddDto> liveConvertAddDtoList = new ArrayList<LiveConvertAddDto>();
			for (LiveConvertAddModel liveConvertAdd : liveConvertAddList) {
				LiveConvertAddDto liveConvertAddDto = new LiveConvertAddDto(liveConvertAdd);
				liveConvertAddDtoList.add(liveConvertAddDto);
			}
			this.convertInfoList = liveConvertAddDtoList;
		}
	}

	public String getLiveId() {
		return this.liveId;
	}

	public void setLiveId(String liveId) {
		this.liveId = liveId;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public String getNoticeId() {
		return this.noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}

	public String getTapNo() {
		return this.tapNo;
	}

	public void setTapNo(String tapNo) {
		this.tapNo = tapNo;
	}

	public String getShootLocation() {
		return this.shootLocation;
	}

	public void setShootLocation(String shootLocation) {
		this.shootLocation = shootLocation;
	}

	public String getShootScene() {
		return this.shootScene;
	}

	public void setShootScene(String shootScene) {
		this.shootScene = shootScene;
	}

	public String getStartTime() {
		return this.startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getArriveTime() {
		return this.arriveTime;
	}

	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}

	public String getBootTime() {
		return this.bootTime;
	}

	public void setBootTime(String bootTime) {
		this.bootTime = bootTime;
	}

	public String getPackupTime() {
		return this.packupTime;
	}

	public void setPackupTime(String packupTime) {
		this.packupTime = packupTime;
	}

	public List<LiveConvertAddDto> getConvertInfoList() {
		return this.convertInfoList;
	}

	public void setConvertInfoList(List<LiveConvertAddDto> convertInfoList) {
		this.convertInfoList = convertInfoList;
	}

	public String getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getMobileTime() {
		return this.mobileTime;
	}

	public void setMobileTime(String mobileTime) {
		this.mobileTime = mobileTime;
	}

	public String getServerTime() {
		return this.serverTime;
	}

	public void setServerTime(String serverTime) {
		this.serverTime = serverTime;
	}
}
