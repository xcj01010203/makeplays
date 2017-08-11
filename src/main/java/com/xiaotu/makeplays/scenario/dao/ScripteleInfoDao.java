package com.xiaotu.makeplays.scenario.dao;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.scenario.model.ScripteleInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 剧本分析基本元素信息表
 * @author xuchangjian
 */
@Repository
public class ScripteleInfoDao extends BaseDao<ScripteleInfoModel> {

	/**
	 * 根据多个条件查询元素信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ScripteleInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + ScripteleInfoModel.TABLE_NAME + " where 1 = 1 ");

		List<Object> conList = new LinkedList<Object>();
		
		if (conditionMap != null) {
			Set<String> keySet = conditionMap.keySet();
			Iterator<String> iter = keySet.iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				Object value = conditionMap.get(key);
				sql.append(" and " + key + " = ?");
				conList.add(value);
			}
		}
		
		Object[] objArr = conList.toArray();
		List<ScripteleInfoModel> scripteleInfoList = this.query(sql.toString(), objArr, ScripteleInfoModel.class, page);
		
		return scripteleInfoList;
	}
}
