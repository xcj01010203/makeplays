package com.xiaotu.makeplays.attachment.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.attachment.model.AttachmentModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

@Repository
public class AttachmentDao extends BaseDao<AttachmentModel> {
	
	/**
	 * 根据多个条件查询附件信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<AttachmentModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from " + AttachmentModel.TABLE_NAME + " where 1 = 1 ");
		
		Set<String> keySet = conditionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		List<Object> conList = new LinkedList<Object>();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = conditionMap.get(key);
			sql.append(" and " + key + " = ?");
			conList.add(value);
		}
		sql.append(" order by createTime desc ");
		Object[] objArr = conList.toArray();
		List<AttachmentModel> attachmentList = this.query(sql.toString(), objArr, AttachmentModel.class, page);
		
		return attachmentList;
	}

	/**
	 * 根据附件包ID删除附件记录
	 * @param attpackId
	 */
	public void deleteByPackId(String attpackId) {
		String sql = "delete from " + AttachmentModel.TABLE_NAME + " where attpackId=?";
		this.getJdbcTemplate().update(sql, new Object[] {attpackId});
	}
	
	/**
	 * 根据附件包ID查询附件记录
	 * @param attpackId
	 * @return
	 */
	public List<AttachmentModel> queryByPackId(String attpackId) {
		String sql = "select * from " + AttachmentModel.TABLE_NAME + " where attpackId=? order by createTime desc,name";
		return this.query(sql, new Object[] {attpackId}, AttachmentModel.class, null);
	}
	
	/**
	 * 根据ID查询附件信息
	 * @param attachmentId
	 * @return
	 * @throws Exception 
	 */
	public AttachmentModel queryById(String attachmentId) throws Exception {
		String sql = "select * from " + AttachmentModel.TABLE_NAME + " where id=?";
		return this.queryForObject(sql, new Object[] {attachmentId}, AttachmentModel.class);
	}
	
	
	/**
	 * 批量查询附件信息
	 * @param attachmentIdList 附件id集合
	 * @return
	 * @throws Exception
	 */
	public List<AttachmentModel> queryByPackIdList(List<String> attpackIdList) {
		List<AttachmentModel> result = new ArrayList<AttachmentModel>();
		StringBuffer sql = new StringBuffer(" select * from tab_attachment_info where attpackid in (");
		if(attpackIdList!=null&&attpackIdList.size()>0){
			for(String attpackId:attpackIdList){
				sql.append("'");
				sql.append(attpackId);
				sql.append("',");
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(") order by createTime");
			result = this.query(sql.toString(), null, AttachmentModel.class, null);
		}
		return result;
	}
}
