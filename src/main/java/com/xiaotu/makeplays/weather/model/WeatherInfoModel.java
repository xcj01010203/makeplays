package com.xiaotu.makeplays.weather.model;

import java.util.Date;

public class WeatherInfoModel {
	
	public static final String TABLE_NAME = "tab_weather_info";
	
	private String id;

	/**
	 * 当前天
	 */
	private Date day;

	/**
	 * 白天天气
	 */
	private String dayWeather;
	
	/**
	 * 晚上天气
	 */
	private String nightWeather;
	
	/**
	 * 白天气温
	 */
	private String dayTemperature;
	
	/**
	 * 晚上气温
	 */
	private String nightTemperature;
	
	/**
	 * 白天风向
	 */
	private String dayWindDirection;
	
	/**
	 * 晚上风向
	 */
	private String nightWindDirection;
	
	/**
	 * 白天天气编码
	 */
	private String dayWeatherCode;
	
	/**
	 * 白天天气图标
	 */
	private String dayWeatherPic;
	//获取天气时间
	private Date updateTime;
	//城市名称
	private String cityName;
	
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDay() {
		return this.day;
	}

	public void setDay(Date day) {
		this.day = day;
	}

	public String getDayWeather() {
		return this.dayWeather;
	}

	public void setDayWeather(String dayWeather) {
		this.dayWeather = dayWeather;
	}

	public String getNightWeather() {
		return this.nightWeather;
	}

	public void setNightWeather(String nightWeather) {
		this.nightWeather = nightWeather;
	}

	public String getDayTemperature() {
		return this.dayTemperature;
	}

	public void setDayTemperature(String dayTemperature) {
		this.dayTemperature = dayTemperature;
	}

	public String getNightTemperature() {
		return this.nightTemperature;
	}

	public void setNightTemperature(String nightTemperature) {
		this.nightTemperature = nightTemperature;
	}

	public String getDayWindDirection() {
		return this.dayWindDirection;
	}

	public void setDayWindDirection(String dayWindDirection) {
		this.dayWindDirection = dayWindDirection;
	}

	public String getNightWindDirection() {
		return this.nightWindDirection;
	}

	public void setNightWindDirection(String nightWindDirection) {
		this.nightWindDirection = nightWindDirection;
	}

	public String getDayWeatherCode() {
		return this.dayWeatherCode;
	}

	public void setDayWeatherCode(String dayWeatherCode) {
		this.dayWeatherCode = dayWeatherCode;
	}

	public String getDayWeatherPic() {
		return this.dayWeatherPic;
	}

	public void setDayWeatherPic(String dayWeatherPic) {
		this.dayWeatherPic = dayWeatherPic;
	}
}
