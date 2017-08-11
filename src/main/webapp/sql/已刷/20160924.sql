DELETE from tab_message_info;
ALTER TABLE tab_message_info ADD receiverId varchar(32) DEFAULT NULL COMMENT '消息接收人ID';
ALTER TABLE tab_message_info ADD remindTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '消息提醒时间';
ALTER TABLE tab_message_info ADD type SMALLINT DEFAULT NULL COMMENT '消息类型：1-发布通告单  2-入组申请审核成功  3-入组申请审核失败  4-申请入组  5-付款单发票提醒  6-合同支付提醒';
ALTER TABLE tab_message_info CHANGE COLUMN `messageId` `id`  varchar(32) NOT NULL COMMENT '消息ID';
ALTER TABLE tab_message_info ADD title varchar(100) DEFAULT NULL COMMENT '标题';
ALTER TABLE tab_message_info ADD buzId varchar(32) DEFAULT NULL COMMENT '业务ID';

