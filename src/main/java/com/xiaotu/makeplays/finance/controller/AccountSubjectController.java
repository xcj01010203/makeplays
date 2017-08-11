package com.xiaotu.makeplays.finance.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xiaotu.makeplays.finance.model.AccoFinacSubjMapModel;
import com.xiaotu.makeplays.finance.model.AccountSubjectModel;
import com.xiaotu.makeplays.finance.model.FinanceSubjectModel;
import com.xiaotu.makeplays.finance.service.AccoFinacSubjMapService;
import com.xiaotu.makeplays.finance.service.AccountSubjecService;
import com.xiaotu.makeplays.finance.service.FinanceSubjectService;
import com.xiaotu.makeplays.utils.BaseController;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.UUIDUtils;

/**
 * 会计科目
 * @author xuchangjian 2016-6-22上午11:01:07
 */
@Controller
@RequestMapping("/accountSubject")
public class AccountSubjectController extends BaseController {
	
	Logger logger = LoggerFactory.getLogger(AccountSubjectController.class);
	
	@Autowired
	private AccountSubjecService accountSubjectService;
	
	@Autowired
	private AccoFinacSubjMapService accoFinacSubjMapService;
	
	@Autowired
	private FinanceSubjectService financeSubjectService;
	
	/**
	 * 跳转到会计科目页面
	 * @return
	 */
	@RequestMapping("/toAccountSubjectPage")
	public ModelAndView toAccountSubjectPage() {
		ModelAndView mv = new ModelAndView("/finance/budget/accountSubject");
		return mv;
	}

	/**
	 * 保存会计科目信息（新增或修改）
	 * @param id
	 * @param name	名称
	 * @param code	代码
	 * @param sequence	排列顺序
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveAccountSubjectInfo")
	public Map<String, Object> saveAccountSubjectInfo(HttpServletRequest request, String id, String name, String code, Integer sequence) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		
		try {
			String crewId = getCrewId(request);
			
			if (StringUtils.isBlank(name)) {
				throw new IllegalArgumentException("请填写科目名称");
			}
			if (StringUtils.isBlank(code)) {
				throw new IllegalArgumentException("请填写科目代码");
			}
			
			List<AccountSubjectModel> accountList = this.accountSubjectService.queryByCodeExpOwn(crewId, id, code);
			if (accountList != null && accountList.size() > 0) {
				throw new IllegalArgumentException("科目代码与“" + accountList.get(0).getName() + "”重复，请更正。");
			}
			
			if (!StringUtils.isBlank(id)) {
				AccountSubjectModel accSubjectModel = this.accountSubjectService.queryById(id);
				accSubjectModel.setName(name);
				accSubjectModel.setCode(code);
				accSubjectModel.setCrewId(crewId);
				if (sequence != null) {
					accSubjectModel.setSequence(sequence);
				}
				this.accountSubjectService.updateOne(accSubjectModel);
				
				this.sysLogService.saveSysLog(request, "添加会计科目", Constants.TERMINAL_PC, AccountSubjectModel.TABLE_NAME, id, 1);
			} else {
				int maxSequence = this.accountSubjectService.queryMaxSequence(crewId);
				id = UUIDUtils.getId();
				
				AccountSubjectModel accSubjectModel = new AccountSubjectModel();
				accSubjectModel.setId(id);
				accSubjectModel.setName(name);
				accSubjectModel.setCode(code);
				accSubjectModel.setCrewId(crewId);
				accSubjectModel.setSequence(++maxSequence);
				if (sequence != null) {
					accSubjectModel.setSequence(sequence);
				}
				this.accountSubjectService.addOne(accSubjectModel);
				
				this.sysLogService.saveSysLog(request, "修改会计科目", Constants.TERMINAL_PC, AccountSubjectModel.TABLE_NAME, id, 2);
			}
			
			resultMap.put("id", id);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);

			this.sysLogService.saveSysLog(request, "保存会计科目失败：" + e.getMessage(), Constants.TERMINAL_PC, AccountSubjectModel.TABLE_NAME, id, 6);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 删除会计科目信息
	 * @param id
	 * @param name	名称
	 * @param code	代码
	 * @param sequence	排列顺序
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteAccountSubjectInfo")
	public Map<String, Object> deleteAccountSubjectInfo(HttpServletRequest request, String id) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(id)) {
				throw new IllegalArgumentException("请选择要删除的科目");
			}
			
			this.accountSubjectService.deleteOne(id);
			
			this.sysLogService.saveSysLog(request, "删除会计科目", Constants.TERMINAL_PC, AccountSubjectModel.TABLE_NAME, id, 3);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "删除会计科目失败：" + e.getMessage(), Constants.TERMINAL_PC, AccountSubjectModel.TABLE_NAME, id, 6);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 修改会计科目和财务科目的关联
	 * @param request
	 * @param accountSubjId	会计科目ID
	 * @param budgetSubjId	财务科目ID
	 * @param operateType	操作类型：1-新增  2-删除
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/modifyAccountBudgetMap")
	public Map<String, Object> modifyAccountBudgetMap(HttpServletRequest request, String accountSubjId, String financeSubjId, int operateType) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			if (StringUtils.isBlank(accountSubjId)) {
				throw new IllegalArgumentException("请选择要操作的会计科目");
			}
			if (StringUtils.isBlank(financeSubjId)) {
				throw new IllegalArgumentException("请选择要操作的财务科目");
			}
			
			String crewId = getCrewId(request);
			
			AccoFinacSubjMapModel map = this.accoFinacSubjMapService.queryByAccAndFinaSubId(crewId, accountSubjId, financeSubjId);
			if (operateType == 1 && map == null) {
				map = new AccoFinacSubjMapModel();
				map.setId(UUIDUtils.getId());
				map.setCrewId(crewId);
				map.setAccountSubjId(accountSubjId);
				map.setFinanceSubjId(financeSubjId);
				this.accoFinacSubjMapService.addOne(map);
			}
			if (operateType == 2) {
				this.accoFinacSubjMapService.deleteByAccAndFinaSubId(crewId, accountSubjId, financeSubjId);
			}
			
			this.sysLogService.saveSysLog(request, "保存会计科目与财务科目的关联关系", Constants.TERMINAL_PC, AccoFinacSubjMapModel.TABLE_NAME, accountSubjId + "," + financeSubjId, 2);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
			this.sysLogService.saveSysLog(request, "保存会计科目与财务科目的关联关系失败：" + e.getMessage(), Constants.TERMINAL_PC, AccoFinacSubjMapModel.TABLE_NAME, accountSubjId + "," + financeSubjId, 6);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询财务科目列表
	 * 把被其他会计科目占用的财务科目标注为不可用
	 * @param request
	 * @param accountSubjId	
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryAllFinaSubjByAccountId")
	public Map<String, Object> queryAllFinaSubjByAccountId (HttpServletRequest request, String accountSubjId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			String crewId = getCrewId(request);
			
			//所有的财务科目
			List<Map<String, Object>> budgetInfoList = this.financeSubjectService.queryByCrewIdWithAccSubj(crewId);
			
			//该会计科目不可用的财务科目
			List<FinanceSubjectModel> unUseBudgetInfoList = this.financeSubjectService.queryUnusedSubjByAccId(crewId, accountSubjId);
			
			List<String>  unUseBudgetSubjIdList = new ArrayList<String>();
			for (FinanceSubjectModel unUseBudgetInfo : unUseBudgetInfoList) {
				unUseBudgetSubjIdList.add(unUseBudgetInfo.getId());
			}
			
			//格式化数据
			List<Map<String, Object>> budgetSubjMapList = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < budgetInfoList.size(); i++) {
				Map<String, Object> budgetSubj =budgetInfoList.get(i);
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("financeSubjId", budgetSubj.get("id"));
				map.put("financeSubjName", budgetSubj.get("name"));
				map.put("accSubjName", budgetSubj.get("accSubjName"));
				map.put("accSubjCode", budgetSubj.get("accSubjCode"));
				if ((Integer) budgetSubj.get("level") != 1) {
					map.put("_parentId", budgetSubj.get("parentId"));
				}
				
				if (StringUtils.isBlank(accountSubjId) || unUseBudgetSubjIdList.contains(budgetSubj.get("id"))) {
					map.put("canUse", false);
				} else {
					map.put("canUse", true);
				}
				
				budgetSubjMapList.add(map);
			}
			
			resultMap.put("total", budgetInfoList.size());
			resultMap.put("rows", budgetSubjMapList);
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询会计科目下的财务科目
	 * @param request
	 * @param accountSubjId	会计科目ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryOwnFinaSubjByAccountId")
	public Map<String, Object> queryOwnFinaSubjByAccountId(HttpServletRequest request, String accountSubjId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			String crewId = getCrewId(request);
			
			//所有的财务科目
			List<FinanceSubjectModel> financeSubjList = this.financeSubjectService.queryByAccountId(crewId, accountSubjId);
			
			resultMap.put("financeSubjList", financeSubjList);
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 调整会计科目顺序
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/modifyAccountSubjSequence")
	public Map<String, Object> modifyAccountSubjSequence(HttpServletRequest request, String ids) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			this.accountSubjectService.modifyAccountSubjSequence(ids);
			
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询所有会计科目
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryAccSubjList")
	public Map<String, Object> queryAccSubjList(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			String crewId = getCrewId(request);
			
			List<Map<String, Object>> accSubjList = this.accountSubjectService.queryByCrewId(crewId);
			
			resultMap.put("accSubjList", accSubjList);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 查询尚未加入到会计科目中的财务科目数目
	 * 只计算叶子节点的数目
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/countUnSelectedBudgSubj")
	public Map<String, Object> countUnSelectedBudgSubj(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		boolean success = true;
		String message = "";
		try {
			String crewId = getCrewId(request);
			
			List<FinanceSubjectModel> budgetSubjList = this.financeSubjectService.queryUnSelectedBudgSubj(crewId);
			
			/*
    		 * 过滤出叶子节点科目
    		 */
    		List<FinanceSubjectModel> parentSubjectList = new ArrayList<FinanceSubjectModel>();
    		List<FinanceSubjectModel> childSubjectList = new ArrayList<FinanceSubjectModel>();
    		
    		for (FinanceSubjectModel fsubject : budgetSubjList) {
    			String fid = fsubject.getId();
    			String fparentId = fsubject.getParentId();

    			boolean isParent = false;
    			boolean isChild = false;
    			for (FinanceSubjectModel csubject : budgetSubjList) {
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
			
			
			
			int count = leafSubjectList.size();
			
			resultMap.put("count", count);
		} catch (IllegalArgumentException ie) {
			success = false;
			message = ie.getMessage();
		} catch (Exception e) {
			success = false;
			message = "未知异常";
			
			logger.error("未知异常", e);
		}
		
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
}
