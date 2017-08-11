ALTER TABLE `tab_crew_info`
MODIFY COLUMN `status`  smallint(2) NULL DEFAULT NULL COMMENT '剧组状态。1：筹备中；2：拍摄中；3：后期制作中；4：已完成；5：播出中；6：暂停' AFTER `shootEndDate`,
ADD COLUMN `seriesNo`  smallint(6) NULL COMMENT '立项集数' AFTER `refreshAuth`,
ADD COLUMN `coProduction`  smallint(2) NULL COMMENT '合拍协议：0:无，1：已签订' AFTER `seriesNo`,
ADD COLUMN `coProMoney`  double NULL COMMENT '合拍协议金额' AFTER `coProduction`,
ADD COLUMN `budget`  double NULL COMMENT '剧组执行预算' AFTER `coProMoney`,
ADD COLUMN `investmentRatio`  double NULL COMMENT '投资比例' AFTER `budget`,
ADD COLUMN `remark`  text NULL COMMENT '重要事项说明及重要情况预警' AFTER `investmentRatio`,
ADD COLUMN `lastRemark`  text NULL COMMENT '前次重要事项说明及重要情况预警' AFTER `remark`;