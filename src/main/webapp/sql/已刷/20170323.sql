alter table tab_props_info add column stock SMALLINT COMMENT '库存量';

alter table tab_props_info add column remark varchar(500) COMMENT '备注';

alter table tab_props_info add column sequence SMALLINT COMMENT '序号';

update tab_props_info set sequence = 1 ;


ALTER TABLE tab_payment_info ADD COLUMN attpackId VARCHAR(32) DEFAULT NULL COMMENT '附件包id';
ALTER TABLE tab_loan_info ADD COLUMN attpackId VARCHAR(32) DEFAULT NULL COMMENT '附件包id';
ALTER TABLE tab_collection_info ADD COLUMN attpackId VARCHAR(32) DEFAULT NULL COMMENT '附件包id';

ALTER TABLE tab_view_notice_map ADD COLUMN prepareView SMALLINT(2) DEFAULT NULL COMMENT '是否是备戏；1表示是，2 表示否';
ALTER TABLE tab_view_notice_map ADD COLUMN shootPage DOUBLE DEFAULT NULL COMMENT '实际拍摄页数';
ALTER TABLE tab_view_notice_map ADD INDEX index_name(noticeId);