package com.xiaotu.makeplays.crew.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.crew.model.CrewSubjectModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 剧组题材
 * @author xuchangjian 2016-12-15上午10:18:39
 */
@Repository
public class CrewSubjectDao extends BaseDao<CrewSubjectModel> {

	/**
	 * 查询题材列表
	 * @return
	 */
	public List<CrewSubjectModel> querySubjectList () {
		String sql = "select * from tab_subject_info";
		return this.query(sql, null, CrewSubjectModel.class, null);
	}
}
