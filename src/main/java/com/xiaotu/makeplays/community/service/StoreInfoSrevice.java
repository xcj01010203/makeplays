package com.xiaotu.makeplays.community.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.community.dao.StoreInfoDao;
import com.xiaotu.makeplays.community.model.StoreInfoModel;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 收藏组训操作的service
 * @author wanrenyi 2016年9月2日下午5:07:23
 */
@Service
public class StoreInfoSrevice {

	@Autowired
	private StoreInfoDao storeInfoDao;
	
	/**
	 * 收藏组训
	 * @param storeInfo
	 * @throws Exception 
	 */
	public void addStoreInfo(StoreInfoModel storeInfo) throws Exception {
		storeInfoDao.addStoreByBean(storeInfo);
	}
	
	
	/**
	 * 取消收藏
	 * @param storeInfo
	 */
	public void cancleStoreInfo(StoreInfoModel storeInfo) {
		storeInfoDao.deleteStoreInfo(storeInfo);
	}
	
	/**
	 * 获取剧组的收藏的人数
	 * @param teamId
	 * @return
	 */
	public List<Map<String, Object>> getStoreUserCount() {
		List<Map<String, Object>> list = storeInfoDao.getStoreCount();
		return list;
	}
	
	/**
	 * 根据组训id查询出收藏组训的所有的人员信息
	 * @param teamId
	 * @return
	 */
	public List<Map<String, Object>> getStoreUser(String teamId){
		return storeInfoDao.getStoreUser(teamId);
	}
	
	/**
	 * 添加或取消收藏
	 * @param storeInfo
	 * @throws Exception
	 */
	public void addOrCancleStore(StoreInfoModel storeInfo) throws Exception {
		//添加还是取消收藏的根据是先从库中查询出收藏记录,如果存在则删除收藏,如果不存在则添加收藏
		List<StoreInfoModel> storeInfoModelList = this.storeInfoDao.getOneStoreInfo(storeInfo.getTeamId(), storeInfo.getUserId());
		if (storeInfoModelList == null || storeInfoModelList.size() == 0) { //添加收藏信息
			String storeId = UUIDUtils.getId();
			storeInfo.setStoreId(storeId);
			this.addStoreInfo(storeInfo);
		}else {
			this.cancleStoreInfo(storeInfo);
		}
	}
}
