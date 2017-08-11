ALTER table tab_contract_month_pay_detail add payWayType INT DEFAULT 2 COMMENT '支付方式，2-按月支付  3-按日支付';
ALTER TABLE `tab_contract_month_pay_detail` COMMENT='合同按月支付薪酬明细，按日支付记录也记录进此表';

ALTER table tab_contract_month_pay_way add payWayType INT DEFAULT 2 COMMENT '支付方式，2-按月支付  3-按日支付';
ALTER TABLE `tab_contract_month_pay_way` COMMENT='合同按月支付表，按日支付记录也记录进此表';