package com.xiaotu.makeplays.finance.dao;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.finance.model.FinanceSettingModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 财务设置
 * @author xuchangjian 2016-5-24下午6:56:00
 */
@Repository
public class FinanceSettingDao extends BaseDao<FinanceSettingModel> {

	/**
	 * 获取剧组下的财务设置信息
	 * @param crewId
	 * @return
	 * @throws Exception 
	 */
	public FinanceSettingModel queryByCrewId(String crewId) throws Exception {
		String sql = "select * from tab_finance_setting_info where crewId = ?";
		return this.queryForObject(sql, new Object[] {crewId}, FinanceSettingModel.class);
	}
}
