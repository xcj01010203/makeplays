package com.xiaotu.makeplays.crew.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.approval.model.ApprovalInfoModel;
import com.xiaotu.makeplays.approval.model.ReceiptInfoModel;
import com.xiaotu.makeplays.attachment.model.constants.AttachmentBuzType;
import com.xiaotu.makeplays.authority.model.CrewAuthMapModel;
import com.xiaotu.makeplays.authority.model.UserAuthMapModel;
import com.xiaotu.makeplays.bulletin.model.BulletinInfoModel;
import com.xiaotu.makeplays.car.model.CarInfoModel;
import com.xiaotu.makeplays.car.model.CarWorkModel;
import com.xiaotu.makeplays.cater.model.CaterInfoModel;
import com.xiaotu.makeplays.cater.model.CaterMoneyInfoModel;
import com.xiaotu.makeplays.crew.dao.CrewClearDao;
import com.xiaotu.makeplays.crew.dao.CrewInfoDao;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.model.CrewRoleUserMapModel;
import com.xiaotu.makeplays.crew.model.CrewUserMapModel;
import com.xiaotu.makeplays.crew.model.FinanceAccountGroupMapModel;
import com.xiaotu.makeplays.crew.model.FinanceAccountGroupModel;
import com.xiaotu.makeplays.crewPicture.model.CrewPictureInfoModel;
import com.xiaotu.makeplays.cutview.model.CutViewInfoModel;
import com.xiaotu.makeplays.finance.model.AccoFinacSubjMapModel;
import com.xiaotu.makeplays.finance.model.AccountSubjectModel;
import com.xiaotu.makeplays.finance.model.CollectionInfoModel;
import com.xiaotu.makeplays.finance.model.ContractActorModel;
import com.xiaotu.makeplays.finance.model.ContractMonthPayDetailModel;
import com.xiaotu.makeplays.finance.model.ContractMonthPaywayModel;
import com.xiaotu.makeplays.finance.model.ContractProduceModel;
import com.xiaotu.makeplays.finance.model.ContractStagePayWayModel;
import com.xiaotu.makeplays.finance.model.ContractToPaidModel;
import com.xiaotu.makeplays.finance.model.ContractWorkerModel;
import com.xiaotu.makeplays.finance.model.CurrencyInfoModel;
import com.xiaotu.makeplays.finance.model.FinanSubjCurrencyMapModel;
import com.xiaotu.makeplays.finance.model.FinancePaymentWayModel;
import com.xiaotu.makeplays.finance.model.FinanceSettingModel;
import com.xiaotu.makeplays.finance.model.FinanceSubjectModel;
import com.xiaotu.makeplays.finance.model.LoanInfoModel;
import com.xiaotu.makeplays.finance.model.PaymentFinanSubjMapModel;
import com.xiaotu.makeplays.finance.model.PaymentInfoModel;
import com.xiaotu.makeplays.goods.model.GoodsInfoModel;
import com.xiaotu.makeplays.goods.model.ViewGoodsInfoMap;
import com.xiaotu.makeplays.locationsearch.model.SceneViewInfoModel;
import com.xiaotu.makeplays.message.model.MessageInfoModel;
import com.xiaotu.makeplays.notice.model.ConvertAddressModel;
import com.xiaotu.makeplays.notice.model.NoticeInfoModel;
import com.xiaotu.makeplays.notice.model.NoticePictureModel;
import com.xiaotu.makeplays.notice.model.NoticePushFedBackModel;
import com.xiaotu.makeplays.notice.model.NoticeTimeModel;
import com.xiaotu.makeplays.notice.model.ViewNoticeMapModel;
import com.xiaotu.makeplays.notice.model.clip.ClipCommentModel;
import com.xiaotu.makeplays.notice.model.clip.ClipPropModel;
import com.xiaotu.makeplays.notice.model.clip.DepartmentEvaluateModel;
import com.xiaotu.makeplays.notice.model.clip.LiveConvertAddModel;
import com.xiaotu.makeplays.notice.model.clip.RoleAttendanceModel;
import com.xiaotu.makeplays.notice.model.clip.ShootAuditionModel;
import com.xiaotu.makeplays.notice.model.clip.ShootLiveModel;
import com.xiaotu.makeplays.notice.model.clip.TmpCancelViewInfoModel;
import com.xiaotu.makeplays.prepare.model.PrepareArteffectLocationModel;
import com.xiaotu.makeplays.prepare.model.PrepareArteffectRoleModel;
import com.xiaotu.makeplays.prepare.model.PrepareCrewPeopleModel;
import com.xiaotu.makeplays.prepare.model.PrepareExtensionModel;
import com.xiaotu.makeplays.prepare.model.PrepareOperateModel;
import com.xiaotu.makeplays.prepare.model.PrepareRoleModel;
import com.xiaotu.makeplays.prepare.model.PrepareScriptModel;
import com.xiaotu.makeplays.prepare.model.PrepareWorkModel;
import com.xiaotu.makeplays.roleactor.model.ActorInfoModel;
import com.xiaotu.makeplays.roleactor.model.ActorLeaveRecordModel;
import com.xiaotu.makeplays.roleactor.model.ActorRoleMapModel;
import com.xiaotu.makeplays.roleactor.model.EvaluateInfoModel;
import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;
import com.xiaotu.makeplays.scenario.model.BookMarkModel;
import com.xiaotu.makeplays.scenario.model.ScenarioInfoModel;
import com.xiaotu.makeplays.shoot.model.ShootGroupModel;
import com.xiaotu.makeplays.shoot.model.ShootLogModel;
import com.xiaotu.makeplays.shoot.model.ShootPlanModel;
import com.xiaotu.makeplays.shoot.model.ViewPlanMapModel;
import com.xiaotu.makeplays.sysrole.model.UserRoleMapModel;
import com.xiaotu.makeplays.user.model.ContactSysroleMapModel;
import com.xiaotu.makeplays.user.model.CrewContactModel;
import com.xiaotu.makeplays.user.model.JoinCrewApplyMsgModel;
import com.xiaotu.makeplays.user.model.UserFocusRoleMapModel;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.view.model.AtmosphereInfoModel;
import com.xiaotu.makeplays.view.model.HistoryViewContentModel;
import com.xiaotu.makeplays.view.model.InsideAdvertModel;
import com.xiaotu.makeplays.view.model.ViewAdvertMapModel;
import com.xiaotu.makeplays.view.model.ViewContentModel;
import com.xiaotu.makeplays.view.model.ViewInfoModel;
import com.xiaotu.makeplays.view.model.ViewLocationMapModel;
import com.xiaotu.makeplays.view.model.ViewLocationModel;
import com.xiaotu.makeplays.view.model.ViewRoleMapModel;

/**
 * @类名：CrewClearService.java
 * @作者：李晓平
 * @时间：2016年10月31日 上午9:10:55 
 * @描述：剧组清除
 */
@Service
public class CrewClearService {
	
	@Autowired
	private CrewClearDao crewClearDao;
	
	@Autowired
	private CrewInfoService crewInfoService;
	
	@Autowired
	private CrewInfoDao crewInfoDao;
	
	/**
	 * 查询剧组信息记录数
	 * @param crewId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> queryCrewInfoNum(String crewId) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		//剧本
		result.put("scenarioNum", this.crewClearDao.queryRecordNum(crewId, "crewId", ViewContentModel.TABLE_NAME));
		//场景
		result.put("viewNum", this.crewClearDao.queryRecordNum(crewId, "crewId", ViewInfoModel.TABLE_NAME));
		//角色
		result.put("viewRoleNum", this.crewClearDao.queryRecordNum(crewId, "crewId", ViewRoleModel.TABLE_NAME));
		//通告单
		result.put("noticeNum", this.crewClearDao.queryRecordNum(crewId, "crewId", NoticeInfoModel.TABLE_NAME));
		//日志
		result.put("shootLogNum", this.crewClearDao.queryRecordNum(crewId, "crewId", ShootLogModel.TABLE_NAME));
		//计划
		result.put("shootPlanNum", this.crewClearDao.queryRecordNum(crewId, "crewId", ShootPlanModel.TABLE_NAME));
		//车辆
		result.put("carNum", this.crewClearDao.queryRecordNum(crewId, "crewId", CarInfoModel.TABLE_NAME));
		//堪景
		result.put("sceneViewNum", this.crewClearDao.queryRecordNum(crewId, "crewId", SceneViewInfoModel.TABLE_NAME));
		//剧组联系表
		result.put("crewContactNum", this.crewClearDao.queryRecordNum(crewId, "crewId", CrewContactModel.TABLE_NAME));
		//住宿信息
		result.put("inHotelNum", this.crewClearDao.queryRecordNumForInHotel(crewId));
		//剧照
		result.put("crewPictureNum", this.crewClearDao.queryRecordNum(crewId, "crewId", CrewPictureInfoModel.TABLE_NAME));
		//收款单
		result.put("collectionNum", this.crewClearDao.queryRecordNum(crewId, "crewId", CollectionInfoModel.TABLE_NAME));
		//付款单
		result.put("paymentNum", this.crewClearDao.queryRecordNum(crewId, "crewId", PaymentInfoModel.TABLE_NAME));
		//借款单
		result.put("loanNum", this.crewClearDao.queryRecordNum(crewId, "crewId", LoanInfoModel.TABLE_NAME));
		//演员合同
		result.put("contractActorNum", this.crewClearDao.queryRecordNum(crewId, "crewId", ContractActorModel.TABLE_NAME));
		//职员合同
		result.put("contractWorkerNum", this.crewClearDao.queryRecordNum(crewId, "crewId", ContractWorkerModel.TABLE_NAME));
		//制作合同
		result.put("contractProduceNum", this.crewClearDao.queryRecordNum(crewId, "crewId", ContractProduceModel.TABLE_NAME));
		//合同待付
		result.put("contractToPaidNum", this.crewClearDao.queryRecordNum(crewId, "crewId", ContractToPaidModel.TABLE_NAME));
		//审批
		result.put("receiptNum", this.crewClearDao.queryRecordNum(crewId, "crewId", ReceiptInfoModel.TABLE_NAME));
		//预算
		result.put("financeNum", this.crewClearDao.queryRecordNum(crewId, "crewId", FinanSubjCurrencyMapModel.TABLE_NAME));
		//财务密码
		Map<String, Object> map = this.crewClearDao.queryFinancePassword(crewId);
		if(map == null || map.isEmpty()) {
			result.put("financePassword", null);
		} else {
			result.put("financePassword", map.get("pwdStatus"));
		}
		//用户
		result.put("crewUserNum", this.crewClearDao.queryRecordNumForCrewUser(crewId));		
		//申请入组信息
		result.put("joinCrewApplyNum", this.crewClearDao.queryRecordNum(crewId, "aimCrewId", JoinCrewApplyMsgModel.TABLE_NAME));
		//餐饮
		result.put("caterNum", this.crewClearDao.queryRecordNum(crewId, "crewId", CaterInfoModel.TABLE_NAME));
		//剧本进度
		result.put("prepareScriptNum", this.crewClearDao.queryRecordNum(crewId, "crewId", PrepareScriptModel.TABLE_NAME));
		//选角进度
		result.put("prepareRoleNum", this.crewClearDao.queryRecordNum(crewId, "crewId", PrepareRoleModel.TABLE_NAME));
		//剧组人员
		result.put("prepareCrewPeopleNum", this.crewClearDao.queryRecordNum(crewId, "crewId", PrepareCrewPeopleModel.TABLE_NAME));
		//美术视觉-角色
		result.put("prepareArteffectRoleNum", this.crewClearDao.queryRecordNum(crewId, "crewId", PrepareArteffectRoleModel.TABLE_NAME));
		//美术视觉-场景
		result.put("prepareArteffectLocationNum", this.crewClearDao.queryRecordNum(crewId, "crewId", PrepareArteffectLocationModel.TABLE_NAME));		
		//宣传进度
		result.put("prepareExtensionNum", this.crewClearDao.queryRecordNum(crewId, "crewId", PrepareExtensionModel.TABLE_NAME));
		//办公筹备
		result.put("prepareWorkNum", this.crewClearDao.queryRecordNum(crewId, "crewId", PrepareWorkModel.TABLE_NAME));
		//商务运营
		result.put("prepareOperateNum", this.crewClearDao.queryRecordNum(crewId, "crewId", PrepareOperateModel.TABLE_NAME));
		//剪辑
		result.put("cutViewNum", this.crewClearDao.queryRecordNum(crewId, "crewId", CutViewInfoModel.TABLE_NAME));
		return result;
	}
		
	/**
	 * 清除剧组数据
	 * @param crewId
	 * @throws Exception
	 */
	public void clearCrewInfo(String crewId, String infoIds) throws Exception {
		if(StringUtils.isNotBlank(infoIds)) {
			String[] infoIdArray = infoIds.split(",");
			for(String str : infoIdArray) {
				if(StringUtils.isNotBlank(str)) {
					if(str.equals("1")) {//删除拍摄生产数据
						this.deleteShootProduceData(crewId);
					} else if(str.equals("2")) {//删除剧组联系表
						this.deleteCrewContact(crewId);
					} else if(str.equals("3")) {//删除费用收支数据
						this.deleteExpenses(crewId);
					} else if(str.equals("4")) {//删除合同数据
						this.deleteContract(crewId);
					} else if(str.equals("5")) {//删除预算数据
						this.deleteFinanceInfo(crewId);
					} else if(str.equals("6")) {//财务密码
						this.clearFinancePassword(crewId);
					} else if(str.equals("7")) {//用户数据
						this.deleteUser(crewId);
					} else if(str.equals("8")) {//车辆
						this.deleteCarInfo(crewId);
					} else if(str.equals("9")) {//堪景
						this.deleteSceneView(crewId);
					} else if(str.equals("10")) {//住宿
						this.deleteInHotel(crewId);
					} else if(str.equals("11")) {//餐饮
						this.deleteCater(crewId);
					} else if(str.equals("12")) {//筹备进展
						this.deletePrepare(crewId);
					} else if(str.equals("13")) {//审批
						this.deleteReceiptInfo(crewId);
					}
				}
			}
		}
	}
		
	/**
	 * 删除剧组
	 * @param crewId
	 * @throws Exception
	 */
	public void deleteCrewInfo(String crewId) throws Exception {
		//我的消息
		this.crewClearDao.deleteOne(crewId, "crewId", MessageInfoModel.TABLE_NAME);
		//消息公告
		this.crewClearDao.deleteOne(crewId, "crewId", BulletinInfoModel.TABLE_NAME);
		//财务设置表
		this.crewClearDao.deleteOne(crewId, "crewId", FinanceSettingModel.TABLE_NAME);
		//货币信息表
		this.crewClearDao.deleteOne(crewId, "crewId", CurrencyInfoModel.TABLE_NAME);
		//删除财务科目信息表
		this.crewClearDao.deleteOne(crewId, "crewId", FinanceSubjectModel.TABLE_NAME);
		//申请入组信息
		this.crewClearDao.deleteOne(crewId, "aimCrewId", JoinCrewApplyMsgModel.TABLE_NAME);
		//剧组用户关联关系
		this.crewClearDao.deleteOne(crewId, "crewId", CrewUserMapModel.TABLE_NAME);
		//剧组权限关联关系
		this.crewClearDao.deleteOne(crewId, "crewId", CrewAuthMapModel.TABLE_NAME);
		//删除剧组图片
		CrewInfoModel crewInfo = this.crewInfoDao.queryById(crewId);
		if(StringUtil.isNotBlank(crewInfo.getPicPath())) {
			FileUtils.deleteFile(crewInfo.getPicPath()); 
		}
		//删除剧组
		this.crewClearDao.deleteOne(crewId, "crewId", CrewInfoModel.TABLE_NAME);
	}
	
	/**
	 * 清空试用剧组数据
	 * @throws Exception
	 */
	public void clearTrialCrewData() throws Exception {
		//查询所有的试用剧组
		List<CrewInfoModel> crewList = this.crewInfoService.queryAllTrialCrew();
		if(crewList != null && crewList.size() > 0) {
			//遍历剧组，清空剧组数据
			for(CrewInfoModel crewInfo : crewList) {
				String crewId = crewInfo.getCrewId();
				//删除拍摄生产数据
				this.deleteShootProduceData(crewId);
				//删除剧组联系表
				this.deleteCrewContact(crewId);
				//删除费用收支数据
				this.deleteExpenses(crewId);
				//删除合同数据
				this.deleteContract(crewId);
				//删除预算数据
				this.deleteFinanceInfo(crewId);
				//财务密码
				this.clearFinancePassword(crewId);
				//用户数据
//				this.deleteUser(crewId);
				//车辆
				this.deleteCarInfo(crewId);
				//堪景
				this.deleteSceneView(crewId);
				//住宿
				this.deleteInHotel(crewId);
				//餐饮
				this.deleteCater(crewId);
				//筹备进展
				this.deletePrepare(crewId);
				//审批
				this.deleteReceiptInfo(crewId);
			}
		}
	}

	/**
	 * 删除拍摄生产数据，包括剧本，场景表，角色表，通告单，日志，计划
	 * @param crewId
	 */
	public void deleteShootProduceData(String crewId) throws Exception{
		/**** 剧本 ****/
		//删除剧本文件
		List<Map<String, Object>> scenarioList = this.crewClearDao.queryScenarioInfoByCrewId(crewId);
		if(scenarioList != null && scenarioList.size() > 0) {
			for(Map<String, Object> scenarioMap : scenarioList) {
				//剧本存放地址
				String scenarioUrl = scenarioMap.get("scenarioUrl") + "";
				if (!StringUtils.isBlank(scenarioUrl)) {
					FileUtils.deleteFile(scenarioUrl);
				}
			}
		}
		//剧本信息表
		this.crewClearDao.deleteOne(crewId, "crewId", ScenarioInfoModel.TABLE_NAME);
		
		/**** 场景、角色 ****/
		//场景信息表
		this.crewClearDao.deleteOne(crewId, "crewId", ViewInfoModel.TABLE_NAME);
		//场景内容信息表
		this.crewClearDao.deleteOne(crewId, "crewId", ViewContentModel.TABLE_NAME);
		//场景与场景地点关联信息表
		this.crewClearDao.deleteOne(crewId, "crewId", ViewLocationMapModel.TABLE_NAME);
		//场景与场景演员角色关联关系
		this.crewClearDao.deleteOne(crewId, "crewId", ViewRoleMapModel.TABLE_NAME);
		//场景与植入广告关联关系
		this.crewClearDao.deleteOne(crewId, "crewId", ViewAdvertMapModel.TABLE_NAME);
		//场景气氛基本信息表
		this.crewClearDao.deleteOne(crewId, "crewId", AtmosphereInfoModel.TABLE_NAME);
		//场景地点信息表(主场景、次场景、三级场景)
		this.crewClearDao.deleteOne(crewId, "crewId", ViewLocationModel.TABLE_NAME);
		//物品基本信息表
		this.crewClearDao.deleteOne(crewId, "crewId", GoodsInfoModel.TABLE_NAME);
		//物品与场景关联关系
		this.crewClearDao.deleteOne(crewId, "crewId", ViewGoodsInfoMap.TABLE_NAME);
		//拍摄地点信息表 ??
//		this.crewClearDao.deleteOne(crewId, "crewId", SceneViewInfoModel.TABLE_NAME);
		//植入广告基本信息表
		this.crewClearDao.deleteOne(crewId, "crewId", InsideAdvertModel.TABLE_NAME);
		//书签表
		this.crewClearDao.deleteOne(crewId, "crewId", BookMarkModel.TABLE_NAME);
		//剧组行页数信息表
		this.crewClearDao.deleteOne(crewId, "crewId", ScenarioInfoModel.TABLE_NAME);
		//场景内容历史记录
		this.crewClearDao.deleteOne(crewId, "crewId", HistoryViewContentModel.TABLE_NAME);
		//演职员评价与标签关联关系表
		this.crewClearDao.deleteEvaluateTagMapByCrewId(crewId);
		//演职员评价信息表
		this.crewClearDao.deleteOne(crewId, "crewId", EvaluateInfoModel.TABLE_NAME);
		//场景角色信息表
		this.crewClearDao.deleteOne(crewId, "crewId", ViewRoleModel.TABLE_NAME);
		//演员与场景角色关联信息表
		this.crewClearDao.deleteOne(crewId, "crewId", ActorRoleMapModel.TABLE_NAME);
		//场景角色与用户关联表
		this.crewClearDao.deleteOne(crewId, "crewId", CrewRoleUserMapModel.TABLE_NAME);
		//剧组联系表和系统角色关联表
		this.crewClearDao.deleteOne(crewId, "crewId", ContactSysroleMapModel.TABLE_NAME);
		//演员基本信息表
		this.crewClearDao.deleteOne(crewId, "crewId", ActorInfoModel.TABLE_NAME);
		//演员请假记录表
		this.crewClearDao.deleteOne(crewId, "crewId", ActorLeaveRecordModel.TABLE_NAME);
		//用户和想要关注的演员的关联关系
		this.crewClearDao.deleteOne(crewId, "crewId", UserFocusRoleMapModel.TABLE_NAME);
		
		/**** 通告单 ****/
		//通告单角色化妆时间
		this.crewClearDao.deleteNoticeRoleTimeByCrewId(crewId);
		//场景与通告单关联信息表
		this.crewClearDao.deleteOne(crewId, "crewId", ViewNoticeMapModel.TABLE_NAME);
		//通告单时间和通联表关联关系表
//		this.crewClearDao.deleteOne(crewId, "crewId", NoticeUserMapModel.TABLE_NAME);
		//通告单反馈信息
		this.crewClearDao.deleteOne(crewId, "crewId", NoticePushFedBackModel.TABLE_NAME);
		//删除通告单预览图片 文件
		List<Map<String, Object>> picList = this.crewClearDao.queryNoticePictureByCrewId(crewId);
		if(picList != null && picList.size() > 0) {
			for(Map<String, Object> picMap : picList) {
				//大图片存储路径
				String bigPicurl = picMap.get("bigPicurl") + "";
				//小图片存储路径
				String smallPicurl = picMap.get("smallPicurl") + "";
				if (!StringUtils.isBlank(bigPicurl)) {
					FileUtils.deleteFile(bigPicurl);
				}
				if (!StringUtils.isBlank(smallPicurl)) {
					FileUtils.deleteFile(smallPicurl);
				}
			}
		}
		//通告单预览图片信息
		this.crewClearDao.deleteOne(crewId, "crewId", NoticePictureModel.TABLE_NAME);
		//通告单内容（其他内容）
		this.crewClearDao.deleteOne(crewId, "crewId", NoticeTimeModel.TABLE_NAME);
		//通告单基本信息（名称、通告日期、销场状态、是否已发布等信息）
		this.crewClearDao.deleteOne(crewId, "crewId", NoticeInfoModel.TABLE_NAME);
		
		/**** 拍摄日志、场记单 ****/
		//删除场记单备注附件  文件
		this.deleteAttachmentFile(crewId, AttachmentBuzType.ClipComment);
		//场记单备注附件信息
		this.crewClearDao.deleteAttachmentInfoByCrewId(crewId, AttachmentBuzType.ClipComment);
		this.crewClearDao.deleteAttachmentPacketByCrewId(crewId, AttachmentBuzType.ClipComment);
		//场记单备注信息
		this.crewClearDao.deleteOne(crewId, "crewId", ClipCommentModel.TABLE_NAME);
		//删除场记单道具附件  文件
		this.deleteAttachmentFile(crewId, AttachmentBuzType.ClipProp);
		//场记单道具附件信息
		this.crewClearDao.deleteAttachmentInfoByCrewId(crewId, AttachmentBuzType.ClipProp);
		this.crewClearDao.deleteAttachmentPacketByCrewId(crewId, AttachmentBuzType.ClipProp);
		//场记单道具信息
		this.crewClearDao.deleteOne(crewId, "crewId", ClipPropModel.TABLE_NAME);
		//场景转场表
		this.crewClearDao.deleteOne(crewId, "crewId", ConvertAddressModel.TABLE_NAME);
		//通告单部门得分表
		this.crewClearDao.deleteOne(crewId, "crewId", DepartmentEvaluateModel.TABLE_NAME);
		//现场转场信息
		this.crewClearDao.deleteOne(crewId, "crewId", LiveConvertAddModel.TABLE_NAME);
		//演员出勤信息
		this.crewClearDao.deleteOne(crewId, "crewId", RoleAttendanceModel.TABLE_NAME);
		//场记申请的临时销场信息
		this.crewClearDao.deleteOne(crewId, "crewId", TmpCancelViewInfoModel.TABLE_NAME);
		//拍摄日志
		this.crewClearDao.deleteOne(crewId, "crewId", ShootLogModel.TABLE_NAME);
		//拍摄镜次信息
		this.crewClearDao.deleteOne(crewId, "crewId", ShootAuditionModel.TABLE_NAME);
		//拍摄现场信息
		this.crewClearDao.deleteOne(crewId, "crewId", ShootLiveModel.TABLE_NAME);		
		
		/**** 计划 ****/
		//拍摄计划基本信息表
		this.crewClearDao.deleteOne(crewId, "crewId", ShootPlanModel.TABLE_NAME);
		//场景与拍摄计划关联表
		this.crewClearDao.deleteOne(crewId, "crewId", ViewPlanMapModel.TABLE_NAME);
		//拍摄分组信息表
		this.crewClearDao.deleteOne(crewId, "crewId", ShootGroupModel.TABLE_NAME);
		
		/**剧照**/
		//删除剧照文件
		this.deleteAttachmentFile(crewId, AttachmentBuzType.CrewPicture);
		//剧照分组
		this.crewClearDao.deleteAttachmentInfoByCrewId(crewId, AttachmentBuzType.CrewPicture);
		this.crewClearDao.deleteAttachmentPacketByCrewId(crewId, AttachmentBuzType.CrewPicture);
		this.crewClearDao.deleteOne(crewId, "crewId", CrewPictureInfoModel.TABLE_NAME);
		
		//剪辑
		this.crewClearDao.deleteOne(crewId, "crewId", CutViewInfoModel.TABLE_NAME);
		
//		//影响因素
//		this.crewClearDao.deletePlanFactorByCrewId(crewId);
//		//计划场景
//		this.crewClearDao.deletePlanViewByCrewId(crewId);
//		//计划
//		this.crewClearDao.deleteOne(crewId, "crewid", "tab_plan");
	}
	
	/**
	 * 删除车辆信息
	 * @param crewId
	 * @throws Exception
	 */
	public void deleteCarInfo(String crewId) throws Exception {
		//车辆加油登记表
		this.crewClearDao.deleteOne(crewId, "crewId", CarWorkModel.TABLE_NAME);
		//车辆信息表
		this.crewClearDao.deleteOne(crewId, "crewId", CarInfoModel.TABLE_NAME);
	}
	
	/**
	 * 删除堪景信息
	 * @param crewId
	 * @throws Exception
	 */
	public void deleteSceneView(String crewId) throws Exception {
		//删除堪景图片文件
		List<Map<String, Object>> picList = this.crewClearDao.queryAttachmentInfoByCrewId(crewId);
		if(picList != null && picList.size() > 0) {
			for(Map<String, Object> map : picList) {
				//高清附件存储路径
				String hdStorePath = map.get("hdStorePath") + "";
				//标清附件存储路径
				String sdStorePath = map.get("sdStorePath") + "";
				if (!StringUtils.isBlank(hdStorePath)) {
					FileUtils.deleteFile(hdStorePath);
				}
				if (!StringUtils.isBlank(sdStorePath)) {
					FileUtils.deleteFile(sdStorePath);
				}
			}
		}
		//删除图片信息
		this.crewClearDao.deleteAttachmentInfoByCrewId(crewId);
		//删除附件包信息
		this.crewClearDao.deleteAttachmentPacketByCrewId(crewId);
		//删除实景-主场景配置信息
//		this.crewClearDao.deleteSceneViewMap(crewId);
		//删除实景信息
		this.crewClearDao.deleteOne(crewId, "crewId", SceneViewInfoModel.TABLE_NAME);
	}
	
	/**
	 * 删除剧组联系表
	 * @param crewId
	 */
	public void deleteCrewContact(String crewId) throws Exception{
		//剧组联系表和系统角色关联表
		this.crewClearDao.deleteOne(crewId, "crewId", ContactSysroleMapModel.TABLE_NAME);
		//剧组联系表
		this.crewClearDao.deleteOne(crewId, "crewId", CrewContactModel.TABLE_NAME);
	}
	
	/**
	 * 删除费用收支数据：收款，付款，借款
	 * @param crewId
	 * @throws Exception
	 */
	public void deleteExpenses(String crewId) throws Exception{
		/**** 付款单 ****/
		//付款信息表
		this.crewClearDao.deleteOne(crewId, "crewId", PaymentInfoModel.TABLE_NAME);
		//付款与财务科目关联信息表
		this.crewClearDao.deleteOne(crewId, "crewId", PaymentFinanSubjMapModel.TABLE_NAME);
		//付款与借款关联信息表
		this.crewClearDao.deleteOne(crewId, "crewId", PaymentFinanSubjMapModel.TABLE_NAME);
		//财务付款方式表
		this.crewClearDao.deleteOne(crewId, "crewId", FinancePaymentWayModel.TABLE_NAME);
		//未分类人员信息表,付款单-收款方
		this.crewClearDao.deleteOne(crewId, "crewId", "tab_finance_person");
		
		/**** 收款单 ****/
		this.crewClearDao.deleteOne(crewId, "crewId", CollectionInfoModel.TABLE_NAME);
		
		/**** 借款单 ****/
		this.crewClearDao.deleteOne(crewId, "crewId", LoanInfoModel.TABLE_NAME);
	}

	/**
	 * 删除合同数据
	 * @param crewId
	 * @throws Exception
	 */
	public void deleteContract(String crewId) throws Exception{
		//演员合同信息表
		this.crewClearDao.deleteOne(crewId, "crewId", ContractActorModel.TABLE_NAME);
		//职员合同信息表
		this.crewClearDao.deleteOne(crewId, "crewId", ContractWorkerModel.TABLE_NAME);
		//制作合同信息表
		this.crewClearDao.deleteOne(crewId, "crewId", ContractProduceModel.TABLE_NAME);
		//合同支付方式表
		this.crewClearDao.deleteOne(crewId, "crewId", ContractStagePayWayModel.TABLE_NAME);
		//合同待付列表
		this.crewClearDao.deleteOne(crewId, "crewId", ContractToPaidModel.TABLE_NAME);
		//合同按月支付薪酬明细
		this.crewClearDao.deleteOne(crewId, "crewId", ContractMonthPayDetailModel.TABLE_NAME);
		//合同按月支付表
		this.crewClearDao.deleteOne(crewId, "crewId", ContractMonthPaywayModel.TABLE_NAME);
		//删除合同附件  文件
		this.deleteAttachmentFile(crewId, AttachmentBuzType.Contract);
		//合同附件
		this.crewClearDao.deleteAttachmentInfoByCrewId(crewId, AttachmentBuzType.Contract);
		this.crewClearDao.deleteAttachmentPacketByCrewId(crewId, AttachmentBuzType.Contract);
	}
	
	/**
	 * 删除附件文件
	 * @param crewId
	 * @param buzType
	 * @throws Exception
	 */
	public void deleteAttachmentFile(String crewId, AttachmentBuzType buzType) throws Exception{
		List<Map<String, Object>> list = this.crewClearDao.queryAttachmentInfoByCrewId(crewId, buzType);
		if(list != null && list.size() > 0) {
			for(Map<String, Object> map : list) {
				//高清附件存储路径
				String hdStorePath = map.get("hdStorePath") + "";
				//标清附件存储路径
				String sdStorePath = map.get("sdStorePath") + "";
				if (!StringUtils.isBlank(hdStorePath)) {
					FileUtils.deleteFile(hdStorePath);
				}
				if (!StringUtils.isBlank(sdStorePath)) {
					FileUtils.deleteFile(sdStorePath);
				}
			}
		}
	}

	/**
	 * 删除预算数据
	 * @param crewId
	 * @throws Exception
	 */
	public void deleteFinanceInfo(String crewId) throws Exception{
		//会计科目表
		this.crewClearDao.deleteOne(crewId, "crewId", AccountSubjectModel.TABLE_NAME);
		//会计科目和预算科目关联关系表
		this.crewClearDao.deleteOne(crewId, "crewId", AccoFinacSubjMapModel.TABLE_NAME);
		//财务科目预算分组信息表(用于费用进度-自定义分组)
		this.crewClearDao.deleteOne(crewId, "crewId", FinanceAccountGroupModel.TABLE_NAME);
		//财务科目预算分组关联信息表
		this.crewClearDao.deleteOne(crewId, "crewId", FinanceAccountGroupMapModel.TABLE_NAME);
		//货币和财务科目关联关系表
		this.crewClearDao.deleteOne(crewId, "crewId", FinanSubjCurrencyMapModel.TABLE_NAME);
		//财务科目信息表(设为空,即删掉货币和财务科目关联关系表)
//		this.crewClearDao.deleteOne(crewId, "crewId", FinanceSubjectModel.TABLE_NAME);
	}
	
	/**
	 * 财务密码（清空是否有财务密码及密码字段）
	 * @param crewId
	 * @throws Exception
	 */
	public void clearFinancePassword(String crewId) throws Exception{
		this.crewClearDao.clearFinancePassword(crewId);
	}
	
	/**
	 * 剧组用户
	 * @param crewId
	 * @throws Exception
	 */
	public void deleteUser(String crewId) throws Exception{
		//删除剧组联系表中的剧组用户和系统角色关联表
		this.crewClearDao.deleteCrewUserContactRole(crewId);
		//删除剧组联系表中的剧组用户入住信息
		//this.crewClearDao.deleteCheckInhotelInfo(crewId);
		//删除剧组联系表中的剧组用户
		this.crewClearDao.deleteCrewUserContact(crewId);
		//删除用户权限
		this.crewClearDao.deleteOne(crewId, "crewId", UserAuthMapModel.TABLE_NAME);
		//删除用户角色信息
		this.crewClearDao.deleteOne(crewId, "crewId", UserRoleMapModel.TABLE_NAME);
		//用户场景角色（场景表中主要演员、特约演员、群众演员）信息
		this.crewClearDao.deleteOne(crewId, "crewId", CrewRoleUserMapModel.TABLE_NAME);
		//删除用户和想要关注的演员的关联关系
		this.crewClearDao.deleteOne(crewId, "crewId", UserFocusRoleMapModel.TABLE_NAME);
		//删除剧组用户关联信息
//		this.crewClearDao.deleteOne(crewId, "crewId", CrewUserMapModel.TABLE_NAME);
		this.crewClearDao.deleteCrewUserMapByCrewId(crewId);
		//申请入组信息
		this.crewClearDao.deleteOne(crewId, "aimCrewId", JoinCrewApplyMsgModel.TABLE_NAME);
	}
	
	/**
	 * 删除住宿信息
	 * @param crewId
	 * @throws Exception
	 */
	public void deleteInHotel(String crewId) throws Exception {
		//将住宿信息表中的一条数据根据日期拆分成多条数据 每天一条数据,进度表 -- 住宿信息展示用
		this.crewClearDao.deleteOne(crewId, "crewid", "tab_inhotelcost_temp");
		//删除宾馆信息
		this.crewClearDao.deleteInhotelInfo(crewId);
		//删除入住信息
		this.crewClearDao.deleteCheckInhotelInfo(crewId);
	}
	
	/**
	 * 删除餐饮信息
	 * @param crewId
	 * @throws Exception
	 */
	public void deleteCater(String crewId) throws Exception {
		//餐饮金额详细信息表
		this.crewClearDao.deleteOne(crewId, "crewId", CaterMoneyInfoModel.TABLE_NAME);
		//餐饮信息表
		this.crewClearDao.deleteOne(crewId, "crewId", CaterInfoModel.TABLE_NAME);
	}
	
	/**
	 * 删除筹备进展信息
	 * @param crewId
	 * @throws Exception
	 */
	public void deletePrepare(String crewId) throws Exception {
		//剧本进度
		//筹备进度-剧本评审分数
		this.crewClearDao.deleteOne(crewId, "crewId", "tab_prepare_script_score");
		//筹备进度-选中剧本类型
		this.crewClearDao.deleteOne(crewId, "crewId", "tab_prepare_script_type_checked");
		//筹备进度-剧本进度信息
		this.crewClearDao.deleteOne(crewId, "crewId", PrepareScriptModel.TABLE_NAME);		
		//筹备进度-剧本评审权重
		this.crewClearDao.deleteOne(crewId, "crewId", "tab_prepare_script_reviewweight");
		//选角进度
		this.crewClearDao.deleteOne(crewId, "crewId", PrepareRoleModel.TABLE_NAME);
		//剧组人员
		this.crewClearDao.deleteOne(crewId, "crewId", PrepareCrewPeopleModel.TABLE_NAME);
		//勘景情况
		this.deleteSceneView(crewId);
		//美术视觉-角色
		this.crewClearDao.deleteOne(crewId, "crewId", PrepareArteffectRoleModel.TABLE_NAME);
		//美术视觉-场景
		this.crewClearDao.deleteOne(crewId, "crewId", PrepareArteffectLocationModel.TABLE_NAME);
		//宣传进度
		this.crewClearDao.deleteOne(crewId, "crewId", PrepareExtensionModel.TABLE_NAME);
		//办公筹备
		this.crewClearDao.deleteOne(crewId, "crewId", PrepareWorkModel.TABLE_NAME);
		//商务运营
		this.crewClearDao.deleteOne(crewId, "crewId", PrepareOperateModel.TABLE_NAME);
	}
	
	/**
	 * 删除审批数据
	 * @param crewId
	 * @throws Exception
	 */
	public void deleteReceiptInfo(String crewId) throws Exception {
		//删除审批意见
		this.crewClearDao.deleteOne(crewId, "crewId", ApprovalInfoModel.TABLE_NAME);
		//删除单据
		this.crewClearDao.deleteOne(crewId, "crewId", ReceiptInfoModel.TABLE_NAME);
	}
}
