package com.xiaotu.makeplays.bulletin.dao;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.bulletin.model.BulletinInfoModel;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 剧组公告信息
 * @author xuchangjian
 */
@Repository
public class BulletinInfoDao extends BaseDao<BulletinInfoModel> {

	/**
	 * 根据多个条件查询剧组公告信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<BulletinInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + BulletinInfoModel.TABLE_NAME + " where 1 = 1 ");
		
		Set<String> keySet = conditionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		List<Object> conList = new LinkedList<Object>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = conditionMap.get(key);
			sql.append(" and " + key + " = ?");
			conList.add(value);
		}
		sql.append(" order by createTime desc");
		Object[] objArr = conList.toArray();
		List<BulletinInfoModel> bulletinInfoList = this.query(sql.toString(), objArr, BulletinInfoModel.class, page);
		
		return bulletinInfoList;
	}
	
	/**
	 * 查询所有公告信息
	 * @param page 分页信息
	 * @return
	 */
	public List<Map<String, Object>> queryAll(Page page) {
		String sql = "select c.crewName, b.* from " + BulletinInfoModel.TABLE_NAME + " b, " + CrewInfoModel.TABLE_NAME + " c where b.crewId = c.crewId order by b.createTime";
		return this.query(sql, null, page);
	}
	
	/**
	 * 通过ID查找剧组公告信息
	 * @param shootLogId
	 */
	public BulletinInfoModel queryOneByBulletinId(String bulletinId) {
		String sql = "select * from " + BulletinInfoModel.TABLE_NAME +" where bulletinId = ?";
		
		BulletinInfoModel bulletinInfo = null;
		Object[] args = new Object[] {bulletinId};
		if (getResultCount(sql, args) == 1) {
			bulletinInfo = this.getJdbcTemplate().queryForObject(sql, args, ParameterizedBeanPropertyRowMapper
					.newInstance(BulletinInfoModel.class));
		}
		
		return bulletinInfo;
	}
}
