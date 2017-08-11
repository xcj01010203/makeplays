ALTER TABLE tab_finance_setting_info ADD contractAdvanceRemindDays INT DEFAULT 5 COMMENT '合同支付提前提醒天数';

DROP TABLE if EXISTS tab_android_version_info;
CREATE TABLE `tab_android_version_info` (
  `id` varchar(32) NOT NULL,
  `versionNo` int(11) NOT NULL COMMENT '版本号',
  `versionName` varchar(100) NOT NULL COMMENT '版本名称',
  `updateLog` varchar(500) DEFAULT NULL COMMENT '版本更新日志',
  `size` mediumtext COMMENT '更新文件大小',
  `storePath` varchar(500) DEFAULT NULL COMMENT '文件存储路径',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
);