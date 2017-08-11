CREATE TABLE `tab_crew_picture_info` (
  `id` varchar(32) NOT NULL COMMENT '剧照id',
  `attpackId` VARCHAR(32) DEFAULT null COMMENT '附件包id',
  `crewId` VARCHAR(32) DEFAULT null COMMENT '剧组id',
  `indexPictureId` varchar(32) DEFAULT null COMMENT '封面照片id（附件id）',
  `attpackName` varchar(50) DEFAULT null COMMENT '剧照名称',
  `createTime` date DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `attpackId` (`attpackId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='剧照信息表';