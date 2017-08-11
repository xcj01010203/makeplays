package com.xiaotu.makeplays.scenario.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.scenario.dao.BookMarkDao;
import com.xiaotu.makeplays.scenario.model.BookMarkModel;
import com.xiaotu.makeplays.utils.Page;

/**
 * 书签
 * @author xuchangjian
 */
@Service
public class BookMarkService {

	@Autowired
	private BookMarkDao bookMarkDao;
	
	/**
	 * 保存书签信息
	 * @param bookMark
	 * @throws Exception 
	 */
	public void saveBookMarkInfo(BookMarkModel bookMark) throws Exception {
		this.bookMarkDao.add(bookMark);
	}
	
	/**
	 * 根据多个条件查询书签信息
	 * @param conditionMap	查询条件:key--查询条件在数据库中的字段名  value--查询条件的值
	 * @param page 分页信息
	 * @return
	 */
	public List<BookMarkModel> queryManyByMutiCondition(Map<String, Object> conditionMap, Page page) {
		return this.bookMarkDao.queryManyByMutiCondition(conditionMap, page);
	}
}
