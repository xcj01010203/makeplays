
CREATE  TABLE `tab_contract_topaid` (
  `id` varchar(32) NOT NULL COMMENT '主键',

	`crewId` varchar(32) NOT NULL COMMENT '剧组id',

  `paiddate` date DEFAULT NULL COMMENT '待付款日期',
  
	`contractId` varchar(32) NOT NULL COMMENT '合同id',
	
	`contractNo` varchar(32) NOT NULL COMMENT '合同编号',
	
  `summary` varchar(100) DEFAULT NULL COMMENT '摘要',

  `money` DOUBLE DEFAULT 0.00 COMMENT '待付款金额',

  `currencyId` varchar(32) not NULL COMMENT '币种id',

  `financeSubjName` varchar(100) DEFAULT NULL COMMENT '财务科目名称',
  
  `subjectId` varchar(32) DEFAULT NULL COMMENT '财务科目id',

  `status` SMALLINT(4) DEFAULT 0 COMMENT '待付状态  0：未付  1：待付  2 ：已付  3:已结算',
	
  `paymentId` varchar(32) DEFAULT NULL COMMENT '付款单号',
	
	`contactname` varchar(32) DEFAULT NULL COMMENT '合同人',

  `contacttype` SMALLINT(4) DEFAULT NULL COMMENT '合同类型  0：演员合同  1：职员合同   2：制作合同',

  `createtime` TIMESTAMP DEFAULT now() COMMENT '创建时间',

   `updatetime` TIMESTAMP DEFAULT now() COMMENT '最后修改时间',
	
   `roleName` varchar(50) COMMENT '职务/角色/负责人',

  PRIMARY KEY (`id`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='合同待付列表';



