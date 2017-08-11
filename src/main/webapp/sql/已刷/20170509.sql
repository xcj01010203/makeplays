-- 创建剧本格式表
DROP TABLE if EXISTS tab_scenario_format;
CREATE TABLE tab_scenario_format (
    id VARCHAR(32) PRIMARY KEY,
    crewId VARCHAR(32) NOT NULL COMMENT '剧组ID',
    wordCount smallint(6) DEFAULT '35' COMMENT '一行字数',
    lineCount smallint(6) DEFAULT '40' COMMENT '一页行数',
    scriptRule varchar(300) DEFAULT NULL COMMENT '剧本分析规则',
  supportCNViewNo int(10) DEFAULT '0' COMMENT '是否支持中文的场次解析'
) COMMENT '剧组剧本格式';


-- 同步老数据到新的剧本格式信息表中
DROP PROCEDURE IF EXISTS pro_deal_scenario_format;
CREATE PROCEDURE pro_deal_scenario_format()
BEGIN
DECLARE myScriptRule VARCHAR(300);
DECLARE mySupportCNViewNo INT(10);
DECLARE myWordCount SMALLINT(6);
DECLARE myLineCount SMALLINT(6);


DECLARE myCrewId VARCHAR(32);
DECLARE endFlag int default 0;

DECLARE crewId_list CURSOR for SELECT tci.crewId from tab_crew_info tci where EXISTS (SELECT 1 from tab_scenario_info tsi where tsi.crewId = tci.crewId) ORDER BY tci.createTime;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET endFlag = 1;

open crewId_list;
FETCH crewId_list INTO myCrewId;
WHILE endFlag <> 1 DO

    SELECT wordCount, lineCount, scriptRule, supportCNViewNo 
        INTO myWordCount, myLineCount, myScriptRule, mySupportCNViewNo
        FROM tab_scenario_info WHERE crewId = myCrewId ORDER BY uploadTime DESC LIMIT 1;

    if myWordCount = 0 and myLineCount = 0 THEN
        SET myWordCount = 35;
        SET myLineCount = 40;
    END IF;

    INSERT tab_scenario_format VALUES(REPLACE(UUID(), '-', ''), myCrewId, myWordCount, myLineCount, myScriptRule, mySupportCNViewNo);
    FETCH crewId_list INTO myCrewId;
END WHILE;
close crewId_list;
end;

CALL pro_deal_scenario_format();

DROP PROCEDURE IF EXISTS pro_deal_scenario_format;
CREATE PROCEDURE pro_deal_scenario_format()
BEGIN
DECLARE myWordCount SMALLINT(6);
DECLARE myLineCount SMALLINT(6);

DECLARE myCrewId VARCHAR(32);
DECLARE endFlag int default 0;

DECLARE crewId_list CURSOR for SELECT crewId from tab_crew_page_info ;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET endFlag = 1;

open crewId_list;
FETCH crewId_list INTO myCrewId;
WHILE endFlag <> 1 DO

    SELECT wordCount, lineCount
        INTO myWordCount, myLineCount
        FROM tab_crew_page_info WHERE crewId = myCrewId;

    update tab_scenario_format SET wordCount = myWordCount, lineCount = myLineCount where crewId = myCrewId;
    FETCH crewId_list INTO myCrewId;
END WHILE;
close crewId_list;
end;

CALL pro_deal_scenario_format();
DROP PROCEDURE IF EXISTS pro_deal_scenario_format;

drop table tab_crew_page_info;