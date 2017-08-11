UPDATE tab_view_role set viewRoleType=0 where viewRoleType=4;
ALTER TABLE `tab_view_role` MODIFY COLUMN `viewRoleType`  smallint(6) DEFAULT 0 COMMENT '场景角色类型。0：待定演员；1：主要演员；2：特约演员；3：群众演员';