package com.xiaotu.makeplays.user.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.service.CrewInfoService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.sysrole.model.SysRoleDataModel;
import com.xiaotu.makeplays.sysrole.model.SysroleInfoModel;
import com.xiaotu.makeplays.sysrole.service.SysRoleInfoService;
import com.xiaotu.makeplays.user.controller.filter.ContactFilter;
import com.xiaotu.makeplays.user.model.CrewContactModel;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.user.service.CrewContactService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.ExcelUtils;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.StringUtil;

/**
 * 剧组联系表
 * @author Mr.zhao
 *
 */
@Controller
@RequestMapping("/contact")
public class CrewContactController extends BaseController {
	
	private static Map<String, String> CREW_CONTACT_MAP = new LinkedHashMap<String, String>();//需要导出的联系人字段
    static{
    	CREW_CONTACT_MAP.put("姓名", "contactName");
    	CREW_CONTACT_MAP.put("性别",  "sex");
    	CREW_CONTACT_MAP.put("手机号",  "phone");
    	CREW_CONTACT_MAP.put("部门/职务",  "sysRoleNames");
    	CREW_CONTACT_MAP.put("入组时间",  "enterDate");
    	CREW_CONTACT_MAP.put("离组时间",  "leaveDate");
    	CREW_CONTACT_MAP.put("证件类型",  "identityCardType");
    	CREW_CONTACT_MAP.put("证件号码",  "identityCardNumber");
    	CREW_CONTACT_MAP.put("餐别",  "mealType");
    	CREW_CONTACT_MAP.put("备注",  "remark");
    }
	
	private final static SimpleDateFormat yyyyMMddFormate = new SimpleDateFormat("yyyyMMddHHmmss");

	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	
	Logger logger = LoggerFactory.getLogger(CrewContactController.class);
	
	private final int terminal = Constants.TERMINAL_PC;
	
	@Autowired
	private CrewContactService crewContactService;
	
	@Autowired
	private SysRoleInfoService sysRoleInfoService;
	@Autowired
	private CrewInfoService crewInfoService;
	
	/**
	 * 进入剧组联系表
	 */
	@RequestMapping("/toContactList")
	public ModelAndView toContactList(HttpServletRequest request){
		ModelAndView view = new ModelAndView("user/contactList");
		
		return view;
	}
	/**
	 * 进入剧组联系表
	 */
	@RequestMapping("/toContactDetailPage")
	public ModelAndView toContactDetailPage(HttpServletRequest request,String contactId){
		ModelAndView view = new ModelAndView("user/crewContactListDetail");
		view.addObject("contactId", contactId);
		return view;
	}
	/**
	 * 跳转到剧组联系列表时,需要加载的数据
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryContactDepart")
	public Map<String, Object> queryContactDepart(HttpServletRequest request){
		String crewId = getCrewId(request);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			/**
			 * 获取部门职务
			 */
			List<SysRoleDataModel> dap = new ArrayList<SysRoleDataModel>();
			List<SysroleInfoModel> roleList = this.sysRoleInfoService.queryByCrewId(crewId);
			
			//获取部门
			for(SysroleInfoModel si:roleList){
				if(si.getParentId().equals("00")) { //表示当前是部门名称不是具体的职位
					SysRoleDataModel sr = new SysRoleDataModel();
					sr.setCanBeEvaluate(si.getCanBeEvaluate());
					sr.setCrewId(si.getCrewId());
					sr.setParentId(si.getParentId());
					sr.setRoleDesc(si.getRoleDesc());
					sr.setRoleId(si.getRoleId());
					sr.setRoleName(si.getRoleName());
					dap.add(sr);
				}
			}
			
			//获取职务
			for(SysroleInfoModel si : roleList){
				for(SysRoleDataModel sd : dap) {
					if(si.getParentId().equals(sd.getRoleId())) {
						sd.getChild().add(si);
					}
				}
			}
			resultMap.put("depart", dap);
			message = "查询成功!";
			
		} catch (Exception e) {
			message = "未知异常查询失败!";
			success = false;
			
			logger.error(message, e);
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 设置联系人是否公开到组
	 * @param request
	 * @param ifOpen
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/setIfOpen")
	public Map<String, Object> setIfOpen(HttpServletRequest request, String contactId, Boolean ifOpen) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			
			this.crewContactService.setIfOpen(contactId, ifOpen);
			
		} catch (Exception e) {
			success = false;
			message = "未知异常，保存联系人信息失败";
			logger.error("未知异常，保存联系人信息失败", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 获取剧组联系列表数据
	 * @param request
	 * @param contactFilterModel 用于封装高级查询条件的对象
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryCrewContactList")
	public List<Map<String, Object>> queryCrewContactList(HttpServletRequest request, ContactFilter contactFilter){
		List<Map<String, Object>> contactList= null;
		
		try {
			String crewId = getCrewId(request);
			
			contactList = this.crewContactService.queryContactListByAdvanceCondition(crewId, contactFilter, null);
		} catch (Exception e) {
			logger.error("未知异常，获取剧组联系表失败!", e);
		}
		return contactList;
	}
	
	
	/**
	 * 保存剧组联系人信息
	 * @param crewId
	 * @param userId
	 * @param contactId	联系人ID
	 * @param contactName	联系人姓名
	 * @param phone	电话
	 * @param sex	性别
	 * @param identityCardType	证件类型
	 * @param identityCardNumber	证件号码
	 * @param sysRoleIds	职务ID，多个以逗号隔开
	 * @param enterDate	入组日期（yyyy-MM-dd）
	 * @param leaveDate	离组日期（yyyy-MM-dd）
	 * @param remark	备注
	 * @param mealType	餐别
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveCrewContactInfo")
	public Map<String, Object> saveCrewContactInfo(HttpServletRequest request, String contactId, 
			String contactName, String phone, Integer sex, 
			Integer identityCardType, String identityCardNumber, String sysRoleIds,
			String enterDate, String leaveDate, String remark, Integer mealType, Boolean ifOpen) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		
		try {
			if (StringUtils.isBlank(contactName)) {
				throw new IllegalArgumentException("请填写联系人姓名");
			}
			if (contactName.length() > 20) {
				throw new IllegalArgumentException("姓名过长，请检查");
			}
			if (StringUtils.isBlank(phone)) {
				throw new IllegalArgumentException("请填写手机号");
			}
			if (phone.length() > 20) {
				throw new IllegalArgumentException("手机号过长，请检查");
			}
			if (sex == null) {
				throw new IllegalArgumentException("请选择性别");
			}
			if (StringUtils.isBlank(sysRoleIds)) {
				throw new IllegalArgumentException("请选择职务");
			}
			if (sysRoleIds.length()>225) {
				throw new IllegalArgumentException("职务字段不能超过225个字符");
			}
			if (ifOpen == null) {
				throw new IllegalArgumentException("请填写是否公开到组");
			}
			if (!StringUtils.isBlank(identityCardNumber) && identityCardNumber.length() > 18) {
				throw new IllegalArgumentException("证件号码过长，请检查");
			}
			
			String crewId = this.getCrewId(request);
			UserInfoModel loginUserInfo = (UserInfoModel) request.getSession().getAttribute(Constants.SESSION_USER_INFO);
			String userId = loginUserInfo.getUserId();
			
			
			//校验是否已有相同姓名的联系人
			ContactFilter filter = new ContactFilter();
			filter.setContactName(contactName);
			List<Map<String, Object>> list = this.crewContactService.queryContactListByAdvanceCondition(crewId, filter, null);
			if (list != null && list.size()>0) {
				for (Map<String, Object> map : list) {
					String tempId = (String) map.get("contactId");
					 if (!tempId.equals(contactId)) {
						 throw new IllegalArgumentException("当前联系人已存在，请重新填写");
					}
				}
			}
			
			//校验是否有相同手机号的联系人
			CrewContactModel model = this.crewContactService.queryByPhone(crewId, contactId, phone);
			if (model != null) {
				if (!model.getContactId().equals(contactId)) {
					throw new IllegalArgumentException("当前手机号已经存在，请重新填写");
				}
			} 
			
			CrewContactModel crewContact = this.crewContactService.saveCrewContactInfo(crewId, userId, contactId, contactName, 
					phone, sex, identityCardType, identityCardNumber, 
					sysRoleIds, enterDate, leaveDate, remark, 
					mealType, ifOpen);
			
			resultMap.put("contactId", crewContact.getContactId());
			
			String logDesc = "";
			Integer operType = null;
			if(StringUtil.isBlank(contactId)) {
				logDesc = "添加剧组联系人";
				operType = 1;
			} else {
				logDesc = "修改剧组联系人";
				operType = 2;
			}
			this.sysLogService.saveSysLog(request, logDesc, terminal, CrewContactModel.TABLE_NAME, crewContact.getContactId(), operType);
		} catch(IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
			logger.error(ie.getMessage(), ie);
		} catch (Exception e) {
			success = false;
			message = "未知异常，保存联系人信息失败";
			logger.error("未知异常，保存联系人信息失败", e);
			this.sysLogService.saveSysLog(request, "保存剧组联系人失败：" + e.getMessage(), terminal, CrewContactModel.TABLE_NAME, contactId, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 删除剧组联系表数据,此接口支持批量删除
	 * @param request
	 * @param contactId 联系人的id,多个联系人之间用","分割
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteCrewContactBatch")
	public Map<String,Object> deleteCrewContact(HttpServletRequest request, String contactIds){
		Map<String,Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = getCrewId(request);
			if (StringUtils.isBlank(contactIds)) {
				throw new IllegalArgumentException("请选择要删除的联系人!");
			}
			
			this.crewContactService.deleteCrewContact(crewId, contactIds);
			message="删除联系人成功!";
			
			this.sysLogService.saveSysLog(request, "删除剧组联系人", terminal, CrewContactModel.TABLE_NAME, null, 3);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，获取剧组联系表失败!";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "删除剧组联系人失败：" + e.getMessage(), terminal, CrewContactModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 更新剧组联系表顺序
	 * @param request
	 * @param contactIds 联系人的id,多个联系人id之间用","进行分隔
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateContactSequence")
	public Map<String,Object> updateContactSequence(HttpServletRequest request,String contactIds) {
		Map<String,Object> resultMap = new HashMap<String, Object>();
		String message = "";
		boolean success = true;
		
		try {
			String crewId = getCrewId(request);
			
			if (StringUtils.isBlank(contactIds)) {
				throw new IllegalArgumentException("请选择要更新的联系人!");
			}
			
			this.crewContactService.updateContactSequence(crewId, contactIds);
			message = "更新成功!";
			
			this.sysLogService.saveSysLog(request, "更新剧组联系表顺序", terminal, CrewContactModel.TABLE_NAME, null, 2);
		} catch (IllegalArgumentException ie) {
			message = ie.getMessage();
			success = false;
			
			logger.error(message, ie);
		} catch (Exception e) {
			message = "未知异常，更新剧组联系表顺序失败!";
			success = false;
			
			logger.error(message, e);
			this.sysLogService.saveSysLog(request, "更新剧组联系表顺序失败：" + e.getMessage(), terminal, CrewContactModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		
		return resultMap;
	}
	
	/**
	 * 导出剧组联系表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryCrewContactInfoForExport")
	public Map<String, Object> queryCrewContactInfoForExport(HttpServletRequest request,HttpServletResponse response,ContactFilter contactFilterModel){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean success = true;
		String message = "";
		try {
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			String crewName = crewInfo.getCrewName();
			String crewId = crewInfo.getCrewId();
			
			List<Map<String, Object>> result = this.crewContactService.queryContactListByAdvanceCondition(crewId,contactFilterModel,null);
			ExcelUtils.exportCrewContactForExcel(result,response,CREW_CONTACT_MAP,crewName);
		} catch(java.lang.IllegalArgumentException iException){
			message = iException.getMessage();
			success = false;
			logger.error(message);
		}catch (Exception e) {
			message = "未知异常";
			success = false;
			logger.error("未知异常",e);
		}
		resultMap.put("message", message);
		resultMap.put("success", success);
		return resultMap;
	}
	
	/**
	 * 导入剧组联系人
	 * 
	 * @param request
	 * @param file
	 * @param isCover 是否覆盖原有数据
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/importCrewContact")
	public Map<String, Object> importCrewContact(HttpServletRequest request,
			MultipartFile file, Boolean isCover) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			if (isCover == null) {
				throw new java.lang.IllegalArgumentException("参数异常");
			}
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			String crewName = crewInfo.getCrewName();
			String crewId = crewInfo.getCrewId();

			// 上传文件到服务器
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String baseStorePath = properties.getProperty("fileupload.path");
			String modelStorePath = baseStorePath + "import/contact";
			String newName = crewName + yyyyMMddFormate.format(new Date());
			Map<String, String> fileMap = FileUtils.uploadFileForExcel(request, modelStorePath, newName);
			if (fileMap == null) {
				throw new IllegalArgumentException("请选择文件");
			}
			String fileStoreName = fileMap.get("fileStoreName");// 新文件名
			String storePath = fileMap.get("storePath");// 服务器存文文件路径

			// 整理预算excel文件内容
			Map<String, Object> crewContactInfoMap = ExcelUtils.readContactInfo(storePath + fileStoreName);
			// 保存数据
			crewContactService.saveCrewContactInfoFromExcel(crewContactInfoMap, crewId, CREW_CONTACT_MAP, isCover);

			this.sysLogService.saveSysLog(request, "导入剧组联系人", terminal, CrewContactModel.TABLE_NAME, null, SysLogOperType.IMPORT.getValue());
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			logger.error("未知异常", e);
			success = false;
			message = "未知异常";
			this.sysLogService.saveSysLog(request, "导入剧组联系人失败：" + e.getMessage(), terminal, CrewContactModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
}
