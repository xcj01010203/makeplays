drop TABLE IF EXISTS tab_weather_info;
CREATE TABLE tab_weather_info (
        id VARCHAR(32) NOT NULL PRIMARY KEY,
        cityName VARCHAR(32) NOT NULL COMMENT '城市名',
        day date NOT NULL COMMENT '当前天',
        dayWeather VARCHAR(100) NULL COMMENT '白天天气',
        nightWeather VARCHAR(100) NULL COMMENT '晚上天气',
        dayTemperature VARCHAR(100) NULL COMMENT '白天气温',
        nightTemperature VARCHAR(100) NULL COMMENT '晚上气温',
        dayWindDirection VARCHAR(100) NULL COMMENT '白天风向',
        nightWindDirection VARCHAR(100) NULL COMMENT '晚上风向',
        dayWeatherCode VARCHAR(100) NULL COMMENT '白天天气编码',
        dayWeatherPic VARCHAR(100) NULL COMMENT '白天天气图标',
        updateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '天气信息';

alter table tab_convertAddress_info modify column afterViewIds varchar(2000);
alter table tab_notice_time modify column noticeContact varchar(2000);
alter table tab_notice_time modify column roleConvertRemark varchar(2000);
ALTER TABLE tab_work_experience_info ADD COLUMN allowUpdate VARCHAR(10) COMMENT '是否允许修改';