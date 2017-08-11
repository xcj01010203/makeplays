ALTER TABLE tab_crew_picture_info ADD COLUMN picturePassword VARCHAR(32) DEFAULT NULL COMMENT '剧照分组密码';
ALTER TABLE tab_crew_picture_info ADD COLUMN createUser VARCHAR(32) DEFAULT NULL COMMENT '创建人id';

ALTER TABLE tab_checkIn_hotel_info ADD COLUMN roomType VARCHAR(50) DEFAULT NULL COMMENT '房间类型';
INSERT INTO tab_checkIn_hotel_info VALUES('1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'','0','单间');
INSERT INTO tab_checkIn_hotel_info VALUES('2',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'','0','标间/双人间');
INSERT INTO tab_checkIn_hotel_info VALUES('3',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'','0','大床间');
INSERT INTO tab_checkIn_hotel_info VALUES('4',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'','0','三人间');
INSERT INTO tab_checkIn_hotel_info VALUES('5',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'','0','套间');
INSERT INTO tab_checkIn_hotel_info VALUES('6',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'','0','商务间');