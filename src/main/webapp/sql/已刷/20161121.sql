-- 修改进度表>拍摄地点
UPDATE tab_sys_authority set authUrl='/viewStatisticManager/toViewLocationStatisticPage',authName='场景汇总' where authUrl='/shootViewLocation/locationForm';
-- 增加分集汇总
INSERT INTO tab_sys_authority (authId, authName, operType, operDesc, authUrl, ifMenu, status, parentId, sequence, authPlantform, authCode, differInRAndW, defaultRorW) VALUES ('515e65e705b841dcb425eb6a965dc9e1', '分集汇总', '2', '', '/viewStatisticManager/toSeriesnoTotalInfoPage', '1', '0', '8df0bb0500f64d639f194d6810423e63', '4', '2', '', '0', '1');