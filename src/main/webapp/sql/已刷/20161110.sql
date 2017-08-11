CREATE TABLE tab_download_scenario_record(
    id VARCHAR(32) NOT NULL PRIMARY KEY,
    crewId VARCHAR(32) NOT NULL COMMENT '剧组ID',
    clientUUID VARCHAR(100) NULL COMMENT '设备号'
) COMMENT '剧组中剧本下载记录';
ALTER TABLE `tab_scenario_info` DROP COLUMN `downloaded`;