package com.xiaotu.makeplays.scenario.dao;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.scenario.model.SeparatorInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 剧本分析分隔符信息表
 * @author xuchangjian
 */
@Repository
public class SeparatorInfoDao extends BaseDao<SeparatorInfoModel> {

	/**
	 * 根据多个条件查询符号信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<SeparatorInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + SeparatorInfoModel.TABLE_NAME + " where 1 = 1 ");

		List<Object> conList = new LinkedList<Object>();
		
		if (conditionMap != null) {
			Set<String> keySet = conditionMap.keySet();
			Iterator<String> iter = keySet.iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				Object value = conditionMap.get(key);
				if (key.equals("crewId")) {
					sql.append(" and (crewId = '0' or crewId = ?) ");
					conList.add(value);
					continue;
				}
				
				sql.append(" and " + key + " = ?");
				conList.add(value);
			}
		}
		
		Object[] objArr = conList.toArray();
		List<SeparatorInfoModel> separatorInfoList = this.query(sql.toString(), objArr, SeparatorInfoModel.class, page);
		
		return separatorInfoList;
	}
	
	/**
	 * 根据ID查找符号信息
	 * @param sepaId
	 * @return
	 * @throws Exception
	 */
	public SeparatorInfoModel queryOneById(String sepaId) throws Exception {
		String sql = "select * from " + SeparatorInfoModel.TABLE_NAME + " where sepaId = ?";
		
		return this.queryForObject(sql, new Object[] {sepaId}, SeparatorInfoModel.class);
	}
	
	/**
	 * 查询除了自己之外其他有相同名称的操作符
	 * @param sepaId
	 * @param sepaName
	 * @return
	 */
	public List<SeparatorInfoModel> querySameNameSepaExceptOwn(String sepaId, String sepaName) {
		String sql = "select * from " + SeparatorInfoModel.TABLE_NAME + " where sepaId != ? and sepaName = ?";
		return this.query(sql, new Object[] {sepaId, sepaName}, null);
	}
}
