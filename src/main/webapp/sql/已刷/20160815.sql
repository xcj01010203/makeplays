-- 修改消息表
ALTER TABLE `tab_message_info`
DROP COLUMN `dealerId`,
DROP COLUMN `messageType`,
DROP COLUMN `invoiceId`,
DROP COLUMN `remindTime`;

ALTER TABLE `tab_message_info` DROP COLUMN STATUS;
alter TABLE tab_message_info ADD `status` smallint(6) DEFAULT '0' COMMENT '消息状态 0：未读  1：已读';

-- 修改合同表添加附件包ID
ALTER TABLE tab_contract_actor ADD `attpackId` varchar(32) DEFAULT NULL COMMENT '附件包ID';
ALTER TABLE tab_contract_worker ADD `attpackId` varchar(32) DEFAULT NULL COMMENT '附件包ID';
ALTER TABLE tab_contract_produce ADD `attpackId` varchar(32) DEFAULT NULL COMMENT '附件包ID';

ALTER TABLE tab_contract_worker ADD `financeSubjName` varchar(100) DEFAULT NULL COMMENT '财务科目名称';
ALTER TABLE tab_contract_actor ADD `financeSubjName` varchar(100) DEFAULT NULL COMMENT '财务科目名称';
ALTER TABLE tab_contract_produce ADD `financeSubjName` varchar(100) DEFAULT NULL COMMENT '财务科目名称';

ALTER TABLE `tab_contract_produce` CHANGE COLUMN `contactPhone` `phone` varchar(30) NULL DEFAULT NULL COMMENT '联系电话';

