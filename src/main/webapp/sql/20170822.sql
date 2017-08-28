
-- 顺序执行

-- 1.增加人工费用 

update tab_cater_money_info set caterType='人工费用' where caterMoneyId='7';

insert into tab_cater_money_info 
VALUES('8','',null,null,'其它','1',null,'','0');

-- 2.添加用餐时间和用餐地点列
alter table  tab_cater_money_info  add caterTimeType varchar(50) COMMENT '用餐时间';

alter table tab_cater_money_info add caterAddr varchar(50) COMMENT '用餐地点';

-- crewid 剧组id为1设置为用餐时间常量
insert into tab_cater_money_info values('20','',null,null,null,null,null,null,'00','早餐',null)
,('21','',null,null,null,null,null,null,'00','午餐',null),('22','',null,null,null,null,null,null,'00','晚餐',null)
,('23','',null,null,null,null,null,null,'00','夜宵',null),('24','',null,null,null,null,null,null,'00','全天',null)
,('25','',null,null,null,null,null,null,'00','其它',null);
-- crewid 剧组id设置为2时设置用餐地点常量

insert into tab_cater_money_info values('30','',null,null,null,null,null,null,'000','','A组')
,('31','',null,null,null,null,null,null,'000','','B组'),('32','',null,null,null,null,null,null,'000','','宾馆')
,('33','',null,null,null,null,null,null,'000','','其它'),('34','',null,null,null,null,null,null,'000','','所有');

