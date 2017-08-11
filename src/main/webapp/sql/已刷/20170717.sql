ALTER TABLE `tab_view_schedulegroup_map`
MODIFY COLUMN `shootGroupId`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 1 COMMENT '计划组别ID，默认单组' AFTER `shootDate`;