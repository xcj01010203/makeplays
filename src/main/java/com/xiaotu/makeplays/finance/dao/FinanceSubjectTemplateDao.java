package com.xiaotu.makeplays.finance.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.finance.model.FinanceSubjectTemplateModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 财务科目模板
 * @author xuchangjian 2016-7-28上午10:18:08
 */
@Repository
public class FinanceSubjectTemplateDao extends BaseDao<FinanceSubjectTemplateModel> {

	/**
	 * 根据类型查询财务模板
	 * @param type
	 * @return
	 */
	public List<FinanceSubjectTemplateModel> queryByType(int type) {
		String sql = "select * from " + FinanceSubjectTemplateModel.TABLE_NAME + " where type = ? order by id + 0";
		return this.query(sql, new Object[] {type}, FinanceSubjectTemplateModel.class, null);
	}
}
