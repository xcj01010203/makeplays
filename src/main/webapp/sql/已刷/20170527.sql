ALTER TABLE `tab_view_info` MODIFY COLUMN `viewType`  smallint(6) NULL DEFAULT NULL COMMENT '场景类型。1：武戏；2：特效；3：武特';
UPDATE tab_view_info set viewType = null where viewType = 1;
UPDATE tab_view_info set viewType = 1 where viewType = 2;
UPDATE tab_view_info set viewType = 1 where viewType = 3;


-- 处理重复的场景老数据
DROP PROCEDURE IF EXISTS pro_deal_repeat_viewlocation;
CREATE PROCEDURE pro_deal_repeat_viewlocation()
BEGIN
DECLARE myLocation VARCHAR(100);
DECLARE myLocationType INT;
DECLARE myCrewId VARCHAR(100);

DECLARE myLocationId VARCHAR(100);

DECLARE endFlag int default 0;
DECLARE cur_repeat_location CURSOR for select crewId, location, locationType from tab_view_location GROUP BY crewId, location, locationType HAVING count(1) > 1;
DECLARE CONTINUE HANDLER FOR NOT FOUND  SET  endFlag = 1;

OPEN cur_repeat_location;

FETCH cur_repeat_location INTO myCrewId, myLocation, myLocationType;
    while endFlag<>1 DO
        SELECT locationId INTO myLocationId from tab_view_location where locationType=myLocationType and location=myLocation AND crewId=myCrewId LIMIT 0, 1;

        UPDATE tab_view_location_map
        SET locationId = myLocationId
        WHERE
            locationId IN (
                SELECT
                    locationId
                FROM
                    tab_view_location
                WHERE
                    locationType = myLocationType
                AND location = myLocation
                AND crewId = myCrewId
            );
        
        DELETE from tab_view_location where locationType=myLocationType and location=myLocation AND crewId=myCrewId AND locationId!=myLocationId;
        FETCH cur_repeat_location INTO myCrewId, myLocation, myLocationType;
    END WHILE;

CLOSE cur_repeat_location;
end;
CALL pro_deal_repeat_viewlocation();
DROP PROCEDURE IF EXISTS pro_deal_repeat_viewlocation;