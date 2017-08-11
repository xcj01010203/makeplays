package com.xiaotu.makeplays.user.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.authority.service.AuthorityService;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.crew.model.constants.CrewType;
import com.xiaotu.makeplays.roleactor.model.ViewRoleModel;
import com.xiaotu.makeplays.sysrole.model.SysroleInfoModel;
import com.xiaotu.makeplays.sysrole.service.SysRoleInfoService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.view.controller.filter.ViewFilter;
import com.xiaotu.makeplays.view.model.AtmosphereInfoModel;
import com.xiaotu.makeplays.view.model.ViewInfoModel;
import com.xiaotu.makeplays.view.model.ViewRoleAndActorModel;
import com.xiaotu.makeplays.view.service.AtmosphereService;
import com.xiaotu.makeplays.view.service.ViewInfoService;

@Controller
@RequestMapping("/roleManager")
public class RoleController  extends BaseController {
	
	Logger logger = LoggerFactory.getLogger(RoleController.class);

	@Autowired
	private SysRoleInfoService sysRoleInfoService;
	
	@Autowired
	private AuthorityService authorityService;
	
	@Autowired
	private ViewInfoService viewInfoService;
	
	@Autowired
	private AtmosphereService atmosphereService;
	
	/**
	 * 跳转到角色权限页面
	 * @return
	 */
	@RequestMapping("/toRoleAuthDetailPage")
	public ModelAndView toRoleAuthDetailPage(String roleId) {
		ModelAndView mv = new ModelAndView("role/roleAuthDetail");
		mv.addObject("aimRoleId", roleId);
		return mv;
	}
	
	/**
	 * 跳转到角色管理页面
	 * @return
	 */
	@RequestMapping("/toRoleListPage")
	public ModelAndView toRoleListPage() {		
		ModelAndView view = new ModelAndView("user/roleList");		
		return view;	
	}
	
	/**
	 * 获取角色列表
	 * @param request
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("queryRoleList")
	@ResponseBody
	public Map<String, Object> queryRoleListJson(HttpServletRequest request,Page page) throws Exception{
			
			Map<String, Object> resultMap = new HashMap<String, Object>();

	        boolean success = true;
	        String message = "";
	        try {
	        	String crewId = this.getCrewId(request);
	        	Map<String, Object> conditionMap = new HashMap<String, Object>();
	        	conditionMap.put("crewId", crewId);
	        	
	        	List<Map<String, Object>> list = sysRoleInfoService.queryRoleWithCrewNameByPage(null);
	        	
	        	resultMap.put("total", 0);
	        	if(list != null && list.size() > 0) {
		        	for(Map<String, Object> map : list) {
		        		String parentId = map.get("parentId") + "";
		        		if(!parentId.equals("00") && !parentId.equals("01")) {
			        		map.put("_parentId", map.get("parentId"));
		        		}
		        	}
		            resultMap.put("total", list.size());
	        	}
	            
				resultMap.put("rows", list);
				
//				this.sysLogService.saveSysLog(request, "获取系统角色表", Constants.TERMINAL_PC, "tab_sysrole_info", null,0);
	        } catch(Exception e) {
	            success = false;
	            message = "未知异常";

	            logger.error("未知异常", e);
	        }

	        resultMap.put("success", success);
	        resultMap.put("message", message);
	        return resultMap;		
	}	

    /**
     * 判断角色名是否已存在
     * @param roleName
     * @return
     */
	@ResponseBody
    @RequestMapping("/isExistRoleName")
   	public Map<String, Object> isExistRoleName(String roleName, String roleId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String count= this.sysRoleInfoService.getRoleName(roleName, roleId);
        	if(Integer.parseInt(count)>0){
          	  	resultMap.put("result", true);
            } else {
            	resultMap.put("result", false);
            }
        } catch(Exception e) {
            success = false;
            message = "未知异常,判断角色名是否已存在发生异常";

            logger.error(message, e);
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
    }
	
	/**
	 * 保存角色信息
	 * @param request
	 * @param roleInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveRole")
	public Map<String, Object> saveRole(HttpServletRequest request, SysroleInfoModel roleInfo) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	if(StringUtils.isBlank(roleInfo.getCrewId())){
  			  	roleInfo.setCrewId("0");
  		  	}
        	if(StringUtils.isBlank(roleInfo.getRoleId())){
				Integer roleId = sysRoleInfoService.getRoleMax();
				roleInfo.setRoleId((roleId + 1) + "");
				int orderNo = sysRoleInfoService.getRoleOrderNoMax(roleInfo.getParentId());
				roleInfo.setOrderNo(orderNo + 1);
				sysRoleInfoService.addRole(roleInfo);
	        	this.sysLogService.saveSysLog(request, "保存角色信息", Constants.TERMINAL_PC, "tab_sysrole_info", roleInfo.getRoleId(),1);
			}else{
				sysRoleInfoService.updateRole(roleInfo);
	        	this.sysLogService.saveSysLog(request, "修改角色信息", Constants.TERMINAL_PC, "tab_sysrole_info", roleInfo.getRoleId(),2);
			}
        } catch(Exception e) {
            success = false;
            message = "未知异常,保存角色信息发生异常";

            logger.error(message, e);
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
	
	/**
	 * 查询单个角色信息
	 * @param request
	 * @param roleId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryRoleInfo")
	public Map<String, Object> queryRoleInfo(HttpServletRequest request, String roleId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	SysroleInfoModel roleInfo = sysRoleInfoService.queryById(roleId);    		
        	resultMap.put("roleInfo", roleInfo);
        	
        } catch(Exception e) {
            success = false;
            message = "未知异常,查询角色信息发生异常";

            logger.error(message, e);
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
	
	/**
	 * 查询角色是否已被引用
	 */
	@ResponseBody
	@RequestMapping("/isExistUserRoleMap")
	public Map<String, Object> isExistUserRoleMap(String roleId){
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	int count = this.sysRoleInfoService.getCountOfUserRoleMap(roleId);
        	if(count > 0) {
        		resultMap.put("result", true);
        	} else {
        		resultMap.put("result", false);
        	}
        } catch(Exception e) {
            success = false;
            message = "未知异常,查询角色是否已被引用发生异常";

            logger.error(message, e);
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}

    /**
     * 删除角色
     * @param roleId
     * @return
     */
	@ResponseBody
    @RequestMapping("deleteRole")
	public Map<String, Object> deleteRole(String roleId,String crewId,HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	this.sysRoleInfoService.delRole(roleId,crewId);
        	this.sysLogService.saveSysLog(request, "删除系统角色", Constants.TERMINAL_PC, "tab_sysrole_info", roleId, 3);
        } catch(Exception e) {
            success = false;
            message = "未知异常,删除角色发生异常";

            logger.error(message, e);
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;    	
    }
    
    /**
     * 更新角色表顺序
     * @param request
     * @param ids 角色IDs
     * @return
     */
    @RequestMapping("/updateRoleSequence")
    @ResponseBody
    public Map<String, Object> updateRoleSequence(HttpServletRequest request,String ids) {
    	Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	
			this.sysRoleInfoService.updateRoleSequence(crewId, ids);
        } catch(Exception e) {
            success = false;
            message = "未知异常，更新角色顺序失败";

            logger.error(message, e);
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
    }
    
    /**
	 * 场景表角色导出
	 * @param request
	 * @param filter
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping("exportExcel")
	public void exportExcel(HttpServletRequest request,String roleIds,HttpServletResponse response) throws Exception{
		
		String crewId = getCrewId(request);
		Properties property = PropertiesUitls.fetchProperties("/config.properties");
		
		CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
		Integer crewType = crewInfo.getCrewType();
		String srcfilePath = "";
		if (crewType == CrewType.Movie.getValue()) {
			srcfilePath = property.getProperty("movie_viewTemplate");
		} else {
			srcfilePath = property.getProperty("tvplay_viewTemplate");
		}
		
		ViewFilter filter = null;
		List<File> srcfile=new ArrayList<File>();
		String roleidstr[] = roleIds.split(",");
		File pathFile = new File(property.getProperty("downloadPath"));
		if(!pathFile.isDirectory()){
			pathFile.mkdirs();
		}
		for(int k=0;k<roleidstr.length;k++){
			filter = new ViewFilter();
			filter.setRoles(roleidstr[k]);
			List<Map<String, Object>> resultList = viewInfoService.queryViewInfoList(crewId, null,filter);
			
			//获取角色名称
			String rolemodel = this.sysRoleInfoService.getRoleNameById(roleidstr[k]);
			
			//获取模板文件地址配置
			String downloadPath = property.getProperty("downloadPath")+"/场景表("+rolemodel+")"+new SimpleDateFormat("yyyyMMdd").format(new Date())+".xls";
			
			//返回的list中每个元素都为Map，map中都包含一个属性roleList，roleList为当前场的所有演员Id
			if(null == resultList){
				resultList=new ArrayList();
			}
			List<AtmosphereInfoModel> atmo = atmosphereService.queryAllByCrewId(crewId);
			Map atmoMap=new HashMap();
			for(AtmosphereInfoModel atmosphere:atmo){
				atmoMap.put(atmosphere.getAtmosphereId(), atmosphere.getAtmosphereName());
			}
			//季节
			Map seasonMap = new HashMap();
			seasonMap.put("1","春");
			seasonMap.put("2","夏");
			seasonMap.put("3","秋");
			seasonMap.put("4","冬");
			seasonMap.put("99","未知");
			//内外景
			Map siteMap = new HashMap();
			siteMap.put("1","内景");
			siteMap.put("2","外景");
			siteMap.put("3","内外景");
			//场景类型
			Map typeMap = new HashMap();
			typeMap.put("1","文戏");
			typeMap.put("2","武戏");
			typeMap.put("3","文武戏");

			//拍摄状态
			Map shootStatusMap = new HashMap();
			shootStatusMap.put("0","未完成");
			shootStatusMap.put("1","部分完成");
			shootStatusMap.put("2","完成");
			shootStatusMap.put("3","删戏");
			
			List<ViewRoleAndActorModel> roleSignList = viewInfoService.queryViewRoleSign(crewId);
			
			if(roleSignList.size()>240){
				List<ViewRoleAndActorModel> newRoleSignList = new ArrayList<ViewRoleAndActorModel>();
				newRoleSignList.addAll(roleSignList.subList(0, 229));
				roleSignList=newRoleSignList;
			}
			
			//遍历查询的场景列表
			for(Map resultMap:resultList){
				
				if(null != resultMap.get("atmosphereId")){
					resultMap.put("atmosphere", atmoMap.get(resultMap.get("atmosphereId")));
				}
				
				if(null != resultMap.get("season")){
					resultMap.put("season", seasonMap.get(((Integer)resultMap.get("season")).intValue()+""));
				}
				
				if(null != resultMap.get("viewType")){
					resultMap.put("viewType", typeMap.get(((Integer)resultMap.get("viewType")).intValue()+""));
				}
				
				if(null != resultMap.get("shootStatus")){
					resultMap.put("shootStatus", shootStatusMap.get(((Integer)resultMap.get("shootStatus")).intValue()+""));
				}
				
				//场景下的角色
				List<Map> roleList = (List<Map>) resultMap.get("roleList");
				List<ViewRoleModel> newRoleList = new ArrayList<ViewRoleModel>();
				//循环所有角色
				for(int i=roleSignList.size()-1;i>=0;i--){
					ViewRoleModel role = roleSignList.get(i);
					
					boolean hasRoleFlag = false;	//标识当前场景的演员在所有主要演员中是否存在
					for(Map roleMap: roleList){
						if(roleMap.get("viewRoleId").equals(role.getViewRoleId())){
							if(StringUtils.isBlank(role.getShortName())){
								role.setShortName("√");
							}
							newRoleList.add(role);
							hasRoleFlag = true;
							break;
						}
					}
					
					//如果不存在就添加一个空的对象，保证在表格中显示列正确
					if (!hasRoleFlag) {
						newRoleList.add(new ViewRoleModel());
					}
					
				}
				Collections.reverse(newRoleList);
				//resultMap.remove("rooeList");
				resultMap.put("roleList",newRoleList);
			}
			
			Map map = new HashMap();
			map.put("resultList", resultList);
			//map.put("result", page);
			//map.put("atmo", atmo);
			//map.put("sceneStatistics", sceneStatistics);
			map.put("roleSignList",roleSignList);
			
			sysLogService.saveSysLog(request, "场景表按角色导出", Constants.TERMINAL_PC, ViewInfoModel.TABLE_NAME, null,5);
			
			viewInfoService.exportViewToExcelTemplate(srcfilePath, map, downloadPath);
			File file11 = new File(downloadPath);
			srcfile.add(file11);
		}
		
		long sct = System.currentTimeMillis();
		File zipfile = new File(property.getProperty("downloadPath")+"view_list_"+sct+".zip");
		
		String zipfilepath = property.getProperty("downloadPath")+"/view_list_"+sct+".zip";
        zipFiles(srcfile, zipfile); 
        response.setHeader("Content-Disposition", "attachment;fileName="+java.net.URLEncoder.encode("场景表"+new SimpleDateFormat("yyyyMMdd").format(new Date())+".zip","UTF-8"));
		response.setContentType("application/x-zip-compressed");
        response.setCharacterEncoding("UTF-8");
        InputStream inputStream=new FileInputStream(zipfilepath);
        OutputStream os=response.getOutputStream();  
        byte[] b=new byte[1024];  
        int length;  
        while((length=inputStream.read(b))>0){  
            os.write(b,0,length);  
        }  
        inputStream.close();  
		
	}
	
	 /** 
     * 压缩文件 
     *  
     * @param srcfile File[] 需要压缩的文件列表 
     * @param zipfile File 压缩后的文件 
     */  
    public void zipFiles(List<File> srcfile, File zipfile) {  
        byte[] buf = new byte[1024];  
        try {  
            // Create the ZIP file  
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));  
            // Compress the files  
            for (int i = 0; i < srcfile.size(); i++) {  
                File file = srcfile.get(i);  
                FileInputStream in = new FileInputStream(file);  
                // Add ZIP entry to output stream.  
                out.putNextEntry(new ZipEntry(file.getName()));  
                // Transfer bytes from the file to the ZIP file  
                int len;  
                while ((len = in.read(buf)) > 0) {  
                    out.write(buf, 0, len);  
                }  
                // Complete the entry  
                out.closeEntry();  
                in.close();  
            }  
            // Complete the ZIP file  
            out.close();  
        } catch (IOException e) {  
        	logger.error("ZipUtil zipFiles exception:"+e);  
        }  
    }
}
