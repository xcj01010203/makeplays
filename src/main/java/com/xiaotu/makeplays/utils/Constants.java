package com.xiaotu.makeplays.utils;


/**
 * 定义常量类 常量类命名规则： 1.全部大写 2.每个单词之间使用下划线隔开（_） 3.相同用途的不同状态前缀必须相同 4.必须写注释
 * 
 * 该类不再维护
 * 如需常量，请到各个模块下constants包中自定义枚举值
 */
@Deprecated
public class Constants {
	
	/**
	 * session中是否需要验证用户IP地址
	 */
	public static final String NEED_VALID_USERIP = "needValidUserIp";
	
	/**
	 * session重是否需要财务密码
	 */
	public static final String NEED_FINANCE_PWD = "needFinancePwd";

	/**
	 * session中的user对象
	 */
	public static final String SESSION_USER_INFO="user";
	
	/**
	 * session中的role对象
	 */
	public static final String SESSION_ROLE_INFO="role";
	
	/**
	 * session中的crewUser对象
	 */
	public static final String SESSION_CREW_USER_INFO="crewUser";
	
	/**
	 * session中的crewInfo对象
	 */
	public static final String SESSION_CREW_INFO="crewInfo";
	/**
	 * session中的currencyInfo对象集合
	 */
	public static final String SESSION_CURRENCY_INFO_LIST="currencyList";
	/**
	 * session中的财务设置对象
	 */
	public static final String SESSION_FINFNCE_SETTING="financeSetting";
	/**
	 * session中的当前用户所有剧组名称、id
	 */
	public static final String SESSION_CREWINFO_ALL="allCrew";
	/**
	 * session中的菜单集合
	 */
	public static final String SESSION_MENUTREE="menuTree";
	/**
	 * session中的权限集合
	 */
	public static final String SESSION_USER_AUTH_MAP = "userAuthMap";
	
	/**
	 * session中用户类型
	 */
	public static final String SESSION_LOGIN_USER_TYPE = "loginUserType";
	
	/**
	 * session中客服类型
	 */
	public static final String SESSION_LOGIN_SERVICE_TYPE = "loginServiceType";
	
	/**
	 * session中是否获取用户权限标识
	 */
	public static final String SESSION_IFCHECK = "ifcheck";
	
	/**
	 * session中的用户不可以访问的菜单权限集合
	 */
	public static final String AUTH_NOTMENU="authNotMenu";
	/**
	 * session中的是否设置了财务密码
	 */
	public static final String SESSION_FINFNCE_PWD="financePwd";
	/**
	 * session 财务数据是否加密
	 */
	public static final String SESSION_FINFNCE_IFENCODE="financeEncode";
	
	/**
	 * 系统默认剧组id为  0  否
	 */
	public static final String SYS_DEFULT_CREW_ID="0";
	/**
	 * 系统默认剧组id(int 类型)为  0  否
	 */
	public static final int SYS_DEFULT_CREW_IDS=0;
	
	/**
	 * 系统默认剧组id为 1  默认
	 */
	public static final String SYS_DEFULT_CREWID="1";
	/**
	 * 系统默认剧组id(int 类型) 为 1  默认
	 */
	public static final int SYS_DEFULT_CREWIDS=1;
	/**
	 * 用户类型：管理员
	 */
	public static final int USER_TYPE_ADMIN = 1;
	
	/**
	 * 用户类型：非管理员
	 */
	public static final int USER_TYPE_NOT_ADMIN = 0;
	
	/**
	 *  角色ID：剧组管理员
	 */
	public static final String ROLE_ID_ADMIN=new String("1");
	/**
	 *  角色ID：客户服务--总客服
	 */
	public static final String ROLE_ID_CUSTOM_SERVICE=new String("2");
	/**
	 *  角色ID：高级客服
	 */
	public static final String ROLE_ID_SENIOR_CUSTOMERSERVICE=new String("4");
	/**
	 *  角色ID：中级客服	
	 */
	public static final String ROLE_ID_MIDDLE_CUSTOMERSERVICE=new String("5");
	/**
	 *  角色ID：初级客服
	 */
	public static final String ROLE_ID_JUNIOR_CUSTOMERSERVICE=new String("6");
	/**
	 *  角色ID：项目总监
	 */
	public static final String ROLE_ID_PROJECT_DIRECTOR=new String("3");
	/**
	 * 剧组管理员
	 */
	public static final Integer CREW_TYPE_ADMIN=new Integer(1);
	/**
	 * 普通用户
	 */
	public static final Integer CREW_TYPE_NOT_ADMIN=new Integer(0);
	/**
	 * 职员合同类型
	 */
	public static final int WORK_CONTRACT_TYPE=1;
	/**
	 * 演员合同类型
	 */
	public static final int ACTOR_CONTRACT_TYPE=2;
	/**
	 * 制作合同类型
	 */
	public static final int PRODUCE_CONTRACT_TYPE=3;
	/**
	 * 职员合同编号前缀
	 */
	public static final String WORK_CONTRACT_PREFIX="ZY";
	/**
	 * 演员合同编号前缀
	 */
	public static final String ACTOR_CONTRACT_PREFIX="YY";
	/**
	 * 制作合同编号前缀
	 */
	public static final String PRODUCE_CONTRACT_PREFIX="ZZ";
	/**
	 * 薪酬已结算
	 */
	public static final int SETTLE_STATUS=1; 
	/**
	 * 用户剧组状态为1正常
	 */
	public static final int  STATUS_OK=1;
	/**
	 * 用户剧组状态为99冻结
	 */
	public static final int  STATUS_NO=99;
	/**
	 * 剧本中的格式正则表达式
	 */
	//每一集的开头（示例：第一集/第1集）
	public static final String REGEX_SERIES = "^(\\t|\\?|"+(char)65279+"| |　|\\[|【)*\u7b2c.+\u96c6";
	
	public static final String REGEX_CN_NUMBER_START = "(一|二|三|四|五|六|七|八|九|十)+";
	
	//每一场的标题匹配正则表达式(示例:1-1，山豹旅战术训练场，营区，日/外)
	public static final String REGEX_VIEW_TITLE = "^\\d+(-|－|——)\\d((,|，)[\u4e00-\u9fa5]+)+(,|，)([\u4e00-\u9fa5](/|／)[\u4e00-\u9fa5])$";
	
	//场景主标题(集-场之间不带空格)
	public static final String REGEX_VIEW_MAIN_TITLE_WITHOUT_SPACE = "^\\d+(-|－|——|\\.)";
	
	//场景主标题（集-场之间带空格）
	public static final String REGEX_VIEW_MAIN_TITLE_WITH_SPACE = "^\\d( )*(-|－|——|\\.)( )*";
	
	//场景集场号分隔符
	public static final String REGEX_VIEW_TITLE_SEPRATOR = "(-|－|——|\\.)";
	
	//空白字符正则表达式(用于把字符串中空格全部去掉)
	public static final String REGEX_SPACE_CHAR = "(\\s|　)+";
	
	//空格、逗号和冒号组成正则表达式(剧本中每集场的标题就是利用这些字符进行元素分割的)
	public static final String REGEX_OTHER_CHAR = "(\\s|　|,|，|:|：|、)";
	
	//子标题的分隔符（示例：人物：甫光，马五，特务，司机）
	public static final String REGEX_SUBTITLE_SPLIT_CHAR = "(:|：)";
	
	//子标题中任务的分隔符（甫光，马五，特务、司机）
	public static final String REGEX_TITLE_FIGURE_SPLIT_CHAR = "(-|－|/|／|,|，|、| )+";
	
	//标题中场景的分隔符
	public static final String REGEX_TITLE_VIEWLOCATION_SPLIT_CHAR = "(-|－|/|／|,|，|、| )";
	
	//左中括号分隔符
	public static final String REGEX_LEFT_BRACKETS = "(\\[|【)";

	//右中括号分隔符
	public static final String REGEX_RIGHT_BRACKETS = "(\\]|】)";

	//右中括号分隔符
	public static final String REGEX_RIGHT_SLASH = "(/|／)";
	
	//汉字的正则表达式
	public static final String REGEX_CHINESE_WORD = "[\u4e00-\u9fa5]";
	
	//日期的正则表达式
	public static final String REGEX_DATE_TYPE = "\\d月\\d日*";
	
	//手机号的正则表达式
	public static final String REGEX_PHONE_NUMBER = "^\\d{11}$";
	
	//身份证正则表达式
	public static final String REGEX_IDENDIFY = "^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$";
	
	//年龄正则表达式0-120
	public static final String REGEX_AGE = "^(?:[1-9][0-9]?|1[01][0-9]|120)$";
	
	//邮箱正则表达式
	public static final String REGEX_EMAIL = "\\w+(\\.\\w+)*@\\w+(\\.\\w+)+";
	//获取括号中的内容 例如：预算(rmb) 获取到rmb
	public static final String REGEX_EXCEL_TITLE="[（ (](.*?)[) ）]";
	//获取括号前的内容 例如：预算(rmb) 获取到预算
	public static final String REGEX_EXCEL = "(.*)[(,（]";
	/**
	 * 剧本中的字符串常量
	 */
	public static final String FIGURE = "人物";
	
	public static final String[] INNERSITEARRAY = new String[]{"内", "内景"};
	
	public static final String[] OUTERSITEARRAY = new String[]{"外", "外景"};
	
	public static final String[] INOUTSITEARRAY = new String[]{"内外", "内景"};
	
	public static final String INNERSITE = "内";
	
	public static final String OUTERSITE = "外";
	
	public static final String INOUTSITE = "内外";
	
	public static final String LITERATE = "文";
	
	public static final String KUNGFU = "武";
	
	public static final String MIXED = "文武";
	
	public static final String SPRING = "春";
	public static final String SUMMER = "夏";
	public static final String AUTUMN = "秋";
	public static final String WINTER = "冬";
	
	//剧本每场标准标题中最少的元素数量（1-1，山豹旅战术训练场，日/外）
	public static final int TITLE_ATLEAST_MEMBER_COUNT = 3;
	
	/**
	 * 文件流常量
	 */
	//文本缓冲区大小
	public static final int CONTENT_BUFFER_SIZE = 1024;
	
	
	/**
	 * 操作终端类型：电脑
	 */
	public static final Integer TERMINAL_PC=new Integer(0);
	
	/**
	 * 操作终端类型：IOS
	 */
	public static final Integer TERMINAL_IOS=new Integer(1); 
	
	/**
	 * 操作终端类型：android
	 */
	public static final Integer TERMINAL_ANDROID=new Integer(2); 
	
	
	/**
	 * 是否为菜单节点：1：是
	 */
	public static final Integer IF_MENU_YES=new Integer(1); 
	
	/**
	 * 是否为菜单节点：0：不是
	 */
	public static final Integer IF_MENU_NO=new Integer(0); 
	
	/**
	 * 币种CODE：人民币
	 */
	public static final String MONEY_CODE_CNY = "CNY";
	/**
	 * 币种不可用	
	 */
	public static final int CURRENCY_DISABLE=0;
	/**
	 * 币种可用
	 */
	public static final int CURRENCY_ENABLE=1;
	/**
	 * 本位币
	 */
	public static final int CURRENCY_STANDARD=1;
	/**
	 * 不是本位币
	 */
	public static final int CURRENCY_NOT_STANDARD=0;
	
	/**
	 * 场景类型：主场景
	 */
	public static final Integer VIEW_ADDRESS_MAJOR=new Integer(1);
	
	/**
	 * 场景类型：次场景
	 */
	public static final Integer VIEW_ADDRESS_MINOR=new Integer(2);
	
	/**
	 * 场景类型：三级场景
	 */
	public static final Integer VIEW_ADDRESS_THIRD_LEVEL=new Integer(3);
	
	/**
	 * 场景道具类型：普通道具
	 */
	public static final Integer VIEW_PROPS_TYPE_ORDINARY=new Integer(0);
	
	/**
	 * 场景道具类型：特殊道具
	 */
	public static final Integer VIEW_PROPS_TYPE_SPECIAL=new Integer(1);
	
	/**
	 * 已结算
	 */
	public static final int SETTLE_STATUS_SETTLED=1;//已结算
	/**
	 * 未结算
	 */
	public static final int SETTLE_STATUS_UNSETTLE=0;//未结算

	/**
	 * 公告状态
	 */
	public static final int BULLETIN_STATUS_DRAFT = 0;	//草稿
	public static final int BULLETIN_STATUS_RELEASE = 1;	//发布
	public static final int BUULETIN_STATUS_DELETE = 2;	//废弃
	
	/**
	 * 公告操作类型
	 */
	public static final int BULLETIN_PERMISSIONTYPE_READ = 1;	//只读操作
	public static final int BULLETIN_PERMISSIONTYPE_WRITE = 2;	//读写操作
	
	/**
	 * 公告保存、发布的操作标识
	 */
	public static final int BULLETIN_OPERATETYPE_SAVE = 1;	//保存
	public static final int BULLETIN_OPERATETYPE_PUBLISH = 2;	//发布
	
	/**
	 * 票据编号基数　
	 */
	public static final long BILL_BASE_NUM = 10000000l;
		
	/**
	 * 票据编号付款前缀 
	 */
	public static final String BILL_FK = "FK";
		
	/**
	 * 票据编号收款前缀 
	 */
	public static final String BILL_SK = "SK";
		
	/**
	 * 票据编号借款前缀 
	 */
	public static final String BILL_JK = "JK";
	
	/**
	 * 拍摄计划类型
	 */
	//宏观计划
	public static final int  PLAN_TYPE_BIG = 1;
	//详细计划
	public static final int PLAN_TYPE_SMALL = 2;
	
	
	/**
	 * 默认剧组分组：未分组
	 */
	public static final String SHOOT_GROUP_ID_DEFAULT="1";
	
	/**
	 * 通联表默认全组
	 */
	public static final String CONTACT_GROUP_ALL = "全组";
	
	/**
	 * 用户状态
	 */
	//有效
	public static final int USER_STATUS_VALID = 1;
	//无效
	public static final int USER_STATUS_INVALID = 2;
	
	/**
	 * bean名称
	 */
	public static final String USERSERVICE = "userService";
	public static final String CREWSERVICE = "crewInfoService";
	
	/**
	 * 短信验证码有效时间
	 */
	public static final String VALIDTIME = "1";
	public static final long VALIDTIMESTAMP = 60000l;
	
	/**
	 * 短信验证码默认模板
	 */
	public static final String VALIDMODEL = "44650";
	
	/**
	 * url地址分隔符
	 */
	public static final String URL_SEPARACTOR = "/";// 
	
	/**
	 * 手机端每页数量
	 */
	public static final Integer MOBILE_PAGE_NUMBER = 5;
	
	/**
	 * 剧组设置URL
	 */
	public static final String CREW_SET__URL = "/crewManager/crewList";
	/**
	 * 费用预算URL
	 */
	public static final String FINANCE_BUDGET_URL = "/budget/getBudgetList";
	/**
	 * 收支管理URL
	 */
	public static final String FINANCE_INVOICE_URL = "/invoice/enterBill";
	/**
	 * 费用结算URL
	 */
	public static final String FINANCE_BALANCE_URL = "/balance/goBalance";
	/**
	 * 账务详情URL
	 */
	public static final String FINANCE_JOURNAL_URL = "/journal/journalManage";
	/**
	 * 合同管理URL
	 */
	public static final String FINANCE_CONSTRACT_URL = "/contract/contractManage";
	/**
	 * 借款详情URL
	 */
	public static final String FINANCE_LOAN_URL = "/loanDetail/goLoanDetail";
	
	/**
	 * 未读消息
	 */
	public static final Integer MESSAGE_UNREAD=new Integer(0);
	
	/**
	 * 已读消息
	 */
	public static final Integer MESSAGE_READ=new Integer(1);
	
	/**
	 * 已处理读消息
	 */
	public static final Integer MESSAGE_HANDLE=new Integer(2);
	
	/**
	 *剧本中元素ID
	 */
	public static final String SCRIPTELE_SERIESNO = "e1";		//集
	public static final String SCRIPTELE_VIEWNO = "e2";			//场
	public static final String SCRIPTELE_VIEWLOCATION = "e3";	//场景
	public static final String SCRIPTELE_ATMOSPHERE = "e4";		//气氛
	public static final String SCRIPTELE_SITE = "e5";			//内外景
	public static final String SCRIPTELE_SEASON = "e6";			//季节
	public static final String SCRIPTELE_FIGURE = "e7";			//人物
	
	
	/**
	 * 剧本中符号ID
	 */
	public static final String SEPARATOR_COMMON = "s0";	//换行符
	public static final String SEPARATOR_LINE = "s1";	//换行符
	
	
	/**
	 * 待评价人类型
	 */
	public static final Integer TO_EVALUATE_PERSON_TYPE_ACTOR = 1;	//艺人
	public static final Integer TO_EVALUATE_PERSON_TYPE_USER = 2;	//主创人
	
	/**
	 * 评价状态
	 */
	public static final Integer EVALUATE_STATUS_CREATE = 0;		//创建
	public static final Integer EVALUATE_STATUS_FINISH = 1;	//评价完成
	
	/**
	 * 权限平台类型
	 */
	public static final Integer AUTHORITY_PLANTFORM_COMMON = 1;	//通用
	public static final Integer AUTHORITY_PLANTFORM_PC = 2;	//PC端
	public static final Integer AUTHORITY_PLANFORM_YD = 3;	//移动端
	
	/**
	 * 剧本导出方式
	 */
	public static final Integer SCENARIO_EXPORT_TYPE_ALL = 1;	//整剧本导出
	public static final Integer SCENARIO_EXPORT_TYPE_SINGLE = 2;	//分集导出
	
	/**
	 * 手机端通告单每页显示条数
	 */
	public static final Integer MOBILE_NOTICE_PER_PAGENUM = 10;
	
	
	/**
	 * 移动端客户端类型
	 */
	public static final Integer MOBILE_CLIENTTYPE_IPHONE = 1;	//iphone
	public static final Integer MOBILE_CLIENTTYPE_ANDROID = 2;	//android
	public static final Integer MOBILE_CLIENTTYPE_IPAD = 3;	//ipad
	
	/**
	 * 通告单push消息反馈状态
	 */
	public static final Integer NOTICE_FEDBACK_STATUS_NOTYET = 1;	//未反馈
	public static final Integer NOTICE_FEDBACK_STATUS_RECEIVED = 2;	//已收到
	public static final Integer NOTICE_FEDBACK_STATUS_BACKED = 3;	//已反馈
	
	/**
	 * 通告单反馈信息是否满意
	 */
	public static final Integer NOTICE_FEDBACK_SATISFIED = 1;	//满意
	public static final Integer NOTICE_FEDBACK_NOTSATISFIED = 0;	//不满意
	
	/**
	 * 默认的用户头像
	 */
	public static final String DEFAULT_USER_PIC = "/images/user_default_img.png";
		
	/**
	 * cookie中的用户登录时间
	 */
	public static final String COOKIE_USER_LOGINTIME = "loginTime";
}
