ALTER TABLE `tab_notice_picture` MODIFY COLUMN `noticeVersion`  varchar(100) NOT NULL COMMENT '通告单版本,此处版本是根据tab_notice_time表中updateTime字段自动生成的一个版本号，格式yyyyMMddHHmmss';
ALTER TABLE `tab_notice_pushFedBack` MODIFY COLUMN `noticeVersion`  varchar(100) NOT NULL COMMENT '通告单版本,此处版本是根据tab_notice_time表中updateTime字段自动生成的一个版本号，格式yyyyMMddHHmmss';

UPDATE tab_notice_time set updateTime=createTime where updateTime is null;