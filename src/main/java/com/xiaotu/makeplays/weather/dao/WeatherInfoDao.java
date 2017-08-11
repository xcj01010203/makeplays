package com.xiaotu.makeplays.weather.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.weather.model.WeatherInfoModel;

/**
 * 天气信息
 * @author xuchangjian 2016-11-7下午5:14:53
 */
@Repository
public class WeatherInfoDao extends BaseDao<WeatherInfoModel> {
	
	
	/**
	 * 根据城市名 和当前时间获取三天天气
	 * 
	 * @param cityName
	 * @return
	 */
	public List<WeatherInfoModel> queryWeatherInfoByCityName(String cityName,String  updateTime){
		String sql = "select * from "+WeatherInfoModel.TABLE_NAME+" where cityName = ? and updateTime = ? order by day ";
		List<WeatherInfoModel> list = this.query(sql, new Object[]{cityName,updateTime}, WeatherInfoModel.class, null);
		return list;
	}
}
