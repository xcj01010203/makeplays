package com.xiaotu.makeplays.mobile.server.notice.dto;

import java.util.List;

import com.xiaotu.makeplays.notice.controller.dto.clip.AttendanceDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.ClipCommentDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.ClipInfoDto;
import com.xiaotu.makeplays.notice.controller.dto.clip.ShootLiveDto;
import com.xiaotu.makeplays.notice.model.clip.ClipPropModel;

/**
 * 场记单请求信息Dto
 * @author xuchangjian 2015-11-11上午11:17:50
 */
public class ClipRequestDto {

	/**
	 * 通告单组别
	 */
	private String groupName;
	
	/**
	 * 通告单日期
	 */
	private String noticeDate;
	
	/**
	 * 设备UID
	 * 20160104 由于需求变更，该字段不再使用，目前作为保留字段
	 */
	private String deviceUID;
	
	/**
	 * 场记单信息
	 */
	private List<ClipInfoDto> clipInfo;
	
	/**
	 * 现场信息
	 */
	private ShootLiveDto liveInfo;
	
	/**
	 * 演员出勤信息
	 */
	private AttendanceDto attendanceInfo;
	
	/**
	 * 特殊道具信息
	 */
	private List<ClipPropModel> specialPropList;
	
	/**
	 * 重要备注信息
	 */
	private List<ClipCommentDto> commentInfo;
	
	/**
	 * 天气信息
	 */
	private String weatherInfo;

	public String getDeviceUID() {
		return this.deviceUID;
	}

	public void setDeviceUID(String deviceUID) {
		this.deviceUID = deviceUID;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getNoticeDate() {
		return this.noticeDate;
	}

	public void setNoticeDate(String noticeDate) {
		this.noticeDate = noticeDate;
	}

	public List<ClipInfoDto> getClipInfo() {
		return this.clipInfo;
	}

	public void setClipInfo(List<ClipInfoDto> clipInfo) {
		this.clipInfo = clipInfo;
	}

	public ShootLiveDto getLiveInfo() {
		return this.liveInfo;
	}

	public void setLiveInfo(ShootLiveDto liveInfo) {
		this.liveInfo = liveInfo;
	}

	public AttendanceDto getAttendanceInfo() {
		return this.attendanceInfo;
	}

	public void setAttendanceInfo(AttendanceDto attendanceInfo) {
		this.attendanceInfo = attendanceInfo;
	}

	public List<ClipPropModel> getSpecialPropList() {
		return this.specialPropList;
	}

	public void setSpecialPropList(List<ClipPropModel> specialPropList) {
		this.specialPropList = specialPropList;
	}

	public List<ClipCommentDto> getCommentInfo() {
		return this.commentInfo;
	}

	public void setCommentInfo(List<ClipCommentDto> commentInfo) {
		this.commentInfo = commentInfo;
	}

	public String getWeatherInfo() {
		return this.weatherInfo;
	}

	public void setWeatherInfo(String weatherInfo) {
		this.weatherInfo = weatherInfo;
	}
}
