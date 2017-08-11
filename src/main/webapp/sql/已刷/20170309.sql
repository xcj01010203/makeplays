DROP TABLE IF EXISTS `tab_cater_info`;
CREATE TABLE `tab_cater_info` (
  `caterId` varchar(32) NOT NULL COMMENT 'ID',
  `caterDate` date DEFAULT NULL COMMENT '就餐日期',
  `budget` double DEFAULT NULL COMMENT '本日预算',
  `crewId` varchar(32) DEFAULT '' COMMENT '剧组id',
  PRIMARY KEY (`caterId`),
  KEY `crewId` (`crewId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='餐饮信息表';

DROP TABLE IF EXISTS `tab_cater_money_info`;
CREATE TABLE `tab_cater_money_info` (
  `caterMoneyId` varchar(32) NOT NULL COMMENT 'ID',
  `caterId` varchar(32) DEFAULT NULL COMMENT '餐饮id',
  `peopleCount` int(20) DEFAULT NULL COMMENT '人数',
  `caterCount` int(20) DEFAULT NULL COMMENT '份数',
  `caterType` varchar(50) DEFAULT NULL COMMENT '餐别',
  `caterMoney` double DEFAULT '1' COMMENT '金额',
  `perCapita` double DEFAULT NULL COMMENT '人均',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `crewId` varchar(32) DEFAULT NULL COMMENT '剧组id',
  PRIMARY KEY (`caterMoneyId`),
  KEY `caterId` (`caterId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='餐饮金额详细信息表';

INSERT INTO `tab_cater_money_info` VALUES ('1', '', null, null, '普餐', '1', null, '', '0');
INSERT INTO `tab_cater_money_info` VALUES ('2', '', null, null, '特餐', '1', null, '', '0');
INSERT INTO `tab_cater_money_info` VALUES ('3', '', null, null, '清真', '1', null, '', '0');
INSERT INTO `tab_cater_money_info` VALUES ('4', '', null, null, '素餐', '1', null, '', '0');
INSERT INTO `tab_cater_money_info` VALUES ('5', '', null, null, '茶水', '1', null, '', '0');
INSERT INTO `tab_cater_money_info` VALUES ('6', '', null, null, '零食', '1', null, '', '0');
INSERT INTO `tab_cater_money_info` VALUES ('7', '', null, null, '其它', '1', null, '', '0');

DROP TABLE IF EXISTS `tab_cater_type`;