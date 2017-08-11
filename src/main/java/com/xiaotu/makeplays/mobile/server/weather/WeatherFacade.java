package com.xiaotu.makeplays.mobile.server.weather;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.weather.controller.dto.WeatherInfoDto;
import com.xiaotu.makeplays.weather.service.WeatherInfoService;

/**
 * 天气相关接口
 * @author xuchangjian 2016-9-23下午2:47:42
 */
@Controller
@RequestMapping("/interface/weatherFacade")
public class WeatherFacade {

	Logger logger = LoggerFactory.getLogger(WeatherFacade.class);
	
	@Autowired
	private WeatherInfoService weatherInfoService;
	
	/**
	 * 获取天气信息
	 * @param cityName 城市名称
	 * @param noticeId 通告单id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/obtainWeatherInfo")
	public Object obtainWeatherInfo(String cityName) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			if (StringUtils.isBlank(cityName)) {
				throw new IllegalArgumentException("请提供城市信息");
			}
			//根据通告单的id查询出通告单的额信息
			List<WeatherInfoDto> weatherList = weatherInfoService.saveWeatherInfoByCityName(cityName);
			
			resultMap.put("weatherList", weatherList);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException("暂无天气信息", ie);
		} catch (Exception e) {
			logger.error("暂无天气信息", e);
			throw new IllegalArgumentException("暂无天气信息", e);
		}
		
		return resultMap;
	}
}
