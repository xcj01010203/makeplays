package com.xiaotu.makeplays.finance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.finance.dao.AccoFinacSubjMapDao;
import com.xiaotu.makeplays.finance.model.AccoFinacSubjMapModel;

/**
 * 会计科目和财务科目的关联关系
 * @author xuchangjian 2016-6-22下午4:13:13
 */
@Service
public class AccoFinacSubjMapService {

	@Autowired
	private AccoFinacSubjMapDao accoFinacSubjMapDao;
	
	/**
	 * 根据会计科目ID和财务科目ID查询对应的关联关系
	 * @param crewId
	 * @param accountSubjId
	 * @param financeSubjId
	 * @return
	 * @throws Exception
	 */
	public AccoFinacSubjMapModel queryByAccAndFinaSubId(String crewId, String accountSubjId, String financeSubjId) throws Exception {
		return this.accoFinacSubjMapDao.queryByAccAndFinaSubId(crewId, accountSubjId, financeSubjId);
	}
	
	/**
	 * 根据会计科目ID和财务科目ID删除对应的关联关系
	 * @param crewId
	 * @param accountSubjId
	 * @param financeSubjId
	 */
	public void deleteByAccAndFinaSubId(String crewId, String accountSubjId, String financeSubjId) {
		this.accoFinacSubjMapDao.deleteByAccAndFinaSubId(crewId, accountSubjId, financeSubjId);
	}
	
	/**
	 * 新增一条记录
	 * @param map
	 * @throws Exception 
	 */
	public void addOne(AccoFinacSubjMapModel map) throws Exception {
		this.accoFinacSubjMapDao.add(map);
	}
	
	/**
	 * 根据会计科目Id删除和财务科目的关联关系
	 * @param accountSubjId
	 */
	public void deleteByAccSubjId(String accountSubjId) {
		this.accoFinacSubjMapDao.deleteByAccSubjId(accountSubjId);
	}
	
	/**
	 * 根据财务科目Id删除和财务科目的关联关系
	 * @param accountSubjId
	 */
	public void deleteByFinaSubId(String financeSubjId) {
		this.accoFinacSubjMapDao.deleteByFinaSubId(financeSubjId);
	}
}
