ALTER TABLE `tab_sceneview_info`
MODIFY COLUMN `holePeoples`  varchar(50) NULL DEFAULT NULL COMMENT '容纳人数' AFTER `distanceToHotel`,
MODIFY COLUMN `modifyViewCost`  varchar(50) NULL DEFAULT NULL COMMENT '改景费用' AFTER `isModifyView`,
MODIFY COLUMN `propCost`  varchar(50) NULL DEFAULT NULL COMMENT '道具陈设费用' AFTER `hasProp`,
MODIFY COLUMN `viewPrice`  varchar(50) NULL DEFAULT NULL COMMENT '场景价格' AFTER `contactRole`;