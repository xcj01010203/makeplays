ALTER TABLE `tab_user_info`
ADD COLUMN `appVersion`  varchar(20) NULL COMMENT '版本号' AFTER `clientType`;