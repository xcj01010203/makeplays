package com.xiaotu.makeplays.shoot.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.shoot.model.ShootLogModel;
import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 拍摄日志
 * @author xuchangjian
 */
@Repository
public class ShootLogDao extends BaseDao<ShootLogModel> {
	
	/**
	 * 查询现场日志列表
	 * 该方法针对日志表、分组表、用户表进行连表查询，目的是取到分组名称、用户名称
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryManyByMutiCondition(String crewId, Page page) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select res.noticeId,res.noticeName,res.noticeDate,GROUP_CONCAT(distinct res.shootLocation) shootLocation, ");
		sql.append(" 	res.groupName, ifnull(sum(res.pageCount),0) sumPage,count(res.viewId) viewCount, ");
		sql.append(" 	res.updateTime,res.canceledStatus ,res.shootScene,res.bootTime,res.packupTime ");
		sql.append(" from ( ");
		sql.append(" 	select tni.noticeId,tni.noticeName,tni.noticeDate, ");
		sql.append(" 		tsa.vname shootLocation,tsg.groupName, tsnm.viewId ,tsi.pageCount, ");
		sql.append(" 		tni.updateTime,tni.canceledStatus ,tli.shootScene,tli.bootTime,tli.packupTime ");
		sql.append(" 	from tab_notice_info tni ");
		sql.append(" 	left join tab_view_notice_map tsnm on tsnm.noticeId=tni.noticeId ");
		sql.append(" 	left join tab_shoot_group tsg on tsg.groupId=tni.groupId ");
		sql.append(" 	left join tab_view_info tsi on tsi.viewId=tsnm.viewId ");
		sql.append(" 	left join tab_sceneview_info tsa on tsa.id=tsi.shootLocationId ");
		sql.append(" 	LEFT JOIN tab_shootLive_info tli ON tli.noticeId = tni.noticeId ");
		sql.append(" 	where tni.crewId=? ");
		sql.append(" 	GROUP BY tni.noticeId,tni.noticeName,tni.noticeDate, tsa.vname ,tsg.groupName, ");
		sql.append(" 		tsnm.viewId ,tsi.pageCount,tni.updateTime,tni.canceledStatus ");
		sql.append(" ) res ");
		sql.append(" group by res.noticeId,res.noticeName,res.noticeDate,res.groupName,res.updateTime,res.canceledStatus ");
		sql.append(" order by res.noticeDate desc ");
		
		List<Map<String, Object>> resultList = this.query(sql.toString(), new Object[] {crewId}, page);
		
		return resultList;
	}
}
