DROP TABLE IF EXISTS `tab_hotel_info`;
CREATE TABLE `tab_hotel_info` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `hotelName` varchar(100) DEFAULT NULL COMMENT '宾馆名称',
  `hotelAddress` varchar(500) DEFAULT NULL COMMENT '宾馆详细地址',
  `longitude` varchar(50) DEFAULT NULL COMMENT '经度',
  `latitude` varchar(50) DEFAULT NULL COMMENT '维度',
  `hotelPhone` varchar(50) DEFAULT NULL COMMENT '酒店电话',
  `roomNumber` int(11) DEFAULT NULL COMMENT '房间数',
  `contactPeople` varchar(20) DEFAULT NULL COMMENT '联系人姓名',
  `contactPhone` varchar(50) DEFAULT NULL COMMENT '联系人电话',
  `priceRemark` varchar(500) DEFAULT NULL COMMENT '报价说明',
  `createTime` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `crewId` varchar(32) DEFAULT '' COMMENT '剧组id',
  PRIMARY KEY (`id`),
  KEY `crewId` (`crewId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='宾馆详细信息表';

DROP TABLE IF EXISTS `tab_checkIn_hotel_info`;
CREATE TABLE `tab_checkIn_hotel_info` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `peopleName` varchar(15) DEFAULT NULL COMMENT '入住人姓名',
  `roomNo` varchar(20) DEFAULT NULL COMMENT '房间号',
  `extension` varchar(20) DEFAULT NULL COMMENT '分机号',
  `roomPrice` double DEFAULT NULL COMMENT '房价',
  `checkInDate` date DEFAULT NULL COMMENT '入住日期',
  `checkoutDate` date DEFAULT NULL COMMENT '退房日期',
  `inTimes` varchar(10) DEFAULT NULL COMMENT '入住天数',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `hotelId` varchar(32) DEFAULT '' COMMENT '宾馆id',
  `crewId` varchar(32) DEFAULT '' COMMENT '剧组id',
  PRIMARY KEY (`id`),
  KEY `hotelId` (`hotelId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='宾馆入住信息表';

ALTER TABLE tab_inhotelcost_temp DROP COLUMN contactid;

drop procedure onetomanybydateforinhotelcost;
CREATE PROCEDURE `onetomanybydateforinhotelcost`(in increwid VARCHAR(32))
BEGIN
    DECLARE  c_hotelName  varchar(255);
		DECLARE  c_roomNo  varchar(100);
		DECLARE  c_roomPrice  DOUBLE;
		DECLARE  c_peopleName  varchar(100);
		DECLARE  c_crewId  varchar(32);
		declare  c_checkindate date;
		declare  c_checkoutdate date;
		declare  nowdate date;

-- 遍历数据结束标志
    DECLARE done INT DEFAULT FALSE;
    -- 游标
    DECLARE cur_account CURSOR FOR SELECT
   thi.hotelName,tchi.roomNo,tchi.roomPrice,tchi.peopleName,tchi.crewId,tchi.checkInDate,tchi.checkoutDate
FROM
   tab_hotel_info thi
LEFT JOIN tab_checkIn_hotel_info tchi ON thi.id=tchi.hotelId
WHERE
  thi.crewId = increwid;

    -- 将结束标志绑定到游标
   -- 将结束标志绑定到游标
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
		delete from tab_inhotelcost_temp where crewid = increwid;
    -- 打开游标
    OPEN  cur_account;     
    -- 遍历
    read_loop: LOOP
            -- 取值 取多个字段
            FETCH  NEXT from cur_account INTO c_hotelName,c_roomNo,c_roomPrice,c_peopleName,c_crewId,c_checkindate,c_checkoutdate;
            IF done THEN
                LEAVE read_loop;
             END IF;
				
				set nowdate = c_checkindate;
				while nowdate <= c_checkoutdate do 
					insert into tab_inhotelcost_temp values (nowdate,c_hotelName,c_roomNo,c_roomPrice,c_peopleName,c_crewId);
					
					set nowdate = date_sub(nowdate,interval - 1 day);
				end while;

       
    END LOOP;
		CLOSE cur_account;
END

