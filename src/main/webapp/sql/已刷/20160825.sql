ALTER TABLE `tab_payment_info` DROP COLUMN `voucherFlag`;
ALTER TABLE `tab_collection_info` DROP COLUMN `voucherFlag`;
ALTER TABLE `tab_payment_info` MODIFY COLUMN `receiptNo`  varchar(20) NULL DEFAULT NULL COMMENT '票据编号';
ALTER TABLE `tab_payment_loan_map` MODIFY COLUMN `loanId`  varchar(32)  NULL DEFAULT NULL COMMENT '借款单ID';

ALTER TABLE tab_payment_finanSubj_map ADD `financeSubjName` varchar(32) DEFAULT NULL COMMENT '财务科目名称';
alter TABLE tab_loan_info ADD `financeSubjName` varchar(32) DEFAULT NULL COMMENT '财务科目名称';