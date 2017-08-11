
CREATE TABLE tab_inhotel_info (
  inhotelId varchar(32) NOT NULL COMMENT '主键',
  contactId varchar(32) NOT NULL COMMENT '联系人id',
  hotelName varchar(255) DEFAULT NULL COMMENT '宾馆名称',
  roomNumber varchar(20) DEFAULT NULL COMMENT '房间号',
  extension varchar(20) DEFAULT NULL COMMENT '分机号',
  checkInDate date DEFAULT NULL COMMENT '入住日期',
  checkoutDate date DEFAULT NULL COMMENT '退房日期',
  mealType smallint(6) DEFAULT NULL COMMENT '餐别。1：常规；2：回民;3: 素餐 ;4:特餐;',
  createTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  price double DEFAULT NULL COMMENT '人均单价',
  PRIMARY KEY (`inhotelId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='人员入住信息';

