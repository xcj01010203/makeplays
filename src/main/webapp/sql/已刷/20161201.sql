-- 新建按月支付表
DROP TABLE if EXISTS tab_contract_month_pay_way;
CREATE TABLE tab_contract_month_pay_way (
    id VARCHAR(32) NOT NULL PRIMARY KEY,
    crewId VARCHAR(32) NOT NULL COMMENT '剧组ID',
    contractId VARCHAR(32) NOT NULL COMMENT '合同Id',
    monthMoney DOUBLE DEFAULT 0.0 COMMENT '月薪',
    startDate date DEFAULT NULL COMMENT '付款开始日期',
    endDate date DEFAULT NULL COMMENT '付款结束日期',
    monthPayDay INT DEFAULT 1 COMMENT '每月发薪日',
    remark VARCHAR(100) NULL COMMENT '备注',
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期'
) COMMENT '合同按月支付表';

DROP TABLE if EXISTS tab_contract_month_pay_detail;
CREATE TABLE tab_contract_month_pay_detail(
    id VARCHAR(32) NOT NULL primary KEY,
    crewId VARCHAR(32) NOT NULL COMMENT '剧组Id',
    contractId VARCHAR(32) NOT NULL COMMENT '合同Id',
    month date NOT NULL COMMENT '支付的薪酬月份，格式yyyy-MM',
    startDate date NOT NULL COMMENT '计算薪酬开始日期',
    endDate date NOT NULL COMMENT '计算薪酬结束日期',
    money DOUBLE NOT NULL DEFAULT 0.0 COMMENT '薪酬',
    payDate date NOT NULL COMMENT '付款日期',
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期'
) COMMENT '合同按月支付薪酬明细';


-- 把数据库中所有按月支付的信息全部改成按照阶段支付
update tab_contract_actor set payWay=1 where payWay=2;
update tab_contract_worker set payWay=1 where payWay=2;
update tab_contract_produce set payWay=1 where payWay=2;

-- 修改支付方式表为按阶段支付表
ALTER TABLE `tab_contract_pay_way`
DROP COLUMN `monthNum`,
CHANGE COLUMN `wayId` `id` varchar(32) NOT NULL COMMENT '支付方式ID' FIRST ,
MODIFY COLUMN `crewId`  varchar(32) NULL DEFAULT NULL COMMENT '剧组ID' AFTER `id`,
MODIFY COLUMN `contractId`  varchar(32) NOT NULL COMMENT '合同ID' AFTER `crewId`,
MODIFY COLUMN `paymentMoney`  double NULL DEFAULT 0.0 COMMENT '支付金额' AFTER `contractId`,
MODIFY COLUMN `paymentRate`  double NULL DEFAULT 0.0 COMMENT '支付比例：小数' AFTER `paymentMoney`,
CHANGE COLUMN `remingTime` `remindTime`  date NULL DEFAULT NULL COMMENT '支付提醒时间' AFTER `paymentRate`,
MODIFY COLUMN `payStage`  smallint(6) NULL DEFAULT NULL COMMENT '支付阶段（按阶段支付时填写）' AFTER `remindTime`,
CHANGE COLUMN `paymentTerm` `remark`  varchar(200) NULL DEFAULT NULL COMMENT '支付条件' AFTER `payStage`;

ALTER TABLE tab_contract_pay_way RENAME TO tab_contract_stage_pay_way;

ALTER TABLE `tab_contract_stage_pay_way`
CHANGE COLUMN `paymentMoney` `money`  double NULL DEFAULT 0 COMMENT '支付金额' AFTER `contractId`,
CHANGE COLUMN `paymentRate` `rate`  double NULL DEFAULT 0 COMMENT '支付比例：小数' AFTER `money`,
CHANGE COLUMN `payStage` `stage`  smallint(6) NULL DEFAULT NULL COMMENT '支付阶段（按阶段支付时填写）' AFTER `remindTime`;
