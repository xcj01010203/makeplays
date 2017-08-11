package com.xiaotu.makeplays.mobile.push.umeng.service.ios;

public class IOSListcast extends IOSNotification  {
	public IOSListcast() {
		try {
			this.setPredefinedKeyValue("type", "listcast");	
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
