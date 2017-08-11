ALTER TABLE `tab_crew_info`
MODIFY COLUMN `projectType`  smallint(2) NULL DEFAULT 0 COMMENT ' 项目类型，0：普通项目，1：试用项目，2：内部项目' AFTER `picPath`;
UPDATE tab_sys_log tsl
SET projectType = (
	SELECT
		projectType
	FROM
		tab_crew_info tci
	WHERE
		tci.crewId = tsl.crewId
)
where projectType is null;
update tab_sys_log tsl set projectType=2 where crewId is not null and projectType is null;