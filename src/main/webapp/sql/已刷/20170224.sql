-- 修改权限配置
update tab_sys_authority set authCode='pc10014',differInRAndW=1,defaultRorW=2 where authUrl='/viewManager/toScenarioAnalysisPage';
update tab_sys_authority set authCode='pc10018',differInRAndW=1,defaultRorW=2 where authUrl='/viewManager/toviewListPage';
update tab_sys_authority set differInRAndW=1,defaultRorW=2 where authUrl='/sceneViewInfoController/toSceneViewPage';
update tab_sys_authority set authCode='pc10021',differInRAndW=1,defaultRorW=2 where authUrl='/notice/toNoticeList';
update tab_sys_authority set authCode='pc10022',differInRAndW=1,defaultRorW=2 where authUrl='/contact/toContactList';
update tab_sys_authority set authCode='pc10025',differInRAndW=1,defaultRorW=2 where authUrl='/carManager/toCarListPage';
update tab_sys_authority set authCode='pc10028',differInRAndW=1,defaultRorW=2 where authUrl='/financeSettingManager/toFinanceSettingPage';
update tab_sys_authority set authCode='pc10033' where authUrl='/getCostManager/toGetCostPage';

ALTER TABLE `tab_crew_info`
DROP COLUMN `projectType`,
ADD COLUMN `projectType`  smallint(2) NULL DEFAULT 0 COMMENT ' 项目类型，0：普通项目，1：试用项目，待扩展' AFTER `picPath`,
ADD COLUMN `refreshAuth`  smallint(2) NULL DEFAULT 0 COMMENT '是否已刷新权限，用于过期剧组，0：否，1：是' AFTER `projectType`;