ALTER TABLE tab_view_content ADD readedPeopleIds text NULL COMMENT '已读用户ID，格式[a, b, c, d]';

update tab_view_content SET content = SUBSTR(content,1, CHAR_LENGTH(content) - 2), figureprint = NULL where content like '%\r\n';
update tab_view_content SET title = SUBSTR(title,1, CHAR_LENGTH(title) - 2) where title like '%\r\n';

ALTER TABLE tab_actor_info ADD COLUMN workHours VARCHAR(10) DEFAULT NULL COMMENT '工作时长';
ALTER TABLE tab_actor_info ADD COLUMN restHours VARCHAR(10) DEFAULT NULL COMMENT '休息时长';
ALTER TABLE tab_view_role ADD COLUMN isAttentionRole SMALLINT(2) DEFAULT 0 COMMENT '是否是关注角色';