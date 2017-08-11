update tab_sceneview_info  set vCity=concat(SUBSTR(vCity FROM 1 FOR 2),SUBSTR(vCity FROM 5)) where vCity is not null;
update tab_sceneview_info tsi set vCity=(select areaName from (select CONCAT(ptai.areaName,tai.areaName) areaName
from tab_area_info tai 
left join tab_area_info ptai on ptai.areaId=tai.parentId
where tai.parentId != 0) my where tsi.vCity like concat(my.areaName,'%')) where vCity is not null;