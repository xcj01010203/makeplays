ALTER TABLE tab_view_info ADD `notGetProps` varchar(200) DEFAULT NULL COMMENT '未提取的道具，多个用/隔开';

ALTER TABLE tab_finanSubj_currency_map MODIFY COLUMN unitType VARCHAR(10) DEFAULT NULL COMMENT '单位类型';

UPDATE tab_finanSubj_currency_map SET unitType = '集' WHERE unitType = '1';
UPDATE tab_finanSubj_currency_map SET unitType = '月' WHERE unitType = '2';
UPDATE tab_finanSubj_currency_map SET unitType = '日' WHERE unitType = '3';
UPDATE tab_finanSubj_currency_map SET unitType = '部' WHERE unitType = '4';
UPDATE tab_finanSubj_currency_map SET unitType = '个' WHERE unitType = '5';
UPDATE tab_finanSubj_currency_map SET unitType = '人' WHERE unitType = '6';

INSERT INTO tab_finanSubj_currency_map VALUE ('1',NULL,NULL,NULL,'0','0','0','集');
INSERT INTO tab_finanSubj_currency_map VALUE ('2',NULL,NULL,NULL,'0','0','0','月');
INSERT INTO tab_finanSubj_currency_map VALUE ('3',NULL,NULL,NULL,'0','0','0','日');
INSERT INTO tab_finanSubj_currency_map VALUE ('4',NULL,NULL,NULL,'0','0','0','部');
INSERT INTO tab_finanSubj_currency_map VALUE ('5',NULL,NULL,NULL,'0','0','0','个');
INSERT INTO tab_finanSubj_currency_map VALUE ('6',NULL,NULL,NULL,'0','0','0','人');