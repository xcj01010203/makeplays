-- 优化勘景备选主场景查询
create index tab_sceneview_viewinfo_map_locationid on tab_sceneview_viewinfo_map(locationid);

ALTER TABLE tab_finance_setting_info ADD monthDayType int(10) DEFAULT 1 COMMENT '每月天数类型，1：自然月天数，2：标准30天';