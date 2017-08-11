package com.xiaotu.makeplays.mobile.push.umeng.service.ios;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.message.service.MessageInfoService;
import com.xiaotu.makeplays.mobile.push.umeng.model.UmengPushCastType;
import com.xiaotu.makeplays.mobile.push.umeng.model.ios.IOSPushMsg;
import com.xiaotu.makeplays.sys.service.SysLogService;
import com.xiaotu.makeplays.utils.PropertiesUitls;

/**
 * 采用第三方平台：友盟，进行消息推送
 * 该种推送可以适用于IOS和Android两种客户端
 * @author xuchangjian
 */
@Service
public class UmengIOSPushService {
	
	Logger logger = LoggerFactory.getLogger(UmengIOSPushService.class);
	
	@Autowired
	private SysLogService sysLogService;

	private String appkey = null;	//应用唯一标识
	private String appMasterSecret = null;
	private String timestamp = null;	//时间戳
	private String published = "false";	//是否用于生产，若为false，则表示只用于测试机器
	
	public UmengIOSPushService() {
		try {
			//读取配置文件中的
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String appkey = properties.getProperty("IOS_APPKEY");
			String appMasterSecret = properties.getProperty("IOS_APP_MASTER_SECRET");
			String published = properties.getProperty("IOS_PUBLISHED");
			
			//为通用参数赋值
			this.appkey = appkey;
			this.appMasterSecret = appMasterSecret;
			this.published = published;
		} catch (Exception e) {
			logger.error("未知异常，获取推送参数失败", e);
		}
	}
	
	/**
	 * 推送消息
	 * @param msg
	 * @return 返回消息推送结果map，当key为success时，值为true或false；当key为message时，值表示推送失败的原因
	 * @throws Exception
	 */
	public void iOSPushMsg(final IOSPushMsg msg) throws Exception {
		String responseStr = "";
		
		if (msg.getCastType() == null) {
			throw new IllegalArgumentException("请选择消息推送类型");
		}
		
		this.timestamp = Integer.toString((int)(System.currentTimeMillis() / 1000));
		
		List<String> tokenList = msg.getTokenList();
		int tokenListSize = tokenList.size();
		
		if (msg.getCastType() == UmengPushCastType.Unicast.getValue()) {
			if (tokenListSize > 0) {
				this.sendIOSUnicast(msg);
			}
		}
		if (msg.getCastType() == UmengPushCastType.Listcast.getValue()) {
			if (tokenListSize > 0) {
				//Umeng列播一次推送设备不能超过500，此处做特殊处理
				int multiple = tokenListSize / 500;
				int remainder = tokenListSize % 500;
				for (int i = 0; i < multiple; i++) {
					int endIndex = (i+1) * 500;
					List<String> subTokenList = tokenList.subList(i * 500, endIndex);
					
					IOSPushMsg subPushMsg = new IOSPushMsg();
					subPushMsg.setTokenList(subTokenList);
					subPushMsg.setAlert(msg.getAlert());
					subPushMsg.setCustomDictionaryMap(msg.getCustomDictionaryMap());
					responseStr = this.sendIosListcase(subPushMsg);
				}
				List<String> subTokenList = tokenList.subList(tokenListSize - remainder, tokenListSize);
				
				IOSPushMsg subPushMsg = new IOSPushMsg();
				subPushMsg.setTokenList(subTokenList);
				subPushMsg.setAlert(msg.getAlert());
				subPushMsg.setCustomDictionaryMap(msg.getCustomDictionaryMap());
				responseStr = this.sendIosListcase(subPushMsg);
			}
		}
		if (msg.getCastType() == UmengPushCastType.Broadcast.getValue()) {
			responseStr = this.sendIosBroadcast(msg);
		}
		if (msg.getCastType() == UmengPushCastType.Groupcast.getValue()) {
			List<String> groupFilterList = msg.getGroupFilterList();
			if (groupFilterList.size() > 0) {
				responseStr = this.sendIosGroupcast(msg);
			}
		}
		if (msg.getCastType() == UmengPushCastType.Customizedcast.getValue()) {
			//TODO 暂时不实现
		}
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
	
	/**
	 * ios广播
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	private String sendIosBroadcast(IOSPushMsg msg) throws Exception {
		String responseStr = "";
		
		Map<String, Object> customDictionaryMap = msg.getCustomDictionaryMap();
		String alert = msg.getAlert();
		
		IOSBroadcast broadcast = new IOSBroadcast();
		broadcast.setAppMasterSecret(appMasterSecret);
		broadcast.setPredefinedKeyValue("appkey", this.appkey);
		broadcast.setPredefinedKeyValue("timestamp", this.timestamp);

		broadcast.setPredefinedKeyValue("alert", alert);
		broadcast.setPredefinedKeyValue("badge", 0);
		broadcast.setPredefinedKeyValue("sound", "");
		
		broadcast.setPredefinedKeyValue("production_mode", this.published);
		
		//添加其他额外的信息
		if (customDictionaryMap != null) {
			Set<String> customSet = customDictionaryMap.keySet();
			for (String customKey : customSet) {
				String cutomValue = customDictionaryMap.get(customKey).toString();
				broadcast.setCustomizedField(customKey, cutomValue);
			}
		}
		
		//推送消息
		responseStr = broadcast.send();
		
		return responseStr;
	}
	
	/**
	 * IOS单播
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	private String sendIOSUnicast(IOSPushMsg msg) throws Exception {
		String responseStr = "";
		
		//获取推送信息
		String token = msg.getTokenList().get(0);
		Map<String, Object> customDictionaryMap = msg.getCustomDictionaryMap();
		String alert = msg.getAlert();
		
		//拼接推送请求参数
		IOSUnicast unicast = new IOSUnicast();
		unicast.setAppMasterSecret(appMasterSecret);
		unicast.setPredefinedKeyValue("appkey", this.appkey);
		unicast.setPredefinedKeyValue("timestamp", this.timestamp);
		
		unicast.setPredefinedKeyValue("device_tokens", token);
		unicast.setPredefinedKeyValue("alert", alert);
		unicast.setPredefinedKeyValue("badge", 0);
		unicast.setPredefinedKeyValue("sound", "");
		
		unicast.setPredefinedKeyValue("production_mode", this.published);

		//添加其他额外的信息
		if (customDictionaryMap != null) {
			Set<String> customSet = customDictionaryMap.keySet();
			for (String customKey : customSet) {
				String cutomValue = customDictionaryMap.get(customKey).toString();
				unicast.setCustomizedField(customKey, cutomValue);
			}
		}
		
		//推送消息
		responseStr = unicast.send();
		
		return responseStr;
	}
	
	/**
	 * IOS列播
	 * @throws Exception
	 */
	private String sendIosListcase(IOSPushMsg msg) throws Exception {
		String responseStr = "";
		
		//获取推送信息
		StringBuilder tokens = new StringBuilder();
		List<String> tokenList = msg.getTokenList();
		for (String token : tokenList) {
			tokens.append(token + ",");
		}
		String tokenStr = tokens.toString();
		
		String alert = msg.getAlert();
		Map<String, Object> customDictionaryMap = msg.getCustomDictionaryMap();
		
		//拼接推送请求参数
		IOSListcast listcast = new IOSListcast();
		listcast.setAppMasterSecret(appMasterSecret);
		listcast.setPredefinedKeyValue("appkey", this.appkey);
		listcast.setPredefinedKeyValue("timestamp", this.timestamp);
		
		listcast.setPredefinedKeyValue("device_tokens", tokenStr.subSequence(0, tokenStr.length() - 1));
		listcast.setPredefinedKeyValue("alert", alert);
		listcast.setPredefinedKeyValue("badge", 0);
		listcast.setPredefinedKeyValue("sound", "");
		
		listcast.setPredefinedKeyValue("production_mode", this.published);
		
		//添加其他额外的信息
		if (customDictionaryMap != null) {
			Set<String> customSet = customDictionaryMap.keySet();
			for (String customKey : customSet) {
				String cutomValue = customDictionaryMap.get(customKey).toString();
				listcast.setCustomizedField(customKey, cutomValue);
			}
		}
		
		//推送消息
		responseStr = listcast.send();
		
		return responseStr;
	}
	
	/**
	 * ios组播
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	private String sendIosGroupcast(IOSPushMsg msg) throws Exception {
		String responseStr = "";

		//获取推送信息
		List<String> groupFilterList = msg.getGroupFilterList();
		Map<String, Object> customDictionaryMap = msg.getCustomDictionaryMap();
		String alert = msg.getAlert();
		
		//拼接推送请求参数
		IOSGroupcast groupcast = new IOSGroupcast();
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
		
		// Set filter condition into rootJson
		groupcast.setPredefinedKeyValue("filter", filterJson);
		groupcast.setPredefinedKeyValue("alert", alert);
		groupcast.setPredefinedKeyValue("badge", 0);
		groupcast.setPredefinedKeyValue("sound", "");
		
		groupcast.setPredefinedKeyValue("production_mode", this.published);
		
		//添加其他额外的信息
		if (customDictionaryMap != null) {
			Set<String> customSet = customDictionaryMap.keySet();
			for (String customKey : customSet) {
				String cutomValue = customDictionaryMap.get(customKey).toString();
				groupcast.setCustomizedField(customKey, cutomValue);
			}
		}
		
		//推送消息
		responseStr = groupcast.send();
		
		return responseStr;
	}
	
	/**
	 * ios个性化推送 TODO
	 * @return
	 * @throws Exception
	 */
	private String sendIOSCustomizedcast() throws Exception {
		String responseStr = "";
		
		IOSCustomizedcast customizedcast = new IOSCustomizedcast();
		customizedcast.setAppMasterSecret(appMasterSecret);
		customizedcast.setPredefinedKeyValue("appkey", this.appkey);
		customizedcast.setPredefinedKeyValue("timestamp", this.timestamp);
		// TODO Set your alias here, and use comma to split them if there are multiple alias.
		// And if you have many alias, you can also upload a file containing these alias, then 
		// use file_id to send customized notification.
		customizedcast.setPredefinedKeyValue("alias", "xx");
		// TODO Set your alias_type here
		customizedcast.setPredefinedKeyValue("alias_type", "xx");
		customizedcast.setPredefinedKeyValue("alert", "IOS 个性化测试");
		customizedcast.setPredefinedKeyValue("badge", 0);
		customizedcast.setPredefinedKeyValue("sound", "chime");
		// TODO set 'production_mode' to 'true' if your app is under production mode
		customizedcast.setPredefinedKeyValue("production_mode", "false");
		responseStr = customizedcast.send();
		
		return responseStr;
	}
	
	/**
	 * ios文件推送 TODO
	 * @return
	 * @throws Exception
	 */
	private String sendIOSFilecast() throws Exception {
		String responseStr = "";
		
		IOSFilecast filecast = new IOSFilecast();
		filecast.setAppMasterSecret(appMasterSecret);
		filecast.setPredefinedKeyValue("appkey", this.appkey);
		filecast.setPredefinedKeyValue("timestamp", this.timestamp);
		// TODO upload your device tokens, and use '\n' to split them if there are multiple tokens 
		filecast.uploadContents("aa"+"\n"+"bb");
		filecast.setPredefinedKeyValue("alert", "IOS 文件播测试");
		filecast.setPredefinedKeyValue("badge", 0);
		filecast.setPredefinedKeyValue("sound", "chime");
		// TODO set 'production_mode' to 'true' if your app is under production mode
		filecast.setPredefinedKeyValue("production_mode", "false");
		responseStr = filecast.send();
		
		return responseStr;
	}
}
