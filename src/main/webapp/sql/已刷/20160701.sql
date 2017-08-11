-- -----------------------------徐长建脚本start--------------------------------------------------
DROP TABLE IF EXISTS tab_account_subject;
create TABLE tab_account_subject(
    id VARCHAR(32) NOT NULL PRIMARY KEY,
    crewId VARCHAR(32) NOT NULL COMMENT '剧组ID',
    name VARCHAR(100) NOT NULL COMMENT '会计科目名称',
    code varchar(100) DEFAULT NULL COMMENT '会计科目代码',
    sequence INT COMMENT '排列序号'
) COMMENT '会计科目表';

DROP TABLE IF EXISTS tab_account_finance_subject_map;
CREATE TABLE tab_account_finance_subject_map(
    id VARCHAR(32) NOT NULL PRIMARY KEY,
    crewId VARCHAR(32) NOT NULL COMMENT '剧组ID',
    accountSubjId VARCHAR(32) NOT NULL COMMENT '会计科目ID',
    financeSubjId VARCHAR(32) NOT NULL COMMENT '财务科目ID'
) COMMENT '会计科目和预算科目关联关系表';
-- -----------------------------徐长建脚本end--------------------------------------------------