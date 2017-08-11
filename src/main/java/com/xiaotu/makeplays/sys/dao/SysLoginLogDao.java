package com.xiaotu.makeplays.sys.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.sys.model.SysLoginModel;
import com.xiaotu.makeplays.utils.BaseDao;

@Repository
public class SysLoginLogDao extends BaseDao<SysLoginModel> {
	
	public boolean getIsExistLog(String userId,String ip){
		String sql = "select 1 from "+SysLoginModel.getTableName()+" where userId=? and ip=?";
		List<Map<String,Object>> li = this.query(sql, new Object[]{userId,ip}, null);
		if(li!=null && li.size()>0){
			return true;
		}
		return false;
	}

}
