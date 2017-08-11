DROP TABLE IF EXISTS tab_feedback_reply;
CREATE TABLE `tab_feedback_reply` (
  `id` varchar(32) NOT NULL DEFAULT '' COMMENT '主键',
  `feedbackId` varchar(32) DEFAULT NULL COMMENT '用户反馈ID',
  `userId` varchar(32) DEFAULT NULL COMMENT '用户ID',
  `reply` text COMMENT '回复内容',
  `createTime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '回复时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户反馈回复信息表';