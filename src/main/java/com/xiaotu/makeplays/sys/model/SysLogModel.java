package com.xiaotu.makeplays.sys.model;

import java.util.Date;

public class SysLogModel {

	
	public static final String TABLE_NAME="tab_sys_log";
	
	private String logId;
	private String userId;
	private String userIp;
	private Integer operType;  //操作类型。0：读；1：插入；2：修改；3：删除；4：导入；,5:导出，99：其他
	private String objectId;
	private String tableName;
	private String authUrl;
	private String params;
	private Date logTime;
	private String logDesc;
	private String logResult;
	private Integer terminal;
	private String crewId;
	private String storePath;	//日志文件存储路径
	private String logFileName;	//日志文件名称
	private Integer projectType; //项目类型
	public String getLogFileName() {
		return this.logFileName;
	}
	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}
	public String getStorePath() {
		return this.storePath;
	}
	public void setStorePath(String storePath) {
		this.storePath = storePath;
	}
	public String getLogId() {
		return logId;
	}
	public void setLogId(String logId) {
		this.logId = logId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Integer getOperType() {
		return operType;
	}
	public void setOperType(Integer operType) {
		this.operType = operType;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getAuthUrl() {
		return authUrl;
	}
	public void setAuthUrl(String authUrl) {
		this.authUrl = authUrl;
	}
	public Date getLogTime() {
		return logTime;
	}
	public void setLogTime(Date logTime) {
		this.logTime = logTime;
	}
	public String getLogDesc() {
		return logDesc;
	}
	public void setLogDesc(String logDesc) {
		this.logDesc = logDesc;
	}
	public String getLogResult() {
		return logResult;
	}
	public void setLogResult(String logResult) {
		this.logResult = logResult;
	}
	public Integer getTerminal() {
		return terminal;
	}
	public void setTerminal(Integer terminal) {
		this.terminal = terminal;
	}
	public String getCrewId() {
		return crewId;
	}
	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public String getUserIp() {
		return userIp;
	}
	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}
	public Integer getProjectType() {
		return projectType;
	}
	public void setProjectType(Integer projectType) {
		this.projectType = projectType;
	}	
}
