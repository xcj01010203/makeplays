package com.xiaotu.makeplays.notice.dao.clip;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.notice.model.clip.CameraInfoModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 拍摄机位信息
 * @author xuchangjian 2015-11-9下午5:06:51
 */
@Repository
public class CameraInfoDao extends BaseDao<CameraInfoModel> {

	/**
	 * 查询剧组下的机位列表
	 * @param crewId
	 * @return
	 */
	public List<CameraInfoModel> queryByCrewId(String crewId) {
		String sql = "select * from " + CameraInfoModel.TABLE_NAME + " where crewId = 0 or crewId = ?";
		return this.query(sql, new Object[] {crewId}, CameraInfoModel.class, null);
	}
}
