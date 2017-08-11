ALTER TABLE `tab_notice_pushFedBack` MODIFY COLUMN `backStatus`  smallint(6) NULL DEFAULT 1 COMMENT '反馈状态 1：未收取  2：已收取   3：已查看';

ALTER table tab_scenario_info add(`downloaded` smallint(6) DEFAULT '0' COMMENT '是否被下载过，用于手机端判断剧本是否有更新');

ALTER TABLE `tab_crew_contact`
DROP COLUMN `department`,
CHANGE COLUMN `idNumber` `identityCertNumber` varchar(18) NULL DEFAULT NULL COMMENT '身份证件号码',
MODIFY COLUMN `duties` varchar(200) NULL DEFAULT NULL COMMENT '职务，格式：部门-职务，多个以逗号隔开',
ADD identityCertType SMALLINT(6) DEFAULT 1 COMMENT '身份证件类型：1-身份证  2-护照  3-台胞证  4-军官证   5-其他';

ALTER TABLE `tab_crew_contact`
CHANGE COLUMN `stayingDate` `checkInDate`  date NULL DEFAULT NULL COMMENT '入住日期';

ALTER TABLE `tab_crew_contact`
CHANGE COLUMN `identityCertType` `identityCardType`  smallint(6) NULL DEFAULT 1 COMMENT '身份证件类型：1-身份证  2-护照  3-台胞证  4-军官证   5-其他',
CHANGE COLUMN `identityCertNumber` `identityCardNumber`  varchar(18) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '身份证件号码' AFTER `identityCardType`;

ALTER TABLE `tab_crew_contact` DROP COLUMN `duties`;


CREATE TABLE tab_contact_sysrole_map(
    id VARCHAR(32) NOT NULL PRIMARY KEY,
    crewId VARCHAR(32) NOT NULL COMMENT '剧组ID',
    contactId VARCHAR(32) NOT NULL COMMENT '剧组联系人ID',
    sysroleId VARCHAR(32) NOT NULL COMMENT '系统角色ID'
) COMMENT '剧组联系表和系统角色关联表';

ALTER TABLE tab_liveConverAdd_info ADD `cpackupTime` timestamp NULL DEFAULT NULL COMMENT '收工时间';
ALTER TABLE tab_liveConverAdd_info ADD `cshootLocation` varchar(1000) DEFAULT NULL COMMENT '拍摄地点';
ALTER TABLE tab_liveConverAdd_info ADD `cshootScene` varchar(1000) DEFAULT NULL COMMENT '拍摄场景';

UPDATE tab_finance_setting_info set pwdStatus=0 where pwdStatus is null;
ALTER TABLE `tab_finance_setting_info` MODIFY COLUMN `pwdStatus`  smallint(6) NULL DEFAULT 0 COMMENT '是否启用密码功能，0：禁用，1：启用';
