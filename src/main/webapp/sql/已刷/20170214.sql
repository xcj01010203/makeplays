-- 新增项目管理权限
INSERT INTO `tab_sys_authority` (`authId`, `authName`, `operType`, `operDesc`, `authUrl`, `ifMenu`, `status`, `parentId`, `sequence`, `authPlantform`, `authCode`, `differInRAndW`, `defaultRorW`) VALUES ('d1adf5ce58f94550966dde9c4081351a', '项目管理', '2', '', '/projectManage/toProductListPage', '1', '0', '0', '7', '2', '', '0', '1');
INSERT INTO `tab_sys_authority` (`authId`, `authName`, `operType`, `operDesc`, `authUrl`, `ifMenu`, `status`, `parentId`, `sequence`, `authPlantform`, `authCode`, `differInRAndW`, `defaultRorW`) VALUES ('c951a8fef3d04daba231daf906d83743', '项目管理', '2', '', '', '0', '0', 'd1adf5ce58f94550966dde9c4081351a', '1', '2', '', '0', '1');
-- 新增项目总监角色
INSERT INTO `tab_sysrole_info` (`roleId`, `roleName`, `roleDesc`, `crewId`, `parentId`, `canBeEvaluate`, `level`, `orderNo`) VALUES ('3', '项目总监', '多个项目进度及相关数据比对', '0', '01', '0', '1', '4');
-- 为项目总监角色设置项目管理权限
INSERT INTO `tab_role_auth_map` (`mapId`, `roleId`, `authId`, `crewId`, `readonly`) VALUES ('0050b7101ad14e259f99da0c178cf46a', '3', 'c951a8fef3d04daba231daf906d83743', '0', '0');
INSERT INTO `tab_role_auth_map` (`mapId`, `roleId`, `authId`, `crewId`, `readonly`) VALUES ('3e7fa5e607f94e11af82e66b8990ddaf', '3', 'd1adf5ce58f94550966dde9c4081351a', '0', '0');
-- 创建剧组权限关联关系表
CREATE TABLE `tab_crew_auth_map` (
  `mapId` varchar(32) NOT NULL COMMENT '关联ID',
  `crewId` varchar(32) DEFAULT NULL COMMENT '剧组ID',
  `authId` varchar(32) DEFAULT NULL COMMENT '权限ID',
  `readonly` smallint(5) DEFAULT '0' COMMENT '如果权限区分读写操作，是否只读（1：是   0：否），默认否',
  PRIMARY KEY (`mapId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='剧组、权限关联信息表';
-- 老数据处理，原则：剧组拥有所有权限，是否只读均设置为否（即可编辑）
insert into tab_crew_auth_map(mapId,crewId,authId,readonly)
select DISTINCT replace(UUID(), '-', ''),tci.crewId,tsa.authId,0
from tab_crew_info tci,tab_sys_authority tsa
left join tab_role_auth_map tuam on tsa.authId=tuam.authId 
where ((tuam.roleId != '0' and tuam.roleId != '2') or tuam.roleId is null) 