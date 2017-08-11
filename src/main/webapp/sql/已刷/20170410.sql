ALTER TABLE `tab_message_info`
ADD COLUMN `readStatus`  smallint(2) NULL DEFAULT 0 COMMENT '查看状态，0：未查看，1：已查看' AFTER `buzId`;
update tab_message_info set readStatus=1 where remindTime<now() and status=1;