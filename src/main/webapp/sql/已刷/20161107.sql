ALTER TABLE tab_finanSubj_currency_map MODIFY `amount` double DEFAULT 0 COMMENT '数量';
UPDATE tab_finanSubj_currency_map set amount=0 where amount is null;

ALTER TABLE tab_finanSubj_currency_map MODIFY `perPrice` double DEFAULT 0.0 COMMENT '单价';
UPDATE tab_finanSubj_currency_map set perPrice=0 where perPrice is null;

ALTER TABLE tab_view_info MODIFY `pageCount` double DEFAULT 0.0 COMMENT '页数';
UPDATE tab_view_info set pageCount=0.0 where pageCount is null;