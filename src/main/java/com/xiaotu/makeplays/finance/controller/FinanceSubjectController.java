package com.xiaotu.makeplays.finance.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

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
import com.xiaotu.makeplays.finance.controller.dto.FinanceSubjectDto;
import com.xiaotu.makeplays.finance.model.FinanceSubjectModel;
import com.xiaotu.makeplays.finance.model.FinanceSubjectTemplateModel;
import com.xiaotu.makeplays.finance.service.ContractActorService;
import com.xiaotu.makeplays.finance.service.ContractProduceService;
import com.xiaotu.makeplays.finance.service.ContractWorkerService;
import com.xiaotu.makeplays.finance.service.FinanceSubjectService;
import com.xiaotu.makeplays.finance.service.FinanceSubjectTemplateService;
import com.xiaotu.makeplays.finance.service.LoanInfoService;
import com.xiaotu.makeplays.finance.service.PaymentFinanSubjMapService;
import com.xiaotu.makeplays.sys.model.constants.SysLogOperType;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.ExcelUtils;
import com.xiaotu.makeplays.utils.FileUtils;
import com.xiaotu.makeplays.utils.PropertiesUitls;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 财务科目
 * @author xuchangjian 2016-7-28上午11:39:13
 */
@Controller
@RequestMapping("/financeSubject")
public class FinanceSubjectController extends BaseController {
	
	private final static SimpleDateFormat yyyyMMddFormate = new SimpleDateFormat("yyyyMMddHHmmss");
	
	Logger logger = LoggerFactory.getLogger(FinanceSubjectController.class);
	
	@Autowired
	private FinanceSubjectService financeSubjectService;
	
	@Autowired
	private FinanceSubjectTemplateService financeSubjectTemplateService;
	
	@Autowired
	private CrewInfoService crewInfoService;
	
	@Autowired
	private PaymentFinanSubjMapService paymentFinanSubjMapService;
	
	@Autowired
	private LoanInfoService loanInfoService;
	
	@Autowired
	private ContractActorService contractActorService;
	
	@Autowired
	private ContractWorkerService contractWorkerService;
	
	@Autowired
	private ContractProduceService contractProduceService;
	
	
	
	
	/**
	 * @Description 根据剧组id查询是否有财务数据关联到财务科目
	 * @param crewId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("queryForReadImport")
	public Object queryForReadImport(HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();
        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	resultMap = financeSubjectService.queryForReadImport(crewId);
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
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
	 * 跳转到财务预算页面
	 * @param pageType 跳转到的页面类型：1-账务详情  2-选择财务模板
	 * @return
	 */
	@RequestMapping("/toFinanceBudgetPage")
	public ModelAndView toFinanceBudgetPage(Integer pageType) {
		ModelAndView mv = new ModelAndView("/finance/budget/financeBudget");
		if (pageType == null || pageType == 1) {
			mv.setViewName("/finance/budget/financeBudget");
		} else if (pageType == 2) {
			mv.setViewName("/finance/budget/selectTemplate");
		}
		return mv;
	}
	
	/**
	 * 跳转到选择财务科目模板页面
	 * @return
	 */
	@RequestMapping("/toSelectTemplatePage")
	public ModelAndView toSelectTemplatePage() {
		ModelAndView mv = new ModelAndView("/finance/budget/selectTemplate");
		return mv;
	}
	
	/**
	 * 根据财务科目模板类型查询对应模板下的财务科目
	 * @param type 财务科目模板类型
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryFinanceSubjectByTempType")
	public Map<String, Object> queryFinanceSubjectByTempType(HttpServletRequest request, Integer type,String crewId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	if (type == null) {
        		throw new IllegalArgumentException("请提供模板类型");
        	}

    		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
        	if(StringUtils.isNotBlank(crewId)){
        		List<FinanceSubjectModel> financeSubjectList = this.financeSubjectService.queryByCrewId(crewId);
        		//将列表处理成easyui树状结构
        		for(FinanceSubjectModel one : financeSubjectList) {
        			Map<String, Object> map = new HashMap<String, Object>();
        			map.put("id", one.getId());
        			map.put("name", one.getName());
        			map.put("level", one.getLevel());
        			map.put("parentId", one.getParentId());
        			if(!one.getParentId().equals("0")) {
        				map.put("_parentId", one.getParentId());
        			}
        			resultList.add(map);
        		}
        	}else{
        		List<FinanceSubjectTemplateModel> financeSubjectTemplateList = this.financeSubjectTemplateService.queryByType(type);
        		resultMap.put("financeSubjectList", financeSubjectTemplateList);
        		//将列表处理成easyui树状结构
        		for(FinanceSubjectTemplateModel one : financeSubjectTemplateList) {
        			Map<String, Object> map = new HashMap<String, Object>();
        			map.put("id", one.getId());
        			map.put("name", one.getName());
        			map.put("level", one.getLevel());
        			map.put("parentId", one.getParentId());
        			if(!one.getParentId().equals("0")) {
            			map.put("_parentId", one.getParentId());
        			}
        			resultList.add(map);
        		}
        	}
    		resultMap.put("total", resultList.size());
    		resultMap.put("rows", resultList);
        	
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
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
	 * 选择财务科目模板
	 * @param type	模板类型 --0：按照制作周期  1：按照部门  2：无模板
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/selectFinanceSubjectTemplate")
	public Map<String, Object> selectFinanceSubjectTemplate(HttpServletRequest request, Integer type,String  oldCrewId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	if (type == null) {
        		throw new IllegalArgumentException("请提供模板类型");
        	}
        	
        	String crewId = this.getCrewId(request);
        	List<FinanceSubjectModel> existSubjectList = this.financeSubjectService.queryByCrewId(crewId);
            if (existSubjectList != null && existSubjectList.size() > 0) {
            	throw new IllegalArgumentException("该剧组下已存在财务科目");
            }
            
            List<FinanceSubjectModel> subjectList = null;
            if(StringUtils.isNotBlank(oldCrewId)){
            	subjectList = this.financeSubjectService.queryByCrewId(oldCrewId);
            	
            	//替换财务科目id
            	Map<String,String> idMap = new HashMap<String, String>();
            	//整理查询结果数据
            	for(FinanceSubjectModel financeSubjectModel :subjectList){
            		String id = financeSubjectModel.getId();
            		String parentId = financeSubjectModel.getParentId();
            		String newId = idMap.get(id);
            		if(StringUtils.isBlank(newId)){
            			newId = UUIDUtils.getId();
            			idMap.put(id, newId);
            			financeSubjectModel.setId(newId);
            		}
            		String newParentId = idMap.get(parentId);
            		if("0".equals(parentId)){
            			newParentId = parentId;
            		}else{
            			if(StringUtils.isBlank(newParentId)){
            				newParentId = UUIDUtils.getId();
            				idMap.put(parentId, newParentId);
            			}
            		}
            		
            		
        			financeSubjectModel.setParentId(newParentId);
            		financeSubjectModel.setCrewId(crewId);
            		financeSubjectModel.setCreateTime(new Date());
            		financeSubjectModel.setRemark("");
            	}
            	
            	
            }else{
            	List<FinanceSubjectTemplateModel> financeSubjectList = this.financeSubjectTemplateService.queryByType(type);
            	List<FinanceSubjectDto> subjectDtoList = this.loopFinanceSubject(new ArrayList<FinanceSubjectDto>(), financeSubjectList);
            	
            	subjectList = this.genFinanceSubject(crewId, subjectDtoList, new ArrayList<FinanceSubjectModel>(), null);
            }
            
        	
        	this.financeSubjectService.addMany(subjectList);
        	
        	this.sysLogService.saveSysLog(request, "选择财务科目模板", Constants.TERMINAL_PC, FinanceSubjectModel.TABLE_NAME, null, 1);
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
        } catch(Exception e) {
            success = false;
            message = "未知异常";

            logger.error("未知异常", e);
        	this.sysLogService.saveSysLog(request, "选择财务科目模板失败：" + e.getMessage(), Constants.TERMINAL_PC, FinanceSubjectModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
        }

        resultMap.put("success", success);
        resultMap.put("message", message);
        return resultMap;
	}
	
	/**
	 * 遍历财务科目数据
	 * 把原本用parentid表示父子关系的方式改成FinanceSubjectDto的表现形式
	 * @param subjectDtoList
	 * @param financeSubjectList
	 * @return
	 */
	private List<FinanceSubjectDto> loopFinanceSubject(List<FinanceSubjectDto> subjectDtoList, List<FinanceSubjectTemplateModel> financeSubjectList) {
		List<FinanceSubjectDto> mySubjectDtoList = new ArrayList<FinanceSubjectDto>();
		
		/*
		 * 首先过滤出纯粹子节点科目
		 */
		List<FinanceSubjectTemplateModel> parentSubjectList = new ArrayList<FinanceSubjectTemplateModel>();
		List<FinanceSubjectTemplateModel> childSubjectList = new ArrayList<FinanceSubjectTemplateModel>();
		
		for (FinanceSubjectTemplateModel fsubject : financeSubjectList) {
			String fid = fsubject.getId();
			String fparentId = fsubject.getParentId();

			boolean isParent = false;
			boolean isChild = false;
			for (FinanceSubjectTemplateModel csubject : financeSubjectList) {
				String cid = csubject.getId();
				String cparentId = csubject.getParentId();
				
				if (fid.equals(cparentId)) {
					isParent = true;
				}
				if (fparentId.equals(cid)) {
					isChild = true;
				}
			}
			
			//双层循环遍历科目列表，区分中哪些科目是别人的子科目，哪些科目是别人的父科目
			//因为数据嵌套多层，过滤出的这两类数据必然会有重合的地方，但是childSubjectList中有而parentSubjectList没有的数据必然是叶子节点
			if (isParent) {
				parentSubjectList.add(fsubject);
			}
			if (isChild || (!isParent && !isChild)) {
				childSubjectList.add(fsubject);
			}
		}
		
		//childSubjectList中有而parentSubjectList没有的数据必然是叶子节点
		List<FinanceSubjectTemplateModel> leafSubjectList = new ArrayList<FinanceSubjectTemplateModel>();
		
		for (FinanceSubjectTemplateModel csubject : childSubjectList) {
			String cid = csubject.getId();
			boolean exist = false;
			for (FinanceSubjectTemplateModel fsubject : parentSubjectList) {
				String fid = fsubject.getId();
				if (cid.equals(fid)) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				leafSubjectList.add(csubject);
			}
		}
		
		
		/*
		 * 为最后的结果字段赋值
		 * leafSubjectList表示当前循环中的叶子科目
		 * 但是相对于上一层传过来的subjectDtoList，leafSubjectList中有些数据为subjectDtoList中数据的父科目
		 * 因此，此处对比出leafSubjectList中每个科目的子科目，然后为相应字段赋值
		 * 
		 * 如果数据在subjectDtoList存在且在leafSubjectList中找不到任何父科目，则说明此数据层级也为当前循环的叶子科目
		 */
		int parentSequence = 0;
		for (FinanceSubjectTemplateModel subject : leafSubjectList) {
			List<FinanceSubjectDto> children = new ArrayList<FinanceSubjectDto>();
			
			int sequence = 0;
			for (FinanceSubjectDto subjectDto : subjectDtoList) {
				if (subjectDto.getParentId().equals(subject.getId())) {
					subjectDto.setSequence(sequence ++);
					children.add(subjectDto);
				}
			}
			
			FinanceSubjectDto mySubjectDto = new FinanceSubjectDto();
			mySubjectDto.setId(subject.getId());
			mySubjectDto.setName(subject.getName());
			mySubjectDto.setLevel(subject.getLevel());
			mySubjectDto.setParentId(subject.getParentId());
			mySubjectDto.setChildren(children);
			mySubjectDto.setSequence(parentSequence ++);
			
			mySubjectDtoList.add(mySubjectDto);
		}
		for (FinanceSubjectDto subjectDto : subjectDtoList) {
			boolean exist = false;
			for (FinanceSubjectTemplateModel subject : leafSubjectList) {
				if (subjectDto.getParentId().equals(subject.getId())) {
					exist = true;
				}
			}
			
			if (!exist) {
				mySubjectDtoList.add(subjectDto);
			}
		}
		
		if (parentSubjectList.size() > 0) {
			financeSubjectList.removeAll(leafSubjectList);
			mySubjectDtoList = this.loopFinanceSubject(mySubjectDtoList, financeSubjectList);
		}
		
		return mySubjectDtoList;
	}
	
	/**
	 * 根据FinanceSubjectDto列表生成新的FinanceSubjectModel列表
	 * @param subjectDtoList
	 * @param subjectModelList
	 * @return
	 */
	private List<FinanceSubjectModel> genFinanceSubject(String crewId,
			List<FinanceSubjectDto> subjectDtoList,
			List<FinanceSubjectModel> subjectList, 
			FinanceSubjectModel subject) {
		List<FinanceSubjectModel> mySubjectModelList = subjectList;
		
		for (FinanceSubjectDto subjectDto : subjectDtoList) {
			FinanceSubjectModel subjectModel = new FinanceSubjectModel();
			subjectModel.setId(UUIDUtils.getId());
			subjectModel.setCrewId(crewId);
			subjectModel.setName(subjectDto.getName());
			subjectModel.setLevel(subjectDto.getLevel());
			subjectModel.setSequence(subjectDto.getSequence());
			if (subject == null) {
				subjectModel.setParentId("0");
			} else {
				subjectModel.setParentId(subject.getId());
			}
			subjectModel.setCreateTime(new Date());
			
			mySubjectModelList.add(subjectModel);
			
			List<FinanceSubjectDto> subSubjectDtoList = subjectDto.getChildren();
			if (subSubjectDtoList != null && subSubjectDtoList.size() > 0) {
				mySubjectModelList = this.genFinanceSubject(crewId, subSubjectDtoList, mySubjectModelList, subjectModel);
			}
		}
		
		return mySubjectModelList;
	}
	
	/**
	 * 判断剧组中是否已经有财务科目
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/nopassword/hasFinanceSubject")
	public Map<String, Object> hasFinanceSubject(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
            String crewId = this.getCrewId(request);
            
            boolean hasSubject = false;
            
            List<FinanceSubjectModel> subjectList = this.financeSubjectService.queryByCrewId(crewId);
            if (subjectList != null && subjectList.size() > 0) {
            	hasSubject = true;
            }
        	
        	resultMap.put("hasSubject", hasSubject);
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
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
	 * 更新财务科目顺序
	 * @param request
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateSubjectSequence")
	public Map<String, Object> updateSubjectSequence(HttpServletRequest request,String ids) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
            String crewId = this.getCrewId(request);
            
            this.financeSubjectService.updateSubjectSequence(crewId, ids);
            
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
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
	 * 把财务科目移动到上一级
	 * @param financeSubjId
	 * @param financeSubjParentId
	 * @param level
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/upSubjectLevel")
	public Map<String, Object> upSubjectLevel(HttpServletRequest request, String financeSubjId, String financeSubjParentId, Integer level) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
            //校验是否是一级科目
            FinanceSubjectModel parentSubject = this.financeSubjectService.queryById(financeSubjParentId);
            if (parentSubject == null) {
            	throw new IllegalArgumentException("当前为一级科目，不能向上移动");
            }
            
            this.financeSubjectService.upSubjectLevel(financeSubjId, financeSubjParentId, level);
            
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
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
	 * 获取财务科目列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/querySubjectList")
	public Map<String, Object> querySubjectList(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	
        	Map<String, Object> conditionMap = new HashMap<String, Object>();
        	conditionMap.put("crewId", crewId);
        	
            List<FinanceSubjectModel> subjectList = this.financeSubjectService.queryManyByMutiCondition(conditionMap, null);
            
            resultMap.put("subjectList", subjectList);
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
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
	 * 获取财务科目列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/querySubjectListWithJqxTreeFormat")
	public Map<String, Object> querySubjectListWithJqxTreeFormat(HttpServletRequest request,String crewId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	if(StringUtils.isBlank(crewId)){
        		crewId = this.getCrewId(request);
        	}
        	
        	Map<String, Object> conditionMap = new HashMap<String, Object>();
        	conditionMap.put("crewId", crewId);
        	
            List<FinanceSubjectModel> subjectList = this.financeSubjectService.queryManyByMutiCondition(conditionMap, null);
            
            List<Map<String, Object>> subjectMapList = new ArrayList<Map<String, Object>>();
            for (FinanceSubjectModel subject : subjectList) {
            	Map<String, Object> subjectMap = new HashMap<String, Object>();
            	subjectMap.put("id", subject.getId());
            	subjectMap.put("parentId", subject.getParentId());
            	subjectMap.put("text", subject.getName());
            	
            	subjectMapList.add(subjectMap);
            }
            
            resultMap.put("subjectList", subjectMapList);
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
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
	 * 获取财务科目列表
	 * 格式化为bootstrap-treeview树形结构
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/querySubjectListWithNodeTreeFormat")
	public Map<String, Object> querySubjectListWithNodeTreeFormat(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	
        	Map<String, Object> conditionMap = new HashMap<String, Object>();
        	conditionMap.put("crewId", crewId);
        	
            List<FinanceSubjectModel> subjectList = this.financeSubjectService.queryManyByMutiCondition(conditionMap, null);
            List<Map<String, Object>> resultList = this.loopSubjectForNodeTree(subjectList, new ArrayList<Map<String, Object>>());
            
            resultMap.put("resultList", resultList);
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
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
	 * 递归财务科目列表
	 * 封装成id, text, nodes的格式
	 * @param subjectList
	 * @param resultList
	 * @return
	 */
	private List<Map<String, Object>> loopSubjectForNodeTree(List<FinanceSubjectModel> subjectList, List<Map<String, Object>> resultList) {
		List<Map<String, Object>> mySubjectMapList = new ArrayList<Map<String, Object>>();
		
		/*
		 * 首先过滤出纯粹子节点科目
		 */
		List<FinanceSubjectModel> parentSubjectList = new ArrayList<FinanceSubjectModel>();
		List<FinanceSubjectModel> childSubjectList = new ArrayList<FinanceSubjectModel>();
		
		for (FinanceSubjectModel fsubject : subjectList) {
			String fid = fsubject.getId();
			String fparentId = fsubject.getParentId();

			boolean isParent = false;
			boolean isChild = false;
			for (FinanceSubjectModel csubject : subjectList) {
				String cid = csubject.getId();
				String cparentId = csubject.getParentId();
				
				if (fid.equals(cparentId)) {
					isParent = true;
				}
				if (fparentId.equals(cid)) {
					isChild = true;
				}
			}
			
			//双层循环遍历科目列表，区分中哪些科目是别人的子科目，哪些科目是别人的父科目
			//因为数据嵌套多层，过滤出的这两类数据必然会有重合的地方，但是childSubjectList中有而parentSubjectList没有的数据必然是叶子节点
			if (isParent) {
				parentSubjectList.add(fsubject);
			}
			if (isChild || (!isParent && !isChild)) {
				childSubjectList.add(fsubject);
			}
		}
		
		//childSubjectList中有而parentSubjectList没有的数据必然是叶子节点
		List<FinanceSubjectModel> leafSubjectList = new ArrayList<FinanceSubjectModel>();
		
		for (FinanceSubjectModel csubject : childSubjectList) {
			String cid = csubject.getId();
			boolean exist = false;
			for (FinanceSubjectModel fsubject : parentSubjectList) {
				String fid = fsubject.getId();
				if (cid.equals(fid)) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				leafSubjectList.add(csubject);
			}
		}
		
		
		/*
		 * 为最后的结果字段赋值
		 * leafSubjectList表示当前循环中的叶子科目
		 * 但是相对于上一层传过来的resultList，leafSubjectList中有些数据为resultList中数据的父科目
		 * 因此，此处对比出leafSubjectList中每个科目的子科目，然后为相应字段赋值
		 * 
		 * 如果数据在resultList存在且在leafSubjectList中找不到任何父科目，则说明此数据层级也为当前循环的叶子科目
		 */
		for (FinanceSubjectModel subject : leafSubjectList) {
			List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
			
			for (Map<String, Object> subjectMap : resultList) {
				String parentId = (String) subjectMap.get("parentId");
				
				if (parentId.equals(subject.getId())) {
					children.add(subjectMap);
				}
			}
			
			//为子科目排序
			Collections.sort(children, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					int o1sequence = (Integer) o1.get("sequence");
					int o2sequence = (Integer) o2.get("sequence");
	        		return o1sequence - o2sequence;
				}
			});
			
			Map<String, Object> mySubjectMap = new HashMap<String, Object>();
			mySubjectMap.put("id", subject.getId());
			mySubjectMap.put("parentId", subject.getParentId());
			mySubjectMap.put("text", subject.getName());
			mySubjectMap.put("sequence", subject.getSequence());
			if (children.size() > 0) {
				mySubjectMap.put("nodes", children);
			}
			
			
			mySubjectMapList.add(mySubjectMap);
		}
		for (Map<String, Object> subjectMap : resultList) {
			boolean exist = false;
			for (FinanceSubjectModel subject : leafSubjectList) {
				String parentId = (String) subjectMap.get("parentId");
				if (parentId.equals(subject.getId())) {
					exist = true;
				}
			}
			
			if (!exist) {
				mySubjectMapList.add(subjectMap);
			}
		}
		
		if (parentSubjectList.size() > 0) {
			subjectList.removeAll(leafSubjectList);
			mySubjectMapList = this.loopSubjectForNodeTree(subjectList, mySubjectMapList);
		}
		
		//排序
		Collections.sort(mySubjectMapList, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				int o1sequence = (Integer) o1.get("sequence");
				int o2sequence = (Integer) o2.get("sequence");
        		return o1sequence - o2sequence;
			}
		});
		return mySubjectMapList;
	}
	
	/**
	 * 获取财务科目列表
	 * 格式化成easyui树表的数据格式
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/querySubjectListWithTreeFormat")
	public Map<String, Object> querySubjectListWithTreeFormat(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

        boolean success = true;
        String message = "";
        try {
        	String crewId = this.getCrewId(request);
        	Map<String, Object> conditionMap = new HashMap<String, Object>();
        	conditionMap.put("crewId", crewId);
        	
            List<FinanceSubjectModel> subjectList = this.financeSubjectService.queryManyByMutiCondition(conditionMap, null);
            
            List<Map<String, Object>> subjectMapList = new ArrayList<Map<String, Object>>();
            for (FinanceSubjectModel subject : subjectList) {
            	Map<String, Object> subjectMap = new HashMap<String, Object>();
            	subjectMap.put("financeSubjId", subject.getId());
            	subjectMap.put("financeSubjName", subject.getName());
            	if (subject.getLevel() != 1) {
            		subjectMap.put("_parentId", subject.getParentId());
				}
            	
            	subjectMapList.add(subjectMap);
            }
            
            resultMap.put("total", subjectMapList.size());
			resultMap.put("rows", subjectMapList);
        } catch (IllegalArgumentException ie) {
            success = false;
            message = ie.getMessage();
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
	 * 导入财务预算
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/importBudget")
	public Map<String, Object> importBudget(HttpServletRequest request, MultipartFile file) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			CrewInfoModel crewInfo = this.getSessionCrewInfo(request);
			String crewName = crewInfo.getCrewName();
			String crewId = crewInfo.getCrewId();

			// 上传文件到服务器
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String baseStorePath = properties.getProperty("fileupload.path");
			String modelStorePath = baseStorePath + "import/finance";
			String newName = crewName + yyyyMMddFormate.format(new Date());
			Map<String, String> fileMap = FileUtils.uploadFileForExcel(request, modelStorePath, newName);
			if (fileMap == null) {
				throw new IllegalArgumentException("请选择文件");
			}
			String fileRealName = fileMap.get("fileRealName");// 原文件名
			String fileStoreName = fileMap.get("fileStoreName");// 新文件名
			String storePath = fileMap.get("storePath");// 服务器存文文件路径

			// 整理预算excel文件内容
			Map<String, Object> list = ExcelUtils.financeSubject(storePath + fileStoreName);
			financeSubjectService.saveImportExcelForBudgetInfo(list, crewId);
			
			this.sysLogService.saveSysLog(request, "导入财务预算", Constants.TERMINAL_PC, FinanceSubjectModel.TABLE_NAME, null, 4);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			logger.error("未知异常", e);
			
			success = false;
			message = "未知异常";
			this.sysLogService.saveSysLog(request, "导入财务预算失败：" + e.getMessage(), Constants.TERMINAL_PC, FinanceSubjectModel.TABLE_NAME, null, SysLogOperType.ERROR.getValue());
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
}
