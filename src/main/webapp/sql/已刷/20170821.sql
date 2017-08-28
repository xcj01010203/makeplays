DROP TABLE IF EXISTS `tab_publish_scenario_setting`;
CREATE TABLE `tab_publish_scenario_setting` (
  `id` varchar(32) NOT NULL,
  `crewId` varchar(32) NOT NULL COMMENT '剧组ID',
  `userId` varchar(32) NOT NULL COMMENT '用户ID',
  `autoShowPublishWin` smallint(6) NOT NULL DEFAULT '1' COMMENT '是否自动显示发布剧本窗口',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='发布剧本设置';