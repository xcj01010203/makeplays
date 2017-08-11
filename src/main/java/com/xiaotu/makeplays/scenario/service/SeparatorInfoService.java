package com.xiaotu.makeplays.scenario.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.scenario.dao.SeparatorInfoDao;
import com.xiaotu.makeplays.scenario.model.SeparatorInfoModel;
import com.xiaotu.makeplays.utils.Page;

/**
 * 剧组分隔符信息的service
 * @author wanrenyi 2016年7月28日下午2:29:04
 */
@Service
public class SeparatorInfoService {

	@Autowired
	private SeparatorInfoDao separatorInfoDao;
	
	/**
	 * 根据多个条件查询符号信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<SeparatorInfoModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.separatorInfoDao.queryManyByMutiCondition(conditionMap, page);
	}
	
	/**
	 * 根据ID查找符号信息
	 * @param opeId
	 * @return
	 * @throws Exception
	 */
	public SeparatorInfoModel queryOneById(String opeId) throws Exception {
		return this.separatorInfoDao.queryOneById(opeId);
	}
	
	/**
	 * 更新符号信息
	 * @param separatorInfoModel
	 * @throws Exception
	 */
	public void updateSeparatorInfo(SeparatorInfoModel separatorInfoModel) throws Exception {
		this.separatorInfoDao.update(separatorInfoModel, "sepaId");
	}
	
	/**
	 * 保存符号信息
	 * @param separatorInfoModel
	 * @throws Exception 
	 */
	public void addSeparatorInfo (SeparatorInfoModel separatorInfoModel) throws Exception {
		this.separatorInfoDao.add(separatorInfoModel);
	}
	
	/**
	 * 删除分割附信息
	 * @param sepaId
	 * @throws Exception 
	 */
	public void deleteSeparatorInfo (String sepaId) throws Exception {
		this.separatorInfoDao.deleteOne(sepaId, "sepaId", SeparatorInfoModel.TABLE_NAME);
	}
	
	/**
	 * 获取符号表中最大ID中的数字
	 * 如：从s1, s2, s3获取3
	 * @return
	 */
	public int genLastSepaIdNum() {
		List<SeparatorInfoModel> separatorInfoList = this.queryManyByMutiCondition(null, null);
		
		int lastSepaIdNum = 0;
		for (SeparatorInfoModel separatorInfoModel : separatorInfoList) {
			String sepaId = separatorInfoModel.getSepaId();
			int sepaIdNum = Integer.parseInt(sepaId.substring(1, sepaId.length()));
			if (sepaIdNum > lastSepaIdNum) {
				lastSepaIdNum = sepaIdNum;
			}
		}
		
		return lastSepaIdNum;
	}
	
	/**
	 * 查询除了自己之外其他有相同名称的操作符
	 * @param sepaId
	 * @param sepaName
	 * @return
	 */
	public List<SeparatorInfoModel> querySameNameSepaExceptOwn(String sepaId, String sepaName) {
		return this.separatorInfoDao.querySameNameSepaExceptOwn(sepaId, sepaName);
	}
}
