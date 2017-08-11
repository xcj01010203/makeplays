package com.xiaotu.makeplays.utils;

/**
 * 权限操作编码
 * @author xuchangjian 2016-5-30下午3:05:11
 */
public class AuthorityConstants {
	
	//PC端权限
	/**
	 * 首页
	 */
	public static final String INDEX_PAGE = "pc10001";
	
	/**
	 * 首页财务进度权限
	 */
	public static final String INDEX_FINANCE_SCHEDULE = "pc10002";

	/**
	 * 批量删除场景信息
	 */
	public static final String DELETE_VIEW_BATCH = "pc10003";
	
	/**
	 * 角色表
	 */
	public static final String ROLE_VIEW = "pc10004";
	
	/**
	 * 账务详情
	 */
	public static final String RUNNGING_ACCOUNT = "pc10005";
	
	/**
	 * 修改已结算单据权限
	 */
	public static final String MODIFY_SETTLED_PAYMENT = "pc10006";
	
	/**
	 * 剧组设置
	 */
	public static final String PC_CREW_SETTING = "pc10007";
	
	/**
	 * 	费用预算
	 */
	public static final String PC_FINANCE_BUDGET = "pc10008";

	/**
	 * 	合同管理
	 */
	public static final String PC_FINANCE_CONTRACT = "pc10009";
	
	/**
	 * 批量删除角色
	 */
	public static final String DELETE_VIEWROLE_BATCH = "pc10010";
	
	/**
	 * 堪景功能
	 * 
	 */
	public static final String LOCATION_SEARCH = "pc10011" ;
	
	/**
	 * 筹备期权限
	 */
	public static final String PREPARE = "pc10013";
	
	/**
	 * 剧本分析
	 */
	public static final String SCENARIO_ANALYSE = "pc10014";
	
	/**
	 * 上传剧本
	 */
	public static final String UPLOAD_SCENARIO = "pc10015";
	
	/**
	 * 导出剧本
	 */
	public static final String EXPORT_SCENARIO = "pc10016";
	
	/**
	 * 导出角色表
	 */
	public static final String EXPORT_VIEWROLE = "pc10017";
	
	/**
	 * 场景表
	 */
	public static final String VIEW_INFO = "pc10018";
	
	/**
	 * 导入场景表
	 */
	public static final String IMPORT_VIEWINFO = "pc10019";
	
	/**
	 * 导出场景表
	 */
	public static final String EXPORT_VIEWINFO = "pc10020";
	
	/**
	 * 通告单
	 */
	public static final String NOTICE_INFO = "pc10021";
	
	/**
	 * 剧组联系表
	 */
	public static final String CREW_CONTACT_WEB = "pc10022";
	
	/**
	 * 导入剧组联系表
	 */
	public static final String IMPORT_CREW_CONTACT = "pc10023";
	
	/**
	 * 导出剧组联系表
	 */
	public static final String EXPORT_CREW_CONTACT = "pc10024";
	
	/**
	 * 车辆
	 */
	public static final String CAR_INFO = "pc10025";
	
	/**
	 * 导入车辆
	 */
	public static final String IMPORT_CARINFO = "pc10026";
	
	/**
	 * 导出车辆
	 */
	public static final String EXPORT_CARINFO = "pc10027";
	
	/**
	 * 财务设置
	 */
	public static final String FINANCE_SET = "pc10028";
	
	/**
	 * 导入费用预算
	 */
	public static final String IMPORT_FINANCE_BUDGET = "pc10029";
	
	/**
	 * 导出费用预算
	 */
	public static final String EXPORT_FINANCE_BUDGET = "pc10030";
	
	/**
	 * 导入合同
	 */
	public static final String IMPORT_CONTRACT = "pc10031";
	
	/**
	 * 导出合同
	 */
	public static final String EXPORT_CONTRACT = "pc10032";
	
	/**
	 * 收支管理
	 */
	public static final String GET_COST = "pc10033";
	
	/**
	 * 导入账务详情
	 */
	public static final String IMPORT_FINANCE_DETAIL = "pc10034";
	
	/**
	 * 导出账务详情
	 */
	public static final String EXPORT_FINANCE_DETAIL = "pc10035";
	
	/**
	 * 导出费用结算
	 */
	public static final String EXPORT_SETTLEMENT = "pc10036";
	
	/**
	 * 导出借款详情
	 */
	public static final String EXPORT_LOAN_DETAIL = "pc10037";
	
	/**
	 * 导出场景汇总
	 */
	public static final String EXPORT_VIEW_TOTAL = "pc10038";
	
	/**
	 * 导出分集汇总
	 */
	public static final String EXPORT_SERIES_TOTAL = "pc10039";
	
	/**
	 * 导出拍摄进度
	 */
	public static final String EXPORT_SHOOT_PRODUCE = "pc10040";
	
	/**
	 * 导出住宿费用
	 */
	public static final String EXPORT_INHOTEL_COST = "pc10041";
	
	/**
	 * web端餐饮
	 */
	public static final String PC_CATER = "pc10042";
	
	/**
	 * web端道具表
	 */
	public static final String PC_PROPS = "pc10043";
	
	/**
	 * web端住宿管理 
	 */
	public static final String PC_HOTEL = "pc10044";
	
	/**
	 * web端审批
	 */
	public static final String PC_APPROVAL = "pc10045";
	
	/**
	 * web端剪辑功能
	 */
	public static final String PC_CUTVIEW = "pc10046";
	
	/**
	 * web端剧照管理
	 */
	public static final String PC_CREWPICTURE = "pc10047";
	
	/**
	 * web端日志
	 */
	public static final String PC_CLIP = "pc10048";
	
	/**
	 * 计划
	 */
	public static final String PC_SCHEDULE = "pc10050";
	
	/**
	 * 导入计划
	 */
	public static final String IMPORT_SCHEDULE = "pc10051";
	
	/**
	 * 导出计划
	 */
	public static final String EXPORT_SCHEDULE = "pc10052";
	
	/**
	 * 所有的导入权限
	 */
	public static final String[] IMPORT_AUTHCODE = new String[]{//"UPLOAD_SCENARIO",
		IMPORT_VIEWINFO,IMPORT_CREW_CONTACT,IMPORT_CARINFO,IMPORT_FINANCE_BUDGET,
		IMPORT_CONTRACT,IMPORT_FINANCE_DETAIL,IMPORT_SCHEDULE};
	
	/**
	 * 所有的导出权限
	 */
	public static final String[] EXPORT_AUTHCODE = new String[]{EXPORT_SCENARIO,
		EXPORT_VIEWROLE,EXPORT_VIEWINFO,EXPORT_CREW_CONTACT,EXPORT_CARINFO,
		EXPORT_FINANCE_BUDGET,EXPORT_CONTRACT,EXPORT_FINANCE_DETAIL,
		EXPORT_SETTLEMENT,EXPORT_LOAN_DETAIL,EXPORT_VIEW_TOTAL,
		EXPORT_SERIES_TOTAL,EXPORT_SHOOT_PRODUCE,EXPORT_INHOTEL_COST,EXPORT_SCHEDULE};
	
	
	//移动端权限
	/**
	 * 通告单权限编码
	 */
	public static final String NOTICE = "yd10001";

	/**
	 * 拍摄进度权限编码
	 */
	public static final String SHOOTPROGRESS = "yd10002";

	/**
	 * 现场信息权限编码
	 */
	public static final String SCENE = "yd10003";

	/**
	 * 剧组联系表权限
	 */
	public static final String CREW_CONTACT= "yd10004";
	
	/**
	 * 成员管理权限
	 */
	public static final String CREW_USER_MANAGE = "yd10005";
	
	/**
	 * 财务权限编码
	 */
	public static final String FINANCE = "yd10006";

	/**
	 * 剧组成员评价
	 */
	public static final String CREW_USER_EVALUATE = "yd10007";
	
	/**
	 * 场记单
	 */
	public static final String CLIP = "yd10008";
	
	/**
	 * 剧本
	 */
	public static final String SCENARIO = "yd10009";
	
	/**
	 * 堪景
	 */
	public static final String SCENEVIEW = "yd10010";
	
	/**
	 * 车辆管理
	 */
	public static final String Car = "yd10011";
	
	/**
	 * 住宿费用
	 */
	public static final String INHOTELCOST = "yd10012";
	
	/**
	 * 移动端餐饮
	 */
	public static final String YD_CATER = "yd10013";
	
	/**
	 * 审批
	 */
	public static final String YD_APPROVAL = "yd10014";
	
	/**
	 * 相册
	 */
	public static final String YD_CREWPICTURE = "yd10015";
}
