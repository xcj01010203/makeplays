UPDATE tab_sys_authority SET authUrl = null where parentId = '0' and ifMenu = 1;

-- 删除原先的剧组设置权限
delete from tab_role_auth_map where authId = (SELECT authId from tab_sys_authority where authCode='pc10029');
DELETE from tab_user_auth where authId= (SELECT authId from tab_sys_authority where authCode='pc10029');
DELETE from tab_sys_authority where authCode='pc10029';

-- 剧组表新增“备案号”和“入组密码”两个字段
ALTER TABLE tab_crew_info add(recordNumber VARCHAR(100) DEFAULT NULL COMMENT '备案号');
ALTER TABLE tab_crew_info add(enterPassword VARCHAR(100) DEFAULT '000000' COMMENT '入组密码');


-- 重新设置首页权限url
update tab_sys_authority set authUrl='/toIndexPage' where authUrl='/index';

-- 用户表添加可建组数量字段
ALTER TABLE tab_user_info ADD COLUMN ubCreateCrewNum SMALLINT(10) DEFAULT 0 COMMENT '可用建组次数';

-- 权限相关字段整改
ALTER TABLE tab_sys_authority ADD differInRAndW SMALLINT(5) DEFAULT 0 COMMENT '是否区分读写操作（1：是  0：否），默认否';

ALTER TABLE tab_role_auth_map ADD readonly SMALLINT(5) DEFAULT 0 COMMENT '如果权限区分读写操作，是否只读（1：是   0：否），默认否';
ALTER TABLE tab_user_auth ADD readonly SMALLINT(5) DEFAULT 0 COMMENT '如果权限区分读写操作，是否只读（1：是   0：否），默认否';

DROP TABLE tab_user_auth_map;
ALTER TABLE tab_user_auth RENAME TO tab_user_auth_map;
ALTER TABLE `tab_user_auth_map` CHANGE COLUMN `userAuthId` `mapId`  varchar(32) NOT NULL COMMENT '权限ID';
ALTER TABLE tab_user_auth_map DROP column authType;

update tab_sys_authority set authCode=null where authPlantform=2;

-- 申请加入剧组信息表
DROP TABLE IF EXISTS tab_joinCrew_applyMsg;
CREATE TABLE tab_joinCrew_applyMsg (
    id VARCHAR(32) NOT NULL PRIMARY KEY,
    applyerId VARCHAR(32) NOT NULL COMMENT '申请人ID',
    aimCrewId VARCHAR(32) NOT NULL COMMENT '想要加入的剧组ID',
    aimRoleIds VARCHAR(32) NOT NULL COMMENT '申请的职务ID，多个职务用逗号隔开',
    aimRoleNames VARCHAR(200) NOT NULL COMMENT '申请的职务名称，多个职务用逗号隔开',
    dealerId VARCHAR(32) DEFAULT NULL COMMENT '审核人ID',
    status SMALLINT NOT NULL DEFAULT 1 COMMENT '状态：1表示审核中  2表示审核通过  3表示审核不通过',
    createTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，和申请时间是一个概念',
    lastModifyTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间，和审核时间是一个概念',
    remark VARCHAR(500) NOT NULL COMMENT '备注'
);

-- 剧组表注释
ALTER TABLE `tab_crew_info`
MODIFY COLUMN `createUser`  varchar(32)  DEFAULT NULL COMMENT '创建者Id' AFTER `status`;

DROP table if EXISTS tab_crew_contact;
CREATE TABLE `tab_crew_contact` (
  `contactId` varchar(32) NOT NULL COMMENT '联系人ID',
  `contactName` varchar(20) DEFAULT NULL COMMENT '联系人姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机',
  `sex` smallint(6) DEFAULT NULL COMMENT '性别。0：女；1：男',
  `idNumber` varchar(18) DEFAULT NULL COMMENT '身份证号码',
  `department` varchar(200) DEFAULT NULL COMMENT '部门',
  `duties` varchar(200) DEFAULT NULL COMMENT '职务',
  `enterDate` date DEFAULT NULL COMMENT '入组日期',
  `leaveDate` date DEFAULT NULL COMMENT '离组日期',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `mealType` smallint(6) DEFAULT NULL COMMENT '餐别。1：常规；2：回民',
  `hotel` varchar(50) DEFAULT NULL COMMENT '宾馆',
  `roomNumber` varchar(20) DEFAULT NULL COMMENT '房间号',
  `extension` varchar(20) DEFAULT NULL COMMENT '分机号',
  `stayingDate` date DEFAULT NULL COMMENT '入住日期',
  `checkoutDate` date DEFAULT NULL COMMENT '退房日期',
  `ifOpen` smallint(6) DEFAULT NULL COMMENT '是否公开到组。1：公开；0：不公开',
  `sequence` smallint(6) DEFAULT NULL COMMENT '排列顺序',
  `crewId` varchar(32) DEFAULT NULL COMMENT '剧组ID',
  `userId` varchar(32) DEFAULT NULL COMMENT '关联的剧组用户信息ID',
  PRIMARY KEY (`contactId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='剧组联系表';


-- 增加入组时间字段
ALTER TABLE tab_crew_user_map add createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';

-- 权限表添加默认读写权限字段
ALTER TABLE tab_sys_authority ADD defaultRorW SMALLINT DEFAULT 2 COMMENT '默认读写操作：1表示只读   2表示可编辑';

