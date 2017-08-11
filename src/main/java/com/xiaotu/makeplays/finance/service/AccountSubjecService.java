package com.xiaotu.makeplays.finance.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.finance.dao.AccoFinacSubjMapDao;
import com.xiaotu.makeplays.finance.dao.AccountSubjectDao;
import com.xiaotu.makeplays.finance.model.AccountSubjectModel;

/**
 * 会计科目 
 * @author xuchangjian 2016-6-22上午11:07:54
 */
@Service
public class AccountSubjecService {

	@Autowired
	private AccountSubjectDao accountSubjectDao;
	
	@Autowired
	private AccoFinacSubjMapDao accoFinacSubjMapDao;
	
	/**
	 * 根据ID查询会计科目信息
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public AccountSubjectModel queryById(String id) throws Exception {
		return this.accountSubjectDao.queryById(id);
	}
	
	/**
	 * 更新
	 * @param accountSubject
	 * @throws Exception
	 */
	public void updateOne(AccountSubjectModel accountSubject) throws Exception {
		this.accountSubjectDao.update(accountSubject, "id");
	}
	
	/**
	 * 新增
	 * @param accountSubject
	 * @throws Exception
	 */
	public void addOne(AccountSubjectModel accountSubject) throws Exception {
		this.accountSubjectDao.add(accountSubject);
	}
	
	/**
	 * 删除
	 * @param id
	 * @throws Exception
	 */
	public void deleteOne(String id) throws Exception {
		this.accountSubjectDao.deleteOne(id, "id", AccountSubjectModel.TABLE_NAME);
		this.accoFinacSubjMapDao.deleteByAccSubjId(id);
	}
	
	/**
	 * 查询最大序列号
	 * @param crewId
	 * @return
	 */
	public int queryMaxSequence(String crewId) {
		return this.accountSubjectDao.queryMaxSequence(crewId);
	}
	
	/**
	 * 查询剧组下的会计科目列表
	 * @param crewId
	 * @return 会计科目id， 名称，代码，其下的所有预算科目名称（用逗号隔开）
	 */
	public List<Map<String, Object>> queryByCrewId(String crewId) {
		return this.accountSubjectDao.queryByCrewId(crewId);
	}
	
	/**
	 * 设置会计科目排列序号
	 * @param id
	 * @param sequence
	 */
	public void modifyAccountSubjSequence(String ids) {
		String[] idArray = ids.split(",");
		for (int i = 0; i < idArray.length; i++) {
			this.accountSubjectDao.modifyAccountSubjSequence(idArray[i], i);
		}
	}
	
	/**
	 * 根据会计科目代码查询会计科目
	 * 该方法接受会计科目ID参数，查询的结果排除该ID的记录
	 * 如果ID为空，则只按照code查询
	 * @param id
	 * @param code
	 * @return
	 */
	public List<AccountSubjectModel> queryByCodeExpOwn(String crewId, String id, String code) {
		return this.accountSubjectDao.queryByCodeExpOwn(crewId, id, code);
	}
}
