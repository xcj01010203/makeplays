package com.xiaotu.makeplays.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import net.sf.json.JSONObject;

import com.xiaotu.makeplays.weather.controller.dto.WeatherInfoDto;

public class WeatherUtils {

	/**
	 * 根据城市名称获取三天的天气信息
	 * @param date
	 * @return
	 */
	public static List<WeatherInfoDto> obtainWeatherInfoByCityName(String cityName) throws Exception {
		List<WeatherInfoDto> weatherList = new ArrayList<WeatherInfoDto>();
		
		Properties properties = PropertiesUitls.fetchProperties("/config.properties");
		String appId = properties.getProperty("YIYUAN_APPID");
		String secret = properties.getProperty("YIYUAN_SECRET");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		//从易源获取今天的天气数据
		JSONObject weatherResultJson = HttpUtils
				.httpGet("http://route.showapi.com/9-2"
						+ "?showapi_appid=" + appId 
						+ "&showapi_sign=" + secret
						+ "&showapi_timestamp=" + sdf.format(new Date())
						+ "&area=" + cityName
						+ "&needMoreDay=1");
		
		//请求返回的状态码
		int responseCode =  weatherResultJson.getInt("showapi_res_code");
		if (responseCode != 0) {
			throw new IllegalArgumentException("获取天气失败，错误码" + responseCode);
		}
		
		//第一天天气
		JSONObject f1Weather = weatherResultJson.getJSONObject("showapi_res_body").getJSONObject("f1");
		WeatherInfoDto f1WeatherInfoDto = new WeatherInfoDto();
		f1WeatherInfoDto.setDay(f1Weather.getString("day"));
		f1WeatherInfoDto.setDayWeather(f1Weather.getString("day_weather"));
		f1WeatherInfoDto.setDayTemperature(f1Weather.getString("day_air_temperature"));
		f1WeatherInfoDto.setNightTemperature(f1Weather.getString("night_air_temperature"));
		f1WeatherInfoDto.setDayWindDirection(f1Weather.getString("day_wind_direction"));
		f1WeatherInfoDto.setNightWindDirection(f1Weather.getString("night_wind_direction"));
		f1WeatherInfoDto.setDayWeatherCode(f1Weather.getString("day_weather_code"));
		f1WeatherInfoDto.setDayWeatherPic(f1Weather.getString("day_weather_pic"));
		
		weatherList.add(f1WeatherInfoDto);
		
		//第二天天气
		JSONObject f2Weather = weatherResultJson.getJSONObject("showapi_res_body").getJSONObject("f2");
		WeatherInfoDto f2WeatherInfoDto = new WeatherInfoDto();
		f2WeatherInfoDto.setDay(f2Weather.getString("day"));
		f2WeatherInfoDto.setDayWeather(f2Weather.getString("day_weather"));
		f2WeatherInfoDto.setDayTemperature(f2Weather.getString("day_air_temperature"));
		f2WeatherInfoDto.setNightTemperature(f2Weather.getString("night_air_temperature"));
		f2WeatherInfoDto.setDayWindDirection(f2Weather.getString("day_wind_direction"));
		f2WeatherInfoDto.setNightWindDirection(f2Weather.getString("night_wind_direction"));
		f2WeatherInfoDto.setDayWeatherCode(f2Weather.getString("day_weather_code"));
		f2WeatherInfoDto.setDayWeatherPic(f2Weather.getString("day_weather_pic"));
		
		weatherList.add(f2WeatherInfoDto);
		
		//第三天天气
		JSONObject f3Weather = weatherResultJson.getJSONObject("showapi_res_body").getJSONObject("f3");
		WeatherInfoDto f3WeatherInfoDto = new WeatherInfoDto();
		f3WeatherInfoDto.setDay(f3Weather.getString("day"));
		f3WeatherInfoDto.setDayWeather(f3Weather.getString("day_weather"));
		f3WeatherInfoDto.setDayTemperature(f3Weather.getString("day_air_temperature"));
		f3WeatherInfoDto.setNightTemperature(f3Weather.getString("night_air_temperature"));
		f3WeatherInfoDto.setDayWindDirection(f3Weather.getString("day_wind_direction"));
		f3WeatherInfoDto.setNightWindDirection(f3Weather.getString("night_wind_direction"));
		f3WeatherInfoDto.setDayWeatherCode(f3Weather.getString("day_weather_code"));
		f3WeatherInfoDto.setDayWeatherPic(f3Weather.getString("day_weather_pic"));
		
		weatherList.add(f3WeatherInfoDto);
		
		return weatherList;
	}
	
}
