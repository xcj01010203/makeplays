ALTER TABLE tab_team_position_info ADD COLUMN needPositionId VARCHAR(100) NOT NULL COMMENT '组训招聘职位的id';
ALTER TABLE tab_search_team_info ADD COLUMN likePositionId VARCHAR(100) NOT NULL COMMENT '意向职位id';
ALTER TABLE tab_tean_resume_map ADD COLUMN positionId VARCHAR(32) NOT NULL COMMENT '招聘信息id';