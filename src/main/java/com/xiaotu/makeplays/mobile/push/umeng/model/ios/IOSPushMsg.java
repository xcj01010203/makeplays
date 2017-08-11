package com.xiaotu.makeplays.mobile.push.umeng.model.ios;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xiaotu.makeplays.mobile.push.umeng.model.UmengPushCastType;

/**
 * push消息对象
 * @author xuchangjian
 */
public class IOSPushMsg {
	
	/**
	 * 推送的方式枚举值，具体值见UmengPushCastType枚举类
	 * 默认为列播
	 */
	private Integer castType = UmengPushCastType.Listcast.getValue();
	
	/**
	 * 组播时的分组信息
	 * 组播时必要条件
	 */
	private List<String> groupFilterList;
	
	/**
	 * 手机端唯一标识
	 * 单播、列播时的必要条件
	 */
	private List<String> tokenList;

	/**
	 * 手机端推送的消息
	 */
	private String alert;

	/**
	 * 其他信息的
	 */
	private Map<String, Object> customDictionaryMap;

	public IOSPushMsg() {
		
	}

	public Integer getCastType() {
		return this.castType;
	}

	public void setCastType(Integer castType) {
		this.castType = castType;
	}

	public List<String> getGroupFilterList() {
		return this.groupFilterList;
	}

	public void setGroupFilterList(List<String> groupFilterList) {
		this.groupFilterList = groupFilterList;
	}

	public void addCustomDictionary(String key, Object value) {
		if (this.customDictionaryMap == null)
			this.customDictionaryMap = new HashMap<String, Object>();
		this.customDictionaryMap.put(key, value);
	}

	public String getAlert() {
		return alert;
	}

	public void setAlert(String alert) {
		this.alert = alert;
	}

	public Map<String, Object> getCustomDictionaryMap() {
		return customDictionaryMap;
	}

	public void setCustomDictionaryMap(Map<String, Object> customDictionaryMap) {
		this.customDictionaryMap = customDictionaryMap;
	}

	public List<String> getTokenList() {
		return this.tokenList;
	}

	public void setTokenList(List<String> tokenList) {
		this.tokenList = tokenList;
	}
}
