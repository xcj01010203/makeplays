ALTER TABLE tab_team_position_info MODIFY `positionRequirement` varchar(1500) DEFAULT '' COMMENT '职位要求';
ALTER TABLE tab_view_info MODIFY `pageCount` double DEFAULT 0 COMMENT '页数';
-- 修改角色管理url
UPDATE tab_sys_authority set authUrl='/roleManager/toRoleListPage' where authUrl='/role/roleList';