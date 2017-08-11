CREATE TABLE `tab_car_info` (
  `carId` varchar(32) NOT NULL COMMENT '车辆ID',
  `carNo` int(5) DEFAULT NULL COMMENT '车辆编号',
  `driver` varchar(20) DEFAULT NULL COMMENT '司机',
  `phone` varchar(20) DEFAULT NULL COMMENT '电话',
  `carModel` varchar(255) DEFAULT NULL COMMENT '车辆型号',
  `carNumber` varchar(255) DEFAULT NULL COMMENT '车牌号',
  `status` smallint(2) DEFAULT '1' COMMENT '状态，0：离组，1：在组',
  `crewId` varchar(32) DEFAULT NULL COMMENT '剧组ID',
  PRIMARY KEY (`carId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tab_car_work` (
  `workId` varchar(32) NOT NULL COMMENT '加油登记ID',
  `carId` varchar(32) DEFAULT NULL COMMENT '车辆ID',
  `workNo` int(10) DEFAULT NULL COMMENT '编号',
  `workDate` date DEFAULT NULL COMMENT '工作日期',
  `mileage` double DEFAULT NULL COMMENT '工作结束里程',
  `kilometers` double DEFAULT NULL COMMENT '公里数',
  `oilLitres` double DEFAULT NULL COMMENT '加油升数',
  `oilMoney` double DEFAULT NULL COMMENT '加油金额',
  `crewId` varchar(32) DEFAULT NULL COMMENT '剧组ID',
  PRIMARY KEY (`workId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;