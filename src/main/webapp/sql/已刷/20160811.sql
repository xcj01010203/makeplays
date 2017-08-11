-- 财务科目信息相关改动
ALTER TABLE `tab_finance_budget_account` CHANGE COLUMN `accountId` `id` varchar(32) NOT NULL COMMENT '财务预算信息ID';
ALTER TABLE tab_finance_budget_account RENAME TO tab_finance_subject;
ALTER TABLE `tab_finance_subject` COMMENT='财务科目信息表';
ALTER TABLE `tab_finance_subject` CHANGE COLUMN `accountName` `name` VARCHAR (50) NULL DEFAULT NULL COMMENT '模板名称',
 CHANGE COLUMN `accountLevel` `level` SMALLINT (6) NULL DEFAULT NULL COMMENT '预算科目级别。1、2、3、4级';


-- 财务科目模板信息相关改动
ALTER TABLE tab_finance_account_template RENAME TO tab_finance_subject_template;
ALTER TABLE `tab_finance_subject_template`
CHANGE COLUMN `templateId` `id`  varchar(32) NOT NULL COMMENT 'id' ,
CHANGE COLUMN `templateName` `name`  varchar(50) NULL DEFAULT NULL COMMENT '名称',
CHANGE COLUMN `templateLevel` `level`  smallint(6) NULL DEFAULT NULL COMMENT '级别。1、2、3、4级',
CHANGE COLUMN `templateType` `type`  smallint(6) NULL DEFAULT NULL COMMENT '类型。0：按制作周期；1：按部门';


-- 货币表相关改动
ALTER TABLE `tab_currency_info`
CHANGE COLUMN `currencyId` `id`  varchar(32) NOT NULL COMMENT '货币信息ID' ,
CHANGE COLUMN `currencyName` `name`  varchar(50) NULL DEFAULT NULL COMMENT '货币名称',
CHANGE COLUMN `currencyCode` `code`  varchar(10) NULL DEFAULT NULL COMMENT '编码';

-- 货币和财务科目关联关系表相关改动
ALTER TABLE tab_account_currency_map RENAME TO tab_finanSubj_currency_map;
ALTER TABLE `tab_finanSubj_currency_map` COMMENT = '货币和财务科目关联关系表';
ALTER TABLE `tab_finanSubj_currency_map` CHANGE COLUMN `accountId` `finanSubjId` varchar(32) NULL DEFAULT NULL COMMENT '财务科目ID';
ALTER TABLE `tab_finanSubj_currency_map` CHANGE COLUMN `finanSubjId` `financeSubjId` varchar(32) NULL DEFAULT NULL COMMENT '财务科目ID';
ALTER TABLE `tab_finanSubj_currency_map`
DROP COLUMN `unit`,
CHANGE COLUMN `price` `perPrice` double NULL DEFAULT NULL COMMENT '单价';


-- 合同相关表
ALTER TABLE `tab_contract_actor` DROP COLUMN `contractAttachment`;
ALTER TABLE `tab_contract_actor` CHANGE COLUMN `accountId` `financeSubjId` varchar(32) NULL DEFAULT NULL COMMENT '财务科目id';

ALTER TABLE `tab_contract_produce`
DROP COLUMN `contractAttachment`,
CHANGE COLUMN `unitName` `company`  varchar(50) NULL DEFAULT NULL COMMENT '公司',
CHANGE COLUMN `accountId` `financeSubjId`  varchar(32) NULL DEFAULT NULL COMMENT '财务科目id';

ALTER TABLE `tab_contract_worker`
DROP COLUMN `contractAttachment`,
CHANGE COLUMN `accountId` `financeSubjId`  varchar(32) NULL DEFAULT NULL COMMENT '财务科目id';

ALTER TABLE `tab_loan_info`
MODIFY COLUMN `paymentWay`  varchar(32) NULL DEFAULT '1' COMMENT '财务付款方式,1：现金 ；2：现金(网转)；3：银行';
ALTER TABLE `tab_loan_info`
CHANGE COLUMN `accountId` `financeSubjId`  varchar(32) NULL DEFAULT NULL COMMENT '财务科目ID';
ALTER TABLE `tab_loan_info`
MODIFY COLUMN `paymentWay`  smallint(10) NULL DEFAULT '1' COMMENT '财务付款方式,1：现金 ；2：现金(网转)；3：银行';


-- 付款单和财务科目关联关系表
ALTER TABLE tab_payment_account_map RENAME TO tab_payment_finanSubj_map;
ALTER TABLE `tab_payment_finanSubj_map` CHANGE COLUMN `accountId` `finanSubjId`  varchar(32)  NULL DEFAULT NULL COMMENT '财务科目ID';
ALTER TABLE `tab_payment_finanSubj_map` CHANGE COLUMN `finanSubjId` `financeSubjId`  varchar(32)  NULL DEFAULT NULL COMMENT '财务科目ID';



















