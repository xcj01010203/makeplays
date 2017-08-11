package com.xiaotu.makeplays.verifycode.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.MsgUtils;
import com.xiaotu.makeplays.utils.UUIDUtils;
import com.xiaotu.makeplays.verifycode.dao.VerifyCodeInfoDao;
import com.xiaotu.makeplays.verifycode.model.VerifyCodeInfoModel;

/**
 * 验证码
 * @author xuchangjian 2016-10-10上午9:18:52
 */
@Service
public class VerifyCodeInfoService {
	
	@Autowired
	private VerifyCodeInfoDao verifyCodeInfoDao;
	
	/**
	 * 发送验证码
	 * 先判断该手机号是否存在该种类型的有效验证码
	 * 
	 * 如果没有有效验证码，直接生成新的验证码，发送短信
	 * 
	 * 如果存在有效验证码，则判断验证码是否发送超过一分钟
	 * 只有超过一分钟的时候才把原有验证码设为无效，生成新的验证码，发送短信
	 * 如果没有超过一分钟，则不发送短信
	 * @param phone	手机号
	 * @param type 验证码类型	
	 * @return	验证码
	 * @throws Exception 
	 */
	public String sendVerifyCode(String phone, Integer type) throws Exception {
		String code = UUIDUtils.getVerificationCode();
//		String code = "000000";
		
		//查询该手机号该种类型的有效验证码
		VerifyCodeInfoModel existVerifyCode = this.verifyCodeInfoDao.queryVaildCodeByPhoneAndType(phone, type);
		
		boolean needSendNewMsg = false;	//是否需要生成新的验证码，发送短信
		if (existVerifyCode != null) {
			Date lastSendDate = existVerifyCode.getCreateTime();
			if (new Date().getTime() - lastSendDate.getTime() > 1 * 60 * 1000) {
				existVerifyCode.setValid(false);
				this.verifyCodeInfoDao.update(existVerifyCode, "id");
				
				needSendNewMsg = true;
			} else {
				code = existVerifyCode.getCode();
			}
		} else {
			needSendNewMsg = true;
		}
		
		//生成新的验证码，发送手机短信
		if (needSendNewMsg) {
			VerifyCodeInfoModel verifyCode = new VerifyCodeInfoModel();
			verifyCode.setId(UUIDUtils.getId());
			verifyCode.setPhone(phone);
			verifyCode.setCode(code);
			verifyCode.setValid(true);
			verifyCode.setCreateTime(new Date());
			verifyCode.setType(type);
			this.verifyCodeInfoDao.add(verifyCode);
			
			MsgUtils.sendMsg(phone, Constants.VALIDMODEL, new String[]{code, Constants.VALIDTIME});
		}
		
		return code;
	}
	
	/**
	 * 根据手机号和验证码查询有效的验证码
	 * @param phone
	 * @param code
	 * @return
	 * @throws Exception 
	 */
	public VerifyCodeInfoModel queryByPhoneAndCode(String phone, String code, Integer flag) throws Exception {
		return this.verifyCodeInfoDao.queryByPhoneAndCode(phone, code, flag);
	}
	
	/**
	 * 设置指定手机号的所有验证码为无效
	 * @param phone
	 */
	public void inValidPhoneCode(String phone, Integer type) {
		this.verifyCodeInfoDao.inValidPhoneCode(phone, type);
	}

	/**
	 * 查询 该手机号有效的验证码
	 * @param phone
	 * @throws Exception 
	 */
	public VerifyCodeInfoModel queryVaildCodeByPhone(String phone, Integer type) throws Exception  {
		return this.verifyCodeInfoDao.queryVaildCodeByPhoneAndType(phone, type); 
	}
	
	/**
	 * 保存验证码
	 * @param verifyCodeInfoModel
	 * @throws Exception 
	 */
	public void saveValidCode(VerifyCodeInfoModel verifyCodeInfoModel) throws Exception{
		this.verifyCodeInfoDao.add(verifyCodeInfoModel);
	}
	
}
