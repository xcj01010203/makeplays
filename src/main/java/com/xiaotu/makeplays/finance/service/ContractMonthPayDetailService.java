package com.xiaotu.makeplays.finance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.finance.dao.ContractMonthPayDetailDao;

/**
 * 合同按月支付薪酬明细
 * @author xuchangjian 2016-11-17上午11:41:23
 */
@Service
public class ContractMonthPayDetailService {

	@Autowired
	private ContractMonthPayDetailDao contractMonthPayDetailDao;
	
	
}
