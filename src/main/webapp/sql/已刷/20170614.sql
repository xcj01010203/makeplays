CREATE TABLE `tab_web_version_info` (
  `id` varchar(32) NOT NULL,
  `versionName` varchar(100) NOT NULL COMMENT '版本名称',
  `insideUpdateLog` text COMMENT '内部更新日志',
  `userUpdateLog` text COMMENT '用户更新日志',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='web端版本管理';

update tab_sys_authority set authName='版本管理',operDesc='管理安卓客户端和web端版本升级' where authName='APP版本管理';