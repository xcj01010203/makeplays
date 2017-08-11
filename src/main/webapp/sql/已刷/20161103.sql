ALTER TABLE tab_contract_actor ADD `identityCardType` smallint(6) DEFAULT '1' COMMENT '身份证件类型：1-身份证  2-护照  3-台胞证  4-军官证   5-其他';
ALTER TABLE `tab_contract_actor` CHANGE COLUMN `idNumber` `identityCardNumber`  varchar(18) NULL DEFAULT NULL COMMENT '身份证件号码';

ALTER TABLE tab_contract_worker ADD `identityCardType` smallint(6) DEFAULT '1' COMMENT '身份证件类型：1-身份证  2-护照  3-台胞证  4-军官证   5-其他';
ALTER TABLE `tab_contract_worker` CHANGE COLUMN `idNumber` `identityCardNumber`  varchar(18) NULL DEFAULT NULL COMMENT '身份证件号码';

ALTER TABLE tab_contract_produce ADD `identityCardType` smallint(6) DEFAULT '1' COMMENT '身份证件类型：1-身份证  2-护照  3-台胞证  4-军官证   5-其他';
ALTER TABLE `tab_contract_produce` CHANGE COLUMN `idNumber` `identityCardNumber`  varchar(18) NULL DEFAULT NULL COMMENT '身份证件号码';


-- 修改费用进度url
UPDATE tab_sys_authority set authUrl='/financeStatisticManager/toFinanceStatisticPage' where authUrl='/financeAccountManager/accountForm';