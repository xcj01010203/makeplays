-- 0701
DROP TABLE IF EXISTS tab_account_subject;
create TABLE tab_account_subject(
    id VARCHAR(32) NOT NULL PRIMARY KEY,
    crewId VARCHAR(32) NOT NULL COMMENT '剧组ID',
    name VARCHAR(100) NOT NULL COMMENT '会计科目名称',
    code varchar(100) DEFAULT NULL COMMENT '会计科目代码',
    sequence INT COMMENT '排列序号'
) COMMENT '会计科目表';

DROP TABLE IF EXISTS tab_account_finance_subject_map;
CREATE TABLE tab_account_finance_subject_map(
    id VARCHAR(32) NOT NULL PRIMARY KEY,
    crewId VARCHAR(32) NOT NULL COMMENT '剧组ID',
    accountSubjId VARCHAR(32) NOT NULL COMMENT '会计科目ID',
    financeSubjId VARCHAR(32) NOT NULL COMMENT '财务科目ID'
) COMMENT '会计科目和预算科目关联关系表';


-- 0727
ALTER TABLE tab_actor_attendance RENAME TO tab_actor_leave_record;

ALTER TABLE tab_actor_leave_record COMMENT '演员请假记录表';

ALTER TABLE `tab_actor_leave_record` CHANGE COLUMN `attendanceId` `id`  varchar(32) NOT NULL  COMMENT '' ;

ALTER TABLE tab_actor_leave_record ADD createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';

ALTER TABLE tab_evtag_info DROP COLUMN userName;

-- 0811
-- 财务科目信息相关改动
ALTER TABLE `tab_finance_budget_account` CHANGE COLUMN `accountId` `id` varchar(32) NOT NULL COMMENT '财务预算信息ID';
ALTER TABLE tab_finance_budget_account RENAME TO tab_finance_subject;
ALTER TABLE `tab_finance_subject` COMMENT='财务科目信息表';
ALTER TABLE `tab_finance_subject` CHANGE COLUMN `accountName` `name` VARCHAR (50) NULL DEFAULT NULL COMMENT '模板名称',
 CHANGE COLUMN `accountLevel` `level` SMALLINT (6) NULL DEFAULT NULL COMMENT '预算科目级别。1、2、3、4级';


-- 财务科目模板信息相关改动
ALTER TABLE tab_finance_account_template RENAME TO tab_finance_subject_template;
ALTER TABLE `tab_finance_subject_template`
CHANGE COLUMN `templateId` `id`  varchar(32) NOT NULL COMMENT 'id' ,
CHANGE COLUMN `templateName` `name`  varchar(50) NULL DEFAULT NULL COMMENT '名称',
CHANGE COLUMN `templateLevel` `level`  smallint(6) NULL DEFAULT NULL COMMENT '级别。1、2、3、4级',
CHANGE COLUMN `templateType` `type`  smallint(6) NULL DEFAULT NULL COMMENT '类型。0：按制作周期；1：按部门';


-- 货币表相关改动
ALTER TABLE `tab_currency_info`
CHANGE COLUMN `currencyId` `id`  varchar(32) NOT NULL COMMENT '货币信息ID' ,
CHANGE COLUMN `currencyName` `name`  varchar(50) NULL DEFAULT NULL COMMENT '货币名称',
CHANGE COLUMN `currencyCode` `code`  varchar(10) NULL DEFAULT NULL COMMENT '编码';

-- 货币和财务科目关联关系表相关改动
ALTER TABLE tab_account_currency_map RENAME TO tab_finanSubj_currency_map;
ALTER TABLE `tab_finanSubj_currency_map` COMMENT = '货币和财务科目关联关系表';
ALTER TABLE `tab_finanSubj_currency_map` CHANGE COLUMN `accountId` `finanSubjId` varchar(32) NULL DEFAULT NULL COMMENT '财务科目ID';
ALTER TABLE `tab_finanSubj_currency_map` CHANGE COLUMN `finanSubjId` `financeSubjId` varchar(32) NULL DEFAULT NULL COMMENT '财务科目ID';
ALTER TABLE `tab_finanSubj_currency_map`
DROP COLUMN `unit`,
CHANGE COLUMN `price` `perPrice` double NULL DEFAULT NULL COMMENT '单价';


-- 合同相关表
ALTER TABLE `tab_contract_actor` DROP COLUMN `contractAttachment`;
ALTER TABLE `tab_contract_actor` CHANGE COLUMN `accountId` `financeSubjId` varchar(32) NULL DEFAULT NULL COMMENT '财务科目id';

ALTER TABLE `tab_contract_produce`
DROP COLUMN `contractAttachment`,
CHANGE COLUMN `unitName` `company`  varchar(50) NULL DEFAULT NULL COMMENT '公司',
CHANGE COLUMN `accountId` `financeSubjId`  varchar(32) NULL DEFAULT NULL COMMENT '财务科目id';

ALTER TABLE `tab_contract_worker`
DROP COLUMN `contractAttachment`,
CHANGE COLUMN `accountId` `financeSubjId`  varchar(32) NULL DEFAULT NULL COMMENT '财务科目id';

ALTER TABLE `tab_loan_info`
MODIFY COLUMN `paymentWay`  varchar(32) NULL DEFAULT '1' COMMENT '财务付款方式,1：现金 ；2：现金(网转)；3：银行';
ALTER TABLE `tab_loan_info`
CHANGE COLUMN `accountId` `financeSubjId`  varchar(32) NULL DEFAULT NULL COMMENT '财务科目ID';
ALTER TABLE `tab_loan_info`
MODIFY COLUMN `paymentWay`  smallint(10) NULL DEFAULT '1' COMMENT '财务付款方式,1：现金 ；2：现金(网转)；3：银行';


-- 付款单和财务科目关联关系表
ALTER TABLE tab_payment_account_map RENAME TO tab_payment_finanSubj_map;
ALTER TABLE `tab_payment_finanSubj_map` CHANGE COLUMN `accountId` `finanSubjId`  varchar(32)  NULL DEFAULT NULL COMMENT '财务科目ID';
ALTER TABLE `tab_payment_finanSubj_map` CHANGE COLUMN `finanSubjId` `financeSubjId`  varchar(32)  NULL DEFAULT NULL COMMENT '财务科目ID';

-- 0815
-- 修改消息表
ALTER TABLE `tab_message_info`
DROP COLUMN `dealerId`,
DROP COLUMN `messageType`,
DROP COLUMN `invoiceId`,
DROP COLUMN `remindTime`;

ALTER TABLE `tab_message_info` DROP COLUMN STATUS;
alter TABLE tab_message_info ADD `status` smallint(6) DEFAULT '0' COMMENT '消息状态 0：未读  1：已读';

-- 修改合同表添加附件包ID
ALTER TABLE tab_contract_actor ADD `attpackId` varchar(32) DEFAULT NULL COMMENT '附件包ID';
ALTER TABLE tab_contract_worker ADD `attpackId` varchar(32) DEFAULT NULL COMMENT '附件包ID';
ALTER TABLE tab_contract_produce ADD `attpackId` varchar(32) DEFAULT NULL COMMENT '附件包ID';

ALTER TABLE tab_contract_worker ADD `financeSubjName` varchar(100) DEFAULT NULL COMMENT '财务科目名称';
ALTER TABLE tab_contract_actor ADD `financeSubjName` varchar(100) DEFAULT NULL COMMENT '财务科目名称';
ALTER TABLE tab_contract_produce ADD `financeSubjName` varchar(100) DEFAULT NULL COMMENT '财务科目名称';

ALTER TABLE `tab_contract_produce` CHANGE COLUMN `contactPhone` `phone` varchar(30) NULL DEFAULT NULL COMMENT '联系电话';

-- 0825
ALTER TABLE `tab_payment_info` DROP COLUMN `voucherFlag`;
ALTER TABLE `tab_collection_info` DROP COLUMN `voucherFlag`;
ALTER TABLE `tab_payment_info` MODIFY COLUMN `receiptNo`  varchar(20) NULL DEFAULT NULL COMMENT '票据编号';
ALTER TABLE `tab_payment_loan_map` MODIFY COLUMN `loanId`  varchar(32)  NULL DEFAULT NULL COMMENT '借款单ID';

ALTER TABLE tab_payment_finanSubj_map ADD `financeSubjName` varchar(32) DEFAULT NULL COMMENT '财务科目名称';
alter TABLE tab_loan_info ADD `financeSubjName` varchar(32) DEFAULT NULL COMMENT '财务科目名称';

-- 0901
CREATE TABLE `tab_team_info` (
  `teamId` varchar(32) NOT NULL COMMENT '组训ID',
  `createUser` varchar(32) NOT NULL COMMENT '创建者Id',
  `crewName` varchar(100) DEFAULT '' COMMENT '剧组名称',
  `crewType` smallint(6) DEFAULT NULL COMMENT '剧组类型。0：电影；1：电视剧；2：网络剧；3: 网大 ; 99：其他',
  `company` varchar(100) DEFAULT '' COMMENT '制片公司',
  `subject` varchar(100) DEFAULT '' COMMENT '题材',
  `shootlocation` varchar(100) DEFAULT '' COMMENT '拍摄地点',
  `director` varchar(100) DEFAULT '' COMMENT '导演',
  `scriptWriter` varchar(100) DEFAULT '' COMMENT '编剧',
  `shootStartDate` date DEFAULT NULL COMMENT '开机日期',
  `shootEndDate` date DEFAULT NULL COMMENT '杀青时间',
  `contactname` varchar(32) DEFAULT '' COMMENT '联系人姓名',
  `phoneNum` varchar(32) DEFAULT '' COMMENT '联系电话',
  `email` varchar(32) DEFAULT '' COMMENT '邮箱',
  `contactAddress` varchar(32) DEFAULT '' COMMENT '联系地址',
  `crewComment` varchar(300) DEFAULT '' COMMENT '剧组简介',
  `picPath` varchar(100) DEFAULT '' COMMENT '上传的宣传图片的保存地址',
  `status` smallint(6) DEFAULT '1' COMMENT '组训状态。1：可用；2：不可用;',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`teamId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='组训基本信息表';


CREATE TABLE `tab_team_position_info` (
  `positionId` varchar(32) NOT NULL COMMENT '职位ID',
  `teamId` varchar(32) NOT NULL COMMENT '组训ID',
  `createUser` varchar(32) NOT NULL COMMENT '创建者Id',
  `positionName` varchar(100) NOT NULL COMMENT '职位名称',
  `needPeopleNum` int(10) DEFAULT NULL COMMENT '招聘人数',
  `positionRequirement` varchar(200) DEFAULT '' COMMENT '职位要求',
  `status` smallint(6) DEFAULT '1' COMMENT '职位状态。1：可用；2：不可用;',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`positionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='组训中招聘职位基本信息表';


CREATE TABLE `tab_work_experience_info` (
  `experienceId` varchar(32) NOT NULL COMMENT '经历ID',
  `createUser` varchar(32) NOT NULL COMMENT '用户Id',
  `crewName` varchar(100) NOT NULL COMMENT '剧组名称',
  `positionId` varchar(50) DEFAULT NULL COMMENT '职务id',
  `positionName` varchar(100) DEFAULT '' COMMENT '职位名称',
  `joinCrewDate` date DEFAULT NULL COMMENT '入组日期',
  `leaveCrewDate` date DEFAULT NULL COMMENT '离组日期',
  `workrequirement` varchar(200) DEFAULT '' COMMENT '工作职责',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`experienceId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='工作经历基本信息表';

CREATE TABLE `tab_search_team_info` (
  `searchTeamId` varchar(32) NOT NULL COMMENT '寻组信息ID',
  `createUser` varchar(32) NOT NULL COMMENT '用户Id',
  `likePositionName` varchar(100) NOT NULL COMMENT '意向职务名称',
  `currentStartDate` date DEFAULT NULL COMMENT '个人档期开始时间',
  `currentEndDate` date DEFAULT NULL COMMENT '个人档期结束时间',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`searchTeamId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='寻组基本信息表(寻求职位基本信息表)';

CREATE TABLE `tab_store_info` (
  `storeId` varchar(32) NOT NULL COMMENT '收藏id',
  `teamId` varchar(32) DEFAULT NULL COMMENT '组训id',
  `userId` varchar(32) DEFAULT NULL COMMENT '收藏人id',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`storeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='收藏信息表';

CREATE TABLE `tab_tean_resume_map` (
  `mapId` varchar(32) NOT NULL COMMENT '关系id',
  `teamId` varchar(32) DEFAULT '' COMMENT '组训id',
  `userId` varchar(32) DEFAULT '' COMMENT '用户id(投递简历人的id)',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`mapId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='组训简历关系表';

CREATE TABLE `tab_report_info` (
  `reportId` varchar(32) NOT NULL COMMENT '举报id',
  `teamId` varchar(32) NOT NULL COMMENT '组训id',
  `userId` varchar(32) DEFAULT '' COMMENT '举报人id',
  `reportType` smallint(6) NOT NULL COMMENT '举报类型 1虚假广告，2色情低俗，3违法违纪，4 咋骗信息',
  `reportComment` varchar(100) DEFAULT '' COMMENT '举报说明',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`reportId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='举报信息表';



-- 0921
ALTER TABLE `tab_notice_pushFedBack` MODIFY COLUMN `backStatus`  smallint(6) NULL DEFAULT 1 COMMENT '反馈状态 1：未收取  2：已收取   3：已查看';

ALTER table tab_scenario_info add(`downloaded` smallint(6) DEFAULT '0' COMMENT '是否被下载过，用于手机端判断剧本是否有更新');

ALTER TABLE `tab_crew_contact`
DROP COLUMN `department`,
CHANGE COLUMN `idNumber` `identityCertNumber` varchar(18) NULL DEFAULT NULL COMMENT '身份证件号码',
MODIFY COLUMN `duties` varchar(200) NULL DEFAULT NULL COMMENT '职务，格式：部门-职务，多个以逗号隔开',
ADD identityCertType SMALLINT(6) DEFAULT 1 COMMENT '身份证件类型：1-身份证  2-护照  3-台胞证  4-军官证   5-其他';

ALTER TABLE `tab_crew_contact`
CHANGE COLUMN `stayingDate` `checkInDate`  date NULL DEFAULT NULL COMMENT '入住日期';

ALTER TABLE `tab_crew_contact`
CHANGE COLUMN `identityCertType` `identityCardType`  smallint(6) NULL DEFAULT 1 COMMENT '身份证件类型：1-身份证  2-护照  3-台胞证  4-军官证   5-其他',
CHANGE COLUMN `identityCertNumber` `identityCardNumber`  varchar(18) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '身份证件号码' AFTER `identityCardType`;

ALTER TABLE `tab_crew_contact` DROP COLUMN `duties`;


CREATE TABLE tab_contact_sysrole_map(
    id VARCHAR(32) NOT NULL PRIMARY KEY,
    crewId VARCHAR(32) NOT NULL COMMENT '剧组ID',
    contactId VARCHAR(32) NOT NULL COMMENT '剧组联系人ID',
    sysroleId VARCHAR(32) NOT NULL COMMENT '系统角色ID'
) COMMENT '剧组联系表和系统角色关联表';

ALTER TABLE tab_liveConverAdd_info ADD `cpackupTime` timestamp NULL DEFAULT NULL COMMENT '收工时间';
ALTER TABLE tab_liveConverAdd_info ADD `cshootLocation` varchar(1000) DEFAULT NULL COMMENT '拍摄地点';
ALTER TABLE tab_liveConverAdd_info ADD `cshootScene` varchar(1000) DEFAULT NULL COMMENT '拍摄场景';

UPDATE tab_finance_setting_info set pwdStatus=0 where pwdStatus is null;
ALTER TABLE `tab_finance_setting_info` MODIFY COLUMN `pwdStatus`  smallint(6) NULL DEFAULT 0 COMMENT '是否启用密码功能，0：禁用，1：启用';


-- 0923
ALTER TABLE tab_crew_info ADD picPath VARCHAR(100) DEFAULT NULL COMMENT '剧照存储路径';


-- 0924
DELETE from tab_message_info;
ALTER TABLE tab_message_info ADD receiverId varchar(32) DEFAULT NULL COMMENT '消息接收人ID';
ALTER TABLE tab_message_info ADD remindTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '消息提醒时间';
ALTER TABLE tab_message_info ADD type SMALLINT DEFAULT NULL COMMENT '消息类型：1-发布通告单  2-入组申请审核成功  3-入组申请审核失败  4-申请入组  5-付款单发票提醒  6-合同支付提醒';
ALTER TABLE tab_message_info CHANGE COLUMN `messageId` `id`  varchar(32) NOT NULL COMMENT '消息ID';
ALTER TABLE tab_message_info ADD title varchar(100) DEFAULT NULL COMMENT '标题';
ALTER TABLE tab_message_info ADD buzId varchar(32) DEFAULT NULL COMMENT '业务ID';

-- 1009
INSERT INTO tab_currency_info values('2', '台币', 'TWD', 1, 1, 0.2121, '0');

ALTER TABLE tab_validCode_info ADD (type smallint(6) NOT NULL DEFAULT 1 COMMENT '类型：1-找回密码  2-注册  3-修改手机号');
ALTER TABLE `tab_validCode_info` CHANGE COLUMN `status` `valid`  smallint(6) NOT NULL DEFAULT 1 COMMENT '是否有效，0：无效  1：有效';
ALTER TABLE `tab_validCode_info` CHANGE COLUMN `codeId` `id`  varchar(32) NOT NULL COMMENT '验证码ID';
ALTER TABLE tab_validCode_info RENAME to tab_verifyCode_info;

ALTER TABLE `tab_contract_worker` MODIFY COLUMN `bankAccountNumber`  varchar(100) NULL DEFAULT NULL COMMENT '银行账户账号';
ALTER TABLE `tab_contract_actor` MODIFY COLUMN `bankAccountNumber`  varchar(100) NULL DEFAULT NULL COMMENT '银行账户账号';
ALTER TABLE `tab_contract_produce` MODIFY COLUMN `bankAccountNumber`  varchar(100) NULL DEFAULT NULL COMMENT '银行账户账号';


CREATE TABLE `tab_feedback_info` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `userId` varchar(32) NOT NULL COMMENT '用户ID',
  `contact` varchar(50) DEFAULT NULL COMMENT '联系方式',
  `message` text NOT NULL COMMENT '反馈意见',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) COMMENT='用户反馈信息表';

ALTER TABLE tab_user_info ADD `age` int(4) DEFAULT 20 COMMENT '年龄';
ALTER TABLE tab_user_info ADD `profile` varchar(500) DEFAULT NULL COMMENT '个人简介';

-- 1013
ALTER TABLE tab_team_position_info ADD COLUMN needPositionId VARCHAR(100) NOT NULL COMMENT '组训招聘职位的id';
ALTER TABLE tab_search_team_info ADD COLUMN likePositionId VARCHAR(100) NOT NULL COMMENT '意向职位id';
ALTER TABLE tab_tean_resume_map ADD COLUMN positionId VARCHAR(32) NOT NULL COMMENT '招聘信息id';

-- 1022

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



-- 1025
UPDATE tab_view_role set viewRoleType=0 where viewRoleType=4;
ALTER TABLE `tab_view_role` MODIFY COLUMN `viewRoleType`  smallint(6) DEFAULT 0 COMMENT '场景角色类型。0：待定演员；1：主要演员；2：特约演员；3：群众演员';

-- 1027
ALTER TABLE `tab_finance_subject_template` MODIFY COLUMN `type`  smallint(6) NULL DEFAULT NULL COMMENT '类型。0：按制作周期；1：按部门；2：无模板';
INSERT INTO tab_finance_subject_template VALUES('477', '一级科目1', 1, '0', 2), ('478', '一级科目2', 1, '0', 2), ('479', '一级科目3', 1, '0', 2);
CREATE TABLE `tab_crew_page_info` (
  `pageId` varchar(32) NOT NULL COMMENT '主键ID',
  `crewId` varchar(32) NOT NULL COMMENT '剧组Id',
  `wordCount` varchar(5) DEFAULT NULL COMMENT '每行显示字数',
  `lineCount` varchar(5) DEFAULT NULL COMMENT '每页显示行数',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`pageId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='剧组行页数信息表';


-- 剧组信息增加项目类型字段
ALTER TABLE `tab_crew_info`
ADD COLUMN `projectType`  smallint(6) NULL COMMENT '项目类型，预留字段，例如试用项目、付费项目等，具体内容待定' AFTER `picPath`;


-- 修改admin剧组管理权限url
update tab_sys_authority set authUrl='/crewManager/toCrewManagePage' where authUrl='/crewManager/getCrewAll';

-- 1028
ALTER TABLE tab_notice_time ADD COLUMN noticeContact VARCHAR(1000) DEFAULT '' COMMENT '通告单中联系人信息';

-- 1102
ALTER TABLE `tab_notice_picture` MODIFY COLUMN `noticeVersion`  varchar(100) NOT NULL COMMENT '通告单版本,此处版本是根据tab_notice_time表中updateTime字段自动生成的一个版本号，格式yyyyMMddHHmmss';
ALTER TABLE `tab_notice_pushFedBack` MODIFY COLUMN `noticeVersion`  varchar(100) NOT NULL COMMENT '通告单版本,此处版本是根据tab_notice_time表中updateTime字段自动生成的一个版本号，格式yyyyMMddHHmmss';

UPDATE tab_notice_time set updateTime=createTime where updateTime is null;

-- 1103
ALTER TABLE tab_contract_actor ADD `identityCardType` smallint(6) DEFAULT '1' COMMENT '身份证件类型：1-身份证  2-护照  3-台胞证  4-军官证   5-其他';
ALTER TABLE `tab_contract_actor` CHANGE COLUMN `idNumber` `identityCardNumber`  varchar(18) NULL DEFAULT NULL COMMENT '身份证件号码';

ALTER TABLE tab_contract_worker ADD `identityCardType` smallint(6) DEFAULT '1' COMMENT '身份证件类型：1-身份证  2-护照  3-台胞证  4-军官证   5-其他';
ALTER TABLE `tab_contract_worker` CHANGE COLUMN `idNumber` `identityCardNumber`  varchar(18) NULL DEFAULT NULL COMMENT '身份证件号码';

ALTER TABLE tab_contract_produce ADD `identityCardType` smallint(6) DEFAULT '1' COMMENT '身份证件类型：1-身份证  2-护照  3-台胞证  4-军官证   5-其他';
ALTER TABLE `tab_contract_produce` CHANGE COLUMN `idNumber` `identityCardNumber`  varchar(18) NULL DEFAULT NULL COMMENT '身份证件号码';


-- 修改费用进度url
UPDATE tab_sys_authority set authUrl='/financeStatisticManager/toFinanceStatisticPage' where authUrl='/financeAccountManager/accountForm';

-- 1104
ALTER TABLE `tab_view_info` MODIFY COLUMN `shotDate`  date NULL DEFAULT NULL COMMENT '拍摄日期';

-- 1007
ALTER TABLE tab_finanSubj_currency_map MODIFY `amount` double DEFAULT 0 COMMENT '数量';
UPDATE tab_finanSubj_currency_map set amount=0 where amount is null;

ALTER TABLE tab_finanSubj_currency_map MODIFY `perPrice` double DEFAULT 0.0 COMMENT '单价';
UPDATE tab_finanSubj_currency_map set perPrice=0 where perPrice is null;

ALTER TABLE tab_view_info MODIFY `pageCount` double DEFAULT 0.0 COMMENT '页数';
UPDATE tab_view_info set pageCount=0.0 where pageCount is null;

-- 1109
drop TABLE IF EXISTS tab_weather_info;
CREATE TABLE tab_weather_info (
        id VARCHAR(32) NOT NULL PRIMARY KEY,
        cityName VARCHAR(32) NOT NULL COMMENT '城市名',
        day date NOT NULL COMMENT '当前天',
        dayWeather VARCHAR(100) NULL COMMENT '白天天气',
        nightWeather VARCHAR(100) NULL COMMENT '晚上天气',
        dayTemperature VARCHAR(100) NULL COMMENT '白天气温',
        nightTemperature VARCHAR(100) NULL COMMENT '晚上气温',
        dayWindDirection VARCHAR(100) NULL COMMENT '白天风向',
        nightWindDirection VARCHAR(100) NULL COMMENT '晚上风向',
        dayWeatherCode VARCHAR(100) NULL COMMENT '白天天气编码',
        dayWeatherPic VARCHAR(100) NULL COMMENT '白天天气图标',
        updateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '天气信息';

alter table tab_convertAddress_info modify column afterViewIds varchar(2000);
alter table tab_notice_time modify column noticeContact varchar(2000);
alter table tab_notice_time modify column roleConvertRemark varchar(2000);
ALTER TABLE tab_work_experience_info ADD COLUMN allowUpdate VARCHAR(10) COMMENT '是否允许修改';

-- 1110
CREATE TABLE tab_download_scenario_record(
    id VARCHAR(32) NOT NULL PRIMARY KEY,
    crewId VARCHAR(32) NOT NULL COMMENT '剧组ID',
    clientUUID VARCHAR(100) NULL COMMENT '设备号'
) COMMENT '剧组中剧本下载记录';
ALTER TABLE `tab_scenario_info` DROP COLUMN `downloaded`;



















