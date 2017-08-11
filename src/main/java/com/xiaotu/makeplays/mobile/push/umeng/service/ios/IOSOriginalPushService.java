package com.xiaotu.makeplays.mobile.push.umeng.service.ios;

import java.util.List;

import javapns.notification.PushNotificationManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.mobile.push.service.ios.ApnsSender;
import com.xiaotu.makeplays.mobile.push.umeng.model.ios.IOSPushMsg;

/**
 * push消息Service
 * 该类中推送采用直接向IOS服务器发送推送请求的方式
 * 该种推送只适用于IOS客户端
 * @author xuchangjian
 */
@Service
public class IOSOriginalPushService {
	private static final Logger logger = LoggerFactory
			.getLogger(IOSOriginalPushService.class);

	/**
	 * 推送消息
	 * 
	 * @param msg
	 * @throws Exception
	 */
	public void iOSPushMsg(final IOSPushMsg msg) throws Exception {

		if (msg != null && msg.getTokenList() != null && msg.getTokenList().size() > 0) {
			final PushNotificationManager pushManager = ApnsSender.openConnection();
			try {
				List<String> tokenList = msg.getTokenList();
				for (final String token : tokenList) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								logger.info("开始给" + token + "发送消息："
										+ msg.getAlert());
								boolean success = ApnsSender.sendMsg(
										pushManager, token, msg.getAlert(),
										msg.getCustomDictionaryMap());
								if (success) {
									logger.info("给" + token + "发送消息成功");
								} else {
									logger.info("给" + token + "发送消息失败");
								}

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).start();
				}

			} catch (Exception e) {
				throw e;
			} finally {
				ApnsSender.closeConnection(pushManager);
				logger.info("连接结束");
			}
		}
	}
}
