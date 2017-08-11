package com.xiaotu.makeplays.verifycode.dao;

import org.springframework.stereotype.Repository;

import com.xiaotu.makeplays.utils.BaseDao;
import com.xiaotu.makeplays.verifycode.model.VerifyCodeInfoModel;

/**
 * 验证码
 * @author xuchangjian 2016-10-9下午7:02:06
 */
@Repository
public class VerifyCodeInfoDao extends BaseDao<VerifyCodeInfoModel> {
	
	/**
	 * 根据手机号和验证码查询有效的验证码
	 * @param phone
	 * @param code
	 * @return
	 * @throws Exception 
	 */
	public VerifyCodeInfoModel queryByPhoneAndCode(String phone, String code, Integer type) throws Exception {
		String sql = "select * from tab_verifyCode_info where phone = ? and code = ? and valid = 1 and type = ?";
		return this.queryForObject(sql, new Object[] {phone, code, type}, VerifyCodeInfoModel.class);
	}
	
	/**
	 * 设置指定手机号的所有验证码为无效
	 * @param phone 手机号
	 * @param type 验证码类型
	 */
	public void inValidPhoneCode(String phone, Integer type) {
		String sql = "update tab_verifyCode_info set valid = 0 where phone = ? and type = ? and valid = 1";
		this.getJdbcTemplate().update(sql, new Object[] {phone, type});
	}
	
	/**
	 * 根据手机号查询有效的验证码
	 * @param phone 手机号
	 * @param type 验证码类型
	 * @return
	 * @throws Exception 
	 */
	public VerifyCodeInfoModel queryVaildCodeByPhoneAndType(String phone, Integer type) throws Exception{
		String sql = "select * from tab_verifyCode_info where phone = ?  and valid = 1 and type = ? ";
		return this.queryForObject(sql, new Object[] {phone, type}, VerifyCodeInfoModel.class);
	}
}
