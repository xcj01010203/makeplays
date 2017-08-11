package com.xiaotu.makeplays.finance.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.finance.dao.FinanceSettingDao;
import com.xiaotu.makeplays.finance.model.FinanceSettingModel;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 财务设置
 * @author xuchangjian 2016-8-11下午6:31:29
 */
@Service
public class FinanceSettingService {

	@Autowired
	private FinanceSettingDao financeSettingDao;
	
	/**
	 * 获取剧组下的财务设置信息
	 * @param crewId
	 * @return
	 * @throws Exception 
	 */
	public FinanceSettingModel queryByCrewId(String crewId) throws Exception {
		FinanceSettingModel financeSetting = this.financeSettingDao.queryByCrewId(crewId);
		return financeSetting;
	}
	
	/**
	 * 初始化剧组的财务设置
	 * @param crewId
	 * @return
	 * @throws Exception
	 */
	public FinanceSettingModel initFinanceSetting (String crewId) throws Exception {
		FinanceSettingModel financeSetting = new FinanceSettingModel();
		financeSetting.setSetId(UUIDUtils.getId());
		financeSetting.setCrewId(crewId);
		financeSetting.setCreateTime(new Date());
		
		this.addOne(financeSetting);
		return financeSetting;
	}
	
	/**
	 * 添加一条记录
	 * @param financeSetting
	 * @throws Exception 
	 */
	public void addOne(FinanceSettingModel financeSetting) throws Exception {
		this.financeSettingDao.add(financeSetting);
	}
	
	/**
	 * 更新一条记录
	 * @param financeSetting
	 * @throws Exception
	 */
	public void updateOne(FinanceSettingModel financeSetting) throws Exception {
		this.financeSettingDao.updateWithNull(financeSetting, "setId");
	}
}
