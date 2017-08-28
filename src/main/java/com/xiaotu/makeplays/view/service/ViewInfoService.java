package com.xiaotu.makeplays.view.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.CollationKey;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.attachment.dao.AttachmentPacketDao;
import com.xiaotu.makeplays.attachment.model.AttachmentPacketModel;
import com.xiaotu.makeplays.goods.dao.GoodsInfoDao;
import com.xiaotu.makeplays.goods.dao.ViewGoodsMapDao;
import com.xiaotu.makeplays.goods.model.GoodsInfoModel;
import com.xiaotu.makeplays.goods.model.ViewGoodsInfoMap;
import com.xiaotu.makeplays.goods.model.constants.GoodsType;
import com.xiaotu.makeplays.locationsearch.dao.SceneViewInfoDao;
import com.xiaotu.makeplays.locationsearch.model.SceneViewInfoModel;
import com.xiaotu.makeplays.notice.dao.NoticeInfoDao;
import com.xiaotu.makeplays.notice.model.NoticeInfoModel;
import com.xiaotu.makeplays.roleactor.dao.ViewRoleDao;
import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;
import com.xiaotu.makeplays.roleactor.model.constants.ViewRoleType;
import com.xiaotu.makeplays.roleactor.service.ViewRoleService;
import com.xiaotu.makeplays.scenario.controller.dto.ScenarioViewDto;
import com.xiaotu.makeplays.scenario.model.BookMarkModel;
import com.xiaotu.makeplays.scenario.model.ScenarioFormatModel;
import com.xiaotu.makeplays.scenario.service.DownloadScenarioRecordService;
import com.xiaotu.makeplays.scenario.service.ScenarioFormatService;
import com.xiaotu.makeplays.scenario.service.ScenarioService;
import com.xiaotu.makeplays.shoot.dao.ScheduleViewMapDao;
import com.xiaotu.makeplays.shoot.model.ShootGroupModel;
import com.xiaotu.makeplays.shoot.service.ShootGroupService;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.DateUtils;
import com.xiaotu.makeplays.utils.MD5Util;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.view.controller.dto.BatchUpdateViewDto;
import com.xiaotu.makeplays.view.controller.dto.QueryContextDto;
import com.xiaotu.makeplays.view.controller.dto.SameViewLocationDto;
import com.xiaotu.makeplays.view.controller.dto.ViewFilterDto;
import com.xiaotu.makeplays.view.controller.dto.ViewInfoDto;
import com.xiaotu.makeplays.view.controller.filter.ViewFilter;
import com.xiaotu.makeplays.view.dao.AtmosphereDao;
import com.xiaotu.makeplays.view.dao.InsideAdvertDao;
import com.xiaotu.makeplays.view.dao.ViewAdvertMapDao;
import com.xiaotu.makeplays.view.dao.ViewContentDao;
import com.xiaotu.makeplays.view.dao.ViewInfoDao;
import com.xiaotu.makeplays.view.dao.ViewLocationDao;
import com.xiaotu.makeplays.view.dao.ViewLocationMapDao;
import com.xiaotu.makeplays.view.dao.ViewRoleAndActorDao;
import com.xiaotu.makeplays.view.dao.ViewRoleMapDao;
import com.xiaotu.makeplays.view.model.AtmosphereInfoModel;
import com.xiaotu.makeplays.view.model.InsideAdvertModel;
import com.xiaotu.makeplays.view.model.ViewAdvertMapModel;
import com.xiaotu.makeplays.view.model.ViewContentModel;
import com.xiaotu.makeplays.view.model.ViewInfoModel;
import com.xiaotu.makeplays.view.model.ViewLocationMapModel;
import com.xiaotu.makeplays.view.model.ViewLocationModel;
import com.xiaotu.makeplays.view.model.ViewRoleAndActorModel;
import com.xiaotu.makeplays.view.model.ViewRoleMapModel;
import com.xiaotu.makeplays.view.model.constants.BookmarkType;
import com.xiaotu.makeplays.view.model.constants.LocationType;
import com.xiaotu.makeplays.view.model.constants.ViewContentStatus;
import com.xiaotu.makeplays.view.model.constants.ViewCreateWay;

/**
 * 主场景表信息
 * @author xuchangjian
 */
@Service
public class ViewInfoService {

	private List<String> noticeViewRoleList = null;
	
	private DecimalFormat df = new DecimalFormat("0.00");
	
	private static final String IMPORTANT_VIEW_REGEX = "(,|，|、|/|；| |\\t|　| )+";
	
	private String lineSeparator = "\r\n";
	
	@Autowired
	private ViewInfoDao viewInfoDao;
	
	@Autowired
	private AttachmentPacketDao attachmentPacketDao;
	
	@Autowired
	private ViewLocationDao viewLocationDao;
	
	@Autowired
	private AtmosphereDao atmosphereDao;
	
	@Autowired
	private ViewContentDao viewContentDao;
	
	@Autowired
	private ViewLocationMapDao viewLocationMapDao;
	
	@Autowired
	private ViewLocationService viewLocationService;
	
	@Autowired
	private ViewRoleDao viewRoleDao;
	
	@Autowired
	private InsideAdvertDao insideAdvertDao;
	
	@Autowired
	private ViewAdvertMapDao viewAdvertMapDao;
	
	@Autowired
	private ViewRoleAndActorDao viewRoleAndActorDao;
	
	@Autowired
	private ViewRoleMapDao viewRoleMapDao;
	
	@Autowired
	private ViewRoleService viewRoleService;
	
	@Autowired
	private AtmosphereService atmosphereService;
	
	@Autowired
	private ShootGroupService shootGroupService;
	
	@Autowired
	private InsideAdvertService insideAdvertService;
	
	@Autowired
	private SceneViewInfoDao sceneViewInfoDao;
	@Autowired
	private NoticeInfoDao noticeInfoDao;
	
	@Autowired
	private DownloadScenarioRecordService downloadScenarioRecordService;
	
	@Autowired
	private GoodsInfoDao goodsInfoDao;
	
	@Autowired
	private ViewGoodsMapDao viewGoodsMapDao;
	
	@Autowired
	private ScenarioService scenarioService;
	
	@Autowired
	private ScenarioFormatService scenarioFormatService;
	
	@Autowired
	private ScheduleViewMapDao scheduleViewMapDao;
	
	
	/**
	 * 根据剧组的ID查找对应的所有场景
	 * @param crewId
	 * @return
	 */
	public List<ViewInfoModel> queryByCrewId(String crewId, Page page) {
		return this.viewInfoDao.queryByCrewId(crewId, page);
	}
	
	/**
	 * 根据多个条件查询场景信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page	分页信息
	 * @return
	 */
	public List<ViewInfoModel> queryManyByMuitiContition(Map<String, Object> conditionMap, Page page) {
		return this.viewInfoDao.queryManyByMuitiContition(conditionMap, page);
	}
	
	/**
	 * 根据viewDto对象新增场景信息
	 * @param viewInfoDto
	 * @throws Exception 
	 */
	public ViewInfoModel addByViewInfoDto(ViewInfoDto viewInfoDto, UserInfoModel userInfo,String crewId) throws Exception {
		
		ViewInfoModel viewInfo = new ViewInfoModel();
		viewInfo.setViewId(UUIDUtils.getId());
		viewInfo.setCrewId(crewId);
		viewInfo.setCreateTime(new Date());
		
		//内外景
		String site = viewInfoDto.getSite();
		viewInfo.setSite(site);
		viewInfo.setIsManualSave(true);
		viewInfo.setMainContent(viewInfoDto.getMainContent());
		viewInfo.setViewNo(viewInfoDto.getViewNo());
		viewInfo.setSeriesNo(viewInfoDto.getSeriesNo());
		viewInfo.setRemark(viewInfoDto.getRemark());
		BigDecimal bdf = new BigDecimal(viewInfoDto.getPageCount());
		viewInfo.setPageCount(bdf.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
		
		//气氛
		String atmosphereId = genAtmosphereInfo(crewId, viewInfoDto.getAtmosphereName());
		if (StringUtils.isBlank(atmosphereId)) {
			atmosphereId = null;
		}
		viewInfo.setAtmosphereId(atmosphereId);
		
		//场景角色信息
		if (!StringUtils.isBlank(viewInfoDto.getMajorActor())) {
			String[] majorActorArr = viewInfoDto.getMajorActor().split(",");	//主要演员
			
			for (String roleName : majorActorArr) {
				String dealedRoleName = "";
				int roleNumber = 1;
				
				if (StringUtils.isNotBlank(roleName)) {
					if (roleName.contains("OS")) {
						roleNumber = 0;
						dealedRoleName = roleName.substring(0,roleName.indexOf("("));
					}else {
						dealedRoleName = roleName;
					}
				}
				this.saveRole(ViewRoleType.MajorActor.getValue(), crewId, viewInfo.getViewId(), dealedRoleName, roleNumber);
			}
		}
		//特约演员
		if (!StringUtils.isBlank(viewInfoDto.getGuestActor())) {
			this.saveRoles(viewInfoDto.getGuestActor(), ViewRoleType.GuestActor.getValue(), crewId, viewInfo.getViewId(), 1);	//特约演员
		}
		
		//群众演员
		if (!StringUtils.isBlank(viewInfoDto.getMassesActor())) {
			String[] massesActorArr = viewInfoDto.getMassesActor().split(",");	//群众演员
			
			for (String roleName : massesActorArr) {
				String[] roleNameArr = roleName.split("_");
				String dealedRoleName = "";
				int roleNumber = 1;
				if (roleNameArr.length > 0) {
					dealedRoleName = roleNameArr[0];
				}
				if (roleNameArr.length > 1) {
					roleNumber = Integer.parseInt(roleNameArr[1]);
				}
				this.saveRole(ViewRoleType.MassesActor.getValue(), crewId, viewInfo.getViewId(), dealedRoleName, roleNumber);
			}
		}
		
		//场景地点信息
		this.viewLocationMapDao.deleteManyByViewId(viewInfo.getViewId());
		//主场景
		this.saveViewLocation(viewInfoDto.getFirstLocation(), LocationType.lvlOneLocation.getValue(), crewId, viewInfo.getViewId());
		//次场景
		this.saveViewLocation(viewInfoDto.getSecondLocation(), LocationType.lvlTwoLocation.getValue(), crewId, viewInfo.getViewId());
		//三级场景
		this.saveViewLocation(viewInfoDto.getThirdLocation(), LocationType.lvlThreeLocation.getValue(), crewId, viewInfo.getViewId());
		
		//删除服化道与场景表之间的关联关系
		this.goodsInfoDao.deleteViewGoodsMapByViewId(viewInfo.getViewId());
		//服装
		if (!StringUtils.isBlank(viewInfoDto.getClothes())) {
			this.saveGoodsInfo(viewInfoDto.getClothes(), crewId, viewInfo.getViewId(), userInfo.getUserId(), userInfo.getUserName(), GoodsType.Clothes.getValue());
		}
		
		//化妆
		if (!StringUtils.isBlank(viewInfoDto.getMakeups())) {
			this.saveGoodsInfo(viewInfoDto.getMakeups(), crewId, viewInfo.getViewId(), userInfo.getUserId(), userInfo.getUserName(), GoodsType.Makeup.getValue());
		}

		//道具信息
		if (!StringUtils.isBlank(viewInfoDto.getCommonProps())) {
			this.saveGoodsInfo(viewInfoDto.getCommonProps(), crewId, viewInfo.getViewId(), userInfo.getUserId(), userInfo.getUserName(), GoodsType.CommonProps.getValue());
		}
		if (!StringUtils.isBlank(viewInfoDto.getSpecialProps())) {
			this.saveGoodsInfo(viewInfoDto.getSpecialProps(), crewId, viewInfo.getViewId(), userInfo.getUserId(), userInfo.getUserName(), GoodsType.SpecialProps.getValue());
		}
		
		//拍摄地点
		SceneViewInfoModel shootLocationInfo = this.addOrGetShootLocationByLocationAndCrewId(viewInfoDto.getShootLocation(), viewInfoDto.getShootRegion(), crewId);
		String shootLocationId = "";
		if (shootLocationInfo != null) {
			shootLocationId = shootLocationInfo.getId();
		}
		viewInfo.setShootLocationId(shootLocationId);
		
		//页数
		viewInfo.setPageCount(viewInfoDto.getPageCount());
//		if (!StringUtils.isBlank(viewInfoDto.getContent())) {
//			ScenarioFormatModel formatInfo = this.scenarioFormatService.queryByCrewId(crewId);
//			int wordCount = 35;
//			int lineCount = 40;
//			if (formatInfo != null) {
//				wordCount = formatInfo.getWordCount();
//				lineCount = formatInfo.getLineCount();
//			}
//			int viewLineCount = this.scenarioService.calculateLineCount(viewInfoDto.getTitle() + this.lineSeparator + viewInfoDto.getContent(), wordCount, true);
//			double pageCount=com.xiaotu.makeplays.utils.StringUtils.div(viewLineCount, lineCount, 1);
//			if (pageCount == 0.0 && StringUtils.isNotBlank(viewInfoDto.getContent())) {
//				pageCount = 0.1;
//			}
//			viewInfo.setPageCount(pageCount);
//		}
		
		//特殊提醒
		viewInfo.setSpecialRemind(viewInfoDto.getSpecialRemind());
		
		//保存信息
		viewInfoDao.add(viewInfo);
		
		//保存剧本内容信息
		if (!StringUtils.isBlank(viewInfoDto.getTitle()) || !StringUtils.isBlank(viewInfoDto.getContent())) {
			ViewContentModel viewContent = new ViewContentModel();
			viewContent.setContentId(UUIDUtils.getId());
			viewContent.setViewId(viewInfo.getViewId());
			viewContent.setTitle(viewInfoDto.getTitle());
			viewContent.setContent(viewInfoDto.getContent());
			viewContent.setCrewId(crewId);
			viewContent.setStatus(ViewContentStatus.AddNotpublished.getValue());
			if (!StringUtils.isBlank(viewInfoDto.getContent())) {
				viewContent.setFigureprint(MD5Util.MD5(viewInfoDto.getContent()));
			}
			this.viewContentDao.add(viewContent);
		}
		
		//更新书签信息
		BookMarkModel bookMarkInfo = new BookMarkModel();
		bookMarkInfo.setId(UUIDUtils.getId());
		bookMarkInfo.setType(BookmarkType.BookMarkType.getValue());
		bookMarkInfo.setCrewId(crewId);
		bookMarkInfo.setUserId(userInfo.getUserId());
		bookMarkInfo.setValue(viewInfo.getViewId());
		this.scenarioService.saveSceBookMark(bookMarkInfo);
		
		return viewInfo;
	}
	
	/**
	 * 根据viewInfoDto对象更新场景信息
	 * @param viewInfoDto
	 * @throws Exception 
	 */
	public ViewInfoModel updateByViewInfoDto(ViewInfoDto viewInfoDto, UserInfoModel userInfo) throws Exception {
		
		ViewInfoModel viewInfo = this.viewInfoDao.queryById(viewInfoDto.getViewId());
		String viewId = viewInfo.getViewId();
		String crewId = viewInfo.getCrewId();
		
		viewInfo.setSeriesNo(viewInfoDto.getSeriesNo());
		viewInfo.setViewNo(viewInfoDto.getViewNo());
		//内外景
		String site = viewInfoDto.getSite();
		viewInfo.setSite(site);
		viewInfo.setIsManualSave(true);
		if (StringUtils.isBlank(viewInfoDto.getMainContent())) {
			viewInfo.setMainContent(null);
		} else {
			viewInfo.setMainContent(viewInfoDto.getMainContent());
		}
		
		//气氛
		String atmosphereId = genAtmosphereInfo(crewId, viewInfoDto.getAtmosphereName());
		if (StringUtils.isBlank(atmosphereId)) {
			atmosphereId = null;
		}
		viewInfo.setAtmosphereId(atmosphereId);
		BigDecimal bdf = new BigDecimal(viewInfoDto.getPageCount());
		viewInfo.setPageCount(bdf.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
		
		if (StringUtils.isBlank(viewInfoDto.getRemark())) {
			viewInfo.setRemark(null);
		} else {
			viewInfo.setRemark(viewInfoDto.getRemark());
		}
		
		//场景角色信息
		//删除该场景下所有和角色的关联关系，然后再新建（考虑到当用户删除一个角色信息时，删除这个操作无法更新到数据库）
		this.viewRoleMapDao.deleteManyByViewId(viewId);
		
		if (!StringUtils.isBlank(viewInfoDto.getMajorActor())) {
			String[] majorActorArr = viewInfoDto.getMajorActor().split(",");	//主要演员
			
			for (String roleName : majorActorArr) {
				String dealedRoleName = "";
				int roleNumber = 1;
				
				if (StringUtils.isNotBlank(roleName)) {
					if (roleName.contains("OS")) {
						roleNumber = 0;
						dealedRoleName = roleName.substring(0,roleName.indexOf("("));
					}else {
						dealedRoleName = roleName;
					}
				}
				this.saveRole(ViewRoleType.MajorActor.getValue(), crewId, viewInfo.getViewId(), dealedRoleName, roleNumber);
			}
		}
		if (!StringUtils.isBlank(viewInfoDto.getGuestActor())) {
			this.saveRoles(viewInfoDto.getGuestActor(), ViewRoleType.GuestActor.getValue(), crewId, viewId, 1);	//特约演员
		}
		if (!StringUtils.isBlank(viewInfoDto.getMassesActor())) {
			String[] massesActorArr = viewInfoDto.getMassesActor().split(",");	
			for (String roleName : massesActorArr) {
				String[] roleNameArr = roleName.split("_");
				String dealedRoleName = "";
				int roleNumber = 1;
				if (roleNameArr.length > 0) {
					dealedRoleName = roleNameArr[0];
				}
				if (roleNameArr.length > 1) {
					roleNumber = Integer.parseInt(roleNameArr[1]);
				}
				this.saveRole(ViewRoleType.MassesActor.getValue(), crewId, viewId, dealedRoleName, roleNumber); //群众演员
			}
		}
		//当更新场景信息时，对话中未获取的角色信息置空
		viewInfo.setNotGetRoleNames(null);
		viewInfo.setNotGetProps(null);
		
		//场景地点信息
		this.viewLocationMapDao.deleteManyByViewId(viewId);
		//主场景
		this.saveViewLocation(viewInfoDto.getFirstLocation(), LocationType.lvlOneLocation.getValue(), crewId, viewId);
		//次场景
		this.saveViewLocation(viewInfoDto.getSecondLocation(), LocationType.lvlTwoLocation.getValue(), crewId, viewId);
		//三级场景
		this.saveViewLocation(viewInfoDto.getThirdLocation(), LocationType.lvlThreeLocation.getValue(), crewId, viewId);
		
		//保存特殊提醒
		viewInfo.setSpecialRemind(viewInfoDto.getSpecialRemind());
		
		//删除服化道与场景表之间的关联关系
		this.goodsInfoDao.deleteViewGoodsMapByViewId(viewInfo.getViewId());
		//服装
		if (!StringUtils.isBlank(viewInfoDto.getClothes())) {
			this.saveGoodsInfo(viewInfoDto.getClothes(), crewId, viewId, userInfo.getUserId(), userInfo.getUserName(), GoodsType.Clothes.getValue());
		}
		
		//化妆
		if (!StringUtils.isBlank(viewInfoDto.getMakeups())) {
			this.saveGoodsInfo(viewInfoDto.getMakeups(), crewId, viewId, userInfo.getUserId(), userInfo.getUserName(), GoodsType.Makeup.getValue());
		}

		//道具信息(普通道具)
		if (!StringUtils.isBlank(viewInfoDto.getCommonProps())) {
			this.saveGoodsInfo(viewInfoDto.getCommonProps(), crewId, viewId, userInfo.getUserId(), userInfo.getUserName(), GoodsType.CommonProps.getValue());
		}
		//保存特殊道具信息
		if (!StringUtils.isBlank(viewInfoDto.getSpecialProps())) {
			this.saveGoodsInfo(viewInfoDto.getSpecialProps(), crewId, viewId, userInfo.getUserId(), userInfo.getUserName(), GoodsType.SpecialProps.getValue());
		}
		
		//拍摄地点
		SceneViewInfoModel shootLocationInfo = this.addOrGetShootLocationByLocationAndCrewId(viewInfoDto.getShootLocation(), viewInfoDto.getShootRegion(), crewId);
		String shootLocationId = null;
		if (shootLocationInfo != null) {
			shootLocationId = shootLocationInfo.getId();
		}
		viewInfo.setShootLocationId(shootLocationId);
		
		//页数
		viewInfo.setPageCount(viewInfoDto.getPageCount());
//		if (!StringUtils.isBlank(viewInfoDto.getContent())) {
//			ScenarioFormatModel formatInfo = this.scenarioFormatService.queryByCrewId(crewId);
//			int wordCount = 35;
//			int lineCount = 40;
//			if (formatInfo != null) {
//				wordCount = formatInfo.getWordCount();
//				lineCount = formatInfo.getLineCount();
//			}
//			int viewLineCount = this.scenarioService.calculateLineCount(viewInfoDto.getTitle() + this.lineSeparator + viewInfoDto.getContent(), wordCount, true);
//			double pageCount=com.xiaotu.makeplays.utils.StringUtils.div(viewLineCount, lineCount, 1);
//			if (pageCount == 0.0 && StringUtils.isNotBlank(viewInfoDto.getContent())) {
//				pageCount = 0.1;
//			}
//			viewInfo.setPageCount(pageCount);
//		}
		
		//更新信息
		this.updateViewInfo(viewInfo);
		
		ViewContentModel viewContent = this.viewContentDao.queryByViewId(viewId);
		
		//只有当标题和内容有一个不是空时，才更新剧本内容信息，因为场景表中的保存不会传标题和内容信息，如果不加判断，则会把对应的剧本内容全部置空
		if (!StringUtils.isBlank(viewInfoDto.getTitle()) || !StringUtils.isBlank(viewInfoDto.getContent())) {
			if (viewContent != null) {
				boolean isChanged = this.isViewContentChange(viewContent, viewInfoDto.getContent(), viewInfoDto.getTitle());
				if (isChanged) {
					viewContent.setViewId(viewInfoDto.getViewId());
					viewContent.setTitle(viewInfoDto.getTitle());
					viewContent.setContent(viewInfoDto.getContent());
					viewContent.setCrewId(crewId);
					if (viewContent.getStatus() == ViewContentStatus.Published.getValue()) {
						viewContent.setStatus(ViewContentStatus.UpdateNotPublished.getValue());
					}
					if (!StringUtils.isBlank(viewInfoDto.getContent())) {
						viewContent.setFigureprint(MD5Util.MD5(viewInfoDto.getContent()));
					}
					this.viewContentDao.updateWithNull(viewContent, "contentId");
				}
				
			} else {
				viewContent = new ViewContentModel();
				viewContent.setContentId(UUIDUtils.getId());
				viewContent.setViewId(viewInfoDto.getViewId());
				viewContent.setTitle(viewInfoDto.getTitle());
				viewContent.setContent(viewInfoDto.getContent());
				viewContent.setCrewId(crewId);
				viewContent.setStatus(ViewContentStatus.AddNotpublished.getValue());
				if (!StringUtils.isBlank(viewInfoDto.getContent())) {
					viewContent.setFigureprint(MD5Util.MD5(viewInfoDto.getContent()));
				}
				this.viewContentDao.add(viewContent);
			}
		}
		return viewInfo;
	}
	
	/**
	 * 根据ScenarioViewDto批量更新场景信息
	 * @param sceDtoList
	 * @throws Exception 
	 */
	public void updateManyBySceDto (List<ScenarioViewDto> sceDtoList, boolean isManualSave, String crewId, UserInfoModel userInfo) throws Exception {
		List<ViewInfoModel> viewInfoList = new ArrayList<ViewInfoModel>();	//待修改的场景信息
		List<ViewContentModel> toUpdateViewContentList = new ArrayList<ViewContentModel>();	//待修改的场景内容信息
		List<ViewContentModel> toAddViewContentList = new ArrayList<ViewContentModel>();	//待新增的场景内容信息
		if(sceDtoList == null || sceDtoList.size() == 0){
			return ;
		}
		
		//数据库中当前剧组的场景信息
		List<ViewInfoModel> viewInfoListInDB = viewInfoDao.queryByCrewId(crewId,null);
		//获取当前剧组下的气氛信息
		Map<String,String> atmMap = queryAtmByCrewId(crewId);
		//批量保存气氛信息
		List<AtmosphereInfoModel> batchAddAtmList = new ArrayList<AtmosphereInfoModel>();
		//获取当前剧组的拍摄地
		Map<String,String> shootMap = queryShootByCrewid(crewId);
		//List<ShootLocationModel> batchAddShootList = new ArrayList<ShootLocationModel>();
		List<SceneViewInfoModel> batchAddSceneViewInfoList = new ArrayList<SceneViewInfoModel>();
		
		//获取当前剧组下的角色信息
		List<ViewRoleModel> viewRoleList = viewRoleDao.queryByCrewId(crewId);
		//批量添加剧组角色信息
		List<ViewRoleModel> batchAddRoleList = new ArrayList<ViewRoleModel>();
		
		//批量添加角色场景关联信息
		List<ViewRoleMapModel> batchAddViewRoleMapList = new ArrayList<ViewRoleMapModel>();
		//批量修改角色场景关联信息
		List<ViewRoleMapModel> batchUpdateViewRoleMapList = new ArrayList<ViewRoleMapModel>();
		//根据剧组id查询角色与场景表关联关系
		List<ViewRoleMapModel> viewRoleMapList = viewRoleMapDao.queryViewRoleMapInfoByCrewId(crewId);
		
		Map<String,String> roleMap1 = new HashMap<String, String>();
		Map<String,String> roleMap2 = new HashMap<String, String>();
		Map<String,String> roleMap3 = new HashMap<String, String>();
		
		//服装数据
		Map<String, Object> clothCondition = new HashMap<String, Object>();
		clothCondition.put("crewId", crewId);
		clothCondition.put("goodsType", 3);
		List<GoodsInfoModel> clothesInfoList = this.goodsInfoDao.queryGoodsByCondition(clothCondition);
		//批量保存服装数据
		List<GoodsInfoModel> batchAddClothesInfoList = new ArrayList<GoodsInfoModel>();
		//场景服装对照信息viewClothesInfoMap
		List<ViewGoodsInfoMap> viewClothesInfoMap = this.viewGoodsMapDao.queryClothViewInfo(crewId, GoodsType.Clothes.getValue());
		//批量保存服装场景信息
		List<ViewGoodsInfoMap> batchAddViewClothesInfoList = new ArrayList<ViewGoodsInfoMap>();
		//服装名称
		Map<String,String> clotheNameMap = new HashMap<String,String>();
		
		
		//化妆信息
		Map<String, Object> makeupCondition = new HashMap<String, Object>();
		makeupCondition.put("crewId", crewId);
		makeupCondition.put("goodsType", 2);
		List<GoodsInfoModel> makeUpInfoList = this.goodsInfoDao.queryGoodsByCondition(makeupCondition);
		//场景化妆对照信息
		List<ViewGoodsInfoMap> viewMakeUpInfoList = this.viewGoodsMapDao.queryClothViewInfo(crewId, GoodsType.Makeup.getValue());
		//化妆名称
		Map<String,String> makeupNameMap = new HashMap<String,String>();
		//批量保存化妆信息
		List<GoodsInfoModel> batchAddMakeUpInfoList = new ArrayList<GoodsInfoModel>();
		//批量保存场景化妆对照关系
		List<ViewGoodsInfoMap> batchAddViewMakeUpMapList = new ArrayList<ViewGoodsInfoMap>();
		
		//批量保存道具信息
		List<GoodsInfoModel> batchAddPropsInfoList = new ArrayList<GoodsInfoModel>();
		//批量保存场景道具信息
		List<ViewGoodsInfoMap> batchAddViewPropsInfoList = new ArrayList<ViewGoodsInfoMap>();
		//道具名称
		Map<String,String> propNameMap1 = new HashMap<String,String>();
		Map<String,String> propNameMap2 = new HashMap<String,String>();
		//道具信息
		List<GoodsInfoModel> propsInfoList = goodsInfoDao.queryCrewPropInfo(crewId);

		//服化道信息
		List<GoodsInfoModel> goodsInfoList = new ArrayList<GoodsInfoModel>();
		goodsInfoList.addAll(clothesInfoList);
		goodsInfoList.addAll(makeUpInfoList);
		goodsInfoList.addAll(propsInfoList);
		//服化道名称
		Map<String,String> goodsNameMap = new HashMap<String,String>();
		
		//场景道具信息
		List<ViewGoodsInfoMap> viewPropMapList = viewGoodsMapDao.queryPropViewMap(crewId);
		
		//批量保存道具信息
		List<ViewLocationModel> batchAddLocationInfoList = new ArrayList<ViewLocationModel>();
		//批量保存场景道具信息
		List<ViewLocationMapModel> batchAddViewLocationInfoList = new ArrayList<ViewLocationMapModel>();
		//道具名称
		Map<String,String> locationNameMapFirst = new HashMap<String,String>();
		Map<String,String> locationNameMapSecond = new HashMap<String,String>();
		Map<String,String> locationNameMapThird = new HashMap<String,String>();
		//道具信息
		List<ViewLocationModel> locationInfoList = viewLocationDao.queryManyByCrewId(crewId);
		//场景道具信息
		List<ViewLocationMapModel> viewLocationMapList = viewLocationMapDao.queryViewLocationMapByCrewId(crewId);
		
		//剧组场景内容
		List<ViewContentModel> viewContentList = viewContentDao.queryByCrewId(crewId);
		
		//删除角色信息
		List<String> delViewRoleByViewId = new ArrayList<String>();
		//删除服装信息
		List<String> delViewClothByViewId = new ArrayList<String>();
		//删除三级场景信息
		List<String> delViewLocationByViewId = new ArrayList<String>();		
		
		
		//查询商值信息
		List<InsideAdvertModel> insideAdvertList = insideAdvertDao.queryAdvertInfoByCrewId(crewId);
		//查询商值场景对照信息
		List<ViewAdvertMapModel> viewAdvertMapList = viewAdvertMapDao.queryAdvertInfoByCrewId(crewId);
		//删除商值场景对照关系
		List<String> batchDelViewAdvertMapList = new ArrayList<String>();
		//保存商值场景对照关系
		List<ViewAdvertMapModel> batchAddViewAdvertMapList = new ArrayList<ViewAdvertMapModel>();
		//保存商值信息
		List<InsideAdvertModel> batchAddViewAdvertList = new ArrayList<InsideAdvertModel>();
		//商值id
		Map<String,String> advertIdMap = new HashMap<String,String>();
		
		
		for (ScenarioViewDto scenarioDto : sceDtoList) {
			
			if(viewInfoListInDB==null||viewInfoListInDB.size()==0){
				continue;
			}
			Integer seriesNo = scenarioDto.getSeriesNo();//集
			String  viewNo = scenarioDto.getViewNo();//场
			ViewInfoModel view = null;
			for(ViewInfoModel viewInfoModel :viewInfoListInDB){
				Integer iSeriesNo = viewInfoModel.getSeriesNo();
				String iViewNo = viewInfoModel.getViewNo();
				if(seriesNo == iSeriesNo && viewNo.toUpperCase().equals(iViewNo.toUpperCase())){
					view = viewInfoModel;
				}
			}
			
			if(view == null){
				continue;
			}
			
			
			//剧本中的场景对应的数据库已存储的场景表信息
			//ViewInfoModel view = this.viewInfoDao.queryOneByCrewIdAndSeViNo(crewId, scenarioDto.getSeriesNo(), scenarioDto.getViewNo());
			String viewId = view.getViewId();
			String site = scenarioDto.getSite();
			view.setSite(site);
			view.setIsManualSave(isManualSave);
			BigDecimal bdf = new BigDecimal(scenarioDto.getPageCount());
			view.setPageCount(bdf.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
			view.setMainContent(scenarioDto.getMainContent());
			view.setRemark(scenarioDto.getRemark());
			view.setCreateWay(ViewCreateWay.BySceAnalyse.getValue());
			view.setSpecialRemind(scenarioDto.getSpecialRemind());
			view.setNotGetRoleNames(null);
			view.setNotGetProps(null);
			view.setViewNo(view.getViewNo().toUpperCase());
			
			//商值
			String advertInfo = scenarioDto.getCommercialImplants();
			batchDelViewAdvertMapList.add(viewId);	
			if(StringUtils.isNotBlank(advertInfo)){
				String[] commStrs = StringUtils.split(advertInfo, ",");
				if(commStrs!=null&&commStrs.length>0){
					for(String commTemp:commStrs){
						String advertId = "";
						for(InsideAdvertModel insideAdvertModel:insideAdvertList){
							String advertName = insideAdvertModel.getAdvertName();
							if(commTemp.equals(advertName)){
								advertId = insideAdvertModel.getAdvertId();
								break;
							}
						}
						
						if(StringUtils.isBlank(advertId)){
							String aid = advertIdMap.get(commTemp);
							if(StringUtils.isBlank(aid)){
								advertId = UUIDUtils.getId();
								InsideAdvertModel insideAdvertModel = new InsideAdvertModel();
								insideAdvertModel.setAdvertId(advertId);
								insideAdvertModel.setAdvertName(commTemp);
								insideAdvertModel.setCrewId(crewId);
								batchAddViewAdvertList.add(insideAdvertModel);
							}else{
								advertId = aid;
							}
							
						}
						
						ViewAdvertMapModel viewAdvertMapModel  = new ViewAdvertMapModel();
						viewAdvertMapModel.setAdvertId(advertId);
						viewAdvertMapModel.setAdvertType("1");
						viewAdvertMapModel.setCrewId(crewId);
						viewAdvertMapModel.setMapId(UUIDUtils.getId());
						viewAdvertMapModel.setViewId(viewId);
						batchAddViewAdvertMapList.add(viewAdvertMapModel);
					}
					
				}
			}
			
			//拍摄时间
			Date shootTime = view.getShotDate();
			if(shootTime==null){
				String shootTimeStr = scenarioDto.getShootTime();
				if(StringUtils.isNotBlank(shootTimeStr)){
					shootTimeStr = DateUtils.formatToString(shootTimeStr, 0);
					view.setShotDate(new SimpleDateFormat("yyyy-MM-dd").parse(shootTimeStr));
				}else{
					view.setShotDate(null);
				}
			}
			
			
			//拍摄地点
			/*ShootLocationModel shootLocationInfo = this.addOrGetShootLocationByLocationAndCrewId(scenarioDto.getShootLocation(), crewId);
			String shootLocationId = null;
			if (shootLocationInfo != null) {
				shootLocationId = shootLocationInfo.getShootLocationId();
			}*/
			
			String shootLocation = scenarioDto.getShootLocation();
			if(StringUtils.isNotBlank(shootLocation)){
				String shootLocationId = shootMap.get(shootLocation);
				if(StringUtils.isBlank(shootLocationId)){
					/*ShootLocationModel shootLocationModel = new ShootLocationModel();
					shootLocationId = UUIDUtils.getId();
					shootLocationModel.setShootLocationId(shootLocationId);
					shootLocationModel.setShootLocation(shootLocation);
					shootLocationModel.setCrewId(crewId);
					batchAddShootList.add(shootLocationModel);
					shootMap.put(shootLocation, shootLocationId);
					*/
					shootLocationId = UUIDUtils.getId();
					SceneViewInfoModel sceneViewInfoModel = new SceneViewInfoModel();
					sceneViewInfoModel.setId(shootLocationId);
					sceneViewInfoModel.setvName(shootLocation);
					sceneViewInfoModel.setOrderNumber(0);
					sceneViewInfoModel.setCrewId(crewId);
					batchAddSceneViewInfoList.add(sceneViewInfoModel);
					shootMap.put(shootLocation, shootLocationId);
				}
				view.setShootLocationId(shootLocationId);
			}else{
				view.setShootLocationId(null);
			}
			
			
			//气氛信息
			/*String atmosphereId = genAtmosphereInfo(crewId, scenarioDto.getAtmosphere());
			if (StringUtils.isBlank(atmosphereId)) {
				atmosphereId = null;
			}*/
			String atmName = scenarioDto.getAtmosphere();
			String atmosphereId = atmMap.get(atmName);
			if(StringUtils.isBlank(atmosphereId)){
				AtmosphereInfoModel atmosphereInfoModel = new AtmosphereInfoModel();
				atmosphereId = UUIDUtils.getId();
				atmosphereInfoModel.setAtmosphereId(atmosphereId);
				atmosphereInfoModel.setAtmosphereName(atmName);
				atmosphereInfoModel.setCrewId(crewId);
				batchAddAtmList.add(atmosphereInfoModel);
				atmMap.put(atmName, atmosphereId);
			}
			Double pagecount = scenarioDto.getPageCount()!=null?scenarioDto.getPageCount():0.0;
			BigDecimal bdf1 = new BigDecimal(pagecount);
			view.setPageCount(bdf1.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
			view.setAtmosphereId(atmosphereId);
			
			viewInfoList.add(view);
			
			//场景内容信息
			String title = scenarioDto.getTitle();
			String content = scenarioDto.getContent();
			//ViewContentModel viewContent = this.viewContentDao.queryByViewId(viewId);
			ViewContentModel viewContent = null;
			if(viewContentList!=null&&viewContentList.size()>0){
				for(ViewContentModel viewContentModel :viewContentList){
					String  inView = viewContentModel.getViewId();
					if(inView.equals(viewId)){
						viewContent = viewContentModel;
					}
				}
			}
			
			if (!StringUtils.isBlank(scenarioDto.getTitle()) || !StringUtils.isBlank(scenarioDto.getContent())) {
				if (viewContent != null) {
					boolean isChanged = this.isViewContentChange(viewContent, content, title);
					if (isChanged && viewContent.getStatus() == ViewContentStatus.Published.getValue()) {
						viewContent.setStatus(ViewContentStatus.UpdateNotPublished.getValue());
//						viewContent.setReadedPeopleIds(null);
					}
					if (!StringUtils.isBlank(content)) {
						viewContent.setFigureprint(MD5Util.MD5(content));
					}
					
					viewContent.setTitle(title);
					viewContent.setContent(content);
					
					toUpdateViewContentList.add(viewContent);
				} else if (!StringUtils.isBlank(content)){//兼容没有剧本内容的场景
					//场景内容信息，肯定执行保存操作
					viewContent = this.genSceneContent(scenarioDto, crewId, viewId);
					toAddViewContentList.add(viewContent);
				}
			}
			
			//场景角色信息，
			//删除该场景下所有和角色的关联关系，然后再新建（考虑到当用户删除一个角色信息时，删除这个操作无法更新到数据库）
			//this.viewRoleMapDao.deleteManyByViewId(viewId);
			delViewRoleByViewId.add(viewId);
			
			//主演
			List<String> roleNameList = scenarioDto.getMajorRoleNameList();	
			//特约演员
			List<String> guestNameList = scenarioDto.getGuestRoleNameList();
			//群众演员
			List<String> massNameList = scenarioDto.getMassRoleNameList();
			//待定演员
			List<String> toConfirmNameList = scenarioDto.getToConfirmRoleNameList();
			
			Map<String,String> roleViewMap1 = new HashMap<String, String>();
			Map<String,String> roleViewMap2 = new HashMap<String, String>();
			Map<String,String> roleViewMap3 = new HashMap<String, String>();
			//主演   特约   群众演员
			arrangeRoleInfo(crewId, viewRoleList, batchAddRoleList, viewRoleMapList, viewId, roleNameList,ViewRoleType.MajorActor.getValue(),batchAddViewRoleMapList,batchUpdateViewRoleMapList,roleMap1,roleViewMap1);
			arrangeRoleInfo(crewId, viewRoleList, batchAddRoleList, viewRoleMapList, viewId, guestNameList,ViewRoleType.GuestActor.getValue(),batchAddViewRoleMapList,batchUpdateViewRoleMapList,roleMap1,roleViewMap1);
			arrangeRoleInfo(crewId, viewRoleList, batchAddRoleList, viewRoleMapList, viewId, massNameList,ViewRoleType.MassesActor.getValue(),batchAddViewRoleMapList,batchUpdateViewRoleMapList,roleMap1,roleViewMap1);
			arrangeRoleInfo(crewId, viewRoleList, batchAddRoleList, viewRoleMapList, viewId, toConfirmNameList,ViewRoleType.ToConfirmActor.getValue(),batchAddViewRoleMapList,batchUpdateViewRoleMapList,roleMap1,roleViewMap1);
			
			
			
			/*if (roleNameList != null && roleNameList.size() > 0) {
				this.viewRoleMapDao.deleteManyByViewId(viewId);
				for (String roleName : roleNameList) {
					this.saveRoleWithoutType(ViewRoleType.MajorActor.getValue(), crewId, viewId, roleName, 1);
				}
			}

			
			if (guestNameList != null) {
				for (String guestName : guestNameList) {
					this.saveRoleWithoutType(ViewRoleType.GuestActor.getValue(), crewId, viewId, guestName, 1);
				}
			}
			
			
			if (massNameList != null) {
				for (String massName : massNameList) {
					this.saveRoleWithoutType(ViewRoleType.MassesActor.getValue(), crewId, viewId, massName, 1);
				}
			}*/
			
			//服装
//			this.viewClothesMapDao.deleteManyByViewId(viewId);
			delViewClothByViewId.add(viewId);
			//this.saveClothes(scenarioDto.getClothes(), crewId, viewId, userInfo.getUserId(), userInfo.getUserName());
			String clothesInfo = scenarioDto.getClothes();
			arrangeViewClotheInfo(crewId, userInfo, goodsInfoList, batchAddClothesInfoList, viewClothesInfoMap,batchAddViewClothesInfoList, goodsNameMap, viewId, clothesInfo);
			
			//化妆
//			this.viewMakeupMapDao.deleteManyByViewId(viewId);
			//this.saveMakeupInfo(scenarioDto.getMakeups(), crewId, viewId, userInfo.getUserId(), userInfo.getUserName());
			String makeUps = scenarioDto.getMakeups();
			arrangeViewMakeUpInfo(crewId, userInfo, goodsInfoList, viewMakeUpInfoList, goodsNameMap,batchAddMakeUpInfoList, batchAddViewMakeUpMapList, viewId, makeUps);
			
			//道具信息
//			this.viewPropsMapDao.deleteManyByViewId(viewId);
			String propInfo = scenarioDto.getProps();
			String specialPropInfo = scenarioDto.getSpecialProps();
			//this.saveProps(scenarioDto.getProps(), crewId, viewId, userInfo.getUserId(), userInfo.getUserName(), PropsType.Normal.getValue());
			//this.saveProps(scenarioDto.getSpecialProps(), crewId, viewId, userInfo.getUserId(), userInfo.getUserName(), PropsType.Special.getValue());
			arrangeViewPropInfo(crewId, userInfo, goodsInfoList, viewPropMapList, goodsNameMap,batchAddPropsInfoList, batchAddViewPropsInfoList, viewId, propInfo,GoodsType.CommonProps.getValue());
			arrangeViewPropInfo(crewId, userInfo, goodsInfoList, viewPropMapList, goodsNameMap,batchAddPropsInfoList, batchAddViewPropsInfoList, viewId, specialPropInfo,GoodsType.SpecialProps.getValue());
			
			
			//场景信息
			delViewLocationByViewId.add(viewId);
			String firstLocation = scenarioDto.getFirstLocation();
			String secondLocation = scenarioDto.getSecondLocation();
			String thirdLocation = scenarioDto.getThirdLocation();
			arrangeViewLocationInfo(crewId, userInfo, locationInfoList, viewLocationMapList, locationNameMapFirst,batchAddLocationInfoList, batchAddViewLocationInfoList, viewId, firstLocation,LocationType.lvlOneLocation.getValue());
			arrangeViewLocationInfo(crewId, userInfo, locationInfoList, viewLocationMapList, locationNameMapSecond,batchAddLocationInfoList, batchAddViewLocationInfoList, viewId, secondLocation,LocationType.lvlTwoLocation.getValue());
			arrangeViewLocationInfo(crewId, userInfo, locationInfoList, viewLocationMapList, locationNameMapThird,batchAddLocationInfoList, batchAddViewLocationInfoList, viewId, thirdLocation,LocationType.lvlThreeLocation.getValue());
		
			
			//场景地点信息
//			this.viewLocationMapDao.deleteManyByViewId(viewId);
			/*//主场景
			this.saveViewLocation(scenarioDto.getFirstLocation(), LocationType.lvlOneLocation.getValue(), crewId, viewId);
			//次场景
			this.saveViewLocation(scenarioDto.getSecondLocation(), LocationType.lvlTwoLocation.getValue(), crewId, viewId);
			//三级场景
			this.saveViewLocation(scenarioDto.getThirdLocation(), LocationType.lvlThreeLocation.getValue(), crewId, viewId);*/
		}
		
		//批量删除场景角色信息
		viewRoleMapDao.deleteBatchByViewId(delViewRoleByViewId);
		
		//批量删除场景与物品的关联关系
		this.goodsInfoDao.deleteBatchByViewId(delViewClothByViewId);
		//批量删除场景  三级场景对照关系
		viewLocationMapDao.deleteBatchByViewId(delViewLocationByViewId);
		
		
		
		//批量保存气氛信息
		atmosphereDao.addBatch(batchAddAtmList, AtmosphereInfoModel.class);
		//批量保存拍摄地信息
	//	shootLocationDao.addBatch(batchAddShootList, ShootLocationModel.class);
		//批量保存角色信息
		viewRoleDao.addBatch(batchAddRoleList, ViewRoleModel.class);
		//批量保存角色场景对照信息
		viewRoleMapDao.addBatch(batchAddViewRoleMapList, ViewRoleMapModel.class);
		//批量修改角色场景对照信息
		viewRoleMapDao.updateBatch(batchUpdateViewRoleMapList, "mapId", ViewRoleMapModel.class);
		
		//批量保存服装信息
		goodsInfoDao.addBatch(batchAddClothesInfoList, GoodsInfoModel.class);
		//批量保存场景服装关联信息
		viewGoodsMapDao.addBatch(batchAddViewClothesInfoList, ViewGoodsInfoMap.class);
		
		//批量保存化妆信息
		goodsInfoDao.addBatch(batchAddMakeUpInfoList, GoodsInfoModel.class);
		//批量保存场景化妆对照信息
		viewGoodsMapDao.addBatch(batchAddViewMakeUpMapList, ViewGoodsInfoMap.class);
		
		//批量保存道具信息
		goodsInfoDao.addBatch(batchAddPropsInfoList, GoodsInfoModel.class);
		//批量保存场景道具对照信息
		viewGoodsMapDao.addBatch(batchAddViewPropsInfoList, ViewGoodsInfoMap.class);
		
		//批量保存多级场景信息
		viewLocationDao.addBatch(batchAddLocationInfoList, ViewLocationModel.class);
		//批量保存场景 多级场景对照信息
		viewLocationMapDao.addBatch(batchAddViewLocationInfoList, ViewLocationMapModel.class);
				
		//批量保存商值信息
		insideAdvertDao.addBatch(batchAddViewAdvertList, InsideAdvertModel.class);
		//批量删除商值场景对照信息
		viewAdvertMapDao.deleteBatchByViewId(batchDelViewAdvertMapList);
		//批量保存商值场景对照信息
		viewAdvertMapDao.addBatch(batchAddViewAdvertMapList, ViewAdvertMapModel.class);
		//批量保存勘景信息
		sceneViewInfoDao.addBatch(batchAddSceneViewInfoList, SceneViewInfoModel.class);
		
		this.viewInfoDao.updateManyViewInfo(viewInfoList);
		this.viewContentDao.updateManyViewContentInfo(toUpdateViewContentList);
		this.viewContentDao.addMany(toAddViewContentList);
	}
	
	/**
	 * 判读剧本内容是否改变
	 * @param viewContent 原有的剧本内容信息
	 * @param newContent	新内容
	 * @param newTitle	新标题
	 * @return
	 */
	public boolean isViewContentChange(ViewContentModel viewContent, String newContent, String newTitle) {
		boolean isTitleChanged = false;	//标题是否改变
		boolean isContentChanged = false;	//内容是否改变
		
		//比较内容是否改变
		String oldFigureprint = viewContent.getFigureprint();
		if (StringUtils.isBlank(oldFigureprint) && !StringUtils.isBlank(viewContent.getContent())) {
			oldFigureprint = MD5Util.MD5(viewContent.getContent());
		}
		if (oldFigureprint == null) {
			oldFigureprint = "";
		}
		String newFigureprint = "";
		if (!StringUtils.isBlank(newContent)) {
			newFigureprint = MD5Util.MD5(newContent);
		}
		if (!oldFigureprint.equals(newFigureprint)) {
			isContentChanged = true;
		}
		
		//比较标题是否改变
		String oldTitle = viewContent.getTitle();
		if (oldTitle == null) {
			oldTitle = "";
		}
		if (newTitle == null) {
			newTitle = "";
		}
		if (!oldTitle.equals(newTitle)) {
			isTitleChanged = true;
		}
		
		return isTitleChanged || isContentChanged;
	}

	/**
	 * 根据ScenarioViewDto批量添加场景信息
	 * @param sceDtoList
	 * @throws Exception 
	 */
	public void addManyBySceViewDto (List<ScenarioViewDto> sceDtoList, boolean isManualSave, String crewId, UserInfoModel userInfo) throws Exception {
		List<ViewInfoModel> viewInfoList = new ArrayList<ViewInfoModel>();
		List<ViewContentModel> viewContentList = new ArrayList<ViewContentModel>();
		
		if(sceDtoList==null||sceDtoList.size()==0){
			return;
		}
		//获取当前剧组下的气氛信息
		Map<String,String> atmMap = queryAtmByCrewId(crewId);
		//批量保存气氛信息
		List<AtmosphereInfoModel> batchAddAtmList = new ArrayList<AtmosphereInfoModel>();
		//获取当前剧组的拍摄地
		Map<String,String> shootMap = queryShootByCrewid(crewId);
		//List<ShootLocationModel> batchAddShootList = new ArrayList<ShootLocationModel>();
		//批量添加堪景信息
		List<SceneViewInfoModel> batchAddSceneViewInfoList = new ArrayList<SceneViewInfoModel>();
		
		
		//获取当前剧组下的角色信息
		List<ViewRoleModel> viewRoleList = viewRoleDao.queryByCrewId(crewId);
		//批量添加剧组角色信息
		List<ViewRoleModel> batchAddRoleList = new ArrayList<ViewRoleModel>();
		
		//批量添加角色场景关联信息
		List<ViewRoleMapModel> batchAddViewRoleMapList = new ArrayList<ViewRoleMapModel>();
		//批量修改角色场景关联信息
		List<ViewRoleMapModel> batchUpdateViewRoleMapList = new ArrayList<ViewRoleMapModel>();
		//根据剧组id查询角色与场景表关联关系
		List<ViewRoleMapModel> viewRoleMapList = viewRoleMapDao.queryViewRoleMapInfoByCrewId(crewId);
		
		Map<String,String> roleMap1 = new HashMap<String, String>();
		Map<String,String> roleMap2 = new HashMap<String, String>();
		Map<String,String> roleMap3 = new HashMap<String, String>();

		//服装数据
		Map<String, Object> clothCondition = new HashMap<String, Object>();
		clothCondition.put("crewId", crewId);
		clothCondition.put("goodsType", 3);
		List<GoodsInfoModel> clothesInfoList = this.goodsInfoDao.queryGoodsByCondition(clothCondition);
		//批量保存服装数据
		List<GoodsInfoModel> batchAddClothesInfoList = new ArrayList<GoodsInfoModel>();
		//场景服装对照信息viewClothesInfoMap
		List<ViewGoodsInfoMap> viewClothesInfoMap = this.viewGoodsMapDao.queryClothViewInfo(crewId, GoodsType.Clothes.getValue());
		//批量保存服装场景信息
		List<ViewGoodsInfoMap> batchAddViewClothesInfoList = new ArrayList<ViewGoodsInfoMap>();
		//服装名称
		Map<String,String> clotheNameMap = new HashMap<String,String>();
		
		
		//化妆信息
		Map<String, Object> makeupCondition = new HashMap<String, Object>();
		makeupCondition.put("crewId", crewId);
		makeupCondition.put("goodsType", 2);
		List<GoodsInfoModel> makeUpInfoList = this.goodsInfoDao.queryGoodsByCondition(makeupCondition);
		//场景化妆对照信息
		List<ViewGoodsInfoMap> viewMakeUpInfoList = this.viewGoodsMapDao.queryClothViewInfo(crewId, GoodsType.Makeup.getValue());
		//化妆名称
		Map<String,String> makeupNameMap = new HashMap<String,String>();
		//批量保存化妆信息
		List<GoodsInfoModel> batchAddMakeUpInfoList = new ArrayList<GoodsInfoModel>();
		//批量保存场景化妆对照关系
		List<ViewGoodsInfoMap> batchAddViewMakeUpMapList = new ArrayList<ViewGoodsInfoMap>();
		
		//批量保存道具信息
		List<GoodsInfoModel> batchAddPropsInfoList = new ArrayList<GoodsInfoModel>();
		//批量保存场景道具信息
		List<ViewGoodsInfoMap> batchAddViewPropsInfoList = new ArrayList<ViewGoodsInfoMap>();
		//道具名称
		Map<String,String> propNameMap1 = new HashMap<String,String>();
		Map<String,String> propNameMap2 = new HashMap<String,String>();
		//道具信息
		List<GoodsInfoModel> propsInfoList = goodsInfoDao.queryCrewPropInfo(crewId);
		//服化道信息
		List<GoodsInfoModel> goodsInfoList = new ArrayList<GoodsInfoModel>();
		goodsInfoList.addAll(clothesInfoList);
		goodsInfoList.addAll(makeUpInfoList);
		goodsInfoList.addAll(propsInfoList);
		//服化道名称信息
		Map<String,String> goodsNameMap = new HashMap<String,String>();
		
		//场景道具信息
		List<ViewGoodsInfoMap> viewPropMapList = viewGoodsMapDao.queryPropViewMap(crewId);
		
		//批量保存道具信息
		List<ViewLocationModel> batchAddLocationInfoList = new ArrayList<ViewLocationModel>();
		//批量保存场景道具信息
		List<ViewLocationMapModel> batchAddViewLocationInfoList = new ArrayList<ViewLocationMapModel>();
		//道具名称
		Map<String,String> locationNameMapFirst = new HashMap<String,String>();
		Map<String,String> locationNameMapSecond = new HashMap<String,String>();
		Map<String,String> locationNameMapThird = new HashMap<String,String>();
		//道具信息
		List<ViewLocationModel> locationInfoList = viewLocationDao.queryLocationInfoByCrewId(crewId);
		//场景道具信息
		List<ViewLocationMapModel> viewLocationMapList = viewLocationMapDao.queryViewLocationMapByCrewId(crewId);
		
		//批量保存商值信息
		List<InsideAdvertModel> insideAdvertList = new ArrayList<InsideAdvertModel>();
		//批量爆粗商值场景表对照信息
		List<ViewAdvertMapModel> viewAdvertMapList = new ArrayList<ViewAdvertMapModel>();
		//商值   id  名称
		Map<String, String> advertMap = new HashMap<String, String>();
		
		//由于勘景信息关联到场景表中，导入场景信息的时候会保存勘景信息，勘景信息中需要保存附件包信息
		List<AttachmentPacketModel> attachmentPacketModelList = new ArrayList<AttachmentPacketModel>();
		
		
		for (ScenarioViewDto scenarioDto : sceDtoList) {
			
			
			//气氛信息，有可能不操作
			//String atmosphereId = genAtmosphereInfo(crewId, scenarioDto.getAtmosphere());
			
			String atmName = scenarioDto.getAtmosphere();
			
			String atmosphereId = atmMap.get(atmName);
			if(StringUtils.isBlank(atmosphereId)){
				AtmosphereInfoModel atmosphereInfoModel = new AtmosphereInfoModel();
				atmosphereId = UUIDUtils.getId();
				atmosphereInfoModel.setAtmosphereId(atmosphereId);
				atmosphereInfoModel.setAtmosphereName(atmName);
				atmosphereInfoModel.setCrewId(crewId);
				batchAddAtmList.add(atmosphereInfoModel);
				
				atmMap.put(atmName, atmosphereId);
			}
			
			
			//场景信息，肯定执行保存操作,需要气氛ID
			ViewInfoModel viewInfo = this.genViewInfo(scenarioDto, isManualSave, crewId, atmosphereId);
			
			
			//拍摄地点
			/*ShootLocationModel shootLocationInfo = this.addOrGetShootLocationByLocationAndCrewId(scenarioDto.getShootLocation(), crewId);
			String shootLocationId = null;
			if (shootLocationInfo != null) {
				shootLocationId = shootLocationInfo.getShootLocationId();
			}*/
			String shootLocation = scenarioDto.getShootLocation();
			if(StringUtils.isNotBlank(shootLocation)){
				String shootLocationId = shootMap.get(shootLocation);
				if(StringUtils.isBlank(shootLocationId)){
					/*ShootLocationModel shootLocationModel = new ShootLocationModel();
					shootLocationId = UUIDUtils.getId();
					shootLocationModel.setShootLocationId(shootLocationId);
					shootLocationModel.setShootLocation(shootLocation);
					shootLocationModel.setCrewId(crewId);
					batchAddShootList.add(shootLocationModel);*/
					shootLocationId = UUIDUtils.getId();
					SceneViewInfoModel sceneViewInfoModel = new SceneViewInfoModel();
					sceneViewInfoModel.setId(shootLocationId);
					sceneViewInfoModel.setvName(shootLocation);
					sceneViewInfoModel.setOrderNumber(0);
					sceneViewInfoModel.setCrewId(crewId);
					batchAddSceneViewInfoList.add(sceneViewInfoModel);
					shootMap.put(shootLocation, shootLocationId);
					
					//保存附件包信息
					AttachmentPacketModel attachmentPacketModel = new AttachmentPacketModel();
					attachmentPacketModel.setId(shootLocationId);
					attachmentPacketModel.setCrewId(crewId);
					attachmentPacketModel.setCreateTime(new Date());
					
					attachmentPacketModelList.add(attachmentPacketModel);
					
				}
				viewInfo.setShootLocationId(shootLocationId);
			}
			
			//拍摄时间
			String shootTime = scenarioDto.getShootTime();
			if(StringUtils.isNotBlank(shootTime)){
				shootTime = DateUtils.formatToString(shootTime, 0);
				viewInfo.setShotDate(new SimpleDateFormat("yyyy-MM-dd").parse(shootTime));
			}
			
			//商值
			String commercialImplants = scenarioDto.getCommercialImplants();
			if(StringUtils.isNotBlank(commercialImplants)){
				String[] commStrs = StringUtils.split(commercialImplants,",");
				if(commStrs!=null&&commStrs.length>0){
					for(String commTemp :commStrs){
						String id = advertMap.get(commTemp);
						String advertId = "";
						if(StringUtils.isBlank(id)){
							InsideAdvertModel insideAdvertModel = new InsideAdvertModel();
							advertId = UUIDUtils.getId();
							insideAdvertModel.setAdvertId(advertId);
							insideAdvertModel.setAdvertName(commTemp);
							insideAdvertModel.setCrewId(crewId);
							insideAdvertList.add(insideAdvertModel);
							advertMap.put(commTemp, advertId);
						}else{
							advertId = id;
						}
						
						
						ViewAdvertMapModel map = new ViewAdvertMapModel();
						map.setAdvertId(advertId);
						map.setCrewId(crewId);
						map.setMapId(UUIDUtils.getId());
						map.setViewId(viewInfo.getViewId());
						map.setAdvertType("1");//广告类型  1：道具；2：台词；99：其他   默认道具
						viewAdvertMapList.add(map);
					}
				}
				
			}
			viewInfoList.add(viewInfo);
			//场景内容信息，肯定执行保存操作
			ViewContentModel viewContent = this.genSceneContent(scenarioDto, crewId, viewInfo.getViewId());
			viewContentList.add(viewContent);
			
			
			//场景角色信息
			//主要演员
			List<String> roleNameList = scenarioDto.getMajorRoleNameList();	
			//特约演员
			List<String> guestNameList = scenarioDto.getGuestRoleNameList();
			//群众演员
			List<String> massNameList = scenarioDto.getMassRoleNameList();
			//待定演员
			List<String> toConfirmNameList = scenarioDto.getToConfirmRoleNameList();
			/*if (roleNameList != null) {
				for (String roleName : roleNameList) {
					this.saveRoleWithoutType(ViewRoleType.MajorActor.getValue(), crewId, viewInfo.getViewId(), roleName, 1);
				}
			}
			
			if (guestNameList != null) {
				for (String guestName : guestNameList) {
					this.saveRoleWithoutType(ViewRoleType.GuestActor.getValue(), crewId, viewInfo.getViewId(), guestName, 1);
				}
			}
			
			
			if (massNameList != null) {
				for (String massName : massNameList) {
					this.saveRoleWithoutType(ViewRoleType.MassesActor.getValue(), crewId, viewInfo.getViewId(), massName, 1);
				}
			}*/
			Map<String,String> roleViewMap1 = new HashMap<String, String>();
			Map<String,String> roleViewMap2 = new HashMap<String, String>();
			Map<String,String> roleViewMap3 = new HashMap<String, String>();
			Map<String,String> roleViewMap4 = new HashMap<String, String>();
			//主演   特约   群众演员
			arrangeRoleInfo(crewId, viewRoleList, batchAddRoleList,
					viewRoleMapList, viewInfo.getViewId(), roleNameList,
					ViewRoleType.MajorActor.getValue(),
					batchAddViewRoleMapList, batchUpdateViewRoleMapList,
					roleMap1, roleViewMap1);
			arrangeRoleInfo(crewId, viewRoleList, batchAddRoleList,
					viewRoleMapList, viewInfo.getViewId(), guestNameList,
					ViewRoleType.GuestActor.getValue(),
					batchAddViewRoleMapList, batchUpdateViewRoleMapList,
					roleMap1, roleViewMap1);
			arrangeRoleInfo(crewId, viewRoleList, batchAddRoleList,
					viewRoleMapList, viewInfo.getViewId(), massNameList,
					ViewRoleType.MassesActor.getValue(),
					batchAddViewRoleMapList, batchUpdateViewRoleMapList,
					roleMap1, roleViewMap1);
			arrangeRoleInfo(crewId, viewRoleList, batchAddRoleList,
					viewRoleMapList, viewInfo.getViewId(), toConfirmNameList,
					ViewRoleType.ToConfirmActor.getValue(),
					batchAddViewRoleMapList, batchUpdateViewRoleMapList,
					roleMap1, roleViewMap1);
			
			//服装信息
			String clothesInfo = scenarioDto.getClothes();
			arrangeViewClotheInfo(crewId, userInfo, goodsInfoList, batchAddClothesInfoList, viewClothesInfoMap,batchAddViewClothesInfoList, goodsNameMap, viewInfo.getViewId(), clothesInfo);
			//服装
			//this.saveClothes(scenarioDto.getClothes(), crewId, viewInfo.getViewId(), userInfo.getUserId(), userInfo.getUserName());
			
			
			//化妆
			String makeUps = scenarioDto.getMakeups();
			arrangeViewMakeUpInfo(crewId, userInfo, goodsInfoList, viewMakeUpInfoList, goodsNameMap,batchAddMakeUpInfoList, batchAddViewMakeUpMapList, viewInfo.getViewId(), makeUps);
			
			//化妆
			//this.saveMakeupInfo(scenarioDto.getMakeups(), crewId, viewInfo.getViewId(), userInfo.getUserId(), userInfo.getUserName());
			//道具信息
			//this.saveProps(scenarioDto.getProps(), crewId, viewInfo.getViewId(), userInfo.getUserId(), userInfo.getUserName(), PropsType.Normal.getValue());
			//this.saveProps(scenarioDto.getSpecialProps(), crewId, viewInfo.getViewId(), userInfo.getUserId(), userInfo.getUserName(), PropsType.Special.getValue());
			
			String propInfo = scenarioDto.getProps();
			String specialPropInfo = scenarioDto.getSpecialProps();
			arrangeViewPropInfo(crewId, userInfo, goodsInfoList, viewPropMapList, goodsNameMap,batchAddPropsInfoList, batchAddViewPropsInfoList, viewInfo.getViewId(), propInfo,GoodsType.CommonProps.getValue());
			arrangeViewPropInfo(crewId, userInfo, goodsInfoList, viewPropMapList, goodsNameMap,batchAddPropsInfoList, batchAddViewPropsInfoList, viewInfo.getViewId(), specialPropInfo,GoodsType.SpecialProps.getValue());
			
			
			
			//场景地点信息
			//主场景
			//this.saveViewLocation(scenarioDto.getFirstLocation(), LocationType.lvlOneLocation.getValue(), crewId, viewInfo.getViewId());
			//次场景
			//this.saveViewLocation(scenarioDto.getSecondLocation(), LocationType.lvlTwoLocation.getValue(), crewId, viewInfo.getViewId());
			//三级场景
			//this.saveViewLocation(scenarioDto.getThirdLocation(), LocationType.lvlThreeLocation.getValue(), crewId, viewInfo.getViewId());
			
			//场景信息
			String firstLocation = scenarioDto.getFirstLocation();
			String secondLocation = scenarioDto.getSecondLocation();
			String thirdLocation = scenarioDto.getThirdLocation();
			arrangeViewLocationInfo(crewId, userInfo, locationInfoList, viewLocationMapList, locationNameMapFirst,batchAddLocationInfoList, batchAddViewLocationInfoList, viewInfo.getViewId(), firstLocation,LocationType.lvlOneLocation.getValue());
			arrangeViewLocationInfo(crewId, userInfo, locationInfoList, viewLocationMapList, locationNameMapSecond,batchAddLocationInfoList, batchAddViewLocationInfoList, viewInfo.getViewId(), secondLocation,LocationType.lvlTwoLocation.getValue());
			arrangeViewLocationInfo(crewId, userInfo, locationInfoList, viewLocationMapList, locationNameMapThird,batchAddLocationInfoList, batchAddViewLocationInfoList, viewInfo.getViewId(), thirdLocation,LocationType.lvlThreeLocation.getValue());
		}
		
		//批量保存气氛信息
		atmosphereDao.addBatch(batchAddAtmList, AtmosphereInfoModel.class);
		//批量保存拍摄地信息
		//shootLocationDao.addBatch(batchAddShootList, ShootLocationModel.class);
		//批量保存角色信息
		viewRoleDao.addBatch(batchAddRoleList, ViewRoleModel.class);
		//批量保存角色场景对照信息
		viewRoleMapDao.addBatch(batchAddViewRoleMapList, ViewRoleMapModel.class);
		//批量修改角色场景对照信息
		viewRoleMapDao.updateBatch(batchUpdateViewRoleMapList, "mapId", ViewRoleMapModel.class);
		
		//批量保存服装信息
		goodsInfoDao.addBatch(batchAddClothesInfoList, GoodsInfoModel.class);
		//批量保存场景服装关联信息
		viewGoodsMapDao.addBatch(batchAddViewClothesInfoList, ViewGoodsInfoMap.class);
		
		//批量保存化妆信息
		goodsInfoDao.addBatch(batchAddMakeUpInfoList, GoodsInfoModel.class);
		//批量保存场景化妆对照信息
		viewGoodsMapDao.addBatch(batchAddViewMakeUpMapList, ViewGoodsInfoMap.class);
		
		//批量保存道具信息
		goodsInfoDao.addBatch(batchAddPropsInfoList, GoodsInfoModel.class);
		//批量保存场景道具对照信息
		viewGoodsMapDao.addBatch(batchAddViewPropsInfoList, ViewGoodsInfoMap.class);
		
		//批量保存多级场景信息
		viewLocationDao.addBatch(batchAddLocationInfoList, ViewLocationModel.class);
		//批量保存场景 多级场景对照信息
		viewLocationMapDao.addBatch(batchAddViewLocationInfoList, ViewLocationMapModel.class);
		
		//批量保存商值信息
		insideAdvertDao.addBatch(insideAdvertList, InsideAdvertModel.class);
		//批量保存商值场景对照信息
		viewAdvertMapDao.addBatch(viewAdvertMapList, ViewAdvertMapModel.class);
		
		//批量保存勘景信息
		sceneViewInfoDao.addBatch(batchAddSceneViewInfoList, SceneViewInfoModel.class);
		//保存勘景信息对应的附件包信息
		attachmentPacketDao.addBatch(attachmentPacketModelList, AttachmentPacketModel.class);
		
		
		//保存信息
		this.viewInfoDao.addMany(viewInfoList);
		this.viewContentDao.addMany(viewContentList);
	}
	
	
	/**
	 * @Description 整理 主、次、三级场景信息用于批量保存
	 * @param crewId  剧组id
	 * @param userInfo  用户信息
	 * @param locationInfoList   场景信息
	 * @param viewLocationMapList   场景     主、次、三级场景对照关系
	 * @param locationNameMap  主、次、三级名称
	 * @param batchAddLocationInfoList  批量保存 主、次、三级数据
	 * @param batchAddViewLocationInfoList  批量保存  场景    主、次、三级场景信息对照关系
	 * @param viewInfo  场景信息
	 * @param locationInfo  场景字符串
	 * @param locationType   场景类型
	 */
	private void arrangeViewLocationInfo(String crewId, UserInfoModel userInfo, List<ViewLocationModel> locationInfoList,
			List<ViewLocationMapModel> viewLocationMapList, Map<String, String> locationNameMap,
			List<ViewLocationModel> batchAddLocationInfoList, List<ViewLocationMapModel> batchAddViewLocationInfoList,
			String viewId, String location,Integer locationType){
		if(StringUtils.isNotBlank(location)){
			Map<String, String> vlMap = new HashMap<String, String>();//view location map
			String locationId = "";
			if(locationInfoList!=null&&locationInfoList.size()>0){
				for(ViewLocationModel viewLocationModel:locationInfoList){
					if(location.equals(viewLocationModel.getLocation())&&locationType == viewLocationModel.getLocationType()){
						locationId = viewLocationModel.getLocationId();
						break;
					}
				}
			}
			//处理是否需要添加服装信息
			if(StringUtils.isBlank(locationId)){
				String lid = locationNameMap.get(location);
				if(StringUtils.isBlank(lid)){
					locationId = UUIDUtils.getId();
					ViewLocationModel viewLocationModel = new ViewLocationModel();
					viewLocationModel.setCrewId(crewId);
					viewLocationModel.setLocation(location);
					viewLocationModel.setLocationId(locationId);
					viewLocationModel.setLocationType(locationType);
					batchAddLocationInfoList.add(viewLocationModel);
					locationNameMap.put(location, locationId);
				}else{
					locationId = lid;
				}
			}
			
			
			//处理是否需要添加场景化妆对照信息
			if(StringUtils.isNotBlank(locationId)){
				ViewLocationMapModel viewLocationMapModel = null;
				if(viewLocationMapList!=null&&viewLocationMapList.size()>0){
					for(ViewLocationMapModel temp :viewLocationMapList){
						String lid = temp.getLocationId();
						String vid = temp.getViewId();
						if(locationId.equals(lid)&&vid.equals(viewId)){
							viewLocationMapModel = temp;
							break;
						}
					}
				}
				if(viewLocationMapModel!=null){
					String clid = vlMap.get(location);
					if(StringUtils.isBlank(clid)){
						batchAddViewLocationInfoList.add(viewLocationMapModel);
						vlMap.put(location, locationId);
					}
				}else{
					String clid = vlMap.get(location);
					if(StringUtils.isBlank(clid)){
						viewLocationMapModel = new ViewLocationMapModel();
						viewLocationMapModel.setCrewId(crewId);
						viewLocationMapModel.setLocationId(locationId);
						viewLocationMapModel.setMapId(UUIDUtils.getId());
						viewLocationMapModel.setViewId(viewId);
						batchAddViewLocationInfoList.add(viewLocationMapModel);
						vlMap.put(location, locationId);
					}
				}
			}
		}
	}
	
	
	
	/**
	 * @Description  整理数据  批量保存道具信息  场景 道具对照信息
	 * @param crewId  剧组id
	 * @param userInfo  用户信息
	 * @param propsInfoList  道具信息
	 * @param viewPropMapList  场景道具对照信息
	 * @param propNameMap  道具名称
	 * @param batchAddPropsInfoList   批量添加道具信息集合
	 * @param batchAddViewPropsInfoList   批量添加场景  道具 对照关系集合
	 * @param viewInfo   场景新
	 * @param propsInfo  道具字符串
	 * @param propsType   道具类型
	 */
	private void arrangeViewPropInfo(String crewId, UserInfoModel userInfo, List<GoodsInfoModel> propsInfoList,
			List<ViewGoodsInfoMap> viewPropMapList, Map<String, String> propNameMap,
			List<GoodsInfoModel> batchAddPropsInfoList, List<ViewGoodsInfoMap> batchAddViewPropsInfoList,
			String viewId, String propsInfo,Integer propsType){
		if(StringUtils.isNotBlank(propsInfo)){
			String[] propsInfoArray = propsInfo.split(IMPORTANT_VIEW_REGEX);
			if(propsInfoArray!=null&&propsInfoArray.length>0){
				Map<String, String> vpMap = new HashMap<String, String>();//view prop map
				for(String propsName :propsInfoArray){
					if(StringUtils.isBlank(propsName)){
						continue;
					}
					String propsId = "";
					if(propsInfoList!=null&&propsInfoList.size()>0){
						for(GoodsInfoModel model:propsInfoList){
							if(propsName.equals(model.getGoodsName())){ // &&propsType == model.getGoodsType() 如果库里已经有一个同名的服化道，则按库里的来，不对比类型
								propsId = model.getId();
								break;
							}
						}
					}
					//处理是否需要添加服装信息
					if(StringUtils.isBlank(propsId)){
						String pid = propNameMap.get(propsName);
						if(StringUtils.isBlank(pid)){
							propsId = UUIDUtils.getId();
							GoodsInfoModel model = new GoodsInfoModel();
							model.setCreateTime(new Date());
							model.setCrewId(crewId);
							model.setId(propsId);
							model.setGoodsName(propsName);;
							model.setGoodsType(propsType);
							model.setUserId(userInfo.getUserId());
							model.setUserName(userInfo.getUserName());
							batchAddPropsInfoList.add(model);
							propNameMap.put(propsName, propsId);
						}else{
							propsId = pid;
						}
					}
					
					
					//处理是否需要添加场景化妆对照信息
					if(StringUtils.isNotBlank(propsId)){
						ViewGoodsInfoMap mapMOdel = null;
						if(viewPropMapList!=null&&viewPropMapList.size()>0){
							for(ViewGoodsInfoMap temp :viewPropMapList){
								String pid = temp.getId();
								String vid = temp.getViewId();
								if(propsId.equals(pid)&&vid.equals(viewId)){
									mapMOdel = temp;
									break;
								}
							}
						}
						if(mapMOdel!=null){
							String clid = vpMap.get(propsName);
							if(StringUtils.isBlank(clid)){
								batchAddViewPropsInfoList.add(mapMOdel);
								vpMap.put(propsName, propsId);
							}
						}else{
							String clid = vpMap.get(propsName);
							if(StringUtils.isBlank(clid)){
								ViewGoodsInfoMap goodMapModel = new ViewGoodsInfoMap();
								goodMapModel.setCrewId(crewId);
								goodMapModel.setId(UUIDUtils.getId());
								goodMapModel.setGoodsId(propsId);
								goodMapModel.setViewId(viewId);
								batchAddViewPropsInfoList.add(goodMapModel);
								vpMap.put(propsName, propsId);
							}
						}
					}
				}
				
			}
		}
	}
	
	/**
	 * @Description 整理批量保存场景 化妆信息
	 * @param crewId  剧组id
	 * @param userInfo  用户信息
	 * @param makeUpInfoList  化妆信息
	 * @param viewMakeUpInfoList  场景化妆对照信息
	 * @param makeupNameMap  化妆名称
	 * @param batchAddMakeUpInfoList 批量保存化妆信息
	 * @param batchAddViewMakeUpMapList  批量保存场景 化妆对照信息
	 * @param viewInfo   场景信息
	 * @param makeUps 化妆信息字符串
	 */
	private void arrangeViewMakeUpInfo(String crewId, UserInfoModel userInfo, List<GoodsInfoModel> makeUpInfoList,
			List<ViewGoodsInfoMap> viewMakeUpInfoList, Map<String, String> makeupNameMap,
			List<GoodsInfoModel> batchAddMakeUpInfoList, List<ViewGoodsInfoMap> batchAddViewMakeUpMapList,
			String viewId, String makeUps) {
		if(StringUtils.isNotBlank(makeUps)){
			String[] makeupArray = makeUps.split(IMPORTANT_VIEW_REGEX);
			if(makeupArray!=null&&makeupArray.length>0){
				Map<String, String> vmMap = new HashMap<String, String>();//view makeup map
				for(String makeupName :makeupArray){
					if(StringUtils.isBlank(makeupName)){
						continue;
					}
					String makeupId = "";
					if(makeUpInfoList!=null&&makeUpInfoList.size()>0){
						for(GoodsInfoModel model:makeUpInfoList){
							if(makeupName.equals(model.getGoodsName())){
								makeupId = model.getId();
								break;
							}
						}
					}
					//处理是否需要添加服装信息
					if(StringUtils.isBlank(makeupId)){
						String mid = makeupNameMap.get(makeupName);
						if(StringUtils.isBlank(mid)){
							makeupId = UUIDUtils.getId();
							GoodsInfoModel model = new GoodsInfoModel();
							model.setCreateTime(new Date());
							model.setCrewId(crewId);
							model.setId(makeupId);
							model.setGoodsName(makeupName);
							model.setUserId(userInfo.getUserId());
							model.setUserName(userInfo.getUserName());
							model.setGoodsType(GoodsType.Makeup.getValue());
							
							batchAddMakeUpInfoList.add(model);
							makeupNameMap.put(makeupName, makeupId);
						}else{
							makeupId = mid;
						}
					}
					
					
					//处理是否需要添加场景化妆对照信息
					if(StringUtils.isNotBlank(makeupId)){
						ViewGoodsInfoMap mapMOdel = null;
						if(viewMakeUpInfoList!=null&&viewMakeUpInfoList.size()>0){
							for(ViewGoodsInfoMap temp :viewMakeUpInfoList){
								String mid = temp.getGoodsId();
								String vid = temp.getViewId();
								if(makeupId.equals(mid)&&vid.equals(viewId)){
									mapMOdel = temp;
									break;
								}
							}
						}
						if(mapMOdel!=null){
							String clid = vmMap.get(makeupName);
							if(StringUtils.isBlank(clid)){
								batchAddViewMakeUpMapList.add(mapMOdel);
								vmMap.put(makeupName, makeupId);
							}
						}else{
							String clid = vmMap.get(makeupName);
							if(StringUtils.isBlank(clid)){
								ViewGoodsInfoMap goodMapMOdel = new ViewGoodsInfoMap();
								goodMapMOdel.setCrewId(crewId);
								goodMapMOdel.setGoodsId(makeupId);
								goodMapMOdel.setId(UUIDUtils.getId());
								goodMapMOdel.setViewId(viewId);
								batchAddViewMakeUpMapList.add(goodMapMOdel);
								vmMap.put(makeupName, makeupId);
							}
						}
					}
				}
				
			}
		}
	}

	
	
	
	/**
	 * @Description 整理批量保存服装信息
	 * @param crewId  剧组id
	 * @param userInfo 用户新
	 * @param clothesInfoList  服装信息
	 * @param batchAddClothesInfoList  批量添加服装信息集合
	 * @param viewClothesInfoMap   场景 服装对照关系
	 * @param batchAddViewClothesInfoList  批量添加场景  服装对照关系
	 * @param clotheNameMap  服装名称
	 * @param viewInfo  场景信息
	 * @param clothesInfo  服装字符串
	 */
	private void arrangeViewClotheInfo(String crewId, UserInfoModel userInfo, List<GoodsInfoModel> clothesInfoList,
			List<GoodsInfoModel> batchAddClothesInfoList, List<ViewGoodsInfoMap> viewClothesInfoMap,
			List<ViewGoodsInfoMap> batchAddViewClothesInfoList, Map<String, String> clotheNameMap,
			String  viewId, String clothesInfo) {
		Map<String, String> vcMap = new HashMap<String, String>();//view cloth map
		if(StringUtils.isNotBlank(clothesInfo)){
			String[] clothesInfoArray = clothesInfo.split(IMPORTANT_VIEW_REGEX);
			if(clothesInfoArray!=null&&clothesInfoArray.length>0){
				for(String clo :clothesInfoArray){
					String clothesId = "";
					if(StringUtils.isBlank(clo)){
						continue;
					}
					//处理是否需要添加服装信息
					if(clothesInfoList!=null&&clothesInfoList.size()>0){
						for(GoodsInfoModel clothesInfoModel: clothesInfoList){
							if(clo.equals(clothesInfoModel.getGoodsName())){
								clothesId = clothesInfoModel.getId();
								break;
							}
						}
					}
					
					if(StringUtils.isBlank(clothesId)){
						String cid = clotheNameMap.get(clo);
						if(StringUtils.isBlank(cid)){
							clothesId = UUIDUtils.getId();
							GoodsInfoModel model = new GoodsInfoModel();
							model.setId(clothesId);
							model.setGoodsName(clo);
							model.setCreateTime(new Date());
							model.setCrewId(crewId);
							model.setUserId(userInfo.getUserId());
							model.setUserName(userInfo.getUserName());
							model.setGoodsType(GoodsType.Clothes.getValue());
							batchAddClothesInfoList.add(model);
							clotheNameMap.put(clo, clothesId);
						}else{
							clothesId = cid;
						}
					}
					//处理是否需要添加场景服装对照信息
					if(StringUtils.isNotBlank(clothesId)){
						ViewGoodsInfoMap model = null;
						if(viewClothesInfoMap!=null&&viewClothesInfoMap.size()>0){
							for(ViewGoodsInfoMap temp :viewClothesInfoMap){
								String clid = temp.getGoodsId();
								String vid = temp.getViewId();
								if(clothesId.equals(clid)&&vid.equals(viewId)){
									model = temp;
									break;
								}
							}
						}
						if(model!=null){
							String clid = vcMap.get(clo);
							if(StringUtils.isBlank(clid)){
								batchAddViewClothesInfoList.add(model);
								vcMap.put(clo, clothesId);
							}
						}else{
							String clid = vcMap.get(clo);
							if(StringUtils.isBlank(clid)){
								ViewGoodsInfoMap mapMOdel = new ViewGoodsInfoMap();
								mapMOdel.setGoodsId(clothesId);
								mapMOdel.setCrewId(crewId);
								mapMOdel.setId(UUIDUtils.getId());
								mapMOdel.setViewId(viewId);
								batchAddViewClothesInfoList.add(mapMOdel);
								vcMap.put(clo, clothesId);
							}
						}
					}
				}
			}
		}
	}

	
	/**
	 * @Description //整理角色信息
	 * @param crewId 剧组id
	 * @param viewRoleList 数据库中场景角色信息
	 * @param batchAddRoleList  批量添加角色信息
	 * @param viewRoleMapList  数据库中场景角色对照信息
	 * @param viewId 场景id
	 * @param roleNameList 文件中角色名称
	 * @param roleType  角色类型   主演   特约演员  群众演员
	 * @param batchAddViewRoleMapList 批量添加剧组 角色对照
	 * @param batchUpdateViewRoleMapList  批量修改场景角色对照信息
	 * @param roleMap 
	 * @param roleViewMap
	 */
	private void arrangeRoleInfo(String crewId, List<ViewRoleModel> viewRoleList, List<ViewRoleModel> batchAddRoleList,
			List<ViewRoleMapModel> viewRoleMapList, String viewId, List<String> roleNameList,int roleType,List<ViewRoleMapModel> batchAddViewRoleMapList,
			List<ViewRoleMapModel> batchUpdateViewRoleMapList,Map<String,String> roleMap,Map<String,String> roleViewMap) {
		if(roleNameList!=null&&roleNameList.size()>0){
			Set<String> setRoleid = new HashSet<String>();
			for (String roleName : roleNameList) {
				if(StringUtils.isBlank(roleName)){
					continue;
				}
				String roleId = "";
				
				if(viewRoleList!=null&&viewRoleList.size()>0){
					for(ViewRoleModel viewRoleModel :viewRoleList){
						int type = viewRoleModel.getViewRoleType();
						String roleN = viewRoleModel.getViewRoleName();
						if(roleName.equals(roleN)){
							roleId = viewRoleModel.getViewRoleId();
							break;
						}
					}
				}
				
				if(StringUtils.isBlank(roleId)){
					String rid = roleMap.get(roleName);
					if(StringUtils.isBlank(rid)&&StringUtils.isNotBlank(roleName)){
						ViewRoleModel viewRoleModel = new ViewRoleModel();
						roleId = UUIDUtils.getId();
						viewRoleModel.setViewRoleId(roleId);
						viewRoleModel.setCrewId(crewId);
						viewRoleModel.setViewRoleName(roleName);
						viewRoleModel.setViewRoleType(roleType);
						batchAddRoleList.add(viewRoleModel);
						
						roleMap.put(roleName, roleId);
					}else{
						roleId = rid;
					}
					
				}
				
				ViewRoleMapModel viewRoleMapModel = null;
				for(ViewRoleMapModel temp :viewRoleMapList){
					String rId = temp.getViewRoleId();
					String vid = temp.getViewId();
					if(rId.equals(roleId)&&vid.equals(viewId)){
						viewRoleMapModel = temp;
						break;
					}
				}
				if(viewRoleMapModel!=null){
					int beforeSize = setRoleid.size();
					setRoleid.add(roleId);
					int afterSize = setRoleid.size();
					if(afterSize != beforeSize){
						viewRoleMapModel.setRoleNum(1);
						batchUpdateViewRoleMapList.add(viewRoleMapModel);
					}
				}else{
					if(StringUtils.isNotBlank(roleId)){
						int beforeSize = setRoleid.size();
						setRoleid.add(roleId);
						int afterSize = setRoleid.size();
						if(afterSize != beforeSize){
							viewRoleMapModel = new ViewRoleMapModel();
							viewRoleMapModel.setCrewId(crewId);
							viewRoleMapModel.setMapId(UUIDUtils.getId());
							viewRoleMapModel.setRoleNum(1);
							viewRoleMapModel.setViewId(viewId);
							viewRoleMapModel.setViewRoleId(roleId);
							batchAddViewRoleMapList.add(viewRoleMapModel);
							
							viewRoleMapList.add(viewRoleMapModel);
						}	
					}
					
				}
			}
		}
	}
	
	/**
	 * 生成气氛信息，并返回气氛ID
	 * @param crewId	剧本ID	
	 * @param playDto	剧本DTO
	 * @return 气氛ID
	 * @throws Exception
	 */
	private String genAtmosphereInfo(String crewId, String atmosphereName)
			throws Exception {
		String atmosphereId = null;
		AtmosphereInfoModel atmosphere = this.atmosphereDao.queryByCrewIdAndAtmName(crewId, atmosphereName);
		if (atmosphere != null) {
			atmosphereId = atmosphere.getAtmosphereId();
		} else if (!StringUtils.isBlank(atmosphereName)) {
			atmosphere = new AtmosphereInfoModel();
			atmosphereId = UUIDUtils.getId();
			atmosphere.setAtmosphereId(atmosphereId);
			atmosphere.setAtmosphereName(atmosphereName);
			atmosphere.setCrewId(crewId);
			this.atmosphereDao.add(atmosphere);
		}
		
		return atmosphereId;
	}
	
	/**
	 * 生成场景表对象
	 * @param playDto	剧本信息
	 * @param crewId	剧组id
	 * @param atmosphereId	气氛Id
	 * @return
	 */
	private ViewInfoModel genViewInfo (ScenarioViewDto playDto, boolean isManualSave, String crewId, String atmosphereId) {
		if (StringUtils.isBlank(atmosphereId)) {
			atmosphereId = null;
		}
		ViewInfoModel view = new ViewInfoModel();
		String viewInfoId = UUIDUtils.getId();
		Integer setNo = playDto.getSeriesNo();
		String viewNo = playDto.getViewNo().toUpperCase();
		String site = playDto.getSite();
		
		view.setViewId(viewInfoId);
		view.setSeriesNo(setNo);
		view.setViewNo(viewNo);
		view.setAtmosphereId(atmosphereId);
		view.setSite(site);
		BigDecimal bdf2 = new BigDecimal(playDto.getPageCount());
		view.setPageCount(bdf2.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
		view.setSpecialRemind(playDto.getSpecialRemind());
		view.setIsManualSave(isManualSave);
		
		view.setCreateWay(ViewCreateWay.BySceAnalyse.getValue());
		view.setCreateTime(new Date());
		view.setCrewId(crewId);
		view.setMainContent(playDto.getMainContent());
		view.setRemark(playDto.getRemark());
		
		return view;
	}
	
	/**
	 * 生成场景内容信息
	 * @param sceViewDto	剧本信息
	 * @param crewId	剧组Id
	 * @param viewId	场景ID
	 * @return
	 */
	private ViewContentModel genSceneContent(ScenarioViewDto sceViewDto, String crewId, String viewId) {
		ViewContentModel viewContent = new ViewContentModel();
		String viewContentId = UUIDUtils.getId();
		String title = sceViewDto.getTitle();
		String content = sceViewDto.getContent();
		
		viewContent.setContentId(viewContentId);
		viewContent.setViewId(viewId);
		viewContent.setTitle(title);
		viewContent.setContent(content);
		viewContent.setCrewId(crewId);
		if(StringUtils.isNotBlank(content)){
			viewContent.setFigureprint(MD5Util.MD5(content));
		}
		viewContent.setStatus(ViewContentStatus.AddNotpublished.getValue());
		
		return viewContent;
	}
	
	/**
	 * 生成场景地点信息
	 * @param address	地点
	 * @param addressType	地点类型
	 * @return
	 */
	private ViewLocationModel genSceneAddress (String address, int addressType, String crewId) {
		if (!StringUtils.isBlank(address)) {
			ViewLocationModel viewAddress = new ViewLocationModel();
			String addressId = UUIDUtils.getId();
			
			viewAddress.setLocationId(addressId);
			viewAddress.setLocationType(addressType);
			viewAddress.setLocation(address);
			viewAddress.setCrewId(crewId);
			
			return viewAddress;
		}
		return null;
	}
	
	/**
	 * 生成场景和场景地址的关联关系
	 * @param address	地址信息	
	 * @param viewId	场景ID
	 * @return
	 */
	private ViewLocationMapModel genSceneAddressMap(ViewLocationModel address, String viewId) {
		if (address != null) {
			ViewLocationMapModel viewAddressMap = new ViewLocationMapModel();
			String viewAddressMapId = UUIDUtils.getId();
			
			viewAddressMap.setMapId(viewAddressMapId);
			viewAddressMap.setViewId(viewId);
			viewAddressMap.setLocationId(address.getLocationId());
			
			return viewAddressMap;
		}
		return null;
	}
	
	/**
	 * 更新场景信息
	 * @param viewInfo
	 * @throws Exception
	 */
	public void updateViewInfo (ViewInfoModel viewInfo) throws Exception {
		this.viewInfoDao.updateWithNull(viewInfo, "viewId");
	}
	
	/**
	 * 根据剧本ID,集次，场次查询对应的场景信息
	 * @param crewId	剧本ID
	 * @param seriesNo	集次
	 * @param viewNo	场次
	 * @return
	 */
	public ViewInfoModel queryOneByCrewIdAndSeriaViewNo (String crewId, Integer seriesNo, String viewNo) {
		return this.viewInfoDao.queryOneByCrewIdAndSeViNo(crewId, seriesNo, viewNo);
	}
	
	/**
	 * 批量更新数据
	 * @param viewInfoList
	 * @throws Exception 
	 */
	public void updateManyViewInfo(List<ViewInfoModel> viewInfoList) throws Exception {
		this.viewInfoDao.updateManyViewInfo(viewInfoList);
	}
	
	
	/**
	 * 保存场景信息
	 * @param content   剧本内容
	 * @param view   场景信息
	 * @param viewRoles  主要演员
	 * @param actor		特约演员
	 * @param pViewRoles 群众演员
	 * @param props		服化道
	 * @param majorView	主场景
	 * @param minorView	次场景
	 * @param thirdLevelView	三级场景
	 * @param propsSpecial 特殊道具
	 * @throws Exception 
	 */
	public void saveView(String content,ViewInfoModel view,String viewRoles,String actor,String pViewRoles,
			String props,String userId,String userName,String majorView,String minorView
			,String thirdLevelView,String propsSpecial) throws Exception{
		view.setViewId(UUIDUtils.getId());
		view.setCreateTime(new Date());
		//保存场景信息
		viewInfoDao.add(view);
		
		//保存场景内容信息
		ViewContentModel viewContent = new ViewContentModel();
		
		String [] contentArray=content.split("\r\n");
		
		Pattern p = Pattern.compile(Constants.REGEX_VIEW_MAIN_TITLE_WITHOUT_SPACE);
		
		String text="";
		for(String contentStr:contentArray){
			Matcher m = p.matcher(contentStr);
			if(m.find()){
				viewContent.setTitle(contentStr);
			}else{
				text=text+contentStr;
			}
		}
		viewContent.setContent(text);
		viewContent.setContentId(UUIDUtils.getId());
		viewContent.setCrewId(view.getCrewId());
		viewContent.setViewId(view.getViewId());
		viewContentDao.add(viewContent);
		
		//保存场景地址
		if(StringUtils.isNotBlank(majorView)){
			//主场景
			saveViewLocation(majorView, Constants.VIEW_ADDRESS_MAJOR, view.getCrewId(), view.getViewId());
		}
		if(StringUtils.isNotBlank(minorView)){
			//次场景
			saveViewLocation(minorView, Constants.VIEW_ADDRESS_MINOR, view.getCrewId(), view.getViewId());
		}
		if(StringUtils.isNotBlank(thirdLevelView)){
			//三级场景
			saveViewLocation(thirdLevelView, Constants.VIEW_ADDRESS_THIRD_LEVEL, view.getCrewId(), view.getViewId());
		}
		
		
		//保存角色
		//主要
		if(StringUtils.isNotBlank(viewRoles)){
			saveRoles(viewRoles, 1, view.getCrewId(), view.getViewId(), 1);
		}
		//特约
		if(StringUtils.isNotBlank(actor)){
			saveRoles(actor, 2, view.getCrewId(), view.getViewId(), 1);
		}
		//群众
		if(StringUtils.isNotBlank(pViewRoles)){
			saveRoles(pViewRoles, 3, view.getCrewId(), view.getViewId(), 1);
		}
		
		//保存服化道
		if(StringUtils.isNotBlank(props)){
			//saveProps(props, view.getCrewId(), view.getViewId(), userId, userName,Constants.VIEW_PROPS_TYPE_ORDINARY);
		}
		//保存特殊道具
		if(StringUtils.isNotBlank(propsSpecial)){
			//saveProps(propsSpecial, view.getCrewId(), view.getViewId(), userId, userName,Constants.VIEW_PROPS_TYPE_SPECIAL);
		}
		
	}
	
	/**
	 * 保存角色信息（多个角色的保存）
	 * @param roles 多个角色名称，用逗号隔开
	 * @param type
	 * @param crewId
	 * @param viewId
	 * @param roleNumber 演员数目
	 * @throws Exception
	 */
	public void saveRoles(String roles,Integer type,String crewId,String viewId, int roleNumber) throws Exception{
		
		String []roleArray = roles.split(",");
		
		for(String roleName:roleArray){
			this.saveRole(type, crewId, viewId, roleName, roleNumber);
		}
	}

	/**
	 * 保存角色信息（单个角色的保存）
	 * @param type	角色类型
	 * @param crewId	剧组ID
	 * @param viewId	场景ID
	 * @param roleName	角色名称
	 * @param roleNumber 演员数目
	 * @throws Exception
	 */
	private void saveRole(Integer type, String crewId, String viewId, String roleName, int roleNumber) throws Exception {
		if (!StringUtils.isBlank(roleName)) {
			Map<String, Object> viewConditionMap = new HashMap<String, Object>();
			viewConditionMap.put("crewId", crewId);
			viewConditionMap.put("viewRoleName", roleName);
			List<ViewRoleModel> viewRoleList = this.viewRoleDao.queryManyByMutiCondition(viewConditionMap, null);
			
			String viewRoleId = "";
			if (viewRoleList != null && viewRoleList.size() > 0) {
				ViewRoleModel viewRoleInfo = viewRoleList.get(0);
				viewRoleId = viewRoleInfo.getViewRoleId();
				
				viewRoleInfo.setViewRoleType(type);
				
				this.viewRoleDao.update(viewRoleInfo);
			} else {
				ViewRoleModel viewRole = new ViewRoleModel();
				
				viewRoleId = UUIDUtils.getId();
				viewRole.setViewRoleId(viewRoleId);
				viewRole.setCrewId(crewId);
				viewRole.setViewRoleName(roleName);
				viewRole.setViewRoleType(type);
				this.viewRoleDao.add(viewRole);
			}
			
			Map<String, Object> mapConditionMap = new HashMap<String, Object>();
			mapConditionMap.put("viewId", viewId);
			mapConditionMap.put("viewRoleId", viewRoleId);
			mapConditionMap.put("crewId", crewId);
			
			List<ViewRoleMapModel> viewRoleMapList = this.viewRoleMapDao.queryManyByMutiCondition(mapConditionMap, null);
			if (viewRoleMapList == null || viewRoleMapList.size() == 0) {
				ViewRoleMapModel viewRoleMap=new ViewRoleMapModel();
				
				viewRoleMap.setMapId(UUIDUtils.getId());
				viewRoleMap.setCrewId(crewId);
				viewRoleMap.setViewId(viewId);
				viewRoleMap.setViewRoleId(viewRoleId);
				viewRoleMap.setRoleNum(roleNumber);
				this.viewRoleMapDao.add(viewRoleMap);
			} else {
				ViewRoleMapModel viewRoleMap = viewRoleMapList.get(0);
				viewRoleMap.setRoleNum(roleNumber);
				this.viewRoleMapDao.update(viewRoleMap);
			}
		}
	}
	
	/**
	 * 保存角色信息（单个角色的保存）
	 * 该方法不考虑演员类型
	 * @param type	角色类型
	 * @param crewId	剧组ID
	 * @param viewId	场景ID
	 * @param roleName	角色名称
	 * @param roleNumber 演员数目
	 * @throws Exception
	 */
	private void saveRoleWithoutType(Integer type, String crewId, String viewId,
			String roleName, int roleNumber) throws Exception {
		if (!StringUtils.isBlank(roleName)) {
			Map<String, Object> viewConditionMap = new HashMap<String, Object>();
			viewConditionMap.put("crewId", crewId);
			viewConditionMap.put("viewRoleName", roleName);
			List<ViewRoleModel> viewRoleList = this.viewRoleDao.queryManyByMutiCondition(viewConditionMap, null);
			
			String viewRoleId = "";
			if (viewRoleList != null && viewRoleList.size() > 0) {
				viewRoleId = viewRoleList.get(0).getViewRoleId();
			} else {
				ViewRoleModel viewRole = new ViewRoleModel();
				
				viewRoleId = UUIDUtils.getId();
				viewRole.setViewRoleId(viewRoleId);
				viewRole.setCrewId(crewId);
				viewRole.setViewRoleName(roleName);
				viewRole.setViewRoleType(type);
				this.viewRoleDao.add(viewRole);
			}
			
			Map<String, Object> mapConditionMap = new HashMap<String, Object>();
			mapConditionMap.put("viewId", viewId);
			mapConditionMap.put("viewRoleId", viewRoleId);
			mapConditionMap.put("crewId", crewId);
			
			List<ViewRoleMapModel> viewRoleMapList = this.viewRoleMapDao.queryManyByMutiCondition(mapConditionMap, null);
			if (viewRoleMapList == null || viewRoleMapList.size() == 0) {
				ViewRoleMapModel viewRoleMap=new ViewRoleMapModel();
				
				viewRoleMap.setMapId(UUIDUtils.getId());
				viewRoleMap.setCrewId(crewId);
				viewRoleMap.setViewId(viewId);
				viewRoleMap.setViewRoleId(viewRoleId);
				viewRoleMap.setRoleNum(roleNumber);
				this.viewRoleMapDao.add(viewRoleMap);
			} else {
				ViewRoleMapModel viewRoleMap = viewRoleMapList.get(0);
				viewRoleMap.setRoleNum(roleNumber);
				this.viewRoleMapDao.update(viewRoleMap);
			}
		}
	}
	
	/**
	 * 保存物品信息
	 * @param props
	 * @param crewId
	 * @param viewId
	 * @param userId
	 * @param userName
	 * @throws Exception
	 */
	public void saveGoodsInfo(String goodsNames,String crewId,String viewId,String userId,String userName,Integer propsType) throws Exception{
		if (!StringUtils.isBlank(goodsNames)) {
			String[]goodsNameArray = goodsNames.split(",");
			
			//需要保存的道具信息可能在该剧组下的道具信息中已经存在
			for(String nameStr:goodsNameArray){
				if (StringUtils.isBlank(nameStr)) {
					continue;
				}
				
				Map<String, Object> propConditionMap = new HashMap<String, Object>();
				propConditionMap.put("crewId", crewId);
				propConditionMap.put("goodsName", nameStr);
				List<GoodsInfoModel> propsInfoList = this.goodsInfoDao.queryGoodsByCondition(propConditionMap);
				
				String id = null;
				if (propsInfoList != null && propsInfoList.size() > 0) {
					id = (String) propsInfoList.get(0).getId();
				} else {
					GoodsInfoModel model = new GoodsInfoModel();
					id = UUIDUtils.getId();
					model.setId(id);;
					model.setCrewId(crewId);
					model.setUserId(userId);
					model.setUserName(userName);
					model.setGoodsName(nameStr);
					model.setGoodsType(propsType);
					model.setCreateTime(new Date());
					
					goodsInfoDao.add(model);
				}
				
				//保存关联关系
				this.goodsId(viewId, id, crewId);
			}
		}
	}
	
	/**
	 * 保存场景地址
	 * @param viewLocation  地址
	 * @param locationType	类型
	 * @param crewId		剧组id
	 * @throws Exception 
	 */
	public void saveViewLocation(String viewLocation,Integer locationType,String crewId,String viewId) throws Exception{
		if (!StringUtils.isBlank(viewLocation)) {
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("locationType", locationType);
			conditionMap.put("location", viewLocation);
			conditionMap.put("crewId", crewId);
			List<ViewLocationModel> viewLocationList = this.viewLocationDao.queryManyByMutiCondition(conditionMap, null);
			
			String viewLocationId = "";
			if (viewLocationList != null && viewLocationList.size() > 0) {
				viewLocationId = viewLocationList.get(0).getLocationId();
			} else {
				ViewLocationModel viewLocationModel = new ViewLocationModel();
				
				viewLocationId = UUIDUtils.getId();
				viewLocationModel.setLocation(viewLocation);
				viewLocationModel.setLocationId(viewLocationId);
				viewLocationModel.setCrewId(crewId);
				viewLocationModel.setLocationType(locationType);
				
				viewLocationDao.add(viewLocationModel);
			}
			
			this.saveViewLoationMap(viewId, viewLocationId, crewId);
		}
	}
	
	/**
	 * 根据剧本ID查找剧本下不重复的场景信息
	 * @param crewId
	 * @return
	 */
	public List<String> querySiteListByCrewId (String crewId) {
		return this.viewInfoDao.querySiteListByCrewId(crewId);
	}
	
	/**
	 * 根据剧本ID查找剧本下不重复的特殊提醒信息
	 * @param crewId
	 * @return
	 */
	public List<String> querySpecialRemindListByCrewId (String crewId) {
		return this.viewInfoDao.querySpecialRemindByCrewId(crewId);
	}
	
	/**
	 * 场景表查询
	 * @param crewId
	 * @param page
	 * @param sortField
	 * @param sort
	 * @return
	 */
	public List<Map<String, Object>> queryViewInfoList(String crewId,Page page,ViewFilter filter){
		
		//查询场景表
		List<Map<String, Object>> viewInfoList = viewInfoDao.queryViewList(crewId, page, filter);
		
		if(null == viewInfoList||viewInfoList.size()==0){
			return null;
		}
		
		String viewIds = "";
		for(Map<String, Object> map : viewInfoList){
			String viewId = (String)map.get("viewId");
			viewIds+="'"+viewId+"',";
			
			String address = (String) map.get("viewAddress");
			
			if(StringUtils.isBlank(address)){
				continue;
			}
		}
		viewIds=viewIds.substring(0,viewIds.length()-1);
		
		if(StringUtils.isBlank(viewIds)){
			return null;
		}
		
		/*
		 * 查询场景表中其他信息
		 */
		//List<Map<String, Object>> viewLocationList = this.viewLocationDao.queryViewLocationByViewIds(viewIds); //主场景、次场景、三级场景
		List<Map<String, Object>> viewRoleList = this.viewRoleDao.queryViewRoleListByViewId(viewIds, null);	//角色演员信息
		List<Map<String, Object>> propsList = this.goodsInfoDao.queryManyByViews(viewIds);//道具信息
		
		//演员角色按照场景ID分组
		Map<String, List<Map<String, Object>>> mainRoleGroup = new HashMap<String, List<Map<String, Object>>>();
		Map<String, String> mainRoleGroupStringMap = new HashMap<String, String>();
		Map<String, String> mainRoleShortNameGroupStringMap = new HashMap<String, String>();
		Map<String, String> guestRoleGroup = new HashMap<String, String>();
		Map<String, String> massRoleGroup = new HashMap<String, String>();
		for (Map<String, Object> viewRoleMap : viewRoleList) {
			String roleViewId = (String) viewRoleMap.get("viewId");
			int roleType = (Integer) viewRoleMap.get("viewRoleType");
			String roleName = (String) viewRoleMap.get("viewRoleName");
			int roleNum = (Integer) viewRoleMap.get("roleNum");
			String shortName = (String) viewRoleMap.get("shortName");
			
			//主要演员分组
			if (roleType == ViewRoleType.MajorActor.getValue()) {
				if (!mainRoleGroup.containsKey(roleViewId)) {
					List<Map<String, Object>> viewMainRoleList = new ArrayList<Map<String, Object>>();
					viewMainRoleList.add(viewRoleMap);
					mainRoleGroup.put(roleViewId, viewMainRoleList);
					
					mainRoleGroupStringMap.put(roleViewId, roleName);
					mainRoleShortNameGroupStringMap.put(roleViewId, shortName);
				} else {
					mainRoleGroup.get(roleViewId).add(viewRoleMap);
					mainRoleGroupStringMap.put(roleViewId, mainRoleGroupStringMap.get(roleViewId) + "," + roleName);
					mainRoleShortNameGroupStringMap.put(roleViewId, mainRoleShortNameGroupStringMap.get(roleViewId) + "," + shortName);
				}
			}
			
			//特约演员分组
			if (roleType == ViewRoleType.GuestActor.getValue()) {
				if (!guestRoleGroup.containsKey(roleViewId)) {
					guestRoleGroup.put(roleViewId, roleName);
				} else {
					guestRoleGroup.put(roleViewId, guestRoleGroup.get(roleViewId) + "," + roleName);
				}
			}
			
			//群众演员分组
			if (roleType == ViewRoleType.MassesActor.getValue()) {
				if (!massRoleGroup.containsKey(roleViewId)) {
					if (roleNum == 1) {
						massRoleGroup.put(roleViewId, roleName);
					}else {
						massRoleGroup.put(roleViewId, roleName +"(" + roleNum + ")");
					}
				} else {
					if (roleNum == 1) {
						massRoleGroup.put(roleViewId, massRoleGroup.get(roleViewId) + "," + roleName);
					}else {
						massRoleGroup.put(roleViewId, massRoleGroup.get(roleViewId) + "," + roleName + "(" + roleNum +")");
					}
				}
			}
		}
		
		//高级查询时主要演员查询条件
		List<String> filterList = new ArrayList<String>();
		
		if(null != filter&& StringUtils.isNotBlank(filter.getRoles())){
			
			String[] role = filter.getRoles().split(",");
			filterList.addAll(Arrays.asList(role));
		}
		
		/*
		 * 道具
		 */
		//道具按照场景ID分组
		Map<String, String> commonPropsGroup = new HashMap<String, String>();
		Map<String, String> specialPropsGroup = new HashMap<String, String>();
		for (Map<String, Object> propmap : propsList) {
			String propViewId = (String) propmap.get("viewId");
			String propName = (String) propmap.get("goodsName");
			int propType = (Integer) propmap.get("goodsType");
			
			//普通道具
			if (propType == GoodsType.CommonProps.getValue()) {
				if(!commonPropsGroup.containsKey(propViewId)) {
					commonPropsGroup.put(propViewId, propName);
				} else {
					commonPropsGroup.put(propViewId, commonPropsGroup.get(propViewId) + "," + propName);
				}
			}
			
			//特殊道具
			if (propType == GoodsType.SpecialProps.getValue()) {
				if(!specialPropsGroup.containsKey(propViewId)) {
					specialPropsGroup.put(propViewId, propName);
				} else {
					specialPropsGroup.put(propViewId, specialPropsGroup.get(propViewId) + "," + propName);
				}
			}
		}
		
		/*
		 * 循环场景表信息，设置场景表中的演员角色、主次三级场景、道具等信息
		 */
		for(int i=viewInfoList.size()-1;i>=0 ;i--){
			Map<String, Object> viewMap = viewInfoList.get(i);
			
			String viewId = (String)viewMap.get("viewId");
			
			//场景地点信息
			/*String firstLocation = "";
			String secondLocation = "";
			String thirdLocation = "";
			if (viewMap.get("majorView") != null) {
				firstLocation += viewMap.get("majorView");
			}
			if (viewMap.get("minorView") != null) {
				secondLocation += viewMap.get("minorView");
			}
			if (viewMap.get("thirdLevelView") != null) {
				thirdLocation += viewMap.get("thirdLevelView");
			}
			
			viewMap.put("majorView", firstLocation);
			viewMap.put("minorView", secondLocation);
			viewMap.put("thirdLevelView", thirdLocation);*/
			
			//演员角色信息
			//主要演员信息
			if(null != mainRoleGroup.get(viewId)){
				List<String> roleList = (ArrayList)mainRoleGroup.get(viewId);
				viewMap.put("roleList", roleList);
				
				viewMap.put("mainRoleList", mainRoleGroupStringMap.get(viewId));
				viewMap.put("mainRoleShortNames", mainRoleShortNameGroupStringMap.get(viewId));
			}else{
				viewMap.put("roleList", new ArrayList<String>());
				viewMap.put("mainRoleList", "");
				viewMap.put("mainRoleShortNames", "");
			}
			
			//特约演员信息
			if(null != guestRoleGroup.get(viewId)){
				viewMap.put("guestRoleList", guestRoleGroup.get(viewId));
			}else{
				viewMap.put("guestRoleList", "");
			}

			//群众演员信息
			if(null != massRoleGroup.get(viewId)){
				viewMap.put("massRoleList", massRoleGroup.get(viewId));
			}else{
				viewMap.put("massRoleList", "");
			}
			
			//道具信息
			//普通道具
			if(null != commonPropsGroup.get(viewId)){
				viewMap.put("propsList", commonPropsGroup.get(viewId));
			}else{
				viewMap.put("propsList", "");
			}
			
			//特殊道具
			if(null != specialPropsGroup.get(viewId)){
				viewMap.put("specialPropsList", specialPropsGroup.get(viewId));
			}else{
				viewMap.put("specialPropsList", "");
			}
		}
		
		return viewInfoList;
	}
	
	

	
	/**
	 * 查询所有场的主要演员去重
	 * @param crewId
	 * @return
	 */
	public List<ViewRoleAndActorModel> queryViewRoleSign(String crewId){
		//查询所有主要角色去重
		List<ViewRoleAndActorModel> roleSignList = viewRoleAndActorDao.queryViewRoleSign(crewId, ViewRoleType.MajorActor.getValue());
		
		return roleSignList;
	}
	
	
	/**
	 * 查询指定场的演员去重
	 * @param viewIds
	 * @return
	 */
	public List<ViewRoleAndActorModel> queryViewRoleSignByViewIds(String viewIds){
		List<ViewRoleAndActorModel> roleSignList = viewRoleAndActorDao.queryViewRoleListViewIdSign("'"+viewIds.replaceAll(",", "','")+"'", ViewRoleType.MajorActor.getValue());
		return roleSignList;
	}
	
	/**
	 * 查询指定场的特约演员去重
	 * @param viewIds
	 * @return
	 */
	public List<ViewRoleAndActorModel> queryViewGuestRoleSignByViewIds(String viewIds){
		List<ViewRoleAndActorModel> roleSignList = viewRoleAndActorDao.queryViewRoleListViewIdSign("'"+viewIds.replaceAll(",", "','")+"'", ViewRoleType.GuestActor.getValue());
		return roleSignList;
	}
	
	/**
	 * 查询指定场的群众演员去重
	 * @param viewIds
	 * @return
	 */
	public List<ViewRoleAndActorModel> queryViewMassRoleSignByViewIds(String viewIds){
		List<ViewRoleAndActorModel> roleSignList = viewRoleAndActorDao.queryViewRoleListViewIdSign("'"+viewIds.replaceAll(",", "','")+"'", ViewRoleType.MassesActor.getValue());
		return roleSignList;
	}
	
	/**
	 * 查询场次统计数据
	 * @return
	 */
	public Map<String, Object> queryViewStatistics(String crewId,ViewFilter filter){
		Map<String, Object> map = new HashMap<String, Object>();
		//统计总场数
		List<Map<String, Object>> viewCountList = viewInfoDao.queryViewListStatistics(crewId, filter, "", "viewId", "count");
		
		Map<String, Object> viewCountMap = (Map<String, Object>)viewCountList.get(0);
		if(null == viewCountMap.get("funResult")){
			viewCountMap.put("funResult",0);
		}
		map.put("statisticsViewCount", viewCountList);
		
		//统计剧本总页数
		List<Map<String, Object>> pageCountList = viewInfoDao.queryViewListStatistics(crewId, filter, "", "pageCount", "sum");
		Map<String, Object> pageMap = (Map<String, Object>)pageCountList.get(0);
		if(null == pageMap.get("funResult")){
			pageMap.put("funResult",0);
		}
		map.put("statisticsPageCount", pageCountList);
		
		//统计拍摄状态
		List<Map<String, Object>> shootStatusList = viewInfoDao.queryViewListStatistics(crewId, filter, "shootStatus", "viewId", "count");
		map.put("statisticsShootStatus",shootStatusList);
		
		//统计内外景
		List<Map<String, Object>> siteList = viewInfoDao.queryViewListStatistics(crewId, filter, "site", "viewId", "count");
		map.put("statisticsSite", siteList);
		
		//统计文武戏
		List<Map<String, Object>> viewTypeList = viewInfoDao.queryViewListStatistics(crewId, filter, "viewType", "viewId", "count");
		map.put("statisticsType",viewTypeList);
		
		return map;
	}
	
	/**
	 * 查询拍摄地
	 * @param crewId
	 * @return
	 */
	public List queryShootAddressByCrewId(String crewId){
		
		List<SceneViewInfoModel> list = sceneViewInfoDao.queryShootAddressByCrewId(crewId);
		
		if(null == list){
			return new ArrayList();
		}
		
		List resList=new ArrayList();
		
		for(SceneViewInfoModel shootAddress:list){
			String shootlocation = shootAddress.getVName();
			resList.add(shootlocation);
		}
		
		return resList;
	}
	
	/**
	 * 查询地域
	 * @param crewId
	 * @return
	 */
	public List<String> queryShootRegionByCrewId(String crewId) {
		List<Map<String, Object>> list = sceneViewInfoDao.queryShootRegionByCrewId(crewId);
		if(list == null) {
			return new ArrayList<String>();
		}
		List<String> resList=new ArrayList<String>();
		
		for(Map<String, Object> one : list){
			String shootRegion = (String) one.get("shootRegion");
			resList.add(shootRegion);
		}		
		return resList;
	}
	
	/**
	 * 查询拍摄地是否存在
	 * @param crewId
	 * @return
	 * @throws Exception 
	 */
	public SceneViewInfoModel addOrGetShootLocationByLocationAndCrewId(String address, String region, String crewId) throws Exception{
		SceneViewInfoModel sceneViewInfoModel = null;
		if (!StringUtils.isBlank(address)) {
			sceneViewInfoModel = sceneViewInfoDao.queryShootAddressByAddress(address,crewId);
			
			if(null == sceneViewInfoModel){
				String id = UUIDUtils.getId();
				sceneViewInfoModel = new SceneViewInfoModel();
				sceneViewInfoModel.setId(id);
				sceneViewInfoModel.setvName(address);
				sceneViewInfoModel.setvCity(region);
				sceneViewInfoModel.setCrewId(crewId);
				sceneViewInfoDao.add(sceneViewInfoModel);
				
				//保存附件包信息
				AttachmentPacketModel attachmentPacketModel = new AttachmentPacketModel();
				attachmentPacketModel.setId(id);
				attachmentPacketModel.setCrewId(crewId);
				attachmentPacketModel.setCreateTime(new Date());
				this.attachmentPacketDao.add(attachmentPacketModel);
			} else {
				sceneViewInfoModel.setvCity(region);
				sceneViewInfoDao.updateWithNull(sceneViewInfoModel, "id");
			}
		}
		return sceneViewInfoModel;
	}
	
	/**
	 * 查询剧本所需道具
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryViewPropsByCrewId(String crewId, int propType){
		List<Map<String, Object>> propsList = this.goodsInfoDao.queryGoodsAndViewByCrewId(crewId, propType);
		return propsList;
	}
	
	public void reportScene(){
		
	}
	
	
	
	/**
	 * 生成excel
	 * @param srcFilePath
	 * @param data
	 * @param destFilePath
	 * @throws ParsePropertyException
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public void exportViewToExcelTemplate(String srcFilePath, Map<String, Object> data, String destFilePath) throws ParsePropertyException, InvalidFormatException, IOException{
		
		XLSTransformer transformer = new XLSTransformer();
		transformer.transformXLS(srcFilePath, data, destFilePath);
		
	}
	
	/**
	 * 通告单下的场景表查询
	 * @param crewId
	 * @param page
	 * @param noticeId
	 * @return
	 */
	public List<Map<String, Object>> queryNoticeViewList(String crewId,Page page,String noticeId,ViewFilter filter){
		
		//查询场景表
		List<Map<String, Object>> viewList = null;
		
		//通告单下的场景状态
		Map<String,String> noticeShootStatusMap = new HashMap<String,String>();
		
		if(null == page){ //不分页查询数据
			//已加入通告单的场次
			String viewIds = "";
			if (filter != null) {
				viewIds = filter.getViewIds();
			}
			viewList =  viewInfoDao.queryNoticeViewList(crewId, noticeId, viewIds);
			noticeShootStatusMap.put("0", "甩戏");
			noticeShootStatusMap.put("1", "部分完成");
			noticeShootStatusMap.put("2", "完成");
			noticeShootStatusMap.put("3", "删戏");
			noticeShootStatusMap.put("4", "加戏部分完成");
			noticeShootStatusMap.put("5", "加戏已完成");
		}else{ //分页查询
			//只查询所有未被添加入通告单的场次
			/*if (!filter.isFromAdvance()) { //是否来自高级查询
				return new ArrayList<Map<String, Object>>();
			}*/
			filter.setNoticeId(noticeId);
			viewList = viewInfoDao.queryViewList(crewId, page,filter);
			noticeShootStatusMap.put("0", "未完成");
			noticeShootStatusMap.put("1", "部分完成");
			noticeShootStatusMap.put("2", "完成");
			noticeShootStatusMap.put("3", "删戏");
		}
		
		//通告单中所有的主演和特约演员的名单列表
		noticeViewRoleList = new ArrayList<String>();
		
		if(null == viewList||viewList.size()==0){
			return new ArrayList<Map<String, Object>>();
		}
		
		
		String viewIds = "";
		//遍历取出每个场景信息的拍摄状态和地址
		for(Map<String, Object> map : viewList){
			String viewId = (String)map.get("viewId");
			viewIds += "'"+viewId+"',";
			
			if(map.get("shootStatus")!=null){
				Integer shootStatus = (Integer)map.get("shootStatus");
				map.put("shootStatus", noticeShootStatusMap.get(shootStatus.intValue()+""));
			}
			
			String address =(String) map.get("viewAddress");
			if(StringUtils.isBlank(address)){
				continue;
			}
		}
		
		viewIds=viewIds.substring(0,viewIds.length()-1);
		if(StringUtils.isBlank(viewIds)){
			return null;
		}
		
		//查询所有主要角色
		List<Map<String, Object>> roleViewList = viewRoleDao.queryViewRoleListByViewId(viewIds, ViewRoleType.MajorActor.getValue());
		
		List<String> filterList = new ArrayList<String>();
		
		//角色按场分组
		Map<String, String> mainRoleNameGroupByViewId = new HashMap<String, String>();
		Map<String, String> mainRoleShortNameGroupByViewId = new HashMap<String, String>();
		//主演角色按场分组
		for(Map<String, Object> roleViewMap : roleViewList){
			
			//将主演的角色id添加到集合中
			noticeViewRoleList.add((String)roleViewMap.get("viewRoleId"));
			
			if(filterList.size()>0 && !filterList.contains(roleViewMap.get("viewRoleId"))){
				continue;
			}
			//如果当前按场分组的列表中没有当前场,则需要将该场的所有角色添加进去;如果有当前场,就讲角色名称拼接到原名臣字符穿上,在保存
			if(null == mainRoleNameGroupByViewId.get(roleViewMap.get("viewId"))){
				String mainRoleNames = (String) roleViewMap.get("viewRoleName");
				mainRoleNameGroupByViewId.put(roleViewMap.get("viewId")+"", mainRoleNames);
				
			}else{
				String mainRoleNames = (String)mainRoleNameGroupByViewId.get(roleViewMap.get("viewId"));
				mainRoleNames += "," + roleViewMap.get("viewRoleName");
				mainRoleNameGroupByViewId.put(roleViewMap.get("viewId")+"", mainRoleNames);
			}
			
			//如果当前按场分组的列表中没有当前场,则需要将该场的所有角色添加进去;如果有当前场,就讲角色名称拼接到原名臣字符穿上,在保存
			if (roleViewMap.get("shortName") != null) {
				if (null == mainRoleShortNameGroupByViewId.get(roleViewMap.get("viewId"))) {
					String roleShortNames = (String) roleViewMap.get("shortName");
					mainRoleShortNameGroupByViewId.put(roleViewMap.get("viewId")+"", roleShortNames);
					
				} else {
					String roleShortNames = (String)mainRoleShortNameGroupByViewId.get(roleViewMap.get("viewId"));
					roleShortNames += "," + roleViewMap.get("shortName");
					mainRoleShortNameGroupByViewId.put(roleViewMap.get("viewId")+"", roleShortNames);
				}
			}
		}
		//将按场分组的角色名称倒叙排列
		for(int i = viewList.size() - 1; i >= 0; i--){
			Map<String, Object> viewMap = viewList.get(i);
			String viewId = (String) viewMap.get("viewId");
			
			//主要演员名称
			if(null != mainRoleNameGroupByViewId.get(viewId)){
				String mainRoleNames = (String)mainRoleNameGroupByViewId.get(viewId);
				viewMap.put("roleList", mainRoleNames);
			}else{
				viewMap.put("roleList", "");
			}
			
			//主要演员简称
			if(null != mainRoleShortNameGroupByViewId.get(viewId)){
				String roleShortNames = (String)mainRoleShortNameGroupByViewId.get(viewId);
				viewMap.put("roleShortNames", roleShortNames);
			}else{
				viewMap.put("roleShortNames", "");
			}
		}
		
		//特约演员角色按场分组
		Map<String, Object> guestRoleGroup = new HashMap<String, Object>();
		//查询所有特约角色
		List<Map<String, Object>> guestRoleViewList = viewRoleDao.queryViewRoleListByViewId(viewIds, ViewRoleType.GuestActor.getValue());
		
		for(Map<String, Object> roleViewMap : guestRoleViewList){
			
			//将特约角色的id添加到集合中
			noticeViewRoleList.add((String)roleViewMap.get("viewRoleId"));
			
			if(null == guestRoleGroup.get(roleViewMap.get("viewId"))){
				String roleList = (String)roleViewMap.get("viewRoleName");
				guestRoleGroup.put(roleViewMap.get("viewId")+"", roleList);
			}else{
				String roleList = (String)guestRoleGroup.get(roleViewMap.get("viewId"));
				roleList+=","+(String)roleViewMap.get("viewRoleName");
				guestRoleGroup.put(roleViewMap.get("viewId")+"", roleList);
			}
		}
		
		for(Map<String, Object> viewMap : viewList){
			
			String viewId = (String)viewMap.get("viewId");
			if(null != guestRoleGroup.get(viewId)){
				viewMap.put("guestRoleList", guestRoleGroup.get(viewId));
			}else{
				viewMap.put("guestRoleList", "");
			}
		}
		
		//群众演员角色按场分组
		Map<String, Object> massRoleGroup = new HashMap<String, Object>();
		//查询所有群众角色
		List<Map<String, Object>> massRoleViewList = viewRoleDao.queryViewRoleListByViewId(viewIds, ViewRoleType.MassesActor.getValue());
		
		for(Map<String, Object> roleViewMap:massRoleViewList){
			
			if(null == massRoleGroup.get(roleViewMap.get("viewId"))){
				String roleList = (String)roleViewMap.get("viewRoleName");
				massRoleGroup.put(roleViewMap.get("viewId")+"", roleList);
			}else{
				String roleList = (String)massRoleGroup.get(roleViewMap.get("viewId"));
				roleList+=","+(String)roleViewMap.get("viewRoleName");
				massRoleGroup.put(roleViewMap.get("viewId")+"", roleList);
			}
		}
		
		for(Map<String, Object> viewMap:viewList){
			
			String viewId = (String)viewMap.get("viewId");
			if(null != massRoleGroup.get(viewId)){
				viewMap.put("massRoleList", massRoleGroup.get(viewId));
			}else{
				viewMap.put("massRoleList", "");
			}
		}
		
		//普通道具
		List<Map<String, Object>> propsList = this.queryViewPropsByCrewId(crewId, GoodsType.CommonProps.getValue());// propsDao.queryPropsByPlayIdAndType(crewId, Constants.VIEW_PROPS_TYPE_ORDINARY);
		if(null != propsList&& propsList.size()>0){
			//普通道具分组
			Map<String, Object> propsGroup = new HashMap<String, Object>();
			for(Map<String, Object> props:propsList){
				if(null == propsGroup.get(props.get("viewId"))){
					String propsStr = (String)props.get("goodsName");
					propsGroup.put(props.get("viewId")+"", propsStr);
				}else{
					String propsStr = (String)propsGroup.get(props.get("viewId"));
					propsStr+=","+(String)props.get("goodsName");
					propsGroup.put(props.get("viewId")+"", propsStr);
				}
			}
			
			for(Map<String, Object> viewMap : viewList){
				String viewId = (String)viewMap.get("viewId");
				if(null != propsGroup.get(viewId)){
					viewMap.put("propsList", propsGroup.get(viewId));
				}else{
					viewMap.put("propsList", "");
				}
			}
		}
		
		//特殊道具
		List<Map<String, Object>> specialPropsList = this.queryViewPropsByCrewId(crewId, GoodsType.SpecialProps.getValue());
		
		if(null != specialPropsList && specialPropsList.size()>0){
			Map<String, Object> specialPropsGroup = new HashMap<String, Object>();
			for(Map<String, Object> props:specialPropsList){
				if(null == specialPropsGroup.get(props.get("viewId"))){
					String propsStr = (String)props.get("goodsName");
					specialPropsGroup.put(props.get("viewId")+"", propsStr);
				}else{
					String propsStr = (String)specialPropsGroup.get(props.get("viewId"));
					propsStr+=","+(String)props.get("goodsName");
					specialPropsGroup.put(props.get("viewId")+"", propsStr);
				}
			}
			
			for(Map<String, Object> viewMap:viewList){
				
				String viewId = (String)viewMap.get("viewId");
				if(null != specialPropsGroup.get(viewId)){
					viewMap.put("specialPropsList", specialPropsGroup.get(viewId));
				}else{
					viewMap.put("specialPropsList", "");
				}
			}
		}
		
		return viewList;
	}
	
	/**
	 * 场景汇总场景表查询
	 * @param crewId
	 * @param shootLocationId 拍摄地ID
	 * @param locationId 主场景ID
	 * @param role 主要演员
	 * @param searchMode 查询模式
	 * @return
	 */
	public List<Map<String, Object>> queryViewListByMajorLocation(String crewId,String shootLocationId, String locationId, ViewFilter filter){
		
		//查询场景表
		String role = filter.getRoles();
		String searchMode = filter.getSearchMode();
		List<Map<String, Object>> viewList = viewInfoDao.queryViewListByMajorLocation(crewId, shootLocationId, locationId, role, searchMode);
		
		//场景表中的场景状态
		Map<String,String> noticeShootStatusMap = new HashMap<String,String>();
		noticeShootStatusMap.put("0", "未完成");
		noticeShootStatusMap.put("1", "部分完成");
		noticeShootStatusMap.put("2", "完成");
		noticeShootStatusMap.put("3", "删戏");
		
		if(null == viewList || viewList.size()==0){
			return new ArrayList<Map<String, Object>>();
		}
				
		String viewIds = "";
		//遍历取出每个场景信息的拍摄状态和地址
		for(Map<String, Object> map : viewList){
			String viewId = (String)map.get("viewId");
			viewIds += "'"+viewId+"',";
			
			if(map.get("shootStatus")!=null){
				Integer shootStatus = (Integer)map.get("shootStatus");
				map.put("shootStatus", noticeShootStatusMap.get(shootStatus.intValue()+""));
			}
			
			String address =(String) map.get("viewAddress");
			if(StringUtils.isBlank(address)){
				continue;
			}
		}
		
		viewIds=viewIds.substring(0, viewIds.length()-1);
		if(StringUtils.isBlank(viewIds)){
			return null;
		}
		
		//主场景、次场景、三级场景
		List<Map<String, Object>> viewLocationList = this.viewLocationDao.queryViewLocationByViewIds(viewIds);
		
		//根据场景信息,添加对应的场景数据
		for(Map<String, Object> viewInfo : viewList) {
			String planViewId = viewInfo.get("viewId") + "";
			String firstLocation = "";
			String secondLocation = "";
			String thirdLocation = "";
			for (Map<String, Object> viewLocationMap : viewLocationList) {
				String locationViewId = viewLocationMap.get("viewId") + "";
				int locationType = (Integer) viewLocationMap.get("locationType");
				String location = viewLocationMap.get("location") + "";
				if (planViewId.equals(locationViewId) && locationType == LocationType.lvlOneLocation.getValue()) {
					firstLocation += location;
				}
				if (planViewId.equals(locationViewId) && locationType == LocationType.lvlTwoLocation.getValue()) {
					secondLocation += location;
				}
				if (planViewId.equals(locationViewId) && locationType == LocationType.lvlThreeLocation.getValue()) {
					thirdLocation += location;
				}
			}
			
			viewInfo.put("majorView", firstLocation);
			viewInfo.put("minorView", secondLocation);
			viewInfo.put("thirdLevelView", thirdLocation);
		}
		
		//查询所有主要角色
		List<Map<String, Object>> roleViewList = viewRoleDao.queryViewRoleListByViewId(viewIds, ViewRoleType.MajorActor.getValue());
		
		List<String> filterList = new ArrayList<String>();
		
		//角色按场分组
		Map<String, String> mainRoleNameGroupByViewId = new HashMap<String, String>();
		Map<String, String> mainRoleShortNameGroupByViewId = new HashMap<String, String>();
		//主演角色按场分组
		for(Map<String, Object> roleViewMap : roleViewList){
						
			if(filterList.size()>0 && !filterList.contains(roleViewMap.get("viewRoleId"))){
				continue;
			}
			//如果当前按场分组的列表中没有当前场,则需要将该场的所有角色添加进去;如果有当前场,就讲角色名称拼接到原名臣字符穿上,在保存
			if(null == mainRoleNameGroupByViewId.get(roleViewMap.get("viewId"))){
				String mainRoleNames = (String) roleViewMap.get("viewRoleName");
				mainRoleNameGroupByViewId.put(roleViewMap.get("viewId")+"", mainRoleNames);
				
			}else{
				String mainRoleNames = (String)mainRoleNameGroupByViewId.get(roleViewMap.get("viewId"));
				mainRoleNames += "," + roleViewMap.get("viewRoleName");
				mainRoleNameGroupByViewId.put(roleViewMap.get("viewId")+"", mainRoleNames);
			}
			
			//如果当前按场分组的列表中没有当前场,则需要将该场的所有角色添加进去;如果有当前场,就讲角色名称拼接到原名臣字符穿上,在保存
			if (roleViewMap.get("shortName") != null) {
				if (null == mainRoleShortNameGroupByViewId.get(roleViewMap.get("viewId"))) {
					String roleShortNames = (String) roleViewMap.get("shortName");
					mainRoleShortNameGroupByViewId.put(roleViewMap.get("viewId")+"", roleShortNames);
					
				} else {
					String roleShortNames = (String)mainRoleShortNameGroupByViewId.get(roleViewMap.get("viewId"));
					roleShortNames += "," + roleViewMap.get("shortName");
					mainRoleShortNameGroupByViewId.put(roleViewMap.get("viewId")+"", roleShortNames);
				}
			}
		}
		//将按场分组的角色名称倒叙排列
		for(int i = viewList.size() - 1; i >= 0; i--){
			Map<String, Object> viewMap = viewList.get(i);
			String viewId = (String) viewMap.get("viewId");
			
			//主要演员名称
			if(null != mainRoleNameGroupByViewId.get(viewId)){
				String mainRoleNames = (String)mainRoleNameGroupByViewId.get(viewId);
				viewMap.put("roleList", mainRoleNames);
			}else{
				viewMap.put("roleList", "");
			}
			
			//主要演员简称
			if(null != mainRoleShortNameGroupByViewId.get(viewId)){
				String roleShortNames = (String)mainRoleShortNameGroupByViewId.get(viewId);
				viewMap.put("roleShortNames", roleShortNames);
			}else{
				viewMap.put("roleShortNames", "");
			}
		}
		
		//特约演员角色按场分组
		Map<String, Object> guestRoleGroup = new HashMap<String, Object>();
		//查询所有特约角色
		List<Map<String, Object>> guestRoleViewList = viewRoleDao.queryViewRoleListByViewId(viewIds, ViewRoleType.GuestActor.getValue());
		
		for(Map<String, Object> roleViewMap : guestRoleViewList){
						
			if(null == guestRoleGroup.get(roleViewMap.get("viewId"))){
				String roleList = (String)roleViewMap.get("viewRoleName");
				guestRoleGroup.put(roleViewMap.get("viewId")+"", roleList);
			}else{
				String roleList = (String)guestRoleGroup.get(roleViewMap.get("viewId"));
				roleList+=","+(String)roleViewMap.get("viewRoleName");
				guestRoleGroup.put(roleViewMap.get("viewId")+"", roleList);
			}
		}
		
		for(Map<String, Object> viewMap : viewList){
			
			String viewId = (String)viewMap.get("viewId");
			if(null != guestRoleGroup.get(viewId)){
				viewMap.put("guestRoleList", guestRoleGroup.get(viewId));
			}else{
				viewMap.put("guestRoleList", "");
			}
		}
		
		//群众演员角色按场分组
		Map<String, Object> massRoleGroup = new HashMap<String, Object>();
		//查询所有群众角色
		List<Map<String, Object>> massRoleViewList = viewRoleDao.queryViewRoleListByViewId(viewIds, ViewRoleType.MassesActor.getValue());
		
		for(Map<String, Object> roleViewMap:massRoleViewList){
			
			if(null == massRoleGroup.get(roleViewMap.get("viewId"))){
				String roleList = (String)roleViewMap.get("viewRoleName");
				massRoleGroup.put(roleViewMap.get("viewId")+"", roleList);
			}else{
				String roleList = (String)massRoleGroup.get(roleViewMap.get("viewId"));
				roleList+=","+(String)roleViewMap.get("viewRoleName");
				massRoleGroup.put(roleViewMap.get("viewId")+"", roleList);
			}
		}
		
		for(Map<String, Object> viewMap:viewList){
			
			String viewId = (String)viewMap.get("viewId");
			if(null != massRoleGroup.get(viewId)){
				viewMap.put("massRoleList", massRoleGroup.get(viewId));
			}else{
				viewMap.put("massRoleList", "");
			}
		}
		
		//普通道具
		List<Map<String, Object>> propsList = this.queryViewPropsByCrewId(crewId, GoodsType.CommonProps.getValue());// propsDao.queryPropsByPlayIdAndType(crewId, Constants.VIEW_PROPS_TYPE_ORDINARY);
		if(null != propsList&& propsList.size()>0){
			//普通道具分组
			Map<String, Object> propsGroup = new HashMap<String, Object>();
			for(Map<String, Object> props:propsList){
				if(null == propsGroup.get(props.get("viewId"))){
					String propsStr = (String)props.get("goodsName");
					propsGroup.put(props.get("viewId")+"", propsStr);
				}else{
					String propsStr = (String)propsGroup.get(props.get("viewId"));
					propsStr+=","+(String)props.get("goodsName");
					propsGroup.put(props.get("viewId")+"", propsStr);
				}
			}
			
			for(Map<String, Object> viewMap : viewList){
				String viewId = (String)viewMap.get("viewId");
				if(null != propsGroup.get(viewId)){
					viewMap.put("propsList", propsGroup.get(viewId));
				}else{
					viewMap.put("propsList", "");
				}
			}
		}
		
		//特殊道具
		List<Map<String, Object>> specialPropsList = this.queryViewPropsByCrewId(crewId, GoodsType.SpecialProps.getValue());
		
		if(null != specialPropsList && specialPropsList.size()>0){
			Map<String, Object> specialPropsGroup = new HashMap<String, Object>();
			for(Map<String, Object> props:specialPropsList){
				if(null == specialPropsGroup.get(props.get("viewId"))){
					String propsStr = (String)props.get("goodsName");
					specialPropsGroup.put(props.get("viewId")+"", propsStr);
				}else{
					String propsStr = (String)specialPropsGroup.get(props.get("viewId"));
					propsStr+=","+(String)props.get("goodsName");
					specialPropsGroup.put(props.get("viewId")+"", propsStr);
				}
			}
			
			for(Map<String, Object> viewMap:viewList){
				
				String viewId = (String)viewMap.get("viewId");
				if(null != specialPropsGroup.get(viewId)){
					viewMap.put("specialPropsList", specialPropsGroup.get(viewId));
				}else{
					viewMap.put("specialPropsList", "");
				}
			}
		}
		
		return viewList;
	}
	
	/**
	 * 查询演员请加信息
	 */
	public String queryActorLeaveInfo(String noticeId, List<String> leaveRoleList, Date noticeDate) {
		Date currnDate = null;
		
		NoticeInfoModel model = null;
		//根据noticeId查询出notice的发布时间
		if (StringUtils.isNotBlank(noticeId)) {
			model = noticeInfoDao.queryNoticeInfoModelById(noticeId);
		}
		
		if (model != null ) {
			currnDate = model.getNoticeDate();
		}else if (noticeDate != null) {
			currnDate = noticeDate;
		}else{
			currnDate = new Date();
		}
		
		if (leaveRoleList != null && leaveRoleList.size()>0) {
			noticeViewRoleList = leaveRoleList;
		}
		
		//判断当前通告单中是否有人请假
		StringBuffer noteInfo = new StringBuffer();
		
		if (noticeViewRoleList == null || noticeViewRoleList.size() == 0) {
			noticeViewRoleList = new ArrayList<String>();
		}
		
		//对场景角色去重
		List<String> viewRoleIdList = new ArrayList<String>();
		for (String viewRoleId : noticeViewRoleList) {
			if (!viewRoleIdList.contains(viewRoleId)) {
				viewRoleIdList.add(viewRoleId);
			}
		}
		
		for (String roleId : viewRoleIdList) {
			List<Map<String, Object>> leaveMap = viewInfoDao.queryActorLeaveRecordByViewRoleId(currnDate, roleId);
			for (Map<String, Object> actorLeavemap : leaveMap) {
				noteInfo.append(actorLeavemap.get("actorName")+ "(饰" + actorLeavemap.get("viewRoleName") + ") 于");
				noteInfo.append(actorLeavemap.get("leaveStartDate") + " / ");
				noteInfo.append(actorLeavemap.get("leaveEndDate") + "号请假。");
				noteInfo.append("\r\n");
			}
		}
		
		return noteInfo.toString();
	}
	
	/**
	 * 根据场景id查询场景中的角色id
	 * @param viewId
	 * @return
	 */
	public List<Map<String, Object>> queryViewRoleIds(String viewId){
		return this.viewInfoDao.queryRoleIdByViewId(viewId);
	}
	
	/**
	 * 通过拍摄组ID查找拍摄分组信息
	 * @param viewId
	 * @return
	 */
	public ViewInfoModel queryOneByViewId (String viewId) {
		return this.viewInfoDao.queryById(viewId);
	}
	
	
	/**
	 * 查询剧本中所有的场景地点
	 * @param crewId
	 * @return
	 */
	public List<ViewLocationModel> queryViewLocation(String crewId){
		
		List<ViewLocationModel> list = viewLocationDao.queryManyByCrewId(crewId);
		
		return list;
	}
	
	/**
	 * 统计剧本总场数
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryStatisticsTotalCount(String crewId) {
		return this.viewInfoDao.queryStatisticsTotalCount(crewId);
	}
	
	/**
	 * 统计剧本总页数
	 * @param crewId
	 * @return
	 */
	public List queryStatisticsPageCount(String crewId){
		return this.viewInfoDao.queryStatisticsPageCount(crewId);
	}
	
	/**
	 * 按照拍摄组统计剧组下已加入拍摄计划的总场数和总页数
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryStatTCountAndPCountByGroup(String crewId) {
		return this.viewInfoDao.queryStatTCountAndPCountByGroup(crewId);
	}
	
	/**
	 * 查询场景内容
	 * @param viewId
	 * @return
	 */
	public ViewContentModel queryViewContentModel(String viewId){
		return this.viewContentDao.queryByViewId(viewId);
	}
	
	
	/**
	 * 根据剧组ID查找剧组下的所有下拉框列表信息
	 * @param crewId
	 * @param includeNotExists 是否包含没有在剧组的所有场景中出现的数据
	 * @return
	 * @throws IllegalAccessException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 */
	public ViewFilterDto genFilterDtoByCrewId (String crewId, boolean includeNotExists) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		ViewFilterDto filterDtoList = new ViewFilterDto();
		
		Map<String, String> groupList = new LinkedHashMap<String, String>();	//分组信息
		Map<String, String> atmosphereList = new LinkedHashMap<String, String>();	//气氛信息
		Map<Integer, String> shootStatusList = new HashMap<Integer, String>();	//拍摄状态信息
		
		List<String> siteList = new ArrayList<String>();	//内外景信息
		Map<String, String> locationList = new LinkedHashMap<String, String>();	//场景地址信息
		Map<String, String> firstLocationList = new LinkedHashMap<String, String>();	//主场景地址
		Map<String, String> secondLocationList = new LinkedHashMap<String, String>();	//次场景地址
		Map<String, String> thirdLocationList = new LinkedHashMap<String, String>();	//三级场景地址
		
		Map<String, String> majorRoleList = new LinkedHashMap<String, String>();	//主要演员
		Map<String, String> guestRoleList = new LinkedHashMap<String, String>();	//特约演员
		Map<String, String> mssesRoleList = new LinkedHashMap<String, String>();	//群众演员
		Map<String, String> commonPropList = new LinkedHashMap<String, String>();	//普通道具信息
		Map<String, String> specialPropList = new LinkedHashMap<String, String>();	//特殊道具信息
		
		Map<String, String> clothesList = new LinkedHashMap<String, String>();	//服装信息
		Map<String, String> makeupList = new LinkedHashMap<String, String>();	//化妆信息
		Map<String, String> shootLocationList = new LinkedHashMap<String, String>();	//拍摄地点信息
		Map<String, String> advertInfoList = new LinkedHashMap<String, String>();
		List<String> specialRemindList = new ArrayList<String>(); //特殊提醒信息 
		List<String> shootRegionList = new ArrayList<String>(); //地域
		List<Map<String, Object>> shootLocationRegionList = new ArrayList<Map<String,Object>>();	//拍摄地点(地域)信息
		Set<String> shootLocationRegionSet = new HashSet<String>();
		
		siteList = this.querySiteListByCrewId(crewId);
		specialRemindList = this.querySpecialRemindListByCrewId(crewId);
		if(includeNotExists) {
			shootRegionList = this.sceneViewInfoDao.queryAllProCity();
		} else {
			shootRegionList = this.queryShootRegionByCrewId(crewId);
		}
		
		shootStatusList.put(0, "未完成");
		shootStatusList.put(1, "部分完成");
		shootStatusList.put(2, "完成");
		shootStatusList.put(3, "删戏");
		
		List<ShootGroupModel> groupModelList = shootGroupService.queryManyByCrewId(crewId);
		for (ShootGroupModel shootGroupModel : groupModelList) {
			String shootGroupName = shootGroupModel.getGroupName();
			if (!groupList.containsValue(shootGroupName)) {
				groupList.put(shootGroupModel.getGroupId(), shootGroupName);
			}
		}
		
		//气氛信息
		List<AtmosphereInfoModel> atmosphereInfoList = null;
		//atmosphereInfoList = this.atmosphereService.queryAllByCrewId(crewId);
		atmosphereInfoList = this.atmosphereService.queryExistByCrewId(crewId);
		Collections.sort(atmosphereInfoList, new Comparator<AtmosphereInfoModel>() {
			@Override
			public int compare(AtmosphereInfoModel o1, AtmosphereInfoModel o2) {
				if (o1.getAtmosphereName() == null) {
					o1.setAtmosphereName("");
				}
				CollationKey key1 = Collator.getInstance().getCollationKey(o1.getAtmosphereName().toString().toLowerCase());// 要想不区分大小写进行比较用o1.toString().toLowerCase()
        		if (o2.getAtmosphereName() == null) {
					o2.setAtmosphereName("");
				}
				CollationKey key2 = Collator.getInstance().getCollationKey(o2.getAtmosphereName().toString().toLowerCase());
        		return key1.compareTo(key2);
			}
		});
		
		for (AtmosphereInfoModel atmosphere : atmosphereInfoList) {
			String atmosphereName = atmosphere.getAtmosphereName();
			if (!atmosphereList.containsValue(atmosphereName)) {
				atmosphereList.put(atmosphere.getAtmosphereId(), atmosphereName);
			}
		}
		
		//场景地点信息
		List<ViewLocationModel> viewLocationList = this.viewLocationService.queryManyByCrewId(crewId);
		/*Collections.sort(viewLocationList, new Comparator<ViewLocationModel>() {
			@Override
			public int compare(ViewLocationModel o1, ViewLocationModel o2) {
				if (o1.getLocation() == null) {
					o1.setLocation("");
				}
				CollationKey key1 = Collator.getInstance().getCollationKey(o1.getLocation().toString().toLowerCase());// 要想不区分大小写进行比较用o1.toString().toLowerCase()
        		if (o2.getLocation() == null) {
        			o2.setLocation("");
				}
				CollationKey key2 = Collator.getInstance().getCollationKey(o2.getLocation().toString().toLowerCase());
        		return key1.compareTo(key2);
			}
		});*/
		for (ViewLocationModel viewLocation : viewLocationList) {
			String location = viewLocation.getLocation();
			String locationId = viewLocation.getLocationId();
			if (!StringUtils.isBlank(location)) {
				if (!locationList.containsValue(location)) {
					locationList.put(locationId, location);
				}
				if (!firstLocationList.containsValue(location) && viewLocation.getLocationType() == LocationType.lvlOneLocation.getValue()) {
					firstLocationList.put(locationId, location);
					
				}
				if (!secondLocationList.containsValue(location) && viewLocation.getLocationType() == LocationType.lvlTwoLocation.getValue()) {
					secondLocationList.put(locationId, location);
				}
				if (!thirdLocationList.containsValue(location) && viewLocation.getLocationType() == LocationType.lvlThreeLocation.getValue()) {
					thirdLocationList.put(locationId, location);
				}
			}
		}
		
		//角色信息
		List<ViewRoleModel> roleModelList = new ArrayList<ViewRoleModel>();
		if (includeNotExists) {
			roleModelList = this.viewRoleService.queryByCrewId(crewId);
		} else {
			roleModelList = this.viewRoleService.queryManyOnlyExistsInCrewView(crewId);
		}
		
		for (ViewRoleModel viewRole : roleModelList) {
			String viewRoleName = viewRole.getViewRoleName();
			String viewRoleId = viewRole.getViewRoleId();
			if (!StringUtils.isBlank(viewRoleName) ) {
				if (!majorRoleList.containsValue(viewRoleName) 
						&& viewRole.getViewRoleType() == ViewRoleType.MajorActor.getValue()) {
					majorRoleList.put(viewRoleId, viewRoleName);
				}
				if (!guestRoleList.containsValue(viewRoleName) 
						&& viewRole.getViewRoleType() == ViewRoleType.GuestActor.getValue()) {
					guestRoleList.put(viewRoleId, viewRoleName);
				}
				if (!mssesRoleList.containsValue(viewRoleName) 
						&& viewRole.getViewRoleType() == ViewRoleType.MassesActor.getValue()) {
					mssesRoleList.put(viewRoleId, viewRoleName);
				}
			}
		}
		
		//道具信息
		List<GoodsInfoModel> propInfoList = this.goodsInfoDao.queryGoodsInfoByCrewId(crewId);
		Collections.sort(propInfoList, new Comparator<GoodsInfoModel>() {
			@Override
			public int compare(GoodsInfoModel o1, GoodsInfoModel o2) {
				CollationKey key1 = Collator.getInstance().getCollationKey(o1.getGoodsName().toString().toLowerCase());// 要想不区分大小写进行比较用o1.toString().toLowerCase()
        		CollationKey key2 = Collator.getInstance().getCollationKey(o2.getGoodsName().toString().toLowerCase());
        		return key1.compareTo(key2);
			}
		});
		
		for (GoodsInfoModel propInfo : propInfoList) {
			String propsName = propInfo.getGoodsName();
			if (!StringUtils.isBlank(propsName) && propInfo.getGoodsType().intValue() == GoodsType.CommonProps.getValue()) {
				if (!commonPropList.containsValue(propsName)) {
					commonPropList.put(propInfo.getId(), propsName);
				}
			}
			if (!StringUtils.isBlank(propsName) &&  propInfo.getGoodsType().intValue() == GoodsType.SpecialProps.getValue()) {
				if (!specialPropList.containsValue(propsName)) {
					specialPropList.put(propInfo.getId(), propsName);
				}
			}
		}
		
		//服装信息
		List<Map<String, Object>> clothesInfoList = this.goodsInfoDao.queryGoodsAndViewByCrewId(crewId, GoodsType.Clothes.getValue());
		Collections.sort(clothesInfoList, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				CollationKey key1 = Collator.getInstance().getCollationKey(o1.get("goodsName").toString().toLowerCase());// 要想不区分大小写进行比较用o1.toString().toLowerCase()
        		CollationKey key2 = Collator.getInstance().getCollationKey(o2.get("goodsName").toString().toLowerCase());
        		return key1.compareTo(key2);
			}
		});
		for (Map<String, Object> clothesInfo : clothesInfoList) {
			String clothesName = (String) clothesInfo.get("goodsName");
			if (!clothesList.containsValue(clothesName)) {
				clothesList.put((String)clothesInfo.get("id"), clothesName);
			}
		}
		
		//化妆信息
		List<Map<String, Object>> makeupInfoList = this.goodsInfoDao.queryGoodsAndViewByCrewId(crewId, GoodsType.Makeup.getValue());
		Collections.sort(makeupInfoList, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				CollationKey key1 = Collator.getInstance().getCollationKey(o1.get("goodsName").toString().toLowerCase());// 要想不区分大小写进行比较用o1.toString().toLowerCase()
        		CollationKey key2 = Collator.getInstance().getCollationKey(o2.get("goodsName").toString().toLowerCase());
        		return key1.compareTo(key2);
			}
		});
		for (Map<String, Object> makeUpInfo : makeupInfoList) {
			String makeupName = (String) makeUpInfo.get("goodsName");
			if (!makeupList.containsValue(makeupName)) {
				makeupList.put((String)makeUpInfo.get("id"), makeupName);
			}
		}
		
		//拍摄地点信息
		List<SceneViewInfoModel> shootLocationInfoList = new ArrayList<SceneViewInfoModel>();
		if (includeNotExists) {
			shootLocationInfoList = this.sceneViewInfoDao.queryShootAddressByCrewId(crewId);
		} else {
			shootLocationInfoList = this.sceneViewInfoDao.queryManyOnlyExistsInCrewView(crewId);
		}
		Collections.sort(shootLocationInfoList, new Comparator<SceneViewInfoModel>() {
			@Override
			public int compare(SceneViewInfoModel o1, SceneViewInfoModel o2) {
				CollationKey key1 = Collator.getInstance().getCollationKey(o1.getVName().toLowerCase());// 要想不区分大小写进行比较用o1.toString().toLowerCase()
        		CollationKey key2 = Collator.getInstance().getCollationKey(o2.getVName().toString().toLowerCase());
        		return key1.compareTo(key2);
			}
		});
		
		for (SceneViewInfoModel shootLocationInfo : shootLocationInfoList) {
			String shootLocationName = shootLocationInfo.getVName();
			String shootLocationId = shootLocationInfo.getId();
			String shootRegion = shootLocationInfo.getVCity();
			String shootLocationRegion = "";
			if(StringUtil.isNotBlank(shootLocationName)) {
				shootLocationRegion += shootLocationName;
				if(StringUtil.isNotBlank(shootRegion)) {
					shootLocationRegion += "(" + shootRegion + ")";
				}
			}
			if (!shootLocationList.containsValue(shootLocationName)) {
				shootLocationList.put(shootLocationId, shootLocationName);
			}
			if(!shootLocationRegionSet.contains(shootLocationRegion)) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("shootLocationId", shootLocationId);
				map.put("shootLocationName", shootLocationName);
				map.put("shootRegion", shootRegion);
				map.put("shootLocationRegion", shootLocationRegion);
				shootLocationRegionList.add(map);
				shootLocationRegionSet.add(shootLocationRegion);
			}
		}
		
		//广告信息
		List<InsideAdvertModel> insideAdvertList = this.insideAdvertService.queryAdvertInfoByCrewId(crewId);
		Collections.sort(insideAdvertList, new Comparator<InsideAdvertModel>() {
			@Override
			public int compare(InsideAdvertModel o1, InsideAdvertModel o2) {
				CollationKey key1 = Collator.getInstance().getCollationKey(o1.getAdvertName().toString().toLowerCase());// 要想不区分大小写进行比较用o1.toString().toLowerCase()
        		CollationKey key2 = Collator.getInstance().getCollationKey(o2.getAdvertName().toString().toLowerCase());
        		return key1.compareTo(key2);
			}
		});
		for (InsideAdvertModel insideAdvert : insideAdvertList) {
			String advertName = insideAdvert.getAdvertName();
			if (!advertInfoList.containsValue(advertName)) {
				advertInfoList.put(insideAdvert.getAdvertId(), insideAdvert.getAdvertName());
			}
		}
		
		filterDtoList.setGroupList(groupList);
		filterDtoList.setAtmosphereList(atmosphereList);
		filterDtoList.setSiteList(siteList);
		filterDtoList.setViewLocationList(locationList);
		filterDtoList.setFirstLocationList(firstLocationList);
		filterDtoList.setSecondLocationList(secondLocationList);
		filterDtoList.setThirdLocationList(thirdLocationList);
		filterDtoList.setMajorRoleList(majorRoleList);
		filterDtoList.setMassesRoleList(mssesRoleList);
		filterDtoList.setGuestRoleList(guestRoleList);
		filterDtoList.setCommonPropList(commonPropList);
		filterDtoList.setSpecialPropList(specialPropList);
		filterDtoList.setClotheList(clothesList);
		filterDtoList.setMakeupList(makeupList);
		filterDtoList.setShootStatusList(shootStatusList);
		filterDtoList.setShootLocationList(shootLocationList);
		filterDtoList.setAdvertInfoList(advertInfoList);
		filterDtoList.setSpecialRemindList(specialRemindList);
		filterDtoList.setShootRegionList(shootRegionList);
		filterDtoList.setShootLocationRegionList(shootLocationRegionList);
		
		return filterDtoList;
	}
	
	
	/**
	 * 根据剧组ID查找拍摄地点、主场景、主要演员下拉框列表信息
	 * @param crewId
	 * @param includeNotExists 是否包含没有在剧组的所有场景中出现的数据
	 * @return
	 * @throws IllegalAccessException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 */
	public ViewFilterDto getFilterDtoForLocStat(String crewId,
			boolean includeNotExists) throws SecurityException,
			IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		ViewFilterDto filterDtoList = new ViewFilterDto();

		Map<String, String> shootLocationList = new LinkedHashMap<String, String>();	//拍摄地点信息
		Map<String, String> firstLocationList = new LinkedHashMap<String, String>();	//主场景地址		
		Map<String, String> majorRoleList = new LinkedHashMap<String, String>();	//主要演员

		//拍摄地点信息
		List<SceneViewInfoModel> shootLocationInfoList = new ArrayList<SceneViewInfoModel>();
		if (includeNotExists) {
			shootLocationInfoList = this.sceneViewInfoDao.queryShootAddressByCrewId(crewId);
		} else {
			shootLocationInfoList = this.sceneViewInfoDao.queryManyOnlyExistsInCrewView(crewId);
		}
		Collections.sort(shootLocationInfoList, new Comparator<SceneViewInfoModel>() {
			@Override
			public int compare(SceneViewInfoModel o1, SceneViewInfoModel o2) {
				CollationKey key1 = Collator.getInstance().getCollationKey(o1.getVName().toLowerCase());// 要想不区分大小写进行比较用o1.toString().toLowerCase()
        		CollationKey key2 = Collator.getInstance().getCollationKey(o2.getVName().toLowerCase());
        		return key1.compareTo(key2);
			}
		});
		for (SceneViewInfoModel shootLocationInfo : shootLocationInfoList) {
			String shootLocationName = shootLocationInfo.getVName();
			String shootLocationId = shootLocationInfo.getId();
			if (!shootLocationList.containsValue(shootLocationName)) {
				shootLocationList.put(shootLocationId, shootLocationName);
			}
		}
		//场景地点信息
		List<ViewLocationModel> viewLocationList = this.viewLocationService.queryManyByCrewId(crewId);
//		Collections.sort(viewLocationList, new Comparator<ViewLocationModel>() {
//			@Override
//			public int compare(ViewLocationModel o1, ViewLocationModel o2) {
//				CollationKey key1 = Collator.getInstance().getCollationKey(o1.getLocation().toString().toLowerCase());// 要想不区分大小写进行比较用o1.toString().toLowerCase()
//        		CollationKey key2 = Collator.getInstance().getCollationKey(o2.getLocation().toString().toLowerCase());
//        		return key1.compareTo(key2);
//			}
//		});
		for (ViewLocationModel viewLocation : viewLocationList) {
			String location = viewLocation.getLocation();
			String locationId = viewLocation.getLocationId();
			if (!StringUtils.isBlank(location)) {
				if (!firstLocationList.containsValue(location) && viewLocation.getLocationType() == LocationType.lvlOneLocation.getValue()) {
					firstLocationList.put(locationId, location);					
				}
			}
		}
		//角色信息
		List<ViewRoleModel> roleModelList = new ArrayList<ViewRoleModel>();
		if (includeNotExists) {
			roleModelList = this.viewRoleService.queryByCrewId(crewId);
		} else {
			roleModelList = this.viewRoleService.queryManyOnlyExistsInCrewView(crewId);
		}
		
		for (ViewRoleModel viewRole : roleModelList) {
			String viewRoleName = viewRole.getViewRoleName();
			String viewRoleId = viewRole.getViewRoleId();
			if (!StringUtils.isBlank(viewRoleName) ) {
				if (!majorRoleList.containsValue(viewRoleName) 
						&& viewRole.getViewRoleType() == ViewRoleType.MajorActor.getValue()) {
					majorRoleList.put(viewRoleId, viewRoleName);
				}
			}
		}
		
		filterDtoList.setFirstLocationList(firstLocationList);
		filterDtoList.setMajorRoleList(majorRoleList);
		filterDtoList.setShootLocationList(shootLocationList);
		
		return filterDtoList;
	}
	
	/**
	 * 根据剧组ID查询集场次列表信息，查出的结果为一集下对应多长，例如：
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> querySeriesViewNoByCrewId (String crewId) {
		return this.viewInfoDao.querySeriesViewNoByCrewId(crewId);
	}

	/**
	 * 查询剧组下的所有集次
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> querySeriesNoByCrewId(String crewId) {
		return this.viewInfoDao.querySeriesNoByCrewId(crewId);
	}
	
	/**
	 * 查询含有指定角色的集次信息
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> querySeriesNoWithRoleInfo(String crewId, String roleIds) {
		return this.viewInfoDao.querySeriesNoWithRoleInfo(crewId, roleIds);
	}
	
	/**
	 * 查询剧组下的指定集下的所有场次
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryViewNoByCrewIdAndSeriesNo(String crewId, String seriesNo) {
		return this.viewInfoDao.queryViewNoByCrewIdAndSeriesNo(crewId, seriesNo);
	}
	
	/**
	 * 查询剧组指定集下的含有指定角色的所有场次
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryViewNoBySeriesNoAndRoleInfo(String crewId, String seriesNo, String roleIds) {
		return this.viewInfoDao.queryViewNoBySeriesNoAndRoleInfo(crewId, seriesNo, roleIds);
	}
	
	/**
	 * 设置拍摄地
	 * @param addressModel
	 * @param viewIds
	 */
	public void setViewShootLoation(String shootLocationId,String viewIds){
		
		String[] viewIdArray = viewIds.split(",");
		
		for(String viewId:viewIdArray){
			viewInfoDao.updateViewShootAddress(viewId, shootLocationId);
		}
	}
	
	/**
	 * 根据场景ID删除场景地点信息
	 * 包括场景地点信息和场景和场景地点的关联关系
	 * @param viewId
	 * @throws Exception 
	 */
	public void deleteLocationByViewId(String viewId) throws Exception {
		List<String> viewLocationIdList = new ArrayList<String>();	//场景地点的ID列表
		
		List<ViewLocationMapModel> mapList = this.viewLocationMapDao.queryManyByViewId(viewId);
		for (ViewLocationMapModel map : mapList) {
			viewLocationIdList.add(map.getLocationId());
		}
		
		//删除场景地点信息
		if (viewLocationIdList.size() > 0) {
			String[] strArray = new String[viewLocationIdList.size()];
			this.viewLocationDao.deleteMany(viewLocationIdList.toArray(strArray), "location", "tab_view_location");
		}
		
		//删除场景和地点的关联关系
		this.viewLocationMapDao.deleteManyByViewId(viewId);
	}
	
	/**
	 * 保存场景和场景地点之间的关联关系
	 * 该方法中加入了判断关联关系是否已经存在的业务逻辑
	 * @param viewId
	 * @param locationId
	 * @param crewId
	 * @return 关联关系的ID
	 * @throws Exception 
	 */
	public String saveViewLoationMap(String viewId, String locationId, String crewId) throws Exception {
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("viewId", viewId);
		conditionMap.put("locationId", locationId);
		conditionMap.put("crewId", crewId);
		
		List<ViewLocationMapModel> viewLocationMapList = this.viewLocationMapDao.queryManyByMutiCondition(conditionMap, null);
		
		String mapId = "";
		if (viewLocationMapList == null || viewLocationMapList.size() == 0) {
			ViewLocationMapModel viewLocationMap = new ViewLocationMapModel();
			
			mapId = UUIDUtils.getId();
			viewLocationMap.setLocationId(locationId);
			viewLocationMap.setMapId(mapId);
			viewLocationMap.setCrewId(crewId);
			viewLocationMap.setViewId(viewId);
			
			this.viewLocationMapDao.add(viewLocationMap);
		} else {
			mapId = viewLocationMapList.get(0).getMapId();
		}
		
		return mapId;
	}
	
	/**
	 * 保存场景物品关联关系，
	 * 此方法会对数据库中是否已存在关联关系做判断，只有不存在的情况下才会进行新增操作
	 * @param viewId
	 * @param propId
	 * @param crewId
	 * @return
	 * @throws Exception 
	 */
	public String goodsId(String viewId, String goodsId, String crewId) throws Exception {
		Map<String, Object> propMapConditionMap = new HashMap<String, Object>();
		propMapConditionMap.put("viewId", viewId);
		propMapConditionMap.put("goodsId", goodsId);
		propMapConditionMap.put("crewId", crewId);
		List<Map<String, Object>> viewPropsMapList = this.viewGoodsMapDao.queryGoodsMapListByCondition(propMapConditionMap);
		String mapId = "";
		if (viewPropsMapList == null || viewPropsMapList.size() == 0) {
			ViewGoodsInfoMap model = new ViewGoodsInfoMap();
			
			mapId = UUIDUtils.getId();
			model.setId(mapId);
			model.setCrewId(crewId);
			model.setGoodsId(goodsId);
			model.setViewId(viewId);
			this.viewGoodsMapDao.add(model);
		} else {
			mapId = (String) viewPropsMapList.get(0).get("id");
		}
		
		return mapId;
	}
	
	
	/**
	 * 根据场景ID删除单条场景信息
	 * 该方法会顺带删除该场和气氛、场景、演员、服装、化妆、道具、商值的关联关系
	 * @param crewId	剧组ID
	 * @param viewId	场景ID
	 * @throws Exception 
	 */
	public void deleteViewByViewId (String crewId, String viewIds) throws Exception {
		//校验该场是否加入通告单
		this.checkViewNotice(crewId, viewIds);
		
		//删除场景
		String[] viewIdsArr = viewIds.split(",");
		this.viewInfoDao.deleteMany(viewIdsArr, "viewId", ViewInfoModel.TABLE_NAME);
		
		//删除场景剧本
		this.viewContentDao.deleteByViewIds(crewId, viewIds);
		
		//删除该场和拍摄场景、演员、服装、化妆、道具、商值的关联关系
		this.viewLocationMapDao.deleteManyByViewIds(viewIds);
		this.viewRoleMapDao.deleteManyByViewIds(viewIds);
		this.goodsInfoDao.deleteManyByViewIds(viewIds);
		this.viewAdvertMapDao.deleteManyByViewIds(viewIds);
		
		this.downloadScenarioRecordService.deleteByCrewId(crewId);
		
		//删除计划与场景关联关系
		this.scheduleViewMapDao.deleteScheduleViewMap(crewId, viewIds);
	}
	
	/**
	 * 校验场景是否加入到了通告单
	 */
	public void checkViewNotice(String crewId, String viewIds) {
		//根据剧组的id和场景id查询出通告单信息列表
		List<Map<String, Object>> noticeViewList = this.noticeInfoDao.queryByViewIds(crewId, viewIds);
		
		//便利通告单列表,确认当前要删除的场景是否在通告单内,若在通告单信息中,则不能删除场景信息
		String message = "";
		if (noticeViewList != null && noticeViewList.size() > 0) {
			for (Map<String, Object> noticeViewMap : noticeViewList) {
				int seriesNo = (Integer) noticeViewMap.get("seriesNo");
				String viewNo = (String) noticeViewMap.get("viewNo");
				String noticeName = (String) noticeViewMap.get("noticeName");
				
				message += seriesNo + "-" + viewNo + "场在通告单《" + noticeName + "》中,";
			}
			message += "请先将其从通告单中移除，再尝试删除。";
			throw new IllegalArgumentException(message);
		}
	}
	
	/**
	 * 查询剧组下所有的场景信息
	 * 该查询会查询出场景对应的内容
	 * @return
	 */
	public List<Map<String, Object>> queryAllViewInfoWithContent(String crewId, Integer seriesNo) {
		return this.viewInfoDao.queryAllViewInfoWithContent(crewId, seriesNo);
	}
	
	
	/**
	 * 查询指定角色在指定通告单下拥有的戏的场次信息
	 * 该方法还会查出场次中所有的主要演员信息，以逗号隔开
	 * @param crewId
	 * @param noticeId
	 * @param roleIdList
	 * @return
	 */
	public List<Map<String, Object>> queryViewByNoticeRole(String crewId, String noticeId, List<String> roleIdList) {
		return this.viewInfoDao.queryViewByNoticeRole(crewId, noticeId, roleIdList);
	}
	
	/**
	 * 查询剧本中的场景信息
	 * @param crewId 剧本ID
	 * @param seriesNo 集次
	 * @return
	 */
	public List<Map<String, Object>> queryScenarioViewInfo(String crewId, Integer seriesNo, Page page, String viewId) {
		return this.viewInfoDao.queryScenarioViewInfo(crewId, seriesNo, page, viewId);
	}
	
	/**
	 * 查询剧本中的场景信息
	 * @param crewId
	 * @return
	 */
	public int countScenarioViewInfo(String crewId) {
		return this.viewInfoDao.countScenarioViewInfo(crewId);
	}
	
	/**
	 * 查询剧本中的场景信息
	 * 只查询气氛、拍摄地主要字段，没排序，排序将在代码中进行
	 * @param crewId
	 * @param seriesNo 集次
	 * @return
	 */
	public List<Map<String, Object>> queryViewList(String crewId, Integer seriesNo) {
		return this.viewInfoDao.queryViewList(crewId, seriesNo);
	}
	
	/**
	 * 根据场景ID查询场景信息
	 * 多个场景ID用英文逗号隔开
	 * @param crewId
	 * @param viewIds
	 * @return
	 */
	public List<Map<String, Object>> queryViewListByViewIds(String crewId, String viewIds) {
		return this.viewInfoDao.queryViewListByViewIds(crewId, viewIds);
	}
	
	/**
	 * 根据集场号列表查询剧组下的场景信息
	 * 该查询会查询出对应的临时销场信息
	 * @param crewId
	 * @param seriesViewNoList
	 * @return
	 */
	public List<Map<String, Object>> queryTmpCanBySeriesViewNoList(String crewId, List<String> seriesViewNoList) {
		return this.viewInfoDao.queryTmpCanBySeriesViewNoList(crewId, seriesViewNoList);
	}
	
	/**
	 * 根据通告单ID查询通告单下的场景信息
	 * 该查询会查询出通告单下场景的销场信息
	 * @param crewId
	 * @param viewIds
	 * @return
	 */
	public List<Map<String, Object>> queryNoticeViewList(String crewId, String noticeId) {
		return this.viewInfoDao.queryNoticeViewList(crewId, noticeId);
	}
	
	/**
	 * 根据集场号列表查询剧组下的场景信息
	 * @param crewId
	 * @param seriesViewNoList
	 * @return
	 */
	public List<ViewInfoModel> queryBySeriesViewNoList(String crewId, List<String> seriesViewNoList) {
		return this.viewInfoDao.queryBySeriesViewNoList(crewId, seriesViewNoList);
	}
	
	/**
	 * 查询剧组下待发布的场景信息
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryTopublishViewInfo(String crewId) {
		return this.viewInfoDao.queryTopublishViewInfo(crewId);
	}
	
	public Map<String, Object> getQueryContext(String crewId, boolean includeNotExists) throws Exception {
		
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Set<String>> initials = new HashMap<String,Set<String>>();
		
		QueryContextDto context = new QueryContextDto();
		
		//内外
		List<String> siteList = this.querySiteListByCrewId(crewId);
		
		List<Map<String, String>> sites = new ArrayList<Map<String, String>>();
		
		for(int i = 0; i< siteList.size(); i++){
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", i+1+"");
			map.put("name", siteList.get(i));
			
			sites.add(map);
		}
		
		context.setSites(sites);
		
		//文武
		String[] cultureTypeArr = {"文戏","武戏","文武戏"};
		
		List<Map<String, String>> cultureTypes = new ArrayList<Map<String, String>>();
		
		for(int i = 0; i< cultureTypeArr.length; i++){
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", i+1+"");
			map.put("name", cultureTypeArr[i]);
			
			cultureTypes.add(map);
		}
		
		context.setCultureTypes(cultureTypes);
		
		//季节
		String[] seasonArr = {"春","夏","秋","冬"};
		
		List<Map<String, String>> seasons = new ArrayList<Map<String, String>>();
		
		for(int i = 0; i< seasonArr.length; i++){
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", i+1+"");
			map.put("name", seasonArr[i]);
			
			seasons.add(map);
		}
		
		context.setSeasons(seasons);
		
		//拍摄状态
		String[] shootStatusArr = {"未完成","部分完成","完成","删戏"};
		
		List<Map<String, String>> shootStates = new ArrayList<Map<String, String>>();
		
		for(int i = 0; i< shootStatusArr.length; i++){
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", i+"");
			map.put("name", shootStatusArr[i]);
			
			shootStates.add(map);
		}
		
		context.setShootStates(shootStates);

		//气氛信息
		List<AtmosphereInfoModel> atmosphereInfoList = atmosphereService.queryExistByCrewId(crewId);
		
		List<Map<String, String>> atmospheres = new ArrayList<Map<String, String>>();
		
		for (AtmosphereInfoModel one : atmosphereInfoList) {
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", one.getAtmosphereId());
			map.put("name", one.getAtmosphereName());
			
			atmospheres.add(map);
		}
		
		context.setAtmospheres(atmospheres);
		
		//场景
		List<ViewLocationModel> viewLocationList = viewLocationService.queryManyByCrewId(crewId);
		
		List<Map<String, String>> primaryScenarios = new ArrayList<Map<String, String>>();
		List<Map<String, String>> secondaryScenarios = new ArrayList<Map<String, String>>();
		List<Map<String, String>> thirdScenarios = new ArrayList<Map<String, String>>();
		
		Set<String> psInitial = new LinkedHashSet<String>();
		Set<String> ssInitial = new LinkedHashSet<String>();
		Set<String> tsInitial = new LinkedHashSet<String>();
		
		for (ViewLocationModel one : viewLocationList) {
			
			if (!StringUtils.isBlank(one.getLocation())) {
				
				String initial = com.xiaotu.makeplays.utils.StringUtils.String2Alpha(one.getLocation());
				
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", one.getLocationId());
				map.put("name", one.getLocation());
				map.put("initial", initial);
		
				if (one.getLocationType() == LocationType.lvlOneLocation.getValue()) {
					
					primaryScenarios.add(map);
					psInitial.add(initial);
					
				}else if (one.getLocationType() == LocationType.lvlTwoLocation.getValue()) {
					
					secondaryScenarios.add(map);
					ssInitial.add(initial);
				} else if (one.getLocationType() == LocationType.lvlThreeLocation.getValue()) {
					thirdScenarios.add(map);
					tsInitial.add(initial);
				}
			}
		}
		
		context.setPrimaryScenarios(primaryScenarios);
		context.setSecondaryScenarios(secondaryScenarios);
		context.setThirdScenarios(thirdScenarios);
		initials.put("psInitial", psInitial);
		initials.put("ssInitial", ssInitial);
		initials.put("tsInitial", tsInitial);
		
		//角色信息
		List<ViewRoleModel> roleModelList = new ArrayList<ViewRoleModel>();
		
		if (includeNotExists) {
			roleModelList = this.viewRoleService.queryByCrewId(crewId);
		} else {
			roleModelList = this.viewRoleService.queryManyOnlyExistsInCrewView(crewId);
		}
		
		List<Map<String, String>> stars = new ArrayList<Map<String, String>>();
		List<Map<String, String>> guestActors = new ArrayList<Map<String, String>>();
		List<Map<String, String>> figurants = new ArrayList<Map<String, String>>();
		
		Set<String> starsInitial = new LinkedHashSet<String>();
		Set<String> guestActorsInitial = new LinkedHashSet<String>();
		Set<String> figurantsInitial = new LinkedHashSet<String>();
		
		for (ViewRoleModel one : roleModelList) {
			
			if (!StringUtils.isBlank(one.getViewRoleName()) ) {
				
				String initial = com.xiaotu.makeplays.utils.StringUtils.String2Alpha(one.getViewRoleName());
				
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", one.getViewRoleId());
				map.put("name", one.getViewRoleName());
				map.put("initial", initial);
				
				if (one.getViewRoleType() == ViewRoleType.MajorActor.getValue()) {
					
					stars.add(map);
					starsInitial.add(initial);
					
				}else if (one.getViewRoleType() == ViewRoleType.GuestActor.getValue()) {
					
					guestActors.add(map);
					guestActorsInitial.add(initial);
					
				}else if (one.getViewRoleType() == ViewRoleType.MassesActor.getValue()) {
					
					figurants.add(map);
					figurantsInitial.add(initial);
				}
			}
		}
		
		context.setStars(stars);
		context.setGuestActors(guestActors);
		context.setFigurants(figurants);
		
		initials.put("starsInitial", starsInitial);
		initials.put("guestActorsInitial", guestActorsInitial);
		initials.put("figurantsInitial", figurantsInitial);
		
		//道具信息
		List<GoodsInfoModel> propInfoList = this.goodsInfoDao.queryGoodsInfoByCrewId(crewId);
		
		List<Map<String, String>> props = new ArrayList<Map<String, String>>();
		List<Map<String, String>> specialProps = new ArrayList<Map<String, String>>();
		
		Set<String> propsInitial = new LinkedHashSet<String>();
		Set<String> specialPropsInitial = new LinkedHashSet<String>();
		
		for (GoodsInfoModel one : propInfoList) {
			
			if(!StringUtils.isBlank(one.getGoodsName())){
				
				String initial = com.xiaotu.makeplays.utils.StringUtils.String2Alpha(one.getGoodsName());
				
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", one.getId());
				map.put("name", one.getGoodsName());
				map.put("initial", initial);
				
				if (one.getGoodsType().intValue() == GoodsType.CommonProps.getValue()) {
					
					props.add(map);
					propsInitial.add(initial);
				
				} else if (one.getGoodsType().intValue() == GoodsType.SpecialProps.getValue()) {
					
					specialProps.add(map);
					specialPropsInitial.add(initial);
				}
			}
		}
		
		context.setProps(props);
		context.setSpecialProps(specialProps);
		
		initials.put("propsInitial", propsInitial);
		initials.put("specialPropsInitial", specialPropsInitial);
		
		
		//服装信息
		List<Map<String, Object>> clothesInfoList = this.goodsInfoDao.queryGoodsAndViewByCrewId(crewId, GoodsType.Clothes.getValue());
		
		List<Map<String, String>> clothings = new ArrayList<Map<String, String>>();
		
		Set<String> clothingsInitial = new LinkedHashSet<String>();
		
		for (Map<String, Object> one : clothesInfoList) {
			
			String initial = com.xiaotu.makeplays.utils.StringUtils.String2Alpha((String)one.get("goodsName"));
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", one.get("id").toString());
			map.put("name", (String)one.get("goodsName"));
			map.put("initial", initial);
			
			clothings.add(map);
			clothingsInitial.add(initial);
		}
		
		context.setClothings(clothings);
		initials.put("clothingsInitial", clothingsInitial);
		
		//化妆信息
		List<Map<String, Object>> makeupInfoList = this.goodsInfoDao.queryGoodsAndViewByCrewId(crewId, GoodsType.Makeup.getValue());
		
		List<Map<String, String>> makeups = new ArrayList<Map<String, String>>();
		
		Set<String> makeupsInitial = new LinkedHashSet<String>();
	
		for (Map<String, Object> one : makeupInfoList) {
			
			String initial = com.xiaotu.makeplays.utils.StringUtils.String2Alpha((String)one.get("goodsName"));
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", one.get("id").toString());
			map.put("name", (String)one.get("goodsName"));
			map.put("initial", initial);
			
			makeups.add(map);
			makeupsInitial.add(initial);
		}
		
		context.setMakeups(makeups);
		initials.put("makeupsInitial", makeupsInitial);
		
		//拍摄地点信息
		List<SceneViewInfoModel> shootLocationInfoList = new ArrayList<SceneViewInfoModel>();
		
		if (includeNotExists) {
			shootLocationInfoList = this.sceneViewInfoDao.queryShootAddressByCrewId(crewId);
		} else {
			shootLocationInfoList = this.sceneViewInfoDao.queryManyOnlyExistsInCrewView(crewId);
		}
		
		List<Map<String, String>> shootLocations = new ArrayList<Map<String, String>>();
		
		Set<String> shootLocationsInitial = new LinkedHashSet<String>();
	
		for (SceneViewInfoModel one : shootLocationInfoList) {
			String shootlocation = one.getVName();
			String shootLocationId = one.getId();
			String initial = com.xiaotu.makeplays.utils.StringUtils.String2Alpha(shootlocation);
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", shootLocationId);
			map.put("name", shootlocation);
			map.put("initial", initial);
			
			shootLocations.add(map);
			shootLocationsInitial.add(initial);
		}
		
		context.setShootLocations(shootLocations);
		initials.put("shootLocationsInitial", shootLocationsInitial);
		
		result.put("context", context);
		result.put("initials", initials);
		
		return result;
	}
	
	/**
	 * 场景信息批量修改
	 * @param batchUpdateViewDto
	 * @throws Exception 
	 */
	public void updateManyScenario(String crewId, UserInfoModel userInfo, String viewIds, BatchUpdateViewDto batchUpdateViewDto) throws Exception {
		/*String[] seriesViewNosArr = seriesViewNos.split(",");
		List<String> seriesViewNoList = Arrays.asList(seriesViewNosArr);
		
		List<ViewInfoModel> viewInfoList = this.queryBySeriesViewNoList(crewId, seriesViewNoList);
		String viewIds = "";
		for (int i = 0; i < viewInfoList.size(); i++) {
			String viewId = viewInfoList.get(i).getViewId();
			if (i == 0) {
				viewIds = viewId;
			} else {
				viewIds += "," + viewId;
			}
		}*/
		
		String[] seriesViewNosArr = viewIds.split(",");
		List<String> seriesViewNoList = Arrays.asList(seriesViewNosArr);
		//主要内容， 备注，场景类型（文武戏），季节，内外景,特殊提醒
		if (batchUpdateViewDto.isCgMainContent()) {
			this.viewInfoDao.updateManyBaseInfo(crewId, seriesViewNoList, "mainContent", batchUpdateViewDto.getMainContent());
		}
		if (batchUpdateViewDto.isCgRemark()) {
			this.viewInfoDao.updateManyBaseInfo(crewId, seriesViewNoList, "remark", batchUpdateViewDto.getRemark());
		}
		if (batchUpdateViewDto.isCgSite()) {
			this.viewInfoDao.updateManyBaseInfo(crewId, seriesViewNoList, "site", batchUpdateViewDto.getSite());
		}
		if (batchUpdateViewDto.isCgSpecialRemark()) {
			this.viewInfoDao.updateManyBaseInfo(crewId, seriesViewNoList, "specialRemind", batchUpdateViewDto.getSpecialRemark());
		}
		
		//设置保存状态为手动保存
		if (batchUpdateViewDto.isCgAtmosphereName() || batchUpdateViewDto.isCgSite() ||  batchUpdateViewDto.isCgShootStatus()) {
			this.viewInfoDao.updateManyBaseInfo(crewId, seriesViewNoList, "isManualSave", batchUpdateViewDto.getIsManualSave());
		}
		
		//气氛
		if (batchUpdateViewDto.isCgAtmosphereName()) {
			String atmosphereId = genAtmosphereInfo(crewId, batchUpdateViewDto.getAtmosphereName());
			if (StringUtils.isBlank(atmosphereId)) {
				atmosphereId = "";
			}
			this.viewInfoDao.updateManyBaseInfo(crewId, seriesViewNoList, "atmosphereId", atmosphereId);
		}
		
		//拍摄地
		if (batchUpdateViewDto.isCgShootLocation()) {
			SceneViewInfoModel shootLocationInfo = this.addOrGetShootLocationByLocationAndCrewId(batchUpdateViewDto.getShootLocation(), batchUpdateViewDto.getShootRegion(), crewId);
			String shootLocationId = "";
			if (shootLocationInfo != null) {
				shootLocationId = shootLocationInfo.getId();
			}
			this.viewInfoDao.updateManyBaseInfo(crewId, seriesViewNoList, "shootLocationId", shootLocationId);
		}
		
		//主场景、次场景、三级场景
		if (batchUpdateViewDto.isCgLvlOneLocation()) {
			/*this.viewLocationMapDao.deleteManyBySeriesViewNos(crewId, seriesViewNoList, LocationType.lvlOneLocation.getValue());*/
			for (String viewId : seriesViewNoList) {
				this.updateViewLocationType(crewId, viewId, batchUpdateViewDto.getLvlOneLocation());
			}
		}
		
		//场景状态
		if (batchUpdateViewDto.isCgShootStatus()) {
			this.viewInfoDao.updateManyBaseInfo(crewId, seriesViewNoList, "shootStatus", batchUpdateViewDto.getShootStatus());
			if (batchUpdateViewDto.getShootStatus() == 3) { 
				//将场景改为删戏状态时，同步通告单中的场景状态为删戏
				this.viewInfoDao.updateViewNoticeMapStatus(3, viewIds);
			}else if (batchUpdateViewDto.getShootStatus() == 0) {
				//将场景表中的删戏设置为未完成时，将通告单中的场景设置为甩戏
				this.viewInfoDao.updateViewNoticeMapStatus(0, viewIds);
			}
		}
		/*if (batchUpdateViewDto.isCgLvlTwoLocation()) {
			this.viewLocationMapDao.deleteManyBySeriesViewNos(crewId, seriesViewNoList, LocationType.lvlTwoLocation.getValue());
			for (ViewInfoModel viewInfo : viewInfoList) {
				this.saveViewLocation(batchUpdateViewDto.getLvlTwoLocation(), LocationType.lvlTwoLocation.getValue(), crewId, viewInfo.getViewId());
			}
		}
		if (batchUpdateViewDto.isCgLvlThreeLocation()) {
			this.viewLocationMapDao.deleteManyBySeriesViewNos(crewId, seriesViewNoList, LocationType.lvlThreeLocation.getValue());
			for (ViewInfoModel viewInfo : viewInfoList) {
				this.saveViewLocation(batchUpdateViewDto.getLvlThreeLocation(), LocationType.lvlThreeLocation.getValue(), crewId, viewInfo.getViewId());
			}
		}*/
		
		//场景角色信息，
		//删除该场景下所有和角色的关联关系，然后再新建（考虑到当用户删除一个角色信息时，删除这个操作无法更新到数据库）		
		//主要演员、特约演员、群众演员
		/*if (batchUpdateViewDto.isCgLeadingRoles()) {
			this.viewRoleMapDao.deleteManyBySeriesViewNos(crewId, seriesViewNoList, ViewRoleType.MajorActor.getValue());
			
			List<String> leadingRoleList = Arrays.asList(batchUpdateViewDto.getLeadingRoles());
			for (String roleName : leadingRoleList) {
				for (ViewInfoModel viewInfo : viewInfoList) {
					this.saveRoleWithoutType(ViewRoleType.MajorActor.getValue(), crewId, viewInfo.getViewId(), roleName, 1);
				}
			}
		}
		if (batchUpdateViewDto.isCgGuestRoles()) {
			this.viewRoleMapDao.deleteManyBySeriesViewNos(crewId, seriesViewNoList, ViewRoleType.GuestActor.getValue());
			
			List<String> guestRoleList = Arrays.asList(batchUpdateViewDto.getGuestRoles());
			for (String roleName : guestRoleList) {
				for (ViewInfoModel viewInfo : viewInfoList) {
					this.saveRoleWithoutType(ViewRoleType.GuestActor.getValue(), crewId, viewInfo.getViewId(), roleName, 1);
				}
			}
		}
		if (batchUpdateViewDto.isCgMassesRoles()) {
			this.viewRoleMapDao.deleteManyBySeriesViewNos(crewId, seriesViewNoList, ViewRoleType.MassesActor.getValue());
			
			List<String> massesRoleList = Arrays.asList(batchUpdateViewDto.getMassesRoles());
			for (String roleName : massesRoleList) {
				for (ViewInfoModel viewInfo : viewInfoList) {
					this.saveRoleWithoutType(ViewRoleType.MassesActor.getValue(), crewId, viewInfo.getViewId(), roleName, 1);
				}
			}
		}*/
		
		//服装、化妆、道具、特殊道具
		/*if (batchUpdateViewDto.isCgClothes()) {
			this.viewClothesMapDao.deleteManyByViewIds(viewIds);
			for (ViewInfoModel viewInfo : viewInfoList) {
				this.saveClothes(batchUpdateViewDto.getClothes(), crewId, viewInfo.getViewId(), userInfo.getUserId(), userInfo.getUserName());
			}
		}
		if (batchUpdateViewDto.isCgMakeups()) {
			this.viewMakeupMapDao.deleteManyByViewIds(viewIds);
			for (ViewInfoModel viewInfo : viewInfoList) {
				this.saveMakeupInfo(batchUpdateViewDto.getMakeups(), crewId, viewInfo.getViewId(), userInfo.getUserId(), userInfo.getUserName());
			}
		}
		if (batchUpdateViewDto.isCgCommonProps()) {
			this.viewPropsMapDao.deleteManyByViewIds(crewId, viewIds, PropsType.Normal.getValue());
			for (ViewInfoModel viewInfo : viewInfoList) {
				this.saveProps(batchUpdateViewDto.getCommonProps(), crewId, viewInfo.getViewId(), userInfo.getUserId(), userInfo.getUserName(), PropsType.Normal.getValue());
			}
		}
		if (batchUpdateViewDto.isCgSpecialProps()) {
			this.viewPropsMapDao.deleteManyByViewIds(crewId, viewIds, PropsType.Normal.getValue());
			for (ViewInfoModel viewInfo : viewInfoList) {
				this.saveProps(batchUpdateViewDto.getSpecialProps(), crewId, viewInfo.getViewId(), userInfo.getUserId(), userInfo.getUserName(), PropsType.Normal.getValue());
			}
		}*/
	}
	
	
	/**
	 * 查询指定角色的场景信息
	 * 
	 * 可扩展返回参数
	 * @param crewId
	 * @param viewRoleId
	 * @return	角色名称、演员名称、场景ID、集次、拍摄状态、页数、主场景ID、主场景名称、文武戏类别、拍摄地ID、拍摄地类型
	 */
	public List<Map<String, Object>> queryRoleViewList(String crewId, String viewRoleId) {
		return this.viewInfoDao.queryRoleViewList(crewId, viewRoleId);
	}
	
	/**
	 * 查询剧组中场景的统计信息
	 * @param crewId
	 * @return	总的需要拍摄的场景数（去除删戏的数量），已完成场数，未完成场数，已完成页数
	 */
	public Map<String, Object> queryViewCountStatistic(String crewId) {
		return this.viewInfoDao.queryViewTotalStatistic(crewId);
	}
	
	/**
	 * 根据场景角色ID查询场景信息
	 * @param viewRoleIds
	 * @return
	 */
	public List<ViewInfoModel> queryByViewRoleIds(String viewRoleIds) {
		return this.viewInfoDao.queryByViewRoleIds(viewRoleIds);
	}

	
	
	
	/**
	 * 根据剧组id获取主场景名称
	 * 
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryMainSceneName(String crewId,String mainSceneName){
		return this.viewInfoDao.queryMainSceneName(crewId,mainSceneName);
	}
	

	
	/**
	 * 分集汇总
	 * @param crewId
	 * @return 集数、场数	、页数、(夜外)场数、比重、页数、比重	(夜景)、场数、比重、页数、比重、(日景)场数、比重、页数、比重、其他
	 */
	public List<Map<String, Object>> querySeriesnoTotalInfo(String crewId) {
		List<Map<String, Object>> seriesnoList = this.viewInfoDao.querySeriesnoTotalInfo(crewId);
		DecimalFormat df1 = new DecimalFormat("0.0");
		if(seriesnoList != null && seriesnoList.size() > 0) {
			for(Map<String, Object> map : seriesnoList) {
				map.put("pageNum", df1.format(map.get("pageNum")));
				map.put("finishedPageNum", df1.format(map.get("finishedPageNum")));
				map.put("noViewPer", df.format(map.get("noViewPer")));
				map.put("nightoutPage", df1.format(map.get("nightoutPage")));
				map.put("noPagePer", df.format(map.get("noPagePer")));
				map.put("nViewPer", df.format(map.get("nViewPer")));
				map.put("nightPage", df1.format(map.get("nightPage")));
				map.put("nPagePer", df.format(map.get("nPagePer")));
				map.put("doViewPer", df.format(map.get("doViewPer")));
				map.put("dayoutPage", df1.format(map.get("dayoutPage")));
				map.put("doPagePer", df.format(map.get("doPagePer")));
				map.put("dViewPer", df.format(map.get("dViewPer")));
				map.put("dayPage", df1.format(map.get("dayPage")));
				map.put("dPagePer", df.format(map.get("dPagePer")));
			}
		}
		return seriesnoList;
	}
	
	/**
	 * 分集汇总--合计、平均
	 * @param crewId
	 * @return 集数、场数	、页数、(夜外)场数、比重、页数、比重	(夜景)、场数、比重、页数、比重、(日景)场数、比重、页数、比重、其他
	 */
	public Map<String, Object> queryTotalAverageInfo(String crewId) {
		Map<String, Object> totalaverage = this.viewInfoDao.queryTotalAverageInfo(crewId);
		DecimalFormat df1 = new DecimalFormat("0.0");
		if(totalaverage != null && !totalaverage.isEmpty()) {
			totalaverage.put("pageNum", df1.format(totalaverage.get("pageNum")));
			totalaverage.put("finishedPageNum", df1.format(totalaverage.get("finishedPageNum")));
			totalaverage.put("noViewPer", df.format(totalaverage.get("noViewPer")));
			totalaverage.put("nightoutPage", df1.format(totalaverage.get("nightoutPage")));
			totalaverage.put("noPagePer", df.format(totalaverage.get("noPagePer")));
			totalaverage.put("nViewPer", df.format(totalaverage.get("nViewPer")));
			totalaverage.put("nightPage", df1.format(totalaverage.get("nightPage")));
			totalaverage.put("nPagePer", df.format(totalaverage.get("nPagePer")));
			totalaverage.put("doViewPer", df.format(totalaverage.get("doViewPer")));
			totalaverage.put("dayoutPage", df1.format(totalaverage.get("dayoutPage")));
			totalaverage.put("doPagePer", df.format(totalaverage.get("doPagePer")));
			totalaverage.put("dViewPer", df.format(totalaverage.get("dViewPer")));
			totalaverage.put("dayPage", df1.format(totalaverage.get("dayPage")));
			totalaverage.put("dPagePer", df.format(totalaverage.get("dPagePer")));
			totalaverage.put("pageAvg", df1.format(totalaverage.get("pageAvg")));
			totalaverage.put("finishedPageAvg", df1.format(totalaverage.get("finishedPageAvg")));
			totalaverage.put("nightoutPageAvg", df1.format(totalaverage.get("nightoutPageAvg")));
			totalaverage.put("nightPageAvg", df1.format(totalaverage.get("nightPageAvg")));
			totalaverage.put("dayoutPageAvg", df1.format(totalaverage.get("dayoutPageAvg")));
			totalaverage.put("dayPageAvg", df1.format(totalaverage.get("dayPageAvg")));
		}
		return totalaverage;
	}
	
	/**
	 * 查询剧组中场景的总体信息，总集数、总场次、总页数
	 * @param crewId
	 * @return
	 */
	public Map<String, Object> queryViewTotalInfo(String crewId) {
		return this.viewInfoDao.queryViewTotalInfo(crewId);
	}
	
	/**
	 * 根据场景id查询出所有主场景，并提取出所有主场景相同的字符串
	 * @param viewIds
	 * @return
	 */
	@Deprecated
	public String queryMaxSameLocationStr(String viewIds) {
		List<String> subLocationStrList = new ArrayList<String>();
		String result = "";
		//根据场景id字符串查询出所有的主场景
		List<Map<String, Object>> mainLocationList = this.viewLocationDao.querySameLocationString(viewIds);
		if (mainLocationList == null) {
			result = "";
		}else if (mainLocationList.size() == 1) {
			Map<String, Object> singleLocation = mainLocationList.get(0);
			result = (String)singleLocation.get("location");
		}else if (mainLocationList.size() == 2) {
			Map<String, Object> firstMap = mainLocationList.get(0);
			String firstLocation = (String) firstMap.get("location");
			
			Map<String, Object> locationMap = mainLocationList.get(1);
			String locationString = (String) locationMap.get("location");
			//比较字符串
			String publicString = getPublicString(firstLocation, locationString);
			if (StringUtils.isNotBlank(publicString)) {
				subLocationStrList.add(publicString);
			}
		}else {
			//有多个主场景时，取出主场景中相同的内容
			int count = 0;
			int i = 0;
			String temp = "";
			while (true){
				
				if (i == 0) {
					Map<String, Object> firstMap = mainLocationList.get(i);
					temp = (String) firstMap.get("location");
					i++;
					continue;
				}
				Map<String, Object> locationMap = mainLocationList.get(i);
				String locationString = (String) locationMap.get("location");
				//比较字符串
				String publicString = getPublicString(temp, locationString);
				if (StringUtils.isNotBlank(publicString)) {
					subLocationStrList.add(publicString);
				}
				//当遍历到最后一个对象时，重置temp对象为列表的第二个对象，在重新进行比对
				if (i == mainLocationList.size()-1) {
					if (count == mainLocationList.size()-1) {
						count ++;
						break;
					}else {
						Map<String, Object> tempMap = mainLocationList.get(count+1);
						temp = (String) tempMap.get("location");
						mainLocationList.remove(count+1);
						count = 1;
						i= count;
						continue;
					}
				}
				
				i ++;
			}
			
			if (subLocationStrList.size() == 1) {
				result = subLocationStrList.get(0);
			}else if(subLocationStrList.size() > 1){
				String minLocation = "";
				//有多个比较结果时，取出最长的比较结果
				for (int a = 0; a < subLocationStrList.size(); a++) {
					if (a == 0) {
						minLocation = subLocationStrList.get(a);
						continue;
					}
					String compareStr = subLocationStrList.get(a);
					if (minLocation.length() < compareStr.length()) {
						minLocation = compareStr;
					}
				}
				
				result = minLocation;
			}
		}
		
		return result;
	}
	
	/**
	 * 根据场景id'查询出所有的主场景、次场景、三级场景
	 * @param viewId
	 * @return
	 */
	public List<Map<String, Object>> queryLocationByViewId(String viewId){
		return this.viewLocationDao.queryLocationByViewid(viewId);
	}
	
	
	/**
	 * 根据场景id字符串查询出合并后的场景列表
	 * @param viewIds
	 * @return
	 */
	public List<List<SameViewLocationDto>> querySameLocationList(String viewIds){
		List<List<SameViewLocationDto>> resultList = new ArrayList<List<SameViewLocationDto>>();
		List<SameViewLocationDto> modelList = new ArrayList<SameViewLocationDto>();
		//根据场景id查询出每个场景对应的场景地点信息
		String[] viewIdArr = viewIds.split(",");
		for (String viewId : viewIdArr) {
			SameViewLocationDto model = new SameViewLocationDto();
			List<Map<String, Object>> locationList = this.viewLocationDao.queryLocationByViewid(viewId);
			for (Map<String, Object> locationMap : locationList) {
				int locationType = (Integer) locationMap.get("locationType");
				String location = (String) locationMap.get("location");
				String locationId = (String) locationMap.get("locationId");
				model.setLocationId(locationId);

				if (locationType == 1) { //主场景
					model.setMainLocation(location);
				}else if (locationType == 2) { //次场景
					model.setSecondLocation(location);
				}else if (locationType == 3) { //三级场景
					model.setThirdLocation(location);
				}
			}
			model.setViewId(viewId);
			//设置默认为空串
			if (model.getMainLocation() == null) {
				model.setMainLocation("");
			}
			if (model.getSecondLocation() == null) {
				model.setSecondLocation("");
			}
			if (model.getThirdLocation() == null) {
				model.setThirdLocation("");
			}
			
			modelList.add(model);
		}
		
		//对场景结果进行排序
		 Collections.sort(modelList, new Comparator<SameViewLocationDto>() {

			@Override
			public int compare(SameViewLocationDto o1, SameViewLocationDto o2) {
				String o1MianLocation = o1.getMainLocation();
				String o1SecondLocation = o1.getSecondLocation();
				String o1ThirdLocation = o1.getThirdLocation();
				String o2MainLocation = o2.getMainLocation();
				String o2SecondLcoation = o2.getSecondLocation();
				String o2ThirdLocation = o2.getThirdLocation();
				
				//对三个场景分别进行排序
				if (o1MianLocation.equals(o2MainLocation)) {
					//主场景相同时，比较次场景
					if (o1SecondLocation.equals(o2SecondLcoation)) {
						//次场景相同，比较三级场景
						if (o1ThirdLocation.equals(o2ThirdLocation)) {
							//三级场景相同，表时这两个对象的三个场景都相同，返回相同
							return 0;
						}else {
							//三级场景不相同，比较三级场景
							return o1ThirdLocation.compareTo(o2ThirdLocation);
						}
					}else {
						//次场景不相同，对次场景排序
						return o1SecondLocation.compareTo(o2SecondLcoation);
					}
				}else {
					//主场景不相同，对主场景排序
					return o1MianLocation.compareTo(o2MainLocation);
				}
			}
			 
		});
		 
		 //对排序后的场景列表，进行比对，构造返回结果
		int i =0;
		SameViewLocationDto tempDto = null;
		String sameViewId = "";
		List<SameViewLocationDto> compareList = null;
		
		//进行比对
		while (i < modelList.size()) {
			if (i == 0) {
				compareList = new ArrayList<SameViewLocationDto>();
				
				tempDto = modelList.get(0);
				sameViewId = tempDto.getViewId();
				compareList.add(tempDto);
				if (modelList.size() == 1) {
					resultList.add(compareList);
				}
				
				i++;
				continue;
			}
			
			SameViewLocationDto modelDto = modelList.get(i);
			//比对主场景
			if (tempDto.getMainLocation().equals(modelDto.getMainLocation())) {
				//主场景相同，比对次场景
				if (tempDto.getSecondLocation().equals(modelDto.getSecondLocation())) {
					//次场景相同，比对三级场景
					if (tempDto.getThirdLocation().equals(modelDto.getThirdLocation())) {
						//三级场景相同时，移除当前对象，取出场景id
						sameViewId = sameViewId + "," + modelDto.getViewId();
						
						compareList.remove(tempDto);
						tempDto.setViewId(sameViewId);
						compareList.add(tempDto);
						
						modelList.remove(i);
						i--;
					}else {
						//三级场景不相同
						compareList.add(modelDto);
						modelList.remove(i);
						i--;
					}
				}else {
					//次场景不相同
					compareList.add(modelDto);
					modelList.remove(i);
					i--;
				}
			}
			
			if (i == modelList.size()-1) {
				//每比对完一轮，就讲结果保存，初始化i的值，开始下一轮
				i =0 ;
				//移除tempDto
				modelList.remove(0);
				
				//对结果进行去重
				for (int j = 0; j < compareList.size(); j++) {
					for(int k =  compareList.size()-1; k > j; k--) {
						SameViewLocationDto firstDto = compareList.get(j);
						SameViewLocationDto secondDto = compareList.get(k);
						if ((firstDto.getSecondLocation()+firstDto.getThirdLocation()).equals((secondDto.getSecondLocation()+secondDto.getThirdLocation()))) {
							String secondViewId = secondDto.getViewId();
							firstDto.setViewId(firstDto.getViewId()+ "," + secondViewId);
							compareList.remove(k);
						}
					}
				}
				
				//将结果添加到结果集中
				resultList.add(compareList);
			}else {
				i++;
			}
			
		}
		
		return resultList;
	}
	
	/**
	 * 更新场景的信息
	 * @param mainLocation 主场景
	 * @param secondLocation 次场景
	 * @param thirdLocation 三级场景
	 * @param viewIdArr 场景id的数组
	 * @throws Exception 
	 */
	public void savebatchViewLocation(String crewId, String mainLocation, String secondLocation, String thirdLocation, String viewIds) throws Exception {
		this.viewLocationMapDao.deleteManyByViewIds(viewIds);
		
		String[] viewIdArray = viewIds.split(",");
		for (String viewId : viewIdArray) {
			this.saveViewLocation(mainLocation, LocationType.lvlOneLocation.getValue(), crewId, viewId);
			this.saveViewLocation(secondLocation, LocationType.lvlTwoLocation.getValue(), crewId, viewId);
			this.saveViewLocation(thirdLocation, LocationType.lvlThreeLocation.getValue(), crewId, viewId);
		}
		
		List<String> seriesViewNoList = Arrays.asList(viewIds.split(","));
		//设置手动保存
		this.viewInfoDao.updateManyBaseInfo(crewId, seriesViewNoList, "isManualSave", true);
		
		
//		//遍历场景id的数组，取出每一场场景的对应信息，并根据场景的值，进行更新或添加操作
//		for (String viewId : viewIdArr) {
//			//根据id查询出场景的
//			List<Map<String, Object>> locationList = this.viewLocationDao.queryLocationByViewid(viewId);
//			//判断场景
//			if (locationList != null && locationList.size() >0) {
//				for (Map<String, Object> locationMap : locationList) {
//					String locationId = "";
//					if (locationMap != null) {
//						int locationType = (Integer) locationMap.get("locationType");
//						//如果原场景不为空时，把主场景或则次场景或则三级场景更新为空时，提示错误，不能更新
//						String location = (String) locationMap.get("location");
//						if (StringUtils.isNotBlank(location)) {
//							if (locationType == 1 && StringUtils.isBlank(mainLocation)) {
//								throw new IllegalArgumentException("不能将已存在的主场景置为空！");
//							}else if (locationType == 2 && StringUtils.isBlank(secondLocation)) {
//								throw new IllegalArgumentException("不能将已存在的次场景置为空！");
//							}else if (locationType == 3 && StringUtils.isBlank(thirdLocation)) {
//								throw new IllegalArgumentException("不能将已存在的三级场景置为空！");
//							}
//						}
//						if (locationType == 1 && StringUtils.isNotBlank(mainLocation)) {
//							//删除原有的主场景
//							locationId = (String) locationMap.get("locationId");
//						}else if (locationType == 2 && StringUtils.isNotBlank(secondLocation)) {
//							//删除次场景
//							locationId = (String) locationMap.get("locationId");
//						}else if (locationType == 3 && StringUtils.isNotBlank(thirdLocation)) {
//							//删除三级场景
//							locationId = (String) locationMap.get("locationId");
//						}
//						
//						if (StringUtils.isNotBlank(locationId)) {
//							//删除关联关系
//							this.viewLocationMapDao.deleteLocationInfoByLocationId(locationId);
//						}
//					}
//				}
//			}
//			
//				//添加场景及，场景与场景地点之间的关系
//				if (StringUtils.isNotBlank(mainLocation)) {
//					//主场景
//					addViewLocation( mainLocation, crewId, viewId, 1);
//				}
//				
//				if (StringUtils.isNotBlank(secondLocation)) {
//					//次场景
//					addViewLocation(secondLocation, crewId, viewId, 2);
//				}
//				
//				if (StringUtils.isNotBlank(thirdLocation)) {
//					//三级场景
//					addViewLocation(thirdLocation, crewId, viewId, 3);
//				}
//				
//			}
//			List<String> seriesViewNoList = Arrays.asList(viewIdArr);
//			//设置手动保存
//			this.viewInfoDao.updateManyBaseInfo(crewId, seriesViewNoList, "isManualSave", true);
		}
	
	/**
	 * 添加场景信息的公共方法
	 * @throws Exception 
	 */
	private void addViewLocation(String locationStr, String crewId, String viewId, int locationType) throws Exception {
			//当以前的主场景为空时，需要判断更新后的主场景里是否填写内容，如果填写，则需要在location表和location_map表中分别新建保存
			ViewLocationModel model = new ViewLocationModel();
			
			String locationId = UUIDUtils.getId();
			model.setLocationId(locationId);
			model.setCrewId(crewId);
			model.setLocation(locationStr);
			model.setLocationType(locationType);
			//添加场景地点信息
			this.viewLocationDao.add(model);
			
			//添加关联关系
			ViewLocationMapModel mapModel = new ViewLocationMapModel();
			String mainMapId = UUIDUtils.getId();
			mapModel.setMapId(mainMapId);
			mapModel.setCrewId(crewId);
			mapModel.setLocationId(locationId);
			mapModel.setViewId(viewId);
			this.viewLocationMapDao.add(mapModel);
	}
	
	/**
	 * 修改当前场景的主场景、次场景、三级场景
	 * @param viewId
	 * @throws Exception 
	 */
	private void updateViewLocationType(String crewId, String viewId, String mainLocation) throws Exception {
		//根据场景id查询出当前场景的主场景
		List<Map<String, Object>> mainLocationList = this.viewLocationDao.queryLocationByType(viewId, 1);
		//查询出次场景
		List<Map<String, Object>> secondLocationList = this.viewLocationDao.queryLocationByType(viewId, 2);
		//查询出三级场景
		List<Map<String, Object>> thirdLocationList = this.viewLocationDao.queryLocationByType(viewId, 3);
		
		/********* 先判断主场景是否为空，如主场景为空时将输入的场景设置为主场景保存同时保存关联关系；不为空时判断二级场景是否为空，为空时，将原有的一级场景设置为二级场景保存，并保存关联关系；不为空时，
		 * 		        先将原有的一级场景更新为二级场景的内容，再判断三级场景是否为空，若为空，景原有的二级场景设置为三级场景保存，并保存关联关系；若不为空，则将原有的二级场景和原有的三级场景内容进行合并，更新三级场景内容
		 */
		
		//主场景为空
		if (mainLocationList == null || mainLocationList.size() == 0) {
			ViewLocationModel model = new ViewLocationModel();
			
			String mainLocationId = UUIDUtils.getId();
			model.setLocationId(mainLocationId);
			model.setCrewId(crewId);
			model.setLocation(mainLocation);
			model.setLocationType(1);
			//添加场景地点信息
			this.viewLocationDao.add(model);
			
			//添加关联关系
			ViewLocationMapModel mapModel = new ViewLocationMapModel();
			String mainMapId = UUIDUtils.getId();
			mapModel.setMapId(mainMapId);
			mapModel.setCrewId(crewId);
			mapModel.setLocationId(mainLocationId);
			mapModel.setViewId(viewId);
			this.viewLocationMapDao.add(mapModel);
		}else {
			//取出主场景内容
			Map<String, Object> mainLocationMap = mainLocationList.get(0);
			//主场景不为空，判断二级场景是否为空
			if (secondLocationList == null || secondLocationList.size() == 0) {
				//保存二级场景
				ViewLocationModel model = new ViewLocationModel();
				
				String secondLocationId = UUIDUtils.getId();
				model.setLocationId(secondLocationId);
				model.setCrewId(crewId);
				model.setLocation((String)mainLocationMap.get("location"));
				model.setLocationType(2);
				//添加场景地点信息
				this.viewLocationDao.add(model);
				
				//添加关联关系
				ViewLocationMapModel mapModel = new ViewLocationMapModel();
				String secondMapId = UUIDUtils.getId();
				mapModel.setMapId(secondMapId);
				mapModel.setCrewId(crewId);
				mapModel.setLocationId(secondLocationId);
				mapModel.setViewId(viewId);
				this.viewLocationMapDao.add(mapModel);
			}else {
				//二级场景不为空，将二级场景内容更新为主场景的内容
				//取出二级场景
				Map<String, Object> secondLocationMap = secondLocationList.get(0);
				
				ViewLocationModel secondModel = new ViewLocationModel();
				secondModel.setLocationId((String)secondLocationMap.get("locationId"));
				secondModel.setLocation((String)mainLocationMap.get("location"));
				
				this.viewLocationDao.update(secondModel);
				
				//判断三级场景是否为空
				if (thirdLocationList == null || thirdLocationList.size() == 0) {
					//将二级场景设置为三级场景保存
					//保存二级场景
					ViewLocationModel model = new ViewLocationModel();
					
					String thirdLocationId = UUIDUtils.getId();
					model.setLocationId(thirdLocationId);
					model.setCrewId(crewId);
					model.setLocation((String)secondLocationMap.get("location"));
					model.setLocationType(3);
					//添加场景地点信息
					this.viewLocationDao.add(model);
					
					//添加关联关系
					ViewLocationMapModel mapModel = new ViewLocationMapModel();
					String thirdMapId = UUIDUtils.getId();
					mapModel.setMapId(thirdMapId);
					mapModel.setCrewId(crewId);
					mapModel.setLocationId(thirdLocationId);
					mapModel.setViewId(viewId);
					this.viewLocationMapDao.add(mapModel);
				}else {
					//取出三级场景
					Map<String, Object> thirdLocationMap = thirdLocationList.get(0);
					//三级场景不为空时，将三级场景的内容更新为二级场景和三级场景合并的内容
					String thirdContent = (String)secondLocationMap.get("location") + (String)thirdLocationMap.get("location");
					
					ViewLocationModel thirdModel = new ViewLocationModel();
					thirdModel.setLocationId((String)thirdLocationMap.get("locationId"));
					thirdModel.setLocation(thirdContent);
					
					this.viewLocationDao.update(thirdModel);
				}
			}
		}
	}
	
	/**
	 * 比较连个为字符串，并提取其公共部分
	 * @param str1
	 * @param str2
	 * @return
	 */
	private String getPublicString(String str1, String str2) {
		String max =null;
		String min = null;
		max=(str1.length()>str2.length()?str1:str2);
		min=max.equals(str1)?str2:str1;
		for (int i = 0; i < min.length(); i++){
			for(int start=0, end=min.length()-i;end != min.length()+1;start++,end++){
				String sub = min.substring(start,end);
				if(max.contains(sub)) {
					return sub; 
				}
			}
		}
		
		return null;
	}
	
	/**
	 * @Description  根据剧组id查询当前剧组有的气氛  map<气氛名称，气氛id>
	 * @param crewId  剧组id
	 * @return
	 */
	private Map<String, String> queryAtmByCrewId(String crewId){
		List<AtmosphereInfoModel> atmList = atmosphereDao.queryByCrewId(crewId);//当前剧组中的气氛信息
		Map<String, String> atmMap = new HashMap<String, String>();
		if(atmList!=null&&atmList.size()>0){
			for(AtmosphereInfoModel atmosphereInfoModel :atmList){
				atmMap.put(atmosphereInfoModel.getAtmosphereName(), atmosphereInfoModel.getAtmosphereId());
			}
		}
		return atmMap;
	}
	
	/**
	 * @Description  根据剧组id查询当前剧组的拍摄地  map<拍摄地名称，拍摄地id>
	 * @param crewId
	 * @return
	 */
	private Map<String, String> queryShootByCrewid(String crewId){
		List<SceneViewInfoModel> list = sceneViewInfoDao.queryShootAddressByCrewId(crewId);
		Map<String, String> shootMap = new HashMap<String, String>();
		if(list!=null&&list.size()>0){
			for(SceneViewInfoModel sceneViewInfoModel:list){
				shootMap.put(sceneViewInfoModel.getVName(), sceneViewInfoModel.getId());
			}
		}
		return shootMap;
	}
	
	/**
	 * 按照拍摄地分组，查询每个拍摄地下的场景统计信息
	 * @return	拍摄地Id，拍摄地名称，总场数，已拍摄场数，总页数，已拍摄页数
	 */
	public List<Map<String, Object>> queryViewStatisticGroupByShootLocation(String crewId) {
		return this.viewInfoDao.queryViewStatisticGroupByShootLocation(crewId);
	}
	
	/**
	 * 查询剧组中场景信息列表，用于统计总体进度
	 * 去掉删戏
	 * @param crewId
	 * @return
	 */
	public List<Map<String, Object>> queryTotalViewInfo(String crewId) {
		return this.viewInfoDao.queryTotalViewInfo(crewId);
	}
	
	/**
	 * 按照拍摄地分组，查询每个拍摄地下的场景统计信息,包括未设置拍摄地（设为待定）
	 * @return	拍摄地Id，拍摄地名称，总场数，已拍摄场数，总页数，已拍摄页数
	 */
	public List<Map<String, Object>> queryShootLocationProduction(String crewId) {
		return this.viewInfoDao.queryShootLocationProduction(crewId);
	}
	
	/**
	 * 查询场景角色的场景统计信息，包括主要演员、特约演员
	 * @return	场景角色Id，场景角色名称，总场数，已拍摄场数，总页数，已拍摄页数
	 */
	public List<Map<String, Object>> queryViewRoleProduction(String crewId) {
		return this.viewInfoDao.queryViewRoleProduction(crewId);
	}
	
	/**
	 * 同步场景表中的特殊提醒数据
	 * @throws Exception 
	 */
	public void ansycViewSpecialRemind() throws Exception {
		Map<Integer, String> cultureTypeList = new HashMap<Integer, String>();
		List<ViewInfoModel> updateList = new ArrayList<ViewInfoModel>();
		//定义季节文武戏的map
		cultureTypeList.put(1, "武戏");
		cultureTypeList.put(2, "特效");
		cultureTypeList.put(3, "武特");
		
		Map<Integer, String> seasonList = new HashMap<Integer, String>();
		seasonList.put(1, "春");
		seasonList.put(2, "夏");
		seasonList.put(3, "秋");
		seasonList.put(4, "冬");
		//查询出场景表中的原始数据
		List<ViewInfoModel> list = this.viewInfoDao.queryAllViewInfo();
		for (ViewInfoModel model : list) {
			//取出季节和文武戏信息
			Integer season = model.getSeason();
			Integer viewType = model.getViewType();
			//季节
			String seasonStr = "";
			if (season != null && season != 0 && season != -1) {
				seasonStr = seasonList.get(season);
			}
			//文武
			String viewTypeStr = "";
			if (viewType != null && viewType != 0 && viewType != -1) {
				viewTypeStr = cultureTypeList.get(viewType);
			}
			
			if (StringUtils.isBlank(seasonStr)) {
				model.setSpecialRemind(viewTypeStr);
			}else if (StringUtils.isBlank(viewTypeStr)) {
				model.setSpecialRemind(seasonStr);
			}else {
				model.setSpecialRemind(seasonStr + "," + viewTypeStr); 
			}
			updateList.add(model);
		}
		
		//批量更新shuju
		this.viewInfoDao.updateBatch(updateList, "viewId", ViewInfoModel.class);
	}
}