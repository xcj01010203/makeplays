ALTER TABLE tab_feedback_reply MODIFY   `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '回复时间';
DROP TABLE tab_shoot_location;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tab_goods_info
-- ----------------------------
DROP TABLE IF EXISTS `tab_goods_info`;
CREATE TABLE `tab_goods_info` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `goodsName` varchar(100) DEFAULT NULL COMMENT '物品名称名称',
  `goodsType` smallint(6) DEFAULT NULL COMMENT '物品类型。0：普通道具；1：特殊道具；2：化妆；3、服装',
  `draftUrl` varchar(200) DEFAULT NULL COMMENT '草图、效果图存放地址',
  `draftDesc` varchar(300) DEFAULT NULL COMMENT '草图、效果图描述',
  `userId` varchar(32) DEFAULT NULL COMMENT '创建人员用户ID',
  `userName` varchar(20) DEFAULT NULL COMMENT '用户名',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `crewId` varchar(32) DEFAULT NULL COMMENT '剧组ID',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `stock` smallint(6) DEFAULT NULL COMMENT '库存量',
  PRIMARY KEY (`id`),
  KEY `ttab_goods_info_crewId` (`crewId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='物品基本信息表（包含服装、化妆、道具三种物品）';


DROP TABLE IF EXISTS `tab_view_goods_map`;
CREATE TABLE `tab_view_goods_map` (
  `id` varchar(32) NOT NULL COMMENT '场景与物品关联id',
  `viewId` varchar(32) DEFAULT NULL COMMENT '场景ID',
  `goodsId` varchar(32) DEFAULT NULL COMMENT '物品ID',
  `crewId` varchar(32) DEFAULT NULL COMMENT '剧组ID',
  PRIMARY KEY (`id`),
  KEY `tab_view_goods_map_viewid` (`viewId`) USING BTREE,
  KEY `tab_view_goods_map_goodsId` (`goodsId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='场景与物品关联信息表';


-- ----------------------------
-- Records of tab_goods_info
-- ----------------------------
