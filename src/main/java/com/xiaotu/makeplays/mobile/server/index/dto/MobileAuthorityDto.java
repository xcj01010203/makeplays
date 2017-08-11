package com.xiaotu.makeplays.mobile.server.index.dto;

public class MobileAuthorityDto {

	/**
	 * 通告单权限
	 */
	private MobileSingleAuthorityDto noticeAuth = new MobileSingleAuthorityDto();
	
	/**
	 * 拍摄进度权限
	 */
	private MobileSingleAuthorityDto shootProgressAuth = new MobileSingleAuthorityDto();
	
	/**
	 * 现场信息权限
	 */
	private MobileSingleAuthorityDto sceneAuth = new MobileSingleAuthorityDto();
	
	/**
	 * 剧组联系表权限
	 */
	private MobileSingleAuthorityDto crewContactAuth = new MobileSingleAuthorityDto(false, true, "", "剧组联系表");
	
	/**
	 * 成员管理权限
	 */
	private MobileSingleAuthorityDto crewSettingAuth = new MobileSingleAuthorityDto();
	
	/**
	 * 财务权限
	 */
	private MobileSingleAuthorityDto financeAuth = new MobileSingleAuthorityDto();
	
	/**
	 * 评价剧组成员权限
	 */
	private MobileSingleAuthorityDto crewUserEvaluateAuth = new MobileSingleAuthorityDto(true, false, "", "评价剧组成员");
	
	/**
	 * 剧本权限
	 */
	private MobileSingleAuthorityDto scenarioAuth = new MobileSingleAuthorityDto();
	
	/**
	 * 堪景权限
	 */
	private MobileSingleAuthorityDto sceneViewAuth = new MobileSingleAuthorityDto();
	
	/**
	 * 堪景权限
	 */
	private MobileSingleAuthorityDto carAuth = new MobileSingleAuthorityDto();
	
	/**
	 * 住宿费用权限
	 */
	private MobileSingleAuthorityDto inhotelCostAuth = new MobileSingleAuthorityDto();

	/**
	 * 餐饮权限
	 */
	private MobileSingleAuthorityDto caterAuth = new MobileSingleAuthorityDto();

	/**
	 * 审批权限
	 */
	private MobileSingleAuthorityDto approvalAuth = new MobileSingleAuthorityDto();
	
	/**
	 * 相册权限
	 */
	private MobileSingleAuthorityDto crewPictureAuth = new MobileSingleAuthorityDto();

	public MobileSingleAuthorityDto getApprovalAuth() {
		return this.approvalAuth;
	}

	public void setApprovalAuth(MobileSingleAuthorityDto approvalAuth) {
		this.approvalAuth = approvalAuth;
	}

	public MobileSingleAuthorityDto getCaterAuth() {
		return this.caterAuth;
	}

	public void setCaterAuth(MobileSingleAuthorityDto caterAuth) {
		this.caterAuth = caterAuth;
	}

	public MobileSingleAuthorityDto getInhotelCostAuth() {
		return this.inhotelCostAuth;
	}

	public void setInhotelCostAuth(MobileSingleAuthorityDto inhotelCostAuth) {
		this.inhotelCostAuth = inhotelCostAuth;
	}

	public MobileSingleAuthorityDto getCarAuth() {
		return this.carAuth;
	}

	public void setCarAuth(MobileSingleAuthorityDto carAuth) {
		this.carAuth = carAuth;
	}

	public MobileSingleAuthorityDto getCrewUserEvaluateAuth() {
		return this.crewUserEvaluateAuth;
	}

	public void setCrewUserEvaluateAuth(
			MobileSingleAuthorityDto crewUserEvaluateAuth) {
		this.crewUserEvaluateAuth = crewUserEvaluateAuth;
	}

	public MobileSingleAuthorityDto getNoticeAuth() {
		return this.noticeAuth;
	}

	public void setNoticeAuth(MobileSingleAuthorityDto noticeAuth) {
		this.noticeAuth = noticeAuth;
	}

	public MobileSingleAuthorityDto getShootProgressAuth() {
		return this.shootProgressAuth;
	}

	public void setShootProgressAuth(MobileSingleAuthorityDto shootProgressAuth) {
		this.shootProgressAuth = shootProgressAuth;
	}

	public MobileSingleAuthorityDto getFinanceAuth() {
		return this.financeAuth;
	}

	public void setFinanceAuth(MobileSingleAuthorityDto financeAuth) {
		this.financeAuth = financeAuth;
	}

	public MobileSingleAuthorityDto getSceneAuth() {
		return this.sceneAuth;
	}

	public void setSceneAuth(MobileSingleAuthorityDto sceneAuth) {
		this.sceneAuth = sceneAuth;
	}

	public MobileSingleAuthorityDto getCrewContactAuth() {
		return this.crewContactAuth;
	}

	public void setCrewContactAuth(MobileSingleAuthorityDto crewContactAuth) {
		this.crewContactAuth = crewContactAuth;
	}

	public MobileSingleAuthorityDto getCrewSettingAuth() {
		return this.crewSettingAuth;
	}

	public void setCrewSettingAuth(MobileSingleAuthorityDto crewSettingAuth) {
		this.crewSettingAuth = crewSettingAuth;
	}

	public MobileSingleAuthorityDto getScenarioAuth() {
		return this.scenarioAuth;
	}

	public void setScenarioAuth(MobileSingleAuthorityDto scenarioAuth) {
		this.scenarioAuth = scenarioAuth;
	}

	public MobileSingleAuthorityDto getSceneViewAuth() {
		return sceneViewAuth;
	}

	public void setSceneViewAuth(MobileSingleAuthorityDto sceneViewAuth) {
		this.sceneViewAuth = sceneViewAuth;
	}

	public MobileSingleAuthorityDto getCrewPictureAuth() {
		return crewPictureAuth;
	}

	public void setCrewPictureAuth(MobileSingleAuthorityDto crewPictureAuth) {
		this.crewPictureAuth = crewPictureAuth;
	}
}
