CREATE TABLE `tab_cache_info` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `type` smallint(2) DEFAULT NULL COMMENT '类型：1：场景表隐藏列',
  `userId` varchar(32) DEFAULT NULL COMMENT '用户ID',
  `crewId` varchar(32) DEFAULT NULL COMMENT '剧组ID',
  `content` text COMMENT '具体内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='信息记录表';

ALTER TABLE tab_car_info ADD COLUMN departments VARCHAR(32) DEFAULT NULL COMMENT '部门id';
ALTER TABLE tab_car_work ADD COLUMN startMileage double DEFAULT NULL COMMENT '工作开始里程数';

ALTER TABLE tab_car_info ADD COLUMN sequence int(6) DEFAULT NULL COMMENT '序号';