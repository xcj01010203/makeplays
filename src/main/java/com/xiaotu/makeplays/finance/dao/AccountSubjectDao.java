package com.xiaotu.makeplays.finance.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.finance.model.AccountSubjectModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 会计科目
 * @author xuchangjian 2016-6-22上午10:59:23
 */
@Repository
public class AccountSubjectDao extends BaseDao<AccountSubjectModel> {

	/**
	 * 根据ID查询信息
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public AccountSubjectModel queryById(String id) throws Exception {
		String sql = "select * from tab_account_subject where id = ?";
		return this.queryForObject(sql, new Object[] {id}, AccountSubjectModel.class);
	}
	
	/**
	 * 查询最大序列号
	 * @param crewId
	 * @return
	 */
	public int queryMaxSequence(String crewId) {
		String sql = "select max(sequence) from tab_account_subject where crewId = ?";
		Number number = this.getJdbcTemplate().queryForObject(sql, new Object[] {crewId}, Integer.class);
		return (number != null ? number.intValue() : 0);
	}
	
	/**
	 * 查询剧组下的会计科目列表
	 * @param crewId
	 * @return 会计科目id， 名称，代码，其下的所有预算科目名称（用逗号隔开）
	 */
	public List<Map<String, Object>> queryByCrewId(String crewId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tas.id, ");
		sql.append(" 	tas.name, ");
		sql.append(" 	tas.code, ");
		sql.append(" 	GROUP_CONCAT(tfs.name order by tfs.sequence) financeSubjNames ");
		sql.append(" FROM ");
		sql.append(" 	tab_account_subject tas ");
		sql.append(" left join tab_account_finance_subject_map tafs on tafs.accountSubjId = tas.id and tafs.crewId = ? ");
		sql.append(" left join tab_finance_subject tfs on tafs.financeSubjId = tfs.id and tfs.crewId = ? ");
		sql.append(" WHERE tas.crewId = ? ");
		sql.append(" GROUP BY ");
		sql.append(" 	tas.id, ");
		sql.append(" 	tas. NAME, ");
		sql.append(" 	tas. CODE ");
		sql.append(" ORDER BY ");
		sql.append(" 	tas.sequence ");
		
		return this.query(sql.toString(), new Object[] {crewId, crewId, crewId}, null);
	}
	
	/**
	 * 设置会计科目排列序号
	 * @param id
	 * @param sequence
	 */
	public void modifyAccountSubjSequence(String id, int sequence) {
		String sql = "update tab_account_subject set sequence = ? where id = ?";
		this.getJdbcTemplate().update(sql, new Object[] {sequence, id});
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
		List<Object> params = new ArrayList<Object>();
		params.add(code);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from tab_account_subject where code = ? ");
		if (!StringUtils.isBlank(id)) {
			sql.append(" and id != ? ");
			params.add(id);
		}
		sql.append(" and crewId = ? ");
		params.add(crewId);
		
		return this.query(sql.toString(), params.toArray(), AccountSubjectModel.class, null);
	}
}
