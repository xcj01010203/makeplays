package com.xiaotu.makeplays.mobile.push.umeng.service.android;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.mobile.push.umeng.model.UmengPushCastType;
import com.xiaotu.makeplays.mobile.push.umeng.model.android.AndroidAfterOpen;
import com.xiaotu.makeplays.mobile.push.umeng.model.android.AndroidPushDisplayType;
import com.xiaotu.makeplays.mobile.push.umeng.model.android.AndroidPushMsg;
import com.xiaotu.makeplays.utils.PropertiesUitls;

/**
 * 采用第三方平台：友盟，进行消息推送
 * 该种推送可以适用于IOS和Android两种客户端
 * @author xuchangjian
 */
@Service
public class UmengAndroidPushService {
	
	Logger logger = LoggerFactory.getLogger(UmengAndroidPushService.class);

	private String appkey = null;	//应用唯一标识
	private String appMasterSecret = null;
	private String timestamp = null;	//时间戳
	private String published = "false";
	
	public UmengAndroidPushService() {
		try {
			//读取配置文件中的
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String appkey = properties.getProperty("ANDROID_APPKEY");
			String appMasterSecret = properties.getProperty("ANDROID_APP_MASTER_SECRET");
			String published = properties.getProperty("ANDROID_PUBLISHED");
			
			//为通用参数赋值
			
			this.appkey = appkey;
			this.appMasterSecret = appMasterSecret;
			this.published = published;

			//this.timestamp = Integer.toString((int)(System.currentTimeMillis() / 1000));
		} catch (Exception e) {
			logger.error("未知异常，获取推送默认值失败", e);
		}
	}
	
	/**
	 * 推送消息
	 * 
	 * @param msg
	 * @throws Exception
	 */
	public void androidPushMsg(final AndroidPushMsg msg) throws Exception {
//		Map<String, Object> pushResultMap = null;
		String responseStr = "";
		this.timestamp = Integer.toString((int)(System.currentTimeMillis() / 1000));
		
		if (msg.getCastType() == null) {
			throw new IllegalArgumentException("请选择消息推送类型");
		}
		
		List<String> tokenList = msg.getTokenList();
		int tokenListSize = tokenList.size();
		
		//单播
		if (msg.getCastType() == UmengPushCastType.Unicast.getValue()) {
			if (tokenListSize > 0) {
				this.sendAndroidUnicast(msg);
			}
		}
		
		//列播
		if (msg.getCastType() == UmengPushCastType.Listcast.getValue()) {
			if (tokenListSize > 0) {
				//Umeng列播一次推送设备不能超过500，此处做特殊处理
				int multiple = tokenListSize / 500;
				int remainder = tokenListSize % 500;
				for (int i = 0; i < multiple; i++) {
					int endIndex = (i+1) * 500;
					List<String> subTokenList = tokenList.subList(i * 500, endIndex);
					
					AndroidPushMsg subPushMsg = new AndroidPushMsg();
					subPushMsg.setTokenList(subTokenList);
					subPushMsg.setDisplayType(msg.getDisplayType());
					subPushMsg.setTicker(msg.getTicker());
					subPushMsg.setTitle(msg.getTitle());
					subPushMsg.setText(msg.getText());
					subPushMsg.setAfterOpen(msg.getAfterOpen());
					subPushMsg.setUrl(msg.getUrl());
					subPushMsg.setActivity(msg.getActivity());
					subPushMsg.setCustom(msg.getCustom());
					subPushMsg.setDescription(msg.getDescription());
					subPushMsg.setCustomDictionaryMap(msg.getCustomDictionaryMap());
					
					responseStr = this.sendAndroidListcase(subPushMsg);
				}
				List<String> subTokenList = tokenList.subList(tokenListSize - remainder, tokenListSize);
				
				AndroidPushMsg subPushMsg = new AndroidPushMsg();
				subPushMsg.setTokenList(subTokenList);
				subPushMsg.setDisplayType(msg.getDisplayType());
				subPushMsg.setTicker(msg.getTicker());
				subPushMsg.setTitle(msg.getTitle());
				subPushMsg.setText(msg.getText());
				subPushMsg.setAfterOpen(msg.getAfterOpen());
				subPushMsg.setUrl(msg.getUrl());
				subPushMsg.setActivity(msg.getActivity());
				subPushMsg.setCustom(msg.getCustom());
				subPushMsg.setDescription(msg.getDescription());
				subPushMsg.setCustomDictionaryMap(msg.getCustomDictionaryMap());
				responseStr = this.sendAndroidListcase(subPushMsg);
			}
		}
		
		//广播
		if (msg.getCastType() == UmengPushCastType.Broadcast.getValue()) {
			//responseStr = this.sendAndroidBroadcast(msg);
		}
		
		//组播
		if (msg.getCastType() == UmengPushCastType.Groupcast.getValue()) {
			List<String> groupFilterList = msg.getGroupFilterList();
			if (groupFilterList.size() > 0) {
				//responseStr = this.sendAndroidGroupcast(msg);
			}
		}
		
		//自定义
		if (msg.getCastType() == UmengPushCastType.Customizedcast.getValue()) {
			//TODO 暂时不实现
		}
		
		//文件播（需要配合文件上传接口）
		if (msg.getCastType() == UmengPushCastType.Filecast.getValue()) {
			//TODO 暂时不实现
		}
		
		if (!StringUtils.isBlank(responseStr)) {
//			pushResultMap = new HashMap<String, Object>();
			JSONObject responseJson = new JSONObject(responseStr);
			String success = responseJson.getString("ret");
//			if (success.equals("SUCCESS")) {
//				pushResultMap.put("success", true);
//				pushResultMap.put("message", "推送成功");
//			}
			if (success.equals("FAIL")) {
//				pushResultMap.put("success", false);
				JSONObject dataJson = responseJson.getJSONObject("data");
				String errorCode= dataJson.getString("error_code");
//				pushResultMap.put("message", "推送异常，错误码：" + errorCode);
				
				logger.error("推送异常，错误码：" + errorCode);
			}
		}
	}
	
	public String sendAndroidBroadcast(AndroidPushMsg msg) throws Exception {
		String responseStr = "";
		
		//获取推送信息
		Map<String, Object> customDictionaryMap = msg.getCustomDictionaryMap();
		String ticker = msg.getTicker();
		String title = msg.getTitle();
		String text = msg.getText();
		int displayType = msg.getDisplayType();
		
		AndroidBroadcast broadcast = new AndroidBroadcast();
		broadcast.setAppMasterSecret(appMasterSecret);
		broadcast.setPredefinedKeyValue("appkey", this.appkey);
		broadcast.setPredefinedKeyValue("timestamp", this.timestamp);
		
		broadcast.setPredefinedKeyValue("ticker", ticker);
		broadcast.setPredefinedKeyValue("title",  title);
		broadcast.setPredefinedKeyValue("text",   text);
		
		this.setAfterOpenInfo(msg, broadcast);
		
		broadcast.setPredefinedKeyValue("display_type", AndroidPushDisplayType.valueOf(displayType).getName());
		
		broadcast.setPredefinedKeyValue("production_mode", this.published);
		
		//添加其他额外的信息
		if (customDictionaryMap != null) {
			Set<String> customSet = customDictionaryMap.keySet();
			for (String customKey : customSet) {
				String cutomValue = customDictionaryMap.get(customKey).toString();
				broadcast.setExtraField(customKey, cutomValue);
			}
		}
		
		//推送消息
		responseStr = broadcast.send();
		
		return responseStr;
	}
	
	/**
	 * 单播
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	private String sendAndroidUnicast(AndroidPushMsg msg) throws Exception {
		String responseStr = "";
		
		//获取推送信息
		Map<String, Object> customDictionaryMap = msg.getCustomDictionaryMap();
		String token = msg.getTokenList().get(0);
		String ticker = msg.getTicker();
		String title = msg.getTitle();
		String text = msg.getText();
		int displayType = msg.getDisplayType();
		
		//拼接推送请求参数
		AndroidUnicast unicast = new AndroidUnicast();
		unicast.setAppMasterSecret(appMasterSecret);
		unicast.setPredefinedKeyValue("appkey", this.appkey);
		unicast.setPredefinedKeyValue("timestamp", this.timestamp);
		
		unicast.setPredefinedKeyValue("device_tokens", token);
		unicast.setPredefinedKeyValue("ticker", ticker);
		unicast.setPredefinedKeyValue("title",  title);
		unicast.setPredefinedKeyValue("text",   text);
		
		this.setAfterOpenInfo(msg, unicast);

		unicast.setPredefinedKeyValue("display_type", AndroidPushDisplayType.valueOf(displayType).getName());
		unicast.setPredefinedKeyValue("production_mode", this.published);
		
		//添加其他额外的信息
		if (customDictionaryMap != null) {
			Set<String> customSet = customDictionaryMap.keySet();
			for (String customKey : customSet) {
				String cutomValue = customDictionaryMap.get(customKey).toString();
				unicast.setExtraField(customKey, cutomValue);
			}
		}
		
		//推送消息
		responseStr = unicast.send();
		
		return responseStr;
	}
	
	/**
	 * Android列播
	 * @throws Exception
	 */
	private String sendAndroidListcase(AndroidPushMsg msg) throws Exception {
		String responseStr = "";
		
		//获取推送信息
		Map<String, Object> customDictionaryMap = msg.getCustomDictionaryMap();
		List<String> tokenList = msg.getTokenList();
		StringBuilder tokens = new StringBuilder();
		for (String token : tokenList) {
			tokens.append(token + ",");
		}
		String tokenStr = tokens.toString();
		String ticker = msg.getTicker();
		String title = msg.getTitle();
		String text = msg.getText();
		int displayType = msg.getDisplayType();
		String custom = msg.getCustom();
		
		AndroidListcast listcast = new AndroidListcast();
		listcast.setAppMasterSecret(appMasterSecret);
		listcast.setPredefinedKeyValue("appkey", this.appkey);
		listcast.setPredefinedKeyValue("timestamp", this.timestamp);
		
		listcast.setPredefinedKeyValue("device_tokens", tokenStr.substring(0, tokenStr.length() - 1));
		listcast.setPredefinedKeyValue("ticker", ticker);
		listcast.setPredefinedKeyValue("title",  title);
		listcast.setPredefinedKeyValue("text",   text);
		
		this.setAfterOpenInfo(msg, listcast);
		if (displayType == AndroidPushDisplayType.Message.getValue()) {
			if (StringUtils.isBlank(custom)) {
				throw new IllegalArgumentException("请提供custom");
			}
			listcast.setPredefinedKeyValue("custom", msg.getCustom());
		}
		listcast.setPredefinedKeyValue("display_type", AndroidPushDisplayType.valueOf(displayType).getName());
		listcast.setPredefinedKeyValue("production_mode", this.published);
		
		//添加额外的信息
		//添加其他额外的信息
		if (customDictionaryMap != null) {
			Set<String> customSet = customDictionaryMap.keySet();
			for (String customKey : customSet) {
				String cutomValue = customDictionaryMap.get(customKey).toString();
				listcast.setExtraField(customKey, cutomValue);
			}
		}
		
		responseStr = listcast.send();
		
		return responseStr;
	}
	
	/**
	 *组播
	 * @return
	 * @throws Exception
	 */
	public String sendAndroidGroupcast(AndroidPushMsg msg) throws Exception {
		String responseStr = "";
		
		//获取推送信息
		Map<String, Object> customDictionaryMap = msg.getCustomDictionaryMap();
		List<String> groupFilterList = msg.getGroupFilterList();
		String ticker = msg.getTicker();
		String title = msg.getTitle();
		String text = msg.getText();
		int displayType = msg.getDisplayType();
		
		AndroidGroupcast groupcast = new AndroidGroupcast();
		groupcast.setAppMasterSecret(appMasterSecret);
		groupcast.setPredefinedKeyValue("appkey", this.appkey);
		groupcast.setPredefinedKeyValue("timestamp", this.timestamp);
		
		/*
		 * 拼接过滤条件
		 */
		JSONObject filterJson = new JSONObject();
		JSONObject whereJson = new JSONObject();
		JSONArray andTagArray = new JSONArray();
		
		//拼接or里面的条件
		JSONArray orTagArray = new JSONArray();
		for (String groupFilter : groupFilterList) {
			JSONObject orTagJson = new JSONObject();
			orTagJson.put("tag", groupFilter);
			orTagArray.put(orTagJson);
		}
		
		//拼接and里面的条件
		JSONObject andTagJson = new JSONObject();
		andTagJson.put("or", orTagArray);
		andTagArray.put(andTagJson);
		whereJson.put("and", andTagArray);
		//把所有添加放到where中
		filterJson.put("where", whereJson);
		System.out.println(filterJson.toString());
		
		groupcast.setPredefinedKeyValue("filter", filterJson);
		groupcast.setPredefinedKeyValue("ticker", ticker);
		groupcast.setPredefinedKeyValue("title", title);
		groupcast.setPredefinedKeyValue("text", text);
		
		this.setAfterOpenInfo(msg, groupcast);
		
		groupcast.setPredefinedKeyValue("display_type", AndroidPushDisplayType.valueOf(displayType).getName());
		groupcast.setPredefinedKeyValue("production_mode", "true");
		
		//添加其他额外的信息
		if (customDictionaryMap != null) {
			Set<String> customSet = customDictionaryMap.keySet();
			for (String customKey : customSet) {
				String cutomValue = customDictionaryMap.get(customKey).toString();
				groupcast.setExtraField(customKey, cutomValue);
			}
		}
		
		//推送消息
		responseStr = groupcast.send();
		
		return responseStr;
	}
	
	public String sendAndroidCustomizedcast() throws Exception {
		String responseStr = "";
		
		AndroidCustomizedcast customizedcast = new AndroidCustomizedcast();
		customizedcast.setAppMasterSecret(appMasterSecret);
		customizedcast.setPredefinedKeyValue("appkey", this.appkey);
		customizedcast.setPredefinedKeyValue("timestamp", this.timestamp);
		// TODO Set your alias here, and use comma to split them if there are multiple alias.
		// And if you have many alias, you can also upload a file containing these alias, then 
		// use file_id to send customized notification.
		customizedcast.setPredefinedKeyValue("alias", "xx");
		// TODO Set your alias_type here
		customizedcast.setPredefinedKeyValue("alias_type", "xx");
		customizedcast.setPredefinedKeyValue("ticker", "Android customizedcast ticker");
		customizedcast.setPredefinedKeyValue("title",  "中文的title");
		customizedcast.setPredefinedKeyValue("text",   "Android customizedcast text");
		customizedcast.setPredefinedKeyValue("after_open", "go_app");
		customizedcast.setPredefinedKeyValue("display_type", "notification");
		// TODO Set 'production_mode' to 'false' if it's a test device. 
		// For how to register a test device, please see the developer doc.
		customizedcast.setPredefinedKeyValue("production_mode", "true");
		responseStr = customizedcast.send();
		
		return responseStr;
	}
	
	public String sendAndroidFilecast() throws Exception {
		String responseStr = "";
		
		AndroidFilecast filecast = new AndroidFilecast();
		filecast.setAppMasterSecret(appMasterSecret);
		filecast.setPredefinedKeyValue("appkey", this.appkey);
		filecast.setPredefinedKeyValue("timestamp", this.timestamp);
		// TODO upload your device tokens, and use '\n' to split them if there are multiple tokens 
		filecast.uploadContents("aa"+"\n"+"bb");
		filecast.setPredefinedKeyValue("ticker", "Android filecast ticker");
		filecast.setPredefinedKeyValue("title",  "中文的title");
		filecast.setPredefinedKeyValue("text",   "Android filecast text");
		filecast.setPredefinedKeyValue("after_open", "go_app");
		filecast.setPredefinedKeyValue("display_type", "notification");
		responseStr = filecast.send();
		
		return responseStr;
	}
	
	/**
	 * 设置推送消息的after_open属性
	 * @param msg
	 * @param notification
	 * @throws Exception
	 */
	private void setAfterOpenInfo(AndroidPushMsg msg, AndroidNotification notification) throws Exception {
		switch (msg.getAfterOpen()) {
		case 1: 
			notification.setPredefinedKeyValue("after_open", AndroidAfterOpen.GoApp.getName());
			break;
		case 2:
			notification.setPredefinedKeyValue("after_open", AndroidAfterOpen.GoUrl.getName());
			if (StringUtils.isBlank(msg.getUrl())) {
				throw new IllegalArgumentException("请提供URL");
			}
			notification.setPredefinedKeyValue("url", msg.getUrl());
			break;
		case 3:
			notification.setPredefinedKeyValue("after_open", AndroidAfterOpen.GoActivity.getName());
			if (StringUtils.isBlank(msg.getActivity())) {
				throw new IllegalArgumentException("请提供Activity");
			}
			notification.setPredefinedKeyValue("activity", msg.getActivity());
			break;
		case 4:
			notification.setPredefinedKeyValue("after_open", AndroidAfterOpen.GoCustom.getName());
			if (StringUtils.isBlank(msg.getCustom())) {
				throw new IllegalArgumentException("请提供Custom");
			}
			notification.setPredefinedKeyValue("custom", msg.getCustom());
			break;
		default:
			break;
		}
	}
}
