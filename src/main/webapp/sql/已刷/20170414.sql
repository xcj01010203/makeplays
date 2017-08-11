ALTER TABLE `tab_feedback_info`
ADD COLUMN `clientType`  smallint(1) NULL COMMENT '终端类型，0：pc，1：ios，2：android' AFTER `createTime`,
ADD COLUMN `status`  smallint(1) DEFAULT 0 COMMENT '状态，0：未读，1：已读' AFTER `clientType`;
ALTER TABLE `tab_feedback_reply`
ADD COLUMN `status`  smallint(1) DEFAULT 0 COMMENT '状态，0：未读，1：已读' AFTER `createTime`,
ADD COLUMN `clientType`  smallint(1) NULL COMMENT '终端类型，0：pc，1：ios，2：android' AFTER `status`;