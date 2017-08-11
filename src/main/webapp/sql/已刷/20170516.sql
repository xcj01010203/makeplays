-- 单据信息表
DROP TABLE IF EXISTS tab_receipt_info;
CREATE TABLE tab_receipt_info (
    id VARCHAR(32) PRIMARY KEY,
    crewId VARCHAR(32) NOT NULL COMMENT '剧组ID',
    createUserId VARCHAR(32) NOT NULL COMMENT '创建人ID',
    type SMALLINT NOT NULL COMMENT '单据类型，1-借款  2-报销  3-预算',
    receiptNo VARCHAR(50) NOT NULL COMMENT '单据编号',
    status  SMALLINT NOT NULL COMMENT '单据状态，1-草稿  2-审核中  3-被拒绝  4-完结',
    money DOUBLE NOT NULL COMMENT '金额',
    currencyId VARCHAR(32) NOT NULL COMMENT '币种ID',
    description VARCHAR(500) NOT NULL COMMENT '说明',
    attpackId VARCHAR(500) NOT NULL COMMENT '附件包ID',
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    submitTime TIMESTAMP NULL COMMENT '提交时间',
    doneUserId VARCHAR(32) COMMENT '完结人ID',
    KEY `idx_receipt_crewId` (`crewId`)
) COMMENT '单据信息表';


-- 审批信息表
DROP table IF EXISTS tab_approval_info;
CREATE TABLE tab_approval_info (
    id VARCHAR(32) PRIMARY KEY,
    crewId VARCHAR(32) NOT NULL COMMENT '剧组ID',
    receiptId VARCHAR(32) NOT NULL COMMENT '单据ID',
    approverId VARCHAR(32) NOT NULL COMMENT '审批人ID',
    sequence SMALLINT NOT NULL DEFAULT 1 COMMENT '审批序列',
    resultType SMALLINT NOT NULL COMMENT '审批结果类型，1-审核中 2-不同意 3-同意 4-退回 5-已删除',
    COMMENT VARCHAR(500) NULL COMMENT '审批意见',
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    approvalTime TIMESTAMP null COMMENT '审核时间',
    KEY idx_approval_crewId(crewId)
) COMMENT '审批信息表';

-- 消息表字段重命名
ALTER TABLE `tab_message_info` CHANGE COLUMN `readStatus` `isNew`  smallint(2) NULL DEFAULT 1 COMMENT '是否是新消息' ;
update tab_message_info SET isNew = 2 where isNew = 1;
update tab_message_info SET isNew = 1 where isNew = 0;
update tab_message_info SET isNew = 0 where isNew = 2;

-- 消息表类型改动
UPDATE tab_attachment_info SET type = 99 where type = 5;