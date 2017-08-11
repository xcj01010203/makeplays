-- 将拍摄地信息复制到勘景表中
INSERT INTO tab_sceneview_info ( id,crewid,vname,orderNumber) SELECT shootlocationid,crewid,shootlocation,0 FROM tab_shoot_location;

-- 优化勘景查询速度
create index tab_view_location_map_crewid on tab_view_location_map (crewid);

-- 删除勘景场景信息对照表
-- drop table tab_sceneview_viewinfo_map;

-- 删除拍摄地表
-- drop table tab_shoot_location;

-- 给tab_inhotelcost_temp 添加注释
alter table tab_inhotelcost_temp modify column showdate date comment '入住时间'; 
alter table tab_inhotelcost_temp modify column contactid varchar(32) comment '入住人id';
alter table tab_inhotelcost_temp modify column hotelname varchar(255) comment '宾馆名称';
alter table tab_inhotelcost_temp modify column roomnumber varchar(20) comment '房间号';
alter table tab_inhotelcost_temp modify column price double comment '房价';
alter table tab_inhotelcost_temp modify column contactname varchar(255) comment '入住人姓名';
alter table tab_inhotelcost_temp modify column crewid varchar(32) comment '剧组id';
alter table tab_inhotelcost_temp comment '剧组住宿费用详情';

-- 将拍摄地信息复制到勘景表中      修改改景状态
update tab_sceneview_info set ismodifyview = 1 where ismodifyview is null ;
-- 将拍摄地信息复制到勘景表中      修改道具陈设
update tab_sceneview_info set hasprop = 1 where hasprop is null ;

-- 将拍摄地信息复制到勘景表中      往附件包表添加数据
INSERT INTO tab_attachment_packet (
   id,
   crewid,
   relatedtobuz,
   containattment,
   buztype,
   createTime
) SELECT
   id,
   crewid,
   0,
   0,
   NULL,
   SYSDATE()
FROM
   tab_sceneview_info
WHERE
   id IN (
      SELECT DISTINCT
         shootLocationId id
      FROM
         tab_shoot_location
   )
AND id NOT IN (
   SELECT
      id
   FROM
      tab_attachment_packet
)