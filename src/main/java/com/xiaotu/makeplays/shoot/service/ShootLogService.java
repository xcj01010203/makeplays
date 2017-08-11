package com.xiaotu.makeplays.shoot.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.shoot.dao.ShootLogDao;
import com.xiaotu.makeplays.utils.Page;

/**
 * 拍摄日志信息
 * @author xuchangjian
 */
@Service
public class ShootLogService {

	@Autowired
	private ShootLogDao shootLogDao;
	
	/**
	 * 根据条件查询拍摄日志详细信息，
	 * 该方法针对日志表、分组表、用户表进行连表查询，目的是取到分组名称、用户名称
	 * @param crewId
	 * @return
	 */
	public Map<String, Object> queryManyByMutiCondition(String crewId, Page page) {
		List<Map<String, Object>> resuList = null;
		List<Map<String, Object>> shootLogList = this.shootLogDao.queryManyByMutiCondition(crewId, page);
		Map<String, Object> resuMap = new LinkedHashMap<String, Object>();
		for (Map<String, Object> map : shootLogList) {
			resuList = new ArrayList<Map<String,Object>>();
			if(resuMap.containsKey(map.get("noticeDate")+"")){
				resuList = (List<Map<String, Object>>) resuMap.get(map.get("noticeDate")+"");
				resuList.add(map);
				resuMap.put(map.get("noticeDate")+"", resuList);
			}else{
				resuList.add(map);
				resuMap.put(map.get("noticeDate")+"", resuList);
			}
		}
		return resuMap;
	}
}
