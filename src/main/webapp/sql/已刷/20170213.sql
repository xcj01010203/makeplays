-- 剧本进度 tab_prepare_script
-- ============================================剧本筹备进度===============6张表=============================
DROP TABLE IF EXISTS tab_prepare_script_type;
create table tab_prepare_script_type (
    id varchar(32) PRIMARY KEY COMMENT '主键',
    name varchar(100) COMMENT '类型名称',
    orderNumber SMALLINT COMMENT '排序编号'

)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='筹备进度-剧本类型';

insert into tab_prepare_script_type (id,name,ordernumber) values(1,'剧情梗概',1);
insert into tab_prepare_script_type (id,name,ordernumber) values(2,'分场剧本',2);
insert into tab_prepare_script_type (id,name,ordernumber) values(3,'台词剧本',3);
insert into tab_prepare_script_type (id,name,ordernumber) values(4,'分集/导演台本',4);

DROP TABLE IF EXISTS tab_prepare_script_type_checked;
create table tab_prepare_script_type_checked(
    id varchar(32) PRIMARY KEY COMMENT '主键',
    scriptTypeId varchar(32) COMMENT '剧本类型id',
    crewId varchar(32) COMMENT '剧组id',
    KEY `crewId` (`crewId`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='筹备进度-选中剧本类型';

DROP TABLE IF EXISTS tab_prepare_script_reviewweight;
create table tab_prepare_script_reviewweight (
    id varchar(32) PRIMARY KEY COMMENT '主键',
    name varchar(100) COMMENT '评审人姓名',
    weight double COMMENT '权重',
    crewId varchar(32) COMMENT '剧组id',
    orderNumber SMALLINT COMMENT '排序编号',
    KEY `crewId` (`crewId`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='筹备进度-剧本评审权重';

DROP TABLE IF EXISTS tab_prepare_script_score;
create table tab_prepare_script_score (
    id varchar(32) PRIMARY KEY COMMENT '主键',
    scriptId varchar(32) COMMENT '剧本评审进度id',
    reviewWeightId varchar(32) COMMENT '剧本权重id',
    score double COMMENT '评分',
    crewId varchar(32) COMMENT '剧组ID',
    KEY `crewId` (`crewId`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='筹备进度-剧本评审分数';

DROP TABLE IF EXISTS tab_prepare_script;
create table tab_prepare_script(
    id varchar(32) PRIMARY KEY COMMENT '主键',
    scriptTypeId varchar(32) COMMENT '剧本类型id',
    edition varchar(20) COMMENT '版本',
    finishDate date COMMENT '交稿日期',
    personLiable varchar(100) COMMENT '负责人',
    content varchar(255) COMMENT '内容',
    status varchar(20) COMMENT '状态',
    mark varchar(255) COMMENT '备注',
    crewId varchar(32) COMMENT '剧组id',
    parentId varchar(32) COMMENT '父id',
    createTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY `crewId` (`crewId`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='筹备进度-剧本进度信息';

-- ============================================剧本筹备进度============================================



-- 选角进度 tab_prepare_role    树表结构
DROP TABLE IF EXISTS tab_prepare_role;
create table tab_prepare_role(
    id varchar(32) PRIMARY KEY COMMENT '主键',
    role varchar(100) COMMENT '角色名称',
    actor varchar(100) comment '备选演员',
    schedule varchar(50) COMMENT '沟通进度',
    content varchar(255) COMMENT '沟通内容',
    mark varchar(255) COMMENT '备注',
    parentId varchar(32) COMMENT '父id',
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    crewId varchar(32) COMMENT '剧组id',
    KEY `crewId` (`crewId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='筹备进度-选角进度';
-- 剧组人员 tab_prepare_crewpeople  树表结构
DROP TABLE IF EXISTS tab_prepare_crewpeople;
create table tab_prepare_crewpeople(
    id varchar(32) PRIMARY KEY COMMENT '主键',
    groupName varchar(100) COMMENT '组别',
    duties varchar(100) comment '职务',
    name varchar(50) COMMENT '姓名',
    phone varchar(255) COMMENT '电话',
    reviewer varchar(255) COMMENT '审核人',
    confirmDate date COMMENT '确认时间',
    arrivalTime date COMMENT '到岗时间',
    payment double COMMENT '酬金',
    createTime TIMESTAMP COMMENT '创建时间',
    parentId varchar(32) COMMENT '父id',
    crewId varchar(32) COMMENT '剧组id',
    KEY `crewId` (`crewId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='筹备进度-剧组人员';
-- 勘景进度                          直接读勘景表

-- 美术视觉 tab_prepare_arteffect_role   tab_prepare_arteffect_location   单表结构
DROP TABLE IF EXISTS tab_prepare_arteffect_role;
create table tab_prepare_arteffect_role(
    id varchar(32) PRIMARY KEY COMMENT '主键',
    role varchar(100) COMMENT '角色',
    modelling varchar(255) COMMENT '造型',
    confirmDate date COMMENT '确定日期',
    status varchar(100) comment '状态',
    mark varchar(255) COMMENT '备注',
    reviewer varchar(100) COMMENT '审核人',
    createTime TIMESTAMP COMMENT '创建日期',
    crewId varchar(32) COMMENT '剧组id',
    KEY `crewId` (`crewId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='筹备进度-美术视觉-角色';
DROP TABLE IF EXISTS tab_prepare_arteffect_location;
create table tab_prepare_arteffect_location(
    id varchar(32) PRIMARY KEY COMMENT '主键',
    location varchar(100) COMMENT '场景',
    designSketch varchar(255) COMMENT '效果图',
    designSketchDate date COMMENT '出效果图日期',
    workDraw varchar(255) COMMENT '施工图',
    workDrawDate date COMMENT '出施工图时间',
    scenery varchar(50) COMMENT '置景',
    sceneryDate date COMMENT '置景时间',
    reviewer varchar(100) COMMENT '审核人',
    opinion varchar(255) COMMENT '意见',
    createTime TIMESTAMP COMMENT '创建时间',
    crewId varchar(32) COMMENT '剧组id',
    KEY `crewId` (`crewId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='筹备进度-美术视觉-场景';
-- 宣传进度 tab_prepare_extension   单表结构
DROP TABLE IF EXISTS tab_prepare_extension;
create table tab_prepare_extension(
    id varchar(32) PRIMARY KEY COMMENT '主键',
    type varchar(255) COMMENT '类型',
    material varchar(255) COMMENT '素材',
    personLiable varchar(100) COMMENT '责任人',
    reviewer varchar(100) COMMENT '审核人',
    createTime Timestamp COMMENT '创建时间',
    crewId varchar(32) COMMENT '剧组id',
    KEY `crewId` (`crewId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='筹备进度-宣传进度';
-- 办公筹备 tab_prepare_work    树表结构
DROP TABLE IF EXISTS tab_prepare_work;
create table tab_prepare_work(
    id varchar(32) PRIMARY KEY COMMENT '主键',
    type varchar(100) COMMENT '类型',
    purpose varchar(100) COMMENT '用途  工作',
    schedule varchar(100) COMMENT '进度',
    personLiable varchar(100) COMMENT '负责人',
    parentId varchar(32) COMMENT '父id',
    createTime Timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    crewId varchar(32) COMMENT '剧组id',
    KEY `crewId` (`crewId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='筹备进度-办公筹备';
-- 商务运营 tab_prepare_operate     树表结构
DROP TABLE IF EXISTS tab_prepare_operate;
create table tab_prepare_operate(
    id varchar(32) PRIMARY KEY COMMENT '主键',
    operateType varchar(100) COMMENT '合作种类',
    operateBrand varchar(100) COMMENT '品牌',
    operateMode varchar(100) COMMENT '合作方式',
    operateCost Double COMMENT '合作费用',
    contactName varchar(100) COMMENT '联系人名称',
    phoneNumber varchar(100) COMMENT '联系电话',
    mark varchar(255) COMMENT '备注',
    personLiable varchar(100) COMMENT '负责人',
    parentId varchar(32) COMMENT '父id',
    createTime Timestamp COMMENT '创建时间',
    crewId varchar(32) COMMENT '剧组id',
    KEY `crewId` (`crewId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='筹备进度-商务运营';