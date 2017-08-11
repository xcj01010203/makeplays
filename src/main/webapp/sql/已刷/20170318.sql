update tab_sys_authority set authUrl='/projectManager/toProjectListPage',authName='项目管控表' where authUrl='/projectManage/toProductListPage';
update tab_sys_authority set authName='项目管控表' where parentId='d1adf5ce58f94550966dde9c4081351a';
ALTER TABLE `tab_sys_log`
MODIFY COLUMN `logDesc`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '日志描述' AFTER `logTime`,
MODIFY COLUMN `operType`  smallint(6) NULL DEFAULT NULL COMMENT '操作类型。0：读；1：插入；2：修改；3：删除；4：批量导入；5:导出，6：异常，99：其他' AFTER `userIp`;