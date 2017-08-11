package com.xiaotu.makeplays.mobile.push.umeng.service.android;


public class AndroidBroadcast extends com.xiaotu.makeplays.mobile.push.umeng.service.android.AndroidNotification {
	public AndroidBroadcast() {
		try {
			this.setPredefinedKeyValue("type", "broadcast");	
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
