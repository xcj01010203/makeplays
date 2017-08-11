

create table tab_sceneview_info (

	id varchar(32) not null comment '主键',

	vName varchar(50) comment '实景名称',

	vCity varchar(50) comment '所在城市',

	vAddress varchar(255) comment '详细地址',

	vLongitude varchar(50) comment '详细地址经度',

	vLatitude varchar(50) comment '详细地址纬度',

	distanceToHotel varchar(50) comment '距离住宿地距离',

	holePeoples   int    comment '容纳人数',

	deviceSpace   varchar(100) comment '设备空间',

	isModifyView   SMALLINT   comment '是否改景   0：是   1： 否',

	modifyViewCost double	comment '改景费用',

	modifyViewTime  varchar(50) comment '改景耗时',

	hasProp   SMALLINT comment '是否有道具陈设  0：是   1： 否',

	propCost  double     comment  '道具陈设费用',

	propTime  varchar(50) comment '道具陈设时间',

	enterViewDate date  comment '进景时间',

	leaveViewDate date comment '离景时间',

	viewUseTime  varchar(50)  comment '使用时间',

	contactNo  varchar(50) comment '联系方式',

	contactName varchar(50) comment '联系人姓名',

	contactRole varchar(50)  comment  '联系人职务',

	viewPrice   DOUBLE   comment  '场景价格',

	freeStartDate DATE   comment  '空档期开始时间',

	freeEndDate  date  comment  '空档期结束时间',

	other  varchar(255) comment '自定义字段',

	remark  varchar(500) comment  '备注',

	crewId  varchar(32) comment '剧组id',

	orderNumber SMALLINT not null comment  '排序标号',

	PRIMARY KEY (id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='剧组实景信息表';







create table tab_sceneview_viewinfo_map(
	
	id varchar(32) not null comment '主键',

	sceneviewId varchar(32) not null comment '实景表id',

	locationId varchar(32) not null comment '主场景id',

	orderNumber SMALLINT not null comment  '排序标号',

	playTime date comment '设置拍摄时间',

	PRIMARY key (id) 

)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='剧组实景信息表与场景对照表';



