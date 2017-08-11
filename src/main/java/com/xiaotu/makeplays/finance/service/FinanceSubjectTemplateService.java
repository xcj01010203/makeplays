package com.xiaotu.makeplays.finance.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.finance.dao.FinanceSubjectTemplateDao;
import com.xiaotu.makeplays.finance.model.FinanceSubjectTemplateModel;

/**
 * 财务科目模板
 * @author xuchangjian 2016-7-28上午10:18:49
 */
@Service
public class FinanceSubjectTemplateService {
	
	@Autowired
	private FinanceSubjectTemplateDao financeSubjectTemplateDao;

	/**
	 * 根据类型查询财务模板
	 * @param type
	 * @return
	 */
	public List<FinanceSubjectTemplateModel> queryByType(int type) {
		return this.financeSubjectTemplateDao.queryByType(type);
	}
}
