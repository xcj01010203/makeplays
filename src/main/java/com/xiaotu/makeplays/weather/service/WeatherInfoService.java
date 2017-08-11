package com.xiaotu.makeplays.weather.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.utils.WeatherUtils;
import com.xiaotu.makeplays.weather.controller.dto.WeatherInfoDto;
import com.xiaotu.makeplays.weather.dao.WeatherInfoDao;
import com.xiaotu.makeplays.weather.model.WeatherInfoModel;

/**
 * 天气信息
 * @author xuchangjian 2016-11-7下午5:18:10
 */
@Service
public class WeatherInfoService {
	private static final SimpleDateFormat SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat SimpleDateFormat1 = new SimpleDateFormat("yyyyMMdd");
	@Autowired
	private WeatherInfoDao weatherInfoDao;
	
	/**
	 * 根据城市名称查询数据库中有没有天气信息  如果没有就请求天气接口
	 * 
	 * @param cityName
	 * @return
	 * @throws Exception
	 */
	public List<WeatherInfoDto> saveWeatherInfoByCityName(String cityName) throws Exception{
		List<WeatherInfoDto> backList = new ArrayList<WeatherInfoDto>();
		String updateTime = SimpleDateFormat.format(new Date());
		List<WeatherInfoModel> list = weatherInfoDao.queryWeatherInfoByCityName(cityName,updateTime);
		if(list==null||list.size()!=3){
			List<WeatherInfoModel> listModel = new ArrayList<WeatherInfoModel>();//需要保存到库里面的天气信息
			backList = WeatherUtils.obtainWeatherInfoByCityName(cityName);//调用接口获取天气信息
			for(WeatherInfoDto wDto :backList){
				WeatherInfoModel wModel = new WeatherInfoModel();
				wModel.setId(UUIDUtils.getId());
				wModel.setDay(SimpleDateFormat1.parse(wDto.getDay()));
				wModel.setDayTemperature(wDto.getDayTemperature());
				wModel.setDayWeather(wDto.getDayWeather());
				wModel.setDayWeatherCode(wDto.getDayWeatherCode());
				wModel.setDayWeatherPic(wDto.getDayWeatherPic());
				wModel.setDayWindDirection(wDto.getDayWindDirection());
				wModel.setNightTemperature(wDto.getNightTemperature());
				wModel.setNightWeather(wDto.getNightWeather());
				wModel.setNightWindDirection(wDto.getNightWindDirection());
				wModel.setCityName(cityName);
				wModel.setUpdateTime(SimpleDateFormat.parse(SimpleDateFormat.format(new Date())));
				listModel.add(wModel);
			}
			weatherInfoDao.addBatch(listModel, WeatherInfoModel.class);//批量保存信息
		}else{
			for(WeatherInfoModel wModel :list){
				WeatherInfoDto wDto = new WeatherInfoDto();
				wDto.setDay(SimpleDateFormat1.format(wModel.getDay()));
				wDto.setDayTemperature(wModel.getDayTemperature());
				wDto.setDayWeather(wModel.getDayWeather());
				wDto.setDayWeatherCode(wModel.getDayWeatherCode());
				wDto.setDayWeatherPic(wModel.getDayWeatherPic());
				wDto.setDayWindDirection(wModel.getDayWindDirection());
				wDto.setNightTemperature(wModel.getNightTemperature());
				wDto.setNightWeather(wModel.getNightWeather());
				wDto.setNightWindDirection(wModel.getNightWindDirection());
				backList.add(wDto);
			}
		}
		return backList;
	}
}
