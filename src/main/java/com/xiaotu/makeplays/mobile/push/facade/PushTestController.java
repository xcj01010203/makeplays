package com.xiaotu.makeplays.mobile.push.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaotu.makeplays.mobile.push.umeng.model.android.AndroidPushDisplayType;
import com.xiaotu.makeplays.mobile.push.umeng.model.android.AndroidPushMsg;
import com.xiaotu.makeplays.mobile.push.umeng.model.ios.IOSPushMsg;
import com.xiaotu.makeplays.mobile.push.umeng.service.android.UmengAndroidPushService;
import com.xiaotu.makeplays.mobile.push.umeng.service.ios.IOSOriginalPushService;
import com.xiaotu.makeplays.mobile.push.umeng.service.ios.UmengIOSPushService;

/**
 * @类名 PushTestController
 * @日期 2015年7月6日
 * @作者 高海军
 * @功能
 */
@Controller
@RequestMapping("/pushTest")
public class PushTestController {
	@Autowired
	private IOSOriginalPushService iOSOriginalPushService;
	
	@Autowired
	private UmengAndroidPushService umengAndroidPushService;
	
	@Autowired
	private UmengIOSPushService umengIOSPushService;

	@ResponseBody
	@RequestMapping("/pushTest")
	public Object pushTest(int castType, Integer afteropen) {
		try {
			List<String> userList = new ArrayList<String>();
			userList.add("d7e6132895b388cf016433167c9e2d97fe4b76ca5a1692209a3b6e3cb3fdcd9c");
			userList.add("2");
			String message = "hello world";
			
			Map<String, Object> customDictionaryMap  = new HashMap<String, Object>();
			customDictionaryMap.put("key1", "value1");
			customDictionaryMap.put("key2", "value2");
			customDictionaryMap.put("key3", "value3");
			customDictionaryMap.put("key4", "value4");
			
			List<String> groupFilterList = new ArrayList<String>();
			groupFilterList.add("asdfasdfasdfasdfsd");
			groupFilterList.add("12323423423423");
			
			IOSPushMsg msg = new IOSPushMsg();
			msg.setCastType(castType);
			msg.setGroupFilterList(groupFilterList);
			msg.setTokenList(userList);
			msg.setAlert(message);
			msg.setCustomDictionaryMap(customDictionaryMap);
			this.umengIOSPushService.iOSPushMsg(msg);
			
			
			AndroidPushMsg androidMsg = new AndroidPushMsg();
			androidMsg.setCastType(castType);
			
			androidMsg.setGroupFilterList(groupFilterList);
			
			androidMsg.setTokenList(userList);
			
			androidMsg.setDisplayType(AndroidPushDisplayType.Notification.getValue());
			androidMsg.setTicker("通知栏提示文字");
			androidMsg.setTitle("通知栏标题");
			androidMsg.setText("通知文字描述");
			androidMsg.setAfterOpen(afteropen);
			androidMsg.setUrl("url");
			androidMsg.setActivity("activity");
			androidMsg.setCustom("custom");
			androidMsg.setDescription("description");
			
			androidMsg.setCustomDictionaryMap(customDictionaryMap);
			this.umengAndroidPushService.androidPushMsg(androidMsg);
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}
}
