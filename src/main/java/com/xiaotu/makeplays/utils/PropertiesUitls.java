package com.xiaotu.makeplays.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * properties文件处理工具类
 * @author xuchangjian
 */
public class PropertiesUitls {

	public static final Properties properties = new Properties();
	
	/**
	 * 读取指定路径下的properties文件
	 * @param filepath
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static Properties fetchProperties(String filepath) throws FileNotFoundException, IOException {
		InputStream inputStream = PropertiesUitls.class.getResourceAsStream(filepath);
		properties.load(inputStream);
		return properties;
	}
}
