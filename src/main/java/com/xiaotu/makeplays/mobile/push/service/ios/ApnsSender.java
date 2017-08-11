package com.xiaotu.makeplays.mobile.push.service.ios;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;

import com.xiaotu.makeplays.utils.PropertiesUitls;

/**
 * 发送消息工具类
 * @author xuchangjian
 */
public class ApnsSender {
	
	private static Properties properties;

	/**
	 * 是否为发布版本
	 */
	private static Boolean produce_published;

	/**
	 * 证书存储路径
	 */
	private static String certificatePath;

	/**
	 * 证书密码
	 */
	private static String certificatePwd;

	static {
		if (properties == null) {
			try {
				properties = PropertiesUitls
						.fetchProperties("/config.properties");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (produce_published == null) {
			produce_published = Boolean.parseBoolean((String) properties.get("PRODUCT_PUBLISHED"));
		}
		if (certificatePath == null) {
			certificatePath = properties.getProperty("CERTIFICATE_PATH");
		}
		if (certificatePwd == null) {
			certificatePwd = properties.getProperty("CERTIFICATE_PWD");
		}
	}
	
	private static PushNotificationPayload getPayLoad(String alert, Map<String, Object> customDictionaryMap)
			throws Exception {
		PushNotificationPayload payLoad = new PushNotificationPayload();
		payLoad.addAlert(alert); // 消息内容
		payLoad.addBadge(1); // iphone应用图标上小红圈上的数值

		if (customDictionaryMap != null) {
			Iterator<String> keyIt = customDictionaryMap.keySet().iterator();
			while (keyIt.hasNext()) {
				String key = (String) keyIt.next();
				payLoad.addCustomDictionary(key, customDictionaryMap.get(key));
			}
		}
		return payLoad;
	}

	public static PushNotificationManager openConnection() throws Exception {
		PushNotificationManager pushManager = new PushNotificationManager();
		pushManager.initializeConnection(new AppleNotificationServerBasicImpl(
				certificatePath, certificatePwd, produce_published));
		return pushManager;
	}

	public static boolean sendMsg(PushNotificationManager pushManager, String token, String alert, Map<String, Object> customDictionaryMap) throws Exception {
		// 发送push消息
		Device device = new BasicDevice();
		device.setToken(token);
		PushedNotification notification;
		if (produce_published)
			notification = pushManager.sendNotification(device,
					getPayLoad(alert, customDictionaryMap), produce_published);
		else
			notification = pushManager
					.sendNotification(device, getPayLoad(alert, customDictionaryMap));
		return notification.isSuccessful();
	}

	public static void closeConnection(PushNotificationManager pushManager)
			throws Exception {
		if (pushManager != null)
			pushManager.stopConnection();
	}
}
