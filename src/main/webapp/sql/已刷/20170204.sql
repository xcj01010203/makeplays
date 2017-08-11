-- 修改拍摄进度-生产进度权限url
update tab_sys_authority set authUrl='/shootStatistic/toShootStatisticPage' where authUrl='/shootReportManager/formList';