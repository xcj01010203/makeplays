package com.xiaotu.makeplays.roleactor.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.roleactor.model.EvtagInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 演员评价标签信息
 * @author xuchangjian 2016-7-19上午11:33:20
 */
@Repository
public class EvtagInfoDao extends BaseDao<EvtagInfoModel> {
	
	/**
	 * 演职员评价标签信息
	 * 
	 * @param crewId
	 * @return
	 */
	public List<EvtagInfoModel> queryEvtagList(String crewId) {
		String sql = "SELECT * FROM tab_evtag_info WHERE crewId='0' or crewId=?";
		return this.query(sql, new Object[] {crewId}, EvtagInfoModel.class, null);
	}
}
