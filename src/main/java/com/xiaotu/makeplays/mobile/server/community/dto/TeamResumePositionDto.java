package com.xiaotu.makeplays.mobile.server.community.dto;

import com.xiaotu.makeplays.community.model.TeamPositionInfoModel;

/**
 * 当前组训中每个职位投递简历人数的dto
 * @author wanrenyi 2016年10月14日下午4:50:34
 */
public class TeamResumePositionDto extends TeamPositionInfoModel{

	/**
	 * 当前职位的投递简历的人数
	 */
	private long resumeCount;

	public long getResumeCount() {
		return resumeCount;
	}

	public void setResumeCount(long resumeCount) {
		this.resumeCount = resumeCount;
	}
}
