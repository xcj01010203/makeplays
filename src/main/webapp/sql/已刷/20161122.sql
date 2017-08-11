-- 角色表增加级别、排序
ALTER TABLE `tab_sysrole_info`
ADD COLUMN `level`  int(2) NULL COMMENT '级别' AFTER `canBeEvaluate`;
ALTER TABLE `tab_sysrole_info`
ADD COLUMN `orderNo`  int(2) NULL COMMENT '排序' AFTER `level`;