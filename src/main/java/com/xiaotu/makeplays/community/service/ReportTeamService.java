package com.xiaotu.makeplays.community.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.community.dao.ReportInfoDao;
import com.xiaotu.makeplays.community.model.ReportInfoModel;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 举报组训service
 * @author wanrenyi 2016年9月3日下午5:04:00
 */
@Service
public class ReportTeamService {

	@Autowired
	private ReportInfoDao reportInfoDao;
	
	/**
	 * 添加举报信息
	 * @param model
	 * @return
	 * @throws Exception 
	 */
	public String addReportTeam(ReportInfoModel model) throws Exception {
		String reportId = UUIDUtils.getId();
		model.setReportId(reportId);
		reportInfoDao.addReportInfo(model);
		
		return reportId;
	}
	
	/**
	 * 根据id删除举报信息
	 * @param reportId
	 * @throws Exception
	 */
	public void deleteReportTeam(String reportId) throws Exception {
		if (StringUtils.isBlank(reportId)) {
			throw new IllegalArgumentException("请选择要删除的举报信息!");
		}
		reportInfoDao.deleteReportInfo(reportId);
	}
}
