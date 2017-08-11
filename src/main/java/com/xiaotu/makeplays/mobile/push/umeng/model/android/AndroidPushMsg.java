package com.xiaotu.makeplays.mobile.push.umeng.model.android;

import java.util.List;
import java.util.Map;

import com.xiaotu.makeplays.mobile.push.umeng.model.UmengPushCastType;

/**
 * Android推送消息字段
 * @author xuchangjian
 */
public class AndroidPushMsg {

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
	 * 消息类型,1代表通知（notification-通知），2代表消息（message-消息）
	 * 默认为notification
	 */
	private int displayType = 1;
	
	/**
	 * 通知栏提示文字
	 * displayType=2时不必填
	 * displayType=1时必填
	 */
	private String ticker;
	
	/**
	 * 通知栏标题
	 * displayType=2时不必填
	 * displayType=1时必填
	 */
	private String title;
	
	/**
	 * 通知文字描述
	 */
	private String text;
	
	/**
	 * 点击通知的后续行为
	 * displayType=1时必填
	 * 详细值建枚举类AndroidAfterOpen
	 */
	private int afterOpen = 1;
	
	/**
	 * 通知栏点击后跳转的URL，要求以http或者https开头
	 * 当"after_open"为"go_url"时，必填
	 */
	private String url;
	
	/**
	 * 通知栏点击后打开的Activity
	 * "after_open"为"go_activity"时，必填
	 */
	private String activity;
	
	/**
	 * 用户自定义内容, 可以为字符串或者JSON格式。
	 * display_type=message, 或者display_type=notification且"after_open"为"go_custom"时，该字段必填。
	 */
	private String custom;
	
	/**
	 * 发送消息描述，建议填写。
	 */
	private String description;

	/**
	 * 其他信息的
	 */
	private Map<String, Object> customDictionaryMap;

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
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

	public List<String> getTokenList() {
		return this.tokenList;
	}

	public void setTokenList(List<String> tokenList) {
		this.tokenList = tokenList;
	}

	public int getDisplayType() {
		return this.displayType;
	}

	public void setDisplayType(int displayType) {
		this.displayType = displayType;
	}

	public String getTicker() {
		return this.ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getAfterOpen() {
		return this.afterOpen;
	}

	public void setAfterOpen(int afterOpen) {
		this.afterOpen = afterOpen;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getActivity() {
		return this.activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getCustom() {
		return this.custom;
	}

	public void setCustom(String custom) {
		this.custom = custom;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, Object> getCustomDictionaryMap() {
		return this.customDictionaryMap;
	}

	public void setCustomDictionaryMap(Map<String, Object> customDictionaryMap) {
		this.customDictionaryMap = customDictionaryMap;
	}
}
