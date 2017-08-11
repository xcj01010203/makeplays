package com.xiaotu.makeplays.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

public class MyOfficeManager {

	private MyOfficeManager() {}
	
	private static OfficeManager officeManager = null;
	
	public static synchronized OfficeManager getInstance() throws FileNotFoundException, IOException {
		if (officeManager == null) {
			Properties properties = PropertiesUitls.fetchProperties("/config.properties");
			String officeHome = properties.getProperty("openInstallPath");
			
			DefaultOfficeManagerConfiguration config = new DefaultOfficeManagerConfiguration();
			config.setOfficeHome(officeHome);
			
			officeManager = config.buildOfficeManager();
			officeManager.start();
		}
		return officeManager;
	}	
}
