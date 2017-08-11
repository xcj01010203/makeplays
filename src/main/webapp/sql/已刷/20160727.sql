ALTER TABLE tab_actor_attendance RENAME TO tab_actor_leave_record;

ALTER TABLE tab_actor_leave_record COMMENT '演员请假记录表';

ALTER TABLE `tab_actor_leave_record` CHANGE COLUMN `attendanceId` `id`  varchar(32) NOT NULL  COMMENT '' ;

ALTER TABLE tab_actor_leave_record ADD createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';

ALTER TABLE tab_evtag_info DROP COLUMN userName;