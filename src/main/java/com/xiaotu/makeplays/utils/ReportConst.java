package com.xiaotu.makeplays.utils;

public class ReportConst {
	
	public static final String LINK_CHAR_DOUHAO=",";
	
	public static final int STATISTICS_TYPE_SCENE=1;//按场统计
	public static final int STATISTICS_TYPE_PAGE=2;//按页统计
	
	//拍摄状态。0:未完成；1:部分完成；2:完成；3:删戏；',
	/**
	 * 场景状态，未完成
	 */
	public static final int SCENE_STATUS_TYPE_UNFINISH = 0;// 场景状态，未完成
	/**
	 *  场景状态，部分完成
	 */
	public static final int SCENE_STATUS_TYPE_PARTFINISH = 1;// 场景状态，部分完成
	/**
	 *  场景状态，已完成
	 */
	public static final int SCENE_STATUS_TYPE_FINISH = 2;// 场景状态，已完成
	/**
	 * 场景状态，删戏
	 */
	public static final int SCENE_STATUS_TYPE_DELETE = 3;// 场景状态，删戏
	/**
	 * 没有拍摄地点的戏量的标题
	 * =待定
	 */
	public static final String SHOOT_ADDRESS_NULL_TITLE="待定";//没有拍摄地点的戏量的标题
	
	// 费用进度统计类型：一级财务科目（FS-FinanceSubject）
	public static final int STATS_TYPE_COST_FIRST_FS = 1;
	
	// 费用进度统计类型：二级财务科目（FS-FinanceSubject）
	public static final int STATS_TYPE_COST_SECOND_FS = 2;

	// 费用进度统计类型：自定义财务科目（FS-FinanceSubject）
	public static final int STATS_TYPE_COST_USERDEFINE_FS = 0;
	
	// 财务科目（FS-FinanceSubject）级别:一级
	public static final int FS_LEVEL_ONE = 1;
	
	// 财务科目（FS-FinanceSubject）级别:二级
	public static final int FS_LEVEL_TWO = 2;
	
	// 财务科目（FS-FinanceSubject）级别:三级
	public static final int FS_LEVEL_THREE = 3;
	
	// 财务科目（FS-FinanceSubject）级别:四级
	public static final int FS_LEVEL_FOUR = 4;
}
