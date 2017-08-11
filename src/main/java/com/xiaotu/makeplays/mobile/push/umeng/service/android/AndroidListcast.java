package com.xiaotu.makeplays.mobile.push.umeng.service.android;

public class AndroidListcast extends AndroidNotification  {
	public AndroidListcast() {
		try {
			this.setPredefinedKeyValue("type", "listcast");	
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
