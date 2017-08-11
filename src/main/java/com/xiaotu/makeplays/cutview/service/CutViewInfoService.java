package com.xiaotu.makeplays.cutview.service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.cutview.dao.CutViewInfoDao;
import com.xiaotu.makeplays.cutview.model.CutViewInfoModel;
import com.xiaotu.makeplays.cutview.model.constants.CutViewStatusType;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 场景剪辑service
 * @author wanrenyi 2017年6月15日下午3:52:11
 */
/**
 * 
 * @author wanrenyi 2017年6月16日下午2:03:59
 */
@Service
public class CutViewInfoService {

	@Autowired
	private CutViewInfoDao cutViewDao;
	
	/**
	 * 根据剪辑id查询出当前场景的剪辑信息
	 * @param crewId
	 * @param viewId
	 * @return
	 * @throws Exception 
	 */
	public CutViewInfoModel queryCutInfoById(String id) throws Exception{
		return this.cutViewDao.queryCutViewInfoById(id);
	}
	
	/**
	 * 保存场景剪辑
	 * @param viewId
	 * @param id
	 * @param cutLength
	 * @param cutDateStr
	 * @param remark
	 * @param crewId
	 * @return
	 * @throws Exception 
	 */
	public String saveCutViewInfo(String id, String cutDataStr, String crewId) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		//分割字符串
		String[] cutArr = cutDataStr.split(",");
		if (StringUtils.isBlank(id)) {
			//当前场景没有被剪辑；新增剪辑信息;此时需要判断日期是否输入
			if (StringUtils.isBlank(cutArr[2])) {
				throw new IllegalArgumentException("请选择剪辑日期");
			}
			
			//生成id
			id = UUIDUtils.getId();
			CutViewInfoModel model = new CutViewInfoModel();
			model.setId(id); 
			model.setCrewId(crewId);
			model.setViewId(cutArr[0]);
			model.setCutDtae(sdf.parse(cutArr[2]));
			int cutLength = 0;
			if (StringUtils.isNotBlank(cutArr[3])) {
				cutLength = Integer.parseInt(cutArr[3]);
			}
			model.setCutLength(cutLength);
			if (cutLength != 0) {
				model.setCutStatus(CutViewStatusType.FinishedCutView.getValue());
			}else {
				model.setCutStatus(CutViewStatusType.UnFinishedCutView.getValue());
			}
			model.setRemark(cutArr[4]);
			model.setNoticeId(cutArr[1]);
			
			this.cutViewDao.add(model);
		}else {
			//更新
			//根据场景id查询剪辑表，判断是否已经保存过
			CutViewInfoModel model = this.cutViewDao.queryCutViewInfoById(id);
			
			if (StringUtils.isNotBlank(cutArr[2])) {
				model.setCutDtae(sdf.parse(cutArr[2]));
			}else {
				model.setCutDtae(null);
			}
			
			int cutLength = 0;
			if (StringUtils.isNotBlank(cutArr[3])) {
				cutLength = Integer.parseInt(cutArr[3]);
			}
			//如果时长为空时，表示将当前剪辑信息置为未完成
			if (cutLength != 0) {
				model.setCutLength(cutLength);
			}else{
				model.setCutLength(null);
			}
			
			//剪辑状态
			if (StringUtils.isNotBlank(cutArr[5])) {
				if (cutArr[5].equals("true")) {
					model.setCutStatus(1);
				}else if (cutArr[5].equals("false")) {
					model.setCutStatus(2);
				}
			}
			
			model.setRemark(cutArr[4]);
			
			//如果当前剪辑信息已经清空，则将剪辑删除
			if (model.getCutDtae() == null && StringUtils.isBlank(model.getRemark())) {
				//根据id删除当前剪辑信息
				this.cutViewDao.deleteOne(id, "id", model.TABLE_NAME);
				id = "";
			}else {
				//更新剪辑信息
				this.cutViewDao.updateWithNull(model, "id");
			}
			
		}
		return id;
	}
	
	/**
	 * 查询出剪辑列表（支持高级查询）
	 * @param crewId
	 * @param conditionMap
	 * @param isAll
	 * @param isASc
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> queryCutViewList(String crewId, Map<String, Object> conditionMap, boolean isAll, boolean isASc, Page page){
		return this.cutViewDao.queryCutViewList(crewId, conditionMap, isAll, isASc, page);
	}
	
	/**
	 * 查询剪辑列表页面统计数据
	 * @param crewId
	 * @param conditionMap
	 * @param isAll
	 * @return
	 */
	public List<Map<String, Object>> queryCutViewTotalDataInfo(String crewId, Map<String, Object> conditionMap, boolean isAll){
		return this.cutViewDao.queryCutViewStaticInfo(crewId, conditionMap, isAll);
	}
	
	/**
	 * 查询剪辑的进度表中的数据列表
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryCutViewStatisticInfo(String crewId){
		return this.cutViewDao.queryCutViewStatisticList(crewId);
	}
	
	/**
	 * 查询剪辑总天数
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryTotalCutDays(String crewId){
		return this.cutViewDao.queryTotalCutDays(crewId);
	}
	
	/**
	 * 查询每集剪辑分钟数（支持电影查询）
	 * @param crewId
	 * @param isMovice
	 * @return
	 */
	public List<Map<String, Object>> queryPreSeriesNoCutInfo(String crewId){
		return this.cutViewDao.queryPreSeriesNoCutInfo(crewId);
	}
	
	/**
	 * 查询每日剪辑量
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryPreDayStatisticData(String crewId){
		return this.cutViewDao.queryPreDayCutStatistic(crewId);
	}
	
	/**
	 * 更新剪辑状态
	 * @param id
	 * @param cutStatus
	 */
	public void updateCutViewStatus(String id, boolean cutStatus) {
		this.cutViewDao.updateCutViewStatus(id, cutStatus);
	}
	
	/**
	 * 根据剧组id删除当前剧组的剪辑信息
	 * @param crewId
	 */
	public void deleteCutViewInfoByCrewId(String crewId) {
		this.cutViewDao.deleteCutViewByCrewId(crewId);
	}
	
	/**
	 * 根据通告单id或则场景id查询出剪辑信息列表
	 * @param noticeId
	 * @param viewId
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryCutInfoByNoticeIdOrViewId(String noticeId, String viewId, String crewId){
		return this.cutViewDao.queryCutInfoByNoticeIdOrViewId(noticeId, viewId, crewId);
	}
}
