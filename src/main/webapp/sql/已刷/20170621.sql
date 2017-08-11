ALTER TABLE `tab_crew_info`
ADD COLUMN `isStop`  smallint(2) NULL DEFAULT 0 COMMENT '是否停用' AFTER `cutRate`;

ALTER TABLE tab_crew_picture_info MODIFY COLUMN createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT'创建时间';