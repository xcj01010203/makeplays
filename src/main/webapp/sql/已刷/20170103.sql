alter table tab_view_temp add column specialProps varchar(200) COMMENT '特殊道具';
alter table tab_view_temp add column shootTime varchar(10) COMMENT '拍摄时间';
alter table tab_view_temp add column commercialImplants varchar(100) COMMENT '商值';

ALTER TABLE tab_notice_role_time MODIFY COLUMN giveMakeupTime varchar(50);
ALTER TABLE tab_notice_role_time MODIFY COLUMN arriveTime varchar(50);
alter table tab_inhotel_info add column inTimes varchar(10) COMMENT '入住天数';





create table tab_inhotelcost_temp(
	showdate date COMMENT '日期',
	
	contactid varchar(32) COMMENT '联系人id',

	hotelname varchar(255) COMMENT '宾馆名称',

	roomnumber varchar(20) COMMENT '房间号',

	price double COMMENT '房价',

	contactname varchar(255) COMMENT '入住人姓名',

	crewid varchar(32) COMMENT '剧组id'
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='计算入住费用零时表';



-- 将入住信息  根据入住日期和退房日期将一条数据拆分成多条数据
create PROCEDURE onetomanybydateforinhotelcost(in increwid VARCHAR(32))
BEGIN
    DECLARE  c_contactId varchar(32);-- 联系人id
    DECLARE  c_hotelName  varchar(255); -- 宾馆名称
	DECLARE  c_roomNumber  varchar(100); -- 房号
	DECLARE  c_price  DOUBLE;-- 房价
	DECLARE  c_contactName  varchar(100);-- 入住人名称
	DECLARE  c_crewId  varchar(32); -- 剧组id
	declare  c_checkindate date;-- 入住日期
	declare  c_checkoutdate date;-- 退房日期
	declare  nowdate date;  -- 当前显示日期

-- 遍历数据结束标志
    DECLARE done INT DEFAULT FALSE;
    -- 游标
    DECLARE cur_account CURSOR FOR SELECT
								   tii.contactId,tii.hotelName,tii.roomNumber,tii.price,tcc.contactName,tcc.crewId,tii.checkInDate,tii.checkoutDate
								FROM
								   tab_inhotel_info tii
								LEFT JOIN tab_crew_contact tcc ON tii.contactId = tcc.contactId
								WHERE
								   tcc.crewId = increwid;
   -- 将结束标志绑定到游标
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
	-- 根据剧组id删除入住信息临时表数据
    delete from tab_inhotelcost_temp where crewid = increwid;
    -- 打开游标
    OPEN  cur_account;     
    -- 遍历
    read_loop: LOOP
            -- 取值 取多个字段
            FETCH  NEXT from cur_account INTO c_contactId,c_hotelName,c_roomNumber,c_price,c_contactName,c_crewId,c_checkindate,c_checkoutdate;
            IF done THEN
                LEAVE read_loop;
             END IF;
				
				set nowdate = c_checkindate;
				while nowdate < c_checkoutdate do 
					insert into tab_inhotelcost_temp values (nowdate,c_contactId,c_hotelName,c_roomNumber,c_price,c_contactName,c_crewId);
					
					set nowdate = date_sub(nowdate,interval - 1 day);
				end while;

       
    END LOOP;
		CLOSE cur_account;
END