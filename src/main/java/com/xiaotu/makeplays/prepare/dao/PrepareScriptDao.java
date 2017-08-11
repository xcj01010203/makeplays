package com.xiaotu.makeplays.prepare.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.prepare.model.PrepareScriptModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * @ClassName PrepareScriptDao
 * @Description 剧本筹备进度
 * @author Administrator
 * @Date 2017年2月10日 上午10:38:14
 * @version 1.0.0
 */
@Repository
public class PrepareScriptDao extends BaseDao<PrepareScriptModel>{
	
	/**
	 * @Description 查询剧本类型
	 * @return
	 */
	public List<Map<String, Object>> queryScriptType(){
		String sql = "select * from tab_prepare_script_type order by ordernumber";
		return this.getJdbcTemplate().queryForList(sql);
	}
	
	/**
	 * @Description 查询剧本类型
	 * @return
	 */
	public List<Map<String, Object>> queryScriptTypeChecked(String crewId){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("    tpst.id, ");
		sql.append("    tpst. name, ");
		sql.append("    tpstc.scripttypeid ");
		sql.append(" FROM ");
		sql.append("    tab_prepare_script_type tpst ");
		sql.append(" LEFT JOIN tab_prepare_script_type_checked tpstc ON tpst.id = tpstc.scriptTypeId and crewid = ?");
		sql.append(" ORDER BY ");
		sql.append("    ordernumber ");
		return this.getJdbcTemplate().queryForList(sql.toString(),new Object[]{crewId});
	}
	/**
	 * @Description 权重信息
	 * @return
	 */
	public List<Map<String, Object>> queryWeightInfo(String crewId){
		String sql = "select * from tab_prepare_script_reviewweight where crewId = ? order by ordernumber";
		return this.getJdbcTemplate().queryForList(sql,new Object[]{crewId});
	}
	/**
	 * @Description 批量保存或修改权重信息
	 */
	public void saveOrUpdateWeightInfo(List<Object[]>argsWeightListAdd,List<Object[]> argsWeightListUpdate){
		//保存数据库中不存在的数据
		String addSql = "insert into tab_prepare_script_reviewweight (id,name,weight,crewid,ordernumber) values (?,?,?,?,?)";
		this.getJdbcTemplate().batchUpdate(addSql,argsWeightListAdd);
		
		//修改数据库中已经存在的
		String updateSql = "update tab_prepare_script_reviewweight set name = ? ,weight = ? where id = ? ";
		this.getJdbcTemplate().batchUpdate(updateSql,argsWeightListUpdate);
		
	}
	
	public void delWeightInfo(String id){
		String delSql = "delete from tab_prepare_script_reviewweight where id = ?";
		this.getJdbcTemplate().update(delSql,new Object[]{id});
		
	}
	public void delReviewInfo(String id){
		String delSql = "delete from tab_prepare_script_score where reviewweightId = ?";
		this.getJdbcTemplate().update(delSql,new Object[]{id});
	}
	
	public int queryMaxOrderNumber(String crewId){
		String querySql = "select max(orderNumber) orderNumber from tab_prepare_script_reviewweight where crewid = ?";
		Map<String, Object> result = this.getJdbcTemplate().queryForMap(querySql,new Object[]{crewId});
		int orderNumber = 1;
		if(result != null){
			orderNumber = result.get("orderNumber")!=null?Integer.valueOf(result.get("orderNumber").toString()):1;
		}
		
		return orderNumber;
		
	}
	
	
	public void saveScriptTypeInfo(List<Object[]> ids){
		String saveSql = "insert into tab_prepare_script_type_checked (id,scriptTypeId,crewid) values(?,?,?)";
		
		this.getJdbcTemplate().batchUpdate(saveSql, ids);
		
	}
	
	public List<Map<String, Object>> queryHasScritpTyps(String crewId){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT DISTINCT ");
		sql.append("    tpstc.scripttypeid, ");
		sql.append("    tps.id ");
		sql.append(" FROM ");
		sql.append("    tab_prepare_script_type_checked tpstc ");
		sql.append(" LEFT JOIN tab_prepare_script tps ON tpstc.scriptTypeId = tps.scriptTypeId  ");
		sql.append(" where tpstc.crewid = ?");
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql.toString(), new Object[]{crewId});
		return list;
		
	}
	
	
	
	public void delScriptTypeInfoByCrewId(String crewId){
		String delSql = "delete from tab_prepare_script_type_checked where crewid = ?";
		this.getJdbcTemplate().update(delSql,new Object[]{crewId});
	}
	
	/**
	 * @Description  修改分数
	 * @param score  分数
	 * @param scriptid   剧本评审信息id
	 * @param reviewweightId  权重信息id
	 */
	public void delScore(List<Object[]> args){
		String updateSql = "delete from tab_prepare_script_score  where scriptid = ? and reviewweightId = ?";
		this.getJdbcTemplate().batchUpdate(updateSql,args);
	}
	
	public void saveScore(List<Object[]> args){
		String updateSql = "insert into tab_prepare_script_score (id,scriptid,reviewweightId,score,crewId) values (?,?,?,?,?)";
		this.getJdbcTemplate().batchUpdate(updateSql, args);
	}
	
	public void delScriptById(List<Object[]> scriptId){
		String sql = "delete from tab_prepare_script where id = ?";
		this.getJdbcTemplate().batchUpdate(sql, scriptId);
	}
	
	public void delScorByScriptId(List<Object[]> scriptId){
		String sql = "delete from tab_prepare_script_score where scriptid = ?";
		this.getJdbcTemplate().batchUpdate(sql, scriptId);
	}
	
	
	public void saveScriptInfo(List<Object[]> addScript){
		String sql = "insert into tab_prepare_script (id,scripttypeid,crewid,parentid,createTime) values (?,?,?,?,now())";
		this.getJdbcTemplate().batchUpdate(sql, addScript);
	}
	
	public List<Map<String, Object>> queryScriptScheduleInfo(String crewId){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append("    tps.id, ");
		sql.append("    tps.parentId, ");
		sql.append("    tps.scriptTypeId, ");
		sql.append("    tps.edition, ");
		sql.append("    tps.finishDate, ");
		sql.append("    tps.personLiable, ");
		sql.append("    tps.content, ");
		sql.append("    tps. status, ");
		sql.append("    tps.mark, ");
		sql.append("    tpst.orderNumber, ");
		sql.append("    tpst. name, ");
		sql.append("    GROUP_CONCAT( ");
		sql.append("       case when tpss.score is null then 'blank' else tpss.score end  ");
		sql.append("       ORDER BY ");
		sql.append("          tpss.reviewweightId ");
		sql.append("    ) score, ");
		sql.append("    GROUP_CONCAT( ");
		sql.append("       tpss.reviewweightId ");
		sql.append("       ORDER BY ");
		sql.append("          tpss.reviewweightId ");
		sql.append("    ) reviewweightId ,GROUP_CONCAT(tpsr.weight order by tpsr.id) weight ");
		sql.append(" FROM ");
		sql.append("    tab_prepare_script tps ");
		sql.append(" LEFT JOIN tab_prepare_script_type tpst ON tps.scriptTypeId = tpst.id ");
		sql.append(" LEFT JOIN tab_prepare_script_score tpss ON tps.id = tpss.scriptid ");
		sql.append(" LEFT JOIN tab_prepare_script_reviewweight tpsr on tpss.reviewweightId = tpsr.id ");
		sql.append(" where tps.crewid = ? ");
		sql.append(" GROUP BY ");
		sql.append("    tps.id, ");
		sql.append("    tps.scriptTypeId, ");
		sql.append("    tpst. name, ");
		sql.append("    tps.parentId, ");
		sql.append("    tpst.orderNumber ");
		sql.append(" ORDER BY ");
		sql.append("    tps.parentid,tpst.orderNumber,tps.createTime ");
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql.toString(), new Object[]{crewId});
		return list;
	}	
	
	
	/**
	 * @Description 保存一条剧本进度信息
	 * @param parentId  父id
	 * @param id  id
	 * @param scriptTypeId   剧本类型id
	 * @param edition  版本
	 * @param finishDate  交稿日期
	 * @param personLiable 负责人
	 * @param content  内容
	 * @param status 状态
	 * @param mark  备注
	 * @param crewId  剧组id
	 */
	public void saveScriptInfo(String id,String parentId,String scriptTypeId,String edition,String finishDate,
			String personLiable,String content,String status,String mark,String crewId){
		
		String sql = "insert into tab_prepare_script (id,scriptTypeId,edition,finishDate,personLiable,content,status,mark,crewid,parentId) values(?,?,?,?,?,?,?,?,?,?)";
		if(StringUtils.isBlank(finishDate)){
			finishDate = null;
		}
		this.getJdbcTemplate().update(sql,new Object[]{id,scriptTypeId,edition,finishDate,personLiable,content,status,mark,crewId,parentId});
	}
	public void updateScriptInfo(String id,String parentId,String scriptTypeId,String edition,String finishDate,String personLiable,String content,String status,String mark){
		if(StringUtils.isBlank(finishDate)){
			finishDate = null;
		}
		String sql = "update tab_prepare_script set parentId =?,scriptTypeId = ?,edition =? ,finishDate = ?,personLiable=?,content=?,status=?,mark=? where id = ?";
		this.getJdbcTemplate().update(sql,new Object[]{parentId,scriptTypeId,edition,finishDate,personLiable,content,status,mark,id});
	}
	
	/**
	 * @Description 根据id删除一条剧本进度信息
	 * @param crewId
	 */
	public void delScriptInfo(String[] id){
		String sql = "delete from tab_prepare_script where id = ?";
		List<Object[]> args = new ArrayList<Object[]>();
		for(String str:id){
			args.add(new Object[]{str});
		}
		this.getJdbcTemplate().batchUpdate(sql, args);
	}
	
}
