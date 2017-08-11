-- 创建方法，用于获取汉字或者拼音首字母
CREATE TABLE `cs_char2letter` (              
  `PY` char(1) character set utf8 NOT NULL,  
  `HZ` char(1) NOT NULL default '',          
  PRIMARY KEY  (`PY`)                        
) ENGINE=InnoDB DEFAULT CHARSET=gbk;

truncate table cs_char2letter;
set names utf8;
insert into cs_char2letter values
('A','骜'),
('B','簿'),
('C','错'),
('D','鵽'),
('E','樲'),
('F','鳆'),
('G','腂'),
('H','夻'),
('J','攈'),
('K','穒'),
('L','鱳'),
('M','旀'),
('N','桛'),
('O','沤'),
('P','曝'),
('Q','囕'),
('R','鶸'),
('S','蜶'),
('T','箨'),
('W','鹜'),
('X','鑂'),
('Y','韵'),
('Z','咗');


DROP FUNCTION IF EXISTS `func_get_first_letter`;

CREATE FUNCTION `func_get_first_letter`(
 words   varchar(255)) RETURNS  char(1) CHARSET utf8
BEGIN   
  declare fpy char(1);   
  declare pc char(1);   
  declare cc char(4);   
  set @fpy = UPPER(left(words,1));   
  set @pc = (CONVERT(@fpy   USING   gbk));   
  set @cc = hex(@pc);   
  if @cc >= "8140" and @cc <="FEA0" then  
    begin   
      select PY from cs_char2letter where hz>=@pc limit 1 into @fpy;
    end;   
  end   if;   
  Return   @fpy;   
  END;