ALTER TABLE tab_contract_actor ADD COLUMN customContractNo VARCHAR(32) DEFAULT '' COMMENT '用户自定义合同编号';
ALTER TABLE tab_contract_produce ADD COLUMN customContractNo VARCHAR(32) DEFAULT '' COMMENT '用户自定义合同编号';
ALTER TABLE tab_contract_worker ADD COLUMN customContractNo VARCHAR(32) DEFAULT '' COMMENT '用户自定义合同编号';