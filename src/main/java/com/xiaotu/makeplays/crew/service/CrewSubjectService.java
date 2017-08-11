package com.xiaotu.makeplays.crew.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.crew.dao.CrewSubjectDao;
import com.xiaotu.makeplays.crew.model.CrewSubjectModel;

/**
 * 剧组题材
 * @author xuchangjian 2016-12-15上午10:19:19
 */
@Service
public class CrewSubjectService {

	@Autowired
	private CrewSubjectDao crewSubjectDao;
	
	/**
	 * 查询题材列表
	 * @return
	 */
	public List<CrewSubjectModel> querySubjectList () {
		return this.crewSubjectDao.querySubjectList();
	}
}
