package com.xiaotu.makeplays.mobile.server.finance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.finance.controller.dto.FinanceSubjectDto;
import com.xiaotu.makeplays.finance.service.FinanceSubjectService;
import com.xiaotu.makeplays.mobile.common.utils.MobileUtils;

/**
 * 财务科目相关接口
 * @author xuchangjian 2016-10-12上午9:51:25
 */
@Controller
@RequestMapping("/interface/financeSubjectFacade")
public class FinanceSubjectFacade {

	Logger logger = LoggerFactory.getLogger(FinanceFacade.class);
	
	@Autowired
	private FinanceSubjectService financeSubjectService;
	
	/**
	 * 搜索财务科目
	 * @param crewId
	 * @param userId
	 * @param keyword
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/searchFinanceSubject")
	public Object searchFinanceSubject(String crewId, String userId, String keyword) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			MobileUtils.checkCrewUserValid(crewId, userId);
			
			if (StringUtils.isBlank(keyword)) {
				throw new IllegalArgumentException("请提供搜索关键字");
			}
			
			List<FinanceSubjectDto> subjectDtoList = this.financeSubjectService.refreshCachedSubjectList(crewId);
            
            /*
    		 * 过滤出叶子节点科目
    		 */
    		List<FinanceSubjectDto> parentSubjectList = new ArrayList<FinanceSubjectDto>();
    		List<FinanceSubjectDto> childSubjectList = new ArrayList<FinanceSubjectDto>();
    		
    		for (FinanceSubjectDto fsubject : subjectDtoList) {
    			String fid = fsubject.getId();
    			String fparentId = fsubject.getParentId();

    			boolean isParent = false;
    			boolean isChild = false;
    			for (FinanceSubjectDto csubject : subjectDtoList) {
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
    		List<FinanceSubjectDto> leafSubjectList = new ArrayList<FinanceSubjectDto>();
    		
    		for (FinanceSubjectDto csubject : childSubjectList) {
    			String cid = csubject.getId();
    			boolean exist = false;
    			for (FinanceSubjectDto fsubject : parentSubjectList) {
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
    		
            //搜索
            List<Map<String, Object>> searchResult = new ArrayList<Map<String, Object>>();
            for (FinanceSubjectDto subject : leafSubjectList) {
            	Map<String, Object> subjectMap = new HashMap<String, Object>();
            	if (subject.getName().indexOf(keyword) != -1) {
            		subjectMap.put("financeSubjId", subject.getId());
            		subjectMap.put("financeSubjName", subject.getName());
            		
            		searchResult.add(subjectMap);
            	}
            }
            
            resultMap.put("financeSubjectList", searchResult);
		} catch (IllegalArgumentException ie) {
			logger.error(ie.getMessage(), ie);
			throw new IllegalArgumentException(ie.getMessage(), ie);
		} catch (Exception e) {
			logger.error("未知异常", e);
			throw new IllegalArgumentException("未知异常", e);
		}

		return resultMap;
	}
}
