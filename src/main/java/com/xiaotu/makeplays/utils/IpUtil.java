package com.xiaotu.makeplays.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpUtil {

	static Logger logger = LoggerFactory.getLogger(IpUtil.class);
	
	/**
	 * 获取客户端请求IP地址
	 * @param request
	 * @return
	 */
	public static String getUserIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");       
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {       
	        ip = request.getHeader("Proxy-Client-IP");
	        logger.info("Proxy-Client-IP");
	    }       
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {       
	        ip = request.getHeader("WL-Proxy-Client-IP"); 
	        logger.info("WL-Proxy-Client-IP");      
	    }
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
	        logger.info("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
	        logger.info("HTTP_X_FORWARDED_FOR");
        }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getRemoteAddr();
	        ip = ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
	        if(ip.equals("127.0.0.1")){
		        //根据网卡取本机配置的IP
			    InetAddress inet=null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ip= inet.getHostAddress();
	        }
	    }
	    if (ip != null && ip.indexOf(",") != -1) {  
	    	ip = ip.substring(ip.lastIndexOf(",") + 1, ip.length()).trim();  
	    }
	    return ip;
	}
	
	/**
	 * 根据IP地址获取所属地区
	 * @param ip
	 * @return
	 * @throws Exception
	 */
	public static String getIpArea(String ip) throws Exception{
		Properties properties = PropertiesUitls.fetchProperties("/config.properties");
		String appId = properties.getProperty("YIYUAN_APPID");
		String secret = properties.getProperty("YIYUAN_SECRET");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMddHHmmss");
		String path = "http://route.showapi.com/20-1?" + "showapi_appid="
				+ appId + "&showapi_timestamp=" + sdf3.format(new Date())
				+ "&ip=" + ip + "&showapi_sign=" + secret;

		logger.info("开始查询ip");

		// 从易源获取外网IP地址
		JSONObject ipResultJson = HttpUtils.httpGet(path);

		String country = "";
		String city = "";
		String county = "";
		if ("0".equals(ipResultJson.get("showapi_res_code").toString())) {
			JSONObject resBody = ipResultJson.getJSONObject("showapi_res_body");
			if(resBody.containsKey("ret_code") && resBody.getInt("ret_code") == -1) {
				country = "局域网";
			} else {
				country = resBody.getString("country");
				city = resBody.getString("city");
				county = resBody.getString("county");
			}
		} else {
			ipResultJson = new JSONObject();
			String sinaPath = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js&ip="+ ip;
			ipResultJson = HttpUtils.httpGet(sinaPath);
			country = ipResultJson.getString("country");
			city = ipResultJson.getString("city");
			county = ipResultJson.getString("county");
		}
		String fin = (country.equals("") ? "" : (country + "-"))
				+ (city.equals("") ? "" : (city + "-"))
				+ (county.equals("") ? "" : (county));
		if (fin.lastIndexOf("-") == (fin.length() - 1)) {
			fin = fin.substring(0, fin.length() - 1);
		}
		return fin;
	}

}
