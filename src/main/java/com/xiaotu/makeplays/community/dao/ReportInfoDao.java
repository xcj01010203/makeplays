package com.xiaotu.makeplays.community.dao;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.community.model.ReportInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 举报信息dao
 * @author wanrenyi 2016年9月3日下午4:54:20
 */
@Repository
public class ReportInfoDao extends BaseDao<ReportInfoModel>{

	/**
	 * 添加举报信息
	 * @param model
	 * @throws Exception 
	 */
	public void addReportInfo(ReportInfoModel model) throws Exception {
		this.add(model);
	}
	
	/**
	 * 根据id删除举报信息
	 * @param reportId
	 * @throws Exception 
	 */
	public void deleteReportInfo(String reportId) throws Exception {
		this.deleteOne(reportId, "reportId", ReportInfoModel.TABLE_NAME);
	}
	
}
