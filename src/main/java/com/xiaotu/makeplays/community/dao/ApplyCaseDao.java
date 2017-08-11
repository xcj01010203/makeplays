package com.xiaotu.makeplays.community.dao;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.community.model.ApplyCaseModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 百晓生系统中的应用案例
 * @author xuchangjian 2017-5-3下午3:13:12
 */
@Repository
public class ApplyCaseDao extends BaseDao<ApplyCaseModel> {

	/**
	 * 根据多个条件查询案例信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<ApplyCaseModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + ApplyCaseModel.TABLE_NAME + " where 1 = 1 ");
		
		Set<String> keySet = conditionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		List<Object> conList = new LinkedList<Object>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = conditionMap.get(key);
			
			if (key.equals("title")) {
				sql.append(" and title like ?");
				conList.add("%" + value + "%");
			} else {
				sql.append(" and " + key + " = ?");
				conList.add(value);
			}
		}
		sql.append(" order by createTime desc ");
		Object[] objArr = conList.toArray();
		List<ApplyCaseModel> applyCaseList = this.query(sql.toString(), objArr, ApplyCaseModel.class, page);
		
		return applyCaseList;
	}
}
