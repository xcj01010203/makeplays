/*
Navicat MySQL Data Transfer

Source Server         : 192.168.10.250
Source Server Version : 50624
Source Host           : 192.168.10.250:3306
Source Database       : produce

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2017-06-15 15:32:08
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tab_cut_view_info
-- ----------------------------
DROP TABLE IF EXISTS `tab_cut_view_info`;
CREATE TABLE `tab_cut_view_info` (
  `id` varchar(32) NOT NULL COMMENT '剪辑ID',
  `cutLength` mediumtext COMMENT '剪辑时长(单位：秒)',
  `cutDtae` date DEFAULT NULL COMMENT '剪辑日期',
  `viewId` varchar(32) DEFAULT NULL COMMENT '场景id',
  `noticeId` varchar(32) DEFAULT NULL COMMENT '通告单id',
  `remark` varchar(300) DEFAULT NULL COMMENT '备注',
  `cutstatus` smallint(2) DEFAULT '2' COMMENT '剪辑状态；1、完成；2、未完成',
  `crewId` varchar(32) DEFAULT NULL COMMENT '剧组ID',
  KEY `tab_cut_view_info_crewId` (`crewId`) USING BTREE,
  KEY `tab_cut_view_info_viewId` (`viewId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='场景剪辑信息表';

ALTER TABLE tab_crew_info ADD COLUMN lengthPerSet DOUBLE DEFAULT NULL COMMENT'每集时长';
ALTER TABLE tab_crew_info ADD COLUMN cutRate DOUBLE DEFAULT NULL COMMENT'预计精剪比';
