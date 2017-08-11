-- 增加客服角色
UPDATE tab_sysrole_info set roleName='总客服' where roleId='2';
INSERT INTO `tab_sysrole_info` (`roleId`, `roleName`, `roleDesc`, `crewId`, `parentId`, `canBeEvaluate`, `level`, `orderNo`) VALUES ('4', '高级客服', '管理部分剧组，拥有所有权限', '0', '01', '0', '1', '6');
INSERT INTO `tab_sysrole_info` (`roleId`, `roleName`, `roleDesc`, `crewId`, `parentId`, `canBeEvaluate`, `level`, `orderNo`) VALUES ('5', '中级客服', '管理部分剧组，不能导入导出', '0', '01', '0', '1', '7');
INSERT INTO `tab_sysrole_info` (`roleId`, `roleName`, `roleDesc`, `crewId`, `parentId`, `canBeEvaluate`, `level`, `orderNo`) VALUES ('6', '初级客服', '管理部分剧组，只读', '0', '01', '0', '1', '8');
-- 修改用户类型，增加客服
ALTER TABLE `tab_user_info`
MODIFY COLUMN `type`  smallint(6) NULL DEFAULT NULL COMMENT '0：剧组成员；1：后台管理员；2：客户服务' AFTER `realName`;
-- 更新总客服信息
update tab_user_info set type=2 where userId in (select userId from tab_crew_user_map where crewId='0');
-- 增加总客服与角色关联关系
insert into tab_user_role_map(mapId,userId,roleId,crewId) 
select REPLACE (UUID(), '-', ''),userId,'2','0' 
from tab_crew_user_map 
where crewId='0';
-- 删除客服与剧组0的关联关系
DELETE FROM tab_crew_user_map where crewId='0';