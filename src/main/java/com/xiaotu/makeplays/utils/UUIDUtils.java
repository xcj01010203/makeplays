package com.xiaotu.makeplays.utils;

import java.util.UUID;

public class UUIDUtils {

	public static String getId(){
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	/**
	 * 获取6位随机数字
	 */
	public static String getVerificationCode(){
		return String.valueOf((Math.random()+6)*1000000).substring(1,7);
	}
	
	public static String getEntryStr() {
		return UUID.randomUUID().toString().replace("-", "").substring(0, 6);
	}
}
