package com.xiaotu.makeplays.index.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.authority.dao.AuthorityDao;
import com.xiaotu.makeplays.finance.model.CurrencyInfoModel;
import com.xiaotu.makeplays.index.dao.IndexDao;
import com.xiaotu.makeplays.view.controller.filter.ViewFilter;
import com.xiaotu.makeplays.view.dao.ViewInfoDao;

@Service
public class IndexService {
	
	@Autowired
	private ViewInfoDao viewInfoDao;
	
	@Autowired
	private IndexDao indexDao;
	
	@Autowired
	private AuthorityDao authorityDao;
	
	//获取文武戏统计
	public Map<String,Object> getViewTypeList(String crewId){
		List<Map<String,Object>>  totalList = this.viewInfoDao.queryViewListStatistics(crewId, null, "viewType", "viewId", "count");
		ViewFilter filter = new ViewFilter();
		filter.setShootStatus("2");
		List<Map<String,Object>>  endList = this.viewInfoDao.queryViewListStatistics(crewId, filter, "viewType", "viewId", "count");
		Map map = new HashMap();
		for(int i = 0;i<totalList.size();i++){
			Map<String,Object> m = totalList.get(i);
			Integer type = (Integer)m.get("viewType");
			if(type == null)
				continue;
			if(type == 1){
				map.put("singingPlay", "0/"+m.get("funResult")); //文戏
			}else if(type == 2){
				map.put("actionPlay", "0/"+m.get("funResult"));  //武戏
			}else if(type == 3){
				map.put("militaryPlay", "0/"+m.get("funResult"));  //文武戏
			}
		}
		for(int i = 0;i<endList.size();i++){
			Map<String,Object> m = endList.get(i);
			Integer type = (Integer)m.get("viewType");
			if(type == null)
				continue;
			if(type == 1){
				map.put("singingPlay", m.get("funResult")+""+map.get("singingPlay").toString().substring(1)); //文戏
			}else if(type == 2){
				map.put("actionPlay", m.get("funResult")+""+map.get("actionPlay").toString().substring(1));  //武戏
			}else if(type == 3){
				map.put("militaryPlay", m.get("funResult")+""+map.get("militaryPlay").toString().substring(1));  //文武戏
			}
		}
		return map;
	}
	
	//获取地点统计
	public Map getAddressData(String crewId){
		return this.indexDao.getAddressData(crewId);
	}
	
	//获取演员统计
	public Map getActorData(String crewId){
		return this.indexDao.getActorData(crewId);
	}
	
	//获取集数
	public Long getCrewEpisode(String crewId){
		return this.indexDao.getCrewEpisode(crewId);
	}
	
	//获取本位币
	public CurrencyInfoModel getStardard(String crewId){
		return null;
	}
	
	//获取收款金额
	public double getCollectionMoney(String crewId){
		return this.indexDao.getCollectionMoney(crewId);
	}
	
	//获取已拍摄天数
	public long getAlreadyShoot(String crewId){
		return this.indexDao.getAlreadyShoot(crewId);
	}
	
	//获取前一天结算的付款数据
	public List<Map<String,Object>> getYesterdayPay(String crewId){
		return this.indexDao.getYesterdayPay(crewId);
	}
	
	//获取上一天所有通告单统计信息
	public List<Map<String,Object>> getPreNoticeTotal(String crewId){
		return this.indexDao.getPreNoticeTotal(crewId);
	}
	
	//获取当天的通告单数据
	public List<Map<String,Object>> getTodayNotice(String crewId){
		return this.indexDao.getTodayNotice(crewId);
	}
	
	//获取场次统计
	public Map getViewListStatistics(String crewId){
		ViewFilter filter = new ViewFilter();
		Map map = new HashMap();
		long total = 0l;
		long finish = 0l;
		List<Map<String, Object>> viewList = this.viewInfoDao.queryViewListStatistics(crewId, filter, "shootStatus", "viewId", "count");
		if(viewList!=null && viewList.size()>0){
			for (Map<String, Object> map2 : viewList) {
				total += (Long)map2.get("funResult");
				if(map2.get("shootStatus") != null)
					if(Integer.valueOf(map2.get("shootStatus").toString()) == 2 || Integer.valueOf(map2.get("shootStatus").toString()) == 3) {
						finish += (Long)map2.get("funResult");
					}
			}
		}
		map.put("totalCrewAmount", total);
		map.put("finishedCrewAmount", finish);
		return map;
	}
	
	//获取通联表(首页数据)
	public List<Map<String,Object>> getContactList(String crewId){
		return this.indexDao.getContactList(crewId);
	}
}
