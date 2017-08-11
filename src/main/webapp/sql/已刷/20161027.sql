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


--修改admin剧组管理权限url
update tab_sys_authority set authUrl='/crewManager/toCrewManagePage' where authUrl='/crewManager/getCrewAll';