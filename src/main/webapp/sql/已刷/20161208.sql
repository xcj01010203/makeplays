-- 增加用户IP地址字段
ALTER TABLE `tab_user_info`
ADD COLUMN `ip`  varchar(500) NULL AFTER `age`;

-- 增加是否验证ip字段
ALTER TABLE `tab_finance_setting_info`
ADD COLUMN `ipStatus`  smallint(2) NULL DEFAULT 0 COMMENT '是否根据用户IP验证手机号，0：否，1：是' AFTER `financePassword`;