-- CREATE TABLE `tab_schedule_info` (
--   `id` varchar(32) NOT NULL COMMENT '计划ID',
--   `name` varchar(200) DEFAULT NULL COMMENT '计划名称',
--   `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--   `crewId` varchar(32) DEFAULT NULL COMMENT '剧组ID',
--   PRIMARY KEY (`id`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='计划信息表';
CREATE TABLE `tab_schedule_group` (
  `id` varchar(32) NOT NULL COMMENT '计划分组ID',
--   `scheduleId` varchar(32) NOT NULL COMMENT '计划ID',
  `groupName` varchar(200) DEFAULT NULL COMMENT '计划分组名称',
  `sequence` smallint(6) DEFAULT NULL COMMENT '排列顺序',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `crewId` varchar(32) DEFAULT NULL COMMENT '剧组ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='计划分组信息表';
CREATE TABLE `tab_view_schedulegroup_map` (
  `id` varchar(32) NOT NULL COMMENT '关联信息ID',
  `viewId` varchar(32) DEFAULT NULL COMMENT '场景ID',
  `planGroupId` varchar(32) DEFAULT NULL COMMENT '计划分组ID',
  `shootDate` date DEFAULT NULL COMMENT '计划拍摄日期',
  `shootGroupId` varchar(32) DEFAULT NULL COMMENT '计划组别ID',
  `sequence` smallint(6) DEFAULT NULL COMMENT '排列顺序',
  `isLock` smallint(1) DEFAULT '0' COMMENT '是否锁定',
  `crewId` varchar(32) DEFAULT NULL COMMENT '剧组ID',
  PRIMARY KEY (`id`),
  KEY `idx_tab_view_schedulegroup_map_crewId` (`crewId`) USING BTREE,
  KEY `idx_tab_view_schedulegroup_map_planGroupId` (`planGroupId`),
  KEY `idx_tab_view_schedulegroup_map_viewId` (`viewId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='计划分组与场景关联关系表';