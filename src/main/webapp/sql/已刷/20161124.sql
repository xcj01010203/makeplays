UPDATE tab_view_role SET viewRoleType=4 where viewRoleType=0;
-- 权限表增加摄制生产报表
INSERT INTO tab_sys_authority (authId, authName, operType, operDesc, authUrl, ifMenu, status, parentId, sequence, authPlantform, authCode, differInRAndW, defaultRorW) VALUES ('13aaee63ace0455eba1eb1aa2d0dd622', '拍摄进度表', '0', '', '/viewStatisticManager/toProductionReportPage', '1', '0', '8df0bb0500f64d639f194d6810423e63', '5', '2', '', '0', '1');

ALTER TABLE tab_actor_info MODIFY `workStartTime` timestamp NULL DEFAULT NULL COMMENT '日工作开始时间';
ALTER TABLE tab_actor_info MODIFY `workEndTIme` timestamp NULL DEFAULT NULL COMMENT '日工作结束时间';
ALTER TABLE tab_android_version_info MODIFY `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间';
ALTER TABLE tab_art_info MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间';
ALTER TABLE tab_bulletin_info MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE tab_clothes_info MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE tab_crew_info MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE tab_crew_page_info MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE tab_evaluate_info MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价信息创建时间';
ALTER TABLE tab_evtag_info MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE tab_finance_person MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE tab_finance_setting_info MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE tab_finance_subject MODIFY `createTime` timestamp NULL DEFAULT NULL COMMENT '添加时间';
ALTER TABLE tab_image_info MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间';
ALTER TABLE tab_makeup_info MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE tab_notice_info MODIFY `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间';
ALTER TABLE tab_notice_pushFedBack MODIFY `statusUpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '状态更新日期';
ALTER TABLE tab_notice_role_time MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE tab_notice_time MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE tab_payment_loan_map MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE tab_props_info MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE tab_report_info MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE tab_salary_info MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE tab_scenario_info MODIFY `uploadTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '剧本上传时间';
ALTER TABLE tab_search_team_info MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE tab_shoot_group MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE tab_shoot_log MODIFY `shootLogTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '日志记录时间';
ALTER TABLE tab_shootLive_info MODIFY `serverTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '服务端最后保存时间';
ALTER TABLE tab_shootplan_info MODIFY `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最新修改时间';
ALTER TABLE tab_store_info MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE tab_sys_log MODIFY `logTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '日志操作时间';
ALTER TABLE tab_team_info MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE tab_team_position_info MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE tab_tean_resume_map MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE tab_tmpCancelView_info MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE tab_user_info MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE tab_user_login_log MODIFY `logTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '日志操作时间';
ALTER TABLE tab_view_info MODIFY `statusUpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '状态修改时间';
ALTER TABLE tab_work_experience_info MODIFY `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';