ALTER TABLE `tab_contract_produce`
MODIFY COLUMN `company`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司' AFTER `contractDate`;

ALTER TABLE `tab_contract_actor`
MODIFY COLUMN `actorName`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '职员姓名' AFTER `contractDate`;

ALTER TABLE `tab_contract_worker`
MODIFY COLUMN `workerName`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '职员姓名' AFTER `contractDate`;