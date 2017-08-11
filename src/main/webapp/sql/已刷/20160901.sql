CREATE TABLE `tab_team_info` (
  `teamId` varchar(32) NOT NULL COMMENT '组训ID',
  `createUser` varchar(32) NOT NULL COMMENT '创建者Id',
  `crewName` varchar(100) DEFAULT '' COMMENT '剧组名称',
  `crewType` smallint(6) DEFAULT NULL COMMENT '剧组类型。0：电影；1：电视剧；2：网络剧；3: 网大 ; 99：其他',
  `company` varchar(100) DEFAULT '' COMMENT '制片公司',
  `subject` varchar(100) DEFAULT '' COMMENT '题材',
  `shootlocation` varchar(100) DEFAULT '' COMMENT '拍摄地点',
  `director` varchar(100) DEFAULT '' COMMENT '导演',
  `scriptWriter` varchar(100) DEFAULT '' COMMENT '编剧',
  `shootStartDate` date DEFAULT NULL COMMENT '开机日期',
  `shootEndDate` date DEFAULT NULL COMMENT '杀青时间',
  `contactname` varchar(32) DEFAULT '' COMMENT '联系人姓名',
  `phoneNum` varchar(32) DEFAULT '' COMMENT '联系电话',
  `email` varchar(32) DEFAULT '' COMMENT '邮箱',
  `contactAddress` varchar(32) DEFAULT '' COMMENT '联系地址',
  `crewComment` varchar(300) DEFAULT '' COMMENT '剧组简介',
  `picPath` varchar(100) DEFAULT '' COMMENT '上传的宣传图片的保存地址',
  `status` smallint(6) DEFAULT '1' COMMENT '组训状态。1：可用；2：不可用;',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`teamId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='组训基本信息表';


CREATE TABLE `tab_team_position_info` (
  `positionId` varchar(32) NOT NULL COMMENT '职位ID',
  `teamId` varchar(32) NOT NULL COMMENT '组训ID',
  `createUser` varchar(32) NOT NULL COMMENT '创建者Id',
  `positionName` varchar(100) NOT NULL COMMENT '职位名称',
  `needPeopleNum` int(10) DEFAULT NULL COMMENT '招聘人数',
  `positionRequirement` varchar(200) DEFAULT '' COMMENT '职位要求',
  `status` smallint(6) DEFAULT '1' COMMENT '职位状态。1：可用；2：不可用;',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`positionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='组训中招聘职位基本信息表';


CREATE TABLE `tab_work_experience_info` (
  `experienceId` varchar(32) NOT NULL COMMENT '经历ID',
  `createUser` varchar(32) NOT NULL COMMENT '用户Id',
  `crewName` varchar(100) NOT NULL COMMENT '剧组名称',
  `positionId` varchar(50) DEFAULT NULL COMMENT '职务id',
  `positionName` varchar(100) DEFAULT '' COMMENT '职位名称',
  `joinCrewDate` date DEFAULT NULL COMMENT '入组日期',
  `leaveCrewDate` date DEFAULT NULL COMMENT '离组日期',
  `workrequirement` varchar(200) DEFAULT '' COMMENT '工作职责',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`experienceId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='工作经历基本信息表';

CREATE TABLE `tab_search_team_info` (
  `searchTeamId` varchar(32) NOT NULL COMMENT '寻组信息ID',
  `createUser` varchar(32) NOT NULL COMMENT '用户Id',
  `likePositionName` varchar(100) NOT NULL COMMENT '意向职务名称',
  `currentStartDate` date DEFAULT NULL COMMENT '个人档期开始时间',
  `currentEndDate` date DEFAULT NULL COMMENT '个人档期结束时间',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`searchTeamId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='寻组基本信息表(寻求职位基本信息表)';

CREATE TABLE `tab_store_info` (
  `storeId` varchar(32) NOT NULL COMMENT '收藏id',
  `teamId` varchar(32) DEFAULT NULL COMMENT '组训id',
  `userId` varchar(32) DEFAULT NULL COMMENT '收藏人id',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`storeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='收藏信息表';

CREATE TABLE `tab_tean_resume_map` (
  `mapId` varchar(32) NOT NULL COMMENT '关系id',
  `teamId` varchar(32) DEFAULT '' COMMENT '组训id',
  `userId` varchar(32) DEFAULT '' COMMENT '用户id(投递简历人的id)',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`mapId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='组训简历关系表';

CREATE TABLE `tab_report_info` (
  `reportId` varchar(32) NOT NULL COMMENT '举报id',
  `teamId` varchar(32) NOT NULL COMMENT '组训id',
  `userId` varchar(32) DEFAULT '' COMMENT '举报人id',
  `reportType` smallint(6) NOT NULL COMMENT '举报类型 1虚假广告，2色情低俗，3违法违纪，4 咋骗信息',
  `reportComment` varchar(100) DEFAULT '' COMMENT '举报说明',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`reportId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='举报信息表';



