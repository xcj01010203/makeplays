INSERT INTO tab_currency_info values('2', '台币', 'TWD', 1, 1, 0.2121, '0');

ALTER TABLE tab_validCode_info ADD (type smallint(6) NOT NULL DEFAULT 1 COMMENT '类型：1-找回密码  2-注册  3-修改手机号');
ALTER TABLE `tab_validCode_info` CHANGE COLUMN `status` `valid`  smallint(6) NOT NULL DEFAULT 1 COMMENT '是否有效，0：无效  1：有效';
ALTER TABLE `tab_validCode_info` CHANGE COLUMN `codeId` `id`  varchar(32) NOT NULL COMMENT '验证码ID';
ALTER TABLE tab_validCode_info RENAME to tab_verifyCode_info;

ALTER TABLE `tab_contract_worker` MODIFY COLUMN `bankAccountNumber`  varchar(100) NULL DEFAULT NULL COMMENT '银行账户账号';
ALTER TABLE `tab_contract_actor` MODIFY COLUMN `bankAccountNumber`  varchar(100) NULL DEFAULT NULL COMMENT '银行账户账号';
ALTER TABLE `tab_contract_produce` MODIFY COLUMN `bankAccountNumber`  varchar(100) NULL DEFAULT NULL COMMENT '银行账户账号';


CREATE TABLE `tab_feedback_info` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `userId` varchar(32) NOT NULL COMMENT '用户ID',
  `contact` varchar(50) DEFAULT NULL COMMENT '联系方式',
  `message` text NOT NULL COMMENT '反馈意见',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) COMMENT='用户反馈信息表';

ALTER TABLE tab_user_info ADD `age` int(4) DEFAULT 20 COMMENT '年龄';
ALTER TABLE tab_user_info ADD `profile` varchar(500) DEFAULT NULL COMMENT '个人简介';