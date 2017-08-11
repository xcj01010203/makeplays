package com.xiaotu.makeplays.finance.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.finance.model.ContractStagePayWayModel;
import com.xiaotu.makeplays.utils.BaseDao;

/**
 * 合同按阶段支付方式
 * @author xuchangjian 2016-8-13下午5:16:33
 */
@Repository
public class ContractStagePayWayDao extends BaseDao<ContractStagePayWayModel> {
	
	/**
	 * 获取合同与付款方式关联
	 * @param contractId
	 * @param crewId
	 * @return
	 */
	public List<ContractStagePayWayModel> queryByContractId(String contractId, String crewId) {
		String sql = "SELECT * FROM " + ContractStagePayWayModel.TABLE_NAME + " tcpw  WHERE tcpw.contractId = ? AND tcpw.crewId= ? " +" ORDER BY tcpw.stage ASC, tcpw.createTime ASC;";
		return this.query(sql, new Object[] {contractId, crewId}, ContractStagePayWayModel.class, null);
	}
	
	/**
	 * 删除合同与付款方式关联
	 */
	public void deleteByContractId(String contractId, String crewId) {
		String sql = "delete from  " + ContractStagePayWayModel.TABLE_NAME + " where contractId = ? and crewId = ? ";
		this.getJdbcTemplate().update(sql, new Object[] {contractId, crewId});
	}
}
